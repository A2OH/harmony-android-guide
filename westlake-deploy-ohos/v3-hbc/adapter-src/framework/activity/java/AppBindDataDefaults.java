/*
 * AppBindDataDefaults.java
 *
 * Factories for A_ONLY_SYNTH fields — Android-only fields where null would
 * crash AOSP downstream paths (AutofillManager / ContentCaptureManager
 * NPE on Activity Window attach). Must produce "looks-real" disabled
 * instances rather than null.
 *
 * Authoritative source: doc/ability_manager_ipc_adapter_design.html §1.1.4.8 and §1.1.5.2
 */
package adapter.activity;

import android.content.ContentCaptureOptions;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.SystemClock;
import android.util.Log;
import android.view.autofill.AutofillManager;
import android.view.contentcapture.ContentCaptureManager;

public final class AppBindDataDefaults {

    private static final String TAG = "OH_AppBindDefaults";

    private AppBindDataDefaults() {}

    /** Disabled AutofillOptions (OH has no Autofill service). */
    public static android.content.AutofillOptions disabledAutofillOptions() {
        try {
            // Public API since AOSP 12: forWhitelistingItself() returns
            // a permissive AutofillOptions. We instead want disabled —
            // construct via the (loggingLevel, compatModeEnabled) ctor.
            return new android.content.AutofillOptions(
                    AutofillManager.NO_LOGGING, /* compatModeEnabled= */ false);
        } catch (Throwable t) {
            Log.w(TAG, "AutofillOptions ctor failed; falling back to whitelistingItself", t);
            try {
                return android.content.AutofillOptions.forWhitelistingItself();
            } catch (Throwable t2) {
                return null;
            }
        }
    }

    /** Disabled ContentCaptureOptions (OH has no ContentCapture service). */
    public static ContentCaptureOptions disabledContentCaptureOptions() {
        try {
            // Public ctor: ContentCaptureOptions(int loggingLevel, int maxBufferSize,
            //   int idleFlushingFrequencyMs, int textChangeFlushingFrequencyMs,
            //   int logHistorySize, int disableFlushForViewTreeAppearing,
            //   ArraySet<ComponentName> whitelistedComponents)
            // Simpler ctor exists: (ArraySet<ComponentName>) → null = disabled
            return new ContentCaptureOptions(/* whitelistedComponents= */ null);
        } catch (Throwable t) {
            Log.w(TAG, "ContentCaptureOptions ctor failed", t);
            try {
                return ContentCaptureOptions.forWhitelistingItself();
            } catch (Throwable t2) {
                return null;
            }
        }
    }

    /** Per-app CompatibilityInfo derived from ApplicationInfo + Configuration. */
    public static CompatibilityInfo compatInfoFor(ApplicationInfo appInfo, Configuration cfg) {
        if (appInfo == null) return CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        try {
            // CompatibilityInfo(ApplicationInfo appInfo, int screenLayout,
            //                   int sw, boolean forceCompat)
            int screenLayout = cfg != null ? cfg.screenLayout : 0;
            int smallestSw = cfg != null ? cfg.smallestScreenWidthDp : 0;
            return new CompatibilityInfo(appInfo, screenLayout, smallestSw, /* forceCompat= */ false);
        } catch (Throwable t) {
            Log.w(TAG, "CompatibilityInfo ctor failed; falling back to DEFAULT", t);
            return CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        }
    }

    public static long startRequestedElapsedTime() {
        return SystemClock.elapsedRealtime();
    }

    public static long startRequestedUptime() {
        return SystemClock.uptimeMillis();
    }

    /** Empty disabledCompatChanges — let AOSP apply all targetSdk-driven compat. */
    public static long[] emptyDisabledCompatChanges() {
        return new long[0];
    }

    /**
     * Attempt to read OH ro.serialno via SystemProperties (AOSP's class is
     * already wired to OH SystemProperties through liboh_adapter_bridge —
     * verify in your environment). Falls back to "unknown".
     */
    public static String buildSerial() {
        try {
            return android.os.SystemProperties.get("ro.serialno", "unknown");
        } catch (Throwable t) {
            return "unknown";
        }
    }
}
