package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.widget.RelativeLayout → ARKUI_NODE_FLEX (flex container, relative positioning).
 *
 * ArkUI does not have a true RelativeLayout; we map to a STACK/FLEX node and
 * accept layout rules as metadata only (positioning is approximate in the shim).
 */
public class RelativeLayout extends ViewGroup {

    // ArkUI FLEX node type (same value used by FrameLayout's STACK, we use STACK here)
    // STACK = 8 (from View.NODE_TYPE_STACK); RelativeLayout defaults to stack semantics
    static final int NODE_TYPE_FLEX = 8; // STACK used as closest approximation

    // RelativeLayout rule verbs
    public static final int LEFT_OF             =  0;
    public static final int RIGHT_OF            =  1;
    public static final int ABOVE               =  2;
    public static final int BELOW               =  3;
    public static final int ALIGN_LEFT          =  5;
    public static final int ALIGN_TOP           =  6;
    public static final int ALIGN_RIGHT         =  7;
    public static final int ALIGN_BOTTOM        =  8;
    public static final int ALIGN_PARENT_LEFT   =  9;
    public static final int ALIGN_PARENT_TOP    = 10;
    public static final int ALIGN_PARENT_RIGHT  = 11;
    public static final int ALIGN_PARENT_BOTTOM = 12;
    public static final int CENTER_IN_PARENT    = 13;
    public static final int CENTER_HORIZONTAL   = 14;
    public static final int CENTER_VERTICAL     = 15;

    public static final int TRUE = -1; // used with ALIGN_PARENT_* as anchor

    public RelativeLayout() {
        super(NODE_TYPE_FLEX);
    }

    // ── LayoutParams ──

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        // Parallel arrays: rule verb → subject (0 = no subject / TRUE sentinel)
        private final int[] rules    = new int[16];
        private final boolean[] ruleSet = new boolean[16];

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        /** Anchor this view relative to parent (e.g., ALIGN_PARENT_LEFT, CENTER_IN_PARENT). */
        public void addRule(int verb) {
            addRule(verb, TRUE);
        }

        /** Anchor this view relative to another view identified by {@code subject} (view id). */
        public void addRule(int verb, int subject) {
            if (verb >= 0 && verb < rules.length) {
                rules[verb] = subject;
                ruleSet[verb] = true;
            }
        }

        public void removeRule(int verb) {
            if (verb >= 0 && verb < rules.length) {
                rules[verb] = 0;
                ruleSet[verb] = false;
            }
        }

        public int[] getRules() {
            return rules.clone();
        }

        public boolean hasRule(int verb) {
            return verb >= 0 && verb < ruleSet.length && ruleSet[verb];
        }
    }
}
