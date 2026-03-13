package com.ohos.shim.bridge;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Mock OHBridge for local JVM testing (no native libraries needed).
 *
 * Replaces the real JNI bridge with in-memory mocks:
 * - Preferences: backed by ConcurrentHashMap
 * - RdbStore/Cursor: backed by in-memory SQLite (or stub)
 * - Log: prints to stdout
 * - Network: returns stub values
 * - DeviceInfo: returns mock values
 * - ArkUI nodes: tracked in memory with handle counter
 *
 * Usage: compile tests with this mock instead of the real OHBridge.
 *        javac -sourcepath test-apps/mock:shim/java ...
 */
public class OHBridge {

    // No native library load — all mock

    // ── Handle counter ──
    private static final AtomicLong sNextHandle = new AtomicLong(1);

    // ── Preferences mock ──

    private static final ConcurrentHashMap<Long, ConcurrentHashMap<String, Object>> sPrefs = new ConcurrentHashMap<>();

    public static long preferencesOpen(String name) {
        long h = sNextHandle.getAndIncrement();
        sPrefs.put(h, new ConcurrentHashMap<>());
        return h;
    }

    private static ConcurrentHashMap<String, Object> getPrefsMap(long handle) {
        ConcurrentHashMap<String, Object> map = sPrefs.get(handle);
        return map != null ? map : new ConcurrentHashMap<>();
    }

    public static String preferencesGetString(long h, String key, String def) {
        Object v = getPrefsMap(h).get(key);
        return v instanceof String ? (String) v : def;
    }
    public static int preferencesGetInt(long h, String key, int def) {
        Object v = getPrefsMap(h).get(key);
        return v instanceof Integer ? (Integer) v : def;
    }
    public static long preferencesGetLong(long h, String key, long def) {
        Object v = getPrefsMap(h).get(key);
        return v instanceof Long ? (Long) v : def;
    }
    public static float preferencesGetFloat(long h, String key, float def) {
        Object v = getPrefsMap(h).get(key);
        return v instanceof Float ? (Float) v : def;
    }
    public static boolean preferencesGetBoolean(long h, String key, boolean def) {
        Object v = getPrefsMap(h).get(key);
        return v instanceof Boolean ? (Boolean) v : def;
    }
    public static void preferencesPutString(long h, String key, String value) { getPrefsMap(h).put(key, value); }
    public static void preferencesPutInt(long h, String key, int value) { getPrefsMap(h).put(key, value); }
    public static void preferencesPutLong(long h, String key, long value) { getPrefsMap(h).put(key, value); }
    public static void preferencesPutFloat(long h, String key, float value) { getPrefsMap(h).put(key, value); }
    public static void preferencesPutBoolean(long h, String key, boolean value) { getPrefsMap(h).put(key, value); }
    public static void preferencesFlush(long h) { /* no-op */ }
    public static void preferencesRemove(long h, String key) { getPrefsMap(h).remove(key); }
    public static void preferencesClear(long h) { getPrefsMap(h).clear(); }
    public static void preferencesClose(long h) { sPrefs.remove(h); }

    // ── RdbStore mock (stub) ──

    public static long rdbStoreOpen(String dbName, int version) { return sNextHandle.getAndIncrement(); }
    public static void rdbStoreExecSQL(long h, String sql) { /* stub */ }
    public static long rdbStoreQuery(long h, String sql, String[] args) { return 0; }
    public static long rdbStoreInsert(long h, String table, String json) { return sNextHandle.getAndIncrement(); }
    public static int rdbStoreUpdate(long h, String json, String table, String where, String[] args) { return 0; }
    public static int rdbStoreDelete(long h, String table, String where, String[] args) { return 0; }
    public static void rdbStoreBeginTransaction(long h) { }
    public static void rdbStoreCommit(long h) { }
    public static void rdbStoreRollback(long h) { }
    public static void rdbStoreClose(long h) { }

    // ── ResultSet mock (stub) ──

    public static boolean resultSetGoToFirstRow(long h) { return false; }
    public static boolean resultSetGoToNextRow(long h) { return false; }
    public static int resultSetGetColumnIndex(long h, String name) { return -1; }
    public static String resultSetGetString(long h, int idx) { return ""; }
    public static int resultSetGetInt(long h, int idx) { return 0; }
    public static long resultSetGetLong(long h, int idx) { return 0; }
    public static float resultSetGetFloat(long h, int idx) { return 0; }
    public static double resultSetGetDouble(long h, int idx) { return 0; }
    public static byte[] resultSetGetBlob(long h, int idx) { return new byte[0]; }
    public static boolean resultSetIsNull(long h, int idx) { return true; }
    public static int resultSetGetRowCount(long h) { return 0; }
    public static int resultSetGetColumnCount(long h) { return 0; }
    public static String resultSetGetColumnName(long h, int idx) { return ""; }
    public static void resultSetClose(long h) { }

    // ── Notification mock ──

    public static void notificationPublish(int id, String title, String text, String channelId, int priority) {
        System.out.println("[MOCK] Notification #" + id + ": " + title + " — " + text);
    }
    public static void notificationCancel(int id) {
        System.out.println("[MOCK] Cancel notification #" + id);
    }
    public static void notificationAddSlot(String channelId, String channelName, int importance) {
        System.out.println("[MOCK] Add slot: " + channelId);
    }

    // ── Reminder mock ──

    private static int sReminderId = 1;
    public static int reminderScheduleTimer(int delay, String title, String content, String target, String params) {
        System.out.println("[MOCK] Reminder in " + delay + "s: " + title);
        return sReminderId++;
    }
    public static void reminderCancel(int id) {
        System.out.println("[MOCK] Cancel reminder #" + id);
    }

    // ── Navigation mock ──

    public static void startAbility(String bundle, String ability, String params) {
        System.out.println("[MOCK] Start ability: " + bundle + "/" + ability);
    }
    public static void terminateSelf() {
        System.out.println("[MOCK] Terminate self");
    }

    // ── Logging mock ──

    public static void logDebug(String tag, String msg) { System.out.println("[D/" + tag + "] " + msg); }
    public static void logInfo(String tag, String msg) { System.out.println("[I/" + tag + "] " + msg); }
    public static void logWarn(String tag, String msg) { System.out.println("[W/" + tag + "] " + msg); }
    public static void logError(String tag, String msg) { System.out.println("[E/" + tag + "] " + msg); }

    // ── Toast mock ──

    public static void showToast(String message, int duration) {
        System.out.println("[MOCK] Toast: " + message + " (" + duration + "ms)");
    }

    // ── Network mock ──

    public static String httpRequest(String url, String method, String headers, String body) {
        System.out.println("[MOCK] HTTP " + method + " " + url);
        return "{\"mock\":true}";
    }
    public static boolean isNetworkAvailable() { return true; }
    public static int getNetworkType() { return 1; }

    // ── WiFi mock ──

    private static volatile boolean sWifiEnabled = true;

    public static boolean wifiIsEnabled() { return sWifiEnabled; }
    public static boolean wifiSetEnabled(boolean enabled) {
        sWifiEnabled = enabled;
        return true;
    }
    public static int wifiGetState() {
        return sWifiEnabled ? 3 /* WIFI_STATE_ENABLED */ : 1 /* WIFI_STATE_DISABLED */;
    }
    public static String wifiGetSSID() { return "MockWiFi"; }
    public static int wifiGetRssi() { return -50; }
    public static int wifiGetLinkSpeed() { return 72; }
    public static int wifiGetFrequency() { return 2412; }

    // ── Device Info mock ──

    public static String getDeviceBrand() { return "MockBrand"; }
    public static String getDeviceModel() { return "MockModel"; }
    public static String getOSVersion() { return "OpenHarmony-Mock-4.0"; }
    public static int getSDKVersion() { return 12; }

    // ── Telephony mock ──

    public static String telephonyGetDeviceId()           { return "mock-device-id-12345"; }
    public static String telephonyGetLine1Number()        { return "+1555000000"; }
    public static String telephonyGetNetworkOperatorName(){ return "MockCarrier"; }
    public static int telephonyGetSimState()              { return 5; /* SIM_STATE_READY */ }
    public static int telephonyGetPhoneType()             { return 1; /* PHONE_TYPE_GSM */ }
    public static int telephonyGetNetworkType()           { return 13; /* NETWORK_TYPE_LTE */ }

    // ── Location mock ──

    /** Mock: returns San Francisco coordinates [lat, lon, alt]. */
    public static double[] locationGetLast() {
        return new double[]{37.7749, -122.4194, 10.0};
    }

    /** Mock: location is always enabled. */
    public static boolean locationIsEnabled() {
        return true;
    }

    // ── ArkUI Node API mock ──

    private static final ConcurrentHashMap<Long, String> sNodes = new ConcurrentHashMap<>();

    public static int arkuiInit() {
        System.out.println("[MOCK] ArkUI init");
        return 0;
    }

    public static long nodeCreate(int nodeType) {
        long h = sNextHandle.getAndIncrement();
        sNodes.put(h, "node_type_" + nodeType);
        return h;
    }

    public static void nodeDispose(long node) { sNodes.remove(node); }
    public static void nodeAddChild(long parent, long child) { /* tracked in Java ViewGroup */ }
    public static void nodeRemoveChild(long parent, long child) { }
    public static void nodeInsertChildAt(long parent, long child, int pos) { }

    public static int nodeSetAttrFloat(long node, int attr, float v0, float v1, float v2, float v3, int count) { return 0; }
    public static int nodeSetAttrColor(long node, int attr, int color) { return 0; }
    public static int nodeSetAttrInt(long node, int attr, int value) { return 0; }
    public static int nodeSetAttrString(long node, int attr, String value) { return 0; }

    public static int nodeRegisterEvent(long node, int eventType, int eventId) { return 0; }
    public static void nodeUnregisterEvent(long node, int eventType) { }
    public static void nodeMarkDirty(long node, int flag) { }

    // ── AudioManager mock ──

    // Per-stream volumes (indices 0..5), max volume = 15, default = 7
    private static final int AUDIO_STREAM_COUNT = 6;
    private static final int[] sStreamVolume    = {7, 7, 7, 7, 7, 7};
    private static final int   AUDIO_MAX_VOL    = 15;
    private static int sRingerMode = 2; // RINGER_MODE_NORMAL

    public static int audioGetStreamVolume(int streamType) {
        if (streamType < 0 || streamType >= AUDIO_STREAM_COUNT) return 7;
        return sStreamVolume[streamType];
    }

    public static int audioGetStreamMaxVolume(int streamType) {
        return AUDIO_MAX_VOL;
    }

    public static void audioSetStreamVolume(int streamType, int index, int flags) {
        if (streamType >= 0 && streamType < AUDIO_STREAM_COUNT) {
            sStreamVolume[streamType] = Math.max(0, Math.min(AUDIO_MAX_VOL, index));
        }
    }

    public static int audioGetRingerMode() {
        return sRingerMode;
    }

    public static void audioSetRingerMode(int mode) {
        sRingerMode = mode;
    }

    public static boolean audioIsMusicActive() {
        return false;
    }

    // ── MediaPlayer mock ──

    private enum MediaPlayerState { IDLE, PLAYING, PAUSED, STOPPED }
    private static final ConcurrentHashMap<Long, MediaPlayerState> sMediaPlayers = new ConcurrentHashMap<>();

    public static long mediaPlayerCreate() {
        long h = sNextHandle.getAndIncrement();
        sMediaPlayers.put(h, MediaPlayerState.IDLE);
        System.out.println("[MOCK] MediaPlayer create handle=" + h);
        return h;
    }

    public static void mediaPlayerSetDataSource(long h, String path) {
        System.out.println("[MOCK] MediaPlayer[" + h + "] setDataSource: " + path);
    }

    public static void mediaPlayerPrepare(long h) {
        System.out.println("[MOCK] MediaPlayer[" + h + "] prepare");
    }

    public static void mediaPlayerStart(long h) {
        sMediaPlayers.put(h, MediaPlayerState.PLAYING);
        System.out.println("[MOCK] MediaPlayer[" + h + "] start");
    }

    public static void mediaPlayerPause(long h) {
        sMediaPlayers.put(h, MediaPlayerState.PAUSED);
        System.out.println("[MOCK] MediaPlayer[" + h + "] pause");
    }

    public static void mediaPlayerStop(long h) {
        sMediaPlayers.put(h, MediaPlayerState.STOPPED);
        System.out.println("[MOCK] MediaPlayer[" + h + "] stop");
    }

    public static void mediaPlayerRelease(long h) {
        sMediaPlayers.remove(h);
        System.out.println("[MOCK] MediaPlayer[" + h + "] release");
    }

    public static void mediaPlayerSeekTo(long h, int msec) {
        System.out.println("[MOCK] MediaPlayer[" + h + "] seekTo " + msec + "ms");
    }

    public static void mediaPlayerReset(long h) {
        sMediaPlayers.put(h, MediaPlayerState.IDLE);
        System.out.println("[MOCK] MediaPlayer[" + h + "] reset");
    }

    public static int mediaPlayerGetDuration(long h) {
        return 30000; // mock 30 seconds
    }

    public static int mediaPlayerGetCurrentPosition(long h) {
        return 0;
    }

    public static boolean mediaPlayerIsPlaying(long h) {
        return MediaPlayerState.PLAYING.equals(sMediaPlayers.get(h));
    }

    public static void mediaPlayerSetVolume(long h, float left, float right) {
        System.out.println("[MOCK] MediaPlayer[" + h + "] setVolume left=" + left + " right=" + right);
    }

    public static void mediaPlayerSetLooping(long h, boolean looping) {
        System.out.println("[MOCK] MediaPlayer[" + h + "] setLooping=" + looping);
    }

    // ── Event dispatch ──

    public static void dispatchNodeEvent(int eventId, long nodeHandle, int eventKind, String stringData) {
        android.view.View view = android.view.View.findViewByHandle(nodeHandle);
        if (view != null) {
            view.onNativeEvent(eventId, eventKind, stringData);
        }
    }
}
