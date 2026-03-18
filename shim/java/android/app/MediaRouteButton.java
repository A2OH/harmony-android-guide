package android.app;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class MediaRouteButton extends View {
    private int mRouteTypes;

    public MediaRouteButton(Context context) { super(context); }
    public MediaRouteButton(Context context, AttributeSet attrs) { super(context, attrs); }
    public MediaRouteButton(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public void setRouteTypes(int types) { mRouteTypes = types; }
    public int getRouteTypes() { return mRouteTypes; }
    public void showDialog() {}
}
