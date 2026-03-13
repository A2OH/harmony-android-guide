/**
 * Headless CLI test harness for the Android→OH shim layer.
 *
 * Runs on OHOS via: hdc shell app_process -cp /data/shim/classes.dex / HeadlessTest
 *
 * Tests all non-UI shim APIs:
 * - SharedPreferences (Preferences)
 * - SQLiteDatabase (RdbStore)
 * - Log (HiLog)
 * - Network (connectivity + HTTP)
 * - DeviceInfo (Build)
 * - Toast (mocked in headless mode)
 * - Notification
 * - AlarmManager / Reminder
 *
 * Exit code 0 = all tests pass, non-zero = failure count.
 */
public class HeadlessTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("═══ Android→OH Shim Headless Test ═══\n");

        testLog();
        testDeviceInfo();
        testPreferences();
        testSQLiteDatabase();
        testNetwork();
        testBundle();
        testIntent();
        testUri();
        testContentValues();
        testSize();
        testFileUtils();
        testBase64();
        testSensorDirectChannel();
        testSmsManager();
        testSizeF();
        testCloseGuard();
        testStatsLog();
        testTimeUtils();
        testCountDownTimer();
        testBatteryManager();
        testVibrator();
        testDropBoxManager();
        testInflateException();
        testEventLog();

        System.out.println("\n═══ Results ═══");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println(failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED");
        System.exit(failed);
    }

    // ── Helpers ──

    static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("  ✓ " + name);
            passed++;
        } else {
            System.out.println("  ✗ FAIL: " + name);
            failed++;
        }
    }

    static void section(String name) {
        System.out.println("\n── " + name + " ──");
    }

    // ── Log ──

    static void testLog() {
        section("android.util.Log → HiLog");
        try {
            android.util.Log.d("TEST", "debug message");
            android.util.Log.i("TEST", "info message");
            android.util.Log.w("TEST", "warn message");
            android.util.Log.e("TEST", "error message");
            check("Log.d/i/w/e don't throw", true);
        } catch (Exception e) {
            check("Log.d/i/w/e don't throw", false);
        }

        String trace = android.util.Log.getStackTraceString(new RuntimeException("test"));
        check("getStackTraceString non-empty", trace != null && trace.length() > 0);
    }

    // ── DeviceInfo ──

    static void testDeviceInfo() {
        section("android.os.Build → DeviceInfo NDK");
        String brand = android.os.Build.BRAND;
        String model = android.os.Build.MODEL;
        String version = android.os.Build.VERSION.RELEASE;
        int sdk = android.os.Build.VERSION.SDK_INT;

        System.out.println("    Brand:   " + brand);
        System.out.println("    Model:   " + model);
        System.out.println("    Version: " + version);
        System.out.println("    SDK:     " + sdk);

        check("Build.BRAND non-null", brand != null);
        check("Build.MODEL non-null", model != null);
        check("Build.VERSION.RELEASE non-null", version != null);
        check("Build.VERSION.SDK_INT > 0", sdk > 0);
    }

    // ── Preferences ──

    static void testPreferences() {
        section("SharedPreferences → OH Preferences");
        try {
            android.content.Context ctx = new android.content.Context() {};
            android.content.SharedPreferences prefs = ctx.getSharedPreferences("test_prefs", 0);
            check("getSharedPreferences returns non-null", prefs != null);

            if (prefs == null) return;

            // Write values
            android.content.SharedPreferences.Editor editor = prefs.edit();
            editor.putString("key_str", "hello_ohos");
            editor.putInt("key_int", 42);
            editor.putLong("key_long", 123456789L);
            editor.putFloat("key_float", 3.14f);
            editor.putBoolean("key_bool", true);
            editor.commit();
            check("Editor.commit() succeeds", true);

            // Read back
            check("getString", "hello_ohos".equals(prefs.getString("key_str", "")));
            check("getInt", prefs.getInt("key_int", 0) == 42);
            check("getLong", prefs.getLong("key_long", 0) == 123456789L);
            check("getFloat", Math.abs(prefs.getFloat("key_float", 0) - 3.14f) < 0.01f);
            check("getBoolean", prefs.getBoolean("key_bool", false));

            // Default values for missing keys
            check("getString default", "default".equals(prefs.getString("missing", "default")));
            check("getInt default", prefs.getInt("missing", -1) == -1);

            // Remove
            editor.remove("key_str");
            editor.commit();
            check("remove + getString returns default", "gone".equals(prefs.getString("key_str", "gone")));

            // Clear
            editor.clear();
            editor.commit();
            check("clear + getInt returns default", prefs.getInt("key_int", -1) == -1);

        } catch (Exception e) {
            check("SharedPreferences no exception: " + e.getMessage(), false);
        }
    }

    // ── SQLiteDatabase ──

    static void testSQLiteDatabase() {
        section("SQLiteDatabase → OH RdbStore");
        try {
            android.database.sqlite.SQLiteOpenHelper helper = new android.database.sqlite.SQLiteOpenHelper(
                null, "test.db", null, 1
            ) {
                @Override
                public void onCreate(android.database.sqlite.SQLiteDatabase db) {
                    db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, title TEXT, body TEXT)");
                }
                @Override
                public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVer, int newVer) {}
            };

            android.database.sqlite.SQLiteDatabase db = helper.getWritableDatabase();
            check("getWritableDatabase non-null", db != null);
            if (db == null) return;

            // Insert
            android.content.ContentValues cv = new android.content.ContentValues();
            cv.put("title", "Test Note");
            cv.put("body", "Hello from headless test");
            long rowId = db.insert("notes", null, cv);
            check("insert returns row ID > 0", rowId > 0);

            // Query
            android.database.Cursor cursor = db.rawQuery("SELECT * FROM notes WHERE title = ?",
                new String[]{"Test Note"});
            check("rawQuery returns cursor", cursor != null);

            if (cursor != null) {
                boolean hasRow = cursor.moveToFirst();
                check("cursor.moveToFirst()", hasRow);

                if (hasRow) {
                    int titleIdx = cursor.getColumnIndex("title");
                    int bodyIdx = cursor.getColumnIndex("body");
                    String title = cursor.getString(titleIdx);
                    String body = cursor.getString(bodyIdx);
                    check("title = 'Test Note'", "Test Note".equals(title));
                    check("body = 'Hello from headless test'", "Hello from headless test".equals(body));
                }
                cursor.close();
            }

            // Transaction
            db.beginTransaction();
            try {
                cv.clear();
                cv.put("title", "Note 2");
                cv.put("body", "Transaction test");
                db.insert("notes", null, cv);
                db.setTransactionSuccessful();
                check("transaction commit", true);
            } finally {
                db.endTransaction();
            }

            // Count
            cursor = db.rawQuery("SELECT COUNT(*) FROM notes", null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                check("row count = 2", count == 2);
                cursor.close();
            }

            // Delete
            int deleted = db.delete("notes", "title = ?", new String[]{"Test Note"});
            check("delete returns 1", deleted == 1);

            // Cleanup
            db.execSQL("DROP TABLE notes");
            db.close();
            check("database cleanup", true);

        } catch (Exception e) {
            check("SQLiteDatabase no exception: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    // ── Network ──

    static void testNetwork() {
        section("Network → OH NetConn + libcurl");
        try {
            boolean available = com.ohos.shim.bridge.OHBridge.isNetworkAvailable();
            System.out.println("    Network available: " + available);
            check("isNetworkAvailable doesn't throw", true);

            int netType = com.ohos.shim.bridge.OHBridge.getNetworkType();
            System.out.println("    Network type: " + netType);
            check("getNetworkType doesn't throw", true);

            // HTTP GET (only if network available)
            if (available) {
                String response = com.ohos.shim.bridge.OHBridge.httpRequest(
                    "https://httpbin.org/get", "GET", null, null);
                check("HTTP GET returns response", response != null && response.length() > 0);
                if (response != null) {
                    System.out.println("    Response length: " + response.length());
                }
            } else {
                System.out.println("    (skipping HTTP test — no network)");
            }
        } catch (Exception e) {
            check("Network no exception: " + e.getMessage(), false);
        }
    }

    // ── Bundle (pure Java) ──

    static void testBundle() {
        section("android.os.Bundle (pure Java)");
        android.os.Bundle b = new android.os.Bundle();
        b.putString("name", "test");
        b.putInt("count", 7);
        b.putBoolean("flag", true);
        b.putDouble("pi", 3.14159);

        check("getString", "test".equals(b.getString("name")));
        check("getInt", b.getInt("count") == 7);
        check("getBoolean", b.getBoolean("flag"));
        check("getDouble", Math.abs(b.getDouble("pi") - 3.14159) < 0.0001);
        check("getString default", "def".equals(b.getString("missing", "def")));
        check("size", b.size() == 4);

        b.remove("name");
        check("remove", b.getString("name") == null);
    }

    // ── Intent (pure Java) ──

    static void testIntent() {
        section("android.content.Intent (pure Java)");
        android.content.Intent intent = new android.content.Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("key1", "value1");
        intent.putExtra("key2", 42);

        check("getAction", "android.intent.action.VIEW".equals(intent.getAction()));
        check("getStringExtra", "value1".equals(intent.getStringExtra("key1")));
        check("getIntExtra", intent.getIntExtra("key2", 0) == 42);

        String json = intent.getExtrasJson();
        check("getExtrasJson non-null", json != null && json.contains("key1"));
    }

    // ── Uri (pure Java) ──

    static void testUri() {
        section("android.net.Uri (pure Java)");
        android.net.Uri uri = android.net.Uri.parse("https://example.com/path?q=test&page=1");
        check("getScheme", "https".equals(uri.getScheme()));
        check("getHost", "example.com".equals(uri.getHost()));
        check("getPath", "/path".equals(uri.getPath()));
        check("getQueryParameter q", "test".equals(uri.getQueryParameter("q")));
        check("getQueryParameter page", "1".equals(uri.getQueryParameter("page")));
        check("toString roundtrip", uri.toString().contains("example.com"));
    }

    // ── Size (pure Java) ──

    static void testSize() {
        section("android.util.Size (pure Java)");
        android.util.Size s = new android.util.Size(1920, 1080);
        check("getWidth", s.getWidth() == 1920);
        check("getHeight", s.getHeight() == 1080);
        check("toString", "1920x1080".equals(s.toString()));
        check("equals same", s.equals(new android.util.Size(1920, 1080)));
        check("equals diff", !s.equals(new android.util.Size(1080, 1920)));

        android.util.Size parsed = android.util.Size.parseSize("640x480");
        check("parseSize width", parsed.getWidth() == 640);
        check("parseSize height", parsed.getHeight() == 480);

        boolean threw = false;
        try { android.util.Size.parseSize("invalid"); } catch (NumberFormatException e) { threw = true; }
        check("parseSize invalid throws", threw);
    }

    // ── FileUtils (pure Java) ──

    static void testFileUtils() {
        section("android.os.FileUtils (pure Java)");
        try {
            byte[] data = "Hello OpenHarmony from FileUtils!".getBytes();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            long copied = android.os.FileUtils.copy(in, out);
            check("copy returns byte count", copied == data.length);
            check("copy content matches", java.util.Arrays.equals(data, out.toByteArray()));

            // Empty stream
            in = new java.io.ByteArrayInputStream(new byte[0]);
            out = new java.io.ByteArrayOutputStream();
            copied = android.os.FileUtils.copy(in, out);
            check("copy empty stream", copied == 0);

            // With progress listener
            data = new byte[10000];
            java.util.Arrays.fill(data, (byte) 'X');
            in = new java.io.ByteArrayInputStream(data);
            out = new java.io.ByteArrayOutputStream();
            final long[] lastProgress = {0};
            copied = android.os.FileUtils.copy(in, out, null, null, p -> lastProgress[0] = p);
            check("copy with listener", copied == 10000);
            check("listener called", lastProgress[0] == 10000);
        } catch (Exception e) {
            check("FileUtils no exception: " + e.getMessage(), false);
        }
    }

    // ── Base64 (pure Java) ──

    static void testBase64() {
        section("android.util.Base64 + Base64InputStream (pure Java)");
        try {
            // encode + decode roundtrip (byte[])
            byte[] original = "Hello, OpenHarmony!".getBytes("UTF-8");
            byte[] encoded = android.util.Base64.encode(original, android.util.Base64.DEFAULT);
            byte[] decoded = android.util.Base64.decode(encoded, android.util.Base64.DEFAULT);
            check("encode+decode roundtrip", java.util.Arrays.equals(original, decoded));

            // encodeToString + decode string
            String encodedStr = android.util.Base64.encodeToString(original, android.util.Base64.DEFAULT);
            check("encodeToString non-null", encodedStr != null && encodedStr.length() > 0);
            byte[] decodedFromStr = android.util.Base64.decode(encodedStr, android.util.Base64.DEFAULT);
            check("decode from string", java.util.Arrays.equals(original, decodedFromStr));

            // known value
            byte[] helloDecoded = android.util.Base64.decode("SGVsbG8=", android.util.Base64.DEFAULT);
            check("decode known value", "Hello".equals(new String(helloDecoded, "UTF-8")));

            // Base64InputStream bulk read
            String base64Input = android.util.Base64.encodeToString(
                "Stream test data".getBytes("UTF-8"), android.util.Base64.DEFAULT);
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(
                base64Input.getBytes("UTF-8"));
            android.util.Base64InputStream b64is = new android.util.Base64InputStream(
                bais, android.util.Base64.DEFAULT);
            byte[] buf = new byte[256];
            int totalRead = 0;
            int n;
            while ((n = b64is.read(buf, totalRead, buf.length - totalRead)) != -1) {
                totalRead += n;
            }
            b64is.close();
            String streamResult = new String(buf, 0, totalRead, "UTF-8");
            check("Base64InputStream bulk read", "Stream test data".equals(streamResult));

            // Base64InputStream single-byte read
            String base64Input2 = android.util.Base64.encodeToString(
                "AB".getBytes("UTF-8"), android.util.Base64.DEFAULT);
            java.io.ByteArrayInputStream bais2 = new java.io.ByteArrayInputStream(
                base64Input2.getBytes("UTF-8"));
            android.util.Base64InputStream b64is2 = new android.util.Base64InputStream(
                bais2, android.util.Base64.DEFAULT);
            int b1 = b64is2.read();
            int b2 = b64is2.read();
            int b3 = b64is2.read();
            b64is2.close();
            check("Base64InputStream single read", b1 == 'A' && b2 == 'B' && b3 == -1);

            // NO_PADDING flag
            String noPad = android.util.Base64.encodeToString(
                "Hi".getBytes("UTF-8"), android.util.Base64.NO_PADDING);
            check("NO_PADDING no trailing =", !noPad.contains("="));

            // URL_SAFE roundtrip
            byte[] urlData = new byte[]{(byte)0xFB, (byte)0xFF, (byte)0xFE};
            String urlEncoded = android.util.Base64.encodeToString(urlData, android.util.Base64.URL_SAFE);
            byte[] urlDecoded = android.util.Base64.decode(urlEncoded, android.util.Base64.URL_SAFE);
            check("URL_SAFE roundtrip", java.util.Arrays.equals(urlData, urlDecoded));

        } catch (Exception e) {
            check("Base64 no exception: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    // ── SensorDirectChannel (stub) ──

    static void testSensorDirectChannel() {
        section("android.hardware.SensorDirectChannel (stub)");
        try {
            android.hardware.SensorDirectChannel ch = new android.hardware.SensorDirectChannel();
            check("isValid initially", ch.isValid());

            android.hardware.Sensor sensor = new android.hardware.Sensor("accel", android.hardware.Sensor.TYPE_ACCELEROMETER);
            int token = ch.configure(sensor, android.hardware.SensorDirectChannel.RATE_NORMAL);
            check("configure returns token > 0", token > 0);

            int token2 = ch.configure(sensor, android.hardware.SensorDirectChannel.RATE_FAST);
            check("second configure returns different token", token2 > token);

            ch.close();
            check("isValid after close", !ch.isValid());
            check("configure after close returns 0", ch.configure(sensor, android.hardware.SensorDirectChannel.RATE_NORMAL) == 0);

            // Constants
            check("RATE_STOP", android.hardware.SensorDirectChannel.RATE_STOP == 0);
            check("TYPE_MEMORY_FILE", android.hardware.SensorDirectChannel.TYPE_MEMORY_FILE == 1);
        } catch (Exception e) {
            check("SensorDirectChannel no exception: " + e.getMessage(), false);
        }
    }

    // ── SmsManager (stub) ──

    static void testSmsManager() {
        section("android.telephony.SmsManager (stub)");
        android.telephony.SmsManager sms = android.telephony.SmsManager.getDefault();
        check("getDefault non-null", sms != null);

        // divideMessage: short
        java.util.ArrayList<String> parts = sms.divideMessage("Hello");
        check("divideMessage short = 1 part", parts.size() == 1);

        // divideMessage: long (> 160 chars)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 200; i++) sb.append('A');
        parts = sms.divideMessage(sb.toString());
        check("divideMessage 200 chars = 2 parts", parts.size() == 2);
        check("divideMessage first part = 160", parts.get(0).length() == 160);
        check("divideMessage second part = 40", parts.get(1).length() == 40);

        // sendTextMessage doesn't throw
        try {
            sms.sendTextMessage("+1234567890", null, "Test SMS", null, null);
            check("sendTextMessage no throw", true);
        } catch (Exception e) {
            check("sendTextMessage no throw", false);
        }

        check("getSubscriptionId", sms.getSubscriptionId() == -1);

        android.telephony.SmsManager sub = android.telephony.SmsManager.getSmsManagerForSubscriptionId(5);
        check("getSmsManagerForSubscriptionId", sub.getSubscriptionId() == 5);
    }

    // ── ContentValues (pure Java) ──

    static void testContentValues() {
        section("android.content.ContentValues (pure Java)");
        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put("name", "Alice");
        cv.put("age", 30);
        cv.put("score", 95.5);
        cv.putNull("middle_name");

        check("getAsString", "Alice".equals(cv.getAsString("name")));
        check("getAsInteger", cv.getAsInteger("age") == 30);
        check("getAsDouble", Math.abs(cv.getAsDouble("score") - 95.5) < 0.01);
        check("containsKey", cv.containsKey("middle_name"));
        check("size", cv.size() == 4);

        String json = cv.toJson();
        check("toJson non-null", json != null && json.contains("Alice"));
    }

    // ── SizeF (pure Java) ──

    static void testSizeF() {
        section("android.util.SizeF (pure Java)");
        android.util.SizeF sf = new android.util.SizeF(19.20f, 10.80f);
        check("getWidth", Math.abs(sf.getWidth() - 19.20f) < 0.01f);
        check("getHeight", Math.abs(sf.getHeight() - 10.80f) < 0.01f);
        check("toString", sf.toString().contains("19.2"));
        check("equals same", sf.equals(new android.util.SizeF(19.20f, 10.80f)));
        check("equals diff", !sf.equals(new android.util.SizeF(10.80f, 19.20f)));

        boolean threw = false;
        try { new android.util.SizeF(Float.NaN, 1.0f); } catch (IllegalArgumentException e) { threw = true; }
        check("NaN throws", threw);
    }

    // ── CloseGuard ──

    static void testCloseGuard() {
        section("android.util.CloseGuard");
        android.util.CloseGuard guard = android.util.CloseGuard.get();
        check("get() non-null", guard != null);
        guard.open("close");
        guard.warnIfOpen(); // should print warning (but not throw)
        guard.close();
        check("close+warnIfOpen no crash", true);
    }

    // ── StatsLog ──

    static void testStatsLog() {
        section("android.util.StatsLog (stub)");
        check("logEvent", android.util.StatsLog.logEvent(100));
        check("logStart", android.util.StatsLog.logStart(200));
        check("logStop", android.util.StatsLog.logStop(200));
    }

    // ── TimeUtils ──

    static void testTimeUtils() {
        section("android.util.TimeUtils");
        java.util.TimeZone tz = android.util.TimeUtils.getTimeZone(0, false, System.currentTimeMillis(), "GB");
        check("getTimeZone non-null", tz != null);

        String ver = android.util.TimeUtils.getTimeZoneDatabaseVersion();
        check("getTimeZoneDatabaseVersion non-null", ver != null && !ver.isEmpty());

        String dur = android.util.TimeUtils.formatDuration(3661500);
        check("formatDuration 1h1m1s", dur.contains("1h") && dur.contains("1m") && dur.contains("1s"));
    }

    // ── CountDownTimer ──

    static void testCountDownTimer() {
        section("android.os.CountDownTimer");
        final boolean[] finished = {false};
        final int[] ticks = {0};
        android.os.CountDownTimer timer = new android.os.CountDownTimer(150, 50) {
            @Override public void onTick(long remaining) { ticks[0]++; }
            @Override public void onFinish() { finished[0] = true; }
        };
        timer.start();
        try { Thread.sleep(300); } catch (InterruptedException e) {}
        check("onFinish called", finished[0]);
        check("onTick called", ticks[0] > 0);

        // Test cancel
        final boolean[] finished2 = {false};
        android.os.CountDownTimer timer2 = new android.os.CountDownTimer(500, 100) {
            @Override public void onTick(long remaining) {}
            @Override public void onFinish() { finished2[0] = true; }
        };
        timer2.start();
        timer2.cancel();
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        check("cancel prevents finish", !finished2[0]);
    }

    // ── BatteryManager ──

    static void testBatteryManager() {
        section("android.os.BatteryManager (stub)");
        android.os.BatteryManager bm = new android.os.BatteryManager();
        int capacity = bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY);
        check("getIntProperty capacity", capacity == 75);
        check("isCharging", !bm.isCharging());
        check("computeChargeTimeRemaining", bm.computeChargeTimeRemaining() == -1);
    }

    // ── Vibrator ──

    static void testVibrator() {
        section("android.os.Vibrator (stub)");
        android.os.Vibrator v = new android.os.Vibrator();
        check("hasVibrator", v.hasVibrator());
        try {
            v.vibrate(100);
            v.vibrate(new long[]{0, 100, 50, 100}, -1);
            v.cancel();
            check("vibrate+cancel no throw", true);
        } catch (Exception e) {
            check("vibrate+cancel no throw", false);
        }
    }

    // ── DropBoxManager ──

    static void testDropBoxManager() {
        section("android.os.DropBoxManager");
        android.os.DropBoxManager db = new android.os.DropBoxManager();
        check("isTagEnabled", db.isTagEnabled("test"));
        db.addText("crash", "NullPointerException at line 42");
        android.os.DropBoxManager.Entry entry = db.getNextEntry("crash", 0);
        check("getNextEntry non-null", entry != null);
        check("entry getText", entry != null && "NullPointerException at line 42".equals(entry.getText(1000)));
        check("entry getTag", entry != null && "crash".equals(entry.getTag()));
        check("getNextEntry wrong tag", db.getNextEntry("other", 0) == null);
    }

    // ── InflateException ──

    static void testInflateException() {
        section("android.view.InflateException");
        try {
            throw new android.view.InflateException("test inflate error");
        } catch (android.view.InflateException e) {
            check("message", "test inflate error".equals(e.getMessage()));
        }
        try {
            throw new android.view.InflateException("wrap", new RuntimeException("cause"));
        } catch (android.view.InflateException e) {
            check("cause", e.getCause() instanceof RuntimeException);
        }
    }

    // ── EventLog ──

    static void testEventLog() {
        section("android.util.EventLog");
        android.util.EventLog.writeEvent(1001, 42);
        android.util.EventLog.writeEvent(1002, "hello");
        android.util.EventLog.writeEvent(1001, 99);

        java.util.ArrayList<android.util.EventLog.Event> events = new java.util.ArrayList<>();
        android.util.EventLog.readEvents(new int[]{1001}, events);
        check("readEvents count", events.size() >= 2);
        check("event tag", events.size() > 0 && events.get(0).getTag() == 1001);
        check("event data", events.size() > 0 && Integer.valueOf(42).equals(events.get(0).getData()));
    }
}
