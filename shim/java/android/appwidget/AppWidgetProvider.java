package android.appwidget;

import android.content.BroadcastReceiver;

/**
 * Android-compatible AppWidgetProvider shim.
 * Extends BroadcastReceiver; all lifecycle callbacks are empty stubs.
 */
public class AppWidgetProvider extends BroadcastReceiver {

    /**
     * Called on ACTION_APPWIDGET_UPDATE. Override to refresh widget UI.
     *
     * @param context  hosting context (Object in shim)
     * @param appWidgetManager the AppWidgetManager instance
     * @param appWidgetIds     IDs of widgets that need updating
     */
    public void onUpdate(Object context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // stub — override in real implementation
    }

    /**
     * Called when the first widget instance is created.
     */
    public void onEnabled(Object context) {
        // stub
    }

    /**
     * Called when the last widget instance is removed.
     */
    public void onDisabled(Object context) {
        // stub
    }

    /**
     * Called when widget instances are deleted.
     */
    public void onDeleted(Object context, int[] appWidgetIds) {
        // stub
    }

    /**
     * Called when the options (size hints) for a widget change.
     */
    public void onAppWidgetOptionsChanged(Object context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Object newOptions) {
        // stub
    }

    /**
     * Dispatches the incoming broadcast to the appropriate lifecycle callback.
     * Subclasses may override this or override the individual callbacks.
     */
    @Override
    public void onReceive(Object context, Object intent) {
        // stub — real dispatch would inspect intent action
    }
}
