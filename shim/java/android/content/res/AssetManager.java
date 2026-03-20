package android.content.res;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetManager {
    private File mAssetDir;

    /** Set the root directory where APK assets were extracted. */
    public void setAssetDir(String path) {
        mAssetDir = (path != null) ? new File(path) : null;
    }

    public InputStream open(String fileName) throws IOException {
        if (mAssetDir != null) {
            File f = new File(mAssetDir, fileName);
            if (f.exists() && f.isFile()) {
                return new FileInputStream(f);
            }
        }
        throw new IOException("Asset not found: " + fileName);
    }

    public InputStream open(String fileName, int accessMode) throws IOException {
        return open(fileName);
    }

    public String[] list(String path) throws IOException {
        if (mAssetDir != null) {
            File dir = path.isEmpty() ? mAssetDir : new File(mAssetDir, path);
            if (dir.exists() && dir.isDirectory()) {
                String[] names = dir.list();
                return names != null ? names : new String[0];
            }
        }
        return new String[0];
    }

    public void close() {
        // no-op — assets stay extracted for app lifetime
    }

    public android.util.SparseArray<String> getAssignedPackageIdentifiers() {
        return new android.util.SparseArray<String>();
    }
}
