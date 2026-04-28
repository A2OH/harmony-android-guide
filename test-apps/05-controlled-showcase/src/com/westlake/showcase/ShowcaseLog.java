package com.westlake.showcase;

final class ShowcaseLog {
    private static final String DEFAULT_MARKER_PATH =
            "/data/user/0/com.westlake.host/files/vm/cutoff_canary_markers.log";
    private static final String PUBLIC_MARKER_PATH =
            "/data/local/tmp/westlake/cutoff_canary_markers.log";

    private ShowcaseLog() {}

    static void mark(String event, String detail) {
        String line = "SHOWCASE_" + event + " " + detail;
        try {
            if (com.westlake.engine.WestlakeLauncher.appendCutoffCanaryMarker(line)) {
                return;
            }
        } catch (Throwable ignored) {
        }
        try {
            appendLine(DEFAULT_MARKER_PATH, line);
        } catch (Throwable first) {
            try {
                appendLine(PUBLIC_MARKER_PATH, line);
            } catch (Throwable ignored) {
            }
        }
    }

    private static void appendLine(String path, String line) throws java.io.IOException {
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
    }
}
