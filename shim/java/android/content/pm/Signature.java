package android.content.pm;

import java.util.Arrays;

/**
 * Shim: android.content.pm.Signature
 *
 * Wraps a raw DER-encoded X.509 signing certificate byte array.
 * Used by {@link PackageInfo#signatures} and related APIs.
 */
public class Signature {

    private final byte[] mBytes;

    /**
     * Create a Signature from a raw byte array.
     *
     * @param data the DER-encoded certificate bytes; must not be null
     */
    public Signature(byte[] data) {
        if (data == null) throw new NullPointerException("data must not be null");
        // defensive copy
        mBytes = Arrays.copyOf(data, data.length);
    }

    /**
     * Create a Signature from a hex-encoded string (as returned by
     * {@link #toCharsString()}).
     *
     * @param text a lowercase hexadecimal string of even length
     */
    public Signature(String text) {
        if (text == null) throw new NullPointerException("text must not be null");
        int len = text.length();
        if ((len & 1) != 0) throw new IllegalArgumentException("odd-length hex string");
        mBytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            mBytes[i / 2] = (byte) Integer.parseInt(text.substring(i, i + 2));
        }
    }

    // ── Data accessors ───────────────────────────────────────────────────────

    /**
     * Return a copy of the raw certificate bytes.
     *
     * @return a fresh byte array containing the DER-encoded certificate
     */
    public byte[] toByteArray() {
        return Arrays.copyOf(mBytes, mBytes.length);
    }

    /**
     * Return a lowercase hexadecimal string representation of the certificate
     * bytes.
     *
     * @return lowercase hex string, never null
     */
    public String toCharsString() {
        StringBuilder sb = new StringBuilder(mBytes.length * 2);
        for (byte b : mBytes) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() < 2) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public int hashCode() {
        return Arrays.hashCode(mBytes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Signature)) return false;
        return Arrays.equals(mBytes, ((Signature) obj).mBytes);
    }

    @Override
    public String toString() {
        return "Signature(" + toCharsString() + ")";
    }
}
