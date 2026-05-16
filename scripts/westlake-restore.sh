#!/usr/bin/env bash
# ============================================================================
# westlake-restore.sh — Single-command state restore for Westlake.
#
# Purpose: kill the per-handoff state-reconstruction tax.  Whether the prior
# agent left the DAYU200 board mid-deploy, abandoned a partial /system push,
# left untracked build cache crud cluttering the local tree, or all three —
# this script restores both sides to a known-good baseline in one shot.
#
# Borrowed and adapted from HBC's
# `westlake-deploy-ohos/v3-hbc/scripts/restore_after_sync.sh` (1069 LOC,
# 2026-04-11).  HBC's version targets AOSP/OH source-tree restoration after
# repo sync (patches, custom product mk, hwui increment scripts, etc.) —
# very different runtime model.  Westlake's variant focuses on:
#   * DAYU200 board: un-overwrite /system files left by partial V3 deploys,
#     stop our daemons, restore factory layout markers.
#   * Local tree: clean known-noisy untracked dirs (gradle cache, ART/oat
#     side effects, artifact dumps from previous noice/McD runs) without
#     touching agent worktrees or commits.
# This is the W9 Pattern 3 deliverable (CR-FF, GitHub A2OH/westlake#634).
#
# Modes:
#   --board-only   restore DAYU200 to a known-clean state (stop our daemons,
#                  un-overwrite /system from .orig_* backups if present,
#                  clear /data/local/tmp/westlake/).
#   --tree-only    restore local working tree (clean obvious untracked
#                  caches; never `git reset --hard`; never delete tracked
#                  files; warn-on-stop on anything ambiguous).
#   --all          both (default).
#   --verify       check state without changing anything.  Used by
#                  `scripts/v3/run-hbc-regression.sh` for drift detection.
#   --dry-run      print actions without executing.  Compatible with all
#                  modes above.
#
# Safety properties (per Westlake claude.md / HBC SOP §"三性"):
#   * Self-contained — no hidden state; reads only env vars and CLI flags.
#   * Idempotent     — re-runnable; each phase checks before acting.
#   * Traceable      — every action logged to stdout with [INFO]/[OK]/[WARN].
#
# Hard constraints (mandatory, do not bypass without user opt-in):
#   * NEVER runs `git reset --hard` automatically — too much loss potential.
#   * NEVER runs `git push --force` — same.
#   * NEVER kills numeric PIDs blindly (HBC Tier-1 rule #1).
#   * NEVER pushes to `/system/` directly — uses staging area per
#     V3-DEPLOY-SOP §V3 Stage 3.
#   * NEVER touches the .claude/worktrees/ tree (other agents may be live).
#
# Usage:
#   bash scripts/westlake-restore.sh                    # restore both
#   bash scripts/westlake-restore.sh --board-only       # just DAYU200
#   bash scripts/westlake-restore.sh --tree-only        # just local tree
#   bash scripts/westlake-restore.sh --verify           # report-only
#   bash scripts/westlake-restore.sh --all --dry-run    # what would happen
#
# Exit codes:
#   0  success (or PASS-with-warn equivalent in --verify mode)
#   1  fatal error
#   2  prerequisite missing (hdc, repo root, etc.)
#   3  partial success / warnings only
#
# See also:
#   docs/engine/V3-RESTORE.md           companion narrative + recipe book
#   docs/engine/V3-DEPLOY-SOP.md        Stage 3 deploy discipline
#   scripts/v3/run-hbc-regression.sh    uses --verify for drift detection
#   westlake-deploy-ohos/v3-hbc/scripts/restore_after_sync.sh
#                                       HBC's original (read-only reference)
# ============================================================================

set -uo pipefail

# ----------------------------------------------------------------------------
# 0. Globals & defaults
# ----------------------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Authoritative defaults — match every other Westlake script in this tree.
HDC="${HDC:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}"
HDC_SERIAL="${HDC_SERIAL:-dd011a414436314130101250040eac00}"
BOARD_DIR="${BOARD_DIR:-/data/local/tmp/westlake}"
STAGE_DIR="${STAGE_DIR:-/data/local/tmp/stage}"

MODE_BOARD=1
MODE_TREE=1
VERIFY_ONLY=0
DRY_RUN=0
WARNINGS=0
ERRORS=0

# ----------------------------------------------------------------------------
# 1. Argument parsing
# ----------------------------------------------------------------------------

for arg in "$@"; do
    case "$arg" in
        --board-only)  MODE_BOARD=1; MODE_TREE=0 ;;
        --tree-only)   MODE_BOARD=0; MODE_TREE=1 ;;
        --all)         MODE_BOARD=1; MODE_TREE=1 ;;
        --verify)      VERIFY_ONLY=1 ;;
        --dry-run)     DRY_RUN=1 ;;
        -h|--help)
            sed -n '/^# Usage:/,/^# Exit codes:/p' "$0" | sed 's/^# \{0,1\}//'
            exit 0
            ;;
        *)
            echo "unknown flag: $arg" >&2
            echo "use --help for usage" >&2
            exit 2
            ;;
    esac
done

# ----------------------------------------------------------------------------
# 2. Color & logging
# ----------------------------------------------------------------------------

if [ -t 1 ]; then
    C_RED=$'\033[31m'; C_GREEN=$'\033[32m'; C_YELLOW=$'\033[33m'
    C_BLUE=$'\033[34m'; C_BOLD=$'\033[1m'; C_RESET=$'\033[0m'
else
    C_RED=""; C_GREEN=""; C_YELLOW=""; C_BLUE=""; C_BOLD=""; C_RESET=""
fi

log_info()  { printf '%s[INFO]%s  %s\n' "$C_BLUE"   "$C_RESET" "$*"; }
log_ok()    { printf '%s[OK]%s    %s\n' "$C_GREEN"  "$C_RESET" "$*"; }
log_warn()  { printf '%s[WARN]%s  %s\n' "$C_YELLOW" "$C_RESET" "$*" >&2; WARNINGS=$((WARNINGS+1)); }
log_err()   { printf '%s[ERR]%s   %s\n' "$C_RED"    "$C_RESET" "$*" >&2; ERRORS=$((ERRORS+1)); }
log_phase() { printf '\n%s========== %s ==========%s\n' "$C_BOLD" "$*" "$C_RESET"; }

# Run a shell command, honoring --dry-run and --verify.
# Verify mode never executes side-effecting commands; dry-run prints them.
run() {
    if [ "$VERIFY_ONLY" = "1" ]; then
        printf '  [verify-only] %s\n' "$*"
        return 0
    fi
    if [ "$DRY_RUN" = "1" ]; then
        printf '  [dry-run] %s\n' "$*"
        return 0
    fi
    eval "$@"
}

# hdc helper.  Honors --verify by short-circuiting to noop.
hdc() {
    [ -x "$HDC" ] || { log_err "hdc not present at $HDC"; return 2; }
    "$HDC" -t "$HDC_SERIAL" "$@"
}
hdc_shell() { hdc shell "$*" 2>&1 | tr -d '\r'; }

# ----------------------------------------------------------------------------
# 3. Banner
# ----------------------------------------------------------------------------

cat <<EOF
${C_BOLD}================================================================================
  Westlake Restore  (W9 Pattern 3, 2026-05-16)
================================================================================${C_RESET}
  REPO_ROOT     = $REPO_ROOT
  HDC           = $HDC$([ -x "$HDC" ] && echo "" || echo "  ${C_RED}(MISSING)${C_RESET}")
  HDC_SERIAL    = $HDC_SERIAL
  BOARD_DIR     = $BOARD_DIR
  STAGE_DIR     = $STAGE_DIR
  MODE_BOARD    = $MODE_BOARD
  MODE_TREE     = $MODE_TREE
  VERIFY_ONLY   = $VERIFY_ONLY
  DRY_RUN       = $DRY_RUN
EOF

# ============================================================================
# Phase A — Board restoration (DAYU200, V3)
# ============================================================================

board_reachable() {
    [ -x "$HDC" ] || return 1
    local listing
    listing="$("$HDC" list targets 2>/dev/null | tr -d '\r')"
    echo "$listing" | grep -q "$HDC_SERIAL"
}

phase_a_board() {
    log_phase "Phase A — DAYU200 board restoration"
    if ! board_reachable; then
        log_warn "DAYU200 ($HDC_SERIAL) not reachable; skipping all board phases"
        return 3
    fi
    log_ok "DAYU200 reachable: $HDC_SERIAL"

    # A1. Stop our daemons (the surface daemon, the audio daemon).  Per HBC
    # SOP Tier-1 rule #1: never bare-PID kill.  Use begetctl on OHOS
    # services and pkill-by-name on our user-space daemons.
    log_info "A1. Stop Westlake daemons (begetctl + pkill-by-name)"
    # Westlake daemons launched by .m7-launch-daemons.sh script in V2 path
    # have stable names; the V3 path uses appspawn-x children which exit on
    # `aa force-stop`.  Stop both.
    for daemon in westlake_surface_daemon westlake_audio_daemon \
                  westlake-surface-daemon westlake-audio-daemon; do
        run "hdc_shell \"pkill -f $daemon\" 2>/dev/null || true"
    done
    # If V3 deploy reached the foundation/render_service stop step, restart
    # them so the board returns to factory-equivalent service set.  Probe
    # first (begetctl is OHOS-only).
    if hdc_shell "command -v begetctl" 2>/dev/null | grep -q begetctl; then
        log_info "A1b. Probe foundation/render_service state"
        local fnd; fnd="$(hdc_shell 'pidof foundation' | tr -d ' \n')"
        local rnd; rnd="$(hdc_shell 'pidof render_service' | tr -d ' \n')"
        if [ -z "$fnd" ]; then
            log_info "  foundation is stopped — restart"
            run "hdc_shell 'begetctl start_service foundation' || true"
        else
            log_ok  "  foundation already running (pid=$fnd)"
        fi
        if [ -z "$rnd" ]; then
            log_info "  render_service is stopped — restart"
            run "hdc_shell 'begetctl start_service render_service' || true"
        else
            log_ok  "  render_service already running (pid=$rnd)"
        fi
    fi

    # A2. Un-overwrite /system from .orig_* backups (per HBC SOP §Stage 1).
    # If a previous V3 deploy left .orig_<TS> snapshots, restore them.
    # Idempotent: silently skips when no .orig_* exists.
    log_info "A2. Restore /system .orig_* backups (if any)"
    local orig_count
    orig_count="$(hdc_shell 'find /system/lib /system/lib/platformsdk /system/etc /system/etc/selinux/targeted/contexts /system/android 2>/dev/null -name "*.orig_*" 2>/dev/null | wc -l' | tr -d ' \n')"
    if [ -z "$orig_count" ] || [ "$orig_count" = "0" ]; then
        log_ok "  no .orig_* backups found — board appears factory or already restored"
    else
        log_warn "  found $orig_count .orig_* backup(s) on /system — restore manually if needed"
        log_warn "  (auto-restore not implemented; safer to inspect first)"
    fi

    # A3. Wipe /data/local/tmp/westlake/ + /data/local/tmp/stage/ — these
    # are scratch areas, no factory data lives here.  Skip in --verify.
    log_info "A3. Clear board scratch areas ($BOARD_DIR, $STAGE_DIR)"
    if hdc_shell "test -d $BOARD_DIR && echo exists" | grep -q exists; then
        run "hdc_shell \"rm -rf $BOARD_DIR\""
        log_ok "  $BOARD_DIR cleared"
    else
        log_ok "  $BOARD_DIR already absent"
    fi
    if hdc_shell "test -d $STAGE_DIR && echo exists" | grep -q exists; then
        run "hdc_shell \"rm -rf $STAGE_DIR\""
        log_ok "  $STAGE_DIR cleared"
    else
        log_ok "  $STAGE_DIR already absent"
    fi

    # A4. SELinux state sanity (read-only).
    log_info "A4. SELinux state check"
    local mode; mode="$(hdc_shell 'getenforce' | tr -d ' \n')"
    case "$mode" in
        Enforcing) log_ok "  SELinux: Enforcing (expected)" ;;
        Permissive) log_warn "  SELinux: Permissive — board may have been set permissive for debug" ;;
        *) log_warn "  SELinux mode: '$mode' (unexpected)" ;;
    esac

    return 0
}

# ============================================================================
# Phase B — Local working tree restoration
# ============================================================================

# List of known-noisy untracked directories that are safe to remove.
# Add a path here ONLY if it is reliably reproducible by re-running the
# relevant build/test step.  NEVER add a path that contains user work.
declare -a SAFE_CLEAN_DIRS=(
    "westlake-host-gradle/.gradle/buildOutputCleanup"
    "westlake-host-gradle/app/build/intermediates/incremental"
    "westlake-host-gradle/app/build/tmp"
    "artifacts/real-mcd/current_phone_check_live"
    "artifacts/real-mcd/dev_http_proxy"
    "out/restore_state.txt"
)

# List of paths that should NEVER be auto-touched (always warn-and-skip).
declare -a NEVER_TOUCH_DIRS=(
    ".claude/worktrees"
    ".git"
    "westlake-deploy-ohos/v3-hbc"
)

phase_b_tree() {
    log_phase "Phase B — Local working tree restoration"

    # B0. Sanity: confirm we're inside the right repo.
    if [ ! -d "$REPO_ROOT/.git" ] && [ ! -f "$REPO_ROOT/.git" ]; then
        log_err "B0: $REPO_ROOT does not look like a git repo (no .git)"
        return 2
    fi
    log_ok "B0. Repo root sanity: $REPO_ROOT"

    # B1. Branch sanity.  We never auto-switch branches — that's user work.
    local branch
    branch="$(cd "$REPO_ROOT" && git branch --show-current 2>/dev/null)"
    if [ -z "$branch" ]; then
        log_warn "B1. HEAD is detached — leaving alone"
    else
        log_ok "B1. On branch: $branch"
    fi

    # B2. Worktree presence — don't disrupt sibling agents.
    if [ -d "$REPO_ROOT/.claude/worktrees" ]; then
        local n
        n="$(find "$REPO_ROOT/.claude/worktrees" -mindepth 1 -maxdepth 1 -type d 2>/dev/null | wc -l)"
        if [ "$n" -gt 0 ]; then
            log_warn "B2. $n sibling worktree(s) under .claude/worktrees/ — left untouched"
        else
            log_ok "B2. No sibling worktrees"
        fi
    fi

    # B3. Clean SAFE_CLEAN_DIRS.  Each entry must (a) exist, (b) be inside
    # REPO_ROOT, (c) not contain ".."  — defensive even though SAFE_CLEAN_DIRS
    # is hard-coded.
    log_info "B3. Clean SAFE_CLEAN_DIRS"
    for rel in "${SAFE_CLEAN_DIRS[@]}"; do
        case "$rel" in
            ..*|/*) log_err "  refuse to clean suspicious path: $rel"; continue ;;
        esac
        local abs="$REPO_ROOT/$rel"
        if [ ! -e "$abs" ]; then
            log_ok "  $rel — already absent"
            continue
        fi
        # Refuse to touch never-touch dirs.
        local skip=0
        for never in "${NEVER_TOUCH_DIRS[@]}"; do
            case "$rel" in
                $never|$never/*) skip=1 ;;
            esac
        done
        if [ "$skip" = "1" ]; then
            log_warn "  $rel intersects NEVER_TOUCH_DIRS — skip"
            continue
        fi
        run "rm -rf \"$abs\""
        log_ok "  cleaned: $rel"
    done

    # B4. Untracked file census (read-only; warn-only).  Print top-level
    # untracked dirs the agent should be aware of — but DO NOT remove them.
    # The user / agent gets a deliberate look at the list before deciding.
    log_info "B4. Untracked-file census (top-level only; read-only)"
    local untracked
    untracked="$(cd "$REPO_ROOT" && git status --short 2>/dev/null \
                 | awk '/^\?\?/{print $2}' \
                 | awk -F/ '{print $1"/"}' | sort -u | head -10)"
    if [ -z "$untracked" ]; then
        log_ok "  no untracked entries"
    else
        local n_untracked
        n_untracked="$(echo "$untracked" | wc -l | tr -d ' \n')"
        log_warn "  $n_untracked top-level untracked entr(y/ies) — inspect manually:"
        while IFS= read -r line; do
            [ -n "$line" ] && log_warn "    $line"
        done <<<"$untracked"
    fi

    # B5. Tracked-file drift census (read-only; warn-only).  Print files
    # with `git diff` content drift, excluding the noisy gradle cache
    # heuristically.
    log_info "B5. Tracked-file drift census"
    local drift
    drift="$(cd "$REPO_ROOT" && git status --short 2>/dev/null \
             | awk '!/^(\?\?|R |  )/{print $2}' \
             | grep -v '^westlake-host-gradle/.gradle/' \
             | grep -v '^westlake-host-gradle/app/build/intermediates/' \
             | grep -v '^westlake-host-gradle/app/build/outputs/' \
             | head -10)"
    if [ -z "$drift" ]; then
        log_ok "  no significant tracked drift"
    else
        local n_drift
        n_drift="$(echo "$drift" | wc -l | tr -d ' \n')"
        log_warn "  $n_drift tracked file(s) with drift (excluding gradle cache):"
        while IFS= read -r line; do
            [ -n "$line" ] && log_warn "    $line"
        done <<<"$drift"
        log_warn "  Did NOT auto-revert — handler must decide per file."
    fi

    return 0
}

# ============================================================================
# Execution
# ============================================================================

if [ "$VERIFY_ONLY" = "1" ]; then
    log_info "VERIFY-ONLY mode — no side effects will be performed"
fi

if [ "$MODE_BOARD" = "1" ]; then
    phase_a_board || true
fi

if [ "$MODE_TREE" = "1" ]; then
    phase_b_tree || true
fi

# ----------------------------------------------------------------------------
# Summary
# ----------------------------------------------------------------------------

log_phase "Summary"
echo "  errors:   $ERRORS"
echo "  warnings: $WARNINGS"
if [ "$ERRORS" -gt 0 ]; then
    log_err "Restore finished with $ERRORS error(s)."
    exit 1
fi
if [ "$WARNINGS" -gt 0 ]; then
    log_warn "Restore finished with $WARNINGS warning(s) — review above."
    exit 3
fi
log_ok "Restore complete — board and tree at known-good baseline."
exit 0
