package java.lang;
import java.util.stream.IntStream;
public interface CharSequence {
    int length();
    char charAt(int index);
    CharSequence subSequence(int start, int end);
    String toString();
    public default IntStream chars() { return null; }
    public default IntStream codePoints() { return null; }
}
