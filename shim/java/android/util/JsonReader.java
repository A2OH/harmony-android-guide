package android.util;

public final class JsonReader {
    public JsonReader() {}

    public void beginArray() {}
    public void beginObject() {}
    public void close() {}
    public void endArray() {}
    public void endObject() {}
    public boolean hasNext() { return false; }
    public boolean isLenient() { return false; }
    public boolean nextBoolean() { return false; }
    public double nextDouble() { return 0.0; }
    public int nextInt() { return 0; }
    public long nextLong() { return 0L; }
    public Object nextName() { return null; }
    public void nextNull() {}
    public Object nextString() { return null; }
    public Object peek() { return null; }
    public void setLenient(Object p0) {}
    public void skipValue() {}
}
