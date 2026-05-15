// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M5 audio daemon (Step 2: IAudioFlinger Tier-1 dispatch)
//
// Step 1 returned NO_ERROR with an empty reply for every transaction.  Step 2
// (this CR) implements the CR37 §2 Tier-1 IAudioFlinger surface (12 methods)
// per the M5_AUDIO_DAEMON_PLAN.md §3.3 split.  Tier-2 collapses to
// no-op-return-OK; Tier-3 collapses to fail-loud (INVALID_OPERATION); the
// RESERVED slot (code 4) falls through to BBinder::onTransact default
// (UNKNOWN_TRANSACTION).
//
// AAudio backend (CR34 spike-validated) provides actual speaker output via
// AudioHelper.  Per CR37 §5.3, openOutput creates one AAudio stream and
// CREATE_TRACK reuses it (Phase 1: single output total).
//
// Anti-drift: no per-app branches; same surface for every Android app that
// looks up "media.audio_flinger".
//
// Companion: docs/engine/CR37_M5_AIDL_DISCOVERY.md
//             docs/engine/M5_AUDIO_DAEMON_PLAN.md
//             docs/engine/M5_STEP2_REPORT.md (this CR)

#ifndef WESTLAKE_AUDIO_FLINGER_H
#define WESTLAKE_AUDIO_FLINGER_H

#include <atomic>
#include <mutex>

#include <binder/Binder.h>
#include <utils/Errors.h>
#include <utils/String16.h>

#include "AudioHelper.h"


namespace android {

class WestlakeAudioFlinger : public BBinder {
public:
    WestlakeAudioFlinger();
    ~WestlakeAudioFlinger() override;

    status_t onTransact(uint32_t code, const Parcel& data, Parcel* reply,
                        uint32_t flags = 0) override;

    // AOSP-canonical descriptor.  Android 11 used
    // "android.media.IAudioFlinger" (IAudioFlinger.cpp:931
    // IMPLEMENT_META_INTERFACE).  Android 12+ uses the AIDL-generated
    // wire interface "android.media.IAudioFlingerService"; the legacy
    // C++ `IAudioFlinger` API is reached process-locally via
    // `AudioFlingerClientAdapter`.  The phone target (cfb7c9e3 / A15)
    // uses the AIDL variant; see WestlakeAudioFlinger.cpp:33.
    const String16& getInterfaceDescriptor() const override;

    // Non-wire virtual on IAudioFlinger (CR37 §4); invoked locally as a
    // side-channel before dispatching codes 1/2/8/9/17/20/41/54.  Empty
    // body — Phase 2+ may wire to media.log.
    void requestLogMerge();

    // CR37 §2 IAudioFlinger transaction codes (AOSP 11
    // frameworks/av/media/libaudioclient/IAudioFlinger.cpp:34-95).  All
    // codes are FIRST_CALL_TRANSACTION + N, with FIRST_CALL_TRANSACTION = 1.
    enum Tag : uint32_t {
        CREATE_TRACK                  = 1,
        CREATE_RECORD                 = 2,
        SAMPLE_RATE                   = 3,
        // 4 RESERVED (was CHANNEL_COUNT)
        FORMAT                        = 5,
        FRAME_COUNT                   = 6,
        LATENCY                       = 7,
        SET_MASTER_VOLUME             = 8,
        SET_MASTER_MUTE               = 9,
        MASTER_VOLUME                 = 10,
        MASTER_MUTE                   = 11,
        SET_STREAM_VOLUME             = 12,
        SET_STREAM_MUTE               = 13,
        STREAM_VOLUME                 = 14,
        STREAM_MUTE                   = 15,
        SET_MODE                      = 16,
        SET_MIC_MUTE                  = 17,
        GET_MIC_MUTE                  = 18,
        SET_RECORD_SILENCED           = 19,
        SET_PARAMETERS                = 20,
        GET_PARAMETERS                = 21,
        REGISTER_CLIENT               = 22,
        GET_INPUTBUFFERSIZE           = 23,
        OPEN_OUTPUT                   = 24,
        OPEN_DUPLICATE_OUTPUT         = 25,
        CLOSE_OUTPUT                  = 26,
        SUSPEND_OUTPUT                = 27,
        RESTORE_OUTPUT                = 28,
        OPEN_INPUT                    = 29,
        CLOSE_INPUT                   = 30,
        INVALIDATE_STREAM             = 31,
        SET_VOICE_VOLUME              = 32,
        GET_RENDER_POSITION           = 33,
        GET_INPUT_FRAMES_LOST         = 34,
        NEW_AUDIO_UNIQUE_ID           = 35,
        ACQUIRE_AUDIO_SESSION_ID      = 36,
        RELEASE_AUDIO_SESSION_ID      = 37,
        QUERY_NUM_EFFECTS             = 38,
        QUERY_EFFECT                  = 39,
        GET_EFFECT_DESCRIPTOR         = 40,
        CREATE_EFFECT                 = 41,
        MOVE_EFFECTS                  = 42,
        LOAD_HW_MODULE                = 43,
        GET_PRIMARY_OUTPUT_SAMPLING_RATE = 44,
        GET_PRIMARY_OUTPUT_FRAME_COUNT   = 45,
        SET_LOW_RAM_DEVICE            = 46,
        LIST_AUDIO_PORTS              = 47,
        GET_AUDIO_PORT                = 48,
        CREATE_AUDIO_PATCH            = 49,
        RELEASE_AUDIO_PATCH           = 50,
        LIST_AUDIO_PATCHES            = 51,
        SET_AUDIO_PORT_CONFIG         = 52,
        GET_AUDIO_HW_SYNC_FOR_SESSION = 53,
        SYSTEM_READY                  = 54,
        FRAME_COUNT_HAL               = 55,
        GET_MICROPHONES               = 56,
        SET_MASTER_BALANCE            = 57,
        GET_MASTER_BALANCE            = 58,
        SET_EFFECT_SUSPENDED          = 59,
        SET_AUDIO_HAL_PIDS            = 60,
    };

private:
    // Open output: lazily creates the single shared AAudio stream.  Returns
    // an audio_io_handle_t (nonzero) the daemon hands out for OPEN_OUTPUT /
    // SAMPLE_RATE / FORMAT / ...  Phase 1: single output total.
    int32_t getOrCreateOutput(int32_t requestedSampleRate,
                              int32_t requestedChannels,
                              int32_t requestedFormat);

    // The single Phase-1 backend stream.  Created on first OPEN_OUTPUT or
    // CREATE_TRACK; reused by all subsequent queries.  Owned by `this`;
    // close in destructor.
    AudioHelper::Stream* mPrimaryStream;
    int32_t                mPrimaryHandle;        // nonzero audio_io_handle_t
    std::mutex             mPrimaryLock;

    // Monotonic counter for NEW_AUDIO_UNIQUE_ID.  Start at 100 per CR37
    // recommendation (avoid collision with HAL-assigned IDs ≤99).
    std::atomic<int32_t> mNextUniqueId;

    // ---- per-transaction handlers ----
    status_t onCreateTrack(const Parcel& data, Parcel* reply);
    status_t onSampleRate(const Parcel& data, Parcel* reply);
    status_t onFormat(const Parcel& data, Parcel* reply);
    status_t onFrameCount(const Parcel& data, Parcel* reply);
    status_t onLatency(const Parcel& data, Parcel* reply);
    status_t onRegisterClient(const Parcel& data, Parcel* reply);
    status_t onOpenOutput(const Parcel& data, Parcel* reply);
    status_t onGetRenderPosition(const Parcel& data, Parcel* reply);
    status_t onNewAudioUniqueId(const Parcel& data, Parcel* reply);
    status_t onGetPrimaryOutputSamplingRate(const Parcel& data, Parcel* reply);
    status_t onGetPrimaryOutputFrameCount(const Parcel& data, Parcel* reply);
    status_t onMasterVolume(const Parcel& data, Parcel* reply);
};

}  // namespace android

#endif  // WESTLAKE_AUDIO_FLINGER_H
