package android.service.autofill;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillId;

import android.view.autofill.AutofillId;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible FillResponse shim. A response to a {@link FillRequest}.
 */
public final class FillResponse {

    private final List<Dataset>  mDatasets;
    private final SaveInfo       mSaveInfo;
    private final Object         mAuthentication;
    private final AutofillId[]   mIgnoredIds;

    private FillResponse(Builder builder) {
        mDatasets       = new ArrayList<>(builder.mDatasets);
        mSaveInfo       = builder.mSaveInfo;
        mAuthentication = builder.mAuthentication;
        mIgnoredIds     = builder.mIgnoredIds;
    }

    public List<Dataset>  getDatasets()       { return mDatasets; }
    public SaveInfo       getSaveInfo()        { return mSaveInfo; }
    public Object         getAuthentication()  { return mAuthentication; }
    public AutofillId[]   getIgnoredIds()      { return mIgnoredIds; }

    // --- Builder ---

    public static final class Builder {

        private final List<Dataset> mDatasets = new ArrayList<>();
        private SaveInfo     mSaveInfo;
        private Object       mAuthentication;
        private AutofillId[] mIgnoredIds;

        public Builder addDataset(Dataset dataset) {
            if (dataset != null) mDatasets.add(dataset);
            return this;
        }

        public Builder setSaveInfo(SaveInfo saveInfo) {
            mSaveInfo = saveInfo;
            return this;
        }

        public Builder setAuthentication(AutofillId[] ids, Object intentSender, Object presentation) {
            mAuthentication = intentSender;
            return this;
        }

        public Builder setIgnoredIds(AutofillId... ids) {
            mIgnoredIds = ids;
            return this;
        }

        public FillResponse build() {
            return new FillResponse(this);
        }
    }
}
