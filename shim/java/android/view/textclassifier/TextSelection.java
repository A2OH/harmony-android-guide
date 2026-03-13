package android.view.textclassifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible TextSelection shim. Data holder — all values are stubs.
 */
public class TextSelection {

    private final int startIndex;
    private final int endIndex;
    private final List<String> entities;
    private final List<Float>  scores;

    private TextSelection(Builder b) {
        this.startIndex = b.startIndex;
        this.endIndex   = b.endIndex;
        this.entities   = new ArrayList<>(b.entities);
        this.scores     = new ArrayList<>(b.scores);
    }

    public int getSelectionStartIndex() { return startIndex; }

    public int getSelectionEndIndex() { return endIndex; }

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

    // -----------------------------------------------------------------
    // Request inner class
    // -----------------------------------------------------------------

    public static final class Request {
        private final CharSequence text;
        private final int startIndex;
        private final int endIndex;

        public Request(CharSequence text, int startIndex, int endIndex) {
            this.text       = text;
            this.startIndex = startIndex;
            this.endIndex   = endIndex;
        }

        public CharSequence getText()      { return text; }
        public int getStartIndex()         { return startIndex; }
        public int getEndIndex()           { return endIndex; }
    }

    // -----------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------

    public static final class Builder {
        private final int startIndex;
        private final int endIndex;
        private final List<String> entities = new ArrayList<>();
        private final List<Float>  scores   = new ArrayList<>();

        public Builder(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex   = endIndex;
        }

        public Builder setEntityType(String type, float confidenceScore) {
            entities.add(type == null ? "" : type);
            scores.add(confidenceScore);
            return this;
        }

        public TextSelection build() {
            return new TextSelection(this);
        }
    }
}
