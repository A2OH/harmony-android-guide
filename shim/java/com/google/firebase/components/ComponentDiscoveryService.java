package com.google.firebase.components;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/** Stub — Firebase uses this to discover components via metadata. */
public class ComponentDiscoveryService extends Service {
    @Override public IBinder onBind(Intent intent) { return null; }
}
