package android.icu.text;
import android.icu.util.ULocale;
import android.icu.util.ULocale;

import android.icu.util.ULocale;
import java.util.HashMap;
import java.util.Map;

/**
 * Android ICU DateTimePatternGenerator shim.
 * Provides best-pattern generation based on skeleton strings.
 * Pure Java implementation — no ICU4J dependency.
 */
public class DateTimePatternGenerator {

    // Maps skeletons to reasonable default patterns for the default locale
    private static final Map<String, String> SKELETON_MAP = new HashMap<String, String>();

    static {
        SKELETON_MAP.put("yMd",         "M/d/y");
        SKELETON_MAP.put("yMMMd",       "MMM d, y");
        SKELETON_MAP.put("yMMMMd",      "MMMM d, y");
        SKELETON_MAP.put("MMMd",        "MMM d");
        SKELETON_MAP.put("MMMMd",       "MMMM d");
        SKELETON_MAP.put("Hm",          "HH:mm");
        SKELETON_MAP.put("Hms",         "HH:mm:ss");
        SKELETON_MAP.put("hm",          "h:mm a");
        SKELETON_MAP.put("hms",         "h:mm:ss a");
        SKELETON_MAP.put("yMdHm",       "M/d/y, HH:mm");
        SKELETON_MAP.put("yMdHms",      "M/d/y, HH:mm:ss");
        SKELETON_MAP.put("yMMMdHm",     "MMM d, y, HH:mm");
        SKELETON_MAP.put("yMMMdHms",    "MMM d, y, HH:mm:ss");
        SKELETON_MAP.put("Ed",          "Object d");
        SKELETON_MAP.put("MEd",         "Object, M/d");
        SKELETON_MAP.put("MMMEd",       "Object, MMM d");
        SKELETON_MAP.put("yMEd",        "Object, M/d/y");
        SKELETON_MAP.put("yMMMEd",      "Object, MMM d, y");
        SKELETON_MAP.put("ms",          "mm:ss");
    }

    private final ULocale locale;
    private final Map<String, String> customPatterns = new HashMap<String, String>();

    private DateTimePatternGenerator(ULocale locale) {
        this.locale = locale;
    }

    // ---- Static factories ----

    public static DateTimePatternGenerator getInstance() {
        return new DateTimePatternGenerator(ULocale.getDefault());
    }

    public static DateTimePatternGenerator getInstance(ULocale locale) {
        return new DateTimePatternGenerator(locale);
    }

    // ---- Core methods ----

    /**
     * Returns the best date/time pattern for the given skeleton.
     * Falls back to the skeleton itself if no mapping is found.
     */
    public String getBestPattern(String skeleton) {
        if (customPatterns.containsKey(skeleton)) {
            return customPatterns.get(skeleton);
        }
        String pattern = SKELETON_MAP.get(skeleton);
        return (pattern != null) ? pattern : skeleton;
    }

    /**
     * Adds a custom pattern for a skeleton, overriding any default.
     * @param pattern  the date/time pattern
     * @param override if true, replace any existing pattern for this skeleton
     * @return the skeleton string extracted from the pattern
     */
    public String addPattern(String pattern, boolean override) {
        String skeleton = getSkeleton(pattern);
        if (override || !customPatterns.containsKey(skeleton)) {
            customPatterns.put(skeleton, pattern);
        }
        return skeleton;
    }

    /**
     * Extracts the skeleton (field letters without widths) from a pattern.
     * e.g. "MMM d, y" -> "MMMdy"
     */
    public String getSkeleton(String pattern) {
        if (pattern == null) return "";
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '\'') {
                inQuote = !inQuote;
                continue;
            }
            if (!inQuote && Character.isLetter(c)) {
                // Append only one occurrence per letter type to form the skeleton
                if (sb.length() == 0 || sb.charAt(sb.length() - 1) != c) {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Returns the base skeleton (letters only, normalized to single occurrences).
     * e.g. "MMMMd" -> "Md"
     */
    public String getBaseSkeleton(String pattern) {
        String skeleton = getSkeleton(pattern);
        // De-duplicate consecutive same letters (already done by getSkeleton logic above)
        return skeleton;
    }
}
