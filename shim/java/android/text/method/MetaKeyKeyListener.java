package android.text.method;
import android.text.Editable;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.View;

public class MetaKeyKeyListener {
    public static final int META_ALT_LOCKED = 0;
    public static final int META_ALT_ON = 0;
    public static final int META_CAP_LOCKED = 0;
    public static final int META_SHIFT_ON = 0;
    public static final int META_SYM_LOCKED = 0;
    public static final int META_SYM_ON = 0;

    public MetaKeyKeyListener() {}

    public static void adjustMetaAfterKeypress(Spannable p0) {}
    public void clearMetaKeyState(View p0, Editable p1, int p2) {}
    public static void clearMetaKeyState(Editable p0, int p1) {}
    public static int getMetaState(CharSequence p0) { return 0; }
    public static int getMetaState(CharSequence p0, KeyEvent p1) { return 0; }
    public static int getMetaState(CharSequence p0, int p1, KeyEvent p2) { return 0; }
    public static long handleKeyDown(long p0, int p1, KeyEvent p2) { return 0L; }
    public static long handleKeyUp(long p0, int p1, KeyEvent p2) { return 0L; }
    public static boolean isMetaTracker(CharSequence p0, Object p1) { return false; }
    public static boolean isSelectingMetaTracker(CharSequence p0, Object p1) { return false; }
    public boolean onKeyDown(View p0, Editable p1, int p2, KeyEvent p3) { return false; }
    public boolean onKeyUp(View p0, Editable p1, int p2, KeyEvent p3) { return false; }
    public static void resetLockedMeta(Spannable p0) {}
    public static void resetMetaState(Spannable p0) {}
    public static final int META_SELECTING = 0x800;
    public static void startSelecting(android.view.View view, Spannable content) {}
    public static void stopSelecting(android.view.View view, Spannable content) {}
}
