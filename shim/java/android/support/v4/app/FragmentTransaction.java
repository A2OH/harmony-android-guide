package android.support.v4.app;

/**
 * Android support-library FragmentTransaction shim. No-op stub.
 *
 * All mutating methods return {@code this} to allow chaining.
 * commit() / commitAllowingStateLoss() are no-ops returning 0.
 */
public class FragmentTransaction {

    public FragmentTransaction add(int containerViewId, Fragment fragment) { return this; }
    public FragmentTransaction add(int containerViewId, Fragment fragment, String tag) { return this; }
    public FragmentTransaction replace(int containerViewId, Fragment fragment) { return this; }
    public FragmentTransaction replace(int containerViewId, Fragment fragment, String tag) { return this; }
    public FragmentTransaction remove(Fragment fragment) { return this; }
    public FragmentTransaction hide(Fragment fragment) { return this; }
    public FragmentTransaction show(Fragment fragment) { return this; }
    public FragmentTransaction detach(Fragment fragment) { return this; }
    public FragmentTransaction attach(Fragment fragment) { return this; }
    public FragmentTransaction addToBackStack(String name) { return this; }
    public FragmentTransaction setTransition(int transit) { return this; }
    public FragmentTransaction setCustomAnimations(int enter, int exit) { return this; }
    public FragmentTransaction setCustomAnimations(int enter, int exit, int popEnter, int popExit) { return this; }

    /** Commit this transaction. No-op stub — returns 0. */
    public int commit() { return 0; }

    /** Commit allowing state loss. No-op stub — returns 0. */
    public int commitAllowingStateLoss() { return 0; }

    /** Commit synchronously. No-op stub. */
    public void commitNow() {}

    /** Commit synchronously allowing state loss. No-op stub. */
    public void commitNowAllowingStateLoss() {}
}
