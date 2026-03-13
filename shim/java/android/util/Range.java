package android.util;

/**
 * Stub for android.util.Range — immutable range with lower and upper bounds.
 */
public final class Range<T extends Comparable<? super T>> {
    private final T mLower;
    private final T mUpper;

    public Range(T lower, T upper) {
        mLower = lower;
        mUpper = upper;
    }

    public T getLower() {
        return mLower;
    }

    public T getUpper() {
        return mUpper;
    }

    public boolean contains(T value) {
        if (value == null) return false;
        return value.compareTo(mLower) >= 0 && value.compareTo(mUpper) <= 0;
    }

    public boolean contains(Range<T> range) {
        if (range == null) return false;
        return range.mLower.compareTo(mLower) >= 0 && range.mUpper.compareTo(mUpper) <= 0;
    }

    public Range<T> intersect(Range<T> range) {
        T newLower = mLower.compareTo(range.mLower) >= 0 ? mLower : range.mLower;
        T newUpper = mUpper.compareTo(range.mUpper) <= 0 ? mUpper : range.mUpper;
        return new Range<>(newLower, newUpper);
    }

    public Range<T> intersect(T lower, T upper) {
        return intersect(new Range<>(lower, upper));
    }

    public Range<T> extend(Range<T> range) {
        T newLower = mLower.compareTo(range.mLower) <= 0 ? mLower : range.mLower;
        T newUpper = mUpper.compareTo(range.mUpper) >= 0 ? mUpper : range.mUpper;
        return new Range<>(newLower, newUpper);
    }

    public Range<T> extend(T lower, T upper) {
        return extend(new Range<>(lower, upper));
    }

    public Range<T> extend(T value) {
        return extend(new Range<>(value, value));
    }

    public Range<T> clamp(T value) {
        if (value.compareTo(mLower) < 0) return new Range<>(mLower, mLower);
        if (value.compareTo(mUpper) > 0) return new Range<>(mUpper, mUpper);
        return new Range<>(value, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Range)) return false;
        Range<?> range = (Range<?>) o;
        return mLower.equals(range.mLower) && mUpper.equals(range.mUpper);
    }

    @Override
    public int hashCode() {
        return 31 * mLower.hashCode() + mUpper.hashCode();
    }

    @Override
    public String toString() {
        return "[" + mLower + ", " + mUpper + "]";
    }
}
