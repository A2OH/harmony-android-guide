package android.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible streaming JSON writer shim.
 */
public final class JsonWriter implements Closeable, Flushable {

    private final Writer out;
    private String indent = null;
    // Stack tracks context: true = object (name expected), false = array
    private final List<Boolean> stack = new ArrayList<>();
    // Whether the current scope has had at least one element written
    private final List<Boolean> firstInScope = new ArrayList<>();
    private boolean lenient = false;
    private boolean closed = false;

    public JsonWriter(Writer out) {
        if (out == null) throw new NullPointerException("out == null");
        this.out = out;
    }

    public void setIndent(String indent) {
        this.indent = (indent != null && indent.isEmpty()) ? null : indent;
    }

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public boolean isLenient() {
        return lenient;
    }

    public JsonWriter beginObject() throws IOException {
        return open(true, "{");
    }

    public JsonWriter endObject() throws IOException {
        return close(true, "}");
    }

    public JsonWriter beginArray() throws IOException {
        return open(false, "[");
    }

    public JsonWriter endArray() throws IOException {
        return close(false, "]");
    }

    public JsonWriter name(String name) throws IOException {
        if (name == null) throw new NullPointerException("name == null");
        if (stack.isEmpty() || !stack.get(stack.size() - 1)) {
            throw new IllegalStateException("Nesting problem: name() only valid inside object");
        }
        beforeName();
        string(name);
        out.write(':');
        if (indent != null) out.write(' ');
        return this;
    }

    public JsonWriter value(String value) throws IOException {
        if (value == null) return nullValue();
        beforeValue();
        string(value);
        return this;
    }

    public JsonWriter value(boolean value) throws IOException {
        beforeValue();
        out.write(value ? "true" : "false");
        return this;
    }

    public JsonWriter value(Boolean value) throws IOException {
        if (value == null) return nullValue();
        return value(value.booleanValue());
    }

    public JsonWriter value(long value) throws IOException {
        beforeValue();
        out.write(Long.toString(value));
        return this;
    }

    public JsonWriter value(double value) throws IOException {
        if (!lenient && (Double.isNaN(value) || Double.isInfinite(value))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        beforeValue();
        out.append(Double.toString(value));
        return this;
    }

    public JsonWriter value(Number value) throws IOException {
        if (value == null) return nullValue();
        String raw = value.toString();
        if (!lenient && (raw.equals("-Infinity") || raw.equals("Infinity") || raw.equals("NaN"))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        beforeValue();
        out.append(raw);
        return this;
    }

    public JsonWriter nullValue() throws IOException {
        beforeValue();
        out.write("null");
        return this;
    }

    @Override
    public void flush() throws IOException {
        if (closed) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.flush();
        out.close();
        closed = true;
        stack.clear();
        firstInScope.clear();
    }

    // ---- Internal helpers ----

    private JsonWriter open(boolean isObject, String bracket) throws IOException {
        beforeValue();
        stack.add(isObject);
        firstInScope.add(true);
        out.write(bracket);
        return this;
    }

    private JsonWriter close(boolean isObject, String bracket) throws IOException {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Nesting problem.");
        }
        boolean top = stack.get(stack.size() - 1);
        if (top != isObject) {
            throw new IllegalStateException("Nesting problem.");
        }
        stack.remove(stack.size() - 1);
        boolean wasFirst = firstInScope.remove(firstInScope.size() - 1);
        if (!wasFirst && indent != null) {
            newline();
            indent(stack.size());
        }
        out.write(bracket);
        return this;
    }

    private void beforeName() throws IOException {
        if (!stack.isEmpty()) {
            boolean first = firstInScope.get(firstInScope.size() - 1);
            if (!first) {
                out.write(',');
            }
            if (indent != null) {
                newline();
                indent(stack.size());
            }
            firstInScope.set(firstInScope.size() - 1, false);
        }
    }

    private void beforeValue() throws IOException {
        if (stack.isEmpty()) {
            // top-level value
            return;
        }
        boolean inObject = stack.get(stack.size() - 1);
        if (!inObject) {
            // inside array
            boolean first = firstInScope.get(firstInScope.size() - 1);
            if (!first) {
                out.write(',');
            }
            if (indent != null) {
                newline();
                indent(stack.size());
            }
            firstInScope.set(firstInScope.size() - 1, false);
        }
        // if inObject, name() already wrote the separator
    }

    private void newline() throws IOException {
        out.write('\n');
    }

    private void indent(int depth) throws IOException {
        if (indent == null) return;
        for (int i = 0; i < depth; i++) {
            out.write(indent);
        }
    }

    private void string(String value) throws IOException {
        out.write('"');
        for (int i = 0, len = value.length(); i < len; i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"':  out.write("\\\""); break;
                case '\\': out.write("\\\\"); break;
                case '\b': out.write("\\b");  break;
                case '\f': out.write("\\f");  break;
                case '\n': out.write("\\n");  break;
                case '\r': out.write("\\r");  break;
                case '\t': out.write("\\t");  break;
                default:
                    if (c < 0x20) {
                        out.write(String.format("\\u%04x", (int) c));
                    } else {
                        out.write(c);
                    }
            }
        }
        out.write('"');
    }
}
