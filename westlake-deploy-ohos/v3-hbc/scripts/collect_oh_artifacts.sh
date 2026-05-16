#!/bin/bash
# collect_oh_artifacts.sh - Copy OH build outputs to adapter/out/
#
# Usage: collect_oh_artifacts.sh [--oh-root=PATH]

set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

for arg in "$@"; do
    case "$arg" in
        --oh-root=*) OH_ROOT="${arg#*=}" ;;
    esac
done

OH_OUT=$(detect_oh_output_dir)
OUT_DIR="$ADAPTER_OUT/oh-service"

log_info "Collecting OH build artifacts from $OH_OUT"
log_info "Destination (flat layout, matches deploy_to_dayu200.sh expectation): $OUT_DIR"

# Create output directories
mkdir -p "$OUT_DIR"
mkdir -p "$OUT_DIR/lib.unstripped"

# Collect stripped artifacts — FLAT layout (deploy_to_dayu200.sh expects
# $OUT/oh-service/<name>.z.so, not nested by OH build tree hierarchy)
COLLECTED=0
MISSING=0

for artifact in "${!OH_ARTIFACTS[@]}"; do
    src="$OH_OUT/${OH_ARTIFACTS[$artifact]}"
    if [ -f "$src" ]; then
        cp "$src" "$OUT_DIR/$artifact"
        file_size=$(ls -lh "$src" | awk '{print $5}')
        log_ok "$artifact ($file_size)"
        COLLECTED=$((COLLECTED + 1))
    else
        log_error "Missing: $src"
        MISSING=$((MISSING + 1))
    fi
done

# Collect unstripped artifacts (for debugging) — also flat, under lib.unstripped/
for artifact in "${!OH_ARTIFACTS_UNSTRIPPED[@]}"; do
    src="$OH_OUT/${OH_ARTIFACTS_UNSTRIPPED[$artifact]}"
    if [ -f "$src" ]; then
        cp "$src" "$OUT_DIR/lib.unstripped/$artifact"
    fi
done

echo ""
log_info "=== Collection Summary ==="
log_info "Output directory: $OUT_DIR"
log_ok "Collected: $COLLECTED artifacts"
if [ "$MISSING" -gt 0 ]; then
    log_error "Missing: $MISSING artifacts"
    exit 1
fi

echo ""
log_info "Artifacts:"
find "$OUT_DIR" -name "*.z.so" ! -path "*/lib.unstripped/*" -exec ls -lh {} \;
