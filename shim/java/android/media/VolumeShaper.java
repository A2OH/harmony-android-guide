package android.media;
import android.content.res.Configuration;
import android.content.res.Configuration;

/**
 * Android-compatible VolumeShaper shim. Stub for volume shaping control.
 */
public class VolumeShaper implements AutoCloseable {

    public VolumeShaper() {}

    public void apply(Operation operation) {
        // no-op
    }

    public float getVolume() { return 1.0f; }

    @Override
    public void close() {
        // no-op
    }

    /**
     * Configuration for a volume shaper.
     */
    public static class Configuration {
        public static final int INTERPOLATOR_TYPE_STEP = 0;
        public static final int INTERPOLATOR_TYPE_LINEAR = 1;
        public static final int INTERPOLATOR_TYPE_CUBIC = 2;
        public static final int INTERPOLATOR_TYPE_CUBIC_MONOTONIC = 3;

        public static final Configuration LINEAR_RAMP =
                new Configuration.Builder()
                        .setInterpolatorType(INTERPOLATOR_TYPE_LINEAR)
                        .setCurve(new float[]{0.0f, 1.0f}, new float[]{0.0f, 1.0f})
                        .setDuration(1000)
                        .build();

        private final int mInterpolatorType;
        private final float[] mTimes;
        private final float[] mVolumes;
        private final long mDurationMs;

        private Configuration(int interpolatorType, float[] times, float[] volumes, long durationMs) {
            mInterpolatorType = interpolatorType;
            mTimes = times;
            mVolumes = volumes;
            mDurationMs = durationMs;
        }

        public int getInterpolatorType() { return mInterpolatorType; }
        public long getDuration() { return mDurationMs; }

        /**
         * Builder for VolumeShaper.Configuration.
         */
        public static class Builder {
            private int mInterpolatorType = INTERPOLATOR_TYPE_CUBIC;
            private float[] mTimes = new float[]{0.0f, 1.0f};
            private float[] mVolumes = new float[]{1.0f, 1.0f};
            private long mDurationMs = 1000;

            public Builder() {}

            public Builder(Configuration configuration) {
                mInterpolatorType = configuration.mInterpolatorType;
                mTimes = configuration.mTimes.clone();
                mVolumes = configuration.mVolumes.clone();
                mDurationMs = configuration.mDurationMs;
            }

            public Builder setInterpolatorType(int interpolatorType) {
                mInterpolatorType = interpolatorType;
                return this;
            }

            public Builder setCurve(float[] times, float[] volumes) {
                mTimes = times;
                mVolumes = volumes;
                return this;
            }

            public Builder setDuration(long durationMs) {
                mDurationMs = durationMs;
                return this;
            }

            public Configuration build() {
                return new Configuration(mInterpolatorType, mTimes, mVolumes, mDurationMs);
            }
        }
    }

    /**
     * Operation to apply to a VolumeShaper.
     */
    public static class Operation {
        public static final Operation PLAY = new Operation(0);
        public static final Operation REVERSE = new Operation(1);

        private final int mOp;

        private Operation(int op) {
            mOp = op;
        }

        /**
         * Builder for VolumeShaper.Operation.
         */
        public static class Builder {
            private int mOp;


            public Builder(Operation operation) {
                mOp = operation.mOp;
            }

            public Builder reverse() {
                mOp = 1;
                return this;
            }

            public Operation build() {
                return new Operation(mOp);
            }
        }
    }
}
