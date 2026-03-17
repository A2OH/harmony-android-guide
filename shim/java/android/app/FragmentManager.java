package android.app;

import java.util.ArrayList;
import java.util.List;

public class FragmentManager {
    private final List<Fragment> mAdded = new ArrayList();
    private final List<BackStackRecord> mBackStack = new ArrayList();
    private final List<OnBackStackChangedListener> mBackStackListeners = new ArrayList();
    Activity mHost;

    public interface BackStackEntry {
        int getId();
        String getName();
    }

    public interface OnBackStackChangedListener {
        void onBackStackChanged();
    }

    public interface FragmentLifecycleCallbacks {
    }

    static class BackStackRecord implements BackStackEntry {
        int mId;
        String mName;
        List<FragmentTransaction.Op> mOps;

        BackStackRecord(int id, String name, List<FragmentTransaction.Op> ops) {
            mId = id;
            mName = name;
            mOps = ops;
        }

        public int getId() { return mId; }
        public String getName() { return mName; }
    }

    public FragmentManager() {}

    void setHost(Activity host) { mHost = host; }

    public FragmentTransaction beginTransaction() {
        return new FragmentTransaction(this);
    }

    public Fragment findFragmentById(int id) {
        for (int i = mAdded.size() - 1; i >= 0; i--) {
            Fragment f = mAdded.get(i);
            if (f.getId() == id) return f;
        }
        return null;
    }

    public Fragment findFragmentByTag(String tag) {
        if (tag == null) return null;
        for (int i = mAdded.size() - 1; i >= 0; i--) {
            Fragment f = mAdded.get(i);
            if (tag.equals(f.getTag())) return f;
        }
        return null;
    }

    public List<Fragment> getFragments() {
        return new ArrayList(mAdded);
    }

    public int getBackStackEntryCount() { return mBackStack.size(); }

    public BackStackEntry getBackStackEntryAt(int index) {
        return mBackStack.get(index);
    }

    public void popBackStack() {
        if (mBackStack.isEmpty()) return;
        BackStackRecord record = mBackStack.remove(mBackStack.size() - 1);
        // Reverse each op
        if (record.mOps != null) {
            for (int i = record.mOps.size() - 1; i >= 0; i--) {
                FragmentTransaction.Op op = record.mOps.get(i);
                switch (op.cmd) {
                    case FragmentTransaction.OP_ADD:
                        removeFragmentInternal(op.fragment);
                        break;
                    case FragmentTransaction.OP_REMOVE:
                        addFragmentInternal(op.fragment, op.tag, op.containerId);
                        break;
                    case FragmentTransaction.OP_REPLACE:
                        removeFragmentInternal(op.fragment);
                        if (op.removed != null) {
                            for (int j = 0; j < op.removed.size(); j++) {
                                addFragmentInternal(op.removed.get(j), op.removed.get(j).mTag, op.removed.get(j).mContainerId);
                            }
                        }
                        break;
                    case FragmentTransaction.OP_HIDE:
                        op.fragment.mHidden = false;
                        op.fragment.onHiddenChanged(false);
                        break;
                    case FragmentTransaction.OP_SHOW:
                        op.fragment.mHidden = true;
                        op.fragment.onHiddenChanged(true);
                        break;
                    case FragmentTransaction.OP_DETACH:
                        op.fragment.mDetached = false;
                        break;
                    case FragmentTransaction.OP_ATTACH:
                        op.fragment.mDetached = true;
                        break;
                }
            }
        }
        notifyBackStackChanged();
    }

    public void popBackStack(String name, int flags) {
        if (name == null && flags == POP_BACK_STACK_INCLUSIVE) {
            // Pop all
            while (!mBackStack.isEmpty()) {
                popBackStack();
            }
        } else {
            // Find matching entry and pop
            popBackStack();
        }
    }

    public void popBackStack(int id, int flags) {
        popBackStack();
    }

    public boolean popBackStackImmediate() {
        if (mBackStack.isEmpty()) return false;
        popBackStack();
        return true;
    }

    public boolean popBackStackImmediate(String name, int flags) {
        if (mBackStack.isEmpty()) return false;
        popBackStack(name, flags);
        return true;
    }

    public boolean popBackStackImmediate(int id, int flags) {
        return popBackStackImmediate();
    }

    public static final int POP_BACK_STACK_INCLUSIVE = 1;

    public void addOnBackStackChangedListener(OnBackStackChangedListener listener) {
        mBackStackListeners.add(listener);
    }

    public void removeOnBackStackChangedListener(OnBackStackChangedListener listener) {
        mBackStackListeners.remove(listener);
    }

    private void notifyBackStackChanged() {
        for (int i = 0; i < mBackStackListeners.size(); i++) {
            mBackStackListeners.get(i).onBackStackChanged();
        }
    }

    public boolean isDestroyed() { return false; }
    public boolean isStateSaved() { return false; }

    public void executePendingTransactions() {
        // No-op in synchronous shim — all transactions execute immediately
    }

    public boolean executePendingTransactions(boolean allowStateLoss) {
        return true;
    }

    void addFragmentInternal(Fragment f, String tag, int containerId) {
        f.mTag = tag;
        f.mContainerId = containerId;
        f.mHost = mHost;
        f.mAdded = true;
        mAdded.add(f);
        // Drive lifecycle
        f.performAttach(mHost);
        if (f.mArguments == null) {
            f.performCreate(null);
        } else {
            f.performCreate(null);
        }
        f.performCreateView(null, null, null);
        f.performActivityCreated(null);
        f.performStart();
        f.performResume();
    }

    void removeFragmentInternal(Fragment f) {
        f.performPause();
        f.performStop();
        f.performDestroyView();
        f.performDestroy();
        f.performDetach();
        f.mAdded = false;
        mAdded.remove(f);
    }

    int addBackStack(String name, List<FragmentTransaction.Op> ops) {
        int id = mBackStack.size();
        mBackStack.add(new BackStackRecord(id, name, ops));
        notifyBackStackChanged();
        return id;
    }

    // Remove all fragments at a given container ID and return the removed list
    List<Fragment> removeFragmentsAtContainer(int containerId) {
        List<Fragment> removed = new ArrayList();
        for (int i = mAdded.size() - 1; i >= 0; i--) {
            Fragment f = mAdded.get(i);
            if (f.mContainerId == containerId) {
                removed.add(f);
                removeFragmentInternal(f);
                i = Math.min(i, mAdded.size()); // Adjust after removal
            }
        }
        return removed;
    }

    public void registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacks cb, boolean recursive) {}
    public void unregisterFragmentLifecycleCallbacks(FragmentLifecycleCallbacks cb) {}

    public void dump(String prefix, java.io.FileDescriptor fd, java.io.PrintWriter writer, String[] args) {
        writer.print(prefix);
        writer.println("FragmentManager{" + Integer.toHexString(System.identityHashCode(this)) + "}");
        writer.print(prefix);
        writer.println("  Added fragments:");
        for (int i = 0; i < mAdded.size(); i++) {
            writer.print(prefix);
            writer.println("    " + mAdded.get(i));
        }
    }

    public Fragment.SavedState saveFragmentInstanceState(Fragment f) { return null; }

    public void putFragment(android.os.Bundle bundle, String key, Fragment fragment) {
        // Store fragment tag in bundle for later retrieval
        if (fragment.mTag != null) {
            bundle.putString(key, fragment.mTag);
        }
    }

    public Fragment getFragment(android.os.Bundle bundle, String key) {
        String tag = bundle.getString(key);
        if (tag != null) {
            return findFragmentByTag(tag);
        }
        return null;
    }
}
