package android.transition;
import android.widget.ImageView;
import android.widget.ImageView;

/**
 * Android-compatible ChangeImageTransform transition shim.
 * Captures an ImageView's matrix before and after the scene change and
 * animates it. Stub — no animation is performed on OpenHarmony.
 */
public class ChangeImageTransform extends Transition {

    public ChangeImageTransform() {}

    @Override
    public void captureStartValues(TransitionValues transitionValues) {}

    @Override
    public void captureEndValues(TransitionValues transitionValues) {}
}
