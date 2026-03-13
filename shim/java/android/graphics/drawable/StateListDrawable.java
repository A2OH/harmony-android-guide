package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Canvas;

import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.graphics.drawable.StateListDrawable
 * OH mapping: state-driven drawable selection (pressed, focused, etc.)
 *
 * Pure Java stub — stores state/drawable pairs; draw() delegates to the
 * currently selected drawable (if any).
 */
public class StateListDrawable extends Drawable {

    // ── State entry ──────────────────────────────────────────────────────────

    private static final class StateEntry {
        final int[]    stateSet;
        final Drawable drawable;

        StateEntry(int[] stateSet, Drawable drawable) {
            this.stateSet = stateSet;
            this.drawable = drawable;
        }
    }

    // ── State ────────────────────────────────────────────────────────────────

    private final List<StateEntry> entries     = new ArrayList<>();
    private       int[]            currentState = new int[0];
    private       int              alpha        = 0xFF;

    // ── Constructors ─────────────────────────────────────────────────────────

    public StateListDrawable() {}

    // ── State entries ────────────────────────────────────────────────────────

    /**
     * Adds a new state/drawable pair.
     * The first matching entry wins when the state is evaluated.
     *
     * @param stateSet  array of state attributes (e.g. android.R.attr.state_pressed).
     *                  Negative values mean "not in that state".
     * @param drawable  drawable to use for this state
     */
    public void addState(int[] stateSet, Drawable drawable) {
        entries.add(new StateEntry(stateSet, drawable));
    }

    // ── Current state ────────────────────────────────────────────────────────

    /**
     * Selects a drawable based on the provided state set.
     * Returns true if the selected drawable changed.
     */
    public boolean setState(int[] stateSet) {
        this.currentState = stateSet != null ? stateSet : new int[0];
        return true;
    }

    public int[] getState() {
        return currentState;
    }

    /**
     * Returns the drawable that matches the current state, or null.
     */
    public Drawable getCurrent() {
        for (StateEntry e : entries) {
            if (stateMatches(e.stateSet, currentState)) return e.drawable;
        }
        return null;
    }

    // ── Alpha ────────────────────────────────────────────────────────────────

    @Override
    public int getAlpha() { return alpha; }

    @Override
    public void setAlpha(int alpha) { this.alpha = alpha & 0xFF; }

    // ── Draw ─────────────────────────────────────────────────────────────────

    @Override
    public void draw(Canvas canvas) {
        Drawable current = getCurrent();
        if (current != null) current.draw(canvas);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static boolean stateMatches(int[] stateSpec, int[] stateSet) {
        if (stateSpec == null || stateSpec.length == 0) return true;
        if (stateSet == null) return false;
        for (int spec : stateSpec) {
            boolean wanted = (spec > 0);
            int     attr   = wanted ? spec : -spec;
            boolean found  = false;
            for (int s : stateSet) {
                if (s == attr) { found = true; break; }
            }
            if (found != wanted) return false;
        }
        return true;
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "StateListDrawable(entries=" + entries.size() + ")";
    }
}
