package android.text;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.EditText;
import android.widget.Filter;

/**
 * Android-compatible InputFilter shim.
 * Interface for filtering input as it is typed into an EditText.
 */
public interface InputFilter {

    /**
     * Called when the buffer is going to replace the range
     * {@code dstart} to {@code dend} of {@code dest} with the new text
     * from the range {@code start} to {@code end} of {@code source}.
     *
     * @return the CharSequence to use in place of source, or null to accept
     *         the original replacement
     */
    CharSequence filter(CharSequence source, int start, int end,
                        Spanned dest, int dstart, int dend);

    /**
     * Filter that constrains the maximum length of the text.
     */
    public static class LengthFilter implements InputFilter {
        private final int mMax;

        public LengthFilter(int max) {
            mMax = max;
        }

        public int getMax() {
            return mMax;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            int keep = mMax - (dest.length() - (dend - dstart));
            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                return null; // keep original
            } else {
                keep += start;
                return source.subSequence(start, keep);
            }
        }
    }

    /**
     * Filter that converts all lower-case characters to upper case.
     */
    public static class AllCaps implements InputFilter {

        public AllCaps() {
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (Character.isLowerCase(source.charAt(i))) {
                    char[] v = new char[end - start];
                    for (int j = start; j < end; j++) {
                        v[j - start] = Character.toUpperCase(source.charAt(j));
                    }
                    return new String(v);
                }
            }
            return null; // no change needed
        }
    }
}
