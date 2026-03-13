package android.widget;
import android.content.Context;

public class Toast {
    public static final int LENGTH_LONG = 0;
    public static final int LENGTH_SHORT = 0;

    public Toast(Context p0) {}

    public void addCallback(Object p0) {}
    public void cancel() {}
    public int getDuration() { return 0; }
    public int getGravity() { return 0; }
    public float getHorizontalMargin() { return 0f; }
    public float getVerticalMargin() { return 0f; }
    public int getXOffset() { return 0; }
    public int getYOffset() { return 0; }
    public static Toast makeText(Context p0, CharSequence p1, int p2) { return null; }
    public void removeCallback(Object p0) {}
    public void setDuration(int p0) {}
    public void setGravity(int p0, int p1, int p2) {}
    public void setMargin(float p0, float p1) {}
    public void setText(int p0) {}
    public void show() {}
    public void onToastHidden() {}
    public void onToastShown() {}
}
