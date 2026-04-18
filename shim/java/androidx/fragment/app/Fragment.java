package androidx.fragment.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;

/**
 * AndroidX Fragment stub. Standalone class (does not extend android.app.Fragment)
 * to match the real Jetpack Fragment which is also standalone.
 */
public class Fragment implements LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
    private static final String TAG = "WestlakeFragment";
    private static boolean shouldTolerateAttachFailure(Fragment fragment, Throwable t) {
        if (fragment == null || t == null) {
            return false;
        }
        return fragment.getClass().getName().equals(
                "com.mcdonalds.homedashboard.fragment.HomeDashboardFragment")
                && t instanceof IllegalArgumentException;
    }
    private static String describeThrowable(Throwable t) {
        if (t == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        Throwable cur = t;
        for (int depth = 0; cur != null && depth < 4; depth++) {
            if (depth > 0) {
                sb.append(" <- ");
            }
            sb.append(cur.getClass().getName());
            try {
                String msg = cur.getMessage();
                if (msg != null && !msg.isEmpty()) {
                    sb.append(": ").append(msg);
                }
            } catch (Throwable ignored) {
            }
            StackTraceElement[] frames = null;
            try {
                frames = cur.getStackTrace();
            } catch (Throwable ignored) {
            }
            if (frames != null) {
                int shown = 0;
                for (int i = 0; i < frames.length && shown < 3; i++) {
                    StackTraceElement frame = frames[i];
                    if (frame == null) {
                        continue;
                    }
                    sb.append(" @ ").append(frame.getClassName())
                            .append('.').append(frame.getMethodName())
                            .append(':').append(frame.getLineNumber());
                    shown++;
                }
            }
            try {
                Throwable next = cur.getCause();
                if (next == cur) {
                    break;
                }
                cur = next;
            } catch (Throwable ignored) {
                break;
            }
        }
        return sb.toString();
    }

    String mTag;
    Bundle mArguments;
    public FragmentActivity mActivity;
    Object mHost;  // FragmentHostCallback in real impl, just Object for stub
    public View mView;
    int mContainerId;
    boolean mAdded;
    boolean mDetached;
    boolean mHidden;
    boolean mResumed;
    boolean mCreated;
    boolean mStarted;
    FragmentManager mFragmentManager;       // parent
    FragmentManager mChildFragmentManager;
    private boolean mRetainInstance;
    private boolean mHasMenu;
    private boolean mMenuVisible = true;
    private boolean mUserVisibleHint = true;
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private ViewModelStore mViewModelStore;
    private SavedStateRegistryController mSavedStateRegistryController;

    public Fragment() {}

    // ── Lifecycle callbacks ──

    public void onAttach(Activity activity) {}

    public void onAttach(Context context) {}

    public void onCreate(Bundle savedInstanceState) {}

    public LayoutInflater onGetLayoutInflater(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            return activity.getLayoutInflater();
        }
        Context context = getContext();
        return context != null ? LayoutInflater.from(context) : null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {}

    public void onActivityCreated(Bundle savedInstanceState) {}

    public void onStart() {}

    public void onResume() {}

    public void onPause() {}

    public void onStop() {}

    public void onDestroyView() {}

    public void onDestroy() {}

    public void onDetach() {}

    // State
    public void onSaveInstanceState(Bundle outState) {}
    public void onViewStateRestored(Bundle savedInstanceState) {}
    public void onHiddenChanged(boolean hidden) {}

    // ── Accessors ──

    public FragmentActivity getActivity() {
        if (mActivity != null) {
            return mActivity;
        }
        return mHost instanceof FragmentActivity ? (FragmentActivity) mHost : null;
    }

    public Context getContext() {
        if (mActivity != null) {
            return mActivity;
        }
        return mHost instanceof Context ? (Context) mHost : null;
    }

    public Object getHost() {
        return mHost != null ? mHost : mActivity;
    }

    public Bundle getArguments() { return mArguments; }

    public void setArguments(Bundle args) { mArguments = args; }

    public String getTag() { return mTag; }

    public int getId() { return mContainerId; }

    public View getView() { return mView; }

    public boolean isAdded() { return mAdded; }

    public boolean isDetached() { return mDetached; }

    public boolean isHidden() { return mHidden; }

    public boolean isResumed() { return mResumed; }

    public boolean isVisible() { return mAdded && !mHidden && mView != null; }

    public boolean isRemoving() { return false; }

    public boolean isStateSaved() { return false; }

    public void setRetainInstance(boolean retain) { mRetainInstance = retain; }

    public boolean getRetainInstance() { return mRetainInstance; }

    public void setHasOptionsMenu(boolean hasMenu) { mHasMenu = hasMenu; }

    public void setMenuVisibility(boolean menuVisible) { mMenuVisible = menuVisible; }

    public void setUserVisibleHint(boolean isVisibleToUser) { mUserVisibleHint = isVisibleToUser; }

    public boolean getUserVisibleHint() { return mUserVisibleHint; }

    // ── require* methods ──

    public FragmentActivity requireActivity() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to an activity.");
        }
        return activity;
    }

    public Context requireContext() {
        Context ctx = getContext();
        if (ctx == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to a context.");
        }
        return ctx;
    }

    public View requireView() {
        if (mView == null) {
            throw new IllegalStateException(
                "Fragment " + this + " did not return a View from onCreateView() or this was called before onViewCreated().");
        }
        return mView;
    }

    // ── Lifecycle owner stubs ──

    /**
     * Returns a LifecycleOwner scoped to the Fragment's View.
     * Stub returns {@code this} (Fragment is not actually a LifecycleOwner in the stub,
     * but callers just need a non-null reference).
     */
    public LifecycleOwner getViewLifecycleOwner() { return this; }

    /**
     * Returns the Lifecycle of this fragment. Stub returns null.
     */
    @Override
    public Lifecycle getLifecycle() { return ensureLifecycleRegistry(); }

    private LifecycleRegistry ensureLifecycleRegistry() {
        if (mLifecycleRegistry == null) {
            mLifecycleRegistry = new LifecycleRegistry(this);
        }
        return mLifecycleRegistry;
    }

    private ViewModelStore ensureViewModelStore() {
        if (mViewModelStore == null) {
            mViewModelStore = new ViewModelStore();
        }
        return mViewModelStore;
    }

    private SavedStateRegistryController ensureSavedStateRegistryController() {
        if (mSavedStateRegistryController == null) {
            try {
                mSavedStateRegistryController = SavedStateRegistryController.create(this);
            } catch (Throwable t) {
                Log.w(TAG, "SavedStateRegistryController.create failed for " + this
                        + " :: " + describeThrowable(t), t);
            }
        }
        return mSavedStateRegistryController;
    }

    @Override
    public ViewModelStore getViewModelStore() {
        return ensureViewModelStore();
    }

    @Override
    public SavedStateRegistry getSavedStateRegistry() {
        return ensureSavedStateRegistryController().getSavedStateRegistry();
    }

    public ViewModelProvider.Factory getDefaultViewModelProviderFactory() {
        return new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                try {
                    return modelClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    // ── Fragment managers ──

    public FragmentManager getParentFragmentManager() {
        if (mFragmentManager == null) {
            throw new IllegalStateException("Fragment " + this + " not associated with a fragment manager.");
        }
        return mFragmentManager;
    }

    public FragmentManager getChildFragmentManager() {
        if (mChildFragmentManager == null) {
            mChildFragmentManager = new FragmentManager();
            FragmentActivity activity = getActivity();
            if (activity != null) {
                mChildFragmentManager.mHost = activity;
            }
        }
        return mChildFragmentManager;
    }

    // ── Resources ──

    public android.content.res.Resources getResources() {
        Context context = getContext();
        return context != null ? context.getResources() : null;
    }

    public String getString(int resId) {
        android.content.res.Resources r = getResources();
        return r != null ? r.getString(resId) : null;
    }

    public String getString(int resId, Object... formatArgs) {
        android.content.res.Resources r = getResources();
        return r != null ? r.getString(resId) : null;
    }

    // ── View finding ──

    public View requireViewById(int id) {
        View v = mView != null ? mView.findViewById(id) : null;
        if (v == null) {
            throw new IllegalArgumentException("ID does not reference a View inside this Fragment");
        }
        return v;
    }

    // ── Lifecycle dispatch (called by FragmentManager) ──

    void performAttach(FragmentActivity host) {
        mActivity = host;
        mHost = host;
        ensureLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        if (host == null) {
            throw new IllegalStateException("Fragment.performAttach host=null for " + this);
        }
        Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("Fragment.performAttach context=null before callbacks for " + this);
        }
        try {
            onAttach(context);
        } catch (Throwable t) {
            Log.e(TAG, "performAttach onAttach(Context) failed for " + this
                    + " :: " + describeThrowable(t), t);
            if (shouldTolerateAttachFailure(this, t)) {
                Log.w(TAG, "performAttach tolerating HomeDashboardFragment onAttach(Context) failure");
                return;
            }
            throw new RuntimeException("Fragment.performAttach:onAttach(Context) " + this, t);
        }
        try {
            onAttach((Activity) host);
        } catch (Throwable t) {
            Log.e(TAG, "performAttach onAttach(Activity) failed for " + this
                    + " :: " + describeThrowable(t), t);
            throw new RuntimeException("Fragment.performAttach:onAttach(Activity) " + this, t);
        }
    }

    void performCreate(Bundle savedInstanceState) {
        mCreated = true;
        SavedStateRegistryController controller = ensureSavedStateRegistryController();
        if (controller != null) {
            controller.performRestore(savedInstanceState);
        } else {
            Log.w(TAG, "performCreate proceeding without SavedStateRegistryController for " + this);
        }
        onCreate(savedInstanceState);
    }

    void performCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        mView = onCreateView(inflater, container, savedInstanceState);
        if (mView != null) {
            onViewCreated(mView, savedInstanceState);
        }
    }

    void performActivityCreated(Bundle savedInstanceState) {
        onActivityCreated(savedInstanceState);
    }

    void performStart() {
        mStarted = true;
        ensureLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_START);
        onStart();
    }

    void performResume() {
        mResumed = true;
        ensureLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        onResume();
    }

    void performPause() {
        mResumed = false;
        ensureLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        onPause();
    }

    void performStop() {
        mStarted = false;
        ensureLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        onStop();
    }

    void performDestroyView() {
        onDestroyView();
        mView = null;
    }

    void performDestroy() {
        mCreated = false;
        ensureLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        onDestroy();
    }

    void performDetach() {
        mActivity = null;
        mHost = null;
        onDetach();
    }

    // ── Static factory helper ──

    public static Fragment instantiate(Context context, String fname) {
        return instantiate(context, fname, null);
    }

    public static Fragment instantiate(Context context, String fname, Bundle args) {
        try {
            Class<?> clazz = context.getClassLoader().loadClass(fname);
            Fragment f = (Fragment) clazz.newInstance();
            if (args != null) {
                f.setArguments(args);
            }
            return f;
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate fragment " + fname, e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Fragment{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        if (mTag != null) {
            sb.append(" tag=").append(mTag);
        }
        if (mContainerId != 0) {
            sb.append(" id=0x").append(Integer.toHexString(mContainerId));
        }
        sb.append('}');
        return sb.toString();
    }

    // SavedState for compatibility
    public static class SavedState {
        final Bundle mState;
        public SavedState(Bundle state) { mState = state; }
        public SavedState() { mState = null; }
    }
}
