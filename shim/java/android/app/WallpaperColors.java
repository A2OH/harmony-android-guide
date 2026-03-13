package android.app;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public final class WallpaperColors implements Parcelable {
    public WallpaperColors(Parcel p0) {}
    public WallpaperColors(Color p0, Color p1, Color p2) {}

    public int describeContents() { return 0; }
    public static WallpaperColors fromBitmap(Bitmap p0) { return null; }
    public static WallpaperColors fromDrawable(Drawable p0) { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
