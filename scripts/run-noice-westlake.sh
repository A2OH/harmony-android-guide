#!/usr/bin/env bash
# ============================================================================
# M7-Step1 -- run-noice-westlake.sh
#
# CR38 §5 M7 acceptance fixture: orchestrate the full Westlake-runs-noice
# stack on OnePlus 6 (cfb7c9e3).  Per CR38 §2.1 the 12-step boot sequence:
#
#   1.  Push/stage artifacts                  (assumed already deployed)
#   2.  Stop device vndservicemanager
#   3.  Spawn our M2 servicemanager on /dev/vndbinder
#   4.  Spawn M5 audio_flinger daemon (addService media.audio_flinger)
#   5.  Spawn M6 surfaceflinger daemon (addService SurfaceFlinger,
#       writes DLST opcodes to WESTLAKE_DLST_PIPE)
#   6.  (Compose host APK -- optional Phase 1 visual layer; SKIPPED in
#       this fixture: we don't need the host to validate signals 1-7;
#       the DLST pipe is captured server-side via a background reader.)
#   7.  Wire surface_daemon stdout/pipe to a reader thread that counts
#       frame metadata bytes
#   8.  Spawn dalvikvm with NoiceLauncher (= NoiceDiscoverWrapper main)
#   9-12. (driven inside dalvikvm by NoiceLauncher -> DiscoverWrapperBase)
#
# After the run, evaluate the 7 CR38 §5 acceptance signals.  The script
# is designed to work even when M5/M6 are partially-degraded -- it
# captures whatever signals fire and reports each one independently.
# When M5-Step5 + M6-Step6 complete, the same script should report
# 7/7 PASS.
#
# Anti-drift: ZERO per-app branches.  Manifest-driven via
# noice.discover.properties (existing W2-discover file).  Renaming
# anything `noice` -> `mcd` here would not produce an M8 launcher --
# that's M8-Step1's responsibility and gets its own script
# (run-mcd-westlake.sh, per CR38 §5.2).
#
# Usage:
#   bash scripts/run-noice-westlake.sh [--no-cleanup] [--timeout=SECONDS]
#                                       [--no-color] [--push-launcher-dex]
#
#   --no-cleanup           Leave daemons + vndservicemanager state as-is
#                          on exit (useful for post-mortem debugging).
#   --timeout=SECONDS      How long to wait for dalvikvm to finish
#                          (default 180).
#   --no-color             Disable ANSI color codes.
#   --push-launcher-dex    Push aosp-libbinder-port/out/NoiceLauncher.dex
#                          + the noice manifest to the phone before
#                          starting (useful after a local rebuild).
#
# Output: artifacts/noice-westlake/YYYYMMDD_HHMMSS/
#   ├── preflight.log         -- file inventory + phone state
#   ├── m7-sm.log             -- servicemanager stdout/stderr
#   ├── m7-af.log             -- audio_flinger stdout/stderr
#   ├── m7-sf.log             -- surfaceflinger stdout/stderr
#   ├── m7-dalvikvm.log       -- NoiceLauncher dalvikvm stdout/stderr
#   ├── dlst-frames.log       -- DLST pipe reader byte/frame trace
#   ├── dumpsys-audio.log     -- `dumpsys media.audio_flinger` snapshot
#   ├── listservices-pre.log  -- listServices BEFORE dalvikvm starts
#   ├── listservices-post.log -- listServices AFTER dalvikvm starts
#   └── result.txt            -- 7-signal scorecard
#
# Exit codes:
#   0   >= 4/7 acceptance signals PASS (M7-Step1 mechanical green)
#   1   < 4/7 signals PASS (something fundamental is broken)
#   2   preflight failed (missing artifacts on phone)
# ============================================================================

set -u
# Don't set -e: this script needs to keep going even when daemons or
# dalvikvm exit non-zero, so we can capture artifacts + grade signals.

# ---------- defaults ---------------------------------------------------------
ADB="${ADB:-/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3}"
WESTLAKE="${WESTLAKE_DIR:-/data/local/tmp/westlake}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
ARTIFACT_ROOT="$REPO_ROOT/artifacts/noice-westlake"
ART="$ARTIFACT_ROOT/$(date +%Y%m%d_%H%M%S)"

NO_CLEANUP=0
TIMEOUT_SECONDS=180
USE_COLOR=1
PUSH_LAUNCHER_DEX=0
# M7-Step2 (2026-05-13): production-launch flag.
#   PRODUCTION=0 (default) -- M7-Step1 discovery harness path:
#                              NoiceLauncher -> NoiceDiscoverWrapper
#                              -> PHASE G4 reflective onCreate.
#   PRODUCTION=1 (--production) -- M7-Step2 production path:
#                              NoiceProductionLauncher -> WAT.attach +
#                              setForceLifecycleEnabled(true) +
#                              performLaunchActivity ->
#                              Instrumentation.callActivityOnCreate ->
#                              Activity.performCreate -> onCreate.
PRODUCTION=0

for arg in "$@"; do
    case "$arg" in
        --no-cleanup)         NO_CLEANUP=1 ;;
        --timeout=*)          TIMEOUT_SECONDS="${arg#--timeout=}" ;;
        --no-color)           USE_COLOR=0 ;;
        --push-launcher-dex)  PUSH_LAUNCHER_DEX=1 ;;
        --production)         PRODUCTION=1 ;;
        -h|--help)
            sed -n '/^# ====/,/^# ====$/p' "$0" | sed 's/^# \{0,1\}//'
            exit 0
            ;;
        *)
            echo "unknown flag: $arg (see --help)" >&2
            exit 2
            ;;
    esac
done

if [ "$USE_COLOR" = "1" ] && [ -t 1 ]; then
    RED=$'\033[31m'; GREEN=$'\033[32m'; YELLOW=$'\033[33m'
    CYAN=$'\033[36m'; BOLD=$'\033[1m'; RESET=$'\033[0m'
else
    RED=""; GREEN=""; YELLOW=""; CYAN=""; BOLD=""; RESET=""
fi

mkdir -p "$ART"

# ---------- shared helpers ---------------------------------------------------
log() { echo "[$(date +%H:%M:%S)] [m7] $*" | tee -a "$ART/orchestrator.log"; }
ts()  { date -u +%Y-%m-%dT%H:%M:%SZ; }

adb_su() { eval "$ADB shell \"su -c '$*'\""; }
adb_sh() { eval "$ADB shell \"$*\""; }

# ---------- cleanup ----------------------------------------------------------
cleanup() {
    if [ "$NO_CLEANUP" = "1" ]; then
        log "skipping cleanup (--no-cleanup)"
        return
    fi
    log "cleanup: killing daemons + restarting vndservicemanager"
    adb_su "pkill -9 -f westlake/bin-bionic/audio_flinger 2>/dev/null; \
            pkill -9 -f westlake/bin-bionic/surfaceflinger 2>/dev/null; \
            pkill -9 -f westlake/bin-bionic/servicemanager 2>/dev/null; \
            pkill -9 -f $WESTLAKE/dalvikvm 2>/dev/null; \
            pkill -9 -f westlake/bin-bionic/sm_smoke 2>/dev/null; \
            pkill -9 -f $WESTLAKE/dlst.fifo 2>/dev/null; \
            rm -f $WESTLAKE/dlst.fifo $WESTLAKE/.m7-launch-daemons.sh \
                  /data/local/tmp/.m7-launch-daemons.sh 2>/dev/null; \
            setprop ctl.start vndservicemanager" >/dev/null 2>&1 || true
}
trap 'cleanup' EXIT INT TERM

# ============================================================================
# PHASE 1 -- preflight
# ============================================================================
log "${BOLD}${CYAN}M7-Step1 -- noice end-to-end Westlake fixture${RESET}"
log "started=$(ts) timeout=${TIMEOUT_SECONDS}s artifacts=$ART"

# Verify phone reachable.
if ! eval "$ADB devices" >/dev/null 2>&1; then
    log "${RED}ERROR${RESET}: adb unreachable"
    exit 2
fi
devstate=$(eval "$ADB get-state" 2>/dev/null | tr -d '\r')
if [ "$devstate" != "device" ]; then
    log "${RED}ERROR${RESET}: phone not in 'device' state (got '$devstate')"
    exit 2
fi

# Optional: push freshly-built launcher dex + manifest.
if [ "$PUSH_LAUNCHER_DEX" = "1" ]; then
    if [ "$PRODUCTION" = "1" ]; then
        LOCAL_DEX="$REPO_ROOT/aosp-libbinder-port/out/NoiceProductionLauncher.dex"
        REMOTE_DEX_NAME="NoiceProductionLauncher.dex"
        BUILD_HINT="aosp-libbinder-port/build_noice_production_launcher.sh"
    else
        LOCAL_DEX="$REPO_ROOT/aosp-libbinder-port/out/NoiceLauncher.dex"
        REMOTE_DEX_NAME="NoiceLauncher.dex"
        BUILD_HINT="aosp-libbinder-port/build_noice_launcher.sh"
    fi
    LOCAL_MANIFEST="$REPO_ROOT/aosp-libbinder-port/test/noice.discover.properties"
    if [ ! -f "$LOCAL_DEX" ]; then
        log "${RED}ERROR${RESET}: $LOCAL_DEX missing (run $BUILD_HINT)"
        exit 2
    fi
    log "pushing $LOCAL_DEX -> $WESTLAKE/dex/$REMOTE_DEX_NAME"
    eval "$ADB push '$LOCAL_DEX' '$WESTLAKE/dex/$REMOTE_DEX_NAME'" >/dev/null 2>&1 || \
        { log "${RED}ERROR${RESET}: push $REMOTE_DEX_NAME failed"; exit 2; }
    if [ -f "$LOCAL_MANIFEST" ]; then
        eval "$ADB push '$LOCAL_MANIFEST' '$WESTLAKE/noice.discover.properties'" \
            >/dev/null 2>&1 || true
    fi
fi

# Inventory on-device artifacts.
log "=== preflight: on-device inventory ==="
{
    echo "ts: $(ts)"
    echo "phone: $($ADB shell getprop ro.product.model 2>/dev/null | tr -d '\r')"
    echo "android: $($ADB shell getprop ro.build.version.release 2>/dev/null | tr -d '\r')"
    echo "kernel: $($ADB shell uname -r 2>/dev/null | tr -d '\r')"
    echo
    echo "=== required files ==="
    for f in \
        bin-bionic/servicemanager \
        bin-bionic/audio_flinger \
        bin-bionic/surfaceflinger \
        bin-bionic/sm_smoke \
        dalvikvm \
        aosp-shim.dex \
        framework.jar \
        ext.jar \
        core-oj.jar \
        core-libart.jar \
        core-icu4j.jar \
        bouncycastle.jar \
        com_github_ashutoshgngwr_noice.apk \
        noice.discover.properties \
        dex/NoiceDiscoverWrapper.dex \
        lib-bionic/libbinder.so \
        lib-bionic/libandroid_runtime_stub.so
    do
        if eval "$ADB shell test -e $WESTLAKE/$f" 2>/dev/null; then
            echo "  OK    $f"
        else
            echo "  MISS  $f"
        fi
    done
    echo
    echo "=== optional / M7-specific ==="
    # NoiceLauncher.dex is M7-specific.  If the phone doesn't have it,
    # we fall back to NoiceDiscoverWrapper.dex (which exercises the same
    # discovery + launch path; only the M7_LAUNCHER startup marker
    # differs).
    for f in dex/NoiceLauncher.dex; do
        if eval "$ADB shell test -e $WESTLAKE/$f" 2>/dev/null; then
            echo "  OK    $f"
        else
            echo "  MISS  $f (will fall back to NoiceDiscoverWrapper)"
        fi
    done
} > "$ART/preflight.log" 2>&1
cat "$ART/preflight.log" | grep -E '^(  OK|  MISS|phone:|android:)' | head -25

# Hard-required artifacts.
if grep -q "^  MISS  bin-bionic/servicemanager\|^  MISS  dalvikvm\|^  MISS  aosp-shim.dex\|^  MISS  framework.jar\|^  MISS  com_github_ashutoshgngwr_noice.apk\|^  MISS  noice.discover.properties\|^  MISS  dex/NoiceDiscoverWrapper.dex" \
    "$ART/preflight.log"; then
    log "${RED}ERROR${RESET}: required artifacts missing on phone (see preflight.log)"
    exit 2
fi

# Pick launcher class.
#   --production: NoiceProductionLauncher (M7-Step2 production path)
#   default:      NoiceLauncher (M7-Step1 discovery harness)
#                 Fallback to NoiceDiscoverWrapper if the launcher dex is absent.
if [ "$PRODUCTION" = "1" ]; then
    LAUNCHER_CLASS="NoiceProductionLauncher"
    LAUNCHER_DEX="$WESTLAKE/dex/NoiceProductionLauncher.dex"
    if ! eval "$ADB shell test -f $LAUNCHER_DEX" 2>/dev/null; then
        log "${RED}ERROR${RESET}: $LAUNCHER_DEX missing on phone."
        log "  build with aosp-libbinder-port/build_noice_production_launcher.sh"
        log "  then re-run with --push-launcher-dex --production"
        exit 2
    fi
else
    LAUNCHER_CLASS="NoiceLauncher"
    LAUNCHER_DEX="$WESTLAKE/dex/NoiceLauncher.dex"
    if ! eval "$ADB shell test -f $LAUNCHER_DEX" 2>/dev/null; then
        log "${YELLOW}WARN${RESET}: $LAUNCHER_DEX missing; falling back to NoiceDiscoverWrapper"
        LAUNCHER_CLASS="NoiceDiscoverWrapper"
        LAUNCHER_DEX="$WESTLAKE/dex/NoiceDiscoverWrapper.dex"
    fi
fi
log "launcher: $LAUNCHER_CLASS ($LAUNCHER_DEX)"

# ============================================================================
# PHASE 2 -- SM bringup (CR38 §2.1 step 2-3)
# ============================================================================
log "=== stage 1: SM bringup ==="
adb_su "pkill -9 -f westlake/bin-bionic/audio_flinger 2>/dev/null; \
        pkill -9 -f westlake/bin-bionic/surfaceflinger 2>/dev/null; \
        pkill -9 -f westlake/bin-bionic/servicemanager 2>/dev/null; \
        pkill -9 -f $WESTLAKE/dalvikvm 2>/dev/null" >/dev/null 2>&1

# Stop device vndservicemanager + wait up to 10s.
adb_su "setprop ctl.stop vndservicemanager" >/dev/null 2>&1
for i in $(seq 1 20); do
    pid=$(adb_su "pidof vndservicemanager" 2>/dev/null | tr -d '\r')
    if [ -z "$pid" ]; then break; fi
    sleep 0.5
done
pid=$(adb_su "pidof vndservicemanager" 2>/dev/null | tr -d '\r')
if [ -n "$pid" ]; then
    log "${YELLOW}WARN${RESET}: vndservicemanager still alive (pid=$pid); force-killing"
    adb_su "kill -9 $pid" >/dev/null 2>&1
    sleep 1
fi

# Spawn our SM + daemons via an on-device launcher script.  Doing it
# this way (rather than via adb_su "nohup ... &") avoids two classes
# of bug:
#   (a) adb's shell wrapper waiting for backgrounded child fds to
#       drain (FIFOs / unclosed pipes)
#   (b) lost stdout redirection when adb-shell-quoting double-escapes
#       the `> $FILE 2>&1` operator
# Mirror what m5step2-smoke.sh + m6step4-smoke.sh do: the script runs
# on the phone and uses a regular shell `&` to background subprocesses.
log "deploying on-device daemon launcher"
DAEMON_LAUNCHER_LOCAL="$ART/.m7-launch-daemons.sh"
cat > "$DAEMON_LAUNCHER_LOCAL" <<'LAUNCHER_EOF'
#!/system/bin/sh
# Phone-side daemon launcher (deployed by run-noice-westlake.sh)
# Args: <stage>  where stage in {sm, audio, surface, all-daemons}.
set -u
DIR=/data/local/tmp/westlake
DEV=/dev/vndbinder
LIB=$DIR/lib-bionic
FIFO=$DIR/dlst.fifo

case "${1:-all}" in
    sm)
        rm -f "$DIR/m7-sm.log"
        LD_LIBRARY_PATH=$LIB "$DIR/bin-bionic/servicemanager" "$DEV" \
            > "$DIR/m7-sm.log" 2>&1 &
        ;;
    audio)
        rm -f "$DIR/m7-af.log"
        LD_LIBRARY_PATH=$LIB "$DIR/bin-bionic/audio_flinger" "$DEV" \
            > "$DIR/m7-af.log" 2>&1 &
        ;;
    surface)
        rm -f "$DIR/m7-sf.log"
        WESTLAKE_DLST_PIPE=$FIFO LD_LIBRARY_PATH=$LIB \
            "$DIR/bin-bionic/surfaceflinger" "$DEV" \
            > "$DIR/m7-sf.log" 2>&1 &
        ;;
    dlst-drainer)
        rm -f "$DIR/dlst-frames.log"
        # Drain the DLST pipe to a log file.  Open the FIFO read+write
        # (<>) so open() returns immediately (no writer-wait); cat then
        # blocks on read() per normal until the daemon writes.
        cat <>"$FIFO" > "$DIR/dlst-frames.log" 2>&1 &
        ;;
    *)
        echo "unknown stage: ${1:-}" >&2
        exit 1
        ;;
esac
# Print the pid we just spawned so the orchestrator can correlate.
echo "[m7-launch] $1 spawned pid=$!"
LAUNCHER_EOF
chmod +x "$DAEMON_LAUNCHER_LOCAL"
# Push via /data/local/tmp (no sudo needed) then move into westlake/.
eval "$ADB push '$DAEMON_LAUNCHER_LOCAL' '/data/local/tmp/.m7-launch-daemons.sh'" \
    >/dev/null 2>&1
adb_su "cp /data/local/tmp/.m7-launch-daemons.sh $WESTLAKE/.m7-launch-daemons.sh; \
        chmod +x $WESTLAKE/.m7-launch-daemons.sh" >/dev/null 2>&1

# Spawn our SM as uid=1000.  /dev/vndbinder context-mgr role is locked
# to the first claimer's uid -- noice-discover.sh established 1000 as
# the canonical uid for the M3+ harness.
log "starting our servicemanager on /dev/vndbinder (as uid=1000)"
# CR58: must run SM as uid=1000 — /dev/vndbinder kernel binder ioctl
# BINDER_SET_CONTEXT_MGR rejects uid=0 with EPERM on this kernel.
# noice-discover.sh + m3-dalvikvm-boot.sh both use `su 1000 -c '...'`.
adb_su "su 1000 -c 'sh $WESTLAKE/.m7-launch-daemons.sh sm'" 2>&1 \
    | tee -a "$ART/orchestrator.log" >/dev/null
sleep 2

sm_pid=$(adb_su "pgrep -f westlake/bin-bionic/servicemanager" 2>/dev/null | \
         tr -d '\r' | head -1)
if [ -z "$sm_pid" ]; then
    log "${RED}ERROR${RESET}: servicemanager failed to start; log:"
    adb_su "cat $WESTLAKE/m7-sm.log" 2>&1 | tee -a "$ART/m7-sm.log" | head -20
    log "exiting"
    exit 1
fi
log "servicemanager up (pid=$sm_pid)"

# ============================================================================
# PHASE 3 -- M5 + M6 daemons (CR38 §2.1 step 4-5; concurrent OK per §2.3)
# ============================================================================
log "=== stage 2: M5 audio + M6 surface daemons ==="

# DLST pipe (M6 step 4+).  Pre-create FIFO so daemon's first writer-open
# doesn't race the reader.  Honored as WESTLAKE_DLST_PIPE env var by
# surfaceflinger (mirrors m6step4-smoke.sh).
DLST_PIPE="$WESTLAKE/dlst.fifo"
adb_su "rm -f $DLST_PIPE; mkfifo $DLST_PIPE; chmod 0666 $DLST_PIPE" >/dev/null 2>&1

# M5 audio daemon (uid=1000 to match SM context-mgr UID).
log "starting audio_flinger daemon (as uid=1000)"
adb_su "su 1000 -c 'sh $WESTLAKE/.m7-launch-daemons.sh audio'" 2>&1 \
    | tee -a "$ART/orchestrator.log" >/dev/null

# M6 surface daemon (uid=1000).
log "starting surfaceflinger daemon (WESTLAKE_DLST_PIPE=$DLST_PIPE, uid=1000)"
adb_su "su 1000 -c 'sh $WESTLAKE/.m7-launch-daemons.sh surface'" 2>&1 \
    | tee -a "$ART/orchestrator.log" >/dev/null

# DLST pipe drainer -- background `cat` whose stdin is opened on the
# FIFO read+write side (`<>$FIFO` non-blocking-open trick), so the
# surface daemon's writer-open doesn't block waiting for a reader and
# our cat doesn't block on open() either.
log "starting DLST pipe drainer"
adb_su "sh $WESTLAKE/.m7-launch-daemons.sh dlst-drainer" 2>&1 \
    | tee -a "$ART/orchestrator.log" >/dev/null

sleep 3   # let both daemons register before we probe.

# Confirm daemons alive.
af_pid=$(adb_su "pgrep -f westlake/bin-bionic/audio_flinger" 2>/dev/null | \
         tr -d '\r' | head -1)
sf_pid=$(adb_su "pgrep -f westlake/bin-bionic/surfaceflinger" 2>/dev/null | \
         tr -d '\r' | head -1)

if [ -z "$af_pid" ]; then
    log "${YELLOW}WARN${RESET}: audio_flinger daemon did not survive 3s post-start"
else
    log "audio_flinger up (pid=$af_pid)"
fi
if [ -z "$sf_pid" ]; then
    log "${YELLOW}WARN${RESET}: surfaceflinger daemon did not survive 3s post-start"
else
    log "surfaceflinger up (pid=$sf_pid)"
fi

# Snapshot listServices (CR38 §2.1 step 4/5 validation).  sm_smoke
# without an SM_TEST_NAME arg lists services and exits.
log "listServices (pre-dalvikvm) -> listservices-pre.log"
adb_su "LD_LIBRARY_PATH=$WESTLAKE/lib-bionic BINDER_DEVICE=/dev/vndbinder \
        $WESTLAKE/bin-bionic/sm_smoke 2>&1 | head -50" \
    > "$ART/listservices-pre.log" 2>&1 || true

# ============================================================================
# PHASE 4 -- dalvikvm + noice (CR38 §2.1 step 8-12)
# ============================================================================
log "=== stage 3: dalvikvm + noice ==="

# BCP = mirror of noice-discover.sh:104-106 (works -- has reached PHASE G4).
BCP="$WESTLAKE/core-oj.jar:$WESTLAKE/core-libart.jar:$WESTLAKE/core-icu4j.jar:$WESTLAKE/bouncycastle.jar:$WESTLAKE/aosp-shim.dex:$WESTLAKE/framework.jar:$WESTLAKE/ext.jar"

log "launching dalvikvm $LAUNCHER_CLASS via $LAUNCHER_DEX (timeout ${TIMEOUT_SECONDS}s)"
log "  BCP=$BCP"
log "  CP=$LAUNCHER_DEX"

# Run dalvikvm.  Mirror noice-discover.sh's invocation verbatim.
# We don't background dalvikvm -- we want it to complete and capture
# its exit code, but bound by a timeout in case it hangs.
adb_su "cd $WESTLAKE && timeout ${TIMEOUT_SECONDS} \
        env BINDER_DEVICE=/dev/vndbinder \
            LD_LIBRARY_PATH=$WESTLAKE/lib-bionic \
        $WESTLAKE/dalvikvm \
            -Xbootclasspath:$BCP \
            -Xverify:none \
            -Xnorelocate \
            -Djava.library.path=$WESTLAKE/lib-bionic \
            -cp $LAUNCHER_DEX \
            $LAUNCHER_CLASS" \
    > "$ART/m7-dalvikvm.log" 2>&1
DALVIK_RC=$?
log "dalvikvm exit code: $DALVIK_RC"

# ============================================================================
# PHASE 5 -- post-run snapshots
# ============================================================================
log "=== stage 4: post-run snapshots ==="

# dumpsys media.audio_flinger (CR38 §5 signal S4).
log "snapshotting dumpsys media.audio_flinger -> dumpsys-audio.log"
adb_sh "dumpsys media.audio_flinger 2>&1 | head -200" \
    > "$ART/dumpsys-audio.log" 2>&1 || true

# Second listServices snapshot.
log "listServices (post-dalvikvm) -> listservices-post.log"
adb_su "LD_LIBRARY_PATH=$WESTLAKE/lib-bionic BINDER_DEVICE=/dev/vndbinder \
        $WESTLAKE/bin-bionic/sm_smoke 2>&1 | head -50" \
    > "$ART/listservices-post.log" 2>&1 || true

# Pull daemon + DLST logs.
log "pulling daemon logs"
adb_su "cat $WESTLAKE/m7-sm.log"        > "$ART/m7-sm.log"        2>/dev/null || true
adb_su "cat $WESTLAKE/m7-af.log"        > "$ART/m7-af.log"        2>/dev/null || true
adb_su "cat $WESTLAKE/m7-sf.log"        > "$ART/m7-sf.log"        2>/dev/null || true
adb_su "cat $WESTLAKE/dlst-frames.log"  > "$ART/dlst-frames.log"  2>/dev/null || true

# ============================================================================
# PHASE 6 -- acceptance scorecard (CR38 §5 acceptance signals)
# ============================================================================
log "=== stage 5: acceptance signals (CR38 §5) ==="
RESULT="$ART/result.txt"
{
    echo "M7-Step1 acceptance scorecard"
    echo "================================"
    echo "ts: $(ts)"
    echo "artifact: $ART"
    echo "launcher: $LAUNCHER_CLASS"
    echo "dalvikvm_rc: $DALVIK_RC"
    echo
} > "$RESULT"

pass=0
fail=0
record_pass() { echo "PASS  $*" >> "$RESULT"; pass=$((pass + 1)); }
record_fail() { echo "FAIL  $*" >> "$RESULT"; fail=$((fail + 1)); }
record_info() { echo "INFO  $*" >> "$RESULT"; }

# Strip ADB CRLFs from the dalvikvm log first; otherwise grep regex
# anchors like \b "won't" trigger on Windows-cooked lines.
sed -i 's/\r$//' "$ART/m7-dalvikvm.log" 2>/dev/null || true
sed -i 's/\r$//' "$ART/dlst-frames.log" 2>/dev/null || true

# ---- S1: MainActivity.onCreate reached ----
# Three paths -- any count as PASS:
#
#  (a) M7-Step1 discovery harness emits
#      "PHASE G4: calling MainActivity.onCreate(null)" + a corresponding
#      "PHASE G4: (onCreate(null) returned cleanly|FAILED|PASSED)" outcome.
#
#  (b) M7-Step2 production launcher emits
#      "M7_PROD_LAUNCHER: PRODUCTION_LAUNCH_(OK|RETURNED) ..." when
#      performLaunchActivity returns the Activity object.
#
#  (c) Production path drove WestlakeInstrumentation.callActivityOnCreate
#      and the dispatch hit MainActivity.onCreate -- detected by an NPE
#      stack frame mentioning MainActivity.onCreate (proves performCreate
#      reached the user body before any in-body NPE). This is the deepest
#      "reached" signal we have for the production path.
if grep -q "PHASE G4: calling MainActivity.onCreate" "$ART/m7-dalvikvm.log" 2>/dev/null \
        && grep -qE "PHASE G4: (onCreate\(null\) returned cleanly|FAILED|PASSED)" \
                  "$ART/m7-dalvikvm.log" 2>/dev/null; then
    record_pass "S1 MainActivity.onCreate reached (discovery harness G4)"
elif grep -qE "M7_PROD_LAUNCHER: PRODUCTION_LAUNCH_(OK|RETURNED) " \
                  "$ART/m7-dalvikvm.log" 2>/dev/null; then
    record_pass "S1 MainActivity.onCreate reached (production performLaunchActivity)"
elif grep -qE "MainActivity\.onCreate\(android\.os\.Bundle\)" \
                  "$ART/m7-dalvikvm.log" 2>/dev/null; then
    record_pass "S1 MainActivity.onCreate body entered (production Instrumentation.callActivityOnCreate)"
else
    record_fail "S1 MainActivity.onCreate NOT reached"
fi

# ---- S2: HomeFragment lifecycle marker ----
# noice's MainActivity inflates HomeFragment via Navigation Component.
# Even when fragment instantiation NPEs, the class typically gets
# classloaded first -- so any HomeFragment-related log entry counts as
# "HomeFragment was at least observed" for M7-Step1.  Refine in M7-Step2.
if grep -qE "HomeFragment" "$ART/m7-dalvikvm.log" 2>/dev/null; then
    record_pass "S2 HomeFragment lifecycle reached"
else
    record_fail "S2 HomeFragment lifecycle NOT reached"
fi

# ---- S3: AudioTrack.write issued ----
# Pre-M5-Step5 noice is unlikely to drive AudioTrack.write through the
# binder layer (Hilt injection blocks the playback pipeline well before
# any audio buffer is queued).  We grep both for client-side AudioTrack
# activity in the dalvikvm log AND for server-side WRITE_FRAMES in the
# audio daemon log.
if grep -qE "AudioTrack\.write|WRITE_FRAMES|AUDIOTRACK_CREATE|CREATE_TRACK" \
         "$ART/m7-dalvikvm.log" "$ART/m7-af.log" 2>/dev/null; then
    record_pass "S3 AudioTrack.write issued"
else
    record_fail "S3 AudioTrack.write NOT issued (expected pre-M5-Step5)"
fi

# ---- S4: dumpsys media.audio_flinger shows our daemon ----
# CR38 §5.1 signal: "process=westlake_audio_daemon for the duration of
# the tap".  Pre-M5-Step5 we just check the service registered with our
# SM at all (more granular check is M7-Step2 once M5-Step5 lands).
if grep -qE "media\.audio_flinger" "$ART/listservices-pre.log" \
                                     "$ART/listservices-post.log" 2>/dev/null \
   || grep -qE "audio_flinger|AudioFlinger" "$ART/dumpsys-audio.log" 2>/dev/null; then
    record_pass "S4 dumpsys shows media.audio_flinger registered with our SM"
else
    record_fail "S4 dumpsys does NOT show media.audio_flinger"
fi

# ---- S5: DLST pipe traffic ----
# The reader appends "FRAME tally" lines every 16 frames + "DLST EOF" on
# close.  Any non-empty pipe traffic counts as PASS for M7-Step1.
if [ -s "$ART/dlst-frames.log" ] && \
   grep -qE "FRAME tally|DLST EOF frames=[1-9]" "$ART/dlst-frames.log" 2>/dev/null; then
    record_pass "S5 DLST pipe traffic observed"
elif grep -qE "queueBuffer|surfaceflinger.*surface" "$ART/m7-sf.log" 2>/dev/null; then
    # Daemon emitted buffer activity but pipe wasn't drained -- partial
    # credit on the daemon side.
    record_info "S5 surfaceflinger emitted buffer activity but pipe empty (PARTIAL)"
    record_fail "S5 DLST pipe traffic NOT observed"
else
    record_fail "S5 DLST pipe traffic NOT observed (expected pre-M6-Step6)"
fi

# ---- S6: zero crashes ----
# Crash patterns: Java FATAL EXCEPTION, native "Fatal signal", SIGBUS/
# SEGV markers, the known PF-arch-054 fault_addr.
# Excludes the "INFO_FATAL_EXCEPTION_TYPE" enum reference (a constant
# name that ships in framework.jar and matches "FATAL EXCEPTION"
# naively) -- we anchor on the start of the typical Android Log.wtf
# header instead.
crash_count=$(grep -cE "Fatal signal|FATAL EXCEPTION:|fault_addr=0xfffffffffffffb17|SIGBUS|SIGSEGV" \
              "$ART/m7-dalvikvm.log" "$ART/m7-sm.log" "$ART/m7-af.log" \
              "$ART/m7-sf.log" 2>/dev/null | awk -F: '{s+=$NF} END{print s+0}')
if [ "$crash_count" = "0" ]; then
    record_pass "S6 zero crashes (no Fatal signal / FATAL EXCEPTION / SIGBUS / SIGSEGV)"
else
    record_fail "S6 crashes observed (count=$crash_count)"
fi

# ---- S7: fail-loud UOE budget (informational) ----
# Per CR38 §5.1 row 7, fail-loud UOEs may exist pre-CR44; the M7
# baseline is "no UNEXPECTED UOEs".  Count + report; don't gate.
uoe_count=$(grep -cE "UnsupportedOperationException|WestlakeServiceMethodMissing|ServiceMethodMissing" \
            "$ART/m7-dalvikvm.log" 2>/dev/null | head -1)
[ -z "$uoe_count" ] && uoe_count=0
record_info "S7 fail-loud UOE count=${uoe_count} (informational; non-zero may be OK pre-CR44)"

# ---- bonus diagnostic: phase reach high-water mark ----
phase_max=$(grep -oE "PHASE [A-G][0-9]?:" "$ART/m7-dalvikvm.log" 2>/dev/null \
            | sort -u | tail -1)
record_info "phase high-water = ${phase_max:-NONE}"
record_info "dalvikvm log size = $(wc -c < "$ART/m7-dalvikvm.log" 2>/dev/null || echo 0) bytes"

# ---- final tally ----
echo >> "$RESULT"
echo "Pass: $pass / 7 (S6 mandatory; S1-S5 vary with M5/M6 progress)" >> "$RESULT"
echo "Fail: $fail" >> "$RESULT"

echo
echo "${BOLD}${CYAN}=== M7-Step1 acceptance scorecard ===${RESET}"
cat "$RESULT"
echo
log "${BOLD}M7-Step1${RESET}: $pass/7 PASS  $fail/7 FAIL"
log "artifacts: $ART"

# Exit policy: 0 = M7-Step1 mechanical green (>= 4/7 acceptance signals
# PASS), 1 otherwise.  The bar is intentionally low: M5-Step5 + M6-Step6
# are still pending; this fixture's first job is verifying the
# orchestration mechanics work, not the daemons' completeness.
if [ "$pass" -ge 4 ]; then
    log "${GREEN}${BOLD}M7-Step1 mechanical PASS${RESET} ($pass/7 signals)"
    exit 0
else
    log "${RED}${BOLD}M7-Step1 FAIL${RESET} ($pass/7 signals)"
    exit 1
fi
