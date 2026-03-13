package android.service.autofill;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillId;

import android.view.autofill.AutofillId;

/**
 * Android-compatible SaveInfo shim. Describes when and how to save autofill data.
 */
public final class SaveInfo {

    public static final int SAVE_DATA_TYPE_PASSWORD      = 1;
    public static final int SAVE_DATA_TYPE_ADDRESS       = 2;
    public static final int SAVE_DATA_TYPE_CREDIT_CARD   = 4;
    public static final int SAVE_DATA_TYPE_USERNAME      = 8;
    public static final int SAVE_DATA_TYPE_EMAIL_ADDRESS = 16;

    private final int          mType;
    private final AutofillId[] mRequiredIds;
    private final int          mFlags;
    private final CharSequence mDescription;

    private SaveInfo(Builder builder) {
        mType        = builder.mType;
        mRequiredIds = builder.mRequiredIds;
        mFlags       = builder.mFlags;
        mDescription = builder.mDescription;
    }

    public int getType()              { return mType; }
    public AutofillId[] getRequiredIds() { return mRequiredIds; }
    public int getFlags()             { return mFlags; }
    public CharSequence getDescription() { return mDescription; }

    // --- Builder ---

    public static final class Builder {

        private final int          mType;
        private final AutofillId[] mRequiredIds;
        private int          mFlags;
        private CharSequence mDescription;

        public Builder(int type, AutofillId[] requiredIds) {
            mType        = type;
            mRequiredIds = requiredIds;
        }

        public Builder setFlags(int flags) {
            mFlags = flags;
            return this;
        }

        public Builder setDescription(CharSequence description) {
            mDescription = description;
            return this;
        }

        public SaveInfo build() {
            return new SaveInfo(this);
        }
    }
}
