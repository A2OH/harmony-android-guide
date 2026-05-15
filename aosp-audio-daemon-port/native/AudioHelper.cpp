// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M5 audio daemon (Step 5: helper-process AAudio backend impl).
//
// Spawn-and-pipe model.  See AudioHelper.h for the protocol and rationale.
//
// Why fork+exec a separate binary rather than fork()-only?  Because the
// daemon process has already brought in our minimal libbinder.so + opened
// `/dev/vndbinder`.  fork()'d AAudio would inherit that ProcessState
// singleton and resolve `media.audio_flinger` back to us (the very bug we're
// trying to escape).  execve resets all of that: the helper's
// `defaultServiceManager()` constructs a brand new ProcessState backed by
// `/dev/binder` (libbinder's default), finds the real audioserver, and
// AAudio's internal RPCs work cleanly.

#include "AudioHelper.h"

#include <errno.h>
#include <fcntl.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <time.h>
#include <unistd.h>

namespace android {

namespace {

struct HelperChild {
    pid_t pid = -1;
    int   cmdFd = -1;    // parent writes commands here
    int   replyFd = -1;  // parent reads replies here
    bool  alive() const { return pid > 0; }
};

static std::mutex      gMu;       // serialises commands; helper is single-stream
static HelperChild     gChild;    // singleton; one daemon → one helper

// Locate the helper binary.  We look for it next to our own executable so
// deployment is "push everything in out/bionic/ together".
static const char* helperPath() {
    static char path[512] = {};
    static bool resolved = false;
    if (resolved) return path[0] ? path : nullptr;
    resolved = true;

    // /proc/self/exe -> /data/local/tmp/westlake/bin-bionic/audio_flinger
    char self[512] = {};
    ssize_t n = readlink("/proc/self/exe", self, sizeof(self) - 1);
    if (n <= 0) return nullptr;
    self[n] = 0;
    char* slash = strrchr(self, '/');
    if (slash == nullptr) return nullptr;
    *slash = 0;
    snprintf(path, sizeof(path), "%s/audio_helper", self);
    if (access(path, X_OK) == 0) return path;
    fprintf(stderr,
            "[wlk-af/helper] audio_helper not found at %s; falling back to PATH lookup\n",
            path);
    snprintf(path, sizeof(path), "audio_helper");
    return path;
}

// Set O_CLOEXEC so we don't leak helper pipes into other fork()s in the daemon.
static void setCloexec(int fd) {
    int f = fcntl(fd, F_GETFD);
    if (f >= 0) fcntl(fd, F_SETFD, f | FD_CLOEXEC);
}

// Read a single \n-terminated line from `fd` with an overall deadline.
// Returns 0 on success, -1 on timeout/EOF/error.  `out` is null-terminated.
static int readLine(int fd, char* out, size_t cap, int64_t timeoutNs) {
    size_t pos = 0;
    int64_t startNs;
    {
        struct timespec ts;
        clock_gettime(CLOCK_MONOTONIC, &ts);
        startNs = (int64_t)ts.tv_sec * 1000000000LL + ts.tv_nsec;
    }
    while (pos + 1 < cap) {
        char c;
        ssize_t r = read(fd, &c, 1);
        if (r == 1) {
            if (c == '\n') {
                out[pos] = 0;
                return 0;
            }
            out[pos++] = c;
            continue;
        }
        if (r == 0) {
            fprintf(stderr, "[wlk-af/helper] reply pipe EOF\n");
            return -1;
        }
        if (errno == EINTR) continue;
        if (errno == EAGAIN || errno == EWOULDBLOCK) {
            struct timespec ts;
            clock_gettime(CLOCK_MONOTONIC, &ts);
            int64_t nowNs = (int64_t)ts.tv_sec * 1000000000LL + ts.tv_nsec;
            if (nowNs - startNs > timeoutNs) {
                fprintf(stderr, "[wlk-af/helper] reply pipe timeout\n");
                return -1;
            }
            // tiny sleep; reply pipe is blocking-by-default so we shouldn't hit this
            usleep(1000);
            continue;
        }
        fprintf(stderr, "[wlk-af/helper] read error: %s\n", strerror(errno));
        return -1;
    }
    fprintf(stderr, "[wlk-af/helper] reply line too long\n");
    return -1;
}

// Write `len` bytes; retry on partial writes / EINTR.
static int writeAll(int fd, const void* buf, size_t len) {
    const uint8_t* p = (const uint8_t*)buf;
    while (len > 0) {
        ssize_t w = write(fd, p, len);
        if (w > 0) { p += w; len -= (size_t)w; continue; }
        if (w < 0 && errno == EINTR) continue;
        return -1;
    }
    return 0;
}

// Send a command line + read one reply line.  Both with timeout.
// Reply buffer is null-terminated by readLine().
static int command(const char* cmdLine, char* reply, size_t replyCap,
                   int64_t timeoutNs) {
    if (!gChild.alive()) {
        fprintf(stderr, "[wlk-af/helper] command but child not alive\n");
        return -1;
    }
    size_t cmdLen = strlen(cmdLine);
    if (writeAll(gChild.cmdFd, cmdLine, cmdLen) != 0) return -1;
    if (cmdLine[cmdLen - 1] != '\n') {
        if (writeAll(gChild.cmdFd, "\n", 1) != 0) return -1;
    }
    return readLine(gChild.replyFd, reply, replyCap, timeoutNs);
}

// Spawn the helper if not already up.  Returns true if alive on exit.
static bool ensureHelper() {
    if (gChild.alive()) return true;

    int toChild[2];   // parent writes, child reads stdin
    int fromChild[2]; // child writes stdout, parent reads
    if (pipe(toChild) != 0) {
        fprintf(stderr, "[wlk-af/helper] pipe(toChild) failed: %s\n", strerror(errno));
        return false;
    }
    if (pipe(fromChild) != 0) {
        fprintf(stderr, "[wlk-af/helper] pipe(fromChild) failed: %s\n", strerror(errno));
        ::close(toChild[0]); ::close(toChild[1]);
        return false;
    }

    pid_t pid = fork();
    if (pid < 0) {
        fprintf(stderr, "[wlk-af/helper] fork failed: %s\n", strerror(errno));
        ::close(toChild[0]); ::close(toChild[1]);
        ::close(fromChild[0]); ::close(fromChild[1]);
        return false;
    }

    if (pid == 0) {
        // Child: hook up stdin <- toChild[0], stdout -> fromChild[1].
        dup2(toChild[0], STDIN_FILENO);
        dup2(fromChild[1], STDOUT_FILENO);
        // Keep child's stderr inherited (logs flow through the daemon's stderr).
        ::close(toChild[0]); ::close(toChild[1]);
        ::close(fromChild[0]); ::close(fromChild[1]);

        // CRITICAL: scrub LD_LIBRARY_PATH so the helper does NOT pick up our
        // minimal libbinder.so / lib-bionic stage area.  AAudio's
        // libaudioclient must load against the platform's stock libbinder.so
        // on /system/lib64, otherwise our [sm-stub] WaitForProperty hook
        // fires and audioserver's `defaultServiceManager()` lookup returns
        // a non-functional Bp.  Empirically: with LD_LIBRARY_PATH set, the
        // helper sees AAUDIO_ERROR_NO_SERVICE (-881) on openStream; with it
        // scrubbed, AAudio opens cleanly (verified standalone via
        // `su 1000 -c '.../audio_helper'`).  See M5_STEP5_REPORT §4.2.
        unsetenv("LD_LIBRARY_PATH");
        // Also scrub LD_PRELOAD in case anyone wires it later.
        unsetenv("LD_PRELOAD");

        const char* path = helperPath();
        if (path == nullptr) {
            fprintf(stderr, "[wlk-af/helper] no helper path; child exit\n");
            _exit(127);
        }
        char* const argv[] = { (char*)path, nullptr };
        execv(path, argv);
        fprintf(stderr, "[wlk-af/helper] execv(%s) failed: %s\n",
                path, strerror(errno));
        _exit(127);
    }

    // Parent.
    ::close(toChild[0]);
    ::close(fromChild[1]);
    setCloexec(toChild[1]);
    setCloexec(fromChild[0]);
    gChild.pid     = pid;
    gChild.cmdFd   = toChild[1];
    gChild.replyFd = fromChild[0];

    // Wait for the child's READY line.
    char banner[128] = {};
    if (readLine(gChild.replyFd, banner, sizeof(banner),
                 5LL * 1000 * 1000 * 1000) != 0) {
        fprintf(stderr, "[wlk-af/helper] no READY banner from child %d; killing\n", pid);
        kill(pid, SIGKILL);
        ::close(gChild.cmdFd); ::close(gChild.replyFd);
        gChild = HelperChild{};
        return false;
    }
    fprintf(stderr, "[wlk-af/helper] child %d up: %s\n", pid, banner);
    return true;
}

}  // anonymous namespace

AudioHelper::Stream* AudioHelper::openOutput(int32_t requestedSampleRate,
                                              int32_t requestedChannelCount,
                                              int32_t requestedFormat) {
    std::lock_guard<std::mutex> lk(gMu);
    int32_t sr = (requestedSampleRate > 0) ? requestedSampleRate : 48000;
    int32_t cc = (requestedChannelCount > 0) ? requestedChannelCount : 1;
    int32_t fmt = (requestedFormat > 0) ? requestedFormat : 1; // PCM_16_BIT

    Stream* s = (Stream*)calloc(1, sizeof(Stream));
    if (s == nullptr) {
        fprintf(stderr, "[wlk-af/helper] calloc Stream fail\n");
        return nullptr;
    }
    s->sampleRate           = sr;
    s->channelCount         = cc;
    s->framesPerBurst       = 240;
    s->bufferCapacityFrames = 3840;
    s->format               = fmt;
    s->requestedChannels    = cc;
    s->framesWrittenLocal   = 0;

    if (!ensureHelper()) {
        fprintf(stderr,
                "[wlk-af/helper] openOutput: helper unavailable; degraded mode "
                "(accept-and-discard) sr=%d ch=%d\n", sr, cc);
        return s;
    }

    char line[128];
    snprintf(line, sizeof(line), "OPEN %d %d %d\n", sr, cc, fmt);
    char reply[256];
    if (command(line, reply, sizeof(reply), 10LL * 1000 * 1000 * 1000) != 0) {
        fprintf(stderr, "[wlk-af/helper] OPEN: no reply\n");
        return s;  // degraded
    }
    if (strncmp(reply, "OK ", 3) != 0) {
        fprintf(stderr, "[wlk-af/helper] OPEN: %s\n", reply);
        return s;  // degraded
    }
    int isr = 0, icc = 0, ifmt = 0, ifpb = 0, icap = 0;
    if (sscanf(reply, "OK %d %d %d %d %d", &isr, &icc, &ifmt, &ifpb, &icap) == 5) {
        s->sampleRate           = isr;
        s->channelCount         = icc;
        s->format               = ifmt;
        s->framesPerBurst       = ifpb;
        s->bufferCapacityFrames = icap;
    }
    fprintf(stderr,
            "[wlk-af/helper] OPEN OK sr=%d ch=%d fmt=%d fpb=%d cap=%d\n",
            s->sampleRate, s->channelCount, s->format,
            s->framesPerBurst, s->bufferCapacityFrames);
    return s;
}

ssize_t AudioHelper::writeFrames(Stream* s, const void* buffer,
                                  int32_t frames, int64_t timeoutNanos) {
    if (s == nullptr || frames <= 0) return 0;
    std::lock_guard<std::mutex> lk(gMu);
    if (!gChild.alive()) {
        // Degraded fallback — same shape as the previous AAudioBackend so
        // upstream apps see forward progress.
        s->framesWrittenLocal += frames;
        return frames;
    }
    // Compute byte count from format.  AOSP audio_format_t: 1=PCM_16_BIT (2B), 5=PCM_FLOAT (4B).
    int32_t bytesPerSample = (s->format == 5) ? 4 : 2;
    int32_t channels = s->channelCount > 0 ? s->channelCount : 1;
    int32_t frameBytes = bytesPerSample * channels;
    int32_t byteCount  = frames * frameBytes;

    char line[64];
    snprintf(line, sizeof(line), "WRITE %d\n", byteCount);
    if (writeAll(gChild.cmdFd, line, strlen(line)) != 0) {
        fprintf(stderr, "[wlk-af/helper] WRITE cmd: pipe broken\n");
        return -1;
    }
    if (writeAll(gChild.cmdFd, buffer, (size_t)byteCount) != 0) {
        fprintf(stderr, "[wlk-af/helper] WRITE data: pipe broken\n");
        return -1;
    }
    char reply[64];
    int64_t to = (timeoutNanos > 0) ? timeoutNanos : 3LL * 1000 * 1000 * 1000;
    if (readLine(gChild.replyFd, reply, sizeof(reply), to) != 0) return -1;
    int wf = 0;
    if (sscanf(reply, "OK %d", &wf) != 1) {
        fprintf(stderr, "[wlk-af/helper] WRITE bad reply: %s\n", reply);
        return -1;
    }
    s->framesWrittenLocal += wf;
    return (ssize_t)wf;
}

static int simpleCommand(const char* cmd, AudioHelper::Stream* /*s*/) {
    std::lock_guard<std::mutex> lk(gMu);
    if (!gChild.alive()) return 0;  // degraded ack
    char reply[64];
    if (command(cmd, reply, sizeof(reply), 5LL * 1000 * 1000 * 1000) != 0) {
        return -1;
    }
    if (strncmp(reply, "OK", 2) != 0) {
        fprintf(stderr, "[wlk-af/helper] %s: %s\n", cmd, reply);
        return -1;
    }
    return 0;
}

int AudioHelper::start(Stream* s)  { return simpleCommand("START\n", s); }
int AudioHelper::stop(Stream* s)   { return simpleCommand("STOP\n", s); }
int AudioHelper::pause(Stream* s)  { return simpleCommand("PAUSE\n", s); }
int AudioHelper::flush(Stream* s)  { return simpleCommand("FLUSH\n", s); }

int AudioHelper::close(Stream* s) {
    if (s == nullptr) return 0;
    if (gChild.alive()) {
        char reply[64];
        std::lock_guard<std::mutex> lk(gMu);
        command("CLOSE\n", reply, sizeof(reply), 5LL * 1000 * 1000 * 1000);
    }
    free(s);
    return 0;
}

uint32_t AudioHelper::latencyMs(const Stream* s) {
    if (s == nullptr || s->sampleRate <= 0) return 0;
    int64_t ms = ((int64_t)s->bufferCapacityFrames * 1000) / s->sampleRate;
    if (ms < 0) ms = 0;
    if (ms > 100000) ms = 100000;
    return (uint32_t)ms;
}

int64_t AudioHelper::getFramesWritten(const Stream* s) {
    if (s == nullptr) return 0;
    return s->framesWrittenLocal;
}

int32_t AudioHelper::aospFormat(const Stream* s) {
    if (s == nullptr) return 1;  // PCM_16_BIT
    return s->format;
}

void AudioHelper::shutdown() {
    std::lock_guard<std::mutex> lk(gMu);
    if (!gChild.alive()) return;
    // Best-effort QUIT then SIGTERM after 500ms.
    const char* q = "QUIT\n";
    writeAll(gChild.cmdFd, q, strlen(q));
    usleep(500 * 1000);
    int status = 0;
    pid_t r = waitpid(gChild.pid, &status, WNOHANG);
    if (r == 0) {
        kill(gChild.pid, SIGTERM);
        usleep(200 * 1000);
        r = waitpid(gChild.pid, &status, WNOHANG);
        if (r == 0) {
            kill(gChild.pid, SIGKILL);
            waitpid(gChild.pid, &status, 0);
        }
    }
    ::close(gChild.cmdFd);
    ::close(gChild.replyFd);
    gChild = HelperChild{};
    fprintf(stderr, "[wlk-af/helper] child reaped\n");
}

}  // namespace android
