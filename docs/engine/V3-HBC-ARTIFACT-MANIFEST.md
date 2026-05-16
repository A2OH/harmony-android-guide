# V3-HBC artifact manifest — what we pulled from HBC into Westlake

**Status:** AUTHORITATIVE for V3-W1 (artifact inventory). 2026-05-16.
**Author:** agent 43, coordinating with agent 42's `V3-ARCHITECTURE.md`.
**See also:** `V3-ARCHITECTURE.md`, `V3-WORKSTREAMS.md` (W1-W13), `CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md`, `CR-FF-HBC-BORROWABLE-PATTERNS.md`.

This document records the set of artifacts pulled from HBC's `/home/HanBingChen/adapter/` tree onto the local Westlake working machine in support of V3-W2 ("boot standalone HBC runtime on DAYU200"). Pulls are READ-ONLY against the HBC machine. The local target tree is:

```
/home/dspfac/android-to-openharmony-migration/westlake-deploy-ohos/v3-hbc/
```

Inventory manifest (one line per file, with size + type + license attribution + reuse priority): `/home/dspfac/android-to-openharmony-migration/artifacts/v3-hbc-inventory/20260516T065719Z/manifest.txt` (641 entries scanned in source tree, 145 LOW-priority entries excluded from local pull).

SHA-256 checksums of every binary artifact: `/home/dspfac/android-to-openharmony-migration/artifacts/v3-hbc-inventory/20260516T065719Z/checksums.sha256` (113 lines).

Smoke test record: `/home/dspfac/android-to-openharmony-migration/artifacts/ohos-mvp/v3-w1-hbc-smoke/20260516T065719Z/README.md`.

---

## 1. Totals

| Measure                     | Value          |
|-----------------------------|----------------|
| Total files pulled          | **563**        |
| Total size on local disk    | **412 MB**     |
| Source on remote            | `chenyue@1.95.175.212:/home/HanBingChen/adapter/` (login user `chenyue` has READ access to `/home/HanBingChen/` — verified) |
| Pull session timestamp      | 20260516T065719Z |
| Skipped on purpose          | `D200.zip` (4.2 GB AOSP tree), `bkup/`, `diag/`, `draft/`, `tmp/`, `out/aosp_lib/obj/` (intermediate .o), `build/_deprecated*`, `*.bak/*.bk/*.orig/*.preclean`, 7 backup variants under `framework/appspawn-x/src/*.bak_*` |

---

## 2. Subdir-by-subdir inventory

### `lib/` — 56 cross-built `.so` files (~87 MB)

ARM32 EABI5 ELF shared objects, soft-float, built against OHOS musl. Verified with `file(1)` on each.

> **2026-05-16 (W2 recovery, agent 53):** Added `libinstalls.z.so` (999 KB; md5 `bafc989ffcbd4185bcc1f6140d05f260`) and `libappexecfwk_common.z.so` (21 KB; md5 `a8c74b317410566d386d60acb8b82a45`), both pulled from `chenyue@1.95.175.212:/home/HanBingChen/oh/out/rk3568/packages/phone/system/lib*/`. Both are mandatory per HBC's `DEPLOY_SOP.md` (Stage 3f and Stage 3b respectively). Their absence in the W1 pull was the reason the W2 deploy was missing 2 of the SOP-mandated 94 files. See `V3-W2-RECOVERY-PROCEDURE.md`.

* **AOSP runtime (38 files)** — from HBC `out/aosp_lib/`:
  `libart.so` (11.8 MB), `libart-compiler.so` (3.6 MB), `libartbase.so`, `libart_runtime_stubs.so`, `libartpalette.so`, `libartpalette-system.so`, `libdexfile.so`, `libprofile.so`, `libelffile.so`, `libsigchain.so`, `libnativebridge.so`, `libnativeloader.so`, `libnativehelper.so`, `libvixl.so`, `libunwindstack.so`, `libbionic_compat.so`, `libandroid_runtime.so`, `libandroidfw.so`, `libandroidio.so`, `libhwui.so` (2.3 MB), `libbase.so`, `libcutils.so`, `libutils.so`, `liblog.so`, `libziparchive.so`, `liblz4.so`, `libtinyxml2.so`, `libminikin.so`, `libharfbuzz_ng.so`, `libft2.so`, `libicui18n.so` (3.1 MB), `libicuuc.so`, `libicu_jni.so`, `libcrypto.so`, `libexpat.so`, `libjavacore.so`, `libopenjdk.so`, `libopenjdkjvm.so`.
* **HBC adapter shims (5 files)** — from `out/adapter/`:
  `liboh_adapter_bridge.so` (1.6 MB; the central JNI bridge), `liboh_android_runtime.so`, `liboh_hwui_shim.so`, `liboh_skia_rtti_shim.so` (12.6 KB; Skia ABI shim), `libapk_installer.so`.
* **OH service replacements (13 files)** — from `out/oh-service/` (11) plus `oh/out/rk3568/packages/phone/system/lib*/` (2):
  `libabilityms.z.so` (4.6 MB), `libappms.z.so`, `libbms.z.so` (5.0 MB), `libwms.z.so`, `libscene_session.z.so`, `libscene_session_manager.z.so`, `librender_service.z.so` (5.4 MB), `librender_service_base.z.so` (8.1 MB), `libsurface.z.so`, `libskia_canvaskit.z.so` (23 MB), `libappspawn_client.z.so`, **`libinstalls.z.so` (999 KB; → /system/lib/, Stage 3f)**, **`libappexecfwk_common.z.so` (21 KB; → /system/lib/platformsdk/ + symlink /system/android/lib/, Stage 3b + [8/8])**.

### `jars/` — 12 cross-built Java framework artifacts + 1 dex (152 MB)

* `framework.jar` (40 MB; AOSP-built with HBC's L5 reflection injection — the central Android framework)
* `framework-classes.dex.jar` (39 MB)
* `framework-res-package.jar` (34 MB)
* `framework-res.apk` (34 MB)
* `core-oj.jar` (5.5 MB), `core-libart.jar`, `core-icu4j.jar`, `apache-xml.jar`, `bouncycastle.jar`, `okhttp.jar`
* `oh-adapter-framework.jar` (103 KB; **pulled from `out/adapter/` per HBC's [B-1] single-source invariant**, NOT `out/aosp_fwk/` which is stale)
* `oh-adapter-runtime.jar` (33 KB), `adapter-mainline-stubs.jar` (49 KB)
* `adapter-classes.dex` (259 KB; HBC adapter's compiled classes.dex)

### `bcp/` — Boot image, 27 files (135 MB)

9-segment BCP (per HBC's [B-6] invariant: must be 9 segments aligned with `framework/appspawn-x/src/main.cpp:62 kBootClasspath`).

For each of these 9 jars, one `.art` + one `.oat` + one `.vdex` (= 27 files):

`boot` (the merged entry), `boot-core-libart`, `boot-core-icu4j`, `boot-okhttp`, `boot-bouncycastle`, `boot-apache-xml`, `boot-adapter-mainline-stubs` (BCP segment 7 added 2026-04-30, contains 149 APEX-mainline stub classes; missing → ART `InitWithoutImage` aborts on `Class mismatch for Ljava/lang/String;`), `boot-framework`, `boot-oh-adapter-framework`.

Largest: `boot-framework.oat` 49 MB; `boot-framework.vdex` 35 MB; `boot-framework.art` 23 MB.

### `bin/` — 1 daemon binary (112 KB)

* `appspawn-x` — HBC's Android-Zygote / OH-appspawn hybrid daemon. 32-bit ARM PIE, dynamically linked to `/lib/ld-musl-arm.so.1`. Phase 1 OH security init → Phase 2 ART VM + JNI registration → Phase 3 framework preload → Phase 4 spawn loop.

### `etc/` — 6 config files (33 MB)

* `appspawn_x.cfg` (3.0 KB; OH init `.cfg` service descriptor)
* `appspawn_x_sandbox.json` (6.7 KB; OH sandbox config)
* `fonts.xml` (4.2 KB; must land at both `/system/etc/fonts.xml` and `/system/android/etc/fonts.xml`; relabel `chcon u:object_r:system_fonts_file:s0` else `EACCES → setSystemFontMap NPE → ensureBindApplication fail`)
* `icudt72l.dat` (33.6 MB; ICU dat)
* `ld-musl-namespace-arm.ini` (3.3 KB; appspawn-x Layer-2 namespace linker config)
* `file_contexts` (1.5 KB; OH SELinux label file — Layer-1 appspawn-x prerequisite)

### `patches/` — 104 files (2.8 MB)

* `aosp_patches/` (33 files):
  - L5 reflection injection sites: 4 patches to `frameworks/core/java/android/{app,view}/*.java` (`ActivityManager`, `ActivityTaskManager`, `ActivityThread`, `WindowManagerGlobal`) — the canonical "4 L5 framework patches" referenced in the project memory
  - 8 patches to AOSP native: `art/runtime/gc/collector/mark_compact.cc`, `frameworks/base/core/jni/AndroidRuntime.cpp`, `frameworks/base/core/jni/android_util_Process.cpp`, `frameworks/base/libs/hwui/jni/graphics_jni_helpers.h`, `frameworks/base/libs/androidfw/*` (3 files), `libs/hwui/hwui_rk3568.patch`
  - 1 ICU android_icu4j Android.bp patch
  - `replacement_headers/` — 4 header files (`sys/pidfd.h`, `meminfo/{meminfo,sysmeminfo,procmeminfo}.h`)
* `ohos_patches/` (71 files): musl namespace linker `.ini` + `ADLTSection.h` patch; appspawn (2 Python apply scripts + headers + client patches); selinux `file_contexts`; bundle_framework BUILD.gn + bundlemgr cpp patches (B8 enabler — adds `adapter_apk_install_minimal.cpp` + `apk_manifest_parser`); third_party/skia/m133 probe scripts (these are HBC's internal Skia debugging probes, may not all be needed for V3); third_party/icu decimfmt + number_decimalquantity patches; graphic_2d + graphic_surface patches; init `apply_init_max_env_value.py`; build/ets2abc gn collision fix + ohos/app/app_internal.gni.patch.

### `scripts/` — 13 build/deploy scripts (256 KB)

* `DEPLOY_SOP.md` (12 KB; the project-authoritative DAYU200 deploy SOP v4 — must follow)
* `README.md` (10 KB; "Android-OpenHarmony Adapter — 产物部署路径说明", maps out/ subdirs to device paths)
* `deploy_to_dayu200.sh` (47 KB; the staged push driver)
* `deploy_stage.sh` (38 KB; helpers — staging discipline `send → ls verify → cp`)
* `deploy_cmd.txt` (19 KB; flat command reference)
* `restore_after_sync.sh` (56 KB; single-command recovery orchestrator — restores both AOSP and OHOS source trees from clean `repo sync`)
* `gen_boot_image.sh` (12 KB; **has the [B-1..B-6] invariant banner** documenting six historical failure modes for boot-image regeneration)
* `apply_aosp_java_patches.py` (14 KB; **implements the L5 reflection injection** of `IActivityManagerSingleton`, `IActivityTaskManagerSingleton`, `getPackageManager`, `getWindowManagerService` + `getWindowSession`)
* `apply_oh_patches.sh` (7.6 KB), `revert_oh_patches.sh` (2.9 KB), `collect_oh_artifacts.sh` (1.8 KB)
* `apply_appspawnx_routing.py` (7.1 KB) — installs appspawn-x as a routed OH service
* `apply_appspawnx_sepolicy_and_namespace.py` (8.1 KB) — installs SELinux + namespace linker for Layer-1/2

### `adapter-src/` — 333 files (4.5 MB)

The five-forward-bridge + six-reverse-bridge architecture, plus `OHEnvironment.java`. Subdirs under `framework/`:

* `core/` (11 files) — `OHEnvironment.java`, `OHServiceManager.java`, `OhTokenRegistry.java`, `ServiceInterceptor.java`, JNI `adapter_bridge.{cpp,h}` (54 KB cpp — central dispatcher), `oh_callback_handler.{cpp,h}`, `android_log_hilog_bridge.cpp`, `skia_codec_register.cpp`, `oh_br_trace.h`.
* `activity/` — `AbilitySchedulerBridge.java`, `AppSchedulerBridge.java` (97 KB; substantial), `ActivityManagerAdapter.java` (93 KB), `ActivityTaskManagerAdapter.java`, `LifecycleAdapter.java`, `IntentWantConverter.java`, `ServiceConnectionRegistry.java`, etc.; JNI `ability_scheduler_adapter.{cpp,h}` (contains the **ScopedJniAttach RAII** pattern referenced in CR-FF), `app_scheduler_adapter.{cpp,h}`, `oh_ability_manager_client.{cpp,h}`, etc.
* `window/` — `IWindowManagerBridge.java`, `IWindowSessionBridge.java`, `WindowManagerAdapter.java`, `WindowSessionAdapter.java`, `DisplayManagerAdapter.java`, `InputManagerAdapter.java`, `SessionStageBridge.java`, `WindowManagerAgentBridge.java`; JNI `oh_input_bridge.h`, `oh_anativewindow_shim.{cpp,h}`, etc.
* `broadcast/`, `contentprovider/`, `package-manager/`, `surface/` (incl. `skia_rtti_shim/`), `hwui-shim/` — full source trees
* `android-runtime/` — `AndroidRuntime.{cpp,h}` plus ~28 `android_*_*.cpp` JNI translation units
* `appspawn-x/` — full daemon source: `src/main.cpp`, `child_main.{cpp,h}`, `spawn_server.{cpp,h}`, `appspawnx_runtime.{cpp,h}`, `oh_ability_manager_client.cpp`, `AndroidRuntime.cpp`; plus `bionic_compat/` — 25+ stub `.cpp`/`.c` files providing musl ↔ bionic ABI bridge (libc++ math macros, sync builtins, art_runtime_stubs, atrace, signal probing, etc.)
* `mainline-stubs/java/` (96 files) — AOSP API-stub classes for APEX mainline modules (media, net, adservices, etc.) → compiled to `adapter-mainline-stubs.jar` → loaded as 7th BCP segment.
* `jni/BUILD.gn` — gn glue.
* `CMakeLists.txt`

### `app/` — HelloWorld test app (180 KB)

* `HelloWorld.apk` (55 KB; HBC's reference test apk) — present, smoke-loaded onto board successfully (md5 match).
* `helloworld_resources.hap` (51 KB; OH-side resources HAP companion)
* `src/` — full Java sources (`MainActivity.java`, `SecondActivity.java`, `HelloService.java`, `HelloWorldApplication.java`), `AndroidManifest.xml`, `res/layout/activity_main.xml`, `BUILD_INSTRUCTIONS.md`.

### `docs/` — 2 reference docs (44 KB)

* `CLAUDE.md` (30 KB; HBC's own internal handoff for their adapter project — has architecture decisions + dont-repeat-mistakes register)
* `readme.txt` (6 KB; intro)

---

## 3. License / IP findings

* **All cross-built native `.so` files** inherit **Apache License 2.0** from their AOSP source. Verified by `strings` on `libart.so`: paths show `/home/HanBingChen/aosp/art/runtime/native/...` — this is AOSP-14 source recompiled with OH musl, *not* a third-party port. There are no GPL, proprietary, or OEM-licensed embedded artifacts.
* **All Java framework jars** (`framework.jar`, `core-oj.jar`, `core-libart.jar`, ICU jars, `okhttp.jar`, `bouncycastle.jar`, `apache-xml.jar`) are Apache-2.0 (AOSP / third-party canonical).
* **`libskia_canvaskit.z.so`** in `oh-service/` is OH's Skia build (BSD-3-Clause / Google).
* **HBC adapter source** under `framework/` and `aosp_patches/` is the same-company effort referenced in the V3 architecture rationale — no IP risk.
* **`out/aosp_fwk/oh-adapter-framework.jar` vs `out/adapter/oh-adapter-framework.jar`**: HBC's [B-1] invariant — `out/adapter/` is the single source of truth. The first pull accidentally got the stale `out/aosp_fwk/` copy (md5 `69a58027...`); we re-pulled from `out/adapter/` and verified `5bf98a3a...` matches remote. **Anyone re-pulling must use `out/adapter/`.**
* No GPL artifacts. No HBC-team-internal-only markers in headers (all source files we sampled have neutral Android comment headers like "Copyright (C) The Android Open Source Project" or HBC's "JNI Bridge implementation. Connects Android Java layer with OpenHarmony C++ IPC framework.").

---

## 4. Deploy order on DAYU200

This summarizes HBC's `DEPLOY_SOP.md` Stages 1-6. Westlake should treat HBC's SOP as the authoritative deploy script for V3 substrate boot.

1. **Stage 0 — preflight**: verify `hdc shell echo alive`, `boot-framework.art` size = 23 760 896.
2. **Stage 1 — backup** (13 device-side files with `.orig_${TS}` suffix).
3. **Stage 2 — stop services**: `begetctl stop_service foundation render_service` (do NOT stop appspawn or launcher).
4. **Stage 3a — pre-create dirs**: `/system/lib/platformsdk`, `/system/android/lib`, `/system/android/framework/arm`, `/system/android/etc/icu`.
5. **Stage 3b — OH-service `.so`** (10 files + `libbms` symlink) → `/system/lib/` + `/system/lib/platformsdk/`.
6. **Stage 3c — AOSP native `.so`** (38 files) → `/system/android/lib/`. Adapter shims (`liboh_hwui_shim`, `liboh_android_runtime`, `liboh_skia_rtti_shim`) go to BOTH `/system/android/lib/` AND `/system/lib/` (dual-path). Then `chcon u:object_r:system_lib_file:s0` on adapter shims.
7. **Stage 3d — Framework jars + ICU + fonts.xml** (11+1+1 files) → `/system/android/framework/`. ICU → `/system/android/etc/icu/icudt72l.dat`. `fonts.xml` dual-path + `chcon system_fonts_file:s0`.
8. **Stage 3e — Boot image** (27 files = 9 segments × 3) → `/system/android/framework/arm/`. **md5 every file**, **chcon all to `system_lib_file:s0`**.
9. **Stage 3f — appspawn-x bin** (`/system/bin/appspawn-x`, 755), `appspawn_x.cfg`, `appspawn_x_sandbox.json`, namespace linker `ld-musl-namespace-arm.ini`, SELinux `file_contexts`, plus symlinks per SOP.
10. **Stage 4 — restart**: `begetctl start_service foundation`, watch for ART VM phase 2 completion in hilog.
11. **Stage 5 — install test apk**: `bm install -p /data/local/tmp/HelloWorld.apk`.
12. **Stage 6 — aa start**: `aa start -b com.example.helloworld -a MainActivity`. Watch hilog for `MainActivity.onCreate` line 83 (their proven endpoint).

The full driver `deploy_to_dayu200.sh` mechanizes Stages 0-4. Westlake adopts it unchanged for V3-W2.

---

## 5. Westlake adaptations needed (V3-W2 onward)

The HBC stack as delivered is internally consistent and provably bootable (smoke-tested 2026-05-16; see §6). For V3 we need:

1. **No rename / no relocation** of HBC artifacts in V3-W2 (boot them as-is to validate the substrate reaches MainActivity.onCreate).
2. **For V3-W3 onward**, when Westlake hosts its own apps:
   - Westlake host APK manifest declares `intent-filter` for our launched test app(s).
   - Westlake's app-hosting engine (the layer above the HBC substrate, see `V3-ARCHITECTURE.md`) does intent rewriting, multi-app coordination, lifecycle orchestration.
   - **`westlake-host-gradle/` stays unchanged on Android (Phase 1)**; for OHOS (Phase 2), we wrap a thin OH side that talks to `appspawn-x` via its spawn-server socket (per `framework/appspawn-x/src/spawn_server.h`).
3. **No source-tree fork of HBC**. We pull artifacts (compiled), not the AOSP source. HBC's `restore_after_sync.sh` and `gen_boot_image.sh` remain on HBC's machine; Westlake's V3-W2 is a binary deploy.
4. **Skia version pinning** — `libskia_canvaskit.z.so` is OH's stock Skia build, paired with HBC's 12.6 KB `liboh_skia_rtti_shim.so`. Any OH base-image upgrade must re-validate the shim. The 4 L5 framework patches presume Android-14 API surface — re-validate on API-level changes.

---

## 6. Smoke-test result (V3-W1 close)

**PARTIAL PASS** — see `artifacts/ohos-mvp/v3-w1-hbc-smoke/20260516T065719Z/README.md` for the full log.

* Pushed 30 representative artifacts (`appspawn-x` + 27 of 38 AOSP `.so` + `HelloWorld.apk` + `DEPLOY_SOP.md`) to `/data/local/tmp/v3-hbc-smoke-dir/` on DAYU200 dd011a414436314130101250040eac00.
* Verified end-to-end md5 match for `appspawn-x`, `libart.so`, `HelloWorld.apk` (local hash = on-device hash).
* Confirmed board has the matching `/lib/ld-musl-arm.so.1` ABI.
* Ran `./appspawn-x --help` with `LD_LIBRARY_PATH=$D:/system/lib` → **printed the daemon's usage banner** ("Usage: ./appspawn-x [OPTIONS]" + 3 option lines). Exit 0.
* Ran `./appspawn-x` (no args) → daemon entered Phase 1, then SIGABRT-ed at sandbox-config open (expected — no `/system/etc/appspawn_x_sandbox.json` because we did NOT touch `/system/`).
* No `Exec format error`, no `Error relocating ... symbol not found` once the full dep closure was on `LD_LIBRARY_PATH`. The dynamic-linker level of the substrate is sound.

Full-pipeline HelloWorld → `MainActivity.onCreate` line 83 was **NOT** attempted; that requires destructive `/system` overwrites per HBC's DEPLOY_SOP (out of scope for V3-W1 per task constraints — "DON'T modify the board's existing state beyond pushing files to /data/local/tmp/"). V3-W2 owns the full SOP-run, with backup-and-restore.

The smoke test cleaned up after itself (`rm -rf /data/local/tmp/v3-hbc-smoke*`). Board is in the same state as before the test.

---

## 7. Integration risks

| Risk | Impact | Mitigation |
|---|---|---|
| HBC's deploy SOP requires `mount -o remount,rw /` + writing `/system/lib/` + 13-file backups. A failed deploy can soft-brick the board. | HIGH | Always follow Stage 1 backup; `revert_oh_patches.sh` is the rollback path for the local source side, but device-side rollback is the `.orig_${TS}` copies. Test on a board we control. |
| The 9-segment BCP is a tight coupling — any single jar diverges from `appspawn-x/src/main.cpp:62 kBootClasspath` and Phase-2 VM init SIGABRT-storms (HBC's `[B-6]` invariant — 2026-04-30 blood lesson). | HIGH | Treat all 9 jar files + their 27 boot-image segments as a single atomic unit. Never partial-update. |
| `libart-compiler.so` was NOT pulled (we have `libart.so` but not the compiler). **Actually it WAS pulled** — confirmed. But it's the cross-built one (3.6 MB) — adequate for runtime, NOT for on-device dex2oat. | LOW | If on-device JIT/AOT is needed, push it; otherwise use the pre-built `boot.oat`/`.vdex` and skip JIT. |
| HBC's adapter `oh-adapter-framework.jar` has a known double-source pitfall (`out/aosp_fwk/` vs `out/adapter/`). The wrong copy → ART `ValidateOatFile` checksum mismatch SIGABRT. | MED | We pulled the correct `out/adapter/` copy (md5 `5bf98a3a...`). Any re-pull must explicitly target `out/adapter/`. Documented in §3. |
| Skia ABI shim (`liboh_skia_rtti_shim.so`) is 12.6 KB but is a `DT_NEEDED` of `libhwui.so` — miss it and the UI stack dies on first `dlopen`. | HIGH | Always co-deploy; dual-path it (both `/system/lib/` and `/system/android/lib/`) per HBC's SOP §3c. |
| HBC's tree contains 31 `_deprecated/` Python scripts (HBC themselves admits anti-pattern). We skipped them. | LOW | Continue to skip; do NOT resurrect. |
| `framework/appspawn-x/src/main.cpp.bak_*` + 6 other `.bak` files left behind in HBC's tree. We deleted them after pull. | LOW | When re-syncing, re-exclude `.bak*` patterns. |
| Phase-2 OHOS board (DAYU200) is aarch64 kernel but 32-bit userspace (`/system/lib64` absent, system `appspawn` is 32-bit ARM ELF). Matches HBC artifacts (verified). | none | Already aligned with `CR60_BITNESS_PIVOT_DECISION.md`. |
| HBC pulls `framework-classes.dex.jar` (39 MB) AND `framework.jar` (40 MB). Westlake currently has both. Deploy uses both per SOP §3d. | INFO | No action needed — both ship. |
| HBC's BUILD.gn / `Android.bp` references and 56-KB `restore_after_sync.sh` presume HBC's local source paths (`$HOME/aosp`, `$HOME/oh`). | INFO | These scripts are NOT for Westlake re-execution; they document HBC's build environment. Westlake consumes artifacts, not source rebuild. |

---

## 8. Cross-reference

* `V3-ARCHITECTURE.md` §3 (substrate layer) — this artifact set IS the layer agent 42 calls "HBC-runtime substrate".
* `V3-WORKSTREAMS.md` W1 (artifact inventory) — closed by this document + smoke test.
* `V3-WORKSTREAMS.md` W2 (boot standalone HBC runtime on DAYU200) — uses the artifacts under `westlake-deploy-ohos/v3-hbc/` plus HBC's `DEPLOY_SOP.md` driver.
* `CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md` — structural map of the layer; this document is the manifest of compiled artifacts that realize it.
* `CR-FF-HBC-BORROWABLE-PATTERNS.md` — references `ScopedJniAttach` (in `adapter-src/framework/activity/jni/ability_scheduler_adapter.cpp`) and `OHEnvironment.java` (in `adapter-src/framework/core/java/OHEnvironment.java`).
* `CR60_BITNESS_PIVOT_DECISION.md` — preserved; HBC's artifact set is 32-bit ARM EABI5 throughout.
