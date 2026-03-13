package android.graphics.text;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;

import android.graphics.Rect;

/**
 * Shim: android.graphics.text.MeasuredText (API 29+)
 * Pre-computed text measurement data for efficient layout.
 *
 * Pure Java stub — stores the source text; measurement ops return defaults.
 */
public class MeasuredText {

    private final char[] mChars;

    // ── Private constructor — use Builder ──────────────────────────────────

    private MeasuredText(char[] chars) {
        mChars = chars;
    }

    // ── Public API ─────────────────────────────────────────────────────────

    /** Returns the characters of the measured text as a new array. */
    public char[] getChars() {
        return mChars.clone();
    }

    /** Stub: returns 0f for the width of the given range. */
    public float getWidth(int start, int end) {
        return 0f;
    }

    /** Stub: fills {@code bounds} with zeros for the given range. */
    public void getBounds(int start, int end, Rect bounds) {
        if (bounds != null) {
            bounds.left   = 0;
            bounds.top    = 0;
            bounds.right  = 0;
            bounds.bottom = 0;
        }
    }

    // ── Builder ────────────────────────────────────────────────────────────

    /**
     * Builder for creating a {@link MeasuredText}.
     */
    public static class Builder {

        private final CharSequence mText;
        @SuppressWarnings("unused") private boolean mComputeHyphenation;
        @SuppressWarnings("unused") private boolean mComputeLayout = true;

        public Builder(CharSequence text) {
            if (text == null) {
                throw new IllegalArgumentException("text must not be null");
            }
            mText = text;
        }

        /**
         * Stub: records a style run; paint type is Object to avoid hard
         * dependency on android.graphics.Paint at compile time.
         */
        public Builder appendStyleRun(Object paint, int length, boolean isRtl) {
            return this;
        }

        /**
         * Stub: records a replacement run with an explicit width.
         */
        public Builder appendReplacementRun(Object paint, int length, float width) {
            return this;
        }

        /** Stub: flag for hyphenation computation. */
        public Builder setComputeHyphenation(boolean computeHyphenation) {
            mComputeHyphenation = computeHyphenation;
            return this;
        }

        /** Stub: flag for layout computation. */
        public Builder setComputeLayout(boolean computeLayout) {
            mComputeLayout = computeLayout;
            return this;
        }

        /** Build the {@link MeasuredText} instance. */
        public MeasuredText build() {
            char[] chars = new char[mText.length()];
            for (int i = 0; i < chars.length; i++) {
                chars[i] = mText.charAt(i);
            }
            return new MeasuredText(chars);
        }
    }
}
