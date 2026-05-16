/*
 * oh_input_bridge.h
 *
 * Native input event bridge between OH MMI and Android InputChannel.
 *
 * Manages per-session InputPublisher instances that write Android-format
 * MotionEvents to the server side of an InputChannel pair. ViewRootImpl
 * reads from the client side, completing the touch event pipeline.
 *
 * Also monitors OH input event fds (obtained during session creation)
 * and forwards events through the InputPublisher.
 */
#ifndef OH_INPUT_BRIDGE_H
#define OH_INPUT_BRIDGE_H

#include <jni.h>
#include <cstdint>
#include <mutex>
#include <unordered_map>
#include <thread>
#include <atomic>

namespace oh_adapter {

/**
 * Manages input event bridging for all window sessions.
 *
 * For each window session, holds:
 * - The server-side InputChannel fd (from Java InputChannel pair)
 * - The OH input event fd (from OH session creation)
 * - An event forwarding mechanism between the two
 */
class OHInputBridge {
public:
    static OHInputBridge& getInstance();

    /**
     * Register a server-side Android InputChannel fd for a session.
     * Called from Java when InputEventBridge creates a channel pair.
     */
    void registerInputChannel(int32_t sessionId, int serverFd);

    /**
     * Unregister and clean up for a session.
     */
    void unregisterInputChannel(int32_t sessionId);

    /**
     * Register the OH-side input event fd for a session.
     * Called when the OH session is created and returns its input channel fd.
     * Starts monitoring for OH input events on this fd.
     */
    void registerOHInputFd(int32_t sessionId, int ohInputFd);

    /**
     * Inject a touch event into the Android InputChannel for a session.
     * Converts parameters to Android InputMessage format and writes
     * to the server-side fd.
     *
     * @param sessionId  Window session ID
     * @param action     MotionEvent action (0=DOWN, 1=UP, 2=MOVE)
     * @param x          Touch X coordinate in window space
     * @param y          Touch Y coordinate in window space
     * @param downTime   Timestamp of ACTION_DOWN in nanoseconds
     * @param eventTime  Timestamp of this event in nanoseconds
     * @return 0 on success, negative on error
     */
    int32_t injectTouchEvent(int32_t sessionId, int32_t action,
                              float x, float y,
                              int64_t downTime, int64_t eventTime);

    /**
     * Set JNI context for callbacks to Java layer.
     */
    void setJavaVM(JavaVM* jvm) { jvm_ = jvm; }

private:
    OHInputBridge() = default;
    ~OHInputBridge() = default;

    struct SessionInput {
        int serverFd = -1;      // Android server-side InputChannel fd
        int ohInputFd = -1;     // OH input event fd
        uint32_t seq = 0;       // Sequence number for InputPublisher protocol
    };

    JavaVM* jvm_ = nullptr;
    std::mutex mutex_;
    std::unordered_map<int32_t, SessionInput> sessions_;

    // OH input event monitoring
    std::atomic<bool> monitoring_{false};
    std::thread monitorThread_;

    /**
     * Write an InputMessage to the server fd.
     * Constructs a minimal InputMessage struct for a single-pointer MotionEvent.
     */
    int writeMotionEvent(int fd, uint32_t seq, int32_t action,
                          float x, float y,
                          int64_t downTime, int64_t eventTime);

    /**
     * Monitor thread: polls OH input fds and forwards events.
     */
    void monitorOHInputEvents();
};

}  // namespace oh_adapter

#endif  // OH_INPUT_BRIDGE_H
