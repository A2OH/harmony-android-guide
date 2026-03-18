package android.media.tv;
import android.content.Context;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.InputEvent;
import android.view.ViewGroup;
import java.util.List;

public class TvView extends ViewGroup {
    public TvView(Context p0) { super(p0); }
    public TvView(Context p0, AttributeSet p1) { super(p0, p1); }
    public TvView(Context p0, AttributeSet p1, int p2) { super(p0, p1, p2); }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {}

    public boolean dispatchUnhandledInputEvent(InputEvent p0) { return false; }
    public String getSelectedTrack(int p0) { return null; }
    public List<?> getTracks(int p0) { return null; }
    public boolean onUnhandledInputEvent(InputEvent p0) { return false; }
    public void reset() {}
    public void selectTrack(int p0, String p1) {}
    public void sendAppPrivateCommand(String p0, Bundle p1) {}
    public void setCallback(Object p0) {}
    public void setCaptionEnabled(boolean p0) {}
    public void setOnUnhandledInputEventListener(Object p0) {}
    public void setStreamVolume(float p1) {}
    public void setTimeShiftPositionCallback(Object p0) {}
    public void setZOrderMediaOverlay(boolean p0) {}
    public void setZOrderOnTop(boolean p0) {}
    public void timeShiftPause() {}
    public void timeShiftPlay(String p0, Uri p1) {}
    public void timeShiftResume() {}
    public void timeShiftSeekTo(long p0) {}
    public void timeShiftSetPlaybackParams(PlaybackParams p0) {}
    public void tune(String p0, Uri p1) {}
    public void tune(String p0, Uri p1, Bundle p2) {}
}
