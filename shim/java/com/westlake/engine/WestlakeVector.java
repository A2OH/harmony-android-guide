/*
 * PF-arch-030: Vector-drawable XML → OHBridge path primitives.
 *
 * Android vector drawables look like:
 *   <vector width="24dp" height="24dp" viewportWidth="24" viewportHeight="24">
 *     <path fillColor="#FFFFFF" pathData="M12,2L4,12L12,22L20,12Z"/>
 *   </vector>
 *
 * SVG path-data mini-language: M (moveTo), L (lineTo), C (cubicTo),
 * Q (quadTo), Z (close). Coordinates can be absolute (uppercase) or
 * relative (lowercase). We parse a minimal subset that covers the
 * icons used in noice's menu.
 */
package com.westlake.engine;

import com.ohos.shim.bridge.OHBridge;

public final class WestlakeVector {
    private WestlakeVector() {}

    /** Read the vector drawable from `axmlData`, draw scaled into target rect. */
    public static boolean draw(byte[] axmlData, long canvas, int targetX, int targetY,
            int targetW, int targetH, int fallbackColor) {
        if (axmlData == null) return false;
        WestlakeNode vec = WestlakeInflater.inflate(axmlData);
        if (vec == null || !"vector".equals(vec.tag)) return false;
        float vpW = parseFloatAttr(vec, "viewportWidth", 24f);
        float vpH = parseFloatAttr(vec, "viewportHeight", 24f);
        float sx = targetW / vpW;
        float sy = targetH / vpH;
        long pen = OHBridge.penCreate();
        long brush = OHBridge.brushCreate();
        try {
            for (WestlakeNode child : vec.children) {
                if ("path".equals(child.tag)) {
                    drawPath(child, canvas, targetX, targetY, sx, sy, pen, brush, fallbackColor);
                } else if ("group".equals(child.tag)) {
                    /* TODO: rotation/translation/scale group transforms */
                    for (WestlakeNode gc : child.children) {
                        if ("path".equals(gc.tag)) {
                            drawPath(gc, canvas, targetX, targetY, sx, sy, pen, brush, fallbackColor);
                        }
                    }
                }
            }
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private static void drawPath(WestlakeNode pathNode, long canvas,
            int tx, int ty, float sx, float sy,
            long pen, long brush, int fallbackColor) {
        String d = pathNode.getAttr("pathData");
        if (d == null) return;
        int declaredFill = parseColorAttr(pathNode, "fillColor", 0);
        int color;
        if (fallbackColor != 0) color = fallbackColor;
        else if (declaredFill != 0) color = declaredFill;
        else color = 0xFFFFFFFF;
        /* PF-arch-040: native path ops now functional — build a real path
         * (preserving curves) and emit OP_PATH (filled). */
        OHBridge.brushSetColor(brush, color);
        long path = 0;
        try {
            path = OHBridge.pathCreate();
            if (path == 0) {
                /* Fallback to wireframe if pool exhausted. */
                OHBridge.penSetColor(pen, color);
                emitPathAsLines(d, canvas, pen, tx, ty, sx, sy);
                return;
            }
            parseSvgPath(d, path, tx, ty, sx, sy);
            OHBridge.canvasDrawPath(canvas, path, 0L, brush);
        } catch (Throwable t) {
            /* Last-resort fallback: wireframe. */
            try {
                OHBridge.penSetColor(pen, color);
                emitPathAsLines(d, canvas, pen, tx, ty, sx, sy);
            } catch (Throwable ignored) {}
        } finally {
            if (path != 0) {
                try { OHBridge.pathDestroy(path); } catch (Throwable ignored) {}
            }
        }
    }

    /** Walk SVG path commands, emit canvasDrawLine between consecutive points.
     * Curves (C/Q) approximated by their end point — wireframe quality. */
    private static void emitPathAsLines(String d, long canvas, long pen,
            int tx, int ty, float sx, float sy) {
        char cmd = 0;
        float curX = 0, curY = 0;
        float subX = 0, subY = 0;
        float prevPaintedX = 0, prevPaintedY = 0;
        boolean penDown = false;
        int i = 0, n = d.length();
        float[] args = new float[16];
        while (i < n) {
            char c = d.charAt(i);
            if (c == ' ' || c == ',' || c == '\t' || c == '\n' || c == '\r') { i++; continue; }
            if (Character.isLetter(c)) { cmd = c; i++; continue; }
            int argCount = expectedArgCount(cmd);
            if (argCount <= 0) { i++; continue; }
            int got = 0;
            while (got < argCount && i < n) {
                while (i < n) {
                    char cc = d.charAt(i);
                    if (cc == ' ' || cc == ',' || cc == '\t' || cc == '\n' || cc == '\r') i++;
                    else break;
                }
                if (i >= n) break;
                int start = i;
                if (d.charAt(i) == '-' || d.charAt(i) == '+') i++;
                while (i < n) {
                    char cc = d.charAt(i);
                    if ((cc >= '0' && cc <= '9') || cc == '.' || cc == 'e' || cc == 'E'
                            || ((cc == '-' || cc == '+') && (i > start && (d.charAt(i-1) == 'e' || d.charAt(i-1) == 'E')))) {
                        i++;
                    } else break;
                }
                if (i == start) break;
                try { args[got++] = Float.parseFloat(d.substring(start, i)); }
                catch (NumberFormatException nfe) { break; }
            }
            if (got < argCount) break;
            float newX = curX, newY = curY;
            switch (cmd) {
                case 'M': newX = args[0]; newY = args[1]; subX = newX; subY = newY; penDown = false; break;
                case 'm': newX = curX + args[0]; newY = curY + args[1]; subX = newX; subY = newY; penDown = false; break;
                case 'L': newX = args[0]; newY = args[1]; break;
                case 'l': newX = curX + args[0]; newY = curY + args[1]; break;
                case 'H': newX = args[0]; newY = curY; break;
                case 'h': newX = curX + args[0]; newY = curY; break;
                case 'V': newX = curX; newY = args[0]; break;
                case 'v': newX = curX; newY = curY + args[0]; break;
                case 'C': newX = args[4]; newY = args[5]; break;
                case 'c': newX = curX + args[4]; newY = curY + args[5]; break;
                case 'Q': newX = args[2]; newY = args[3]; break;
                case 'q': newX = curX + args[2]; newY = curY + args[3]; break;
                case 'Z': case 'z': newX = subX; newY = subY; break;
                default: continue;
            }
            if (penDown) {
                float x1 = tx + curX * sx, y1 = ty + curY * sy;
                float x2 = tx + newX * sx, y2 = ty + newY * sy;
                OHBridge.canvasDrawLine(canvas, x1, y1, x2, y2, pen);
            }
            curX = newX; curY = newY;
            if (cmd != 'M' && cmd != 'm') penDown = true;
            /* SVG: implicit-L after M */
            if (cmd == 'M') cmd = 'L';
            else if (cmd == 'm') cmd = 'l';
        }
    }

    private static int expectedArgCount(char cmd) {
        switch (cmd) {
            case 'M': case 'm': case 'L': case 'l': case 'T': case 't': return 2;
            case 'H': case 'h': case 'V': case 'v': return 1;
            case 'C': case 'c': return 6;
            case 'S': case 's': case 'Q': case 'q': return 4;
            case 'Z': case 'z': return 0;
            case 'A': case 'a': return 7;
            default: return 0;
        }
    }

    /** Minimal SVG path parser: M/L/C/Q/Z plus relative m/l/c/q. */
    static void parseSvgPath(String d, long path, int tx, int ty, float sx, float sy) {
        char cmd = 0;
        float curX = 0, curY = 0;
        float subX = 0, subY = 0; /* start of current sub-path */
        int i = 0, n = d.length();
        float[] args = new float[16];
        while (i < n) {
            char c = d.charAt(i);
            if (c == ' ' || c == ',' || c == '\t' || c == '\n' || c == '\r') {
                i++; continue;
            }
            if (Character.isLetter(c)) {
                cmd = c;
                i++;
                continue;
            }
            /* parse numbers for this cmd */
            int argCount = expectedArgCount(cmd);
            if (argCount <= 0) { i++; continue; }
            int got = 0;
            while (got < argCount && i < n) {
                /* skip separators */
                while (i < n) {
                    char cc = d.charAt(i);
                    if (cc == ' ' || cc == ',' || cc == '\t' || cc == '\n' || cc == '\r') i++;
                    else break;
                }
                if (i >= n) break;
                int start = i;
                if (d.charAt(i) == '-' || d.charAt(i) == '+') i++;
                while (i < n) {
                    char cc = d.charAt(i);
                    if ((cc >= '0' && cc <= '9') || cc == '.' || cc == 'e' || cc == 'E'
                            || ((cc == '-' || cc == '+') && (i > start && (d.charAt(i-1) == 'e' || d.charAt(i-1) == 'E')))) {
                        i++;
                    } else break;
                }
                if (i == start) break;
                try {
                    args[got++] = Float.parseFloat(d.substring(start, i));
                } catch (NumberFormatException nfe) { break; }
            }
            if (got < argCount) break;
            applyCmd(cmd, args, path, tx, ty, sx, sy,
                    new float[] { curX, curY, subX, subY });
            float[] state = decode(cmd, args, curX, curY, subX, subY);
            curX = state[0]; curY = state[1]; subX = state[2]; subY = state[3];
            /* SVG: repeated commands without letter use the previous cmd
             * (but for M, repeated args become L). */
            if (cmd == 'M') cmd = 'L';
            else if (cmd == 'm') cmd = 'l';
        }
    }

    private static void applyCmd(char cmd, float[] args, long path,
            int tx, int ty, float sx, float sy, float[] cur) {
        float cx = cur[0], cy = cur[1], subx = cur[2], suby = cur[3];
        switch (cmd) {
            case 'M':
                OHBridge.pathMoveTo(path, tx + args[0] * sx, ty + args[1] * sy);
                break;
            case 'm':
                OHBridge.pathMoveTo(path, tx + (cx + args[0]) * sx, ty + (cy + args[1]) * sy);
                break;
            case 'L':
                OHBridge.pathLineTo(path, tx + args[0] * sx, ty + args[1] * sy);
                break;
            case 'l':
                OHBridge.pathLineTo(path, tx + (cx + args[0]) * sx, ty + (cy + args[1]) * sy);
                break;
            case 'H':
                OHBridge.pathLineTo(path, tx + args[0] * sx, ty + cy * sy);
                break;
            case 'h':
                OHBridge.pathLineTo(path, tx + (cx + args[0]) * sx, ty + cy * sy);
                break;
            case 'V':
                OHBridge.pathLineTo(path, tx + cx * sx, ty + args[0] * sy);
                break;
            case 'v':
                OHBridge.pathLineTo(path, tx + cx * sx, ty + (cy + args[0]) * sy);
                break;
            case 'C':
                OHBridge.pathCubicTo(path,
                        tx + args[0] * sx, ty + args[1] * sy,
                        tx + args[2] * sx, ty + args[3] * sy,
                        tx + args[4] * sx, ty + args[5] * sy);
                break;
            case 'c':
                OHBridge.pathCubicTo(path,
                        tx + (cx + args[0]) * sx, ty + (cy + args[1]) * sy,
                        tx + (cx + args[2]) * sx, ty + (cy + args[3]) * sy,
                        tx + (cx + args[4]) * sx, ty + (cy + args[5]) * sy);
                break;
            case 'Q':
                OHBridge.pathQuadTo(path,
                        tx + args[0] * sx, ty + args[1] * sy,
                        tx + args[2] * sx, ty + args[3] * sy);
                break;
            case 'q':
                OHBridge.pathQuadTo(path,
                        tx + (cx + args[0]) * sx, ty + (cy + args[1]) * sy,
                        tx + (cx + args[2]) * sx, ty + (cy + args[3]) * sy);
                break;
            case 'Z': case 'z':
                OHBridge.pathClose(path);
                break;
            default:
                /* unhandled — ignore */
                break;
        }
    }

    private static float[] decode(char cmd, float[] args,
            float curX, float curY, float subX, float subY) {
        float[] r = new float[] { curX, curY, subX, subY };
        switch (cmd) {
            case 'M': r[0] = args[0]; r[1] = args[1]; r[2] = args[0]; r[3] = args[1]; break;
            case 'm': r[0] += args[0]; r[1] += args[1]; r[2] = r[0]; r[3] = r[1]; break;
            case 'L': r[0] = args[0]; r[1] = args[1]; break;
            case 'l': r[0] += args[0]; r[1] += args[1]; break;
            case 'H': r[0] = args[0]; break;
            case 'h': r[0] += args[0]; break;
            case 'V': r[1] = args[0]; break;
            case 'v': r[1] += args[0]; break;
            case 'C': r[0] = args[4]; r[1] = args[5]; break;
            case 'c': r[0] += args[4]; r[1] += args[5]; break;
            case 'Q': r[0] = args[2]; r[1] = args[3]; break;
            case 'q': r[0] += args[2]; r[1] += args[3]; break;
            case 'Z': case 'z': r[0] = r[2]; r[1] = r[3]; break;
            default: break;
        }
        return r;
    }

    private static float parseFloatAttr(WestlakeNode n, String name, float defaultValue) {
        String v = n.getAttr(name);
        if (v == null) return defaultValue;
        try {
            int end = 0;
            while (end < v.length() && (v.charAt(end) == '-'
                    || (v.charAt(end) >= '0' && v.charAt(end) <= '9') || v.charAt(end) == '.')) {
                end++;
            }
            if (end == 0) return defaultValue;
            return Float.parseFloat(v.substring(0, end));
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    private static int parseColorAttr(WestlakeNode n, String name, int defaultValue) {
        String v = n.getAttr(name);
        if (v == null || v.isEmpty()) return defaultValue;
        if (v.startsWith("#")) {
            try {
                long parsed = Long.parseLong(v.substring(1), 16);
                int len = v.length() - 1;
                if (len == 6) return 0xFF000000 | (int) parsed;
                return (int) parsed;
            } catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        try {
            return (int) Long.parseLong(v);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
}
