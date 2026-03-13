package android.os;

import java.util.Arrays;
import java.util.Locale;

/**
 * A2OH shim: LocaleList - an immutable, ordered list of Locales.
 */
public final class LocaleList {

    private final Locale[] mList;

    private static final LocaleList EMPTY_LOCALE_LIST = new LocaleList();

    // ---- Construction -------------------------------------------------------

    /** Creates a LocaleList from the given locales (varargs). */
    public LocaleList(Locale... locales) {
        if (locales == null) {
            mList = new Locale[0];
        } else {
            mList = Arrays.copyOf(locales, locales.length);
        }
    }

    // ---- Instance methods ---------------------------------------------------

    /** Returns the Locale at the given index. */
    public Locale get(int index) {
        return mList[index];
    }

    /** Returns the number of locales in this list. */
    public int size() {
        return mList.length;
    }

    /** Returns {@code true} if the list contains no locales. */
    public boolean isEmpty() {
        return mList.length == 0;
    }

    /**
     * Returns the index of the first locale in this list that equals the given
     * locale, or {@code -1} if not found.
     */
    public int indexOf(Locale locale) {
        for (int i = 0; i < mList.length; i++) {
            if (mList[i].equals(locale)) return i;
        }
        return -1;
    }

    /**
     * Returns a comma-separated string of BCP-47 language tags for all locales
     * in this list (e.g. {@code "en-US,fr-FR"}).
     */
    public String toLanguageTags() {
        if (mList.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mList.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(mList[i].toLanguageTag());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "[" + toLanguageTags() + "]";
    }

    // ---- Static helpers -----------------------------------------------------

    /** Returns the default system LocaleList (wraps {@link Locale#getDefault()}). */
    public static LocaleList getDefault() {
        return new LocaleList(Locale.getDefault());
    }

    /**
     * Returns the adjusted default LocaleList.
     * In the shim this is identical to {@link #getDefault()}.
     */
    public static LocaleList getAdjustedDefault() {
        return getDefault();
    }

    /** Returns the empty LocaleList singleton. */
    public static LocaleList getEmptyLocaleList() {
        return EMPTY_LOCALE_LIST;
    }

    /**
     * Constructs a LocaleList from a comma-separated string of BCP-47 language
     * tags (e.g. {@code "en-US,fr-FR"}).
     */
    public static LocaleList forLanguageTags(String list) {
        if (list == null || list.isEmpty()) return EMPTY_LOCALE_LIST;
        String[] tags = list.split(",");
        Locale[] locales = new Locale[tags.length];
        for (int i = 0; i < tags.length; i++) {
            locales[i] = Locale.forLanguageTag(tags[i].trim());
        }
        return new LocaleList(locales);
    }
}
