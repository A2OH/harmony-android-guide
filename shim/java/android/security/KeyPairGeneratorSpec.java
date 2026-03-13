package android.security;

import java.math.BigInteger;
import java.util.Date;

/**
 * Android-compatible KeyPairGeneratorSpec shim.
 *
 * @deprecated Use {@code android.security.keystore.KeyGenParameterSpec} instead.
 */
@Deprecated
public final class KeyPairGeneratorSpec {

    private final Object mContext;
    private final String mKeystoreAlias;
    private final String mSubject;
    private final BigInteger mSerialNumber;
    private final Date mStartDate;
    private final Date mEndDate;

    private KeyPairGeneratorSpec(Builder builder) {
        mContext        = builder.mContext;
        mKeystoreAlias  = builder.mKeystoreAlias;
        mSubject        = builder.mSubject;
        mSerialNumber   = builder.mSerialNumber;
        mStartDate      = builder.mStartDate;
        mEndDate        = builder.mEndDate;
    }

    public Object getContext() {
        return mContext;
    }

    public String getKeystoreAlias() {
        return mKeystoreAlias;
    }

    public String getSubject() {
        return mSubject;
    }

    public BigInteger getSerialNumber() {
        return mSerialNumber;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    /**
     * Builder for {@link KeyPairGeneratorSpec}.
     *
     * @deprecated Use {@code KeyGenParameterSpec.Builder} instead.
     */
    @Deprecated
    public static final class Builder {
        private final Object mContext;
        private String mKeystoreAlias;
        private String mSubject;
        private BigInteger mSerialNumber;
        private Date mStartDate;
        private Date mEndDate;

        public Builder(Object context) {
            if (context == null) throw new NullPointerException("context == null");
            mContext = context;
        }

        public Builder setAlias(String alias) {
            mKeystoreAlias = alias;
            return this;
        }

        public Builder setSubject(String subject) {
            mSubject = subject;
            return this;
        }

        public Builder setSerialNumber(BigInteger serialNumber) {
            mSerialNumber = serialNumber;
            return this;
        }

        public Builder setStartDate(Date startDate) {
            mStartDate = startDate;
            return this;
        }

        public Builder setEndDate(Date endDate) {
            mEndDate = endDate;
            return this;
        }

        public KeyPairGeneratorSpec build() {
            return new KeyPairGeneratorSpec(this);
        }
    }
}
