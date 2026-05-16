/*
 * apk_bundle_parser.h
 *
 * Converts parsed APK ManifestData into OH InnerBundleInfo structure.
 *
 * Mapping:
 *   manifest.package       -> BundleInfo.bundleName
 *   activity               -> AbilityInfo (type=PAGE), name += "Ability" suffix
 *   service                -> ExtensionAbilityInfo (type=SERVICE)
 *   receiver               -> ExtensionAbilityInfo (type=STATICSUBSCRIBER)
 *   provider               -> ExtensionAbilityInfo (type=DATASHARE)
 *   intent-filter.action   -> Skills.actions (via MapAction)
 *   intent-filter.category -> Skills.entities (via MapCategory)
 *   uses-permission        -> RequestPermission (via PermissionMapper)
 */
#ifndef APK_BUNDLE_PARSER_H
#define APK_BUNDLE_PARSER_H

#include "apk_manifest_parser.h"

#include <string>

// Forward declarations for OH BMS types
namespace OHOS {
namespace AppExecFwk {
    class InnerBundleInfo;
}
}

namespace oh_adapter {

class ApkBundleParser {
public:
    /**
     * Build an InnerBundleInfo from parsed APK manifest data.
     *
     * @param manifest  Parsed manifest data from ApkManifestParser
     * @param installDir  Installation directory (e.g. /data/app/android/{pkg})
     * @return Populated InnerBundleInfo with bundleType=APP_ANDROID
     */
    static OHOS::AppExecFwk::InnerBundleInfo Build(
        const ApkManifestParser::ManifestData& manifest,
        const std::string& installDir);

private:
    /**
     * Map Android class name to OH Ability name.
     * Consistent with IntentWantConverter.mapClassToAbilityName():
     *   "com.example.MainActivity" -> "MainActivityAbility"
     */
    static std::string MapClassToAbilityName(const std::string& className);

    /**
     * Map Android Intent action to OH Want action.
     * Consistent with IntentWantConverter.mapAction().
     */
    static std::string MapAction(const std::string& androidAction);

    /**
     * Map Android Intent category to OH entity.
     */
    static std::string MapCategory(const std::string& androidCategory);

    /**
     * Map Android launch mode to OH launch mode.
     * standard(0)/singleTop(1) -> STANDARD
     * singleTask(2)/singleInstance(3) -> SINGLETON
     */
    static int32_t MapLaunchMode(int32_t androidLaunchMode);

    /**
     * Map Android screen orientation to OH display orientation.
     */
    static int32_t MapOrientation(int32_t androidOrientation);
};

}  // namespace oh_adapter

#endif  // APK_BUNDLE_PARSER_H
