# M6_SURFACE_DAEMON_PLAN.md — `westlake-surface-daemon` scoping

**Status:** scoping (M6 prep)
**Author:** Architect agent (2026-05-12)
**Companion to:** `docs/engine/BINDER_PIVOT_DESIGN.md` §3.3/§3.6, `docs/engine/BINDER_PIVOT_MILESTONES.md` §M6
**Predecessor work:** M1 (`libbinder.so` musl/bionic), M2 (`servicemanager`), M3 (dalvikvm wired through libbinder), M4b (`WestlakeWindowManagerService` — the IWindowSession plumbing M6 receives layer geometry from)
**Reference pattern:** `aosp-libbinder-port/BUILD_PLAN.md` (M1 scoping doc), `docs/engine/M5_AUDIO_DAEMON_PLAN.md` (sibling native-daemon scoping)

This document specifies the build scaffold and acceptance criteria for the Westlake-owned surface daemon. The Builder agent who executes M6 should treat this as the work breakdown.

**M6 is the highest-risk Phase-1 milestone** (per `BINDER_PIVOT_MILESTONES.md` R3 and §M6 estimated 5-7 days). Read §5 carefully before starting.

---

## 1. Scope summary

**What M6 delivers:** a single native binary `westlake-surface-daemon` that:

1. Launches as a separate process during boot, after `servicemanager`.
2. Implements the AOSP `ISurfaceComposer` Binder service contract (the AIDL handle name is `"SurfaceFlinger"`).
3. Registers itself with our M2 servicemanager under the canonical AOSP name.
4. Accepts the transactions that AOSP's `libgui.so` / framework `Surface` / `SurfaceTexture` / `ViewRootImpl` issue via real Binder.
5. Implements the **BufferQueue producer** path enough that `ViewRootImpl`'s `relayoutWindow → dequeueBuffer → queueBuffer` cycle round-trips, delivering each queued buffer to a platform-specific surface backend.
6. **Phase 1 backend (this milestone):** the existing **DLST pipe → Compose host SurfaceView** path that today's `WestlakeRenderer` writes to. M6 reuses that mechanism — the daemon, not the renderer, becomes the new writer. (See `WESTLAKE_LAUNCHER_AUDIT.md` S30 and `WestlakeVM.kt` `0x444C5354` magic.)
7. **Phase 2 backend (deferred to M12):** OHOS **XComponent** surface (NativeXComponent + EGL/Vulkan blit) — same daemon, identical Binder surface, swap backend at compile time via `#ifdef OHOS_TARGET` (≈ 500 LOC of OHOS-specific bridge code per `BINDER_PIVOT_DESIGN.md` §3.6).

**What M6 does NOT deliver:**

- AOSP SurfaceFlinger compositor itself — we are not porting `frameworks/native/services/surfaceflinger/SurfaceFlinger.{h,cpp}` (~6353 LOC) wholesale. We write a *minimal substitute* that answers the ISurfaceComposer AIDL contract by:
  - Returning canned display info for `GET_DISPLAY_INFO` / `GET_DISPLAY_CONFIGS` (1080×2280@60Hz, density 480).
  - Owning the BufferQueue's *producer* side for client apps; the *consumer* side is in-daemon and routes buffers directly to the backend (Compose host pipe in Phase 1, XComponent in Phase 2). No real-GPU compositor — we don't *composite* multiple layers; we just *forward* the topmost-Z layer's buffers.
  - Stubbing `SET_TRANSACTION_STATE` (the heaviest method — the engine of all SurfaceComposerClient.Transaction.apply() calls); for Phase 1 we record state changes but only enforce them lazily (visible/invisible, position deltas applied to the buffer-forwarding pipeline only when relevant; opacity/transform ignored).
- HWUI / RenderThread / Skia integration — those live in dalvikvm-side `libhwui.so`. The buffers arriving at M6 are already-rasterized via that path. M6 receives `GraphicBuffer` handles and forwards their content (via SHM or direct mmap) to the backend.
- gralloc HAL — see §5; **we use a memfd-backed `GraphicBuffer` substitute**, not a vendor gralloc HAL.
- Display modes, HDR, color management, screenshot/CAPTURE_SCREEN, brightness control, vsync injection, animation frame stats, region sampling — all stubbed or fail-loud (Tier-2/3).
- The Java `IWindowManager.openSession` / `IWindowSession` plumbing — that's M4b territory. M6 receives buffers from `IGraphicBufferProducer` (set up *during* `IWindowSession.addToDisplay → ViewRootImpl.relayout` flow) but does not implement IWindowSession itself.

**Architectural placement:** see `BINDER_PIVOT_DESIGN.md` §3.3 diagram, "westlake-surface-daemon (~25 MB)". M6 is the boundary between *framework-jar Surface → libgui.so → Binder → westlake-surface-daemon* (uniform Westlake-owned plumbing) and *Compose host SurfaceView | XComponent* (platform-specific output).

---

## 2. AIDL surface analysis

### 2.1 AOSP source locations

| Artifact | AOSP path (android-11.0.0_r48 local at `/home/dspfac/aosp-android-11`) | Size |
|---|---|---|
| Header `ISurfaceComposer.h` | `frameworks/native/libs/gui/include/gui/ISurfaceComposer.h` | 609 LOC |
| Implementation `ISurfaceComposer.cpp` (BpSurfaceComposer + onTransact switch) | `frameworks/native/libs/gui/ISurfaceComposer.cpp` | ~2300 LOC |
| Header `ISurfaceComposerClient.h` | `frameworks/native/libs/gui/include/gui/ISurfaceComposerClient.h` | (~20 virtual methods — needed for createSurface/destroySurface) |
| Header `IGraphicBufferProducer.h` | `frameworks/native/libs/gui/include/gui/IGraphicBufferProducer.h` | 681 LOC, **~28 virtual methods** |
| Header `IGraphicBufferConsumer.h` | `frameworks/native/libs/gui/include/gui/IGraphicBufferConsumer.h` | (in-process side, NOT crossing binder; defer) |
| `BufferQueueCore.cpp` reference | `frameworks/native/libs/gui/BufferQueueCore.cpp` | 502 LOC |
| `BufferQueueProducer.cpp` reference | `frameworks/native/libs/gui/BufferQueueProducer.cpp` | 1681 LOC |
| `BufferQueueConsumer.cpp` reference | `frameworks/native/libs/gui/BufferQueueConsumer.cpp` | 827 LOC |
| SurfaceFlinger reference | `frameworks/native/services/surfaceflinger/SurfaceFlinger.{h,cpp}` | 6353 LOC main file + 73 supporting files (**DO NOT port wholesale**) |

**Note:** Android 11 uses hand-written Bp/Bn classes in `.cpp` files; Android 16 may have migrated parts to `.aidl`. Builder agent verifies against framework.jar version we ship — same pattern as M5. Transaction code enum is observable from `ISurfaceComposer.h:548-603` (47 codes Android 11).

### 2.2 ISurfaceComposer method count

**Total ISurfaceComposer virtual methods: ~52** (counted from `ISurfaceComposer.h` Android 11).

Transaction codes from `ISurfaceComposer.h:548-603` (Android 11 enum, in order):

| # | Code | Method | Tier |
|---|---|---|---|
| 1 | `BOOT_FINISHED` | `bootFinished()` | Tier-1 (called once at boot; ActivityManagerService gates on it) |
| 2 | `CREATE_CONNECTION` | `createConnection()` → `sp<ISurfaceComposerClient>` | **Tier-1** (first call: every Surface goes through this) |
| 3 | `GET_DISPLAY_INFO` | `getDisplayInfo(display, *info)` | **Tier-1** (ViewRootImpl + DisplayManager use this) |
| 4 | `CREATE_DISPLAY_EVENT_CONNECTION` | `createDisplayEventConnection(vsync_source, config)` → `sp<IDisplayEventConnection>` | **Tier-1** (Choreographer vsync delivery; without it animations / Choreographer dispatch stalls) |
| 5 | `CREATE_DISPLAY` | virtual display creation | Tier-3 (media projection) |
| 6 | `DESTROY_DISPLAY` | symmetric | Tier-3 |
| 7 | `GET_PHYSICAL_DISPLAY_TOKEN` | `getPhysicalDisplayToken(displayId)` | **Tier-1** (returned token is the IBinder handle for the rest of the calls) |
| 8 | `SET_TRANSACTION_STATE` | `setTransactionState(state, displays, flags, applyToken, inputWindowCommands, desiredPresentTime, ...)` — heaviest method, ~50% of all calls | **Tier-1, HEAVY** |
| 9 | `AUTHENTICATE_SURFACE` | for SurfaceView intercept | Tier-2 (return true safely) |
| 10 | `GET_SUPPORTED_FRAME_TIMESTAMPS` | timestamp fence support | Tier-2 |
| 11 | `GET_DISPLAY_CONFIGS` | resolution list | **Tier-1** (return one canned config) |
| 12 | `GET_ACTIVE_CONFIG` | currently-selected | **Tier-1** (return 0) |
| 13 | `GET_DISPLAY_STATE` | rotation/powerState | **Tier-1** |
| 14 | `CAPTURE_SCREEN` | screenshot | Tier-3 (return BAD_VALUE; framework tolerates) |
| 15 | `CAPTURE_LAYERS` | layer screenshot | Tier-3 |
| 16-17 | `CLEAR_ANIMATION_FRAME_STATS`, `GET_ANIMATION_FRAME_STATS` | animation stats | Tier-3 |
| 18 | `SET_POWER_MODE` | display power | Tier-2 (no-op return OK) |
| 19 | `GET_DISPLAY_STATS` | refresh interval, etc. | **Tier-1** (Choreographer queries) |
| 20 | `GET_HDR_CAPABILITIES` | HDR | Tier-2 (return empty) |
| 21-23 | `GET_DISPLAY_COLOR_MODES`, `GET_ACTIVE_COLOR_MODE`, `SET_ACTIVE_COLOR_MODE` | color modes | Tier-2 (single SRGB mode) |
| 24-25 | `ENABLE_VSYNC_INJECTIONS`, `INJECT_VSYNC` | test infra | Tier-3 |
| 26 | `GET_LAYER_DEBUG_INFO` | dumpsys | Tier-3 |
| 27-28 | `GET_COMPOSITION_PREFERENCE`, `GET_COLOR_MANAGEMENT` | color/composition pref | Tier-2 |
| 29-31 | `GET_DISPLAYED_CONTENT_SAMPLING_*` | content sampling | Tier-3 |
| 32 | `GET_PROTECTED_CONTENT_SUPPORT` | DRM | Tier-2 (return false) |
| 33 | `IS_WIDE_COLOR_DISPLAY` | | Tier-2 (return false) |
| 34 | `GET_DISPLAY_NATIVE_PRIMARIES` | color primaries | Tier-2 |
| 35 | `GET_PHYSICAL_DISPLAY_IDS` | enumeration | **Tier-1** (return `{0}`) |
| 36-37 | `ADD_REGION_SAMPLING_LISTENER`, `REMOVE_REGION_SAMPLING_LISTENER` | accent color sampling | Tier-3 |
| 38-39 | `SET_DESIRED_DISPLAY_CONFIG_SPECS`, `GET_DESIRED_DISPLAY_CONFIG_SPECS` | refresh rate config | Tier-2 |
| 40-41 | `GET_DISPLAY_BRIGHTNESS_SUPPORT`, `SET_DISPLAY_BRIGHTNESS` | brightness | Tier-2 |
| 42 | `CAPTURE_SCREEN_BY_ID` | screenshot by display ID | Tier-3 |
| 43 | `NOTIFY_POWER_HINT` | perf hint | Tier-2 (no-op) |
| 44 | `SET_GLOBAL_SHADOW_SETTINGS` | shadow rendering pref | Tier-2 |
| 45-46 | `GET_AUTO_LOW_LATENCY_MODE_SUPPORT`, `SET_AUTO_LOW_LATENCY_MODE` | gaming low-latency | Tier-3 |
| 47-48 | `GET_GAME_CONTENT_TYPE_SUPPORT`, `SET_GAME_CONTENT_TYPE` | gaming HDMI hint | Tier-3 |
| 49 | `SET_FRAME_RATE` | per-window refresh rate | Tier-2 (no-op return OK) |
| 50 | `ACQUIRE_FRAME_RATE_FLEXIBILITY_TOKEN` | refresh rate gating | Tier-3 |
| 51 | `AUTHENTICATE_SURFACE` (alt-named) | | Tier-2 |
| 52 | (Android-16 additions; verify via reflection) | | Tier-2/3 |

**Tier-1 count: ~10 ISurfaceComposer methods + ~5 ISurfaceComposerClient methods + ~12 IGraphicBufferProducer methods = ~27 methods.**

### 2.3 ISurfaceComposerClient (per-app connection)

After `CREATE_CONNECTION`, the client interacts mostly through `ISurfaceComposerClient`. ~20 methods total; Tier-1 subset:

- `createSurface(name, w, h, format, flags, parent, layerStack, ...)` → returns `sp<IGraphicBufferProducer>` (THE BufferQueue) — **Tier-1**, the call that allocates a layer
- `destroySurface(layer)` — Tier-1
- `clearLayerFrameStats`, `getLayerFrameStats` — Tier-3
- `getTransformHint` — **Tier-1** (returns rotation hint; ViewRootImpl uses for buffer rotation)

### 2.4 IGraphicBufferProducer — the BufferQueue producer side

This is THE critical interface for M6. The dalvikvm-side `Surface` is a thin wrapper over an `IGraphicBufferProducer` proxy; every frame the app draws goes through this binder object. **~28 virtual methods** (per `IGraphicBufferProducer.h`), counted from grep on the AOSP header.

Tier-1 subset (the hot path):

| # | Method | Reason |
|---|---|---|
| 1 | `requestBuffer(slot, *buf)` → `GraphicBuffer` handle | Called after dequeueBuffer to materialize the actual buffer for the slot |
| 2 | `dequeueBuffer(*slot, *fence, w, h, format, usage, ...)` | Client asks for a free buffer to draw into |
| 3 | `queueBuffer(slot, QueueBufferInput, *QueueBufferOutput)` | Client returns finished buffer to BufferQueue |
| 4 | `cancelBuffer(slot, *fence)` | Symmetric to queue (no frame produced) |
| 5 | `connect(IProducerListener, api, producerControlledByApp, *output)` | Establish producer side connection |
| 6 | `disconnect(api, mode)` | Symmetric to connect |
| 7 | `setMaxDequeuedBufferCount(n)` | Pre-allocate buffer count (typically 3 for triple-buffered) |
| 8 | `setAsyncMode(async)` | sync vs async (we run async — non-blocking enqueue) |
| 9 | `query(what, *value)` | Get queue properties: width, height, format, etc. |
| 10 | `setGenerationNumber(n)` | Buffer generation gen-counter |
| 11 | `setDequeueTimeout(ns)` | Block timeout |
| 12 | `getConsumerName()` | Debug |

The remaining methods (`detachBuffer`, `attachBuffer`, `allocateBuffers`, `setSidebandStream`, `setSharedBufferMode`, etc.) are Tier-2/3 — implement fail-loud for now.

### 2.5 IDisplayEventConnection — vsync

After `CREATE_DISPLAY_EVENT_CONNECTION`, the framework gets back an `IDisplayEventConnection`. The client side calls `setVsyncRate(int)` to subscribe. The daemon then has to push vsync events back to the client via a file descriptor (BitTube). **Without this, Choreographer doesn't tick, and rendering stalls.**

For Phase 1: implement a minimal **software vsync** — a separate thread in M6 that wakes every 16.6 ms and writes vsync events to each subscribed BitTube fd. Trivial; ~60 LOC. The Compose host SurfaceView naturally vsyncs on the phone; we don't need to be cycle-accurate, just provide a steady tick.

### 2.6 Summary

**Total Tier-1 surface for M6:** ~10 ISurfaceComposer + ~3 ISurfaceComposerClient + ~12 IGraphicBufferProducer + ~2 IDisplayEventConnection = **~27 methods + 1 vsync thread**.

**Total fail-loud overrides:** ~40 ISurfaceComposer + ~17 ISurfaceComposerClient + ~16 IGraphicBufferProducer = **~73 fail-loud stubs**.

This is roughly 2× the M5 audio daemon surface — consistent with `BINDER_PIVOT_DESIGN.md` §3.6 sizing (5K LOC vs 3K LOC for audio).

---

## 3. Build approach

### 3.1 Language and ABI

**C++**, NOT Java — same rationale as M5. Phase 1 bionic-arm64 (OnePlus 6), Phase 2 musl-arm64 (OHOS).

### 3.2 Source organization

**New directory `aosp-surface-daemon-port/`**, peer to `aosp-libbinder-port/` and `aosp-audio-daemon-port/`. Same justification as M5 §3.2 (Option A).

### 3.3 Files the Builder will create (~10-15 .cpp)

```
aosp-surface-daemon-port/
├── BUILD_PLAN.md
├── Makefile                              (~200 LOC; mirror aosp-libbinder-port/Makefile)
├── build.sh
├── aosp-src/                             (subset of frameworks/native/libs/gui)
│   ├── ISurfaceComposer.cpp              (verbatim AOSP; Bp/Bn skeletons + onTransact switch)
│   ├── ISurfaceComposerClient.cpp
│   ├── IGraphicBufferProducer.cpp
│   ├── IGraphicBufferConsumer.cpp        (only the parcelable parts)
│   ├── IDisplayEventConnection.cpp
│   ├── BitTube.cpp                       (vsync event channel transport)
│   ├── DisplayInfo.cpp                   (parcelable)
│   ├── LayerState.cpp                    (parcelable for SET_TRANSACTION_STATE)
│   ├── DisplayState.cpp                  (parcelable)
│   └── include/gui/...                   (headers)
├── deps-src/
│   ├── ui-headers/                       (frameworks/native/libs/ui — headers only for GraphicBuffer, Rect, Region, Fence types)
│   ├── ui-stubs/
│   │   ├── GraphicBuffer-memfd.cpp       (~250 LOC; our memfd-backed substitute for AOSP's GraphicBuffer; see §5)
│   │   ├── Fence-stub.cpp                (~80 LOC; no GPU fences in Phase 1 — return immediately-signaled fence)
│   │   ├── Region.cpp                    (verbatim — pure data structure)
│   │   ├── Rect.cpp                      (verbatim)
│   │   └── PixelFormat.cpp               (verbatim)
│   └── ui-include/                       (matching headers)
├── patches/
│   ├── 0001-drop-gralloc-hal.patch       (replace `GraphicBuffer::alloc(...)` calls with memfd substitute)
│   ├── 0002-stub-gpu-fence.patch         (immediate-signal fences)
│   └── 0003-skip-binder-shell-test-deps.patch (a few cosmetic AOSP utilities we don't need)
├── src/
│   ├── main.cpp                          (~120 LOC; ProcessState + addService + joinThreadPool)
│   ├── SurfaceComposerImpl.cpp           (~700 LOC; BnSurfaceComposer; 10 Tier-1 + ~40 fail-loud)
│   ├── SurfaceComposerImpl.h
│   ├── SurfaceComposerClientImpl.cpp     (~250 LOC; BnSurfaceComposerClient; 3 Tier-1 + ~17 fail-loud)
│   ├── SurfaceComposerClientImpl.h
│   ├── GraphicBufferProducerImpl.cpp     (~800 LOC; BnGraphicBufferProducer; 12 Tier-1 + ~16 fail-loud; THE BufferQueue producer side)
│   ├── GraphicBufferProducerImpl.h
│   ├── BufferQueueCore.cpp               (~400 LOC; in-daemon producer/consumer state; slot map; sync logic)
│   ├── BufferQueueCore.h
│   ├── DisplayEventConnection.cpp        (~150 LOC; BnDisplayEventConnection; vsync subscription)
│   ├── DisplayEventConnection.h
│   ├── VsyncThread.cpp                   (~80 LOC; 60 Hz tick thread; per-connection BitTube write)
│   ├── VsyncThread.h
│   ├── LayerState.cpp                    (~200 LOC; in-daemon layer map; receives SET_TRANSACTION_STATE deltas)
│   ├── LayerState.h
│   ├── SurfaceBackend.h                  (~50 LOC; abstract interface — see §4)
│   ├── DlstPipeBackend.cpp               (~300 LOC; Phase-1 — write DLST opcode frames to stdout pipe, with magic 0x444C5354)
│   ├── DlstPipeBackend.h
│   ├── XComponentBackend.cpp             (~400 LOC; Phase-2 — STUB FOR M6, IMPLEMENTED IN M12)
│   ├── XComponentBackend.h
│   ├── FailLoud.cpp                      (~30 LOC; ServiceMethodMissing analog)
│   └── FailLoud.h
└── test/
    ├── surface_smoke.cpp                 (~100 LOC; connect to daemon, dequeue+queue 30 buffers, no Binder client-side complexity)
    ├── surface_binder_smoke.cpp          (~150 LOC; full IGraphicBufferProducer round-trip)
    ├── SurfaceTest.java                  (~80 LOC; dalvikvm-side dex that uses real framework Surface)
    └── run-surface-tests.sh
```

**Total estimated daemon source: ~5-6 K LOC C++ + ~400 LOC build infra.** Matches `BINDER_PIVOT_DESIGN.md` §3.6's "~5 K C++".

### 3.4 Build flow

Same shape as M5 §3.4 but with more aosp-src files and the ui-stubs subdirectory.

**Linker dependencies (Phase 1 bionic):**

```
$(CXX) ... -o westlake-surface-daemon \
    [.o files of src/* + aosp-src/* + deps-src/ui-stubs/*] \
    -L../aosp-libbinder-port/out/bionic -lbinder \
    -L../aosp-libbinder-port/out/bionic -lutils_binder \
    -L$(NDK_SYSROOT)/usr/lib/aarch64-linux-android -llog -landroid \
    -lc -lm -ldl -lpthread
```

Phase 1 does NOT link to `-lgui` (libgui.so) — we own those AIDL stubs ourselves now. We DO link `-landroid` (for `ATrace_*`, debug-friendly NDK calls).

For Phase 2: `-lace_ndk` + `-lace_napi` (OHOS XComponent NDK).

**Expected stripped size:** ~10 MB.

### 3.5 Process model

Same shape as M5 §3.5. Key differences:

- Adds a dedicated **vsync thread** (60 Hz timer + per-connection BitTube write).
- Adds a dedicated **buffer-forwarding thread** (consumer-side of every BufferQueue → backend write). One thread per layer keeps things simple Phase 1; threading consolidation is Phase 3 work.
- **Boot order matters more than for M5:** the daemon MUST be up before any framework.jar code calls `ServiceManager.getService("SurfaceFlinger")`. AOSP framework caches this lookup in `SurfaceComposerClient::getDefault()`-style globals — if the first lookup returns null, subsequent lookups also return null (the cached null is sticky). Builder MUST verify the orchestrator starts M6 before dalvikvm boots.

---

## 4. Backend abstraction

### 4.1 Interface (~50 LOC `SurfaceBackend.h`)

```cpp
class SurfaceBackend {
public:
    virtual ~SurfaceBackend() = default;

    // One-time probe.
    virtual bool probe() = 0;

    // Called when daemon receives createSurface — backend records the layer
    // and prepares a destination (e.g. opens the DLST pipe FD, allocates an
    // XComponent NativeWindow).
    virtual status_t createLayer(uint32_t layerId, int32_t w, int32_t h,
                                  int32_t format) = 0;
    virtual status_t destroyLayer(uint32_t layerId) = 0;

    // Called from a per-layer consumer thread when a buffer was queued
    // by the producer (dalvikvm-side app frame ready). data points into
    // the GraphicBuffer's mmap'd region. The backend is expected to consume
    // it synchronously (Phase 1: write to DLST pipe; Phase 2: blit into
    // XComponent NativeWindow buffer).
    virtual status_t presentBuffer(uint32_t layerId,
                                    const void* data, size_t bytes,
                                    int32_t w, int32_t h, int32_t stride,
                                    int32_t format,
                                    nsecs_t presentTimeNs) = 0;

    // For setTransactionState — backend may need to know layer Z-order, visibility, position deltas.
    virtual void setLayerVisible(uint32_t layerId, bool visible) = 0;
    virtual void setLayerZOrder(uint32_t layerId, int32_t z) = 0;
    virtual void setLayerPosition(uint32_t layerId, int32_t x, int32_t y) = 0;

    // Vsync source — return a monotonic-ns timestamp to attach to vsync events.
    virtual nsecs_t now() const = 0;

    static std::unique_ptr<SurfaceBackend> make();
};
```

Factory selects at compile time the same way as M5 §4.1.

### 4.2 Phase 1 — DLST pipe backend

The current Westlake rendering chain (pre-pivot) is:

```
dalvikvm WestlakeRenderer.draw()
       ↓ (serialize View tree to DLST opcodes, magic 0x444C5354)
       ↓ stdout
Compose host (WestlakeVM.kt:1850 onwards)
       ↓ reads pipe, parses opcodes
       ↓ SurfaceView.lockCanvas() / Skia.draw()
       ↓
SurfaceView on phone screen
```

M6 Phase 1 **keeps the Compose host side unchanged**. What changes is the writer: instead of `WestlakeRenderer.draw()` writing DLST opcodes, the M6 daemon writes them — but now driven by real `IGraphicBufferProducer.queueBuffer` calls from inside dalvikvm framework.jar's `ViewRootImpl.relayoutWindow` path.

Two sub-paths considered:

**Path A — DLST passthrough:** the daemon receives a fully-rasterized buffer (RGB pixels) and emits a DLST `RAW_BITMAP` opcode containing the buffer's pixel data. Compose host SurfaceView blits the pixels. Pro: dead simple; reuses 100% of existing host infrastructure. Con: bandwidth — 1080×2280×4 bytes × 60 FPS = ~600 MB/s through the pipe.

**Path B — Render the DLST opcodes daemon-side from layer composition:** the daemon, knowing all layers' GraphicBuffer contents, emits high-level DLST opcodes (DRAW_RECT, DRAW_TEXT, etc.). Pro: bandwidth-efficient. Con: requires re-decoding the pixel-rasterized GraphicBuffer back into vector commands — impossible. **Disqualified.**

**Recommendation: Path A.** Phase 1 acceptance is "visual proof of pixels," not "60 FPS." The pipe-bandwidth concern is mitigated by:
- Send only **changed** scanlines (the BufferQueue's QueueBufferInput.crop region is a hint).
- Lazy-compress with `zstd --fast=10` on the pipe writer side, `zstd --decode` on the reader side (~100 LOC each); 4× bandwidth reduction without measurable latency cost.
- If still slow: bandwidth limits *throughput*, not *pixel correctness* — visible "first frame" landing is the M6 acceptance gate. FPS optimization is M12 / Phase 3 work.

Estimated DlstPipeBackend.cpp: **~250-300 LOC** (including the optional zstd compression path).

### 4.3 Phase 2 — OHOS XComponent backend (deferred to M12)

OHOS XComponent (interface at `/home/dspfac/openharmony/interface/sdk_c/arkui/ace_engine/native/native_interface_xcomponent.h`) exposes a `NativeWindow*` from which the backend can:

- `OH_NativeWindow_NativeWindowRequestBuffer(window, &buffer, &fenceFd)`
- Copy our queued-buffer pixels into the XComponent's buffer
- `OH_NativeWindow_NativeWindowFlushBuffer(window, buffer, fenceFd, ...)`

This is a direct blit — no DLST encoding, no pipe — making Phase 2 *faster than Phase 1*. Architecture validates: Phase 1's pipe is a Phase-1-only workaround for the Android-side test environment, Phase 2 cleans up to the canonical NativeWindow path.

Estimated XComponentBackend.cpp: **~300-400 LOC** when implemented in M12.

### 4.4 Compile-time selection

Identical to M5 §4.4 — `-DWESTLAKE_PHASE1_DLSTPIPE=1` vs `-DWESTLAKE_OHOS_TARGET=1` selects the backend at link time.

---

## 5. Shared memory / GraphicBuffer / gralloc — THE HARDEST PART

**This is the #1 risk for M6** (see `BINDER_PIVOT_MILESTONES.md` R3 — "BufferQueue / gralloc complexity blows M6 schedule"). The audio path's MemoryHeapBase/IMemory is *trivial* by comparison.

### 5.1 What AOSP does

AOSP `Surface.dequeueBuffer` chain:

1. Client `Surface::dequeueBuffer` → `IGraphicBufferProducer::dequeueBuffer` Binder call.
2. SurfaceFlinger's BufferQueue tracks ~3 slots; finds a free one; if slot's buffer is uninitialized OR was reallocated, returns slot index *only*.
3. Client calls `IGraphicBufferProducer::requestBuffer(slot, &buf)` → returns the `sp<GraphicBuffer>` for that slot.
4. `GraphicBuffer` wraps a `native_handle_t` containing N file descriptors + metadata (width, height, format, stride).
5. Buffer was allocated by **gralloc HAL** (`hardware/gralloc/<vendor>.so`) when the slot was first populated. Backing store is a vendor-allocated ION region, DMA-buf, GPU heap, etc.
6. Client `mmap`s the buffer's first fd at the offset gralloc declared, gets a pointer into shared physical memory that the GPU and the CPU can both touch.
7. Client renders into that pointer (via libhwui / Skia), then `IGraphicBufferProducer::queueBuffer(slot, ...)`.
8. SurfaceFlinger composes that buffer (`BufferQueueConsumer::acquireBuffer`) and reads via gralloc's `lock`/`unlock`.

**We cannot use gralloc** — it's a vendor HAL chained through `hardware/libhardware/`. The whole point of M6 is to *bypass* the system SurfaceFlinger / gralloc chain.

### 5.2 What we substitute

**memfd-backed GraphicBuffer.** Same approach as M5's audio cblk:

- At `dequeueBuffer` time, if the slot is unallocated, allocate via:
  ```cpp
  int fd = memfd_create("westlake-gbuf", MFD_CLOEXEC | MFD_ALLOW_SEALING);
  ftruncate(fd, height * stride * bytes_per_pixel);
  fcntl(fd, F_ADD_SEALS, F_SEAL_SHRINK | F_SEAL_GROW);
  ```
- Wrap that fd in a custom `GraphicBuffer-memfd.cpp` that synthesizes a `native_handle_t` with `numFds=1, numInts=4` (width, height, stride, format).
- The Binder marshaling of `requestBuffer` already supports `native_handle_t` (it dups fds across processes — kernel binder handles fd table replication).
- Client-side libgui receives the `native_handle_t`, mmaps fd 0 — same code path as for a real gralloc buffer. **Critical:** the client doesn't *know* it's memfd — libgui's `GraphicBuffer::lock` just calls `mmap(.. fd, 0)`, which works regardless of fd origin.

**Format support:** Phase 1 supports only RGBA_8888 and RGBX_8888 (4 bytes-per-pixel formats — what HWUI produces by default). Other formats (YUV, RGB_565) fail-loud — noice's UI is RGBA_8888 throughout.

**Strides:** memfd has no alignment constraint; the daemon picks `stride = align(width, 64) * 4` to match HWUI's expectations. The actual buffer is exactly that big; no padding mystery.

**Fences:** in Phase 1 we run with **no real GPU fences** — every buffer is "immediately signaled." The `Fence-stub.cpp` returns `Fence::NO_FENCE` (sentinel value) for every dequeue/queue. This means producer-consumer sync is **sequential, not pipelined** — slightly higher latency but no correctness risk. Phase 2 may need to wire real EGLFence / Vulkan fence support; defer.

**Acceptance check:** M4-PRE7 (AssetManager natives) already proved memfd-backed mmap works on OnePlus 6 kernel. M5's audio cblk reuses the same path. The MemoryHeapBase + ashmem-host.cpp already cross-compiled in `aosp-libbinder-port/out/bionic/libbinder.so` and known-working. M6 adds memfd-backed `GraphicBuffer` (the *typed* wrapper) — ~250 LOC of new code in `GraphicBuffer-memfd.cpp`.

### 5.3 What can go wrong

| Concern | Severity | Mitigation |
|---|---|---|
| HWUI's RenderThread sets specific `GraphicBuffer::USAGE_*` flags (USAGE_HW_RENDER, USAGE_HW_TEXTURE) that imply GPU access | High | Phase 1: ignore the flags (we're CPU-mmap-only). HWUI will work as long as `mmap(MAP_SHARED)` succeeds — it does on memfd. The "hw" prefix is honored by AOSP's gralloc to mean "back this with a GPU-coherent region;" memfd is not GPU-coherent but HWUI's *software* rendering paths still work. **Risk:** if framework.jar's HWUI insists on EGL surface allocation paths instead of CPU mmap, Phase 1 fails — verify by enabling HWUI software rendering globally via system property `debug.hwui.renderer=skiavk-non-gpu` or, defensively, the env var `LIBHWUI_USE_GPU=0`. |
| GraphicBuffer ABI variation between Android 11 and Android 16 | Medium | The `native_handle_t` ABI is stable across Android versions (it's a kernel-touched data structure). `GraphicBuffer`'s constructor signature has changed; we own the substitute, so we provide constructors matching whatever framework-shipped libui expects (verify at compile time against framework.jar's GraphicBuffer.aidl + classes2.dex disassembly). |
| `BufferQueue` slot count expected to be exactly 3 | Low | Phase 1: hardcode 3 slots. Any value 2-8 works for noice. |
| Producer/consumer dequeue blocking semantics ("BufferQueue is empty" return codes) | Medium | Implement faithfully per AOSP `BufferQueueProducer.cpp:dequeueBuffer` semantics. Reference impl at `frameworks/native/libs/gui/BufferQueueProducer.cpp` is 1681 LOC; our slim version is ~400 LOC because we omit detach/attach/sideband flows. |

### 5.4 Decision

**Use memfd. Same path as M5. ~250 LOC of new `GraphicBuffer-memfd.cpp` + ~50 LOC of `native_handle_t` glue. No new kernel work. No gralloc HAL.**

The 5-7 day M6 budget per `BINDER_PIVOT_MILESTONES.md` §M6 includes 1-2 days specifically for this risk; the architectural reduction to memfd-backed buffers is what made the budget feasible.

---

## 6. Bringup sequence

```
1. servicemanager (M2) starts.

2. westlake-audio-daemon (M5) starts.

3. westlake-surface-daemon (M6) starts.
   - SurfaceBackend::make() → DlstPipeBackend (Phase 1).
   - DlstPipeBackend::probe() opens stdout as DLST pipe writer.
   - Daemon spins up VsyncThread (60 Hz tick → per-connection BitTube fd write).
   - Daemon spins up its consumer threads (one per layer; none yet — created
     lazily at first queueBuffer per layer).
   - defaultServiceManager()->addService("SurfaceFlinger", svc).
   - Logs "registered SurfaceFlinger".
   - joinThreadPool.

4. dalvikvm (M3) boots.
   - At ServiceManager.getService("SurfaceFlinger"): SUCCESS (M6 found).
   - libgui.so init succeeds.
   - First `WindowSession.addToDisplay` (M4b) triggers
     `ViewRootImpl.relayoutWindow → ISurfaceComposer.createConnection
     → ISurfaceComposerClient.createSurface → IGraphicBufferProducer for that surface`.
   - HWUI draws first frame; `Surface.queueBuffer` arrives at M6.
   - M6's DlstPipeBackend writes a DLST `RAW_BITMAP` opcode with the buffer's
     pixels, magic 0x444C5354, to stdout.
   - Compose host (WestlakeVM.kt) reads the pipe, blits pixels to SurfaceView.

5. Acceptance: noice's MainActivity is visible (or at least, its background +
   AppBarLayout) on the phone screen, driven by real binder ISurfaceComposer
   traffic from dalvikvm.
```

**Diagrammatically (extension of `BINDER_PIVOT_DESIGN.md` §3.3):**

```
              dalvikvm
            +----------+
            |framework |   ViewRootImpl.relayout → Surface.queueBuffer
            |.jar      |        |
            +----------+        v
                  |        +-----------+
                  |        |GraphicBuf |     ← memfd-backed
                  |        | mmap     |
                  v        +-----------+
            +----------+        |
            |libgui.so |        |
            +----------+        |
                  |             |
            ISurfaceComposer   IGraphicBufferProducer       [/dev/vndbinder]
                 binder                  binder
                  |                        |
            +-----+------------------------+---+
            |     westlake-surface-daemon       | (M6)
            |     +---------------+              |
            |     |SurfaceComposer|              |
            |     | Impl          |              |
            |     +-------+-------+              |
            |             v                      |
            |     +----------------+   per-layer consumer thread
            |     |BufferQueueCore | <-- dequeue/queue ring
            |     +-------+--------+              |
            |             v                      |
            |     +---------------+              |
            |     | SurfaceBackend |              |
            |     | (DLST | XComp) |              |
            |     +-------+-------+              |
            +-------------+----------------------+
                          v
              ┌───────────────┐
              │ Compose host  │  (Phase 1)  - DLST pipe → SurfaceView
              │ OR XComponent │  (Phase 2)  - direct NativeWindow blit
              └───────────────┘
                          v
                     phone screen
```

---

## 7. Acceptance tests

### 7.1 Smoke 1 — standalone backend test (no Binder)

**Source:** `aosp-surface-daemon-port/test/surface_smoke.cpp` (~100 LOC).

**Body:**
- `SurfaceBackend::make()`
- `createLayer(1, 1080, 2280, RGBA_8888)`
- For i in 0..30: allocate a 1080×2280×4-byte buffer in memfd, fill with `gradient(i)`, call `presentBuffer(1, ...)`
- `destroyLayer(1)`
- Exit 0.

**Pass criteria:** 30 frames of a moving gradient visible on the phone screen via Compose host. Verifies backend pipeline without Binder.

### 7.2 Smoke 2 — Binder-backed daemon test

**Source:** `aosp-surface-daemon-port/test/surface_binder_smoke.cpp` (~150 LOC).

**Body:**
- Start daemon as child.
- `defaultServiceManager()->getService("SurfaceFlinger")`
- `ISurfaceComposer::Stub.asInterface`
- Call `createConnection` → get `sp<ISurfaceComposerClient>`
- Call `createSurface(...)` → get `sp<IGraphicBufferProducer>` (= surface's BufferQueue)
- 30 iterations: dequeueBuffer → mmap → fill gradient → queueBuffer
- destroySurface, kill daemon.

**Pass criteria:** same visual as 7.1, but driven through real Binder. Demonstrates end-to-end buffer transport.

### 7.3 Integration — dex driving framework Surface

**Source:** new dex in `aosp-surface-daemon-port/test/SurfaceTest.java` (~80 LOC; built via `build_surface_test.sh` mirroring HelloBinder pattern).

**Body:**
- Class init triggers libgui/libhwui native loading (paths already handled via M4-PRE7 / M5-PRE for related natives; verify M6's needs).
- Construct a `SurfaceTexture` + `Surface` pair, drive `lockCanvas → drawColor → unlockCanvasAndPost` for 30 frames.
- Verify each frame routes through real Binder to M6.

**Pass criteria:** real framework code path; same visual; logs from M6 show 30 queueBuffer transactions per second.

### 7.4 Integration — noice's MainActivity displays a non-empty frame

This is M7-territory but the **specific acceptance bar for M6** is: *noice's MainActivity gets past ViewRootImpl.draw() without crashing, and visible content (any color, any shape) ends up on the phone screen via M6.*

Per `BINDER_PIVOT_DESIGN.md` §4.1, M7 acceptance is "noice renders main UI." M6 acceptance is the weaker prerequisite: "the noice process at least pushes a buffer through M6's BufferQueue, and that buffer reaches the phone screen — pixel content not yet asserted."

**This is the M6 milestone gate**: first proof of pixels on screen from a real-framework code path through Westlake's binder substrate. See `BINDER_PIVOT_MILESTONES.md` §M6 acceptance.

### 7.5 Regression integration

Master regression script (`scripts/binder-pivot-regression.sh`) gets an entry:

```bash
# Section: M6 surface daemon
section_start "M6 surface daemon"
run_test "surface_smoke (standalone backend)" \
    "$ADB shell '/data/local/tmp/westlake/bin-bionic/surface_smoke'" \
    "frames done" "FAIL"
run_test "surface_binder_smoke (full path)" \
    "$ADB shell '/data/local/tmp/westlake/bin-bionic/surface_binder_smoke'" \
    "frames done via binder" "FAIL"
section_end
```

---

## 8. Risk register

| # | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| 1 | **HWUI insists on GPU-coherent buffers; memfd-mmap fails or produces black frames** | **High** | **Critical** | Force HWUI software path via system property `debug.hwui.renderer=skiacpu` or env var. Verify in a 1-day pre-M6 spike test BEFORE writing the daemon. If GPU-coherent buffers are required: extend to allocating via `dma-buf` heap (`/dev/dma_heap/system`) which the OnePlus 6's kernel supports — adds ~150 LOC, ~1 day. |
| 2 | **BufferQueue protocol semantics diverge between Android 11 and Android 16** (dequeue/queue race conditions, slot count, sync-mode handling) | High | High | Verify against framework.jar's actual IGraphicBufferProducer.aidl + libgui call patterns. Pre-M6 discovery: run a dex that creates a Surface and traces every Binder transaction via the existing `binder_jni_stub.cc` log path. 0.5-1 day. Mirror M5's same pattern. |
| 3 | **`SET_TRANSACTION_STATE` is enormous and noice's ViewRootImpl calls it constantly** (>50% of transactions; complex parcelable: LayerState, DisplayState, InputWindowCommands) | High | Medium | Phase 1 implements it as a *recorder* — store the transaction into a per-layer state struct, then return OK. Most fields (transform, alpha, blur, color space) we ignore. Visible/Z-order/position deltas we *do* apply to the backend (via SurfaceBackend::setLayerVisible/Z/Position). The full parcelable parse is ~300 LOC but is mostly skip-and-no-op work. |
| 4 | **Vsync timing matters for Choreographer-driven animations** | Medium | Medium | Phase 1 VsyncThread is "good enough" — 60 Hz wall-clock tick. If animations stutter, add a phase-locked-loop tied to actual queueBuffer arrival. Phase 2/12 will use OHOS XComponent's vsync natively. |
| 5 | **AOSP libgui's BpSurfaceComposerClient marshaling expects specific binder version flags** (e.g. `Stability::Vendor` markings; Android 11→16 changed these) | Medium | Medium | The patch 0004 (CR11 in M4_DISCOVERY.md §40) widens our receive-side magic checks; M6 should verify *send-side* compatibility too — emit `'VNDR'` headers if framework.jar expects vendor side. The Makefile's `KHEADER_FLAG` knob (see `BINDER_PIVOT_DESIGN.md` §5.8) is the lever. |
| 6 | **IDisplayEventConnection BitTube — sock_seqpacket setup is fiddly** | Medium | Low | The BitTube is just a `SOCK_SEQPACKET` socketpair the daemon hands the client. AOSP's `BitTube.cpp` (~100 LOC) is dropped in verbatim; verified-portable. |
| 7 | **DLST pipe bandwidth saturates and frames drop** | Medium | Low | Bandwidth budget at 1080×2280×4 bytes × 60 FPS = ~590 MB/s through stdout pipe. Practical limit on a fork-and-exec'd stdout is ~200-500 MB/s. Mitigation: zstd compression on the pipe (1080×2280 is highly compressible; expect 4-8× ratio). Or: cap at 30 FPS for acceptance. Or: send only delta rectangles per the QueueBufferInput.crop hint. Phase 1 is *correctness*, not 60 FPS. |
| 8 | **Per-layer consumer thread count explodes** (every Activity / Dialog / popup / status bar / IME / system UI overlay = a separate layer) | Low | Medium | Phase 1: cap layer count at 8. Above that, fail-loud at createSurface. Single-app noice case is ~3 layers (status bar, IME container, app main). |
| 9 | **First buffer arrives BEFORE the Compose host's SurfaceView is ready to receive it** | Medium | Medium | The Compose host already handles "magic sync" pattern in WestlakeVM.kt:1878 onwards (`syncing to DLST magic`). New buffers arriving before SurfaceView readiness are dropped. Phase 1 acceptable. |

**Top 3 risks ranked by likelihood × impact:** #1 (HWUI GPU requirement) > #2 (BufferQueue protocol drift) > #3 (SET_TRANSACTION_STATE complexity).

**Top 1 risk requiring architectural pre-investigation:** #1 — Builder MUST do a 1-day spike before writing the daemon, verifying memfd-backed buffers can flow through HWUI. The whole M6 timeline depends on this.

---

## 9. Effort estimate

### 9.1 Phase 1 (Android sandbox, DLST pipe backend) — this milestone

| Sub-task | Person-days |
|---|---|
| **Pre-flight HWUI spike** — verify memfd-backed buffer feeds HWUI in a minimal isolated test (no daemon, no full M6); confirm or pivot to dma-buf | 1.0 |
| Transaction-code discovery via dex-side `Surface` smoke (same pattern as M5 §3 Tier-1 inventory verification) | 0.5 |
| `aosp-surface-daemon-port/` scaffolding + Makefile + build.sh + patches | 0.5 |
| `aosp-src/ISurfaceComposer.cpp` + headers + Bn skeleton wiring | 0.5 |
| `aosp-src/IGraphicBufferProducer.cpp` + Bn skeleton | 0.5 |
| `aosp-src/ISurfaceComposerClient.cpp` + Bn skeleton | 0.5 |
| `deps-src/ui-stubs/GraphicBuffer-memfd.cpp` + Fence + Region + Rect | 1.0 |
| `src/SurfaceComposerImpl.cpp` — 10 Tier-1 + 40 fail-loud | 1.5 |
| `src/SurfaceComposerClientImpl.cpp` + `src/GraphicBufferProducerImpl.cpp` + `BufferQueueCore.cpp` | 2.0 |
| `src/DisplayEventConnection.cpp` + `VsyncThread.cpp` + BitTube setup | 0.5 |
| `src/DlstPipeBackend.cpp` — Phase 1 backend (optional zstd) | 1.0 |
| `src/LayerState.cpp` — SET_TRANSACTION_STATE recorder | 0.5 |
| `main.cpp` + boot orchestration patch (must start before dalvikvm) | 0.5 |
| Smoke tests (`surface_smoke.cpp`, `surface_binder_smoke.cpp`, `SurfaceTest.dex`) | 1.0 |
| Integration test — first noice MainActivity frame on phone screen | 1.0 |
| Polish + regression entry + docs | 0.5 |

**Total Phase 1: ~12 person-days** (vs `BINDER_PIVOT_MILESTONES.md` §M6's "5-7 days" estimate). The milestones-doc estimate is optimistic; the more detailed breakdown above accounts for:
- The 1-day HWUI spike (added explicitly here)
- The 0.5-day transaction-code discovery (added explicitly)
- The full BufferQueue protocol (~2 days, the dense work)

Reconciliation with milestones-doc: their 5-7 day estimate is feasible IF the HWUI spike confirms memfd buffers work as-is. If the spike requires a pivot to dma-buf or another path, expect 14-15 days. **The HWUI spike is the gating risk for the schedule estimate.**

### 9.2 Phase 2 (OHOS backend swap — M12)

| Sub-task | Person-days |
|---|---|
| OHOS sysroot cross-build path | 0.5 |
| `XComponentBackend.cpp` — adapt to OH_NativeWindow_NativeWindowRequestBuffer / FlushBuffer | 1.5 |
| Direct GraphicBuffer→NativeWindow blit path replacing DLST pipe | 1.0 |
| OHOS phone smoke + integration (M13 noice on OHOS) | 1.0 |

**Total Phase 2 (M12): ~4 person-days.** Matches `BINDER_PIVOT_MILESTONES.md` §M12's "4-5 days" estimate.

### 9.3 Combined M6 + M12 effort

**~16 person-days total across both phases** (best case with HWUI spike passing first try); **~20 person-days** if dma-buf pivot needed.

Hard dependency on M1-M3 + M4b being green. Soft dependency: M5 (audio daemon) — same boot infrastructure scaffolding; nice to land first so the Boot orchestrator already handles "start a native daemon before dalvikvm."

---

## 10. Critical files for the Builder agent to read first

1. `docs/engine/BINDER_PIVOT_DESIGN.md` §3.3, §3.6, §3.8, §5.2 (BufferQueue ABI), §5.4 (memfd) — architectural envelope
2. `docs/engine/BINDER_PIVOT_MILESTONES.md` §M6, §M12 — acceptance criteria
3. `docs/engine/M5_AUDIO_DAEMON_PLAN.md` — sibling-pattern reference (memfd reuse, backend abstraction, build scaffolding)
4. `aosp-libbinder-port/BUILD_PLAN.md` and `Makefile` — bionic-arm64 cross-compile to mirror
5. `westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt` lines 1850-2200 — Compose host SurfaceView + DLST pipe reader (the *consumer* of our Phase 1 backend output)
6. `docs/engine/WESTLAKE_LAUNCHER_AUDIT.md` S30 — DLST byte-protocol writer that M6 replaces (currently in WestlakeLauncher) plus sections S22-S37 covering related disposition
7. `shim/java/com/westlake/services/WestlakeWindowManagerService.java` — M4b's IWindowManager.openSession plumbing, which calls into M6 via IGraphicBufferProducer
8. `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/include/gui/ISurfaceComposer.h` — interface header
9. `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/ISurfaceComposer.cpp` — Bp/Bn skeleton to derive from
10. `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/include/gui/IGraphicBufferProducer.h` — producer interface header
11. `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/BufferQueueProducer.cpp` — reference implementation (DO NOT port wholesale; pattern only)
12. `/home/dspfac/openharmony/interface/sdk_c/arkui/ace_engine/native/native_interface_xcomponent.h` — Phase 2 target API (M12 forward-planning only)

---

## 11. Critical insights (lower the milestones-doc risk estimate)

1. **Lower-risk than estimated:** the Phase-1 backend is a known-working component (Compose host SurfaceView + DLST pipe is what today's `WestlakeLauncher` writes to). M6 just rewires where the writer lives — from dalvikvm-side `WestlakeRenderer.draw()` to daemon-side `DlstPipeBackend::presentBuffer()`. **No new visual infrastructure required.**

2. **Lower-risk than estimated:** memfd path for buffer transport is already battle-tested by M4-PRE7 (AssetManager natives) + M5 (audio cblk). The "BufferQueue / gralloc" risk in `BINDER_PIVOT_MILESTONES.md` R3 is partially de-risked — the BufferQueue logic is ours (slim ~400 LOC reimplementation), the gralloc replacement is memfd-backed (~250 LOC), neither requires new kernel work.

3. **Higher-risk than estimated:** HWUI's preference for GPU-coherent buffers (memfd is CPU-only). The milestones-doc R3 doesn't explicitly call this out. Builder must do a 1-day spike before committing to the rest of M6 — this is the single most decision-load-bearing pre-flight test.

4. **Higher-risk than estimated:** `SET_TRANSACTION_STATE` is the heaviest method on the ISurfaceComposer surface; its parcelable (`LayerState`) has ~30 fields whose semantics we need to either parse or skip safely. ~300 LOC of parcelable parsing work that the milestones-doc didn't itemize.

5. **Neutral:** Phase 1 → Phase 2 backend swap is clean (~400 LOC of OhosBackend in M12). The DLST pipe is intentionally a Phase-1-only mechanism; M12 cleans up to direct XComponent NativeWindow blit and the DLST infrastructure can subsequently be `[DELETE-after-M12]`.

**The Builder agent's biggest risks:**
- **#1 (memfd vs HWUI):** mitigation is a 1-day spike, MUST be the first thing done.
- **#2 (transaction code drift):** mitigation is 0.5-day dex-side discovery.
Both are bounded; passing the spikes unlocks the full 12-day plan.

**The Builder agent's biggest opportunity:** M6 is the first milestone where *real pixels* go through *real binder*. M5 produces sound (perceptual but not visual); M6 produces pixels (visible, demonstrable proof). The acceptance bar is intentionally weak ("noice's MainActivity displays SOMETHING") — pixel-perfect rendering is M7 / Phase 3 polish. **Land M6 = land the first proof that the binder pivot delivers a UX-visible result.**

End of M6_SURFACE_DAEMON_PLAN.md.
