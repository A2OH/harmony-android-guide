package android.view.textservice;

/**
 * Android-compatible SentenceSuggestionsInfo shim. Data holder — wraps per-word SuggestionsInfo.
 */
public class SentenceSuggestionsInfo {

    private final SuggestionsInfo[] suggestionsInfos;
    private final int[]             offsets;
    private final int[]             lengths;

    public SentenceSuggestionsInfo(SuggestionsInfo[] suggestionsInfos, int[] offsets, int[] lengths) {
        this.suggestionsInfos = suggestionsInfos != null ? suggestionsInfos : new SuggestionsInfo[0];
        this.offsets          = offsets          != null ? offsets          : new int[0];
        this.lengths          = lengths          != null ? lengths          : new int[0];
    }

    public int getSuggestionsCount() { return suggestionsInfos.length; }

    public SuggestionsInfo getSuggestionsInfoAt(int i) {
        if (i < 0 || i >= suggestionsInfos.length) return null;
        return suggestionsInfos[i];
    }

    public int getOffsetAt(int i) {
        if (i < 0 || i >= offsets.length) return -1;
        return offsets[i];
    }

    public int getLengthAt(int i) {
        if (i < 0 || i >= lengths.length) return -1;
        return lengths[i];
    }
}
