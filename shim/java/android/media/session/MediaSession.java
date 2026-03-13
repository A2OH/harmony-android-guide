package android.media.session;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaMetadata;
import android.media.Rating;
import android.media.VolumeProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import java.util.List;

public final class MediaSession {
    public MediaSession(Context p0, String p1) {}
    public MediaSession(Context p0, String p1, Bundle p2) {}

    public boolean isActive() { return false; }
    public void release() {}
    public void sendSessionEvent(String p0, Bundle p1) {}
    public void setActive(boolean p0) {}
    public void setCallback(Object p0) {}
    public void setCallback(Object p0, Handler p1) {}
    public void setExtras(Bundle p0) {}
    public void setFlags(int p0) {}
    public void setMediaButtonReceiver(PendingIntent p0) {}
    public void setMetadata(MediaMetadata p0) {}
    public void setPlaybackState(PlaybackState p0) {}
    public void setPlaybackToLocal(AudioAttributes p0) {}
    public void setPlaybackToRemote(VolumeProvider p0) {}
    public void setQueue(java.util.List<Object> p0) {}
    public void setQueueTitle(CharSequence p0) {}
    public void setRatingType(int p0) {}
    public void setSessionActivity(PendingIntent p0) {}
    public void onCommand(String p0, Bundle p1, ResultReceiver p2) {}
    public void onCustomAction(String p0, Bundle p1) {}
    public void onFastForward() {}
    public boolean onMediaButtonEvent(Intent p0) { return false; }
    public void onPause() {}
    public void onPlay() {}
    public void onPlayFromMediaId(String p0, Bundle p1) {}
    public void onPlayFromSearch(String p0, Bundle p1) {}
    public void onPlayFromUri(Uri p0, Bundle p1) {}
    public void onPrepare() {}
    public void onPrepareFromMediaId(String p0, Bundle p1) {}
    public void onPrepareFromSearch(String p0, Bundle p1) {}
    public void onPrepareFromUri(Uri p0, Bundle p1) {}
    public void onRewind() {}
    public void onSeekTo(long p0) {}
    public void onSetPlaybackSpeed(float p0) {}
    public void onSetRating(Rating p0) {}
    public void onSkipToNext() {}
    public void onSkipToPrevious() {}
    public void onSkipToQueueItem(long p0) {}
    public void onStop() {}
}
