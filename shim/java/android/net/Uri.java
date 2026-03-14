package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Shim: android.net.Uri — immutable URI reference (RFC 2396).
 * Pure Java implementation backed by java.net.URI.
 */
public abstract class Uri implements Parcelable, Comparable<Uri> {

    public static final Uri EMPTY = new StringUri("");

    /* ── Abstract methods (implemented by StringUri) ── */

    public abstract String toString();
    public abstract String getScheme();
    public abstract String getSchemeSpecificPart();
    public abstract String getEncodedSchemeSpecificPart();
    public abstract String getAuthority();
    public abstract String getHost();
    public abstract int getPort();
    public abstract String getPath();
    public abstract String getQuery();
    public abstract String getFragment();
    public abstract boolean isHierarchical();
    public abstract boolean isRelative();
    public abstract List<String> getPathSegments();
    public abstract Builder buildUpon();

    /* ── Concrete methods ── */

    public boolean isAbsolute() { return !isRelative(); }
    public boolean isOpaque() { return !isHierarchical(); }

    public String getLastPathSegment() {
        List<String> segments = getPathSegments();
        if (segments == null || segments.isEmpty()) return null;
        return segments.get(segments.size() - 1);
    }

    public String getQueryParameter(String key) {
        if (key == null) throw new NullPointerException("key");
        String query = getQuery();
        if (query == null) return null;
        int start = 0;
        int len = query.length();
        while (start < len) {
            int nextAmp = query.indexOf('&', start);
            int end = (nextAmp == -1) ? len : nextAmp;
            int eq = query.indexOf('=', start);
            if (eq == -1 || eq > end) {
                // key with no value
                String k = decode(query.substring(start, end));
                if (key.equals(k)) return "";
            } else {
                String k = decode(query.substring(start, eq));
                if (key.equals(k)) {
                    return decode(query.substring(eq + 1, end));
                }
            }
            start = end + 1;
        }
        return null;
    }

    public List<String> getQueryParameters(String key) {
        if (key == null) throw new NullPointerException("key");
        String query = getQuery();
        if (query == null) return Collections.emptyList();
        List<String> result = new ArrayList<String>();
        int start = 0;
        int len = query.length();
        while (start < len) {
            int nextAmp = query.indexOf('&', start);
            int end = (nextAmp == -1) ? len : nextAmp;
            int eq = query.indexOf('=', start);
            if (eq == -1 || eq > end) {
                String k = decode(query.substring(start, end));
                if (key.equals(k)) result.add("");
            } else {
                String k = decode(query.substring(start, eq));
                if (key.equals(k)) {
                    result.add(decode(query.substring(eq + 1, end)));
                }
            }
            start = end + 1;
        }
        return Collections.unmodifiableList(result);
    }

    public Set<String> getQueryParameterNames() {
        String query = getQuery();
        if (query == null) return Collections.emptySet();
        Set<String> names = new LinkedHashSet<String>();
        int start = 0;
        int len = query.length();
        while (start < len) {
            int nextAmp = query.indexOf('&', start);
            int end = (nextAmp == -1) ? len : nextAmp;
            int eq = query.indexOf('=', start);
            if (eq == -1 || eq > end) {
                names.add(decode(query.substring(start, end)));
            } else {
                names.add(decode(query.substring(start, eq)));
            }
            start = end + 1;
        }
        return Collections.unmodifiableSet(names);
    }

    public boolean getBooleanQueryParameter(String key, boolean defaultValue) {
        String val = getQueryParameter(key);
        if (val == null) return defaultValue;
        val = val.toLowerCase();
        return !"false".equals(val) && !"0".equals(val);
    }

    public Uri normalizeScheme() {
        String scheme = getScheme();
        if (scheme == null) return this;
        String lower = scheme.toLowerCase();
        if (scheme.equals(lower)) return this;
        return buildUpon().scheme(lower).build();
    }

    @Override
    public int compareTo(Uri other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Uri)) return false;
        return toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /* ── Parcelable stubs ── */

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel dest, int flags) {
        if (dest != null) dest.writeString(toString());
    }
    public static void writeToParcel(Parcel dest, Uri uri) {
        if (dest != null) dest.writeString(uri != null ? uri.toString() : "");
    }

    /* ── Static factory methods ── */

    public static Uri parse(String uriString) {
        if (uriString == null) throw new NullPointerException("uriString");
        return new StringUri(uriString);
    }

    public static Uri fromParts(String scheme, String ssp, String fragment) {
        if (scheme == null) throw new NullPointerException("scheme");
        if (ssp == null) throw new NullPointerException("ssp");
        StringBuilder sb = new StringBuilder();
        sb.append(scheme).append(':').append(encode(ssp));
        if (fragment != null) sb.append('#').append(encode(fragment));
        return new OpaqueUri(sb.toString(), scheme, ssp, fragment);
    }

    public static Uri fromFile(File file) {
        if (file == null) throw new NullPointerException("file");
        return new StringUri("file://" + file.getAbsolutePath());
    }

    public static Uri withAppendedPath(Uri baseUri, String pathSegment) {
        if (baseUri == null) throw new NullPointerException("baseUri");
        if (pathSegment == null) throw new NullPointerException("pathSegment");
        return baseUri.buildUpon().appendPath(pathSegment).build();
    }

    /* ── Encoding helpers ── */

    public static String encode(String s) {
        return encode(s, null);
    }

    public static String encode(String s, String allow) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
                    || c == '-' || c == '_' || c == '.' || c == '~'
                    || (allow != null && allow.indexOf(c) >= 0)) {
                sb.append(c);
            } else {
                try {
                    byte[] bytes = String.valueOf(c).getBytes("UTF-8");
                    for (byte b : bytes) {
                        sb.append('%');
                        sb.append(Character.toUpperCase(Character.forDigit((b >> 4) & 0xf, 16)));
                        sb.append(Character.toUpperCase(Character.forDigit(b & 0xf, 16)));
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return sb.toString();
    }

    public static String decode(String s) {
        if (s == null) return null;
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // Builder
    // ══════════════════════════════════════════════════════════════════

    public static class Builder {
        private String scheme;
        private String authority;
        private String path = "";
        private StringBuilder query;
        private String fragment;

        public Builder() {}

        public Builder scheme(String scheme) { this.scheme = scheme; return this; }
        public Builder authority(String authority) { this.authority = authority; return this; }
        public Builder encodedAuthority(String authority) { this.authority = authority; return this; }

        public Builder path(String path) { this.path = path != null ? path : ""; return this; }
        public Builder encodedPath(String path) { this.path = path != null ? path : ""; return this; }

        public Builder appendPath(String segment) {
            if (segment == null) return this;
            if (path == null) path = "";
            if (!path.endsWith("/")) path += "/";
            path += encode(segment, "@:!$&'()*+,;=-._~");
            return this;
        }
        public Builder appendEncodedPath(String segment) {
            if (segment == null) return this;
            if (path == null) path = "";
            if (!path.endsWith("/")) path += "/";
            path += segment;
            return this;
        }

        public Builder query(String query) {
            this.query = query != null ? new StringBuilder(query) : null;
            return this;
        }
        public Builder encodedQuery(String query) {
            this.query = query != null ? new StringBuilder(query) : null;
            return this;
        }

        public Builder appendQueryParameter(String key, String value) {
            if (query == null) query = new StringBuilder();
            if (query.length() > 0) query.append('&');
            query.append(encode(key));
            query.append('=');
            query.append(encode(value));
            return this;
        }

        public Builder clearQuery() { query = null; return this; }

        public Builder fragment(String fragment) { this.fragment = fragment; return this; }
        public Builder encodedFragment(String fragment) { this.fragment = fragment; return this; }

        public Builder opaquePart(String ssp) {
            // For opaque URIs: scheme:ssp#fragment
            this.authority = null;
            this.path = ssp != null ? ssp : "";
            return this;
        }
        public Builder encodedOpaquePart(String ssp) { return opaquePart(ssp); }

        public Uri build() {
            StringBuilder sb = new StringBuilder();
            if (scheme != null) {
                sb.append(scheme).append(':');
                if (authority != null) {
                    sb.append("//").append(authority);
                }
            }
            if (path != null) sb.append(path);
            if (query != null && query.length() > 0) sb.append('?').append(query);
            if (fragment != null) sb.append('#').append(fragment);
            return new StringUri(sb.toString());
        }

        @Override
        public String toString() {
            return build().toString();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // StringUri — hierarchical URI backed by java.net.URI
    // ══════════════════════════════════════════════════════════════════

    private static class StringUri extends Uri {
        private final String uriString;
        private java.net.URI parsed;

        StringUri(String uriString) {
            this.uriString = uriString != null ? uriString : "";
            try {
                this.parsed = new java.net.URI(this.uriString);
            } catch (Exception e) {
                this.parsed = null;
            }
        }

        @Override public String toString() { return uriString; }
        @Override public String getScheme() { return parsed != null ? parsed.getScheme() : null; }
        @Override public String getHost() { return parsed != null ? parsed.getHost() : null; }
        @Override public int getPort() { return parsed != null ? parsed.getPort() : -1; }
        @Override public String getPath() { return parsed != null ? parsed.getPath() : null; }
        @Override public String getQuery() { return parsed != null ? parsed.getRawQuery() : null; }
        @Override public String getFragment() { return parsed != null ? parsed.getFragment() : null; }
        @Override public String getAuthority() { return parsed != null ? parsed.getAuthority() : null; }

        @Override
        public String getSchemeSpecificPart() {
            if (parsed == null) return null;
            return parsed.getSchemeSpecificPart();
        }

        @Override
        public String getEncodedSchemeSpecificPart() {
            if (parsed == null) return null;
            return parsed.getRawSchemeSpecificPart();
        }

        @Override
        public boolean isHierarchical() {
            if (parsed == null) return uriString.isEmpty();
            return parsed.getScheme() == null || parsed.getRawAuthority() != null || (parsed.getRawPath() != null && parsed.getRawPath().startsWith("/"));
        }

        @Override
        public boolean isRelative() {
            return getScheme() == null;
        }

        @Override
        public List<String> getPathSegments() {
            String path = getPath();
            if (path == null || path.isEmpty()) return Collections.emptyList();
            List<String> segments = new ArrayList<String>();
            for (String s : path.split("/")) {
                if (!s.isEmpty()) segments.add(decode(s));
            }
            return Collections.unmodifiableList(segments);
        }

        @Override
        public Builder buildUpon() {
            Builder b = new Builder();
            b.scheme(getScheme());
            b.authority(getAuthority());
            b.path(getPath());
            b.query(getQuery());
            b.fragment(getFragment());
            return b;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // OpaqueUri — for URIs created via fromParts (scheme:ssp#fragment)
    // ══════════════════════════════════════════════════════════════════

    private static class OpaqueUri extends Uri {
        private final String uriString;
        private final String scheme;
        private final String ssp;
        private final String fragment;

        OpaqueUri(String uriString, String scheme, String ssp, String fragment) {
            this.uriString = uriString;
            this.scheme = scheme;
            this.ssp = ssp;
            this.fragment = fragment;
        }

        @Override public String toString() { return uriString; }
        @Override public String getScheme() { return scheme; }
        @Override public String getSchemeSpecificPart() { return ssp; }
        @Override public String getEncodedSchemeSpecificPart() { return encode(ssp); }
        @Override public String getAuthority() { return null; }
        @Override public String getHost() { return null; }
        @Override public int getPort() { return -1; }
        @Override public String getPath() { return null; }
        @Override public String getQuery() { return null; }
        @Override public String getFragment() { return fragment; }
        @Override public boolean isHierarchical() { return false; }
        @Override public boolean isRelative() { return false; }
        @Override public List<String> getPathSegments() { return Collections.emptyList(); }

        @Override
        public Builder buildUpon() {
            return new Builder().scheme(scheme).opaquePart(ssp).fragment(fragment);
        }
    }
}
