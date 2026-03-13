package android.nfc;

import java.util.Arrays;

/**
 * Android-compatible Tag shim. Stub for A2OH migration.
 */
public final class Tag {

    private final byte[]   mId;
    private final String[] mTechList;

    /** Package-private constructor — callers inside the NFC stack create Tags. */
    Tag(byte[] id, String[] techList) {
        mId       = id       != null ? Arrays.copyOf(id,       id.length)       : new byte[0];
        mTechList = techList != null ? Arrays.copyOf(techList, techList.length) : new String[0];
    }

    public byte[] getId() {
        return Arrays.copyOf(mId, mId.length);
    }

    public String[] getTechList() {
        return Arrays.copyOf(mTechList, mTechList.length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Tag[id=");
        for (byte b : mId) sb.append(String.format("%02X", b));
        sb.append(" techs=").append(Arrays.toString(mTechList)).append("]");
        return sb.toString();
    }
}
