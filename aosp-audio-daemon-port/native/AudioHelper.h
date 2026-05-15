// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M5 audio daemon (Step 5: helper-process AAudio backend).
//
// Step 4 exposed an architectural dead-end: when the audio_flinger daemon
// itself loads libaaudio.so via dlopen, libaudioclient's internal
// `defaultServiceManager()->getService("media.audio_flinger")` resolves
// against OUR `/dev/vndbinder` SM and returns OUR own Bn-side
// WestlakeAudioFlinger.  AAudio then tries to linkToDeath on that local
// binder (ENOSYS -38), loops on getService, and the daemon's worker thread
// is occupied servicing AAudio's own re-entrance — so the smoke client's
// CREATE_TRACK never completes.  M5_STEP4_REPORT §4 documents this.
//
// Step 5 resolution per M5 plan §3.3 / CR34 §3.2 spike:  drop in-process
// AAudio entirely and spawn a child process (`audio_helper`) that holds
// the AAudio stream in its own address space, with its own ProcessState
// pinned to `/dev/binder` (the platform SM where the *real*
// `media.audio_flinger` provided by `audioserver` lives).  The CR34 spike
// already proved this works end-to-end from a uid=2000 / shell process.
//
// Parent ↔ child protocol (line-oriented commands on a pipe; binary audio
// frames are length-prefixed and follow the WRITE command):
//
//     Parent → child (cmd pipe):
//         OPEN <sampleRate> <channelCount> <aospFormat>\n
//         START\n
//         STOP\n
//         PAUSE\n
//         FLUSH\n
//         WRITE <byteCount>\n  (then byteCount raw bytes)
//         CLOSE\n
//         QUIT\n
//     Child → parent (reply pipe):
//         OK <sr> <ch> <fmt> <fpb> <cap>\n     after OPEN
//         OK\n                                  after START/STOP/PAUSE/FLUSH/CLOSE/QUIT
//         OK <framesWritten>\n                  after WRITE
//         ERR <code> <message>\n                on any failure
//
// Phase-1 ownership semantics: one helper per daemon process (single
// AAudio output stream, same as M5_AUDIO_DAEMON_PLAN.md §4.2).  AudioHelper
// is therefore a singleton.
//
// Anti-drift: no per-app branches.  Every Android client that talks to
// our daemon goes through the same WRITE pipe regardless of upstream.
//
// Companion: docs/engine/M5_STEP5_REPORT.md
//             docs/engine/CR34_M5_SPIKE_REPORT.md (helper feasibility)
//             docs/engine/M5_AUDIO_DAEMON_PLAN.md §3.3 (backend abstraction)

#ifndef WESTLAKE_AUDIO_HELPER_H
#define WESTLAKE_AUDIO_HELPER_H

#include <mutex>
#include <stdint.h>
#include <sys/types.h>

namespace android {

class AudioHelper {
public:
    // Mirrors the metadata the previous in-process AAudioBackend exposed —
    // every other source file (WestlakeAudioFlinger, WestlakeAudioTrack)
    // reads only these fields, so the type-shape is preserved across the
    // backend swap (intentional, to keep the swap a localised change).
    struct Stream {
        int32_t sampleRate;
        int32_t channelCount;
        int32_t framesPerBurst;
        int32_t bufferCapacityFrames;
        int32_t format;             // AOSP audio_format_t (PCM_16_BIT / PCM_FLOAT)
        int32_t requestedChannels;
        int64_t framesWrittenLocal;
    };

    // Construct/destroy the helper child.  Called from
    // `WestlakeAudioFlinger`'s ctor/dtor.  The fork+exec happens lazily on
    // first openOutput so that simply starting the daemon doesn't spend
    // helper resources until an app actually creates a track.
    static Stream* openOutput(int32_t requestedSampleRate,
                              int32_t requestedChannelCount,
                              int32_t requestedFormat);

    // Blocking write — sends WRITE <bytes>\n followed by `frames*frameBytes`
    // raw bytes down the parent→child pipe, reads the OK reply.
    static ssize_t writeFrames(Stream* s, const void* buffer, int32_t frames,
                               int64_t timeoutNanos);

    static int start(Stream* s);
    static int stop(Stream* s);
    static int pause(Stream* s);
    static int flush(Stream* s);
    static int close(Stream* s);

    static uint32_t latencyMs(const Stream* s);
    static int64_t getFramesWritten(const Stream* s);
    static int32_t aospFormat(const Stream* s);

    // Optional explicit teardown — kills the helper child if alive.  Called
    // from atexit-like paths (audio_flinger main).
    static void shutdown();
};

}  // namespace android

#endif  // WESTLAKE_AUDIO_HELPER_H
