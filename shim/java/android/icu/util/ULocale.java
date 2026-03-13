package android.icu.util;

import java.util.Locale;

/**
 * Android ICU ULocale shim. Wraps java.util.Locale.
 */
public final class ULocale {

    public static final ULocale ENGLISH  = new ULocale("en");
    public static final ULocale FRENCH   = new ULocale("fr");
    public static final ULocale GERMAN   = new ULocale("de");
    public static final ULocale JAPANESE = new ULocale("ja");
    public static final ULocale US       = new ULocale("en_US");
    public static final ULocale UK       = new ULocale("en_GB");
    public static final ULocale CANADA   = new ULocale("en_CA");
    public static final ULocale CHINA    = new ULocale("zh_CN");
    public static final ULocale JAPAN    = new ULocale("ja_JP");

    private final Locale locale;

    public ULocale(String localeID) {
        this.locale = parseLocaleID(localeID);
    }

    private ULocale(Locale locale) {
        this.locale = locale;
    }

    private static Locale parseLocaleID(String id) {
        if (id == null || id.isEmpty()) return Locale.getDefault();
        // Support BCP-47 tags and underscore-separated ids
        String[] parts = id.replace('-', '_').split("_");
        String lang    = parts.length > 0 ? parts[0] : "";
        String country = parts.length > 1 ? parts[1] : "";
        String variant = parts.length > 2 ? parts[2] : "";
        return new Locale(lang, country, variant);
    }

    // ---- Static factories ----

    public static ULocale forLanguageTag(String languageTag) {
        return new ULocale(Locale.forLanguageTag(languageTag));
    }

    public static ULocale getDefault() {
        return new ULocale(Locale.getDefault());
    }

    public static ULocale[] getAvailableLocales() {
        Locale[] jLocales = Locale.getAvailableLocales();
        ULocale[] result = new ULocale[jLocales.length];
        for (int i = 0; i < jLocales.length; i++) {
            result[i] = new ULocale(jLocales[i]);
        }
        return result;
    }

    // ---- Instance methods ----

    public String getLanguage() {
        return locale.getLanguage();
    }

    public String getCountry() {
        return locale.getCountry();
    }

    public String getScript() {
        return locale.getScript();
    }

    public String getVariant() {
        return locale.getVariant();
    }

    public String getName() {
        StringBuilder sb = new StringBuilder(locale.getLanguage());
        if (!locale.getCountry().isEmpty()) sb.append('_').append(locale.getCountry());
        if (!locale.getVariant().isEmpty()) sb.append('_').append(locale.getVariant());
        return sb.toString();
    }

    public String toLanguageTag() {
        return locale.toLanguageTag();
    }

    public String getDisplayName() {
        return locale.getDisplayName();
    }

    /** Returns the underlying java.util.Locale for interop. */
    public Locale toLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ULocale)) return false;
        return locale.equals(((ULocale) obj).locale);
    }

    @Override
    public int hashCode() {
        return locale.hashCode();
    }
}
