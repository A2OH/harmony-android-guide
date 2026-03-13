package android.media;

import java.io.Closeable;
import java.io.IOException;

/**
 * Shim stub for android.media.MediaDataSource.
 * Abstract base class for media data sources used by MediaPlayer and MediaExtractor.
 */
public abstract class MediaDataSource implements Closeable {

    /**
     * Called to request data from the given position.
     *
     * @param position the starting position of the data to be read
     * @param buffer   the buffer into which the data should be placed
     * @param offset   the offset within buffer at which to place the data
     * @param size     the amount of data requested
     * @return the number of bytes read, or -1 on end-of-stream
     */
    public abstract int readAt(long position, byte[] buffer, int offset, int size)
            throws IOException;

    /**
     * @return the size of the data source, or -1 if the length cannot be determined
     */
    public abstract long getSize() throws IOException;

    @Override
    public void close() throws IOException {
        // stub — subclasses should override to release resources
    }
}
