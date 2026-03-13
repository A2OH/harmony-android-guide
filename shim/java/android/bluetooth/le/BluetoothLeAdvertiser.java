package android.bluetooth.le;
import android.os.Handler;

public final class BluetoothLeAdvertiser {
    public BluetoothLeAdvertiser() {}

    public void startAdvertising(AdvertiseSettings p0, AdvertiseData p1, AdvertiseCallback p2) {}
    public void startAdvertising(AdvertiseSettings p0, AdvertiseData p1, AdvertiseData p2, AdvertiseCallback p3) {}
    public void startAdvertisingSet(AdvertisingSetParameters p0, AdvertiseData p1, AdvertiseData p2, PeriodicAdvertisingParameters p3, AdvertiseData p4, AdvertisingSetCallback p5) {}
    public void startAdvertisingSet(AdvertisingSetParameters p0, AdvertiseData p1, AdvertiseData p2, PeriodicAdvertisingParameters p3, AdvertiseData p4, AdvertisingSetCallback p5, Handler p6) {}
    public void startAdvertisingSet(AdvertisingSetParameters p0, AdvertiseData p1, AdvertiseData p2, PeriodicAdvertisingParameters p3, AdvertiseData p4, int p5, int p6, AdvertisingSetCallback p7) {}
    public void startAdvertisingSet(AdvertisingSetParameters p0, AdvertiseData p1, AdvertiseData p2, PeriodicAdvertisingParameters p3, AdvertiseData p4, int p5, int p6, AdvertisingSetCallback p7, Handler p8) {}
    public void stopAdvertising(AdvertiseCallback p0) {}
    public void stopAdvertisingSet(AdvertisingSetCallback p0) {}
}
