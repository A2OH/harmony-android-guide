package android.service.controls;

import android.app.Service;

/**
 * Android-compatible ControlsProviderService shim. Stub for device-controls provider service.
 */
public abstract class ControlsProviderService extends Service {

    /**
     * Returns a publisher that emits all available controls.
     * Callers should treat the returned Object as a {@code Publisher<Control>}.
     */
    public abstract Object createPublisherForAllAvailable();

    /**
     * Returns a publisher that emits suggested controls.
     * Callers should treat the returned Object as a {@code Publisher<Control>}.
     */
    public abstract Object createPublisherForSuggested();

    /**
     * Performs the given action on the identified control.
     *
     * @param controlId  identifier of the target control
     * @param action     the action to perform (ControlAction or compatible Object)
     * @param consumer   callback that accepts a boolean indicating success
     */
    public abstract void performControlAction(String controlId,
            Object action, Object consumer);
}
