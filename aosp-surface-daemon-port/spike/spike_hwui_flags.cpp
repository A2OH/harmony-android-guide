// =============================================================================
// CR33 spike — supplemental probe: HWUI-style AHardwareBuffer usage flags
// =============================================================================
//
// The main spike (spike.cpp) tests CPU-only usage flags.  But HWUI's
// RenderThread typically asks for buffers with GPU_FRAMEBUFFER | GPU_TEXTURE
// (i.e. USAGE_HW_RENDER | USAGE_HW_TEXTURE in libgui parlance) — see
// AOSP-11 frameworks/base/libs/hwui/renderthread/CanvasContext.cpp and
// SurfaceFlinger's createBufferQueueLayer.
//
// M6's plan §5.3 risk row #1 calls out: "if framework.jar's HWUI insists on
// EGL surface allocation paths instead of CPU mmap, Phase 1 fails."  This
// supplemental probe answers a related question: when HWUI does insist on
// GPU usage flags, does the gralloc allocator still give us back something
// CPU-mappable that our daemon-side consumer thread can read?
//
// Three sub-probes, ascending stringency:
//   1. GPU_FRAMEBUFFER alone        — render-target usage; HWUI minimal case.
//   2. GPU_FRAMEBUFFER+GPU_TEXTURE  — typical HWUI scratch buffer flags.
//   3. GPU_FRAMEBUFFER+GPU_TEXTURE+CPU_READ_RARELY+CPU_WRITE_RARELY
//                                   — what HWUI requests when the framework
//                                     wants a buffer it can also touch in CPU.
//
// For each, the test allocates → describes → tries to lock for CPU read.
// If the lock returns OK and the locked virtual address is valid, the buffer
// is CPU-mappable even with GPU usage flags — which means our daemon can
// safely substitute memfd at the *Producer.requestBuffer* boundary.
// If the lock returns INVALID_OPERATION (-EINVAL), we've found a buffer that
// the gralloc allocator marks as GPU-only — we'd need to discard those.
//
// Build: see build_hwui_flags.sh (mirror of build.sh with different source).
// =============================================================================

#define _GNU_SOURCE
#include <android/hardware_buffer.h>
#include <android/log.h>
#include <errno.h>
#include <inttypes.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#define TAG "westlake-cr33-spike-hwui"
#define LOGI(...) do { fprintf(stdout, "[I] " __VA_ARGS__); fputc('\n', stdout); \
                       __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__); } while (0)
#define LOGE(...) do { fprintf(stdout, "[E] " __VA_ARGS__); fputc('\n', stdout); \
                       __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__); } while (0)
#define LOGS(...) do { fprintf(stdout, "    " __VA_ARGS__); fputc('\n', stdout); } while (0)

struct Probe {
    const char* name;
    uint64_t    usage;
};

static int try_probe(const Probe& p) {
    LOGI("--- probe: %s (usage=0x%" PRIx64 ") ---", p.name, p.usage);
    AHardwareBuffer_Desc desc = {};
    desc.width  = 1080;
    desc.height = 2280;
    desc.layers = 1;
    desc.format = AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM;
    desc.usage  = p.usage;
    desc.stride = 0;

    AHardwareBuffer* buf = nullptr;
    int rc = AHardwareBuffer_allocate(&desc, &buf);
    if (rc != 0 || !buf) {
        LOGE("    allocate FAILED rc=%d (errno=%d)", rc, errno);
        return -1;
    }
    AHardwareBuffer_Desc back = {};
    AHardwareBuffer_describe(buf, &back);
    LOGS("    allocate OK; stride=%u backUsage=0x%" PRIx64,
         back.stride, (uint64_t)back.usage);

    // Try to lock for CPU read.
    void* va = nullptr;
    rc = AHardwareBuffer_lock(buf,
            AHARDWAREBUFFER_USAGE_CPU_READ_OFTEN,
            -1, nullptr, &va);
    if (rc != 0 || !va) {
        LOGS("    lock(CPU_READ_OFTEN) FAILED rc=%d — buffer is GPU-only", rc);
        AHardwareBuffer_release(buf);
        return 1;  // allocated but not CPU-mappable
    }
    LOGS("    lock(CPU_READ_OFTEN) OK — buffer IS CPU-mappable even with GPU flags");
    AHardwareBuffer_unlock(buf, nullptr);

    // Also try a CPU read+write lock (what M6's consumer would do to copy).
    va = nullptr;
    rc = AHardwareBuffer_lock(buf,
            AHARDWAREBUFFER_USAGE_CPU_READ_OFTEN
          | AHARDWAREBUFFER_USAGE_CPU_WRITE_OFTEN,
            -1, nullptr, &va);
    if (rc == 0 && va) {
        ((uint32_t*)va)[0] = 0xBADC0DEDu;
        AHardwareBuffer_unlock(buf, nullptr);
        LOGS("    lock(CPU_READ+WRITE) OK + wrote 0xBADC0DED");
    } else {
        LOGS("    lock(CPU_READ+WRITE) FAILED rc=%d", rc);
    }
    AHardwareBuffer_release(buf);
    return 0;
}

int main(int argc, char** argv) {
    (void)argc; (void)argv;
    LOGI("CR33 supplemental: AHardwareBuffer HWUI-flag probe (pid=%d)", (int)getpid());

    Probe probes[] = {
        { "GPU_FRAMEBUFFER",
          AHARDWAREBUFFER_USAGE_GPU_FRAMEBUFFER },
        { "GPU_FRAMEBUFFER|GPU_SAMPLED_IMAGE",
          AHARDWAREBUFFER_USAGE_GPU_FRAMEBUFFER
        | AHARDWAREBUFFER_USAGE_GPU_SAMPLED_IMAGE },
        { "GPU_FRAMEBUFFER|GPU_SAMPLED_IMAGE|CPU_READ_RARELY|CPU_WRITE_RARELY",
          AHARDWAREBUFFER_USAGE_GPU_FRAMEBUFFER
        | AHARDWAREBUFFER_USAGE_GPU_SAMPLED_IMAGE
        | AHARDWAREBUFFER_USAGE_CPU_READ_RARELY
        | AHARDWAREBUFFER_USAGE_CPU_WRITE_RARELY },
        { "GPU_FRAMEBUFFER|GPU_SAMPLED_IMAGE|CPU_READ_OFTEN|CPU_WRITE_OFTEN",
          AHARDWAREBUFFER_USAGE_GPU_FRAMEBUFFER
        | AHARDWAREBUFFER_USAGE_GPU_SAMPLED_IMAGE
        | AHARDWAREBUFFER_USAGE_CPU_READ_OFTEN
        | AHARDWAREBUFFER_USAGE_CPU_WRITE_OFTEN },
    };

    int alloc_ok = 0, cpu_lock_ok = 0;
    for (auto& p : probes) {
        int r = try_probe(p);
        if (r == 0) { alloc_ok++; cpu_lock_ok++; }
        else if (r == 1) { alloc_ok++; }
    }

    LOGI("============================================");
    LOGI("CR33 HWUI-flag probe summary");
    LOGI("  allocations succeeded   : %d / %zu", alloc_ok, sizeof(probes)/sizeof(probes[0]));
    LOGI("  CPU-lockable allocations: %d / %zu", cpu_lock_ok, sizeof(probes)/sizeof(probes[0]));
    LOGI("============================================");
    if (cpu_lock_ok == (int)(sizeof(probes)/sizeof(probes[0]))) {
        LOGI("INTERPRETATION: every HWUI-style allocation is CPU-mappable on this device.");
        LOGI("                M6 daemon-side consumer can read buffers regardless of usage flags.");
        return 0;
    }
    if (cpu_lock_ok > 0) {
        LOGI("INTERPRETATION: some HWUI-style allocations are CPU-mappable.");
        LOGI("                The CPU_READ_*-bearing combinations are safe.");
        LOGI("                M6 daemon must intercept dequeueBuffer and force CPU_READ bits on.");
        return 0;
    }
    LOGE("INTERPRETATION: no GPU-flagged allocation is CPU-mappable.");
    LOGE("                M6 path: enforce CPU_READ_OFTEN on every dequeueBuffer.");
    return 1;
}
