package android.view.inputmethod;

/**
 * Android-compatible CursorAnchorInfo shim.
 * Positional information about the text insertion po(int and composing text.
 */
public class CursorAnchorInfo {
    public static final int FLAG_HAS_VISIBLE_REGION = 0x01;
    public static final int FLAG_HAS_INVISIBLE_REGION = 0x02;
    public static final int FLAG_IS_RTL = 0x04;
    private int mSelectionStart;
    private int mSelectionEnd;
    private int mComposingTextStart;
    private float mInsertionMarkerBaseline;
    private float mInsertionMarkerTop;
    private float mInsertionMarkerBottom;

    private CursorAnchorInfo(Builder builder) {
        mSelectionStart = builder.mSelectionStart;
        mSelectionEnd = builder.mSelectionEnd;
        mComposingTextStart = builder.mComposingTextStart;
        mInsertionMarkerBaseline = builder.mInsertionMarkerBaseline;
        mInsertionMarkerTop = builder.mInsertionMarkerTop;
        mInsertionMarkerBottom = builder.mInsertionMarkerBottom;
    }

    public int getSelectionStart() {
        return mSelectionStart;
    }

    public int getSelectionEnd() {
        return mSelectionEnd;
    }

    public int getComposingTextStart() {
        return mComposingTextStart;
    }

    public float getInsertionMarkerBaseline() {
        return mInsertionMarkerBaseline;
    }

    public float getInsertionMarkerTop() {
        return mInsertionMarkerTop;
    }

    public float getInsertionMarkerBottom() {
        return mInsertionMarkerBottom;
    }

    public android.graphics.RectF getCharacterBounds(int index) { return null; }
    public int getCharacterBoundsFlags(int index) { return 0; }

    public static final class Builder {
        private int mSelectionStart;
        private int mSelectionEnd;
        private int mComposingTextStart = -1;
        private float mInsertionMarkerBaseline;
        private float mInsertionMarkerTop;
        private float mInsertionMarkerBottom;

        public Builder() {}

        public Builder reset() {
            mSelectionStart = 0;
            mSelectionEnd = 0;
            mComposingTextStart = -1;
            return this;
        }

        public Builder setSelectionRange(int newStart, int newEnd) {
            mSelectionStart = newStart;
            mSelectionEnd = newEnd;
            return this;
        }

        public Builder setComposingText(int composingTextStart, CharSequence composingText) {
            mComposingTextStart = composingTextStart;
            return this;
        }

        public Builder setInsertionMarkerLocation(float horizontalPosition,
                float lineTop, float lineBaseline, float lineBottom, int flags) {
            mInsertionMarkerTop = lineTop;
            mInsertionMarkerBaseline = lineBaseline;
            mInsertionMarkerBottom = lineBottom;
            return this;
        }

        public Builder addCharacterBounds(int index, float left, float top, float right, float bottom, int flags) {
            return this;
        }

        public Builder setMatrix(android.graphics.Matrix matrix) { return this; }

        public CursorAnchorInfo build() {
            return new CursorAnchorInfo(this);
        }
    }
}
