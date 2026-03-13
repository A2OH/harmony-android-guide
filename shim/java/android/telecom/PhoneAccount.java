package android.telecom;

/**
 * Android-compatible PhoneAccount shim. Stub implementation for mock testing.
 */
public class PhoneAccount {

    /** This phone account can make and receive standard PSTN calls. */
    public static final int CAPABILITY_CALL_PROVIDER = 0x1;
    /** This phone account corresponds to a SIM subscription. */
    public static final int CAPABILITY_SIM_SUBSCRIPTION = 0x4;

    /** URI scheme for telephone numbers (tel:). */
    public static final String SCHEME_TEL = "tel";
    /** URI scheme for SIP addresses (sip:). */
    public static final String SCHEME_SIP = "sip";

    private final PhoneAccountHandle mAccountHandle;
    private final Object mAddress; // android.net.Uri (stub as Object)
    private final CharSequence mLabel;
    private final int mCapabilities;

    private PhoneAccount(Builder builder) {
        mAccountHandle = builder.mAccountHandle;
        mAddress = builder.mAddress;
        mLabel = builder.mLabel;
        mCapabilities = builder.mCapabilities;
    }

    /** Returns the handle that identifies this account. */
    public PhoneAccountHandle getAccountHandle() {
        return mAccountHandle;
    }

    /** Returns the address (URI) of this account (returned as Object). */
    public Object getAddress() {
        return mAddress;
    }

    /** Returns the label for this account. */
    public CharSequence getLabel() {
        return mLabel;
    }

    /** Returns the bitfield of capabilities for this account. */
    public int getCapabilities() {
        return mCapabilities;
    }

    /** Returns true if this account has all of the specified capabilities. */
    public boolean hasCapabilities(int capabilities) {
        return (mCapabilities & capabilities) == capabilities;
    }

    @Override
    public String toString() {
        return "PhoneAccount{handle=" + mAccountHandle + ", label=" + mLabel
                + ", capabilities=0x" + Integer.toHexString(mCapabilities) + "}";
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    /** Builder for constructing PhoneAccount instances. */
    public static final class Builder {
        private final PhoneAccountHandle mAccountHandle;
        private final CharSequence mLabel;
        private Object mAddress;
        private int mCapabilities;

        public Builder(PhoneAccountHandle accountHandle, CharSequence label) {
            mAccountHandle = accountHandle;
            mLabel = label;
        }

        /** Sets the address (URI) for this account. */
        public Builder setAddress(Object address) {
            mAddress = address;
            return this;
        }

        /** Sets the capabilities bitfield. */
        public Builder setCapabilities(int capabilities) {
            mCapabilities = capabilities;
            return this;
        }

        /** Adds a single capability flag. */
        public Builder addCapability(int capability) {
            mCapabilities |= capability;
            return this;
        }

        /** Builds and returns the PhoneAccount. */
        public PhoneAccount build() {
            return new PhoneAccount(this);
        }
    }
}
