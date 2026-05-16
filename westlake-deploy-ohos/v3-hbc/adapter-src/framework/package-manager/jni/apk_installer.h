/*
 * apk_installer.h
 *
 * APK file deployment: copy APK, extract native libraries,
 * run dex2oat optimization, create Android data directories.
 *
 * Called by BMS BundleInstaller after APK parsing and signature verification.
 */
#ifndef APK_INSTALLER_H
#define APK_INSTALLER_H

#include <cstdint>
#include <string>
#include <vector>

namespace oh_adapter {

class ApkInstaller {
public:
    struct InstallResult {
        bool success = false;
        std::string errorMsg;
        std::string installedApkPath;    // /data/app/android/{pkg}/base.apk
        std::string nativeLibPath;       // /data/app/android/{pkg}/lib/{abi}/
        std::string oatDir;              // /data/app/android/{pkg}/oat/{isa}/
    };

    /**
     * Deploy an APK file: copy to install directory, extract native libraries,
     * run DEX optimization, and create Android-style data directories.
     *
     * @param srcApkPath   Source APK file path
     * @param packageName  Package name from AndroidManifest.xml
     * @param uid          UID assigned by BMS
     * @param gid          GID assigned by BMS
     * @return InstallResult with success status and paths
     */
    InstallResult DeployApk(const std::string& srcApkPath,
                            const std::string& packageName,
                            int32_t uid, int32_t gid);

    /**
     * Remove an installed APK and its data directories.
     * Called during uninstall.
     *
     * @param packageName  Package name to remove
     * @return true on success
     */
    bool RemoveApk(const std::string& packageName);

    /**
     * Select the best ABI from an APK based on device support.
     *
     * @param apkPath  Path to the APK file
     * @return Best supported ABI string, or empty if no native libs
     */
    static std::string SelectPrimaryAbi(const std::string& apkPath);

    /**
     * Build an OH "resources HAP" for an APK by combining the embedded template
     * (containing module.json + resources.index referencing media:icon /
     * media:app_icon) with the APK's own launcher icon PNG bytes.
     *
     * The output HAP can be opened by OH ResourceManager's
     * createModuleResourceManager → getMediaBase64(0x01000005) to render the
     * APK's icon on the launcher desktop.
     *
     * Implements the runtime half of "方案 2b" (template + ZIP byte-level
     * replacement). See doc/apk_installation_design.html appendix C.
     *
     * @param srcApkPath  Path to the APK on disk
     * @param outHapPath  Where to write the synthesized resources HAP
     * @return true on success; false if APK has no launcher icon or ZIP fails
     */
    static bool ExtractAndPackResourceHap(const std::string& srcApkPath,
                                          const std::string& outHapPath);

private:
    static constexpr const char* ANDROID_INSTALL_DIR = "/data/app/android";
    static constexpr const char* ANDROID_DATA_DIR = "/data/app/el2/0/android";
    static constexpr const char* ANDROID_EXT_DIR = "/data/app/el2/0/android_ext";
    static constexpr const char* DEX2OAT_PATH = "/system/android/bin/dex2oat";
    static constexpr const char* BOOT_IMAGE_PATH = "/system/android/framework/boot.art";
    static constexpr const char* ANDROID_ROOT = "/system/android";

    // ABI priority list (ARM64 device)
    static const std::vector<std::string>& GetSupportedAbis();

    bool CreateInstallDirs(const std::string& packageName);
    bool CopyApk(const std::string& src, const std::string& dst);
    bool ExtractNativeLibs(const std::string& apkPath, const std::string& libDir,
                           const std::string& primaryAbi);
    bool RunDexOpt(const std::string& apkPath, const std::string& oatDir,
                   int32_t uid, const std::string& isa,
                   const std::string& compilerFilter);
    bool CreateDataDirs(const std::string& packageName, int32_t uid, int32_t gid);
    bool SetPermissions(const std::string& path, int32_t uid, int32_t gid, mode_t mode);
};

}  // namespace oh_adapter

#endif  // APK_INSTALLER_H
