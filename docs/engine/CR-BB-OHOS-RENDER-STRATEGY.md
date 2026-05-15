# CR-BB — Top-Down OHOS Render Strategy

**Date:** 2026-05-15
**Author:** strategic research after current DRM-direct path was rejected as architecturally wrong
**Status:** PROPOSAL (READ-ONLY research; no code touched)
**Supersedes (when adopted):** the implicit "SoftwareCanvas + drm_inproc_bridge" production path
**Cross-refs:** `BINDER_PIVOT_ARCHITECTURE.md`, `CR60_BITNESS_PIVOT_DECISION.md`, `CR61_BINDER_STRATEGY_POST_CR60.md`, `cr66-e10-libsurface/.../CHECKPOINT.md`, `cr67-compose-spike/.../CHECKPOINT.md`

---

## TL;DR

The current DRM-direct path is a dead end for first-class coexistence with OHOS apps because it (a) drops most Skia ops (text, bitmap, path) on the floor in our hand-rolled SoftwareCanvas, (b) kills `composer_host` to take DRM master, and (c) cannot share the screen with any other OHOS surface. We must insert Westlake **above** the OHOS compositor, not under it.

The recommended path is **Candidate C (XComponent host HAP) for the MVP**, with **Candidate A (direct `RSSurfaceNode` producer via `WindowSessionImpl`) staged as the 12-week production target** once the binder/SELinux work in Phase 2 lands. We **defer Candidate B** (raw `RSCanvasNode` IPC) — it would only buy us partial Skia and still demands the same forbidden IPC.

CR61's "no `libipc.dylib.so` direct dlopen" rule is preserved: every recommended candidate either (1) uses libgraphic_2d / libwm transitively (same loophole CR66 used) or (2) crosses the host-HAP NAPI boundary (no libipc in our process).

---

## 1. How ArkUI renders, end to end

**1a. ArkTS → ACE FrameNode.** `Text("Hello").fontSize(20)` parses to a JS object; ACE's declarative bridge (`foundation/arkui/ace_engine/frameworks/bridge/declarative_frontend/`) translates it to a `FrameNode` (`frameworks/core/pattern/...`). FrameNodes are the Android-`View` equivalent.

**1b. FrameNode → RSNode.** Each visible FrameNode owns a `Rosen::RSNode` from libgraphic_2d. Three subtypes:
- `RSCanvasNode` (`rosen/modules/render_service_client/core/ui/rs_canvas_node.h:28`) — records `DrawCmdList` ops client-side, ships to render_service. `BeginRecording(w,h)` returns an `ExtendRecordingCanvas` (line 45) with the full Drawing API.
- `RSCanvasDrawingNode` — server owns a persistent canvas; clients stream incremental commands. ArkTS `<Canvas>`.
- `RSSurfaceNode` (`rosen/modules/render_service_client/core/ui/rs_surface_node.h:52-113`) — owns an actual `OHOS::Surface` (BufferQueue producer). The app produces frames via NativeWindow; render_service consumes. ArkTS XComponent and SceneBoard's leash windows use this (`window_scene/session/host/src/scb_system_session.cpp:35`).

**1c. Client-side recording.** `RSCanvasNode::BeginRecording` records into `Drawing::DrawCmdList` (`rosen/modules/2d_graphics/include/recording/draw_cmd_list.h:26`); every `OH_Drawing_*` call appends a `DrawOpItem`. No pixels yet — only commands. The cmdlist is the wire format.

**1d. Client → render_service IPC.** Cmdlists batch into `RSTransactionData` and ship via `RSTransactionProxy` → `RSRenderServiceClient` → `RSRenderServiceConnectionProxy::SendRequest` (`rosen/modules/render_service_base/src/platform/ohos/rs_render_service_connection_proxy.cpp`). Connection is acquired via samgr in `rs_render_service_connect_hub.cpp:105`: `SystemAbilityManagerClient::GetInstance().GetSystemAbilityManager()->CheckSystemAbility(RENDER_SERVICE)`. Kernel binder carries the parcel.

**1e. RenderService composition (Skia invoked here).** `RSMainThread` (`rosen/modules/render_service/core/pipeline/rs_main_thread.cpp`) on vsync walks the render tree, replaying cmdlists into `RSPaintFilterCanvas` (`render_service_base/include/pipeline/rs_paint_filter_canvas.h`) — a Skia-backed `Drawing::Canvas`. Skia adapter: `2d_graphics/src/drawing/engine_adapter/skia_adapter/skia_canvas.cpp`. For RSSurfaceNodes, the renderer composites the BufferQueue's latest buffer instead.

**1f. RenderService → HDF → DRM/KMS.** Composited frame → `RSHardwareThread` → `composer_host` (`drivers/peripheral/display`) → DRM → panel. HW-overlay composition decided here.

**1g. WindowManager.** Window stack, focus, z-order live in `window_scene/session_manager/` (SceneBoard). Window's visible surface is the `RSSurfaceNode` created in `WindowSessionImpl::CreateSurfaceNode` (`wm/src/window_session_impl.cpp:172`). Session manager arranges z-order on the display's RSNode tree.

**Key separation:** *recording* in the app process; *rasterization* in render_service; *scanout* in composer_host. Skia runs server-side except for off-screen rendering.

## 2. Skia integration

Skia runs **server-side in render_service** (cmdlist replay; RSSurfaceNode composition); it runs **client-side** when an app draws into a `Drawing::Bitmap`-backed canvas or any GraphicBuffer-bound canvas (off-screen render).

Libraries on DAYU200:
- `libskia_canvaskit.z.so` — full Skia (CPU + Ganesh GPU).
- `lib2d_graphics.z.so` — OHOS Drawing C++ API on Skia via `2d_graphics/src/drawing/engine_adapter/skia_adapter/`.
- `libnative_drawing.so` — the stable C NDK (`OH_Drawing_*`).

Public surface:
- `OH_Drawing_*` C NDK (`interface/sdk_c/graphic/graphic_2d/native_drawing/*.h`) — stable, ABI-locked, **dlopen-able from any process**. Westlake's `dalvik-port/compat/oh_drawing_bridge.c` (1119 LOC) already binds it.
- `Drawing::Canvas` C++ API — unstable C++ ABI; don't link.
- `RSPaintFilterCanvas` — internal to render_service, no public surface.

GPU: Ganesh-GL default; Vulkan on supported boards. `OH_Drawing_CanvasBind(canvas, bitmap_or_GraphicBuffer)` gives CPU canvas.

**Conclusion:** any process can use Skia for off-screen rasterization via `OH_Drawing_*` + `dlopen libnative_drawing.so`. To put pixels on screen still requires a surface acquired from render_service.

## 3. WindowManager + RenderService architecture

WindowManager (SceneBoard, DAYU200 7.0) owns: session table (`session_manager/src/scene_session_manager.cpp`), z-order via the `RSDisplayNode` child list (`window_scene/session/host/src/scb_system_session.cpp:35` creates `APP_WINDOW_NODE` RSSurfaceNodes under the display), focus/input routing (via MMI), system bars, animations.

**App ↔ WMS protocol:** `Window::Create(name, option, abilityContext)` → `wm/src/window.cpp:64` → `WindowSessionImpl::Create` → samgr binder → SceneBoard's `SceneSessionManager::CreateAndConnectSpecificSession`. SceneBoard verifies the AbilityRuntime token, allocates SessionId, creates a server-side `APP_WINDOW_NODE` RSSurfaceNode, and returns `sptr<ISession>` + the parcelable RSSurfaceNode (`RSSurfaceNode::Unmarshalling`).

**WMS ↔ RS:** shared global RSNode ID space. WMS calls `RSDisplayNode::AddChild(rsSurfaceNode)` to z-order the window; render_service picks up next vsync. BufferQueue producer lives app-side (`OHOS::Surface` / `OHNativeWindow*`), consumer lives in render_service.

**Frame flow:** `OH_NativeWindow_NativeWindowRequestBuffer` → dequeue GraphicBuffer → app draws → `OH_NativeWindow_NativeWindowFlushBuffer` → enqueue → render_service composes next vsync.

This is the architecture we plug into.

## 4. Westlake insertion-point candidates

### Candidate A — `RSSurfaceNode` producer via WindowManager (right answer, hardest)

- **Insert:** at the Window boundary. Westlake spawns a window, gets a producer, runs render loop into it.
- **Own:** an `RSSurfaceNode` (server-side by SceneBoard) + `OHNativeWindow*` producer end.
- **Window acquisition:** the hard part. SceneBoard needs an AbilityRuntime token. Two options: (A1) wrap `dalvikvm` in a signed HAP that calls `Window::Create` for us and hands the producer over UDS/memfd; (A2) extend `westlake-servicemanager` to mint a token-equivalent and call `wm` directly. A2 overlaps CR41 Phase-2 M11/M12.
- **Draw flow:** Java `Canvas.drawText/Bitmap/Path` → shim → `OH_Drawing_*` NDK in-process → Skia rasterizes into the dequeued GraphicBuffer (zero copy) → flush.
- **Lose:** initial complexity in HAP packaging; GPU path needs follow-up.
- **Gain:** true first-class window; z-order, focus, multi-app, system UI, animations.
- **Effort:** 6-10 person-weeks (HAP + AbilityContext shim + producer wiring + Skia bind).
- **CR61 compat:** A1 keeps `libipc.dylib.so` in the HAP process, never in `dalvikvm` — contract-clean. A2 requires a CR61 relaxation.

### Candidate B — `RSCanvasNode` cmdlist via direct RS IPC (deferred)

- **Insert:** below WMS but above render_service; we build cmdlists client-side and ship them.
- **Own:** an `RSCanvasNode`; no BufferQueue.
- **Window acquisition:** still needs a parent `RSSurfaceNode` — same blocker as A. Does NOT solve acquisition; only changes wire format.
- **Effort/CR61:** similar to A.
- **Verdict:** not separately viable. Roll into A as a later optimization (smaller wire, server-side GPU).

### Candidate C — XComponent host HAP (MVP — the *only* path that works in weeks)

- **Insert:** at the NAPI boundary. A signed ArkTS HAP hosts `<XComponent type="surface">` and exposes a NAPI method handing `dalvikvm` an `OHNativeWindow*`.
- **Own:** an `OHNativeWindow*` provided by the host HAP. HAP owns the window, we own pixels.
- **Window acquisition:** handled by the HAP for free. XComponent's `OnSurfaceCreated(component, void* window)` callback (`native_interface_xcomponent.h:299`) yields a render_service-attached `OHNativeWindow*` via the standard ArkTS path. We either marshal that pointer to a sibling dalvikvm process, or run dalvikvm as a worker thread inside the HAP.
- **Draw flow:** Java Canvas → existing `oh_drawing_bridge.c` (1119 LOC, full OH_Drawing_*) → canvas bound to the GraphicBuffer dequeued from the XComponent's NativeWindow → flush. **In-process Skia; full text/bitmap/path.**
- **Lose:** single XComponent per HAP — Westlake apps are sub-views, not peer desktop windows. Acceptable for MVP.
- **Gain:** coexistence with all OHOS apps; composer_host stays alive; full Skia; no DRM-master; production-shippable HAP; SELinux-clean (HAP contexts are allowed).
- **Effort:** 2-4 person-weeks once HAP scaffold is up. Agent 6's earlier XComponent attempt failed at the dalvikvm-needs-the-window-pointer boundary, not at "XComponent + dalvikvm render together."
- **CR61 compat:** clean. libipc never enters `dalvikvm`; HAP holds the binder; NAPI bridge hands us only an opaque pointer.

This pattern is how OHOS apps embed Cocos / Unity / Flutter today.

### Candidate D — Surface from `OH_NativeImage`

`OH_NativeImage_AcquireNativeWindow` (`interface/sdk_c/graphic/graphic_2d/native_image/native_image.h:96`) returns an **app-side consumer** surface, not a render_service-attached one. Same trap CR66 hit. Not viable.

## 5. Recommendation

**Sequence: Candidate C first (MVP, 4 weeks), then Candidate A on top of CR41 Phase-2 (production, 12 weeks).**

Candidate C unlocks the strategic goal — Westlake apps render *through* the OHOS compositor, not under it — in the smallest available engineering window. It uses libgraphic_2d Skia for full fidelity, preserves composer_host, restores multi-app coexistence, and respects CR61 verbatim. Macro-shim contract unchanged: no per-app branches; no `Unsafe.allocateInstance`; no `setAccessible`. The OH_Drawing JNI is a host adapter, not a framework method.

Candidate A is the eventual destination. Its window-acquisition gap is the same blocker CR41 Phase-2 already plans to solve (AbilityContext-equivalent at the binder/IPC layer is needed for M11/M12 anyway). When that lands, `dalvikvm` calls `Window::Create` itself; the HAP scaffold becomes optional.

**Why this beats DRM-direct:** DRM-direct kills composer_host, breaks every other OHOS surface, and drops ~80% of Skia ops to the hand-rolled SoftwareCanvas. Candidate C uses real Skia via the public NDK, composes with real WMS+RS, ships as a real HAP — first-class.

**CR61 implications:** none for C. libipc stays in the HAP; never enters dalvikvm. The handoff is one `OHNativeWindow*` pointer. The libipc "transitively through a library we dlopen" loophole is preserved by routing through libnative_window only. A relaxation for direct `libgraphic_2d.z.so` dlopen (path A2) is a separate later CR.

**4-week milestones (MVP, C):**
1. **W1**: signed HAP scaffold; `<XComponent type="surface">` with NAPI export; "JNI bridge prints OHNativeWindow* address." Verify `composer_host` PID stable.
2. **W2**: bind OH_Drawing canvas to the GraphicBuffer dequeued from XComponent; one `OH_Drawing_CanvasDrawText` lands a visible pixel. MVP-2-skia gate.
3. **W3**: wire `android.graphics.Canvas` shim → OH_Drawing bridge (text, rect, bitmap, path, clip, transform, save/restore). Delete SoftwareCanvas.
4. **W4**: noice decor view renders through real Skia. Replace `drm_inproc_bridge` with the XComponent path. Keep DRM as fallback flag.

**12-week milestones (A staged):**
5. **W5-7**: Hilt/Fragment lifecycle wiring (CR67-followup) so noice's real main_activity reaches the render loop.
6. **W8-9**: touch — XComponent `OnTouchEvent` → Westlake `MotionEvent`.
7. **W10-12**: AbilityContext-light shim in `westlake-servicemanager` (CR41 M11 prereq) → `dalvikvm` calls `Window::Create` directly → Candidate A lit. HAP optional.

**First 3 days:**
- **D1**: read `test/xts/acts/arkui/ace_ets_xcomponent/entry/src/main/cpp/render/native_xcomponent.cpp` end-to-end (XTS XComponent producer reference). Stand up a minimal HAP rendering a green rect via XComponent NAPI on DAYU200. Confirm `composer_host` PID unchanged.
- **D2**: NAPI method `__westlake_get_native_window(): bigint` returning the `OHNativeWindow*` pointer. JNI-side: `OH_Drawing_CanvasBind` to the dequeued GraphicBuffer. `OH_Drawing_CanvasDrawColor(canvas, 0xFFFF0000)` shows red.
- **D3**: `OH_Drawing_CanvasDrawTextBlob` with a registered font. `oh_drawing_bridge.c` already has the function-pointer scaffolding.

## 6. Honest caveats

**Could not determine:**
- Whether `dalvikvm` can run as a *thread inside* the HAP process (vs. separate process + NAPI handoff). ArkTS VM and dalvikvm may share-process cleanly or clash on signal handlers / TLS / atfork. Needs 2-day spike. Thread-in-HAP dramatically simplifies the architecture.
- Whether DAYU200 7.0 accepts a self-signed HAP in developer mode or needs a real Huawei dev cert.
- The exact NAPI ABI for cross-VM `OHNativeWindow*` handoff (NAPI `external` is per-VM; cross-process needs binder/memfd).

**Obscured ArkUI internals:**
- `RSTransactionData` cmdlist wire format is partly templated and not version-stable. Mitigation: Candidate C ships rasterized GraphicBuffers, not cmdlists.
- ACE "render thread" vs "UI thread" boundary varies by version; we sidestep by not running ACE.

**Risks of the recommended path:**
1. **HAP packaging.** A signed HAP shipping a 30 MB `dalvikvm` may not pass app-store policy; production probably needs vendor partnership or system-app sideload.
2. **Cross-process NAPI.** If thread-in-HAP fails, ship `OHNativeWindow*` via binder/memfd — which approaches the CR61 boundary.
3. **OHOS NAPI ABI drift** across 5 → 6 → 7. Pin to 7.0/DAYU200; multi-version is separate.
4. **Single-XComponent-per-HAP.** All Westlake-hosted apps live inside one XComponent — sub-views, not peer desktop windows. Acceptable for MVP; resolved by Candidate A.

**Fallback if Candidate C is infeasible:**
- Drop to Candidate A2: relax CR61 to allow transitive `dlopen libgraphic_2d.z.so`, mint an AbilityContext stub in `westlake-servicemanager`, call `Window::Create` directly. 8-12 weeks. libgraphic_2d / librender_service_client SELinux contexts on DAYU200 appear world-readable (consistent with CR66 dlopen results); samgr policy for our domain remains the gate (CR61 §4).
- If both fail: M12 "own compositor" (Westlake ships its own SurfaceFlinger-equivalent, owns DRM, provides composer-host-style API to OHOS apps). Multi-quarter. The current SoftwareCanvas + drm_inproc_bridge is a *subset* of that path and should NOT be polished further — every hour on SoftwareCanvas is wasted vs. Candidate C.

**Already in place for Candidate C:**
- `dalvik-port/compat/oh_drawing_bridge.c` (1119 LOC) — OH_Drawing_* dlopen scaffold, function-pointer typedefs for Bitmap/Canvas/Pen/Brush/Path/Font/TextBlob. ~60% of the JNI layer done.
- `dalvik-port/compat/ohos_dlopen_smoke.c` — proven `dlopen libnative_window.so` from 32-bit dalvikvm.
- `dalvik-port/compat/dalvik_canvas_skia.c` (642 LOC) — canvas-to-Skia translation, repurposable.
- V2 substrate's `android.graphics.Canvas` shim already routes through a JNI boundary we control.

What's missing is the HAP scaffold and NAPI bridge — ~2 weeks of focused work. Cheapest available first move; lights up the strategic goal in 4 weeks.
