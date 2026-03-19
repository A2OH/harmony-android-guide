package android.text.method;

import java.util.Locale;

/**
 * Stub for android.text.method.WordIterator.
 * Iterates over word boundaries in text.
 */
public class WordIterator {

    public WordIterator() {}
    public WordIterator(Locale locale) {}

    public void setCharSequence(CharSequence text, int start, int end) {}

    public int preceding(int offset) { return java.text.BreakIterator.DONE; }
    public int following(int offset) { return java.text.BreakIterator.DONE; }
    public boolean isBoundary(int offset) { return false; }

    public int getBeginning(int offset) { return offset; }
    public int getEnd(int offset) { return offset; }

    public int prevBoundary(int offset) { return Math.max(0, offset - 1); }
    public int nextBoundary(int offset) { return offset + 1; }

    public boolean isOnPunctuation(int offset) { return false; }
    public boolean isAfterPunctuation(int offset) { return false; }

    public int getPunctuationBeginning(int offset) { return offset; }
    public int getPunctuationEnd(int offset) { return offset; }

    public int getPrevWordBeginningOnTwoWordsBoundary(int offset) { return offset; }
    public int getNextWordEndOnTwoWordBoundary(int offset) { return offset; }
}
