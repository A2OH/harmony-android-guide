package android.view;

import com.ohos.shim.bridge.OHBridge;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shim: android.view.View → ArkUI native node.
 *
 * Each View holds a native ArkUI node handle (nativeHandle). When the app
 * calls setVisibility(), setBackgroundColor(), etc., the shim immediately
 * pushes the change to the underlying ArkUI node via OHBridge JNI calls.
 *
 * Subclasses (TextView, Button, EditText, etc.) create specific node types
 * and expose type-specific attribute setters.
 */
public class View {
    public static final int VISIBLE = 0x00000000;
    public static final int INVISIBLE = 0x00000004;
    public static final int GONE = 0x00000008;

    // ArkUI attribute constants (must match oh_ffi::attr)
    static final int ATTR_WIDTH = 0;
    static final int ATTR_HEIGHT = 1;
    static final int ATTR_BACKGROUND_COLOR = 2;
    static final int ATTR_PADDING = 4;
    static final int ATTR_ID = 5;
    static final int ATTR_ENABLED = 6;
    static final int ATTR_MARGIN = 7;
    static final int ATTR_OPACITY = 16;
    static final int ATTR_VISIBILITY = 22;
    static final int ATTR_FOCUSABLE = 39;

    // ArkUI event constants (must match oh_ffi::event)
    static final int EVENT_ON_CLICK = 5;
    static final int EVENT_ON_FOCUS = 3;
    static final int EVENT_ON_BLUR = 4;

    // ArkUI node type for generic View (STACK — overlay container)
    public static final int NODE_TYPE_STACK = 8;

    // Global handle → View lookup for event dispatch
    private static final ConcurrentHashMap<Long, View> sHandleMap = new ConcurrentHashMap<>();

    // Monotonic event ID counter
    private static final AtomicInteger sNextEventId = new AtomicInteger(1);

    // The native ArkUI node handle (0 = not yet created)
    protected long nativeHandle;

    // View state
    private int id;
    private int visibility = VISIBLE;
    private int backgroundColor;
    private boolean enabled = true;
    private boolean focusable = false;
    private boolean clickable = false;
    private CharSequence contentDescription;
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;
    private OnTouchListener onTouchListener;
    private Object tag;
    private int paddingLeft, paddingTop, paddingRight, paddingBottom;
    private ViewGroup.LayoutParams layoutParams;

    // Event ID for click events on this view
    private int clickEventId;

    public View() {
        this(NODE_TYPE_STACK);
    }

    /** Subclasses call this with their specific ArkUI node type */
    protected View(int arkuiNodeType) {
        nativeHandle = OHBridge.nodeCreate(arkuiNodeType);
        if (nativeHandle != 0) {
            sHandleMap.put(nativeHandle, this);
        }
    }

    /** Look up a View by its native handle (used by event dispatch) */
    public static View findViewByHandle(long handle) {
        return sHandleMap.get(handle);
    }

    /** Get the native ArkUI node handle */
    public long getNativeHandle() {
        return nativeHandle;
    }

    // ── ID ──

    public int getId() { return id; }
    public void setId(int id) {
        this.id = id;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrString(nativeHandle, ATTR_ID, "view_" + id);
        }
    }

    // ── Visibility ──

    public int getVisibility() { return visibility; }
    public void setVisibility(int visibility) {
        this.visibility = visibility;
        if (nativeHandle != 0) {
            // ArkUI: 0=VISIBLE, 1=HIDDEN, 2=NONE (similar to GONE)
            int arkVis;
            switch (visibility) {
                case INVISIBLE: arkVis = 1; break;
                case GONE:      arkVis = 2; break;
                default:        arkVis = 0; break;
            }
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_VISIBILITY, arkVis);
        }
    }

    // ── Focus / Click / Content description ──

    public boolean isFocusable() { return focusable; }
    public void setFocusable(boolean focusable) { this.focusable = focusable; }

    public boolean isClickable() { return clickable; }
    public void setClickable(boolean clickable) { this.clickable = clickable; }

    public boolean isChecked() { return false; }

    public CharSequence getContentDescription() { return contentDescription; }
    public void setContentDescription(CharSequence desc) { this.contentDescription = desc; }

    // ── Background color ──

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        if (nativeHandle != 0) {
            // Android color is 0xAARRGGBB, ArkUI expects 0xAARRGGBB — same format
            OHBridge.nodeSetAttrColor(nativeHandle, ATTR_BACKGROUND_COLOR, color);
        }
    }

    // ── Padding ──

    public void setPadding(int left, int top, int right, int bottom) {
        this.paddingLeft = left;
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
        if (nativeHandle != 0) {
            // ArkUI padding: top, right, bottom, left (4 floats in vp)
            OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_PADDING,
                (float) top, (float) right, (float) bottom, (float) left, 4);
        }
    }

    public int getPaddingLeft() { return paddingLeft; }
    public int getPaddingTop() { return paddingTop; }
    public int getPaddingRight() { return paddingRight; }
    public int getPaddingBottom() { return paddingBottom; }

    // ── Enabled ──

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_ENABLED, enabled ? 1 : 0);
        }
    }
    public boolean isEnabled() { return enabled; }

    // ── Size ──

    public void setMinimumWidth(int minWidth) {
        // ArkUI doesn't have direct min-width on generic nodes — set width
    }
    public void setMinimumHeight(int minHeight) {}

    // ── Opacity / Alpha ──

    public void setAlpha(float alpha) {
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_OPACITY, alpha, 0, 0, 0, 1);
        }
    }

    // ── Tag ──

    public void setTag(Object tag) { this.tag = tag; }
    public Object getTag() { return tag; }

    // ── Focus ──

    public void requestFocus() {
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_FOCUSABLE, 1);
        }
    }
    public void clearFocus() {}
    public boolean hasFocus() { return false; }

    // ── Layout params ──

    public void setLayoutParams(ViewGroup.LayoutParams params) {
        this.layoutParams = params;
        applyLayoutParams();
    }

    public ViewGroup.LayoutParams getLayoutParams() { return layoutParams; }

    protected void applyLayoutParams() {
        if (nativeHandle == 0 || layoutParams == null) return;
        if (layoutParams.width > 0) {
            OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_WIDTH, (float) layoutParams.width, 0, 0, 0, 1);
        }
        if (layoutParams.height > 0) {
            OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_HEIGHT, (float) layoutParams.height, 0, 0, 0, 1);
        }
    }

    // ── Click listener ──

    public void setOnClickListener(OnClickListener l) {
        this.onClickListener = l;
        if (nativeHandle != 0 && l != null && clickEventId == 0) {
            clickEventId = sNextEventId.getAndIncrement();
            OHBridge.nodeRegisterEvent(nativeHandle, EVENT_ON_CLICK, clickEventId);
        }
    }

    public boolean performClick() {
        if (onClickListener != null) {
            onClickListener.onClick(this);
            return true;
        }
        return false;
    }

    public void setOnLongClickListener(OnLongClickListener l) {
        this.onLongClickListener = l;
    }

    public void setOnTouchListener(OnTouchListener l) {
        this.onTouchListener = l;
    }

    // ── Invalidate / layout ──

    public void invalidate() {
        if (nativeHandle != 0) {
            OHBridge.nodeMarkDirty(nativeHandle, 3); // NODE_NEED_RENDER
        }
    }

    public void requestLayout() {
        if (nativeHandle != 0) {
            OHBridge.nodeMarkDirty(nativeHandle, 1); // NODE_NEED_MEASURE
        }
    }

    // ── Native event handler (called from OHBridge.dispatchNodeEvent) ──

    public void onNativeEvent(int eventId, int eventKind, String stringData) {
        if (eventKind == EVENT_ON_CLICK) {
            performClick();
        }
    }

    // ── Cleanup ──

    public void destroy() {
        if (nativeHandle != 0) {
            sHandleMap.remove(nativeHandle);
            OHBridge.nodeDispose(nativeHandle);
            nativeHandle = 0;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    // ── Listener interfaces ──

    public interface OnClickListener {
        void onClick(View v);
    }

    public interface OnLongClickListener {
        boolean onLongClick(View v);
    }

    public interface OnTouchListener {
        boolean onTouch(View v, Object event);
    }
}
