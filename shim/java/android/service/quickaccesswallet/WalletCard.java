package android.service.quickaccesswallet;
import android.graphics.drawable.Icon;
import android.graphics.drawable.Icon;

/**
 * Android-compatible WalletCard shim. Stub for a card shown in the Quick Access Wallet.
 */
public final class WalletCard {

    private final String mCardId;
    private final Object mCardImage;          // Icon in real API; Object here
    private final CharSequence mContentDescription;

    private WalletCard(Builder b) {
        mCardId             = b.mCardId;
        mCardImage          = b.mCardImage;
        mContentDescription = b.mContentDescription;
    }

    public String      getCardId()             { return mCardId;             }
    public Object      getCardImage()          { return mCardImage;          }
    public CharSequence getContentDescription() { return mContentDescription; }

    public static final class Builder {
        private String      mCardId             = "";
        private Object      mCardImage          = null;
        private CharSequence mContentDescription = "";

        public Builder setCardId(String cardId) {
            mCardId = cardId != null ? cardId : "";
            return this;
        }

        public Builder setCardImage(Object cardImage) {
            mCardImage = cardImage;
            return this;
        }

        public Builder setContentDescription(CharSequence contentDescription) {
            mContentDescription = contentDescription;
            return this;
        }

        public WalletCard build() {
            return new WalletCard(this);
        }
    }
}
