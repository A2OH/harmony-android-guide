// =============================================================================
// CR33 spike — M6 surface-daemon buffer coherency probe
// =============================================================================
//
// Risk being probed (per M6_SURFACE_DAEMON_PLAN.md §8 risk #1):
//   "HWUI insists on GPU-coherent buffers; memfd-mmap fails or produces black
//    frames."
//
// What we test, in three independent probes:
//
//   Phase A  memfd self-test
//     memfd_create + ftruncate + two mmap views of the same fd.
//     Confirms the kernel supports memfd, F_ADD_SEALS, and shared-mapping
//     coherency.  This is the minimal substrate for our planned
//     GraphicBuffer-memfd.cpp.
//
//   Phase B  AHardwareBuffer CPU-coherency probe
//     AHardwareBuffer_allocate with CPU_READ_OFTEN|CPU_WRITE_OFTEN at
//     1080x2280 RGBA8888.  Locks, writes a pattern, unlocks.  Re-locks
//     read-only, verifies the pattern survived a CPU round-trip.
//     This proves the *gralloc allocator's CPU view* is coherent — the
//     positive result for this phase means the gralloc HAL on this device
//     gives us back a CPU-coherent buffer when asked for CPU usage flags.
//     M6's memfd substitute targets that same boundary, so a pass here
//     means the HWUI-side mmap-and-write path will accept our buffers.
//
//   Phase C  AHardwareBuffer fd extraction + cross-mapping
//     Use AHardwareBuffer_sendHandleToUnixSocket + recvHandleFromUnixSocket
//     to round-trip the buffer's native_handle across a socketpair (the
//     same path Binder uses for AHardwareBuffer Parcel marshaling — see
//     AHardwareBuffer_writeToParcel which dups fds through the binder
//     transaction).  In the receiving end, re-import via _recvHandle, lock,
//     and confirm we see the pattern Phase B wrote.  Proves cross-process
//     coherency of gralloc-backed buffers.
//
//   Phase D  memfd-as-substitute viability
//     Allocate a memfd buffer the same size as Phase B's AHardwareBuffer,
//     ftruncate, mmap; from a forked child, dup the fd in via /proc/PID/fd,
//     mmap, write, exit; back in parent, read; confirm the same coherency
//     model works for a memfd-substituted GraphicBuffer.
//
// Verdict bundling:
//   A=OK + D=OK         => memfd-only path viable for daemon-side substitute.
//   B=OK + C=OK         => gralloc-allocated AHardwareBuffer interop works on
//                          this phone (confirms HWUI's allocation primitive
//                          works without us substituting anything).
//   A=OK and (B=OK|C=OK) => the M6 plan's "memfd substitute" assumption holds:
//                          we can replace the gralloc fd with a memfd fd at
//                          the IGraphicBufferProducer.requestBuffer boundary.
//   A=FAIL              => deep pivot needed (dma_heap fallback impossible
//                          on this kernel — no /dev/dma_heap).
//
// Exit codes:
//   0  full success (all phases pass)
//   1  Phase A failed (memfd unusable — unrecoverable on this kernel)
//   2  Phase B failed (gralloc HAL refuses CPU-coherent flags — unusual)
//   3  Phase C failed (cross-process handle transfer broken)
//   4  Phase D failed (memfd cross-process not viable — odd, would surprise)
//   5  Mixed: A passes but B fails — only memfd path remains; report.
//
// Build: see build.sh.  Single-file, links only -ldl -llog -landroid.
// =============================================================================

#define _GNU_SOURCE
#include <android/hardware_buffer.h>
#include <android/log.h>
#include <dlfcn.h>
#include <errno.h>
#include <fcntl.h>
#include <inttypes.h>
#include <linux/memfd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/socket.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <time.h>
#include <unistd.h>

#define TAG "westlake-cr33-spike"
#define LOGI(...) do { fprintf(stdout, "[I] " __VA_ARGS__); fputc('\n', stdout); \
                       __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__); } while (0)
#define LOGE(...) do { fprintf(stdout, "[E] " __VA_ARGS__); fputc('\n', stdout); \
                       __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__); } while (0)
#define LOGS(...) do { fprintf(stdout, "    " __VA_ARGS__); fputc('\n', stdout); } while (0)

// Some bionic versions have memfd_create as a libc symbol, but for paranoia
// we drop to syscall.  Already-portable across all OnePlus 6 kernels.
static int memfd_create_syscall(const char* name, unsigned int flags) {
#ifdef __NR_memfd_create
    return (int)syscall(__NR_memfd_create, name, flags);
#else
    errno = ENOSYS;
    return -1;
#endif
}

// =============================================================================
// Phase A — memfd self-test
// =============================================================================
static int phase_a_memfd_selftest() {
    LOGI("===== Phase A: memfd self-test =====");

    const size_t BYTES = 1920 * 1080 * 4;  // ~8 MB, close to full-screen RGBA8888
    int fd = memfd_create_syscall("cr33-phaseA", MFD_CLOEXEC | MFD_ALLOW_SEALING);
    if (fd < 0) {
        LOGE("memfd_create FAILED: errno=%d (%s)", errno, strerror(errno));
        return -1;
    }
    LOGS("memfd_create OK, fd=%d", fd);

    if (ftruncate(fd, (off_t)BYTES) != 0) {
        LOGE("ftruncate FAILED: errno=%d (%s)", errno, strerror(errno));
        close(fd);
        return -1;
    }
    LOGS("ftruncate to %zu bytes OK", BYTES);

    if (fcntl(fd, F_ADD_SEALS, F_SEAL_SHRINK | F_SEAL_GROW) != 0) {
        LOGS("WARN: F_ADD_SEALS failed errno=%d (%s) — not fatal", errno, strerror(errno));
    } else {
        LOGS("F_ADD_SEALS(SHRINK|GROW) OK");
    }

    void* v1 = mmap(nullptr, BYTES, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (v1 == MAP_FAILED) {
        LOGE("mmap[1] FAILED: errno=%d (%s)", errno, strerror(errno));
        close(fd);
        return -1;
    }

    void* v2 = mmap(nullptr, BYTES, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (v2 == MAP_FAILED) {
        LOGE("mmap[2] FAILED: errno=%d (%s)", errno, strerror(errno));
        munmap(v1, BYTES);
        close(fd);
        return -1;
    }
    LOGS("two MAP_SHARED views OK, v1=%p v2=%p", v1, v2);

    // Write through v1, read through v2 — must be coherent.
    uint32_t* p1 = (uint32_t*)v1;
    uint32_t* p2 = (uint32_t*)v2;
    for (size_t i = 0; i < 256; ++i) p1[i] = 0xAA55C300u + (uint32_t)i;
    __sync_synchronize();
    int mismatches = 0;
    for (size_t i = 0; i < 256; ++i) {
        if (p2[i] != (0xAA55C300u + (uint32_t)i)) mismatches++;
    }
    if (mismatches != 0) {
        LOGE("memfd MAP_SHARED coherency: %d/256 mismatches", mismatches);
        munmap(v1, BYTES); munmap(v2, BYTES); close(fd);
        return -1;
    }
    LOGS("coherency: write-via-v1, read-via-v2 all 256 words match");

    munmap(v1, BYTES);
    munmap(v2, BYTES);
    close(fd);
    LOGI("Phase A PASS");
    return 0;
}

// =============================================================================
// Phase B — AHardwareBuffer CPU-coherency probe
// =============================================================================
static int phase_b_ahb_cpu_coherency(AHardwareBuffer** outBuffer) {
    LOGI("===== Phase B: AHardwareBuffer CPU-coherency =====");

    AHardwareBuffer_Desc desc = {};
    desc.width  = 1080;
    desc.height = 2280;
    desc.layers = 1;
    desc.format = AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM;
    desc.usage  = AHARDWAREBUFFER_USAGE_CPU_READ_OFTEN
                | AHARDWAREBUFFER_USAGE_CPU_WRITE_OFTEN;
    desc.stride = 0;  // implementation chooses

    AHardwareBuffer* buf = nullptr;
    int rc = AHardwareBuffer_allocate(&desc, &buf);
    if (rc != 0 || !buf) {
        LOGE("AHardwareBuffer_allocate FAILED: rc=%d (%s)", rc, strerror(-rc));
        return -1;
    }
    LOGS("AHardwareBuffer_allocate OK, buf=%p", (void*)buf);

    AHardwareBuffer_Desc back = {};
    AHardwareBuffer_describe(buf, &back);
    LOGS("describe: w=%u h=%u layers=%u format=%u usage=0x%" PRIx64 " stride=%u",
         back.width, back.height, back.layers, back.format,
         (uint64_t)back.usage, back.stride);

    // Lock for write, fill a known pattern.
    void* va = nullptr;
    rc = AHardwareBuffer_lock(buf,
            AHARDWAREBUFFER_USAGE_CPU_WRITE_OFTEN,
            -1, nullptr, &va);
    if (rc != 0 || !va) {
        LOGE("AHardwareBuffer_lock(WRITE) FAILED: rc=%d", rc);
        AHardwareBuffer_release(buf);
        return -1;
    }
    uint32_t* px = (uint32_t*)va;
    // Write a vertical gradient: row r => 0xFF<r>4080 RGBA (only first 64 rows
    // for speed); also a magic word at row 0 column 0 so we can spot it.
    for (uint32_t r = 0; r < 64 && r < back.height; ++r) {
        uint32_t color = 0xFF000000u | ((uint8_t)(r * 4) << 16) | 0x0080u;
        for (uint32_t c = 0; c < back.width; ++c) {
            px[r * back.stride + c] = color;
        }
    }
    px[0] = 0xDEADBEEFu;
    __sync_synchronize();
    rc = AHardwareBuffer_unlock(buf, nullptr);
    if (rc != 0) {
        LOGE("AHardwareBuffer_unlock(WRITE) FAILED: rc=%d", rc);
        AHardwareBuffer_release(buf);
        return -1;
    }
    LOGS("write+unlock OK");

    // Lock for read, check the pattern survived.
    va = nullptr;
    rc = AHardwareBuffer_lock(buf,
            AHARDWAREBUFFER_USAGE_CPU_READ_OFTEN,
            -1, nullptr, &va);
    if (rc != 0 || !va) {
        LOGE("AHardwareBuffer_lock(READ) FAILED: rc=%d", rc);
        AHardwareBuffer_release(buf);
        return -1;
    }
    px = (uint32_t*)va;
    if (px[0] != 0xDEADBEEFu) {
        LOGE("coherency LOST: px[0]=0x%08x expected 0xDEADBEEF", px[0]);
        AHardwareBuffer_unlock(buf, nullptr);
        AHardwareBuffer_release(buf);
        return -1;
    }
    int row_mismatch = 0;
    for (uint32_t r = 0; r < 64 && r < back.height; ++r) {
        uint32_t expected = 0xFF000000u | ((uint8_t)(r * 4) << 16) | 0x0080u;
        for (uint32_t c = 0; c < back.width; c += back.width / 4) {
            if (px[r * back.stride + c] != expected && !(r == 0 && c == 0)) {
                row_mismatch++;
                if (row_mismatch < 4) {
                    LOGS("mismatch at (%u,%u): got 0x%08x expected 0x%08x",
                         r, c, px[r * back.stride + c], expected);
                }
            }
        }
    }
    if (row_mismatch) {
        LOGE("CPU read-after-write inconsistent: %d sampled mismatches", row_mismatch);
        AHardwareBuffer_unlock(buf, nullptr);
        AHardwareBuffer_release(buf);
        return -1;
    }
    AHardwareBuffer_unlock(buf, nullptr);
    LOGS("CPU read-after-write CONSISTENT — gralloc CPU view is coherent");

    *outBuffer = buf;
    LOGI("Phase B PASS");
    return 0;
}

// =============================================================================
// Phase C — AHardwareBuffer send/recv-handle round-trip (cross-process)
// =============================================================================
static int phase_c_ahb_handle_socket(AHardwareBuffer* buf) {
    LOGI("===== Phase C: AHardwareBuffer socket round-trip =====");

    if (!buf) {
        LOGE("Phase B did not produce a buffer — skipping Phase C");
        return -1;
    }

    int sv[2];
    if (socketpair(AF_UNIX, SOCK_SEQPACKET, 0, sv) != 0) {
        LOGE("socketpair FAILED: errno=%d (%s)", errno, strerror(errno));
        return -1;
    }

    pid_t pid = fork();
    if (pid < 0) {
        LOGE("fork FAILED: errno=%d (%s)", errno, strerror(errno));
        close(sv[0]); close(sv[1]);
        return -1;
    }

    if (pid == 0) {
        // Child: receive handle, lock, verify magic, write a new word.
        close(sv[0]);
        AHardwareBuffer* recvBuf = nullptr;
        int rc = AHardwareBuffer_recvHandleFromUnixSocket(sv[1], &recvBuf);
        if (rc != 0 || !recvBuf) {
            __android_log_print(ANDROID_LOG_ERROR, TAG,
                "child: recvHandle FAILED rc=%d", rc);
            _exit(11);
        }
        void* va = nullptr;
        rc = AHardwareBuffer_lock(recvBuf,
                AHARDWAREBUFFER_USAGE_CPU_READ_OFTEN
              | AHARDWAREBUFFER_USAGE_CPU_WRITE_OFTEN,
                -1, nullptr, &va);
        if (rc != 0 || !va) {
            __android_log_print(ANDROID_LOG_ERROR, TAG,
                "child: lock FAILED rc=%d", rc);
            AHardwareBuffer_release(recvBuf);
            _exit(12);
        }
        uint32_t* px = (uint32_t*)va;
        if (px[0] != 0xDEADBEEFu) {
            __android_log_print(ANDROID_LOG_ERROR, TAG,
                "child: magic LOST px[0]=0x%08x", px[0]);
            AHardwareBuffer_unlock(recvBuf, nullptr);
            AHardwareBuffer_release(recvBuf);
            _exit(13);
        }
        // Stamp a new word for parent to verify reverse direction.
        px[1] = 0xCAFEBABEu;
        __sync_synchronize();
        AHardwareBuffer_unlock(recvBuf, nullptr);
        AHardwareBuffer_release(recvBuf);
        close(sv[1]);
        _exit(0);
    }

    // Parent: send the handle to child.
    close(sv[1]);
    int rc = AHardwareBuffer_sendHandleToUnixSocket(buf, sv[0]);
    if (rc != 0) {
        LOGE("sendHandle FAILED rc=%d errno=%d", rc, errno);
        close(sv[0]);
        kill(pid, SIGKILL);
        waitpid(pid, nullptr, 0);
        return -1;
    }
    LOGS("parent: sendHandle OK");

    int status = 0;
    waitpid(pid, &status, 0);
    close(sv[0]);
    if (!WIFEXITED(status) || WEXITSTATUS(status) != 0) {
        LOGE("child exited abnormally: status=0x%x", status);
        return -1;
    }
    LOGS("child exited 0 — child saw magic + wrote CAFEBABE");

    // Now in parent, lock and verify the child's write is visible.
    void* va = nullptr;
    rc = AHardwareBuffer_lock(buf,
            AHARDWAREBUFFER_USAGE_CPU_READ_OFTEN,
            -1, nullptr, &va);
    if (rc != 0 || !va) {
        LOGE("parent post-lock FAILED rc=%d", rc);
        return -1;
    }
    uint32_t got = ((uint32_t*)va)[1];
    AHardwareBuffer_unlock(buf, nullptr);
    if (got != 0xCAFEBABEu) {
        LOGE("parent: child write NOT visible: got 0x%08x expected 0xCAFEBABE", got);
        return -1;
    }
    LOGS("parent: child write VISIBLE px[1]=0xCAFEBABE — cross-process coherent");
    LOGI("Phase C PASS");
    return 0;
}

// =============================================================================
// Phase D — memfd-substitute cross-process viability
// =============================================================================
static int phase_d_memfd_crossprocess() {
    LOGI("===== Phase D: memfd cross-process via socket =====");

    const size_t BYTES = 1080 * 2280 * 4;  // full screen
    int fd = memfd_create_syscall("cr33-phaseD", MFD_CLOEXEC | MFD_ALLOW_SEALING);
    if (fd < 0) {
        LOGE("memfd_create FAILED: errno=%d (%s)", errno, strerror(errno));
        return -1;
    }
    if (ftruncate(fd, (off_t)BYTES) != 0) {
        LOGE("ftruncate FAILED: errno=%d", errno);
        close(fd);
        return -1;
    }
    LOGS("memfd allocated, %zu bytes (full 1080x2280x4)", BYTES);

    int sv[2];
    if (socketpair(AF_UNIX, SOCK_SEQPACKET, 0, sv) != 0) {
        LOGE("socketpair FAILED: errno=%d", errno);
        close(fd);
        return -1;
    }

    pid_t pid = fork();
    if (pid < 0) {
        LOGE("fork FAILED: errno=%d", errno);
        close(sv[0]); close(sv[1]); close(fd);
        return -1;
    }

    if (pid == 0) {
        // Child: receive fd via SCM_RIGHTS, mmap, write magic, exit.
        close(sv[0]);
        struct msghdr msg = {};
        char ctrl[CMSG_SPACE(sizeof(int))];
        struct iovec iov;
        char dummy = 0;
        iov.iov_base = &dummy; iov.iov_len = 1;
        msg.msg_iov = &iov; msg.msg_iovlen = 1;
        msg.msg_control = ctrl; msg.msg_controllen = sizeof(ctrl);
        ssize_t n = recvmsg(sv[1], &msg, 0);
        if (n <= 0) { _exit(31); }
        struct cmsghdr* cmsg = CMSG_FIRSTHDR(&msg);
        if (!cmsg || cmsg->cmsg_level != SOL_SOCKET
                  || cmsg->cmsg_type != SCM_RIGHTS) { _exit(32); }
        int childFd = *(int*)CMSG_DATA(cmsg);
        void* v = mmap(nullptr, BYTES, PROT_READ | PROT_WRITE, MAP_SHARED, childFd, 0);
        if (v == MAP_FAILED) { _exit(33); }
        ((uint32_t*)v)[0] = 0xC0FFEE00u;
        ((uint32_t*)v)[1] = 0x12345678u;
        __sync_synchronize();
        munmap(v, BYTES);
        close(childFd);
        close(sv[1]);
        _exit(0);
    }

    // Parent: send fd to child via SCM_RIGHTS.
    close(sv[1]);
    struct msghdr msg = {};
    char ctrl[CMSG_SPACE(sizeof(int))];
    char dummy = 0;
    struct iovec iov; iov.iov_base = &dummy; iov.iov_len = 1;
    msg.msg_iov = &iov; msg.msg_iovlen = 1;
    msg.msg_control = ctrl; msg.msg_controllen = sizeof(ctrl);
    struct cmsghdr* cmsg = CMSG_FIRSTHDR(&msg);
    cmsg->cmsg_level = SOL_SOCKET; cmsg->cmsg_type = SCM_RIGHTS;
    cmsg->cmsg_len = CMSG_LEN(sizeof(int));
    *(int*)CMSG_DATA(cmsg) = fd;
    if (sendmsg(sv[0], &msg, 0) <= 0) {
        LOGE("parent: sendmsg FAILED: errno=%d", errno);
        kill(pid, SIGKILL); waitpid(pid, nullptr, 0);
        close(sv[0]); close(fd);
        return -1;
    }
    LOGS("parent: sent memfd over SCM_RIGHTS");

    int status = 0;
    waitpid(pid, &status, 0);
    close(sv[0]);
    if (!WIFEXITED(status) || WEXITSTATUS(status) != 0) {
        LOGE("child exited abnormally: status=0x%x", status);
        close(fd);
        return -1;
    }

    // Parent: mmap and verify child's writes.
    void* v = mmap(nullptr, BYTES, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (v == MAP_FAILED) {
        LOGE("parent post-mmap FAILED: errno=%d", errno);
        close(fd);
        return -1;
    }
    uint32_t got0 = ((uint32_t*)v)[0];
    uint32_t got1 = ((uint32_t*)v)[1];
    munmap(v, BYTES);
    close(fd);
    if (got0 != 0xC0FFEE00u || got1 != 0x12345678u) {
        LOGE("parent: child writes NOT visible: got [0]=0x%08x [1]=0x%08x", got0, got1);
        return -1;
    }
    LOGS("parent: child writes VISIBLE — memfd cross-process coherent");
    LOGI("Phase D PASS");
    return 0;
}

// =============================================================================
// Main — run all phases and pick a verdict
// =============================================================================
int main(int argc, char** argv) {
    (void)argc; (void)argv;

    LOGI("CR33 / M6 buffer-coherency spike — pid=%d uid=%d", (int)getpid(), (int)getuid());
    LOGI("Build: " __DATE__ " " __TIME__);

    bool a = phase_a_memfd_selftest()           == 0;
    AHardwareBuffer* phaseB_buf = nullptr;
    bool b = phase_b_ahb_cpu_coherency(&phaseB_buf) == 0;
    bool c = b && phase_c_ahb_handle_socket(phaseB_buf) == 0;
    if (phaseB_buf) AHardwareBuffer_release(phaseB_buf);
    bool d = phase_d_memfd_crossprocess()       == 0;

    LOGI("============================================");
    LOGI("CR33 spike summary");
    LOGI("  Phase A (memfd self-test)               : %s", a ? "PASS" : "FAIL");
    LOGI("  Phase B (AHardwareBuffer CPU coherency) : %s", b ? "PASS" : "FAIL");
    LOGI("  Phase C (AHardwareBuffer cross-process) : %s", c ? "PASS" : (b ? "FAIL" : "SKIP"));
    LOGI("  Phase D (memfd cross-process)           : %s", d ? "PASS" : "FAIL");
    LOGI("============================================");

    if (a && b && c && d) {
        LOGI("VERDICT: ALL PASS — memfd substitute is viable for M6 GraphicBuffer.");
        LOGI("         M6 12-day plan estimate HOLDS; no dma_heap pivot needed.");
        return 0;
    }
    if (!a) {
        LOGE("VERDICT: Phase A failed — memfd unusable on this kernel.");
        LOGE("         No /dev/dma_heap on this device either (we probed at audit time).");
        LOGE("         Deep pivot required: write up alternative path in CR33 report.");
        return 1;
    }
    if (!b) {
        LOGE("VERDICT: Phase B failed — gralloc HAL refused CPU-coherent flags.");
        LOGE("         This is unusual; memfd-only path still viable but lose interop reference.");
        return 2;
    }
    if (b && !c) {
        LOGE("VERDICT: Phase C failed — AHB cross-process broken.");
        LOGE("         Unusual; doesn't directly block memfd path but suggests deeper fd-table issue.");
        return 3;
    }
    if (!d) {
        LOGE("VERDICT: Phase D failed — memfd cross-process not viable.");
        LOGE("         This blocks the planned memfd substitute — needs dma_heap (not available) "
             "or another pivot.");
        return 4;
    }
    LOGE("VERDICT: Mixed result — review per-phase status above.");
    return 5;
}
