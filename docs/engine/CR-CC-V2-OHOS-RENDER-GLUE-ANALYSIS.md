# CR-CC — Top-Down OHOS Render Glue Analysis for V2-Substrate Port

**Date:** 2026-05-15
**Author:** agent 37 (research-only; user holds all commits)
**Status:** PROPOSAL — pending codex review before implementation
**Cross-refs:** `CR-BB-OHOS-RENDER-STRATEGY.md` (deferred; sub-view path), `CR41_PHASE2_OHOS_ROADMAP.md` (M9-M13), `CR60_BITNESS_PIVOT_DECISION.md`, `CR61_BINDER_STRATEGY_POST_CR60.md`, `BINDER_PIVOT_DESIGN_V2.md`, `M6_STEP6_REPORT.md`, memory: `project_v2_ohos_direction.md`, `feedback_additive_shim_vs_architectural_pivot.md`, `feedback_macro_shim_contract.md`, `/tmp/cr-bb-phase1-render-binder-analysis.md`

---

## TL;DR

Plug Westlake's V2 substrate into OHOS render_service at the **`RSSurfaceNode` producer boundary** acquired through `librender_service_client.z.so` (an `RSDisplayNode::AddChild` mirror-child path), with the existing surface daemon (`westlake-surface-daemon`) reframed as a **client-of-render_service** that holds the producer-side `Surface`/`OHNativeWindow*` and copies the BufferQueueCore frames it already manages onto frames dequeued from render_service's BufferQueue. Same daemon binary; same AOSP `IGraphicBufferProducer` Bn surface for dalvikvm; only the **scanout backend** swaps from "DLST opcodes over stdout" (Phase 1) to "OH_NativeWindow_RequestBuffer/FlushBuffer against an RSSurfaceNode producer" (Phase 2). Z-order participation comes from `RSDisplayNode::AddChild` on a mirror screen (CR61-clean) for MVP, escalating to first-class WMS `WindowSession` only after a CR61 amendment for `IsStartByHdcd`-signed dalvikvm in dev mode. Audio is symmetric: `OH_AudioRenderer` swap inside the existing audio daemon. **No new IPC stack; no new HAP; no SoftwareCanvas.** ~9-12 person-weeks total.

---

## Section 1 — OHOS render pipeline end-to-end

A frame from an ArkTS app:

**1a. ArkTS → ACE FrameNode.** `Text("Hi")` parses to a JS object; declarative bridge (`foundation/arkui/ace_engine/frameworks/bridge/declarative_frontend/`) translates to `FrameNode` (`frameworks/core/components_ng/base/frame_node.cpp`) — Android-`View` equivalent.

**1b. FrameNode → RSNode tree.** Each visible FrameNode owns a `Rosen::RSNode` from `librender_service_client.z.so`:
- `RSCanvasNode` (`render_service_client/core/ui/rs_canvas_node.h`) — records `DrawCmdList` ops client-side, ships to render_service.
- `RSCanvasDrawingNode` — server owns persistent canvas; clients stream incremental commands. ArkTS `<Canvas>`.
- `RSSurfaceNode` (`render_service_client/core/ui/rs_surface_node.h`, impl `.cpp:47-132`) — owns an actual `OHOS::Surface` BufferQueue producer. App produces frames via `OHNativeWindow*`; render_service consumes. ArkTS `<XComponent>`, SceneBoard leash windows, every WMS-managed window.

**1c. Client-side recording.** `RSCanvasNode::BeginRecording` records into `Drawing::DrawCmdList` (`rosen/modules/2d_graphics/include/recording/draw_cmd_list.h`); every `OH_Drawing_*` call appends a `DrawOpItem`. No pixels yet.

**1d. Client → render_service IPC.** Cmdlists batch into `RSTransactionData` and ship via `RSTransactionProxy` → `RSRenderServiceClient` → `RSRenderServiceConnectionProxy::SendRequest` (`rs_render_service_connection_proxy.cpp:174,208`). Connection acquired via samgr in `rs_render_service_connect_hub.cpp:90-147`: `SystemAbilityManagerClient::GetInstance().GetSystemAbilityManager()->CheckSystemAbility(RENDER_SERVICE)` → `iface_cast<RSRenderServiceProxy>` → `CreateConnection(token)`. Wire is OHOS-libipc parcels over kernel `/dev/binder` (`foundation/communication/ipc/ipc/native/src/mock/source/binder_connector.cpp:59`).

**1e. RenderService composition (Skia invoked here).** `RSMainThread` (`render_service/core/pipeline/rs_main_thread.cpp`) on vsync walks the render tree, replaying cmdlists into `RSPaintFilterCanvas` — a Skia-backed `Drawing::Canvas` (`2d_graphics/src/drawing/engine_adapter/skia_adapter/skia_canvas.cpp`). For RSSurfaceNodes, the renderer composites the BufferQueue's latest buffer instead of replaying cmdlists.

**1f. RenderService → HDF → DRM/KMS.** Composited frame → `RSHardwareThread` → `composer_host` (`drivers/peripheral/display`) → DRM → panel.

**1g. WindowManager.** Window stack, focus, z-order live in `window_scene/session_manager/scene_session_manager.cpp` (SceneBoard). The window's visible surface is the `RSSurfaceNode` created by `WindowSessionImpl::CreateSurfaceNode` (`wm/src/window_session_impl.cpp:155-173`). For app main windows, hostSession_ comes from AbilityRuntime + AMS via the AbilityContext; `WindowSceneSessionImpl::Create` (`wm/src/window_scene_session_impl.cpp:320-369`) uses `Connect()` (path A) for main windows and `CreateAndConnectSpecificSession()` (path B, `:131-191`) for system/sub windows. Path B calls `WindowAdapter::CreateAndConnectSpecificSession` → `SessionManager::GetSceneSessionManagerProxy()` (samgr `WINDOW_MANAGER_SERVICE_ID`) → `SceneSessionManager::CreateAndConnectSpecificSession` (`window_scene/session_manager/src/scene_session_manager.cpp:1617-1674`) which does `CheckSystemWindowPermission` (line 1691-1720). That permission check accepts `IsSystemCalling()` OR `IsStartByHdcd()` (`session_permission.cpp:163-173` — calling-process accesstoken processName == "hdcd") for system windows.

**1h. Per-frame BufferQueue.** `OH_NativeWindow_NativeWindowRequestBuffer` (`graphic_2d/interfaces/inner_api/surface/external_window.h:339`) → dequeue GraphicBuffer → app draws (Skia or memcpy) → `OH_NativeWindow_NativeWindowFlushBuffer` → enqueue → render_service composes next vsync. Producer-side bridge: `CreateNativeWindowFromSurface(sptr<Surface>*)` (`graphic_surface/surface/src/native_window.cpp:33-55`) wraps the producer-side Surface in an `OHNativeWindow` for the C-NDK consumer.

**Key separation:** *recording* in app process; *rasterization* in render_service; *scanout* in composer_host. **WMS only owns Z-order** via `RSDisplayNode::AddChild`; render_service is independently reachable via samgr — RSSurfaceNode permission-gating in `RSIRenderServiceConnectionInterfaceCodeAccessVerifier` (`rs_irender_service_connection_ipc_interface_code_access_verifier.h`) gates only `TAKE_SURFACE_CAPTURE` and `SET_REFRESH_RATE_MODE`; `CREATE_NODE_AND_SURFACE` is **unrestricted**.

---

## Section 2 — Candidate glue points

### A — RSSurfaceNode producer via WindowSessionImpl::Create (full WMS first-class)
| Field | Value |
|---|---|
| Where | At Window/Session boundary in `wm/src/window_scene_session_impl.cpp:131,320`. |
| What we own | A real WMS `SceneSession` + `RSSurfaceNode` + producer-side `Surface`/`OHNativeWindow*`. WMS-managed Z-order, focus, animations. |
| API surface | `Window::Create()` from `interfaces/innerkits/wm/window.h`, in `libwm.z.so`; transitively pulls `librender_service_client.z.so`, `libsurface.z.so`. |
| IPC layer | Yes: samgr → `IMockSessionManagerInterface` → `ISessionManagerService` → `ISceneSessionManager` (window_scene IDL); samgr → `IRenderService` → `IRenderServiceConnection`. All via libipc. |
| CR61 | **Indirect via wrapper.** `libwm.z.so` is loaded; libipc enters our process transitively. CR61 §"MAY use libraries that internally call libipc" — borderline. Stronger: this requires a real `AbilityContext` token AMS issues. Without that, `WindowSceneSessionImpl::Create` for main windows fails (`hostSession_` is null). System-window subtype + `IsStartByHdcd` token works. |
| SELinux | Need `westlake_app` domain or inherit `hdcd` token. Dev board: hdcd shell already speaks libipc. |
| Z-order | Real peer window. |
| Per-frame | 2 binder calls (RequestBuffer + FlushBuffer) on `IBufferProducer`. |
| Effort | 6-10 person-weeks (libipc co-existence, AbilityContext stub, full Window IDL conformance). |

### B — RSCanvasNode cmdlist via direct RS IPC
| Field | Value |
|---|---|
| Where | Below WMS, above render_service; client builds cmdlists. |
| What we own | An `RSCanvasNode`; no BufferQueue. |
| API | `librender_service_client.z.so` (`RSCanvasNode`, `RSTransactionProxy`). |
| IPC | Yes — render_service via libipc. |
| CR61 | Indirect via wrapper (same as A). |
| SELinux | Same as A. |
| Z-order | Still needs a parent RSSurfaceNode/RSDisplayNode — does NOT solve acquisition. |
| Per-frame | 1 IPC (transaction batch); cmdlist replay server-side. Skia rasterizes server-side. |
| Effort | 4-6 weeks; doesn't solve peer-window goal alone. |
| Verdict | Not separately viable; consider as later optimization on top of A or G. |

### C — XComponent host HAP + same-process dalvikvm (CR-BB Candidate C)
| Field | Value |
|---|---|
| Where | NAPI boundary inside ArkTS HAP. |
| What we own | `OHNativeWindow*` handed by HAP from `<XComponent type="surface">`. |
| API | `OH_NativeXComponent_GetNativeWindow`, `OH_NativeWindow_*`. |
| IPC | None in dalvikvm; HAP owns libipc. |
| CR61 | Clean. |
| SELinux | HAP context (allowed). |
| Z-order | **Sub-view inside one HAP window** — fails the user's product goal. |
| Effort | 2-4 weeks. |
| Verdict | Rejected per `project_v2_ohos_direction.md` 2026-05-15. |

### D — Direct binder protocol (re-implement OHOS libipc inside dalvikvm)
| Field | Value |
|---|---|
| Where | Wire-format substitute: speak OHOS libipc parcels over `/dev/binder` from our libbinder. |
| What we own | Hand-rolled OHOS-IRemoteObject ABI. |
| API | None; we re-derive from `foundation/communication/ipc/ipc/native/src/core/`. |
| IPC | Yes — but our own implementation. |
| CR61 | **Spirit-violating.** CR61 forbids `dlopen("libipc.dylib.so")` to keep AOSP-binder semantics in our process tree; re-implementing that wire ABI in our libbinder is the same outcome. |
| SELinux | Open `/dev/binder` (vs `/dev/vndbinder`) from non-system token. |
| Z-order | Same as A once we can talk WMS/RS. |
| Per-frame | 2 binder calls. |
| Effort | 12-20 weeks (parcel format, IRemoteObject ABI, samgr re-implementation, drift maintenance). |
| Verdict | Rejected — explodes scope and contradicts CR61's coexistence model. |

### E — OH_NativeImage AcquireNativeWindow (CR-BB Candidate D)
APP-SIDE consumer surface; not render_service-attached. Not viable. Rejected by CR-BB §4.

### F — Hybrid (mock APK that mints surface + cross-process handoff)
| Field | Value |
|---|---|
| Where | Tiny ArkTS HAP creates an `RSSurfaceNode`, calls `surface->GetUniqueId()`, then sends the `uniqueId` (a `uint64_t`) over a UDS to dalvikvm. dalvikvm calls `SurfaceUtils::GetInstance()->GetSurface(uniqueId)` (`graphic_surface/surface/src/surface_utils.cpp:36`) — **but this is in-process state**, so the Surface object is NOT visible across processes by uniqueId alone. Requires also passing the `IBufferProducer` IRemoteObject across (binder fd in a parcel). |
| Verdict | Same engineering surface as A or C; gives nothing new. Skip. |

### G — Mirror-screen RSDisplayNode (the surprising candidate)
| Field | Value |
|---|---|
| Where | Bypass WMS entirely. Westlake creates its own `RSDisplayNode` with `screenId = MIRROR_SCREEN` (`render_service_client/core/ui/rs_display_node.cpp:25-37` — purely a transaction proxy command), parents its `RSSurfaceNode` under it, and either (i) drives a virtual screen that mirrors to physical via `RSScreenManager`, or (ii) requests an `expandDisplay`. |
| What we own | RSDisplayNode + RSSurfaceNode + producer-side `Surface`/`OHNativeWindow*`. |
| API | `librender_service_client.z.so` (no `libwm`). |
| IPC | Yes — render_service via libipc transitively (NO WMS). |
| CR61 | **Indirect via wrapper.** No `libipc.dylib.so` dlopen; only `librender_service_client.z.so`. |
| SELinux | Looser than A — only render_service contacted. RS only permission-gates `TAKE_SURFACE_CAPTURE` + `SET_REFRESH_RATE_MODE`. |
| Z-order | **No WMS participation.** Limits: cannot interleave with ArkTS app windows; mirror-screen path is parallel display, not co-display. **Not first-class peer.** |
| Per-frame | 2 binder calls. |
| Effort | 3-5 weeks (no WMS work). |
| Verdict | **Excellent stepping stone for the mock APK and MVP**, but does not deliver final product goal alone. Promote to A once CR61 amendment lands. |

### H — RSSurfaceNode + WMS, but as system window via IsStartByHdcd token
| Field | Value |
|---|---|
| Where | `WindowSceneSessionImpl::Create` taking `WINDOW_TYPE_FLOAT` or similar system subtype. |
| What we own | Same as A, but via system-window permission grant (`scene_session_manager.cpp:1714` — `IsSystemCalling() OR IsStartByHdcd()`). |
| API | `libwm.z.so`. |
| IPC | Yes — full WMS + RS, transitive libipc. |
| CR61 | **Same indirect-via-wrapper situation as A**, but bypasses the AbilityContext-token requirement. |
| SELinux | Inherit hdcd token (dev mode); production needs vendor-signed system app. |
| Z-order | Real WMS-managed peer window. Floating window subtype is acceptable as MVP "first-class." |
| Per-frame | 2 binder calls. |
| Effort | 4-6 weeks. |
| Verdict | **The realistic V2-OHOS production target** for dev-board MVP. Production phone deployment requires either (a) Westlake-signed vendor system app, or (b) `westlake_app` domain with explicit policy. |

---

## Section 3 — Recommendation

**Sequence: G (mirror-screen MVP, ~3 weeks) → H (system-window-via-hdcd-token, ~4-6 weeks) → A (full app-main-window, deferred).**

Rationale:

1. **G is the cleanest CR61 fit** — only `librender_service_client.z.so` is loaded; libipc enters via the same "transitive wrapper" loophole CR66 already validated. No `libwm` deps. Lets the surface daemon swap its scanout backend without disturbing the substrate.
2. **G also de-risks the entire pipeline before WMS work begins.** If `RSSurfaceNode::Create` from a Westlake process can be made to land a pixel on a mirror screen, every M11/M12 backend swap is then a strict additive optimization.
3. **H is the production peer-window target on dev boards.** It honors the `IsSystemCalling()/IsStartByHdcd()` gate the OHOS sources actually expose for `WINDOW_TYPE_FLOAT` (and `WINDOW_TYPE_DIALOG`/`TOAST`/`PIP` per `scene_session_manager.cpp:1703-1707`). On a dev board with hdcd-spawned dalvikvm, no AMS token is needed.
4. **A is the eventual destination** but requires AbilityContext stub + AMS interaction. Defer until after H validates and a CR61 §amendment is written for the libwm transitive load (the CR61 spirit is "don't dlopen libipc directly"; loading libwm transitively is contract-clean by precedent).

**CR61 implications:** G and H both rely on the existing CR61 loophole — Westlake processes MAY load `librender_service_client.z.so`, `libwm.z.so`, `libsurface.z.so`, etc., even though they internally call libipc. We do NOT call `libipc.dylib.so` directly, do NOT call `samgr` directly, do NOT register Westlake services on samgr. The dalvikvm process tree's IPC stack remains AOSP libbinder over `/dev/vndbinder`; the **only** OHOS-libipc surface in our process is the inbound API of `librender_service_client.z.so` (which fires CHECKSYSTEMABILITY at samgr internally, on the libipc side of the wrapper). This is identical to how the ohos_dlopen_smoke.c test already exercises `libnative_window.so` from 32-bit dalvikvm.

**Macro-shim contract:** unchanged. The render-glue is native daemon code (C++ in `westlake-surface-daemon`), not Java framework duplicates. The only Java touch is the existing `WindowManagerImpl`/`PhoneWindow`/`DecorView` substrate (V2) — those keep their AOSP-default behaviors and route their producer-side `Surface` through `WestlakeSurfaceComposerClient` as today.

**Why this beats CR-BB Candidate C:** C achieves coexistence as sub-views; G/H achieve coexistence as peer surfaces visible to the OHOS compositor at the same composition level as ArkTS app surfaces. Strategic alignment with `project_v2_ohos_direction.md` is direct.

**Why this beats DRM-direct (today's polish path):** DRM-direct kills `composer_host` and breaks every other surface; G/H let composer_host live and merely register Westlake's surfaces under render_service's normal composition.

---

## Section 4 — Mock APK validation plan

**Goal:** Prove that an OHOS userspace process holding only `librender_service_client.z.so` (no `libwm.z.so`, no AbilityContext) can:
1. Acquire an `RSDisplayNode` for the physical screen (mirror) OR an `RSSurfaceNode` parented to the physical RSDisplayNode.
2. Get a producer-side `Surface` and wrap it as `OHNativeWindow*`.
3. Push 60 frames of a known color pattern (red→green→blue cycle) via `OH_NativeWindow_NativeWindowRequestBuffer/FlushBuffer`.
4. Verify panel output matches input via on-board screenshot or a phone-camera capture cycle.
5. Confirm `composer_host` PID is unchanged across the run (no DRM-master fight).

**Files (~300-500 LOC total, ≤ 1 person-week):**

```
mock-render-glue/
├── BUILD.gn                       # ohos_executable, deps on librender_service_client + libsurface
├── mock_render_glue.cpp           # main: spawns RSDisplayNode/RSSurfaceNode, drives 60 frames
├── color_pattern.h/.cpp           # known-good pattern producer (RGBA8888, deterministic)
├── verify.sh                      # hdc shell snapshot + pixel diff
└── README.md                      # build steps, gate criteria
```

**Build steps:**
```
cd /home/dspfac/openharmony
hb build mock_render_glue --product-name rk3568   # or qemu_arm_linux_standard
hdc file send out/.../mock_render_glue /data/local/tmp/
hdc shell /data/local/tmp/mock_render_glue
```

**Test criteria (go/no-go):**
- [ ] Process attaches to render_service (RS connect log line appears).
- [ ] `RSSurfaceNode::Create` returns non-null; `GetSurface()` returns producer-side Surface.
- [ ] `OH_NativeWindow_NativeWindowRequestBuffer` returns 0; buffer pointer non-null; W×H matches request.
- [ ] 60 frames flush cleanly (`OH_NativeWindow_NativeWindowFlushBuffer` returns 0).
- [ ] On-screen output cycles through R/G/B (visual gate; or `take_surface_capture` if permission allows).
- [ ] `composer_host` PID before == PID after (no crash/restart).
- [ ] No SELinux denials in `dmesg | grep avc`.

**Decision:**
- **All PASS** → Proceed to M12 backend swap.
- **RS connect FAIL with EACCES/EPERM** → SELinux denial; document the policy gap, escalate to either policy module (CR61 Tier C) or run mock as `system` uid in dev mode.
- **Surface acquired but no pixel** → composition path issue; the mirror-screen path may need an explicit `SetScreenChangeCallback` or virtual-screen plumbing.
- **composer_host dies** → unexpected resource conflict; revisit Candidate H earlier than planned.

This mock is the **only thing standing between today's analysis and a 9-12 person-week implementation budget**. Build it first.

---

## Section 5 — V2-OHOS implementation roadmap (post-mock-validation)

### M10-arm32 — libbinder + servicemanager for arm-linux-ohos-musl (~1-2 person-days)
- Per CR61 §5: add `musl-arm32` target to `aosp-libbinder-port/Makefile` paralleling existing `musl` (aarch64) and `bionic` targets. Toolchain: OHOS Clang 15 with `--target=arm-linux-ohos --march=armv7-a -mfloat-abi=softfp`. Sysroot: `dalvik-port/ohos-sysroot-arm32/`.
- Output: `aosp-libbinder-port/out/arm32/{libbinder.so, servicemanager}`.
- Test: `HelloBinder.dex` under dalvikvm-arm32 → /dev/vndbinder → register/retrieve. Identical to M3 regression.
- Files: `aosp-libbinder-port/Makefile`, `aosp-libbinder-port/test/HelloBinder.dex`.

### M12 — westlake-surface-daemon for OHOS (~3-5 person-weeks)
- **Existing daemon** (`aosp-surface-daemon-port/native/`) already has the BufferQueueCore, `WestlakeGraphicBufferProducer` (AOSP `IGraphicBufferProducer` Bn handler), `WestlakeDisplayEventConnection` (BitTube vsync source), `MemfdGraphicBuffer`. **None of this changes.**
- **Backend swap**: replace the `DlstPipeBackend` (stdout opcodes) with a new `OhosRenderServiceBackend` that:
  1. `dlopen("librender_service_client.z.so")` + `dlopen("libsurface.z.so")` + `dlopen("libnative_window.so")`.
  2. On Bn `CREATE_SURFACE`, also call `RSSurfaceNode::Create` (and either G's mirror-display or H's `Window::Create` system window) to get a producer-side `OHOS::Surface*`. Wrap with `CreateNativeWindowFromSurface(&sptrSurface)` → `OHNativeWindow*`.
  3. On Bn `QUEUE_BUFFER` arriving from dalvikvm: dequeue an OHNativeWindow buffer with `OH_NativeWindow_NativeWindowRequestBuffer`; `memcpy` from our memfd-backed `GraphicBuffer` to the OHOS buffer; `OH_NativeWindow_NativeWindowFlushBuffer`.
  4. On vsync: `OH_NativeWindow_NativeWindowSetTargetFrameRate` plus `RegisterCallback` for the callback-based vsync (or fall through to render_service's vsync via `IDisplayEventConnection`-equivalent on the OHOS side); forward to BitTube to dalvikvm.
- **Zero-copy follow-up** (M12.5): replace memcpy with dma-buf fd handoff once we confirm OHOS GraphicBuffer fd-import path. Phase 1 already does memfd; Phase 2 should map to OHOS `BUFFER_USAGE_MEM_DMA`.
- Files: new `aosp-surface-daemon-port/native/OhosRenderServiceBackend.{h,cpp}`; `Makefile` `#ifdef WLK_OHOS_BACKEND`; deps `librender_service_client.z.so`, `libsurface.z.so`.
- Test: `m6step5-smoke.sh` re-run on OHOS dev board with backend selected → existing test surface (red/green/blue) appears on panel.

### M11 — westlake-audio-daemon for OHOS (~2 person-weeks)
- Symmetric: existing `aosp-audio-daemon-port/` has `WestlakeAudioTrack` Bn handler over libbinder + AAudio backend. Swap backend to `OH_AudioRenderer` (`libohaudio.so`).
- Pull-mode adapter: AOSP IAudioTrack push-style `write(buffer, frames)` → OHOS pull-style `OnWriteData` callback. Use a SPSC ring buffer.
- Files: new `aosp-audio-daemon-port/native/OhosBackend.{h,cpp}`; deps `libohaudio.so`.
- Test: HelloAudio.dex → 440 Hz sine wave on speaker.

### M13 — noice on OHOS dev board (~1-2 person-weeks)
- Boot: hdc shell launches `westlake-servicemanager` → `westlake-surface-daemon` → `westlake-audio-daemon` → `dalvikvm -Xbootclasspath=... noice.apk`.
- Verify: noice Welcome screen appears at panel resolution; tabs work; touch routes back through input substrate.
- Files: orchestrator script under `aosp-surface-daemon-port/`; SELinux policy module under `device/board/.../sepolicy/westlake.te` if Tier-C escalation needed.

**Total V2-OHOS budget:** 9-12 person-weeks (M10 + M12 + M11 + M13 + 20% buffer for the Hilt/Resources surprises that always appear).

---

## Section 6 — Honest caveats

**Could not determine:**
1. **Mirror-screen RSDisplayNode practical viability** — RSDisplayNode::Create's transaction is just "register an ID"; render_service may reject if the screenId conflicts with a real screen, or may silently accept and never composite. Needs the mock APK to confirm. If it fails, fall back to H earlier.
2. **The actual SELinux label of dalvikvm** spawned by hdcd shell on rk3568 OHOS standard — could be `u:r:hdcd:s0` (system service domain, likely binder-allowed) or `u:r:shell:s0`. Determines whether render_service IPC works without policy work. Needs ½-day on-device probe.
3. **Whether `librender_service_client.z.so`'s libipc dependency drags samgr into our process eagerly or lazily** — eager would mean even initialization triggers a samgr connect; lazy means only the first RSSurfaceNode::Create. Lazy is fine; eager creates a noisier process attach surface.
4. **Hardware availability** — rk3568 dev board is mandatory for M9-M13 validation. If unavailable, the entire roadmap is gated until acquired (per CR41 §8 R1).
5. **The exact 7.0/8.0 OHOS API drift on RSSurfaceNode/Window IDLs** between this source tree and the actual on-board libraries — `nm -D` on the board after first hardware contact will reveal.

**Obscured ArkUI internals:**
- ACE FrameNode → RSNode binding is templated and version-specific; we sidestep by not running ACE.
- `RSTransactionData` cmdlist wire format is templated; we sidestep by using the `RSSurfaceNode` BufferQueue path (rasterized frames, not cmdlists).

**Risks of the recommended path:**
1. **Mirror-screen RSDisplayNode may not exist in OHOS 7.0 source** as a freely-creatable virtual screen — `RSScreenManager` may reject. **Mitigation:** mock APK validates; if fails, jump to H.
2. **CR61 spirit-violation drift:** loading `librender_service_client.z.so` transitively brings libipc into our process. If a future CR61 audit decides "transitive libipc is also forbidden," the entire V2-OHOS path collapses. **Mitigation:** write CR61.1 amendment now memorializing the wrapper-loading loophole as the V2-OHOS architectural position.
3. **memcpy-per-frame** between our memfd GraphicBuffer and the render_service-supplied buffer is a perf cliff at 60 Hz × 1080×2400×4B = ~600 MB/s. **Mitigation:** M12.5 dma-buf fd swap; OHOS BufferQueue supports fd-import. Acceptable interim cost.
4. **Hardware bottleneck** — rk3568 not in lab today (per `MEMORY.md`); the entire plan is gated on procurement.

**Fallback if recommended path fails:**
- If G fails (no RSSurfaceNode without WMS): jump to H (system window via hdcd token).
- If H fails (libwm spirit-violates CR61): write CR61.1 amendment OR retreat to **CR-BB Candidate C** (XComponent HAP) as MVP and document that the user's "first-class peer windows" goal slips to a future architecture phase.
- If C also fails for product reasons: M12 "own compositor" — Westlake ships its own SurfaceFlinger-equivalent, owns DRM, provides composer-host-style API to OHOS apps. Multi-quarter, last resort.

**Already in place for the recommended path:**
- `dalvik-port/compat/oh_drawing_bridge.c` (1119 LOC) — OH_Drawing_* dlopen scaffold for in-process Skia (not strictly needed in G/H but de-risks the future client-side rasterization optimizations).
- `dalvik-port/compat/ohos_dlopen_smoke.c` — proven `dlopen libnative_window.so` from 32-bit dalvikvm.
- `aosp-surface-daemon-port/native/{WestlakeSurfaceComposer,WestlakeGraphicBufferProducer,WestlakeDisplayEventConnection,MemfdGraphicBuffer}.cpp` — entire AOSP-side daemon stays.
- 32-bit musl sysroot at `dalvik-port/ohos-sysroot-arm32/`.

What's missing: M10-arm32 Makefile target (~1 person-day), `OhosRenderServiceBackend.cpp` (~600 LOC), `OhosBackend.cpp` for audio (~350 LOC), mock APK validation (~1 week). Cheapest available first move: **build the mock APK** and let it tell us whether G or H is the real path.

---

## Self-audit checklist (run before implementation kickoff)

- [ ] Mock APK PASS on rk3568 dev board (panel pixel cycles R/G/B for 60 frames; composer_host PID stable).
- [ ] CR61.1 amendment drafted memorializing transitive-libipc-via-wrapper as the V2-OHOS position.
- [ ] Codex review of this CR-CC.
- [ ] M10-arm32 build green: `out/arm32/libbinder.so` + `servicemanager` + HelloBinder smoke PASS.
- [ ] No `dlopen("libipc.dylib.so")` anywhere in dalvikvm or daemons.
- [ ] No `setenforce 0` in any script.
- [ ] aarch64 Phase-1 builds still PASS (no parallel regression).

---

## Cross-references

- `docs/engine/CR-BB-OHOS-RENDER-STRATEGY.md` — sub-view path; deferred per `project_v2_ohos_direction.md`.
- `docs/engine/CR41_PHASE2_OHOS_ROADMAP.md` — original M9-M13 plan; this CR refines M12's "XComponentBackend" to "OhosRenderServiceBackend" (G/H instead of C).
- `docs/engine/CR60_BITNESS_PIVOT_DECISION.md` — bitness prerequisite.
- `docs/engine/CR61_BINDER_STRATEGY_POST_CR60.md` — binder strategy; this CR proposes CR61.1 amendment for transitive-wrapper rule.
- `docs/engine/M5_AUDIO_DAEMON_PLAN.md` §4.3 — pre-scaffolded OhosBackend audio backend.
- `docs/engine/M6_SURFACE_DAEMON_PLAN.md` §4.3 — pre-scaffolded XComponent surface backend (this CR refactors that to RSSurfaceNode-direct).
- `docs/engine/M6_STEP6_REPORT.md` — A11→A15 AIDL drift in IGraphicBufferProducer; same Bn surface stays in M12.
- `/tmp/cr-bb-phase1-render-binder-analysis.md` — Phase-1 render binder trace; per-frame budget = 2 binder calls in either path.
- Memory: `project_v2_ohos_direction.md`, `feedback_additive_shim_vs_architectural_pivot.md`, `feedback_macro_shim_contract.md`, `feedback_subtraction_not_addition.md`, `feedback_no_per_app_hacks.md`.
- Source citations:
  - `foundation/window/window_manager/wm/src/window_session_impl.cpp:151,155-173`
  - `foundation/window/window_manager/wm/src/window_scene_session_impl.cpp:131-191,320-369`
  - `foundation/window/window_manager/window_scene/session_manager/src/scene_session_manager.cpp:1617-1720`
  - `foundation/window/window_manager/window_scene/common/src/session_permission.cpp:88-101,163-173`
  - `foundation/window/window_manager/window_scene/session_manager/src/session_manager.cpp:121-167`
  - `foundation/graphic/graphic_2d/rosen/modules/render_service_base/src/platform/ohos/rs_render_service_connect_hub.cpp:90-147`
  - `foundation/graphic/graphic_2d/rosen/modules/render_service_base/src/platform/ohos/rs_render_service_connection_proxy.cpp:182-216`
  - `foundation/graphic/graphic_2d/rosen/modules/render_service_client/core/ui/rs_surface_node.cpp:47-132`
  - `foundation/graphic/graphic_2d/rosen/modules/render_service_client/core/ui/rs_display_node.cpp:25-37`
  - `foundation/graphic/graphic_2d/rosen/modules/render_service_base/include/platform/ohos/rs_irender_service_connection_ipc_interface_code_access_verifier.h:48-58`
  - `foundation/graphic/graphic_surface/surface/src/native_window.cpp:33-55`
  - `foundation/graphic/graphic_surface/surface/src/surface_utils.cpp:36`
  - `foundation/communication/ipc/ipc/native/src/mock/source/binder_connector.cpp:59`
