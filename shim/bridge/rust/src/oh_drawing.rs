//! OH_Drawing JNI bridge — maps Java Canvas/Paint/Bitmap/Path/Font calls
//! from OHBridge native methods to OpenHarmony's OH_Drawing C NDK.
//!
//! Every function follows the pattern:
//! 1. Cast jlong handles back to *mut OH_Drawing_* pointers
//! 2. Call the corresponding OH_Drawing_* C function
//! 3. Return results as jlong/jint/void

#![allow(non_snake_case)]

use jni::JNIEnv;
use jni::objects::{JClass, JFloatArray, JString};
use jni::sys::{jboolean, jfloat, jint, jlong};
use std::ffi::CString;
use std::os::raw::c_int;

use crate::oh_ffi::*;

// ═══════════════════════════════════════════════════════════════════
// Helper: attach pen and/or brush to canvas, returning whether each
// was attached (so caller can detach after draw).
// ═══════════════════════════════════════════════════════════════════

unsafe fn attach_pen_brush(canvas: *mut OH_Drawing_Canvas, pen: jlong, brush: jlong) -> (bool, bool) {
    let has_pen = pen != 0;
    let has_brush = brush != 0;
    if has_pen {
        OH_Drawing_CanvasAttachPen(canvas, pen as *const OH_Drawing_Pen);
    }
    if has_brush {
        OH_Drawing_CanvasAttachBrush(canvas, brush as *const OH_Drawing_Brush);
    }
    (has_pen, has_brush)
}

unsafe fn detach_pen_brush(canvas: *mut OH_Drawing_Canvas, had_pen: bool, had_brush: bool) {
    if had_pen {
        OH_Drawing_CanvasDetachPen(canvas);
    }
    if had_brush {
        OH_Drawing_CanvasDetachBrush(canvas);
    }
}

// ═══════════════════════════════════════════════════════════════════
// Bitmap
// ═══════════════════════════════════════════════════════════════════

/// bitmapCreate(int width, int height, int format) -> long
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_bitmapCreate(
    _env: JNIEnv, _class: JClass,
    width: jint, height: jint, _format: jint,
) -> jlong {
    unsafe {
        let bmp = OH_Drawing_BitmapCreate();
        if bmp.is_null() {
            return 0;
        }
        // format: 0 = ARGB_8888 -> colorType=4 (RGBA_8888), alphaType=2 (premul)
        let fmt = OH_Drawing_BitmapFormat {
            color_type: 4, // COLOR_FORMAT_RGBA_8888
            alpha_type: 2, // ALPHA_FORMAT_PREMUL
        };
        OH_Drawing_BitmapBuild(bmp, width as u32, height as u32, &fmt);
        bmp as jlong
    }
}

/// bitmapDestroy(long bitmap)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_bitmapDestroy(
    _env: JNIEnv, _class: JClass, bitmap: jlong,
) {
    if bitmap == 0 { return; }
    unsafe { OH_Drawing_BitmapDestroy(bitmap as *mut OH_Drawing_Bitmap); }
}

/// bitmapGetWidth(long bitmap) -> int
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_bitmapGetWidth(
    _env: JNIEnv, _class: JClass, bitmap: jlong,
) -> jint {
    if bitmap == 0 { return 0; }
    unsafe { OH_Drawing_BitmapGetWidth(bitmap as *mut OH_Drawing_Bitmap) as jint }
}

/// bitmapGetHeight(long bitmap) -> int
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_bitmapGetHeight(
    _env: JNIEnv, _class: JClass, bitmap: jlong,
) -> jint {
    if bitmap == 0 { return 0; }
    unsafe { OH_Drawing_BitmapGetHeight(bitmap as *mut OH_Drawing_Bitmap) as jint }
}

/// bitmapSetPixel(long bitmap, int x, int y, int argb)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_bitmapSetPixel(
    _env: JNIEnv, _class: JClass,
    bitmap: jlong, x: jint, y: jint, argb: jint,
) {
    if bitmap == 0 { return; }
    unsafe {
        let bmp = bitmap as *mut OH_Drawing_Bitmap;
        let pixels = OH_Drawing_BitmapGetPixels(bmp) as *mut u8;
        if pixels.is_null() { return; }
        let w = OH_Drawing_BitmapGetWidth(bmp) as i32;
        if x < 0 || y < 0 || x >= w { return; }
        // RGBA_8888 layout: R, G, B, A (one byte each)
        // Android ARGB_8888: 0xAARRGGBB
        let a = ((argb >> 24) & 0xFF) as u8;
        let r = ((argb >> 16) & 0xFF) as u8;
        let g = ((argb >> 8) & 0xFF) as u8;
        let b = (argb & 0xFF) as u8;
        let offset = ((y * w + x) * 4) as isize;
        *pixels.offset(offset) = r;
        *pixels.offset(offset + 1) = g;
        *pixels.offset(offset + 2) = b;
        *pixels.offset(offset + 3) = a;
    }
}

/// bitmapGetPixel(long bitmap, int x, int y) -> int (ARGB)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_bitmapGetPixel(
    _env: JNIEnv, _class: JClass,
    bitmap: jlong, x: jint, y: jint,
) -> jint {
    if bitmap == 0 { return 0; }
    unsafe {
        let bmp = bitmap as *mut OH_Drawing_Bitmap;
        let pixels = OH_Drawing_BitmapGetPixels(bmp) as *const u8;
        if pixels.is_null() { return 0; }
        let w = OH_Drawing_BitmapGetWidth(bmp) as i32;
        if x < 0 || y < 0 || x >= w { return 0; }
        let offset = ((y * w + x) * 4) as isize;
        let r = *pixels.offset(offset) as i32;
        let g = *pixels.offset(offset + 1) as i32;
        let b = *pixels.offset(offset + 2) as i32;
        let a = *pixels.offset(offset + 3) as i32;
        // Convert RGBA -> ARGB
        (a << 24) | (r << 16) | (g << 8) | b
    }
}

// ═══════════════════════════════════════════════════════════════════
// Canvas
// ═══════════════════════════════════════════════════════════════════

/// canvasCreate(long bitmapHandle) -> long
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasCreate(
    _env: JNIEnv, _class: JClass, bitmap_handle: jlong,
) -> jlong {
    unsafe {
        let canvas = OH_Drawing_CanvasCreate();
        if canvas.is_null() { return 0; }
        if bitmap_handle != 0 {
            OH_Drawing_CanvasBind(canvas, bitmap_handle as *mut OH_Drawing_Bitmap);
        }
        canvas as jlong
    }
}

/// canvasDestroy(long canvas)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDestroy(
    _env: JNIEnv, _class: JClass, canvas: jlong,
) {
    if canvas == 0 { return; }
    unsafe { OH_Drawing_CanvasDestroy(canvas as *mut OH_Drawing_Canvas); }
}

/// canvasDrawRect(long canvas, float l, float t, float r, float b, long pen, long brush)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDrawRect(
    _env: JNIEnv, _class: JClass,
    canvas: jlong, l: jfloat, t: jfloat, r: jfloat, b: jfloat,
    pen: jlong, brush: jlong,
) {
    if canvas == 0 { return; }
    unsafe {
        let c = canvas as *mut OH_Drawing_Canvas;
        let (hp, hb) = attach_pen_brush(c, pen, brush);
        let rect = OH_Drawing_RectCreate(l, t, r, b);
        OH_Drawing_CanvasDrawRect(c, rect);
        OH_Drawing_RectDestroy(rect);
        detach_pen_brush(c, hp, hb);
    }
}

/// canvasDrawCircle(long canvas, float cx, float cy, float r, long pen, long brush)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDrawCircle(
    _env: JNIEnv, _class: JClass,
    canvas: jlong, cx: jfloat, cy: jfloat, r: jfloat,
    pen: jlong, brush: jlong,
) {
    if canvas == 0 { return; }
    unsafe {
        let c = canvas as *mut OH_Drawing_Canvas;
        let (hp, hb) = attach_pen_brush(c, pen, brush);
        OH_Drawing_CanvasDrawCircle(c, cx, cy, r);
        detach_pen_brush(c, hp, hb);
    }
}

/// canvasDrawLine(long canvas, float x1, float y1, float x2, float y2, long pen)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDrawLine(
    _env: JNIEnv, _class: JClass,
    canvas: jlong, x1: jfloat, y1: jfloat, x2: jfloat, y2: jfloat,
    pen: jlong,
) {
    if canvas == 0 { return; }
    unsafe {
        let c = canvas as *mut OH_Drawing_Canvas;
        if pen != 0 {
            OH_Drawing_CanvasAttachPen(c, pen as *const OH_Drawing_Pen);
        }
        OH_Drawing_CanvasDrawLine(c, x1, y1, x2, y2);
        if pen != 0 {
            OH_Drawing_CanvasDetachPen(c);
        }
    }
}

/// canvasDrawPath(long canvas, long path, long pen, long brush)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDrawPath(
    _env: JNIEnv, _class: JClass,
    canvas: jlong, path: jlong, pen: jlong, brush: jlong,
) {
    if canvas == 0 || path == 0 { return; }
    unsafe {
        let c = canvas as *mut OH_Drawing_Canvas;
        let (hp, hb) = attach_pen_brush(c, pen, brush);
        OH_Drawing_CanvasDrawPath(c, path as *const OH_Drawing_Path);
        detach_pen_brush(c, hp, hb);
    }
}

/// canvasDrawBitmap(long canvas, long bitmap, float x, float y)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDrawBitmap(
    _env: JNIEnv, _class: JClass,
    canvas: jlong, bitmap: jlong, x: jfloat, y: jfloat,
) {
    if canvas == 0 || bitmap == 0 { return; }
    unsafe {
        OH_Drawing_CanvasDrawBitmap(
            canvas as *mut OH_Drawing_Canvas,
            bitmap as *const OH_Drawing_Bitmap,
            x, y,
        );
    }
}

/// canvasDrawText(long canvas, String text, float x, float y, long font, long pen, long brush)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDrawText(
    env: JNIEnv, _class: JClass,
    canvas: jlong, text: JString, x: jfloat, y: jfloat,
    font: jlong, pen: jlong, brush: jlong,
) {
    if canvas == 0 || font == 0 { return; }
    let text_str: String = match env.get_string(text) {
        Ok(s) => s.into(),
        Err(_) => return,
    };
    let c_text = match CString::new(text_str) {
        Ok(s) => s,
        Err(_) => return,
    };
    unsafe {
        let c = canvas as *mut OH_Drawing_Canvas;
        let (hp, hb) = attach_pen_brush(c, pen, brush);
        // encoding 0 = UTF8
        let blob = OH_Drawing_TextBlobCreateFromString(
            c_text.as_ptr(), font as *const OH_Drawing_Font, 0,
        );
        if !blob.is_null() {
            OH_Drawing_CanvasDrawTextBlob(c, blob, x, y);
            OH_Drawing_TextBlobDestroy(blob);
        }
        detach_pen_brush(c, hp, hb);
    }
}

/// canvasSave(long canvas)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasSave(
    _env: JNIEnv, _class: JClass, canvas: jlong,
) {
    if canvas == 0 { return; }
    unsafe { OH_Drawing_CanvasSave(canvas as *mut OH_Drawing_Canvas); }
}

/// canvasRestore(long canvas)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasRestore(
    _env: JNIEnv, _class: JClass, canvas: jlong,
) {
    if canvas == 0 { return; }
    unsafe { OH_Drawing_CanvasRestore(canvas as *mut OH_Drawing_Canvas); }
}

/// canvasTranslate(long canvas, float dx, float dy)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasTranslate(
    _env: JNIEnv, _class: JClass, canvas: jlong, dx: jfloat, dy: jfloat,
) {
    if canvas == 0 { return; }
    unsafe { OH_Drawing_CanvasTranslate(canvas as *mut OH_Drawing_Canvas, dx, dy); }
}

/// canvasScale(long canvas, float sx, float sy)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasScale(
    _env: JNIEnv, _class: JClass, canvas: jlong, sx: jfloat, sy: jfloat,
) {
    if canvas == 0 { return; }
    unsafe { OH_Drawing_CanvasScale(canvas as *mut OH_Drawing_Canvas, sx, sy); }
}

/// canvasRotate(long canvas, float degrees, float px, float py)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasRotate(
    _env: JNIEnv, _class: JClass,
    canvas: jlong, degrees: jfloat, px: jfloat, py: jfloat,
) {
    if canvas == 0 { return; }
    unsafe { OH_Drawing_CanvasRotate(canvas as *mut OH_Drawing_Canvas, degrees, px, py); }
}

/// canvasClipRect(long canvas, float l, float t, float r, float b)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasClipRect(
    _env: JNIEnv, _class: JClass,
    canvas: jlong, l: jfloat, t: jfloat, r: jfloat, b: jfloat,
) {
    if canvas == 0 { return; }
    unsafe {
        let rect = OH_Drawing_RectCreate(l, t, r, b);
        // op=0 (INTERSECT), aa=true
        OH_Drawing_CanvasClipRect(canvas as *mut OH_Drawing_Canvas, rect, 0, true);
        OH_Drawing_RectDestroy(rect);
    }
}

/// canvasClipPath(long canvas, long path)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasClipPath(
    _env: JNIEnv, _class: JClass, canvas: jlong, path: jlong,
) {
    if canvas == 0 || path == 0 { return; }
    unsafe {
        // op=0 (INTERSECT), aa=true
        OH_Drawing_CanvasClipPath(
            canvas as *mut OH_Drawing_Canvas,
            path as *const OH_Drawing_Path,
            0, true,
        );
    }
}

/// canvasDrawColor(long canvas, int argb)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDrawColor(
    _env: JNIEnv, _class: JClass, canvas: jlong, argb: jint,
) {
    if canvas == 0 { return; }
    unsafe { OH_Drawing_CanvasClear(canvas as *mut OH_Drawing_Canvas, argb as u32); }
}

/// canvasDrawArc(long canvas, float l, float t, float r, float b,
///               float startAngle, float sweepAngle, boolean useCenter, long pen, long brush)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDrawArc(
    _env: JNIEnv, _class: JClass,
    canvas: jlong, l: jfloat, t: jfloat, r: jfloat, b: jfloat,
    start_angle: jfloat, sweep_angle: jfloat, _use_center: jboolean,
    pen: jlong, brush: jlong,
) {
    if canvas == 0 { return; }
    unsafe {
        let c = canvas as *mut OH_Drawing_Canvas;
        let (hp, hb) = attach_pen_brush(c, pen, brush);
        let rect = OH_Drawing_RectCreate(l, t, r, b);
        OH_Drawing_CanvasDrawArc(c, rect, start_angle, sweep_angle);
        OH_Drawing_RectDestroy(rect);
        detach_pen_brush(c, hp, hb);
    }
}

/// canvasDrawOval(long canvas, float l, float t, float r, float b, long pen, long brush)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDrawOval(
    _env: JNIEnv, _class: JClass,
    canvas: jlong, l: jfloat, t: jfloat, r: jfloat, b: jfloat,
    pen: jlong, brush: jlong,
) {
    if canvas == 0 { return; }
    unsafe {
        let c = canvas as *mut OH_Drawing_Canvas;
        let (hp, hb) = attach_pen_brush(c, pen, brush);
        let rect = OH_Drawing_RectCreate(l, t, r, b);
        OH_Drawing_CanvasDrawOval(c, rect);
        OH_Drawing_RectDestroy(rect);
        detach_pen_brush(c, hp, hb);
    }
}

/// canvasDrawRoundRect(long canvas, float l, float t, float r, float b,
///                     float rx, float ry, long pen, long brush)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDrawRoundRect(
    _env: JNIEnv, _class: JClass,
    canvas: jlong, l: jfloat, t: jfloat, r: jfloat, b: jfloat,
    rx: jfloat, ry: jfloat, pen: jlong, brush: jlong,
) {
    if canvas == 0 { return; }
    unsafe {
        let c = canvas as *mut OH_Drawing_Canvas;
        let (hp, hb) = attach_pen_brush(c, pen, brush);
        let rect = OH_Drawing_RectCreate(l, t, r, b);
        let rr = OH_Drawing_RoundRectCreate(rect, rx, ry);
        OH_Drawing_CanvasDrawRoundRect(c, rr);
        OH_Drawing_RoundRectDestroy(rr);
        OH_Drawing_RectDestroy(rect);
        detach_pen_brush(c, hp, hb);
    }
}

/// canvasConcat(long canvas, float[] matrix9)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasConcat(
    env: JNIEnv, _class: JClass,
    canvas: jlong, matrix9: JFloatArray,
) {
    if canvas == 0 { return; }
    let mut vals = [0.0f32; 9];
    if env.get_float_array_region(matrix9, 0, &mut vals).is_err() {
        return;
    }
    unsafe {
        let mat = OH_Drawing_MatrixCreate();
        // Android Matrix order: [scaleX, skewX, transX, skewY, scaleY, transY, persp0, persp1, persp2]
        OH_Drawing_MatrixSetMatrix(mat,
            vals[0], vals[1], vals[2],
            vals[3], vals[4], vals[5],
            vals[6], vals[7], vals[8],
        );
        OH_Drawing_CanvasConcatMatrix(canvas as *mut OH_Drawing_Canvas, mat);
        OH_Drawing_MatrixDestroy(mat);
    }
}

// ═══════════════════════════════════════════════════════════════════
// Pen (stroke style)
// ═══════════════════════════════════════════════════════════════════

/// penCreate() -> long
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_penCreate(
    _env: JNIEnv, _class: JClass,
) -> jlong {
    unsafe { OH_Drawing_PenCreate() as jlong }
}

/// penDestroy(long pen)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_penDestroy(
    _env: JNIEnv, _class: JClass, pen: jlong,
) {
    if pen == 0 { return; }
    unsafe { OH_Drawing_PenDestroy(pen as *mut OH_Drawing_Pen); }
}

/// penSetColor(long pen, int argb)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_penSetColor(
    _env: JNIEnv, _class: JClass, pen: jlong, argb: jint,
) {
    if pen == 0 { return; }
    unsafe { OH_Drawing_PenSetColor(pen as *mut OH_Drawing_Pen, argb as u32); }
}

/// penSetWidth(long pen, float width)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_penSetWidth(
    _env: JNIEnv, _class: JClass, pen: jlong, width: jfloat,
) {
    if pen == 0 { return; }
    unsafe { OH_Drawing_PenSetWidth(pen as *mut OH_Drawing_Pen, width); }
}

/// penSetAntiAlias(long pen, boolean aa)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_penSetAntiAlias(
    _env: JNIEnv, _class: JClass, pen: jlong, aa: jboolean,
) {
    if pen == 0 { return; }
    unsafe { OH_Drawing_PenSetAntiAlias(pen as *mut OH_Drawing_Pen, aa != 0); }
}

/// penSetCap(long pen, int cap)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_penSetCap(
    _env: JNIEnv, _class: JClass, pen: jlong, cap: jint,
) {
    if pen == 0 { return; }
    unsafe { OH_Drawing_PenSetCap(pen as *mut OH_Drawing_Pen, cap as c_int); }
}

/// penSetJoin(long pen, int join)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_penSetJoin(
    _env: JNIEnv, _class: JClass, pen: jlong, join: jint,
) {
    if pen == 0 { return; }
    unsafe { OH_Drawing_PenSetJoin(pen as *mut OH_Drawing_Pen, join as c_int); }
}

// ═══════════════════════════════════════════════════════════════════
// Brush (fill style)
// ═══════════════════════════════════════════════════════════════════

/// brushCreate() -> long
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_brushCreate(
    _env: JNIEnv, _class: JClass,
) -> jlong {
    unsafe { OH_Drawing_BrushCreate() as jlong }
}

/// brushDestroy(long brush)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_brushDestroy(
    _env: JNIEnv, _class: JClass, brush: jlong,
) {
    if brush == 0 { return; }
    unsafe { OH_Drawing_BrushDestroy(brush as *mut OH_Drawing_Brush); }
}

/// brushSetColor(long brush, int argb)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_brushSetColor(
    _env: JNIEnv, _class: JClass, brush: jlong, argb: jint,
) {
    if brush == 0 { return; }
    unsafe { OH_Drawing_BrushSetColor(brush as *mut OH_Drawing_Brush, argb as u32); }
}

// ═══════════════════════════════════════════════════════════════════
// Path
// ═══════════════════════════════════════════════════════════════════

/// pathCreate() -> long
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_pathCreate(
    _env: JNIEnv, _class: JClass,
) -> jlong {
    unsafe { OH_Drawing_PathCreate() as jlong }
}

/// pathDestroy(long path)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_pathDestroy(
    _env: JNIEnv, _class: JClass, path: jlong,
) {
    if path == 0 { return; }
    unsafe { OH_Drawing_PathDestroy(path as *mut OH_Drawing_Path); }
}

/// pathMoveTo(long path, float x, float y)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_pathMoveTo(
    _env: JNIEnv, _class: JClass, path: jlong, x: jfloat, y: jfloat,
) {
    if path == 0 { return; }
    unsafe { OH_Drawing_PathMoveTo(path as *mut OH_Drawing_Path, x, y); }
}

/// pathLineTo(long path, float x, float y)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_pathLineTo(
    _env: JNIEnv, _class: JClass, path: jlong, x: jfloat, y: jfloat,
) {
    if path == 0 { return; }
    unsafe { OH_Drawing_PathLineTo(path as *mut OH_Drawing_Path, x, y); }
}

/// pathQuadTo(long path, float x1, float y1, float x2, float y2)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_pathQuadTo(
    _env: JNIEnv, _class: JClass,
    path: jlong, x1: jfloat, y1: jfloat, x2: jfloat, y2: jfloat,
) {
    if path == 0 { return; }
    unsafe { OH_Drawing_PathQuadTo(path as *mut OH_Drawing_Path, x1, y1, x2, y2); }
}

/// pathCubicTo(long path, float x1, float y1, float x2, float y2, float x3, float y3)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_pathCubicTo(
    _env: JNIEnv, _class: JClass,
    path: jlong, x1: jfloat, y1: jfloat, x2: jfloat, y2: jfloat, x3: jfloat, y3: jfloat,
) {
    if path == 0 { return; }
    unsafe { OH_Drawing_PathCubicTo(path as *mut OH_Drawing_Path, x1, y1, x2, y2, x3, y3); }
}

/// pathClose(long path)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_pathClose(
    _env: JNIEnv, _class: JClass, path: jlong,
) {
    if path == 0 { return; }
    unsafe { OH_Drawing_PathClose(path as *mut OH_Drawing_Path); }
}

/// pathReset(long path)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_pathReset(
    _env: JNIEnv, _class: JClass, path: jlong,
) {
    if path == 0 { return; }
    unsafe { OH_Drawing_PathReset(path as *mut OH_Drawing_Path); }
}

/// pathAddRect(long path, float l, float t, float r, float b, int dir)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_pathAddRect(
    _env: JNIEnv, _class: JClass,
    path: jlong, l: jfloat, t: jfloat, r: jfloat, b: jfloat, dir: jint,
) {
    if path == 0 { return; }
    unsafe { OH_Drawing_PathAddRect(path as *mut OH_Drawing_Path, l, t, r, b, dir as c_int); }
}

/// pathAddCircle(long path, float cx, float cy, float r, int dir)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_pathAddCircle(
    _env: JNIEnv, _class: JClass,
    path: jlong, cx: jfloat, cy: jfloat, r: jfloat, dir: jint,
) {
    if path == 0 { return; }
    unsafe { OH_Drawing_PathAddCircle(path as *mut OH_Drawing_Path, cx, cy, r, dir as c_int); }
}

// ═══════════════════════════════════════════════════════════════════
// Font
// ═══════════════════════════════════════════════════════════════════

/// fontCreate() -> long
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_fontCreate(
    _env: JNIEnv, _class: JClass,
) -> jlong {
    unsafe { OH_Drawing_FontCreate() as jlong }
}

/// fontDestroy(long font)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_fontDestroy(
    _env: JNIEnv, _class: JClass, font: jlong,
) {
    if font == 0 { return; }
    unsafe { OH_Drawing_FontDestroy(font as *mut OH_Drawing_Font); }
}

/// fontSetSize(long font, float size)
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_fontSetSize(
    _env: JNIEnv, _class: JClass, font: jlong, size: jfloat,
) {
    if font == 0 { return; }
    unsafe { OH_Drawing_FontSetSize(font as *mut OH_Drawing_Font, size); }
}
