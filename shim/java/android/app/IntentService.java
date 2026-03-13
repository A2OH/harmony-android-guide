package android.app;

import android.content.Intent;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Shim: android.app.IntentService
 * Tier 2 — abstract base that processes Intents on a single background thread.
 *
 * OH mapping: subclasses override onHandleIntent(); the OH adapter
 * dispatches each queued work item via a TaskDispatcher background task.
 *
 * This shim provides a fully functional pure-Java implementation so that
 * subclasses compile and run on the JVM shim layer unchanged.
 */
public abstract class IntentService extends Service {

    private final String mName;
    private volatile boolean mRedelivery;

    private final LinkedBlockingQueue<Intent> mQueue = new LinkedBlockingQueue<>();
    private Thread mWorker;
    private volatile boolean mStopped;

    /**
     * Creates an IntentService.
     *
     * @param name Used to name the worker thread; important for debugging.
     */
    public IntentService(String name) {
        mName = name;
    }

    /**
     * Set intent redelivery behaviour.
     * If true, onStartCommand returns START_REDELIVER_INTENT; otherwise START_NOT_STICKY.
     */
    public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStopped = false;
        mWorker = new Thread(mName) {
            @Override
            public void run() {
                while (!mStopped) {
                    Intent intent;
                    try {
                        intent = mQueue.take();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    onHandleIntent(intent);
                }
            }
        };
        mWorker.start();
    }

    @Override
    public void onDestroy() {
        mStopped = true;
        if (mWorker != null) {
            mWorker.interrupt();
        }
        super.onDestroy();
    }

    /**
     * Unless you provide binding for your service, you don't need to implement this
     * method, because the default implementation returns null.
     */
    @Override
    public android.os.IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Queues the intent for processing on the background thread.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mQueue.add(intent);
        }
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Subclasses must implement this method. Only one Intent is processed at a time,
     * but the processing happens on a worker thread that runs independently from other
     * application logic. When all requests have been handled, the IntentService stops itself.
     *
     * @param intent The Intent delivered to this service, or null if it was re-started.
     */
    protected abstract void onHandleIntent(Intent intent);
}
