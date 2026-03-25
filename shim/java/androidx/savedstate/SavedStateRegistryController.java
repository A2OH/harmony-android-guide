package androidx.savedstate;

import android.os.Bundle;

/**
 * Controller for SavedStateRegistry — creates and manages the registry.
 */
public class SavedStateRegistryController {

    private final SavedStateRegistryOwner owner;
    private final SavedStateRegistry registry;

    private SavedStateRegistryController(SavedStateRegistryOwner owner) {
        this.owner = owner;
        this.registry = new SavedStateRegistry();
    }

    public static SavedStateRegistryController create(SavedStateRegistryOwner owner) {
        return new SavedStateRegistryController(owner);
    }

    public SavedStateRegistry getSavedStateRegistry() {
        return registry;
    }

    public void performRestore(Bundle savedState) {
        registry.performRestore(savedState);
    }

    public void performSave(Bundle outBundle) {
        registry.performSave(outBundle);
    }
}
