/*
 * OhApplicationInfoConverter.java
 *
 * Converts OH ApplicationInfo (delivered as JSON from oh_bundle_mgr_client.cpp
 * BundleInfoToJson) into Android android.content.pm.ApplicationInfo.
 *
 * Field mapping authoritative source: doc/ability_manager_ipc_adapter_design.html
 *   §1.1.4.1 (应用身份与代码定位)
 *   §1.1.4.2 (进程与 UID)
 *   §1.1.4.3 (SDK / 版本 / Flags)
 *
 * Phase 1 fields are filled from OH BMS JSON directly. Phase 2 fields
 * (className/theme/minSdk/targetSdk真值/largeHeap/appComponentFactory/
 * classLoaderName/sharedLibraryFiles/networkSecurityConfigRes) require
 * AndroidManifest persistence (metaData["android.app.*"]) which is a
 * separate package-manager work item; this converter reads from metaData
 * if present and falls back to safe defaults otherwise.
 */
package adapter.activity;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public final class OhApplicationInfoConverter {

    private static final String TAG = "OH_AppInfoConv";

    /** Phase 1 default for HelloWorld until AndroidManifest persistence lands. */
    private static final int DEFAULT_TARGET_SDK = 34;
    private static final int DEFAULT_MIN_SDK = 24;

    private OhApplicationInfoConverter() {}

    /** Parse OH BundleInfo JSON and produce a fully populated Android ApplicationInfo. */
    public static ApplicationInfo fromBundleInfoJson(String bundleInfoJson) {
        if (bundleInfoJson == null || bundleInfoJson.isEmpty()) {
            return null;
        }
        try {
            return convert(new JSONObject(bundleInfoJson));
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse BundleInfo JSON", e);
            return null;
        }
    }

    /** Convert pre-parsed OH BundleInfo JSON. */
    public static ApplicationInfo convert(JSONObject bundleJson) {
        ApplicationInfo ai = new ApplicationInfo();

        String bundleName = bundleJson.optString("name", "");
        ai.packageName = bundleName;

        // Pull nested OH applicationInfo block (oh_bundle_mgr_client.cpp BundleInfoToJson).
        JSONObject ohApp = bundleJson.optJSONObject("applicationInfo");
        if (ohApp == null) {
            ohApp = new JSONObject();
        }

        // §1.1.4.1 Application identity & code location -------------------------
        // packageName / processName / dataDir / sourceDir / nativeLibraryDir /
        // primaryCpuAbi : DIRECT
        ai.processName = firstNonEmpty(
                ohApp.optString("process", ""),
                ai.packageName);

        ai.dataDir = firstNonEmpty(
                ohApp.optString("dataDir", ""),
                "/data/data/" + bundleName);
        ai.deviceProtectedDataDir = ai.dataDir;
        ai.credentialProtectedDataDir = ai.dataDir;

        // sourceDir: OH installer lays out APK at /system/app/<pkg>/<pkg>.apk
        // (see apk_installation_design + memory reference_oh_app_sandbox_paths.md).
        // codePath is OH-side directory; rebuild Android-style absolute APK path.
        String codePath = ohApp.optString("codePath", "");
        if (!codePath.isEmpty() && !codePath.endsWith(".apk")) {
            ai.sourceDir = codePath + "/" + bundleName + ".apk";
        } else if (!codePath.isEmpty()) {
            ai.sourceDir = codePath;
        } else {
            ai.sourceDir = "/system/app/" + bundleName + "/" + bundleName + ".apk";
        }
        ai.publicSourceDir = ai.sourceDir;

        // nativeLibraryDir: OH BMS gives nativeLibraryPath; if absent derive from
        // codePath + cpuAbi (rk3568 = armeabi-v7a, ARM64 device = arm64-v8a).
        String ohCpuAbi = ohApp.optString("cpuAbi", "");
        ai.primaryCpuAbi = mapOhAbiToAndroid(ohCpuAbi);
        // secondaryCpuAbi: DEFAULT null (32-bit only on rk3568).
        ai.secondaryCpuAbi = null;

        String nativeLibPath = ohApp.optString("nativeLibraryPath", "");
        if (!nativeLibPath.isEmpty()) {
            ai.nativeLibraryDir = nativeLibPath;
        } else {
            String abiDir = ai.primaryCpuAbi != null ? ai.primaryCpuAbi : "armeabi-v7a";
            ai.nativeLibraryDir = "/system/app/" + bundleName + "/lib/" + abiDir;
        }

        // §1.1.4.2 Process & UID ------------------------------------------------
        int uid = ohApp.optInt("uid", -1);
        if (uid < 0) {
            uid = bundleJson.optInt("uid", -1);
        }
        ai.uid = uid;

        // §1.1.4.3 SDK / version / flags ---------------------------------------
        ai.versionCode = bundleJson.optInt("versionCode", 0);
        ai.longVersionCode = ai.versionCode; // Android long = OH uint32, high bits 0

        // targetSdk / minSdk: OH apiTargetVersion ≠ Android API level.
        // P2 PARSE 路径会从 Manifest 真读；此处 P1 默认值。
        int ohTargetVersion = ohApp.optInt("apiTargetVersion",
                bundleJson.optInt("maxSdkVersion", DEFAULT_TARGET_SDK));
        ai.targetSdkVersion = mapOhApiVersionToAndroid(ohTargetVersion, DEFAULT_TARGET_SDK);
        int ohCompatVersion = ohApp.optInt("apiCompatibleVersion", DEFAULT_MIN_SDK);
        ai.minSdkVersion = mapOhApiVersionToAndroid(ohCompatVersion, DEFAULT_MIN_SDK);

        // compileSdk: OH stores as string; default to targetSdk
        ai.compileSdkVersion = ai.targetSdkVersion;

        // Resource ids — G2.5 (2026-04-30): OH iconId/labelId/descriptionId
        // use OH's resource ID format (e.g. 0x01000005, type byte 0x00) which
        // is invalid in Android's 0xPPTTNNNN scheme (type 0 invalid).  Until
        // we build OH→Android resource ID translation (P3 via ResourceManager),
        // zero these out so PhoneWindow.setDefaultIcon falls back to
        // platform-default icon instead of throwing NotFoundException at
        // setContentView.
        ai.iconRes = 0;
        ai.labelRes = 0;
        ai.descriptionRes = 0;
        ai.theme = 0;  // OH themeId same problem

        // Flags: derive from OH bool fields
        int flags = 0;
        if (ohApp.optBoolean("debug", false)) {
            flags |= ApplicationInfo.FLAG_DEBUGGABLE;
        }
        if (ohApp.optBoolean("systemApp", false)) {
            flags |= ApplicationInfo.FLAG_SYSTEM;
        }
        // FLAG_HAS_CODE: assume true for any installed Android APK
        flags |= ApplicationInfo.FLAG_HAS_CODE;
        // FLAG_SUPPORTS_SCREEN_DENSITIES: P1 default true (modern apps); P2 may PARSE
        flags |= ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES;
        // FLAG_ALLOW_CLEAR_USER_DATA: default true
        flags |= ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA;
        // FLAG_INSTALLED: true if BMS returned this entry at all
        flags |= ApplicationInfo.FLAG_INSTALLED;
        ai.flags = flags;

        ai.enabled = ohApp.optBoolean("enabled", true);

        // §1.1.4.7 Instrumentation / SdkSandbox: A_ONLY_NULL — leave default null

        // metaData: P2 will read android.app.* keys here
        Bundle meta = parseMetaData(ohApp);
        if (meta != null) {
            ai.metaData = meta;
            applyMetaDataOverrides(ai, meta);
        }

        // className: OH may have stored Android Application class via manifest parse;
        // default null = AOSP uses android.app.Application (current Phase 1 behaviour).
        if (ai.className == null) {
            String ohClassName = ohApp.optString("name", "");
            // OH "name" is bundleName; ignore. P2 metaData["android.app.className"] takes priority.
        }

        Log.d(TAG, "Converted ApplicationInfo: pkg=" + ai.packageName
                + " uid=" + ai.uid
                + " targetSdk=" + ai.targetSdkVersion
                + " sourceDir=" + ai.sourceDir
                + " nativeLibraryDir=" + ai.nativeLibraryDir
                + " flags=0x" + Integer.toHexString(ai.flags));
        return ai;
    }

    /** OH cpuAbi string → Android primaryCpuAbi convention. */
    private static String mapOhAbiToAndroid(String ohAbi) {
        if (ohAbi == null || ohAbi.isEmpty()) {
            // rk3568 default
            return "armeabi-v7a";
        }
        switch (ohAbi) {
            case "arm":
            case "armeabi":
            case "armeabi-v7a":
                return "armeabi-v7a";
            case "arm64":
            case "arm64-v8a":
                return "arm64-v8a";
            case "x86":
                return "x86";
            case "x86_64":
                return "x86_64";
            default:
                return ohAbi;
        }
    }

    /**
     * OH apiVersion uses different numbering (e.g. 9 / 10 / 12). Map to closest
     * Android API level. Until P2 manifest parse is in, use a coarse table.
     */
    private static int mapOhApiVersionToAndroid(int ohVersion, int fallback) {
        if (ohVersion <= 0) return fallback;
        // OH 12 ≈ Android 14 (API 34); OH 11 ≈ 33; OH 10 ≈ 31; OH 9 ≈ 30.
        // For HelloWorld stage just return fallback unless OH version is sane Android-range.
        if (ohVersion >= 21 && ohVersion <= 36) {
            // Already an Android-style API level (some OH builds copy through)
            return ohVersion;
        }
        // Coarse OH→Android mapping; refined by P2 PARSE.
        switch (ohVersion) {
            case 8:  return 28;
            case 9:  return 30;
            case 10: return 31;
            case 11: return 33;
            case 12: return 34;
            default: return fallback;
        }
    }

    /** Build a Bundle from OH JSON metadata array if present (Phase 2 pre-wiring). */
    private static Bundle parseMetaData(JSONObject ohApp) {
        org.json.JSONArray arr = ohApp.optJSONArray("metaData");
        if (arr == null || arr.length() == 0) {
            return null;
        }
        Bundle b = new Bundle();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject kv = arr.optJSONObject(i);
            if (kv == null) continue;
            String key = kv.optString("name", "");
            String val = kv.optString("value", "");
            if (!key.isEmpty()) {
                b.putString(key, val);
            }
        }
        return b;
    }

    /** P2 hook: when metaData carries android.app.* overrides, apply them. */
    private static void applyMetaDataOverrides(ApplicationInfo ai, Bundle meta) {
        String className = meta.getString("android.app.className");
        if (className != null && !className.isEmpty()) {
            ai.className = className;
        }
        String factory = meta.getString("android.app.appComponentFactory");
        if (factory != null && !factory.isEmpty()) {
            ai.appComponentFactory = factory;
        }
        String classLoader = meta.getString("android.app.classLoaderName");
        if (classLoader != null && !classLoader.isEmpty()) {
            ai.classLoaderName = classLoader;
        }
        String themeStr = meta.getString("android.app.theme");
        if (themeStr != null) {
            try { ai.theme = Integer.parseInt(themeStr); } catch (NumberFormatException ignored) {}
        }
        String targetSdkStr = meta.getString("android.app.targetSdkVersion");
        if (targetSdkStr != null) {
            try { ai.targetSdkVersion = Integer.parseInt(targetSdkStr); } catch (NumberFormatException ignored) {}
        }
        String minSdkStr = meta.getString("android.app.minSdkVersion");
        if (minSdkStr != null) {
            try { ai.minSdkVersion = Integer.parseInt(minSdkStr); } catch (NumberFormatException ignored) {}
        }
        String largeHeapStr = meta.getString("android.app.largeHeap");
        if ("true".equalsIgnoreCase(largeHeapStr)) {
            ai.flags |= ApplicationInfo.FLAG_LARGE_HEAP;
        }
        String netSecRes = meta.getString("android.app.networkSecurityConfigRes");
        if (netSecRes != null) {
            try { ai.networkSecurityConfigRes = Integer.parseInt(netSecRes); } catch (NumberFormatException ignored) {}
        }
    }

    private static String firstNonEmpty(String a, String b) {
        return (a != null && !a.isEmpty()) ? a : b;
    }
}
