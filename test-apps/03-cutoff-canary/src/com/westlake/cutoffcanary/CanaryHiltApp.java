package com.westlake.cutoffcanary;

public final class CanaryHiltApp extends Hilt_CanaryHiltApp {
    boolean mCanaryHiltInjected;

    public CanaryHiltApp() {
        CanaryLog.mark("L4HILTAPP_APP_CTOR_OK",
                "application=" + getClass().getName());
    }

    @Override
    public void onCreate() {
        Object component = generatedComponent();
        if (component instanceof CanaryHiltApp_GeneratedInjector) {
            ((CanaryHiltApp_GeneratedInjector) component).injectCanaryHiltApp(this);
        }
        if (!mCanaryHiltInjected) {
            CanaryLog.mark("L4HILTAPP_APP_INJECT_ERR",
                    "application=" + getClass().getName());
            return;
        }
        CanaryLog.mark("L4HILTAPP_APP_ON_CREATE_OK",
                "application=" + getClass().getName());
        super.onCreate();
    }
}
