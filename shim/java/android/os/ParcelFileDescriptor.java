package android.os;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Android-compatible ParcelFileDescriptor shim. Wraps a FileDescriptor.
 */
public class ParcelFileDescriptor implements Closeable {

    public static final int MODE_READ_ONLY  = 0x10000000;
    public static final int MODE_WRITE_ONLY = 0x20000000;
    public static final int MODE_READ_WRITE = 0x30000000;
    public static final int MODE_CREATE     = 0x08000000;
    public static final int MODE_TRUNCATE   = 0x04000000;

    private FileDescriptor mFd;
    private boolean mClosed;

    protected ParcelFileDescriptor(FileDescriptor fd) {
        mFd = fd;
    }

    public static ParcelFileDescriptor open(File file, int mode) throws IOException {
        // Stub: return a detached descriptor
        FileDescriptor fd = new FileDescriptor();
        return new ParcelFileDescriptor(fd);
    }

    public static ParcelFileDescriptor[] createPipe() throws IOException {
        return new ParcelFileDescriptor[]{
            new ParcelFileDescriptor(new FileDescriptor()),
            new ParcelFileDescriptor(new FileDescriptor())
        };
    }

    public static ParcelFileDescriptor[] createSocketPair() throws IOException {
        return createPipe();
    }

    public int getFd() {
        return -1; // no native fd in shim
    }

    public int detachFd() {
        mClosed = true;
        return -1;
    }

    public FileDescriptor getFileDescriptor() {
        return mFd;
    }

    @Override
    public void close() throws IOException {
        mClosed = true;
    }

    public boolean isClosed() {
        return mClosed;
    }

    // ---- AutoCloseInputStream ----
    public static class AutoCloseInputStream extends java.io.FileInputStream {
        private final ParcelFileDescriptor mPfd;

        public AutoCloseInputStream(ParcelFileDescriptor pfd) {
            super(pfd.getFileDescriptor());
            mPfd = pfd;
        }

        @Override
        public void close() throws IOException {
            super.close();
            mPfd.close();
        }
    }

    // ---- AutoCloseOutputStream ----
    public static class AutoCloseOutputStream extends java.io.FileOutputStream {
        private final ParcelFileDescriptor mPfd;

        public AutoCloseOutputStream(ParcelFileDescriptor pfd) {
            super(pfd.getFileDescriptor());
            mPfd = pfd;
        }

        @Override
        public void close() throws IOException {
            super.close();
            mPfd.close();
        }
    }
}
