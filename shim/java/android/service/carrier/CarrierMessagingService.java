package android.service.carrier;

public class CarrierMessagingService {
    public CarrierMessagingService() {}

    public static final int DOWNLOAD_STATUS_ERROR = 0;
    public static final int DOWNLOAD_STATUS_OK = 0;
    public static final int DOWNLOAD_STATUS_RETRY_ON_CARRIER_NETWORK = 0;
    public static final int RECEIVE_OPTIONS_DEFAULT = 0;
    public static final int RECEIVE_OPTIONS_DROP = 0;
    public static final int RECEIVE_OPTIONS_SKIP_NOTIFY_WHEN_CREDENTIAL_PROTECTED_STORAGE_UNAVAILABLE = 0;
    public static final int SEND_FLAG_REQUEST_DELIVERY_STATUS = 0;
    public static final int SEND_STATUS_ERROR = 0;
    public static final int SEND_STATUS_OK = 0;
    public static final int SEND_STATUS_RETRY_ON_CARRIER_NETWORK = 0;
    public static final int SERVICE_INTERFACE = 0;
    public void onDownloadMms(Object p0, Object p1, Object p2, Object p3) {}
    public void onReceiveTextSms(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public void onSendDataSms(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {}
    public void onSendMms(Object p0, Object p1, Object p2, Object p3) {}
    public void onSendMultipartTextSms(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public void onSendTextSms(Object p0, Object p1, Object p2, Object p3, Object p4) {}
}
