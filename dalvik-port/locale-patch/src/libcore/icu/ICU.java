// Stub for javac compile-time resolution only. At runtime, the real
// libcore.icu.ICU class from core-android-x86.jar's classes.dex is loaded
// (its classes.dex precedes our locale-patch.dex's reference, so the
// existing field-and-method bytecode in Locale.class resolves against the
// real ICU's methods by name+descriptor). This stub is NEVER loaded — d8
// emits Locale.class bytecode referencing Llibcore/icu/ICU; method ids that
// the runtime resolves against core-android-x86.jar's ICU at class link.
//
// CR-Y+1 (2026-05-15): minimal compile-only surface — just the signatures
// the kitkat Locale.java references.
package libcore.icu;

import java.util.Locale;

public final class ICU {
    private ICU() {}

    public static Locale[] getAvailableLocales() { return new Locale[] { Locale.US }; }
    public static String[] getISOCountries() { return new String[0]; }
    public static String[] getISOLanguages() { return new String[0]; }
    public static String getDisplayCountryNative(String localeName, String displayLocaleName) { return ""; }
    public static String getDisplayLanguageNative(String localeName, String displayLocaleName) { return ""; }
    public static String getDisplayVariantNative(String localeName, String displayLocaleName) { return ""; }
    public static String getISO3CountryNative(String localeName) { return ""; }
    public static String getISO3LanguageNative(String localeName) { return ""; }
}
