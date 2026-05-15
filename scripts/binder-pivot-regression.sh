#!/usr/bin/env bash
# ============================================================================
# binder-pivot-regression.sh — full Westlake Binder Pivot regression suite.
#
# Runs every smoke + service test currently in the tree against the OnePlus 6
# (cfb7c9e3) and reports pass/fail with timings. Intended for swarm
# coordination: agents run this once instead of remembering 7+ individual
# test invocations.
#
# Specified in docs/engine/BINDER_PIVOT_MILESTONES.md §"Test Plan Master
# Summary" (the "master regression script" line at the end). Created as D2
# (May 2026).
#
# Usage:
#   bash scripts/binder-pivot-regression.sh [--quick|--full|--phase=N] [--no-color]
#     --quick     sm_smoke + HelloBinder + AsInterfaceTest only (~1 min)
#     --full      everything, including noice discovery (default; ~5-7 min)
#     --phase=N   stop after milestone N (e.g. --phase=4 skips noice discovery)
#     --no-color  disable ANSI color codes
#
# Exit codes:
#   0  all tests PASS (SKIPs allowed)
#   1  one or more tests FAIL
#   2  setup error (adb missing, device offline, required artifact absent)
#
# Adapts to whatever dalvikvm + aosp-shim.dex + dex/ files are currently on
# the phone — it does NOT rebuild or push. If a test dex isn't present, the
# corresponding test is SKIPped. (Use scripts/build-shim-dex.sh +
# aosp-libbinder-port/build.sh first to refresh artifacts.)
#
# Environment overrides:
#   ADB           full path + flags for adb (default: Windows-WSL with
#                 cfb7c9e3 serial — same as every other script in this tree)
#   ADB_SERIAL    just the serial; ignored if ADB already pins -s
#   WESTLAKE_DIR  on-device path (default: /data/local/tmp/westlake)
# ============================================================================

set -uo pipefail

# ---------- defaults / arg parsing ------------------------------------------
ADB="${ADB:-/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3}"
WESTLAKE_DIR="${WESTLAKE_DIR:-/data/local/tmp/westlake}"
MODE="full"
PHASE_LIMIT=99
USE_COLOR=1
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

for arg in "$@"; do
    case "$arg" in
        --quick) MODE="quick" ;;
        --full)  MODE="full"  ;;
        --phase=*) PHASE_LIMIT="${arg#--phase=}" ;;
        --no-color) USE_COLOR=0 ;;
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

# ---------- color helpers ----------------------------------------------------
if [ "$USE_COLOR" = "1" ] && [ -t 1 ]; then
    RED=$'\033[31m'; GREEN=$'\033[32m'; YELLOW=$'\033[33m'
    CYAN=$'\033[36m'; BOLD=$'\033[1m'; RESET=$'\033[0m'
else
    RED=""; GREEN=""; YELLOW=""; CYAN=""; BOLD=""; RESET=""
fi

# ---------- state ------------------------------------------------------------
declare -i PASS_COUNT=0 FAIL_COUNT=0 SKIP_COUNT=0 TOTAL=0
declare -a FAILED_TESTS=()
declare -a SKIPPED_TESTS=()
SUITE_START=$(date +%s)

# ---------- preflight --------------------------------------------------------
preflight() {
    # adb reachable?
    if ! eval "$ADB devices" >/dev/null 2>&1; then
        echo "${RED}ERROR${RESET}: \`$ADB devices\` failed — check ADB path / server" >&2
        exit 2
    fi
    local devstate
    devstate=$(eval "$ADB get-state" 2>/dev/null | tr -d '\r')
    if [ "$devstate" != "device" ]; then
        echo "${RED}ERROR${RESET}: device not in 'device' state (got '$devstate')" >&2
        exit 2
    fi
    # westlake/ exists?
    if ! eval "$ADB shell test -d $WESTLAKE_DIR" 2>/dev/null; then
        echo "${RED}ERROR${RESET}: $WESTLAKE_DIR missing on device" >&2
        exit 2
    fi
    # critical artifacts
    local missing=0
    for f in dalvikvm aosp-shim.dex bin-bionic/servicemanager bin-bionic/sm_smoke \
             bin-bionic/sm_registrar bin-bionic/m3-dalvikvm-boot.sh \
             dex/HelloBinder.dex core-oj.jar; do
        if ! eval "$ADB shell test -e $WESTLAKE_DIR/$f" 2>/dev/null; then
            echo "${RED}ERROR${RESET}: $WESTLAKE_DIR/$f missing on device" >&2
            missing=1
        fi
    done
    if [ "$missing" = "1" ]; then
        echo "Run scripts/build-shim-dex.sh + aosp-libbinder-port/build.sh + push first." >&2
        exit 2
    fi
}

# ---------- core test dispatcher ---------------------------------------------
# run_test <display name> <milestone-number> <pass-grep> <fail-grep> <command...>
#
#   pass-grep: regex (egrep) that must appear in stdout/stderr for PASS
#              — pass "" to skip pass-checking (only exit-code matters)
#   fail-grep: regex that, if found, means FAIL even when pass-grep matched
#              — pass "" to skip fail-checking
#   command...: shell command run locally; output is captured.
#
# Conventions: exit 0 is necessary but not sufficient — pass-grep must also
# match (most of our tests print "TestName: PASS" on success). Any FAIL/
# crash markers in the captured output flip the verdict.
run_test() {
    local name="$1" phase="$2" passre="$3" failre="$4"
    shift 4
    TOTAL+=1
    local idx=$TOTAL
    if [ "$phase" -gt "$PHASE_LIMIT" ]; then
        SKIP_COUNT+=1
        SKIPPED_TESTS+=("$name (phase limit)")
        printf "[%2d] %-44s ${YELLOW}SKIP${RESET} (--phase=%d)\n" \
            "$idx" "$name" "$PHASE_LIMIT"
        return 0
    fi
    printf "[%2d] %-44s " "$idx" "$name"
    local start=$(date +%s) output rc=0
    output=$("$@" 2>&1) || rc=$?
    local elapsed=$(( $(date +%s) - start ))
    # Normalize CRLF (adb shell from Windows yields CRLF)
    output=$(echo "$output" | tr -d '\r')

    # Reserved exit code 99 from a test fn = SKIP (e.g. dex missing).
    if [ "$rc" = "99" ]; then
        SKIP_COUNT+=1
        SKIPPED_TESTS+=("$name — test fn returned skip sentinel")
        printf "${YELLOW}SKIP${RESET} (%2ds)\n" "$elapsed"
        return 0
    fi

    local verdict="PASS" reason=""
    if [ "$rc" -ne 0 ]; then
        verdict="FAIL"; reason="exit $rc"
    elif [ -n "$failre" ] && echo "$output" | grep -qE "$failre"; then
        verdict="FAIL"; reason="matched fail pattern: $failre"
    elif [ -n "$passre" ] && ! echo "$output" | grep -qE "$passre"; then
        verdict="FAIL"; reason="missing pass pattern: $passre"
    fi

    if [ "$verdict" = "PASS" ]; then
        PASS_COUNT+=1
        printf "${GREEN}PASS${RESET} (%2ds)\n" "$elapsed"
    else
        FAIL_COUNT+=1
        FAILED_TESTS+=("$name — $reason")
        printf "${RED}FAIL${RESET} (%2ds) — %s\n" "$elapsed" "$reason"
        echo "$output" | tail -15 | sed 's/^/      /'
    fi

    # Between tests, the device's init may struggle to restart
    # vndservicemanager (each test stops/starts it). Throttle to give init
    # time to settle so back-to-back tests aren't doomed by init rate-limits.
    sleep 2
}

# Skip a test the script logic determined isn't applicable on this device.
skip_test() {
    local name="$1" reason="$2"
    TOTAL+=1
    SKIP_COUNT+=1
    SKIPPED_TESTS+=("$name — $reason")
    printf "[%2d] %-44s ${YELLOW}SKIP${RESET} — %s\n" "$TOTAL" "$name" "$reason"
}

# ---------- test invocations -------------------------------------------------
#
# Each helper below shells out to adb. We capture combined output, parse it
# for PASS/FAIL markers, and let run_test() grade.
#
# NOTE on shell quoting: the `adb shell "su -c '...'"` triple-quoted form is
# the established pattern in m3-dalvikvm-boot.sh / noice-discover.sh. We pass
# the whole adb invocation as one argv element so quoting is contained inside
# bash arrays we never expand naively.

adb_su_run() {
    # adb_su_run <shell command string>
    # The command runs as root via `su -c "..."` on the device.
    local cmd="$1"
    eval "$ADB shell \"su -c '$cmd'\""
}

# ---- Phase 1: M1+M2 sandbox sm_smoke ---------------------------------------
#
# The musl sandbox-boot.sh isn't always pushed (different agents deploy
# different fragments). We push the local copy if needed, then run.
test_sm_smoke_sandbox() {
    # If sandbox-boot.sh is missing on the device, push it. Use the canonical
    # on-device path so we don't fight with other agents' deployments.
    local on_device="$WESTLAKE_DIR/bin/sandbox-boot.sh"
    if ! eval "$ADB shell test -f $on_device" 2>/dev/null; then
        local local_path="$REPO_ROOT/aosp-libbinder-port/sandbox-boot.sh"
        if [ ! -f "$local_path" ]; then
            echo "      (sandbox-boot.sh not present locally; skipping push)"
            return 1
        fi
        eval "$ADB push '$local_path' '$on_device'" >/dev/null 2>&1 || return 1
        eval "$ADB shell chmod +x '$on_device'" >/dev/null 2>&1 || return 1
    fi
    # Use `sh` not `bash` here. The script shebangs to /system/bin/sh and
    # uses `[ -x file ]` checks which on this device's Magisk-su+bash
    # combination spuriously return false (the 0777 permission isn't
    # honored by bash from inside su context, but sh's POSIX test built-in
    # works). m3-dalvikvm-boot.sh and noice-discover.sh side-step the
    # issue by using `[ -e ]` checks; sandbox-boot.sh predates that.
    adb_su_run "sh $on_device test"
}

# ---- Phase 2: M3 / M3++ dalvikvm tests -------------------------------------
test_hello_binder() {
    adb_su_run "bash $WESTLAKE_DIR/bin-bionic/m3-dalvikvm-boot.sh test"
}

test_as_interface() {
    adb_su_run "bash $WESTLAKE_DIR/bin-bionic/m3-dalvikvm-boot.sh test --test AsInterfaceTest"
}

# ---- Phase 3: BCP regression (PF-arch-053 / M3+) ---------------------------
test_bcp_shim() {
    adb_su_run "bash $WESTLAKE_DIR/bin-bionic/m3-dalvikvm-boot.sh test --bcp-shim"
}

test_bcp_framework() {
    # framework.jar must exist on device
    if ! eval "$ADB shell test -f $WESTLAKE_DIR/framework.jar" 2>/dev/null; then
        return 99   # signal "skip" via reserved exit code
    fi
    adb_su_run "bash $WESTLAKE_DIR/bin-bionic/m3-dalvikvm-boot.sh test --bcp-shim --bcp-framework"
}

# ---- Phase 4: M4 service tests ---------------------------------------------
# Each service test runs as `m3-dalvikvm-boot.sh test --test ClassName`. The
# dex must be present in $WESTLAKE_DIR/dex/.
test_service() {
    local class="$1"
    if ! eval "$ADB shell test -f $WESTLAKE_DIR/dex/${class}.dex" 2>/dev/null; then
        return 99
    fi
    # M4 service tests subclass IXxx.Stub from framework.jar; service impl classes
    # live in aosp-shim.dex. Need both on -Xbootclasspath so the test dex's classloader
    # can resolve them. CR8 (2026-05-12).
    adb_su_run "bash $WESTLAKE_DIR/bin-bionic/m3-dalvikvm-boot.sh test --bcp-shim --bcp-framework --test $class"
}

# ---- Phase 5: noice subtractive discovery ----------------------------------
test_noice_discover() {
    if ! eval "$ADB shell test -f $WESTLAKE_DIR/com_github_ashutoshgngwr_noice.apk" 2>/dev/null; then
        return 99
    fi
    adb_su_run "bash $WESTLAKE_DIR/bin-bionic/noice-discover.sh"
}

# ============================================================================
# Suite execution
# ============================================================================

echo "${BOLD}${CYAN}Westlake Binder Pivot Regression Suite${RESET}"
echo "${CYAN}========================================${RESET}"
echo "mode=$MODE phase-limit=$PHASE_LIMIT date=$(date -u '+%Y-%m-%dT%H:%M:%SZ')"
echo "ADB=$ADB"
echo "WESTLAKE_DIR=$WESTLAKE_DIR"
echo

preflight
echo "${GREEN}preflight OK${RESET}"
echo

# -------- Phase 1: M1+M2 sandbox ---------------------------------------------
run_test "sm_smoke / sandbox (M1+M2)" 2 \
    "sm_smoke: PASS|addService.*ok|listServices returned" \
    "FAIL|Fatal signal|SIGBUS|SIGSEGV" \
    test_sm_smoke_sandbox

# -------- Phase 2: M3 dalvikvm + M3++ asInterface ----------------------------
run_test "HelloBinder (M3)" 3 \
    "HelloBinder: PASS" \
    "HelloBinder: FAIL|Fatal signal|SIGBUS|SIGSEGV" \
    test_hello_binder

run_test "AsInterfaceTest (M3++)" 3 \
    "AsInterfaceTest: PASS" \
    "AsInterfaceTest: FAIL|Fatal signal|SIGBUS|SIGSEGV" \
    test_as_interface

# -------- Phase 3: PF-arch-053 BCP regression --------------------------------
run_test "BCP-shim (M3+)" 3 \
    "HelloBinder: PASS" \
    "FAIL|fault_addr=0xfffffffffffffb17|Fatal signal" \
    test_bcp_shim

# bcp-framework needs framework.jar; test_bcp_framework returns sentinel 99 if absent.
run_test "BCP-framework (M3+ / PF-arch-053)" 3 \
    "HelloBinder: PASS" \
    "FAIL|fault_addr=0xfffffffffffffb17|Fatal signal" \
    test_bcp_framework

# -------- Phase 4: M4 service tests ------------------------------------------
# Map: (Class, milestone-tag, milestone-number)
SERVICES=(
    "ActivityServiceTest|M4a|4"
    "PowerServiceTest|M4-power|4"
    "SystemServiceRouteTest|CR3|4"
    "DisplayServiceTest|M4d|4"
    "NotificationServiceTest|M4e|4"
    "InputMethodServiceTest|M4e|4"
    "WindowServiceTest|M4b|4"
    "PackageServiceTest|M4c|4"
)

for entry in "${SERVICES[@]}"; do
    IFS='|' read -r cls tag phase <<< "$entry"
    # test_service returns sentinel 99 if dex isn't on the device.
    run_test "$cls ($tag)" "$phase" \
        "${cls}: PASS" \
        "${cls}: FAIL|Fatal signal|SIGBUS|SIGSEGV" \
        test_service "$cls"
done

# -------- Phase 5: noice discovery (only in --full) --------------------------
if [ "$MODE" = "quick" ]; then
    skip_test "noice-discover (W2/M4-PRE)" "--quick mode"
elif [ "$PHASE_LIMIT" -lt 5 ]; then
    skip_test "noice-discover (W2/M4-PRE)" "phase limit < 5"
else
    # noice-discover.sh does not print a single "PASS" line; success is
    # exit-code 0 AND reaching at least PHASE E (the post-M4-PRE2 high-water
    # mark — Application.onCreate completes cleanly). PHASE G (post-M4-PRE4)
    # gets further but we don't gate on it here.
    run_test "noice-discover (W2/M4-PRE)" 5 \
        "PHASE E|Application.onCreate|END OF RUN" \
        "Fatal signal|SIGBUS|SIGSEGV|fault_addr=0xfffffffffffffb17" \
        test_noice_discover
fi

# ============================================================================
# Summary
# ============================================================================
SUITE_ELAPSED=$(( $(date +%s) - SUITE_START ))
echo
echo "${CYAN}========================================${RESET}"
echo "${BOLD}Results${RESET}: ${GREEN}${PASS_COUNT} PASS${RESET}  ${RED}${FAIL_COUNT} FAIL${RESET}  ${YELLOW}${SKIP_COUNT} SKIP${RESET}  (total ${TOTAL}, ${SUITE_ELAPSED}s)"
if [ "$FAIL_COUNT" -gt 0 ]; then
    echo
    echo "${RED}Failed tests:${RESET}"
    for t in "${FAILED_TESTS[@]}"; do echo "  - $t"; done
fi
if [ "$SKIP_COUNT" -gt 0 ]; then
    echo
    echo "${YELLOW}Skipped tests:${RESET}"
    for t in "${SKIPPED_TESTS[@]}"; do echo "  - $t"; done
fi
echo

if [ "$FAIL_COUNT" -gt 0 ]; then
    echo "${RED}${BOLD}REGRESSION SUITE: FAIL${RESET}"
    exit 1
fi
echo "${GREEN}${BOLD}REGRESSION SUITE: ALL PASS${RESET}"
exit 0
