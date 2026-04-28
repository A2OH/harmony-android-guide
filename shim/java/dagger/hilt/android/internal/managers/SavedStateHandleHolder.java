package dagger.hilt.android.internal.managers;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.viewmodel.CreationExtras;

public class SavedStateHandleHolder {
    private CreationExtras pendingCreationExtras;
    private SavedStateHandle savedStateHandle;

    public SavedStateHandleHolder() {
        this(null);
    }

    public SavedStateHandleHolder(CreationExtras extras) {
        pendingCreationExtras = extras;
    }

    // Hilt-generated activities call this from onDestroy().
    public void a() {
        pendingCreationExtras = null;
    }

    public SavedStateHandle b() {
        if (savedStateHandle == null) {
            savedStateHandle = new SavedStateHandle();
        }
        pendingCreationExtras = null;
        return savedStateHandle;
    }

    public boolean c() {
        return savedStateHandle == null && pendingCreationExtras == null;
    }

    public void d(CreationExtras extras) {
        if (savedStateHandle == null) {
            pendingCreationExtras = extras;
        }
    }

    // Older fallback aliases kept for pre-Hilt stubs still calling the bundle form.
    public void a(android.os.Bundle bundle) {}
    public void b(android.os.Bundle bundle) {}
}
