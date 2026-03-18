package com.android.internal.widget;

/** AOSP compilation stub. */
public class ScrollBarUtils {
    public static int getThumbLength(int size, int thickness, int extent, int range) {
        if (range <= 0) return 0;
        int minLen = thickness * 2;
        int length = (int)((long)size * extent / range);
        return Math.max(length, minLen);
    }

    public static int getThumbOffset(int size, int thumbLength, int extent, int range, int offset) {
        if (range <= extent) return 0;
        int travel = size - thumbLength;
        return (int)((long)travel * offset / (range - extent));
    }
}
