package android.widget;

/**
 * Android-compatible RemoteViewsService shim.
 * A Service that returns a RemoteViewsFactory for populating collection views
 * (e.g. ListView, GridView, StackView) hosted in a remote process such as an app widget.
 *
 * Extends Object because android.app.Service is not present in this shim layer.
 */
public abstract class RemoteViewsService {

    // -----------------------------------------------------------------------
    // Service lifecycle stubs
    // -----------------------------------------------------------------------

    public void onCreate() {}
    public void onDestroy() {}

    /**
     * Return the IBinder for binding. In a real implementation this returns
     * an internal binder that the framework uses to call onGetViewFactory.
     * Stub returns null.
     */
    public Object onBind(Object intent) {
        return null;
    }

    // -----------------------------------------------------------------------
    // Abstract factory accessor
    // -----------------------------------------------------------------------

    /**
     * To be implemented by the concrete service. Return the RemoteViewsFactory
     * that will supply views for the collection identified by {@code intent}.
     */
    public abstract RemoteViewsFactory onGetViewFactory(Object intent);

    // -----------------------------------------------------------------------
    // Inner interface
    // -----------------------------------------------------------------------

    /**
     * An interface for an adapter between a remote collection view (ListView, GridView, etc.)
     * and the underlying data for that view.
     */
    public interface RemoteViewsFactory {

        /** Called when the factory is first created. */
        void onCreate();

        /** Called when the data backing the factory has changed. */
        void onDataSetChanged();

        /** Called when the last RemoteViewsAdapter that is backed by this factory is unbound. */
        void onDestroy();

        /** Returns the number of items in the data set. */
        int getCount();

        /**
         * Returns a RemoteViews representing the data at the specified position.
         * @param position the position of the item within the data set
         * @return the RemoteViews object for the item at position
         */
        RemoteViews getViewAt(int position);

        /**
         * Returns a RemoteViews to be displayed as a loading indicator while the actual
         * view for the item is being prepared. Return null for the default loading view.
         */
        RemoteViews getLoadingView();

        /** Returns the number of distinct view types. */
        int getViewTypeCount();

        /** Returns the stable ID for the item at the given position. */
        long getItemId(int position);

        /** Returns true if the same ID always refers to the same object. */
        boolean hasStableIds();
    }
}
