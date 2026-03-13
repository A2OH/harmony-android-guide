package android.service.quickaccesswallet;

import android.app.Service;

/**
 * Android-compatible QuickAccessWalletService shim. Stub for wallet card provider service.
 */
public abstract class QuickAccessWalletService extends Service {

    /**
     * Called when the system requests wallet cards to display.
     * In the real API the parameter is a GetWalletCardsRequest and callback a
     * GetWalletCardsCallback; both are typed as Object here to avoid extra stubs.
     *
     * @param request  the card request descriptor
     * @param callback callback used to deliver the card response
     */
    public abstract void onWalletCardsRequested(Object request, Object callback);

    /**
     * Called when the wallet UI is dismissed.
     */
    public abstract void onWalletDismissed();

    /**
     * Called when the user selects a specific wallet card.
     *
     * @param selectedCard the card that was selected
     */
    public abstract void onWalletCardSelected(WalletCard selectedCard);
}
