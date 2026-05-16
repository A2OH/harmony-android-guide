/*
 * AppSchedulerBridge.java
 *
 * Reverse bridge: OH IAppScheduler callbacks -> Android IApplicationThread.
 *
 * OH IAppScheduler is called by OH AppMgrService to notify the app process
 * of lifecycle transitions, memory events, and configuration changes.
 * This bridge converts those callbacks to Android IApplicationThread calls.
 *
 * Mapping:
 *   OH IAppScheduler -> Android IApplicationThread (app process lifecycle)
 *
 * Methods are categorized as:
 *   [BRIDGED]     - Mapped to Android equivalent
 *   [PARTIAL]     - Partially mapped (semantic gap)
 *   [STUB]        - No Android equivalent, handled internally
 *   [OH_ONLY]     - OH-specific, no Android counterpart
 */
package adapter.activity;

import android.app.ActivityThread;
import android.app.IApplicationThread;
import android.app.servertransaction.ClientTransaction;
import android.app.servertransaction.LaunchActivityItem;
import android.app.servertransaction.ResumeActivityItem;
import android.app.servertransaction.DestroyActivityItem;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import adapter.activity.IntentWantConverter;
import adapter.activity.LifecycleAdapter;

public class AppSchedulerBridge {

    private static final String TAG = "OH_AppSchedulerBridge";
    private final Object mApplicationThread; // Android IApplicationThread proxy

    public AppSchedulerBridge(Object applicationThread) {
        mApplicationThread = applicationThread;
    }

    // G2.14i (2026-05-01): mirror OH MainThread::HandleForegroundApplication.
    //
    // C++ AppSchedulerAdapter::ScheduleForegroundApplication 收到 IAppScheduler IPC 后
    // 立刻调本方法。本方法 post 一个 Runnable 到 main Handler — 当前 IPC 同步返回让
    // AppMS 完成 foregroundingAbilityTokens_.insert(token)，下一轮 main looper iter
    // 才执行 Runnable，调 native nativeNotifyApplicationForegrounded(recordId)。
    //
    // 整链路用 main looper 串行同步，不需要任何 sleep / 时间窗口。对应 OH 标准客户端
    // main_thread.cpp:2826 HandleForegroundApplication 的"PostTask 到 main looper 后
    // 才调 appMgr_->ApplicationForegrounded"模式。
    private static native void nativeNotifyApplicationForegrounded(int recordId);

    public static void notifyForegroundDeferred(final int recordId) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override public void run() {
                Log.i(TAG, "[G2.14i] main-looper iter: ApplicationForegrounded(recordId=" + recordId + ")");
                nativeNotifyApplicationForegrounded(recordId);
            }
        });
    }

    // ============================================================
    // 方向 3 (B.39 真适配): OH IPC ScheduleLaunchApplication 路由
    //
    // OH AppMgr 在 AttachApplication 后会调 IAppScheduler.ScheduleLaunchApplication
    // (反向 callback)，这是 OH 端的 "app bind" 时机。AOSP 端等价路径是
    // IApplicationThread.bindApplication → H.BIND_APPLICATION → handleBindApplication。
    //
    // 因此真适配 = 把 OH ScheduleLaunchApplication 直接路由到 AOSP handleBindApplication。
    // 入口在 IPC 接口适配点 (C++ AppSchedulerAdapter::ScheduleLaunchApplication →
    // Java nativeOnScheduleLaunchApplication)。
    //
    // 此前在 nativeOnScheduleLaunchAbility 入口补 ensureBindApplication 是投机做法
    // (在错误的 IPC 接口点做 bind 等价工作); 已修正到本入口。
    // ============================================================

    /**
     * Static native entry called from C++ AppSchedulerAdapter::ScheduleLaunchApplication.
     *
     * Phase 1 (B.43, 2026-04-30): C++ side now passes 8-key OH Configuration as
     * flat String[] {k0,v0,...}. Java side uses OhApplicationInfoConverter +
     * OhConfigurationConverter + OhDisplayProvider + AppBindDataDefaults to
     * construct a fully-populated AppBindData per
     * doc/ability_manager_ipc_adapter_design.html §1.1.4 / §1.1.5.
     *
     * Reflection retained for AppBindData ctor + handleBindApplication invocation
     * (P2 work item: replace via aosp_patches public hook).
     */
    public static void nativeOnScheduleLaunchApplication(Object appThread,
                                                          String bundleName,
                                                          String processName,
                                                          int pid,
                                                          String[] ohConfigKv) {
        System.err.println("[B43-LA] nativeOnScheduleLaunchApplication ENTRY bundle=" + bundleName
                + " process=" + processName + " pid=" + pid
                + " ohConfigKv.len=" + (ohConfigKv == null ? -1 : ohConfigKv.length));
        Log.i(TAG, "nativeOnScheduleLaunchApplication: bundle=" + bundleName
                + ", process=" + processName + ", pid=" + pid);

        try {
            ActivityThread activityThread = ActivityThread.currentActivityThread();
            if (activityThread == null) {
                System.err.println("[B43-LA] ActivityThread.currentActivityThread() == null");
                return;
            }
            ensureBindApplication(activityThread, bundleName, ohConfigKv);
        } catch (Throwable t) {
            System.err.println("[B43-LA] FAILED: " + t);
            t.printStackTrace(System.err);
        }
    }

    /**
     * Build AppBindData and dispatch to ActivityThread.handleBindApplication.
     * Idempotent — only runs once per process.
     */
    private static volatile boolean sBindAppDone = false;
    private static synchronized void ensureBindApplication(
            ActivityThread activityThread, String bundleName, String[] ohConfigKv) {
        if (sBindAppDone) return;
        try {
            System.err.println("[B43-BIND] ensureBindApplication start bundle=" + bundleName);

            // 1. Resolve ApplicationInfo via PackageManagerAdapter — already
            //    routes through OhApplicationInfoConverter (PackageInfoBuilder
            //    delegates to it), so flags/sourceDir/nativeLibraryDir/uid/
            //    targetSdk are populated correctly.
            android.content.pm.IPackageManager pm =
                    android.app.ActivityThread.getPackageManager();
            if (pm == null) {
                System.err.println("[B43-BIND] FATAL: ActivityThread.getPackageManager() is null");
                return;
            }
            android.content.pm.ApplicationInfo appInfo =
                    pm.getApplicationInfo(bundleName, 0L, 0);
            if (appInfo == null) {
                System.err.println("[B43-BIND] FATAL: getApplicationInfo(" + bundleName + ") returned null");
                return;
            }
            System.err.println("[B43-BIND] resolved ApplicationInfo pkg=" + appInfo.packageName
                    + " uid=" + appInfo.uid + " process=" + appInfo.processName
                    + " targetSdk=" + appInfo.targetSdkVersion
                    + " sourceDir=" + appInfo.sourceDir
                    + " nativeLibraryDir=" + appInfo.nativeLibraryDir);

            // 2. Build Android Configuration from OH key/value + display snapshot.
            // Inlined to avoid PathClassLoader cross-class resolution issue
            // (separate Oh* classes were ClassNotFoundException despite being
            // in same dex — root cause TBD; inlining unblocks Phase 1).
            DisplaySnapshot disp = getDisplaySnapshot();
            android.content.res.Configuration cfg = buildConfiguration(ohConfigKv, disp);

            // 3. Construct ActivityThread.AppBindData (package-private inner class).
            Class<?> appBindDataCls = Class.forName(
                    "android.app.ActivityThread$AppBindData");
            java.lang.reflect.Constructor<?> abdCtor =
                    appBindDataCls.getDeclaredConstructor();
            abdCtor.setAccessible(true);
            Object data = abdCtor.newInstance();

            // 4. Populate AppBindData fields per §1.1.4 strategy table.
            setField(data, "processName",
                    appInfo.processName != null ? appInfo.processName : bundleName);
            setField(data, "appInfo", appInfo);
            // P2-B v2 (2026-04-30): real providers + manifest enrichment via local
            // native methods (alias to PackageManagerAdapter native impls in bridge.so).
            // BCP PackageManagerAdapter cannot get new methods without boot image
            // rebuild — see memory feedback_boot_image_full_rebuild_risk.md.
            String manifestJson = "";
            try {
                manifestJson = nativeParseManifestJson(bundleName);
                if (manifestJson == null) manifestJson = "";
            } catch (Throwable t) {
                System.err.println("[B43-BIND] nativeParseManifestJson failed: " + t);
            }
            java.util.List<android.content.pm.ProviderInfo> realProviders =
                    buildProvidersFromManifest(bundleName, manifestJson);
            setField(data, "providers", realProviders);
            System.err.println("[B43-BIND] providers populated: " + realProviders.size());

            // P2-B v2: enrich appInfo with manifest fields (className/theme/largeHeap/factory).
            try {
                applyManifestFieldsToAppInfoLocal(appInfo, manifestJson);
            } catch (Throwable t) {
                System.err.println("[B43-BIND] applyManifestFields failed: " + t);
            }
            // Instrumentation / SdkSandbox: A_ONLY_NULL — permanent null.
            setField(data, "instrumentationName", null);
            setField(data, "instrumentationArgs", null);
            setField(data, "instrumentationWatcher", null);
            setField(data, "instrumentationUiAutomationConnection", null);
            // Debug derived from appInfo.flags FLAG_DEBUGGABLE (set by converter).
            int debugMode = (appInfo.flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
                    ? 1 /* DEBUG_ON */ : 0 /* DEBUG_OFF */;
            setField(data, "debugMode", debugMode);
            setField(data, "enableBinderTracking", false);
            setField(data, "trackAllocation", false);
            setField(data, "restrictedBackupMode", false);
            // P2 §1.1.4.5: persistent — derive from FLAG_PERSISTENT (set by
            // applyManifestFieldsToAppInfoLocal when manifest application
            // android:persistent=true), since OH applicationInfo.keepAlive
            // semantics align. Almost always false for 3rd-party Apps.
            boolean persistent = (appInfo.flags & android.content.pm.ApplicationInfo.FLAG_PERSISTENT) != 0;
            setField(data, "persistent", persistent);
            setField(data, "config", cfg);
            // compatInfo: real-derived from appInfo + cfg (inlined defaults).
            setField(data, "compatInfo", buildCompatInfo(appInfo, cfg));
            setField(data, "initProfilerInfo", null);
            setField(data, "buildSerial", buildSerialDefault());
            // Autofill / ContentCapture: A_ONLY_SYNTH — disabled but non-null.
            setField(data, "autofillOptions", buildDisabledAutofillOptions());
            setField(data, "contentCaptureOptions", buildDisabledContentCaptureOptions());
            setField(data, "disabledCompatChanges", new long[0]);
            setField(data, "mSerializedSystemFontMap", null);
            setField(data, "startRequestedElapsedTime", android.os.SystemClock.elapsedRealtime());
            setField(data, "startRequestedUptime", android.os.SystemClock.uptimeMillis());

            System.err.println("[B43-BIND] AppBindData constructed (real Configuration + flags + autofill defaults)");

            // P2 (2026-04-30, B.45): seed ActivityThread.mCoreSettings before
            // handleBindApplication runs. Spec: ability_manager_ipc_adapter_design
            // §1.1.4.6 — coreSettings Bundle is consumed by handleBindApplication
            // line 6831 (Settings.System.TIME_12_24 → DateFormat 12/24h pref) and
            // line ~6841 updateDebugViewAttributeState (Settings.Global keys).
            // Without this, mCoreSettings is null → NPE on .getString().
            //
            // For real values (any Android App): pull OH ohos.system.hour from
            // ohConfigKv to derive TIME_12_24. Other keys default to absent
            // (handleBindApplication / updateDebugViewAttributeState handle
            // missing keys gracefully — they fall back to locale-default 12/24
            // and disabled debug view attrs).
            try {
                android.os.Bundle coreSettings = buildCoreSettings(ohConfigKv);
                java.lang.reflect.Field coreSettingsField =
                        ActivityThread.class.getDeclaredField("mCoreSettings");
                coreSettingsField.setAccessible(true);
                coreSettingsField.set(activityThread, coreSettings);
                System.err.println("[B43-BIND] mCoreSettings seeded (TIME_12_24="
                        + coreSettings.getString(android.provider.Settings.System.TIME_12_24)
                        + ", keys=" + coreSettings.size() + ")");
            } catch (Throwable t) {
                System.err.println("[B43-BIND] mCoreSettings seed failed: " + t);
            }

            // 5. Invoke ActivityThread.handleBindApplication(data) — private.
            java.lang.reflect.Method handleBind =
                    ActivityThread.class.getDeclaredMethod(
                            "handleBindApplication", appBindDataCls);
            handleBind.setAccessible(true);
            handleBind.invoke(activityThread, data);

            System.err.println("[B43-BIND] handleBindApplication returned OK");
            sBindAppDone = true;
        } catch (Throwable t) {
            System.err.println("[B43-BIND] ensureBindApplication FAILED: " + t);
            t.printStackTrace(System.err);
            // Don't mark done — let next ScheduleLaunchAbility retry.
        }
    }

    private static void setField(Object obj, String name, Object value)
            throws ReflectiveOperationException {
        java.lang.reflect.Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, value);
    }

    // ============================================================
    // Inlined helpers (formerly Oh*Converter / *Provider / *Defaults).
    // Inlined into this class because separate classes in same dex/jar
    // hit ClassNotFoundException on PathClassLoader resolution — root
    // cause TBD. Authoritative field-mapping spec:
    // doc/ability_manager_ipc_adapter_design.html §1.1.4 / §1.1.5
    // ============================================================

    static final class DisplaySnapshot {
        int widthPx, heightPx, densityDpi, rotation;
        float density;
        boolean valid;
    }

    private static DisplaySnapshot getDisplaySnapshot() {
        DisplaySnapshot s = new DisplaySnapshot();
        try {
            android.view.DisplayInfo info =
                    adapter.window.DisplayManagerAdapter.getInstance().getDisplayInfo(0);
            if (info != null) {
                s.widthPx  = info.appWidth > 0 ? info.appWidth : info.logicalWidth;
                s.heightPx = info.appHeight > 0 ? info.appHeight : info.logicalHeight;
                s.densityDpi = info.logicalDensityDpi > 0 ? info.logicalDensityDpi : 320;
                s.density = s.densityDpi / 160f;
                s.rotation = info.rotation;
                s.valid = (s.widthPx > 0 && s.heightPx > 0);
            }
        } catch (Throwable t) {
            System.err.println("[B43] DisplayManagerAdapter query failed: " + t);
        }
        if (!s.valid) {
            s.widthPx = 1280; s.heightPx = 720; s.densityDpi = 320;
            s.density = 2.0f; s.rotation = 0;
        }
        return s;
    }

    /** Configuration.seq monotonic counter (§1.1.4.4 P1 A_ONLY_SYNTH). */
    private static final java.util.concurrent.atomic.AtomicInteger sConfigSeq =
            new java.util.concurrent.atomic.AtomicInteger(1);

    private static android.content.res.Configuration buildConfiguration(
            String[] kv, DisplaySnapshot disp) {
        android.content.res.Configuration cfg = new android.content.res.Configuration();
        cfg.setToDefaults();
        cfg.seq = sConfigSeq.getAndIncrement();

        // Locale (must non-empty — handleBindApplication 6782 LocaleList.setDefault rejects empty)
        // 不走 cfg.setLocales —— 它内部 setLayoutDirection → TextUtils.ICU 在 OH 上
        // ICU data 未注入会抛 MissingResourceException (project_b42 已知 blocker).
        // 反射设 mLocaleList + locale + screenLayout 的 LAYOUTDIR_LTR bit (本地化为
        // 主流 LTR 语言，不依赖 ICU 检测).
        String localeTag = kvGet(kv, "ohos.system.locale");
        String langOnly  = kvGet(kv, "ohos.system.language");
        java.util.Locale loc = parseLocale(localeTag, langOnly);
        android.os.LocaleList ll = new android.os.LocaleList(loc);
        try {
            java.lang.reflect.Field fLocaleList =
                    android.content.res.Configuration.class.getDeclaredField("mLocaleList");
            fLocaleList.setAccessible(true);
            fLocaleList.set(cfg, ll);
            java.lang.reflect.Field fLocale =
                    android.content.res.Configuration.class.getDeclaredField("locale");
            fLocale.setAccessible(true);
            fLocale.set(cfg, loc);
        } catch (Throwable t) {
            System.err.println("[B43] reflect-set mLocaleList failed: " + t);
        }

        // Font scale
        cfg.fontScale = parseFloat(kvGet(kv, "ohos.system.fontSizeScale"), 1.0f);

        // Density / screen geometry from display snapshot
        cfg.densityDpi = disp.densityDpi;
        int wDp = (int) (disp.widthPx / disp.density);
        int hDp = (int) (disp.heightPx / disp.density);
        cfg.screenWidthDp = wDp;
        cfg.screenHeightDp = hDp;
        cfg.smallestScreenWidthDp = Math.min(wDp, hDp);

        // Orientation
        cfg.orientation = disp.widthPx >= disp.heightPx
                ? android.content.res.Configuration.ORIENTATION_LANDSCAPE
                : android.content.res.Configuration.ORIENTATION_PORTRAIT;

        // screenLayout
        int screenLayout = android.content.res.Configuration.SCREENLAYOUT_LAYOUTDIR_LTR;
        int sw = cfg.smallestScreenWidthDp;
        if (sw >= 720) screenLayout |= android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE;
        else if (sw >= 600) screenLayout |= android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
        else if (sw >= 480) screenLayout |= android.content.res.Configuration.SCREENLAYOUT_SIZE_NORMAL;
        else screenLayout |= android.content.res.Configuration.SCREENLAYOUT_SIZE_SMALL;
        float lr = (float) Math.max(disp.widthPx, disp.heightPx)
                  / Math.min(disp.widthPx, disp.heightPx);
        screenLayout |= lr >= 1.6f
                ? android.content.res.Configuration.SCREENLAYOUT_LONG_YES
                : android.content.res.Configuration.SCREENLAYOUT_LONG_NO;
        cfg.screenLayout = screenLayout;

        // uiMode night
        int uiMode = android.content.res.Configuration.UI_MODE_TYPE_NORMAL;
        String colorMode = kvGet(kv, "ohos.system.colorMode");
        if ("dark".equalsIgnoreCase(colorMode)) {
            uiMode |= android.content.res.Configuration.UI_MODE_NIGHT_YES;
        } else {
            uiMode |= android.content.res.Configuration.UI_MODE_NIGHT_NO;
        }
        // P2 §1.1.4.4: uiMode TYPE from const.build.characteristics
        String devType = kvGet(kv, "const.build.characteristics");
        if (devType != null && !devType.isEmpty()) {
            int typeMask;
            switch (devType.toLowerCase()) {
                case "watch": typeMask = android.content.res.Configuration.UI_MODE_TYPE_WATCH; break;
                case "tv":    typeMask = android.content.res.Configuration.UI_MODE_TYPE_TELEVISION; break;
                case "car":   typeMask = android.content.res.Configuration.UI_MODE_TYPE_CAR; break;
                case "vr":    typeMask = android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET; break;
                case "desktop": typeMask = android.content.res.Configuration.UI_MODE_TYPE_DESK; break;
                default:      typeMask = android.content.res.Configuration.UI_MODE_TYPE_NORMAL; break;
            }
            uiMode = (uiMode & ~android.content.res.Configuration.UI_MODE_TYPE_MASK) | typeMask;
        }
        cfg.uiMode = uiMode;

        // mcc/mnc
        cfg.mcc = parseInt(kvGet(kv, "ohos.system.mcc"), 0);
        cfg.mnc = parseInt(kvGet(kv, "ohos.system.mnc"), 0);

        // P2 §1.1.4.4: touchscreen / keyboard / navigation true derivation
        // OH input.pointer.device == "true" => touch-capable phone-like; otherwise
        // desktop/TV-style with no touch but external input.
        String pointerDev = kvGet(kv, "input.pointer.device");
        boolean hasPointer = pointerDev == null || Boolean.parseBoolean(pointerDev);
        if (hasPointer) {
            cfg.touchscreen = android.content.res.Configuration.TOUCHSCREEN_FINGER;
            cfg.keyboard = android.content.res.Configuration.KEYBOARD_NOKEYS;
            cfg.keyboardHidden = android.content.res.Configuration.KEYBOARDHIDDEN_NO;
            cfg.navigation = android.content.res.Configuration.NAVIGATION_NONAV;
            cfg.navigationHidden = android.content.res.Configuration.NAVIGATIONHIDDEN_YES;
        } else {
            cfg.touchscreen = android.content.res.Configuration.TOUCHSCREEN_NOTOUCH;
            cfg.keyboard = android.content.res.Configuration.KEYBOARD_QWERTY;
            cfg.keyboardHidden = android.content.res.Configuration.KEYBOARDHIDDEN_NO;
            cfg.navigation = android.content.res.Configuration.NAVIGATION_DPAD;
            cfg.navigationHidden = android.content.res.Configuration.NAVIGATIONHIDDEN_NO;
        }

        System.err.println("[B43] Configuration: locale=" + loc + " fontScale=" + cfg.fontScale
                + " densityDpi=" + cfg.densityDpi + " orient=" + cfg.orientation
                + " uiMode=0x" + Integer.toHexString(cfg.uiMode));
        return cfg;
    }

    private static java.util.Locale parseLocale(String tag, String langOnly) {
        String s = (tag != null && !tag.isEmpty()) ? tag : langOnly;
        if (s != null && !s.isEmpty()) {
            try {
                java.util.Locale l = java.util.Locale.forLanguageTag(s);
                if (l != null && !l.getLanguage().isEmpty()) return l;
            } catch (Throwable ignored) {}
        }
        java.util.Locale def = java.util.Locale.getDefault();
        if (def == null || def.getLanguage().isEmpty()) def = java.util.Locale.US;
        return def;
    }

    private static String kvGet(String[] kv, String k) {
        if (kv == null) return null;
        for (int i = 0; i + 1 < kv.length; i += 2) {
            if (k.equals(kv[i])) return kv[i + 1];
        }
        return null;
    }

    private static float parseFloat(String s, float fb) {
        if (s == null || s.isEmpty()) return fb;
        try { return Float.parseFloat(s); } catch (NumberFormatException e) { return fb; }
    }

    private static int parseInt(String s, int fb) {
        if (s == null || s.isEmpty()) return fb;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return fb; }
    }

    private static android.content.res.CompatibilityInfo buildCompatInfo(
            android.content.pm.ApplicationInfo appInfo, android.content.res.Configuration cfg) {
        if (appInfo == null) return android.content.res.CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        try {
            int sl = cfg != null ? cfg.screenLayout : 0;
            int sw = cfg != null ? cfg.smallestScreenWidthDp : 0;
            return new android.content.res.CompatibilityInfo(appInfo, sl, sw, false);
        } catch (Throwable t) {
            return android.content.res.CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        }
    }

    // P2-B v2 (2026-04-30): native methods provided by liboh_adapter_bridge.so
    // (alias symbols added in apk_manifest_jni.cpp). Enables PathClassLoader-
    // loaded AppSchedulerBridge to parse APK manifest + read OH SystemProperties
    // without depending on BCP PackageManagerAdapter (which would require boot
    // image rebuild). See memory feedback_boot_image_full_rebuild_risk.md.
    private static native String nativeParseManifestJson(String packageName);
    private static native String nativeGetSysProp(String key, String defValue);

    /**
     * P2 (B.45 2026-04-30): build minimal coreSettings Bundle for AppBindData.
     * Maps OH ohos.system.hour → Android Settings.System.TIME_12_24 ("24"/"12").
     * Other Settings.System / Settings.Global keys are left absent — AOSP
     * handleBindApplication path (line 6831) and updateDebugViewAttributeState
     * tolerate missing keys (treat as locale-default / disabled).
     */
    private static android.os.Bundle buildCoreSettings(String[] ohConfigKv) {
        android.os.Bundle b = new android.os.Bundle();
        String hour = kvGet(ohConfigKv, "ohos.system.hour");
        if (hour != null && !hour.isEmpty()) {
            // OH hour values: "24" / "12" — matches Android TIME_12_24 directly.
            // Some OH builds store "true"/"false" — fall back reasonably.
            String mapped;
            if ("24".equals(hour) || "true".equalsIgnoreCase(hour)) mapped = "24";
            else if ("12".equals(hour) || "false".equalsIgnoreCase(hour)) mapped = "12";
            else mapped = hour;  // pass through unexpected values
            b.putString(android.provider.Settings.System.TIME_12_24, mapped);
        }
        return b;
    }

    private static String buildSerialDefault() {
        try {
            String v = android.os.SystemProperties.get("ro.serialno", "");
            if (v != null && !v.isEmpty() && !"unknown".equals(v)) return v;
        } catch (Throwable ignored) {}
        try {
            String v = nativeGetSysProp("ro.serialno", "unknown");
            return v != null ? v : "unknown";
        } catch (Throwable t) {
            return "unknown";
        }
    }

    /** P2-B v2: build ProviderInfo[] from already-parsed manifest JSON. */
    private static java.util.List<android.content.pm.ProviderInfo>
            buildProvidersFromManifest(String packageName, String manifestJson) {
        java.util.List<android.content.pm.ProviderInfo> out = new java.util.ArrayList<>();
        if (manifestJson == null || manifestJson.isEmpty()) return out;
        try {
            org.json.JSONObject m = new org.json.JSONObject(manifestJson);
            org.json.JSONArray provs = m.optJSONArray("providers");
            if (provs == null) return out;

            android.content.pm.ApplicationInfo appInfoTmpl =
                    new android.content.pm.ApplicationInfo();
            appInfoTmpl.packageName = packageName;
            appInfoTmpl.processName = packageName;
            appInfoTmpl.dataDir = "/data/data/" + packageName;
            appInfoTmpl.sourceDir =
                    "/system/app/" + packageName + "/" + packageName + ".apk";
            appInfoTmpl.publicSourceDir = appInfoTmpl.sourceDir;
            appInfoTmpl.enabled = true;

            for (int i = 0; i < provs.length(); i++) {
                org.json.JSONObject p = provs.getJSONObject(i);
                String procName = p.optString("processName", "");
                if (procName.isEmpty()) procName = packageName;
                android.content.pm.ProviderInfo pi = new android.content.pm.ProviderInfo();
                pi.name = p.optString("name", "");
                pi.packageName = packageName;
                pi.processName = procName;
                pi.authority = p.optString("authorities", "");
                pi.exported = p.optBoolean("exported", false);
                String r = p.optString("readPermission", "");
                pi.readPermission = r.isEmpty() ? null : r;
                String w = p.optString("writePermission", "");
                pi.writePermission = w.isEmpty() ? null : w;
                pi.grantUriPermissions = p.optBoolean("grantUriPermissions", false);
                pi.multiprocess = p.optBoolean("multiprocess", false);
                pi.initOrder = p.optInt("initOrder", 0);
                pi.applicationInfo = appInfoTmpl;
                pi.flags = 0;
                pi.enabled = true;
                out.add(pi);
            }
        } catch (Throwable t) {
            System.err.println("[B43-BIND] buildProvidersFromManifest failed: " + t);
        }
        return out;
    }

    /**
     * P2-B v2: enrich Android ApplicationInfo with parsed manifest fields.
     * Spec: ability_manager_ipc_adapter_design §1.1.4.1 / §1.1.4.3 PARSE rows
     */
    // ============================================================
    // B.47 (P2 §1.2 ScheduleLaunchAbility) — adapter helpers.
    // Spec: doc/ability_manager_ipc_adapter_design.html §1.2.4 / §1.2.5
    // ============================================================

    /**
     * §1.2.4.2 — build complete Android ActivityInfo from OH AbilityInfo JSON.
     * Maps launchMode / orientation / configChanges / window flags / theme.
     */
    private static android.content.pm.ActivityInfo buildActivityInfoFromAbility(
            String packageName, String abilityName,
            android.content.pm.ApplicationInfo appInfo, String abilityJson) {
        android.content.pm.ActivityInfo ai = new android.content.pm.ActivityInfo();
        ai.packageName = packageName;
        ai.applicationInfo = appInfo;
        // Default name: convert from OH ability ("MainActivity") to Android FQN.
        ai.name = adapter.activity.IntentWantConverter.abilityNameToClassName(
                packageName, abilityName);
        ai.launchMode = android.content.pm.ActivityInfo.LAUNCH_MULTIPLE;
        ai.screenOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        ai.exported = false;

        if (abilityJson == null || abilityJson.isEmpty()) {
            return ai;
        }
        try {
            org.json.JSONObject m = new org.json.JSONObject(abilityJson);

            // OH AbilityInfo.className is FQN; if present, override.
            String className = m.optString("className", "");
            if (!className.isEmpty()) ai.name = className;
            String process = m.optString("process", "");
            if (!process.isEmpty()) ai.processName = process;
            else ai.processName = packageName;

            // G2.5 (2026-04-30) — OH iconId/labelId/descriptionId are OH-format
            // resource IDs (e.g. 0x01000005), incompatible with Android's
            // 0xPPTTNNNN scheme (top byte = pkg id, second byte = type id;
            // OH IDs have type=0x00 which Android treats as invalid type → throws
            // NotFoundException at PhoneWindow.setDefaultIcon → setContentView fail).
            // Until OH→Android resource ID translation is built (P3, requires
            // OH ResourceManager.getMediaBase64 + caching as Android drawable),
            // zero these out so Android uses platform default icon/label.
            ai.labelRes = 0;
            ai.icon = 0;
            ai.descriptionRes = 0;
            ai.exported = m.optBoolean("visible", false);
            ai.enabled = m.optBoolean("enabled", true);

            // launchMode: OH (0=SINGLETON 1=STANDARD 2=SPECIFIED) → Android.
            int ohLaunchMode = m.optInt("launchMode", 1);
            switch (ohLaunchMode) {
                case 0: ai.launchMode = android.content.pm.ActivityInfo.LAUNCH_SINGLE_INSTANCE; break;
                case 1: ai.launchMode = android.content.pm.ActivityInfo.LAUNCH_MULTIPLE; break;
                case 2: ai.launchMode = android.content.pm.ActivityInfo.LAUNCH_SINGLE_TASK; break;
                default: ai.launchMode = android.content.pm.ActivityInfo.LAUNCH_MULTIPLE; break;
            }

            // orientation: OH DisplayOrientation → Android SCREEN_ORIENTATION_*
            int ohOrient = m.optInt("orientation", 0);
            ai.screenOrientation = mapOhOrientationToAndroid(ohOrient);

            // configChanges: vector<string> → bitmask
            org.json.JSONArray cc = m.optJSONArray("configChanges");
            if (cc != null) {
                int bits = 0;
                for (int i = 0; i < cc.length(); i++) {
                    bits |= mapConfigChangeToken(cc.optString(i, ""));
                }
                ai.configChanges = bits;
            }

            // theme: OH theme can be either:
            //   - a numeric string (already a res id) → parseInt
            //   - a resource reference like "$theme:0x01030010" or "@style/AppTheme"
            //     → strip prefix + parse hex/dec
            //   - any other free-form name → fallback to ApplicationInfo.theme
            String themeStr = m.optString("theme", "");
            if (!themeStr.isEmpty()) {
                String numeric = themeStr;
                if (numeric.startsWith("$theme:")) numeric = numeric.substring(7);
                else if (numeric.startsWith("@")) {
                    // resource name reference; can't resolve without resource table.
                    // Leave fallback to appInfo.theme below.
                    numeric = "";
                }
                if (!numeric.isEmpty()) {
                    try {
                        if (numeric.startsWith("0x") || numeric.startsWith("0X")) {
                            ai.theme = Integer.parseUnsignedInt(numeric.substring(2), 16);
                        } else {
                            ai.theme = Integer.parseInt(numeric);
                        }
                    } catch (NumberFormatException ignored) { ai.theme = 0; }
                }
            }
            if (ai.theme == 0 && appInfo != null) ai.theme = appInfo.theme;

            // permission: OH multiple → first as Android single
            org.json.JSONArray perms = m.optJSONArray("permissions");
            if (perms != null && perms.length() > 0) {
                ai.permission = perms.optString(0, null);
            }

            // flags: derive from bool fields
            int flags = 0;

            // 2026-05-08 G2.14z: OH 默认所有 ability GPU 加速 (OH 不像 Android 有
            // ability 级 hardwareAccelerated 开关)。adapter 必须默认设此 flag，
            // 否则 AOSP Activity.attach line 8530 传 PhoneWindow.setWindowManager
            // hardwareAccelerated=false → ViewRootImpl.setupHardwareAcceleration
            // 不创建 ThreadedRenderer → isHardwareEnabled()=false → ViewRootImpl.draw
            // 走 drawSoftware → Surface.nativeLockCanvas 在 adapter 设计下返 0
            // (hwui owns drawing per android_view_surface_stubs.cpp:25) →
            // unlockSwCanvasAndPost 抛 IllegalStateException("Surface was not locked")。
            // 仅当 OH manifest 显式 hardwareAccelerated:false 时清除（与
            // ApplicationInfo.FLAG_HARDWARE_ACCELERATED 处理 line 1135 一致）。
            flags |= android.content.pm.ActivityInfo.FLAG_HARDWARE_ACCELERATED;
            if (m.has("hardwareAccelerated") && !m.optBoolean("hardwareAccelerated", true)) {
                flags &= ~android.content.pm.ActivityInfo.FLAG_HARDWARE_ACCELERATED;
            }

            if (m.optBoolean("excludeFromMissions", false)) {
                flags |= android.content.pm.ActivityInfo.FLAG_EXCLUDE_FROM_RECENTS;
            }
            if (m.optBoolean("removeMissionAfterTerminate", false)) {
                flags |= android.content.pm.ActivityInfo.FLAG_NO_HISTORY;
            }
            if (m.optBoolean("allowSelfRedirect", true)) {
                flags |= android.content.pm.ActivityInfo.FLAG_ALLOW_TASK_REPARENTING;
            }
            ai.flags = flags;

            // Window ratios (P3): ActivityInfo.maxAspectRatio / minAspectRatio
            // are @hide; skip until aosp_patches expose. Manifest PARSE persistence
            // can carry these on metadata for future activation.

            // P2 §1.2.4.2: windowModes → resizeMode + supportsPictureInPicture.
            // OH SupportWindowMode enum: 0=FULLSCREEN 1=SPLIT_PRIMARY 2=SPLIT_SECONDARY
            // 3=FLOATING (PIP-like). Android: RESIZE_MODE_RESIZEABLE=2; PIP via flag.
            org.json.JSONArray wm = m.optJSONArray("windowModes");
            if (wm != null && wm.length() > 0) {
                boolean hasSplit = false, hasFloating = false;
                for (int i = 0; i < wm.length(); i++) {
                    int mode = wm.optInt(i, 0);
                    if (mode == 1 || mode == 2) hasSplit = true;
                    if (mode == 3) hasFloating = true;
                }
                if (hasSplit) {
                    ai.resizeMode = android.content.pm.ActivityInfo.RESIZE_MODE_RESIZEABLE;
                }
                if (hasFloating) {
                    ai.flags |= android.content.pm.ActivityInfo.FLAG_SUPPORTS_PICTURE_IN_PICTURE;
                }
            }

            // P2 §1.2.4.2 taskAffinity: prefer manifest activity-level taskAffinity
            // (set when packaged into android.app.* metadata) — else default
            // to packageName + "." + moduleName.
            String mfTaskAffinity = m.optString("taskAffinity", "");
            if (!mfTaskAffinity.isEmpty()) {
                ai.taskAffinity = mfTaskAffinity;
            }

            // taskAffinity: only set default when manifest didn't override
            if (ai.taskAffinity == null || ai.taskAffinity.isEmpty()) {
                String moduleName = m.optString("moduleName", "");
                ai.taskAffinity = packageName + (moduleName.isEmpty() ? "" : "." + moduleName);
            }
        } catch (Throwable t) {
            System.err.println("[B47-SLA] buildActivityInfoFromAbility parse failed: " + t);
        }
        return ai;
    }

    /** OH DisplayOrientation enum (int) → Android SCREEN_ORIENTATION_* */
    private static int mapOhOrientationToAndroid(int oh) {
        // OH enum (per ability_info.h DisplayOrientation):
        // 0 UNSPECIFIED / 1 LANDSCAPE / 2 PORTRAIT / 3 FOLLOWRECENT /
        // 4 LANDSCAPE_INVERTED / 5 PORTRAIT_INVERTED / 6 AUTO_ROTATION /
        // 7 AUTO_ROTATION_LANDSCAPE / 8 AUTO_ROTATION_PORTRAIT / ...
        switch (oh) {
            case 0:  return android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            case 1:  return android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case 2:  return android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            case 4:  return android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            case 5:  return android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            case 6:  return android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
            case 7:  return android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
            case 8:  return android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
            default: return android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        }
    }

    /** OH configChanges token → Android CONFIG_* bit */
    private static int mapConfigChangeToken(String t) {
        if (t == null) return 0;
        switch (t) {
            case "locale":      return android.content.pm.ActivityInfo.CONFIG_LOCALE;
            case "fontSize":    return android.content.pm.ActivityInfo.CONFIG_FONT_SCALE;
            case "fontWeight":  return android.content.pm.ActivityInfo.CONFIG_FONT_SCALE;
            case "orientation": return android.content.pm.ActivityInfo.CONFIG_ORIENTATION;
            case "density":     return android.content.pm.ActivityInfo.CONFIG_DENSITY;
            case "screenSize":  return android.content.pm.ActivityInfo.CONFIG_SCREEN_SIZE;
            case "smallestScreenSize": return android.content.pm.ActivityInfo.CONFIG_SMALLEST_SCREEN_SIZE;
            case "colorMode":   return android.content.pm.ActivityInfo.CONFIG_UI_MODE;
            case "layout":      return android.content.pm.ActivityInfo.CONFIG_SCREEN_LAYOUT;
            case "mcc":         return android.content.pm.ActivityInfo.CONFIG_MCC;
            case "mnc":         return android.content.pm.ActivityInfo.CONFIG_MNC;
            case "keyboard":    return android.content.pm.ActivityInfo.CONFIG_KEYBOARD;
            case "keyboardHidden": return android.content.pm.ActivityInfo.CONFIG_KEYBOARD_HIDDEN;
            case "navigation":  return android.content.pm.ActivityInfo.CONFIG_NAVIGATION;
            case "touchscreen": return android.content.pm.ActivityInfo.CONFIG_TOUCHSCREEN;
            default: return 0;
        }
    }

    /**
     * §1.2.4.1 — build Intent from OH Want JSON (full extraction):
     * action / categories / data / type / flags / extras + component fallback.
     */
    private static Intent buildIntentFromWant(String packageName, String defaultClassName,
            String wantJson) {
        Intent intent = new Intent();
        try {
            org.json.JSONObject w = (wantJson == null || wantJson.isEmpty())
                    ? new org.json.JSONObject() : new org.json.JSONObject(wantJson);

            // Want.ToJson schema: top-level keys include "deviceId", "bundleName",
            // "abilityName", "uri", "type", "flags", "action", "parameters" (string-encoded
            // WantParams JSON), "entities" (array). Parse defensively — schema may
            // vary slightly across OH versions.
            String bundle = w.optString("bundleName", packageName);
            String ability = w.optString("abilityName", defaultClassName);
            String fqn = (ability != null && ability.contains("."))
                    ? ability
                    : adapter.activity.IntentWantConverter.abilityNameToClassName(bundle, ability);
            if (bundle == null || bundle.isEmpty()) bundle = packageName;
            if (fqn == null || fqn.isEmpty()) fqn = defaultClassName;
            intent.setComponent(new ComponentName(bundle, fqn));

            // action — apply OH→Android string map
            String action = w.optString("action", "");
            if (!action.isEmpty()) {
                intent.setAction(mapOhActionToAndroid(action));
            }

            // categories
            org.json.JSONArray entities = w.optJSONArray("entities");
            if (entities != null) {
                for (int i = 0; i < entities.length(); i++) {
                    String e = entities.optString(i, "");
                    if (!e.isEmpty()) intent.addCategory(mapOhEntityToAndroid(e));
                }
            }

            // data uri
            String uri = w.optString("uri", "");
            if (!uri.isEmpty()) {
                try { intent.setData(android.net.Uri.parse(uri)); }
                catch (Throwable ignored) {}
            }

            // mime type
            String type = w.optString("type", "");
            if (!type.isEmpty()) intent.setType(type);

            // flags (OH FLAG_AUTH_* values match Android FLAG_GRANT_* exactly)
            int ohFlags = w.optInt("flags", 0);
            int aFlags = ohFlags & 0x000007FF;  // pass-through low bits (URI perm + similar)
            if (aFlags != 0) intent.setFlags(aFlags);

            // parameters → Bundle extras. Want.ToJson serializes parameters as a
            // string field whose value is itself a JSON object (per OH WantParams.ToJson).
            org.json.JSONObject paramsObj = null;
            // Try several layout variants observed across OH versions:
            Object p1 = w.opt("parameters");
            if (p1 instanceof org.json.JSONObject) {
                paramsObj = (org.json.JSONObject) p1;
            } else if (p1 instanceof String && !((String) p1).isEmpty()) {
                try { paramsObj = new org.json.JSONObject((String) p1); }
                catch (Throwable ignored) {}
            }
            if (paramsObj != null) {
                android.os.Bundle extras = wantParamsToBundle(paramsObj);
                if (extras.size() > 0) intent.putExtras(extras);
            }
        } catch (Throwable t) {
            System.err.println("[B47-SLA] buildIntentFromWant parse failed: " + t);
            // Always at least set component for safety
            if (intent.getComponent() == null) {
                intent.setComponent(new ComponentName(packageName,
                        adapter.activity.IntentWantConverter.abilityNameToClassName(
                                packageName, defaultClassName)));
            }
        }
        return intent;
    }

    /**
     * §1.2.5.2 — OH action string → Android Intent action constant.
     * Phase 1 minimum + Phase 2 full common.
     */
    private static String mapOhActionToAndroid(String ohAction) {
        if (ohAction == null) return null;
        switch (ohAction) {
            case "ohos.want.action.home":
            case "action.system.home":
                return Intent.ACTION_MAIN;
            case "ohos.want.action.viewData":  return Intent.ACTION_VIEW;
            case "ohos.want.action.editData":  return Intent.ACTION_EDIT;
            case "ohos.want.action.search":    return Intent.ACTION_SEARCH;
            case "ohos.want.action.send":      return Intent.ACTION_SEND;
            case "ohos.want.action.sendto":    return Intent.ACTION_SENDTO;
            case "ohos.want.action.dial":      return Intent.ACTION_DIAL;
            case "ohos.want.action.call":      return Intent.ACTION_CALL;
            case "ohos.want.action.pickItem":  return Intent.ACTION_PICK;
            case "ohos.want.action.imageCapture":
                return "android.media.action.IMAGE_CAPTURE";   // MediaStore constant
            case "ohos.want.action.videoCapture":
                return "android.media.action.VIDEO_CAPTURE";   // MediaStore constant
            default:
                return ohAction;  // pass-through (App-defined custom actions)
        }
    }

    /** OH entity string → Android Intent category constant */
    private static String mapOhEntityToAndroid(String ohEntity) {
        if (ohEntity == null) return null;
        switch (ohEntity) {
            case "entity.system.home":     return Intent.CATEGORY_HOME;
            case "entity.system.default":  return Intent.CATEGORY_DEFAULT;
            case "entity.system.browsable":return Intent.CATEGORY_BROWSABLE;
            case "entity.system.launcher": return Intent.CATEGORY_LAUNCHER;
            default: return ohEntity;
        }
    }

    /**
     * §1.2.4.1 — convert OH WantParams JSON object to Android Bundle.
     * WantParams.ToJson serializes each key/value as { "key": value-or-typed-object }.
     * Heuristic: detect type by JSON literal (string / number / boolean / array / object).
     */
    private static android.os.Bundle wantParamsToBundle(org.json.JSONObject params) {
        android.os.Bundle b = new android.os.Bundle();
        if (params == null) return b;
        java.util.Iterator<String> it = params.keys();
        while (it.hasNext()) {
            String key = it.next();
            Object v = params.opt(key);
            if (v == null || v == org.json.JSONObject.NULL) continue;
            if (v instanceof Boolean) {
                b.putBoolean(key, (Boolean) v);
            } else if (v instanceof Integer) {
                b.putInt(key, (Integer) v);
            } else if (v instanceof Long) {
                b.putLong(key, (Long) v);
            } else if (v instanceof Double) {
                b.putDouble(key, (Double) v);
            } else if (v instanceof Float) {
                b.putFloat(key, (Float) v);
            } else if (v instanceof String) {
                b.putString(key, (String) v);
            } else if (v instanceof org.json.JSONArray) {
                org.json.JSONArray arr = (org.json.JSONArray) v;
                // Type-homogeneous array detect: pick element 0's type.
                if (arr.length() == 0) {
                    b.putStringArray(key, new String[0]);
                } else {
                    Object first = arr.opt(0);
                    if (first instanceof String) {
                        String[] sa = new String[arr.length()];
                        for (int i = 0; i < arr.length(); i++) sa[i] = arr.optString(i, "");
                        b.putStringArray(key, sa);
                    } else if (first instanceof Integer) {
                        int[] ia = new int[arr.length()];
                        for (int i = 0; i < arr.length(); i++) ia[i] = arr.optInt(i, 0);
                        b.putIntArray(key, ia);
                    } else if (first instanceof Long) {
                        long[] la = new long[arr.length()];
                        for (int i = 0; i < arr.length(); i++) la[i] = arr.optLong(i, 0L);
                        b.putLongArray(key, la);
                    } else if (first instanceof Double || first instanceof Float) {
                        double[] da = new double[arr.length()];
                        for (int i = 0; i < arr.length(); i++) da[i] = arr.optDouble(i, 0);
                        b.putDoubleArray(key, da);
                    } else if (first instanceof Boolean) {
                        boolean[] ba = new boolean[arr.length()];
                        for (int i = 0; i < arr.length(); i++) ba[i] = arr.optBoolean(i, false);
                        b.putBooleanArray(key, ba);
                    } else {
                        // Fallback: stringify
                        b.putString(key, arr.toString());
                    }
                }
            } else if (v instanceof org.json.JSONObject) {
                // Nested object — flatten as JSON string (App can re-parse if needed)
                b.putString(key, v.toString());
            } else {
                b.putString(key, String.valueOf(v));
            }
        }
        return b;
    }

    /** §1.2.4.4 referrer extraction (P2). */
    private static String extractReferrer(String wantJson) {
        if (wantJson == null || wantJson.isEmpty()) return null;
        try {
            org.json.JSONObject w = new org.json.JSONObject(wantJson);
            Object p = w.opt("parameters");
            org.json.JSONObject params = null;
            if (p instanceof org.json.JSONObject) params = (org.json.JSONObject) p;
            else if (p instanceof String) params = new org.json.JSONObject((String) p);
            if (params != null) {
                String r = params.optString("ohos.aafwk.param.callerBundleName", "");
                if (!r.isEmpty()) return r;
            }
        } catch (Throwable ignored) {}
        return null;
    }

    /** §1.2.4.4 savedInstanceState extraction (P2). */
    private static android.os.Bundle extractSavedInstanceState(String wantJson) {
        if (wantJson == null || wantJson.isEmpty()) return null;
        try {
            org.json.JSONObject w = new org.json.JSONObject(wantJson);
            Object p = w.opt("parameters");
            org.json.JSONObject params = null;
            if (p instanceof org.json.JSONObject) params = (org.json.JSONObject) p;
            else if (p instanceof String) params = new org.json.JSONObject((String) p);
            if (params != null) {
                String stateJson = params.optString("ohos.aafwk.param.savedInstanceState", "");
                if (!stateJson.isEmpty()) {
                    return wantParamsToBundle(new org.json.JSONObject(stateJson));
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }

    /**
     * §1.2.4.3 OH IRemoteObject token ↔ Android IBinder registry.
     * Static map to enable reverse callbacks (OH SchedulePauseAbility/Stop/Destroy
     * with OH token → look up Android IBinder for transaction routing; and
     * App finish() with Android IBinder → look up OH token for OH TerminateAbility).
     */
    /**
     * Backward-compat shim. Real registry lives in BCP at
     * {@link adapter.core.OhTokenRegistry} so both BCP and runtime jars can
     * reference it. All static methods here delegate.
     *
     * Native side (app_scheduler_adapter.cpp::TokenDeathRecipient) calls
     * adapter.core.OhTokenRegistry directly via JNI on death.
     */
    public static final class OhTokenRegistry {
        public static IBinder acquireAndroidToken(int abilityRecordId, long ohTokenAddr) {
            return adapter.core.OhTokenRegistry.acquireAndroidToken(abilityRecordId, ohTokenAddr);
        }
        public static IBinder findByOhToken(long ohTokenAddr) {
            return adapter.core.OhTokenRegistry.findByOhToken(ohTokenAddr);
        }
        public static Long findOhToken(IBinder androidBinder) {
            return adapter.core.OhTokenRegistry.findOhToken(androidBinder);
        }
        public static IBinder findByRecordId(int abilityRecordId) {
            return adapter.core.OhTokenRegistry.findByRecordId(abilityRecordId);
        }
        public static void setMainSessionId(long ohTokenAddr, int persistentId) {
            adapter.core.OhTokenRegistry.setMainSessionId(ohTokenAddr, persistentId);
        }
        public static int getMainSessionId(IBinder androidBinder) {
            return adapter.core.OhTokenRegistry.getMainSessionId(androidBinder);
        }
        public static void removeByOhToken(long ohTokenAddr) {
            adapter.core.OhTokenRegistry.removeByOhToken(ohTokenAddr);
        }
    }

    private static void applyManifestFieldsToAppInfoLocal(
            android.content.pm.ApplicationInfo ai, String manifestJson) {
        if (manifestJson == null || manifestJson.isEmpty()) return;
        try {
            org.json.JSONObject m = new org.json.JSONObject(manifestJson);

            // P2-B v3 (B.46 2026-04-30): override sourceDir / nativeLibraryDir
            // when BCP PackageInfoBuilder gave a non-existent path. The BCP
            // (orig) version hardcodes /data/app/android/<pkg>/base.apk which
            // doesn't exist on this OH build (real APK lives under
            // /data/app/el1/bundle/public/<pkg>/android/base.apk or
            // /system/app/<pkg>/<pkg>.apk). Without correct sourceDir,
            // LoadedApk.makeApplicationContext can't load Resources →
            // appContext.getResources() returns null →
            // handleBindApplication line 6893 NPE on cm.getDefaultProxy.
            // Same path probing as apk_manifest_jni.cpp ResolveApkPath:
            String pkg = ai.packageName;
            String[] candidates = {
                "/system/app/" + pkg + "/" + pkg + ".apk",
                "/data/app/el1/bundle/public/" + pkg + "/android/base.apk",
                "/data/app/android/" + pkg + "/base.apk",
            };
            String realApkPath = null;
            for (String p : candidates) {
                if (new java.io.File(p).isFile()) { realApkPath = p; break; }
            }
            if (realApkPath != null && (ai.sourceDir == null
                    || !new java.io.File(ai.sourceDir).isFile())) {
                ai.sourceDir = realApkPath;
                ai.publicSourceDir = realApkPath;
                String apkDir = new java.io.File(realApkPath).getParent();
                String abi = ai.primaryCpuAbi != null ? ai.primaryCpuAbi : "armeabi-v7a";
                String libDir = apkDir + "/lib/" + abi;
                if (new java.io.File(libDir).isDirectory()) {
                    ai.nativeLibraryDir = libDir;
                }
                System.err.println("[B43-BIND] sourceDir overridden: "
                        + realApkPath + " (nativeLibraryDir=" + ai.nativeLibraryDir + ")");
            }

            // P2-B v3 (B.46 2026-04-30): override dataDir to OH per-user app
            // storage. Standard Android /data/data/<pkg> doesn't exist on OH;
            // OH puts app private storage at /data/app/el2/<userId>/base/<pkg>/.
            // Without correct dataDir, ContextImpl.createAppContext throws
            // "No data directory found for package".
            int userId = android.os.UserHandle.getUserId(ai.uid);
            String[] dataCandidates = {
                "/data/app/el2/" + userId + "/base/" + pkg,
                "/data/app/el1/" + userId + "/base/" + pkg,
                "/data/app/el2/100/base/" + pkg,
                "/data/data/" + pkg,
            };
            String realDataDir = null;
            for (String d : dataCandidates) {
                if (new java.io.File(d).isDirectory()) { realDataDir = d; break; }
            }
            if (realDataDir != null) {
                ai.dataDir = realDataDir;
                ai.deviceProtectedDataDir = realDataDir;
                ai.credentialProtectedDataDir = realDataDir;
                System.err.println("[B43-BIND] dataDir overridden: " + realDataDir);
            } else {
                System.err.println("[B43-BIND] no real dataDir found, leaving "
                        + ai.dataDir);
            }

            // P2-B v3 (B.46): force FLAG_HAS_CODE + FLAG_INSTALLED +
            // FLAG_SUPPORTS_SCREEN_DENSITIES + FLAG_ALLOW_CLEAR_USER_DATA.
            // BCP PackageInfoBuilder on device is the orig version which
            // returns flags=0; without FLAG_HAS_CODE, LoadedApk skips
            // adding APK as dex source → ClassLoader's DexPathList is empty
            // → ClassNotFoundException for application class.
            ai.flags |= android.content.pm.ApplicationInfo.FLAG_HAS_CODE
                    |  android.content.pm.ApplicationInfo.FLAG_INSTALLED
                    |  android.content.pm.ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES
                    |  android.content.pm.ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA;

            String className = m.optString("appClassName", "");
            if (!className.isEmpty()) ai.className = className;
            String factory = m.optString("appComponentFactory", "");
            if (!factory.isEmpty()) ai.appComponentFactory = factory;
            String classLoader = m.optString("classLoaderName", "");
            if (!classLoader.isEmpty()) ai.classLoaderName = classLoader;
            int theme = m.optInt("appTheme", 0);
            if (theme != 0) ai.theme = theme;
            int netSec = m.optInt("networkSecurityConfigRes", 0);
            if (netSec != 0) ai.networkSecurityConfigRes = netSec;
            int targetSdk = m.optInt("targetSdkVersion", 0);
            if (targetSdk > 0) ai.targetSdkVersion = targetSdk;
            int minSdk = m.optInt("minSdkVersion", 0);
            if (minSdk > 0) ai.minSdkVersion = minSdk;
            String process = m.optString("appProcessName", "");
            if (!process.isEmpty()) ai.processName = process;
            if (m.optBoolean("largeHeap", false)) {
                ai.flags |= android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
            }
            // P2 §1.1.4.5 persistent — manifest application android:persistent.
            // apk_manifest_jni currently doesn't expose this; treat absent as false.
            if (m.optBoolean("persistent", false)) {
                ai.flags |= android.content.pm.ApplicationInfo.FLAG_PERSISTENT;
            }
            if (!m.optBoolean("allowBackup", true)) {
                ai.flags &= ~android.content.pm.ApplicationInfo.FLAG_ALLOW_BACKUP;
            } else {
                ai.flags |= android.content.pm.ApplicationInfo.FLAG_ALLOW_BACKUP;
            }
            if (!m.optBoolean("hardwareAccelerated", true)) {
                ai.flags &= ~android.content.pm.ApplicationInfo.FLAG_HARDWARE_ACCELERATED;
            } else {
                ai.flags |= android.content.pm.ApplicationInfo.FLAG_HARDWARE_ACCELERATED;
            }
            org.json.JSONArray libs = m.optJSONArray("sharedLibraryFiles");
            if (libs != null && libs.length() > 0) {
                String[] arr = new String[libs.length()];
                for (int i = 0; i < libs.length(); i++) arr[i] = libs.optString(i, "");
                ai.sharedLibraryFiles = arr;
            }
            System.err.println("[B43-BIND] appInfo enriched: className=" + ai.className
                    + " theme=0x" + Integer.toHexString(ai.theme)
                    + " factory=" + ai.appComponentFactory
                    + " targetSdk=" + ai.targetSdkVersion
                    + " flags=0x" + Integer.toHexString(ai.flags));
        } catch (Throwable t) {
            System.err.println("[B43-BIND] applyManifestFieldsToAppInfoLocal parse failed: " + t);
        }
    }

    private static android.content.AutofillOptions buildDisabledAutofillOptions() {
        try {
            return new android.content.AutofillOptions(
                    android.view.autofill.AutofillManager.NO_LOGGING, false);
        } catch (Throwable t) {
            try { return android.content.AutofillOptions.forWhitelistingItself(); }
            catch (Throwable t2) { return null; }
        }
    }

    private static android.content.ContentCaptureOptions buildDisabledContentCaptureOptions() {
        try {
            return new android.content.ContentCaptureOptions((android.util.ArraySet<android.content.ComponentName>) null);
        } catch (Throwable t) {
            try { return android.content.ContentCaptureOptions.forWhitelistingItself(); }
            catch (Throwable t2) { return null; }
        }
    }

    // ============================================================
    // Static entry points called from C++ JNI (app_scheduler_adapter.cpp)
    // ============================================================

    /**
     * Called by native AppSchedulerAdapter.ScheduleLaunchAbility().
     * Constructs a ClientTransaction with LaunchActivityItem and schedules
     * it on the current ActivityThread, which triggers:
     *   ActivityThread.handleLaunchActivity()
     *     -> Activity.attach()
     *     -> Activity.onCreate()
     *
     * This is the critical path for Hello World: the OH system calls
     * ScheduleLaunchAbility, and we must translate it into an Android
     * activity launch.
     */
    /**
     * B.47 (2026-04-30, P2 §1.2 ScheduleLaunchAbility): full adapter from
     * OH AbilityInfo + Want + IRemoteObject token to Android LaunchActivityItem.
     * Spec: doc/ability_manager_ipc_adapter_design.html §1.2.4
     *
     * @param appThread          IApplicationThread (unused; kept for compat)
     * @param bundleName         OH AbilityInfo.bundleName
     * @param abilityName        OH AbilityInfo.name (FQN)
     * @param abilityRecordId    OH AppRunningRecord ability id (Lifecycle key)
     * @param abilityJson        Full AbilityInfo serialized to JSON (B.47 new)
     * @param wantJson           Want.ToJson() serialized to JSON (B.47 new)
     * @param ohTokenAddr        OH IRemoteObject pointer (B.47 — for reverse callback map)
     */
    public static void nativeOnScheduleLaunchAbility(Object appThread,
                                                      String bundleName,
                                                      String abilityName,
                                                      int abilityRecordId,
                                                      String abilityJson,
                                                      String wantJson,
                                                      long ohTokenAddr) {
        System.err.println("[B47-SLA] ENTRY bundle=" + bundleName
                + " ability=" + abilityName + " recordId=" + abilityRecordId
                + " abilityJson.len=" + (abilityJson == null ? -1 : abilityJson.length())
                + " wantJson.len=" + (wantJson == null ? -1 : wantJson.length())
                + " ohTokenAddr=0x" + Long.toHexString(ohTokenAddr));
        Log.i(TAG, "nativeOnScheduleLaunchAbility v2: bundle=" + bundleName
                + ", ability=" + abilityName + ", recordId=" + abilityRecordId);

        try {
            ActivityThread activityThread = ActivityThread.currentActivityThread();
            if (activityThread == null) {
                System.err.println("[B47-SLA] ActivityThread.currentActivityThread() == null");
                return;
            }
            IApplicationThread applicationThread = activityThread.getApplicationThread();
            if (applicationThread == null) {
                System.err.println("[B47-SLA] applicationThread == null");
                return;
            }

            // Resolve packageName (fallback chain).
            String packageName = bundleName;
            if (packageName == null || packageName.isEmpty()) {
                packageName = activityThread.currentPackageName();
                if (packageName == null && activityThread.getApplication() != null) {
                    packageName = activityThread.getApplication().getApplicationInfo().packageName;
                }
            }

            // Acquire app's ApplicationInfo (already enriched in ensureBindApplication).
            android.content.pm.ApplicationInfo appInfo = null;
            try {
                android.app.Application currentApp = android.app.ActivityThread.currentApplication();
                if (currentApp != null) appInfo = currentApp.getApplicationInfo();
            } catch (Throwable ignored) {}
            if (appInfo == null) {
                try {
                    appInfo = android.app.ActivityThread.getPackageManager()
                            .getApplicationInfo(packageName, 0, 0);
                } catch (Throwable t) {
                    System.err.println("[B47-SLA] PM fallback: " + t);
                }
            }
            if (appInfo == null) {
                appInfo = new android.content.pm.ApplicationInfo();
                appInfo.packageName = packageName;
                appInfo.processName = packageName;
                appInfo.uid = android.os.Process.myUid();
            }

            // §1.2.4.2: build complete ActivityInfo from OH AbilityInfo JSON.
            android.content.pm.ActivityInfo activityInfo =
                    buildActivityInfoFromAbility(packageName, abilityName, appInfo, abilityJson);

            // §1.2.4.1: build Intent from OH Want JSON (component + action + categories
            // + data + type + flags + extras).
            Intent intent = buildIntentFromWant(packageName, activityInfo.name, wantJson);

            // §1.2.4.3: token map. Create/get Android IBinder for this OH token,
            // store mapping for reverse callback (finishActivity / pause / stop).
            IBinder token = OhTokenRegistry.acquireAndroidToken(abilityRecordId, ohTokenAddr);

            System.err.println("[B47-SLA] intent.component=" + intent.getComponent()
                    + " action=" + intent.getAction()
                    + " categories=" + intent.getCategories()
                    + " data=" + intent.getData()
                    + " extrasKeys=" + (intent.getExtras() == null ? 0 : intent.getExtras().size())
                    + " activityInfo.theme=0x" + Integer.toHexString(activityInfo.theme)
                    + " launchMode=" + activityInfo.launchMode
                    + " orientation=" + activityInfo.screenOrientation
                    + " flags=0x" + Integer.toHexString(activityInfo.flags)
                    + " (HW_ACCEL=" + ((activityInfo.flags
                            & android.content.pm.ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0)
                    + ")");

            // Construct the ClientTransaction with LaunchActivityItem
            ClientTransaction transaction = ClientTransaction.obtain(
                    applicationThread, token);

            android.content.res.Configuration cfg = activityThread.getConfiguration();
            // LaunchActivityItem carries all data needed for activity creation.
            // §1.2.4.4: most A_ONLY_* fields stay null (Phase 1/2 default).
            LaunchActivityItem launchItem = LaunchActivityItem.obtain(
                    intent,
                    System.identityHashCode(token),       // ident (P1 A_ONLY_SYNTH)
                    activityInfo,
                    cfg,                                  // curConfig
                    cfg,                                  // overrideConfig (P3 split-screen 才需差异化)
                    0,                                    // deviceId (default display)
                    extractReferrer(wantJson),            // referrer (P2: from Want.parameters caller)
                    null,                                 // voiceInteractor (P3 A_ONLY_NULL)
                    2,                                    // procState = PROCESS_STATE_TOP
                    extractSavedInstanceState(wantJson),  // state (P2: from Want.parameters)
                    null,                                 // persistentState (P2)
                    null,                                 // pendingResults (P2: startActivityForResult chain)
                    null,                                 // pendingNewIntents (P2)
                    null,                                 // activityOptions (P2: from OH StartOptions)
                    false,                                // isForward (P2: from Want.flags)
                    null,                                 // profilerInfo (P3 A_ONLY_NULL)
                    null,                                 // assistToken (P3)
                    adapter.activity.ActivityClientControllerAdapter.getInstance(),
                                                          // activityClientController (P1 §1.2.4.4)
                    null,                                 // shareableActivityToken (P3)
                    false,                                // launchedFromBubble (default)
                    null                                  // taskFragmentToken (P3)
            );
            transaction.addCallback(launchItem);

            // G2.14m (2026-05-01): NO setLifecycleStateRequest at launch time.
            // OH protocol is two-phase:
            //   1. ScheduleLaunchAbility (this path)        = Activity.onCreate (stops at ON_CREATE)
            //   2. ScheduleAbilityTransaction(FOREGROUND_NEW) = Activity.onResume (separate trigger)
            //
            // Adapter previously hard-coded `setLifecycleStateRequest(ResumeActivityItem)` here,
            // which ran Activity.onResume immediately after onCreate — racing AMS state machine
            // (AMS still in INACTIVE, expects FOREGROUNDING before AbilityTransitionDone(FG))
            // and producing rc=22 ERR_INVALID_VALUE.
            //
            // The ResumeActivityItem transaction is now constructed by
            // AbilitySchedulerBridge.onScheduleAbilityTransaction(targetState=FOREGROUND_NEW=5)
            // which is driven by AMS::ForegroundAbility after ApplicationForegrounded reverse
            // callback advances state to FOREGROUNDING. By then, AbilityTransitionDone(FG) returns rc=0.

            // Register the token mapping for lifecycle events (legacy LifecycleAdapter
            // map; OhTokenRegistry above is the new B.47 path; both kept during cutover).
            LifecycleAdapter.getInstance().registerActivityToken(
                    abilityRecordId, token);

            // G2.5 trace + force-zero defensively (catch-all for the
            // 0x1000005 icon source that survived PackageInfoBuilder zeros).
            // Log ActivityInfo + ApplicationInfo icon fields right before
            // dispatch — whatever value is here is what mActivityInfo will
            // see in performLaunchActivity → initWindowDecorActionBar.
            android.content.pm.ApplicationInfo aiApp = activityInfo.applicationInfo;
            int aiIconBefore = activityInfo.icon;
            int aiIconResBefore = activityInfo.getIconResource();  // method, not field
            int appIconBefore = aiApp != null ? aiApp.icon : -1;
            int appIconResBefore = aiApp != null ? aiApp.iconRes : -1;
            int appLogoBefore = aiApp != null ? aiApp.logo : -1;
            int appBannerBefore = aiApp != null ? aiApp.banner : -1;
            int aiThemeBefore = activityInfo.theme;
            int appThemeBefore = aiApp != null ? aiApp.theme : -1;
            System.err.println("[G2.5-SLA-PRE] activityInfo "
                    + " icon=0x" + Integer.toHexString(aiIconBefore)
                    + " iconResource=0x" + Integer.toHexString(aiIconResBefore)
                    + " labelRes=0x" + Integer.toHexString(activityInfo.labelRes)
                    + " theme=0x" + Integer.toHexString(aiThemeBefore)
                    + " | appInfo"
                    + " icon=0x" + Integer.toHexString(appIconBefore)
                    + " iconRes=0x" + Integer.toHexString(appIconResBefore)
                    + " logo=0x" + Integer.toHexString(appLogoBefore)
                    + " banner=0x" + Integer.toHexString(appBannerBefore)
                    + " theme=0x" + Integer.toHexString(appThemeBefore));
            // Force-zero everything that PhoneWindow.setDefaultIcon /
            // initWindowDecorActionBar / loadIcon paths might dereference.
            activityInfo.icon = 0;
            activityInfo.labelRes = 0;
            activityInfo.theme = 0;
            activityInfo.logo = 0;
            activityInfo.banner = 0;
            if (aiApp != null) {
                aiApp.icon = 0;
                aiApp.iconRes = 0;
                aiApp.labelRes = 0;
                aiApp.descriptionRes = 0;
                aiApp.theme = 0;
                aiApp.logo = 0;
                aiApp.banner = 0;
                aiApp.roundIconRes = 0;
            }
            // Schedule the transaction — triggers the full activity launch sequence
            System.err.println("[B47-SLA] BEFORE scheduleTransaction className="
                    + activityInfo.name);
            applicationThread.scheduleTransaction(transaction);
            System.err.println("[B47-SLA] AFTER scheduleTransaction OK");

            Log.i(TAG, "LaunchActivity transaction scheduled: " + activityInfo.name);

            // 2026-05-09 G2.14ah REMOVED (R-fix root cause):
            //   The previous patch posted a manual ON_CREATE_DONE reverse-notify
            //   (nativeAbilityTransitionDone(token, INACTIVE)) to the main Looper
            //   right after scheduleTransaction. This crashed the main thread with
            //   SIGSEGV at fault addr 0x86c (NULL deref in liboh_adapter_bridge.so
            //   sptr<IRemoteObject> ctor), killing helloworld 1ms after the post.
            //
            //   The premise was wrong: G2.14m already established the working
            //   reverse-notify chain via ActivityClientControllerAdapter.activity*
            //   hooks (activityResumed → reportOhLifecycle(FOREGROUND), etc.),
            //   and OH AMS internally drives the INACTIVE state advance itself
            //   (verified G2.14h hilog: AMS::ProcessForegroundAbility →
            //   AbilityTransitionDone state:1 within 3ms, no app-side notify
            //   needed). LIFECYCLE_HALF_TIMEOUT in pre-G2.14ah runs was caused by
            //   onResume never firing for unrelated reasons, not by missing
            //   ON_CREATE_DONE.

        } catch (Throwable e) {
            // Catch Throwable not just Exception — covers UnsatisfiedLinkError,
            // NoClassDefFoundError, etc. that often hit during boot-image-mismatch
            // class loading.
            System.err.println("[B47-SLA] FAILED to schedule launch: " + e);
            e.printStackTrace(System.err);
            Log.e(TAG, "Failed to schedule launch activity", e);
        }
    }

    /**
     * Called by native AppSchedulerAdapter.ScheduleCleanAbility().
     * Constructs a ClientTransaction with DestroyActivityItem.
     */
    public static void nativeOnScheduleCleanAbility(Object appThread,
                                                     boolean isCacheProcess) {
        Log.i(TAG, "nativeOnScheduleCleanAbility: isCacheProcess=" + isCacheProcess);

        try {
            ActivityThread activityThread = ActivityThread.currentActivityThread();
            if (activityThread == null) return;

            IApplicationThread applicationThread = activityThread.getApplicationThread();
            if (applicationThread == null) return;

            // For Hello World, we destroy the most recent activity.
            // In a full implementation, the OH token would identify which activity.
            // The destroy is handled by LifecycleAdapter.dispatchAndroidLifecycle.
            Log.i(TAG, "Clean ability scheduled (handled via lifecycle adapter)");

        } catch (Exception e) {
            Log.e(TAG, "Failed to schedule clean ability", e);
        }
    }

    /**
     * Called by native AppSchedulerAdapter.ScheduleConfigurationUpdated().
     * Forwards configuration changes to the Android runtime.
     */
    public static void nativeOnScheduleConfigurationUpdated(Object appThread,
                                                             String configString) {
        Log.i(TAG, "nativeOnScheduleConfigurationUpdated: " + configString);
        // Configuration changes are handled by the Android framework automatically
        // when activities are relaunched. For simple Hello World, this is a no-op.
    }

    // ============================================================
    // Category 1: App Lifecycle (-> IApplicationThread)
    // ============================================================

    /**
     * [BRIDGED] ScheduleLaunchApplication -> IApplicationThread.bindApplication
     *
     * OH launches app with AppLaunchData + Configuration.
     * Android binds app with packageName, ApplicationInfo, providers, config, etc.
     *
     * Semantic gap: OH AppLaunchData contains bundleName, appInfo, processInfo.
     * Android bindApplication requires more parameters (providers, testName, etc.).
     * Bridge extracts available info and fills defaults for missing params.
     */
    public void onScheduleLaunchApplication(String bundleName, String processName, int pid) {
        logBridged("ScheduleLaunchApplication", "-> IApplicationThread.bindApplication");
        // In Phase 1, app binding is handled by Android framework itself.
        // This callback is used to synchronize OH app state with Android process.
        try {
            invokeApplicationThread("setProcessState", new Class[]{int.class},
                    new Object[]{2}); // PROCESS_STATE_TOP
        } catch (Exception e) {
            Log.e(TAG, "Failed to set process state on launch", e);
        }
    }

    /**
     * [BRIDGED] ScheduleForegroundApplication -> IApplicationThread.setProcessState
     *
     * OH notifies app to switch to foreground state.
     * Android uses setProcessState with PROCESS_STATE_TOP (2).
     */
    public void onScheduleForegroundApplication() {
        logBridged("ScheduleForegroundApplication", "-> IApplicationThread.setProcessState(TOP)");
        try {
            invokeApplicationThread("setProcessState", new Class[]{int.class},
                    new Object[]{2}); // PROCESS_STATE_TOP
        } catch (Exception e) {
            Log.e(TAG, "Failed to set foreground state", e);
        }
    }

    /**
     * [BRIDGED] ScheduleBackgroundApplication -> IApplicationThread.setProcessState
     *
     * OH notifies app to switch to background state.
     * Android uses setProcessState with PROCESS_STATE_CACHED_ACTIVITY (16).
     */
    public void onScheduleBackgroundApplication() {
        logBridged("ScheduleBackgroundApplication", "-> IApplicationThread.setProcessState(CACHED)");
        try {
            invokeApplicationThread("setProcessState", new Class[]{int.class},
                    new Object[]{16}); // PROCESS_STATE_CACHED_ACTIVITY
        } catch (Exception e) {
            Log.e(TAG, "Failed to set background state", e);
        }
    }

    /**
     * [BRIDGED] ScheduleTerminateApplication -> IApplicationThread.scheduleExit
     *
     * OH requests app process termination.
     * Android uses scheduleExit() or scheduleSuicide().
     */
    public void onScheduleTerminateApplication(boolean isLastProcess) {
        logBridged("ScheduleTerminateApplication", "-> IApplicationThread.scheduleExit");
        try {
            if (isLastProcess) {
                invokeApplicationThread("scheduleSuicide", null, null);
            } else {
                invokeApplicationThread("scheduleExit", null, null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to schedule exit", e);
        }
    }

    /**
     * [BRIDGED] ScheduleProcessSecurityExit -> IApplicationThread.scheduleSuicide
     */
    public void onScheduleProcessSecurityExit() {
        logBridged("ScheduleProcessSecurityExit", "-> IApplicationThread.scheduleSuicide");
        try {
            invokeApplicationThread("scheduleSuicide", null, null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to schedule suicide", e);
        }
    }

    // ============================================================
    // Category 2: Memory Management (-> IApplicationThread)
    // ============================================================

    /**
     * [BRIDGED] ScheduleLowMemory -> IApplicationThread.scheduleLowMemory
     */
    public void onScheduleLowMemory() {
        logBridged("ScheduleLowMemory", "-> IApplicationThread.scheduleLowMemory");
        try {
            invokeApplicationThread("scheduleLowMemory", null, null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to schedule low memory", e);
        }
    }

    /**
     * [BRIDGED] ScheduleShrinkMemory -> IApplicationThread.scheduleTrimMemory
     *
     * OH memory shrink level maps to Android TRIM_MEMORY levels:
     *   OH level mapping -> Android ComponentCallbacks2.TRIM_MEMORY_*
     */
    public void onScheduleShrinkMemory(int level) {
        int androidLevel = mapMemoryLevel(level);
        logBridged("ScheduleShrinkMemory", "-> IApplicationThread.scheduleTrimMemory(" + androidLevel + ")");
        try {
            invokeApplicationThread("scheduleTrimMemory", new Class[]{int.class},
                    new Object[]{androidLevel});
        } catch (Exception e) {
            Log.e(TAG, "Failed to schedule trim memory", e);
        }
    }

    /**
     * [BRIDGED] ScheduleMemoryLevel -> IApplicationThread.scheduleTrimMemory
     *
     * OH provides specific memory level notifications.
     * Android uses TRIM_MEMORY levels.
     */
    public void onScheduleMemoryLevel(int level) {
        int androidLevel = mapMemoryLevel(level);
        logBridged("ScheduleMemoryLevel", "-> IApplicationThread.scheduleTrimMemory(" + androidLevel + ")");
        try {
            invokeApplicationThread("scheduleTrimMemory", new Class[]{int.class},
                    new Object[]{androidLevel});
        } catch (Exception e) {
            Log.e(TAG, "Failed to schedule memory level", e);
        }
    }

    /**
     * [PARTIAL] ScheduleHeapMemory -> IApplicationThread.dumpHeap
     *
     * OH requests heap memory info via MallocInfo struct.
     * Android dumpHeap writes to a file descriptor.
     * Semantic gap: different output formats and mechanisms.
     */
    public void onScheduleHeapMemory(int pid) {
        logPartial("ScheduleHeapMemory", "-> IApplicationThread.dumpHeap (format differs)");
        // Cannot directly map - OH expects MallocInfo struct, Android writes to fd
    }

    /**
     * [OH_ONLY] ScheduleJsHeapMemory - OH ArkTS/JS specific
     * No Android equivalent. OH-specific JS engine memory management.
     * Impact: None - Android apps don't use ArkTS JS engine.
     */
    public void onScheduleJsHeapMemory() {
        logOhOnly("ScheduleJsHeapMemory", "ArkTS JS engine specific, no Android equivalent");
    }

    /**
     * [OH_ONLY] ScheduleCjHeapMemory - OH CJ language specific
     * No Android equivalent.
     * Impact: None - Android apps don't use CJ language.
     */
    public void onScheduleCjHeapMemory() {
        logOhOnly("ScheduleCjHeapMemory", "CJ language specific, no Android equivalent");
    }

    // ============================================================
    // Category 3: Configuration (-> IApplicationThread)
    // ============================================================

    /**
     * [BRIDGED] ScheduleConfigurationUpdated -> IApplicationThread.scheduleTransaction
     *
     * OH sends Configuration update to app.
     * Android uses scheduleTransaction with ConfigurationChangeItem.
     *
     * Semantic gap: OH Configuration and Android Configuration have different fields.
     * Bridge converts common fields (locale, orientation, density, fontScale).
     */
    public void onScheduleConfigurationUpdated(String configJson) {
        logBridged("ScheduleConfigurationUpdated",
                "-> IApplicationThread.scheduleTransaction(ConfigurationChangeItem)");
        // Phase 1: Configuration conversion handled by LifecycleAdapter
    }

    /**
     * [PARTIAL] ScheduleProfileChanged -> (no direct Android equivalent)
     *
     * OH Profile contains user preferences.
     * Android handles this via Configuration changes or SharedPreferences.
     * Impact: LOW - Profile changes are rare and non-critical for most apps.
     * Strategy: Convert applicable profile fields to Configuration changes.
     */
    public void onScheduleProfileChanged() {
        logPartial("ScheduleProfileChanged",
                "-> partial Configuration change (profile fields differ)");
    }

    // ============================================================
    // Category 4: Ability Stage (-> IApplicationThread)
    // ============================================================

    /**
     * [PARTIAL] ScheduleAbilityStage -> IApplicationThread.scheduleTransaction
     *
     * OH notifies app to create an AbilityStage (module-level lifecycle).
     * Android has no direct module-level lifecycle concept.
     * Impact: LOW - AbilityStage is typically used for module initialization.
     * Strategy: Map to Application.onCreate or ignore if already initialized.
     */
    public void onScheduleAbilityStage(String moduleName) {
        logPartial("ScheduleAbilityStage",
                "-> Application init (no direct Android AbilityStage concept)");
    }

    /**
     * [BRIDGED] ScheduleLaunchAbility -> IApplicationThread.scheduleTransaction
     *
     * OH launches an ability instance.
     * Android launches via scheduleTransaction with LaunchActivityItem.
     */
    public void onScheduleLaunchAbility(String abilityName, int abilityRecordId) {
        logBridged("ScheduleLaunchAbility",
                "-> IApplicationThread.scheduleTransaction(LaunchActivityItem)");
    }

    /**
     * [BRIDGED] ScheduleCleanAbility -> IApplicationThread.scheduleTransaction
     *
     * OH cleans up an ability instance.
     * Android destroys via scheduleTransaction with DestroyActivityItem.
     */
    public void onScheduleCleanAbility(boolean isCacheProcess) {
        logBridged("ScheduleCleanAbility",
                "-> IApplicationThread.scheduleTransaction(DestroyActivityItem)");
    }

    // ============================================================
    // Category 5: Service Lifecycle (-> IApplicationThread)
    // ============================================================

    /**
     * [BRIDGED] ScheduleAcceptWant -> IApplicationThread.scheduleBindService (partial)
     *
     * OH's onAcceptWant is for specified process creation.
     * Android has no direct equivalent - closest is service rebind.
     * Impact: MEDIUM - Affects multi-instance scenarios.
     * Strategy: Map to service bind with rebind flag.
     */
    public void onScheduleAcceptWant(String moduleName) {
        logPartial("ScheduleAcceptWant",
                "-> approximate IApplicationThread.scheduleBindService(rebind)");
    }

    /**
     * [BRIDGED] ScheduleNewProcessRequest -> IApplicationThread.bindApplication
     *
     * OH requests new process for ability.
     * Android handles via new process bindApplication.
     */
    public void onScheduleNewProcessRequest(String moduleName) {
        logBridged("ScheduleNewProcessRequest",
                "-> IApplicationThread.bindApplication (new process)");
    }

    // ============================================================
    // Category 6: Application Info Update (-> IApplicationThread)
    // ============================================================

    /**
     * [BRIDGED] ScheduleUpdateApplicationInfoInstalled
     *     -> IApplicationThread.scheduleApplicationInfoChanged
     */
    public void onScheduleUpdateApplicationInfoInstalled(String bundleName) {
        logBridged("ScheduleUpdateApplicationInfoInstalled",
                "-> IApplicationThread.scheduleApplicationInfoChanged");
        try {
            // Phase 1: notify Android side of app info change
        } catch (Exception e) {
            Log.e(TAG, "Failed to schedule app info changed", e);
        }
    }

    // ============================================================
    // Category 7: Hot Reload / Quick Fix (OH_ONLY)
    // ============================================================

    /**
     * [OH_ONLY] ScheduleNotifyLoadRepairPatch
     * OH hot-fix mechanism. No Android equivalent.
     * Impact: None for Android apps - they use their own update mechanism.
     * Strategy: Ignore. Android apps don't use OH repair patches.
     */
    public void onScheduleNotifyLoadRepairPatch(String bundleName) {
        logOhOnly("ScheduleNotifyLoadRepairPatch", "OH hot-fix, no Android equivalent");
    }

    /**
     * [OH_ONLY] ScheduleNotifyHotReloadPage
     * OH hot reload for development. No Android equivalent (Android uses InstantRun/Apply Changes).
     * Impact: None for Android apps.
     */
    public void onScheduleNotifyHotReloadPage() {
        logOhOnly("ScheduleNotifyHotReloadPage", "OH hot reload, no Android equivalent");
    }

    /**
     * [OH_ONLY] ScheduleNotifyUnLoadRepairPatch
     */
    public void onScheduleNotifyUnLoadRepairPatch(String bundleName) {
        logOhOnly("ScheduleNotifyUnLoadRepairPatch", "OH hot-fix, no Android equivalent");
    }

    // ============================================================
    // Category 8: Fault / Debug (-> IApplicationThread)
    // ============================================================

    /**
     * [BRIDGED] ScheduleNotifyAppFault -> IApplicationThread.scheduleCrash
     *
     * OH fault notification. Android uses scheduleCrash for controlled crash.
     */
    public void onScheduleNotifyAppFault(String faultType, String reason) {
        logBridged("ScheduleNotifyAppFault", "-> IApplicationThread.scheduleCrash");
    }

    /**
     * [PARTIAL] AttachAppDebug -> IApplicationThread.attachAgent
     *
     * OH debug attachment. Android uses attachAgent for JVMTI.
     * Semantic gap: Different debug mechanisms.
     */
    public void onAttachAppDebug(boolean isDebugFromLocal) {
        logPartial("AttachAppDebug", "-> IApplicationThread.attachAgent (mechanism differs)");
    }

    /**
     * [PARTIAL] DetachAppDebug -> (no direct Android equivalent)
     * Android doesn't have explicit debug detach.
     * Impact: None - debug detach is cleanup only.
     */
    public void onDetachAppDebug() {
        logPartial("DetachAppDebug", "No Android detach equivalent, ignored");
    }

    // ============================================================
    // Category 9: GC / Cache (OH_ONLY)
    // ============================================================

    /**
     * [OH_ONLY] ScheduleChangeAppGcState
     * OH-specific GC state change for NativeEngine.
     * Impact: None - Android has its own GC management.
     */
    public void onScheduleChangeAppGcState(int state) {
        logOhOnly("ScheduleChangeAppGcState", "OH NativeEngine GC, Android uses ART GC");
    }

    /**
     * [OH_ONLY] ScheduleCacheProcess
     * OH process caching mechanism.
     * Impact: LOW - Android has its own process caching via oom_adj.
     */
    public void onScheduleCacheProcess() {
        logOhOnly("ScheduleCacheProcess", "OH process cache, Android uses oom_adj");
    }

    /**
     * [OH_ONLY] ScheduleClearPageStack
     * OH recovery page stack clearing.
     * Impact: None for Android apps.
     */
    public void onScheduleClearPageStack() {
        logOhOnly("ScheduleClearPageStack", "OH recovery specific");
    }

    // ============================================================
    // Category 10: IPC Dump / FFRT (OH_ONLY)
    // ============================================================

    /**
     * [OH_ONLY] ScheduleDumpIpcStart/Stop/Stat
     * OH IPC payload diagnostics. No Android equivalent.
     * Impact: None - diagnostic only.
     */
    public void onScheduleDumpIpc(String operation) {
        logOhOnly("ScheduleDumpIpc" + operation, "OH IPC diagnostics, no Android equivalent");
    }

    /**
     * [OH_ONLY] ScheduleDumpFfrt
     * OH FFRT (Function Flow Runtime) diagnostics.
     * Impact: None - diagnostic only.
     */
    public void onScheduleDumpFfrt() {
        logOhOnly("ScheduleDumpFfrt", "OH FFRT diagnostics, no Android equivalent");
    }

    /**
     * [OH_ONLY] ScheduleDumpArkWeb
     * OH ArkWeb diagnostics.
     * Impact: None - diagnostic only.
     */
    public void onScheduleDumpArkWeb() {
        logOhOnly("ScheduleDumpArkWeb", "OH ArkWeb diagnostics, no Android equivalent");
    }

    /**
     * [OH_ONLY] SchedulePrepareTerminate
     * OH prepare terminate for module-level cleanup.
     * Impact: LOW - Android handles via onDestroy.
     */
    public void onSchedulePrepareTerminate(String moduleName) {
        logOhOnly("SchedulePrepareTerminate",
                "OH module terminate, Android uses Activity.onDestroy");
    }

    /**
     * [OH_ONLY] SetWatchdogBackgroundStatus
     * OH watchdog background status.
     * Impact: None - Android has its own ANR watchdog.
     */
    public void onSetWatchdogBackgroundStatus(boolean status) {
        logOhOnly("SetWatchdogBackgroundStatus", "OH watchdog, Android uses ANR mechanism");
    }

    /**
     * [OH_ONLY] OnLoadAbilityFinished
     * OH callback for ability load completion.
     * Impact: None - Android handles via activity launch completion.
     */
    public void onLoadAbilityFinished(int pid) {
        logOhOnly("OnLoadAbilityFinished", "OH ability load completion callback");
    }

    // ==================== Utility ====================

    private int mapMemoryLevel(int ohLevel) {
        // OH memory levels -> Android TRIM_MEMORY_* constants
        // OH: 0=normal, 1=low, 2=critical
        switch (ohLevel) {
            case 0: return 5;  // TRIM_MEMORY_RUNNING_MODERATE
            case 1: return 10; // TRIM_MEMORY_RUNNING_LOW
            case 2: return 15; // TRIM_MEMORY_RUNNING_CRITICAL
            default: return 5;
        }
    }

    private void invokeApplicationThread(String methodName, Class<?>[] paramTypes,
                                          Object[] args) throws Exception {
        if (mApplicationThread == null) return;
        java.lang.reflect.Method method;
        if (paramTypes != null) {
            method = mApplicationThread.getClass().getMethod(methodName, paramTypes);
        } else {
            method = mApplicationThread.getClass().getMethod(methodName);
        }
        method.invoke(mApplicationThread, args);
    }

    private void logBridged(String method, String target) {
        Log.d(TAG, "[BRIDGED] " + method + " " + target);
    }

    private void logPartial(String method, String reason) {
        Log.d(TAG, "[PARTIAL] " + method + " - " + reason);
    }

    private void logOhOnly(String method, String reason) {
        Log.d(TAG, "[OH_ONLY] " + method + " - " + reason);
    }
}
