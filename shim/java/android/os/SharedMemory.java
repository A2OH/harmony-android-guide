package android.os;
import android.util.Size;
import android.util.Size;
import java.io.FileDescriptor;
import java.util.Map;
import java.util.Set;

import java.io.Closeable;
import java.nio.ByteBuffer;

/**
 * Shim: android.os.SharedMemory — stub for Android's shared-memory API.
 * All methods are no-ops returning sensible defaults.
 */
public class SharedMemory implements Closeable {

    private final String name;
    private final int size;
    private boolean closed;

    private SharedMemory(String name, int size) {
        this.name = name;
        this.size = size;
        this.closed = false;
    }

    /**
     * Create a new SharedMemory instance.
     * @param name debug name for the region (may be null)
     * @param size size of the shared memory region in bytes
     * @return a new SharedMemory instance
     */
    public static SharedMemory create(String name, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive, got " + size);
        }
        return new SharedMemory(name, size);
    }

    /**
     * Map the shared memory region into the calling process's address space.
     * Stub: returns a heap-allocated ByteBuffer of the appropriate size.
     * @param prot  protection flags (PROT_READ, PROT_WRITE, etc.)
     * @param offset offset within the shared memory region
     * @param length number of bytes to map
     * @return a ByteBuffer providing access to the shared memory
     */
    public ByteBuffer map(int prot, int offset, int length) {
        return ByteBuffer.allocate(length);
    }

    /**
     * Convenience overload: map the entire region as read-write.
     */
    public ByteBuffer mapReadWrite() {
        return ByteBuffer.allocate(size);
    }

    /**
     * Convenience overload: map the entire region as read-only.
     */
    public ByteBuffer mapReadOnly() {
        return ByteBuffer.allocate(size).asReadOnlyBuffer();
    }

    /**
     * Unmap a previously mapped ByteBuffer.
     * Stub: no-op (the heap buffer will be GC'd).
     */
    public static void unmap(ByteBuffer buffer) {
        // no-op in stub
    }

    /**
     * Return the size of the shared memory region.
     */
    public int getSize() {
        return size;
    }

    /**
     * Close the shared memory region.
     */
    @Override
    public void close() {
        closed = true;
    }

    /**
     * Set the protection on the shared memory to be read-only.
     * Stub: no-op.
     */
    public boolean setProtect(int prot) {
        return !closed;
    }

    /**
     * Return the file descriptor backing this SharedMemory object.
     * Stub: returns null (no real FD in pure-Java shim).
     */
    public java.io.FileDescriptor getFileDescriptor() {
        return null;
    }
}
