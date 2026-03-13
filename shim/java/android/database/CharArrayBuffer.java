package android.database;

/**
 * Android-compatible CharArrayBuffer shim.
 * Simple holder used by Cursor.copyStringToBuffer() to avoid allocations.
 */
public final class CharArrayBuffer {

    public char[] data;
    public int sizeCopied;

    public CharArrayBuffer(int size) {
        data = new char[size];
    }

    public CharArrayBuffer(char[] buf) {
        data = buf;
    }
}
