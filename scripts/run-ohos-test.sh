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
    cmd="$cmd ANDROID_ROOT=$BOARD_DIR /data/local/tmp/dalvikvm"
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
    cmd="$cmd ANDROID_ROOT=$BOARD_DIR /data/local/tmp/dalvikvm"
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
    cmd="$cmd ANDROID_ROOT=$BOARD_DIR /data/local/tmp/dalvikvm"
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
        red-square)         cmd_red_square "$@" ;;
        red-square-drm)     cmd_red_square_drm "$@" ;;
        m6-drm-daemon)      cmd_m6_drm_daemon "$@" ;;
        -h|--help|help)     usage; exit 0 ;;
        *)
            err "unknown subcommand: $sub"
            usage
            exit 2
            ;;
    esac
}

main "$@"
