package com.example.foodcode.sunmi;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.view.SurfaceHolder;

import com.sunmi.scan.Config;
import com.sunmi.scan.Image;
import com.sunmi.scan.ImageScanner;
import com.sunmi.scan.Symbol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * Created by Administrator on 2017/10/12.
 */

public class ScanCode extends ImageScanner {

    public static final int PEDEST_NONE = 0;
    public static final int PEDEST_EXIST = 1;

    private static SurfaceHolder holder = null;
    private static Camera mCamera = null;
    private static Camera.Parameters mParam = null;
    private static Camera.PreviewCallback callback = null;

    private static final String BASE_CHARGE_PATH = "/sys/devices/platform/battery/base_charge";
    private static final String NODE_PATH_FLASH = "/sys/devices/virtual/flash/torch/flash_current";
    private static final String NODE_PATH_IMAGE_FLIP = "/sys/devices/virtual/MIRROR/mirror/mirror_enable";

    /**
     * 非正式版接口，正式版可能会改动！！！！！
     *
     * @param value
     */
    //设置闪光灯亮度: "1"为最暗，"4"最亮
    public static void setFlashBright(String value) {

        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter(NODE_PATH_FLASH, false);
            bw = new BufferedWriter(fw);
            String myreadline = value;
            bw.write(myreadline);
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 非正式版接口，正式版可能会改动！！！！！
     *
     * @param value
     */
    //设置图像上下颠倒: "0"为不颠倒，其他值以及默认颠倒
    public static void setImageFlip(String value) {

        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter(NODE_PATH_IMAGE_FLIP, false);
            bw = new BufferedWriter(fw);
            String myreadline = value;
            bw.write(myreadline);
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void open(Camera.PreviewCallback callback, SurfaceHolder holder) {

        setConfig(0, Config.X_DENSITY, 2);  //列扫描间隔
        setConfig(0, Config.Y_DENSITY, 2);  //行扫描间隔
        setConfig(0, Config.ENABLE_MULTILESYMS, 0);  //是否开启同一幅图一次解多个条码,0表示只解一个，1为多个
        setConfig(0, Config.ENABLE_INVERSE, 1);  //是否解反色的条码
        setConfig(Symbol.PDF417, Config.ENABLE, 1);  //是否开启识读PDF417码
        setConfig(Symbol.DataMatrix, Config.ENABLE, 0);  //是否开启识读DataMatrix码

        if (mCamera == null) {
            mCamera = Camera.open();
        }

        setImageFlip("1");//不颠倒图像
        setFlashBright("4");

        mParam = mCamera.getParameters();

        this.callback = callback;
        this.holder = holder;

    }

    public void close() {

        if (mCamera == null) return;

        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

    }

    /**
     * TODO 设置摄像头模式
     *
     * @param mode 0 : FLASH_MODE_AUTO = "auto"
     *             1 : FLASH_MODE_OFF = "off"
     *             2 : FLASH_MODE_ON = "on"
     *             3 : FLASH_MODE_RED_EYE = "red-eye"
     *             4 : FLASH_MODE_TORCH = "torch"
     * @author kingsway
     * @date 2017-10-12
     */
    public int setFlashMode(int mode) {

        if (mCamera == null) return -1;
        if (mode < 0 || mode > 4) return -1;

        if (mode == 0) {
            mParam.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            setParameters(mParam);
        } else if (mode == 1) {
            mParam.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            setParameters(mParam);
        } else if (mode == 2) {
            mParam.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            setParameters(mParam);
        } else if (mode == 3) {
            mParam.setFlashMode(Camera.Parameters.FLASH_MODE_RED_EYE);
            setParameters(mParam);
        } else if (mode == 4) {
            mParam.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            setParameters(mParam);
        }

        return 0;

    }

    public void flash() {

        if (mCamera == null) return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mParam.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    if (setParameters(mParam) < 0) return null;

                    sleep(300);

                    mParam.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    if (setParameters(mParam) < 0) return null;

                    sleep(500);

                    mParam.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    if (setParameters(mParam) < 0) return null;

                    sleep(300);

                    mParam.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    if (setParameters(mParam) < 0) return null;

                    sleep(500);

                    mParam.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    if (setParameters(mParam) < 0) return null;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public int getPedestalStaus() {

        if (getPedestalStaus(BASE_CHARGE_PATH).equals("1")) {
            return PEDEST_EXIST;
        } else {
            return PEDEST_NONE;
        }

    }

    //自动对焦

    /**
     * 非正式版接口，正式版可能会改动！！！！！
     */
    public void setAutoFocus() {

        if (mCamera == null) return;
        if (callback == null) return;
        if (holder == null) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mParam.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    mParam.setPreviewSize(800, 480);//预览分辨率
                    mCamera.setDisplayOrientation(90);
                    mCamera.setPreviewCallback(callback);
                    setParameters(mParam);
                    mCamera.startPreview();

                    if (holder != null) {
                        try {
                            mCamera.setPreviewDisplay(holder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    mParam.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

                    setParameters(mParam);
                    mCamera.autoFocus(mAutoFocusCallback);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 非正式版接口，正式版可能会改动！！！！！
     */
    //定焦
    public void setFixedFocus(int pos) {

        if (mCamera == null) return;
        if (callback == null) return;
        if (holder == null) return;

        final int pos2 = pos;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mParam.setPreviewSize(800, 480);//预览分辨率
                    mCamera.setDisplayOrientation(90);
                    mCamera.setPreviewCallback(callback);
                    setParameters(mParam);
                    mCamera.startPreview();
                    mCamera.cancelAutoFocus();

                    if (holder != null) {
                        try {
                            mCamera.setPreviewDisplay(holder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    mParam.set("focus-mode", "manual");
                    mParam.set("afeng-pos", pos2);

                    setParameters(mParam);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    /**
     * 非正式版接口，正式版可能会改动！！！！！
     *
     */
    //设置定焦
    public void setFixedFocusPos(int pos) {

        if (mCamera == null) return;

        mParam.set("focus-mode", "manual");
        mParam.set("afeng-pos", pos);
        setParameters(mParam);

    }

    public void autoFocus() {

        if (mCamera == null) return;

        mCamera.autoFocus(mAutoFocusCallback);

    }

    public void setPreviewDisplay(SurfaceHolder holder) {

        if (mCamera == null) return;
        if (holder == null) return;

        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Image setData(Camera camera, byte[] data) {

        if (camera == null) return null;
        if (data == null) return null;

        Camera.Size size = mParam.getPreviewSize();//获取预览分辨率

        //创建解码图像，并转换为原始灰度数据，注意图片是被旋转了90度的
        Image image = new Image(size.width, size.height, "Y800");
        image.setData(data);//填充数据

        return image;

    }

    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {

            if (!success) return;
            if (camera == null) return;

            camera.cancelAutoFocus();
            mParam = camera.getParameters();
            mParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setParameters(mParam);
        }

    };

    private int setParameters(Camera.Parameters params) {

        byte n = 0;

        if (mCamera == null) return -1;
        if (params == null) return -1;

        while (n < 5) {
            try {
                mCamera.setParameters(params);
            } catch (Exception e) {
                n++;

                try {
                    sleep(50);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                continue;
            }

            break;
        }

        if (n >= 5) return -2;

        return 0;

    }

    //获取连接底座状态:"1":连接 "0":断开
    private static String getPedestalStaus(String path) {

        String prop = "waiting";// 默认值

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            prop = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return prop;

    }

}
