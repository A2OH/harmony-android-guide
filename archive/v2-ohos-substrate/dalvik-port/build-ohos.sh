#!/bin/bash
# ═══════════════════════════════════════════════════════════════
# Dalvik VM — OpenHarmony Cross-Compilation Script
#
# Builds the Dalvik VM for OpenHarmony aarch64 (ARM64).
# Requires: OHOS LLVM toolchain + musl headers from OH source tree.
#
# Usage:
#   ./build-ohos.sh              # Full build (sysroot + compile + link)
#   ./build-ohos.sh sysroot      # Only build sysroot
#   ./build-ohos.sh compile      # Only compile (sysroot must exist)
#   ./build-ohos.sh first-pass   # Quick test — compile 3 files
#   ./build-ohos.sh clean        # Remove OHOS build artifacts
# ═══════════════════════════════════════════════════════════════

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# ── Paths ──
OHOS_LLVM="/home/dspfac/openharmony/prebuilts/clang/ohos/linux-x86_64/llvm"
OHOS_MUSL="/home/dspfac/openharmony/third_party/musl"
SYSROOT="$SCRIPT_DIR/ohos-sysroot"

# ── Verify toolchain ──
check_toolchain() {
    if [ ! -x "$OHOS_LLVM/bin/clang++" ]; then
        echo "ERROR: OHOS clang++ not found at $OHOS_LLVM/bin/clang++"
        echo "Expected: OpenHarmony prebuilts at /home/dspfac/openharmony/"
        exit 1
    fi
    echo "Toolchain: $($OHOS_LLVM/bin/clang++ --version | head -1)"
}

# ── Build sysroot from musl headers ──
build_sysroot() {
    echo "═══ Building OHOS aarch64 sysroot ═══"

    if [ ! -d "$OHOS_MUSL/ndk_musl_include" ]; then
        echo "ERROR: musl headers not found at $OHOS_MUSL/ndk_musl_include"
        exit 1
    fi

    rm -rf "$SYSROOT"
    mkdir -p "$SYSROOT/usr/include/bits"
    mkdir -p "$SYSROOT/usr/lib"

    # Copy musl headers
    cp -r "$OHOS_MUSL/ndk_musl_include/"* "$SYSROOT/usr/include/"

    # Copy aarch64 arch-specific headers (bits/*)
    cp -r "$OHOS_MUSL/arch/aarch64/bits/"* "$SYSROOT/usr/include/bits/"
    cp "$OHOS_MUSL/arch/aarch64/"*.h "$SYSROOT/usr/include/" 2>/dev/null || true

    # Copy generic arch bits (don't override arch-specific)
    cp -n "$OHOS_MUSL/arch/generic/bits/"* "$SYSROOT/usr/include/bits/" 2>/dev/null || true

    # Generate bits/alltypes.h (musl generates this from .in files)
    sed -f "$OHOS_MUSL/tools/mkalltypes.sed" \
        "$OHOS_MUSL/arch/aarch64/bits/alltypes.h.in" \
        "$OHOS_MUSL/include/alltypes.h.in" \
        > "$SYSROOT/usr/include/bits/alltypes.h"

    # Generate bits/syscall.h (NR_ + SYS_ aliases)
    sed -n -e 's/__NR_/SYS_/p' "$OHOS_MUSL/arch/aarch64/bits/syscall.h.in" > /tmp/_sys.tmp
    cat "$OHOS_MUSL/arch/aarch64/bits/syscall.h.in" /tmp/_sys.tmp > "$SYSROOT/usr/include/bits/syscall.h"
    rm -f /tmp/_sys.tmp

    # Create version.h
    echo "#define VERSION \"$(cat $OHOS_MUSL/VERSION)\"" > "$SYSROOT/usr/include/version.h"

    # Copy Linux kernel headers if available
    if [ -d "$OHOS_MUSL/porting/linux/user/include" ]; then
        cp -rn "$OHOS_MUSL/porting/linux/user/include/"* "$SYSROOT/usr/include/" 2>/dev/null || true
    fi

    # Copy kernel headers from OH source
    OH_KERNEL_HEADERS="/home/dspfac/openharmony/kernel/linux/patches/linux-5.10/prebuilts/usr/include"
    if [ -d "$OH_KERNEL_HEADERS" ]; then
        cp -rn "$OH_KERNEL_HEADERS/"* "$SYSROOT/usr/include/" 2>/dev/null || true
    fi

    # Copy kernel uapi asm headers (asm/ioctl.h, etc.)
    KERNEL="/home/dspfac/openharmony/kernel/linux/linux-5.10"
    if [ -d "$KERNEL" ]; then
        mkdir -p "$SYSROOT/usr/include/asm" "$SYSROOT/usr/include/asm-generic"
        cp -n "$KERNEL/include/uapi/asm-generic/"*.h "$SYSROOT/usr/include/asm-generic/" 2>/dev/null || true
        cp -n "$KERNEL/arch/arm64/include/uapi/asm/"*.h "$SYSROOT/usr/include/asm/" 2>/dev/null || true
        # Fill asm/ with generic fallbacks for missing arch-specific headers
        for f in "$SYSROOT/usr/include/asm-generic/"*.h; do
            name=$(basename "$f")
            [ ! -f "$SYSROOT/usr/include/asm/$name" ] && cp "$f" "$SYSROOT/usr/include/asm/$name"
        done
        # Copy linux uapi headers (don't override musl porting headers)
        cp -n "$KERNEL/include/uapi/linux/"*.h "$SYSROOT/usr/include/linux/" 2>/dev/null || true
    fi

    # Copy compiler-rt and libc++ for aarch64
    cp "$OHOS_LLVM/lib/clang/15.0.4/lib/aarch64-linux-ohos/"* "$SYSROOT/usr/lib/" 2>/dev/null || true
    cp "$OHOS_LLVM/lib/aarch64-linux-ohos/"* "$SYSROOT/usr/lib/" 2>/dev/null || true

    HEADERS=$(find "$SYSROOT/usr/include" -name '*.h' | wc -l)
    LIBS=$(find "$SYSROOT/usr/lib" -type f | wc -l)
    echo "Sysroot: $SYSROOT"
    echo "  Headers: $HEADERS files"
    echo "  Libs:    $LIBS files"
    echo ""
}

# ── Build libffi for aarch64-linux-ohos ──
build_libffi() {
    echo "═══ Building libffi for aarch64-linux-ohos ═══"

    LIBFFI_SRC="/home/dspfac/openharmony/third_party/libffi"
    if [ ! -d "$LIBFFI_SRC" ]; then
        # Try system libffi source
        LIBFFI_SRC="/usr/src/libffi"
    fi

    if [ ! -d "$LIBFFI_SRC" ]; then
        echo "WARNING: libffi source not found — using stub ffi.h"
        echo "  Link will fail if JNI bridge uses libffi calls."
        echo "  To fix: apt-get source libffi-dev, or provide OHOS libffi"
        return 0
    fi

    LIBFFI_BUILD="$SCRIPT_DIR/build-libffi-ohos"
    mkdir -p "$LIBFFI_BUILD"
    cd "$LIBFFI_SRC"

    CC="$OHOS_LLVM/bin/clang" \
    CXX="$OHOS_LLVM/bin/clang++" \
    AR="$OHOS_LLVM/bin/llvm-ar" \
    RANLIB="$OHOS_LLVM/bin/llvm-ranlib" \
    CFLAGS="--target=aarch64-linux-ohos --sysroot=$SYSROOT -D__MUSL__" \
    ./configure \
        --host=aarch64-linux-ohos \
        --prefix="$SYSROOT/usr" \
        --enable-static \
        --disable-shared \
        --disable-docs 2>&1 | tail -5

    make -j$(nproc) 2>&1 | tail -5
    make install 2>&1 | tail -3

    cd "$SCRIPT_DIR"
    echo "libffi installed to $SYSROOT/usr/lib/"
    echo ""
}

# ── Build zlib for aarch64-linux-ohos ──
build_zlib() {
    echo "═══ Building zlib for aarch64-linux-ohos ═══"

    ZLIB_SRC="/home/dspfac/openharmony/third_party/zlib"
    if [ ! -d "$ZLIB_SRC" ]; then
        echo "WARNING: zlib source not found at $ZLIB_SRC"
        return 0
    fi

    ZLIB_BUILD="$SCRIPT_DIR/build-zlib-ohos"
    rm -rf "$ZLIB_BUILD"
    cp -r "$ZLIB_SRC" "$ZLIB_BUILD"
    cd "$ZLIB_BUILD"

    CC="$OHOS_LLVM/bin/clang --target=aarch64-linux-ohos --sysroot=$SYSROOT -D__MUSL__" \
    AR="$OHOS_LLVM/bin/llvm-ar" \
    RANLIB="$OHOS_LLVM/bin/llvm-ranlib" \
    ./configure --prefix="$SYSROOT/usr" --static 2>&1 | tail -3

    make -j$(nproc) 2>&1 | tail -5
    make install 2>&1 | tail -3

    cd "$SCRIPT_DIR"
    rm -rf "$ZLIB_BUILD"
    echo "zlib installed to $SYSROOT/usr/lib/"
    echo ""
}

# ── Compile Dalvik VM ──
compile_dalvik() {
    echo "═══ Cross-compiling Dalvik VM for OHOS aarch64 ═══"

    if [ ! -d "$SYSROOT/usr/include" ]; then
        echo "ERROR: sysroot not found. Run: $0 sysroot"
        exit 1
    fi

    make TARGET=ohos build-all link
    echo ""
}

# ── Link dalvikvm + dexopt ──
link_dalvik() {
    echo "═══ Linking dalvikvm + dexopt for OHOS aarch64 ═══"
    make TARGET=ohos dalvikvm dexopt
    echo ""

    echo "═══ Build complete ═══"
    echo "Output:"
    ls -lh build-ohos-aarch64/dalvikvm build-ohos-aarch64/dexopt build-ohos-aarch64/libdvm.a 2>/dev/null || true
    file build-ohos-aarch64/dalvikvm 2>/dev/null || true
}

# ── Main ──
CMD="${1:-all}"

case "$CMD" in
    sysroot)
        check_toolchain
        build_sysroot
        ;;
    libffi)
        check_toolchain
        build_libffi
        ;;
    zlib)
        check_toolchain
        build_zlib
        ;;
    deps)
        check_toolchain
        build_sysroot
        build_zlib
        build_libffi
        ;;
    compile)
        check_toolchain
        compile_dalvik
        ;;
    first-pass)
        check_toolchain
        if [ ! -d "$SYSROOT/usr/include" ]; then
            build_sysroot
        fi
        make TARGET=ohos first-pass
        ;;
    link)
        link_dalvik
        ;;
    all)
        check_toolchain
        build_sysroot
        build_zlib
        build_libffi
        compile_dalvik
        link_dalvik
        ;;
    clean)
        make TARGET=ohos clean-ohos
        rm -rf build-libffi-ohos build-zlib-ohos
        echo "Cleaned OHOS build artifacts"
        ;;
    *)
        echo "Usage: $0 {all|sysroot|deps|libffi|zlib|compile|first-pass|link|clean}"
        exit 1
        ;;
esac
