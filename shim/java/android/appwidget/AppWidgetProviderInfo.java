package android.appwidget;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;

public class AppWidgetProviderInfo implements Parcelable {
    public static final int RESIZE_BOTH = 0;
    public static final int RESIZE_HORIZONTAL = 0;
    public static final int RESIZE_NONE = 0;
    public static final int RESIZE_VERTICAL = 0;
    public static final int WIDGET_CATEGORY_HOME_SCREEN = 0;
    public static final int WIDGET_CATEGORY_KEYGUARD = 0;
    public static final int WIDGET_CATEGORY_SEARCHBOX = 0;
    public static final int WIDGET_FEATURE_HIDE_FROM_PICKER = 0;
    public static final int WIDGET_FEATURE_RECONFIGURABLE = 0;
    public int autoAdvanceViewId = 0;
    public int configure = 0;
    public int icon = 0;
    public int initialKeyguardLayout = 0;
    public int initialLayout = 0;
    public int minHeight = 0;
    public int minResizeHeight = 0;
    public int minResizeWidth = 0;
    public int minWidth = 0;
    public int previewImage = 0;
    public int provider = 0;
    public int resizeMode = 0;
    public int updatePeriodMillis = 0;
    public int widgetCategory = 0;
    public int widgetFeatures = 0;

    public AppWidgetProviderInfo() {}
    public AppWidgetProviderInfo(Parcel p0) {}

    public AppWidgetProviderInfo clone() { return null; }
    public int describeContents() { return 0; }
    public UserHandle getProfile() { return null; }
    public Drawable loadIcon(Context p0, int p1) { return null; }
    public String loadLabel(PackageManager p0) { return null; }
    public Drawable loadPreviewImage(Context p0, int p1) { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
