#!/usr/bin/env python3
# ============================================================================
# apply_hwui_nio_utils_unstub.py
#
# Blocker A.6 (2026-04-22): restore AOSP `android_nio_utils.cpp` that was
# wrapped in `#if 0 // OH adapter nio_utils stub ... #endif` by an earlier
# (undocumented) agent, disabling AutoBufferPointer's real implementation.
#
# Without this patch, libhwui.so dlopen reports UND:
#   _ZN7android17AutoBufferPointerC1EP7_JNIEnvP8_jobjecth
# because the file compiles to an empty 660-byte .o.
#
# Paired with: `build/skia_compat_headers/nativehelper/JNIPlatformHelp.h`
# was also simplified to delegate to AOSP canonical header (previously had
# stub inlines with `void*` return for jniGetNioBufferPointer that mismatched
# AOSP's `jlong` signature — preventing the newly-enabled
# android_nio_utils.cpp from compiling). The shim change is applied at
# adapter side and doesn't need `repo sync` restoration since it's in
# build/ (ECS-authoritative).
#
# File restored:
#   frameworks/base/libs/hwui/jni/android_nio_utils.cpp
#
# Idempotent: re-running is safe (checks for `#if 0` marker before patching).
#
# See doc/build_patch_log.html appendix AA.9 for full diagnostic chain.
# ============================================================================

import os
import sys

AOSP = os.environ.get("AOSP_ROOT", os.path.expanduser("~/aosp"))
NIO_CPP = os.path.join(AOSP, "frameworks/base/libs/hwui/jni/android_nio_utils.cpp")


def main():
    if not os.path.exists(NIO_CPP):
        print(f"[FAIL] source not found: {NIO_CPP}", file=sys.stderr)
        sys.exit(1)

    with open(NIO_CPP) as f:
        src = f.read()

    open_marker = "#if 0  // OH adapter nio_utils stub\n"

    if open_marker not in src:
        # Either never stubbed or already unstubbed — check by probing for
        # the real definition string to distinguish.
        if "AutoBufferPointer::AutoBufferPointer(JNIEnv* env" in src:
            print("[SKIP] android_nio_utils.cpp already active (AutoBufferPointer impl present)")
            return
        else:
            print("[FAIL] android_nio_utils.cpp in unexpected state — neither stubbed nor active",
                  file=sys.stderr)
            sys.exit(2)

    # Strip the opening `#if 0 // OH adapter nio_utils stub` and the matching
    # `#endif` at the end of the file. The original AOSP source has a single
    # top-level `#include`-guard-free body, so only one pair to remove.
    src = src.replace(open_marker, "", 1)

    # The closing `#endif` is the last `#endif` in the file.
    last_endif = src.rfind("#endif")
    if last_endif == -1:
        print("[FAIL] closing #endif not found", file=sys.stderr)
        sys.exit(3)
    # Remove just that #endif line (and trailing whitespace after it).
    before = src[:last_endif]
    after = src[last_endif + len("#endif"):]
    src = before.rstrip() + after.lstrip("\n")
    if not src.endswith("\n"):
        src += "\n"

    with open(NIO_CPP, "w") as f:
        f.write(src)
    print("[OK] android_nio_utils.cpp #if 0 wrapper removed — AutoBufferPointer now active")


if __name__ == "__main__":
    main()
