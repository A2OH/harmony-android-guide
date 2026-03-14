package android.os;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;
import java.io.Serializable;
import java.util.ArrayList;

public final class Bundle extends BaseBundle implements Cloneable, Parcelable {
    public static final int EMPTY = 0;

    public Bundle() { super(); }
    public Bundle(ClassLoader p0) { super(); }
    public Bundle(int capacity) { super(capacity); }
    public Bundle(Bundle other) { super(other); }
    public Bundle(PersistableBundle p0) { super(); }

    public Object clone() {
        return new Bundle(this);
    }
    public Bundle deepCopy() { return new Bundle(this); }
    public int describeContents() { return 0; }
    public byte getByte(String p0) { return getByte(p0, (byte) 0); }
    public Byte getByte(String p0, byte p1) {
        Object v = map.get(p0);
        return v instanceof Number ? ((Number) v).byteValue() : p1;
    }
    public char getChar(String p0) { return getChar(p0, '\0'); }
    public char getChar(String p0, char p1) {
        Object v = map.get(p0);
        return v instanceof Character ? (Character) v : p1;
    }
    public CharSequence getCharSequence(String p0, CharSequence p1) {
        Object v = map.get(p0);
        return v instanceof CharSequence ? (CharSequence) v : p1;
    }
    public ClassLoader getClassLoader() { return null; }
    public float getFloat(String p0) { return getFloat(p0, 0f); }
    public float getFloat(String p0, float p1) {
        Object v = map.get(p0);
        return v instanceof Number ? ((Number) v).floatValue() : p1;
    }
    public short getShort(String p0) { return getShort(p0, (short) 0); }
    public short getShort(String p0, short p1) {
        Object v = map.get(p0);
        return v instanceof Number ? ((Number) v).shortValue() : p1;
    }
    public boolean hasFileDescriptors() { return false; }
    public void putAll(Bundle other) {
        if (other != null) map.putAll(other.map);
    }
    public void putBinder(String p0, IBinder p1) { map.put(p0, p1); }
    public void putBundle(String p0, Bundle p1) { map.put(p0, p1); }
    public void putByte(String p0, byte p1) { map.put(p0, p1); }
    public void putByteArray(String p0, byte[] p1) { map.put(p0, p1); }
    public void putChar(String p0, char p1) { map.put(p0, p1); }
    public void putCharArray(String p0, char[] p1) { map.put(p0, p1); }
    public void putCharSequence(String p0, CharSequence p1) { map.put(p0, p1); }
    public void putCharSequenceArray(String p0, CharSequence[] p1) { map.put(p0, p1); }
    public void putCharSequenceArrayList(String p0, java.util.ArrayList<Object> p1) { map.put(p0, p1); }
    public void putFloat(String p0, float p1) { map.put(p0, p1); }
    public void putFloatArray(String p0, float[] p1) { map.put(p0, p1); }
    public void putBooleanArray(String p0, boolean[] p1) { map.put(p0, p1); }
    public void putDoubleArray(String p0, double[] p1) { map.put(p0, p1); }
    public void putLongArray(String p0, long[] p1) { map.put(p0, p1); }
    public void putIntegerArrayList(String p0, java.util.ArrayList<Object> p1) { map.put(p0, p1); }
    public void putParcelable(String p0, Parcelable p1) { map.put(p0, p1); }
    public void putParcelableArray(String p0, Parcelable[] p1) { map.put(p0, p1); }
    public void putParcelableArrayList(String p0, java.util.ArrayList<Object> p1) { map.put(p0, p1); }
    public void putSerializable(String p0, Serializable p1) { map.put(p0, p1); }
    public void putShort(String p0, short p1) { map.put(p0, p1); }
    public void putShortArray(String p0, short[] p1) { map.put(p0, p1); }
    public void putSize(String p0, Size p1) { map.put(p0, p1); }
    public void putSizeF(String p0, SizeF p1) { map.put(p0, p1); }
    public void putSparseParcelableArray(String p0, Object p1) { map.put(p0, p1); }
    public void putStringArrayList(String p0, java.util.ArrayList<Object> p1) { map.put(p0, p1); }
    public void readFromParcel(Parcel p0) {}
    public void setClassLoader(ClassLoader p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
