package android.text;

/**
 * Android-compatible AlteredCharSequence stub.
 *
 * @deprecated This class is deprecated as of API level 28.
 */
@Deprecated
public class AlteredCharSequence implements CharSequence, GetChars {

    private final CharSequence mSource;
    private final char[] mSub;
    private final int mSubStart;
    private final int mSubEnd;

    private AlteredCharSequence(CharSequence source, char[] sub,
                                int subStart, int subEnd) {
        mSource = source;
        mSub = sub;
        mSubStart = subStart;
        mSubEnd = subEnd;
    }

    /**
     * Create an AlteredCharSequence whose text (and possibly spans) are
     * mirrored from {@code source}, except that the range of offsets
     * {@code subStart}..{@code subEnd} are mirrored instead from {@code sub},
     * beginning at offset 0.
     */
    public static AlteredCharSequence make(CharSequence source, char[] sub,
                                           int subStart, int subEnd) {
        return new AlteredCharSequence(source, sub, subStart, subEnd);
    }

    @Override
    public int length() {
        return mSource.length();
    }

    @Override
    public char charAt(int index) {
        if (index >= mSubStart && index < mSubEnd) {
            return mSub[index - mSubStart];
        }
        return mSource.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return make(mSource.subSequence(start, end), mSub,
                    mSubStart - start, mSubEnd - start);
    }

    @Override
    public String toString() {
        int len = length();
        char[] buf = new char[len];
// FIXME:         getChars(0, len, buf);
        return new String(buf);
    }

    @Override
    public void getChars(int start, int end, char[] dest, int destoff) {
        for (int i = start; i < end; i++) {
            dest[destoff + (i - start)] = charAt(i);
        }
    }
}
