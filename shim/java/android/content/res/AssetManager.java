package android.content.res;

import java.io.IOException;
import java.io.InputStream;

public class AssetManager {
    public InputStream open(String fileName) throws IOException {
        throw new IOException("AssetManager stub: cannot open " + fileName);
    }

    public String[] list(String path) throws IOException {
        return new String[0];
    }

    public void close() {
        // no-op
    }
}
