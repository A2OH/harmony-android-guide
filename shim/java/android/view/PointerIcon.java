package android.view;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public final class PointerIcon implements Parcelable {
    public static final int TYPE_ALIAS = 0;
    public static final int TYPE_ALL_SCROLL = 0;
    public static final int TYPE_ARROW = 0;
    public static final int TYPE_CELL = 0;
    public static final int TYPE_CONTEXT_MENU = 0;
    public static final int TYPE_COPY = 0;
    public static final int TYPE_CROSSHAIR = 0;
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_GRAB = 0;
    public static final int TYPE_GRABBING = 0;
    public static final int TYPE_HAND = 0;
    public static final int TYPE_HELP = 0;
    public static final int TYPE_HORIZONTAL_DOUBLE_ARROW = 0;
    public static final int TYPE_NO_DROP = 0;
    public static final int TYPE_NULL = 0;
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW = 0;
    public static final int TYPE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW = 0;
    public static final int TYPE_VERTICAL_DOUBLE_ARROW = 0;
    public static final int TYPE_VERTICAL_TEXT = 0;
    public static final int TYPE_WAIT = 0;
    public static final int TYPE_ZOOM_IN = 0;
    public static final int TYPE_ZOOM_OUT = 0;

    public PointerIcon() {}

    public static PointerIcon create(Bitmap p0, float p1, float p2) { return null; }
    public int describeContents() { return 0; }
    public static PointerIcon getSystemIcon(Context p0, int p1) { return null; }
    public static PointerIcon load(Resources p0, int p1) { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
