package android.media.tv;
import android.app.Service;
import android.content.Intent;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import java.util.List;

public class TvInputService extends Service {
    public static final int PRIORITY_HINT_USE_CASE_TYPE_BACKGROUND = 0;
    public static final int PRIORITY_HINT_USE_CASE_TYPE_LIVE = 0;
    public static final int PRIORITY_HINT_USE_CASE_TYPE_PLAYBACK = 0;
    public static final int PRIORITY_HINT_USE_CASE_TYPE_RECORD = 0;
    public static final int PRIORITY_HINT_USE_CASE_TYPE_SCAN = 0;
    public static final int SERVICE_INTERFACE = 0;
    public static final int SERVICE_META_DATA = 0;

    public TvInputService() {}

    public IBinder onBind(Intent p0) { return null; }
    public String getHardwareInputId() { return null; }
    public void onHardwareVideoAvailable() {}
    public void onHardwareVideoUnavailable(int p0) {}
    public boolean onSetSurface(Surface p0) { return false; }
    public void notifyError(int p0) {}
    public void notifyRecordingStopped(Uri p0) {}
    public void notifyTuned(Uri p0) {}
    public void onAppPrivateCommand(String p0, Bundle p1) {}
    public void onRelease() {}
    public void onStartRecording(Uri p0) {}
    public void onStartRecording(Uri p0, Bundle p1) {}
    public void onStopRecording() {}
    public void onTune(Uri p0) {}
    public void onTune(Uri p0, Bundle p1) {}
    public void layoutSurface(int p0, int p1, int p2, int p3) {}
    public void notifyChannelRetuned(Uri p0) {}
    public void notifyContentAllowed() {}
    public void notifyContentBlocked(TvContentRating p0) {}
    public void notifyTimeShiftStatusChanged(int p0) {}
    public void notifyTrackSelected(int p0, String p1) {}
    public void notifyTracksChanged(java.util.List<Object> p0) {}
    public void notifyVideoAvailable() {}
    public void notifyVideoUnavailable(int p0) {}
    public View onCreateOverlayView() { return null; }
    public boolean onGenericMotionEvent(MotionEvent p0) { return false; }
    public boolean onKeyDown(int p0, KeyEvent p1) { return false; }
    public boolean onKeyLongPress(int p0, KeyEvent p1) { return false; }
    public boolean onKeyMultiple(int p0, int p1, KeyEvent p2) { return false; }
    public boolean onKeyUp(int p0, KeyEvent p1) { return false; }
    public void onOverlayViewSizeChanged(int p0, int p1) {}
    public boolean onSelectTrack(int p0, String p1) { return false; }
    public void onSetCaptionEnabled(boolean p0) {}
    public void onSetStreamVolume(float p1) {}
    public void onSurfaceChanged(int p0, int p1, int p2) {}
    public long onTimeShiftGetCurrentPosition() { return 0L; }
    public long onTimeShiftGetStartPosition() { return 0L; }
    public void onTimeShiftPause() {}
    public void onTimeShiftPlay(Uri p0) {}
    public void onTimeShiftResume() {}
    public void onTimeShiftSeekTo(long p0) {}
    public void onTimeShiftSetPlaybackParams(PlaybackParams p0) {}
    public boolean onTouchEvent(MotionEvent p0) { return false; }
    public boolean onTrackballEvent(MotionEvent p0) { return false; }
    public void onUnblockContent(TvContentRating p0) {}
    public void setOverlayViewEnabled(boolean p0) {}
}
