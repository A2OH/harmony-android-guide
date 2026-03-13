package android.system;
import java.io.File;

/**
 * Android-compatible StructStat shim.
 * Mirrors android.system.StructStat returned by Os.stat() / Os.fstat().
 * All fields are public final, matching the Android API contract.
 */
public final class StructStat {

    /** Device ID of device containing file. */
    public final long st_dev;

    /** File serial (inode) number. */
    public final long st_ino;

    /** File type and permissions (mode bits). */
    public final int st_mode;

    /** Number of hard links. */
    public final long st_nlink;

    /** User ID of file owner. */
    public final int st_uid;

    /** Group ID of file owner. */
    public final int st_gid;

    /** Device ID (if special file). */
    public final long st_rdev;

    /** Total size in bytes. */
    public final long st_size;

    /** Last access time (seconds since epoch). */
    public final long st_atime;

    /** Last data modification time (seconds since epoch). */
    public final long st_mtime;

    /** Last status change time (seconds since epoch). */
    public final long st_ctime;

    /** Preferred I/O block size in bytes. */
    public final long st_blksize;

    /** Number of 512-byte blocks allocated. */
    public final long st_blocks;

    public StructStat(
            long st_dev,
            long st_ino,
            int  st_mode,
            long st_nlink,
            int  st_uid,
            int  st_gid,
            long st_rdev,
            long st_size,
            long st_atime,
            long st_mtime,
            long st_ctime,
            long st_blksize,
            long st_blocks) {
        this.st_dev     = st_dev;
        this.st_ino     = st_ino;
        this.st_mode    = st_mode;
        this.st_nlink   = st_nlink;
        this.st_uid     = st_uid;
        this.st_gid     = st_gid;
        this.st_rdev    = st_rdev;
        this.st_size    = st_size;
        this.st_atime   = st_atime;
        this.st_mtime   = st_mtime;
        this.st_ctime   = st_ctime;
        this.st_blksize = st_blksize;
        this.st_blocks  = st_blocks;
    }

    @Override
    public String toString() {
        return "StructStat{st_mode=" + st_mode
                + ", st_size=" + st_size
                + ", st_uid=" + st_uid
                + ", st_gid=" + st_gid + "}";
    }
}
