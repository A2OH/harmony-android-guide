package android.app.assist;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class AssistContent implements Parcelable {
    public AssistContent() {}

    public int describeContents() { return 0; }
    public ClipData getClipData() { return null; }
    public Bundle getExtras() { return null; }
    public Intent getIntent() { return null; }
    public String getStructuredData() { return null; }
    public Uri getWebUri() { return null; }
    public boolean isAppProvidedIntent() { return false; }
    public boolean isAppProvidedWebUri() { return false; }
    public void setClipData(ClipData p0) {}
    public void setIntent(Intent p0) {}
    public void setStructuredData(String p0) {}
    public void setWebUri(Uri p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
