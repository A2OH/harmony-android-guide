package android.icu.util;
import java.util.Date;
import java.util.Locale;

public class GregorianCalendar extends Calendar {
    public static final int AD = 0;
    public static final int BC = 0;
    public int invertGregorian = 0;
    public int isGregorian = 0;

    public GregorianCalendar() {}
    public GregorianCalendar(TimeZone p0) {}
    public GregorianCalendar(Locale p0) {}
    public GregorianCalendar(ULocale p0) {}
    public GregorianCalendar(TimeZone p0, Locale p1) {}
    public GregorianCalendar(TimeZone p0, ULocale p1) {}
    public GregorianCalendar(int p0, int p1, int p2) {}
    public GregorianCalendar(int p0, int p1, int p2, int p3, int p4) {}
    public GregorianCalendar(int p0, int p1, int p2, int p3, int p4, int p5) {}

    public Date getGregorianChange() { return null; }
    public int handleComputeMonthStart(int p0, int p1, boolean p2) { return 0; }
    public int handleGetExtendedYear() { return 0; }
    public int handleGetLimit(int p0, int p1) { return 0; }
    public boolean isLeapYear(int p0) { return false; }
    public void setGregorianChange(Date p0) {}
}
