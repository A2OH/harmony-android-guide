package android.media;

/**
 * Android-compatible AudioDeviceInfo shim. Stub for audio device information.
 */
public class AudioDeviceInfo {

    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_BUILTIN_EARPIECE = 1;
    public static final int TYPE_BUILTIN_SPEAKER = 2;
    public static final int TYPE_WIRED_HEADSET = 3;
    public static final int TYPE_WIRED_HEADPHONES = 4;
    public static final int TYPE_LINE_ANALOG = 5;
    public static final int TYPE_LINE_DIGITAL = 6;
    public static final int TYPE_BLUETOOTH_SCO = 7;
    public static final int TYPE_BLUETOOTH_A2DP = 8;
    public static final int TYPE_HDMI = 9;
    public static final int TYPE_HDMI_ARC = 10;
    public static final int TYPE_USB_DEVICE = 11;
    public static final int TYPE_USB_ACCESSORY = 12;
    public static final int TYPE_DOCK = 13;
    public static final int TYPE_FM = 14;
    public static final int TYPE_BUILTIN_MIC = 15;
    public static final int TYPE_FM_TUNER = 16;
    public static final int TYPE_TV_TUNER = 17;
    public static final int TYPE_TELEPHONY = 18;
    public static final int TYPE_AUX_LINE = 19;
    public static final int TYPE_IP = 20;
    public static final int TYPE_BUS = 21;
    public static final int TYPE_USB_HEADSET = 22;

    public AudioDeviceInfo() {}

    public int getType() { return TYPE_UNKNOWN; }

    public int getId() { return 0; }

    public CharSequence getProductName() { return ""; }

    public String getAddress() { return ""; }

    public boolean isSource() { return false; }

    public boolean isSink() { return false; }

    public int[] getSampleRates() { return new int[0]; }

    public int[] getChannelMasks() { return new int[0]; }

    public int[] getChannelIndexMasks() { return new int[0]; }

    public int[] getChannelCounts() { return new int[0]; }

    public int[] getEncodings() { return new int[0]; }
}
