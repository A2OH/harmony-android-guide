package com.android.internal.util;

import java.lang.reflect.Array;

/**
 * Stub for com.android.internal.util.ArrayUtils.
 */
public class ArrayUtils {

    @SuppressWarnings("unchecked")
    public static <T> T[] emptyArray(Class<T> kind) {
        return (T[]) Array.newInstance(kind, 0);
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean contains(Object[] array, Object value) {
        if (array == null) return false;
        for (Object o : array) {
            if (o == value || (o != null && o.equals(value))) return true;
        }
        return false;
    }

    public static boolean contains(int[] array, int value) {
        if (array == null) return false;
        for (int i : array) {
            if (i == value) return true;
        }
        return false;
    }

    /** Create an int array with at least minLen elements. */
    public static int[] newUnpaddedIntArray(int minLen) {
        return new int[minLen];
    }

    /** Create an array of the given type with at least minLen elements. */
    @SuppressWarnings("unchecked")
    public static <T> T[] newUnpaddedArray(Class<T> kind, int minLen) {
        return (T[]) Array.newInstance(kind, minLen);
    }

    /** Create a long array with at least minLen elements. */
    public static long[] newUnpaddedLongArray(int minLen) {
        return new long[minLen];
    }

    /** Create a float array with at least minLen elements. */
    public static float[] newUnpaddedFloatArray(int minLen) {
        return new float[minLen];
    }
}
