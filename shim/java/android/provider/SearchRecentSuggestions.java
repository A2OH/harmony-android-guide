package android.provider;
import android.content.ContentResolver;
import android.content.Context;

public class SearchRecentSuggestions {
    public static final int QUERIES_PROJECTION_1LINE = 0;
    public static final int QUERIES_PROJECTION_2LINE = 0;
    public static final int QUERIES_PROJECTION_DATE_INDEX = 0;
    public static final int QUERIES_PROJECTION_DISPLAY1_INDEX = 0;
    public static final int QUERIES_PROJECTION_DISPLAY2_INDEX = 0;
    public static final int QUERIES_PROJECTION_QUERY_INDEX = 0;

    public SearchRecentSuggestions(Context p0, String p1, int p2) {}

    public void clearHistory() {}
    public void saveRecentQuery(String p0, String p1) {}
    public void truncateHistory(ContentResolver p0, int p1) {}
}
