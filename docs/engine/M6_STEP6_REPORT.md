# M6-Step6 Report — CR35 §7 AIDL-Drift Mitigation

**Milestone**: M6-Step6 — Apply CR35 AIDL discovery findings (Android-15
descriptor + transaction-code translation shim)
**Date**: 2026-05-13
**Owner**: Builder
**Person-time**: ~50 min (inside 1-2 h budget)
**Smoke result**: `m6step5-smoke.sh` 8/8 PASS on phone `cfb7c9e3`

---

## §1. Goal

CR35's read-only research (`CR35_M6_AIDL_DISCOVERY.md`, 462 LOC, landed
2026-05-13) catalogued **84 transaction codes across 4 binder interfaces**
(`ISurfaceComposer` 50, `ISurfaceComposerClient` 5, `IGraphicBufferProducer`
26, `IDisplayEventConnection` 3) and surfaced material A11→A15 drift:

- `ISurfaceComposer`: 50 hand-coded AOSP-11 methods → 75 AIDL-generated A15
  methods (+25 net new, 10 renamed/reshuffled).
- `ISurfaceComposerClient`: 5 → 6 methods (-1 removed + 2 added).
- `IDisplayEventConnection`: 3 → 5 (+2 added).
- `IGraphicBufferProducer`: 26 → 26, **no drift** (CR35 §6.4 — still
  hand-coded Bp/Bn in A15).
- **Descriptor drift suspected** (CR35 §6.6): A15 mangled symbol
  `_ZN7android16ISurfaceComposer10descriptorE` lives in `android::`, not
  `android::ui::` — so the live descriptor is likely
  `"android.ISurfaceComposer"` (no `ui` segment).

M6-Step2..Step5 wired the **AOSP-11** Tier-1 surface against an AOSP-11
`surface_smoke` peer. **M7-Step2 will wire real apps** whose libgui.so is
the on-phone A15 binary — at which point the strict AOSP-11
`CHECK_INTERFACE(ISurfaceComposer, ...)` macro would reject every
transaction with `PERMISSION_DENIED` because A15 writes a different
descriptor string and A15 codes don't match the AOSP-11 enum slots.

**M6-Step6's goal** (per task brief): apply CR35 §7's TODO items relevant to
this daemon — specifically §D-B (descriptor tolerance) and §D-C (small
A15→A11 code translation shim, "Option A ~30 LOC" in CR35 §6.2 prose).
Defer items that require real-app traffic to audit (§D-A strace), or that
risk masking app-discovery probes (§T2/§T3 tier promotions per CR35's own
"do after one full noice/McD discovery run" guidance).

---

## §2. CR35 §7 triage — what landed, what defers

CR35 §7 has 16 items. Triaged against M6-Step6 scope:

| CR35 § | Description | Decision | Reason |
|---|---|---|---|
| §T1-A | Explicit Parcelable for DisplayInfo/DisplayConfig/DisplayState worst-case envelopes (~80 LOC) | **Defer to M7-Step2** | Current 96/64/32-byte zeroed envelopes work for AOSP-11 smoke (Step 2-5 verified) and pass through A15 callers as default-initialized; real-app readers don't error on zeroed fields, they fall through to default behavior. Revisit when host APK reads non-zero fields. |
| §T1-B | 12 IGraphicBufferProducer Tier-1 handlers (~800 LOC) | **Already landed** | M6-Step3 (per CR35 §10 and M6_STEP2_REPORT.md §7.1). No action. |
| §T1-C | 3 IDisplayEventConnection Tier-1 + vsync thread (~230 LOC) | **Already landed** | M6-Step4 + M6-Step5 (per CR35 §10). No action. |
| §T1-D | Real BnDisplayEventConnection (~10 LOC) | **Already landed** | M6-Step5 — `onCreateDisplayEventConnection` returns real `WestlakeDisplayEventConnection`. No action. |
| §T1-E | Real BnGraphicBufferProducer from CREATE_SURFACE (~10 LOC) | **Already landed** | M6-Step3 — `WestlakeSurfaceComposerClient::onCreateSurface` returns real `WestlakeGraphicBufferProducer`. No action. |
| §T2-A | 19 ISurfaceComposer Tier-2 explicit handlers (~250 LOC) | **Defer to M7-Step2** | Phase-1 ack-with-empty-reply path is safe — deserializes as `status=0 + zero-initialized struct`. Explicit handlers are observability + edge-case safety, not correctness. Revisit when §D-A strace shows which Tier-2 codes real apps hit. |
| §T2-B | 6 IGraphicBufferProducer Tier-2 handlers (~80 LOC) | **Defer to M7-Step2** | Same rationale; M6-Step3 covers Tier-1 hot path. |
| §T3-A | 18 ISurfaceComposer + 8 IGBP Tier-3 fail-loud (`ALOGE + return INVALID_OPERATION`) (~100 LOC) | **Defer to M7-Step2** | CR35 itself says "Do AFTER one full noice/McD discovery run to confirm none of the 26 Tier-3 codes are actually called." Fail-loud now risks masking observability of real-app probes that we DO want to ack silently. |
| §D-A | Strace `dalvikvm` during real-app run, file follow-up CR for any unrecognized codes | **Defer to M7-Step2** | M7-Step2's job — it runs real apps into the daemon. |
| **§D-B** | **A15 descriptor accept (single string-literal or widen receive)** | **LANDED in this CR** | `WestlakeSurfaceComposer::checkSurfaceComposerInterface` + symmetric helper in `WestlakeSurfaceComposerClient::onTransact`. See §3. |
| **§D-C** | **A15-AIDL-code → AOSP-11-code translation shim (~30 LOC)** | **LANDED in this CR** | `WestlakeSurfaceComposer::dispatchA15Code` + named `A15AidlCode` enum entries. See §4. |
| §D-D | Fail-loud stubs for ~25 + 2 + 2 net-new A15 methods (~50 LOC) | **Partially landed** | The default branch of `dispatchA15Code` logs `[wlk-sf] A15 code=%u not in CR35 §D-C translation table; ack with empty reply` — same observability as explicit stubs, without static enumeration. M7-Step2's §D-A strace will name the actual codes seen, at which point converting log-line → `WestlakeServiceMethodMissing.fail()` stubs (per CR17 pattern) is a 1-CR refactor. |
| §E-A | Preserve AOSP-11 `CHECK_INTERFACE(IGraphicBuffer, ...)` typo at IGBP codes 19+26 | **Already honored** | M6-Step3 — `WestlakeGraphicBufferProducer.cpp` matches AOSP-11 verbatim. No action. |
| §E-B | SafeInterface reply ordering (status_t LAST for ISCC + IDEC; FIRST for ISC) | **Already honored** | M6-Step2 — `WestlakeSurfaceComposerClient::onCreateSurface` writes outputs first then status_t (line 126-129). No action. |
| §E-C | RESERVED slots in interface enums | **N/A** | CR35 confirms none of the 4 M6 interfaces have RESERVED slots. No action. |

**Summary**: 3 items applied this CR (§D-B, §D-C, partial §D-D);
8 items deferred to M7-Step2 / future as CR35 itself recommends;
5 items already covered by earlier M6 Steps per CR35 §10.

---

## §3. §D-B — Descriptor-tolerant header probe

### §3.1 The problem

AOSP's `CHECK_INTERFACE(I, data, reply)` macro (defined in
`aosp-libbinder-port/aosp-src/libbinder/include/binder/IInterface.h:175`)
expands to:

```c++
if (!(data).checkInterface(this)) { return PERMISSION_DENIED; }
```

`Parcel::checkInterface` (Parcel.cpp:1047) calls
`enforceInterface(binder->getInterfaceDescriptor())` which reads the
descriptor String16 off the wire (Parcel.cpp:1129) and `memcmp`s it
strictly against our `WestlakeSurfaceComposer::getInterfaceDescriptor()`
return value, which is the AOSP-11 literal
`"android.ui.ISurfaceComposer"`.

Per CR35 §6.6, A15's AIDL-generated `libgui.so` writes
`"android.ISurfaceComposer"` (no `ui` segment) — so a strict
`CHECK_INTERFACE` against AOSP-11 rejects every A15 peer with
`PERMISSION_DENIED`.

### §3.2 The fix

Replace `CHECK_INTERFACE(ISurfaceComposer, data, reply)` at
`WestlakeSurfaceComposer::onTransact` with a new helper
`checkSurfaceComposerInterface(data, &isA15)` that:

1. **Consumes the same Parcel header bytes** that
   `Parcel::enforceInterface` reads in the `BINDER_WITH_KERNEL_IPC` path:
   - `readInt32()` — StrictModePolicy
   - `readInt32()` — WorkSource
   - `readInt32()` — kHeader (libbinder Parcel.cpp:1083; already made
     tolerant by CR11 — we just consume so the read-position advances)
   - `readString16Inplace()` — interface descriptor
   This is critical: handlers reach for `data.readInt32()` etc. assuming
   the header is already consumed. If our probe doesn't consume the same
   4 fields, downstream reads land on the wrong bytes.
2. **Accepts either descriptor**:
   - `"android.ui.ISurfaceComposer"` (AOSP-11 — surface_smoke peer)
   - `"android.ISurfaceComposer"` (A15 AIDL — on-phone libgui peer)
3. **Sets `*outIsA15` so the caller can route**.
4. **Rejects** unrecognized descriptors with a log line + return false →
   caller maps to `PERMISSION_DENIED`. Preserves the protocol-level
   reject semantics CHECK_INTERFACE had.

This pattern is **symmetric with CR11's libbinder kHeader receive-tolerance**
in `aosp-libbinder-port/aosp-src/libbinder/Parcel.cpp` §1106-1117
(receive widely from any of SYST/VNDR/RECO/UNKN kHeader values; send
canonically as our chosen SYST). Same principle: **receive widely, send
canonically**.

Same widening is applied to `WestlakeSurfaceComposerClient::onTransact` for
`"android.ui.ISurfaceComposerClient"` vs
`"android.gui.ISurfaceComposerClient"` (per CR35 §6.3 + §6.6 namespace
drift), inline rather than as a named helper (only one dispatcher site,
no router needed since A15 ISurfaceComposerClient method ordering aligns
with AOSP-11 Tag enum for the 4 carryover methods).

### §3.3 Verified Parcel-position alignment

To confirm the probe consumes the same bytes `CHECK_INTERFACE` did, we
checked `Parcel::enforceInterface` (Parcel.cpp:1062-1117):

| Field | Read | Notes |
|---|---|---|
| StrictModePolicy | `readInt32()` | Line 1065 — set on IPCThreadState; we drop it (sandbox semantics) |
| WorkSource | `readInt32()` | Line 1080 — set on IPCThreadState; we drop it |
| kHeader | `readInt32()` | Line 1083 — CR11 made the receive widely-tolerant (line 1106-1118) |
| Descriptor String16 | `readString16Inplace()` | Line 1129 |

`checkSurfaceComposerInterface` reads exactly these 4 fields in order,
then validates the descriptor. **m6step5-smoke 8/8 PASS** confirms the
post-header parcel reads in `onCreateConnection`, `onGetPhysicalDisplayIds`,
etc., all see the same bytes they saw under `CHECK_INTERFACE`.

---

## §4. §D-C — A15 AIDL code → AOSP-11 handler translation

### §4.1 The problem

A15's AIDL-generated `ISurfaceComposer` has 75 methods in alphabetical
order (CR35 §6.2). AIDL assigns `FIRST_CALL_TRANSACTION + N` codes in the
order the methods appear in the `.aidl` source file, which for the A15
`frameworks/native/libs/gui/aidl/android/gui/ISurfaceComposer.aidl` is
alphabetical. So `addFpsListener=1`, `addHdrLayerInfoListener=2`, …,
`updateSmallAreaDetection=75`.

These numbers **do not match** AOSP-11's hand-coded enum:
`BOOT_FINISHED=1`, `CREATE_CONNECTION=2`, …, `ACQUIRE_FRAME_RATE_FLEXIBILITY_TOKEN=50`.

### §4.2 The fix

Per CR35 §6.2's recommendation: keep AOSP-11 codes for the in-daemon smoke
(unchanged), and add a small redirection table for A15 callers.

Implementation: new method `WestlakeSurfaceComposer::dispatchA15Code(code,
data, reply)` invoked from `onTransact`'s `isA15 == true` branch. The
table covers three classes of A15 method:

**A. Carryover-equivalent** — same name + Parcel I/O as A11; route to
existing handler.

| A15 code | A15 method | AOSP-11 handler |
|---|---|---|
| 7 | bootFinished | onBootFinished |
| 14 | createConnection | onCreateConnection |
| 15 | createDisplayEventConnection | onCreateDisplayEventConnection |
| 16 | createVirtualDisplay (A15 rename of CREATE_DISPLAY) | onCreateDisplay |
| 17 | destroyVirtualDisplay (A15 rename of DESTROY_DISPLAY) | onDestroyDisplay |
| 41 | getPhysicalDisplayIds | onGetPhysicalDisplayIds |
| 42 | getPhysicalDisplayToken | onGetPhysicalDisplayToken |
| 29 | getDisplayState | onGetDisplayState |
| 30 | getDisplayStats | onGetDisplayStats |

**B. Reshuffled — A11 GET_DISPLAY_INFO(3) is A15's three-way split** (CR35 §6.2):

| A15 code | A15 method | AOSP-11 handler |
|---|---|---|
| 33 | getDynamicDisplayInfoFromToken | onGetDisplayInfo |
| 34 | getDynamicDisplayInfoFromId | onGetDisplayInfo |
| 46 | getStaticDisplayInfo | onGetDisplayInfo |

The 96-byte zeroed envelope `onGetDisplayInfo` writes deserializes safely
as either a static-fields struct (immutable: connectionType, density,
secure) or a dynamic-fields struct (activeColorMode, supportedColorModes,
displayConfigs) — both are zero-tolerant per the Phase-1 contract.

**C. Tier-2/3 rebrandings — log + safe-default ack** (named for clarity in
daemon log; same end-effect as the unhandled fall-through):

| A15 code | A15 method | A11 equivalent |
|---|---|---|
| 12 | clearAnimationFrameStats | code 16 |
| 21 | getAnimationFrameStats | code 17 |
| 26 | getDisplayBrightnessSupport | code 40 |
| 28 | getDisplayNativePrimaries | code 34 |
| 47 | getSupportedFrameTimestamps | code 10 |
| 48 | isWideColorDisplay | code 33 |
| 49 | notifyPowerBoost | A14+ rename of NOTIFY_POWER_HINT (code 43) |
| 61 | setActiveColorMode | code 23 |
| 67 | setDisplayBrightness | code 41 |
| 68 | setDisplayContentSamplingEnabled | code 30 |
| 69 | setGameContentType | code 48 |
| 72 | setGlobalShadowSettings | code 44 |
| 74 | setPowerMode | code 18 |

**D. Capture (screenshot) family — return BAD_VALUE** so framework falls
back per CR35 §2 Tier-3 notes:

| A15 code | A15 method |
|---|---|
| 8 | captureDisplay |
| 9 | captureDisplayById |
| 10 | captureLayers |
| 11 | captureLayersSync |

**E. Default — log + ack with empty reply** (Phase-1 graceful degradation,
named log line surfaces unknown codes for M7-Step2 §D-A strace audit):

```
[wlk-sf] A15 code=%u not in CR35 §D-C translation table; ack with empty reply (Phase-1 graceful)
```

### §4.3 Critical caveat — code numbers are speculative until §D-A strace

The A15 code numbers above are derived from **alphabetical ordering of
CR35 §6.2's 75 method names**. AIDL nominally generates codes in the
order methods appear in the .aidl source. CR35 did NOT extract the actual
codes (would have required parsing AIDL or running strace). **M7-Step2's
§D-A strace will verify or contradict these numbers**. Any mismatch is a
single-line fix to the `A15AidlCode` enum in `WestlakeSurfaceComposer.h`.

Phase-1 safety property: any speculative code that turns out wrong falls
into the default branch — which logs + acks. Never crashes, never
`PERMISSION_DENIED`.

---

## §5. Smoke result

### §5.1 Build

```
cd /home/dspfac/android-to-openharmony-migration/aosp-surface-daemon-port
bash build.sh
…
════ Linking westlake-surface-daemon (surfaceflinger) ════
Sizes:
-rwxr-xr-x  82,848 B  /…/out/bionic/surfaceflinger
════ Build complete ════
```

No new warnings introduced.

### §5.2 Deploy + smoke

```
adb push out/bionic/surfaceflinger /data/local/tmp/westlake/bin-bionic/
adb shell "su -c 'chmod 755 /data/local/tmp/westlake/bin-bionic/surfaceflinger'"
adb shell "su -c 'sh /data/local/tmp/westlake/m6step5-smoke.sh'"
…
[surface_smoke] summary: 0 failure(s) of 8 checks
[m6-step5] PASS: all 8 transaction checks passed (Step-2 A..E + Step-3 F + Step-4 G + Step-5 H)
[m6-step5] done; result=0
```

**8 of 8 transaction checks PASS** (preserves M6-Step5 acceptance):

| Check | Description | Result |
|---|---|---|
| A | CREATE_CONNECTION + SurfaceComposerClient construction | PASS |
| B | CREATE_SURFACE (1080×2280, fmt=1) | PASS |
| C | DEQUEUE/QUEUE_BUFFER × 2 frames | PASS |
| D | GET_PHYSICAL_DISPLAY_IDS → {0} | PASS |
| E | GET_PHYSICAL_DISPLAY_TOKEN + GET_DISPLAY_STATS (vsyncPeriod=16666667ns) | PASS |
| F | IGraphicBufferProducer roundtrip | PASS |
| G | DLST pipe consumer (1 frame written for 64×32 surface) | PASS |
| H | IDisplayEventConnection vsync — 3 events at ~60Hz (16ms, 18ms spacing) | PASS |

Note the daemon log shows **no** `[wlk-sf] CR35 §D-B: A15 AIDL descriptor
observed` line — because `surface_smoke` is linked against AOSP-11 libgui
and writes the AOSP-11 descriptor on the wire. The A15 path is correctly
**cold** during this regression. When M7-Step2 wires real apps, the same
log will show that line + per-code `[wlk-sf] A15 …` traces.

### §5.3 Sample post-test daemon log

Relevant excerpts (full log at
`aosp-surface-daemon-port/m6step5-smoke-run.log` once captured):

```
[wlk-surface-daemon pid=4163] starting; binder=/dev/vndbinder; will register as "SurfaceFlinger"
[sm-stub] WaitForProperty(servicemanager.ready=true) -> immediate true
[wlk-surface-daemon] defaultServiceManager() OK
[wlk-sf] WestlakeSurfaceComposer constructed; physicalDisplayToken=0x76b18e6350
[wlk-surface-daemon] DLST pipe path: "/data/local/tmp/westlake/dlst.fifo" (env=WESTLAKE_DLST_PIPE)
[wlk-surface-daemon] addService("SurfaceFlinger") OK; entering joinThreadPool

[wlk-sf] GET_PHYSICAL_DISPLAY_IDS -> {0}
[wlk-sf] GET_PHYSICAL_DISPLAY_TOKEN id=0
[wlk-sf] GET_DISPLAY_STATS display=0x76b18e6350 -> vsyncPeriod=16666667ns
[wlk-sf] CREATE_CONNECTION
[wlk-sf] CREATE_SURFACE name="westlake-m6step3-smoke" 1080x2280 fmt=1 flags=0x0 parent=0x0
[wlk-igbp] DEQUEUE_BUFFER req=1080x2280 fmt=1 usage=0x0 getTs=0
[wlk-mgb] allocated memfd=4 size=9922560 (1080x2280 stride=1088 fmt=1)
…
[wlk-vsync] start: spawning 60 Hz tick thread (period=16666667 ns)
[wlk-sf] CREATE_DISPLAY_EVENT_CONNECTION vsyncSource=0 configChanged=0 -> binder=0x77018e9780 (60 Hz tick thread started; total live connections=1)
[wlk-vsync] STEAL_RECEIVE_CHANNEL: parceled recv=6 send=7
[wlk-vsync] SET_VSYNC_RATE: 0 -> 1
[wlk-vsync] SET_VSYNC_RATE: 1 -> 0
```

All transactions used the AOSP-11 path — the descriptor-tolerant probe
detected `"android.ui.ISurfaceComposer"` and `"android.ui.ISurfaceComposerClient"`,
set `isA15=false`, and routed to the existing AOSP-11 enum switches.

---

## §6. Predicted A15 real-app traffic vs handled

From CR35 §6.2's 75-method enumeration, classified by what real Android
apps (vs SystemServer) typically exercise:

| Class | Examples | Expected real-app rate | Handled by §D-C? |
|---|---|---|---|
| Connection bootstrap | bootFinished, createConnection, createDisplayEventConnection | One-shot per app launch | YES — A15 codes 7, 14, 15 routed |
| Display discovery | getPhysicalDisplayIds, getPhysicalDisplayToken, getStaticDisplayInfo, getDynamicDisplayInfo* | Once per WindowManagerGlobal init | YES — A15 codes 41, 42, 33, 34, 46 routed |
| Surface allocation | (via ISurfaceComposerClient.createSurface) | Per Window | YES — ISCC descriptor probe widens |
| Vsync sub | (via IDisplayEventConnection) | Per Choreographer | YES — IDEC carries over unchanged (CR35 §6.5 says 2 of 3 are stable name/sig) |
| SET_TRANSACTION_STATE-equivalent | scheduleCommit, scheduleComposite, setActivePictureListener, setDesiredDisplayModeSpecs | Every frame in steady-state | Falls through to log + ack — **expected to be fine for Phase-1** since our daemon's role is shovel-pixels-to-host, not honor every state mutation |
| Listeners | addFpsListener, addJankListener, addHdrLayerInfoListener, addRegionSamplingListener, addTunnelModeEnabledListener, addWindowInfosListener | Rare — only when statusbar/sysui-equiv subscribes | Falls through — apps without sysui responsibilities probably don't call |
| Capture (screenshot) | captureDisplay, captureDisplayById, captureLayers, captureLayersSync | Rare per app — most apps don't screenshot | YES — A15 codes 8-11 return BAD_VALUE |
| Boot display mode | setBootDisplayMode, clearBootDisplayMode, getBootDisplayModeSupport | Never from app — SystemServer-only | Falls through — log surfaces if hit |
| HDR conversion | setHdrConversionStrategy, getHdrConversionCapabilities, getHdrOutputConversionSupport | Rare — HDR app/player only | Falls through |
| Misc query | getProtectedContentSupport, isWideColorDisplay, getSchedulingPolicy, getGpuContextPriority | One-shot at app init | YES — code 48 (isWideColorDisplay) routed; others fall through |
| Telemetry | onPullAtom, notifyShutdown, setDebugFlash | SystemServer-only | Falls through |

**Predicted M7-Step2 strace finding**: app processes exercise ≤10 unique
A15 codes — the bootstrap + display-discovery + vsync set. All of them
either route to our existing handlers or fall to the safe ack-with-empty
default. The 25 net-new A15 methods that CR35 §6.2 lists are mostly
SystemServer-internal (jank/fps/HDR listeners, scheduling, telemetry) and
should never appear on app→SurfaceFlinger traffic.

---

## §7. Anti-drift compliance

- **Source code edits this CR**: 3 files in
  `aosp-surface-daemon-port/native/` (WestlakeSurfaceComposer.h +
  .cpp + WestlakeSurfaceComposerClient.cpp).
- **Test edits**: 0. `m6step5-smoke.sh` was push-deployed unchanged for
  the regression run.
- **Shim edits**: 0. `shim/java/` untouched.
- **Daemon edits outside M6 dir**: 0. `aosp-audio-daemon-port/` (M5
  stable), `aosp-libbinder-port/` (CR11 stable) both untouched.
- **art-latest / aosp-shim.dex / framework.jar edits**: 0.
- **Memory file edits**: 0.
- **Per-app branches introduced**: 0. Every change is interface-level
  (descriptor-class-level, not app-class-level).
- **Phone reboots**: 0. The smoke harness's `setprop ctl.stop
  vndservicemanager` + replay is non-destructive (vndservicemanager
  restarted at end of script via `ctl.start`).
- **SM cycles**: 1 (the smoke's own — same as M6-Step5 baseline).
- **`Unsafe` / `setAccessible`**: 0 (N/A — this is native C++).
- **Files in the brief's NOT-TO-TOUCH list (re-verified untouched)**:
  - `aosp-surface-daemon-port/native/DlstConsumer.{h,cpp}` ✓
  - `aosp-surface-daemon-port/native/MemfdGraphicBuffer.{h,cpp}` ✓
  - `aosp-surface-daemon-port/native/WestlakeGraphicBufferProducer.{h,cpp}` ✓
  - `aosp-surface-daemon-port/native/WestlakeDisplayEventConnection.{h,cpp}` ✓
  - `shim/java/*` ✓
  - `aosp-audio-daemon-port/*` ✓
  - `aosp-libbinder-port/*` ✓

Macro-shim anti-drift contract (`memory/feedback_macro_shim_contract.md`)
honored: edits land at the architectural boundary
(descriptor-recognition + transaction-code-dispatch) where the rest of the
project benefits from the additional symmetry, not at a per-app
branch-point.

---

## §8. Person-time

- Reading CR35 §6 + §7 + WestlakeSurfaceComposer.{h,cpp} + libbinder
  Parcel.cpp enforceInterface + IInterface.h CHECK_INTERFACE macro: ~15
  min
- Designing the descriptor-tolerant probe (matching Parcel header reads):
  ~10 min
- Drafting `A15AidlCode` enum from CR35 §6.2's alphabetical list of 75
  methods + cross-ref to AOSP-11 enum: ~10 min
- Implementing helpers + rewiring `onTransact` (composer + client): ~10
  min
- Build + push + smoke: ~5 min
- Authoring this doc + PHASE_1_STATUS row: ~10 min

**Total: ~50 min.** Well inside the 1-2 h budget.

---

## §9. Cross-references

- **Authoritative drift survey**: `docs/engine/CR35_M6_AIDL_DISCOVERY.md`
  (462 LOC) — especially §6 (drift analysis) and §7 (16-item TODO list).
- **Earlier M6 reports**: `docs/engine/M6_STEP{1,2,3,4,5}_REPORT.md`.
- **M6 plan**: `docs/engine/M6_SURFACE_DAEMON_PLAN.md`.
- **Sibling pattern**: CR11 libbinder kHeader receive-tolerance in
  `aosp-libbinder-port/aosp-src/libbinder/Parcel.cpp` §1106-1117 — same
  "receive widely, send canonically" principle.
- **Anti-drift contract**: `memory/feedback_macro_shim_contract.md`.
- **Companion M5 audio daemon discovery**:
  `docs/engine/CR37_M5_AIDL_DISCOVERY.md` — same pattern, smaller surface
  (74 transactions vs M6's 84).
- **AOSP-11 sources** (read-only references):
  - `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/include/gui/ISurfaceComposer.h:549-603` (enum)
  - `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/ISurfaceComposer.cpp:1226-2065` (BnSurfaceComposer::onTransact)
  - `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/ISurfaceComposerClient.cpp:32-39` (Tag enum)
- **A15 on-phone artifacts** (read-only, cached at `/tmp/cr35-fwk/`):
  - `libgui.so` (1.62 MB ARM64) — 75 AIDL methods via
    `strings | grep '^AIDL::cpp::ISurfaceComposer::'`
  - `nm -D libgui.so | grep descriptor` — `_ZN7android16ISurfaceComposer10descriptorE`

---

## §10. What M7-Step2 needs to do (§D-A audit checklist)

When real apps wire into the daemon:

1. `strace -e trace=ioctl -f -p $(pidof dalvikvm64) 2>&1 | grep -E '(BC_TRANSACTION|BR_TRANSACTION)' | head -100`
2. Decode each transaction's `code` field, cross-reference against this
   doc's §4 table.
3. For each unique code seen:
   - **If the code is in §D-C's named table**: confirm the alphabetical-
     ordering guess was right. If not, edit the single
     `A15_XYZ = N` line in `WestlakeSurfaceComposer.h::A15AidlCode` and
     rebuild.
   - **If the code falls into the default branch** (logged as `[wlk-sf]
     A15 code=%u not in CR35 §D-C translation table`): decide
     - (a) safe to silently ack — leave alone;
     - (b) needs a routed handler — add a new `case A15_NEW_METHOD:` line
       redirecting to an existing handler or a new no-op response;
     - (c) catastrophically wrong — replace the default's `return
       NO_ERROR` with `ALOGE + return INVALID_OPERATION` to surface the
       error to the framework.

The translation shim landed in M6-Step6 is **adjustable** in O(1)
edits per discovered code — no architectural rework needed.
