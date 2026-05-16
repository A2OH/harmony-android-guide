# V3 Workstreams — W1 through W13

**Date:** 2026-05-16
**Author:** agent 42
**Status:** AUTHORITATIVE for V3 OHOS path
**Companion:** `V3-ARCHITECTURE.md`, `V3-SUPERVISION-PLAN.md`, `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md`

---

## 0. Master tracking table

| W | Title | GitHub Issue | Effort (PD) | Depends on |
|---|---|---|---|---|
| W1 | HBC artifact inventory + pull | [#626](https://github.com/A2OH/westlake/issues/626) | 2-3 | — |
| W2 | Boot HBC runtime standalone on DAYU200 | [#627](https://github.com/A2OH/westlake/issues/627) | 3-5 | W1 |
| W3 | Replace OhosMvpLauncher with appspawn-x integration | [#628](https://github.com/A2OH/westlake/issues/628) | 3-4 | W1, W2 |
| W4 | Adapter customization for Westlake scope | [#629](https://github.com/A2OH/westlake/issues/629) | 5-8 | W2, W3 |
| W5 | Mock APK validation (non-Hilt simple APK through V3) | [#630](https://github.com/A2OH/westlake/issues/630) | 2-3 | W3 |
| W6 | noice on OHOS via V3 | [#631](https://github.com/A2OH/westlake/issues/631) | 5-8 | W4, W5 |
| W7 | McD on OHOS via V3 | [#632](https://github.com/A2OH/westlake/issues/632) | 4-6 | W4, W5, W6 |
| W8 | SceneBoard bring-up (board config) — independent of V3 | [#633](https://github.com/A2OH/westlake/issues/633) | 5-10 | (none on V3) |
| W9 | Borrow HBC Tier-1 patterns (ScopedJniAttach, DEPLOY_SOP, restore script) | [#634](https://github.com/A2OH/westlake/issues/634) | 2-3 | W1 |
| W10 | Memory + handoff doc refresh for V3 | [#635](https://github.com/A2OH/westlake/issues/635) | 1 | W1 |
| W11 | Audit V2 Android-phone path: what carries forward, what's V3-specific | [#636](https://github.com/A2OH/westlake/issues/636) | 1-2 | — |
| W12 | CR61.1 amendment + downstream code disposition | [#637](https://github.com/A2OH/westlake/issues/637) | 1 | — (this CR landed today) |
| W13 | Migration plan: archive dalvik-kitkat OHOS work | [#638](https://github.com/A2OH/westlake/issues/638) | 1-2 | W1, W3 |

**Total estimated effort:** 35-55 person-days across W1-W13. W8 (SceneBoard) is independent and may run in parallel — see `V3-SUPERVISION-PLAN.md` for the dependency DAG.

**GitHub issues:** all 13 opened in `A2OH/westlake` (#626-#638) on 2026-05-16 by agent 42.

---

## W1 — HBC artifact inventory + pull

### Goal

Pull every HBC binary artifact + adapter source listed in `V3-ARCHITECTURE.md` §5.1-5.2 into the Westlake repo at a stable path (`third_party/hbc-runtime/`) with provenance manifest and md5 checksums. **Read-only on HBC's side** — never modify `~/adapter/` on GZ05.

### Acceptance criteria

- [ ] `third_party/hbc-runtime/aosp_lib/` contains all 38 cross-built native .so (`libart.so`, `libhwui.so`, `libandroid_runtime.so`, `libandroidfw.so`, `libminikin.so`, `libnativehelper.so`, `libicuuc.so`, `libft2.so`, `libopenjdk{,jvm}.so`, `libbase.so`, `libcutils.so`, `libutils.so`, `libdexfile.so`, `libsigchain.so`, `libnativeloader.so`, `libnativebridge.so`, `libprofile.so`, `libelffile.so`, `libartbase.so`, `libart-compiler.so`, `libbionic_compat.so`, and the other 16 .so listed in CR-EE App. B)
- [ ] `third_party/hbc-runtime/aosp_fwk/` contains all 11 jars (`framework.jar`, `core-oj.jar`, `core-libart.jar`, `core-icu4j.jar`, `okhttp.jar`, `bouncycastle.jar`, `apache-xml.jar`, `framework-res.apk`, `oh-adapter-framework.jar`, `oh-adapter-runtime.jar`, `adapter-mainline-stubs.jar`)
- [ ] `third_party/hbc-runtime/boot-image/` contains all 27 boot-image files (9 segments × art/oat/vdex)
- [ ] `third_party/hbc-runtime/adapter/` contains `appspawn-x`, `liboh_adapter_bridge.so`, `liboh_android_runtime.so`, `liboh_hwui_shim.so`, `liboh_skia_rtti_shim.so`, `libapk_installer.so`
- [ ] `third_party/hbc-runtime/oh-service/` contains all 7 patched OH service .so
- [ ] `third_party/hbc-runtime/adapter-source/` contains the HBC adapter Java + JNI sources (`~/adapter/framework/`) for reference reading — NOT to be edited; we only consume the built artifacts
- [ ] `third_party/hbc-runtime/MANIFEST.md` lists every file with md5, source path on GZ05, pull timestamp, and HBC commit/version identifier
- [ ] `third_party/hbc-runtime/PROVENANCE.md` documents the read-only pull procedure so it's reproducible
- [ ] No edits to any file under `third_party/hbc-runtime/` after pull — verified by `git log --stat third_party/hbc-runtime/` showing only the import commit

### Dependencies

None. W1 is the gate for W2-W7, W9, W10, W13.

### Files / artifacts produced

- `third_party/hbc-runtime/` directory tree (binary + source)
- `third_party/hbc-runtime/MANIFEST.md`
- `third_party/hbc-runtime/PROVENANCE.md`
- `scripts/pull-hbc-artifacts.sh` (one-shot pull script using `scp` / `rsync` read-only against GZ05)

### Self-audit gate

```bash
# Every file accounted for in MANIFEST
find third_party/hbc-runtime -type f | grep -v MANIFEST | grep -v PROVENANCE | sort > /tmp/files.txt
awk '/^- / { print $2 }' third_party/hbc-runtime/MANIFEST.md | sort > /tmp/manifest.txt
diff /tmp/files.txt /tmp/manifest.txt && echo "OK: every file manifested"

# md5 checksums verify
( cd third_party/hbc-runtime && md5sum -c MANIFEST.md.md5sums ) | grep -v ": OK$" | head && echo "Above are FAIL"

# No accidental edits since import
git log --oneline -- third_party/hbc-runtime/ | wc -l   # should equal 1 (the import commit)
```

---

## W2 — Boot HBC runtime standalone on DAYU200

### Goal

Get HBC's stack (ART + framework.jar + appspawn-x + their HelloWorld.apk) running on DAYU200 with **no Westlake code involved**. This proves the HBC artifact pull was complete and the deploy procedure works for us. Smoke-tests their stack on its own.

### Acceptance criteria

- [ ] DAYU200 reflashed clean OHOS 7.0.0.18 Beta1 (rk3568, 32-bit ARM userspace) — verified via `hdc shell getconf LONG_BIT` returning `32` and `hdc shell file /system/bin/sh` showing `32-bit LSB arm`
- [ ] HBC artifacts deployed to device via Westlake's port of `DEPLOY_SOP.md` (W9 produces this script). All 94 files + 4 symlinks land in the right places with correct SELinux labels
- [ ] `pidof appspawn-x` returns a PID after boot
- [ ] `hdc shell aa start com.example.helloworld` (HBC's HelloWorld.apk preinstalled via `bm install`) reaches `MainActivity.onCreate` (verified by `hilog | grep MainActivity` or HBC's own log markers)
- [ ] Reproduces HBC's "MainActivity.onCreate line 83 (TextView ctor)" milestone (CR-EE §8) on our copy of the board
- [ ] Documented `artifacts/v3/w2-hbc-standalone/` checkpoint with logcat / hilog dumps and screenshot
- [ ] No Westlake-owned code involved at all — `find /system/ -name 'libwestlake*' -o -name 'aosp-shim-ohos*'` returns empty on device

### Dependencies

W1 (artifacts must be pulled). W9 partial (the DEPLOY_SOP script — can be hand-run for W2 and codified into a script in W9).

### Files / artifacts produced

- `artifacts/v3/w2-hbc-standalone/CHECKPOINT.md` + dumps
- `scripts/deploy-hbc-to-dayu200.sh` (port of HBC's deploy_to_dayu200.sh)
- `scripts/restore-v3-state.sh` (single-command recovery — CR-EE §9 "一键恢复规则")

### Self-audit gate

```bash
# Verify pure HBC, no Westlake bleed-in
hdc shell ls /system/ | grep -i westlake && echo "FAIL: Westlake bleed" || echo "OK"
hdc shell ls /system/android/ | head -20  # HBC artifact tree should exist
hdc shell "aa start com.example.helloworld" && hdc shell "sleep 3 && hilog | grep -c 'MainActivity.onCreate'"
# Expect non-zero hit count
```

---

## W3 — Replace OhosMvpLauncher with appspawn-x integration

### Goal

Replace Westlake's V2-OHOS `OhosMvpLauncher` (and surrounding launch infrastructure) with the HBC `aa start <bundle>` → AMS → AbilitySchedulerAdapter → ActivityThread path. After W3, Westlake's OHOS-target launchers are entirely replaced by HBC's launch model. No more `OhosMvpLauncher`. No more bespoke driver.

### Acceptance criteria

- [ ] `OhosMvpLauncher` source files moved to `archive/v2-ohos-substrate/launchers/` with a top-level `README.md` pointing to V3-ARCHITECTURE §4
- [ ] All call sites that depended on `OhosMvpLauncher` either removed (OHOS path) or marked Android-phone-only (V2 path)
- [ ] HBC's launch model documented in `docs/engine/V3-LAUNCH-MODEL.md` — short doc covering `aa start <bundle>` → AppMgrService → appspawn-x socket → fork → child runs ActivityThread.main()
- [ ] Westlake's launcher driver wrapper (if any) reduced to a thin shell that just calls `hdc shell aa start ...`
- [ ] HelloWorld.apk launches via this path on DAYU200, reproducing W2 result
- [ ] Mock APK from W5 launches via this path (forward-link; W3 acceptance can be partial pending W5)
- [ ] Macro-shim contract self-audit clean: no Unsafe / setAccessible / per-app branches in any new code

### Dependencies

W1, W2.

### Files / artifacts produced

- `archive/v2-ohos-substrate/launchers/` (relocated code)
- `docs/engine/V3-LAUNCH-MODEL.md`
- thin shell driver (if any) — replaces ~600 LOC of `OhosMvpLauncher`

### Self-audit gate

```bash
# No Westlake-owned launcher code on OHOS target path
find android-to-openharmony-migration -name '*.java' -o -name '*.kt' -o -name '*.cpp' \
  | xargs grep -l "OhosMvpLauncher\|InProcessAppLauncher.*ohos" \
  | grep -v "archive/" | grep -v "docs/" \
  && echo "FAIL: live references to OhosMvpLauncher" || echo "OK"
```

---

## W4 — Adapter customization for Westlake scope

### Goal

Identify the specific places where Westlake's service scope differs from HBC's HelloWorld scope, and either (a) consume HBC's adapter as-is (preferred), (b) shadow specific adapter classes in our own `oh-adapter-runtime.jar` (per CR-FF Pattern 2, PathClassLoader-loaded), or (c) raise upstream change requests to HBC. **Zero edits to HBC adapter source.**

Examples of likely Westlake-scope differences:
- Intent rewriting for cross-package launches (existing Java-side Instrumentation subclass — see `project_noice_inprocess_breakthrough.md`)
- Hilt-aware lifecycle behaviors (noice uses Hilt; HBC HelloWorld doesn't)
- Multi-app coordination (HBC tests one app at a time; Westlake hosts several)
- Per-app constants table integration (each app's 4 constants need a feed-point)

### Acceptance criteria

- [ ] Diff catalog: `docs/engine/V3-WESTLAKE-SCOPE-DIFFS.md` enumerates every Westlake-scope behavior that HBC's adapter doesn't already do, with disposition (consume-as-is / shadow-class / upstream-request)
- [ ] Every "shadow-class" disposition has a Westlake-owned class in `oh-adapter-runtime-westlake.jar` (separate jar so we can pull HBC's adapter-runtime jar verbatim alongside)
- [ ] Every "upstream-request" disposition has an issue filed in HBC's tracker (or, if HBC has no public tracker, in our `A2OH/westlake` repo with `hbc-upstream` label) with concrete request text
- [ ] Zero edits to `third_party/hbc-runtime/adapter-source/` — verified by `git status`
- [ ] Macro-shim contract self-audit clean across the shadow classes (no Unsafe / setAccessible / per-app)

### Dependencies

W2 (need HBC running to know which adapters Westlake actually exercises), W3 (need launch model in place).

### Files / artifacts produced

- `docs/engine/V3-WESTLAKE-SCOPE-DIFFS.md`
- `oh-adapter-runtime-westlake.jar` source tree (Westlake-owned, PathClassLoader-loaded)
- Upstream-request issues filed where applicable

### Self-audit gate

```bash
# No HBC source edits
git diff --name-only HEAD~5 | grep -E "^third_party/hbc-runtime/adapter-source/" && echo "FAIL" || echo "OK"
# Macro contract on Westlake shadow classes
grep -rn "Unsafe.allocateInstance\|setAccessible(true)" oh-adapter-runtime-westlake/ && echo "FAIL" || echo "OK"
grep -rniE "noice|mcdonalds|com\.mcd" oh-adapter-runtime-westlake/ | grep -v "^.*://\|^.*// " && echo "FAIL: per-app branch" || echo "OK"
```

---

## W5 — Mock APK validation

### Goal

Build a small non-Hilt single-Activity APK and run it through the V3 stack end-to-end. Validates that V3 works for a generic Android app before tackling noice (Hilt) or McD (deep cross-package).

### Acceptance criteria

- [ ] Mock APK: single Activity, one TextView, one Button toggling text — pure stock Android, builds on local Android Studio (not on GZ05)
- [ ] APK transparency invariant: source contains zero `import adapter.*` and zero `import com.westlake.*`
- [ ] `hdc shell aa start com.westlake.mockapk` launches the mock; Activity reaches onResume; pixel renders on screen (verified by `hdc shell snapshot_display`)
- [ ] Button click → text toggle → re-render confirmed (smoke for touch input + invalidate path)
- [ ] Lifecycle: backgrounding via `aa start com.example.helloworld` (HBC HelloWorld) and re-foregrounding restores state cleanly
- [ ] Logcat / hilog dump captured under `artifacts/v3/w5-mock-apk/`
- [ ] No Westlake-owned framework class shows up in stack traces — only `android.*`, HBC adapter, and our Westlake engine entry points

### Dependencies

W3 (launch path in place).

### Files / artifacts produced

- `mock-apks/v3-smoke/` — APK source tree
- `artifacts/v3/w5-mock-apk/CHECKPOINT.md` + dumps + screenshot

### Self-audit gate

```bash
# APK transparency
grep -rE "import (adapter\.|com\.westlake\.)" mock-apks/v3-smoke/ && echo "FAIL" || echo "OK"
# Visible pixel
hdc shell snapshot_display | file - | grep -i png && echo "OK: pixel captured"
```

---

## W6 — noice on OHOS via V3

### Goal

Run unmodified `com.github.ashutoshgngwr.noice` APK through the V3 stack. Reproduces the V2 in-process Option 3 result (`project_noice_inprocess_breakthrough.md`) on OHOS instead of Android phone. Validates that V3 handles a real Hilt + multi-fragment NavController app.

### Acceptance criteria

- [ ] noice APK launches via `aa start ai.noice.app` (or appropriate bundle name)
- [ ] Welcome screen renders (real pixels, captured screenshot)
- [ ] Library / Favorites / Profile bottom-nav tabs all reachable
- [ ] At least one Fragment transition confirmed in logcat
- [ ] No SoftwareCanvas / drm_inproc_bridge in the stack — real libhwui RenderThread is the render path (verified via `pmap <pid>` showing `libhwui.so` mapped)
- [ ] Per-app constants table integration: noice has its 4-constant entry; zero per-app code branches anywhere in Westlake code (grep clean)
- [ ] V3 self-audit checklist (V3-ARCHITECTURE §9) all green
- [ ] Side-by-side comparison artifact `artifacts/v3/w6-noice/` showing V2-on-phone vs V3-on-DAYU200 screenshots

### Dependencies

W4 (adapter scope diffs done), W5 (mock APK proves V3 stack works generically).

### Files / artifacts produced

- noice per-app constants entry (4 lines added to constants table)
- `artifacts/v3/w6-noice/` checkpoint + screenshots
- Any newly-discovered shadow classes added to `oh-adapter-runtime-westlake.jar`

### Self-audit gate

```bash
# Real hwui in noice process
hdc shell "pmap $(pidof ai.noice.app | head -1)" | grep -E "libhwui|libart" | head -5 && echo "OK"
# No SoftwareCanvas / drm_inproc_bridge
hdc shell "pmap $(pidof ai.noice.app | head -1)" | grep -iE "softwarecanvas|drm_inproc" && echo "FAIL" || echo "OK"
# No per-app branches in V3 engine
grep -rniE "noice|ai\.noice" android-to-openharmony-migration/{westlake-host-gradle,oh-adapter-runtime-westlake} \
  --include='*.java' --include='*.kt' --include='*.cpp' \
  | grep -v "constants table\|^.*://\|^.*// " | head
```

---

## W7 — McD on OHOS via V3

### Goal

Run unmodified McDonald's app through the V3 stack. McD has deep cross-package intents (the Java-side Instrumentation rewriting open item — see `project_noice_inprocess_breakthrough.md`) and is the harder target.

### Acceptance criteria

- [ ] McD APK launches via `aa start com.mcdonalds.app`
- [ ] SplashActivity renders → Wi-Fry McD-branded offline screen reached
- [ ] Cross-package intent rewriting: when McD launches a sibling activity in a different package, Westlake's Instrumentation subclass rewrites the intent and the launch succeeds inside the same HBC child process (or, if architecturally required, a sibling child)
- [ ] Per-app constants: McD's 4-constant entry only; zero per-app code branches anywhere
- [ ] V3 self-audit checklist all green
- [ ] Side-by-side artifact `artifacts/v3/w7-mcd/` showing V2-on-phone vs V3-on-DAYU200

### Dependencies

W4, W5, W6 (lessons from noice carry forward).

### Files / artifacts produced

- McD per-app constants entry
- `artifacts/v3/w7-mcd/` checkpoint + screenshots
- Instrumentation subclass for cross-pkg rewriting (Westlake-owned)

### Self-audit gate

(same shape as W6)

---

## W8 — SceneBoard bring-up (board config; independent of V3)

### Goal

Address the DAYU200 SceneBoard-disabled finding (`artifacts/ohos-mvp/multi-hap-peer-window-spike/20260515_181930/CHECKPOINT.md`): on stock OHOS 7.0.0.18 Beta1 the freeform-window / multi-window / peer-window UX is gated off (`sceneboard.config = DISABLED`, `aa start --ww/--wh` returns error 10106107). For Westlake's product goal ("Android-as-OHOS-citizen alongside ArkTS apps via WindowManager"), SceneBoard must be enabled.

This workstream is **independent of V3** — it's a board-config item that both V2-OHOS and V3 would have inherited. Listed here for completeness.

### Acceptance criteria

- [ ] Decision: enable SceneBoard on DAYU200, or target a different OHOS device profile (PC / 2-in-1) where SceneBoard ships enabled — documented in `docs/engine/W8-SCENEBOARD-DECISION.md`
- [ ] If "enable on DAYU200": product-config / build-args change identified (`window_manager_use_sceneboard=true` and downstream deps), reflashed image validated
- [ ] If "different device": device acquired, V3 stack reflashed there, freeform-window UX verified
- [ ] `aa start --ww 600 --wh 800` succeeds (or equivalent freeform-window invocation)
- [ ] Two HAPs simultaneously visible on screen (the peer-window goal)
- [ ] Two V3-hosted Android apps simultaneously visible (Westlake's actual product goal)

### Dependencies

None on V3. Can run fully in parallel.

### Files / artifacts produced

- `docs/engine/W8-SCENEBOARD-DECISION.md`
- `artifacts/v3/w8-sceneboard/` proof artifacts

### Self-audit gate

```bash
hdc shell "cat /system/etc/sceneboard.config" | grep -i enabled && echo "OK: SCB on" || echo "Still DISABLED"
hdc shell "aa start --ww 600 --wh 800 -b ohos.samples.etsclock"  # should NOT return 10106107
```

---

## W9 — Borrow HBC Tier-1 patterns

### Goal

Port HBC's three highest-ROI tactical patterns (CR-FF Tier-1) into Westlake's tooling: `ScopedJniAttach` RAII, `DEPLOY_SOP.md` staging discipline, and `restore_after_sync.sh` recovery automation.

### Acceptance criteria

- [ ] `ScopedJniAttach.h` (copied or re-authored, both options OK) usable in any Westlake-owned JNI code; all new V3 JNI helpers use it
- [ ] `docs/engine/V3-DEPLOY-SOP.md` documents the staging discipline (never `hdc send <src> /system/...`; always `send → /data/local/tmp/stage/<basename>`, `ls -la` to verify, then `cp` into target). Source for this rule: `~/adapter/deploy/DEPLOY_SOP.md` (CR-EE §9).
- [ ] `scripts/deploy-hbc-to-dayu200.sh` (from W2) implements V3-DEPLOY-SOP.md
- [ ] `scripts/restore-v3-state.sh` reproduces last-green V3 state from a clean checkout in a single command (CR-EE §9 "一键恢复规则"). Tested.
- [ ] `docs/engine/V3-RCA-DISCIPLINE.md` documents "blame adapter first" — when something breaks at the integration seam, suspect (1) our adapter customization (W4 jar), (2) cross-compile flags, (3) HBC adapter, (4) AOSP, (5) OHOS — in that order. Source: HBC's `~/adapter/CLAUDE.md` §"Root Cause Analysis Discipline" (CR-EE §11.7).

### Dependencies

W1 (need HBC source available for reference reading).

### Files / artifacts produced

- `tools/jni-helpers/ScopedJniAttach.h`
- `docs/engine/V3-DEPLOY-SOP.md`
- `docs/engine/V3-RCA-DISCIPLINE.md`
- `scripts/restore-v3-state.sh`

### Self-audit gate

```bash
# New JNI helpers use ScopedJniAttach
git grep -l "JNIEnv\*\|AttachCurrentThread" -- '*.cpp' '*.c' \
  | xargs grep -L "ScopedJniAttach" \
  | xargs -I{} echo "FAIL: missing ScopedJniAttach in {}"
# restore-v3-state.sh is idempotent
bash scripts/restore-v3-state.sh && bash scripts/restore-v3-state.sh && echo "OK"
```

---

## W10 — Memory + handoff doc refresh for V3

### Goal

Update the agent memory + handoff system to point at V3 as the current OHOS direction, while preserving V2-OHOS memories as superseded (not deleted) per the project's preservation discipline.

### Acceptance criteria

- [ ] `/home/dspfac/.claude/projects/-home-dspfac-openharmony/memory/MEMORY.md` "START HERE" section updated: V3 docs listed; V2-OHOS direction noted as superseded with pointer to V3-ARCHITECTURE.md
- [ ] `/home/dspfac/.claude/projects/-home-dspfac-openharmony/memory/project_v2_ohos_direction.md` gets a header banner: "SUPERSEDED 2026-05-16 by `docs/engine/V3-ARCHITECTURE.md`. Kept verbatim for traceability." No content deleted.
- [ ] New memory: `project_v3_direction.md` — short pointer to V3-ARCHITECTURE.md + W1-W13 list + W1 status
- [ ] New handoff: `handoff_2026-05-16.md` — top-of-next-agent orientation reflecting V3 pivot, top-3 commands, current blocker (W1 in flight), 14/14 regression state, V2 phone-path unchanged
- [ ] `project_binder_pivot.md` updated to note CR61 amended by CR61.1 for V3 path (Android-phone V2 unchanged)
- [ ] Handoffs older than `handoff_2026-05-14.md` remain in place; new handoff replaces "START HERE" pointer

### Dependencies

V3-ARCHITECTURE.md landed (today).

### Files / artifacts produced

- updated `MEMORY.md`
- updated `project_v2_ohos_direction.md` (banner only, content preserved)
- new `project_v3_direction.md`
- new `handoff_2026-05-16.md`
- updated `project_binder_pivot.md` (CR61.1 reference)

### Self-audit gate

```bash
# V2 memory still readable, just marked superseded
head -5 /home/dspfac/.claude/projects/-home-dspfac-openharmony/memory/project_v2_ohos_direction.md | grep -i superseded
# V3 memory exists
test -f /home/dspfac/.claude/projects/-home-dspfac-openharmony/memory/project_v3_direction.md
# Handoff exists
test -f /home/dspfac/.claude/projects/-home-dspfac-openharmony/memory/handoff_2026-05-16.md
```

---

## W11 — Audit V2 Android-phone path: what carries forward, what's V3-specific

### Goal

Make explicit which V2 work transfers into V3 unchanged (the Android-phone path), which transfers with adaptation, and which is V3-specific. Driven by `project_noice_inprocess_breakthrough.md` "5-pillar pattern" (hidden-API bypass + LoadedApk dir redirect + safe-context bind stub + LocaleManager binder hook + lifecycle drive). Each pillar gets a disposition.

### Acceptance criteria

- [ ] `docs/engine/V3-V2-CARRYFORWARD-AUDIT.md` table: for each V2 pillar / engine subsystem, disposition is one of {PHONE-ONLY, V3-INHERITS-VERBATIM, V3-ADAPTS-AT-INTEGRATION-SEAM, V3-OBSOLETED}
- [ ] Specifically covers: V2 5-pillar pattern, `westlake-host-gradle` (PHONE-ONLY, but informs V3-W3 launch wrapper), V2 substrate classes (PHONE-ONLY for V3; superseded by real `framework.jar` on OHOS), V2 audio + surface daemons (PHONE-ONLY for V3), CR59 lifecycle drive lesson (V3-INHERITS — apply at HBC's `AbilitySchedulerBridge` Handler.post seam), macro-shim contract (V3-ADAPTS — narrower scope)
- [ ] Cross-linked from V3-ARCHITECTURE.md §6 migration path

### Dependencies

None (read-only audit).

### Files / artifacts produced

- `docs/engine/V3-V2-CARRYFORWARD-AUDIT.md`

### Self-audit gate

```bash
# Every V2 pillar named in MEMORY.md "BOTH apps in-process" section is in the table
for p in "hidden-API bypass" "LoadedApk dir redirect" "safe-context bind stub" "LocaleManager binder hook" "lifecycle drive"; do
  grep -q "$p" docs/engine/V3-V2-CARRYFORWARD-AUDIT.md || echo "MISSING: $p"
done
```

---

## W12 — CR61.1 amendment + downstream code disposition

### Goal

CR61.1 amendment is authored today (2026-05-16, `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md`). W12 ensures the downstream code disposition matches: archived OHOS-target binder / audio / surface builds; Android-phone V2 path verifiably untouched; self-audit greps clean.

### Acceptance criteria

- [ ] `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` committed (landed in same commit set as V3 docs)
- [ ] `CR61_BINDER_STRATEGY_POST_CR60.md` annotated with AMENDED-BY pointer to CR61.1
- [ ] `aosp-libbinder-port/out/arm32/` (if it exists as a Westlake-built OHOS arm32 target) moved to `archive/v2-ohos-substrate/aosp-libbinder-port-arm32/`. The musl + bionic builds stay in place (Android-phone V2).
- [ ] `aosp-{audio,surface}-daemon-port/` OHOS-target binaries similarly archived. Android-phone builds untouched.
- [ ] CR61.1 §6 self-audit gate passes (no `dlopen("libipc")` etc. from Westlake code; no HBC source edits; no `setenforce 0`)
- [ ] Android-phone V2 regression still 14/14 PASS (verified by running existing regression on cfb7c9e3 or equivalent)

### Dependencies

CR61.1 landed (today). W11 (to know what V2 paths are PHONE-ONLY vs OHOS-ONLY).

### Files / artifacts produced

- CR61.1 amendment file
- `archive/v2-ohos-substrate/` reorganization (move-only, no delete)
- Updated `CR61_BINDER_STRATEGY_POST_CR60.md` header note

### Self-audit gate

CR61.1 §6 self-audit (run as-is).

---

## W13 — Migration plan: archive dalvik-kitkat OHOS work

### Goal

Archive (not delete) the dalvik-kitkat OHOS port + associated build artifacts + V2-OHOS substrate work, with a clear top-level README pointing future readers to V3-ARCHITECTURE.md §4 for the disposition rationale.

### Acceptance criteria

- [ ] `archive/v2-ohos-substrate/README.md` lists every archived component (dalvik-port OHOS targets, aosp-shim-ohos.dex, SoftwareCanvas, drm_inproc_bridge.c, OhosMvpLauncher, M5/M6 OHOS targets) with reason ("V3 uses HBC's libart.so + appspawn-x") and a one-line cross-ref to V3-ARCHITECTURE.md §4 row
- [ ] Build system updated so V2-OHOS targets are no longer built by default; can still be built explicitly via `--target=v2-ohos-archive` flag for forensic / regression purposes
- [ ] `dalvik-port/build-ohos-*/` directories moved (not deleted) under `archive/v2-ohos-substrate/dalvik-port-ohos/`
- [ ] CR-DD recommendation (HYBRID with Candidate C MVP first) noted as superseded by V3 — V3 effectively chooses option (d), reuse HBC, which neither CR-DD nor CR-BB enumerated because HBC's existence wasn't known when those CRs were authored
- [ ] All git history preserved; archive is a directory move, not a `git rm`

### Dependencies

W1 (so we know HBC artifacts work), W3 (so launchers are replaced).

### Files / artifacts produced

- `archive/v2-ohos-substrate/README.md`
- moved subtrees under archive/
- updated build scripts

### Self-audit gate

```bash
# Default build does not touch V2-OHOS targets
make -n  2>&1 | grep -E "(dalvik-port/build-ohos|aosp-shim-ohos|SoftwareCanvas)" && echo "FAIL: V2-OHOS still in default build" || echo "OK"
# But archive is preserved (no deletion)
test -d archive/v2-ohos-substrate/dalvik-port-ohos && echo "OK: archived"
# Android-phone V2 regression still passes
bash scripts/run-v2-android-phone-regression.sh  # 14/14 PASS expected
```

---

## §15 — GitHub issue creation log

All 13 issues opened in `A2OH/westlake` on 2026-05-16 by agent 42:

| W | Issue | Title | Labels |
|---|---|---|---|
| W1 | [#626](https://github.com/A2OH/westlake/issues/626) | HBC artifact inventory + pull | v3, enhancement, runtime-substrate, blocker |
| W2 | [#627](https://github.com/A2OH/westlake/issues/627) | Boot HBC runtime standalone on DAYU200 | v3, enhancement, runtime-substrate, validation |
| W3 | [#628](https://github.com/A2OH/westlake/issues/628) | Replace OhosMvpLauncher with appspawn-x integration | v3, enhancement, app-hosting-engine |
| W4 | [#629](https://github.com/A2OH/westlake/issues/629) | Adapter customization for Westlake scope | v3, enhancement, app-hosting-engine |
| W5 | [#630](https://github.com/A2OH/westlake/issues/630) | Mock APK validation | v3, enhancement, validation |
| W6 | [#631](https://github.com/A2OH/westlake/issues/631) | noice on OHOS via V3 | v3, enhancement, app-hosting-engine, validation, real-apk |
| W7 | [#632](https://github.com/A2OH/westlake/issues/632) | McD on OHOS via V3 | v3, enhancement, app-hosting-engine, validation, mcdonald |
| W8 | [#633](https://github.com/A2OH/westlake/issues/633) | SceneBoard bring-up | v3, enhancement, board-config |
| W9 | [#634](https://github.com/A2OH/westlake/issues/634) | Borrow HBC Tier-1 patterns | v3, enhancement, runtime-substrate, docs |
| W10 | [#635](https://github.com/A2OH/westlake/issues/635) | Memory + handoff doc refresh for V3 | v3, enhancement, docs |
| W11 | [#636](https://github.com/A2OH/westlake/issues/636) | V2 Android-phone path carryforward audit | v3, enhancement, docs |
| W12 | [#637](https://github.com/A2OH/westlake/issues/637) | CR61.1 amendment + downstream code disposition | v3, enhancement, docs, runtime-substrate |
| W13 | [#638](https://github.com/A2OH/westlake/issues/638) | Archive dalvik-kitkat OHOS work as superseded | v3, enhancement, docs |

Labels created during W12 work: `v3`, `runtime-substrate`, `app-hosting-engine`, `validation`, `board-config`, `docs`.

Each issue body is self-contained: goal paragraph + dependencies (cross-linked to dependent issue numbers) + acceptance criteria checklist + effort estimate. Bodies link back to this doc (`V3-WORKSTREAMS.md#W<N>`) for the canonical spec.
