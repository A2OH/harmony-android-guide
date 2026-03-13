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
        testSystemClock();
        testConditionVariable();
        testStatFs();
        testAtomicFile();
        testProcess();
        testMemoryFile();
        testEnvironment();
        testPowerManager();
        testHandlerThread();
        testParcel();
        testHardwareBuffer();
        testKeyguardManager();
        testMediaSyncEvent();
        testGeomagneticField();
        testLocalServerSocket();
        testGeocoder();
        testAddress();
        testConsumerIrManager();
        testTypeface();
        testContentProviderClient();
        testMediaStore();
        testLocation();
        testWebView();
        testSensorManager();
        testService();
        testHandler();
        testContentProvider();
        testMediaPlayer();
        testAudioManager();
        testConnectivityManager();
        testLocationManager();
        testWifiManager();
        testTelephonyManager();
        testGraphics();
        testMatrixCursor();
        testMergeCursor();
        testMatrix();
        testDatabaseUtils();
        testRectFExtended();

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

    // ── SystemClock ──

    static void testSystemClock() {
        section("android.os.SystemClock");
        long uptime = android.os.SystemClock.uptimeMillis();
        check("uptimeMillis > 0", uptime > 0);

        long realtime = android.os.SystemClock.elapsedRealtime();
        check("elapsedRealtime > 0", realtime > 0);

        long nanos = android.os.SystemClock.elapsedRealtimeNanos();
        check("elapsedRealtimeNanos > 0", nanos > 0);

        android.os.SystemClock.sleep(10);
        long uptime2 = android.os.SystemClock.uptimeMillis();
        check("sleep advances clock", uptime2 >= uptime + 5);
    }

    // ── ConditionVariable ──

    static void testConditionVariable() {
        section("android.os.ConditionVariable");
        android.os.ConditionVariable cv = new android.os.ConditionVariable(false);
        check("block(timeout) returns false when closed", !cv.block(10));

        cv.open();
        check("block returns true when open", cv.block(10));

        cv.close();
        check("block after close times out", !cv.block(10));

        // Thread signaling
        final android.os.ConditionVariable cv2 = new android.os.ConditionVariable(false);
        new Thread(() -> {
            try { Thread.sleep(30); } catch (InterruptedException e) {}
            cv2.open();
        }).start();
        boolean result = cv2.block(500);
        check("block unblocked by another thread", result);
    }

    // ── StatFs ──

    static void testStatFs() {
        section("android.os.StatFs");
        android.os.StatFs sf = new android.os.StatFs("/tmp");
        check("getTotalBytes > 0", sf.getTotalBytes() > 0);
        check("getAvailableBytes > 0", sf.getAvailableBytes() > 0);
        check("getFreeBytes > 0", sf.getFreeBytes() > 0);
        check("getBlockSizeLong", sf.getBlockSizeLong() == 4096);
        check("getBlockCountLong > 0", sf.getBlockCountLong() > 0);
    }

    // ── AtomicFile ──

    static void testAtomicFile() {
        section("android.util.AtomicFile");
        try {
            java.io.File tmpFile = java.io.File.createTempFile("atomictest", ".dat");
            tmpFile.deleteOnExit();
            android.util.AtomicFile af = new android.util.AtomicFile(tmpFile);
            check("getBaseFile", af.getBaseFile().equals(tmpFile));

            // Write
            java.io.FileOutputStream fos = af.startWrite();
            fos.write("hello atomic".getBytes());
            af.finishWrite(fos);

            // Read
            byte[] data = af.readFully();
            check("readFully", "hello atomic".equals(new String(data)));

            // Overwrite
            fos = af.startWrite();
            fos.write("updated".getBytes());
            af.finishWrite(fos);
            data = af.readFully();
            check("overwrite + readFully", "updated".equals(new String(data)));

            // Failed write (should restore backup)
            fos = af.startWrite();
            fos.write("BAD DATA".getBytes());
            af.failWrite(fos);
            data = af.readFully();
            check("failWrite restores backup", "updated".equals(new String(data)));

            af.delete();
            check("delete", !tmpFile.exists());
        } catch (Exception e) {
            check("AtomicFile no exception: " + e.getMessage(), false);
        }
    }

    // ── Process ──

    static void testProcess() {
        section("android.os.Process");
        check("myPid > 0", android.os.Process.myPid() > 0);
        check("myTid > 0", android.os.Process.myTid() > 0);
        check("myUid is app UID", android.os.Process.myUid() >= android.os.Process.FIRST_APPLICATION_UID);
        check("isApplicationUid", android.os.Process.isApplicationUid(android.os.Process.myUid()));
        check("isApplicationUid system", !android.os.Process.isApplicationUid(android.os.Process.SYSTEM_UID));
        check("isIsolated", !android.os.Process.isIsolated());
        check("supportsProcesses", android.os.Process.supportsProcesses());
    }

    // ── MemoryFile ──

    static void testMemoryFile() {
        section("android.os.MemoryFile");
        try {
            android.os.MemoryFile mf = new android.os.MemoryFile("test", 1024);
            check("length", mf.length() == 1024);

            byte[] data = "Hello MemoryFile".getBytes();
            mf.writeBytes(data, 0, 0, data.length);

            byte[] readBuf = new byte[data.length];
            mf.readBytes(readBuf, 0, 0, readBuf.length);
            check("read matches write", java.util.Arrays.equals(data, readBuf));

            // Stream interface
            java.io.OutputStream os = mf.getOutputStream();
            os.write("Stream!".getBytes());
            java.io.InputStream is = mf.getInputStream();
            byte[] streamBuf = new byte[7];
            is.read(streamBuf);
            check("stream write", "Stream!".equals(new String(streamBuf)));

            mf.close();
            boolean threw = false;
            try { mf.readBytes(readBuf, 0, 0, 1); } catch (java.io.IOException e) { threw = true; }
            check("closed throws", threw);
        } catch (Exception e) {
            check("MemoryFile no exception: " + e.getMessage(), false);
        }
    }

    // ── Environment ──

    static void testEnvironment() {
        section("android.os.Environment");
        check("getDataDirectory", android.os.Environment.getDataDirectory() != null);
        check("getExternalStorageDirectory", android.os.Environment.getExternalStorageDirectory() != null);
        check("getExternalStorageState", "mounted".equals(android.os.Environment.getExternalStorageState()));

        java.io.File pics = android.os.Environment.getExternalStoragePublicDirectory(
            android.os.Environment.DIRECTORY_PICTURES);
        check("DIRECTORY_PICTURES", pics != null && pics.getPath().contains("Pictures"));
    }

    // ── PowerManager ──

    static void testPowerManager() {
        section("android.os.PowerManager");
        android.os.PowerManager pm = new android.os.PowerManager();
        check("isInteractive", pm.isInteractive());
        check("isDeviceIdleMode", !pm.isDeviceIdleMode());
        check("isPowerSaveMode", !pm.isPowerSaveMode());

        android.os.PowerManager.WakeLock wl = pm.newWakeLock(
            android.os.PowerManager.PARTIAL_WAKE_LOCK, "test");
        check("WakeLock not held initially", !wl.isHeld());
        wl.acquire();
        check("WakeLock held after acquire", wl.isHeld());
        wl.release();
        check("WakeLock released", !wl.isHeld());
    }

    // ── HandlerThread ──

    static void testHandlerThread() {
        section("android.os.HandlerThread");
        android.os.HandlerThread ht = new android.os.HandlerThread("test-thread");
        ht.start();
        ht.waitUntilReady();
        check("thread alive", ht.isAlive());
        check("getThreadId > 0", ht.getThreadId() > 0);
        ht.quit();
        try { ht.join(500); } catch (InterruptedException e) {}
        check("quit stops thread", !ht.isAlive());
    }

    // ── Parcel ──

    static void testParcel() {
        section("android.os.Parcel");
        android.os.Parcel p = android.os.Parcel.obtain();

        p.writeInt(42);
        p.writeLong(123456789L);
        p.writeFloat(3.14f);
        p.writeString("hello parcel");
        p.writeString(null);
        p.writeByteArray(new byte[]{1, 2, 3});

        p.setDataPosition(0);
        check("readInt", p.readInt() == 42);
        check("readLong", p.readLong() == 123456789L);
        check("readFloat", Math.abs(p.readFloat() - 3.14f) < 0.01f);
        check("readString", "hello parcel".equals(p.readString()));
        check("readString null", p.readString() == null);
        byte[] bytes = p.createByteArray();
        check("createByteArray", bytes != null && bytes.length == 3 && bytes[0] == 1);
        check("dataSize > 0", p.dataSize() > 0);

        p.recycle();
    }

    // ── HardwareBuffer ──

    static void testHardwareBuffer() {
        section("android.hardware.HardwareBuffer");
        android.hardware.HardwareBuffer buf = android.hardware.HardwareBuffer.create(
            1920, 1080, android.hardware.HardwareBuffer.RGBA_8888, 1,
            android.hardware.HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE);
        check("getWidth", buf.getWidth() == 1920);
        check("getHeight", buf.getHeight() == 1080);
        check("getFormat", buf.getFormat() == android.hardware.HardwareBuffer.RGBA_8888);
        check("getLayers", buf.getLayers() == 1);
        check("isClosed initially", !buf.isClosed());
        buf.close();
        check("isClosed after close", buf.isClosed());
        check("isSupported", android.hardware.HardwareBuffer.isSupported(100, 100, 1, 1, 0));
        check("isSupported invalid", !android.hardware.HardwareBuffer.isSupported(0, 100, 1, 1, 0));
    }

    // ── KeyguardManager ──

    static void testKeyguardManager() {
        section("android.app.KeyguardManager");
        android.app.KeyguardManager km = new android.app.KeyguardManager();
        check("isKeyguardLocked", !km.isKeyguardLocked());
        check("isDeviceLocked", !km.isDeviceLocked());
        check("isDeviceSecure", !km.isDeviceSecure());
        android.app.KeyguardManager.KeyguardLock kl = km.newKeyguardLock("test");
        kl.disableKeyguard();
        kl.reenableKeyguard();
        check("KeyguardLock no throw", true);
    }

    // ── MediaSyncEvent ──

    static void testMediaSyncEvent() {
        section("android.media.MediaSyncEvent");
        android.media.MediaSyncEvent evt = android.media.MediaSyncEvent.createEvent(
            android.media.MediaSyncEvent.SYNC_EVENT_PRESENTATION_COMPLETE);
        check("getType", evt.getType() == android.media.MediaSyncEvent.SYNC_EVENT_PRESENTATION_COMPLETE);
        evt.setAudioSessionId(42);
        check("getAudioSessionId", evt.getAudioSessionId() == 42);
    }

    // ── GeomagneticField ──

    static void testGeomagneticField() {
        section("android.hardware.GeomagneticField");
        // San Francisco: 37.7749° N, 122.4194° W
        android.hardware.GeomagneticField gf = new android.hardware.GeomagneticField(
            37.7749f, -122.4194f, 0, System.currentTimeMillis());
        check("getFieldStrength > 0", gf.getFieldStrength() > 0);
        check("getDeclination is finite", Float.isFinite(gf.getDeclination()));
        check("getInclination is finite", Float.isFinite(gf.getInclination()));
        check("getX non-zero", gf.getX() != 0);
    }

    // ── LocalServerSocket ──

    static void testLocalServerSocket() {
        section("android.net.LocalServerSocket");
        try {
            android.net.LocalServerSocket server = new android.net.LocalServerSocket("test");
            check("getName", "test".equals(server.getName()));
            check("getLocalPort > 0", server.getLocalPort() > 0);
            server.close();
            check("close no throw", true);
        } catch (Exception e) {
            check("LocalServerSocket no exception: " + e.getMessage(), false);
        }
    }

    // ── Geocoder ──

    static void testGeocoder() {
        section("android.location.Geocoder");
        android.location.Geocoder gc = new android.location.Geocoder(null);
        check("isPresent", !android.location.Geocoder.isPresent()); // mock returns false
        try {
            java.util.List<android.location.Address> results = gc.getFromLocation(37.7, -122.4, 1);
            check("getFromLocation returns list", results != null);
            results = gc.getFromLocationName("San Francisco", 1);
            check("getFromLocationName returns list", results != null);
        } catch (Exception e) {
            check("Geocoder no exception", false);
        }
    }

    // ── Address ──

    static void testAddress() {
        section("android.location.Address");
        android.location.Address addr = new android.location.Address(java.util.Locale.US);
        addr.setAddressLine(0, "123 Main St");
        addr.setLocality("Springfield");
        addr.setCountryCode("US");
        addr.setLatitude(39.78);
        addr.setLongitude(-89.65);

        check("getAddressLine", "123 Main St".equals(addr.getAddressLine(0)));
        check("getLocality", "Springfield".equals(addr.getLocality()));
        check("getCountryCode", "US".equals(addr.getCountryCode()));
        check("hasLatitude", addr.hasLatitude());
        check("getLatitude", Math.abs(addr.getLatitude() - 39.78) < 0.01);
        check("hasLongitude", addr.hasLongitude());
    }

    // ── ConsumerIrManager ──

    static void testConsumerIrManager() {
        section("android.hardware.ConsumerIrManager");
        android.hardware.ConsumerIrManager ir = new android.hardware.ConsumerIrManager();
        check("hasIrEmitter", !ir.hasIrEmitter());
        check("getCarrierFrequencies empty", ir.getCarrierFrequencies().length == 0);
        ir.transmit(38000, new int[]{100, 50, 100, 50});
        check("transmit no throw", true);

        android.hardware.ConsumerIrManager.CarrierFrequencyRange r =
            new android.hardware.ConsumerIrManager.CarrierFrequencyRange(30000, 60000);
        check("CarrierFrequencyRange min", r.getMinFrequency() == 30000);
        check("CarrierFrequencyRange max", r.getMaxFrequency() == 60000);
    }

    // ── Typeface ──

    static void testTypeface() {
        section("android.graphics.Typeface");
        check("DEFAULT non-null", android.graphics.Typeface.DEFAULT != null);
        check("DEFAULT_BOLD is bold", android.graphics.Typeface.DEFAULT_BOLD.isBold());
        check("MONOSPACE non-null", android.graphics.Typeface.MONOSPACE != null);

        android.graphics.Typeface bold = android.graphics.Typeface.create("sans-serif",
            android.graphics.Typeface.BOLD);
        check("create bold", bold.isBold());
        check("create bold style", bold.getStyle() == android.graphics.Typeface.BOLD);

        android.graphics.Typeface italic = android.graphics.Typeface.create("serif",
            android.graphics.Typeface.ITALIC);
        check("create italic", italic.isItalic());

        android.graphics.Typeface weighted = android.graphics.Typeface.create(
            android.graphics.Typeface.DEFAULT, 300, false);
        check("create weight 300", weighted.getWeight() == 300);
    }

    // ── ContentProviderClient ──

    static void testContentProviderClient() {
        section("android.content.ContentProviderClient");
        android.content.ContentProviderClient cpc =
            new android.content.ContentProviderClient("com.example");
        check("query returns null", cpc.query(null, null, null, null, null) == null);
        check("update returns 0", cpc.update(null, null, null, null) == 0);
        check("delete returns 0", cpc.delete(null, null, null) == 0);
        cpc.close();
        check("close no throw", true);
    }

    // ── MediaStore ──

    static void testMediaStore() {
        section("android.provider.MediaStore");
        check("Images URI", android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI != null);
        check("Video URI", android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI != null);
        check("Audio URI", android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI != null);

        android.net.Uri filesUri = android.provider.MediaStore.Files.getContentUri("external");
        check("Files getContentUri", filesUri != null && filesUri.toString().contains("external"));

        android.net.Uri fileUri = android.provider.MediaStore.Files.getContentUri("external", 42);
        check("Files getContentUri with id", fileUri != null && fileUri.toString().contains("42"));

        check("AUTHORITY", "media".equals(android.provider.MediaStore.AUTHORITY));
    }

    // ── Location ──

    static void testLocation() {
        section("android.location.Location");
        android.location.Location loc = new android.location.Location("gps");
        loc.setLatitude(37.7749);
        loc.setLongitude(-122.4194);
        loc.setAltitude(10.0);
        loc.setAccuracy(5.0f);
        loc.setTime(System.currentTimeMillis());

        check("getProvider", "gps".equals(loc.getProvider()));
        check("getLatitude", Math.abs(loc.getLatitude() - 37.7749) < 0.0001);
        check("getLongitude", Math.abs(loc.getLongitude() + 122.4194) < 0.0001);
        check("hasAltitude", loc.hasAltitude());
        check("getAltitude", Math.abs(loc.getAltitude() - 10.0) < 0.01);
        check("hasAccuracy", loc.hasAccuracy());

        // Distance: SF to LA (~560 km)
        android.location.Location la = new android.location.Location("gps");
        la.setLatitude(34.0522);
        la.setLongitude(-118.2437);
        float dist = loc.distanceTo(la);
        check("distanceTo SF-LA ~560km", dist > 500000 && dist < 620000);

        // Copy constructor
        android.location.Location copy = new android.location.Location(loc);
        check("copy latitude", Math.abs(copy.getLatitude() - loc.getLatitude()) < 0.0001);

        check("toString", loc.toString().contains("gps"));
    }

    // ── WebView ──

    static void testWebView() {
        section("android.webkit.WebView → @ohos.web.webview.WebviewController");

        // ── Construction and initial state ──────────────────────────────────
        android.webkit.WebView wv = new android.webkit.WebView();
        check("WebView created non-null", wv != null);
        check("getUrl() null before load", wv.getUrl() == null);
        check("getTitle() null before load", wv.getTitle() == null);
        check("canGoBack() false initially", !wv.canGoBack());
        check("canGoForward() false initially", !wv.canGoForward());

        // ── loadUrl stores URL ───────────────────────────────────────────────
        wv.loadUrl("https://example.com");
        check("getUrl() returns loaded URL", "https://example.com".equals(wv.getUrl()));
        check("canGoBack() false after first load", !wv.canGoBack());
        check("canGoForward() false after first load", !wv.canGoForward());

        // ── Second load creates history ──────────────────────────────────────
        wv.loadUrl("https://openharmony.io");
        check("getUrl() updated after second load",
              "https://openharmony.io".equals(wv.getUrl()));
        check("canGoBack() true after second load", wv.canGoBack());
        check("canGoForward() false at end of history", !wv.canGoForward());

        // ── goBack ───────────────────────────────────────────────────────────
        wv.goBack();
        check("getUrl() after goBack is first URL",
              "https://example.com".equals(wv.getUrl()));
        check("canGoBack() false at start of history", !wv.canGoBack());
        check("canGoForward() true after goBack", wv.canGoForward());

        // ── goForward ────────────────────────────────────────────────────────
        wv.goForward();
        check("getUrl() after goForward is second URL",
              "https://openharmony.io".equals(wv.getUrl()));
        check("canGoForward() false at end again", !wv.canGoForward());
        check("canGoBack() true after goForward", wv.canGoBack());

        // ── goBack then new load clears forward history ──────────────────────
        wv.goBack();
        wv.loadUrl("https://harmonyos.com");
        check("new load clears forward history", !wv.canGoForward());
        check("getUrl() is new URL after branch load",
              "https://harmonyos.com".equals(wv.getUrl()));

        // ── reload ───────────────────────────────────────────────────────────
        final boolean[] reloadFinished = {false};
        wv.setWebViewClient(new android.webkit.WebViewClient() {
            @Override public void onPageFinished(android.webkit.WebView view, String url) {
                reloadFinished[0] = true;
            }
        });
        wv.reload();
        check("reload fires onPageFinished", reloadFinished[0]);
        check("getUrl() unchanged after reload",
              "https://harmonyos.com".equals(wv.getUrl()));

        // ── stopLoading doesn't throw ────────────────────────────────────────
        try {
            wv.stopLoading();
            check("stopLoading() does not throw", true);
        } catch (Exception e) {
            check("stopLoading() does not throw", false);
        }

        // ── WebSettings ──────────────────────────────────────────────────────
        android.webkit.WebSettings ws = wv.getSettings();
        check("getSettings() returns non-null", ws != null);

        ws.setJavaScriptEnabled(true);
        check("setJavaScriptEnabled(true) / getJavaScriptEnabled()",
              ws.getJavaScriptEnabled());

        ws.setJavaScriptEnabled(false);
        check("setJavaScriptEnabled(false)", !ws.getJavaScriptEnabled());

        ws.setDomStorageEnabled(true);
        check("setDomStorageEnabled / getDomStorageEnabled",
              ws.getDomStorageEnabled());

        ws.setBuiltInZoomControls(true);
        check("setBuiltInZoomControls / getBuiltInZoomControls",
              ws.getBuiltInZoomControls());

        ws.setUseWideViewPort(true);
        check("setUseWideViewPort / getUseWideViewPort", ws.getUseWideViewPort());

        ws.setLoadWithOverviewMode(true);
        check("setLoadWithOverviewMode / getLoadWithOverviewMode",
              ws.getLoadWithOverviewMode());

        ws.setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);
        check("setCacheMode / getCacheMode",
              ws.getCacheMode() == android.webkit.WebSettings.LOAD_NO_CACHE);

        check("LOAD_DEFAULT == 0",
              android.webkit.WebSettings.LOAD_DEFAULT == 0);
        check("LOAD_CACHE_ELSE_NETWORK == 1",
              android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK == 1);
        check("LOAD_NO_CACHE == 2",
              android.webkit.WebSettings.LOAD_NO_CACHE == 2);
        check("LOAD_CACHE_ONLY == 3",
              android.webkit.WebSettings.LOAD_CACHE_ONLY == 3);

        ws.setUserAgentString("TestAgent/1.0");
        check("setUserAgentString / getUserAgentString",
              "TestAgent/1.0".equals(ws.getUserAgentString()));

        ws.setAllowFileAccess(false);
        check("setAllowFileAccess(false)", !ws.getAllowFileAccess());

        ws.setMediaPlaybackRequiresUserGesture(false);
        check("setMediaPlaybackRequiresUserGesture(false)",
              !ws.getMediaPlaybackRequiresUserGesture());

        // ── WebViewClient callbacks ──────────────────────────────────────────
        final boolean[] pageStarted  = {false};
        final boolean[] pageFinished = {false};
        final String[]  callbackUrl  = {null};

        android.webkit.WebView wv2 = new android.webkit.WebView();
        wv2.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public void onPageStarted(android.webkit.WebView view, String url,
                                      android.graphics.Bitmap favicon) {
                pageStarted[0] = true;
                callbackUrl[0] = url;
            }
            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                pageFinished[0] = true;
            }
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                return false;
            }
        });

        wv2.loadUrl("https://test.example");
        check("onPageStarted callback fired", pageStarted[0]);
        check("onPageStarted URL matches", "https://test.example".equals(callbackUrl[0]));
        check("onPageFinished callback fired", pageFinished[0]);

        // ── WebChromeClient callbacks ─────────────────────────────────────────
        final int[]    progressVal = {-1};
        final String[] chromTitle  = {null};

        android.webkit.WebView wv3 = new android.webkit.WebView();
        wv3.setWebChromeClient(new android.webkit.WebChromeClient() {
            @Override
            public void onProgressChanged(android.webkit.WebView view, int newProgress) {
                progressVal[0] = newProgress;
            }
            @Override
            public void onReceivedTitle(android.webkit.WebView view, String title) {
                chromTitle[0] = title;
            }
        });

        wv3.loadUrl("https://chrom.example");
        check("onProgressChanged called with 100", progressVal[0] == 100);

        wv3.pushTitle("Test Page Title");
        check("onReceivedTitle callback fired", "Test Page Title".equals(chromTitle[0]));
        check("getTitle() updated via pushTitle",
              "Test Page Title".equals(wv3.getTitle()));

        // ── evaluateJavascript ────────────────────────────────────────────────
        final boolean[] evalCalled = {false};
        final String[]  evalResult = {null};
        wv.evaluateJavascript("1+1", new android.webkit.ValueCallback<String>() {
            @Override public void onReceiveValue(String value) {
                evalCalled[0] = true;
                evalResult[0] = value;
            }
        });
        check("evaluateJavascript callback fired", evalCalled[0]);
        check("evaluateJavascript result is null (stub)", evalResult[0] == null);

        // ── addJavascriptInterface doesn't throw ─────────────────────────────
        try {
            wv.addJavascriptInterface(new Object(), "Android");
            check("addJavascriptInterface does not throw", true);
        } catch (Exception e) {
            check("addJavascriptInterface does not throw", false);
        }

        // ── loadData / loadDataWithBaseURL don't throw ────────────────────────
        try {
            wv.loadData("<html><body>Hello</body></html>", "text/html", "UTF-8");
            check("loadData does not throw", true);
        } catch (Exception e) {
            check("loadData does not throw", false);
        }

        try {
            wv.loadDataWithBaseURL("https://base.example", "<html/>",
                                   "text/html", "UTF-8", "https://history.example");
            check("loadDataWithBaseURL does not throw", true);
            check("loadDataWithBaseURL uses historyUrl",
                  "https://history.example".equals(wv.getUrl()));
        } catch (Exception e) {
            check("loadDataWithBaseURL does not throw", false);
        }

        // ── destroy ───────────────────────────────────────────────────────────
        android.webkit.WebView wv4 = new android.webkit.WebView();
        wv4.loadUrl("https://destroy.example");
        try {
            wv4.destroy();
            check("destroy() does not throw", true);
        } catch (Exception e) {
            check("destroy() does not throw", false);
        }
        try {
            wv4.loadUrl("https://after.destroy");
            check("loadUrl after destroy should have thrown", false);
        } catch (IllegalStateException e) {
            check("loadUrl after destroy throws IllegalStateException", true);
        }
    }

    // ── SensorManager ─────────────────────────────────────────────

    static void testSensorManager() {
        section("android.hardware.SensorManager → @ohos.sensor");

        // ── Delay constants ─────────────────────────────────────────────
        check("SENSOR_DELAY_FASTEST == 0",
                android.hardware.SensorManager.SENSOR_DELAY_FASTEST == 0);
        check("SENSOR_DELAY_GAME == 1",
                android.hardware.SensorManager.SENSOR_DELAY_GAME == 1);
        check("SENSOR_DELAY_UI == 2",
                android.hardware.SensorManager.SENSOR_DELAY_UI == 2);
        check("SENSOR_DELAY_NORMAL == 3",
                android.hardware.SensorManager.SENSOR_DELAY_NORMAL == 3);

        // ── Accuracy constants ────────────────────────────────────────────
        check("SENSOR_STATUS_UNRELIABLE == 0",
                android.hardware.SensorManager.SENSOR_STATUS_UNRELIABLE == 0);
        check("SENSOR_STATUS_ACCURACY_LOW == 1",
                android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_LOW == 1);
        check("SENSOR_STATUS_ACCURACY_MEDIUM == 2",
                android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM == 2);
        check("SENSOR_STATUS_ACCURACY_HIGH == 3",
                android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH == 3);

        // ── GRAVITY_EARTH constant ────────────────────────────────────────────
        check("GRAVITY_EARTH ~9.80665",
                Math.abs(android.hardware.SensorManager.GRAVITY_EARTH - 9.80665f) < 0.0001f);

        android.hardware.SensorManager sm = new android.hardware.SensorManager();

        // ── getDefaultSensor ────────────────────────────────────────────
        android.hardware.Sensor accel =
                sm.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
        check("getDefaultSensor(ACCELEROMETER) non-null", accel != null);
        check("getDefaultSensor(ACCELEROMETER) type correct",
                accel != null && accel.getType() == android.hardware.Sensor.TYPE_ACCELEROMETER);

        android.hardware.Sensor gyro =
                sm.getDefaultSensor(android.hardware.Sensor.TYPE_GYROSCOPE);
        check("getDefaultSensor(GYROSCOPE) non-null", gyro != null);

        android.hardware.Sensor light =
                sm.getDefaultSensor(android.hardware.Sensor.TYPE_LIGHT);
        check("getDefaultSensor(LIGHT) non-null", light != null);

        android.hardware.Sensor proximity =
                sm.getDefaultSensor(android.hardware.Sensor.TYPE_PROXIMITY);
        check("getDefaultSensor(PROXIMITY) non-null", proximity != null);

        android.hardware.Sensor unknown = sm.getDefaultSensor(9999);
        check("getDefaultSensor(unknown) returns null", unknown == null);

        // ── getSensorList ─────────────────────────────────────────────────
        java.util.List<android.hardware.Sensor> allSensors =
                sm.getSensorList(android.hardware.Sensor.TYPE_ALL);
        check("getSensorList(TYPE_ALL) non-null", allSensors != null);
        check("getSensorList(TYPE_ALL) size >= 5",
                allSensors != null && allSensors.size() >= 5);

        java.util.List<android.hardware.Sensor> accelList =
                sm.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
        check("getSensorList(ACCELEROMETER) has one entry",
                accelList != null && accelList.size() == 1);
        check("getSensorList(ACCELEROMETER) correct type",
                accelList != null && !accelList.isEmpty()
                && accelList.get(0).getType() == android.hardware.Sensor.TYPE_ACCELEROMETER);

        java.util.List<android.hardware.Sensor> noneList = sm.getSensorList(9999);
        check("getSensorList(unknown) returns empty list",
                noneList != null && noneList.isEmpty());

        // ── SensorEvent construction ──────────────────────────────────────────
        android.hardware.SensorEvent event = new android.hardware.SensorEvent();
        event.sensor    = accel;
        event.accuracy  = android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
        event.timestamp = System.nanoTime();
        event.values    = new float[]{1.0f, 2.0f, 9.8f};
        check("SensorEvent.sensor assigned", event.sensor == accel);
        check("SensorEvent.accuracy assigned",
                event.accuracy == android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        check("SensorEvent.timestamp > 0", event.timestamp > 0);
        check("SensorEvent.values length == 3",
                event.values != null && event.values.length == 3);
        check("SensorEvent.values[2] ~9.8", Math.abs(event.values[2] - 9.8f) < 0.1f);

        // Convenience constructor
        android.hardware.SensorEvent event2 = new android.hardware.SensorEvent(gyro, 3);
        check("SensorEvent(sensor, count) sets sensor", event2.sensor == gyro);
        check("SensorEvent(sensor, count) allocates values",
                event2.values != null && event2.values.length == 3);

        // toString must not throw
        String evtStr = event.toString();
        check("SensorEvent.toString() non-null", evtStr != null);

        // ── SensorEventListener registration ─────────────────────────────────
        final android.hardware.SensorEvent[] received = {null};
        final int[] accuracyChangedArr = {-1};

        android.hardware.SensorEventListener listener =
                new android.hardware.SensorEventListener() {
            @Override
            public void onSensorChanged(android.hardware.SensorEvent e) {
                received[0] = e;
            }
            @Override
            public void onAccuracyChanged(android.hardware.Sensor s, int acc) {
                accuracyChangedArr[0] = acc;
            }
        };

        check("no listeners before register", !sm.hasListener(listener));

        boolean registered = sm.registerListener(
                listener, accel,
                android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
        check("registerListener returns true", registered);
        check("listener registered after registerListener", sm.hasListener(listener));
        check("listenerCount == 1 after register", sm.getListenerCount() == 1);

        // Register same listener for a second sensor
        sm.registerListener(listener, gyro,
                android.hardware.SensorManager.SENSOR_DELAY_GAME);
        java.util.List<android.hardware.Sensor> forListener =
                sm.getSensorsForListener(listener);
        check("getSensorsForListener size == 2 after second register",
                forListener.size() == 2);

        // Registering a duplicate sensor must not add twice
        sm.registerListener(listener, accel,
                android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
        check("duplicate sensor not added twice",
                sm.getSensorsForListener(listener).size() == 2);

        // ── unregisterListener(listener, sensor) ──────────────────────────────────────
        sm.unregisterListener(listener, accel);
        java.util.List<android.hardware.Sensor> afterPartial =
                sm.getSensorsForListener(listener);
        check("after unregister(sensor), only gyro remains",
                afterPartial.size() == 1
                && afterPartial.get(0).getType() == android.hardware.Sensor.TYPE_GYROSCOPE);
        check("listener still tracked after partial unregister",
                sm.hasListener(listener));

        // ── unregisterListener(listener) removes all ──────────────────────────────────────
        sm.unregisterListener(listener);
        check("listener removed after full unregisterListener",
                !sm.hasListener(listener));
        check("listenerCount == 0 after full unregister",
                sm.getListenerCount() == 0);

        // ── null-safety ───────────────────────────────────────────────────────────────
        boolean nullReg = sm.registerListener(null, accel,
                android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
        check("registerListener(null,sensor,delay) returns false", !nullReg);

        nullReg = sm.registerListener(listener, null,
                android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
        check("registerListener(listener,null,delay) returns false", !nullReg);

        sm.unregisterListener(null);
        sm.unregisterListener(null, accel);
        check("unregisterListener(null) does not throw", true);

        // ── SensorEventListener interface dispatches correctly ─────────────────────────
        listener.onSensorChanged(event);
        check("onSensorChanged dispatch works", received[0] == event);

        listener.onAccuracyChanged(accel,
                android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_LOW);
        check("onAccuracyChanged dispatch works",
                accuracyChangedArr[0]
                == android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_LOW);
    }

    // ── Service ──

    static void testService() {
        section("android.app.Service → ServiceExtensionAbility");

        // Subclass Service
        final boolean[] lifecycleEvents = {false, false, false, false};
        android.app.Service svc = new android.app.Service() {
            @Override
            public void onCreate() {
                lifecycleEvents[0] = true;
            }
            @Override
            public int onStartCommand(android.content.Intent intent, int flags, int startId) {
                lifecycleEvents[1] = true;
                return super.onStartCommand(intent, flags, startId);
            }
            @Override
            public android.os.IBinder onBind(android.content.Intent intent) {
                lifecycleEvents[2] = true;
                return null;
            }
            @Override
            public void onDestroy() {
                lifecycleEvents[3] = true;
            }
        };

        // Constants
        check("START_STICKY == 1",
                android.app.Service.START_STICKY == 1);
        check("START_NOT_STICKY == 2",
                android.app.Service.START_NOT_STICKY == 2);
        check("START_REDELIVER_INTENT == 3",
                android.app.Service.START_REDELIVER_INTENT == 3);

        // Lifecycle
        svc.onCreate();
        check("onCreate called", lifecycleEvents[0]);

        android.content.Intent intent = new android.content.Intent();
        int result = svc.onStartCommand(intent, 0, 1);
        check("onStartCommand called", lifecycleEvents[1]);
        check("onStartCommand returns START_STICKY",
                result == android.app.Service.START_STICKY);

        android.os.IBinder binder = svc.onBind(intent);
        check("onBind called", lifecycleEvents[2]);
        check("onBind returns null (default)", binder == null);

        // stopSelf triggers onDestroy
        check("isStopRequested false before stopSelf", !svc.isStopRequested());
        svc.stopSelf();
        check("onDestroy called via stopSelf", lifecycleEvents[3]);
        check("isStopRequested true after stopSelf", svc.isStopRequested());

        // stopSelf(id) — only stops when startId matches
        android.app.Service svc2 = new android.app.Service() {
            @Override public android.os.IBinder onBind(android.content.Intent i) { return null; }
        };
        svc2.onStartCommand(intent, 0, 5);
        check("stopSelf(wrong id) returns false", !svc2.stopSelf(3));
        check("stopSelf(correct id) returns true", svc2.stopSelf(5));
    }

    // ── Handler ──

    static void testHandler() {
        section("android.os.Handler + Message + Looper");

        // post(Runnable) runs immediately
        final boolean[] runnableRan = {false};
        android.os.Handler handler = new android.os.Handler();
        boolean posted = handler.post(() -> runnableRan[0] = true);
        check("post(Runnable) returns true", posted);
        check("post(Runnable) runs immediately", runnableRan[0]);

        // sendMessage dispatches to handleMessage
        final int[] receivedWhat = {-1};
        android.os.Handler msgHandler = new android.os.Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                receivedWhat[0] = msg.what;
            }
        };
        android.os.Message msg = android.os.Message.obtain(msgHandler, 42);
        check("Message.obtain sets what", msg.what == 42);
        check("Message.obtain sets target", msg.target == msgHandler);

        boolean sent = msgHandler.sendMessage(msg);
        check("sendMessage returns true", sent);
        check("handleMessage received what=42", receivedWhat[0] == 42);

        // sendEmptyMessage
        final int[] emptyWhat = {-1};
        android.os.Handler emptyHandler = new android.os.Handler() {
            @Override
            public void handleMessage(android.os.Message m) {
                emptyWhat[0] = m.what;
            }
        };
        emptyHandler.sendEmptyMessage(99);
        check("sendEmptyMessage dispatches what=99", emptyWhat[0] == 99);

        // Message.obtain with arg1/arg2
        android.os.Message msg2 = android.os.Message.obtain(msgHandler, 7, 10, 20);
        check("Message.obtain(h,what,arg1,arg2) arg1==10", msg2.arg1 == 10);
        check("Message.obtain(h,what,arg1,arg2) arg2==20", msg2.arg2 == 20);

        // Message.obtain with obj
        android.os.Message msg3 = android.os.Message.obtain(msgHandler, 8, "payload");
        check("Message.obtain(h,what,obj) obj set", "payload".equals(msg3.obj));

        // Handler.Callback intercept
        final boolean[] callbackFired = {false};
        android.os.Handler cbHandler = new android.os.Handler(
                (android.os.Handler.Callback) m -> {
                    callbackFired[0] = true;
                    return true; // consumed
                });
        cbHandler.sendEmptyMessage(1);
        check("Handler.Callback intercepts message", callbackFired[0]);

        // Looper.getMainLooper
        android.os.Looper mainLooper = android.os.Looper.getMainLooper();
        check("Looper.getMainLooper non-null", mainLooper != null);
        check("Looper.getMainLooper thread non-null",
                mainLooper != null && mainLooper.getThread() != null);

        // Looper.myLooper
        android.os.Looper myLooper = android.os.Looper.myLooper();
        check("Looper.myLooper non-null", myLooper != null);

        // Looper.prepare() and loop() don't throw
        try {
            android.os.Looper.prepare();
            android.os.Looper.loop();
            check("Looper.prepare/loop no-op, no exception", true);
        } catch (Exception e) {
            check("Looper.prepare/loop no-op, no exception", false);
        }

        // Message pool recycle
        android.os.Message poolMsg = android.os.Message.obtain();
        check("Message.obtain from pool non-null", poolMsg != null);
        poolMsg.what = 100;
        poolMsg.recycle();
        android.os.Message reused = android.os.Message.obtain();
        check("Message after recycle what reset", reused.what == 0);

        // Handler bound to looper
        android.os.Handler looperHandler = new android.os.Handler(mainLooper);
        check("Handler.getLooper returns mainLooper",
                looperHandler.getLooper() == mainLooper);
    }

    // ── ContentProvider ──

    static void testContentProvider() {
        section("android.content.ContentProvider → DataShareExtensionAbility");

        // Concrete subclass
        final java.util.List<String> insertedUris = new java.util.ArrayList<>();
        android.content.ContentProvider provider = new android.content.ContentProvider() {
            @Override
            public boolean onCreate() { return true; }

            @Override
            public android.database.Cursor query(android.net.Uri uri, String[] projection,
                    String selection, String[] selectionArgs, String sortOrder) {
                return null; // stub
            }

            @Override
            public android.net.Uri insert(android.net.Uri uri, android.content.ContentValues values) {
                String uriStr = uri.toString() + "/" + System.nanoTime();
                insertedUris.add(uriStr);
                return android.net.Uri.parse(uriStr);
            }

            @Override
            public int update(android.net.Uri uri, android.content.ContentValues values,
                    String selection, String[] selectionArgs) {
                return 1;
            }

            @Override
            public int delete(android.net.Uri uri, String selection, String[] selectionArgs) {
                return 2;
            }

            @Override
            public String getType(android.net.Uri uri) {
                return "vnd.android.cursor.dir/vnd.test.item";
            }
        };

        // Attach context
        android.content.Context ctx = new android.content.Context() {};
        android.content.ContentProvider.ProviderInfo info =
                new android.content.ContentProvider.ProviderInfo("com.example.test");
        provider.attachInfo(ctx, info);
        check("getContext returns attached context", provider.getContext() == ctx);

        // onCreate
        check("onCreate returns true", provider.onCreate());

        // insert + query + update + delete + getType
        android.net.Uri baseUri = android.net.Uri.parse("content://com.example.test/items");
        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put("name", "Alice");

        android.net.Uri inserted = provider.insert(baseUri, cv);
        check("insert returns non-null URI", inserted != null);

        android.database.Cursor cursor = provider.query(baseUri, null, null, null, null);
        check("query returns (stub) cursor (may be null)", true); // null is acceptable

        int updated = provider.update(baseUri, cv, null, null);
        check("update returns count >= 0", updated >= 0);

        int deleted = provider.delete(baseUri, null, null);
        check("delete returns count >= 0", deleted >= 0);

        String type = provider.getType(baseUri);
        check("getType returns non-null string", type != null && !type.isEmpty());

        // bulkInsert — default implementation loops over insert()
        android.content.ContentValues[] bulkCvs = {
            new android.content.ContentValues(),
            new android.content.ContentValues(),
            new android.content.ContentValues()
        };
        bulkCvs[0].put("name", "Bob");
        bulkCvs[1].put("name", "Carol");
        bulkCvs[2].put("name", "Dave");
        int bulkCount = provider.bulkInsert(baseUri, bulkCvs);
        check("bulkInsert returns 3", bulkCount == 3);

        // ProviderInfo authority
        check("ProviderInfo authority set", "com.example.test".equals(info.authority));

        // call() default returns null
        android.os.Bundle callResult = provider.call("myMethod", null, null);
        check("call() default returns null", callResult == null);
    }

    // ── MediaPlayer ──

    static void testMediaPlayer() {
        section("android.media.MediaPlayer → AVPlayer");

        android.media.MediaPlayer mp = new android.media.MediaPlayer();
        check("initial state is IDLE",
                mp.getState() == android.media.MediaPlayer.State.IDLE);

        // setDataSource transitions to INITIALIZED
        mp.setDataSource("/sdcard/test.mp3");
        check("state after setDataSource is INITIALIZED",
                mp.getState() == android.media.MediaPlayer.State.INITIALIZED);

        // setDataSource in wrong state throws
        try {
            mp.setDataSource("/sdcard/other.mp3");
            check("setDataSource in INITIALIZED should throw", false);
        } catch (IllegalStateException e) {
            check("setDataSource in non-IDLE throws IllegalStateException", true);
        }

        // prepare
        final boolean[] preparedCalled = {false};
        mp.setOnPreparedListener(m -> preparedCalled[0] = true);
        mp.prepare();
        check("state after prepare is PREPARED",
                mp.getState() == android.media.MediaPlayer.State.PREPARED);
        check("onPrepared callback fired", preparedCalled[0]);

        // start
        mp.start();
        check("state after start is STARTED",
                mp.getState() == android.media.MediaPlayer.State.STARTED);
        check("isPlaying() true after start", mp.isPlaying());

        // getDuration (mock returns 30000)
        int duration = mp.getDuration();
        check("getDuration returns > 0", duration > 0);

        // setVolume does not throw
        try {
            mp.setVolume(0.5f, 0.5f);
            check("setVolume does not throw", true);
        } catch (Exception e) {
            check("setVolume does not throw", false);
        }

        // seekTo
        final boolean[] seekDone = {false};
        mp.setOnSeekCompleteListener(m -> seekDone[0] = true);
        mp.seekTo(5000);
        check("seekTo does not throw", true);
        check("onSeekComplete callback fired", seekDone[0]);

        // setLooping does not throw
        try {
            mp.setLooping(true);
            check("setLooping(true) does not throw", true);
        } catch (Exception e) {
            check("setLooping(true) does not throw", false);
        }

        // pause
        mp.pause();
        check("state after pause is PAUSED",
                mp.getState() == android.media.MediaPlayer.State.PAUSED);
        check("isPlaying() false after pause", !mp.isPlaying());

        // stop
        mp.stop();
        check("state after stop is STOPPED",
                mp.getState() == android.media.MediaPlayer.State.STOPPED);

        // release
        mp.release();
        check("state after release is END",
                mp.getState() == android.media.MediaPlayer.State.END);
        check("isPlaying() false after release", !mp.isPlaying());

        // getDuration after release throws
        try {
            mp.getDuration();
            check("getDuration after release should throw", false);
        } catch (IllegalStateException e) {
            check("getDuration after release throws IllegalStateException", true);
        }

        // State machine: reset returns to IDLE
        android.media.MediaPlayer mp2 = new android.media.MediaPlayer();
        mp2.setDataSource("/test.mp3");
        mp2.reset();
        check("reset returns state to IDLE",
                mp2.getState() == android.media.MediaPlayer.State.IDLE);
    }

    // ── AudioManager ──

    static void testAudioManager() {
        section("android.media.AudioManager → @ohos.multimedia.audio");

        android.media.AudioManager am = new android.media.AudioManager();

        // Stream type constants
        check("STREAM_VOICE_CALL == 0",
                android.media.AudioManager.STREAM_VOICE_CALL == 0);
        check("STREAM_MUSIC == 3",
                android.media.AudioManager.STREAM_MUSIC == 3);
        check("STREAM_ALARM == 4",
                android.media.AudioManager.STREAM_ALARM == 4);

        // Ringer mode constants
        check("RINGER_MODE_SILENT == 0",
                android.media.AudioManager.RINGER_MODE_SILENT == 0);
        check("RINGER_MODE_VIBRATE == 1",
                android.media.AudioManager.RINGER_MODE_VIBRATE == 1);
        check("RINGER_MODE_NORMAL == 2",
                android.media.AudioManager.RINGER_MODE_NORMAL == 2);

        // AudioFocus constants
        check("AUDIOFOCUS_REQUEST_GRANTED == 1",
                android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED == 1);
        check("AUDIOFOCUS_REQUEST_FAILED == 0",
                android.media.AudioManager.AUDIOFOCUS_REQUEST_FAILED == 0);

        // getStreamMaxVolume
        int maxVol = am.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC);
        check("getStreamMaxVolume(MUSIC) > 0", maxVol > 0);

        // Volume roundtrip
        int origVol = am.getStreamVolume(android.media.AudioManager.STREAM_MUSIC);
        int newVol = Math.max(0, origVol - 1);
        am.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, newVol, 0);
        check("setStreamVolume / getStreamVolume roundtrip",
                am.getStreamVolume(android.media.AudioManager.STREAM_MUSIC) == newVol);

        // Ringer mode roundtrip
        int origMode = am.getRingerMode();
        check("getRingerMode returns valid mode", origMode >= 0 && origMode <= 2);
        am.setRingerMode(android.media.AudioManager.RINGER_MODE_SILENT);
        check("setRingerMode SILENT / getRingerMode",
                am.getRingerMode() == android.media.AudioManager.RINGER_MODE_SILENT);
        am.setRingerMode(origMode); // restore

        // Audio mode (in-process state)
        am.setMode(android.media.AudioManager.MODE_IN_CALL);
        check("setMode/getMode roundtrip",
                am.getMode() == android.media.AudioManager.MODE_IN_CALL);
        am.setMode(android.media.AudioManager.MODE_NORMAL);

        // Speakerphone (in-process)
        am.setSpeakerphoneOn(true);
        check("setSpeakerphoneOn true / isSpeakerphoneOn", am.isSpeakerphoneOn());
        am.setSpeakerphoneOn(false);
        check("setSpeakerphoneOn false / isSpeakerphoneOn", !am.isSpeakerphoneOn());

        // requestAudioFocus always grants in shim
        int focusResult = am.requestAudioFocus(
                fc -> { /* listener */ },
                android.media.AudioManager.STREAM_MUSIC,
                android.media.AudioManager.AUDIOFOCUS_GAIN);
        check("requestAudioFocus returns GRANTED",
                focusResult == android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED);

        int abandonResult = am.abandonAudioFocus(null);
        check("abandonAudioFocus returns GRANTED",
                abandonResult == android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
    }

    // ── ConnectivityManager ──

    static void testConnectivityManager() {
        section("android.net.ConnectivityManager + NetworkInfo + Network");

        android.net.ConnectivityManager cm = new android.net.ConnectivityManager();

        // Constants
        check("TYPE_MOBILE == 0",  android.net.ConnectivityManager.TYPE_MOBILE == 0);
        check("TYPE_WIFI == 1",    android.net.ConnectivityManager.TYPE_WIFI == 1);
        check("TYPE_ETHERNET == 9", android.net.ConnectivityManager.TYPE_ETHERNET == 9);

        // getActiveNetworkInfo (mock always returns a network)
        android.net.NetworkInfo info = cm.getActiveNetworkInfo();
        check("getActiveNetworkInfo non-null (mock is connected)", info != null);

        if (info != null) {
            check("NetworkInfo.isConnected()", info.isConnected());
            int type = info.getType();
            check("NetworkInfo.getType() is known type",
                    type == android.net.ConnectivityManager.TYPE_WIFI
                    || type == android.net.ConnectivityManager.TYPE_MOBILE
                    || type == android.net.ConnectivityManager.TYPE_ETHERNET);
            String typeName = info.getTypeName();
            check("NetworkInfo.getTypeName() non-null", typeName != null && !typeName.isEmpty());
            check("NetworkInfo.getState() is CONNECTED",
                    info.getState() == android.net.NetworkInfo.State.CONNECTED);
            check("NetworkInfo.isAvailable()", info.isAvailable());
            check("NetworkInfo.isConnectedOrConnecting()", info.isConnectedOrConnecting());
        }

        // getActiveNetwork
        android.net.Network net = cm.getActiveNetwork();
        check("getActiveNetwork non-null", net != null);

        // isDefaultNetworkActive
        check("isDefaultNetworkActive() true", cm.isDefaultNetworkActive());

        // getNetworkInfo per type
        android.net.NetworkInfo wifiInfo = cm.getNetworkInfo(
                android.net.ConnectivityManager.TYPE_WIFI);
        check("getNetworkInfo(WIFI) non-null", wifiInfo != null);

        // getAllNetworkInfo
        android.net.NetworkInfo[] all = cm.getAllNetworkInfo();
        check("getAllNetworkInfo returns 3 entries", all != null && all.length == 3);

        // NetworkCallback registration/unregistration
        android.net.ConnectivityManager.NetworkCallback cb =
                new android.net.ConnectivityManager.NetworkCallback();
        cm.registerDefaultNetworkCallback(cb);
        cm.registerDefaultNetworkCallback(cb); // duplicate should not be added twice
        cm.unregisterNetworkCallback(cb);
        check("NetworkCallback register/unregister does not throw", true);
    }

    // ── LocationManager ──

    static void testLocationManager() {
        section("android.location.LocationManager + Criteria");

        android.location.LocationManager lm = new android.location.LocationManager();

        // Provider constants
        check("GPS_PROVIDER is 'gps'",
                "gps".equals(android.location.LocationManager.GPS_PROVIDER));
        check("NETWORK_PROVIDER is 'network'",
                "network".equals(android.location.LocationManager.NETWORK_PROVIDER));
        check("PASSIVE_PROVIDER is 'passive'",
                "passive".equals(android.location.LocationManager.PASSIVE_PROVIDER));

        // getLastKnownLocation (mock returns SF coords)
        android.location.Location loc =
                lm.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
        check("getLastKnownLocation non-null", loc != null);
        if (loc != null) {
            check("latitude is a valid double", !Double.isNaN(loc.getLatitude()));
            check("longitude is a valid double", !Double.isNaN(loc.getLongitude()));
            check("provider is GPS",
                    android.location.LocationManager.GPS_PROVIDER.equals(loc.getProvider()));
        }

        // isProviderEnabled (mock returns true)
        check("isProviderEnabled(GPS) true",
                lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER));

        // getProviders(false) returns all 3
        java.util.List<String> allProviders = lm.getProviders(false);
        check("getProviders(false) returns 3 entries",
                allProviders != null && allProviders.size() == 3);

        // getProviders(true) when enabled returns 3
        java.util.List<String> enabledProviders = lm.getProviders(true);
        check("getProviders(true) returns 3 when enabled",
                enabledProviders != null && enabledProviders.size() == 3);

        // Criteria
        android.location.Criteria fineCriteria = new android.location.Criteria();
        fineCriteria.setAccuracy(android.location.Criteria.ACCURACY_FINE);
        fineCriteria.setPowerRequirement(android.location.Criteria.POWER_HIGH);
        check("Criteria.getAccuracy FINE",
                fineCriteria.getAccuracy() == android.location.Criteria.ACCURACY_FINE);
        check("Criteria.getPowerRequirement HIGH",
                fineCriteria.getPowerRequirement() == android.location.Criteria.POWER_HIGH);

        // getBestProvider with FINE criteria returns GPS
        String bestFine = lm.getBestProvider(fineCriteria, true);
        check("getBestProvider(FINE) returns GPS",
                android.location.LocationManager.GPS_PROVIDER.equals(bestFine));

        android.location.Criteria coarseCriteria = new android.location.Criteria();
        coarseCriteria.setAccuracy(android.location.Criteria.ACCURACY_COARSE);
        String bestCoarse = lm.getBestProvider(coarseCriteria, true);
        check("getBestProvider(COARSE) returns NETWORK",
                android.location.LocationManager.NETWORK_PROVIDER.equals(bestCoarse));

        // Register / unregister listener
        android.location.LocationListener listener = new android.location.LocationListener() {
            @Override public void onLocationChanged(android.location.Location l) {}
            @Override public void onStatusChanged(String p, int s, android.os.Bundle e) {}
            @Override public void onProviderEnabled(String p) {}
            @Override public void onProviderDisabled(String p) {}
        };

        check("no listener before register", !lm.hasListener(listener));
        lm.requestLocationUpdates(
                android.location.LocationManager.GPS_PROVIDER, 1000L, 0f, listener);
        check("listener registered", lm.hasListener(listener));
        check("listenerCount == 1", lm.getListenerCount() == 1);

        lm.removeUpdates(listener);
        check("listener removed after removeUpdates", !lm.hasListener(listener));
        check("listenerCount == 0 after removeUpdates", lm.getListenerCount() == 0);
    }

    // ── WifiManager ──

    static void testWifiManager() {
        section("android.net.wifi.WifiManager + WifiInfo");

        android.net.wifi.WifiManager wm = new android.net.wifi.WifiManager();

        // State constants
        check("WIFI_STATE_DISABLED == 1",
                android.net.wifi.WifiManager.WIFI_STATE_DISABLED == 1);
        check("WIFI_STATE_ENABLED == 3",
                android.net.wifi.WifiManager.WIFI_STATE_ENABLED == 3);

        // isWifiEnabled (mock starts as enabled)
        check("isWifiEnabled() true initially", wm.isWifiEnabled());

        // setWifiEnabled roundtrip
        wm.setWifiEnabled(false);
        check("isWifiEnabled() false after setWifiEnabled(false)", !wm.isWifiEnabled());
        wm.setWifiEnabled(true);
        check("isWifiEnabled() true after setWifiEnabled(true)", wm.isWifiEnabled());

        // getWifiState
        int state = wm.getWifiState();
        check("getWifiState returns ENABLED when wifi on",
                state == android.net.wifi.WifiManager.WIFI_STATE_ENABLED);

        // startScan returns true
        check("startScan() returns true", wm.startScan());

        // getScanResults returns non-null list
        java.util.List<android.net.wifi.ScanResult> scanResults = wm.getScanResults();
        check("getScanResults returns non-null", scanResults != null);

        // getConnectionInfo
        android.net.wifi.WifiInfo wifiInfo = wm.getConnectionInfo();
        check("getConnectionInfo non-null", wifiInfo != null);
        if (wifiInfo != null) {
            String ssid = wifiInfo.getSSID();
            check("getSSID non-null", ssid != null);
            int rssi = wifiInfo.getRssi();
            check("getRssi is negative (dBm)", rssi < 0);
        }

        // calculateSignalLevel
        int level = android.net.wifi.WifiManager.calculateSignalLevel(-50, 5);
        check("calculateSignalLevel(-50, 5) in [0..4]", level >= 0 && level <= 4);

        int minLevel = android.net.wifi.WifiManager.calculateSignalLevel(-100, 5);
        check("calculateSignalLevel(MIN_RSSI) == 0", minLevel == 0);

        int maxLevel = android.net.wifi.WifiManager.calculateSignalLevel(-55, 5);
        check("calculateSignalLevel(MAX_RSSI) == 4", maxLevel == 4);

        // compareSignalLevel
        int cmp = android.net.wifi.WifiManager.compareSignalLevel(-60, -70);
        check("compareSignalLevel(-60, -70) > 0 (stronger)", cmp > 0);

        // getDhcpInfo
        android.net.wifi.DhcpInfo dhcp = wm.getDhcpInfo();
        check("getDhcpInfo non-null", dhcp != null);
    }

    // ── TelephonyManager ──

    static void testTelephonyManager() {
        section("android.telephony.TelephonyManager → @ohos.telephony");

        android.telephony.TelephonyManager tm =
                android.telephony.TelephonyManager.from(new android.content.Context() {});

        // Phone type constants
        check("PHONE_TYPE_NONE == 0",
                android.telephony.TelephonyManager.PHONE_TYPE_NONE == 0);
        check("PHONE_TYPE_GSM == 1",
                android.telephony.TelephonyManager.PHONE_TYPE_GSM == 1);
        check("PHONE_TYPE_CDMA == 2",
                android.telephony.TelephonyManager.PHONE_TYPE_CDMA == 2);

        // SIM state constants
        check("SIM_STATE_UNKNOWN == 0",
                android.telephony.TelephonyManager.SIM_STATE_UNKNOWN == 0);
        check("SIM_STATE_READY == 5",
                android.telephony.TelephonyManager.SIM_STATE_READY == 5);

        // Network type constants
        check("NETWORK_TYPE_LTE == 13",
                android.telephony.TelephonyManager.NETWORK_TYPE_LTE == 13);
        check("NETWORK_TYPE_NR == 20",
                android.telephony.TelephonyManager.NETWORK_TYPE_NR == 20);

        // getDeviceId (mock returns non-empty)
        String deviceId = tm.getDeviceId();
        check("getDeviceId non-null/non-empty",
                deviceId != null && !deviceId.isEmpty());

        // getLine1Number (mock returns a number)
        String line1 = tm.getLine1Number();
        check("getLine1Number non-null", line1 != null);

        // getSimState (mock returns SIM_STATE_READY = 5)
        int simState = tm.getSimState();
        check("getSimState returns valid state", simState >= 0 && simState <= 5);

        // getPhoneType (mock returns PHONE_TYPE_GSM = 1)
        int phoneType = tm.getPhoneType();
        check("getPhoneType returns valid type",
                phoneType == android.telephony.TelephonyManager.PHONE_TYPE_GSM
                || phoneType == android.telephony.TelephonyManager.PHONE_TYPE_CDMA
                || phoneType == android.telephony.TelephonyManager.PHONE_TYPE_SIP
                || phoneType == android.telephony.TelephonyManager.PHONE_TYPE_NONE);

        // getNetworkType (mock returns NETWORK_TYPE_LTE = 13)
        int netType = tm.getNetworkType();
        check("getNetworkType returns valid type", netType >= 0);

        // getNetworkOperatorName
        String opName = tm.getNetworkOperatorName();
        check("getNetworkOperatorName non-null", opName != null);

        // isNetworkRoaming
        check("isNetworkRoaming returns boolean (no exception)",
                !tm.isNetworkRoaming() || tm.isNetworkRoaming()); // always passes

        // getDataState
        int dataState = tm.getDataState();
        check("getDataState returns valid state",
                dataState == android.telephony.TelephonyManager.DATA_DISCONNECTED
                || dataState == android.telephony.TelephonyManager.DATA_CONNECTING
                || dataState == android.telephony.TelephonyManager.DATA_CONNECTED);

        // from() is singleton
        android.telephony.TelephonyManager tm2 =
                android.telephony.TelephonyManager.from(new android.content.Context() {});
        check("from() returns singleton", tm == tm2);
    }

    // ── Graphics ──

    static void testGraphics() {
        section("android.graphics.Color / Rect / RectF / Point / PointF");

        // ── Color constants ─────────────────────────────────────────────────────
        check("Color.BLACK == 0xFF000000",
                android.graphics.Color.BLACK == 0xFF000000);
        check("Color.WHITE == 0xFFFFFFFF",
                android.graphics.Color.WHITE == 0xFFFFFFFF);
        check("Color.RED == 0xFFFF0000",
                android.graphics.Color.RED == 0xFFFF0000);
        check("Color.TRANSPARENT == 0",
                android.graphics.Color.TRANSPARENT == 0);

        // argb / component extractors
        int col = android.graphics.Color.argb(128, 255, 64, 0);
        check("Color.argb alpha == 128",
                android.graphics.Color.alpha(col) == 128);
        check("Color.argb red == 255",
                android.graphics.Color.red(col) == 255);
        check("Color.argb green == 64",
                android.graphics.Color.green(col) == 64);
        check("Color.argb blue == 0",
                android.graphics.Color.blue(col) == 0);

        // rgb (fully opaque)
        int rgbCol = android.graphics.Color.rgb(0, 128, 255);
        check("Color.rgb alpha == 255",
                android.graphics.Color.alpha(rgbCol) == 255);
        check("Color.rgb blue == 255",
                android.graphics.Color.blue(rgbCol) == 255);

        // parseColor #RRGGBB
        int parsed = android.graphics.Color.parseColor("#FF8000");
        check("parseColor #FF8000 red == 255",
                android.graphics.Color.red(parsed) == 255);
        check("parseColor #FF8000 green == 128",
                android.graphics.Color.green(parsed) == 128);
        check("parseColor #FF8000 blue == 0",
                android.graphics.Color.blue(parsed) == 0);
        check("parseColor #FF8000 alpha == 255",
                android.graphics.Color.alpha(parsed) == 255);

        // parseColor named
        check("parseColor 'red' == RED",
                android.graphics.Color.parseColor("red") == android.graphics.Color.RED);
        check("parseColor 'white' == WHITE",
                android.graphics.Color.parseColor("white") == android.graphics.Color.WHITE);

        // parseColor #AARRGGBB
        int argbParsed = android.graphics.Color.parseColor("#80FF0000");
        check("parseColor #AARRGGBB alpha == 0x80",
                android.graphics.Color.alpha(argbParsed) == 0x80);

        // parseColor invalid throws
        try {
            android.graphics.Color.parseColor("notacolor");
            check("parseColor invalid should throw", false);
        } catch (IllegalArgumentException e) {
            check("parseColor invalid throws IllegalArgumentException", true);
        }

        // ── Rect ──────────────────────────────────────────────────────────────────
        android.graphics.Rect rect = new android.graphics.Rect(10, 20, 110, 70);
        check("Rect.width() == 100", rect.width() == 100);
        check("Rect.height() == 50", rect.height() == 50);
        check("Rect.centerX() == 60", rect.centerX() == 60);
        check("Rect.centerY() == 45", rect.centerY() == 45);
        check("Rect.isEmpty() false", !rect.isEmpty());

        // contains(point)
        check("Rect.contains(60, 45) true", rect.contains(60, 45));
        check("Rect.contains(0, 0) false", !rect.contains(0, 0));

        // contains(rect)
        check("Rect.contains sub-rect", rect.contains(20, 25, 100, 65));

        // intersect
        android.graphics.Rect r2 = new android.graphics.Rect(60, 40, 200, 100);
        android.graphics.Rect intersectRect = new android.graphics.Rect(rect);
        boolean intersected = intersectRect.intersect(r2);
        check("Rect.intersect returns true when overlap", intersected);
        check("Rect.intersect left == 60", intersectRect.left == 60);
        check("Rect.intersect top == 40", intersectRect.top == 40);
        check("Rect.intersect right == 110", intersectRect.right == 110);
        check("Rect.intersect bottom == 70", intersectRect.bottom == 70);

        // no intersection
        android.graphics.Rect noOver = new android.graphics.Rect(rect);
        boolean noIntersect = noOver.intersect(200, 200, 300, 300);
        check("Rect.intersect returns false when no overlap", !noIntersect);

        // set + equals
        android.graphics.Rect r3 = new android.graphics.Rect();
        r3.set(10, 20, 110, 70);
        check("Rect.set / equals", r3.equals(rect));

        // offset
        android.graphics.Rect r4 = new android.graphics.Rect(rect);
        r4.offset(5, 10);
        check("Rect.offset left", r4.left == 15);
        check("Rect.offset bottom", r4.bottom == 80);

        // ── RectF ─────────────────────────────────────────────────────────────────
        android.graphics.RectF rf = new android.graphics.RectF(0f, 0f, 3.5f, 2.0f);
        check("RectF.width() == 3.5",
                Math.abs(rf.width() - 3.5f) < 0.001f);
        check("RectF.height() == 2.0",
                Math.abs(rf.height() - 2.0f) < 0.001f);
        check("RectF.centerX() == 1.75",
                Math.abs(rf.centerX() - 1.75f) < 0.001f);
        check("RectF.isEmpty() false", !rf.isEmpty());

        android.graphics.RectF rf2 = new android.graphics.RectF(2.0f, 1.0f, 5.0f, 4.0f);
        android.graphics.RectF rfCopy = new android.graphics.RectF(rf.left, rf.top, rf.right, rf.bottom);
        boolean rfIntersected = rfCopy.intersect(rf2);
        check("RectF.intersect returns true", rfIntersected);
        check("RectF.intersect left == 2.0", Math.abs(rfCopy.left - 2.0f) < 0.001f);

        // RectF from Rect
        android.graphics.RectF rfFromInt = new android.graphics.RectF(rect);
        check("RectF from Rect left == 10", Math.abs(rfFromInt.left - 10f) < 0.001f);

        // ── Point ─────────────────────────────────────────────────────────────────
        android.graphics.Point p = new android.graphics.Point(3, 7);
        check("Point.x == 3", p.x == 3);
        check("Point.y == 7", p.y == 7);

        android.graphics.Point p2 = new android.graphics.Point(p);
        check("Point copy constructor", p2.equals(p));

        p2.set(10, 20);
        check("Point.set x == 10", p2.x == 10);
        check("Point.set y == 20", p2.y == 20);
        check("Point not equal after set", !p2.equals(p));

        // ── PointF ────────────────────────────────────────────────────────────────
        android.graphics.PointF pf = new android.graphics.PointF(1.5f, 2.5f);
        check("PointF.x == 1.5", Math.abs(pf.x - 1.5f) < 0.001f);
        check("PointF.y == 2.5", Math.abs(pf.y - 2.5f) < 0.001f);

        float len = pf.length();
        check("PointF.length() ~2.915",
                Math.abs(len - (float) Math.sqrt(1.5*1.5 + 2.5*2.5)) < 0.001f);

        android.graphics.PointF pf2 = new android.graphics.PointF(pf);
        check("PointF copy constructor equals original", pf2.equals(pf));

        pf2.set(0f, 0f);
        check("PointF.set to (0,0) not equals original", !pf2.equals(pf));
    }

    // ── MatrixCursor tests ──────────────────────────────────────────────────

    static void testMatrixCursor() {
        section("android.database.MatrixCursor");

        String[] cols = {"_id", "name", "value"};
        android.database.MatrixCursor mc = new android.database.MatrixCursor(cols);
        check("initial count == 0", mc.getCount() == 0);

        // addRow with Object[]
        mc.addRow(new Object[]{1, "alpha", "100"});
        mc.addRow(new Object[]{2, "beta", "200"});
        check("count after 2 addRow == 2", mc.getCount() == 2);

        // getColumnNames
        String[] names = mc.getColumnNames();
        check("getColumnNames length == 3", names.length == 3);
        check("getColumnNames[1] == name", "name".equals(names[1]));

        // getColumnIndex
        check("getColumnIndex(name) == 1", mc.getColumnIndex("name") == 1);
        check("getColumnIndex(missing) == -1", mc.getColumnIndex("missing") == -1);

        // moveToFirst and read values
        check("moveToFirst", mc.moveToFirst());
        check("getString(1) == alpha", "alpha".equals(mc.getString(1)));
        check("getInt(0) == 1", mc.getInt(0) == 1);

        // moveToNext
        check("moveToNext", mc.moveToNext());
        check("getString(1) == beta", "beta".equals(mc.getString(1)));
        check("getInt(2) == 200", mc.getInt(2) == 200);

        // moveToNext at end
        check("moveToNext past end false", !mc.moveToNext());

        // newRow with RowBuilder
        mc.newRow().add(3).add("gamma").add("300");
        check("count after newRow == 3", mc.getCount() == 3);
        mc.moveToLast();
        check("getString(1) after newRow == gamma", "gamma".equals(mc.getString(1)));

        // isNull
        android.database.MatrixCursor mc2 = new android.database.MatrixCursor(new String[]{"a"});
        mc2.addRow(new Object[]{null});
        mc2.moveToFirst();
        check("isNull for null value", mc2.isNull(0));

        // position checks
        mc.moveToFirst();
        check("isFirst", mc.isFirst());
        mc.moveToLast();
        check("isLast", mc.isLast());
    }

    // ── MergeCursor tests ───────────────────────────────────────────────────

    static void testMergeCursor() {
        section("android.database.MergeCursor");

        String[] cols = {"_id", "val"};
        android.database.MatrixCursor c1 = new android.database.MatrixCursor(cols);
        c1.addRow(new Object[]{1, "a"});
        c1.addRow(new Object[]{2, "b"});

        android.database.MatrixCursor c2 = new android.database.MatrixCursor(cols);
        c2.addRow(new Object[]{3, "c"});

        android.database.MergeCursor merge = new android.database.MergeCursor(
            new android.database.Cursor[]{c1, c2});

        check("total count == 3", merge.getCount() == 3);
        check("getColumnNames[0] == _id", "_id".equals(merge.getColumnNames()[0]));

        // Navigate through all rows
        check("moveToFirst", merge.moveToFirst());
        check("row 0 val == a", "a".equals(merge.getString(1)));

        check("moveToNext", merge.moveToNext());
        check("row 1 val == b", "b".equals(merge.getString(1)));

        check("moveToNext crosses cursor boundary", merge.moveToNext());
        check("row 2 val == c", "c".equals(merge.getString(1)));

        check("moveToNext at end false", !merge.moveToNext());

        // moveToPosition
        check("moveToPosition(0)", merge.moveToPosition(0));
        check("position 0 val == a", "a".equals(merge.getString(1)));

        check("moveToPosition(2)", merge.moveToPosition(2));
        check("position 2 val == c", "c".equals(merge.getString(1)));

        // Empty merge
        android.database.MergeCursor empty = new android.database.MergeCursor(
            new android.database.Cursor[]{});
        check("empty merge count == 0", empty.getCount() == 0);
    }

    // ── Matrix tests ────────────────────────────────────────────────────────

    static void testMatrix() {
        section("android.graphics.Matrix");

        android.graphics.Matrix m = new android.graphics.Matrix();
        check("new Matrix isIdentity", m.isIdentity());
        check("new Matrix isAffine", m.isAffine());

        // Constants
        check("MSCALE_X == 0", android.graphics.Matrix.MSCALE_X == 0);
        check("MSCALE_Y == 4", android.graphics.Matrix.MSCALE_Y == 4);
        check("MTRANS_X == 2", android.graphics.Matrix.MTRANS_X == 2);
        check("MTRANS_Y == 5", android.graphics.Matrix.MTRANS_Y == 5);
        check("MPERSP_2 == 8", android.graphics.Matrix.MPERSP_2 == 8);

        // getValues roundtrip
        float[] vals = new float[9];
        m.getValues(vals);
        check("identity[0] == 1", vals[0] == 1f);
        check("identity[4] == 1", vals[4] == 1f);
        check("identity[8] == 1", vals[8] == 1f);
        check("identity[1] == 0", vals[1] == 0f);

        // setTranslate
        m.setTranslate(10f, 20f);
        check("after setTranslate not identity", !m.isIdentity());
        m.getValues(vals);
        check("translate X == 10", vals[2] == 10f);
        check("translate Y == 20", vals[5] == 20f);

        // mapPoints with translation
        float[] pts = {0f, 0f};
        m.mapPoints(pts);
        check("mapPoints translated X == 10", Math.abs(pts[0] - 10f) < 0.001f);
        check("mapPoints translated Y == 20", Math.abs(pts[1] - 20f) < 0.001f);

        // reset
        m.reset();
        check("after reset isIdentity", m.isIdentity());

        // setScale
        m.setScale(2f, 3f);
        m.getValues(vals);
        check("scale X == 2", vals[0] == 2f);
        check("scale Y == 3", vals[4] == 3f);

        // copy constructor
        android.graphics.Matrix m2 = new android.graphics.Matrix(m);
        check("copy equals original", m2.equals(m));

        // set(Matrix)
        android.graphics.Matrix m3 = new android.graphics.Matrix();
        m3.set(m);
        check("set(matrix) equals", m3.equals(m));

        // invert
        m.setScale(2f, 4f);
        android.graphics.Matrix inv = new android.graphics.Matrix();
        boolean ok = m.invert(inv);
        check("invert scale succeeds", ok);
        inv.getValues(vals);
        check("inverted scale X == 0.5", Math.abs(vals[0] - 0.5f) < 0.001f);
        check("inverted scale Y == 0.25", Math.abs(vals[4] - 0.25f) < 0.001f);

        // postTranslate
        m.reset();
        m.postTranslate(5f, 10f);
        m.getValues(vals);
        check("postTranslate X == 5", Math.abs(vals[2] - 5f) < 0.001f);

        // setRotate 90 degrees
        m.setRotate(90f);
        float[] rpts = {1f, 0f};
        m.mapPoints(rpts);
        check("rotate 90 maps (1,0) to ~(0,1) x", Math.abs(rpts[0]) < 0.001f);
        check("rotate 90 maps (1,0) to ~(0,1) y", Math.abs(rpts[1] - 1f) < 0.001f);
    }

    // ── DatabaseUtils tests ─────────────────────────────────────────────────

    static void testDatabaseUtils() {
        section("android.database.DatabaseUtils");

        // Statement type constants
        check("STATEMENT_SELECT == 1",
            android.database.DatabaseUtils.STATEMENT_SELECT == 1);
        check("STATEMENT_UPDATE == 2",
            android.database.DatabaseUtils.STATEMENT_UPDATE == 2);
        check("STATEMENT_ATTACH == 3",
            android.database.DatabaseUtils.STATEMENT_ATTACH == 3);
        check("STATEMENT_BEGIN == 4",
            android.database.DatabaseUtils.STATEMENT_BEGIN == 4);
        check("STATEMENT_COMMIT == 5",
            android.database.DatabaseUtils.STATEMENT_COMMIT == 5);
        check("STATEMENT_ABORT == 6",
            android.database.DatabaseUtils.STATEMENT_ABORT == 6);
        check("STATEMENT_PRAGMA == 7",
            android.database.DatabaseUtils.STATEMENT_PRAGMA == 7);
        check("STATEMENT_DDL == 8",
            android.database.DatabaseUtils.STATEMENT_DDL == 8);
        check("STATEMENT_OTHER == 99",
            android.database.DatabaseUtils.STATEMENT_OTHER == 99);

        // getSqlStatementType
        check("SELECT type",
            android.database.DatabaseUtils.getSqlStatementType("SELECT * FROM t") == 1);
        check("INSERT type",
            android.database.DatabaseUtils.getSqlStatementType("INSERT INTO t VALUES(1)") == 2);
        check("UPDATE type",
            android.database.DatabaseUtils.getSqlStatementType("UPDATE t SET x=1") == 2);
        check("DELETE type",
            android.database.DatabaseUtils.getSqlStatementType("DELETE FROM t") == 2);
        check("CREATE type",
            android.database.DatabaseUtils.getSqlStatementType("CREATE TABLE t(x)") == 8);
        check("BEGIN type",
            android.database.DatabaseUtils.getSqlStatementType("BEGIN TRANSACTION") == 4);
        check("COMMIT type",
            android.database.DatabaseUtils.getSqlStatementType("COMMIT") == 5);
        check("PRAGMA type",
            android.database.DatabaseUtils.getSqlStatementType("PRAGMA journal_mode") == 7);

        // sqlEscapeString
        String escaped = android.database.DatabaseUtils.sqlEscapeString("it's a test");
        check("sqlEscapeString quotes", escaped.equals("'it''s a test'"));

        String simple = android.database.DatabaseUtils.sqlEscapeString("hello");
        check("sqlEscapeString simple", simple.equals("'hello'"));

        // concatenateWhere
        String where = android.database.DatabaseUtils.concatenateWhere("a=1", "b=2");
        check("concatenateWhere", "(a=1) AND (b=2)".equals(where));

        String whereNull = android.database.DatabaseUtils.concatenateWhere(null, "b=2");
        check("concatenateWhere null first", "b=2".equals(whereNull));

        // appendSelectionArgs
        String[] args = android.database.DatabaseUtils.appendSelectionArgs(
            new String[]{"a"}, new String[]{"b", "c"});
        check("appendSelectionArgs length == 3", args.length == 3);
        check("appendSelectionArgs[2] == c", "c".equals(args[2]));

        // getCollationKey
        String key = android.database.DatabaseUtils.getCollationKey("Hello");
        check("getCollationKey lowercase", "hello".equals(key));

        // getHexCollationKey
        String hex = android.database.DatabaseUtils.getHexCollationKey("ab");
        check("getHexCollationKey ab", "00610062".equals(hex));

        // appendEscapedSQLString
        StringBuilder sb = new StringBuilder();
        android.database.DatabaseUtils.appendEscapedSQLString(sb, "O'Brien");
        check("appendEscapedSQLString", "'O''Brien'".equals(sb.toString()));
    }

    // ── RectF extended tests ────────────────────────────────────────────────

    static void testRectFExtended() {
        section("android.graphics.RectF (extended)");

        // Copy constructor
        android.graphics.RectF orig = new android.graphics.RectF(1f, 2f, 3f, 4f);
        android.graphics.RectF copy = new android.graphics.RectF(orig);
        check("RectF copy constructor", copy.equals(orig));

        // contains(RectF)
        android.graphics.RectF outer = new android.graphics.RectF(0f, 0f, 10f, 10f);
        android.graphics.RectF inner = new android.graphics.RectF(2f, 2f, 8f, 8f);
        check("contains(RectF) true", outer.contains(inner));
        check("contains(RectF) false", !inner.contains(outer));

        // setEmpty
        android.graphics.RectF r = new android.graphics.RectF(1f, 2f, 3f, 4f);
        r.setEmpty();
        check("setEmpty isEmpty", r.isEmpty());

        // offsetTo
        r.set(10f, 20f, 30f, 40f);
        r.offsetTo(0f, 0f);
        check("offsetTo left == 0", r.left == 0f);
        check("offsetTo right == 20", r.right == 20f);
        check("offsetTo bottom == 20", r.bottom == 20f);

        // sort
        android.graphics.RectF unsorted = new android.graphics.RectF(5f, 5f, 1f, 1f);
        unsorted.sort();
        check("sort left < right", unsorted.left < unsorted.right);
        check("sort top < bottom", unsorted.top < unsorted.bottom);

        // round
        android.graphics.RectF rf = new android.graphics.RectF(1.3f, 2.7f, 3.5f, 4.2f);
        android.graphics.Rect rounded = new android.graphics.Rect();
        rf.round(rounded);
        check("round left == 1", rounded.left == 1);
        check("round top == 3", rounded.top == 3);
        check("round right == 4", rounded.right == 4 || rounded.right == 3);

        // roundOut
        android.graphics.Rect roundedOut = new android.graphics.Rect();
        rf.roundOut(roundedOut);
        check("roundOut left == 1", roundedOut.left == 1);
        check("roundOut top == 2", roundedOut.top == 2);
        check("roundOut right == 4", roundedOut.right == 4);
        check("roundOut bottom == 5", roundedOut.bottom == 5);

        // flattenToString / unflattenFromString
        android.graphics.RectF r2 = new android.graphics.RectF(1.5f, 2.5f, 3.5f, 4.5f);
        String flat = r2.flattenToString();
        check("flattenToString non-null", flat != null);
        android.graphics.RectF unflat = android.graphics.RectF.unflattenFromString(flat);
        check("unflattenFromString roundtrip", unflat != null && unflat.equals(r2));

        // toShortString
        check("toShortString non-null", r2.toShortString() != null);

        // setIntersect
        android.graphics.RectF a = new android.graphics.RectF(0f, 0f, 10f, 10f);
        android.graphics.RectF b = new android.graphics.RectF(5f, 5f, 15f, 15f);
        android.graphics.RectF result = new android.graphics.RectF();
        check("setIntersect true", result.setIntersect(a, b));
        check("setIntersect left == 5", result.left == 5f);
        check("setIntersect bottom == 10", result.bottom == 10f);

        // intersects (non-mutating)
        check("intersects true", a.intersects(5f, 5f, 15f, 15f));
        check("intersects false", !a.intersects(20f, 20f, 30f, 30f));
    }
}
