#!/usr/bin/env bash
# CR33 / M6 spike build script — cross-compile spike.cpp for bionic-arm64
# (OnePlus 6 cfb7c9e3, Android 15 LineageOS 22).
#
# Toolchain mirrors aosp-libbinder-port/Makefile bionic targets (NDK r25).
# Links only against bionic libc + libdl + liblog + libnativewindow (for AHardwareBuffer).
# Single-file binary, no dependence on libbinder or any of our own .so artifacts.

set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

NDK_LLVM="/home/dspfac/android-sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/linux-x86_64"
NDK_SYSROOT="${NDK_LLVM}/sysroot"
CXX="${NDK_LLVM}/bin/clang++"

TARGET_FLAGS="--target=aarch64-linux-android33 \
              --sysroot=${NDK_SYSROOT} \
              -fuse-ld=lld \
              --rtlib=compiler-rt"

CXXFLAGS="${TARGET_FLAGS} \
          -std=c++17 \
          -O2 \
          -Wall \
          -Wno-unused-parameter \
          -Wno-unused-variable \
          -fno-rtti \
          -fno-exceptions \
          -D_GNU_SOURCE \
          -stdlib=libc++"

# -static-libstdc++ links libc++ statically so the binary needs no libc++_shared.so
# on the phone.  -landroid pulls AHardwareBuffer_* (libnativewindow shipped via
# libandroid).
LDFLAGS="-static-libstdc++ -llog -landroid"

OUT="${SCRIPT_DIR}/spike"
SRC="${SCRIPT_DIR}/spike.cpp"

echo "[build] Compiling ${SRC} -> ${OUT}"
"${CXX}" ${CXXFLAGS} -o "${OUT}" "${SRC}" ${LDFLAGS}

echo "[build] done: $(stat -c '%s bytes' "${OUT}") $(file "${OUT}" | sed 's,^[^:]*:,,')"
