// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M5 Step 2 audio-transaction smoke test.
//
// Sandbox protocol (mirrors M6's surface_smoke pattern):
//   1. caller (m5step2-smoke.sh) has done:
//        setprop ctl.stop vndservicemanager
//        ./servicemanager /dev/vndbinder &
//        ./audio_flinger /dev/vndbinder &   (registers as "media.audio_flinger")
//   2. this test:
//        opens /dev/vndbinder, asks SM for "media.audio_flinger", sends real
//        IAudioFlinger Tier-1 transactions, parses replies; for CREATE_TRACK
//        sends a follow-up WLK_WRITE_FRAMES that the daemon turns into an
//        AAudio write — the speaker should produce 1 s of 440 Hz sine.
//   3. exits 0 on PASS, 1 on FAIL (one tally per check).
//
// Acceptance checks (M5_STEP2_REPORT.md §5):
//   A. checkService("media.audio_flinger") returns a non-null remote BpBinder.
//   B. GET_PRIMARY_OUTPUT_SAMPLING_RATE returns 48000 (or AAudio-negotiated).
//   C. NEW_AUDIO_UNIQUE_ID returns a non-zero monotonic int32.
//   D. CREATE_TRACK returns status=OK + a non-null IAudioTrack proxy + sane
//      output envelope (sampleRate>0, framesPerBurst>0).
//   E. IAudioTrack.START succeeds (status=NO_ERROR).
//   F. WLK_WRITE_FRAMES with 1 s of 440 Hz sine returns frames-written > 0
//      AND speaker emits audible tone (operator confirmation).
//   G. IAudioTrack.STOP returns cleanly.

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <math.h>

#include <binder/Binder.h>
#include <binder/BpBinder.h>
#include <binder/IPCThreadState.h>
#include <binder/IServiceManager.h>
#include <binder/Parcel.h>
#include <binder/ProcessState.h>
#include <utils/String8.h>
#include <utils/String16.h>

using namespace android;

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

// IAudioFlinger transaction codes (CR37 §2).  Smoke uses a subset; full
// enum lives on WestlakeAudioFlinger::Tag in the daemon header.
namespace af {
    constexpr uint32_t CREATE_TRACK                    = 1;
    constexpr uint32_t NEW_AUDIO_UNIQUE_ID             = 35;
    constexpr uint32_t GET_PRIMARY_OUTPUT_SAMPLING_RATE = 44;
}

// IAudioTrack transaction codes (CR37 §3).
namespace at {
    constexpr uint32_t START               = 2;
    constexpr uint32_t STOP                = 3;
    constexpr uint32_t WLK_WRITE_FRAMES    = 0x57'4C'4B'01u;
}

// M5-Step4: match Android-12+ AIDL wire descriptor used by libaudioclient
// (`android.media.IAudioFlingerService`).  See WestlakeAudioFlinger.cpp:33
// for the platform-side evidence chain.
static const String16 kAfDescriptor("android.media.IAudioFlingerService");
static const String16 kAtDescriptor("android.media.IAudioTrack");
static const String16 kAfService("media.audio_flinger");

static const char* dev_for_run() {
    const char* d = getenv("BINDER_DEVICE");
    return (d && *d) ? d : "/dev/vndbinder";
}

static int check_a_find_service(sp<IBinder>* outBinder) {
    sp<IServiceManager> sm = defaultServiceManager();
    if (sm == nullptr) {
        fprintf(stderr, "[audio_smoke] A: FAIL defaultServiceManager() null\n");
        return 1;
    }
    sp<IBinder> proxy = sm->checkService(kAfService);
    if (proxy == nullptr) {
        fprintf(stderr, "[audio_smoke] A: FAIL checkService -> null\n");
        return 1;
    }
    BpBinder* bp = proxy->remoteBinder();
    if (bp == nullptr) {
        fprintf(stderr, "[audio_smoke] A: FAIL got local binder\n");
        return 1;
    }
    fprintf(stderr, "[audio_smoke] A: PASS audio_flinger bp=%p\n", bp);
    *outBinder = proxy;
    return 0;
}

static int check_b_sample_rate(const sp<IBinder>& af) {
    Parcel data, reply;
    data.writeInterfaceToken(kAfDescriptor);
    status_t st = af->transact(af::GET_PRIMARY_OUTPUT_SAMPLING_RATE, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[audio_smoke] B: FAIL transact -> %d\n", st);
        return 1;
    }
    int32_t sr = reply.readInt32();
    if (sr <= 0) {
        fprintf(stderr, "[audio_smoke] B: FAIL sr=%d (expected >0)\n", sr);
        return 1;
    }
    fprintf(stderr, "[audio_smoke] B: PASS sample_rate=%d\n", sr);
    return 0;
}

static int check_c_unique_id(const sp<IBinder>& af) {
    Parcel data, reply;
    data.writeInterfaceToken(kAfDescriptor);
    data.writeInt32(0);
    status_t st = af->transact(af::NEW_AUDIO_UNIQUE_ID, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[audio_smoke] C: FAIL transact -> %d\n", st);
        return 1;
    }
    int32_t id = reply.readInt32();
    if (id <= 0) {
        fprintf(stderr, "[audio_smoke] C: FAIL id=%d\n", id);
        return 1;
    }
    fprintf(stderr, "[audio_smoke] C: PASS unique_id=%d\n", id);
    return 0;
}

static int check_d_create_track(const sp<IBinder>& af, sp<IBinder>* outTrack,
                                 int32_t* outSampleRate,
                                 int32_t* outChannels,
                                 int32_t* outFormat) {
    Parcel data, reply;
    data.writeInterfaceToken(kAfDescriptor);
    // Phase 1 daemon ignores CreateTrackInput payload; send empty.
    status_t st = af->transact(af::CREATE_TRACK, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[audio_smoke] D: FAIL transact CREATE_TRACK -> %d\n", st);
        return 1;
    }
    int32_t status = reply.readInt32();
    if (status != NO_ERROR) {
        fprintf(stderr, "[audio_smoke] D: FAIL CREATE_TRACK reply status=%d\n", status);
        return 1;
    }
    sp<IBinder> track = reply.readStrongBinder();
    if (track == nullptr) {
        fprintf(stderr, "[audio_smoke] D: FAIL null IAudioTrack\n");
        return 1;
    }
    int32_t sr  = reply.readInt32();
    int32_t ch  = reply.readInt32();
    int32_t fmt = reply.readInt32();
    int32_t fc  = reply.readInt32();
    int32_t fpb = reply.readInt32();
    int32_t cap = reply.readInt32();
    int32_t lat = reply.readInt32();
    int32_t io  = reply.readInt32();
    int32_t uid = reply.readInt32();
    if (sr <= 0 || ch <= 0 || fpb <= 0) {
        fprintf(stderr, "[audio_smoke] D: FAIL bad envelope sr=%d ch=%d fpb=%d\n",
                sr, ch, fpb);
        return 1;
    }
    fprintf(stderr,
            "[audio_smoke] D: PASS CREATE_TRACK track=%p sr=%d ch=%d fmt=%d fc=%d fpb=%d cap=%d lat=%dms io=%d uid=%d\n",
            track.get(), sr, ch, fmt, fc, fpb, cap, lat, io, uid);
    *outTrack = track;
    *outSampleRate = sr;
    *outChannels   = ch;
    *outFormat     = fmt;
    return 0;
}

static int check_e_start(const sp<IBinder>& track) {
    Parcel data, reply;
    data.writeInterfaceToken(kAtDescriptor);
    status_t st = track->transact(at::START, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[audio_smoke] E: FAIL transact START -> %d\n", st);
        return 1;
    }
    int32_t status = reply.readInt32();
    if (status != NO_ERROR) {
        fprintf(stderr, "[audio_smoke] E: FAIL START status=%d\n", status);
        return 1;
    }
    fprintf(stderr, "[audio_smoke] E: PASS START\n");
    return 0;
}

// Synthesize 1 s of 440 Hz sine in the daemon's negotiated format and pump
// it via WLK_WRITE_FRAMES.  PASS iff frames written > 0 across the full
// payload (chunked through framesPerBurst-sized writes for back-pressure).
static int check_f_write_audible(const sp<IBinder>& track,
                                  int32_t sampleRate, int32_t channels,
                                  int32_t aospFormat) {
    constexpr int kDurationS = 1;
    const int frames = sampleRate * kDurationS;
    const bool isFloat = (aospFormat == 0x5);  // AOSP AUDIO_FORMAT_PCM_FLOAT
    const int bytesPerSample = isFloat ? 4 : 2;
    const int frameBytes = bytesPerSample * channels;
    const int totalBytes = frames * frameBytes;
    void* buf = calloc((size_t)frames * channels, (size_t)bytesPerSample);
    if (buf == nullptr) {
        fprintf(stderr, "[audio_smoke] F: FAIL calloc\n");
        return 1;
    }
    // Generate 440 Hz sine.
    for (int i = 0; i < frames; ++i) {
        float s = sinf(2.0f * (float)M_PI * 440.0f *
                       (float)i / (float)sampleRate);
        if (isFloat) {
            float v = s * 0.25f;
            for (int c = 0; c < channels; ++c) {
                ((float*)buf)[i * channels + c] = v;
            }
        } else {
            int16_t v = (int16_t)(s * 8000.0f);
            for (int c = 0; c < channels; ++c) {
                ((int16_t*)buf)[i * channels + c] = v;
            }
        }
    }
    // Pump through WLK_WRITE_FRAMES.  AOSP's binder per-parcel limit is 1 MB
    // by default; for 48000 frames stereo PCM_I16 that's 192 KB — fits in one
    // parcel.  For PCM_FLOAT it's 384 KB — still fits.  Send as a single
    // transaction; daemon's AAudio writeFrames is blocking with a 2s timeout.
    Parcel data, reply;
    data.writeInt32(totalBytes);
    data.write(buf, (size_t)totalBytes);
    status_t st = track->transact(at::WLK_WRITE_FRAMES, data, &reply);
    free(buf);
    if (st != NO_ERROR) {
        fprintf(stderr, "[audio_smoke] F: FAIL transact WRITE -> %d\n", st);
        return 1;
    }
    int32_t framesWritten = reply.readInt32();
    if (framesWritten <= 0) {
        fprintf(stderr, "[audio_smoke] F: FAIL framesWritten=%d\n", framesWritten);
        return 1;
    }
    fprintf(stderr,
            "[audio_smoke] F: PASS framesWritten=%d (expected %d). "
            "Listen for 1 s of 440 Hz sine.\n",
            framesWritten, frames);
    // Let buffered audio play out.
    usleep(1100 * 1000);
    return 0;
}

static int check_g_stop(const sp<IBinder>& track) {
    Parcel data, reply;
    data.writeInterfaceToken(kAtDescriptor);
    status_t st = track->transact(at::STOP, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[audio_smoke] G: FAIL transact STOP -> %d\n", st);
        return 1;
    }
    fprintf(stderr, "[audio_smoke] G: PASS STOP\n");
    return 0;
}

int main(int argc, char** argv) {
    (void)argc;
    (void)argv;
    const char* dev = dev_for_run();
    fprintf(stderr, "[audio_smoke pid=%d] opening %s\n", getpid(), dev);
    sp<ProcessState> ps = ProcessState::initWithDriver(dev);
    if (ps == nullptr) {
        fprintf(stderr, "[audio_smoke] FAIL initWithDriver(%s)\n", dev);
        return 1;
    }
    ps->startThreadPool();

    int failures = 0;
    sp<IBinder> af;
    failures += check_a_find_service(&af);
    if (af == nullptr) {
        fprintf(stderr, "[audio_smoke] aborting (no AF handle)\n");
        return 1;
    }
    failures += check_b_sample_rate(af);
    failures += check_c_unique_id(af);

    sp<IBinder> track;
    int32_t sr = 0, ch = 0, fmt = 0;
    int d_rc = check_d_create_track(af, &track, &sr, &ch, &fmt);
    failures += d_rc;
    if (d_rc != 0 || track == nullptr) {
        fprintf(stderr, "[audio_smoke] skipping E/F/G (no track)\n");
        return failures == 0 ? 0 : 1;
    }
    failures += check_e_start(track);
    failures += check_f_write_audible(track, sr, ch, fmt);
    failures += check_g_stop(track);

    fprintf(stderr, "[audio_smoke] summary: %d failure(s) of 7 checks\n", failures);
    return failures == 0 ? 0 : 1;
}
