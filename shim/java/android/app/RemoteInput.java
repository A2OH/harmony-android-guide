package android.app;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Map;
import java.util.Set;

public final class RemoteInput implements Parcelable {
    public static final int EDIT_CHOICES_BEFORE_SENDING_AUTO = 0;
    public static final int EDIT_CHOICES_BEFORE_SENDING_DISABLED = 0;
    public static final int EDIT_CHOICES_BEFORE_SENDING_ENABLED = 0;
    public static final int EXTRA_RESULTS_DATA = 0;
    public static final int RESULTS_CLIP_LABEL = 0;
    public static final int SOURCE_CHOICE = 0;
    public static final int SOURCE_FREE_FORM_INPUT = 0;

    public RemoteInput() {}

    public static void addDataResultToIntent(RemoteInput p0, Intent p1, java.util.Map<Object,Object> p2) {}
    public static void addResultsToIntent(RemoteInput[] p0, Intent p1, Bundle p2) {}
    public int describeContents() { return 0; }
    public boolean getAllowFreeFormInput() { return false; }
    public Set<?> getAllowedDataTypes() { return null; }
    public CharSequence[] getChoices() { return null; }
    public static Map<?,?> getDataResultsFromIntent(Intent p0, String p1) { return null; }
    public int getEditChoicesBeforeSending() { return 0; }
    public Bundle getExtras() { return null; }
    public CharSequence getLabel() { return null; }
    public String getResultKey() { return null; }
    public static Bundle getResultsFromIntent(Intent p0) { return null; }
    public static int getResultsSource(Intent p0) { return 0; }
    public boolean isDataOnly() { return false; }
    public static void setResultsSource(Intent p0, int p1) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
