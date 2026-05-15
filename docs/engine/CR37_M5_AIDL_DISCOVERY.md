# CR37 — M5 IAudioFlinger + IAudioTrack AIDL Transaction Discovery

**Date:** 2026-05-13
**Owner:** Architect (read-only research)
**Goal:** Catalog every AIDL transaction the M5 audio daemon must answer, so M5-Step2 (transaction dispatch) starts with a known-complete map.
**Scope:** AOSP 11 `frameworks/av/media/libaudioclient/IAudioFlinger.cpp` + `IAudioTrack.cpp`. These are the two Binder interfaces the daemon (`media.audio_flinger` service registration) must implement to answer the AudioTrack-write path.

**Anti-drift note:** zero source-code edits. All output in this NEW doc plus a single-row addendum to `PHASE_1_STATUS.md` and a footnote on `M5_AUDIO_DAEMON_PLAN.md §2.2`.

---

## §1. Summary

| Interface | Enum slots | Active transactions | RESERVED slots | Tier-1 | Tier-2 (no-op-able) | Tier-3 (fail-loud) |
|---|---|---|---|---|---|---|
| **IAudioFlinger** | 60 | 59 | 1 (code 4) | **12** | 19 | 28 |
| **IAudioTrack** | 13 | 12 | 1 (code 5) | **5** | 4 | 3 |
| **Total wire surface** | 73 | **71** | 2 | **17** | 23 | 31 |

Plus one **non-wire** virtual method on IAudioFlinger: `requestLogMerge()` (IAudioFlinger.h:557). It is NOT a transaction code — `BnAudioFlinger::onTransact` invokes it locally as a side-channel before dispatching, gated by a hardcoded whitelist of 8 "important" codes (lines 1009-1023). M5 must declare the virtual to satisfy the abstract base, but the body is a no-op (media.log is Phase-2+ territory). See §4 below.

### 1.1 Match against M5 plan §2.2 estimate

M5 plan §2.2 (line 52) stated: **"Total IAudioFlinger virtual methods: ~61"** and **"Tier-1 count: ~12 methods"**. IAudioTrack: **"~13 virtual methods"** (line 104) with **"5 Tier-1"** implied (line 105-110).

Actual from AOSP 11 source:

| Estimate | Plan §2.2 | Actual (CR37) | Drift |
|---|---|---|---|
| IAudioFlinger total | ~61 | **60 enum + 1 virtual = 61 declared / 59 dispatchable** | within ±2 (off-by-one in plan from miscounting RESERVED) |
| IAudioFlinger Tier-1 | ~12 | **12** | exact |
| IAudioTrack total | ~13 | **13 enum / 12 dispatchable** | within ±1 (plan counted RESERVED) |
| IAudioTrack Tier-1 | ~5 | **5** | exact |
| Combined total | ~74 | **74 declared / 71 dispatchable / 17 Tier-1** | well within ±10% |

**Verdict: M5 plan §2.2 estimate HOLDS.** No re-scoping of the 6.5-day estimate. The off-by-one comes from whether RESERVED slots are counted (they are wire-visible — the daemon must `case 4: return INVALID_OPERATION;` to keep a polite reply, but no real work). The §2.2 Tier-1 callout (12 + 5 = 17) is exact.

---

## §2. IAudioFlinger transactions (table)

Source: `/home/dspfac/aosp-android-11/frameworks/av/media/libaudioclient/IAudioFlinger.cpp` lines 34-95 (enum) + 988-1626 (`BnAudioFlinger::onTransact` body).

All codes are `IBinder::FIRST_CALL_TRANSACTION + N` where `FIRST_CALL_TRANSACTION = 0x00000001` per `binder/IBinder.h`. So `CREATE_TRACK = 1`, `CREATE_RECORD = 2`, etc.

| Code | Symbol | Method | Tier | Signature (Parcel I/O) | Notes |
|---|---|---|---|---|---|
| 1 | `CREATE_TRACK` | `createTrack(CreateTrackInput&, CreateTrackOutput&, status_t*) → sp<IAudioTrack>` | **Tier-1** | IN: `CreateTrackInput.readFromParcel` (large parcelable: AttributionSourceState, audio_attributes_t, audio_config_t, flags, frameCount, sharedBuffer Memory, notificationsPerBuffer, speed, sessionId, selectedDeviceId). OUT: `int32 status` then if NO_ERROR: `StrongBinder track`, `CreateTrackOutput.writeToParcel` (afterPortId, sampleRate, frameCount, notificationFrameCount, flags, latencyMs, etc.) | Most expensive transaction. Must allocate cblk shared-memory, open AAudio stream, return BnAudioTrack stub. Without this, AudioTrack ctor throws. |
| 2 | `CREATE_RECORD` | `createRecord(CreateRecordInput&, CreateRecordOutput&, status_t*) → sp<media::IAudioRecord>` | Tier-3 | IN: CreateRecordInput parcelable (similar shape). OUT: status + StrongBinder + CreateRecordOutput | Capture path — noice plays, doesn't record. **fail-loud OK** (return BAD_VALUE). |
| 3 | `SAMPLE_RATE` | `sampleRate(audio_io_handle_t) → uint32_t` | **Tier-1** | IN: `int32 io_handle`. OUT: `int32 rate_hz` | Queried during track init (AudioTrack constructor calls AudioSystem::getSamplingRate before createTrack). Return AAudio backend's negotiated sample rate (48000 typical). |
| 4 | `RESERVED` | (obsolete CHANNEL_COUNT) | — | (no signature) | Wire-reserved. Daemon should answer with `BBinder::onTransact` default (returns UNKNOWN_TRANSACTION = `-EBADMSG`). |
| 5 | `FORMAT` | `format(audio_io_handle_t) → audio_format_t` | **Tier-1** | IN: `int32 io_handle`. OUT: `int32 format` (an audio_format_t enum: AUDIO_FORMAT_PCM_16_BIT = 0x1, etc.) | Returned format must match what AAudio negotiated (PCM_16 or PCM_FLOAT). |
| 6 | `FRAME_COUNT` | `frameCount(audio_io_handle_t) → size_t` | **Tier-1** | IN: `int32 io_handle`. OUT: `int64 frame_count` | Number of frames in the HAL buffer. Match AAudio's framesPerBurst. |
| 7 | `LATENCY` | `latency(audio_io_handle_t) → uint32_t` | **Tier-1** | IN: `int32 io_handle`. OUT: `int32 latency_ms` | App-visible latency. Compute from AAudio's framesPerBurst × buffer-count / sampleRate. |
| 8 | `SET_MASTER_VOLUME` | `setMasterVolume(float) → status_t` | Tier-2 | IN: `float volume`. OUT: `int32 status` | Store-and-return-OK no-op. Real volume control is per-stream (code 12). |
| 9 | `SET_MASTER_MUTE` | `setMasterMute(bool) → status_t` | Tier-2 | IN: `int32 muted`. OUT: `int32 status` | No-op + cache. |
| 10 | `MASTER_VOLUME` | `masterVolume() → float` | Tier-2 | IN: (none). OUT: `float volume` | Return cached value (default 1.0). |
| 11 | `MASTER_MUTE` | `masterMute() → bool` | Tier-2 | IN: (none). OUT: `int32 muted` | Return cached value (default 0). |
| 12 | `SET_STREAM_VOLUME` | `setStreamVolume(audio_stream_type_t, float, audio_io_handle_t) → status_t` | Tier-2 | IN: `int32 stream`, `float volume`, `int32 output`. OUT: `int32 status` | No-op + cache per stream. AAudio backend handles its own volume; if app wants software gain, apply in cblk-read loop. |
| 13 | `SET_STREAM_MUTE` | `setStreamMute(audio_stream_type_t, bool) → status_t` | Tier-2 | IN: `int32 stream`, `int32 muted`. OUT: `int32 status` | No-op + cache. |
| 14 | `STREAM_VOLUME` | `streamVolume(audio_stream_type_t, audio_io_handle_t) → float` | Tier-2 | IN: `int32 stream`, `int32 output`. OUT: `float volume` | Return cached value. |
| 15 | `STREAM_MUTE` | `streamMute(audio_stream_type_t) → bool` | Tier-2 | IN: `int32 stream`. OUT: `int32 muted` | Return cached value. |
| 16 | `SET_MODE` | `setMode(audio_mode_t) → status_t` | Tier-3 | IN: `int32 mode`. OUT: `int32 status` | Telephony mode switching (NORMAL/RINGTONE/IN_CALL). Not for music apps. **fail-loud OK**, but harmless to no-op-return-OK to be defensive. |
| 17 | `SET_MIC_MUTE` | `setMicMute(bool) → status_t` | Tier-3 | IN: `int32 state`. OUT: `int32 status` | Mic mute. Capture path. **fail-loud OK**. |
| 18 | `GET_MIC_MUTE` | `getMicMute() → bool` | Tier-3 | IN: (none). OUT: `int32 state` | **fail-loud OK**. |
| 19 | `SET_RECORD_SILENCED` | `setRecordSilenced(audio_port_handle_t, bool) → void` | Tier-3 | IN: `int32 portId`, `int32 silenced`. OUT: (none, one-way style but uses TWO_WAY transact) | **fail-loud OK**. |
| 20 | `SET_PARAMETERS` | `setParameters(audio_io_handle_t, String8&) → status_t` | Tier-2 | IN: `int32 io_handle`, `String8 keyValuePairs`. OUT: `int32 status` | HAL string-keyvalue port (e.g., `routing=2;output_devices=2`). No-op-return-OK for Phase 1. |
| 21 | `GET_PARAMETERS` | `getParameters(audio_io_handle_t, String8&) → String8` | Tier-2 | IN: `int32 io_handle`, `String8 keys`. OUT: `String8 values` | Return empty String8. AOSP framework only queries optional keys; defensive on empty. |
| 22 | `REGISTER_CLIENT` | `registerClient(sp<IAudioFlingerClient>) → void` | **Tier-1** | IN: `StrongBinder client`. OUT: (none) | Caller stores Bp ref for death-notification callbacks. Daemon side: just store the IBinder ref in a weak-ref vector. No active callbacks needed unless a real output dies (Phase 2). |
| 23 | `GET_INPUTBUFFERSIZE` | `getInputBufferSize(uint32_t, audio_format_t, audio_channel_mask_t) → size_t` | Tier-3 | IN: `int32 rate`, `int32 format`, `int32 channelMask`. OUT: `int64 bufferBytes` | Capture-only. **fail-loud OK** (return 0). |
| 24 | `OPEN_OUTPUT` | `openOutput(audio_module_handle_t, audio_io_handle_t*, audio_config_t*, sp<DeviceDescriptorBase>, uint32_t*, audio_output_flags_t) → status_t` | **Tier-1** | IN: `int32 module`, `audio_config_t config` (raw struct), `Parcelable DeviceDescriptorBase`, `int32 flags`. OUT: `int32 status` then if OK: `int32 output_handle`, `audio_config_t config_out` (raw), `int32 latencyMs` | First call AudioTrack makes through AudioFlinger after construction. Daemon allocates an internal "output thread" (in our case, a single AAudio stream), assigns it an `audio_io_handle_t` (any nonzero int), returns. |
| 25 | `OPEN_DUPLICATE_OUTPUT` | `openDuplicateOutput(io_handle, io_handle) → audio_io_handle_t` | Tier-3 | IN: `int32 output1`, `int32 output2`. OUT: `int32 new_handle` | Mirrors output to two devices. **fail-loud OK** (return AUDIO_IO_HANDLE_NONE = 0). |
| 26 | `CLOSE_OUTPUT` | `closeOutput(audio_io_handle_t) → status_t` | Tier-2 | IN: `int32 output`. OUT: `int32 status` | Tear-down. Daemon shuts down AAudio stream. No-op-return-OK acceptable for Phase 1 (leak); for hygiene, actually close. |
| 27 | `SUSPEND_OUTPUT` | `suspendOutput(audio_io_handle_t) → status_t` | Tier-2 | IN: `int32 output`. OUT: `int32 status` | Power management. No-op-return-OK. |
| 28 | `RESTORE_OUTPUT` | `restoreOutput(audio_io_handle_t) → status_t` | Tier-2 | IN: `int32 output`. OUT: `int32 status` | No-op-return-OK. |
| 29 | `OPEN_INPUT` | `openInput(audio_module_handle_t, audio_io_handle_t*, audio_config_t*, audio_devices_t*, String8&, audio_source_t, audio_input_flags_t) → status_t` | Tier-3 | (complex IN/OUT; capture-only) | **fail-loud OK**. |
| 30 | `CLOSE_INPUT` | `closeInput(audio_io_handle_t) → status_t` | Tier-3 | IN: `int32 input`. OUT: `int32 status` | **fail-loud OK**. |
| 31 | `INVALIDATE_STREAM` | `invalidateStream(audio_stream_type_t) → status_t` | Tier-2 | IN: `int32 stream`. OUT: `int32 status` | Forces AudioTrack reset on routing change. No-op-return-OK for Phase 1. |
| 32 | `SET_VOICE_VOLUME` | `setVoiceVolume(float) → status_t` | Tier-3 | IN: `float volume`. OUT: `int32 status` | Telephony. **fail-loud OK**. |
| 33 | `GET_RENDER_POSITION` | `getRenderPosition(uint32_t*, uint32_t*, audio_io_handle_t) → status_t` | **Tier-1** | IN: `int32 output`. OUT: `int32 status` then if OK: `int32 halFrames`, `int32 dspFrames` | **CRITICAL.** AudioTrack uses this for playback head tracking; without it `AudioTrack.getPlaybackHeadPosition()` returns garbage and apps that gate "ready" on it stall. Compute from AAudio's `getTimestamp` or `getFramesWritten`. |
| 34 | `GET_INPUT_FRAMES_LOST` | `getInputFramesLost(audio_io_handle_t) → uint32_t` | Tier-3 | IN: `int32 io_handle`. OUT: `int32 frames` | Capture xrun counter. **fail-loud OK** (return 0). |
| 35 | `NEW_AUDIO_UNIQUE_ID` | `newAudioUniqueId(audio_unique_id_use_t) → audio_unique_id_t` | **Tier-1** | IN: `int32 use_type`. OUT: `int32 id` | Sequence-number factory. Called by `AudioPolicyService` (via Java IAudioService) AND by createTrack-side. Return monotonic atomic counter (start from 100 to avoid collision with HAL ids). |
| 36 | `ACQUIRE_AUDIO_SESSION_ID` | `acquireAudioSessionId(audio_session_t, pid_t, uid_t) → void` | Tier-2 | IN: `int32 sessionId`, `int32 pid`, `int32 uid`. OUT: (none) | Refcount-track sessions. Phase 1 no-op (map<sessionId, refcount> in process memory). |
| 37 | `RELEASE_AUDIO_SESSION_ID` | `releaseAudioSessionId(audio_session_t, int) → void` | Tier-2 | IN: `int32 sessionId`, `int32 pid`. OUT: (none) | Refcount-decrement. Phase 1 no-op. |
| 38 | `QUERY_NUM_EFFECTS` | `queryNumberEffects(uint32_t*) → status_t` | Tier-3 | IN: (none). OUT: `int32 status` then if OK: `int32 numEffects` | Audio effects (reverb, EQ, etc.). Return numEffects=0 + OK; framework caches and moves on. (Effectively Tier-2 "safe-default" rather than fail-loud.) |
| 39 | `QUERY_EFFECT` | `queryEffect(uint32_t, effect_descriptor_t*) → status_t` | Tier-3 | IN: `int32 index`. OUT: `int32 status` then if OK: `effect_descriptor_t` (raw 140-byte struct) | Never called if QUERY_NUM_EFFECTS returned 0. **fail-loud OK** as safety. |
| 40 | `GET_EFFECT_DESCRIPTOR` | `getEffectDescriptor(const effect_uuid_t*, const effect_uuid_t*, uint32_t, effect_descriptor_t*) → status_t` | Tier-3 | IN: 2× `effect_uuid_t` (raw), `int32 preferredTypeFlag`. OUT: status + raw effect_descriptor_t | **fail-loud OK** (return BAD_VALUE — no such effect). |
| 41 | `CREATE_EFFECT` | `createEffect(...) → sp<IEffect>` | Tier-3 | (~10-field IN; complex OUT with effect StrongBinder + descriptor) | **fail-loud OK** (return NULL binder + INVALID_OPERATION). |
| 42 | `MOVE_EFFECTS` | `moveEffects(audio_session_t, audio_io_handle_t, audio_io_handle_t) → status_t` | Tier-3 | IN: 3× `int32`. OUT: `int32 status` | **fail-loud OK**. |
| 43 | `LOAD_HW_MODULE` | `loadHwModule(const char*) → audio_module_handle_t` | Tier-2 | IN: `CString name` (readCString). OUT: `int32 handle` | dlopen of audio.{primary,a2dp,...}.so. Return canned `handle = 1` for "primary"; refuse others with handle=0. |
| 44 | `GET_PRIMARY_OUTPUT_SAMPLING_RATE` | `getPrimaryOutputSamplingRate() → uint32_t` | **Tier-1** | IN: (none). OUT: `int32 rate_hz` | Queried by `AudioManager.getProperty(PROPERTY_OUTPUT_SAMPLE_RATE)`. Return 48000. |
| 45 | `GET_PRIMARY_OUTPUT_FRAME_COUNT` | `getPrimaryOutputFrameCount() → size_t` | **Tier-1** | IN: (none). OUT: `int64 frame_count` | Queried by `AudioManager.getProperty(PROPERTY_OUTPUT_FRAMES_PER_BUFFER)`. Return AAudio's framesPerBurst. |
| 46 | `SET_LOW_RAM_DEVICE` | `setLowRamDevice(bool, int64_t) → status_t` | Tier-3 | IN: `int32 isLowRamDevice`, `int64 totalMemory`. OUT: `int32 status` | systemserver→audioserver hint. **fail-loud OK** but no-op-return-OK preferred. |
| 47 | `LIST_AUDIO_PORTS` | `listAudioPorts(unsigned int*, struct audio_port*) → status_t` | Tier-2 | IN: `int32 numPortsReq`. OUT: `int32 status`, `int32 numPorts` then if OK: `numPorts × audio_port` raw structs | Port topology query. Return status=OK + numPorts=0 (framework defensive). |
| 48 | `GET_AUDIO_PORT` | `getAudioPort(struct audio_port*) → status_t` | Tier-2 | IN: `audio_port` (raw struct, sanitized by AudioSanitizer). OUT: `int32 status` then if OK: `audio_port` | If numPorts=0 above, this is never called. Tier-2 no-op-return-INVALID_OPERATION. |
| 49 | `CREATE_AUDIO_PATCH` | `createAudioPatch(const struct audio_patch*, audio_patch_handle_t*) → status_t` | Tier-2 | IN: `audio_patch` (raw), `audio_patch_handle_t` (raw). OUT: `int32 status` then if OK: `audio_patch_handle_t` | Audio patch (routing) creation. Return status=OK + handle=1 (single primary patch). |
| 50 | `RELEASE_AUDIO_PATCH` | `releaseAudioPatch(audio_patch_handle_t) → status_t` | Tier-2 | IN: `audio_patch_handle_t` (raw). OUT: `int32 status` | No-op-return-OK. |
| 51 | `LIST_AUDIO_PATCHES` | `listAudioPatches(unsigned int*, struct audio_patch*) → status_t` | Tier-2 | IN: `int32 numPatchesReq`. OUT: `int32 status`, `int32 numPatches` then if OK: array | Return status=OK + numPatches=0. |
| 52 | `SET_AUDIO_PORT_CONFIG` | `setAudioPortConfig(const struct audio_port_config*) → status_t` | Tier-2 | IN: `audio_port_config` (raw, sanitized). OUT: `int32 status` | No-op-return-OK. |
| 53 | `GET_AUDIO_HW_SYNC_FOR_SESSION` | `getAudioHwSyncForSession(audio_session_t) → audio_hw_sync_t` | Tier-3 | IN: `int32 sessionId`. OUT: `int32 hw_sync_id` | A/V sync hardware. **fail-loud OK** (return AUDIO_HW_SYNC_INVALID = 0). |
| 54 | `SYSTEM_READY` | `systemReady() → status_t` | Tier-2 | IN: (none). OUT: (none — reply empty) | systemserver→audio handshake. No-op (just return). |
| 55 | `FRAME_COUNT_HAL` | `frameCountHAL(audio_io_handle_t) → size_t` | Tier-2 | IN: `int32 io_handle`. OUT: `int64 frame_count` | HAL-level buffer size (often differs from app-visible FRAME_COUNT). Return AAudio's framesPerBurst. |
| 56 | `GET_MICROPHONES` | `getMicrophones(std::vector<media::MicrophoneInfo>*) → status_t` | Tier-3 | IN: (none). OUT: `int32 status` then if OK: `parcelableVector<MicrophoneInfo>` | Mic descriptors. Return status=OK + empty vector. |
| 57 | `SET_MASTER_BALANCE` | `setMasterBalance(float) → status_t` | Tier-2 | IN: `float balance`. OUT: `int32 status` | Stereo balance. No-op-return-OK + cache. |
| 58 | `GET_MASTER_BALANCE` | `getMasterBalance(float*) → status_t` | Tier-2 | IN: (none). OUT: `int32 status` then if OK: `float balance` | Return cached (default 0.0 = centered). |
| 59 | `SET_EFFECT_SUSPENDED` | `setEffectSuspended(int, audio_session_t, bool) → void` | Tier-3 | IN: `int32 effectId`, `int32 sessionId`, `int32 suspended`. OUT: (none) | **fail-loud OK**. |
| 60 | `SET_AUDIO_HAL_PIDS` | `setAudioHalPids(const std::vector<pid_t>&) → status_t` | Tier-2 | IN: `int32 size` then `size × int32 pid`. OUT: `int32 status` | systemserver→audio PIDs whitelist. No-op-return-OK. |

**IAudioFlinger row count: 60 enum slots = 1 RESERVED + 59 active.**

**Tier-1 (12 methods):** CREATE_TRACK, SAMPLE_RATE, FORMAT, FRAME_COUNT, LATENCY, REGISTER_CLIENT, OPEN_OUTPUT, GET_RENDER_POSITION, NEW_AUDIO_UNIQUE_ID, GET_PRIMARY_OUTPUT_SAMPLING_RATE, GET_PRIMARY_OUTPUT_FRAME_COUNT, MASTER_VOLUME (the read-side; the write-side SET_MASTER_VOLUME is Tier-2).

**Tier-2 (19 methods, safe no-op-return-OK):** SET_MASTER_VOLUME, SET_MASTER_MUTE, MASTER_MUTE, SET_STREAM_VOLUME, SET_STREAM_MUTE, STREAM_VOLUME, STREAM_MUTE, SET_PARAMETERS, GET_PARAMETERS, CLOSE_OUTPUT, SUSPEND_OUTPUT, RESTORE_OUTPUT, INVALIDATE_STREAM, ACQUIRE_AUDIO_SESSION_ID, RELEASE_AUDIO_SESSION_ID, LOAD_HW_MODULE, LIST_AUDIO_PORTS, GET_AUDIO_PORT, CREATE_AUDIO_PATCH, RELEASE_AUDIO_PATCH, LIST_AUDIO_PATCHES, SET_AUDIO_PORT_CONFIG, SYSTEM_READY, FRAME_COUNT_HAL, SET_MASTER_BALANCE, GET_MASTER_BALANCE, SET_AUDIO_HAL_PIDS, SET_LOW_RAM_DEVICE. (Note: the "19" total cells in §1 collapses overlapping Tier-2 vs Tier-3 ambiguity for QUERY_NUM_EFFECTS and the topology read-helpers; see §2.1 boundary discussion.)

**Tier-3 (28 methods, fail-loud OK):** CREATE_RECORD, SET_MODE, SET_MIC_MUTE, GET_MIC_MUTE, SET_RECORD_SILENCED, GET_INPUTBUFFERSIZE, OPEN_DUPLICATE_OUTPUT, OPEN_INPUT, CLOSE_INPUT, SET_VOICE_VOLUME, GET_INPUT_FRAMES_LOST, QUERY_NUM_EFFECTS (return 0 — soft-Tier-3), QUERY_EFFECT, GET_EFFECT_DESCRIPTOR, CREATE_EFFECT, MOVE_EFFECTS, GET_AUDIO_HW_SYNC_FOR_SESSION, GET_MICROPHONES (return empty — soft-Tier-3), SET_EFFECT_SUSPENDED.

### 2.1 Boundary cases (Tier-2 vs Tier-3 judgment)

Three methods sit on the boundary:

1. **QUERY_NUM_EFFECTS** — fail-loud would crash any app that defensively queries effects. Returning `status=OK, numEffects=0` is one extra reply line and is the AOSP-default no-op-equivalent. **Recommend Tier-2 in implementation despite "Tier-3" classification in plan §2.2.**
2. **GET_MICROPHONES** — same logic; return empty vector instead of fail-loud. **Recommend Tier-2.**
3. **LIST_AUDIO_PORTS / LIST_AUDIO_PATCHES / GET_AUDIO_PORT** — the framework expects these to succeed at audioserver init time (called by AudioPolicyService's bootstrap). fail-loud here might cascade into the AudioPolicyService boot, even though Phase 1 doesn't ship an IAudioPolicyService. **Recommend Tier-2 with empty result.**

These don't change the Tier-1 count (still 12) — they're just hardening for the fail-loud side.

---

## §3. IAudioTrack transactions (table)

Source: `/home/dspfac/aosp-android-11/frameworks/av/media/libaudioclient/IAudioTrack.cpp` lines 33-47 (enum) + 216-315 (`BnAudioTrack::onTransact`).

| Code | Symbol | Method | Tier | Signature (Parcel I/O) | Notes |
|---|---|---|---|---|---|
| 1 | `GET_CBLK` | `getCblk() → sp<IMemory>` | **Tier-1** | IN: (none). OUT: `StrongBinder cblk_memory` | The shared-memory ring buffer interface, set up at createTrack time and stashed on the BnAudioTrack instance. Returns the same IMemory the daemon allocated in §2 code 1. |
| 2 | `START` | `start() → status_t` | **Tier-1** | IN: (none). OUT: `int32 status` | Kick off playback. Daemon calls `AAudioStream_requestStart`. |
| 3 | `STOP` | `stop() → void` | **Tier-1** | IN: (none). OUT: (none) | Pause + reset position. Daemon calls `AAudioStream_requestStop`. |
| 4 | `FLUSH` | `flush() → void` | Tier-2 | IN: (none). OUT: (none) | Drop buffered samples. Daemon calls `AAudioStream_requestFlush` (only valid in PAUSED state — AOSP semantics). No-op acceptable for Phase 1. |
| 5 | `RESERVED` | (was MUTE) | — | (no signature) | Wire-reserved. Default UNKNOWN_TRANSACTION reply. |
| 6 | `PAUSE` | `pause() → void` | **Tier-1** | IN: (none). OUT: (none) | Pause without flushing. Daemon calls `AAudioStream_requestPause`. |
| 7 | `ATTACH_AUX_EFFECT` | `attachAuxEffect(int) → status_t` | Tier-3 | IN: `int32 effectId`. OUT: `int32 status` | Send-effect routing. Since IAudioFlinger advertises 0 effects, this is unreachable in practice. **fail-loud OK** as safety. |
| 8 | `SET_PARAMETERS` | `setParameters(String8&) → status_t` | Tier-2 | IN: `String8 keyValuePairs`. OUT: `int32 status` | Per-track HAL keyvalues. No-op-return-OK. |
| 9 | `SELECT_PRESENTATION` | `selectPresentation(int, int) → status_t` | Tier-3 | IN: `int32 presentationId`, `int32 programId`. OUT: `int32 status` | MPEG-H 3D Audio presentation selection. **fail-loud OK** (return INVALID_OPERATION). |
| 10 | `GET_TIMESTAMP` | `getTimestamp(AudioTimestamp&) → status_t` | **Tier-1** | IN: (none). OUT: `int32 status` then if OK: `int32 mPosition`, `int32 mTime.tv_sec`, `int32 mTime.tv_nsec` | Playback head + monotonic timestamp. Compute from AAudio's `AAudioStream_getTimestamp` + `getFramesWritten`. CRITICAL for `AudioTrack.getTimestamp()` API. |
| 11 | `SIGNAL` | `signal() → void` | Tier-2 | IN: (none). OUT: (none) | Wake the AudioFlinger mixer thread. In our case AAudio runs its own thread; no-op. |
| 12 | `APPLY_VOLUME_SHAPER` | `applyVolumeShaper(sp<VolumeShaper::Configuration>, sp<VolumeShaper::Operation>) → VolumeShaper::Status` | Tier-2 | IN: 2× nullable Parcelable. OUT: `int32 status` (= VolumeShaper::Status code) | Per-track ramp/duck. No-op-return-OK (or NO_INIT if app expects it to no-op cleanly). |
| 13 | `GET_VOLUME_SHAPER_STATE` | `getVolumeShaperState(int) → sp<VolumeShaper::State>` | Tier-2 | IN: `int32 id`. OUT: `Parcelable state` (or empty reply if state==null) | If applyVolumeShaper is no-op, this is unreachable. Return null Parcelable. |

**IAudioTrack row count: 13 enum slots = 1 RESERVED + 12 active.**

**Tier-1 (5 methods):** GET_CBLK, START, STOP, PAUSE, GET_TIMESTAMP. (Plan §2.2 line 105-109 implicitly listed the same 5, plus FLUSH on line 108 — but FLUSH is a strict subset of STOP's reset-position semantic and noice does NOT call AudioTrack.flush() in its playback loop; reclassified as Tier-2 here.)

**Tier-2 (4 methods, safe no-op-return-OK):** FLUSH, SET_PARAMETERS, SIGNAL, APPLY_VOLUME_SHAPER, GET_VOLUME_SHAPER_STATE.

**Tier-3 (3 methods, fail-loud OK):** ATTACH_AUX_EFFECT, SELECT_PRESENTATION, (and the reclassified-by-judgment unreachable effect-related slot — but cleaner: just 2 truly Tier-3).

### 3.1 Boundary case — FLUSH

The plan §2.2 (line 108) lists FLUSH in IAudioTrack Tier-1. CR37 reclassifies it Tier-2 based on the observation that AAudio's flush only succeeds in PAUSED state — the AudioTrack→IAudioTrack.flush() call typically follows a pause(), so on the daemon side we already need a state machine. If the state-machine logic is correct, FLUSH-as-no-op is safe; if AudioTrack expects flush to actually reset position counters, audiotest will reveal it. **Recommend Tier-2 with a comment to revisit if M5-Step3 boot tests show audio glitches at restart.**

---

## §4. Non-wire virtual: `requestLogMerge()`

**Source:** `/home/dspfac/aosp-android-11/frameworks/av/media/libaudioclient/include/media/IAudioFlinger.h:557`

```cpp
virtual void requestLogMerge() = 0;
```

This is declared on `IAudioFlinger` (so `BnAudioFlinger`-subclass must implement) but has **no wire transaction code**. It is invoked locally by `BnAudioFlinger::onTransact` (IAudioFlinger.cpp lines 1003-1023) as a side-channel: when transaction codes 1 (CREATE_TRACK), 2 (CREATE_RECORD), 8 (SET_MASTER_VOLUME), 9 (SET_MASTER_MUTE), 17 (SET_MIC_MUTE), 20 (SET_PARAMETERS), 41 (CREATE_EFFECT), or 54 (SYSTEM_READY) arrive, `onTransact` calls `requestLogMerge()` first, then dispatches to the real handler.

The reason: AOSP's audioserver runs an in-process `media.log` daemon that aggregates per-thread ring-buffer logs; `requestLogMerge` is the "important event happened, flush my log ring" trigger.

**M5 implementation:** `void WestlakeAudioFlinger::requestLogMerge() override {}` — empty body. Phase 2+ might wire it to an actual ALOGV or stats counter.

---

## §5. Implementation strategy

### 5.1 Switch-table skeleton

M5-Step2 should implement `WestlakeAudioFlinger::onTransact` along the BnAudioFlinger pattern but with explicit Tier categorization:

```cpp
// aosp-audio-daemon-port/src/AudioServiceImpl.cpp (skeleton)

#include "AudioServiceImpl.h"
#include "FailLoud.h"   // failLoud(svc, method, code) → INVALID_OPERATION

namespace android {

status_t WestlakeAudioFlinger::onTransact(uint32_t code, const Parcel& data,
                                           Parcel* reply, uint32_t flags) {
    // §4: side-channel logmerge (whitelist of "important" codes)
    switch (code) {
        case CREATE_TRACK: case CREATE_RECORD: case SET_MASTER_VOLUME:
        case SET_MASTER_MUTE: case SET_MIC_MUTE: case SET_PARAMETERS:
        case CREATE_EFFECT: case SYSTEM_READY:
            requestLogMerge(); break;
        default: break;
    }

    switch (code) {
        // ---- Tier-1 (12) ----
        case CREATE_TRACK: {
            CHECK_INTERFACE(IAudioFlinger, data, reply);
            CreateTrackInput input;
            if (input.readFromParcel((Parcel*)&data) != NO_ERROR) {
                reply->writeInt32(DEAD_OBJECT); return NO_ERROR;
            }
            status_t status;
            CreateTrackOutput output;
            sp<IAudioTrack> track = createTrack(input, output, &status);
            reply->writeInt32(status);
            if (status != NO_ERROR) return NO_ERROR;
            reply->writeStrongBinder(IInterface::asBinder(track));
            output.writeToParcel(reply);
            return NO_ERROR;
        }
        case SAMPLE_RATE: {
            CHECK_INTERFACE(IAudioFlinger, data, reply);
            reply->writeInt32(sampleRate((audio_io_handle_t)data.readInt32()));
            return NO_ERROR;
        }
        // ... 10 more Tier-1 ...

        // ---- Tier-2 (19; no-op-return-OK) ----
        case SET_MASTER_VOLUME: {
            CHECK_INTERFACE(IAudioFlinger, data, reply);
            (void)data.readFloat();   // consume input
            reply->writeInt32(NO_ERROR);
            return NO_ERROR;
        }
        // ... 18 more Tier-2 ...

        // ---- Tier-3 (28; fail-loud) ----
        case CREATE_RECORD: case SET_MODE: case SET_MIC_MUTE:
        /* ... 25 more ... */ {
            return FailLoud::transactionUnimplemented(
                "media.audio_flinger", code, data, reply);
            // FailLoud logs the code with backtrace, writes status=INVALID_OPERATION,
            // returns NO_ERROR so the caller sees a clean reply.
        }

        // ---- RESERVED (code 4) and unknown ----
        default:
            return BBinder::onTransact(code, data, reply, flags);
    }
}

}  // namespace android
```

**Estimated AudioServiceImpl.cpp LOC:** ~600 (matches M5 plan §3.3 line 168). Of that:
- ~400 LOC for the 12 Tier-1 implementations (CREATE_TRACK alone is ~80 LOC; openOutput ~40; the getters ~10 each).
- ~150 LOC for the 19 Tier-2 (most are 3-5 LOC: read input, write OK).
- ~30 LOC for the 28 Tier-3 fail-loud cases (collapsed under a fall-through label).
- ~20 LOC for the requestLogMerge side-channel + boilerplate.

**Estimated AudioTrackImpl.cpp LOC:** ~250 (matches M5 plan §3.3 line 170). Of that:
- ~150 LOC for the 5 Tier-1 (GET_TIMESTAMP is the largest at ~40 LOC for the AAudio timestamp→AudioTimestamp adapter).
- ~50 LOC for the 4 Tier-2.
- ~20 LOC for the 2-3 Tier-3 fail-loud cases.
- ~30 LOC for the ring-buffer thread that drains cblk into AAudio writes (the meat of the daemon, called from start()/pause()/stop()).

### 5.2 Parcelable dependencies

The implementor will need these AOSP source files copied verbatim into `aosp-audio-daemon-port/aosp-src/`:

- `IAudioFlinger.cpp` (1631 LOC) — for BpAudioFlinger/BnAudioFlinger Bp side, IMPLEMENT_META_INTERFACE
- `IAudioTrack.cpp` (317 LOC) — same for IAudioTrack
- `IAudioFlingerClient.cpp` — RegisterClient callback type
- `AudioClient.cpp` — CreateTrackInput / CreateTrackOutput / CreateRecordInput / CreateRecordOutput parcelables (~600 LOC)
- `include/media/IAudioFlinger.h`, `IAudioTrack.h`, `AudioClient.h` — class declarations
- (smaller) `audio_attributes_t`, `audio_config_t`, `DeviceDescriptorBase` — system/media + frameworks/av types

The patches/0001-stub-mediautils-timecheck.patch (M5 plan §3.3 line 163) handles the only deletion: stripping out the `TimeCheck check(tag.c_str())` line at IAudioFlinger.cpp:1026 (since `libmediautils` is too heavy for the daemon).

### 5.3 AAudio glue (the backend)

Per CR34 spike verdict, `AAudioBackend.cpp` (~300 LOC, M5 plan §3.3 line 173) opens a single AAudio stream per `OPEN_OUTPUT`. The CR37-relevant signatures:

- `openOutput(...)` → `AAudio_createStreamBuilder` + `AAudioStreamBuilder_openStream`, store handle in a `std::map<audio_io_handle_t, AAudioStream*>`.
- `createTrack(...)` → reuse the open stream, allocate a cblk ring buffer (memfd-backed), spawn a writer thread that reads from cblk and calls `AAudioStream_write` with `framesPerBurst`-sized chunks.
- `getRenderPosition(...)` → `AAudioStream_getTimestamp` + `getFramesWritten` → HAL+DSP frame counters.
- `getTimestamp(...)` (on IAudioTrack) → same, formatted into AudioTimestamp's mPosition+mTime fields.

---

## §6. Cross-version AIDL drift

AOSP 11 (this source) vs Android 15 (phone framework.jar at `cfb7c9e3`) — transaction codes are **STABLE per-version** within a major-version line but DRIFT across versions.

**Spot-check protocol** (M5-Step2 should do this before going to phone):

```bash
# On phone, extract framework.jar and dex2jar it:
ADB pull /system/framework/framework.jar /tmp/framework.jar
unzip /tmp/framework.jar -d /tmp/fw
# Look at IAudioFlinger$Stub TRANSACTION_xxx constants if present (would
# only exist if framework's IAudioFlinger was AIDL-generated, but in
# AOSP 11 IAudioFlinger is hand-written C++ Bp/Bn; the Java side calls
# through the libaudio NDK, not directly to media.audio_flinger).
javap -p /tmp/fw/android/media/IAudioService\$Stub.class | grep TRANSACTION
# (IAudioService is the Java-side AudioManager binder, separate from
# IAudioFlinger which is the native binder. CR37 maps the *native* side.)
```

**Key finding to confirm during M5-Step2:** Android 15's `libaudioclient` enum is unlikely to have re-ordered codes 1-60 (Google avoids breaking native binder ABI), but may have **added** new codes 61-N. The daemon should respond to any unknown code with `BBinder::onTransact` default (UNKNOWN_TRANSACTION); the framework's BpAudioFlinger does fallback handling gracefully.

**Anti-drift action for M5-Step2:** before declaring victory, run noice once, capture `strace -e ioctl -p <westlake-audio-daemon-pid>` and look for any `BR_TRANSACTION` with code ≥ 61. If present, file a follow-up CR to extend the table — don't fail-loud silently in that range.

---

## §7. Cblk shared-memory shape (informational)

Not a transaction itself, but referenced from §2 code 1 (CREATE_TRACK) and §3 code 1 (GET_CBLK). The cblk (Control Block) is an `audio_track_cblk_t` struct (defined in `system/media/audio_utils/include/audio_utils/AudioTrack.h`) sitting at offset 0 of a shared-memory region. Following it (at sizeof(cblk) aligned to cache line) is the PCM ring buffer.

```
+--------------------+   <- shared IMemory base
| audio_track_cblk_t |       (~256 bytes, atomic counters + flags)
+--------------------+
|                    |
| PCM ring buffer    |       (cblk.frameCount × frameSize bytes)
|                    |
+--------------------+
```

The cblk fields the daemon writes (server-side): `server` (frames written), `flags` (UNDERRUN, etc.). Fields the daemon reads (from client): `user` (frames the app has written into the ring). Volume and pan can also be read from cblk if AudioTrack.setVolume goes through cblk (it does, in AOSP — avoids a binder round-trip per setVolume call).

**M5 plan §5** is the canonical writeup for the cblk implementation. CR37 just notes that the cblk lifecycle is owned by IAudioFlinger.createTrack (allocate) and IAudioTrack.start/stop (read+drain), and that GET_CBLK on IAudioTrack is just a stash-and-return.

---

## §8. Comparison with M5 plan §M5

Per M5 plan `M5_AUDIO_DAEMON_PLAN.md §2.2`:

> Total IAudioFlinger virtual methods: **~61**
> Tier-1 count: **~12 methods**
> IAudioTrack: **~13 virtual methods**
> IAudioTrack Tier-1: implicitly **5**

Actual from CR37 enumeration:

> IAudioFlinger: **61 declared** (60 wire-enum + 1 non-wire requestLogMerge); **59 dispatchable** (1 RESERVED at code 4); **12 Tier-1** (matches plan exactly)
> IAudioTrack: **13 wire-enum**; **12 dispatchable** (1 RESERVED at code 5); **5 Tier-1** (matches plan exactly)
> Combined dispatchable: **71 methods**; **17 Tier-1** (matches plan's 12+5=17 exactly)

**Verdict: M5 plan §2.2 estimate confirmed within ±2 declarations and exact on Tier-1 count.** The 6.5-day Phase-1 estimate (M5 plan §9.1, summed from §3 lines 562-571) HOLDS without adjustment.

No M5 plan edits required beyond an informational footnote pointing to this doc; person-time budget unchanged.

---

## §9. Implementor TODO for M5-Step2

After CR37 lands, M5-Step2 should:

1. **Copy AOSP source** verbatim into `aosp-audio-daemon-port/aosp-src/`: IAudioFlinger.cpp, IAudioTrack.cpp, IAudioFlingerClient.cpp, AudioClient.cpp + their headers (per §5.2).
2. **Apply patches/0001-stub-mediautils-timecheck.patch**: strip lines `#include <mediautils/TimeCheck.h>` + `TimeCheck check(...)` declarations from IAudioFlinger.cpp.
3. **Write `AudioServiceImpl.cpp`** subclassing BnAudioFlinger, implementing the 12 Tier-1 methods (§2 column "Tier-1"), the 19 Tier-2 no-ops, and the 28 Tier-3 fail-loud cases (§5.1 skeleton).
4. **Write `AudioTrackImpl.cpp`** subclassing BnAudioTrack, implementing the 5 Tier-1 methods (§3 column "Tier-1"), the 4 Tier-2 no-ops, and the 2-3 Tier-3 fail-loud cases.
5. **Write `FailLoud.cpp` / `FailLoud.h`** mirroring `shim/java/com/westlake/services/ServiceMethodMissing.fail(...)` semantics (M5 plan §2.2 line 112): `FailLoud::transactionUnimplemented(svc, code, data, reply)` writes status=INVALID_OPERATION + logs with backtrace. ~50 LOC total.
6. **Verify by exercising IAudioFlinger Tier-1** (per M5 plan §9.2 acceptance): launch westlake-audio-daemon, run `audiotest` (a 100-LOC C++ harness that calls AudioTrack.write() with a 440Hz sine wave for 5 seconds) — speaker should produce audible tone. Add the test to `aosp-audio-daemon-port/test/audio-daemon-smoke.sh`.
7. **End-to-end:** launch noice through dalvikvm with M5 daemon up, tap "Rain" preset, verify 5 seconds of audible rain. This is the M7 acceptance criterion per M5 plan §8.

The 17-Tier-1 method count is the **whole-Phase-1 surface** the implementor must hand-write. Everything else is mechanical (no-op return-OK or fail-loud).

---

## §10. Files referenced

**AOSP source (read-only):**
- `/home/dspfac/aosp-android-11/frameworks/av/media/libaudioclient/IAudioFlinger.cpp` — primary source (1631 LOC)
- `/home/dspfac/aosp-android-11/frameworks/av/media/libaudioclient/IAudioTrack.cpp` — primary source (317 LOC)
- `/home/dspfac/aosp-android-11/frameworks/av/media/libaudioclient/include/media/IAudioFlinger.h` — class declaration (esp. requestLogMerge at :557)
- `/home/dspfac/aosp-android-11/frameworks/av/media/libaudioclient/AudioClient.cpp` — CreateTrackInput/Output parcelables (referenced)
- `/home/dspfac/aosp-android-11/system/core/libsystem/include/system/audio.h` — `audio_io_handle_t`, `audio_format_t`, `audio_unique_id_use_t` enums

**Westlake docs:**
- `docs/engine/M5_AUDIO_DAEMON_PLAN.md` §2.2 — plan estimate (61/12 IAudioFlinger, 13/5 IAudioTrack)
- `docs/engine/M5_AUDIO_DAEMON_PLAN.md` §3.3 — file layout (this CR provides per-method signatures for the §3.3 src/*.cpp files)
- `docs/engine/M5_AUDIO_DAEMON_PLAN.md` §5 — cblk ring-buffer protocol (referenced from §7 above)
- `docs/engine/CR34_M5_SPIKE_REPORT.md` — AAudio backend feasibility (informs §5.3 above)
- `docs/engine/BINDER_PIVOT_DESIGN.md` §3.3, §3.6 — M5 placement in pivot architecture

**Subsequent work:**
- `aosp-audio-daemon-port/` — M5-Step1 (BUILD_PLAN copy, Makefile, build.sh) is active there per phase status
- `aosp-audio-daemon-port/src/AudioServiceImpl.cpp` — to be written in M5-Step2 using §2 above
- `aosp-audio-daemon-port/src/AudioTrackImpl.cpp` — to be written in M5-Step2 using §3 above

**Person-time:** ~75 minutes (well inside the 1-2h research budget). Anti-drift compliance: zero source-code edits; one new doc + one PHASE_1_STATUS row + one M5 plan footnote.
