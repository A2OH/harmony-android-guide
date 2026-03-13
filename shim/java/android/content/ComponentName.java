package android.content;

import android.os.Parcel;
import android.os.Parcelable;

public final class ComponentName implements Parcelable, Comparable<ComponentName> {

    private final String mPackage;
    private final String mClass;

    public ComponentName(String pkg, String cls) {
        mPackage = pkg;
        mClass = cls;
    }

    public ComponentName(Context pkg, String cls) {
        mPackage = pkg != null ? pkg.getPackageName() : "";
        mClass = cls;
    }

    public ComponentName(Context pkg, Class<?> cls) {
        mPackage = pkg != null ? pkg.getPackageName() : "";
        mClass = cls.getName();
    }

    /** Unflatten from short string "pkg/cls" */
    public static ComponentName unflattenFromString(String str) {
        if (str == null) return null;
        int sep = str.indexOf('/');
        if (sep < 0) return null;
        String pkg = str.substring(0, sep);
        String cls = str.substring(sep + 1);
        if (cls.length() > 0 && cls.charAt(0) == '.') {
            cls = pkg + cls;
        }
        return new ComponentName(pkg, cls);
    }

    public String getPackageName() { return mPackage; }
    public String getClassName() { return mClass; }

    public String getShortClassName() {
        if (mClass.startsWith(mPackage)) {
            int pn = mPackage.length();
            if (pn < mClass.length() && mClass.charAt(pn) == '.') {
                return mClass.substring(pn);
            }
        }
        return mClass;
    }

    public String flattenToString() {
        return mPackage + "/" + mClass;
    }

    public String flattenToShortString() {
        return mPackage + "/" + getShortClassName();
    }

    public String toShortString() {
        return "{" + flattenToShortString() + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComponentName) {
            ComponentName o = (ComponentName) obj;
            return mPackage.equals(o.mPackage) && mClass.equals(o.mClass);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mPackage.hashCode() + mClass.hashCode();
    }

    @Override
    public int compareTo(ComponentName that) {
        int v = this.mPackage.compareTo(that.mPackage);
        if (v != 0) return v;
        return this.mClass.compareTo(that.mClass);
    }

    @Override
    public String toString() {
        return "ComponentName{" + flattenToShortString() + "}";
    }

    /* Parcelable stubs */
    public int describeContents() { return 0; }
    public void writeToParcel(Parcel dest, int flags) {}
    public static ComponentName readFromParcel(Parcel in) { return null; }
}
