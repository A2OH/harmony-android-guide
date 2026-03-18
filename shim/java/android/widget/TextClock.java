package android.widget;
import android.content.Context;
import android.util.AttributeSet;

public class TextClock extends TextView {
    public static final CharSequence DEFAULT_FORMAT_12_HOUR = "h:mm aa";
    public static final CharSequence DEFAULT_FORMAT_24_HOUR = "H:mm";

    private CharSequence mFormat12 = DEFAULT_FORMAT_12_HOUR;
    private CharSequence mFormat24 = DEFAULT_FORMAT_24_HOUR;
    private String mTimeZone;

    public TextClock(Context context) { super(context); }
    public TextClock(Context context, AttributeSet attrs) { super(context, attrs); }
    public TextClock(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public CharSequence getFormat12Hour() { return mFormat12; }
    public void setFormat12Hour(CharSequence format) { mFormat12 = format != null ? format : DEFAULT_FORMAT_12_HOUR; }
    public CharSequence getFormat24Hour() { return mFormat24; }
    public void setFormat24Hour(CharSequence format) { mFormat24 = format != null ? format : DEFAULT_FORMAT_24_HOUR; }
    public String getTimeZone() { return mTimeZone; }
    public void setTimeZone(String timeZone) { this.mTimeZone = timeZone; }
    public boolean is24HourModeEnabled() { return false; }
}
