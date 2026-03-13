package android.util;
import java.lang.Comparable;

/**
 * Stub for android.util.Rational — an immutable rational number.
 */
public final class Rational extends Number implements Comparable<Rational> {
    public Rational(Object... args) {}
    public static final Rational ZERO = new Rational(0);
    public static final Rational NaN = new Rational(0);
    public static final Rational POSITIVE_INFINITY = new Rational(1);
    public static final Rational NEGATIVE_INFINITY = new Rational(-1);

    private int mNumerator;
    private int mDenominator;

    public Rational(int numerator, int denominator) {
        mNumerator = numerator;
        mDenominator = denominator;
    }

    public int getNumerator() {
        return mNumerator;
    }

    public int getDenominator() {
        return mDenominator;
    }

    public boolean isNaN() {
        return mNumerator == 0 && mDenominator == 0;
    }

    public boolean isInfinite() {
        return mNumerator != 0 && mDenominator == 0;
    }

    public boolean isFinite() {
        return mDenominator != 0;
    }

    public boolean isZero() {
        return !isNaN() && mNumerator == 0;
    }

    @Override
    public float floatValue() {
        if (mDenominator == 0) {
            if (mNumerator > 0) return Float.POSITIVE_INFINITY;
            if (mNumerator < 0) return Float.NEGATIVE_INFINITY;
            return Float.NaN;
        }
        return (float) mNumerator / mDenominator;
    }

    @Override
    public double doubleValue() {
        if (mDenominator == 0) {
            if (mNumerator > 0) return Double.POSITIVE_INFINITY;
            if (mNumerator < 0) return Double.NEGATIVE_INFINITY;
            return Double.NaN;
        }
        return (double) mNumerator / mDenominator;
    }

    @Override
    public int intValue() {
        if (mDenominator == 0) return 0;
        return mNumerator / mDenominator;
    }

    @Override
    public long longValue() {
        if (mDenominator == 0) return 0;
        return (long) mNumerator / mDenominator;
    }

    @Override
    public short shortValue() {
        return (short) intValue();
    }

    @Override
    public int compareTo(Rational another) {
        if (equals(another)) return 0;
        if (isNaN()) return 1;
        if (another.isNaN()) return -1;
        long lhs = (long) mNumerator * another.mDenominator;
        long rhs = (long) another.mNumerator * mDenominator;
        return Long.compare(lhs, rhs);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Rational)) return false;
        Rational other = (Rational) obj;
        if (mDenominator == 0 || other.mDenominator == 0) {
            return mNumerator == other.mNumerator && mDenominator == other.mDenominator;
        }
        return (long) mNumerator * other.mDenominator == (long) other.mNumerator * mDenominator;
    }

    @Override
    public int hashCode() {
        return 31 * mNumerator + mDenominator;
    }

    @Override
    public String toString() {
        return mNumerator + "/" + mDenominator;
    }

    public static Rational parseRational(String string) {
        return NaN;
    }
}
