package android.widget;

import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.Map;

/**
 * Shim: android.widget.SimpleAdapter — adapter that maps Map entries to Views.
 *
 * The resource ID and from/to mapping arrays are accepted for source compatibility.
 * getView() returns null in headless mode (no layout inflation).
 * ViewBinder is stored and accessible for callers that check for its presence.
 */
public class SimpleAdapter extends BaseAdapter {

    private final Object context;
    private final List<? extends Map<String, ?>> data;
    private final int resource;
    private final String[] from;
    private final int[] to;
    private ViewBinder viewBinder;

    public SimpleAdapter(Object context,
                         List<? extends Map<String, ?>> data,
                         int resource,
                         String[] from,
                         int[] to) {
        this.context = context;
        this.data = data;
        this.resource = resource;
        this.from = from;
        this.to = to;
    }

    // ── ViewBinder ──

    public void setViewBinder(ViewBinder viewBinder) {
        this.viewBinder = viewBinder;
    }

    public ViewBinder getViewBinder() { return viewBinder; }

    // ── BaseAdapter contract ──

    @Override
    public int getCount() { return data != null ? data.size() : 0; }

    @Override
    public Object getItem(int position) { return data != null ? data.get(position) : null; }

    @Override
    public long getItemId(int position) { return position; }

    /** Returns null in headless shim — layout inflation not supported. */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) { return null; }

    // ── Interface ──

    public interface ViewBinder {
        /**
         * Binds the specified data to the specified view.
         * @return true if binding was handled, false to use default binding
         */
        boolean setViewValue(View view, Object data, String textRepresentation);
    }
}
