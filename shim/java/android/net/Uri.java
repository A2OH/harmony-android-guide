package android.net;
import java.net.URI;

/**
 * Shim: android.net.Uri — pure Java implementation.
 * URI parsing doesn't need OH APIs, so this is a self-contained shim.
 */
public class Uri {
    public static final Uri EMPTY = new StringUri("");

    public String toString() { return null; }
    public String getScheme() { return null; }
    public String getHost() { return null; }
    public int getPort() { return 0; }
    public String getPath() { return null; }
    public String getQuery() { return null; }
    public String getFragment() { return null; }
    public String getAuthority() { return null; }

    public static Uri parse(String uriString) {
        return new StringUri(uriString);
    }

    public static Uri fromParts(String scheme, String ssp, String fragment) {
        StringBuilder sb = new StringBuilder();
        sb.append(scheme).append(":").append(ssp);
        if (fragment != null) sb.append("#").append(fragment);
        return new StringUri(sb.toString());
    }

    public String getLastPathSegment() {
        String path = getPath();
        if (path == null || path.isEmpty()) return null;
        int last = path.lastIndexOf('/');
        return last >= 0 ? path.substring(last + 1) : path;
    }

    public String getQueryParameter(String key) {
        String query = getQuery();
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if (kv.length == 2 && kv[0].equals(key)) {
                return java.net.URLDecoder.decode(kv[1]);
            }
        }
        return null;
    }

    // ── Builder ──

    public static class Builder {
        private String scheme;
        private String authority;
        private String path = "";
        private StringBuilder query = new StringBuilder();
        private String fragment;

        public Builder scheme(String scheme) { this.scheme = scheme; return this; }
        public Builder authority(String authority) { this.authority = authority; return this; }
        public Builder path(String path) { this.path = path; return this; }
        public Builder appendPath(String segment) {
            if (!this.path.endsWith("/")) this.path += "/";
            this.path += segment;
            return this;
        }
        public Builder appendQueryParameter(String key, String value) {
            if (query.length() > 0) query.append("&");
            query.append(java.net.URLEncoder.encode(key))
                 .append("=")
                 .append(java.net.URLEncoder.encode(value));
            return this;
        }
        public Builder fragment(String fragment) { this.fragment = fragment; return this; }

        public Uri build() {
            StringBuilder sb = new StringBuilder();
            if (scheme != null) sb.append(scheme).append("://");
            if (authority != null) sb.append(authority);
            sb.append(path);
            if (query.length() > 0) sb.append("?").append(query);
            if (fragment != null) sb.append("#").append(fragment);
            return new StringUri(sb.toString());
        }
    }

    // ── Internal ──

    private static class StringUri extends Uri {
        private final String uriString;
        private java.net.URI parsed;

        StringUri(String uriString) {
            this.uriString = uriString;
            try {
                this.parsed = new java.net.URI(uriString);
            } catch (Exception e) {
                this.parsed = null;
            }
        }

        @Override public String toString() { return uriString; }
        @Override public String getScheme() { return parsed != null ? parsed.getScheme() : null; }
        @Override public String getHost() { return parsed != null ? parsed.getHost() : null; }
        @Override public int getPort() { return parsed != null ? parsed.getPort() : -1; }
        @Override public String getPath() { return parsed != null ? parsed.getPath() : null; }
        @Override public String getQuery() { return parsed != null ? parsed.getQuery() : null; }
        @Override public String getFragment() { return parsed != null ? parsed.getFragment() : null; }
        @Override public String getAuthority() { return parsed != null ? parsed.getAuthority() : null; }
    }
}
