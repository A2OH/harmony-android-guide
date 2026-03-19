package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Rect;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.graphics.drawable.StateListDrawable
 * OH mapping: state-driven drawable selection (pressed, focused, etc.)
 *
 * Maps View state sets (pressed, focused, enabled, checked, etc.) to
 * different Drawables. The first matching entry wins when the state is
 * evaluated — matching AOSP's StateListDrawable behavior. Empty state spec
 * acts as a wildcard (default/fallback drawable).
 */
public class StateListDrawable extends Drawable {

    // ── State entry ──────────────────────────────────────────────────────────

    public static final class StateEntry {
        final int[]    stateSet;
        final Drawable drawable;

        StateEntry(int[] stateSet, Drawable drawable) {
            this.stateSet = stateSet;
            this.drawable = drawable;
        }
    }

    // ── State ────────────────────────────────────────────────────────────────

    private final List<StateEntry> entries     = new ArrayList<StateEntry>();
    private       int[]            currentState = new int[0];
    private       int              alpha        = 0xFF;
    private       Drawable         lastSelected = null;

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

    /**
     * Returns the number of state entries.
     */
    public int getStateCount() {
        return entries.size();
    }

    /**
     * Returns the state set at the given index.
     */
    public int[] getStateSet(int index) {
        if (index < 0 || index >= entries.size()) return null;
        return entries.get(index).stateSet;
    }

    /**
     * Returns the drawable at the given index.
     */
    public Drawable getStateDrawable(int index) {
        if (index < 0 || index >= entries.size()) return null;
        return entries.get(index).drawable;
    }

    // ── Current state ────────────────────────────────────────────────────────

    /**
     * Selects a drawable based on the provided state set.
     * Returns true if the selected drawable changed.
     */
    public boolean setState(int[] stateSet) {
        this.currentState = stateSet != null ? stateSet : new int[0];
        Drawable newSelected = findMatchingDrawable();
        boolean changed = (newSelected != lastSelected);
        lastSelected = newSelected;
        return changed;
    }

    public int[] getState() {
        return currentState;
    }

    /**
     * Returns the drawable that matches the current state, or null.
     */
    public Drawable getCurrent() {
        if (lastSelected == null) {
            lastSelected = findMatchingDrawable();
        }
        return lastSelected;
    }

    /**
     * Always returns true — a StateListDrawable is inherently stateful.
     */
    public boolean isStateful() {
        return true;
    }

    // ── Alpha ────────────────────────────────────────────────────────────────

    @Override
    public int getAlpha() { return alpha; }

    @Override
    public void setAlpha(int alpha) { this.alpha = alpha & 0xFF; }

    // ── Intrinsic size — delegates to current drawable ───────────────────────

    @Override
    public int getIntrinsicWidth() {
        Drawable current = getCurrent();
        return current != null ? current.getIntrinsicWidth() : -1;
    }

    @Override
    public int getIntrinsicHeight() {
        Drawable current = getCurrent();
        return current != null ? current.getIntrinsicHeight() : -1;
    }

    // ── Draw — delegates to current drawable, applying bounds ────────────────

    @Override
    public void draw(Canvas canvas) {
        Drawable current = getCurrent();
        if (current != null) {
            current.setBounds(getBounds());
            current.draw(canvas);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Drawable findMatchingDrawable() {
        for (int i = 0; i < entries.size(); i++) {
            StateEntry e = entries.get(i);
            if (stateMatches(e.stateSet, currentState)) return e.drawable;
        }
        return null;
    }

    static boolean stateMatches(int[] stateSpec, int[] stateSet) {
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

    public int findStateDrawableIndex(int[] stateSet) {
        for (int i = 0; i < entries.size(); i++) {
            if (java.util.Arrays.equals(entries.get(i).stateSet, stateSet)) return i;
        }
        return -1;
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "StateListDrawable(entries=" + entries.size() + ")";
    }
}
