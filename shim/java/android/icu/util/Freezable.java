package android.icu.util;

public interface Freezable extends Cloneable {
    Object cloneAsThawed();
    Object freeze();
    boolean isFrozen();
}
