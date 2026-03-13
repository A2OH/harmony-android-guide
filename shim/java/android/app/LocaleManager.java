package android.app;
import android.os.LocaleList;
import android.os.LocaleList;

import android.os.LocaleList;

/**
 * A2OH shim: LocaleManager — per-app locale management (API 33+).
 *
 * OH mapping: No direct equivalent. OpenHarmony controls locale via
 * @ohos.intl and system settings; per-app locale override is not exposed
 * as a public API. This shim satisfies compile-time dependencies and
 * returns sensible defaults at runtime (empty LocaleList = system default).
 */
public class LocaleManager {

    private LocaleList mAppLocales = new LocaleList();

    // ── Per-application locales ────────────────────────────────────────────────

    /**
     * Sets the UI locales for the calling application.
     *
     * @param locales the desired locales; an empty list means "use system default".
     */
    public void setApplicationLocales(LocaleList locales) {
        mAppLocales = (locales != null) ? locales : new LocaleList();
    }

    /**
     * Returns the UI locales for the calling application.
     * An empty list indicates the app follows the system locale.
     */
    public LocaleList getApplicationLocales() {
        return mAppLocales;
    }

    // ── System locales ─────────────────────────────────────────────────────────

    /**
     * Returns the current system locales.
     * In the shim this delegates to {@link LocaleList#getDefault()}.
     */
    public LocaleList getSystemLocales() {
        return new LocaleList();
    }
}
