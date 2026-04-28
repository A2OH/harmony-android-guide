package com.westlake.cutoffcanary;

import android.content.Context;
import androidx.activity.contextaware.OnContextAvailableListener;
import dagger.hilt.android.internal.managers.ActivityComponentManager;

abstract class Hilt_L4HiltActivity extends L4Activity {
    private volatile ActivityComponentManager mComponentManager;
    private boolean mInjected;

    Hilt_L4HiltActivity() {
        addOnContextAvailableListener(new OnContextAvailableListener() {
            @Override
            public void onContextAvailable(Context context) {
                CanaryLog.mark("L4HILTAPP_CONTEXT_AVAILABLE_OK",
                        "activity=" + Hilt_L4HiltActivity.this.getClass().getName());
                inject();
            }
        });
    }

    public final ActivityComponentManager componentManager() {
        if (mComponentManager == null) {
            synchronized (this) {
                if (mComponentManager == null) {
                    mComponentManager = new ActivityComponentManager(this);
                    CanaryLog.mark("L4HILTAPP_ACTIVITY_COMPONENT_MANAGER_OK",
                            "manager=" + mComponentManager.getClass().getName());
                }
            }
        }
        return mComponentManager;
    }

    public final Object generatedComponent() {
        Object component = componentManager().generatedComponent();
        if (component != null) {
            CanaryLog.mark("L4HILTAPP_ACTIVITY_GENERATED_COMPONENT_OK",
                    "component=" + component.getClass().getName());
        }
        return component;
    }

    private void inject() {
        if (mInjected) {
            return;
        }
        mInjected = true;
        Object component = generatedComponent();
        if (component instanceof L4HiltActivity_GeneratedInjector) {
            ((L4HiltActivity_GeneratedInjector) component)
                    .injectL4HiltActivity((L4HiltActivity) this);
        } else {
            CanaryLog.mark("L4HILTAPP_ACTIVITY_INJECT_ERR",
                    "component=" + (component != null ? component.getClass().getName() : "null"));
        }
    }
}
