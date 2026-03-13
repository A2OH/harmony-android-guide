package android.animation;

/**
 * Android-compatible Keyframe shim.
 * Represents a single key frame in an animation. Pure Java stub.
 */
public abstract class Keyframe {

    protected float mFraction;
    protected Object mValue;
    protected boolean mHasValue;

    private Keyframe() {
    }

    // ── Factory methods ──

    public static Keyframe ofFloat(float fraction) {
        return new FloatKeyframe(fraction);
    }

    public static Keyframe ofFloat(float fraction, float value) {
        FloatKeyframe kf = new FloatKeyframe(fraction);
        kf.mValue = value;
        kf.mHasValue = true;
        return kf;
    }

    public static Keyframe ofInt(float fraction) {
        return new IntKeyframe(fraction);
    }

    public static Keyframe ofInt(float fraction, int value) {
        IntKeyframe kf = new IntKeyframe(fraction);
        kf.mValue = value;
        kf.mHasValue = true;
        return kf;
    }

    public static Keyframe ofObject(float fraction) {
        return new ObjectKeyframe(fraction);
    }

    public static Keyframe ofObject(float fraction, Object value) {
        ObjectKeyframe kf = new ObjectKeyframe(fraction);
        kf.mValue = value;
        kf.mHasValue = (value != null);
        return kf;
    }

    // ── Accessors ──

    public float getFraction() {
        return mFraction;
    }

    public void setFraction(float fraction) {
        mFraction = fraction;
    }

    public Object getValue() {
        return mValue;
    }

    public void setValue(Object value) {
        mValue = value;
        mHasValue = (value != null);
    }

    public boolean hasValue() {
        return mHasValue;
    }

    public abstract Class getType();

    // ── Concrete subclasses ──

    private static class FloatKeyframe extends Keyframe {
        FloatKeyframe(float fraction) {
            mFraction = fraction;
        }
        @Override
        public Class getType() { return float.class; }
    }

    private static class IntKeyframe extends Keyframe {
        IntKeyframe(float fraction) {
            mFraction = fraction;
        }
        @Override
        public Class getType() { return int.class; }
    }

    private static class ObjectKeyframe extends Keyframe {
        ObjectKeyframe(float fraction) {
            mFraction = fraction;
        }
        @Override
        public Class getType() { return Object.class; }
    }
}
