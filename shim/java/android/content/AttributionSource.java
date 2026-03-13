package android.content;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Android-compatible AttributionSource shim (API 31+).
 *
 * An attribution source is a chain of sources that caused an API call —
 * used for permission-checking context propagation across process boundaries.
 *
 * Pure Java stub — returns safe defaults (0 / null) for all accessors.
 */
public final class AttributionSource implements Parcelable {

    private final int mUid;
    private final int mPid;
    private final String mPackageName;
    private final String mAttributionTag;
    private final AttributionSource mNext;

    private AttributionSource(int uid, int pid, String packageName,
                              String attributionTag, AttributionSource next) {
        mUid = uid;
        mPid = pid;
        mPackageName = packageName;
        mAttributionTag = attributionTag;
        mNext = next;
    }

    // ── Accessors ──

    /** Returns the UID of the attribution source, or 0 in this stub. */
    public int getUid() {
        return mUid;
    }

    /** Returns the PID of the attribution source, or 0 in this stub. */
    public int getPid() {
        return mPid;
    }

    /** Returns the package name of the attribution source, or null in this stub. */
    public String getPackageName() {
        return mPackageName;
    }

    /** Returns the attribution tag, or null in this stub. */
    public String getAttributionTag() {
        return mAttributionTag;
    }

    /** Returns the next attribution source in the chain, or null in this stub. */
    public AttributionSource getNext() {
        return mNext;
    }

    // ── Parcelable ──

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // stub — no-op
    }

    public static final Parcelable.Creator<AttributionSource> CREATOR =
            new Parcelable.Creator<AttributionSource>() {
                @Override
                public AttributionSource createFromParcel(Parcel source) {
                    return new AttributionSource(0, 0, null, null, null);
                }

                @Override
                public AttributionSource[] newArray(int size) {
                    return new AttributionSource[size];
                }
            };

    // ── Builder ──

    /** Builder for {@link AttributionSource}. */
    public static final class Builder {

        private int mUid;
        private int mPid;
        private String mPackageName;
        private String mAttributionTag;

        public Builder() {
        }

        /** Sets the UID. */
        public Builder setUid(int uid) {
            mUid = uid;
            return this;
        }

        /** Sets the PID. */
        public Builder setPid(int pid) {
            mPid = pid;
            return this;
        }

        /** Sets the package name. */
        public Builder setPackageName(String packageName) {
            mPackageName = packageName;
            return this;
        }

        /** Sets the attribution tag. */
        public Builder setAttributionTag(String attributionTag) {
            mAttributionTag = attributionTag;
            return this;
        }

        /** Builds the {@link AttributionSource}. */
        public AttributionSource build() {
            return new AttributionSource(mUid, mPid, mPackageName, mAttributionTag, null);
        }
    }
}
