package com.westlake.cutoffcanary;

final class CanaryLog {
    private static final String DEFAULT_MARKER_PATH =
            "/data/user/0/com.westlake.host/files/vm/cutoff_canary_markers.log";
    private static final String PUBLIC_MARKER_PATH =
            "/data/local/tmp/westlake/cutoff_canary_markers.log";
    private static volatile String sLastMarker;

    private CanaryLog() {}

    static void raw(String event, String detail) {
        String line = "CANARY_" + event + " " + detail;
        sLastMarker = line;
    }

    static void mark(String event, String detail) {
        String line = "CANARY_" + event + " " + detail;
        sLastMarker = line;
        try {
            appendMarkerLine(line);
        } catch (Throwable ignored) {
        }
        raw(event, detail);
    }

    public static String lastMarker() {
        return sLastMarker;
    }

    private static void appendMarkerLine(String line) throws java.io.IOException {
        try {
            if (com.westlake.engine.WestlakeLauncher.appendCutoffCanaryMarker(line)) {
                return;
            }
        } catch (Throwable ignored) {
        }
        if (appendMarkerLine(DEFAULT_MARKER_PATH, line)) {
            return;
        }
        appendMarkerLine(PUBLIC_MARKER_PATH, line);
    }

    private static boolean appendMarkerLine(String path, String line) throws java.io.IOException {
        java.io.File markerFile = new java.io.File(path);
        java.io.File parent = markerFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        java.io.FileOutputStream out = new java.io.FileOutputStream(markerFile, true);
        try {
            out.write(line.getBytes("UTF-8"));
            out.write('\n');
            out.flush();
        } finally {
            out.close();
        }
        return true;
    }
}
