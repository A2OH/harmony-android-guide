package android.webkit;
import java.io.OutputStream;
import java.util.concurrent.Executor;

public class TracingController {
    public TracingController() {}

    public  boolean isTracing() { return false; }
    public  void start(TracingConfig p0) { return; }
    public  boolean stop(OutputStream p0, Executor p1) { return false; }
}
