/*
 * oh_anativewindow_shim.cpp — AOSP ANativeWindow ABI ↔ OH NDK bridge.
 *
 * See oh_anativewindow_shim.h for rationale + design (§7.11 in
 * graphics_rendering_design.html).
 *
 * Lifetime model:
 *   - Each oh_anw_wrap allocates one AdapterAnw on heap.
 *   - hwui sees the embedded ANativeWindow first field; all 10 function
 *     pointers route to static wrappers in this file.
 *   - Wrappers reinterpret_cast the ANativeWindow* arg back to AdapterAnw*
 *     using offsetof identity (anw struct is the first field).
 *   - Buffer cache: dequeueBuffer creates AdapterAnwBuffer wrapping OH
 *     OHNativeWindowBuffer; queue/cancel/release look up the cached
 *     wrapper by AOSP buffer pointer identity. Cache is per-shim.
 *
 * NDK constants referenced:
 *   AOSP NATIVE_WINDOW_*           — system/window.h
 *   OH NativeWindowOperation       — external_window.h
 */

#include "oh_anativewindow_shim.h"

#include <errno.h>
#include <stdarg.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>     // close()
#include <atomic>
#include <mutex>
#include <new>          // std::nothrow
#include <unordered_map>

// AOSP ANativeWindow + ANativeWindowBuffer + android_native_base_t — byte-exact
// copy from AOSP frameworks/native/libs/nativewindow/include/system/window.h
// and frameworks/native/libs/nativebase/include/nativebase/nativebase.h.
//
// Embedded directly here (rather than #include) because the OH cross-compile
// environment for oh_adapter_bridge.so doesn't have AOSP's include path on
// its -I list, and adding it would pull in transitive AOSP dependencies that
// drift. The hwui binary we link against uses these exact offsets, so any
// drift from AOSP source layout would cause silent ABI mismatch — keep this
// block in lockstep with AOSP frameworks/native/libs/nativewindow/include/
// system/window.h struct ANativeWindow.
extern "C" {

#define ANDROID_NATIVE_WINDOW_MAGIC \
    ((((int)'_') << 24) | (((int)'w') << 16) | (((int)'n') << 8) | ((int)'d'))
#define ANDROID_NATIVE_BUFFER_MAGIC \
    ((((int)'_') << 24) | (((int)'b') << 16) | (((int)'f') << 8) | ((int)'r'))

typedef struct android_native_base_t {
    int magic;
    int version;
    void* reserved[4];
    void (*incRef)(struct android_native_base_t* base);
    void (*decRef)(struct android_native_base_t* base);
} android_native_base_t;

typedef struct ANativeWindowBuffer {
    struct android_native_base_t common;
    int width;
    int height;
    int stride;
    int format;
    int usage_deprecated;
    uintptr_t layerCount;
    void* reserved[1];
    const struct native_handle_t* handle;
    uint64_t usage;
    void* reserved_proc[8 - (sizeof(uint64_t) / sizeof(void*))];
} ANativeWindowBuffer_t;
typedef struct ANativeWindowBuffer ANativeWindowBuffer;

struct ANativeWindow {
    struct android_native_base_t common;
    const uint32_t flags;
    const int   minSwapInterval;
    const int   maxSwapInterval;
    const float xdpi;
    const float ydpi;
    intptr_t    oem[4];
    int (*setSwapInterval)(struct ANativeWindow* window, int interval);
    int (*dequeueBuffer_DEPRECATED)(struct ANativeWindow* window, struct ANativeWindowBuffer** buffer);
    int (*lockBuffer_DEPRECATED)(struct ANativeWindow* window, struct ANativeWindowBuffer* buffer);
    int (*queueBuffer_DEPRECATED)(struct ANativeWindow* window, struct ANativeWindowBuffer* buffer);
    int (*query)(const struct ANativeWindow* window, int what, int* value);
    int (*perform)(struct ANativeWindow* window, int operation, ...);
    int (*cancelBuffer_DEPRECATED)(struct ANativeWindow* window, struct ANativeWindowBuffer* buffer);
    int (*dequeueBuffer)(struct ANativeWindow* window, struct ANativeWindowBuffer** buffer, int* fenceFd);
    int (*queueBuffer)(struct ANativeWindow* window, struct ANativeWindowBuffer* buffer, int fenceFd);
    int (*cancelBuffer)(struct ANativeWindow* window, struct ANativeWindowBuffer* buffer, int fenceFd);
};

// AOSP NATIVE_WINDOW_* constants — query and perform live in separate enums
// in upstream AOSP (system/window.h) but share numeric ranges; we keep them
// in two enums here for symbol cleanliness (and to avoid C++ warning about
// duplicate enumerator values when used in the same switch).
enum {  /* query 'what' codes */
    NATIVE_WINDOW_WIDTH                          = 0,
    NATIVE_WINDOW_HEIGHT                         = 1,
    NATIVE_WINDOW_FORMAT                         = 2,
    NATIVE_WINDOW_MIN_UNDEQUEUED_BUFFERS         = 3,
    NATIVE_WINDOW_QUEUES_TO_WINDOW_COMPOSER      = 4,
    NATIVE_WINDOW_CONCRETE_TYPE                  = 5,
    NATIVE_WINDOW_DEFAULT_WIDTH                  = 6,
    NATIVE_WINDOW_DEFAULT_HEIGHT                 = 7,
    NATIVE_WINDOW_TRANSFORM_HINT                 = 8,
    NATIVE_WINDOW_BUFFER_AGE                     = 13,
    NATIVE_WINDOW_LAYER_COUNT                    = 16,
    NATIVE_WINDOW_IS_VALID                       = 17,
    NATIVE_WINDOW_DATASPACE                      = 20,
    NATIVE_WINDOW_MAX_BUFFER_COUNT               = 21,
};

enum {  /* perform 'operation' codes */
    NATIVE_WINDOW_SET_USAGE                      = 0,
    NATIVE_WINDOW_CONNECT                        = 1,
    NATIVE_WINDOW_DISCONNECT                     = 2,
    NATIVE_WINDOW_SET_CROP                       = 3,
    NATIVE_WINDOW_SET_BUFFER_COUNT               = 4,
    NATIVE_WINDOW_SET_BUFFERS_GEOMETRY           = 5,
    NATIVE_WINDOW_SET_BUFFERS_TRANSFORM          = 6,
    NATIVE_WINDOW_SET_BUFFERS_TIMESTAMP          = 7,
    NATIVE_WINDOW_SET_BUFFERS_DIMENSIONS         = 8,
    NATIVE_WINDOW_SET_BUFFERS_FORMAT             = 9,
    NATIVE_WINDOW_SET_SCALING_MODE               = 10,
    NATIVE_WINDOW_LOCK                           = 11,
    NATIVE_WINDOW_UNLOCK_AND_POST                = 12,
    NATIVE_WINDOW_API_CONNECT                    = 13,
    NATIVE_WINDOW_API_DISCONNECT                 = 14,
    NATIVE_WINDOW_SET_BUFFERS_USER_DIMENSIONS    = 15,
    NATIVE_WINDOW_SET_POST_TRANSFORM_CROP        = 16,
    NATIVE_WINDOW_SET_BUFFERS_STICKY_TRANSFORM   = 17,
};

}  // extern "C" — AOSP ANativeWindow ABI

// OH NDK forward decls — copied minimal subset to avoid -I cross-namespace.
// Source: ~/oh/foundation/graphic/graphic_surface/interfaces/inner_api/surface/external_window.h
extern "C" {
struct OHNativeWindow;
struct OHNativeWindowBuffer;
struct BufferHandle;

// NativeWindowOperation enum codes (subset we use)
enum {
    OH_OP_SET_BUFFER_GEOMETRY = 0,
    OH_OP_GET_BUFFER_GEOMETRY = 1,
    OH_OP_GET_FORMAT          = 2,
    OH_OP_SET_FORMAT          = 3,
    OH_OP_GET_USAGE           = 4,
    OH_OP_SET_USAGE           = 5,
    OH_OP_SET_STRIDE          = 6,
    OH_OP_GET_STRIDE          = 7,
    OH_OP_SET_SWAP_INTERVAL   = 8,
    OH_OP_GET_SWAP_INTERVAL   = 9,
    OH_OP_SET_TIMEOUT         = 10,
    OH_OP_GET_TIMEOUT         = 11,
    OH_OP_SET_COLOR_GAMUT     = 12,
    OH_OP_GET_COLOR_GAMUT     = 13,
    OH_OP_SET_TRANSFORM       = 14,
    OH_OP_GET_TRANSFORM       = 15,
};

// Region forward decl (used by FlushBuffer)
struct Region {
    int32_t reserved;
};

int32_t OH_NativeWindow_NativeWindowRequestBuffer(OHNativeWindow* window,
                                                   OHNativeWindowBuffer** buffer,
                                                   int* fenceFd);
int32_t OH_NativeWindow_NativeWindowFlushBuffer(OHNativeWindow* window,
                                                 OHNativeWindowBuffer* buffer,
                                                 int fenceFd, struct Region region);
int32_t OH_NativeWindow_NativeWindowAbortBuffer(OHNativeWindow* window,
                                                 OHNativeWindowBuffer* buffer);
int32_t OH_NativeWindow_NativeWindowHandleOpt(OHNativeWindow* window, int code, ...);
int32_t OH_NativeObjectReference(void* obj);    // generic for OH NativeObject
int32_t OH_NativeObjectUnreference(void* obj);
// 2026-05-12 G2.14aw probe A.2: read producer uniqueId — used to verify
// hwui's NativeWindow is the same producer ID as the one created in
// OHWindowManagerClient::getOhNativeWindow.
int32_t OH_NativeWindow_GetSurfaceId(OHNativeWindow* window, uint64_t* surfaceId);

// HiLog forward decl (same trick as compat_shim)
int HiLogPrint(int type, int level, unsigned int domain,
               const char* tag, const char* fmt, ...);
}  // extern "C"

#define ALOGI(...) HiLogPrint(3, 4, 0xD000F00u, "OH_AnwShim", __VA_ARGS__)
#define ALOGW(...) HiLogPrint(3, 5, 0xD000F00u, "OH_AnwShim", __VA_ARGS__)
#define ALOGE(...) HiLogPrint(3, 6, 0xD000F00u, "OH_AnwShim", __VA_ARGS__)

namespace {

constexpr uint32_t kAdapterAnwMagic    = 0x414E5731u; /* 'ANW1' */
constexpr uint32_t kAdapterAnwBufMagic = 0x414E4231u; /* 'ANB1' */

struct AdapterAnwBuffer {
    struct ANativeWindowBuffer aosp;       // AOSP ABI face — must be first
    OHNativeWindowBuffer*      oh;          // OH side handle
    uint32_t                   magic;
};

// adapter ANativeWindow bound to an OH window
struct AdapterAnw {
    struct ANativeWindow aosp;              // AOSP ABI face — must be first
    OHNativeWindow*      oh;                 // OH side handle
    uint32_t             magic;
    std::atomic<int32_t> refCount;          // android_native_base_t refcount
    std::mutex                                 bufCacheLock;
    std::unordered_map<OHNativeWindowBuffer*,
                       AdapterAnwBuffer*>      bufCache;  // OH buf → wrapper
};

inline AdapterAnw* as_adapter(struct ANativeWindow* w) {
    auto* a = reinterpret_cast<AdapterAnw*>(w);
    return (a && a->magic == kAdapterAnwMagic) ? a : nullptr;
}
inline AdapterAnw* as_adapter_const(const struct ANativeWindow* w) {
    return as_adapter(const_cast<struct ANativeWindow*>(w));
}
inline AdapterAnwBuffer* as_adapter_buf(struct ANativeWindowBuffer* b) {
    auto* x = reinterpret_cast<AdapterAnwBuffer*>(b);
    return (x && x->magic == kAdapterAnwBufMagic) ? x : nullptr;
}

// Recover the AdapterAnw* containing a given android_native_base_t*.
// Two hops: base→ANativeWindow (subtract offsetof(ANativeWindow, common))
// then ANativeWindow→AdapterAnw (zero, since aosp is the first field).
inline AdapterAnw* base_to_adapter(struct android_native_base_t* base) {
    auto* anw = reinterpret_cast<struct ANativeWindow*>(
        reinterpret_cast<char*>(base) - offsetof(struct ANativeWindow, common));
    return as_adapter(anw);
}

// android_native_base_t hooks (object refcount)
void anw_base_incRef(struct android_native_base_t* base) {
    auto* a = base_to_adapter(base);
    if (a) a->refCount.fetch_add(1, std::memory_order_acq_rel);
}
void anw_base_decRef(struct android_native_base_t* base) {
    auto* a = base_to_adapter(base);
    if (!a) return;
    int32_t prev = a->refCount.fetch_sub(1, std::memory_order_acq_rel);
    if (prev <= 1) {
        // Final release. We don't own the OH handle, so just delete shim.
        oh_anw_destroy(reinterpret_cast<struct ANativeWindow*>(a));
    }
}

// Buffer wrapping helpers
AdapterAnwBuffer* wrap_oh_buffer(AdapterAnw* a, OHNativeWindowBuffer* ohBuf) {
    if (!a || !ohBuf) return nullptr;
    std::lock_guard<std::mutex> lock(a->bufCacheLock);
    auto it = a->bufCache.find(ohBuf);
    if (it != a->bufCache.end()) return it->second;

    auto* w = new AdapterAnwBuffer();
    memset(&w->aosp, 0, sizeof(w->aosp));
    w->aosp.common.magic   = ANDROID_NATIVE_BUFFER_MAGIC;
    w->aosp.common.version = sizeof(struct ANativeWindowBuffer);
    // incRef/decRef intentionally null — hwui doesn't refcount buffers
    // returned from dequeueBuffer (the window is the owner); we only need
    // the magic + handle to round-trip.
    w->oh    = ohBuf;
    w->magic = kAdapterAnwBufMagic;
    a->bufCache[ohBuf] = w;
    return w;
}

OHNativeWindowBuffer* unwrap_aosp_buffer(AdapterAnw* a, ANativeWindowBuffer* aospBuf) {
    auto* w = as_adapter_buf(aospBuf);
    return (w && w->oh) ? w->oh : nullptr;
}

// 2026-05-11 G2.14aj phase-1: per-slot probe trampolines.
// Replaces the single G2.14ae oh_anw_noop_hook that masked all four
// common.reserved[N] slots with the same opaque no-op.  Each slot now
// has its own probe that logs:
//   - which slot fired (0/1/3 — slot 2 is `aanw_query` directly, see
//     oh_anw_wrap init below; objdump of CanvasContext::setupPipeline
//     Surface at 0x1160b0..0x1160b6 confirmed `ldr r3,[r6,#16]; blx r3`
//     with r1=NATIVE_WINDOW_MIN_UNDEQUEUED_BUFFERS — slot 2 = query)
//   - caller LR (feed into `llvm-addr2line -e libhwui.so 0x<LR>` to
//     map back to the AOSP source line that invoked the slot)
//   - first four argv-register values (covers the common 1- to 3-arg
//     hook signatures used across AOSP ANativeWindow hooks)
// Phase 1 keeps each probe non-fatal: it heuristically zero-writes a
// memory location pointed by a2 when a2 looks like a writable in-
// process address.  That mimics enough of a successful "query value-
// out" or "perform args" to keep the caller from immediately faulting
// on uninitialised stack memory (the original "window->query failed
// value=-166522112" symptom that masked rendering).
// Phase 2 (next iteration): replace each probe with the real handler
// once LR-trace pins down the AOSP function each slot maps to.

static int oh_anw_slot_probe_impl(int slot,
                                  unsigned a0, unsigned a1, unsigned a2,
                                  unsigned a3) {
    void* lr = __builtin_return_address(1);
    static std::atomic<int> s_called[4]{{0}, {0}, {0}, {0}};
    int n = s_called[slot & 3].fetch_add(1, std::memory_order_relaxed);
    if (n < 16) {
        ALOGW("[ANW slot %d call #%d] LR=%p args={%#x,%#x,%#x,%#x}",
              slot, n + 1, lr, a0, a1, a2, a3);
    }
    if (a2 >= 0x10000000u && a2 < 0xF0000000u) {
        *reinterpret_cast<int*>(static_cast<uintptr_t>(a2)) = 0;
    }
    return 0;
}
extern "C" int oh_anw_slot0_probe(unsigned a0, unsigned a1,
                                  unsigned a2, unsigned a3) {
    return oh_anw_slot_probe_impl(0, a0, a1, a2, a3);
}
extern "C" int oh_anw_slot1_probe(unsigned a0, unsigned a1,
                                  unsigned a2, unsigned a3) {
    return oh_anw_slot_probe_impl(1, a0, a1, a2, a3);
}
extern "C" int oh_anw_slot3_probe(unsigned a0, unsigned a1,
                                  unsigned a2, unsigned a3) {
    return oh_anw_slot_probe_impl(3, a0, a1, a2, a3);
}

// =========================================================================
// 10 AOSP function pointer hooks
// =========================================================================

int aanw_setSwapInterval(struct ANativeWindow* w, int interval) {
    auto* a = as_adapter(w);
    if (!a) return -EINVAL;
    int32_t v = interval;
    return OH_NativeWindow_NativeWindowHandleOpt(a->oh, OH_OP_SET_SWAP_INTERVAL, v);
}

int aanw_dequeueBuffer(struct ANativeWindow* w,
                       struct ANativeWindowBuffer** outBuf, int* outFenceFd) {
    auto* a = as_adapter(w);
    if (!a) return -EINVAL;
    OHNativeWindowBuffer* ohBuf = nullptr;
    int fence = -1;
    int32_t rc = OH_NativeWindow_NativeWindowRequestBuffer(a->oh, &ohBuf, &fence);
    if (rc != 0) {
        ALOGW("dequeueBuffer rc=%d", rc);
        return -EIO;
    }
    AdapterAnwBuffer* wb = wrap_oh_buffer(a, ohBuf);
    if (!wb) return -ENOMEM;
    *outBuf = &wb->aosp;
    if (outFenceFd) *outFenceFd = fence;
    return 0;
}

int aanw_queueBuffer(struct ANativeWindow* w,
                     struct ANativeWindowBuffer* buf, int fenceFd) {
    auto* a = as_adapter(w);
    if (!a) return -EINVAL;
    OHNativeWindowBuffer* ohBuf = unwrap_aosp_buffer(a, buf);
    if (!ohBuf) return -EINVAL;
    // 2026-05-12 G2.14aw probe A.2: log uniqueId on first frames + every 60th.
    // Compare with createSession-time uniqueId to confirm hwui swap path is on
    // the same producer ID. Counter avoids log flood at 60Hz.
    static std::atomic<uint32_t> s_qbCount{0};
    uint32_t n = s_qbCount.fetch_add(1, std::memory_order_relaxed);
    if (n < 5 || (n % 60) == 0) {
        uint64_t uid = 0;
        int32_t qrc = OH_NativeWindow_GetSurfaceId(a->oh, &uid);
        ALOGI("aanw_queueBuffer[probe #%u]: AdapterAnw=%p oh=%p ohBuf=%p fence=%d uniqueId=0x%llx (rc=%d)",
              n, (void*) a, (void*) a->oh, (void*) ohBuf, fenceFd,
              (unsigned long long) uid, qrc);
    }
    struct Region region = {0};  // empty region = full surface
    int32_t rc = OH_NativeWindow_NativeWindowFlushBuffer(a->oh, ohBuf, fenceFd, region);
    if (rc != 0) {
        ALOGW("queueBuffer rc=%d", rc);
        return -EIO;
    }
    return 0;
}

int aanw_cancelBuffer(struct ANativeWindow* w,
                      struct ANativeWindowBuffer* buf, int /*fenceFd*/) {
    auto* a = as_adapter(w);
    if (!a) return -EINVAL;
    OHNativeWindowBuffer* ohBuf = unwrap_aosp_buffer(a, buf);
    if (!ohBuf) return -EINVAL;
    return OH_NativeWindow_NativeWindowAbortBuffer(a->oh, ohBuf);
}

// Deprecated variants — hwui rarely calls these but provide non-crashing
// stubs that delegate to non-deprecated versions where possible.
int aanw_dequeueBuffer_DEPRECATED(struct ANativeWindow* w,
                                   struct ANativeWindowBuffer** buf) {
    int fence = -1;
    int rc = aanw_dequeueBuffer(w, buf, &fence);
    if (rc == 0 && fence >= 0) close(fence);
    return rc;
}
int aanw_lockBuffer_DEPRECATED(struct ANativeWindow* /*w*/,
                                struct ANativeWindowBuffer* /*buf*/) {
    // AOSP doc: this is a no-op for binary compat.
    return 0;
}
int aanw_queueBuffer_DEPRECATED(struct ANativeWindow* w,
                                 struct ANativeWindowBuffer* buf) {
    return aanw_queueBuffer(w, buf, -1);
}
int aanw_cancelBuffer_DEPRECATED(struct ANativeWindow* w,
                                  struct ANativeWindowBuffer* buf) {
    return aanw_cancelBuffer(w, buf, -1);
}

// query: what (NATIVE_WINDOW_*) → out value
int aanw_query(const struct ANativeWindow* w, int what, int* value) {
    auto* a = as_adapter_const(w);
    if (!a || !value) return -EINVAL;
    switch (what) {
        case NATIVE_WINDOW_WIDTH:
        case NATIVE_WINDOW_DEFAULT_WIDTH: {
            int32_t hw = 0, ww = 0;
            OH_NativeWindow_NativeWindowHandleOpt(a->oh, OH_OP_GET_BUFFER_GEOMETRY, &hw, &ww);
            *value = ww;
            return 0;
        }
        case NATIVE_WINDOW_HEIGHT:
        case NATIVE_WINDOW_DEFAULT_HEIGHT: {
            int32_t hw = 0, ww = 0;
            OH_NativeWindow_NativeWindowHandleOpt(a->oh, OH_OP_GET_BUFFER_GEOMETRY, &hw, &ww);
            *value = hw;
            return 0;
        }
        case NATIVE_WINDOW_FORMAT: {
            int32_t fmt = 1; // R8G8B8A8 default
            OH_NativeWindow_NativeWindowHandleOpt(a->oh, OH_OP_GET_FORMAT, &fmt);
            *value = fmt;
            return 0;
        }
        case NATIVE_WINDOW_MIN_UNDEQUEUED_BUFFERS:
            *value = 1;     // safe default for triple-buffered queue
            return 0;
        case NATIVE_WINDOW_QUEUES_TO_WINDOW_COMPOSER:
            *value = 1;     // OH RS is the composer
            return 0;
        case NATIVE_WINDOW_CONCRETE_TYPE:
            *value = 0;     // unspecified type
            return 0;
        case NATIVE_WINDOW_TRANSFORM_HINT:
            *value = 0;     // no transform on RK3568 baseline
            return 0;
        case NATIVE_WINDOW_BUFFER_AGE:
            *value = 0;     // age tracking not supported via OH NDK; force redraw
            return 0;
        case NATIVE_WINDOW_LAYER_COUNT:
            *value = 1;
            return 0;
        case NATIVE_WINDOW_IS_VALID:
            *value = 1;
            return 0;
        case NATIVE_WINDOW_DATASPACE:
            *value = 142671872; // ADATASPACE_SRGB (matches §7.9 ADisplay shim)
            return 0;
        case NATIVE_WINDOW_MAX_BUFFER_COUNT:
            *value = 3;     // OH BufferQueue triple-buffer
            return 0;
        default:
            ALOGW("query unsupported what=%d", what);
            *value = 0;
            return -ENOSYS;
    }
}

// perform: variadic dispatch on operation code
int aanw_perform(struct ANativeWindow* w, int op, ...) {
    auto* a = as_adapter(w);
    if (!a) return -EINVAL;
    va_list ap;
    va_start(ap, op);
    int rc = 0;
    switch (op) {
        case NATIVE_WINDOW_SET_USAGE: {
            int usage = va_arg(ap, int);
            uint64_t u64 = (uint32_t) usage;
            rc = OH_NativeWindow_NativeWindowHandleOpt(a->oh, OH_OP_SET_USAGE, u64);
            break;
        }
        case NATIVE_WINDOW_SET_BUFFERS_DIMENSIONS:
        case NATIVE_WINDOW_SET_BUFFERS_GEOMETRY: {
            int32_t width  = va_arg(ap, int);
            int32_t height = va_arg(ap, int);
            // NATIVE_WINDOW_SET_BUFFERS_GEOMETRY also has a format param
            int32_t format = -1;
            if (op == NATIVE_WINDOW_SET_BUFFERS_GEOMETRY) {
                format = va_arg(ap, int);
            }
            rc = OH_NativeWindow_NativeWindowHandleOpt(a->oh, OH_OP_SET_BUFFER_GEOMETRY, width, height);
            if (rc == 0 && format > 0) {
                OH_NativeWindow_NativeWindowHandleOpt(a->oh, OH_OP_SET_FORMAT, format);
            }
            break;
        }
        case NATIVE_WINDOW_SET_BUFFERS_FORMAT: {
            int32_t format = va_arg(ap, int);
            rc = OH_NativeWindow_NativeWindowHandleOpt(a->oh, OH_OP_SET_FORMAT, format);
            break;
        }
        case NATIVE_WINDOW_SET_BUFFERS_TRANSFORM: {
            int32_t transform = va_arg(ap, int);
            rc = OH_NativeWindow_NativeWindowHandleOpt(a->oh, OH_OP_SET_TRANSFORM, transform);
            break;
        }
        case NATIVE_WINDOW_SET_BUFFERS_TIMESTAMP:
            // OH 没有等价 op；hwui 用此设置 buffer presentation timestamp。
            // 忽略让 OH RS 用默认（vsync-aligned）时序。
            rc = 0;
            break;
        case NATIVE_WINDOW_API_CONNECT:
        case NATIVE_WINDOW_API_DISCONNECT:
        case NATIVE_WINDOW_CONNECT:        // deprecated alias
        case NATIVE_WINDOW_DISCONNECT:     // deprecated alias
            // OH NativeWindow 不需要 connect/disconnect 协议（OH BufferQueue
            // producer-consumer 由 RS 维护连接生命周期）。No-op success.
            rc = 0;
            break;
        case NATIVE_WINDOW_SET_BUFFER_COUNT: {
            // OH BufferQueue 的 queue size 通过 SetQueueSize 在 RS 端配置；
            // hwui 这边的请求当成 advisory，不实际改 OH。返 0 success。
            (void) va_arg(ap, int);
            rc = 0;
            break;
        }
        case NATIVE_WINDOW_SET_SCALING_MODE: {
            // 跟 OH NativeWindow 的 SCALING_MODE 概念基本一致，但 OH NDK
            // 暴露 OH_NativeWindow_NativeWindowSetScalingMode 是 per-buffer
            // 的，跟 AOSP perform 的 surface-wide 语义不完全对应。先 no-op
            // 让 hwui 默认 freeze 行为生效（OH 默认就是 freeze 等同效果）。
            (void) va_arg(ap, int);
            rc = 0;
            break;
        }
        case NATIVE_WINDOW_SET_BUFFERS_USER_DIMENSIONS:
        case NATIVE_WINDOW_SET_POST_TRANSFORM_CROP:
        case NATIVE_WINDOW_SET_BUFFERS_STICKY_TRANSFORM:
        case NATIVE_WINDOW_SET_CROP:
        case NATIVE_WINDOW_LOCK:
        case NATIVE_WINDOW_UNLOCK_AND_POST:
            // Private / deprecated ops — hwui 通常不调；安全忽略。
            rc = 0;
            break;
        default:
            ALOGW("perform unsupported op=%d", op);
            rc = -ENOSYS;
            break;
    }
    va_end(ap);
    return rc;
}

}  // anonymous namespace

// =========================================================================
// Public API
// =========================================================================

// Forward decls for slot probes defined above with extern "C" linkage —
// re-declared at namespace scope so they're addressable from oh_anw_wrap.
extern "C" int oh_anw_slot0_probe(unsigned, unsigned, unsigned, unsigned);
extern "C" int oh_anw_slot1_probe(unsigned, unsigned, unsigned, unsigned);
extern "C" int oh_anw_slot3_probe(unsigned, unsigned, unsigned, unsigned);

extern "C" struct ANativeWindow* oh_anw_wrap(OHNativeWindow* oh) {
    if (!oh) {
        ALOGE("oh_anw_wrap: oh handle is null");
        return nullptr;
    }
    // AdapterAnw embeds AOSP ANativeWindow with const fields (flags / xdpi /
    // ...) and trailing C++ metadata (atomic / mutex / unordered_map). The
    // const fields delete the implicit default ctor, so we cannot use
    // `new AdapterAnw()`. Instead: calloc raw memory (zero-init all bytes),
    // then placement-new the C++ metadata members and write the const fields
    // through const_cast. Memory layout matches a real AdapterAnw and
    // destructors run via oh_anw_destroy.
    void* mem = std::calloc(1, sizeof(AdapterAnw));
    if (!mem) return nullptr;
    auto* a = reinterpret_cast<AdapterAnw*>(mem);

    // Initialize AOSP face — common.magic/version/reserved/incRef/decRef
    a->aosp.common.magic   = ANDROID_NATIVE_WINDOW_MAGIC;
    a->aosp.common.version = sizeof(struct ANativeWindow);
    // 2026-05-11 G2.14aj — slot 2 is confirmed `query`.
    // Objdump of libhwui.so CanvasContext::setupPipelineSurface at
    // 0x1160b0..0x1160b6 shows `ldr r3,[r6,#16]; blx r3` with r1=
    // NATIVE_WINDOW_MIN_UNDEQUEUED_BUFFERS (=3) and r2=&query_value,
    // matching the inlined setBufferCount path (CanvasContext.cpp:172).
    // Install `aanw_query` directly at common.reserved[2] so hwui's
    // query call succeeds and writes a real value (was: noop returned 0
    // without writing *value → stack-garbage triggered "window->query
    // failed value=-166522112" and stalled the RT pipeline).
    // Slots 0/1/3 get individual LR-tracing probes (phase 1 of G2.14aj).
    // Phase 2 will swap them for real handlers once we know what AOSP
    // function each slot maps to.
    a->aosp.common.reserved[0] = (void*) &oh_anw_slot0_probe;
    a->aosp.common.reserved[1] = (void*) &oh_anw_slot1_probe;
    a->aosp.common.reserved[2] = (void*) &aanw_query;
    a->aosp.common.reserved[3] = (void*) &oh_anw_slot3_probe;
    a->aosp.common.incRef = anw_base_incRef;
    a->aosp.common.decRef = anw_base_decRef;

    // Hardcode some readonly fields to plausible defaults; hwui mostly
    // ignores them or overwrites via perform.
    *((uint32_t*) &a->aosp.flags)            = 0;
    *((int*)      &a->aosp.minSwapInterval)  = 0;
    *((int*)      &a->aosp.maxSwapInterval)  = 1;
    *((float*)    &a->aosp.xdpi)             = 240.f;
    *((float*)    &a->aosp.ydpi)             = 240.f;

    // 10 function pointers
    a->aosp.setSwapInterval         = aanw_setSwapInterval;
    a->aosp.dequeueBuffer_DEPRECATED = aanw_dequeueBuffer_DEPRECATED;
    a->aosp.lockBuffer_DEPRECATED   = aanw_lockBuffer_DEPRECATED;
    a->aosp.queueBuffer_DEPRECATED  = aanw_queueBuffer_DEPRECATED;
    a->aosp.query                    = aanw_query;
    a->aosp.perform                  = aanw_perform;
    a->aosp.cancelBuffer_DEPRECATED = aanw_cancelBuffer_DEPRECATED;
    a->aosp.dequeueBuffer            = aanw_dequeueBuffer;
    a->aosp.queueBuffer              = aanw_queueBuffer;
    a->aosp.cancelBuffer             = aanw_cancelBuffer;

    // Adapter face — placement-new the C++ metadata members on the
    // calloc'd memory (their default ctors run; std::atomic gets value 0,
    // std::mutex gets unlocked, unordered_map gets empty).
    new (&a->refCount) std::atomic<int32_t>(1);
    new (&a->bufCacheLock) std::mutex();
    new (&a->bufCache) std::unordered_map<OHNativeWindowBuffer*, AdapterAnwBuffer*>();
    a->oh    = oh;
    a->magic = kAdapterAnwMagic;

    // 2026-05-12 G2.14aw probe A.2: verify wrap didn't swap backing surface.
    // Expectation: uniqueId here == uniqueId logged in
    // OHWindowManagerClient::getOhNativeWindow[probe-postCreate].
    {
        uint64_t uid = 0;
        int32_t qrc = OH_NativeWindow_GetSurfaceId(oh, &uid);
        ALOGI("oh_anw_wrap[probe]: oh=%p -> aosp_anw=%p (magic=ANW1) uniqueId=0x%llx (rc=%d)",
              (void*) oh, (void*) a, (unsigned long long) uid, qrc);
    }
    return reinterpret_cast<struct ANativeWindow*>(a);
}

extern "C" void oh_anw_destroy(struct ANativeWindow* aosp) {
    auto* a = as_adapter(aosp);
    if (!a) return;
    {
        std::lock_guard<std::mutex> lock(a->bufCacheLock);
        for (auto& kv : a->bufCache) {
            delete kv.second;
        }
        a->bufCache.clear();
    }
    a->magic = 0;
    // Run dtors on placement-new'd metadata members, then free raw memory.
    a->bufCache.~unordered_map();
    a->bufCacheLock.~mutex();
    a->refCount.~atomic();
    std::free(a);
}

extern "C" OHNativeWindow* oh_anw_get_oh(struct ANativeWindow* aosp) {
    auto* a = as_adapter(aosp);
    return a ? a->oh : nullptr;
}

// G2.14ag: refcount probes for AOSP NDK compat shims (see header for
// rationale).
//
// Pre-G2.14ag the compat shims forwarded ANativeWindow_acquire/release to
// OH NativeObjectReference / NativeObjectUnreference, which silently FAILED
// the magic check (offset 0 of AdapterAnw is AOSP '_wnd', not OH WINDOW
// magic) — effectively a no-op. hwui's actual refcount lifecycle is driven
// by the AOSP function pointer table at common.incRef/decRef (already wired
// to anw_base_incRef/decRef on the same atomic). So the shim was kept alive
// by the function-pointer path; the magic illegal was only a noisy warning.
//
// G2.14ag's first attempt added independent refcount mutation in these
// probes, but hwui's call pattern doesn't pair NDK acquire/release
// symmetrically with common.incRef/decRef — two independent refcount paths
// over-decrement and destroy the shim while OH BufferQueue still holds the
// buffer ("buffer is released" errors, no CreateBufferLayer follow-through).
//
// Final design: keep the probes as literal no-ops on the AdapterAnw refcount.
// They only "consume" the call (return 1) so the compat shim doesn't forward
// to OH NativeObjectReference (which would re-trigger the magic illegal
// warning). The shim's refcount continues to be managed by anw_base_incRef
// / anw_base_decRef via the AOSP common function pointer slots — the path
// that worked in G2.14af.
extern "C" int oh_anw_try_acquire(struct ANativeWindow* aosp) {
    auto* a = as_adapter(aosp);
    if (!a) return 0;
    return 1;  // consumed — no refcount mutation (see comment above)
}

extern "C" int oh_anw_try_release(struct ANativeWindow* aosp) {
    auto* a = as_adapter(aosp);
    if (!a) return 0;
    return 1;  // consumed — no refcount mutation (see comment above)
}
