/*
 * oh_input_bridge.cpp
 *
 * Native input event bridge implementation.
 *
 * Writes Android-format InputMessage structs to the server side of
 * InputChannel socket pairs. The message format must match what
 * InputConsumer (in ViewRootImpl) expects to read.
 *
 * InputMessage format (simplified for single-pointer touch):
 *   - Header: type, seq
 *   - Body (motion): action, deviceId, source, displayId, pointerCount,
 *                     downTime, eventTime, pointerProperties, pointerCoords
 */
#include "oh_input_bridge.h"

#include <android/log.h>
#include <sys/socket.h>
#include <unistd.h>
#include <poll.h>
#include <cerrno>
#include <cstring>
#include <ctime>

#define LOG_TAG "OH_InputBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Android InputMessage constants (from InputTransport.h)
// These must match the AOSP InputMessage struct layout
namespace {

// InputMessage types
constexpr uint32_t INPUT_MSG_TYPE_MOTION = 2;

// MotionEvent source
constexpr int32_t AINPUT_SOURCE_TOUCHSCREEN = 0x00001002;

// MotionEvent actions
constexpr int32_t AMOTION_EVENT_ACTION_DOWN = 0;
constexpr int32_t AMOTION_EVENT_ACTION_UP = 1;
constexpr int32_t AMOTION_EVENT_ACTION_MOVE = 2;

// MotionEvent tool type
constexpr int32_t AMOTION_EVENT_TOOL_TYPE_FINGER = 1;

// Maximum number of pointer coordinates axes
constexpr size_t MAX_POINTER_COORDS_AXES = 30;

/*
 * Minimal InputMessage structure for single-pointer MotionEvent.
 *
 * This is a simplified version of the full InputMessage struct from
 * frameworks/native/libs/input/include/input/InputTransport.h.
 * It contains only the fields needed for basic touch event delivery.
 *
 * The full struct is complex (union of key/motion/focus/etc), but for
 * our purposes we only need the motion event variant.
 */
struct InputMessageHeader {
    uint32_t type;       // INPUT_MSG_TYPE_MOTION
    uint32_t seq;        // Sequence number
};

struct PointerProperties {
    int32_t id;          // Pointer ID (0 for single touch)
    int32_t toolType;    // AMOTION_EVENT_TOOL_TYPE_FINGER
};

struct PointerCoords {
    uint64_t bits;       // Bitmask of which axes have values
    // Axis values follow; for touch we use X(0) and Y(1)
    float values[MAX_POINTER_COORDS_AXES];
};

// Axis bit indices
constexpr uint64_t AXIS_X_BIT = (1ULL << 0);
constexpr uint64_t AXIS_Y_BIT = (1ULL << 1);
constexpr uint64_t AXIS_PRESSURE_BIT = (1ULL << 2);
constexpr uint64_t AXIS_SIZE_BIT = (1ULL << 3);

/*
 * Simplified MotionEvent body for InputMessage.
 * Laid out to match AOSP InputMessage::Body::Motion for single pointer.
 */
struct MotionEventBody {
    int32_t eventId;
    int32_t deviceId;
    int32_t source;
    int32_t displayId;
    uint8_t hmac[32];
    int32_t action;
    int32_t actionButton;
    int32_t flags;
    int32_t edgeFlags;
    int32_t metaState;
    int32_t buttonState;
    int32_t classification;

    // Transform (3x3 matrix in row-major, 9 floats for display transform)
    float dsdx;     // transform[0][0] = 1.0
    float dtdx;     // transform[0][1] = 0.0
    float tx;       // transform[0][2] = 0.0
    float dtdy;     // transform[1][0] = 0.0
    float dsdy;     // transform[1][1] = 1.0
    float ty;       // transform[1][2] = 0.0
    // 2D transform only needs 6 values

    float xPrecision;
    float yPrecision;
    float xCursorPosition;
    float yCursorPosition;

    // Raw transform
    float rawDsdx;
    float rawDtdx;
    float rawTx;
    float rawDtdy;
    float rawDsdy;
    float rawTy;

    int64_t downTime;
    int64_t eventTime;

    uint32_t pointerCount;

    // Inline pointer data for first pointer
    PointerProperties pointerProperties;
    PointerCoords pointerCoords;
};

}  // anonymous namespace

namespace oh_adapter {

OHInputBridge& OHInputBridge::getInstance() {
    static OHInputBridge instance;
    return instance;
}

void OHInputBridge::registerInputChannel(int32_t sessionId, int serverFd) {
    std::lock_guard<std::mutex> lock(mutex_);

    SessionInput& session = sessions_[sessionId];
    session.serverFd = serverFd;
    session.seq = 0;

    LOGI("Registered input channel: session=%d, fd=%d", sessionId, serverFd);
}

void OHInputBridge::unregisterInputChannel(int32_t sessionId) {
    std::lock_guard<std::mutex> lock(mutex_);

    auto it = sessions_.find(sessionId);
    if (it != sessions_.end()) {
        // Don't close serverFd here; Java InputChannel owns it
        LOGI("Unregistered input channel: session=%d", sessionId);
        sessions_.erase(it);
    }
}

void OHInputBridge::registerOHInputFd(int32_t sessionId, int ohInputFd) {
    std::lock_guard<std::mutex> lock(mutex_);

    auto it = sessions_.find(sessionId);
    if (it != sessions_.end()) {
        it->second.ohInputFd = ohInputFd;
        LOGI("Registered OH input fd: session=%d, ohFd=%d", sessionId, ohInputFd);
    }

    // Start monitoring thread if not already running
    if (!monitoring_.load()) {
        monitoring_ = true;
        monitorThread_ = std::thread(&OHInputBridge::monitorOHInputEvents, this);
        monitorThread_.detach();
    }
}

int32_t OHInputBridge::injectTouchEvent(int32_t sessionId, int32_t action,
                                          float x, float y,
                                          int64_t downTime, int64_t eventTime) {
    std::lock_guard<std::mutex> lock(mutex_);

    auto it = sessions_.find(sessionId);
    if (it == sessions_.end()) {
        LOGE("injectTouchEvent: session %d not found", sessionId);
        return -1;
    }

    SessionInput& session = it->second;
    if (session.serverFd < 0) {
        LOGE("injectTouchEvent: session %d has no server fd", sessionId);
        return -1;
    }

    session.seq++;
    int result = writeMotionEvent(session.serverFd, session.seq,
                                   action, x, y, downTime, eventTime);

    LOGD("injectTouchEvent: session=%d, action=%d, x=%.1f, y=%.1f, result=%d",
         sessionId, action, x, y, result);

    return result;
}

int OHInputBridge::writeMotionEvent(int fd, uint32_t seq, int32_t action,
                                     float x, float y,
                                     int64_t downTime, int64_t eventTime) {
    /*
     * Write an InputMessage to the socket fd.
     *
     * The message format must match what android::InputConsumer::consume()
     * expects. InputConsumer reads the header first to determine message type,
     * then reads the body based on type.
     *
     * For Phase 1, we construct the message manually. In Phase 2, we should
     * use the InputPublisher class directly (requires linking libinput).
     */

    // Construct the message buffer
    // Total message = header + motion body
    struct {
        InputMessageHeader header;
        MotionEventBody body;
    } msg;

    memset(&msg, 0, sizeof(msg));

    // Header
    msg.header.type = INPUT_MSG_TYPE_MOTION;
    msg.header.seq = seq;

    // Motion body
    msg.body.eventId = static_cast<int32_t>(seq);
    msg.body.deviceId = 1;
    msg.body.source = AINPUT_SOURCE_TOUCHSCREEN;
    msg.body.displayId = 0;
    msg.body.action = action;
    msg.body.flags = 0;
    msg.body.edgeFlags = 0;
    msg.body.metaState = 0;
    msg.body.buttonState = 0;
    msg.body.classification = 0;

    // Identity transform
    msg.body.dsdx = 1.0f;
    msg.body.dtdx = 0.0f;
    msg.body.tx = 0.0f;
    msg.body.dtdy = 0.0f;
    msg.body.dsdy = 1.0f;
    msg.body.ty = 0.0f;

    msg.body.xPrecision = 1.0f;
    msg.body.yPrecision = 1.0f;
    msg.body.xCursorPosition = x;
    msg.body.yCursorPosition = y;

    // Raw transform = identity
    msg.body.rawDsdx = 1.0f;
    msg.body.rawDtdx = 0.0f;
    msg.body.rawTx = 0.0f;
    msg.body.rawDtdy = 0.0f;
    msg.body.rawDsdy = 1.0f;
    msg.body.rawTy = 0.0f;

    msg.body.downTime = downTime;
    msg.body.eventTime = eventTime;

    // Single pointer
    msg.body.pointerCount = 1;
    msg.body.pointerProperties.id = 0;
    msg.body.pointerProperties.toolType = AMOTION_EVENT_TOOL_TYPE_FINGER;

    // Pointer coordinates: set X, Y, pressure, size axes
    msg.body.pointerCoords.bits = AXIS_X_BIT | AXIS_Y_BIT
                                  | AXIS_PRESSURE_BIT | AXIS_SIZE_BIT;
    msg.body.pointerCoords.values[0] = x;        // AXIS_X
    msg.body.pointerCoords.values[1] = y;        // AXIS_Y
    msg.body.pointerCoords.values[2] = 1.0f;     // AXIS_PRESSURE
    msg.body.pointerCoords.values[3] = 0.01f;    // AXIS_SIZE

    // Write to socket
    ssize_t written = send(fd, &msg, sizeof(msg), MSG_DONTWAIT | MSG_NOSIGNAL);
    if (written < 0) {
        LOGE("writeMotionEvent: send failed, errno=%d (%s)", errno, strerror(errno));
        return -errno;
    }

    return 0;
}

void OHInputBridge::monitorOHInputEvents() {
    LOGI("OH input monitor thread started");

    while (monitoring_.load()) {
        std::vector<pollfd> fds;
        std::vector<int32_t> sessionIds;

        {
            std::lock_guard<std::mutex> lock(mutex_);
            for (auto& pair : sessions_) {
                if (pair.second.ohInputFd >= 0 && pair.second.serverFd >= 0) {
                    pollfd pfd;
                    pfd.fd = pair.second.ohInputFd;
                    pfd.events = POLLIN;
                    pfd.revents = 0;
                    fds.push_back(pfd);
                    sessionIds.push_back(pair.first);
                }
            }
        }

        if (fds.empty()) {
            // No OH input fds to monitor yet, sleep briefly
            usleep(100000); // 100ms
            continue;
        }

        int ret = poll(fds.data(), fds.size(), 100 /* timeout_ms */);
        if (ret <= 0) continue;

        for (size_t i = 0; i < fds.size(); i++) {
            if (fds[i].revents & POLLIN) {
                /*
                 * Read OH input event and convert to Android MotionEvent.
                 *
                 * OH PointerEvent format (from MultiModal Input framework):
                 * - action: DOWN/UP/MOVE
                 * - pointerId, x, y, pressure
                 * - timestamp
                 *
                 * Phase 2: Parse the actual OH MMI event format here.
                 * Phase 1: The monitoring thread is set up but real OH
                 * event parsing depends on the actual OH MMI binary format.
                 */
                uint8_t buf[4096];
                ssize_t nread = read(fds[i].fd, buf, sizeof(buf));
                if (nread > 0) {
                    LOGD("OH input event received: session=%d, %zd bytes",
                         sessionIds[i], nread);
                    // Phase 2: Parse OH event format and call injectTouchEvent()
                }
            }
        }
    }

    LOGI("OH input monitor thread stopped");
}

}  // namespace oh_adapter
