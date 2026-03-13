package android.drm;

/**
 * Android-compatible DrmInfo shim. Stub — stores info type, data, and MIME type.
 */
public class DrmInfo {
    private final int mInfoType;
    private final byte[] mData;
    private final String mMimeType;

    public DrmInfo(int infoType, byte[] data, String mimeType) {
        mInfoType = infoType;
        mData = data;
        mMimeType = mimeType;
    }

    public int getInfoType() {
        return mInfoType;
    }

    public byte[] getData() {
        return mData;
    }

    public String getMimeType() {
        return mMimeType;
    }
}
