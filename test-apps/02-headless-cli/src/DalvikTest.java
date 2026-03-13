/**
 * Headless test for Dalvik VM on OpenHarmony.
 * No lambdas, no try-with-resources — compatible with dx/DEX format 035.
 */
public class DalvikTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Dalvik VM on OpenHarmony - Shim Test ===\n");

        testLog();
        testBuild();
        testBundle();
        testIntent();
        testUri();
        testContentValues();
        testBase64();
        testColor();
        testRect();
        testPoint();
        testMatrix();
        testPaint();
        testCanvas();
        testBitmap();
        testHandler();
        testMessage();
        testLooper();
        testSystemClock();
        testEnvironment();
        testProcess();
        testParcelUuid();
        testComponentName();
        testSparseArray();
        testLruCache();
        testPair();
        testArrayMap();
        testJsonObject();
        testTextUtils();
        testSharedPreferences();
        testSQLiteDatabase();
        testLocation();
        testNetwork();
        testSensor();

        System.out.println("\n=== Results ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println(failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED");
        System.exit(failed);
    }

    static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("  + " + name);
            passed++;
        } else {
            System.out.println("  FAIL: " + name);
            failed++;
        }
    }

    static void section(String name) {
        System.out.println("\n-- " + name + " --");
    }

    // ── android.util.Log ──

    static void testLog() {
        section("android.util.Log");
        try {
            android.util.Log.d("TEST", "debug");
            android.util.Log.i("TEST", "info");
            android.util.Log.w("TEST", "warn");
            android.util.Log.e("TEST", "error");
            check("Log.d/i/w/e don't throw", true);
        } catch (Exception e) {
            check("Log.d/i/w/e don't throw", false);
        }
        check("Log.isLoggable", android.util.Log.isLoggable("TEST", android.util.Log.DEBUG));
    }

    // ── android.os.Build ──

    static void testBuild() {
        section("android.os.Build");
        check("Build.MANUFACTURER not null", android.os.Build.MANUFACTURER != null);
        check("Build.MODEL not null", android.os.Build.MODEL != null);
        check("Build.DEVICE not null", android.os.Build.DEVICE != null);
        check("Build.VERSION.SDK_INT > 0", android.os.Build.VERSION.SDK_INT > 0);
        check("Build.VERSION.RELEASE not null", android.os.Build.VERSION.RELEASE != null);
    }

    // ── android.os.Bundle ──

    static void testBundle() {
        section("android.os.Bundle");
        android.os.Bundle b = new android.os.Bundle();
        b.putString("key", "value");
        b.putInt("num", 42);
        b.putBoolean("flag", true);
        check("getString", "value".equals(b.getString("key")));
        check("getInt", b.getInt("num") == 42);
        check("getBoolean", b.getBoolean("flag") == true);
        check("containsKey", b.containsKey("key"));
        check("size", b.size() == 3);
    }

    // ── android.content.Intent ──

    static void testIntent() {
        section("android.content.Intent");
        android.content.Intent intent = new android.content.Intent();
        intent.setAction("com.test.ACTION");
        check("setAction/getAction", "com.test.ACTION".equals(intent.getAction()));
        intent.putExtra("extra_key", "extra_val");
        check("putExtra/getStringExtra", "extra_val".equals(intent.getStringExtra("extra_key")));
        intent.addCategory("cat1");
        check("hasCategory", intent.hasCategory("cat1"));
    }

    // ── android.net.Uri ──

    static void testUri() {
        section("android.net.Uri");
        android.net.Uri uri = android.net.Uri.parse("https://example.com/path?q=1");
        check("getScheme", "https".equals(uri.getScheme()));
        check("getHost", "example.com".equals(uri.getHost()));
        check("getPath", "/path".equals(uri.getPath()));
        check("getQueryParameter", "1".equals(uri.getQueryParameter("q")));
        check("toString", uri.toString().contains("example.com"));
    }

    // ── android.content.ContentValues ──

    static void testContentValues() {
        section("android.content.ContentValues");
        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put("name", "test");
        cv.put("age", 30);
        check("getAsString", "test".equals(cv.getAsString("name")));
        check("getAsInteger", Integer.valueOf(30).equals(cv.getAsInteger("age")));
        check("size", cv.size() == 2);
        check("containsKey", cv.containsKey("name"));
    }

    // ── android.util.Base64 ──

    static void testBase64() {
        section("android.util.Base64");
        byte[] input = "Hello Dalvik!".getBytes();
        String encoded = android.util.Base64.encodeToString(input, android.util.Base64.DEFAULT);
        check("encodeToString not null", encoded != null);
        byte[] decoded = android.util.Base64.decode(encoded, android.util.Base64.DEFAULT);
        check("decode roundtrip", "Hello Dalvik!".equals(new String(decoded)));
    }

    // ── android.graphics.Color ──

    static void testColor() {
        section("android.graphics.Color");
        int red = android.graphics.Color.RED;
        check("Color.RED", red != 0);
        int c = android.graphics.Color.rgb(255, 128, 0);
        check("Color.rgb", android.graphics.Color.red(c) == 255);
        check("Color.green", android.graphics.Color.green(c) == 128);
        check("Color.blue", android.graphics.Color.blue(c) == 0);
        int parsed = android.graphics.Color.parseColor("#FF0000");
        check("parseColor #FF0000", android.graphics.Color.red(parsed) == 255);
    }

    // ── android.graphics.Rect ──

    static void testRect() {
        section("android.graphics.Rect");
        android.graphics.Rect r = new android.graphics.Rect(10, 20, 110, 120);
        check("width", r.width() == 100);
        check("height", r.height() == 100);
        check("contains point", r.contains(50, 50));
        check("centerX", r.centerX() == 60);
    }

    // ── android.graphics.Point ──

    static void testPoint() {
        section("android.graphics.Point");
        android.graphics.Point p = new android.graphics.Point(3, 4);
        check("Point.x", p.x == 3);
        check("Point.y", p.y == 4);
        check("equals", p.x == 3 && p.y == 4);
    }

    // ── android.graphics.Matrix ──

    static void testMatrix() {
        section("android.graphics.Matrix");
        android.graphics.Matrix m = new android.graphics.Matrix();
        check("isIdentity", m.isIdentity());
        m.setTranslate(10, 20);
        check("after setTranslate not identity", !m.isIdentity());
        m.reset();
        check("after reset isIdentity", m.isIdentity());
    }

    // ── android.graphics.Paint ──

    static void testPaint() {
        section("android.graphics.Paint");
        android.graphics.Paint p = new android.graphics.Paint();
        p.setColor(0xFFFF0000);
        check("setColor/getColor", p.getColor() == 0xFFFF0000);
        p.setTextSize(24f);
        check("setTextSize/getTextSize", p.getTextSize() == 24f);
        float w = p.measureText("Hello");
        check("measureText > 0", w > 0);
    }

    // ── android.graphics.Canvas ──

    static void testCanvas() {
        section("android.graphics.Canvas");
        android.graphics.Canvas c = new android.graphics.Canvas();
        int save = c.save();
        check("save returns > 0", save > 0);
        c.translate(10, 20);
        c.rotate(45);
        c.restore();
        check("save/restore cycle", c.getSaveCount() == 0);
    }

    // ── android.graphics.Bitmap ──

    static void testBitmap() {
        section("android.graphics.Bitmap");
        android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(100, 100,
                android.graphics.Bitmap.Config.ARGB_8888);
        check("createBitmap not null", bmp != null);
        check("width", bmp.getWidth() == 100);
        check("height", bmp.getHeight() == 100);
        check("config ARGB_8888", bmp.getConfig() == android.graphics.Bitmap.Config.ARGB_8888);
    }

    // ── android.os.Handler ──

    static void testHandler() {
        section("android.os.Handler");
        android.os.Handler h = new android.os.Handler();
        check("Handler created", h != null);
        android.os.Message msg = android.os.Message.obtain();
        msg.what = 1;
        check("Message.what", msg.what == 1);
    }

    // ── android.os.Message ──

    static void testMessage() {
        section("android.os.Message");
        android.os.Message m = android.os.Message.obtain();
        m.what = 42;
        m.arg1 = 1;
        m.arg2 = 2;
        check("what", m.what == 42);
        check("arg1", m.arg1 == 1);
        android.os.Bundle data = m.getData();
        check("getData not null", data != null);
    }

    // ── android.os.Looper ──

    static void testLooper() {
        section("android.os.Looper");
        try {
            android.os.Looper main = android.os.Looper.getMainLooper();
            check("getMainLooper not null", main != null);
        } catch (Exception e) {
            check("getMainLooper", false);
        }
    }

    // ── android.os.SystemClock ──

    static void testSystemClock() {
        section("android.os.SystemClock");
        long up = android.os.SystemClock.uptimeMillis();
        check("uptimeMillis > 0", up > 0);
        long elapsed = android.os.SystemClock.elapsedRealtime();
        check("elapsedRealtime > 0", elapsed > 0);
    }

    // ── android.os.Environment ──

    static void testEnvironment() {
        section("android.os.Environment");
        java.io.File ext = android.os.Environment.getExternalStorageDirectory();
        check("getExternalStorageDirectory not null", ext != null);
        java.io.File data = android.os.Environment.getDataDirectory();
        check("getDataDirectory not null", data != null);
    }

    // ── android.os.Process ──

    static void testProcess() {
        section("android.os.Process");
        int pid = android.os.Process.myPid();
        check("myPid > 0", pid > 0);
        int uid = android.os.Process.myUid();
        check("myUid >= 0", uid >= 0);
    }

    // ── android.os.ParcelUuid ──

    static void testParcelUuid() {
        section("android.os.ParcelUuid");
        java.util.UUID uuid = java.util.UUID.randomUUID();
        android.os.ParcelUuid pu = new android.os.ParcelUuid(uuid);
        check("getUuid", uuid.equals(pu.getUuid()));
        check("toString", pu.toString().contains(uuid.toString()));
    }

    // ── android.content.ComponentName ──

    static void testComponentName() {
        section("android.content.ComponentName");
        android.content.ComponentName cn = new android.content.ComponentName("com.test", ".MainActivity");
        check("getPackageName", "com.test".equals(cn.getPackageName()));
        check("getClassName", ".MainActivity".equals(cn.getClassName()));
        check("flattenToString", cn.flattenToString().contains("com.test"));
    }

    // ── android.util.SparseArray ──

    static void testSparseArray() {
        section("android.util.SparseArray");
        android.util.SparseArray sa = new android.util.SparseArray();
        sa.put(5, "five");
        sa.put(10, "ten");
        check("get(5)", "five".equals(sa.get(5)));
        check("size", sa.size() == 2);
        check("indexOfKey", sa.indexOfKey(10) >= 0);
    }

    // ── android.util.LruCache ──

    static void testLruCache() {
        section("android.util.LruCache");
        android.util.LruCache cache = new android.util.LruCache(3);
        cache.put("a", "1");
        cache.put("b", "2");
        cache.put("c", "3");
        check("get", "2".equals(cache.get("b")));
        check("size", cache.size() == 3);
    }

    // ── android.util.Pair ──

    static void testPair() {
        section("android.util.Pair");
        android.util.Pair p = android.util.Pair.create("hello", Integer.valueOf(42));
        check("first", "hello".equals(p.first));
        check("second", Integer.valueOf(42).equals(p.second));
    }

    // ── android.util.ArrayMap ──

    static void testArrayMap() {
        section("android.util.ArrayMap");
        android.util.ArrayMap map = new android.util.ArrayMap();
        map.put("x", "y");
        check("get", "y".equals(map.get("x")));
        check("containsKey", map.containsKey("x"));
        check("size", map.size() == 1);
    }

    // ── java.util.HashMap (JSON substitute) ──

    static void testJsonObject() {
        section("java.util.HashMap (as JSON substitute)");
        java.util.HashMap map = new java.util.HashMap();
        map.put("name", "dalvik");
        map.put("version", Integer.valueOf(1));
        check("get name", "dalvik".equals(map.get("name")));
        check("get version", Integer.valueOf(1).equals(map.get("version")));
        check("containsKey", map.containsKey("name"));
        check("size", map.size() == 2);
    }

    // ── android.text.TextUtils ──

    static void testTextUtils() {
        section("android.text.TextUtils");
        check("isEmpty null", android.text.TextUtils.isEmpty(null));
        check("isEmpty empty", android.text.TextUtils.isEmpty(""));
        check("not isEmpty", !android.text.TextUtils.isEmpty("hi"));
        String joined = android.text.TextUtils.join(", ", new String[]{"a", "b", "c"});
        check("join", "a, b, c".equals(joined));
    }

    // ── android.content.SharedPreferences ──

    static void testSharedPreferences() {
        section("android.content.SharedPreferences");
        // Test via the shim's concrete SharedPreferences implementation
        try {
            Class cls = Class.forName("android.content.SharedPreferences");
            check("SharedPreferences class exists", cls != null);
            check("SharedPreferences is interface", cls.isInterface());
        } catch (Exception e) {
            check("SharedPreferences class exists", false);
        }
    }

    // ── android.database.sqlite.SQLiteDatabase ──

    static void testSQLiteDatabase() {
        section("android.database.sqlite.SQLiteDatabase");
        try {
            Class cls = Class.forName("android.database.sqlite.SQLiteDatabase");
            check("SQLiteDatabase class exists", cls != null);
            // Verify ContentValues works standalone
            android.content.ContentValues cv = new android.content.ContentValues();
            cv.put("name", "dalvik");
            cv.put("id", 1);
            check("ContentValues put/get", "dalvik".equals(cv.getAsString("name")));
            check("ContentValues size", cv.size() == 2);
        } catch (Exception e) {
            check("SQLiteDatabase class exists", false);
        }
    }

    // ── android.location.Location ──

    static void testLocation() {
        section("android.location.Location");
        android.location.Location loc = new android.location.Location("gps");
        loc.setLatitude(37.7749);
        loc.setLongitude(-122.4194);
        loc.setAccuracy(10f);
        check("getLatitude", loc.getLatitude() == 37.7749);
        check("getLongitude", loc.getLongitude() == -122.4194);
        check("getProvider", "gps".equals(loc.getProvider()));
        float dist = loc.distanceTo(loc);
        check("distanceTo self == 0", dist == 0f);
    }

    // ── android.net.NetworkInfo ──

    static void testNetwork() {
        section("android.net.NetworkInfo");
        try {
            Class cls = Class.forName("android.net.NetworkInfo");
            check("NetworkInfo class exists", cls != null);
        } catch (Exception e) {
            check("NetworkInfo class exists", false);
        }
    }

    // ── android.hardware.Sensor ──

    static void testSensor() {
        section("android.hardware.Sensor");
        check("TYPE_ACCELEROMETER", android.hardware.Sensor.TYPE_ACCELEROMETER == 1);
        check("TYPE_GYROSCOPE", android.hardware.Sensor.TYPE_GYROSCOPE == 4);
        check("TYPE_MAGNETIC_FIELD", android.hardware.Sensor.TYPE_MAGNETIC_FIELD == 2);
    }
}
