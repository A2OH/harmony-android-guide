package com.google.firebase.messaging;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FirebaseMessagingService extends Service {
    public void onMessageReceived(Object remoteMessage) {}
    public void onDeletedMessages() {}
    public void onNewToken(String token) {}
    @Override public IBinder onBind(Intent intent) { return null; }
}
