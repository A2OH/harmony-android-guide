package android.content.res;

import android.content.Context;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Stub: FontResourcesParser — parses font-family XML resources.
 */
public class FontResourcesParser {

    public interface FamilyResourceEntry {}

    public static class ProviderResourceEntry implements FamilyResourceEntry {
        private FontRequest mRequest;
        private String mAuthority;
        private String mPkg;
        private String mQuery;

        public ProviderResourceEntry(FontRequest request, int fetchStrategy, int timeout) {
            mRequest = request;
        }
        public FontRequest getRequest() { return mRequest; }
        public int getFetchStrategy() { return 0; }
        public int getTimeoutMs() { return 0; }
        public String getAuthority() { return mAuthority; }
        public String getPackage() { return mPkg; }
        public String getQuery() { return mQuery; }
        public java.util.List getCerts() { return java.util.Collections.emptyList(); }
    }

    public static class FontFamilyFilesResourceEntry implements FamilyResourceEntry {
        private FontFileResourceEntry[] mEntries;
        public FontFamilyFilesResourceEntry(FontFileResourceEntry[] entries) {
            mEntries = entries;
        }
        public FontFileResourceEntry[] getEntries() { return mEntries; }
    }

    public static class FontFileResourceEntry {
        public static final int ITALIC = 1;
        public static final int UPRIGHT = 0;
        private String mFileName;
        private int mWeight;
        private int mItalic;

        public FontFileResourceEntry(String fileName, int weight, int italic, String variationSettings, int ttcIndex) {
            mFileName = fileName;
            mWeight = weight;
            mItalic = italic;
        }

        public String getFileName() { return mFileName; }
        public int getWeight() { return mWeight; }
        public int getItalic() { return mItalic; }
        public String getVariationSettings() { return null; }
        public int getTtcIndex() { return 0; }
    }

    public static class FontRequest {
        public FontRequest(String authority, String query, List certs) {}
        public String getProviderAuthority() { return ""; }
        public String getQuery() { return ""; }
    }

    public static final int FETCH_STRATEGY_BLOCKING = 0;
    public static final int FETCH_STRATEGY_ASYNC = 1;
    public static final int INFINITE_TIMEOUT_VALUE = -1;

    public static FamilyResourceEntry parse(XmlPullParser parser, Resources resources) throws XmlPullParserException, IOException {
        return null;
    }
}
