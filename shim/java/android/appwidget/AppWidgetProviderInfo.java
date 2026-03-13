package android.appwidget;

/**
 * Android-compatible AppWidgetProviderInfo shim.
 * Plain data-holder; all fields default to zero/null.
 */
public class AppWidgetProviderInfo {

    /** Widget may be placed on the home screen. */
    public static final int WIDGET_CATEGORY_HOME_SCREEN = 1;
    /** Widget may be placed on the keyguard (lock screen). */
    public static final int WIDGET_CATEGORY_KEYGUARD    = 2;
    /** Widget may be used as a search box. */
    public static final int WIDGET_CATEGORY_SEARCHBOX   = 4;

    /** Minimum width in dp. */
    public int minWidth;

    /** Minimum height in dp. */
    public int minHeight;

    /** How often the widget should be updated, in milliseconds. 0 = never auto-update. */
    public int updatePeriodMillis;

    /** Resource id of the initial layout. */
    public int initialLayout;

    /** ComponentName (as Object) of the configuration activity, or null. */
    public Object configure;

    /** Human-readable label for this widget. */
    public String label;

    /** Resource id of the preview image. */
    public int previewImage;

    /** Combination of WIDGET_CATEGORY_* flags. */
    public int widgetCategory = WIDGET_CATEGORY_HOME_SCREEN;

    public AppWidgetProviderInfo() {}
}
