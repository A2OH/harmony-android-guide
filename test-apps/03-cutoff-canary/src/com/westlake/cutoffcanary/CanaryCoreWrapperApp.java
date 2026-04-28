package com.westlake.cutoffcanary;

public final class CanaryCoreWrapperApp extends CanaryApp {
    public CanaryCoreWrapperApp() {
        CanaryLog.mark("L4COREAPP_WRAPPER_CTOR_OK",
                "application=" + getClass().getName());
    }

    @Override
    public void onCreate() {
        CanaryLog.mark("L4COREAPP_WRAPPER_ON_CREATE_OK",
                "application=" + getClass().getName());
        super.onCreate();
        CanaryLog.mark("L4COREAPP_WRAPPER_ON_CREATE_RETURNED_OK",
                "application=" + getClass().getName());
    }
}
