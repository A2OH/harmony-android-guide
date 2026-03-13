package android.animation;

/**
 * Shim: android.animation.ObjectAnimator — pure Java stub.
 *
 * Extends ValueAnimator with a target object and property name.
 * Actual property reflection/setter invocation is not performed in the shim;
 * the bridge layer drives real ArkUI property animations where supported.
 */
public class ObjectAnimator extends ValueAnimator {

    private Object mTarget;
    private String mPropertyName;

    // ── Factory methods ──

    public static ObjectAnimator ofFloat(Object target, String propertyName, float... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.mTarget = target;
        anim.mPropertyName = propertyName;
        anim.mValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            anim.mValues[i] = values[i];
        }
        if (values.length > 0) {
            anim.mAnimatedValue = values[0];
        }
        return anim;
    }

    public static ObjectAnimator ofInt(Object target, String propertyName, int... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.mTarget = target;
        anim.mPropertyName = propertyName;
        anim.mValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            anim.mValues[i] = values[i];
        }
        if (values.length > 0) {
            anim.mAnimatedValue = values[0];
        }
        return anim;
    }

    // ── Target / property accessors ──

    public Object getTarget()       { return mTarget; }
    public String getPropertyName() { return mPropertyName; }

    public void setTarget(Object target)           { mTarget = target; }
    public void setPropertyName(String propertyName) { mPropertyName = propertyName; }

    @Override
    public String toString() {
        return "ObjectAnimator{target=" + mTarget
                + ", property=" + mPropertyName
                + ", duration=" + getDuration() + "ms}";
    }
}
