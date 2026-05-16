// Mainline APEX stub.  android.net.ProxyInfo (Connectivity APEX).
// Covers method signatures referenced by framework.jar (8 refs) so ART
// pre-execution invoke check resolves without preallocated NCDFE.

package android.net;

import java.util.List;

public class ProxyInfo {
    public ProxyInfo(ProxyInfo source) { /* no-op */ }
    private ProxyInfo() { }

    public static ProxyInfo buildDirectProxy(String host, int port, List<String> exclList) { return new ProxyInfo(); }
    public static ProxyInfo buildPacProxy(android.net.Uri pacUri) { return new ProxyInfo(); }

    public String[] getExclusionList() { return new String[0]; }
    public String getHost() { return ""; }
    public android.net.Uri getPacFileUrl() { return null; }
    public int getPort() { return 0; }
    public boolean isValid() { return false; }
}
