#!/usr/bin/env bash
# ============================================================================
# Westlake — M6 surface daemon build driver
#
# Cross-compiles westlake-surface-daemon for bionic-arm64.  Reuses the
# sibling aosp-libbinder-port/out/bionic/libbinder.so + libutils/libcutils
# /libbase static archives — runs `aosp-libbinder-port/build.sh bionic`
# if those artifacts are missing.
#
# Usage:
#   ./build.sh           Build out/bionic/surfaceflinger
#   ./build.sh clean     Remove out/
# ============================================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

LIBBINDER_PORT="$SCRIPT_DIR/../aosp-libbinder-port"
LIBBINDER_OUT="$LIBBINDER_PORT/out/bionic"

NDK_LLVM=/home/dspfac/android-sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/linux-x86_64
if [ ! -x "$NDK_LLVM/bin/clang++" ]; then
    echo "ERROR: NDK r25 clang++ not at $NDK_LLVM/bin/clang++"
    echo "       Install Android NDK r25 at /home/dspfac/android-sdk/ndk/25.2.9519653/"
    exit 1
fi

# Ensure the sibling port has been built — we link against its libbinder.so.
if [ ! -f "$LIBBINDER_OUT/libbinder.so" ]; then
    echo "[surface-daemon] libbinder.so not built yet; running aosp-libbinder-port/build.sh bionic first"
    (cd "$LIBBINDER_PORT" && bash build.sh bionic)
fi

CMD="${1:-all}"
case "$CMD" in
    all|build)
        make -j"$(nproc)"
        ;;
    clean)
        make clean
        ;;
    *)
        echo "Usage: $0 {all|build|clean}"
        exit 1
        ;;
esac
