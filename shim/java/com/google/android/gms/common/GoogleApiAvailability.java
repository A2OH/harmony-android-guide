package com.google.android.gms.common;

import android.app.Activity;
import android.content.Context;

/**
 * Stub GoogleApiAvailability — reports Play Services as unavailable.
 * Apps that gracefully handle missing GMS will fall back to non-GMS paths.
 */
public class GoogleApiAvailability {
    public static final int SUCCESS = 0;
    public static final int SERVICE_MISSING = 1;
    public static final int SERVICE_VERSION_UPDATE_REQUIRED = 2;
    public static final int SERVICE_DISABLED = 3;
    public static final int SERVICE_INVALID = 9;

    private static final GoogleApiAvailability sInstance = new GoogleApiAvailability();

    public static GoogleApiAvailability getInstance() { return sInstance; }

    public int isGooglePlayServicesAvailable(Context context) {
        return SERVICE_MISSING;
    }

    public int isGooglePlayServicesAvailable(Context context, int minVersion) {
        return SERVICE_MISSING;
    }

    public boolean isUserResolvableError(int errorCode) { return false; }

    public void makeGooglePlayServicesAvailable(Activity activity) {}

    public String getErrorString(int errorCode) {
        return "Google Play Services not available (Westlake)";
    }

    public boolean showErrorDialogFragment(Activity activity, int errorCode, int requestCode) {
        return false;
    }
}
