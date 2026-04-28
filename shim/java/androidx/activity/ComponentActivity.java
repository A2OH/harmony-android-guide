package androidx.activity;

import android.os.Bundle;
import android.view.View;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.lifecycle.ViewTreeViewModelStoreOwner;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;
import androidx.savedstate.ViewTreeSavedStateRegistryOwner;
import com.westlake.engine.WestlakeLauncher;

public class ComponentActivity extends android.app.Activity
        implements LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    private LifecycleRegistry mLifecycleRegistry;
    private SavedStateRegistryController savedStateRegistryController;
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
        if (savedStateRegistryController == null) {
            savedStateRegistryController = SavedStateRegistryController.create(this);
        }
        return savedStateRegistryController;
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
        try {
            WestlakeLauncher.noteMarker("CV ComponentActivity restore begin");
            ensureSavedStateRegistryController().performRestore(savedInstanceState);
            WestlakeLauncher.noteMarker("CV ComponentActivity restore end");
        } catch (Throwable t) {
            WestlakeLauncher.noteMarker("CV ComponentActivity restore error");
            try {
                WestlakeLauncher.dumpThrowable("[ComponentActivity] saved-state restore", t);
            } catch (Throwable ignored) {
            }
        }
        // Fire OnContextAvailableListeners BEFORE super.onCreate()
        // This is where Hilt's inject() callback runs
        try {
            WestlakeLauncher.noteMarker("CV ComponentActivity context begin");
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
                        try {
                            WestlakeLauncher.dumpThrowable("[ComponentActivity] OnContextAvailable", t);
                        } catch (Throwable ignored) {
                        }
                    }
                }
            }
            WestlakeLauncher.noteMarker("CV ComponentActivity context end");
        } catch (Throwable t) {
            WestlakeLauncher.noteMarker("CV ComponentActivity context error");
            try {
                WestlakeLauncher.dumpThrowable("[ComponentActivity] context dispatch", t);
            } catch (Throwable ignored) {
            }
        }
        try {
            WestlakeLauncher.noteMarker("CV ComponentActivity super begin");
            super.onCreate(savedInstanceState);
            WestlakeLauncher.noteMarker("CV ComponentActivity super end");
        } catch (Throwable t) {
            // Catch NPEs from super chain (e.g., refreshBasketLayout) so the Activity's
            // own onCreate code (fragment setup, ViewModels) can still run
            WestlakeLauncher.noteMarker("CV ComponentActivity super error");
            try {
                WestlakeLauncher.dumpThrowable("[ComponentActivity] super.onCreate", t);
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ensureLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ensureLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    @Override
    protected void onPause() {
        ensureLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        ensureLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ensureLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ensureSavedStateRegistryController().performSave(outState);
    }

    @Override
    public void setContentView(View view) {
        attachViewTreeOwners(view);
        super.setContentView(view);
    }

    protected void attachViewTreeOwners(View view) {
        if (view == null) {
            return;
        }
        ViewTreeLifecycleOwner.set(view, this);
        ViewTreeViewModelStoreOwner.set(view, this);
        ViewTreeSavedStateRegistryOwner.set(view, this);
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
