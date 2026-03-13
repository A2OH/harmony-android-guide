package android.hardware.input;

/**
 * Android-compatible InputManager shim. Stub implementation.
 */
public class InputManager {

    /** Listener for input device state changes. */
    public interface InputDeviceListener {
        void onInputDeviceAdded(int deviceId);
        void onInputDeviceChanged(int deviceId);
        void onInputDeviceRemoved(int deviceId);
    }

    /** Stub InputDevice returned by getInputDevice(). */
    public static class InputDevice {
        private final int mId;
        public InputDevice(int id) { mId = id; }
        public int getId() { return mId; }
        @Override public String toString() { return "InputDevice{id=" + mId + "}"; }
    }

    private static final InputManager sInstance = new InputManager();

    private InputManager() {}

    public static InputManager getInstance() { return sInstance; }

    /**
     * @param id device id
     * @return null stub (no real device)
     */
    public InputDevice getInputDevice(int id) { return null; }

    public int[] getInputDeviceIds() { return new int[0]; }

    /**
     * @param listener the listener to register
     * @param handler  handler on which to invoke the listener (Object; may be null)
     */
    public void registerInputDeviceListener(InputDeviceListener listener, Object handler) {
        // stub: no-op
    }

    public void unregisterInputDeviceListener(InputDeviceListener listener) {
        // stub: no-op
    }
}
