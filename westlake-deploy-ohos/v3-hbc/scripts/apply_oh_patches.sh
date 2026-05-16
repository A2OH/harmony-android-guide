#!/bin/bash
# apply_oh_patches.sh - Apply all OH patches to source tree working copy
#
# Usage: apply_oh_patches.sh [--oh-root=PATH] [--type=build|functional|all]
#
# Applies build system patches and functional patches. Creates backups
# of all modified files. Writes a marker file for revert_oh_patches.sh.

set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

# Parse arguments
PATCH_TYPE="all"
for arg in "$@"; do
    case "$arg" in
        --oh-root=*) OH_ROOT="${arg#*=}" ;;
        --type=*)    PATCH_TYPE="${arg#*=}" ;;
        --help)
            echo "Usage: $0 [--oh-root=PATH] [--type=build|functional|all]"
            exit 0 ;;
    esac
done

PATCHES_DIR="$ADAPTER_ROOT/ohos_patches"
BACKUP_LIST=""

log_info "Applying OH patches (type=$PATCH_TYPE) to $OH_ROOT"

# Marker is now a "last applied" timestamp, not a lock.
# Per-patch idempotence (reverse-check) makes re-runs safe.

# ============================================================
# Helper: backup a file before patching
# ============================================================
backup_file() {
    local target="$1"
    if [ -f "$target" ] && [ ! -f "${target}.adapter_orig" ]; then
        cp "$target" "${target}.adapter_orig"
        BACKUP_LIST="$BACKUP_LIST\n$target"
    fi
}

# ============================================================
# 1. Build system patches (GN/gni fixes)
# ============================================================
apply_build_patches() {
    log_info "Applying build system patches..."

    # Apply unified diff patches
    for patch_file in "${!OH_BUILD_PATCHES[@]}"; do
        local target="${OH_BUILD_PATCHES[$patch_file]}"
        local full_target="$OH_ROOT/$target"
        local full_patch="$SCRIPT_DIR/oh_build_patches/$patch_file"

        if [ ! -f "$full_patch" ]; then
            log_warn "Patch file not found: $full_patch - skipping"
            continue
        fi

        if [ ! -f "$full_target" ]; then
            log_warn "Target file not found: $full_target - skipping"
            continue
        fi

        backup_file "$full_target"

        # Determine the directory context for patch -p1
        # The patch was created with git diff in the component's git repo
        local target_dir
        target_dir=$(dirname "$full_target")

        # Determine component root (patches are git diffs relative to sub-repo)
        local component_root
        case "$patch_file" in
            ets2abc_config*) component_root="$OH_ROOT/build" ;;
            app_internal*)   component_root="$OH_ROOT/build" ;;
            libjpeg*)        component_root="$OH_ROOT/third_party/libjpeg-turbo" ;;
            ui_lite*)        component_root="$OH_ROOT/foundation/arkui/ui_lite" ;;
            *)               component_root="$OH_ROOT" ;;
        esac

        # Per-patch idempotence: try reverse-check first.
        # If reverse applies cleanly, the patch is already in place → skip.
        # Otherwise try forward apply.
        if patch -R --dry-run -p1 -d "$component_root" < "$full_patch" &>/dev/null; then
            log_ok "Already applied: $patch_file (reverse-check passed)"
        elif patch --dry-run -p1 -d "$component_root" < "$full_patch" &>/dev/null; then
            patch -p1 -d "$component_root" < "$full_patch"
            log_ok "Applied: $patch_file -> $target"
        elif patch -R --dry-run -p1 -d "$OH_ROOT" < "$full_patch" &>/dev/null; then
            log_ok "Already applied: $patch_file (reverse-check passed at OH_ROOT)"
        elif patch --dry-run -p1 -d "$OH_ROOT" < "$full_patch" &>/dev/null; then
            patch -p1 -d "$OH_ROOT" < "$full_patch"
            log_ok "Applied (at OH_ROOT): $patch_file -> $target"
        else
            log_error "Failed to apply: $patch_file"
            log_error "  Target: $full_target"
            log_error "  Forward and reverse both failed — source drift detected"
            log_error "  Try applying manually: patch -p1 -d $component_root < $full_patch"
            return 1
        fi
    done

    # Replace product config
    local config_target="$OH_ROOT/$OH_CONFIG_TARGET"
    local config_source="$ADAPTER_ROOT/build/$OH_CONFIG_FILE"
    if [ -f "$config_source" ]; then
        backup_file "$config_target"
        cp "$config_source" "$config_target"
        log_ok "Replaced: $OH_CONFIG_TARGET"
    fi

    log_ok "Build system patches applied"
}

# ============================================================
# 2. Functional patches (adapter feature changes)
# ============================================================
apply_functional_patches() {
    log_info "Applying functional patches..."

    # Full file replacements
    for rel_path in "${OH_FULL_FILE_REPLACEMENTS[@]}"; do
        local source="$PATCHES_DIR/$rel_path"
        # Map patch dir to OH source dir
        local component_key="${rel_path%%/*}"
        local oh_base="${OH_FUNC_PATCH_DIRS[$component_key]}"
        local file_rel="${rel_path#*/}"
        local target="$OH_ROOT/$oh_base/$file_rel"

        if [ ! -f "$source" ]; then
            log_warn "Source file not found: $source - skipping"
            continue
        fi

        # Create target directory if needed (for new files)
        mkdir -p "$(dirname "$target")"
        backup_file "$target"
        cp "$source" "$target"
        log_ok "Copied: $rel_path -> $oh_base/$file_rel"
    done

    # Diff patches
    for rel_path in "${OH_DIFF_PATCHES[@]}"; do
        local patch_file="$PATCHES_DIR/$rel_path"
        local component_key="${rel_path%%/*}"
        local oh_base="${OH_FUNC_PATCH_DIRS[$component_key]}"

        if [ ! -f "$patch_file" ]; then
            log_warn "Patch file not found: $patch_file - skipping"
            continue
        fi

        # Determine target file from patch filename (remove .patch suffix)
        local file_rel="${rel_path#*/}"
        file_rel="${file_rel%.patch}"
        local target="$OH_ROOT/$oh_base/$file_rel"

        if [ ! -f "$target" ]; then
            log_warn "Target file not found: $target - skipping"
            continue
        fi

        backup_file "$target"

        # Per-patch idempotence
        if patch -R --dry-run -p0 "$target" < "$patch_file" &>/dev/null; then
            log_ok "Already applied: $rel_path (reverse-check passed)"
        elif patch --dry-run -p0 "$target" < "$patch_file" &>/dev/null; then
            patch -p0 "$target" < "$patch_file"
            log_ok "Patched: $rel_path"
        elif patch -R --dry-run -p1 "$target" < "$patch_file" &>/dev/null; then
            log_ok "Already applied: $rel_path (reverse-check -p1)"
        elif patch --dry-run -p1 "$target" < "$patch_file" &>/dev/null; then
            patch -p1 "$target" < "$patch_file"
            log_ok "Patched: $rel_path (-p1)"
        else
            log_warn "Skipped: $rel_path — neither forward nor reverse applies (malformed patch or source drift)"
        fi
    done

    log_ok "Functional patches applied"
}

# ============================================================
# Main
# ============================================================

case "$PATCH_TYPE" in
    build)
        apply_build_patches
        ;;
    functional)
        apply_functional_patches
        ;;
    all)
        apply_build_patches
        apply_functional_patches
        ;;
    *)
        log_error "Unknown patch type: $PATCH_TYPE (use build|functional|all)"
        exit 1
        ;;
esac

# Write marker file with list of backed-up files
echo -e "$BACKUP_LIST" > "$PATCH_MARKER"
echo "$(date -Iseconds)" >> "$PATCH_MARKER"

log_ok "All patches applied. Marker written to $PATCH_MARKER"
