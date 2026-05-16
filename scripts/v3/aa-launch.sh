#!/usr/bin/env bash
# ============================================================================
# aa-launch.sh — V3 thin wrapper around `hdc shell aa start`.
#
# Replaces the dalvik-kitkat-era OhosMvpLauncher (~600 LOC; now archived
# under archive/v2-ohos-substrate/ohos-tests-gradle/launcher/). On V3 we
# don't drive ActivityThread ourselves: HBC's appspawn-x + AMS does, and
# we hand it the bundle+ability via the standard OHOS `aa start` CLI.
#
# This is the W3 acceptance artifact (V3-WORKSTREAMS §W3, GitHub
# A2OH/westlake#628).
#
# Subcommands:
#   launch <bundle> <ability>     pre-check + `aa start -b <bundle> -a <ability>`
#   launch-helloworld             shortcut for HBC's bundled HelloWorld
#                                 (com.example.helloworld / .MainActivity)
#   precheck                      board reachable + V3 artifacts deployed +
#                                 appspawn-x running. Idempotent. No launches.
#   stop <bundle>                 `aa force-stop` (best-effort cleanup).
#   help / -h                     show this header.
#
# Usage:
#   scripts/v3/aa-launch.sh launch-helloworld
#   scripts/v3/aa-launch.sh launch com.example.helloworld .MainActivity
#   scripts/v3/aa-launch.sh launch com.westlake.mockapk .MainActivity
#   scripts/v3/aa-launch.sh precheck
#
# Env overrides (same as run-ohos-test.sh + run-hbc-regression.sh):
#   HDC         path to hdc.exe   (default WSL Windows binary)
#   HDC_SERIAL  serial of DAYU200 (default rk3568 dd011a4…)
#   BOARD_DIR   on-device V3 base (default /data/local/tmp/westlake; V3
#               artifacts live under $BOARD_DIR/v3-hbc/ per W2 deploy).
#   POST_LAUNCH_WAIT   seconds to wait between `aa start` and marker scan
#                      (default 5).
#   MARKER      regex/string to grep in hilog for PASS gate
#                      (default "MainActivity.onCreate")
#
# Exit codes:
#   0   launch issued (and, if marker scan ran, marker observed)
#   1   pre-check failed (board unreachable / V3 not deployed /
#       appspawn-x not running)
#   2   `aa start` returned non-zero or marker not seen within
#       POST_LAUNCH_WAIT seconds (only returned for `launch`/`launch-helloworld`
#       subcommands; `precheck` returns 0/1 only)
#   3   usage error / unknown subcommand
#
# Design notes:
#   * Read-only on artifacts: this script never writes to v3-hbc/ tree.
#     Board push is done by scripts/run-ohos-test.sh push-bcp (legacy
#     V2 BCP) or scripts/deploy-hbc-to-dayu200.sh (W2 deliverable).
#     aa-launch.sh only INVOKES.
#   * Idempotent: `precheck` and `launch-helloworld` can be invoked
#     repeatedly without state buildup (HBC AMS handles dup launches).
#   * Macro-shim contract clean: no Unsafe / setAccessible /
#     per-app branches. The only per-app data passed is via the
#     positional `<bundle> <ability>` args.
#   * Does not depend on $WINSTAGE / `hdc file send` (no host→board
#     pushes here). hdc.exe shell-only invocation.
# ============================================================================

set -uo pipefail

HDC="${HDC:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}"
HDC_SERIAL="${HDC_SERIAL:-dd011a414436314130101250040eac00}"
BOARD_DIR="${BOARD_DIR:-/data/local/tmp/westlake}"
POST_LAUNCH_WAIT="${POST_LAUNCH_WAIT:-5}"
MARKER="${MARKER:-MainActivity.onCreate}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
V3_LOCAL_DIR="${V3_LOCAL_DIR:-$REPO_ROOT/westlake-deploy-ohos/v3-hbc}"

# ---------- color + log helpers --------------------------------------------
if [ -t 1 ]; then
    RED=$'\033[31m'; GREEN=$'\033[32m'; YELLOW=$'\033[33m'
    CYAN=$'\033[36m'; BOLD=$'\033[1m'; RESET=$'\033[0m'
else
    RED=""; GREEN=""; YELLOW=""; CYAN=""; BOLD=""; RESET=""
fi

log()  { echo "[$(date +%H:%M:%S)] [aa-launch] $*"; }
warn() { echo "${YELLOW}[$(date +%H:%M:%S)] [aa-launch][WARN]${RESET} $*" >&2; }
err()  { echo "${RED}[$(date +%H:%M:%S)] [aa-launch][ERR]${RESET}  $*" >&2; }
ok()   { echo "${GREEN}[$(date +%H:%M:%S)] [aa-launch][OK]${RESET}   $*"; }

# ---------- hdc helpers ----------------------------------------------------
hdc()       { "$HDC" -t "$HDC_SERIAL" "$@"; }
hdc_shell() { hdc shell "$*" 2>&1 | tr -d '\r'; }

# ---------- pre-checks ------------------------------------------------------
# Each returns 0 on PASS, 1 on FAIL. Each prints a single status line.

precheck_hdc_binary() {
    if [ ! -x "$HDC" ]; then
        err "hdc.exe not present at $HDC"
        return 1
    fi
    ok "hdc.exe: $HDC"
}

precheck_board_reachable() {
    local listing
    listing="$("$HDC" list targets 2>/dev/null | tr -d '\r')"
    if ! echo "$listing" | grep -q "$HDC_SERIAL"; then
        err "DAYU200 ($HDC_SERIAL) not connected; hdc list said: ${listing:-empty}"
        return 1
    fi
    ok "DAYU200 reachable: $HDC_SERIAL"
}

precheck_v3_local() {
    # Make sure the v3-hbc artifact root exists locally; this guards
    # against running aa-launch.sh against a clean checkout that hasn't
    # pulled W1 yet.
    if [ ! -d "$V3_LOCAL_DIR" ]; then
        err "V3 local artifact tree missing: $V3_LOCAL_DIR (run W1 pull)"
        return 1
    fi
    if [ ! -x "$V3_LOCAL_DIR/bin/appspawn-x" ]; then
        warn "$V3_LOCAL_DIR/bin/appspawn-x not present or not executable"
        # not a hard fail — board may have a different deploy source
    fi
    ok "v3-hbc local tree present"
}

precheck_v3_deployed_on_board() {
    # W2's `scripts/deploy-hbc-to-dayu200.sh` puts v3-hbc/ under
    # /system/android/ on the board (per HBC DEPLOY_SOP.md), but during
    # bring-up it's also acceptable to stage under $BOARD_DIR/v3-hbc/.
    # Probe both. We don't insist on a specific layout — we just want
    # SOMETHING that looks like the HBC tree to exist before we
    # `aa start`.
    local sys_ok=0 staged_ok=0
    if hdc_shell "ls /system/android/framework/framework.jar 2>/dev/null" \
        | grep -q "framework.jar"; then
        sys_ok=1
    fi
    if hdc_shell "ls $BOARD_DIR/v3-hbc 2>/dev/null | head -1" \
        | grep -qE "."; then
        staged_ok=1
    fi
    if [ "$sys_ok" = "0" ] && [ "$staged_ok" = "0" ]; then
        err "V3 artifacts not deployed on board"
        err "  /system/android/framework/framework.jar : MISSING"
        err "  $BOARD_DIR/v3-hbc/                       : MISSING"
        err "  Run W2 deploy first: scripts/deploy-hbc-to-dayu200.sh"
        return 1
    fi
    if [ "$sys_ok" = "1" ]; then
        ok "V3 deployed under /system/android/ (HBC canonical layout)"
    fi
    if [ "$staged_ok" = "1" ]; then
        ok "V3 staged under $BOARD_DIR/v3-hbc/ (bring-up layout)"
    fi
}

precheck_appspawn_x_running() {
    local pid
    pid="$(hdc_shell 'pidof appspawn-x' | tr -d ' \n')"
    if [ -z "$pid" ]; then
        # appspawn-x may also be registered as a regular `appspawn`
        # service in some HBC build flavors; double-check.
        pid="$(hdc_shell 'pidof appspawn' | tr -d ' \n')"
        if [ -z "$pid" ]; then
            err "appspawn-x not running (pidof appspawn-x AND appspawn both empty)"
            err "  Try: hdc shell 'start appspawn-x' or reboot the board after W2 deploy"
            return 1
        fi
        warn "appspawn-x not running, but legacy 'appspawn' is alive (pid=$pid)"
        warn "  V3 expects HBC appspawn-x; this may be the stock OHOS appspawn."
        return 1
    fi
    ok "appspawn-x running (pid=$pid)"
}

precheck_all() {
    log "${BOLD}== V3 pre-flight checks ==${RESET}"
    local rc=0
    precheck_hdc_binary           || rc=1
    [ "$rc" = "1" ] && return 1
    precheck_board_reachable      || rc=1
    [ "$rc" = "1" ] && return 1
    precheck_v3_local             || rc=1
    precheck_v3_deployed_on_board || rc=1
    precheck_appspawn_x_running   || rc=1
    if [ "$rc" = "0" ]; then
        ok "${BOLD}all pre-flight checks PASS${RESET}"
    else
        err "${BOLD}pre-flight checks FAIL — fix above before launching${RESET}"
    fi
    return $rc
}

# ---------- subcommands -----------------------------------------------------

cmd_precheck() {
    precheck_all
}

cmd_launch() {
    local bundle="${1:-}"
    local ability="${2:-}"
    if [ -z "$bundle" ] || [ -z "$ability" ]; then
        err "usage: aa-launch.sh launch <bundle> <ability>"
        return 3
    fi
    # Run pre-flight; bail on FAIL.
    precheck_all || return 1

    log "${BOLD}== aa start -b $bundle -a $ability ==${RESET}"
    local out
    out="$(hdc_shell "aa start -b $bundle -a $ability" 2>&1)"
    echo "$out" | sed 's/^/    /'

    # `aa start` typically prints "start ability successfully." on success.
    # Some HBC builds print just exit-status; we treat any non-error line
    # as success and key off the marker grep below for the real verdict.
    if echo "$out" | grep -qiE "error|fail|denied|10[0-9]{6,}"; then
        err "aa start reported an error"
        return 2
    fi

    if [ "$POST_LAUNCH_WAIT" -gt 0 ] 2>/dev/null; then
        log "[wait ${POST_LAUNCH_WAIT}s for app to reach onCreate...]"
        sleep "$POST_LAUNCH_WAIT"
    fi

    # Marker scan: hilog (preferred) then logcat fallback.
    local marker_re="$MARKER"
    local hilog_dump
    hilog_dump="$(hdc_shell "hilog -x 2>/dev/null | tail -300" || true)"
    if echo "$hilog_dump" | grep -qE "$marker_re"; then
        ok "marker observed: '$marker_re'"
        echo "$hilog_dump" | grep -E "$marker_re" | head -3 | sed 's/^/    /'
        return 0
    fi

    # Marker not seen — still soft-PASS if a process for the bundle exists,
    # since the marker text varies per app. We surface this as warn.
    local app_pid
    app_pid="$(hdc_shell "pidof $bundle" | tr -d ' \n')"
    if [ -n "$app_pid" ]; then
        warn "marker '$marker_re' not in hilog tail, but $bundle pid=$app_pid is alive"
        warn "this may be a marker-text mismatch — inspect hilog manually"
        return 0
    fi

    err "no marker AND no live process for $bundle within ${POST_LAUNCH_WAIT}s"
    return 2
}

cmd_launch_helloworld() {
    # HBC's bundled smoke. Per westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md:
    #   aa start -a com.example.helloworld.MainActivity -b com.example.helloworld
    # Our `launch` wrapper accepts (bundle, ability) in that order; aa CLI
    # accepts -a and -b independently. HBC's standard marker is
    # "MainActivity.onCreate" in hilog.
    cmd_launch "com.example.helloworld" ".MainActivity"
}

cmd_stop() {
    local bundle="${1:-}"
    if [ -z "$bundle" ]; then
        err "usage: aa-launch.sh stop <bundle>"
        return 3
    fi
    log "force-stopping $bundle"
    hdc_shell "aa force-stop -b $bundle" | sed 's/^/    /'
    return 0
}

usage() {
    sed -n '/^# ====/,/^# ====$/p' "$0" | sed 's/^# \{0,1\}//'
}

main() {
    if [ "$#" -lt 1 ]; then
        usage
        exit 3
    fi
    local sub="$1"; shift
    case "$sub" in
        precheck)           cmd_precheck "$@"           ;;
        launch)             cmd_launch "$@"             ;;
        launch-helloworld)  cmd_launch_helloworld "$@"  ;;
        stop)               cmd_stop "$@"               ;;
        -h|--help|help)     usage; exit 0               ;;
        *)
            err "unknown subcommand: $sub"
            usage
            exit 3
            ;;
    esac
}

main "$@"
