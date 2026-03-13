package android.provider;

import android.net.Uri;

/**
 * Android-compatible UserDictionary provider shim.
 *
 * OH mapping: No direct OH equivalent. Use @ohos.data.preferences or a custom
 * RdbStore table to persist user-defined words for custom keyboard/IME input.
 */
public final class UserDictionary {

    private UserDictionary() {}

    /**
     * Words stored in the user dictionary.
     */
    public static final class Words {
        private Words() {}

        public static final Uri CONTENT_URI =
                Uri.parse("content://user_dictionary/words");

        /** Row ID column. */
        public static final String _ID        = "_id";

        /** The word itself. */
        public static final String WORD       = "word";

        /**
         * The frequency score of the word (higher = preferred).
         * Range: 0–255.
         */
        public static final String FREQUENCY  = "frequency";

        /**
         * The locale for the word (e.g. "en_US"), or null/empty for all locales.
         */
        public static final String LOCALE     = "locale";

        /** The app that added the word. */
        public static final String APP_ID     = "appid";

        /** Optional shortcut string that expands to this word. */
        public static final String SHORTCUT   = "shortcut";

        /** Default sort order. */
        public static final String DEFAULT_SORT_ORDER = WORD + " ASC";

        /**
         * Add a word to the user dictionary.
         *
         * @param context   application context (typed as Object per A2OH convention)
         * @param word      the word to add
         * @param frequency usage frequency (0–255); use 250 for a typical new word
         * @param locale    BCP-47 locale tag, or null for all locales
         */
        public static void addWord(Object context, String word, int frequency, String locale) {
            // Stub: in a real implementation this inserts a row via ContentResolver.
        }

        /**
         * Add a word with a shortcut to the user dictionary.
         *
         * @param context   application context (typed as Object per A2OH convention)
         * @param word      the word to add
         * @param shortcut  the shortcut string that expands to this word, or null
         * @param frequency usage frequency (0–255)
         * @param locale    BCP-47 locale tag, or null for all locales
         */
        public static void addWord(Object context, String word, String shortcut,
                int frequency, String locale) {
            // Stub: in a real implementation this inserts/updates a row via ContentResolver.
        }
    }
}
