package android.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible streaming JSON reader shim.
 */
public final class JsonReader implements Closeable {

    public enum JsonToken {
        BEGIN_ARRAY,
        END_ARRAY,
        BEGIN_OBJECT,
        END_OBJECT,
        NAME,
        STRING,
        NUMBER,
        BOOLEAN,
        NULL,
        END_DOCUMENT
    }

    private final Reader in;
    private boolean lenient = false;

    // Stack: true = object, false = array
    private final List<Boolean> stack = new ArrayList<>();
    // Parsed tokens buffered
    private JsonToken peeked = null;
    private String peekedString = null;

    public JsonReader(Reader in) {
        if (in == null) throw new NullPointerException("in == null");
        this.in = new BufferedReader(in);
    }

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public boolean isLenient() {
        return lenient;
    }

    public void beginArray() throws IOException {
        expect(JsonToken.BEGIN_ARRAY);
        stack.add(false);
    }

    public void endArray() throws IOException {
        expect(JsonToken.END_ARRAY);
        if (!stack.isEmpty()) stack.remove(stack.size() - 1);
    }

    public void beginObject() throws IOException {
        expect(JsonToken.BEGIN_OBJECT);
        stack.add(true);
    }

    public void endObject() throws IOException {
        expect(JsonToken.END_OBJECT);
        if (!stack.isEmpty()) stack.remove(stack.size() - 1);
    }

    public boolean hasNext() throws IOException {
        JsonToken token = peek();
        return token != JsonToken.END_OBJECT && token != JsonToken.END_ARRAY
                && token != JsonToken.END_DOCUMENT;
    }

    public JsonToken peek() throws IOException {
        if (peeked == null) {
            peeked = advance();
        }
        return peeked;
    }

    public String nextName() throws IOException {
        JsonToken token = peek();
        if (token != JsonToken.NAME) {
            throw new IllegalStateException("Expected NAME but was " + token);
        }
        String name = peekedString;
        peeked = null;
        peekedString = null;
        return name;
    }

    public String nextString() throws IOException {
        JsonToken token = peek();
        if (token != JsonToken.STRING && token != JsonToken.NUMBER) {
            throw new IllegalStateException("Expected STRING but was " + token);
        }
        String value = peekedString;
        peeked = null;
        peekedString = null;
        return value;
    }

    public boolean nextBoolean() throws IOException {
        JsonToken token = peek();
        if (token != JsonToken.BOOLEAN) {
            throw new IllegalStateException("Expected BOOLEAN but was " + token);
        }
        boolean value = Boolean.parseBoolean(peekedString);
        peeked = null;
        peekedString = null;
        return value;
    }

    public void nextNull() throws IOException {
        JsonToken token = peek();
        if (token != JsonToken.NULL) {
            throw new IllegalStateException("Expected NULL but was " + token);
        }
        peeked = null;
        peekedString = null;
    }

    public double nextDouble() throws IOException {
        JsonToken token = peek();
        if (token != JsonToken.NUMBER && token != JsonToken.STRING) {
            throw new IllegalStateException("Expected NUMBER but was " + token);
        }
        double value = Double.parseDouble(peekedString);
        peeked = null;
        peekedString = null;
        return value;
    }

    public long nextLong() throws IOException {
        JsonToken token = peek();
        if (token != JsonToken.NUMBER && token != JsonToken.STRING) {
            throw new IllegalStateException("Expected NUMBER but was " + token);
        }
        long value = Long.parseLong(peekedString);
        peeked = null;
        peekedString = null;
        return value;
    }

    public int nextInt() throws IOException {
        JsonToken token = peek();
        if (token != JsonToken.NUMBER && token != JsonToken.STRING) {
            throw new IllegalStateException("Expected NUMBER but was " + token);
        }
        int value = Integer.parseInt(peekedString);
        peeked = null;
        peekedString = null;
        return value;
    }

    public void skipValue() throws IOException {
        int depth = 0;
        do {
            JsonToken token = peek();
            peeked = null;
            peekedString = null;
            if (token == JsonToken.BEGIN_ARRAY || token == JsonToken.BEGIN_OBJECT) {
                depth++;
            } else if (token == JsonToken.END_ARRAY || token == JsonToken.END_OBJECT) {
                depth--;
            } else if (token == JsonToken.END_DOCUMENT) {
                return;
            }
        } while (depth != 0);
    }

    @Override
    public void close() throws IOException {
        peeked = null;
        peekedString = null;
        stack.clear();
        in.close();
    }

    // ---- Internal parser ----

    private int readChar = -2; // -2 = not buffered

    private int nextChar() throws IOException {
        if (readChar != -2) {
            int c = readChar;
            readChar = -2;
            return c;
        }
        return in.read();
    }

    private void pushBack(int c) {
        readChar = c;
    }

    private void skipWhitespace() throws IOException {
        int c;
        while ((c = nextChar()) != -1) {
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                pushBack(c);
                return;
            }
        }
    }

    private JsonToken advance() throws IOException {
        skipWhitespace();
        int c = nextChar();
        if (c == -1) {
            return JsonToken.END_DOCUMENT;
        }
        // Skip comma/colon separators
        if (c == ',' || c == ':') {
            skipWhitespace();
            c = nextChar();
            if (c == -1) return JsonToken.END_DOCUMENT;
        }
        switch (c) {
            case '{':
                return JsonToken.BEGIN_OBJECT;
            case '}':
                return JsonToken.END_OBJECT;
            case '[':
                return JsonToken.BEGIN_ARRAY;
            case ']':
                return JsonToken.END_ARRAY;
            case '"': {
                String s = readString();
                // Determine if this is a name (next non-ws char is ':')
                skipWhitespace();
                int next = nextChar();
                if (next == ':') {
                    peekedString = s;
                    return JsonToken.NAME;
                } else {
                    if (next != -1) pushBack(next);
                    peekedString = s;
                    return JsonToken.STRING;
                }
            }
            case 't': {
                consume("rue");
                peekedString = "true";
                return JsonToken.BOOLEAN;
            }
            case 'f': {
                consume("alse");
                peekedString = "false";
                return JsonToken.BOOLEAN;
            }
            case 'n': {
                consume("ull");
                peekedString = null;
                return JsonToken.NULL;
            }
            default: {
                // number
                StringBuilder sb = new StringBuilder();
                sb.append((char) c);
                int ch;
                while ((ch = nextChar()) != -1) {
                    if (ch == ',' || ch == '}' || ch == ']' || ch == ' '
                            || ch == '\t' || ch == '\r' || ch == '\n') {
                        pushBack(ch);
                        break;
                    }
                    sb.append((char) ch);
                }
                peekedString = sb.toString();
                return JsonToken.NUMBER;
            }
        }
    }

    private String readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = nextChar()) != -1 && c != '"') {
            if (c == '\\') {
                int esc = nextChar();
                switch (esc) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u': {
                        char[] hex = new char[4];
                        for (int i = 0; i < 4; i++) hex[i] = (char) nextChar();
                        sb.append((char) Integer.parseInt(new String(hex), 16));
                        break;
                    }
                    default: sb.append((char) esc);
                }
            } else {
                sb.append((char) c);
            }
        }
        return sb.toString();
    }

    private void consume(String expected) throws IOException {
        for (int i = 0; i < expected.length(); i++) {
            int c = nextChar();
            if (c != expected.charAt(i)) {
                throw new IOException("Malformed JSON: expected '" + expected + "'");
            }
        }
    }

    private void expect(JsonToken expected) throws IOException {
        JsonToken actual = peek();
        if (actual != expected) {
            throw new IllegalStateException("Expected " + expected + " but was " + actual);
        }
        peeked = null;
        peekedString = null;
    }
}
