package android.service.carrier;

import android.app.Service;

/**
 * Android-compatible CarrierService shim. Stub for carrier configuration service.
 */
public abstract class CarrierService extends Service {

    public Object onLoadConfig(CarrierIdentifier id) {
        return null;
    }

    public static final class CarrierIdentifier {
        private final String mMcc;
        private final String mMnc;
        private final String mSpn;
        private final String mImsi;
        private final String mGid1;
        private final String mGid2;

        public CarrierIdentifier(String mcc, String mnc, String spn,
                String imsi, String gid1, String gid2) {
            mMcc  = mcc  != null ? mcc  : "";
            mMnc  = mnc  != null ? mnc  : "";
            mSpn  = spn  != null ? spn  : "";
            mImsi = imsi != null ? imsi : "";
            mGid1 = gid1 != null ? gid1 : "";
            mGid2 = gid2 != null ? gid2 : "";
        }

        public String getMcc()  { return mMcc;  }
        public String getMnc()  { return mMnc;  }
        public String getSpn()  { return mSpn;  }
        public String getImsi() { return mImsi; }
        public String getGid1() { return mGid1; }
        public String getGid2() { return mGid2; }
    }
}
