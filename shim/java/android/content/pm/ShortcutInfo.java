package android.content.pm;

// Shim: android.content.pm.ShortcutInfo
// Android-to-OpenHarmony migration compatibility stub.

public class ShortcutInfo {

    private final String id;
    private final CharSequence shortLabel;
    private final CharSequence longLabel;
    private final Object activity;
    private final Object intent;
    private final int rank;

    private ShortcutInfo(Builder builder) {
        this.id = builder.id;
        this.shortLabel = builder.shortLabel;
        this.longLabel = builder.longLabel;
        this.activity = builder.activity;
        this.intent = builder.intent;
        this.rank = builder.rank;
    }

    public String getId() {
        return id;
    }

    public CharSequence getShortLabel() {
        return shortLabel;
    }

    public CharSequence getLongLabel() {
        return longLabel;
    }

    public Object getActivity() {
        return activity;
    }

    public Object getIntent() {
        return intent;
    }

    public int getRank() {
        return rank;
    }

    public static final class Builder {

        private final String id;
        private CharSequence shortLabel;
        private CharSequence longLabel;
        private Object activity;
        private Object intent;
        private int rank;

        public Builder(String id) {
            this.id = id;
        }

        public Builder setShortLabel(CharSequence shortLabel) {
            this.shortLabel = shortLabel;
            return this;
        }

        public Builder setLongLabel(CharSequence longLabel) {
            this.longLabel = longLabel;
            return this;
        }

        public Builder setActivity(Object componentName) {
            this.activity = componentName;
            return this;
        }

        public Builder setIntent(Object intent) {
            this.intent = intent;
            return this;
        }

        public Builder setRank(int rank) {
            this.rank = rank;
            return this;
        }

        public ShortcutInfo build() {
            return new ShortcutInfo(this);
        }
    }
}
