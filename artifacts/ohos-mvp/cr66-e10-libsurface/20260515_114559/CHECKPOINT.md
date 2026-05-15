# CR66-E10 libsurface coexistence — CHECKPOINT (do not advance)

**Agent:** 24
**Date:** 2026-05-15
**Verdict:** **STOP — architectural premise rebutted.** libsurface.z.so
cannot mint a render_service-attached `OHNativeWindow*` without an IPC
call that CR61 explicitly forbids. Spike took 1 dev-half, well under the
2-day cap.

## Hypothesis tested

> A dlopen of libsurface.z.so + a call to a producer-side factory yields
> an OHNativeWindow* wired into render_service in-process, transitively
> using libipc through libsurface's internals (which CR61 permits).

## Result

**FALSIFIED at the source-code level**, confirmed empirically at
runtime. libsurface exposes exactly three ways to obtain a producer:

1. `OHOS::Surface::CreateSurfaceAsProducer(sptr<IBufferProducer>&)` —
   takes an `IBufferProducer` (binder proxy). The proxy can only be
   obtained from a consumer via IPC (the consumer side lives in
   render_service for app-style surfaces). We cannot synthesize this
   pointer.
2. `OH_NativeWindow_CreateNativeWindowFromSurfaceId(uint64_t,
   OHNativeWindow**)` — per `SurfaceUtils::GetSurface()` at
   `/home/dspfac/openharmony/foundation/graphic/graphic_2d/frameworks/surface/src/surface_utils.cpp:36-44`,
   this is a pure per-process hash-table lookup on `surfaceCache_`.
   **No IPC fallback** to render_service. The cache only contains
   surfaces that THIS process previously created (via
   `CreateSurfaceAsProducer` — which needs a producer from IPC, see #1)
   or registered via `SurfaceUtils::Add()` (still needs the source
   surface to exist).
3. `OHOS::Surface::CreateSurfaceAsConsumer(string)` — fully in-process,
   no IPC needed. Stands up `BufferQueue + BufferQueueProducer +
   BufferQueueConsumer` in OUR process. Pixels never reach
   render_service because we own the consumer side. Useful as a
   producer-end smoke; useless for compositor coexistence.

The "transitively through libsurface" loophole **does not exist** for
producer-side acquisition: every code path that returns a render_service-
attached producer takes a pre-resolved `sptr<IBufferProducer>` as input;
none of them go fetch one from samgr internally.

## Standalone smoke result

Built `dalvik-port/compat/surface_inproc_smoke.c` as arm32 dynamic-PIE
ELF (`build-ohos-arm32-dynamic/surface_inproc_smoke`, 39508 bytes,
identical link flags to `ohos_dlopen_smoke` and the CR60 dalvikvm).
Pushed to `/data/local/tmp/surface_inproc_smoke`, ran.

Verbatim output (also in `smoke.stdout`):

```
[surface-smoke] CR66-E10 standalone smoke starting
[surface-smoke] P1 dlopen /system/lib/chipset-sdk-sp/libsurface.z.so
[surface-smoke] P1 PASS: handle=0x58b2e131
[surface-smoke] P2 dlsym CreateNativeWindowFromSurfaceId = 0xf664c7fd
[surface-smoke] P2 CreateFromSurfaceId(id=0) rc=40001000 win=0
[surface-smoke] P2 CreateFromSurfaceId(id=0x12345...) rc=40001000 win=0
[surface-smoke] P3 dlsym CreateSurfaceAsConsumer = 0xf664710d
[surface-smoke] DONE: composer_host alive throughout
[surface-smoke] PASS-INFRA (load+resolve); SurfaceId path requires
                IPC-registered uniqueId in surfaceCache_
```

### Symbol probes

| Probe | Symbol | Return / outcome |
|-------|--------|------------------|
| P1 dlopen | `/system/lib/chipset-sdk-sp/libsurface.z.so` | handle `0x58b2e131` (PASS — transitive `NEEDED libipc_single.z.so` resolved cleanly without direct dlopen, no `setenforce 0`) |
| P2 dlsym | `OH_NativeWindow_CreateNativeWindowFromSurfaceId` | `0xf664c7fd` (resolved) |
| P2 call  | `CreateFromSurfaceId(id=0)` | `rc=40001000` (decimal) = `GSERROR_INVALID_ARGUMENTS` / `SURFACE_ERROR_INVALID_PARAM` per `interfaces/inner_api/common/graphic_common_c.h:27`, `win=NULL` |
| P2 call  | `CreateFromSurfaceId(id=0x1234567890ABCDEF)` | `rc=40001000`, `win=NULL` — identical, confirms it's a `SurfaceUtils::GetSurface()` cache-miss path |
| P3 dlsym | `_ZN4OHOS7Surface23CreateSurfaceAsConsumerENSt3__h12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEE` (CreateSurfaceAsConsumer) | `0xf664710d` (resolved, not invoked) |

### composer_host PID — pre/post (PRIMARY SUCCESS GATE)

```
PRE  : composer_host PID 23573 / render_service PID 5955
POST : composer_host PID 23573 / render_service PID 5955
```

**Both unchanged.** No SIGKILL, no respawn, no DRM master takeover. The
"do no harm to the compositor" half of the coexistence success criterion
**did succeed**. What did not succeed is the other half — actually
producing a surface render_service knows about.

## Why the architectural premise was wrong

CR66 brief speculated libsurface "internally handles all the libipc +
render_service IPC" similar to how Android's `libgui` internally speaks
binder. Source review confirms this is partially true (`libsurface.z.so`
links `libipc_single.z.so` and uses MessageParcel / SendRequest
internally), but only on the **consumer-driven** path: a consumer in
render_service calls `BufferQueueProducer::CreateBufferClientProducer`
and hands the resulting binder proxy to the app via samgr. The producer
side cannot bootstrap itself — there is no `Surface::CreateProducerByName`
or `Surface::ConnectToWindowManager` factory.

This matches Android's libgui: an app calls
`SurfaceComposerClient::createSurface()` → binder → SurfaceFlinger →
returns a `Surface` (== `IGraphicBufferProducer` wrapper). Without
SurfaceComposerClient calling SurfaceFlinger via binder, you don't get a
SurfaceFlinger-attached Surface. The same is true here with
SurfaceComposerClient → render_service. We don't ship that client
because CR61 prohibits it.

## What's needed to advance (not in CR66 scope)

Pure-Java surface-acquisition would require ONE of:

a) **CR67 (Compose) path** wins on its own — irrelevant whether E10
   works. This is the recommended branch.
b) **Sign a HAP** so render_service trusts us as a producer client via
   the OHOS samgr universe. Requires a per-app branch in policy +
   re-architecting Westlake to live as a HAP. Explicitly out of CR66
   scope (and arguably violates the macro-shim contract).
c) **Bring up our own AOSP libbinder + SurfaceFlinger ourselves** on
   OHOS — the CR41 Phase-2 "M11/M12 own-compositor" plan. Multi-week
   effort.
d) **Bypass (E9b) path** that we just proved works — kill composer_host,
   take DRM master, scan out directly. Defeats coexistence by design.

## Recommendation

1. **Mark E10 (libsurface coexistence) DEAD** in CR60-followup tracker.
   The producer-side handle cannot be obtained without IPC to
   render_service, and CR61 explicitly rules out that IPC.
2. **Promote CR67 (Compose)** as the next-best lever — it changes WHAT
   we render (single-process pipeline that doesn't need a separate
   compositor) rather than HOW we present (E10's target).
3. **Keep E9b (DRM bypass) as the production path** for arm32 pixel
   delivery until a real Westlake-owned compositor lands. Document its
   "composer_host gets SIGKILL" cost openly in the runtime brief.
4. **No code wired into dalvikvm.** The E10 plan called for a
   `surface_inproc_bridge.c` JNI bridge + `SurfaceInprocClient.java`;
   building those would be sunk cost given the empirical result above.
   The standalone smoke binary + this checkpoint are the deliverable.

## Self-audit gate

- [x] No direct dlopen of `libipc.dylib.so` / `libsamgr.dylib.so`. The
      smoke dlopens only `/system/lib/chipset-sdk-sp/libsurface.z.so`;
      libipc_single is pulled in via `NEEDED` by the loader.
- [x] No `Unsafe.allocateInstance`, `setAccessible`, or per-app
      branches. The smoke is pure C with C-style dlsym; no Java added.
- [x] composer_host PID unchanged through the test: 23573 → 23573.
      render_service PID unchanged: 5955 → 5955. (PRIMARY GATE PASS.)
- [x] Prior regression preserved: no production code modified. Sanity
      run of `scripts/run-ohos-test.sh --arch arm32 hello` PASS (marker
      found; `artifacts/ohos-mvp/mvp0-hello/20260515_114544/`).
- [x] Honest assessment: surface acquisition **FAILED** by design (no
      IPC path available); pixel-on-panel **NOT ATTEMPTED** (gate
      failed at acquisition step).
- [x] Exact symbol failure documented:
      `OH_NativeWindow_CreateNativeWindowFromSurfaceId` returns
      `GSERROR_INVALID_ARGUMENTS (40001000)` because
      `SurfaceUtils::surfaceCache_` is per-process and empty in our
      dalvikvm at startup.

## Files in this checkpoint

- `CHECKPOINT.md` — this file
- `surface_inproc_smoke.c` — source (also at
  `dalvik-port/compat/surface_inproc_smoke.c`)
- `surface_inproc_smoke` — 32-bit arm dynamic-PIE binary (39508 bytes)
- `smoke.stdout` — captured device-side run output
- `composer_pids.txt` — post-run `ps` snapshot
- `libsurface_symbols.txt` — full `nm -D | c++filt` of libsurface
- `libsurface_needed.txt` — `readelf -d` NEEDED list

## Bitness discipline

The new smoke .c uses `uintptr_t` for pointer-as-address printing and
`uint64_t` for the SurfaceId argument. `intptr_t`/`size_t` are unused
because the smoke never indexes arrays or computes offsets. Compiled
with `--target=arm-linux-ohos -march=armv7-a -mfloat-abi=softfp
-mfpu=neon -fPIE -pie` — matches `ohos-arm32-dynamic` TARGET in
`dalvik-port/Makefile`.

## Cross-references

- `docs/engine/CR61_BINDER_STRATEGY_POST_CR60.md` — what's forbidden
- `docs/engine/CR60_BITNESS_PIVOT_DECISION.md` — arm32 substrate
- `dalvik-port/compat/ohos_dlopen_smoke.c` — smoke pattern template
- `dalvik-port/compat/drm_inproc_bridge.c` — E9b bypass (still works,
  still kills composer_host; left in place as the production path)
- `/home/dspfac/openharmony/foundation/graphic/graphic_2d/frameworks/surface/src/surface_utils.cpp` — `GetSurface` is per-process hash-table only
- `/home/dspfac/openharmony/foundation/graphic/graphic_2d/frameworks/surface/src/consumer_surface.cpp:72-84` — `ConsumerSurface::Init` builds in-process queue (no IPC)
- `/home/dspfac/openharmony/foundation/graphic/graphic_2d/frameworks/surface/src/buffer_client_producer.cpp:65` — `BufferClientProducer` constructed from `sptr<IRemoteObject>` (binder proxy, IPC required)
- `/home/dspfac/openharmony/foundation/graphic/graphic_2d/interfaces/inner_api/common/graphic_common_c.h:27` — `GSERROR_INVALID_ARGUMENTS = 40001000`
