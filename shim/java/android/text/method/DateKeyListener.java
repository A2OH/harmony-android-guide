package android.text.method;
import java.util.Locale;

public class DateKeyListener extends NumberKeyListener {
    public DateKeyListener() {}
    public DateKeyListener(Locale p0) {}
    public static DateKeyListener getInstance() { return new DateKeyListener(); }
    public static DateKeyListener getInstance(Locale locale) { return new DateKeyListener(locale); }

    public int getInputType() { return 0; }
    public void clearMetaKeyState(android.view.View view, android.text.Editable content, int states) {}
}