package android.widget;

import android.view.Menu;
import android.view.SimpleMenu;
import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.Toolbar → ArkUI ROW node (horizontal action bar).
 *
 * Toolbar is a generalised action bar widget introduced in API 21. In the
 * shim it extends ViewGroup (rendered as a ROW node) and maintains the
 * title, subtitle, navigation icon and overflow menu as Java-side state.
 * Full visual rendering of these items requires wiring to ArkUI attribute
 * setters that will be added as the shim matures.
 */
public class Toolbar extends ViewGroup {

    // ROW node for horizontal layout
    static final int NODE_TYPE_ROW = 17;

    // Attribute constants (reusing the TEXT constants for title text)
    static final int ATTR_TEXT_CONTENT = 1000;
    static final int ATTR_IMAGE_SRC    = 4000;

    private CharSequence mTitle;
    private CharSequence mSubtitle;
    private int mNavigationIconRes;
    private View.OnClickListener mNavigationClickListener;
    private final Menu mMenu;

    // Internal TextView children for title / subtitle
    private final TextView mTitleView;
    private final TextView mSubtitleView;

    public Toolbar() {
        super(NODE_TYPE_ROW);
        mMenu = new SimpleMenu();
        mTitleView    = new TextView();
        mSubtitleView = new TextView();
        addView(mTitleView);
        addView(mSubtitleView);
    }

    /** Constructor accepting a context-like object (ignored in shim). */
    public Toolbar(Object context) {
        this();
    }

    /** Constructor accepting context and attribute set (both ignored). */
    public Toolbar(Object context, Object attrs) {
        this();
    }

    /** Constructor accepting context, attribute set and default style (all ignored). */
    public Toolbar(Object context, Object attrs, int defStyleAttr) {
        this();
    }

    // ── Title ──

    public void setTitle(CharSequence title) {
        mTitle = title;
        mTitleView.setText(title);
    }

    public void setTitle(int resId) {
        setTitle("@" + resId);
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    // ── Subtitle ──

    public void setSubtitle(CharSequence subtitle) {
        mSubtitle = subtitle;
        mSubtitleView.setText(subtitle);
    }

    public void setSubtitle(int resId) {
        setSubtitle("@" + resId);
    }

    public CharSequence getSubtitle() {
        return mSubtitle;
    }

    // ── Navigation icon ──

    /**
     * Set the navigation icon by drawable resource ID.
     * Actual icon display is stubbed — set a string attribute for now.
     */
    public void setNavigationIcon(int resId) {
        mNavigationIconRes = resId;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrString(nativeHandle, ATTR_IMAGE_SRC, "res://" + resId);
        }
    }

    /** Set the navigation icon from a drawable-like object (ignored in shim). */
    public void setNavigationIcon(Object drawable) {
        // Drawable support not yet implemented
    }

    /** Return the resource id of the current navigation icon (0 if none set). */
    public int getNavigationIconRes() {
        return mNavigationIconRes;
    }

    // ── Navigation click listener ──

    public void setNavigationOnClickListener(View.OnClickListener listener) {
        mNavigationClickListener = listener;
        // The navigation button is represented as a click on this Toolbar;
        // a dedicated nav-button View would normally receive the click.
        setOnClickListener(listener);
    }

    // ── Menu ──

    /**
     * Return the Menu containing the overflow items for this Toolbar.
     * Callers can call {@link Menu#add} to populate the menu programmatically.
     */
    public Menu getMenu() {
        return mMenu;
    }

    /**
     * Inflate a menu resource into this Toolbar's overflow menu.
     * Resource inflation is not supported in the shim; this is a no-op.
     *
     * @param resId the menu resource ID
     */
    public void inflateMenu(int resId) {
        // Resource-based menu inflation not yet implemented in the shim
    }
}
