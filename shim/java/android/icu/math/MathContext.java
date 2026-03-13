package android.icu.math;
import java.io.Serializable;

public final class MathContext implements Serializable {
    public static final int DEFAULT = 0;
    public static final int ENGINEERING = 0;
    public static final int PLAIN = 0;
    public static final int ROUND_CEILING = 0;
    public static final int ROUND_DOWN = 0;
    public static final int ROUND_FLOOR = 0;
    public static final int ROUND_HALF_DOWN = 0;
    public static final int ROUND_HALF_EVEN = 0;
    public static final int ROUND_HALF_UP = 0;
    public static final int ROUND_UNNECESSARY = 0;
    public static final int ROUND_UP = 0;
    public static final int SCIENTIFIC = 0;

    public MathContext(int p0) {}
    public MathContext(int p0, int p1) {}
    public MathContext(int p0, int p1, boolean p2) {}
    public MathContext(int p0, int p1, boolean p2, int p3) {}

    public int getDigits() { return 0; }
    public int getForm() { return 0; }
    public boolean getLostDigits() { return false; }
    public int getRoundingMode() { return 0; }
}
