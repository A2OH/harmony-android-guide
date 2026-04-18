package sun.security.jca;

import java.security.Provider;
import java.util.List;
import java.util.ArrayList;

/**
 * Stub Providers — provides a minimal provider list so SecureRandom/UUID work.
 * The real AOSP version loads from java.security properties file.
 */
public class Providers {
    private static final ProviderList providerList;

    static {
        // Create a minimal provider list with our stub
        providerList = ProviderList.newList(new Provider("Westlake", 1.0, "Stub provider") {
            {
                put("SecureRandom.SHA1PRNG", "sun.security.provider.SecureRandom");
                put("MessageDigest.SHA-1", "sun.security.provider.SHA");
                put("MessageDigest.SHA-256", "sun.security.provider.SHA2$SHA256");
                put("MessageDigest.SHA-512", "sun.security.provider.SHA5$SHA512");
                put("MessageDigest.MD5", "sun.security.provider.MD5");
            }
        });
    }

    public static ProviderList getProviderList() {
        return providerList;
    }

    public static void setProviderList(ProviderList pl) {
        // no-op
    }

    public static ProviderList getFullProviderList() {
        return providerList;
    }

    public static Object startJarVerification() { return null; }
    public static void stopJarVerification(Object obj) {}

    public static int getThreadProviderListSize() { return 0; }
}
