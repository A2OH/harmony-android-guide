package android.widget;

/** Stub for android.widget.SpellChecker. */
public class SpellChecker {

    public SpellChecker(TextView textView) {}

    public static boolean haveWordBoundariesChanged(
            CharSequence text, int start, int end, int spanStart, int spanEnd) {
        return false;
    }

    public void onSpellCheckSpanRemoved(android.text.style.SpellCheckSpan span) {}
    public void spellCheck(int start, int end) {}
    public void closeSession() {}
    public void resetSession() {}
    public void onSelectionChanged() {}
}
