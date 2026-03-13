package android.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.app.FragmentTransaction
 *
 * API for performing a set of Fragment operations atomically. Transactions
 * are obtained from FragmentManager.beginTransaction() and committed via
 * commit(). Each operation is recorded and applied in sequence on commit.
 *
 * In OH the equivalent is router.pushUrl() / router.back() for navigation
 * and conditional rendering of @Component subtrees for show/hide. This shim
 * preserves the Android API so existing code compiles without changes.
 */
public abstract class FragmentTransaction {

    /**
     * Calls add(containerViewId, fragment, null).
     */
    public abstract FragmentTransaction add(int containerViewId, Fragment fragment);

    /**
     * Add a fragment to the activity state. This fragment may optionally also
     * have its name in the back stack recorded as per addToBackStack(String).
     *
     * @param containerViewId  Optional identifier of the container this fragment is
     *                         to be placed in. Use 0 to not place it in a container.
     * @param fragment         The fragment to be added.
     * @param tag              Optional tag name for the fragment, to later retrieve
     *                         the fragment with findFragmentByTag(String).
     */
    public abstract FragmentTransaction add(int containerViewId, Fragment fragment, String tag);

    /**
     * Calls replace(containerViewId, fragment, null).
     */
    public abstract FragmentTransaction replace(int containerViewId, Fragment fragment);

    /**
     * Replace an existing fragment that was added to a container. This is essentially
     * the same as calling remove(Fragment) for all currently added fragments that were
     * added with the same containerViewId and then add(int, Fragment, String).
     *
     * @param containerViewId  Identifier of the container whose fragment(s) are to be replaced.
     * @param fragment         The new fragment to place in the container.
     * @param tag              Optional tag name for the fragment.
     */
    public abstract FragmentTransaction replace(int containerViewId, Fragment fragment, String tag);

    /**
     * Remove an existing fragment. If it was added to a container, its view
     * is also removed from that container.
     */
    public abstract FragmentTransaction remove(Fragment fragment);

    /**
     * Hide an existing fragment. This is only relevant for fragments whose views
     * have been added to a container, as this will cause the view to be hidden.
     */
    public abstract FragmentTransaction hide(Fragment fragment);

    /**
     * Shows a previously hidden fragment. This is only relevant for fragments whose
     * views have been added to a container, as this will cause the view to be shown.
     */
    public abstract FragmentTransaction show(Fragment fragment);

    /**
     * Add this transaction to the back stack. This means that the transaction will
     * be remembered after it is committed, and will reverse its operation when later
     * popped off the stack.
     *
     * @param name  An optional name for this back stack state, or null.
     */
    public abstract FragmentTransaction addToBackStack(String name);

    /**
     * Schedules a commit of this transaction. The commit does not happen immediately;
     * it will be scheduled on the main thread. In this shim, commit() applies changes
     * synchronously since there is no deferred layout pass.
     *
     * @return  Returns the identifier of this transaction's back stack entry, if
     *          addToBackStack(String) had been called. Otherwise, returns a negative value.
     */
    public abstract int commit();

    // ── Concrete implementation ──

    /**
     * Simple concrete FragmentTransaction used by SimpleFragmentManager.
     * Records all operations in an ordered list and applies them atomically
     * in commit(), driving Fragment lifecycle callbacks as each op is performed.
     */
    static class SimpleFragmentTransaction extends FragmentTransaction {

        // Operation types
        private static final int OP_ADD     = 1;
        private static final int OP_REPLACE = 2;
        private static final int OP_REMOVE  = 3;
        private static final int OP_HIDE    = 4;
        private static final int OP_SHOW    = 5;

        private static class Op {
            final int cmd;
            final int containerViewId;
            final Fragment fragment;
            final String tag;

            Op(int cmd, int containerViewId, Fragment fragment, String tag) {
                this.cmd = cmd;
                this.containerViewId = containerViewId;
                this.fragment = fragment;
                this.tag = tag;
            }
        }

        private final FragmentManager.SimpleFragmentManager manager;
        private final List<Op> ops = new ArrayList<>();
        private boolean addedToBackStack = false;
        private String backStackName;
        private boolean committed = false;

        SimpleFragmentTransaction(FragmentManager.SimpleFragmentManager manager) {
            this.manager = manager;
        }

        @Override
        public FragmentTransaction add(int containerViewId, Fragment fragment) {
            return add(containerViewId, fragment, null);
        }

        @Override
        public FragmentTransaction add(int containerViewId, Fragment fragment, String tag) {
            checkNotCommitted();
            ops.add(new Op(OP_ADD, containerViewId, fragment, tag));
            return this;
        }

        @Override
        public FragmentTransaction replace(int containerViewId, Fragment fragment) {
            return replace(containerViewId, fragment, null);
        }

        @Override
        public FragmentTransaction replace(int containerViewId, Fragment fragment, String tag) {
            checkNotCommitted();
            ops.add(new Op(OP_REPLACE, containerViewId, fragment, tag));
            return this;
        }

        @Override
        public FragmentTransaction remove(Fragment fragment) {
            checkNotCommitted();
            ops.add(new Op(OP_REMOVE, 0, fragment, null));
            return this;
        }

        @Override
        public FragmentTransaction hide(Fragment fragment) {
            checkNotCommitted();
            ops.add(new Op(OP_HIDE, 0, fragment, null));
            return this;
        }

        @Override
        public FragmentTransaction show(Fragment fragment) {
            checkNotCommitted();
            ops.add(new Op(OP_SHOW, 0, fragment, null));
            return this;
        }

        @Override
        public FragmentTransaction addToBackStack(String name) {
            checkNotCommitted();
            addedToBackStack = true;
            backStackName = name;
            return this;
        }

        @Override
        public int commit() {
            checkNotCommitted();
            committed = true;
            for (Op op : ops) {
                switch (op.cmd) {
                    case OP_ADD:
                        manager.performAdd(op.containerViewId, op.fragment, op.tag);
                        break;
                    case OP_REPLACE:
                        manager.performReplace(op.containerViewId, op.fragment, op.tag);
                        break;
                    case OP_REMOVE:
                        manager.performRemove(op.fragment);
                        break;
                    case OP_HIDE:
                        manager.performHide(op.fragment);
                        break;
                    case OP_SHOW:
                        manager.performShow(op.fragment);
                        break;
                    default:
                        break;
                }
            }
            return addedToBackStack ? 1 : -1;
        }

        private void checkNotCommitted() {
            if (committed) {
                throw new IllegalStateException("commit() already called on this transaction");
            }
        }
    }
}
