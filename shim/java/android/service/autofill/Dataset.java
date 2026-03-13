package android.service.autofill;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;

import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Android-compatible Dataset shim. A dataset is a group of field values used to fill a form.
 */
public final class Dataset {

    private final Map<AutofillId, AutofillValue> mFields;
    private final Object mAuthentication;

    private Dataset(Builder builder) {
        mFields         = new LinkedHashMap<>(builder.mFields);
        mAuthentication = builder.mAuthentication;
    }

    public Map<AutofillId, AutofillValue> getFields() {
        return mFields;
    }

    public Object getAuthentication() {
        return mAuthentication;
    }

    // --- Builder ---

    public static final class Builder {

        private final Map<AutofillId, AutofillValue> mFields = new LinkedHashMap<>();
        private Object mAuthentication;

        public Builder() {}

        public Builder setValue(AutofillId id, AutofillValue value) {
            mFields.put(id, value);
            return this;
        }

        public Builder setAuthentication(Object intentSender) {
            mAuthentication = intentSender;
            return this;
        }

        public Dataset build() {
            return new Dataset(this);
        }
    }
}
