package android.view;

/**
 * Android-compatible InputDevice shim.
 * Represents an input device (keyboard, touchscreen, mouse, gamepad, etc.).
 */
public class InputDevice {

    // ── Source constants ──

    public static final int SOURCE_UNKNOWN       = 0x00000000;
    public static final int SOURCE_KEYBOARD      = 0x00000101;
    public static final int SOURCE_DPAD          = 0x00000201;
    public static final int SOURCE_GAMEPAD       = 0x00000401;
    public static final int SOURCE_TOUCHSCREEN   = 0x00001002;
    public static final int SOURCE_MOUSE         = 0x00002002;
    public static final int SOURCE_JOYSTICK      = 0x01000010;
    public static final int SOURCE_ROTARY_ENCODER = 0x00400000;

    // Source class constants
    public static final int SOURCE_CLASS_MASK     = 0x000000ff;
    public static final int SOURCE_CLASS_NONE     = 0x00000000;
    public static final int SOURCE_CLASS_BUTTON   = 0x00000001;
    public static final int SOURCE_CLASS_POINTER  = 0x00000002;
    public static final int SOURCE_CLASS_TRACKBALL= 0x00000004;
    public static final int SOURCE_CLASS_POSITION = 0x00000008;
    public static final int SOURCE_CLASS_JOYSTICK = 0x00000010;

    private final int mId;
    private final String mName;
    private final int mSources;
    private final boolean mVirtual;

    private InputDevice(int id, String name, int sources, boolean virtual) {
        mId = id;
        mName = name;
        mSources = sources;
        mVirtual = virtual;
    }

    /**
     * Returns the InputDevice with the given ID, or null if not found.
     * Stub: always returns null.
     */
    public static InputDevice getDevice(int id) {
        return null;
    }

    public int getId() { return mId; }
    public String getName() { return mName; }
    public int getSources() { return mSources; }
    public boolean isVirtual() { return mVirtual; }

    /**
     * Returns the MotionRange for the given axis, or null if unsupported.
     * Stub: always returns null.
     */
    public MotionRange getMotionRange(int axis) {
        return null;
    }

    // ── MotionRange inner class ──

    public static final class MotionRange {
        private final float mMin;
        private final float mMax;
        private final float mFlat;
        private final float mFuzz;

        public MotionRange(float min, float max, float flat, float fuzz) {
            mMin = min;
            mMax = max;
            mFlat = flat;
            mFuzz = fuzz;
        }

        public float getMin() { return mMin; }
        public float getMax() { return mMax; }
        public float getRange() { return mMax - mMin; }
        public float getFlat() { return mFlat; }
        public float getFuzz() { return mFuzz; }
    }
}
