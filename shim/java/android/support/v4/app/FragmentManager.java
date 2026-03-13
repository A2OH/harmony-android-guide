package android.support.v4.app;

/**
 * Android support-library FragmentManager shim. Delegates to the framework stub.
 *
 * android.support.v4.app.FragmentManager is the support-library counterpart of
 * android.app.FragmentManager. It is used by FragmentActivity-based apps to
 * manage Fragment back-stacks in a backward-compatible way.
 *
 * This shim mirrors the public API surface and delegates to a simple in-memory
 * implementation — no OH bridge calls needed.
 *
 * OH migration note: Fragment navigation maps to ArkUI Navigation/router.
 */
public class FragmentManager {

    /**
     * Start a series of fragment edit operations.
     *
     * @return a new FragmentTransaction
     */
    public FragmentTransaction beginTransaction() {
        return new FragmentTransaction();
    }

    /**
     * Find a fragment by its container view ID.
     *
     * @param id the container view resource ID
     * @return the Fragment, or null if not found
     */
    public Fragment findFragmentById(int id) {
        return null; // stub
    }

    /**
     * Find a fragment by its tag.
     *
     * @param tag the tag name to search for
     * @return the Fragment, or null if not found
     */
    public Fragment findFragmentByTag(String tag) {
        return null; // stub
    }

    /**
     * Pop the top entry from the back stack. No-op stub.
     */
    public void popBackStack() {}

    /**
     * Pop the back stack until the entry with the given name is at the top.
     * No-op stub.
     *
     * @param name  name of the back-stack state to pop to, or null to pop everything
     * @param flags 0 or FragmentManager.POP_BACK_STACK_INCLUSIVE
     */
    public void popBackStack(String name, int flags) {}

    /**
     * Returns the number of entries in the back stack. Always 0 (stub).
     */
    public int getBackStackEntryCount() { return 0; }

    /**
     * Execute all pending actions immediately. No-op stub.
     */
    public void executePendingTransactions() {}

    // ── Constants ──────────────────────────────────────────────────────────────

    /** Pop the back stack to include the named state. */
    public static final int POP_BACK_STACK_INCLUSIVE = 1;
}
