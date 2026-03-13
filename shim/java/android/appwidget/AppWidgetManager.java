package android.appwidget;

import java.util.List;
import java.util.ArrayList;

/**
 * Android-compatible AppWidgetManager shim.
 * Stub — no real widget host; all mutating calls are no-ops.
 */
public class AppWidgetManager {

    public static final String ACTION_APPWIDGET_UPDATE    = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String ACTION_APPWIDGET_CONFIGURE = "android.appwidget.action.APPWIDGET_CONFIGURE";
    public static final String EXTRA_APPWIDGET_ID         = "appWidgetId";
    public static final String EXTRA_APPWIDGET_IDS        = "appWidgetIds";
    public static final int    INVALID_APPWIDGET_ID       = 0;

    private static final AppWidgetManager sInstance = new AppWidgetManager();

    private AppWidgetManager() {}

    /** Returns the singleton shim instance (context is ignored). */
    public static AppWidgetManager getInstance(Object context) {
        return sInstance;
    }

    /** Updates the widget identified by appWidgetId with the given RemoteViews. No-op in shim. */
    public void updateAppWidget(int appWidgetId, android.widget.RemoteViews views) {
        // stub — no-op
    }

    /** Bulk update overload. No-op in shim. */
    public void updateAppWidget(int[] appWidgetIds, android.widget.RemoteViews views) {
        // stub — no-op
    }

    /** Returns an empty array — no widgets hosted in shim environment. */
    public int[] getAppWidgetIds(Object provider) {
        return new int[0];
    }

    /** Returns an empty list — no providers installed in shim environment. */
    public List<AppWidgetProviderInfo> getInstalledProviders() {
        return new ArrayList<AppWidgetProviderInfo>();
    }

    /** Returns an empty list for the given category filter. */
    public List<AppWidgetProviderInfo> getInstalledProviders(int categoryFilter) {
        return new ArrayList<AppWidgetProviderInfo>();
    }

    /**
     * Attempts to bind appWidgetId to provider. Always returns false in shim
     * (caller must use the real BIND_APPWIDGET permission on device).
     */
    public boolean bindAppWidgetIdIfAllowed(int appWidgetId, Object provider) {
        return false;
    }
}
