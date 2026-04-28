package com.westlake.cutoffcanary;

import android.app.Application;

public class CanaryApp extends Application {
    public CanaryApp() {
        if ("L4WATAPPREFLECT".equals(System.getProperty("westlake.canary.stage"))) {
            CanaryLog.mark("L4APPREFLECT_CANARY_APP_CTOR_OK",
                    "application=" + getClass().getName());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CanaryLog.mark("L0_OK", "application=" + getClass().getName() + " package=" + getPackageName());
    }
}
