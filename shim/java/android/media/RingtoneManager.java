package android.media;
import android.net.Uri;
import android.net.Uri;
import java.net.URI;

/**
 * Android-compatible RingtoneManager shim. Stub for ringtone/notification/alarm URI lookup.
 *
 * Note: android.net.Uri is not declared here to keep this shim self-contained.
 * URI values are returned as Strings; callers may wrap via android.net.Uri.parse().
 */
public class RingtoneManager {

    public static final int TYPE_RINGTONE     = 1;
    public static final int TYPE_NOTIFICATION = 2;
    public static final int TYPE_ALARM        = 4;
    public static final int TYPE_ALL          = TYPE_RINGTONE | TYPE_NOTIFICATION | TYPE_ALARM;

    /**
     * Returns a synthetic default URI string for the given ringtone type.
     * Maps to an OH-side placeholder URI understood by the bridge layer.
     */
    public static String getDefaultUri(int type) {
        switch (type) {
            case TYPE_NOTIFICATION:
                return "content://settings/system/notification_sound";
            case TYPE_ALARM:
                return "content://settings/system/alarm_alert";
            case TYPE_RINGTONE:
            default:
                return "content://settings/system/ringtone";
        }
    }

    /**
     * Returns the actual default ringtone URI string (same as {@link #getDefaultUri} in this shim).
     *
     * @param context ignored in this stub
     * @param type    one of {@link #TYPE_RINGTONE}, {@link #TYPE_NOTIFICATION}, {@link #TYPE_ALARM}
     */
    public static String getActualDefaultRingtoneUri(Object context, int type) {
        return getDefaultUri(type);
    }

    /**
     * Returns a {@link Ringtone} stub for the given URI string.
     * The returned object logs play/stop calls.
     *
     * @param context ignored in this stub
     * @param uri(String URI of the ringtone (use {@link #getDefaultUri} or a custom URI string)
     */
    public static Ringtone getRingtone(Object context, String uriString) {
        return new Ringtone(uriString);
    }

    /**
     * Minimal stub Ringtone returned by {@link #getRingtone}.
     */
    public static class Ringtone {
        private final String mUri;
        private boolean mPlaying = false;

        Ringtone(String uri) {
            mUri = uri;
        }

        public void play() {
            mPlaying = true;
            System.out.println("[Ringtone] play: " + mUri);
        }

        public void stop() {
            mPlaying = false;
            System.out.println("[Ringtone] stop: " + mUri);
        }

        public boolean isPlaying() {
            return mPlaying;
        }

        public String getUri() {
            return mUri;
        }

        /** Returns a human-readable title derived from the URI. */
        public String getTitle(Object context) {
            if (mUri == null) return "";
            int slash = mUri.lastIndexOf('/');
            return slash >= 0 ? mUri.substring(slash + 1) : mUri;
        }
    }
}
