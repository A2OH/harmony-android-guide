// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M5 audio daemon (Step 2: IAudioTrack Bn-side stub).
//
// See WestlakeAudioTrack.h for the contract.  Per-method handlers mirror
// AOSP-11 BnAudioTrack::onTransact (frameworks/av/media/libaudioclient/
// IAudioTrack.cpp:216-315) with the cblk path stubbed (returns null IMemory)
// and AAudioStream as the playback engine.

#include "WestlakeAudioTrack.h"

#include <stdio.h>
#include <time.h>

#include <binder/IInterface.h>
#include <binder/Parcel.h>

namespace android {

// AOSP IAudioTrack interface descriptor — preserved verbatim so peers using
// BpBinder::getInterfaceDescriptor() see the canonical name.
//   AOSP source: IAudioTrack.cpp line 31 (DECLARE_META_INTERFACE installs).
static const String16 kIfaceDescriptor("android.media.IAudioTrack");

WestlakeAudioTrack::WestlakeAudioTrack(AudioHelper::Stream* stream)
    : mStream(stream) {
    fprintf(stderr, "[wlk-at] WestlakeAudioTrack constructed stream=%p\n",
            (void*)mStream);
}

WestlakeAudioTrack::~WestlakeAudioTrack() {
    fprintf(stderr, "[wlk-at] WestlakeAudioTrack destructed\n");
    if (mStream != nullptr) {
        AudioHelper::close(mStream);
        mStream = nullptr;
    }
}

const String16& WestlakeAudioTrack::getInterfaceDescriptor() const {
    return kIfaceDescriptor;
}

int32_t WestlakeAudioTrack::sampleRate() const {
    return mStream != nullptr ? mStream->sampleRate : 48000;
}
int32_t WestlakeAudioTrack::channelCount() const {
    return mStream != nullptr ? mStream->channelCount : 2;
}
int32_t WestlakeAudioTrack::framesPerBurst() const {
    return mStream != nullptr ? mStream->framesPerBurst : 240;
}
int32_t WestlakeAudioTrack::bufferCapacityFrames() const {
    return mStream != nullptr ? mStream->bufferCapacityFrames : 3840;
}
int32_t WestlakeAudioTrack::format() const {
    return mStream != nullptr ? AudioHelper::aospFormat(mStream)
                              : /*PCM_16_BIT*/ 0x1;
}

// ---------------------------------------------------------------------------
// Per-transaction handlers
// ---------------------------------------------------------------------------

// GET_CBLK -> sp<IMemory>.  Phase 1: we don't have a cblk ring buffer yet
// (M5-Step3 follow-up); return a null strong binder.  Apps gating on
// non-null cblk will fall back to the Binder write path (WLK_WRITE_FRAMES).
status_t WestlakeAudioTrack::onGetCblk(const Parcel& data, Parcel* reply) {
    (void)data;
    fprintf(stderr, "[wlk-at] GET_CBLK -> null IMemory (Phase-1; M5-Step3 will wire cblk)\n");
    if (reply != nullptr) {
        reply->writeStrongBinder(nullptr);
    }
    return NO_ERROR;
}

// START -> status_t.  Engage AAudio's stream.
status_t WestlakeAudioTrack::onStart(const Parcel& data, Parcel* reply) {
    (void)data;
    int rc = AudioHelper::start(mStream);
    int32_t status = (rc == 0) ? NO_ERROR : (status_t)-EINVAL;
    if (reply != nullptr) reply->writeInt32(status);
    fprintf(stderr, "[wlk-at] START -> %d\n", status);
    return NO_ERROR;
}

// STOP -> void.  AOSP BpAudioTrack::stop ignores reply payload.
status_t WestlakeAudioTrack::onStop(const Parcel& data, Parcel* reply) {
    (void)data;
    (void)reply;
    AudioHelper::stop(mStream);
    fprintf(stderr, "[wlk-at] STOP\n");
    return NO_ERROR;
}

// PAUSE -> void.
status_t WestlakeAudioTrack::onPause(const Parcel& data, Parcel* reply) {
    (void)data;
    (void)reply;
    AudioHelper::pause(mStream);
    fprintf(stderr, "[wlk-at] PAUSE\n");
    return NO_ERROR;
}

// FLUSH -> void.  Per CR37 §3.1: AAudio's flush only succeeds in PAUSED;
// we attempt unconditionally and accept failure.
status_t WestlakeAudioTrack::onFlush(const Parcel& data, Parcel* reply) {
    (void)data;
    (void)reply;
    AudioHelper::flush(mStream);
    fprintf(stderr, "[wlk-at] FLUSH\n");
    return NO_ERROR;
}

// GET_TIMESTAMP -> status_t + AudioTimestamp.  AOSP-11 wire format:
//   int32 status; if(status==OK) { int32 position; int32 tv_sec; int32 tv_nsec; }
// Position is in frames; time pair is CLOCK_MONOTONIC.  Per CR37 §3 code 10.
status_t WestlakeAudioTrack::onGetTimestamp(const Parcel& data, Parcel* reply) {
    (void)data;
    if (reply == nullptr) return NO_ERROR;
    int64_t fw = AudioHelper::getFramesWritten(mStream);
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    reply->writeInt32(NO_ERROR);
    // mPosition truncates to int32 — same as AOSP-11 IAudioTrack.cpp.
    reply->writeInt32((int32_t)(fw & 0x7fffffff));
    reply->writeInt32((int32_t)ts.tv_sec);
    reply->writeInt32((int32_t)ts.tv_nsec);
    fprintf(stderr, "[wlk-at] GET_TIMESTAMP pos=%lld ts=%lld.%09lld\n",
            (long long)fw, (long long)ts.tv_sec, (long long)ts.tv_nsec);
    return NO_ERROR;
}

// WLK_WRITE_FRAMES -> int32 frames written.  Westlake-private code that the
// shim's AudioTrack.write(...) issues until M5-Step3 lands the cblk.
//
// Wire format:
//   in:  int32 byteCount; <byteCount raw bytes>
//   out: int32 framesWritten (negative = AAudio status_t)
status_t WestlakeAudioTrack::onWriteFrames(const Parcel& data, Parcel* reply) {
    int32_t byteCount = 0;
    if (data.readInt32(&byteCount) != NO_ERROR || byteCount < 0) {
        fprintf(stderr, "[wlk-at] WLK_WRITE_FRAMES: bad byteCount\n");
        if (reply != nullptr) reply->writeInt32(-1);
        return NO_ERROR;
    }
    // Bound the size to keep a runaway client from exhausting memory.
    constexpr int32_t kMaxBytes = 16 * 1024 * 1024;
    if (byteCount > kMaxBytes) byteCount = kMaxBytes;
    const void* payload = data.readInplace((size_t)byteCount);
    if (payload == nullptr && byteCount > 0) {
        fprintf(stderr, "[wlk-at] WLK_WRITE_FRAMES: readInplace(%d) failed\n",
                byteCount);
        if (reply != nullptr) reply->writeInt32(-2);
        return NO_ERROR;
    }
    // Translate byteCount → frame count using current format & channels.
    // AudioHelper::Stream::format is the AOSP audio_format_t (1=PCM_16_BIT,
    // 5=PCM_FLOAT); see audio_helper.cc fromAAudioFormat().
    int32_t bytesPerSample = (mStream != nullptr &&
                              mStream->format == /*PCM_FLOAT*/ 5)
                                ? 4 : 2;
    int32_t channels = mStream != nullptr ? mStream->channelCount : 1;
    if (channels < 1) channels = 1;
    int32_t frameBytes = bytesPerSample * channels;
    int32_t frames = (frameBytes > 0) ? (byteCount / frameBytes) : 0;
    // 2-second timeout per write — generous but not infinite.
    ssize_t r = AudioHelper::writeFrames(mStream, payload, frames,
                                            2L * 1000 * 1000 * 1000);
    fprintf(stderr,
            "[wlk-at] WLK_WRITE_FRAMES bytes=%d frames=%d -> %zd\n",
            byteCount, frames, r);
    if (reply != nullptr) reply->writeInt32((int32_t)r);
    return NO_ERROR;
}

// ---------------------------------------------------------------------------
// Top-level dispatch
// ---------------------------------------------------------------------------

status_t WestlakeAudioTrack::onTransact(uint32_t code,
                                        const Parcel& data,
                                        Parcel* reply,
                                        uint32_t flags) {
    // For our private WLK_WRITE_FRAMES code we don't expect an interface
    // header (the daemon's own shim writes the parcel without one), so do
    // the CHECK_INTERFACE only for AOSP-defined codes.
    if (code != WLK_WRITE_FRAMES) {
        CHECK_INTERFACE(IAudioTrack, data, reply);
    }
    switch (code) {
        // Tier-1
        case GET_CBLK:        return onGetCblk(data, reply);
        case START:           return onStart(data, reply);
        case STOP:            return onStop(data, reply);
        case PAUSE:           return onPause(data, reply);
        case GET_TIMESTAMP:   return onGetTimestamp(data, reply);
        // Tier-2 (no-op-return-OK; FLUSH is reclassified Tier-2 per CR37 §3.1)
        case FLUSH:           return onFlush(data, reply);
        case SET_PARAMETERS: {
            if (reply != nullptr) reply->writeInt32(NO_ERROR);
            return NO_ERROR;
        }
        case SIGNAL:          return NO_ERROR;
        case APPLY_VOLUME_SHAPER: {
            // VolumeShaper::Status code (== NO_ERROR == OK).
            if (reply != nullptr) reply->writeInt32(NO_ERROR);
            return NO_ERROR;
        }
        case GET_VOLUME_SHAPER_STATE: {
            // Null Parcelable.
            if (reply != nullptr) reply->writeInt32(0);
            return NO_ERROR;
        }
        // Tier-3 fail-loud (return INVALID_OPERATION).
        case ATTACH_AUX_EFFECT:
        case SELECT_PRESENTATION: {
            fprintf(stderr, "[wlk-at] Tier-3 code=%u INVALID_OPERATION\n", code);
            if (reply != nullptr) reply->writeInt32(INVALID_OPERATION);
            return NO_ERROR;
        }
        // Westlake extension.
        case WLK_WRITE_FRAMES: return onWriteFrames(data, reply);
        default:
            fprintf(stderr,
                    "[wlk-at] unknown code=%u flags=0x%x; falling through to BBinder\n",
                    code, flags);
            return BBinder::onTransact(code, data, reply, flags);
    }
}

}  // namespace android
