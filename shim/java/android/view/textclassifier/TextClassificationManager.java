package android.view.textclassifier;

/**
 * Android-compatible TextClassificationManager shim. Always returns NO_OP classifier.
 */
public class TextClassificationManager {

    private TextClassifier classifier = TextClassifier.NO_OP;

    public TextClassifier getTextClassifier() {
        return classifier;
    }

    public void setTextClassifier(TextClassifier classifier) {
        this.classifier = classifier == null ? TextClassifier.NO_OP : classifier;
    }

    /**
     * Creates a lightweight session wrapper around the current classifier.
     * The returned object implements TextClassifier and delegates all calls.
     */
    public TextClassifier createTextClassificationSession(Object request) {
        return classifier;
    }

    public TextClassifier createTextClassificationSession(Object context, TextClassifier classifier) {
        return classifier != null ? classifier : this.classifier;
    }
}
