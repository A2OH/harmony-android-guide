// Stub: odr_statslog/odr_statslog.h
// odrefresh metrics upload — no-op on OH
#pragma once
#include <string>
namespace odrefresh {
inline bool UploadStatsIfAvailable(std::string* /*err*/) { return true; }
}
