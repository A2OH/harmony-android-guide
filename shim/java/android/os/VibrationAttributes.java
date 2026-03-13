package android.os;

public final class VibrationAttributes implements Parcelable {
    public static final int FLAG_BYPASS_INTERRUPTION_POLICY = 0;
    public static final int USAGE_ALARM = 0;
    public static final int USAGE_CLASS_ALARM = 0;
    public static final int USAGE_CLASS_FEEDBACK = 0;
    public static final int USAGE_CLASS_MASK = 0;
    public static final int USAGE_CLASS_UNKNOWN = 0;
    public static final int USAGE_COMMUNICATION_REQUEST = 0;
    public static final int USAGE_HARDWARE_FEEDBACK = 0;
    public static final int USAGE_NOTIFICATION = 0;
    public static final int USAGE_PHYSICAL_EMULATION = 0;
    public static final int USAGE_RINGTONE = 0;
    public static final int USAGE_TOUCH = 0;
    public static final int USAGE_UNKNOWN = 0;

    public VibrationAttributes() {}

    public int describeContents() { return 0; }
    public int getFlags() { return 0; }
    public int getUsage() { return 0; }
    public int getUsageClass() { return 0; }
    public boolean isFlagSet(int p0) { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
