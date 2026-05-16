#!/usr/bin/env bash
# Cross-compile m6-drm-daemon for aarch64-linux-ohos.
# Usage: ./build.sh
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
REPO="$(cd "$HERE/../../.." && pwd)"
CLANG="${CLANG:-/home/dspfac/openharmony/prebuilts/clang/ohos/linux-x86_64/llvm/bin/clang}"
SYSROOT="${SYSROOT:-$REPO/dalvik-port/ohos-sysroot}"

if [ ! -x "$CLANG" ]; then
  echo "clang not found at $CLANG" >&2; exit 1
fi
if [ ! -d "$SYSROOT/usr/include/drm" ]; then
  echo "sysroot missing drm headers: $SYSROOT/usr/include/drm" >&2; exit 1
fi

OUT="${OUT:-$HERE/m6-drm-daemon}"
echo "[build] target=aarch64-linux-ohos sysroot=$SYSROOT out=$OUT"
"$CLANG" \
  --target=aarch64-linux-ohos \
  --sysroot="$SYSROOT" \
  -static \
  -O2 -Wall -Wextra \
  -Wno-unused-parameter -Wno-unused-result -Wno-missing-field-initializers \
  -o "$OUT" \
  "$HERE/m6_drm_daemon.c"
ls -lh "$OUT"
file "$OUT" || true
echo "[build] ok"
