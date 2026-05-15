package androidx.fragment.app;

import com.westlake.engine.WestlakeLauncher;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete FragmentTransaction implementation that modifies the FragmentManager's
 * fragment list on commit.
 */
public class FragmentTransactionImpl extends FragmentTransaction {
    final FragmentManager mManager;
    final ArrayList<Op> mOps = new ArrayList<>();
    String mName;
    boolean mAddToBackStack;

    public FragmentTransactionImpl(FragmentManager manager) {
        mManager = manager;
    }

    @Override
    public FragmentTransaction add(int containerViewId, Fragment fragment) {
        note("FragmentTransaction add2 enter txId=" + System.identityHashCode(this)
                + " fragment=" + (fragment != null)
                + " opsBefore=" + mOps.size());
        return add(containerViewId, fragment, null);
    }

    @Override
    public FragmentTransaction add(int containerViewId, Fragment fragment, String tag) {
        note("FragmentTransaction add3 enter txId=" + System.identityHashCode(this)
                + " txClass=" + getClass().getName()
                + " fragment=" + (fragment != null)
                + " tag=" + tag
                + " opsBefore=" + mOps.size());
        Op op = new Op();
        op.cmd = OP_ADD;
        op.fragment = fragment;
        op.tag = tag;
        op.containerId = containerViewId;
        mOps.add(op);
        note("FragmentTransaction add3 exit txId=" + System.identityHashCode(this)
                + " opsAfter=" + mOps.size());
        return this;
    }

    @Override
    public FragmentTransaction replace(int containerViewId, Fragment fragment) {
        return replace(containerViewId, fragment, null);
    }

    @Override
    public FragmentTransaction replace(int containerViewId, Fragment fragment, String tag) {
        note("FragmentTransaction replace enter txId=" + System.identityHashCode(this)
                + " txClass=" + getClass().getName()
                + " fragment=" + (fragment != null)
                + " tag=" + tag
                + " containerId=0x" + Integer.toHexString(containerViewId)
                + " opsBefore=" + mOps.size());
        Op op = new Op();
        op.cmd = OP_REPLACE;
        op.fragment = fragment;
        op.tag = tag;
        op.containerId = containerViewId;
        mOps.add(op);
        note("FragmentTransaction replace exit txId=" + System.identityHashCode(this)
                + " opsAfter=" + mOps.size());
        return this;
    }

    @Override
    public FragmentTransaction remove(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_REMOVE;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    @Override
    public FragmentTransaction hide(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_HIDE;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    @Override
    public FragmentTransaction show(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_SHOW;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    @Override
    public FragmentTransaction detach(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_DETACH;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    @Override
    public FragmentTransaction attach(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_ATTACH;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    @Override
    public FragmentTransaction addToBackStack(String name) {
        mAddToBackStack = true;
        mName = name;
        return this;
    }

    @Override
    public int commit() { return commitInternal(false); }

    @Override
    public int commitAllowingStateLoss() { return commitInternal(true); }

    @Override
    public void commitNow() { commitInternal(false); }

    @Override
    public void commitNowAllowingStateLoss() { commitInternal(true); }

    @Override
    public boolean isEmpty() { return mOps.isEmpty(); }

    private static void note(String marker) {
        try {
            WestlakeLauncher.noteMarker("CV " + marker);
        } catch (Throwable ignored) {
        }
    }

    private int commitInternal(boolean allowStateLoss) {
        note("FragmentTransaction commit enter txId=" + System.identityHashCode(this)
                + " txClass=" + getClass().getName()
                + " manager=" + (mManager != null)
                + " managerId=" + (mManager != null ? System.identityHashCode(mManager) : -1)
                + " ops=" + (mOps != null ? mOps.size() : -1)
                + " allowStateLoss=" + allowStateLoss);
        if (mManager == null) {
            note("FragmentTransaction commit manager null");
            return -1;
        }

        for (int i = 0; i < mOps.size(); i++) {
            Op op = mOps.get(i);
            note("FragmentTransaction commit op index=" + i
                    + " cmd=" + (op != null ? op.cmd : -1)
                    + " fragment=" + (op != null && op.fragment != null));
            switch (op.cmd) {
                case OP_ADD:
                    mManager.addFragmentInternal(op.fragment, op.tag, op.containerId);
                    break;
                case OP_REPLACE:
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
        note("FragmentTransaction commit ops applied count=" + mOps.size());

        int backStackId = -1;
        if (mAddToBackStack) {
            backStackId = mManager.addBackStack(mName, new ArrayList<>(mOps));
        }
        note("FragmentTransaction commit return backStackId=" + backStackId);
        return backStackId;
    }

}
