/*
 * InputEventBridge.java
 *
 * Bridges OH input events to Android InputChannel.
 *
 * When OH's MMI framework delivers touch/key events to an adapter window,
 * this class receives them (via JNI from the native layer) and publishes
 * them to the Android InputChannel's server end. ViewRootImpl's
 * WindowInputEventReceiver reads from the client end, completing the pipeline:
 *
 *   OH MMI -> OH Input Channel fd -> Native InputEventBridge
 *   -> InputPublisher -> Android InputChannel (server)
 *   -> Android InputChannel (client) -> ViewRootImpl -> View hierarchy
 */
package adapter.window;

import android.os.IBinder;
import android.util.Log;
import android.view.InputChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InputEventBridge {

    private static final String TAG = "OH_InputEventBridge";
    private static volatile InputEventBridge sInstance;

    // Maps window token -> session ID for input routing
    private final Map<IBinder, Integer> mWindowSessionMap = new ConcurrentHashMap<>();

    // Maps session ID -> server-side InputChannel
    private final Map<Integer, InputChannel> mServerChannels = new ConcurrentHashMap<>();

    public static InputEventBridge getInstance() {
        if (sInstance == null) {
            synchronized (InputEventBridge.class) {
                if (sInstance == null) {
                    sInstance = new InputEventBridge();
                }
            }
        }
        return sInstance;
    }

    private InputEventBridge() {
    }

    /**
     * Create an InputChannel pair for a window.
     * Returns the client-side InputChannel (for ViewRootImpl).
     * The server-side channel is retained and registered with the native layer.
     *
     * @param windowToken Android IWindow binder token
     * @param sessionId   OH window session ID
     * @param windowName  Window name for debugging
     * @return Client-side InputChannel to be given to ViewRootImpl
     */
    public InputChannel createInputChannelPair(IBinder windowToken, int sessionId,
                                                String windowName) {
        String channelName = windowName + " (OH session " + sessionId + ")";
        InputChannel[] channels = InputChannel.openInputChannelPair(channelName);

        // channels[0] = server (we write to this)
        // channels[1] = client (ViewRootImpl reads from this)
        InputChannel serverChannel = channels[0];
        InputChannel clientChannel = channels[1];

        mServerChannels.put(sessionId, serverChannel);
        mWindowSessionMap.put(windowToken, sessionId);

        // Register the server channel's fd with the native InputPublisher
        nativeRegisterInputChannel(sessionId, serverChannel);

        Log.i(TAG, "InputChannel pair created: session=" + sessionId
                + ", name=" + channelName);

        return clientChannel;
    }

    /**
     * Remove an InputChannel pair when a window is destroyed.
     */
    public void destroyInputChannel(IBinder windowToken) {
        Integer sessionId = mWindowSessionMap.remove(windowToken);
        if (sessionId != null) {
            InputChannel serverChannel = mServerChannels.remove(sessionId);
            nativeUnregisterInputChannel(sessionId);
            if (serverChannel != null) {
                serverChannel.dispose();
            }
            Log.i(TAG, "InputChannel destroyed: session=" + sessionId);
        }
    }

    /**
     * Get session ID for a window token.
     */
    public int getSessionId(IBinder windowToken) {
        Integer sessionId = mWindowSessionMap.get(windowToken);
        return sessionId != null ? sessionId : -1;
    }

    // ==================== Native Methods ====================

    /**
     * Register a server-side InputChannel with the native InputPublisher.
     * The native layer creates an InputPublisher wrapping the channel's fd,
     * ready to receive OH input events and publish them as Android MotionEvents.
     */
    private static native void nativeRegisterInputChannel(int sessionId,
                                                           InputChannel serverChannel);

    /**
     * Unregister and clean up the native InputPublisher for a session.
     */
    private static native void nativeUnregisterInputChannel(int sessionId);

    /**
     * Called from native code when OH delivers a touch event.
     * This is a callback entry point from the C++ OH input monitoring thread.
     *
     * @param sessionId  OH window session ID
     * @param action     MotionEvent action (ACTION_DOWN=0, ACTION_UP=1, ACTION_MOVE=2)
     * @param x          Touch X coordinate
     * @param y          Touch Y coordinate
     * @param downTime   Time of the initial ACTION_DOWN (nanoseconds)
     * @param eventTime  Time of this event (nanoseconds)
     */
    public static void onOHTouchEvent(int sessionId, int action,
                                       float x, float y,
                                       long downTime, long eventTime) {
        Log.d(TAG, "OH touch event: session=" + sessionId
                + ", action=" + action + ", x=" + x + ", y=" + y);
        // The native layer has already published this event to the InputChannel
        // via InputPublisher. This callback is for logging/debugging only.
    }
}
