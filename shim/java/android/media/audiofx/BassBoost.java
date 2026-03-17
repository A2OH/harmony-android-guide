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
                String[] parts = splitByChar(settings.substring("BassBoost;".length()), ';');
                for (String kv : parts) {
                    int eq = kv.indexOf('=');
                    if (eq >= 0) {
                        String key = kv.substring(0, eq).trim();
                        String val = kv.substring(eq + 1).trim();
                        if ("strength".equals(key)) {
                            try { strength = Short.parseShort(val); } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
        }

        private static String[] splitByChar(String s, char delim) {
            java.util.List<String> parts = new java.util.ArrayList<>();
            int start = 0;
            for (int i = 0; i <= s.length(); i++) {
                if (i == s.length() || s.charAt(i) == delim) {
                    parts.add(s.substring(start, i));
                    start = i + 1;
                }
            }
            return parts.toArray(new String[0]);
        }

        @Override
        public String toString() {
            return "BassBoost;strength=" + strength;
        }
    }
}
