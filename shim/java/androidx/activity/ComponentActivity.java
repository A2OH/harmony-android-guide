package androidx.activity;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;
import com.westlake.engine.WestlakeLauncher;

public class ComponentActivity extends FragmentActivity
        implements LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    private LifecycleRegistry mLifecycleRegistry;
    private SavedStateRegistryController mSavedStateRegistryController;
    private ViewModelStore mViewModelStore;

    private java.util.List<androidx.activity.contextaware.OnContextAvailableListener> mContextListeners =
            new java.util.ArrayList<>();
    private boolean mContextAvailableFired = false;

    public ComponentActivity() {}

    private LifecycleRegistry ensureLifecycleRegistry() {
        if (mLifecycleRegistry == null) {
            mLifecycleRegistry = new LifecycleRegistry(this);
        }
        return mLifecycleRegistry;
    }

    private SavedStateRegistryController ensureSavedStateRegistryController() {
        if (mSavedStateRegistryController == null) {
            mSavedStateRegistryController = SavedStateRegistryController.create(this);
        }
        return mSavedStateRegistryController;
    }

    private ViewModelStore ensureViewModelStore() {
        if (mViewModelStore == null) {
            mViewModelStore = new ViewModelStore();
        }
        return mViewModelStore;
    }

    private java.util.List<androidx.activity.contextaware.OnContextAvailableListener>
            ensureContextListeners() {
        if (mContextListeners == null) {
            mContextListeners = new java.util.ArrayList<>();
        }
        return mContextListeners;
    }

    @Override
    public Lifecycle getLifecycle() { return ensureLifecycleRegistry(); }

    @Override
    public SavedStateRegistry getSavedStateRegistry() {
        return ensureSavedStateRegistryController().getSavedStateRegistry();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ensureSavedStateRegistryController().performRestore(savedInstanceState);
        // Fire OnContextAvailableListeners BEFORE super.onCreate()
        // This is where Hilt's inject() callback runs
        if (!mContextAvailableFired) {
            mContextAvailableFired = true;
            for (androidx.activity.contextaware.OnContextAvailableListener l :
                    new java.util.ArrayList<>(ensureContextListeners())) {
                try {
                    // Try obfuscated name first (R8 renames onContextAvailable → a)
                    try {
                        java.lang.reflect.Method m = l.getClass().getMethod("a", android.content.Context.class);
                        m.invoke(l, (android.content.Context) this);
                    } catch (NoSuchMethodException nsm) {
                        l.onContextAvailable(this);
                    }
                } catch (Throwable t) {
                    WestlakeLauncher.dumpThrowable("[ComponentActivity] OnContextAvailable", t);
                }
            }
        }
        try {
            super.onCreate(savedInstanceState);
        } catch (Throwable t) {
            // Catch NPEs from super chain (e.g., refreshBasketLayout) so the Activity's
            // own onCreate code (fragment setup, ViewModels) can still run
            WestlakeLauncher.dumpThrowable("[ComponentActivity] super.onCreate", t);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ensureSavedStateRegistryController().performSave(outState);
    }

    public void addOnContextAvailableListener(androidx.activity.contextaware.OnContextAvailableListener listener) {
        ensureContextListeners().add(listener);
        // If context already available, fire immediately
        if (mContextAvailableFired) {
            try { listener.onContextAvailable(this); } catch (Throwable t) {}
        }
    }
    public void removeOnContextAvailableListener(androidx.activity.contextaware.OnContextAvailableListener listener) {
        ensureContextListeners().remove(listener);
    }
    public void addOnBackPressedCallback(Object callback) { /* stub */ }
    private final OnBackPressedDispatcher mOnBackPressedDispatcher = new OnBackPressedDispatcher();
    public OnBackPressedDispatcher getOnBackPressedDispatcher() { return mOnBackPressedDispatcher; }
    @Override
    public ViewModelStore getViewModelStore() { return ensureViewModelStore(); }

    public <I, O> androidx.activity.result.ActivityResultLauncher<I> registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContract<I, O> contract,
            androidx.activity.result.ActivityResultCallback<O> callback) {
        return new androidx.activity.result.ActivityResultLauncher<I>() {
            @Override public void launch(I input) { /* stub — no real activity result */ }
            @Override public void unregister() {}
            @Override public Object getContract() { return contract; }
        };
    }

    public <I, O> androidx.activity.result.ActivityResultLauncher<I> registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContract<I, O> contract,
            androidx.activity.result.ActivityResultRegistry registry,
            androidx.activity.result.ActivityResultCallback<O> callback) {
        return registerForActivityResult(contract, callback);
    }
}
