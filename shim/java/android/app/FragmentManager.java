package android.app;

import java.util.HashMap;
import java.util.Map;

/**
 * Shim: android.app.FragmentManager
 *
 * Interface for interacting with Fragment objects inside an Activity.
 * Provides an abstract API matching the AOSP contract, with a concrete
 * inner implementation (SimpleFragmentManager) used by the Activity shim.
 *
 * In OH, the role of FragmentManager is fulfilled by the router/navigation
 * stack managed by the ArkUI Navigation component. This shim preserves the
 * Android API surface so existing Java code compiles unchanged.
 */
public abstract class FragmentManager {

    /**
     * Start a series of edit operations on the Fragments associated with this
     * FragmentManager. Note: A Fragment transaction can only be created/committed
     * prior to an activity saving its state. If you try to commit a transaction
     * after Activity.onSaveInstanceState(), you will get an error.
     */
    public abstract FragmentTransaction beginTransaction();

    /**
     * Find an existing fragment by its ID. This is most useful for static
     * fragments declared in layouts.
     *
     * @param id  The ID assigned to the fragment via setId or from the container view.
     * @return    The fragment, or null if no fragment with that ID is active.
     */
    public abstract Fragment findFragmentById(int id);

    /**
     * Find an existing fragment by its tag.
     *
     * @param tag The tag name for the fragment to find.
     * @return    The fragment, or null if no fragment with that tag is active.
     */
    public abstract Fragment findFragmentByTag(String tag);

    // ── Back stack ──

    /**
     * Pop the top state off the back stack. Returns immediately without waiting
     * for the pop to complete; if you need to know the state has changed, use
     * addOnBackStackChangedListener(OnBackStackChangedListener).
     * This shim is a no-op — back stack management is handled by OH router.
     */
    public void popBackStack() {}

    /**
     * Return the number of entries currently in the back stack.
     */
    public int getBackStackEntryCount() {
        return 0;
    }

    // ── Concrete implementation ──

    /**
     * Simple concrete FragmentManager used by the Activity shim.
     * Tracks fragments by ID and tag in plain HashMaps; commits are applied
     * immediately (no deferred layout pass).
     */
    public static class SimpleFragmentManager extends FragmentManager {

        // Fragments keyed by container view ID
        private final Map<Integer, Fragment> fragmentsById = new HashMap<>();

        // Fragments keyed by tag
        private final Map<String, Fragment> fragmentsByTag = new HashMap<>();

        // The host activity (used to supply Context to fragments on attach)
        private final Activity hostActivity;

        public SimpleFragmentManager(Activity hostActivity) {
            this.hostActivity = hostActivity;
        }

        @Override
        public FragmentTransaction beginTransaction() {
            return new FragmentTransaction.SimpleFragmentTransaction(this);
        }

        @Override
        public Fragment findFragmentById(int id) {
            return fragmentsById.get(id);
        }

        @Override
        public Fragment findFragmentByTag(String tag) {
            if (tag == null) return null;
            return fragmentsByTag.get(tag);
        }

        // ── Internal add/replace/remove used by SimpleFragmentTransaction ──

        void performAdd(int containerViewId, Fragment fragment, String tag) {
            fragment.setId(containerViewId);
            fragment.setTag(tag);
            fragment.setAdded(true);
            fragment.setDetached(false);

            // Drive the attach → create → createView → viewCreated → activityCreated lifecycle
            fragment.onAttach(hostActivity);
            fragment.onCreate(null);

            android.view.View inflated = fragment.onCreateView(
                    android.view.LayoutInflater.from(hostActivity), null, null);
            fragment.setView(inflated);
            fragment.onViewCreated(inflated != null ? inflated : new android.view.View(), null);
            fragment.onActivityCreated(null);
            fragment.onStart();
            fragment.onResume();

            fragmentsById.put(containerViewId, fragment);
            if (tag != null) {
                fragmentsByTag.put(tag, fragment);
            }
        }

        void performReplace(int containerViewId, Fragment fragment, String tag) {
            // Tear down any existing fragment in this container first
            Fragment existing = fragmentsById.get(containerViewId);
            if (existing != null) {
                performRemove(existing);
            }
            performAdd(containerViewId, fragment, tag);
        }

        void performRemove(Fragment fragment) {
            fragment.onPause();
            fragment.onStop();
            fragment.onDestroyView();
            fragment.onDestroy();
            fragment.onDetach();
            fragment.setAdded(false);

            fragmentsById.remove(fragment.getId());
            if (fragment.getTag() != null) {
                fragmentsByTag.remove(fragment.getTag());
            }
        }

        void performHide(Fragment fragment) {
            fragment.setHidden(true);
        }

        void performShow(Fragment fragment) {
            fragment.setHidden(false);
        }
    }
}
