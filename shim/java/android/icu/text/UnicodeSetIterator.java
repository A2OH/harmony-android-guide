package android.icu.text;

public class UnicodeSetIterator {
    public static final int IS_STRING = 0;
    public int codepoint = 0;
    public int codepointEnd = 0;
    public int string = 0;

    public UnicodeSetIterator(UnicodeSet p0) {}
    public UnicodeSetIterator() {}

    public String getString() { return null; }
    public boolean next() { return false; }
    public boolean nextRange() { return false; }
    public void reset(UnicodeSet p0) {}
    public void reset() {}
}
