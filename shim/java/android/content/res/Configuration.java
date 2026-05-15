package android.content.res;
import java.util.Locale;

public class Configuration {
    public static final int ORIENTATION_UNDEFINED  = 0;
    public static final int ORIENTATION_PORTRAIT   = 1;
    public static final int ORIENTATION_LANDSCAPE  = 2;

    public int             orientation   = ORIENTATION_PORTRAIT;
    public int             screenWidthDp  = 240;
    public int             screenHeightDp = 400;
    public int             densityDpi     = 320;
    public java.util.Locale locale        = java.util.Locale.getDefault();
    public int             uiMode         = 0;

    public static final int UI_MODE_TYPE_MASK = 0x0f;
    public static final int UI_MODE_TYPE_UNDEFINED = 0;
    public static final int UI_MODE_TYPE_NORMAL = 1;
    public static final int UI_MODE_TYPE_DESK = 2;
    public static final int UI_MODE_TYPE_CAR = 3;
    public static final int UI_MODE_TYPE_TELEVISION = 4;
    public static final int UI_MODE_TYPE_APPLIANCE = 5;
    public static final int UI_MODE_TYPE_WATCH = 6;
    public static final int UI_MODE_TYPE_VR_HEADSET = 7;

    public static final int UI_MODE_NIGHT_MASK = 0x30;
    public static final int UI_MODE_NIGHT_UNDEFINED = 0;
    public static final int UI_MODE_NIGHT_NO = 0x10;
    public static final int UI_MODE_NIGHT_YES = 0x20;

    public static final int SCREENLAYOUT_SIZE_MASK = 0x0f;
    public static final int SCREENLAYOUT_SIZE_UNDEFINED = 0;
    public static final int SCREENLAYOUT_SIZE_SMALL = 1;
    public static final int SCREENLAYOUT_SIZE_NORMAL = 2;
    public static final int SCREENLAYOUT_SIZE_LARGE = 3;
    public static final int SCREENLAYOUT_SIZE_XLARGE = 4;
    public int screenLayout = 0;
    public float fontScale = 1.0f;

    public Configuration() {}

    public Configuration(Configuration o) {
        this.orientation = o.orientation;
        this.screenWidthDp = o.screenWidthDp;
        this.screenHeightDp = o.screenHeightDp;
        this.densityDpi = o.densityDpi;
        this.locale = o.locale;
        this.uiMode = o.uiMode;
        this.screenLayout = o.screenLayout;
        this.fontScale = o.fontScale;
    }

    public int smallestScreenWidthDp = 320;
    public int navigation = 0;
    public int touchscreen = 0;
    public int keyboard = 0;
    public int keyboardHidden = 0;
    public int hardKeyboardHidden = 0;
    public int navigationHidden = 0;
    public int mnc = 0;
    public int mcc = 0;
    public int colorMode = 0;
    public int screenLayoutLong = 0;

    public boolean isScreenRound() { return false; }

    public android.os.LocaleList getLocales() {
        return new android.os.LocaleList(locale != null ? locale : java.util.Locale.getDefault());
    }

    public void setLocales(android.os.LocaleList locales) {
        if (locales != null && locales.size() > 0) {
            locale = locales.get(0);
        }
    }

    public void setToDefaults() {
        fontScale = 1.0f;
        orientation = ORIENTATION_UNDEFINED;
        uiMode = 0;
        locale = java.util.Locale.getDefault();
    }

    public boolean equals(Configuration that) {
        if (that == null) {
            return false;
        }
        return orientation == that.orientation
                && screenWidthDp == that.screenWidthDp
                && screenHeightDp == that.screenHeightDp
                && densityDpi == that.densityDpi
                && uiMode == that.uiMode
                && screenLayout == that.screenLayout
                && Float.compare(fontScale, that.fontScale) == 0
                && smallestScreenWidthDp == that.smallestScreenWidthDp
                && navigation == that.navigation
                && touchscreen == that.touchscreen
                && keyboard == that.keyboard
                && keyboardHidden == that.keyboardHidden
                && hardKeyboardHidden == that.hardKeyboardHidden
                && navigationHidden == that.navigationHidden
                && mnc == that.mnc
                && mcc == that.mcc
                && colorMode == that.colorMode
                && screenLayoutLong == that.screenLayoutLong
                && (locale == that.locale || (locale != null && locale.equals(that.locale)));
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof Configuration && equals((Configuration) that);
    }

    @Override
    public int hashCode() {
        int result = orientation;
        result = 31 * result + screenWidthDp;
        result = 31 * result + screenHeightDp;
        result = 31 * result + densityDpi;
        result = 31 * result + uiMode;
        result = 31 * result + screenLayout;
        result = 31 * result + Float.floatToIntBits(fontScale);
        result = 31 * result + smallestScreenWidthDp;
        result = 31 * result + navigation;
        result = 31 * result + touchscreen;
        result = 31 * result + keyboard;
        result = 31 * result + keyboardHidden;
        result = 31 * result + hardKeyboardHidden;
        result = 31 * result + navigationHidden;
        result = 31 * result + mnc;
        result = 31 * result + mcc;
        result = 31 * result + colorMode;
        result = 31 * result + screenLayoutLong;
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        return result;
    }

    public int diff(Configuration delta) { return 0; }

    public static final int SCREENLAYOUT_LAYOUTDIR_MASK = 0xC0;
    public static final int SCREENLAYOUT_LAYOUTDIR_SHIFT = 6;
    public static final int SCREENLAYOUT_LAYOUTDIR_UNDEFINED = 0;
    public static final int SCREENLAYOUT_LAYOUTDIR_LTR = 1 << SCREENLAYOUT_LAYOUTDIR_SHIFT;
    public static final int SCREENLAYOUT_LAYOUTDIR_RTL = 2 << SCREENLAYOUT_LAYOUTDIR_SHIFT;

    /**
     * AOSP-default body of {@code Configuration.setTo(Configuration)} —
     * copy every public field from {@code o} into {@code this}. Mirrors
     * frameworks/base/core/java/android/content/res/Configuration.java
     * (Android 14/Android 15 source).
     */
    public void setTo(Configuration o) {
        if (o == null) return;
        this.fontScale          = o.fontScale;
        this.mcc                = o.mcc;
        this.mnc                = o.mnc;
        this.locale             = o.locale;
        this.orientation        = o.orientation;
        this.screenLayout       = o.screenLayout;
        this.colorMode          = o.colorMode;
        this.uiMode             = o.uiMode;
        this.touchscreen        = o.touchscreen;
        this.keyboard           = o.keyboard;
        this.keyboardHidden     = o.keyboardHidden;
        this.hardKeyboardHidden = o.hardKeyboardHidden;
        this.navigation         = o.navigation;
        this.navigationHidden   = o.navigationHidden;
        this.densityDpi         = o.densityDpi;
        this.screenWidthDp      = o.screenWidthDp;
        this.screenHeightDp     = o.screenHeightDp;
        this.smallestScreenWidthDp = o.smallestScreenWidthDp;
        this.screenLayoutLong   = o.screenLayoutLong;
    }

    /**
     * AOSP-default body of {@code Configuration.setLocale(Locale)} —
     * sets the primary locale and refreshes the layout direction bits.
     */
    public void setLocale(java.util.Locale loc) {
        this.locale = loc;
        setLayoutDirection(loc);
    }

    /**
     * AOSP-default body of {@code Configuration.setLayoutDirection(Locale)} —
     * derives the layout direction from a Locale and stores it in the
     * SCREENLAYOUT_LAYOUTDIR_MASK bits of {@link #screenLayout}.
     */
    public void setLayoutDirection(java.util.Locale loc) {
        int dir = (loc == null) ? SCREENLAYOUT_LAYOUTDIR_LTR : SCREENLAYOUT_LAYOUTDIR_LTR;
        // We default to LTR; real direction inference (TextUtils.getLayoutDirectionFromLocale)
        // is not modelled in this thin shim. Apps that need RTL set it explicitly.
        this.screenLayout = (this.screenLayout & ~SCREENLAYOUT_LAYOUTDIR_MASK) | dir;
    }

    /**
     * AOSP-default body of {@code Configuration.getLayoutDirection()} —
     * returns View.LAYOUT_DIRECTION_LTR (=0) or LAYOUT_DIRECTION_RTL (=1)
     * derived from the SCREENLAYOUT_LAYOUTDIR_MASK bits.
     */
    public int getLayoutDirection() {
        return (screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK) == SCREENLAYOUT_LAYOUTDIR_RTL ? 1 : 0;
    }
}
