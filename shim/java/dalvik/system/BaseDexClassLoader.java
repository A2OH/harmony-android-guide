package dalvik.system;
import java.io.File;

public class BaseDexClassLoader extends ClassLoader {
    public BaseDexClassLoader(String dexPath, java.io.File optimizedDir, String libraryPath, ClassLoader parent) { super(parent); }
    public BaseDexClassLoader() {}

    public String findLibrary(String p0) { return null; }
    public java.util.Enumeration<java.net.URL> findResources(String p0) { return null; }
}
