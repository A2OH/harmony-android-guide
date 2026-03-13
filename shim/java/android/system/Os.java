package android.system;
import android.os.Environment;
import android.os.Process;
import android.os.Environment;
import android.os.Process;
import java.io.File;
import java.net.Socket;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Android-compatible Os shim.
 * Provides static stubs mirroring android.system.Os.
 * Where a sensible Java implementation exists it is provided; otherwise a
 * stub throws ErrnoException(ENOSYS) or returns a neutral dummy value.
 *
 * NOTE: real Android Os wraps native syscalls; this shim targets host-JVM
 * testing inside the A2OH migration harness.
 */
public final class Os {
    private Os() {}

    // -------------------------------------------------------------------------
    // File descriptor operations
    // -------------------------------------------------------------------------

    /**
     * Open a file. Returns a dummy FileDescriptor; actual I/O should use
     * java.io directly on the host JVM.
     */
    public static FileDescriptor open(String path, int flags, int mode) throws ErrnoException {
        // Stub: signal that we are not implementing native open
        throw new ErrnoException("open", OsConstants.ENOSYS);
    }

    public static void close(FileDescriptor fd) throws ErrnoException {
        if (fd == null) throw new ErrnoException("close", OsConstants.EBADF);
        try {
            fd.sync(); // best-effort flush
        } catch (Exception e) {
            // ignore on stub fd
        }
    }

    public static int read(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount)
            throws ErrnoException {
        throw new ErrnoException("read", OsConstants.ENOSYS);
    }

    public static int write(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount)
            throws ErrnoException {
        throw new ErrnoException("write", OsConstants.ENOSYS);
    }

    public static long lseek(FileDescriptor fd, long offset, int whence) throws ErrnoException {
        throw new ErrnoException("lseek", OsConstants.ENOSYS);
    }

    public static int dup(FileDescriptor oldFd) throws ErrnoException {
        throw new ErrnoException("dup", OsConstants.ENOSYS);
    }

    public static FileDescriptor dup(FileDescriptor oldFd, boolean returnFd) throws ErrnoException {
        throw new ErrnoException("dup", OsConstants.ENOSYS);
    }

    public static int dup2(FileDescriptor oldFd, int newFd) throws ErrnoException {
        throw new ErrnoException("dup2", OsConstants.ENOSYS);
    }

    public static FileDescriptor[] pipe() throws ErrnoException {
        throw new ErrnoException("pipe", OsConstants.ENOSYS);
    }

    public static int fcntlInt(FileDescriptor fd, int cmd, int arg) throws ErrnoException {
        throw new ErrnoException("fcntl", OsConstants.ENOSYS);
    }

    // -------------------------------------------------------------------------
    // File metadata
    // -------------------------------------------------------------------------

    public static StructStat fstat(FileDescriptor fd) throws ErrnoException {
        throw new ErrnoException("fstat", OsConstants.ENOSYS);
    }

    public static StructStat stat(String path) throws ErrnoException {
        java.io.File f = new java.io.File(path);
        if (!f.exists()) throw new ErrnoException("stat", OsConstants.ENOENT);
        long now = System.currentTimeMillis() / 1000L;
        int mode = OsConstants.S_IFREG | OsConstants.S_IRUSR | OsConstants.S_IWUSR
                 | OsConstants.S_IRGRP | OsConstants.S_IROTH;
        if (f.isDirectory()) {
            mode = OsConstants.S_IFDIR | OsConstants.S_IRWXU
                 | OsConstants.S_IRGRP | OsConstants.S_IXGRP
                 | OsConstants.S_IROTH | OsConstants.S_IXOTH;
        }
        return new StructStat(
                0L,          // st_dev
                0L,          // st_ino
                mode,        // st_mode
                1L,          // st_nlink
                0,           // st_uid
                0,           // st_gid
                0L,          // st_rdev
                f.length(),  // st_size
                now,         // st_atime
                now,         // st_mtime
                now,         // st_ctime
                4096L,       // st_blksize
                (f.length() + 511) / 512  // st_blocks
        );
    }

    public static StructStat lstat(String path) throws ErrnoException {
        return stat(path); // symlinks not resolved on stub
    }

    // -------------------------------------------------------------------------
    // File system operations
    // -------------------------------------------------------------------------

    public static void chmod(String path, int mode) throws ErrnoException {
        // No-op on host JVM (no POSIX chmod in standard Java)
    }

    public static void chown(String path, int uid, int gid) throws ErrnoException {
        // No-op on host JVM
    }

    public static void rename(String oldPath, String newPath) throws ErrnoException {
        java.io.File src = new java.io.File(oldPath);
        java.io.File dst = new java.io.File(newPath);
        if (!src.exists()) throw new ErrnoException("rename", OsConstants.ENOENT);
        if (!src.renameTo(dst)) throw new ErrnoException("rename", OsConstants.EIO);
    }

    public static void remove(String path) throws ErrnoException {
        java.io.File f = new java.io.File(path);
        if (!f.exists()) throw new ErrnoException("remove", OsConstants.ENOENT);
        if (!f.delete()) throw new ErrnoException("remove", OsConstants.EIO);
    }

    public static void unlink(String path) throws ErrnoException {
        remove(path);
    }

    public static void mkdir(String path, int mode) throws ErrnoException {
        java.io.File f = new java.io.File(path);
        if (f.exists()) throw new ErrnoException("mkdir", OsConstants.EEXIST);
        if (!f.mkdir()) throw new ErrnoException("mkdir", OsConstants.EIO);
    }

    public static void symlink(String oldPath, String newPath) throws ErrnoException {
        throw new ErrnoException("symlink", OsConstants.ENOSYS);
    }

    public static String readlink(String path) throws ErrnoException {
        throw new ErrnoException("readlink", OsConstants.ENOSYS);
    }

    public static boolean access(String path, int mode) throws ErrnoException {
        java.io.File f = new java.io.File(path);
        if (mode == OsConstants.F_OK) return f.exists();
        if ((mode & OsConstants.R_OK) != 0 && !f.canRead()) return false;
        if ((mode & OsConstants.W_OK) != 0 && !f.canWrite()) return false;
        if ((mode & OsConstants.X_OK) != 0 && !f.canExecute()) return false;
        return true;
    }

    // -------------------------------------------------------------------------
    // Memory mapping
    // -------------------------------------------------------------------------

    public static long mmap(long address, long byteCount, int prot, int flags,
            FileDescriptor fd, long offset) throws ErrnoException {
        throw new ErrnoException("mmap", OsConstants.ENOSYS);
    }

    public static void munmap(long address, long byteCount) throws ErrnoException {
        throw new ErrnoException("munmap", OsConstants.ENOSYS);
    }

    // -------------------------------------------------------------------------
    // Environment
    // -------------------------------------------------------------------------

    public static String getenv(String name) {
        return System.getenv(name);
    }

    public static void setenv(String name, String value, boolean overwrite) throws ErrnoException {
        // Java does not support setenv; no-op stub
    }

    // -------------------------------------------------------------------------
    // Process / user info
    // -------------------------------------------------------------------------

    public static int getpid() {
        return (int) ProcessHandle.current().pid();
    }

    public static int getppid() {
        return ProcessHandle.current().parent()
                .map(ph -> (int) ph.pid()).orElse(0);
    }

    public static int getuid() {
        return 0; // stub — root on OpenHarmony native
    }

    public static int geteuid() {
        return 0;
    }

    public static int getgid() {
        return 0;
    }

    public static int getegid() {
        return 0;
    }

    public static void kill(int pid, int signal) throws ErrnoException {
        if (signal == OsConstants.SIGKILL || signal == OsConstants.SIGTERM) {
            ProcessHandle.of(pid).ifPresent(ProcessHandle::destroy);
        }
        // other signals are no-ops on the host JVM
    }

    public static String strerror(int errno) {
        // Delegate to the mapping inside ErrnoException via a throw/catch trick
        try {
            throw new ErrnoException("strerror", errno);
        } catch (ErrnoException e) {
            // Extract just the strerror portion after "failed: "
            String msg = e.getMessage();
            int idx = msg.indexOf("failed: ");
            if (idx >= 0) {
                int end = msg.indexOf(" (errno=");
                return end >= 0 ? msg.substring(idx + 8, end) : msg.substring(idx + 8);
            }
            return msg;
        }
    }

    public static long sysconf(int name) {
        switch (name) {
            case 30: // _SC_PAGE_SIZE / _SC_PAGESIZE (both == 30)
                return 4096L;
            case OsConstants._SC_CLK_TCK:
                return 100L;
            case OsConstants._SC_NPROCESSORS_CONF:
            case OsConstants._SC_NPROCESSORS_ONLN:
                return Runtime.getRuntime().availableProcessors();
            case OsConstants._SC_OPEN_MAX:
                return 1024L;
            default:
                return -1L;
        }
    }

    // -------------------------------------------------------------------------
    // Socket operations
    // -------------------------------------------------------------------------

    public static FileDescriptor socket(int domain, int type, int protocol)
            throws ErrnoException {
        throw new ErrnoException("socket", OsConstants.ENOSYS);
    }

    public static void bind(FileDescriptor fd, InetAddress address, int port)
            throws ErrnoException {
        throw new ErrnoException("bind", OsConstants.ENOSYS);
    }

    public static void listen(FileDescriptor fd, int backlog) throws ErrnoException {
        throw new ErrnoException("listen", OsConstants.ENOSYS);
    }

    public static FileDescriptor accept(FileDescriptor fd, InetSocketAddress peerAddress)
            throws ErrnoException {
        throw new ErrnoException("accept", OsConstants.ENOSYS);
    }

    public static void connect(FileDescriptor fd, InetAddress address, int port)
            throws ErrnoException {
        throw new ErrnoException("connect", OsConstants.ENOSYS);
    }

    public static int sendto(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount,
            int flags, InetAddress inetAddress, int port) throws ErrnoException {
        throw new ErrnoException("sendto", OsConstants.ENOSYS);
    }

    public static int recvfrom(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount,
            int flags, InetSocketAddress srcAddress) throws ErrnoException {
        throw new ErrnoException("recvfrom", OsConstants.ENOSYS);
    }

    public static void setsockopt(FileDescriptor fd, int level, int option, Object value)
            throws ErrnoException {
        // No-op stub — socket option not enforceable on JVM FileDescriptor
    }

    public static void setsockoptInt(FileDescriptor fd, int level, int option, int value)
            throws ErrnoException {
        // No-op stub
    }

    public static int getsockoptInt(FileDescriptor fd, int level, int option)
            throws ErrnoException {
        return 0; // stub
    }

    public static Object getsockopt(FileDescriptor fd, int level, int option)
            throws ErrnoException {
        return null; // stub
    }

    // -------------------------------------------------------------------------
    // poll()
    // -------------------------------------------------------------------------

    /**
     * Stub poll(). Returns 0 (timeout) without modifying revents.
     */
    public static int poll(StructPollfd[] fds, int timeoutMs) throws ErrnoException {
        return 0;
    }
}
