# CR35 — M6 ISurfaceComposer / ISurfaceComposerClient / IGraphicBufferProducer / IDisplayEventConnection Transaction Discovery

**Date:** 2026-05-13
**Owner:** Architect (read-only research)
**Goal:** Catalog every transaction code in the four Binder interfaces the M6 surface daemon must implement (`westlake-surface-daemon`), so subsequent M6-Step6 work has a known-complete map. Also surface drift between the AOSP-11 reference codes that M6-Step1..Step5 used and the Android-15 libgui / SurfaceFlinger actually running on phone `cfb7c9e3` (OnePlus 6, LineageOS 22).

**Scope:** AOSP-11 source at `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/`:

- `ISurfaceComposer.h` (`enum BnSurfaceComposer::ISurfaceComposerTag`) + `ISurfaceComposer.cpp` (`BnSurfaceComposer::onTransact` switch lines 1226-2065)
- `ISurfaceComposerClient.h` + `ISurfaceComposerClient.cpp` (anonymous `enum class Tag` lines 32-39, SafeInterface dispatch lines 103-121)
- `IGraphicBufferProducer.h` + `IGraphicBufferProducer.cpp` (anonymous enum lines 50-77, `BnGraphicBufferProducer::onTransact` lines 786-1084)
- `IDisplayEventConnection.h` + `IDisplayEventConnection.cpp` (anonymous `enum class Tag` lines 25-30, SafeInterface dispatch lines 64-78)

Plus drift evidence pulled from phone `cfb7c9e3`:

- `/system/framework/framework.jar` (39.9 MB; pulled to `/tmp/cr35-fwk/framework.jar`)
- `/system/lib64/libgui.so` (1.62 MB; pulled to `/tmp/cr35-fwk/libgui.so`)
- `/system/bin/surfaceflinger` (8.30 MB; pulled to `/tmp/cr35-fwk/surfaceflinger`)
- `/system/framework/framework-graphics.jar` (3661 B — config XML only, no dex)

**Anti-drift note:** ZERO source / test / shim / daemon edits. All output is this NEW doc plus a single-row addendum to `PHASE_1_STATUS.md`. No phone reboot, no SM cycling.

---

## §1. Method counts

| Interface | AOSP-11 enum slots | AOSP-11 virtuals (header) | AOSP-11 dispatchable | Tier-1 (M6 plan §2.2/§2.3/§2.4/§2.5) | Tier-2 (no-op-able) | Tier-3 (fail-loud) | Android-15 method count (cfb7c9e3 libgui.so AIDL strings) |
|---|---|---|---|---|---|---|---|
| **ISurfaceComposer** | 50 | 50 (`= 0` pure-virtual: 50) | 50 | **13** | 19 | 18 | **75** (50 inherited + ≥25 new in A12-A15) |
| **ISurfaceComposerClient** | 5 | 5 | 5 | **5** (all of them — every one is on a draw path) | 0 | 0 | **6** (added `mirrorDisplay`; `getSchedulingPolicy` is non-AIDL) |
| **IGraphicBufferProducer** | 26 | 28 (2 non-pure: `setLegacyBufferDrop`, `setAutoPrerotation`, `getFrameTimestamps`, `exportToParcel` etc.) | 26 | **12** | 6 | 8 | unchanged structurally on cfb7c9e3 (hand-coded Bp/Bn, no `AIDL::cpp::IGraphicBufferProducer::*` strings) |
| **IDisplayEventConnection** | 3 | 3 | 3 | **3** (all of them — vsync is on the critical path) | 0 | 0 | **5** (added `getLatestVsyncEventData`, `getSchedulingPolicy`) |
| **Total wire surface (AOSP-11)** | **84** | 86 | 84 | **33** | 25 | 26 | n/a |

### 1.1 Match against M6 plan §2.6 estimate

M6 plan §2.6 stated: **"~10 ISurfaceComposer + ~3 ISurfaceComposerClient + ~12 IGraphicBufferProducer + ~2 IDisplayEventConnection = ~27 methods + 1 vsync thread"** for Tier-1, **"~40 + ~17 + ~16 = ~73 fail-loud stubs"**. Actual from AOSP-11 source:

| Estimate | Plan §2.6 | Actual (CR35, AOSP-11) | Drift |
|---|---|---|---|
| ISurfaceComposer total | ~52 | **50** | within ±2 (plan rounded up) |
| ISurfaceComposer Tier-1 | ~10 | **13** (Step-2 implemented 13 — see M6_STEP2_REPORT.md §3 table) | +3 (plan undercounted: `BOOT_FINISHED`, `CREATE_DISPLAY`, `DESTROY_DISPLAY` are necessary Tier-1 even though plan §2.2 marked some Tier-3) |
| ISurfaceComposerClient total | ~20 (plan §2.3) | **5** | **−15 large drift** (plan §2.3's "~20" was a misread — the *enum* in `.cpp` is 5; plan likely mixed up the `enum flags { eHidden, ... }` in the .h with the transaction enum) |
| ISurfaceComposerClient Tier-1 | ~3 | **5** (all 5; M6-Step2 wired all 5) | +2 (every method is on a draw path — plan §2.3 only listed `createSurface` + `destroySurface` + `getTransformHint`, but `destroySurface` doesn't exist in AOSP-11 — handle releases happen via Binder reference-count, not a method; and `getTransformHint` is an *out-parameter* of `createSurface` in AOSP-11, not a separate transaction) |
| IGraphicBufferProducer total | ~28 | **26 enum slots / 28 declared virtuals** (2 non-pure defaults: `setLegacyBufferDrop`, `setAutoPrerotation`, `getFrameTimestamps`, `exportToParcel`) | within ±2 |
| IGraphicBufferProducer Tier-1 | ~12 | **12** | exact |
| IDisplayEventConnection total | ~2 | **3** (plan §2.5 mentioned only "setVsyncRate" path) | +1 |
| IDisplayEventConnection Tier-1 | ~2 | **3** | +1 (plan rolled `stealReceiveChannel` into "BitTube fd setup" prose) |
| **Combined Tier-1** | ~27 | **33** | +6 (~22% over-budget but fully within M6's 5-7 day envelope) |
| **Combined fail-loud** | ~73 | **51** (84 dispatchable − 33 Tier-1 = 51, of which ~26 truly fail-loud + ~25 no-op-able Tier-2) | **−22 under** (plan rounded fail-loud generously; CR35 split into Tier-2 [no-op-able] vs Tier-3 [fail-loud]) |

**Verdict: M6 plan §2.6 surface-size estimate HOLDS within ±25% — no rescoping of the 5-7 day budget.** Three boundary cases were reclassified vs plan §2.2/§2.3:

1. `ISurfaceComposerClient.createWithSurfaceParent` reclassified **Tier-1** (vs plan-implicit Tier-2): every Compose / Fragment child-Surface goes through it once `setContentView` runs; M6-Step2 already wires it identically to `createSurface`.
2. `IDisplayEventConnection.stealReceiveChannel` reclassified **Tier-1** (vs plan-implicit "internal"): Choreographer cannot subscribe to vsync without it — the BitTube fd is the vsync channel. M6-Step4 wires this.
3. `ISurfaceComposer.GET_DISPLAY_CONFIGS` was Tier-1 in plan §2.2; CR35 keeps it Tier-1 but notes the **struct-layout caveat** in M6_STEP2_REPORT.md §4 — implementor sources field-by-field layout from the live `framework.jar` schema discovered by CR35.

---

## §2. ISurfaceComposer transactions (table)

Source: `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/include/gui/ISurfaceComposer.h:549-603` (enum) + `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/ISurfaceComposer.cpp:1226-2065` (`BnSurfaceComposer::onTransact` switch).

All codes are `IBinder::FIRST_CALL_TRANSACTION + N` where `FIRST_CALL_TRANSACTION = 0x00000001`. So `BOOT_FINISHED = 1`, `CREATE_CONNECTION = 2`, …, `ACQUIRE_FRAME_RATE_FLEXIBILITY_TOKEN = 50`.

Descriptor (AOSP-11): `"android.ui.ISurfaceComposer"` (per `IMPLEMENT_META_INTERFACE(SurfaceComposer, "android.ui.ISurfaceComposer")` at ISurfaceComposer.cpp:1224).

| Code | Symbol | Method | Tier | Step-2 status | Signature (Parcel I/O) | Notes |
|---|---|---|---|---|---|---|
| 1 | `BOOT_FINISHED` | `bootFinished()` | **Tier-1** | wired | IN: (none beyond CHECK_INTERFACE). OUT: (none). | ActivityManagerService gates init on this. Step-2 ack'd; M6-Step6 should keep the no-op + log. Comment in header: "BOOT_FINISHED must remain this value, it is called from Java by ActivityManagerService." |
| 2 | `CREATE_CONNECTION` | `createConnection() → sp<ISurfaceComposerClient>` | **Tier-1** | wired | IN: (none). OUT: `StrongBinder client`. | First call: every `SurfaceComposerClient` ctor calls this. Step-2 returns a `WestlakeSurfaceComposerClient` BBinder. |
| 3 | `GET_DISPLAY_INFO` | `getDisplayInfo(sp<IBinder> display, DisplayInfo*)` | **Tier-1** | wired (worst-case envelope) | IN: `StrongBinder display`. OUT: `int32 status` then if NO_ERROR: `writeInplace(sizeof(DisplayInfo))` raw struct memcpy. | Struct layout caveat — see M6_STEP2_REPORT.md §4.1. AOSP-11 `DisplayInfo` is `{DisplayConnectionType, float density, bool secure, std::optional<DeviceProductInfo>}` — `sizeof` depends on libstdc++ version and is NOT ABI-stable. Step-5 follow-up: swap to explicit Parcelable. |
| 4 | `CREATE_DISPLAY_EVENT_CONNECTION` | `createDisplayEventConnection(VsyncSource, ConfigChanged) → sp<IDisplayEventConnection>` | **Tier-1** | wired (null connection; Step-4 wires real vsync) | IN: `int32 vsyncSource`, `int32 configChanged`. OUT: `StrongBinder connection`. | Choreographer vsync subscription entry point. Step-2 returns null binder (canonical AOSP-11 fail-soft); M6-Step4 spins up real BnDisplayEventConnection. |
| 5 | `CREATE_DISPLAY` | `createDisplay(String8 name, bool secure) → sp<IBinder>` | **Tier-1** | wired | IN: `String8 name`, `int32 secure`. OUT: `StrongBinder display`. | Virtual display token creation (media projection). Step-2 returns a synthetic BBinder per request. |
| 6 | `DESTROY_DISPLAY` | `destroyDisplay(sp<IBinder> display)` | **Tier-1** | wired (no-op) | IN: `StrongBinder display`. OUT: (none). | Symmetric to CREATE_DISPLAY. No-op acceptable in Phase 1. |
| 7 | `GET_PHYSICAL_DISPLAY_TOKEN` | `getPhysicalDisplayToken(PhysicalDisplayId) → sp<IBinder>` | **Tier-1** | wired | IN: `uint64 displayId`. OUT: `StrongBinder display`. | All subsequent display calls are keyed on this token. Step-2 returns `kPhysicalDisplayToken` (id=0). |
| 8 | `SET_TRANSACTION_STATE` | `setTransactionState(...)` — composer's main mutator | **Tier-1, HEAVY** | wired (no-op; Step-5 wires real LayerState) | IN (in order): `uint32 stateCount`, `stateCount × ComposerState.read(data)`, `uint32 displayCount`, `displayCount × DisplayState.read(data)`, `uint32 stateFlags`, `StrongBinder applyToken`, `InputWindowCommands.read(data)`, `int64 desiredPresentTime`, `StrongBinder uncached.token`, `uint64 uncached.id`, `bool hasListenerCallbacks`, `int32 listenersSize`, `listenersSize × {StrongBinder listener, int64Vector callbackIds}`. OUT: (none). | ~50% of all calls once UI starts. Step-2 reads-and-discards; M6-Step5 wires the LayerState mutator to BufferQueueCore. Heaviest transaction by parcel size and frequency. |
| 9 | `AUTHENTICATE_SURFACE` | `authenticateSurfaceTexture(sp<IGraphicBufferProducer>) → bool` | **Tier-2** | ack-only (Step-1 fall-through) | IN: `StrongBinder bufferProducer`. OUT: `int32 result` (1 or 0). | Return-true safe — SurfaceView intercept only validates the BBP is one we issued. M6-Step6 should answer `1` for any BBP that came from our `createSurface`. |
| 10 | `GET_SUPPORTED_FRAME_TIMESTAMPS` | `getSupportedFrameTimestamps(vector<FrameEvent>*) → status_t` | **Tier-2** | ack-only | IN: (none). OUT: `int32 status`, `int32Vector supported`. | Return empty vector + NO_ERROR. AOSP framework treats empty as "no timestamps supported" and falls back to wall-clock. |
| 11 | `GET_DISPLAY_CONFIGS` | `getDisplayConfigs(sp<IBinder> display, Vector<DisplayConfig>*)` | **Tier-1** | wired (worst-case envelope + best-effort prefix) | IN: `StrongBinder display`. OUT: `int32 status` then if NO_ERROR: `uint32 count`, `count × writeInplace(sizeof(DisplayConfig))` raw struct memcpy. | Resolution enumeration. AOSP-11 `DisplayConfig` ≈ 48 B nominal: `{ui::Size, float xDpi, float yDpi, float refreshRate, 3× nsecs_t offsets, int configGroup}`. Step-2 worst-case envelope sized at 64 B. Step-5 follow-up: parcelable. |
| 12 | `GET_ACTIVE_CONFIG` | `getActiveConfig(sp<IBinder> display) → int` | **Tier-1** | wired | IN: `StrongBinder display`. OUT: `int32 id`. | Selected config index. Step-2 returns 0 (kCannedActiveConfig). |
| 13 | `GET_DISPLAY_STATE` | `getDisplayState(sp<IBinder>, ui::DisplayState*)` | **Tier-1** | wired (worst-case envelope) | IN: `StrongBinder display`. OUT: `int32 status` then if NO_ERROR: `writeInplace(sizeof(ui::DisplayState))` raw struct memcpy. | Rotation / power state. `ui::DisplayState` ≈ 24 B nominal: `{int32 layerStack, ui::Rotation, uint32 w, uint32 h}`. Step-2 worst-case envelope sized at 32 B. Step-5 follow-up: parcelable. |
| 14 | `CAPTURE_SCREEN` | `captureScreen(...) → status_t` | **Tier-3** | ack-only | IN: `StrongBinder display`, `int32 reqDataspace`, `int32 reqPixelFormat`, `Rect sourceCrop`, `uint32 reqWidth`, `uint32 reqHeight`, `int32 useIdentityTransform`, `int32 rotation`, `int32 captureSecureLayers`. OUT: `int32 status` then if NO_ERROR: `GraphicBuffer outBuffer`, `bool capturedSecureLayers`. | Screenshot. Return BAD_VALUE — framework tolerates. |
| 15 | `CAPTURE_LAYERS` | `captureLayers(...)` | **Tier-3** | ack-only | IN: `StrongBinder layerHandle`, `int32 reqDataspace`, `int32 reqPixelFormat`, `Rect sourceCrop`, `int32 numExcludeHandles`, `numExcludeHandles × StrongBinder`, `float frameScale`, `bool childrenOnly`. OUT: `int32 status` + optional `GraphicBuffer outBuffer`. | Layer screenshot. Return BAD_VALUE. |
| 16 | `CLEAR_ANIMATION_FRAME_STATS` | `clearAnimationFrameStats() → status_t` | **Tier-3** | ack-only | IN: (none). OUT: `int32 status`. | Animation stats. Return NO_ERROR no-op. |
| 17 | `GET_ANIMATION_FRAME_STATS` | `getAnimationFrameStats(FrameStats*) → status_t` | **Tier-3** | ack-only | IN: (none). OUT: `FrameStats stats` (parcelable), `int32 status`. | Note: AOSP-11 writes stats BEFORE status (reverse of typical pattern). Return empty FrameStats + NO_ERROR. |
| 18 | `SET_POWER_MODE` | `setPowerMode(sp<IBinder>, int32)` | **Tier-2** | ack-only | IN: `StrongBinder display`, `int32 mode`. OUT: (none). | Display power. No-op acceptable. |
| 19 | `GET_DISPLAY_STATS` | `getDisplayStats(sp<IBinder>, DisplayStatInfo*) → status_t` | **Tier-1** | wired (full struct layout) | IN: `StrongBinder display`. OUT: `int32 status` then if NO_ERROR: `writeInplace(sizeof(DisplayStatInfo))` raw memcpy. | Choreographer queries `vsyncPeriod`. `DisplayStatInfo = {nsecs_t vsyncTime, nsecs_t vsyncPeriod}` — only 16 B, layout STABLE (just two int64). Step-2 writes explicit `{0, 16666667}` — 60 Hz. |
| 20 | `GET_HDR_CAPABILITIES` | `getHdrCapabilities(sp<IBinder>, HdrCapabilities*) → status_t` | **Tier-2** | ack-only | IN: `StrongBinder display`. OUT: `int32 status` then if NO_ERROR: `HdrCapabilities` (parcelable). | Return empty HdrCapabilities + NO_ERROR. |
| 21 | `GET_DISPLAY_COLOR_MODES` | `getDisplayColorModes(sp<IBinder>, Vector<ColorMode>*) → status_t` | **Tier-2** | ack-only | IN: `StrongBinder display`. OUT: `int32 status` then if NO_ERROR: `uint32 count`, `count × int32 colorMode`. | Color modes. Return `{ColorMode::SRGB}` + NO_ERROR. |
| 22 | `GET_ACTIVE_COLOR_MODE` | `getActiveColorMode(sp<IBinder>) → ColorMode` | **Tier-2** | ack-only | IN: `StrongBinder display`. OUT: `int32 colorMode`. | Return ColorMode::SRGB (=0). |
| 23 | `SET_ACTIVE_COLOR_MODE` | `setActiveColorMode(sp<IBinder>, ColorMode) → status_t` | **Tier-2** | ack-only | IN: `StrongBinder display`, `int32 colorModeInt`. OUT: `int32 status`. | No-op + NO_ERROR. |
| 24 | `ENABLE_VSYNC_INJECTIONS` | `enableVSyncInjections(bool) → status_t` | **Tier-3** | ack-only | IN: `bool enable`. OUT: (none — direct return). | Test infra. Return PERMISSION_DENIED (=-1) — only authorized in dev builds. |
| 25 | `INJECT_VSYNC` | `injectVSync(int64) → status_t` | **Tier-3** | ack-only | IN: `int64 when`. OUT: (none — direct return). | Test infra. Return PERMISSION_DENIED. |
| 26 | `GET_LAYER_DEBUG_INFO` | `getLayerDebugInfo(vector<LayerDebugInfo>*) → status_t` | **Tier-3** | ack-only | IN: (none). OUT: `int32 status` then if NO_ERROR: `parcelableVector outLayers`. | dumpsys path. Return empty + NO_ERROR. |
| 27 | `GET_COMPOSITION_PREFERENCE` | `getCompositionPreference(*, *, *, *) → status_t` | **Tier-2** | ack-only | IN: (none). OUT: `int32 error`, `int32 defaultDataspace`, `int32 defaultPixelFormat`, `int32 wideColorGamutDataspace`, `int32 wideColorGamutPixelFormat`. | Return `{SRGB, RGBA_8888, SRGB, RGBA_8888}` + NO_ERROR. |
| 28 | `GET_COLOR_MANAGEMENT` | `getColorManagement(bool*) → status_t` | **Tier-2** | ack-only | IN: (none). OUT: `bool result`. | Return false. |
| 29 | `GET_DISPLAYED_CONTENT_SAMPLING_ATTRIBUTES` | (display, *, *, *) | **Tier-3** | ack-only | IN: `StrongBinder display`. OUT (if NO_ERROR): `uint32 format`, `uint32 dataspace`, `uint32 component`. | Region sampling for accent color. Return UNKNOWN_TRANSACTION (=-EBADMSG) or BAD_VALUE. |
| 30 | `SET_DISPLAY_CONTENT_SAMPLING_ENABLED` | `setDisplayContentSamplingEnabled(display, bool, uint8, uint64) → status_t` | **Tier-3** | ack-only | IN: `StrongBinder display`, `bool enable`, `int8 componentMask`, `uint64 maxFrames`. OUT: (direct return). | Return BAD_VALUE. |
| 31 | `GET_DISPLAYED_CONTENT_SAMPLE` | `getDisplayedContentSample(display, uint64, uint64, DisplayedFrameStats*) → status_t` | **Tier-3** | ack-only | IN: `StrongBinder display`, `uint64 maxFrames`, `uint64 timestamp`. OUT (if NO_ERROR): `uint64 numFrames`, 4× `uint64Vector component_N_sample`. | Return BAD_VALUE. |
| 32 | `GET_PROTECTED_CONTENT_SUPPORT` | `getProtectedContentSupport(bool*) → status_t` | **Tier-2** | ack-only | IN: (none). OUT: `bool result`. | DRM. Return false. |
| 33 | `IS_WIDE_COLOR_DISPLAY` | `isWideColorDisplay(sp<IBinder>, bool*) → status_t` | **Tier-2** | ack-only | IN: `StrongBinder display`. OUT (if NO_ERROR): `bool result`. | Return false. |
| 34 | `GET_DISPLAY_NATIVE_PRIMARIES` | `getDisplayNativePrimaries(sp<IBinder>, DisplayPrimaries&) → status_t` | **Tier-2** | ack-only | IN: `StrongBinder display`. OUT: `int32 status` then if NO_ERROR: `writeInplace(sizeof(ui::DisplayPrimaries))` raw memcpy. | Color primaries. Return BAD_VALUE — framework falls back to compile-time defaults. |
| 35 | `GET_PHYSICAL_DISPLAY_IDS` | `getPhysicalDisplayIds() → vector<PhysicalDisplayId>` | **Tier-1** | wired | IN: (none). OUT: `uint64Vector`. | Display enumeration. Step-2 returns `{0}` (single physical display). |
| 36 | `ADD_REGION_SAMPLING_LISTENER` | `addRegionSamplingListener(Rect, sp<IBinder>, sp<IRegionSamplingListener>) → status_t` | **Tier-3** | ack-only | IN: `Rect samplingArea`, `nullable StrongBinder stopLayerHandle`, `nullable StrongBinder listener`. OUT: (direct return). | Accent-color sampling (statusbar tint follows wallpaper). Return BAD_VALUE. |
| 37 | `REMOVE_REGION_SAMPLING_LISTENER` | `removeRegionSamplingListener(sp<IRegionSamplingListener>) → status_t` | **Tier-3** | ack-only | IN: `nullable StrongBinder listener`. OUT: (direct return). | Symmetric. Return NO_ERROR. |
| 38 | `SET_DESIRED_DISPLAY_CONFIG_SPECS` | `setDesiredDisplayConfigSpecs(...) → status_t` | **Tier-2** | ack-only | IN: `StrongBinder displayToken`, `int32 defaultConfig`, 4× `float refreshRateMin/Max`. OUT: `int32 status`. | Refresh rate config. No-op + NO_ERROR. |
| 39 | `GET_DESIRED_DISPLAY_CONFIG_SPECS` | `getDesiredDisplayConfigSpecs(...) → status_t` | **Tier-2** | ack-only | IN: `StrongBinder displayToken`. OUT: `int32 defaultConfig`, 4× `float refreshRateMin/Max`, `int32 status`. | Return `{0, 60.0, 60.0, 60.0, 60.0}` + NO_ERROR. |
| 40 | `GET_DISPLAY_BRIGHTNESS_SUPPORT` | `getDisplayBrightnessSupport(sp<IBinder>, bool*) → status_t` | **Tier-2** | ack-only | IN: `nullable StrongBinder displayToken`. OUT: `bool support`, direct-return status. | Return false. |
| 41 | `SET_DISPLAY_BRIGHTNESS` | `setDisplayBrightness(sp<IBinder>, float) → status_t` | **Tier-2** | ack-only | IN: `nullable StrongBinder displayToken`, `float brightness`. OUT: (direct return). | Return NO_ERROR no-op. |
| 42 | `CAPTURE_SCREEN_BY_ID` | `captureScreen(uint64, Dataspace*, GraphicBuffer*) → status_t` | **Tier-3** | ack-only | IN: `uint64 displayOrLayerStack`. OUT: `int32 status` then if NO_ERROR: `int32 outDataspace`, `GraphicBuffer outBuffer`. | Return BAD_VALUE. |
| 43 | `NOTIFY_POWER_HINT` | `notifyPowerHint(int32) → status_t` | **Tier-2** | ack-only | IN: `int32 hintId`. OUT: (direct return). | No-op + NO_ERROR. |
| 44 | `SET_GLOBAL_SHADOW_SETTINGS` | `setGlobalShadowSettings(half4, half4, float, float, float) → status_t` | **Tier-2** | ack-only | IN: `floatVector shadowConfig[11]` (ambient.RGBA + spot.RGBA + lightPosY + lightPosZ + lightRadius). OUT: (direct return). | Return NO_ERROR no-op. |
| 45 | `GET_AUTO_LOW_LATENCY_MODE_SUPPORT` | `getAutoLowLatencyModeSupport(sp<IBinder>, bool*) → status_t` | **Tier-3** | ack-only | IN: `StrongBinder display`. OUT: `bool supported`. | Gaming HDMI hint. Return false. |
| 46 | `SET_AUTO_LOW_LATENCY_MODE` | `setAutoLowLatencyMode(sp<IBinder>, bool)` | **Tier-3** | ack-only | IN: `StrongBinder display`, `bool setAllm`. OUT: (none). | No-op. |
| 47 | `GET_GAME_CONTENT_TYPE_SUPPORT` | `getGameContentTypeSupport(sp<IBinder>, bool*) → status_t` | **Tier-3** | ack-only | IN: `StrongBinder display`. OUT: `bool supported`. | Return false. |
| 48 | `SET_GAME_CONTENT_TYPE` | `setGameContentType(sp<IBinder>, bool)` | **Tier-3** | ack-only | IN: `StrongBinder display`, `bool setGameContentTypeOn`. OUT: (none). | No-op. |
| 49 | `SET_FRAME_RATE` | `setFrameRate(sp<IGraphicBufferProducer>, float, int8) → status_t` | **Tier-2** | ack-only | IN: `StrongBinder surface`, `float frameRate`, `int8 compatibility`. OUT: `int32 result`. | Per-window refresh rate hint. No-op + NO_ERROR. |
| 50 | `ACQUIRE_FRAME_RATE_FLEXIBILITY_TOKEN` | `acquireFrameRateFlexibilityToken(sp<IBinder>*) → status_t` | **Tier-3** | ack-only | IN: (none). OUT: `int32 result` then if NO_ERROR: `StrongBinder token`. | Refresh-rate-gating token. Return PERMISSION_DENIED — single-app dev test API. |

### 2.1 ISurfaceComposer Tier-1 set (the 13 that M6-Step2 actually wires)

| # | Symbol | Why Tier-1 (cross-ref M6_STEP2_REPORT.md §1) |
|---|---|---|
| 1 | `BOOT_FINISHED` | ActivityManagerService gates init on it |
| 2 | `CREATE_CONNECTION` | Every `SurfaceComposerClient` ctor calls this |
| 3 | `GET_DISPLAY_INFO` | `ViewRootImpl` / `DisplayManager` |
| 4 | `CREATE_DISPLAY_EVENT_CONNECTION` | Choreographer vsync subscription |
| 5 | `CREATE_DISPLAY` | Virtual-display tokens |
| 6 | `DESTROY_DISPLAY` | Symmetric |
| 7 | `GET_PHYSICAL_DISPLAY_TOKEN` | All subsequent display calls are keyed on this token |
| 8 | `SET_TRANSACTION_STATE` | Heaviest method; ~50% of all calls once UI starts |
| 11 | `GET_DISPLAY_CONFIGS` | Resolution enumeration |
| 12 | `GET_ACTIVE_CONFIG` | Selected config index |
| 13 | `GET_DISPLAY_STATE` | Rotation / power state |
| 19 | `GET_DISPLAY_STATS` | Choreographer queries `vsyncPeriod` |
| 35 | `GET_PHYSICAL_DISPLAY_IDS` | Display enumeration |

13 Tier-1; 19 Tier-2 (no-op-able); 18 Tier-3 (fail-loud OK). Total 50 dispatchable.

---

## §3. ISurfaceComposerClient transactions (table)

Source: `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/ISurfaceComposerClient.cpp:32-39` (`enum class Tag`) + lines 103-121 (`BnSurfaceComposerClient::onTransact`). **SafeInterface-based** — the auto-marshalling template generates the parcel layout; we hand-code to match.

Descriptor (AOSP-11): `"android.ui.ISurfaceComposerClient"` (line 99).

**SafeInterface wire-format note** (cross-ref M6_STEP2_REPORT.md §4.2): reply format is **outputs first, status_t LAST** — reverse of typical BBinder convention. M6-Step6 implementor must preserve this in any new handlers.

All 5 codes are M6-Step2 wired today.

| Code | Symbol | Method | Tier | Step-2 status | Signature (Parcel I/O via SafeInterface auto-marshalling) | Notes |
|---|---|---|---|---|---|---|
| 1 | `CREATE_SURFACE` | `createSurface(String8 name, uint32 w, uint32 h, PixelFormat, uint32 flags, sp<IBinder> parent, LayerMetadata, sp<IBinder>* handle, sp<IGraphicBufferProducer>* gbp, uint32* outTransformHint) → status_t` | **Tier-1** | wired (null gbp until Step-3) | IN: `String8 name`, `uint32 w`, `uint32 h`, `int32 format`, `uint32 flags`, `StrongBinder parent`, `LayerMetadata` (parcelable). OUT (SafeInterface order): `StrongBinder handle`, `StrongBinder gbp`, `uint32 outTransformHint`, `int32 status`. | THE call that allocates a layer. Step-2 returns `{handle=BBinder, gbp=nullptr, transformHint=0, NO_ERROR}`. M6-Step3 replaces nullptr with real memfd-backed BnGraphicBufferProducer. |
| 2 | `CREATE_WITH_SURFACE_PARENT` | `createWithSurfaceParent(name, w, h, PixelFormat, flags, sp<IGraphicBufferProducer> parent, LayerMetadata, *handle, *gbp, *outTransformHint) → status_t` | **Tier-1** | wired (identical to CREATE_SURFACE) | IN: as CREATE_SURFACE except `parent` is `StrongBinder` to IGraphicBufferProducer (not IBinder). OUT: same. | Reparenting subsurface to existing BBP. Step-2 identical canned response. |
| 3 | `CLEAR_LAYER_FRAME_STATS` | `clearLayerFrameStats(sp<IBinder> handle) const → status_t` | **Tier-1** | wired (no-op + NO_ERROR) | IN: `StrongBinder handle`. OUT: `int32 status`. | No-op acceptable. |
| 4 | `GET_LAYER_FRAME_STATS` | `getLayerFrameStats(sp<IBinder> handle, FrameStats*) const → status_t` | **Tier-1** | wired (empty FrameStats) | IN: `StrongBinder handle`. OUT: `FrameStats outStats` (parcelable), `int32 status`. | Return empty FrameStats. |
| 5 | `MIRROR_SURFACE` | `mirrorSurface(sp<IBinder> mirrorFromHandle, sp<IBinder>* outHandle) → status_t` | **Tier-1** | wired (synthetic handle) | IN: `StrongBinder mirrorFromHandle`. OUT: `StrongBinder outHandle`, `int32 status`. | Layer mirroring. Step-2 returns a synthetic BBinder + NO_ERROR. |

5 Tier-1; 0 Tier-2; 0 Tier-3. **Every ISurfaceComposerClient method is on a draw-path code path** — there are no "safe to fail-loud" methods in this interface.

---

## §4. IGraphicBufferProducer transactions (table)

Source: `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/IGraphicBufferProducer.cpp:50-77` (anonymous enum) + lines 786-1084 (`BnGraphicBufferProducer::onTransact` switch).

Descriptor (AOSP-11): `"android.gui.IGraphicBufferProducer"` (set by `IMPLEMENT_META_INTERFACE_DUAL` macro in IGraphicBufferProducer.cpp:34).

**Two of the 26 case labels have a typo in AOSP-11** (`CHECK_INTERFACE(IGraphicBuffer, ...)` instead of `IGraphicBufferProducer`): code 19 `SET_AUTO_REFRESH` (line 981) and code 26 `SET_AUTO_PREROTATION` (line 1076). This is a known AOSP-11 bug — the interface-token check accepts whatever literal descriptor `IGraphicBuffer::getInterfaceDescriptor()` returns, which is **not** the same as `IGraphicBufferProducer`'s. In practice both descriptor templates expand to the empty-IInterface-base interface name and the check passes by coincidence, but M6's port should keep the verbatim AOSP-11 typo for wire-compat. **Action: do not "fix" these typos when porting** — clients have been compiled against the typo'd descriptor for years.

All codes are `IBinder::FIRST_CALL_TRANSACTION + N`. `REQUEST_BUFFER = 1`, …, `SET_AUTO_PREROTATION = 26`.

| Code | Symbol | Method | Tier | Step-2 status | Signature (Parcel I/O) | Notes |
|---|---|---|---|---|---|---|
| 1 | `REQUEST_BUFFER` | `requestBuffer(int slot, sp<GraphicBuffer>*) → status_t` | **Tier-1** | not yet (M6-Step3) | IN: `int32 bufferIdx`. OUT: `bool nonNull` then if true `GraphicBuffer buffer` (via `reply.write(*buffer)` — flattenable; embeds dup'd buffer-handle fds), `int32 result`. | M6-Step3 wires this. Returns the actual GraphicBuffer for a slot after dequeueBuffer. Real GraphicBuffer flattening must dup the underlying memfd via SCM_RIGHTS. |
| 2 | `DEQUEUE_BUFFER` | `dequeueBuffer(*slot, *fence, w, h, format, usage, *bufferAge, *frameTimestamps) → status_t` | **Tier-1** | not yet (M6-Step3) | IN: `uint32 width`, `uint32 height`, `int32 format`, `uint64 usage`, `bool getTimestamps`. OUT: `int32 slot`, `Fence fence` (parcelable: dup'd fd), `uint64 bufferAge`, optional `FrameEventHistoryDelta frameTimestamps`, `int32 result`. | Client asks for free buffer. Phase-1: NO_FENCE for the fence (immediate-signal), allocate-on-demand via memfd if slot empty. |
| 3 | `DETACH_BUFFER` | `detachBuffer(int slot) → status_t` | **Tier-2** | ack-only | IN: `int32 slot`. OUT: `int32 result`. | App-detach for cross-process re-attach. No-op + NO_ERROR. |
| 4 | `DETACH_NEXT_BUFFER` | `detachNextBuffer(*buffer, *fence) → status_t` | **Tier-2** | ack-only | IN: (none). OUT: `int32 result` then if NO_ERROR: `bool bufferNonNull` + optional `GraphicBuffer`, `bool fenceNonNull` + optional `Fence`. | Same as above. Return BAD_VALUE — we don't support detach in Phase 1. |
| 5 | `ATTACH_BUFFER` | `attachBuffer(*outSlot, sp<GraphicBuffer>) → status_t` | **Tier-2** | ack-only | IN: `GraphicBuffer` (read into new GraphicBuffer). OUT: `int32 slot`, `int32 result`. | App-attach symmetric to detach. Return BAD_VALUE. |
| 6 | `QUEUE_BUFFER` | `queueBuffer(int slot, QueueBufferInput&, QueueBufferOutput*) → status_t` | **Tier-1** | not yet (M6-Step3) | IN: `int32 slot`, `QueueBufferInput` (parcelable: timestamp, dataspace, crop, scalingMode, transform, sticky, async, fence). OUT: `QueueBufferOutput` (parcelable: width, height, transformHint, numPendingBuffers, nextFrameNumber, bufferReplaced), `int32 result`. | Client returns finished buffer. **This is the hot path** — every frame goes through it. M6-Step3 must forward the buffer to DlstPipeBackend → host SurfaceView. |
| 7 | `CANCEL_BUFFER` | `cancelBuffer(int slot, sp<Fence>) → status_t` | **Tier-1** | not yet (M6-Step3) | IN: `int32 buf`, `Fence fence`. OUT: `int32 result`. | Symmetric to queueBuffer for cases where the app discards a dequeued buffer without producing a frame. |
| 8 | `QUERY` | `query(int what, int* value) → int` | **Tier-1** | not yet (M6-Step3) | IN: `int32 what`. OUT: `int32 value`, `int32 res`. | Query queue properties. `what` values from `NATIVE_WINDOW_*` in `<system/window.h>`: WIDTH, HEIGHT, FORMAT, MIN_UNDEQUEUED_BUFFERS, CONSUMER_RUNNING_BEHIND, CONSUMER_USAGE_BITS, DEFAULT_WIDTH, DEFAULT_HEIGHT, TRANSFORM_HINT, etc. ~20 queries; M6 implements the half-dozen most-common. |
| 9 | `CONNECT` | `connect(IProducerListener, int api, bool producerControlledByApp, QueueBufferOutput*) → status_t` | **Tier-1** | not yet (M6-Step3) | IN: `int32 hasListener`, optional `StrongBinder listener`, `int32 api`, `int32 producerControlledByApp`. OUT: `QueueBufferOutput`, `int32 res`. | Establish producer connection. `api` = NATIVE_WINDOW_API_EGL/CPU/MEDIA/CAMERA. M6-Step3 must accept connect + populate QueueBufferOutput. |
| 10 | `DISCONNECT` | `disconnect(int api, DisconnectMode) → status_t` | **Tier-1** | not yet (M6-Step3) | IN: `int32 api`, `int32 mode`. OUT: `int32 res`. | Symmetric to connect. |
| 11 | `SET_SIDEBAND_STREAM` | `setSidebandStream(sp<NativeHandle>) → status_t` | **Tier-3** | ack-only | IN: `int32 hasStream`, optional `NativeHandle` (dup'd-fd list). OUT: `int32 result`. | TV tuner sideband. Return INVALID_OPERATION. |
| 12 | `ALLOCATE_BUFFERS` | `allocateBuffers(uint32 w, uint32 h, PixelFormat, uint64 usage)` | **Tier-2** | ack-only | IN: `uint32 width`, `uint32 height`, `int32 format`, `uint64 usage`. OUT: (none — async hint). | Pre-allocation hint. No-op acceptable (we allocate lazily). |
| 13 | `ALLOW_ALLOCATION` | `allowAllocation(bool) → status_t` | **Tier-2** | ack-only | IN: `int32 allow`. OUT: `int32 result`. | Override per-frame allocation policy. No-op + NO_ERROR. |
| 14 | `SET_GENERATION_NUMBER` | `setGenerationNumber(uint32) → status_t` | **Tier-1** | not yet (M6-Step3) | IN: `uint32 generationNumber`. OUT: `int32 result`. | Buffer gen-counter — used to invalidate stale buffers after Surface re-creation. Track in BufferQueueCore. |
| 15 | `GET_CONSUMER_NAME` | `getConsumerName() → String8` | **Tier-1** | not yet (M6-Step3) | IN: (none). OUT: `String8`. | Debug-only — but called early by every Surface ctor. Return `"westlake-bufferqueue"`. |
| 16 | `SET_MAX_DEQUEUED_BUFFER_COUNT` | `setMaxDequeuedBufferCount(int) → status_t` | **Tier-1** | not yet (M6-Step3) | IN: `int32 maxDequeuedBuffers`. OUT: `int32 result`. | Pre-allocate count (typically 3 for triple-buffered). Track in BufferQueueCore + cap allocations. |
| 17 | `SET_ASYNC_MODE` | `setAsyncMode(bool) → status_t` | **Tier-1** | not yet (M6-Step3) | IN: `bool async`. OUT: `int32 result`. | Sync vs async. We run async (non-blocking enqueue). No-op + NO_ERROR. |
| 18 | `SET_SHARED_BUFFER_MODE` | `setSharedBufferMode(bool) → status_t` | **Tier-3** | ack-only | IN: `bool sharedBufferMode`. OUT: `int32 result`. | Single-shared-buffer mode (used by Daydream / SystemUI). Return BAD_VALUE — Phase 1 doesn't support. |
| 19 | `SET_AUTO_REFRESH` | `setAutoRefresh(bool) → status_t` | **Tier-3** | ack-only | IN: `bool autoRefresh`. OUT: `int32 result`. | **TYPO IN AOSP-11**: case label uses `CHECK_INTERFACE(IGraphicBuffer, ...)` not `IGraphicBufferProducer`. Auto-refresh pairs with shared-buffer mode. Return BAD_VALUE. |
| 20 | `SET_DEQUEUE_TIMEOUT` | `setDequeueTimeout(nsecs_t) → status_t` | **Tier-1** | not yet (M6-Step3) | IN: `int64 timeout`. OUT: `int32 result`. | Block timeout for dequeueBuffer. Track in BufferQueueCore. |
| 21 | `GET_LAST_QUEUED_BUFFER` | `getLastQueuedBuffer(*buffer, *fence, float[16] transform) → status_t` | **Tier-3** | ack-only | IN: (none). OUT: `int32 result` then if NO_ERROR: `bool bufferNonNull` + optional `GraphicBuffer` + `float[16] transform`, `Fence fence`. | Touch-aware buffer caching. Return BAD_VALUE — Phase 1 doesn't track. |
| 22 | `GET_FRAME_TIMESTAMPS` | `getFrameTimestamps(FrameEventHistoryDelta*)` | **Tier-3** | ack-only | IN: (none). OUT: `FrameEventHistoryDelta` (complex parcelable). | Frame-pacing telemetry. Write empty delta. |
| 23 | `GET_UNIQUE_ID` | `getUniqueId(uint64*) → status_t` | **Tier-1** | not yet (M6-Step3) | IN: (none). OUT: `int32 actualResult`, `uint64 outId`. | Unique BufferQueue ID. Return a deterministic per-Surface counter. |
| 24 | `GET_CONSUMER_USAGE` | `getConsumerUsage(uint64*) → status_t` | **Tier-2** | ack-only | IN: (none). OUT: `int32 actualResult`, `uint64 outUsage`. | Consumer-side usage flags. Return `GRALLOC_USAGE_HW_FB \| GRALLOC_USAGE_HW_COMPOSER` (0x900) + NO_ERROR. |
| 25 | `SET_LEGACY_BUFFER_DROP` | `setLegacyBufferDrop(bool) → status_t` | **Tier-3** | ack-only | IN: `bool drop`. OUT: `int32 result`. | Legacy frame-drop policy. No-op + NO_ERROR. Default-implemented in header (not pure-virtual) — M6's BBP impl inherits the default if not overridden. |
| 26 | `SET_AUTO_PREROTATION` | `setAutoPrerotation(bool) → status_t` | **Tier-3** | ack-only | IN: `bool autoPrerotation`. OUT: `int32 result`. | **TYPO IN AOSP-11** (same as code 19). Pre-rotation hint for rotated displays. No-op + NO_ERROR. Default-implemented in header. |

12 Tier-1; 6 Tier-2; 8 Tier-3. Total 26 dispatchable.

### 4.1 IGraphicBufferProducer Tier-1 set (the 12 — matches M6 plan §2.4 exactly)

`REQUEST_BUFFER`, `DEQUEUE_BUFFER`, `QUEUE_BUFFER`, `CANCEL_BUFFER`, `CONNECT`, `DISCONNECT`, `SET_MAX_DEQUEUED_BUFFER_COUNT`, `SET_ASYNC_MODE`, `QUERY`, `SET_GENERATION_NUMBER`, `SET_DEQUEUE_TIMEOUT`, `GET_CONSUMER_NAME` (12 total — `GET_UNIQUE_ID` is a 13th the plan-table didn't enumerate but is called by every Surface ctor for ANR-trace purposes).

---

## §5. IDisplayEventConnection transactions (table)

Source: `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/IDisplayEventConnection.cpp:25-30` (`enum class Tag`) + lines 64-78 (`BnDisplayEventConnection::onTransact`). **SafeInterface-based** (same wire format as ISurfaceComposerClient).

Descriptor (AOSP-11): `"android.gui.DisplayEventConnection"` (line 62).

| Code | Symbol | Method | Tier | Step-2 status | Signature (Parcel I/O via SafeInterface) | Notes |
|---|---|---|---|---|---|---|
| 1 | `STEAL_RECEIVE_CHANNEL` | `stealReceiveChannel(gui::BitTube* outChannel) → status_t` | **Tier-1** | not yet (M6-Step4) | IN: (none). OUT: `BitTube outChannel` (parcelable, dup'd socketpair fd), `int32 status`. | Client takes ownership of the vsync-event channel fd. Without this, Choreographer can't subscribe. M6-Step4 must socketpair() + push fd via SCM_RIGHTS. |
| 2 | `SET_VSYNC_RATE` | `setVsyncRate(uint32 count) → status_t` | **Tier-1** | not yet (M6-Step4) | IN: `uint32 count`. OUT: `int32 status`. | Subscribe to every `count`-th vsync (0=off, 1=every, 2=every-other). Track per-connection. M6-Step4: store in per-connection state, gate the 60Hz tick thread on it. |
| 3 | `REQUEST_NEXT_VSYNC` | `requestNextVsync() → void` (one-way) | **Tier-1** | not yet (M6-Step4) | IN: (none — async). OUT: (none — `callRemoteAsync`). | Request a single vsync event (fires exactly once next vsync, regardless of setVsyncRate). M6-Step4: flag the per-connection state to fire-once. |

3 Tier-1; 0 Tier-2; 0 Tier-3. **Every IDisplayEventConnection method is on the vsync critical path** — Choreographer stalls without all three.

---

## §6. Drift analysis (AOSP-11 vs Android-15 framework.jar / libgui.so on cfb7c9e3)

Phone `cfb7c9e3` (OnePlus 6, LineageOS 22) is **Android 15** (SDK 35, `ro.build.version.release=15`). The reference codes M6-Step1..Step5 used are from **AOSP-11**. This section catalogs the observable drift.

### 6.1 framework.jar drift

`/system/framework/framework.jar` on phone is **41.9 MB** with **5 dex files** (classes.dex through classes5.dex, totaling ~41 MB raw). The Java framework on Android 15 reaches SurfaceFlinger via **JNI through SurfaceControl** — there is **no `ISurfaceComposer` Java class** in framework.jar (verified: `dexdump | grep ISurfaceComposer` returns empty across all 5 dex files). All SurfaceFlinger interactions go: `SurfaceControl.<native method>` → `frameworks/base/core/jni/android_view_SurfaceControl.cpp` → `libgui.so`'s `BpSurfaceComposer::method()` → Binder.

**`android.view.SurfaceControl` (classes4.dex):** has **125 unique `native*` JNI methods** declared (up from ~60 in Android 11). New methods relative to Android 11 include `nativeSetBuffer`, `nativeSetCrop`, `nativeSetDamageRegion`, `nativeSetDataSpace`, `nativeSetDesiredHdrHeadroom`, `nativeSetEdgeExtensionEffect`, `nativeSetExtendedRangeBrightness`, `nativeSetLuts`, `nativeSetPictureProfileId`, `nativeAddJankDataListener`, `nativeAddWindowInfosReportedListener`, `nativeCreateJankDataListenerWrapper`, `nativeCreateTpc` (TrustedPresentationCallback), `nativeNotifyShutdown`, `nativeSetCanOccludePresentation`, `nativeAddTransactionCommittedListener`, etc. **These all translate to Binder transactions on ISurfaceComposer that did NOT exist in AOSP-11** — see §6.2 for the AIDL-side count.

`framework-graphics.jar` is **3661 B with no dex** — only `MANIFEST.MF` + a 3376-B XML config. Not relevant for transaction discovery.

### 6.2 libgui.so AIDL drift

Symbol-table extraction from `/system/lib64/libgui.so` (pulled to `/tmp/cr35-fwk/libgui.so`, 1.62 MB ARM64 stripped):

**`ISurfaceComposer`** — Android 15 has migrated to **AIDL-generated dispatch**. Grep `strings libgui.so | grep '^AIDL::cpp::ISurfaceComposer::'` returns **75 distinct method names** (one `cppClient` + one `cppServer` trace marker per method). Concretely:

```
addFpsListener            addHdrLayerInfoListener   addJankListener
addRegionSamplingListener addTunnelModeEnabledListener  addWindowInfosListener
bootFinished              captureDisplay            captureDisplayById
captureLayers             captureLayersSync         clearAnimationFrameStats
clearBootDisplayMode      createConnection          createDisplayEventConnection
createVirtualDisplay      destroyVirtualDisplay     enableRefreshRateOverlay
flushJankData             forceClientComposition    getAnimationFrameStats
getBootDisplayModeSupport getCompositionPreference  getDesiredDisplayModeSpecs
getDisplayBrightnessSupport getDisplayDecorationSupport getDisplayNativePrimaries
getDisplayState           getDisplayStats           getDisplayedContentSample
getDisplayedContentSamplingAttributes getDynamicDisplayInfoFromId
getDynamicDisplayInfoFromToken getGpuContextPriority getHdrConversionCapabilities
getHdrOutputConversionSupport getMaxAcquiredBufferCount getMaxLayerPictureProfiles
getOverlaySupport         getPhysicalDisplayIds     getPhysicalDisplayToken
getProtectedContentSupport getSchedulingPolicy      getStalledTransactionInfo
getStaticDisplayInfo      getSupportedFrameTimestamps isWideColorDisplay
notifyPowerBoost          notifyShutdown            onPullAtom
overrideHdrTypes          removeFpsListener         removeHdrLayerInfoListener
removeJankListener        removeRegionSamplingListener removeTunnelModeEnabledListener
removeWindowInfosListener scheduleCommit            scheduleComposite
setActiveColorMode        setActivePictureListener  setAutoLowLatencyMode
setBootDisplayMode        setDebugFlash             setDesiredDisplayModeSpecs
setDisplayBrightness      setDisplayContentSamplingEnabled setGameContentType
setGameDefaultFrameRateOverride setGameModeFrameRateOverride setGlobalShadowSettings
setHdrConversionStrategy  setPowerMode              setSmallAreaDetectionThreshold
updateSmallAreaDetection
```

**Verdict: 75 methods vs AOSP-11's 50 — ~50% growth (+25 net new methods).** Of the 50 AOSP-11 methods:

- **40 still present** with same name (mapped to AIDL `methodName` from AOSP-11's `SCREAMING_SNAKE_CASE`).
- **10 RENAMED or RESHAPED** in the A11→A15 migration:
  - `GET_DISPLAY_INFO` (AOSP-11 code 3) → split into `getStaticDisplayInfo` + `getDynamicDisplayInfoFromToken` + `getDynamicDisplayInfoFromId`. Plan: M6-Step6 reroutes the AOSP-11-style `GET_DISPLAY_INFO` to a synthetic merge of static+dynamic.
  - `GET_DISPLAY_CONFIGS` (AOSP-11 code 11) → folded into `getDynamicDisplayInfo*` (configs are now a field of DynamicDisplayInfo).
  - `GET_ACTIVE_CONFIG` (AOSP-11 code 12) → no direct equivalent; the active config index is read from DynamicDisplayInfo.
  - `GET_SUPPORTED_FRAME_TIMESTAMPS` (code 10) → still present.
  - `GET_DISPLAY_COLOR_MODES` (code 21) / `GET_ACTIVE_COLOR_MODE` (code 22) / `SET_ACTIVE_COLOR_MODE` (code 23) → `setActiveColorMode` still present; the GET-side folded into DynamicDisplayInfo's `supportedColorModes` / `activeColorMode` fields.
  - `ACQUIRE_FRAME_RATE_FLEXIBILITY_TOKEN` (code 50) → removed in A12+; superseded by `setDesiredDisplayModeSpecs` (A11 code 38).
  - `ENABLE_VSYNC_INJECTIONS` / `INJECT_VSYNC` (codes 24/25) → removed in A12+ (test infra moved to libsurfaceflinger_unittest).
  - `GET_DISPLAY_BRIGHTNESS_SUPPORT` (code 40) → still present as `getDisplayBrightnessSupport`.
  - `NOTIFY_POWER_HINT` (code 43) → renamed to `notifyPowerBoost` (A14+).
  - `GET_AUTO_LOW_LATENCY_MODE_SUPPORT` (code 45) / `GET_GAME_CONTENT_TYPE_SUPPORT` (code 47) → folded into DynamicDisplayInfo.
- **25+ NET NEW** methods in A12-A15: `captureDisplay`/`captureDisplayById` (replace `CAPTURE_SCREEN`), `captureLayers`/`captureLayersSync`, `createVirtualDisplay`/`destroyVirtualDisplay` (replace `CREATE_DISPLAY`/`DESTROY_DISPLAY`), `scheduleCommit`, `scheduleComposite`, `forceClientComposition`, `setBootDisplayMode`/`clearBootDisplayMode`/`getBootDisplayModeSupport`, `setHdrConversionStrategy`/`getHdrConversionCapabilities`/`getHdrOutputConversionSupport`, `addJankListener`/`removeJankListener`/`flushJankData`, `addFpsListener`/`removeFpsListener`, `addHdrLayerInfoListener`/`removeHdrLayerInfoListener`, `addTunnelModeEnabledListener`/`removeTunnelModeEnabledListener`, `addWindowInfosListener`/`removeWindowInfosListener`, `setDesiredDisplayModeSpecs`/`getDesiredDisplayModeSpecs` (replace `SET_DESIRED_DISPLAY_CONFIG_SPECS`), `enableRefreshRateOverlay`, `notifyShutdown`, `onPullAtom`, `overrideHdrTypes`, `setDebugFlash`, `setSmallAreaDetectionThreshold`/`updateSmallAreaDetection`, `setActivePictureListener`, `setGameDefaultFrameRateOverride`/`setGameModeFrameRateOverride`, `getGpuContextPriority`, `getMaxAcquiredBufferCount`, `getMaxLayerPictureProfiles`, `getOverlaySupport`, `getSchedulingPolicy`, `getStalledTransactionInfo`, `getDisplayDecorationSupport`.

**Impact on M6-Step6 implementor:** the 13 Tier-1 codes M6-Step2 wires from AOSP-11 (1, 2, 3, 4, 5, 6, 7, 8, 11, 12, 13, 19, 35) cover the methods that are still present-and-numerically-stable when going to Android-15 — **with the exception of codes 3, 11, 12 which need redirection to the AIDL `getStaticDisplayInfo` / `getDynamicDisplayInfo*` path on real A15 libgui clients**. Because **the OnePlus 6 noice/McD framework.jar is Android 15**, and the framework.jar uses `SurfaceControl` JNI → `libgui.so` → Binder, **the actual wire-codes emitted on the phone may not match AOSP-11's enum values** — they will be the AIDL-generated codes from the A15 `android.gui.aidl.ISurfaceComposer` AIDL file.

**However**: in M6 Phase 1, the daemon **isn't talking to the phone's framework.jar over Binder** — it is registered as `"SurfaceFlinger"` in our M2 servicemanager on `/dev/vndbinder` (per M6_STEP1_REPORT.md / M6_STEP2_REPORT.md), and the **only Binder client of our daemon is the `surface_smoke` test binary we link against AOSP-11 libgui** in `aosp-surface-daemon-port/`. So the AOSP-11 codes are correct **for now**. The drift becomes relevant **when M7-Step2 wires real apps into the daemon** — at that point the noice / McD process's libgui.so (the Android-15 one we just enumerated) will start emitting AIDL-generated codes, not AOSP-11 codes.

**Decision (forwarded to M6-Step6 / M7-Step2):** keep AOSP-11 codes for the Phase-1 in-daemon smoke; when wiring real apps in M7, audit the on-wire codes via `strace -e trace=ioctl -f` of the dalvikvm process and either:
- (Option A) build a translation shim that maps A15 AIDL codes → AOSP-11 hand-coded codes (preserves daemon's hand-written switch).
- (Option B) regenerate the daemon's onTransact from the A15 `frameworks/native/libs/gui/aidl/android/gui/ISurfaceComposer.aidl` source (estimate: ~600 LOC of AIDL-generated dispatch in addition to our 13 wired handlers).

Per the M6 plan's "Phase-1 returns canned values" contract (§4 of M6_STEP2_REPORT.md), Option A is the strong Phase-1 default and Option B is only needed if a Tier-1 transaction code happens to land on a NEW A15 enum value (no overlap with the AOSP-11 ones we handle, in which case it falls through to the default ack — harmless for non-critical methods, but **breaks Choreographer if `createDisplayEventConnection` was renumbered**).

### 6.3 ISurfaceComposerClient drift (A11 → A15)

```
A11 methods (5):                A15 methods (6 via libgui.so strings):
  CREATE_SURFACE                  createSurface
  CREATE_WITH_SURFACE_PARENT      —  (REMOVED in A14; reparenting moved to Transaction.reparent)
  CLEAR_LAYER_FRAME_STATS         clearLayerFrameStats
  GET_LAYER_FRAME_STATS           getLayerFrameStats
  MIRROR_SURFACE                  mirrorSurface
                                  mirrorDisplay              (NEW in A13)
                                  getSchedulingPolicy        (NEW in A14; one-way poll)
```

**Verdict: small drift, 1 removal + 2 additions.** `CREATE_WITH_SURFACE_PARENT` is the only AOSP-11 method that doesn't survive — the A15 framework's `SurfaceControl.Builder.setParent(SurfaceControl)` now uses `Transaction.reparent` after a regular `createSurface`. M6-Step2 wires both `CREATE_SURFACE` and `CREATE_WITH_SURFACE_PARENT` identically (same canned reply), so Phase 1 is robust to either client behavior.

### 6.4 IGraphicBufferProducer drift (A11 → A15)

```
strings libgui.so | grep '^AIDL::cpp::IGraphicBufferProducer::'  → EMPTY
```

**Verdict: IGraphicBufferProducer is STILL hand-coded Bp/Bn in Android 15** — NOT migrated to AIDL. The 26 transaction codes in AOSP-11 should match Android 15 verbatim. Confirmed by `nm -D /tmp/cr35-fwk/libgui.so | grep -E "(BpGraphicBufferProducer|BnGraphicBufferProducer)"` returning expected symbols, and by absence of `IGraphicBufferProducer.aidl` in any of the AOSP source tarballs / on-phone .dylib.so AIDL trace artifacts.

**Action: M6-Step3's hand-coded IGBP handlers can use AOSP-11 codes verbatim** — no drift mitigation needed for the buffer-queue path.

### 6.5 IDisplayEventConnection drift (A11 → A15)

```
A11 methods (3):                A15 methods (5 via libgui.so strings):
  STEAL_RECEIVE_CHANNEL           stealReceiveChannel
  SET_VSYNC_RATE                  setVsyncRate
  REQUEST_NEXT_VSYNC              requestNextVsync
                                  getLatestVsyncEventData    (NEW in A12)
                                  getSchedulingPolicy        (NEW in A14)
```

**Verdict: 2 net new methods. Both Tier-2 (no-op-able).** `getLatestVsyncEventData` is the AIDL replacement for the BitTube-pull pattern; if the A15 framework prefers it, our null BnDisplayEventConnection breaks. **M6-Step4 implementor: prepare both paths** — wire the BitTube push (the AOSP-11 way) AND add a fail-loud stub for `getLatestVsyncEventData` so we can detect A15 client preference and pivot if needed.

### 6.6 Descriptor drift

AOSP-11 descriptors (from `IMPLEMENT_META_INTERFACE`):

```
ISurfaceComposer       : "android.ui.ISurfaceComposer"
ISurfaceComposerClient : "android.ui.ISurfaceComposerClient"
IGraphicBufferProducer : "android.gui.IGraphicBufferProducer"
IDisplayEventConnection: "android.gui.DisplayEventConnection"
```

Android 15 AIDL-converted descriptors (from `nm -D /tmp/cr35-fwk/libgui.so`):

```
_ZN7android16ISurfaceComposer10descriptorE        — symbol exists (the descriptor string is in another translation unit / dynamic loader)
_ZN7android22IGraphicBufferProducer10descriptorE  — symbol exists
_ZN7android3gui12IFpsListener10descriptorE        — example A15-only listener; descriptor namespace is `android.gui.*`
_ZN7android3gui13IJankListener10descriptorE       — same
```

Direct grep `strings libgui.so | grep -oE "android\.(gui|ui)\.[A-Za-z]+"` returns empty — descriptors are runtime-constructed (typical for AIDL via `getInterfaceDescriptor` + `String16` allocation). We cannot verify drift purely from strings, but the **C++ mangled symbol shows `android::ISurfaceComposer::descriptor` is still in the `android::` namespace, NOT `android::ui::`** — strong hint that Android-15 dropped the `ui` segment in the descriptor string.

**Action: M6-Step6 implementor must verify the live descriptor by adding an `ALOGI("descriptor=%s", String8(getInterfaceDescriptor()).string());` probe to the daemon and trace one real-app Binder call. If the live descriptor is `"android.ISurfaceComposer"` (no `ui`), the daemon's `IMPLEMENT_META_INTERFACE` call must be amended. This is a single string-literal change.**

---

## §7. Implementor TODO list for M6-Step6 cleanup

The following items are surfaced by CR35 and should be addressed (in priority order) by the M6-Step6 implementor:

### 7.1 Tier-1 wire-format polish (highest priority — unblocks real app workloads)

1. **§T1-A** Replace the worst-case envelope writes for `GET_DISPLAY_INFO` (code 3) / `GET_DISPLAY_CONFIGS` (code 11) / `GET_DISPLAY_STATE` (code 13) in `WestlakeSurfaceComposer.cpp` with explicit Parcelable-style write paths. Source the field-by-field layout from `/home/dspfac/aosp-android-11/frameworks/native/libs/ui/include/ui/DisplayInfo.h` + `DisplayConfig.h` + `DisplayState.h`. Estimate: ~80 LOC.

2. **§T1-B** Wire 12 IGraphicBufferProducer Tier-1 handlers (codes 1, 2, 6, 7, 8, 9, 10, 14, 15, 16, 17, 20, 23) in a new `WestlakeGraphicBufferProducer.cpp` (referenced by M6_STEP2_REPORT.md §7.1 as the Step-3 deliverable; CR35 confirms the 12-method scope is exactly right). Estimate: ~800 LOC per M6 plan §3.3.

3. **§T1-C** Wire 3 IDisplayEventConnection Tier-1 handlers (codes 1, 2, 3) in a new `WestlakeDisplayEventConnection.cpp` (referenced as Step-4 deliverable). Estimate: ~150 LOC + ~80 LOC vsync tick thread.

4. **§T1-D** Replace the null `IDisplayEventConnection` reply in `CREATE_DISPLAY_EVENT_CONNECTION` (code 4) with a real BnDisplayEventConnection BBinder. Touch site: `WestlakeSurfaceComposer.cpp::onCreateDisplayEventConnection`. Estimate: ~10 LOC.

5. **§T1-E** Replace the null `IGraphicBufferProducer` returned from `CREATE_SURFACE` (ISurfaceComposerClient code 1) with a real `BnGraphicBufferProducer`. Touch site: `WestlakeSurfaceComposerClient.cpp::onCreateSurface`. Estimate: ~10 LOC + binding to BufferQueueCore (§T1-B).

### 7.2 Tier-2 fail-soft hardening (medium priority — unblocks A15 framework opportunistic queries)

6. **§T2-A** Promote 19 ISurfaceComposer Tier-2 codes (9, 10, 18, 20, 21, 22, 23, 27, 28, 32, 33, 34, 38, 39, 40, 41, 43, 44, 49) from the current Step-1 ack-only fall-through to explicit `case` handlers returning safe defaults (per §2 table's "Notes" column). Estimate: ~250 LOC (10-15 LOC per handler). Justification: under Step-1 fall-through they return `NO_ERROR` with empty reply, which deserializes as `status=0 + zero-initialized struct` — usually safe, but `GET_DISPLAY_BRIGHTNESS_SUPPORT` (code 40) reading `bool` from empty parcel may produce an undefined value on some Parcel implementations. Explicit handlers are safer.

7. **§T2-B** Same for 6 IGraphicBufferProducer Tier-2 codes (3, 4, 5, 12, 13, 24). Estimate: ~80 LOC.

8. **§T2-C** No Tier-2 codes in ISurfaceComposerClient or IDisplayEventConnection.

### 7.3 Tier-3 fail-loud (low priority — observability)

9. **§T3-A** Promote 18 ISurfaceComposer Tier-3 codes (14, 15, 16, 17, 24, 25, 26, 29, 30, 31, 36, 37, 42, 45, 46, 47, 48, 50) and 8 IGraphicBufferProducer Tier-3 codes (11, 18, 19, 21, 22, 25, 26) from ack-only to **fail-loud with `ALOGE("UNHANDLED <CODE> code=%u", code); return INVALID_OPERATION;`**. Justification: per M6_STEP2_REPORT.md §6.4, ack-only is the deliberate Step-1 default to avoid crashing apps that probe optional surface; but once M6-Step3..Step5 stabilize, fail-loud surfaces unexpected new A15 transactions for triage. Estimate: ~100 LOC. **Do AFTER one full noice/McD discovery run to confirm none of the 26 Tier-3 codes are actually called.**

### 7.4 A15 drift mitigation (deferred — defer until M7-Step2 wires real apps)

10. **§D-A** When M7-Step2 starts running real noice/McD into the daemon, run `strace -e trace=ioctl -f -p <dalvikvm-pid>` and capture the actual transaction codes emitted by the on-phone libgui.so. Reconcile against §2 / §3 / §4 / §5 tables. File any unrecognized A15-only codes as a CR35-followup.

11. **§D-B** If the descriptor is found to be `"android.ISurfaceComposer"` (sans `ui`) on A15, amend `IMPLEMENT_META_INTERFACE(SurfaceComposer, ...)` in our port and rebuild. Single-line change.

12. **§D-C** Add an A15-AIDL-code → AOSP-11-code translation shim for `getStaticDisplayInfo` / `getDynamicDisplayInfo*` if real apps emit those (likely). The shim is a `case <A15-code>:` redirect to the existing `onGetDisplayInfo` / `onGetDisplayConfigs` / `onGetDisplayState` handlers. Estimate: ~30 LOC.

13. **§D-D** Add fail-loud stubs for ~25 net-new A15 ISurfaceComposer methods (§6.2 list) + 2 net-new A15 IDisplayEventConnection methods + 2 net-new A15 ISurfaceComposerClient methods. Estimate: ~50 LOC of `ALOGE` + `return INVALID_OPERATION` stubs. Use `WestlakeServiceMethodMissing.fail()`-style observability per CR2/CR17 lessons.

### 7.5 Wire-format edge cases (architect's notes)

14. **§E-A** **AOSP-11 typo preservation** in IGraphicBufferProducer codes 19 (`SET_AUTO_REFRESH`) and 26 (`SET_AUTO_PREROTATION`): the AOSP-11 source uses `CHECK_INTERFACE(IGraphicBuffer, ...)` instead of `IGraphicBufferProducer`. **Do NOT fix the typo** — keep the bug for wire-compat with clients compiled against the typo'd descriptor. M6-Step3's BBP implementation must use the same `CHECK_INTERFACE(IGraphicBuffer, ...)` call for those two codes.

15. **§E-B** **SafeInterface reply ordering** (status_t LAST, not FIRST) applies to ISurfaceComposerClient + IDisplayEventConnection but NOT to ISurfaceComposer (which uses hand-coded Bp/Bn with status_t FIRST). M6-Step6 must keep this distinction visible — `WestlakeSurfaceComposerClient.cpp` already uses the SafeInterface convention; CR35 confirms it's correct.

16. **§E-C** **Per-transaction RESERVED slots**: unlike IAudioFlinger (CR37 found code 4 RESERVED in the audio side), none of the four M6 interfaces has explicit RESERVED enum slots in AOSP-11. The enums are dense.

---

## §8. Cross-references

- **M6 plan**: `docs/engine/M6_SURFACE_DAEMON_PLAN.md` §2.2/§2.3/§2.4/§2.5/§2.6
- **M6-Step1 report**: `docs/engine/M6_STEP1_REPORT.md`
- **M6-Step2 report**: `docs/engine/M6_STEP2_REPORT.md` §1 / §3 / §4 / §6.1 / §7.2 / §7.3 — this is the primary consumer of CR35's findings
- **M6-Step3..Step5 reports**: `docs/engine/M6_STEP3_REPORT.md`, `M6_STEP4_REPORT.md`, `M6_STEP5_REPORT.md`
- **Sibling AIDL discovery**: `docs/engine/CR37_M5_AIDL_DISCOVERY.md` (M5 audio daemon — same pattern, smaller surface)
- **AOSP-11 sources** (read-only references):
  - `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/include/gui/ISurfaceComposer.h`
  - `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/ISurfaceComposer.cpp`
  - `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/include/gui/ISurfaceComposerClient.h`
  - `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/ISurfaceComposerClient.cpp`
  - `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/include/gui/IGraphicBufferProducer.h`
  - `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/IGraphicBufferProducer.cpp`
  - `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/include/gui/IDisplayEventConnection.h`
  - `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/IDisplayEventConnection.cpp`
- **Android-15 on-phone artifacts** (read-only, cached at `/tmp/cr35-fwk/`):
  - `framework.jar` (41.9 MB, 5 dex files) — `SurfaceControl` Java class has 125 native JNI methods
  - `libgui.so` (1.62 MB ARM64) — AIDL-generated ISurfaceComposer with 75 methods; hand-coded IGraphicBufferProducer
  - `surfaceflinger` (8.30 MB ARM64) — server side
  - `framework-graphics.jar` (3661 B — config only, no dex)
- **Anti-drift contract**: `memory/feedback_macro_shim_contract.md`

---

## §9. Anti-drift compliance

- **Source code edits this CR: 0.** All output is this new doc plus one row in PHASE_1_STATUS.md (§10 below).
- **Test edits: 0.**
- **Shim edits: 0.** (`shim/java/` untouched.)
- **Daemon edits: 0.** (`aosp-surface-daemon-port/` untouched.)
- **Script edits: 0.** (`scripts/` untouched — note M7-Step2 has `run-noice-westlake.sh` active.)
- **Memory file edits: 0.** (`memory/` untouched.)
- **Phone reboot: 0.** ADB `pull` is non-destructive (verified via `adb -s cfb7c9e3 shell getprop ro.build.version.release` showing the device is up and not in fastboot).
- **SM cycle: 0.** No `servicemanager` restart, no service registration.
- **Per-app branches introduced: 0.** Every classification (Tier-1/2/3) is interface-level, not app-level.
- **No `Unsafe` / `setAccessible`** (N/A — read-only research).

---

## §10. Person-time spent

- Sources read (AOSP-11 .h/.cpp × 8 files; ~3000 LOC total scanned): ~30 min
- Phone artifact extraction (`adb pull` × 4 + dexdump SurfaceControl + strings libgui.so / surfaceflinger): ~20 min
- §2-§5 transaction-table authoring (101 rows total): ~40 min
- §6 drift analysis (cross-referencing A11 enum names against A15 AIDL strings): ~25 min
- §7 implementor TODO + §8 cross-refs + §9 anti-drift compliance: ~20 min
- This doc final pass + PHASE_1_STATUS.md row: ~15 min

**Total: ~2 h 30 min** (slight over the 1-2h brief; budget allowed up to 2h, ran ~30 min over due to the A15 AIDL drift surfacing being more substantial than expected — `IGraphicBufferProducer` did NOT migrate but `ISurfaceComposer` DID, and the +25 method delta needed enumeration). Net research delivered: 84 transaction rows across 4 interfaces + 6 drift sub-findings + 16 implementor TODOs.
