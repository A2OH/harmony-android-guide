# V3 W11 — V2 Carryforward Audit

**Date:** 2026-05-16
**Author:** agent 44
**Status:** READ-ONLY audit (no code moved). Proposes archive plan for W12 + W13.
**GitHub issue:** [A2OH/westlake#636](https://github.com/A2OH/westlake/issues/636)
**Base commit:** `073059c2` (V3 architecture pivot)
**Companions:** `V3-ARCHITECTURE.md` §4 (deletion catalog), `V3-WORKSTREAMS.md` §W11, `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md`, `V3-SUPERVISION-PLAN.md` §1 (DAG)
**Alias:** This doc is also referenced as `V3-V2-CARRYFORWARD-AUDIT.md` in `V3-WORKSTREAMS.md` §W11 acceptance criteria — same artifact, two filenames in flight; recommend updating the W11 brief to use this filename.

---

## Summary

- **51 components surveyed** across Android-phone V2 substrate, OHOS V2 substrate (dalvik-kitkat era), and cross-cutting infrastructure.
- **22 STAYS** (mostly Phase-1 Android-phone path, plus conceptual lessons/contracts that map forward verbatim)
- **23 ARCHIVES** (V2-OHOS substrate code superseded by HBC's runtime; preserve in `archive/v2-ohos-substrate/` via `git mv`, never `git rm`)
- **6 REFACTORS** (V2 plumbing that survives V3 with adapter or scope change)

Per `V3-WORKSTREAMS.md` §W11 acceptance, every disposition maps to one of the four labels {PHONE-ONLY, V3-INHERITS-VERBATIM, V3-ADAPTS-AT-INTEGRATION-SEAM, V3-OBSOLETED}. The STAYS/ARCHIVES/REFACTORS verbs in the table below align with those labels as: STAYS ≡ PHONE-ONLY ∪ V3-INHERITS-VERBATIM; ARCHIVES ≡ V3-OBSOLETED; REFACTORS ≡ V3-ADAPTS-AT-INTEGRATION-SEAM.

---

## §1. Decision Table

### 1.1 Android-phone path (V2 substrate on cfb7c9e3, OnePlus 6)

These are PHONE-ONLY. They produced the noice + McD in-process Option 3 result (`project_noice_inprocess_breakthrough.md`, 14/14 regression on cfb7c9e3). V3 does NOT change them; only the OHOS-target builds of dual-target artifacts are affected.

| Component | Path | Verdict | Reason | Action |
|---|---|---|---|---|
| `westlake-host-gradle/` (host APK, Option 3 in-process driver) | `westlake-host-gradle/` | STAYS | Phase-1 Android production path; runs on stock Android phone against real `framework.jar`. V3-ARCHITECTURE §3 explicitly preserves this as the OHOS-V3-engine analog on Android. | none |
| `NoiceInProcessActivity.kt` / `McdInProcessActivity.kt` (610 LOC each) | `westlake-host-gradle/app/src/main/java/com/westlake/host/` | STAYS | The reference implementation of the 5-pillar pattern. Informs W3 (V3 launch wrapper) and W4 (adapter scope diffs) as the "what does V3 need to also do?" reference. | none |
| `WestlakeBridgeInstrumentation.kt` (Instrumentation subclass) | `westlake-host-gradle/app/src/main/java/com/westlake/host/` | STAYS | Cross-package intent rewriting for in-process hosting. Directly informs W4/W7 (McD cross-pkg) on V3 side. Per `project_noice_inprocess_breakthrough.md` "open item: McD cross-package intent rewriting needs Java-side Instrumentation subclass". | none |
| `WestlakeActivity.kt` / `WestlakeMcdActivity.kt` / `WestlakeVM.kt` (host-side Compose UIs over guest activities) | `westlake-host-gradle/app/src/main/java/com/westlake/host/` | STAYS | Host UI; Phase-1 only; not part of V3 OHOS path. | none |
| `MultiProcInProcessActivity.kt` + `multiproc-test-gradle/` | `westlake-host-gradle/` + sibling | STAYS | Multi-process Phase-1 validation. Westlake's existing multi-app coordination work that V3 W4 will need as a reference. | none |
| `aosp-libbinder-port/` musl + bionic builds (Android-phone targets) | `aosp-libbinder-port/{out/musl,out/bionic}/` | STAYS | Powers the binder-pivot regression on cfb7c9e3 (14/14). Per CR61.1 §3.A: "Westlake ships `aosp-libbinder-port/out/{musl,bionic}/libbinder.so` + `servicemanager`. CR61 stands [for Android phone]." | none |
| `aosp-shim.dex` (Android-side, framework_duplicates.txt-stripped) | `aosp-shim.dex`, `scripts/framework_duplicates.txt` (1946 lines), `scripts/build-shim-dex.sh` | STAYS | Per `project_real_framework_jar_arch.md` (2026-05-07): "aosp-shim.dex slimmed 72% (1.3MB) by stripping 1813 duplicate-with-framework classes; verified on host x86-64". This is the Android-phone classpath shim and is independent of the dalvik-kitkat OHOS shim. | none |
| `shim/java/` (V2 substrate Java sources: WestlakeActivity, WestlakeApplication, WestlakeResources, WestlakeContextImpl, PhoneWindow, DecorView, WindowManagerImpl, ServiceRegistrar, 50+ services) | `shim/java/com/westlake/{engine,services,compat}/`, `shim/java/{android,com/android/internal}/...` | STAYS (PHONE-ONLY) | These compile into `aosp-shim.dex` and also into `aosp-shim-ohos.dex`. The class set is dual-purpose. For Phase-1 Android they're load-bearing (Westlake-shadowed `Activity`/`Window`/etc.). For OHOS V3 they're replaced by HBC's real `framework.jar`. **Gray area:** the sources STAY because the Android-phone build needs them; only the **OHOS build target** of these sources is obsolete. W13's archive must NOT delete `shim/java/`. | none on source; W13 documents that `build-shim-dex-ohos.sh` and `aosp-shim-ohos.dex` are archived (see ARCHIVES below) |
| `WestlakeLauncher.java` (22,744 LOC — current count, not the 12,403 cited in MEMORY.md) | `shim/java/com/westlake/engine/WestlakeLauncher.java` | STAYS (PHONE-ONLY) | Phase-1 in-process driver; reaches `MainActivity.onCreate` body per `M7_STEP2_REPORT.md`. Per `project_binder_pivot.md` "WestlakeLauncher.java now 12,403 LOC (-46% vs original)" — the file has continued to evolve since that memory entry; current size 22,744. **Risk flag:** MEMORY.md LOC count is stale; W10 should refresh, or remove the LOC claim entirely. | none on code; W10 update memory LOC |
| WestlakeContextImpl + CR59 Hilt fix (`getApplicationContext()` resolution) | `shim/java/com/westlake/services/WestlakeContextImpl.java` (commit `459cb133` "CR62 (E13 stage C unblocked): pre-attached Context via thread-local") and the CR59 changes described in `CR59_REPORT.md` | STAYS (V3-INHERITS-VERBATIM as concept) | The **fix itself** (Application.mBase = null root cause, cross-package protected-method invoke bug) is Phase-1-Android-specific code. The **lesson** (Handler.post on main looper at JNI seam; "blame adapter first") is V3-inheritable per V3-ARCHITECTURE §5.2 row "Handler(Looper.getMainLooper()).post(...) thread-switch at JNI seam — Adopted (already a Westlake lesson per CR59)". | none on Android-phone code; V3 self-audit gate enforces the lesson |
| M5 audio daemon Android-phone target (`aosp-audio-daemon-port` musl/bionic builds) | `aosp-audio-daemon-port/out/{musl,bionic}/` | STAYS | Per V3-ARCHITECTURE §4 "What does NOT get deleted" — "M5 audio daemon + M6 surface daemon for ANDROID phone path — useful for phone V2 path. Only the OHOS targets of these daemons are deleted." | none |
| M6 surface daemon Android-phone target | `aosp-surface-daemon-port/out/{musl,bionic}/` | STAYS | same rationale | none |
| M4 Java service implementations (WestlakeActivityManagerService etc) | `shim/java/com/westlake/services/Westlake{ActivityManager,Display,Notification,Power,Package,Window,InputMethod}*Service.java` | STAYS (PHONE-ONLY) | These ARE the V2 substrate's services. Phase-1 path loads them; V3 OHOS replaces them with real OH services via HBC's 5 forward bridges + 6 reverse bridges (V3-ARCHITECTURE §1.3). Sources stay; only the OHOS-target use is obsoleted. | none |
| `scripts/binder-pivot-regression.sh` (master regression suite) | `scripts/binder-pivot-regression.sh` | STAYS | Per `BINDER_PIVOT_MILESTONES.md` §"Test Plan Master Summary"; runs 7+ smoke + service tests against cfb7c9e3. V3-WORKSTREAMS §W12 requires "Android-phone V2 regression still 14/14 PASS". This script is how that's verified. | none |
| `scripts/{run-noice,run-real-mcd}-{westlake,phone-gate}.sh`, `scripts/check-{noice,real-mcd}-proof.sh`, `scripts/run-mcd-{profile,westlake}.sh`, `scripts/run-yelp-live.sh`, `scripts/run-controlled-showcase.sh` | `scripts/` | STAYS | Phase-1 app-specific drivers; needed for the 14/14 regression. | none |
| `scripts/run-apk.sh`, `scripts/sync-westlake-phone-runtime.sh` | `scripts/` | STAYS | Phase-1 deploy / driver primitives. | none |
| `ohos-deploy/aosp-shim.dex` (root, Android-side build) | `ohos-deploy/aosp-shim.dex` | STAYS | Despite the `ohos-deploy/` directory name, this is the Android-side dex (the OHOS variant is `aosp-shim-ohos.dex`). Phase-1 deploy artifact. **Gray area:** confusing colocation with the OHOS-archived files. W13 should move only the OHOS-specific files out of `ohos-deploy/`, NOT this one. | none |
| 14/14 binder-pivot test artifacts (`HelloBinder.dex`, `AsInterfaceTest.dex`, `*ServiceTest.dex`, `McdLauncher.dex`, `NoiceLauncher.dex`, etc.) | `aosp-libbinder-port/out/{Hello,AsInterface,*Service*,McdLauncher,NoiceLauncher,NoiceDiscoverWrapper,McdDiscoverWrapper}.dex` | STAYS | The actual 14/14 regression payloads. | none |

### 1.2 V2-OHOS substrate components (dalvik-kitkat era) — archived under V3

Per V3-ARCHITECTURE §4, these are superseded by HBC's runtime substrate.

| Component | Path | Verdict | Reason | Action |
|---|---|---|---|---|
| `dalvik-port/aosp-src/`, `bionic-overlay/`, `deps-src/` (source + overlays for cross-compile dalvikvm) | `dalvik-port/{aosp-src,bionic-overlay,deps-src}/` | ARCHIVES | V3-ARCHITECTURE §4 row 1: replaced by HBC's `libart.so` (15 MB, AOSP-14 ART cross-built to OHOS musl). The dalvik-kitkat lineage is end-of-life on V3 OHOS path. ~50K LOC mostly upstream. | `git mv dalvik-port/{aosp-src,bionic-overlay,deps-src}/ archive/v2-ohos-substrate/dalvik-port/` |
| `dalvik-port/build-ohos-{aarch64,arm32,arm32-dynamic,arm32-shlib}/`, `build-ohos.sh` | `dalvik-port/build-ohos*/`, `dalvik-port/build-ohos.sh` | ARCHIVES | Cross-compile pipeline for OHOS dalvikvm. Replaced by HBC artifact pull (W1). | `git mv dalvik-port/build-ohos*/ dalvik-port/build-ohos.sh archive/v2-ohos-substrate/dalvik-port/` |
| `dalvik-port/compat/` (drm_inproc_bridge, software_canvas.h, dalvik_canvas_skia.c, skia_canvas_direct.cpp, oh_drawing_bridge.c, xcomponent_bridge.c, arkui_bridge.cpp, libcore_bridge.cpp, drm_*.c, surface_inproc_smoke.c, test_dvm_entry_inproc.c, ohos_dlopen_smoke.c, plus subdirs: m6-drm-daemon/, log/, machine/, selinux/, sys/, system/, utils/, nativehelper/, bionic/, cutils/) | `dalvik-port/compat/` | ARCHIVES | All of this is the OHOS-targeted native glue Westlake built to bridge dalvikvm to the platform. Replaced by HBC's `liboh_adapter_bridge.so` + `liboh_skia_rtti_shim.so` + `liboh_hwui_shim.so`. | `git mv dalvik-port/compat/ archive/v2-ohos-substrate/dalvik-port/compat/` |
| `dalvik-port/compat/m6-drm-daemon/m6_drm_daemon.c` (OHOS M6 surface daemon) | `dalvik-port/compat/m6-drm-daemon/m6_drm_daemon.c` | ARCHIVES | "Phase 2 M6 daemon, OHOS variant (sibling of aosp-surface-daemon-port for Android Phase 1)" per file header. V3-ARCHITECTURE §4 row 12: replaced by HBC's `OHGraphicBufferProducer` + `RSSurfaceNode` path. **Mild gray area:** could conceivably be a "DRM-direct fallback for boards without HBC runtime" — but that's a hypothetical we don't need today; CR60 / V3 commits to DAYU200 with HBC's stack. | covered by `dalvik-port/compat/` move above |
| `dalvik-port/Makefile`, `dalvik-port/launcher.cpp` (currently modified in working tree) | `dalvik-port/{Makefile,launcher.cpp}` | ARCHIVES | These tie the cross-compile pipeline together for OHOS targets. Working-tree changes (uncommitted, see `git status`) imply the previous agent was still iterating; W12/W13 should evaluate whether those changes need committing first or can simply be discarded as part of archive. | commit-then-`git mv` (or discard then `git mv`); coordinate with whoever owns the WIP |
| `dalvik-port/patches/` (33 patches for KitKat dalvikvm 64-bit, JNI, FFI, init/thread/boot fixes) | `dalvik-port/patches/` | ARCHIVES | Patches against `~/dalvik-kitkat/` source; obsolete under V3. | `git mv dalvik-port/patches/ archive/v2-ohos-substrate/dalvik-port/patches/` |
| `dalvik-port/build_*.sh` (build_noice_launcher, build_mcd_*, build_*_test, build_discover.sh, build_asinterface.sh) | `dalvik-port/build_*.sh` | ARCHIVES | Dalvik-on-OHOS test wrapper builds. Replaced by V3's `hdc shell aa start <bundle>` model. **Confusion risk:** some of these (e.g. `build_noice_launcher.sh`) may have been used to produce the Android-phone-V2 DEX files in `aosp-libbinder-port/out/`. Audit at archive time. If any script is dual-purpose, refactor or duplicate before archive. | conditional `git mv`; review per-script first |
| `dalvik-port/{BUILD_PLAN,M3_NOTES,M4_PRE_NOTES,README}.md`, `lib-boot.sh`, `m3-dalvikvm-boot.sh` | `dalvik-port/*.md`, `dalvik-port/{lib-boot,m3-dalvikvm-boot}.sh` | ARCHIVES | Dalvik-kitkat-era docs and boot scripts. Preserve as historical record. | `git mv dalvik-port/{BUILD_PLAN,M3_NOTES,M4_PRE_NOTES,README}.md dalvik-port/{lib-boot,m3-dalvikvm-boot}.sh archive/v2-ohos-substrate/dalvik-port/` |
| `aosp-shim-ohos.dex` (custom OHOS framework substitute, built via `build-shim-dex-ohos.sh`) | `ohos-deploy/aosp-shim-ohos.dex` | ARCHIVES | V3-ARCHITECTURE §4 row 2: replaced by HBC's real `framework.jar`. | `git mv ohos-deploy/aosp-shim-ohos.dex archive/v2-ohos-substrate/ohos-deploy/` |
| `scripts/build-shim-dex-ohos.sh` (CR65 STRIP_CLASSES pipeline) | `scripts/build-shim-dex-ohos.sh` | ARCHIVES | Produces the obsoleted `aosp-shim-ohos.dex`. STRIP_CLASSES list (4 androidx classes) was a CR65-era workaround for E13 stage C+D class collisions; V3 has no equivalent need because HBC ships the real BCP. **CR65 STRIP_CLASSES build pipeline — verdict: ARCHIVES** (not refactor; nothing in V3 boot image needs class-stripping because we don't build a boot image — HBC does, and they ship one as artifact). | `git mv scripts/build-shim-dex-ohos.sh archive/v2-ohos-substrate/scripts/` |
| `core-android-x86.jar` + `.pre-cr-y+1` backup (Java libcore for kitkat dalvikvm) | `ohos-deploy/core-android-x86.jar{,.pre-cr-y+1}` | ARCHIVES | V3-ARCHITECTURE §4: "Real AOSP libcore" supersedes. HBC's BCP has real `core-oj.jar`, `core-libart.jar`, `core-icu4j.jar`. | `git mv ohos-deploy/core-android-x86.jar{,.pre-cr-y+1} archive/v2-ohos-substrate/ohos-deploy/` |
| Locale.forLanguageTag BCP patch (CR-Y+1) | inside `core-android-x86.jar` (binary-edited; source recipe in `scripts/patch-core-oj-westlake.py` if present and OHOS-target) | ARCHIVES | V3-ARCHITECTURE §4 row 7: "Real AOSP libcore ships unmodified". | covered by `core-android-x86.jar` archive |
| `ohos-deploy/{boot.art,boot.oat,boot.vdex}` + per-segment boot images (`boot-{aosp-shim,core-icu4j,core-libart,apache-xml,bouncycastle}.{art,oat,vdex}`) | `ohos-deploy/boot*.{art,oat,vdex}` | ARCHIVES | V2-OHOS boot images for dalvik-kitkat. V3 uses HBC's 9-segment dex2oat-baked boot image (V3-ARCHITECTURE §5.1). | `git mv ohos-deploy/boot*.{art,oat,vdex} archive/v2-ohos-substrate/ohos-deploy/` |
| `ohos-deploy/dalvikvm` (OHOS-target dalvikvm binary) | `ohos-deploy/dalvikvm` | ARCHIVES | The deployable VM binary itself. | `git mv ohos-deploy/dalvikvm archive/v2-ohos-substrate/ohos-deploy/` |
| `ohos-deploy/liboh_bridge.so` (V2 OHOS bridge .so) | `ohos-deploy/liboh_bridge.so` | ARCHIVES | V2 bridge between dalvikvm and OHOS native APIs. Replaced by HBC's `liboh_adapter_bridge.so`. | `git mv ohos-deploy/liboh_bridge.so archive/v2-ohos-substrate/ohos-deploy/` |
| `ohos-deploy/arm64/` + `ohos-deploy/arm64-a15/` (V2 OHOS arm64 binaries) | `ohos-deploy/arm64{,-a15}/` | ARCHIVES | Per CR60 bitness pivot, OHOS path is now 32-bit-primary; these arm64 artifacts are vestigial. Note CR60 §"Cost of reverse-pivot" preserves the path conceptually; the binaries themselves are V2-substrate-shaped. | `git mv ohos-deploy/arm64{,-a15}/ archive/v2-ohos-substrate/ohos-deploy/` |
| `ohos-deploy/{app.dex,direct-print-stream.jar,run.sh}` (V2 OHOS test fixtures) | `ohos-deploy/{app.dex,direct-print-stream.jar,run.sh}` | ARCHIVES | V2 OHOS dalvikvm test rig. | `git mv ohos-deploy/{app.dex,direct-print-stream.jar,run.sh} archive/v2-ohos-substrate/ohos-deploy/` |
| `ohos-tests-gradle/{red-square,inproc-app-launcher,m6-test,launcher,trivial-activity,hello,hello-color-apk,xcomponent-test}/` (mock/probe APKs from dalvik-kitkat OHOS era) | `ohos-tests-gradle/*/` | ARCHIVES | These are the test APK harnesses for V2 OHOS substrate (SoftwareCanvas, drm_inproc_bridge, OhosMvpLauncher, M6DrmClient). Per V3-ARCHITECTURE §4 rows 8-12 plus W5 acceptance: V3's mock APK is a fresh smoke (`mock-apks/v3-smoke/`), NOT these. **Gray area:** `trivial-activity` is a stock-Android no-Westlake-import smoke; could conceivably be repurposed as W5's mock — but cleaner to archive and let W5 build fresh per its acceptance criteria. | `git mv ohos-tests-gradle/ archive/v2-ohos-substrate/ohos-tests-gradle/` (the entire gradle root) |
| `ohos-tests-gradle/launcher/.../OhosMvpLauncher.java` | inside the above | ARCHIVES | V3-ARCHITECTURE §4 row 13 (~600 LOC); replaced by `aa start <bundle>` → HBC `appspawn-x`. **NOTE my task brief says "REFACTORS (becomes appspawn-x bridge per W3)" but V3-WORKSTREAMS §W3 acceptance says "OhosMvpLauncher source files moved to archive/v2-ohos-substrate/launchers/" — i.e., ARCHIVES, with the W3 replacement being a thin `hdc shell aa start ...` shell driver, NOT a refactored OhosMvpLauncher. I follow V3-WORKSTREAMS here over the brief.** | covered by `ohos-tests-gradle/` move above |
| `ohos-tests-gradle/red-square/.../SoftwareCanvas.java`, `ohos-tests-gradle/inproc-app-launcher/.../SoftwareCanvas.java` | inside the above | ARCHIVES | V3-ARCHITECTURE §4 row 8: real libhwui + real Skia supersede. | covered by `ohos-tests-gradle/` move above |
| `ohos-tests-gradle/inproc-app-launcher/.../InProcessAppLauncher.java`, `DrmInprocessPresenter.java`, `InProcDrawSource.java` | inside the above | ARCHIVES | V3-ARCHITECTURE §4 row 14: replaced by real hwui RenderThread + EGL eglSwapBuffers. | covered |
| `ohos-tests-gradle/m6-test/.../M6DrmClient.java`, `M6FramePainter.java`, `M6ClientTestActivity.java`, `m6-bridge-stub/.../UnixSocketBridge.java` | inside the above | ARCHIVES | V2 OHOS M6 surface client; replaced by HBC's ANativeWindow → IBufferProducer path. **UnixSocketBridge: there are two copies — `shim/java/com/westlake/compat/UnixSocketBridge.java` (Phase-1 substrate, used by Android phone) and `ohos-tests-gradle/m6-test/.../UnixSocketBridge.java` (OHOS M6 test). The OHOS-test one ARCHIVES with `ohos-tests-gradle/`; the `shim/java/` one STAYS (Phase-1).** | covered for OHOS-test copy |
| `ohos-tests-gradle/red-square/.../Fb0Presenter.java`, `DrmPresenter.java`, `RedView.java`, `MainActivity.java` | inside the above | ARCHIVES | MVP-2 red-square fixtures (visible-pixel via fb0/DRM). Done their job; V3 doesn't replay. | covered |
| `aosp-libbinder-port/out/arm32/` (Westlake-built OHOS arm32 libbinder target — if present) | `aosp-libbinder-port/out/arm32/` | ARCHIVES (conditional) | V3-WORKSTREAMS §W12 acceptance: "if it exists as a Westlake-built OHOS arm32 target, moved to archive/v2-ohos-substrate/aosp-libbinder-port-arm32/. The musl + bionic builds stay in place (Android-phone V2)." **Check at archive time whether `out/arm32/` exists; I did not enumerate it during this audit.** | `git mv aosp-libbinder-port/out/arm32/ archive/v2-ohos-substrate/aosp-libbinder-port-arm32/` (only if exists) |
| `aosp-audio-daemon-port/` OHOS-arm32-target binaries (if any) | `aosp-audio-daemon-port/out/<ohos-arm32-target>/` | ARCHIVES (conditional) | Same shape as previous row. V3-WORKSTREAMS §W12. | conditional `git mv` |
| `aosp-surface-daemon-port/` OHOS-arm32-target binaries (if any) | `aosp-surface-daemon-port/out/<ohos-arm32-target>/` | ARCHIVES (conditional) | Same shape. V3-WORKSTREAMS §W12. | conditional `git mv` |
| CR-W theme arsc parser | `shim/java/com/westlake/services/ResourceArscParser.java` | ARCHIVES (PHONE-ONLY-IF-USED) | V3-ARCHITECTURE §4 row 4: replaced by real `Resources` / `AssetManager` from AOSP. **Gray area:** this class might also be referenced by Phase-1 Android substrate (since Westlake-shadowed Resources are loaded on phone too). If `grep -r ResourceArscParser westlake-host-gradle/ shim/java/com/westlake/{compat,engine}/` shows usage outside `services/`, this is Phase-1-load-bearing and STAYS. **W13 should grep before moving.** Safer default: STAYS in `shim/java/` until grep proves it's OHOS-only. | grep-first; if Phase-1 callers exist → STAYS; else move to `archive/v2-ohos-substrate/shim-services/` |
| CR-X+1 lifecycle drive code (Activity.onStart/onResume drive in apk-mode launcher) | inside `WestlakeLauncher.java` and `OhosMvpLauncher.java` | ARCHIVES (the OHOS path); STAYS (the Android-phone path) | Per V3-ARCHITECTURE §4 row 5: replaced by real ClientTransaction + handleLaunchActivity via HBC's AbilitySchedulerBridge. **The Android-phone in-process drive (5-pillar pattern, "lifecycle drive to Resumed") STAYS** per `project_noice_inprocess_breakthrough.md`; that code is in the host APK, not the dalvik-port driver. | covered by OhosMvpLauncher archive above |
| CR-Z Date/ByteOrder `<clinit>` fixes (BCP-level patches) | inside `core-android-x86.jar` (post-CR-Z) | ARCHIVES | V3-ARCHITECTURE §4 row 7: real AOSP libcore in HBC's BCP supersedes both. | covered by `core-android-x86.jar` archive |
| CR62/CR63/CR64 substrate fixes (Configuration.setTo, Theme.rebase, strip 4 BCP-colliding AndroidX shim classes) | the changes themselves landed in commits `459cb133` (CR62), `cf617394` (CR63), `caa49411` (CR64) | ARCHIVES-CONCEPTUALLY | These were workarounds for V2-OHOS substrate gaps (E13 stage C-past-attachBaseContext). V3 doesn't replay them: real `framework.jar` has the correct behavior natively. **No code to move** — the workarounds are intertwined with V2 substrate code paths that themselves archive. | none beyond V2 substrate archive |
| CR65 STRIP_CLASSES build pipeline (`scripts/build-shim-dex-ohos.sh` STRIP_CLASSES array) | `scripts/build-shim-dex-ohos.sh` lines stripping 4 androidx classes | ARCHIVES | Already covered by `build-shim-dex-ohos.sh` archive above. **No V3 refactor needed**: HBC's framework already has consistent androidx provenance; no class-stripping required at V3 build time. | covered |
| `westlake-deploy-ohos/v3-hbc/` (already created at HEAD as untracked) | `westlake-deploy-ohos/v3-hbc/` | NOT-YET-IN-VCS | This appears to be an in-flight V3 deployment skeleton (subdirs: `adapter-src/`, `app/`, `bcp/`, `bin/`, `docs/`, `etc/`, `jars/`, `lib/`, `patches/`, `scripts/`) prepared by a previous agent. **Not yet `git add`-ed.** W1 will likely formalize this into `third_party/hbc-runtime/`. **W11 audit recommendation:** before W1 commits, decide whether `westlake-deploy-ohos/v3-hbc/` should be (a) renamed to `third_party/hbc-runtime/` per V3-WORKSTREAMS §W1 acceptance, or (b) kept as a separate deploy-staging tree and `third_party/hbc-runtime/` becomes the canonical source. Either is OK, but it should be settled in W1 not later. | flag for W1 owner |

### 1.3 Cross-cutting

| Component | Path | Verdict | Reason | Action |
|---|---|---|---|---|
| Macro-shim contract (`feedback_macro_shim_contract.md`) | memory file | STAYS (REFACTORS scope) | V3-ARCHITECTURE §7: still applies, scope narrowed from "framework class surface" to "integration seam between Westlake engine and HBC runtime". Self-audit gate runs at every V3 CR. | none on file; V3 CR review enforces |
| Subtraction-not-addition rule (`feedback_subtraction_not_addition.md`) | memory file | STAYS | Debugging discipline; V3-applicable. | none |
| Additive-shim-vs-architectural-pivot rule (`feedback_additive_shim_vs_architectural_pivot.md`) | memory file | STAYS | The rule that motivated V3 itself; immortal. | none |
| No-per-app-hacks rule (`feedback_no_per_app_hacks.md`) | memory file | STAYS | V3-ARCHITECTURE §8 anti-pattern #5 cites this. | none |
| `feedback_core_jar.md`, `feedback_agentb_complete.md`, `feedback_bitness_as_parameter.md` | memory files | STAYS | Engineering discipline; carry-forward. Bitness rule (CR60) is V3 prerequisite per V3-ARCHITECTURE §6.5. | none |
| CR60 32-bit pivot (`CR60_BITNESS_PIVOT_DECISION.md`) | docs/engine/ | STAYS | V3 inherits 32-bit ARM on DAYU200 (V3-ARCHITECTURE §6.5: "CR60 bitness discipline still applies"). | none |
| CR61 binder prohibition (`CR61_BINDER_STRATEGY_POST_CR60.md`) | docs/engine/ | REFACTORS (header annotation only) | V3-WORKSTREAMS §W12: "annotated AMENDED-BY pointer to CR61.1". V2 path text remains verbatim; OHOS path AMENDED. **In flight per W12** — header annotation is the deliverable, not a file move. | append AMENDED-BY header note per W12 |
| CR61.1 amendment (`CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md`) | docs/engine/ | STAYS (just landed in `073059c2`) | V3-internal-consistency document. | none |
| Memory system (`MEMORY.md` + handoffs) | `/home/dspfac/.claude/projects/-home-dspfac-openharmony/memory/` | REFACTORS | W10 refreshes "START HERE" pointer to V3; banners V2-OHOS memories as superseded; preserves content verbatim. Out of W11 scope; flagged here for the W10 owner. **Stale claim to update:** MEMORY.md "WestlakeLauncher.java now 12,403 LOC" — actual count today is 22,744. | per W10 |
| `project_v2_ohos_direction.md` memory | memory file | REFACTORS | W10: SUPERSEDED-BY banner; content preserved. | per W10 |
| `project_binder_pivot.md` memory | memory file | REFACTORS | W10: CR61 amended-by-CR61.1 reference added; content preserved. | per W10 |
| `project_noice_inprocess_breakthrough.md` memory | memory file | STAYS | Phase-1 Android baseline, unchanged by V3. | none |
| `project_real_framework_jar_arch.md` memory | memory file | STAYS | 2026-05-07 cleanup; Phase-1 Android-side aosp-shim deduplication. V3-orthogonal. | none |
| `scripts/run-ohos-test.sh` (test infrastructure) | `scripts/run-ohos-test.sh` | REFACTORS | Wraps OHOS dalvikvm test invocation. V3 needs `scripts/v3/run-hbc-test.sh` (or analogous) for `hdc shell aa start <bundle>` smoke. The old script is V2-substrate-shaped; refactor or archive + replace. Recommend: keep old script alive for the OHOS dalvik-kitkat archive build target (per W13 acceptance "can still be built explicitly via `--target=v2-ohos-archive` flag"); add new V3 script. | new V3 script per W2/W3; archive-build wrapper retained |
| `scripts/run-apk-ohos.sh` | `scripts/run-apk-ohos.sh` | ARCHIVES (likely) | Run-apk wrapper for V2 OHOS substrate. V3 launches via `aa start`. **Grep at archive time** to confirm no Phase-1 callers. | `git mv scripts/run-apk-ohos.sh archive/v2-ohos-substrate/scripts/` |
| `scripts/build-shim-dex.sh` (Android-side build) | `scripts/build-shim-dex.sh` | STAYS | Builds the Phase-1 Android `aosp-shim.dex`. Distinct from `build-shim-dex-ohos.sh`. | none |
| Top-level `aosp-shim.dex` + `westlake-host-gradle/app/build/intermediates/.../aosp-shim.dex` (currently modified per `git status`) | various | STAYS | Build outputs for Phase-1; the modifications in working tree are part of normal Phase-1 iteration. | none |
| `westlake-host` (two directories, one untracked / one tracked — see `ls` of repo root showing both) | `westlake-host/` (twice) | STAYS (audit further) | The repo has two `westlake-host` entries at top-level. Possibly a tracked dir + an untracked one of the same name, or a typo in my `ls` output reflecting two different listings. **Flag for inspection;** if duplicated, dedupe before W13. | manual inspection required |

---

## §2. 5-Pillar Pattern Disposition (per `V3-WORKSTREAMS.md` §W11 explicit enumeration)

Per W11 acceptance: every V2 pillar must have a disposition.

| Pillar (from `project_noice_inprocess_breakthrough.md`) | Disposition | V3 mapping |
|---|---|---|
| **1. Hidden-API bypass** | PHONE-ONLY for V3 OHOS | On Android phone, requires reflection-against-platform-hidden-API workarounds. HBC's runtime IS real AOSP-14 framework with everything exposed via real classloader; no hidden-API gating to bypass. The pillar's lesson (reflection brittleness) informs V3 macro-shim contract §7 "no setAccessible(true)". |
| **2. LoadedApk dir redirect** | PHONE-ONLY for V3 OHOS | Android phone needs LoadedApk redirected so guest APK's data/code dirs resolve. HBC's `appspawn-x` already forks per-app with correct data dirs (it's a Zygote analog). No redirect needed. |
| **3. Safe-context bind stub** | V3-INHERITS-VERBATIM (as concept) | The V2 lesson — that bind/getApplicationContext can be invoked before Context's mBase is non-null, requiring a fallback — informs V3 W4's adapter-scope-diffs and Hilt-aware behaviors. The actual code (`WestlakeContextImpl` thread-local) stays Phase-1; V3 expresses the same invariant via HBC's `OHEnvironment` pattern. |
| **4. LocaleManager binder hook** | V3-ADAPTS-AT-INTEGRATION-SEAM | V2 Android needed to hook LocaleManager because the host process's Locale state leaks. V3 has real AOSP libcore (V3-ARCHITECTURE §4 row 6) so Locale state is correctly per-process from `appspawn-x` fork. The lesson — locale state is process-global and must be set before app code runs — maps to V3's appspawn-x entrypoint setup; no Westlake-side hook needed. |
| **5. Lifecycle drive to Resumed** | V3-INHERITS-VERBATIM (lesson + Handler.post idiom) | CR59 lesson: `Handler(Looper.getMainLooper()).post(...)` at JNI seam. V3-ARCHITECTURE §5.2 row "Handler thread-switch at JNI seam — Adopted (already a Westlake lesson per CR59)" cites this. Per `project_noice_inprocess_breakthrough.md`, this is the lesson V3 directly inherits. |

---

## §3. Archive Plan (concrete `git mv` commands for W12 + W13)

**Important:** All commands assume `cwd = /home/dspfac/android-to-openharmony-migration` and that `archive/v2-ohos-substrate/` already exists. None of these are executed by W11 — this is the proposal for W12/W13 owners.

### 3.1 Pre-flight

```bash
cd /home/dspfac/android-to-openharmony-migration
mkdir -p archive/v2-ohos-substrate/{dalvik-port/{compat,patches},ohos-deploy,ohos-tests-gradle,scripts,shim-services,aosp-libbinder-port-arm32,aosp-audio-daemon-port-arm32,aosp-surface-daemon-port-arm32}
# Decide: commit or discard dalvik-port/{Makefile,launcher.cpp} WIP changes
git status dalvik-port/{Makefile,launcher.cpp}
# Decide: commit or `git add` westlake-deploy-ohos/v3-hbc/ (W1 owner)
```

### 3.2 dalvik-port archive moves

```bash
git mv dalvik-port/aosp-src                      archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/bionic-overlay                archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/deps-src                      archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/build-ohos-aarch64            archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/build-ohos-arm32              archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/build-ohos-arm32-dynamic      archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/build-ohos-arm32-shlib        archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/build-ohos.sh                 archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/compat                        archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/patches                       archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/Makefile                      archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/launcher.cpp                  archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/BUILD_PLAN.md                 archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/M3_NOTES.md                   archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/M4_PRE_NOTES.md               archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/README.md                     archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/lib-boot.sh                   archive/v2-ohos-substrate/dalvik-port/
git mv dalvik-port/m3-dalvikvm-boot.sh           archive/v2-ohos-substrate/dalvik-port/

# Conditional (audit each per §1.2 row "dalvik-port/build_*.sh"):
for s in dalvik-port/build_{activity_service_test,asinterface,discover,hello,m4de_tests,mcd_discover_wrapper,mcd_launcher,mcd_production_launcher,noice_launcher,noice_production_launcher,package_service_test,power_service_test,system_service_route_test,window_service_test}.sh; do
    grep -l "ohos\|musl" "$s" 2>/dev/null  # detect target
done
# move OHOS-only scripts; keep dual-purpose scripts pending refactor
```

### 3.3 ohos-deploy archive moves

```bash
git mv ohos-deploy/aosp-shim-ohos.dex            archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/core-android-x86.jar          archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/core-android-x86.jar.pre-cr-y+1 archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/dalvikvm                      archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/liboh_bridge.so               archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/arm64                         archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/arm64-a15                     archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/app.dex                       archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/direct-print-stream.jar       archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/run.sh                        archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/core-icu4j.jar                archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/core-libart.jar               archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/core-oj.jar                   archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/boot.art                      archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/boot.oat                      archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/boot.vdex                     archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/boot-aosp-shim.art            archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/boot-aosp-shim.oat            archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/boot-aosp-shim.vdex           archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/boot-core-icu4j.art           archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/boot-core-icu4j.oat           archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/boot-core-icu4j.vdex          archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/boot-core-libart.art          archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/boot-core-libart.oat          archive/v2-ohos-substrate/ohos-deploy/
git mv ohos-deploy/boot-core-libart.vdex         archive/v2-ohos-substrate/ohos-deploy/
# NOTE: leave ohos-deploy/aosp-shim.dex IN PLACE — that's the Phase-1 Android-side shim.
```

### 3.4 ohos-tests-gradle archive moves

```bash
git mv ohos-tests-gradle archive/v2-ohos-substrate/
```

### 3.5 scripts archive moves

```bash
git mv scripts/build-shim-dex-ohos.sh            archive/v2-ohos-substrate/scripts/
git mv scripts/run-apk-ohos.sh                   archive/v2-ohos-substrate/scripts/
# scripts/run-ohos-test.sh: do NOT archive — refactor per W2/W3 to also drive the V3 path,
# or keep both old + new. W12 owner decides.
```

### 3.6 Optional daemon-port arm32 moves (conditional)

```bash
[ -d aosp-libbinder-port/out/arm32         ] && git mv aosp-libbinder-port/out/arm32         archive/v2-ohos-substrate/aosp-libbinder-port-arm32/
[ -d aosp-audio-daemon-port/out/arm32      ] && git mv aosp-audio-daemon-port/out/arm32      archive/v2-ohos-substrate/aosp-audio-daemon-port-arm32/
[ -d aosp-surface-daemon-port/out/arm32    ] && git mv aosp-surface-daemon-port/out/arm32    archive/v2-ohos-substrate/aosp-surface-daemon-port-arm32/
```

### 3.7 ResourceArscParser (grep-first)

```bash
# If outside-services callers exist → STAYS in shim/java/; else archive.
grep -rn "ResourceArscParser" /home/dspfac/android-to-openharmony-migration \
    --include='*.java' --include='*.kt' \
    | grep -v "shim/java/com/westlake/services/ResourceArscParser.java"
# Empty result → archive:
# git mv shim/java/com/westlake/services/ResourceArscParser.java archive/v2-ohos-substrate/shim-services/
```

### 3.8 Post-archive validation

```bash
# Default build doesn't touch V2-OHOS targets (W13 acceptance)
make -n 2>&1 | grep -E "(aosp-shim-ohos|core-android-x86|dalvikvm.*ohos)" && echo FAIL || echo OK
# Android-phone V2 regression still 14/14
bash scripts/binder-pivot-regression.sh --full
# Westlake-only artifact still in place on Android-phone deploy
ls -l aosp-shim.dex aosp-libbinder-port/out/{musl,bionic}/libbinder.so
```

---

## §4. Carryforward to V3 (concrete mapping, not "V2 work is valuable")

The following V2 Phase-1 Android work directly applies to V3. Each row gives the V3 workstream + concrete consumption pattern.

| V2 work | V3 workstream | How it applies |
|---|---|---|
| 5-pillar pattern (5 specific shims for in-process hosting on Android phone) | W3, W4 | W3 launch model uses HBC's `aa start <bundle>`, but the 5-pillar pattern reveals which hosting concerns also exist on the V3 side: hidden-API (NONE under V3 — real classloader), LoadedApk-dir-redirect (NONE — appspawn-x forks per-app), safe-context-bind (HBC's `OHEnvironment.getActivityManagerAdapter()` returns null safely; W4 verifies our apps tolerate this same boundary), LocaleManager (NONE — real libcore), lifecycle-drive-to-Resumed (V3 uses HBC's `AbilitySchedulerBridge` which produces the same end state via real `ClientTransaction` instead of Westlake-driven mutation). |
| `WestlakeBridgeInstrumentation.kt` (cross-pkg intent rewriting) | W4, W7 | The Java-side Instrumentation subclass pattern (`project_noice_inprocess_breakthrough.md` open item for McD) IS the V3 W4 deliverable for intent rewriting. Architecturally portable. |
| CR59 Handler.post-on-main-looper-at-JNI-seam lesson | W3, W4, W6 self-audit checklist | V3-ARCHITECTURE §9 self-audit gate: "If lifecycle drive was involved, real `Handler(Looper.getMainLooper()).post(...)` was used at the JNI seam (CR59 lesson)." |
| Macro-shim contract (`feedback_macro_shim_contract.md`) | W4 acceptance, every V3 CR | V3-ARCHITECTURE §7 + §9 carry the contract forward with narrower scope (integration seams, not framework class surface). Forbidden list (Unsafe.allocateInstance / setAccessible / per-app branches) is unchanged. |
| Macro-shim self-audit grep | every V3 CR | The exact grep idioms (V3-ARCHITECTURE §7) port over: `grep -rn "sun.misc.Unsafe\|jdk.internal.misc.Unsafe\|Unsafe.allocateInstance" <touched files>` etc. |
| `aosp-libbinder-port/` cross-compile experience (musl + bionic ABI) | W4 (HBC's bionic_compat.so understanding) | V3 doesn't ship our libbinder on OHOS, but the ABI understanding we built — bionic ↔ musl mismatches, parcel format, FFI boundaries — directly informs reading HBC's `libbionic_compat.so` source and diagnosing W4 integration issues. |
| CR60 32-bit ARM bitness discipline | W2 deploy validation (DAYU200 LONG_BIT=32 check) | V3-ARCHITECTURE §6.5 + V3-WORKSTREAMS §W2 acceptance "verified via `hdc shell getconf LONG_BIT` returning `32`". |
| Per-app constants table (4 constants per app from Phase-1) | W4, W6, W7 | V3-ARCHITECTURE §3 "per-app diff = 4 constants + manifest aliases" — preserves V2's contract. W6/W7 acceptance: "noice has its 4-constant entry; zero per-app code branches anywhere in Westlake code". |
| `framework_duplicates.txt` (1946 lines listing classes deduplicated from aosp-shim.dex against framework.jar) | W4 reference reading | HBC handles this via 4 surgical L5 patches instead of mass-dedup; the comparison is illustrative for the W4 owner's mental model. |
| `binder-pivot-regression.sh` master regression script | W12 gate, W13 acceptance | V3-WORKSTREAMS §W12 / W13: "Android-phone V2 regression still 14/14 PASS". This script is how every V3 CR's "did I accidentally break Phase-1?" check runs. |
| `feedback_subtraction_not_addition.md` debugging discipline | V3 debugging | "debug by removing layers from a working baseline, never by speculatively adding shims" — applies at V3 integration seams. |
| `feedback_additive_shim_vs_architectural_pivot.md` rule | V3 supervisor judgment | The rule that produced V3 itself. V3-ARCHITECTURE §8 anti-pattern #1 ("Just one more shim and we're there") restates for V3. |

---

## §5. Risks for Downstream Workstreams

Per the W11 self-audit checklist: identify anything that needs CR61.1 follow-up or downstream cleanup.

### R1 — Hilt may need a CR59-equivalent fix on V3

CR59 resolved `getApplicationContext()` returning null in noice's Hilt-generated `Hilt_MainActivity` because `Application.mBase` was null during early lifecycle. On V3, HBC's `MainActivity.onCreate` reached line 83 (TextView ctor) per V3-ARCHITECTURE §1.3, but that's a HelloWorld with no Hilt. **W6 (noice via V3) may rediscover this exact failure mode** because HBC has not validated Hilt against their adapter scope (V3-SUPERVISION-PLAN §7 risk #1). Mitigation: W4 includes Hilt-aware adapter customization; W6 acceptance includes "Hilt + multi-fragment NavController app" validation.

**Action item:** W4 owner reads `CR59_REPORT.md` end-to-end and proactively asks "does HBC's `OHEnvironment.getActivityManagerAdapter()` return a real adapter with non-null Context, or a safe-null analog?" before writing any shadow classes. If safe-null, port the CR59 thread-local Context pre-attachment pattern as a Westlake-owned shadow class (per CR-FF Pattern 2, in `oh-adapter-runtime-westlake.jar`).

### R2 — V3 has no regression suite analog

`binder-pivot-regression.sh` runs 7+ tests against cfb7c9e3 (Android phone). V3-WORKSTREAMS §W2/W3/W5/W6/W7 each have their own self-audit gates, but there is no equivalent of a single "run-everything-and-fail-fast" regression script for V3. Mitigation suggestion: W2 produces a `scripts/v3/run-hbc-regression.sh` skeleton (HelloWorld smoke), W5 extends it (mock APK), W6 + W7 extend further (real apps). At G7 (V2-OHOS substrate archived) we should have a single V3 regression command analog to the V2 one.

**Action item:** W2 owner authors `scripts/v3/run-hbc-regression.sh` even as a stub. Don't wait until W7 to discover we have no consolidated test entry point.

### R3 — HBC artifacts are unstable; archive plan assumes W1 freeze

V3-SUPERVISION-PLAN §7 risk #3: HBC iterates with "ongoing, hundreds of entries" in their build_patch_log.html. W1 manifests a frozen HBC version, but my archive plan moves V2-OHOS code to `archive/v2-ohos-substrate/` based on the assumption HBC's stack is the production path. **If HBC pivots away from their current stack in the next 4-6 weeks** (V3-SUPERVISION-PLAN §3 wall-clock estimate), we'd want to un-archive some of the V2-OHOS work. Mitigation: the `git mv` archive plan is reversible (`git mv archive/v2-ohos-substrate/dalvik-port/ dalvik-port/`); the discipline cost is the audit time.

**Action item:** W13 acceptance should include "archive is reversible: a single command restores any subtree". The directory move preserves git history, so this is automatic; just document it.

### R4 — `westlake-deploy-ohos/v3-hbc/` is untracked at HEAD

This in-flight V3 staging tree (`adapter-src/`, `app/`, `bcp/`, `bin/`, `docs/`, `etc/`, `jars/`, `lib/`, `patches/`, `scripts/`) was created by a previous agent but never committed. It's not part of any workstream's deliverable directly. Risk: if W1 creates `third_party/hbc-runtime/` as the canonical artifact tree, `westlake-deploy-ohos/v3-hbc/` becomes orphan-or-duplicate. Mitigation: W1 owner should reconcile (either rename to `third_party/hbc-runtime/`, or delete as superseded scaffolding, or formalize as a deploy-staging tree).

**Action item:** Flag for W1 owner — described in §1.2 row "westlake-deploy-ohos/v3-hbc/".

### R5 — `ResourceArscParser.java` cross-callers (gray area for ARCHIVES)

If `ResourceArscParser` is referenced from Phase-1 Android code paths (not just OHOS-substrate), archiving it would break the Android phone build. Mitigation: §3.7 grep-first command. **Action item:** W13 owner runs §3.7 before moving the file.

### R6 — Two `westlake-host/` directories at repo root

My `ls /home/dspfac/android-to-openharmony-migration` showed `westlake-host` twice plus `westlake-host-gradle` once. Either a typo in `ls` output, an untracked duplicate, or a tracked-and-untracked pair. **Action item:** W13 owner inspects with `ls -la` and dedupes before archiving anything adjacent.

### R7 — Dual-target sources in `shim/java/`

The `shim/java/` tree compiles into both `aosp-shim.dex` (Android-phone) and `aosp-shim-ohos.dex` (V2 OHOS, archiving). The sources STAY because Phase-1 needs them, but the **build target** for OHOS is gone. W13 must NOT delete sources to be careful; only `aosp-shim-ohos.dex` and its build script archive. Risk: a future cleanup pass mistakes `shim/java/com/westlake/services/Westlake{ActivityManager,Window,etc.}Service.java` as "V2-OHOS substrate" and deletes them, breaking Phase-1.

**Action item:** W13 README under `archive/v2-ohos-substrate/` should explicitly note: "Westlake-shadowed framework classes under `shim/java/` are PHASE-1-LOAD-BEARING and have NOT been archived; only the V2-OHOS build product (`aosp-shim-ohos.dex`) is archived."

### R8 — V3-WORKSTREAMS filename mismatch

V3-WORKSTREAMS §W11 acceptance says deliverable is `docs/engine/V3-V2-CARRYFORWARD-AUDIT.md`. My task brief says `docs/engine/V3-W11-CARRYFORWARD-AUDIT.md`. I've used the latter (task-brief authoritative for the doc location); the W10 owner should update V3-WORKSTREAMS §W11 to point at this filename.

### R9 — MEMORY.md `WestlakeLauncher.java` LOC stale

MEMORY.md claims WestlakeLauncher is 12,403 LOC; today it's 22,744 (1.8×). The file has grown post-2026-05-14. W10's memory refresh should either update the count or drop the count claim (LOC counts in memory tend to bit-rot).

---

## §6. W11 acceptance self-audit (from `V3-WORKSTREAMS.md` §W11)

- [x] Every V2 pillar named in MEMORY.md "BOTH apps in-process" section is in the table (§2: hidden-API bypass, LoadedApk dir redirect, safe-context bind stub, LocaleManager binder hook, lifecycle drive — all 5 covered)
- [x] Each pillar/subsystem assigned a disposition (§2)
- [x] Covers `westlake-host-gradle` (PHONE-ONLY, but informs V3-W3 launch wrapper)
- [x] Covers V2 substrate classes (PHONE-ONLY for V3; OHOS build product superseded by real `framework.jar`)
- [x] Covers V2 audio + surface daemons (Android-phone STAYS; OHOS targets ARCHIVES)
- [x] CR59 lifecycle drive lesson covered (§4 carryforward; §5 R1 risk)
- [x] Macro-shim contract covered (§1.3 STAYS with narrower scope; §4 carryforward)
- [x] Archive plan has concrete `git mv` commands (§3)
- [x] Carryforward section is concrete (§4 with V3-workstream mapping per row)
- [x] Risks section identifies CR61.1 follow-up (R1 Hilt; R2 V3 regression suite; R3 HBC stability)
- [x] Cross-references back to V3-WORKSTREAMS.md W11 acceptance (this section)
- [ ] Local commit with descriptive message (next step after writing this doc)

---

## §7. Self-audit (W11 task brief)

- [x] All major components categorized (51 surveyed)
- [x] Archive plan has specific git mv commands (§3)
- [x] Carryforward section concrete (§4 — every row has a V3-workstream mapping)
- [x] Risks section identifies anything that needs CR61.1 follow-up (§5 R1-R9)
- [ ] Local commit (next)
- [x] Cross-references back to V3-WORKSTREAMS.md W11 acceptance (§6)

---

## Cross-references

- `V3-ARCHITECTURE.md` §3 ownership table, §4 deletion catalog, §6 migration path, §7 macro-shim V3 scope, §8 anti-patterns, §9 self-audit checklist
- `V3-WORKSTREAMS.md` §W3 (OhosMvpLauncher archive), §W11 (this doc's brief), §W12 (CR61.1 disposition), §W13 (archive plan execution)
- `V3-SUPERVISION-PLAN.md` §1 DAG, §3 parallelizability, §7 risks (R1-R3 in this doc cite the supervision plan's risks)
- `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` §3 amendment matrix (which CR61 decisions stay vs amend per path)
- `CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md` (HBC overview)
- `CR-FF-HBC-BORROWABLE-PATTERNS.md` (HBC borrowable patterns)
- `BINDER_PIVOT_ARCHITECTURE.md` (V2 consolidated, Phase-1 STAYS authoritative)
- `BINDER_PIVOT_MILESTONES.md` (binder-pivot regression script reference)
- `CR41_PHASE2_OHOS_ROADMAP.md` (V2-OHOS roadmap — SUPERSEDED by V3)
- Memory: `project_v2_ohos_direction.md` (SUPERSEDED — W10 banner), `project_binder_pivot.md`, `project_noice_inprocess_breakthrough.md`, `project_real_framework_jar_arch.md`, `feedback_macro_shim_contract.md`, `feedback_additive_shim_vs_architectural_pivot.md`, `feedback_no_per_app_hacks.md`, `feedback_subtraction_not_addition.md`, `feedback_bitness_as_parameter.md`
- `CR59_REPORT.md` (Hilt fix — Phase-1; possible V3 re-occurrence per §5 R1)
- `CR60_BITNESS_PIVOT_DECISION.md` (32-bit prerequisite)
- `CR61_BINDER_STRATEGY_POST_CR60.md` (amended by CR61.1)
- `M7_STEP2_REPORT.md`, `CR62_REPORT.md`, `CR63_REPORT.md`, `CR64_REPORT.md`, `CR65_*`, `CR66_*`, `CR-W`/`CR-X`/`CR-Y`/`CR-Z`/`CR-AA` (V2-OHOS CR chain — all conceptually ARCHIVES)

---

**End of audit.**
