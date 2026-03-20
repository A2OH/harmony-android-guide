package android.text;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Stub: FontConfig — system font configuration.
 */
public class FontConfig {

    public FontConfig(List<Family> families, List<Alias> aliases) {}

    public List<Family> getFamilies() { return Collections.emptyList(); }
    public List<Alias> getAliases() { return Collections.emptyList(); }

    public static class Family {
        public Family(String name, List<Font> fonts, String language, int variant) {}
        public String getName() { return null; }
        public List<Font> getFonts() { return Collections.emptyList(); }
        public String getLanguage() { return null; }
        public int getVariant() { return 0; }
    }

    public static class Font {
        public Font(String fontName, int ttcIndex, List axes, int weight, boolean isItalic) {}
        public String getFontName() { return ""; }
        public int getTtcIndex() { return 0; }
        public int getWeight() { return 400; }
        public boolean isItalic() { return false; }
    }

    public static class Alias {
        public Alias(String name, String toName, int weight) {}
        public String getName() { return ""; }
        public String getToName() { return ""; }
        public int getWeight() { return 400; }
    }
}
