package android.text.method;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

import android.text.Spannable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Android-compatible MovementMethod interface shim.
 * Defines how cursor movement and text selection behave for a TextView.
 */
public interface MovementMethod {
    void    initialize(TextView widget, Spannable text);
    boolean onKeyDown(TextView widget, Spannable text, int keyCode, KeyEvent event);
    boolean onKeyUp(TextView widget, Spannable text, int keyCode, KeyEvent event);
    boolean onTouchEvent(TextView widget, Spannable text, MotionEvent event);
    boolean onTrackballEvent(TextView widget, Spannable text, MotionEvent event);
    boolean canSelectArbitrarily();
    void    onTakeFocus(TextView widget, Spannable text, int direction);
    boolean onGenericMotionEvent(TextView widget, Spannable text, MotionEvent event);
}
