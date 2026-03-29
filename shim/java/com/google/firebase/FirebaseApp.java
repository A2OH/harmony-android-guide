package com.google.firebase;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * Stub FirebaseApp — no-op initialization.
 */
public class FirebaseApp {
    public static final String DEFAULT_APP_NAME = "[DEFAULT]";
    private static FirebaseApp sDefault;
    private final String mName;
    private final Context mContext;

    private FirebaseApp(Context context, String name) {
        mContext = context;
        mName = name;
    }

    public static FirebaseApp initializeApp(Context context) {
        if (sDefault == null) {
            sDefault = new FirebaseApp(context, DEFAULT_APP_NAME);
            System.out.println("[Firebase] initializeApp (stub) — no-op");
        }
        return sDefault;
    }

    public static FirebaseApp initializeApp(Context context, Object options) {
        return initializeApp(context);
    }

    public static FirebaseApp initializeApp(Context context, Object options, String name) {
        if (DEFAULT_APP_NAME.equals(name)) return initializeApp(context);
        return new FirebaseApp(context, name);
    }

    public static FirebaseApp getInstance() {
        if (sDefault == null) sDefault = new FirebaseApp(null, DEFAULT_APP_NAME);
        return sDefault;
    }

    public static FirebaseApp getInstance(String name) {
        return getInstance();
    }

    public static List<FirebaseApp> getApps(Context context) {
        List<FirebaseApp> apps = new ArrayList<>();
        if (sDefault != null) apps.add(sDefault);
        return apps;
    }

    public Context getApplicationContext() { return mContext; }
    public String getName() { return mName; }
    public boolean isDefaultApp() { return DEFAULT_APP_NAME.equals(mName); }
    public void delete() { if (this == sDefault) sDefault = null; }
}
