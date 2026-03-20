package android.text;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Stub: TextLine — represents a single line of text for rendering.
 * Used by Layout.draw() for complex text rendering (bidi, tabs, spans).
 */
class TextLine {
    private static final TextLine[] sCached = new TextLine[3];

    private TextPaint mPaint;
    private CharSequence mText;
    private int mStart;
    private int mEnd;
    private int mDir;
    private Layout.Directions mDirections;
    private boolean mHasTabs;
    private Layout.TabStops mTabs;
    private int mEllipsisStart;
    private int mEllipsisEnd;

    public static TextLine obtain() {
        synchronized (sCached) {
            for (int i = 0; i < sCached.length; i++) {
                if (sCached[i] != null) {
                    TextLine tl = sCached[i];
                    sCached[i] = null;
                    return tl;
                }
            }
        }
        return new TextLine();
    }

    public static void recycle(TextLine tl) {
        tl.mPaint = null;
        tl.mText = null;
        synchronized (sCached) {
            for (int i = 0; i < sCached.length; i++) {
                if (sCached[i] == null) {
                    sCached[i] = tl;
                    return;
                }
            }
        }
    }

    public void set(TextPaint paint, CharSequence text, int start, int end,
            int dir, Layout.Directions directions, boolean hasTabs,
            Layout.TabStops tabStops, int ellipsisStart, int ellipsisEnd) {
        mPaint = paint;
        mText = text;
        mStart = start;
        mEnd = end;
        mDir = dir;
        mDirections = directions;
        mHasTabs = hasTabs;
        mTabs = tabStops;
        mEllipsisStart = ellipsisStart;
        mEllipsisEnd = ellipsisEnd;
    }

    public void draw(Canvas c, float x, int top, int y, int bottom) {
        if (mText == null || mPaint == null) return;
        String sub = mText.toString().substring(mStart, mEnd);
        c.drawText(sub, x, (float) y, mPaint);
    }

    /**
     * Measures the width of the text line.
     */
    public float metrics(Paint.FontMetricsInt fmi) {
        if (mText == null || mPaint == null) return 0;
        String sub = mText.toString().substring(mStart, mEnd);
        if (fmi != null) {
            mPaint.getFontMetricsInt(fmi);
        }
        return mPaint.measureText(sub);
    }

    /**
     * Measures a range of this text line.
     */
    public float measure(int offset, boolean trailing, Paint.FontMetricsInt fmi) {
        if (mText == null || mPaint == null) return 0;
        int clampedOffset = Math.max(mStart, Math.min(offset, mEnd));
        String sub = mText.toString().substring(mStart, clampedOffset);
        return mPaint.measureText(sub);
    }

    public void justify(float justifyWidth) {
        // no-op in stub
    }

    public int getOffsetToLeftRightOf(int cursor, boolean toLeft) {
        return cursor;
    }

    public int getOffsetForAdvance(int lineStart, int lineEnd, int dir, float advance) {
        return lineStart;
    }

    public float[] measureAllOffsets(boolean[] trailing, Paint.FontMetricsInt fmi) {
        int len = mEnd - mStart + 1;
        float[] offsets = new float[len];
        if (mText != null && mPaint != null) {
            String sub = mText.toString().substring(mStart, mEnd);
            for (int i = 0; i < sub.length(); i++) {
                offsets[i + 1] = mPaint.measureText(sub.substring(0, i + 1));
            }
        }
        return offsets;
    }

    public static boolean isLineEndSpace(char c) {
        return c == ' ' || c == '\t' || c == 0x1680
                || (0x2000 <= c && c <= 0x200A && c != 0x2007)
                || c == 0x205F || c == 0x3000;
    }
}
