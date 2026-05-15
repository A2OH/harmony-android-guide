#!/usr/bin/env bash
# Westlake — M5 audio daemon build entry-point.
# Cross-compiles `audio_flinger` (bionic-arm64) against the prebuilt
# aosp-libbinder-port/out/bionic/libbinder.so.
#
# Idempotent: make handles incremental builds.  Output: out/bionic/audio_flinger.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

make all
BIN="out/bionic/audio_flinger"
if [[ -f "${BIN}" ]]; then
    SIZE=$(stat -c%s "${BIN}")
    echo ""
    echo "[build.sh] Built: ${BIN} (${SIZE} bytes)"
else
    echo "[build.sh] ERROR: ${BIN} not present after make"
    exit 1
fi
