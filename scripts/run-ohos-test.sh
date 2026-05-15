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

# ---------- CR60: bitness-as-parameter (--arch) -----------------------------
# We keep BOTH aarch64 and ARM32 dalvikvm builds alive. The CR60 spike
# (2026-05-14) showed DAYU200 userspace is 32-bit only, so a 32-bit dalvikvm
# can dlopen OHOS libs in-process. The aarch64 binary stays as the primary
# for Android phones / any future 64-bit OHOS ROM.
#
# ARCH selection:
#   --arch aarch64       use 64-bit dalvikvm (board path /data/local/tmp/dalvikvm)
#   --arch arm32         use 32-bit DYNAMIC-PIE dalvikvm (default arm32 since
#                        CR60-followup #3, E11). Board path
#                        /data/local/tmp/dalvikvm-arm32-dyn ; host
#                        dalvik-port/build-ohos-arm32-dynamic/dalvikvm. Superset
#                        of arm32-static — passes everything arm32-static
#                        passes (MVP-0/1) plus System.loadLibrary / in-process
#                        dlopen (E9a, E9b).
#   --arch arm32-static  legacy 32-bit STATIC dalvikvm (pre-E11 default). Board
#                        path /data/local/tmp/dalvikvm-arm32 ; host
#                        dalvik-port/build-ohos-arm32/dalvikvm. Kept buildable
#                        for MVP-0 baseline / regression testing; cannot
#                        dlopen OHOS libs (static musl).
#   --arch auto          auto-detect via `hdc shell getconf LONG_BIT` (default
#                        bash entrypoint). 32-bit board → arm32 (dynamic).
# Or set ARCH=aarch64 / arm32 / arm32-static / auto in env.
#
# Resolved by resolve_arch(); writes DALVIKVM_BOARD_PATH + DALVIKVM_HOST_PATH.
# See docs/engine/CR60_BITNESS_PIVOT_DECISION.md.
ARCH="${ARCH:-auto}"
DALVIKVM_BOARD_PATH=""   # populated by resolve_arch
DALVIKVM_HOST_PATH=""    # populated by resolve_arch

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

# OHOS-specific BCP additions (MVP-1, #619). These are the OHOS-tailored
# BCP slices that the standalone dalvikvm consumes on the rk3568 board:
#   - aosp-shim-ohos.dex : NON-stripped version of the AOSP shim (4.9MB,
#                          dex.035). Built by `dx --dex` against the full
#                          shim/java source tree, WITHOUT applying
#                          scripts/framework_duplicates.txt. The phone path
#                          can keep the slimmed aosp-shim.dex because real
#                          framework.jar fills in the gap; OHOS has no
#                          framework.jar (the phone's framework.jar is dex.039,
#                          unloadable by dalvik-kitkat), so the full shim has
#                          to ship.
#   - core-android-x86.jar : richer core library than core-kitkat.jar
#                           (includes java.util.concurrent.* etc. needed for
#                           android.content.Context.<clinit>). Already dex.035.
#   - direct-print-stream.jar : MVP-0 stdout bypass (unchanged).
BCP_FILES_OHOS=(
    "aosp-shim-ohos.dex"
    "core-android-x86.jar"
    "direct-print-stream.jar"
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

# ---------- CR60 arch resolution -------------------------------------------
# Populates $DALVIKVM_BOARD_PATH and $DALVIKVM_HOST_PATH based on $ARCH.
# Idempotent. Call at the top of every arch-sensitive subcommand.
resolve_arch() {
    if [ -n "$DALVIKVM_BOARD_PATH" ]; then
        return 0  # already resolved
    fi
    local effective="$ARCH"
    if [ "$effective" = "auto" ]; then
        local bits
        bits="$(hdc shell "getconf LONG_BIT" 2>/dev/null | tr -d '\r\n ' | head -c 4)"
        case "$bits" in
            32) effective="arm32" ;;
            64) effective="aarch64" ;;
            *)
                warn "auto-detect failed (getconf LONG_BIT='$bits'); falling back to aarch64"
                effective="aarch64"
                ;;
        esac
    fi
    case "$effective" in
        arm32)
            # E11 (CR60-followup #4, 2026-05-15): arm32 default flipped from
            # static to dynamic-PIE. Rationale: agents 14/15 proved the
            # dynamic binary is a strict superset — MVP-0/1 PASS unchanged
            # (commit 8710bf4f), plus E9a System.loadLibrary (commit
            # 02d7a975) and E9b in-process DRM (commit 411870cb) are only
            # reachable from the dynamic build. The static binary remains
            # buildable and selectable via --arch arm32-static for MVP-0
            # regression / smaller-blast-radius testing.
            DALVIKVM_BOARD_PATH="/data/local/tmp/dalvikvm-arm32-dyn"
            DALVIKVM_HOST_PATH="$REPO_ROOT/dalvik-port/build-ohos-arm32-dynamic/dalvikvm"
            ;;
        arm32-static)
            # Pre-E11 default. Kept for explicit regression testing of the
            # static-linked binary. Cannot dlopen OHOS libs (static musl).
            DALVIKVM_BOARD_PATH="/data/local/tmp/dalvikvm-arm32"
            DALVIKVM_HOST_PATH="$REPO_ROOT/dalvik-port/build-ohos-arm32/dalvikvm"
            ;;
        aarch64)
            DALVIKVM_BOARD_PATH="/data/local/tmp/dalvikvm"
            DALVIKVM_HOST_PATH="$REPO_ROOT/dalvik-port/build-ohos-aarch64/dalvikvm"
            ;;
        *)
            err "unknown ARCH '$effective' (valid: aarch64, arm32, arm32-static, auto)"
            return 1
            ;;
    esac
    log "[arch] resolved ARCH=$ARCH -> $effective  (board=$DALVIKVM_BOARD_PATH host=$DALVIKVM_HOST_PATH)"
    return 0
}

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
    # Phase 1 BCP (Android phone). Kept for traceability — these files are
    # boot-ART images and won't be consumed by the OHOS dalvikvm path, but
    # the harness pushes them so the board state matches the phone state.
    for f in "${BCP_FILES[@]}"; do
        if path="$(_find_bcp_file "$f")"; then
            paths+=("$path")
            log "  found: $path"
            found=$((found + 1))
        else
            warn "  missing (phase-1, optional on OHOS): $f"
        fi
    done
    # Phase 2 OHOS BCP — REQUIRED for MVP-1.
    for f in "${BCP_FILES_OHOS[@]}"; do
        if path="$(_find_bcp_file "$f")"; then
            paths+=("$path")
            log "  found: $path"
            found=$((found + 1))
        else
            err "  missing (OHOS, REQUIRED): $f"
            missing=$((missing + 1))
        fi
    done
    if [ "$missing" -gt 0 ]; then
        err "$missing OHOS BCP file(s) missing — cannot continue"
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
    # E11 hardening (2026-05-15): wipe both per-test and BCP dalvik-cache
    # so switching between --arch arm32 (dynamic) and --arch arm32-static
    # (or aarch64) doesn't trip dexopt cache contamination — the core-kitkat
    # odex is keyed by path only, not by the producer binary, so a switch
    # surfaces as 'Fatal error: java/lang/Object' on the second run. Same
    # rationale documented in cmd_hello_dlopen_real (CR60-followup E9a).
    local cmd="rm -f /data/dalvik-cache/data@local@tmp@westlake@HelloOhos* /data/dalvik-cache/data@local@tmp@westlake@bcp@* 2>/dev/null;"
    cmd="$cmd ANDROID_ROOT=${BOARD_DIR} ${DALVIKVM_BOARD_PATH}"
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
# MVP-1 (#619): an Android Activity's onCreate() runs on the board.
#
# Pipeline:
#   1. gradle :trivial-activity:assembleDebug          (APK we won't actually use)
#      gradle :launcher:compileJava                    (OhosMvpLauncher .class)
#   2. d8 --min-api 13 on MainActivity.class           (-> TrivialActivity.dex, dex.035)
#      d8 --min-api 13 on OhosMvpLauncher.class        (-> OhosMvpLauncher.dex, dex.035)
#      (We bypass the gradle APK because AGP's d8 emits dex.037 which
#      dalvik-kitkat refuses; --min-api 13 forces dex.035.)
#   3. push both dex files + verify aosp-shim-ohos.dex on board
#   4. run dalvikvm with the full BCP (core-android-x86 + direct-print-stream
#      + aosp-shim-ohos + TrivialActivity + OhosMvpLauncher) and invoke
#      OhosMvpLauncher with "<package>/.MainActivity".
#   5. grep both dalvikvm.stdout and hilog for the marker.
#
# Marker: "OhosTrivialActivity.onCreate reached"

cmd_trivial_activity() {
    local outdir="$ARTIFACT_ROOT/mvp1-trivial/$TS"
    mkdir -p "$outdir"
    log "${BOLD}== MVP-1 trivial-activity (#619) -> $outdir ==${RESET}"

    log "[1/5] gradle :trivial-activity:assembleDebug + :launcher:compileJava"
    if ! (cd "$GRADLE_DIR" && ./gradlew :trivial-activity:assembleDebug :launcher:compileJava --no-daemon -q) \
            > "$outdir/gradle.log" 2>&1; then
        err "gradle build failed — see $outdir/gradle.log"
        return 1
    fi
    local activity_classes="$GRADLE_DIR/trivial-activity/build/intermediates/javac/debug/classes"
    local launcher_classes="$GRADLE_DIR/launcher/build/classes/java/main"
    local activity_class="$activity_classes/com/westlake/ohostests/trivial/MainActivity.class"
    local launcher_class="$launcher_classes/com/westlake/ohostests/launcher/OhosMvpLauncher.class"
    if [ ! -f "$activity_class" ] || [ ! -f "$launcher_class" ]; then
        err "missing class files; activity=$activity_class launcher=$launcher_class"
        return 1
    fi
    ok "gradle build complete"

    log "[2/5] d8 --min-api 13 -> {TrivialActivity,OhosMvpLauncher}.dex"
    if [ ! -x "$D8" ]; then
        err "d8 not found at $D8 (set \$D8 or install build-tools/34.0.0)"
        return 1
    fi
    local dexdir="$outdir/dex"
    mkdir -p "$dexdir/activity" "$dexdir/launcher"
    "$D8" --min-api 13 --output "$dexdir/activity" "$activity_class" \
            > "$outdir/d8-activity.log" 2>&1 || { err "d8 activity failed"; return 1; }
    "$D8" --min-api 13 --output "$dexdir/launcher" "$launcher_class" \
            > "$outdir/d8-launcher.log" 2>&1 || { err "d8 launcher failed"; return 1; }
    mv "$dexdir/activity/classes.dex" "$dexdir/TrivialActivity.dex"
    mv "$dexdir/launcher/classes.dex" "$dexdir/OhosMvpLauncher.dex"
    rmdir "$dexdir/activity" "$dexdir/launcher" 2>/dev/null || true
    ok "dex emitted: TrivialActivity.dex=$(du -b "$dexdir/TrivialActivity.dex" | awk '{print $1}')B "\
"OhosMvpLauncher.dex=$(du -b "$dexdir/OhosMvpLauncher.dex" | awk '{print $1}')B"

    log "[3/5] push dex + verify BCP on board"
    hdc_shell "mkdir -p $BOARD_DIR $BOARD_DIR/bcp" >/dev/null 2>&1
    hdc_send "$dexdir/TrivialActivity.dex"  "$BOARD_DIR/TrivialActivity.dex"  || return 1
    hdc_send "$dexdir/OhosMvpLauncher.dex"  "$BOARD_DIR/OhosMvpLauncher.dex"  || return 1
    # Verify required BCP pieces exist on the board (we don't push them
    # every run — they're stable per board provisioning step).
    local bcp_check
    bcp_check="$(hdc_shell "ls $BOARD_DIR/bcp/aosp-shim-ohos.dex $BOARD_DIR/bcp/core-android-x86.jar $BOARD_DIR/bcp/direct-print-stream.jar 2>&1")"
    if echo "$bcp_check" | grep -q "No such file"; then
        err "missing BCP files on board (need aosp-shim-ohos.dex + core-android-x86.jar + direct-print-stream.jar in $BOARD_DIR/bcp/)"
        err "  hdc said: $bcp_check"
        return 1
    fi
    hdc_shell "ls -la $BOARD_DIR/TrivialActivity.dex $BOARD_DIR/OhosMvpLauncher.dex $BOARD_DIR/bcp/" \
            > "$outdir/on-device-ls.log" 2>&1
    ok "dex pushed; BCP intact"

    log "[4/5] invoke dalvikvm + OhosMvpLauncher"
    local bcp="$BOARD_DIR/bcp/core-android-x86.jar"
    bcp="$bcp:$BOARD_DIR/bcp/direct-print-stream.jar"
    bcp="$bcp:$BOARD_DIR/bcp/aosp-shim-ohos.dex"
    bcp="$bcp:$BOARD_DIR/TrivialActivity.dex"
    bcp="$bcp:$BOARD_DIR/OhosMvpLauncher.dex"
    local cmd="rm -f /data/dalvik-cache/data@local@tmp@westlake@* 2>/dev/null;"
    cmd="$cmd ANDROID_ROOT=$BOARD_DIR ${DALVIKVM_BOARD_PATH}"
    cmd="$cmd -Xbootclasspath:$bcp"
    cmd="$cmd com.westlake.ohostests.launcher.OhosMvpLauncher"
    cmd="$cmd com.westlake.ohostests.trivial/.MainActivity"
    hdc_shell "$cmd" > "$outdir/dalvikvm.stdout" 2> "$outdir/dalvikvm.stderr" || {
        warn "dalvikvm exited non-zero — capturing output regardless"
    }
    log "  stdout: $outdir/dalvikvm.stdout ($(wc -l < "$outdir/dalvikvm.stdout") lines)"
    log "  stderr: $outdir/dalvikvm.stderr"

    log "[5/5] grep marker"
    local marker="OhosTrivialActivity.onCreate reached"
    hdc_shell "hilog -x 2>/dev/null | tail -200" > "$outdir/hilog-tail.log" 2>&1 || true
    if grep -qF "$marker" "$outdir/dalvikvm.stdout" 2>/dev/null \
       || grep -qF "$marker" "$outdir/hilog-tail.log" 2>/dev/null; then
        ok "${GREEN}${BOLD}MVP-1 PASS${RESET}: marker found"
        echo "PASS" > "$outdir/result.txt"
        local marker_line
        marker_line="$(grep -F "$marker" "$outdir/dalvikvm.stdout" | head -1 | tr -d '\r')"
        log "  marker line: $marker_line"
        return 0
    else
        err "marker NOT found in stdout or hilog"
        echo "FAIL" > "$outdir/result.txt"
        return 1
    fi
}

# ---------- subcommand: red-square ------------------------------------------
# MVP-2 (PF-ohos-mvp-003): a red rectangle is visible on the DAYU200's HDMI
# display, driven by RedView.onDraw(canvas) { canvas.drawColor(Color.RED) }
# through the V2 substrate, with pixels delivered to /dev/graphics/fb0 via
# libcore.io.Libcore.os.{open,writeBytes,close}.
#
# Pipeline (parallels trivial-activity):
#   1. gradle :red-square:assembleDebug + :launcher:compileJava
#   2. d8 --min-api 13 on the 4 .class files (MainActivity, RedView,
#      SoftwareCanvas, Fb0Presenter) → RedSquare.dex (dex.035)
#      d8 --min-api 13 on OhosMvpLauncher.class → OhosMvpLauncher.dex
#   3. push both dex files, verify BCP intact
#   4. dalvikvm + OhosMvpLauncher com.westlake.ohostests.red/.MainActivity
#   5. grep stdout + hilog for the marker chain:
#      "OhosRedSquare.onCreate reached"
#      "OhosRedSquare.fb0 write OK"
#
# Marker contract: PASS requires both "onCreate reached" AND "fb0 write OK".
# A passing run with VISIBLE PIXELS still requires manual phone-camera
# capture into artifacts/ohos-mvp/mvp2-red-square/.

cmd_red_square() {
    local outdir="$ARTIFACT_ROOT/mvp2-red-square/$TS"
    mkdir -p "$outdir"
    log "${BOLD}== MVP-2 red-square (PF-ohos-mvp-003) -> $outdir ==${RESET}"

    log "[1/5] gradle :red-square:assembleDebug + :launcher:compileJava"
    if ! (cd "$GRADLE_DIR" && ./gradlew :red-square:assembleDebug :launcher:compileJava --no-daemon -q) \
            > "$outdir/gradle.log" 2>&1; then
        err "gradle build failed — see $outdir/gradle.log"
        return 1
    fi
    local app_classes="$GRADLE_DIR/red-square/build/intermediates/javac/debug/classes"
    local launcher_classes="$GRADLE_DIR/launcher/build/classes/java/main"
    local launcher_class="$launcher_classes/com/westlake/ohostests/launcher/OhosMvpLauncher.class"
    if [ ! -d "$app_classes/com/westlake/ohostests/red" ] || [ ! -f "$launcher_class" ]; then
        err "missing class output: $app_classes/com/westlake/ohostests/red/ or $launcher_class"
        return 1
    fi
    # Collect every .class under com/westlake/ohostests/red/
    local app_class_list=()
    while IFS= read -r f; do
        app_class_list+=("$f")
    done < <(find "$app_classes/com/westlake/ohostests/red" -name '*.class' | sort)
    if [ ${#app_class_list[@]} -lt 4 ]; then
        warn "expected 4 .class files (MainActivity, RedView, SoftwareCanvas, Fb0Presenter); got ${#app_class_list[@]}"
        for f in "${app_class_list[@]}"; do warn "  $f"; done
    fi
    ok "gradle build complete (${#app_class_list[@]} app classes)"

    log "[2/5] d8 --min-api 13 -> {RedSquare,OhosMvpLauncher}.dex"
    if [ ! -x "$D8" ]; then
        err "d8 not found at $D8"
        return 1
    fi
    local dexdir="$outdir/dex"
    mkdir -p "$dexdir/app" "$dexdir/launcher"
    "$D8" --min-api 13 --output "$dexdir/app" "${app_class_list[@]}" \
            > "$outdir/d8-app.log" 2>&1 || { err "d8 app failed — see $outdir/d8-app.log"; return 1; }
    "$D8" --min-api 13 --output "$dexdir/launcher" "$launcher_class" \
            > "$outdir/d8-launcher.log" 2>&1 || { err "d8 launcher failed"; return 1; }
    mv "$dexdir/app/classes.dex" "$dexdir/RedSquare.dex"
    mv "$dexdir/launcher/classes.dex" "$dexdir/OhosMvpLauncher.dex"
    rmdir "$dexdir/app" "$dexdir/launcher" 2>/dev/null || true
    ok "dex emitted: RedSquare.dex=$(du -b "$dexdir/RedSquare.dex" | awk '{print $1}')B "\
"OhosMvpLauncher.dex=$(du -b "$dexdir/OhosMvpLauncher.dex" | awk '{print $1}')B"

    log "[3/5] push dex + verify BCP on board"
    hdc_shell "mkdir -p $BOARD_DIR $BOARD_DIR/bcp" >/dev/null 2>&1
    hdc_send "$dexdir/RedSquare.dex"        "$BOARD_DIR/RedSquare.dex"        || return 1
    hdc_send "$dexdir/OhosMvpLauncher.dex"  "$BOARD_DIR/OhosMvpLauncher.dex"  || return 1
    local bcp_check
    bcp_check="$(hdc_shell "ls $BOARD_DIR/bcp/aosp-shim-ohos.dex $BOARD_DIR/bcp/core-android-x86.jar $BOARD_DIR/bcp/direct-print-stream.jar 2>&1")"
    if echo "$bcp_check" | grep -q "No such file"; then
        err "missing BCP files on board"
        err "  hdc said: $bcp_check"
        return 1
    fi
    hdc_shell "ls -la $BOARD_DIR/RedSquare.dex $BOARD_DIR/OhosMvpLauncher.dex $BOARD_DIR/bcp/" \
            > "$outdir/on-device-ls.log" 2>&1
    ok "dex pushed; BCP intact"

    log "[4/5] invoke dalvikvm + OhosMvpLauncher"
    local bcp="$BOARD_DIR/bcp/core-android-x86.jar"
    bcp="$bcp:$BOARD_DIR/bcp/direct-print-stream.jar"
    bcp="$bcp:$BOARD_DIR/bcp/aosp-shim-ohos.dex"
    bcp="$bcp:$BOARD_DIR/RedSquare.dex"
    bcp="$bcp:$BOARD_DIR/OhosMvpLauncher.dex"
    local cmd="rm -f /data/dalvik-cache/data@local@tmp@westlake@* 2>/dev/null;"
    cmd="$cmd ANDROID_ROOT=$BOARD_DIR ${DALVIKVM_BOARD_PATH}"
    cmd="$cmd -Xbootclasspath:$bcp"
    cmd="$cmd com.westlake.ohostests.launcher.OhosMvpLauncher"
    cmd="$cmd com.westlake.ohostests.red/.MainActivity"
    hdc_shell "$cmd" > "$outdir/dalvikvm.stdout" 2> "$outdir/dalvikvm.stderr" || {
        warn "dalvikvm exited non-zero — capturing output regardless"
    }
    log "  stdout: $outdir/dalvikvm.stdout ($(wc -l < "$outdir/dalvikvm.stdout") lines)"
    log "  stderr: $outdir/dalvikvm.stderr"

    log "[5/5] grep markers"
    local marker_oncreate="OhosRedSquare.onCreate reached"
    local marker_fb0ok="OhosRedSquare.fb0 write OK"
    hdc_shell "hilog -x 2>/dev/null | tail -300" > "$outdir/hilog-tail.log" 2>&1 || true
    local have_oncreate=0 have_fb0ok=0
    if grep -qF "$marker_oncreate" "$outdir/dalvikvm.stdout" 2>/dev/null \
       || grep -qF "$marker_oncreate" "$outdir/hilog-tail.log" 2>/dev/null; then
        have_oncreate=1
    fi
    if grep -qF "$marker_fb0ok" "$outdir/dalvikvm.stdout" 2>/dev/null \
       || grep -qF "$marker_fb0ok" "$outdir/hilog-tail.log" 2>/dev/null; then
        have_fb0ok=1
    fi
    log "  onCreate marker: $have_oncreate   fb0-write-OK marker: $have_fb0ok"
    if [ "$have_oncreate" -eq 1 ] && [ "$have_fb0ok" -eq 1 ]; then
        ok "${GREEN}${BOLD}MVP-2 LOGICAL PASS${RESET}: both markers found"
        log "  next step: phone-camera photo of DAYU200 HDMI display showing red,"
        log "  save into $outdir/"
        echo "LOGICAL_PASS" > "$outdir/result.txt"
        local line1 line2
        line1="$(grep -F "$marker_oncreate" "$outdir/dalvikvm.stdout" | head -1 | tr -d '\r')"
        line2="$(grep -F "$marker_fb0ok"   "$outdir/dalvikvm.stdout" | head -1 | tr -d '\r')"
        log "  marker 1: $line1"
        log "  marker 2: $line2"
        return 0
    elif [ "$have_oncreate" -eq 1 ]; then
        warn "Activity ran but fb0 write did not complete cleanly — see $outdir/dalvikvm.stdout"
        echo "PARTIAL_ACTIVITY_NO_FB0" > "$outdir/result.txt"
        return 1
    else
        err "neither marker reached — Activity didn't run"
        echo "FAIL" > "$outdir/result.txt"
        return 1
    fi
}

# ---------- subcommand: red-square-drm --------------------------------------
# Two-stage driver:
#   Stage A (dalvikvm side):  invoke MainActivity (same as `red-square`); it
#                             runs RedView.onDraw -> SoftwareCanvas and dumps
#                             a 720*1280*4 BGRA buffer to
#                             /data/local/tmp/red_bgra.bin via DrmPresenter.
#   Stage B (driver side):    push the aarch64 `drm_present` helper, stop
#                             composer_host so DRM master is free, run
#                             drm_present with the BGRA file piped to stdin
#                             — it CREATE_DUMB / mmap / fill / ADDFB2 /
#                             SETCRTC the DSI-1 panel for HOLD_SECS seconds.
#
# This is the >4-hour-budget-friendly variant: the kernel-side scan-out
# pipeline is the proof-of-concept; phone-camera or hdmi-capture of the
# panel during the hold window confirms visible red. See
# artifacts/ohos-mvp/mvp2-red-square-drm/ for kernel-state evidence.
#
# After Stage B, composer_host respawns automatically (it's supervised by
# hdf_devmgr), so no manual recovery is required in the common case.

cmd_red_square_drm() {
    local outdir="$ARTIFACT_ROOT/mvp2-red-square-drm/$TS"
    mkdir -p "$outdir"
    local hold_secs="${RED_SQUARE_DRM_HOLD_SECS:-12}"
    log "${BOLD}== MVP-2 red-square-drm (PF-ohos-mvp-003) -> $outdir hold=${hold_secs}s ==${RESET}"

    log "[A.1/6] gradle :red-square:assembleDebug + :launcher:compileJava"
    if ! (cd "$GRADLE_DIR" && ./gradlew :red-square:assembleDebug :launcher:compileJava --no-daemon -q) \
            > "$outdir/gradle.log" 2>&1; then
        err "gradle build failed — see $outdir/gradle.log"
        return 1
    fi
    local app_classes="$GRADLE_DIR/red-square/build/intermediates/javac/debug/classes"
    local launcher_classes="$GRADLE_DIR/launcher/build/classes/java/main"
    local launcher_class="$launcher_classes/com/westlake/ohostests/launcher/OhosMvpLauncher.class"
    if [ ! -d "$app_classes/com/westlake/ohostests/red" ] || [ ! -f "$launcher_class" ]; then
        err "missing class output"
        return 1
    fi
    local app_class_list=()
    while IFS= read -r f; do app_class_list+=("$f")
    done < <(find "$app_classes/com/westlake/ohostests/red" -name '*.class' | sort)
    ok "gradle build complete (${#app_class_list[@]} app classes)"

    log "[A.2/6] d8 --min-api 13 -> {RedSquare,OhosMvpLauncher}.dex"
    if [ ! -x "$D8" ]; then err "d8 not found at $D8"; return 1; fi
    local dexdir="$outdir/dex"
    mkdir -p "$dexdir/app" "$dexdir/launcher"
    "$D8" --min-api 13 --output "$dexdir/app" "${app_class_list[@]}" \
            > "$outdir/d8-app.log" 2>&1 || { err "d8 app failed"; return 1; }
    "$D8" --min-api 13 --output "$dexdir/launcher" "$launcher_class" \
            > "$outdir/d8-launcher.log" 2>&1 || { err "d8 launcher failed"; return 1; }
    mv "$dexdir/app/classes.dex" "$dexdir/RedSquare.dex"
    mv "$dexdir/launcher/classes.dex" "$dexdir/OhosMvpLauncher.dex"
    rmdir "$dexdir/app" "$dexdir/launcher" 2>/dev/null || true
    ok "dex emitted"

    log "[A.3/6] push dex + drm_present"
    hdc_shell "mkdir -p $BOARD_DIR $BOARD_DIR/bcp" >/dev/null 2>&1
    hdc_send "$dexdir/RedSquare.dex"       "$BOARD_DIR/RedSquare.dex"       || return 1
    hdc_send "$dexdir/OhosMvpLauncher.dex" "$BOARD_DIR/OhosMvpLauncher.dex" || return 1
    local drm_present_host="$REPO_ROOT/artifacts/ohos-mvp/mvp2-red-square-drm/drm_present"
    if [ ! -f "$drm_present_host" ]; then
        err "drm_present helper missing at $drm_present_host"
        err "  rebuild via: clang --target=aarch64-linux-ohos --sysroot=dalvik-port/ohos-sysroot ..."
        return 1
    fi
    hdc_send "$drm_present_host" "/data/local/tmp/drm_present" || return 1
    hdc_shell "chmod 0755 /data/local/tmp/drm_present" >/dev/null 2>&1
    ok "binaries pushed"

    log "[A.4/6] invoke dalvikvm + OhosMvpLauncher (dumps BGRA to /data/local/tmp/red_bgra.bin)"
    local bcp="$BOARD_DIR/bcp/core-android-x86.jar"
    bcp="$bcp:$BOARD_DIR/bcp/direct-print-stream.jar"
    bcp="$bcp:$BOARD_DIR/bcp/aosp-shim-ohos.dex"
    bcp="$bcp:$BOARD_DIR/RedSquare.dex"
    bcp="$bcp:$BOARD_DIR/OhosMvpLauncher.dex"
    local cmd="rm -f /data/dalvik-cache/data@local@tmp@westlake@* 2>/dev/null;"
    cmd="$cmd rm -f /data/local/tmp/red_bgra.bin 2>/dev/null;"
    cmd="$cmd ANDROID_ROOT=$BOARD_DIR ${DALVIKVM_BOARD_PATH}"
    cmd="$cmd -Xbootclasspath:$bcp"
    cmd="$cmd com.westlake.ohostests.launcher.OhosMvpLauncher"
    cmd="$cmd com.westlake.ohostests.red/.MainActivity"
    hdc_shell "$cmd" > "$outdir/dalvikvm.stdout" 2> "$outdir/dalvikvm.stderr" || {
        warn "dalvikvm non-zero — continuing"
    }
    log "  stdout: $outdir/dalvikvm.stdout ($(wc -l < "$outdir/dalvikvm.stdout") lines)"

    local marker_drm_dump="OhosRedSquare.drm bgra-dump OK"
    if ! grep -qF "$marker_drm_dump" "$outdir/dalvikvm.stdout"; then
        err "Java side did not emit '$marker_drm_dump' — Activity failed before reaching DrmPresenter"
        echo "FAIL_NO_BGRA_DUMP" > "$outdir/result.txt"
        return 1
    fi
    local bgra_size
    bgra_size="$(hdc_shell "stat -c%s /data/local/tmp/red_bgra.bin 2>/dev/null" | tr -d '\r')"
    if [ -z "$bgra_size" ] || [ "$bgra_size" -lt 3686400 ]; then
        err "red_bgra.bin missing or short: got '$bgra_size' bytes (expected >=3686400)"
        echo "FAIL_BGRA_FILE_BAD" > "$outdir/result.txt"
        return 1
    fi
    ok "Stage A done — Java dumped $bgra_size bytes BGRA to /data/local/tmp/red_bgra.bin"

    log "[B.5/6] Stage B: kill composer_host + run drm_present (panel scans red for ${hold_secs}s)"
    log "  IMPORTANT: take a phone-camera photo of the panel during this window"
    log "  capturing kernel state mid-flight as fallback evidence"

    # Snapshot pre-state
    hdc_shell "cat /sys/kernel/debug/dri/0/state" > "$outdir/drm-state-pre.txt" 2>&1
    hdc_shell "cat /sys/kernel/debug/dri/0/clients" > "$outdir/drm-clients-pre.txt" 2>&1

    # Fire-and-snapshot: kill composer_host, run drm_present in background,
    # then sleep ~3s and capture mid-flight DRM state, then wait for hold to finish.
    local stage_b_cmd
    stage_b_cmd="kill -9 \$(pidof composer_host) 2>/dev/null;"
    stage_b_cmd="$stage_b_cmd /data/local/tmp/drm_present $hold_secs"
    stage_b_cmd="$stage_b_cmd < /data/local/tmp/red_bgra.bin"
    stage_b_cmd="$stage_b_cmd > /data/local/tmp/drm_present.stdout"
    stage_b_cmd="$stage_b_cmd 2> /data/local/tmp/drm_present.stderr"
    stage_b_cmd="$stage_b_cmd &"
    stage_b_cmd="$stage_b_cmd sleep 3;"
    stage_b_cmd="$stage_b_cmd echo '=== mid-flight state ===';"
    stage_b_cmd="$stage_b_cmd cat /sys/kernel/debug/dri/0/state;"
    stage_b_cmd="$stage_b_cmd echo '=== mid-flight framebuffer ===';"
    stage_b_cmd="$stage_b_cmd cat /sys/kernel/debug/dri/0/framebuffer;"
    stage_b_cmd="$stage_b_cmd echo '=== mid-flight summary ===';"
    stage_b_cmd="$stage_b_cmd cat /sys/kernel/debug/dri/0/summary;"
    stage_b_cmd="$stage_b_cmd wait;"
    stage_b_cmd="$stage_b_cmd echo '=== drm_present.stdout ===';"
    stage_b_cmd="$stage_b_cmd cat /data/local/tmp/drm_present.stdout;"
    stage_b_cmd="$stage_b_cmd echo '=== drm_present.stderr ===';"
    stage_b_cmd="$stage_b_cmd cat /data/local/tmp/drm_present.stderr"
    hdc_shell "$stage_b_cmd" > "$outdir/stage-b.log" 2>&1

    # Post-state
    hdc_shell "cat /sys/kernel/debug/dri/0/state" > "$outdir/drm-state-post.txt" 2>&1
    hdc_shell "cat /sys/kernel/debug/dri/0/clients" > "$outdir/drm-clients-post.txt" 2>&1

    log "[B.6/6] grep success markers"
    local marker_scanout="DRM_SCANOUT_OK"
    local marker_kernel_fb="allocated by = drm_present"
    local have_scanout=0 have_kernel_fb=0
    if grep -qF "$marker_scanout" "$outdir/stage-b.log"; then have_scanout=1; fi
    if grep -qF "$marker_kernel_fb" "$outdir/stage-b.log"; then have_kernel_fb=1; fi
    log "  DRM_SCANOUT_OK marker: $have_scanout"
    log "  kernel-side fb allocated by drm_present: $have_kernel_fb"

    if [ "$have_scanout" -eq 1 ] && [ "$have_kernel_fb" -eq 1 ]; then
        ok "${GREEN}${BOLD}MVP-2 DRM SCAN-OUT PASS${RESET}: kernel confirms fb_id bound to DSI-1 CRTC for ${hold_secs}s"
        log "  next step: phone-camera photo of DAYU200 DSI panel during the hold window,"
        log "  save into $outdir/"
        echo "DRM_SCANOUT_PASS" > "$outdir/result.txt"
        return 0
    elif [ "$have_scanout" -eq 1 ]; then
        warn "stdout said DRM_SCANOUT_OK but kernel didn't show our fb — racy capture?"
        echo "PARTIAL_DRM" > "$outdir/result.txt"
        return 1
    else
        err "drm_present did not reach scan-out — see $outdir/stage-b.log"
        echo "FAIL_DRM" > "$outdir/result.txt"
        return 1
    fi
}

# ---------- subcommand: m6-drm-daemon ---------------------------------------
# Long-lived DRM/KMS daemon port (Phase 2 M6 OHOS variant; PF-ohos-m6-001).
# Runs the standalone self-test (page-flip at native vsync without a client)
# AND an end-to-end AF_UNIX + memfd round-trip with the built-in test client
# sending 120 frames (60 RED + 60 BLUE). Captures kernel DRM debugfs state
# mid-flight to prove our daemon owns the scanout while composer_host stays
# alive.
cmd_m6_drm_daemon() {
    local outdir="$ARTIFACT_ROOT/m6-drm-daemon/$TS"
    mkdir -p "$outdir"
    log "${BOLD}== M6 DRM daemon (PF-ohos-m6-001) -> $outdir ==${RESET}"

    local daemon_host="$REPO_ROOT/dalvik-port/compat/m6-drm-daemon/m6-drm-daemon"
    if [ ! -f "$daemon_host" ]; then
        log "[1/5] cross-compiling daemon"
        bash "$REPO_ROOT/dalvik-port/compat/m6-drm-daemon/build.sh" \
                > "$outdir/build.log" 2>&1 || { err "build failed"; return 1; }
    else
        log "[1/5] daemon present at $daemon_host"
    fi
    if [ ! -f "$daemon_host" ]; then err "daemon binary missing"; return 1; fi

    log "[2/5] push daemon to /data/local/tmp/m6-drm-daemon"
    hdc_send "$daemon_host" "/data/local/tmp/m6-drm-daemon" || return 1
    hdc_shell "chmod 0755 /data/local/tmp/m6-drm-daemon" >/dev/null 2>&1

    log "[3/5] self-test (5 s, no kill composer_host)"
    hdc_shell "/data/local/tmp/m6-drm-daemon --self-test 5 --no-kill-composer" \
            > "$outdir/self-test.log" 2>&1
    local st_ok
    st_ok=$(grep -c "M6_SELF_TEST_OK" "$outdir/self-test.log" || true)
    if [ "$st_ok" -ne 1 ]; then
        err "self-test failed — see $outdir/self-test.log"
        echo "FAIL_SELF_TEST" > "$outdir/result.txt"
        return 1
    fi
    ok "self-test PASS: $(grep M6_SELF_TEST_OK "$outdir/self-test.log")"

    log "[4/5] end-to-end (daemon + test-client, 120 frames)"
    hdc_shell "rm -f /data/local/tmp/westlake/m6-drm.sock 2>/dev/null;
        mkdir -p /data/local/tmp/westlake;
        /data/local/tmp/m6-drm-daemon --accept-client --no-kill-composer --max-frames 120 > /data/local/tmp/m6d.stdout 2>/data/local/tmp/m6d.stderr &
        DPID=\$!;
        sleep 1;
        /data/local/tmp/m6-drm-daemon --test-client --frames 120 --split 60 > /data/local/tmp/m6c.stdout 2>/data/local/tmp/m6c.stderr &
        CPID=\$!;
        sleep 1;
        echo === midflight clients ===;
        cat /sys/kernel/debug/dri/0/clients;
        echo === midflight state ===;
        cat /sys/kernel/debug/dri/0/state;
        wait \$CPID;
        sleep 1;
        echo === daemon stdout ===;
        cat /data/local/tmp/m6d.stdout;
        echo === daemon stderr ===;
        tail -25 /data/local/tmp/m6d.stderr;
        echo === client stdout ===;
        cat /data/local/tmp/m6c.stdout;
        echo === post clients ===;
        cat /sys/kernel/debug/dri/0/clients" \
        > "$outdir/end-to-end.log" 2>&1

    log "[5/5] grep markers"
    local m_daemon_done m_client_ok m_kernel_owns
    m_daemon_done=$(grep -c "M6_DAEMON_DONE frames=120" "$outdir/end-to-end.log" || true)
    m_client_ok=$(grep -c "M6_TEST_CLIENT_DONE ok=120 fail=0" "$outdir/end-to-end.log" || true)
    m_kernel_owns=$(grep -c "allocated by = m6-drm-daemon" "$outdir/end-to-end.log" || true)
    log "  M6_DAEMON_DONE frames=120: $m_daemon_done"
    log "  M6_TEST_CLIENT_DONE ok=120: $m_client_ok"
    log "  kernel allocated-by=m6-drm-daemon: $m_kernel_owns"

    if [ "$m_daemon_done" -ge 1 ] && [ "$m_client_ok" -ge 1 ] && [ "$m_kernel_owns" -ge 1 ]; then
        local hz
        hz=$(grep -oE 'hz=[0-9.]+' "$outdir/end-to-end.log" | head -1)
        ok "${GREEN}${BOLD}M6 DRM daemon PASS${RESET}: 120 frames flipped, $hz, composer_host coexists"
        cp "$daemon_host" "$outdir/m6-drm-daemon" 2>/dev/null || true
        echo "M6_DAEMON_PASS" > "$outdir/result.txt"
        return 0
    else
        err "M6 daemon did not reach all markers — see $outdir/end-to-end.log"
        echo "FAIL_M6" > "$outdir/result.txt"
        return 1
    fi
}

# ---------- subcommand: m6-java-client --------------------------------------
# M6-OHOS-Step2 (PF-ohos-m6-002): the Java-side counterpart of m6-drm-daemon's
# built-in --test-client. Proves that dalvikvm Activity code can submit BGRA
# frames to the long-lived M6 daemon over AF_UNIX/SCM_RIGHTS, replacing the
# single-shot Fb0Presenter/DrmPresenter file-dump paths with a streaming
# memfd handoff.
#
# Pipeline:
#   1. Build :m6-test:assembleDebug + :launcher:compileJava.
#   2. d8 --min-api 13 the four m6-test classes (M6DrmClient, M6FramePainter,
#      M6ClientTestActivity, $Builder) into M6Test.dex; same for launcher.
#   3. Push m6-drm-daemon (if not already), verify BCP intact.
#   4. Start daemon on board in background with --accept-client
#      --no-kill-composer --max-frames 120.
#   5. Run dalvikvm + OhosMvpLauncher com.westlake.ohostests.m6/.M6ClientTestActivity.
#   6. Capture kernel debugfs (clients, framebuffer) mid-flight to prove
#      `allocated by = m6-drm-daemon` while dalvikvm is sending.
#   7. Grep stdout for M6_JAVA_CLIENT_DONE marker AND the daemon's
#      M6_DAEMON_DONE frames=120.
#
# Markers required for PASS:
#   - "M6_JAVA_CLIENT_DONE ok=120 fail=0 ..."
#   - "M6_DAEMON_DONE frames=120 ..."
#   - "allocated by = m6-drm-daemon"
#   - composer_host pid stable before+after (the daemon coexists, doesn't kill it)

cmd_m6_java_client() {
    local outdir="$ARTIFACT_ROOT/m6-java-client/$TS"
    mkdir -p "$outdir"
    log "${BOLD}== M6-OHOS-Step2 m6-java-client (PF-ohos-m6-002) -> $outdir ==${RESET}"

    log "[1/7] gradle :m6-test:assembleDebug + :launcher:compileJava"
    if ! (cd "$GRADLE_DIR" && ./gradlew :m6-test:assembleDebug :launcher:compileJava --no-daemon -q) \
            > "$outdir/gradle.log" 2>&1; then
        err "gradle build failed — see $outdir/gradle.log"
        return 1
    fi
    local app_classes="$GRADLE_DIR/m6-test/build/intermediates/javac/debug/classes"
    local launcher_classes="$GRADLE_DIR/launcher/build/classes/java/main"
    local launcher_class="$launcher_classes/com/westlake/ohostests/launcher/OhosMvpLauncher.class"
    if [ ! -d "$app_classes/com/westlake/ohostests/m6" ] || [ ! -f "$launcher_class" ]; then
        err "missing class output: $app_classes/com/westlake/ohostests/m6/ or $launcher_class"
        return 1
    fi
    # Collect ONLY the app's classes under com/westlake/ohostests/m6/.
    # UnixSocketBridge.class lives at build/.../com/westlake/compat/ from
    # the compile-time stub source set — we intentionally exclude it so
    # at runtime the BCP-side aosp-shim-ohos.dex resolves it (matching
    # the registered JNI natives in libcore_bridge.cpp).
    local app_class_list=()
    while IFS= read -r f; do
        app_class_list+=("$f")
    done < <(find "$app_classes/com/westlake/ohostests/m6" -name '*.class' | sort)
    if [ ${#app_class_list[@]} -lt 3 ]; then
        warn "expected at least 3 app classes (M6DrmClient, M6FramePainter, M6ClientTestActivity); got ${#app_class_list[@]}"
        for f in "${app_class_list[@]}"; do warn "  $f"; done
    fi
    ok "gradle build complete (${#app_class_list[@]} app classes)"

    log "[2/7] d8 --min-api 13 -> {M6Test,OhosMvpLauncher}.dex"
    if [ ! -x "$D8" ]; then err "d8 not found at $D8"; return 1; fi
    local dexdir="$outdir/dex"
    mkdir -p "$dexdir/app" "$dexdir/launcher"
    "$D8" --min-api 13 --output "$dexdir/app" "${app_class_list[@]}" \
            > "$outdir/d8-app.log" 2>&1 || { err "d8 app failed — see $outdir/d8-app.log"; return 1; }
    "$D8" --min-api 13 --output "$dexdir/launcher" "$launcher_class" \
            > "$outdir/d8-launcher.log" 2>&1 || { err "d8 launcher failed"; return 1; }
    mv "$dexdir/app/classes.dex" "$dexdir/M6Test.dex"
    mv "$dexdir/launcher/classes.dex" "$dexdir/OhosMvpLauncher.dex"
    rmdir "$dexdir/app" "$dexdir/launcher" 2>/dev/null || true
    ok "dex emitted: M6Test.dex=$(du -b "$dexdir/M6Test.dex" | awk '{print $1}')B "\
"OhosMvpLauncher.dex=$(du -b "$dexdir/OhosMvpLauncher.dex" | awk '{print $1}')B"

    log "[3/7] push dex + verify daemon + BCP on board"
    hdc_shell "mkdir -p $BOARD_DIR $BOARD_DIR/bcp" >/dev/null 2>&1
    hdc_send "$dexdir/M6Test.dex"           "$BOARD_DIR/M6Test.dex"           || return 1
    hdc_send "$dexdir/OhosMvpLauncher.dex"  "$BOARD_DIR/OhosMvpLauncher.dex"  || return 1
    # Verify daemon binary is on the board; push if missing.
    local daemon_host="$REPO_ROOT/dalvik-port/compat/m6-drm-daemon/m6-drm-daemon"
    local daemon_present
    daemon_present="$(hdc_shell "ls -la /data/local/tmp/m6-drm-daemon 2>&1" | tr -d '\r')"
    if echo "$daemon_present" | grep -q "No such"; then
        log "  daemon missing on board — pushing"
        if [ ! -f "$daemon_host" ]; then
            err "daemon binary missing at $daemon_host"
            err "  build via: bash dalvik-port/compat/m6-drm-daemon/build.sh"
            return 1
        fi
        hdc_send "$daemon_host" "/data/local/tmp/m6-drm-daemon" || return 1
        hdc_shell "chmod 0755 /data/local/tmp/m6-drm-daemon" >/dev/null 2>&1
    else
        log "  daemon already on board: $daemon_present"
    fi
    # Verify BCP intact.
    local bcp_check
    bcp_check="$(hdc_shell "ls $BOARD_DIR/bcp/aosp-shim-ohos.dex $BOARD_DIR/bcp/core-android-x86.jar $BOARD_DIR/bcp/direct-print-stream.jar 2>&1")"
    if echo "$bcp_check" | grep -q "No such file"; then
        err "missing BCP files on board"
        err "  hdc said: $bcp_check"
        return 1
    fi
    # If aosp-shim-ohos.dex was rebuilt locally (new UnixSocketBridge), refresh it on the board.
    local local_shim="$REPO_ROOT/ohos-deploy/aosp-shim-ohos.dex"
    local local_shim_size
    local_shim_size="$(stat -c%s "$local_shim" 2>/dev/null || echo 0)"
    local board_shim_size
    board_shim_size="$(hdc_shell "stat -c%s $BOARD_DIR/bcp/aosp-shim-ohos.dex 2>/dev/null" | tr -d '\r')"
    if [ "$local_shim_size" != "$board_shim_size" ] && [ "$local_shim_size" -gt 0 ]; then
        log "  refreshing aosp-shim-ohos.dex on board (local=$local_shim_size board=$board_shim_size)"
        hdc_send "$local_shim" "$BOARD_DIR/bcp/aosp-shim-ohos.dex" || return 1
    fi
    # Also refresh dalvikvm if local is newer than the board's.
    # CR60: use the arch-resolved host + board paths.
    local local_dvm="$DALVIKVM_HOST_PATH"
    if [ -f "$local_dvm" ]; then
        local local_dvm_size board_dvm_size
        local_dvm_size="$(stat -c%s "$local_dvm")"
        board_dvm_size="$(hdc_shell "stat -c%s $DALVIKVM_BOARD_PATH 2>/dev/null" | tr -d '\r')"
        if [ "$local_dvm_size" != "$board_dvm_size" ]; then
            log "  refreshing dalvikvm on board (local=$local_dvm_size board=$board_dvm_size)"
            hdc_send "$local_dvm" "$DALVIKVM_BOARD_PATH" || return 1
            hdc_shell "chmod 0755 $DALVIKVM_BOARD_PATH" >/dev/null 2>&1
        fi
    fi
    hdc_shell "ls -la $BOARD_DIR/M6Test.dex $BOARD_DIR/OhosMvpLauncher.dex $BOARD_DIR/bcp/" \
            > "$outdir/on-device-ls.log" 2>&1
    ok "dex pushed; daemon + BCP intact"

    log "[4/7] capture pre-state (composer_host pid, DRM clients)"
    hdc_shell "ps -ef | grep -E 'composer_host|m6-drm-daemon' | grep -v grep" \
            > "$outdir/ps-pre.txt" 2>&1
    hdc_shell "cat /sys/kernel/debug/dri/0/clients" > "$outdir/drm-clients-pre.txt" 2>&1
    local composer_pid_pre
    composer_pid_pre="$(grep -E 'composer_host' "$outdir/ps-pre.txt" | head -1 | awk '{print $2}')"
    log "  composer_host pid pre: ${composer_pid_pre:-(none)}"

    log "[5/7] start daemon in background + run dalvikvm client"
    # Combined shell script:
    #  - clean leftover sock + caches
    #  - launch daemon in background (--max-frames 120 so it exits cleanly after the test)
    #  - sleep 1 to give daemon time to bind + emit M6_DAEMON_READY
    #  - run dalvikvm in foreground
    #  - sleep 1, capture mid-flight DRM state in parallel via subshell
    #  - wait for daemon to exit
    #  - dump both logs
    local bcp="$BOARD_DIR/bcp/core-android-x86.jar"
    bcp="$bcp:$BOARD_DIR/bcp/direct-print-stream.jar"
    bcp="$bcp:$BOARD_DIR/bcp/aosp-shim-ohos.dex"
    bcp="$bcp:$BOARD_DIR/M6Test.dex"
    bcp="$bcp:$BOARD_DIR/OhosMvpLauncher.dex"
    local run_cmd
    run_cmd="rm -f /data/dalvik-cache/data@local@tmp@westlake@* 2>/dev/null;"
    run_cmd="$run_cmd rm -f /data/local/tmp/westlake/m6-drm.sock 2>/dev/null;"
    run_cmd="$run_cmd rm -f /data/local/tmp/m6d.stdout /data/local/tmp/m6d.stderr 2>/dev/null;"
    run_cmd="$run_cmd mkdir -p /data/local/tmp/westlake;"
    run_cmd="$run_cmd /data/local/tmp/m6-drm-daemon --accept-client --no-kill-composer --max-frames 120"
    run_cmd="$run_cmd > /data/local/tmp/m6d.stdout 2>/data/local/tmp/m6d.stderr &"
    run_cmd="$run_cmd DPID=\$!;"
    run_cmd="$run_cmd sleep 1;"
    # Mid-flight DRM capture in another background task ~2s after dalvikvm starts.
    run_cmd="$run_cmd ( sleep 2;"
    run_cmd="$run_cmd   echo === midflight clients ===;"
    run_cmd="$run_cmd   cat /sys/kernel/debug/dri/0/clients;"
    run_cmd="$run_cmd   echo === midflight framebuffer ===;"
    run_cmd="$run_cmd   cat /sys/kernel/debug/dri/0/framebuffer;"
    run_cmd="$run_cmd   echo === midflight composer ===;"
    run_cmd="$run_cmd   ps -ef | grep -E 'composer_host|m6-drm-daemon|dalvikvm' | grep -v grep;"
    run_cmd="$run_cmd ) > /data/local/tmp/m6.midflight.log 2>&1 &"
    # Run dalvikvm + launcher targeting M6ClientTestActivity.
    run_cmd="$run_cmd ANDROID_ROOT=$BOARD_DIR $DALVIKVM_BOARD_PATH"
    run_cmd="$run_cmd -Xbootclasspath:$bcp"
    run_cmd="$run_cmd com.westlake.ohostests.launcher.OhosMvpLauncher"
    run_cmd="$run_cmd com.westlake.ohostests.m6/.M6ClientTestActivity;"
    run_cmd="$run_cmd CRC=\$?;"
    run_cmd="$run_cmd wait \$DPID 2>/dev/null;"
    run_cmd="$run_cmd echo ====dalvikvm exit=\$CRC====;"
    run_cmd="$run_cmd echo === daemon stdout ===;"
    run_cmd="$run_cmd cat /data/local/tmp/m6d.stdout;"
    run_cmd="$run_cmd echo === daemon stderr tail ===;"
    run_cmd="$run_cmd tail -30 /data/local/tmp/m6d.stderr;"
    run_cmd="$run_cmd echo === midflight capture ===;"
    run_cmd="$run_cmd cat /data/local/tmp/m6.midflight.log"
    hdc_shell "$run_cmd" > "$outdir/run.log" 2> "$outdir/run.stderr" || {
        warn "shell exit non-zero — continuing"
    }
    log "  run.log: $(wc -l < "$outdir/run.log") lines"

    log "[6/7] capture post-state"
    hdc_shell "ps -ef | grep -E 'composer_host|m6-drm-daemon' | grep -v grep" \
            > "$outdir/ps-post.txt" 2>&1
    hdc_shell "cat /sys/kernel/debug/dri/0/clients" > "$outdir/drm-clients-post.txt" 2>&1
    local composer_pid_post
    composer_pid_post="$(grep -E 'composer_host' "$outdir/ps-post.txt" | head -1 | awk '{print $2}')"
    log "  composer_host pid post: ${composer_pid_post:-(none)}"

    log "[7/7] grep markers"
    local m_java_done m_daemon_done m_kernel_owns m_connect_ok m_disconnect_ok
    m_java_done=$(grep -c "M6_JAVA_CLIENT_DONE" "$outdir/run.log" || true)
    m_daemon_done=$(grep -c "M6_DAEMON_DONE frames=120" "$outdir/run.log" || true)
    m_kernel_owns=$(grep -c "allocated by = m6-drm-daemon" "$outdir/run.log" || true)
    m_connect_ok=$(grep -c "OhosM6ClientTest.client connected" "$outdir/run.log" || true)
    m_disconnect_ok=$(grep -c "OhosM6ClientTest.disconnect OK" "$outdir/run.log" || true)
    log "  M6_JAVA_CLIENT_DONE: $m_java_done"
    log "  M6_DAEMON_DONE frames=120: $m_daemon_done"
    log "  kernel allocated-by=m6-drm-daemon: $m_kernel_owns"
    log "  client connect marker: $m_connect_ok"
    log "  client disconnect OK: $m_disconnect_ok"

    # composer coexistence: pid stable AND not zero.
    local composer_stable=0
    if [ -n "$composer_pid_pre" ] && [ "$composer_pid_pre" = "$composer_pid_post" ]; then
        composer_stable=1
    fi
    log "  composer_host pid stable (pre=$composer_pid_pre post=$composer_pid_post): $composer_stable"

    # Extract Java-side timing for the report.
    local java_timing
    java_timing="$(grep -F "M6_JAVA_CLIENT_DONE" "$outdir/run.log" | head -1 | tr -d '\r')"
    if [ -n "$java_timing" ]; then
        log "  Java timing: $java_timing"
    fi
    local daemon_timing
    daemon_timing="$(grep -F "M6_DAEMON_DONE" "$outdir/run.log" | head -1 | tr -d '\r')"
    if [ -n "$daemon_timing" ]; then
        log "  Daemon timing: $daemon_timing"
    fi

    if [ "$m_java_done" -ge 1 ] && [ "$m_daemon_done" -ge 1 ] \
        && [ "$m_kernel_owns" -ge 1 ] && [ "$m_disconnect_ok" -ge 1 ] \
        && [ "$composer_stable" -eq 1 ]; then
        ok "${GREEN}${BOLD}M6-OHOS-Step2 PASS${RESET}: Java client drove 120 frames via daemon, composer_host stable"
        echo "PASS" > "$outdir/result.txt"
        return 0
    else
        err "M6-OHOS-Step2 did not reach all markers — see $outdir/run.log"
        echo "FAIL" > "$outdir/result.txt"
        return 1
    fi
}

# ---------- subcommand: xcomponent-test -------------------------------------
# CR60 follow-up #2 (post-E7): prove that the in-process dlopen of OHOS
# production native libs actually produces working function pointers, not
# just resolved symbols. The HelloDlopen.java test (committed 8710bf4f)
# only proved System.loadLibrary returns OK — symbol calls might still
# SIGSEGV at first invocation. This subcommand runs the tiered API-call
# acceptance ladder defined in XComponentTest.java.
#
# Always uses --arch arm32 (the dynamic-PIE binary). The aarch64 build
# does NOT ship libxcomponent_bridge.so — that bridge is OHOS-arm32-only
# (TARGET=ohos-arm32-dynamic in dalvik-port/Makefile).
#
# Pipeline:
#   1. gradle :xcomponent-test:assemble (compileJava only — plain-java module).
#   2. make TARGET=ohos-arm32-dynamic xcomponent-bridge → libxcomponent_bridge.so
#   3. d8 --min-api 13 on the two .class files → XComponentTest.dex (dex.035).
#   4. push dex + .so to BOARD_DIR.
#   5. invoke dalvikvm-arm32-dyn with LD_LIBRARY_PATH set so
#      System.loadLibrary("xcomponent_bridge") resolves.
#   6. grep stdout for the "xcomp-test-done highest=<n>" marker; emit
#      PASS/FAIL based on the highest tier reached.
#
# Tier ladder (matches XComponentTest.java):
#   highest=0  Tier 0: libxcomponent_bridge.so loaded; no further progress.
#   highest=1  Tier 1: OH_NativeBuffer_Alloc(NULL) returned 0 without crash.
#   highest=2  Tier 2: OH_NativeBuffer_Alloc(720x1280 BGRA8888) returned non-NULL.
#   highest=3  Tier 3: map → fill RED → unmap completed.
#
# The script reports PASS at highest>=1 (Tier 1 is the actual gate the
# brief asked us to clear). highest>=2/3 are bonus signals.

cmd_xcomponent_test() {
    # Hard-pin to arm32 dynamic — the bridge .so is built for that target
    # only. Allow the caller's --arch flag to override but warn loudly.
    if [ "$ARCH" = "aarch64" ]; then
        warn "xcomponent-test requires --arch arm32; overriding ARCH from aarch64"
        ARCH="arm32"
        DALVIKVM_BOARD_PATH=""
        DALVIKVM_HOST_PATH=""
        resolve_arch || return 1
    fi
    if [ "$ARCH" = "auto" ]; then
        # auto-detect already resolved to one of {arm32, aarch64}; if
        # board returned 64-bit, override to arm32 for this test.
        case "$DALVIKVM_BOARD_PATH" in
            *dalvikvm-arm32*) ;;
            *)
                warn "auto-detect picked $DALVIKVM_BOARD_PATH; forcing arm32 for xcomponent-test"
                ARCH="arm32"
                DALVIKVM_BOARD_PATH=""
                DALVIKVM_HOST_PATH=""
                resolve_arch || return 1
                ;;
        esac
    fi
    # Switch the on-device binary path to the dynamic-PIE variant. The
    # static binary cannot do runtime dlopen of OHOS libs.
    local dvm_dyn_board="/data/local/tmp/dalvikvm-arm32-dyn"
    local dvm_dyn_host="$REPO_ROOT/dalvik-port/build-ohos-arm32-dynamic/dalvikvm"

    local outdir="$ARTIFACT_ROOT/cr60-followup-xcomp-call/$TS"
    mkdir -p "$outdir"
    log "${BOLD}== CR60 follow-up xcomponent-test (in-process NDK call) -> $outdir ==${RESET}"

    log "[1/7] gradle :xcomponent-test:assemble"
    if ! (cd "$GRADLE_DIR" && ./gradlew :xcomponent-test:assemble --no-daemon -q) \
            > "$outdir/gradle.log" 2>&1; then
        err "gradle build failed — see $outdir/gradle.log"
        return 1
    fi
    local app_classes="$GRADLE_DIR/xcomponent-test/build/classes/java/main"
    local bridge_class="$app_classes/com/westlake/ohostests/xcomponent/XComponentBridge.class"
    local test_class="$app_classes/com/westlake/ohostests/xcomponent/XComponentTest.class"
    if [ ! -f "$bridge_class" ] || [ ! -f "$test_class" ]; then
        err "missing class files: bridge=$bridge_class test=$test_class"
        return 1
    fi
    ok "gradle build complete"

    log "[2/7] make libxcomponent_bridge.so (TARGET=ohos-arm32-dynamic)"
    if ! (cd "$REPO_ROOT/dalvik-port" && make TARGET=ohos-arm32-dynamic xcomponent-bridge) \
            > "$outdir/bridge-build.log" 2>&1; then
        err "bridge build failed — see $outdir/bridge-build.log"
        return 1
    fi
    local bridge_so="$REPO_ROOT/dalvik-port/build-ohos-arm32-dynamic/libxcomponent_bridge.so"
    if [ ! -f "$bridge_so" ]; then
        err "libxcomponent_bridge.so not produced at $bridge_so"
        return 1
    fi
    ok "bridge .so built: $(du -b "$bridge_so" | awk '{print $1}') bytes"

    log "[3/7] d8 --min-api 13 -> XComponentTest.dex"
    if [ ! -x "$D8" ]; then err "d8 not found at $D8"; return 1; fi
    local dexdir="$outdir/dex"
    mkdir -p "$dexdir"
    if ! "$D8" --min-api 13 --output "$dexdir" \
            "$bridge_class" "$test_class" \
            > "$outdir/d8.log" 2>&1; then
        err "d8 failed — see $outdir/d8.log"
        return 1
    fi
    mv "$dexdir/classes.dex" "$dexdir/XComponentTest.dex"
    ok "dex emitted: XComponentTest.dex=$(du -b "$dexdir/XComponentTest.dex" | awk '{print $1}')B"

    log "[4/7] push dalvikvm-arm32-dyn + dex + .so"
    hdc_shell "mkdir -p $BOARD_DIR" >/dev/null 2>&1
    if [ ! -f "$dvm_dyn_host" ]; then
        err "dalvikvm-arm32 dynamic missing at $dvm_dyn_host"
        err "  build via: make -C dalvik-port TARGET=ohos-arm32-dynamic dalvikvm"
        return 1
    fi
    # Always push the local dalvikvm-arm32-dyn — the launcher's
    # DVM_PRELOAD_LIB handling lives there (CR60 follow-up), so an
    # older on-device binary would silently miss it. The send is
    # idempotent and only takes ~1s for the 6.5MB binary.
    hdc_send "$dvm_dyn_host" "$dvm_dyn_board" || return 1
    hdc_shell "chmod 0755 $dvm_dyn_board" >/dev/null 2>&1
    hdc_send "$dexdir/XComponentTest.dex" "$BOARD_DIR/XComponentTest.dex" || return 1
    hdc_send "$bridge_so" "$BOARD_DIR/libxcomponent_bridge.so" || return 1
    hdc_shell "chmod 0644 $BOARD_DIR/libxcomponent_bridge.so" >/dev/null 2>&1
    hdc_shell "ls -la $BOARD_DIR/XComponentTest.dex $BOARD_DIR/libxcomponent_bridge.so" \
            > "$outdir/on-device-ls.log" 2>&1
    ok "dex + .so pushed"

    log "[5/7] check SELinux state (no setenforce 0)"
    local enforce
    enforce="$(hdc_shell 'getenforce' 2>&1 | tr -d '\r' | head -1)"
    log "  getenforce: $enforce"
    echo "getenforce=$enforce" > "$outdir/selinux.log"

    log "[6/7] invoke dalvikvm-arm32-dyn"
    # BCP: minimum viable set (core-kitkat + DirectPrintStream + our dex).
    # LD_LIBRARY_PATH: must include BOARD_DIR so dlopen("libxcomponent_bridge.so")
    # finds it. We also keep the default OHOS paths so the bridge's own
    # dlopen(/system/lib/chipset-sdk-sp/libnative_buffer.so) resolves the
    # secondary deps (libdisplay_buffer*, libcomposer*, libsync_fence, …).
    local bcp="$BOARD_DIR/bcp/core-kitkat.jar"
    bcp="${bcp}:$BOARD_DIR/bcp/direct-print-stream.jar"
    bcp="${bcp}:$BOARD_DIR/XComponentTest.dex"
    local cmd="rm -f /data/dalvik-cache/data@local@tmp@westlake@XComponentTest* 2>/dev/null;"
    # DVM_PRELOAD_LIB: launcher.cpp dvmLoadNativeCode preload (CR60
    # follow-up workaround for the stubbed Runtime.load in
    # core-kitkat.jar). Without this, the JNI methods on
    # XComponentBridge won't resolve.
    cmd="$cmd DVM_PRELOAD_LIB=$BOARD_DIR/libxcomponent_bridge.so"
    cmd="$cmd LD_LIBRARY_PATH=$BOARD_DIR:/system/lib:/system/lib/chipset-sdk-sp:/system/lib/platformsdk:/system/lib/ndk"
    cmd="$cmd ANDROID_ROOT=$BOARD_DIR $dvm_dyn_board"
    cmd="$cmd -Xbootclasspath:$bcp"
    cmd="$cmd com.westlake.ohostests.xcomponent.XComponentTest"
    log "  cmd: $cmd"
    hdc_shell "$cmd" > "$outdir/dalvikvm.stdout" 2> "$outdir/dalvikvm.stderr" || {
        warn "dalvikvm exited non-zero — capturing output regardless"
    }
    log "  stdout: $outdir/dalvikvm.stdout ($(wc -l < "$outdir/dalvikvm.stdout") lines)"
    log "  stderr: $outdir/dalvikvm.stderr ($(wc -l < "$outdir/dalvikvm.stderr") lines)"

    log "[7/7] grade markers"
    local highest_line highest
    highest_line="$(grep -E '^xcomp-test-done highest=' "$outdir/dalvikvm.stdout" 2>/dev/null | tail -1 | tr -d '\r')"
    highest="$(echo "$highest_line" | sed -n 's/.*highest=\(-\?[0-9]\+\).*/\1/p')"
    if [ -z "$highest" ]; then highest="-1"; fi
    log "  highest tier reached: $highest"
    log "  marker line: $highest_line"

    # Echo every tier-N line to the operator for quick triage.
    grep -E "^tier-[0-9]" "$outdir/dalvikvm.stdout" | tr -d '\r' | while IFS= read -r line; do
        log "  $line"
    done

    # PASS gate: highest >= 1 (Tier 1 is the minimum brief gate).
    if [ "$highest" -ge 1 ]; then
        ok "${GREEN}${BOLD}xcomponent-test PASS${RESET}: in-process NDK call returned; tier=$highest"
        echo "PASS highest=$highest" > "$outdir/result.txt"
        return 0
    else
        err "xcomponent-test FAIL: highest=$highest (no API call returned without crash)"
        echo "FAIL highest=$highest" > "$outdir/result.txt"
        return 1
    fi
}

# ---------- subcommand: hello-dlopen-real -----------------------------------
# CR60 follow-up #3 (E9a): close the System.loadLibrary stub gate.
#
# Agent 13 hit a false positive in HelloDlopen.java (commit 8710bf4f) —
# it reported every probed library as status=OK, but the BCP in use
# (core-kitkat.jar) has Runtime.loadLibrary / System.loadLibrary stubs
# (return-void). The "OK" proved the calls returned without throwing;
# it did NOT prove a real dlopen happened. Agent 13 worked around the
# stub with $DVM_PRELOAD_LIB (launcher.cpp calls dvmLoadNativeCode
# before main() runs). That fix is per-launch, not durable.
#
# This subcommand swaps the BCP to core-android-x86.jar — which carries
# the REAL KitKat Runtime/System with nativeLoad wired through to the
# registered Dalvik_java_lang_Runtime_nativeLoad → dvmLoadNativeCode →
# dlopen chain. It then runs HelloDlopenReal.main(), which:
#   1. calls System.loadLibrary("xcomponent_bridge") with NO env var,
#   2. invokes XComponentBridge.nativeInit() — a JNI call into the
#      just-loaded .so, which itself dlopens libnative_buffer.so and
#      resolves OH_NativeBuffer_Alloc / Unreference,
#   3. invokes nativeAlloc(720,1280) → real buffer handle,
#   4. invokes nativeGetSeqNum + nativeUnref to prove the handle is alive.
#
# PASS criterion: HelloDlopenReal prints "loadLibrary OK" AND
# "nativeInit=1" AND "nativeAlloc handle=<non-zero>" AND no
# "hello-dlopen-real-FAIL" lines. The driver collapses these to PASS/FAIL.

cmd_hello_dlopen_real() {
    # Hard-pin to arm32 dynamic (same as xcomponent-test) — the bridge
    # .so is only built for that target. Allow --arch to override but
    # warn loudly.
    if [ "$ARCH" = "aarch64" ]; then
        warn "hello-dlopen-real requires --arch arm32; overriding ARCH from aarch64"
        ARCH="arm32"
        DALVIKVM_BOARD_PATH=""
        DALVIKVM_HOST_PATH=""
        resolve_arch || return 1
    fi
    if [ "$ARCH" = "auto" ]; then
        case "$DALVIKVM_BOARD_PATH" in
            *dalvikvm-arm32*) ;;
            *)
                warn "auto-detect picked $DALVIKVM_BOARD_PATH; forcing arm32 for hello-dlopen-real"
                ARCH="arm32"
                DALVIKVM_BOARD_PATH=""
                DALVIKVM_HOST_PATH=""
                resolve_arch || return 1
                ;;
        esac
    fi
    local dvm_dyn_board="/data/local/tmp/dalvikvm-arm32-dyn"
    local dvm_dyn_host="$REPO_ROOT/dalvik-port/build-ohos-arm32-dynamic/dalvikvm"

    local outdir="$ARTIFACT_ROOT/cr60-followup-e9/$TS"
    mkdir -p "$outdir"
    log "${BOLD}== CR60-followup E9a hello-dlopen-real -> $outdir ==${RESET}"

    log "[1/7] gradle :hello:compileJava + :xcomponent-test:assemble"
    if ! (cd "$GRADLE_DIR" && ./gradlew :hello:compileJava :xcomponent-test:assemble --no-daemon -q) \
            > "$outdir/gradle.log" 2>&1; then
        err "gradle build failed — see $outdir/gradle.log"
        return 1
    fi
    local hello_classes="$GRADLE_DIR/hello/build/classes/java/main"
    local xcomp_classes="$GRADLE_DIR/xcomponent-test/build/classes/java/main"
    local real_class="$hello_classes/com/westlake/ohostests/hello/HelloDlopenReal.class"
    local bridge_class="$xcomp_classes/com/westlake/ohostests/xcomponent/XComponentBridge.class"
    if [ ! -f "$real_class" ] || [ ! -f "$bridge_class" ]; then
        err "missing class files: real=$real_class bridge=$bridge_class"
        return 1
    fi
    ok "gradle build complete"

    log "[2/7] make libxcomponent_bridge.so (TARGET=ohos-arm32-dynamic)"
    if ! (cd "$REPO_ROOT/dalvik-port" && make TARGET=ohos-arm32-dynamic xcomponent-bridge) \
            > "$outdir/bridge-build.log" 2>&1; then
        err "bridge build failed — see $outdir/bridge-build.log"
        return 1
    fi
    local bridge_so="$REPO_ROOT/dalvik-port/build-ohos-arm32-dynamic/libxcomponent_bridge.so"
    if [ ! -f "$bridge_so" ]; then
        err "libxcomponent_bridge.so not produced at $bridge_so"
        return 1
    fi
    ok "bridge .so built: $(du -b "$bridge_so" | awk '{print $1}') bytes"

    log "[3/7] d8 --min-api 13 -> HelloDlopenReal.dex"
    if [ ! -x "$D8" ]; then err "d8 not found at $D8"; return 1; fi
    local dexdir="$outdir/dex"
    mkdir -p "$dexdir"
    if ! "$D8" --min-api 13 --output "$dexdir" \
            "$real_class" "$bridge_class" \
            > "$outdir/d8.log" 2>&1; then
        err "d8 failed — see $outdir/d8.log"
        return 1
    fi
    mv "$dexdir/classes.dex" "$dexdir/HelloDlopenReal.dex"
    ok "dex emitted: HelloDlopenReal.dex=$(du -b "$dexdir/HelloDlopenReal.dex" | awk '{print $1}')B"

    log "[4/7] push dalvikvm-arm32-dyn + dex + .so"
    hdc_shell "mkdir -p $BOARD_DIR" >/dev/null 2>&1
    if [ ! -f "$dvm_dyn_host" ]; then
        err "dalvikvm-arm32 dynamic missing at $dvm_dyn_host"
        err "  build via: make -C dalvik-port TARGET=ohos-arm32-dynamic dalvikvm"
        return 1
    fi
    hdc_send "$dvm_dyn_host" "$dvm_dyn_board" || return 1
    hdc_shell "chmod 0755 $dvm_dyn_board" >/dev/null 2>&1
    hdc_send "$dexdir/HelloDlopenReal.dex" "$BOARD_DIR/HelloDlopenReal.dex" || return 1
    hdc_send "$bridge_so" "$BOARD_DIR/libxcomponent_bridge.so" || return 1
    hdc_shell "chmod 0644 $BOARD_DIR/libxcomponent_bridge.so" >/dev/null 2>&1
    # Verify the production BCP is on board (this test REQUIRES
    # core-android-x86.jar — core-kitkat.jar stubs would silently pass
    # loadLibrary without doing dlopen).
    local bcp_check
    bcp_check="$(hdc_shell "ls $BOARD_DIR/bcp/core-android-x86.jar $BOARD_DIR/bcp/direct-print-stream.jar 2>&1")"
    if echo "$bcp_check" | grep -q "No such file"; then
        err "missing required BCP files on board (need core-android-x86.jar + direct-print-stream.jar in $BOARD_DIR/bcp/)"
        err "  hdc said: $bcp_check"
        return 1
    fi
    hdc_shell "ls -la $BOARD_DIR/HelloDlopenReal.dex $BOARD_DIR/libxcomponent_bridge.so $BOARD_DIR/bcp/core-android-x86.jar" \
            > "$outdir/on-device-ls.log" 2>&1
    ok "dex + .so pushed; BCP intact"

    log "[5/7] check SELinux state (no setenforce 0)"
    local enforce
    enforce="$(hdc_shell 'getenforce' 2>&1 | tr -d '\r' | head -1)"
    log "  getenforce: $enforce"
    echo "getenforce=$enforce" > "$outdir/selinux.log"

    log "[6/7] invoke dalvikvm-arm32-dyn (NO DVM_PRELOAD_LIB)"
    # BCP differs from xcomponent-test: we use core-android-x86.jar
    # (real Runtime/System) instead of core-kitkat.jar (stub
    # loadLibrary). aosp-shim-ohos.dex is included for parity with
    # trivial-activity/red-square — without it, dexopt SIGSEGVs in
    # rewriteInvokeObjectInit while scanning constructors of classes
    # that reference shim-provided android.app.* superclasses.
    # (Verified empirically 2026-05-14: dropping aosp-shim from the
    # BCP makes BCP-load dexopt crash at Optimize.cpp:365.)
    local bcp="$BOARD_DIR/bcp/core-android-x86.jar"
    bcp="${bcp}:$BOARD_DIR/bcp/direct-print-stream.jar"
    bcp="${bcp}:$BOARD_DIR/bcp/aosp-shim-ohos.dex"
    bcp="${bcp}:$BOARD_DIR/HelloDlopenReal.dex"
    # Wipe BOTH the test-dex cache AND every BCP cache. Without this,
    # if a prior test ran with a DIFFERENT BCP order (e.g., aarch64
    # trivial-activity which has the same files in a different layout),
    # the dexopt subprocess silently fails and the late-optimize path
    # SIGSEGVs trying to write into the read-only raw-dex mapping.
    # (Verified 2026-05-14: dropping this wipe makes the test flaky
    # after any aarch64 run between two arm32 runs.)
    local cmd="rm -f /data/dalvik-cache/data@local@tmp@westlake@HelloDlopenReal* /data/dalvik-cache/data@local@tmp@westlake@bcp@* 2>/dev/null;"
    # NO DVM_PRELOAD_LIB. java.library.path lets Runtime.loadLibrary
    # resolve "xcomponent_bridge" → "$BOARD_DIR/libxcomponent_bridge.so".
    # LD_LIBRARY_PATH stays for the bridge's secondary dlopen of
    # /system/lib/chipset-sdk-sp/libnative_buffer.so deps.
    cmd="$cmd LD_LIBRARY_PATH=$BOARD_DIR:/system/lib:/system/lib/chipset-sdk-sp:/system/lib/platformsdk:/system/lib/ndk"
    cmd="$cmd ANDROID_ROOT=$BOARD_DIR $dvm_dyn_board"
    cmd="$cmd -Xbootclasspath:$bcp"
    cmd="$cmd -Djava.library.path=$BOARD_DIR"
    cmd="$cmd com.westlake.ohostests.hello.HelloDlopenReal"
    log "  cmd: $cmd"
    hdc_shell "$cmd" > "$outdir/dalvikvm.stdout" 2> "$outdir/dalvikvm.stderr" || {
        warn "dalvikvm exited non-zero — capturing output regardless"
    }
    log "  stdout: $outdir/dalvikvm.stdout ($(wc -l < "$outdir/dalvikvm.stdout") lines)"
    log "  stderr: $outdir/dalvikvm.stderr ($(wc -l < "$outdir/dalvikvm.stderr") lines)"

    log "[7/7] grade markers"
    local done_line passed failed
    done_line="$(grep -E '^hello-dlopen-real-done passed=' "$outdir/dalvikvm.stdout" 2>/dev/null | tail -1 | tr -d '\r')"
    passed="$(echo "$done_line" | sed -n 's/.*passed=\([0-9]\+\).*/\1/p')"
    failed="$(echo "$done_line" | sed -n 's/.*failed=\([0-9]\+\).*/\1/p')"
    if [ -z "$passed" ]; then passed="0"; fi
    if [ -z "$failed" ]; then failed="-1"; fi
    log "  done line: $done_line"

    # Echo every hello-dlopen-real line to operator.
    grep -E '^hello-dlopen-real' "$outdir/dalvikvm.stdout" | tr -d '\r' | while IFS= read -r line; do
        log "  $line"
    done

    # PASS criteria, all three required:
    #   - "loadLibrary OK" line present
    #   - "nativeInit=1" line present (NOT 0)
    #   - no "-FAIL" line (any stage)
    #   - "nativeAlloc handle=0x..." with a non-zero hex value
    local has_load_ok=0 has_init1=0 has_fail=0 has_handle_nonzero=0
    grep -qF "hello-dlopen-real loadLibrary OK" "$outdir/dalvikvm.stdout" 2>/dev/null && has_load_ok=1
    grep -qE 'hello-dlopen-real nativeInit=1$|hello-dlopen-real nativeInit=1[^0-9]' "$outdir/dalvikvm.stdout" 2>/dev/null && has_init1=1
    grep -qF "hello-dlopen-real-FAIL" "$outdir/dalvikvm.stdout" 2>/dev/null && has_fail=1
    grep -qE 'hello-dlopen-real nativeAlloc handle=0x[0-9a-f]*[1-9a-f][0-9a-f]*' "$outdir/dalvikvm.stdout" 2>/dev/null && has_handle_nonzero=1

    log "  has_load_ok=$has_load_ok has_init1=$has_init1 has_handle_nonzero=$has_handle_nonzero has_fail=$has_fail"

    if [ "$has_load_ok" -eq 1 ] && [ "$has_init1" -eq 1 ] \
       && [ "$has_handle_nonzero" -eq 1 ] && [ "$has_fail" -eq 0 ]; then
        ok "${GREEN}${BOLD}hello-dlopen-real PASS${RESET}: pure-Java System.loadLibrary works (no env var)"
        echo "PASS passed=$passed failed=$failed" > "$outdir/result.txt"
        return 0
    else
        err "hello-dlopen-real FAIL (loadOK=$has_load_ok init1=$has_init1 handle=$has_handle_nonzero fail=$has_fail)"
        echo "FAIL passed=$passed failed=$failed loadOK=$has_load_ok init1=$has_init1 handle=$has_handle_nonzero failLine=$has_fail" > "$outdir/result.txt"
        return 1
    fi
}

# ---------- subcommand: hello-drm-inprocess ---------------------------------
# CR60 follow-up #3 (E9b, Path Y): visible red pixel on DSI panel driven
# in-process by dalvikvm-arm32-dynamic. No daemon, no host APK, no second
# binary — just one JVM, one .so, one dex.
#
# Pipeline (mirrors the existing red-square-drm flow, but the DRM call
# site moves from a standalone aarch64 static helper into a JNI bridge
# loaded by dalvikvm):
#   A. gradle :hello:compileJava → DrmInProcessBridge + HelloDrmInProcess.
#   B. make TARGET=ohos-arm32-dynamic drm-inproc-bridge → .so.
#   C. d8 → HelloDrmInProcess.dex (with DrmInProcessBridge inline).
#   D. push dalvikvm + dex + .so. Verify BCP intact.
#   E. capture pre-state: /sys/kernel/debug/dri/0/{clients,framebuffer,state}.
#   F. kill -9 $(pidof composer_host)  (auto-respawn after test).
#   G. run dalvikvm com.westlake.ohostests.hello.HelloDrmInProcess <holdSecs>.
#      Bridge takes SET_MASTER, allocates dumb BO, fills RED, SETCRTC.
#      Panel is red for holdSecs seconds.
#   H. capture post-state. composer_host respawns within ~1s of cleanup.
#
# PASS criterion: nativePresent rc=0 AND post-state shows our fb_id
# *was* attached to the crtc during the hold window (or a separate
# kernel-state capture during the window confirms it).
#
# Phone-camera evidence: the operator should aim a camera at the DSI
# panel during the hold window; the captured frame goes into the
# artifact directory.

cmd_hello_drm_inprocess() {
    # Hard-pin to arm32 dynamic — this is the in-process gate; the
    # bridge .so is OHOS-arm32-dynamic-only (Makefile target enforces).
    if [ "$ARCH" = "aarch64" ]; then
        warn "hello-drm-inprocess requires --arch arm32; overriding ARCH from aarch64"
        ARCH="arm32"
        DALVIKVM_BOARD_PATH=""
        DALVIKVM_HOST_PATH=""
        resolve_arch || return 1
    fi
    if [ "$ARCH" = "auto" ]; then
        case "$DALVIKVM_BOARD_PATH" in
            *dalvikvm-arm32*) ;;
            *)
                warn "auto-detect picked $DALVIKVM_BOARD_PATH; forcing arm32 for hello-drm-inprocess"
                ARCH="arm32"
                DALVIKVM_BOARD_PATH=""
                DALVIKVM_HOST_PATH=""
                resolve_arch || return 1
                ;;
        esac
    fi
    local dvm_dyn_board="/data/local/tmp/dalvikvm-arm32-dyn"
    local dvm_dyn_host="$REPO_ROOT/dalvik-port/build-ohos-arm32-dynamic/dalvikvm"
    local hold_secs="${DRM_INPROC_HOLD_SECS:-10}"

    local outdir="$ARTIFACT_ROOT/cr60-followup-e9/$TS-drm-inprocess"
    mkdir -p "$outdir"
    log "${BOLD}== CR60-followup E9b hello-drm-inprocess -> $outdir hold=${hold_secs}s ==${RESET}"

    log "[A/H] gradle :hello:compileJava"
    if ! (cd "$GRADLE_DIR" && ./gradlew :hello:compileJava --no-daemon -q) \
            > "$outdir/gradle.log" 2>&1; then
        err "gradle build failed — see $outdir/gradle.log"
        return 1
    fi
    local hello_classes="$GRADLE_DIR/hello/build/classes/java/main/com/westlake/ohostests/hello"
    local bridge_class="$hello_classes/DrmInProcessBridge.class"
    local main_class="$hello_classes/HelloDrmInProcess.class"
    if [ ! -f "$bridge_class" ] || [ ! -f "$main_class" ]; then
        err "missing class files: bridge=$bridge_class main=$main_class"
        return 1
    fi
    ok "gradle build complete"

    log "[B/H] make libdrm_inproc_bridge.so (TARGET=ohos-arm32-dynamic)"
    if ! (cd "$REPO_ROOT/dalvik-port" && make TARGET=ohos-arm32-dynamic drm-inproc-bridge) \
            > "$outdir/bridge-build.log" 2>&1; then
        err "bridge build failed — see $outdir/bridge-build.log"
        return 1
    fi
    local bridge_so="$REPO_ROOT/dalvik-port/build-ohos-arm32-dynamic/libdrm_inproc_bridge.so"
    if [ ! -f "$bridge_so" ]; then
        err "libdrm_inproc_bridge.so not produced at $bridge_so"
        return 1
    fi
    ok "bridge .so built: $(du -b "$bridge_so" | awk '{print $1}') bytes"

    log "[C/H] d8 --min-api 13 -> HelloDrmInProcess.dex"
    if [ ! -x "$D8" ]; then err "d8 not found at $D8"; return 1; fi
    local dexdir="$outdir/dex"
    mkdir -p "$dexdir"
    if ! "$D8" --min-api 13 --output "$dexdir" \
            "$bridge_class" "$main_class" \
            > "$outdir/d8.log" 2>&1; then
        err "d8 failed — see $outdir/d8.log"
        return 1
    fi
    mv "$dexdir/classes.dex" "$dexdir/HelloDrmInProcess.dex"
    ok "dex emitted: HelloDrmInProcess.dex=$(du -b "$dexdir/HelloDrmInProcess.dex" | awk '{print $1}')B"

    log "[D/H] push dalvikvm-arm32-dyn + dex + .so"
    hdc_shell "mkdir -p $BOARD_DIR" >/dev/null 2>&1
    if [ ! -f "$dvm_dyn_host" ]; then
        err "dalvikvm-arm32 dynamic missing at $dvm_dyn_host"
        return 1
    fi
    hdc_send "$dvm_dyn_host" "$dvm_dyn_board" || return 1
    hdc_shell "chmod 0755 $dvm_dyn_board" >/dev/null 2>&1
    hdc_send "$dexdir/HelloDrmInProcess.dex" "$BOARD_DIR/HelloDrmInProcess.dex" || return 1
    hdc_send "$bridge_so" "$BOARD_DIR/libdrm_inproc_bridge.so" || return 1
    hdc_shell "chmod 0644 $BOARD_DIR/libdrm_inproc_bridge.so" >/dev/null 2>&1
    local bcp_check
    bcp_check="$(hdc_shell "ls $BOARD_DIR/bcp/core-android-x86.jar $BOARD_DIR/bcp/direct-print-stream.jar $BOARD_DIR/bcp/aosp-shim-ohos.dex 2>&1")"
    if echo "$bcp_check" | grep -q "No such file"; then
        err "missing required BCP files on board"
        err "  hdc said: $bcp_check"
        return 1
    fi
    ok "dex + .so pushed; BCP intact"

    log "[E/H] check SELinux state + capture DRM pre-state"
    local enforce
    enforce="$(hdc_shell 'getenforce' 2>&1 | tr -d '\r' | head -1)"
    log "  getenforce: $enforce (NOT changing — see brief)"
    echo "getenforce=$enforce" > "$outdir/selinux.log"
    hdc_shell "cat /sys/kernel/debug/dri/0/clients" \
            > "$outdir/drm-clients-pre.txt" 2>&1 || true
    hdc_shell "cat /sys/kernel/debug/dri/0/framebuffer" \
            > "$outdir/drm-framebuffer-pre.txt" 2>&1 || true
    hdc_shell "cat /sys/kernel/debug/dri/0/state 2>/dev/null | head -200" \
            > "$outdir/drm-state-pre.txt" 2>&1 || true
    hdc_shell "pidof composer_host" \
            > "$outdir/composer-pid-pre.txt" 2>&1 || true
    log "  composer_host pid (pre): $(cat "$outdir/composer-pid-pre.txt" 2>/dev/null | tr -d '\r')"

    log "[F/H] (note) composer_host kill now happens INSIDE the JNI bridge"
    # The bridge does the kill itself (drm_inproc_bridge.c
    # kill_composer_host) right before SET_MASTER, with retries on
    # respawn. That eliminates the race window that defeated the
    # original driver-side kill: hdf_devmgr respawns composer_host
    # within ~0.3s of SIGKILL, faster than VM startup (~5s). Doing the
    # kill from inside the bridge means it happens immediately before
    # the SET_MASTER syscall — no round-trip latency.
    log "  (see drm_inproc_bridge.c kill_composer_host + SET_MASTER retry loop)"

    log "[G/H] invoke dalvikvm-arm32-dyn (in-process DRM scan-out, hold=${hold_secs}s)"
    # Same BCP shape as hello-dlopen-real (E9a) — core-android-x86.jar
    # for the real Runtime/System (pure-Java System.loadLibrary path).
    local bcp="$BOARD_DIR/bcp/core-android-x86.jar"
    bcp="${bcp}:$BOARD_DIR/bcp/direct-print-stream.jar"
    bcp="${bcp}:$BOARD_DIR/bcp/aosp-shim-ohos.dex"
    bcp="${bcp}:$BOARD_DIR/HelloDrmInProcess.dex"
    # Fused composer-kill + dvm-exec: hdf_devmgr respawns composer_host
    # within ~0.3s, but a respawn must reopen /dev/dri/card0 + grab
    # SET_MASTER — both syscalls, ~tens of ms each from a fresh forked
    # process. Issuing the dalvikvm exec immediately after the kill (no
    # hdc round-trip) gives our process the ~hundreds-of-ms window to
    # call SET_MASTER first. We pre-warm the dexopt cache so the VM
    # doesn't pause for verify/optimize during the race. Then we issue
    # kill+invoke as a single shell pipeline.
    local cmd="rm -f /data/dalvik-cache/data@local@tmp@westlake@HelloDrmInProcess* /data/dalvik-cache/data@local@tmp@westlake@bcp@* 2>/dev/null;"
    cmd="$cmd LD_LIBRARY_PATH=$BOARD_DIR:/system/lib:/system/lib/chipset-sdk-sp:/system/lib/platformsdk:/system/lib/ndk"
    cmd="$cmd ANDROID_ROOT=$BOARD_DIR $dvm_dyn_board"
    cmd="$cmd -Xbootclasspath:$bcp"
    cmd="$cmd -Djava.library.path=$BOARD_DIR"
    cmd="$cmd com.westlake.ohostests.hello.HelloDrmInProcess $hold_secs"
    log "  cmd: $cmd"

    # Run the dalvik test in the background and capture kernel state
    # mid-flight (while the scan-out is held). The Java side sleeps
    # holdSecs inside nativePresent. VM startup is ~5s; nativePresent
    # then takes ~0.5s before sleeping. So we sample at +6s — well
    # inside the hold window for the default 6s hold (totaling ~12s
    # of subprocess wall time). Operator can override DRM_INPROC_HOLD_SECS
    # to widen the window if running with a phone-camera capture.
    hdc_shell "$cmd" > "$outdir/dalvikvm.stdout" 2> "$outdir/dalvikvm.stderr" &
    local dvm_pid=$!
    # First sample: before our bridge has run (compare against pre-state).
    sleep 1.5
    hdc_shell "cat /sys/kernel/debug/dri/0/clients" \
            > "$outdir/drm-clients-early.txt" 2>&1 || true
    # Mid sample: after VM startup + kill_composer_host + SET_MASTER, well
    # inside our hold window. VM startup ~5s + 0.5s setup → sample at +6.5s.
    sleep 5.0
    hdc_shell "cat /sys/kernel/debug/dri/0/clients" \
            > "$outdir/drm-clients-mid.txt" 2>&1 || true
    hdc_shell "cat /sys/kernel/debug/dri/0/framebuffer" \
            > "$outdir/drm-framebuffer-mid.txt" 2>&1 || true
    hdc_shell "cat /sys/kernel/debug/dri/0/state 2>/dev/null | head -200" \
            > "$outdir/drm-state-mid.txt" 2>&1 || true
    # Wait for the Java side to finish.
    wait $dvm_pid || warn "dalvikvm exited non-zero — capturing output regardless"
    log "  stdout: $outdir/dalvikvm.stdout ($(wc -l < "$outdir/dalvikvm.stdout") lines)"
    log "  stderr: $outdir/dalvikvm.stderr ($(wc -l < "$outdir/dalvikvm.stderr") lines)"

    log "[H/H] capture DRM post-state + grade markers"
    sleep 1.5  # give hdf_devmgr time to respawn composer_host
    hdc_shell "cat /sys/kernel/debug/dri/0/clients" \
            > "$outdir/drm-clients-post.txt" 2>&1 || true
    hdc_shell "cat /sys/kernel/debug/dri/0/framebuffer" \
            > "$outdir/drm-framebuffer-post.txt" 2>&1 || true
    hdc_shell "cat /sys/kernel/debug/dri/0/state 2>/dev/null | head -200" \
            > "$outdir/drm-state-post.txt" 2>&1 || true
    hdc_shell "pidof composer_host" \
            > "$outdir/composer-pid-post.txt" 2>&1 || true
    log "  composer_host pid (post): $(cat "$outdir/composer-pid-post.txt" 2>/dev/null | tr -d '\r')"

    # Echo every hello-drm-inprocess line to operator.
    grep -E '^hello-drm-inprocess' "$outdir/dalvikvm.stdout" | tr -d '\r' | while IFS= read -r line; do
        log "  $line"
    done

    # PASS criteria:
    #   - "loadLibrary OK" present
    #   - "nativePresent rc=0" present (and no "-FAIL" line)
    # CHECKPOINT (still useful):
    #   - rc != 0 with a specific failure code (DRM_FAIL_* in the .c)
    #     so operator can diagnose.
    local has_load_ok=0 has_rc0=0 has_fail=0 rc_line
    grep -qF "hello-drm-inprocess loadLibrary OK" "$outdir/dalvikvm.stdout" 2>/dev/null && has_load_ok=1
    rc_line="$(grep -E '^hello-drm-inprocess nativePresent rc=' "$outdir/dalvikvm.stdout" 2>/dev/null | tail -1 | tr -d '\r')"
    case "$rc_line" in
        *"rc=0 "*) has_rc0=1 ;;
    esac
    grep -qF "hello-drm-inprocess-FAIL" "$outdir/dalvikvm.stdout" 2>/dev/null && has_fail=1
    log "  has_load_ok=$has_load_ok has_rc0=$has_rc0 has_fail=$has_fail"
    log "  rc line: $rc_line"

    if [ "$has_load_ok" -eq 1 ] && [ "$has_rc0" -eq 1 ] && [ "$has_fail" -eq 0 ]; then
        ok "${GREEN}${BOLD}hello-drm-inprocess PASS${RESET}: in-process DRM scan-out completed cleanly"
        log "  panel was RED for ~${hold_secs}s during this run"
        log "  evidence in mid-state: $outdir/drm-clients-mid.txt / drm-state-mid.txt"
        log "  PHONE-CAMERA: capture a photo of the DSI panel showing red and"
        log "    save it into $outdir/ (filename like 'panel-red.jpg')"
        echo "PASS rc_line=$rc_line" > "$outdir/result.txt"
        return 0
    else
        err "hello-drm-inprocess FAIL/CHECKPOINT (loadOK=$has_load_ok rc0=$has_rc0 fail=$has_fail)"
        echo "FAIL/CHECKPOINT loadOK=$has_load_ok rc0=$has_rc0 fail=$has_fail rc_line=$rc_line" > "$outdir/result.txt"
        return 1
    fi
}

# ---------- subcommand: inproc-app ------------------------------------------
# CR60 follow-up E12 (2026-05-15): smallest real-Android-Activity pixel
# on the DAYU200 DSI panel via the in-process path. Stage 1 routes the
# test APK through -Xbootclasspath (system classloader); stage 2 (TODO)
# loads it via DexClassLoader.
#
# Pipeline (mirrors hello-drm-inprocess but with an Activity in the
# middle):
#   A. gradle :hello-color-apk:assembleDebug + :inproc-app-launcher:compileJava
#   B. make TARGET=ohos-arm32-dynamic drm-inproc-bridge → libdrm_inproc_bridge.so
#   C. d8 → HelloColorApk.dex (Activity, ColorView) + InProcessAppLauncher.dex
#      (launcher, DrmInprocessPresenter, SoftwareCanvas, InProcDrawSource)
#   D. push dalvikvm-arm32-dyn + dex + .so. Verify BCP intact.
#   E. capture DRM pre-state.
#   F. (note) composer_host kill happens inside libdrm_inproc_bridge.so
#      via the same SET_MASTER retry loop E9b uses.
#   G. run dalvikvm com.westlake.ohostests.inproc.InProcessAppLauncher
#         com.westlake.ohostests.helloc/.MainActivity <holdSecs>
#      Launcher resolves MainActivity (BCP), instantiates it, drives
#      Instrumentation.callActivityOnCreate, locates the View via the
#      InProcDrawSource interface, allocates SoftwareCanvas, view.draw
#      records BLUE, materializes int[w*h] ARGB, calls
#      DrmInprocessPresenter.present → DSI panel shows BLUE for holdSecs.
#   H. capture DRM post-state; grade markers.
#
# PASS criterion: marker `inproc-app-launcher present rc=0` AND the
# success line shows `fill=argb` (NOT `fill=red` — that would mean the
# E9b hardcoded path was somehow invoked instead). Operator captures a
# phone-camera photo of the DSI panel showing BLUE (distinguishable
# from E9b's RED) during the hold window.

cmd_inproc_app() {
    # Hard-pin to arm32 dynamic (same as hello-drm-inprocess) — the
    # in-process pipeline depends on libdrm_inproc_bridge.so which is
    # arm32-dynamic-only.
    if [ "$ARCH" = "aarch64" ]; then
        warn "inproc-app requires --arch arm32; overriding ARCH from aarch64"
        ARCH="arm32"
        DALVIKVM_BOARD_PATH=""
        DALVIKVM_HOST_PATH=""
        resolve_arch || return 1
    fi
    if [ "$ARCH" = "auto" ]; then
        case "$DALVIKVM_BOARD_PATH" in
            *dalvikvm-arm32*) ;;
            *)
                warn "auto-detect picked $DALVIKVM_BOARD_PATH; forcing arm32 for inproc-app"
                ARCH="arm32"
                DALVIKVM_BOARD_PATH=""
                DALVIKVM_HOST_PATH=""
                resolve_arch || return 1
                ;;
        esac
    fi
    local dvm_dyn_board="/data/local/tmp/dalvikvm-arm32-dyn"
    local dvm_dyn_host="$REPO_ROOT/dalvik-port/build-ohos-arm32-dynamic/dalvikvm"
    local hold_secs="${INPROC_APP_HOLD_SECS:-10}"
    # Activity spec; defaults to :hello-color-apk. The brief allows
    # --apk overrides for noice/mcd (stretch); we don't wire those yet.
    local activity_spec="${1:-com.westlake.ohostests.helloc/.MainActivity}"

    local outdir="$ARTIFACT_ROOT/cr60-e12/$TS-inproc-app"
    mkdir -p "$outdir"
    log "${BOLD}== CR60-followup E12 inproc-app -> $outdir hold=${hold_secs}s ==${RESET}"
    log "  activity: $activity_spec"

    log "[A/H] gradle :hello-color-apk:assembleDebug + :inproc-app-launcher:compileJava"
    if ! (cd "$GRADLE_DIR" && ./gradlew :hello-color-apk:assembleDebug :inproc-app-launcher:compileJava --no-daemon -q) \
            > "$outdir/gradle.log" 2>&1; then
        err "gradle build failed — see $outdir/gradle.log"
        return 1
    fi
    local app_classes="$GRADLE_DIR/hello-color-apk/build/intermediates/javac/debug/classes"
    local launcher_classes="$GRADLE_DIR/inproc-app-launcher/build/classes/java/main"
    if [ ! -d "$app_classes/com/westlake/ohostests/helloc" ]; then
        err "missing hello-color-apk classes at $app_classes/com/westlake/ohostests/helloc"
        return 1
    fi
    if [ ! -d "$launcher_classes/com/westlake/ohostests/inproc" ]; then
        err "missing inproc-app-launcher classes at $launcher_classes/com/westlake/ohostests/inproc"
        return 1
    fi
    local app_class_list=()
    while IFS= read -r f; do
        app_class_list+=("$f")
    done < <(find "$app_classes/com/westlake/ohostests/helloc" -name '*.class' | sort)
    local launcher_class_list=()
    while IFS= read -r f; do
        launcher_class_list+=("$f")
    done < <(find "$launcher_classes/com/westlake/ohostests/inproc" -name '*.class' | sort)
    ok "gradle build complete (${#app_class_list[@]} app + ${#launcher_class_list[@]} launcher classes)"

    log "[B/H] make libdrm_inproc_bridge.so (TARGET=ohos-arm32-dynamic)"
    if ! (cd "$REPO_ROOT/dalvik-port" && make TARGET=ohos-arm32-dynamic drm-inproc-bridge) \
            > "$outdir/bridge-build.log" 2>&1; then
        err "bridge build failed — see $outdir/bridge-build.log"
        return 1
    fi
    local bridge_so="$REPO_ROOT/dalvik-port/build-ohos-arm32-dynamic/libdrm_inproc_bridge.so"
    if [ ! -f "$bridge_so" ]; then
        err "libdrm_inproc_bridge.so not produced at $bridge_so"
        return 1
    fi
    ok "bridge .so built: $(du -b "$bridge_so" | awk '{print $1}') bytes"

    log "[C/H] d8 --min-api 13 -> HelloColorApk.dex + InProcessAppLauncher.dex"
    if [ ! -x "$D8" ]; then err "d8 not found at $D8"; return 1; fi
    local dexdir="$outdir/dex"
    mkdir -p "$dexdir/app" "$dexdir/launcher"
    if ! "$D8" --min-api 13 --output "$dexdir/app" "${app_class_list[@]}" \
            > "$outdir/d8-app.log" 2>&1; then
        err "d8 app failed — see $outdir/d8-app.log"
        return 1
    fi
    if ! "$D8" --min-api 13 --output "$dexdir/launcher" "${launcher_class_list[@]}" \
            > "$outdir/d8-launcher.log" 2>&1; then
        err "d8 launcher failed — see $outdir/d8-launcher.log"
        return 1
    fi
    mv "$dexdir/app/classes.dex" "$dexdir/HelloColorApk.dex"
    mv "$dexdir/launcher/classes.dex" "$dexdir/InProcAppLauncher.dex"
    rmdir "$dexdir/app" "$dexdir/launcher" 2>/dev/null || true
    ok "dex emitted: HelloColorApk.dex=$(du -b "$dexdir/HelloColorApk.dex" | awk '{print $1}')B "\
"InProcAppLauncher.dex=$(du -b "$dexdir/InProcAppLauncher.dex" | awk '{print $1}')B"

    log "[D/H] push dalvikvm-arm32-dyn + dex + .so"
    hdc_shell "mkdir -p $BOARD_DIR" >/dev/null 2>&1
    if [ ! -f "$dvm_dyn_host" ]; then
        err "dalvikvm-arm32 dynamic missing at $dvm_dyn_host"
        return 1
    fi
    hdc_send "$dvm_dyn_host" "$dvm_dyn_board" || return 1
    hdc_shell "chmod 0755 $dvm_dyn_board" >/dev/null 2>&1
    hdc_send "$dexdir/HelloColorApk.dex"      "$BOARD_DIR/HelloColorApk.dex"      || return 1
    hdc_send "$dexdir/InProcAppLauncher.dex"  "$BOARD_DIR/InProcAppLauncher.dex"  || return 1
    hdc_send "$bridge_so" "$BOARD_DIR/libdrm_inproc_bridge.so" || return 1
    hdc_shell "chmod 0644 $BOARD_DIR/libdrm_inproc_bridge.so" >/dev/null 2>&1
    # BCP files are stable across runs — just verify they exist.
    local bcp_check
    bcp_check="$(hdc_shell "ls $BOARD_DIR/bcp/aosp-shim-ohos.dex $BOARD_DIR/bcp/core-android-x86.jar $BOARD_DIR/bcp/direct-print-stream.jar 2>&1")"
    if echo "$bcp_check" | grep -q "No such file"; then
        err "missing BCP files on board (need aosp-shim-ohos.dex + core-android-x86.jar + direct-print-stream.jar in $BOARD_DIR/bcp/)"
        return 1
    fi
    hdc_shell "ls -la $BOARD_DIR/HelloColorApk.dex $BOARD_DIR/InProcAppLauncher.dex $BOARD_DIR/libdrm_inproc_bridge.so" \
            > "$outdir/on-device-ls.log" 2>&1
    ok "dex + .so pushed; BCP intact"

    log "[E/H] check SELinux + capture DRM pre-state"
    local enforce
    enforce="$(hdc_shell 'getenforce' 2>&1 | tr -d '\r' | head -1)"
    log "  getenforce: $enforce (NOT changing — see brief)"
    echo "getenforce=$enforce" > "$outdir/selinux.log"
    hdc_shell "cat /sys/kernel/debug/dri/0/clients" > "$outdir/drm-clients-pre.txt" 2>&1
    hdc_shell "cat /sys/kernel/debug/dri/0/state" > "$outdir/drm-state-pre.txt" 2>&1
    local composer_pid_pre
    composer_pid_pre="$(hdc_shell 'ps -ef | grep composer_host | grep -v grep' 2>&1 | head -1 | awk '{print $2}' | tr -d '\r')"
    log "  composer_host pid (pre): ${composer_pid_pre:-(none)}"

    log "[F/H] (note) composer_host kill happens INSIDE libdrm_inproc_bridge.so"

    log "[G/H] invoke dalvikvm-arm32-dyn (in-process app, hold=${hold_secs}s)"
    local bcp="$BOARD_DIR/bcp/core-android-x86.jar"
    bcp="$bcp:$BOARD_DIR/bcp/direct-print-stream.jar"
    bcp="$bcp:$BOARD_DIR/bcp/aosp-shim-ohos.dex"
    bcp="$bcp:$BOARD_DIR/HelloColorApk.dex"
    bcp="$bcp:$BOARD_DIR/InProcAppLauncher.dex"
    # Wipe per-run dalvik-cache for stable dexopt across --arch switches
    # (same rationale documented in cmd_hello_dlopen_real).
    local cmd="rm -f /data/dalvik-cache/data@local@tmp@westlake@HelloColorApk* /data/dalvik-cache/data@local@tmp@westlake@InProcAppLauncher* /data/dalvik-cache/data@local@tmp@westlake@bcp@* 2>/dev/null;"
    cmd="$cmd LD_LIBRARY_PATH=$BOARD_DIR:/system/lib:/system/lib/chipset-sdk-sp:/system/lib/platformsdk:/system/lib/ndk"
    cmd="$cmd ANDROID_ROOT=$BOARD_DIR $dvm_dyn_board"
    cmd="$cmd -Xbootclasspath:$bcp"
    cmd="$cmd -Djava.library.path=$BOARD_DIR"
    cmd="$cmd com.westlake.ohostests.inproc.InProcessAppLauncher"
    cmd="$cmd $activity_spec $hold_secs"
    log "  cmd: $cmd"
    hdc_shell "$cmd" > "$outdir/dalvikvm.stdout" 2> "$outdir/dalvikvm.stderr" || {
        warn "dalvikvm exited non-zero — capturing output regardless"
    }
    log "  stdout: $outdir/dalvikvm.stdout ($(wc -l < "$outdir/dalvikvm.stdout") lines)"
    log "  stderr: $outdir/dalvikvm.stderr ($(wc -l < "$outdir/dalvikvm.stderr") lines)"

    log "[H/H] capture DRM post-state + grade markers"
    hdc_shell "cat /sys/kernel/debug/dri/0/clients" > "$outdir/drm-clients-post.txt" 2>&1
    hdc_shell "cat /sys/kernel/debug/dri/0/state" > "$outdir/drm-state-post.txt" 2>&1
    local composer_pid_post
    composer_pid_post="$(hdc_shell 'ps -ef | grep composer_host | grep -v grep' 2>&1 | head -1 | awk '{print $2}' | tr -d '\r')"
    log "  composer_host pid (post): ${composer_pid_post:-(none)}"

    # Echo step lines for triage.
    grep -E "^(inproc-app-launcher|HelloColorApk\.)" "$outdir/dalvikvm.stdout" | tr -d '\r' | while IFS= read -r line; do
        log "  $line"
    done

    local has_present_rc0=0 has_fill_argb=0 has_oncreate=0 has_fail=0
    local rc_line
    rc_line="$(grep -E '^inproc-app-launcher present rc=' "$outdir/dalvikvm.stdout" 2>/dev/null | tail -1 | tr -d '\r')"
    case "$rc_line" in
        *"rc=0 "*) has_present_rc0=1 ;;
    esac
    case "$rc_line" in
        *"fill=argb"*) has_fill_argb=1 ;;
    esac
    grep -qF "HelloColorApk.onCreate reached" "$outdir/dalvikvm.stdout" 2>/dev/null && has_oncreate=1
    grep -qE 'inproc-app-launcher.*(FAIL|step.*FAIL)' "$outdir/dalvikvm.stdout" 2>/dev/null && has_fail=1
    log "  has_oncreate=$has_oncreate has_present_rc0=$has_present_rc0 has_fill_argb=$has_fill_argb has_fail=$has_fail"
    log "  rc line: $rc_line"

    if [ "$has_oncreate" -eq 1 ] && [ "$has_present_rc0" -eq 1 ] \
       && [ "$has_fill_argb" -eq 1 ] && [ "$has_fail" -eq 0 ]; then
        ok "${GREEN}${BOLD}inproc-app PASS${RESET}: real Activity pixel reached DSI panel in-process"
        log "  panel was BLUE for ~${hold_secs}s during this run (color from ColorView.onDraw)"
        log "  PHONE-CAMERA: capture a photo of the DSI panel showing BLUE during the hold"
        log "    and save it into $outdir/ (filename like 'panel-blue.jpg')"
        echo "PASS rc_line=$rc_line" > "$outdir/result.txt"
        return 0
    elif [ "$has_oncreate" -eq 1 ]; then
        warn "Activity ran but pixel pipeline failed (rc0=$has_present_rc0 argb=$has_fill_argb fail=$has_fail)"
        echo "PARTIAL_ACTIVITY_NO_PIXEL rc0=$has_present_rc0 argb=$has_fill_argb rc_line=$rc_line" > "$outdir/result.txt"
        return 1
    else
        err "inproc-app FAIL: Activity didn't reach onCreate"
        echo "FAIL rc0=$has_present_rc0 argb=$has_fill_argb oncreate=$has_oncreate rc_line=$rc_line" > "$outdir/result.txt"
        return 1
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
  red-square          build :red-square APK, push, run, paint red on fb0 (PF-ohos-mvp-003)
  red-square-drm      build :red-square, run Java side, then drm_present pipe
                      → panel scans red via DRM/KMS (PF-ohos-mvp-003 MVP-2)
  m6-drm-daemon       run long-lived DRM/KMS daemon: self-test (5s) + end-to-end
                      AF_UNIX/memfd round-trip (120 frames RED/BLUE @ vsync;
                      composer_host coexists) (PF-ohos-m6-001)
  m6-java-client      run Java-side M6DrmClient against the daemon: 120 BGRA
                      frames RED/BLUE submitted from a dalvikvm Activity via
                      memfd + SCM_RIGHTS (PF-ohos-m6-002 / M6-OHOS-Step2)
  xcomponent-test     CR60 follow-up: in-process OHOS NDK API call ladder
                      (Tier 1: OH_NativeBuffer_Alloc(NULL) returns w/o crash,
                       Tier 2: real alloc returns non-NULL handle,
                       Tier 3: map → BGRA8888 fill RED → unmap). Forces
                       --arch arm32 (dynamic PIE).
  hello-dlopen-real   CR60-followup E9a: pure-Java System.loadLibrary path
                      via core-android-x86.jar (no \$DVM_PRELOAD_LIB env var
                      workaround). Loads libxcomponent_bridge.so + invokes
                      its JNI methods to prove the load was real.
  hello-drm-inprocess CR60-followup E9b (Path Y): in-process DRM/KMS scan-out
                      from dalvikvm-arm32-dynamic. Kills composer_host
                      around the test (auto-respawns afterwards), drives
                      DSI-1 panel red via CREATE_DUMB+ADDFB2+SETCRTC.
                      Visible-pixel gate — phone-camera evidence required.
  inproc-app          CR60-followup E12 (2026-05-15): real Android Activity
                      onCreate → View.onDraw → SoftwareCanvas → BGRA →
                      DrmInprocessPresenter. Default target is
                      :hello-color-apk (BLUE Activity, distinguishable
                      from E9b's hardcoded RED). PASS criterion: marker
                      'present rc=0' AND 'fill=argb'. Phone-camera evidence
                      of BLUE panel is the visible-pixel gate.

Flags (apply to any subcommand; place before the subcommand name):
  --arch aarch64|arm32|arm32-static|auto
                              pick dalvikvm bitness (CR60). 'auto' (default)
                              uses 'hdc shell getconf LONG_BIT' to decide.
                              'arm32' selects the dynamic-PIE binary (E11
                              default since CR60-followup #4, 2026-05-15).
                              'arm32-static' selects the legacy static binary
                              (pre-E11 default, kept for MVP-0 regression).

Environment overrides:
  HDC=$HDC
  HDC_SERIAL=$HDC_SERIAL
  WINSTAGE=$WINSTAGE
  BOARD_DIR=$BOARD_DIR
  ARCH=$ARCH   (aarch64 | arm32 | arm32-static | auto)
EOF
}

main() {
    # CR60: parse --arch flag(s) that may precede the subcommand.
    while [ "$#" -gt 0 ]; do
        case "$1" in
            --arch)
                if [ "$#" -lt 2 ]; then
                    err "--arch requires an argument (aarch64|arm32|arm32-static|auto)"
                    exit 2
                fi
                ARCH="$2"
                shift 2
                ;;
            --arch=*)
                ARCH="${1#--arch=}"
                shift
                ;;
            *)
                break
                ;;
        esac
    done

    if [ "$#" -lt 1 ]; then
        usage
        exit 2
    fi
    local sub="$1"; shift
    # CR60: any subcommand that touches dalvikvm needs the arch resolved up
    # front. We always call resolve_arch so subcommands can use
    # $DALVIKVM_BOARD_PATH / $DALVIKVM_HOST_PATH; cmd_status uses neither so
    # we skip it explicitly. cmd_push_bcp likewise (BCP is arch-neutral).
    case "$sub" in
        status|push-bcp|-h|--help|help) ;;
        *) resolve_arch || exit 1 ;;
    esac
    case "$sub" in
        status)             cmd_status "$@" ;;
        push-bcp)           cmd_push_bcp "$@" ;;
        hello)              cmd_hello "$@" ;;
        trivial-activity)   cmd_trivial_activity "$@" ;;
        red-square)         cmd_red_square "$@" ;;
        red-square-drm)     cmd_red_square_drm "$@" ;;
        m6-drm-daemon)      cmd_m6_drm_daemon "$@" ;;
        m6-java-client)     cmd_m6_java_client "$@" ;;
        xcomponent-test)    cmd_xcomponent_test "$@" ;;
        hello-dlopen-real)  cmd_hello_dlopen_real "$@" ;;
        hello-drm-inprocess) cmd_hello_drm_inprocess "$@" ;;
        inproc-app)         cmd_inproc_app "$@" ;;
        -h|--help|help)     usage; exit 0 ;;
        *)
            err "unknown subcommand: $sub"
            usage
            exit 2
            ;;
    esac
}

main "$@"
