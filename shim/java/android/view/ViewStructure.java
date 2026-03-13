package android.view;

/**
 * Android-compatible ViewStructure shim.
 * Abstract class used to build an autofill-compatible structure describing
 * the content of a view hierarchy. All methods are stubs.
 */
public abstract class ViewStructure {

    /** Set the class name of the node. */
    public abstract void setClassName(String className);

    /** Set the text content of the node. */
    public abstract void setText(CharSequence text);

    /** Set line ranges for the text content. */
    public abstract void setTextLines(int[] charOffsets, int[] baselines);

    /** Set the hint (placeholder) text. */
    public abstract void setHint(CharSequence hint);

    /** Set the content description. */
    public abstract void setContentDescription(CharSequence contentDescription);

    /** Set the visibility of the node (View.VISIBLE, INVISIBLE, GONE). */
    public abstract void setVisibility(int visibility);

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
    public abstract void setDimens(int left, int top, int scrollX, int scrollY,
                                   int width, int height);

    /** Set the view ID and related resource info for autofill. */
    public abstract void setId(int id, String packageName, String typeName, String entryName);

    /**
     * Set the autofill type of this view.
     * See View.AUTOFILL_TYPE_* constants.
     */
    public abstract void setAutofillType(int autofillType);

    /** Set the current autofill value. */
    public abstract void setAutofillValue(Object autofillValue);

    /** Set the autofill hints for this view. */
    public abstract void setAutofillHints(String[] hints);

    /**
     * Declare that this view has the given number of children.
     * Must be called before newChild() or asyncNewChild().
     *
     * @param num total number of children to be added
     */
    public abstract void addChildCount(int num);

    /**
     * Create and return a child ViewStructure at the given index.
     * The caller should populate the returned object.
     */
    public abstract ViewStructure newChild(int index);

    /**
     * Create and return a child ViewStructure at the given index
     * for asynchronous population.
     */
    public abstract ViewStructure asyncNewChild(int index);
}
