package android.util;

/**
 * Android-compatible Base64 shim. Delegates to java.util.Base64.
 */
public class Base64 {
    public static final int DEFAULT = 0;
    public static final int NO_PADDING = 1;
    public static final int NO_WRAP = 2;
    public static final int URL_SAFE = 8;

    public static byte[] decode(String str, int flags) {
        return getDecoder(flags).decode(str);
    }

    public static byte[] decode(byte[] input, int flags) {
        return getDecoder(flags).decode(input);
    }

    public static byte[] encode(byte[] input, int flags) {
        return getEncoder(flags).encode(input);
    }

    public static String encodeToString(byte[] input, int flags) {
        return getEncoder(flags).encodeToString(input);
    }

    private static java.util.Base64.Decoder getDecoder(int flags) {
        if ((flags & URL_SAFE) != 0) {
            return java.util.Base64.getUrlDecoder();
        }
        return java.util.Base64.getDecoder();
    }

    private static java.util.Base64.Encoder getEncoder(int flags) {
        java.util.Base64.Encoder enc;
        if ((flags & URL_SAFE) != 0) {
            enc = java.util.Base64.getUrlEncoder();
        } else {
            enc = java.util.Base64.getEncoder();
        }
        if ((flags & NO_PADDING) != 0) {
            enc = enc.withoutPadding();
        }
        return enc;
    }
}
