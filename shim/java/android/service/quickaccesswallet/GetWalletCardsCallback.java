package android.service.quickaccesswallet;

public interface GetWalletCardsCallback {
    void onFailure(GetWalletCardsError p0);
    void onSuccess(GetWalletCardsResponse p0);
}
