package android.text;

/**
 * Android-compatible AndroidCharacter stub for the A2OH shim layer.
 * Provides character-property utilities; all methods are no-op stubs.
 */
public class AndroidCharacter {

    public static final int EAST_ASIAN_WIDTH_NEUTRAL = 0;
    public static final int EAST_ASIAN_WIDTH_AMBIGUOUS = 1;
    public static final int EAST_ASIAN_WIDTH_HALF_WIDTH = 2;
    public static final int EAST_ASIAN_WIDTH_FULL_WIDTH = 3;
    public static final int EAST_ASIAN_WIDTH_NARROW = 4;
    public static final int EAST_ASIAN_WIDTH_WIDE = 5;

    /**
     * Fills in the directionalities of the specified range of characters.
     * No-op stub.
     */
    public static void getDirectionalities(char[] src, byte[] dest, int count) {
        // no-op
    }

    /**
     * Returns the East Asian Width of the given character.
     * Stub always returns 0 (EAST_ASIAN_WIDTH_NEUTRAL).
     */
    public static int getEastAsianWidth(char c) {
        return 0;
    }

    /**
     * Fills in the East Asian Widths of the specified range of characters.
     * No-op stub.
     */
    public static void getEastAsianWidths(char[] src, int start, int count, byte[] dest) {
        // no-op
    }

    /**
     * Returns the mirror of the given character, if any.
     * Stub always returns the input character unchanged.
     */
    public static char getMirror(char c) {
        return c;
    }

    /**
     * Attempts to mirror the characters in the specified range.
     * Stub always returns false (no mirroring performed).
     */
    public static boolean mirror(char[] text, int start, int count) {
        return false;
    }
}
