package android.media.tv;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public final class TvTrackInfo implements Parcelable {
    public static final int TYPE_AUDIO = 0;
    public static final int TYPE_SUBTITLE = 0;
    public static final int TYPE_VIDEO = 0;

    public TvTrackInfo() {}

    public int describeContents() { return 0; }
    public int getAudioChannelCount() { return 0; }
    public int getAudioSampleRate() { return 0; }
    public CharSequence getDescription() { return null; }
    public Bundle getExtra() { return null; }
    public String getId() { return null; }
    public String getLanguage() { return null; }
    public int getType() { return 0; }
    public byte getVideoActiveFormatDescription() { return 0; }
    public float getVideoFrameRate() { return 0f; }
    public int getVideoHeight() { return 0; }
    public float getVideoPixelAspectRatio() { return 0f; }
    public int getVideoWidth() { return 0; }
    public boolean isAudioDescription() { return false; }
    public boolean isEncrypted() { return false; }
    public boolean isHardOfHearing() { return false; }
    public boolean isSpokenSubtitle() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
