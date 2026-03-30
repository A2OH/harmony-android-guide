package sun.security.provider;

import java.security.Provider;

public class Sun extends Provider {
    public Sun() {
        super("SUN", 1.0, "Westlake stub Sun provider");
        put("SecureRandom.SHA1PRNG", "sun.security.provider.SecureRandom");
    }
}
