package android.appwidget;

public class AppWidgetManager {
    public AppWidgetManager() {}

    public static final int ACTION_APPWIDGET_BIND = 0;
    public static final int ACTION_APPWIDGET_CONFIGURE = 0;
    public static final int ACTION_APPWIDGET_DELETED = 0;
    public static final int ACTION_APPWIDGET_DISABLED = 0;
    public static final int ACTION_APPWIDGET_ENABLED = 0;
    public static final int ACTION_APPWIDGET_HOST_RESTORED = 0;
    public static final int ACTION_APPWIDGET_OPTIONS_CHANGED = 0;
    public static final int ACTION_APPWIDGET_PICK = 0;
    public static final int ACTION_APPWIDGET_RESTORED = 0;
    public static final int ACTION_APPWIDGET_UPDATE = 0;
    public static final int EXTRA_APPWIDGET_ID = 0;
    public static final int EXTRA_APPWIDGET_IDS = 0;
    public static final int EXTRA_APPWIDGET_OLD_IDS = 0;
    public static final int EXTRA_APPWIDGET_OPTIONS = 0;
    public static final int EXTRA_APPWIDGET_PREVIEW = 0;
    public static final int EXTRA_APPWIDGET_PROVIDER = 0;
    public static final int EXTRA_APPWIDGET_PROVIDER_PROFILE = 0;
    public static final int EXTRA_CUSTOM_EXTRAS = 0;
    public static final int EXTRA_CUSTOM_INFO = 0;
    public static final int EXTRA_HOST_ID = 0;
    public static final int INVALID_APPWIDGET_ID = 0;
    public static final int META_DATA_APPWIDGET_PROVIDER = 0;
    public static final int OPTION_APPWIDGET_HOST_CATEGORY = 0;
    public static final int OPTION_APPWIDGET_MAX_HEIGHT = 0;
    public static final int OPTION_APPWIDGET_MAX_WIDTH = 0;
    public static final int OPTION_APPWIDGET_MIN_HEIGHT = 0;
    public static final int OPTION_APPWIDGET_MIN_WIDTH = 0;
    public static final int OPTION_APPWIDGET_RESTORE_COMPLETED = 0;
    public boolean bindAppWidgetIdIfAllowed(Object p0, Object p1) { return false; }
    public boolean bindAppWidgetIdIfAllowed(Object p0, Object p1, Object p2) { return false; }
    public boolean bindAppWidgetIdIfAllowed(Object p0, Object p1, Object p2, Object p3) { return false; }
    public int getAppWidgetIds(Object p0) { return 0; }
    public Object getAppWidgetInfo(Object p0) { return null; }
    public Object getAppWidgetOptions(Object p0) { return null; }
    public Object getInstalledProviders() { return null; }
    public static Object getInstance(Object p0) { return null; }
    public boolean isRequestPinAppWidgetSupported() { return false; }
    public void notifyAppWidgetViewDataChanged(Object p0, Object p1) {}
    public void partiallyUpdateAppWidget(Object p0, Object p1) {}
    public boolean requestPinAppWidget(Object p0, Object p1, Object p2) { return false; }
    public void updateAppWidget(Object p0, Object p1) {}
    public void updateAppWidgetOptions(Object p0, Object p1) {}
    public void updateAppWidgetProviderInfo(Object p0, Object p1) {}
}
