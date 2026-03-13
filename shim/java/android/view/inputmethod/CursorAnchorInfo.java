package android.view.inputmethod;

/**
 * Android-compatible CursorAnchorInfo shim.
 * Positional information about the text insertion po(int and composing text.
 */
public class CursorAnchorInfo {
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

    public static final class Builder {
        private int mSelectionStart;
        private int mSelectionEnd;
        private int mComposingTextStart = -1;
        private float mInsertionMarkerBaseline;
        private float mInsertionMarkerTop;
        private float mInsertionMarkerBottom;

        public Builder() {}

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

        public CursorAnchorInfo build() {
            return new CursorAnchorInfo(this);
        }
    }
}
