package android.transition;

import java.util.HashMap;
import java.util.Map;

/**
 * Shim for android.transition.TransitionValues.
 * Holds property values captured during a scene transition.
 */
public class TransitionValues {
    /** The view associated with these values (typed as Object for shim portability). */
    public Object view;

    /** Map of property name to captured value. */
    public Map<String, Object> values = new HashMap<>();
}
