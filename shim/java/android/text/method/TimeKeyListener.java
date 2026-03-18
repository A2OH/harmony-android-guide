package android.text.method;
import java.util.Locale;

public class TimeKeyListener extends NumberKeyListener {
    public TimeKeyListener() {}
    public TimeKeyListener(Locale p0) {}
    public static TimeKeyListener getInstance() { return new TimeKeyListener(); }
    public static TimeKeyListener getInstance(Locale locale) { return new TimeKeyListener(locale); }

    public int getInputType() { return 0; }
    public void clearMetaKeyState(android.view.View view, android.text.Editable content, int states) {}
}