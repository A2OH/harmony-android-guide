package android.os.strictmode;

public class InstanceCountViolation extends Violation {
    public InstanceCountViolation() {}

    public long getNumberOfInstances() { return 0L; }
}
