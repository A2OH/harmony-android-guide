// SPDX-License-Identifier: Apache-2.0
//
// Westlake V2-Step5 Window stub (decision 12-A from BINDER_PIVOT_DESIGN_V2.md §3.5).
//
// Westlake-owned classpath-shadowed Window. No real PhoneWindow chrome
// (no title bar, no action bar, no status/nav bar, no system insets).
// `setContentView(int)` inflates the layout via LayoutInflater and
// stores the result as both decor view and content view; `getDecorView()`
// returns that view.
//
// Generic across all APKs — no per-app branches. The pre-V2 Window had
// McDonalds-specific structured page-shell + toolbar alias logic; that
// has been removed per the "NO per-app hacks" architectural rule and the
// CR14/CR16 launcher-slim cleanup.
//
// Backwards-compatible shim methods (`adoptContext`, `installMinimalStandaloneContent`)
// are retained as benign no-ops because existing callers in Activity.java,
// WestlakeActivityThread.java, and WestlakeLauncher.java already wrap
// these calls in try/catch (Throwable), but keeping them as defined
// no-ops avoids stack-trace noise.

package android.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.widget.MediaController;

public class Window {

    // ------------------------------------------------------------------
    // Feature / progress / decor constants (kept from V1 Window — Tier-1
    // surface read by AppCompat and many host apps).
    // ------------------------------------------------------------------
    public static final int FEATURE_OPTIONS_PANEL = 0;
    public static final int FEATURE_NO_TITLE = 1;
    public static final int FEATURE_PROGRESS = 2;
    public static final int FEATURE_LEFT_ICON = 3;
    public static final int FEATURE_RIGHT_ICON = 4;
    public static final int FEATURE_INDETERMINATE_PROGRESS = 5;
    public static final int FEATURE_CONTEXT_MENU = 6;
    public static final int FEATURE_CUSTOM_TITLE = 7;
    public static final int FEATURE_ACTION_BAR = 8;
    public static final int FEATURE_ACTION_BAR_OVERLAY = 9;
    public static final int FEATURE_ACTION_MODE_OVERLAY = 10;
    public static final int FEATURE_SWIPE_TO_DISMISS = 11;
    public static final int FEATURE_CONTENT_TRANSITIONS = 12;
    public static final int FEATURE_ACTIVITY_TRANSITIONS = 13;
    public static final int FEATURE_MAX = FEATURE_ACTIVITY_TRANSITIONS;

    public static final int PROGRESS_VISIBILITY_ON = -1;
    public static final int PROGRESS_VISIBILITY_OFF = -2;
    public static final int PROGRESS_INDETERMINATE_ON = -3;
    public static final int PROGRESS_INDETERMINATE_OFF = -4;
    public static final int PROGRESS_START = 0;
    public static final int PROGRESS_END = 10000;
    public static final int PROGRESS_SECONDARY_START = 20000;
    public static final int PROGRESS_SECONDARY_END = 30000;

    public static final int DECOR_CAPTION_SHADE_AUTO = 0;
    public static final int DECOR_CAPTION_SHADE_DARK = 0;
    public static final int DECOR_CAPTION_SHADE_LIGHT = 0;

    public static final int ID_ANDROID_CONTENT = 0x01020002;
    public static final int NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME = 0;
    public static final int STATUS_BAR_BACKGROUND_TRANSITION_NAME = 0;

    // ------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------
    private Context mContext;
    private View mDecorView;
    private View mContentView;
    private Callback mCallback;
    private CharSequence mTitle;
    private int mTitleColor;
    private int mStatusBarColor;
    private int mNavigationBarColor;
    private int mFeatures;
    private int mFlags;

    // ------------------------------------------------------------------
    // Callback (Activity implements this)
    // ------------------------------------------------------------------
    public interface Callback {
        // Required methods (implementers must provide). These match the
        // pre-V2 Window.Callback so existing Activity.java implementations
        // remain valid.
        boolean dispatchKeyEvent(KeyEvent event);
        boolean dispatchTouchEvent(MotionEvent event);
        boolean dispatchTrackballEvent(MotionEvent event);
        void onContentChanged();
        void onWindowFocusChanged(boolean hasFocus);
        void onAttachedToWindow();
        void onDetachedFromWindow();
        void onWindowAttributesChanged(WindowManager.LayoutParams attrs);
        boolean onMenuOpened(int featureId, android.view.Menu menu);
        boolean onMenuItemSelected(int featureId, android.view.MenuItem item);
        void onPanelClosed(int featureId, android.view.Menu menu);
        boolean onSearchRequested();
        View onCreatePanelView(int featureId);
        boolean onCreatePanelMenu(int featureId, android.view.Menu menu);
        boolean onPreparePanel(int featureId, View view, android.view.Menu menu);

        // Methods added in AOSP after API 19. Defaulted to no-op so existing
        // shim Callback implementers (which only override the legacy set
        // above) compile unchanged.
        default boolean dispatchKeyShortcutEvent(KeyEvent event) { return false; }
        default boolean dispatchGenericMotionEvent(MotionEvent event) { return false; }
        default boolean dispatchPopulateAccessibilityEvent(android.view.accessibility.AccessibilityEvent event) { return false; }
        default boolean onSearchRequested(SearchEvent searchEvent) { return onSearchRequested(); }
        default android.view.ActionMode onWindowStartingActionMode(android.view.ActionMode.Callback callback) { return null; }
        default android.view.ActionMode onWindowStartingActionMode(android.view.ActionMode.Callback callback, int type) { return null; }
        default void onActionModeStarted(android.view.ActionMode mode) {}
        default void onActionModeFinished(android.view.ActionMode mode) {}
    }

    public static interface OnContentApplyWindowInsetsListener {
        android.util.Pair<android.graphics.Insets, WindowInsets> onContentApplyWindowInsets(View view, WindowInsets insets);
    }

    public static interface OnFrameMetricsAvailableListener {}

    // ------------------------------------------------------------------
    // ctor
    // ------------------------------------------------------------------
    public Window(Context context) {
        mContext = context;
        // CR36: don't construct a FrameLayout here. Building a real
        // android.view.View in our standalone dalvikvm substrate pulls
        // android.graphics.RenderNode.nCreate from framework.jar, whose
        // native side is not loaded — the ctor throws UnsatisfiedLinkError
        // (a LinkageError) and leaves mWindow=null all the way out to
        // AppCompat's attachToWindow, which then NPEs on Window.getCallback().
        //
        // setContentView() already lazily constructs its own decor when an
        // app invokes it, and that path is `try { ... } catch (Throwable)`
        // wrapped so the same UnsatisfiedLinkError there degrades to a
        // null content. peekDecorView() / getDecorView() return null until
        // setContentView fills them in, which matches AOSP's contract.
        //
        // Safe-primitive (contract bucket (b)): return null/empty for state
        // we cannot honestly populate in this substrate. The Window object
        // itself MUST be constructable so callers' mWindow field is non-null.
        mDecorView = null;
    }

    // ------------------------------------------------------------------
    // Tier-1 API surface
    // ------------------------------------------------------------------
    public Context getContext() { return mContext; }

    /** Backwards-compat shim entry point used by Activity / WestlakeActivityThread. */
    public void adoptContext(Context context) {
        if (context != null) {
            mContext = context;
        }
    }

    /**
     * Backwards-compat shim entry point used by WestlakeLauncher. V2 design
     * has no chrome/title bar/action bar — caller already handles return=false
     * gracefully.
     */
    public boolean installMinimalStandaloneContent() {
        return false;
    }

    public void setContentView(int layoutResID) {
        try {
            LayoutInflater inflater = getLayoutInflater();
            android.widget.FrameLayout tempRoot = new android.widget.FrameLayout(mContext);
            View inflated = null;
            try {
                inflated = inflater.inflate(layoutResID, tempRoot, true);
            } catch (Throwable t) {
                try {
                    inflated = inflater.inflate(layoutResID, null);
                } catch (Throwable ignored) {}
            }
            if (tempRoot.getChildCount() > 0) {
                View content = tempRoot.getChildAt(0);
                tempRoot.removeAllViews();
                setContentView(content);
            } else if (inflated != null && inflated != tempRoot) {
                setContentView(inflated);
            }
        } catch (Throwable t) {
            android.util.Log.w("Window", "setContentView(int 0x"
                    + Integer.toHexString(layoutResID) + ") failed: "
                    + t.getClass().getName() + ": " + t.getMessage());
        }
    }

    public void setContentView(View view) {
        setContentView(view, null);
    }

    /**
     * Object-typed overload used by Activity.java's
     * `setContentView(Object, Object)`. Coerces params into
     * ViewGroup.LayoutParams when possible.
     */
    public void setContentView(View view, Object params) {
        setContentView(view, params instanceof ViewGroup.LayoutParams
                ? (ViewGroup.LayoutParams) params
                : null);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentView = view;
        if (view == null) {
            return;
        }
        if (mContext != null && view.mContext == null) {
            view.mContext = mContext;
        }
        if (params != null) {
            view.setLayoutParams(params);
        }
        try {
            if (mDecorView instanceof ViewGroup && mDecorView != view) {
                ViewGroup decor = (ViewGroup) mDecorView;
                decor.removeAllViews();
                if (params == null && decor instanceof android.widget.FrameLayout) {
                    view.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                }
                decor.addView(view);
            } else {
                mDecorView = view;
            }
        } catch (Throwable ignored) {
            mDecorView = view;
        }
    }

    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (mDecorView instanceof ViewGroup) {
            if (params != null) {
                view.setLayoutParams(params);
            }
            ((ViewGroup) mDecorView).addView(view);
        }
    }

    public View getDecorView() {
        // CR-W: AppCompatDelegateImpl.ensureSubDecor() does
        //   mWindow.findViewById(android.R.id.content)
        // expecting a pre-existing ViewGroup (AOSP PhoneWindow installs one
        // in its DecorView constructor). Without it, the smali's
        // child-transfer + setId path is skipped, leaving the new
        // ContentFrameLayout WITHOUT android.R.id.content, which then
        // breaks the second findViewById(android.R.id.content) later in
        // y(). Solution: lazily build a minimal DecorView (FrameLayout)
        // containing a content FrameLayout with id=android.R.id.content
        // when getDecorView is first read.
        //
        // CR36 documented why we couldn't eagerly construct this in the
        // Window ctor (RenderNode.nCreate UnsatisfiedLinkError under the
        // standalone substrate). Lazy + try/catch sidesteps that — the
        // catch leaves mDecorView=null which is the pre-CR-W behaviour.
        if (mDecorView == null) {
            try {
                android.widget.FrameLayout decor =
                        new android.widget.FrameLayout(mContext);
                android.widget.FrameLayout content =
                        new android.widget.FrameLayout(mContext);
                content.setId(0x1020002); // android.R.id.content
                decor.addView(content, new android.widget.FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                // CR-X (2026-05-15): AOSP PhoneWindow.DecorView reads
                //   ?android:windowBackground
                // from the activity's theme and installs it as the decor's
                // own Drawable so the first frame paints to the theme's
                // background color rather than transparent/white. Our lazy
                // DecorView replicates that here: peek the activity's
                // android:theme out of AndroidManifest.xml, walk the
                // compiled style bag (with parent chain) in resources.arsc,
                // and lift the windowBackground (or colorBackground)
                // attribute to a ColorDrawable on the decor.
                //
                // Generic across any apk: no per-app branches, all data
                // sourced from the apk that mContext.getPackageResourcePath()
                // points at (or `-Dwestlake.apk.path` set by the test
                // harness). Failure is silent — the catch leaves the decor
                // without a background, matching pre-CR-X behaviour.
                int themeBg = resolveThemeBackgroundColor();
                if (themeBg != 0) {
                    try {
                        decor.setBackgroundColor(themeBg);
                        // dalvik-kitkat's android.util.Log.i is dropped on
                        // OHOS rk3568 (no logd binder back-channel); use
                        // System.out so the marker reaches the test harness.
                        System.out.println("[Window] decor windowBackground=0x"
                                + Integer.toHexString(themeBg)
                                + " (theme-resolved)");
                    } catch (Throwable t) {
                        System.out.println("[Window] decor setBackgroundColor threw: " + t);
                    }
                } else {
                    System.out.println("[Window] decor windowBackground UNRESOLVED -- using transparent");
                }
                mDecorView = decor;
            } catch (Throwable ignored) {
                // RenderNode.nCreate may UnsatisfiedLinkError — leave null,
                // matches pre-CR-W behaviour.
            }
        }
        return mDecorView;
    }

    /**
     * CR-X (2026-05-15): resolve the activity's
     * {@code ?android:windowBackground} (falling back to
     * {@code ?android:colorBackground}) to a concrete ARGB color.
     *
     * <p>Source-of-truth chain, all reading from the apk identified by
     * {@code mContext.getPackageResourcePath()} (or the
     * {@code westlake.apk.path} system property when the Context isn't
     * apk-backed — e.g. early standalone-substrate Activity construction):
     * <ol>
     *   <li>{@link android.content.pm.ManifestParser} → activity's
     *       {@code android:theme} resource id (or app-level fallback).</li>
     *   <li>{@link com.westlake.services.ResourceArscParser} →
     *       {@link android.content.res.ResourceTable#getStyleAttrs} for the
     *       theme id (parent chain already walked).</li>
     *   <li>Style bag → {@code 0x01010054}
     *       ({@code android.R.attr.windowBackground}) or
     *       {@code 0x01010031}
     *       ({@code android.R.attr.colorBackground}).</li>
     *   <li>Value-side: {@code @0xXXXXX} reference → arsc value map
     *       (color resources are TYPE_INT_COLOR_ARGB8); {@code #AARRGGBB}
     *       literal → direct hex parse.</li>
     * </ol>
     *
     * <p>Returns 0 when any step fails. Generic — no per-app branches.
     */
    private int resolveThemeBackgroundColor() {
        try {
            // Step 1: which apk are we resolving against?
            String apkPath = null;
            String apkPathSource = "(none)";
            if (mContext != null) {
                try {
                    apkPath = mContext.getPackageResourcePath();
                    if (apkPath != null && !apkPath.isEmpty()) {
                        apkPathSource = "Context.getPackageResourcePath";
                    }
                } catch (Throwable ignored) {}
            }
            if (apkPath == null || apkPath.isEmpty()) {
                try {
                    apkPath = System.getProperty("westlake.apk.path");
                    if (apkPath != null && !apkPath.isEmpty()) {
                        apkPathSource = "westlake.apk.path";
                    }
                } catch (Throwable ignored) {}
            }
            if (apkPath == null || apkPath.isEmpty()) {
                System.out.println("[Window] resolveThemeBackgroundColor: no apkPath");
                return 0;
            }

            // Step 2: activity FQCN — needed to pick activity-specific theme.
            String activityFqcn = null;
            if (mContext instanceof android.app.Activity) {
                try {
                    android.content.ComponentName cn =
                            ((android.app.Activity) mContext).getComponentName();
                    if (cn != null) activityFqcn = cn.getClassName();
                } catch (Throwable ignored) {}
                if (activityFqcn == null) {
                    activityFqcn = mContext.getClass().getName();
                }
            }
            System.out.println("[Window] resolveThemeBackgroundColor: apk=" + apkPath
                    + " (src=" + apkPathSource + ") activity=" + activityFqcn);

            // Step 3: parse manifest → theme resource id (cached per apk).
            android.content.pm.ManifestParser.ManifestInfo mi =
                    sManifestCache.get(apkPath);
            if (mi == null) {
                byte[] axml = readApkEntryBytes(apkPath, "AndroidManifest.xml");
                if (axml != null) {
                    try {
                        mi = android.content.pm.ManifestParser.parse(axml);
                    } catch (Throwable t) {
                        System.out.println("[Window] manifest parse threw: " + t);
                    }
                } else {
                    System.out.println("[Window] manifest not readable from apk=" + apkPath);
                }
                if (mi == null) {
                    mi = MANIFEST_NONE;
                }
                sManifestCache.put(apkPath, mi);
            }
            if (mi == MANIFEST_NONE) {
                System.out.println("[Window] manifest=NONE for apk=" + apkPath);
                return 0;
            }
            int themeResId = mi.getActivityTheme(activityFqcn);
            System.out.println("[Window] manifest themes: app=0x"
                    + Integer.toHexString(mi.applicationTheme)
                    + " activities=" + mi.activityThemes.size()
                    + " resolved-for-" + activityFqcn + "=0x"
                    + Integer.toHexString(themeResId));
            if (themeResId == 0) {
                return 0;
            }

            // Step 4: minimal in-place arsc walker for the
            // {@code android:windowBackground} attribute of the style
            // identified by {@code themeResId}. We can't reuse
            // {@link com.westlake.services.ResourceArscParser#parse} here
            // because its underlying {@link android.content.res.ResourceTable}
            // calls
            // {@code ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)}
            // — and {@code java.nio.ByteOrder.isLittleEndian:()Z} is an
            // unimplemented native on dalvik-on-OHOS arm32 (verified:
            // {@code UnsatisfiedLinkError} thrown at first call). The same
            // workaround LayoutInflater uses for its identifier-table
            // parse: pure byte-array walk, no ByteBuffer, no ByteOrder.
            //
            // resources.arsc is STORED in modern apks (verified for noice)
            // so {@link #readStoredZipEntry} produces it without invoking
            // Inflater (the OTHER dalvik-on-OHOS pathology — Inflater
            // ref-leaks 32M+ jobjs without terminating).
            byte[] arscBytes = sArscBytesCache.get(apkPath);
            if (arscBytes == null) {
                arscBytes = readStoredZipEntry(apkPath, "resources.arsc");
                sArscBytesCache.put(apkPath,
                        arscBytes != null ? arscBytes : EMPTY_BYTES);
            }
            if (arscBytes == null || arscBytes.length == 0 || arscBytes == EMPTY_BYTES) {
                System.out.println("[Window] arsc not readable from apk");
                return 0;
            }
            System.out.println("[Window] arsc bytes=" + arscBytes.length);

            int color;
            sCurrentArsc.set(arscBytes);
            try {
                color = walkArscForThemeBg(arscBytes, themeResId);
            } finally {
                sCurrentArsc.set(null);
            }
            System.out.println("[Window] arsc walk theme=0x"
                    + Integer.toHexString(themeResId) + " -> color=0x"
                    + Integer.toHexString(color)
                    + " (activity=" + activityFqcn + ")");
            return color;
        } catch (Throwable t) {
            System.out.println("[Window] resolveThemeBackgroundColor threw: " + t);
            t.printStackTrace(System.out);
            return 0;
        }
    }

    /** android.R.attr.windowBackground (frameworks/base public attrs). */
    private static final int ATTR_WINDOW_BACKGROUND = 0x01010054;
    /** android.R.attr.colorBackground. */
    private static final int ATTR_COLOR_BACKGROUND  = 0x01010031;

    /** Cached manifests per apk path. */
    private static final java.util.Map<String, android.content.pm.ManifestParser.ManifestInfo>
            sManifestCache = new java.util.concurrent.ConcurrentHashMap<>();
    /** Sentinel for apks whose manifest failed to parse / is missing. */
    private static final android.content.pm.ManifestParser.ManifestInfo MANIFEST_NONE =
            new android.content.pm.ManifestParser.ManifestInfo();
    /** Cached arsc raw bytes per apk path. Entry value is
     *  {@link #EMPTY_BYTES} when the apk has no readable resources.arsc. */
    private static final java.util.Map<String, byte[]> sArscBytesCache =
            new java.util.concurrent.ConcurrentHashMap<>();
    private static final byte[] EMPTY_BYTES = new byte[0];

    /**
     * CR-X: minimal byte-walk parser that extracts the
     * {@code android:windowBackground} (falling back to
     * {@code android:colorBackground}) attribute value from a style entry
     * in a parsed {@code resources.arsc} byte array. Walks the parent
     * chain. Resolves a single level of TYPE_REFERENCE (to follow
     * {@code @color/foo} → {@code TYPE_INT_COLOR_*}). Returns 0 on any
     * failure.
     *
     * <p>This is functionally equivalent to
     * {@code ResourceTable.getStyleAttrs(themeResId).get(0x01010054)} + a
     * value-map probe, but written without
     * {@link java.nio.ByteBuffer}/{@link java.nio.ByteOrder} because both
     * native classes are gappily-implemented on dalvik-on-OHOS arm32
     * (verified: {@code ByteOrder.isLittleEndian:()Z} throws
     * UnsatisfiedLinkError at first reference).
     *
     * <p>arsc format reference: AOSP
     * {@code frameworks/base/libs/androidfw/include/androidfw/ResourceTypes.h}.
     */
    private static int walkArscForThemeBg(byte[] data, int themeResId) {
        if (data == null || data.length < 12) return 0;
        try {
            // ResTable_header: type(2), headerSize(2), size(4), packageCount(4)
            int tableType = u16w(data, 0);
            if (tableType != 0x0002) return 0;
            int tableHeaderSize = u16w(data, 2);
            int p = tableHeaderSize;
            int targetPkgId = (themeResId >>> 24) & 0xFF;
            while (p + 8 <= data.length) {
                int chunkType = u16w(data, p);
                int chunkSize = u32w(data, p + 4);
                if (chunkSize < 8 || p + chunkSize > data.length) break;
                if (chunkType == 0x0200 // RES_TABLE_PACKAGE_TYPE
                        && p + 12 <= data.length
                        && u32w(data, p + 8) == targetPkgId) {
                    int color = walkPkgForThemeBg(data, p, chunkSize, themeResId, /*depth*/0);
                    if (color != 0) return color;
                }
                p += chunkSize;
            }
        } catch (Throwable t) {
            System.out.println("[Window] walkArscForThemeBg threw: " + t);
        }
        return 0;
    }

    private static int walkPkgForThemeBg(byte[] data, int pkgStart, int pkgSize,
            int themeResId, int depth) {
        if (depth > 16) return 0; // parent-chain cycle guard
        int targetTypeId = (themeResId >>> 16) & 0xFF;
        int targetEntryIdx = themeResId & 0xFFFF;
        int pkgHdr = u16w(data, pkgStart + 2);
        if (pkgHdr < 8 + 4 + 256 + 16) return 0;
        int p = pkgStart + pkgHdr;
        int pkgEnd = pkgStart + pkgSize;
        int parentRefOut = 0;
        // First pass: find the matching ResTable_type for this typeId and
        // pull our entry's bag pairs.
        while (p + 8 <= pkgEnd) {
            int cType = u16w(data, p);
            int cHdr = u16w(data, p + 2);
            int cSize = u32w(data, p + 4);
            if (cSize < 8 || p + cSize > pkgEnd) break;
            if (cType == 0x0201 && cHdr >= 20 && (data[p + 8] & 0xFF) == targetTypeId) {
                // ResTable_type: id(1), res0(1), res1(2), entryCount(4),
                //                entriesStart(4), ResTable_config(variable)
                int entryCount = u32w(data, p + 12);
                int entriesStart = u32w(data, p + 16);
                if (targetEntryIdx >= 0 && targetEntryIdx < entryCount) {
                    int eOffOff = p + cHdr + targetEntryIdx * 4;
                    if (eOffOff + 4 <= p + cSize) {
                        int eOff = u32w(data, eOffOff);
                        if (eOff != 0xFFFFFFFF) {
                            int entryHdrPos = p + entriesStart + eOff;
                            if (entryHdrPos + 8 <= data.length) {
                                int entrySize = u16w(data, entryHdrPos);
                                int entryFlags = u16w(data, entryHdrPos + 2);
                                if ((entryFlags & 0x0001) != 0 // FLAG_COMPLEX
                                        && entryHdrPos + entrySize + 8 <= data.length) {
                                    // ResTable_map_entry: size + parent(4) + count(4) + maps.
                                    // Note `size` is the entry header size (8 bytes for plain,
                                    // 16 for map_entry — but the next 8 bytes after the plain
                                    // header are PARENT + COUNT).
                                    int bagStart = entryHdrPos + 8; // after plain header
                                    int parentRef = u32w(data, bagStart);
                                    int bagCount = u32w(data, bagStart + 4);
                                    int mapStart = bagStart + 8;
                                    parentRefOut = parentRef;
                                    if (bagCount > 0 && bagCount < 200) {
                                        int color = scanBagForBg(data, mapStart, bagCount);
                                        if (color != 0) return color;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            p += cSize;
        }
        // Not found locally — try parent style.
        if (parentRefOut != 0 && parentRefOut != themeResId) {
            int parentPkgId = (parentRefOut >>> 24) & 0xFF;
            if (parentPkgId == ((themeResId >>> 24) & 0xFF)) {
                return walkPkgForThemeBg(data, pkgStart, pkgSize,
                        parentRefOut, depth + 1);
            }
            // Cross-package parent (e.g. inherit from android.R) — bail
            // gracefully. The framework style chain rarely defines
            // windowBackground at the leaf level for AppCompat themes; the
            // app's own theme almost always sets it, which we'll have
            // already returned.
        }
        return 0;
    }

    /**
     * Walks {@code bagCount} ResTable_map entries starting at {@code mapStart}.
     * Each entry is 12 bytes: name(4) + Res_value(8) = size(2) res0(1)
     * dataType(1) data(4). Returns the first color value seen for
     * windowBackground or colorBackground, or 0.
     */
    private static int scanBagForBg(byte[] data, int mapStart, int bagCount) {
        int found = 0;
        int foundIsBackup = 0; // 1 if colorBackground, 0 if windowBackground
        for (int i = 0; i < bagCount; i++) {
            int entryOff = mapStart + i * 12;
            if (entryOff + 12 > data.length) break;
            int name = u32w(data, entryOff);
            int valType = data[entryOff + 4 + 3] & 0xFF; // skip size(2),res0(1)
            int valData = u32w(data, entryOff + 4 + 4);
            if (name != ATTR_WINDOW_BACKGROUND && name != ATTR_COLOR_BACKGROUND) {
                continue;
            }
            int color = 0;
            if (valType == 0x1C /* TYPE_INT_COLOR_ARGB8 */) {
                color = valData;
            } else if (valType == 0x1D /* TYPE_INT_COLOR_RGB8 */) {
                color = 0xFF000000 | valData;
            } else if (valType == 0x1E /* TYPE_INT_COLOR_ARGB4 */) {
                int a = (valData >>> 12) & 0xF, r = (valData >>> 8) & 0xF,
                    g = (valData >>> 4) & 0xF, b = valData & 0xF;
                color = (a * 0x11 << 24) | (r * 0x11 << 16)
                        | (g * 0x11 << 8) | (b * 0x11);
            } else if (valType == 0x1F /* TYPE_INT_COLOR_RGB4 */) {
                int r = (valData >>> 8) & 0xF, g = (valData >>> 4) & 0xF,
                    b = valData & 0xF;
                color = 0xFF000000 | (r * 0x11 << 16)
                        | (g * 0x11 << 8) | (b * 0x11);
            } else if (valType == 0x01 /* TYPE_REFERENCE */) {
                // Single-level resolve into the arsc — find the entry
                // pointed at by valData and read its INT_COLOR* value.
                color = resolveColorReference(valData);
            }
            if (color == 0) continue;
            if (name == ATTR_WINDOW_BACKGROUND) {
                System.out.println("[Window] bag windowBackground type=0x"
                        + Integer.toHexString(valType) + " data=0x"
                        + Integer.toHexString(valData) + " -> 0x"
                        + Integer.toHexString(color));
                return color;
            }
            if (name == ATTR_COLOR_BACKGROUND && found == 0) {
                found = color;
                foundIsBackup = 1;
                System.out.println("[Window] bag colorBackground type=0x"
                        + Integer.toHexString(valType) + " data=0x"
                        + Integer.toHexString(valData) + " -> 0x"
                        + Integer.toHexString(color));
            }
        }
        return found;
    }

    /**
     * One-level reference resolution against the cached arsc bytes for
     * the current apk. Looks up {@code refId} in the same way the bag
     * walker does — finds its ResTable_type chunk, reads the plain entry,
     * and returns the {@code TYPE_INT_COLOR_*} value (or 0 if not a color).
     *
     * <p>The resolver uses the apk path threaded down via a thread-local
     * — set by {@link #resolveThemeBackgroundColor()} before the bag walk
     * runs — to find the right arsc bytes without re-reading the file.
     */
    private static int resolveColorReference(int refId) {
        if (refId == 0) return 0;
        byte[] arscBytes = sCurrentArsc.get();
        if (arscBytes == null) return 0;
        try {
            int targetPkgId = (refId >>> 24) & 0xFF;
            int targetTypeId = (refId >>> 16) & 0xFF;
            int targetEntryIdx = refId & 0xFFFF;
            int tableHdr = u16w(arscBytes, 2);
            int p = tableHdr;
            while (p + 8 <= arscBytes.length) {
                int chunkType = u16w(arscBytes, p);
                int chunkSize = u32w(arscBytes, p + 4);
                if (chunkSize < 8 || p + chunkSize > arscBytes.length) break;
                if (chunkType == 0x0200
                        && p + 12 <= arscBytes.length
                        && u32w(arscBytes, p + 8) == targetPkgId) {
                    int color = scanPkgForColorEntry(arscBytes, p, chunkSize,
                            targetTypeId, targetEntryIdx);
                    if (color != 0) return color;
                }
                p += chunkSize;
            }
        } catch (Throwable t) {
            System.out.println("[Window] resolveColorReference threw: " + t);
        }
        return 0;
    }

    private static int scanPkgForColorEntry(byte[] data, int pkgStart, int pkgSize,
            int targetTypeId, int targetEntryIdx) {
        int pkgHdr = u16w(data, pkgStart + 2);
        if (pkgHdr < 8 + 4 + 256 + 16) return 0;
        int p = pkgStart + pkgHdr;
        int pkgEnd = pkgStart + pkgSize;
        while (p + 8 <= pkgEnd) {
            int cType = u16w(data, p);
            int cHdr = u16w(data, p + 2);
            int cSize = u32w(data, p + 4);
            if (cSize < 8 || p + cSize > pkgEnd) break;
            if (cType == 0x0201 && cHdr >= 20 && (data[p + 8] & 0xFF) == targetTypeId) {
                int entryCount = u32w(data, p + 12);
                int entriesStart = u32w(data, p + 16);
                if (targetEntryIdx >= 0 && targetEntryIdx < entryCount) {
                    int eOff = u32w(data, p + cHdr + targetEntryIdx * 4);
                    if (eOff != 0xFFFFFFFF) {
                        int entryHdrPos = p + entriesStart + eOff;
                        if (entryHdrPos + 16 <= data.length) {
                            int entryFlags = u16w(data, entryHdrPos + 2);
                            if ((entryFlags & 0x0001) == 0) {
                                // Plain entry: size(2) flags(2) key(4)
                                // then Res_value: size(2) res0(1) type(1) data(4)
                                int valOff = entryHdrPos + 8;
                                int valType = data[valOff + 3] & 0xFF;
                                int valData = u32w(data, valOff + 4);
                                if (valType == 0x1C) return valData;
                                if (valType == 0x1D) return 0xFF000000 | valData;
                                if (valType == 0x1E) {
                                    int a = (valData >>> 12) & 0xF, r = (valData >>> 8) & 0xF,
                                        g = (valData >>> 4) & 0xF, b = valData & 0xF;
                                    return (a * 0x11 << 24) | (r * 0x11 << 16)
                                            | (g * 0x11 << 8) | (b * 0x11);
                                }
                                if (valType == 0x1F) {
                                    int r = (valData >>> 8) & 0xF, g = (valData >>> 4) & 0xF,
                                        b = valData & 0xF;
                                    return 0xFF000000 | (r * 0x11 << 16)
                                            | (g * 0x11 << 8) | (b * 0x11);
                                }
                                // TYPE_REFERENCE chain: would need another
                                // hop. Bail — most theme→drawable refs land
                                // at a color resource in one hop. If we
                                // need deeper resolution, extend with a
                                // depth counter.
                            }
                        }
                    }
                }
            }
            p += cSize;
        }
        return 0;
    }

    /** Per-thread cache of the arsc bytes the current resolve is using —
     *  threaded so {@link #resolveColorReference} doesn't need to receive
     *  it down the call chain. Cleared at the end of the resolve. */
    private static final ThreadLocal<byte[]> sCurrentArsc = new ThreadLocal<>();

    private static int u32w(byte[] b, int o) {
        return (b[o] & 0xFF) | ((b[o + 1] & 0xFF) << 8)
                | ((b[o + 2] & 0xFF) << 16) | ((b[o + 3] & 0xFF) << 24);
    }

    private static int u16w(byte[] b, int o) {
        return (b[o] & 0xFF) | ((b[o + 1] & 0xFF) << 8);
    }

    /**
     * CR-X: read the apk's AndroidManifest.xml — or any other entry —
     * either directly from disk (when the harness extracted it as a
     * sibling file via {@code -Dwestlake.apk.manifest=<path>} or
     * {@code <apk>.manifest}), or from the apk zip itself.
     *
     * <p>dalvik-on-OHOS rk3568's {@link java.util.zip.Inflater} ref-leaks
     * uncontrollably during DEFLATE (verified: 32M+ JNI local refs in
     * seconds, never terminates). Both raw {@code Inflater.inflate} and
     * {@code InflaterInputStream.read} hang. So we cannot live-inflate
     * AndroidManifest.xml on the board. STORED entries (like
     * resources.arsc) work fine via the existing
     * {@link LayoutInflater#readZipEntry} approach — those are handled by
     * the rest of the resolveThemeBackgroundColor pipeline. For
     * AndroidManifest.xml (always DEFLATE in modern apks) we rely on the
     * harness to pre-extract it to a known path.
     *
     * <p>Lookup order (generic, no per-app branches):
     * <ol>
     *   <li>{@code -Dwestlake.apk.manifest=<path>} system property (set
     *       by the test driver after {@code unzip -p <apk> AndroidManifest.xml}).</li>
     *   <li>{@code <apkPath>.manifest} sibling file (same convention).</li>
     *   <li>{@code <apkDir>/AndroidManifest.xml} extracted-tree fallback.</li>
     *   <li>Direct zip extraction via {@link java.util.zip.ZipFile}, which
     *       may also fail on this dalvik but is worth trying as a
     *       last resort for entries that aren't pre-extracted.</li>
     * </ol>
     */
    private static byte[] readApkEntryBytes(String apkPath, String entryName) {
        if ("AndroidManifest.xml".equals(entryName)) {
            // Tier 1: explicit -Dwestlake.apk.manifest=<path>.
            try {
                String mfPath = System.getProperty("westlake.apk.manifest");
                if (mfPath != null && !mfPath.isEmpty()) {
                    byte[] data = readFileFully(mfPath);
                    if (data != null) {
                        System.out.println("[Window] manifest from -Dwestlake.apk.manifest="
                                + mfPath + " (" + data.length + " B)");
                        return data;
                    }
                }
            } catch (Throwable ignored) {}
            // Tier 2: <apkPath>.manifest sibling.
            try {
                byte[] data = readFileFully(apkPath + ".manifest");
                if (data != null) {
                    System.out.println("[Window] manifest from sibling " + apkPath
                            + ".manifest (" + data.length + " B)");
                    return data;
                }
            } catch (Throwable ignored) {}
            // Tier 3: extracted-tree fallback.
            try {
                int slash = apkPath.lastIndexOf('/');
                String dir = slash >= 0 ? apkPath.substring(0, slash) : ".";
                byte[] data = readFileFully(dir + "/AndroidManifest.xml");
                if (data != null) {
                    System.out.println("[Window] manifest from dir " + dir
                            + "/AndroidManifest.xml (" + data.length + " B)");
                    return data;
                }
            } catch (Throwable ignored) {}
        }
        // Tier 4 (fallback for STORED entries / non-manifest paths): try ZipFile.
        // For the manifest this will almost certainly fail on this dalvik but
        // doesn't hang the way Inflater does — it returns/throws cleanly.
        java.util.zip.ZipFile zip = null;
        java.io.InputStream is = null;
        try {
            zip = new java.util.zip.ZipFile(apkPath);
            java.util.zip.ZipEntry e = zip.getEntry(entryName);
            if (e == null) return null;
            // Only attempt the read if it's STORED (method 0). Reading
            // DEFLATE here would trigger the same hang we're avoiding above.
            if (e.getMethod() != 0) return null;
            is = zip.getInputStream(e);
            long sz = e.getSize();
            java.io.ByteArrayOutputStream baos = sz > 0 && sz < (1 << 24)
                    ? new java.io.ByteArrayOutputStream((int) sz)
                    : new java.io.ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) > 0) baos.write(buf, 0, n);
            return baos.toByteArray();
        } catch (Throwable t) {
            return null;
        } finally {
            if (is != null) try { is.close(); } catch (Throwable ignored) {}
            if (zip != null) try { zip.close(); } catch (Throwable ignored) {}
        }
    }

    /**
     * CR-X: STORED-only LFH zip entry walker. Used for resources.arsc
     * (always STORED in modern apks — aapt2's
     * {@code -0 resources.arsc} default) since our dalvik can't run the
     * Inflater path. Mirrors the same approach as
     * {@link android.view.LayoutInflater#readZipEntry} but local to Window
     * to avoid a cross-shim dependency cycle.
     */
    private static byte[] readStoredZipEntry(String apkPath, String entryName) {
        java.io.FileInputStream fis = null;
        try {
            long sz = new java.io.File(apkPath).length();
            if (sz <= 0) return null;
            fis = new java.io.FileInputStream(apkPath);
            java.io.ByteArrayOutputStream baos =
                    new java.io.ByteArrayOutputStream((int) Math.min(sz, 65536));
            byte[] buf = new byte[65536];
            int n;
            while ((n = fis.read(buf)) > 0) {
                baos.write(buf, 0, n);
            }
            byte[] all = baos.toByteArray();
            if (all.length < 30) return null;
            int pos = 0;
            while (pos + 30 <= all.length) {
                int sig = readU32_(all, pos);
                if (sig != 0x04034b50) {
                    return null;
                }
                int method = readU16_(all, pos + 8);
                int compSize = readU32_(all, pos + 18);
                int uncompSize = readU32_(all, pos + 22);
                int nameLen = readU16_(all, pos + 26);
                int extraLen = readU16_(all, pos + 28);
                int nameStart = pos + 30;
                if (nameStart + nameLen > all.length) return null;
                String name;
                try {
                    name = new String(all, nameStart, nameLen, "UTF-8");
                } catch (Throwable t) {
                    name = "";
                }
                int dataStart = nameStart + nameLen + extraLen;
                if (entryName.equals(name)) {
                    if (method != 0) return null; // STORED only
                    if (dataStart + uncompSize > all.length) return null;
                    byte[] data = new byte[uncompSize];
                    System.arraycopy(all, dataStart, data, 0, uncompSize);
                    return data;
                }
                pos = dataStart + compSize;
            }
        } catch (Throwable t) {
            return null;
        } finally {
            if (fis != null) try { fis.close(); } catch (Throwable ignored) {}
        }
        return null;
    }

    private static int readU32_(byte[] b, int o) {
        return (b[o] & 0xFF) | ((b[o + 1] & 0xFF) << 8)
                | ((b[o + 2] & 0xFF) << 16) | ((b[o + 3] & 0xFF) << 24);
    }

    private static int readU16_(byte[] b, int o) {
        return (b[o] & 0xFF) | ((b[o + 1] & 0xFF) << 8);
    }

    private static byte[] readFileFully(String path) {
        if (path == null || path.isEmpty()) return null;
        java.io.File f = new java.io.File(path);
        if (!f.isFile()) return null;
        long sz = f.length();
        if (sz <= 0 || sz > 4 * 1024 * 1024) return null;
        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(f);
            byte[] data = new byte[(int) sz];
            int off = 0;
            while (off < data.length) {
                int got = fis.read(data, off, data.length - off);
                if (got < 0) break;
                off += got;
            }
            if (off != data.length) return null;
            return data;
        } catch (Throwable t) {
            return null;
        } finally {
            if (fis != null) try { fis.close(); } catch (Throwable ignored) {}
        }
    }

    public View peekDecorView() { return mDecorView; }

    public LayoutInflater getLayoutInflater() {
        return new LayoutInflater(mContext);
    }

    public <T extends View> T findViewById(int id) {
        if (mDecorView != null) {
            return mDecorView.findViewById(id);
        }
        return null;
    }

    public Object findViewById_legacy(int id) {
        return findViewById(id);
    }

    public void setTheme(int resid) { /* no-op: no chrome */ }

    public void setTitle(CharSequence title) { mTitle = title; }
    public CharSequence getTitle() { return mTitle; }
    public void setTitleColor(int color) { mTitleColor = color; }

    public void setCallback(Callback callback) { mCallback = callback; }
    public void setCallback(Object callback) { /* legacy/erased overload */ }
    public final Callback getCallback() { return mCallback; }

    // requestFeature historically returned boolean in AOSP; some shim
    // callers expect that, so keep boolean.
    public boolean requestFeature(int featureId) {
        mFeatures |= (1 << featureId);
        return true;
    }
    public final void setFeatureInt(int featureId, int value) { /* no-op */ }
    public final void setFeatureDrawableResource(int featureId, int resId) { /* no-op */ }
    public final void setFeatureDrawable(int featureId, Drawable drawable) { /* no-op */ }
    public void setFeatureDrawableAlpha(int featureId, int alpha) { /* no-op */ }
    public void setFeatureDrawableUri(int featureId, Uri uri) { /* no-op */ }
    public boolean hasFeature(int featureId) { return (mFeatures & (1 << featureId)) != 0; }
    public int getFeatures() { return mFeatures; }
    public int getLocalFeatures() { return 0; }
    public int getForcedWindowFlags() { return 0; }
    public static int getDefaultFeatures(Context context) { return 0; }

    public void invalidatePanelMenu(int featureId) { /* no-op */ }

    public void addFlags(int flags) { mFlags |= flags; }
    public void clearFlags(int flags) { mFlags &= ~flags; }
    public void setFlags(int flags, int mask) { mFlags = (mFlags & ~mask) | (flags & mask); }

    public void setStatusBarColor(int color) { mStatusBarColor = color; }
    public int getStatusBarColor() { return mStatusBarColor; }
    public void setNavigationBarColor(int color) { mNavigationBarColor = color; }
    public int getNavigationBarColor() { return mNavigationBarColor; }
    public void setNavigationBarDividerColor(int color) { /* no-op */ }
    public int getNavigationBarDividerColor() { return 0; }
    public void setStatusBarContrastEnforced(boolean ensureContrast) { /* no-op */ }
    public boolean isStatusBarContrastEnforced() { return false; }
    public void setNavigationBarContrastEnforced(boolean ensureContrast) { /* no-op */ }
    public boolean isNavigationBarContrastEnforced() { return false; }

    public WindowManager getWindowManager() {
        // Lazy WindowManagerImpl per Window — V2-Step5 stub; addView is a no-op
        // until M6 surface daemon lands.
        return new WindowManagerImpl(mContext);
    }
    public TypedArray getWindowStyle() { return null; }

    public void setWindowManager(WindowManager wm, IBinder appToken, String appName) { /* no-op */ }
    public void setWindowManager(WindowManager wm, IBinder appToken, String appName, boolean hardwareAccelerated) { /* no-op */ }

    public boolean hasChildren() {
        return mDecorView instanceof ViewGroup
                && ((ViewGroup) mDecorView).getChildCount() > 0;
    }
    public boolean isActive() { return false; }
    public boolean isFloating() { return false; }
    public boolean isWideColorGamut() { return false; }
    public boolean hasSoftInputMode() { return false; }
    public boolean isShortcutKey(int keyCode, KeyEvent event) { return false; }

    public Window getContainer() { return null; }
    public void setContainer(Window container) { /* no-op */ }

    public void makeActive() { /* no-op */ }
    public void onActive() { /* no-op */ }
    public void onConfigurationChanged(Configuration newConfig) { /* no-op */ }

    public void closeAllPanels() { /* no-op */ }
    public void closePanel(int featureId) { /* no-op */ }
    public void openPanel(int featureId, KeyEvent event) { /* no-op */ }
    public void togglePanel(int featureId, KeyEvent event) { /* no-op */ }

    public boolean performContextMenuIdentifierAction(int id, int flags) { return false; }
    public boolean performPanelIdentifierAction(int featureId, int id, int flags) { return false; }
    public boolean performPanelShortcut(int featureId, int keyCode, KeyEvent event, int flags) { return false; }

    public boolean superDispatchKeyEvent(KeyEvent event) { return false; }
    public boolean superDispatchKeyShortcutEvent(KeyEvent event) { return false; }
    public boolean superDispatchTouchEvent(MotionEvent event) { return false; }
    public boolean superDispatchTrackballEvent(MotionEvent event) { return false; }
    public boolean superDispatchGenericMotionEvent(MotionEvent event) { return false; }

    public void takeInputQueue(Object callback) { /* no-op */ }
    public void takeKeyEvents(boolean get) { /* no-op */ }
    public void takeSurface(Object callback) { /* no-op */ }

    public void injectInputEvent(InputEvent event) { /* no-op */ }

    public void setBackgroundDrawable(Drawable drawable) { /* no-op */ }
    public void setBackgroundDrawableResource(int resId) { /* no-op */ }
    public void setChildDrawable(int featureId, Drawable drawable) { /* no-op */ }
    public void setChildInt(int featureId, int value) { /* no-op */ }
    public void setClipToOutline(boolean clipToOutline) { /* no-op */ }
    public void setColorMode(int colorMode) { /* no-op */ }
    public int getColorMode() { return 0; }
    public void setDecorCaptionShade(int decorCaptionShade) { /* no-op */ }
    public void setDecorFitsSystemWindows(boolean decorFitsSystemWindows) { /* no-op */ }
    public void setDefaultWindowFormat(int format) { /* no-op */ }
    public void setDimAmount(float amount) { /* no-op */ }
    public void setElevation(float elevation) { /* no-op */ }
    public void setFormat(int format) { /* no-op */ }
    public void setGravity(int gravity) { /* no-op */ }
    public void setIcon(int resId) { /* no-op */ }
    public void setLogo(int resId) { /* no-op */ }
    public void setLayout(int width, int height) { /* no-op */ }
    public void setLocalFocus(boolean hasFocus, boolean inTouchMode) { /* no-op */ }
    public void setMediaController(MediaController controller) { /* no-op */ }
    public MediaController getMediaController() { return null; }
    public void setPreferMinimalPostProcessing(boolean isPreferred) { /* no-op */ }
    public void setResizingCaptionDrawable(Drawable drawable) { /* no-op */ }
    public void setRestrictedCaptionAreaListener(Object listener) { /* no-op */ }
    public void setSoftInputMode(int mode) { /* no-op */ }
    public void setSustainedPerformanceMode(boolean enable) { /* no-op */ }
    public void setSystemGestureExclusionRects(java.util.List<Object> rects) { /* no-op */ }
    public void setType(int type) { /* no-op */ }
    public void setUiOptions(int uiOptions) { /* no-op */ }
    public void setUiOptions(int uiOptions, int mask) { /* no-op */ }
    public void setVolumeControlStream(int streamType) { /* no-op */ }
    public int getVolumeControlStream() { return 0; }
    public void setWindowAnimations(int resId) { /* no-op */ }

    public void setAttributes(Object attrs) { /* no-op */ }
    public Object getAttributes() { return null; }

    public void setAllowEnterTransitionOverlap(boolean allow) { /* no-op */ }
    public boolean getAllowEnterTransitionOverlap() { return false; }
    public void setAllowReturnTransitionOverlap(boolean allow) { /* no-op */ }
    public boolean getAllowReturnTransitionOverlap() { return false; }

    public void setEnterTransition(Transition transition) { /* no-op */ }
    public Transition getEnterTransition() { return null; }
    public void setExitTransition(Transition transition) { /* no-op */ }
    public Transition getExitTransition() { return null; }
    public void setReenterTransition(Transition transition) { /* no-op */ }
    public Transition getReenterTransition() { return null; }
    public void setReturnTransition(Transition transition) { /* no-op */ }
    public Transition getReturnTransition() { return null; }
    public void setSharedElementEnterTransition(Transition transition) { /* no-op */ }
    public Transition getSharedElementEnterTransition() { return null; }
    public void setSharedElementExitTransition(Transition transition) { /* no-op */ }
    public Transition getSharedElementExitTransition() { return null; }
    public void setSharedElementReenterTransition(Transition transition) { /* no-op */ }
    public Transition getSharedElementReenterTransition() { return null; }
    public void setSharedElementReturnTransition(Transition transition) { /* no-op */ }
    public Transition getSharedElementReturnTransition() { return null; }
    public void setSharedElementsUseOverlay(boolean sharedElementsUseOverlay) { /* no-op */ }
    public boolean getSharedElementsUseOverlay() { return false; }
    public void setTransitionBackgroundFadeDuration(long fadeDurationMillis) { /* no-op */ }
    public long getTransitionBackgroundFadeDuration() { return 0L; }
    public void setTransitionManager(TransitionManager tm) { /* no-op */ }
    public TransitionManager getTransitionManager() { return null; }
    public Scene getContentScene() { return null; }

    public void addOnFrameMetricsAvailableListener(Object listener, Handler handler) { /* no-op */ }
    public void removeOnFrameMetricsAvailableListener(Object listener) { /* no-op */ }

    public void restoreHierarchyState(Bundle savedInstanceState) { /* no-op */ }
    public Bundle saveHierarchyState() { return null; }
}
