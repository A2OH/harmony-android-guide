/*
 * oh_hardwarebuffer_shim.c
 *
 * Android NDK AHardwareBuffer_* API shim, implemented as wrappers around
 * OH_NativeBuffer_* API.
 *
 * Build:
 *   $CC --target=arm-linux-ohos -shared -fPIC \
 *        -o liboh_hardwarebuffer_shim.so oh_hardwarebuffer_shim.c \
 *        -lnative_buffer
 */
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

/* Forward declarations of OH NativeBuffer API */
typedef struct OH_NativeBuffer OH_NativeBuffer;

typedef struct {
    int32_t width;
    int32_t height;
    int32_t format;
    int32_t usage;
    int32_t stride;
} OH_NativeBuffer_Config;

extern OH_NativeBuffer *OH_NativeBuffer_Alloc(const OH_NativeBuffer_Config *config);
extern int32_t OH_NativeBuffer_Reference(OH_NativeBuffer *buffer);
extern int32_t OH_NativeBuffer_Unreference(OH_NativeBuffer *buffer);
extern void OH_NativeBuffer_GetConfig(OH_NativeBuffer *buffer, OH_NativeBuffer_Config *config);
extern int32_t OH_NativeBuffer_Map(OH_NativeBuffer *buffer, void **virAddr);
extern int32_t OH_NativeBuffer_Unmap(OH_NativeBuffer *buffer);

/* ============================================================ */
/* Android AHardwareBuffer types                                 */
/* ============================================================ */

/* AHardwareBuffer is identical to OH_NativeBuffer at the binary level
 * (both are opaque pointers). We define AHardwareBuffer as an alias. */
typedef struct AHardwareBuffer AHardwareBuffer;

typedef struct {
    uint32_t width;
    uint32_t height;
    uint32_t layers;
    uint32_t format;
    uint64_t usage;
    uint32_t stride;
    uint32_t rfu0;
    uint64_t rfu1;
} AHardwareBuffer_Desc;

/* Cast helper */
static inline OH_NativeBuffer *to_oh(AHardwareBuffer *b) {
    return (OH_NativeBuffer *)b;
}

/* ============================================================ */
/* Format / usage conversion                                     */
/* ============================================================ */

/* Android format → OH format (matching values for common cases) */
static int32_t android_to_oh_format(uint32_t android_format) {
    /* Pixel formats are mostly identical between Android and OH:
     *   AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM = 1 → GRAPHIC_PIXEL_FMT_RGBA_8888 = 12
     *   AHARDWAREBUFFER_FORMAT_R8G8B8X8_UNORM = 2 → GRAPHIC_PIXEL_FMT_RGBX_8888 = 13
     *   AHARDWAREBUFFER_FORMAT_R8G8B8_UNORM = 3   → GRAPHIC_PIXEL_FMT_RGB_888 = 14
     *   AHARDWAREBUFFER_FORMAT_R5G6B5_UNORM = 4   → GRAPHIC_PIXEL_FMT_RGB_565 = 15
     */
    switch (android_format) {
        case 1: return 12;
        case 2: return 13;
        case 3: return 14;
        case 4: return 15;
        case 22: return 35;  /* RGBA_FP16 */
        case 43: return 36;  /* RGBA_1010102 */
        default: return 12;
    }
}

static uint64_t android_to_oh_usage(uint64_t android_usage) {
    uint64_t oh_usage = 0;
    if (android_usage & 0x100ULL) oh_usage |= (1ULL << 9);  /* GPU_SAMPLED → HW_TEXTURE */
    if (android_usage & 0x200ULL) oh_usage |= (1ULL << 8);  /* GPU_COLOR_OUTPUT → HW_RENDER */
    if (android_usage & 0xFULL)   oh_usage |= (1ULL << 0);  /* CPU_READ */
    if (android_usage & 0xF0ULL)  oh_usage |= (1ULL << 1);  /* CPU_WRITE */
    /* Always enable DMA for shared buffers */
    oh_usage |= (1ULL << 3);  /* MEM_DMA */
    return oh_usage;
}

/* ============================================================ */
/* AHardwareBuffer API exports                                   */
/* ============================================================ */

int AHardwareBuffer_allocate(const AHardwareBuffer_Desc *desc,
                             AHardwareBuffer **outBuffer) {
    if (!desc || !outBuffer) return -1;

    OH_NativeBuffer_Config cfg;
    cfg.width = (int32_t)desc->width;
    cfg.height = (int32_t)desc->height;
    cfg.format = android_to_oh_format(desc->format);
    cfg.usage = (int32_t)android_to_oh_usage(desc->usage);
    cfg.stride = (int32_t)desc->stride;

    OH_NativeBuffer *ohBuf = OH_NativeBuffer_Alloc(&cfg);
    if (!ohBuf) {
        *outBuffer = NULL;
        return -1;
    }
    *outBuffer = (AHardwareBuffer *)ohBuf;
    return 0;
}

void AHardwareBuffer_acquire(AHardwareBuffer *buffer) {
    if (buffer) {
        OH_NativeBuffer_Reference(to_oh(buffer));
    }
}

void AHardwareBuffer_release(AHardwareBuffer *buffer) {
    if (buffer) {
        OH_NativeBuffer_Unreference(to_oh(buffer));
    }
}

void AHardwareBuffer_describe(const AHardwareBuffer *buffer,
                              AHardwareBuffer_Desc *outDesc) {
    if (!buffer || !outDesc) return;
    OH_NativeBuffer_Config cfg;
    OH_NativeBuffer_GetConfig((OH_NativeBuffer *)buffer, &cfg);
    memset(outDesc, 0, sizeof(*outDesc));
    outDesc->width = (uint32_t)cfg.width;
    outDesc->height = (uint32_t)cfg.height;
    outDesc->layers = 1;
    outDesc->format = (uint32_t)cfg.format;
    outDesc->usage = (uint64_t)cfg.usage;
    outDesc->stride = (uint32_t)cfg.stride;
}

int AHardwareBuffer_lock(AHardwareBuffer *buffer, uint64_t usage,
                         int32_t fence, const void *rect,
                         void **outVirtualAddress) {
    (void)usage; (void)fence; (void)rect;
    if (!buffer || !outVirtualAddress) return -1;
    return OH_NativeBuffer_Map(to_oh(buffer), outVirtualAddress);
}

int AHardwareBuffer_unlock(AHardwareBuffer *buffer, int32_t *fence) {
    if (fence) *fence = -1;
    if (!buffer) return -1;
    return OH_NativeBuffer_Unmap(to_oh(buffer));
}

/* AHardwareBuffer_to_ANativeWindowBuffer: VNDK function used by
 * ReliableSurface.cpp to convert AHardwareBuffer* → ANativeWindowBuffer*.
 * On Android both are views of the same GraphicBuffer; on OH both map
 * to the same OH_NativeBuffer handle. Cast is correct. */
struct ANativeWindowBuffer;
struct ANativeWindowBuffer *AHardwareBuffer_to_ANativeWindowBuffer(
        AHardwareBuffer *buffer) {
    return (struct ANativeWindowBuffer *)buffer;
}

int AHardwareBuffer_sendHandleToUnixSocket(const AHardwareBuffer *buffer, int socketFd) {
    /* Cross-process buffer sharing — not supported in Phase 1 */
    (void)buffer; (void)socketFd;
    return -1;
}

int AHardwareBuffer_recvHandleFromUnixSocket(int socketFd, AHardwareBuffer **outBuffer) {
    (void)socketFd;
    if (outBuffer) *outBuffer = NULL;
    return -1;
}

/* AHardwareBuffer_getDataSpace: returns the data space (color space metadata)
 * of the buffer. On OH, NativeBuffer doesn't expose this query directly.
 * Return ADATASPACE_SRGB (142671872 = STANDARD_BT709 | TRANSFER_SRGB |
 * RANGE_FULL) as the default — this is correct for the vast majority of
 * rendering buffers and matches what most Android drivers return for
 * standard RGBA8888 buffers. */
int32_t AHardwareBuffer_getDataSpace(AHardwareBuffer *buffer) {
    (void)buffer;
    return 142671872;  /* ADATASPACE_SRGB */
}
