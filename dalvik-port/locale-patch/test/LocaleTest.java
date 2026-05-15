// CR-Y+1: host-side dalvikvm smoke test for the patched java.util.Locale.
// Validates additive contract: existing API still works, new BCP-47 methods
// parse/emit per RFC 5646 prefix subset.
public class LocaleTest {
    static int failures = 0;
    static void assertEq(String tag, Object actual, Object expected) {
        String a = String.valueOf(actual);
        String e = String.valueOf(expected);
        if (!a.equals(e)) {
            System.out.println("FAIL " + tag + " expected=" + e + " actual=" + a);
            failures++;
        } else {
            System.out.println("OK   " + tag + " -> " + a);
        }
    }
    public static void main(String[] args) {
        // ---- Existing API preserved ----
        java.util.Locale us = java.util.Locale.US;
        assertEq("Locale.US.getLanguage", us.getLanguage(), "en");
        assertEq("Locale.US.getCountry", us.getCountry(), "US");
        assertEq("Locale.US.toString", us.toString(), "en_US");
        assertEq("Locale.ROOT.toString", java.util.Locale.ROOT.toString(), "");
        assertEq("new Locale(en,US,POSIX).toString", new java.util.Locale("en","US","POSIX").toString(), "en_US_POSIX");
        // ---- New: toLanguageTag ----
        assertEq("Locale.US.toLanguageTag", java.util.Locale.US.toLanguageTag(), "en-US");
        assertEq("Locale.ROOT.toLanguageTag", java.util.Locale.ROOT.toLanguageTag(), "und");
        assertEq("Locale(en).toLanguageTag", new java.util.Locale("en").toLanguageTag(), "en");
        assertEq("Locale(zh,CN).toLanguageTag", new java.util.Locale("zh","CN").toLanguageTag(), "zh-CN");
        // Hebrew round-trip: storage uses "iw" (per Java legacy), BCP-47 emits "he"
        assertEq("Locale(he).toLanguageTag", new java.util.Locale("he","IL").toLanguageTag(), "he-IL");
        // ---- New: forLanguageTag ----
        java.util.Locale enUS = java.util.Locale.forLanguageTag("en-US");
        assertEq("forLanguageTag(en-US).lang", enUS.getLanguage(), "en");
        assertEq("forLanguageTag(en-US).country", enUS.getCountry(), "US");
        java.util.Locale zhCN = java.util.Locale.forLanguageTag("zh-CN");
        assertEq("forLanguageTag(zh-CN).lang", zhCN.getLanguage(), "zh");
        assertEq("forLanguageTag(zh-CN).country", zhCN.getCountry(), "CN");
        // Script: zh-Hant-TW — script "Hant" gets swallowed, region "TW" kept
        java.util.Locale zhTW = java.util.Locale.forLanguageTag("zh-Hant-TW");
        assertEq("forLanguageTag(zh-Hant-TW).lang", zhTW.getLanguage(), "zh");
        assertEq("forLanguageTag(zh-Hant-TW).country", zhTW.getCountry(), "TW");
        // Empty/malformed
        assertEq("forLanguageTag(empty)", java.util.Locale.forLanguageTag(""), java.util.Locale.ROOT);
        assertEq("forLanguageTag(null)", java.util.Locale.forLanguageTag(null), java.util.Locale.ROOT);
        assertEq("forLanguageTag(und)", java.util.Locale.forLanguageTag("und"), java.util.Locale.ROOT);
        // Round-trip
        java.util.Locale rt = java.util.Locale.forLanguageTag(java.util.Locale.JAPAN.toLanguageTag());
        assertEq("JAPAN roundtrip lang", rt.getLanguage(), "ja");
        assertEq("JAPAN roundtrip country", rt.getCountry(), "JP");
        // Extension swallow: en-US-u-ca-gregory
        java.util.Locale enUSExt = java.util.Locale.forLanguageTag("en-US-u-ca-gregory");
        assertEq("forLanguageTag(en-US-u-ca-gregory).lang", enUSExt.getLanguage(), "en");
        assertEq("forLanguageTag(en-US-u-ca-gregory).country", enUSExt.getCountry(), "US");
        // Underscore tolerance: AOSP accepts "en_US" too
        java.util.Locale enUSu = java.util.Locale.forLanguageTag("en_US");
        assertEq("forLanguageTag(en_US).lang", enUSu.getLanguage(), "en");
        assertEq("forLanguageTag(en_US).country", enUSu.getCountry(), "US");

        if (failures > 0) {
            System.out.println("RESULT: " + failures + " FAILURE(S)");
            System.exit(1);
        } else {
            System.out.println("RESULT: ALL PASS");
        }
    }
}
