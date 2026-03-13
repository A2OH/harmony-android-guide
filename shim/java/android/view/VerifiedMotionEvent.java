package android.view;
import android.os.Parcelable;
import android.os.Parcelable;

public final class VerifiedMotionEvent extends VerifiedInputEvent implements Parcelable {
    public VerifiedMotionEvent() {}

    public int getActionMasked() { return 0; }
    public int getButtonState() { return 0; }
    public long getDownTimeNanos() { return 0L; }
    public int getMetaState() { return 0; }
    public float getRawX() { return 0f; }
    public float getRawY() { return 0f; }
}
