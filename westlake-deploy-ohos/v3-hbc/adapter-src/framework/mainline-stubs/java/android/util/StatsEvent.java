// B.18 (2026-04-28) — hand-augmented mainline stub for StatsEvent.
// FrameworkStatsLog.write() uses StatsEvent.newBuilder().setAtomId().write*().build()
// fluent API. Empty stub (auto-generated) lacked these methods → NoSuchMethodError.
//
// Per overall_design §15.1 第 3 条原则: HelloWorld 不依赖统计事件，所有 method
// 返 safe default（链式 builder 返自身；build 返 StatsEvent 实例）。
package android.util;

public class StatsEvent {

    public StatsEvent() {}

    public static Builder newBuilder() { return new Builder(); }

    public byte[] getBytes() { return new byte[0]; }
    public int getNumBytes() { return 0; }
    public int getAtomId() { return 0; }
    public void release() {}

    public static final class Builder {
        public Builder() {}

        public Builder setAtomId(int atomId) { return this; }
        public Builder writeBoolean(boolean v) { return this; }
        public Builder writeInt(int v) { return this; }
        public Builder writeLong(long v) { return this; }
        public Builder writeFloat(float v) { return this; }
        public Builder writeString(String v) { return this; }
        public Builder writeByteArray(byte[] v) { return this; }
        public Builder writeBooleanArray(boolean[] v) { return this; }
        public Builder writeIntArray(int[] v) { return this; }
        public Builder writeLongArray(long[] v) { return this; }
        public Builder writeFloatArray(float[] v) { return this; }
        public Builder writeStringArray(String[] v) { return this; }
        public Builder writeKeyValuePairs(android.util.SparseIntArray ki,
                                           android.util.SparseLongArray kl,
                                           android.util.SparseArray<String> ks,
                                           android.util.SparseArray<Float> kf) {
            return this;
        }

        public Builder addBooleanAnnotation(byte id, boolean v) { return this; }
        public Builder addIntAnnotation(byte id, int v) { return this; }

        public Builder usePooledBuffer() { return this; }

        public StatsEvent build() { return new StatsEvent(); }
    }
}
