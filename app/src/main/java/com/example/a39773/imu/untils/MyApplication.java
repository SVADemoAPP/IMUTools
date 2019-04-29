package com.example.a39773.imu.untils;

import android.app.Application;
import android.os.Environment;

import java.io.File;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.getInstance()
                .setDiskPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "IMU" + File.separator + "Log")
                .setLevel(LogUtils.VERBOSE_LEVEL)
                .setWriteFlag(true);
        MyCrashHandler crashHandler = MyCrashHandler.instance();
        crashHandler.init(getApplicationContext());
    }
}
