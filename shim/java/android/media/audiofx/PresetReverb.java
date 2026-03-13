package android.media.audiofx;
import android.provider.Settings;
import android.provider.Settings;

public class PresetReverb extends AudioEffect {
    public static final int PARAM_PRESET = 0;
    public static final int PRESET_LARGEHALL = 0;
    public static final int PRESET_LARGEROOM = 0;
    public static final int PRESET_MEDIUMHALL = 0;
    public static final int PRESET_MEDIUMROOM = 0;
    public static final int PRESET_NONE = 0;
    public static final int PRESET_PLATE = 0;
    public static final int PRESET_SMALLROOM = 0;

    public PresetReverb(int p0, int p1) {}

    public short getPreset() { return 0; }
    public Settings getProperties() { return null; }
    public void setParameterListener(Object p0) {}
    public void setPreset(short p0) {}
    public void setProperties(Settings p0) {}
}
