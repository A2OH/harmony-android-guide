package android.text.method;
import java.util.Locale;

public class DateTimeKeyListener extends NumberKeyListener {
    public DateTimeKeyListener() {}
    public DateTimeKeyListener(Locale p0) {}
    public static DateTimeKeyListener getInstance() { return new DateTimeKeyListener(); }
    public static DateTimeKeyListener getInstance(Locale locale) { return new DateTimeKeyListener(locale); }

    public int getInputType() { return 0; }
    public void clearMetaKeyState(android.view.View view, android.text.Editable content, int states) {}
}