# Westlake → Full McD App: Gap Review & Agent-Ready Punch List

**Date:** 2026-05-03 PT
**Author:** Claude Opus 4.7 (review session)
**Baseline:** `origin/main @ 5e5559e3` (handoff `WESTLAKE_REAL_MCD_HANDOFF_20260503.md`)
**Phone:** OnePlus 6 `cfb7c9e3` (LineageOS 22 / Android 15, rooted, SELinux permissive)
**Scope:** every gap between the 2026-05-02 17:57 PT bounded green proof and a full-function `com.mcdonalds.app` running on Westlake guest `dalvikvm` — both on Android phone and on OHOS.

This doc is structured so each `### PF-…` section is **self-contained**: a fresh agent can be dropped in with just that section as context and start work. The "Next-agent prompt" sub-blocks are copy-paste ready.

---

## 1. Methodology and confidence legend

Sources combined for this review:
- `WESTLAKE_PLATFORM_FIRST_ISSUES.md` (master ledger, ~67k tokens)
- `WESTLAKE_REAL_MCD_48H_FULL_APP_RALLY_20260502.md` (~36k tokens)
- `WESTLAKE_SOUTHBOUND_API.md` (~27k tokens)
- `WESTLAKE_REAL_MCD_HANDOFF_20260503.md` (the handoff)
- All `artifacts/real-mcd/2026050[12]_*` directories (6+ recent runs)
- Source scan of `shim/java/`, `westlake-host-gradle/`, `art-latest/stubs/`, `ohos-deploy/`
- GitHub issues `gh issue list --repo A2OH/westlake --state open`

Confidence:
- **[E]** = empirically observed in artifact logcat / proof checker output
- **[D]** = explicitly documented in a program doc (cited)
- **[C]** = code-scan only — likely but not yet exercised end-to-end

Where these conflict, **[E] wins**. A few **[C]** claims in this doc are flagged as "verify before acting" — see §10.

---

## 2. Baseline (what works today, accepted artifact)

`artifacts/real-mcd/20260502_175722_mcd_48h_network_pf621_bounded_final/` (`gate_status=PASS`)

- Stock `com.mcdonalds.app` runs inside Westlake guest `dalvikvm` subprocess (`westlake_subprocess_purity ... direct_mcd_processes=0`)
- Real APK + DEX + resources loaded from `/data/local/tmp/westlake/`
- Network 2xx via host HTTP bridge V2 + localhost proxy via `adb reverse`
- Dashboard inflates real section/item XML; popular-row click routes to PDP
- PDP real XML inflated; LiveData mutation observed; observer dispatch gate fires
- `BasketAPIHandler` reached; `MCD_PDP_DOWNSTREAM_BASKET_COMMIT_REACHED count=8`
- `MCD_PDP_CARTINFO_PROJECTION_STATUS readback=3 bridge=1` → cart count = 1 visible to app
- `MCD_PDP_STOCK_VIEW_CLICK invoked=true` via `callOnClick` route (NOT `performClick` — see PF-632)

**Sub-gate caveat:** the artifact's `check-real-mcd-proof.txt` contains `pdp_add_cart_gate_status=FAIL` alongside the overall `gate_status=PASS`. The overall gate accepts because the sub-gate is intentionally non-blocking under PF-621. Future readers — do not panic when you see this.

---

## 3. Empirical blockers (observed crashes/stalls in 2026-05-02 artifacts)

### B1. SIGBUS in `OsSharedRealm.nativeCloseSharedRealm` (PF-630)
- Signal `7 (SIGBUS, BUS_ADRALN)` at fault addr `0xfffffffffffffb17`
- Stack: `io.realm.internal.OsSharedRealm.close()` → `nativeCloseSharedRealm(long)` JNI boundary
- Artifact: `artifacts/real-mcd/20260502_174634_mcd_48h_network_pf621_full_app_final/logcat-dump.txt:10279`
- Trigger: Realm cleanup after cart writes; happens in **long-soak** runs, not in the bounded gate
- Candidate fix: `/home/dspfac/art-latest/build-ohos-arm64/bin/dalvikvm` (built with patched `patches/runtime/interpreter/interpreter_common.cc`); documented hash `20acd7b1c18493cd9a52c0cf8f67243c32459e34fc0faa2be1295cf0f43d2999`. **Not yet deployed.**

### B2. SIGBUS in `BasketAPIHandler.Q → nativeSetLong` (PF-628)
- Signal `7 (SIGBUS)`, same fault-addr family
- Stack: `BasketAPIHandler.Q → BaseStorage.insertOrUpdate → [PFCUT-REALM-ARGS] nativeSetLong`
- Artifact: `artifacts/real-mcd/20260502_183431_mcd_48h_pf628_lifecycle_no_bridge_long/check-real-mcd-proof.txt:196`
- Marker: `pf626_current_downstream_blocker type=realm_sigbus sigbus=1`
- Triggered by removing `MCD_PDP_CARTINFO_SET_BRIDGE` (i.e. trying to satisfy PF-628 without the McD-specific propagation). Likely the same root cause as B1 — a Realm JNI table-state bug surfaced by deeper writes.

### B3. `NoSuchMethodError: AlertDialog.Builder(Context,int)` (PF-632)
- `java.lang.NoSuchMethodError: No method __init__ (Landroid/content/Context;I)V in class Landroid/app/AlertDialog/Builder`
- Suppresses the `performClick` PDP path; bounded proof only succeeds via the `callOnClick` workaround (`MCD_PDP_STOCK_VIEW_CLICK source=projected_hit route=performClick invoked=false`)
- Artifact: `20260502_175722_…/logcat-dump.txt`
- The handoff says the shim was added — **verify whether the symbol is actually resolved at runtime** (see PF-632 section).

### B4. `MCD_PDP_OBSERVER_DISPATCH_GATE blocked=1, reason=observer_dispatch_opt_in_required` (PF-628)
- Soft gate, not a crash
- The no-bridge path reaches the LiveData observer but the dispatch is opt-in-gated. With the bridge on, the gate passes; without it, the dispatch never fires and downstream commit doesn't get exact stock context.
- Artifact: `20260502_183431_…`

### B5. `mcd_pdp_fragment_view_created/attached count=0` in failure runs (PF-622, masked green)
- Fragment view-tree lifecycle markers don't fire under the no-bridge path; only fire when the bridge is in place
- Bounded gate currently passes by accepting the compatibility marker shape (`mState=7` / `isResumed()`) instead of native `mResumed`
- This means **PF-622 is not actually closed**; it's masked.
- Source: `WESTLAKE_SOUTHBOUND_API.md:1219-1225`, `WESTLAKE_PLATFORM_FIRST_ISSUES.md:2611`

---

## 4. Open GitHub issues — status & agent-ready prompts

Issue numbers verified via `gh issue list --repo A2OH/westlake --state open`. Order is **priority for full-McD**, not chronological.

### PF-630 — Realm close/finalizer SIGBUS — `#596`
**Status:** candidate runtime built, not deployed.
**Acceptance:** 10-min post-cart soak, no fatal/SIGBUS, guest VM pid still present.
**Risk if rushed:** deploying replaces the only known-good runtime; if the candidate is worse, the bounded proof regresses.

**Next-agent prompt:**
```
Goal: deploy the PF-630 candidate dalvikvm and prove it eliminates the
OsSharedRealm.nativeCloseSharedRealm SIGBUS without regressing the bounded gate.

1. Verify candidate hash matches docs:
     sha256sum /home/dspfac/art-latest/build-ohos-arm64/bin/dalvikvm
     # expect 20acd7b1c18493cd9a52c0cf8f67243c32459e34fc0faa2be1295cf0f43d2999
2. Save current phone runtime hash for rollback:
     adb -s cfb7c9e3 shell sha256sum /data/local/tmp/westlake/dalvikvm
3. Sync the candidate (uses DALVIKVM_SRC override):
     env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
       ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
       DALVIKVM_SRC=/home/dspfac/art-latest/build-ohos-arm64/bin/dalvikvm \
       ./scripts/sync-westlake-phone-runtime.sh
4. First run: bounded gate (regression check) — must still PASS:
     env ADB_BIN=… ADB_HOST=… ADB_PORT=… ADB_SERIAL=… \
       WESTLAKE_HTTP_PROXY_BASE=http://127.0.0.1:18080 \
       WESTLAKE_START_HTTP_PROXY=1 WESTLAKE_GATE_INTERACT=1 \
       WESTLAKE_GATE_SLEEP=470 WESTLAKE_DASH_WAIT=360 WESTLAKE_PDP_WAIT=240 \
       ./scripts/run-real-mcd-phone-gate.sh mcd_pf630_candidate_bounded_regression
5. If bounded PASSES, run the long-soak (10 min post-cart):
     env … WESTLAKE_GATE_SLEEP=600 … \
       ./scripts/run-real-mcd-phone-gate.sh mcd_pf630_realm_close_soak_long
6. If long-soak PASSES (no fatal/SIGBUS, guest VM pid present at end), update
   issue #596 with both artifact paths and close. Add a short note to the
   handoff doc.
7. If ANY step fails, revert: re-sync the original runtime via the same script
   without DALVIKVM_SRC, and re-run bounded gate to confirm baseline restored.

Guardrails:
- Do not weaken scripts/check-real-mcd-proof.sh.
- Do not change the bounded-gate parameters (sleep/wait values are tuned).
- Capture both artifacts under artifacts/real-mcd/ for evidence.
```

### PF-628 — Remove McD CartInfo bridge — `#594`
**Status:** last attempt died at SIGBUS in `BasketAPIHandler.Q` (B2 above). Tied to PF-630.
**Acceptance:** `CartViewModel` updates from natural lifecycle/LiveData with `bridge=0` and positive cart readback; no fatal signal.

**Next-agent prompt:**
```
Goal: prove cart count > 0 readback with MCD_PDP_CARTINFO_SET_BRIDGE removed.

PRECONDITION: PF-630 (#596) candidate dalvikvm is deployed and bounded gate
green. If not, do PF-630 first.

1. Inspect the dispatch gate marker `observer_dispatch_opt_in_required`:
     grep -rn observer_dispatch_opt_in_required shim scripts
   Find what flips the opt-in. If it's a feature flag, identify the env var.
   If it's a code branch, decide whether to enable for the no-bridge run.
2. Re-run the no-bridge gate with the candidate runtime (PF-630 hash):
     env ADB_BIN=… ADB_HOST=… ADB_PORT=… ADB_SERIAL=… \
       WESTLAKE_HTTP_PROXY_BASE=http://127.0.0.1:18080 \
       WESTLAKE_START_HTTP_PROXY=1 WESTLAKE_GATE_INTERACT=1 \
       MCD_PDP_CARTINFO_SET_BRIDGE=0 \
       WESTLAKE_GATE_SLEEP=600 WESTLAKE_DASH_WAIT=360 WESTLAKE_PDP_WAIT=240 \
       ./scripts/run-real-mcd-phone-gate.sh mcd_pf628_no_bridge_post_pf630
3. Acceptance markers in check-real-mcd-proof.txt:
   - bridge=0 (no MCD_PDP_CARTINFO_SET_BRIDGE applied=true)
   - cart_or_bag_mutated > 0 (positive readback)
   - guestDalvikvm=1 at end (no SIGBUS)
4. If still failing, do NOT re-enable the bridge. Capture the failure
   artifact path + offending log lines and update issue #594 with the
   diagnosis. Hand back to user.

Guardrails:
- The McD-specific bridge is a known crutch; the goal is to remove it, not
  re-strengthen it.
- Do not edit MockBackendServer or other host-side state to make the proof
  pass; the change must be in the framework substrate.
```

### PF-629 — Deeper stock routes — `#595`
**Status:** PDP-cart proven; cart screen / bottom-nav / login / payment / order tracker / location features all unproven.
**Acceptance per route:** explicit success marker for the route stage and observable app-state change.

**Next-agent prompt:**
```
Goal: prove the next deeper route. Start with quantity_plus → cart=2 then
expand to a cart screen route.

1. Run the quantity_plus probe (already documented in handoff):
     env ADB_BIN=… ADB_HOST=… ADB_PORT=… ADB_SERIAL=… \
       WESTLAKE_HTTP_PROXY_BASE=http://127.0.0.1:18080 \
       WESTLAKE_START_HTTP_PROXY=1 WESTLAKE_GATE_INTERACT=1 \
       WESTLAKE_GATE_PDP_PRE_ADD_TAPS=quantity_plus \
       WESTLAKE_GATE_SLEEP=150 WESTLAKE_DASH_WAIT=260 WESTLAKE_PDP_WAIT=180 \
       ./scripts/run-real-mcd-phone-gate.sh mcd_pf629_quantity_plus_cart2_gate
   Expect: MCD_ORDER_PDP_STOCK_ACTION control=quantity_plus invoked=true
           cart count = 2 (or precise stock rejection)
2. After cart=2 passes, design the next probe. Candidates in priority:
   a. customize → add (proves customize sheet inflates and writes options)
   b. cart screen (bottom-nav cart) — likely uncovers RecyclerView gaps
   c. checkout shell — likely uncovers Material Snackbar/BottomSheet gaps
   d. login modal — likely uncovers AccountManager / Firebase Auth gaps
3. For each NEW route, expect to discover new framework misses. Open one
   PF-632 sub-issue per distinct missing API; do not bundle.
4. Update WESTLAKE_SOUTHBOUND_API.md whenever a new host dependency is added.

Guardrails:
- One route per artifact; do not chain routes in a single gate run.
- Do not introduce new MCD_*-specific bridges; if a route requires
  app-specific behavior, raise it as a finding instead of patching it.
```

### PF-632 — Framework/API misses — `#597`
**Status:** `AlertDialog.Builder(Context, int)` already added per handoff; needs runtime-fire verification.
**Acceptance:** missing API resolves at runtime; the route that hit it advances.

**Next-agent prompt:**
```
Goal: confirm AlertDialog.Builder(Context,int) actually resolves under the
performClick path, and add a tiny test marker.

1. Read shim/java/android/app/AlertDialog.java; confirm the constructor
     public Builder(Context context, int themeResId)
   exists and the body is non-trivial (not a stub that throws).
2. Confirm the class is included in aosp-shim.dex by checking the dex
   manifest or running:
     /mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -s cfb7c9e3 shell \
       dexdump /data/local/tmp/westlake/aosp-shim.dex 2>/dev/null \
       | grep -A1 'AlertDialog$Builder'
3. Run a targeted gate that exercises performClick (NOT callOnClick):
   - Identify the env var or input that forces performClick path. If none
     exists, examine the gate harness and add WESTLAKE_GATE_PDP_FORCE_PERFORM_CLICK=1.
4. Acceptance: in the resulting check-real-mcd-proof.txt, look for
     MCD_PDP_STOCK_VIEW_CLICK source=projected_hit route=performClick invoked=true
   AND no NoSuchMethodError on AlertDialog$Builder.<init>.
5. If invoked=false persists, the symbol may be present but the dex isn't
   being preferred over the platform's android.app.AlertDialog. Diagnose
   classloader precedence; do NOT add a wrapper-class workaround.
```

### PF-627 — Dashboard rows + RecyclerView parity — `#593`
**Status:** rows inflate enough for popular-row click; broader RecyclerView paths (sticky headers, pinned items, swipe actions, multi-viewtype recycling) untested.
**Acceptance:** stock dashboard scrolls fully with all expected sections rendered, including images, with no placeholder fallbacks.

**Next-agent prompt:**
```
Goal: expand RecyclerView/Glide/image-pipeline parity from "popular row"
to "full dashboard".

1. Identify all section types in the McD dashboard from
   artifacts/real-mcd/20260502_175722_…/screen.png and the corresponding
   logcat MCD_STOCK_DASHBOARD_PROJECTION root_select=1 markers.
2. For each section type, find the AndroidX RecyclerView Adapter onCreateViewHolder
   path in the McD dex and the corresponding shim path. Capture which
   ViewHolder types currently render and which fall back to placeholder.
3. Image pipeline: McD likely uses Glide. Trace one image load through
   shim/java/com/bumptech/glide/ (or wherever the Glide shim lives) and
   confirm whether the bitmap actually loads or returns placeholder.
4. Propose a code-only diff that adds the next missing ViewHolder /
   Glide shim. Validate with a bounded gate run capturing the new section
   marker.

Guardrails:
- No McD-specific Adapter shims; only generic AndroidX RecyclerView /
  Material / Glide shims.
- Image-pipeline failures must surface as a log warning, not silent
  placeholder, so this gap can be measured next time.
```

### PF-622 — PDP Fragment lifecycle — `#587` (mislabeled green)
**Status:** marked green in the rally doc 18:10 PT update because the bounded gate accepts the compatibility marker shape (`mState=7`/`isResumed()`); the native `mResumed` flag is **not** actually being driven.
**Acceptance to truly close:** `mcd_pdp_fragment_view_created count>0` AND `mcd_pdp_fragment_view_attached count>0` in a no-bridge run, AND the compatibility marker can be removed without regressing.

**Next-agent prompt:**
```
Goal: convert PF-622 from "compatibility-marker green" to actually green.

1. In a fresh artifact, search for the compatibility marker setter:
     grep -rn 'mState=7\|isResumed()\|mResumed' shim westlake-host-gradle
2. Identify why native lifecycle isn't driving the markers in no-bridge
   runs (artifact 20260502_183431). Likely candidate: AndroidX Fragment
   onViewCreated → performStart/Resume isn't being invoked because the
   guest's MiniActivityManager skips a step.
3. Propose a code change that drives native lifecycle, with a regression
   gate that removes the compatibility marker.
4. Update the rally doc to either:
   (a) declare PF-622 truly green and remove the compatibility-marker
       caveat, OR
   (b) re-open PF-622 with explicit acceptance criteria.

Guardrails:
- Don't widen the compatibility-marker scope; the goal is to remove it.
- Capture before/after artifact paths.
```

### PF-625 — McD network/storage/Realm/cart fidelity — `#591`
**Status:** bounded route green; broader Realm tables, cancel/close coherence, SQLite breadth still open.
**Sub-tags A/B/C/D referenced in earlier doc sections were collapsed in the 18:10 PT update without explicit retirement.** Treat as one issue with a coherence-extension acceptance, but flag the sub-tag confusion.

**Next-agent prompt:**
```
Goal: prove Realm coherence across more than the BaseCart.cartProducts
table.

1. From the McD dex, list every Realm RealmObject subclass (com.mcdonalds.*).
   For each, capture write-frequency from logcat MCD_* markers in the
   bounded artifact.
2. For the top 3 by write-frequency (other than BaseCart), construct a
   targeted probe (gate sequence) that exercises insert/update/delete/close.
3. Run each probe with PF-630 candidate runtime; collect SIGBUS frequency
   per table. Open one PF-625 sub-issue per table that crashes.
```

### PF-624 — Order/customize/bag/cart downstream proof — `#590`
**Status:** marked green by the 18:10 PT update because telemetry no-throw publisher seed is reapplied each run. Flagged as regression-prone; needs an explicit gate.

**Next-agent prompt:**
```
Goal: add a regression gate so PF-624 cannot silently re-break.

1. Read scripts/run-real-mcd-phone-gate.sh and check-real-mcd-proof.sh;
   find where the telemetry seed is applied.
2. Add an explicit checker assertion: telemetry publisher must be reachable
   AND must accept N>0 events per run, recorded in check-real-mcd-proof.txt
   as a PASS/FAIL line.
3. Validate the new gate fires PASS on the bounded artifact baseline.
```

### PF-623 — Generic View rendering / input dispatch — `#589`
**Status:** rendering and tap routing work in bounded path; dispatch correctness across all View types (custom Views, overlapping Views, scroll children) unproven.

**Next-agent prompt:** (defer until PF-627 widens the View surface)

### PF-608 — OHOS/musl southbound parity — `#581`
**Status:** the entire 2026-05-03 proof is Android-phone-only. OHOS adapter implementation is essentially unstarted for 12 surfaces (see §6).
**Acceptance:** each southbound surface has a phone-vs-OHOS proof pair.

**Next-agent prompt:** (this is the largest single workstream — see §6 for the 12-surface inventory; do NOT attempt as a single PR)

### PF-607 — Runtime stability / coroutine event path — `#580`
**Status:** coroutine `SharedFlow` event path is bypassed by a temp `JustFlipBase.c(...)` shield. Real Kotlin coroutine dispatch is not running.
**Source:** `WESTLAKE_PLATFORM_FIRST_ISSUES.md:42, 389-393`
**Risk:** removing the shield will surface unknown-quantity coroutine bugs in McD.

**Next-agent prompt:**
```
Goal: characterize the JustFlipBase.c(...) shield, design a removal plan.

DO NOT REMOVE THE SHIELD WITHOUT EXPLICIT AUTHORIZATION — it is currently
load-bearing for the bounded proof.

1. Locate the shield:
     grep -rn 'JustFlipBase' shim
2. Trace what call-site triggered it: what coroutine path does it short-circuit?
3. Identify the underlying Kotlin coroutine machinery that's missing or broken.
   Likely candidates: Dispatchers.Main, CoroutineExceptionHandler, ThreadLocal
   continuation context.
4. Produce a written diagnosis: shield call-sites, root cause, proposed fix
   (probably in shim/java/kotlin/coroutines/), risk assessment, expected
   regression surface.
5. Hand the diagnosis back; do not commit code changes.
```

### PF-606 — UI rendering / Material/AppCompat fidelity — `#579`
**Status:** generic stock View drawing, Material/AppCompat fidelity, theme fidelity beyond row-XML inflation in PF-627.
**Source:** `WESTLAKE_PLATFORM_FIRST_ISSUES.md:2673`

**Next-agent prompt:** (defer until PF-627 surfaces specific Material widget gaps)

---

## 5. New gaps not in any open issue

These came out of the review and are not currently tracked.

### N1. PF-622 mislabeled-green meta-issue (doc cleanup)
- The rally doc 18:10 PT update says PF-622 is green; the underlying lifecycle is not actually driving native flags (covered in §3 B5)
- Fix: either re-open PF-622 with explicit acceptance, or document the compatibility-marker caveat clearly. Doc PR only.

### N2. Stale entries in `WESTLAKE_PLATFORM_FIRST_ISSUES.md`
- PF-620 (network) marked green at 18:10 PT but earlier 16:27/17:00/17:08 sections still call it P0 red (`:2614, :2509, :2462` vs `:2665`) — superseded entries not pruned
- PF-622 — never explicitly declared green; current "green" is masked by compatibility marker (`:2557`)
- PF-624 — flips green between 14:55 and 15:28 with no regression gate (`:1057-1066`)
- PF-625 sub-tags A/B/C/D appear in mid-day sections then collapse to single "PF-625 green" without retiring sub-tags (`:2452-2503, :2497-2503, :2549-2607`)
- `no_unsafe` checker false-positive (rally:1587) — fixed but earlier sections still cite as blocker
- Fix: pure doc cleanup — see worktree A in §8.

### N3. SQLite / SharedPreferences persistence honesty
- Code-scan agent claimed `SharedPreferencesImpl` is in-memory only and `SQLiteDatabase` uses HashMap-backed tables (not real SQLite)
- **NEEDS VERIFICATION** before being scoped — the bounded proof reaches Realm successfully (modulo SIGBUS), so at least Realm persists. SharedPrefs/SQLite call sites may be cold paths.
- Fix: targeted code-read of `shim/java/android/content/SharedPreferences*` and `shim/java/android/database/sqlite/SQLiteDatabase*`; either confirm and open issue, or close as not-an-issue.

### N4. WebView absence
- Code-scan agent claimed `android.webkit.WebView` is not in the shim and any McD route triggering it would `ClassNotFoundException`
- **NEEDS VERIFICATION** — confirm by `grep -rn 'WebView' shim/java/android/webkit/` and by checking whether McD's bounded route currently triggers WebView at all.
- Fix: if confirmed absent and any future route needs it, open a PF-632 sub-issue with priority tied to the route plan.

---

## 6. OHOS southbound parity (12 surfaces, all open under PF-608 / PF-626)

Per `WESTLAKE_SOUTHBOUND_API.md:478-489, 1295-1319`, the entire phone proof needs an OHOS adapter pair. None of the 12 surfaces below has a working OHOS implementation yet.

| # | Surface | OHOS adapter target | Phone implementation reference |
|---|---|---|---|
| 1 | Process/runtime launch | OHOS Ability subprocess + stdio | `dalvikvm` subprocess via host APK |
| 2 | HTTP bridge | OHOS networking | `adb reverse` + `westlake-dev-http-proxy.py` |
| 3 | Display-list/frame transport | OHOS XComponent/canvas consumer | `westlake_frames.dlst` + `DisplayListFrameView` |
| 4 | Window/Display | OHOS window service shim | bionic/AOSP native window |
| 5 | Input transport | OHOS pointer-event packet adapter | Android InputTransport |
| 6 | Realm in-memory store | musl-compatible Realm | bionic Realm `.so` |
| 7 | Resources/AssetManager + XML | OHOS-side text/image/drawable | AOSP AssetManager |
| 8 | Telemetry no-throw publisher | OHOS sink (or local) | host telemetry seed |
| 9 | Crypto/TLS | Production parity | partial Conscrypt shim |
| 10 | Background scheduling | Looper/Handler/WorkManager parity | bionic Looper |
| 11 | Device/system providers | OHOS provider mapping | Android system services |
| 12 | Storage breadth | SQLite/cursor/file-locks | bionic SQLite |

These are tagged PF-451–457 in `WESTLAKE_PLATFORM_FIRST_ISSUES.md:67-97` as "Android phone proof accepted; OHOS adapter implementation remains open".

**Recommendation:** create a PF-626 OHOS sub-track, gate phone-side work on at least one OHOS surface landing per quarter, so portability doesn't keep slipping.

---

## 7. Recommended priority ordering

| Priority | Item | Why |
|---|---|---|
| **P0** | PF-630 candidate runtime deploy + soak | Unblocks PF-628; isolated change with rollback path |
| **P0** | PF-628 no-bridge re-attempt with new runtime | Removes the largest McD-specific crutch |
| **P0** | PF-632 `AlertDialog.Builder(Context,int)` runtime verification | Tiny scope; unlocks `performClick` route |
| **P1** | PF-629 `quantity_plus` then customize / cart-screen routes | Each new route surfaces the next set of framework misses |
| **P1** | PF-622 truly-green conversion | Removes a hidden technical debt before it bites |
| **P2** | PF-627 RecyclerView/Glide image-pipeline parity | UI fidelity; large but well-scoped |
| **P2** | PF-625 Realm coherence breadth | Per-table soak proves the runtime fix generalizes |
| **P2** | PF-624 explicit telemetry regression gate | Cheap insurance |
| **P3** | PF-606 Material/AppCompat | Defer until P2 surfaces specific widgets |
| **P3** | PF-607 coroutine shield removal | Risky; do diagnosis before any code change |
| **P3 (massive)** | PF-608/626 OHOS southbound (12 surfaces) | Should run in parallel as its own track |
| **P-cleanup** | N2 stale-doc pruning | Zero risk, big clarity |

---

## 8. Local fix-spree (no phone-time, isolated worktrees)

This section lists fixes that do NOT touch the phone, do NOT push commits, and stay in isolated worktrees until reviewed.

### Worktree A — stale-doc pruning (N2)
- File: `docs/program/WESTLAKE_PLATFORM_FIRST_ISSUES.md`
- Action: prune superseded red/green flips for PF-620, PF-622, PF-624, PF-625; add a top-of-file "Last update supersedes earlier sections" note
- Risk: zero (text)

### Worktree B — PF-632 `AlertDialog.Builder` verification
- Files: `shim/java/android/app/AlertDialog.java`, `aosp-shim.dex` artifact
- Action: read the shim, confirm signature, propose tiny diff if missing; do NOT exercise on phone
- Risk: tiny

### Worktree C — PF-628 observer-dispatch-gate diagnosis
- Files: anything matching `grep -rn observer_dispatch_opt_in_required`
- Action: characterize the gate; produce a written diagnosis + candidate code change; do NOT commit, do NOT run on phone
- Risk: zero (analysis only)

### Worktree D — PF-630 candidate runtime verification
- Files: `/home/dspfac/art-latest/build-ohos-arm64/bin/dalvikvm`, `patches/runtime/interpreter/interpreter_common.cc`
- Action: hash check, characterize the patch, write (do NOT execute) a dry-run sync+gate script tagged `mcd_pf630_candidate_dryrun`
- Risk: zero (no phone touch)

After all worktrees return: surface diffs, ask user for go/no-go on commit and (separately) phone deployment.

---

## 9. Phone-deploy gate list (require explicit user authorization)

These steps each require explicit user OK because they affect the phone and the only known-good baseline:

1. **PF-630 candidate runtime sync to phone** (~1 min) — overwrites `/data/local/tmp/westlake/dalvikvm`
2. **PF-630 bounded regression gate** (~10 min phone time) — verifies baseline still green
3. **PF-630 long-soak gate** (~15 min phone time) — verifies the SIGBUS is gone
4. **PF-628 no-bridge re-attempt** (~15 min phone time) — depends on 1+2+3
5. **PF-632 performClick gate** (~10 min phone time)
6. **PF-629 quantity_plus gate** (~5 min phone time)

For each, the agent prompt in §4 contains the exact env-var-prefixed command.

---

## 10. Claims that need verification before being acted on

- **N3** SharedPrefs/SQLite "in-memory only" — code-scan claim, not yet exercised
- **N4** WebView "ClassNotFoundException" — code-scan claim, not yet exercised
- One review-agent claim that "input is unwired" was **contradicted by artifact evidence** (taps demonstrably route through `MCD_PDP_DASHBOARD_POPULAR_CLICK → MCD_PDP_REAL_XML_ENHANCED → MCD_PDP_STOCK_VIEW_CLICK invoked=true`); discarded.

---

## 11. Guardrails (carry-forward from handoff)

- Do **not** weaken `scripts/check-real-mcd-proof.sh` acceptance.
- Keep McD-specific bridges labeled and removable; prefer generic shims.
- Keep `unsafe` flags off for accepted proofs; isolate unsafe probes to explicitly named artifacts.
- Update `WESTLAKE_SOUTHBOUND_API.md` whenever a new host dependency is added.
- Always create NEW commits rather than amending; never push --no-verify.
- Never deploy to the phone or push to `origin/main` without explicit user OK.

---

## 12. Cross-references

- Handoff: `docs/program/WESTLAKE_REAL_MCD_HANDOFF_20260503.md`
- Master ledger: `docs/program/WESTLAKE_PLATFORM_FIRST_ISSUES.md`
- Rally: `docs/program/WESTLAKE_REAL_MCD_48H_FULL_APP_RALLY_20260502.md`
- Southbound: `docs/program/WESTLAKE_SOUTHBOUND_API.md`
- Platform-first contract: `docs/program/WESTLAKE_PLATFORM_FIRST_CONTRACT.md`
- Issues on GitHub: `https://github.com/A2OH/westlake/issues` (PF-606=#579, 607=#580, 608=#581, 609=#582, 622=#587, 623=#589, 624=#590, 625=#591, 626=#592, 627=#593, 628=#594, 629=#595, 630=#596, 632=#597)

---

## 13. Addendum — review-session findings (2026-05-03 PT supervised fix spree)

This section records empirical findings from the 2026-05-03 PT supervised fix spree that ran immediately after this doc was first committed (`20c3b52e`). Three subagents ran in parallel; one phone gate executed against the PF-630 candidate runtime.

### 13.1 PF-632 verified — already fixed; bundling proven on phone (Agent B)

- Constructor `public Builder(Context context, int themeResId)` exists at `shim/java/android/app/AlertDialog.java:202-205` with a real generic body — wraps `context` in `ContextThemeWrapper(context, themeResId)` when `themeResId != 0`, stores `themeResId` in `mThemeResId`. The no-arg `Builder(Context)` (line 198-200) delegates to it via `this(context, 0)`.
- `aosp-shim.dex` is built by `scripts/build-shim-dex.sh` from every `*.java` under `shim/java/`. `AlertDialog$Builder` is in scope.
- `dexdump -h` on the phone-side `aosp-shim.dex` (hash `da24e7a3…`) confirms BOTH `<init>(Landroid/content/Context;)V` AND `<init>(Landroid/content/Context;I)V` are present.
- **Stale dex caveat:** `ohos-deploy/arm64-a15/aosp-shim.dex` (May 2 18:10) is missing the `(Context,int)` constructor — but it is NOT what gets pushed by `sync-westlake-phone-runtime.sh` (which uses `$REPO_ROOT/aosp-shim.dex` per `sync-westlake-phone-runtime.sh:29`). Don't accidentally publish or sync the stale dex.
- **Empirical performClick proof still pending** — there is no `WESTLAKE_GATE_PDP_FORCE_PERFORM_CLICK` env hook today; the bounded gate uses `callOnClick` by design. Adding such a hook is the cheapest way to retire PF-632.
- **Classloader precedence note** (Agent B flagged): under the dalvikvm64 path used by `run-real-mcd-phone-gate.sh`, the shim dex wins because it's first on the classpath. If the same shim were ever loaded inside a Zygote-spawned app process, the platform's `boot.art`-baked `android.app.AlertDialog` would shadow it. Not actionable now; worth knowing.

### 13.2 PF-628 — architectural reframe (Agent C)

The handoff's framing ("remove the `MCD_PDP_CARTINFO_SET_BRIDGE` so `CartViewModel` updates from natural lifecycle/LiveData") **does not match the code**. Empirical evidence from `WestlakeLauncher.java:20001-20013`:

```java
boolean observerAllowed = isMcdUnsafeObserverDispatchEnabled();   // line 20001
boolean storageAllowed  = isMcdUnsafeStorageCommitEnabled();
boolean allowed = requested && observerAllowed && storageAllowed;
...
} else if (!observerAllowed) {
    reason = "observer_dispatch_opt_in_required";                  // line 20008
}
```

`isMcdUnsafeObserverDispatchEnabled()` (`WestlakeLauncher.java:320-338`) is true only if `westlake.mcd.unsafe_observer_dispatch` system prop / env / launch-file / probe file `westlake_mcd_unsafe_observer_dispatch.txt` is set. **The gate is purely an opt-in flag check; it has nothing to do with LiveData or Lifecycle state.**

When the flag IS set, the code reflectively invokes `fragment.q7(Boolean.TRUE)` at `WestlakeLauncher.java:20040` — i.e. it fires the LiveData manually, bypassing the observer chain entirely. So:
- The gate currently emits `allowed=false reason=observer_dispatch_opt_in_required` in **both** the bounded green proof AND the no-bridge attempt (verified by grepping `MCD_PDP_OBSERVER_DISPATCH_GATE` across both artifacts: identical wording at L48537 and L48542 respectively).
- The bounded proof is "green" because of the **side-channel reflective `cartVm.setCartInfo(stockCartInfo)`** at `WestlakeLauncher.java:21059-21082`, NOT because any observer fired. Proof: `MCD_PDP_OBSERVER_DISPATCH ... invoked=true` count is 0 in both artifacts.
- The acceptance script accepts via `pdp_cartinfo_bridge_positive` / `pdp_cart_or_bag_mutated` at `scripts/check-real-mcd-proof.sh:518-519,720`. **That acceptance line is itself a McD-specific accommodation.**

**Cross-rule-out:**
- **PF-622 mislabel: NOT involved here.** `MCD_PDP_LIVEDATA_STATE` shows `lifecycleState=RESUMED fragmentResumed=true` in both artifacts (L48534/48539). Lifecycle is correctly RESUMED at gate-fire time.
- **PF-607 coroutine shield: NOT involved here.** Both runs have identical `[PFCUT-MCD] JustFlipBase.c event emission noop` shield state; neither uses the coroutine event path to drive the LiveData.

**Implications for PF-628 acceptance:**
- "Remove the bridge" without changing anything else just removes the side-channel — there is no other path to mutate the VM cart today.
- A meaningful no-bridge gate must (a) plant `westlake_mcd_unsafe_observer_dispatch.txt` (under explicit `unsafe_*` artifact naming per handoff guardrail), and (b) verify the observer dispatch actually invokes `q7(true)`, and (c) verify `BasketAPIHandler.Q → nativeSetLong` survives — the latter blocked on a working PF-630 fix.
- Long-term, PF-628 acceptance criterion in `check-real-mcd-proof.sh` should require `MCD_PDP_OBSERVER_DISPATCH ... invoked=true count >= 1`, not just `pdp_cart_or_bag_mutated`.

**Next-agent prompt for PF-628 (replaces the §4 prompt above):**
```
PRECONDITION: a working PF-630 fix is on the phone (the 2026-05-03 candidate
at /home/dspfac/art-latest/build-ohos-arm64/bin/dalvikvm REGRESSED — see §13.3).

1. Plant the unsafe opt-in probe (artifact naming MUST include "unsafe"):
     /mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -s cfb7c9e3 \
       shell 'echo 1 > /data/local/tmp/westlake/westlake_mcd_unsafe_observer_dispatch.txt'
2. Run a labeled-unsafe gate with bridge OFF:
     env ADB_BIN=… ADB_HOST=… ADB_PORT=… ADB_SERIAL=… \
       WESTLAKE_HTTP_PROXY_BASE=http://127.0.0.1:18080 \
       WESTLAKE_START_HTTP_PROXY=1 WESTLAKE_GATE_INTERACT=1 \
       MCD_PDP_CARTINFO_SET_BRIDGE=0 \
       WESTLAKE_GATE_SLEEP=600 WESTLAKE_DASH_WAIT=360 WESTLAKE_PDP_WAIT=240 \
       ./scripts/run-real-mcd-phone-gate.sh \
         mcd_unsafe_pf628_no_bridge_observer_dispatch_post_pf630
3. Acceptance (decisive grep):
     grep -c 'MCD_PDP_OBSERVER_DISPATCH .*invoked=true' \
       artifacts/real-mcd/<latest>/logcat-stream.txt
   Expected >= 1 AND no SIGBUS in BasketAPIHandler.Q AND
   pf621_final_acceptance_gate=PASS without MCD_PDP_CARTINFO_SET_BRIDGE
   applied=true having to fire.
4. Always remove the unsafe probe afterward:
     adb -s cfb7c9e3 shell 'rm -f /data/local/tmp/westlake/westlake_mcd_unsafe_observer_dispatch.txt'
5. If observer dispatch invokes but downstream still crashes in
   BasketAPIHandler.Q, the storage commit also needs unsafe opt-in
   (westlake_mcd_unsafe_storage_commit.txt). Treat that as a separate test.
```

### 13.3 PF-630 candidate runtime — REGRESSED the bounded baseline

- Candidate: `/home/dspfac/art-latest/build-ohos-arm64/bin/dalvikvm`, sha256 `20acd7b1c18493cd9a52c0cf8f67243c32459e34fc0faa2be1295cf0f43d2999` (matches handoff hash claim).
- Test: bounded regression gate `mcd_pf630_candidate_bounded_regression`, 2026-05-03 12:04:32 PT.
- Result: **`gate_status=FAIL`** at `artifacts/real-mcd/20260503_120432_mcd_pf630_candidate_bounded_regression/check-real-mcd-proof.txt`.
- Failure profile:
  - `FAIL no_fatal_failed_requirement count=2`
  - `FAIL network_bridge_or_urlconnection newrelic=0 westlake_bridge=0 urlconnection_noop=0`
  - All dashboard sections `count=0` (HERO, MENU, PROMOTION, POPULAR)
  - `FAIL strict_dashboard_frame_sparse bytes=1734 views=4`
- Diagnostic clue from logcat: repeated `java.lang.NoClassDefFoundError: Class not found using the boot class loader; no stack trace available` near app startup — suggests the patched `interpreter_common.cc` broke a boot class loading path (the original runtime resolves the same classes fine).
- **Rollback executed**: original runtime `d7e10e47…` restored from `/home/dspfac/westlake-runtime-backups/dalvikvm.pre-pf630-d7e10e47.bak`. Verified post-rollback bounded gate.
- **Implication for PF-630 issue (#596):** the candidate is not deployable as-is. The next agent on PF-630 should:
  1. Investigate why `interpreter_common.cc` patch produces `NoClassDefFoundError` for boot classes (likely a class-init / resolution path bug introduced by the patch).
  2. Reproduce the regression locally if possible (run a minimal class-resolution test against the candidate runtime).
  3. Either revise the patch or pursue an alternative root-cause fix for `OsSharedRealm.nativeCloseSharedRealm` SIGBUS.
  4. Re-run the bounded regression gate before any long-soak.
- **Backup retained** at `/home/dspfac/westlake-runtime-backups/dalvikvm.pre-pf630-d7e10e47.bak` for future rollback if anyone re-tries the candidate.

### 13.4 Stale-doc cleanup committed (Agent A)

- Commit `78d4d098` on `main` (not pushed).
- 16 single-line italic annotations on `WESTLAKE_PLATFORM_FIRST_ISSUES.md`:
  - 5× `[SUPERSEDED 2026-05-03 — see 18:10 PT update; PF-620 is green]` above earlier red entries
  - 8× `[SUBTAG COLLAPSED INTO PARENT IN 18:10 PT UPDATE]` above PF-625 sub-tag headers (file had 8, the spec listed 6 — Agent annotated all)
  - 1× `[STILL OPEN — masked by compatibility marker; see WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md §3 B5]` above the PF-622 17:08 PT entry
  - 1× `[GREEN BUT REGRESSION-PRONE; see GAP_REPORT_20260503 §4 PF-624]` above the 15:28 PT PF-624 first green-flip
  - 1× top-of-file note pointing readers at this doc
- Insertions only — no content removed or paragraphs rewritten.

### 13.5 Updated priority ordering after this session

| Priority | Item | Adjustment |
|---|---|---|
| **P0** | PF-630 runtime fix re-attempt | Candidate REGRESSED; need a different patch or different root-cause approach. Diagnose `NoClassDefFoundError` from boot class loader first. |
| **P0** | PF-628 acceptance redefinition | Agent C revealed acceptance currently passes via reflective `setCartInfo`, not observer dispatch. The `check-real-mcd-proof.sh` line `pdp_cartinfo_bridge_positive` should ultimately require `MCD_PDP_OBSERVER_DISPATCH ... invoked=true`. |
| **P0** | PF-632 empirical proof | Add `WESTLAKE_GATE_PDP_FORCE_PERFORM_CLICK` env hook to gate harness so the fix's wire-up can be verified. |
| **P1** | PF-629 quantity_plus probe | Independent of PF-630/PF-628 chain — can run on rolled-back baseline to prove cart=2. |
| Unchanged | PF-627, PF-625, PF-606, PF-607, PF-608/626 | Per §7 above. |

### 13.6 Artifacts produced this session

| Artifact | Result | Path |
|---|---|---|
| Pre-deploy runtime backup | Saved | `/home/dspfac/westlake-runtime-backups/dalvikvm.pre-pf630-d7e10e47.bak` |
| PF-630 candidate regression gate | FAIL | `artifacts/real-mcd/20260503_120432_mcd_pf630_candidate_bounded_regression/` |
| Post-rollback baseline | PASS | `artifacts/real-mcd/20260503_121537_mcd_baseline_post_pf630_rollback/` |
| PF-629 quantity_plus (handoff waits) | FAIL — PDP never loaded (lifecycle=0) | `artifacts/real-mcd/20260503_122359_mcd_pf629_quantity_plus_cart2_gate/` |
| PF-629 quantity_plus (bounded waits) | FAIL — PDP loaded, then SIGBUS at 12th basket commit | `artifacts/real-mcd/20260503_154816_mcd_pf629_quantity_plus_cart2_bounded_waits/` |

### 13.7 PF-630 candidate runtime dissection (out-of-scope PF-625 logic caused the regression)

A second-pass analysis of `/home/dspfac/art-latest/patches/runtime/interpreter/interpreter_common.cc` (uncommitted relative to art-latest `HEAD`) revealed why the candidate failed.

**Patch shape:** ~9,556 insertions across 172 files. Behaviourally significant clusters:

1. **Realm in-process emulation** (~3,000 lines, `interpreter_common.cc:+5694..+8704`): `PFCutRealmGlobalState`, `PFCutRealmTableState`, `PFCutRealmSetStringArrayResult`, `PFCutTryRealmNativeState`, `PFCutTryRealmNativeBoundaryNoop`. Targets `Lio/realm/internal/OsSharedRealm;` and `nativeCloseSharedRealm` — short-circuits the JNI boundary BEFORE crossing into librealm. **This is the actual PF-630 fix and it is independent of the regression cluster.**
2. **Stale-native-entry detection widened** (`interpreter_common.cc:53-64` and `interpreter.cc:60-67`):
   ```cpp
   static inline bool PFCutPf625EntryLooksInvalid(const void* entry) {
     const uintptr_t value = reinterpret_cast<uintptr_t>(entry);
     if (value == 0u) return false;
     return value == kPFCutPf625StaleNativeEntry ||
         value < 4096u ||
         (value >> 48u) == 0xffffu ||
         (value & 0x3u) != 0u;
   }
   ```
   Previously a single-sentinel equality check; now flags anything `< 4096`, in the kernel half, or unaligned. Three call sites in `DoCallCommon` (lines `+10994`, `+11244`) and one in `ArtInterpreterToCompiledCodeBridge` (`+9477`) react by clobbering `EntryPointFromJniPtrSize(nullptr)` and forcing the interpreter JNI dispatch.
3. **Mirror Unsafe path "bogus object" widened** in `sun_misc_Unsafe.cc` and `interpreter.cc`. Interpreter path uses `PFCutUnsafeGetObjectArraySlot` / `PFCutUnsafeSetObjectArraySlot` which call `mirror::ObjectArray::SetWithoutChecks` — typed-array element check **bypassed**.
4. **Atomic intrinsic rewrites** to `Get/SetFieldObjectVolatile` + `CasFieldObject<false>` — bypasses typed-array element check on `getAndSet` / `compareAndSet`.
5. **Lifecycle factory broad fallback gate weakened** (`+5031`): `number_of_inputs != 3u` → `< 2u`; `kotlin.reflect.KClass` substring requirement dropped.

**Empirical evidence (FAIL vs PASS comparison):**

| Marker | FAIL (candidate) | PASS (baseline) |
|---|---|---|
| `Tolerating clinit failure` lines | 11+ | 0 |
| `ArrayStoreException` mentions | 46 | 0 |
| `[PFCUT] InterpreterJni` early (08:04:43) | 100+ | 0 — first not until 08:20:07 (16 min in) |
| `[PFCUT] InterpreterJni jdk.Unsafe CAS ref` during ICU/Provider/Crypto clinit | active | not invoked |
| Dashboard sections inflated (HERO/MENU/PROMOTION/POPULAR) | 0 | 1 each |

Boot-class clinit ASE cascade in FAIL artifact (excerpt):
```
08:04:43.013  Landroid/icu/impl/ICUBinary;       ArrayStoreException
08:04:43.220  Ljava/nio/charset/CoderResult;     ArrayStoreException
08:04:43.221  Ljava/lang/VMClassLoader;          NPE (cascade from above)
08:04:43.256  Lsun/security/jca/Providers;       ArrayStoreException
08:05:15.376  Landroid/os/Build;                 ArrayStoreException
08:05:30.753  Lcom/mcdonalds/mcdcoreapp/common/Crypto;  ArrayStoreException
08:04:44.328  [WestlakeLauncher] Application error (caught): java.lang.ArrayStoreException
```

**Root cause (high confidence):** the widened `PFCutPf625EntryLooksInvalid` is **falsely flagging legitimate early-boot native JNI entries as stale**, then redirecting them through the `InterpreterJni` path. That path uses `PFCutUnsafe[Get|Set]ObjectArraySlot` which call `SetWithoutChecks` — typed-array element check bypassed. Boot-phase code (Charset/ICU/Provider/Crypto) heavily uses `Unsafe.compareAndSwapObject` on `String[]`-backed slots (`Atomic*FieldUpdater`, internal cache arrays). With the bypassed type-checked path active during boot, wrong-typed references leak into String-typed slots; the next regular `aget-object`/`aput-object` reads/writes through the typed-array path and observes the wrong element class, producing the canonical `ArrayStoreException: java.lang.String cannot be stored in an array of type java.lang.String[]` (the message wording is `dst_component_type` driven and indicates a slot whose element class is `[Ljava/lang/String;` instead of `Ljava/lang/String;`).

**Confirmation signal:** FAIL log shows `[PFCUT] InterpreterJni jdk.Unsafe CAS ref ... array_index=N success=1` IMMEDIATELY before each ASE-clinit failure. PASS log has zero `InterpreterJni` activity in the first 16 minutes — Unsafe goes through the registered FAST_NATIVE entry.

**Cross-reference with PF-630 issue body** (#596): acceptance scope is "Realm finalizer cleanup path / `OsSharedRealm.nativeCloseSharedRealm`". The stale-entry widening and interpreter JNI redirection in this candidate are **out of scope for PF-630** — they appear to be PF-625 logic broadened opportunistically. The remediation can keep the in-scope work and drop the out-of-scope changes.

**Proposed remediation (ordered by promise/risk):**

1. **Lowest risk — narrow the stale-entry test back.** Restore `PFCutPf625EntryLooksInvalid` to single-sentinel equality (`value == kPFCutPf625StaleNativeEntry`) at:
   - `art-latest/patches/runtime/interpreter/interpreter_common.cc:53-64`
   - the three reaction sites (`DoCallCommon` `+10994`, `+11244`; `ArtInterpreterToCompiledCodeBridge` `+9477`)
   - `art-latest/patches/runtime/interpreter/interpreter.cc:60-67` and the equivalent at `:487`
   - `art-latest/patches/runtime/native/sun_misc_Unsafe.cc` mirror predicate
   Approximately 10 lines changed total. Keep all the new Realm in-process emulation.
2. **If broader detection needed** for actual SIGBUS-bait entries, narrow the gate to apply only AFTER the boot ClassLoader is fully populated:
   ```cpp
   if (called_method->IsNative() && Runtime::Current()->IsStarted() && app_class_loader_seen)
   ```
   Boot classes resolve all natives via `JNI_OnLoad_javacore` (08:04:42.897 in failure log) which is detectable.
3. **Realm finalizer race itself** is already addressed by `PFCutTryRealmNativeState`/`PFCutTryRealmNativeBoundaryNoop` independently of the stale-entry widening. Keep these.
4. **Lifecycle broad fallback** restore: re-require KClass OR add a positive declaring-class match. Low risk.
5. Rebuild via `make -f Makefile.ohos-arm64` (working dir `/home/dspfac/art-latest`); deploy via `DALVIKVM_SRC=…` flag to `sync-westlake-phone-runtime.sh`; run bounded regression FIRST, then long-soak for actual PF-630 acceptance.

**Files referenced (absolute):**
- `/home/dspfac/art-latest/patches/runtime/interpreter/interpreter_common.cc`
- `/home/dspfac/art-latest/patches/runtime/interpreter/interpreter.cc`
- `/home/dspfac/art-latest/patches/runtime/native/sun_misc_Unsafe.cc`
- `/home/dspfac/art-latest/patches/runtime/native/jdk_internal_misc_Unsafe.cc`
- `/home/dspfac/art-latest/build-ohos-arm64/bin/dalvikvm` (sha256 `20acd7b1…`)
- `/tmp/pf630_diff.txt` (full candidate diff, 5074 lines, generated for analysis only)

### 13.8 PF-628 unsafe-opt-in probe (Agent C reframe empirically validated + 2 surprises)

Artifact: `artifacts/real-mcd/20260503_155711_mcd_unsafe_pf628_observer_dispatch_probe_pre_pf630/`

**Setup:** planted `/data/local/tmp/westlake/westlake_mcd_unsafe_observer_dispatch.txt` containing `1`, ran the bounded gate without disabling the bridge. Removed the probe file post-run.

**Primary finding — Agent C's two-flag reframe is empirically confirmed.** The `MCD_PDP_OBSERVER_DISPATCH_GATE` reason text changed from `observer_dispatch_opt_in_required` to **`storage_sigbus_risk`**. This proves the gate predicate is `requested && observerAllowed && storageAllowed`: planting the observer flag flipped the first clause; the storage-allowed clause now blocks. To actually fire `q7(Boolean.TRUE)` and exercise the natural observer-dispatch path, BOTH flags must be planted:
- `westlake_mcd_unsafe_observer_dispatch.txt`
- `westlake_mcd_unsafe_storage_commit.txt`

The second probe is what would empirically validate whether the dispatch path itself works — but it would also expose the PF-630 SIGBUS chain, so it requires a working PF-630 fix first.

**Surprise #1 — strongest green proof of the session.** Despite the dispatch gate still blocking, the run achieved:

| Marker | Original 17:57 baseline | This unsafe probe |
|---|---|---|
| `gate_status` | PASS | PASS |
| `pdp_add_cart_gate_status` (sub-gate) | **FAIL** (always FAIL prior) | **PASS** ← first time |
| `mcd_full_app_lifecycle_gate fragment_resumed` | 0 (PF-622 mislabel) | **12** ← first time > 0 |
| `mcd_full_app_lifecycle_gate soft_resume_recovery` | 1 | 6 |
| `mcd_full_app_cart_mutation_gate cartOrBagMutated` | 0 (failures) / partial (baseline) | **7** |
| `pf621_final_acceptance_gate` sub-criteria all green | 7 of 8 | **8 of 8** |

`MCD_PDP_CARTINFO_SET_BRIDGE applied=true` still fired (`beforeVmTotalBagCount=0 afterVmTotalBagCount=1`). The bridge wasn't disabled; both the bridge AND the cleaner lifecycle path coexisted. **Mechanism unknown without code-read** — the unsafe-observer-flag presence apparently changes launcher behavior elsewhere (perhaps it disables some defensive short-circuiting that masks the lifecycle progression). This is a real load-bearing observation worth investigating before declaring the unsafe path "merely an unsafe variant."

**Surprise #2 — proof-checker coverage gap.** `PASS proof_unsafe_flags_off markers=0 flags=0` — the checker reports zero unsafe markers despite the deliberately planted flag. The checker doesn't appear to detect `westlake_mcd_unsafe_observer_dispatch.txt` (and presumably neither does it detect `westlake_mcd_unsafe_storage_commit.txt`). **A future accepted "clean" artifact could silently have unsafe flags planted and still pass `unsafeOff=1`** — the proof-system's `no_unsafe` guarantee is incomplete. This should be a blocker-level fix to `scripts/check-real-mcd-proof.sh`.

**Implications for PF-628 acceptance criteria** (refines §13.2):

- The original "remove the bridge → observer drives the cart" model is two layers off:
  1. The observer dispatch is gated by two opt-in flags (`unsafe_observer_dispatch` AND `unsafe_storage_commit`), neither of which is planted in the bounded baseline.
  2. The bridge satisfies the cart criterion via reflective `setCartInfo` regardless of dispatch state.
- A meaningful PF-628 acceptance must require **at least one of**:
  - `MCD_PDP_OBSERVER_DISPATCH ... invoked=true count >= 1` (real observer dispatch fired), AND no McD-specific bridge marker; **or**
  - A new generic-lifecycle path that doesn't go through either the unsafe flags or the McD-specific bridge.
- The proof-checker also needs to enforce `unsafe_flags_off` properly (Surprise #2) — otherwise the unsafe path could be silently accepted as the canonical green.

**Probe cleanup verified:** `/data/local/tmp/westlake/westlake_mcd_unsafe_observer_dispatch.txt` removed post-run; `ls` confirms no such file.

### 13.9 Updated artifacts table

| Artifact | Result | Notable |
|---|---|---|
| `20260503_120432_mcd_pf630_candidate_bounded_regression` | FAIL | Boot-class ASE cascade from PF-625 widening |
| `20260503_121537_mcd_baseline_post_pf630_rollback` | PASS | Confirms rollback restored baseline |
| `20260503_122359_mcd_pf629_quantity_plus_cart2_gate` | FAIL | Short waits — PDP never loaded |
| `20260503_154816_mcd_pf629_quantity_plus_cart2_bounded_waits` | FAIL | Bounded waits — PDP loaded, then PF-630 SIGBUS at 12th basket commit |
| `20260503_155711_mcd_unsafe_pf628_observer_dispatch_probe_pre_pf630` | PASS — strongest of the session | Two-flag reframe confirmed; checker gap surfaced |

**Next-agent prompt for PF-630 (replaces the §4 prompt):**
```
PRECONDITION: art-latest at HEAD has the candidate uncommitted; the stale-entry
widening regressed the bounded baseline (see §13.7 above). Plan: revert the
out-of-scope PF-625 changes, keep the in-scope Realm emulation, rebuild, sync,
test.

1. cd /home/dspfac/art-latest; verify git status shows the modified patches.
2. Edit `patches/runtime/interpreter/interpreter_common.cc` (lines ~53-64,
   ~9477, ~10994, ~11244) to restore the single-sentinel equality:
     return value == kPFCutPf625StaleNativeEntry;
3. Apply the same revert to `patches/runtime/interpreter/interpreter.cc:60-67`
   and `:487`, and `patches/runtime/native/sun_misc_Unsafe.cc` mirror predicate.
4. KEEP the Realm in-process emulation block (lines ~5694-8704 in
   interpreter_common.cc). KEEP the AtomicReference rewrites (or revert
   separately if you want; they are not the boot-time root cause).
5. Optionally also restore the lifecycle factory broad-fallback gate to
   `!= 3u` and re-require `kotlin.reflect.KClass`.
6. make -f Makefile.ohos-arm64 (will re-link dalvikvm; check the build
   actually picks up your edits — patches/ is the source of truth).
7. sha256 the resulting build-ohos-arm64/bin/dalvikvm; that's the new
   candidate hash. Update PF-630 issue with it.
8. Rollback-prepare:
     /mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -s cfb7c9e3 \
       pull /data/local/tmp/westlake/dalvikvm \
       /home/dspfac/westlake-runtime-backups/dalvikvm.pre-pf630-v2.bak
9. Sync the new candidate via DALVIKVM_SRC=… (see existing handoff prompt).
10. Bounded regression gate FIRST. Must be PASS to proceed.
11. Long-soak gate (10 min post-cart). Must be PASS for PF-630 acceptance.
12. If bounded REGRESSES again, roll back from the v2 backup, re-baseline, and
    surface the new failure mode.
```
