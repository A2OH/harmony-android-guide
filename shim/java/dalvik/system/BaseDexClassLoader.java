package dalvik.system;
import java.io.File;

public class BaseDexClassLoader extends ClassLoader {
    private final String dexPath;
    private final String optimizedDirectory;
    private final String librarySearchPath;

    public BaseDexClassLoader(String dexPath, java.io.File optimizedDir, String libraryPath, ClassLoader parent) {
        super(parent);
        this.dexPath = dexPath;
        this.optimizedDirectory = optimizedDir != null ? optimizedDir.getAbsolutePath() : null;
        this.librarySearchPath = libraryPath;
    }

    public BaseDexClassLoader() {
        this.dexPath = null;
        this.optimizedDirectory = null;
        this.librarySearchPath = null;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // In the engine model, all DEX files are on the boot classpath.
        // Try Class.forName with the parent loader first.
        ClassLoader parent = getParent();
        if (parent != null) {
            try {
                return Class.forName(name, true, parent);
            } catch (ClassNotFoundException e) {
                // Fall through
            }
        }
        // Try system class loader
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(
                "Class not found in dexPath (" + dexPath + "): " + name, e);
        }
    }

    public String findLibrary(String name) {
        // Check app native lib dir
        String nativeDir = System.getProperty("app.native.lib.dir");
        if (nativeDir != null) {
            java.io.File lib = new java.io.File(nativeDir, "lib" + name + ".so");
            if (lib.exists()) return lib.getAbsolutePath();
        }
        if (librarySearchPath != null) {
            for (String dir : librarySearchPath.split(":")) {
                java.io.File lib = new java.io.File(dir, "lib" + name + ".so");
                if (lib.exists()) return lib.getAbsolutePath();
            }
        }
        return null;
    }

    public java.util.Enumeration<java.net.URL> findResources(String p0) { return null; }

    @Override
    public String toString() {
        return getClass().getName() + "[dexPath=" + dexPath + "]";
    }
}
