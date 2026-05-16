==============================================================================
  OH BMS/installd/AppMgr Patches for Android APK Installation Support
==============================================================================

Overview:
  These patches extend the OH Bundle Manager Service (BMS), installd,
  and AppMgrService to support Android APK installation and launch.
  Patch files are organized to mirror the OH source tree structure.

Patch Files:

  1. bundle_framework/interfaces/inner_api/appexecfwk_base/include/bundle_constants.h.patch
     - Adds BundleType::APP_ANDROID = 10 enum value

  2. bundle_framework/services/bundlemgr/src/bundle_installer.cpp.patch
     - Adds APK detection in ProcessBundleInstall() (checks .apk suffix)
     - Adds ProcessApkInstall() method for full APK installation flow

  3. bundle_framework/services/bundlemgr/include/installd/installd_interface.h.patch
     - Adds 5 new IPC methods (codes 90-94):
       CreateAndroidDataDir, ExtractApkNativeLibs, DexOptAndroid,
       RemoveAndroidDataDir, VerifyApkSignature

  4. bundle_framework/services/bundlemgr/src/installd/installd_host_impl_android.cpp
     - NEW FILE: Implementation of the 5 new installd IPC methods

  5. ability_rt/services/appmgr/src/app_mgr_service_inner.cpp.patch
     - Adds APP_ANDROID routing: bundleType == APP_ANDROID -> appspawn-x

Dependencies:
  - framework/package-manager/ module (apk_manifest_parser, apk_bundle_parser, etc.)
  - minizip, OpenSSL, AOSP androidfw
  - framework/appspawn-x/ (Android process spawner)

Application Order:
  1. Apply bundle_constants.h.patch (enum dependency)
  2. Apply installd_interface.h.patch (interface declaration)
  3. Copy installd_host_impl_android.cpp to installd source directory
  4. Apply bundle_installer.cpp.patch (uses new installd methods)
  5. Apply app_mgr_service_inner.cpp.patch (uses BundleType::APP_ANDROID)
