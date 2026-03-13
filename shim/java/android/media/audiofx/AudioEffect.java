package android.media.audiofx;

import java.util.UUID;

/**
 * Android-compatible AudioEffect shim. Abstract base stub for audio effects.
 */
public class AudioEffect {
    public AudioEffect(Object... args) {}
    public static final int SUCCESS              =  0;
    public static final int ERROR                = -1;
    public static final int ALREADY_EXISTS       = -2;
    public static final int ERROR_NO_MEMORY      = -12;
    public static final int ERROR_BAD_VALUE      = -4;
    public static final int ERROR_INVALID_OPERATION = -5;
    public static final int ERROR_DEAD_OBJECT    = -7;

    public static final int EFFECT_INSERT        = 0;
    public static final int EFFECT_AUXILIARY     = 1;
    public static final int EFFECT_PRE_PROCESSING = 2;

    private int     mId;
    private boolean mEnabled;
    private boolean mHasControl;
    private boolean mReleased;

    protected AudioEffect(UUID type, UUID uuid, int priority, int audioSession) {
        mId         = System.identityHashCode(this);
        mEnabled    = false;
        mHasControl = true;
    }

    public int     getId()        { return mId; }
    public boolean getEnabled()   { return mEnabled && !mReleased; }
    public boolean hasControl()   { return mHasControl && !mReleased; }

    public int setEnabled(boolean enabled) {
        if (mReleased) return ERROR_INVALID_OPERATION;
        mEnabled = enabled;
        return SUCCESS;
    }

    public void release() {
        mReleased   = true;
        mEnabled    = false;
        mHasControl = false;
    }

    protected void checkNotReleased() {
        if (mReleased) throw new IllegalStateException("AudioEffect already released");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + mId + ", enabled=" + mEnabled + "}";
    }

    // -----------------------------------------------------------------------
    public static final class Descriptor {
        public String type;
        public String uuid;
        public String connectType;
        public String name;
        public String implementor;

        public Descriptor() {}

        public Descriptor(String type, String uuid, String connectType,
                String name, String implementor) {
            this.type        = type;
            this.uuid        = uuid;
            this.connectType = connectType;
            this.name        = name;
            this.implementor = implementor;
        }

        @Override
        public String toString() {
            return "Descriptor{type=" + type + ", uuid=" + uuid + ", name=" + name + "}";
        }
    }

    // -----------------------------------------------------------------------
    public interface OnEnableStatusChangeListener {
        void onEnableStatusChange(AudioEffect effect, boolean enabled);
    }

    public interface OnControlStatusChangeListener {
        void onControlStatusChange(AudioEffect effect, boolean controlGranted);
    }
}
