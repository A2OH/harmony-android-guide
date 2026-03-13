package android.net.nsd;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Android-compatible NsdServiceInfo shim. Stub — mutable service descriptor.
 */
public class NsdServiceInfo {

    private String      serviceName = "";
    private String      serviceType = "";
    private InetAddress host        = null;
    private int         port        = 0;
    private final Map<String, byte[]> attributes = new HashMap<>();

    public NsdServiceInfo() {}

    // -------------------------------------------------------------------------
    // Service name
    // -------------------------------------------------------------------------

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String name) {
        this.serviceName = name;
    }

    // -------------------------------------------------------------------------
    // Service type
    // -------------------------------------------------------------------------

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String type) {
        this.serviceType = type;
    }

    // -------------------------------------------------------------------------
    // Host / port
    // -------------------------------------------------------------------------

    public InetAddress getHost() {
        return host;
    }

    public void setHost(InetAddress host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // -------------------------------------------------------------------------
    // TXT record attributes
    // -------------------------------------------------------------------------

    public void setAttribute(String key, String value) {
        attributes.put(key, value != null ? value.getBytes() : new byte[0]);
    }

    public void setAttribute(String key, byte[] value) {
        attributes.put(key, value != null ? value : new byte[0]);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    public Map<String, byte[]> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public String toString() {
        return "NsdServiceInfo{name=" + serviceName
                + ", type=" + serviceType
                + ", host=" + host
                + ", port=" + port + "}";
    }
}
