package com.ohos.shim.bridge;
import android.app.AlarmManager;
import android.app.Notification;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Network;
import android.provider.Telephony;
import android.telecom.Call;
import android.view.View;
import android.widget.Toast;
import android.app.AlarmManager;
import android.app.Notification;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Network;
import android.provider.Telephony;
import android.telecom.Call;
import android.view.View;
import android.widget.Toast;
import java.util.Set;

/**
 * JNI bridge between Java shim layer and OpenHarmony native APIs.
 * Native side is implemented in Rust (shim/bridge/rust/).
 * Produces liboh_bridge.so loaded at runtime.
 */
public class OHBridge {
    private static final String BACKEND_MODE_CONTROL_ANDROID = "control_android_backend";

    public static boolean strictGuestFieldProbe = true;
    private static boolean nativeAvailable;
    private static boolean nativeProbeAttempted;
    private static boolean nativeLoadAttempted;
    private static boolean subprocessMode;
    private static boolean sControlBackendResolved;
    private static boolean sControlBackendCached;

    private static void strictTrace(String message) {
        if (message == null) {
            return;
        }
        if (sControlBackendResolved && sControlBackendCached) {
            return;
        }
        try {
            com.westlake.engine.WestlakeLauncher.strictTrace(message);
        } catch (Throwable ignored) {
        }
    }

    private static void initLog(String message) {
        // Avoid stdio during bridge initialization. Charset/PrintStream startup is
        // still unstable on the Westlake ART path and logging here can abort boot.
    }

    private static boolean trySystemLoad(String path) {
        try {
            System.load(path);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean trySystemLoadLibrary(String lib) {
        try {
            System.loadLibrary(lib);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean isControlAndroidBackend() {
        if (sControlBackendResolved) {
            return sControlBackendCached;
        }
        boolean result = false;
        try {
            if (com.westlake.engine.WestlakeLauncher.isControlAndroidBackend()) {
                result = true;
            }
        } catch (Throwable ignored) {
        }
        if (!result) {
            try {
                String prop = System.getProperty("westlake.backend.mode");
                result = BACKEND_MODE_CONTROL_ANDROID.equals(prop);
            } catch (Throwable ignored) {
                result = false;
            }
        }
        sControlBackendCached = result;
        sControlBackendResolved = true;
        return result;
    }

    private static boolean probeLinkedBridge() {
        strictTrace("PF301 strict OHBridge probe entry");
        if (isControlAndroidBackend()) {
            strictTrace("PF301 strict OHBridge probe control-backend skip");
            return false;
        }
        if (nativeAvailable) {
            strictTrace("PF301 strict OHBridge probe cached");
            return true;
        }
        nativeProbeAttempted = true;
        try {
            strictTrace("PF301 strict OHBridge probe surfaceCreate call");
            long surface = surfaceCreate(0, 1, 1);
            strictTrace("PF301 strict OHBridge probe surfaceCreate returned");
            if (surface != 0) {
                try {
                    surfaceDestroy(surface);
                } catch (Throwable ignored) {
                }
            }
            nativeAvailable = true;
            strictTrace("PF301 strict OHBridge probe surfaceCreate positive");
            return true;
        } catch (UnsatisfiedLinkError | NoSuchMethodError ignored) {
            strictTrace("PF301 strict OHBridge probe surfaceCreate linkage");
        } catch (Throwable ignored) {
            // A non-linkage failure still proves the bridge symbols are present.
            nativeAvailable = true;
            strictTrace("PF301 strict OHBridge probe surfaceCreate throwable");
            return true;
        }
        try {
            strictTrace("PF301 strict OHBridge probe sdk call");
            getSDKVersion();
            nativeAvailable = true;
            strictTrace("PF301 strict OHBridge probe sdk returned");
            return true;
        } catch (Throwable ignored) {
            strictTrace("PF301 strict OHBridge probe sdk threw");
        }
        try {
            strictTrace("PF301 strict OHBridge probe brand call");
            getDeviceBrand();
            nativeAvailable = true;
            strictTrace("PF301 strict OHBridge probe brand returned");
            return true;
        } catch (Throwable ignored) {
            strictTrace("PF301 strict OHBridge probe brand threw");
        }
        try {
            strictTrace("PF301 strict OHBridge probe bitmap call");
            long bitmap = bitmapCreate(1, 1, 0);
            strictTrace("PF301 strict OHBridge probe bitmap returned");
            if (bitmap != 0) {
                try {
                    bitmapDestroy(bitmap);
                } catch (Throwable ignored) {
                }
            }
            nativeAvailable = true;
            strictTrace("PF301 strict OHBridge probe bitmap positive");
            return true;
        } catch (Throwable ignored) {
            strictTrace("PF301 strict OHBridge probe bitmap threw");
        }
        strictTrace("PF301 strict OHBridge probe false");
        return false;
    }

    private static void ensureNativeRegistration() {
        strictTrace("PF301 strict OHBridge ensure entry");
        if (isControlAndroidBackend()) {
            strictTrace("PF301 strict OHBridge ensure control-backend skip");
            return;
        }
        if (nativeAvailable) {
            strictTrace("PF301 strict OHBridge ensure cached");
            return;
        }
        nativeLoadAttempted = true;
        trySystemLoad("/data/local/tmp/westlake/libc++_shared.so");
        trySystemLoad("/data/local/tmp/westlake/libframework_stubs.so");
        trySystemLoad("/data/local/tmp/westlake/libwestlake_art.so");
        strictTrace("PF301 strict OHBridge ensure preload phase done");
        boolean loadedAny = false;
        if (trySystemLoad("/data/local/tmp/westlake/libwestlake_natives.so")) {
            loadedAny = true;
        }
        if (trySystemLoad("/data/local/tmp/westlake/liboh_bridge.so")) {
            loadedAny = true;
        }
        if (trySystemLoad("/data/local/tmp/westlake/libohbridge_sub.so")) {
            loadedAny = true;
        }
        strictTrace("PF301 strict OHBridge ensure direct load phase done");
        if (loadedAny && probeLinkedBridge()) {
            strictTrace("PF301 strict OHBridge ensure direct load positive");
            return;
        }
        if (trySystemLoadLibrary("westlake_natives")) {
            loadedAny = true;
        }
        if (trySystemLoadLibrary("oh_bridge")) {
            loadedAny = true;
        }
        strictTrace("PF301 strict OHBridge ensure library load phase done");
        if (loadedAny) {
            strictTrace("PF301 strict OHBridge ensure probe call");
            probeLinkedBridge();
            strictTrace("PF301 strict OHBridge ensure probe returned");
        }
    }

    static {
        strictTrace("PF301 strict OHBridge clinit begin");
        initLog("Static init starting...");
        strictTrace("PF301 strict OHBridge clinit after initLog");
        boolean isSubprocess = false;
        try {
            isSubprocess = !com.westlake.engine.WestlakeLauncher.isRealFrameworkFallbackAllowed();
            strictTrace("PF301 strict OHBridge clinit westlake policy returned");
        } catch (Throwable ignored) {
            strictTrace("PF301 strict OHBridge clinit westlake policy threw");
        }
        boolean controlAndroidBackend = isControlAndroidBackend();
        if (controlAndroidBackend) {
            isSubprocess = true;
            strictTrace("PF301 strict OHBridge clinit control-backend");
        }
        if (!isSubprocess) {
            strictTrace("PF301 strict OHBridge clinit prop scan call");
            try {
                isSubprocess = System.getProperty("westlake.apk.package") != null
                        || System.getProperty("westlake.apk.path") != null
                        || System.getenv("WESTLAKE_APK_PACKAGE") != null
                        || System.getenv("WESTLAKE_APK_PATH") != null
                        || System.getenv("WESTLAKE_APK_RESDIR") != null;
            } catch (Throwable ignored) {
            }
            strictTrace("PF301 strict OHBridge clinit prop scan returned");
        } else {
            strictTrace("PF301 strict OHBridge clinit prop scan skipped");
        }
        subprocessMode = isSubprocess;
        strictTrace(controlAndroidBackend
                ? "PF301 strict OHBridge clinit control-backend effective-subprocess"
                : (isSubprocess
                ? "PF301 strict OHBridge clinit subprocess"
                : "PF301 strict OHBridge clinit host"));
        if (isSubprocess) {
            // Keep strict guest clinit minimal; prove registration and native entry
            // on explicit post-clinit calls instead of reopening this earlier seam.
            strictTrace(controlAndroidBackend
                    ? "PF301 strict OHBridge clinit control-backend defer registration"
                    : "PF301 strict OHBridge clinit guest defer registration");
        } else {
            if (trySystemLoad("/data/local/tmp/westlake/liboh_bridge.so")) {
                nativeAvailable = true;
                initLog("System.load OK: /data/local/tmp/westlake/liboh_bridge.so");
            } else if (trySystemLoad("/data/local/tmp/westlake/libohbridge_sub.so")) {
                nativeAvailable = true;
                initLog("System.load OK: /data/local/tmp/westlake/libohbridge_sub.so");
            } else if (trySystemLoad("/data/local/tmp/westlake/libwestlake_natives.so")) {
                nativeAvailable = true;
                initLog("System.load OK: /data/local/tmp/westlake/libwestlake_natives.so");
            } else {
                initLog("System.load FAIL");
            }
            if (!nativeAvailable) {
                initLog("Try loadLibrary(westlake_natives)");
                if (trySystemLoadLibrary("westlake_natives")) {
                    nativeAvailable = true;
                    initLog("loadLibrary OK: westlake_natives");
                } else {
                    initLog("Try loadLibrary(oh_bridge)");
                    if (trySystemLoadLibrary("oh_bridge")) {
                        nativeAvailable = true;
                        initLog("loadLibrary OK: oh_bridge");
                    } else {
                        initLog("loadLibrary FAIL");
                    }
                }
            }
        }
        if (!nativeAvailable) {
            if (isSubprocess) {
                strictTrace("PF301 strict OHBridge clinit tail probe skipped");
            } else {
                strictTrace("PF301 strict OHBridge clinit tail probe call");
                probeLinkedBridge();
                strictTrace("PF301 strict OHBridge clinit tail probe returned");
            }
        }
        OHBridgeState.nativeAvailableSnapshot = nativeAvailable;
        OHBridgeState.subprocessSnapshot = subprocessMode;
        OHBridgeState.clinitCompleted = true;
        strictTrace("PF301 strict OHBridge clinit state published");
        strictTrace(nativeAvailable
                ? "PF301 strict OHBridge clinit positive"
                : "PF301 strict OHBridge clinit nonpositive");
        initLog(null);
    }

    public static boolean isNativeAvailable() {
        strictTrace("PF301 strict OHBridge available entry");
        strictTrace("PF301 strict OHBridge available ensure call");
        ensureNativeRegistration();
        strictTrace("PF301 strict OHBridge available ensure returned");
        if (nativeAvailable) {
            strictTrace("PF301 strict OHBridge available positive");
            return true;
        }
        strictTrace("PF301 strict OHBridge available probe call");
        boolean result = probeLinkedBridge();
        strictTrace(result
                ? "PF301 strict OHBridge available probe positive"
                : "PF301 strict OHBridge available probe nonpositive");
        return result;
    }

    public static boolean strictGuestPing() {
        strictTrace("PF301 strict OHBridge ping entry");
        strictTrace(nativeAvailable
                ? "PF301 strict OHBridge ping native positive"
                : "PF301 strict OHBridge ping native nonpositive");
        strictTrace(subprocessMode
                ? "PF301 strict OHBridge ping subprocess"
                : "PF301 strict OHBridge ping host");
        return nativeAvailable;
    }

    public static boolean strictGuestEnsureOnly() {
        strictTrace("PF301 strict OHBridge ensureOnly entry");
        ensureNativeRegistration();
        strictTrace("PF301 strict OHBridge ensureOnly returned");
        return nativeAvailable;
    }

    // ── Image decoding (stb_image) ──────────────────────────────
    /**
     * Decode PNG/JPEG bytes to ARGB pixel array.
     * Returns int[] = [width, height, pixel0, pixel1, ...] or null.
     */
    public static native int[] imageDecodeToPixels(byte[] data);
    /** Draw ARGB pixel array to canvas at position */
    public static native void canvasDrawArgbBitmap(long canvas, int[] pixels, float x, float y, int w, int h);

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

    // ── Clipboard ─────────────────────────────────────────────────

    public static native void clipboardSet(String text);
    public static native String clipboardGet();

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

    // ── OH_Drawing: Canvas ─────────────────────────────────────

    public static native long canvasCreate(long bitmapHandle);
    public static native void canvasDestroy(long canvas);
    public static native void canvasDrawRect(long canvas, float l, float t, float r, float b, long pen, long brush);
    public static native void canvasDrawCircle(long canvas, float cx, float cy, float r, long pen, long brush);
    public static native void canvasDrawLine(long canvas, float x1, float y1, float x2, float y2, long pen);
    public static native void canvasDrawPath(long canvas, long path, long pen, long brush);
    public static native void canvasDrawBitmap(long canvas, long bitmap, float x, float y);
    /** Draw raw image bytes (PNG/JPEG/WebP) — host decodes and renders */
    public static native void canvasDrawImage(long canvas, byte[] imageData, float x, float y, int w, int h);
    public static native void canvasDrawText(long canvas, String text, float x, float y, long font, long pen, long brush);
    public static native void canvasSave(long canvas);
    public static native void canvasRestore(long canvas);
    public static native void canvasTranslate(long canvas, float dx, float dy);
    public static native void canvasScale(long canvas, float sx, float sy);
    public static native void canvasRotate(long canvas, float degrees, float px, float py);
    public static native void canvasClipRect(long canvas, float l, float t, float r, float b);
    public static native void canvasClipPath(long canvas, long path);
    public static native void canvasDrawColor(long canvas, int argb);
    public static native void canvasDrawArc(long canvas, float l, float t, float r, float b, float startAngle, float sweepAngle, boolean useCenter, long pen, long brush);
    public static native void canvasDrawOval(long canvas, float l, float t, float r, float b, long pen, long brush);
    public static native void canvasDrawRoundRect(long canvas, float l, float t, float r, float b, float rx, float ry, long pen, long brush);
    public static native void canvasConcat(long canvas, float[] matrix9);

    // ── Surface / XComponent ──────────────────────────────────
    public static native long surfaceCreate(long xcomponentHandle, int width, int height);
    public static native void surfaceDestroy(long surfaceCtx);
    public static native void surfaceResize(long surfaceCtx, int width, int height);
    public static native long surfaceGetCanvas(long surfaceCtx);
    public static native int surfaceFlush(long surfaceCtx);

    // ── OH_Drawing: Pen (stroke) ────────────────────────────────

    public static native long penCreate();
    public static native void penDestroy(long pen);
    public static native void penSetColor(long pen, int argb);
    public static native void penSetWidth(long pen, float width);
    public static native void penSetAntiAlias(long pen, boolean aa);
    public static native void penSetCap(long pen, int cap);
    public static native void penSetJoin(long pen, int join);

    // ── OH_Drawing: Brush (fill) ────────────────────────────────

    public static native long brushCreate();
    public static native void brushDestroy(long brush);
    public static native void brushSetColor(long brush, int argb);

    // ── OH_Drawing: Path ────────────────────────────────────────

    public static native long pathCreate();
    public static native void pathDestroy(long path);
    public static native void pathMoveTo(long path, float x, float y);
    public static native void pathLineTo(long path, float x, float y);
    public static native void pathQuadTo(long path, float x1, float y1, float x2, float y2);
    public static native void pathCubicTo(long path, float x1, float y1, float x2, float y2, float x3, float y3);
    public static native void pathClose(long path);
    public static native void pathReset(long path);
    public static native void pathAddRect(long path, float l, float t, float r, float b, int dir);
    public static native void pathAddCircle(long path, float cx, float cy, float r, int dir);

    // ── OH_Drawing: Bitmap ──────────────────────────────────────

    public static native long bitmapCreate(int width, int height, int format);
    public static native void bitmapDestroy(long bitmap);
    public static native int bitmapGetWidth(long bitmap);
    public static native int bitmapGetHeight(long bitmap);
    public static native void bitmapSetPixel(long bitmap, int x, int y, int argb);
    public static native int bitmapGetPixel(long bitmap, int x, int y);
    /** Bulk write entire bitmap to file (header + pixels). Returns pixel count or negative on error. */
    public static native int bitmapWriteToFile(long bitmap, String path);
    /** Blit bitmap to /dev/fb0 at given scroll offset. Returns pixel count or negative on error. */
    public static native int bitmapBlitToFb0(long bitmap, int scrollY);

    // ── OH_Drawing: Font / TextBlob ─────────────────────────────

    public static native long fontCreate();
    public static native void fontDestroy(long font);
    public static native void fontSetSize(long font, float size);
    public static native float fontMeasureText(long font, String text);
    /** Returns [ascent(negative), descent, leading] for the font at its current size */
    public static native float[] fontGetMetrics(long font);

    // ── Vibrator ──────────────────────────────────────────────────

    public static native boolean vibratorHasVibrator();
    public static native void vibratorVibrate(long milliseconds);
    public static native void vibratorCancel();

    // ── Input dispatch (called from native) ────────────────────────

    /**
     * Called from native (Rust/C++) when the XComponent receives a touch event.
     * Creates a MotionEvent and dispatches through the active Activity's decor view.
     *
     * @param action   MotionEvent action (0=DOWN, 1=UP, 2=MOVE, 3=CANCEL)
     * @param x        X coordinate in surface pixels
     * @param y        Y coordinate in surface pixels
     * @param timestamp Monotonic timestamp in milliseconds
     */
    public static void dispatchTouchEvent(int action, float x, float y, long timestamp) {
        android.view.MotionEvent event = android.view.MotionEvent.obtain(action, x, y, timestamp);
        android.app.Activity activity = getResumedActivity();
        if (activity != null) {
            activity.dispatchTouchEvent(event);
        }
        event.recycle();
    }

    /**
     * Called from native when a key event is received.
     *
     * @param action   KeyEvent action (0=DOWN, 1=UP)
     * @param keyCode  Android keycode value
     * @param timestamp Monotonic timestamp in milliseconds
     */
    public static void dispatchKeyEvent(int action, int keyCode, long timestamp) {
        android.view.KeyEvent event = new android.view.KeyEvent(action, keyCode);
        android.app.Activity activity = getResumedActivity();
        if (activity != null) {
            activity.dispatchKeyEvent(event);
        }
    }

    /** Returns the currently resumed Activity, or null. */
    private static android.app.Activity getResumedActivity() {
        try {
            android.app.MiniServer server = android.app.MiniServer.get();
            if (server != null) {
                return server.getActivityManager().getResumedActivity();
            }
        } catch (Exception e) {
            // MiniServer may not be initialized
        }
        return null;
    }

    // ── Permissions ────────────────────────────────────────────────

    public static native int checkPermission(String permission);

    // ── Sensor ────────────────────────────────────────────────────

    /** Returns true if the given sensor type is available on this device. */
    public static native boolean sensorIsAvailable(int sensorType);

    /** Returns the latest sensor data for the given type, or null. */
    public static native float[] sensorGetData(int sensorType);

    // ── Event dispatch (called from native) ──────────────────────

    /** Called from native code when an ArkUI node event fires.
     *  Dispatches to the appropriate View's listener. */
    public static void dispatchNodeEvent(int eventId, long nodeHandle, int eventKind, String stringData) {
        // TODO: look up View by native handle and dispatch event
    }
}
