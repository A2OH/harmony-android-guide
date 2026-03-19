package android.view.textclassifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible TextClassification shim. Data holder — all values are stubs.
 */
public class TextClassification {

    private final String text;
    private final List<String> entities;
    private final List<Float> scores;

    private TextClassification(Builder b) {
        this.text     = b.text;
        this.entities = new ArrayList<>(b.entities);
        this.scores   = new ArrayList<>(b.scores);
    }

    public String getText() { return text; }

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

    /** Always returns null — no drawable support in shim. */
    public Object getIcon() { return null; }

    /** Always returns null — no label in shim. */
    public CharSequence getLabel() { return null; }

    /** Always returns null — no intent in shim. */
    public android.content.Intent getIntent() { return null; }

    /** Always returns null — no click listener in shim. */
    public android.view.View.OnClickListener getOnClickListener() { return null; }

    /** Returns empty actions list. */
    public List<android.app.RemoteAction> getActions() { return new ArrayList<>(); }

    /** Create a PendingIntent from context and intent. */
    public static android.app.PendingIntent createPendingIntent(
            android.content.Context context, android.content.Intent intent, int requestCode) {
        return android.app.PendingIntent.getActivity(context, requestCode, intent, 0);
    }

    /** Create an OnClickListener that fires the given PendingIntent. */
    public static android.view.View.OnClickListener createIntentOnClickListener(
            android.app.PendingIntent pendingIntent) {
        return null;
    }

    /** Request class for text classification. */
    public static class Request {
        private final CharSequence mText;
        private final int mStartIndex;
        private final int mEndIndex;
        private Request(CharSequence text, int startIndex, int endIndex) {
            mText = text; mStartIndex = startIndex; mEndIndex = endIndex;
        }
        public CharSequence getText() { return mText; }
        public int getStartIndex() { return mStartIndex; }
        public int getEndIndex() { return mEndIndex; }
        public static class Builder {
            private CharSequence mText;
            private int mStartIndex;
            private int mEndIndex;
            public Builder(CharSequence text, int startIndex, int endIndex) {
                mText = text; mStartIndex = startIndex; mEndIndex = endIndex;
            }
            public Builder setDefaultLocales(android.os.LocaleList locales) { return this; }
            public Request build() { return new Request(mText, mStartIndex, mEndIndex); }
        }
    }

    // -----------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------

    public static final class Builder {
        private String text = "";
        private final List<String> entities = new ArrayList<>();
        private final List<Float>  scores   = new ArrayList<>();

        public Builder() {}

        public Builder setText(String text) {
            this.text = text == null ? "" : text;
            return this;
        }

        public Builder setEntityType(String type, float confidenceScore) {
            entities.add(type == null ? "" : type);
            scores.add(confidenceScore);
            return this;
        }

        public TextClassification build() {
            return new TextClassification(this);
        }
    }
}
