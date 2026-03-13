package android.media.tv;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;

public final class TvContract {
    public static final int ACTION_INITIALIZE_PROGRAMS = 0;
    public static final int ACTION_PREVIEW_PROGRAM_ADDED_TO_WATCH_NEXT = 0;
    public static final int ACTION_PREVIEW_PROGRAM_BROWSABLE_DISABLED = 0;
    public static final int ACTION_REQUEST_CHANNEL_BROWSABLE = 0;
    public static final int ACTION_WATCH_NEXT_PROGRAM_BROWSABLE_DISABLED = 0;
    public static final int AUTHORITY = 0;
    public static final int EXTRA_CHANNEL_ID = 0;
    public static final int EXTRA_PREVIEW_PROGRAM_ID = 0;
    public static final int EXTRA_WATCH_NEXT_PROGRAM_ID = 0;

    public TvContract() {}

    public static Uri buildChannelLogoUri(long p0) { return null; }
    public static Uri buildChannelUri(long p0) { return null; }
    public static Uri buildChannelUriForPassthroughInput(String p0) { return null; }
    public static Uri buildChannelsUriForInput(String p0) { return null; }
    public static String buildInputId(ComponentName p0) { return null; }
    public static Uri buildPreviewProgramUri(long p0) { return null; }
    public static Uri buildPreviewProgramsUriForChannel(long p0) { return null; }
    public static Uri buildProgramUri(long p0) { return null; }
    public static Uri buildProgramsUriForChannel(long p0) { return null; }
    public static Uri buildProgramsUriForChannel(long p0, long p1, long p2) { return null; }
    public static Uri buildRecordedProgramUri(long p0) { return null; }
    public static Uri buildWatchNextProgramUri(long p0) { return null; }
    public static boolean isChannelUri(Uri p0) { return false; }
    public static boolean isChannelUriForPassthroughInput(Uri p0) { return false; }
    public static boolean isChannelUriForTunerInput(Uri p0) { return false; }
    public static boolean isProgramUri(Uri p0) { return false; }
    public static boolean isRecordedProgramUri(Uri p0) { return false; }
    public static void requestChannelBrowsable(Context p0, long p1) {}
}
