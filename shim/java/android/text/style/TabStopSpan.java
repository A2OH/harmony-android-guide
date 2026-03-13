package android.text.style;

/**
 * Android shim: TabStopSpan
 * Interface for paragraph spans that define a single tab stop position.
 */
public interface TabStopSpan {

    /**
     * Returns the offset of the tab stop from the leading margin of the line,
     * in pixels.
     */
    int getTabStop();

    /**
     * Default implementation of {@link TabStopSpan} that stores a single
     * tab stop position.
     */
    class Standard implements TabStopSpan {

        private final int mTabStop;

        /**
         * Creates a Standard tab stop at the given position.
         *
         * @param tabStop offset from the leading margin in pixels
         */
        public Standard(int tabStop) {
            mTabStop = tabStop;
        }

        @Override
        public int getTabStop() {
            return mTabStop;
        }
    }
}
