package dalvik.system;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
            for (String dir : splitByChar(librarySearchPath, ':')) {
                java.io.File lib = new java.io.File(dir, "lib" + name + ".so");
                if (lib.exists()) return lib.getAbsolutePath();
            }
        }
        return null;
    }

    public static String[] splitByChar(String s, char delim) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        int start = 0;
        for (int i = 0; i <= s.length(); i++) {
            if (i == s.length() || s.charAt(i) == delim) {
                parts.add(s.substring(start, i));
                start = i + 1;
            }
        }
        return parts.toArray(new String[0]);
    }

    @Override
    protected URL findResource(String name) {
        Enumeration<URL> resources = findResources(name);
        return resources.hasMoreElements() ? resources.nextElement() : null;
    }

    @Override
    public Enumeration<URL> findResources(String name) {
        final boolean traceMainDispatcher =
                "META-INF/services/kotlinx.coroutines.internal.MainDispatcherFactory".equals(name);
        if (name == null || name.isEmpty()) {
            return Collections.enumeration(Collections.<URL>emptyList());
        }

        List<URL> matches = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        List<File> containers = resourceContainers();
        if (traceMainDispatcher) {
            System.err.println("[BaseDexCL] findResources(" + name + ") dexPath=" + dexPath
                    + " containers=" + containers);
        }
        for (File container : containers) {
            URL url = findZipResource(container, name);
            if (url == null) {
                continue;
            }
            String key = url.toExternalForm();
            if (seen.add(key)) {
                matches.add(url);
                if (traceMainDispatcher) {
                    System.err.println("[BaseDexCL]   hit " + key);
                }
            }
        }
        if (traceMainDispatcher && matches.isEmpty()) {
            System.err.println("[BaseDexCL]   no matches");
        }
        return Collections.enumeration(matches);
    }

    private List<File> resourceContainers() {
        List<File> result = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        addZipPathList(result, seen, dexPath);
        addZipPathList(result, seen, System.getProperty("java.class.path"));
        addZipPath(result, seen, System.getProperty("westlake.apk.path"));
        addZipPath(result, seen, System.getenv("WESTLAKE_APK_PATH"));
        return result;
    }

    private static void addZipPathList(List<File> out, Set<String> seen, String pathList) {
        if (pathList == null || pathList.isEmpty()) {
            return;
        }
        for (String path : splitByChar(pathList, ':')) {
            addZipPath(out, seen, path);
        }
    }

    private static void addZipPath(List<File> out, Set<String> seen, String path) {
        if (path == null || path.isEmpty()) {
            return;
        }
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            return;
        }
        String lower = file.getName().toLowerCase(java.util.Locale.ROOT);
        if (!lower.endsWith(".apk") && !lower.endsWith(".jar") && !lower.endsWith(".zip")) {
            return;
        }
        String fullPath = file.getAbsolutePath();
        if (seen.add(fullPath)) {
            out.add(file);
        }
    }

    private static URL findZipResource(File container, String name) {
        try (ZipFile zipFile = new ZipFile(container)) {
            ZipEntry entry = zipFile.getEntry(name);
            if (entry == null || entry.isDirectory()) {
                return null;
            }
            return new URL("jar:" + container.toURI().toURL().toExternalForm() + "!/" + name);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "[dexPath=" + dexPath + "]";
    }
}
