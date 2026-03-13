package android.text.method;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.Spanned;
import android.view.KeyEvent;

public class NumberKeyListener extends BaseKeyListener implements InputFilter {
    public NumberKeyListener() {}

    public CharSequence filter(CharSequence p0, int p1, int p2, Spanned p3, int p4, int p5) { return null; }
    public int lookup(KeyEvent p0, Spannable p1) { return 0; }
    public static boolean ok(char[] p0, char p1) { return false; }
}
