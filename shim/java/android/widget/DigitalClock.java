package android.widget;

/**
 * Shim: android.widget.DigitalClock
 *
 * @deprecated Deprecated since API level 17. Use {@link TextClock} instead.
 *
 * Displays the current time as a text string in h:mm format.  Extends
 * TextView; the real implementation registers a time-tick broadcast receiver.
 * This stub allows compilation only — no live clock updates are performed.
 */
@Deprecated
public class DigitalClock extends TextView {

    public DigitalClock() {
        super();
    }
}
