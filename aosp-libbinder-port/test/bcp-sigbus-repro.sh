#!/usr/bin/env bash
# ============================================================================
# PF-arch-053 — bcp-sigbus-repro.sh
#
# Verifies that the PathClassLoader-bootclasspath SIGBUS that previously
# prevented placing aosp-shim.dex (and later framework.jar) on dalvikvm's
# `-Xbootclasspath:` argument is no longer reproducible.
#
# History:
#   * Before May 12 2026: the fat aosp-shim.dex (4.8 MB, 3835 classes) would
#     SIGBUS during PathClassLoader init when on BCP, with `fault_addr =
#     0xfffffffffffffb17` — the Westlake stale-native-entry sentinel
#     `kPFCutStaleNativeEntry` (defined in art-latest/patches/runtime/
#     class_linker.cc:169 and used by several PFCut sentinel checks).
#   * Root cause (now historical): boot-time class init reached one of the
#     ~3000 framework-duplicate shim classes; ART's LinkCode unconditionally
#     stomped EntryPointFromJni → dlsym stub; bionic-static dlsym returns
#     NULL; on the next dispatch trampoline call this fell through to the
#     sentinel-poisoned path and SIGBUSed at 0xfffffffffffffb17.
#   * Fix lineage (all already applied before this script existed):
#       - PF-arch-019: ClassLinker::LinkCode preserves valid JNI entries.
#       - scripts/framework_duplicates.txt: strips 1813 classes that
#         duplicate framework.jar from aosp-shim.dex; aosp-shim.dex is now
#         1.4 MB / 754 classes.
#       - M3-finish: binder JNI baked into dalvikvm statically.
#
# PF-arch-053 is therefore a *verification/regression* fix rather than a
# code patch.  This script encodes the acceptance test so a future
# regression (e.g. someone re-adds duplicates to shim, or breaks
# PF-arch-019) would be detected.
#
# Usage:
#   bash bcp-sigbus-repro.sh        # runs all three modes
#   MODE=baseline   bash bcp-sigbus-repro.sh
#   MODE=bcp-shim   bash bcp-sigbus-repro.sh
#   MODE=bcp-framework bash bcp-sigbus-repro.sh
#
# Environment:
#   ADB           full path to adb (default: Windows-WSL path)
#   ADB_SERIAL    device serial (default: cfb7c9e3)
#   WESTLAKE_DIR  on-device path (default: /data/local/tmp/westlake)
# ============================================================================

set -uo pipefail

ADB="${ADB:-/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe}"
ADB_OPTS="${ADB_OPTS:--H localhost -P 5037}"
ADB_SERIAL="${ADB_SERIAL:-cfb7c9e3}"
WESTLAKE_DIR="${WESTLAKE_DIR:-/data/local/tmp/westlake}"
MODE="${MODE:-all}"

shell() {
    "$ADB" $ADB_OPTS -s "$ADB_SERIAL" shell "$@"
}

log() {
    echo "[bcp-sigbus-repro] $*"
}

# Run a single boot-script invocation and grade pass/fail.  Pass means
# *no SIGBUS* and HelloBinder reports PASS — i.e. exit code 0 plus the
# expected log lines.
run_mode() {
    local label="$1"
    local extra_flags="$2"

    log "MODE=$label flags='$extra_flags'"
    local out
    # Strip CR (adb shell from Windows yields CRLF) so grep ^/$ anchors work.
    out=$(shell "su -c 'cd $WESTLAKE_DIR && bash bin-bionic/m3-dalvikvm-boot.sh test $extra_flags'" 2>&1 | tr -d '\r')
    local rc=$?

    # 1. The presence of `fault_addr=0xfffffffffffffb17` would be the
    #    smoking-gun SIGBUS signature.  Fail hard if seen.
    if echo "$out" | grep -q 'fault_addr=0xfffffffffffffb17'; then
        log "  FAIL: regressed — SIGBUS at sentinel 0xfffffffffffffb17"
        echo "----- regression output -----"
        echo "$out" | tail -40
        echo "----- end regression output -----"
        return 1
    fi
    # 2. Any other SIGBUS is also a fail.
    if echo "$out" | grep -qi 'SIGBUS\|Fatal signal 7'; then
        log "  FAIL: unrelated SIGBUS"
        echo "----- regression output -----"
        echo "$out" | grep -B2 -A8 -E 'SIGBUS|Fatal signal 7' | head -40
        echo "----- end regression output -----"
        return 1
    fi
    # 3. Acceptance test: HelloBinder.PASS line + dalvikvm exit code 0.
    if ! echo "$out" | grep -q '^HelloBinder: PASS$'; then
        log "  FAIL: HelloBinder did not report PASS"
        echo "$out" | tail -30
        return 1
    fi
    if ! echo "$out" | grep -q 'dalvikvm exit code: 0'; then
        log "  FAIL: dalvikvm exit code not 0"
        echo "$out" | tail -10
        return 1
    fi
    log "  PASS"
    return 0
}

OVERALL_RC=0

case "$MODE" in
    baseline|all)
        if ! run_mode baseline ""; then OVERALL_RC=1; fi
        ;;
esac
case "$MODE" in
    bcp-shim|all)
        if ! run_mode bcp-shim "--bcp-shim"; then OVERALL_RC=1; fi
        ;;
esac
case "$MODE" in
    bcp-framework|all)
        if ! run_mode bcp-framework "--bcp-shim --bcp-framework"; then OVERALL_RC=1; fi
        ;;
esac

if [ "$OVERALL_RC" = "0" ]; then
    log "SUCCESS: PF-arch-053 verified — no PathClassLoader-BCP SIGBUS in any mode"
else
    log "FAILURE: at least one mode regressed (see output above)"
fi
exit $OVERALL_RC
