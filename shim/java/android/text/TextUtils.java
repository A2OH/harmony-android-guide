package android.text;

/**
 * Shim: android.text.TextUtils
 *
 * Static utility methods for working with strings and CharSequences.
 * Pure Java — no OHBridge calls needed; these are string algorithms that
 * run entirely on the JVM/ART side.
 */
public final class TextUtils {

    private TextUtils() {}

    // ── isEmpty / isWhitespace ────────────────────────────────────────

    /**
     * Returns {@code true} if {@code str} is null or has zero length.
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns the trimmed length of {@code s}: the length after stripping
     * leading and trailing ASCII whitespace.
     */
    public static int getTrimmedLength(CharSequence s) {
        if (s == null) return 0;
        int len = s.length();
        int start = 0;
        while (start < len && s.charAt(start) <= ' ') start++;
        while (len > start && s.charAt(len - 1) <= ' ') len--;
        return len - start;
    }

    // ── equals ────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code a} and {@code b} are equal as character
     * sequences, or both null.
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        int length = a.length();
        if (length != b.length()) return false;
        if (a instanceof String && b instanceof String) {
            return a.equals(b);
        }
        for (int i = 0; i < length; i++) {
            if (a.charAt(i) != b.charAt(i)) return false;
        }
        return true;
    }

    // ── join ──────────────────────────────────────────────────────────

    /**
     * Joins the elements of {@code tokens} with {@code delimiter}.
     */
    public static String join(CharSequence delimiter, CharSequence[] tokens) {
        if (tokens == null || tokens.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            if (i > 0) sb.append(delimiter);
            sb.append(tokens[i]);
        }
        return sb.toString();
    }

    /**
     * Joins the elements of {@code tokens} with {@code delimiter}.
     */
    public static String join(CharSequence delimiter, Iterable<?> tokens) {
        if (tokens == null) return "";
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object token : tokens) {
            if (!first) sb.append(delimiter);
            sb.append(token);
            first = false;
        }
        return sb.toString();
    }

    // ── split ─────────────────────────────────────────────────────────

    /**
     * Splits a string on a regex, returning an empty array instead of a
     * single-element array containing the empty string.
     */
    public static String[] split(String text, String expression) {
        if (text == null) return new String[0];
        if (text.length() == 0) return new String[0];
        return text.split(expression, -1);
    }

    // ── htmlEncode ────────────────────────────────────────────────────

    /**
     * HTML-encodes the string, replacing {@code &}, {@code <}, {@code >},
     * {@code "}, and {@code '} with their entity equivalents.
     */
    public static String htmlEncode(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&':  sb.append("&amp;");  break;
                case '<':  sb.append("&lt;");   break;
                case '>':  sb.append("&gt;");   break;
                case '"':  sb.append("&quot;"); break;
                case '\'': sb.append("&#39;");  break;
                default:   sb.append(c);        break;
            }
        }
        return sb.toString();
    }

    // ── isDigitsOnly ──────────────────────────────────────────────────

    /**
     * Returns {@code true} if every character in {@code str} is a decimal
     * ASCII digit (0–9). Returns {@code false} for empty strings.
     */
    public static boolean isDigitsOnly(CharSequence str) {
        if (isEmpty(str)) return false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

    // ── isGraphic ─────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the string contains at least one non-space,
     * non-control printable character.
     */
    public static boolean isGraphic(CharSequence str) {
        if (isEmpty(str)) return false;
        for (int i = 0; i < str.length(); i++) {
            int gc = Character.getType(str.charAt(i));
            if (gc != Character.CONTROL
                    && gc != Character.SPACE_SEPARATOR
                    && gc != Character.LINE_SEPARATOR
                    && gc != Character.PARAGRAPH_SEPARATOR
                    && gc != Character.UNASSIGNED
                    && gc != Character.FORMAT) {
                return true;
            }
        }
        return false;
    }

    // ── concat ────────────────────────────────────────────────────────

    /**
     * Returns a new CharSequence that is the concatenation of {@code a}
     * and {@code b}. If both are Spanned the result is a SpannableString
     * preserving all spans; otherwise a plain String is returned.
     */
    public static CharSequence concat(CharSequence... text) {
        if (text == null || text.length == 0) return "";
        if (text.length == 1) return text[0] != null ? text[0] : "";

        // Check if any segment is Spanned
        boolean anySpanned = false;
        for (CharSequence s : text) {
            if (s instanceof Spanned) { anySpanned = true; break; }
        }

        if (!anySpanned) {
            StringBuilder sb = new StringBuilder();
            for (CharSequence s : text) { if (s != null) sb.append(s); }
            return sb.toString();
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (CharSequence s : text) { if (s != null) ssb.append(s); }
        return ssb;
    }

    // ── copySpansFrom ─────────────────────────────────────────────────

    /**
     * Copies all spans of type {@code kind} from {@code source} to
     * {@code dest}, adjusting offsets so that source[sourceStart..sourceEnd)
     * maps to dest[destStart..).
     */
    public static <T> void copySpansFrom(Spanned source, int start, int end,
                                          Class<T> kind, Spannable dest, int destOff) {
        T[] spans = source.getSpans(start, end, kind);
        for (T span : spans) {
            int ss = source.getSpanStart(span);
            int se = source.getSpanEnd(span);
            int sf = source.getSpanFlags(span);
            ss = Math.max(ss, start) - start + destOff;
            se = Math.min(se, end)   - start + destOff;
            dest.setSpan(span, ss, se, sf);
        }
    }

    // ── regionMatches ─────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code one[toffset, toffset+len)} equals
     * {@code two[ooffset, ooffset+len)} (case-sensitive).
     */
    public static boolean regionMatches(CharSequence one, int toffset,
                                         CharSequence two, int ooffset, int len) {
        for (int i = 0; i < len; i++) {
            if (one.charAt(toffset + i) != two.charAt(ooffset + i)) return false;
        }
        return true;
    }

    // ── indexOf / lastIndexOf ─────────────────────────────────────────

    /** Finds the index of {@code needle} in {@code s} starting at {@code start}. */
    public static int indexOf(CharSequence s, char needle, int start) {
        if (s == null) return -1;
        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == needle) return i;
        }
        return -1;
    }

    public static int indexOf(CharSequence s, char needle) {
        return indexOf(s, needle, 0);
    }

    public static int lastIndexOf(CharSequence s, char needle) {
        if (s == null) return -1;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == needle) return i;
        }
        return -1;
    }

    // ── expandTemplate ────────────────────────────────────────────────

    /**
     * Very simple template expansion: replaces {@code ^1}, {@code ^2}, … with
     * the corresponding values from {@code values}.
     */
    public static CharSequence expandTemplate(CharSequence template, CharSequence... values) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(template);
        for (int i = values.length; i >= 1; i--) {
            String key = "^" + i;
            String str = ssb.toString();
            int idx = str.indexOf(key);
            while (idx >= 0) {
                ssb.replace(idx, idx + key.length(), values[i - 1]);
                str = ssb.toString();
                idx = str.indexOf(key);
            }
        }
        return ssb;
    }

    // ── getChars ──────────────────────────────────────────────────────

    /** Copies characters from {@code s} into {@code dest}. */
    public static void getChars(CharSequence s, int start, int end,
                                 char[] dest, int destOff) {
        for (int i = start; i < end; i++) {
            dest[destOff + (i - start)] = s.charAt(i);
        }
    }

    // ── stringOrSpannedString ─────────────────────────────────────────

    /**
     * Returns the source unchanged if it is already a String, otherwise
     * wraps it in a SpannableString (preserving spans).
     */
    public static CharSequence stringOrSpannedString(CharSequence source) {
        if (source == null) return null;
        if (source instanceof SpannableString) return source;
        if (source instanceof Spanned) return new SpannableString(source);
        return source.toString();
    }
}
