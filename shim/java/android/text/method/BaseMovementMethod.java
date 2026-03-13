package android.text.method;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

public class BaseMovementMethod implements MovementMethod {
    public BaseMovementMethod() {}

    public boolean bottom(TextView p0, Spannable p1) { return false; }
    public boolean canSelectArbitrarily() { return false; }
    public boolean down(TextView p0, Spannable p1) { return false; }
    public boolean end(TextView p0, Spannable p1) { return false; }
    public int getMovementMetaState(Spannable p0, KeyEvent p1) { return 0; }
    public boolean handleMovementKey(TextView p0, Spannable p1, int p2, int p3, KeyEvent p4) { return false; }
    public boolean home(TextView p0, Spannable p1) { return false; }
    public void initialize(TextView p0, Spannable p1) {}
    public boolean left(TextView p0, Spannable p1) { return false; }
    public boolean lineEnd(TextView p0, Spannable p1) { return false; }
    public boolean lineStart(TextView p0, Spannable p1) { return false; }
    public boolean onGenericMotionEvent(TextView p0, Spannable p1, MotionEvent p2) { return false; }
    public boolean onKeyDown(TextView p0, Spannable p1, int p2, KeyEvent p3) { return false; }
    public boolean onKeyOther(TextView p0, Spannable p1, KeyEvent p2) { return false; }
    public boolean onKeyUp(TextView p0, Spannable p1, int p2, KeyEvent p3) { return false; }
    public void onTakeFocus(TextView p0, Spannable p1, int p2) {}
    public boolean onTouchEvent(TextView p0, Spannable p1, MotionEvent p2) { return false; }
    public boolean onTrackballEvent(TextView p0, Spannable p1, MotionEvent p2) { return false; }
    public boolean pageDown(TextView p0, Spannable p1) { return false; }
    public boolean pageUp(TextView p0, Spannable p1) { return false; }
    public boolean right(TextView p0, Spannable p1) { return false; }
    public boolean top(TextView p0, Spannable p1) { return false; }
    public boolean up(TextView p0, Spannable p1) { return false; }
}
