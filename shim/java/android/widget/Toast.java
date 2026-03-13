package android.widget;

import android.content.Context;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.Toast → @ohos.promptAction.showToast
 * Tier 1 — direct mapping.
 */
public class Toast {
    public static final int LENGTH_SHORT = 0;
    public static final int LENGTH_LONG = 1;

    private final Context context;
    private String text;
    private int duration;

    private Toast(Context context) {
        this.context = context;
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        Toast toast = new Toast(context);
        toast.text = text.toString();
        toast.duration = duration;
        return toast;
    }

    public static Toast makeText(Context context, int resId, int duration) {
        Toast toast = new Toast(context);
        toast.text = context.getString(resId);
        toast.duration = duration;
        return toast;
    }

    public void show() {
        OHBridge.showToast(text, duration == LENGTH_LONG ? 3000 : 1500);
    }

    public void setText(CharSequence s) { this.text = s.toString(); }
    public void setText(int resId) { this.text = context.getString(resId); }
    public void setDuration(int duration) { this.duration = duration; }
}
