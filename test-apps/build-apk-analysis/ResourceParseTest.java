import java.io.*;
import java.util.*;
import java.util.zip.*;
import android.content.res.ResourceTableParser;
import android.content.res.ResourceTable;
import android.content.res.Resources;

public class ResourceParseTest {

    public static void main(String[] args) {
        String[] apks = {
            "/tmp/test-apks/apk1/multi.apk",
            "/tmp/test-apks/apk2/form.apk",
            "/tmp/test-apks/apk3/data.apk"
        };
        String[] names = {"Multi-Activity", "Form App", "Data App"};

        int totalPass = 0;
        int totalFail = 0;

        for (int i = 0; i < apks.length; i++) {
            System.out.println("==============================================");
            System.out.println("=== " + names[i] + ": " + apks[i]);
            System.out.println("==============================================");

            try {
                byte[] arscData = extractFromApk(apks[i], "resources.arsc");
                byte[] manifestData = extractFromApk(apks[i], "AndroidManifest.xml");

                if (arscData == null) {
                    System.out.println("  FAIL: resources.arsc not found in APK");
                    totalFail++;
                    continue;
                }
                System.out.println("  resources.arsc: " + arscData.length + " bytes");

                // Test 1: parseToMap
                System.out.println("\n  --- Test: parseToMap ---");
                Map<Integer, Object> resMap = ResourceTableParser.parseToMap(arscData);
                System.out.println("  Resources found: " + resMap.size());
                boolean mapOk = resMap.size() > 0;
                System.out.println("  " + (mapOk ? "PASS" : "FAIL") + ": parseToMap returns entries");
                if (mapOk) totalPass++; else totalFail++;

                // Print all resources
                TreeMap<Integer, Object> sorted = new TreeMap<Integer, Object>(resMap);
                Iterator<Map.Entry<Integer, Object>> iter = sorted.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<Integer, Object> e = iter.next();
                    int id = e.getKey().intValue();
                    Object val = e.getValue();
                    String hex = "0x" + Integer.toHexString(id);
                    System.out.println("    " + hex + " => " + val + " (" + val.getClass().getSimpleName() + ")");
                }

                // Test 2: parseToTable
                System.out.println("\n  --- Test: parseToTable ---");
                ResourceTable table = ResourceTableParser.parseToTable(arscData);
                boolean tableOk = table != null;
                System.out.println("  " + (tableOk ? "PASS" : "FAIL") + ": parseToTable returns non-null");
                if (tableOk) totalPass++; else totalFail++;

                if (tableOk) {
                    String[] pool = table.getGlobalStringPool();
                    System.out.println("  Global string pool: " + (pool != null ? pool.length : 0) + " entries");
                    if (pool != null) {
                        for (int j = 0; j < pool.length && j < 20; j++) {
                            System.out.println("    [" + j + "] " + pool[j]);
                        }
                    }
                    boolean poolOk = pool != null && pool.length > 0;
                    System.out.println("  " + (poolOk ? "PASS" : "FAIL") + ": string pool has entries");
                    if (poolOk) totalPass++; else totalFail++;
                }

                // Test 3: parse into Resources
                System.out.println("\n  --- Test: parse into Resources ---");
                Resources resources = new Resources();
                ResourceTableParser.parse(arscData, resources);
                // Try to get a known string (app_name is always first)
                // R.string type is 0x02 for most aapt outputs, entry 0x0000
                // From the R.java we saw: app_name=0x7f020000
                String appName = resources.getString(0x7f020000);
                boolean strOk = appName != null;
                System.out.println("  getString(0x7f020000) = " + appName);
                System.out.println("  " + (strOk ? "PASS" : "FAIL") + ": getString returns app_name");
                if (strOk) totalPass++; else totalFail++;

                // Test 4: manifest data
                if (manifestData != null) {
                    System.out.println("\n  --- Binary AndroidManifest.xml ---");
                    System.out.println("  Size: " + manifestData.length + " bytes");
                    // Check magic bytes
                    if (manifestData.length >= 4) {
                        int magic = ((manifestData[0] & 0xFF)) | ((manifestData[1] & 0xFF) << 8);
                        System.out.println("  Chunk type: 0x" + Integer.toHexString(magic));
                        boolean isXml = magic == 0x0003; // RES_XML_TYPE
                        System.out.println("  " + (isXml ? "PASS" : "FAIL") + ": manifest is binary XML (type 0x0003)");
                        if (isXml) totalPass++; else totalFail++;
                    }
                }

                // List APK contents
                System.out.println("\n  --- APK Contents ---");
                listApkContents(apks[i]);

            } catch (Exception ex) {
                System.out.println("  FAIL: Exception - " + ex.getMessage());
                ex.printStackTrace();
                totalFail++;
            }

            System.out.println();
        }

        System.out.println("==============================================");
        System.out.println("TOTAL: " + totalPass + " passed, " + totalFail + " failed");
        System.out.println("==============================================");
    }

    static byte[] extractFromApk(String apkPath, String entryName) throws Exception {
        ZipFile zip = new ZipFile(apkPath);
        ZipEntry entry = zip.getEntry(entryName);
        if (entry == null) {
            zip.close();
            return null;
        }
        InputStream is = zip.getInputStream(entry);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int len;
        while ((len = is.read(buf)) != -1) {
            bos.write(buf, 0, len);
        }
        is.close();
        zip.close();
        return bos.toByteArray();
    }

    static void listApkContents(String apkPath) throws Exception {
        ZipFile zip = new ZipFile(apkPath);
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry e = entries.nextElement();
            System.out.println("    " + e.getName() + " (" + e.getSize() + " bytes)");
        }
        zip.close();
    }
}
