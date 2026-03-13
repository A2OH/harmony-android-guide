package android.media.audiofx;
import android.provider.Settings;
import android.provider.Settings;

public class EnvironmentalReverb extends AudioEffect {
    public static final int PARAM_DECAY_HF_RATIO = 0;
    public static final int PARAM_DECAY_TIME = 0;
    public static final int PARAM_DENSITY = 0;
    public static final int PARAM_DIFFUSION = 0;
    public static final int PARAM_REFLECTIONS_DELAY = 0;
    public static final int PARAM_REFLECTIONS_LEVEL = 0;
    public static final int PARAM_REVERB_DELAY = 0;
    public static final int PARAM_REVERB_LEVEL = 0;
    public static final int PARAM_ROOM_HF_LEVEL = 0;
    public static final int PARAM_ROOM_LEVEL = 0;

    public EnvironmentalReverb(int p0, int p1) {}

    public short getDecayHFRatio() { return 0; }
    public int getDecayTime() { return 0; }
    public short getDensity() { return 0; }
    public short getDiffusion() { return 0; }
    public Settings getProperties() { return null; }
    public int getReflectionsDelay() { return 0; }
    public short getReflectionsLevel() { return 0; }
    public int getReverbDelay() { return 0; }
    public short getReverbLevel() { return 0; }
    public short getRoomHFLevel() { return 0; }
    public short getRoomLevel() { return 0; }
    public void setDecayHFRatio(short p0) {}
    public void setDecayTime(int p0) {}
    public void setDensity(short p0) {}
    public void setDiffusion(short p0) {}
    public void setParameterListener(Object p0) {}
    public void setProperties(Settings p0) {}
    public void setReflectionsDelay(int p0) {}
    public void setReflectionsLevel(short p0) {}
    public void setReverbDelay(int p0) {}
    public void setReverbLevel(short p0) {}
    public void setRoomHFLevel(short p0) {}
    public void setRoomLevel(short p0) {}
}
