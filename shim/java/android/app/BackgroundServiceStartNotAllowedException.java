package android.app;

/**
 * Shim: android.app.BackgroundServiceStartNotAllowedException (API 31+)
 * Tier 1 — exception thrown when an app tries to start a background service
 * while it is not allowed to do so (e.g. the app targets API 31+ and is not
 * in an allowed state to start background services).
 *
 * OH mapping: OpenHarmony does not have an exact equivalent of Android's
 * background-service restriction.  ServiceExtensionAbility on OH is always
 * started explicitly via startServiceExtensionAbility(); the closest
 * analogue is error code 16000011 (context does not exist / not allowed)
 * returned when the caller lacks the required state or permission.
 */
public class BackgroundServiceStartNotAllowedException extends IllegalStateException {

    /**
     * Creates the exception with no detail message.
     */
    public BackgroundServiceStartNotAllowedException() {
        super();
    }

    /**
     * Creates the exception with the supplied detail message.
     *
     * @param message detail message
     */
    public BackgroundServiceStartNotAllowedException(String message) {
        super(message);
    }
}
