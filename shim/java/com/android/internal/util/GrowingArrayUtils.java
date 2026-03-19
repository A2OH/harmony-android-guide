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

    public static int growSize(int currentSize) {
        return currentSize <= 4 ? 8 : currentSize * 2;
    }
}
