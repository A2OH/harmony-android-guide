/*
 * OhConfigurationConverter.java
 *
 * Converts OH Configuration items + DisplayInfo snapshot into
 * android.content.res.Configuration.
 *
 * OH Configuration is a key/value map. The C++ AppSchedulerAdapter extracts
 * the 8 standard keys (see global_configuration_key.h) and passes them as a
 * String[] of (key, value, key, value, ...) pairs. This class consumes that
 * array plus an OhDisplayProvider.Snapshot for screen-related fields.
 *
 * Authoritative source: doc/ability_manager_ipc_adapter_design.html §1.1.4.4
 *
 * Phase 1 fields handled:
 *   mLocaleList    DERIVE  (BCP-47 → LocaleList — must be non-empty)
 *   fontScale      DIRECT  (ohos.system.fontSizeScale)
 *   densityDpi     QUERY   (OhDisplayProvider)
 *   uiMode (Night) DERIVE  (ohos.system.colorMode)
 *   orientation    QUERY+DERIVE
 *   screenLayout/widthDp/heightDp/smallestScreenWidthDp DERIVE from snapshot
 *
 * Phase 2 fields handled (when keys present):
 *   mcc/mnc        DIRECT  (ohos.system.mcc / mnc)
 *   uiMode (Type)  DERIVE  (const.build.characteristics)
 *   touchscreen/keyboard/navigation DERIVE  (input.pointer.device)
 */
package adapter.activity;

import android.content.res.Configuration;
import android.os.LocaleList;
import android.util.Log;

import java.util.Locale;

public final class OhConfigurationConverter {

    private static final String TAG = "OH_ConfigConv";

    // OH Configuration key constants (mirror global_configuration_key.h).
    public static final String KEY_LOCALE = "ohos.system.locale";
    public static final String KEY_LANGUAGE = "ohos.system.language";
    public static final String KEY_COLOR_MODE = "ohos.system.colorMode";
    public static final String KEY_HOUR = "ohos.system.hour";
    public static final String KEY_FONT_SCALE = "ohos.system.fontSizeScale";
    public static final String KEY_FONT_WEIGHT_SCALE = "ohos.system.fontWeightScale";
    public static final String KEY_MCC = "ohos.system.mcc";
    public static final String KEY_MNC = "ohos.system.mnc";
    public static final String KEY_DEVICE_TYPE = "const.build.characteristics";
    public static final String KEY_INPUT_POINTER = "input.pointer.device";

    private OhConfigurationConverter() {}

    /**
     * Convert the OH key/value items + display snapshot into Android Configuration.
     *
     * @param ohConfigKvPairs flat array {k0,v0,k1,v1,...}; nullable
     * @param display         display snapshot (must not be null)
     */
    public static Configuration convert(String[] ohConfigKvPairs, OhDisplayProvider.Snapshot display) {
        Configuration cfg = new Configuration();
        cfg.setToDefaults();

        // ---------------- Locale (must non-empty, see §1.1.4.4 warn) ----------
        Locale locale = parseLocale(get(ohConfigKvPairs, KEY_LOCALE),
                                     get(ohConfigKvPairs, KEY_LANGUAGE));
        cfg.setLocales(new LocaleList(locale));

        // ---------------- Font scale ----------------
        cfg.fontScale = parseFloat(get(ohConfigKvPairs, KEY_FONT_SCALE), 1.0f);

        // ---------------- Density / screen geometry from display snapshot ----
        cfg.densityDpi = display.densityDpi;
        // screenWidthDp / screenHeightDp / smallestScreenWidthDp = px / density
        int wDp = (int) (display.widthPx / display.density);
        int hDp = (int) (display.heightPx / display.density);
        cfg.screenWidthDp = wDp;
        cfg.screenHeightDp = hDp;
        cfg.smallestScreenWidthDp = Math.min(wDp, hDp);

        // ---------------- Orientation ----------------
        cfg.orientation = display.widthPx >= display.heightPx
                ? Configuration.ORIENTATION_LANDSCAPE
                : Configuration.ORIENTATION_PORTRAIT;

        // ---------------- screenLayout (size + long + layoutdir) ----------
        int screenLayout = Configuration.SCREENLAYOUT_LAYOUTDIR_LTR;
        // SizeMask
        int sizeMask;
        int sw = cfg.smallestScreenWidthDp;
        if (sw >= 720)      sizeMask = Configuration.SCREENLAYOUT_SIZE_XLARGE;
        else if (sw >= 600) sizeMask = Configuration.SCREENLAYOUT_SIZE_LARGE;
        else if (sw >= 480) sizeMask = Configuration.SCREENLAYOUT_SIZE_NORMAL;
        else                sizeMask = Configuration.SCREENLAYOUT_SIZE_SMALL;
        screenLayout |= sizeMask;
        // long if aspect ratio > 1.6
        float longRatio = (float) Math.max(display.widthPx, display.heightPx)
                          / Math.min(display.widthPx, display.heightPx);
        screenLayout |= longRatio >= 1.6f
                ? Configuration.SCREENLAYOUT_LONG_YES
                : Configuration.SCREENLAYOUT_LONG_NO;
        cfg.screenLayout = screenLayout;

        // ---------------- uiMode: night + type ----------------
        int uiMode = Configuration.UI_MODE_TYPE_NORMAL;
        String colorMode = get(ohConfigKvPairs, KEY_COLOR_MODE);
        if ("dark".equalsIgnoreCase(colorMode)) {
            uiMode |= Configuration.UI_MODE_NIGHT_YES;
        } else {
            uiMode |= Configuration.UI_MODE_NIGHT_NO;
        }
        // P2: uiMode type from device characteristics
        String devType = get(ohConfigKvPairs, KEY_DEVICE_TYPE);
        if (devType != null) {
            int typeMask = Configuration.UI_MODE_TYPE_NORMAL;
            switch (devType) {
                case "watch":    typeMask = Configuration.UI_MODE_TYPE_WATCH; break;
                case "tv":       typeMask = Configuration.UI_MODE_TYPE_TELEVISION; break;
                case "car":      typeMask = Configuration.UI_MODE_TYPE_CAR; break;
                case "vr":       typeMask = Configuration.UI_MODE_TYPE_VR_HEADSET; break;
                case "desktop":  typeMask = Configuration.UI_MODE_TYPE_DESK; break;
                default:         typeMask = Configuration.UI_MODE_TYPE_NORMAL; break;
            }
            // Replace type bits
            uiMode = (uiMode & ~Configuration.UI_MODE_TYPE_MASK) | typeMask;
        }
        cfg.uiMode = uiMode;

        // ---------------- mcc / mnc (P2) ----------------
        cfg.mcc = parseInt(get(ohConfigKvPairs, KEY_MCC), 0);
        cfg.mnc = parseInt(get(ohConfigKvPairs, KEY_MNC), 0);

        // ---------------- input devices (P2) ----------------
        String pointerDev = get(ohConfigKvPairs, KEY_INPUT_POINTER);
        if (pointerDev != null && Boolean.parseBoolean(pointerDev)) {
            cfg.touchscreen = Configuration.TOUCHSCREEN_FINGER;
            cfg.keyboard = Configuration.KEYBOARD_NOKEYS;
            cfg.keyboardHidden = Configuration.KEYBOARDHIDDEN_NO;
            cfg.navigation = Configuration.NAVIGATION_NONAV;
            cfg.navigationHidden = Configuration.NAVIGATIONHIDDEN_YES;
        } else {
            cfg.touchscreen = Configuration.TOUCHSCREEN_FINGER;
            cfg.keyboard = Configuration.KEYBOARD_NOKEYS;
            cfg.navigation = Configuration.NAVIGATION_NONAV;
        }

        Log.i(TAG, "Converted Configuration: locale=" + locale
                + " fontScale=" + cfg.fontScale
                + " densityDpi=" + cfg.densityDpi
                + " orientation=" + cfg.orientation
                + " screenWidthDp=" + cfg.screenWidthDp
                + " uiMode=0x" + Integer.toHexString(cfg.uiMode));
        return cfg;
    }

    /** Best-effort BCP-47 parse with safe fallback. */
    private static Locale parseLocale(String localeTag, String langOnly) {
        String tag = (localeTag != null && !localeTag.isEmpty()) ? localeTag : langOnly;
        if (tag != null && !tag.isEmpty()) {
            try {
                Locale loc = Locale.forLanguageTag(tag);
                if (loc != null && !loc.getLanguage().isEmpty()) {
                    return loc;
                }
            } catch (Throwable ignored) {}
        }
        // Fallback: JVM default → en-US ultimate
        Locale def = Locale.getDefault();
        if (def == null || def.getLanguage().isEmpty()) {
            def = Locale.US;
        }
        return def;
    }

    private static String get(String[] kv, String key) {
        if (kv == null || key == null) return null;
        for (int i = 0; i + 1 < kv.length; i += 2) {
            if (key.equals(kv[i])) return kv[i + 1];
        }
        return null;
    }

    private static float parseFloat(String s, float fallback) {
        if (s == null || s.isEmpty()) return fallback;
        try { return Float.parseFloat(s); } catch (NumberFormatException e) { return fallback; }
    }

    private static int parseInt(String s, int fallback) {
        if (s == null || s.isEmpty()) return fallback;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return fallback; }
    }
}
