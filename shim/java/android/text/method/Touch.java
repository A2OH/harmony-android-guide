package android.text.method;
import android.text.Layout;
import android.text.Spannable;
import android.view.MotionEvent;
import android.widget.TextView;

public class Touch {
    public Touch() {}

    public static int getInitialScrollX(TextView p0, Spannable p1) { return 0; }
    public static int getInitialScrollY(TextView p0, Spannable p1) { return 0; }
    public static boolean onTouchEvent(TextView p0, Spannable p1, MotionEvent p2) { return false; }
    public static void scrollTo(TextView p0, Layout p1, int p2, int p3) {}
}
