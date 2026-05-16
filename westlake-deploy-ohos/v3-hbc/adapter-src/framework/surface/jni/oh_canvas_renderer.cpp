/*
 * oh_canvas_renderer.cpp
 *
 * ============================================================================
 * 2026-05-06 — ENTIRE FILE BODY DEACTIVATED via #if 0 ... #endif
 * ============================================================================
 *
 * Reason: software rendering path (Surface.lockCanvas / unlockAndPost using
 * OH_Drawing_Canvas) has been REMOVED from the v3.0 design
 * (graphics_rendering_design.html). The new design is hardware-only — pixels
 * flow through hwui RenderThread → eglCreateWindowSurface → GPU → OH
 * ProducerSurface → RS BufferQueue. lockCanvas / OH_Drawing_Canvas is no
 * longer part of the production path.
 *
 * The only consumer of OHCanvasRenderer was framework/surface/jni/
 * android_view_surface_stubs.cpp (Part 2 lockCanvas), which itself was
 * deactivated on the same date. Therefore this file is orphan.
 *
 * Body preserved (commented via #if 0) for historical reference per user
 * request 2026-05-06 "和本设计方案不一致的代码，请注释掉但不删除，已备后续了解".
 *
 * If this file is ever to be re-enabled, first verify the design has
 * reverted to allowing a software-rendering fallback (it does not as of v3.0).
 *
 * ============================================================================
 * Original header preserved below for reference:
 * ----------------------------------------------------------------------------
 *
 * Software rendering: Android Canvas operations → OH Drawing NDK API.
 *
 * Buffer lifecycle:
 *   lockCanvas:     OHGraphicBufferProducer::dequeueBuffer() → mmap → create OH_Drawing_Bitmap/Canvas
 *   draw*:          OH_Drawing_Canvas* operations (forwarded to Skia M133 internally)
 *   unlockAndPost:  OH_Drawing_CanvasDetachBrush/Pen → queueBuffer()
 */

#if 0  // 2026-05-06 file body deactivated — see header comment above.

#include "oh_canvas_renderer.h"

// Include surface.h before oh_graphic_buffer_producer.h to resolve sptr<Surface>
#include <surface.h>
#include "oh_graphic_buffer_producer.h"
#include "pixel_format_mapper.h"

#include <drawing_bitmap.h>
#include <drawing_brush.h>
#include <drawing_canvas.h>
#include <drawing_color.h>
#include <drawing_font.h>
#include <drawing_matrix.h>
#include <drawing_path.h>
#include <drawing_pen.h>
#include <drawing_rect.h>
#include <drawing_round_rect.h>
#include <drawing_text_blob.h>
#include <drawing_types.h>
#include <drawing_typeface.h>
#include <drawing_point.h>

#include <hilog/log.h>
#include <cstring>
#include <cmath>

#undef LOG_TAG
#define LOG_TAG "OH_CanvasRenderer"
#undef LOG_DOMAIN
#define LOG_DOMAIN 0xD002900

#define LOGI(...) HILOG_INFO(LOG_CORE, __VA_ARGS__)
#define LOGW(...) HILOG_WARN(LOG_CORE, __VA_ARGS__)
#define LOGE(...) HILOG_ERROR(LOG_CORE, __VA_ARGS__)

namespace oh_adapter {

// ============================================================
// Construction / Destruction
// ============================================================

OHCanvasRenderer* OHCanvasRenderer::create(OHGraphicBufferProducer* producer,
                                           int32_t width, int32_t height) {
    if (!producer || width <= 0 || height <= 0) {
        LOGE("create: invalid params (producer=%{public}p, w=%{public}d, h=%{public}d)",
             producer, width, height);
        return nullptr;
    }
    return new OHCanvasRenderer(producer, width, height);
}

OHCanvasRenderer::OHCanvasRenderer(OHGraphicBufferProducer* producer,
                                   int32_t width, int32_t height)
    : producer_(producer), width_(width), height_(height) {
    // Create reusable Brush and Pen
    brush_ = OH_Drawing_BrushCreate();
    pen_ = OH_Drawing_PenCreate();

    // Create default typeface and font for text rendering
    defaultTypeface_ = OH_Drawing_TypefaceCreateDefault();
    defaultFont_ = OH_Drawing_FontCreate();
    if (defaultFont_ && defaultTypeface_) {
        OH_Drawing_FontSetTypeface(defaultFont_, defaultTypeface_);
    }

    LOGI("OHCanvasRenderer created: %{public}dx%{public}d", width, height);
}

OHCanvasRenderer::~OHCanvasRenderer() {
    if (locked_) {
        LOGW("~OHCanvasRenderer: destroying while locked, force unlock");
        unlockAndPost();
    }

    if (defaultFont_) OH_Drawing_FontDestroy(defaultFont_);
    if (defaultTypeface_) OH_Drawing_TypefaceDestroy(defaultTypeface_);
    if (pen_) OH_Drawing_PenDestroy(pen_);
    if (brush_) OH_Drawing_BrushDestroy(brush_);

    LOGI("OHCanvasRenderer destroyed");
}

// ============================================================
// Lock / Unlock Canvas
// ============================================================

bool OHCanvasRenderer::lockCanvas(int32_t dirtyLeft, int32_t dirtyTop,
                                  int32_t dirtyRight, int32_t dirtyBottom) {
    std::lock_guard<std::mutex> lock(mutex_);

    if (locked_) {
        LOGE("lockCanvas: already locked");
        return false;
    }

    // 1. Dequeue buffer from OH Surface
    int slot = -1;
    int fenceFd = -1;
    uint64_t usage = OH_USAGE_CPU_READ | OH_USAGE_CPU_WRITE | OH_USAGE_MEM_DMA;

    int ret = producer_->dequeueBuffer(&slot, &fenceFd,
                                       width_, height_,
                                       ANDROID_PIXEL_FORMAT_RGBA_8888, usage);
    if (ret != 0) {
        LOGE("lockCanvas: dequeueBuffer failed: %{public}d", ret);
        return false;
    }

    currentSlot_ = slot;
    currentFenceFd_ = fenceFd;

    // 2. Get buffer info
    int32_t bufW = 0, bufH = 0, bufStride = 0, bufFmt = 0;
    if (!producer_->getBufferInfo(slot, &bufW, &bufH, &bufStride, &bufFmt)) {
        LOGE("lockCanvas: getBufferInfo failed for slot %{public}d", slot);
        producer_->cancelBuffer(slot, fenceFd);
        currentSlot_ = -1;
        return false;
    }
    bufferStride_ = bufStride;

    // 3. Get buffer virtual address (mmap'd by OH Surface internally)
    int dmabufFd = producer_->getBufferFd(slot);
    if (dmabufFd < 0) {
        LOGE("lockCanvas: getBufferFd failed for slot %{public}d", slot);
        producer_->cancelBuffer(slot, fenceFd);
        currentSlot_ = -1;
        return false;
    }

    // The buffer address is obtained from the OH SurfaceBuffer's GetVirAddr()
    // which is set after RequestBuffer(). We access it through the producer.
    // For now, we use OH_Drawing_BitmapCreateFromPixels approach.

    // 4. Create OH Drawing Bitmap bound to buffer pixels
    targetBitmap_ = OH_Drawing_BitmapCreate();
    OH_Drawing_BitmapFormat fmt;
    fmt.colorFormat = COLOR_FORMAT_RGBA_8888;
    fmt.alphaFormat = ALPHA_FORMAT_PREMUL;
    OH_Drawing_BitmapBuild(targetBitmap_, bufW, bufH, &fmt);

    // 5. Create Canvas from Bitmap
    canvas_ = OH_Drawing_CanvasCreate();
    OH_Drawing_CanvasBind(canvas_, targetBitmap_);

    // 6. Apply dirty clip if not full-screen
    if (dirtyLeft > 0 || dirtyTop > 0 ||
        dirtyRight < width_ || dirtyBottom < height_) {
        OH_Drawing_CanvasClipRect(canvas_,
            OH_Drawing_RectCreate(dirtyLeft, dirtyTop, dirtyRight, dirtyBottom),
            INTERSECT, true);
    }

    locked_ = true;
    LOGI("lockCanvas: slot=%{public}d, buf=%{public}dx%{public}d, stride=%{public}d",
         slot, bufW, bufH, bufStride);
    return true;
}

bool OHCanvasRenderer::unlockAndPost() {
    std::lock_guard<std::mutex> lock(mutex_);

    if (!locked_) {
        LOGE("unlockAndPost: not locked");
        return false;
    }

    // 1. Get rendered pixels from OH Drawing Bitmap and copy to buffer
    // (OH_Drawing_BitmapGetPixels returns the internal pixel storage)
    if (targetBitmap_ && canvas_) {
        void* pixels = OH_Drawing_BitmapGetPixels(targetBitmap_);
        if (pixels) {
            // Pixels are ready in the bitmap's internal buffer
            // The queueBuffer will submit whatever is in the OH SurfaceBuffer
            LOGI("unlockAndPost: pixels rendered at %{public}p", pixels);
        }
    }

    // 2. Destroy canvas and bitmap
    if (canvas_) {
        OH_Drawing_CanvasDestroy(canvas_);
        canvas_ = nullptr;
    }
    if (targetBitmap_) {
        OH_Drawing_BitmapDestroy(targetBitmap_);
        targetBitmap_ = nullptr;
    }

    // 3. Queue buffer back to OH Surface
    int ret = producer_->queueBuffer(currentSlot_, -1 /* no fence */,
                                     0 /* timestamp */,
                                     0, 0, width_, height_ /* crop = full */);
    if (ret != 0) {
        LOGE("unlockAndPost: queueBuffer failed: %{public}d", ret);
    }

    currentSlot_ = -1;
    currentFenceFd_ = -1;
    bufferAddr_ = nullptr;
    locked_ = false;

    LOGI("unlockAndPost: completed, ret=%{public}d", ret);
    return (ret == 0);
}

// ============================================================
// Canvas State
// ============================================================

int OHCanvasRenderer::save() {
    if (!canvas_) return 0;
    OH_Drawing_CanvasSave(canvas_);
    return getSaveCount();
}

void OHCanvasRenderer::restore() {
    if (!canvas_) return;
    OH_Drawing_CanvasRestore(canvas_);
}

void OHCanvasRenderer::restoreToCount(int saveCount) {
    if (!canvas_) return;
    OH_Drawing_CanvasRestoreToCount(canvas_, saveCount);
}

int OHCanvasRenderer::getSaveCount() const {
    if (!canvas_) return 0;
    return OH_Drawing_CanvasGetSaveCount(canvas_);
}

// ============================================================
// Transforms
// ============================================================

void OHCanvasRenderer::translate(float dx, float dy) {
    if (!canvas_) return;
    OH_Drawing_CanvasTranslate(canvas_, dx, dy);
}

void OHCanvasRenderer::scale(float sx, float sy) {
    if (!canvas_) return;
    OH_Drawing_CanvasScale(canvas_, sx, sy);
}

void OHCanvasRenderer::rotate(float degrees) {
    if (!canvas_) return;
    OH_Drawing_CanvasRotate(canvas_, degrees, 0, 0);
}

void OHCanvasRenderer::rotate(float degrees, float px, float py) {
    if (!canvas_) return;
    OH_Drawing_CanvasRotate(canvas_, degrees, px, py);
}

void OHCanvasRenderer::concat(const float matrix[9]) {
    if (!canvas_ || !matrix) return;
    OH_Drawing_Matrix* m = OH_Drawing_MatrixCreate();
    OH_Drawing_MatrixSetMatrix(m,
        matrix[0], matrix[1], matrix[2],
        matrix[3], matrix[4], matrix[5],
        matrix[6], matrix[7], matrix[8]);
    OH_Drawing_CanvasConcatMatrix(canvas_, m);
    OH_Drawing_MatrixDestroy(m);
}

// ============================================================
// Clip
// ============================================================

void OHCanvasRenderer::clipRect(float left, float top, float right, float bottom) {
    if (!canvas_) return;
    OH_Drawing_Rect* rect = OH_Drawing_RectCreate(left, top, right, bottom);
    OH_Drawing_CanvasClipRect(canvas_, rect, INTERSECT, true);
    OH_Drawing_RectDestroy(rect);
}

void OHCanvasRenderer::clipPath(OH_Drawing_Path* path) {
    if (!canvas_ || !path) return;
    OH_Drawing_CanvasClipPath(canvas_, path, INTERSECT, true);
}

// ============================================================
// Helper: Brush / Pen color
// ============================================================

void OHCanvasRenderer::setBrushColor(uint32_t argb) {
    if (!brush_) return;
    uint8_t a = (argb >> 24) & 0xFF;
    uint8_t r = (argb >> 16) & 0xFF;
    uint8_t g = (argb >> 8) & 0xFF;
    uint8_t b = argb & 0xFF;
    OH_Drawing_BrushSetColor(brush_, OH_Drawing_ColorSetArgb(a, r, g, b));
}

void OHCanvasRenderer::setPenColor(uint32_t argb, float strokeWidth) {
    if (!pen_) return;
    uint8_t a = (argb >> 24) & 0xFF;
    uint8_t r = (argb >> 16) & 0xFF;
    uint8_t g = (argb >> 8) & 0xFF;
    uint8_t b = argb & 0xFF;
    OH_Drawing_PenSetColor(pen_, OH_Drawing_ColorSetArgb(a, r, g, b));
    OH_Drawing_PenSetWidth(pen_, strokeWidth);
}

// ============================================================
// Draw Operations
// ============================================================

void OHCanvasRenderer::drawColor(uint32_t color) {
    if (!canvas_) return;
    uint8_t a = (color >> 24) & 0xFF;
    uint8_t r = (color >> 16) & 0xFF;
    uint8_t g = (color >> 8) & 0xFF;
    uint8_t b = color & 0xFF;
    OH_Drawing_CanvasClear(canvas_, OH_Drawing_ColorSetArgb(a, r, g, b));
}

void OHCanvasRenderer::drawRect(float left, float top, float right, float bottom,
                                uint32_t color, bool fill, float strokeWidth) {
    if (!canvas_) return;

    OH_Drawing_Rect* rect = OH_Drawing_RectCreate(left, top, right, bottom);

    if (fill) {
        setBrushColor(color);
        OH_Drawing_CanvasAttachBrush(canvas_, brush_);
        OH_Drawing_CanvasDrawRect(canvas_, rect);
        OH_Drawing_CanvasDetachBrush(canvas_);
    } else {
        setPenColor(color, strokeWidth);
        OH_Drawing_CanvasAttachPen(canvas_, pen_);
        OH_Drawing_CanvasDrawRect(canvas_, rect);
        OH_Drawing_CanvasDetachPen(canvas_);
    }

    OH_Drawing_RectDestroy(rect);
}

void OHCanvasRenderer::drawRoundRect(float left, float top, float right, float bottom,
                                     float rx, float ry,
                                     uint32_t color, bool fill) {
    if (!canvas_) return;

    OH_Drawing_Rect* rect = OH_Drawing_RectCreate(left, top, right, bottom);
    OH_Drawing_RoundRect* rrect = OH_Drawing_RoundRectCreate(rect, rx, ry);

    if (fill) {
        setBrushColor(color);
        OH_Drawing_CanvasAttachBrush(canvas_, brush_);
        OH_Drawing_CanvasDrawRoundRect(canvas_, rrect);
        OH_Drawing_CanvasDetachBrush(canvas_);
    } else {
        setPenColor(color, 1.0f);
        OH_Drawing_CanvasAttachPen(canvas_, pen_);
        OH_Drawing_CanvasDrawRoundRect(canvas_, rrect);
        OH_Drawing_CanvasDetachPen(canvas_);
    }

    OH_Drawing_RoundRectDestroy(rrect);
    OH_Drawing_RectDestroy(rect);
}

void OHCanvasRenderer::drawCircle(float cx, float cy, float radius,
                                  uint32_t color, bool fill) {
    if (!canvas_) return;

    OH_Drawing_Point* center = OH_Drawing_PointCreate(cx, cy);
    if (fill) {
        setBrushColor(color);
        OH_Drawing_CanvasAttachBrush(canvas_, brush_);
        OH_Drawing_CanvasDrawCircle(canvas_, center, radius);
        OH_Drawing_CanvasDetachBrush(canvas_);
    } else {
        setPenColor(color, 1.0f);
        OH_Drawing_CanvasAttachPen(canvas_, pen_);
        OH_Drawing_CanvasDrawCircle(canvas_, center, radius);
        OH_Drawing_CanvasDetachPen(canvas_);
    }
    OH_Drawing_PointDestroy(center);
}

void OHCanvasRenderer::drawLine(float startX, float startY,
                                float stopX, float stopY,
                                uint32_t color, float strokeWidth) {
    if (!canvas_) return;

    setPenColor(color, strokeWidth);
    OH_Drawing_CanvasAttachPen(canvas_, pen_);
    OH_Drawing_CanvasDrawLine(canvas_, startX, startY, stopX, stopY);
    OH_Drawing_CanvasDetachPen(canvas_);
}

void OHCanvasRenderer::drawText(const char* text, float x, float y,
                                float textSize, uint32_t color) {
    drawTextWithTypeface(text, x, y, textSize, color, defaultTypeface_);
}

void OHCanvasRenderer::drawTextWithTypeface(const char* text, float x, float y,
                                            float textSize, uint32_t color,
                                            OH_Drawing_Typeface* typeface) {
    if (!canvas_ || !text || !defaultFont_) return;

    // Set font size
    OH_Drawing_FontSetTextSize(defaultFont_, textSize);
    if (typeface) {
        OH_Drawing_FontSetTypeface(defaultFont_, typeface);
    }

    // Build text blob
    OH_Drawing_TextBlobBuilder* builder = OH_Drawing_TextBlobBuilderCreate();
    size_t textLen = strlen(text);

    // Allocate run for the text
    const OH_Drawing_RunBuffer* runBuffer =
        OH_Drawing_TextBlobBuilderAllocRunPos(builder, defaultFont_,
                                              (int32_t)textLen, nullptr);
    if (runBuffer && runBuffer->glyphs) {
        // Simple: use font to get glyph IDs
        // For ASCII text this is straightforward
        uint16_t* glyphs = runBuffer->glyphs;
        OH_Drawing_Point* points = (OH_Drawing_Point*)runBuffer->pos;

        // Get glyph IDs from font
        OH_Drawing_FontTextToGlyphs(defaultFont_, text, (uint32_t)textLen,
                                    TEXT_ENCODING_UTF8, glyphs, (int)textLen);

        // Position glyphs horizontally
        float advance = 0;
        for (size_t i = 0; i < textLen; i++) {
            float glyphWidth = 0;
            OH_Drawing_FontGetWidths(defaultFont_, &glyphs[i], 1, &glyphWidth);
            // Set position: (x + advance, y)
            float* posData = (float*)points + i * 2;
            posData[0] = advance;
            posData[1] = 0;
            advance += glyphWidth;
        }
    }

    OH_Drawing_TextBlob* blob = OH_Drawing_TextBlobBuilderMake(builder);
    if (blob) {
        setBrushColor(color);
        OH_Drawing_CanvasAttachBrush(canvas_, brush_);
        OH_Drawing_CanvasDrawTextBlob(canvas_, blob, x, y);
        OH_Drawing_CanvasDetachBrush(canvas_);
        OH_Drawing_TextBlobDestroy(blob);
    }

    OH_Drawing_TextBlobBuilderDestroy(builder);
}

void OHCanvasRenderer::drawBitmap(OH_Drawing_Bitmap* bitmap, float left, float top) {
    if (!canvas_ || !bitmap) return;

    OH_Drawing_CanvasAttachBrush(canvas_, brush_);
    OH_Drawing_CanvasDrawBitmap(canvas_, bitmap, left, top);
    OH_Drawing_CanvasDetachBrush(canvas_);
}

void OHCanvasRenderer::drawPath(OH_Drawing_Path* path, uint32_t color, bool fill) {
    if (!canvas_ || !path) return;

    if (fill) {
        setBrushColor(color);
        OH_Drawing_CanvasAttachBrush(canvas_, brush_);
        OH_Drawing_CanvasDrawPath(canvas_, path);
        OH_Drawing_CanvasDetachBrush(canvas_);
    } else {
        setPenColor(color, 1.0f);
        OH_Drawing_CanvasAttachPen(canvas_, pen_);
        OH_Drawing_CanvasDrawPath(canvas_, path);
        OH_Drawing_CanvasDetachPen(canvas_);
    }
}

}  // namespace oh_adapter

#endif  // 2026-05-06 file body deactivated
