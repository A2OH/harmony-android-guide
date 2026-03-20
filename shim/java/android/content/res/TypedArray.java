package android.content.res;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable;

public class TypedArray {
    public TypedArray() {}
    public TypedArray(Resources res, int[] data, int[] attrs, int len) {}

    public String getString(int index) {
        return null;
    }

    public int getInt(int index, int defValue) {
        return defValue;
    }

    public float getFloat(int index, float defValue) {
        return defValue;
    }

    public boolean getBoolean(int index, boolean defValue) {
        return defValue;
    }

    public int getColor(int index, int defValue) {
        return defValue;
    }

    public float getDimension(int index, float defValue) {
        return defValue;
    }

    public int getDimensionPixelSize(int index, int defValue) {
        return defValue;
    }

    public android.graphics.drawable.Drawable getDrawable(int index) {
        return null;
    }

    public int getResourceId(int index, int defValue) {
        return defValue;
    }

    public int getInteger(int index, int defValue) {
        return defValue;
    }

    public CharSequence getText(int index) {
        return null;
    }

    public int length() {
        return 0;
    }

    public void recycle() {
        // no-op
    }

    public boolean hasValue(int index) {
        return false;
    }

    public int getDimensionPixelOffset(int index, int defValue) {
        return defValue;
    }

    public int getLayoutDimension(int index, int defValue) {
        return defValue;
    }

    public int getLayoutDimension(int index, String name) {
        return 0;
    }

    public int getChangingConfigurations() {
        return 0;
    }

    public int getIndexCount() {
        return 0;
    }

    public int getIndex(int at) {
        return 0;
    }

    public int getType(int index) {
        return 0;
    }

    public boolean hasValueOrEmpty(int index) {
        return false;
    }

    public CharSequence[] getTextArray(int index) {
        return null;
    }

    public android.content.res.ColorStateList getColorStateList(int index) {
        return null;
    }

    public float getFraction(int index, int base, int pbase, float defValue) {
        return defValue;
    }

    public android.util.TypedValue peekValue(int index) {
        return null;
    }

    public android.graphics.Typeface getFont(int index) { return null; }
    public Resources getResources() { return new Resources(); }
    public int getSourceResourceId(int index, int defValue) { return defValue; }
    public boolean getValue(int index, android.util.TypedValue outValue) { return false; }
    public String getPositionDescription() { return ""; }
    public int[] extractThemeAttrs() { return null; }
    public String getNonResourceString(int index) { return null; }
}
