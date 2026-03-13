package android.transition;

import android.view.View;
import java.util.HashMap;

/**
 * Android-compatible TransitionValues shim.
 * Data structure holding per-view values captured by transitions before
 * and after a scene change. Contains the target view and a map of
 * property-name to property-value pairs.
 */
public class TransitionValues {

    /** The View with these values. */
    public View view;

    /** The set of values tracked by transitions. */
    public final HashMap<String, Object> values = new HashMap<>();
}
