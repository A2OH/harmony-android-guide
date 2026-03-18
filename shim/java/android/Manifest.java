package android;

/**
 * Shim for android.Manifest.
 * Provides permission and permission_group string constants.
 */
public final class Manifest {

    private Manifest() {}

    public static final class permission {
        public static final String INTERNET = "android.permission.INTERNET";
        public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
        public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";
        public static final String CAMERA = "android.permission.CAMERA";
        public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";
        public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
        public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
        public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";
        public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";
        public static final String CALL_PHONE = "android.permission.CALL_PHONE";
        public static final String SEND_SMS = "android.permission.SEND_SMS";
        public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";
        public static final String ACCESS_NETWORK_STATE = "android.permission.ACCESS_NETWORK_STATE";
        public static final String ACCESS_WIFI_STATE = "android.permission.ACCESS_WIFI_STATE";
        public static final String BLUETOOTH = "android.permission.BLUETOOTH";
        public static final String BLUETOOTH_ADMIN = "android.permission.BLUETOOTH_ADMIN";
        public static final String BLUETOOTH_CONNECT = "android.permission.BLUETOOTH_CONNECT";
        public static final String BLUETOOTH_SCAN = "android.permission.BLUETOOTH_SCAN";
        public static final String VIBRATE = "android.permission.VIBRATE";
        public static final String WAKE_LOCK = "android.permission.WAKE_LOCK";
        public static final String NFC = "android.permission.NFC";
        public static final String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";
        public static final String FOREGROUND_SERVICE = "android.permission.FOREGROUND_SERVICE";
        public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
        public static final String READ_CALENDAR = "android.permission.READ_CALENDAR";
        public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";
        public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";
        public static final String USE_BIOMETRIC = "android.permission.USE_BIOMETRIC";
        public static final String ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION";
        public static final String READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES";
        public static final String READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO";
        public static final String READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO";

        public static final String INTERACT_ACROSS_USERS_FULL = "android.permission.INTERACT_ACROSS_USERS_FULL";
        private permission() {}
    }

    public static final class permission_group {
        public static final String LOCATION = "android.permission-group.LOCATION";
        public static final String CAMERA = "android.permission-group.CAMERA";
        public static final String MICROPHONE = "android.permission-group.MICROPHONE";
        public static final String STORAGE = "android.permission-group.STORAGE";
        public static final String CONTACTS = "android.permission-group.CONTACTS";
        public static final String PHONE = "android.permission-group.PHONE";
        public static final String SMS = "android.permission-group.SMS";
        public static final String CALENDAR = "android.permission-group.CALENDAR";
        public static final String NEARBY_DEVICES = "android.permission-group.NEARBY_DEVICES";
        public static final String SENSORS = "android.permission-group.SENSORS";
        public static final String NOTIFICATIONS = "android.permission-group.NOTIFICATIONS";

        private permission_group() {}
    }
}
