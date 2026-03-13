package android.media.audiofx;
import android.se.omapi.Channel;
import android.util.Config;
import android.se.omapi.Channel;
import android.util.Config;

public final class DynamicsProcessing extends AudioEffect {
    public static final int VARIANT_FAVOR_FREQUENCY_RESOLUTION = 0;
    public static final int VARIANT_FAVOR_TIME_RESOLUTION = 0;

    public DynamicsProcessing(int p0) {}
    public DynamicsProcessing(int p0, int p1, Config p2) {}

    public Channel getChannelByChannelIndex(int p0) { return null; }
    public int getChannelCount() { return 0; }
    public Config getConfig() { return null; }
    public float getInputGainByChannelIndex(int p0) { return 0f; }
    public Object getLimiterByChannelIndex(int p0) { return null; }
    public Object getMbcBandByChannelIndex(int p0, int p1) { return null; }
    public Object getMbcByChannelIndex(int p0) { return null; }
    public Object getPostEqBandByChannelIndex(int p0, int p1) { return null; }
    public Object getPostEqByChannelIndex(int p0) { return null; }
    public Object getPreEqBandByChannelIndex(int p0, int p1) { return null; }
    public Object getPreEqByChannelIndex(int p0) { return null; }
    public void setAllChannelsTo(Channel p0) {}
    public void setChannelTo(int p0, Channel p1) {}
    public void setInputGainAllChannelsTo(float p0) {}
    public void setInputGainbyChannel(int p0, float p1) {}
    public void setLimiterAllChannelsTo(Object p0) {}
    public void setLimiterByChannelIndex(int p0, Object p1) {}
    public void setMbcAllChannelsTo(Object p0) {}
    public void setMbcBandAllChannelsTo(int p0, Object p1) {}
    public void setMbcBandByChannelIndex(int p0, int p1, Object p2) {}
    public void setMbcByChannelIndex(int p0, Object p1) {}
    public void setPostEqAllChannelsTo(Object p0) {}
    public void setPostEqBandAllChannelsTo(int p0, Object p1) {}
    public void setPostEqBandByChannelIndex(int p0, int p1, Object p2) {}
    public void setPostEqByChannelIndex(int p0, Object p1) {}
    public void setPreEqAllChannelsTo(Object p0) {}
    public void setPreEqBandAllChannelsTo(int p0, Object p1) {}
    public void setPreEqBandByChannelIndex(int p0, int p1, Object p2) {}
    public void setPreEqByChannelIndex(int p0, Object p1) {}
}
