package android.app.backup;

public class BackupDataInput {
    public BackupDataInput() {}

    public int getDataSize() { return 0; }
    public String getKey() { return null; }
    public int readEntityData(byte[] p0, int p1, int p2) { return 0; }
    public boolean readNextHeader() { return false; }
    public void skipEntityData() {}
}
