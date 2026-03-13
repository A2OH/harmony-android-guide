package android.util;

/**
 * Android-compatible Base64 shim.
 * Manual implementation — no dependency on java.util.Base64 (missing in KitKat core).
 */
public class Base64 {
    public static final int DEFAULT = 0;
    public static final int NO_PADDING = 1;
    public static final int NO_WRAP = 2;
    public static final int URL_SAFE = 8;

    private static final char[] STD_ALPHABET =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final char[] URL_ALPHABET =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();

    private static final int[] DECODE_TABLE = new int[128];
    private static final int[] URL_DECODE_TABLE = new int[128];

    static {
        for (int i = 0; i < 128; i++) {
            DECODE_TABLE[i] = -1;
            URL_DECODE_TABLE[i] = -1;
        }
        for (int i = 0; i < 64; i++) {
            DECODE_TABLE[STD_ALPHABET[i]] = i;
            URL_DECODE_TABLE[URL_ALPHABET[i]] = i;
        }
    }

    public static byte[] decode(String str, int flags) {
        if (str == null) return null;
        return decodeBytes(str, flags);
    }

    public static byte[] decode(byte[] input, int flags) {
        if (input == null) return null;
        return decodeBytes(new String(input), flags);
    }

    private static byte[] decodeBytes(String str, int flags) {
        int[] table = ((flags & URL_SAFE) != 0) ? URL_DECODE_TABLE : DECODE_TABLE;

        // Strip whitespace and padding, count valid chars
        int validCount = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '=' || c == '\n' || c == '\r' || c == ' ' || c == '\t') continue;
            if (c < 128 && table[c] >= 0) validCount++;
        }

        int outLen = (validCount * 3) / 4;
        byte[] out = new byte[outLen];
        int buf = 0;
        int bits = 0;
        int pos = 0;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '=' || c == '\n' || c == '\r' || c == ' ' || c == '\t') continue;
            if (c >= 128 || table[c] < 0) continue;
            buf = (buf << 6) | table[c];
            bits += 6;
            if (bits >= 8) {
                bits -= 8;
                if (pos < outLen) {
                    out[pos++] = (byte) ((buf >> bits) & 0xFF);
                }
            }
        }

        if (pos < outLen) {
            byte[] trimmed = new byte[pos];
            System.arraycopy(out, 0, trimmed, 0, pos);
            return trimmed;
        }
        return out;
    }

    public static byte[] encode(byte[] input, int flags) {
        return encodeToString(input, flags).getBytes();
    }

    public static String encodeToString(byte[] input, int flags) {
        if (input == null) return null;
        char[] alphabet = ((flags & URL_SAFE) != 0) ? URL_ALPHABET : STD_ALPHABET;
        boolean pad = (flags & NO_PADDING) == 0;

        int len = input.length;
        int outLen = ((len + 2) / 3) * 4;
        char[] out = new char[outLen];
        int pos = 0;

        for (int i = 0; i < len; i += 3) {
            int b0 = input[i] & 0xFF;
            int b1 = (i + 1 < len) ? (input[i + 1] & 0xFF) : 0;
            int b2 = (i + 2 < len) ? (input[i + 2] & 0xFF) : 0;
            int triple = (b0 << 16) | (b1 << 8) | b2;

            out[pos++] = alphabet[(triple >> 18) & 0x3F];
            out[pos++] = alphabet[(triple >> 12) & 0x3F];
            out[pos++] = (i + 1 < len) ? alphabet[(triple >> 6) & 0x3F] : (pad ? '=' : 0);
            out[pos++] = (i + 2 < len) ? alphabet[triple & 0x3F] : (pad ? '=' : 0);
        }

        // Trim nulls if no padding
        if (!pad) {
            int end = pos;
            while (end > 0 && out[end - 1] == 0) end--;
            return new String(out, 0, end);
        }
        return new String(out, 0, pos);
    }
}
