/*
 * PackageInfoBuilder.java
 *
 * Converts OH BundleInfo (as JSON from JNI) to Android PackageInfo.
 *
 * Used by PackageManagerAdapter to return Android-compatible query results
 * when an app calls PackageManager.getPackageInfo() etc.
 *
 * Reverse conversion chain:
 *   BMS BundleInfo -> JSON (JNI) -> PackageInfoBuilder -> Android PackageInfo
 *
 * Key reverse mappings:
 *   - AbilityInfo.name (with "Ability" suffix) -> ActivityInfo.name (via IntentWantConverter)
 *   - ohos.permission.* -> android.permission.* (via PermissionMapper)
 *   - OH codePath/dataDir -> Android sourceDir/dataDir format
 */
package adapter.packagemanager;

import adapter.activity.IntentWantConverter;
import android.content.pm.ActivityInfo;
import adapter.activity.IntentWantConverter;
import android.content.pm.ApplicationInfo;
import adapter.activity.IntentWantConverter;
import android.content.pm.PackageInfo;
import adapter.activity.IntentWantConverter;
import android.content.pm.ProviderInfo;
import adapter.activity.IntentWantConverter;
import android.content.pm.ServiceInfo;
import adapter.activity.IntentWantConverter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PackageInfoBuilder {

    private static final String TAG = "OH_PkgInfoBuilder";

    /**
     * Convert OH BundleInfo JSON to Android PackageInfo.
     *
     * Expected JSON format (from BMS via JNI):
     * {
     *   "name": "com.example.app",
     *   "versionCode": 1,
     *   "versionName": "1.0",
     *   "uid": 10086,
     *   "maxSdkVersion": 33,
     *   "abilityInfos": [{"name":"MainActivityAbility","visible":true,...}],
     *   "extensionAbilityInfos": [{"name":"MyService","type":"SERVICE",...}],
     *   "reqPermissions": ["ohos.permission.INTERNET",...]
     * }
     */
    public static PackageInfo fromBundleInfo(String bundleInfoJson) {
        PackageInfo pi = new PackageInfo();

        if (bundleInfoJson == null || bundleInfoJson.isEmpty()) {
            Log.w(TAG, "Empty bundleInfoJson");
            return pi;
        }

        try {
            JSONObject json = new JSONObject(bundleInfoJson);
            return fromBundleInfoJson(json);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse BundleInfo JSON", e);
            return pi;
        }
    }

    /**
     * Convert OH BundleInfo JSONObject to Android PackageInfo.
     */
    public static PackageInfo fromBundleInfoJson(JSONObject json) throws JSONException {
        PackageInfo pi = new PackageInfo();

        // Basic package info
        pi.packageName = json.getString("name");
        pi.versionCode = json.optInt("versionCode", 0);
        pi.versionName = json.optString("versionName", "");

        // ApplicationInfo
        pi.applicationInfo = buildApplicationInfo(json, pi.packageName);

        // AbilityInfos -> ActivityInfo[] (reverse Ability suffix)
        JSONArray abilities = json.optJSONArray("abilityInfos");
        if (abilities != null && abilities.length() > 0) {
            pi.activities = new ActivityInfo[abilities.length()];
            for (int i = 0; i < abilities.length(); i++) {
                pi.activities[i] = buildActivityInfo(
                    abilities.getJSONObject(i), pi.packageName);
            }
        }

        // ExtensionAbilityInfos -> ServiceInfo[] + ProviderInfo[]
        JSONArray extensions = json.optJSONArray("extensionAbilityInfos");
        if (extensions != null && extensions.length() > 0) {
            buildExtensionInfos(extensions, pi);
        }

        // Permission reverse mapping (ohos.permission.* -> android.permission.*)
        JSONArray perms = json.optJSONArray("reqPermissions");
        if (perms != null && perms.length() > 0) {
            pi.requestedPermissions = new String[perms.length()];
            for (int i = 0; i < perms.length(); i++) {
                pi.requestedPermissions[i] =
                    PermissionMapper.mapToAndroid(perms.getString(i));
            }
        }

        return pi;
    }

    private static ApplicationInfo buildApplicationInfo(JSONObject json, String packageName) {
        // P1 inline: 与 OhApplicationInfoConverter 同样字段，但 PackageInfoBuilder
        // 在 BCP framework jar 中，不能引用 PathClassLoader 里的 OhApplicationInfoConverter。
        // 字段映射权威：doc/ability_manager_ipc_adapter_design.html §1.1.4
        ApplicationInfo ai = new ApplicationInfo();
        ai.packageName = packageName;

        JSONObject ohApp = json.optJSONObject("applicationInfo");
        if (ohApp == null) ohApp = new JSONObject();

        // §1.1.4.1 identity & code location
        String process = ohApp.optString("process", "");
        ai.processName = process.isEmpty() ? packageName : process;

        String dataDir = ohApp.optString("dataDir", "");
        ai.dataDir = dataDir.isEmpty() ? "/data/data/" + packageName : dataDir;
        ai.deviceProtectedDataDir = ai.dataDir;
        ai.credentialProtectedDataDir = ai.dataDir;

        String codePath = ohApp.optString("codePath", "");
        if (!codePath.isEmpty() && !codePath.endsWith(".apk")) {
            ai.sourceDir = codePath + "/" + packageName + ".apk";
        } else if (!codePath.isEmpty()) {
            ai.sourceDir = codePath;
        } else {
            ai.sourceDir = "/system/app/" + packageName + "/" + packageName + ".apk";
        }
        ai.publicSourceDir = ai.sourceDir;

        String ohCpuAbi = ohApp.optString("cpuAbi", "");
        ai.primaryCpuAbi = mapAbi(ohCpuAbi);
        String nativeLibPath = ohApp.optString("nativeLibraryPath", "");
        ai.nativeLibraryDir = !nativeLibPath.isEmpty()
                ? nativeLibPath
                : "/system/app/" + packageName + "/lib/" + ai.primaryCpuAbi;

        // §1.1.4.2 process & uid
        int uid = ohApp.optInt("uid", json.optInt("uid", -1));
        ai.uid = uid;

        // §1.1.4.3 SDK version & flags
        ai.versionCode = json.optInt("versionCode", 0);
        ai.longVersionCode = ai.versionCode;
        int targetVer = ohApp.optInt("apiTargetVersion", json.optInt("maxSdkVersion", 34));
        ai.targetSdkVersion = (targetVer >= 21 && targetVer <= 36) ? targetVer : 34;
        int compatVer = ohApp.optInt("apiCompatibleVersion", 24);
        ai.minSdkVersion = (compatVer >= 21 && compatVer <= 36) ? compatVer : 24;
        ai.compileSdkVersion = ai.targetSdkVersion;

        // G2.5 (2026-04-30): OH iconId/labelId/descriptionId have type byte=0x00
        // which is invalid as Android resource ID → NotFoundException at
        // PhoneWindow.setDefaultIcon during setContentView.  Until OH→Android
        // resource ID translation is built (P3), zero out so platform default
        // icon/label fallback applies.  Both PackageItemInfo.icon (inherited)
        // AND ApplicationInfo.iconRes are zeroed because ActivityInfo.getIconResource()
        // checks `applicationInfo.icon` first (PackageItemInfo field).
        ai.icon = 0;          // PackageItemInfo.icon (inherited)
        ai.iconRes = 0;       // ApplicationInfo.iconRes
        ai.banner = 0;        // PackageItemInfo.banner (inherited)
        ai.logo = 0;          // PackageItemInfo.logo (inherited)
        ai.labelRes = 0;      // PackageItemInfo.labelRes (inherited)
        ai.descriptionRes = 0;
        ai.theme = 0;
        System.err.println("[G2.5-PIB] ApplicationInfo " + packageName
                + " icon/iconRes/labelRes/theme zeroed");

        int flags = ApplicationInfo.FLAG_HAS_CODE
                | ApplicationInfo.FLAG_INSTALLED
                | ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES
                | ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA;
        if (ohApp.optBoolean("debug", false)) flags |= ApplicationInfo.FLAG_DEBUGGABLE;
        if (ohApp.optBoolean("systemApp", false)) flags |= ApplicationInfo.FLAG_SYSTEM;
        ai.flags = flags;
        ai.enabled = ohApp.optBoolean("enabled", true);

        return ai;
    }

    private static String mapAbi(String ohAbi) {
        if (ohAbi == null || ohAbi.isEmpty()) return "armeabi-v7a";
        switch (ohAbi) {
            case "arm": case "armeabi": case "armeabi-v7a": return "armeabi-v7a";
            case "arm64": case "arm64-v8a": return "arm64-v8a";
            case "x86": return "x86";
            case "x86_64": return "x86_64";
            default: return ohAbi;
        }
    }

    private static ActivityInfo buildActivityInfo(JSONObject abilityJson,
                                                   String packageName) throws JSONException {
        ActivityInfo ai = new ActivityInfo();
        String abilityName = abilityJson.getString("name");

        // Reverse the Ability suffix to get Android class name
        ai.name = IntentWantConverter.abilityNameToClassName(packageName, abilityName);
        ai.packageName = packageName;
        ai.exported = abilityJson.optBoolean("visible", false);

        // Launch mode reverse mapping
        String launchMode = abilityJson.optString("launchMode", "STANDARD");
        if ("SINGLETON".equals(launchMode)) {
            ai.launchMode = ActivityInfo.LAUNCH_SINGLE_TASK;
        } else {
            ai.launchMode = ActivityInfo.LAUNCH_MULTIPLE;
        }

        // Orientation reverse mapping
        String orientation = abilityJson.optString("orientation", "UNSPECIFIED");
        switch (orientation) {
            case "LANDSCAPE":
                ai.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case "PORTRAIT":
                ai.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            default:
                ai.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
                break;
        }

        return ai;
    }

    private static void buildExtensionInfos(JSONArray extensions,
                                              PackageInfo pi) throws JSONException {
        int serviceCount = 0;
        int providerCount = 0;

        // Count types first
        for (int i = 0; i < extensions.length(); i++) {
            JSONObject ext = extensions.getJSONObject(i);
            String type = ext.optString("type", "");
            if ("SERVICE".equals(type)) {
                serviceCount++;
            } else if ("DATASHARE".equals(type)) {
                providerCount++;
            }
            // STATICSUBSCRIBER -> Android BroadcastReceiver (handled differently, not in PackageInfo arrays)
        }

        if (serviceCount > 0) {
            pi.services = new ServiceInfo[serviceCount];
            int idx = 0;
            for (int i = 0; i < extensions.length(); i++) {
                JSONObject ext = extensions.getJSONObject(i);
                if ("SERVICE".equals(ext.optString("type", ""))) {
                    ServiceInfo si = new ServiceInfo();
                    si.name = ext.getString("name");
                    si.packageName = pi.packageName;
                    si.exported = ext.optBoolean("visible", false);
                    pi.services[idx++] = si;
                }
            }
        }

        if (providerCount > 0) {
            pi.providers = new ProviderInfo[providerCount];
            int idx = 0;
            for (int i = 0; i < extensions.length(); i++) {
                JSONObject ext = extensions.getJSONObject(i);
                if ("DATASHARE".equals(ext.optString("type", ""))) {
                    ProviderInfo pri = new ProviderInfo();
                    pri.name = ext.getString("name");
                    pri.packageName = pi.packageName;
                    pri.exported = ext.optBoolean("visible", false);
                    String uri = ext.optString("uri", "");
                    if (uri.startsWith("datashare:///")) {
                        pri.authority = uri.substring("datashare:///".length());
                    }
                    pi.providers[idx++] = pri;
                }
            }
        }
    }
}
