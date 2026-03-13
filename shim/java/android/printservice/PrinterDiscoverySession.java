package android.printservice;
import android.os.CancellationSignal;
import android.print.PrinterId;
import android.os.CancellationSignal;
import android.print.PrinterId;
import java.util.List;

public class PrinterDiscoverySession {
    public PrinterDiscoverySession() {}

    public void addPrinters(java.util.List<Object> p0) {}
    public boolean isDestroyed() { return false; }
    public boolean isPrinterDiscoveryStarted() { return false; }
    public  void onDestroy() { return; }
    public void onRequestCustomPrinterIcon(PrinterId p0, CancellationSignal p1, CustomPrinterIconCallback p2) {}
    public  void onStartPrinterDiscovery(java.util.List<Object> p0) { return; }
    public  void onStartPrinterStateTracking(PrinterId p0) { return; }
    public  void onStopPrinterDiscovery() { return; }
    public  void onStopPrinterStateTracking(PrinterId p0) { return; }
    public  void onValidatePrinters(java.util.List<Object> p0) { return; }
    public void removePrinters(java.util.List<Object> p0) {}
}
