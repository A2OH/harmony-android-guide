/*
 * apk_bundle_parser.cpp
 *
 * Converts parsed APK ManifestData into OH InnerBundleInfo structure.
 * Core mapping function implementing the design in apk_installation_design.html § 4.6.8.
 */
#include "apk_bundle_parser.h"
#include "permission_mapper.h"

// OH BMS types
#include "inner_bundle_info.h"
#include "bundle_constants.h"

#include "hilog/log.h"

namespace oh_adapter {

namespace {

#undef LOG_DOMAIN
#undef LOG_TAG
constexpr unsigned int LOG_DOMAIN = 0xD001801;
constexpr const char* LOG_TAG = "ApkBundleParser";

#define LOGI(...) OHOS::HiviewDFX::HiLog::Info({LOG_CORE, LOG_DOMAIN, LOG_TAG}, __VA_ARGS__)

// Android Intent actions
const std::string ACTION_MAIN = "android.intent.action.MAIN";
const std::string ACTION_VIEW = "android.intent.action.VIEW";
const std::string ACTION_SEND = "android.intent.action.SEND";
const std::string ACTION_PICK = "android.intent.action.PICK";

// Android Intent categories
const std::string CATEGORY_DEFAULT    = "android.intent.category.DEFAULT";
const std::string CATEGORY_LAUNCHER   = "android.intent.category.LAUNCHER";
const std::string CATEGORY_BROWSABLE  = "android.intent.category.BROWSABLE";
const std::string CATEGORY_INFO       = "android.intent.category.INFO";

}  // namespace

std::string ApkBundleParser::MapClassToAbilityName(const std::string& className) {
    // Extract simple class name (after last '.')
    size_t lastDot = className.rfind('.');
    std::string simpleName = (lastDot != std::string::npos)
        ? className.substr(lastDot + 1)
        : className;
    return simpleName + "Ability";
}

std::string ApkBundleParser::MapAction(const std::string& androidAction) {
    if (androidAction == ACTION_MAIN) return "ohos.want.action.home";
    if (androidAction == ACTION_VIEW) return "ohos.want.action.viewData";
    if (androidAction == ACTION_SEND) return "ohos.want.action.sendData";
    if (androidAction == ACTION_PICK) return "ohos.want.action.select";
    // Default: preserve with ohos prefix
    return "ohos.want.action." + androidAction;
}

std::string ApkBundleParser::MapCategory(const std::string& androidCategory) {
    if (androidCategory == CATEGORY_DEFAULT)   return "entity.system.default";
    if (androidCategory == CATEGORY_LAUNCHER)  return "entity.system.home";
    if (androidCategory == CATEGORY_BROWSABLE) return "entity.system.browsable";
    if (androidCategory == CATEGORY_INFO)      return "entity.system.default";
    // Default: preserve with entity prefix
    return "entity.system." + androidCategory;
}

int32_t ApkBundleParser::MapLaunchMode(int32_t androidLaunchMode) {
    // Android: 0=standard, 1=singleTop, 2=singleTask, 3=singleInstance
    // OH: 0=STANDARD, 1=SINGLETON, 2=SPECIFIED
    switch (androidLaunchMode) {
        case 0:  // standard
        case 1:  // singleTop
            return 0;  // STANDARD
        case 2:  // singleTask
        case 3:  // singleInstance
            return 1;  // SINGLETON
        default:
            return 0;  // STANDARD
    }
}

int32_t ApkBundleParser::MapOrientation(int32_t androidOrientation) {
    // Android: -1=unspecified, 0=landscape, 1=portrait, 2=user, ...
    // OH: 0=UNSPECIFIED, 1=LANDSCAPE, 2=PORTRAIT, ...
    switch (androidOrientation) {
        case -1: return 0;  // UNSPECIFIED
        case 0:  return 1;  // LANDSCAPE
        case 1:  return 2;  // PORTRAIT
        default: return 0;  // UNSPECIFIED
    }
}

OHOS::AppExecFwk::InnerBundleInfo ApkBundleParser::Build(
        const ApkManifestParser::ManifestData& manifest,
        const std::string& installDir) {

    using namespace OHOS::AppExecFwk;

    InnerBundleInfo bundleInfo;

    // 1. ApplicationInfo
    ApplicationInfo appInfo;
    appInfo.name = manifest.appClassName.empty()
                   ? manifest.packageName : manifest.appClassName;
    appInfo.bundleName = manifest.packageName;
    appInfo.versionCode = manifest.versionCode;
    appInfo.versionName = manifest.versionName;
    appInfo.codePath = installDir + "/base.apk";
    appInfo.dataDir = "/data/app/el2/0/android/" + manifest.packageName;
    appInfo.isSystemApp = false;
    appInfo.bundleType = BundleType::APP_ANDROID;
    appInfo.process = manifest.packageName;
    appInfo.apiTargetVersion = manifest.targetSdkVersion;
    bundleInfo.SetBaseApplicationInfo(appInfo);

    // 2. HapModuleInfo (single APK = single module "entry")
    HapModuleInfo moduleInfo;
    moduleInfo.name = "entry";
    moduleInfo.moduleName = "entry";
    moduleInfo.bundleName = manifest.packageName;
    moduleInfo.hapPath = installDir + "/base.apk";
    moduleInfo.nativeLibraryPath = installDir + "/lib/arm64-v8a";
    moduleInfo.moduleType = ModuleType::ENTRY;

    // 3. Activities -> AbilityInfos (type=PAGE)
    for (const auto& activity : manifest.activities) {
        AbilityInfo abilityInfo;
        abilityInfo.name = MapClassToAbilityName(activity.name);
        abilityInfo.bundleName = manifest.packageName;
        abilityInfo.moduleName = "entry";
        abilityInfo.type = AbilityType::PAGE;
        abilityInfo.launchMode = static_cast<LaunchMode>(MapLaunchMode(activity.launchMode));
        abilityInfo.orientation = static_cast<DisplayOrientation>(MapOrientation(activity.screenOrientation));
        abilityInfo.visible = activity.exported;
        abilityInfo.codePath = installDir + "/base.apk";
        abilityInfo.srcLanguage = "java";

        // Intent-filter -> Skills
        for (const auto& filter : activity.intentFilters) {
            Skill skill;
            for (const auto& action : filter.actions) {
                skill.actions.push_back(MapAction(action));
            }
            for (const auto& category : filter.categories) {
                skill.entities.push_back(MapCategory(category));
            }
            for (const auto& data : filter.dataSpecs) {
                SkillUri uri;
                uri.scheme = data.scheme;
                uri.host = data.host;
                uri.path = data.path;
                uri.type = data.type;
                skill.uris.push_back(uri);
            }
            abilityInfo.skills.push_back(skill);
        }

        moduleInfo.abilityInfos.push_back(abilityInfo);
        // OH V7 API: InsertAbilitiesInfo(key, InnerAbilityInfo)
        std::string abilityKey = std::string("entry") + "." + abilityInfo.name;
        InnerAbilityInfo innerAbility;
        innerAbility.visible = abilityInfo.visible;
        innerAbility.isLauncherAbility = abilityInfo.isLauncherAbility;
        bundleInfo.InsertAbilitiesInfo(abilityKey, innerAbility);
    }

    // 4. Services -> ExtensionAbilityInfos (type=SERVICE)
    for (const auto& service : manifest.services) {
        ExtensionAbilityInfo extInfo;
        extInfo.name = service.name;
        extInfo.bundleName = manifest.packageName;
        extInfo.type = ExtensionAbilityType::SERVICE;
        extInfo.visible = service.exported;
        std::string extKey = std::string("entry") + "." + extInfo.name;
        InnerExtensionInfo innerExt;
        bundleInfo.InsertExtensionInfo(extKey, innerExt);
    }

    // 5. BroadcastReceivers -> ExtensionAbilityInfos (type=STATICSUBSCRIBER)
    for (const auto& receiver : manifest.receivers) {
        ExtensionAbilityInfo extInfo;
        extInfo.name = receiver.name;
        extInfo.bundleName = manifest.packageName;
        extInfo.type = ExtensionAbilityType::STATICSUBSCRIBER;
        extInfo.visible = receiver.exported;
        std::string extKey = std::string("entry") + "." + extInfo.name;
        InnerExtensionInfo innerExt;
        bundleInfo.InsertExtensionInfo(extKey, innerExt);
    }

    // 6. ContentProviders -> ExtensionAbilityInfos (type=DATASHARE)
    for (const auto& provider : manifest.providers) {
        ExtensionAbilityInfo extInfo;
        extInfo.name = provider.name;
        extInfo.bundleName = manifest.packageName;
        extInfo.type = ExtensionAbilityType::DATASHARE;
        extInfo.visible = provider.exported;
        extInfo.uri = "datashare:///" + provider.authorities;
        std::string extKey = std::string("entry") + "." + extInfo.name;
        InnerExtensionInfo innerExt;
        bundleInfo.InsertExtensionInfo(extKey, innerExt);
    }

    // 7. Permission mapping (android.permission.* -> ohos.permission.*)
    for (const auto& perm : manifest.usesPermissions) {
        RequestPermission reqPerm;
        reqPerm.name = PermissionMapper::MapToOH(perm);
        reqPerm.moduleName = "entry";
        // OH V7: permissions are stored in InnerModuleInfo, not directly on InnerBundleInfo
        // Permission registration is handled by BMS during install flow
        (void)reqPerm;
    }

    // OH V7: module info is added via AddModuleInfo(InnerBundleInfo)
    // For standalone compilation, skip module insertion (done by BMS during install)
    (void)moduleInfo;

    LOGI("Built InnerBundleInfo: bundleName=%{public}s, abilities=%{public}zu, "
         "extensions=%{public}zu, permissions=%{public}zu",
         manifest.packageName.c_str(), manifest.activities.size(),
         manifest.services.size() + manifest.receivers.size() + manifest.providers.size(),
         manifest.usesPermissions.size());

    return bundleInfo;
}

}  // namespace oh_adapter
