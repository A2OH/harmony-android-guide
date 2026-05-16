/*
 * pixel_format_mapper.h
 *
 * Maps Android pixel formats and buffer usage flags to OH equivalents.
 * Used by OHGraphicBufferProducer for buffer allocation.
 */
#ifndef PIXEL_FORMAT_MAPPER_H
#define PIXEL_FORMAT_MAPPER_H

#include <cstdint>

namespace oh_adapter {

/**
 * Android pixel format constants (from android/pixel_format.h and graphics.h).
 */
enum AndroidPixelFormat {
    ANDROID_PIXEL_FORMAT_UNKNOWN        = 0,
    ANDROID_PIXEL_FORMAT_RGBA_8888      = 1,
    ANDROID_PIXEL_FORMAT_RGBX_8888      = 2,
    ANDROID_PIXEL_FORMAT_RGB_888        = 3,
    ANDROID_PIXEL_FORMAT_RGB_565        = 4,
    ANDROID_PIXEL_FORMAT_BGRA_8888      = 5,
    ANDROID_PIXEL_FORMAT_RGBA_1010102   = 43,
    ANDROID_PIXEL_FORMAT_RGBA_FP16      = 22,
};

/**
 * OH pixel format constants (from graphic_surface/interfaces/inner_api/common/).
 */
// 2026-05-01 G2.14n CRITICAL FIX: values verified against OH source
// foundation/graphic/graphic_surface/interfaces/inner_api/surface/surface_type.h
// (GraphicPixelFormat enum). Previous values were wildly wrong:
//   BGRA_8888 was 16  → OH 16 is GRAPHIC_PIXEL_FMT_BGRA_4444 (4-bit packed!)
//   RGBX_8888 was 13  → OH 13 is GRAPHIC_PIXEL_FMT_RGB_888
//   RGB_888   was 14  → OH 14 is GRAPHIC_PIXEL_FMT_BGR_565
//   RGB_565   was 15  → OH 15 is GRAPHIC_PIXEL_FMT_BGRX_4444
//   RGBA_FP16 was 35  → OH 35 is GRAPHIC_PIXEL_FMT_YCBCR_P010 (YUV!)
//   RGBA_1010102 was 36 → OH 36 is GRAPHIC_PIXEL_FMT_YCRCB_P010 (YUV!)
// Result: OH RS thought our RGBA buffers were YUV/Bayer/4-bit and routed
// them into libohosffmpeg's pixel-format converter table, where a vtable
// dispatch landed in BSS → SEGV_ACCERR at PC=0xedd08f9c with lr in
// bayer_bggr16be_to_yv12_interpolate.  Only RGBA_8888 = 12 was correct.
enum OHPixelFormat {
    OH_PIXEL_FMT_RGBA_8888     = 12,
    OH_PIXEL_FMT_RGBX_8888     = 11,
    OH_PIXEL_FMT_RGB_888       = 13,
    OH_PIXEL_FMT_RGB_565       = 3,
    OH_PIXEL_FMT_BGRA_8888     = 20,
    OH_PIXEL_FMT_RGBA_1010102  = 34,
    OH_PIXEL_FMT_RGBA_FP16     = 39,  // GRAPHIC_PIXEL_FMT_RGBA16_FLOAT
};

/**
 * Android buffer usage flags (from gralloc/hardware/gralloc.h).
 */
enum AndroidBufferUsage : uint64_t {
    ANDROID_USAGE_SW_READ_RARELY    = 0x00000002ULL,
    ANDROID_USAGE_SW_READ_OFTEN     = 0x00000003ULL,
    ANDROID_USAGE_SW_WRITE_RARELY   = 0x00000020ULL,
    ANDROID_USAGE_SW_WRITE_OFTEN    = 0x00000030ULL,
    ANDROID_USAGE_HW_TEXTURE        = 0x00000100ULL,
    ANDROID_USAGE_HW_RENDER         = 0x00000200ULL,
    ANDROID_USAGE_HW_COMPOSER       = 0x00000800ULL,
    ANDROID_USAGE_GPU_DATA_BUFFER   = 0x01000000ULL,
};

/**
 * OH buffer usage flags (from graphic_surface surface_type.h).
 *
 * 2026-05-02 G2.14r CRITICAL FIX (verified against authoritative source
 * /home/HanBingChen/oh/out/rk3568/innerkits/ohos-arm/graphic_surface/surface/include/surface_type.h
 * lines 324-353):
 *   HW_COMPOSER was (1<<12) — wrong bit, that's BUFFER_USAGE_CAMERA_READ on OH.
 *   When HelloWorld submits a HW_COMPOSER buffer to OH RS, OH RS sees CAMERA_READ
 *   instead, OH HWC rejects the composition with "Apply:250 gfx composition can
 *   not surpport the type 0", buffer is released, App hits CHECK in fence/buffer
 *   wait path → SIGABRT.  Fix: HW_COMPOSER = (1<<10).  Same value as already
 *   defined correctly in oh_egl_surface_adapter.cpp line 52 (the two adapter
 *   places had drifted apart — this fix unifies them).
 */
enum OHBufferUsage : uint64_t {
    OH_USAGE_CPU_READ           = (1ULL << 0),
    OH_USAGE_CPU_WRITE          = (1ULL << 1),
    OH_USAGE_MEM_DMA            = (1ULL << 3),
    OH_USAGE_MEM_SHARE          = (1ULL << 4),   // 2026-05-06: required by §5.2 force-list
    OH_USAGE_HW_RENDER          = (1ULL << 8),
    OH_USAGE_HW_TEXTURE         = (1ULL << 9),
    OH_USAGE_HW_COMPOSER        = (1ULL << 10),  // G2.14r FIX: was (1<<12) = CAMERA_READ
};

/**
 * Convert Android pixel format to OH pixel format.
 */
inline int32_t androidToOHPixelFormat(int32_t androidFormat) {
    switch (androidFormat) {
        case ANDROID_PIXEL_FORMAT_RGBA_8888:     return OH_PIXEL_FMT_RGBA_8888;
        case ANDROID_PIXEL_FORMAT_RGBX_8888:     return OH_PIXEL_FMT_RGBX_8888;
        case ANDROID_PIXEL_FORMAT_RGB_888:       return OH_PIXEL_FMT_RGB_888;
        case ANDROID_PIXEL_FORMAT_RGB_565:       return OH_PIXEL_FMT_RGB_565;
        case ANDROID_PIXEL_FORMAT_BGRA_8888:     return OH_PIXEL_FMT_BGRA_8888;
        case ANDROID_PIXEL_FORMAT_RGBA_1010102:  return OH_PIXEL_FMT_RGBA_1010102;
        case ANDROID_PIXEL_FORMAT_RGBA_FP16:     return OH_PIXEL_FMT_RGBA_FP16;
        default:                                 return OH_PIXEL_FMT_RGBA_8888;
    }
}

/**
 * Convert OH pixel format to Android pixel format.
 */
inline int32_t ohToAndroidPixelFormat(int32_t ohFormat) {
    switch (ohFormat) {
        case OH_PIXEL_FMT_RGBA_8888:     return ANDROID_PIXEL_FORMAT_RGBA_8888;
        case OH_PIXEL_FMT_RGBX_8888:     return ANDROID_PIXEL_FORMAT_RGBX_8888;
        case OH_PIXEL_FMT_RGB_888:       return ANDROID_PIXEL_FORMAT_RGB_888;
        case OH_PIXEL_FMT_RGB_565:       return ANDROID_PIXEL_FORMAT_RGB_565;
        case OH_PIXEL_FMT_BGRA_8888:     return ANDROID_PIXEL_FORMAT_BGRA_8888;
        case OH_PIXEL_FMT_RGBA_1010102:  return ANDROID_PIXEL_FORMAT_RGBA_1010102;
        case OH_PIXEL_FMT_RGBA_FP16:     return ANDROID_PIXEL_FORMAT_RGBA_FP16;
        default:                         return ANDROID_PIXEL_FORMAT_RGBA_8888;
    }
}

/**
 * Convert Android buffer usage to OH buffer usage.
 */
inline uint64_t androidToOHUsage(uint64_t androidUsage) {
    uint64_t ohUsage = 0;

    if (androidUsage & ANDROID_USAGE_SW_READ_OFTEN)
        ohUsage |= OH_USAGE_CPU_READ;
    if (androidUsage & ANDROID_USAGE_SW_READ_RARELY)
        ohUsage |= OH_USAGE_CPU_READ;
    if (androidUsage & ANDROID_USAGE_SW_WRITE_OFTEN)
        ohUsage |= OH_USAGE_CPU_WRITE;
    if (androidUsage & ANDROID_USAGE_SW_WRITE_RARELY)
        ohUsage |= OH_USAGE_CPU_WRITE;
    if (androidUsage & ANDROID_USAGE_HW_TEXTURE)
        ohUsage |= OH_USAGE_HW_TEXTURE;
    if (androidUsage & ANDROID_USAGE_HW_RENDER)
        ohUsage |= OH_USAGE_HW_RENDER;
    if (androidUsage & ANDROID_USAGE_HW_COMPOSER)
        ohUsage |= OH_USAGE_HW_COMPOSER;

    // 2026-05-06 — Per graphics_rendering_design.html §5.2 / §9.1 三件套 #4:
    //   For any buffer that goes through hwui RenderThread (i.e., the App's
    //   self-drawing layer), force-on the FULL accept set so OH HDI considers
    //   the layer DEVICE-eligible.  HW_COMPOSER alone is necessary but not
    //   sufficient (实测 G2.14r abort 验证) — RS prepare also checks DMA
    //   and inter-process SHARE attributes when judging overlay candidacy.
    if (ohUsage & (OH_USAGE_HW_RENDER | OH_USAGE_HW_TEXTURE | OH_USAGE_HW_COMPOSER)) {
        ohUsage |= OH_USAGE_MEM_DMA;
        ohUsage |= OH_USAGE_MEM_SHARE;     // 2026-05-06: §5.2 force-list 4-bit set
        ohUsage |= OH_USAGE_HW_COMPOSER;   // 2026-05-06: HWC accept (HDI 直通)
        ohUsage |= OH_USAGE_HW_RENDER;     // 2026-05-06: GPU writable
    }

    // Default: GPU renderable + DMA + SHARE + HWC accept (full §5.2 force list)
    if (ohUsage == 0) {
        ohUsage = OH_USAGE_HW_RENDER
                | OH_USAGE_HW_TEXTURE
                | OH_USAGE_HW_COMPOSER
                | OH_USAGE_MEM_DMA
                | OH_USAGE_MEM_SHARE;
    }

    return ohUsage;
}

}  // namespace oh_adapter

#endif  // PIXEL_FORMAT_MAPPER_H
