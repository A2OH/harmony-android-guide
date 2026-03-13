package android.telephony;

import android.os.PersistableBundle;

/**
 * Android-compatible CarrierConfigManager shim. Stub implementation for mock testing.
 */
public class CarrierConfigManager {

    public static final String ACTION_CARRIER_CONFIG_CHANGED =
            "android.telephony.action.CARRIER_CONFIG_CHANGED";

    // VoLTE / VT / WFC availability
    public static final String KEY_CARRIER_VOLTE_AVAILABLE_BOOL =
            "carrier_volte_available_bool";
    public static final String KEY_CARRIER_VT_AVAILABLE_BOOL =
            "carrier_vt_available_bool";
    public static final String KEY_CARRIER_WFC_AVAILABLE_BOOL =
            "carrier_wfc_available_bool";
    public static final String KEY_CARRIER_WFC_IMS_AVAILABLE_BOOL =
            "carrier_wfc_ims_available_bool";

    // Network settings visibility
    public static final String KEY_HIDE_CARRIER_NETWORK_SETTINGS_BOOL =
            "hide_carrier_network_settings_bool";
    public static final String KEY_DISABLE_SUPPLEMENTARY_SERVICES_IN_AIRPLANE_MODE_BOOL =
            "disable_supplementary_services_in_airplane_mode_bool";

    // SIM / subscription
    public static final String KEY_CARRIER_NAME_OVERRIDE_BOOL =
            "carrier_name_override_bool";
    public static final String KEY_CARRIER_NAME_STRING =
            "carrier_name_string";
    public static final String KEY_FORCE_HOME_NETWORK_BOOL =
            "force_home_network_bool";
    public static final String KEY_SHOW_APN_SETTING_CDMA_BOOL =
            "show_apn_setting_cdma_bool";
    public static final String KEY_HIDE_SIM_LOCK_SETTINGS_BOOL =
            "hide_sim_lock_settings_bool";

    // Data / APN
    public static final String KEY_CARRIER_DATA_CALL_RETRY_CONFIG_STRINGS =
            "carrier_data_call_retry_config_strings";
    public static final String KEY_CARRIER_DEFAULT_DATA_ROAMING_ENABLED_BOOL =
            "carrier_default_data_roaming_enabled_bool";
    public static final String KEY_DATA_LIMIT_THRESHOLD_BYTES_LONG =
            "data_limit_threshold_bytes_long";
    public static final String KEY_DATA_WARNING_THRESHOLD_BYTES_LONG =
            "data_warning_threshold_bytes_long";

    // Call / DTMF
    public static final String KEY_CARRIER_ALLOW_TURNOFF_IMS_BOOL =
            "carrier_allow_turnoff_ims_bool";
    public static final String KEY_DTMF_TYPE_ENABLED_BOOL =
            "dtmf_type_enabled_bool";
    public static final String KEY_CARRIER_USE_IMS_FIRST_FOR_EMERGENCY_BOOL =
            "carrier_use_ims_first_for_emergency_bool";
    public static final String KEY_CARRIER_IMS_GBA_REQUIRED_BOOL =
            "carrier_ims_gba_required_bool";

    // CDMA
    public static final String KEY_CDMA_NONROAMING_NETWORKS_STRING_ARRAY =
            "cdma_nonroaming_networks_string_array";
    public static final String KEY_CDMA_ROAMING_NETWORKS_STRING_ARRAY =
            "cdma_roaming_networks_string_array";

    // Emergency
    public static final String KEY_EMERGENCY_NUMBER_PREFIX_STRING_ARRAY =
            "emergency_number_prefix_string_array";
    public static final String KEY_CARRIER_SUPPORTS_SS_OVER_UT_BOOL =
            "carrier_supports_ss_over_ut_bool";

    // Roaming
    public static final String KEY_FORCE_HOME_NETWORK_IN_ROAMING_BOOL =
            "force_home_network_in_roaming_bool";
    public static final String KEY_IS_ROAMING_INDICATOR_FROM_NETWORK_ENABLED_BOOL =
            "is_roaming_indicator_from_network_enabled_bool";

    public PersistableBundle getConfig() {
        return new PersistableBundle();
    }

    public PersistableBundle getConfigForSubId(int subId) {
        return new PersistableBundle();
    }
}
