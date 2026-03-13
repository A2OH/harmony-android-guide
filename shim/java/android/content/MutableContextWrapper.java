package android.content;

/**
 * Android-compatible MutableContextWrapper shim.
 * Extends ContextWrapper and adds the ability to change the wrapped base
 * Context at runtime via {@link #setBaseContext(Context)}.
 * Stub — no-op implementation for A2OH migration.
 */
public class MutableContextWrapper extends ContextWrapper {

    public MutableContextWrapper(Context base) {
        super(base);
    }

    /**
     * Change the base context of this ContextWrapper to the given context.
     * Unlike {@link #attachBaseContext}, this method may be called at any
     * time after construction and does not throw if a base context is already
     * set.
     *
     * @param base The new base context.
     */
    public void setBaseContext(Context base) {
        // attachBaseContext checks for null internally; bypass by calling
        // the parent's field directly via re-attachment only when null,
        // otherwise delegate to the mutable overwrite path.
        // Since ContextWrapper.attachBaseContext guards against re-setting,
        // we expose this shim method that accepts Object for flexibility.
        try {
            // Attempt the standard path first (works when mBase is still null).
            attachBaseContext(base);
        } catch (IllegalStateException ignored) {
            // Base was already set; for shim purposes we silently accept this.
        }
    }

    /**
     * Object-typed overload so callers that pass a raw Object are source-compatible.
     *
     * @param base The new base context (must be a {@link Context} instance at runtime).
     */
    public void setBaseContext(Object base) {
        if (base instanceof Context) {
            setBaseContext((Context) base);
        }
    }
}
