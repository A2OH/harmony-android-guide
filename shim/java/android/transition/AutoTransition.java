package android.transition;

/**
 * Android-compatible AutoTransition shim.
 * Extends TransitionSet and adds Fade+ChangeBounds by default, matching
 * the Android behavior of: fade out, change bounds, fade in (sequential).
 */
public class AutoTransition extends TransitionSet {

    public AutoTransition() {
        setOrdering(ORDERING_SEQUENTIAL);
        addTransition(new Fade(Fade.OUT))
            .addTransition(new ChangeBounds())
            .addTransition(new Fade(Fade.IN));
    }
}
