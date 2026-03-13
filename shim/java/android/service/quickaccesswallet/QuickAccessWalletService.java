package android.service.quickaccesswallet;
import android.app.Service;

public class QuickAccessWalletService extends Service {
    public static final int ACTION_VIEW_WALLET = 0;
    public static final int ACTION_VIEW_WALLET_SETTINGS = 0;
    public static final int SERVICE_INTERFACE = 0;
    public static final int SERVICE_META_DATA = 0;

    public QuickAccessWalletService() {}

    public void onWalletCardSelected(SelectWalletCardRequest p0) {}
    public void onWalletCardsRequested(GetWalletCardsRequest p0, GetWalletCardsCallback p1) {}
    public void onWalletDismissed() {}
    public void sendWalletServiceEvent(WalletServiceEvent p0) {}
}
