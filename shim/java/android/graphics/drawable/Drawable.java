package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.opengl.Visibility;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.opengl.Visibility;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;

/**
 * Shim: android.graphics.drawable.Drawable
 * OH mapping: drawing primitives / PixelMap textures
 *
 * Abstract base; concrete subclasses must implement draw() and getAlpha().
 */
public class Drawable {

    // ── Callback interface (AOSP name) ──────────────────────────────────────

    public interface Callback {
        void invalidateDrawable(Drawable who);
        void scheduleDrawable(Drawable who, Runnable what, long when);
        void unscheduleDrawable(Drawable who, Runnable what);
    }

    // Backward-compat alias (stub generator used "Object")
    public interface Object extends Callback {}

    // ── State ────────────────────────────────────────────────────────────────

    private final Rect    bounds  = new Rect();
    private       boolean visible = true;
    private       Callback callback;

    // ── Abstract API ─────────────────────────────────────────────────────────

    public void draw(Canvas canvas) {}

    public int getAlpha() { return 0; }

    // ── Optional overrides for subclasses ───────────────────────────────────

    public int getOpacity() { return -3; /* PixelFormat.TRANSLUCENT */ }

    public void setColorFilter(ColorFilter colorFilter) { /* no-op */ }

    protected boolean onLevelChange(int level) { return false; }

    public final boolean setLevel(int level) {
        return onLevelChange(level);
    }

    // ── Bounds ───────────────────────────────────────────────────────────────

    public void setBounds(int left, int top, int right, int bottom) {
        bounds.set(left, top, right, bottom);
    }

    public void setBounds(Rect bounds) {
        if (bounds != null) this.bounds.set(bounds);
    }

    public Rect getBounds() { return bounds; }

    // ── Alpha ────────────────────────────────────────────────────────────────

    public void setAlpha(int alpha) { /* no-op in base; subclasses may override */ }

    // ── Intrinsic size ───────────────────────────────────────────────────────

    /** Returns -1 to indicate no intrinsic width. */
    public int getIntrinsicWidth()  { return -1; }

    /** Returns -1 to indicate no intrinsic height. */
    public int getIntrinsicHeight() { return -1; }

    // ── Visibility ───────────────────────────────────────────────────────────

    public boolean isVisible() { return visible; }

    /**
     * @param visible  true to make visible
     * @param restart  ignored in this shim
     * @return true if the visibility actually changed
     */
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = (this.visible != visible);
        this.visible = visible;
        return changed;
    }

    // ── Minimum size (AOSP) ─────────────────────────────────────────────────

    /** Returns 0 by default; subclasses with intrinsic size override. */
    public int getMinimumWidth() {
        int intrinsicWidth = getIntrinsicWidth();
        return intrinsicWidth > 0 ? intrinsicWidth : 0;
    }

    /** Returns 0 by default; subclasses with intrinsic size override. */
    public int getMinimumHeight() {
        int intrinsicHeight = getIntrinsicHeight();
        return intrinsicHeight > 0 ? intrinsicHeight : 0;
    }

    // ── Callback / invalidation ─────────────────────────────────────────────

    public void setCallback(Callback cb) { this.callback = cb; }
    public Callback getCallback()        { return callback; }

    public void invalidateSelf() {
        if (callback != null) callback.invalidateDrawable(this);
    }

    // ── ConstantState ───────────────────────────────────────────────────────

    /**
     * Shim: Drawable.ConstantState – shared state between Drawable instances.
     */
    public static abstract class ConstantState {
        public Drawable newDrawable() { return null; }
        public Drawable newDrawable(android.content.res.Resources resources) { return newDrawable(); }
        public Drawable newDrawable(android.content.res.Resources resources, android.content.res.Resources.Theme theme) { return newDrawable(); }
        public int getChangingConfigurations() { return 0; }
    }

    public ConstantState getConstantState() { return null; }

    // ── Padding ─────────────────────────────────────────────────────────────

    public boolean getPadding(Rect padding) {
        padding.set(0, 0, 0, 0);
        return false;
    }

    // ── State ────────────────────────────────────────────────────────────────

    public boolean isStateful() { return false; }
    public boolean setState(int[] stateSet) { return false; }
    public int[] getState() { return new int[0]; }
    public Drawable getCurrent() { return this; }

    // ── Tint ─────────────────────────────────────────────────────────────────

    public void setTintList(android.content.res.ColorStateList tint) {}

    // ── Hotspot ──────────────────────────────────────────────────────────────

    public void setHotspot(float x, float y) {}
    public void setHotspotBounds(int left, int top, int right, int bottom) {}

    // ── Mutate ──────────────────────────────────────────────────────────────

    public Drawable mutate() { return this; }

    // ── Jump to current state ───────────────────────────────────────────────

    public void jumpToCurrentState() {}

    // ── AutoMirrored ────────────────────────────────────────────────────────

    public boolean isAutoMirrored() { return false; }
    public void setAutoMirrored(boolean mirrored) {}

    // ── Layout direction ────────────────────────────────────────────────────

    public void setLayoutDirection(int layoutDirection) {}
    public int getLayoutDirection() { return 0; }

    // ── BlendMode ───────────────────────────────────────────────────────────

    public static android.graphics.BlendMode parseBlendMode(int val, android.graphics.BlendMode defaultMode) {
        return defaultMode;
    }

    public void setTintBlendMode(android.graphics.BlendMode blendMode) {}

    public static Drawable createFromPath(String pathName) { return null; }
    public boolean hasFocusStateSpecified() { return false; }
    public void setXfermode(android.graphics.Xfermode mode) {}
    public int getChangingConfigurations() { return 0; }
    public void setChangingConfigurations(int configs) {}

    // Methods needed for View.java compilation
    public android.graphics.Insets getOpticalInsets() { return android.graphics.Insets.NONE; }
    public boolean isProjected() { return false; }
    public void copyBounds(android.graphics.Rect bounds) {
        if (bounds != null) {
            bounds.set(this.bounds.left, this.bounds.top, this.bounds.right, this.bounds.bottom);
        }
    }
    public android.graphics.Rect getDirtyBounds() { return getBounds(); }
    public android.graphics.Region getTransparentRegion() { return null; }
    public void getHotspotBounds(android.graphics.Rect outRect) {
        if (outRect != null) outRect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return getClass().getSimpleName()
             + "(bounds=" + bounds + ", visible=" + visible + ")";
    }
}
