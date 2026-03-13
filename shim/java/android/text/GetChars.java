package android.text;

/**
 * Android-compatible GetChars shim.
 * Interface for extracting characters from a CharSequence into a char array.
 */
public interface GetChars extends CharSequence {

    /**
     * Copies characters from this object into the destination array.
     *
     * @param start  start offset in this sequence
     * @param end    end offset in this sequence
     * @param dest   destination char array
     * @param destoff offset in the destination array
     */
    void getChars(int start, int end, char[] dest, int destoff);
}
