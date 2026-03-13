package com.ohos.shim.bridge;

/**
 * JNI bridge between Java shim layer and OpenHarmony native APIs.
 * Native side is implemented in Rust (shim/bridge/rust/).
 * Produces liboh_bridge.so loaded at runtime.
 */
public class OHBridge {

    static {
        System.loadLibrary("oh_bridge");
    }

    // ── Preferences (SharedPreferences shim) ──────────────────────

    public static native long preferencesOpen(String name);
    public static native String preferencesGetString(long handle, String key, String defValue);
    public static native int preferencesGetInt(long handle, String key, int defValue);
    public static native long preferencesGetLong(long handle, String key, long defValue);
    public static native float preferencesGetFloat(long handle, String key, float defValue);
    public static native boolean preferencesGetBoolean(long handle, String key, boolean defValue);
    public static native void preferencesPutString(long handle, String key, String value);
    public static native void preferencesPutInt(long handle, String key, int value);
    public static native void preferencesPutLong(long handle, String key, long value);
    public static native void preferencesPutFloat(long handle, String key, float value);
    public static native void preferencesPutBoolean(long handle, String key, boolean value);
    public static native void preferencesFlush(long handle);
    public static native void preferencesRemove(long handle, String key);
    public static native void preferencesClear(long handle);
    public static native void preferencesClose(long handle);

    // ── RdbStore (SQLiteDatabase shim) ────────────────────────────

    public static native long rdbStoreOpen(String dbName, int version);
    public static native void rdbStoreExecSQL(long handle, String sql);
    public static native long rdbStoreQuery(long handle, String sql, String[] args);
    public static native long rdbStoreInsert(long handle, String table, String valuesJson);
    public static native int rdbStoreUpdate(long handle, String valuesJson, String table, String whereClause, String[] whereArgs);
    public static native int rdbStoreDelete(long handle, String table, String whereClause, String[] whereArgs);
    public static native void rdbStoreBeginTransaction(long handle);
    public static native void rdbStoreCommit(long handle);
    public static native void rdbStoreRollback(long handle);
    public static native void rdbStoreClose(long handle);

    // ── ResultSet (Cursor shim) ───────────────────────────────────

    public static native boolean resultSetGoToFirstRow(long handle);
    public static native boolean resultSetGoToNextRow(long handle);
    public static native int resultSetGetColumnIndex(long handle, String columnName);
    public static native String resultSetGetString(long handle, int columnIndex);
    public static native int resultSetGetInt(long handle, int columnIndex);
    public static native long resultSetGetLong(long handle, int columnIndex);
    public static native float resultSetGetFloat(long handle, int columnIndex);
    public static native double resultSetGetDouble(long handle, int columnIndex);
    public static native byte[] resultSetGetBlob(long handle, int columnIndex);
    public static native boolean resultSetIsNull(long handle, int columnIndex);
    public static native int resultSetGetRowCount(long handle);
    public static native int resultSetGetColumnCount(long handle);
    public static native String resultSetGetColumnName(long handle, int columnIndex);
    public static native void resultSetClose(long handle);

    // ── Notification ──────────────────────────────────────────────

    public static native void notificationPublish(int id, String title, String text, String channelId, int priority);
    public static native void notificationCancel(int id);
    public static native void notificationAddSlot(String channelId, String channelName, int importance);

    // ── Reminder (AlarmManager shim) ──────────────────────────────

    public static native int reminderScheduleTimer(int delaySeconds, String title, String content, String targetAbility, String paramsJson);
    public static native void reminderCancel(int reminderId);

    // ── Navigation ────────────────────────────────────────────────

    public static native void startAbility(String bundleName, String abilityName, String paramsJson);
    public static native void terminateSelf();

    // ── Logging ───────────────────────────────────────────────────

    public static native void logDebug(String tag, String msg);
    public static native void logInfo(String tag, String msg);
    public static native void logWarn(String tag, String msg);
    public static native void logError(String tag, String msg);

    // ── Toast ─────────────────────────────────────────────────────

    public static native void showToast(String message, int duration);

    // ── Network ───────────────────────────────────────────────────

    public static native String httpRequest(String url, String method, String headersJson, String body);
    public static native boolean isNetworkAvailable();
    public static native int getNetworkType();

    // ── WiFi ──────────────────────────────────────────────────────

    public static native boolean wifiIsEnabled();
    public static native boolean wifiSetEnabled(boolean enabled);
    public static native int wifiGetState();
    public static native String wifiGetSSID();
    public static native int wifiGetRssi();
    public static native int wifiGetLinkSpeed();
    public static native int wifiGetFrequency();

    // ── Device Info ───────────────────────────────────────────────

    public static native String getDeviceBrand();
    public static native String getDeviceModel();
    public static native String getOSVersion();
    public static native int getSDKVersion();

    // ── Telephony ─────────────────────────────────────────────────

    public static native String telephonyGetDeviceId();
    public static native String telephonyGetLine1Number();
    public static native String telephonyGetNetworkOperatorName();
    public static native int telephonyGetSimState();
    public static native int telephonyGetPhoneType();
    public static native int telephonyGetNetworkType();

    // ── Location ──────────────────────────────────────────────────

    /** Returns [lat, lon, alt] for the last known position, or null. */
    public static native double[] locationGetLast();

    /** Returns true when a location provider is enabled on the device. */
    public static native boolean locationIsEnabled();

    // ── ArkUI Native Node API (View rendering) ───────────────────

    /** Initialize ArkUI native node system. Call once at startup. */
    public static native int arkuiInit();

    /** Create a native ArkUI node. Returns handle (0 on failure).
     *  nodeType: 1=TEXT, 4=IMAGE, 5=TOGGLE, 7=TEXT_INPUT, 8=STACK,
     *  9=SCROLL, 10=LIST, 12=TEXT_AREA, 13=BUTTON, 14=PROGRESS,
     *  15=CHECKBOX, 16=COLUMN, 17=ROW, 18=FLEX, 19=LIST_ITEM, 26=SLIDER */
    public static native long nodeCreate(int nodeType);
    public static native void nodeDispose(long node);

    /** Tree operations */
    public static native void nodeAddChild(long parent, long child);
    public static native void nodeRemoveChild(long parent, long child);
    public static native void nodeInsertChildAt(long parent, long child, int position);

    /** Set attributes by type */
    public static native int nodeSetAttrFloat(long node, int attrType,
                                               float v0, float v1, float v2, float v3, int count);
    public static native int nodeSetAttrColor(long node, int attrType, int color);
    public static native int nodeSetAttrInt(long node, int attrType, int value);
    public static native int nodeSetAttrString(long node, int attrType, String value);

    /** Events */
    public static native int nodeRegisterEvent(long node, int eventType, int eventId);
    public static native void nodeUnregisterEvent(long node, int eventType);
    public static native void nodeMarkDirty(long node, int flag);

    // ── AudioManager ──────────────────────────────────────────────

    public static native int audioGetStreamVolume(int streamType);
    public static native int audioGetStreamMaxVolume(int streamType);
    public static native void audioSetStreamVolume(int streamType, int index, int flags);
    public static native int audioGetRingerMode();
    public static native void audioSetRingerMode(int mode);
    public static native boolean audioIsMusicActive();

    // ── MediaPlayer ───────────────────────────────────────────────

    public static native long mediaPlayerCreate();
    public static native void mediaPlayerSetDataSource(long handle, String path);
    public static native void mediaPlayerPrepare(long handle);
    public static native void mediaPlayerStart(long handle);
    public static native void mediaPlayerPause(long handle);
    public static native void mediaPlayerStop(long handle);
    public static native void mediaPlayerRelease(long handle);
    public static native void mediaPlayerSeekTo(long handle, int msec);
    public static native void mediaPlayerReset(long handle);
    public static native int mediaPlayerGetDuration(long handle);
    public static native int mediaPlayerGetCurrentPosition(long handle);
    public static native boolean mediaPlayerIsPlaying(long handle);
    public static native void mediaPlayerSetVolume(long handle, float left, float right);
    public static native void mediaPlayerSetLooping(long handle, boolean looping);

    // ── Event dispatch (called from native) ──────────────────────

    /** Called from native code when an ArkUI node event fires.
     *  Dispatches to the appropriate View's listener. */
    public static void dispatchNodeEvent(int eventId, long nodeHandle, int eventKind, String stringData) {
        android.view.View view = android.view.View.findViewByHandle(nodeHandle);
        if (view != null) {
            view.onNativeEvent(eventId, eventKind, stringData);
        }
    }
}
