package android.app.assist;

import java.util.ArrayList;
import java.util.List;

/**
 * A2OH shim: android.app.assist.AssistStructure
 *
 * Parceled representation of the view hierarchy of the app that was on screen
 * when an assist action was triggered. Used by the Autofill framework and
 * voice-interaction services.
 *
 * This is a no-op stub: all getters return safe defaults and the tree is empty.
 */
public class AssistStructure {

    /**
     * Represents a single view node in the assist structure tree.
     */
    public static class ViewNode {

        private final List<ViewNode> mChildren = new ArrayList<>();
        private CharSequence mText;
        private String mClassName;

        /** @hide */
        ViewNode() {}

        /**
         * Returns the text content of this view, or {@code null} if none.
         */
        public CharSequence getText() {
            return mText;
        }

        /**
         * Returns the fully-qualified class name of the view (e.g.
         * {@code "android.widget.EditText"}), or {@code null} if unknown.
         */
        public String getClassName() {
            return mClassName;
        }

        /**
         * Returns the number of direct child nodes under this node.
         */
        public int getChildCount() {
            return mChildren.size();
        }

        /**
         * Returns the child node at the given index.
         *
         * @param index zero-based child index; must be &lt; {@link #getChildCount()}.
         * @return the child {@link ViewNode}
         * @throws IndexOutOfBoundsException if {@code index} is out of range
         */
        public ViewNode getChildAt(int index) {
            return mChildren.get(index);
        }
    }

    // ── Window list ──────────────────────────────────────────────────────────

    /**
     * Returns the number of windows described by this structure. The shim
     * always returns 0 because no real UI hierarchy is captured.
     */
    public int getWindowNodeCount() {
        return 0;
    }

    /**
     * Constructs a new, empty AssistStructure.
     *
     * On a real device this object is produced by the platform and delivered
     * to the assist/autofill service; apps never construct one directly.
     */
    public AssistStructure() {}
}
