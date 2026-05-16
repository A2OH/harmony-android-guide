#!/bin/bash
# revert_oh_patches.sh - Restore OH source tree to clean state
#
# Usage: revert_oh_patches.sh [--oh-root=PATH]
#
# Restores all .adapter_orig backup files created by apply_oh_patches.sh.
# Falls back to git checkout if backups are missing.

set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

# Parse arguments
for arg in "$@"; do
    case "$arg" in
        --oh-root=*) OH_ROOT="${arg#*=}" ;;
    esac
done

log_info "Reverting OH patches in $OH_ROOT"

REVERTED=0
FAILED=0

# Strategy 1: Restore from .adapter_orig backups
restore_backups() {
    local count=0
    while IFS= read -r -d '' orig_file; do
        local target="${orig_file%.adapter_orig}"
        if [ -f "$orig_file" ]; then
            mv "$orig_file" "$target"
            count=$((count + 1))
        fi
    done < <(find "$OH_ROOT" -name "*.adapter_orig" -print0 2>/dev/null)

    # Handle new files that didn't have originals (like installd_host_impl_android.cpp)
    # These were added by patches and should be removed
    for rel_path in "${OH_FULL_FILE_REPLACEMENTS[@]}"; do
        local component_key="${rel_path%%/*}"
        local oh_base="${OH_FUNC_PATCH_DIRS[$component_key]}"
        local file_rel="${rel_path#*/}"
        local target="$OH_ROOT/$oh_base/$file_rel"
        if [ -f "$target" ] && [ ! -f "${target}.adapter_orig" ]; then
            # This was a new file added by patches - check if it's in git
            local component_git_dir="$OH_ROOT/$oh_base"
            if cd "$component_git_dir" 2>/dev/null && git ls-files --error-unmatch "$file_rel" &>/dev/null; then
                : # File exists in git, don't remove
            else
                rm -f "$target"
                count=$((count + 1))
            fi
        fi
    done

    REVERTED=$count
}

# Strategy 2: Git checkout fallback for individual repos
git_restore() {
    local dirs=(
        "$OH_ROOT/build"
        "$OH_ROOT/third_party/libjpeg-turbo"
        "$OH_ROOT/foundation/arkui/ui_lite"
        "$OH_ROOT/vendor/hihope"
        "$OH_ROOT/foundation/ability/ability_runtime"
        "$OH_ROOT/foundation/bundlemanager/bundle_framework"
    )

    for dir in "${dirs[@]}"; do
        if [ -d "$dir/.git" ] || (cd "$dir" 2>/dev/null && git rev-parse --git-dir &>/dev/null); then
            cd "$dir"
            local changes
            changes=$(git diff --name-only 2>/dev/null | wc -l)
            if [ "$changes" -gt 0 ]; then
                git checkout . 2>/dev/null && log_ok "Git restored: $dir ($changes files)" || true
            fi
        fi
    done
}

# Execute restore
restore_backups

if [ "$REVERTED" -gt 0 ]; then
    log_ok "Restored $REVERTED files from backups"
else
    log_info "No backup files found, trying git restore..."
    git_restore
fi

# Remove marker file
rm -f "$PATCH_MARKER"

log_ok "OH source tree restored to clean state"
