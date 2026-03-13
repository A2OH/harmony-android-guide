package android.system;
import android.location.Address;
import android.telecom.Connection;
import android.location.Address;
import android.telecom.Connection;
import java.io.File;
import java.net.Socket;

import java.io.IOException;

/**
 * Android-compatible ErrnoException shim.
 * Mirrors android.system.ErrnoException: wraps a POSIX errno code.
 */
public class ErrnoException extends Exception {

    /** The errno value from the failed syscall. */
    public final int errno;

    public ErrnoException(String functionName, int errno) {
        super(functionName + " failed: " + strerror(errno) + " (errno=" + errno + ")");
        this.errno = errno;
    }

    public ErrnoException(String functionName, int errno, Throwable cause) {
        super(functionName + " failed: " + strerror(errno) + " (errno=" + errno + ")", cause);
        this.errno = errno;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    /**
     * Converts this ErrnoException to an IOException for callers that do not
     * want to handle errno directly.
     */
    public IOException rethrowAsIOException() throws IOException {
        IOException ioe = new IOException(getMessage(), this);
        throw ioe;
    }

    /** Basic errno-to-string mapping matching POSIX / Linux. */
    private static String strerror(int errno) {
        switch (errno) {
            case OsConstants.EPERM:   return "Operation not permitted";
            case OsConstants.ENOENT:  return "No such file or directory";
            case OsConstants.EINTR:   return "Interrupted system call";
            case OsConstants.EIO:     return "Input/output error";
            case OsConstants.ENXIO:   return "No such device or address";
            case OsConstants.EBADF:   return "Bad file descriptor";
            case OsConstants.EAGAIN:  return "Resource temporarily unavailable";
            case OsConstants.ENOMEM:  return "Out of memory";
            case OsConstants.EACCES:  return "Permission denied";
            case OsConstants.EFAULT:  return "Bad address";
            case OsConstants.EBUSY:   return "Device or resource busy";
            case OsConstants.EEXIST:  return "File exists";
            case OsConstants.ENODEV:  return "No such device";
            case OsConstants.ENOTDIR: return "Not a directory";
            case OsConstants.EISDIR:  return "Is a directory";
            case OsConstants.EINVAL:  return "Invalid argument";
            case OsConstants.ENFILE:  return "Too many open files in system";
            case OsConstants.EMFILE:  return "Too many open files";
            case OsConstants.ENOSPC:  return "No space left on device";
            case OsConstants.EROFS:   return "Read-only file system";
            case OsConstants.EPIPE:   return "Broken pipe";
            case OsConstants.ERANGE:  return "Numerical result out of range";
            case OsConstants.EDEADLK: return "Resource deadlock avoided";
            case OsConstants.ENAMETOOLONG: return "File name too long";
            case OsConstants.ENOSYS:  return "Function not implemented";
            case OsConstants.ENOTEMPTY: return "Directory not empty";
            case OsConstants.ELOOP:   return "Too many levels of symbolic links";
            case OsConstants.ENOTSOCK: return "Socket operation on non-socket";
            case OsConstants.EADDRINUSE: return "Address already in use";
            case OsConstants.ECONNREFUSED: return "Connection refused";
            case OsConstants.ETIMEDOUT: return "Connection timed out";
            case OsConstants.ECONNRESET: return "Connection reset by peer";
            case OsConstants.ENOTCONN: return "Transport endpo(int is not connected";
            case OsConstants.EPROTONOSUPPORT: return "Protocol not supported";
            case OsConstants.EOPNOTSUPP: return "Operation not supported";
            case OsConstants.EAFNOSUPPORT: return "Address family not supported by protocol";
            default: return "Unknown error " + errno;
        }
    }
}
