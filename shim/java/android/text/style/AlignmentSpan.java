package android.text.style;
import android.text.Layout;
import android.text.ParcelableSpan;

/**
 * Android-compatible AlignmentSpan stub.
 */
public interface AlignmentSpan {

    /**
     * Returns the alignment (Layout.Alignment) for the text.
     */
    Layout.Alignment getAlignment();

    /**
     * Standard implementation of AlignmentSpan.
     */
    class Standard implements AlignmentSpan, ParcelableSpan {
        private final Layout.Alignment mAlignment;

        public Standard(Layout.Alignment alignment) {
            mAlignment = alignment;
        }

        @Override
        public Layout.Alignment getAlignment() {
            return mAlignment;
        }

        @Override
        public int getSpanTypeId() {
            return 0;
        }

        public void writeToParcel(Object dest, int flags) {
            // no-op stub
        }
    }
}
