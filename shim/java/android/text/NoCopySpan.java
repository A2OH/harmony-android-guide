package android.text;

/**
 * Android-compatible NoCopySpan shim.
 * Marker interface for spans that should not be copied when performing
 * text operations like substring or replace.
 */
public interface NoCopySpan {

    /**
     * Concrete implementation of NoCopySpan for convenience.
     */
    public class Concrete implements NoCopySpan {
    }
}
