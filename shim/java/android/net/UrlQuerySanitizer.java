package android.net;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Android-compatible UrlQuerySanitizer shim. Stub — simple query string parser.
 */
public class UrlQuerySanitizer {

    // -------------------------------------------------------------------------
    // ValueSanitizer interface
    // -------------------------------------------------------------------------

    public interface ValueSanitizer {
        String sanitize(String value);
    }

    // -------------------------------------------------------------------------
    // AllIllegal inner class — rejects every value (returns "")
    // -------------------------------------------------------------------------

    public static final class AllIllegal implements ValueSanitizer {
        public static final AllIllegal INSTANCE = new AllIllegal();

        @Override
        public String sanitize(String value) {
            return "";
        }
    }

    // -------------------------------------------------------------------------
    // AllButNulAndAngle inner class — strips NUL and angle brackets
    // -------------------------------------------------------------------------

    public static final class AllButNulAndAngle implements ValueSanitizer {
        public static final AllButNulAndAngle INSTANCE = new AllButNulAndAngle();

        @Override
        public String sanitize(String value) {
            if (value == null) return "";
            return value.replace("\u0000", "").replace("<", "").replace(">", "");
        }
    }

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private boolean          mAllowUnregistered = false;
    private ValueSanitizer   mUnregisteredSanitizer = AllIllegal.INSTANCE;
    private final Map<String, String> mEntries = new LinkedHashMap<>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public UrlQuerySanitizer() {}

    public UrlQuerySanitizer(String url) {
        parseUrl(url);
    }

    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    public void setAllowUnregisteredParamaters(boolean allow) {
        mAllowUnregistered = allow;
    }

    public void setUnregisteredParameterValueSanitizer(ValueSanitizer sanitizer) {
        mUnregisteredSanitizer = sanitizer;
    }

    // -------------------------------------------------------------------------
    // Parsing
    // -------------------------------------------------------------------------

    public void parseUrl(String url) {
        mEntries.clear();
        if (url == null) return;
        int qIdx = url.indexOf('?');
        String query = (qIdx >= 0) ? url.substring(qIdx + 1) : url;
        if (query.isEmpty()) return;
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            String key   = decode(eq >= 0 ? pair.substring(0, eq) : pair);
            String value = eq >= 0 ? decode(pair.substring(eq + 1)) : "";
            if (mAllowUnregistered) {
                String sanitized = mUnregisteredSanitizer != null
                        ? mUnregisteredSanitizer.sanitize(value) : value;
                mEntries.put(key, sanitized);
            } else {
                mEntries.put(key, value);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Query methods
    // -------------------------------------------------------------------------

    public Set<String> getParameterSet() {
        return mEntries.keySet();
    }

    public String getValue(String parameter) {
        return mEntries.get(parameter);
    }

    public boolean hasParameter(String parameter) {
        return mEntries.containsKey(parameter);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String decode(String s) {
        try {
            return java.net.URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }
}
