package android.text.style;

/**
 * Stub for android.text.style.SuggestionRangeSpan.
 * Used to highlight the range of text being replaced by a suggestion.
 */
public class SuggestionRangeSpan extends CharacterStyle {
    private int mBackgroundColor;

    public SuggestionRangeSpan() {}

    public void setBackgroundColor(int color) { mBackgroundColor = color; }
    public int getBackgroundColor() { return mBackgroundColor; }

    public void updateDrawState(android.text.TextPaint tp) {}
}
