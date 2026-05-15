# CR38 — M7/M8 final integration scoping (post-V2 + post-M5/M6)

**Status:** scoping (read-only architect pre-flight for M7+M8)
**Author:** Architect agent (2026-05-13)
**Companion to:**
- `docs/engine/BINDER_PIVOT_MILESTONES.md` §M7 / §M8 (the canonical V1 entries this doc refines)
- `docs/engine/M5_AUDIO_DAEMON_PLAN.md` (CR21; native audio daemon scoping)
- `docs/engine/M6_SURFACE_DAEMON_PLAN.md` (CR21; native surface daemon scoping)
- `docs/engine/CR33_M6_SPIKE_REPORT.md` (memfd/AHB feasibility PASS)
- `docs/engine/CR34_M5_SPIKE_REPORT.md` (AAudio feasibility PASS)
- `docs/engine/CR37_M5_AIDL_DISCOVERY.md` (M5 transaction surface, exact)
- `docs/engine/BINDER_PIVOT_DESIGN_V2.md` (V2 in-process Java substrate)
- `docs/engine/M6_STEP1_REPORT.md` (M6 daemon process pattern PASS)
- `docs/engine/PHASE_1_STATUS.md` §1.4 (current state — V2 14/14 PASS; M5/M6 daemons not yet registered)
- `aosp-libbinder-port/test/noice-discover.sh` + `aosp-libbinder-port/test/mcd-discover.sh` (current discovery harnesses; predecessors to the M7/M8 integration tests this doc scopes)

**Predecessor state (as of 2026-05-13):**
- V2 substrate: complete, 14/14 regression PASS (V2-Step1..Step8-fix landed; `WestlakeActivity` + `WestlakeApplication` + thin `WestlakeResources` + `Window/PhoneWindow/DecorView/WindowManagerImpl` stubs shadow framework.jar via `framework_duplicates.txt`).
- M1/M2/M3/M3++: complete (musl + bionic libbinder.so, servicemanager, dalvikvm wired against our libbinder, same-process `Stub.asInterface` elision).
- M4a/M4b/M4c/M4d/M4e + M4-power: complete (6 binder services registered, Tier-1 methods implemented per CR1, all others fail-loud per CR2).
- M4-PRE4 / M4-PRE5 / M4-PRE7 / M4-PRE9 / M4-PRE10: complete (MessageQueue JNI, local PackageManager stub, AssetManager JNI [V2 may delete], ContentResolver, CharsetPrimer).
- M5-PRE: complete (105 AudioSystem JNI stubs).
- noice-discover reaches PHASE G4 (`MainActivity.onCreate(null)` body executing).
- M5 audio daemon: spike done (CR34 PASS, plan §9.1 estimate 6.5 person-days HELD); native daemon **not yet started**.
- M6 surface daemon: M6-Step1 done (skeleton + process pattern + addService PASS); Steps 2-6 pending; CR35 (AIDL discovery for ISurfaceComposer) gates Step 2; full §9.1 estimate 12 person-days HELD.

**Anti-drift contract honored:** zero source-code edits, zero Westlake-shim changes, all output in this NEW doc plus a small row in `PHASE_1_STATUS.md` §1.4 and small annotations on `BINDER_PIVOT_MILESTONES.md` §M7/§M8 pointing here.

This doc pre-scopes the final integration step: once M5 + M6 land, how do M7 (noice e2e) and M8 (McDonald's regression) plug them together into running apps end-to-end? Boot sequence, daemon wiring, cross-daemon coordination, test fixtures, effort estimates, plumbing-gap audit. The Builder/Integrator agent who eventually executes M7 + M8 should treat this as the work breakdown.

---

## §1. Architecture diagram (post-V2 + post-M5 + post-M6)

### 1.1 Process tree on the OnePlus 6 (Phase 1 target)

When `scripts/run-noice-westlake.sh` (NEW, M7 deliverable) or `scripts/run-mcd-westlake.sh` (NEW, M8 deliverable) fires, the Westlake host APK on the phone is the orchestrator. From its perspective:

```
com.westlake.host (the Android app installed on cfb7c9e3 via adb install)
│   pid=N (uid=10NNN — normal Android app uid; not system)
│   APK contents:
│     - assets/dalvikvm                       (28 MB bionic-static dalvikvm)
│     - assets/lib-bionic/libbinder.so        (1.76 MB)
│     - assets/lib-bionic/libutils_static.a etc. (build-time only)
│     - assets/bin-bionic/servicemanager      (M2 bionic)
│     - assets/bin-bionic/audio_daemon        (M5 bionic — new)
│     - assets/bin-bionic/surface_daemon      (M6 bionic — new)
│     - assets/aosp-shim.dex                  (~1.4 MB, V2-substrate Java)
│     - assets/framework.jar + ext.jar        (real AOSP framework on BCP)
│     - assets/core-{oj,libart,icu4j}.jar + bouncycastle.jar
│     - assets/noice.apk OR mcd.apk           (subject under test)
│   Host code:
│     - Kotlin/Compose UI: status panel + SurfaceView (existing DLST pipe consumer)
│     - WestlakeVM.kt:1850+ (DLST opcode reader → SurfaceView.lockCanvas)
│     - Subprocess orchestrator (this work — see §3)
│
├── /data/local/tmp/westlake/bin-bionic/servicemanager   (M2 daemon)
│       uid=10NNN, opens /dev/vndbinder, owns binder context-mgr role
│       (NOT the phone's vndservicemanager — we stop that for our test window)
│
├── /data/local/tmp/westlake/bin-bionic/audio_daemon     (M5 daemon)
│       uid=10NNN, opens /dev/vndbinder, addService("media.audio_flinger")
│       internal: AudioBackend = AAudioBackend → libaaudio.so → OS speaker
│       (per M5 plan §3.5 + CR34 spike verified)
│
├── /data/local/tmp/westlake/bin-bionic/surface_daemon   (M6 daemon)
│       uid=10NNN, opens /dev/vndbinder, addService("SurfaceFlinger")
│       internal: SurfaceBackend = DlstPipeBackend → stdout DLST opcodes
│       (per M6 plan §3.5 + 4.2 + M6-Step1 process pattern verified)
│
└── /data/local/tmp/westlake/dalvikvm                    (M3 process)
        uid=10NNN, bionic-static dalvikvm
        -Xbootclasspath = aosp-shim.dex:framework.jar:ext.jar:core-*.jar
        -cp = noice.apk OR mcd.apk
        Main class = com.westlake.engine.WestlakeLauncher
        Loads V2 substrate (WestlakeActivity etc.) via classpath-shadow
        Hilt injection + Activity.onCreate fire real binder transactions:
          - getService("activity") → BpBinder → /dev/vndbinder → M4a
          - getService("media.audio_flinger") → BpBinder → /dev/vndbinder → M5
          - getService("SurfaceFlinger") → BpBinder → /dev/vndbinder → M6
        Surface frames flow: View tree → Canvas/HWUI → BufferQueue
            → IGraphicBufferProducer (binder) → M6 → DLST pipe
            → com.westlake.host's stdin → WestlakeVM.kt parser → SurfaceView
```

Five OS-level processes total on the phone during a single M7 (or M8) run, all under one uid: host APK, servicemanager, audio_daemon, surface_daemon, dalvikvm. (Phase 1 keeps the Compose host SurfaceView visual layer; Phase 2 / M12 collapses that into XComponent.)

### 1.2 Binder substrate vs in-process substrate (V1 + V2 combined)

The substitution surface, drawn against `BINDER_PIVOT_DESIGN_V2.md` §3.1's table:

```
+-----------------------------+   in-process    +-----------------------------+
| dalvikvm process            |   (no binder)   | dalvikvm process            |
|                             |                 |                             |
|  app.MainActivity           |                 |  androidx.fragment.*        |
|       │ extends             |                 |  android.view.View*         |
|  app.AppCompatActivity      |                 |  android.widget.*           |
|       │ extends             |                 |  android.os.Handler/Looper  |
|  android.app.Activity       <─── V2 SHADOW    |  android.graphics.*         |
|  (= WestlakeActivity)       |   (this file    |  android.os.Parcel/Bundle   |
|       │ holds mBase         |   replaces      |  android.util.*             |
|  android.content.Context    |   framework.jar |  (all from framework.jar    |
|  (= WestlakeContextImpl)    |   copy)         |   verbatim — V2 row 13)     |
|       │                     |                 |                             |
|       ▼                     |                 |                             |
|  ServiceManager.getService("name")            |                             |
|       │ JNI                                   |                             |
|       ▼                                       |                             |
|  binder_jni_stub.cc → libbinder.so → BpBinder |                             |
|       │ /dev/vndbinder ioctl                  |                             |
+-------┼-----------------------------------------------------------+---------+
        │
        │  cross-process binder (V1 substrate; survives unchanged)
        ▼
+-------+-----------------------------------------------------------+---------+
| servicemanager (M2)        audio_daemon (M5)      surface_daemon (M6)       |
|   /dev/vndbinder            BnAudioFlinger          BnSurfaceComposer       |
|   context-mgr               BnAudioTrack            BnGraphicBufferProducer |
|                             AAudioBackend           DlstPipeBackend         |
|                             ─→ libaaudio.so         ─→ stdout pipe          |
+-------+-----------------------------------------------------------+---------+
        │                          │                                │
        │                          ▼                                ▼
        │                       OS speaker              com.westlake.host SurfaceView
        │                       (audible)                (visible)
```

Same-process binder traffic from dalvikvm → ServiceManager goes through M3++'s `nativeGetLocalService` elision when the service is registered in-process (e.g. our M4 Java services), and through real `/dev/vndbinder` round-trips for cross-process services (the 3 native daemons). The split is invisible to framework.jar.

### 1.3 What's new at M7/M8 vs current (post-V2-Step8-fix, pre-M5/M6)

```
                          PRE-M5/M6                 POST-M5/M6 (M7/M8 target)
ServiceManager:           ✓                         ✓
M4 services (6):          ✓                         ✓
V2 substrate (Java):      ✓ 14/14 PASS              ✓ 14/14 PASS
audio_daemon:             ✗ (M5-PRE stubs only)     ✓ registered, AAudio backed
surface_daemon:           ✗ (M6-Step1 stub only)    ✓ registered, DLST pipe back
noice/MainActivity:       PHASE G4 + Configuration  full onCreate + first frame
                          .setTo(null) NPE          visible on phone screen
McD/SplashActivity:       SuperSeparated G-NPE wall sim. → SplashActivity render
Audio playback:           N/A                       noice "Rain" preset audible
```

The architectural delta between "V2 substrate landed" (current) and "M7/M8 PASS" (target) is exactly: **two native daemons run, framework-side audio + surface paths reach them, and the visible-output / audible-output assertions hold.** No further Java substrate work — V2 already covers everything in dalvikvm.

---

## §2. Boot sequence (12-step canonical orchestration)

This is the canonical Phase-1 boot. The Builder of M7's `scripts/run-noice-westlake.sh` (and M8's `scripts/run-mcd-westlake.sh`) implements exactly this sequence. The current `aosp-libbinder-port/test/noice-discover.sh` is the **predecessor** harness — it stops at step 8 (no daemons, no rendering); the M7 script extends it through step 12.

### 2.1 The 12 steps

```
Step  Actor                    Action                                                Validation signal
────  ─────                    ──────                                                ─────────────────
 1   adb / launcher script    Push artifacts to /data/local/tmp/westlake/ on phone   files in place; SHA-256 match
 2   adb / launcher script    Stop the phone's native vndservicemanager              setprop ctl.stop vndservicemanager; pidof returns nothing within 5s
 3   adb / launcher script    Spawn our M2 servicemanager on /dev/vndbinder          pgrep matches; SM log "context mgr obtained"
 4   adb / launcher script    Spawn M5 audio_daemon                                  daemon log "AAudio probe ok"; SM log "addService media.audio_flinger ok"
 5   adb / launcher script    Spawn M6 surface_daemon (stdout piped → host APK)     daemon log "DlstPipeBackend stdout opened"; SM log "addService SurfaceFlinger ok"
 6   adb / launcher script    Start the Compose host APK foreground                 com.westlake.host activity visible; SurfaceView present and listening
 7   adb / launcher script    Pipe surface_daemon stdout INTO the host APK's stdin  (preserved across binder; see §3.2 for plumbing detail)
 8   adb / launcher script    Spawn dalvikvm with V2 substrate + target APK         pidof dalvikvm matches
 9   dalvikvm                 Run WestlakeLauncher.main → ColdBootstrap.ensure()    sCurrentActivityThread seeded; Looper.prepareMainLooper OK
10   dalvikvm                 Reflectively load + instantiate app.Application       Application.<init> runs; Hilt-generated Hilt_NoiceApplication.<init> OK
11   dalvikvm                 Application.attachBaseContext → onCreate              Hilt DI fires; real binder transactions to M4a (activity) + Hilt-injected
                                                                                    M4b (window) + M4c (package). First M5 transaction may fire here if app
                                                                                    pre-warms AudioManager (most apps don't).
12   dalvikvm                 Launch MainActivity (or SplashActivity for McD)       Activity.attach → Activity.onCreate → setContentView →
                                                                                    LayoutInflater inflates real layout XML →
                                                                                    Activity.onResume → first draw() → BufferQueue dequeueBuffer →
                                                                                    M6 receives queueBuffer → DLST opcode out → host SurfaceView draws → VISIBLE FRAME
                                                                                    User taps sound preset → real noice MediaPlayer/AudioTrack →
                                                                                    AudioTrack.write → cblk → M5 → AAudio → AUDIBLE
```

### 2.2 Step-level dependencies

```
Step 1  ──> 2  ──> 3 (SM)
                   ├──> 4 (audio_daemon needs SM)
                   ├──> 5 (surface_daemon needs SM)
                   ├──> 6 (host APK independent but UI orchestration timing)
                   └──> 7 (pipe wiring; needs both 5 + 6)
                              └──> 8 (dalvikvm: ALL services + pipe must be ready)
                                       └──> 9 ──> 10 ──> 11 ──> 12

Hard-fail gates (script must exit 1 if any of these miss within timeout):
  3:  SM log "context mgr obtained"  (timeout 5s)
  4:  SM log "addService media.audio_flinger ok"  (timeout 10s — AAudio probe ~2s)
  5:  SM log "addService SurfaceFlinger ok"  (timeout 5s)
  6:  com.westlake.host visible (am stack)  (timeout 15s)
  8:  pidof dalvikvm  (timeout 5s)
  11: aosp-shim.dex log "Application.onCreate ENTER" then "Application.onCreate EXIT"  (timeout 60s)
  12: surface_daemon log "queueBuffer slot=N seq=1" (first frame)  (timeout 30s)
       AND visual: host SurfaceView shows non-black content
```

### 2.3 Drift-prevention: do NOT manufacture coupling between daemons

M5 and M6 are independent. M5 does not depend on M6; M6 does not depend on M5. Order of step 4 vs step 5 is arbitrary; the script can spawn them concurrently if it wants (one less `wait` saves ~0.5s). The only ordering constraint is **both must finish addService before dalvikvm boots in step 8** (because framework.jar caches `ServiceManager.getService("SurfaceFlinger")` lookups; same for `media.audio_flinger`; a null lookup is sticky).

The M5/M6 daemons share no IPC, no shared memory, no callbacks. Resist the temptation to add a "render hook" from M5 into M6 (audio→video sync) — Phase 1 doesn't need it. Phase 2 (OHOS, post-M11/M12) may want true A/V sync for media playback; that's a separate cross-daemon protocol and is **explicitly out of M7/M8 scope**. See §4 below.

---

## §3. Daemon wiring (the routing table)

This section is the canonical answer to "where does each system service handle live?" — for every `ServiceManager.getService(name)` call in framework.jar.

### 3.1 The routing table

| Service name | Service handle type | Lives in | Backed by | Wire path | Milestone |
|---|---|---|---|---|---|
| `activity` | `IActivityManager.Stub` | dalvikvm (same-process) | `WestlakeActivityManagerService` Java | M3++ local elision | M4a (done) |
| `window` | `IWindowManager.Stub` | dalvikvm (same-process) | `WestlakeWindowManagerService` Java | M3++ local elision | M4b (done) |
| `package` | `IPackageManager.Stub` | dalvikvm (same-process) | `WestlakePackageManagerService` Java | M3++ local elision | M4c (done) |
| `display` | `IDisplayManager.Stub` | dalvikvm (same-process) | `WestlakeDisplayManagerService` Java | M3++ local elision | M4d (done) |
| `notification` | `INotificationManager.Stub` | dalvikvm (same-process) | `WestlakeNotificationManagerService` Java | M3++ local elision | M4e (done) |
| `input_method` | `IInputMethodManager.Stub` | dalvikvm (same-process) | `WestlakeInputMethodManagerService` Java | M3++ local elision | M4e (done) |
| `power` | `IPowerManager.Stub` | dalvikvm (same-process) | `WestlakePowerManagerService` Java | M3++ local elision | M4-power (done) |
| **`media.audio_flinger`** | `IAudioFlinger.Stub` (native AOSP IBinder) | **separate process: audio_daemon** | `AudioServiceImpl` C++ → AAudioBackend → `libaaudio.so` | real /dev/vndbinder round-trip | **M5 (pending)** |
| **`SurfaceFlinger`** | `ISurfaceComposer.Stub` (native AOSP IBinder) | **separate process: surface_daemon** | `SurfaceComposerImpl` C++ → DlstPipeBackend → stdout | real /dev/vndbinder round-trip | **M6 (pending)** |
| **(per-track) IAudioTrack** | `IAudioTrack.Stub` | audio_daemon process | `AudioTrackImpl` C++ — returned from `createTrack` | real /dev/vndbinder round-trip | M5 (pending) |
| **(per-conn) ISurfaceComposerClient** | `ISurfaceComposerClient.Stub` | surface_daemon process | `SurfaceComposerClientImpl` C++ — returned from `createConnection` | real /dev/vndbinder round-trip | M6 (pending) |
| **(per-surface) IGraphicBufferProducer** | `IGraphicBufferProducer.Stub` | surface_daemon process | `GraphicBufferProducerImpl` C++ — returned from `createSurface` | real /dev/vndbinder round-trip | M6 (pending) |
| `media.audio_policy` | `IAudioPolicyService.Stub` | (optional; not built yet) | optional Tier-2 in audio_daemon | real binder | M5 (optional; see plan §2.3) |
| `audio` (Java side) | `IAudioService.Stub` | (Tier-2 Java service) | not yet implemented; AudioManager.requestAudioFocus path | M3++ local | (none yet — discover during M7 if noice needs it) |
| `media.player`, `media.metrics`, `media.camera`, `dropbox`, `power_manager` (note: NOT same as `power`), `appops`, `accessibility`, `connectivity`, ... | various | (not built) | fail-loud via `getService(name) → null` (ServiceManager.getService returns null; framework code defensive there) | n/a | M14+ as needed |

### 3.2 Critical pipe-wiring detail (M6 → host SurfaceView)

The current Westlake renderer (pre-pivot, but surviving in `WestlakeRenderer` per V2 §5) writes DLST opcodes to **stdout** of the dalvikvm process. The Compose host (`WestlakeVM.kt:1850+`) reads them from the dalvikvm's **stdin** side of the pipe (host APK is the parent process; dalvikvm is its child; host owns the pipe).

In the M7/M8 boot sequence:
- **Pre-M6 design:** dalvikvm's WestlakeRenderer wrote frames directly.
- **Post-M6 design:** M6's `surface_daemon` writes frames; dalvikvm's WestlakeRenderer is bypassed (the view tree → BufferQueue path now feeds M6 instead).

For step 7 of §2.1 (pipe wiring), the cleanest model is:
- **host APK** forks both `surface_daemon` AND `dalvikvm` as children
- `surface_daemon`'s stdout = host APK's pipe-read fd (the same pipe-read fd the host used to read from dalvikvm pre-M6)
- `dalvikvm`'s stdout = `/data/local/tmp/westlake/dalvikvm.log` (no longer the DLST pipe — frames moved to surface_daemon)
- `dalvikvm`'s stdin = `/dev/null`
- Both daemons + dalvikvm `dup2` shared `/dev/vndbinder` open via their `ProcessState::initWithDriver(/dev/vndbinder)` — kernel binder handles fd-table replication

The Compose host's `WestlakeVM.kt` reader code is **unchanged** — it sees DLST opcodes on its read-end, identical magic word (`0x444C5354`), identical opcode set (Path A `RAW_BITMAP` per M6 plan §4.2). The writer identity is invisible.

### 3.3 How `Context.getSystemService` ends up at the right daemon

Concrete example: noice calls `context.getSystemService("audio")`. The chain:

```
1. WestlakeContextImpl.getSystemService("audio")
   shim/java/com/westlake/services/WestlakeContextImpl.java (the V2 substrate one)
   → calls SystemServiceWrapperRegistry.wrap("audio", this)

2. SystemServiceWrapperRegistry.wrap("audio", ctx)
   shim/java/com/westlake/services/SystemServiceWrapperRegistry.java
   → looks up "audio" in dispatch table
   → currently: "audio" is NOT in the dispatch table (it's not one of the 6 M4 services)
   → returns null

3. WestlakeContextImpl gets null back, falls through to its fallback
   → today: returns null (which framework code handles defensively in most paths)
   → for M7 noice: framework's AudioManager.<clinit> calls
     AudioSystem.<clinit> which references libaudioclient.so; class init succeeds
     because M5-PRE landed the 105 AudioSystem JNI stubs
   → AudioManager construction itself: if noice does `new AudioManager(ctx)` or
     ctx.getSystemService(Context.AUDIO_SERVICE), AudioManager needs an
     IAudioService binder. That's Tier-2-Java-service territory (see §3.1 row
     "audio" Java side) — NOT M5.

4. When noice actually plays a sound:
   noice calls MediaPlayer or AudioTrack.write(...)
   → MediaPlayer JNI → libmedia.so → libaudioclient.so → BpAudioFlinger
   → BpAudioFlinger looks up ServiceManager.getService("media.audio_flinger")
   → returns BpBinder pointing at M5 daemon
   → BpBinder.transact(CREATE_TRACK, parcel)
   → /dev/vndbinder ioctl → kernel routes to audio_daemon
   → AudioServiceImpl::onTransact(CREATE_TRACK) → allocates cblk + AAudio stream
   → returns BnAudioTrack proxy + IMemory cblk back to dalvikvm
   → AudioTrack.write loop writes into cblk shared memory
   → M5's per-track reader thread pumps cblk → AAudioBackend::write → libaaudio.so
   → AAudio buffer → audioserver → speaker
```

Same shape for SurfaceFlinger: `ViewRootImpl.relayoutWindow` → `ISurfaceComposer.createConnection` → ... → M6 daemon's `SurfaceComposerImpl::onTransact`. The path is fully invisible to the app; it's identical to a real-Android boot.

### 3.4 What "audio"/Java-side service really requires (M7 may surface this)

Per `M5_AUDIO_DAEMON_PLAN.md` §1 "What M5 does NOT deliver" — the **Java** IAudioService (handle name `"audio"`) is NOT part of M5; M5 is strictly the **native** `media.audio_flinger`. If noice's `AudioManager.requestAudioFocus(...)` path hits, framework's `AudioManager` will call `ServiceManager.getService("audio")` → null → defensive code → focus-grant returns false (most apps tolerate this).

If noice or McD **strictly require** audio focus to play (unusual; most consumer apps degrade gracefully), the discovery during M7 will surface that as an NPE or "request failed" path. The follow-up CR adds a minimal Java `WestlakeAudioService extends IAudioService.Stub` (~5-10 methods, all return safe defaults). Estimated 0.5 person-day if needed. This is **deferred discovery work**, not part of the M7 baseline.

### 3.5 What `getDisplay()` returns and where it gets density from

Framework's `DisplayManagerGlobal.getDisplay(0)` calls our M4d `WestlakeDisplayManagerService.getDisplayInfo(displayId=0)` (binder). M4d returns canned info: 1080×2280, 60 Hz, density 480, defaultDisplay flag set. ViewRootImpl uses this for layout sizing. Our M6 surface_daemon also returns 1080×2280 from `ISurfaceComposer.GET_DISPLAY_INFO` (M6 plan §2.2 row 3 — Tier-1). The two services must return **the same physical dimensions** or layout will mismatch the buffer dimensions; pre-M7 sanity check: ensure both daemons load their display constants from the same source (recommend: a shared header `aosp-shim-config/display.h` or env var; M5/M6/M4d all read it).

---

## §4. Cross-daemon coordination (Phase 1: NONE; document explicitly)

This section exists to **prevent** an integrator from manufacturing coupling that isn't needed. Phase 1 audio + surface daemons run in isolation; they share no protocol, no IPC, no shared memory.

### 4.1 Phase 1 (this milestone group)

**No coordination. Period.**

- M5 and M6 do NOT communicate with each other.
- M5 has no idea what surface a sound is associated with.
- M6 has no idea what audio stream a frame is associated with.
- Vsync from M6 does NOT clock M5 (audio runs at its own AAudio cadence).
- Audio underrun in M5 does NOT pause M6 frame production.
- A/V sync (for video playback) is **not addressed in Phase 1.** noice's sound presets are audio-only; McD's home screen is video-less.

If you find yourself adding a callback from M6 into M5 (or vice versa), stop. That's drift. The integration test (§5) does NOT need A/V sync to pass.

### 4.2 What apps that need A/V sync would require (deferred)

If a future app (M14 / M17 benchmark suite — TikTok, YouTube) needs A/V sync, the wire-up would be:

- `MediaCodec` audio frame timestamp + `MediaCodec` video frame timestamp share the same epoch (`AudioTrack.getTimestamp().nanoTime` and `Choreographer.FrameCallback` timestamps).
- M5 returns true AAudio-reported timestamps via `IAudioFlinger.GET_RENDER_POSITION` (Tier-1 — M5 plan §2.2 code 33).
- M6 emits vsync events with monotonic-ns timestamps via `IDisplayEventConnection` (M6 plan §2.5).
- Framework MediaPlayer / ExoPlayer aligns video frame presentation to audio timestamps using these signals.
- **Both timestamp sources are already on the M5/M6 critical path — no new cross-daemon protocol is needed.**

So even if A/V sync becomes required later, the architecture supports it without changing M5↔M6 coupling. Phase 1 M7/M8 ships with zero cross-daemon work.

### 4.3 Shared display config (the one common bit)

The single non-coupling shared concern: **both M6 and M4d (display service) must report identical display info** (1080×2280 / 60 Hz / density 480). Implementation choices:

- **Recommended:** hardcode the same constants in M6 source (`SurfaceComposerImpl.cpp::getDisplayInfo`) and M4d Java source (`WestlakeDisplayManagerService.getDisplayInfo`). Both files reference the same values via copy.
- **Alternative:** runtime config file `/data/local/tmp/westlake/display.conf` parsed by both. Adds I/O + parse complexity for one config struct — overkill for Phase 1.

Recommendation: hardcode + add a CR-tracked checklist item to keep them in sync if Phase 2 changes the display dimensions.

---

## §5. Test fixtures (M7 + M8 acceptance tests)

### 5.1 M7 — noice end-to-end

#### Pre-conditions

- All of Phase 1's prior milestones green (V2 substrate + M1-M4 + M5 + M6 daemons running on cfb7c9e3).
- noice.apk pushed to `/data/local/tmp/westlake/noice.apk`.
- `aosp-shim.dex` + `framework.jar` + `ext.jar` + dalvikvm runtime in standard layout per `noice-discover.sh` `DIR` layout.

#### Launch

```bash
bash /home/dspfac/android-to-openharmony-migration/scripts/run-noice-westlake.sh
# (new script in M7 deliverables; supersedes noice-discover.sh)
```

The script:
1. Executes steps 1-12 of §2 verbatim.
2. Polls for first-frame visible (step 12 hard gate) within 60s.
3. Runs a fixed 10-minute interactive session via `adb shell input tap` coordinates:
   - tap Library tab (x=270, y=2100)
   - tap "Rain" preset (x=540, y=900)
   - wait 5s — verify audio audible via `dumpsys media.audio_flinger` (must show our M5 owning the active output stream)
   - tap pause (x=540, y=2000)
   - wait 2s — verify audio silent
   - tap Sleep Timer tab (x=810, y=2100)
   - tap "30 minutes" preset (x=540, y=1200)
   - wait 5s
   - tap back button (x=80, y=200)
4. Captures screenshot via `adb exec-out screencap -p > artifacts/noice-westlake-after.png`
5. Captures logcat to `artifacts/noice-westlake.log`.

#### Acceptance signals (must ALL hold)

| Signal | Source | Pass threshold |
|---|---|---|
| noice's MainActivity reaches `onCreate` completion | aosp-shim.dex log "WestlakeActivity.onCreate EXIT" + the app's MainActivity full body executed | log marker present |
| First fragment visible (HomeFragment inflates) | aosp-shim.dex log "FragmentManager.addFragmentInternal name=HomeFragment status=ATTACHED" | log marker present |
| User can tap a sound preset → audio plays via M5 | audio_daemon log "AudioTrackImpl::start sessionId=N output=primary" + speaker output | audible + log marker |
| `dumpsys media.audio_flinger` shows OUR daemon owning the active stream | adb shell `dumpsys media.audio_flinger` post-tap | output thread shows process=westlake_audio_daemon (not /system/bin/audioserver) for the duration of the tap |
| SurfaceView shows noice's UI (frames flow through M6) | surface_daemon log "queueBuffer slot=N seq=NNN" + visual non-black SurfaceView in host APK + screenshot diff vs native run within ±10% pixel difference | log marker + visual diff PASS |
| Zero crashes | logcat: no `SIGSEGV`, `SIGBUS`, `Process crashed`, `FATAL EXCEPTION` in 10 minutes | grep returns nothing |
| Zero fail-loud UOE | logcat: no `WestlakeServiceMethodMissing.fail` thrown from any service | grep returns nothing (or if some fire, document them as known Tier-2 unimplemented + log no-op) |

#### Negative gates

- Crash within the 10-minute session = M7 FAIL.
- Frame production stops for >5s during interaction = M7 FAIL.
- Audio truncated mid-stream (xrun > 100ms) = M7 FAIL.
- Any fail-loud UOE that hadn't been silently Tier-2'd in prior CRs = M7 FAIL (it surfaces the next missing service method; address in follow-up CR before M7 PASS declared).

### 5.2 M8 — McDonald's regression (end-to-end)

#### Pre-conditions

Identical to M7 §5.1 but `mcd.apk` (`com.mcdonalds.app`) instead of noice.apk.

#### Launch

```bash
bash /home/dspfac/android-to-openharmony-migration/scripts/run-mcd-westlake.sh
```

#### Acceptance signals (must ALL hold)

| Signal | Source | Pass threshold |
|---|---|---|
| McD `McDMarketApplication.onCreate` completes | aosp-shim.dex log marker | present |
| SplashActivity reaches `performResume` | aosp-shim.dex log marker (existing `MCD_APP_PROOF` marker from C2-era code) | present |
| Branded splash renders (the McDonald's "M" logo) | surface_daemon log "queueBuffer" + screenshot shows non-black + visual match | present |
| Dashboard sections inflate (HERO, MENU, PROMOTION, POPULAR) | aosp-shim.dex log "FragmentManager.addFragmentInternal name=Dashboard*" × 4 | present |
| Network calls succeed (McD's HTTP) | logcat shows `Retrofit / OkHttp` 200 responses; bridge HTTP proxy log shows menu JSON fetch + image fetch | present |
| `MCD_APP_PROOF` markers emitted (the pre-pivot check still passes) | existing `check-real-mcd-proof.sh` style grep | present |
| Zero crashes / fail-loud UOEs | same as M7 | clean |

#### Notes on McD-specific concerns

- **C2-era per-app constants**: pre-CR14 there were McD-specific branches in `WestlakeLauncher.mainImpl` (S15 / S16). CR14+CR16 removed S15+S16; the remaining ones are deferred per `feedback_no_per_app_hacks.md`. **None of these are part of M8** — if McD needs them post-V2, V2's substrate design failed and an architect cycle is required first.
- **McD's HTTP bridge proxy** (`scripts/westlake-dev-http-proxy.py`) is a separate dev tool, predates the pivot. Still active for M8 — McD makes real HTTPS calls to https://app.mcdonalds.com/... which the proxy mediates (development-only certificate handling). Document but don't change.
- **McD has more layers/fragments** than noice (it's a more complex app). Surface frame rate may dip — Phase 1 acceptance is **correctness, not 60 FPS** (per M6 plan §4.2 + §8 risk #7). Document the FPS measurement but don't gate on it.

### 5.3 Shared infrastructure (M7 + M8)

Both tests share:

- `scripts/run-*-westlake.sh` boot scripts (each ~150-200 LOC, parameterized identically — much of `run-noice-phone-gate.sh` is the right starting point).
- Artifact bundle layout `artifacts/{noice,mcd}-westlake/{logcat,screenshot,daemon-logs,dumpsys}.{log,png}`.
- A single regression entry in `scripts/binder-pivot-regression.sh` "Section: M7+M8 e2e" running both back-to-back.
- A shared dumpsys assertion: between M7 and M8 runs, run `dumpsys media.audio_flinger` and verify NO leftover output threads from a prior run (i.e. M5 daemon clean-shutdown is correct).

### 5.4 What M7/M8 fixture does NOT cover

- Pixel-perfect rendering parity with native run (M3+ work; out of scope).
- Performance/FPS benchmarks (Phase 3, M17).
- Long-running soak (>10 minutes; sample memory leak / file-descriptor leak; M17 territory).
- App-to-app handoff (intent flinging between apps; Phase 2 work).
- Multi-window / picture-in-picture (Phase 3+).
- AccessibilityService callbacks (Phase 3+).

---

## §6. Effort estimates

### 6.1 M7 (noice end-to-end test + fixture)

| Sub-task | Person-days |
|---|---|
| Adapt `noice-discover.sh` → `run-noice-westlake.sh`: add steps 4 + 5 (spawn M5, M6 daemons), add step 7 pipe-wiring, extend step 12 acceptance polling | 0.5 |
| Implement 10-minute interaction script (tap profile, screenshot capture, log gates) | 0.5 |
| Run noice live: capture first-failure cycle (probably a missed Tier-2 method in M5/M6 or a per-app-flavor M4 surface), file targeted CR for each gap | 0.5-1.0 |
| Verify audio "Rain" preset audible end-to-end (dumpsys check) | 0.0 (folded into above) |
| Verify SurfaceView shows real frames (visual + log check) | 0.0 (folded into above) |
| Declare M7 PASS; update `PHASE_1_STATUS.md` | 0.0 (folded) |

**Total M7: 1.5-2.0 person-days** (assuming M5/M6 are solid; if M5/M6 surface bugs during the first noice live run, add 0.5-1.0 day per bug — typical pattern: discovery cycle, fix in the responsible daemon, re-run). **Upper bound: 2 days.**

### 6.2 M8 (McDonald's regression test + fixture)

| Sub-task | Person-days |
|---|---|
| Adapt `run-noice-westlake.sh` → `run-mcd-westlake.sh` (mostly env-var differences; package, APK, tap coordinates) | 0.25 |
| Run McD live: capture first-failure cycle for McD-specific paths (Hilt module differences, Retrofit configuration, image loading) | 0.5-1.0 |
| Verify dashboard sections inflate (4 fragments) | 0.0 (folded) |
| Verify HTTP bridge still serves McD traffic correctly | 0.25 |
| Declare M8 PASS; update `PHASE_1_STATUS.md` | 0.0 (folded) |

**Total M8: 1.0-1.5 person-days.** McD's surface is broader than noice but most of the broadening was already absorbed by V1 work; V2 generalized away the per-app branches. **Upper bound: 1.5 days.**

### 6.3 Combined M7 + M8

**Best case: 2.5 days** (M5/M6 are perfect; both apps hit only minor Tier-2 gaps that are 30-min fixes).
**Expected: 3.5 days** (each app surfaces 1-2 missing service methods or a daemon bug; standard discovery iteration cycle).
**Worst case: 4 days** (M5/M6 hit a substantive bug — e.g. cblk ring-buffer corruption on noice's mid-tap audio reroute; M6 frame-pacing breaks on McD's dense layouts — but bounded because the architecture is right; only Tier-1/2 method gaps remain).

`BINDER_PIVOT_MILESTONES.md` §M7 estimated 2-3 days; §M8 estimated 0.5-3 days. CR38's refined estimate: **M7+M8 total = 2-4 calendar days for a single agent.** The lower bound assumes M5/M6 land robust; the upper bound builds in standard discovery iteration.

### 6.4 Per-app Tier-1 method discovery iteration (the unknown unknown)

Each app likely hits 1-3 service methods in M4 or M5 or M6 that aren't yet implemented (i.e. fail-loud per CR2). The pattern from M4-PRE / M4 work: discovery surfaces a method; we add 5-50 LOC to the responsible service; re-run; advance to the next issue. Each iteration = **0.5-1 day**. For M7 likely 2-3 iterations (= 1-2 days bonus); for M8 likely 1-2 iterations (= 0.5-1 day bonus). These are **inside** the §6.3 estimates' margin. The "expected 3.5 days" line item already absorbs them.

### 6.5 Plumbing changes (see §7 below)

Estimated 0.5-1 day for §7 plumbing (V2 substrate doesn't currently route every system service the apps might ask for). Folded into M7 (where most plumbing gaps surface).

---

## §7. Plumbing changes needed in V2 substrate

V2 substrate is "structurally complete" per `PHASE_1_STATUS.md` 14/14 PASS, but the regression tests exercise narrow paths. M7 will likely surface narrow holes that need closing. Audit:

### 7.1 `WestlakeContextImpl.getSystemService` dispatch table

Currently routes: `activity`, `power`, `window`, `display`, `notification`, `input_method`, `layout_inflater`. M7/M8 likely needs additions:

- **`audio`** → return real `android.media.AudioManager` from framework.jar, ctor'd with our `WestlakeContextImpl`. AudioManager wraps a `IAudioService` lookup; the Java IAudioService is NOT M5; it's a TBD Java-side service stub (~5-10 methods returning defaults). Estimate: ~10 LOC in `SystemServiceWrapperRegistry` + ~30 LOC `WestlakeAudioService extends IAudioService.Stub` (similar shape to M4e's NotificationManagerService — small, defaults all the way down).
- **`media_router`** (rare; noice probably doesn't use; McD might): same shape as `audio` — Java-side stub with safe defaults. Estimate: ~10-20 LOC if needed.
- **`vibrator`** (haptic feedback on tap): may be hit by noice ripple effects. Return a no-op `Vibrator`. Estimate: ~5 LOC.
- **`accessibility`** (talkback queries): defensive null is usually fine. ~0 LOC unless surfaced.
- **`connectivity`** (network status — McD's "are you online" checks): McD definitely uses. Return a `ConnectivityManager` whose `getActiveNetworkInfo()` reports "connected, wifi". Estimate: ~30 LOC stub Java service.
- **`wifi`**, **`telephony`**: rarely hit by content apps but defensive return-null is fine. ~0 LOC.

**Total V2 extension: ~50-100 LOC across 2-4 new minimal Java stub services + dispatch-table additions.** Each new entry = 5-30 LOC, single file each. Fits well inside M7+M8 budget.

### 7.2 `WestlakeActivityThread` extension for multi-activity intent dispatch

Per BINDER_PIVOT_DESIGN_V2.md §8.4: noice is single-activity, McD is multi-activity (SplashActivity → DashboardActivity transition). The V2 substrate currently handles single-activity boot; multi-activity Intent dispatch needs:

- `WestlakeActivity.startActivity(Intent)` → resolve target component via local PackageManager → call `WestlakeActivityThread.launchActivityByComponent(component)` → orderly tear down current Activity + spin up new one.

This is **the V2 §8.4 HIGH-risk open question.** Estimate **0.5-1 person-day** if McD's SplashActivity → DashboardActivity transition requires it (which it does; SplashActivity finishes itself in onResume and starts DashboardActivity). Folded into M8 budget.

### 7.3 `findViewById` performance with deep view trees (V2 §8.5)

McD dashboard is thousands of views. Build a `HashMap<Integer, View>` cache on `setContentView` and invalidate on view-tree mutations. Estimate: ~30 LOC in `WestlakeActivity.findViewById`. Surfaces during M8 only if profiling shows it's a bottleneck (most likely it isn't; modern view trees do log-N lookups via parent pointers).

### 7.4 AppCompatDelegate compatibility (V2 §8.3)

McD uses AppCompat themes heavily; AppCompatDelegate installs a custom DecorView via Activity.getWindow().setContentView. Our Window stub must support that path. Likely 5-15 LOC adjustments in `WindowImpl.setContentView(View)`. Surfaces during M8.

### 7.5 Theme parent chain (V2 §8.6)

McD probably uses `Theme.AppCompat.Light.DarkActionBar` → parent chain. WestlakeTheme's current impl may not honor multi-level parents. Estimate: ~50-100 LOC to walk parent chain in `WestlakeTheme.obtainStyledAttributes`. Surfaces during M8 visual review (eyeball missing colors / shadows).

### 7.6 Combined plumbing audit

Total V2 plumbing extension estimate: **0.5-1 person-day across §§7.1-7.5**, surfaced and addressed iteratively during M7 (noice surfaces ~30%) and M8 (McD surfaces ~70%, especially §§7.2 + 7.4 + 7.5). **Folded into the §6.3 effort estimate; not additive.**

---

## §8. Open risks

### 8.1 Risk register for M7/M8 integration

| # | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| 1 | **Surface composition coordination — M6 buffer cadence vs DLST writer cadence** | Medium | High | M6 plan §4.2 Path A (per-frame RAW_BITMAP) is sequential blocking-write; ordering is deterministic. Risk: M6's per-layer consumer thread writes faster than host APK's SurfaceView can blit → backpressure overruns the pipe. Mitigation: pipe blocks the writer when full (POSIX pipe semantics); M6's reader thread naturally throttles. Verified via M6 plan §7.4 acceptance gate. |
| 2 | **App's TextureView usage (if any)** — separate buffer path from SurfaceView | Low | Medium | noice doesn't use TextureView; McD doesn't either (verified via decompiling main_activity.xml; both use plain ImageView + RecyclerView). If a future app does, TextureView routes through a *different* IGraphicBufferProducer path (`Surface.surface()` from a SurfaceTexture). M6 plan §2.4 Tier-3 covers it as fail-loud; would need M14+ work. |
| 3 | **Vsync coordination across multiple windows** | Low | Low | Single Activity per process; single DecorView; single root window. Phase 1 doesn't multi-window. Phase 2 may; not M7/M8. |
| 4 | **Touch input routing latency** | Medium | Medium | Touch from phone hardware → host APK SurfaceView.onTouchEvent → host APK pipes touch bytes to dalvikvm → dalvikvm WestlakeMotionEvent → V2 Activity.dispatchTouchEvent → app's onTouchEvent. End-to-end estimate ~10-30ms; acceptable for non-game UX. Existing pre-pivot Westlake infrastructure handles this (predates pivot); reuse unchanged. |
| 5 | **M5 / M6 daemon clean shutdown between M7 and M8 runs** | Medium | High | Daemons must exit on SIGTERM cleanly: release AAudio stream, close DLST pipe, unregister from servicemanager. Otherwise the next run finds stale `media.audio_flinger` / `SurfaceFlinger` entries (or worse, AAudio refuses new openStream because the prior one didn't release). Mitigation: M5/M6 plan-Step-9 each include SIGTERM handlers; verify in pre-M7 spike. **Add to §5.3 fixture infrastructure: between-run check that prior daemons exited and no zombie processes remain.** |
| 6 | **First-frame latency exceeds 60s gate** | Low | Medium | Cold-start: ART boot (~5s on cfb7c9e3) + framework.jar class init (~3s) + Application.onCreate (~5s; Hilt DI) + MainActivity.onCreate (~2s) + first relayout + first BufferQueue → first M6 frame. Total expected ~15-20s. 60s gate is generous. If pre-V2 work surfaces issues, the 60s gate can be raised. |
| 7 | **noice plays audio at 0 volume** (volume cache returns 0 due to Tier-2 method incorrectly returning 0 instead of 1.0) | Medium | High | M5 plan §2.2 marks `MASTER_VOLUME`, `MASTER_MUTE` etc. as Tier-2 "no-op return cached value" — but the cache initializer must default to 1.0 (volume) / 0 (mute), not 0 (volume) / 1 (mute). One-line check in `AudioServiceImpl::AudioServiceImpl()` ctor. Add to M5-Step2 review checklist. |
| 8 | **Multi-activity intent dispatch in McD blocks M8** (V2 §8.4 HIGH risk) | High | High | Per §7.2 above, this is the V2 substrate's biggest known gap. The 0.5-1 day estimate in §7.2 absorbs it. If it's larger (SplashActivity has deeper system_server-expected coupling than visible), M8 timeline extends. Track as M8's specific risk. |
| 9 | **Hilt component construction fails post-V2 due to subtle Context API gap** | Medium | High | V2 §8.2 documents the Hilt path; expected LOW risk; but Hilt's `ApplicationContextModule.context` injection has many downstream users (every `@Inject Context` field). If our `WestlakeApplication.getApplicationContext()` doesn't behave identically to framework's (e.g., returns wrong base context), Hilt component-construction NPE cascades. Mitigation: M7's first run is the validation; if it fails, audit `WestlakeApplication` Context delegation. |
| 10 | **fail-loud surfaces a Tier-1 method that the plan §2.2 / §2.4 missed** | Medium | Medium | M5 plan §2.2 + §11 risk #1 explicitly call this out. CR37 reduced M5's risk by enumerating every transaction. M6's analogous CR35 isn't done yet — that's the equivalent risk for the surface side. M6-Step2 is currently gated on CR35; can't begin until CR35 lands. **M7/M8 cannot start until CR35 + M6-Step2 done.** |

### 8.2 Top 2 risks ranked by likelihood × impact

1. **#8: Multi-activity intent dispatch (McD SplashActivity → DashboardActivity).** Highest probability of blocking M8. Mitigation budgeted in §7.2.
2. **#5: Daemon clean shutdown between runs.** Practical operational risk; cheap to mitigate (SIGTERM handler ~10 LOC in M5/M6) but easy to forget. Add to M5/M6-Step-9 review checklists.

Honorable mentions: #1 (M6 pipe backpressure) — fundamentally just a system property; either works or it doesn't, and M6 plan already acknowledges it.

### 8.3 Risks that are NOT in scope (deferred)

- Phase 2 OHOS port (M9-M13): the M11/M12 backend swaps are pre-engineered in M5/M6 plans §4.4; no M7/M8 risk.
- Multi-app: not a Phase 1 concern.
- Real GPU rendering: M6's DLST pipe is CPU-only; HWUI sw renderer is forced (M6 plan §8 risk #1 mitigation). Phase 3+ for GPU path.

---

## §9. Comparison with M7/M8 entries in `BINDER_PIVOT_MILESTONES.md`

This section reconciles CR38's refined plan with the canonical V1 entries.

### 9.1 What V1's §M7 said

`BINDER_PIVOT_MILESTONES.md` §M7:
- **Goal:** "noice runs in the binder-sandbox on the Pixel, renders main UI, plays audio, no visible defects vs. native run."
- **Scope:** "integration testing only. No new code beyond stitching previous milestones."
- **Acceptance:** "10-minute interactive session: open noice, navigate through Library/Presets/Sleep Timer tabs, tap a sound to play it, pause it, navigate back. No crashes."
- **Side-by-side screenshot comparison** with noice running natively on the same phone. Layout matches within ±5px on key elements.
- **logcat shape comparison:** same major lifecycle events.
- **Estimated effort:** 2-3 days.
- **Test plan:** `adb shell '/data/local/tmp/westlake/bin/sandbox-boot.sh com.github.ashutoshgngwr.noice'`.

### 9.2 What CR38 refines

1. **Target device update**: V1 said "Pixel"; current state runs on **OnePlus 6 cfb7c9e3** (`memory/MEMORY.md` "Phone connection"). CR38 doc reflects this.
2. **Process model fleshed out**: V1's "stitching" was vague. CR38 §1 + §2 provide concrete 12-step boot + 5-process tree.
3. **Acceptance bar tightened**: V1's "no visible defects vs. native" is subjective. CR38 §5.1 enumerates 7 specific signals (log markers + dumpsys checks + visual diff).
4. **Negative gates added**: V1 didn't explicitly say "audio xrun > 100ms = FAIL"; CR38 does.
5. **Effort estimate broadened**: V1 said 2-3 days; CR38 §6.3 says 2-4 days with explicit best/expected/worst breakdown — accounts for plumbing-discovery iterations from V2 substrate gaps.
6. **Plumbing audit added**: V1 said "no new code beyond stitching"; CR38 §7 acknowledges V2 substrate likely has 5-10 small gaps (`audio`, `connectivity`, multi-activity intent, etc.) totaling 50-100 LOC.
7. **Test fixture script renamed**: V1's `sandbox-boot.sh com.github.ashutoshgngwr.noice` → CR38's `run-noice-westlake.sh` (deliberate generalization; the script is no longer "the sandbox" — it's the host APK orchestrator).

### 9.3 What V1's §M8 said

- **Goal:** "McDonald's still launches and reaches `SplashActivity.performResume` post-pivot."
- **Scope:** "run existing McD test (`./scripts/check-real-mcd-proof.sh`)."
- **Estimated effort:** 0.5 day (assuming green) or 1-3 days (if regressions surface).

### 9.4 What CR38 refines for M8

1. **Acceptance bar expanded**: V1 stopped at "SplashActivity.performResume"; CR38 §5.2 extends to "dashboard sections inflate" + "Network calls succeed" + "MCD_APP_PROOF markers emitted". V1's bar is the lower bound; CR38's is the upper bound. Setting the upper bound as M8 PASS is the right move — the lower bound has been passing intermittently for months and isn't an end-to-end assertion of the binder-substituted McD path.
2. **V2 multi-activity dispatch surfaced explicitly**: V1 didn't call this out; CR38 §7.2 + §8 risk #8 flag it as the M8-specific risk.
3. **Effort estimate narrowed**: V1's "0.5 or 1-3 days" → CR38's "1.0-1.5 days" with concrete sub-task breakdown.

### 9.5 Net assessment of V1 plan vs CR38 refinement

**V1's M7/M8 plan remains valid in scope and intent.** Both milestones are still "integration of previously-built pieces", still gated on M5/M6 + V2 substrate. CR38 doesn't extend M7/M8 outward — it just zooms in.

**V1's effort numbers (2-3 + 0.5-3 = 2.5-6 days) align with CR38 (2-4 days total).** CR38's number is at the lower end of V1's range because CR38 enumerates the gating risks (the §8 risks #5 + #8 + #10) so they're visible upfront and budgeted; V1 left these implicit, which inflated the upper-bound estimate.

**No restructuring of the milestones doc required.** The recommended update is a one-line annotation per row, pointing here:

```
§M7: ... estimated effort: 2-3 days  →  ... estimated effort: 2-3 days  (CR38 refines to 1.5-2.0)
§M8: ... estimated effort: 0.5-3 days  →  ... estimated effort: 0.5-3 days  (CR38 refines to 1.0-1.5)
```

These small annotations are the second-deliverable of this CR. (See §10 below.)

### 9.6 Renumbering / new milestone tracking

CR38 does NOT create new milestones. M7 and M8 stay as canonical V1 entries; CR38 is **scoping for them**, not a new milestone. PHASE_1_STATUS.md gets a one-row CR38 entry under §1.4 (cross-cutting):

```
| CR38 — M7/M8 final integration pre-scoping | done (read-only research) | 2026-05-13 | Architect (this work) | New doc `docs/engine/CR38_M7_M8_INTEGRATION_SCOPING.md` (~XXX LOC) covers post-V2 + post-M5/M6 integration: process tree, 12-step boot sequence, daemon wiring table, no-cross-daemon-coupling decision, M7+M8 acceptance fixtures, 2-4 day combined effort estimate, top-2 risks (multi-activity intent dispatch for McD; daemon clean shutdown). V1 §M7/§M8 plan upheld; CR38 zooms in (no scope expansion). | `docs/engine/CR38_M7_M8_INTEGRATION_SCOPING.md` (NEW); `docs/engine/BINDER_PIVOT_MILESTONES.md` (M7+M8 row annotation); `docs/engine/PHASE_1_STATUS.md` (this row) |
```

---

## §10. Critical files for the M7/M8 Integrator agent to read first

In recommended order:

1. **This doc** (`CR38_M7_M8_INTEGRATION_SCOPING.md`) — full integration architecture. 45 minutes.
2. `docs/engine/BINDER_PIVOT_MILESTONES.md` §M7 / §M8 — canonical V1 acceptance bars (small + still in force). 10 minutes.
3. `docs/engine/M5_AUDIO_DAEMON_PLAN.md` §6 (bringup sequence) + §7 (acceptance tests) — what M5 will look like when it lands. 20 minutes.
4. `docs/engine/M6_SURFACE_DAEMON_PLAN.md` §6 (bringup sequence) + §7 (acceptance tests) — same for M6. 20 minutes.
5. `docs/engine/BINDER_PIVOT_DESIGN_V2.md` §8 (open architectural questions, especially §8.4 multi-activity dispatch). 15 minutes.
6. `docs/engine/PHASE_1_STATUS.md` §3 (what works) + §4 (what doesn't yet). 15 minutes.
7. `aosp-libbinder-port/test/noice-discover.sh` — the predecessor harness M7 evolves from. 10 minutes (read the comment header).
8. `scripts/run-noice-phone-gate.sh` — the predecessor full-Westlake script noice was previously run through (pre-V2; useful for tap profile + artifact bundle structure). 15 minutes.

**Total CR38 onboarding for M7/M8 Integrator:** ~2.5 hours of reading before touching code.

---

## §11. Critical insights (anti-drift compliance)

1. **No new architectural decisions** required at M7/M8. Every architectural choice (per-app vs generic; binder vs in-process; same-process elision; V2 cut at Activity.attach; M5/M6 in separate processes) is locked in by prior milestones. M7/M8 is **stitching only**.
2. **No cross-daemon coupling** (§4). Resist the temptation to wire M5 ↔ M6. Phase 1 doesn't need it; Phase 2 may add it elsewhere; here, it would be drift.
3. **No per-app hacks** (§5.2). Both M7 and M8 fixtures use the same boot sequence (§2) and same routing table (§3); the only per-app surface is the tap profile (which is test infrastructure, not Westlake-shim). If M8 surfaces a need for a per-app Westlake-shim path, that's a V2 substrate gap to address generically — not a McD branch.
4. **No additive shimming** (V2 §10.1). If M7 surfaces a fail-loud on a Tier-2 method (e.g. M5's `AUDIO_PORT_LIST`), the response is to implement that method in M5 (5-20 LOC, generic), not to plant a field on a synthetic AudioPort.
5. **Fail-loud is a feature**, not a bug (CR2). Each fail-loud during M7/M8 is **discovery** — it identifies exactly the next service method to implement. Treat fail-loud as a working signal, not a regression.

### 11.1 What success looks like (concrete)

At M7 PASS:
- 10-minute interactive noice session captured (logcat + screenshot + dumpsys + 2 daemon logs).
- Tap-to-audible-sound latency < 200ms (subjective; not measured rigorously).
- Visual rendering of noice's UI on the OnePlus 6 phone screen, driven by real binder traffic through our M5+M6 daemons.
- The audio output thread shown by `dumpsys media.audio_flinger` is our `westlake_audio_daemon` PID, not Android's stock `audioserver`.
- aosp-shim.dex shows zero fail-loud UOEs in the session's full 10 minutes.

At M8 PASS:
- Same shape, but for McD.
- `MCD_APP_PROOF` markers present (pre-pivot acceptance proof, still firing).
- 4 dashboard fragments inflated.
- HTTP traffic via dev proxy completes.

**Phase 1 binder pivot then transitions to "complete" status, gating Phase 2 (OHOS port; M9-M13) per `BINDER_PIVOT_MILESTONES.md` §0.**

### 11.2 What failure looks like (and what to do)

- **Phase 1 fails at M7/M8 with a Tier-1 method gap**: address it in M5/M6 directly; iterate. CR2 fail-loud points at the exact method.
- **Phase 1 fails with a V2 substrate gap** (e.g. AppCompatDelegate compatibility): address it in the V2 Java substrate per §7; do NOT add app-specific reflection.
- **Phase 1 fails with a kernel/runtime gap** (rare; CR33/CR34 spikes cleared this risk): escalate; may require a sysprop or env-var tweak; if unsolvable, document and pivot M11/M12 to an alternative backend.
- **Phase 1 surfaces drift (someone adds a per-app branch or a reflective field plant)**: cite `feedback_no_per_app_hacks.md` + V2 §10. Reject and rework.

---

## §12. Decision log

| Date | Decision | Rationale | Reversibility |
|---|---|---|---|
| 2026-05-13 | CR38 = pre-scoping for M7/M8; not a new milestone | M7/M8 already canonical in V1 milestones doc; CR38 fills in zoom-in detail | Reversible: this doc can be archived if M7/M8 land smooth and no one references it |
| 2026-05-13 | Phase 1 has NO cross-daemon coordination | M5 + M6 are independent; A/V sync is Phase 2+/M11+ territory | Reversible if Phase 2 finds it necessary; for now defer |
| 2026-05-13 | M7+M8 = 2-4 calendar days combined | V1 estimate range (2.5-6 days) accommodates this; CR38 narrows to lower end given V2 substrate stability + CR33/34 spike PASS | Update if M7's first live run surfaces unexpected scope |
| 2026-05-13 | New scripts `run-noice-westlake.sh` + `run-mcd-westlake.sh` deliver M7/M8 | Generalizes from current `noice-discover.sh` (Phase 0) and `run-noice-phone-gate.sh` (pre-pivot) to the post-V2 + post-M5/M6 architecture | Reversible: the scripts are test infrastructure, easy to iterate |
| 2026-05-13 | Single regression entry runs M7 + M8 back-to-back | Maintains the `binder-pivot-regression.sh --full` workflow + makes Phase-1-complete a single command | Same script + add 2 sub-sections; simple |
| 2026-05-13 | M5/M6 daemons launched + owned by the host APK process, not by sandbox-boot.sh | Aligns with the existing pre-pivot model where host APK forks dalvikvm; M5/M6 are additional children of host APK | Reversible: standalone-process model is also possible (separate shell-launched daemons) but adds another orchestration burden |

---

## §13. Final summary

CR38 is a 2-3 hour read-only architect pre-flight that pre-answers every "how do M7/M8 actually work?" question before either milestone begins. Key deliverables:

- **§1**: process tree on cfb7c9e3 (5 processes; one uid)
- **§2**: 12-step boot sequence with hard-fail gates at steps 3/4/5/6/8/11/12
- **§3**: routing table for every system service the apps might query
- **§4**: explicit "no cross-daemon coordination needed in Phase 1" finding
- **§5**: M7 + M8 acceptance fixtures (10-minute interactive session each, 7 acceptance signals)
- **§6**: 2-4 day combined effort estimate with best/expected/worst breakdown
- **§7**: V2 substrate plumbing audit (50-100 LOC of likely small extensions surfaced by M7/M8 discovery)
- **§8**: 10-row risk register with top-2 ranked (multi-activity intent dispatch + daemon clean shutdown)
- **§9**: reconciliation with V1 §M7/§M8 — no scope expansion; refinement only

The integration path is concrete, bounded, and architecturally drift-free. M5 + M6 + V2 substrate already individually validated; M7/M8 stitches them.

**End of CR38_M7_M8_INTEGRATION_SCOPING.md.**
