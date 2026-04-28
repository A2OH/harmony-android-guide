package com.westlake.cutoffcanary;

import dagger.hilt.android.internal.managers.ApplicationComponentManager;
import dagger.hilt.android.internal.managers.ComponentSupplier;

abstract class Hilt_CanaryHiltApp extends CanaryApp {
    private volatile ApplicationComponentManager mComponentManager;

    Hilt_CanaryHiltApp() {
        CanaryLog.mark("L4HILTAPP_BASE_CTOR_OK",
                "application=" + getClass().getName());
    }

    public final ApplicationComponentManager componentManager() {
        if (mComponentManager == null) {
            synchronized (this) {
                if (mComponentManager == null) {
                    mComponentManager = new ApplicationComponentManager(
                            new ComponentSupplier() {
                                @Override
                                public Object get() {
                                    return new CanaryHiltComponent();
                                }
                            });
                    CanaryLog.mark("L4HILTAPP_COMPONENT_MANAGER_OK",
                            "manager=" + mComponentManager.getClass().getName());
                }
            }
        }
        return mComponentManager;
    }

    public final Object generatedComponent() {
        Object component = componentManager().generatedComponent();
        if (component != null) {
            CanaryLog.mark("L4HILTAPP_GENERATED_COMPONENT_OK",
                    "component=" + component.getClass().getName());
        }
        return component;
    }
}
