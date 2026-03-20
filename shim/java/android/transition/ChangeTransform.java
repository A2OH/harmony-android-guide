package android.transition;

/**
 * Android-compatible ChangeTransform transition shim.
 * Captures scale, rotation, and translation for views before and after the
 * scene change and animates those changes.
 * Stub — no animation is performed on OpenHarmony.
 */
public class ChangeTransform extends Transition {

    private boolean mReparent = true;
    private boolean mReparentWithOverlay = true;

    public ChangeTransform() {
    }

    public void setReparent(boolean reparent) {
        mReparent = reparent;
    }

    public boolean getReparent() {
        return mReparent;
    }

    public void setReparentWithOverlay(boolean reparentWithOverlay) {
        mReparentWithOverlay = reparentWithOverlay;
    }

    public boolean getReparentWithOverlay() {
        return mReparentWithOverlay;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {}

    @Override
    public void captureEndValues(TransitionValues transitionValues) {}
}
