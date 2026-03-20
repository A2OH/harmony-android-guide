package android.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Stub for android.transition.TransitionUtils.
 * Utility methods used by transition classes.
 */
class TransitionUtils {

    static View copyViewImage(ViewGroup sceneRoot, View view, View parent) {
        // Stub: return the original view
        return view;
    }

    static Animator mergeAnimators(Animator animator1, Animator animator2) {
        if (animator1 == null) return animator2;
        if (animator2 == null) return animator1;
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator1, animator2);
        return set;
    }
}
