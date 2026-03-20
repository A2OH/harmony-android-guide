package android.app;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Provides default Holo-Light-like visual appearance for widgets
 * that have no theme applied. Called by Activity.renderFrame() before
 * measure/layout/draw to ensure widgets have visible default drawables.
 *
 * Matches KitKat Holo Light rendering:
 * - Light gray (#F5F5F5) background
 * - Gray header text (#757575), dark body text (#212121)
 * - Dark gradient buttons with white centered text
 * - Small 24dp checkbox/radio icons left of text on same line
 * - Thin 4dp progress bar, thin 2dp seek track with round thumb
 * - Blue/gold filled stars, gray outlines for empty
 * - Blue underline-only EditText
 *
 * This is a bridge-layer file (not AOSP) -- safe to modify.
 */
public class DefaultTheme {

    // Holo Light colors
    public static final int COLOR_ACCENT = 0xFF33B5E5;       // Holo blue
    public static final int COLOR_BUTTON_DARK = 0xFF4A4A4A;   // Dark button bg (Holo)
    public static final int COLOR_BUTTON_PRESSED = 0xFFBBBBBB;
    public static final int COLOR_TEXT_PRIMARY = 0xFF212121;   // Near black (body text)
    public static final int COLOR_TEXT_SECONDARY = 0xFF757575; // Gray (headers)
    public static final int COLOR_TEXT_HINT = 0xFF9E9E9E;
    public static final int COLOR_DIVIDER = 0xFFBDBDBD;
    public static final int COLOR_BG = 0xFFF5F5F5;            // Off-white background
    public static final int COLOR_TRACK = 0xFFBDBDBD;          // Gray track
    public static final int COLOR_PROGRESS = 0xFF33B5E5;       // Blue fill
    public static final int COLOR_STAR_FILLED = 0xFFFFB400;    // Gold star
    public static final int COLOR_STAR_EMPTY = 0xFFBDBDBD;     // Gray star outline
    public static final int COLOR_SWITCH_ON = 0xFF33B5E5;      // Blue
    public static final int COLOR_SWITCH_OFF = 0xFFBDBDBD;     // Gray
    public static final int COLOR_TOGGLE_ON = 0xFF33B5E5;      // Blue for ON state
    public static final int COLOR_TOGGLE_OFF = 0xFF808080;     // Gray for OFF state

    // Tracks whether we already applied to a given view tree root
    // (to avoid re-applying every frame). Uses View.setTag().
    private static final Object TAG_THEMED = new Object();

    /**
     * Apply default theme to a view tree after inflation.
     * Walks the tree and sets default drawables for widgets that have none.
     * Safe to call multiple times -- skips views already themed.
     */
    public static void applyToViewTree(View root) {
        if (root == null) return;
        // Only apply once per root to avoid re-doing work each frame
        if (root.getTag() == TAG_THEMED) return;
        root.setTag(TAG_THEMED);

        // Set root background to Holo Light gray
        if (root.getBackground() == null) {
            root.setBackgroundColor(COLOR_BG);
        }

        applyRecursive(root);
    }

    private static void applyRecursive(View v) {
        applyToView(v);
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                applyRecursive(vg.getChildAt(i));
            }
        }
    }

    /**
     * Apply theme to an individual view based on its widget type.
     */
    public static void applyToView(View v) {
        // Apply text colors to all TextViews first (unless already colored)
        if (v instanceof TextView && !(v instanceof Button)
                && !(v instanceof CompoundButton) && !(v instanceof ToggleButton)
                && !(v instanceof EditText)) {
            applyTextViewTheme((TextView) v);
        }
        // Button (but not CompoundButton subclasses)
        if (v instanceof Button && !(v instanceof CompoundButton)
                && !(v instanceof ToggleButton) && v.getBackground() == null) {
            applyButtonTheme((Button) v);
        }
        // CheckBox
        if (v instanceof CheckBox) {
            applyCheckBoxTheme((CheckBox) v);
        }
        // RadioButton
        if (v instanceof RadioButton) {
            applyRadioButtonTheme((RadioButton) v);
        }
        // Switch
        if (v instanceof Switch) {
            applySwitchTheme((Switch) v);
        }
        // ToggleButton
        if (v instanceof ToggleButton && !(v instanceof Switch)) {
            applyToggleTheme((ToggleButton) v);
        }
        // EditText (but not AutoCompleteTextView)
        if (v instanceof EditText && !(v instanceof AutoCompleteTextView)
                && v.getBackground() == null) {
            applyEditTextTheme((EditText) v);
        }
        // ProgressBar (but not SeekBar or RatingBar)
        if (v instanceof ProgressBar && !(v instanceof SeekBar)
                && !(v instanceof RatingBar)) {
            applyProgressBarTheme((ProgressBar) v);
        }
        // SeekBar
        if (v instanceof SeekBar && !(v instanceof RatingBar)) {
            applySeekBarTheme((SeekBar) v);
        }
        // RatingBar
        if (v instanceof RatingBar) {
            applyRatingBarTheme((RatingBar) v);
        }
    }

    // ── TextView ────────────────────────────────────────────────────────

    private static void applyTextViewTheme(TextView tv) {
        if (tv.getCurrentTextColor() != 0) return; // already colored
        // Headers (18sp+) get gray, body text gets dark gray
        float textSize = tv.getTextSize();
        if (textSize >= 18.0f) {
            tv.setTextColor(COLOR_TEXT_SECONDARY); // #757575 gray for headers
        } else {
            tv.setTextColor(COLOR_TEXT_PRIMARY);   // #212121 dark for body
        }
    }

    // ── Button ──────────────────────────────────────────────────────────

    private static void applyButtonTheme(Button btn) {
        // Holo button: dark solid background, white centered text, rounded corners
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_BUTTON_DARK);   // Dark gray like Holo buttons
        bg.setCornerRadius(8);             // 4dp radius (slightly rounded)
        btn.setBackground(bg);
        btn.setPadding(24, 16, 24, 16);
        btn.setTextColor(0xFFFFFFFF);      // White text always
    }

    // ── CheckBox ────────────────────────────────────────────────────────

    private static void applyCheckBoxTheme(CheckBox cb) {
        if (cb.getButtonDrawable() != null) return;
        cb.setButtonDrawable(new CheckBoxDrawable());
        cb.setPadding(8, 0, 0, 0);
    }

    /**
     * Custom drawable that draws a checkbox: 24x24 box with optional checkmark.
     * Small icon size matches real Android KitKat Holo rendering.
     * Reads state from the drawable state set (state_checked).
     */
    static class CheckBoxDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path mCheckPath = new Path();
        private boolean mChecked;

        public int getIntrinsicWidth() { return 36; }
        public int getIntrinsicHeight() { return 36; }

        public boolean isStateful() { return true; }

        protected boolean onStateChange(int[] stateSet) {
            boolean wasChecked = mChecked;
            mChecked = hasState(stateSet, android.R.attr.state_checked);
            return mChecked != wasChecked;
        }

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            int cx = b.centerX();
            int cy = b.centerY();
            // 24dp icon inside the bounds
            int half = 12;

            if (mChecked) {
                // Filled blue rounded box
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(COLOR_ACCENT);
                RectF box = new RectF(cx - half, cy - half, cx + half, cy + half);
                canvas.drawRoundRect(box, 4, 4, mPaint);

                // White checkmark lines drawn as individual line segments
                mPaint.setColor(0xFFFFFFFF);
                mPaint.setStrokeWidth(3);
                // Left segment of checkmark
                canvas.drawLine(
                    cx - half * 0.45f, cy,
                    cx - half * 0.05f, cy + half * 0.4f, mPaint);
                // Right segment of checkmark
                canvas.drawLine(
                    cx - half * 0.05f, cy + half * 0.4f,
                    cx + half * 0.5f, cy - half * 0.35f, mPaint);
            } else {
                // Empty gray outline box
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(COLOR_DIVIDER);
                RectF outerBox = new RectF(cx - half, cy - half, cx + half, cy + half);
                canvas.drawRoundRect(outerBox, 4, 4, mPaint);
                // Inner white fill to create outline appearance
                mPaint.setColor(0xFFFFFFFF);
                int inset = 3;
                RectF innerBox = new RectF(cx - half + inset, cy - half + inset,
                    cx + half - inset, cy + half - inset);
                canvas.drawRoundRect(innerBox, 2, 2, mPaint);
            }
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    // ── RadioButton ─────────────────────────────────────────────────────

    private static void applyRadioButtonTheme(RadioButton rb) {
        if (rb.getButtonDrawable() != null) return;
        rb.setButtonDrawable(new RadioButtonDrawable());
        rb.setPadding(8, 0, 0, 0);
    }

    /**
     * Custom drawable that draws a radio button: 24dp circle with optional filled dot.
     * Small icon size matches real Android KitKat Holo rendering.
     */
    static class RadioButtonDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean mChecked;

        public int getIntrinsicWidth() { return 36; }
        public int getIntrinsicHeight() { return 36; }

        public boolean isStateful() { return true; }

        protected boolean onStateChange(int[] stateSet) {
            boolean wasChecked = mChecked;
            mChecked = hasState(stateSet, android.R.attr.state_checked);
            return mChecked != wasChecked;
        }

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            int cx = b.centerX();
            int cy = b.centerY();
            // 24dp circle
            int radius = 12;

            if (mChecked) {
                // Outer blue circle (filled, then white inner to make ring)
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(COLOR_ACCENT);
                canvas.drawCircle(cx, cy, radius, mPaint);
                // White ring gap
                mPaint.setColor(0xFFFFFFFF);
                canvas.drawCircle(cx, cy, radius - 3, mPaint);
                // Inner blue filled dot
                mPaint.setColor(COLOR_ACCENT);
                canvas.drawCircle(cx, cy, radius * 0.45f, mPaint);
            } else {
                // Gray outer circle (filled, then white inner to make ring)
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(COLOR_DIVIDER);
                canvas.drawCircle(cx, cy, radius, mPaint);
                mPaint.setColor(0xFFFFFFFF);
                canvas.drawCircle(cx, cy, radius - 3, mPaint);
            }
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    // ── Switch ──────────────────────────────────────────────────────────

    private static void applySwitchTheme(Switch sw) {
        // Switch uses track and thumb drawables via its own setTrackDrawable/setThumbDrawable
        // Since those are AOSP code, we set them via the public API
        if (sw.getTrackDrawable() == null) {
            sw.setTrackDrawable(new SwitchTrackDrawable());
        }
        if (sw.getThumbDrawable() == null) {
            sw.setThumbDrawable(new SwitchThumbDrawable());
        }
    }

    static class SwitchTrackDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean mChecked;

        public int getIntrinsicWidth() { return 80; }
        public int getIntrinsicHeight() { return 32; }
        public boolean isStateful() { return true; }

        protected boolean onStateChange(int[] stateSet) {
            boolean was = mChecked;
            mChecked = hasState(stateSet, android.R.attr.state_checked);
            return mChecked != was;
        }

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            float r = b.height() / 2.0f;

            // Track background
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mChecked ? 0x8033B5E5 : 0x80BDBDBD);
            canvas.drawRoundRect(new RectF(b.left, b.top, b.right, b.bottom), r, r, mPaint);

            // Draw OFF/ON text inside track
            mPaint.setColor(mChecked ? 0xFFFFFFFF : 0xFF757575);
            mPaint.setTextSize(11);
            float textY = b.centerY() + 4;
            if (mChecked) {
                // "ON" on left side
                canvas.drawText("ON", b.left + 8, textY, mPaint);
            } else {
                // "OFF" on right side
                canvas.drawText("OFF", b.right - 28, textY, mPaint);
            }
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    static class SwitchThumbDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean mChecked;

        public int getIntrinsicWidth() { return 36; }
        public int getIntrinsicHeight() { return 36; }
        public boolean isStateful() { return true; }

        protected boolean onStateChange(int[] stateSet) {
            boolean was = mChecked;
            mChecked = hasState(stateSet, android.R.attr.state_checked);
            return mChecked != was;
        }

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            int cx = b.centerX();
            int cy = b.centerY();
            int radius = Math.min(b.width(), b.height()) / 2 - 2;

            // Shadow
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(0x40000000);
            canvas.drawCircle(cx + 1, cy + 2, radius, mPaint);

            // Thumb circle
            mPaint.setColor(mChecked ? COLOR_SWITCH_ON : 0xFFFAFAFA);
            canvas.drawCircle(cx, cy, radius, mPaint);

            // Border (draw as slightly larger circle behind, but since renderer
            // only does fill, simulate border with darker outer then lighter inner)
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    // ── ToggleButton ────────────────────────────────────────────────────

    private static void applyToggleTheme(ToggleButton tb) {
        if (tb.getBackground() != null) return;
        // Holo toggle: distinct ON (blue) / OFF (gray) visual
        // Start with OFF state appearance
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_TOGGLE_OFF);
        bg.setCornerRadius(8);
        tb.setBackground(bg);
        tb.setPadding(24, 16, 24, 16);
        if (tb.getTextOn() == null) tb.setTextOn("ON");
        if (tb.getTextOff() == null) tb.setTextOff("OFF");
        tb.setTextColor(0xFFFFFFFF); // White text on dark bg
    }

    // ── EditText ────────────────────────────────────────────────────────

    private static void applyEditTextTheme(EditText et) {
        // EditText Holo look: thin blue underline at bottom ONLY
        et.setBackground(new EditTextUnderlineDrawable());
        et.setPadding(4, 8, 4, 12);
        if (et.getCurrentTextColor() == 0) {
            et.setTextColor(COLOR_TEXT_PRIMARY);
        }
    }

    static class EditTextUnderlineDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            // Thin blue underline - 2px thick at very bottom
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(COLOR_ACCENT);
            canvas.drawRect(b.left, b.bottom - 2, b.right, b.bottom, mPaint);
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    // ── ProgressBar ─────────────────────────────────────────────────────

    private static void applyProgressBarTheme(ProgressBar pb) {
        if (pb.getProgressDrawable() != null) return;

        // Background track (thin gray line, 4dp tall)
        GradientDrawable trackDrawable = new GradientDrawable();
        trackDrawable.setColor(COLOR_TRACK);
        trackDrawable.setSize(-1, 4);
        trackDrawable.setCornerRadius(2);

        // Progress fill (thin blue line, 4dp tall)
        GradientDrawable progressFill = new GradientDrawable();
        progressFill.setColor(COLOR_PROGRESS);
        progressFill.setSize(-1, 4);
        progressFill.setCornerRadius(2);
        ClipDrawable progressClip = new ClipDrawable(
                progressFill, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        // Build LayerDrawable: [0]=background, [1]=secondaryProgress, [2]=progress
        LayerDrawable layers = new LayerDrawable(new Drawable[]{
                trackDrawable, progressClip, progressClip});
        layers.setId(0, android.R.id.background);
        layers.setId(1, android.R.id.secondaryProgress);
        layers.setId(2, android.R.id.progress);
        pb.setProgressDrawable(layers);

        // Thin progress bar height
        if (pb.getMinimumHeight() <= 0) {
            pb.setMinimumHeight(4);
        }
    }

    // ── SeekBar ─────────────────────────────────────────────────────────

    private static void applySeekBarTheme(SeekBar sb) {
        if (sb.getProgressDrawable() != null && sb.getThumb() != null) return;

        if (sb.getProgressDrawable() == null) {
            // Track (very thin 2dp gray line)
            GradientDrawable trackDrawable = new GradientDrawable();
            trackDrawable.setColor(COLOR_TRACK);
            trackDrawable.setSize(-1, 2);
            trackDrawable.setCornerRadius(1);

            // Progress (very thin 2dp blue line)
            GradientDrawable progressFill = new GradientDrawable();
            progressFill.setColor(COLOR_PROGRESS);
            progressFill.setSize(-1, 2);
            progressFill.setCornerRadius(1);
            ClipDrawable progressClip = new ClipDrawable(
                    progressFill, Gravity.LEFT, ClipDrawable.HORIZONTAL);

            LayerDrawable layers = new LayerDrawable(new Drawable[]{
                    trackDrawable, progressClip, progressClip});
            layers.setId(0, android.R.id.background);
            layers.setId(1, android.R.id.secondaryProgress);
            layers.setId(2, android.R.id.progress);
            sb.setProgressDrawable(layers);
        }

        if (sb.getThumb() == null) {
            // Thumb: 24dp round blue circle
            sb.setThumb(new SeekBarThumbDrawable());
        }
    }

    static class SeekBarThumbDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public int getIntrinsicWidth() { return 24; }
        public int getIntrinsicHeight() { return 24; }

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            int cx = b.centerX();
            int cy = b.centerY();
            int radius = Math.min(b.width(), b.height()) / 2 - 1;

            // Shadow
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(0x30000000);
            canvas.drawCircle(cx + 1, cy + 1, radius, mPaint);

            // Blue circle
            mPaint.setColor(COLOR_ACCENT);
            canvas.drawCircle(cx, cy, radius, mPaint);
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    // ── RatingBar ───────────────────────────────────────────────────────

    private static void applyRatingBarTheme(RatingBar rb) {
        if (rb.getProgressDrawable() != null) return;

        int numStars = rb.getNumStars();
        if (numStars <= 0) numStars = 5;

        // Create a star drawable for the rating bar
        int starSize = 36; // Smaller stars matching Holo

        // Background: empty stars (gray outlines)
        StarDrawable bgStars = new StarDrawable(numStars, starSize, false);
        // Progress: filled stars (gold, clipped by ClipDrawable)
        StarDrawable filledStars = new StarDrawable(numStars, starSize, true);
        ClipDrawable progressClip = new ClipDrawable(
                filledStars, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        LayerDrawable layers = new LayerDrawable(new Drawable[]{
                bgStars, progressClip, progressClip});
        layers.setId(0, android.R.id.background);
        layers.setId(1, android.R.id.secondaryProgress);
        layers.setId(2, android.R.id.progress);
        rb.setProgressDrawable(layers);
    }

    /**
     * Draws a row of star shapes for the RatingBar.
     * Uses drawLine segments to approximate star outlines since drawPath
     * is not supported by the PixelRenderer.
     */
    static class StarDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int mNumStars;
        private final int mStarSize;
        private final boolean mFilled;

        StarDrawable(int numStars, int starSize, boolean filled) {
            mNumStars = numStars;
            mStarSize = starSize;
            mFilled = filled;
        }

        public int getIntrinsicWidth() { return mStarSize * mNumStars; }
        public int getIntrinsicHeight() { return mStarSize; }

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            float cellW = (float) b.width() / mNumStars;
            float cellH = b.height();
            float size = Math.min(cellW, cellH) * 0.8f;

            for (int i = 0; i < mNumStars; i++) {
                float cx = b.left + cellW * i + cellW / 2;
                float cy = b.top + cellH / 2;
                drawStar(canvas, cx, cy, size / 2);
            }
        }

        private void drawStar(Canvas canvas, float cx, float cy, float outerR) {
            float innerR = outerR * 0.4f;

            // Compute the 10 vertices of the star
            float[] xPts = new float[10];
            float[] yPts = new float[10];
            for (int i = 0; i < 10; i++) {
                double angle = Math.PI * i / 5.0 - Math.PI / 2.0;
                float r = (i % 2 == 0) ? outerR : innerR;
                xPts[i] = cx + (float)(r * Math.cos(angle));
                yPts[i] = cy + (float)(r * Math.sin(angle));
            }

            if (mFilled) {
                // Fill: draw as a circle approximation since drawPath not rendered
                // Use filled circle at ~70% outer radius as gold fill approximation
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(COLOR_STAR_FILLED);
                canvas.drawCircle(cx, cy, outerR * 0.65f, mPaint);
                // Draw points as small circles at the outer tips
                for (int i = 0; i < 10; i += 2) {
                    canvas.drawCircle(xPts[i], yPts[i], outerR * 0.15f, mPaint);
                }
            } else {
                // Empty star outline: draw as line segments between vertices
                mPaint.setColor(COLOR_STAR_EMPTY);
                mPaint.setStrokeWidth(2);
                for (int i = 0; i < 10; i++) {
                    int next = (i + 1) % 10;
                    canvas.drawLine(xPts[i], yPts[i], xPts[next], yPts[next], mPaint);
                }
            }
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    // ── Utility ─────────────────────────────────────────────────────────

    /**
     * Check if a state set contains a given state attribute.
     * Used by custom drawables to detect checked/pressed/etc. states
     * from the drawable state set passed via onStateChange().
     */
    private static boolean hasState(int[] stateSet, int state) {
        if (stateSet == null) return false;
        for (int i = 0; i < stateSet.length; i++) {
            if (stateSet[i] == state) return true;
        }
        return false;
    }
}
