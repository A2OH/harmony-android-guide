package android.speech.tts;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Locale;
import java.util.Set;

public class Voice implements Parcelable {
    public static final int LATENCY_HIGH = 0;
    public static final int LATENCY_LOW = 0;
    public static final int LATENCY_NORMAL = 0;
    public static final int LATENCY_VERY_HIGH = 0;
    public static final int LATENCY_VERY_LOW = 0;
    public static final int QUALITY_HIGH = 0;
    public static final int QUALITY_LOW = 0;
    public static final int QUALITY_NORMAL = 0;
    public static final int QUALITY_VERY_HIGH = 0;
    public static final int QUALITY_VERY_LOW = 0;

    public Voice(String p0, Locale p1, int p2, int p3, boolean p4, java.util.Set<Object> p5) {}

    public int describeContents() { return 0; }
    public Set<?> getFeatures() { return null; }
    public int getLatency() { return 0; }
    public Locale getLocale() { return null; }
    public String getName() { return null; }
    public int getQuality() { return 0; }
    public boolean isNetworkConnectionRequired() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
