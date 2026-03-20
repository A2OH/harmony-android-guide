package android.graphics.drawable;
import android.graphics.Bitmap;
import android.graphics.Bitmap;

import android.graphics.Bitmap;

/**
 * Android-compatible Icon shim. Represents an icon that can be used for notifications
 * and other UI elements. Supports creation from resources, bitmaps, and file paths.
 */
public class Icon implements android.os.Parcelable {
    private static final int TYPE_RESOURCE = 1;
    private static final int TYPE_BITMAP = 2;
    private static final int TYPE_FILE = 3;

    private int mType;
    private int mResId;
    private String mResPackage;
    private Bitmap mBitmap;
    private String mFilePath;

    private Icon(int type) {
        mType = type;
    }

    /**
     * Create an Icon from a resource ID.
     * @param resPackage package name for the resource
     * @param resId      resource identifier
     * @return a new Icon instance
     */
    public static Icon createWithResource(String resPackage, int resId) {
        Icon icon = new Icon(TYPE_RESOURCE);
        icon.mResPackage = resPackage;
        icon.mResId = resId;
        return icon;
    }

    /**
     * Create an Icon from a Bitmap.
     * @param bitmap the bitmap to use
     * @return a new Icon instance
     */
    public static Icon createWithBitmap(Bitmap bitmap) {
        Icon icon = new Icon(TYPE_BITMAP);
        icon.mBitmap = bitmap;
        return icon;
    }

    /**
     * Create an Icon from a file path.
     * @param path file path to an image
     * @return a new Icon instance
     */
    public static Icon createWithFilePath(String path) {
        Icon icon = new Icon(TYPE_FILE);
        icon.mFilePath = path;
        return icon;
    }

    /**
     * Load and return a Drawable representation of this Icon.
     * In this shim, returns null as no actual rendering is available.
     * @return a Drawable, or null
     */
    public Drawable loadDrawable() {
        return null;
    }

    public Drawable loadDrawable(android.content.Context context) {
        return null;
    }

    public int getType() { return mType; }
    public int getResId() { return mResId; }
    public String getResPackage() { return mResPackage; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {}

    public static final android.os.Parcelable.Creator<Icon> CREATOR =
            new android.os.Parcelable.Creator<Icon>() {
                public Icon createFromParcel(android.os.Parcel in) { return null; }
                public Icon[] newArray(int size) { return new Icon[size]; }
            };
}
