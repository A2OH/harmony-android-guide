package android.system;

public final class StructTimeval {
    public int tv_sec = 0;
    public int tv_usec = 0;

    public StructTimeval() {}

    public long toMillis() { return 0L; }
}
