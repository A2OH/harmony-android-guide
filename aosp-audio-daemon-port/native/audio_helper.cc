// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M5-Step5 audio helper subprocess.
//
// Spawned by AudioHelper (parent = audio_flinger daemon).  Lives in its own
// process so its libaudioclient sees a virgin ProcessState (no
// /dev/vndbinder Bp-to-self loop — see M5_STEP4_REPORT §4.2).
//
// Protocol (synchronous request/reply on stdin/stdout — see AudioHelper.h):
//     OPEN <sr> <ch> <fmt>\n        -> OK <sr> <ch> <fmt> <fpb> <cap>\n
//     START\n                       -> OK\n
//     STOP\n                        -> OK\n
//     PAUSE\n                       -> OK\n
//     FLUSH\n                       -> OK\n
//     WRITE <bytes>\n <bytes>       -> OK <frames>\n
//     CLOSE\n                       -> OK\n
//     QUIT\n                        -> OK\n  (then exit 0)
//
// Banner sent on startup so the parent knows we're ready:
//     READY audio_helper v1\n
//
// Anti-drift: ZERO Westlake-shim / framework / aosp-shim references.  This
// binary depends only on bionic libc + libaaudio + libm; ~ 200 LOC.

#define _GNU_SOURCE
#include <errno.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <unistd.h>

#include <aaudio/AAudio.h>

namespace {

static AAudioStream* gStream = nullptr;
static int32_t gSampleRate    = 48000;
static int32_t gChannelCount  = 1;
static int32_t gFmtAOSP       = 1;       // PCM_16_BIT
static aaudio_format_t gFmtAA = AAUDIO_FORMAT_PCM_I16;
static int32_t gFramesPerBurst = 240;
static int32_t gCapacityFrames = 3840;

static void sendReply(const char* fmt, ...) {
    char buf[256];
    va_list ap;
    va_start(ap, fmt);
    int n = vsnprintf(buf, sizeof(buf), fmt, ap);
    va_end(ap);
    if (n < 0) return;
    if (n >= (int)sizeof(buf)) n = sizeof(buf) - 1;
    if (n == 0 || buf[n-1] != '\n') { buf[n++] = '\n'; }
    ssize_t w = write(STDOUT_FILENO, buf, n);
    (void)w;
}

// Read exactly `n` bytes from stdin; retries on short reads.
static int readAll(void* buf, size_t n) {
    uint8_t* p = (uint8_t*)buf;
    while (n > 0) {
        ssize_t r = read(STDIN_FILENO, p, n);
        if (r > 0) { p += r; n -= (size_t)r; continue; }
        if (r == 0) return -1;
        if (errno == EINTR) continue;
        return -1;
    }
    return 0;
}

// Read a \n-terminated line from stdin into `out` (cap-1 max).  Returns 0
// on success, -1 on EOF/error.
static int readLine(char* out, size_t cap) {
    size_t pos = 0;
    while (pos + 1 < cap) {
        char c;
        ssize_t r = read(STDIN_FILENO, &c, 1);
        if (r == 1) {
            if (c == '\n') { out[pos] = 0; return 0; }
            out[pos++] = c;
            continue;
        }
        if (r == 0) return -1;
        if (errno == EINTR) continue;
        return -1;
    }
    out[pos] = 0;
    return 0;
}

static aaudio_format_t toAAudioFormat(int32_t aospFormat) {
    switch (aospFormat) {
        case 5:  return AAUDIO_FORMAT_PCM_FLOAT;
        case 1:  return AAUDIO_FORMAT_PCM_I16;
        default: return AAUDIO_FORMAT_PCM_I16;
    }
}

static int32_t fromAAudioFormat(aaudio_format_t aa) {
    switch (aa) {
        case AAUDIO_FORMAT_PCM_FLOAT: return 5;
        case AAUDIO_FORMAT_PCM_I16:   return 1;
        default: return 1;
    }
}

static void cmdOpen(const char* args) {
    int sr = 48000, ch = 1, aospFmt = 1;
    sscanf(args, "%d %d %d", &sr, &ch, &aospFmt);

    if (gStream != nullptr) {
        AAudioStream_close(gStream);
        gStream = nullptr;
    }

    AAudioStreamBuilder* b = nullptr;
    aaudio_result_t r = AAudio_createStreamBuilder(&b);
    if (r != AAUDIO_OK) {
        sendReply("ERR %d createStreamBuilder=%s", r, AAudio_convertResultToText(r));
        return;
    }
    aaudio_format_t aaFmt = toAAudioFormat(aospFmt);
    AAudioStreamBuilder_setDirection(b, AAUDIO_DIRECTION_OUTPUT);
    AAudioStreamBuilder_setSampleRate(b, sr);
    AAudioStreamBuilder_setChannelCount(b, ch);
    AAudioStreamBuilder_setFormat(b, aaFmt);
    AAudioStreamBuilder_setSharingMode(b, AAUDIO_SHARING_MODE_SHARED);
    AAudioStreamBuilder_setPerformanceMode(b, AAUDIO_PERFORMANCE_MODE_NONE);
    AAudioStreamBuilder_setUsage(b, AAUDIO_USAGE_MEDIA);
    AAudioStreamBuilder_setContentType(b, AAUDIO_CONTENT_TYPE_MUSIC);

    r = AAudioStreamBuilder_openStream(b, &gStream);
    AAudioStreamBuilder_delete(b);
    if (r != AAUDIO_OK) {
        sendReply("ERR %d openStream=%s", r, AAudio_convertResultToText(r));
        return;
    }
    gSampleRate     = AAudioStream_getSampleRate(gStream);
    gChannelCount   = AAudioStream_getChannelCount(gStream);
    aaudio_format_t neg = AAudioStream_getFormat(gStream);
    gFmtAA          = neg;
    gFmtAOSP        = fromAAudioFormat(neg);
    gFramesPerBurst = AAudioStream_getFramesPerBurst(gStream);
    gCapacityFrames = AAudioStream_getBufferCapacityInFrames(gStream);
    if (gFramesPerBurst <= 0) gFramesPerBurst = 240;
    if (gCapacityFrames <= 0) gCapacityFrames = 3840;
    fprintf(stderr,
            "[audio_helper] OPEN ok sr=%d ch=%d fmt=%d fpb=%d cap=%d\n",
            gSampleRate, gChannelCount, gFmtAOSP, gFramesPerBurst, gCapacityFrames);
    sendReply("OK %d %d %d %d %d", gSampleRate, gChannelCount, gFmtAOSP,
              gFramesPerBurst, gCapacityFrames);
}

static void cmdStart() {
    if (gStream == nullptr) { sendReply("ERR -1 no_stream"); return; }
    aaudio_result_t r = AAudioStream_requestStart(gStream);
    if (r != AAUDIO_OK) { sendReply("ERR %d start=%s", r, AAudio_convertResultToText(r)); return; }
    // Wait briefly for state transition so the first WRITE doesn't race.
    aaudio_stream_state_t next = AAUDIO_STREAM_STATE_UNINITIALIZED;
    AAudioStream_waitForStateChange(gStream, AAUDIO_STREAM_STATE_STARTING,
                                    &next, 2L * 1000 * 1000 * 1000);
    sendReply("OK");
}

static void cmdStop() {
    if (gStream == nullptr) { sendReply("OK"); return; }
    aaudio_result_t r = AAudioStream_requestStop(gStream);
    if (r != AAUDIO_OK) { sendReply("ERR %d stop=%s", r, AAudio_convertResultToText(r)); return; }
    sendReply("OK");
}
static void cmdPause() {
    if (gStream == nullptr) { sendReply("OK"); return; }
    aaudio_result_t r = AAudioStream_requestPause(gStream);
    if (r != AAUDIO_OK) { sendReply("ERR %d pause=%s", r, AAudio_convertResultToText(r)); return; }
    sendReply("OK");
}
static void cmdFlush() {
    if (gStream == nullptr) { sendReply("OK"); return; }
    aaudio_result_t r = AAudioStream_requestFlush(gStream);
    if (r != AAUDIO_OK) { sendReply("ERR %d flush=%s", r, AAudio_convertResultToText(r)); return; }
    sendReply("OK");
}

static void cmdWrite(const char* args) {
    int byteCount = 0;
    sscanf(args, "%d", &byteCount);
    if (byteCount <= 0 || byteCount > 16 * 1024 * 1024) {
        sendReply("ERR -1 bad_bytecount=%d", byteCount);
        return;
    }
    void* buf = malloc((size_t)byteCount);
    if (buf == nullptr) { sendReply("ERR -1 oom"); return; }
    if (readAll(buf, (size_t)byteCount) != 0) {
        free(buf);
        sendReply("ERR -1 read_payload_failed");
        return;
    }
    if (gStream == nullptr) {
        free(buf);
        sendReply("ERR -1 no_stream");
        return;
    }
    int bytesPerSample = (gFmtAA == AAUDIO_FORMAT_PCM_FLOAT) ? 4 : 2;
    int frameBytes = bytesPerSample * gChannelCount;
    int frames = (frameBytes > 0) ? (byteCount / frameBytes) : 0;
    int written = 0;
    int chunk = gFramesPerBurst > 0 ? gFramesPerBurst : 240;
    while (written < frames) {
        int toWrite = frames - written;
        if (toWrite > chunk) toWrite = chunk;
        char* p = (char*)buf + (size_t)written * (size_t)frameBytes;
        aaudio_result_t w = AAudioStream_write(gStream, p, toWrite,
                                                2L * 1000 * 1000 * 1000);
        if (w < 0) {
            fprintf(stderr,
                    "[audio_helper] WRITE error %d (%s) after %d/%d frames\n",
                    (int)w, AAudio_convertResultToText(w), written, frames);
            break;
        }
        if (w == 0) break;
        written += w;
    }
    free(buf);
    sendReply("OK %d", written);
}

static void cmdClose() {
    if (gStream != nullptr) {
        AAudioStream_close(gStream);
        gStream = nullptr;
    }
    sendReply("OK");
}

}  // namespace

int main(int argc, char** argv) {
    (void)argc; (void)argv;
    fprintf(stderr, "[audio_helper pid=%d] starting\n", getpid());
    sendReply("READY audio_helper v1");

    char line[256];
    while (readLine(line, sizeof(line)) == 0) {
        if (line[0] == 0) continue;
        if (strncmp(line, "OPEN ", 5) == 0)       cmdOpen(line + 5);
        else if (strcmp(line, "START") == 0)      cmdStart();
        else if (strcmp(line, "STOP") == 0)       cmdStop();
        else if (strcmp(line, "PAUSE") == 0)      cmdPause();
        else if (strcmp(line, "FLUSH") == 0)      cmdFlush();
        else if (strncmp(line, "WRITE ", 6) == 0) cmdWrite(line + 6);
        else if (strcmp(line, "CLOSE") == 0)      cmdClose();
        else if (strcmp(line, "QUIT") == 0) {
            if (gStream != nullptr) { AAudioStream_close(gStream); gStream = nullptr; }
            sendReply("OK");
            fprintf(stderr, "[audio_helper] QUIT; exit 0\n");
            return 0;
        }
        else {
            sendReply("ERR -1 unknown_cmd=%s", line);
        }
    }
    if (gStream != nullptr) AAudioStream_close(gStream);
    fprintf(stderr, "[audio_helper] stdin closed; exit\n");
    return 0;
}
