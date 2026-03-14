package android.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Load and parse an APK file.
     *
     * @param apkPath Path to the .apk file
     * @return ApkInfo with parsed manifest and extracted DEX paths
     * @throws IOException if the APK cannot be read or extracted
     */
    public static ApkInfo load(String apkPath) throws IOException {
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            throw new IOException("APK not found: " + apkPath);
        }

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

                if (name.matches("classes\\d*\\.dex")) {
                    File dexOut = new File(extractDir, name);
                    extractEntry(zip, entry, dexOut);
                    info.dexPaths.add(dexOut.getAbsolutePath());
                }
            }

            // Sort DEX paths so classes.dex comes first, then classes2.dex, etc.
            info.dexPaths.sort((a, b) -> {
                String na = new File(a).getName();
                String nb = new File(b).getName();
                return na.compareTo(nb);
            });

            // Parse AndroidManifest.xml
            ZipEntry manifest = zip.getEntry("AndroidManifest.xml");
            if (manifest != null) {
                try (InputStream in = zip.getInputStream(manifest)) {
                    BinaryXmlParser parser = new BinaryXmlParser();
                    parser.parse(in, info);
                }
            }
        }

        return info;
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
}
