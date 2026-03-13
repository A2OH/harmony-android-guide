package android.view.autofill;

/**
 * Android-compatible AutofillManager shim. Provides autofill framework access.
 */
public class AutofillManager {

    // --- Inner abstract callback class ---

    public abstract static class AutofillCallback {
        public static final int EVENT_INPUT_SHOWN    = 1;
        public static final int EVENT_INPUT_HIDDEN   = 2;
        public static final int EVENT_INPUT_UNAVAILABLE = 3;

        public abstract void onAutofillEvent(Object view, AutofillId id, int event);
    }

    // --- State queries ---

    public boolean isEnabled() {
        return false;
    }

    public boolean isAutofillSupported() {
        return false;
    }

    // --- Actions ---

    public void requestAutofill(Object view) {
        // stub — autofill not supported on OpenHarmony
    }

    public void commit() {
        // stub
    }

    public void cancel() {
        // stub
    }

    public void notifyValueChanged(Object view) {
        // stub
    }

    // --- Callback registration ---

    public void registerCallback(AutofillCallback callback) {
        // stub
    }

    public void unregisterCallback(AutofillCallback callback) {
        // stub
    }
}
