package android.content.res;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Android-compatible AssetFileDescriptor shim. Stub implementation
 * with getFileDescriptor(), getStartOffset(), getLength(), and close().
 */
public class AssetFileDescriptor implements Closeable {

    public static final long UNKNOWN_LENGTH = -1;

    private final FileDescriptor mFd;
    private final long mStartOffset;
    private final long mLength;

    public AssetFileDescriptor(FileDescriptor fd, long startOffset, long length) {
        mFd = fd;
        mStartOffset = startOffset;
        mLength = length;
    }

    /**
     * Returns the FileDescriptor for this asset.
     */
    public FileDescriptor getFileDescriptor() {
        return mFd;
    }

    /**
     * Returns the byte offset within the file where this asset's data begins.
     */
    public long getStartOffset() {
        return mStartOffset;
    }

    /**
     * Returns the total number of bytes of this asset's data,
     * or UNKNOWN_LENGTH if not known.
     */
    public long getLength() {
        return mLength;
    }

    /**
     * Returns the total number of bytes of this asset's data,
     * or UNKNOWN_LENGTH if not known.
     */
    public long getDeclaredLength() {
        return mLength;
    }

    /**
     * Create a FileInputStream for reading this asset. Stub -- returns null.
     */
    public FileInputStream createInputStream() throws IOException {
        return null; // stub
    }

    @Override
    public void close() throws IOException {
        // stub -- no-op
    }

    @Override
    public String toString() {
        return "AssetFileDescriptor{startOffset=" + mStartOffset
                + ", length=" + mLength + "}";
    }
}
