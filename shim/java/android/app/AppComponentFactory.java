package android.app;

/**
 * Android-compatible AppComponentFactory shim.
 * Allows an application to override the instantiation of app components
 * (Activity, Application, BroadcastReceiver, Service, ContentProvider).
 *
 * The real class was added in API 28 (Android 9). In the shim layer every
 * method returns {@code null}; subclasses may override them to supply custom
 * instances.
 */
public class AppComponentFactory {

    /**
     * Instantiate an Application from the given class name.
     *
     * @param cl        ClassLoader to use
     * @param className fully-qualified class name
     * @param intent    the Intent that triggered instantiation (typed as Object)
     * @return a new Application instance, or {@code null} for the default path
     */
    public Object instantiateApplication(ClassLoader cl, String className) {
        return null;
    }

    /**
     * Instantiate an Activity from the given class name.
     *
     * @param cl        ClassLoader to use
     * @param className fully-qualified class name
     * @param intent    the Intent that triggered instantiation (typed as Object)
     * @return a new Activity instance, or {@code null} for the default path
     */
    public Object instantiateActivity(ClassLoader cl, String className, Object intent) {
        return null;
    }

    /**
     * Instantiate a BroadcastReceiver from the given class name.
     *
     * @param cl        ClassLoader to use
     * @param className fully-qualified class name
     * @param intent    the Intent that triggered instantiation (typed as Object)
     * @return a new BroadcastReceiver instance, or {@code null} for the default path
     */
    public Object instantiateReceiver(ClassLoader cl, String className, Object intent) {
        return null;
    }

    /**
     * Instantiate a Service from the given class name.
     *
     * @param cl        ClassLoader to use
     * @param className fully-qualified class name
     * @param intent    the Intent that triggered instantiation (typed as Object)
     * @return a new Service instance, or {@code null} for the default path
     */
    public Object instantiateService(ClassLoader cl, String className, Object intent) {
        return null;
    }

    /**
     * Instantiate a ContentProvider from the given class name.
     *
     * @param cl        ClassLoader to use
     * @param className fully-qualified class name
     * @return a new ContentProvider instance, or {@code null} for the default path
     */
    public Object instantiateProvider(ClassLoader cl, String className) {
        return null;
    }
}
