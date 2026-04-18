package androidx.savedstate;

import android.os.Bundle;

public final class SavedStateRegistryController {
    private final SavedStateRegistryOwner mOwner;
    private final SavedStateRegistry mRegistry;

    private SavedStateRegistryController(SavedStateRegistryOwner owner) {
        mOwner = owner;
        mRegistry = new SavedStateRegistry();
    }

    public static SavedStateRegistryController create(SavedStateRegistryOwner owner) {
        return new SavedStateRegistryController(owner);
    }

    // Obfuscated AndroidX aliases used by app-shipped activity code.
    public static SavedStateRegistryController a(SavedStateRegistryOwner owner) {
        return create(owner);
    }

    public SavedStateRegistry getSavedStateRegistry() { return mRegistry; }
    public SavedStateRegistry b() { return mRegistry; }

    public void c() { /* attach no-op for shim */ }
    public void performRestore(Bundle savedState) { mRegistry.performRestore(savedState); }
    public void d(Bundle savedState) { performRestore(savedState); }
    public void performSave(Bundle outBundle) { mRegistry.performSave(outBundle); }
    public void e(Bundle outBundle) { performSave(outBundle); }
}
