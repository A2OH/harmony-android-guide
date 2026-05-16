Canonical source rule for libbms adapter integration
======================================================

This directory holds OH-side glue code that lives in
`oh/foundation/bundlemanager/bundle_framework/services/bundlemgr/src/` at
build time but is sourced from the adapter project at restore time.

Files and their canonical locations:

  adapter_apk_install_minimal.cpp
      Canonical location: HERE (this file)
      Reason: it implements `OHOS::AppExecFwk::ProcessApkInstall` which is a
      bundlemgr-internal function called from bundle_installer.cpp. It cannot
      live in the adapter project because gn rejects cross-component header
      includes for non-innerapi code. The file is OH-side glue.
      Deployed by: restore_after_sync.sh phase B10
        cp $ADAPTER_ROOT/ohos_patches/bundle_framework/services/bundlemgr/src/adapter_apk_install_minimal.cpp \
           $OH_ROOT/foundation/bundlemanager/bundle_framework/services/bundlemgr/src/

  apk_manifest_parser.cpp
  apk_manifest_parser.h
      Canonical location: $ADAPTER_ROOT/framework/package-manager/jni/
      Reason: this is an Android AXML parser that's part of the adapter's
      package-manager subsystem (sibling of apk_installer.cpp, apk_bundle_parser.cpp,
      etc.). It needs to be inside libbms at gn compile time, so we deploy a
      copy via restore_after_sync.sh — but the canonical source stays in adapter.
      Deployed by: restore_after_sync.sh phase B10
        cp $ADAPTER_ROOT/framework/package-manager/jni/apk_manifest_parser.{h,cpp} \
           $OH_ROOT/foundation/bundlemanager/bundle_framework/services/bundlemgr/src/

Why this layout (item 3 collapse, 2026-04-12):
  Previously, apk_manifest_parser.{h,cpp} existed BOTH in the adapter source
  and as a duplicate copy here. That was a "two-source-of-truth" footgun —
  any edit on the adapter side had to be manually re-copied. Now there's
  exactly one canonical location per file, and restore_after_sync.sh phase
  B10 deploys them on every restore. To modify any of these files, edit only
  the canonical copy and re-run restore_after_sync.sh.

Compile path verification:
  After restore_after_sync.sh, all 3 files exist as siblings under
  oh/.../bundlemgr/src/. The libbms BUILD.gn (patched by phase B8) lists
  them in `sources +=`. They build under the same cflags_cc as the rest of
  libbms with the 10 -I AOSP header paths added (also from B8).
