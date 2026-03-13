package android.util;

import java.io.*;

/**
 * Android-compatible AtomicFile shim. Writes to temp file, renames on finish.
 */
public class AtomicFile {
    private final File mBaseName;
    private final File mBackupName;

    public AtomicFile(File baseName) {
        mBaseName = baseName;
        mBackupName = new File(baseName.getPath() + ".bak");
    }

    public File getBaseFile() {
        return mBaseName;
    }

    public FileOutputStream startWrite() throws IOException {
        // Backup existing file
        if (mBaseName.exists()) {
            if (!mBackupName.exists()) {
                if (!mBaseName.renameTo(mBackupName)) {
                    throw new IOException("Couldn't rename to backup");
                }
            } else {
                mBaseName.delete();
            }
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(mBaseName);
        } catch (FileNotFoundException e) {
            File parent = mBaseName.getParentFile();
            if (parent != null && !parent.mkdirs()) {
                throw new IOException("Couldn't create directory " + parent);
            }
            fos = new FileOutputStream(mBaseName);
        }
        return fos;
    }

    public void finishWrite(FileOutputStream str) {
        if (str != null) {
            try { str.close(); } catch (IOException e) {}
        }
        mBackupName.delete();
    }

    public void failWrite(FileOutputStream str) {
        if (str != null) {
            try { str.close(); } catch (IOException e) {}
        }
        mBaseName.delete();
        mBackupName.renameTo(mBaseName);
    }

    public FileInputStream openRead() throws FileNotFoundException {
        if (mBackupName.exists()) {
            mBaseName.delete();
            mBackupName.renameTo(mBaseName);
        }
        return new FileInputStream(mBaseName);
    }

    public byte[] readFully() throws IOException {
        try (FileInputStream fis = openRead();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = fis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            return bos.toByteArray();
        }
    }

    public void delete() {
        mBaseName.delete();
        mBackupName.delete();
    }
}
