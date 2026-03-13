package android.system;

import java.io.FileDescriptor;

/**
 * Android-compatible StructPollfd shim.
 * Represents one entry in the array passed to Os.poll().
 * Fields are mutable (matching Android's public field contract).
 */
public final class StructPollfd {

    /** The file descriptor to poll. */
    public FileDescriptor fd;

    /** Requested events bitmask (POLLIN, POLLOUT, etc. from OsConstants). */
    public short events;

    /** Returned events bitmask filled in by Os.poll(). */
    public short revents;

    /** No-arg constructor; caller sets fields directly. */
    public StructPollfd() {
    }

    @Override
    public String toString() {
        return "StructPollfd{fd=" + fd
                + ", events=" + events
                + ", revents=" + revents + "}";
    }
}
