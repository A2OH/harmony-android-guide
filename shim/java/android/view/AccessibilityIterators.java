package android.view;
/** AOSP compilation stub. */
public class AccessibilityIterators {
    public interface TextSegmentIterator {
        int[] following(int current);
        int[] preceding(int current);
    }
    public static class CharacterTextSegmentIterator implements TextSegmentIterator {
        protected CharacterTextSegmentIterator() {}
        public static CharacterTextSegmentIterator getInstance(java.util.Locale locale) { return new CharacterTextSegmentIterator(); }
        public void initialize(String text) {}
        public int[] following(int current) { return null; }
        public int[] preceding(int current) { return null; }
    }
    public static class WordTextSegmentIterator implements TextSegmentIterator {
        protected WordTextSegmentIterator() {}
        public static WordTextSegmentIterator getInstance(java.util.Locale locale) { return new WordTextSegmentIterator(); }
        public void initialize(String text) {}
        public int[] following(int current) { return null; }
        public int[] preceding(int current) { return null; }
    }
    public static class ParagraphTextSegmentIterator implements TextSegmentIterator {
        protected ParagraphTextSegmentIterator() {}
        public static ParagraphTextSegmentIterator getInstance() { return new ParagraphTextSegmentIterator(); }
        public void initialize(String text) {}
        public int[] following(int current) { return null; }
        public int[] preceding(int current) { return null; }
    }
    public static class LineTextSegmentIterator implements TextSegmentIterator {
        protected LineTextSegmentIterator() {}
        public static LineTextSegmentIterator getInstance() { return new LineTextSegmentIterator(); }
        public void initialize(String text) {}
        public int[] following(int current) { return null; }
        public int[] preceding(int current) { return null; }
    }
    public static class PageTextSegmentIterator implements TextSegmentIterator {
        protected PageTextSegmentIterator() {}
        public static PageTextSegmentIterator getInstance() { return new PageTextSegmentIterator(); }
        public void initialize(String text) {}
        public int[] following(int current) { return null; }
        public int[] preceding(int current) { return null; }
    }
}
