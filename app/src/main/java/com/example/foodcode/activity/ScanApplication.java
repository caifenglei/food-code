package com.example.foodcode.activity;

import android.app.Application;
import android.widget.Toast;

import com.example.foodcode.utils.FileUtils;
import com.example.foodcode.utils.LogUtil;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Created by KenMa on 2016/10/21.
 */
public class ScanApplication extends Application {
    private static final String TAG = "ScanApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        configUncaughtExceptionHandler();
    }

    /**
     * 捕获异常
     */
    private void configUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                LogUtil.e(TAG, "uncaughtException crash");
                Toast.makeText(ScanApplication.this, "crash", Toast.LENGTH_LONG).show();
                try {
                    ex.printStackTrace(new PrintStream(FileUtils.createErrorFile()));
                } catch (FileNotFoundException e) {
                    LogUtil.e(TAG, "创建异常文件失败");
                    e.printStackTrace();
                }

            }

        });
    }
}
