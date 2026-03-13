package android.text.util;
import android.text.Spannable;
import android.widget.TextView;
import android.text.Spannable;
import android.widget.TextView;
import java.net.URL;
import java.util.regex.Pattern;

import android.text.Spannable;
import android.widget.TextView;

/**
 * Android-compatible Linkify shim.
 * Linkify scans text and turns text patterns (URLs, phone numbers, etc.)
 * into clickable links.
 */
public final class Linkify {

    /** Bit mask indicating web URLs should be matched. */
    public static final int WEB_URLS       = 0x01;
    /** Bit mask indicating email addresses should be matched. */
    public static final int EMAIL_ADDRESSES = 0x02;
    /** Bit mask indicating phone numbers should be matched. */
    public static final int PHONE_NUMBERS  = 0x04;
    /** Bit mask indicating map addresses should be matched. */
    public static final int MAP_ADDRESSES  = 0x08;
    /** Bit mask for all supported pattern types. */
    public static final int ALL            = WEB_URLS | EMAIL_ADDRESSES | PHONE_NUMBERS | MAP_ADDRESSES;

    /**
     * Interface for deciding whether a regex match is a valid link.
     */
    public interface MatchFilter {
        boolean acceptMatch(CharSequence s, int start, int end);
    }

    /**
     * Interface for transforming the matched text into a URL.
     */
    public interface TransformFilter {
        String transformUrl(java.util.regex.Matcher match, String url);
    }

    private Linkify() {}

    /**
     * Scans the text of {@code text} and turns all occurrences of the specified
     * mask patterns into clickable links. Stub — performs no-op in shim.
     */
    public static boolean addLinks(Spannable text, int mask) {
        return false;
    }

    /**
     * Scans the text of {@code view} and turns all occurrences of the specified
     * mask patterns into clickable links. Stub — performs no-op in shim.
     */
    public static boolean addLinks(TextView view, int mask) {
        return false;
    }

    /**
     * Applies a supplied regex to the text of {@code text}. Stub — no-op.
     */
    public static boolean addLinks(Spannable text, java.util.regex.Pattern pattern,
                                   String scheme) {
        return false;
    }

    /**
     * Applies a supplied regex to the text of {@code text} with custom filters. Stub — no-op.
     */
    public static boolean addLinks(Spannable text, java.util.regex.Pattern pattern,
                                   String scheme, MatchFilter matchFilter,
                                   TransformFilter transformFilter) {
        return false;
    }

    /**
     * Applies a supplied regex to the text of {@code view}. Stub — no-op.
     */
    public static boolean addLinks(TextView view, java.util.regex.Pattern pattern,
                                   String scheme) {
        return false;
    }
}
