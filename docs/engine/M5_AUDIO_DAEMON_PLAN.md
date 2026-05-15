# M5_AUDIO_DAEMON_PLAN.md — `westlake-audio-daemon` scoping

**Status:** scoping (M5 prep)
**Author:** Architect agent (2026-05-12)
**Companion to:** `docs/engine/BINDER_PIVOT_DESIGN.md` §3.3/§3.6, `docs/engine/BINDER_PIVOT_MILESTONES.md` §M5
**Predecessor work:** M1 (`libbinder.so` musl/bionic), M2 (`servicemanager`), M3 (dalvikvm wired through libbinder), M5-PRE (105 `AudioSystem` JNI stubs at `art-latest/stubs/audiosystem_jni_stub.cc`, ~770 LOC)
**Reference pattern:** `aosp-libbinder-port/BUILD_PLAN.md` (M1 scoping doc, format mirrored here)

This document specifies the build scaffold and acceptance criteria for the Westlake-owned audio daemon. The Builder agent who executes M5 should treat this as the work breakdown.

---

## 1. Scope summary

**What M5 delivers:** a single native binary `westlake-audio-daemon` that:

1. Launches as a separate process during boot, after `servicemanager`.
2. Implements the AOSP `IAudioFlinger` (and optionally `IAudioPolicyService`) Binder service contract.
3. Registers itself with our M2 servicemanager under the canonical AOSP names `"media.audio_flinger"` (and `"media.audio_policy"` if implemented).
4. Accepts the transactions that AOSP's `libaudioclient.so` / framework `AudioTrack` / `AudioRecord` issue via real Binder, and bridges them to a platform-specific audio backend behind a single C++ interface.
5. **Phase 1 backend (this milestone):** Android **AAudio** NDK (`libaaudio.so`) — Westlake on a real Android phone (OnePlus 6 `cfb7c9e3`) uses AAudio to feed the device's actual speaker. This is the architecturally-equivalent validation environment proposed in `BINDER_PIVOT_DESIGN.md` §4.1.
6. **Phase 2 backend (deferred to M11):** OHOS **AudioRenderer** NDK (`libohaudio.so`) — same daemon, identical Binder surface, swap backend at compile time via `#ifdef OHOS_TARGET` (≈ 500 LOC of OHOS-specific bridge code per §3.6).

**What M5 does NOT deliver:**

- AOSP `audioserver` itself — we are not porting AudioFlinger's ~5K LOC `frameworks/av/services/audioflinger/AudioFlinger.{h,cpp}` engine wholesale. We write a *minimal substitute* that answers the IAudioFlinger AIDL contract by routing tracks straight to the backend's output stream(s). Mixers, effects, AudioPolicy routing tables, audio-port topology, AAudio MMAP path, hardware HAL plumbing — all are stubbed or fail-loud.
- `media.audio_policy` is **optional** for Phase 1; if AOSP framework only ever talks to it for `requestAudioFocus`, we can supply an empty Java-side `IAudioService` (Tier-2 M4 work) and avoid implementing the native service at all. The Builder agent should keep this surface optional and add it only if discovery shows it's needed.
- The Java `IAudioService` (handle name `"audio"`, AIDL `frameworks/base/media/java/android/media/IAudioService.aidl`) is **not** an M5 deliverable — that's a Java-side Tier-2 M4 service. M5 is strictly the **native** daemon backing `media.audio_flinger`.
- Capture (`AudioRecord` / `createRecord`) is **best-effort Phase 1** — noice needs playback, not capture. Implement only the `IAudioFlinger::createRecord` failure path (return BAD_VALUE) until a Tier-1 use case demands real capture.

**Architectural placement:** see `BINDER_PIVOT_DESIGN.md` §3.3 diagram, "westlake-audio-daemon (~15 MB)". M5 is the boundary between *framework-jar AudioTrack → libaudioclient.so → Binder → westlake-audio-daemon* (uniform Westlake-owned plumbing) and *AAudio | AudioRenderer* (platform-specific output).

---

## 2. AIDL surface analysis

### 2.1 AOSP source location

| Artifact | AOSP path (android-11.0.0_r48 local at `/home/dspfac/aosp-android-11`) | Size |
|---|---|---|
| Header `IAudioFlinger.h` | `frameworks/av/media/libaudioclient/include/media/IAudioFlinger.h` | 565 LOC |
| Implementation `IAudioFlinger.cpp` (BpAudioFlinger + BnAudioFlinger::onTransact) | `frameworks/av/media/libaudioclient/IAudioFlinger.cpp` | ~1100 LOC |
| Header `IAudioPolicyService.h` | `frameworks/av/media/libaudioclient/include/media/IAudioPolicyService.h` | (83 virtual methods — out-of-scope unless required) |
| Header `IAudioTrack.h` | `frameworks/av/media/libaudioclient/include/media/IAudioTrack.h` | (13 virtual methods — needed) |
| AudioFlinger reference implementation | `frameworks/av/services/audioflinger/AudioFlinger.{h,cpp}` | ~5K LOC (DO NOT port wholesale) |
| `main_audioserver.cpp` boot stub | `frameworks/av/media/audioserver/main_audioserver.cpp` | 153 LOC (reference for our daemon's main()) |

**Note:** Android 11 uses the *header-defined* IAudioFlinger pattern (no `.aidl` file — the Bp/Bn classes are hand-written in `IAudioFlinger.cpp`). Android 16 may have migrated to AIDL-generated stubs; the Builder agent should verify by checking `frameworks/av/media/libaudioclient/aidl/` against the framework-jar version we ship. **If Android 16 AIDL'ed it, regenerate the stubs from `.aidl`; if not, follow the Android 11 manual-Bn pattern.** Transaction code numbering is observable from `IAudioFlinger.cpp` line 34-95 (Android 11 enum) and is the canonical source of truth for Android 11; for Android 16 verify with reflection on `BnAudioFlinger`'s `TRANSACTION_*` constants (mirroring M4 services' approach in `M4_DISCOVERY.md` §7).

### 2.2 Method count

> **CR37 update (2026-05-13):** Per-transaction signatures, RESERVED slot accounting, Tier-2/Tier-3 boundary case reclassifications, and skeleton onTransact code are now in `docs/engine/CR37_M5_AIDL_DISCOVERY.md`. **The plan estimate below holds within ±2 declarations and is exact on Tier-1 (12 + 5 = 17).** Actual: IAudioFlinger has 60 wire-enum + 1 non-wire (`requestLogMerge`) = 61 declared / 59 dispatchable; IAudioTrack has 13 wire-enum / 12 dispatchable. **No 6.5-day estimate revision needed.** M5-Step2 implementor should read CR37 first.

**Total IAudioFlinger virtual methods: ~61** (counted from `IAudioFlinger.h` Android 11).

Transaction codes from `IAudioFlinger.cpp:34-95` (Android 11, in enum order):

| Index | Code | Method | Tier |
|---|---|---|---|
| 1 | `CREATE_TRACK` | `createTrack(input, output, status)` → `sp<IAudioTrack>` | **Tier-1** |
| 2 | `CREATE_RECORD` | `createRecord(...)` → `sp<media::IAudioRecord>` | Tier-3 (capture; defer) |
| 3 | `SAMPLE_RATE` | `sampleRate(io_handle)` → `uint32_t` | **Tier-1** (queried during track init) |
| 4 | `(RESERVED)` | obsolete CHANNEL_COUNT | — |
| 5 | `FORMAT` | `format(output)` → `audio_format_t` | **Tier-1** |
| 6 | `FRAME_COUNT` | `frameCount(io_handle)` → `size_t` | **Tier-1** |
| 7 | `LATENCY` | `latency(output)` → `uint32_t` | **Tier-1** |
| 8-15 | `SET_MASTER_VOLUME`, `SET_MASTER_MUTE`, `MASTER_VOLUME`, `MASTER_MUTE`, `SET_STREAM_VOLUME`, `SET_STREAM_MUTE`, `STREAM_VOLUME`, `STREAM_MUTE` | volume/mute getters/setters | Tier-2 (return cached / no-op) |
| 16 | `SET_MODE` | `setMode(audio_mode_t)` | Tier-3 (no-op for music) |
| 17-19 | `SET_MIC_MUTE`, `GET_MIC_MUTE`, `SET_RECORD_SILENCED` | mic state | Tier-3 |
| 20-21 | `SET_PARAMETERS`, `GET_PARAMETERS` | string-key-value (HAL-style) | Tier-2 (no-op; return empty) |
| 22 | `REGISTER_CLIENT` | `registerClient(IAudioFlingerClient)` | **Tier-1** (just store the ref; never call back unless death) |
| 23 | `GET_INPUTBUFFERSIZE` | `getInputBufferSize(rate, fmt, ch)` → `size_t` | Tier-3 (capture; defer) |
| 24 | `OPEN_OUTPUT` | `openOutput(module, &output_handle, &config, device, &latencyMs, flags)` | **Tier-1** (first call AudioTrack makes through AudioFlinger) |
| 25-28 | `OPEN_DUPLICATE_OUTPUT`, `CLOSE_OUTPUT`, `SUSPEND_OUTPUT`, `RESTORE_OUTPUT` | output lifecycle | Tier-2 (`CLOSE_OUTPUT` needed; others no-op) |
| 29-30 | `OPEN_INPUT`, `CLOSE_INPUT` | input lifecycle | Tier-3 (capture; defer) |
| 31 | `INVALIDATE_STREAM` | `invalidateStream(stream_type)` | Tier-2 (no-op) |
| 32 | `SET_VOICE_VOLUME` | telephony | Tier-3 |
| 33 | `GET_RENDER_POSITION` | `getRenderPosition(hal, dsp, output)` → frame counters | **Tier-1** (AudioTrack uses this for playback head tracking; without it `AudioTrack.getPlaybackHeadPosition` returns garbage and apps that gate "ready" on it stall) |
| 34 | `GET_INPUT_FRAMES_LOST` | capture xrun counter | Tier-3 |
| 35 | `NEW_AUDIO_UNIQUE_ID` | session/io id factory | **Tier-1** (called by AudioPolicyService too) |
| 36-37 | `ACQUIRE_AUDIO_SESSION_ID`, `RELEASE_AUDIO_SESSION_ID` | session refcount | Tier-2 (no-op, store counts) |
| 38-42 | `QUERY_NUM_EFFECTS`, `QUERY_EFFECT`, `GET_EFFECT_DESCRIPTOR`, `CREATE_EFFECT`, `MOVE_EFFECTS` | audio effects (reverb, EQ) | Tier-3 (return 0 effects) |
| 43 | `LOAD_HW_MODULE` | dlopen of audio.{primary,a2dp,...}.so | Tier-2 (return single canned `audio_module_handle_t` for "primary") |
| 44-45 | `GET_PRIMARY_OUTPUT_SAMPLING_RATE`, `GET_PRIMARY_OUTPUT_FRAME_COUNT` | sysfs-like accessors | **Tier-1** (queried by AudioManager.getProperty) |
| 46 | `SET_LOW_RAM_DEVICE` | system→audioserver hint | Tier-3 |
| 47-52 | `LIST_AUDIO_PORTS`, `GET_AUDIO_PORT`, `CREATE_AUDIO_PATCH`, `RELEASE_AUDIO_PATCH`, `LIST_AUDIO_PATCHES`, `SET_AUDIO_PORT_CONFIG` | port/patch topology | Tier-2 (return single canned port pair: primary output + earpiece) |
| 53 | `GET_AUDIO_HW_SYNC_FOR_SESSION` | A/V sync | Tier-3 |
| 54 | `SYSTEM_READY` | systemserver→audio handshake | Tier-2 (no-op, return OK) |
| 55 | `FRAME_COUNT_HAL` | HAL buffer size | Tier-2 (return canned 256 or 1024) |
| 56 | `GET_MICROPHONES` | mic descriptors | Tier-3 |
| 57-58 | `SET_MASTER_BALANCE`, `GET_MASTER_BALANCE` | stereo balance | Tier-2 |
| 59 | `SET_EFFECT_SUSPENDED` | effect global pause | Tier-3 |
| 60 | `SET_AUDIO_HAL_PIDS` | systemserver→audio | Tier-2 |
| 61 | `requestLogMerge()` (BnAudioFlinger-only side channel) | media.log | Tier-3 (no-op) |

**Tier-1 count: ~12 methods.** These are what the *first* AudioTrack.write() loop exercises:
1. `OPEN_OUTPUT` — open the output stream (called once per AudioManager.STREAM_*)
2. `SAMPLE_RATE`, `FORMAT`, `FRAME_COUNT`, `LATENCY` — query output capabilities
3. `CREATE_TRACK` — allocate a track on that output, get back a Bn track + shared-memory ring buffer (`IMemory cblk`)
4. `GET_RENDER_POSITION` — playback head tracking
5. `NEW_AUDIO_UNIQUE_ID` — id allocation for sessions
6. `REGISTER_CLIENT` — death-notification handshake
7. `GET_PRIMARY_OUTPUT_SAMPLING_RATE`, `GET_PRIMARY_OUTPUT_FRAME_COUNT` — AudioManager.getProperty path
8. `SET_MASTER_VOLUME` / `MASTER_VOLUME` — typical app does query, then trust system value

Plus the **IAudioTrack surface** (returned from CREATE_TRACK; the per-track Binder object). IAudioTrack has ~13 virtual methods (per `IAudioTrack.h`); Tier-1 subset:
- `start()` — kick off playback
- `stop()` — pause
- `pause()`
- `flush()`
- `getTimestamp(AudioTimestamp&)` — playback head
- (the rest: `signal`, `setParameters`, `getCblk` — `getCblk` returns the shared-memory `IMemory` set up at `createTrack` time, no extra work)

**Other methods get fail-loud** per the CR2 pattern already used by M4a/M4b/etc.: each method's body calls a helper `failLoud("media.audio_flinger", "MethodName", code)` that logs the unexpected transaction code with backtrace, then returns `INVALID_OPERATION` (-38). Discovery will surface any Tier-1 misclassification within minutes of running noice. (This mirrors the C++ analog of `shim/java/com/westlake/services/ServiceMethodMissing.fail(...)` — the Builder agent should write a tiny `native/audio-daemon/FailLoud.cpp` exposing the same shape.)

### 2.3 IAudioPolicyService — optional Tier-2

`media.audio_policy` is a separate Binder service. AOSP framework's `AudioSystem` calls it for routing decisions (`getOutputForAttr`, `releaseOutput`, `setStreamVolumeIndex`, `getDevicesForStream`, etc. — ~83 methods total).

**Recommendation:** ship a stub `IAudioPolicyService` ONLY if discovery reveals AOSP framework crashes the first frame on its absence. The Java-side `AudioManager.requestAudioFocus` path goes through Java IAudioService (Tier-2 M4 territory), not directly to IAudioPolicyService. Most apps tolerate `media.audio_policy` returning null in `ServiceManager.getService` (framework code is defensive there).

**If implemented:** ~5 methods Tier-1: `registerClient`, `getOutputForAttr` (returns canned "primary" output), `releaseOutput` (no-op), `getDevicesForStream` (return AUDIO_DEVICE_OUT_SPEAKER), `setStreamVolumeIndex` (no-op).

---

## 3. Build approach

### 3.1 Language and ABI

**C++**, NOT Java. M5 is a separate native process — there is no dalvikvm here, no Java services. The dalvikvm-side code calls into M5 transparently via the libbinder + libaudioclient path.

**ABI:** match dalvikvm's ABI:
- Phase 1 (this milestone): **bionic-arm64** for the OnePlus 6 (`cfb7c9e3`). Same toolchain as `aosp-libbinder-port` bionic targets (Android NDK r25 + bionic sysroot). Output: `aosp-audio-daemon-port/out/bionic/westlake-audio-daemon`.
- Phase 2 (M11): **musl-arm64** for OHOS. Same source, different sysroot. Output: `aosp-audio-daemon-port/out/musl/westlake-audio-daemon`.

(Note: Phase-1 here uses **bionic** rather than musl because the Builder picks AAudio on the OnePlus, and `libaaudio.so` is bionic-only. The libbinder.so we use is M1's bionic variant — already cross-compiled at `aosp-libbinder-port/out/bionic/libbinder.so` per the Makefile section `*-bionic` targets. For Phase 2 OHOS, both libbinder and the daemon swap to musl and the backend swaps to OHOS AudioRenderer, which is OHOS-native.)

### 3.2 Source organization

Either of:

**Option A: new directory `aosp-audio-daemon-port/`**, peer to `aosp-libbinder-port/`. Mirrors the M1 layout — own Makefile, own deps-src, own patches. Pro: clean separation of native daemons. Con: ~50 LOC of Makefile boilerplate to copy.

**Option B: extend `aosp-libbinder-port/Makefile`** with `audio-daemon` and `audio-daemon-bionic` targets. Pro: reuses libbinder/libutils_binder/AIDL build rules already there. Con: Makefile balloons toward unrelated concerns.

**Recommendation: Option A.** The audio daemon has *its own* C++ deps (AOSP `libaudioclient.so` source for Bn classes + audio_utils for ring buffer + at minimum a slim AudioFlinger.cpp substitute). Keeping it next to its peer `aosp-surface-daemon-port/` (M6) — both depending on the M1 libbinder.so artifact at `../aosp-libbinder-port/out/bionic/libbinder.so` — gives a clean three-directory dependency graph.

### 3.3 Files the Builder will create (~5-10 .cpp)

```
aosp-audio-daemon-port/
├── BUILD_PLAN.md                         (this file, copied for the daemon's own scoping)
├── Makefile                              (mirror aosp-libbinder-port/Makefile, ~150 LOC)
├── build.sh                              (driver)
├── aosp-src/                             (subset of frameworks/av/media/libaudioclient)
│   ├── IAudioFlinger.cpp                 (verbatim from AOSP — Bp/Bn stubs)
│   ├── IAudioTrack.cpp
│   ├── IAudioFlingerClient.cpp
│   ├── AudioClient.cpp                   (parcelables — CreateTrackInput, etc.)
│   └── include/media/...                 (headers)
├── deps-src/
│   ├── audio_utils/                      (system/media/audio_utils — ring buffer + format helpers)
│   └── libcutils-extra/                  (any pieces not already in aosp-libbinder-port)
├── patches/
│   ├── 0001-stub-mediautils-timecheck.patch  (drop ServiceUtilities + TimeCheck deps)
│   ├── 0002-stub-hidl-transport-support.patch (avoid HIDL dependency)
│   └── 0003-audio-utils-musl-fixes.patch (if Phase 2 needs them; defer to M11)
├── src/
│   ├── main.cpp                          (~150 LOC; mirrors main_audioserver.cpp:42-153 pattern: ProcessState::self(), startThreadPool, register, joinThreadPool)
│   ├── AudioServiceImpl.cpp              (~600 LOC; BnAudioFlinger subclass; ~12 Tier-1 + ~49 fail-loud overrides)
│   ├── AudioServiceImpl.h
│   ├── AudioTrackImpl.cpp                (~250 LOC; BnAudioTrack subclass; ring-buffer feed → backend write)
│   ├── AudioTrackImpl.h
│   ├── AudioBackend.h                    (~50 LOC; abstract interface — see §4)
│   ├── AAudioBackend.cpp                 (~300 LOC; Phase-1 implementation via libaaudio.so)
│   ├── AAudioBackend.h
│   ├── OhosBackend.cpp                   (~300 LOC; Phase-2 implementation via libohaudio.so; STUB FOR M5, IMPLEMENTED IN M11)
│   ├── OhosBackend.h
│   ├── SharedRingBuffer.cpp              (~200 LOC; the ashmem/memfd-backed cblk; see §5)
│   ├── SharedRingBuffer.h
│   ├── FailLoud.cpp                      (~30 LOC; the ServiceMethodMissing.fail analog)
│   └── FailLoud.h
└── test/
    ├── audio_smoke.cpp                   (~80 LOC standalone — opens a backend, writes 440Hz tone, no Binder)
    ├── audio_binder_smoke.cpp            (~120 LOC — connects as IAudioFlinger client, creates a track, writes tone)
    └── run-audio-tests.sh                (~30 LOC orchestration)
```

**Total estimated daemon source: ~2.5-3 K LOC C++ + ~300 LOC build infrastructure.** Matches `BINDER_PIVOT_DESIGN.md` §3.6's "~3 K C++" estimate.

### 3.4 Build flow

```
make aidl-audio    # If Android 16 has AIDL'd parts of libaudioclient, regenerate; else no-op
make deps-audio    # Compile audio_utils + extras into static archives
make libaudioclient-stubs  # Compile IAudioFlinger.cpp + IAudioTrack.cpp into static archive (Bp/Bn skeletons only)
make audio-daemon-bionic   # Compile + link M5 binary, Phase 1 bionic ABI
make audio-smoke-bionic
make audio-binder-smoke-bionic
make all-bionic
# Phase 2 will add audio-daemon-musl + the OHOS sysroot path.
```

**Linker dependencies (Phase 1 bionic):**

```
$(CXX) ... -o westlake-audio-daemon \
    [.o files of src/* + aosp-src/* + deps-src/*] \
    -L../aosp-libbinder-port/out/bionic -lbinder \
    -L../aosp-libbinder-port/out/bionic -lutils_binder \
    -L$(NDK_SYSROOT)/usr/lib/aarch64-linux-android -laaudio -llog \
    -lc -lm -ldl -lpthread
```

`libaaudio.so` is part of bionic NDK API level 26+ (we target API 30 to match dalvikvm's compile target).

**Expected stripped size:** ~6-8 MB (smaller than `BINDER_PIVOT_DESIGN.md`'s ~15 MB estimate — that figure was inclusive of audio policy + a more ambitious mixer; minimal substitute is leaner).

### 3.5 Process model

Mirrors `main_audioserver.cpp`'s shape but stripped of fork/media.log/hidl:

```cpp
int main(int argc, char** argv) {
    // 1. Pin to AOSP's expected priority class (best-effort).
    // 2. Configure logging — stderr fallback per OS_non_android_linux.cpp pattern.
    sp<ProcessState> ps = ProcessState::self();
    ps->setThreadPoolMaxThreadCount(4);       // 4 IPC threads
    ps->startThreadPool();

    // 3. Construct our backend FIRST (so registration is "ready or fail").
    std::unique_ptr<AudioBackend> backend = AudioBackend::make();
    if (!backend->probe()) { LOG_FATAL("backend probe failed"); return 1; }

    // 4. Construct service impl.
    sp<AudioServiceImpl> svc = new AudioServiceImpl(backend.get());

    // 5. Register with servicemanager. Use IServiceManager::addService so this
    //    works against M2 servicemanager identically to M4 services in dalvikvm.
    sp<IServiceManager> sm = defaultServiceManager();
    status_t st = sm->addService(String16("media.audio_flinger"), svc);
    if (st != OK) { LOG_FATAL("addService failed: %d", st); return 2; }

    // 6. Join the thread pool — block forever, answering Binder transactions.
    IPCThreadState::self()->joinThreadPool();
    return 0;
}
```

**Launch:** `scripts/sandbox-boot.sh` (or its successor `westlake-launch.sh`) starts the daemon AFTER `westlake-servicemanager` is `addService("manager", ...)`-ready and BEFORE dalvikvm boots (so framework.jar's first `ServiceManager.getService("media.audio_flinger")` finds it). The orchestrator already has the start-servicemanager-then-wait pattern (see `aosp-libbinder-port/lib-boot.sh` — CR7 hardened it); M5 adds one more line after servicemanager is up.

**Lifetime:** lives for the duration of the Westlake session. No lazy-start optimization in Phase 1. (Per `BINDER_PIVOT_DESIGN.md` §3.8 the eventual goal is lazy-launch, but M5 ships eager-launch to simplify; conversion is a Phase 3 optimization.)

---

## 4. Backend abstraction

### 4.1 Interface (~50 LOC `AudioBackend.h`)

```cpp
class AudioBackend {
public:
    virtual ~AudioBackend() = default;

    // One-time probe; returns true if backend is available (e.g. libaaudio loaded).
    virtual bool probe() = 0;

    // Opens an output stream matching the requested config.
    // The audio_io_handle_t is OUR opaque value — we keep a map handle → BackendStream.
    // The IAudioFlinger::openOutput path calls this; AudioServiceImpl assigns the handle.
    virtual status_t openOutput(uint32_t sampleRate, int channelCount,
                                audio_format_t format, audio_output_flags_t flags,
                                BackendStream** outStream, uint32_t* latencyMs) = 0;
    virtual status_t closeOutput(BackendStream* stream) = 0;

    // Pump from cblk ring buffer to backend. Called from per-track service thread.
    // Returns frames consumed; backend may block briefly waiting on its output.
    virtual ssize_t write(BackendStream* stream, const void* data, size_t bytes) = 0;

    // Lifecycle.
    virtual status_t startStream(BackendStream* stream) = 0;
    virtual status_t stopStream(BackendStream* stream) = 0;
    virtual status_t pauseStream(BackendStream* stream) = 0;
    virtual status_t flushStream(BackendStream* stream) = 0;

    // Track position.
    virtual status_t getRenderPosition(BackendStream* stream,
                                       uint64_t* framesRendered,
                                       int64_t* timeNs) = 0;

    // Factory.
    static std::unique_ptr<AudioBackend> make();
};

// Opaque per-stream handle. AAudioBackend wraps an AAudioStream*; OhosBackend wraps OH_AudioRenderer*.
struct BackendStream;
```

Factory selects at **compile time** via two mutually-exclusive defines (set by Makefile):

```cpp
std::unique_ptr<AudioBackend> AudioBackend::make() {
#if defined(WESTLAKE_PHASE1_AAUDIO)
    return std::make_unique<AAudioBackend>();
#elif defined(WESTLAKE_OHOS_TARGET)
    return std::make_unique<OhosBackend>();
#else
#  error "M5 requires WESTLAKE_PHASE1_AAUDIO or WESTLAKE_OHOS_TARGET"
#endif
}
```

### 4.2 Phase 1 — AAudio backend

Library: `libaaudio.so` (bionic NDK, API 26+).

Key API surface (from `frameworks/av/media/libaaudio/include/aaudio/AAudio.h`):

- `AAudio_createStreamBuilder(&builder)`
- `AAudioStreamBuilder_setSampleRate(builder, rate)`
- `AAudioStreamBuilder_setChannelCount(builder, channels)`
- `AAudioStreamBuilder_setFormat(builder, AAUDIO_FORMAT_PCM_FLOAT)` (we feed AOSP's float fmt)
- `AAudioStreamBuilder_setDirection(builder, AAUDIO_DIRECTION_OUTPUT)`
- `AAudioStreamBuilder_setPerformanceMode(builder, AAUDIO_PERFORMANCE_MODE_NONE)` — Phase 1 picks blocking mode; LOW_LATENCY can come later
- `AAudioStreamBuilder_openStream(builder, &stream)`
- `AAudioStream_requestStart(stream)` / `AAudioStream_requestPause(stream)` / `AAudioStream_requestStop(stream)`
- `AAudioStream_write(stream, audioData, numFrames, timeoutNanos)` — blocking write
- `AAudioStream_getTimestamp(stream, CLOCK_MONOTONIC, &framePosition, &timeNanos)`

Estimated AAudioBackend.cpp: **~250-300 LOC**.

### 4.3 Phase 2 — OHOS AudioRenderer backend (deferred to M11)

Library: `libohaudio.so` (OHOS SDK).

Key API surface (from `/home/dspfac/openharmony/interface/sdk_c/multimedia/audio_framework/audio_renderer/native_audiorenderer.h`):

- `OH_AudioStreamBuilder_Create(&builder, AUDIOSTREAM_TYPE_RENDERER)`
- `OH_AudioStreamBuilder_SetSamplingRate(builder, rate)`
- `OH_AudioStreamBuilder_SetChannelCount(builder, channels)`
- `OH_AudioStreamBuilder_SetSampleFormat(builder, AUDIOSTREAM_SAMPLE_F32LE)`
- `OH_AudioStreamBuilder_SetRendererCallback(builder, callbacks, userData)`
- `OH_AudioStreamBuilder_GenerateRenderer(builder, &renderer)`
- `OH_AudioRenderer_Start(renderer)` / `OH_AudioRenderer_Pause(renderer)` / `OH_AudioRenderer_Stop(renderer)` / `OH_AudioRenderer_Flush(renderer)`
- `OH_AudioRenderer_GetSamplingRate(renderer, &rate)`

OHOS AudioRenderer is **pull-based** (callback-driven), AAudio is **push-or-pull** depending on mode. Phase 1's AAudioBackend uses push (blocking write); M11's OhosBackend will use the OHOS callback model — that requires a different relationship to the ring buffer (callback drains it, not our thread pushing). This is the **single biggest architectural seam** between Phase 1 and Phase 2 audio paths, and the abstraction in §4.1 anticipates it by hiding the pump direction behind `write()` semantics that document "may not be called by external thread in pull mode" — the OhosBackend's `write()` becomes a producer-side enqueue, and the OHOS callback runs the actual consume-from-buffer-to-device.

Estimated OhosBackend.cpp: **~250-350 LOC** when implemented in M11.

### 4.4 Compile-time selection

Makefile chooses one variant per build:

```make
audio-daemon-bionic: WESTLAKE_BACKEND_DEFINE := -DWESTLAKE_PHASE1_AAUDIO=1
audio-daemon-musl:   WESTLAKE_BACKEND_DEFINE := -DWESTLAKE_OHOS_TARGET=1
```

Source files for the *unused* backend still compile (and just hold a stub `probe() { return false; }` if you ever instantiate them), so a single `make all` builds both variants — selecting via `LD_LIBRARY_PATH` at deploy time. The chosen backend is determined at link time by which factory implementation is referenced.

---

## 5. Shared memory transport (ashmem / memfd / cblk)

This is the **second-biggest M5 risk** after backend integration. AOSP `IAudioFlinger::createTrack` returns an `IMemory cblk` (a shared-memory region containing the control block + ring buffer) and an `IMemory buffers` (for non-shared-buffer mode). The dalvikvm-side AudioTrack maps these via `mmap` and writes audio data into the ring buffer; the daemon reads from the same region.

### 5.1 What AOSP does

`IAudioFlinger::createTrack` allocates via `MemoryDealer` (which in turn allocates an `ashmem` region — see `MemoryHeapBase.cpp` constructor with `MemoryHeapBase::USE_ASHMEM` flag — and slices it into chunks). The handle is passed across Binder via `Parcel::writeStrongBinder(IInterface::asBinder(memHeapBase))`; the receiver's BpMemoryHeap proxy maps the underlying file descriptor.

### 5.2 What we need

**ashmem is a Linux kernel module bundled with the binder driver mainline.** Pixel/OnePlus phones running Android have `/dev/ashmem`. So does any Linux kernel built with `CONFIG_ASHMEM=y`.

**The existing `aosp-libbinder-port` already pulls in AOSP's `ashmem-host.cpp`** (the memfd-based replacement that AOSP itself uses for non-Android builds) — see `aosp-libbinder-port/Makefile` line 156-159 (`LIBCUTILS_SRCS := ashmem-host.cpp native_handle.cpp atrace_stub.cpp`). This is the canonical solution: **use the AOSP-supplied memfd-based ashmem shim**. It implements the same `ashmem_create_region` / `ashmem_set_prot_region` / `ashmem_pin_region` / `ashmem_get_size_region` ABI but is backed by `memfd_create` + `fcntl(F_ADD_SEALS)`. The phone's actual `/dev/ashmem` is irrelevant in this path.

### 5.3 What M5 needs to do

1. **Reuse `aosp-libbinder-port/deps-src/libcutils/ashmem-host.cpp` and `MemoryHeapBase.cpp`.** Both are already cross-compiled for bionic-arm64 inside libbinder.so per Makefile lines 122-128 (`LIBBINDER_MEM_SRCS`). M5's daemon links against M1's libbinder.so and gets MemoryHeapBase / MemoryDealer / IMemory for free.

2. **Allocate the cblk via MemoryDealer.** Mirrors AOSP — at `createTrack` time, allocate ~64 KB of shared memory per track (cblk header ~256 bytes + ring buffer sized to frameCount × channelCount × sampleSize). Hand the resulting `sp<IMemory>` to the dalvikvm client via the Parcel.

3. **Run a per-track reader thread in the daemon** that mmaps the same cblk (same fd, dup'd at Binder marshaling time), spins on a futex (or sleeps short intervals) checking the cblk's `mServer` / `mUser` indices, and pumps data → backend.write().

4. **Use AOSP's `audio_utils/fifo` for the ring buffer logic** (`system/media/audio_utils/`). It's the same library AOSP AudioFlinger uses internally. Phase 1: pull in just `roundup.h`, `clock.h`, `format.h`, and maybe `RingBuffer.cpp` (a few hundred LOC). The dalvikvm-side AudioTrack already uses this library inside libaudioclient.so.

**M4-PRE7 (AssetManager natives) already demonstrates the ashmem-equivalent path works** — that path uses memfd at the asset-loading layer. Confirms `memfd_create` + `MAP_SHARED` is functional on the OnePlus 6's kernel. **No new kernel-side work is required for M5.**

### 5.4 What can go wrong

- **`futex(FUTEX_WAKE)` waits asymmetric** — AOSP audio_utils' fifo uses `futex()` syscalls. musl ≥1.2.0 supports `FUTEX_WAIT_PRIVATE`; bionic does. No portability problem.
- **CPU affinity / scheduling priority for the per-track reader thread** — AOSP audioserver is a SCHED_FIFO process with elevated priority; we run as a regular AID_SHELL (or whatever the orchestrator gives us). Phase 1 acceptable — small underruns at startup; mitigation is a small (16 KB) priming buffer the daemon pre-writes before AudioTrack.write begins.
- **`mmap(.., MAP_SHARED, fd, 0)` across UID boundaries** — dalvikvm and daemon both run as the orchestrator's UID in our sandbox (no per-app UID separation in Phase 1). No problem.

---

## 6. Bringup sequence

Reading from the orchestrator's perspective:

```
1. servicemanager (M2) starts.
   Waits on `BINDER_SET_CONTEXT_MGR` ioctl to /dev/vndbinder.
   Logs "context manager ok".

2. westlake-audio-daemon (M5) starts.
   Calls ProcessState::self() → opens /dev/vndbinder.
   Calls AudioBackend::make() → AAudioBackend probes:
     - dlopen("libaaudio.so") via NDK-static path
     - AAudio_createStreamBuilder(&b)
     - AAudioStreamBuilder_openStream(b, &probe)  (test stream, immediately close)
     - Logs "AAudio probe ok, sample rate=48000, frame_count=240"
   Calls defaultServiceManager()->addService("media.audio_flinger", svc).
   Logs "registered media.audio_flinger".
   Joins thread pool — blocks waiting for transactions.

3. dalvikvm (M3) boots.
   Bootclasspath = framework.jar + ext.jar + services.jar + aosp-shim.dex.
   M5-PRE registered 105 AudioSystem natives at System.loadLibrary("android_runtime_stub").

4. Test program (audio_binder_smoke.cpp standalone, OR a dex driving AudioTrack):
   - Looks up service: defaultServiceManager()->getService("media.audio_flinger")
   - Calls IAudioFlinger::Stub.asInterface(binder) → BpAudioFlinger
   - bp->openOutput(...) → M5 receives OPEN_OUTPUT, calls backend->openOutput → returns audio_io_handle_t (our opaque value 1).
   - bp->createTrack(input, &output, &status) → M5 receives CREATE_TRACK, allocates cblk via MemoryDealer, allocates a per-track reader thread; returns BnAudioTrack proxy + cblk IMemory.
   - Client mmap's cblk, writes samples into ring buffer in a loop.
   - Daemon reader thread pumps cblk → backend->write → AAudio → speaker.

5. Acceptance: 1s of 440 Hz tone audible from the OnePlus 6.
```

**Diagrammatically (extension of `BINDER_PIVOT_DESIGN.md` §3.3):**

```
              dalvikvm
            +----------+
            |framework |   AudioTrack.write(samples)
            |.jar      |        |
            +----------+        v
                  |        +-----------+
                  |        |cblk ring  | <-- shared memory (memfd)
                  v        |  buffer   |
            +----------+   +-----------+
            |libaudio  |        ^
            |client.so |        |
            +----------+        |
                  |             |
              IAudioFlinger  IAudioTrack    [/dev/vndbinder]
                 binder        binder
                  |             |
            +-----+-------------+----+
            |     westlake-audio-daemon  | (M5)
            |     +---------------+      |
            |     |AudioServiceImpl|     |
            |     |+ AudioTrackImpl|     |
            |     +-------+-------+      |
            |             v              |
            |     +---------------+      |
            |     |  AudioBackend |      |
            |     | (AAudio | OH) |      |
            |     +-------+-------+      |
            +-------------+--------------+
                          v
                 +------------------+
                 | libaaudio.so     |  (Phase 1)
                 | OR libohaudio.so |  (Phase 2)
                 +------------------+
                          v
                       speaker
```

---

## 7. Acceptance tests

### 7.1 Smoke 1 — standalone backend test (no Binder, no daemon)

**Source:** `aosp-audio-daemon-port/test/audio_smoke.cpp` (~80 LOC).

**Body:**
- `AudioBackend::make()` → AAudio (Phase 1) or OHOS (Phase 2)
- `openOutput(48000, 2, AUDIO_FORMAT_PCM_FLOAT, 0, &stream, &latency)`
- `startStream(stream)`
- Loop for 1 s, write 240 frames per chunk of float samples of `sin(2π·440·t)`
- `stopStream(stream)`, `closeOutput(stream)`
- Exit 0.

**Pass criteria:** 1 s of 440 Hz tone audible. Verifies backend works without bringing in any Binder dependency. Reproduces in <1 min after build.

### 7.2 Smoke 2 — Binder-backed daemon test

**Source:** `aosp-audio-daemon-port/test/audio_binder_smoke.cpp` (~120 LOC).

**Body:**
- Starts daemon as a child process (`fork` + `execve` daemon binary).
- `defaultServiceManager()->getService("media.audio_flinger")`
- `IAudioFlinger::Stub.asInterface(...)`
- Calls `openOutput`, then `createTrack`, mmaps cblk, writes 1 s of 440 Hz, kills daemon.

**Pass criteria:** tone audible, no `LOG_FATAL` lines from daemon, no SIGSEGV/SIGABRT either side, daemon exits cleanly when sent `SIGTERM`.

### 7.3 Integration — dex driving AudioTrack

**Source:** new dex in `aosp-audio-daemon-port/test/AudioTrackTest.java` (~70 LOC; built into a dex via `build_audio_track_test.sh` mirroring M3's HelloBinder pattern).

**Body:**
- Class init triggers `System.loadLibrary("android_runtime_stub")` → JNI_OnLoad_audiosystem registers M5-PRE's 105 stubs.
- Calls `new AudioTrack.Builder().setAudioFormat(...).setBufferSize(...).build()` → through framework.jar's AudioTrack → libaudioclient.so → BpAudioFlinger → M5.
- `track.write(samples, 0, samples.length, AudioTrack.WRITE_BLOCKING)`
- Verify tone is audible.

**Pass criteria:** real framework code path, end-to-end. Demonstrates dalvikvm-side AudioTrack works through M5.

### 7.4 Integration — noice plays a sound

This is M7-territory but listed here to confirm Tier-1 coverage is correct. After M4 services + M5 are all up, noice should be able to tap a sound preset and have audio play. **Pass criteria:** noice "Rain" sound audible for 5 seconds after tap. (See `BINDER_PIVOT_MILESTONES.md` §M5 acceptance and `M4_DISCOVERY.md` §6 Tier-2.)

### 7.5 Regression integration

Master regression script (`scripts/binder-pivot-regression.sh`) gets an entry:

```bash
# Section: M5 audio daemon
section_start "M5 audio daemon"
run_test "audio_smoke (standalone backend)" \
    "$ADB shell '/data/local/tmp/westlake/bin-bionic/audio_smoke'" \
    "tone done" "FAIL"
run_test "audio_binder_smoke (full path)" \
    "$ADB shell '/data/local/tmp/westlake/bin-bionic/audio_binder_smoke'" \
    "tone done via binder" "FAIL"
section_end
```

Mirroring D2 ('Master regression test script').

---

## 8. Risk register

| # | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| 1 | **AOSP's Android-11 `IAudioFlinger.cpp` Bp/Bn doesn't match the AudioTrack version we run** (transaction codes drift Android 16 vs 11) | **High** | High | Run discovery on the dalvikvm side BEFORE writing the daemon: dex script that calls `AudioTrack.write(...)` and logs every transaction code seen via `binder_jni_stub.cc`'s existing instrumentation. Pin the daemon's transaction enum to whatever Android 16 framework.jar's `BnAudioFlinger` uses. Spend ~half a day on this step; saves multiple days of integration debug. |
| 2 | **MemoryDealer/IMemory cross-process mmap blows up on bionic-only feature** (Android 16 IMemory has changed unsecurePointer semantics post-Android 11) | Medium | High | Phase 1 uses `aosp-libbinder-port/out/bionic/libbinder.so` which includes MemoryHeapBase + ashmem-host.cpp from Android 14+ source. Verify dalvikvm-side libaudioclient.so calls `IMemory::pointer()` not the deprecated `IMemory::unsecurePointer()`. If it does call the deprecated form: patch the dalvikvm-loaded libaudioclient.so OR provide a compatibility shim in our libbinder.so. |
| 3 | **AAudio underruns or refuses our chosen format** | Medium | Medium | Defensive: probe AAudio capabilities at `probe()` time (sample rates, formats), and have AudioBackend::openOutput return the *closest match* the backend supports — AudioServiceImpl's openOutput then reports back the actual sample rate to the client via the Parcel reply, which AudioTrack will resample to client-side. AAudio API has `AAudioStream_getFormat`/`AAudioStream_getSampleRate` for this. |
| 4 | **HIDL / mediautils dependencies in IAudioFlinger.cpp explode the dep graph** (the AOSP file `#include`s `<hidl/HidlTransportSupport.h>` and `<mediautils/TimeCheck.h>` — neither of which we want to port) | Medium | Medium | Patches `patches/0001-stub-mediautils-timecheck.patch` and `patches/0002-stub-hidl-transport-support.patch` apply trivial no-op stubs. AOSP's `TimeCheck` is a watchdog for slow transactions — safely no-oppable for Phase 1. HIDL is only used for AudioFlinger-internal HAL device discovery; we're not implementing the HAL, so safely stubbable. |
| 5 | **AudioPolicyService dependency exists but we haven't discovered it yet** (noice's AudioManager.requestAudioFocus path may bind to it) | Medium | Low | Make `media.audio_policy` registration optional. If discovery reveals it's needed: add a minimal `IAudioPolicyService` Bn ~5 methods (estimated +1 day). If not needed: omit entirely. |
| 6 | **The dalvikvm-side libaudioclient.so isn't even loaded** (AudioTrack class init may fail before reaching M5) | Low | High | M5-PRE already addressed: 105 AudioSystem JNI methods stubbed, ART tolerates `<clinit>`. The AudioTrack class itself uses libaudioclient.so via System.loadLibrary("audioclient") — we may need a *libaudioclient_stub.so* if framework.jar's class init demands it (mirroring M3's libandroid_runtime_stub.so pattern). Verify before writing the daemon. |
| 7 | **Phase 2 OHOS AudioRenderer's pull-model can't reuse Phase 1's blocking-write reader thread shape** | Medium (only matters at M11) | Medium | Already anticipated in §4.3. M5 ships AAudioBackend; M11 swaps to OhosBackend with the producer/consumer relationship inverted via the existing `BackendStream` opaque layer. Defer the work to M11; don't preempt-design now. |

**Top 3 risks ranked by likelihood × impact:** #1 (transaction code drift) > #2 (cross-process mmap) > #4 (HIDL/mediautils dep graph).

**Top 1 risk that requires architectural attention before Builder starts:** #1 — Builder MUST do transaction-code discovery first via dex-side `AudioTrack` smoke test before writing the daemon's `onTransact` switch.

---

## 9. Effort estimate

### 9.1 Phase 1 (Android sandbox, AAudio backend) — this milestone

| Sub-task | Person-days |
|---|---|
| Transaction-code discovery via dex-side `AudioTrack` smoke (Builder writes a dex that exercises AudioTrack, runs in sandbox, captures transaction codes from binder_jni_stub.cc logs) | 0.5 |
| `aosp-audio-daemon-port/` directory + Makefile + build.sh + Phase-1 patches (stub HIDL + TimeCheck) | 0.5 |
| `AudioServiceImpl.cpp` — 12 Tier-1 + 49 fail-loud overrides | 1.5 |
| `AudioTrackImpl.cpp` + cblk/ring-buffer plumbing (reusing MemoryHeapBase + audio_utils/fifo) | 1.0 |
| `AAudioBackend.cpp` + `AudioBackend.h` (probe, openOutput, write, start/stop/pause/flush, getRenderPosition) | 1.0 |
| `main.cpp` + boot orchestration patch in `sandbox-boot.sh` / `westlake-launch.sh` | 0.5 |
| `audio_smoke.cpp` + `audio_binder_smoke.cpp` + `AudioTrackTest.dex` | 0.5 |
| Acceptance test on OnePlus 6 (audible 440 Hz tone end-to-end) | 0.5 |
| Polish + regression entry + doc | 0.5 |

**Total Phase 1: ~6.5 person-days.** Matches `BINDER_PIVOT_MILESTONES.md` §M5's "4-5 days" estimate plus the discovery step (which the milestones doc didn't itemize).

### 9.2 Phase 2 (OHOS backend swap — M11)

| Sub-task | Person-days |
|---|---|
| OHOS sysroot cross-build path (Makefile already supports musl variant by mirror) | 0.5 |
| `OhosBackend.cpp` — pull-model adapter for OH_AudioRenderer | 1.0 |
| Pull-model vs push-model reader-thread refactor (the `BackendStream` interface holds, but `AudioTrackImpl.cpp` needs a small reorg) | 0.5 |
| OHOS phone smoke test (replays §7.1 + §7.2 on OHOS hardware) | 0.5 |

**Total Phase 2 (M11): ~2.5 person-days.** Matches `BINDER_PIVOT_MILESTONES.md` §M11's "2-3 days" estimate.

### 9.3 Combined M5 + M11 effort

**~9 person-days total across both phases**, with a hard dependency on M1-M3 (libbinder + servicemanager + dalvikvm wiring) being green and on M5-PRE (105 AudioSystem JNI stubs) already landed (verified — §1.5 of `PHASE_1_STATUS.md`).

---

## 10. Critical files for the Builder agent to read first

1. `docs/engine/BINDER_PIVOT_DESIGN.md` §3.3, §3.6, §3.8 — architectural envelope
2. `docs/engine/BINDER_PIVOT_MILESTONES.md` §M5, §M11 — acceptance criteria
3. `aosp-libbinder-port/BUILD_PLAN.md` — Builder pattern to mirror
4. `aosp-libbinder-port/Makefile` — bionic target rules to extend
5. `shim/java/com/westlake/services/WestlakeActivityManagerService.java` — Tier-1 + fail-loud pattern in C++ form
6. `/home/dspfac/aosp-android-11/frameworks/av/media/libaudioclient/include/media/IAudioFlinger.h` — interface header (Android 11; verify against framework.jar)
7. `/home/dspfac/aosp-android-11/frameworks/av/media/libaudioclient/IAudioFlinger.cpp` — Bp/Bn skeleton to derive from
8. `/home/dspfac/aosp-android-11/frameworks/av/media/audioserver/main_audioserver.cpp` — main() shape (strip the fork/media.log/HIDL)
9. `art-latest/stubs/audiosystem_jni_stub.cc` — M5-PRE work this depends on
10. `/home/dspfac/openharmony/interface/sdk_c/multimedia/audio_framework/audio_renderer/native_audiorenderer.h` — Phase 2 target API (for M11 forward-planning only)

---

## 11. Critical insights (lower the milestones-doc risk estimate)

1. **Lower-risk than estimated:** `aosp-libbinder-port` already supplies MemoryHeapBase + ashmem-host.cpp (memfd-backed). The R3 risk in `BINDER_PIVOT_MILESTONES.md` ("IAudioFlinger uses shared-memory… need ashmem OR memfd replacement") is **already addressed** — M5 Builder reuses what M1 built. No new SHM work.

2. **Lower-risk than estimated:** M5-PRE already proved AOSP's 105 AudioSystem natives are stub-friendly (none of the AudioSystem natives need to land at a real backend — they're metadata-only). The dalvikvm-side framework.jar AudioSystem class init now succeeds; M5 just has to answer the *AudioTrack* path, which is a narrower binder-only surface.

3. **Higher-risk than estimated:** AOSP `IAudioFlinger.cpp` includes `<hidl/HidlTransportSupport.h>` and `<mediautils/TimeCheck.h>`. The HIDL transport dependency does NOT show up in the milestones-doc risk register but **adds 2 patches and ~50 LOC of stub work**. Builder should expect this surface.

4. **Higher-risk than estimated:** transaction code drift between Android 11 and Android 16. Builder MUST run dex-side discovery first to pin the transaction codes against the framework.jar version we ship.

5. **Neutral:** The Phase 1 vs Phase 2 backend split is clean in principle (~500 LOC `#ifdef` per `BINDER_PIVOT_DESIGN.md` §3.6) but the AAudio/AudioRenderer push-vs-pull model asymmetry means M11 will require a small refactor of `AudioTrackImpl.cpp` — the abstract `AudioBackend` interface in §4.1 anticipates this so the surface area is small (~50 LOC change).

**The Builder agent's biggest risk:** #1 (AIDL transaction code drift). Mitigation is a half-day dex-side discovery run that's roadmapped explicitly. ALL OTHER risks are bounded by reuse of existing infrastructure (libbinder.so + MemoryHeapBase + ashmem shim + M5-PRE AudioSystem stubs).

End of M5_AUDIO_DAEMON_PLAN.md.
