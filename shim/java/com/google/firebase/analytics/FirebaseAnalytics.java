package com.google.firebase.analytics;

import android.content.Context;
import android.os.Bundle;

/**
 * Stub FirebaseAnalytics — silently drops all events.
 */
public class FirebaseAnalytics {
    private static FirebaseAnalytics sInstance;

    private FirebaseAnalytics() {}

    public static FirebaseAnalytics getInstance(Context context) {
        if (sInstance == null) sInstance = new FirebaseAnalytics();
        return sInstance;
    }

    public void logEvent(String name, Bundle params) {}
    public void setUserId(String id) {}
    public void setUserProperty(String name, String value) {}
    public void setAnalyticsCollectionEnabled(boolean enabled) {}
    public void setCurrentScreen(Object activity, String screenName, String screenClassOverride) {}
    public void setDefaultEventParameters(Bundle params) {}
    public void resetAnalyticsData() {}
}
