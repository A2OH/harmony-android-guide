package android.os;

/**
 * Shim: android.os.VibrationEffect — abstract class representing a
 * vibration effect that can be played by a Vibrator.
 */
public class VibrationEffect {

    /** The default vibration strength of the device. */
    public static final int DEFAULT_AMPLITUDE = -1;

    /**
     * Vibration amplitude representing no vibration (off period in a waveform).
     */
    public static final int EFFECT_CLICK = 0;
    public static final int EFFECT_DOUBLE_CLICK = 1;
    public static final int EFFECT_HEAVY_CLICK = 5;
    public static final int EFFECT_TICK = 2;

    VibrationEffect() {}

    /**
     * Create a one-shot vibration.
     * @param milliseconds duration in milliseconds
     * @param amplitude    vibration amplitude (1-255) or DEFAULT_AMPLITUDE
     */
    public static VibrationEffect createOneShot(long milliseconds, int amplitude) {
        return new OneShotEffect(milliseconds, amplitude);
    }

    /**
     * Create a waveform vibration.
     * @param timings    array of durations alternating between on and off
     * @param repeat     index to repeat from, or -1 for no repeat
     */
    public static VibrationEffect createWaveform(long[] timings, int repeat) {
        return new WaveformEffect(timings, null, repeat);
    }

    /**
     * Create a waveform vibration with amplitude control.
     * @param timings     array of durations
     * @param amplitudes  amplitude for each timing entry
     * @param repeat      index to repeat from, or -1 for no repeat
     */
    public static VibrationEffect createWaveform(long[] timings, int[] amplitudes, int repeat) {
        return new WaveformEffect(timings, amplitudes, repeat);
    }

    /**
     * Create a predefined vibration effect.
     * @param effectId one of EFFECT_CLICK, EFFECT_DOUBLE_CLICK, etc.
     */
    public static VibrationEffect createPredefined(int effectId) {
        return new PredefinedEffect(effectId);
    }

    // ── Inner stub implementations ──

    private static final class OneShotEffect extends VibrationEffect {
        final long milliseconds;
        final int amplitude;
        OneShotEffect(long milliseconds, int amplitude) {
            this.milliseconds = milliseconds;
            this.amplitude = amplitude;
        }
    }

    private static final class WaveformEffect extends VibrationEffect {
        final long[] timings;
        final int[] amplitudes;
        final int repeat;
        WaveformEffect(long[] timings, int[] amplitudes, int repeat) {
            this.timings = timings;
            this.amplitudes = amplitudes;
            this.repeat = repeat;
        }
    }

    private static final class PredefinedEffect extends VibrationEffect {
        final int effectId;
        PredefinedEffect(int effectId) {
            this.effectId = effectId;
        }
    }
}
