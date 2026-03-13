package android.media.audiofx;

import java.util.UUID;

/**
 * Android-compatible Equalizer shim. Stub with 5-band equalizer.
 */
public class Equalizer extends AudioEffect {

    private static final UUID TYPE_EQUALIZER =
            UUID.fromString("0bed4300-ddd6-11db-8f34-0002a5d5c51b");
    private static final UUID IMPL_EQUALIZER =
            UUID.fromString("e25aa840-543b-11df-98a5-0002a5d5c51b");

    private static final short NUM_BANDS    = 5;
    private static final short NUM_PRESETS  = 10;

    // Band centre frequencies in mHz: 60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz
    private static final int[] BAND_FREQ_MIN = { 30000,  120000,  460000, 1800000,  7000000 };
    private static final int[] BAND_FREQ_MAX = { 120000, 460000, 1800000, 7000000, 20000000 };

    private final short[] mBandLevels = new short[NUM_BANDS]; // in mB

    private static final String[] PRESET_NAMES = {
        "Normal", "Classical", "Dance", "Flat", "Folk",
        "Heavy Metal", "Hip Hop", "Jazz", "Pop", "Rock"
    };

    public Equalizer(int priority, int audioSession) {
        super(TYPE_EQUALIZER, IMPL_EQUALIZER, priority, audioSession);
    }

    public short getNumberOfBands() {
        checkNotReleased();
        return NUM_BANDS;
    }

    /**
     * Returns {minFreq, maxFreq} in mHz for the given band.
     */
    public int[] getBandFreqRange(short band) {
        checkNotReleased();
        if (band < 0 || band >= NUM_BANDS) throw new IllegalArgumentException("Invalid band: " + band);
        return new int[]{ BAND_FREQ_MIN[band], BAND_FREQ_MAX[band] };
    }

    public short getBandLevel(short band) {
        checkNotReleased();
        if (band < 0 || band >= NUM_BANDS) throw new IllegalArgumentException("Invalid band: " + band);
        return mBandLevels[band];
    }

    public void setBandLevel(short band, short level) {
        checkNotReleased();
        if (band < 0 || band >= NUM_BANDS) throw new IllegalArgumentException("Invalid band: " + band);
        mBandLevels[band] = level;
    }

    public short getNumberOfPresets() {
        checkNotReleased();
        return NUM_PRESETS;
    }

    public String getPresetName(short preset) {
        checkNotReleased();
        if (preset < 0 || preset >= NUM_PRESETS)
            throw new IllegalArgumentException("Invalid preset: " + preset);
        return PRESET_NAMES[preset];
    }

    public void usePreset(short preset) {
        checkNotReleased();
        if (preset < 0 || preset >= NUM_PRESETS)
            throw new IllegalArgumentException("Invalid preset: " + preset);
        // stub: apply flat band levels per preset index offset
        for (short i = 0; i < NUM_BANDS; i++) {
            mBandLevels[i] = (short)(preset * 100);
        }
    }

    /** Returns the band that best covers the given frequency (in mHz). */
    public short getBand(int frequency) {
        checkNotReleased();
        for (short i = 0; i < NUM_BANDS; i++) {
            if (frequency >= BAND_FREQ_MIN[i] && frequency <= BAND_FREQ_MAX[i]) return i;
        }
        return -1;
    }

    /** Returns {min, max} band level in mB. */
    public short[] getBandLevelRange() {
        checkNotReleased();
        return new short[]{ -1500, 1500 };
    }
}
