package android.content;

/**
 * Android-compatible ActivityNotFoundException shim.
 * Thrown when startActivity() cannot find an Activity to handle the Intent.
 */
public class ActivityNotFoundException extends RuntimeException {

    public ActivityNotFoundException() {
        super();
    }

    public ActivityNotFoundException(String message) {
        super(message);
    }
}
