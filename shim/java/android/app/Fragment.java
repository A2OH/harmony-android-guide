package android.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment {
    Activity mHost;
    String mTag;
    int mContainerId;
    boolean mAdded;
    boolean mDetached;
    boolean mHidden;
    boolean mResumed;
    boolean mCreated;
    boolean mStarted;
    View mView;
    Bundle mArguments;
    Bundle mSavedFragmentState;
    FragmentManager mChildFragmentManager;
    private boolean mRetainInstance;
    private boolean mHasMenu;
    private boolean mMenuVisible = true;
    private boolean mUserVisibleHint = true;

    public Fragment() {}

    // Lifecycle callbacks
    public void onAttach(Context context) {}
    public void onAttach(Activity activity) {}
    public void onCreate(Bundle savedInstanceState) {}
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return null; }
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

    // Accessors
    public final Activity getActivity() { return mHost; }
    public final Context getContext() { return mHost; }
    public final Bundle getArguments() { return mArguments; }
    public void setArguments(Bundle args) { mArguments = args; }
    public final String getTag() { return mTag; }
    public final int getId() { return mContainerId; }
    public final boolean isAdded() { return mAdded; }
    public final boolean isDetached() { return mDetached; }
    public final boolean isHidden() { return mHidden; }
    public final boolean isResumed() { return mResumed; }
    public final boolean isVisible() { return mAdded && !mHidden && mView != null; }
    public View getView() { return mView; }
    public final boolean isRemoving() { return false; }
    public final boolean isInLayout() { return false; }
    public final boolean isStateSaved() { return false; }

    public void setRetainInstance(boolean retain) { mRetainInstance = retain; }
    public final boolean getRetainInstance() { return mRetainInstance; }
    public void setHasOptionsMenu(boolean hasMenu) { mHasMenu = hasMenu; }
    public void setMenuVisibility(boolean menuVisible) { mMenuVisible = menuVisible; }
    public void setUserVisibleHint(boolean isVisibleToUser) { mUserVisibleHint = isVisibleToUser; }
    public boolean getUserVisibleHint() { return mUserVisibleHint; }

    public final FragmentManager getFragmentManager() {
        return mHost != null ? mHost.getFragmentManager() : null;
    }

    public final FragmentManager getChildFragmentManager() {
        if (mChildFragmentManager == null) {
            mChildFragmentManager = new FragmentManager();
            if (mHost != null) {
                mChildFragmentManager.setHost(mHost);
            }
        }
        return mChildFragmentManager;
    }

    public final FragmentManager getParentFragmentManager() {
        return getFragmentManager();
    }

    // Resources
    public final android.content.res.Resources getResources() {
        return mHost != null ? mHost.getResources() : null;
    }

    public final String getString(int resId) {
        android.content.res.Resources r = getResources();
        return r != null ? r.getString(resId) : null;
    }

    // View finding
    public final View requireView() {
        if (mView == null) {
            throw new IllegalStateException("Fragment " + this + " did not return a View from onCreateView");
        }
        return mView;
    }

    public final Activity requireActivity() {
        if (mHost == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to an activity");
        }
        return mHost;
    }

    public final Context requireContext() {
        Context ctx = getContext();
        if (ctx == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to a context");
        }
        return ctx;
    }

    // Lifecycle dispatch (called by FragmentManager)
    void performAttach(Activity host) {
        mHost = host;
        onAttach(host);
        onAttach((Context) host);
    }

    void performCreate(Bundle savedInstanceState) {
        mCreated = true;
        onCreate(savedInstanceState);
    }

    void performCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        onStart();
    }

    void performResume() {
        mResumed = true;
        onResume();
    }

    void performPause() {
        mResumed = false;
        onPause();
    }

    void performStop() {
        mStarted = false;
        onStop();
    }

    void performDestroyView() {
        onDestroyView();
        mView = null;
    }

    void performDestroy() {
        mCreated = false;
        onDestroy();
    }

    void performDetach() {
        mHost = null;
        onDetach();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Fragment{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        if (mTag != null) {
            sb.append(" tag=");
            sb.append(mTag);
        }
        if (mContainerId != 0) {
            sb.append(" id=0x");
            sb.append(Integer.toHexString(mContainerId));
        }
        sb.append('}');
        return sb.toString();
    }

    // SavedState for FragmentManager compatibility
    public static class SavedState {
        final Bundle mState;
        public SavedState(Bundle state) { mState = state; }
        public SavedState() { mState = null; }
    }

    // Static factory helper used by some apps
    public static Fragment instantiate(Context context, String fname) {
        return instantiate(context, fname, null);
    }

    public static Fragment instantiate(Context context, String fname, Bundle args) {
        try {
            Class<?> clazz = Class.forName(fname);
            Fragment f = (Fragment) clazz.newInstance();
            if (args != null) {
                f.setArguments(args);
            }
            return f;
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate fragment " + fname, e);
        }
    }
}
