package android.view.inputmethod;

/**
 * Android-compatible InputMethodSubtype shim.
 * Information about a specific configuration of an input method.
 */
public class InputMethodSubtype {
    private String mLocale;
    private String mMode;
    private String mLanguageTag;

    private InputMethodSubtype(Builder builder) {
        mLocale = builder.mLocale;
        mMode = builder.mMode;
        mLanguageTag = builder.mLanguageTag;
    }

    public String getLocale() {
        return mLocale;
    }

    public String getMode() {
        return mMode;
    }

    public String getLanguageTag() {
        return mLanguageTag;
    }

    public static final class Builder {
        private String mLocale = "";
        private String mMode = "";
        private String mLanguageTag = "";

        public Builder() {}

        public Builder setSubtypeLocale(String locale) {
            mLocale = locale != null ? locale : "";
            return this;
        }

        public Builder setIsAuxiliary(boolean isAuxiliary) {
            return this;
        }

        public Builder setMode(String mode) {
            mMode = mode != null ? mode : "";
            return this;
        }

        public Builder setLanguageTag(String languageTag) {
            mLanguageTag = languageTag != null ? languageTag : "";
            return this;
        }

        public InputMethodSubtype build() {
            return new InputMethodSubtype(this);
        }
    }
}
