/*
 * HelloService.java
 *
 * Minimal Service used by MainActivity's bindService demo.
 * Pure vanilla Android — no awareness of any adaptation layer.
 */
package com.example.helloworld;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class HelloService extends Service {

    private static final String TAG = "HelloService";

    public class LocalBinder extends Binder {
        public HelloService getService() {
            return HelloService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: " + intent);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind: " + intent);
        return false;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
