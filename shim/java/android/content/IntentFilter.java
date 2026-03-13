package android.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Android-compatible IntentFilter shim.
 *
 * Structured description of Intent values to be matched. Used when
 * registering BroadcastReceiver instances via Context.registerReceiver().
 *
 * Pure Java stub — no OH bridge calls needed.
 */
public class IntentFilter {

    private final List<String> mActions    = new ArrayList<>();
    private final List<String> mCategories = new ArrayList<>();
    private final List<String> mDataSchemes = new ArrayList<>();
    private final List<String> mDataTypes  = new ArrayList<>();

    private int mPriority = 0;

    // ── Constructors ──

    /** Create an empty IntentFilter. */
    public IntentFilter() {}

    /**
     * Create an IntentFilter that matches a single action.
     * @param action the action to match (e.g. Intent.ACTION_VIEW)
     */
    public IntentFilter(String action) {
        addAction(action);
    }

    /**
     * Create an IntentFilter that matches a single action and a single
     * data MIME type.
     * @param action   the action to match
     * @param dataType MIME type to match
     */
    public IntentFilter(String action, String dataType) {
        addAction(action);
        mDataTypes.add(dataType);
    }

    // ── Actions ──

    /** Add an action to match against. */
    public void addAction(String action) {
        if (action != null && !mActions.contains(action)) {
            mActions.add(action);
        }
    }

    /** @return true if this filter includes the given action */
    public boolean hasAction(String action) {
        return mActions.contains(action);
    }

    /**
     * Match an action against this filter.
     * @return true if the filter has no actions (matches all) or contains the given action
     */
    public boolean matchAction(String action) {
        if (mActions.isEmpty()) return true;
        return mActions.contains(action);
    }

    /** Return the number of actions in this filter. */
    public int countActions() {
        return mActions.size();
    }

    /** Return the action at the given index. */
    public String getAction(int index) {
        return mActions.get(index);
    }

    /** @return an Iterator over the actions in this filter */
    public Iterator<String> actionsIterator() {
        return mActions.iterator();
    }

    // ── Categories ──

    /** Add a category to match against. */
    public void addCategory(String category) {
        if (category != null && !mCategories.contains(category)) {
            mCategories.add(category);
        }
    }

    /** @return true if this filter includes the given category */
    public boolean hasCategory(String category) {
        return mCategories.contains(category);
    }

    /** Return the number of categories in this filter. */
    public int countCategories() {
        return mCategories.size();
    }

    /** Return the category at the given index. */
    public String getCategory(int index) {
        return mCategories.get(index);
    }

    /** @return an Iterator over the categories in this filter */
    public Iterator<String> categoriesIterator() {
        return mCategories.iterator();
    }

    // ── Data schemes ──

    /**
     * Add a URI scheme (e.g. "http", "content") that this filter matches.
     */
    public void addDataScheme(String scheme) {
        if (scheme != null && !mDataSchemes.contains(scheme)) {
            mDataSchemes.add(scheme);
        }
    }

    /** @return true if this filter includes the given data scheme */
    public boolean hasDataScheme(String scheme) {
        return mDataSchemes.contains(scheme);
    }

    /** Return the number of data schemes in this filter. */
    public int countDataSchemes() {
        return mDataSchemes.size();
    }

    /** Return the data scheme at the given index. */
    public String getDataScheme(int index) {
        return mDataSchemes.get(index);
    }

    /** @return an Iterator over the data schemes in this filter */
    public Iterator<String> schemesIterator() {
        return mDataSchemes.iterator();
    }

    // ── Data types ──

    /**
     * Add a MIME data type to match.
     * @param type MIME type (e.g. "image/jpeg", "text/*")
     */
    public void addDataType(String type) {
        if (type != null && !mDataTypes.contains(type)) {
            mDataTypes.add(type);
        }
    }

    /** @return true if this filter includes the given MIME data type */
    public boolean hasDataType(String type) {
        return mDataTypes.contains(type);
    }

    /** Return the number of data types in this filter. */
    public int countDataTypes() {
        return mDataTypes.size();
    }

    /** @return an Iterator over the data types in this filter */
    public Iterator<String> typesIterator() {
        return mDataTypes.iterator();
    }

    // ── Priority ──

    /**
     * Specify the priority of this filter. Receivers with higher priority
     * are called before those with lower priority.
     */
    public void setPriority(int priority) {
        mPriority = priority;
    }

    /** @return the priority of this filter */
    public int getPriority() {
        return mPriority;
    }

    // ── Matching ──

    /**
     * Test whether this filter matches the given Intent action, returning true
     * if it does.
     *
     * Mirrors the real Android IntentFilter.match() logic for action-only
     * matching. More complex URI/type matching is not implemented in this stub.
     *
     * @param action the action from the Intent
     * @return true if this filter matches the action (or has no action constraints)
     */
    public boolean match(String action) {
        return matchAction(action);
    }

    @Override
    public String toString() {
        return "IntentFilter{actions=" + mActions
                + ", categories=" + mCategories
                + ", schemes=" + mDataSchemes + "}";
    }
}
