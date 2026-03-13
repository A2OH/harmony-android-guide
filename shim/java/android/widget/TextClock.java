package android.widget;

/**
 * Shim: android.widget.TextClock
 *
 * A TextView subclass that displays the current time (and optionally date)
 * as a formatted string, updating automatically every second (or minute).
 *
 * On Android the widget registers a BroadcastReceiver for
 * {@code ACTION_TIME_TICK} / {@code ACTION_TIME_CHANGED} and redraws itself.
 * In the shim we keep the format strings but the automatic-update mechanism
 * is not implemented; the displayed text is static.
 *
 * OH mapping: ARKUI_NODE_TEXT — timer-driven updates not yet wired.
 */
public class TextClock extends TextView {

    // ── Default format strings (same as Android framework defaults) ───────────

    /** Default 12-hour format: "h:mm aa"  */
    public static final CharSequence DEFAULT_FORMAT_12_HOUR = "h:mm aa";
    /** Default 24-hour format: "H:mm"     */
    public static final CharSequence DEFAULT_FORMAT_24_HOUR = "H:mm";

    private CharSequence mFormat12;
    private CharSequence mFormat24;
    private CharSequence mContentDescription;
    private boolean      mShowCurrentUserTime = false;
    private int          mTimeZoneId = -1;   // -1 = system default
    private String       mTimeZone;

    public TextClock() {
        super();
        mFormat12 = DEFAULT_FORMAT_12_HOUR;
        mFormat24 = DEFAULT_FORMAT_24_HOUR;
        refreshTime();
    }

    public TextClock(Object context) {
        this();
    }

    public TextClock(Object context, Object attrs) {
        this();
    }

    public TextClock(Object context, Object attrs, int defStyleAttr) {
        this();
    }

    // ── Format strings ────────────────────────────────────────────────────────

    /**
     * Returns the format string used when the system clock is in 12-hour mode.
     */
    public CharSequence getFormat12Hour() {
        return mFormat12;
    }

    /**
     * Sets the format string used when the system clock is in 12-hour mode.
     * The string follows {@link java.text.SimpleDateFormat} conventions.
     *
     * @param format  format pattern, or null to use the default.
     */
    public void setFormat12Hour(CharSequence format) {
        mFormat12 = format != null ? format : DEFAULT_FORMAT_12_HOUR;
        refreshTime();
    }

    /**
     * Returns the format string used when the system clock is in 24-hour mode.
     */
    public CharSequence getFormat24Hour() {
        return mFormat24;
    }

    /**
     * Sets the format string used when the system clock is in 24-hour mode.
     *
     * @param format  format pattern, or null to use the default.
     */
    public void setFormat24Hour(CharSequence format) {
        mFormat24 = format != null ? format : DEFAULT_FORMAT_24_HOUR;
        refreshTime();
    }

    // ── Time zone ─────────────────────────────────────────────────────────────

    /**
     * Returns the time-zone ID string currently in use, or null if the
     * system default time zone is being used.
     */
    public String getTimeZone() {
        return mTimeZone;
    }

    /**
     * Sets the time zone to use for displaying the clock.
     *
     * @param timeZone  IANA time-zone ID (e.g. {@code "America/Los_Angeles"}),
     *                  or null to use the system default.
     */
    public void setTimeZone(String timeZone) {
        this.mTimeZone = timeZone;
        refreshTime();
    }

    // ── 24-hour mode query ────────────────────────────────────────────────────

    /**
     * Returns whether the system is currently configured to use 24-hour time.
     * In the shim this always returns false (12-hour mode assumed).
     */
    public boolean is24HourModeEnabled() {
        return false;
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    /**
     * Re-formats and sets the displayed text using the current time.
     * In the shim we use {@link java.util.Date#toString()} as a placeholder.
     */
    private void refreshTime() {
        // Lightweight placeholder: just show the epoch time as a string.
        // A full implementation would apply mFormat12/mFormat24 via
        // java.text.SimpleDateFormat with the selected time zone.
        setText(new java.util.Date().toString());
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Called when the view is attached to a window.  On Android this is where
     * the tick receiver is registered.  No-op in shim.
     */
    public void onAttachedToWindow() {
        // no-op in shim — would start a repeating timer here
    }

    /**
     * Called when the view is detached from a window.  On Android this is
     * where the tick receiver is unregistered.  No-op in shim.
     */
    public void onDetachedFromWindow() {
        // no-op in shim — would stop the repeating timer here
    }
}
