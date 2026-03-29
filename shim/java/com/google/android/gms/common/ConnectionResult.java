package com.google.android.gms.common;

public class ConnectionResult {
    public static final int SUCCESS = 0;
    public static final int SERVICE_MISSING = 1;
    public static final int SERVICE_VERSION_UPDATE_REQUIRED = 2;
    public static final int SERVICE_DISABLED = 3;
    public static final int SIGN_IN_REQUIRED = 4;
    public static final int INVALID_ACCOUNT = 5;
    public static final int RESOLUTION_REQUIRED = 6;
    public static final int NETWORK_ERROR = 7;
    public static final int INTERNAL_ERROR = 8;
    public static final int SERVICE_INVALID = 9;
    public static final int DEVELOPER_ERROR = 10;
    public static final int API_UNAVAILABLE = 16;

    private int mStatusCode;

    public ConnectionResult(int statusCode) { mStatusCode = statusCode; }

    public int getErrorCode() { return mStatusCode; }
    public boolean isSuccess() { return mStatusCode == SUCCESS; }
    public boolean hasResolution() { return false; }
    public String getErrorMessage() { return "GMS unavailable"; }
}
