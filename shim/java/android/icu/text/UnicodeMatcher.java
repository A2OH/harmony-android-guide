package android.icu.text;

public interface UnicodeMatcher {
    void addMatchSetTo(UnicodeSet p0);
    int matches(Replaceable p0, int[] p1, int p2, boolean p3);
    boolean matchesIndexValue(int p0);
    String toPattern(boolean p0);
}
