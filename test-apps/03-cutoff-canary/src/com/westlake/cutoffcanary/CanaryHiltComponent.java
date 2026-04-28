package com.westlake.cutoffcanary;

import android.app.Activity;

public final class CanaryHiltComponent implements CanaryHiltApp_GeneratedInjector {
    @Override
    public void injectCanaryHiltApp(CanaryHiltApp app) {
        app.mCanaryHiltInjected = true;
        CanaryLog.mark("L4HILTAPP_APP_INJECT_OK",
                "application=" + app.getClass().getName());
    }

    public ActivityComponentBuilder activityComponentBuilder() {
        return new ActivityComponentBuilder();
    }

    public static final class ActivityComponentBuilder {
        private Activity mActivity;

        public ActivityComponentBuilder activity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Object build() {
            return new CanaryHiltActivityComponent(mActivity);
        }
    }
}
