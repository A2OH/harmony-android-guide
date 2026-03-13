package android.view;

/**
 * Shim: android.view.KeyEvent — keyboard / hardware-button input event.
 *
 * Pure Java stub. The shim populates instances from the ArkUI key-event
 * callback and delivers them to the focused view via dispatchKeyEvent().
 * No native bridge calls are needed: the data is entirely captured at the
 * moment the ArkUI callback fires.
 */
public final class KeyEvent {

    // ── Action constants ──

    public static final int ACTION_DOWN     = 0;
    public static final int ACTION_UP       = 1;
    public static final int ACTION_MULTIPLE = 2;

    // ── Keycode constants ──

    public static final int KEYCODE_UNKNOWN       = 0;
    public static final int KEYCODE_HOME          = 3;
    public static final int KEYCODE_BACK          = 4;
    public static final int KEYCODE_CALL          = 5;
    public static final int KEYCODE_ENDCALL       = 6;
    public static final int KEYCODE_0             = 7;
    public static final int KEYCODE_1             = 8;
    public static final int KEYCODE_2             = 9;
    public static final int KEYCODE_3             = 10;
    public static final int KEYCODE_4             = 11;
    public static final int KEYCODE_5             = 12;
    public static final int KEYCODE_6             = 13;
    public static final int KEYCODE_7             = 14;
    public static final int KEYCODE_8             = 15;
    public static final int KEYCODE_9             = 16;
    public static final int KEYCODE_STAR          = 17;
    public static final int KEYCODE_POUND         = 18;
    public static final int KEYCODE_DPAD_UP       = 19;
    public static final int KEYCODE_DPAD_DOWN     = 20;
    public static final int KEYCODE_DPAD_LEFT     = 21;
    public static final int KEYCODE_DPAD_RIGHT    = 22;
    public static final int KEYCODE_DPAD_CENTER   = 23;
    public static final int KEYCODE_VOLUME_UP     = 24;
    public static final int KEYCODE_VOLUME_DOWN   = 25;
    public static final int KEYCODE_POWER         = 26;
    public static final int KEYCODE_CAMERA        = 27;
    public static final int KEYCODE_CLEAR         = 28;
    public static final int KEYCODE_A             = 29;
    public static final int KEYCODE_B             = 30;
    public static final int KEYCODE_C             = 31;
    public static final int KEYCODE_D             = 32;
    public static final int KEYCODE_E             = 33;
    public static final int KEYCODE_F             = 34;
    public static final int KEYCODE_G             = 35;
    public static final int KEYCODE_H             = 36;
    public static final int KEYCODE_I             = 37;
    public static final int KEYCODE_J             = 38;
    public static final int KEYCODE_K             = 39;
    public static final int KEYCODE_L             = 40;
    public static final int KEYCODE_M             = 41;
    public static final int KEYCODE_N             = 42;
    public static final int KEYCODE_O             = 43;
    public static final int KEYCODE_P             = 44;
    public static final int KEYCODE_Q             = 45;
    public static final int KEYCODE_R             = 46;
    public static final int KEYCODE_S             = 47;
    public static final int KEYCODE_T             = 48;
    public static final int KEYCODE_U             = 49;
    public static final int KEYCODE_V             = 50;
    public static final int KEYCODE_W             = 51;
    public static final int KEYCODE_X             = 52;
    public static final int KEYCODE_Y             = 53;
    public static final int KEYCODE_Z             = 54;
    public static final int KEYCODE_COMMA         = 55;
    public static final int KEYCODE_PERIOD        = 56;
    public static final int KEYCODE_ALT_LEFT      = 57;
    public static final int KEYCODE_ALT_RIGHT     = 58;
    public static final int KEYCODE_SHIFT_LEFT    = 59;
    public static final int KEYCODE_SHIFT_RIGHT   = 60;
    public static final int KEYCODE_TAB           = 61;
    public static final int KEYCODE_SPACE         = 62;
    public static final int KEYCODE_SYM           = 63;
    public static final int KEYCODE_EXPLORER      = 64;
    public static final int KEYCODE_ENVELOPE      = 65;
    public static final int KEYCODE_ENTER         = 66;
    public static final int KEYCODE_DEL           = 67;
    public static final int KEYCODE_GRAVE         = 68;
    public static final int KEYCODE_MINUS         = 69;
    public static final int KEYCODE_EQUALS        = 70;
    public static final int KEYCODE_LEFT_BRACKET  = 71;
    public static final int KEYCODE_RIGHT_BRACKET = 72;
    public static final int KEYCODE_BACKSLASH     = 73;
    public static final int KEYCODE_SEMICOLON     = 74;
    public static final int KEYCODE_APOSTROPHE    = 75;
    public static final int KEYCODE_SLASH         = 76;
    public static final int KEYCODE_AT            = 77;
    public static final int KEYCODE_MENU          = 82;
    public static final int KEYCODE_SEARCH        = 84;
    public static final int KEYCODE_ESCAPE        = 111;
    public static final int KEYCODE_FORWARD_DEL   = 112;
    public static final int KEYCODE_CTRL_LEFT     = 113;
    public static final int KEYCODE_CTRL_RIGHT    = 114;
    public static final int KEYCODE_CAPS_LOCK     = 115;
    public static final int KEYCODE_SCROLL_LOCK   = 116;
    public static final int KEYCODE_META_LEFT     = 117;
    public static final int KEYCODE_META_RIGHT    = 118;
    public static final int KEYCODE_FUNCTION      = 119;
    public static final int KEYCODE_SYSRQ         = 120;
    public static final int KEYCODE_BREAK         = 121;
    public static final int KEYCODE_MOVE_HOME     = 122;
    public static final int KEYCODE_MOVE_END      = 123;
    public static final int KEYCODE_INSERT        = 124;
    public static final int KEYCODE_FORWARD       = 125;
    public static final int KEYCODE_PAGE_UP       = 92;
    public static final int KEYCODE_PAGE_DOWN     = 93;

    // ── Modifier meta-state bit flags ──

    public static final int META_ALT_ON     = 0x02;
    public static final int META_ALT_LEFT_ON  = 0x10;
    public static final int META_ALT_RIGHT_ON = 0x20;
    public static final int META_SHIFT_ON   = 0x01;
    public static final int META_SHIFT_LEFT_ON  = 0x40;
    public static final int META_SHIFT_RIGHT_ON = 0x80;
    public static final int META_CTRL_ON    = 0x1000;
    public static final int META_CTRL_LEFT_ON  = 0x2000;
    public static final int META_CTRL_RIGHT_ON = 0x4000;
    public static final int META_META_ON    = 0x10000;
    public static final int META_CAPS_LOCK_ON = 0x100000;

    // ── Fields ──

    private final int  action;
    private final int  keyCode;
    private final int  repeatCount;
    private final int  metaState;
    private final long eventTime;
    private final long downTime;

    // ── Constructors ──

    public KeyEvent(int action, int keyCode) {
        this(action, keyCode, 0, 0, 0L, 0L);
    }

    public KeyEvent(int action, int keyCode, int repeatCount, int metaState,
                    long downTime, long eventTime) {
        this.action      = action;
        this.keyCode     = keyCode;
        this.repeatCount = repeatCount;
        this.metaState   = metaState;
        this.downTime    = downTime;
        this.eventTime   = eventTime;
    }

    // ── Accessors ──

    public int  getAction()      { return action; }
    public int  getKeyCode()     { return keyCode; }
    public int  getRepeatCount() { return repeatCount; }
    public int  getMetaState()   { return metaState; }
    public long getDownTime()    { return downTime; }
    public long getEventTime()   { return eventTime; }

    /** True if any Shift modifier key was held. */
    public boolean isShiftPressed() {
        return (metaState & META_SHIFT_ON) != 0
            || (metaState & META_SHIFT_LEFT_ON) != 0
            || (metaState & META_SHIFT_RIGHT_ON) != 0;
    }

    /** True if any Ctrl modifier key was held. */
    public boolean isCtrlPressed() {
        return (metaState & META_CTRL_ON) != 0
            || (metaState & META_CTRL_LEFT_ON) != 0
            || (metaState & META_CTRL_RIGHT_ON) != 0;
    }

    /** True if any Alt modifier key was held. */
    public boolean isAltPressed() {
        return (metaState & META_ALT_ON) != 0
            || (metaState & META_ALT_LEFT_ON) != 0
            || (metaState & META_ALT_RIGHT_ON) != 0;
    }

    /** True if Caps Lock was active at the time of this event. */
    public boolean isCapsLockOn() {
        return (metaState & META_CAPS_LOCK_ON) != 0;
    }

    /**
     * Returns true if this is a BACK key-up event.
     * Convenience for the common "intercept back press" pattern.
     */
    public boolean isBackKey() {
        return keyCode == KEYCODE_BACK && action == ACTION_UP;
    }

    @Override
    public String toString() {
        return "KeyEvent{action=" + action + " keyCode=" + keyCode
                + " repeat=" + repeatCount + " meta=0x" + Integer.toHexString(metaState) + "}";
    }
}
