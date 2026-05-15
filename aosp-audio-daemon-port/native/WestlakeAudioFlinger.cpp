// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M5 audio daemon (Step 2: IAudioFlinger Tier-1 dispatch).
//
// Per CR37 §2 (`docs/engine/CR37_M5_AIDL_DISCOVERY.md`):
//   - 12 Tier-1 methods get real handlers (this CR).
//   - 19 Tier-2 methods get no-op-return-OK collapses.
//   - 28 Tier-3 methods get fail-loud (status=INVALID_OPERATION).
//   - 1 RESERVED slot (code 4) falls through to BBinder::onTransact default
//     (UNKNOWN_TRANSACTION).
//
// AAudio backend (CR34 spike-validated) provides the actual speaker output.
// Phase 1: single shared output (one AAudio stream); CREATE_TRACK reuses it.
//
// Anti-drift: zero per-app branches — same surface for every Android peer.

#include "WestlakeAudioFlinger.h"
#include "WestlakeAudioTrack.h"
#include "AudioHelper.h"


#include <stdio.h>
#include <string.h>

#include <binder/IInterface.h>
#include <binder/Parcel.h>
#include <utils/String8.h>
#include <utils/String16.h>

namespace android {

// AOSP-canonical descriptor.  On Android 11 this was
// IMPLEMENT_META_INTERFACE(AudioFlinger, "android.media.IAudioFlinger") in
// IAudioFlinger.cpp:931.  On Android 12+ libaudioclient switched to an
// AIDL-generated wire interface `android.media.IAudioFlingerService` (with a
// process-local `AudioFlingerClientAdapter` translating to the legacy
// `IAudioFlinger` C++ API).  The phone (cfb7c9e3 = LineageOS 22 / Android 15)
// ships the AIDL variant; we verified the symbols
// `_ZN7android5media20IAudioFlingerService10descriptorE` +
// `_ZN7android25AudioFlingerClientAdapter...IAudioFlingerService...` are
// present in `/system/lib64/libaudioclient.so` and the
// `Parcel: enforceInterface() expected 'android.media.IAudioFlinger' but
// read 'android.media.IAudioFlingerService'` daemon-log error in
// M5_STEP3_REPORT §5.2 (Builder mis-summarised the directionality in §5.3:
// the platform writes `IAudioFlingerService`; we must advertise the same).
static const String16 kIfaceDescriptor("android.media.IAudioFlingerService");

// Phase-1 canned constants (audible-tone path).  Match common AAudio defaults
// on the OnePlus 6 cfb7c9e3 per the CR34 spike output.
static constexpr int32_t kDefaultSampleRate    = 48000;
static constexpr int32_t kDefaultChannelCount  = 2;       // stereo (HAL upmix)
static constexpr int32_t kDefaultFormat        = 0x1;     // PCM_16_BIT
static constexpr int32_t kPrimaryIoHandleSeed  = 13;

WestlakeAudioFlinger::WestlakeAudioFlinger()
    : mPrimaryStream(nullptr),
      mPrimaryHandle(0),
      mNextUniqueId(100) {
    fprintf(stderr, "[wlk-af] WestlakeAudioFlinger constructed (Step-2 dispatch)\n");
}

WestlakeAudioFlinger::~WestlakeAudioFlinger() {
    fprintf(stderr, "[wlk-af] WestlakeAudioFlinger destructed\n");
    if (mPrimaryStream != nullptr) {
        AudioHelper::close(mPrimaryStream);
        mPrimaryStream = nullptr;
    }
}

const String16& WestlakeAudioFlinger::getInterfaceDescriptor() const {
    return kIfaceDescriptor;
}

void WestlakeAudioFlinger::requestLogMerge() {
    // Phase-1 no-op (CR37 §4); Phase-2+ may emit a stat.
}

// ---------------------------------------------------------------------------
// Output stream factory — Phase 1 keeps a single shared AAudio stream.  Both
// CREATE_TRACK (which wants its own IAudioTrack proxy) and OPEN_OUTPUT route
// here.  Phase 2 will add a per-track stream once cblk arrives.
// ---------------------------------------------------------------------------
int32_t WestlakeAudioFlinger::getOrCreateOutput(int32_t requestedSampleRate,
                                                int32_t requestedChannels,
                                                int32_t requestedFormat) {
    std::lock_guard<std::mutex> lk(mPrimaryLock);
    if (mPrimaryStream != nullptr) {
        return mPrimaryHandle;
    }
    AudioHelper::Stream* s = AudioHelper::openOutput(
        requestedSampleRate > 0 ? requestedSampleRate : kDefaultSampleRate,
        requestedChannels   > 0 ? requestedChannels   : kDefaultChannelCount,
        requestedFormat     > 0 ? requestedFormat     : kDefaultFormat);
    if (s == nullptr) {
        fprintf(stderr, "[wlk-af] getOrCreateOutput: AAudio openOutput FAILED\n");
        return 0;
    }
    mPrimaryStream = s;
    mPrimaryHandle = kPrimaryIoHandleSeed;
    return mPrimaryHandle;
}

// ---------------------------------------------------------------------------
// Tier-1 handlers
// ---------------------------------------------------------------------------

// CREATE_TRACK (code 1) — the heaviest transaction.  In AOSP the full
// signature is createTrack(CreateTrackInput&, CreateTrackOutput&, status_t*).
// Phase 1: we do NOT parse the AOSP CreateTrackInput Parcelable (it pulls in
// AttributionSourceState + audio_attributes_t + audio_config_t + IMemory and
// would add ~600 LOC of AOSP-src dependency per CR37 §5.2).  Instead we
// accept the input as opaque, allocate the primary AAudio stream with
// defaults, and reply with a minimal CreateTrackOutput envelope plus the
// IAudioTrack binder.  The shim (CR36 territory) writes the matching minimal
// CreateTrackInput on the BpAudioFlinger side.
//
// AOSP reply format (IAudioFlinger.cpp:1050):
//   int32 status; if (status==OK) { sp<IAudioTrack> track;
//                                   CreateTrackOutput.writeToParcel(reply); }
// where CreateTrackOutput contains (in order):
//   float speed; size_t frameCount; size_t notificationFrameCount;
//   audio_io_handle_t outputId; audio_port_handle_t portId;
//   sp<IMemory> sharedBuffer (often null); uint32_t flags;
//   audio_session_t sessionId; audio_stream_type_t streamType;
//   audio_attributes_t attributes; audio_config_base_t serverConfig;
//   audio_config_base_t clientConfig; uint32_t afFrameCount;
//   uint32_t afSampleRate; uint32_t afLatencyMs; int32_t afTrackFlags;
//   audio_port_handle_t selectedDeviceId; ... (Parcelable layout).
//
// For Phase 1 we write the minimal subset our companion shim cares about:
//   int32 status (0=OK)
//   StrongBinder track
//   int32 sampleRate
//   int32 channelCount
//   int32 format
//   int32 frameCount
//   int32 framesPerBurst
//   int32 bufferCapacityFrames
//   int32 latencyMs
//   int32 audio_io_handle
//   int32 audio_unique_id
// Future Step 3+ can pad to the full AOSP CreateTrackOutput when wiring real
// framework.jar BpAudioFlinger; cross-version drift (CR37 §6) flags this as
// the most fragile reply parcel.  The Westlake shim writes the matching
// reduced parcel.
status_t WestlakeAudioFlinger::onCreateTrack(const Parcel& data,
                                              Parcel* reply) {
    (void)data;
    int32_t ioHandle = getOrCreateOutput(0, 0, 0);  // accept AAudio defaults
    if (ioHandle == 0 || mPrimaryStream == nullptr) {
        fprintf(stderr, "[wlk-af] CREATE_TRACK: openOutput failed; reply BAD_VALUE\n");
        if (reply != nullptr) reply->writeInt32(BAD_VALUE);
        return NO_ERROR;
    }
    // Per CR37 §3 the daemon allocates a Bn-side IAudioTrack proxy that
    // wraps the backend stream.  Phase 1: one stream → one IAudioTrack;
    // M5-Step3 will revisit when multi-track / cblk lands.
    sp<WestlakeAudioTrack> track = sp<WestlakeAudioTrack>::make(mPrimaryStream);
    // Note: this transfers ownership of mPrimaryStream into the WAT; clear
    // our pointer so destructor doesn't double-free.  Subsequent
    // CREATE_TRACK calls in Phase 1 will reallocate.  (Anti-drift: this is
    // a Phase-1 limitation, NOT a per-app branch.)
    AudioHelper::Stream* s = mPrimaryStream;
    mPrimaryStream = nullptr;
    mPrimaryHandle = 0;
    int32_t sr   = s->sampleRate;
    int32_t ch   = s->channelCount;
    int32_t fmt  = AudioHelper::aospFormat(s);
    int32_t fpb  = s->framesPerBurst;
    int32_t cap  = s->bufferCapacityFrames;
    int32_t lat  = (int32_t)AudioHelper::latencyMs(s);
    int32_t uid  = mNextUniqueId.fetch_add(1);

    if (reply != nullptr) {
        reply->writeInt32(NO_ERROR);                    // status
        reply->writeStrongBinder(sp<IBinder>(track.get()));
        reply->writeInt32(sr);
        reply->writeInt32(ch);
        reply->writeInt32(fmt);
        reply->writeInt32(cap);                         // frameCount
        reply->writeInt32(fpb);                         // framesPerBurst
        reply->writeInt32(cap);                         // bufferCapacityFrames
        reply->writeInt32(lat);                         // latencyMs
        reply->writeInt32(ioHandle);                    // io handle
        reply->writeInt32(uid);                         // audio_unique_id
    }
    fprintf(stderr,
            "[wlk-af] CREATE_TRACK OK io=%d sr=%d ch=%d fmt=%d fpb=%d cap=%d lat=%dms uid=%d\n",
            ioHandle, sr, ch, fmt, fpb, cap, lat, uid);
    return NO_ERROR;
}

// SAMPLE_RATE(audio_io_handle) -> uint32_t
status_t WestlakeAudioFlinger::onSampleRate(const Parcel& data, Parcel* reply) {
    int32_t io = data.readInt32();
    (void)io;
    int32_t sr = (mPrimaryStream != nullptr) ? mPrimaryStream->sampleRate
                                              : kDefaultSampleRate;
    if (reply != nullptr) reply->writeInt32(sr);
    return NO_ERROR;
}

// FORMAT(audio_io_handle) -> audio_format_t
status_t WestlakeAudioFlinger::onFormat(const Parcel& data, Parcel* reply) {
    (void)data.readInt32();
    int32_t fmt = (mPrimaryStream != nullptr) ? AudioHelper::aospFormat(mPrimaryStream)
                                              : kDefaultFormat;
    if (reply != nullptr) reply->writeInt32(fmt);
    return NO_ERROR;
}

// FRAME_COUNT(audio_io_handle) -> size_t (int64 on wire)
status_t WestlakeAudioFlinger::onFrameCount(const Parcel& data, Parcel* reply) {
    (void)data.readInt32();
    int64_t fc = (mPrimaryStream != nullptr) ? mPrimaryStream->bufferCapacityFrames
                                              : 3840;
    if (reply != nullptr) reply->writeInt64(fc);
    return NO_ERROR;
}

// LATENCY(audio_io_handle) -> uint32_t
status_t WestlakeAudioFlinger::onLatency(const Parcel& data, Parcel* reply) {
    (void)data.readInt32();
    uint32_t ms = (mPrimaryStream != nullptr) ? AudioHelper::latencyMs(mPrimaryStream)
                                              : 80;
    if (reply != nullptr) reply->writeInt32((int32_t)ms);
    return NO_ERROR;
}

// REGISTER_CLIENT(sp<IAudioFlingerClient>) -> void.  Stash the IBinder ref
// so its death notification can fire if Phase 2 wires that; otherwise no-op.
status_t WestlakeAudioFlinger::onRegisterClient(const Parcel& data, Parcel* reply) {
    sp<IBinder> client = data.readStrongBinder();
    (void)client;  // Phase 1: don't keep a strong ref (would block client GC).
    (void)reply;
    fprintf(stderr, "[wlk-af] REGISTER_CLIENT (Phase-1 no-op)\n");
    return NO_ERROR;
}

// OPEN_OUTPUT(audio_module_handle, audio_config_t*, sp<DeviceDescriptorBase>,
//             uint32_t*, audio_output_flags_t) -> status_t + io_handle +
//             audio_config_t + latencyMs
// Phase-1 simplification: ignore inputs; create the AAudio stream; reply
// status=OK + io_handle + canned audio_config + latency.
status_t WestlakeAudioFlinger::onOpenOutput(const Parcel& data, Parcel* reply) {
    (void)data;
    int32_t ioHandle = getOrCreateOutput(0, 0, 0);
    if (reply == nullptr) return NO_ERROR;
    if (ioHandle == 0 || mPrimaryStream == nullptr) {
        reply->writeInt32(BAD_VALUE);
        return NO_ERROR;
    }
    reply->writeInt32(NO_ERROR);
    reply->writeInt32(ioHandle);
    // audio_config_t output (sample rate, channel mask, format).
    reply->writeInt32(mPrimaryStream->sampleRate);
    reply->writeInt32(mPrimaryStream->channelCount);
    reply->writeInt32(AudioHelper::aospFormat(mPrimaryStream));
    reply->writeInt32((int32_t)AudioHelper::latencyMs(mPrimaryStream));
    fprintf(stderr, "[wlk-af] OPEN_OUTPUT io=%d\n", ioHandle);
    return NO_ERROR;
}

// GET_RENDER_POSITION(audio_io_handle) -> status + halFrames + dspFrames
status_t WestlakeAudioFlinger::onGetRenderPosition(const Parcel& data, Parcel* reply) {
    (void)data.readInt32();
    if (reply == nullptr) return NO_ERROR;
    int64_t fw = (mPrimaryStream != nullptr)
                    ? AudioHelper::getFramesWritten(mPrimaryStream) : 0;
    reply->writeInt32(NO_ERROR);
    reply->writeInt32((int32_t)(fw & 0x7fffffff));      // halFrames
    reply->writeInt32((int32_t)(fw & 0x7fffffff));      // dspFrames (same in Phase 1)
    return NO_ERROR;
}

// NEW_AUDIO_UNIQUE_ID(audio_unique_id_use_t) -> audio_unique_id_t.  Monotonic.
status_t WestlakeAudioFlinger::onNewAudioUniqueId(const Parcel& data, Parcel* reply) {
    (void)data.readInt32();
    int32_t id = mNextUniqueId.fetch_add(1);
    if (reply != nullptr) reply->writeInt32(id);
    return NO_ERROR;
}

// GET_PRIMARY_OUTPUT_SAMPLING_RATE() -> uint32_t
status_t WestlakeAudioFlinger::onGetPrimaryOutputSamplingRate(const Parcel& data, Parcel* reply) {
    (void)data;
    int32_t sr = (mPrimaryStream != nullptr) ? mPrimaryStream->sampleRate
                                              : kDefaultSampleRate;
    if (reply != nullptr) reply->writeInt32(sr);
    return NO_ERROR;
}

// GET_PRIMARY_OUTPUT_FRAME_COUNT() -> size_t (int64)
status_t WestlakeAudioFlinger::onGetPrimaryOutputFrameCount(const Parcel& data, Parcel* reply) {
    (void)data;
    int64_t fpb = (mPrimaryStream != nullptr) ? mPrimaryStream->framesPerBurst : 240;
    if (reply != nullptr) reply->writeInt64(fpb);
    return NO_ERROR;
}

// MASTER_VOLUME() -> float.  Phase-1: return 1.0 (unattenuated).
status_t WestlakeAudioFlinger::onMasterVolume(const Parcel& data, Parcel* reply) {
    (void)data;
    if (reply != nullptr) reply->writeFloat(1.0f);
    return NO_ERROR;
}

// ---------------------------------------------------------------------------
// Top-level dispatch
// ---------------------------------------------------------------------------
status_t WestlakeAudioFlinger::onTransact(uint32_t code,
                                          const Parcel& data,
                                          Parcel* reply,
                                          uint32_t flags) {
    CHECK_INTERFACE(IAudioFlinger, data, reply);

    // §4 side-channel: 8 "important" codes trigger requestLogMerge before
    // dispatch.  Phase-1 no-op; keep the call site so wire ordering matches
    // AOSP-11 BnAudioFlinger::onTransact (IAudioFlinger.cpp:1009-1023).
    switch (code) {
        case CREATE_TRACK:        case CREATE_RECORD:
        case SET_MASTER_VOLUME:   case SET_MASTER_MUTE:
        case SET_MIC_MUTE:        case SET_PARAMETERS:
        case CREATE_EFFECT:       case SYSTEM_READY:
            requestLogMerge(); break;
        default: break;
    }

    switch (code) {
        // ---- Tier-1 (12 methods) ----
        case CREATE_TRACK:                       return onCreateTrack(data, reply);
        case SAMPLE_RATE:                        return onSampleRate(data, reply);
        case FORMAT:                             return onFormat(data, reply);
        case FRAME_COUNT:                        return onFrameCount(data, reply);
        case LATENCY:                            return onLatency(data, reply);
        case REGISTER_CLIENT:                    return onRegisterClient(data, reply);
        case OPEN_OUTPUT:                        return onOpenOutput(data, reply);
        case GET_RENDER_POSITION:                return onGetRenderPosition(data, reply);
        case NEW_AUDIO_UNIQUE_ID:                return onNewAudioUniqueId(data, reply);
        case GET_PRIMARY_OUTPUT_SAMPLING_RATE:   return onGetPrimaryOutputSamplingRate(data, reply);
        case GET_PRIMARY_OUTPUT_FRAME_COUNT:     return onGetPrimaryOutputFrameCount(data, reply);
        case MASTER_VOLUME:                      return onMasterVolume(data, reply);

        // ---- Tier-2 (no-op-return-OK; 19 methods + boundary cases) ----
        case SET_MASTER_VOLUME:
        case SET_MASTER_MUTE:
        case SET_STREAM_VOLUME:
        case SET_STREAM_MUTE:
        case SET_PARAMETERS:
        case CLOSE_OUTPUT:
        case SUSPEND_OUTPUT:
        case RESTORE_OUTPUT:
        case INVALIDATE_STREAM:
        case RELEASE_AUDIO_PATCH:
        case SET_AUDIO_PORT_CONFIG:
        case SET_LOW_RAM_DEVICE:
        case SET_MASTER_BALANCE:
        case SET_AUDIO_HAL_PIDS:
            if (reply != nullptr) reply->writeInt32(NO_ERROR);
            return NO_ERROR;

        case MASTER_MUTE:                       // bool
        case STREAM_MUTE:                       // bool
            if (reply != nullptr) reply->writeInt32(0);  // false
            return NO_ERROR;

        case STREAM_VOLUME:                     // float
            if (reply != nullptr) reply->writeFloat(1.0f);
            return NO_ERROR;

        case GET_MASTER_BALANCE:                // status + float
            if (reply != nullptr) { reply->writeInt32(NO_ERROR); reply->writeFloat(0.0f); }
            return NO_ERROR;

        case GET_PARAMETERS:                    // String8
            if (reply != nullptr) reply->writeString8(String8(""));
            return NO_ERROR;

        case ACQUIRE_AUDIO_SESSION_ID:          // void
        case RELEASE_AUDIO_SESSION_ID:          // void
            return NO_ERROR;

        case LOAD_HW_MODULE:                    // -> audio_module_handle
            (void)data;
            if (reply != nullptr) reply->writeInt32(1);  // canned "primary"
            return NO_ERROR;

        case LIST_AUDIO_PORTS:                  // status + numPorts(0)
        case LIST_AUDIO_PATCHES:                // status + numPatches(0)
            if (reply != nullptr) { reply->writeInt32(NO_ERROR); reply->writeInt32(0); }
            return NO_ERROR;

        case GET_AUDIO_PORT:                    // status
            if (reply != nullptr) reply->writeInt32(INVALID_OPERATION);
            return NO_ERROR;

        case CREATE_AUDIO_PATCH:                // status + audio_patch_handle
            if (reply != nullptr) { reply->writeInt32(NO_ERROR); reply->writeInt32(1); }
            return NO_ERROR;

        case SYSTEM_READY:                      // void
            return NO_ERROR;

        case FRAME_COUNT_HAL: {                 // io_handle -> int64
            (void)data.readInt32();
            int64_t fpb = (mPrimaryStream != nullptr) ? mPrimaryStream->framesPerBurst : 240;
            if (reply != nullptr) reply->writeInt64(fpb);
            return NO_ERROR;
        }

        // Boundary cases reclassified Tier-2 per CR37 §2.1
        case QUERY_NUM_EFFECTS:                 // status + 0
            if (reply != nullptr) { reply->writeInt32(NO_ERROR); reply->writeInt32(0); }
            return NO_ERROR;

        case GET_MICROPHONES:                   // status + empty parcelableVector
            if (reply != nullptr) { reply->writeInt32(NO_ERROR); reply->writeInt32(0); }
            return NO_ERROR;

        // ---- Tier-3 (fail-loud INVALID_OPERATION; 28 methods) ----
        case CREATE_RECORD:
        case SET_MODE:
        case SET_MIC_MUTE:
        case GET_MIC_MUTE:
        case SET_RECORD_SILENCED:
        case GET_INPUTBUFFERSIZE:
        case OPEN_DUPLICATE_OUTPUT:
        case OPEN_INPUT:
        case CLOSE_INPUT:
        case SET_VOICE_VOLUME:
        case GET_INPUT_FRAMES_LOST:
        case QUERY_EFFECT:
        case GET_EFFECT_DESCRIPTOR:
        case CREATE_EFFECT:
        case MOVE_EFFECTS:
        case GET_AUDIO_HW_SYNC_FOR_SESSION:
        case SET_EFFECT_SUSPENDED:
            fprintf(stderr, "[wlk-af] Tier-3 fail-loud code=%u flags=0x%x\n", code, flags);
            if (reply != nullptr) reply->writeInt32(INVALID_OPERATION);
            return NO_ERROR;

        // ---- RESERVED (code 4) and unknown codes ≥61 ----
        default:
            fprintf(stderr,
                    "[wlk-af] unknown/RESERVED code=%u flags=0x%x; falling through to BBinder\n",
                    code, flags);
            return BBinder::onTransact(code, data, reply, flags);
    }
}

}  // namespace android
