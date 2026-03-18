package android.appwidget;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import java.util.concurrent.Executor;

public class AppWidgetHostView extends FrameLayout {
    public AppWidgetHostView(Context p0) { super(p0); }
    public AppWidgetHostView(Context p0, int p1, int p2) { super(p0); }

    public int getAppWidgetId() { return 0; }
    public AppWidgetProviderInfo getAppWidgetInfo() { return null; }
    public static Rect getDefaultPaddingForWidget(Context p0, ComponentName p1, Rect p2) { return null; }
    public View getDefaultView() { return null; }
    public View getErrorView() { return null; }
    public void prepareView(View p0) {}
    public void setAppWidget(int p0, AppWidgetProviderInfo p1) {}
    public void setExecutor(Executor p0) {}
    public void setOnLightBackground(boolean p0) {}
    public void updateAppWidget(RemoteViews p0) {}
    public void updateAppWidgetOptions(Bundle p0) {}
    public void updateAppWidgetSize(Bundle p0, int p1, int p2, int p3, int p4) {}
}
