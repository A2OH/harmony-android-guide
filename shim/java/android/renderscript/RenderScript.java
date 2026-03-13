package android.renderscript;

/**
 * Android-compatible RenderScript shim. Stub for GPU compute context.
 */
public class RenderScript {

    public enum Priority {
        LOW,
        NORMAL
    }

    public static class RSMessageHandler implements Runnable {
        protected int[] mData;
        protected int mID;
        protected int mLength;

        public RSMessageHandler() {}

        public void run() {}
    }

    private RSMessageHandler mMessageCallback;

    protected RenderScript() {}

    public static RenderScript create(Object context) {
        return new RenderScript();
    }

    public void destroy() {}

    public void finish() {}

    public void setPriority(Priority p) {}

    public RSMessageHandler getMessageHandler() {
        return mMessageCallback;
    }

    public void setMessageHandler(RSMessageHandler msg) {
        mMessageCallback = msg;
    }
}
