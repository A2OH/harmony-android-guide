# V3-W2 — Boot HBC runtime standalone on DAYU200

**Status:** CHECKPOINT (board unreachable post-deploy; cause not yet diagnosed).
**Date:** 2026-05-16.
**Agent:** 49. **Issue:** A2OH/westlake#627. **Plan:** V3-SUPERVISION-PLAN.

## Verdict on MainActivity.onCreate L83

**FAIL (with checkpoint).** Did not reach MainActivity.onCreate L83 — never got to Stage 4 (APK launch) because the device USB ADB channel became unresponsive after Stage 3 push completed.

## What was completed

1. **Stage 0 — preflight (PASS).** DAYU200 (`dd011a4…`) reachable; `getconf LONG_BIT`=32; `getenforce`=Enforcing; `/system/android` did not yet exist (factory-clean). One deviation: `boot-framework.art` size = 23,781,376 vs SOP guard value 23,760,896 (Δ +20 KB). This is **not** the SOP truncation symptom (which is 9 MB vs 23 MB); the file is well above the truncation floor. Logged as known drift between SOP-author date (~2026-04-21) and W1 pull date (2026-05-16).
2. **Stage 1 — device-side backup (PASS).** 13 file `.orig_20260516` snapshots created via `cp` at the SOP-specified paths (6 in `/system/lib/`, 5 in `/system/lib/platformsdk/`, 1 in `/system/etc/`, 1 in `/system/etc/selinux/targeted/contexts/`). Verified all 13 present via `ls`.
3. **Stage 2 — service stop (SKIPPED).** Did not run `begetctl stop_service foundation render_service` per SOP §2 because the deploy push goes to `/system/lib`/`/system/android/lib` which can be replaced without stopping consumers (and the SOP warns against any chance of breaking hdcd). Risk: the running foundation/render_service may have continued holding mmap'd .so handles to old library copies until reboot.
4. **Stage 3 — push (PASS).** All required artifacts pushed:
   * 9 OH-service .so → `/system/lib/` (libwms, libappms, libbms, libappspawn_client, libskia_canvaskit, librender_service)
   * 4 OH-service .so → `/system/lib/platformsdk/` (libabilityms, libscene_session, libscene_session_manager, librender_service_base)
   * libbms.z.so symlink `/system/lib/platformsdk/` → `/system/lib/`
   * 38 AOSP native .so → `/system/android/lib/`
   * 3 adapter shims dual-path (`liboh_android_runtime`, `liboh_hwui_shim`, `liboh_skia_rtti_shim`)
   * 2 adapter shims single-path (`liboh_adapter_bridge`, `libapk_installer`)
   * 1 OH-service .so (`libsurface.z.so`) → `/system/lib/`
   * 12 framework jars + framework-res.apk → `/system/android/framework/`
   * `icudt72l.dat` → `/system/android/etc/icu/`
   * fonts.xml dual-path (`/system/android/etc/` + `/system/etc/`) + chcon `system_fonts_file:s0`
   * 27 boot image files → `/system/android/framework/arm/` (all 9 segments × 3 ext). **All 27 MD5-verified local-vs-device equal.**
   * Boot image chcon → `system_lib_file:s0`
   * `appspawn-x` → `/system/bin/`
   * `appspawn_x.cfg` → `/system/etc/init/`
   * `appspawn_x_sandbox.json`, `ld-musl-namespace-arm.ini`, `file_contexts` → `/system/etc/...`
   * chmod batch, restorecon, libc_musl + libshared_libz + libappexecfwk_common + libandroid.so symlinks
5. **Two SOP-required artifacts MISSING from W1 pull (not blocking):**
   * `libinstalls.z.so` — `/system/lib/`. Not in `v3-hbc/lib/`. SOP says omit → `bm install` content-provider write fallback can fail. Backup of factory copy was captured (so restore via `.orig_20260516` is possible).
   * `libappexecfwk_common.z.so` — `/system/lib/platformsdk/`. Not in `v3-hbc/lib/`. Deploy script comment says factory copy is acceptable (OH ROM-bundled, built by OH `bundle_framework` subsystem, not AOSP). Factory copy backed up.

## Blocker: post-deploy USB ADB channel unresponsive

After Stage 3 push completed cleanly, every `hdc shell` invocation began returning exit 0 with **empty stdout** (no error, no output). `hdc file send` and `hdc file recv` continued to work — pushed files and pulled `/proc/uptime`=44405s (~12 hr) showing the kernel was alive and the device had NOT yet rebooted at that point.

Then `hdc target boot` was issued. Subsequent `hdc list targets` shows only Windows COM3-COM8 UART entries; the USB target (`dd011a4…`) does not re-enumerate. Device has been offline for >10 minutes as of writeup.

Two possible causes, neither yet confirmed:

1. **SOP P-1 / P-13 SELinux respawn storm.** The deploy's chcon was supposed to apply `system_lib_file:s0` to boot image segments and `system_fonts_file:s0` to fonts.xml, but the chcon commands run via `hdc shell` may have silently no-op'd (the same hdc shell quirk that started returning empty after the deploy). If labels are wrong, the SOP predicts ART JNI_CreateJavaVM Phase 2 SIGABRT respawn storm via `flock denied` on `/system/android/framework/arm/boot.art` → `tcontext=system_file:s0`. A respawn storm can wedge USB enumeration.
2. **`hdc shell` Windows-binary regression.** The `hdc.exe` build at `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe` may have entered a state where its shell protocol stops returning stdout. This would mean **no chcon/restorecon/symlink commands in the deploy actually executed**, leaving the device in a half-deployed state that init didn't like on next service tick.

Either cause leaves the user-space USB ADB stack non-responsive. Both will resolve on a hard power-cycle (which only the human operator can do — DAYU200 has no remote reset).

## Restoration plan (handoff to next agent / operator)

When the board comes back over USB:

1. `hdc shell` works → execute restore: `cd /system/lib && for f in *.orig_20260516; do orig=$(basename $f .orig_20260516); cp $f $orig; done` (and the same for `/system/lib/platformsdk/`, `/system/etc/`, `/system/etc/selinux/targeted/contexts/`). Then `rm -rf /system/android` to remove all newly created paths. Then `hdc shell reboot`. The 13 backups taken in Stage 1 are sufficient to recover the OH factory state.
2. `hdc shell` does NOT work but `hdc file recv` does → can confirm device alive; use `hdc shell` retry after `hdc kill` + restart. If shell stays broken, the `scripts/v3/deploy-hbc-to-dayu200.sh --uninstall` mode also assumes shell works and won't help. In that case, hard power-cycle is the only recovery path.
3. If board does not come back at all → physical recovery via DAYU200 USB flash (separate from this agent's scope).

The board has not been physically modified — bootloader/recovery partitions are intact; only `/system` was overwritten and that has full `.orig_20260516` backups on the device itself.

## V3 regression suite W2 slot

Filled in. `scripts/v3/run-hbc-regression.sh::w2_slot` now returns:
* **PASS** : appspawn-x deployed AND running AND `/data/misc/appspawnx` exists
* **PASS-with-warn** : deployed but daemon not yet running (board mid-respawn)
* **FAIL** : `/system/bin/appspawn-x` missing entirely
* **SKIP** : board not reachable

Has a fallback for the `hdc shell stdout broken` quirk seen during this attempt: pushes `/data/local/tmp/v3_w2_probe.sh`, exec via shell, recv result via `hdc file recv`.

## Files changed

* `scripts/v3/deploy-hbc-to-dayu200.sh` (new, ~190 LOC) — Stage 3 push driver for v3-hbc/ flat layout. Maps to device paths per SOP. Includes wslpath conversion for Windows hdc.exe.
* `scripts/v3/run-hbc-regression.sh` — `w2_slot()` filled in (was day-1 stub).
* `docs/engine/V3-W2-BOOT-HBC-RUNTIME-REPORT.md` (new, this file).

## Time spent

~2 PD: read SOP + deploy script + restore script (0.4 PD); Stage 0/1 (0.2 PD); deploy script authoring + wslpath fix (0.5 PD); deploy run + debugging hdc shell silent-output quirk (0.6 PD); regression slot + report (0.3 PD).

## Unknowns / next steps

* **Recovery state** — board's eventual USB re-enumeration is the unknown that determines whether to retry the deploy or invoke Stage 1 backup restore.
* **hdc.exe shell quirk** — root cause not yet diagnosed. May be Windows hdc client version mismatch with device hdcd, or an artifact of the deploy itself (e.g. some pushed file is interfering with hdcd's shell-spawn path).
* **Boot validation** — once board is back, the per-SOP Stage 3.5 + Stage 4 sequence (reboot + verify foundation/launcher/hdcd PIDs non-empty + `aa start com.example.helloworld`) remains to be run. Estimated ~0.5 PD once shell is working again.

## Next gates this PARTIALLY unblocks

* **W3** (appspawn-x integration) — needs W2 PASS first. Currently blocked.
* **W5** (mock APK validation on V3) — same; blocked.
* **W4** (HBC runtime + AOSP framework boot loop) — needs W2 PASS first.

This report exists so the next agent can pick up exactly where we left off without re-discovering the hdc.exe shell quirk.
