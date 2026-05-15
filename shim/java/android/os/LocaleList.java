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

    // CR62 (2026-05-15): AOSP also exposes `setDefault(LocaleList)` and
    // `setDefault(LocaleList, int)`. AppCompatDelegate.applyConfigurationToResources
    // (R8-shrunk `e.v.c` chain) calls one of these during attachBaseContext2,
    // so omitting them produces NoSuchMethodError on the noice MainActivity
    // ctor path even after CR62 Step 2 thread-local pre-attached context
    // unblocks the original NPE.
    //
    // Safe-primitive bodies: AOSP propagates via Locale.setDefault on the
    // first entry; we replicate that minimal contract.
    public static void setDefault(LocaleList locales) {
        if (locales != null && locales.size() > 0) {
            try {
                java.util.Locale.setDefault(locales.get(0));
            } catch (Throwable ignored) {
                // best-effort; bad Locale shouldn't crash the engine
            }
        }
    }

    public static void setDefault(LocaleList locales, int localeIndex) {
        if (locales != null && localeIndex >= 0 && localeIndex < locales.size()) {
            try {
                java.util.Locale.setDefault(locales.get(localeIndex));
            } catch (Throwable ignored) {
            }
        }
    }

    /** AOSP-compatible matchesLanguageAndScript stub; permissive return. */
    public boolean matchesLanguageAndScript(Locale supported) {
        if (supported == null || mList.length == 0) return false;
        String supportedLang = supported.getLanguage();
        if (supportedLang == null || supportedLang.isEmpty()) return false;
        for (Locale l : mList) {
            if (l != null && supportedLang.equals(l.getLanguage())) return true;
        }
        return false;
    }

    /** AOSP-compatible getFirstMatch stub; returns first locale or null. */
    public Locale getFirstMatch(String[] supportedLocales) {
        if (mList.length == 0) return null;
        return mList[0];
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof LocaleList)) return false;
        return java.util.Arrays.equals(mList, ((LocaleList) other).mList);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(mList);
    }

    /**
     * Constructs a LocaleList from a comma-separated string of BCP-47 language
     * tags (e.g. {@code "en-US,fr-FR"}).
     */
    public static LocaleList forLanguageTags(String list) {
        if (list == null || list.isEmpty()) return EMPTY_LOCALE_LIST;
        String[] tags = splitByChar(list, ',');
        Locale[] locales = new Locale[tags.length];
        for (int i = 0; i < tags.length; i++) {
            locales[i] = Locale.forLanguageTag(tags[i].trim());
        }
        return new LocaleList(locales);
    }

    private static String[] splitByChar(String s, char delim) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        int start = 0;
        for (int i = 0; i <= s.length(); i++) {
            if (i == s.length() || s.charAt(i) == delim) {
                parts.add(s.substring(start, i));
                start = i + 1;
            }
        }
        return parts.toArray(new String[0]);
    }
}
