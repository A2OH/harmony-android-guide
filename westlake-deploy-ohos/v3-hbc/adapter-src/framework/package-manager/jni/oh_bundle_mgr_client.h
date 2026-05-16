/*
 * oh_bundle_mgr_client.h
 *
 * OpenHarmony BundleManagerService IPC client.
 * Wraps remote calls to OH BMS for package query operations.
 *
 * Used by PackageManagerAdapter (Java) via JNI to query package information.
 */
#ifndef OH_BUNDLE_MGR_CLIENT_H
#define OH_BUNDLE_MGR_CLIENT_H
#include <iremote_broker.h>
#include <refbase.h>

#include <string>

// OH IPC types (provided by OH SDK at build time)
namespace OHOS {
    class IRemoteObject;
    namespace AppExecFwk {
        class IBundleMgr;
        class BundleInfo;
    }
}

namespace oh_adapter {

class OHBundleMgrClient {
public:
    static OHBundleMgrClient& getInstance();

    /**
     * Connect to OH BundleManagerService.
     * Obtains remote proxy via OH SystemAbilityManager.
     */
    bool connect();

    /**
     * Disconnect from service.
     */
    void disconnect();

    /**
     * Get BundleInfo by bundle name.
     * @param bundleName OH bundle name (= Android package name)
     * @param flags Query flags
     * @return BundleInfo as JSON string, or empty string if not found
     */
    std::string getBundleInfo(const std::string& bundleName, int32_t flags);

    /**
     * Get ApplicationInfo by bundle name.
     * @param bundleName OH bundle name
     * @param flags Query flags
     * @return ApplicationInfo as JSON string, or empty string if not found
     */
    std::string getApplicationInfo(const std::string& bundleName, int32_t flags);

    /**
     * Get all installed bundle infos.
     * @param flags Query flags
     * @return JSON array of BundleInfo strings
     */
    std::string getAllBundleInfos(int32_t flags);

    /**
     * Query ability infos matching a Want.
     * @param wantJson Want as JSON string
     * @param flags Query flags
     * @return JSON array of AbilityInfo
     */
    std::string queryAbilityInfos(const std::string& wantJson, int32_t flags);

    /**
     * Get UID by bundle name.
     * @param bundleName OH bundle name
     * @return UID, or -1 if not found
     */
    int32_t getUidByBundleName(const std::string& bundleName);

    /**
     * Check permission for a bundle.
     * @param bundleName OH bundle name
     * @param permission OH permission name
     * @return 0 for granted, -1 for denied
     */
    int32_t checkPermission(const std::string& bundleName, const std::string& permission);

private:
    OHBundleMgrClient() = default;
    ~OHBundleMgrClient() = default;

    OHOS::sptr<OHOS::AppExecFwk::IBundleMgr> bundleMgr_ = nullptr;
    bool connected_ = false;
};

}  // namespace oh_adapter

#endif  // OH_BUNDLE_MGR_CLIENT_H
