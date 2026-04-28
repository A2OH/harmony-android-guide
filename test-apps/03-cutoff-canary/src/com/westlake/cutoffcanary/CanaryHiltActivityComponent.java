package com.westlake.cutoffcanary;

import android.app.Activity;

public final class CanaryHiltActivityComponent implements L4HiltActivity_GeneratedInjector {
    private final Activity mActivity;

    CanaryHiltActivityComponent(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void injectL4HiltActivity(L4HiltActivity activity) {
        activity.mCanaryHiltInjected = true;
        CanaryLog.mark("L4HILTAPP_ACTIVITY_INJECT_OK",
                "activity=" + activity.getClass().getName()
                        + " componentActivity=" + (mActivity == activity));
    }
}
