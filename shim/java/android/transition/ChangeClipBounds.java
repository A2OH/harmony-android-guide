package android.transition;

/**
 * Android-compatible ChangeClipBounds transition shim.
 * Captures the clip bounds before and after the scene change and animates
 * between them. Stub — no animation is performed on OpenHarmony.
 */
public class ChangeClipBounds extends Transition {

    public ChangeClipBounds() {}

    @Override
    public void captureStartValues(TransitionValues transitionValues) {}

    @Override
    public void captureEndValues(TransitionValues transitionValues) {}
}
