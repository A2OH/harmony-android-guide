package android.graphics.fonts;

import android.text.FontConfig;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * Stub: SystemFonts — provides system font information.
 */
public final class SystemFonts {
    private SystemFonts() {}

    private static final FontFamily[] EMPTY_FAMILIES = new FontFamily[0];

    public static FontFamily[] getSystemFallback(String familyName) {
        return EMPTY_FAMILIES;
    }

    public static Map<String, FontFamily[]> getRawSystemFallbackMap() {
        return Collections.emptyMap();
    }

    public static FontConfig.Alias[] getAliases() {
        return new FontConfig.Alias[0];
    }

    public static Set<Font> getAvailableFonts() {
        return Collections.emptySet();
    }
}
