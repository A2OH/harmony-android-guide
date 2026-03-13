package android.app;

/**
 * Shim: android.app.AppOpsManager
 * Tier 2 — App operations / permission check gate.
 *
 * OH mapping:
 *   AppOpsManager.checkOp()  → @ohos.abilityAccessCtrl checkAccessToken()
 *   AppOpsManager.noteOp()   → same; also triggers audit log
 *   AppOpsManager.startOp()  → checkAccessToken() + begin session
 *   AppOpsManager.finishOp() → end session (no-op in OH)
 *
 * In the shim layer all ops return MODE_ALLOWED so that app logic proceeds
 * during development.  The real bridge replaces this with OH permission queries.
 */
public class AppOpsManager {

    // ── Result codes ──

    /** The op is allowed. */
    public static final int MODE_ALLOWED  = 0;
    /** The op is silently ignored. */
    public static final int MODE_IGNORED  = 1;
    /** The op causes an exception. */
    public static final int MODE_ERRORED  = 2;
    /** The op should use the default behaviour for the app. */
    public static final int MODE_DEFAULT  = 3;
    /** The op is in the foreground state. */
    public static final int MODE_FOREGROUND = 4;

    // ── OPSTR constants (subset of the most commonly used ops) ──

    public static final String OPSTR_COARSE_LOCATION         = "android:coarse_location";
    public static final String OPSTR_FINE_LOCATION           = "android:fine_location";
    public static final String OPSTR_GPS                     = "android:gps";
    public static final String OPSTR_VIBRATE                 = "android:vibrate";
    public static final String OPSTR_READ_CONTACTS           = "android:read_contacts";
    public static final String OPSTR_WRITE_CONTACTS          = "android:write_contacts";
    public static final String OPSTR_READ_CALL_LOG           = "android:read_call_log";
    public static final String OPSTR_WRITE_CALL_LOG          = "android:write_call_log";
    public static final String OPSTR_READ_CALENDAR           = "android:read_calendar";
    public static final String OPSTR_WRITE_CALENDAR          = "android:write_calendar";
    public static final String OPSTR_WIFI_SCAN               = "android:wifi_scan";
    public static final String OPSTR_POST_NOTIFICATION       = "android:post_notification";
    public static final String OPSTR_NEIGHBORING_CELLS       = "android:neighboring_cells";
    public static final String OPSTR_CALL_PHONE              = "android:call_phone";
    public static final String OPSTR_READ_SMS                = "android:read_sms";
    public static final String OPSTR_WRITE_SMS               = "android:write_sms";
    public static final String OPSTR_RECEIVE_SMS             = "android:receive_sms";
    public static final String OPSTR_SEND_SMS                = "android:send_sms";
    public static final String OPSTR_CAMERA                  = "android:camera";
    public static final String OPSTR_RECORD_AUDIO            = "android:record_audio";
    public static final String OPSTR_PLAY_AUDIO              = "android:play_audio";
    public static final String OPSTR_READ_CLIPBOARD          = "android:read_clipboard";
    public static final String OPSTR_WRITE_CLIPBOARD         = "android:write_clipboard";
    public static final String OPSTR_TAKE_MEDIA_BUTTONS      = "android:take_media_buttons";
    public static final String OPSTR_TAKE_AUDIO_FOCUS        = "android:take_audio_focus";
    public static final String OPSTR_AUDIO_MASTER_VOLUME     = "android:audio_master_volume";
    public static final String OPSTR_AUDIO_VOICE_VOLUME      = "android:audio_voice_volume";
    public static final String OPSTR_AUDIO_RING_VOLUME       = "android:audio_ring_volume";
    public static final String OPSTR_AUDIO_MEDIA_VOLUME      = "android:audio_media_volume";
    public static final String OPSTR_AUDIO_ALARM_VOLUME      = "android:audio_alarm_volume";
    public static final String OPSTR_AUDIO_NOTIFICATION_VOLUME = "android:audio_notification_volume";
    public static final String OPSTR_AUDIO_BLUETOOTH_VOLUME  = "android:audio_bluetooth_volume";
    public static final String OPSTR_WAKE_LOCK               = "android:wake_lock";
    public static final String OPSTR_MONITOR_LOCATION        = "android:monitor_location";
    public static final String OPSTR_MONITOR_HIGH_POWER_LOCATION = "android:monitor_location_high_power";
    public static final String OPSTR_GET_USAGE_STATS         = "android:get_usage_stats";
    public static final String OPSTR_MUTE_MICROPHONE         = "android:mute_microphone";
    public static final String OPSTR_TOAST_WINDOW            = "android:toast_window";
    public static final String OPSTR_PROJECT_MEDIA           = "android:project_media";
    public static final String OPSTR_ACTIVATE_VPN            = "android:activate_vpn";
    public static final String OPSTR_WRITE_WALLPAPER         = "android:write_wallpaper";
    public static final String OPSTR_ASSIST_STRUCTURE        = "android:assist_structure";
    public static final String OPSTR_ASSIST_SCREENSHOT       = "android:assist_screenshot";
    public static final String OPSTR_READ_PHONE_STATE        = "android:read_phone_state";
    public static final String OPSTR_ADD_VOICEMAIL           = "android:add_voicemail";
    public static final String OPSTR_USE_SIP                 = "android:use_sip";
    public static final String OPSTR_PROCESS_OUTGOING_CALLS  = "android:process_outgoing_calls";
    public static final String OPSTR_USE_FINGERPRINT         = "android:use_fingerprint";
    public static final String OPSTR_BODY_SENSORS            = "android:body_sensors";
    public static final String OPSTR_READ_CELL_BROADCASTS    = "android:read_cell_broadcasts";
    public static final String OPSTR_MOCK_LOCATION            = "android:mock_location";
    public static final String OPSTR_READ_EXTERNAL_STORAGE   = "android:read_external_storage";
    public static final String OPSTR_WRITE_EXTERNAL_STORAGE  = "android:write_external_storage";
    public static final String OPSTR_TURN_SCREEN_ON          = "android:turn_screen_on";
    public static final String OPSTR_GET_ACCOUNTS            = "android:get_accounts";
    public static final String OPSTR_RUN_IN_BACKGROUND       = "android:run_in_background";
    public static final String OPSTR_AUDIO_ACCESSIBILITY_VOLUME = "android:audio_accessibility_volume";
    public static final String OPSTR_READ_PHONE_NUMBERS      = "android:read_phone_numbers";
    public static final String OPSTR_REQUEST_INSTALL_PACKAGES = "android:request_install_packages";
    public static final String OPSTR_PICTURE_IN_PICTURE       = "android:picture_in_picture";
    public static final String OPSTR_INSTANT_APP_START_FOREGROUND = "android:instant_app_start_foreground";
    public static final String OPSTR_ANSWER_PHONE_CALLS       = "android:answer_phone_calls";
    public static final String OPSTR_RUN_ANY_IN_BACKGROUND    = "android:run_any_in_background";
    public static final String OPSTR_CHANGE_WIFI_STATE        = "android:change_wifi_state";
    public static final String OPSTR_REQUEST_DELETE_PACKAGES  = "android:request_delete_packages";
    public static final String OPSTR_BIND_ACCESSIBILITY_SERVICE = "android:bind_accessibility_service";
    public static final String OPSTR_ACCEPT_HANDOVER          = "android:accept_handover";
    public static final String OPSTR_MANAGE_IPSEC_TUNNELS     = "android:manage_ipsec_tunnels";
    public static final String OPSTR_START_FOREGROUND         = "android:start_foreground";
    public static final String OPSTR_BLUETOOTH_SCAN           = "android:bluetooth_scan";
    public static final String OPSTR_USE_BIOMETRIC            = "android:use_biometric";

    // ── API ──

    /**
     * Do a quick check for whether an application might be able to perform an operation.
     * This is NOT a security check; you must use a standard permission check if security
     * is needed.
     *
     * @param op        The operation to check. One of the OP_* constants.
     * @param uid       The user id of the application attempting to perform the operation.
     * @param packageName The name of the application attempting to perform the operation.
     * @return {@link #MODE_ALLOWED} if the operation is allowed, or
     *         {@link #MODE_IGNORED} if it is not allowed and should be silently ignored
     *         (without causing the app to crash).
     */
    public int checkOp(String op, int uid, String packageName) {
        return MODE_ALLOWED; // stub — always permit
    }

    /**
     * Make note of an application performing an operation. This is not a security check;
     * see {@link #checkOp} for that.
     *
     * @return {@link #MODE_ALLOWED} or {@link #MODE_IGNORED}.
     */
    public int noteOp(String op, int uid, String packageName) {
        return MODE_ALLOWED; // stub
    }

    /**
     * Report that an application has started executing a long-running operation.
     *
     * @return {@link #MODE_ALLOWED} or {@link #MODE_IGNORED}.
     */
    public int startOp(String op, int uid, String packageName) {
        return MODE_ALLOWED; // stub
    }

    /**
     * Report that an application is no longer performing an operation that had previously
     * been started with {@link #startOp}.  There is no validation of input or correlation
     * with previous starts; you are simply telling the system that there is one fewer
     * operation running.
     */
    public void finishOp(String op, int uid, String packageName) {
        // stub — no-op
    }

    /**
     * Convenience variant taking no uid/packageName — uses current process values.
     */
    public int checkOp(String op) {
        return MODE_ALLOWED;
    }

    public int noteOp(String op) {
        return MODE_ALLOWED;
    }
}
