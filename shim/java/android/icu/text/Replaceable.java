package android.icu.text;

public interface Replaceable {
    int char32At(int p0);
    char charAt(int p0);
    void copy(int p0, int p1, int p2);
    void getChars(int p0, int p1, char[] p2, int p3);
    boolean hasMetaData();
    int length();
    void replace(int p0, int p1, String p2);
    void replace(int p0, int p1, char[] p2, int p3, int p4);
}
