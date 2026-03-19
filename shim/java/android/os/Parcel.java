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

    public boolean readBoolean() { return readInt() != 0; }
    public void writeBoolean(boolean val) { writeInt(val ? 1 : 0); }

    public byte readByte() {
        try { return mDis.readByte(); } catch (Exception e) { return 0; }
    }

    public void writeByte(byte val) {
        try { mDos.writeByte(val); } catch (IOException e) {}
    }

    public Object readValue(ClassLoader loader) {
        int type = readInt();
        switch (type) {
            case 0: return null;
            case 1: return readInt();
            case 2: return readLong();
            case 3: return readFloat();
            case 4: return readDouble();
            case 5: return readString();
            case 6: return readBoolean();
            default: return null;
        }
    }

    public void writeValue(Object v) {
        if (v == null) { writeInt(0); return; }
        if (v instanceof Integer) { writeInt(1); writeInt((Integer) v); }
        else if (v instanceof Long) { writeInt(2); writeLong((Long) v); }
        else if (v instanceof Float) { writeInt(3); writeFloat((Float) v); }
        else if (v instanceof Double) { writeInt(4); writeDouble((Double) v); }
        else if (v instanceof String) { writeInt(5); writeString((String) v); }
        else if (v instanceof Boolean) { writeInt(6); writeBoolean((Boolean) v); }
        else { writeInt(0); }
    }

    public int dataPosition() { return mDataPos; }
    public int dataAvail() { return dataSize() - mDataPos; }
    public int dataCapacity() { return dataSize(); }

    public void writeParcelable(Parcelable p, int flags) {
        if (p == null) { writeString(null); return; }
        writeString(p.getClass().getName());
        p.writeToParcel(this, flags);
    }

    @SuppressWarnings("unchecked")
    public <T extends Parcelable> T readParcelable(ClassLoader loader) {
        return null; // stub
    }

    public void writeBundle(Bundle val) {
        if (val == null) { writeInt(-1); return; }
        writeInt(0); // stub
    }

    public Bundle readBundle() { return new Bundle(); }
    public Bundle readBundle(ClassLoader loader) { return new Bundle(); }

    public android.util.SparseBooleanArray readSparseBooleanArray() {
        return new android.util.SparseBooleanArray();
    }

    public void writeSparseBooleanArray(android.util.SparseBooleanArray val) {
        // stub
    }

    public android.util.SparseArray readSparseArray(ClassLoader loader) {
        return new android.util.SparseArray();
    }

    @SuppressWarnings("unchecked")
    public <T extends Parcelable> T readTypedObject(Parcelable.Creator<T> c) {
        return null;
    }

    public void writeTypedObject(Parcelable val, int flags) {
        if (val == null) { writeInt(0); return; }
        writeInt(1);
        val.writeToParcel(this, flags);
    }

    public void readList(java.util.List outVal, ClassLoader loader) {}
    public void writeList(java.util.List val) {}
}
