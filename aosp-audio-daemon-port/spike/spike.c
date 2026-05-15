// =============================================================================
// CR34 spike — M5 audio-daemon AAudio backend feasibility probe
// =============================================================================
//
// Risk being probed (per M5_AUDIO_DAEMON_PLAN.md §4.2):
//   "Phase 1 backend: Android AAudio NDK (libaaudio.so) — Westlake on a real
//    Android phone (OnePlus 6 cfb7c9e3) uses AAudio to feed the device's
//    actual speaker."  The 6.5-person-day estimate assumes this works from a
//    plain shell/daemon user-space (no system_server context, no MediaServer
//    selinux label, no APP_RECORD/PLAYBACK Java permissions).  If AAudio
//    refuses to open a stream from /data/local/tmp/-spawned processes, the
//    whole M5 architecture has to pivot — either run the daemon under a
//    privileged uid, switch to OpenSL ES (older NDK API; usually less
//    restricted), or fold AudioTrack code back into dalvikvm and skip the
//    daemon altogether.
//
// What this spike tests, in three independent probes:
//
//   Phase A — AAudio open-stream probe
//     AAudio_createStreamBuilder + set sample-rate=48000 / mono / PCM_I16 /
//     direction=OUTPUT / sharing=SHARED / performance=NONE, then
//     AAudioStreamBuilder_openStream.  Just opening.  No write, no audio
//     anywhere.  This is the gate question: does the AAudio service let us
//     open a stream from this uid/selinux-label?
//
//     Pass = we got an AAudioStream*.  This alone confirms the Phase 1
//     plan's architectural assumption.  Most failures here will be
//     AAUDIO_ERROR_INVALID_STATE, _INTERNAL, _PERMISSION_DENIED, or
//     _NO_SERVICE (Audio system busy / unavailable).
//
//   Phase B — AAudio actually-write probe
//     Generate 1 second of 440 Hz sine in 48 kHz / mono / s16; call
//     AAudioStream_requestStart, then AAudioStream_write (blocking with a
//     1-second timeout) until all frames consumed, then requestStop /
//     close.  The phone's speaker will emit an audible tone if you're
//     within earshot.
//
//     Pass = AAudioStream_write returns positive frames-consumed and the
//     stream state transitions through STARTING → STARTED → STOPPING →
//     STOPPED cleanly without error.  Phase B failures usually mean
//     AAUDIO_ERROR_DISCONNECTED (HAL refused) or AAUDIO_ERROR_TIMEOUT
//     (write blocked indefinitely — would mean the daemon would hang
//     too, which IS bad).
//
//   Phase C — OpenSL ES fallback probe
//     If Phase A/B reports anything wonky, OpenSL ES is the documented
//     alternative.  We don't actually need it for the verdict if AAudio
//     passes, but we probe it always so the report has data either way.
//     slCreateEngine + Realize + GetInterface(SL_IID_ENGINE) +
//     CreateOutputMix + CreateAudioPlayer with a BufferQueue source +
//     OutputMix sink + Realize the player; Enqueue 1s of the same 440Hz
//     tone; sleep; destroy.
//
//     Pass = the player Realize succeeds AND BufferQueue.Enqueue returns
//     SL_RESULT_SUCCESS.  An audible tone is the bonus.  Both APIs going
//     through the same audioserver, so any phone-side audio refusal will
//     hit both; an unequal result is informative.
//
// Verdict bundling:
//   A=PASS + B=PASS                  => AAudio fully viable; M5 6.5-day plan
//                                       estimate HOLDS, no pivot.
//   A=PASS + B=FAIL                  => can open but can't stream — exotic;
//                                       documentation-only follow-up to
//                                       Phase 1 plan §6 ("Bringup sequence").
//   A=FAIL + C=PASS                  => AAudio refuses our context but OpenSL
//                                       works; pivot M5 to OpenSL backend
//                                       (≈ +1 person-day for slightly more
//                                       boilerplate; same architectural shape).
//   A=FAIL + C=FAIL                  => any non-privileged NDK audio refused;
//                                       deeper pivot needed (uid=1000
//                                       audioserver context OR fold
//                                       AudioTrack into dalvikvm OR run the
//                                       daemon as platform_app).
//
// Exit codes:
//   0 — A=PASS + B=PASS                       (canonical verdict, M5 unblocked)
//   1 — A=PASS + B=FAIL                       (degenerate; document)
//   2 — A=FAIL + C=PASS                       (pivot to OpenSL ES)
//   3 — A=FAIL + C=FAIL                       (deeper pivot)
//   4 — process setup failed (couldn't dlopen libaaudio / libOpenSLES; very
//       unusual — Android 8+ ships both as platform libs)
//
// Build (see build.sh): NDK r25 bionic-arm64 toolchain, --target=android33,
// statically linked libc++.  No dependence on our libbinder.so or any of
// our own .so artifacts.  Single-file native binary.

#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <math.h>
#include <time.h>
#include <errno.h>

#include <aaudio/AAudio.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

// ----------------------------------------------------------------------------
// Test parameters
// ----------------------------------------------------------------------------
static const int kSampleRate    = 48000;
static const int kChannelCount  = 1;
static const float kToneFreqHz  = 440.0f;
static const float kToneAmp     = 8000.0f;   // about -12 dBFS for s16
static const int kToneDurationS = 1;         // 1 second of audio

// Generate kToneDurationS seconds of 440Hz mono s16 into caller-provided buf
// (which must hold kSampleRate*kToneDurationS int16_t samples).
static void generate_tone(int16_t* buf, int frames) {
    for (int i = 0; i < frames; i++) {
        buf[i] = (int16_t)(sinf(2.0f * (float)M_PI * kToneFreqHz *
                                 (float)i / (float)kSampleRate) * kToneAmp);
    }
}

// ============================================================================
// Phase A — AAudio open-stream probe
// ============================================================================
static int phase_a_open(AAudioStream** out_stream) {
    fprintf(stdout, "\n=== Phase A: AAudio open-stream probe ===\n");
    fflush(stdout);

    AAudioStreamBuilder* builder = NULL;
    aaudio_result_t r = AAudio_createStreamBuilder(&builder);
    if (r != AAUDIO_OK) {
        fprintf(stderr, "[A] AAudio_createStreamBuilder failed: %d (%s)\n",
                r, AAudio_convertResultToText(r));
        return -1;
    }
    fprintf(stdout, "[A] AAudio_createStreamBuilder OK\n");

    AAudioStreamBuilder_setDirection(builder, AAUDIO_DIRECTION_OUTPUT);
    AAudioStreamBuilder_setSampleRate(builder, kSampleRate);
    AAudioStreamBuilder_setChannelCount(builder, kChannelCount);
    AAudioStreamBuilder_setFormat(builder, AAUDIO_FORMAT_PCM_I16);
    AAudioStreamBuilder_setSharingMode(builder, AAUDIO_SHARING_MODE_SHARED);
    AAudioStreamBuilder_setPerformanceMode(builder,
                                           AAUDIO_PERFORMANCE_MODE_NONE);
    AAudioStreamBuilder_setUsage(builder, AAUDIO_USAGE_MEDIA);
    AAudioStreamBuilder_setContentType(builder, AAUDIO_CONTENT_TYPE_MUSIC);

    AAudioStream* stream = NULL;
    r = AAudioStreamBuilder_openStream(builder, &stream);
    if (r != AAUDIO_OK) {
        fprintf(stderr, "[A] FAIL AAudioStreamBuilder_openStream: %d (%s)\n",
                r, AAudio_convertResultToText(r));
        AAudioStreamBuilder_delete(builder);
        return -1;
    }
    fprintf(stdout, "[A] PASS openStream\n");

    // Report negotiated parameters; AAudio is allowed to substitute these.
    fprintf(stdout, "[A]   sample rate    : %d\n",
            AAudioStream_getSampleRate(stream));
    fprintf(stdout, "[A]   channel count  : %d\n",
            AAudioStream_getChannelCount(stream));
    fprintf(stdout, "[A]   format         : %d  (%d=PCM_I16, %d=PCM_FLOAT)\n",
            AAudioStream_getFormat(stream),
            AAUDIO_FORMAT_PCM_I16, AAUDIO_FORMAT_PCM_FLOAT);
    fprintf(stdout, "[A]   buffer capacity: %d frames\n",
            AAudioStream_getBufferCapacityInFrames(stream));
    fprintf(stdout, "[A]   buffer size    : %d frames\n",
            AAudioStream_getBufferSizeInFrames(stream));
    fprintf(stdout, "[A]   frames/burst   : %d\n",
            AAudioStream_getFramesPerBurst(stream));
    fprintf(stdout, "[A]   xrun count     : %d\n",
            AAudioStream_getXRunCount(stream));
    fprintf(stdout, "[A]   sharing mode   : %d\n",
            AAudioStream_getSharingMode(stream));
    fprintf(stdout, "[A]   performance    : %d\n",
            AAudioStream_getPerformanceMode(stream));
    fflush(stdout);

    AAudioStreamBuilder_delete(builder);
    *out_stream = stream;
    return 0;
}

// ============================================================================
// Phase B — AAudio actually-write probe
// ============================================================================
static int phase_b_write(AAudioStream* stream) {
    fprintf(stdout, "\n=== Phase B: AAudio actually-write probe ===\n");
    fflush(stdout);

    int total_frames = kSampleRate * kToneDurationS;

    // Note: AAudio's negotiated format may not be PCM_I16 even though we asked
    // for it (some HALs only do PCM_FLOAT).  Handle both.
    aaudio_format_t fmt = AAudioStream_getFormat(stream);
    int channels = AAudioStream_getChannelCount(stream);
    int frames_total = total_frames;     // mono -> 1 sample per frame

    void* buf = NULL;
    if (fmt == AAUDIO_FORMAT_PCM_I16) {
        int16_t* b16 = (int16_t*)calloc((size_t)frames_total * channels,
                                         sizeof(int16_t));
        if (!b16) { fprintf(stderr, "[B] calloc failed\n"); return -1; }
        // mono tone replicated across channels if HAL forced stereo
        for (int i = 0; i < frames_total; i++) {
            int16_t s = (int16_t)(sinf(2.0f * (float)M_PI * kToneFreqHz *
                                       (float)i / (float)kSampleRate) *
                                  kToneAmp);
            for (int c = 0; c < channels; c++) b16[i * channels + c] = s;
        }
        buf = b16;
    } else if (fmt == AAUDIO_FORMAT_PCM_FLOAT) {
        float* bf = (float*)calloc((size_t)frames_total * channels,
                                    sizeof(float));
        if (!bf) { fprintf(stderr, "[B] calloc failed\n"); return -1; }
        for (int i = 0; i < frames_total; i++) {
            float s = sinf(2.0f * (float)M_PI * kToneFreqHz *
                           (float)i / (float)kSampleRate) * 0.25f;
            for (int c = 0; c < channels; c++) bf[i * channels + c] = s;
        }
        buf = bf;
    } else {
        fprintf(stderr, "[B] unsupported negotiated format %d\n", fmt);
        return -1;
    }

    aaudio_result_t r = AAudioStream_requestStart(stream);
    if (r != AAUDIO_OK) {
        fprintf(stderr, "[B] FAIL requestStart: %d (%s)\n",
                r, AAudio_convertResultToText(r));
        free(buf);
        return -1;
    }
    fprintf(stdout, "[B] requestStart OK\n");

    // Wait until STARTED (or fail).
    aaudio_stream_state_t next = AAUDIO_STREAM_STATE_UNINITIALIZED;
    r = AAudioStream_waitForStateChange(stream, AAUDIO_STREAM_STATE_STARTING,
                                        &next, 2L * 1000 * 1000 * 1000);
    fprintf(stdout, "[B] state after start: r=%d next=%d (%d=STARTED)\n",
            r, next, AAUDIO_STREAM_STATE_STARTED);

    // Blocking write of all frames with a 2-second timeout (per chunk; AAudio
    // returns frames consumed and we loop).
    int written_total = 0;
    int chunk = AAudioStream_getFramesPerBurst(stream);
    if (chunk <= 0) chunk = 240;  // 5 ms @ 48k as a fallback
    int frame_bytes = (fmt == AAUDIO_FORMAT_PCM_I16 ? sizeof(int16_t)
                                                    : sizeof(float)) *
                      channels;
    while (written_total < frames_total) {
        int to_write = frames_total - written_total;
        if (to_write > chunk) to_write = chunk;
        char* p = (char*)buf + (size_t)written_total * (size_t)frame_bytes;
        int w = AAudioStream_write(stream, p, to_write,
                                   2L * 1000 * 1000 * 1000);
        if (w < 0) {
            fprintf(stderr, "[B] FAIL AAudioStream_write: %d (%s)\n",
                    w, AAudio_convertResultToText(w));
            AAudioStream_requestStop(stream);
            free(buf);
            return -1;
        }
        if (w == 0) {
            fprintf(stderr, "[B] FAIL write returned 0 (timeout / stalled)\n");
            AAudioStream_requestStop(stream);
            free(buf);
            return -1;
        }
        written_total += w;
    }
    fprintf(stdout, "[B] wrote %d / %d frames (full second)\n",
            written_total, frames_total);

    // Sleep enough to let buffered audio finish playing.
    usleep(1100 * 1000);

    r = AAudioStream_requestStop(stream);
    fprintf(stdout, "[B] requestStop: %d\n", r);

    free(buf);
    fprintf(stdout, "[B] PASS\n");
    fflush(stdout);
    return 0;
}

// ============================================================================
// Phase C — OpenSL ES fallback probe
// ============================================================================
static int phase_c_opensl(void) {
    fprintf(stdout, "\n=== Phase C: OpenSL ES fallback probe ===\n");
    fflush(stdout);

    SLObjectItf engineObj = NULL;
    SLEngineItf engine = NULL;
    SLObjectItf outputMix = NULL;
    SLObjectItf player = NULL;
    SLPlayItf playItf = NULL;
    SLAndroidSimpleBufferQueueItf bqItf = NULL;
    int16_t* tone = NULL;
    int rc = -1;

    SLresult r = slCreateEngine(&engineObj, 0, NULL, 0, NULL, NULL);
    if (r != SL_RESULT_SUCCESS) {
        fprintf(stderr, "[C] FAIL slCreateEngine: 0x%lx\n", (unsigned long)r);
        goto cleanup;
    }
    r = (*engineObj)->Realize(engineObj, SL_BOOLEAN_FALSE);
    if (r != SL_RESULT_SUCCESS) {
        fprintf(stderr, "[C] FAIL engine Realize: 0x%lx\n", (unsigned long)r);
        goto cleanup;
    }
    r = (*engineObj)->GetInterface(engineObj, SL_IID_ENGINE, &engine);
    if (r != SL_RESULT_SUCCESS) {
        fprintf(stderr, "[C] FAIL engine GetInterface: 0x%lx\n",
                (unsigned long)r);
        goto cleanup;
    }
    fprintf(stdout, "[C] engine realized\n");

    r = (*engine)->CreateOutputMix(engine, &outputMix, 0, NULL, NULL);
    if (r != SL_RESULT_SUCCESS) {
        fprintf(stderr, "[C] FAIL CreateOutputMix: 0x%lx\n",
                (unsigned long)r);
        goto cleanup;
    }
    r = (*outputMix)->Realize(outputMix, SL_BOOLEAN_FALSE);
    if (r != SL_RESULT_SUCCESS) {
        fprintf(stderr, "[C] FAIL outputMix Realize: 0x%lx\n",
                (unsigned long)r);
        goto cleanup;
    }
    fprintf(stdout, "[C] output mix realized\n");

    // Player source = Android-simple BufferQueue with PCM s16 mono 48k.
    SLDataLocator_AndroidSimpleBufferQueue locBq = {
        SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 1
    };
    SLDataFormat_PCM pcm = {
        SL_DATAFORMAT_PCM,
        (SLuint32)kChannelCount,
        SL_SAMPLINGRATE_48,
        SL_PCMSAMPLEFORMAT_FIXED_16,
        SL_PCMSAMPLEFORMAT_FIXED_16,
        SL_SPEAKER_FRONT_CENTER,
        SL_BYTEORDER_LITTLEENDIAN
    };
    SLDataSource src = { &locBq, &pcm };

    // Player sink = output mix.
    SLDataLocator_OutputMix locMix = {
        SL_DATALOCATOR_OUTPUTMIX, outputMix
    };
    SLDataSink snk = { &locMix, NULL };

    const SLInterfaceID ids[1] = { SL_IID_ANDROIDSIMPLEBUFFERQUEUE };
    const SLboolean req[1] = { SL_BOOLEAN_TRUE };
    r = (*engine)->CreateAudioPlayer(engine, &player, &src, &snk,
                                      1, ids, req);
    if (r != SL_RESULT_SUCCESS) {
        fprintf(stderr, "[C] FAIL CreateAudioPlayer: 0x%lx\n",
                (unsigned long)r);
        goto cleanup;
    }
    r = (*player)->Realize(player, SL_BOOLEAN_FALSE);
    if (r != SL_RESULT_SUCCESS) {
        fprintf(stderr, "[C] FAIL player Realize: 0x%lx\n",
                (unsigned long)r);
        goto cleanup;
    }
    fprintf(stdout, "[C] player realized\n");

    r = (*player)->GetInterface(player, SL_IID_PLAY, &playItf);
    if (r != SL_RESULT_SUCCESS) {
        fprintf(stderr, "[C] FAIL GetInterface(PLAY): 0x%lx\n",
                (unsigned long)r);
        goto cleanup;
    }
    r = (*player)->GetInterface(player, SL_IID_ANDROIDSIMPLEBUFFERQUEUE,
                                 &bqItf);
    if (r != SL_RESULT_SUCCESS) {
        fprintf(stderr, "[C] FAIL GetInterface(BQ): 0x%lx\n",
                (unsigned long)r);
        goto cleanup;
    }

    // Synthesize the same 1-second 440Hz tone.
    int frames = kSampleRate * kToneDurationS;
    tone = (int16_t*)calloc((size_t)frames, sizeof(int16_t));
    if (!tone) { fprintf(stderr, "[C] calloc failed\n"); goto cleanup; }
    generate_tone(tone, frames);

    r = (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_PLAYING);
    if (r != SL_RESULT_SUCCESS) {
        fprintf(stderr, "[C] FAIL SetPlayState(PLAYING): 0x%lx\n",
                (unsigned long)r);
        goto cleanup;
    }

    r = (*bqItf)->Enqueue(bqItf, tone, (SLuint32)frames * sizeof(int16_t));
    if (r != SL_RESULT_SUCCESS) {
        fprintf(stderr, "[C] FAIL Enqueue: 0x%lx\n", (unsigned long)r);
        goto cleanup;
    }
    fprintf(stdout, "[C] Enqueue OK (%d frames)\n", frames);

    // Let it play out.
    usleep(1100 * 1000);

    r = (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_STOPPED);
    fprintf(stdout, "[C] SetPlayState(STOPPED): 0x%lx\n", (unsigned long)r);

    fprintf(stdout, "[C] PASS\n");
    rc = 0;

cleanup:
    if (tone) free(tone);
    if (player) (*player)->Destroy(player);
    if (outputMix) (*outputMix)->Destroy(outputMix);
    if (engineObj) (*engineObj)->Destroy(engineObj);
    fflush(stdout);
    return rc;
}

// ============================================================================
// main
// ============================================================================
int main(int argc, char** argv) {
    (void)argc; (void)argv;
    fprintf(stdout,
        "================================================================\n"
        "CR34 spike — M5 audio-daemon AAudio feasibility probe\n"
        "Phone: OnePlus 6 cfb7c9e3 (Android 15 LineageOS 22, kernel 4.9.337)\n"
        "================================================================\n");
    fflush(stdout);

    // Phase A.  Open a stream.  This is the architectural gate.
    AAudioStream* stream = NULL;
    int rc_a = phase_a_open(&stream);
    int rc_b = -1;
    int rc_c = -1;

    // Phase B.  Actually write audio.  Only if A passed.
    if (rc_a == 0 && stream) {
        rc_b = phase_b_write(stream);
    } else {
        fprintf(stdout, "\n=== Phase B: SKIPPED (Phase A failed) ===\n");
        fflush(stdout);
    }

    if (stream) {
        AAudioStream_close(stream);
        stream = NULL;
    }

    // Phase C.  Run OpenSL ES regardless of A's verdict.  If A passed, this is
    // confirmation that both NDK paths work and the daemon has a fallback in
    // hand "for free".  If A failed, this is the actual pivot decision data.
    rc_c = phase_c_opensl();

    // Verdict.
    fprintf(stdout, "\n================================================================\n");
    fprintf(stdout, "VERDICT\n");
    fprintf(stdout, "================================================================\n");
    fprintf(stdout, "  Phase A (AAudio open)  : %s\n",
            rc_a == 0 ? "PASS" : "FAIL");
    fprintf(stdout, "  Phase B (AAudio write) : %s\n",
            rc_a != 0 ? "SKIPPED" : (rc_b == 0 ? "PASS" : "FAIL"));
    fprintf(stdout, "  Phase C (OpenSL ES)    : %s\n",
            rc_c == 0 ? "PASS" : "FAIL");

    int exit_code;
    if (rc_a == 0 && rc_b == 0) {
        fprintf(stdout, "\n  M5 timeline impact: HOLDS (6.5 person-days).\n");
        fprintf(stdout, "  AAudio is fully viable as the M5 Phase-1 backend.\n");
        if (rc_c == 0) {
            fprintf(stdout,
                "  Bonus: OpenSL ES also works; fallback path is free.\n");
        }
        exit_code = 0;
    } else if (rc_a == 0 && rc_b != 0) {
        fprintf(stdout, "\n  M5 timeline impact: +0.5 person-day debug.\n");
        fprintf(stdout, "  AAudio opens but won't stream — exotic; document.\n");
        exit_code = 1;
    } else if (rc_a != 0 && rc_c == 0) {
        fprintf(stdout, "\n  M5 timeline impact: +1 person-day pivot to OpenSL.\n");
        fprintf(stdout, "  AAudio refuses our context; OpenSL ES works — use it.\n");
        exit_code = 2;
    } else {
        fprintf(stdout, "\n  M5 timeline impact: DEEPER PIVOT NEEDED (+ days).\n");
        fprintf(stdout, "  Both NDK audio APIs refuse this context.\n");
        fprintf(stdout, "  Options: (a) run daemon as uid=1000 / platform_app;\n");
        fprintf(stdout, "           (b) fold AudioTrack into dalvikvm process;\n");
        fprintf(stdout, "           (c) selinux audioserver-context launcher.\n");
        exit_code = 3;
    }
    fprintf(stdout, "================================================================\n");
    fflush(stdout);
    return exit_code;
}
