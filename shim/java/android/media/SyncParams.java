package android.media;

public final class SyncParams {
    public static final int AUDIO_ADJUST_MODE_DEFAULT = 0;
    public static final int AUDIO_ADJUST_MODE_RESAMPLE = 0;
    public static final int AUDIO_ADJUST_MODE_STRETCH = 0;
    public static final int SYNC_SOURCE_AUDIO = 0;
    public static final int SYNC_SOURCE_DEFAULT = 0;
    public static final int SYNC_SOURCE_SYSTEM_CLOCK = 0;
    public static final int SYNC_SOURCE_VSYNC = 0;

    public SyncParams() {}

    public SyncParams allowDefaults() { return null; }
    public int getAudioAdjustMode() { return 0; }
    public float getFrameRate() { return 0f; }
    public int getSyncSource() { return 0; }
    public float getTolerance() { return 0f; }
    public SyncParams setAudioAdjustMode(int p0) { return null; }
    public SyncParams setFrameRate(float p0) { return null; }
    public SyncParams setSyncSource(int p0) { return null; }
    public SyncParams setTolerance(float p0) { return null; }
}
