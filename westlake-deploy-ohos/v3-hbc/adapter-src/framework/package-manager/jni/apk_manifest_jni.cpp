/*
 * apk_manifest_jni.cpp
 *
 * JNI bridge: on-demand parse of an installed APK's AndroidManifest.xml to
 * deliver Android-specific fields (className/theme/largeHeap/appComponentFactory/
 * classLoaderName/networkSecurityConfigRes/processName + provider list) that
 * OH BMS does not store. Result returned as JSON for Java-side consumption.
 *
 * Authoritative spec: doc/ability_manager_ipc_adapter_design.html §1.1.4.6 / §1.1.5.2
 *
 * Why on-demand (vs. write-time persistence to OH BMS metadata):
 *  - Avoids modifying OH libbms install path
 *  - APK already lives at /system/app/<pkg>/<pkg>.apk after install
 *  - Java side caches result per packageName so the parse cost is one-shot
 *
 * Also exposes a SystemProperties bridge so Java SystemProperties.get fall back
 * to OH OHOS::system::GetParameter for properties not in the AOSP property store.
 */

#include "apk_manifest_parser.h"

#include <cstring>
#include <jni.h>
#include <nlohmann/json.hpp>
#include <string>
#include <sys/stat.h>

#include "hilog/log.h"
#include "parameters.h"  // OHOS::system::GetParameter

#undef LOG_DOMAIN
#undef LOG_TAG
namespace {
constexpr unsigned int LOG_DOMAIN = 0xD001151;
constexpr const char* LOG_TAG = "OH_ApkManifestJni";

#define LOGI(...) OHOS::HiviewDFX::HiLog::Info({LOG_CORE, LOG_DOMAIN, LOG_TAG}, __VA_ARGS__)
#define LOGW(...) OHOS::HiviewDFX::HiLog::Warn({LOG_CORE, LOG_DOMAIN, LOG_TAG}, __VA_ARGS__)
#define LOGE(...) OHOS::HiviewDFX::HiLog::Error({LOG_CORE, LOG_DOMAIN, LOG_TAG}, __VA_ARGS__)

// Resolve packageName → on-disk APK path.  The adapter installer lays out APKs
// at /system/app/<pkg>/<pkg>.apk (ref: apk_installation_design + memory
// reference_oh_app_sandbox_paths.md — only /system/app/<pkg>/ is visible from
// the app sandbox, so this is the canonical install location).
std::string ResolveApkPath(const std::string& packageName) {
    struct stat st;
    // 1. Adapter installer canonical path (visible from app sandbox).
    std::string p1 = "/system/app/" + packageName + "/" + packageName + ".apk";
    if (stat(p1.c_str(), &st) == 0 && S_ISREG(st.st_mode)) return p1;
    // 2. OH BMS canonical path for adapter-installed APKs (per V2 install entry).
    std::string p2 = "/data/app/el1/bundle/public/" + packageName + "/android/base.apk";
    if (stat(p2.c_str(), &st) == 0 && S_ISREG(st.st_mode)) return p2;
    // 3. Legacy path used by very old installer versions.
    std::string p3 = "/data/app/android/" + packageName + "/base.apk";
    if (stat(p3.c_str(), &st) == 0 && S_ISREG(st.st_mode)) return p3;
    LOGW("ResolveApkPath: APK not found for %{public}s (tried %{public}s, %{public}s, %{public}s)",
        packageName.c_str(), p1.c_str(), p2.c_str(), p3.c_str());
    return {};
}

nlohmann::json ProviderToJson(const oh_adapter::ApkManifestParser::ProviderData& p) {
    nlohmann::json j;
    j["name"] = p.name;
    j["authorities"] = p.authorities;
    j["exported"] = p.exported;
    j["readPermission"] = p.readPermission;
    j["writePermission"] = p.writePermission;
    j["grantUriPermissions"] = p.grantUriPermissions;
    j["multiprocess"] = p.multiprocess;
    j["initOrder"] = p.initOrder;
    j["processName"] = p.processName;
    return j;
}

}  // namespace

extern "C" {

// Returns the parsed AndroidManifest as JSON, fields documented in
// ability_manager_ipc_adapter_design §1.1.4.  Empty string on failure.
JNIEXPORT jstring JNICALL
Java_adapter_packagemanager_PackageManagerAdapter_nativeParseApkManifestJson(
        JNIEnv* env, jclass /*clazz*/, jstring jPackageName) {
    if (jPackageName == nullptr) {
        return env->NewStringUTF("");
    }
    const char* pkgChars = env->GetStringUTFChars(jPackageName, nullptr);
    std::string pkg(pkgChars ? pkgChars : "");
    env->ReleaseStringUTFChars(jPackageName, pkgChars);

    if (pkg.empty()) {
        return env->NewStringUTF("");
    }

    std::string apkPath = ResolveApkPath(pkg);
    if (apkPath.empty()) {
        return env->NewStringUTF("");
    }

    oh_adapter::ApkManifestParser::ManifestData m;
    if (!oh_adapter::ApkManifestParser::Parse(apkPath, m)) {
        LOGE("nativeParseApkManifestJson: ApkManifestParser::Parse failed for %{public}s",
            apkPath.c_str());
        return env->NewStringUTF("");
    }

    nlohmann::json j;
    j["packageName"] = m.packageName;
    j["versionCode"] = m.versionCode;
    j["versionName"] = m.versionName;
    j["minSdkVersion"] = m.minSdkVersion;
    j["targetSdkVersion"] = m.targetSdkVersion;
    j["sharedUserId"] = m.sharedUserId;

    j["appClassName"] = m.appClassName;
    j["appLabel"] = m.appLabel;
    j["appTheme"] = m.appTheme;
    j["appProcessName"] = m.appProcessName;
    j["appComponentFactory"] = m.appComponentFactory;
    j["classLoaderName"] = m.classLoaderName;
    j["networkSecurityConfigRes"] = m.networkSecurityConfigRes;
    j["debuggable"] = m.debuggable;
    j["largeHeap"] = m.largeHeap;
    j["allowBackup"] = m.allowBackup;
    j["persistent"] = m.persistent;
    j["hardwareAccelerated"] = m.hardwareAccelerated;
    j["extractNativeLibs"] = m.extractNativeLibs;

    nlohmann::json libs = nlohmann::json::array();
    for (const auto& lib : m.sharedLibraryFiles) libs.push_back(lib);
    j["sharedLibraryFiles"] = libs;

    nlohmann::json provs = nlohmann::json::array();
    for (const auto& p : m.providers) provs.push_back(ProviderToJson(p));
    j["providers"] = provs;

    nlohmann::json perms = nlohmann::json::array();
    for (const auto& p : m.usesPermissions) perms.push_back(p);
    j["usesPermissions"] = perms;

    LOGI("nativeParseApkManifestJson: pkg=%{public}s providers=%{public}zu className=%{public}s "
         "theme=0x%{public}x largeHeap=%{public}d",
         m.packageName.c_str(), m.providers.size(),
         m.appClassName.c_str(), m.appTheme, m.largeHeap);
    return env->NewStringUTF(j.dump().c_str());
}

// 2026-04-30 (P2-B v2): JNI alias for AppSchedulerBridge.  Same impl as
// PackageManagerAdapter.nativeParseApkManifestJson but registered under
// adapter/activity/AppSchedulerBridge.  This lets the PathClassLoader-loaded
// AppSchedulerBridge call manifest parse without depending on the BCP class
// PackageManagerAdapter (which would require a boot image rebuild to add new
// methods — see memory feedback_boot_image_full_rebuild_risk.md).
JNIEXPORT jstring JNICALL
Java_adapter_activity_AppSchedulerBridge_nativeParseManifestJson(
        JNIEnv* env, jclass clazz, jstring jPackageName) {
    return Java_adapter_packagemanager_PackageManagerAdapter_nativeParseApkManifestJson(
            env, clazz, jPackageName);
}

// OH SystemProperties bridge.  Returns the value of the given key from OH
// system parameter store (OHOS::system::GetParameter), or defValue if the key
// is not set.  Used by adapter Java for ro.serialno and similar OH-stored
// system properties that AOSP SystemProperties cannot see.
JNIEXPORT jstring JNICALL
Java_adapter_packagemanager_PackageManagerAdapter_nativeGetOhSystemProperty(
        JNIEnv* env, jclass /*clazz*/, jstring jKey, jstring jDefValue) {
    if (jKey == nullptr) {
        return env->NewStringUTF(jDefValue ? "" : "");
    }
    const char* keyChars = env->GetStringUTFChars(jKey, nullptr);
    const char* defChars = jDefValue ? env->GetStringUTFChars(jDefValue, nullptr) : "";
    std::string key(keyChars ? keyChars : "");
    std::string def(defChars ? defChars : "");
    env->ReleaseStringUTFChars(jKey, keyChars);
    if (jDefValue) env->ReleaseStringUTFChars(jDefValue, defChars);

    std::string val = OHOS::system::GetParameter(key, def);
    return env->NewStringUTF(val.c_str());
}

// 2026-04-30 (P2-B v2): JNI alias for AppSchedulerBridge.  Same impl as
// PackageManagerAdapter.nativeGetOhSystemProperty but registered for the
// PathClassLoader-loaded class.
JNIEXPORT jstring JNICALL
Java_adapter_activity_AppSchedulerBridge_nativeGetSysProp(
        JNIEnv* env, jclass clazz, jstring jKey, jstring jDefValue) {
    return Java_adapter_packagemanager_PackageManagerAdapter_nativeGetOhSystemProperty(
            env, clazz, jKey, jDefValue);
}

}  // extern "C"
