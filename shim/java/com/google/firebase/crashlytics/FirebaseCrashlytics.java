package com.google.firebase.crashlytics;

public class FirebaseCrashlytics {
    private static FirebaseCrashlytics sInstance;

    public static FirebaseCrashlytics getInstance() {
        if (sInstance == null) sInstance = new FirebaseCrashlytics();
        return sInstance;
    }

    public void log(String msg) {}
    public void recordException(Throwable t) {}
    public void setUserId(String id) {}
    public void setCustomKey(String key, String value) {}
    public void setCustomKey(String key, boolean value) {}
    public void setCustomKey(String key, int value) {}
    public void setCustomKey(String key, long value) {}
    public void setCustomKey(String key, float value) {}
    public void setCustomKey(String key, double value) {}
    public void setCrashlyticsCollectionEnabled(boolean enabled) {}
    public boolean didCrashOnPreviousExecution() { return false; }
    public void sendUnsentReports() {}
    public void deleteUnsentReports() {}
}
