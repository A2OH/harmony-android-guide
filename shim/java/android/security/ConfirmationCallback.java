package android.security;

public class ConfirmationCallback {
    public ConfirmationCallback() {}

    public void onCanceled() {}
    public void onConfirmed(byte[] p0) {}
    public void onDismissed() {}
    public void onError(Throwable p0) {}
}
