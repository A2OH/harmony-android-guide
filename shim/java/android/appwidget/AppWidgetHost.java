package android.appwidget;

public class AppWidgetHost {
    public AppWidgetHost() {}

    public int allocateAppWidgetId() { return 0; }
    public void clearViews() {}
    public Object createView(Object p0, Object p1, Object p2) { return null; }
    public static void deleteAllHosts() {}
    public void deleteAppWidgetId(Object p0) {}
    public void deleteHost() {}
    public int getAppWidgetIds() { return 0; }
    public void onAppWidgetRemoved(Object p0) {}
    public Object onCreateView(Object p0, Object p1, Object p2) { return null; }
    public void onProviderChanged(Object p0, Object p1) {}
    public void onProvidersChanged() {}
    public void startAppWidgetConfigureActivityForResult(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public void startListening() {}
    public void stopListening() {}
}
