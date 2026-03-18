package android.util;

/**
 * Android-compatible StateSet shim.
 * Utility for matching drawable state sets.
 */
public final class StateSet {

    /**
     * The wild card state set: matches any state set.
     */
    public static final int[] WILD_CARD = new int[0];

    /**
     * The "nothing" state set: never matches any state set.
     */
    public static final int[] NOTHING = new int[] { 0 };

    // View state bit indices
    public static final int VIEW_STATE_WINDOW_FOCUSED = 0;
    public static final int VIEW_STATE_SELECTED = 1;
    public static final int VIEW_STATE_FOCUSED = 2;
    public static final int VIEW_STATE_ENABLED = 3;
    public static final int VIEW_STATE_PRESSED = 4;
    public static final int VIEW_STATE_ACTIVATED = 5;
    public static final int VIEW_STATE_ACCELERATED = 6;
    public static final int VIEW_STATE_HOVERED = 7;
    public static final int VIEW_STATE_DRAG_CAN_ACCEPT = 8;
    public static final int VIEW_STATE_DRAG_HOVERED = 9;

    private static final int[][] VIEW_STATE_SETS;
    static {
        VIEW_STATE_SETS = new int[1 << 10][];
        for (int i = 0; i < VIEW_STATE_SETS.length; i++) {
            int count = Integer.bitCount(i);
            int[] stateSet = new int[count];
            int idx = 0;
            for (int bit = 0; bit < 10; bit++) {
                if ((i & (1 << bit)) != 0) {
                    stateSet[idx++] = bit;
                }
            }
            VIEW_STATE_SETS[i] = stateSet;
        }
    }

    public static int[] get(int mask) {
        if (mask >= 0 && mask < VIEW_STATE_SETS.length) {
            return VIEW_STATE_SETS[mask];
        }
        return NOTHING;
    }

    private StateSet() {}

    /**
     * Return true if the stateSpec is satisfied by the stateSet, i.e. stateSet
     * contains all the states in stateSpec that are positive, and none of the
     * states in stateSpec that are negative (prefixed with minus).
     */
    public static boolean stateSetMatches(int[] stateSpec, int[] stateSet) {
        if (stateSet == NOTHING) {
            return (stateSpec == null || stateSpec.length == 0);
        }
        if (stateSpec == null || stateSpec.length == 0) {
            return true; // WILD_CARD matches everything
        }
        for (int specState : stateSpec) {
            if (specState == 0) {
                break; // end of spec
            }
            boolean mustMatch = (specState > 0);
            int lookFor = mustMatch ? specState : -specState;
            boolean found = false;
            if (stateSet != null) {
                for (int s : stateSet) {
                    if (s == lookFor) {
                        found = true;
                        break;
                    }
                }
            }
            if (mustMatch != found) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true if the stateSpec is satisfied by the single state.
     */
    public static boolean stateSetMatches(int[] stateSpec, int state) {
        if (stateSpec == null || stateSpec.length == 0) {
            return true;
        }
        for (int specState : stateSpec) {
            if (specState == 0) {
                break;
            }
            if (specState > 0) {
                if (specState == state) continue;
                return false;
            } else {
                if (-specState == state) return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the array is the WILD_CARD (an empty or null int array).
     */
    public static boolean isWildCard(int[] stateSetOrSpec) {
        return stateSetOrSpec == null || stateSetOrSpec.length == 0;
    }

    /**
     * Trims the state set to remove trailing 0 entries.
     */
    public static int[] trimStateSet(int[] states, int newSize) {
        if (states.length == newSize) {
            return states;
        }
        int[] trimmedStates = new int[newSize];
        System.arraycopy(states, 0, trimmedStates, 0, newSize);
        return trimmedStates;
    }
}
