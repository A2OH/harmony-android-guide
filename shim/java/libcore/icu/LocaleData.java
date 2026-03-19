package libcore.icu;

import java.util.Locale;

/** Stub for AOSP libcore LocaleData used by NumberPicker. */
public class LocaleData {
    public char zeroDigit = '0';

    public LocaleData() {}

    public static LocaleData get(Locale locale) {
        return new LocaleData();
    }
}
