package android.media.tv;

/**
 * Android-compatible TvInputInfo shim. Stub for TV input service metadata.
 */
public class TvInputInfo {
    public static final int TYPE_TUNER     = 0;
    public static final int TYPE_OTHER     = 1000;
    public static final int TYPE_COMPOSITE = 1001;
    public static final int TYPE_SVIDEO    = 1002;
    public static final int TYPE_SCART     = 1003;
    public static final int TYPE_COMPONENT = 1004;
    public static final int TYPE_VGA       = 1005;
    public static final int TYPE_DVI       = 1006;
    public static final int TYPE_HDMI      = 1007;

    private String mId;
    private int mType = TYPE_TUNER;
    private boolean mPassthrough;

    public TvInputInfo() {}

    public String getId() {
        return mId;
    }

    /** Returns a stub ServiceInfo-like Object (real type not shimmed). */
    public Object getServiceInfo() {
        return null;
    }

    public int getType() {
        return mType;
    }

    public boolean isPassthroughInput() {
        return mPassthrough;
    }

    public CharSequence loadLabel(Object context) {
        return mId != null ? mId : "TvInput";
    }
}
