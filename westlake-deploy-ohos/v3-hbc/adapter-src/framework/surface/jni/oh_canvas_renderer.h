/*
 * oh_canvas_renderer.h
 *
 * Software rendering adapter: implements Android Canvas drawing operations
 * using OH Drawing NDK API (OH_Drawing_*).
 *
 * This is the software rendering path (Part I):
 *   1. lockCanvas()   — dequeue buffer from OH Surface, mmap, create OH_Drawing_Canvas
 *   2. draw*()        — forward Android Canvas calls to OH_Drawing_Canvas
 *   3. unlockAndPost() — flush drawing, unmap, queue buffer back to OH Surface
 *
 * The OH Drawing NDK wraps Skia M133 (libskia_canvaskit.z.so) internally,
 * so all 2D rendering is GPU-quality even in software mode.
 *
 * Used by: ViewRootImpl.drawSoftware() path, and as fallback for
 *          hardwareAccelerated=false Views.
 */
#ifndef OH_CANVAS_RENDERER_H
#define OH_CANVAS_RENDERER_H

#include <cstdint>
#include <mutex>

// Forward declarations — actual OH Drawing types are opaque handles
typedef struct OH_Drawing_Canvas OH_Drawing_Canvas;
typedef struct OH_Drawing_Brush OH_Drawing_Brush;
typedef struct OH_Drawing_Pen OH_Drawing_Pen;
typedef struct OH_Drawing_Path OH_Drawing_Path;
typedef struct OH_Drawing_Bitmap OH_Drawing_Bitmap;
typedef struct OH_Drawing_Typeface OH_Drawing_Typeface;
typedef struct OH_Drawing_Font OH_Drawing_Font;
typedef struct OH_Drawing_TextBlob OH_Drawing_TextBlob;
typedef struct OH_Drawing_TextBlobBuilder OH_Drawing_TextBlobBuilder;
typedef struct OH_Drawing_Rect OH_Drawing_Rect;
typedef struct OH_Drawing_RoundRect OH_Drawing_RoundRect;
typedef struct OH_Drawing_Matrix OH_Drawing_Matrix;
typedef struct OH_Drawing_Region OH_Drawing_Region;

// Forward declarations for OH Surface types
namespace OHOS {
    template<typename T> class sptr;
    class Surface;
    class SurfaceBuffer;
}

namespace oh_adapter {

class OHGraphicBufferProducer;

/**
 * OHCanvasRenderer provides Android Canvas-compatible drawing operations
 * backed by OH Drawing NDK API.
 *
 * Lifecycle:
 *   auto renderer = OHCanvasRenderer::create(producer, width, height);
 *   renderer->lockCanvas(dirtyL, dirtyT, dirtyR, dirtyB);
 *   renderer->drawColor(0xFFFFFFFF);  // white background
 *   renderer->drawText("Hello", 100, 200, textSize, color);
 *   renderer->unlockAndPost();
 */
class OHCanvasRenderer {
public:
    /**
     * Create renderer bound to an OH Surface via its producer.
     */
    static OHCanvasRenderer* create(OHGraphicBufferProducer* producer,
                                    int32_t width, int32_t height);
    ~OHCanvasRenderer();

    /**
     * Lock the canvas for drawing.
     * Dequeues a buffer from OH Surface, creates OH_Drawing_Canvas bound to it.
     *
     * @param dirtyLeft/Top/Right/Bottom  Dirty region (0,0,w,h for full redraw).
     * @return true on success.
     */
    bool lockCanvas(int32_t dirtyLeft, int32_t dirtyTop,
                    int32_t dirtyRight, int32_t dirtyBottom);

    /**
     * Unlock and post the canvas.
     * Flushes OH_Drawing_Canvas, queues buffer back to OH Surface.
     *
     * @return true on success.
     */
    bool unlockAndPost();

    // ========== Canvas State Operations ==========

    int save();
    void restore();
    void restoreToCount(int saveCount);
    int getSaveCount() const;

    // ========== Transform Operations ==========

    void translate(float dx, float dy);
    void scale(float sx, float sy);
    void rotate(float degrees);
    void rotate(float degrees, float px, float py);
    void concat(const float matrix[9]);  // 3x3 matrix

    // ========== Clip Operations ==========

    void clipRect(float left, float top, float right, float bottom);
    void clipPath(OH_Drawing_Path* path);

    // ========== Draw Operations ==========

    /** Fill entire canvas with color (ARGB). */
    void drawColor(uint32_t color);

    /** Draw rectangle. */
    void drawRect(float left, float top, float right, float bottom,
                  uint32_t color, bool fill = true, float strokeWidth = 0.0f);

    /** Draw rounded rectangle. */
    void drawRoundRect(float left, float top, float right, float bottom,
                       float rx, float ry,
                       uint32_t color, bool fill = true);

    /** Draw circle. */
    void drawCircle(float cx, float cy, float radius,
                    uint32_t color, bool fill = true);

    /** Draw line. */
    void drawLine(float startX, float startY, float stopX, float stopY,
                  uint32_t color, float strokeWidth = 1.0f);

    /** Draw text string. */
    void drawText(const char* text, float x, float y,
                  float textSize, uint32_t color);

    /** Draw text string with specified typeface. */
    void drawTextWithTypeface(const char* text, float x, float y,
                              float textSize, uint32_t color,
                              OH_Drawing_Typeface* typeface);

    /** Draw bitmap/image at position. */
    void drawBitmap(OH_Drawing_Bitmap* bitmap, float left, float top);

    /** Draw path with specified color and fill mode. */
    void drawPath(OH_Drawing_Path* path, uint32_t color, bool fill = true);

    // ========== Dimension Queries ==========

    int32_t getWidth() const { return width_; }
    int32_t getHeight() const { return height_; }

private:
    OHCanvasRenderer(OHGraphicBufferProducer* producer,
                     int32_t width, int32_t height);

    // Disallow copy
    OHCanvasRenderer(const OHCanvasRenderer&) = delete;
    OHCanvasRenderer& operator=(const OHCanvasRenderer&) = delete;

    // Helper: set brush color (ARGB)
    void setBrushColor(uint32_t argb);
    // Helper: set pen color and width
    void setPenColor(uint32_t argb, float strokeWidth);

    OHGraphicBufferProducer* producer_;  // Not owned
    int32_t width_;
    int32_t height_;

    // OH Drawing handles (created during lockCanvas, destroyed in unlockAndPost)
    OH_Drawing_Canvas* canvas_ = nullptr;
    OH_Drawing_Bitmap* targetBitmap_ = nullptr;
    OH_Drawing_Brush* brush_ = nullptr;
    OH_Drawing_Pen* pen_ = nullptr;
    OH_Drawing_Font* defaultFont_ = nullptr;
    OH_Drawing_Typeface* defaultTypeface_ = nullptr;

    // Buffer state
    int currentSlot_ = -1;
    int currentFenceFd_ = -1;
    void* bufferAddr_ = nullptr;   // mmap'd buffer address
    int32_t bufferStride_ = 0;

    std::mutex mutex_;
    bool locked_ = false;
};

}  // namespace oh_adapter

#endif  // OH_CANVAS_RENDERER_H
