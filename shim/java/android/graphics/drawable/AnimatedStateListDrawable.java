package android.graphics.drawable;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible AnimatedStateListDrawable shim. Extends StateListDrawable
 * with the ability to register animated transitions between states.
 */
public class AnimatedStateListDrawable extends StateListDrawable {

    private final java.util.List<TransitionEntry> mTransitions = new java.util.ArrayList<>();

    public AnimatedStateListDrawable() {
        super();
    }

    /**
     * Adds a new state drawable with an optional key id for referencing in
     * transitions.
     *
     * @param stateSet the set of states that activate this drawable
     * @param drawable the drawable to display
     * @param id       an identifier for use with {@link #addTransition}
     */
    public void addState(int[] stateSet, Drawable drawable, int id) {
        super.addState(stateSet, drawable);
        // id is stored implicitly by index; a real implementation would map id→index.
    }

    /**
     * Adds a transition animation between two states identified by their ids.
     *
     * @param fromId     id of the source state drawable
     * @param toId       id of the destination state drawable
     * @param transition the animated drawable to play during the transition
     * @param reversible whether the transition can play in reverse
     */
    public void addTransition(int fromId, int toId, Drawable transition,
                              boolean reversible) {
        mTransitions.add(new TransitionEntry(fromId, toId, transition, reversible));
    }

    // ---------------------------------------------------------------

    private static final class TransitionEntry {
        final int fromId;
        final int toId;
        final Drawable transition;
        final boolean reversible;

        TransitionEntry(int fromId, int toId, Drawable transition, boolean reversible) {
            this.fromId = fromId;
            this.toId = toId;
            this.transition = transition;
            this.reversible = reversible;
        }
    }
}
