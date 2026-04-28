package androidx.lifecycle;

import androidx.lifecycle.viewmodel.CreationExtras;

public class ViewModelProvider {
    public interface Factory {
        <T extends ViewModel> T create(Class<T> modelClass);

        default <T extends ViewModel> T create(Class<T> modelClass, CreationExtras extras) {
            return create(modelClass);
        }
    }

    private static final String DEFAULT_KEY_PREFIX =
            "androidx.lifecycle.ViewModelProvider.DefaultKey:";

    private final ViewModelStore mStore;
    private final Factory mFactory;
    private final CreationExtras mDefaultCreationExtras;

    public ViewModelProvider(ViewModelStoreOwner owner) {
        this(owner, defaultFactory());
    }

    public ViewModelProvider(ViewModelStoreOwner owner, Factory factory) {
        this(owner != null ? owner.getViewModelStore() : new ViewModelStore(),
                factory,
                CreationExtras.Empty.c);
    }

    public ViewModelProvider(ViewModelStore store, Factory factory) {
        this(store, factory, CreationExtras.Empty.c);
    }

    public ViewModelProvider(ViewModelStore store, Factory factory, CreationExtras defaultCreationExtras) {
        mStore = store != null ? store : new ViewModelStore();
        mFactory = factory != null ? factory : defaultFactory();
        mDefaultCreationExtras = defaultCreationExtras != null
                ? defaultCreationExtras
                : CreationExtras.Empty.c;
    }

    private ViewModelProvider(ViewModelStoreOwner owner,
            Factory factory,
            CreationExtras defaultCreationExtras) {
        mStore = owner != null ? owner.getViewModelStore() : new ViewModelStore();
        mFactory = factory != null ? factory : defaultFactory();
        mDefaultCreationExtras = defaultCreationExtras != null
                ? defaultCreationExtras
                : CreationExtras.Empty.c;
    }

    public <T extends ViewModel> T get(Class<T> modelClass) {
        String canonicalName = modelClass != null ? modelClass.getCanonicalName() : null;
        String key = canonicalName != null ? DEFAULT_KEY_PREFIX + canonicalName : DEFAULT_KEY_PREFIX;
        return get(key, modelClass);
    }

    /** Obfuscated alias (R8 renames get → a) */
    public <T extends ViewModel> T a(Class<T> modelClass) {
        return get(modelClass);
    }

    /** get(String, Class) variant */
    public <T extends ViewModel> T get(String key, Class<T> modelClass) {
        if (modelClass == null) {
            throw new IllegalArgumentException("modelClass == null");
        }
        String actualKey = key != null ? key : DEFAULT_KEY_PREFIX + modelClass.getName();
        ViewModel existing = mStore.get(actualKey);
        if (modelClass.isInstance(existing)) {
            return modelClass.cast(existing);
        }
        T created = mFactory.create(modelClass, mDefaultCreationExtras);
        mStore.put(actualKey, created);
        return created;
    }

    private static Factory defaultFactory() {
        return new Factory() {
            @Override public <T extends ViewModel> T create(Class<T> c) {
                try {
                    return c.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
