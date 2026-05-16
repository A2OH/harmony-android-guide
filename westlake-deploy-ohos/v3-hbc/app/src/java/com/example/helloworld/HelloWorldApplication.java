/*
 * HelloWorldApplication.java
 *
 * Standard Android Application class. Pure vanilla code — no awareness of
 * any adaptation layer or runtime environment. Per the OH Adapter project's
 * core principle, the same APK must run unchanged on a stock Android device
 * and on an OpenHarmony device with the adapter installed.
 *
 * The redirection from android.* system services to OH services is handled
 * entirely by framework.jar patches (L5) + native adapter .so libraries —
 * the application code is never touched.
 */
package com.example.helloworld;

import android.app.Application;
import android.util.Log;

public class HelloWorldApplication extends Application {

    private static final String TAG = "HelloWorld";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "=== Hello World Application Starting ===");
    }
}
