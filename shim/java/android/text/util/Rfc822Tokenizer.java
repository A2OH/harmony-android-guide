package android.text.util;

import android.widget.MultiAutoCompleteTextView;

/**
 * Android-compatible Rfc822Tokenizer shim.
 * Tokenizer for RFC-822 (email) address lists, suitable for use with
 * MultiAutoCompleteTextView.
 */
public class Rfc822Tokenizer implements MultiAutoCompleteTextView.Tokenizer {

    /**
     * Splits the specified text into an array of RFC-822 tokens.
     * Returns an empty array in this stub.
     */
    public static String[] tokenize(CharSequence text) {
        return new String[0];
    }

    @Override
    public int findTokenStart(CharSequence text, int cursor) {
        int i = cursor;
        while (i > 0 && text.charAt(i - 1) != ',') {
            i--;
        }
        while (i < cursor && text.charAt(i) == ' ') {
            i++;
        }
        return i;
    }

    @Override
    public int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;
        int len = text.length();
        while (i < len) {
            if (text.charAt(i) == ',') {
                return i;
            }
            i++;
        }
        return len;
    }

    @Override
    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();
        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }
        if (i > 0 && text.charAt(i - 1) == ',') {
            return text;
        }
        if (text instanceof android.text.Spanned) {
            android.text.SpannableString sp = new android.text.SpannableString(text + ", ");
            android.text.TextUtils.copySpansFrom((android.text.Spanned) text, 0, text.length(),
                    Object.class, sp, 0);
            return sp;
        }
        return text + ", ";
    }
}
