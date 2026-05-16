/**
 * appspawn-x spawn message definitions.
 *
 * Defines the message structures used for communication between
 * foundation's AppSpawnClient and the appspawn-x daemon over Unix socket.
 * Message codes and StartFlags must match OH appspawn protocol definitions.
 */

#pragma once

#include <cstdint>
#include <cstdio>
#include <string>
#include <vector>

// ---------- Logging macros (portable, switch to hilog for OH build) ----------
// 2026-04-17: route INFO/DEBUG through stderr (not stdout) with fflush, because
// when the process redirects stdout to a file (e.g. our test harness), stdout
// becomes fully buffered — lines stay in buffer until exit, and if the process
// hangs or is killed (SIGTERM from timeout) the recent INFO logs never reach
// the file, making diagnosis impossible. stderr is line-buffered by default
// and every message ends with fflush to be sure.
// 2026-04-29 B.30: route LOG macros to OH hilog so child process logs are
// visible after init service launch (stderr is /dev/null under init service).
// Stderr fallback retained for shell-launched paths where hilog may not be
// initialized.  HiLogPrint signature:
//   int HiLogPrint(LogType type, LogLevel level, unsigned int domain,
//                  const char* tag, const char* fmt, ...);
#include <hilog/log.h>
#define APPSPAWNX_LOG_TAG "AppSpawnX"
#define APPSPAWNX_LOG_DOMAIN 0xD000F00
#define LOGI(fmt, ...) do { \
    HiLogPrint(LOG_CORE, LOG_INFO, APPSPAWNX_LOG_DOMAIN, APPSPAWNX_LOG_TAG, fmt, ##__VA_ARGS__); \
    fprintf(stderr, "[%s] " fmt "\n", APPSPAWNX_LOG_TAG, ##__VA_ARGS__); fflush(stderr); \
} while(0)
#define LOGD(fmt, ...) do { \
    HiLogPrint(LOG_CORE, LOG_DEBUG, APPSPAWNX_LOG_DOMAIN, APPSPAWNX_LOG_TAG, fmt, ##__VA_ARGS__); \
    fprintf(stderr, "[%s][D] " fmt "\n", APPSPAWNX_LOG_TAG, ##__VA_ARGS__); fflush(stderr); \
} while(0)
#define LOGE(fmt, ...) do { \
    HiLogPrint(LOG_CORE, LOG_ERROR, APPSPAWNX_LOG_DOMAIN, APPSPAWNX_LOG_TAG, fmt, ##__VA_ARGS__); \
    fprintf(stderr, "[%s][E] " fmt "\n", APPSPAWNX_LOG_TAG, ##__VA_ARGS__); fflush(stderr); \
} while(0)
#define LOGW(fmt, ...) do { \
    HiLogPrint(LOG_CORE, LOG_WARN, APPSPAWNX_LOG_DOMAIN, APPSPAWNX_LOG_TAG, fmt, ##__VA_ARGS__); \
    fprintf(stderr, "[%s][W] " fmt "\n", APPSPAWNX_LOG_TAG, ##__VA_ARGS__); fflush(stderr); \
} while(0)

namespace appspawnx {

// AppSpawn message codes — aligned with OH AppSpawnMsgType
// (base/startup/appspawn/interfaces/innerkits/include/appspawn.h)
constexpr int MSG_APP_SPAWN = 0;
constexpr int MSG_GET_RENDER_TERMINATION_STATUS = 1;
constexpr int MSG_SPAWN_NATIVE_PROCESS = 2;
constexpr int MSG_DUMP = 3;

// OH binary TLV spawn protocol constants
// Source: base/startup/appspawn/modules/module_engine/include/appspawn_msg.h
constexpr uint32_t OH_SPAWN_MSG_MAGIC = 0xEF201234u;
constexpr size_t OH_APP_LEN_PROC_NAME = 256;
constexpr size_t OH_APP_MAX_GIDS = 64;
constexpr size_t OH_APP_USER_NAME = 64;
constexpr size_t OH_APPSPAWN_TLV_NAME_LEN = 32;

// AppSpawnTlvType enum
constexpr uint16_t OH_TLV_BUNDLE_INFO = 0;
constexpr uint16_t OH_TLV_MSG_FLAGS = 1;
constexpr uint16_t OH_TLV_DAC_INFO = 2;
constexpr uint16_t OH_TLV_DOMAIN_INFO = 3;
constexpr uint16_t OH_TLV_OWNER_INFO = 4;
constexpr uint16_t OH_TLV_ACCESS_TOKEN_INFO = 5;
constexpr uint16_t OH_TLV_PERMISSION = 6;
constexpr uint16_t OH_TLV_INTERNET_INFO = 7;
constexpr uint16_t OH_TLV_RENDER_TERMINATION_INFO = 8;
constexpr uint16_t OH_TLV_CHECK_POINT_INFO = 9;
constexpr uint16_t OH_TLV_MAX = 10;

// 4-byte TLV alignment
constexpr size_t OH_TLV_ALIGN(size_t n) { return (n + 3u) & ~3u; }

#pragma pack(push, 4)

// AppSpawnMsg header (280 bytes, 4-byte aligned)
struct OhAppSpawnMsgHeader {
    uint32_t magic;
    uint32_t msgType;
    uint32_t msgLen;
    uint32_t msgId;
    uint32_t tlvCount;
    char processName[OH_APP_LEN_PROC_NAME];
};
static_assert(sizeof(OhAppSpawnMsgHeader) == 4u * 5u + OH_APP_LEN_PROC_NAME,
              "OhAppSpawnMsgHeader size mismatch");

// Basic TLV header (4 bytes)
struct OhAppSpawnTlv {
    uint16_t tlvLen;
    uint16_t tlvType;
};

// Extended TLV header (40 bytes)
struct OhAppSpawnTlvExt {
    uint16_t tlvLen;
    uint16_t tlvType;
    uint16_t dataLen;
    uint16_t dataType;
    char tlvName[OH_APPSPAWN_TLV_NAME_LEN];
};

// TLV_BUNDLE_INFO payload
struct OhAppSpawnMsgBundleInfo {
    uint32_t bundleIndex;
    char bundleName[1];  // flexible trailing string (C89-compatible)
};

// TLV_DAC_INFO payload (reuses AppDacInfo)
struct OhAppDacInfo {
    uint32_t uid;
    uint32_t gid;
    uint32_t gidCount;
    uint32_t gidTable[OH_APP_MAX_GIDS];
    char userName[OH_APP_USER_NAME];
};

// TLV_MSG_FLAGS payload (variable-length flags[])
struct OhAppSpawnMsgFlags {
    uint32_t count;
    uint32_t flags[1];  // flexible trailing uint32[]
};

// TLV_DOMAIN_INFO payload
struct OhAppSpawnMsgDomainInfo {
    uint32_t hapFlags;
    char apl[1];  // flexible trailing C-string
};

// TLV_ACCESS_TOKEN_INFO payload
struct OhAppSpawnMsgAccessToken {
    uint64_t accessTokenIdEx;
};

// TLV_INTERNET_INFO payload
struct OhAppSpawnMsgInternetInfo {
    uint8_t setAllowInternet;
    uint8_t allowInternet;
    uint8_t res[2];
};

// AppSpawnResult (16 bytes)
struct OhAppSpawnResult {
    int32_t result;
    int32_t pid;
    uint64_t checkPointId;
};

// AppSpawnResponseMsg (304 bytes: 280 + 16 + 8)
struct OhAppSpawnResponseMsg {
    OhAppSpawnMsgHeader msgHdr;
    OhAppSpawnResult result;
    uint64_t checkPointId;
};

#pragma pack(pop)

// StartFlags bit positions (must match OH AppSpawnStartMsg::StartFlags)
struct StartFlags {
    static constexpr int COLD_START = 0;
    static constexpr int DEBUGGABLE = 3;
    static constexpr int ASANENABLED = 4;
    static constexpr int NATIVEDEBUG = 6;
    static constexpr int NO_SANDBOX = 7;
};

// Parsed spawn request message
struct SpawnMsg {
    int32_t code;
    std::string procName;
    std::string bundleName;
    int32_t uid;
    int32_t gid;
    std::vector<int32_t> gids;
    uint64_t accessTokenIdEx;
    uint32_t hapFlags;
    std::string apl;              // "system_core", "system_basic", "normal"
    uint64_t flags;               // StartFlags bitmask

    // Android-specific extensions
    std::string apkPath;          // APK file path
    std::string dexPaths;         // DEX search paths
    std::string nativeLibPaths;   // Native library paths
    std::string targetClass;      // Entry class (default: "android.app.ActivityThread")
    int32_t targetSdkVersion;

    // SELinux
    std::string selinuxContext;   // e.g. "u:r:android_app:s0"

    // OH binary-protocol trace fields (filled when parsed from OH TLV format;
    // used by handleConnection to generate the correct 304-byte response).
    bool isOhBinary;
    uint32_t ohMsgId;

    SpawnMsg() : code(0), uid(-1), gid(-1), accessTokenIdEx(0),
                 hapFlags(0), flags(0), targetSdkVersion(0),
                 isOhBinary(false), ohMsgId(0) {}

    bool hasFlag(int bit) const { return (flags >> bit) & 1; }
};

// Spawn result sent back to foundation
struct SpawnResult {
    int32_t pid;
    int32_t result;  // 0 = success
};

} // namespace appspawnx
