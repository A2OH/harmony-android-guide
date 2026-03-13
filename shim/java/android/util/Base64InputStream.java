package android.util;

import java.io.*;

/**
 * Android-compatible Base64InputStream shim.
 * Wraps an InputStream, decoding Base64 on-the-fly.
 */
public class Base64InputStream extends FilterInputStream {
    private byte[] mDecoded;
    private int mPos;

    public Base64InputStream(InputStream in, int flags) {
        super(in);
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] tmp = new byte[4096];
            int len;
            while ((len = in.read(tmp)) != -1) {
                buf.write(tmp, 0, len);
            }
            mDecoded = Base64.decode(buf.toByteArray(), flags);
            mPos = 0;
        } catch (IOException e) {
            mDecoded = new byte[0];
            mPos = 0;
        }
    }

    @Override
    public int read() {
        if (mPos >= mDecoded.length) return -1;
        return mDecoded[mPos++] & 0xFF;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        if (mPos >= mDecoded.length) return -1;
        int avail = mDecoded.length - mPos;
        int count = Math.min(avail, length);
        System.arraycopy(mDecoded, mPos, buffer, offset, count);
        mPos += count;
        return count;
    }

    @Override
    public int available() {
        return mDecoded.length - mPos;
    }
}
