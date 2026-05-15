// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M5 audio daemon (Step 2: IAudioTrack Bn-side stub)
//
// IAudioTrack is the per-track Binder interface IAudioFlinger.CREATE_TRACK
// returns to the client.  CR37 §3 enumerates 13 enum slots (12 dispatchable,
// 5 Tier-1).  Phase-1 Tier-1 implementations:
//
//   GET_CBLK     -> returns null IMemory (Phase 1 skips the cblk ring;
//                    Phase 2 will allocate a memfd-backed audio_track_cblk_t
//                    per M5 plan §5).  Many apps will tolerate null cblk and
//                    fall back to the blocking-write Binder path.
//   START        -> AAudioStream_requestStart on the backend stream
//   STOP         -> AAudioStream_requestStop
//   PAUSE        -> AAudioStream_requestPause
//   GET_TIMESTAMP-> emits {framesWritten, monotonic-ns}
//
// Plus a synthetic non-AOSP code WRITE_FRAMES (the path the daemon will use
// for direct Binder write before cblk lands in Phase 2) — see §5 of the
// CR37 doc; the daemon writes through this code while the cblk is null.
//
// Anti-drift: zero per-app branches; same wire surface for every Android
// app that talks to a returned IAudioTrack proxy.
//
// Companion: docs/engine/CR37_M5_AIDL_DISCOVERY.md §3
//             docs/engine/M5_AUDIO_DAEMON_PLAN.md §3.3, §5
//             docs/engine/M5_STEP2_REPORT.md (this CR)

#ifndef WESTLAKE_AUDIO_TRACK_H
#define WESTLAKE_AUDIO_TRACK_H

#include <binder/Binder.h>
#include <utils/Errors.h>
#include <utils/String16.h>

#include "AudioHelper.h"

namespace android {

class WestlakeAudioTrack : public BBinder {
public:
    explicit WestlakeAudioTrack(AudioHelper::Stream* stream);
    ~WestlakeAudioTrack() override;

    status_t onTransact(uint32_t code, const Parcel& data, Parcel* reply,
                        uint32_t flags = 0) override;

    // Canonical AOSP descriptor "android.media.IAudioTrack" so peers that
    // call BpBinder::getInterfaceDescriptor() see the expected name.  AOSP
    // source: frameworks/av/media/libaudioclient/IAudioTrack.cpp:33-47
    const String16& getInterfaceDescriptor() const override;

    // CR37 §3 transaction codes.  Same as BnAudioTrack from AOSP 11.
    enum Tag : uint32_t {
        GET_CBLK            = 1,
        START               = 2,
        STOP                = 3,
        FLUSH               = 4,
        // 5 RESERVED (was MUTE)
        PAUSE               = 6,
        ATTACH_AUX_EFFECT   = 7,
        SET_PARAMETERS      = 8,
        SELECT_PRESENTATION = 9,
        GET_TIMESTAMP       = 10,
        SIGNAL              = 11,
        APPLY_VOLUME_SHAPER = 12,
        GET_VOLUME_SHAPER_STATE = 13,

        // Westlake-private extension.  Not in AOSP's BnAudioTrack enum.  The
        // daemon's framework-side shim can issue this from
        // android.media.AudioTrack.write(byte[]/short[]/float[]/buffer) to
        // bypass the cblk ring until M5-Step3 lands the shared-memory path.
        // Wire format:
        //   in:  int32 byteCount; <byteCount bytes raw>
        //   out: int32 framesWritten (negative = error)
        WLK_WRITE_FRAMES    = 0x57'4C'4B'01u,  // "WLK\1"
    };

    // Accessors so the daemon can inspect properties (CREATE_TRACK's reply
    // marshals these into CreateTrackOutput).
    int32_t sampleRate() const;
    int32_t channelCount() const;
    int32_t framesPerBurst() const;
    int32_t bufferCapacityFrames() const;
    int32_t format() const;
    AudioHelper::Stream* stream() const { return mStream; }

private:
    AudioHelper::Stream* mStream;  // owned; closed in destructor

    status_t onGetCblk(const Parcel& data, Parcel* reply);
    status_t onStart(const Parcel& data, Parcel* reply);
    status_t onStop(const Parcel& data, Parcel* reply);
    status_t onPause(const Parcel& data, Parcel* reply);
    status_t onFlush(const Parcel& data, Parcel* reply);
    status_t onGetTimestamp(const Parcel& data, Parcel* reply);
    status_t onWriteFrames(const Parcel& data, Parcel* reply);
};

}  // namespace android

#endif  // WESTLAKE_AUDIO_TRACK_H
