package android.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.app.Fragment
 *
 * Models the Android Fragment lifecycle as a pure Java object.
 * In OH, Fragment maps to a reusable ArkUI @Component with its own lifecycle
 * managed by the host UIAbility/Activity shim. The bridge coordinates
 * attach/detach events with the ArkUI page lifecycle.
 *
 * All lifecycle methods are no-ops by default; apps override what they need.
 */
public class Fragment {

    private Activity activity;
    private Context context;
    private Bundle arguments;
    private View view;
    private String tag;
    private int id;
    private boolean added;
    private boolean detached;
    private boolean hidden;

    // ── Lifecycle ──

    /**
     * Called when the fragment is first attached to its host Activity.
     * Called before onCreate(Bundle).
     */
    public void onAttach(Context context) {
        this.context = context;
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }

    /**
     * Called to do initial creation of the fragment.
     */
    public void onCreate(Bundle savedInstanceState) {}

    /**
     * Called to have the fragment instantiate its user interface view.
     * Return null if the fragment does not provide a UI.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle)
     * has returned, but before any saved state has been restored in to the view.
     */
    public void onViewCreated(View view, Bundle savedInstanceState) {}

    /**
     * Called when the fragment's activity has been created and this fragment's
     * view hierarchy instantiated.
     */
    public void onActivityCreated(Bundle savedInstanceState) {}

    /** Called when the Fragment is visible to the user. */
    public void onStart() {}

    /** Called when the fragment is visible to the user and actively running. */
    public void onResume() {}

    /** Called when the Fragment is no longer resumed. */
    public void onPause() {}

    /** Called when the Fragment is no longer started. */
    public void onStop() {}

    /** Called when the view previously created by onCreateView has been detached. */
    public void onDestroyView() {
        this.view = null;
    }

    /** Called when the fragment is no longer in use. */
    public void onDestroy() {}

    /** Called when the fragment is no longer attached to its activity. */
    public void onDetach() {
        this.activity = null;
        this.context = null;
    }

    // ── Host accessors ──

    /**
     * Return the Activity this fragment is currently associated with.
     * Returns null if not currently attached.
     */
    public final Activity getActivity() {
        return activity;
    }

    /**
     * Return the Context this fragment is currently associated with.
     * Returns null if not currently attached.
     */
    public final Context getContext() {
        return context;
    }

    // ── Arguments ──

    /**
     * Return the arguments supplied to setArguments(Bundle), if any.
     */
    public final Bundle getArguments() {
        return arguments;
    }

    /**
     * Supply the construction arguments for this fragment. The arguments
     * supplied here will be retained across fragment destroy and creation.
     */
    public void setArguments(Bundle args) {
        this.arguments = args;
    }

    // ── View ──

    /**
     * Get the root view for the fragment's layout (the one returned by
     * onCreateView(LayoutInflater, ViewGroup, Bundle)). Returns null if no
     * view has been created, or if onDestroyView() has been called.
     */
    public View getView() {
        return view;
    }

    /**
     * Called by FragmentManager after onCreateView to store the view reference.
     * Package-private — not part of the public Fragment API.
     */
    void setView(View v) {
        this.view = v;
    }

    // ── State queries ──

    /**
     * Return true if the fragment is currently added to its activity.
     */
    public final boolean isAdded() {
        return added && activity != null;
    }

    /**
     * Return true if the fragment has been explicitly detached from the UI.
     * Detached fragments are not destroyed; they can be re-attached later via
     * FragmentTransaction.attach().
     */
    public final boolean isDetached() {
        return detached;
    }

    /**
     * Return true if the fragment is currently visible to the user.
     * A fragment is visible when it is added, not hidden, and its Activity is started.
     */
    public final boolean isVisible() {
        return added && !hidden && !detached && view != null;
    }

    // ── Resources (stub) ──

    /**
     * Return a localized string from the application's package's default string
     * table. This shim returns a synthetic placeholder string.
     */
    public String getString(int resId) {
        return "string_" + resId;
    }

    // ── Tag / ID ──

    /**
     * Get the tag name of the fragment, if specified.
     */
    public final String getTag() {
        return tag;
    }

    /**
     * Set the tag for this fragment. Called by FragmentManager when the fragment
     * is added with a tag via FragmentTransaction.add(int, Fragment, String).
     * Package-private — apps do not call this directly.
     */
    void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Return the identifier this fragment is known by. This is either the
     * android:id value supplied in a layout or the container view ID supplied
     * when adding the fragment.
     */
    public final int getId() {
        return id;
    }

    /**
     * Set the container view ID for this fragment. Called by FragmentManager.
     * Package-private.
     */
    void setId(int id) {
        this.id = id;
    }

    // ── Internal state mutators (called by FragmentManager) ──

    void setAdded(boolean added) {
        this.added = added;
    }

    void setDetached(boolean detached) {
        this.detached = detached;
    }

    void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{tag=" + tag + ", id=" + id
                + ", added=" + added + ", detached=" + detached + "}";
    }
}
