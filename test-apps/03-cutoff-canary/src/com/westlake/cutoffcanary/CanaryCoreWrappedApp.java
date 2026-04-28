package com.westlake.cutoffcanary;

import android.app.Application;
import androidx.core.app.CoreComponentFactory;

public final class CanaryCoreWrappedApp extends Application
        implements CoreComponentFactory.CompatWrapped {
    public CanaryCoreWrappedApp() {
        CanaryLog.mark("L4COREAPP_WRAPPED_CTOR_OK",
                "application=" + getClass().getName());
    }

    @Override
    public Object getWrapper() {
        CanaryLog.mark("L4COREAPP_GET_WRAPPER_OK",
                "application=" + getClass().getName());
        CanaryCoreWrapperApp wrapper = new CanaryCoreWrapperApp();
        CanaryLog.mark("L4COREAPP_WRAPPER_RETURNED_OK",
                "application=" + wrapper.getClass().getName());
        return wrapper;
    }

    @Override
    public void onCreate() {
        CanaryLog.mark("L4COREAPP_WRAPPED_ON_CREATE_ERR",
                "wrapped application onCreate should not run");
    }
}
