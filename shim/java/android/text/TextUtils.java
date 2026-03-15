package android.text;

public class TextUtils {
    public TextUtils() {}

    public static final int CAP_MODE_CHARACTERS = 4096;
    public static final int CAP_MODE_SENTENCES = 16384;
    public static final int CAP_MODE_WORDS = 8192;
    public static final int CHAR_SEQUENCE_CREATOR = 0;
    public static final int SAFE_STRING_FLAG_FIRST_LINE = 0x1;
    public static final int SAFE_STRING_FLAG_SINGLE_LINE = 0x2;
    public static final int SAFE_STRING_FLAG_TRIM = 0x4;

    /**
     * Returns true if the string is null or 0-length.
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns true if a and b are equal, including if they are both null.
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

    /**
     * Returns a string containing the tokens joined by delimiters.
     */
    public static String join(CharSequence delimiter, Object[] tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     */
    public static String join(CharSequence delimiter, Iterable tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    /**
     * String.split() returns [''] when the string to be split is empty.
     * This returns []. This does not remove any empty strings from the result.
     */
    public static String[] split(String text, String expression) {
        if (text == null) return new String[0];
        if (text.length() == 0) {
            return new String[0];
        }
        return text.split(expression, -1);
    }

    /**
     * Returns true if the string is composed entirely of digits.
     */
    public static boolean isDigitsOnly(CharSequence str) {
        final int len = str.length();
        if (len == 0) return false;
        for (int i = 0; i < len; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Html-encode the string.
     */
    public static String htmlEncode(String s) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Returns the length that the specified CharSequence would have if
     * spaces and ASCII control characters were trimmed from the start and end.
     */
    public static int getTrimmedLength(CharSequence s) {
        int len = s.length();
        int start = 0;
        while (start < len && s.charAt(start) <= ' ') {
            start++;
        }
        int end = len;
        while (end > start && s.charAt(end - 1) <= ' ') {
            end--;
        }
        return end - start;
    }

    /**
     * Returns the index of the first occurrence of ch in s, starting from start,
     * or -1 if not found.
     */
    public static int indexOf(CharSequence s, char ch) {
        return indexOf(s, ch, 0);
    }

    public static int indexOf(CharSequence s, char ch, int start) {
        return indexOf(s, ch, start, s.length());
    }

    public static int indexOf(CharSequence s, char ch, int start, int end) {
        if (s instanceof String) {
            // Delegate for efficiency
            int idx = ((String) s).indexOf(ch, start);
            if (idx >= end) return -1;
            return idx;
        }
        int clampEnd = Math.min(end, s.length());
        for (int i = Math.max(0, start); i < clampEnd; i++) {
            if (s.charAt(i) == ch) return i;
        }
        return -1;
    }

    /**
     * Returns the index of the first occurrence of needle in s,
     * starting from start, or -1 if not found.
     */
    public static int indexOf(CharSequence s, CharSequence needle) {
        return indexOf(s, needle, 0, s.length());
    }

    public static int indexOf(CharSequence s, CharSequence needle, int start) {
        return indexOf(s, needle, start, s.length());
    }

    public static int indexOf(CharSequence s, CharSequence needle, int start, int end) {
        int nlen = needle.length();
        if (nlen == 0) return start;
        char c = needle.charAt(0);
        for (int i = start; i <= end - nlen; i++) {
            if (s.charAt(i) == c) {
                boolean found = true;
                for (int j = 1; j < nlen; j++) {
                    if (s.charAt(i + j) != needle.charAt(j)) {
                        found = false;
                        break;
                    }
                }
                if (found) return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(CharSequence s, char ch) {
        return lastIndexOf(s, ch, s.length() - 1);
    }

    public static int lastIndexOf(CharSequence s, char ch, int last) {
        return lastIndexOf(s, ch, 0, last);
    }

    public static int lastIndexOf(CharSequence s, char ch, int start, int last) {
        int clampLast = Math.min(last, s.length() - 1);
        for (int i = clampLast; i >= start; i--) {
            if (s.charAt(i) == ch) return i;
        }
        return -1;
    }

    /**
     * Create a new String object containing the given range of characters
     * from the source string.
     */
    public static String substring(CharSequence source, int start, int end) {
        if (source instanceof String) {
            return ((String) source).substring(start, end);
        }
        char[] buf = new char[end - start];
        for (int i = start; i < end; i++) {
            buf[i - start] = source.charAt(i);
        }
        return new String(buf);
    }

    /**
     * Return the specified subsequence of the source string, replacing
     * occurrences of sources[i] with destinations[i].
     */
    public static CharSequence replace(CharSequence template, String[] sources, CharSequence[] destinations) {
        String s = template.toString();
        for (int i = 0; i < sources.length; i++) {
            s = s.replace(sources[i], destinations[i]);
        }
        return s;
    }

    /**
     * Does a region match between CharSequences.
     */
    public static boolean regionMatches(CharSequence one, int toffset,
                                        CharSequence two, int ooffset, int len) {
        if (one instanceof String && two instanceof String) {
            return ((String) one).regionMatches(toffset, (String) two, ooffset, len);
        }
        int tempLen = 2 * len;
        if (tempLen < len) {
            // overflow
            return false;
        }
        char[] tmp = new char[tempLen];
        // getChars for one
        for (int i = 0; i < len; i++) {
            int idx = toffset + i;
            if (idx < 0 || idx >= one.length()) return false;
            tmp[i] = one.charAt(idx);
        }
        // getChars for two
        for (int i = 0; i < len; i++) {
            int idx = ooffset + i;
            if (idx < 0 || idx >= two.length()) return false;
            tmp[len + i] = two.charAt(idx);
        }
        for (int i = 0; i < len; i++) {
            if (tmp[i] != tmp[len + i]) return false;
        }
        return true;
    }

    public static boolean isGraphic(CharSequence str) {
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            int type = Character.getType(str.charAt(i));
            if (type != Character.CONTROL
                && type != Character.FORMAT
                && type != Character.SURROGATE
                && type != Character.UNASSIGNED
                && type != Character.LINE_SEPARATOR
                && type != Character.PARAGRAPH_SEPARATOR
                && type != Character.SPACE_SEPARATOR) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence concat(CharSequence... text) {
        if (text.length == 0) return "";
        if (text.length == 1) return text[0];
        StringBuilder sb = new StringBuilder();
        for (CharSequence piece : text) {
            sb.append(piece);
        }
        return sb.toString();
    }

    public static CharSequence stringOrSpannedString(CharSequence source) {
        if (source == null) return null;
        return source.toString();
    }

    public static void getChars(CharSequence s, int start, int end, char[] dest, int destoff) {
        if (s instanceof String) {
            ((String) s).getChars(start, end, dest, destoff);
        } else {
            for (int i = start; i < end; i++) {
                dest[destoff + (i - start)] = s.charAt(i);
            }
        }
    }

    public static void copySpansFrom(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {}
    public static void dumpSpans(Object p0, Object p1, Object p2) {}
    public static Object ellipsize(Object p0, Object p1, Object p2, Object p3) { return null; }
    public static Object ellipsize(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) { return null; }
    public static Object expandTemplate(Object p0, Object p1) { return null; }
    public static int getCapsMode(Object p0, Object p1, Object p2) { return 0; }
    public static int getLayoutDirectionFromLocale(Object p0) { return 0; }
    public static int getOffsetAfter(Object p0, Object p1) { return 0; }
    public static int getOffsetBefore(Object p0, Object p1) { return 0; }
    public static Object listEllipsize(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) { return null; }
    public static void writeToParcel(Object p0, Object p1, Object p2) {}
}
