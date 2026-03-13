package android.widget;

/**
 * Shim: android.widget.WrapperListAdapter — a {@link ListAdapter} that decorates another
 * ListAdapter and exposes the wrapped instance.
 *
 * Implementations such as {@link HeaderViewListAdapter} delegate all data-access calls
 * to the wrapped adapter while adding header/footer rows around the core data.
 */
public interface WrapperListAdapter extends ListAdapter {

    /**
     * Returns the adapter wrapped by this WrapperListAdapter.
     *
     * @return the underlying {@link ListAdapter} being decorated
     */
    ListAdapter getWrappedAdapter();
}
