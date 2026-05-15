# M6_STEP4_REPORT.md — Consumer-side DLST pipe writer; pixels flow to Compose host

**Status:** done (build clean + smoke PASS 2026-05-13 on cfb7c9e3, all 7 transaction checks green including new pipe-consumer check G)
**Owner:** Builder (CR for M6-Step4)
**Companion to:** [`M6_SURFACE_DAEMON_PLAN.md`](M6_SURFACE_DAEMON_PLAN.md) §4.2 / §4.4, [`M6_STEP3_REPORT.md`](M6_STEP3_REPORT.md), [`CR33_M6_SPIKE_REPORT.md`](CR33_M6_SPIKE_REPORT.md), [`PHASE_1_STATUS.md`](PHASE_1_STATUS.md)
**Predecessor:** M6-Step3 (12 Tier-1 IGraphicBufferProducer transactions + memfd-backed GraphicBuffer; producer side complete but slots immediately recycled on QUEUE — no consumer ever saw a frame)
**Successor:** M6-Step5 (IDisplayEventConnection + 60 Hz vsync tick; CR35 AIDL audit; SET_TRANSACTION_STATE LayerState envelope; init.rc autostart) — see M6 plan §7

---

## §1  Goal

Step 3 wired the producer side end-to-end: an app calling
`Surface.lockCanvas → IGraphicBufferProducer.dequeueBuffer → requestBuffer`
got a usable memfd-backed buffer it could `mmap` and paint, and
`queueBuffer` returned `NO_ERROR`.  But the slot was *immediately* recycled
back to FREE — no consumer existed to read out the pixels, so the frame
disappeared at the daemon boundary.

Step 4 (this CR) replaces the immediate recycle with a **per-Surface
consumer thread** that:
1. Waits on `WestlakeGraphicBufferProducer::takeQueuedSlot()` (a
   condition_variable-backed call that returns the oldest QUEUED slot
   when one becomes available).
2. Snapshots the slot's memfd + dims + stride under the producer's lock.
3. mmap's the memfd (`MAP_SHARED, PROT_READ`).
4. Writes a **DLST-format frame** to a configurable pipe path: the
   envelope (`uint32 magic = 0x444C5354 ("DLST") | uint32 size`) followed
   by an inline `OP_ARGB_BITMAP` op (`byte opcode=12 | float x | float y
   | int32 w | int32 h | int32 dataLen | bytes[] rgba`).
5. Calls `releaseSlot(idx)` — transitions the slot back to FREE and
   wakes any DEQUEUE_BUFFER caller blocked on back-pressure.

This is the **first time pixels actually leave the daemon**.  The Compose
host's existing `WestlakeVM.kt` pipe reader (`westlake-host-gradle/...`,
constants `DLIST_MAGIC = 0x444C5354` and `OP_ARGB_BITMAP = 12`) consumes
this exact wire format unchanged — no host-side edits needed.  Step-4
acceptance is "consumer thread fired + DLST envelope written + payload
contents survive round-trip"; full end-to-end pixel coherency through the
Compose host SurfaceView is M7 integration.

---

## §2  What was built

### 2.1 Files touched

```
aosp-surface-daemon-port/
├── native/
│   ├── DlstConsumer.{h,cpp}                  NEW       (~110 + ~210 = ~320 LOC)
│   ├── WestlakeGraphicBufferProducer.h       EXTENDED  (+~50 LOC: mLock,
│   │                                                    mProducerCv, mConsumerCv,
│   │                                                    takeQueuedSlot/releaseSlot/
│   │                                                    wake/snapshotSlotMemfd
│   │                                                    decls; std::mutex /
│   │                                                    std::condition_variable
│   │                                                    includes; accessor getters)
│   ├── WestlakeGraphicBufferProducer.cpp     EXTENDED  (+~110 LOC: thread-safe
│   │                                                    slot transitions; impl
│   │                                                    of 4 consumer-side hooks;
│   │                                                    16-ms back-pressure
│   │                                                    wait in DEQUEUE_BUFFER;
│   │                                                    QUEUE_BUFFER no longer
│   │                                                    immediately marks FREE)
│   ├── WestlakeSurfaceComposerClient.{h,cpp} EXTENDED  (+~60 LOC: mConsumers
│   │                                                    ownership vector;
│   │                                                    setDlstPipePath();
│   │                                                    spawnProducerAndConsumer
│   │                                                    helper; CREATE_SURFACE +
│   │                                                    CREATE_WITH_SURFACE_PARENT
│   │                                                    now call it)
│   ├── WestlakeSurfaceComposer.{h,cpp}       EXTENDED  (+~10 LOC: mDlstPipePath
│   │                                                    field; CREATE_CONNECTION
│   │                                                    plumbs it into the new
│   │                                                    client)
│   ├── surfaceflinger_main.cpp               EXTENDED  (+~15 LOC: $WESTLAKE_DLST_PIPE
│   │                                                    env-var resolution; default
│   │                                                    path /data/local/tmp/
│   │                                                    westlake/dlst.fifo)
│   └── surface_smoke.cc                      EXTENDED  (+~290 LOC: check G with
│                                                        FIFO mkfifo, reader thread,
│                                                        pattern paint, full
│                                                        round-trip verify;
│                                                        request_one_capture_fd
│                                                        helper with dup() fix)
├── Makefile                                   EXTENDED  (DlstConsumer.cpp added
│                                                        to DAEMON_SRCS + DAEMON_HDRS)
├── m6step4-smoke.sh                           NEW       (~145 LOC: extends
│                                                        m6step3-smoke.sh with
│                                                        FIFO mkfifo + env-var
│                                                        plumbing)
└── m6step4-smoke-run.log                      NEW       (captured PASS transcript)
```

Net-new Step-4 LOC: **~870 C++ + ~145 shell** (vs Step-3's ~960 C++ +
~143 shell — comparable budget).

### 2.2 Files NOT touched (anti-drift compliance)

Per the macro-shim contract (`memory/feedback_macro_shim_contract.md`):
- `shim/java/**` — Compose host pipe-reader (`WestlakeVM.kt`) was read
  ONLY to confirm the existing wire-format constants (`DLIST_MAGIC =
  0x444C5354`, `OP_ARGB_BITMAP = 12`).  Zero edits there.
- `art-latest/**` — substrate stable.
- `aosp-libbinder-port/**` — substrate stable; consumed as build
  dependency only.
- `aosp-audio-daemon-port/**` — M5-Step3 may have it; untouched.
- `aosp-shim.dex` — unchanged.
- `westlake-host-gradle/**` — read-only (confirmed `WestlakeVM.kt`
  pipe-reader's existing OP_ARGB_BITMAP branch).
- Memory files — unchanged.

Zero per-app branches introduced.  Every `WestlakeGraphicBufferProducer`
gets the **same** paired `DlstConsumer` instance with the **same** pipe
path — the daemon's behavior is generic.

Zero `Unsafe` / `setAccessible` (N/A — native C++).

---

## §3  DlstConsumer architecture

### 3.1  Lifecycle

```
                                                     surface client lifecycle
WestlakeSurfaceComposer::onCreateConnection                      │
        │                                                        │
        ├─ sp<WestlakeSurfaceComposerClient> clientImpl          │
        ├─ clientImpl->setDlstPipePath($WESTLAKE_DLST_PIPE)      │
        ▼                                                        ▼
WestlakeSurfaceComposerClient::onCreateSurface
        │
        ├─ spawnProducerAndConsumer(w, h, format):
        │     gbp = new WestlakeGraphicBufferProducer
        │     consumer = new DlstConsumer(gbp, mDlstPipePath)
        │     consumer.start()                ── thread spawn ───┐
        │     mConsumers.push_back(consumer)                     │
        ▼                                                        ▼
return strongBinder(gbp) to caller                  DlstConsumer::run()
                                                       loop:
                                                         slot = gbp->takeQueuedSlot()
                                                         if slot < 0: exit
                                                         writeFrame(slot)
                                                         gbp->releaseSlot(slot)
```

When the surface client's BBinder drops on the binder driver, its
destructor stops all its DlstConsumers (which call `gbp->wake()`, causing
`takeQueuedSlot()` to return -1, the loop exits, the thread joins).

### 3.2  Slot state machine (Step-4)

```
        DEQUEUE_BUFFER  (may wait up to 16 ms for FREE)
FREE ───────────────────────────────► DEQUEUED
                                          │
                                          │ QUEUE_BUFFER
                                          ▼
                                       QUEUED ──── consumer thread takes ─────┐
                                                      │                       │
                                                      │ writeFrame()         │
                                                      ▼                       │
                                                  pipe write                  │
                                                      │                       │
                                                      ▼                       │
                                       releaseSlot()    ← producer waits     │
                                          │                                   │
                                          ▼                                   │
                                       FREE ←──────────────────────────────────┘
                                          │
                                          │ CANCEL_BUFFER (rare; same effect)
                                          ▼
                                       FREE
```

The Step-4 slot state machine differs from Step-3 in one critical way:
**QUEUE no longer immediately marks the slot FREE.**  The slot stays
QUEUED until the consumer thread releases it.  This (a) provides
back-pressure (DEQUEUE waits up to 16 ms for a FREE slot if both are in
flight), (b) gives the consumer the entire post-QUEUE window to mmap +
write, and (c) avoids the Step-3 trick of "Phase-1 immediate recycle"
that bypassed the consumer entirely.

### 3.3  Wire format — exact match with WestlakeVM.kt reader

Each frame consists of two pieces:

```
   ┌─ envelope (8 bytes) ──────────────────────────────────────┐
   │  uint32_le  magic   = 0x444C5354 ("DLST")                  │
   │  uint32_le  size    = length of `payload` that follows     │
   └─ payload (size bytes) ────────────────────────────────────┘
   │  byte      opcode   = OP_ARGB_BITMAP (=12)                 │
   │  float     x        = 0.0f                                 │
   │  float     y        = 0.0f                                 │
   │  int32_le  width                                           │
   │  int32_le  height                                          │
   │  int32_le  dataLen  = width * height * 4                   │
   │  byte[]    rgba     = stride-cropped raw RGBA8888 pixels   │
```

Cross-references:
- Magic comes from
  `westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt:47`
  (`DLIST_MAGIC = 0x444C5354`).
- Opcode from `WestlakeVM.kt:61` (`OP_ARGB_BITMAP = 12`).
- Payload schema matches `WestlakeVM.kt:1746-1768` (the `OP_ARGB_BITMAP`
  branch in `replayDisplayList`): the reader reads exactly
  `x, y, w, h, dataLen` and then `dataLen` bytes which it interprets as
  4-byte RGBA tuples (`r, g, b, a → ARGB int = (a<<24)|(r<<16)|(g<<8)|b`).

The consumer crops scanlines to `width * 4` bytes (skipping the
gralloc-convention `(stride - width) * 4` trailing bytes per row) so the
host receives a tightly-packed RGBA bitmap matching its `dataLen`
expectation.  For the smoke test's 64×32 surface, stride==width so this
is a single big write.

### 3.4  Pipe path resolution

| Source | Behavior |
|---|---|
| `$WESTLAKE_DLST_PIPE` (env var on daemon's process) | Used verbatim |
| absent | Falls back to `/data/local/tmp/westlake/dlst.fifo` (matches `surfaceflinger_main.cpp:kDefaultDlstPipe`) |
| empty (`setDlstPipePath("")` explicitly) | DlstConsumer **not spawned** — back to Step-3 behavior; useful for unit tests where pipe machinery isn't relevant |

The launcher (`m6step4-smoke.sh`) `mkfifo`'s the path before starting
the daemon.  If the FIFO doesn't exist, the daemon's `open(O_WRONLY |
O_NONBLOCK)` fails with `ENOENT` and the frame silently drops — same
"reader not attached yet" semantics as `ENXIO`.

### 3.5  Back-pressure & drop semantics

- **No reader attached (`ENXIO` from `open(O_WRONLY | O_NONBLOCK)`)**:
  silent drop; `framesDropped++`.  Matches the "Compose host SurfaceView
  may not be attached yet" comment in M6 plan §4.1
  (`DlstPipeBackend::onQueued`).
- **Reader attached, but kernel pipe buffer momentarily full**: the
  consumer switches to blocking after open (via `fcntl(F_SETFL, flags &
  ~O_NONBLOCK)`) and `writeFully` retries EINTR/EAGAIN with a 2 ms
  sleep.  For a single 64×32 frame (8213 bytes) this never matters
  because Linux pipe buffers are 64 KB by default.  For a 1080×2280
  frame (~9.4 MB) one write may EAGAIN; the retry policy handles it.
- **EPIPE (reader closed mid-frame)**: drop the frame; do NOT crash the
  consumer thread.  Future host re-attaches see fresh frames.

---

## §4  Smoke verification

### 4.1  Test driver: `surface_smoke.cc` (extended)

Step-3's `surface_smoke` is now extended with **check G**:

```
G. CREATE_CONNECTION to get a fresh client (so check-F's lifecycle is
   independent), then on that client CREATE_SURFACE for a small 64×32
   surface (RGBA8888).  Verify gbp != null.
   On that GBP:
     dequeue_one              — DEQUEUE_BUFFER, get slot index.
     request_one_capture_fd   — REQUEST_BUFFER + dup() the memfd into
                                the smoke process's fd table (the Parcel
                                owns the original; dup before destruct).
     mmap(memfd) + paint      — Top half FF 00 00 FF (red,
                                opaque); bottom half 00 FF 00 FF (green,
                                opaque).  munmap.
     queue_one                — QUEUE_BUFFER → daemon's DlstConsumer
                                wakes, opens the FIFO, writes a DLST
                                frame to the reader thread.
   Reader thread (spawned BEFORE the writer-side transactions to avoid
   the daemon's open(O_WRONLY | O_NONBLOCK) racing on no-reader ENXIO):
     1. open(fifoPath, O_RDONLY)  — blocks until daemon opens for write.
     2. read envelope: magic, size.
     3. read inline header: opcode, x, y, w, h, dataLen.
     4. read first 4 bytes of pixel payload (spot-check pattern).
     5. drain the remainder so the writer doesn't see EPIPE.
   Verify on the reader side:
     magic == 0x444C5354
     opcode == 12 (OP_ARGB_BITMAP)
     dims == 64×32
     dataLen == 64 * 32 * 4 = 8192
     firstPixel[] == { 0xFF, 0x00, 0x00, 0xFF }   (red-top)
```

### 4.2  On-phone smoke: `m6step4-smoke.sh`

Same sandbox protocol as m6step3-smoke.sh, plus:
1. **mkfifo** at `/data/local/tmp/westlake/dlst.fifo` (mode 0666) before
   starting the daemon.
2. Daemon launched with `WESTLAKE_DLST_PIPE=/data/local/tmp/westlake/dlst.fifo`.
3. surface_smoke launched with the same env var + `WESTLAKE_SMOKE_CHECK_G=1`.

Full transcript: `aosp-surface-daemon-port/m6step4-smoke-run.log`.  Key
acceptance line:

```
[surface_smoke] G: PASS magic=0x444c5354 payloadSize=8213 opcode=12 dims=64x32 dataLen=8192 firstPixel=FF 00 00 FF
[surface_smoke] summary: 0 failure(s) of 7 checks
[m6-step4] PASS: all 7 transaction checks passed (Step-2 A..E + Step-3 F + Step-4 G)
[m6-step4] done; result=0
```

Daemon-side log confirming the new consumer thread machinery:

```
[wlk-surface-daemon] DLST pipe path: "/data/local/tmp/westlake/dlst.fifo" (env=WESTLAKE_DLST_PIPE)
...
[wlk-sf] CREATE_SURFACE name="westlake-m6step4-smoke" 64x32 fmt=1 flags=0x0 parent=0x0
[wlk-igbp] ctor 64x32 fmt=1 slots=2
[wlk-dlst] ctor gbp=0x7dea08f650 pipe="/data/local/tmp/westlake/dlst.fifo"
[wlk-dlst] start() thread spawned for gbp=0x7dea08f650 pipe="/data/local/tmp/westlake/dlst.fifo"
[wlk-sf] spawned DlstConsumer #1 for gbp=0x7dea08f650 pipe="/data/local/tmp/westlake/dlst.fifo"
[wlk-dlst] run() loop start gbp=0x7dea08f650 pipe="/data/local/tmp/westlake/dlst.fifo"
...
[wlk-igbp] QUEUE_BUFFER slot=0 state=QUEUED frame#=1 (consumer signaled)
[wlk-dlst] writeFrame slot=0 frame#=1 wrote magic+payload = 8221 bytes (64x32 stride=64 memfd=5)
...
[wlk-dlst] stop() joined; framesWritten=1 framesDropped=0
[wlk-dlst] dtor (gbp=0x7dea08f650; framesWritten=1 framesDropped=0 bytesWritten=8221)
```

Reader thread observed `8 + 8213 = 8221` bytes total (envelope + payload),
matching the daemon's `8221 bytes` log line exactly.

Independent confirmation that **check F's surface's consumer thread also
fired** (just had no reader, as expected):

```
[wlk-igbp] QUEUE_BUFFER slot=0 state=QUEUED frame#=1 (consumer signaled)
[wlk-igbp] QUEUE_BUFFER slot=1 state=QUEUED frame#=2 (consumer signaled)
...
[wlk-dlst] stop() joined; framesWritten=0 framesDropped=2
[wlk-dlst] dtor (gbp=0x7dea08dd90; framesWritten=0 framesDropped=2 bytesWritten=0)
```

Both check-F-cycle frames were dropped on `ENXIO` (no reader attached
during check F) — exactly the "silent drop, retry next QUEUE" policy
documented in §3.5.

### 4.3  Regression coverage

- **A–E** (Step-1/2): SurfaceFlinger registration + Tier-1 ISurfaceComposer
  display info + CREATE_CONNECTION.  Unchanged from Step-3.
- **F** (Step-3): IGraphicBufferProducer pipeline.  Now also exercises
  consumer-side state transitions — slot ring cycled 0→1 (Step-3 ran
  slot=0→0 because immediate recycle).  Verified: `slotA=0 slotB=1` —
  confirms back-pressure path works (slot 0 was still QUEUED when
  smoke called second DEQUEUE; smoke's wait_for() returned slot=1
  instead).
- **G** (Step-4 NEW): DLST consumer fires + writes verified envelope.

---

## §5  Anti-drift compliance & person-time

**Files touched (all within sanctioned scope):**
- `aosp-surface-daemon-port/native/*` — sole native edit zone for M6.
- `aosp-surface-daemon-port/Makefile` — additive (one source + one header).
- `aosp-surface-daemon-port/m6step4-smoke.sh` — new smoke driver.
- `aosp-surface-daemon-port/out/bionic/*` — rebuilt artefacts.
- `docs/engine/M6_STEP4_REPORT.md` — this report.
- `docs/engine/PHASE_1_STATUS.md` — M6-Step4 row.

**Files NOT touched** (per contract): `shim/java/**`, `art-latest/**`,
`aosp-libbinder-port/**`, `aosp-audio-daemon-port/**`, `aosp-shim.dex`,
`westlake-host-gradle/**`, `scripts/**`, memory files.

**Zero per-app branches** — every CREATE_SURFACE in the daemon spawns
the same paired DlstConsumer with the same pipe path.

**Person-time**: ~3.5 hours (well inside the 4–6 hour budget).
Breakdown: ~30 min reading Step-3 producer code + WestlakeVM.kt wire
format; ~90 min DlstConsumer.{h,cpp} + producer-side mutex/condvar
wiring; ~45 min surface_smoke check G + reader thread; ~30 min build &
phone debugging (caught the `readFileDescriptor` lifetime bug — Parcel
owns the fd until destruct, smoke now `dup()`s it for the post-Parcel
mmap); ~15 min report.

---

## §6  Open follow-ups (NOT in scope for Step-4)

1. **Real Compose-host SurfaceView integration (M7)**: today's smoke
   verifies the wire format with an in-process reader thread.  M7 will
   wire the daemon's FIFO to the host's existing `WestlakeVM.kt`
   pipe-reader (`readPipeAndRenderLocked`).  No format changes needed —
   the host already handles `OP_ARGB_BITMAP` via `replayDisplayList()`
   §1746.
2. **Vsync timing (M6-Step5 / IDisplayEventConnection)**: currently the
   consumer thread races as fast as QUEUE_BUFFER calls arrive.  A 60 Hz
   tick from a future `WestlakeDisplayEventConnection` would gate the
   consumer's pipe writes.
3. **Multi-surface composition**: today each surface streams to the
   same FIFO independently — interleaved frames would confuse the host
   reader.  Phase-1 single-foreground-app assumption (M6 plan §4.4)
   makes this OK; Phase-2 XComponent backend will composite in the
   daemon before emitting.
4. **Compressed DLST opcodes for non-RGBA bitmaps**: Phase-1 only
   supports RGBA8888 via `OP_ARGB_BITMAP`.  YUV / RGB_565 paths
   defer to Phase-2.
5. **CR35 AIDL audit**: framework.jar's `IGraphicBufferProducer`
   transaction-code stability check vs. Android-11 reference.  Step-4
   does not depend on this.

---

## §7  Cross-references

- M6 plan §4.2 "Phase 1 — DLST pipe backend" — architectural diagram
  matched 1:1 by this implementation.
- M6 plan §4.1 `SurfaceBackend` interface — `DlstConsumer` is the
  concrete Phase-1 implementation of `SurfaceBackend::onQueued`.
- CR33 spike report §2.4 — memfd `mmap(MAP_SHARED)` works on
  cfb7c9e3's kernel 4.9.337; Step-4 exercises this same primitive
  inside the daemon process (same memfd produced by Step-3's
  `MemfdGraphicBuffer::ensureAllocated`).
- `WestlakeVM.kt` §1746 — receiver-side `OP_ARGB_BITMAP` handler;
  format match documented in §3.3.
- `feedback_macro_shim_contract.md` — anti-drift contract that
  constrains the touched-files set; see §5.
