package android.media.audiofx;
import android.provider.Settings;
import android.provider.Settings;

import java.util.UUID;

/**
 * Android-compatible BassBoost shim. Stub for bass boost audio effect.
 */
public class BassBoost extends AudioEffect {

    private static final UUID TYPE_BASS_BOOST =
            UUID.fromString("0634f220-ddd4-11db-a0fc-0002a5d5c51b");
    private static final UUID IMPL_BASS_BOOST =
            UUID.fromString("fa8181f2-588b-11ed-9b6a-0242ac120002");

    private static final boolean STRENGTH_SUPPORTED = true;
    private short mStrength; // 0..1000

    public BassBoost(int priority, int audioSession) {
        super(TYPE_BASS_BOOST, IMPL_BASS_BOOST, priority, audioSession);
        mStrength = 0;
    }

    public void setStrength(short strength) {
        checkNotReleased();
        if (strength < 0 || strength > 1000)
            throw new IllegalArgumentException("Strength out of range [0,1000]: " + strength);
        mStrength = strength;
    }

    public short getRoundedStrength() {
        checkNotReleased();
        return mStrength;
    }

    public boolean getStrengthSupported() {
        checkNotReleased();
        return STRENGTH_SUPPORTED;
    }

    // -----------------------------------------------------------------------
    public static final class Settings {
        public short strength;

        public Settings() { strength = 0; }

        public Settings(String settings) {
            // parse "BassBoost;strength=NNN"
            strength = 0;
            if (settings != null && settings.startsWith("BassBoost;")) {
                String[] parts = settings.substring("BassBoost;".length()).split(";");
                for (String kv : parts) {
                    String[] pair = kv.split("=");
                    if (pair.length == 2 && "strength".equals(pair[0].trim())) {
                        try { strength = Short.parseShort(pair[1].trim()); } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "BassBoost;strength=" + strength;
        }
    }
}
