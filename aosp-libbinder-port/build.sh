#!/bin/bash
# ============================================================================
# Westlake — libbinder.so cross-compile driver (musl + bionic variants)
#
# Wraps the Makefile, optionally constructing the OHOS sysroot first if it
# hasn't been built.  Mirrors the dalvik-port/build-ohos.sh pattern.
#
# Usage:
#   ./build.sh sysroot         Build (or rebuild) the OHOS aarch64 sysroot
#   ./build.sh aidl            Generate AIDL .cpp/.h
#   ./build.sh deps            Compile libutils_binder + libcutils archives (musl)
#   ./build.sh libbinder       Compile + link libbinder.so (musl)
#   ./build.sh smoke           Build the binder_smoke test (M1, musl)
#   ./build.sh servicemanager  Compile AOSP servicemanager binary (M2, musl)
#   ./build.sh sm_smoke        Compile sm_smoke test (M2 sandbox test, musl)
#   ./build.sh all             sysroot + aidl + deps + libbinder + smoke + servicemanager + sm_smoke (musl)
#   ./build.sh bionic          M3 build: all bionic-linked targets (out/bionic/)
#   ./build.sh clean           Remove out/ tree (both variants)
#   ./build.sh distclean       Remove out/ and ohos-sysroot/
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

OHOS_LLVM=/home/dspfac/openharmony/prebuilts/clang/ohos/linux-x86_64/llvm
OHOS_MUSL=/home/dspfac/openharmony/third_party/musl
OHOS_KERNEL=/home/dspfac/openharmony/kernel/linux/linux-5.10
OHOS_KERNEL_HEADERS_PREBUILT=/home/dspfac/openharmony/kernel/linux/patches/linux-5.10/prebuilts/usr/include
PREBUILT_SYSROOT=/home/dspfac/openharmony/out/sdk/sdk-native/os-irrelevant/sysroot/usr
SYSROOT="$SCRIPT_DIR/ohos-sysroot"

check_toolchain() {
    if [ ! -x "$OHOS_LLVM/bin/clang++" ]; then
        echo "ERROR: OHOS clang++ not at $OHOS_LLVM/bin/clang++"
        exit 1
    fi
    if [ ! -x /home/dspfac/android-sdk/build-tools/34.0.0/aidl ]; then
        echo "ERROR: AIDL compiler not at /home/dspfac/android-sdk/build-tools/34.0.0/aidl"
        exit 1
    fi
}

build_sysroot() {
    echo "═══ Building OHOS aarch64 sysroot ═══"
    rm -rf "$SYSROOT"
    mkdir -p "$SYSROOT/usr/include/bits"
    mkdir -p "$SYSROOT/usr/include/linux/android"
    mkdir -p "$SYSROOT/usr/lib"

    # Musl headers
    cp -r "$OHOS_MUSL/ndk_musl_include/"* "$SYSROOT/usr/include/"

    # aarch64 arch-specific bits
    cp -r "$OHOS_MUSL/arch/aarch64/bits/"* "$SYSROOT/usr/include/bits/"
    cp "$OHOS_MUSL/arch/aarch64/"*.h "$SYSROOT/usr/include/" 2>/dev/null || true

    # Generic arch bits (no override of arch-specific)
    cp -n "$OHOS_MUSL/arch/generic/bits/"* "$SYSROOT/usr/include/bits/" 2>/dev/null || true

    # alltypes.h (musl generates this from .in files)
    sed -f "$OHOS_MUSL/tools/mkalltypes.sed" \
        "$OHOS_MUSL/arch/aarch64/bits/alltypes.h.in" \
        "$OHOS_MUSL/include/alltypes.h.in" \
        > "$SYSROOT/usr/include/bits/alltypes.h"

    # syscall.h (NR_ + SYS_ aliases)
    sed -n -e 's/__NR_/SYS_/p' "$OHOS_MUSL/arch/aarch64/bits/syscall.h.in" > /tmp/_sys.tmp
    cat "$OHOS_MUSL/arch/aarch64/bits/syscall.h.in" /tmp/_sys.tmp > "$SYSROOT/usr/include/bits/syscall.h"
    rm -f /tmp/_sys.tmp

    echo "#define VERSION \"$(cat $OHOS_MUSL/VERSION)\"" > "$SYSROOT/usr/include/version.h"

    # Linux porting headers (musl shim layer)
    if [ -d "$OHOS_MUSL/porting/linux/user/include" ]; then
        cp -rn "$OHOS_MUSL/porting/linux/user/include/"* "$SYSROOT/usr/include/" 2>/dev/null || true
    fi

    # OHOS kernel prebuilt headers
    if [ -d "$OHOS_KERNEL_HEADERS_PREBUILT" ]; then
        cp -rn "$OHOS_KERNEL_HEADERS_PREBUILT/"* "$SYSROOT/usr/include/" 2>/dev/null || true
    fi

    # OHOS kernel uapi headers
    if [ -d "$OHOS_KERNEL" ]; then
        mkdir -p "$SYSROOT/usr/include/asm" "$SYSROOT/usr/include/asm-generic"
        cp -n "$OHOS_KERNEL/include/uapi/asm-generic/"*.h "$SYSROOT/usr/include/asm-generic/" 2>/dev/null || true
        cp -n "$OHOS_KERNEL/arch/arm64/include/uapi/asm/"*.h "$SYSROOT/usr/include/asm/" 2>/dev/null || true
        for f in "$SYSROOT/usr/include/asm-generic/"*.h; do
            name=$(basename "$f")
            [ ! -f "$SYSROOT/usr/include/asm/$name" ] && cp "$f" "$SYSROOT/usr/include/asm/$name"
        done
        cp -n "$OHOS_KERNEL/include/uapi/linux/"*.h "$SYSROOT/usr/include/linux/" 2>/dev/null || true
    fi

    # Binder UAPI — OHOS 5.10's binder.h lacks BR_FROZEN_BINDER and friends
    # that Android 16 libbinder uses.  Pull a newer one straight from AOSP.
    BINDER_H_NEW=/tmp/binder.h.aosp
    if curl -sfL --max-time 30 \
        'https://android.googlesource.com/kernel/common/+/refs/heads/android-mainline/include/uapi/linux/android/binder.h?format=TEXT' \
        | base64 -d > "$BINDER_H_NEW" \
        && [ -s "$BINDER_H_NEW" ]; then
        cp "$BINDER_H_NEW" "$SYSROOT/usr/include/linux/android/binder.h"
        echo "  Installed AOSP mainline binder.h"
    else
        # Fall back to bionic's local AOSP-11 copy (still has FLAT_BINDER_FLAG_*).
        cp /home/dspfac/aosp-android-11/bionic/libc/kernel/uapi/linux/android/binder.h \
           "$SYSROOT/usr/include/linux/android/binder.h"
        echo "  Network unavailable; using bionic-11 binder.h (older BR codes)"
    fi
    cp /home/dspfac/aosp-android-11/bionic/libc/kernel/uapi/linux/android/binderfs.h \
       "$SYSROOT/usr/include/linux/android/binderfs.h" 2>/dev/null || true

    # Compiler-rt and libs
    cp "$OHOS_LLVM/lib/clang/15.0.4/lib/aarch64-linux-ohos/"* "$SYSROOT/usr/lib/" 2>/dev/null || true
    cp "$OHOS_LLVM/lib/aarch64-linux-ohos/"* "$SYSROOT/usr/lib/" 2>/dev/null || true
    # Pre-built sysroot libs (musl crt*.o, libc.so, libdl.a, libpthread.a, ...)
    cp "$PREBUILT_SYSROOT/lib/aarch64-linux-ohos/"* "$SYSROOT/usr/lib/" 2>/dev/null || true

    HEADERS=$(find "$SYSROOT/usr/include" -name '*.h' | wc -l)
    LIBS=$(find "$SYSROOT/usr/lib" -type f | wc -l)
    echo "Sysroot: $SYSROOT"
    echo "  Headers: $HEADERS"
    echo "  Libs:    $LIBS"
}

CMD="${1:-all}"
case "$CMD" in
    sysroot)
        check_toolchain
        build_sysroot
        ;;
    aidl)
        check_toolchain
        make aidl
        ;;
    deps)
        check_toolchain
        [ -d "$SYSROOT/usr/include" ] || build_sysroot
        make deps
        ;;
    libbinder)
        check_toolchain
        [ -d "$SYSROOT/usr/include" ] || build_sysroot
        make libbinder
        ;;
    smoke)
        check_toolchain
        [ -d "$SYSROOT/usr/include" ] || build_sysroot
        make smoke
        ;;
    servicemanager)
        check_toolchain
        [ -d "$SYSROOT/usr/include" ] || build_sysroot
        [ -f "$SCRIPT_DIR/out/libbinder.so" ] || make libbinder
        make servicemanager
        ;;
    sm_smoke)
        check_toolchain
        [ -d "$SYSROOT/usr/include" ] || build_sysroot
        [ -f "$SCRIPT_DIR/out/libbinder.so" ] || make libbinder
        make sm_smoke
        ;;
    all)
        check_toolchain
        [ -d "$SYSROOT/usr/include" ] || build_sysroot
        make -j$(nproc) all
        ;;
    bionic)
        # M3 bionic build.  Uses NDK r25 from /home/dspfac/android-sdk/ndk.
        # AIDL generation step is reused from the musl build, so we still
        # need the OHOS sysroot to exist (it doesn't affect ABI — AIDL .cpp
        # files are toolchain-independent).
        NDK_LLVM=/home/dspfac/android-sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/linux-x86_64
        if [ ! -x "$NDK_LLVM/bin/clang++" ]; then
            echo "ERROR: NDK r25 clang++ not at $NDK_LLVM/bin/clang++"
            echo "       Install Android NDK r25 at /home/dspfac/android-sdk/ndk/25.2.9519653/"
            exit 1
        fi
        if [ ! -x /home/dspfac/android-sdk/build-tools/34.0.0/aidl ]; then
            echo "ERROR: AIDL compiler not at /home/dspfac/android-sdk/build-tools/34.0.0/aidl"
            exit 1
        fi
        make -j$(nproc) all-bionic
        ;;
    javabinder)
        # M3++: rebuild only the JavaBBinderHolder + repackage the static
        # archive.  Fast path for iterating on the M3++ JavaBBinder code.
        NDK_LLVM=/home/dspfac/android-sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/linux-x86_64
        if [ ! -x "$NDK_LLVM/bin/clang++" ]; then
            echo "ERROR: NDK r25 clang++ not at $NDK_LLVM/bin/clang++"
            exit 1
        fi
        # The static archive's deps may not exist yet on a clean tree —
        # delegate to all-bionic in that case.
        if [ ! -f "$SCRIPT_DIR/out/bionic/libbinder.so" ]; then
            echo "[javabinder] base bionic build missing; running all-bionic first"
            make -j$(nproc) all-bionic
        fi
        # Force rebuild of JavaBBinderHolder.o + static archive.
        rm -f "$SCRIPT_DIR/out/bionic/obj/jni/JavaBBinderHolder.o" \
              "$SCRIPT_DIR/out/bionic/libbinder_full_static.a"
        make -j$(nproc) libbinder_full_static-bionic
        ;;
    clean)
        rm -rf out
        ;;
    distclean)
        rm -rf out ohos-sysroot
        ;;
    *)
        echo "Usage: $0 {all|bionic|javabinder|sysroot|aidl|deps|libbinder|smoke|servicemanager|sm_smoke|clean|distclean}"
        exit 1
        ;;
esac
