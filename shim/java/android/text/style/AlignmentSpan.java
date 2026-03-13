package android.text.style;
import android.text.Layout;
import android.text.ParcelableSpan;
import android.text.Layout;
import android.text.ParcelableSpan;

import android.text.ParcelableSpan;

/**
 * Android-compatible AlignmentSpan stub.
 * A2OH shim layer — delegates to OpenHarmony at runtime.
 */
public interface AlignmentSpan {

    /**
     * Returns the alignment (Layout.Alignment) for the text.
     */
    Object getAlignment();

    /**
     * Standard implementation of AlignmentSpan.
     */
    class Standard implements AlignmentSpan, ParcelableSpan {
        private final Object mAlignment;

        public Standard(Object alignment) {
            mAlignment = alignment;
        }

        @Override
        public Object getAlignment() {
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
