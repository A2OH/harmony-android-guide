package android.media.audiofx;
import android.provider.Settings;
import android.provider.Settings;

public class Virtualizer extends AudioEffect {
    public static final int PARAM_STRENGTH = 0;
    public static final int PARAM_STRENGTH_SUPPORTED = 0;
    public static final int VIRTUALIZATION_MODE_AUTO = 0;
    public static final int VIRTUALIZATION_MODE_BINAURAL = 0;
    public static final int VIRTUALIZATION_MODE_OFF = 0;
    public static final int VIRTUALIZATION_MODE_TRANSAURAL = 0;

    public Virtualizer(int p0, int p1) {}

    public boolean canVirtualize(int p0, int p1) { return false; }
    public boolean forceVirtualizationMode(int p0) { return false; }
    public Settings getProperties() { return null; }
    public short getRoundedStrength() { return 0; }
    public boolean getSpeakerAngles(int p0, int p1, int[] p2) { return false; }
    public boolean getStrengthSupported() { return false; }
    public int getVirtualizationMode() { return 0; }
    public void setParameterListener(Object p0) {}
    public void setProperties(Settings p0) {}
    public void setStrength(short p0) {}
}
