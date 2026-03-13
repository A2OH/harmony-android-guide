package android.os;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

/**
 * Android-compatible Parcel shim. In-memory serialization using byte buffer.
 */
public class Parcel {
    private ByteArrayOutputStream mOut;
    private DataOutputStream mDos;
    private byte[] mData;
    private DataInputStream mDis;
    private int mDataPos;

    private Parcel() {
        mOut = new ByteArrayOutputStream();
        mDos = new DataOutputStream(mOut);
    }

    public static Parcel obtain() {
        return new Parcel();
    }

    public void recycle() {
        mOut = null;
        mDos = null;
        mData = null;
        mDis = null;
    }

    public void writeInt(int val) {
        try { mDos.writeInt(val); } catch (IOException e) {}
    }

    public void writeLong(long val) {
        try { mDos.writeLong(val); } catch (IOException e) {}
    }

    public void writeFloat(float val) {
        try { mDos.writeFloat(val); } catch (IOException e) {}
    }

    public void writeDouble(double val) {
        try { mDos.writeDouble(val); } catch (IOException e) {}
    }

    public void writeString(String val) {
        try {
            if (val == null) { mDos.writeInt(-1); return; }
            byte[] bytes = val.getBytes("UTF-8");
            mDos.writeInt(bytes.length);
            mDos.write(bytes);
        } catch (IOException e) {}
    }

    public void writeByteArray(byte[] b) {
        try {
            if (b == null) { mDos.writeInt(-1); return; }
            mDos.writeInt(b.length);
            mDos.write(b);
        } catch (IOException e) {}
    }

    public void setDataPosition(int pos) {
        if (mData == null) {
            mData = mOut.toByteArray();
        }
        mDataPos = pos;
        mDis = new DataInputStream(new ByteArrayInputStream(mData, pos, mData.length - pos));
    }

    public int readInt() {
        try { return mDis.readInt(); } catch (Exception e) { return 0; }
    }

    public long readLong() {
        try { return mDis.readLong(); } catch (Exception e) { return 0; }
    }

    public float readFloat() {
        try { return mDis.readFloat(); } catch (Exception e) { return 0; }
    }

    public double readDouble() {
        try { return mDis.readDouble(); } catch (Exception e) { return 0; }
    }

    public String readString() {
        try {
            int len = mDis.readInt();
            if (len < 0) return null;
            byte[] bytes = new byte[len];
            mDis.readFully(bytes);
            return new String(bytes, "UTF-8");
        } catch (Exception e) { return null; }
    }

    public byte[] createByteArray() {
        try {
            int len = mDis.readInt();
            if (len < 0) return null;
            byte[] bytes = new byte[len];
            mDis.readFully(bytes);
            return bytes;
        } catch (Exception e) { return null; }
    }

    public int dataSize() {
        if (mData != null) return mData.length;
        return mOut.size();
    }

    public byte[] marshall() {
        if (mData == null) mData = mOut.toByteArray();
        return mData;
    }
}
