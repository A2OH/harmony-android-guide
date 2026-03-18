package android.widget;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/**
 * Shim: android.widget.AbsSeekBar — abstract base for seek-bar style widgets.
 *
 * Extends {@link ProgressBar} and adds thumb-drawable support. Concrete subclasses
 * (SeekBar, RatingBar) provide interaction logic on top of this class.
 *
 * Note: the existing {@link SeekBar} shim extends View directly for ArkUI
 * integration; this class provides the correct Android class hierarchy for
 * code that references AbsSeekBar explicitly (e.g. custom seek bar subclasses).
 */
public class AbsSeekBar extends ProgressBar {

    // ── State ────────────────────────────────────────────────────────────────

    private Drawable thumbDrawable;
    private int      thumbOffset = 0;
    private int      keyProgressIncrement = 1;

    // ── Constructor ──────────────────────────────────────────────────────────

    public AbsSeekBar(android.content.Context context) {
        super(context);
    }
    public AbsSeekBar(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }
    public AbsSeekBar(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public AbsSeekBar(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // ── Thumb drawable ───────────────────────────────────────────────────────

    /**
     * Sets the thumb drawable used to display the seek position.
     *
     * @param thumb a Drawable representing the draggable thumb, or null to clear
     */
    public void setThumb(Drawable thumb) {
        this.thumbDrawable = thumb;
    }

    /**
     * Returns the thumb drawable, or null if none has been set.
     *
     * @return the thumb Drawable
     */
    public Drawable getThumb() {
        return thumbDrawable;
    }

    // ── Thumb offset ─────────────────────────────────────────────────────────

    /**
     * Sets the thumb offset that allows the thumb to extend out of the track.
     *
     * @param thumbOffset offset in pixels
     */
    public void setThumbOffset(int thumbOffset) {
        this.thumbOffset = thumbOffset;
    }

    /**
     * Returns the current thumb offset in pixels.
     *
     * @return thumb offset in pixels
     */
    public int getThumbOffset() {
        return thumbOffset;
    }

    // ── Key increment ────────────────────────────────────────────────────────

    /**
     * Sets the amount of progress changed via the arrow keys.
     *
     * @param increment the amount by which progress changes for each key event
     */
    public void setKeyProgressIncrement(int increment) {
        this.keyProgressIncrement = Math.abs(increment);
    }

    /**
     * Returns the amount of progress changed via the arrow keys.
     *
     * @return the key progress increment
     */
    public int getKeyProgressIncrement() {
        return keyProgressIncrement;
    }

    // ── Thumb t(int ───────────────────────────────────────────────────────────

    /**
     * Applies a t(int color to the thumb drawable. No-op in this shim.
     *
     * @param t(int the t(int color as an ARGB int
     */
    public void setThumbTintList(Object tint) {
        // no-op: ColorStateList tinting not implemented in shim
    }

    /**
     * Returns the t(int applied to the thumb, always null in this shim.
     *
     * @return null
     */
    public Object getThumbTintList() {
        return null;
    }
}
