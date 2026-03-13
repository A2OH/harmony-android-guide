package android.os;

import java.io.*;

/**
 * Android-compatible FileUtils shim. Pure Java stream copy utilities.
 */
public class FileUtils {

    public interface ProgressListener {
        void onProgress(long progress);
    }

    private static final int BUFFER_SIZE = 4096;

    public static long copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        long total = 0;
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
            total += len;
        }
        return total;
    }

    public static long copy(InputStream in, OutputStream out,
                            CancellationSignal signal,
                            java.util.concurrent.Executor executor,
                            ProgressListener listener) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        long total = 0;
        int len;
        while ((len = in.read(buf)) != -1) {
            if (signal != null && signal.isCanceled()) {
                throw new IOException("Cancelled");
            }
            out.write(buf, 0, len);
            total += len;
            if (listener != null) {
                final long progress = total;
                if (executor != null) {
                    executor.execute(() -> listener.onProgress(progress));
                } else {
                    listener.onProgress(progress);
                }
            }
        }
        return total;
    }

    public static long copy(FileDescriptor in, FileDescriptor out) throws IOException {
        return copy(new java.io.FileInputStream(in), new java.io.FileOutputStream(out));
    }

    public static long copy(FileDescriptor in, FileDescriptor out,
                            CancellationSignal signal,
                            java.util.concurrent.Executor executor,
                            ProgressListener listener) throws IOException {
        return copy(new java.io.FileInputStream(in), new java.io.FileOutputStream(out), signal, executor, listener);
    }
}
