package android.util;

import java.util.Objects;

/**
 * Shim: android.util.Pair<F,S>
 * Immutable container for two typed values.
 * Pure Java — no Android or OHBridge dependencies.
 */
public final class Pair<F, S> {

    public final F first;
    public final S second;

    public Pair(F first, S second) {
        this.first  = first;
        this.second = second;
    }

    /**
     * Object method — mirrors {@code android.util.Pair.create(a, b)}.
     */
    public static <A, B> Pair<A, B> create(A a, B b) {
        return new Pair<>(a, b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> other = (Pair<?, ?>) o;
        return Objects.equals(first,  other.first)
            && Objects.equals(second, other.second);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(first) + Objects.hashCode(second);
    }

    @Override
    public String toString() {
        return "Pair{" + first + " " + second + '}';
    }
}
