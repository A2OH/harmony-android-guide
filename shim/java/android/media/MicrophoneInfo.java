package android.media;
import java.util.List;

public final class MicrophoneInfo {
    public static final int CHANNEL_MAPPING_DIRECT = 0;
    public static final int CHANNEL_MAPPING_PROCESSED = 0;
    public static final int DIRECTIONALITY_BI_DIRECTIONAL = 0;
    public static final int DIRECTIONALITY_CARDIOID = 0;
    public static final int DIRECTIONALITY_HYPER_CARDIOID = 0;
    public static final int DIRECTIONALITY_OMNI = 0;
    public static final int DIRECTIONALITY_SUPER_CARDIOID = 0;
    public static final int DIRECTIONALITY_UNKNOWN = 0;
    public static final int GROUP_UNKNOWN = 0;
    public static final int INDEX_IN_THE_GROUP_UNKNOWN = 0;
    public static final int LOCATION_MAINBODY = 0;
    public static final int LOCATION_MAINBODY_MOVABLE = 0;
    public static final int LOCATION_PERIPHERAL = 0;
    public static final int LOCATION_UNKNOWN = 0;
    public static final int ORIENTATION_UNKNOWN = 0;
    public static final int POSITION_UNKNOWN = 0;
    public static final int SENSITIVITY_UNKNOWN = 0;
    public static final int SPL_UNKNOWN = 0;

    public MicrophoneInfo() {}

    public List<?> getChannelMapping() { return null; }
    public String getDescription() { return null; }
    public int getDirectionality() { return 0; }
    public List<?> getFrequencyResponse() { return null; }
    public int getGroup() { return 0; }
    public int getId() { return 0; }
    public int getIndexInTheGroup() { return 0; }
    public int getLocation() { return 0; }
    public float getMaxSpl() { return 0f; }
    public float getMinSpl() { return 0f; }
    public Object getOrientation() { return null; }
    public Object getPosition() { return null; }
    public float getSensitivity() { return 0f; }
    public int getType() { return 0; }
}
