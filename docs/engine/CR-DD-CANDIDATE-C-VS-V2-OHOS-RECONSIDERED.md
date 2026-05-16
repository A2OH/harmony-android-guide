# CR-DD — Candidate C (post-spike) vs V2-OHOS (post-codex) — Honest Reconsideration

**Date:** 2026-05-15
**Author:** agent 38 (READ-ONLY strategic re-evaluation, ~1 day)
**Status:** ANALYSIS — recommends user re-confirm or override the 2026-05-15 morning V2-OHOS commitment
**Cross-refs:** `CR-BB-OHOS-RENDER-STRATEGY.md`, `CR-CC-V2-OHOS-RENDER-GLUE-ANALYSIS.md`, `CR60_BITNESS_PIVOT_DECISION.md`, `CR61_BINDER_STRATEGY_POST_CR60.md`, `artifacts/ohos-mvp/cr-bb-spike/20260515_170900/CHECKPOINT.md`, memories `project_v2_ohos_direction.md` + `feedback_additive_shim_vs_architectural_pivot.md`
**Codex transcripts:** referenced as `/tmp/codex-cr-bb-spike-review.md` and `/tmp/codex-cr-cc-review.md` in the brief; the on-disk files were not available to this agent. Their substance as relayed in the brief was used; key claims independently verified against OHOS source (citations below).

---

## TL;DR

Two events on 2026-05-15 weakened the morning's V2-OHOS commitment:

1. **CR-BB W0 pre-spike PASSED in 17 minutes**, reducing Candidate C's MVP from 4-6 to **4-5 weeks** with all foundation risks (libdvm_arm32.so shared library; dvm_entry in-process; SIGBUS chaining) resolved or proven moot.
2. **Codex's CR-CC review demolished V2-OHOS's "CR61 stays clean" claim.** `librender_service_client.z.so` transitively NEEDs `libipc_core.z.so` and `samgr_proxy`. **Independently verified**: `render_service_base/BUILD.gn:400` hard-deps `ipc_core`; `render_service_base/src/platform/ohos/BUILD.gn:163` and `render_backend/BUILD.gn:95` both list `samgr:samgr_proxy`; `rs_render_service_connect_hub.cpp:105` calls `SystemAbilityManagerClient::GetInstance().GetSystemAbilityManager()->CheckSystemAbility(RENDER_SERVICE)` then `CreateConnection` (line 132). V2-OHOS now requires a CR61 amendment to legitimize transitive samgr/libipc, plus 4 unmeasured DAYU200 blockers.

**Recommendation: (c) Hybrid — Candidate C MVP first (4-5 weeks; visible end-to-end demo), then evaluate V2-OHOS migration on stock OHOS once the substrate is proven and the 4 blocker probes have been run cheaply during weeks W1-W4 of Candidate C.** Rationale and risk in §6.

---

## 1. Codex P2 corrections to Candidate C — material to schedule? No.

The P2s identified in `/tmp/codex-cr-bb-spike-review.md` (per brief):

| P2 | Substance | Schedule impact |
|---|---|---|
| #1 Candidate A's draw flow zero-copy → one-copy | Same `OH_Drawing_BitmapBuild` + memcpy pattern that Candidate C already uses. Aligns A with C. | None — surgical implementation detail in W2-W3. |
| #2 SET_USAGE before RequestBuffer | Add `OH_NativeWindow_NativeWindowHandleOpt(window, SET_USAGE, BUFFER_USAGE_CPU_WRITE \| BUFFER_USAGE_MEM_DMA)` before request. ~5 LOC. | None — fits inside W2's bind-and-draw work. |
| #3 Wait on request fence | Add `OH_NativeWindow_NativeWindowAttachBuffer` fence wait or `Sync` on the returned fence FD before memcpy. | None — same W2 work. |
| #4 Row-by-row copy with separate src/dst strides | Replace flat `memcpy(dst, src, w*h*4)` with per-row loop honoring buffer's `pixmap.GetRowBytes()`. | None — implementation detail. |

**Net: 4-5 week MVP estimate stands** with ~½-day extra in W2 to internalize the four corrections. They make the implementation **more correct**; they don't expand scope.

---

## 2. Pre-W0 spike findings applied to Candidate C estimate

From `artifacts/ohos-mvp/cr-bb-spike/20260515_170900/CHECKPOINT.md`:

- **libdvm_arm32.so**: PROVEN (6.7 MB ELF SO; `dvm_entry` + `JNI_CreateJavaVM` exported; 53-second build from clean).
- **dvm_entry() in-process call**: PROVEN (HelloOhos.dex marker printed from inside dlopen-loaded SO; exit 0).
- **SIGBUS chaining**: NOT NEEDED — busCatcher install at `/home/dspfac/dalvik-kitkat/vm/Init.cpp:1350-1356` is gated `if (false)`. The 5-line patch becomes 0 lines.

**W0 trims from 5 dev-days to 3 dev-days** (NAPI module shell + smoke + integration only). The HAP-toolchain inventory was kept out of this spike (per agent 36's hard constraint) but spike 34 already inventoried it as ~1 day. So:

**Total Candidate C MVP: 4-5 weeks** (was 4-6 before pre-W0). Weeks W1 (HAP scaffold + composer_host stability), W2 (XComponent → OH_Drawing canvas with codex P2 corrections), W3 (android.graphics.Canvas → OH_Drawing wiring; delete SoftwareCanvas), W4 (noice decor view through real Skia, replace `drm_inproc_bridge`). W0 (3 days) folds into W1.

---

## 3. Honest scorecard

| Dimension | Candidate C (post-spike + P2s) | V2-OHOS (post-codex) |
|---|---|---|
| **Window status** | Sub-view inside HAP (one XComponent per HAP). HAP is itself a peer window in OHOS WindowManager. | Path G: NOT peer (mirror screen, parallel display). Path H: peer (subject to `IsStartByHdcd` token spike on DAYU200). Path A: peer (subject to AbilityContext stub work). |
| **CR61 compliance** | **CLEAN.** HAP isolates libipc; dalvikvm process holds only opaque `OHNativeWindow*`. `CR-BB §4 Candidate C` line 102. | **REQUIRES amendment.** `render_service_base/BUILD.gn:400` hard-deps `ipc_core`; `render_service_base/src/platform/ohos/BUILD.gn:163` lists `samgr:samgr_proxy`; first RS use triggers samgr `CheckSystemAbility(RENDER_SERVICE)` per `rs_render_service_connect_hub.cpp:105-132`. CR61's "no `libipc.dylib.so` direct dlopen" stays technically true but the spirit ("don't drag samgr into our process tree") is violated transitively. |
| **Same-process dalvikvm** | **PROVEN** (libdvm_arm32.so + dvm_entry in-process per pre-W0 spike). | Daemon-mediated; no in-process dalvikvm needed for V2 substrate. Already-existing `aosp-surface-daemon-port/native/` reused. |
| **MVP weeks** | **4-5** (post-W0 spike). | **9-12** if no surprises. **NEED MORE DATA** per codex's 4 blockers (token domain, mirror-screen viability, samgr amendment, sustained perf, H system-window without AbilityContext). |
| **Hidden risks** | HAP signing chain on DAYU200 (~1 day inventoried by spike 34); cross-process NAPI for `OHNativeWindow*` if thread-in-HAP fails (CR-BB §6 caveat #2); OHOS NAPI ABI drift (CR-BB §6 caveat #3). | (i) DAYU200 token/SELinux probe, (ii) mirror-screen RSDisplayNode may not exist in OHOS 7.0, (iii) CR61 transitive amendment, (iv) sustained 60 Hz × 1080×2400 perf at one-copy ~600 MB/s, (v) H spike for system-window without AbilityContext. None measured today. |
| **Code transfer from Phase 1** | ~50% (canvas substrate + JNI bridge + V2 Java substrate + producer side; loses BufferQueueCore/MemfdGraphicBuffer/`WestlakeGraphicBufferProducer` daemon path). | ~60-70% (entire surface daemon kept; only backend swaps; per CR-CC §5 + §6). |
| **Per-frame perf** | One-copy memcpy app → GraphicBuffer (~600 MB/s on rk3568 LPDDR4 — OK at 1080×2400×60). Codex P2 #4 row-stride correction makes this honest. | Same one-copy memcpy from memfd-backed AOSP GraphicBuffer to OHOS GraphicBuffer (CR-CC §5 M12 step 3) PLUS daemon-mediated DLST consumer chain. Strictly more layers. |
| **Production path** | XComponent → render_service (well-trodden; how Cocos/Unity/Flutter ship today on OHOS per CR-BB §4). | Cross-process binder (our libbinder /dev/vndbinder) → daemon → librender_service_client → samgr → render_service. More layers, more failure modes. |
| **Multi-window** | Single XComponent per HAP. **But:** multiple HAPs (or multiple UIAbilities per HAP) ARE peer windows in OHOS WindowManager — see §4. | True peer windows ONCE H lands (4-6 wks after G's 3-5 wks). Until H lands, V2-OHOS is not a peer-window architecture either. |
| **Project risk profile** | **Low.** 4-5 weeks; foundation validated; clear spec; codex P2s are surgical. | **Medium-High.** 9-12 weeks; 4 unmeasured blockers; CR61 amendment required; rk3568 hardware bottleneck (per CR-CC §6 risk #4 + MEMORY.md). |

---

## 4. The peer-window question — does V2-OHOS's advantage actually exist?

The user's stated reason for V2-OHOS (per `project_v2_ohos_direction.md` line 16-17):

> Westlake's product goal is "Android apps as first-class OHOS citizens managed by WindowManager alongside ArkTS apps." Candidate C's "single window per HAP, sub-views" structure doesn't deliver that.

**This claim deserves re-examination.**

### What the OHOS WindowManager actually exposes

From `docs/en/application-dev/windowmanager/window-overview.md`:
- Application windows split into **main windows** (one per UIAbility) and **subwindows** (dialogs, floating windows; lifecycle bound to main).
- Three application window modes: full-screen, **split-screen**, and **freeform window** (multi-window concurrent display, z-axis ordered).
- "**Multiple freeform windows can be simultaneously displayed on the screen.** These freeform windows are arranged on the z-axis in the sequence that they are opened or gain the focus."

From `docs/en/application-dev/windowmanager/application-window-stage.md`:
- Each `UIAbility.onWindowStageCreate` gets its own `WindowStage`.
- `windowStage.createSubWindow` creates additional windows under the same UIAbility.
- `Window.createWindow(config, ...)` creates subwindow OR system window.

### What this means for Candidate C

| Configuration | OHOS WindowManager treats as |
|---|---|
| 1 HAP × 1 UIAbility × 1 XComponent hosting noice | 1 peer window (noice = peer alongside ArkTS apps). User can split-screen, freeform, etc. |
| 1 HAP × N UIAbility × 1 XComponent each (each hosting one Westlake app) | N peer windows. WindowManager z-orders them, MMI routes touch independently. |
| N HAPs × 1 UIAbility each × 1 XComponent each | N peer windows. Same as above but cross-HAP boundary. |
| 1 HAP × 1 UIAbility × 1 main window XComponent + N subWindows × 1 XComponent each | 1 peer + N subwindows (dialogs/floating windows) — WindowManager managed. |

**The "single XComponent per HAP" limit doesn't equal "single Westlake app per device."** It equals "the UIAbility-to-XComponent mapping is 1:1." OHOS's stage model already supports N UIAbilities per HAP, and each gets a peer main window.

### What V2-OHOS uniquely buys

- **Avoids HAP packaging overhead** for the Nth Westlake app (in V2-OHOS, dalvikvm just calls `Window::Create` directly).
- **Does NOT add a peer-window capability that Candidate C lacks** — Candidate C achieves multi-peer-window via N HAPs (or N UIAbilities).
- **Strategic narrative**: "we're peer to OHOS" is genuine in Candidate C too — the HAP IS a peer ArkTS app from WindowManager's view. Westlake-host-HAP just happens to render Westlake's pixels via XComponent.

**Verdict: the peer-window advantage of V2-OHOS as stated this morning largely dissolves under scrutiny.** What V2-OHOS really buys is **avoiding the HAP packaging step per app** and **first-class libwm.z.so use** — not first-class peer-window status.

---

## 5. Recommendation — option (c) HYBRID

**Sequence: Candidate C MVP (4-5 wks) → defer V2-OHOS evaluation to post-MVP based on what Candidate C teaches us.**

### Why hybrid, not (a) pure pivot to C

A pure pivot to C abandons the V2-OHOS investment narrative without measuring whether V2-OHOS's blockers are real. The hybrid path **runs the codex blocker probes cheaply DURING Candidate C weeks W1-W4** — they cost ~1 day each and don't compete with the C critical path:

1. **W1 day-spare**: `hdc shell id -Z` from a HAP-spawned dalvikvm thread → reveals process domain. Same probe satisfies both C (XComponent NAPI domain) and V2-OHOS (Path H token domain).
2. **W2 day-spare**: write the 300-LOC mock APK CR-CC §4 specifies — 1 person-week budget but a 1-day standalone test ("does `RSSurfaceNode::Create` from a non-WMS process land a pixel"). Determines whether Path G is dead before V2-OHOS commits.
3. **W3 day-spare**: sustained perf test rig — already needed for Candidate C's 60 Hz validation.
4. **W4 day-spare**: H-spike read-only — just check whether `IsStartByHdcd` accepts hdcd-spawned dalvikvm in `WindowSceneSessionImpl::Create` path B without an AbilityContext token; no need to actually create a window yet.

End of W4: Candidate C has a usable demo (noice through real Skia, peer to OHOS apps via the HAP wrapper) AND the four V2-OHOS blocker measurements are in hand. THEN re-decide whether the next 4-8 weeks go to V2-OHOS H+A or to extending C (touch routing, multi-HAP orchestration, lifecycle).

### Why NOT (a) pure pivot to C

C alone permanently caps Westlake at "needs an ArkTS HAP per N apps" — fine for MVP but not the long-term direction the user already chose. Hybrid keeps that door open without paying for it now.

### Why NOT (b) stay on V2-OHOS

V2-OHOS's "peer window" advantage has been overstated (§4); its CR61 cleanliness has been disproven (codex transcript + independent verification §3); and its 9-12 wk estimate is unmeasured against 4 codex blockers. Spending 4-6 person-weeks before any visible end-to-end pixel is exactly the `feedback_additive_shim_vs_architectural_pivot.md` anti-pattern: high-confidence top-down design that hasn't met the board.

### What the hybrid recommendation explicitly preserves

- The CR60 32-bit pivot (CR60 stands; arm32 dalvikvm is C's substrate too).
- The CR61 "no direct libipc dlopen" rule (C honors it cleanly; V2-OHOS would force a CR61.1 amendment if revisited).
- The V2 Java substrate (works in both paths — it's bitness-and-IPC-neutral).
- The agent 36 pre-W0 spike work (libdvm_arm32.so + dvm_entry — directly used by C).
- The mock APK from CR-CC §4 (built during C's W2 day-spare; informs V2-OHOS reactivation decision).
- The macro-shim contract (unchanged in either path).

---

## 6. Honest caveats — what I could not determine

1. **The actual codex transcripts** (`/tmp/codex-cr-bb-spike-review.md`, `/tmp/codex-cr-cc-review.md`) **were not on disk** at the time of this analysis. I worked from the user's brief summary and verified each cited claim against the OHOS source tree (citations in §3). If codex made additional claims not captured in the brief, this analysis missed them.
2. **HAP signing on DAYU200 in dev mode** — spike 34 inventoried the toolchain (~1 day) but did not actually sign and load a HAP yet. If signing reveals an unexpected blocker, C's W1 slips. Mitigation: spike 34's inventory plus the existence of XComponent-based shipped apps (Cocos/Unity/Flutter) on OHOS suggests the path is well-trodden.
3. **Whether dalvikvm-as-thread-inside-HAP avoids signal-handler/atfork/TLS clashes with the ArkTS VM** (CR-BB §6 caveat #1). Pre-W0 spike validated dvm_entry from a standalone harness, NOT inside a NAPI worker thread. If thread-in-HAP fails, the fallback is process-separated dalvikvm + cross-process `OHNativeWindow*` handoff — which approaches the CR61 boundary that C otherwise stays clear of.
4. **Whether OHOS `BUFFER_USAGE_MEM_DMA` actually grants the CPU-write/copy properties C needs** (codex P2 #2). Documented in spec but unverified on rk3568.
5. **Whether the OHOS NAPI ABI for `external` values can carry an `OHNativeWindow*` across the JNI boundary** as a `bigint` — the brief assumes yes; not verified.

### Risks of recommended path (c) hybrid

1. **HAP signing on DAYU200 turns out to be a hard blocker** (vendor cert needed, dev-mode self-signing rejected). Mitigation: fall back to V2-OHOS Path H-via-hdcd-token, accepting CR61 amendment as the cost. This is the same fallback CR-BB §6 already documents.
2. **Thread-in-HAP architecture fails** at the dalvikvm signal-handler/TLS boundary. Mitigation: process-separated dalvikvm + UDS+memfd handoff of `OHNativeWindow*`. Adds ~1 wk to W1; CR61 unchanged because the libipc still lives in the HAP process.

### Fallback if (c) fails

- **C MVP fails W1 (HAP signing) → jump to V2-OHOS Path H** (4-6 wks, requires CR61.1 amendment + IsStartByHdcd token success). The hybrid has already run the W1 day-spare token probe by then, so the H path is no worse than starting today.
- **C MVP fails W2 (XComponent + OH_Drawing perf cliff) → drop to V2-OHOS Path G mirror-screen** (3-5 wks; the mock APK from W2 day-spare has already validated whether G works at all).
- **All paths fail → M12 own compositor** (per CR-BB §6 fallback; CR-CC §6 fallback). Multi-quarter; last resort. This is the same "ultimate fallback" both specs already identify, so no new risk introduced.

---

## 7. Self-audit

- [x] Read all required files: CR-BB, CR-CC, CR60, CR61, pre-W0 CHECKPOINT, project_v2_ohos_direction, feedback_additive_shim_vs_architectural_pivot, feedback_macro_shim_contract.
- [x] Verified codex CR-CC's "samgr/libipc transitive" claim against source: `render_service_base/BUILD.gn:400`, `render_service_base/src/platform/ohos/BUILD.gn:163`, `render_backend/BUILD.gn:95`, `rs_render_service_connect_hub.cpp:105-132`. **Confirmed.**
- [x] Probed OHOS multi-window capability via `docs/en/application-dev/windowmanager/window-overview.md` + `application-window-stage.md`. **Confirmed**: freeform/split mode + multiple UIAbilities + multiple HAPs all yield peer windows.
- [x] Honest scorecard built (§3); each row cited.
- [x] Re-evaluated the "peer window" goal (§4); found the V2-OHOS advantage smaller than this morning's framing.
- [x] No code changed. No commits. No pushes. READ-ONLY discipline maintained.
- [x] Recommendation is (c) hybrid, with explicit rationale and explicit risk + fallback.

---

## 8. Cross-references

- `docs/engine/CR-BB-OHOS-RENDER-STRATEGY.md` — Candidate C spec (PENDING REVISION; this CR's recommendation lifts it back to active).
- `docs/engine/CR-CC-V2-OHOS-RENDER-GLUE-ANALYSIS.md` — V2-OHOS spec (this CR proposes deferring its execution pending C's MVP findings).
- `artifacts/ohos-mvp/cr-bb-spike/20260515_170900/CHECKPOINT.md` — pre-W0 sub-gate validation; foundation for C's revised 4-5 wk estimate.
- `docs/engine/CR60_BITNESS_PIVOT_DECISION.md` — bitness prerequisite (preserved in both paths).
- `docs/engine/CR61_BINDER_STRATEGY_POST_CR60.md` — binder strategy. C honors as-written; V2-OHOS would require CR61.1 amendment (not written).
- `foundation/graphic/graphic_2d/rosen/modules/render_service_base/BUILD.gn:400` — hard `ipc_core` dep proving codex CR-CC samgr claim.
- `foundation/graphic/graphic_2d/rosen/modules/render_service_base/src/platform/ohos/BUILD.gn:163` — `samgr:samgr_proxy` dep.
- `foundation/graphic/graphic_2d/rosen/modules/render_service_base/src/render_backend/BUILD.gn:95` — second `samgr:samgr_proxy` dep.
- `foundation/graphic/graphic_2d/rosen/modules/render_service_base/src/platform/ohos/rs_render_service_connect_hub.cpp:105-132` — actual samgr `CheckSystemAbility` + `CreateConnection` call site.
- `docs/en/application-dev/windowmanager/window-overview.md` — multi-window mode documentation.
- `docs/en/application-dev/windowmanager/application-window-stage.md` — UIAbility/WindowStage/createSubWindow APIs.
- Memory: `project_v2_ohos_direction.md` (the morning's commitment), `feedback_additive_shim_vs_architectural_pivot.md` (the lesson; cuts both ways here — V2-OHOS's 4 unmeasured blockers risk becoming the same trap).
