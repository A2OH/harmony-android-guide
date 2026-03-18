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

        public void onAutofillEvent(Object view, AutofillId id, int event) {}
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

    // --- Object registration ---

    public void registerCallback(AutofillCallback callback) {
        // stub
    }

    public void unregisterCallback(AutofillCallback callback) {
        // stub
    }

    // Methods needed for View.java compilation
    public void notifyViewEntered(android.view.View view) {}
    public void notifyViewExited(android.view.View view) {}
    public void notifyViewClicked(android.view.View view) {}
    public void notifyViewVisibilityChanged(android.view.View view, boolean isVisible) {}
    public void notifyViewEnteredForAugmentedAutofill(android.view.View view) {}
}
