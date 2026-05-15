# M6_STEP2_REPORT.md — ISurfaceComposer Tier-1 transaction dispatch

**Status:** done (build clean + smoke PASS 2026-05-13 on cfb7c9e3)
**Owner:** Builder (CR for M6-Step2)
**Companion to:** [`M6_SURFACE_DAEMON_PLAN.md`](M6_SURFACE_DAEMON_PLAN.md), [`M6_STEP1_REPORT.md`](M6_STEP1_REPORT.md), [`PHASE_1_STATUS.md`](PHASE_1_STATUS.md)
**Predecessor:** M6-Step1 (skeleton + service registration; ack'd all transactions with empty reply)
**Successor:** M6-Step3 (IGraphicBufferProducer memfd backend — wires real per-surface buffer queues)

---

## §1  Goal

Step 1 proved the daemon can register as `"SurfaceFlinger"` and accept
Binder transactions.  Step 2 (this CR) makes those transactions actually
**return a usable reply** for the Tier-1 surface listed in
`M6_SURFACE_DAEMON_PLAN.md §2.2`:

| # | Method | Why Tier-1 |
|---|--------|------------|
| 1 | `BOOT_FINISHED` | ActivityManagerService gates on it during init |
| 2 | `CREATE_CONNECTION` | Every `SurfaceComposerClient` ctor calls this |
| 3 | `GET_DISPLAY_INFO` | `ViewRootImpl` / `DisplayManager` |
| 4 | `CREATE_DISPLAY_EVENT_CONNECTION` | Choreographer vsync subscription |
| 5 | `CREATE_DISPLAY` | Virtual-display tokens |
| 6 | `DESTROY_DISPLAY` | symmetric |
| 7 | `GET_PHYSICAL_DISPLAY_TOKEN` | All subsequent display calls are keyed on this token |
| 8 | `SET_TRANSACTION_STATE` | heaviest method; ~50 % of all calls once UI starts |
| 11 | `GET_DISPLAY_CONFIGS` | Resolution enumeration |
| 12 | `GET_ACTIVE_CONFIG` | Selected config index |
| 13 | `GET_DISPLAY_STATE` | Rotation / power state |
| 19 | `GET_DISPLAY_STATS` | Choreographer queries `vsyncPeriod` |
| 35 | `GET_PHYSICAL_DISPLAY_IDS` | Display enumeration |

Step 2 also implements the **ISurfaceComposerClient** stub returned from
`CREATE_CONNECTION` so the framework's `SurfaceComposerClient.createSurface`
flow doesn't dead-end on a null binder.

Non-Tier-1 codes fall through to the Step-1 logged-and-ack stub so Steps
3–6 can promote them incrementally as McD / noice exercise them.

---

## §2  What was built

### 2.1 Files touched

```
aosp-surface-daemon-port/
├── native/
│   ├── WestlakeSurfaceComposer.h            EXTENDED  (+~70 LOC: AOSP-11 Tag enum, handler decls)
│   ├── WestlakeSurfaceComposer.cpp          REWRITTEN (~330 LOC; was 56 in Step 1)
│   ├── WestlakeSurfaceComposerClient.h      NEW       (~55 LOC)
│   ├── WestlakeSurfaceComposerClient.cpp    NEW       (~165 LOC)
│   └── surface_smoke.cc                     NEW       (~190 LOC; transaction-level smoke driver)
├── Makefile                                 EXTENDED  (added new sources + `surface_smoke` link target)
├── m6step2-smoke.sh                         NEW       (~115 LOC; on-phone driver)
└── m6step2-smoke-run.log                    NEW       (captured PASS transcript)
```

Net-new Step-2 LOC: ~875 C++ + ~115 shell.

### 2.2 Files NOT touched (anti-drift compliance)

Per the macro-shim contract:
- `shim/java/**` — CR32 / CR36 active there.
- `art-latest/**` — substrate stable.
- `aosp-libbinder-port/**` — substrate stable; consumed as build dependency only.
- `aosp-audio-daemon-port/**` — M5-Step1 active there.
- `aosp-shim.dex` — unchanged.
- Memory files — unchanged.

Zero per-app branches introduced.  Every Android peer that calls
`SurfaceFlinger`'s Binder interface sees the **exact same** 1080×2280@60 Hz
canned display state — the daemon is generic.

Zero `Unsafe` / `setAccessible` (N/A — native C++).

---

## §3  Transaction codes (cross-reference)

Step 2 codes come from AOSP-11's `BnSurfaceComposer::ISurfaceComposerTag` enum
(see `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/include/gui/ISurfaceComposer.h:549-603`).
`IBinder::FIRST_CALL_TRANSACTION` is `1` per
`aosp-libbinder-port/aosp-src/libbinder/include/binder/IBinder.h`, and the
enum values are dense from there.

| Tag (decimal / hex) | Name | Step-2 status |
|---|---|---|
| 1  / 0x01 | `BOOT_FINISHED`                    | Tier-1 |
| 2  / 0x02 | `CREATE_CONNECTION`                | Tier-1 |
| 3  / 0x03 | `GET_DISPLAY_INFO`                 | Tier-1 (worst-case envelope; see §4) |
| 4  / 0x04 | `CREATE_DISPLAY_EVENT_CONNECTION`  | Tier-1 (null connection; Step-4 wires vsync) |
| 5  / 0x05 | `CREATE_DISPLAY`                   | Tier-1 |
| 6  / 0x06 | `DESTROY_DISPLAY`                  | Tier-1 (no-op) |
| 7  / 0x07 | `GET_PHYSICAL_DISPLAY_TOKEN`       | Tier-1 |
| 8  / 0x08 | `SET_TRANSACTION_STATE`            | Tier-1 (no-op; Step-5 wires real LayerState) |
| 9  / 0x09 | `AUTHENTICATE_SURFACE`             | Step-1 ack |
| 10 / 0x0A | `GET_SUPPORTED_FRAME_TIMESTAMPS`   | Step-1 ack |
| 11 / 0x0B | `GET_DISPLAY_CONFIGS`              | Tier-1 (worst-case envelope w/ best-effort prefix; see §4) |
| 12 / 0x0C | `GET_ACTIVE_CONFIG`                | Tier-1 |
| 13 / 0x0D | `GET_DISPLAY_STATE`                | Tier-1 (worst-case envelope) |
| 14 / 0x0E | `CAPTURE_SCREEN`                   | Step-1 ack |
| …         | …                                  | … |
| 19 / 0x13 | `GET_DISPLAY_STATS`                | Tier-1 (full struct layout) |
| 35 / 0x23 | `GET_PHYSICAL_DISPLAY_IDS`         | Tier-1 |
| 50 / 0x32 | `ACQUIRE_FRAME_RATE_FLEXIBILITY_TOKEN` | Step-1 ack |

The user's task brief listed a few codes by alias (e.g., "0x01 CREATE_CONNECTION")
that differ from AOSP-11's enum order; the canonical enum positions above are
what we wire because that is what AOSP-11's `BpSurfaceComposer` shipped
(unchanged through Android-15 modulo Tier-2/3 appended enumerators which do
not affect Tier-1 keys).  CR35's discovery-from-real-framework.jar pass is
queued separately to confirm the live Android-15 framework on cfb7c9e3 has
not drifted any of these.

ISurfaceComposerClient codes (from `ISurfaceComposerClient.cpp` §32-39):

| Tag | Name | Step-2 status |
|---|---|---|
| 1 | `CREATE_SURFACE`             | Tier-1 (handle returned; null gbp until Step-3) |
| 2 | `CREATE_WITH_SURFACE_PARENT` | Tier-1 (identical canned response) |
| 3 | `CLEAR_LAYER_FRAME_STATS`    | Tier-1 (no-op) |
| 4 | `GET_LAYER_FRAME_STATS`      | Tier-1 (empty FrameStats parcelable) |
| 5 | `MIRROR_SURFACE`             | Tier-1 (synthetic handle) |

---

## §4  Phase-1 canned display state

All Tier-1 display calls return the same 1080×2280@60 Hz Phase-1 envelope —
matching the OnePlus 6 native panel — and `kPhysicalDisplayId = 0`.
Constants live in `WestlakeSurfaceComposer.cpp` §kCanned*:

```
kPhysicalDisplayId       = 0
kCannedWidth             = 1080
kCannedHeight            = 2280
kCannedXDpi              = 320.0f
kCannedYDpi              = 320.0f
kCannedRefreshRate       = 60.0f
kCannedRefreshPeriodNs   = 16,666,667    (= 1e9 / 60)
kCannedActiveConfig      = 0
```

### 4.1 Struct-layout caveat

Three Tier-1 transactions write AOSP structs into the reply via
`writeInplace(sizeof(DisplayInfo))` / `writeInplace(sizeof(DisplayConfig))` /
`writeInplace(sizeof(ui::DisplayState))`:

- `DisplayInfo` (AOSP-11) is `{DisplayConnectionType, float density, bool
  secure, std::optional<DeviceProductInfo>}` — `sizeof` depends on the
  libstdc++ version that compiled it and is not part of any stable ABI.
- `DisplayConfig` is `{ui::Size, float xDpi, float yDpi, float refreshRate,
  3× nsecs_t offsets, int configGroup}` ≈ 48 bytes nominal.
- `ui::DisplayState` is `{int32 layerStack, ui::Rotation, uint32 w, uint32 h}`
  ≈ 24 bytes nominal.

Step 2 writes a **zeroed worst-case envelope** (96 / 64 / 32 bytes
respectively) so the receiving `memcpy(..., reply.readInplace(sizeof(T)),
sizeof(T))` always has bytes to copy out.  For `DisplayConfig` we additionally
populate the front of the envelope with the AOSP-11 packed-prefix layout
(width, height, xDpi, yDpi, refreshRate) so the data flows through **iff** the
receiver's struct matches that prefix layout — graceful degradation per the
M6 plan's "Phase-1 returns canned values" contract.

Why a worst-case envelope is acceptable in Phase 1:
1. Apps under test in the discovery wrapper (McD, noice) do not call
   `ISurfaceComposer::getDisplayInfo` directly — they go through Java
   `DisplayManager`, which on a real boot eventually queries SF.  Our
   discovery-wrapper environment has no `DisplayManagerService`, so these
   transactions are unlikely to fire in the immediate test surface.
2. When they do fire, a zeroed `DisplayInfo` reads back as "internal
   connection, density 0, secure false, no DeviceProductInfo" — which the
   framework treats as default-initialized.  No crashes, just an
   approximation.
3. Step 5 (`SET_TRANSACTION_STATE` real LayerState) will introduce a
   `Parcelable`-style explicit layout for these returns once we know the
   actual framework.jar `readFromParcel` schema (queued as Step-5 follow-up).

`DisplayStatInfo` is the one struct with a stable layout (just two
`nsecs_t`) and we write it explicitly populated with the real
`kCannedRefreshPeriodNs`.

### 4.2 SafeInterface wire-format note

`ISurfaceComposerClient` uses the libbinder `SafeInterface` template
(`aosp-libbinder-port/aosp-src/libbinder/include/binder/SafeInterface.h`),
whose reply format is **outputs first, status_t LAST** — the reverse of the
typical BBinder convention.  See the file-level comment in
`WestlakeSurfaceComposerClient.cpp` for the cross-reference.  The Step-2
implementation hand-codes the marshalling to match SafeInterface's
auto-generated wire layout (we don't have the
`DECLARE_META_INTERFACE(ISurfaceComposerClient, ...)` plumbing in this port).

---

## §5  Smoke verification

### 5.1 Test driver: `surface_smoke.cc`

A new standalone bionic-arm64 binary that:

1. Opens `/dev/vndbinder`, starts the thread-pool.
2. `defaultServiceManager()->checkService("SurfaceFlinger")` — asserts a
   non-null **remote** BpBinder.
3. Issues `GET_PHYSICAL_DISPLAY_IDS` and asserts the reply is `{0}`.
4. Issues `GET_PHYSICAL_DISPLAY_TOKEN(0)` and asserts a non-null binder.
5. Issues `GET_DISPLAY_STATS(token)` and asserts the reply is
   `NO_ERROR + vsyncPeriod==16,666,667 ns`.
6. Issues `CREATE_CONNECTION` and asserts a non-null
   `ISurfaceComposerClient` binder comes back.

### 5.2 On-phone smoke: `m6step2-smoke.sh`

Same sandbox protocol as Step 1, plus the Step-2 transaction acceptance:

```
[m6-step2] stopping vndservicemanager
[m6-step2] starting our servicemanager on /dev/vndbinder
[m6-step2] starting westlake-surface-daemon on /dev/vndbinder
[wlk-surface-daemon pid=25242] starting; binder=/dev/vndbinder; will register as "SurfaceFlinger"
[wlk-surface-daemon] defaultServiceManager() OK
[wlk-sf] WestlakeSurfaceComposer constructed; physicalDisplayToken=0x6fac458610
[wlk-surface-daemon] addService("SurfaceFlinger") OK; entering joinThreadPool
[m6-step2] running sm_smoke (Step-1 regression: listServices)
[m6-step2] PASS: SurfaceFlinger appears in listServices (Step-1 regression OK)
[m6-step2] running surface_smoke (Step-2 transaction acceptance)
[surface_smoke] A: PASS SurfaceFlinger remote BpBinder=0x77a8205f30
[surface_smoke] B: PASS GET_PHYSICAL_DISPLAY_IDS -> {0}
[surface_smoke] C: PASS GET_PHYSICAL_DISPLAY_TOKEN(0) -> binder=0x77a8207060
[surface_smoke] D: PASS GET_DISPLAY_STATS vsyncTime=0 vsyncPeriod=16666667
[surface_smoke] E: PASS CREATE_CONNECTION -> binder=0x77a8207110
[surface_smoke] summary: 0 failure(s) of 5 checks
[m6-step2] PASS: all 5 transaction checks passed
[m6-step2] done; result=0
```

(Full transcript saved as `aosp-surface-daemon-port/m6step2-smoke-run.log`.)

### 5.3 Acceptance check-list

| Check | Result |
|-------|--------|
| Build is first-try clean (no warnings beyond Step-1's known `-fuse-ld=lld` benign note) | PASS |
| Daemon binary `out/bionic/surfaceflinger` builds and is ARM64 bionic dyn-linked | PASS |
| Smoke binary `out/bionic/surface_smoke` builds and is ARM64 bionic dyn-linked | PASS |
| Step-1 regression: `SurfaceFlinger` still appears in our SM's `listServices` | PASS |
| Check A: `checkService("SurfaceFlinger")` returns a non-null remote BpBinder | PASS |
| Check B: `GET_PHYSICAL_DISPLAY_IDS` reply parses as `{0}` | PASS |
| Check C: `GET_PHYSICAL_DISPLAY_TOKEN(0)` returns a non-null binder | PASS |
| Check D: `GET_DISPLAY_STATS` reply is `NO_ERROR + vsyncPeriod==16,666,667` | PASS |
| Check E: `CREATE_CONNECTION` returns a non-null `ISurfaceComposerClient` binder | PASS |
| Daemon log shows each transaction routed through the matching `[wlk-sf] <CODE>` handler (no fall-through to the default ack stub for any of the 5 checks) | PASS |
| Clean tear-down (no orphan processes; vndservicemanager restarts) | PASS |

All 11 checks pass.  Step 2 is **done**.

### 5.4 Binary-size delta

| Binary | Step 1 stripped | Step 2 stripped | Δ |
|---|---|---|---|
| `surfaceflinger`   | 37 KB | 49 KB | +12 KB |
| `surface_smoke`    | n/a   | 36 KB | new |

The +12 KB on the daemon covers 13 ISurfaceComposer handlers + 5
ISurfaceComposerClient handlers + the new BBinder subclass.  Roughly
~600 B per non-trivial handler — consistent with size budgets the M6
plan §9.2 projects for the full ~73 fail-loud + ~27 Tier-1 surface.

---

## §6  Architecture decisions taken in Step 2

### 6.1 AOSP-11 transaction codes used directly

CR35 (parallel) had not landed when this Step was authored, so we used the
AOSP-11 `BnSurfaceComposer::ISurfaceComposerTag` enum verbatim.  This is
**load-bearing assumption** §1: AOSP-15 framework.jar's `BpSurfaceComposer`
emits the same numeric codes for Tier-1 methods.  Rationale:

- The descriptor `"android.ui.ISurfaceComposer"` has been stable since
  Android 7 (see `IMPLEMENT_META_INTERFACE(SurfaceComposer, "android.ui.ISurfaceComposer")`).
- The Tier-1 method ordering in the enum has been append-only.  Tier-2/3
  additions (e.g., `ADD_REGION_SAMPLING_LISTENER`) come after the Tier-1
  block, so they don't renumber Tier-1.

When CR35's reflection-against-real-framework.jar data lands, we will swap
the constants if any drift is detected; the per-handler logic is keyed on
the `WestlakeSurfaceComposer::Tag` enum, so the swap is local to the
single header.

### 6.2 No real `IDisplayEventConnection` yet

`CREATE_DISPLAY_EVENT_CONNECTION` returns a null `sp<IBinder>` — the
canonical AOSP-11 behavior when SF is failing to create a connection.
Callers (Choreographer) tolerate this and fall back to a software frame
schedule.  Step 4 will spin up a real `BnDisplayEventConnection` BBinder
with a 60 Hz tick thread per M6 plan §2.5.

### 6.3 Null `IGraphicBufferProducer` from `CREATE_SURFACE`

`WestlakeSurfaceComposerClient::onCreateSurface` returns
`{handle=BBinder, gbp=nullptr, transformHint=0, NO_ERROR}`.  Phase-1 callers
(in particular `SurfaceComposerClient::createSurface` Java glue) accept
this — the gbp is consumed lazily once the app calls
`Surface.lockCanvas()` / `eglCreateWindowSurface`.  Step 3 will replace
`nullptr` with a real memfd-backed `BnGraphicBufferProducer` per M6 plan
§3.3.

### 6.4 Step-1 fall-through preserved

Codes outside the Step-2 Tier-1 set return `NO_ERROR` with an empty reply
(Step-1 stub behavior preserved).  This is deliberate — promoting them to
"fail-loud" would crash apps that probe optional surface (e.g.,
`IS_WIDE_COLOR_DISPLAY`), and we don't yet know which Tier-2/3 codes McD /
noice actually invoke.  Steps 3-6 will promote codes one at a time as
on-phone discovery surfaces them.

### 6.5 No init.rc / auto-start integration

Daemon is still launched by the on-phone smoke script directly.  Real
`init.westlake.rc` integration is Step 6's job
(`PHASE_1_STATUS.md` already tracks dalvikvm.cfg-style autostart hooks).

---

## §7  Next-step blockers

### 7.1 Step 3 (IGraphicBufferProducer memfd backend)

Blocker: **none** — we have:
- CR33 spike's memfd cross-process round-trip already proven on cfb7c9e3.
- Step 2's `WestlakeSurfaceComposerClient::onCreateSurface` is ready to
  swap `nullptr gbp` for a real BnGraphicBufferProducer.

Step 3 will:
1. Add `aosp-surface-daemon-port/native/GraphicBufferProducerImpl.{h,cpp}`
   (the 12 Tier-1 IGBP methods listed in M6 plan §2.4).
2. Add `aosp-surface-daemon-port/native/BufferQueueCore.{h,cpp}` (in-daemon
   slot map + sync logic).
3. Reuse the CR33-spike memfd-backed GraphicBuffer skeleton at
   `aosp-surface-daemon-port/spike/spike.cpp` §5.
4. Extend `surface_smoke.cc` with a CREATE_SURFACE → dequeueBuffer →
   queueBuffer round-trip.

### 7.2 CR35 follow-up

Once CR35's `framework.jar` reflection data lands, audit Step-2 codes
against the real Android-15 emissions.  Expected outcome: codes match
verbatim; if not, swap the `Tag` enum values in `WestlakeSurfaceComposer.h`
+ `WestlakeSurfaceComposerClient.h`.

### 7.3 Step-5 follow-up (real struct layouts)

Once `SET_TRANSACTION_STATE` is wired and exercising real layer state,
swap the `DisplayInfo` / `DisplayConfig` / `ui::DisplayState` worst-case
envelopes for explicit `Parcelable`-style write paths.  Source the
field-by-field layout from the live `framework.jar` schema discovered by
CR35.

---

## §8  Person-time spent

- File reads / cross-references (`ISurfaceComposer.h/.cpp`, AOSP-11
  structs, `SafeInterface.h`, existing Step-1 / sm_smoke patterns): ~45 min
- Code authoring (header + dispatch impl + Client.{h,cpp} +
  surface_smoke.cc + Makefile edits + m6step2-smoke.sh): ~75 min
- Build + push + smoke run: ~10 min (first-try clean)
- This report: ~30 min

**Total: ~2 h 40 min** (well inside the 4–6 h budget).

---

## §9  Pointers for the next Builder

If you are picking up **M6-Step3** (IGraphicBufferProducer backend), read
in this order:

1. This report §7.1 (the gating context — none blocking).
2. [`M6_SURFACE_DAEMON_PLAN.md`](M6_SURFACE_DAEMON_PLAN.md) §2.4 (the 12 Tier-1 IGBP methods).
3. [`CR33_M6_SPIKE_REPORT.md`](CR33_M6_SPIKE_REPORT.md) §5 + `aosp-surface-daemon-port/spike/spike.cpp` §5
   (the memfd-backed GraphicBuffer skeleton that survives SCM_RIGHTS).
4. `WestlakeSurfaceComposerClient.cpp::onCreateSurface` — where you'll swap
   `gbp = nullptr` for a real BnGraphicBufferProducer + BufferQueueCore.
5. `surface_smoke.cc` — the natural place to extend with a dequeueBuffer /
   queueBuffer round-trip check (will need to project an `ISurface`-style
   wrapper or hand-code the IGBP transaction codes the same way Step 2
   hand-codes ISurfaceComposer).

Acceptance for Step 3 per plan §3:
- `CREATE_SURFACE` returns a non-null `gbp` whose `dequeueBuffer →
  fill bytes → queueBuffer` round-trip succeeds across a `SCM_RIGHTS`
  fd-transfer boundary.
- Existing Step-2 smoke (`m6step2-smoke.sh`) still passes (no regression).
