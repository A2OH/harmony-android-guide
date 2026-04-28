#!/bin/bash
# Fail if the standalone Westlake dalvikvm contains unexpected strong
# unresolved symbols. This prevents runtime sentinel calls like the
# BitVector::ClearAllBits failure from reaching the phone.

set -euo pipefail

BIN="${1:-/home/dspfac/art-latest/build-bionic-arm64/bin/dalvikvm}"
NM="${NM:-nm}"

if [ ! -f "$BIN" ]; then
    echo "ERROR: runtime binary not found: $BIN" >&2
    exit 2
fi

if ! command -v "$NM" >/dev/null 2>&1; then
    echo "ERROR: nm not found; set NM=/path/to/nm" >&2
    exit 2
fi

TMP="$(mktemp)"
trap 'rm -f "$TMP"' EXIT

"$NM" -C --undefined-only "$BIN" |
    awk '$1=="U"{sub(/^[[:space:]]*U[[:space:]]+/, ""); print}' |
    grep -Ev '^android::base::ReceiveFileDescriptorVector\(' > "$TMP" || true

if [ -s "$TMP" ]; then
    echo "ERROR: unexpected strong unresolved symbols in $BIN" >&2
    cat "$TMP" >&2
    exit 1
fi

if "$NM" -C --undefined-only "$BIN" |
        grep -E ' U .*art::|BitVector::ClearAllBits|ClassLinker|interpreter|mirror::|Westlake|PFCUT' >/dev/null; then
    echo "ERROR: forbidden unresolved runtime symbol pattern in $BIN" >&2
    "$NM" -C --undefined-only "$BIN" |
        grep -E ' U .*art::|BitVector::ClearAllBits|ClassLinker|interpreter|mirror::|Westlake|PFCUT' >&2
    exit 1
fi

echo "Runtime symbol gate: PASSED ($BIN)"
