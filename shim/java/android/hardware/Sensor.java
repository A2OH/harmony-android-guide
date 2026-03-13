package android.hardware;

/**
 * Android-compatible Sensor stub.
 */
public class Sensor {
    public static final int TYPE_ACCELEROMETER = 1;
    public static final int TYPE_MAGNETIC_FIELD = 2;
    public static final int TYPE_GYROSCOPE = 4;
    public static final int TYPE_LIGHT = 5;
    public static final int TYPE_PROXIMITY = 8;

    private final String mName;
    private final int mType;

    public Sensor(String name, int type) {
        mName = name;
        mType = type;
    }

    public String getName() { return mName; }
    public int getType() { return mType; }

    @Override
    public String toString() {
        return "Sensor{name=" + mName + ", type=" + mType + "}";
    }
}
