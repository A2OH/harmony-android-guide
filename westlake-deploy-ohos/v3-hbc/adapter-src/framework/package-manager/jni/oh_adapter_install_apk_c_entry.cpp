/*
 * oh_adapter_install_apk_c_entry.cpp
 *
 * Stable C ABI entry point that libbms (Route C) dlsym()s after detecting a
 * .apk path in BundleInstaller::Install. Bridges to ApkInstaller::DeployApk.
 *
 * Why a separate translation unit:
 *   The C++ class methods on ApkInstaller / ApkManifestParser are mangled
 *   (clang Itanium ABI). Across toolchain refreshes the mangled names can
 *   shift; libbms can't safely dlsym a mangled name. This one C entry stays
 *   stable forever:
 *     extern "C" int oh_adapter_install_apk(const char* apkPath, int userId);
 *
 * Why not in apk_installer.cpp directly:
 *   Keeps the C bridge surface clean and isolated from the C++ implementation
 *   so future swaps of the implementation class don't risk touching ABI.
 */

#include "apk_installer.h"
#include "apk_manifest_parser.h"

#include <hilog/log.h>
#include <nlohmann/json.hpp>
#include <cstring>
#include <string>

#undef LOG_DOMAIN
#define LOG_DOMAIN 0xD001150
#undef LOG_TAG
#define LOG_TAG "OH_AdapterApkEntry"

#define ENTRY_LOGI(...) HILOG_INFO(LOG_CORE, __VA_ARGS__)
#define ENTRY_LOGE(...) HILOG_ERROR(LOG_CORE, __VA_ARGS__)

extern "C" int oh_adapter_install_apk(const char* apkPath, int userId)
{
    if (apkPath == nullptr || apkPath[0] == '\0') {
        ENTRY_LOGE("oh_adapter_install_apk: null/empty apkPath");
        return -1;
    }
    std::string path(apkPath);
    ENTRY_LOGI("oh_adapter_install_apk: path=%{public}s userId=%{public}d",
        path.c_str(), userId);

    // 1. Parse AndroidManifest.xml from the APK to obtain packageName.
    oh_adapter::ApkManifestParser::ManifestData manifest;
    if (!oh_adapter::ApkManifestParser::Parse(path, manifest)) {
        ENTRY_LOGE("oh_adapter_install_apk: ApkManifestParser::Parse failed for %{public}s",
            path.c_str());
        return -2;
    }
    if (manifest.packageName.empty()) {
        ENTRY_LOGE("oh_adapter_install_apk: empty packageName from manifest");
        return -3;
    }

    // 2. Assign uid/gid in the Android shared-app range. Use a deterministic
    //    derivation from packageName hash for now (stable per package). BMS
    //    proper assigns via app provisioning; the adapter doesn't have that
    //    pipe yet, so this is a placeholder until UID allocation is wired
    //    through OH App Service Framework.
    auto hashUid = [](const std::string& s) -> int32_t {
        // Stable hash → range [10000, 19999] (Android third-party app range).
        uint32_t h = 5381;
        for (char c : s) {
            h = ((h << 5) + h) + static_cast<unsigned char>(c);
        }
        return 10000 + static_cast<int32_t>(h % 10000);
    };
    int32_t uid = hashUid(manifest.packageName);
    int32_t gid = uid;

    // 3. DeployApk: copies APK, extracts native libs, runs dex2oat,
    //    creates /data/app/android/{pkg}/ tree.
    oh_adapter::ApkInstaller installer;
    auto result = installer.DeployApk(path, manifest.packageName, uid, gid);
    if (!result.success) {
        ENTRY_LOGE("oh_adapter_install_apk: DeployApk failed: %{public}s",
            result.errorMsg.c_str());
        return -4;
    }

    ENTRY_LOGI("oh_adapter_install_apk: deployed package=%{public}s uid=%{public}d apk=%{public}s",
        manifest.packageName.c_str(), uid, result.installedApkPath.c_str());
    (void)userId;  // userId routing (multi-user) is a follow-up.
    return 0;
}

// ============================================================================
// V2 entry: same as v1 but writes manifest+deploy info as JSON to caller buffer
// for libbms to use when registering the bundle in BMS BundleDataMgr.
// ============================================================================
//
// outJsonBuf must be a writable buffer of at least 8KB. On success, JSON is
// written NUL-terminated. Returns the same status codes as v1 (0=success,
// negative=failure).
//
// JSON schema (minimum viable for BMS to register a launchable app):
// {
//   "bundleName": "com.example.helloworld",
//   "versionCode": 1,
//   "versionName": "1.0",
//   "appLabel": "Hello World",
//   "appClassName": "com.example.helloworld.HelloApp",
//   "deployedApkPath": "/data/app/android/<pkg>/base.apk",
//   "deployedDir": "/data/app/android/<pkg>",
//   "uid": 13736,
//   "gid": 13736,
//   "minSdkVersion": 21,
//   "targetSdkVersion": 33,
//   "abilities": [
//     { "name":"com.example.helloworld.MainActivity",
//       "label":"...", "isMainAbility":true,
//       "actions":["android.intent.action.MAIN"],
//       "categories":["android.intent.category.LAUNCHER"] },
//     ...
//   ]
// }
extern "C" int oh_adapter_install_apk_with_manifest(
    const char* apkPath, int userId,
    char* outJsonBuf, int outJsonBufSize)
{
    if (apkPath == nullptr || apkPath[0] == '\0') {
        ENTRY_LOGE("oh_adapter_install_apk_with_manifest: null/empty apkPath");
        return -1;
    }
    std::string path(apkPath);
    ENTRY_LOGI("oh_adapter_install_apk_with_manifest: path=%{public}s userId=%{public}d",
        path.c_str(), userId);

    oh_adapter::ApkManifestParser::ManifestData manifest;
    if (!oh_adapter::ApkManifestParser::Parse(path, manifest)) {
        ENTRY_LOGE("ApkManifestParser::Parse failed for %{public}s", path.c_str());
        return -2;
    }
    if (manifest.packageName.empty()) {
        ENTRY_LOGE("empty packageName from manifest");
        return -3;
    }

    auto hashUid = [](const std::string& s) -> int32_t {
        uint32_t h = 5381;
        for (char c : s) h = ((h << 5) + h) + static_cast<unsigned char>(c);
        return 10000 + static_cast<int32_t>(h % 10000);
    };
    int32_t uid = hashUid(manifest.packageName);
    int32_t gid = uid;

    // Deployment of base.apk + native libs + resources HAP is done by libbms
    // register patch via InstalldClient IPC (installd runs in installs SELinux
    // domain which has write perms on data_app_el*_file). This C entry only
    // parses the APK manifest and reports paths in the JSON that libbms then
    // uses to drive InstalldClient::{CreateBundleDir, Mkdir, CopyFile,
    // ExtractFiles(APK_RESOURCES_HAP)}.
    //
    // Moved out of BMS (foundation domain) because foundation.te grants no
    // write perms on data_app_el1_file / data_app_file — the previous
    // design relied on setenforce 0 which was never a real solution.
    std::string bundleRoot = std::string("/data/app/el1/bundle/public/") + manifest.packageName;
    std::string deployedDir = bundleRoot + "/android";
    std::string deployedApk = deployedDir + "/base.apk";

    // Select primary ABI from the source APK (read-only, safe in foundation).
    std::string primaryAbi = oh_adapter::ApkInstaller::SelectPrimaryAbi(path);

    // Build JSON for libbms to register InnerBundleInfo.
    if (outJsonBuf == nullptr || outJsonBufSize <= 0) {
        ENTRY_LOGI("Skipping JSON serialization (outJsonBuf nullptr)");
        (void)userId;
        return 0;
    }

    nlohmann::json j;
    j["bundleName"]       = manifest.packageName;
    j["versionCode"]      = manifest.versionCode;
    j["versionName"]      = manifest.versionName;
    j["appLabel"]         = manifest.appLabel.empty() ? manifest.packageName : manifest.appLabel;
    j["appClassName"]     = manifest.appClassName;
    j["srcApkPath"]       = path;            // caller uses this for InstalldClient::CopyFile src
    j["deployedApkPath"]  = deployedApk;
    j["deployedDir"]      = deployedDir;
    j["bundleRoot"]       = bundleRoot;
    j["entryHapPath"]     = bundleRoot + "/entry.hap";  // synth via ExtractFiles(APK_RESOURCES_HAP)
    j["primaryAbi"]       = primaryAbi;
    j["uid"]              = uid;
    j["gid"]              = gid;
    j["minSdkVersion"]    = manifest.minSdkVersion;
    j["targetSdkVersion"] = manifest.targetSdkVersion;
    j["debuggable"]       = manifest.debuggable;

    nlohmann::json abilitiesJson = nlohmann::json::array();
    for (const auto& ability : manifest.activities) {
        nlohmann::json a;
        a["name"]          = ability.name;
        a["label"]         = ability.label.empty() ? manifest.appLabel : ability.label;
        a["launchMode"]    = ability.launchMode;
        a["screenOrientation"] = ability.screenOrientation;
        a["exported"]      = ability.exported;
        nlohmann::json actions = nlohmann::json::array();
        nlohmann::json categories = nlohmann::json::array();
        bool isMain = false;
        for (const auto& filter : ability.intentFilters) {
            for (const auto& act : filter.actions) {
                actions.push_back(act);
                if (act == "android.intent.action.MAIN") isMain = true;
            }
            for (const auto& cat : filter.categories) {
                categories.push_back(cat);
            }
        }
        a["actions"]       = actions;
        a["categories"]    = categories;
        a["isMainAbility"] = isMain;
        abilitiesJson.push_back(a);
    }
    j["abilities"] = abilitiesJson;

    std::string jsonStr = j.dump();
    if (static_cast<int>(jsonStr.size()) + 1 > outJsonBufSize) {
        ENTRY_LOGE("JSON output (%{public}zu bytes) exceeds buffer (%{public}d bytes)",
            jsonStr.size(), outJsonBufSize);
        return -5;
    }
    memcpy(outJsonBuf, jsonStr.c_str(), jsonStr.size() + 1);
    ENTRY_LOGI("oh_adapter_install_apk_with_manifest: deployed pkg=%{public}s uid=%{public}d, "
               "json=%{public}zu bytes, abilities=%{public}zu",
        manifest.packageName.c_str(), uid, jsonStr.size(), manifest.activities.size());
    return 0;
}

// ============================================================================
// V3 entry — installd-side resources HAP synthesis (no BMS fs writes required)
// ============================================================================
//
// Called from installd (installs SELinux domain) via InstalldOperator::
// BuildApkResourcesHap after BMS invokes InstalldClient::ExtractFiles with
// extractFileType=APK_RESOURCES_HAP. Runs in installs process context which
// has SELinux write perms on data_app_el*_file and data_app_file, so the
// synthesized HAP can land directly at the caller-specified outHapPath.
//
// Pure transform: APK launcher icon → template merge → output HAP at outHapPath.
// Returns 0 on success, non-zero on failure.
extern "C" int oh_adapter_build_resources_hap(const char* apkPath, const char* outHapPath)
{
    if (apkPath == nullptr || apkPath[0] == '\0') {
        ENTRY_LOGE("oh_adapter_build_resources_hap: null/empty apkPath");
        return -1;
    }
    if (outHapPath == nullptr || outHapPath[0] == '\0') {
        ENTRY_LOGE("oh_adapter_build_resources_hap: null/empty outHapPath");
        return -2;
    }
    ENTRY_LOGI("oh_adapter_build_resources_hap: apk=%{public}s out=%{public}s",
        apkPath, outHapPath);
    if (!oh_adapter::ApkInstaller::ExtractAndPackResourceHap(
            std::string(apkPath), std::string(outHapPath))) {
        ENTRY_LOGE("oh_adapter_build_resources_hap: ExtractAndPackResourceHap failed");
        return -3;
    }
    return 0;
}
