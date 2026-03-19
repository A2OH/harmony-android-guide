package android.text.style;
import android.content.Context;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import java.util.Locale;

public class SuggestionSpan extends CharacterStyle implements ParcelableSpan {
    public static final int FLAG_AUTO_CORRECTION = 0;
    public static final int FLAG_EASY_CORRECT = 0;
    public static final int FLAG_MISSPELLED = 0;
    public static final int SUGGESTIONS_MAX_SIZE = 0;

    public SuggestionSpan(Context p0, String[] p1, int p2) {}
    public SuggestionSpan(Locale p0, String[] p1, int p2) {}
    public SuggestionSpan(Context p0, Locale p1, String[] p2, int p3, Object p4) {}
    public SuggestionSpan(Parcel p0) {}

    public int describeContents() { return 0; }
    public int getFlags() { return 0; }
    public int getSpanTypeId() { return 0; }
    public String[] getSuggestions() { return null; }
    public void setFlags(int p0) {}
    public void updateDrawState(TextPaint p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
    public int getUnderlineColor() { return 0; }
}
