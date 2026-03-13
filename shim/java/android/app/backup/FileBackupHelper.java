package android.app.backup;
import android.content.Context;
import android.os.ParcelFileDescriptor;

public class FileBackupHelper implements BackupHelper {
    public FileBackupHelper(Context p0, String... p1) {}

    public void performBackup(ParcelFileDescriptor p0, BackupDataOutput p1, ParcelFileDescriptor p2) {}
    public void restoreEntity(BackupDataInputStream p0) {}
    public void writeNewStateDescription(ParcelFileDescriptor p0) {}
}
