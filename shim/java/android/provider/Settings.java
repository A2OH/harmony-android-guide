package android.provider;

import java.util.HashMap;

public class Settings {

    public static final String ACTION_SETTINGS                     = "android.settings.SETTINGS";
    public static final String ACTION_WIRELESS_SETTINGS            = "android.settings.WIRELESS_SETTINGS";
    public static final String ACTION_AIRPLANE_MODE_SETTINGS       = "android.settings.AIRPLANE_MODE_SETTINGS";
    public static final String ACTION_WIFI_SETTINGS                = "android.settings.WIFI_SETTINGS";
    public static final String ACTION_BLUETOOTH_SETTINGS           = "android.settings.BLUETOOTH_SETTINGS";
    public static final String ACTION_DATE_SETTINGS                = "android.settings.DATE_SETTINGS";
    public static final String ACTION_SOUND_SETTINGS               = "android.settings.SOUND_SETTINGS";
    public static final String ACTION_DISPLAY_SETTINGS             = "android.settings.DISPLAY_SETTINGS";
    public static final String ACTION_SECURITY_SETTINGS            = "android.settings.SECURITY_SETTINGS";
    public static final String ACTION_LOCATION_SOURCE_SETTINGS     = "android.settings.LOCATION_SOURCE_SETTINGS";
    public static final String ACTION_APPLICATION_SETTINGS         = "android.settings.APPLICATION_SETTINGS";
    public static final String ACTION_APPLICATION_DETAILS_SETTINGS = "android.settings.APPLICATION_DETAILS_SETTINGS";
    public static final String ACTION_MANAGE_APPLICATIONS_SETTINGS = "android.settings.MANAGE_APPLICATIONS_SETTINGS";
    public static final String ACTION_ACCESSIBILITY_SETTINGS       = "android.settings.ACCESSIBILITY_SETTINGS";
    public static final String ACTION_BATTERY_SAVER_SETTINGS       = "android.settings.BATTERY_SAVER_SETTINGS";
    public static final String ACTION_LOCALE_SETTINGS              = "android.settings.LOCALE_SETTINGS";
    public static final String ACTION_PRIVACY_SETTINGS             = "android.settings.PRIVACY_SETTINGS";
    public static final String ACTION_NFCSHARING_SETTINGS          = "android.settings.NFCSHARING_SETTINGS";
    public static final String ACTION_MANAGE_OVERLAY_PERMISSION    = "android.settings.action.MANAGE_OVERLAY_PERMISSION";
    public static final String ACTION_MANAGE_WRITE_SETTINGS        = "android.settings.action.MANAGE_WRITE_SETTINGS";
    public static final String ACTION_NFC_SETTINGS                 = "android.settings.NFC_SETTINGS";
    public static final String ACTION_USER_DICTIONARY_INSERT        = "com.android.settings.USER_DICTIONARY_INSERT";

    public Settings() {}

    public static boolean canDrawOverlays(Object context) { return false; }

    // ── System ──────────────────────────────────────────────────────────────

    public static class System {
        public static final android.net.Uri CONTENT_URI = android.net.Uri.parse("content://settings/system");
        private static final HashMap<String, String> sStore = new HashMap<String, String>();

        public static android.net.Uri getUriFor(String name) {
            return android.net.Uri.parse("content://settings/system/" + name);
        }

        public static final String SCREEN_BRIGHTNESS        = "screen_brightness";
        public static final String SCREEN_BRIGHTNESS_MODE   = "screen_brightness_mode";
        public static final String SCREEN_OFF_TIMEOUT       = "screen_off_timeout";
        public static final String RINGTONE                 = "ringtone";
        public static final String DEFAULT_RINGTONE_URI     = "ringtone";
        public static final String NOTIFICATION_SOUND       = "notification_sound";
        public static final String ALARM_ALERT              = "alarm_alert";
        public static final String VOLUME_RING              = "volume_ring";
        public static final String VOLUME_NOTIFICATION      = "volume_notification";
        public static final String VOLUME_SYSTEM            = "volume_system";
        public static final String VOLUME_MUSIC             = "volume_music";
        public static final String VOLUME_ALARM             = "volume_alarm";
        public static final String VIBRATE_ON               = "vibrate_on";
        public static final String VIBRATE_WHEN_RINGING     = "vibrate_when_ringing";
        public static final String TIME_12_24               = "time_12_24";
        public static final String DATE_FORMAT              = "date_format";
        public static final String ACCELEROMETER_ROTATION   = "accelerometer_rotation";
        public static final String FONT_SCALE               = "font_scale";
        public static final String DTMF_TONE_WHEN_DIALING   = "dtmf_tone";
        public static final String HAPTIC_FEEDBACK_ENABLED  = "haptic_feedback_enabled";
        public static final String SOUND_EFFECTS_ENABLED    = "sound_effects_enabled";
        public static final String SHOW_BATTERY_PERCENTAGE  = "show_battery_percent";
        public static final String POINTER_SPEED            = "pointer_speed";
        public static final String WALLPAPER_ACTIVITY       = "wallpaper_activity";

        public System() {}

        public static String getString(Object cr, String name) { return sStore.get(name); }
        public static boolean putString(Object cr, String name, String value) { sStore.put(name, value); return true; }

        public static int getInt(Object cr, String name) throws SettingNotFoundException {
            String v = sStore.get(name);
            if (v == null) throw new SettingNotFoundException(name);
            try { return Integer.parseInt(v); } catch (NumberFormatException e) { throw new SettingNotFoundException(name); }
        }
        public static int getInt(Object cr, String name, int def) {
            String v = sStore.get(name);
            if (v == null) return def;
            try { return Integer.parseInt(v); } catch (NumberFormatException e) { return def; }
        }
        public static boolean putInt(Object cr, String name, int value) { sStore.put(name, String.valueOf(value)); return true; }

        public static float getFloat(Object cr, String name) throws SettingNotFoundException {
            String v = sStore.get(name);
            if (v == null) throw new SettingNotFoundException(name);
            try { return Float.parseFloat(v); } catch (NumberFormatException e) { throw new SettingNotFoundException(name); }
        }
        public static float getFloat(Object cr, String name, float def) {
            String v = sStore.get(name);
            if (v == null) return def;
            try { return Float.parseFloat(v); } catch (NumberFormatException e) { return def; }
        }
        public static boolean putFloat(Object cr, String name, float value) { sStore.put(name, String.valueOf(value)); return true; }

        public static long getLong(Object cr, String name) throws SettingNotFoundException {
            String v = sStore.get(name);
            if (v == null) throw new SettingNotFoundException(name);
            try { return Long.parseLong(v); } catch (NumberFormatException e) { throw new SettingNotFoundException(name); }
        }
        public static long getLong(Object cr, String name, long def) {
            String v = sStore.get(name);
            if (v == null) return def;
            try { return Long.parseLong(v); } catch (NumberFormatException e) { return def; }
        }
        public static boolean putLong(Object cr, String name, long value) { sStore.put(name, String.valueOf(value)); return true; }
    }

    // ── Secure ──────────────────────────────────────────────────────────────

    public static class Secure {
        private static final HashMap<String, String> sStore = new HashMap<String, String>();

        public static final String ANDROID_ID                = "android_id";
        public static final String LOCATION_MODE             = "location_mode";
        public static final String LOCATION_PROVIDERS_ALLOWED = "location_providers_allowed";
        public static final String BLUETOOTH_NAME            = "bluetooth_name";
        public static final String BLUETOOTH_ADDRESS         = "bluetooth_address";
        public static final String WIFI_ON                   = "wifi_on";
        public static final String DEFAULT_INPUT_METHOD      = "default_input_method";
        public static final String ENABLED_INPUT_METHODS     = "enabled_input_methods";
        public static final String INSTALL_NON_MARKET_APPS   = "install_non_market_apps";
        public static final String ENABLED_ACCESSIBILITY_SERVICES = "enabled_accessibility_services";
        public static final String SCREENSAVER_ENABLED       = "screensaver_enabled";
        public static final String SCREENSAVER_ACTIVATE_ON_DOCK = "screensaver_activate_on_dock";
        public static final String SCREENSAVER_ACTIVATE_ON_SLEEP = "screensaver_activate_on_sleep";
        public static final String SCREENSAVER_COMPONENTS    = "screensaver_components";
        public static final String LOCK_SCREEN_SHOW_NOTIFICATIONS = "lock_screen_show_notifications";
        public static final String USER_SETUP_COMPLETE       = "user_setup_complete";
        public static final String ACCESSIBILITY_ENABLED     = "accessibility_enabled";
        public static final String SPEAK_PASSWORD            = "speak_password";
        public static final String TTS_DEFAULT_RATE          = "tts_default_rate";
        public static final String TTS_DEFAULT_PITCH         = "tts_default_pitch";
        public static final String TTS_DEFAULT_SYNTH         = "tts_default_synth";
        public static final String BACKUP_ENABLED            = "backup_enabled";
        public static final String BACKUP_AUTO_RESTORE       = "backup_auto_restore";

        public static final int LOCATION_MODE_OFF              = 0;
        public static final int LOCATION_MODE_SENSORS_ONLY     = 1;
        public static final int LOCATION_MODE_BATTERY_SAVING   = 2;
        public static final int LOCATION_MODE_HIGH_ACCURACY    = 3;

        public Secure() {}

        public static String getString(Object cr, String name) { return sStore.get(name); }
        public static boolean putString(Object cr, String name, String value) { sStore.put(name, value); return true; }

        public static int getInt(Object cr, String name) throws SettingNotFoundException {
            String v = sStore.get(name);
            if (v == null) throw new SettingNotFoundException(name);
            try { return Integer.parseInt(v); } catch (NumberFormatException e) { throw new SettingNotFoundException(name); }
        }
        public static int getInt(Object cr, String name, int def) {
            String v = sStore.get(name);
            if (v == null) return def;
            try { return Integer.parseInt(v); } catch (NumberFormatException e) { return def; }
        }
        public static boolean putInt(Object cr, String name, int value) { sStore.put(name, String.valueOf(value)); return true; }

        public static float getFloat(Object cr, String name) throws SettingNotFoundException {
            String v = sStore.get(name);
            if (v == null) throw new SettingNotFoundException(name);
            try { return Float.parseFloat(v); } catch (NumberFormatException e) { throw new SettingNotFoundException(name); }
        }
        public static float getFloat(Object cr, String name, float def) {
            String v = sStore.get(name);
            if (v == null) return def;
            try { return Float.parseFloat(v); } catch (NumberFormatException e) { return def; }
        }
        public static boolean putFloat(Object cr, String name, float value) { sStore.put(name, String.valueOf(value)); return true; }

        public static long getLong(Object cr, String name) throws SettingNotFoundException {
            String v = sStore.get(name);
            if (v == null) throw new SettingNotFoundException(name);
            try { return Long.parseLong(v); } catch (NumberFormatException e) { throw new SettingNotFoundException(name); }
        }
        public static long getLong(Object cr, String name, long def) {
            String v = sStore.get(name);
            if (v == null) return def;
            try { return Long.parseLong(v); } catch (NumberFormatException e) { return def; }
        }
        public static boolean putLong(Object cr, String name, long value) { sStore.put(name, String.valueOf(value)); return true; }
    }

    // ── Global ──────────────────────────────────────────────────────────────

    public static class Global {
        private static final HashMap<String, String> sStore = new HashMap<String, String>();

        public static final String AIRPLANE_MODE_ON          = "airplane_mode_on";
        public static final String AIRPLANE_MODE_RADIOS      = "airplane_mode_radios";
        public static final String DEVICE_NAME               = "device_name";
        public static final String WIFI_ON                   = "wifi_on";
        public static final String WIFI_SLEEP_POLICY         = "wifi_sleep_policy";
        public static final String BLUETOOTH_ON              = "bluetooth_on";
        public static final String DATA_ROAMING              = "data_roaming";
        public static final String AUTO_TIME                 = "auto_time";
        public static final String AUTO_TIME_ZONE            = "auto_time_zone";
        public static final String USB_MASS_STORAGE_ENABLED  = "usb_mass_storage_enabled";
        public static final String DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled";
        public static final String ADB_ENABLED               = "adb_enabled";
        public static final String NETWORK_PREFERENCE        = "network_preference";
        public static final String ALWAYS_FINISH_ACTIVITIES  = "always_finish_activities";
        public static final String ANIMATOR_DURATION_SCALE   = "animator_duration_scale";
        public static final String TRANSITION_ANIMATION_SCALE = "transition_animation_scale";
        public static final String WINDOW_ANIMATION_SCALE    = "window_animation_scale";
        public static final String STAY_ON_WHILE_PLUGGED_IN  = "stay_on_while_plugged_in";
        public static final String PACKAGE_VERIFIER_ENABLE   = "package_verifier_enable";
        public static final String BOOT_COUNT                = "boot_count";
        public static final String INSTALL_NON_MARKET_APPS   = "install_non_market_apps";
        public static final String DEVICE_PROVISIONED       = "device_provisioned";

        public Global() {}

        public static String getString(Object cr, String name) { return sStore.get(name); }
        public static boolean putString(Object cr, String name, String value) { sStore.put(name, value); return true; }

        public static int getInt(Object cr, String name) throws SettingNotFoundException {
            String v = sStore.get(name);
            if (v == null) throw new SettingNotFoundException(name);
            try { return Integer.parseInt(v); } catch (NumberFormatException e) { throw new SettingNotFoundException(name); }
        }
        public static int getInt(Object cr, String name, int def) {
            String v = sStore.get(name);
            if (v == null) return def;
            try { return Integer.parseInt(v); } catch (NumberFormatException e) { return def; }
        }
        public static boolean putInt(Object cr, String name, int value) { sStore.put(name, String.valueOf(value)); return true; }

        public static float getFloat(Object cr, String name) throws SettingNotFoundException {
            String v = sStore.get(name);
            if (v == null) throw new SettingNotFoundException(name);
            try { return Float.parseFloat(v); } catch (NumberFormatException e) { throw new SettingNotFoundException(name); }
        }
        public static float getFloat(Object cr, String name, float def) {
            String v = sStore.get(name);
            if (v == null) return def;
            try { return Float.parseFloat(v); } catch (NumberFormatException e) { return def; }
        }
        public static boolean putFloat(Object cr, String name, float value) { sStore.put(name, String.valueOf(value)); return true; }

        public static long getLong(Object cr, String name) throws SettingNotFoundException {
            String v = sStore.get(name);
            if (v == null) throw new SettingNotFoundException(name);
            try { return Long.parseLong(v); } catch (NumberFormatException e) { throw new SettingNotFoundException(name); }
        }
        public static long getLong(Object cr, String name, long def) {
            String v = sStore.get(name);
            if (v == null) return def;
            try { return Long.parseLong(v); } catch (NumberFormatException e) { return def; }
        }
        public static boolean putLong(Object cr, String name, long value) { sStore.put(name, String.valueOf(value)); return true; }
    }

    // ── SettingNotFoundException ─────────────────────────────────────────────

    public static class SettingNotFoundException extends Exception {
        public SettingNotFoundException(String name) {
            super("Setting not found: " + name);
        }
    }
}
