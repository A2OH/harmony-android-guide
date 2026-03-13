package android.view.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Android-compatible AnimationSet shim. Composes multiple animations that run
 * together, optionally sharing a common interpolator.
 */
public class AnimationSet extends Animation {

    private final boolean mShareInterpolator;
    private final List<Animation> mAnimations = new ArrayList<>();

    /**
     * @param shareInterpolator if true, all child animations use this set's
     *                          interpolator instead of their own
     */
    public AnimationSet(boolean shareInterpolator) {
        mShareInterpolator = shareInterpolator;
    }

    /**
     * Adds an animation to the set.
     */
    public void addAnimation(Animation a) {
        if (a != null) mAnimations.add(a);
    }

    /**
     * Returns an unmodifiable view of the child animations.
     */
    public List<Animation> getAnimations() {
        return java.util.Collections.unmodifiableList(mAnimations);
    }

    public boolean willShareInterpolator() {
        return mShareInterpolator;
    }
}
