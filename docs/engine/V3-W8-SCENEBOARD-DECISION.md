# V3 W8: SceneBoard Bring-up Decision

**Date:** 2026-05-16
**Author:** agent 46
**Status:** RECOMMENDATION (decision to be ratified by Yue)
**GitHub issue:** [A2OH/westlake#633](https://github.com/A2OH/westlake/issues/633)
**Companion:** `V3-ARCHITECTURE.md`, `V3-SUPERVISION-PLAN.md` §0/§5/§7, `V3-WORKSTREAMS.md` §W8

---

## 1. Strategic context

### What the spike found

The 2026-05-15 multi-HAP peer-window spike
(`artifacts/ohos-mvp/multi-hap-peer-window-spike/20260515_181930/CHECKPOINT.md`)
established three hard facts about our actual MVP target board:

1. `/system/etc/sceneboard.config` on DAYU200 OHOS 7.0.0.18 Beta1 = **`DISABLED`**.
   The judgement code (`foundation/window/window_manager/window_scene/interfaces/innerkits/src/scene_board_judgement.cpp:46`)
   only returns true when the first line of that file is `ENABLED`.
2. Active shell is **legacy `com.ohos.launcher` + `com.ohos.systemui`**
   (pids 1515 + 1480 in the spike capture). Not `com.ohos.sceneboard`.
3. `aa start --wl --wt --ww --wh` returns
   **`Error 10106107: The current device does not support using window options`**.
   The runtime authority that gates windowOptions reads its decision from
   `sceneboard.config` + the legacy WMS path, both of which veto it on this board.

Consequence: on stock DAYU200, **only one Westlake app can be on-screen at a
time**. Sequential `aa start` of two HAPs transitions the first to
`#BACKGROUND` and gives the second the full screen.

### What this means for our two paths

- **V3 (HBC-substrate) path** — V3's app-hosting engine sits *under* OH's
  WindowManager. V3 doesn't change which shell is the WindowManager client.
  V3 on DAYU200 = single-Android-app-foreground, same as V2-OHOS would have been.
- **V2-OHOS substrate path (archived)** — would have inherited the same wall.
- **HBC's working solution** — explicitly tuned to legacy WMS. CR-EE §4
  records:
  > `window_manager_use_sceneboard = false` — DAYU200 runs legacy WMS,
  > not SceneBoard. … HBC's `WindowManagerAdapter` talks to `IWindowManager(OH)`
  > (legacy) via `CreateWindow / AddWindow / SetWindowId`,
  > not to `ISceneSessionManager`.
  HBC accepts the limit. Their adapter would need a second window-bridge
  family ("SCB chain") to function once SCB is on. CR-EE §11.5 explicitly
  flags this as a strategic risk:
  > Building Westlake onto legacy is shipping on a dead-end OH variant.

So the SceneBoard problem is **independent of substrate choice**. It is a
**device-/board-/product-profile problem**. V3 supervision plan §5 G8 codifies
this:
> G7 (V2-OHOS archived) is V3-internal. **G8 (peer-window UX) is the Westlake
> product gate; it's intentionally decoupled from V3 because solving it is a
> board-config problem, not a runtime-substrate problem.**

### What's gated on resolving this

The Westlake product north-star is **"Android-as-OHOS-citizen alongside
ArkTS apps via WindowManager"**. The `alongside` clause requires two windows
visible at the same time. Today on DAYU200 we cannot deliver `alongside` —
only `instead-of`. That's a step backwards from Android's stock behaviour
and not a defensible product position.

Multi-HAP-as-peer (the CR-DD §4 claim) does not rescue us — see spike
verdict FAIL. So the only way to deliver `alongside` is to either (A) turn
SceneBoard on under us, (B) move under a device that already has it on, or
(C) descope `alongside` from the MVP and ship `instead-of`.

---

## 2. Option A — Enable SceneBoard on DAYU200

### Feasibility

**Technically tractable, but a non-trivial system-image rebuild + multi-component
co-flip, not a one-line config tweak.** Source-tree evidence:

1. **GN build flag:** `foundation/window/window_manager/scene_board_enable.gni`
   declares `scene_board_enabled = false` as the default. Many BUILD.gn files
   (`wmserver/BUILD.gn`, `sa_profile/BUILD.gn`, `window_scene/BUILD.gn`,
   `windowmanager_aafwk.gni`, `wm/test/unittest/BUILD.gn`,
   `window_scene/interfaces/kits/napi/BUILD.gn`, `dm_lite/BUILD.gn`,
   `previewer/BUILD.gn`, `interfaces/kits/napi/window_runtime/BUILD.gn`)
   branch on this flag. Flipping it requires a from-source rebuild of the
   window subsystem and replacement of multiple system .so + system SA
   profile files on the image.
2. **Runtime config file:** `/system/etc/sceneboard.config` must contain
   `ENABLED` on the first line. Our source tree does **not** contain a
   producer of this file (no `sceneboard.config` source under
   `foundation/window/`, `vendor/`, or `productdefine/`). On the device
   today the file ships with body `DISABLED`. The producer is either a
   downstream HiHope/Huawei-side artifact not present in our open-source
   checkout, or it must be added as a new vendor `ohos_prebuilt_etc` target.
3. **SceneBoard HAP source:** **NOT present in our source tree.**
   `find /home/dspfac/openharmony -type d -name 'sceneboard'` returns
   empty. The open-source OHOS we have ships `applications/standard/launcher`
   + `applications/standard/systemui` (the legacy pair, which is what
   actually runs on the board). `com.ohos.sceneboard` is referenced from
   `component.startup.extension.multiprocess.whitelist` and from
   `vendor/hihope/rk3568/security_config/sanitizer_check_list.gni`
   (`scene_board_ext` group), but the HAP binary itself appears to be a
   Huawei-closed artifact that is **not built from the OpenHarmony 7.0.0.18
   sources we have**. (CR-EE App. A confirms HBC operates from "OH weekly
   `weekly_20260302` = OH 7.0.0.18", same as us, and HBC also could not
   make SCB chain work on this build.)
4. **Vendor config gap:** `vendor/hihope/rk3568/config.json` is the
   product config we'd modify. Today it does not set
   `window_manager_use_sceneboard` or `scene_board_enabled`. Setting them
   alone is necessary but not sufficient — we also need the SceneBoard HAP
   binary and the `sceneboard.config = ENABLED` etc artifact and the
   matching `SystemCapability.WindowManager.WindowManager.MultiWindow`
   profile (currently `const.SystemCapability.WindowManager.WindowManager.Core`
   is the only WMS cap exposed per the spike's `window-params.txt`).
5. **Two parallel WMS implementations:** the source has both
   `wmserver/` (legacy) and `window_scene/session_manager_service/` (SCB).
   Building both is the standard configuration; flipping the runtime
   gate switches which is the active SA. Apps must then talk to the
   correct one. Per CR-EE §4, HBC's adapter binds the **legacy**
   interface only.

### Cost

**5-10 person-days** as already estimated in W8 (V3-WORKSTREAMS.md §0).
Concretely:

| Sub-task | PD |
|---|---|
| Locate / synthesize SceneBoard HAP binary (closed Huawei artifact gap) | 2-4 |
| Vendor config: set `scene_board_enabled = true`, add SCB SA profile, add `sceneboard.config = ENABLED` prebuilt etc | 1 |
| Full image rebuild + flash + bring-up debug | 1-2 |
| Validate `aa start --ww/--wh` accepts; two HAPs visible | 0.5 |
| Re-validate Westlake stack still works on flipped board (legacy WMS clients now broken if HBC adapter only knows legacy) | 1-2 |
| HBC adapter `SCB chain` work — building the missing second window bridge | (out of scope; HBC's problem; **but blocks G4-G7 from re-passing on the flipped board**) |

**Critical hidden cost:** **flipping SCB on invalidates HBC's working
solution.** HBC's `WindowManagerAdapter` only knows the legacy chain
(CR-EE §4). Once SCB is the active WMS, HBC apps will fail to register
their windows. Their analysis explicitly names this an open future-work
item, not a today-shipping capability. Until HBC builds the SCB chain
(or we patch their adapter ourselves, violating the W1-pull read-only
invariant), **V3 itself stops working on a SCB-enabled board**. Effective
total to ship "V3 + SCB + peer-window" on DAYU200 ≈ **15-25 PD** counting
the HBC-side window-bridge work.

### Risk

- **High:** invalidates the entire V3 dependency chain (W1→W7) that V3-SUPERVISION-PLAN.md
  is built around — V3's HBC consumption only works on legacy WMS.
- **High:** SceneBoard HAP source not in our checkout. We may need to
  borrow it from HiHope's downstream artifacts or rebuild it from a
  later OH release where it is open. Provenance and signing risk.
- **Medium:** Westlake fleet (multiple DAYU200 boards) all need reflash.
  Lose the "stock board" baseline that current `MEMORY.md` regression
  (14/14 PASS) implicitly assumes.
- **Medium:** HBC isn't ours (CR-EE risk #9). We can't dictate when HBC
  gets the SCB chain working. Coupling to that timeline = external
  schedule risk for V3.

### Verdict

**Not viable as a near-term MVP path.** Tightly couples V3 success to
HBC's not-yet-built SCB chain. Could be revisited in 2-3 quarters if HBC
publishes a SCB-chain adapter or if we decide to fork their adapter
(at the cost of W1's read-only invariant and W9's RCA discipline).

---

## 3. Option B — Target a newer OHOS device with SceneBoard default-on

### Feasibility

**Plausibly correct strategically; requires hardware acquisition + investigation.**

What we know from the source tree:

- **`vendor/hihope/dayu210/` exists** (config.json present). This is the
  **HiHope DAYU210 / rk3588 board**, `target_cpu = arm64`, `type = standard`,
  `api_version 8`. Newer SoC than rk3568 (3568 → 3588 is a generational
  step). **Not validated** by us whether dayu210 ships with SCB on.
  We have a corresponding `vendor/hihope/dayu210/window_config/window_manager_config.xml`
  but its `<defaultWindowMode>` was not read in this investigation (the
  configuration of the physical board is what counts, not just the
  source-tree config).
- **`vendor/unionman/unionpi_tiger/`** is another vendor config in the
  tree — UnionMan UnionPi Tiger (an A311D board). Unknown SCB status.
- **`product.devicetype = default`** on our DAYU200 (per spike's
  `form-factor-params.txt:31`). Phone / 2-in-1 / PC profile gates many
  things in OH, including default multi-window mode. SCB ships **on by
  default** in **2in1 / PC / tablet** OH product profiles per
  CR-EE §11.5's "OH's strategic direction is SceneBoard" + the V3 supervision
  plan §7 risk #5 ("if it identifies a different device profile as the
  right target").
- **OpenHarmony in-the-wild SCB-on devices** known publicly include:
  Huawei MatePad Pro lineage (HarmonyOS NEXT — but that's Huawei-OS, not
  open OHOS), some 2-in-1 PC reference boards from third-party vendors,
  and Huawei's own internal dev kits. **The cleanest known open-OHOS path
  is the rk3588 family with the `2in1` product profile**, which is what
  HiHope sells as DAYU210 with the right product config — but we have not
  confirmed the stock image ships SCB on.

### Cost

| Sub-task | PD |
|---|---|
| Procurement: 1-2 rk3588-class boards or PC-profile boards (DAYU210, MatePad-class dev board, or equivalent) | **out-of-scope-for-this-agent; needs Yue's input on dev board procurement.** Lead time typically 2-6 wks. |
| Hardware bring-up: flash open-OHOS 7.x onto new board; confirm `sceneboard.config = ENABLED` | 2-3 |
| Re-validate HBC stack on new board — `aa start` of HBC HelloWorld, ART boot, surface flow | 3-5 |
| Re-build V3 W1-W3 acceptance on new board (or accept that DAYU200 stays our HBC-standalone target and only W6/W7 + peer-window proof happens on dayu210/PC board) | 2-4 |
| Manage the bitness/profile split — DAYU200 = 32-bit ARM, rk3588 = 64-bit ARM. CR60 bitness pivot already requires we build both arches; on rk3588 we'd run 64-bit and abandon 32-bit work for that target | 1-2 |

**Total Westlake-side: 8-14 PD post-acquisition + 2-6 wks procurement
calendar time.** Significantly less than Option A's hidden HBC-coupling
cost, and the deliverable is *correct architecturally* (we end up on the
OH-strategic-direction substrate, not the OH-deprecated one).

### Risk

- **Medium:** procurement lead time and budget out of our hands; needs
  Yue's call.
- **Low-to-medium:** dayu210 / rk3588 may *also* ship SCB-disabled in
  current OH 7.0.0.18 vendor builds. Risk it's same problem on bigger
  silicon. Needs first-hand verification before committing.
- **Low:** HBC's adapter is rk3568-tuned (CR-EE §3 "weekly_20260302 product
  rk3568 32-bit ARM"). Porting HBC artifacts to rk3588 64-bit is non-zero
  but the CR60 bitness pivot work already covers most of this. Adapter
  patches are mostly arch-independent.
- **Strategic upside:** moving to rk3588 also unlocks 64-bit memory
  addressing, more memory, faster GPU. Westlake's product story improves.
- **Lose access to DAYU200 fleet:** *we don't actually lose it* — V3 W2
  (HBC standalone smoke) and Westlake's V2 Android-phone path continue
  to use DAYU200 as a regression baseline. Only the **peer-window MVP**
  moves to the new board.

### Verdict

**Strategically correct, but procurement-gated.** This is the right
long-term answer (matches OH's strategic direction; doesn't fork HBC's
adapter; doesn't lose the regression baseline). Cannot start this week.

---

## 4. Option C — Accept legacy WMS limit; ship single-fullscreen Westlake-app

### Feasibility

**Already done.** Today's MEMORY.md baseline (14/14 PASS) is single-app.
HBC's working solution is single-app. V3 W6/W7 acceptance criteria (per
V3-WORKSTREAMS.md §W6/§W7) are single-app reaching MainActivity.onCreate.

### Cost

**Zero new investment.** All current workstream effort already assumes
this constraint.

### Risk

- **High product risk:** "Android-as-OHOS-citizen alongside ArkTS apps"
  is the project's stated north-star. `alongside` is unfulfilled. We
  ship a `Westlake-Android-instead-of-OHOS` MVP, not `Westlake-Android-alongside-OHOS`.
- **Medium positioning risk:** evaluators / stakeholders comparing this
  to stock Android-on-OHOS demos (which can side-by-side) will see
  Westlake as a step backwards in UX.
- **Low technical risk:** zero new code paths. Existing 14/14 regression
  remains green. No new bugs introduced by *this* option.

### Verdict

**Right for the next 4-6 weeks while V3 W1-W7 brings the substrate live.**
Wrong as the long-term product position.

---

## 5. Recommendation

### Sequenced plan: C now + B as soon as procurement clears

**Phase 1 (now → +6 wks, MVP window):** **Option C.** Ship V3 W1-W7
single-foreground-app on DAYU200. This is what V3-SUPERVISION-PLAN.md
already commits to and what HBC has validated. G1-G7 stay on DAYU200.
G8 (peer-window) is **deferred**, not abandoned, and the deferral is
explicitly tracked in this doc + the W8 issue (#633).

**Phase 2 (parallel, starting now): Option B procurement workstream.**
File a procurement ask with Yue for **1-2 rk3588-class OHOS dev boards
(DAYU210 or equivalent), preferring a PC / 2-in-1 product profile**.
While waiting (2-6 wks), one agent investigates the open-OHOS rk3588
SCB-on status, dayu210 `window_manager_config.xml` `<defaultWindowMode>`,
and any open-OHOS reference device that *empirically* boots with
`sceneboard.config = ENABLED` out of the box. If such a device exists,
prefer it over enabling SCB ourselves.

**Phase 3 (target: +8-12 wks): land G8 on rk3588 board.**
Re-run V3 W2/W6 acceptance on the new board. If V3 stack ports cleanly,
launch two V3-hosted Android apps in freeform-window mode side-by-side.
That is the actual product-goal milestone.

**Option A is explicitly NOT recommended** because (a) it requires the
SceneBoard HAP binary which is not in our source tree, (b) it breaks
HBC's adapter (HBC adapter knows legacy WMS only; CR-EE §4), and (c) the
effort to flip + maintain it (15-25 PD effective with HBC-side window
bridge) exceeds Option B's effort once procurement closes. Option A
becomes attractive only in the failure case where Option B is blocked
by procurement and the product needs `alongside` before B's calendar
clears. In that contingency, this doc should be revisited.

### Why this is the right sequence

1. **Doesn't delay the MVP.** V3 W1-W7 critical path (22-34 PD; per
   V3-SUPERVISION-PLAN §1) proceeds untouched. No agent gets re-tasked
   off the V3 critical path to chase SCB.
2. **Doesn't burn engineering on a legacy substrate.** Option A would
   build Westlake atop a deprecated WMS *and* a fork of HBC's adapter
   for SCB. Option B keeps us atop OH's strategic substrate where HBC
   (eventually) lands their SCB chain and we benefit for free.
3. **Doesn't lose the regression baseline.** DAYU200 + 14/14 PASS stays
   the V2-Android-phone + V3-HBC-standalone-smoke baseline. Only the
   peer-window-MVP target moves.
4. **Honest about the gap.** We tell stakeholders today's MVP is
   single-app and the peer-window milestone is the +8-12 wk milestone,
   on a different board. CR-DD §4's contested claim is retracted.
5. **Aligns with HBC's open future work.** CR-EE §11.5 already names
   the SCB chain as HBC's known gap. We let HBC solve their half;
   we solve ours (different board, then port).

---

## 6. Acceptance criteria for chosen path (C now + B by week 8-12)

### Near-term (Phase 1: C)

- [ ] V3 W6 (noice via V3) closes on DAYU200, single-foreground-app:
      MainActivity.onCreate reached, UI visible.
- [ ] V3 W7 (McD via V3) closes on DAYU200, single-foreground-app:
      Wi-Fry offline screen visible.
- [ ] G8 (peer-window) **formally documented as deferred** in W8
      issue #633 with a reference back to this doc.
- [ ] This doc is referenced from MEMORY.md START HERE section under
      "Known limitations" so future agents do not re-investigate.

### Mid-term (Phase 2: procurement + research)

- [ ] Yue confirms / declines procurement of rk3588-class dev board.
      If declined, return to this doc and re-evaluate Option A.
- [ ] If procurement approved: device identified, ordered, ETA tracked
      in W8 issue.
- [ ] Investigation deliverable: **"open-OHOS device-profile survey"**
      research note documenting which dev boards / product profiles
      empirically ship `sceneboard.config = ENABLED` out of the box,
      to inform the procurement choice.

### Long-term (Phase 3: B execution)

- [ ] On the new board: `cat /system/etc/sceneboard.config` returns
      `ENABLED`.
- [ ] `aa start --ww 600 --wh 800 -b ohos.samples.etsclock` returns
      success (not 10106107).
- [ ] V3 W2 smoke re-passes on the new board.
- [ ] Two V3-hosted Android apps (e.g., noice + McD) visible
      simultaneously in freeform-window mode on the new board.
      **This is G8.**

---

## 7. Biggest unknown

**Does *any* open-OHOS-7.x reference device ship `sceneboard.config = ENABLED`
out of the box?** If yes (likely for dayu210 in 2in1 profile, but
**unverified**), Option B is a straight procurement + port story.
If no (i.e., every open-OHOS reference board ships SCB-disabled and SCB is
only on Huawei-closed devices like MatePad), then Option B collapses
into Option A on a bigger board, and the SceneBoard HAP availability
problem returns. Confirming this is the **first task** of the Phase 2
research workstream and gates whether the procurement ask should be
filed at all.

---

## 8. Cross-references

- `V3-ARCHITECTURE.md` — V3 substrate design; agnostic to window manager
- `V3-SUPERVISION-PLAN.md` — §0/§2/§5 (G8 gate decoupled from G1-G7);
  §7 risk #5 (DAYU200 hardware bottleneck)
- `V3-WORKSTREAMS.md` §W8 — workstream definition + acceptance
- `CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md` §4 (HBC on legacy WMS);
  §11.5 (HBC's risk #5: legacy WMS dead-end); App. A (HBC source paths)
- `CR-FF-HBC-BORROWABLE-PATTERNS.md` — Tier-1/2 patterns (none address SCB)
- `CR-DD-CANDIDATE-C-VS-V2-OHOS-RECONSIDERED.md` §4 — retracted by spike
- `artifacts/ohos-mvp/multi-hap-peer-window-spike/20260515_181930/CHECKPOINT.md` —
  the empirical FAIL evidence; spike author (agent 39); 11 hdc dumps +
  2 screenshots; 2026-05-15
- Source-tree references:
  - `foundation/window/window_manager/scene_board_enable.gni` —
    `scene_board_enabled = false` default
  - `foundation/window/window_manager/window_scene/interfaces/innerkits/src/scene_board_judgement.cpp:46` —
    runtime gate reads `/etc/sceneboard.config`, requires literal
    `ENABLED` on first line
  - `vendor/hihope/rk3568/` — current target board config (DAYU200)
  - `vendor/hihope/dayu210/config.json` — rk3588 board config
    (`target_cpu = arm64`); candidate for Phase 2 procurement
  - `vendor/hihope/rk3568/window_config/window_manager_config.xml:35` —
    `<defaultWindowMode>1</>` (fullscreen) for our current board
  - `applications/standard/launcher/`, `applications/standard/systemui/` —
    legacy shell (present in our checkout)
  - SceneBoard HAP source — **NOT present** in our checkout

---

## 9. Self-audit

- [x] All 3 options seriously investigated with source-tree evidence
- [x] Recommendation has concrete cost basis (5-10 PD for A with hidden
      +10-15 PD HBC coupling = 15-25 PD effective; 8-14 PD for B post-procurement
      + 2-6 wks calendar; 0 PD for C)
- [x] Recommendation has concrete risk basis (A breaks V3, B is procurement-gated,
      C is product-goal-deferred)
- [x] Sequenced recommendation (C now + B by week 8-12) explicitly named
- [x] Cross-references intact to all upstream docs and the spike CHECKPOINT
- [x] Out-of-scope items flagged ("dev board procurement: needs Yue's input")
- [x] Hard constraint compliance:
      - READ-ONLY (no config flipped, no SCB enabled)
      - Probed the spike artifacts (no new hdc commands — hdc not available
        in this agent environment; relied on spike's captured state)
      - Searched source tree thoroughly for SceneBoard build flags + configs
      - No push (local commit only)
- [x] Hard time bound (1-2 PD) — completed in well under one PD of agent time
