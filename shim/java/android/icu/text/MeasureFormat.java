package android.icu.text;

import java.util.Locale;

public class MeasureFormat {
    public MeasureFormat() {}

    public enum FormatWidth {
        WIDE,
        SHORT,
        NARROW,
        NUMERIC;
    }

    public boolean equals(Object p0) { return false; }
    public Object format(Object p0, Object p1, Object p2) { return null; }
    public Object formatMeasurePerUnit(Object p0, Object p1, Object p2, Object p3) { return null; }
    public String formatMeasures(android.icu.util.Measure... measures) { return ""; }
    public static MeasureFormat getCurrencyFormat(Locale locale) { return new MeasureFormat(); }
    public static MeasureFormat getCurrencyFormat() { return new MeasureFormat(); }
    public static MeasureFormat getInstance(Locale locale, FormatWidth formatWidth) { return new MeasureFormat(); }
    public static MeasureFormat getInstance(Locale locale, FormatWidth formatWidth, Object numberFormat) { return new MeasureFormat(); }
    public Object getLocale() { return null; }
    public Object getNumberFormat() { return null; }
    public Object getUnitDisplayName(Object p0) { return null; }
    public FormatWidth getWidth() { return null; }
    public int hashCode() { return 0; }
    public Object parseObject(Object p0, Object p1) { return null; }
}
