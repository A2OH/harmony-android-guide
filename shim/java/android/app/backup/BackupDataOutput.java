package android.app.backup;

public class BackupDataOutput {
    public BackupDataOutput() {}

    public long getQuota() { return 0L; }
    public int getTransportFlags() { return 0; }
    public int writeEntityData(byte[] p0, int p1) { return 0; }
    public int writeEntityHeader(String p0, int p1) { return 0; }
}
