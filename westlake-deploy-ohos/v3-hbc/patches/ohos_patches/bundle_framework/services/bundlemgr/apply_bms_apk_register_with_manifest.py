#!/usr/bin/env python3
"""apply_bms_apk_register_with_manifest.py — extend ProcessBundleInstall .apk
dispatch to also register the bundle in BMS BundleDataMgr.

Background:
    apply_bms_apk_dispatch_processinstall.py made bm install -p X.apk dispatch
    to libapk_installer.so + return ERR_OK. Files were deployed but BMS never
    learned about the bundle, so aa start / launcher couldn't find it
    (BundleDataMgr.QueryAbilityInfo returns "not exist").

Action:
    Replace the dispatch block injected by apply_bms_apk_dispatch_processinstall
    with an extended version that:
      1. dlsym oh_adapter_install_apk_with_manifest (new C entry that returns
         JSON manifest data via output buffer)
      2. Parse JSON
      3. Construct minimum-viable InnerBundleInfo (BundleInfo + ApplicationInfo
         + InnerModuleInfo "entry" + AbilityInfo[] for each <activity>)
      4. dataMgr_->AddInnerBundleInfo(bundleName, innerBundleInfo)

The minimum InnerBundleInfo is what's needed for:
  - launcher to enumerate the package (BundleDataMgr.GetBundleInfos)
  - aa start -b <pkg> -a <activity> to resolve the ability
    (ExplicitQueryAbility match)

Requires: apply_bms_apk_dispatch_processinstall.py applied first (we replace
its anchor block).

Idempotent.
"""
import argparse
import os
import sys

DEFAULT = (
    "/home/HanBingChen/oh/foundation/bundlemanager/bundle_framework/"
    "services/bundlemgr/src/base_bundle_installer.cpp"
)

# Marker for THIS patch (different from the dispatch-only patch's marker).
# v2 (2026-04-21): move install target from /data/app/android/<pkg>/ to
#   /data/app/el1/bundle/public/<pkg>/android/, drive all fs writes via
#   InstalldClient IPC, synthesize resources HAP via
#   InstalldClient::ExtractFiles(APK_RESOURCES_HAP), add SaveInnerBundleInfo
#   call (fixes R.3.8 reboot loss).
# v3 (2026-05-01): call AccessTokenKit::AllocHapToken before AddInnerBundleInfo so
#   appInfo.accessTokenId / InnerBundleUserInfo.accessTokenId are real (non-zero)
#   HAP tokens. Without this AMS stored 0, child appspawn-x's SetSelfTokenID(0)
#   was rejected by kernel, JudgeSelfCalled mismatched parent's inherited token,
#   AbilityTransitionDone returned CHECK_PERMISSION_FAILED (rc=2097177).
PATCH_MARKER = "// adapter project: .apk register with manifest v3"
PATCH_MARKER_V2 = "// adapter project: .apk register with manifest v2"
PATCH_MARKER_V1 = "// adapter project: .apk register with manifest"

# This is the EXACT block injected by apply_bms_apk_dispatch_processinstall.py
# (must match verbatim including whitespace).
ANCHOR_OLD = (
    "#ifdef OH_ADAPTER_ANDROID\n"
    "    // adapter project: .apk dispatch before HAP signature pipeline.\n"
    "    // bundlePaths now holds the resolved real paths of files inside tempDir_;\n"
    "    // for bm install -p X.apk it is [.../OHAdapterHelloWorld.apk]. Route .apk\n"
    "    // batches to libapk_installer.so + skip CheckMultipleHapsSignInfo / parse.\n"
    "    {\n"
    "        bool anyApk = false;\n"
    "        for (const auto &p : bundlePaths) {\n"
    "            if (p.size() > 4 && p.compare(p.size() - 4, 4, \".apk\") == 0) {\n"
    "                anyApk = true;\n"
    "                break;\n"
    "            }\n"
    "        }\n"
    "        if (anyApk) {\n"
    "            LOG_I(BMS_TAG_INSTALLER, \"adapter .apk path detected, dispatching\");\n"
    "            void* handle = dlopen(\"libapk_installer.so\", RTLD_NOW | RTLD_GLOBAL);\n"
    "            using ApkFn = int (*)(const char*, int);\n"
    "            ApkFn fn = handle ? reinterpret_cast<ApkFn>(\n"
    "                dlsym(handle, \"oh_adapter_install_apk\")) : nullptr;\n"
    "            if (fn == nullptr) {\n"
    "                LOG_E(BMS_TAG_INSTALLER, \"oh_adapter_install_apk symbol unavailable\");\n"
    "                return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "            }\n"
    "            for (const auto &p : bundlePaths) {\n"
    "                if (!(p.size() > 4 && p.compare(p.size() - 4, 4, \".apk\") == 0)) continue;\n"
    "                int r = fn(p.c_str(), installParam.userId);\n"
    "                if (r != 0) {\n"
    "                    LOG_E(BMS_TAG_INSTALLER, \"oh_adapter_install_apk failed: %{public}d\", r);\n"
    "                    return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "                }\n"
    "            }\n"
    "            LOG_I(BMS_TAG_INSTALLER, \"adapter .apk install path completed\");\n"
    "            return ERR_OK;\n"
    "        }\n"
    "    }\n"
    "#endif\n"
)

ANCHOR_NEW = (
    "#ifdef OH_ADAPTER_ANDROID\n"
    "    // adapter project: .apk register with manifest v3\n"
    "    // 1. dispatch to libapk_installer.so — manifest parse only (no fs writes)\n"
    "    // 2. parse returned JSON manifest data\n"
    "    // 3. route file deployment via InstalldClient IPC (installs domain)\n"
    "    // 4. construct minimum-viable InnerBundleInfo\n"
    "    // 5. allocate real HAP AccessTokenID via AccessTokenKit::AllocHapToken\n"
    "    // 6. dataMgr->AddInnerBundleInfo + SaveInnerBundleInfo (reboot-safe)\n"
    "    {\n"
    "        bool anyApk = false;\n"
    "        for (const auto &p : bundlePaths) {\n"
    "            if (p.size() > 4 && p.compare(p.size() - 4, 4, \".apk\") == 0) {\n"
    "                anyApk = true;\n"
    "                break;\n"
    "            }\n"
    "        }\n"
    "        if (anyApk) {\n"
    "            LOG_I(BMS_TAG_INSTALLER, \"adapter .apk path detected, dispatching+register v2\");\n"
    "            void* handle = dlopen(\"libapk_installer.so\", RTLD_NOW | RTLD_GLOBAL);\n"
    "            using ApkFn2 = int (*)(const char*, int, char*, int);\n"
    "            ApkFn2 fn = handle ? reinterpret_cast<ApkFn2>(\n"
    "                dlsym(handle, \"oh_adapter_install_apk_with_manifest\")) : nullptr;\n"
    "            if (fn == nullptr) {\n"
    "                LOG_E(BMS_TAG_INSTALLER, \"oh_adapter_install_apk_with_manifest unavailable\");\n"
    "                return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "            }\n"
    "            constexpr int kJsonBufSize = 65536;\n"
    "            std::vector<char> jsonBuf(kJsonBufSize, 0);\n"
    "            for (const auto &p : bundlePaths) {\n"
    "                if (!(p.size() > 4 && p.compare(p.size() - 4, 4, \".apk\") == 0)) continue;\n"
    "                int r = fn(p.c_str(), installParam.userId, jsonBuf.data(), kJsonBufSize);\n"
    "                if (r != 0) {\n"
    "                    LOG_E(BMS_TAG_INSTALLER, \"oh_adapter_install_apk_with_manifest failed: %{public}d\", r);\n"
    "                    return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "                }\n"
    "                // Parse manifest JSON and register bundle.\n"
    "                try {\n"
    "                    auto j = nlohmann::json::parse(std::string(jsonBuf.data()));\n"
    "                    std::string pkg = j.value(\"bundleName\", std::string());\n"
    "                    if (pkg.empty()) {\n"
    "                        LOG_E(BMS_TAG_INSTALLER, \"manifest JSON missing bundleName\");\n"
    "                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "                    }\n"
    "                    // adapter v2: route all fs writes via InstalldClient IPC (installs\n"
    "                    // domain has write perms on data_app_el*_file; foundation does not).\n"
    "                    // Previous design leaked direct fs writes from foundation and\n"
    "                    // required setenforce 0 to actually work.\n"
    "                    std::string bundleRoot   = j.value(\"bundleRoot\", std::string(\"/data/app/el1/bundle/public/\") + pkg);\n"
    "                    std::string deployedDir  = j.value(\"deployedDir\", bundleRoot + \"/android\");\n"
    "                    std::string deployedApk  = j.value(\"deployedApkPath\", deployedDir + \"/base.apk\");\n"
    "                    std::string entryHapPath = j.value(\"entryHapPath\", bundleRoot + \"/entry.hap\");\n"
    "                    std::string srcApkPath   = j.value(\"srcApkPath\", p);\n"
    "                    auto installd = InstalldClient::GetInstance();\n"
    "                    if (installd == nullptr) {\n"
    "                        LOG_E(BMS_TAG_INSTALLER, \"InstalldClient unavailable — cannot deploy APK\");\n"
    "                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "                    }\n"
    "                    // 1. Bundle root dir (data_app_el1_file label — CreateBundleDir\n"
    "                    //    removes if exists, creates fresh. foundation-readable.)\n"
    "                    ErrCode ec = installd->CreateBundleDir(bundleRoot);\n"
    "                    if (ec != ERR_OK) {\n"
    "                        LOG_E(BMS_TAG_INSTALLER, \"CreateBundleDir(%{public}s) failed: %{public}d\", bundleRoot.c_str(), ec);\n"
    "                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "                    }\n"
    "                    // 2. 'android/' subdir — holds adapter-specific files (base.apk, lib/, oat/).\n"
    "                    //    Owned root:root so app process can't tamper.\n"
    "                    ec = installd->Mkdir(deployedDir, 0755, 0, 0);\n"
    "                    if (ec != ERR_OK) {\n"
    "                        LOG_E(BMS_TAG_INSTALLER, \"Mkdir(%{public}s) failed: %{public}d\", deployedDir.c_str(), ec);\n"
    "                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "                    }\n"
    "                    // 3. base.apk — copy from stream_install staging to persistent\n"
    "                    //    location via installs-domain CopyFile.\n"
    "                    ec = installd->CopyFile(srcApkPath, deployedApk, std::string());\n"
    "                    if (ec != ERR_OK) {\n"
    "                        LOG_E(BMS_TAG_INSTALLER, \"CopyFile(%{public}s -> %{public}s) failed: %{public}d\",\n"
    "                            srcApkPath.c_str(), deployedApk.c_str(), ec);\n"
    "                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "                    }\n"
    "                    // 4. Synthesize OH resources HAP directly at entry.hap location —\n"
    "                    //    installd loads libapk_installer.so (installs domain) and calls\n"
    "                    //    oh_adapter_build_resources_hap(apk, outPath). No fs work in BMS.\n"
    "                    ExtractParam ep;\n"
    "                    ep.srcPath = srcApkPath;\n"
    "                    ep.targetPath = entryHapPath;\n"
    "                    ep.extractFileType = ExtractFileType::APK_RESOURCES_HAP;\n"
    "                    ec = installd->ExtractFiles(ep);\n"
    "                    if (ec != ERR_OK) {\n"
    "                        LOG_W(BMS_TAG_INSTALLER, \"ExtractFiles(APK_RESOURCES_HAP) %{public}s -> %{public}s failed: %{public}d — launcher icon will fall back\",\n"
    "                            srcApkPath.c_str(), entryHapPath.c_str(), ec);\n"
    "                    } else {\n"
    "                        LOG_I(BMS_TAG_INSTALLER, \"ExtractFiles(APK_RESOURCES_HAP) OK %{public}s\", entryHapPath.c_str());\n"
    "                    }\n"
    "\n"
    "                    InnerBundleInfo innerBundleInfo;\n"
    "                    BundleInfo bundleInfo;\n"
    "                    bundleInfo.name = pkg;\n"
    "                    bundleInfo.vendor = \"android\";\n"
    "                    bundleInfo.versionCode = j.value(\"versionCode\", 0);\n"
    "                    bundleInfo.versionName = j.value(\"versionName\", std::string());\n"
    "                    bundleInfo.minSdkVersion = j.value(\"minSdkVersion\", 0);\n"
    "                    bundleInfo.targetVersion = j.value(\"targetSdkVersion\", 0);\n"
    "                    innerBundleInfo.SetBaseBundleInfo(bundleInfo);\n"
    "\n"
    "                    ApplicationInfo appInfo;\n"
    "                    appInfo.name = pkg;\n"
    "                    appInfo.bundleName = pkg;\n"
    "                    appInfo.label = j.value(\"appLabel\", pkg);\n"
    "                    appInfo.codePath = j.value(\"deployedDir\", std::string());\n"
    "                    appInfo.dataDir = std::string(\"/data/app/el2/0/android/\") + pkg;\n"
    "                    appInfo.uid = j.value(\"uid\", 10000);\n"
    "                    appInfo.debug = j.value(\"debuggable\", false);\n"
    "                    appInfo.deviceId = \"PHONE-001\";\n"
    "                    // Icon path: launcher requires applicationInfo.iconPath OR\n"
    "                    // applicationInfo.iconResource non-empty to render the icon.\n"
    "                    // ApkInstaller writes a fallback icon.png to deployedDir at\n"
    "                    // install time (extracts from APK or uses generic placeholder).\n"
    "                    appInfo.iconPath = j.value(\"deployedDir\", std::string()) + \"/icon.png\";\n"
    "                    appInfo.icon = appInfo.iconPath;\n"
    "                    // adapter project: app-level iconId/labelId from synthesized\n"
    "                    // resources.index (restool-generated AppScope-level entries):\n"
    "                    //   media:app_icon  -> 0x01000001\n"
    "                    //   string:app_name -> 0x01000000\n"
    "                    appInfo.iconId  = 0x01000001;\n"
    "                    appInfo.labelId = 0x01000000;\n"
    "                    // Mark as Android app so AMS routes spawn requests through\n"
    "                    // the AndroidSpawnClient branch (-> appspawn-x) instead of\n"
    "                    // standard OH appspawn (which expects HAP + ArkTS runtime).\n"
    "                    appInfo.bundleType = BundleType::APP_ANDROID;\n"
    "                    // adapter v3 (2026-05-01): allocate real HAP AccessTokenID at install\n"
    "                    // time. Without this, BMS stored accessTokenId=0 -> AMS retrieved 0 ->\n"
    "                    // appspawn-x's SetSelfTokenID(0) was rejected by kernel -> child kept\n"
    "                    // parent's inherited tokenID -> JudgeSelfCalled mismatch -> AMS\n"
    "                    // CHECK_PERMISSION_FAILED (2097177) on AbilityTransitionDone.\n"
    "                    // We allocate a fresh APL_NORMAL token; permList/permStateList stay\n"
    "                    // empty (Android permissions are mapped at runtime via\n"
    "                    // PermissionMapper, not at install time).\n"
    "                    Security::AccessToken::HapInfoParams hapInfo;\n"
    "                    hapInfo.userID = 100;\n"
    "                    hapInfo.bundleName = pkg;\n"
    "                    hapInfo.instIndex = 0;\n"
    "                    hapInfo.dlpType = 0;\n"
    "                    hapInfo.appIDDesc = pkg + \"_apk_adapter\";\n"
    "                    hapInfo.apiVersion = 14;\n"
    "                    hapInfo.isSystemApp = false;\n"
    "                    hapInfo.appDistributionType = \"none\";\n"
    "                    Security::AccessToken::HapPolicyParams hapPolicy;\n"
    "                    hapPolicy.apl = Security::AccessToken::APL_NORMAL;\n"
    "                    hapPolicy.domain = \"android.adapter.domain\";\n"
    "                    Security::AccessToken::AccessTokenIDEx allocTokenIdEx =\n"
    "                        Security::AccessToken::AccessTokenKit::AllocHapToken(hapInfo, hapPolicy);\n"
    "                    if (allocTokenIdEx.tokenIdExStruct.tokenID == 0) {\n"
    "                        LOG_E(BMS_TAG_INSTALLER,\n"
    "                            \"AllocHapToken returned 0 for %{public}s — token allocation failed\",\n"
    "                            pkg.c_str());\n"
    "                        return ERR_APPEXECFWK_INSTALL_GRANT_REQUEST_PERMISSIONS_FAILED;\n"
    "                    }\n"
    "                    LOG_I(BMS_TAG_INSTALLER,\n"
    "                        \"AllocHapToken OK %{public}s tokenID=0x%{public}x\",\n"
    "                        pkg.c_str(),\n"
    "                        static_cast<uint32_t>(allocTokenIdEx.tokenIdExStruct.tokenID));\n"
    "                    appInfo.accessTokenId   = allocTokenIdEx.tokenIdExStruct.tokenID;\n"
    "                    appInfo.accessTokenIdEx = allocTokenIdEx.tokenIDEx;\n"
    "                    innerBundleInfo.SetBaseApplicationInfo(appInfo);\n"
    "                    // Override the bundleType field on InnerBundleInfo itself\n"
    "                    // (used by GetApplicationBundleType() which AMS reads to dispatch).\n"
    "                    innerBundleInfo.SetApplicationBundleType(BundleType::APP_ANDROID);\n"
    "\n"
    "                    InnerModuleInfo moduleInfo;\n"
    "                    moduleInfo.name = \"entry\";\n"
    "                    moduleInfo.modulePackage = \"entry\";\n"
    "                    moduleInfo.moduleName = \"entry\";\n"
    "                    moduleInfo.modulePath = j.value(\"deployedDir\", std::string());\n"
    "                    moduleInfo.isEntry = true;\n"
    "                    moduleInfo.installationFree = false;\n"
    "                    moduleInfo.distro.moduleType = \"entry\";\n"
    "                    moduleInfo.distro.installationFree = false;\n"
    "                    moduleInfo.distro.deliveryWithInstall = true;\n"
    "                    moduleInfo.distro.moduleName = \"entry\";\n"
    "                    // v2: entry.hap already synthesized by installd above via\n"
    "                    //     ExtractFiles(APK_RESOURCES_HAP). No BMS-side staging.\n"
    "                    moduleInfo.hapPath = entryHapPath;\n"
    "                    moduleInfo.moduleResPath = moduleInfo.hapPath;\n"
    "\n"
    "                    if (j.contains(\"abilities\") && j[\"abilities\"].is_array()) {\n"
    "                        for (const auto &ab : j[\"abilities\"]) {\n"
    "                            std::string abilityName = ab.value(\"name\", std::string());\n"
    "                            if (abilityName.empty()) continue;\n"
    "                            InnerAbilityInfo innerAbility;\n"
    "                            innerAbility.name = abilityName;\n"
    "                            innerAbility.bundleName = pkg;\n"
    "                            innerAbility.moduleName = \"entry\";\n"
    "                            innerAbility.package = \"entry\";\n"
    "                            innerAbility.applicationName = pkg;\n"
    "                            innerAbility.label = ab.value(\"label\", abilityName);\n"
    "                            innerAbility.launchMode = static_cast<LaunchMode>(\n"
    "                                ab.value(\"launchMode\", 0));\n"
    "                            // OH InnerAbilityInfo defaults: visible=false, enabled=false,\n"
    "                            // isLauncherAbility=false. All three need to be true for\n"
    "                            // launcher's QueryLauncherAbilityInfos to surface this ability.\n"
    "                            innerAbility.visible = true;\n"
    "                            innerAbility.enabled = true;\n"
    "                            innerAbility.isLauncherAbility = ab.value(\"isMainAbility\", false);\n"
    "                            innerAbility.kind = \"page\";\n"
    "                            innerAbility.type = AbilityType::PAGE;\n"
    "                            innerAbility.codePath = j.value(\"deployedDir\", std::string());\n"
    "                            innerAbility.deviceId = \"PHONE-001\";\n"
    "                            innerAbility.iconPath = j.value(\"deployedDir\", std::string()) + \"/icon.png\";\n"
    "                            // adapter project: iconId / labelId match the synthesized\n"
    "                            // resources.index inside entry.hap (restool-generated):\n"
    "                            //   media:icon  -> 0x01000005\n"
    "                            //   string:EntryAbility_label -> 0x01000003\n"
    "                            // OH launcher's getAppIconWithCache requires iconId > 0\n"
    "                            // and ResourceManager.getMediaBase64(iconId) succeeds.\n"
    "                            innerAbility.iconId  = 0x01000005;\n"
    "                            innerAbility.labelId = 0x01000003;\n"
    "                            innerAbility.hapPath = entryHapPath;\n"
    "                            innerAbility.resourcePath = innerAbility.hapPath;\n"
    "                            // Translate Android intent-filter to OH skills.\n"
    "                            // Crucially: when Android MAIN+LAUNCHER are present (isMainAbility=true)\n"
    "                            // we also add ohos.want.action.home so OH launcher's QueryLauncherAbilityInfos\n"
    "                            // (which filters by Want::ACTION_HOME = \"ohos.want.action.home\") finds this ability.\n"
    "                            Skill skill;\n"
    "                            if (ab.contains(\"actions\") && ab[\"actions\"].is_array()) {\n"
    "                                for (const auto &act : ab[\"actions\"]) {\n"
    "                                    skill.actions.push_back(act.get<std::string>());\n"
    "                                }\n"
    "                            }\n"
    "                            if (ab.contains(\"categories\") && ab[\"categories\"].is_array()) {\n"
    "                                for (const auto &cat : ab[\"categories\"]) {\n"
    "                                    skill.entities.push_back(cat.get<std::string>());\n"
    "                                }\n"
    "                            }\n"
    "                            if (ab.value(\"isMainAbility\", false)) {\n"
    "                                skill.actions.push_back(\"ohos.want.action.home\");\n"
    "                                skill.entities.push_back(\"entity.system.home\");\n"
    "                            }\n"
    "                            innerAbility.skills.push_back(skill);\n"
    "                            std::string key = pkg + \".entry.\" + abilityName;\n"
    "                            innerBundleInfo.InsertAbilitiesInfo(key, innerAbility);\n"
    "                            if (ab.value(\"isMainAbility\", false)) {\n"
    "                                moduleInfo.mainAbility = abilityName;\n"
    "                                moduleInfo.entryAbilityKey = key;\n"
    "                            }\n"
    "                        }\n"
    "                    }\n"
    "                    innerBundleInfo.InsertInnerModuleInfo(\"entry\", moduleInfo);\n"
    "\n"
    "                    auto dataMgr = DelayedSingleton<BundleMgrService>::GetInstance()->GetDataMgr();\n"
    "                    if (dataMgr == nullptr) {\n"
    "                        LOG_E(BMS_TAG_INSTALLER, \"BundleDataMgr unavailable for register\");\n"
    "                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "                    }\n"
    "                    // BMS requires installStates_[bundleName]==INSTALL_START before\n"
    "                    // AddInnerBundleInfo will store the InnerBundleInfo.\n"
    "                    if (!dataMgr->UpdateBundleInstallState(pkg, InstallState::INSTALL_START)) {\n"
    "                        LOG_E(BMS_TAG_INSTALLER, \"UpdateBundleInstallState INSTALL_START failed for %{public}s\", pkg.c_str());\n"
    "                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "                    }\n"
    "                    // Register InnerBundleUserInfo for BOTH userId 0 (system) and 100\n"
    "                    // (current user). Launcher uses 100; AMS/AppMS internal calls\n"
    "                    // (e.g. CheckLaunchApplicationParams in app_mgr_service_inner.cpp:1693)\n"
    "                    // sometimes use userId 0 — without an entry there `aa start` fails\n"
    "                    // with `getBundleInfo fail` → mission_list timeout → fork never happens.\n"
    "                    auto installTime = std::chrono::duration_cast<std::chrono::milliseconds>(\n"
    "                        std::chrono::system_clock::now().time_since_epoch()).count();\n"
    "                    for (int32_t uid_user : {0, 100}) {\n"
    "                        InnerBundleUserInfo userInfo;\n"
    "                        userInfo.bundleName = pkg;\n"
    "                        userInfo.bundleUserInfo.userId = uid_user;\n"
    "                        userInfo.bundleUserInfo.enabled = true;\n"
    "                        userInfo.uid = j.value(\"uid\", 10000);\n"
    "                        userInfo.gids.push_back(j.value(\"gid\", 10000));\n"
    "                        userInfo.installTime = installTime;\n"
    "                        userInfo.updateTime = installTime;\n"
    "                        // adapter v3: persist allocated HAP token into the per-userId\n"
    "                        // InnerBundleUserInfo so InnerBundleInfo::GetAccessTokenId(userId)\n"
    "                        // (called by AMS to populate bundleInfo.applicationInfo.accessTokenId)\n"
    "                        // returns the real allocated token, not 0.\n"
    "                        userInfo.accessTokenId = allocTokenIdEx.tokenIdExStruct.tokenID;\n"
    "                        userInfo.accessTokenIdEx = allocTokenIdEx.tokenIDEx;\n"
    "                        innerBundleInfo.AddInnerBundleUserInfo(userInfo);\n"
    "                        // Also set via official setter so any side-state inside\n"
    "                        // InnerBundleInfo (e.g. cached lookup tables) stays consistent.\n"
    "                        innerBundleInfo.SetAccessTokenIdEx(allocTokenIdEx, uid_user);\n"
    "                    }\n"
    "                    if (!dataMgr->AddInnerBundleInfo(pkg, innerBundleInfo, true)) {\n"
    "                        LOG_E(BMS_TAG_INSTALLER, \"AddInnerBundleInfo failed for %{public}s\", pkg.c_str());\n"
    "                        dataMgr->UpdateBundleInstallState(pkg, InstallState::INSTALL_FAIL);\n"
    "                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "                    }\n"
    "                    // v2 R.3.8 fix: persist to dataStorage so bundle survives reboot.\n"
    "                    // HAP install path in base_bundle_installer.cpp (lines 2197/2428/7938)\n"
    "                    // calls SaveInnerBundleInfo explicitly; the older adapter patch omitted\n"
    "                    // it, leaving RAM-only state that vanished on BMS restart.\n"
    "                    if (!dataMgr->SaveInnerBundleInfo(innerBundleInfo)) {\n"
    "                        LOG_E(BMS_TAG_INSTALLER, \"SaveInnerBundleInfo failed for %{public}s — bundle will be lost on reboot\", pkg.c_str());\n"
    "                    }\n"
    "                    if (!dataMgr->UpdateBundleInstallState(pkg, InstallState::INSTALL_SUCCESS)) {\n"
    "                        LOG_E(BMS_TAG_INSTALLER, \"UpdateBundleInstallState INSTALL_SUCCESS failed for %{public}s\", pkg.c_str());\n"
    "                    }\n"
    "                    LOG_I(BMS_TAG_INSTALLER, \"registered %{public}s in BMS for userId=100 (persistent)\", pkg.c_str());\n"
    "                    // Fire COMMON_EVENT_PACKAGE_ADDED broadcast so launcher / Sceneboard\n"
    "                    // refresh and add the icon dynamically. Without this, launcher only\n"
    "                    // re-queries BMS at startup and won't notice our manual register.\n"
    "                    NotifyBundleEvents installRes;\n"
    "                    installRes.type           = NotifyType::INSTALL;\n"
    "                    installRes.resultCode     = ERR_OK;\n"
    "                    installRes.bundleName     = pkg;\n"
    "                    installRes.modulePackage  = \"entry\";\n"
    "                    installRes.uid            = j.value(\"uid\", 10000);\n"
    "                    installRes.userId         = 100;\n"
    "                    installRes.isAppUpdate    = false;\n"
    "                    // adapter v3 (A+ fix 2026-05-09): launcher consumes these two\n"
    "                    // fields directly from the broadcast event JSON (without a BMS\n"
    "                    // re-query). Hardcoding bundleType=0 / accessTokenId=0 made\n"
    "                    // launcher silent-skip the view refresh — icon only appeared\n"
    "                    // after a reboot's full re-scan. Carry the real values written\n"
    "                    // by AllocHapToken + SetApplicationBundleType above.\n"
    "                    installRes.bundleType     = static_cast<int32_t>(BundleType::APP_ANDROID);\n"
    "                    installRes.accessTokenId  = allocTokenIdEx.tokenIdExStruct.tokenID;\n"
    "                    installRes.appDistributionType = \"none\";\n"
    "                    if (NotifyBundleStatus(installRes) != ERR_OK) {\n"
    "                        LOG_W(BMS_TAG_INSTALLER, \"NotifyBundleStatus(INSTALL) failed for %{public}s — launcher may not refresh\", pkg.c_str());\n"
    "                    } else {\n"
    "                        LOG_I(BMS_TAG_INSTALLER, \"broadcast COMMON_EVENT_PACKAGE_ADDED for %{public}s\", pkg.c_str());\n"
    "                    }\n"
    "                    // Directly write ResourceInfo into BundleResourceRdb with the\n"
    "                    // PNG bytes already extracted by ApkInstaller to\n"
    "                    // /data/app/android/<pkg>/icon.png. Bypass\n"
    "                    // BundleResourceManager::AddResourceInfoByBundleNameWhenInstall\n"
    "                    // because it tries to parse resources.index (always missing for\n"
    "                    // .apk) and ProcessResourceInfoWhenParseFailed substitutes a\n"
    "                    // generic default icon instead of our extracted PNG.\n"
    "                    {\n"
    "                        std::vector<uint8_t> iconBytes;\n"
    "                        // Try install dir first, then a stable staging path (so the\n"
    "                        // icon can be pre-placed by the user / a future ApkInstaller\n"
    "                        // extract step and survive across uninstalls).\n"
    "                        std::vector<std::string> candidates = {\n"
    "                            deployedDir + \"/icon.png\",\n"
    "                            std::string(\"/data/local/tmp/\") + pkg + \"_icon.png\"\n"
    "                        };\n"
    "                        for (const auto &iconFile : candidates) {\n"
    "                            std::ifstream icf(iconFile, std::ios::binary | std::ios::ate);\n"
    "                            if (!icf) continue;\n"
    "                            std::streamsize sz = icf.tellg();\n"
    "                            if (sz <= 0 || sz >= (10 * 1024 * 1024)) continue;\n"
    "                            icf.seekg(0);\n"
    "                            iconBytes.resize(static_cast<size_t>(sz));\n"
    "                            if (icf.read(reinterpret_cast<char*>(iconBytes.data()), sz)) {\n"
    "                                LOG_I(BMS_TAG_INSTALLER, \"loaded icon %{public}s (%{public}zu bytes)\", iconFile.c_str(), iconBytes.size());\n"
    "                                break;\n"
    "                            }\n"
    "                            iconBytes.clear();\n"
    "                        }\n"
    "                        if (iconBytes.empty()) {\n"
    "                            LOG_W(BMS_TAG_INSTALLER, \"no icon file found for %{public}s — RDB row will lack icon\", pkg.c_str());\n"
    "                        }\n"
    "                        // OH launcher reads the ICON TEXT column which holds a\n"
    "                        // \"data:image/png;base64,...\" URI, NOT the FOREGROUND BLOB.\n"
    "                        // Inline base64 encode iconBytes into a data URI so the row\n"
    "                        // matches what BundleResourceParser would produce for a HAP.\n"
    "                        std::string iconDataUri;\n"
    "                        if (!iconBytes.empty()) {\n"
    "                            static const char b64[] = \"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\";\n"
    "                            std::string b64str;\n"
    "                            b64str.reserve(((iconBytes.size() + 2) / 3) * 4);\n"
    "                            size_t i = 0;\n"
    "                            for (; i + 2 < iconBytes.size(); i += 3) {\n"
    "                                uint32_t n = (uint32_t(iconBytes[i]) << 16) | (uint32_t(iconBytes[i+1]) << 8) | uint32_t(iconBytes[i+2]);\n"
    "                                b64str.push_back(b64[(n >> 18) & 0x3F]);\n"
    "                                b64str.push_back(b64[(n >> 12) & 0x3F]);\n"
    "                                b64str.push_back(b64[(n >> 6)  & 0x3F]);\n"
    "                                b64str.push_back(b64[n & 0x3F]);\n"
    "                            }\n"
    "                            if (i < iconBytes.size()) {\n"
    "                                uint32_t n = uint32_t(iconBytes[i]) << 16;\n"
    "                                if (i + 1 < iconBytes.size()) n |= uint32_t(iconBytes[i+1]) << 8;\n"
    "                                b64str.push_back(b64[(n >> 18) & 0x3F]);\n"
    "                                b64str.push_back(b64[(n >> 12) & 0x3F]);\n"
    "                                b64str.push_back(i + 1 < iconBytes.size() ? b64[(n >> 6) & 0x3F] : '=');\n"
    "                                b64str.push_back('=');\n"
    "                            }\n"
    "                            iconDataUri = std::string(\"data:image/png;base64,\") + b64str;\n"
    "                        }\n"
    "                        BundleResourceRdb rdb;\n"
    "                        // App-level row (abilityName empty).\n"
    "                        ResourceInfo appRes;\n"
    "                        appRes.bundleName_     = pkg;\n"
    "                        appRes.moduleName_     = \"entry\";\n"
    "                        appRes.abilityName_    = \"\";\n"
    "                        appRes.label_          = j.value(\"appLabel\", pkg);\n"
    "                        appRes.iconNeedParse_  = false;\n"
    "                        appRes.labelNeedParse_ = false;\n"
    "                        appRes.icon_           = iconDataUri;\n"
    "                        if (!iconBytes.empty()) {\n"
    "                            appRes.foreground_ = iconBytes;\n"
    "                        }\n"
    "                        if (!rdb.AddResourceInfo(appRes)) {\n"
    "                            LOG_W(BMS_TAG_INSTALLER, \"BundleResourceRdb app-level Add fail %{public}s\", pkg.c_str());\n"
    "                        } else {\n"
    "                            LOG_I(BMS_TAG_INSTALLER, \"BundleResourceRdb app-level Add OK %{public}s\", pkg.c_str());\n"
    "                        }\n"
    "                        // Per-ability rows (one per launcher activity so\n"
    "                        // GetLauncherAbilityResourceInfo returns a list).\n"
    "                        for (const auto &abilityJson : j[\"abilities\"]) {\n"
    "                            ResourceInfo aRes = appRes;\n"
    "                            aRes.abilityName_ = abilityJson.value(\"name\", std::string(\"\"));\n"
    "                            std::string aLabel = abilityJson.value(\"label\", j.value(\"appLabel\", pkg));\n"
    "                            if (!aLabel.empty()) {\n"
    "                                aRes.label_ = aLabel;\n"
    "                            }\n"
    "                            if (aRes.abilityName_.empty()) continue;\n"
    "                            if (!rdb.AddResourceInfo(aRes)) {\n"
    "                                LOG_W(BMS_TAG_INSTALLER, \"BundleResourceRdb ability Add fail %{public}s/%{public}s\", pkg.c_str(), aRes.abilityName_.c_str());\n"
    "                            } else {\n"
    "                                LOG_I(BMS_TAG_INSTALLER, \"BundleResourceRdb ability Add OK %{public}s/%{public}s\", pkg.c_str(), aRes.abilityName_.c_str());\n"
    "                            }\n"
    "                        }\n"
    "                    }\n"
    "                } catch (const std::exception &e) {\n"
    "                    LOG_E(BMS_TAG_INSTALLER, \"manifest JSON parse/register failed: %{public}s\", e.what());\n"
    "                    return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "                }\n"
    "            }\n"
    "            LOG_I(BMS_TAG_INSTALLER, \"adapter .apk install + register completed\");\n"
    "            return ERR_OK;\n"
    "        }\n"
    "    }\n"
    "#endif\n"
)

# ----------------------------------------------------------------------------
# Add #include "bundle_resource/bundle_resource_manager.h" so the inline call
# above resolves. Anchor on an existing bundle_resource include if present,
# otherwise prepend after the first #include line in the file.
# ----------------------------------------------------------------------------
INCLUDE_MARKER = '#include "bundle_resource/bundle_resource_rdb.h"'

INCLUDE_ANCHOR_OLD = '#include "base_bundle_installer.h"\n'
INCLUDE_ANCHOR_NEW = (
    '#include "base_bundle_installer.h"\n'
    '#include "bundle_resource/bundle_resource_rdb.h"  // adapter project\n'
    '#include "bundle_resource/resource_info.h"  // adapter project\n'
    '#include "installd_client.h"  // adapter project: route entry.hap copy via installd\n'
    '#include <sys/stat.h>  // adapter project: chmod for entry.hap copy\n'
)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--src", default=DEFAULT)
    args = ap.parse_args()

    if not os.path.isfile(args.src):
        sys.exit(f"ERROR: source not found: {args.src}")

    with open(args.src, "r", encoding="utf-8") as f:
        text = f.read()

    if PATCH_MARKER in text:
        print(f"[skip] {args.src} already has v3 register-with-manifest patch")
        return

    bak = args.src + ".adapter_orig_register_with_manifest"
    # Upgrade path: file was previously patched with v1 or v2 content. Revert to
    # .adapter_orig_register_with_manifest backup (stored before v1 patch),
    # then re-apply v3 below. The backup content == post-dispatch-patch
    # but pre-register-patch — exactly what we need as anchor base.
    if (PATCH_MARKER_V2 in text or PATCH_MARKER_V1 in text) and PATCH_MARKER not in text:
        if not os.path.exists(bak):
            sys.exit(
                "ERROR: prior register-with-manifest patch present but no backup "
                f"to revert from. Expected {bak}"
            )
        prior = "v2" if PATCH_MARKER_V2 in text else "v1"
        print(f"[{prior}->v3] reverting {args.src} from {bak}, then re-applying v3")
        with open(bak, "r", encoding="utf-8") as f:
            text = f.read()
        with open(args.src, "w", encoding="utf-8") as f:
            f.write(text)

    if not os.path.exists(bak):
        with open(bak, "w", encoding="utf-8") as f:
            f.write(text)
        print(f"[backup] {bak}")

    if ANCHOR_OLD not in text:
        sys.exit(
            "ERROR: dispatch anchor not found. "
            "Did apply_bms_apk_dispatch_processinstall.py run first? "
            "Or has its content shifted?"
        )

    if INCLUDE_MARKER not in text:
        if INCLUDE_ANCHOR_OLD not in text:
            sys.exit("ERROR: include anchor base_bundle_installer.h not found")
        text = text.replace(INCLUDE_ANCHOR_OLD, INCLUDE_ANCHOR_NEW, 1)

    text = text.replace(ANCHOR_OLD, ANCHOR_NEW, 1)
    with open(args.src, "w", encoding="utf-8") as f:
        f.write(text)
    print(f"[patched] {args.src} — .apk dispatch now registers bundle + "
          f"AllocHapToken (v3)")


if __name__ == "__main__":
    main()
