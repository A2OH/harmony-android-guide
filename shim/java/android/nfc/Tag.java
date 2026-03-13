package android.nfc;

/**
 * Android-compatible Tag stub.
 */
public class Tag {
    public byte[] getId() { return new byte[0]; }

    public String[] getTechList() { return new String[0]; }

    @Override
    public String toString() {
        return "Tag{}";
    }
}
