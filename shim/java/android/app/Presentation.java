package android.app;
import android.content.Context;
import android.content.res.Resources;
import android.view.Display;

public class Presentation extends Dialog {
    public Presentation(Context p0, Display p1) {}
    public Presentation(Context p0, Display p1, int p2) {}

    public Display getDisplay() { return null; }
    public Resources getResources() { return null; }
    public void onDisplayChanged() {}
    public void onDisplayRemoved() {}
}
