package android.hardware;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Android-compatible SensorDirectChannel shim. Stub implementation.
 */
public class SensorDirectChannel implements Closeable {
    public static final int RATE_STOP = 0;
    public static final int RATE_NORMAL = 1;
    public static final int RATE_FAST = 2;
    public static final int RATE_VERY_FAST = 3;

    public static final int TYPE_MEMORY_FILE = 1;
    public static final int TYPE_HARDWARE_BUFFER = 2;

    private static final AtomicInteger sNextToken = new AtomicInteger(1);
    private boolean mValid;

    public SensorDirectChannel() {
        mValid = true;
    }

    public int configure(Sensor sensor, int rate) {
        if (!mValid) return 0;
        if (rate == RATE_STOP) return 0;
        return sNextToken.getAndIncrement();
    }

    public boolean isValid() {
        return mValid;
    }

    @Override
    public void close() throws IOException {
        mValid = false;
    }
}
