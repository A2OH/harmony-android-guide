// libbinder-port: minimal stub for android-base/properties.h required symbols.
//
// servicemanager calls SetProperty("servicemanager.ready", "true") and
// ServiceManager::tryStartService() calls SetProperty("ctl.interface_start",
// "aidl/<name>").  In our sandbox we don't have init listening for property
// changes, so SetProperty just logs and returns true (callers don't care).
//
// GetProperty/GetBoolProperty return defaults — we never set anything.
// WaitForProperty / WaitForPropertyCreation return true immediately so the
// BackendUnifiedServiceManager's "wait for servicemanager.ready=true" loop
// unblocks (our SM is already up by the time the client calls in).

#include <stdio.h>
#include <chrono>
#include <string>

#include <android-base/properties.h>

namespace android {
namespace base {

std::string GetProperty(const std::string& /*key*/,
                        const std::string& default_value) {
    return default_value;
}

bool GetBoolProperty(const std::string& /*key*/, bool default_value) {
    return default_value;
}

template <typename T>
T GetIntProperty(const std::string& /*key*/, T default_value, T /*min*/, T /*max*/) {
    return default_value;
}

template <typename T>
T GetUintProperty(const std::string& /*key*/, T default_value, T /*max*/) {
    return default_value;
}

// Explicit instantiations for the integer types libbase's clients use.
template int8_t   GetIntProperty<int8_t>(const std::string&, int8_t, int8_t, int8_t);
template int16_t  GetIntProperty<int16_t>(const std::string&, int16_t, int16_t, int16_t);
template int32_t  GetIntProperty<int32_t>(const std::string&, int32_t, int32_t, int32_t);
template int64_t  GetIntProperty<int64_t>(const std::string&, int64_t, int64_t, int64_t);
template uint8_t  GetUintProperty<uint8_t>(const std::string&, uint8_t, uint8_t);
template uint16_t GetUintProperty<uint16_t>(const std::string&, uint16_t, uint16_t);
template uint32_t GetUintProperty<uint32_t>(const std::string&, uint32_t, uint32_t);
template uint64_t GetUintProperty<uint64_t>(const std::string&, uint64_t, uint64_t);

bool SetProperty(const std::string& key, const std::string& value) {
    // No-op in sandbox; callers (servicemanager) ignore the return value or
    // log on false.  Print so the test transcript shows what would happen.
    fprintf(stderr, "[sm-stub] SetProperty(%s=%s) -> ignored\n", key.c_str(),
            value.c_str());
    return true;
}

bool WaitForProperty(const std::string& key, const std::string& expected_value,
                     std::chrono::milliseconds /*relative_timeout*/) {
    // BackendUnifiedServiceManager spins on WaitForProperty("servicemanager.ready",
    // "true", 1s) before delegating service lookups.  Our sandbox starts
    // servicemanager FIRST (sandbox-boot.sh / m3-dalvikvm-boot.sh) so by the
    // time the client process hits this wait the SM is definitely up.
    // Return true immediately so we don't block.
    fprintf(stderr, "[sm-stub] WaitForProperty(%s=%s) -> immediate true\n",
            key.c_str(), expected_value.c_str());
    return true;
}

bool WaitForPropertyCreation(const std::string& key,
                             std::chrono::milliseconds /*relative_timeout*/) {
    fprintf(stderr, "[sm-stub] WaitForPropertyCreation(%s) -> immediate true\n",
            key.c_str());
    return true;
}

}  // namespace base
}  // namespace android
