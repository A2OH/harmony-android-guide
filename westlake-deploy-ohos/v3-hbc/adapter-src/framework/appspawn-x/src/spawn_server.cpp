/**
 * appspawn-x spawn server implementation.
 *
 * Manages the Unix domain socket lifecycle, accepts connections from
 * foundation's AppSpawnClient, parses spawn request messages, and
 * dispatches them to the registered SpawnHandler. Also handles SIGCHLD
 * to reap forked child processes.
 */

#include "spawn_server.h"

#include <cerrno>
#include <cstdlib>
#include <cstring>
#include <poll.h>
#include <signal.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/un.h>
#include <sys/wait.h>
#include <unistd.h>

namespace appspawnx {

// ---------------------------------------------------------------------------
// SIGCHLD handler – reap zombie child processes
// ---------------------------------------------------------------------------
static void sigchldHandler(int /*sig*/) {
    // Reap all terminated children without blocking
    int savedErrno = errno;
    while (waitpid(-1, nullptr, WNOHANG) > 0) {
        // Continue reaping
    }
    errno = savedErrno;
}

// ---------------------------------------------------------------------------
// Install SIGCHLD handler with SA_RESTART so accept()/poll() auto-restart
// ---------------------------------------------------------------------------
static int installSigchldHandler() {
    struct sigaction sa;
    memset(&sa, 0, sizeof(sa));
    sa.sa_handler = sigchldHandler;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = SA_RESTART | SA_NOCLDSTOP;

    if (sigaction(SIGCHLD, &sa, nullptr) < 0) {
        LOGE("sigaction(SIGCHLD) failed: %s", strerror(errno));
        return -1;
    }
    return 0;
}

// ---------------------------------------------------------------------------
// Maximum message size we accept from a client (256 KB should be plenty)
// ---------------------------------------------------------------------------
static constexpr size_t kMaxMessageSize = 256 * 1024;

// Maximum number of pending connections on the listen socket
static constexpr int kListenBacklog = 8;

// ---------------------------------------------------------------------------
// SpawnServer
// ---------------------------------------------------------------------------

SpawnServer::SpawnServer(const std::string& socketName)
    : socketName_(socketName),
      listenFd_(-1) {
    // Build the socket filesystem path.
    // On OH, appspawn sockets live under /dev/unix/socket/
    socketPath_ = std::string("/dev/unix/socket/") + socketName_;
}

SpawnServer::~SpawnServer() {
    closeSocket();
}

// ---------------------------------------------------------------------------
// initSecurity  –  initialize OH sandbox, SELinux, AccessToken modules
// ---------------------------------------------------------------------------
int SpawnServer::initSecurity(const std::string& sandboxConfigPath) {
    LOGI("Initializing security modules");

    // Step 1: Load sandbox configuration
    // In production, this reads the JSON sandbox profile that defines
    // mount namespaces, bind mounts, and filesystem restrictions per APL.
    LOGI("Loading sandbox config from: %s", sandboxConfigPath.c_str());
    // TODO: Parse sandboxConfigPath JSON using OH sandbox APIs
    //   - LoadAppSandboxConfig(sandboxConfigPath)
    //   - Validate required mount points exist
    if (access(sandboxConfigPath.c_str(), R_OK) != 0) {
        LOGW("Sandbox config not readable: %s (errno=%d: %s)",
             sandboxConfigPath.c_str(), errno, strerror(errno));
        LOGW("Continuing without sandbox config – "
             "apps will run without filesystem isolation");
    } else {
        LOGI("Sandbox config file found");
    }

    // Step 2: Initialize SELinux labeling
    // Load file_contexts and seapp_contexts for Android app domain mapping
    LOGI("Initializing SELinux labeling");
    // TODO: Link against libselinux and call:
    //   - selinux_android_setcontext()
    //   - selabel_open() for file labeling
    // For now, log the intent
    LOGD("SELinux labeling: stub initialized");

    // Step 3: Initialize AccessToken management
    // OH AccessToken provides fine-grained permission control.
    // We need to set the correct token for each spawned app process.
    LOGI("Initializing AccessToken management");
    // TODO: Link against libaccesstoken_sdk and call:
    //   - AccessTokenKit::Init()
    LOGD("AccessToken management: stub initialized");

    // Step 4: Install SIGCHLD handler for child reaping
    if (installSigchldHandler() != 0) {
        LOGE("Failed to install SIGCHLD handler");
        return -1;
    }
    LOGI("SIGCHLD handler installed");

    LOGI("Security modules initialized successfully");
    return 0;
}

// ---------------------------------------------------------------------------
// createListenSocket  –  acquire listen fd
//
// Preferred path (when running as init service):
//   init cfg 里声明的 "socket" 会被 init 在 fork 前 bind+listen+labeled，
//   fd 号通过 env `OHOS_SOCKET_<socketName>` 传给子进程。直接取用即可。
//   这也是 OH 原生 appspawn 的做法，必须这样才能在 Enforcing 下跑通
//   （appspawn 域的 policy 不允许 unlink dev_unix_file sock_file;
//    而且 factory policy.31 没有 "AppSpawnX" 的 name-based type_transition,
//    自己 bind 出来的 socket label 是 dev_unix_file 而非 appspawn_socket,
//    会触发 avc denied）。
//
// Fallback path (standalone / unit test):
//   env 没设 → 走原本的 socket+unlink+bind+listen 流程（Permissive 下可用）。
// ---------------------------------------------------------------------------
int SpawnServer::createListenSocket() {
    std::string envKey = "OHOS_SOCKET_" + socketName_;
    const char* fdStr = getenv(envKey.c_str());
    if (fdStr != nullptr && *fdStr != '\0') {
        int fd = atoi(fdStr);
        if (fd >= 0) {
            listenFd_ = fd;
            LOGI("Using init-provided socket: env=%s fd=%d path=%s",
                 envKey.c_str(), fd, socketPath_.c_str());
            // init 已 bind+listen+label; 不要 unlink/bind/listen/chmod,
            // 否则 Enforcing 下会被 policy 拒 (avc denied unlink dev_unix_file)
            return 0;
        }
        LOGW("Env %s=%s invalid, falling back to manual bind", envKey.c_str(), fdStr);
    } else {
        LOGI("Env %s not set, falling back to manual bind (standalone mode)",
             envKey.c_str());
    }

    // Fallback: manual create + bind + listen (standalone, Permissive only)
    LOGI("Creating listen socket manually: %s", socketPath_.c_str());

    listenFd_ = socket(AF_UNIX, SOCK_STREAM, 0);
    if (listenFd_ < 0) {
        LOGE("socket(AF_UNIX) failed: %s", strerror(errno));
        return -1;
    }

    unlink(socketPath_.c_str());

    struct sockaddr_un addr;
    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;

    if (socketPath_.size() >= sizeof(addr.sun_path)) {
        LOGE("Socket path too long (%zu >= %zu): %s",
             socketPath_.size(), sizeof(addr.sun_path), socketPath_.c_str());
        close(listenFd_);
        listenFd_ = -1;
        return -1;
    }
    strncpy(addr.sun_path, socketPath_.c_str(), sizeof(addr.sun_path) - 1);

    socklen_t addrLen = offsetof(struct sockaddr_un, sun_path) +
                        strlen(addr.sun_path) + 1;

    if (bind(listenFd_, reinterpret_cast<struct sockaddr*>(&addr), addrLen) < 0) {
        LOGE("bind(%s) failed: %s", socketPath_.c_str(), strerror(errno));
        close(listenFd_);
        listenFd_ = -1;
        return -1;
    }

    if (chmod(socketPath_.c_str(), 0666) < 0) {
        LOGW("chmod(%s, 0666) failed: %s", socketPath_.c_str(), strerror(errno));
    }

    if (listen(listenFd_, kListenBacklog) < 0) {
        LOGE("listen() failed: %s", strerror(errno));
        close(listenFd_);
        listenFd_ = -1;
        return -1;
    }

    LOGI("Listen socket ready (manual): fd=%d path=%s", listenFd_, socketPath_.c_str());
    return 0;
}

// ---------------------------------------------------------------------------
// setSpawnHandler
// ---------------------------------------------------------------------------
void SpawnServer::setSpawnHandler(SpawnHandler handler) {
    spawnHandler_ = std::move(handler);
}

// ---------------------------------------------------------------------------
// run  –  main event loop
// ---------------------------------------------------------------------------
void SpawnServer::run() {
    LOGI("Entering event loop on %s", socketPath_.c_str());

    if (listenFd_ < 0) {
        LOGE("Cannot run – listen socket not created");
        return;
    }

    struct pollfd pfd;
    pfd.fd = listenFd_;
    pfd.events = POLLIN;

    while (true) {
        // Wait for incoming connections
        int nready = poll(&pfd, 1, -1 /* block indefinitely */);

        if (nready < 0) {
            if (errno == EINTR) {
                // Interrupted by signal (e.g. SIGCHLD) – restart poll
                continue;
            }
            LOGE("poll() failed: %s", strerror(errno));
            break;
        }

        if (nready == 0) {
            // Timeout (shouldn't happen with -1 timeout)
            continue;
        }

        if (pfd.revents & POLLIN) {
            // Accept the incoming connection
            struct sockaddr_un clientAddr;
            socklen_t clientLen = sizeof(clientAddr);
            int clientFd = accept(listenFd_,
                                  reinterpret_cast<struct sockaddr*>(&clientAddr),
                                  &clientLen);
            if (clientFd < 0) {
                if (errno == EINTR) {
                    continue;
                }
                LOGE("accept() failed: %s", strerror(errno));
                continue;
            }

            LOGD("Accepted connection, clientFd=%d", clientFd);

            // Handle the spawn request synchronously.
            // This is acceptable because fork() is fast and the parent
            // returns quickly after spawning the child.
            handleConnection(clientFd);

            close(clientFd);
        }

        if (pfd.revents & (POLLERR | POLLHUP | POLLNVAL)) {
            LOGE("Listen socket error (revents=0x%x)", pfd.revents);
            break;
        }
    }

    LOGE("Event loop terminated");
}

// ---------------------------------------------------------------------------
// closeSocket  –  close the listen fd (used in child after fork)
// ---------------------------------------------------------------------------
void SpawnServer::closeSocket() {
    if (listenFd_ >= 0) {
        close(listenFd_);
        listenFd_ = -1;
    }
}

// ---------------------------------------------------------------------------
// handleConnection  –  read request, dispatch to handler, send result
// ---------------------------------------------------------------------------
// Read exactly `want` bytes from fd; returns true on success, false on EOF/error.
static bool recvAll(int fd, uint8_t* buf, size_t want) {
    size_t total = 0;
    while (total < want) {
        ssize_t n = recv(fd, buf + total, want - total, 0);
        if (n <= 0) {
            if (n < 0 && errno == EINTR) continue;
            return false;
        }
        total += static_cast<size_t>(n);
    }
    return true;
}

// Send a binary AppSpawnResponseMsg (304 bytes) to the OH client.
// Echoes msgId from request so the client can correlate.
static void sendOhBinaryResponse(int clientFd, uint32_t reqMsgId,
                                 const char* procName,
                                 int pid, int result) {
    OhAppSpawnResponseMsg resp{};
    resp.msgHdr.magic = OH_SPAWN_MSG_MAGIC;
    resp.msgHdr.msgType = MSG_APP_SPAWN;  // response type mirrors request class
    resp.msgHdr.msgLen = static_cast<uint32_t>(sizeof(resp));
    resp.msgHdr.msgId = reqMsgId;
    resp.msgHdr.tlvCount = 0;
    if (procName) {
        strncpy(resp.msgHdr.processName, procName, OH_APP_LEN_PROC_NAME - 1);
    }
    resp.result.result = result;
    resp.result.pid = pid;
    resp.result.checkPointId = 0;
    resp.checkPointId = 0;

    ssize_t n = send(clientFd, &resp, sizeof(resp), MSG_NOSIGNAL);
    if (n != static_cast<ssize_t>(sizeof(resp))) {
        LOGE("sendOhBinaryResponse partial/failed: n=%zd errno=%s",
             n, strerror(errno));
    } else {
        LOGD("OH binary response sent: pid=%d result=%d msgId=%u",
             pid, result, reqMsgId);
    }
}

void SpawnServer::handleConnection(int clientFd) {
    // Peek first 4 bytes to detect protocol: OH binary starts with
    // magic 0xEF201234; adapter-legacy starts with a network-order length prefix.
    uint32_t probe = 0;
    ssize_t n = recv(clientFd, &probe, sizeof(probe), MSG_WAITALL);
    if (n != sizeof(probe)) {
        LOGE("Failed to read protocol probe: n=%zd errno=%s",
             n, strerror(errno));
        SpawnResult err = {-1, -1};
        sendResult(clientFd, err);
        return;
    }

    std::vector<uint8_t> buffer;
    bool isBinary = (probe == OH_SPAWN_MSG_MAGIC);

    if (isBinary) {
        // OH binary TLV: read rest of 280-byte header, then (msgLen - 280) TLV body.
        buffer.resize(sizeof(OhAppSpawnMsgHeader));
        memcpy(buffer.data(), &probe, sizeof(probe));
        if (!recvAll(clientFd, buffer.data() + sizeof(probe),
                     sizeof(OhAppSpawnMsgHeader) - sizeof(probe))) {
            LOGE("Failed to read OH header remainder: errno=%s", strerror(errno));
            sendOhBinaryResponse(clientFd, 0, "", -1, -1);
            return;
        }
        auto* hdr = reinterpret_cast<const OhAppSpawnMsgHeader*>(buffer.data());
        uint32_t msgLen = hdr->msgLen;
        uint32_t msgId = hdr->msgId;
        if (msgLen < sizeof(OhAppSpawnMsgHeader) || msgLen > kMaxMessageSize) {
            LOGE("Invalid OH msgLen: %u", msgLen);
            sendOhBinaryResponse(clientFd, msgId, "", -1, -1);
            return;
        }
        size_t tlvBytes = msgLen - sizeof(OhAppSpawnMsgHeader);
        if (tlvBytes > 0) {
            buffer.resize(msgLen);
            if (!recvAll(clientFd, buffer.data() + sizeof(OhAppSpawnMsgHeader),
                         tlvBytes)) {
                LOGE("Failed to read OH TLV body: errno=%s", strerror(errno));
                sendOhBinaryResponse(clientFd, msgId, "", -1, -1);
                return;
            }
        }
        LOGI("OH binary msg received: type=%u len=%u id=%u tlvCount=%u proc=%.32s",
             hdr->msgType, hdr->msgLen, hdr->msgId, hdr->tlvCount, hdr->processName);
    } else {
        // Legacy adapter protocol: probe was the 4-byte payload length.
        uint32_t payloadLen = probe;
        if (payloadLen == 0 || payloadLen > kMaxMessageSize) {
            LOGE("Invalid legacy message length: %u (0x%08x)", payloadLen, payloadLen);
            SpawnResult err = {-1, -1};
            sendResult(clientFd, err);
            return;
        }
        buffer.resize(payloadLen);
        if (!recvAll(clientFd, buffer.data(), payloadLen)) {
            LOGE("Failed to read legacy payload: errno=%s", strerror(errno));
            SpawnResult err = {-1, -1};
            sendResult(clientFd, err);
            return;
        }
    }

    // Parse the message (binary or text — parseMessage dispatches on magic).
    SpawnMsg msg;
    if (!parseMessage(buffer.data(), buffer.size(), msg)) {
        LOGE("Failed to parse spawn message (isBinary=%d)", isBinary);
        if (isBinary) {
            uint32_t msgId = reinterpret_cast<const OhAppSpawnMsgHeader*>(
                buffer.data())->msgId;
            sendOhBinaryResponse(clientFd, msgId, "", -1, -1);
        } else {
            SpawnResult err = {-1, -1};
            sendResult(clientFd, err);
        }
        return;
    }

    LOGI("Parsed spawn request: code=%d proc=%s bundle=%s uid=%d isOhBinary=%d",
         msg.code, msg.procName.c_str(), msg.bundleName.c_str(), msg.uid,
         msg.isOhBinary);

    // Dispatch to the spawn handler
    int pid = -1;
    int rc = -1;
    if (spawnHandler_) {
        pid = spawnHandler_(msg);
        rc = (pid > 0) ? 0 : -1;
    } else {
        LOGE("No spawn handler registered");
    }

    if (msg.isOhBinary) {
        sendOhBinaryResponse(clientFd, msg.ohMsgId, msg.procName.c_str(), pid, rc);
    } else {
        SpawnResult result{pid, rc};
        sendResult(clientFd, result);
    }
}

// ---------------------------------------------------------------------------
// parseMessage  –  simplified JSON-based protocol for development
// ---------------------------------------------------------------------------
//
// In production, this would use the OH appspawn binary protocol by linking
// against appspawn libraries (appspawn_msg_parse). For development and
// testing, we use a simple text-based key=value format, one field per line:
//
//   code=0
//   procName=com.example.app
//   bundleName=com.example.app
//   uid=10042
//   gid=10042
//   gids=1003,1004,3003
//   accessTokenIdEx=123456
//   hapFlags=0
//   apl=normal
//   flags=0
//   apkPath=/data/app/com.example.app/base.apk
//   dexPaths=/data/app/com.example.app/base.apk
//   nativeLibPaths=/data/app/com.example.app/lib/arm64
//   targetClass=android.app.ActivityThread
//   targetSdkVersion=34
//   selinuxContext=u:r:android_app:s0
//
// Parse OH binary TLV spawn message.
// Layout: OhAppSpawnMsgHeader (280) + tlvCount TLVs (4-byte aligned).
static bool parseOhBinaryMessage(const uint8_t* data, size_t len, SpawnMsg& msg) {
    if (len < sizeof(OhAppSpawnMsgHeader)) {
        LOGE("parseOhBinary: buffer %zu < header %zu", len, sizeof(OhAppSpawnMsgHeader));
        return false;
    }
    auto* hdr = reinterpret_cast<const OhAppSpawnMsgHeader*>(data);
    if (hdr->magic != OH_SPAWN_MSG_MAGIC) {
        LOGE("parseOhBinary: bad magic 0x%08x", hdr->magic);
        return false;
    }
    if (hdr->msgLen != len) {
        LOGW("parseOhBinary: header msgLen=%u != buffer len=%zu (trusting buffer)",
             hdr->msgLen, len);
    }
    msg.isOhBinary = true;
    msg.ohMsgId = hdr->msgId;
    msg.code = static_cast<int32_t>(hdr->msgType);
    // processName is null-terminated inside the 256-byte slot.
    msg.procName = std::string(hdr->processName,
        strnlen(hdr->processName, OH_APP_LEN_PROC_NAME));

    // Iterate TLVs.  Per OH convention (verified 2026-04-27 against
    // base/startup/appspawn/interfaces/innerkits/client/appspawn_msg.c
    // AddAppData: realLen = sizeof(AppSpawnTlv) + APPSPAWN_ALIGN(payload), and
    // standard/appspawn_msgmgr.c TraverseTlv: currLen += tlv->tlvLen):
    //   tlv->tlvLen = sizeof(AppSpawnTlv) + aligned_payload_len  (INCLUDES 4B
    //                                                              header)
    // So advance via `off += tLen` and payload size is `tLen - 4`.  An earlier
    // version of this parser treated tLen as payload-only and over-advanced
    // by 4 bytes per TLV; on TLV #2 it landed inside OH's alignment padding
    // (which is zeros) and reported "malformed TLV at off=302 type=0 len=0".
    // B.31 probe: log header tlvCount + msg len with %{public} so they're visible.
    HiLogPrint(LOG_CORE, LOG_INFO, APPSPAWNX_LOG_DOMAIN, APPSPAWNX_LOG_TAG,
        "[B31-TLV] parseOhBinary entry: msgLen=%{public}zu tlvCount=%{public}u msgType=%{public}u msgId=%{public}u",
        len, hdr->tlvCount, hdr->msgType, hdr->msgId);
    size_t off = sizeof(OhAppSpawnMsgHeader);
    uint32_t seen = 0;
    while (off + sizeof(OhAppSpawnTlv) <= len && seen < hdr->tlvCount) {
        auto* tlv = reinterpret_cast<const OhAppSpawnTlv*>(data + off);
        uint16_t tType = tlv->tlvType;
        uint16_t tLen = tlv->tlvLen;
        HiLogPrint(LOG_CORE, LOG_INFO, APPSPAWNX_LOG_DOMAIN, APPSPAWNX_LOG_TAG,
            "[B31-TLV] iter %{public}u/%{public}u at off=%{public}zu type=%{public}u len=%{public}u",
            seen, hdr->tlvCount, off, tType, tLen);
        if (tLen < sizeof(OhAppSpawnTlv) || off + tLen > len) {
            LOGE("parseOhBinary: malformed TLV at off=%zu type=%u len=%u (must be >=%zu and fit in msgLen=%zu)",
                 off, tType, tLen, sizeof(OhAppSpawnTlv), len);
            return false;
        }
        const uint8_t* payload = data + off + sizeof(OhAppSpawnTlv);
        size_t payloadLen = tLen - sizeof(OhAppSpawnTlv);

        switch (tType) {
            case OH_TLV_BUNDLE_INFO: {
                if (payloadLen >= sizeof(uint32_t) + 1) {
                    auto* bi = reinterpret_cast<const OhAppSpawnMsgBundleInfo*>(payload);
                    msg.bundleName = std::string(bi->bundleName,
                        strnlen(bi->bundleName, payloadLen - sizeof(uint32_t)));
                }
                break;
            }
            case OH_TLV_MSG_FLAGS: {
                if (payloadLen >= sizeof(uint32_t)) {
                    auto* mf = reinterpret_cast<const OhAppSpawnMsgFlags*>(payload);
                    // OH flags[] is an array of uint32 bitmask; we pack the first
                    // one (if any) into msg.flags. bit 0 = COLD_START per OH.
                    if (mf->count > 0 &&
                        payloadLen >= sizeof(uint32_t) + mf->count * sizeof(uint32_t)) {
                        msg.flags = mf->flags[0];
                    }
                }
                break;
            }
            case OH_TLV_DAC_INFO: {
                if (payloadLen >= sizeof(OhAppDacInfo)) {
                    auto* dac = reinterpret_cast<const OhAppDacInfo*>(payload);
                    msg.uid = static_cast<int32_t>(dac->uid);
                    msg.gid = static_cast<int32_t>(dac->gid);
                    uint32_t n = dac->gidCount;
                    if (n > OH_APP_MAX_GIDS) n = OH_APP_MAX_GIDS;
                    msg.gids.reserve(n);
                    for (uint32_t i = 0; i < n; ++i) {
                        msg.gids.push_back(static_cast<int32_t>(dac->gidTable[i]));
                    }
                }
                break;
            }
            case OH_TLV_DOMAIN_INFO: {
                if (payloadLen >= sizeof(uint32_t) + 1) {
                    auto* di = reinterpret_cast<const OhAppSpawnMsgDomainInfo*>(payload);
                    msg.hapFlags = di->hapFlags;
                    msg.apl = std::string(di->apl,
                        strnlen(di->apl, payloadLen - sizeof(uint32_t)));
                }
                break;
            }
            case OH_TLV_ACCESS_TOKEN_INFO: {
                // B.31 probe: confirm TLV is received and log raw value with
                // %{public} so hilog privacy doesn't mask the actual token id.
                HiLogPrint(LOG_CORE, LOG_INFO, APPSPAWNX_LOG_DOMAIN, APPSPAWNX_LOG_TAG,
                    "[B31-TLV] OH_TLV_ACCESS_TOKEN_INFO seen at off=%{public}zu payloadLen=%{public}zu (struct size=%{public}zu)",
                    off, payloadLen, sizeof(OhAppSpawnMsgAccessToken));
                if (payloadLen >= sizeof(OhAppSpawnMsgAccessToken)) {
                    auto* at = reinterpret_cast<const OhAppSpawnMsgAccessToken*>(payload);
                    msg.accessTokenIdEx = at->accessTokenIdEx;
                    HiLogPrint(LOG_CORE, LOG_INFO, APPSPAWNX_LOG_DOMAIN, APPSPAWNX_LOG_TAG,
                        "[B31-TLV] accessTokenIdEx=0x%{public}llx (decimal %{public}llu)",
                        static_cast<unsigned long long>(at->accessTokenIdEx),
                        static_cast<unsigned long long>(at->accessTokenIdEx));
                } else {
                    HiLogPrint(LOG_CORE, LOG_ERROR, APPSPAWNX_LOG_DOMAIN, APPSPAWNX_LOG_TAG,
                        "[B31-TLV] OH_TLV_ACCESS_TOKEN_INFO payload too short (%{public}zu < %{public}zu) — parsing skipped",
                        payloadLen, sizeof(OhAppSpawnMsgAccessToken));
                }
                break;
            }
            case OH_TLV_OWNER_INFO:
            case OH_TLV_PERMISSION:
            case OH_TLV_INTERNET_INFO:
            case OH_TLV_RENDER_TERMINATION_INFO:
            case OH_TLV_CHECK_POINT_INFO:
                LOGD("parseOhBinary: TLV type=%u len=%u skipped (not on hot path)",
                     tType, tLen);
                break;
            default:
                LOGD("parseOhBinary: unknown TLV type=%u len=%u skipped",
                     tType, tLen);
                break;
        }

        off += tLen;  // OH tlvLen already includes the 4B header
        ++seen;
    }
    if (seen != hdr->tlvCount) {
        LOGW("parseOhBinary: TLV count mismatch: parsed=%u declared=%u off=%zu len=%zu",
             seen, hdr->tlvCount, off, len);
    }

    // Validate minimum required fields for spawn.
    if (msg.procName.empty()) {
        LOGE("parseOhBinary: processName empty");
        return false;
    }
    if (msg.uid < 0) {
        // TLV_DAC_INFO might be absent for MSG_DUMP / cmd messages; treat as OK
        // for non-spawn messages and leave uid=-1 for the handler to decide.
        LOGW("parseOhBinary: DAC info missing (uid=%d) — may be non-spawn msg type=%d",
             msg.uid, msg.code);
    }
    return true;
}

bool SpawnServer::parseMessage(const uint8_t* data, size_t len, SpawnMsg& msg) {
    // Dispatch by magic: OH binary TLV vs legacy adapter text.
    if (len >= sizeof(uint32_t)) {
        uint32_t magic = 0;
        memcpy(&magic, data, sizeof(magic));
        if (magic == OH_SPAWN_MSG_MAGIC) {
            return parseOhBinaryMessage(data, len, msg);
        }
    }

    // Convert to string for line-by-line parsing
    std::string text(reinterpret_cast<const char*>(data), len);

    // Helper lambda: extract value for a given key from text
    auto getValue = [&text](const std::string& key) -> std::string {
        std::string prefix = key + "=";
        size_t pos = text.find(prefix);
        if (pos == std::string::npos) return "";

        size_t start = pos + prefix.size();
        size_t end = text.find('\n', start);
        if (end == std::string::npos) end = text.size();

        // Trim trailing \r if present
        if (end > start && text[end - 1] == '\r') end--;

        return text.substr(start, end - start);
    };

    // Helper lambda: parse integer with default value
    auto getInt = [&getValue](const std::string& key, int32_t def) -> int32_t {
        std::string val = getValue(key);
        if (val.empty()) return def;
        return static_cast<int32_t>(strtol(val.c_str(), nullptr, 10));
    };

    // Helper lambda: parse uint64 with default value
    auto getUint64 = [&getValue](const std::string& key, uint64_t def) -> uint64_t {
        std::string val = getValue(key);
        if (val.empty()) return def;
        return static_cast<uint64_t>(strtoull(val.c_str(), nullptr, 10));
    };

    // Parse all fields
    msg.code = getInt("code", MSG_APP_SPAWN);
    msg.procName = getValue("procName");
    msg.bundleName = getValue("bundleName");
    msg.uid = getInt("uid", -1);
    msg.gid = getInt("gid", -1);
    msg.accessTokenIdEx = getUint64("accessTokenIdEx", 0);
    msg.hapFlags = static_cast<uint32_t>(getInt("hapFlags", 0));
    msg.apl = getValue("apl");
    msg.flags = getUint64("flags", 0);
    msg.apkPath = getValue("apkPath");
    msg.dexPaths = getValue("dexPaths");
    msg.nativeLibPaths = getValue("nativeLibPaths");
    msg.targetClass = getValue("targetClass");
    msg.targetSdkVersion = getInt("targetSdkVersion", 0);
    msg.selinuxContext = getValue("selinuxContext");

    // Parse supplementary GIDs (comma-separated)
    std::string gidsStr = getValue("gids");
    if (!gidsStr.empty()) {
        size_t pos = 0;
        while (pos < gidsStr.size()) {
            size_t comma = gidsStr.find(',', pos);
            if (comma == std::string::npos) comma = gidsStr.size();
            std::string gidVal = gidsStr.substr(pos, comma - pos);
            if (!gidVal.empty()) {
                msg.gids.push_back(
                    static_cast<int32_t>(strtol(gidVal.c_str(), nullptr, 10)));
            }
            pos = comma + 1;
        }
    }

    // Validate required fields
    if (msg.procName.empty()) {
        LOGE("parseMessage: procName is empty");
        return false;
    }
    if (msg.uid < 0) {
        LOGE("parseMessage: uid is invalid (%d)", msg.uid);
        return false;
    }

    return true;
}

// ---------------------------------------------------------------------------
// sendResult  –  write the spawn result back to the client
// ---------------------------------------------------------------------------
void SpawnServer::sendResult(int clientFd, const SpawnResult& result) {
    ssize_t n = send(clientFd, &result, sizeof(result), MSG_NOSIGNAL);
    if (n != sizeof(result)) {
        LOGE("Failed to send spawn result: n=%zd errno=%s",
             n, strerror(errno));
    } else {
        LOGD("Sent spawn result: pid=%d result=%d", result.pid, result.result);
    }
}

} // namespace appspawnx
