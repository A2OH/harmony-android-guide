package android.telephony;

/**
 * Android-compatible PhoneNumberUtils shim. Pure Java static utility methods.
 * Provides basic phone number parsing and formatting without Android framework.
 */
public class PhoneNumberUtils {

    private PhoneNumberUtils() {}

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    /**
     * Returns true if the string appears to be a globally dialable phone number.
     * Accepts digits, spaces, dashes, parentheses, '+', '#', '*'.
     */
    public static boolean isGlobalPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        for (int i = 0; i < phoneNumber.length(); i++) {
            char c = phoneNumber.charAt(i);
            if (!Character.isDigit(c) && c != '+' && c != '-' && c != '.'
                    && c != '(' && c != ')' && c != ' ' && c != '#' && c != '*') {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the number is a recognized emergency number.
     * Checks a limited hard-coded set for shim purposes.
     */
    public static boolean isEmergencyNumber(String number) {
        if (number == null) return false;
        String stripped = stripSeparators(number);
        return stripped.equals("911") || stripped.equals("112")
                || stripped.equals("999") || stripped.equals("000")
                || stripped.equals("110") || stripped.equals("119")
                || stripped.equals("120") || stripped.equals("122");
    }

    // -------------------------------------------------------------------------
    // Normalization / stripping
    // -------------------------------------------------------------------------

    /**
     * Removes non-dialable characters (spaces, dashes, parentheses, dots)
     * from a phone number string, keeping digits, '+', '#', '*'.
     */
    public static String stripSeparators(String phoneNumber) {
        if (phoneNumber == null) return null;
        StringBuilder sb = new StringBuilder(phoneNumber.length());
        for (int i = 0; i < phoneNumber.length(); i++) {
            char c = phoneNumber.charAt(i);
            if (Character.isDigit(c) || c == '+' || c == '#' || c == '*') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Normalizes a phone number: strips separators and converts letters to digits
     * using standard dialpad mapping.
     */
    public static String normalizeNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
        StringBuilder sb = new StringBuilder(phoneNumber.length());
        for (int i = 0; i < phoneNumber.length(); i++) {
            char c = phoneNumber.charAt(i);
            if (Character.isDigit(c) || c == '+' || c == '#' || c == '*') {
                sb.append(c);
            } else {
                char mapped = letterToDigit(Character.toUpperCase(c));
                if (mapped != 0) sb.append(mapped);
            }
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Formatting
    // -------------------------------------------------------------------------

    /**
     * Formats a phone number string using a simple national-number heuristic.
     * For 10-digit US numbers produces (NXX) NXX-XXXX; otherwise returns as-is.
     */
    public static String formatNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
        String stripped = stripSeparators(phoneNumber);
        if (stripped == null) return phoneNumber;
        // US local 10-digit: (NXX) NXX-XXXX
        if (stripped.length() == 10 && !stripped.startsWith("+")) {
            return "(" + stripped.substring(0) + ") "
                    + stripped.substring(3) + "-"
                    + stripped.substring(6);
        }
        // US with country code 11-digit starting with 1
        if (stripped.length() == 11 && stripped.startsWith("1")) {
            return "+1 (" + stripped.substring(1) + ") "
                    + stripped.substring(4) + "-"
                    + stripped.substring(7);
        }
        return phoneNumber;
    }

    /**
     * Formats a phone number with a supplied network format hint (ignored in shim;
     * delegates to {@link #formatNumber(String)}).
     */
    public static String formatNumber(String phoneNumber, String defaultCountryIso) {
        return formatNumber(phoneNumber);
    }

    // -------------------------------------------------------------------------
    // Comparison
    // -------------------------------------------------------------------------

    /**
     * Compares two phone numbers for equality, ignoring formatting separators.
     * Trailing-digit suffix comparison (min 7 digits) is used to handle
     * country-code variations.
     */
    public static boolean compare(String a, String b) {
        if (a == null || b == null) return a == b;
        String na = stripSeparators(a);
        String nb = stripSeparators(b);
        if (na.equals(nb)) return true;
        // Compare trailing 7 digits as a minimal suffix match
        int minLen = 7;
        if (na.length() >= minLen && nb.length() >= minLen) {
            return na.substring(na.length() - minLen)
                     .equals(nb.substring(nb.length() - minLen));
        }
        return false;
    }

    /**
     * Compares two phone numbers with an optional context (ignored in shim;
     * delegates to {@link #compare(String, String)}).
     */
    public static boolean compare(Object context, String a, String b) {
        return compare(a, b);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static char letterToDigit(char c) {
        switch (c) {
            case 'A': case 'B': case 'C': return '2';
            case 'D': case 'E': case 'F': return '3';
            case 'G': case 'H': case 'I': return '4';
            case 'J': case 'K': case 'L': return '5';
            case 'M': case 'N': case 'O': return '6';
            case 'P': case 'Q': case 'R': case 'S': return '7';
            case 'T': case 'U': case 'V': return '8';
            case 'W': case 'X': case 'Y': case 'Z': return '9';
            default: return 0;
        }
    }
}
