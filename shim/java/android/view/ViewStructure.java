package android.view;
import java.util.Set;

/**
 * Android-compatible ViewStructure shim.
 * Abstract class used to build an autofill-compatible structure describing
 * the content of a view hierarchy. All methods are stubs.
 */
public class ViewStructure {

    /** Set the class name of the node. */
    public void setClassName(String className) {}

    /** Set the text content of the node. */
    public void setText(CharSequence text) {}

    /** Set line ranges for the text content. */
    public void setTextLines(int[] charOffsets, int[] baselines) {}

    /** Set the hint (placeholder) text. */
    public void setHint(CharSequence hint) {}

    /** Set the content description. */
    public void setContentDescription(CharSequence contentDescription) {}

    /** Set the visibility of the node (View.VISIBLE, INVISIBLE, GONE). */
    public void setVisibility(int visibility) {}

    /**
     * Set the display dimensions of the node.
     *
     * @param left    left edge relative to parent
     * @param top     top edge relative to parent
     * @param scrollX current horizontal scroll offset
     * @param scrollY current vertical scroll offset
     * @param width   width in pixels
     * @param height  height in pixels
     */
    public void setDimens(int left, int top, int scrollX, int scrollY,
                                   int width, int height) {}

    /** Set the view ID and related resource info for autofill. */
    public void setId(int id, String packageName, String typeName, String entryName) {}

    /**
     * Set the autofill type of this view.
     * See View.AUTOFILL_TYPE_* constants.
     */
    public void setAutofillType(int autofillType) {}

    /** Set the current autofill value. */
    public void setAutofillValue(Object autofillValue) {}

    /** Set the autofill hints for this view. */
    public void setAutofillHints(String[] hints) {}

    /**
     * Declare that this view has the given number of children.
     * Must be called before newChild() or asyncNewChild().
     *
     * @param num total number of children to be added
     */
    public void addChildCount(int num) {}

    /**
     * Create and return a child ViewStructure at the given index.
     * The caller should populate the returned object.
     */
    public ViewStructure newChild(int index) { return null; }

    /**
     * Create and return a child ViewStructure at the given index
     * for asynchronous population.
     */
    public ViewStructure asyncNewChild(int index) { return null; }
}
