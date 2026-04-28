package com.westlake.cutoffcanary;

public final class L4HiltActivity extends Hilt_L4HiltActivity {
    boolean mCanaryHiltInjected;

    public L4HiltActivity() {
        CanaryLog.mark("L4HILTAPP_ACTIVITY_CTOR_OK",
                "activity=" + getClass().getName());
    }
}
