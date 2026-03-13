package android.media;

public class VolumeProvider {
    public static final int VOLUME_CONTROL_ABSOLUTE = 0;
    public static final int VOLUME_CONTROL_FIXED = 0;
    public static final int VOLUME_CONTROL_RELATIVE = 0;

    public VolumeProvider(int p0, int p1, int p2) {}
    public VolumeProvider(int p0, int p1, int p2, String p3) {}

    public int getCurrentVolume() { return 0; }
    public int getMaxVolume() { return 0; }
    public int getVolumeControl() { return 0; }
    public void onAdjustVolume(int p0) {}
    public void onSetVolumeTo(int p0) {}
    public void setCurrentVolume(int p0) {}
}
