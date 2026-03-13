package android.graphics.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;

/**
 * Shim: android.graphics.drawable.Drawable
 * OH mapping: drawing primitives / PixelMap textures
 *
 * Abstract base; concrete subclasses must implement draw() and getAlpha().
 */
public abstract class Drawable {

    // ── Callback interface ───────────────────────────────────────────────────

    public interface Callback {
        void invalidateDrawable(Drawable who);
        void scheduleDrawable(Drawable who, Runnable what, long when);
        void unscheduleDrawable(Drawable who, Runnable what);
    }

    // ── State ────────────────────────────────────────────────────────────────

    private final Rect    bounds  = new Rect();
    private       boolean visible = true;
    private       Callback callback;

    // ── Abstract API ─────────────────────────────────────────────────────────

    public abstract void draw(Canvas canvas);

    public abstract int  getAlpha();

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

    // ── Callback / invalidation ──────────────────────────────────────────────

    public void setCallback(Callback cb) { this.callback = cb; }
    public Callback getCallback()        { return callback; }

    public void invalidateSelf() {
        if (callback != null) callback.invalidateDrawable(this);
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return getClass().getSimpleName()
             + "(bounds=" + bounds + ", visible=" + visible + ")";
    }
}
