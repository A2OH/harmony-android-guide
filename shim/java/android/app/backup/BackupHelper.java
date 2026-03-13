package android.app.backup;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor;

public interface BackupHelper {
    void performBackup(ParcelFileDescriptor p0, BackupDataOutput p1, ParcelFileDescriptor p2);
    void restoreEntity(BackupDataInputStream p0);
    void writeNewStateDescription(ParcelFileDescriptor p0);
}
