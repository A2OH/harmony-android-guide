package com.westlake.materialyelp;

import android.app.Application;

public final class MaterialYelpApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MaterialYelpLog.mark("APP_ON_CREATE_OK",
                "application=" + getClass().getName() + " package=" + getPackageName());
    }
}
