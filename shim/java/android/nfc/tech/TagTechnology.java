package android.nfc.tech;
import android.nfc.Tag;
import android.nfc.Tag;

import android.nfc.Tag;
import java.io.Closeable;
import java.io.IOException;

/**
 * Android-compatible TagTechnology stub interface.
 * All NFC tag technology classes implement this interface.
 */
public interface TagTechnology extends Closeable {
    /** Connect to the tag. Must be called before any I/O operations. */
    void connect() throws IOException;

    /** Close the connection to the tag. */
    void close() throws IOException;

    /** Returns true if the connection is currently open. */
    boolean isConnected();

    /** Returns the Tag object backing this technology instance. */
    Tag getTag();
}
