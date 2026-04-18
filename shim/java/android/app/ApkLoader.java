package android.app;

import android.content.res.ResourceTable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ApkLoader — extracts and parses APK files for the engine runtime.
 *
 * An APK is a ZIP file containing:
 * - AndroidManifest.xml (binary AXML format)
 * - classes.dex, classes2.dex, ... (Dalvik bytecode)
 * - resources.arsc, res/, lib/, META-INF/
 *
 * This loader:
 * 1. Opens the APK as a ZIP
 * 2. Extracts all classes*.dex files to a temp directory
 * 3. Parses the binary AndroidManifest.xml
 * 4. Returns an ApkInfo with all metadata needed to launch the app
 */
public class ApkLoader {
    private static native byte[] nativeReadFileBytes(String path);

    /**
     * Load and parse an APK file.
     *
     * @param apkPath Path to the .apk file
     * @return ApkInfo with parsed manifest and extracted DEX paths
     * @throws IOException if the APK cannot be read or extracted
     */
    public static ApkInfo load(String apkPath) throws IOException {
        File apkFile = new File(apkPath);

        ApkInfo info = new ApkInfo();

        // Create extraction directory
        String baseName = apkFile.getName().replace(".apk", "");
        File extractDir = new File(System.getProperty("java.io.tmpdir"),
                "apk-" + baseName + "-" + System.currentTimeMillis());
        extractDir.mkdirs();
        info.extractDir = extractDir.getAbsolutePath();

        try (ZipFile zip = new ZipFile(apkFile)) {
            // Extract DEX files
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();

                if (isDexFileName(name)) {
                    File dexOut = new File(extractDir, name);
                    extractEntry(zip, entry, dexOut);
                    info.dexPaths.add(dexOut.getAbsolutePath());
                }
            }

            // Sort DEX paths so classes.dex comes first, then classes2.dex, etc.
            Collections.sort(info.dexPaths, new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    String na = new File(a).getName();
                    String nb = new File(b).getName();
                    return na.compareTo(nb);
                }
            });

            // Parse AndroidManifest.xml
            ZipEntry manifest = zip.getEntry("AndroidManifest.xml");
            if (manifest != null) {
                try (InputStream in = zip.getInputStream(manifest)) {
                    BinaryXmlParser parser = new BinaryXmlParser();
                    parser.parse(in, info);
                }
            }

            // Second pass: extract assets, native libs, resources.arsc, res/
            String preferredAbi = System.getProperty("os.arch", "");
            // Map Java arch names to Android ABI names
            String[] abiPriority;
            if (preferredAbi.contains("aarch64") || preferredAbi.contains("arm64")) {
                abiPriority = new String[]{"arm64-v8a", "armeabi-v7a", "armeabi"};
            } else if (preferredAbi.contains("arm")) {
                abiPriority = new String[]{"armeabi-v7a", "armeabi", "arm64-v8a"};
            } else if (preferredAbi.contains("amd64") || preferredAbi.contains("x86_64")) {
                abiPriority = new String[]{"x86_64", "x86", "arm64-v8a", "armeabi-v7a"};
            } else {
                abiPriority = new String[]{"x86", "x86_64", "armeabi-v7a", "arm64-v8a"};
            }

            // Find which ABI dir the APK has
            String matchedAbi = null;
            for (String abi : abiPriority) {
                if (zip.getEntry("lib/" + abi + "/") != null) {
                    matchedAbi = abi;
                    break;
                }
            }
            // Fallback: scan entries if directory entry doesn't exist
            if (matchedAbi == null) {
                for (String abi : abiPriority) {
                    String prefix = "lib/" + abi + "/";
                    Enumeration<? extends ZipEntry> scan = zip.entries();
                    while (scan.hasMoreElements()) {
                        if (scan.nextElement().getName().startsWith(prefix)) {
                            matchedAbi = abi;
                            break;
                        }
                    }
                    if (matchedAbi != null) break;
                }
            }

            File assetOutDir = new File(extractDir, "assets");
            File nativeOutDir = new File(extractDir, "native-libs");
            File resOutDir = new File(extractDir, "res");

            Enumeration<? extends ZipEntry> entries2 = zip.entries();
            while (entries2.hasMoreElements()) {
                ZipEntry entry = entries2.nextElement();
                String name = entry.getName();

                // Extract assets/**
                if (name.startsWith("assets/") && !entry.isDirectory()) {
                    // Strip "assets/" prefix and keep sub-path
                    File assetOut = new File(assetOutDir, name.substring("assets/".length()));
                    extractEntry(zip, entry, assetOut);
                    if (info.assetDir == null) {
                        info.assetDir = assetOutDir.getAbsolutePath();
                    }
                }

                // Extract native libs for matching ABI
                if (matchedAbi != null && name.startsWith("lib/" + matchedAbi + "/")
                        && name.endsWith(".so") && !entry.isDirectory()) {
                    String soName = name.substring(name.lastIndexOf('/') + 1);
                    File soOut = new File(nativeOutDir, soName);
                    extractEntry(zip, entry, soOut);
                    info.nativeLibPaths.add(soOut.getAbsolutePath());
                    if (info.nativeLibDir == null) {
                        info.nativeLibDir = nativeOutDir.getAbsolutePath();
                    }
                }

                // Extract res/** files
                if (name.startsWith("res/") && !entry.isDirectory()) {
                    File resOut = new File(resOutDir, name.substring("res/".length()));
                    extractEntry(zip, entry, resOut);
                    if (info.resDir == null) {
                        info.resDir = resOutDir.getAbsolutePath();
                    }
                }
            }

            // Parse resources.arsc
            ZipEntry arscEntry = zip.getEntry("resources.arsc");
            if (arscEntry != null) {
                try (InputStream in = zip.getInputStream(arscEntry)) {
                    byte[] arscData = readAllBytes(in);
                    ResourceTable resTable = new ResourceTable();
                    resTable.parse(arscData);
                    info.resourceTable = resTable;
                } catch (Exception e) {
                    // Non-fatal: some APKs may have malformed resources
                }
            }

            // Set the native lib dir system property for ClassLoader.findLibrary
            if (info.nativeLibDir != null) {
                System.setProperty("app.native.lib.dir", info.nativeLibDir);
            }
        }

        return info;
    }

    /** Check if a filename matches classes*.dex pattern */
    private static boolean isDexFileName(String name) {
        if (name.equals("classes.dex")) return true;
        if (name.startsWith("classes") && name.endsWith(".dex")) {
            String mid = name.substring(7, name.length() - 4);
            return isDigits(mid);
        }
        return false;
    }

    private static boolean isDigits(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

    /**
     * Extract a single entry from a ZipFile to a target file.
     */
    private static void extractEntry(ZipFile zip, ZipEntry entry, File target)
            throws IOException {
        target.getParentFile().mkdirs();
        try (InputStream in = zip.getInputStream(entry);
             FileOutputStream out = new FileOutputStream(target)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        }
    }

    /**
     * Read all bytes from an InputStream into a byte array.
     */
    private static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) != -1) {
            baos.write(buf, 0, n);
        }
        return baos.toByteArray();
    }

    /**
     * Build a classpath string from extracted DEX paths (colon-separated).
     */
    public static String buildClasspath(ApkInfo info) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < info.dexPaths.size(); i++) {
            if (i > 0) sb.append(':');
            sb.append(info.dexPaths.get(i));
        }
        return sb.toString();
    }

    /**
     * Load APK info from pre-extracted files (no ZipFile needed).
     * The host app extracts resources.arsc + res/ layouts before spawning dalvikvm.
     */
    public static ApkInfo loadFromExtracted(String resDir, String packageName,
            String activityName) throws IOException {
        ApkInfo info = new ApkInfo();
        info.packageName = packageName;
        info.extractDir = resDir;

        // Parse resources.arsc (skip for large files — interpreter too slow)
        try {
            byte[] data = nativeReadFileBytes(resDir + "/resources.arsc");
            if (data != null && data.length > 0) {
                // Resource bytes are present, but defer parsing to avoid heavy startup work.
            }
        } catch (Exception e) {
            // Ignore missing or unreadable resource table during early bootstrap.
        }

        // Set res dir for layout inflation
        info.assetDir = resDir;
        info.resDir = resDir;

        if (activityName != null && !activityName.isEmpty()) {
            info.launcherActivity = activityName;
            info.activities.add(activityName);
        }

        return info;
    }
}
