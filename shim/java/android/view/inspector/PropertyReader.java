package android.view.inspector;
import android.graphics.Color;
import android.graphics.Color;

public interface PropertyReader {
    void readBoolean(int p0, boolean p1);
    void readByte(int p0, byte p1);
    void readChar(int p0, char p1);
    void readColor(int p0, int p1);
    void readColor(int p0, long p1);
    void readColor(int p0, Color p1);
    void readDouble(int p0, double p1);
    void readFloat(int p0, float p1);
    void readGravity(int p0, int p1);
    void readInt(int p0, int p1);
    void readIntEnum(int p0, int p1);
    void readIntFlag(int p0, int p1);
    void readLong(int p0, long p1);
    void readObject(int p0, Object p1);
    void readResourceId(int p0, int p1);
    void readShort(int p0, short p1);
}
