package android.util;

import java.util.TimeZone;

/**
 * Android-compatible TimeUtils shim. Pure Java timezone utilities.
 */
public class TimeUtils {

    public static TimeZone getTimeZone(int offset, boolean dst, long when, String country) {
        String[] ids = TimeZone.getAvailableIDs(offset);
        if (ids != null && ids.length > 0) {
            for (String id : ids) {
                TimeZone tz = TimeZone.getTimeZone(id);
                if (tz.useDaylightTime() == dst) {
                    return tz;
                }
            }
            return TimeZone.getTimeZone(ids[0]);
        }
        return TimeZone.getDefault();
    }

    public static String getTimeZoneDatabaseVersion() {
        return "2024a"; // stub version
    }

    public static String formatDuration(long duration) {
        StringBuilder sb = new StringBuilder();
        if (duration < 0) {
            sb.append('-');
            duration = -duration;
        }
        long sec = duration / 1000;
        long ms = duration % 1000;
        if (sec >= 3600) {
            sb.append(sec / 3600).append("h");
            sec %= 3600;
        }
        if (sec >= 60) {
            sb.append(sec / 60).append("m");
            sec %= 60;
        }
        sb.append(sec).append("s");
        if (ms > 0) sb.append(ms).append("ms");
        return sb.toString();
    }
}
