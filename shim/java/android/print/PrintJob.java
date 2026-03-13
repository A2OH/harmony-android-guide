package android.print;

public final class PrintJob {
    public PrintJob() {}

    public void cancel() {}
    public boolean isBlocked() { return false; }
    public boolean isCancelled() { return false; }
    public boolean isCompleted() { return false; }
    public boolean isFailed() { return false; }
    public boolean isQueued() { return false; }
    public boolean isStarted() { return false; }
    public void restart() {}
}
