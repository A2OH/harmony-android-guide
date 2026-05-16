/*
 * Software Canvas renderer for Dalvik on ARM32 QEMU.
 * Replaces OH_Drawing stubs with a real pixel-pushing implementation
 * using stb_truetype for text rendering.
 *
 * All drawing goes to an in-memory ARGB8888 pixel buffer (SWBitmap).
 * The ViewDumper init binary can read the bitmap pixels to blit to /dev/fb0.
 */
#ifndef SOFTWARE_CANVAS_H
#define SOFTWARE_CANVAS_H

#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

/* ── Data structures ── */

typedef struct {
    uint32_t *pixels;
    int width, height;
} SWBitmap;

typedef struct {
    int color;   /* ARGB */
    float width;
    int antialias;
    int cap;     /* 0=BUTT 1=ROUND 2=SQUARE */
    int join;    /* 0=MITER 1=ROUND 2=BEVEL */
} SWPen;

typedef struct {
    int color;   /* ARGB */
} SWBrush;

typedef struct {
    float size;
} SWFont;

/* Simple path: array of commands */
#define PATH_MOVE  0
#define PATH_LINE  1
#define PATH_CLOSE 2
#define PATH_MAX_OPS 1024

typedef struct {
    int ops[PATH_MAX_OPS];
    float xs[PATH_MAX_OPS];
    float ys[PATH_MAX_OPS];
    int count;
} SWPath;

/* Canvas with save/restore stack */
#define CANVAS_STACK_SIZE 32
typedef struct {
    SWBitmap *bitmap;
    float tx, ty;       /* translation */
    float sx, sy;       /* scale */
    float clipL, clipT, clipR, clipB;
    /* Save stack */
    struct {
        float tx, ty, sx, sy;
        float clipL, clipT, clipR, clipB;
    } stack[CANVAS_STACK_SIZE];
    int stackDepth;
} SWCanvas;

/* ── Bitmap operations ── */

static SWBitmap *sw_bitmap_create(int w, int h) {
    SWBitmap *b = (SWBitmap*)calloc(1, sizeof(SWBitmap));
    if (!b) return NULL;
    b->width = w; b->height = h;
    b->pixels = (uint32_t*)calloc(w * h, sizeof(uint32_t));
    return b;
}

static void sw_bitmap_destroy(SWBitmap *b) {
    if (b) { free(b->pixels); free(b); }
}

static inline void sw_pixel_blend(uint32_t *dst, uint32_t color) {
    uint8_t a = (color >> 24) & 0xFF;
    if (a == 0) return;
    if (a == 255) { *dst = color; return; }
    uint32_t bg = *dst;
    uint8_t br = (bg >> 16) & 0xFF, bg_g = (bg >> 8) & 0xFF, bb = bg & 0xFF;
    uint8_t fr = (color >> 16) & 0xFF, fg = (color >> 8) & 0xFF, fb = color & 0xFF;
    uint8_t r = (fr * a + br * (255 - a)) / 255;
    uint8_t g = (fg * a + bg_g * (255 - a)) / 255;
    uint8_t b = (fb * a + bb * (255 - a)) / 255;
    *dst = 0xFF000000 | (r << 16) | (g << 8) | b;
}

/* ── Canvas operations ── */

static SWCanvas *sw_canvas_create(SWBitmap *bmp) {
    SWCanvas *c = (SWCanvas*)calloc(1, sizeof(SWCanvas));
    if (!c) return NULL;
    c->bitmap = bmp;
    c->sx = c->sy = 1.0f;
    c->clipL = 0; c->clipT = 0;
    c->clipR = bmp ? (float)bmp->width : 0;
    c->clipB = bmp ? (float)bmp->height : 0;
    return c;
}

static void sw_canvas_save(SWCanvas *c) {
    if (c->stackDepth >= CANVAS_STACK_SIZE) return;
    int d = c->stackDepth++;
    c->stack[d].tx = c->tx; c->stack[d].ty = c->ty;
    c->stack[d].sx = c->sx; c->stack[d].sy = c->sy;
    c->stack[d].clipL = c->clipL; c->stack[d].clipT = c->clipT;
    c->stack[d].clipR = c->clipR; c->stack[d].clipB = c->clipB;
}

static void sw_canvas_restore(SWCanvas *c) {
    if (c->stackDepth <= 0) return;
    int d = --c->stackDepth;
    c->tx = c->stack[d].tx; c->ty = c->stack[d].ty;
    c->sx = c->stack[d].sx; c->sy = c->stack[d].sy;
    c->clipL = c->stack[d].clipL; c->clipT = c->stack[d].clipT;
    c->clipR = c->stack[d].clipR; c->clipB = c->stack[d].clipB;
}

static inline int sw_clamp(int v, int lo, int hi) { return v < lo ? lo : (v > hi ? hi : v); }

static void sw_canvas_fill_rect(SWCanvas *c, float l, float t, float r, float b, uint32_t color) {
    if (!c || !c->bitmap) return;
    int x0 = sw_clamp((int)(l * c->sx + c->tx), (int)c->clipL, (int)c->clipR);
    int y0 = sw_clamp((int)(t * c->sy + c->ty), (int)c->clipT, (int)c->clipB);
    int x1 = sw_clamp((int)(r * c->sx + c->tx), (int)c->clipL, (int)c->clipR);
    int y1 = sw_clamp((int)(b * c->sy + c->ty), (int)c->clipT, (int)c->clipB);
    int W = c->bitmap->width;
    for (int y = y0; y < y1 && y < c->bitmap->height; y++)
        for (int x = x0; x < x1 && x < W; x++)
            sw_pixel_blend(&c->bitmap->pixels[y * W + x], color);
}

static void sw_canvas_stroke_rect(SWCanvas *c, float l, float t, float r, float b, uint32_t color, float strokeW) {
    if (strokeW < 1) strokeW = 1;
    float hw = strokeW / 2.0f;
    sw_canvas_fill_rect(c, l - hw, t - hw, r + hw, t + hw, color); /* top */
    sw_canvas_fill_rect(c, l - hw, b - hw, r + hw, b + hw, color); /* bottom */
    sw_canvas_fill_rect(c, l - hw, t, l + hw, b, color); /* left */
    sw_canvas_fill_rect(c, r - hw, t, r + hw, b, color); /* right */
}

static void sw_canvas_draw_line(SWCanvas *c, float x1, float y1, float x2, float y2, uint32_t color, float strokeW) {
    if (!c || !c->bitmap) return;
    /* Bresenham with thickness */
    float hw = strokeW / 2.0f;
    if (hw < 0.5f) hw = 0.5f;
    int W = c->bitmap->width, H = c->bitmap->height;
    float dx = x2 - x1, dy = y2 - y1;
    float len = sqrtf(dx * dx + dy * dy);
    if (len < 0.001f) return;
    float steps = len * 2;
    for (float i = 0; i <= steps; i++) {
        float t = i / steps;
        float px = (x1 + dx * t) * c->sx + c->tx;
        float py = (y1 + dy * t) * c->sy + c->ty;
        int ix = (int)px, iy = (int)py;
        int ihw = (int)(hw + 0.5f);
        for (int dy2 = -ihw; dy2 <= ihw; dy2++)
            for (int dx2 = -ihw; dx2 <= ihw; dx2++) {
                int sx = ix + dx2, sy = iy + dy2;
                if (sx >= 0 && sx < W && sy >= 0 && sy < H)
                    sw_pixel_blend(&c->bitmap->pixels[sy * W + sx], color);
            }
    }
}

static void sw_canvas_fill_circle(SWCanvas *c, float cx, float cy, float r, uint32_t color) {
    if (!c || !c->bitmap) return;
    int W = c->bitmap->width, H = c->bitmap->height;
    float tcx = cx * c->sx + c->tx, tcy = cy * c->sy + c->ty;
    float tr = r * c->sx; /* approximate */
    int x0 = sw_clamp((int)(tcx - tr - 1), 0, W);
    int x1 = sw_clamp((int)(tcx + tr + 1), 0, W);
    int y0 = sw_clamp((int)(tcy - tr - 1), 0, H);
    int y1 = sw_clamp((int)(tcy + tr + 1), 0, H);
    for (int y = y0; y < y1; y++)
        for (int x = x0; x < x1; x++) {
            float dx = x - tcx, dy = y - tcy;
            if (dx * dx + dy * dy <= tr * tr)
                sw_pixel_blend(&c->bitmap->pixels[y * W + x], color);
        }
}

static void sw_canvas_fill_roundrect(SWCanvas *c, float l, float t, float r, float b, float rx, float ry, uint32_t color) {
    /* Simple: fill rect + circles at corners */
    if (rx < 1 || ry < 1) { sw_canvas_fill_rect(c, l, t, r, b, color); return; }
    /* Fill center */
    sw_canvas_fill_rect(c, l + rx, t, r - rx, b, color);
    sw_canvas_fill_rect(c, l, t + ry, r, b - ry, color);
    /* Corner circles */
    sw_canvas_fill_circle(c, l + rx, t + ry, rx, color);
    sw_canvas_fill_circle(c, r - rx, t + ry, rx, color);
    sw_canvas_fill_circle(c, l + rx, b - ry, rx, color);
    sw_canvas_fill_circle(c, r - rx, b - ry, rx, color);
}

/* ── stb_truetype text rendering ── */

#ifdef STB_TRUETYPE_IMPLEMENTATION
/* Already included */
#else
#define STB_TRUETYPE_IMPLEMENTATION
#define STBTT_ifloor(x) ((int)(x))
#define STBTT_iceil(x)  ((int)((x)+0.999999))
#include "stb_truetype.h"
#endif

static stbtt_fontinfo g_sw_font;
static int g_sw_font_loaded = 0;
static unsigned char *g_sw_font_data = NULL;

static int sw_load_font(const char *path) {
    if (g_sw_font_loaded) return 1;
    FILE *f = fopen(path, "rb");
    if (!f) return 0;
    fseek(f, 0, SEEK_END);
    long sz = ftell(f);
    fseek(f, 0, SEEK_SET);
    g_sw_font_data = (unsigned char*)malloc(sz);
    if (!g_sw_font_data) { fclose(f); return 0; }
    fread(g_sw_font_data, 1, sz, f);
    fclose(f);
    if (!stbtt_InitFont(&g_sw_font, g_sw_font_data, 0)) {
        free(g_sw_font_data); g_sw_font_data = NULL; return 0;
    }
    g_sw_font_loaded = 1;
    return 1;
}

static void sw_canvas_draw_text(SWCanvas *c, const char *text, float x, float y, float fontSize, uint32_t color) {
    if (!c || !c->bitmap || !text) return;
    int W = c->bitmap->width, H = c->bitmap->height;

    if (!g_sw_font_loaded) {
        /* Fallback: block font */
        float bw = fontSize * 0.55f;
        for (int i = 0; text[i]; i++) {
            if (text[i] != ' ') {
                int px = (int)((x + i * bw) * c->sx + c->tx);
                int py = (int)(y * c->sy + c->ty);
                int pw = (int)(bw - 1); int ph = (int)(fontSize * 0.8f);
                for (int dy = 0; dy < ph && py + dy < H; dy++)
                    for (int dx = 0; dx < pw && px + dx < W; dx++)
                        if (px + dx >= 0 && py + dy >= 0)
                            sw_pixel_blend(&c->bitmap->pixels[(py + dy) * W + (px + dx)], color);
            }
        }
        return;
    }

    float scale = stbtt_ScaleForPixelHeight(&g_sw_font, fontSize);
    int ascent, descent, lineGap;
    stbtt_GetFontVMetrics(&g_sw_font, &ascent, &descent, &lineGap);
    int baseline = (int)(y * c->sy + c->ty + ascent * scale);

    float xpos = x * c->sx + c->tx;
    for (int i = 0; text[i]; i++) {
        int ch = (unsigned char)text[i];
        if (ch < 32) continue;

        int advance, lsb;
        stbtt_GetCodepointHMetrics(&g_sw_font, ch, &advance, &lsb);

        int x0, y0, x1, y1;
        stbtt_GetCodepointBitmapBox(&g_sw_font, ch, scale, scale, &x0, &y0, &x1, &y1);

        int gw = x1 - x0, gh = y1 - y0;
        if (gw > 0 && gh > 0 && gw < 256 && gh < 256) {
            unsigned char bmp[256 * 256];
            stbtt_MakeCodepointBitmap(&g_sw_font, bmp, gw, gh, gw, scale, scale, ch);

            int dx_start = (int)(xpos + x0 + lsb * scale);
            int dy_start = baseline + y0;
            uint8_t cr = (color >> 16) & 0xFF, cg = (color >> 8) & 0xFF, cb = color & 0xFF;
            for (int row = 0; row < gh; row++) {
                int sy = dy_start + row;
                if (sy < 0 || sy >= H) continue;
                for (int col = 0; col < gw; col++) {
                    int sx = dx_start + col;
                    if (sx < 0 || sx >= W) continue;
                    uint8_t a = bmp[row * gw + col];
                    if (a > 0) {
                        uint32_t blended = ((uint32_t)a << 24) | ((uint32_t)cr << 16) | ((uint32_t)cg << 8) | cb;
                        sw_pixel_blend(&c->bitmap->pixels[sy * W + sx], blended);
                    }
                }
            }
        }
        xpos += advance * scale;
        if (text[i + 1]) {
            int kern = stbtt_GetCodepointKernAdvance(&g_sw_font, ch, (unsigned char)text[i + 1]);
            xpos += kern * scale;
        }
    }
}

#endif /* SOFTWARE_CANVAS_H */
