/*
 * xcomponent_bridge.c — CR60 Workstream E follow-up #2 (post-E7).
 *
 * Goal: prove that the in-process dlopen of OHOS production native libs
 * (validated by HelloDlopen.java + ohos_dlopen_smoke.c) actually produces
 * working function pointers, not just resolved symbols. The smoke test
 * confirmed dlsym returns a non-NULL address; this bridge invokes the
 * resolved entry points and reports the return value back to Java.
 *
 * Loaded by Java via System.loadLibrary("xcomponent_bridge"). All OHOS
 * libs are dlopen'd by ABSOLUTE PATH inside the bridge — we do NOT rely
 * on java.library.path / musl ld-musl resolution finding the deep
 * /system/lib/chipset-sdk-sp/ paths. The DAYU200 ld-musl-arm.path covers
 * many directories but NOT chipset-sdk-sp, where libnative_window.so
 * and libnative_buffer.so actually live (the prior HelloDlopen "OK"
 * came through System.loadLibrary which itself searches paths the
 * smoke test had hardcoded — see comments in ohos_dlopen_smoke.c).
 *
 * Tier ladder (mirrors brief in CR60-followup-xcomp-call):
 *
 *   Tier 1 (smoke): call OH_NativeBuffer_Alloc(NULL) — must return NULL
 *                   without SIGSEGV. Proves function pointer is real.
 *   Tier 2 (alloc): call OH_NativeBuffer_Alloc(&cfg) with cfg=720x1280
 *                   BGRA8888 + CPU usage. Must return non-NULL buffer.
 *   Tier 3 (map):   OH_NativeBuffer_Map(buf, &virAddr), fill with RED
 *                   pixels, OH_NativeBuffer_Unmap, OH_NativeBuffer_Unreference.
 *                   Proves producer pipeline doesn't crash.
 *
 * Tier 4 (DRM direct) is NOT in this bridge — it lives in
 * dalvik-port/compat/m6-drm-daemon/ already. If Tiers 1-3 stall, the
 * checkpoint defers to that path.
 *
 * Architectural notes:
 *   - This is an OHOS-only bridge (TARGET=ohos-arm32-dynamic). The host
 *     x86_64 / static-arm32 / aarch64 builds do NOT compile or ship it.
 *   - No per-app branches anywhere. No Unsafe / reflection on the Java
 *     side. JNI/native exempt per the macro-shim contract.
 *   - All pointer-sized values use uintptr_t / size_t. No raw (int) /
 *     (long) casts of pointers (per feedback_bitness_as_parameter.md).
 *
 * Author: agent 13 (CR60 follow-up, 2026-05-14).
 */
#include <dlfcn.h>
#include <jni.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

/* OHOS production lib paths on DAYU200 OHOS 7.0.0.18. Confirmed via
 * `hdc shell find /system -name 'libnative_*'` 2026-05-14. Each path
 * is an *absolute* path — see file header for why. */
#define LIB_NATIVE_BUFFER   "/system/lib/chipset-sdk-sp/libnative_buffer.so"
#define LIB_NATIVE_WINDOW   "/system/lib/chipset-sdk-sp/libnative_window.so"

/* Opaque types from OHOS NDK (interface/sdk_c/graphic/graphic_2d/...).
 * We don't include the headers; we declare just enough to call the
 * three symbols we need. The struct types are fully opaque to us — we
 * only ever hold an OH_NativeBuffer* and round-trip it. */
struct OH_NativeBuffer;

/* OH_NativeBuffer_Config matches the layout in native_buffer.h. We
 * declare it here so we don't need to depend on the OHOS NDK headers
 * being on the dalvik-port include path. Verified against
 * interface/sdk_c/graphic/graphic_2d/native_buffer/native_buffer.h
 * at /home/dspfac/openharmony/ (2026-05-14). */
typedef struct {
    int32_t  width;
    int32_t  height;
    int32_t  format;   /* OH_NativeBuffer_Format enum */
    int32_t  usage;    /* OH_NativeBuffer_Usage bitmask */
    uint64_t stride;   /* output only, but field is present in input cfg */
} OH_NativeBuffer_Config_compat;

/* From OH_NativeBuffer_Format enum (excerpted):
 *   NATIVEBUFFER_PIXEL_FMT_BGRA_8888 = 12  // BGRA 4x8-bit
 *   NATIVEBUFFER_PIXEL_FMT_RGBA_8888 = 11  // RGBA 4x8-bit
 * From OH_NativeBuffer_Usage:
 *   NATIVEBUFFER_USAGE_CPU_READ      = (1ULL << 0)
 *   NATIVEBUFFER_USAGE_CPU_WRITE     = (1ULL << 1)
 *   NATIVEBUFFER_USAGE_MEM_DMA       = (1ULL << 3)
 */
#define OH_FMT_BGRA_8888    12
#define OH_FMT_RGBA_8888    11
#define OH_USAGE_CPU_READ   (1U << 0)
#define OH_USAGE_CPU_WRITE  (1U << 1)
#define OH_USAGE_MEM_DMA    (1U << 3)

/* Resolved entry points. NULL on dlopen/dlsym failure — every API
 * accessor checks and reports back. */
typedef struct OH_NativeBuffer* (*FN_NativeBuffer_Alloc)(
        const OH_NativeBuffer_Config_compat *cfg);
typedef int32_t (*FN_NativeBuffer_Map)(
        struct OH_NativeBuffer *buf, void **virAddr);
typedef int32_t (*FN_NativeBuffer_Unmap)(struct OH_NativeBuffer *buf);
typedef int32_t (*FN_NativeBuffer_Unreference)(struct OH_NativeBuffer *buf);
typedef uint32_t (*FN_NativeBuffer_GetSeqNum)(struct OH_NativeBuffer *buf);

static struct {
    int                          loaded;
    void                        *h_buffer;
    void                        *h_window;
    FN_NativeBuffer_Alloc        alloc;
    FN_NativeBuffer_Map          map;
    FN_NativeBuffer_Unmap        unmap;
    FN_NativeBuffer_Unreference  unref;
    FN_NativeBuffer_GetSeqNum    getseq;
} g_oh = {0};

/* Returned to Java as a string for human-readable diagnostics. */
static char g_last_error[512] = {0};

static void set_err(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    vsnprintf(g_last_error, sizeof(g_last_error), fmt, ap);
    va_end(ap);
}

/* Lazy-init: dlopen the OHOS libs and resolve the entry points. Called
 * the first time any nativeXxx() is invoked. Idempotent. Returns 1
 * on success (or already-loaded), 0 on failure. */
static int oh_load_once(void) {
    if (g_oh.loaded) return 1;

    g_oh.h_buffer = dlopen(LIB_NATIVE_BUFFER, RTLD_NOW);
    if (!g_oh.h_buffer) {
        const char *e = dlerror();
        set_err("dlopen %s: %s", LIB_NATIVE_BUFFER, e ? e : "(null)");
        return 0;
    }
    g_oh.h_window = dlopen(LIB_NATIVE_WINDOW, RTLD_NOW);
    if (!g_oh.h_window) {
        const char *e = dlerror();
        set_err("dlopen %s: %s", LIB_NATIVE_WINDOW, e ? e : "(null)");
        /* Continue without it — buffer-only is enough for Tier 1-3. */
    }

    g_oh.alloc  = (FN_NativeBuffer_Alloc)
                  dlsym(g_oh.h_buffer, "OH_NativeBuffer_Alloc");
    g_oh.map    = (FN_NativeBuffer_Map)
                  dlsym(g_oh.h_buffer, "OH_NativeBuffer_Map");
    g_oh.unmap  = (FN_NativeBuffer_Unmap)
                  dlsym(g_oh.h_buffer, "OH_NativeBuffer_Unmap");
    g_oh.unref  = (FN_NativeBuffer_Unreference)
                  dlsym(g_oh.h_buffer, "OH_NativeBuffer_Unreference");
    g_oh.getseq = (FN_NativeBuffer_GetSeqNum)
                  dlsym(g_oh.h_buffer, "OH_NativeBuffer_GetSeqNum");

    /* Minimum viable set for Tier 1: alloc + unref (so we can clean up).
     * map/unmap/getseq are nice-to-have for Tiers 2-3. */
    if (!g_oh.alloc || !g_oh.unref) {
        set_err("dlsym: alloc=%p unref=%p map=%p unmap=%p getseq=%p",
                (void*)g_oh.alloc, (void*)g_oh.unref,
                (void*)g_oh.map, (void*)g_oh.unmap, (void*)g_oh.getseq);
        return 0;
    }

    g_oh.loaded = 1;
    return 1;
}

/* =========================================================================
 * JNI entry points. Class: com.westlake.ohostests.xcomponent.XComponentBridge
 *
 * Static methods (one per tier + diagnostics):
 *   long   nativeInit()                  — loads libs, returns 1 ok / 0 fail
 *   String nativeLastError()             — last dlopen/dlsym error message
 *   long   nativeAllocNull()             — TIER 1: alloc(NULL), expects NULL
 *   long   nativeAlloc(int w, int h)     — TIER 2: alloc real buffer, returns ptr
 *   int    nativeFillRed(long bufPtr,    — TIER 3: map, fill BGRA red, unmap
 *                        int w, int h,
 *                        int stride)
 *   int    nativeGetSeqNum(long bufPtr)  — diagnostic getter (tier 1 alt)
 *   int    nativeUnref(long bufPtr)      — cleanup
 *
 * All "long" returns are JNI-ABI 64-bit so we can transport native pointers
 * unchanged on either bitness. The Java side treats them as opaque handles.
 * ========================================================================= */

JNIEXPORT jlong JNICALL
Java_com_westlake_ohostests_xcomponent_XComponentBridge_nativeInit(
        JNIEnv *env, jclass cls) {
    return oh_load_once() ? 1 : 0;
}

JNIEXPORT jstring JNICALL
Java_com_westlake_ohostests_xcomponent_XComponentBridge_nativeLastError(
        JNIEnv *env, jclass cls) {
    return (*env)->NewStringUTF(env, g_last_error);
}

JNIEXPORT jlong JNICALL
Java_com_westlake_ohostests_xcomponent_XComponentBridge_nativeAllocNull(
        JNIEnv *env, jclass cls) {
    if (!oh_load_once()) return 0;
    /* OH_NativeBuffer_Alloc(NULL) — should return NULL without crashing.
     * Per OHOS source: the implementation checks `if (config == nullptr)`
     * at the top and returns nullptr. This is our Tier-1 sanity check:
     * resolved function pointer actually executes producer-lib code. */
    struct OH_NativeBuffer *buf = g_oh.alloc(NULL);
    return (jlong)(uintptr_t)buf;  /* expected NULL → returns 0 to Java */
}

JNIEXPORT jlong JNICALL
Java_com_westlake_ohostests_xcomponent_XComponentBridge_nativeAlloc(
        JNIEnv *env, jclass cls, jint w, jint h) {
    if (!oh_load_once()) return 0;
    OH_NativeBuffer_Config_compat cfg;
    memset(&cfg, 0, sizeof(cfg));
    cfg.width  = w;
    cfg.height = h;
    cfg.format = OH_FMT_BGRA_8888;
    cfg.usage  = OH_USAGE_CPU_READ | OH_USAGE_CPU_WRITE | OH_USAGE_MEM_DMA;
    struct OH_NativeBuffer *buf = g_oh.alloc(&cfg);
    if (!buf) {
        set_err("OH_NativeBuffer_Alloc(%dx%d BGRA8888 cpu+dma) returned NULL",
                (int)w, (int)h);
    }
    return (jlong)(uintptr_t)buf;
}

JNIEXPORT jint JNICALL
Java_com_westlake_ohostests_xcomponent_XComponentBridge_nativeGetSeqNum(
        JNIEnv *env, jclass cls, jlong bufPtr) {
    if (!oh_load_once() || !g_oh.getseq) return -1;
    struct OH_NativeBuffer *buf = (struct OH_NativeBuffer*)(uintptr_t)bufPtr;
    if (!buf) return -1;
    return (jint)g_oh.getseq(buf);
}

JNIEXPORT jint JNICALL
Java_com_westlake_ohostests_xcomponent_XComponentBridge_nativeFillRed(
        JNIEnv *env, jclass cls, jlong bufPtr,
        jint w, jint h, jint stridePx) {
    if (!oh_load_once() || !g_oh.map || !g_oh.unmap) return -1;
    struct OH_NativeBuffer *buf = (struct OH_NativeBuffer*)(uintptr_t)bufPtr;
    if (!buf) return -2;

    void *virAddr = NULL;
    int32_t rc = g_oh.map(buf, &virAddr);
    if (rc != 0 || !virAddr) {
        set_err("OH_NativeBuffer_Map rc=%d virAddr=%p", (int)rc, virAddr);
        return -3;
    }

    /* BGRA8888 RED: B=0, G=0, R=0xFF, A=0xFF → little-endian 0xFF0000FF.
     * Write w*h pixels assuming stridePx (in pixels) >= w. If stride
     * is unknown, callers pass w. */
    uint32_t *pixels = (uint32_t*)virAddr;
    size_t stride = (stridePx > 0) ? (size_t)stridePx : (size_t)w;
    /* Pack ARGB constant once: B=0x00 G=0x00 R=0xFF A=0xFF → little-endian
     * (LSB first) the bytes appear as 00 00 FF FF in memory, which a
     * BGRA8888-reading composer interprets as B=0 G=0 R=0xFF A=0xFF. */
    uint32_t red = 0xFFFF0000u;  /* AARRGGBB; written as BGRA: 00 00 FF FF */
    for (size_t y = 0; y < (size_t)h; y++) {
        uint32_t *row = pixels + y * stride;
        for (size_t x = 0; x < (size_t)w; x++) {
            row[x] = red;
        }
    }

    rc = g_oh.unmap(buf);
    if (rc != 0) {
        set_err("OH_NativeBuffer_Unmap rc=%d", (int)rc);
        /* Don't fail — fill succeeded; unmap rc is informational. */
    }
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_westlake_ohostests_xcomponent_XComponentBridge_nativeUnref(
        JNIEnv *env, jclass cls, jlong bufPtr) {
    if (!oh_load_once()) return -1;
    struct OH_NativeBuffer *buf = (struct OH_NativeBuffer*)(uintptr_t)bufPtr;
    if (!buf) return 0;
    return (jint)g_oh.unref(buf);
}
