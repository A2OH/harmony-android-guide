package com.android.internal.util;

import java.lang.reflect.Array;

/**
 * Stub for com.android.internal.util.GrowingArrayUtils.
 * Utility methods for growing arrays.
 */
public class GrowingArrayUtils {

    @SuppressWarnings("unchecked")
    public static <T> T[] append(T[] array, int currentSize, T element) {
        if (currentSize + 1 > array.length) {
            T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), growSize(currentSize));
            System.arraycopy(array, 0, newArray, 0, currentSize);
            newArray[currentSize] = element;
            return newArray;
        }
        array[currentSize] = element;
        return array;
    }

    public static int[] append(int[] array, int currentSize, int element) {
        if (currentSize + 1 > array.length) {
            int[] newArray = new int[growSize(currentSize)];
            System.arraycopy(array, 0, newArray, 0, currentSize);
            newArray[currentSize] = element;
            return newArray;
        }
        array[currentSize] = element;
        return array;
    }

    public static long[] append(long[] array, int currentSize, long element) {
        if (currentSize + 1 > array.length) {
            long[] newArray = new long[growSize(currentSize)];
            System.arraycopy(array, 0, newArray, 0, currentSize);
            newArray[currentSize] = element;
            return newArray;
        }
        array[currentSize] = element;
        return array;
    }

    public static boolean[] append(boolean[] array, int currentSize, boolean element) {
        if (currentSize + 1 > array.length) {
            boolean[] newArray = new boolean[growSize(currentSize)];
            System.arraycopy(array, 0, newArray, 0, currentSize);
            newArray[currentSize] = element;
            return newArray;
        }
        array[currentSize] = element;
        return array;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] insert(T[] array, int currentSize, int index, T element) {
        if (currentSize + 1 <= array.length) {
            System.arraycopy(array, index, array, index + 1, currentSize - index);
            array[index] = element;
            return array;
        }
        T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), growSize(currentSize));
        System.arraycopy(array, 0, newArray, 0, index);
        newArray[index] = element;
        System.arraycopy(array, index, newArray, index + 1, currentSize - index);
        return newArray;
    }

    public static int growSize(int currentSize) {
        return currentSize <= 4 ? 8 : currentSize * 2;
    }
}
