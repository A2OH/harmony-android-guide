package com.westlake.materialyelp;

import com.westlake.engine.WestlakeLauncher;

final class MaterialYelpLog {
    private MaterialYelpLog() {}

    static void mark(String name, String detail) {
        String message = "MATERIAL_" + name + " " + (detail == null ? "" : detail);
        try {
            WestlakeLauncher.appendCutoffCanaryMarker(message);
        } catch (Throwable ignored) {
        }
    }

    static String token(String value) {
        if (value == null || value.length() == 0) {
            return "empty";
        }
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if ((c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9')) {
                out.append(c);
            } else if (c == '.' || c == '-' || c == '_') {
                out.append(c);
            } else {
                out.append('_');
            }
        }
        if (out.length() > 96) {
            return out.substring(0, 96);
        }
        return out.length() == 0 ? "empty" : out.toString();
    }
}
