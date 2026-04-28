package com.westlake.yelplive;

import android.app.Application;

public final class YelpLiveApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        YelpLiveLog.mark("APP_ON_CREATE_OK",
                "application=" + getClass().getName() + " package=" + getPackageName());
    }
}
