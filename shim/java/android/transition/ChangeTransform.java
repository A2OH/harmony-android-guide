package android.transition;

/**
 * Android-compatible ChangeTransform transition shim.
 * Captures scale and rotation for views before and after the scene change
 * and animates those changes. Stub — no animation is performed on OpenHarmony.
 */
public class ChangeTransform extends Transition {

    private boolean mReparent = true;

    public ChangeTransform() {}

    public boolean getReparent() {
        return mReparent;
    }

    public void setReparent(boolean reparent) {
        mReparent = reparent;
    }
}
