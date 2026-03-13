package android.text.method;

import android.text.Spannable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Android-compatible ScrollingMovementMethod shim.
 * A movement method that interprets arrow keys by scrolling the text buffer.
 */
public class ScrollingMovementMethod implements MovementMethod {
    private static ScrollingMovementMethod sInstance;

    public static ScrollingMovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new ScrollingMovementMethod();
        }
        return sInstance;
    }

    @Override
    public void initialize(TextView widget, Spannable text) {}

    @Override
    public boolean onKeyDown(TextView widget, Spannable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(TextView widget, Spannable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable text, MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTrackballEvent(TextView widget, Spannable text, MotionEvent event) {
        return false;
    }

    @Override
    public boolean canSelectArbitrarily() {
        return false;
    }

    @Override
    public void onTakeFocus(TextView widget, Spannable text, int direction) {}

    @Override
    public boolean onGenericMotionEvent(TextView widget, Spannable text, MotionEvent event) {
        return false;
    }
}
