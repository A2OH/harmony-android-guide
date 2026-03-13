package android.widget;

import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.widget.HeaderViewListAdapter — wraps a {@link ListAdapter} and prepends
 * header rows and appends footer rows around the core data set.
 *
 * This is the adapter installed by {@link ListView} whenever header or footer views are
 * added via {@code addHeaderView()} / {@code addFooterView()}.
 */
public class HeaderViewListAdapter implements WrapperListAdapter {

    // ── FixedViewInfo ────────────────────────────────────────────────────────

    /**
     * Mirrors the internal ListView.FixedViewInfo struct: a view pinned at the
     * top (header) or bottom (footer) of the list, with an optional data object
     * and an isSelectable flag.
     */
    public static class FixedViewInfo {
        public View   view;
        public Object data;
        public boolean isSelectable;

        public FixedViewInfo(View view, Object data, boolean isSelectable) {
            this.view         = view;
            this.data         = data;
            this.isSelectable = isSelectable;
        }
    }

    // ── State ────────────────────────────────────────────────────────────────

    private final List<FixedViewInfo> mHeaderViewInfos;
    private final List<FixedViewInfo> mFooterViewInfos;
    private final ListAdapter         mAdapter;

    /** Sentinel empty list used when callers pass null. */
    private static final List<FixedViewInfo> EMPTY = new ArrayList<>(0);

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * @param headerViewInfos  header row descriptors (may be null)
     * @param footerViewInfos  footer row descriptors (may be null)
     * @param adapter          the core data adapter (may be null)
     */
    public HeaderViewListAdapter(List<FixedViewInfo> headerViewInfos,
                                 List<FixedViewInfo> footerViewInfos,
                                 ListAdapter adapter) {
        this.mHeaderViewInfos = (headerViewInfos != null) ? headerViewInfos : EMPTY;
        this.mFooterViewInfos = (footerViewInfos != null) ? footerViewInfos : EMPTY;
        this.mAdapter         = adapter;
    }

    // ── WrapperListAdapter ───────────────────────────────────────────────────

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    // ── Header / footer counts ───────────────────────────────────────────────

    public int getHeadersCount() { return mHeaderViewInfos.size(); }
    public int getFootersCount() { return mFooterViewInfos.size(); }

    // ── ListAdapter ──────────────────────────────────────────────────────────

    @Override
    public int getCount() {
        int coreCount = (mAdapter != null) ? mAdapter.getCount() : 0;
        return mHeaderViewInfos.size() + coreCount + mFooterViewInfos.size();
    }

    @Override
    public Object getItem(int position) {
        int numHeaders = mHeaderViewInfos.size();
        if (position < numHeaders) {
            return mHeaderViewInfos.get(position).data;
        }
        int adjPosition = position - numHeaders;
        int coreCount = (mAdapter != null) ? mAdapter.getCount() : 0;
        if (adjPosition < coreCount) {
            return (mAdapter != null) ? mAdapter.getItem(adjPosition) : null;
        }
        int footerIndex = adjPosition - coreCount;
        if (footerIndex < mFooterViewInfos.size()) {
            return mFooterViewInfos.get(footerIndex).data;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        int numHeaders = mHeaderViewInfos.size();
        int coreCount  = (mAdapter != null) ? mAdapter.getCount() : 0;
        int adjPosition = position - numHeaders;
        if (adjPosition >= 0 && adjPosition < coreCount && mAdapter != null) {
            return mAdapter.getItemId(adjPosition);
        }
        return -1L;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int numHeaders = mHeaderViewInfos.size();
        if (position < numHeaders) {
            return mHeaderViewInfos.get(position).view;
        }
        int adjPosition = position - numHeaders;
        int coreCount = (mAdapter != null) ? mAdapter.getCount() : 0;
        if (adjPosition < coreCount) {
            return (mAdapter != null)
                    ? mAdapter.getView(adjPosition, convertView, parent)
                    : convertView;
        }
        int footerIndex = adjPosition - coreCount;
        if (footerIndex < mFooterViewInfos.size()) {
            return mFooterViewInfos.get(footerIndex).view;
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        int numHeaders = mHeaderViewInfos.size();
        int coreCount  = (mAdapter != null) ? mAdapter.getCount() : 0;
        int adjPosition = position - numHeaders;
        if (adjPosition >= 0 && adjPosition < coreCount && mAdapter != null) {
            return mAdapter.getItemViewType(adjPosition);
        }
        // Header and footer rows use a dedicated view-type bucket that adapters
        // do not own; returning IGNORE_ITEM_VIEW_TYPE signals RecyclerBin to skip.
        return -1; /* AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER */
    }

    @Override
    public int getViewTypeCount() {
        return (mAdapter != null) ? mAdapter.getViewTypeCount() : 1;
    }

    @Override
    public boolean isEmpty() {
        return (mAdapter == null || mAdapter.isEmpty())
                && mHeaderViewInfos.isEmpty()
                && mFooterViewInfos.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        // Headers/footers may be non-selectable; delegate the rest to the adapter.
        if (mAdapter != null && !mAdapter.areAllItemsEnabled()) return false;
        for (FixedViewInfo fi : mHeaderViewInfos) { if (!fi.isSelectable) return false; }
        for (FixedViewInfo fi : mFooterViewInfos) { if (!fi.isSelectable) return false; }
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        int numHeaders = mHeaderViewInfos.size();
        if (position < numHeaders) {
            return mHeaderViewInfos.get(position).isSelectable;
        }
        int adjPosition = position - numHeaders;
        int coreCount = (mAdapter != null) ? mAdapter.getCount() : 0;
        if (adjPosition < coreCount) {
            return (mAdapter == null) || mAdapter.isEnabled(adjPosition);
        }
        int footerIndex = adjPosition - coreCount;
        if (footerIndex < mFooterViewInfos.size()) {
            return mFooterViewInfos.get(footerIndex).isSelectable;
        }
        return false;
    }
}
