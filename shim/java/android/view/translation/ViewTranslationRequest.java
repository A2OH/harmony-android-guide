package android.view.translation;
import android.view.View;
import android.view.View;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Android-compatible ViewTranslationRequest stub (API 31+).
 * Represents a request to translate content in a View.
 */
public class ViewTranslationRequest {

    private final int mAutofillId;
    private final Map<String, Object> mValues;

    private ViewTranslationRequest(int autofillId, Map<String, Object> values) {
        mAutofillId = autofillId;
        mValues = Collections.unmodifiableMap(values);
    }

    /** Returns the autofill ID associated with this request. Stub returns stored value. */
    public int getAutofillId() {
        return mAutofillId;
    }

    /** Returns the set of keys in this request. */
    public Set<String> getKeys() {
        return mValues.keySet();
    }

    /**
     * Builder for constructing a ViewTranslationRequest.
     */
    public static class Builder {
        private final int mAutofillId;
        private final Map<String, Object> mValues = new LinkedHashMap<>();

        public Builder(int autofillId) {
            mAutofillId = autofillId;
        }

        /** Sets a key-value pair in the request. */
        public Builder setValue(String key, Object value) {
            mValues.put(key, value);
            return this;
        }

        /** Builds the ViewTranslationRequest. */
        public ViewTranslationRequest build() {
            return new ViewTranslationRequest(mAutofillId, mValues);
        }
    }
}
