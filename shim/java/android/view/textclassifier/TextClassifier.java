package android.view.textclassifier;

/**
 * Android-compatible TextClassifier shim. Interface stub — all classify operations are no-ops.
 */
public interface TextClassifier {

    String TYPE_ADDRESS       = "address";
    String TYPE_EMAIL         = "email";
    String TYPE_PHONE         = "phone";
    String TYPE_URL           = "url";
    String TYPE_DATE          = "date";
    String TYPE_DATE_TIME     = "datetime";
    String TYPE_FLIGHT_NUMBER = "flight";

    /** A no-op TextClassifier that returns empty stubs for every operation. */
    TextClassifier NO_OP = new TextClassifier() {
        @Override
        public TextClassification classifyText(CharSequence text, int startIndex, int endIndex, Object options) {
            return new TextClassification.Builder().setText(text == null ? "" : text.subSequence(startIndex, endIndex).toString()).build();
        }
        @Override
        public TextSelection suggestSelection(CharSequence text, int selectionStartIndex, int selectionEndIndex, Object options) {
            return new TextSelection.Builder(selectionStartIndex, selectionEndIndex).build();
        }
        @Override
        public TextLinks generateLinks(CharSequence text, Object options) {
            return new TextLinks.Builder(text == null ? "" : text.toString()).build();
        }
        @Override
        public String detectLanguage(CharSequence text) {
            return "";
        }
    };

    TextClassification classifyText(CharSequence text, int startIndex, int endIndex, Object options);

    TextSelection suggestSelection(CharSequence text, int selectionStartIndex, int selectionEndIndex, Object options);

    TextLinks generateLinks(CharSequence text, Object options);

    String detectLanguage(CharSequence text);

    String WIDGET_TYPE_TEXTVIEW = "textview";
    String WIDGET_TYPE_EDITTEXT = "edittext";
    String WIDGET_TYPE_UNSELECTABLE_TEXTVIEW = "nosel-textview";
}
