package android.hardware.camera2.params;

public final class RecommendedStreamConfigurationMap {
    public static final int USECASE_LOW_LATENCY_SNAPSHOT = 0;
    public static final int USECASE_PREVIEW = 0;
    public static final int USECASE_RAW = 0;
    public static final int USECASE_RECORD = 0;
    public static final int USECASE_SNAPSHOT = 0;
    public static final int USECASE_VIDEO_SNAPSHOT = 0;
    public static final int USECASE_ZSL = 0;

    public RecommendedStreamConfigurationMap() {}

    public int getRecommendedUseCase() { return 0; }
    public boolean isOutputSupportedFor(int p0) { return false; }
}
