package android.app;

import java.util.List;
import java.util.Map;

/**
 * Android-compatible SharedElementCallback shim.
 * Provides callbacks for customising shared element transitions between activities.
 * Stub implementation — no actual transitions in the shim layer.
 */
public abstract class SharedElementCallback {

    /**
     * Called at the start of a shared element transition; allows modification of the
     * initial state of the shared elements.
     *
     * @param sharedElementNames  names of the shared elements
     * @param sharedElements      shared element Views (typed as Object to avoid dependency)
     * @param sharedElementSnapshots snapshot Views (typed as Object)
     */
    public void onSharedElementStart(List<String> sharedElementNames,
            List<Object> sharedElements,
            List<Object> sharedElementSnapshots) {}

    /**
     * Called at the end of a shared element transition; allows modification of the
     * final state of the shared elements.
     *
     * @param sharedElementNames  names of the shared elements
     * @param sharedElements      shared element Views (typed as Object)
     * @param sharedElementSnapshots snapshot Views (typed as Object)
     */
    public void onSharedElementEnd(List<String> sharedElementNames,
            List<Object> sharedElements,
            List<Object> sharedElementSnapshots) {}

    /**
     * Called when a shared element is not found in the entering Activity.
     *
     * @param rejectedSharedElements the list of transition names whose Views were not found
     */
    public void onRejectSharedElements(List<Object> rejectedSharedElements) {}

    /**
     * Lets the callback override the mapping of shared element transition names to Views.
     *
     * @param names    in/out list of shared element transition names
     * @param sharedElements in/out map from transition name to View (Object)
     */
    public void onMapSharedElements(List<String> names, Map<String, Object> sharedElements) {}

    /**
     * Creates a snapshot of a shared element to be used during the transition.
     *
     * @param sharedElement   the shared element View (Object)
     * @param viewToGlobalMatrix  matrix from local to global coordinates (Object)
     * @param screenBounds    bounding rectangle in screen coordinates (Object)
     * @return a snapshot View, or {@code null} to use the default behaviour
     */
    public Object onCaptureSharedElementSnapshot(Object sharedElement,
            Object viewToGlobalMatrix, Object screenBounds) {
        return null;
    }

    /**
     * Creates a View from a previously captured snapshot.
     *
     * @param context   context (typed as Object to avoid dependency)
     * @param snapshot  the snapshot previously returned by
     *                  {@link #onCaptureSharedElementSnapshot}
     * @return a View representing the snapshot, or {@code null}
     */
    public Object onCreateSnapshotView(Object context, Object snapshot) {
        return null;
    }

    /**
     * Called after the shared elements have arrived at the destination and the
     * shared element transition is about to start.
     *
     * @param sharedElementNames names of the shared elements
     * @param sharedElements     the shared element Views (Object)
     * @param listener           callback to signal when the app is ready to start (Object)
     */
    public void onSharedElementsArrived(List<String> sharedElementNames,
            List<Object> sharedElements, Object listener) {
        // Default: immediately signal ready if listener is OnSharedElementsReadyListener
        // In the shim layer there are no real transitions; nothing to do.
    }
}
