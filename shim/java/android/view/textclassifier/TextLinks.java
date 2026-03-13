package android.view.textclassifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Android-compatible TextLinks shim. Data holder — all values are stubs.
 */
public class TextLinks {

    private final List<TextLink> links;

    private TextLinks(Builder b) {
        this.links = Collections.unmodifiableList(new ArrayList<>(b.links));
    }

    public Collection<TextLink> getLinks() { return links; }

    // -----------------------------------------------------------------
    // TextLink inner class
    // -----------------------------------------------------------------

    public static final class TextLink {
        private final int start;
        private final int end;
        private final List<String> entities;
        private final List<Float>  scores;

        public TextLink(int start, int end, List<String> entities, List<Float> scores) {
            this.start    = start;
            this.end      = end;
            this.entities = new ArrayList<>(entities);
            this.scores   = new ArrayList<>(scores);
        }

        public int getStart() { return start; }

        public int getEnd() { return end; }

        public int getEntityCount() { return entities.size(); }

        public String getEntity(int index) {
            if (index < 0 || index >= entities.size()) return "";
            return entities.get(index);
        }

        public float getConfidenceScore(String entity) {
            int idx = entities.indexOf(entity);
            if (idx < 0) return 0f;
            return scores.get(idx);
        }
    }

    // -----------------------------------------------------------------
    // Request inner class
    // -----------------------------------------------------------------

    public static final class Object {
        private final CharSequence text;

        public Object(CharSequence text) {
            this.text = text;
        }

        public CharSequence getText() { return text; }
    }

    // -----------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------

    public static final class Builder {
        private final String fullText;
        private final List<TextLink> links = new ArrayList<>();

        public Builder(String fullText) {
            this.fullText = fullText == null ? "" : fullText;
        }

        public Builder addLink(int start, int end, List<String> entityTypes, List<Float> scores) {
            links.add(new TextLink(start, end, entityTypes, scores));
            return this;
        }

        public TextLinks build() {
            return new TextLinks(this);
        }
    }
}
