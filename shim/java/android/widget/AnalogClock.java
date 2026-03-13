package android.widget;
import android.icu.util.TimeZone;
import android.view.View;
import android.icu.util.TimeZone;
import android.view.View;

import android.view.View;

/**
 * Shim: android.widget.AnalogClock
 *
 * Displays an analog clock face. Extends View; actual rendering is not
 * implemented — this stub allows compilation of code that references the class.
 *
 * On real Android the clock updates via a time-tick broadcast; here all
 * time-related methods are no-ops.
 */
public class AnalogClock extends View {

    // ArkUI node type: STACK (generic container — no native analog-clock node)
    static final int NODE_TYPE_STACK = 8;

    private String timeZone = null;

    public AnalogClock() {
        super(NODE_TYPE_STACK);
    }

    /**
     * Sets the time zone to use.  Pass a {@link java.util.TimeZone} ID string
     * such as {@code "America/New_York"}.  Passing {@code null} resets to the
     * default system time zone.
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Returns the currently configured time zone ID, or {@code null} if the
     * system default is being used.
     */
    public String getTimeZone() {
        return timeZone;
    }
}
