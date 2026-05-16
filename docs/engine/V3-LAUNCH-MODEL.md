# V3 Launch Model — `aa start` → AMS → appspawn-x → ActivityThread

**Date:** 2026-05-16
**Author:** agent 50 (W3 deliverable, V3-WORKSTREAMS §W3, A2OH/westlake#628)
**Status:** AUTHORITATIVE for V3 OHOS path
**Companions:** `V3-ARCHITECTURE.md` (§4 row 13), `V3-WORKSTREAMS.md` (§W3),
                `CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md` (§8 — HBC's
                MainActivity.onCreate milestone), `westlake-deploy-ohos/
                v3-hbc/scripts/DEPLOY_SOP.md` (HBC's source-of-truth)

---

## 1. Why this doc exists

Westlake's V2-OHOS substrate launched Android Activities by hand-driving
`ActivityThread.main()` from a Java entrypoint named `OhosMvpLauncher`
running under `dalvikvm-arm32-dyn`. The launcher was ~600 LOC and
shadowed AOSP framework methods (Looper, Instrumentation,
ClientTransaction) inside its own process.

V3 deletes that path. We piggyback on HBC's port of AOSP's normal
launch model: `hdc shell aa start -b <bundle> -a <ability>` enqueues an
intent against OHOS's Ability Manager Service (AMS), which forks
HBC's `appspawn-x` child, which boots ART + framework.jar and calls
`ActivityThread.main()` itself. Westlake's job shrinks to "hand AMS
the right bundle/ability strings" — exactly what the `aa` CLI is for.

This doc documents the dataflow, the wrapper script that automates it
(`scripts/v3/aa-launch.sh`), and the migration from `OhosMvpLauncher`.

## 2. End-to-end dataflow

```
host (WSL)                                    DAYU200 (rk3568, OHOS 7.0.0.18)

scripts/v3/aa-launch.sh launch-helloworld
   │
   │ pre-flight checks (read-only)
   │   - hdc.exe present
   │   - HDC_SERIAL reachable
   │   - $V3_LOCAL_DIR/bin/appspawn-x exists
   │   - /system/android/framework/framework.jar OR
   │     $BOARD_DIR/v3-hbc/ present on device
   │   - pidof appspawn-x non-empty
   │
   │ hdc shell aa start -b com.example.helloworld -a .MainActivity
   ▼
                                              aa CLI (OHOS bm/aa tools)
                                                 │
                                                 ▼
                                              AbilityManagerService (AMS)
                                                 │  resolve bundle → app spawn request
                                                 │  via AMS_appspawn IPC
                                                 ▼
                                              appspawn-x  (HBC's modified appspawn,
                                                            patched per CR-EE §6)
                                                 │  fork()
                                                 ▼
                                              child process
                                                 │  dlopen libart.so, libhwui.so,
                                                 │      libandroid_runtime.so, ...
                                                 │  load BCP from /system/android/
                                                 │      framework/{boot,core-oj,
                                                 │      framework}.jar
                                                 │  JNI_CreateJavaVM
                                                 ▼
                                              ActivityThread.main()
                                                 │  bindApplication(...)
                                                 │  handleLaunchActivity(...)
                                                 ▼
                                              MainActivity.onCreate()
                                                 │  hilog line: "MainActivity.onCreate"
                                                 ▼
                                              View tree inflated, hwui RenderThread
                                                 │  EGL surface from RSSurfaceNode
                                                 ▼
                                              pixels on DSI-1 panel
   │
   │ (back on host) post-launch wait, then hilog -x scan for $MARKER
   │ (default "MainActivity.onCreate")
   ▼
PASS / FAIL verdict + exit code
```

## 3. What replaces what

| V2 substrate (archived 2026-05-16) | V3 (current) |
|---|---|
| `ohos-tests-gradle/launcher/.../OhosMvpLauncher.java` (~600 LOC) | `scripts/v3/aa-launch.sh` (~250 LOC shell) |
| `OhosMvpLauncher.main(<package>/.MainActivity)` invoked by `dalvikvm-arm32-dyn` with `-Xbootclasspath:...:OhosMvpLauncher.dex` | `aa start -b <bundle> -a <ability>` invoked by `hdc shell` |
| Java-side `Looper.prepareMainLooper()` + `ActivityThread`-equivalent shim (handleLaunchActivity inline) | HBC's real `ActivityThread.main()` running inside appspawn-x child |
| Per-app constants table read by `OhosMvpLauncher` to pick MainActivity class | OHOS bundle/ability resolution via AMS — no per-app code in the launcher |
| Boot classpath: `core-android-x86.jar:direct-print-stream.jar:aosp-shim-ohos.dex:<App>.dex:OhosMvpLauncher.dex` | Boot classpath: `framework.jar:core-oj.jar:core-libart.jar:core-icu4j.jar:...` (HBC's stock 11-jar BCP under `/system/android/framework/`) |
| `dalvikvm-arm32-dyn` (Westlake-built static-PIE) | `appspawn-x` child (HBC-built, dynamic) |
| In-process render: `SoftwareCanvas` + `DrmInprocessPresenter` | Real `libhwui.so` RenderThread + EGL eglSwapBuffers |
| Visible-pixel proof: DRM/KMS scan-out from dalvikvm process (kills composer_host) | Real `RSSurfaceNode` (composer_host coexists) |
| Single Activity per dalvikvm invocation | Multiple AMS-managed Activities; appspawn-x fork-per-app |

## 4. The wrapper script: `scripts/v3/aa-launch.sh`

Idempotent, read-only on artifacts, takes either `(bundle, ability)`
explicit args or the `launch-helloworld` shortcut. Pre-flight gates:

- `hdc.exe` present at `$HDC`
- `$HDC list targets` includes `$HDC_SERIAL`
- `$V3_LOCAL_DIR` tree exists (so we know W1 ran)
- Either `/system/android/framework/framework.jar` exists (canonical
  HBC layout from `DEPLOY_SOP.md`) or `$BOARD_DIR/v3-hbc/` is staged
  (bring-up convenience layout from W2)
- `pidof appspawn-x` returns non-empty (the V3 child-spawn daemon
  is alive; legacy stock-OHOS `appspawn` is flagged as a warning)

Post-launch grade:

- `aa start` output scanned for `error|fail|denied|10[0-9]{6,}` (exit
  2 on hit)
- `hilog -x | tail -300` scanned for `$MARKER` (default
  `MainActivity.onCreate`)
- Fallback: `pidof <bundle>` non-empty within `$POST_LAUNCH_WAIT`s

See the script header for exit codes and env knobs.

## 5. The wrapper script is wrapped (run-ohos-test.sh)

For users with muscle memory of `scripts/run-ohos-test.sh`, two
forwarding subcommands exist:

- `scripts/run-ohos-test.sh v3-launch`
- `scripts/run-ohos-test.sh launch-helloworld`

Both immediately `exec` `scripts/v3/aa-launch.sh launch-helloworld`.
No state. No dalvikvm.

## 6. Deprecated `run-ohos-test.sh` subcommands

W3 hard-errors the following V2 subcommands with a redirect message:

| Subcommand | V2 role | Why deprecated |
|---|---|---|
| `hello` | Compile :hello → HelloOhos.dex → run on dalvikvm; print "hello" | `ohos-tests-gradle/hello/` archived; superseded by HBC HelloWorld.apk |
| `trivial-activity` | MVP-1 Android Activity.onCreate marker via OhosMvpLauncher | `ohos-tests-gradle/trivial-activity/` + OhosMvpLauncher archived; superseded by V3 path with HBC HelloWorld (and W5 mock APK) |
| `red-square` | MVP-2 SoftwareCanvas → /dev/fb0 BGRA blit | `ohos-tests-gradle/red-square/` archived; superseded by real hwui (no SoftwareCanvas in V3) |
| `red-square-drm` | MVP-2 DRM/KMS scan-out (kills composer_host) | Same as red-square; HBC coexists with composer_host via RSSurfaceNode |
| `m6-drm-daemon` | M6 long-lived DRM/KMS daemon, AF_UNIX/memfd round-trip | `dalvik-port/compat/m6-drm-daemon/` archived; superseded by HBC's `OHGraphicBufferProducer` |
| `m6-java-client` | M6-OHOS-Step2 Java-side BGRA client | `ohos-tests-gradle/m6-test/` archived; same replacement |
| `xcomponent-test` | CR60 in-process OHOS NDK API ladder | `dalvik-port/compat/xcomponent_bridge.c` archived; HBC's adapter handles native bridging |
| `hello-dlopen-real` | CR60-followup E9a: pure-Java System.loadLibrary path | Same replacement; HBC adapter dlopens via its own classloader |
| `hello-drm-inprocess` | CR60-followup E9b: in-process DRM/KMS from dalvikvm | Same as red-square-drm; V3 NEVER scans out in-process |
| `inproc-app` | CR60-followup E12/E13: real Android Activity via SoftwareCanvas + DrmInprocessPresenter | All three layers archived; V3 launches each app as its own appspawn-x child, NEVER in-process with the launcher |

The deprecation handler points each subcommand at its V3 replacement.
Exit code is `2` (usage error) so wrapping CI / regression scripts
treat it as a hard fail rather than a SKIP. Set
`RUN_OHOS_ALLOW_V2_ARCHIVE=1` to also surface the manual archive-restore
recipe (W13 preserved git history, so `git mv archive/v2-ohos-substrate/
ohos-tests-gradle ohos-tests-gradle` restores it cleanly).

## 7. Acceptance criteria (W3, from V3-WORKSTREAMS.md)

| Criterion | Status (2026-05-16) | Note |
|---|---|---|
| `OhosMvpLauncher` source files moved to `archive/v2-ohos-substrate/launchers/` | DONE (by W13) | Actually at `archive/v2-ohos-substrate/ohos-tests-gradle/launcher/` since the whole gradle root archived together |
| All call sites that depended on `OhosMvpLauncher` either removed (OHOS path) or marked Android-phone-only (V2 path) | DONE | `scripts/run-ohos-test.sh` deprecates 10 V2 subcommands with redirect to aa-launch.sh |
| HBC's launch model documented in `docs/engine/V3-LAUNCH-MODEL.md` | DONE | this doc |
| Westlake's launcher driver wrapper reduced to a thin shell | DONE | `scripts/v3/aa-launch.sh` ~250 LOC shell, ~60% comments |
| HelloWorld.apk launches via this path on DAYU200, reproducing W2 result | PENDING W2 | aa-launch.sh ready; needs `scripts/deploy-hbc-to-dayu200.sh` (W2 deliverable) to push v3-hbc/ to the board first |
| Mock APK from W5 launches via this path | PENDING W5 | forward-link; W3 acceptance can be partial |
| Macro-shim contract self-audit clean: no Unsafe / setAccessible / per-app branches in any new code | PASS | aa-launch.sh is shell; no Java; no per-app branches (the only per-app data passed is positional `<bundle> <ability>` args) |

## 8. Self-audit gate (V3-WORKSTREAMS §W3)

```bash
# No Westlake-owned launcher code on OHOS target path
find android-to-openharmony-migration -name '*.java' -o -name '*.kt' -o -name '*.cpp' \
  | xargs grep -l "OhosMvpLauncher\|InProcessAppLauncher.*ohos" \
  | grep -v "archive/" | grep -v "docs/" \
  && echo "FAIL: live references to OhosMvpLauncher" || echo "OK"
```

As of 2026-05-16 (W3 landing), this returns OK: every remaining
reference to `OhosMvpLauncher` is either under `archive/` (W13's move)
or in `docs/engine/V3-LAUNCH-MODEL.md` / `V3-W11-CARRYFORWARD-AUDIT.md`
(documentation of the deprecation). `scripts/run-ohos-test.sh` still
*names* the symbol in deprecated-subcommand bodies that we keep alive
for the `RUN_OHOS_ALLOW_V2_ARCHIVE=1` forensic mode — those are
unreachable on the default code path.

## 9. Cross-references

- `V3-WORKSTREAMS.md` §W3 — acceptance criteria for this workstream
- `V3-WORKSTREAMS.md` §W2 — board deploy prerequisite (in flight as of 2026-05-16)
- `V3-WORKSTREAMS.md` §W5 — mock APK validation, downstream of W3
- `V3-W11-CARRYFORWARD-AUDIT.md` — confirms `scripts/run-ohos-test.sh`
  refactor over archive
- `V3-W12-CR61.1-CODE-DISPOSITION.md` — binder strategy that informs
  this launch model (OHOS libipc via HBC, not Westlake-built binder)
- `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md` — HBC's
  source-of-truth for the `aa start` invocation we wrap
- `CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md` §8 — HBC's
  MainActivity.onCreate milestone we expect to reproduce
- `archive/v2-ohos-substrate/ohos-tests-gradle/launcher/.../OhosMvpLauncher.java`
  — the archived 600-LOC launcher this doc replaces
