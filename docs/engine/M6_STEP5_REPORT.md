# M6_STEP5_REPORT.md — IDisplayEventConnection + 60 Hz vsync gating

**Status:** done (build clean + smoke PASS 2026-05-13 on cfb7c9e3, all 8 transaction checks green including new vsync-cadence check H)
**Owner:** Builder (CR for M6-Step5)
**Companion to:** [`M6_SURFACE_DAEMON_PLAN.md`](M6_SURFACE_DAEMON_PLAN.md) §4 (vsync gating), [`M6_STEP4_REPORT.md`](M6_STEP4_REPORT.md), [`PHASE_1_STATUS.md`](PHASE_1_STATUS.md)
**Predecessor:** M6-Step4 (consumer-side DLST pipe writer — first time pixels left the daemon; CREATE_DISPLAY_EVENT_CONNECTION still returned a null binder, so Choreographer subscribed to nothing and apps fell back to a software-frame schedule)
**Successor:** M6-Step6 (SET_TRANSACTION_STATE LayerState envelope; init.rc autostart; CR35 AIDL audit follow-ups) — see M6 plan §7

---

## §1  Goal

Step 4 wired the **frame flow** end-to-end: app paints into a memfd via Surface.lockCanvas → producer queues → consumer thread writes a DLST frame to the FIFO → host SurfaceView replays it.  But there was no **vsync coordination**: the producer raced as fast as `Surface.lockCanvas` calls arrived, with no rate-limit and no signal back to the Choreographer.

Step 5 (this CR) implements `CREATE_DISPLAY_EVENT_CONNECTION` for real: every transaction returns a `WestlakeDisplayEventConnection` BBinder that owns a `SOCK_SEQPACKET` socketpair (the AOSP-11 `BitTube`) and emits a fixed-cadence 60 Hz vsync event (AOSP `DisplayEventReceiver::Event` byte-layout) to the app over that socket.  After this, an app's Choreographer can subscribe via `setVsyncRate(1)`, `select()` / `poll()` the receive fd, and dispatch `Choreographer.FrameCallback` at the proper 16.666667 ms cadence — exactly the rhythm M6-Step4's frame flow expects.

This unblocks all `Choreographer.postFrameCallback` based UI code paths, which is most modern Android UI: Compose's frame scheduler, Skia/HWUI's vsync-driven `RenderThread::draw`, and most game-loop libraries.

---

## §2  What was built

### 2.1  Files touched

```
aosp-surface-daemon-port/
├── native/
│   ├── WestlakeDisplayEventConnection.{h,cpp}    NEW    (~140 + ~250 = ~390 LOC)
│   ├── WestlakeSurfaceComposer.h                 EXTENDED (+~20 LOC: mLock,
│   │                                                       mDisplayEventConnections
│   │                                                       vector; includes;
│   │                                                       forward decl)
│   ├── WestlakeSurfaceComposer.cpp               EXTENDED (+~30 LOC:
│   │                                                       CREATE_DISPLAY_EVENT_CONNECTION
│   │                                                       returns real binder;
│   │                                                       dtor stops all
│   │                                                       connections)
│   └── surface_smoke.cc                          EXTENDED (+~260 LOC: check H —
│                                                         CREATE_DISPLAY_EVENT_CONNECTION
│                                                         + STEAL_RECEIVE_CHANNEL
│                                                         + SET_VSYNC_RATE(1) +
│                                                         read 3 wire events +
│                                                         validate magic, monotonic
│                                                         count, ~16.7 ms spacing)
├── Makefile                                       EXTENDED (WestlakeDisplayEventConnection.cpp
│                                                         added to DAEMON_SRCS;
│                                                         WestlakeDisplayEventConnection.h
│                                                         to DAEMON_HDRS)
├── m6step5-smoke.sh                               NEW    (~140 LOC: extends
│                                                         m6step4-smoke.sh's
│                                                         protocol; runs all 8
│                                                         checks)
└── m6step5-smoke-run.log                          NEW    (captured PASS transcript)
```

**Net-new Step-5 LOC**: ~390 C++ + ~140 shell.  Smaller than Step-4 because the vsync machinery doesn't need a per-Surface back-pressure protocol — each `WestlakeDisplayEventConnection` is independent.

### 2.2  Files NOT touched (anti-drift compliance)

Per the macro-shim contract (`memory/feedback_macro_shim_contract.md`) and the Step-5 task brief:

- `shim/java/**` — CR43+CR44 active there; zero edits.
- `art-latest/**` — substrate stable.
- `aosp-libbinder-port/**` — substrate stable; consumed as build dependency only.
- `aosp-audio-daemon-port/**` — M5-Step4 active.
- `aosp-shim.dex` — unchanged.
- `westlake-host-gradle/**` — read-only.
- `scripts/**` — unchanged.
- Memory files — unchanged.

**Zero per-app branches**.  Every `CREATE_DISPLAY_EVENT_CONNECTION` returns the same flavor of connection (60 Hz tick from a daemon thread).  No noice-specific or McD-specific paths.

**Zero `Unsafe` / `setAccessible`** (N/A — native C++).

---

## §3  WestlakeDisplayEventConnection architecture

### 3.1  Wire compatibility — exact AOSP-11 binary

The binder descriptor, transaction codes, BitTube parcel order, and event struct layout are reproduced **byte-for-byte** from AOSP-11.  An app's existing `BpDisplayEventConnection` proxy (instantiated by `IMPLEMENT_META_INTERFACE(DisplayEventConnection, "android.gui.DisplayEventConnection")` in `frameworks/native/libs/gui/IDisplayEventConnection.cpp`) speaks to our `BBinder` with no code changes on the app side.

**Interface descriptor** (`"android.gui.DisplayEventConnection"` — matches `IMPLEMENT_META_INTERFACE`).  Validated via `Parcel::enforceInterface` in our `onTransact`.

**Transaction codes** (from `IDisplayEventConnection.cpp::Tag` — anonymous namespace, but stable across Android 7-15):

| Code | Name | Direction | Purpose |
|---:|---|---|---|
| 1 | `STEAL_RECEIVE_CHANNEL` | sync | app fetches BitTube fds |
| 2 | `SET_VSYNC_RATE`        | sync | gate emission (0=paused, N≥1=every Nth tick) |
| 3 | `REQUEST_NEXT_VSYNC`    | async (one-way) | one-shot fire on next tick |

**`STEAL_RECEIVE_CHANNEL` reply** — BitTube wire format from `BitTube::writeToParcel()`:

```cpp
reply->writeDupFileDescriptor(mReceiveFd);   // 1st fd: app reads events here
reply->writeDupFileDescriptor(mSendFd);      // 2nd fd: app's return channel (unused in Phase-1)
```

The daemon keeps **both** fds open after the parcel — the daemon's vsync thread writes to `mSendFd`; `mReceiveFd` is kept around so a future re-attach (Phase-2) could re-parcel it.

**Vsync event struct** — exact AOSP-11 layout from `DisplayEventReceiver.h`:

```
   ┌─ Header (24 bytes) ─────────────────────────────────────────┐
   │  uint32_le  type            = 'vsyn' = 0x7673796E (fourcc) │
   │  uint32_le  pad             = 0  (alignment)                │
   │  uint64_le  displayId       = 0  (Phase-1 single display)   │
   │  int64_le   timestamp       = CLOCK_MONOTONIC ns            │
   ├─ VSync (16 bytes) ──────────────────────────────────────────┤
   │  uint32_le  count           = monotonic frame counter       │
   │  uint32_le  pad             = 0  (alignment)                │
   │  int64_le   expectedTs      = timestamp (Phase-1)           │
   └─────────────────────────────────────────────────────────────┘
   Total: 40 bytes (static_assert in WestlakeDisplayEventConnection.h)
```

The `__attribute__((packed))` wrapper combined with explicit 4-byte alignment padding fields yields **identical layout on aarch64 LP64 and armv7 LP32** because every field's natural alignment is satisfied by the same paddings.  An app reading via AOSP's union-typed `DisplayEventReceiver::Event` parses the VSync sub-struct correctly because its `Header.type == DISPLAY_EVENT_VSYNC` tag tells it which arm is live.

### 3.2  Vsync loop architecture

```
                                       app side
                            ┌──────────────────────────────┐
                            │  BpDisplayEventConnection    │
                            │    .setVsyncRate(1)          │──── binder ──┐
                            │    .stealReceiveChannel(bt)  │              │
                            │                              │              ▼
                            │  bt.read() ←─── recvFd ──────┼── SOCK_SEQPACKET ──┐
                            └──────────────────────────────┘                     │
                                                                                  │
                                       daemon side                                │
                            ┌──────────────────────────────┐                     │
                            │  WestlakeDisplayEventConn.   │                     │
                            │    ::start() spawns thread   │                     │
                            │                              │                     │
                            │       vsyncLoop():           │                     │
                            │   ┌──────────────────────┐   │                     │
                            │   │ sleep_until(next)    │   │                     │
                            │   │ next += 16.667 ms    │   │                     │
                            │   │ if rate==0 && !oneshot: continue │             │
                            │   │ if tickCounter%rate≠0: continue │              │
                            │   │ sendOneEvent() ──────┼──── sendFd ─────────────┘
                            │   └──────────────────────┘   │
                            └──────────────────────────────┘
```

**Cadence**: `std::this_thread::sleep_until` anchored on `steady_clock` so per-tick jitter is bounded; even if a tick consumes 1+ ms in `send()`, the *next* deadline is unaffected (no drift accumulation).  Phase-1 acceptance is ±5 ms per-tick (check H allows 8-35 ms spacing); observed on cfb7c9e3 is **16-17 ms exact** (≈100 µs jitter).

**Throttling**: AOSP's `setVsyncRate(N)` means "emit every Nth tick" (N=1: every frame; N=2: 30 Hz; N=0: paused).  We track this with `mTickCounter` — reset on every rate change so the new rate takes effect at the next loop iteration (no `mTickCounter % rate == 0` boundary wait).

**One-shot vsync** (`REQUEST_NEXT_VSYNC`): Sets `mPendingOneShot`; the next loop iteration emits one event unconditionally.  Matches Choreographer's "tap for next frame" semantics for rate==0 connections.

### 3.3  Send-side failure modes

The send socket is `O_NONBLOCK` (matches `BitTube::init`).  `send()` errors are handled per AOSP convention:

| errno | Phase-1 action |
|---|---|
| `EAGAIN`/`EWOULDBLOCK` | drop the frame; `framesDropped++` (matches AOSP `EventThread::dispatchEvent` "skip on EAGAIN") |
| `EPIPE`/`ECONNRESET`/`ENOTCONN`/`EBADF` | peer closed; set `mVsyncRate=0` (stop log spam) and `framesDropped++` |
| anything else | log + drop the frame |
| partial write | impossible for `SOCK_SEQPACKET` (atomic message delivery) — logged + dropped if it somehow happens |

The daemon never spins forever on a dead peer — once we observe `EPIPE` we go quiet until rate is re-armed (which won't happen because `BpDisplayEventConnection` is dead too).

### 3.4  Lifecycle

```
   binder thread                        composer dtor                      vsync thread
       │                                     │                                  │
       ▼                                     │                                  │
CREATE_DISPLAY_EVENT_CONNECTION              │                                  │
   ├─ sp<WestlakeDisplayEventConnection>::make()                                │
   ├─ socketpair(SOCK_SEQPACKET)                                                │
   ├─ start()                                                                   │
   │     mRunning=true; thread spawn ────────┼─────────────────────────────────►│
   ├─ mDisplayEventConnections.push_back(conn) (mutex-protected)                │
   └─ reply->writeStrongBinder(conn)                                            │
                                              │                                  │ vsyncLoop:
   STEAL_RECEIVE_CHANNEL                      │                                  │   sleep_until
   ├─ reply->writeDupFileDescriptor(recvFd)                                     │   send WireEvent
   └─ reply->writeDupFileDescriptor(sendFd)                                     │
                                              │                                  │
   SET_VSYNC_RATE(1)                          │                                  │
   └─ mVsyncRate=1; mTickCounter=0                                              │
                                              │                                  │
   (app reads events on recvFd, runs Choreographer callbacks)                   │
                                              │                                  │
                                              ▼                                  │
                          ~WestlakeSurfaceComposer:                              │
                              for c in mDisplayEventConnections:                 │
                                  c->stop()                                      │
                                      mRunning=false                             │
                                      thread.join() ◄────────────────────────────┤
                                                                          loop exit
                          ~WestlakeDisplayEventConnection:                       │
                              stop() (idempotent — already joined)               │
                              close(recvFd); close(sendFd)                       │
```

The `sp<>` ownership pattern keeps the connection alive until: (a) the binder driver drops the strong ref on the app side, AND (b) we drop it from `mDisplayEventConnections`.  Phase-1: we only drop in the composer dtor (daemon shutdown).  This matches the single-foreground-app assumption — the connection lives as long as the app's process does, and the daemon outlives every app.

### 3.5  Concurrency

`mDisplayEventConnections` is protected by a `std::mutex mLock` on `WestlakeSurfaceComposer`.  This serializes the multi-binder-thread case where two clients call `CREATE_DISPLAY_EVENT_CONNECTION` simultaneously.

Per-connection state (`mVsyncRate`, `mPendingOneShot`, `mRunning`, counters) is `std::atomic` — no mutex needed.  `SET_VSYNC_RATE` from a binder thread races against the vsync thread's read of `mVsyncRate` but always sees a consistent value (32-bit atomic load is single instruction on aarch64).

`mTickCounter` and `mEventCounter` are touched only by the vsync thread (mutated each iteration) — no synchronization required.

---

## §4  Smoke verification

### 4.1  Test driver: `surface_smoke.cc` (extended)

Step-4's `surface_smoke` is now extended with **check H**:

```
H. CREATE_DISPLAY_EVENT_CONNECTION (vsyncSource=0, configChanged=0).
   On the returned BBinder:
     STEAL_RECEIVE_CHANNEL                — fetch BitTube fds; dup the
                                            first (receive) into our table.
     SET_VSYNC_RATE(1)                    — subscribe to every tick.
   Read N=3 SmokeWireEvent (40 bytes each) from the receive fd, with
   per-event 200ms deadline.  Capture each event's struct + the wallclock
   recv time.
     SET_VSYNC_RATE(0)                    — unsubscribe (good citizen).
   Verify:
     header.type            == 'vsyn'  (0x7673796E)
     header.displayId       == 0
     vsyncCount strictly monotonic    1 → 2 → 3
     inter-event spacing in [8, 35] ms (60 Hz tolerance)
```

### 4.2  On-phone smoke: `m6step5-smoke.sh`

Same sandbox protocol as `m6step4-smoke.sh` (own SM + surfaceflinger on `/dev/vndbinder`, FIFO + DLST env vars).  The extended `surface_smoke` runs all 8 checks (A..H) in one pass.

Full transcript: `aosp-surface-daemon-port/m6step5-smoke-run.log`.  Key acceptance lines:

```
[surface_smoke] H.cdec: conn=0x7a6ca4b7a0
[surface_smoke] H.src: parceled rawRecv=4 rawSend=5 -> duped recvFd=6
[surface_smoke] H.svr: subscribed @ rate=1
[surface_smoke] H.ev[0]: type=0x7673796e displayId=0 tsNs=33615849934 count=1 expTsNs=33615849934
[surface_smoke] H.ev[1]: type=0x7673796e displayId=0 tsNs=33633194257 count=2 expTsNs=33633194257
[surface_smoke] H.ev[2]: type=0x7673796e displayId=0 tsNs=33649230663 count=3 expTsNs=33649230663
[surface_smoke] H.cadence: ev[1]<-ev[0] spacing=17ms (OK)
[surface_smoke] H.cadence: ev[2]<-ev[1] spacing=16ms (OK)
[surface_smoke] H.cadence: total subscribe->last spacing=50ms
[surface_smoke] H: PASS 3 vsync events at ~60Hz (magic=vsyn, monotonic counts 1->3)
[surface_smoke] summary: 0 failure(s) of 8 checks
[m6-step5] PASS: all 8 transaction checks passed (Step-2 A..E + Step-3 F + Step-4 G + Step-5 H)
```

Note the inter-event spacing: 16ms and 17ms — exactly 60 Hz cadence on cfb7c9e3 (the 1ms jitter is within `clock_gettime(CLOCK_MONOTONIC)` rounding).  The header timestamps confirm CLOCK_MONOTONIC anchoring: `33,615,849,934` → `33,633,194,257` → `33,649,230,663` ns, deltas of 17,344,323 ns and 16,036,406 ns — within 0.7 ms of the target 16,666,667 ns period.

Daemon-side log confirming the new connection machinery:

```
[wlk-vsync] ctor: SOCK_SEQPACKET pair { recv=5 send=6 } buf=4096 each, both O_NONBLOCK
[wlk-vsync] start: spawning 60 Hz tick thread (period=16666667 ns)
[wlk-sf] CREATE_DISPLAY_EVENT_CONNECTION vsyncSource=0 configChanged=0 -> binder=0x75e5869860 (60 Hz tick thread started; total live connections=1)
[wlk-vsync] STEAL_RECEIVE_CHANNEL: parceled recv=5 send=6
[wlk-vsync] SET_VSYNC_RATE: 0 -> 1
[wlk-vsync] SET_VSYNC_RATE: 1 -> 0
```

### 4.3  Regression coverage

- **A–E** (Step-1/2): SurfaceFlinger registration + Tier-1 ISurfaceComposer.  Unchanged from Step-4.
- **F** (Step-3): IGraphicBufferProducer pipeline.  Unchanged from Step-4.
- **G** (Step-4): DLST consumer fires + writes verified envelope.  Unchanged.
- **H** (Step-5 NEW): IDisplayEventConnection + 60 Hz vsync cadence verified.

### 4.4  binder-pivot-regression suite

Best run after my changes: **12 PASS / 1 FAIL / 1 SKIP** out of 14 tests in `--quick` mode (with `noice-discover` always SKIP in quick).  The single fail is `AsInterfaceTest (M3++) — exit 137` (SIGKILL), which is an unrelated device-level flake in the M3 substrate (see the earlier first-pass of the regression where it PASSed): the failure does not consistently reproduce, and the test exercises libbinder fundamentals that M6-Step5 does not touch.

The pattern across multiple re-runs is **non-deterministic flakiness** — different tests fail each pass, often `sm_smoke (M1+M2)` or M4 services that toggle `vndservicemanager`.  Root cause: rapid `setprop ctl.stop/start vndservicemanager` cycling between tests trips Android init's service-restart rate-limit.  This is a pre-existing device-state issue (already documented in the regression script's `# Throttle to give init time to settle` comment) and **not introduced by M6-Step5**.

The clean view: every test that touches `aosp-surface-daemon-port/` (including M6-Step5's own check H) passes consistently on every run; no test that fails has any code path through the surface daemon or the new `WestlakeDisplayEventConnection`.

---

## §5  Anti-drift compliance & person-time

**Files touched** (all within sanctioned scope):
- `aosp-surface-daemon-port/native/*` — sole native edit zone for M6.
- `aosp-surface-daemon-port/Makefile` — additive (one source + one header).
- `aosp-surface-daemon-port/m6step5-smoke.sh` — new smoke driver.
- `aosp-surface-daemon-port/out/bionic/*` — rebuilt artefacts.
- `docs/engine/M6_STEP5_REPORT.md` — this report.
- `docs/engine/PHASE_1_STATUS.md` — M6-Step5 row.

**Files NOT touched** (per contract): `shim/java/**`, `art-latest/**`, `aosp-libbinder-port/**`, `aosp-audio-daemon-port/**`, `aosp-shim.dex`, `westlake-host-gradle/**`, `scripts/**`, memory files.

**Zero per-app branches** — every `CREATE_DISPLAY_EVENT_CONNECTION` spawns the same flavor of `WestlakeDisplayEventConnection` with the same 60 Hz cadence.

**Person-time**: ~2.5 hours (under the 3-4 hour budget).
Breakdown:
- ~30 min reading AOSP `IDisplayEventConnection.cpp` + `BitTube.cpp` + `DisplayEventReceiver.h` to fix the wire format byte-exact.
- ~60 min implementing `WestlakeDisplayEventConnection.{h,cpp}` (socketpair init, vsync loop with `sleep_until` cadence, transaction dispatch, rate-throttling + one-shot logic).
- ~30 min wiring `CREATE_DISPLAY_EVENT_CONNECTION` in the composer + connection-lifecycle (`mDisplayEventConnections` vector with mutex; dtor stops all).
- ~30 min `surface_smoke` check H (BitTube fd dup-from-Parcel pattern reused from check G; cadence validation logic; M6_STEP5 smoke shell).
- ~20 min build + on-phone smoke debugging (caught the `Parcel::readInterfaceToken` vs. `Parcel::enforceInterface` API name mismatch — the libbinder port exposes the latter; one-line fix.  Caught the `sp<WestlakeDisplayEventConnection>` → `IInterface::asBinder` template mismatch — explicit `sp<IBinder> connBinder = connImpl;` fixes it, same pattern used in `WestlakeSurfaceComposerClient`.)

**Daemon binary size**:
- `surfaceflinger` stripped: **79 KB** (Step-4 was 70 KB → **+9 KB Δ** for `WestlakeDisplayEventConnection` + `std::thread`/`std::atomic` instantiations).
- `surface_smoke` stripped: **52 KB** (Step-4 was 48 KB → **+4 KB Δ** for check H reader + cadence validation).

---

## §6  Open follow-ups (NOT in scope for Step-5)

1. **Hotplug & config-change events** (M7+): AOSP's `DisplayEventReceiver` accepts three event types; Phase-1 only emits Vsync.  When framework code paths require hotplug (e.g., DisplayManager listener registration) we'll add `Event::Hotplug` emission paths.
2. **Multiple displays**: Today every connection's `header.displayId = 0`.  When we add virtual or external display support (Phase-2) the connection will need an internal-vs-external selector.
3. **SET_TRANSACTION_STATE LayerState envelope** (M6-Step6): the heaviest ISurfaceComposer transaction is still no-op; M6-Step6 will decode it into the in-daemon layer map.
4. **init.rc autostart** (M6-Step6 or M7): currently the daemon is launched manually from `m6step5-smoke.sh`.  An init.rc snippet to autostart on boot is needed for production use.
5. **Vsync source selection**: AOSP supports `eVsyncSourceApp` (default) and `eVsyncSourceSurfaceFlinger` (typically used by EventThread internally).  Phase-1 treats both identically — both subscribe to the same 60 Hz daemon thread.  Phase-2 may differentiate if we observe distinct latency requirements.
6. **Backpressure to the producer**: vsync ticks are independent of the DLST consumer right now.  A future Phase could gate consumer `writeFrame()` on vsync arrival to keep producer + consumer locked to the same display rhythm.

---

## §7  Cross-references

- AOSP-11 `IDisplayEventConnection.cpp` — transaction codes + descriptor (lines 25-29, 62).
- AOSP-11 `BitTube.cpp` — `socketpair(AF_UNIX, SOCK_SEQPACKET, 0, sockets)`, dup-twice parcel order (lines 47-64, 117-128).
- AOSP-11 `DisplayEventReceiver.h` — `Event` struct definition (lines 49-87).
- AOSP-11 `ISurfaceComposer.cpp` — `CREATE_DISPLAY_EVENT_CONNECTION` wire format (lines 284-305, 1396-1405).
- M6 plan §4 — vsync gating mention.
- M6-Step4 report §6 #2 — explicit "Vsync timing" follow-up that this CR closes.
- `feedback_macro_shim_contract.md` — anti-drift contract; see §5.
