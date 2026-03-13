package android.bluetooth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Android-compatible BluetoothGattCharacteristic shim. Stub.
 */
public class BluetoothGattCharacteristic {

    public static final int PROPERTY_READ        = 0x02;
    public static final int PROPERTY_WRITE       = 0x08;
    public static final int PROPERTY_NOTIFY      = 0x10;
    public static final int PROPERTY_INDICATE    = 0x20;

    public static final int FORMAT_UINT8         = 0x11;
    public static final int FORMAT_UINT16        = 0x12;
    public static final int FORMAT_SINT8         = 0x21;
    public static final int FORMAT_SINT16        = 0x22;
    public static final int FORMAT_FLOAT         = 0x34;

    public static final int PERMISSION_READ      = 0x01;
    public static final int PERMISSION_WRITE     = 0x10;

    public static final int WRITE_TYPE_DEFAULT      = 0x02;
    public static final int WRITE_TYPE_NO_RESPONSE  = 0x01;

    private final UUID mUuid;
    private final int mProperties;
    private final int mPermissions;
    private byte[] mValue;
    private BluetoothGattService mService;
    private final List<BluetoothGattDescriptor> mDescriptors = new ArrayList<>();

    public BluetoothGattCharacteristic(UUID uuid, int properties, int permissions) {
        mUuid = uuid;
        mProperties = properties;
        mPermissions = permissions;
    }

    public UUID getUuid() { return mUuid; }

    public int getProperties() { return mProperties; }

    public int getPermissions() { return mPermissions; }

    public byte[] getValue() { return mValue; }

    public boolean setValue(byte[] value) {
        mValue = value;
        return true;
    }

    public boolean setValue(int value, int formatType, int offset) {
        int len = offset;
        if (formatType == FORMAT_UINT8 || formatType == FORMAT_SINT8) {
            len += 1;
        } else if (formatType == FORMAT_UINT16 || formatType == FORMAT_SINT16) {
            len += 2;
        } else if (formatType == FORMAT_FLOAT) {
            len += 4;
        } else {
            len += 4;
        }
        if (mValue == null || mValue.length < len) {
            mValue = new byte[len];
        }
        if (formatType == FORMAT_UINT8 || formatType == FORMAT_SINT8) {
            mValue[offset] = (byte) (value & 0xFF);
        } else if (formatType == FORMAT_UINT16 || formatType == FORMAT_SINT16) {
            mValue[offset]     = (byte) (value & 0xFF);
            mValue[offset + 1] = (byte) ((value >> 8) & 0xFF);
        } else {
            mValue[offset]     = (byte) (value & 0xFF);
            mValue[offset + 1] = (byte) ((value >> 8)  & 0xFF);
            mValue[offset + 2] = (byte) ((value >> 16) & 0xFF);
            mValue[offset + 3] = (byte) ((value >> 24) & 0xFF);
        }
        return true;
    }

    public Integer getIntValue(int formatType, int offset) {
        if (mValue == null || mValue.length <= offset) return null;
        switch (formatType) {
            case FORMAT_UINT8:
                return mValue[offset] & 0xFF;
            case FORMAT_SINT8:
                return (int) mValue[offset];
            case FORMAT_UINT16:
                if (mValue.length < offset + 2) return null;
                return (mValue[offset] & 0xFF) | ((mValue[offset + 1] & 0xFF) << 8);
            case FORMAT_SINT16:
                if (mValue.length < offset + 2) return null;
                return (int)(short) ((mValue[offset] & 0xFF) | ((mValue[offset + 1] & 0xFF) << 8));
            default:
                return null;
        }
    }

    public Float getFloatValue(int formatType, int offset) {
        if (mValue == null || mValue.length < offset + 4) return null;
        int bits = (mValue[offset] & 0xFF)
                 | ((mValue[offset + 1] & 0xFF) << 8)
                 | ((mValue[offset + 2] & 0xFF) << 16)
                 | ((mValue[offset + 3] & 0xFF) << 24);
        return Float.intBitsToFloat(bits);
    }

    public String getStringValue(int offset) {
        if (mValue == null || mValue.length <= offset) return null;
        return new String(mValue, offset, mValue.length - offset);
    }

    public List<BluetoothGattDescriptor> getDescriptors() {
        return mDescriptors;
    }

    public BluetoothGattDescriptor getDescriptor(UUID uuid) {
        for (BluetoothGattDescriptor d : mDescriptors) {
            if (uuid.equals(d.getUuid())) return d;
        }
        return null;
    }

    public boolean addDescriptor(BluetoothGattDescriptor descriptor) {
        return mDescriptors.add(descriptor);
    }

    public BluetoothGattService getService() { return mService; }

    /** Package-private setter used by BluetoothGattService. */
    void setService(BluetoothGattService service) { mService = service; }
}
