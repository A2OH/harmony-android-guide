package android.text;
import java.util.Locale;

/**
 * Android-compatible TextDirectionHeuristics shim.
 * Provides static instances for common text direction heuristics.
 */
public final class TextDirectionHeuristics {

    private TextDirectionHeuristics() {}

    /** Interface matching android.text.TextDirectionHeuristic. */
    public interface TextDirectionHeuristic {
        /** Returns true if the text run in the given range is RTL. */
        boolean isRtl(char[] array, int start, int count);
    }

    private static final class ConstantHeuristic implements TextDirectionHeuristic {
        private final boolean mIsRtl;
        ConstantHeuristic(boolean isRtl) { mIsRtl = isRtl; }
        @Override public boolean isRtl(char[] array, int start, int count) { return mIsRtl; }
    }

    private static final class FirstStrongHeuristic implements TextDirectionHeuristic {
        private final boolean mDefaultRtl;
        FirstStrongHeuristic(boolean defaultRtl) { mDefaultRtl = defaultRtl; }
        @Override
        public boolean isRtl(char[] array, int start, int count) {
            if (array == null) return mDefaultRtl;
            for (int i = start, end = start + count; i < end; i++) {
                char c = array[i];
                // Basic RTL range check (Arabic/Hebrew)
                if ((c >= '\u0590' && c <= '\u08FF') || (c >= '\uFB1D' && c <= '\uFDFF')
                        || (c >= '\uFE70' && c <= '\uFEFF')) {
                    return true;
                }
                if (c < '\u0300') {
                    return false;
                }
            }
            return mDefaultRtl;
        }
    }

    private static final class AnyRtlHeuristic implements TextDirectionHeuristic {
        @Override
        public boolean isRtl(char[] array, int start, int count) {
            if (array == null) return false;
            for (int i = start, end = start + count; i < end; i++) {
                char c = array[i];
                if ((c >= '\u0590' && c <= '\u08FF') || (c >= '\uFB1D' && c <= '\uFDFF')
                        || (c >= '\uFE70' && c <= '\uFEFF')) {
                    return true;
                }
            }
            return false;
        }
    }

    /** Always resolves to LTR. */
    public static final TextDirectionHeuristic LTR = new ConstantHeuristic(false);

    /** Always resolves to RTL. */
    public static final TextDirectionHeuristic RTL = new ConstantHeuristic(true);

    /** Examines the first strong directional character; defaults LTR. */
    public static final TextDirectionHeuristic FIRSTSTRONG_LTR = new FirstStrongHeuristic(false);

    /** Examines the first strong directional character; defaults RTL. */
    public static final TextDirectionHeuristic FIRSTSTRONG_RTL = new FirstStrongHeuristic(true);

    /** If any RTL character is found, resolves RTL; otherwise LTR. */
    public static final TextDirectionHeuristic ANYRTL_LTR = new AnyRtlHeuristic();

    /** Uses the system locale direction. */
    public static final TextDirectionHeuristic LOCALE = new TextDirectionHeuristic() {
        @Override
        public boolean isRtl(char[] array, int start, int count) {
            return false; // stub: assume LTR locale
        }
    };
}
