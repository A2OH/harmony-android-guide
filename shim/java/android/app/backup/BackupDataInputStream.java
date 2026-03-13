package android.app.backup;
import java.io.InputStream;

public class BackupDataInputStream extends InputStream {
    public BackupDataInputStream() {}

    public String getKey() { return null; }
    public int read() { return 0; }
    public int size() { return 0; }
}
