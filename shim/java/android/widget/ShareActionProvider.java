package android.widget;

/**
 * Android-compatible ShareActionProvider shim.
 * Extends Object because android.view.ActionProvider is not present in this shim layer.
 * Manages a share action in the action bar, including a share history file.
 */
public class ShareActionProvider {

    /** Default file name for the share history. */
    public static final String DEFAULT_SHARE_HISTORY_FILE_NAME = "share_history.xml";

    private Object mShareIntent;
    private OnShareTargetSelectedListener mListener;
    private String mShareHistoryFileName = DEFAULT_SHARE_HISTORY_FILE_NAME;

    public ShareActionProvider(Object context) {}

    // -----------------------------------------------------------------------
    // Share intent
    // -----------------------------------------------------------------------

    /**
     * Set the intent that will be used when the user taps the default share target.
     * @param shareIntent an Intent with action {@code Intent.ACTION_SEND} (or SEND_MULTIPLE)
     */
    public void setShareIntent(Object shareIntent) {
        mShareIntent = shareIntent;
    }

    public Object getShareIntent() {
        return mShareIntent;
    }

    // -----------------------------------------------------------------------
    // History
    // -----------------------------------------------------------------------

    public void setShareHistoryFileName(String shareHistoryFile) {
        mShareHistoryFileName = shareHistoryFile;
    }

    public String getShareHistoryFileName() {
        return mShareHistoryFileName;
    }

    // -----------------------------------------------------------------------
    // Listener
    // -----------------------------------------------------------------------

    /**
     * Register a listener that is called whenever the user selects a share target.
     */
    public void setOnShareTargetSelectedListener(OnShareTargetSelectedListener listener) {
        mListener = listener;
    }

    // -----------------------------------------------------------------------
    // ActionProvider surface stubs
    // -----------------------------------------------------------------------

    /** Returns a view to be placed in the action bar. Stub returns null. */
    public Object onCreateActionView() {
        return null;
    }

    public boolean onPerformDefaultAction() {
        return false;
    }

    public boolean hasSubMenu() {
        return true;
    }

    // -----------------------------------------------------------------------
    // Inner listener interface
    // -----------------------------------------------------------------------

    /**
     * Listener called when the user selects a share target from the drop-down.
     */
    public interface OnShareTargetSelectedListener {
        /**
         * Called when a share target has been selected. Return true to override the
         * default launch behaviour; false to let the framework proceed normally.
         *
         * @param source  the ShareActionProvider that was interacted with
         * @param intent  the intent that will be sent to the chosen share target
         * @return true to handle the selection; false for default behaviour
         */
        boolean onShareTargetSelected(ShareActionProvider source, Object intent);
    }
}
