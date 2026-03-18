package android.widget;
import android.view.View;
import android.view.View;
import java.util.Set;

import android.view.View;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.SearchView → ArkUI COLUMN + TEXT_INPUT node.
 *
 * SearchView is a composite widget containing a search icon and an
 * EditText-like input field. In the shim it extends LinearLayout (COLUMN)
 * and holds an internal EditText child to capture user input. When the
 * app is in iconified mode the EditText is hidden.
 */
public class SearchView extends LinearLayout {

    // Reuse TEXT_INPUT node type from EditText
    static final int NODE_TYPE_TEXT_INPUT = 7;
    static final int ATTR_TEXT_PLACEHOLDER = 7002;

    private CharSequence mQuery = "";
    private boolean mIconified = true;
    private OnQueryTextListener mQueryTextListener;
    private String mQueryHint;

    // Internal text input node for capturing the search query
    private final EditText mSearchInput;

    public SearchView() { super(new android.content.Context());
        setOrientation(HORIZONTAL);
        mSearchInput = new EditText(new android.content.Context());
        mSearchInput.setVisibility(mIconified ? GONE : VISIBLE);
        addView(mSearchInput);
    }

    /** Constructor accepting a context-like object (ignored in shim). */
    public SearchView(Object context) {
        this();
    }

    /** Constructor accepting context and attribute set (both ignored). */
    public SearchView(Object context, Object attrs) {
        this();
    }

    /** Constructor accepting context, attribute set and default style (all ignored). */
    public SearchView(Object context, Object attrs, int defStyleAttr) {
        this();
    }

    // ── Query ──

    /**
     * Set the query text in the search field.
     *
     * @param query  the query text to set
     * @param submit if true, fire the onQueryTextSubmit callback immediately
     */
    public void setQuery(CharSequence query, boolean submit) {
        mQuery = query != null ? query : "";
        mSearchInput.setText(mQuery);
        if (mQueryTextListener != null) {
            mQueryTextListener.onQueryTextChange(mQuery.toString());
            if (submit) {
                mQueryTextListener.onQueryTextSubmit(mQuery.toString());
            }
        }
    }

    /** Return the current query text. */
    public CharSequence getQuery() {
        return mQuery;
    }

    /** Set the hint text shown when the search field is empty. */
    public void setQueryHint(CharSequence hint) {
        mQueryHint = hint != null ? hint.toString() : null;
    }

    public CharSequence getQueryHint() {
        return mQueryHint;
    }

    // ── Iconified state ──

    /**
     * Iconify or expand the search view.
     * When iconified the text input is hidden and only the search icon shows.
     */
    public void setIconified(boolean iconified) {
        mIconified = iconified;
        mSearchInput.setVisibility(iconified ? GONE : VISIBLE);
    }

    public boolean isIconified() {
        return mIconified;
    }

    /** Set whether the view can be iconified at all. */
    public void setIconifiedByDefault(boolean iconified) {
        mIconified = iconified;
        mSearchInput.setVisibility(iconified ? GONE : VISIBLE);
    }

    // ── Listener ──

    public void setOnQueryTextListener(OnQueryTextListener listener) {
        mQueryTextListener = listener;
    }

    // ── OnQueryTextListener interface ──

    public interface OnQueryTextListener {
        /**
         * Called when the user submits the query (presses the search button
         * or action key).
         *
         * @return true if the listener consumed the action; false to let
         *         the SearchView perform the default action
         */
        boolean onQueryTextSubmit(String query);

        /**
         * Called when the query text has changed.
         *
         * @return true if the listener consumed the change; false to allow
         *         further processing
         */
        boolean onQueryTextChange(String newText);
    }
}
