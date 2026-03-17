package android.app;

import java.util.ArrayList;
import java.util.List;

public class FragmentTransaction {
    final FragmentManager mManager;
    final ArrayList<Op> mOps = new ArrayList();
    String mName;
    boolean mAddToBackStack;

    static final int OP_ADD = 1;
    static final int OP_REPLACE = 2;
    static final int OP_REMOVE = 3;
    static final int OP_HIDE = 4;
    static final int OP_SHOW = 5;
    static final int OP_DETACH = 6;
    static final int OP_ATTACH = 7;

    public static final int TRANSIT_NONE = 0;
    public static final int TRANSIT_FRAGMENT_OPEN = 1 | 0x2000;
    public static final int TRANSIT_FRAGMENT_CLOSE = 2 | 0x2000;
    public static final int TRANSIT_FRAGMENT_FADE = 3 | 0x2000;

    static class Op {
        int cmd;
        Fragment fragment;
        String tag;
        int containerId;
        List<Fragment> removed; // For REPLACE: fragments that were removed
    }

    // Package-private constructor used by FragmentManager
    FragmentTransaction(FragmentManager manager) {
        mManager = manager;
    }

    // Public no-arg constructor for compilation compat
    public FragmentTransaction() {
        mManager = null;
    }

    public FragmentTransaction add(Fragment fragment, String tag) {
        return add(0, fragment, tag);
    }

    public FragmentTransaction add(int containerViewId, Fragment fragment) {
        return add(containerViewId, fragment, null);
    }

    public FragmentTransaction add(int containerViewId, Fragment fragment, String tag) {
        Op op = new Op();
        op.cmd = OP_ADD;
        op.fragment = fragment;
        op.tag = tag;
        op.containerId = containerViewId;
        mOps.add(op);
        return this;
    }

    public FragmentTransaction replace(int containerViewId, Fragment fragment) {
        return replace(containerViewId, fragment, null);
    }

    public FragmentTransaction replace(int containerViewId, Fragment fragment, String tag) {
        Op op = new Op();
        op.cmd = OP_REPLACE;
        op.fragment = fragment;
        op.tag = tag;
        op.containerId = containerViewId;
        mOps.add(op);
        return this;
    }

    public FragmentTransaction remove(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_REMOVE;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    public FragmentTransaction hide(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_HIDE;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    public FragmentTransaction show(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_SHOW;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    public FragmentTransaction detach(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_DETACH;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    public FragmentTransaction attach(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_ATTACH;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    public FragmentTransaction addToBackStack(String name) {
        mAddToBackStack = true;
        mName = name;
        return this;
    }

    public boolean isAddToBackStackAllowed() { return true; }
    public FragmentTransaction disallowAddToBackStack() { return this; }

    public FragmentTransaction setTransition(int transit) { return this; }
    public FragmentTransaction setCustomAnimations(int enter, int exit) { return this; }
    public FragmentTransaction setCustomAnimations(int enter, int exit, int popEnter, int popExit) { return this; }
    public FragmentTransaction setTransitionStyle(int styleRes) { return this; }
    public FragmentTransaction setBreadCrumbTitle(int res) { return this; }
    public FragmentTransaction setBreadCrumbTitle(CharSequence text) { return this; }
    public FragmentTransaction setBreadCrumbShortTitle(int res) { return this; }
    public FragmentTransaction setBreadCrumbShortTitle(CharSequence text) { return this; }
    public FragmentTransaction setPrimaryNavigationFragment(Fragment fragment) { return this; }
    public FragmentTransaction setReorderingAllowed(boolean reorderingAllowed) { return this; }
    public FragmentTransaction setAllowOptimization(boolean allowOptimization) { return this; }
    public FragmentTransaction runOnCommit(Runnable runnable) {
        if (runnable != null) runnable.run();
        return this;
    }

    public int commit() { return commitInternal(false); }
    public int commitAllowingStateLoss() { return commitInternal(true); }
    public void commitNow() { commitInternal(false); }
    public void commitNowAllowingStateLoss() { commitInternal(true); }

    private int commitInternal(boolean allowStateLoss) {
        if (mManager == null) return -1;

        for (int i = 0; i < mOps.size(); i++) {
            Op op = mOps.get(i);
            switch (op.cmd) {
                case OP_ADD:
                    mManager.addFragmentInternal(op.fragment, op.tag, op.containerId);
                    break;
                case OP_REPLACE:
                    // Remove existing fragments at this container
                    List<Fragment> removed = mManager.removeFragmentsAtContainer(op.containerId);
                    op.removed = removed;
                    mManager.addFragmentInternal(op.fragment, op.tag, op.containerId);
                    break;
                case OP_REMOVE:
                    mManager.removeFragmentInternal(op.fragment);
                    break;
                case OP_HIDE:
                    op.fragment.mHidden = true;
                    op.fragment.onHiddenChanged(true);
                    break;
                case OP_SHOW:
                    op.fragment.mHidden = false;
                    op.fragment.onHiddenChanged(false);
                    break;
                case OP_DETACH:
                    op.fragment.mDetached = true;
                    break;
                case OP_ATTACH:
                    op.fragment.mDetached = false;
                    break;
            }
        }

        int backStackId = -1;
        if (mAddToBackStack) {
            backStackId = mManager.addBackStack(mName, new ArrayList(mOps));
        }
        return backStackId;
    }

    public boolean isEmpty() { return mOps.isEmpty(); }
}
