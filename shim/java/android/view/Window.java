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
import java.util.List;

public class Window {
    public static final int DECOR_CAPTION_SHADE_AUTO = 0;
    public static final int DECOR_CAPTION_SHADE_DARK = 0;
    public static final int DECOR_CAPTION_SHADE_LIGHT = 0;
    public static final int FEATURE_OPTIONS_PANEL = 0;
    public static final int FEATURE_NO_TITLE = 1;
    public static final int FEATURE_CONTEXT_MENU = 6;
    public static final int FEATURE_CUSTOM_TITLE = 7;
    public static final int FEATURE_ACTION_BAR = 8;
    public static final int FEATURE_ACTION_BAR_OVERLAY = 9;
    public static final int FEATURE_ACTION_MODE_OVERLAY = 10;
    public static final int FEATURE_CONTENT_TRANSITIONS = 12;
    public static final int FEATURE_ACTIVITY_TRANSITIONS = 13;
    public static final int FEATURE_LEFT_ICON = 3;
    public static final int FEATURE_RIGHT_ICON = 4;
    public static final int ID_ANDROID_CONTENT = 0x01020002;
    public static final int NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME = 0;
    public static final int STATUS_BAR_BACKGROUND_TRANSITION_NAME = 0;

    private Context mContext;
    private View mDecorView;
    private View mContentView;
    private int mFeatures;
    private int mFlags;
    private Callback mCallback;

    public interface Callback {
        boolean dispatchKeyEvent(KeyEvent event);
        boolean dispatchTouchEvent(android.view.MotionEvent event);
        boolean dispatchTrackballEvent(android.view.MotionEvent event);
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
    }
    private CharSequence mTitle;
    private int mStatusBarColor;
    private int mNavigationBarColor;

    public Window(Context p0) {
        mContext = p0;
        // Create a default FrameLayout as the decor view
        mDecorView = new android.widget.FrameLayout(p0);
    }

    public void adoptContext(Context context) {
        if (context != null) {
            mContext = context;
            if (mDecorView != null && mDecorView.mContext == null) {
                mDecorView.mContext = context;
            }
            if (mContentView != null && mContentView.mContext == null) {
                mContentView.mContext = context;
            }
        }
    }

    public void addContentView(View p0, Object p1) {
        if (mDecorView instanceof ViewGroup) {
            ((ViewGroup) mDecorView).addView(p0);
        }
    }
    public void addFlags(int p0) { mFlags |= p0; }
    public void addOnFrameMetricsAvailableListener(Object p0, Handler p1) {}
    public void clearFlags(int p0) { mFlags &= ~p0; }
    public void closeAllPanels() {}
    public void closePanel(int p0) {}
    public View getDecorView() {
        final boolean strictStandalone = !com.westlake.engine.WestlakeLauncher.isRealFrameworkFallbackAllowed();
        if (strictStandalone && mDecorView == null) {
            if (mContext == null) {
                com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window getDecorView context null");
            }
            com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window getDecorView alloc call");
            Object rawDecor = com.westlake.engine.WestlakeLauncher.tryAllocInstance(android.widget.FrameLayout.class);
            com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window getDecorView alloc returned");
            if (rawDecor instanceof View) {
                mDecorView = (View) rawDecor;
                if (mContext != null && mDecorView.mContext == null) {
                    mDecorView.mContext = mContext;
                    com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window getDecorView context seeded");
                }
                com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window getDecorView alloc usable");
            } else {
                com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window getDecorView alloc unusable");
            }
        }
        return mDecorView;
    }

    public boolean installMinimalStandaloneContent() {
        final boolean strictStandalone = !com.westlake.engine.WestlakeLauncher.isRealFrameworkFallbackAllowed();
        if (!strictStandalone) {
            return false;
        }
        android.util.Log.i("Window", "strict installMinimalStandaloneContent begin");
        com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent begin");
        View decor = getDecorView();
        com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent decor returned");
        if (!(decor instanceof ViewGroup)) {
            android.util.Log.i("Window", "strict installMinimalStandaloneContent decor unusable");
            com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent decor unusable");
            return false;
        }
        int existingChildren = ((ViewGroup) decor).getChildCount();
        android.util.Log.i("Window", "strict installMinimalStandaloneContent existingChildren="
                + existingChildren);
        if (existingChildren > 0) {
            com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent already positive");
            return true;
        }
        if (mContext == null) {
            android.util.Log.i("Window", "strict installMinimalStandaloneContent context null");
            com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent context null");
            return false;
        }
        View content = null;
        com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent alloc call");
        Object rawContent = com.westlake.engine.WestlakeLauncher.tryAllocInstance(android.widget.FrameLayout.class);
        com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent alloc returned");
        if (rawContent instanceof View) {
            content = (View) rawContent;
            if (content.mContext == null) {
                content.mContext = mContext;
                com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent context seeded");
            }
            com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent alloc usable");
        } else {
            com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent alloc unusable");
        }
        if (content == null) {
            return false;
        }
        com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent set call");
        setContentView(content, null);
        com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent set returned");
        View updatedDecor = getDecorView();
        com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent verify returned");
        int newChildren = updatedDecor instanceof ViewGroup
                ? ((ViewGroup) updatedDecor).getChildCount()
                : -1;
        android.util.Log.i("Window", "strict installMinimalStandaloneContent newChildren="
                + newChildren);
        if (newChildren > 0) {
            com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent verify positive");
            return true;
        }
        com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window installContent verify nonpositive");
        return false;
    }
    public LayoutInflater getLayoutInflater() { return new LayoutInflater(mContext); }

    public <T extends View> T findViewById(int id) {
        if (mDecorView != null) {
            T found = mDecorView.findViewById(id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
    public Object findViewById_legacy(int p0) {
        return findViewById(p0);
    }
    public boolean getAllowEnterTransitionOverlap() { return false; }
    public boolean getAllowReturnTransitionOverlap() { return false; }
    public Object getAttributes() { return null; }
    public Callback getCallback() { return mCallback; }
    public int getColorMode() { return 0; }
    public Window getContainer() { return null; }
    public Scene getContentScene() { return null; }
    public Context getContext() { return mContext; }
    public static int getDefaultFeatures(Context p0) { return 0; }
    public Transition getEnterTransition() { return null; }
    public Transition getExitTransition() { return null; }
    public int getFeatures() { return mFeatures; }
    public int getForcedWindowFlags() { return 0; }
    public int getLocalFeatures() { return 0; }
    public MediaController getMediaController() { return null; }
    public Transition getReenterTransition() { return null; }
    public Transition getReturnTransition() { return null; }
    public Transition getSharedElementEnterTransition() { return null; }
    public Transition getSharedElementExitTransition() { return null; }
    public Transition getSharedElementReenterTransition() { return null; }
    public Transition getSharedElementReturnTransition() { return null; }
    public boolean getSharedElementsUseOverlay() { return false; }
    public long getTransitionBackgroundFadeDuration() { return 0L; }
    public TransitionManager getTransitionManager() { return null; }
    public int getVolumeControlStream() { return 0; }
    public WindowManager getWindowManager() { return null; }
    public TypedArray getWindowStyle() { return null; }
    public boolean hasChildren() { return false; }
    public boolean hasFeature(int p0) { return (mFeatures & (1 << p0)) != 0; }
    public boolean hasSoftInputMode() { return false; }
    public void injectInputEvent(InputEvent p0) {}
    public void invalidatePanelMenu(int p0) {}
    public boolean isActive() { return false; }
    public boolean isFloating() { return false; }
    public boolean isNavigationBarContrastEnforced() { return false; }
    public boolean isShortcutKey(int p0, KeyEvent p1) { return false; }
    public boolean isStatusBarContrastEnforced() { return false; }
    public boolean isWideColorGamut() { return false; }
    public void makeActive() {}
    public void onActive() {}
    public void onConfigurationChanged(Configuration p0) {}
    public void openPanel(int p0, KeyEvent p1) {}
    public View peekDecorView() { return mDecorView; }
    public boolean performContextMenuIdentifierAction(int p0, int p1) { return false; }
    public boolean performPanelIdentifierAction(int p0, int p1, int p2) { return false; }
    public boolean performPanelShortcut(int p0, int p1, KeyEvent p2, int p3) { return false; }
    public void removeOnFrameMetricsAvailableListener(Object p0) {}
    public boolean requestFeature(int p0) { mFeatures |= (1 << p0); return true; }
    public void restoreHierarchyState(Bundle p0) {}
    public Bundle saveHierarchyState() { return null; }
    public void setAllowEnterTransitionOverlap(boolean p0) {}
    public void setAllowReturnTransitionOverlap(boolean p0) {}
    public void setAttributes(Object p0) {}
    public void setBackgroundDrawable(Drawable p0) {}
    public void setBackgroundDrawableResource(int p0) {}
    public void setCallback(Callback p0) { mCallback = p0; }
    public void setCallback(Object p0) { /* legacy */ }
    public void setChildDrawable(int p0, Drawable p1) {}
    public void setChildInt(int p0, int p1) {}
    public void setClipToOutline(boolean p0) {}
    public void setColorMode(int p0) {}
    public void setContainer(Window p0) {}
    public void setContentView(int layoutResID) {
        try {
            android.util.Log.i("Window", "setContentView(resId=0x"
                    + Integer.toHexString(layoutResID) + ")");
            LayoutInflater inflater = new LayoutInflater(mContext);
            if (inflater == null) {
                android.util.Log.e("Window", "LayoutInflater is null!");
                return;
            }
            android.widget.FrameLayout tempRoot = new android.widget.FrameLayout(mContext);
            android.util.Log.i("Window", "tempRoot created: " + tempRoot.getClass().getSimpleName());
            View inflated = null;
            try {
                inflated = inflater.inflate(layoutResID, tempRoot, true);
                android.util.Log.i("Window", "inflate with tempRoot returned");
            } catch (Throwable t) {
                android.util.Log.w("Window", "inflate with tempRoot failed: "
                        + t.getClass().getName() + ": " + t.getMessage());
                try {
                    inflated = inflater.inflate(layoutResID, null);
                    android.util.Log.i("Window", "inflate without root returned");
                } catch (Throwable t2) {
                    android.util.Log.w("Window", "inflate without root failed: "
                            + t2.getClass().getName() + ": " + t2.getMessage());
                }
            }
            android.util.Log.i("Window", "inflate result: "
                    + (inflated != null ? inflated.getClass().getSimpleName() : "null")
                    + " children=" + tempRoot.getChildCount());
            if (tempRoot.getChildCount() > 0) {
                View content = tempRoot.getChildAt(0);
                tempRoot.removeAllViews();
                setContentView(content);
            } else if (inflated != null && inflated != tempRoot) {
                setContentView(inflated);
            } else if (inflated == null) {
                android.util.Log.w("Window", "setContentView left decor empty for layout 0x"
                        + Integer.toHexString(layoutResID));
            }
        } catch (Throwable outer) {
            android.util.Log.e("Window", "setContentView outer failure for 0x"
                    + Integer.toHexString(layoutResID) + ": "
                    + outer.getClass().getName() + ": " + outer.getMessage());
        }
    }
    public void setContentView(View p0) {
        setContentView(p0, null);
    }
    public void setContentView(View p0, Object p1) {
        mContentView = p0;
        if (p0 == null) {
            return;
        }
        final boolean strictStandalone = !com.westlake.engine.WestlakeLauncher.isRealFrameworkFallbackAllowed();
        if (mContext != null && p0.mContext == null) {
            p0.mContext = mContext;
        }
        if (mDecorView != null && mContext != null && mDecorView.mContext == null) {
            mDecorView.mContext = mContext;
        }
        if (strictStandalone) {
            com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window setContentView entry");
            try {
                if (mDecorView instanceof ViewGroup && mDecorView != p0) {
                    com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window setContentView childInstall call");
                    ((ViewGroup) mDecorView).installStandaloneChild(p0);
                    com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window setContentView childInstall returned");
                } else {
                    com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window setContentView replace decor call");
                    mDecorView = p0;
                    com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window setContentView replace decor returned");
                }
            } catch (Throwable ignored) {
                com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window setContentView childInstall threw");
                mDecorView = p0;
                com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window setContentView fallback direct");
            }
            try {
                if (mContext instanceof android.app.Activity) {
                    mDecorView.setTag(mContext);
                    ((android.app.Activity) mContext).invalidateLayout();
                }
                com.westlake.engine.WestlakeLauncher.marker(
                        "PF301 strict Window setContentView invalidate skipped");
                int childCount = mDecorView instanceof ViewGroup
                        ? ((ViewGroup) mDecorView).getChildCount()
                        : -1;
            } catch (Throwable ignored) {
                com.westlake.engine.WestlakeLauncher.marker(
                        "PF301 strict Window setContentView invalidate threw");
            }
            com.westlake.engine.WestlakeLauncher.marker("PF301 strict Window setContentView standalone end");
            return;
        }
        try {
            if (mDecorView instanceof ViewGroup && mDecorView != p0) {
                ViewGroup decor = (ViewGroup) mDecorView;
                decor.removeAllViews();
                decor.addView(p0);
            } else {
                mDecorView = p0;
            }
        } catch (Throwable ignored) {
            // The standalone shim still trips layout-param and parent wiring faults when
            // wrapping arbitrary app views in the fake decor container. Fall back to using
            // the content root directly as the decor view.
            mDecorView = p0;
        }
        // Tag decor with the owning Activity so View.invalidate() can trigger renderFrame()
        if (mContext instanceof android.app.Activity) {
            mDecorView.setTag(mContext);
            try {
                ((android.app.Activity) mContext).invalidateLayout();
            } catch (Throwable ignored) {
            }
        }
        ensureMcdToolbarShell();
    }
    public void setDecorCaptionShade(int p0) {}
    public void setDecorFitsSystemWindows(boolean p0) {}
    public void setDefaultWindowFormat(int p0) {}
    public void setDimAmount(float p0) {}
    public void setElevation(float p0) {}
    public void setEnterTransition(Transition p0) {}
    public void setExitTransition(Transition p0) {}
    public void setFeatureDrawable(int p0, Drawable p1) {}
    public void setFeatureDrawableAlpha(int p0, int p1) {}
    public void setFeatureDrawableResource(int p0, int p1) {}
    public void setFeatureDrawableUri(int p0, Uri p1) {}
    public void setFeatureInt(int p0, int p1) {}
    public void setFlags(int p0, int p1) { mFlags = (mFlags & ~p1) | (p0 & p1); }
    public void setFormat(int p0) {}
    public void setGravity(int p0) {}
    public void setIcon(int p0) {}
    public void setLayout(int p0, int p1) {}
    public void setLocalFocus(boolean p0, boolean p1) {}

    private int resolveResourceId(String type, String name) {
        if (mContext == null || type == null || name == null) {
            return 0;
        }
        try {
            android.content.res.Resources res = mContext.getResources();
            if (res == null) {
                return 0;
            }
            String[] packages = {
                    mContext.getPackageName(),
                    "com.mcdonalds.app",
                    "com.mcdonalds.homedashboard"
            };
            for (int i = 0; i < packages.length; i++) {
                String pkg = packages[i];
                if (pkg == null || pkg.isEmpty()) {
                    continue;
                }
                int id = res.getIdentifier(name, type, pkg);
                if (id != 0) {
                    return id;
                }
            }
        } catch (Throwable ignored) {
        }
        return 0;
    }

    private boolean matchesLayoutResource(int layoutResId, String name) {
        if (layoutResId == 0 || name == null) {
            return false;
        }
        int resolved = resolveResourceId("layout", name);
        return resolved != 0 && resolved == layoutResId;
    }

    private boolean hasId(View root, int id) {
        return root != null && id != 0 && root.findViewById(id) != null;
    }

    private boolean isStructuredPageLayout(int layoutResId) {
        return matchesLayoutResource(layoutResId, "activity_base")
                || matchesLayoutResource(layoutResId, "base_layout");
    }

    private boolean hasStructuredPageShell(View root) {
        if (root == null) {
            return false;
        }
        int pageRootId = resolveResourceId("id", "page_root");
        int contentViewId = resolveResourceId("id", "content_view");
        int pageContentId = resolveResourceId("id", "page_content");
        int pageContentHolderId = resolveResourceId("id", "page_content_holder");
        int toolbarId = resolveResourceId("id", "toolbar");
        return hasId(root, pageRootId)
                && hasId(root, contentViewId)
                && hasId(root, pageContentId)
                && hasId(root, pageContentHolderId)
                && hasId(root, toolbarId);
    }

    private boolean isStructuredShellLookupId(int id) {
        if (id == 0) {
            return false;
        }
        String[] names = {
                "page_root",
                "content_view",
                "page_content",
                "page_content_holder",
                "toolbar",
                "mcdBackNavigationButton",
                "basket_layout",
                "toolbarRightButton",
                "toolbarSearchIcon",
                "basket_price",
                "basket_error",
                "toolbarTitleText",
                "toolbarCenterImageIcon",
                "back",
                "close",
                "toolbar_title"
        };
        for (int i = 0; i < names.length; i++) {
            if (resolveResourceId("id", names[i]) == id) {
                return true;
            }
        }
        return false;
    }

    private int dp(int value) {
        try {
            float density = mContext != null
                    ? mContext.getResources().getDisplayMetrics().density
                    : 1.0f;
            return Math.max(1, (int) (value * density + 0.5f));
        } catch (Throwable ignored) {
            return Math.max(1, value);
        }
    }

    private void ensureToolbarAliasViews(View toolbarView) {
        if (!(toolbarView instanceof ViewGroup) || mContext == null) {
            return;
        }
        try {
            ViewGroup toolbar = (ViewGroup) toolbarView;
            int backId = resolveResourceId("id", "back");
            int closeId = resolveResourceId("id", "close");
            int titleId = resolveResourceId("id", "toolbar_title");
            int centerIconId = resolveResourceId("id", "toolbarCenterImageIcon");

            if (backId != 0 && toolbar.findViewById(backId) == null) {
                android.widget.ImageView back = new android.widget.ImageView(mContext);
                back.setId(backId);
                back.setVisibility(View.GONE);
                if (toolbar instanceof android.widget.RelativeLayout) {
                    android.widget.RelativeLayout.LayoutParams lp =
                            new android.widget.RelativeLayout.LayoutParams(dp(40), dp(40));
                    lp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_START);
                    lp.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
                    lp.leftMargin = dp(8);
                    toolbar.addView(back, lp);
                } else {
                    toolbar.addView(back);
                }
            }

            if (closeId != 0 && toolbar.findViewById(closeId) == null) {
                android.widget.ImageView close = new android.widget.ImageView(mContext);
                close.setId(closeId);
                close.setVisibility(View.GONE);
                if (toolbar instanceof android.widget.RelativeLayout) {
                    android.widget.RelativeLayout.LayoutParams lp =
                            new android.widget.RelativeLayout.LayoutParams(dp(40), dp(40));
                    lp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_END);
                    lp.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
                    lp.rightMargin = dp(8);
                    toolbar.addView(close, lp);
                } else {
                    toolbar.addView(close);
                }
            }

            if (titleId != 0 && toolbar.findViewById(titleId) == null) {
                com.mcdonalds.mcduikit.widget.McDTextView title =
                        new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
                title.setId(titleId);
                title.setTextColor(0xFFFFFFFF);
                title.setVisibility(View.GONE);
                if (toolbar instanceof android.widget.RelativeLayout) {
                    android.widget.RelativeLayout.LayoutParams lp =
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT);
                    toolbar.addView(title, lp);
                } else {
                    toolbar.addView(title);
                }
            }

            if (centerIconId != 0 && toolbar.findViewById(centerIconId) == null) {
                android.widget.ImageView centerIcon = new android.widget.ImageView(mContext);
                centerIcon.setId(centerIconId);
                centerIcon.setVisibility(View.GONE);
                if (toolbar instanceof android.widget.RelativeLayout) {
                    android.widget.RelativeLayout.LayoutParams lp =
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT);
                    toolbar.addView(centerIcon, lp);
                } else {
                    toolbar.addView(centerIcon);
                }
            }
        } catch (Throwable t) {
            android.util.Log.w("Window", "ensureToolbarAliasViews failed: "
                    + t.getClass().getName() + ": " + t.getMessage());
        }
    }

    private View buildStructuredPageShell() {
        if (mContext == null) {
            return null;
        }

        int pageRootId = resolveResourceId("id", "page_root");
        int contentViewId = resolveResourceId("id", "content_view");
        int pageContentId = resolveResourceId("id", "page_content");
        int pageContentHolderId = resolveResourceId("id", "page_content_holder");
        int toolbarId = resolveResourceId("id", "toolbar");

        android.widget.LinearLayout pageRoot = new android.widget.LinearLayout(mContext);
        pageRoot.setOrientation(android.widget.LinearLayout.VERTICAL);
        if (pageRootId != 0) {
            pageRoot.setId(pageRootId);
        }
        pageRoot.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        com.mcdonalds.mcduikit.widget.McDToolBarView toolbar =
                new com.mcdonalds.mcduikit.widget.McDToolBarView(mContext);
        if (toolbarId != 0) {
            toolbar.setId(toolbarId);
        }
        pageRoot.addView(toolbar, new android.widget.LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        android.widget.FrameLayout contentView = new android.widget.FrameLayout(mContext);
        if (contentViewId != 0) {
            contentView.setId(contentViewId);
        }
        pageRoot.addView(contentView, new android.widget.LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1.0f));

        android.widget.LinearLayout pageContent = new android.widget.LinearLayout(mContext);
        pageContent.setOrientation(android.widget.LinearLayout.VERTICAL);
        if (pageContentId != 0) {
            pageContent.setId(pageContentId);
        }
        contentView.addView(pageContent, new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        android.widget.FrameLayout pageContentHolder = new android.widget.FrameLayout(mContext);
        if (pageContentHolderId != 0) {
            pageContentHolder.setId(pageContentHolderId);
        }
        pageContent.addView(pageContentHolder, new android.widget.LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        return pageRoot;
    }

    private void maybeInstallStructuredPageShellForLookup(int id) {
        if (!isStructuredShellLookupId(id) || mContext == null) {
            return;
        }
        try {
            if (!hasStructuredPageShell(mDecorView)) {
                View shell = buildStructuredPageShell();
                if (shell != null) {
                    setContentView(shell, null);
                    android.util.Log.i("Window", "Installed structured page shell via findViewById for 0x"
                            + Integer.toHexString(id));
                }
            }
            int toolbarId = resolveResourceId("id", "toolbar");
            ensureToolbarAliasViews(toolbarId != 0 ? mDecorView.findViewById(toolbarId) : null);
        } catch (Throwable t) {
            android.util.Log.w("Window", "maybeInstallStructuredPageShellForLookup failed: "
                    + t.getClass().getName() + ": " + t.getMessage());
        }
    }

    private void ensureStructuredPageShell(int layoutResId) {
        if (!isStructuredPageLayout(layoutResId) || mContext == null) {
            return;
        }
        try {
            if (!hasStructuredPageShell(mDecorView)) {
                View shell = buildStructuredPageShell();
                if (shell != null) {
                    setContentView(shell, null);
                    android.util.Log.i("Window", "Installed structured page shell for 0x"
                            + Integer.toHexString(layoutResId));
                }
            }
            int toolbarId = resolveResourceId("id", "toolbar");
            ensureToolbarAliasViews(toolbarId != 0 ? mDecorView.findViewById(toolbarId) : null);
        } catch (Throwable t) {
            android.util.Log.w("Window", "ensureStructuredPageShell failed: "
                    + t.getClass().getName() + ": " + t.getMessage());
        }
    }

    private void ensureMcdToolbarShell() {
        if (mDecorView == null || mContext == null) {
            return;
        }
        try {
            int toolbarId = resolveResourceId("id", "toolbar");
            int basketLayoutId = resolveResourceId("id", "basket_layout");
            int pageRootId = resolveResourceId("id", "page_root");
            if (toolbarId == 0 || basketLayoutId == 0) {
                return;
            }

            View toolbarView = mDecorView.findViewById(toolbarId);
            if (toolbarView instanceof com.mcdonalds.mcduikit.widget.McDToolBarView
                    && toolbarView.findViewById(basketLayoutId) != null) {
                return;
            }

            ViewGroup parent = null;
            ViewGroup.LayoutParams replacementLp = null;
            int index = 0;
            if (toolbarView != null) {
                ViewParent rawParent = toolbarView.getParent();
                if (rawParent instanceof ViewGroup) {
                    parent = (ViewGroup) rawParent;
                    replacementLp = toolbarView.getLayoutParams();
                    index = parent.indexOfChild(toolbarView);
                    parent.removeView(toolbarView);
                }
            } else {
                View pageRoot = pageRootId != 0 ? mDecorView.findViewById(pageRootId) : null;
                if (pageRoot != null) {
                    ViewParent rawParent = pageRoot.getParent();
                    if (rawParent instanceof ViewGroup) {
                        parent = (ViewGroup) rawParent;
                        index = parent.indexOfChild(pageRoot);
                    }
                }
            }
            if (parent == null) {
                return;
            }

            com.mcdonalds.mcduikit.widget.McDToolBarView replacement =
                    new com.mcdonalds.mcduikit.widget.McDToolBarView(mContext);
            replacement.setId(toolbarId);
            if (replacementLp == null) {
                replacementLp = new android.widget.LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            parent.addView(replacement, index, replacementLp);
            android.util.Log.i("Window", "ensureMcdToolbarShell installed toolbar="
                    + replacement.getClass().getSimpleName() + " basket="
                    + (replacement.findViewById(basketLayoutId) != null));
        } catch (Throwable t) {
            android.util.Log.w("Window", "ensureMcdToolbarShell failed: "
                    + t.getClass().getName() + ": " + t.getMessage());
        }
    }
    public void setLogo(int p0) {}
    public void setMediaController(MediaController p0) {}
    public void setNavigationBarColor(int p0) { mNavigationBarColor = p0; }
    public void setNavigationBarContrastEnforced(boolean p0) {}
    public void setNavigationBarDividerColor(int p0) {}
    public void setPreferMinimalPostProcessing(boolean p0) {}
    public void setReenterTransition(Transition p0) {}
    public void setResizingCaptionDrawable(Drawable p0) {}
    public void setRestrictedCaptionAreaListener(Object p0) {}
    public void setReturnTransition(Transition p0) {}
    public void setSharedElementEnterTransition(Transition p0) {}
    public void setSharedElementExitTransition(Transition p0) {}
    public void setSharedElementReenterTransition(Transition p0) {}
    public void setSharedElementReturnTransition(Transition p0) {}
    public void setSharedElementsUseOverlay(boolean p0) {}
    public void setSoftInputMode(int p0) {}
    public void setStatusBarColor(int p0) { mStatusBarColor = p0; }
    public void setStatusBarContrastEnforced(boolean p0) {}
    public void setSustainedPerformanceMode(boolean p0) {}
    public void setSystemGestureExclusionRects(java.util.List<Object> p0) {}
    public void setTitle(CharSequence p0) { mTitle = p0; }
    public void setTransitionBackgroundFadeDuration(long p0) {}
    public void setTransitionManager(TransitionManager p0) {}
    public void setType(int p0) {}
    public void setUiOptions(int p0) {}
    public void setUiOptions(int p0, int p1) {}
    public void setVolumeControlStream(int p0) {}
    public void setWindowAnimations(int p0) {}
    public void setWindowManager(WindowManager p0, IBinder p1, String p2) {}
    public void setWindowManager(WindowManager p0, IBinder p1, String p2, boolean p3) {}
    public boolean superDispatchGenericMotionEvent(MotionEvent p0) { return false; }
    public boolean superDispatchKeyEvent(KeyEvent p0) { return false; }
    public boolean superDispatchKeyShortcutEvent(KeyEvent p0) { return false; }
    public boolean superDispatchTouchEvent(MotionEvent p0) { return false; }
    public boolean superDispatchTrackballEvent(MotionEvent p0) { return false; }
    public void takeInputQueue(Object p0) {}
    public void takeKeyEvents(boolean p0) {}
    public void takeSurface(Object p0) {}
    public void togglePanel(int p0, KeyEvent p1) {}

    /** Auto-generated stub. */
    public static interface OnContentApplyWindowInsetsListener {
        android.util.Pair<android.graphics.Insets, WindowInsets> onContentApplyWindowInsets(View view, WindowInsets insets);
    }

    /** Auto-generated stub. */
    public static interface OnFrameMetricsAvailableListener {}
}
