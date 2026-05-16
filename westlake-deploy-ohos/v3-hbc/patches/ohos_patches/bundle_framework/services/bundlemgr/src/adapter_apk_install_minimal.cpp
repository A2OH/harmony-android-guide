/*
 * adapter_apk_install_minimal.cpp — gap 0.2/0.6 real AXML-parsing APK install
 *
 * 2026-04-11 v2: replaces the original hardcoded filename→packageName mapping
 * with a real AndroidManifest.xml (AXML binary format) parser. Also writes
 * parsed Activity / Service info into InnerBundleInfo so that OH AMS's query
 * path (bm dump / am start) can find abilities by name.
 *
 * Depends on:
 *   - libandroidfw.so (cross-compiled to out/aosp_lib/, provides ResXMLParser /
 *     ResXMLTree for binary XML parsing)
 *   - apk_manifest_parser.cpp (framework/package-manager/jni/) which uses
 *     ResXMLParser + minizip unzip.h to extract AndroidManifest.xml from the
 *     .apk ZIP and populate oh_adapter::ApkManifestParser::ManifestData
 *
 * Provides `OHOS::AppExecFwk::ProcessApkInstall(apkPath, installParam)` as a
 * NAMESPACE-SCOPE free function. The existing bundle_installer.cpp.patch
 * calls it by unqualified name from inside BundleInstaller::Install().
 *
 * What this implementation does (Hello World MVP path):
 *   1. Parse AndroidManifest.xml via oh_adapter::ApkManifestParser::Parse
 *   2. Create /data/app/android/<packageName>/ install directory
 *   3. Copy the .apk to <installDir>/base.apk (C FILE*, no <fstream>)
 *   4. Build InnerBundleInfo with:
 *      - ApplicationInfo (bundleName, codePath, bundleType=APP_ANDROID)
 *      - InnerModuleInfo (moduleName="entry", single module)
 *      - For each Activity in manifest: InsertAbilitiesInfo(key, innerAbilityInfo)
 *        where key = "bundleName.moduleName.abilityName" and innerAbilityInfo
 *        has name + bundleName + moduleName + type=PAGE + codePath set
 *   5. Register via BundleDataMgr::AddInnerBundleInfo
 *   6. Return ERR_OK
 *
 * What this DOES NOT do (deliberately minimal):
 *   - Native lib extraction
 *   - dex2oat optimization
 *   - Signature verification
 *   - Service/Provider/Receiver registration (only Activity → PAGE Ability)
 *   - Permission mapping (android.permission.* → ohos.permission.*)
 *   - Skills / intent-filter mapping into BMS Skill objects
 *
 * Authored: 2026-04-11 per user feedback
 *   "要启动编译" for ProcessApkInstall AXML 解析
 */

#ifdef OH_ADAPTER_ANDROID

#include <cerrno>
#include <cstddef>
#include <cstdio>
#include <cstring>
#include <string>
#include <sys/stat.h>
#include <sys/types.h>

#include "app_log_wrapper.h"
#include "appexecfwk_errors.h"
#include "bundle_data_mgr.h"
#include "bundle_mgr_service.h"
#include "inner_bundle_info.h"
#include "install_param.h"
#include "application_info.h"

// Adapter AXML parser (defined in //adapter/framework/package-manager/jni/)
#include "apk_manifest_parser.h"

namespace OHOS {
namespace AppExecFwk {

namespace {

constexpr const char* APK_INSTALL_DIR_PREFIX = "/data/app/android";
constexpr const char* ANDROID_MODULE_NAME = "entry";  // Android has no module concept

bool MakeDirRecursive(const std::string& path) {
    if (path.empty()) return false;
    std::string cur;
    for (size_t i = 0; i < path.size(); ++i) {
        cur += path[i];
        if (path[i] == '/' || i + 1 == path.size()) {
            if (cur == "/") continue;
            if (mkdir(cur.c_str(), 0755) != 0 && errno != EEXIST) {
                APP_LOGE("ProcessApkInstall: mkdir %{public}s failed: %{public}s",
                         cur.c_str(), strerror(errno));
                return false;
            }
        }
    }
    return true;
}

bool CopyFileBinary(const std::string& src, const std::string& dst) {
    FILE* in = std::fopen(src.c_str(), "rb");
    if (in == nullptr) {
        APP_LOGE("ProcessApkInstall: fopen %{public}s read failed: %{public}s",
                 src.c_str(), std::strerror(errno));
        return false;
    }
    FILE* out = std::fopen(dst.c_str(), "wb");
    if (out == nullptr) {
        APP_LOGE("ProcessApkInstall: fopen %{public}s write failed: %{public}s",
                 dst.c_str(), std::strerror(errno));
        std::fclose(in);
        return false;
    }
    constexpr size_t kBufSize = 64 * 1024;
    char buf[kBufSize];
    bool ok = true;
    while (true) {
        size_t n = std::fread(buf, 1, kBufSize, in);
        if (n == 0) {
            if (std::ferror(in)) {
                APP_LOGE("ProcessApkInstall: fread error on %{public}s", src.c_str());
                ok = false;
            }
            break;
        }
        if (std::fwrite(buf, 1, n, out) != n) {
            APP_LOGE("ProcessApkInstall: fwrite short on %{public}s", dst.c_str());
            ok = false;
            break;
        }
    }
    std::fclose(in);
    std::fclose(out);
    return ok;
}

LaunchMode MapAndroidLaunchMode(int32_t androidMode) {
    // Android: 0=standard, 1=singleTop, 2=singleTask, 3=singleInstance
    // OH:      SINGLETON / STANDARD / SPECIFIED / MULTITON
    switch (androidMode) {
        case 2: case 3: return LaunchMode::SINGLETON;   // singleTask / singleInstance → SINGLETON
        case 1:         return LaunchMode::STANDARD;    // singleTop  ≈ STANDARD
        case 0:
        default:        return LaunchMode::STANDARD;    // standard → STANDARD
    }
}

// Build one InnerAbilityInfo for an Android Activity
InnerAbilityInfo BuildInnerAbilityInfo(
        const oh_adapter::ApkManifestParser::ActivityData& activity,
        const std::string& packageName,
        const std::string& installDir) {
    InnerAbilityInfo info;
    info.name = activity.name;          // fully-qualified Java class (e.g. com.example.helloworld.MainActivity)
    info.bundleName = packageName;
    info.moduleName = ANDROID_MODULE_NAME;
    info.applicationName = packageName;
    info.codePath = installDir;
    info.type = AbilityType::PAGE;
    info.visible = activity.exported;
    info.enabled = true;
    info.launchMode = MapAndroidLaunchMode(activity.launchMode);
    info.srcEntrance = activity.name;   // Android Activity is the entry point
    info.label = activity.label.empty() ? activity.name : activity.label;
    return info;
}

}  // anonymous namespace

// =============================================================================
// ProcessApkInstall — invoked by BundleInstaller::Install() when the input
// path ends with .apk (see bundle_installer.cpp.patch).
// =============================================================================
ErrCode ProcessApkInstall(const std::string& apkPath,
                          const InstallParam& /*installParam*/) {
    APP_LOGI("ProcessApkInstall: deploying %{public}s", apkPath.c_str());

    // 1. Parse AndroidManifest.xml using real AXML parser (libandroidfw)
    oh_adapter::ApkManifestParser::ManifestData manifest;
    if (!oh_adapter::ApkManifestParser::Parse(apkPath, manifest)) {
        APP_LOGE("ProcessApkInstall: AXML parse failed for %{public}s",
                 apkPath.c_str());
        return ERR_APPEXECFWK_INSTALL_INVALID_BUNDLE_FILE;
    }
    if (manifest.packageName.empty()) {
        APP_LOGE("ProcessApkInstall: manifest has empty packageName");
        return ERR_APPEXECFWK_INSTALL_INVALID_BUNDLE_FILE;
    }
    APP_LOGI("ProcessApkInstall: parsed packageName=%{public}s "
             "versionCode=%{public}d activities=%{public}zu",
             manifest.packageName.c_str(),
             manifest.versionCode,
             manifest.activities.size());

    const std::string& bundleName = manifest.packageName;

    // 2. Create install dir
    std::string installDir = std::string(APK_INSTALL_DIR_PREFIX) + "/" + bundleName;
    if (!MakeDirRecursive(installDir)) {
        return ERR_APPEXECFWK_INSTALLD_CREATE_DIR_FAILED;
    }

    // 3. Copy APK
    std::string baseApkPath = installDir + "/base.apk";
    if (!CopyFileBinary(apkPath, baseApkPath)) {
        return ERR_APPEXECFWK_INSTALL_COPY_HAP_FAILED;
    }
    APP_LOGI("ProcessApkInstall: copied to %{public}s", baseApkPath.c_str());

    // 4. Build InnerBundleInfo with ApplicationInfo + activities
    InnerBundleInfo info;
    ApplicationInfo appInfo;
    appInfo.bundleName = bundleName;
    appInfo.name = bundleName;
    appInfo.codePath = installDir;
    appInfo.bundleType = BundleType::APP_ANDROID;
    appInfo.debug = manifest.debuggable;
    appInfo.label = manifest.appLabel.empty() ? bundleName : manifest.appLabel;
    appInfo.minCompatibleVersionCode = manifest.minSdkVersion;
    appInfo.apiTargetVersion = manifest.targetSdkVersion;
    info.SetBaseApplicationInfo(appInfo);

    // 5. Insert each activity as a PAGE ability
    size_t insertedActivities = 0;
    for (const auto& activity : manifest.activities) {
        if (activity.name.empty()) continue;
        InnerAbilityInfo innerAbility = BuildInnerAbilityInfo(activity, bundleName, installDir);
        // Key format: bundleName.moduleName.abilityName (per InsertAbilitiesInfo contract)
        std::string key = bundleName + "." + ANDROID_MODULE_NAME + "." + activity.name;
        info.InsertAbilitiesInfo(key, innerAbility);
        ++insertedActivities;
    }
    APP_LOGI("ProcessApkInstall: inserted %{public}zu activities", insertedActivities);

    // 6. Register in BMS
    auto bms = DelayedSingleton<BundleMgrService>::GetInstance();
    if (bms == nullptr) {
        APP_LOGE("ProcessApkInstall: BundleMgrService not available");
        return ERR_APPEXECFWK_INSTALLD_GET_PROXY_ERROR;
    }
    auto dataMgr = bms->GetDataMgr();
    if (dataMgr == nullptr) {
        APP_LOGE("ProcessApkInstall: BundleDataMgr not available");
        return ERR_APPEXECFWK_INSTALLD_GET_PROXY_ERROR;
    }

    bool added = dataMgr->AddInnerBundleInfo(bundleName, info);
    if (!added) {
        APP_LOGW("ProcessApkInstall: AddInnerBundleInfo returned false "
                 "(bundle may already exist — update path not implemented)");
    }

    APP_LOGI("ProcessApkInstall: success bundleName=%{public}s base=%{public}s "
             "activities=%{public}zu",
             bundleName.c_str(), baseApkPath.c_str(), insertedActivities);
    return ERR_OK;
}

}  // namespace AppExecFwk
}  // namespace OHOS

#endif  // OH_ADAPTER_ANDROID
