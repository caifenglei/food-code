package com.example.foodcode;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.foodcode.sunmi.ScanCode;
import com.example.foodcode.sunmi.Sound;
import com.example.foodcode.utils.BitmapTransformUtils;
import com.sunmi.scan.Image;
import com.sunmi.scan.Symbol;
import com.sunmi.scan.SymbolSet;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

/**
 * A calculator
 */
public class CalculatorFragment extends Fragment implements SurfaceHolder.Callback {

    private static final int START_SCAN = 0x0001;
    private static final String USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";

    private final int RESULT_LOAD_IMAGE = 44;
    private final int RESULT_OK = 1;
    private static ScanCode scanCode = null;
    private AsyncDecode asyncDecode = null;
    private static Sound sound = null;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    CashierWaitingDialogFragment waitingPayDialog;


    Double calResult = 0.00;
    String numberStack = "";
    View fragmentView;
    TextView calResultView;
    ArrayList<Double> calElements = new ArrayList<>(2);
    int activeOperator;

    public CalculatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_calculator, container, false);

        //Code Scan
        asyncDecode = new AsyncDecode();
        scanCode = new ScanCode();

//        mSurfaceView = (SurfaceView) fragmentView.findViewById(R.id.preview);
//        mSurfaceHolder = mSurfaceView.getHolder();
//        mSurfaceView.setFocusable(true);
//        mSurfaceHolder.addCallback(this);

        sound = new Sound(this.getActivity());
        sound.addSound(0, R.raw.beep);

        initView();
        initAction();

        //Calculator
        calResultView = fragmentView.findViewById(R.id.amountResult);

        Button keyOne = fragmentView.findViewById(R.id.keyOne);
        Button keyTwo = fragmentView.findViewById(R.id.keyTwo);
        Button keyThree = fragmentView.findViewById(R.id.keyThree);
        Button keyFour = fragmentView.findViewById(R.id.keyFour);
        Button keyFive = fragmentView.findViewById(R.id.keyFive);
        Button keySix = fragmentView.findViewById(R.id.keySix);
        Button keySeven = fragmentView.findViewById(R.id.keySeven);
        Button keyEight = fragmentView.findViewById(R.id.keyEight);
        Button keyNine = fragmentView.findViewById(R.id.keyNine);
        Button keyZero = fragmentView.findViewById(R.id.keyZero);
        Button keyPoint = fragmentView.findViewById(R.id.keyPoint);
        AppCompatImageButton keyDelete = fragmentView.findViewById(R.id.keyDelete);
        Button keyPlus = fragmentView.findViewById(R.id.keyPlus);
        Button keyDeduct = fragmentView.findViewById(R.id.keyDeduct);
        Button keyMultiply = fragmentView.findViewById(R.id.keyMultiply);
        Button keyDivide = fragmentView.findViewById(R.id.keyDivide);
        Button keyCashier = fragmentView.findViewById(R.id.keyCashier);

        keyOne.setOnClickListener(onCalculatorKeyClick);
        keyTwo.setOnClickListener(onCalculatorKeyClick);
        keyThree.setOnClickListener(onCalculatorKeyClick);
        keyFour.setOnClickListener(onCalculatorKeyClick);
        keyFive.setOnClickListener(onCalculatorKeyClick);
        keySix.setOnClickListener(onCalculatorKeyClick);
        keySeven.setOnClickListener(onCalculatorKeyClick);
        keyEight.setOnClickListener(onCalculatorKeyClick);
        keyNine.setOnClickListener(onCalculatorKeyClick);
        keyZero.setOnClickListener(onCalculatorKeyClick);
        keyPoint.setOnClickListener(onCalculatorKeyClick);
        keyDelete.setOnClickListener(onCalculatorKeyClick);
        keyPlus.setOnClickListener(onCalculatorKeyClick);
        keyDeduct.setOnClickListener(onCalculatorKeyClick);
        keyMultiply.setOnClickListener(onCalculatorKeyClick);
        keyDivide.setOnClickListener(onCalculatorKeyClick);
        keyCashier.setOnClickListener(onCalculatorKeyClick);

        return fragmentView;
    }

    private void initView(){
        waitingPayDialog = new CashierWaitingDialogFragment();
    }

    private void initAction(){

        waitingPayDialog.setCompleteListener(new CashierWaitingDialogFragment.OnCompleteListener() {
            @Override
            public void onCancel() {
//                if (menus.size() > 0) {
//                    if (videoMenuDisplay != null) {
//                        videoMenuDisplay.show();
//                    }
//                }
                Log.i("COMPLETE", "cancelled");
            }

            @Override
            public void onSuccess(final int payMode) {
                Log.i("COMPLETE", "success");
//                playSound(payMode);
//                myHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        paySuccessToPrinter(payMode);
//                        payDialog.setPhoneNumber("");
//
//                    }
//                }, 1000);
            }

            @Override
            public void onComplete(int payMode) {
                Log.i("COMPLETE", "complete");
//                delectProduct();
//                payCompleteToReMenu();
            }
        });
    }

    private View.OnClickListener onCalculatorKeyClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.keyOne:
                case R.id.keyTwo:
                case R.id.keyThree:
                case R.id.keyFour:
                case R.id.keyFive:
                case R.id.keySix:
                case R.id.keySeven:
                case R.id.keyEight:
                case R.id.keyNine:
                case R.id.keyZero:
                    Button b = (Button) view;
                    tapNumber(b.getText().toString());
                    break;
                case R.id.keyPoint:
                    tapPoint();
                    break;
                case R.id.keyPlus:
                case R.id.keyDeduct:
                case R.id.keyMultiply:
                case R.id.keyDivide:
                    tapOperator(viewId);
                    break;
                case R.id.keyDelete:
                    tapClear();
                    break;
                case R.id.keyCashier:
                    tapCashier();
                    break;
            }
        }
    };

    private void tapNumber(String nbr) {
        if (Objects.equals(numberStack, "")) {
            numberStack = nbr;
        } else {
            if (numberStack.length() < 6) {
                numberStack = numberStack + nbr;
            }
        }
        formatResultValue(Double.parseDouble(numberStack));
    }

    private void tapPoint() {
        if (Objects.equals(numberStack, "")) {
            numberStack = "0.";
        } else {
            if (!numberStack.endsWith(".")) {
                numberStack = numberStack + '.';
            }
        }
    }

    private void tapOperator(int opt) {
        int elementCount = calElements.size();
        if (elementCount == 0) {
            if (!Objects.equals(numberStack, "")) {
                calElements.add(Double.parseDouble(numberStack));
                numberStack = "";
                activeOperator = opt;
            }
        } else if (elementCount == 1) {
            if (!Objects.equals(numberStack, "")) {
                calElements.add(Double.parseDouble(numberStack));
                if (calculateResult(activeOperator)) {
                    calElements.add(calResult);
                    activeOperator = opt;
                }
            }
        } else {
            //2 factors, then calculate the result
            calculateResult(opt);
        }
    }

    // clear all
    private void tapClear() {
        numberStack = "";
        calElements.clear();
        calResult = 0.00;
        calResultView.setText("0.00");
    }

    private void tapCashier() {
        int factorCount = calElements.size();
        Double moneyToPay = 0.00;
        if (factorCount == 0 && numberStack != "" && Double.parseDouble(numberStack) != 0) {
            moneyToPay = Double.parseDouble(numberStack);
        } else if (factorCount == 1 && calResult > 0) {
            moneyToPay = calResult;
        } else if (factorCount == 2) {
            if (calculateResult(activeOperator)) {
                moneyToPay = calResult;
            }
        }
        if (moneyToPay > 0) {
            Log.i("cashier", String.valueOf(moneyToPay));

            Bundle bundle = new Bundle();
            bundle.putString("money", String.valueOf(moneyToPay));
            waitingPayDialog.setArguments(bundle);
            waitingPayDialog.show(getParentFragmentManager(), "refund_dialog");

            //内部集成的扫码模块
//            Intent intent = new Intent("com.summi.scan");
//            intent.setPackage("com.sunmi.sunmiqrcodescanner");
//            startActivityForResult(intent, START_SCAN);

            //扫码底座接入
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(intent, RESULT_LOAD_IMAGE);

//            Intent intent = new Intent(USB_DEVICE_ATTACHED, 1);
//            Intent intent = new Intent(USB_DEVICE_ATTACHED);
//            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//            String deviceName = device.getDeviceName();
//            Log.i("DEVICE", deviceName);
//            UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
//            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
//            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//            while(deviceIterator.hasNext()){
//                UsbDevice device = deviceIterator.next();
//                //your code
//                Log.i("DEVICE", device.getDeviceName() + " - VID:" + device.getVendorId() + ", PID:" + device.getProductId());
//            }


        } else {
            Log.i("cashier", "no Money to pay");
        }
    }

    private boolean calculateResult(int opt) {

        boolean ok = true;
        if (opt == R.id.keyPlus) {
            calResult = calElements.get(0) + calElements.get(1);
        } else if (opt == R.id.keyDeduct) {
            calResult = calElements.get(0) - calElements.get(1);
        } else if (opt == R.id.keyMultiply) {
            calResult = calElements.get(0) * calElements.get(1);
        } else if (opt == R.id.keyDivide) {
            if (calElements.get(1) == 0) {
                calResultView.setText("错误");
                ok = false;
            } else {
                calResult = calElements.get(0) / calElements.get(1);
            }
        }

        if (ok) {
            numberStack = "";
            calElements.clear();
            formatResultValue(calResult);
        }

        return ok;
    }

    private String formatResultValue(Double value) {
        BigDecimal bd = new BigDecimal(value);
        calResult = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); //四舍五入

        DecimalFormat df = new DecimalFormat("0.00");
        String format = df.format(value);
        calResultView.setText(format);

//        Log.i("FFF", String.valueOf(value) + "," + String.valueOf(bd) + "," + String.valueOf(calResult) + "|" + format);
        return format;
    }


    //摄像头集成模块回调
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(resultCode, resultCode, data);
//        Log.i("SCAN_RESULT", String.valueOf(resultCode));
//        if(resultCode == 1 && data != null){
//            Bundle bundle = data.getExtras();
//            ArrayList<HashMap<String, String>> result = (ArrayList<HashMap<String, String>>) bundle.getSerializable("data");
//            Iterator<HashMap<String, String>> it = result.iterator();
//            while (it.hasNext()) {
//                HashMap<String, String> hashMap = it.next();
//                Log.i("sunmi", (String) hashMap.get("TYPE"));//扫码类型
//                Log.i("sunmi", (String) hashMap.get("VALUE"));//扫码结果
//            }
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            ContentResolver cr = getActivity().getContentResolver();
            Bitmap bitmap = null;

            try {
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(selectedImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Image source = BitmapTransformUtils.getImageByBitmap(bitmap);
            scanCode.close();
            scanCode.open(previewCallback, mSurfaceHolder);
            scanCode.setAutoFocus();

            asyncDecode = new AsyncDecode();
            asyncDecode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, source);
        }
    }


    //=========scan related=========
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        scanCode.setPreviewDisplay(mSurfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (scanCode.getPedestalStaus() == ScanCode.PEDEST_NONE) scanCode.autoFocus();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        public void onPreviewFrame(byte[] data, Camera camera) {

            if (asyncDecode.isStoped()) {
                asyncDecode = new AsyncDecode();
                asyncDecode.execute(scanCode.setData(camera, data));//调用异步执行解码
            }
        }
    };

    private class AsyncDecode extends AsyncTask<Image, Void, String> {

        private boolean stoped = true;
        private StringBuilder sb = new StringBuilder();

        @Override
        protected String doInBackground(Image... params) {

            stoped = false;

            try {
                Log.i("---STATUS", String.valueOf(scanCode.getPedestalStaus()));
//                if (scanCode.getPedestalStaus() == ScanCode.PEDEST_NONE) {
//                    if (winDialog != null) {
//                        if (winDialog.getDialogStatus(tradewaitviewname)) {
//                            isCharge = false;
//                            isPlaying = false;
//                            manager.cancel(ID);
//                        }
//                    }
//                }else {
//                    if (winDialog != null) {
//                        if (winDialog.getDialogStatus(tradewaitviewname)) {
//                            if (!stopstate) {
//                                manager.notify(ID, builder.build());
//                            }
//                        }
//                    }
//                }

                Image data = params[0];//获取灰度数据
                long startTimeMillis = System.currentTimeMillis();

                int nsyms = scanCode.scanImage(data);

                long endTimeMillis = System.currentTimeMillis();
                long cost_time = endTimeMillis - startTimeMillis;

                sb.append("");

                if (nsyms > 0) {
                    sb.append("耗时: " + String.valueOf(cost_time) + " ms \n");

                    SymbolSet syms = scanCode.getResults();  //获取解码结果

                    for (Symbol sym : syms) {
                        sb.append("码制: " + sym.getSymbolName() + "\n");
                        sb.append("内容: " + sym.getResult() + "\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return sb.toString();

        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if (result == null || result.equals("")) {
                stoped = true;
                return;
            }

//            tvInfo.setText(result);
            Log.i("---onPostExecute", result);

            //打印
//            printtradeinfo();

//            if (winDialog != null) {
//                winDialog.Destroy();
//                winDialog = null;
//                waittradestate = true;
//            }

            scanCode.close();
//            keyboardview.setVisibility(View.VISIBLE);
//            tradescanview.setVisibility(View.GONE);
//
//            tradesuccessstate = true;
//
//            winDialog = WinDialog.newWinDialog(true, R.layout.tradesuccess);
//            winDialog.show(getFragmentManager(), successviewname);

            sound.playsound(R.raw.tradeseccuss);

            stoped = true;
        }

        public boolean isStoped() {

            return stoped;
        }
    }
}