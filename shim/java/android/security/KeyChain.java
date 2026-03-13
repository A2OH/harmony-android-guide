package android.security;

public final class KeyChain {
    public KeyChain() {}

    public static final int ACTION_KEYCHAIN_CHANGED = 0;
    public static final int ACTION_KEY_ACCESS_CHANGED = 0;
    public static final int ACTION_TRUST_STORE_CHANGED = 0;
    public static final int EXTRA_CERTIFICATE = 0;
    public static final int EXTRA_KEY_ACCESSIBLE = 0;
    public static final int EXTRA_KEY_ALIAS = 0;
    public static final int EXTRA_NAME = 0;
    public static final int EXTRA_PKCS12 = 0;
    public static final int KEY_ALIAS_SELECTION_DENIED = 0;
    public static void choosePrivateKeyAlias(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {}
    public static void choosePrivateKeyAlias(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {}
    public static boolean isKeyAlgorithmSupported(Object p0) { return false; }
}
