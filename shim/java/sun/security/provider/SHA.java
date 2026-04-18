package sun.security.provider;

import java.security.MessageDigestSpi;

/**
 * Minimal SHA-1 MessageDigest implementation for framework.jar compatibility.
 * Uses Java's built-in SHA-1 from the boot class path.
 */
public class SHA extends MessageDigestSpi {
    private byte[] buffer = new byte[0];

    @Override
    protected void engineUpdate(byte input) {
        byte[] old = buffer;
        buffer = new byte[old.length + 1];
        System.arraycopy(old, 0, buffer, 0, old.length);
        buffer[old.length] = input;
    }

    @Override
    protected void engineUpdate(byte[] input, int offset, int len) {
        byte[] old = buffer;
        buffer = new byte[old.length + len];
        System.arraycopy(old, 0, buffer, 0, old.length);
        System.arraycopy(input, offset, buffer, old.length, len);
    }

    @Override
    protected byte[] engineDigest() {
        // Direct SHA-1 implementation (FIPS 180-4)
        int msgLen = buffer.length;
        int padLen = 64 - ((msgLen + 9) % 64);
        if (padLen == 64) padLen = 0;
        byte[] padded = new byte[msgLen + 1 + padLen + 8];
        System.arraycopy(buffer, 0, padded, 0, msgLen);
        padded[msgLen] = (byte)0x80;
        long bitLen = (long)msgLen * 8;
        for (int i = 0; i < 8; i++)
            padded[padded.length - 1 - i] = (byte)(bitLen >>> (i * 8));

        int h0=0x67452301, h1=0xEFCDAB89, h2=0x98BADCFE, h3=0x10325476, h4=0xC3D2E1F0;
        int[] w = new int[80];
        for (int chunk = 0; chunk < padded.length; chunk += 64) {
            for (int i = 0; i < 16; i++)
                w[i] = ((padded[chunk+i*4]&0xFF)<<24)|((padded[chunk+i*4+1]&0xFF)<<16)|
                       ((padded[chunk+i*4+2]&0xFF)<<8)|(padded[chunk+i*4+3]&0xFF);
            for (int i = 16; i < 80; i++) {
                int x = w[i-3]^w[i-8]^w[i-14]^w[i-16];
                w[i] = (x<<1)|(x>>>31);
            }
            int a=h0, b=h1, c=h2, d=h3, e=h4;
            for (int i = 0; i < 80; i++) {
                int f, k;
                if (i<20) { f=(b&c)|((~b)&d); k=0x5A827999; }
                else if (i<40) { f=b^c^d; k=0x6ED9EBA1; }
                else if (i<60) { f=(b&c)|(b&d)|(c&d); k=0x8F1BBCDC; }
                else { f=b^c^d; k=0xCA62C1D6; }
                int t = ((a<<5)|(a>>>27)) + f + e + k + w[i];
                e=d; d=c; c=(b<<30)|(b>>>2); b=a; a=t;
            }
            h0+=a; h1+=b; h2+=c; h3+=d; h4+=e;
        }
        byte[] digest = new byte[20];
        for (int i=0;i<4;i++) { digest[i]=(byte)(h0>>>(24-i*8)); digest[4+i]=(byte)(h1>>>(24-i*8));
            digest[8+i]=(byte)(h2>>>(24-i*8)); digest[12+i]=(byte)(h3>>>(24-i*8)); digest[16+i]=(byte)(h4>>>(24-i*8)); }
        return digest;
    }

    @Override
    protected void engineReset() {
        buffer = new byte[0];
    }

    @Override
    protected int engineGetDigestLength() {
        return 20;
    }
}
