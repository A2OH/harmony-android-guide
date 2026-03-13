package android.widget;

/**
 * Shim: android.widget.SectionIndexer — optional interface for adapters that expose
 * fast-scroll section headings (e.g. alphabetical jump-to letters).
 *
 * Adapters implement this alongside {@link ListAdapter} to provide index data to
 * {@link AbsListView} (e.g. ListView) so that the fast-scroll thumb can jump
 * directly to a lettered section.
 */
public interface SectionIndexer {

    /**
     * Returns an array of section objects. The sections form the basis of the
     * fast-scroll alphabet strip. Each element's {@code toString()} is used as
     * the label shown in the fast-scroll overlay.
     *
     * @return array of section labels (may be empty, never null)
     */
    Object[] getSections();

    /**
     * Returns the starting position of the given section within the adapter's
     * data set.
     *
     * @param sectionIndex  index into the array returned by {@link #getSections()}
     * @return the adapter position of the first item in the section
     */
    int getPositionForSection(int sectionIndex);

    /**
     * Returns the section index for the given adapter position.
     *
     * @param position  the adapter position to query
     * @return the section index that contains the given position
     */
    int getSectionForPosition(int position);
}
