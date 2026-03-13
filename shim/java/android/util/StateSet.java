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
