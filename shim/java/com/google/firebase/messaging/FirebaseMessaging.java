package com.google.firebase.messaging;

import com.google.android.gms.tasks.Task;

public class FirebaseMessaging {
    private static FirebaseMessaging sInstance;

    public static FirebaseMessaging getInstance() {
        if (sInstance == null) sInstance = new FirebaseMessaging();
        return sInstance;
    }

    public Task<String> getToken() {
        return new Task<>("westlake-stub-token");
    }

    public Task<Void> subscribeToTopic(String topic) {
        return new Task<>((Void) null);
    }

    public Task<Void> unsubscribeFromTopic(String topic) {
        return new Task<>((Void) null);
    }

    public void setAutoInitEnabled(boolean enabled) {}
    public boolean isAutoInitEnabled() { return false; }
    public Task<Void> deleteToken() { return new Task<>((Void) null); }
}
