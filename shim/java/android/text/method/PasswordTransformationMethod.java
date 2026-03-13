package android.text.method;
import android.view.View;
import android.view.View;

/**
 * Android-compatible PasswordTransformationMethod stub.
 * Replaces every character in the source text with a bullet/dot for password masking.
 */
public class PasswordTransformationMethod implements TransformationMethod {

    private static final char DOT = '\u2022'; // bullet character used by Android

    private static PasswordTransformationMethod sInstance;

    private PasswordTransformationMethod() {}

    /**
     * Returns the singleton instance of PasswordTransformationMethod.
     *
     * @return the singleton instance
     */
    public static PasswordTransformationMethod getInstance() {
        if (sInstance == null) {
            sInstance = new PasswordTransformationMethod();
        }
        return sInstance;
    }

    /**
     * Returns a CharSequence where every character is replaced with a dot,
     * matching Android's password masking behaviour.
     *
     * @param source the original text
     * @param view   the View displaying the text (typed as Object for compatibility)
     * @return a same-length CharSequence of dot characters
     */
    @Override
    public CharSequence getTransformation(CharSequence source, Object view) {
        if (source == null) {
            return null;
        }
        int len = source.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(DOT);
        }
        return sb.toString();
    }

    /**
     * No-op implementation — password fields do not change masking on focus.
     */
    @Override
    public void onFocusChanged(Object view, CharSequence sourceText, boolean focused,
            int direction, Object previouslyFocusedRect) {
        // no-op
    }
}
