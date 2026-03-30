package sun.security.provider;

import java.security.SecureRandomSpi;

/**
 * Stub SHA1PRNG SecureRandom — uses System.nanoTime() for seeding.
 * Not cryptographically secure, but sufficient for UUID generation.
 */
public class SecureRandom extends SecureRandomSpi {
    private long seed;

    public SecureRandom() {
        seed = System.nanoTime() ^ Thread.currentThread().getId();
    }

    @Override
    protected void engineSetSeed(byte[] s) {
        long v = 0;
        for (int i = 0; i < Math.min(s.length, 8); i++) {
            v = (v << 8) | (s[i] & 0xFF);
        }
        seed ^= v;
    }

    @Override
    protected void engineNextBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            seed = seed * 6364136223846793005L + 1442695040888963407L; // LCG
            bytes[i] = (byte)(seed >>> 56);
        }
    }

    @Override
    protected byte[] engineGenerateSeed(int numBytes) {
        byte[] result = new byte[numBytes];
        engineNextBytes(result);
        return result;
    }
}
