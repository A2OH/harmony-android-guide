package com.westlake.showcase;

import android.app.Application;

public final class ShowcaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ShowcaseLog.mark("APP_ON_CREATE_OK",
                "application=" + getClass().getName() + " package=" + getPackageName());
    }
}
