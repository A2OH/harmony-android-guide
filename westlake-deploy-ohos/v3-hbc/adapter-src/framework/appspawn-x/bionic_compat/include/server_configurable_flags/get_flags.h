// Stub: server_configurable_flags/get_flags.h
// Not available on OH - provide empty implementation
#pragma once
#include <string>
namespace server_configurable_flags {
inline std::string GetServerConfigurableFlag(
    const std::string&, const std::string&, const std::string& default_value) {
    return default_value;
}
}
