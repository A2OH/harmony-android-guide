package android.security;
import android.content.Context;
import java.util.concurrent.Executor;

public class ConfirmationPrompt {
    public ConfirmationPrompt() {}

    public void cancelPrompt() {}
    public static boolean isSupported(Context p0) { return false; }
    public void presentPrompt(Executor p0, ConfirmationCallback p1) {}
}
