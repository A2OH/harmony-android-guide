package android.view;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.Insets;
import android.graphics.Rect;

public final class DisplayCutout {
    public DisplayCutout(Insets p0, Rect p1, Rect p2, Rect p3, Rect p4) {}
    public DisplayCutout(Insets p0, Rect p1, Rect p2, Rect p3, Rect p4, Insets p5) {}

    public int getSafeInsetBottom() { return 0; }
    public int getSafeInsetLeft() { return 0; }
    public int getSafeInsetRight() { return 0; }
    public int getSafeInsetTop() { return 0; }
}
