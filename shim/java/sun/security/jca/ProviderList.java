package sun.security.jca;

import java.security.Provider;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Stub ProviderList — minimal implementation for SecureRandom resolution.
 */
public class ProviderList {
    private final List<Provider> providers;

    private ProviderList(List<Provider> providers) {
        this.providers = providers;
    }

    static ProviderList newList(Provider... provs) {
        List<Provider> list = new ArrayList<>();
        for (Provider p : provs) if (p != null) list.add(p);
        return new ProviderList(list);
    }

    public List<Provider> providers() { return Collections.unmodifiableList(providers); }
    public int size() { return providers.size(); }
    public Provider getProvider(int index) { return providers.get(index); }

    public List<Provider.Service> getServices(String type, String algorithm) {
        List<Provider.Service> services = new ArrayList<>();
        for (Provider p : providers) {
            Provider.Service s = p.getService(type, algorithm);
            if (s != null) services.add(s);
        }
        return services;
    }

    public Provider.Service getService(String type, String algorithm) {
        for (Provider p : providers) {
            Provider.Service s = p.getService(type, algorithm);
            if (s != null) return s;
        }
        return null;
    }

    public class ServiceList implements Iterable<Provider.Service> {
        private final String type, algorithm;
        ServiceList(String type, String algorithm) { this.type = type; this.algorithm = algorithm; }
        public java.util.Iterator<Provider.Service> iterator() {
            List<Provider.Service> services = new ArrayList<>();
            for (Provider p : providers) {
                Provider.Service s = p.getService(type, algorithm);
                if (s != null) services.add(s);
            }
            return services.iterator();
        }
    }
}
