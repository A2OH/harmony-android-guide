package android.view;
import android.os.Parcelable;
import android.os.Parcelable;

public final class VerifiedKeyEvent extends VerifiedInputEvent implements Parcelable {
    public VerifiedKeyEvent() {}

    public int getAction() { return 0; }
    public long getDownTimeNanos() { return 0L; }
    public int getKeyCode() { return 0; }
    public int getMetaState() { return 0; }
    public int getRepeatCount() { return 0; }
    public int getScanCode() { return 0; }
}
