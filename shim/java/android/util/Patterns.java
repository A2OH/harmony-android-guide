package android.util;

import java.util.regex.Pattern;

/**
 * Android-compatible Patterns shim. Provides pre-compiled regex Pattern constants.
 */
public final class Patterns {

    /** Pattern for matching a valid email address. */
    public static final Pattern EMAIL_ADDRESS = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
    );

    /** Pattern for matching phone numbers. Loosely matches common formats. */
    public static final Pattern PHONE = Pattern.compile(
            "(\\+[0-9]+[\\- \\.]*)?" +
            "(\\([0-9]+\\)[\\- \\.]*)?" +
            "([0-9][0-9\\- \\.]+[0-9])"
    );

    /** Pattern for matching web URLs (http/https/ftp). */
    public static final Pattern WEB_URL = Pattern.compile(
            "(?i)\\b((?:https?://|ftp://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)" +
            "(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+" +
            "(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»\"\"'']))"
    );

    /** Pattern for matching IPv4 addresses. */
    public static final Pattern IP_ADDRESS = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
            + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
            + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
            + "|[1-9][0-9]|[0-9]))"
    );

    /** Pattern for matching domain names. */
    public static final Pattern DOMAIN_NAME = Pattern.compile(
            "(?i)((?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61})?[a-zA-Z0-9])" +
            "(?:\\.(?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61})?[a-zA-Z0-9]))*)" +
            "\\.([a-zA-Z]{2,63})"
    );

    private Patterns() {}
}
