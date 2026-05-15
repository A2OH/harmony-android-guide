#!/usr/bin/env bash
# CR34 / M5 spike build script — cross-compile spike.c for bionic-arm64
# (OnePlus 6 cfb7c9e3, Android 15 LineageOS 22).
#
# Toolchain mirrors aosp-libbinder-port/Makefile bionic targets (NDK r25),
# same shape as the CR33 surface-daemon spike's build.sh.
# Links against bionic libc + libdl + liblog + libaaudio + libOpenSLES + libm.
# Single-file C binary, no dependence on libbinder.so or any of our own .so.

set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

NDK_LLVM="/home/dspfac/android-sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/linux-x86_64"
NDK_SYSROOT="${NDK_LLVM}/sysroot"
CC="${NDK_LLVM}/bin/clang"

# API 33 to match the rest of the bionic-arm64 toolchain used elsewhere in
# the project.  AAudio is API 26+; OpenSL ES is API 9+; both available at 33.
TARGET_FLAGS="--target=aarch64-linux-android33 \
              --sysroot=${NDK_SYSROOT} \
              -fuse-ld=lld \
              --rtlib=compiler-rt"

CFLAGS="${TARGET_FLAGS} \
        -std=c11 \
        -O2 \
        -Wall \
        -Wno-unused-parameter \
        -D_GNU_SOURCE"

LDFLAGS="-llog -laaudio -lOpenSLES -lm"

OUT="${SCRIPT_DIR}/spike"
SRC="${SCRIPT_DIR}/spike.c"

echo "[build] Compiling ${SRC} -> ${OUT}"
"${CC}" ${CFLAGS} -o "${OUT}" "${SRC}" ${LDFLAGS}

echo "[build] done: $(stat -c '%s bytes' "${OUT}") $(file "${OUT}" | sed 's,^[^:]*:,,')"
