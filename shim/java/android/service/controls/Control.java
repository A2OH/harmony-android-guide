package android.service.controls;

/**
 * Android-compatible Control shim. Stub for a home-device control.
 */
public final class Control {

    public static final int STATUS_UNKNOWN   = 0;
    public static final int STATUS_OK        = 1;
    public static final int STATUS_NOT_FOUND = 2;
    public static final int STATUS_ERROR     = 3;
    public static final int STATUS_DISABLED  = 4;

    private final String mControlId;
    private final CharSequence mTitle;
    private final CharSequence mSubtitle;
    private final int mDeviceType;
    private final int mStatus;

    private Control(Builder b) {
        mControlId  = b.mControlId;
        mTitle      = b.mTitle;
        mSubtitle   = b.mSubtitle;
        mDeviceType = b.mDeviceType;
        mStatus     = b.mStatus;
    }

    public String      getControlId()  { return mControlId;  }
    public CharSequence getTitle()     { return mTitle;      }
    public CharSequence getSubtitle()  { return mSubtitle;   }
    public int         getDeviceType() { return mDeviceType; }
    public int         getStatus()     { return mStatus;     }

    public static final class Builder {
        private final String mControlId;
        private CharSequence mTitle    = "";
        private CharSequence mSubtitle = "";
        private int mDeviceType        = DeviceTypes.TYPE_UNKNOWN;
        private int mStatus            = STATUS_UNKNOWN;

        public Builder(String controlId) {
            mControlId = controlId != null ? controlId : "";
        }

        public Builder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public Builder setSubtitle(CharSequence subtitle) {
            mSubtitle = subtitle;
            return this;
        }

        public Builder setDeviceType(int deviceType) {
            mDeviceType = deviceType;
            return this;
        }

        public Builder setStatus(int status) {
            mStatus = status;
            return this;
        }

        public Control build() {
            return new Control(this);
        }
    }
}
