package android.bluetooth.le;

/**
 * A2OH shim: Bluetooth LE advertise settings.
 * Maps to OpenHarmony @ohos.bluetooth.ble AdvertisingParams.
 */
public final class AdvertiseSettings {

    public static final int ADVERTISE_MODE_LOW_POWER = 0;
    public static final int ADVERTISE_MODE_BALANCED = 1;
    public static final int ADVERTISE_MODE_LOW_LATENCY = 2;

    public static final int ADVERTISE_TX_POWER_ULTRA_LOW = 0;
    public static final int ADVERTISE_TX_POWER_LOW = 1;
    public static final int ADVERTISE_TX_POWER_MEDIUM = 2;
    public static final int ADVERTISE_TX_POWER_HIGH = 3;

    private final int mode;
    private final int txPowerLevel;
    private final boolean connectable;
    private final int timeout;

    private AdvertiseSettings(int mode, int txPowerLevel, boolean connectable, int timeout) {
        this.mode = mode;
        this.txPowerLevel = txPowerLevel;
        this.connectable = connectable;
        this.timeout = timeout;
    }

    public int getMode() {
        return mode;
    }

    public int getTxPowerLevel() {
        return txPowerLevel;
    }

    public boolean isConnectable() {
        return connectable;
    }

    public int getTimeout() {
        return timeout;
    }

    public static final class Builder {
        private int mode = ADVERTISE_MODE_LOW_POWER;
        private int txPowerLevel = ADVERTISE_TX_POWER_MEDIUM;
        private boolean connectable = true;
        private int timeout = 0;

        public Builder setAdvertiseMode(int advertiseMode) {
            this.mode = advertiseMode;
            return this;
        }

        public Builder setTxPowerLevel(int txPowerLevel) {
            this.txPowerLevel = txPowerLevel;
            return this;
        }

        public Builder setConnectable(boolean connectable) {
            this.connectable = connectable;
            return this;
        }

        public Builder setTimeout(int timeoutMillis) {
            this.timeout = timeoutMillis;
            return this;
        }

        public AdvertiseSettings build() {
            return new AdvertiseSettings(mode, txPowerLevel, connectable, timeout);
        }
    }
}
