/*
 * permission_mapper.cpp
 *
 * Bidirectional permission name mapping between Android and OpenHarmony.
 */
#include "permission_mapper.h"

#include <unordered_map>

namespace oh_adapter {

namespace {

// Android -> OH permission mapping table
const std::unordered_map<std::string, std::string>& GetAndroidToOHMap() {
    static const std::unordered_map<std::string, std::string> sMap = {
        // Network
        {"android.permission.INTERNET",                 "ohos.permission.INTERNET"},
        {"android.permission.ACCESS_NETWORK_STATE",     "ohos.permission.GET_NETWORK_INFO"},
        {"android.permission.ACCESS_WIFI_STATE",        "ohos.permission.GET_WIFI_INFO"},
        {"android.permission.CHANGE_WIFI_STATE",        "ohos.permission.SET_WIFI_INFO"},
        {"android.permission.CHANGE_NETWORK_STATE",     "ohos.permission.SET_NETWORK_INFO"},

        // Camera & Media
        {"android.permission.CAMERA",                   "ohos.permission.CAMERA"},
        {"android.permission.RECORD_AUDIO",             "ohos.permission.MICROPHONE"},

        // Storage
        {"android.permission.READ_EXTERNAL_STORAGE",    "ohos.permission.READ_MEDIA"},
        {"android.permission.WRITE_EXTERNAL_STORAGE",   "ohos.permission.WRITE_MEDIA"},
        {"android.permission.MANAGE_EXTERNAL_STORAGE",  "ohos.permission.FILE_ACCESS_MANAGER"},

        // Location
        {"android.permission.ACCESS_FINE_LOCATION",     "ohos.permission.APPROXIMATELY_LOCATION"},
        {"android.permission.ACCESS_COARSE_LOCATION",   "ohos.permission.APPROXIMATELY_LOCATION"},
        {"android.permission.ACCESS_BACKGROUND_LOCATION", "ohos.permission.LOCATION_IN_BACKGROUND"},

        // Contacts
        {"android.permission.READ_CONTACTS",            "ohos.permission.READ_CONTACTS"},
        {"android.permission.WRITE_CONTACTS",           "ohos.permission.WRITE_CONTACTS"},

        // Phone
        {"android.permission.READ_PHONE_STATE",         "ohos.permission.GET_TELEPHONY_STATE"},
        {"android.permission.CALL_PHONE",               "ohos.permission.PLACE_CALL"},
        {"android.permission.READ_CALL_LOG",            "ohos.permission.READ_CALL_LOG"},
        {"android.permission.WRITE_CALL_LOG",           "ohos.permission.WRITE_CALL_LOG"},
        {"android.permission.READ_PHONE_NUMBERS",       "ohos.permission.GET_TELEPHONY_STATE"},

        // SMS
        {"android.permission.SEND_SMS",                 "ohos.permission.SEND_MESSAGES"},
        {"android.permission.READ_SMS",                 "ohos.permission.READ_MESSAGES"},
        {"android.permission.RECEIVE_SMS",              "ohos.permission.RECEIVE_SMS"},

        // Calendar
        {"android.permission.READ_CALENDAR",            "ohos.permission.READ_CALENDAR"},
        {"android.permission.WRITE_CALENDAR",           "ohos.permission.WRITE_CALENDAR"},

        // Sensors
        {"android.permission.BODY_SENSORS",             "ohos.permission.READ_HEALTH_DATA"},

        // Bluetooth
        {"android.permission.BLUETOOTH",                "ohos.permission.USE_BLUETOOTH"},
        {"android.permission.BLUETOOTH_ADMIN",          "ohos.permission.MANAGE_BLUETOOTH"},
        {"android.permission.BLUETOOTH_CONNECT",        "ohos.permission.USE_BLUETOOTH"},
        {"android.permission.BLUETOOTH_SCAN",           "ohos.permission.USE_BLUETOOTH"},

        // System
        {"android.permission.VIBRATE",                  "ohos.permission.VIBRATE"},
        {"android.permission.WAKE_LOCK",                "ohos.permission.RUNNING_LOCK"},
        {"android.permission.RECEIVE_BOOT_COMPLETED",   "ohos.permission.RUNNING_LOCK"},
        {"android.permission.SET_ALARM",                "ohos.permission.PUBLISH_AGENT_REMINDER"},
        {"android.permission.REQUEST_INSTALL_PACKAGES", "ohos.permission.INSTALL_BUNDLE"},
        {"android.permission.FOREGROUND_SERVICE",       "ohos.permission.KEEP_BACKGROUND_RUNNING"},

        // Notifications
        {"android.permission.POST_NOTIFICATIONS",       "ohos.permission.NOTIFICATION_CONTROLLER"},

        // NFC
        {"android.permission.NFC",                      "ohos.permission.NFC_TAG"},
    };
    return sMap;
}

// OH -> Android permission mapping table (reverse)
const std::unordered_map<std::string, std::string>& GetOHToAndroidMap() {
    static const std::unordered_map<std::string, std::string> sMap = {
        {"ohos.permission.INTERNET",                "android.permission.INTERNET"},
        {"ohos.permission.GET_NETWORK_INFO",        "android.permission.ACCESS_NETWORK_STATE"},
        {"ohos.permission.GET_WIFI_INFO",           "android.permission.ACCESS_WIFI_STATE"},
        {"ohos.permission.SET_WIFI_INFO",           "android.permission.CHANGE_WIFI_STATE"},
        {"ohos.permission.SET_NETWORK_INFO",        "android.permission.CHANGE_NETWORK_STATE"},
        {"ohos.permission.CAMERA",                  "android.permission.CAMERA"},
        {"ohos.permission.MICROPHONE",              "android.permission.RECORD_AUDIO"},
        {"ohos.permission.READ_MEDIA",              "android.permission.READ_EXTERNAL_STORAGE"},
        {"ohos.permission.WRITE_MEDIA",             "android.permission.WRITE_EXTERNAL_STORAGE"},
        {"ohos.permission.FILE_ACCESS_MANAGER",     "android.permission.MANAGE_EXTERNAL_STORAGE"},
        {"ohos.permission.APPROXIMATELY_LOCATION",  "android.permission.ACCESS_FINE_LOCATION"},
        {"ohos.permission.LOCATION_IN_BACKGROUND",  "android.permission.ACCESS_BACKGROUND_LOCATION"},
        {"ohos.permission.READ_CONTACTS",           "android.permission.READ_CONTACTS"},
        {"ohos.permission.WRITE_CONTACTS",          "android.permission.WRITE_CONTACTS"},
        {"ohos.permission.GET_TELEPHONY_STATE",     "android.permission.READ_PHONE_STATE"},
        {"ohos.permission.PLACE_CALL",              "android.permission.CALL_PHONE"},
        {"ohos.permission.READ_CALL_LOG",           "android.permission.READ_CALL_LOG"},
        {"ohos.permission.WRITE_CALL_LOG",          "android.permission.WRITE_CALL_LOG"},
        {"ohos.permission.SEND_MESSAGES",           "android.permission.SEND_SMS"},
        {"ohos.permission.READ_MESSAGES",           "android.permission.READ_SMS"},
        {"ohos.permission.RECEIVE_SMS",             "android.permission.RECEIVE_SMS"},
        {"ohos.permission.READ_CALENDAR",           "android.permission.READ_CALENDAR"},
        {"ohos.permission.WRITE_CALENDAR",          "android.permission.WRITE_CALENDAR"},
        {"ohos.permission.READ_HEALTH_DATA",        "android.permission.BODY_SENSORS"},
        {"ohos.permission.USE_BLUETOOTH",           "android.permission.BLUETOOTH"},
        {"ohos.permission.MANAGE_BLUETOOTH",        "android.permission.BLUETOOTH_ADMIN"},
        {"ohos.permission.VIBRATE",                 "android.permission.VIBRATE"},
        {"ohos.permission.RUNNING_LOCK",            "android.permission.WAKE_LOCK"},
        {"ohos.permission.PUBLISH_AGENT_REMINDER",  "android.permission.SET_ALARM"},
        {"ohos.permission.INSTALL_BUNDLE",          "android.permission.REQUEST_INSTALL_PACKAGES"},
        {"ohos.permission.KEEP_BACKGROUND_RUNNING", "android.permission.FOREGROUND_SERVICE"},
        {"ohos.permission.NOTIFICATION_CONTROLLER", "android.permission.POST_NOTIFICATIONS"},
        {"ohos.permission.NFC_TAG",                 "android.permission.NFC"},
    };
    return sMap;
}

const std::string kAndroidPrefix = "android.permission.";
const std::string kOHPrefix = "ohos.permission.";
const std::string kOHAndroidPrefix = "ohos.permission.android.";

}  // namespace

std::string PermissionMapper::MapToOH(const std::string& androidPermission) {
    const auto& map = GetAndroidToOHMap();
    auto it = map.find(androidPermission);
    if (it != map.end()) {
        return it->second;
    }
    // Unknown: strip "android.permission." and add "ohos.permission.android." prefix
    if (androidPermission.compare(0, kAndroidPrefix.size(), kAndroidPrefix) == 0) {
        return kOHAndroidPrefix + androidPermission.substr(kAndroidPrefix.size());
    }
    return kOHAndroidPrefix + androidPermission;
}

std::string PermissionMapper::MapToAndroid(const std::string& ohPermission) {
    const auto& map = GetOHToAndroidMap();
    auto it = map.find(ohPermission);
    if (it != map.end()) {
        return it->second;
    }
    // Reverse passthrough: strip "ohos.permission.android." prefix
    if (ohPermission.compare(0, kOHAndroidPrefix.size(), kOHAndroidPrefix) == 0) {
        return kAndroidPrefix + ohPermission.substr(kOHAndroidPrefix.size());
    }
    return ohPermission;
}

}  // namespace oh_adapter
