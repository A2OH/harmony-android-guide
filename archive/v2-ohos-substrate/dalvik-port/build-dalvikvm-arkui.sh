#!/bin/bash
# Build dalvikvm-arkui: Dalvik VM + ArkUI headless engine for ARM32 OHOS QEMU
# Produces: build-ohos-arm32/dalvikvm-arkui
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
OH=/home/dspfac/openharmony
SYSROOT=$SCRIPT_DIR/ohos-sysroot-arm32
CLANG=$OH/prebuilts/clang/ohos/linux-x86_64/llvm/bin/clang++
STRIP=$OH/prebuilts/clang/ohos/linux-x86_64/llvm/bin/llvm-strip
BUILD=$SCRIPT_DIR/build-ohos-arm32
ARKUI_BUILD=/tmp/arkui-arm32-build
ARKUI_STANDALONE=$OH/arkui_test_standalone

echo "═══ Building dalvikvm-arkui (Dalvik + ArkUI headless) ═══"

# Step 1: Ensure libdvm.a exists
if [ ! -f "$BUILD/libdvm.a" ]; then
    echo "ERROR: libdvm.a not found. Run 'make TARGET=ohos-arm32' first."
    exit 1
fi

# Step 2: Ensure ArkUI objects exist
if [ ! -d "$ARKUI_BUILD/CMakeFiles/button_test_ng.dir" ]; then
    echo "ERROR: ArkUI build not found at $ARKUI_BUILD"
    echo "Run: cmake -DCMAKE_TOOLCHAIN_FILE=/tmp/arm32-ohos-toolchain.cmake -S $ARKUI_STANDALONE -B $ARKUI_BUILD && make -C $ARKUI_BUILD -j\$(nproc) button_test_ng"
    exit 1
fi

# Step 3: Collect all ArkUI .o files
echo "Collecting ArkUI objects..."
ARKUI_OBJECTS=$(find $ARKUI_BUILD/CMakeFiles/button_test_ng.dir -name "*.o" | grep -v "arkui_arm32_test" | sort)
ARKUI_COUNT=$(echo "$ARKUI_OBJECTS" | wc -l)
echo "  Found $ARKUI_COUNT ArkUI objects"

# Step 4: Use patched libc (fixes dynlink.o __init_tls conflict)
PATCHED_LIBC=$SYSROOT/usr/lib/libc_static_fixed.a
if [ ! -f "$PATCHED_LIBC" ]; then
    echo "ERROR: Patched libc not found at $PATCHED_LIBC"
    exit 1
fi

# Step 5: Custom TLS init
CUSTOM_TLS=$ARKUI_STANDALONE/custom_init_tls.o
if [ ! -f "$CUSTOM_TLS" ]; then
    echo "ERROR: custom_init_tls.o not found at $CUSTOM_TLS"
    exit 1
fi

# Step 6: Temporarily swap libc for the link
echo "Installing patched libc..."
cp $SYSROOT/usr/lib/libc.a $SYSROOT/usr/lib/libc.a.backup
cp $PATCHED_LIBC $SYSROOT/usr/lib/libc.a

# Step 7: Link everything
echo "Linking dalvikvm-arkui..."
$CLANG \
    --target=arm-linux-ohos --sysroot=$SYSROOT \
    -march=armv7-a -mfloat-abi=softfp -mfpu=neon \
    -fuse-ld=lld --rtlib=compiler-rt -static \
    -std=c++17 -fno-rtti \
    -o $BUILD/dalvikvm-arkui \
    $SCRIPT_DIR/launcher.cpp \
    $BUILD/libdvm.a \
    $ARKUI_OBJECTS \
    $CUSTOM_TLS \
    $SYSROOT/usr/lib/preinit_stubs.o \
    /tmp/gtest-build/lib/libgtest.a \
    /tmp/gtest-build/lib/libgmock.a \
    -lpthread -ldl -lz -lffi \
    -I$SCRIPT_DIR/../dalvik-kitkat/vm \
    -I$SCRIPT_DIR/../dalvik-kitkat \
    -I$SCRIPT_DIR/compat \
    -I$OH/prebuilts/clang/ohos/linux-x86_64/llvm/../../../aosp-android-11/libnativehelper/include_jni \
    -I$OH/../aosp-android-11/libnativehelper/include_jni \
    -I$OH/../aosp-android-11/external/zlib \
    -I$OH/../aosp-android-11/external/dlmalloc \
    -DHAVE_LITTLE_ENDIAN -DHAVE_ENDIAN_H -DHAVE_SYS_UIO_H \
    -DANDROID_SMP=1 -DLOG_TAG='"dalvikvm"' -DDVM_SHOW_EXCEPTION=1 \
    -D_GNU_SOURCE -DNDEBUG -DHAVE_POSIX_FILEMAP \
    -DDVM_NO_ASM_INTERP=1 -DDVM_JMP_TABLE_MTERP=1 \
    -DHAVE_STRLCPY=1 -Dtypeof=__typeof__ \
    -fpermissive -include unistd.h -include sys/uio.h -include sys/resource.h \
    -Wno-unused-parameter -Wno-sign-compare -Wno-write-strings \
    -Wno-format -Wno-narrowing -Wno-pointer-arith \
    -D__MUSL__ \
    2>&1

# Step 8: Restore libc
cp $SYSROOT/usr/lib/libc.a.backup $SYSROOT/usr/lib/libc.a
rm $SYSROOT/usr/lib/libc.a.backup

if [ -f "$BUILD/dalvikvm-arkui" ]; then
    echo "═══ SUCCESS ═══"
    ls -lh $BUILD/dalvikvm-arkui
    file $BUILD/dalvikvm-arkui

    # Strip
    $STRIP $BUILD/dalvikvm-arkui -o $BUILD/dalvikvm-arkui-stripped
    echo "Stripped: $(ls -lh $BUILD/dalvikvm-arkui-stripped | awk '{print $5}')"
else
    echo "═══ LINK FAILED ═══"
    exit 1
fi
