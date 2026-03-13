package android.media;

import java.util.UUID;

/**
 * Android-compatible MediaCrypto shim. Stub for media decryption.
 */
public class MediaCrypto {

    private final UUID mSchemeUUID;

    public MediaCrypto(UUID uuid, byte[] initData) {
        if (uuid == null) {
            throw new IllegalArgumentException("uuid must not be null");
        }
        mSchemeUUID = uuid;
    }

    public static boolean isCryptoSchemeSupported(UUID uuid) {
        return false;
    }

    public boolean requiresSecureDecoderComponent(String mime) {
        return false;
    }

    public void setMediaDrmSession(byte[] sessionId) {}

    public void release() {}
}
