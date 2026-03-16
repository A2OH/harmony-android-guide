package dalvik.system;

public class DexClassLoader extends BaseDexClassLoader {
    public DexClassLoader(String dexPath, String optimizedDirectory,
                           String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory != null ? new java.io.File(optimizedDirectory) : null,
              librarySearchPath, parent);
    }
}
