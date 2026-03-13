package android.graphics.drawable;

/**
 * Android-compatible LevelListDrawable shim. Selects a child drawable based
 * on the current level set via {@link #setLevel(int)}.
 */
public class LevelListDrawable extends DrawableContainer {

    private final LevelListState mState;

    public LevelListDrawable() {
        mState = new LevelListState();
        setConstantState(mState);
    }

    /**
     * Adds a drawable that is active when the level is in [low, high].
     */
    public void addLevel(int low, int high, Drawable drawable) {
        int idx = mState.addChild(drawable);
        mState.addRange(idx, low, high);
    }

    @Override
    protected boolean onLevelChange(int level) {
        int index = mState.indexForLevel(level);
        return selectDrawable(index);
    }

    // ---------------------------------------------------------------

    private static class LevelListState extends DrawableContainerState {

        private final java.util.List<int[]> mRanges = new java.util.ArrayList<>();

        void addRange(int index, int low, int high) {
            // Ensure list is large enough (addChild may create gaps if called
            // concurrently, but in practice they are always sequential here).
            while (mRanges.size() <= index) mRanges.add(null);
            mRanges.set(index, new int[]{low, high});
        }

        int indexForLevel(int level) {
            for (int i = 0; i < mRanges.size(); i++) {
                int[] range = mRanges.get(i);
                if (range != null && level >= range[0] && level <= range[1]) {
                    return i;
                }
            }
            return -1;
        }
    }
}
