package com.android.internal.util;
/** AOSP compilation stub. */
public class Preconditions {
    public static <T> T checkNotNull(T reference) { return reference; }
    public static <T> T checkNotNull(T reference, Object msg) { return reference; }
    public static <T> T checkNotNull(T reference, String msg, Object... args) { return reference; }
    public static void checkArgument(boolean expression) {}
    public static void checkArgument(boolean expression, Object msg) {}
    public static void checkArgument(boolean expression, String msg, Object... args) {}
    public static int checkArgumentNonnegative(int value) { return value; }
    public static int checkArgumentNonnegative(int value, String msg) { return value; }
    public static void checkState(boolean expression) {}
    public static void checkState(boolean expression, Object msg) {}
    public static int checkArgumentPositive(int value, String msg) { return value; }
    public static <T> T checkArgumentInRange(T value, T lower, T upper, String name) { return value; }
    public static int checkFlagsArgument(int flags, int mask) { return flags; }
}
