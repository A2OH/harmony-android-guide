package android.view;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class SurfaceControl implements Parcelable {
    public SurfaceControl() {}

    public int describeContents() { return 0; }
    public boolean isValid() { return false; }
    public void readFromParcel(Parcel p0) {}
    public void release() {}
    public void writeToParcel(Parcel p0, int p1) {}

    public static class Builder {
        public Builder(SurfaceSession session) {}
        public Builder() {}
        public Builder setName(String name) { return this; }
        public Builder setBufferSize(int width, int height) { return this; }
        public Builder setFormat(int format) { return this; }
        public Builder setParent(SurfaceControl parent) { return this; }
        public Builder setFlags(int flags) { return this; }
        public Builder setCallsite(String callsite) { return this; }
        public SurfaceControl build() { return new SurfaceControl(); }
    }
}
