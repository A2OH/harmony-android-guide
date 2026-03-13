package android.printservice;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import java.util.List;

public class PrintService extends Service {
    public static final int EXTRA_CAN_SELECT_PRINTER = 0;
    public static final int EXTRA_PRINTER_INFO = 0;
    public static final int EXTRA_PRINT_DOCUMENT_INFO = 0;
    public static final int EXTRA_PRINT_JOB_INFO = 0;
    public static final int EXTRA_SELECT_PRINTER = 0;
    public static final int SERVICE_INTERFACE = 0;
    public static final int SERVICE_META_DATA = 0;

    public PrintService() {}

    public void attachBaseContext(Context p0) {}
    public List<?> getActivePrintJobs() { return null; }
    public IBinder onBind(Intent p0) { return null; }
    public void onConnected() {}
    public void onDisconnected() {}
    public void onPrintJobQueued(PrintJob p0) {}
    public void onRequestCancelPrintJob(PrintJob p0) {}
}
