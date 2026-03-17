package android.os;
/**
 * Shim: android.os.PatternMatcher — a simple pattern matcher for paths.
 * Supports literal, prefix, and simple glob matching.
 */
public class PatternMatcher {

    /** Pattern type: the given pattern must exactly match the string. */
    public static final int PATTERN_LITERAL = 0;

    /** Pattern type: the given pattern must match the beginning of the string. */
    public static final int PATTERN_PREFIX = 1;

    /** Pattern type: the given pattern is a simple glob ('*' matches any sequence). */
    public static final int PATTERN_SIMPLE_GLOB = 2;

    /** Pattern type: the given pattern is an advanced glob. */
    public static final int PATTERN_ADVANCED_GLOB = 3;

    /** Pattern type: the given pattern is treated as a suffix. */
    public static final int PATTERN_SUFFIX = 4;

    private final String pattern;
    private final int type;

    public PatternMatcher(String pattern, int type) {
        this.pattern = pattern;
        this.type = type;
    }

    public String getPath() {
        return pattern;
    }

    public int getType() {
        return type;
    }

    /**
     * Attempt to match the given string against the pattern.
     */
    public boolean match(String str) {
        if (str == null) return false;
        switch (type) {
            case PATTERN_LITERAL:
                return pattern.equals(str);
            case PATTERN_PREFIX:
                return str.startsWith(pattern);
            case PATTERN_SUFFIX:
                return str.endsWith(pattern);
            case PATTERN_SIMPLE_GLOB:
                return matchSimpleGlob(pattern, str);
            case PATTERN_ADVANCED_GLOB:
                return matchSimpleGlob(pattern, str);
            default:
                return false;
        }
    }

    private static boolean matchSimpleGlob(String pattern, String str) {
        // Simple implementation: split on '*' and check ordered containment
        String[] parts = splitByChar(pattern, '*');
        int pos = 0;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;
            int idx = str.indexOf(part, pos);
            if (idx < 0) return false;
            if (i == 0 && idx != 0) return false; // must match start if no leading *
            pos = idx + part.length();
        }
        // If pattern doesn't end with *, str must end at pos
        if (!pattern.endsWith("*") && pos != str.length()) return false;
        return true;
    }

    private static String[] splitByChar(String s, char delim) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        int start = 0;
        for (int i = 0; i <= s.length(); i++) {
            if (i == s.length() || s.charAt(i) == delim) {
                parts.add(s.substring(start, i));
                start = i + 1;
            }
        }
        return parts.toArray(new String[0]);
    }

    @Override
    public String toString() {
        String typeStr;
        switch (type) {
            case PATTERN_LITERAL: typeStr = "LITERAL"; break;
            case PATTERN_PREFIX: typeStr = "PREFIX"; break;
            case PATTERN_SIMPLE_GLOB: typeStr = "GLOB"; break;
            case PATTERN_ADVANCED_GLOB: typeStr = "AGLOB"; break;
            case PATTERN_SUFFIX: typeStr = "SUFFIX"; break;
            default: typeStr = "?" + type; break;
        }
        return "PatternMatcher{" + typeStr + ":" + pattern + "}";
    }
}
