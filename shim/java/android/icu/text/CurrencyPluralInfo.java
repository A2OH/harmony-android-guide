package android.icu.text;
import android.icu.util.ULocale;
import android.icu.util.ULocale;
import java.io.Serializable;
import java.util.Locale;

public class CurrencyPluralInfo implements Cloneable, Serializable {
    public CurrencyPluralInfo() {}
    public CurrencyPluralInfo(Locale p0) {}
    public CurrencyPluralInfo(ULocale p0) {}

    public Object clone() { return null; }
    public String getCurrencyPluralPattern(String p0) { return null; }
    public static CurrencyPluralInfo getInstance() { return null; }
    public static CurrencyPluralInfo getInstance(Locale p0) { return null; }
    public ULocale getLocale() { return null; }
    public PluralRules getPluralRules() { return null; }
    public void setCurrencyPluralPattern(String p0, String p1) {}
    public void setLocale(ULocale p0) {}
    public void setPluralRules(String p0) {}
}
