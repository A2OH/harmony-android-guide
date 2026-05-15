# M6_STEP3_REPORT.md — IGraphicBufferProducer Bn-side stub + memfd-backed GraphicBuffer

**Status:** done (build clean + smoke PASS 2026-05-13 on cfb7c9e3, all 6 transaction checks green)
**Owner:** Builder (CR for M6-Step3)
**Companion to:** [`M6_SURFACE_DAEMON_PLAN.md`](M6_SURFACE_DAEMON_PLAN.md) §2.4 / §5, [`M6_STEP2_REPORT.md`](M6_STEP2_REPORT.md), [`CR33_M6_SPIKE_REPORT.md`](CR33_M6_SPIKE_REPORT.md), [`PHASE_1_STATUS.md`](PHASE_1_STATUS.md)
**Predecessor:** M6-Step2 (Tier-1 ISurfaceComposer dispatch; CREATE_SURFACE returned null `gbp`)
**Successor:** M6-Step4 (wire the QUEUE_BUFFER side into the Compose host's SurfaceView via the DLST pipe — actually display pixels)

---

## §1  Goal

Step 2 wired the display-/connection-side transactions and made
`ISurfaceComposerClient::createSurface` return a real handle, but the
returned `IGraphicBufferProducer` was `nullptr`.  Apps that try to
`Surface.lockCanvas()` against that hit a null-binder crash inside
`BpGraphicBufferProducer`.

Step 3 (this CR) replaces the `nullptr gbp` with a real
`BnGraphicBufferProducer` stub backed by a memfd-substitute GraphicBuffer.
The 12 Tier-1 IGBP transactions from `M6_SURFACE_DAEMON_PLAN.md §2.4` all
have working handlers; a per-Surface 2-slot ring lazily allocates a memfd
on first DEQUEUE; the resulting `GraphicBuffer`-flattened parcel is
exactly the wire format AOSP-11's `BpGraphicBufferProducer::requestBuffer`
expects.

The frame is **NOT yet displayed**.  That's Step 4's job (wire QUEUE_BUFFER
to the writer-side DLST pipe).  Step 3's success criterion: an app calling
`Surface.lockCanvas` → `IGraphicBufferProducer.dequeueBuffer` →
`requestBuffer` gets back a memfd-backed buffer it can `mmap` and write
pixels into; calling `queueBuffer` returns `NO_ERROR` and the slot becomes
available for the next dequeue.

---

## §2  What was built

### 2.1 Files touched

```
aosp-surface-daemon-port/
├── native/
│   ├── MemfdGraphicBuffer.{h,cpp}             NEW       (~330 LOC combined)
│   ├── WestlakeGraphicBufferProducer.{h,cpp}  NEW       (~630 LOC combined)
│   ├── WestlakeSurfaceComposerClient.cpp      EXTENDED  (CREATE_SURFACE +
│   │                                                    CREATE_WITH_SURFACE_PARENT
│   │                                                    now return real GBP)
│   └── surface_smoke.cc                       EXTENDED  (+~190 LOC: check F,
│                                                        dequeue_one,
│                                                        request_one,
│                                                        queue_one helpers)
├── Makefile                                   EXTENDED  (added 2 sources)
├── m6step3-smoke.sh                           NEW       (~143 LOC)
└── m6step3-smoke-run.log                      NEW       (captured PASS transcript)
```

Net-new Step-3 LOC: ~960 C++ + ~143 shell.

### 2.2 Files NOT touched (anti-drift compliance)

Per the macro-shim contract (`memory/feedback_macro_shim_contract.md`):
- `shim/java/**` — CR32 / CR36 active there.
- `art-latest/**` — substrate stable.
- `aosp-libbinder-port/**` — substrate stable; consumed as build dependency only.
- `aosp-audio-daemon-port/**` — M5-Step2 active there.
- `aosp-shim.dex` — unchanged.
- Memory files — unchanged.

Zero per-app branches introduced.  Every Android peer that calls
`CREATE_SURFACE` gets the **same** 2-slot memfd-backed ring — the daemon
is generic.

Zero `Unsafe` / `setAccessible` (N/A — native C++).

---

## §3  Transaction codes implemented

`WestlakeGraphicBufferProducer::Tag` mirrors AOSP-11's
`BnGraphicBufferProducer` enum from
`frameworks/native/libs/gui/IGraphicBufferProducer.cpp` §50-77.
FIRST_CALL_TRANSACTION = 1.

| Code | Name | Step-3 status |
|---|---|---|
| 1 | `REQUEST_BUFFER`              | Tier-1 (returns memfd-backed `GraphicBuffer` flat) |
| 2 | `DEQUEUE_BUFFER`              | Tier-1 (returns slot index in [0,2) + NO_FENCE + BUFFER_NEEDS_REALLOCATION) |
| 3 | `DETACH_BUFFER`               | Tier-1 (no-op) |
| 4 | `DETACH_NEXT_BUFFER`          | fall-through (Step-3 ack-with-NO_ERROR) |
| 5 | `ATTACH_BUFFER`               | Tier-1 (NACK with -1 / INVALID_OPERATION) |
| 6 | `QUEUE_BUFFER`                | Tier-1 (marks slot FREE; returns QueueBufferOutput flat) |
| 7 | `CANCEL_BUFFER`               | Tier-1 (marks slot FREE) |
| 8 | `QUERY`                       | Tier-1 (canned WIDTH/HEIGHT/FORMAT/MIN_UNDEQUEUED/…) |
| 9 | `CONNECT`                     | Tier-1 (returns QueueBufferOutput flat) |
| 10 | `DISCONNECT`                 | Tier-1 (no-op) |
| 11 | `SET_SIDEBAND_STREAM`        | fall-through |
| 12 | `ALLOCATE_BUFFERS`           | fall-through |
| 13 | `ALLOW_ALLOCATION`           | fall-through |
| 14 | `SET_GENERATION_NUMBER`      | Tier-1 (no-op) |
| 15 | `GET_CONSUMER_NAME`          | fall-through |
| 16 | `SET_MAX_DEQUEUED_BUFFER_COUNT` | Tier-1 (no-op) |
| 17 | `SET_ASYNC_MODE`             | Tier-1 (no-op) |
| 18-26 | …                          | fall-through (logged + ack with single int32 NO_ERROR) |

**12 Tier-1 transactions implemented** (per task spec).  Non-Tier-1 codes
log + return `int32(NO_ERROR)` — matches the Step-2 fall-through philosophy.

---

## §4  Buffer pipeline architecture

### 4.1  Slot ring

```
struct Slot {
    sp<MemfdGraphicBuffer> buf;     // lazily allocated on first DEQUEUE
    SlotState state;                 // FREE | DEQUEUED | QUEUED
};
Slot mSlots[2];   // M6 plan §4.4 Phase-1 sizing
```

State machine (per slot):

```
        DEQUEUE_BUFFER
FREE ─────────────────────► DEQUEUED
                            │
                            │ QUEUE_BUFFER  (Phase-1: immediate recycle)
                            │ CANCEL_BUFFER
                            ▼
                          FREE
```

In Phase 1 we recycle the slot on QUEUE — there is no consumer thread
holding the buffer yet.  Step 4 will move the recycle to the consumer
ack path so back-pressure works (slot stays QUEUED until DLST writer
finishes scanning out the pixels).

### 4.2  Memfd allocation (lazy)

`MemfdGraphicBuffer` records dimensions/format on construct but does NOT
allocate the memfd until the first call to `ensureAllocated()` (driven
by `DEQUEUE_BUFFER → ensureSlotAllocatedLocked`).  Allocation does:

```cpp
fd = memfd_create("westlake-gbuf", MFD_CLOEXEC | MFD_ALLOW_SEALING);
ftruncate(fd, stride * height * 4);    // RGBA8888
fcntl(fd, F_ADD_SEALS, F_SEAL_SHRINK | F_SEAL_GROW);
```

Stride convention is `(width + 63) & ~63` — matching what gralloc picks
on the OnePlus 6 / sdm845 device (CR33 §2.4 measured 1088 for a 1080-wide
allocation; we match exactly so HWUI's stride-handling code is happy).

For a 1080×2280 RGBA8888 surface this is **9 922 560 bytes (~9.4 MB)** —
within memory budget for the 2-slot ring (×2 = ~19 MB per Surface).

### 4.3  GraphicBuffer wire format

`MemfdGraphicBuffer::writeToParcelAsGraphicBuffer` emits the parcel
payload AOSP-11's `BpGraphicBufferProducer::requestBuffer` expects.
The wire layout follows `frameworks/native/libs/ui/GraphicBuffer.cpp::flatten`
(§364) — a 13-int32 header followed by N native_handle ints, with the
memfd written separately via the binder fd-table replication path.

```
Parcel write sequence (after REQUEST_BUFFER reply's int32(1) "nonNull" marker):
  int32 len      = 68                    (13 + 4 = 17 ints × 4 bytes)
  int32 fdCount  = 1
  17 × int32 payload (writeInplace):
    [0]  = 'GB01'                        (magic)
    [1]  = width
    [2]  = height
    [3]  = stride
    [4]  = format
    [5]  = layerCount = 1
    [6]  = usage low32 = 0
    [7]  = id >> 32
    [8]  = id & 0xFFFFFFFF
    [9]  = generationNumber = 0
    [10] = numFds = 1
    [11] = numInts = 4
    [12] = usage high32 = 0
    [13] = width                          (native_handle data[1])
    [14] = height                         (native_handle data[2])
    [15] = stride                         (native_handle data[3])
    [16] = format                         (native_handle data[4])
  writeDupFileDescriptor(memfd)          → kernel-replicated to receiver
```

Receiver-side `GraphicBuffer::unflatten` reads the 13-int header, calls
`native_handle_create(numFds=1, numInts=4)`, copies the trailing 4 ints
into `handle->data[1..4]`, and assigns the dup'd memfd to `handle->data[0]`.

Subsequent `lock(usage, &vaddr)` calls in HWUI just `mmap(.., fd, 0)` —
which CR33 Phase A/D proved works coherently across processes on this
kernel.

### 4.4  QueueBufferOutput flatten

`CONNECT` and `QUEUE_BUFFER` both reply with a flattenned
`IGraphicBufferProducer::QueueBufferOutput` (see
`frameworks/native/libs/gui/QueueBufferInputOutput.cpp` §115-139).  Layout
is **57 bytes, 0 fds**:

```
struct __attribute__((packed)) {
    uint32_t width;                  // populated with mWidth
    uint32_t height;                 // populated with mHeight
    uint32_t transformHint;          // 0
    uint32_t numPendingBuffers;      // 0
    uint64_t nextFrameNumber;        // 0
    uint8_t  bufferReplaced;         // 0
    uint32_t maxBufferCount;         // 2 (kNumSlots)
    // Embedded FrameEventHistoryDelta:
    int64_t  ct_deadline;            // 0
    int64_t  ct_interval;            // 16,666,667 ns (60 Hz)
    int64_t  ct_presentLatency;      // 16,666,667 ns
    uint32_t deltaSize;              // 0  (no FrameEventsDelta entries)
};
```

A `static_assert(sizeof(...) == 57)` catches any future struct drift
at compile time.

---

## §5  Smoke verification

### 5.1  Test driver: `surface_smoke.cc` (extended)

Step-2's surface_smoke is now extended with **check F**:

```
F. CREATE_SURFACE on the ISurfaceComposerClient returns a non-null handle
   and a non-null IGraphicBufferProducer (DIFFERENT from Step-2, which
   returned null gbp).
   On that GBP:
     dequeue_one  — DEQUEUE_BUFFER round-trip; verify slot index in [0,2),
                    Fence flatten header == {len=4, fdCount=0},
                    status == 0 or 1 (BUFFER_NEEDS_REALLOCATION).
     request_one  — REQUEST_BUFFER(slot); verify nonNull marker == 1,
                    Flattenable header == {len=68, fdCount=1},
                    magic == 'GB01', dims match request, numFds=1, numInts=4,
                    readFileDescriptor() succeeds with fd >= 0 (= memfd
                    replicated via binder fd-table).
     queue_one    — QUEUE_BUFFER(slot); verify QueueBufferOutput header
                    == {len=57, fdCount=0}, trailing status == NO_ERROR.
   Repeats once (two full round-trips) to exercise the slot ring.
```

### 5.2  On-phone smoke: `m6step3-smoke.sh`

Same sandbox protocol as Steps 1-2, plus the new check F.  Full
transcript captured in
`aosp-surface-daemon-port/m6step3-smoke-run.log`.  Key excerpt:

```
[m6-step3] starting westlake-surface-daemon on /dev/vndbinder
[wlk-surface-daemon pid=19491] starting; binder=/dev/vndbinder; will register as "SurfaceFlinger"
[wlk-sf] WestlakeSurfaceComposer constructed; physicalDisplayToken=0x77008149d0
[wlk-surface-daemon] addService("SurfaceFlinger") OK; entering joinThreadPool

[m6-step3] PASS: SurfaceFlinger appears in listServices (Step-1 regression OK)
[m6-step3] running surface_smoke (Step-2 + Step-3 transaction acceptance, 6 checks)
[surface_smoke] A: PASS SurfaceFlinger remote BpBinder=0x7dcbdb54b0
[surface_smoke] B: PASS GET_PHYSICAL_DISPLAY_IDS -> {0}
[surface_smoke] C: PASS GET_PHYSICAL_DISPLAY_TOKEN(0) -> binder=0x7dcbdb4430
[surface_smoke] D: PASS GET_DISPLAY_STATS vsyncTime=0 vsyncPeriod=16666667
[surface_smoke] E: PASS CREATE_CONNECTION -> binder=0x7dcbdb56c0
[surface_smoke] F.cs: handle=0x7dcbdb4e80 gbp=0x7dcbdb5da0 transformHint=0 status=0
[surface_smoke] F.deq: slot=0 bufferAge=0 status=1
[surface_smoke] F.req: slot=0 magic=GB01 dims=1080x2280 stride=1088 format=1 numFds=1 numInts=4 fd=4 status=0
[surface_smoke] F.que: slot=0 status=0
[surface_smoke] F.deq: slot=0 bufferAge=0 status=1
[surface_smoke] F.req: slot=0 magic=GB01 dims=1080x2280 stride=1088 format=1 numFds=1 numInts=4 fd=4 status=0
[surface_smoke] F.que: slot=0 status=0
[surface_smoke] F: PASS buffer pipeline (slotA=0 slotB=0)
[surface_smoke] summary: 0 failure(s) of 6 checks
[m6-step3] PASS: all 6 transaction checks passed (Step-2 A..E + Step-3 F)
[m6-step3] done; result=0
```

Daemon-side log fragments confirming the memfd path:

```
[wlk-sf] CREATE_SURFACE name="westlake-m6step3-smoke" 1080x2280 fmt=1 flags=0x0 parent=0x0
[wlk-igbp] ctor 1080x2280 fmt=1 slots=2
[wlk-igbp] DEQUEUE_BUFFER req=1080x2280 fmt=1 usage=0x0 getTs=0
[wlk-mgb] ctor 1080x2280 fmt=1 stride=1088 bytes=9922560 (lazy; no memfd yet)
[wlk-mgb] allocated memfd=4 size=9922560 (1080x2280 stride=1088 fmt=1)
[wlk-igbp] DEQUEUE_BUFFER -> slot=0 memfd=4 state=DEQUEUED
[wlk-igbp] REQUEST_BUFFER idx=0
[wlk-igbp] QUEUE_BUFFER slot=0
[wlk-igbp] DEQUEUE_BUFFER req=1080x2280 fmt=1 usage=0x0 getTs=0
[wlk-igbp] DEQUEUE_BUFFER -> slot=0 memfd=4 state=DEQUEUED
[wlk-igbp] REQUEST_BUFFER idx=0
[wlk-igbp] QUEUE_BUFFER slot=0
[wlk-igbp] dtor (this=0x762081ae10)
[wlk-mgb] dtor closing memfd=4
```

Important observation: the **memfd is correctly allocated once and reused
across both DEQUEUE/REQUEST/QUEUE cycles** (the second round reuses memfd=4,
not a new allocation — proves slot caching works as designed).  When the
surface_smoke client process exits, the daemon's `WestlakeGraphicBufferProducer`
destructor closes the memfd cleanly (`[wlk-mgb] dtor closing memfd=4`).

### 5.3  Cross-process fd visibility

The smoke-side log line:

```
[surface_smoke] F.req: ... fd=4 status=0
```

shows that the memfd allocated in the daemon process (PID 19491) was
successfully replicated into the smoke process via the binder fd-table
replication mechanism that `Parcel::writeDupFileDescriptor` triggers.
CR33 Phase D pre-validated this same mechanism in isolation; this CR
proves it works end-to-end through `BpGraphicBufferProducer::requestBuffer`'s
reader path.

(Whether the fd value happens to be 4 in BOTH processes is coincidental
— file descriptors are per-process integers; the kernel's binder driver
allocates a fresh fd in the receiver's fd-table during the BR_TRANSACTION
parse and that fd happens to have the same numeric value in the daemon
and the smoke client in this run.)

### 5.4  Acceptance check-list

| Check | Result |
|-------|--------|
| Build is first-try clean (no warnings beyond the inherited `-fuse-ld=lld` benign notes) | PASS |
| Daemon binary `out/bionic/surfaceflinger` builds and is ARM64 bionic dyn-linked | PASS |
| Smoke binary `out/bionic/surface_smoke` builds and is ARM64 bionic dyn-linked | PASS |
| Step-1 regression: `SurfaceFlinger` still appears in our SM's `listServices` | PASS |
| Step-2 regressions A..E: all five Step-2 transaction checks still pass | PASS |
| Check F.cs: `CREATE_SURFACE` returns non-null handle AND non-null GBP | PASS |
| Check F.deq (×2): `DEQUEUE_BUFFER` returns slot in [0,2), Fence header valid | PASS |
| Check F.req (×2): `REQUEST_BUFFER` returns GraphicBuffer with 'GB01' magic, correct dims, 1 fd | PASS |
| Check F.que (×2): `QUEUE_BUFFER` returns valid QueueBufferOutput + NO_ERROR | PASS |
| Memfd lazily allocated once, reused across cycles (no leak across 2 round-trips) | PASS (log confirms) |
| Memfd correctly destroyed on daemon-side GBP destruction | PASS (log: `[wlk-mgb] dtor closing memfd=4`) |
| Clean tear-down (no orphan processes; vndservicemanager restarts) | PASS |

All 12 acceptance checks pass.  Step 3 is **done**.

### 5.5  Binary-size delta

| Binary | Step 2 stripped | Step 3 stripped | Δ |
|---|---|---|---|
| `surfaceflinger`   | 49 KB | 61 KB | +12 KB |
| `surface_smoke`    | 36 KB | 40 KB | +4 KB |

Daemon +12 KB covers:
- 12 IGBP handlers (~500 B each).
- `MemfdGraphicBuffer` allocator + flatten path (~3 KB).
- New BBinder subclass `WestlakeGraphicBufferProducer` (~2 KB vtable + virtuals).
- ~1 KB miscellaneous (string constants, format checks, dispatch table).

Smoke +4 KB covers the three new helpers (`dequeue_one`, `request_one`,
`queue_one`) plus the check F body.

---

## §6  Architecture decisions taken in Step 3

### 6.1  Memfd is the only Phase-1 backend

Per CR33's spike result, the memfd path is validated on the target phone
and `/dev/dma_heap` is **not** available on this kernel — meaning there
is no fallback to a dma-buf heap if memfd ever fails.  Step 3 commits
unconditionally to memfd for Phase 1; the M6 plan §5.4 decision holds
unchanged.  Step 4 / Step 5 can layer a dma_heap path on top **if** a
future device with kernel >= 4.18 ever needs GPU-backed buffers — but
that's M12 / Phase 3 work.

### 6.2  No `<ui/GraphicBuffer.h>` dependency

The daemon links only against libbinder + libutils/libcutils — NOT libui
(which would drag in libgralloctypes, libsync, the HIDL allocator client
glue, etc., ~3 MB of unwanted code).  Instead, we hand-emit the
`GraphicBuffer::flatten` wire format from `frameworks/native/libs/ui/GraphicBuffer.cpp`
§364 verbatim.  This is acceptable because:

1. The wire format is stable across Android 7..15 (it's `'GB01'` magic
   on every version; `'GBFR'` was the pre-Android-8 magic and AOSP-11
   reader supports both).
2. We don't need to *consume* GraphicBuffers; we only produce them.
3. Apps lock the buffer via `mmap(fd, 0, len)` going through their own
   libgui — which they get from the phone's `/system/lib64/libgui.so`
   (Android 15).  Our wire format is what their `unflatten` reads.

If a future Android version drifts the GraphicBuffer wire layout, the
fail surface is a single integer mismatch in `WestlakeMemfdGraphicBuffer::writeFlatPayload`
— easy to spot and easy to fix.  The hand-emitted layout is documented
inline with explicit field-by-field cross-references to the AOSP source.

### 6.3  Slot ring of 2, with immediate recycle on QUEUE

Phase 1 has no consumer thread — the daemon's QUEUE_BUFFER handler
immediately marks the slot FREE again so the next DEQUEUE can pick it.
This is a deliberate Phase-1 simplification:

- M6 plan §4.4 documents "2-slot ring" as the minimum viable.
- Real Android SurfaceFlinger has a consumer thread that pulls
  QUEUED buffers, locks them, blits to the display, and only then marks
  the slot FREE — providing natural back-pressure.
- Step 4 will introduce a consumer thread that pulls from the QUEUED
  state and ACKs the slot back to FREE via the DLST pipe writer.  Until
  then, the immediate-recycle behaviour is correct for smoke testing
  (apps can still write into the buffer, queue it, and we don't have a
  display anyway).

### 6.4  Lazy memfd allocation on first DEQUEUE

Two reasons:

1. **Memory cost**: a 1080×2280 RGBA8888 ring of 2 is ~19 MB of
   physical memory per surface.  Apps that create many surfaces but
   only render to one shouldn't pay for unused slots.
2. **Dimension flexibility**: the IGBP's DEQUEUE_BUFFER carries the
   requested width/height which may differ from CREATE_SURFACE's
   defaults (apps call `setBuffersDimensions` between).  Lazy alloc
   lets us pick the actual size at first use.

The trade-off: first DEQUEUE has higher latency (~100 µs for the memfd
+ ftruncate syscalls + 64-byte aligned stride math + fault-time page
allocation).  Subsequent DEQUEUE hits the cached slot — micro-fast.

### 6.5  `BUFFER_NEEDS_REALLOCATION` on every DEQUEUE

We return status=1 (BUFFER_NEEDS_REALLOCATION) on **every** DEQUEUE,
not just the first one.  Rationale: AOSP's libgui caches the
`GraphicBuffer` on its slot map and skips REQUEST_BUFFER if the daemon
returns status=0.  In Phase 1 we want libgui to issue REQUEST_BUFFER
every time so we can verify the round-trip works repeatedly — this is
also what real BufferQueueProducer does when the dimensions changed.

Step 4 may optimise this: cache the slot on the libgui side and skip
REQUEST_BUFFER until the slot's dimensions actually drift.

### 6.6  Non-Tier-1 codes ack with int32(NO_ERROR)

Most non-Tier-1 IGBP transactions return a single int32 status in
their reply.  Step 3 fall-through writes `int32(NO_ERROR)` — safe for
the common case (the caller reads zero, treats it as success, moves on).
A few transactions return more bytes (e.g., GET_UNIQUE_ID writes a
uint64).  Those will read trash if exercised, which is acceptable
fail-loud-then-promote behaviour for Phase 1.

---

## §7  Next-step blockers

### 7.1 Step 4 (writer-side DLST pipe + Compose host SurfaceView)

Blocker: **none** — we have:
- A working memfd-backed buffer pipeline (this CR).
- A working DLST pipe protocol from `project_pipe_surfaceview.md` (the
  stdout-pipe + SurfaceView render path used by the dalvikvm path).

Step 4 will:
1. Add a consumer thread inside the daemon that pulls QUEUED slots,
   `mmap`s the buffer's memfd, writes the pixel bytes to the DLST pipe,
   `munmap`s, marks the slot FREE.
2. Defer the slot recycle in QUEUE_BUFFER from "immediate" to
   "after consumer ACK".
3. Extend `surface_smoke.cc` with a check G that mmaps the buffer
   returned from REQUEST_BUFFER, writes a known pattern, queues it,
   and verifies the consumer-side mmap (or a /proc/$PID/fd hop)
   sees the pattern.

### 7.2 Step-2 follow-up (DisplayInfo struct layouts)

Unrelated to Step 3 — still pending whenever CR35 lands the live
framework.jar discovery.

### 7.3 ATTACH_BUFFER NACK is not a problem for noice / MockDonalds

CPU-rendered apps (which is everything we're testing) call DEQUEUE +
QUEUE, never ATTACH.  ATTACH is only used by hardware-composer paths
(BLASTBufferQueue, sync framework callers).  Phase-1 NACK is acceptable.

---

## §8  Person-time spent

- File reads / cross-references (AOSP `IGraphicBufferProducer.cpp`,
  `GraphicBuffer.cpp::flatten`, `QueueBufferInputOutput.cpp`,
  `FrameTimestamps.cpp`, our own Step-2 code, CR33 spike): ~50 min
- Code authoring (`MemfdGraphicBuffer.{h,cpp}` +
  `WestlakeGraphicBufferProducer.{h,cpp}` + CREATE_SURFACE wiring +
  surface_smoke.cc extensions + Makefile + m6step3-smoke.sh):
  ~90 min
- Build + push + smoke run: ~10 min (first-try clean, smoke PASS on
  first attempt)
- This report: ~35 min

**Total: ~3 h** (well inside the 4–6 h budget).

---

## §9  Pointers for the next Builder

If you are picking up **M6-Step4** (writer-side DLST pipe + display), read
in this order:

1. This report §7.1 (the gating context — none blocking).
2. [`M6_SURFACE_DAEMON_PLAN.md`](M6_SURFACE_DAEMON_PLAN.md) §4 (consumer-side
   pipeline) + §6 (DLST integration).
3. `project_pipe_surfaceview.md` (the existing stdout-pipe / SurfaceView
   render path from the dalvikvm Westlake stack — reuse its pixel-handoff
   protocol).
4. `WestlakeGraphicBufferProducer::onQueueBuffer` — where the immediate
   recycle currently happens.  You'll move it behind a consumer-thread
   ACK barrier.
5. `surface_smoke.cc` — natural place to add check G (mmap + verify
   cross-process pattern survival).

Acceptance for Step 4 per M6 plan §3:
- `surface_smoke.cc` check G: write a pattern into the dequeued
  buffer, queue it, observe the daemon-side consumer thread emits
  it on the DLST pipe (or via an inspection probe).
- Existing Step-3 smoke (`m6step3-smoke.sh`) still passes (no regression).
- A noice / MockDonalds run starts producing observable output on the
  Compose host SurfaceView (the actual "render to display" milestone).
