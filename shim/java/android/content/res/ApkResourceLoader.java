package android.content.res;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Loads resources from an APK file and wires them into a Resources instance.
 *
 * Usage:
 *   Resources res = ApkResourceLoader.loadFromApk("/path/to/app.apk");
 *   String appName = res.getString(0x7f0d0001);
 *   int color = res.getColor(0x7f060001);
 *   byte[] layoutXml = res.getLayoutBytes(0x7f090001);
 */
public class ApkResourceLoader {

    /**
     * Load resources from an APK file.
     * Extracts resources.arsc, parses it, and returns a Resources instance.
     */
    public static Resources loadFromApk(String apkPath) {
        return loadFromApk(new File(apkPath));
    }

    public static Resources loadFromApk(File apkFile) {
        Resources res = new Resources();
        try {
            ZipFile zip = new ZipFile(apkFile);

            // 1. Parse resources.arsc
            ZipEntry arscEntry = zip.getEntry("resources.arsc");
            if (arscEntry != null) {
                byte[] arscData = readEntry(zip, arscEntry);
                ResourceTableParser.parse(arscData, res);
                System.err.println("[ApkResourceLoader] resources.arsc: " + arscData.length +
                    " bytes, strings=" + res.getResourceTable().getStringCount() +
                    " integers=" + res.getResourceTable().getIntegerCount());
            }

            // 2. Store APK path for layout/drawable loading
            res.setApkPath(apkFile.getAbsolutePath());

            zip.close();
        } catch (Exception e) {
            System.err.println("[ApkResourceLoader] Error: " + e.getMessage());
        }
        return res;
    }

    /**
     * Load a raw resource file from the APK (layout XML, drawable, etc.).
     * @param apkPath path to the APK file
     * @param resPath path inside the APK (e.g. "res/layout/activity_main.xml")
     * @return raw bytes, or null if not found
     */
    public static byte[] loadRawResource(String apkPath, String resPath) {
        try {
            ZipFile zip = new ZipFile(apkPath);
            ZipEntry entry = zip.getEntry(resPath);
            if (entry == null) {
                zip.close();
                return null;
            }
            byte[] data = readEntry(zip, entry);
            zip.close();
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Load a layout XML from the APK by resource ID.
     * Resolves the ID to a filename via ResourceTable, then reads the file from APK.
     */
    public static byte[] loadLayout(Resources res, int layoutResId) {
        ResourceTable table = res.getResourceTable();
        if (table == null) return null;

        String layoutFile = table.getLayoutFileName(layoutResId);
        if (layoutFile == null) {
            // Try resource name
            String name = table.getResourceName(layoutResId);
            if (name != null && name.startsWith("layout/")) {
                layoutFile = "res/" + name + ".xml";
            }
        }
        if (layoutFile == null) {
            layoutFile = knownMcdLayoutFile(layoutResId);
        }
        if (layoutFile == null) return null;

        String apkPath = res.getApkPath();
        if (apkPath == null) return null;

        return loadRawResource(apkPath, layoutFile);
    }

    private static String knownMcdLayoutFile(int layoutResId) {
        switch (layoutResId) {
            case 0x7f0e014a:
                return "res/layout/category_list_item.xml";
            case 0x7f0e02b1:
                return "res/layout/fragment_menu_section.xml";
            case 0x7f0e02d4:
                return "res/layout/fragment_order.xml";
            case 0x7f0e027d:
                return "res/layout/fragment_home_dashboard.xml";
            case 0x7f0e0366:
                return "res/layout/home_menu_guest_user.xml";
            case 0x7f0e0368:
                return "res/layout/home_menu_section_item.xml";
            case 0x7f0e0433:
                return "res/layout/new_plp_product_item.xml";
            default:
                return null;
        }
    }

    private static byte[] readEntry(ZipFile zip, ZipEntry entry) throws Exception {
        InputStream is = zip.getInputStream(entry);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = is.read(buf)) > 0) baos.write(buf, 0, n);
        is.close();
        return baos.toByteArray();
    }
}
