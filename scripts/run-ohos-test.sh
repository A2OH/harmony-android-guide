#!/usr/bin/env bash
# ============================================================================
# run-ohos-test.sh — Phase 2 OHOS MVP single-command driver
#                    (#624 / PF-ohos-mvp-011)
#
# Mirrors the Phase 1 patterns of scripts/binder-pivot-regression.sh and
# scripts/run-noice-westlake.sh but targets the rk3568 OHOS board via
# hdc.exe (not adb). The board's serial is wired in by default
# (dd011a414436314130101250040eac00); override via $HDC_SERIAL.
#
# Subcommands:
#   status           hdc list targets + board health check (kernel, /data
#                    free, dalvikvm presence, /dev/binder presence).
#   push-bcp         push boot classpath files (boot-aosp-shim.{art,oat,vdex}
#                    + aosp-shim.dex) into $BOARD_DIR/bcp/.
#   hello            compile :hello (Java), d8 → HelloOhos.dex, push,
#                    invoke dalvikvm and capture marker. (#616 / MVP-0)
#   trivial-activity build :trivial-activity APK, push, run, capture marker
#                    from hilog. (#619 / MVP-1)
#
# Usage:
#   scripts/run-ohos-test.sh <subcommand> [args]
#
# IMPORTANT: hdc.exe is a Windows binary running under WSL and cannot
# resolve WSL UNC paths (\\wsl$\...). Every file it touches must live
# under the Windows-visible staging directory $WINSTAGE
# (/mnt/c/Users/dspfa/Dev/ohos-tools/stage/). The script copies files
# into that staging area before invoking `hdc file send`.
#
# Environment overrides:
#   HDC          path to hdc.exe (default Windows path under /mnt/c)
#   HDC_SERIAL   serial of OHOS board (default rk3568 dd011a4...)
#   WINSTAGE     Windows-visible staging dir
#   BOARD_DIR    on-device path (default /data/local/tmp/westlake)
#   REPO_ROOT    repo root (auto-detected from script location)
#
# Exit codes:
#   0  subcommand completed cleanly (or, for `hello`/`trivial-activity`,
#      the artifact was built + pushed + invoked; the runtime may still
#      SIGSEGV — that's tracked separately under #614 and does not fail
#      the harness)
#   1  subcommand failed before reaching the device (build failure,
#      missing artifact, hdc not reachable, etc.)
#   2  usage error / unknown subcommand
# ============================================================================

set -u

HDC="${HDC:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}"
HDC_SERIAL="${HDC_SERIAL:-dd011a414436314130101250040eac00}"
WINSTAGE="${WINSTAGE:-/mnt/c/Users/dspfa/Dev/ohos-tools/stage}"
BOARD_DIR="${BOARD_DIR:-/data/local/tmp/westlake}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="${REPO_ROOT:-$(cd "$SCRIPT_DIR/.." && pwd)}"
GRADLE_DIR="$REPO_ROOT/ohos-tests-gradle"
ARTIFACT_ROOT="$REPO_ROOT/artifacts/ohos-mvp"
TS="$(date +%Y%m%d_%H%M%S)"

# Sources of BCP files (#624 acceptance: "verify they exist at
# dalvik-port/build-ohos-aarch64/... if not there, look in ohos-deploy/").
# We check ohos-deploy/ first (where they're already staged today,
# 2026-05-14), then fall back to the build dir.
BCP_SOURCES=(
    "$REPO_ROOT/ohos-deploy"
    "$REPO_ROOT/dalvik-port/build-ohos-aarch64"
)
BCP_FILES=(
    "boot-aosp-shim.art"
    "boot-aosp-shim.oat"
    "boot-aosp-shim.vdex"
    "aosp-shim.dex"
)

# Android SDK / tooling
ANDROID_SDK="${ANDROID_SDK:-/home/dspfac/android-sdk}"
D8="${D8:-$ANDROID_SDK/build-tools/34.0.0/d8}"

# ---------- color + log helpers ---------------------------------------------
if [ -t 1 ]; then
    RED=$'\033[31m'; GREEN=$'\033[32m'; YELLOW=$'\033[33m'
    CYAN=$'\033[36m'; BOLD=$'\033[1m'; RESET=$'\033[0m'
else
    RED=""; GREEN=""; YELLOW=""; CYAN=""; BOLD=""; RESET=""
fi

log()  { echo "[$(date +%H:%M:%S)] [ohos] $*"; }
warn() { echo "${YELLOW}[$(date +%H:%M:%S)] [ohos][WARN]${RESET} $*" >&2; }
err()  { echo "${RED}[$(date +%H:%M:%S)] [ohos][ERR]${RESET} $*" >&2; }
ok()   { echo "${GREEN}[$(date +%H:%M:%S)] [ohos][OK]${RESET} $*"; }

# ---------- hdc helpers ------------------------------------------------------
# We always pin the serial via `-t <serial>` to defeat multi-board ambiguity.
# hdc.exe runs on Windows and exits with status 0 even when the command
# fails on-device; callers check stdout/stderr to discriminate.

hdc() { "$HDC" -t "$HDC_SERIAL" "$@"; }

hdc_shell() {
    # Single-string shell command — quoted twice (once by us, once by hdc).
    hdc shell "$*"
}

# Send a file from the WSL filesystem to the device. The file is first
# copied to $WINSTAGE so hdc.exe can read it (hdc.exe runs as a Windows
# binary and resolves relative-looking paths against its WSL UNC cwd —
# `\\wsl.localhost\...\` — which fails). We convert the staged path
# via `wslpath -w` to a proper `C:\...` form before passing to hdc.
# $1 = host file (WSL path), $2 = on-device path.
hdc_send() {
    local src="$1" dst="$2"
    if [ ! -f "$src" ]; then
        err "hdc_send: source missing: $src"
        return 1
    fi
    local stagename
    stagename="$(basename "$src")"
    mkdir -p "$WINSTAGE"
    cp -f "$src" "$WINSTAGE/$stagename"
    local winsrc
    winsrc="$(wslpath -w "$WINSTAGE/$stagename")"
    log "  send: $stagename -> $dst"
    local sendlog
    sendlog="$(hdc file send "$winsrc" "$dst" 2>&1)"
    if ! echo "$sendlog" | grep -q "FileTransfer finish"; then
        err "hdc file send failed: $stagename -> $dst"
        err "  hdc said: $sendlog"
        return 1
    fi
}

# ---------- subcommand: status ----------------------------------------------
cmd_status() {
    log "${BOLD}== OHOS board status ==${RESET}"
    if [ ! -x "$HDC" ]; then
        err "hdc.exe not found / not executable: $HDC"
        return 1
    fi
    log "hdc binary: $HDC"
    log "target serial: $HDC_SERIAL"
    log ""
    log "${CYAN}-- hdc list targets --${RESET}"
    "$HDC" list targets
    log ""
    log "${CYAN}-- uname -a --${RESET}"
    hdc_shell "uname -a"
    log ""
    log "${CYAN}-- /etc/os-release (head) --${RESET}"
    hdc_shell "cat /etc/os-release 2>/dev/null | head -10"
    log ""
    log "${CYAN}-- /data + /system free --${RESET}"
    hdc_shell "df -h /data /system 2>/dev/null"
    log ""
    log "${CYAN}-- binder devices --${RESET}"
    hdc_shell "ls -l /dev/binder /dev/hwbinder /dev/vndbinder 2>/dev/null"
    log ""
    log "${CYAN}-- hilog presence --${RESET}"
    hdc_shell "ls -l /system/bin/hilog 2>/dev/null"
    log ""
    log "${CYAN}-- $BOARD_DIR contents --${RESET}"
    hdc_shell "ls -la $BOARD_DIR 2>/dev/null || echo '(missing)'"
    log ""
    log "${CYAN}-- dalvikvm presence --${RESET}"
    hdc_shell "ls -la $BOARD_DIR/dalvikvm 2>/dev/null || echo '(no dalvikvm staged yet)'"
    ok "status complete"
}

# ---------- subcommand: push-bcp --------------------------------------------
# Locate the four BCP files (boot-aosp-shim.{art,oat,vdex} + aosp-shim.dex)
# and push them to $BOARD_DIR/bcp/. Tries each directory in $BCP_SOURCES in
# order.

_find_bcp_file() {
    local name="$1" src
    for src in "${BCP_SOURCES[@]}"; do
        if [ -f "$src/$name" ]; then
            echo "$src/$name"
            return 0
        fi
    done
    return 1
}

cmd_push_bcp() {
    log "${BOLD}== push BCP files -> $BOARD_DIR/bcp/ ==${RESET}"
    local found=0 missing=0 path
    declare -a paths=()
    for f in "${BCP_FILES[@]}"; do
        if path="$(_find_bcp_file "$f")"; then
            paths+=("$path")
            log "  found: $path"
            found=$((found + 1))
        else
            err "  missing: $f (searched: ${BCP_SOURCES[*]})"
            missing=$((missing + 1))
        fi
    done
    if [ "$missing" -gt 0 ]; then
        err "$missing BCP file(s) missing — cannot continue"
        return 1
    fi
    hdc_shell "mkdir -p $BOARD_DIR/bcp" >/dev/null 2>&1
    for path in "${paths[@]}"; do
        hdc_send "$path" "$BOARD_DIR/bcp/$(basename "$path")" || return 1
    done
    log ""
    log "${CYAN}-- on-device verification --${RESET}"
    hdc_shell "ls -la $BOARD_DIR/bcp"
    ok "BCP push complete ($found files)"
}

# ---------- subcommand: hello ------------------------------------------------
# Builds com.westlake.ohostests.hello.HelloOhos -> classes/ -> HelloOhos.dex
# via d8, pushes the dex, and invokes dalvikvm. Until #614 is fixed the
# dalvikvm invocation SIGSEGVs at VM init; this is expected and does not
# fail the script.

cmd_hello() {
    local outdir="$ARTIFACT_ROOT/mvp0-hello/$TS"
    mkdir -p "$outdir"
    log "${BOLD}== MVP-0 hello (#616) -> $outdir ==${RESET}"

    log "[1/5] gradle :hello:assemble"
    if ! (cd "$GRADLE_DIR" && ./gradlew :hello:assemble --no-daemon -q) \
            > "$outdir/gradle.log" 2>&1; then
        err "gradle build failed — see $outdir/gradle.log"
        return 1
    fi
    local classes="$GRADLE_DIR/hello/build/classes/java/main"
    if [ ! -f "$classes/com/westlake/ohostests/hello/HelloOhos.class" ]; then
        err "HelloOhos.class missing under $classes"
        return 1
    fi
    ok "gradle build complete"

    log "[2/5] d8 -> HelloOhos.dex"
    if [ ! -x "$D8" ]; then
        err "d8 not found at $D8 (set \$D8 or install build-tools/34.0.0)"
        return 1
    fi
    local dexdir="$outdir/dex"
    mkdir -p "$dexdir"
    if ! "$D8" --output "$dexdir" \
            "$classes/com/westlake/ohostests/hello/HelloOhos.class" \
            > "$outdir/d8.log" 2>&1; then
        err "d8 failed — see $outdir/d8.log"
        return 1
    fi
    # d8 names the output classes.dex; rename for clarity.
    mv "$dexdir/classes.dex" "$dexdir/HelloOhos.dex"
    ok "dex emitted: $(du -b "$dexdir/HelloOhos.dex" | awk '{print $1}') bytes"

    log "[3/5] push -> $BOARD_DIR/HelloOhos.dex"
    hdc_shell "mkdir -p $BOARD_DIR" >/dev/null 2>&1
    hdc_send "$dexdir/HelloOhos.dex" "$BOARD_DIR/HelloOhos.dex" || return 1

    log "[4/5] invoke dalvikvm"
    # #614 (PF-ohos-mvp-001): VM init needs an explicit bootclasspath
    # (core-kitkat.jar) plus our DirectPrintStream jar to bypass the
    # broken System.<clinit>/Libcore.os path. ANDROID_ROOT lets the VM
    # locate dexopt at ${ANDROID_ROOT}/bin/dexopt. The user dex is
    # appended to BCP because our minimal launcher.cpp doesn't wire up
    # a PathClassLoader for -cp.
    local bcp="/data/local/tmp/westlake/bcp/core-kitkat.jar"
    bcp="${bcp}:/data/local/tmp/westlake/bcp/direct-print-stream.jar"
    bcp="${bcp}:${BOARD_DIR}/HelloOhos.dex"
    local cmd="ANDROID_ROOT=${BOARD_DIR} /data/local/tmp/dalvikvm"
    cmd="$cmd -Xbootclasspath:${bcp}"
    cmd="$cmd com.westlake.ohostests.hello.HelloOhos arg1 arg2"
    hdc_shell "$cmd" > "$outdir/dalvikvm.stdout" 2> "$outdir/dalvikvm.stderr" || {
        warn "dalvikvm exited non-zero — capturing output regardless"
        warn "(expected if VM aborts after main returns)"
    }
    log "  stdout: $outdir/dalvikvm.stdout"
    log "  stderr: $outdir/dalvikvm.stderr"

    log "[5/5] grade marker"
    local marker="westlake-dalvik on OHOS — main reached"
    if grep -qF "$marker" "$outdir/dalvikvm.stdout" 2>/dev/null; then
        ok "${GREEN}${BOLD}MVP-0 PASS${RESET}: marker found"
        echo "PASS" > "$outdir/result.txt"
        return 0
    else
        warn "marker NOT found — runtime probably still SIGSEGVs (#614)"
        warn "harness mechanics OK; see $outdir/ for evidence"
        echo "BLOCKED_ON_614" > "$outdir/result.txt"
        # Return 0: per #616 the harness is the deliverable; runtime is
        # a different agent's job.
        return 0
    fi
}

# ---------- subcommand: trivial-activity ------------------------------------
# Builds the :trivial-activity APK, pushes it, attempts to launch via
# `aa start` (OHOS) or via dalvikvm-managed PathClassLoader bootstrap if
# `aa` is unavailable, and greps hilog for the onCreate marker.
#
# Note: the actual launch path under Westlake (PathClassLoader +
# WestlakeLauncher) is still being wired up for the OHOS board (Phase 2
# milestone M8). For now this subcommand only verifies the APK builds,
# pushes, and surfaces hilog-grep wiring — it is fail-soft on launch
# failure, like the `hello` subcommand.

cmd_trivial_activity() {
    local outdir="$ARTIFACT_ROOT/mvp1-trivial/$TS"
    mkdir -p "$outdir"
    log "${BOLD}== MVP-1 trivial-activity (#619) -> $outdir ==${RESET}"

    log "[1/5] gradle :trivial-activity:assembleDebug"
    if ! (cd "$GRADLE_DIR" && ./gradlew :trivial-activity:assembleDebug --no-daemon -q) \
            > "$outdir/gradle.log" 2>&1; then
        err "gradle build failed — see $outdir/gradle.log"
        return 1
    fi
    local apk="$GRADLE_DIR/trivial-activity/build/outputs/apk/debug/trivial-activity-debug.apk"
    if [ ! -f "$apk" ]; then
        err "APK not produced: $apk"
        return 1
    fi
    ok "APK built: $(du -b "$apk" | awk '{print $1}') bytes"

    log "[2/5] push -> $BOARD_DIR/OhosTrivialActivity.apk"
    hdc_shell "mkdir -p $BOARD_DIR" >/dev/null 2>&1
    hdc_send "$apk" "$BOARD_DIR/OhosTrivialActivity.apk" || return 1

    log "[3/5] verify on-device"
    hdc_shell "ls -la $BOARD_DIR/OhosTrivialActivity.apk" | tee "$outdir/on-device-ls.log"

    log "[4/5] attempt launch (best-effort; full path lands with M8)"
    # Try the dalvikvm-direct PathClassLoader path first — matches what
    # the Westlake launcher will eventually do. The dex name passed to
    # dalvikvm is the APK itself (Dalvik happily reads classes.dex out
    # of a ZIP).
    local cmd="cd $BOARD_DIR && ./dalvikvm -cp OhosTrivialActivity.apk com.westlake.ohostests.trivial.MainActivity"
    hdc_shell "$cmd" > "$outdir/dalvikvm.stdout" 2> "$outdir/dalvikvm.stderr" || {
        warn "dalvikvm launch returned non-zero — expected pre-#614"
    }

    log "[5/5] grep hilog for marker"
    local marker="OhosTrivialActivity.onCreate reached"
    # hilog -x is a one-shot dump on OHOS (-x = exit when caught up).
    hdc_shell "hilog -x 2>/dev/null | tail -200" > "$outdir/hilog-tail.log" 2>&1 || true
    if grep -qF "$marker" "$outdir/dalvikvm.stdout" 2>/dev/null \
       || grep -qF "$marker" "$outdir/hilog-tail.log" 2>/dev/null; then
        ok "${GREEN}${BOLD}MVP-1 PASS${RESET}: marker found"
        echo "PASS" > "$outdir/result.txt"
        return 0
    else
        warn "marker NOT found — Activity launch path is M8-Step1 (#614 unblocks)"
        warn "harness mechanics OK; see $outdir/ for evidence"
        echo "BLOCKED_ON_614_OR_M8" > "$outdir/result.txt"
        return 0
    fi
}

# ---------- usage / dispatch ------------------------------------------------
usage() {
    cat <<EOF
Usage: $0 <subcommand>

Subcommands:
  status              hdc list targets + board health check
  push-bcp            push boot-aosp-shim.{art,oat,vdex} + aosp-shim.dex
                      to $BOARD_DIR/bcp/
  hello               compile :hello, dex via d8, push, run, capture (#616)
  trivial-activity    build :trivial-activity APK, push, run, capture (#619)

Environment overrides:
  HDC=$HDC
  HDC_SERIAL=$HDC_SERIAL
  WINSTAGE=$WINSTAGE
  BOARD_DIR=$BOARD_DIR
EOF
}

main() {
    if [ "$#" -lt 1 ]; then
        usage
        exit 2
    fi
    local sub="$1"; shift
    case "$sub" in
        status)             cmd_status "$@" ;;
        push-bcp)           cmd_push_bcp "$@" ;;
        hello)              cmd_hello "$@" ;;
        trivial-activity)   cmd_trivial_activity "$@" ;;
        -h|--help|help)     usage; exit 0 ;;
        *)
            err "unknown subcommand: $sub"
            usage
            exit 2
            ;;
    esac
}

main "$@"
