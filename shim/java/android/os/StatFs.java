package android.os;

import java.io.File;

/**
 * Android-compatible StatFs shim. Uses java.io.File for disk stats.
 */
public class StatFs {
    private final File mPath;

    public StatFs(String path) {
        mPath = new File(path);
    }

    public void restat(String path) {
        // no-op — File methods are always live
    }

    public long getBlockSizeLong() {
        return 4096; // standard block size
    }

    public long getBlockCountLong() {
        return mPath.getTotalSpace() / getBlockSizeLong();
    }

    public long getAvailableBlocksLong() {
        return mPath.getUsableSpace() / getBlockSizeLong();
    }

    public long getFreeBlocksLong() {
        return mPath.getFreeSpace() / getBlockSizeLong();
    }

    public long getTotalBytes() {
        return mPath.getTotalSpace();
    }

    public long getAvailableBytes() {
        return mPath.getUsableSpace();
    }

    public long getFreeBytes() {
        return mPath.getFreeSpace();
    }

    @Deprecated
    public int getBlockSize() { return (int) getBlockSizeLong(); }
    @Deprecated
    public int getBlockCount() { return (int) getBlockCountLong(); }
    @Deprecated
    public int getAvailableBlocks() { return (int) getAvailableBlocksLong(); }
    @Deprecated
    public int getFreeBlocks() { return (int) getFreeBlocksLong(); }
}
