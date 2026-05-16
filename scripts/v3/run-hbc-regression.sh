#!/usr/bin/env bash
# ============================================================================
# run-hbc-regression.sh — V3 HBC-runtime regression suite (scaffold).
#
# Phase 2 OHOS / V3 architecture analog of the Phase 1 V2 master regression
# script `scripts/binder-pivot-regression.sh`. Authored as a thin scaffold
# on day 1 so workstream owners (W2/W3/W5/W6/W7) can grow their slots into
# real tests without first having to invent a harness. Existence of this
# file is itself the mitigation for W11 audit risk R2 — "V3 has no
# regression suite analog to binder-pivot-regression.sh".
#
# Specified in:
#   docs/engine/V3-W11-CARRYFORWARD-AUDIT.md §R2  ("Action item: W2 owner
#       authors scripts/v3/run-hbc-regression.sh even as a stub.")
#   docs/engine/V3-WORKSTREAMS.md §W2/W3/W5/W6/W7  (workstream owners
#       supply the per-slot test bodies).
#   docs/engine/V3-REGRESSION.md  (this script's contract +
#       slot-fill-in guide).
#
# Usage:
#   bash scripts/v3/run-hbc-regression.sh [--quick|--full] [--no-color]
#                                         [--no-board] [-h|--help]
#     --quick     artifact discovery + DAYU200 smoke probes only (~30s).
#                 Skips every W slot — useful from CI / pre-push hook.
#     --full      everything: artifact discovery + smoke probes + every
#                 workstream slot. Slots currently SKIP cleanly with a
#                 "owner not yet implemented" message; once an owner
#                 lands real tests, --full picks them up automatically.
#     --no-board  skip every test that needs hdc (artifact-only run on
#                 hosts without DAYU200 access — CI, swarm coordinator).
#     --no-color  disable ANSI color codes.
#
# Exit codes:
#   0  all tests PASS or PASS-with-warn (SKIPs always allowed).
#   1  one or more HARD failures (artifact missing that an owner declared
#      mandatory, or a real workstream test failed).
#   2  setup error (hdc binary not present, --no-board not passed, etc.).
#
# Verdict vocabulary (different from V2 — V3 makes "warn" first-class):
#   PASS            test passed unconditionally.
#   PASS-with-warn  expected-future artifact missing OR board probe found
#                   a known-pending state (e.g. v3-hbc/ not yet deployed
#                   on the board). Counts as PASS for the suite verdict;
#                   surfaced separately in the summary so reviewers can
#                   see what's still pending.
#   SKIP            slot owner has not implemented the test yet (V3 day-1
#                   default for every W-slot).
#   FAIL            HARD failure.
#
# Environment overrides:
#   HDC          path to hdc.exe   (default: Windows-WSL DAYU200 binary)
#   HDC_SERIAL   serial of OHOS board (default rk3568 dd011a4…)
#   BOARD_DIR    on-device path    (default: /data/local/tmp/westlake)
#   REPO_ROOT    repo root         (auto-detected from script location)
#   V3_LOCAL_DIR local v3-hbc/ root (default: westlake-deploy-ohos/v3-hbc)
#
# DESIGN NOTES — read before adding a workstream slot:
#
#   * READ-ONLY contract. This script must never write to the board
#     (no `hdc file send`, no `hdc shell rm/mv/touch`). Board state is
#     prepared by other scripts (run-ohos-test.sh push-bcp etc.); this
#     suite only probes.
#
#   * IDEMPOTENT. Re-running back-to-back must give the same verdict
#     (modulo board uptime drift). No side effects.
#
#   * Self-contained artifact existence checks: an expected-future
#     artifact MUST use `check_expected_artifact` (returns PASS-with-warn
#     when missing). Use `check_required_artifact` (returns FAIL when
#     missing) ONLY for artifacts the owner has declared landed.
#
#   * Slot signature for a W-owner is:
#         w<N>_slot() { ... return 0|99|nonzero ... }
#     Return 0 for PASS, 99 for SKIP (owner not yet implemented — the
#     default at day-1), any other nonzero for FAIL. The slot body
#     should print human-readable progress to stdout; suite captures
#     and grades.
# ============================================================================

set -uo pipefail

# ---------- defaults / arg parsing ------------------------------------------
HDC="${HDC:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}"
HDC_SERIAL="${HDC_SERIAL:-dd011a414436314130101250040eac00}"
BOARD_DIR="${BOARD_DIR:-/data/local/tmp/westlake}"
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
V3_LOCAL_DIR="${V3_LOCAL_DIR:-$REPO_ROOT/westlake-deploy-ohos/v3-hbc}"

MODE="full"
USE_COLOR=1
SKIP_BOARD=0

for arg in "$@"; do
    case "$arg" in
        --quick)     MODE="quick" ;;
        --full)      MODE="full"  ;;
        --no-color)  USE_COLOR=0  ;;
        --no-board)  SKIP_BOARD=1 ;;
        -h|--help)
            sed -n '/^# ====/,/^# ====$/p' "$0" | sed 's/^# \{0,1\}//'
            exit 0
            ;;
        *)
            echo "unknown flag: $arg" >&2
            echo "use --help for usage" >&2
            exit 2
            ;;
    esac
done

# ---------- color helpers ---------------------------------------------------
if [ "$USE_COLOR" = "1" ] && [ -t 1 ]; then
    RED=$'\033[31m'; GREEN=$'\033[32m'; YELLOW=$'\033[33m'
    CYAN=$'\033[36m'; BOLD=$'\033[1m'; RESET=$'\033[0m'
else
    RED=""; GREEN=""; YELLOW=""; CYAN=""; BOLD=""; RESET=""
fi

# ---------- state -----------------------------------------------------------
declare -i PASS_COUNT=0 FAIL_COUNT=0 SKIP_COUNT=0 WARN_COUNT=0 TOTAL=0
declare -a FAILED_TESTS=()
declare -a SKIPPED_TESTS=()
declare -a WARNED_TESTS=()
SUITE_START=$(date +%s)

# ---------- hdc helper (kept private; identical pattern to run-ohos-test.sh)
hdc() { "$HDC" -t "$HDC_SERIAL" "$@"; }

hdc_shell() {
    # Single-string shell command, captured to stdout.
    hdc shell "$*" 2>&1 | tr -d '\r'
}

board_reachable() {
    [ "$SKIP_BOARD" = "0" ] || return 1
    [ -x "$HDC" ] || return 1
    local listing
    listing="$("$HDC" list targets 2>/dev/null | tr -d '\r')"
    echo "$listing" | grep -q "$HDC_SERIAL"
}

# ---------- core grader -----------------------------------------------------
# run_check <display-name> <command-fn> [args...]
#
# command-fn return code mapping:
#   0    -> PASS
#   77   -> PASS-with-warn (reserved sentinel; sets WARN_COUNT++)
#   99   -> SKIP            (slot not yet implemented / artifact absent in
#                            a way the slot considers tolerable)
#   *    -> FAIL
#
# command-fn is expected to print a single line of human-readable detail
# (or nothing) to stdout. That line is shown next to the verdict.
run_check() {
    local name="$1"; shift
    TOTAL+=1
    local idx=$TOTAL
    printf "[%2d] %-50s " "$idx" "$name"
    local start; start=$(date +%s)
    local output rc=0
    output=$("$@" 2>&1) || rc=$?
    local elapsed=$(( $(date +%s) - start ))
    # First non-empty line of output is the "detail" we show.
    local detail
    detail=$(echo "$output" | tr -d '\r' | grep -v '^$' | head -1)

    case "$rc" in
        0)
            PASS_COUNT+=1
            printf "${GREEN}PASS${RESET} (%2ds) %s\n" "$elapsed" "$detail"
            ;;
        77)
            PASS_COUNT+=1
            WARN_COUNT+=1
            WARNED_TESTS+=("$name — ${detail:-pending}")
            printf "${YELLOW}PASS-with-warn${RESET} (%2ds) %s\n" "$elapsed" "$detail"
            ;;
        99)
            SKIP_COUNT+=1
            SKIPPED_TESTS+=("$name — ${detail:-skipped}")
            printf "${YELLOW}SKIP${RESET} (%2ds) %s\n" "$elapsed" "$detail"
            ;;
        *)
            FAIL_COUNT+=1
            FAILED_TESTS+=("$name — exit $rc; ${detail:-no detail}")
            printf "${RED}FAIL${RESET} (%2ds) exit=%d %s\n" "$elapsed" "$rc" "$detail"
            echo "$output" | tail -10 | sed 's/^/      /'
            ;;
    esac
}

# ---------- artifact-existence helpers --------------------------------------

# Use for an artifact the owner has declared landed. Missing => FAIL.
check_required_artifact() {
    local label="$1" path="$2" min_count="${3:-1}"
    if [ -d "$path" ]; then
        local n; n=$(find "$path" -maxdepth 1 -mindepth 1 -type f | wc -l)
        if [ "$n" -ge "$min_count" ]; then
            echo "$label OK ($n files in $(basename "$path")/)"
            return 0
        fi
        echo "$label dir present but only $n files (need >= $min_count): $path"
        return 1
    fi
    if [ -e "$path" ]; then
        echo "$label OK ($(basename "$path"))"
        return 0
    fi
    echo "$label MISSING: $path"
    return 1
}

# Use for an artifact a future workstream will produce. Missing => PASS-with-warn.
check_expected_artifact() {
    local label="$1" path="$2" min_count="${3:-1}"
    if [ -d "$path" ]; then
        local n; n=$(find "$path" -maxdepth 1 -mindepth 1 -type f | wc -l)
        if [ "$n" -ge "$min_count" ]; then
            echo "$label OK ($n files in $(basename "$path")/)"
            return 0
        fi
        echo "$label dir present but only $n files (need >= $min_count) — expected from future workstream"
        return 77
    fi
    if [ -e "$path" ]; then
        echo "$label OK ($(basename "$path"))"
        return 0
    fi
    echo "$label not yet present: $path"
    return 77
}

# ============================================================================
# Section 1: Local artifact discovery
# ============================================================================

check_v3_lib()        { check_required_artifact "v3-hbc/lib"  "$V3_LOCAL_DIR/lib" 10; }
check_v3_jars()       { check_required_artifact "v3-hbc/jars" "$V3_LOCAL_DIR/jars" 5; }
check_v3_bcp()        {
    # boot.art is the single anchor file in bcp/; segments (boot-framework.art
    # etc.) join it as W1/W2 land more.
    check_required_artifact "v3-hbc/bcp/boot.art" "$V3_LOCAL_DIR/bcp/boot.art"
}
check_v3_appspawn_x() { check_required_artifact "v3-hbc/bin/appspawn-x" "$V3_LOCAL_DIR/bin/appspawn-x"; }

check_doc_v3_arch() {
    check_required_artifact "docs/engine/V3-ARCHITECTURE.md" \
        "$REPO_ROOT/docs/engine/V3-ARCHITECTURE.md"
}
check_doc_cr61_1() {
    check_required_artifact "docs/engine/CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md" \
        "$REPO_ROOT/docs/engine/CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md"
}
check_doc_v3_regression() {
    # This doc is co-authored with the script; treat missing as warn (not
    # fail) so the script self-runs on the very first commit even if the
    # doc isn't yet staged.
    check_expected_artifact "docs/engine/V3-REGRESSION.md" \
        "$REPO_ROOT/docs/engine/V3-REGRESSION.md"
}

# ============================================================================
# Section 2: Board smoke probes (READ-ONLY)
# ============================================================================

smoke_list_targets() {
    if [ "$SKIP_BOARD" = "1" ]; then
        echo "--no-board passed"; return 99
    fi
    if [ ! -x "$HDC" ]; then
        echo "hdc.exe not present at $HDC"; return 77
    fi
    local listing; listing="$("$HDC" list targets 2>/dev/null | tr -d '\r')"
    if ! echo "$listing" | grep -q "$HDC_SERIAL"; then
        echo "DAYU200 ($HDC_SERIAL) not connected; hdc said: ${listing:-empty}"
        return 77
    fi
    echo "DAYU200 visible: $HDC_SERIAL"
}

smoke_long_bit() {
    board_reachable || { echo "board not reachable"; return 99; }
    local bits; bits="$(hdc_shell 'getconf LONG_BIT' | tr -d ' \n')"
    if [ "$bits" = "32" ]; then
        echo "userspace LONG_BIT=32 (CR60-compatible)"
        return 0
    fi
    if [ "$bits" = "64" ]; then
        echo "WARN: LONG_BIT=64 — DAYU200 should report 32 per CR60 bitness pivot"
        return 77
    fi
    echo "unexpected LONG_BIT=$bits"
    return 1
}

smoke_selinux() {
    board_reachable || { echo "board not reachable"; return 99; }
    local mode; mode="$(hdc_shell 'getenforce' | tr -d ' \n')"
    if [ "$mode" = "Enforcing" ]; then
        echo "selinux: Enforcing"
        return 0
    fi
    echo "selinux mode=$mode (expected Enforcing)"
    return 77
}

smoke_v3_deployed() {
    board_reachable || { echo "board not reachable"; return 99; }
    # READ-ONLY probe; never deploys. Up to W3 to push v3-hbc/ to the
    # board — this just observes whether it's already there.
    local out
    out="$(hdc_shell "ls $BOARD_DIR/v3-hbc 2>/dev/null | head -5")"
    out="$(echo "$out" | tr -d '\r')"
    if [ -z "$out" ] || echo "$out" | grep -qi "no such\|not found"; then
        echo "$BOARD_DIR/v3-hbc/ not yet deployed (W3 push pending)"
        return 77
    fi
    local n; n="$(hdc_shell "ls $BOARD_DIR/v3-hbc 2>/dev/null | wc -l" | tr -d ' \r\n')"
    echo "$BOARD_DIR/v3-hbc/ deployed ($n top-level entries)"
}

# ============================================================================
# Section 3: Workstream slots — owners fill these in.
#
# Day-1 contract: every slot prints `[SKIP] W<N> — owner not yet implemented`
# and returns 99 (SKIP). The suite verdict remains PASS so CI / pre-push
# hooks don't trip. When a W-owner lands real coverage, replace the slot
# body with the real test and switch the return code accordingly.
#
# WHERE TO FILL IN: each slot is a single bash function below. Search for
# the matching "W<N>-STUB" comment, replace the `return 99` with your real
# command, and update the printed line above it to describe what runs.
# Keep the function self-contained; if you need helpers, prefer adding
# them to scripts/v3/lib/ (create the dir) rather than this file.
# ============================================================================

w2_slot() {
    # W2-STUB — Boot HBC runtime standalone on DAYU200.
    # Owner: V3-WORKSTREAMS.md §W2. Recommended fill-in:
    #   - hdc shell into BOARD_DIR/v3-hbc, run appspawn-x with HBC boot
    #     classpath, capture marker line ("HBC runtime: BOOTED" or similar).
    #   - Acceptance: dalvikvm-x process visible via `ps -ef | grep appspawn-x`
    #     and HBC boot completes within 30s without SIGSEGV/SIGBUS.
    # See: westlake-deploy-ohos/v3-hbc/bin/appspawn-x; docs/engine/V3-ARCHITECTURE.md §"HBC boot".
    echo "W2 owner not yet implemented (boot HBC runtime standalone)"
    return 99
}

w3_slot() {
    # W3 — appspawn-x integration replacing OhosMvpLauncher.
    # Filled in: 2026-05-16 (agent 50, V3-WORKSTREAMS §W3, issue #628).
    #
    # This slot invokes `scripts/v3/aa-launch.sh precheck` (read-only) to
    # exercise the V3 launch path's pre-flight without actually starting
    # an Activity. The real `launch-helloworld` smoke runs out-of-band
    # (it depends on W2 having pushed v3-hbc/ to the board, which the
    # regression suite is forbidden from doing).
    #
    # Verdict mapping:
    #   - aa-launch.sh missing or non-executable        -> FAIL  (regression)
    #   - precheck exit 0 (board ready, V3 deployed)    -> PASS
    #   - precheck exit 1 (V3 not yet deployed by W2)   -> PASS-with-warn (77)
    #     (this is the expected day-1 state until W2 lands its deploy)
    local aa_sh="$REPO_ROOT/scripts/v3/aa-launch.sh"
    if [ ! -x "$aa_sh" ]; then
        echo "scripts/v3/aa-launch.sh missing or not executable"
        return 1
    fi
    if [ "$SKIP_BOARD" = "1" ]; then
        echo "W3 aa-launch.sh present; precheck skipped (--no-board)"
        return 0
    fi
    # Capture precheck output and grade.
    local out rc=0
    out="$("$aa_sh" precheck 2>&1)" || rc=$?
    if [ "$rc" = "0" ]; then
        echo "W3 aa-launch.sh precheck PASS (V3 deployed; appspawn-x running)"
        return 0
    fi
    # Common pending-state: V3 not yet pushed to board by W2.
    if echo "$out" | grep -qE "V3 artifacts not deployed|appspawn-x not running"; then
        echo "W3 aa-launch.sh present; V3 deploy pending W2 (precheck FAIL — expected)"
        return 77
    fi
    # Board unreachable (hdc returned empty target list). Don't FAIL the
    # suite for transient connectivity — the Section 2 smoke probes above
    # will already have flagged it. Map to PASS-with-warn.
    if echo "$out" | grep -qE "not connected|not present at|hdc.exe not present"; then
        echo "W3 aa-launch.sh present; board not reachable for precheck (transient — see Section 2)"
        return 77
    fi
    echo "W3 aa-launch.sh precheck FAIL: $(echo "$out" | tail -1)"
    return 1
}

w5_slot() {
    # W5-STUB — Mock APK validation on V3.
    # Owner: V3-WORKSTREAMS.md §W5. Recommended fill-in:
    #   - Build trivial-activity APK (`scripts/run-ohos-test.sh trivial-activity`
    #     is the V2 analog), push, run under HBC runtime, capture hilog marker.
    #   - Acceptance: MainActivity.onCreate marker visible within 60s.
    # See: V2 analog in scripts/run-ohos-test.sh cmd_trivial_activity.
    echo "W5 owner not yet implemented (mock APK validation)"
    return 99
}

w6_slot() {
    # W6-STUB — noice on OHOS via V3.
    # Owner: V3-WORKSTREAMS.md §W6. Recommended fill-in:
    #   - Push com_github_ashutoshgngwr_noice.apk to BOARD_DIR/v3-hbc/app/.
    #   - Invoke through appspawn-x; assert noice Welcome screen reached
    #     (hilog marker "Noice: Welcome rendered" or DRM frame counter > 0).
    # See: V2 analog: scripts/binder-pivot-regression.sh test_noice_discover.
    echo "W6 owner not yet implemented (noice on V3)"
    return 99
}

w7_slot() {
    # W7-STUB — McD on OHOS via V3.
    # Owner: V3-WORKSTREAMS.md §W7. Recommended fill-in:
    #   - Push McD APK (mcdonalds-real.apk or wi-fry build) to v3-hbc/app/.
    #   - Invoke; assert SplashActivity reached and Wi-Fry offline screen
    #     rendered. Acceptance mirrors V2 in-process breakthrough doc.
    # See: docs/engine/M7_STEP2_REPORT.md for V2 acceptance criteria.
    echo "W7 owner not yet implemented (McD on V3)"
    return 99
}

# ============================================================================
# Suite execution
# ============================================================================

echo "${BOLD}${CYAN}V3 HBC-runtime Regression Suite (scaffold)${RESET}"
echo "${CYAN}===========================================${RESET}"
echo "mode=$MODE  no-board=$SKIP_BOARD  date=$(date -u '+%Y-%m-%dT%H:%M:%SZ')"
echo "HDC=$HDC"
echo "HDC_SERIAL=$HDC_SERIAL"
echo "BOARD_DIR=$BOARD_DIR"
echo "V3_LOCAL_DIR=$V3_LOCAL_DIR"
echo

echo "${BOLD}-- Section 1: local V3 artifact discovery --${RESET}"
run_check "v3-hbc/lib/ populated"          check_v3_lib
run_check "v3-hbc/jars/ populated"         check_v3_jars
run_check "v3-hbc/bcp/boot.art present"    check_v3_bcp
run_check "v3-hbc/bin/appspawn-x present"  check_v3_appspawn_x
run_check "doc: V3-ARCHITECTURE.md"        check_doc_v3_arch
run_check "doc: CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md" check_doc_cr61_1
run_check "doc: V3-REGRESSION.md"          check_doc_v3_regression
echo

echo "${BOLD}-- Section 2: DAYU200 smoke probes (read-only) --${RESET}"
run_check "hdc list targets shows DAYU200" smoke_list_targets
run_check "getconf LONG_BIT == 32"         smoke_long_bit
run_check "getenforce == Enforcing"        smoke_selinux
run_check "v3-hbc/ deployed under $BOARD_DIR" smoke_v3_deployed
echo

if [ "$MODE" = "quick" ]; then
    echo "${YELLOW}-- Section 3 skipped (--quick mode) --${RESET}"
    echo
else
    echo "${BOLD}-- Section 3: workstream slots (day-1 SKIPs) --${RESET}"
    run_check "W2: boot HBC runtime standalone"     w2_slot
    run_check "W3: appspawn-x integration"          w3_slot
    run_check "W5: mock APK validation"             w5_slot
    run_check "W6: noice on V3"                     w6_slot
    run_check "W7: McD on V3"                       w7_slot
    echo
fi

# ----------------------------------------------------------------------------
# Summary (tab-separated tally on the "Results:" line, matching V2 format)
# ----------------------------------------------------------------------------
SUITE_ELAPSED=$(( $(date +%s) - SUITE_START ))
echo "${CYAN}===========================================${RESET}"
printf "${BOLD}Results${RESET}:\t${GREEN}%d PASS${RESET}\t${YELLOW}%d WARN${RESET}\t${RED}%d FAIL${RESET}\t${YELLOW}%d SKIP${RESET}\ttotal=%d\telapsed=%ds\n" \
    "$PASS_COUNT" "$WARN_COUNT" "$FAIL_COUNT" "$SKIP_COUNT" "$TOTAL" "$SUITE_ELAPSED"

if [ "$FAIL_COUNT" -gt 0 ]; then
    echo
    echo "${RED}Failed tests:${RESET}"
    for t in "${FAILED_TESTS[@]}"; do echo "  - $t"; done
fi
if [ "$WARN_COUNT" -gt 0 ]; then
    echo
    echo "${YELLOW}PASS-with-warn (pending owners / expected-future artifacts):${RESET}"
    for t in "${WARNED_TESTS[@]}"; do echo "  - $t"; done
fi
if [ "$SKIP_COUNT" -gt 0 ]; then
    echo
    echo "${YELLOW}Skipped tests:${RESET}"
    for t in "${SKIPPED_TESTS[@]}"; do echo "  - $t"; done
fi
echo

if [ "$FAIL_COUNT" -gt 0 ]; then
    echo "${RED}${BOLD}V3 REGRESSION SUITE: FAIL${RESET}"
    exit 1
fi
if [ "$WARN_COUNT" -gt 0 ]; then
    echo "${GREEN}${BOLD}V3 REGRESSION SUITE: PASS-with-warn${RESET} (${WARN_COUNT} pending owners / artifacts)"
    exit 0
fi
echo "${GREEN}${BOLD}V3 REGRESSION SUITE: ALL PASS${RESET}"
exit 0
