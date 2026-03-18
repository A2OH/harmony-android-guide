package android.view;

import android.os.Handler;

/** Auto-generated stub for AOSP compilation. */
public class FrameMetricsObserver {
    public Window.OnFrameMetricsAvailableListener mListener;

    public FrameMetricsObserver(Window window, Handler handler, Window.OnFrameMetricsAvailableListener listener) {
        mListener = listener;
    }

    public Object getRendererObserver() { return null; }
}
