# Android 11 (API 30) Public API Enumeration: Java Standard

Generated from `frameworks/base/api/current.txt`

## Summary

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `java.awt.font` | 3 | 12 | 79 | 1 |
| `java.beans` | 5 | 23 | 0 | 4 |
| `java.io` | 79 | 441 | 79 | 141 |
| `java.lang` | 97 | 670 | 345 | 201 |
| `java.lang.annotation` | 6 | 11 | 0 | 5 |
| `java.lang.invoke` | 11 | 102 | 13 | 13 |
| `java.lang.ref` | 5 | 8 | 0 | 6 |
| `java.lang.reflect` | 24 | 85 | 15 | 15 |
| `java.math` | 4 | 72 | 15 | 25 |
| `java.net` | 64 | 445 | 87 | 90 |
| `java.nio` | 14 | 175 | 2 | 4 |
| `java.nio.channels` | 54 | 188 | 7 | 42 |
| `java.nio.channels.spi` | 6 | 39 | 0 | 6 |
| `java.nio.charset` | 12 | 77 | 11 | 10 |
| `java.nio.charset.spi` | 1 | 2 | 0 | 1 |
| `java.nio.file` | 47 | 160 | 4 | 34 |
| `java.nio.file.attribute` | 25 | 64 | 3 | 2 |
| `java.nio.file.spi` | 2 | 29 | 0 | 2 |
| `java.security` | 85 | 333 | 11 | 113 |
| `java.security.acl` | 8 | 25 | 0 | 3 |
| `java.security.cert` | 53 | 283 | 0 | 70 |
| `java.security.interfaces` | 13 | 27 | 8 | 0 |
| `java.security.spec` | 27 | 65 | 9 | 32 |
| `java.sql` | 48 | 793 | 134 | 139 |
| `java.text` | 30 | 265 | 43 | 44 |
| `java.time` | 18 | 606 | 23 | 3 |
| `java.time.chrono` | 21 | 317 | 10 | 1 |
| `java.time.format` | 8 | 85 | 16 | 3 |
| `java.time.temporal` | 16 | 99 | 12 | 2 |
| `java.time.zone` | 5 | 39 | 0 | 2 |
| `java.util` | 124 | 915 | 86 | 183 |
| `java.util.concurrent` | 71 | 680 | 1 | 97 |
| `java.util.concurrent.atomic` | 16 | 194 | 0 | 23 |
| `java.util.concurrent.locks` | 14 | 177 | 0 | 12 |
| `java.util.function` | 43 | 78 | 0 | 0 |
| `java.util.jar` | 11 | 40 | 200 | 21 |
| `java.util.logging` | 17 | 117 | 7 | 23 |
| `java.util.prefs` | 9 | 94 | 5 | 9 |
| `java.util.regex` | 4 | 29 | 9 | 1 |
| `java.util.stream` | 13 | 233 | 0 | 0 |
| `java.util.zip` | 20 | 100 | 190 | 47 |
| **TOTAL** | **1133** | **8197** | **1424** | **1430** |

---

## Package: `java.awt.font`

### `class final NumericShaper`

- **Implements:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ALL_RANGES = 524287` |  |
| `static final int ARABIC = 2` |  |
| `static final int BENGALI = 16` |  |
| `static final int DEVANAGARI = 8` |  |
| `static final int EASTERN_ARABIC = 4` |  |
| `static final int ETHIOPIC = 65536` |  |
| `static final int EUROPEAN = 1` |  |
| `static final int GUJARATI = 64` |  |
| `static final int GURMUKHI = 32` |  |
| `static final int KANNADA = 1024` |  |
| `static final int KHMER = 131072` |  |
| `static final int LAO = 8192` |  |
| `static final int MALAYALAM = 2048` |  |
| `static final int MONGOLIAN = 262144` |  |
| `static final int MYANMAR = 32768` |  |
| `static final int ORIYA = 128` |  |
| `static final int TAMIL = 256` |  |
| `static final int TELUGU = 512` |  |
| `static final int THAI = 4096` |  |
| `static final int TIBETAN = 16384` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.awt.font.NumericShaper getContextualShaper(int)` |  |
| `static java.awt.font.NumericShaper getContextualShaper(java.util.Set<java.awt.font.NumericShaper.Range>)` |  |
| `static java.awt.font.NumericShaper getContextualShaper(int, int)` |  |
| `static java.awt.font.NumericShaper getContextualShaper(java.util.Set<java.awt.font.NumericShaper.Range>, java.awt.font.NumericShaper.Range)` |  |
| `java.util.Set<java.awt.font.NumericShaper.Range> getRangeSet()` |  |
| `int getRanges()` |  |
| `static java.awt.font.NumericShaper getShaper(int)` |  |
| `static java.awt.font.NumericShaper getShaper(java.awt.font.NumericShaper.Range)` |  |
| `boolean isContextual()` |  |
| `void shape(char[], int, int)` |  |
| `void shape(char[], int, int, int)` |  |
| `void shape(char[], int, int, java.awt.font.NumericShaper.Range)` |  |

---

### `enum NumericShaper.Range`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.awt.font.NumericShaper.Range ARABIC` |  |
| `static final java.awt.font.NumericShaper.Range BALINESE` |  |
| `static final java.awt.font.NumericShaper.Range BENGALI` |  |
| `static final java.awt.font.NumericShaper.Range CHAM` |  |
| `static final java.awt.font.NumericShaper.Range DEVANAGARI` |  |
| `static final java.awt.font.NumericShaper.Range EASTERN_ARABIC` |  |
| `static final java.awt.font.NumericShaper.Range ETHIOPIC` |  |
| `static final java.awt.font.NumericShaper.Range EUROPEAN` |  |
| `static final java.awt.font.NumericShaper.Range GUJARATI` |  |
| `static final java.awt.font.NumericShaper.Range GURMUKHI` |  |
| `static final java.awt.font.NumericShaper.Range JAVANESE` |  |
| `static final java.awt.font.NumericShaper.Range KANNADA` |  |
| `static final java.awt.font.NumericShaper.Range KAYAH_LI` |  |
| `static final java.awt.font.NumericShaper.Range KHMER` |  |
| `static final java.awt.font.NumericShaper.Range LAO` |  |
| `static final java.awt.font.NumericShaper.Range LEPCHA` |  |
| `static final java.awt.font.NumericShaper.Range LIMBU` |  |
| `static final java.awt.font.NumericShaper.Range MALAYALAM` |  |
| `static final java.awt.font.NumericShaper.Range MEETEI_MAYEK` |  |
| `static final java.awt.font.NumericShaper.Range MONGOLIAN` |  |
| `static final java.awt.font.NumericShaper.Range MYANMAR` |  |
| `static final java.awt.font.NumericShaper.Range MYANMAR_SHAN` |  |
| `static final java.awt.font.NumericShaper.Range NEW_TAI_LUE` |  |
| `static final java.awt.font.NumericShaper.Range NKO` |  |
| `static final java.awt.font.NumericShaper.Range OL_CHIKI` |  |
| `static final java.awt.font.NumericShaper.Range ORIYA` |  |
| `static final java.awt.font.NumericShaper.Range SAURASHTRA` |  |
| `static final java.awt.font.NumericShaper.Range SUNDANESE` |  |
| `static final java.awt.font.NumericShaper.Range TAI_THAM_HORA` |  |
| `static final java.awt.font.NumericShaper.Range TAI_THAM_THAM` |  |
| `static final java.awt.font.NumericShaper.Range TAMIL` |  |
| `static final java.awt.font.NumericShaper.Range TELUGU` |  |
| `static final java.awt.font.NumericShaper.Range THAI` |  |
| `static final java.awt.font.NumericShaper.Range TIBETAN` |  |
| `static final java.awt.font.NumericShaper.Range VAI` |  |

---

### `class final TextAttribute`

- **Extends:** `java.text.AttributedCharacterIterator.Attribute`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextAttribute(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.awt.font.TextAttribute BACKGROUND` |  |
| `static final java.awt.font.TextAttribute BIDI_EMBEDDING` |  |
| `static final java.awt.font.TextAttribute CHAR_REPLACEMENT` |  |
| `static final java.awt.font.TextAttribute FAMILY` |  |
| `static final java.awt.font.TextAttribute FONT` |  |
| `static final java.awt.font.TextAttribute FOREGROUND` |  |
| `static final java.awt.font.TextAttribute INPUT_METHOD_HIGHLIGHT` |  |
| `static final java.awt.font.TextAttribute INPUT_METHOD_UNDERLINE` |  |
| `static final java.awt.font.TextAttribute JUSTIFICATION` |  |
| `static final Float JUSTIFICATION_FULL` |  |
| `static final Float JUSTIFICATION_NONE` |  |
| `static final java.awt.font.TextAttribute KERNING` |  |
| `static final Integer KERNING_ON` |  |
| `static final java.awt.font.TextAttribute LIGATURES` |  |
| `static final Integer LIGATURES_ON` |  |
| `static final java.awt.font.TextAttribute NUMERIC_SHAPING` |  |
| `static final java.awt.font.TextAttribute POSTURE` |  |
| `static final Float POSTURE_OBLIQUE` |  |
| `static final Float POSTURE_REGULAR` |  |
| `static final java.awt.font.TextAttribute RUN_DIRECTION` |  |
| `static final Boolean RUN_DIRECTION_LTR` |  |
| `static final Boolean RUN_DIRECTION_RTL` |  |
| `static final java.awt.font.TextAttribute SIZE` |  |
| `static final java.awt.font.TextAttribute STRIKETHROUGH` |  |
| `static final Boolean STRIKETHROUGH_ON` |  |
| `static final java.awt.font.TextAttribute SUPERSCRIPT` |  |
| `static final Integer SUPERSCRIPT_SUB` |  |
| `static final Integer SUPERSCRIPT_SUPER` |  |
| `static final java.awt.font.TextAttribute SWAP_COLORS` |  |
| `static final Boolean SWAP_COLORS_ON` |  |
| `static final java.awt.font.TextAttribute TRACKING` |  |
| `static final Float TRACKING_LOOSE` |  |
| `static final Float TRACKING_TIGHT` |  |
| `static final java.awt.font.TextAttribute TRANSFORM` |  |
| `static final java.awt.font.TextAttribute UNDERLINE` |  |
| `static final Integer UNDERLINE_LOW_DASHED` |  |
| `static final Integer UNDERLINE_LOW_DOTTED` |  |
| `static final Integer UNDERLINE_LOW_GRAY` |  |
| `static final Integer UNDERLINE_LOW_ONE_PIXEL` |  |
| `static final Integer UNDERLINE_LOW_TWO_PIXEL` |  |
| `static final Integer UNDERLINE_ON` |  |
| `static final java.awt.font.TextAttribute WEIGHT` |  |
| `static final Float WEIGHT_BOLD` |  |
| `static final Float WEIGHT_DEMIBOLD` |  |
| `static final Float WEIGHT_DEMILIGHT` |  |
| `static final Float WEIGHT_EXTRABOLD` |  |
| `static final Float WEIGHT_EXTRA_LIGHT` |  |
| `static final Float WEIGHT_HEAVY` |  |
| `static final Float WEIGHT_LIGHT` |  |
| `static final Float WEIGHT_MEDIUM` |  |
| `static final Float WEIGHT_REGULAR` |  |
| `static final Float WEIGHT_SEMIBOLD` |  |
| `static final Float WEIGHT_ULTRABOLD` |  |
| `static final java.awt.font.TextAttribute WIDTH` |  |
| `static final Float WIDTH_CONDENSED` |  |
| `static final Float WIDTH_EXTENDED` |  |
| `static final Float WIDTH_REGULAR` |  |
| `static final Float WIDTH_SEMI_CONDENSED` |  |
| `static final Float WIDTH_SEMI_EXTENDED` |  |

---

## Package: `java.beans`

### `class IndexedPropertyChangeEvent`

- **Extends:** `java.beans.PropertyChangeEvent`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IndexedPropertyChangeEvent(Object, String, Object, Object, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getIndex()` |  |

---

### `class PropertyChangeEvent`

- **Extends:** `java.util.EventObject`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PropertyChangeEvent(Object, String, Object, Object)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object getNewValue()` |  |
| `Object getOldValue()` |  |
| `Object getPropagationId()` |  |
| `String getPropertyName()` |  |
| `void setPropagationId(Object)` |  |

---

### `interface PropertyChangeListener`

- **Extends:** `java.util.EventListener`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void propertyChange(java.beans.PropertyChangeEvent)` |  |

---

### `class PropertyChangeListenerProxy`

- **Extends:** `java.util.EventListenerProxy<java.beans.PropertyChangeListener>`
- **Implements:** `java.beans.PropertyChangeListener`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PropertyChangeListenerProxy(String, java.beans.PropertyChangeListener)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getPropertyName()` |  |
| `void propertyChange(java.beans.PropertyChangeEvent)` |  |

---

### `class PropertyChangeSupport`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PropertyChangeSupport(Object)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addPropertyChangeListener(java.beans.PropertyChangeListener)` |  |
| `void addPropertyChangeListener(String, java.beans.PropertyChangeListener)` |  |
| `void fireIndexedPropertyChange(String, int, Object, Object)` |  |
| `void fireIndexedPropertyChange(String, int, int, int)` |  |
| `void fireIndexedPropertyChange(String, int, boolean, boolean)` |  |
| `void firePropertyChange(String, Object, Object)` |  |
| `void firePropertyChange(String, int, int)` |  |
| `void firePropertyChange(String, boolean, boolean)` |  |
| `void firePropertyChange(java.beans.PropertyChangeEvent)` |  |
| `java.beans.PropertyChangeListener[] getPropertyChangeListeners()` |  |
| `java.beans.PropertyChangeListener[] getPropertyChangeListeners(String)` |  |
| `boolean hasListeners(String)` |  |
| `void removePropertyChangeListener(java.beans.PropertyChangeListener)` |  |
| `void removePropertyChangeListener(String, java.beans.PropertyChangeListener)` |  |

---

## Package: `java.io`

### `class BufferedInputStream`

- **Extends:** `java.io.FilterInputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BufferedInputStream(java.io.InputStream)` |  |
| `BufferedInputStream(java.io.InputStream, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `volatile byte[] buf` |  |
| `int count` |  |
| `int marklimit` |  |
| `int markpos` |  |
| `int pos` |  |

---

### `class BufferedOutputStream`

- **Extends:** `java.io.FilterOutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BufferedOutputStream(java.io.OutputStream)` |  |
| `BufferedOutputStream(java.io.OutputStream, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] buf` |  |
| `int count` |  |

---

### `class BufferedReader`

- **Extends:** `java.io.Reader`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BufferedReader(java.io.Reader, int)` |  |
| `BufferedReader(java.io.Reader)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `java.util.stream.Stream<java.lang.String> lines()` |  |
| `int read(char[], int, int) throws java.io.IOException` |  |
| `String readLine() throws java.io.IOException` |  |

---

### `class BufferedWriter`

- **Extends:** `java.io.Writer`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BufferedWriter(java.io.Writer)` |  |
| `BufferedWriter(java.io.Writer, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void flush() throws java.io.IOException` |  |
| `void newLine() throws java.io.IOException` |  |
| `void write(char[], int, int) throws java.io.IOException` |  |

---

### `class ByteArrayInputStream`

- **Extends:** `java.io.InputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ByteArrayInputStream(byte[])` |  |
| `ByteArrayInputStream(byte[], int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] buf` |  |
| `int count` |  |
| `int mark` |  |
| `int pos` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int available()` |  |
| `int read()` |  |
| `int read(byte[], int, int)` |  |
| `void reset()` |  |
| `long skip(long)` |  |

---

### `class ByteArrayOutputStream`

- **Extends:** `java.io.OutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ByteArrayOutputStream()` |  |
| `ByteArrayOutputStream(int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int count` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void reset()` |  |
| `int size()` |  |
| `void write(int)` |  |
| `void write(@NonNull byte[], int, int)` |  |
| `void writeTo(@NonNull java.io.OutputStream) throws java.io.IOException` |  |

---

### `class CharArrayReader`

- **Extends:** `java.io.Reader`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CharArrayReader(char[])` |  |
| `CharArrayReader(char[], int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `char[] buf` |  |
| `int count` |  |
| `int markedPos` |  |
| `int pos` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `int read(char[], int, int) throws java.io.IOException` |  |

---

### `class CharArrayWriter`

- **Extends:** `java.io.Writer`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CharArrayWriter()` |  |
| `CharArrayWriter(int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `char[] buf` |  |
| `int count` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.io.CharArrayWriter append(CharSequence)` |  |
| `java.io.CharArrayWriter append(CharSequence, int, int)` |  |
| `java.io.CharArrayWriter append(char)` |  |
| `void close()` |  |
| `void flush()` |  |
| `void reset()` |  |
| `int size()` |  |
| `char[] toCharArray()` |  |
| `void write(int)` |  |
| `void write(char[], int, int)` |  |
| `void write(String, int, int)` |  |
| `void writeTo(java.io.Writer) throws java.io.IOException` |  |

---

### `class CharConversionException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CharConversionException()` |  |
| `CharConversionException(String)` |  |

---

### `interface Closeable`

- **Extends:** `java.lang.AutoCloseable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |

---

### `class final Console`

- **Implements:** `java.io.Flushable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void flush()` |  |
| `java.io.Console format(String, java.lang.Object...)` |  |
| `java.io.Console printf(String, java.lang.Object...)` |  |
| `String readLine(String, java.lang.Object...)` |  |
| `String readLine()` |  |
| `char[] readPassword(String, java.lang.Object...)` |  |
| `char[] readPassword()` |  |
| `java.io.Reader reader()` |  |
| `java.io.PrintWriter writer()` |  |

---

### `interface DataInput`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean readBoolean() throws java.io.IOException` |  |
| `byte readByte() throws java.io.IOException` |  |
| `char readChar() throws java.io.IOException` |  |
| `double readDouble() throws java.io.IOException` |  |
| `float readFloat() throws java.io.IOException` |  |
| `void readFully(byte[]) throws java.io.IOException` |  |
| `void readFully(byte[], int, int) throws java.io.IOException` |  |
| `int readInt() throws java.io.IOException` |  |
| `String readLine() throws java.io.IOException` |  |
| `long readLong() throws java.io.IOException` |  |
| `short readShort() throws java.io.IOException` |  |
| `String readUTF() throws java.io.IOException` |  |
| `int readUnsignedByte() throws java.io.IOException` |  |
| `int readUnsignedShort() throws java.io.IOException` |  |
| `int skipBytes(int) throws java.io.IOException` |  |

---

### `class DataInputStream`

- **Extends:** `java.io.FilterInputStream`
- **Implements:** `java.io.DataInput`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DataInputStream(java.io.InputStream)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int read(byte[]) throws java.io.IOException` |  |
| `final int read(byte[], int, int) throws java.io.IOException` |  |
| `final boolean readBoolean() throws java.io.IOException` |  |
| `final byte readByte() throws java.io.IOException` |  |
| `final char readChar() throws java.io.IOException` |  |
| `final double readDouble() throws java.io.IOException` |  |
| `final float readFloat() throws java.io.IOException` |  |
| `final void readFully(byte[]) throws java.io.IOException` |  |
| `final void readFully(byte[], int, int) throws java.io.IOException` |  |
| `final int readInt() throws java.io.IOException` |  |
| `final long readLong() throws java.io.IOException` |  |
| `final short readShort() throws java.io.IOException` |  |
| `final String readUTF() throws java.io.IOException` |  |
| `static final String readUTF(java.io.DataInput) throws java.io.IOException` |  |
| `final int readUnsignedByte() throws java.io.IOException` |  |
| `final int readUnsignedShort() throws java.io.IOException` |  |
| `final int skipBytes(int) throws java.io.IOException` |  |

---

### `interface DataOutput`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void write(int) throws java.io.IOException` |  |
| `void write(byte[]) throws java.io.IOException` |  |
| `void write(byte[], int, int) throws java.io.IOException` |  |
| `void writeBoolean(boolean) throws java.io.IOException` |  |
| `void writeByte(int) throws java.io.IOException` |  |
| `void writeBytes(String) throws java.io.IOException` |  |
| `void writeChar(int) throws java.io.IOException` |  |
| `void writeChars(String) throws java.io.IOException` |  |
| `void writeDouble(double) throws java.io.IOException` |  |
| `void writeFloat(float) throws java.io.IOException` |  |
| `void writeInt(int) throws java.io.IOException` |  |
| `void writeLong(long) throws java.io.IOException` |  |
| `void writeShort(int) throws java.io.IOException` |  |
| `void writeUTF(String) throws java.io.IOException` |  |

---

### `class DataOutputStream`

- **Extends:** `java.io.FilterOutputStream`
- **Implements:** `java.io.DataOutput`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DataOutputStream(java.io.OutputStream)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int written` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int size()` |  |
| `final void writeBoolean(boolean) throws java.io.IOException` |  |
| `final void writeByte(int) throws java.io.IOException` |  |
| `final void writeBytes(String) throws java.io.IOException` |  |
| `final void writeChar(int) throws java.io.IOException` |  |
| `final void writeChars(String) throws java.io.IOException` |  |
| `final void writeDouble(double) throws java.io.IOException` |  |
| `final void writeFloat(float) throws java.io.IOException` |  |
| `final void writeInt(int) throws java.io.IOException` |  |
| `final void writeLong(long) throws java.io.IOException` |  |
| `final void writeShort(int) throws java.io.IOException` |  |
| `final void writeUTF(String) throws java.io.IOException` |  |

---

### `class EOFException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EOFException()` |  |
| `EOFException(String)` |  |

---

### `interface Externalizable`

- **Extends:** `java.io.Serializable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void readExternal(java.io.ObjectInput) throws java.lang.ClassNotFoundException, java.io.IOException` |  |
| `void writeExternal(java.io.ObjectOutput) throws java.io.IOException` |  |

---

### `class File`

- **Implements:** `java.lang.Comparable<java.io.File> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `File(@NonNull String)` |  |
| `File(@Nullable String, @NonNull String)` |  |
| `File(@Nullable java.io.File, @NonNull String)` |  |
| `File(@NonNull java.net.URI)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final char pathSeparatorChar` |  |
| `static final char separatorChar` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean canExecute()` |  |
| `boolean canRead()` |  |
| `boolean canWrite()` |  |
| `int compareTo(@NonNull java.io.File)` |  |
| `boolean createNewFile() throws java.io.IOException` |  |
| `boolean delete()` |  |
| `void deleteOnExit()` |  |
| `boolean exists()` |  |
| `long getFreeSpace()` |  |
| `long getTotalSpace()` |  |
| `long getUsableSpace()` |  |
| `boolean isAbsolute()` |  |
| `boolean isDirectory()` |  |
| `boolean isFile()` |  |
| `boolean isHidden()` |  |
| `long lastModified()` |  |
| `long length()` |  |
| `boolean mkdir()` |  |
| `boolean mkdirs()` |  |
| `boolean renameTo(@NonNull java.io.File)` |  |
| `boolean setExecutable(boolean, boolean)` |  |
| `boolean setExecutable(boolean)` |  |
| `boolean setLastModified(long)` |  |
| `boolean setReadOnly()` |  |
| `boolean setReadable(boolean, boolean)` |  |
| `boolean setReadable(boolean)` |  |
| `boolean setWritable(boolean, boolean)` |  |
| `boolean setWritable(boolean)` |  |

---

### `class final FileDescriptor`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileDescriptor()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.io.FileDescriptor err` |  |
| `static final java.io.FileDescriptor in` |  |
| `static final java.io.FileDescriptor out` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void sync() throws java.io.SyncFailedException` |  |
| `boolean valid()` |  |

---

### `interface FileFilter`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean accept(java.io.File)` |  |

---

### `class FileInputStream`

- **Extends:** `java.io.InputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileInputStream(String) throws java.io.FileNotFoundException` |  |
| `FileInputStream(java.io.File) throws java.io.FileNotFoundException` |  |
| `FileInputStream(java.io.FileDescriptor)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finalize() throws java.io.IOException` |  |
| `java.nio.channels.FileChannel getChannel()` |  |
| `final java.io.FileDescriptor getFD() throws java.io.IOException` |  |
| `int read() throws java.io.IOException` |  |

---

### `class FileNotFoundException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileNotFoundException()` |  |
| `FileNotFoundException(String)` |  |

---

### `class FileOutputStream`

- **Extends:** `java.io.OutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileOutputStream(String) throws java.io.FileNotFoundException` |  |
| `FileOutputStream(String, boolean) throws java.io.FileNotFoundException` |  |
| `FileOutputStream(java.io.File) throws java.io.FileNotFoundException` |  |
| `FileOutputStream(java.io.File, boolean) throws java.io.FileNotFoundException` |  |
| `FileOutputStream(java.io.FileDescriptor)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finalize() throws java.io.IOException` |  |
| `java.nio.channels.FileChannel getChannel()` |  |
| `final java.io.FileDescriptor getFD() throws java.io.IOException` |  |
| `void write(int) throws java.io.IOException` |  |

---

### `class final FilePermission`

- **Extends:** `java.security.Permission`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FilePermission(String, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getActions()` |  |
| `boolean implies(java.security.Permission)` |  |

---

### `class FileReader`

- **Extends:** `java.io.InputStreamReader`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileReader(String) throws java.io.FileNotFoundException` |  |
| `FileReader(java.io.File) throws java.io.FileNotFoundException` |  |
| `FileReader(java.io.FileDescriptor)` |  |

---

### `class FileWriter`

- **Extends:** `java.io.OutputStreamWriter`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileWriter(String) throws java.io.IOException` |  |
| `FileWriter(String, boolean) throws java.io.IOException` |  |
| `FileWriter(java.io.File) throws java.io.IOException` |  |
| `FileWriter(java.io.File, boolean) throws java.io.IOException` |  |
| `FileWriter(java.io.FileDescriptor)` |  |

---

### `interface FilenameFilter`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean accept(java.io.File, String)` |  |

---

### `class FilterInputStream`

- **Extends:** `java.io.InputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FilterInputStream(java.io.InputStream)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `volatile java.io.InputStream in` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int read() throws java.io.IOException` |  |

---

### `class FilterOutputStream`

- **Extends:** `java.io.OutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FilterOutputStream(java.io.OutputStream)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.io.OutputStream out` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void write(int) throws java.io.IOException` |  |

---

### `class abstract FilterReader`

- **Extends:** `java.io.Reader`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FilterReader(java.io.Reader)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.io.Reader in` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `int read(char[], int, int) throws java.io.IOException` |  |

---

### `class abstract FilterWriter`

- **Extends:** `java.io.Writer`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FilterWriter(java.io.Writer)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.io.Writer out` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void flush() throws java.io.IOException` |  |
| `void write(char[], int, int) throws java.io.IOException` |  |

---

### `interface Flushable`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void flush() throws java.io.IOException` |  |

---

### `class IOError`

- **Extends:** `java.lang.Error`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IOError(Throwable)` |  |

---

### `class IOException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IOException()` |  |
| `IOException(String)` |  |
| `IOException(String, Throwable)` |  |
| `IOException(Throwable)` |  |

---

### `class abstract InputStream`

- **Implements:** `java.io.Closeable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InputStream()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int available() throws java.io.IOException` |  |
| `void close() throws java.io.IOException` |  |
| `void mark(int)` |  |
| `boolean markSupported()` |  |
| `abstract int read() throws java.io.IOException` |  |
| `int read(byte[]) throws java.io.IOException` |  |
| `int read(byte[], int, int) throws java.io.IOException` |  |
| `void reset() throws java.io.IOException` |  |
| `long skip(long) throws java.io.IOException` |  |

---

### `class InputStreamReader`

- **Extends:** `java.io.Reader`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InputStreamReader(java.io.InputStream)` |  |
| `InputStreamReader(java.io.InputStream, String) throws java.io.UnsupportedEncodingException` |  |
| `InputStreamReader(java.io.InputStream, java.nio.charset.Charset)` |  |
| `InputStreamReader(java.io.InputStream, java.nio.charset.CharsetDecoder)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `String getEncoding()` |  |
| `int read(char[], int, int) throws java.io.IOException` |  |

---

### `class InterruptedIOException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InterruptedIOException()` |  |
| `InterruptedIOException(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int bytesTransferred` |  |

---

### `class InvalidClassException`

- **Extends:** `java.io.ObjectStreamException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidClassException(String)` |  |
| `InvalidClassException(String, String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `String classname` |  |

---

### `class InvalidObjectException`

- **Extends:** `java.io.ObjectStreamException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidObjectException(String)` |  |

---

### `class LineNumberInputStream` ~~DEPRECATED~~

- **Extends:** `java.io.FilterInputStream`
- **Annotations:** `@Deprecated`

---

### `class LineNumberReader`

- **Extends:** `java.io.BufferedReader`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LineNumberReader(java.io.Reader)` |  |
| `LineNumberReader(java.io.Reader, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getLineNumber()` |  |
| `void setLineNumber(int)` |  |

---

### `class NotActiveException`

- **Extends:** `java.io.ObjectStreamException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NotActiveException(String)` |  |
| `NotActiveException()` |  |

---

### `class NotSerializableException`

- **Extends:** `java.io.ObjectStreamException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NotSerializableException(String)` |  |
| `NotSerializableException()` |  |

---

### `interface ObjectInput`

- **Extends:** `java.io.DataInput java.lang.AutoCloseable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int available() throws java.io.IOException` |  |
| `void close() throws java.io.IOException` |  |
| `int read() throws java.io.IOException` |  |
| `int read(byte[]) throws java.io.IOException` |  |
| `int read(byte[], int, int) throws java.io.IOException` |  |
| `Object readObject() throws java.lang.ClassNotFoundException, java.io.IOException` |  |
| `long skip(long) throws java.io.IOException` |  |

---

### `class ObjectInputStream`

- **Extends:** `java.io.InputStream`
- **Implements:** `java.io.ObjectInput java.io.ObjectStreamConstants`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ObjectInputStream(java.io.InputStream) throws java.io.IOException` |  |
| `ObjectInputStream() throws java.io.IOException, java.lang.SecurityException` |  |
| `ObjectInputStream.GetField()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void defaultReadObject() throws java.lang.ClassNotFoundException, java.io.IOException` |  |
| `boolean enableResolveObject(boolean) throws java.lang.SecurityException` |  |
| `int read() throws java.io.IOException` |  |
| `boolean readBoolean() throws java.io.IOException` |  |
| `byte readByte() throws java.io.IOException` |  |
| `char readChar() throws java.io.IOException` |  |
| `java.io.ObjectStreamClass readClassDescriptor() throws java.lang.ClassNotFoundException, java.io.IOException` |  |
| `double readDouble() throws java.io.IOException` |  |
| `java.io.ObjectInputStream.GetField readFields() throws java.lang.ClassNotFoundException, java.io.IOException` |  |
| `float readFloat() throws java.io.IOException` |  |
| `void readFully(byte[]) throws java.io.IOException` |  |
| `void readFully(byte[], int, int) throws java.io.IOException` |  |
| `int readInt() throws java.io.IOException` |  |
| `long readLong() throws java.io.IOException` |  |
| `final Object readObject() throws java.lang.ClassNotFoundException, java.io.IOException` |  |
| `Object readObjectOverride() throws java.lang.ClassNotFoundException, java.io.IOException` |  |
| `short readShort() throws java.io.IOException` |  |
| `void readStreamHeader() throws java.io.IOException, java.io.StreamCorruptedException` |  |
| `String readUTF() throws java.io.IOException` |  |
| `Object readUnshared() throws java.lang.ClassNotFoundException, java.io.IOException` |  |
| `int readUnsignedByte() throws java.io.IOException` |  |
| `int readUnsignedShort() throws java.io.IOException` |  |
| `void registerValidation(java.io.ObjectInputValidation, int) throws java.io.InvalidObjectException, java.io.NotActiveException` |  |
| `Class<?> resolveClass(java.io.ObjectStreamClass) throws java.lang.ClassNotFoundException, java.io.IOException` |  |
| `Object resolveObject(Object) throws java.io.IOException` |  |
| `Class<?> resolveProxyClass(String[]) throws java.lang.ClassNotFoundException, java.io.IOException` |  |
| `int skipBytes(int) throws java.io.IOException` |  |
| `abstract boolean defaulted(String) throws java.io.IOException` |  |
| `abstract boolean get(String, boolean) throws java.io.IOException` |  |
| `abstract byte get(String, byte) throws java.io.IOException` |  |
| `abstract char get(String, char) throws java.io.IOException` |  |
| `abstract short get(String, short) throws java.io.IOException` |  |
| `abstract int get(String, int) throws java.io.IOException` |  |
| `abstract long get(String, long) throws java.io.IOException` |  |
| `abstract float get(String, float) throws java.io.IOException` |  |
| `abstract double get(String, double) throws java.io.IOException` |  |
| `abstract Object get(String, Object) throws java.io.IOException` |  |
| `abstract java.io.ObjectStreamClass getObjectStreamClass()` |  |

---

### `interface ObjectInputValidation`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void validateObject() throws java.io.InvalidObjectException` |  |

---

### `interface ObjectOutput`

- **Extends:** `java.io.DataOutput java.lang.AutoCloseable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void flush() throws java.io.IOException` |  |
| `void writeObject(Object) throws java.io.IOException` |  |

---

### `class ObjectOutputStream`

- **Extends:** `java.io.OutputStream`
- **Implements:** `java.io.ObjectOutput java.io.ObjectStreamConstants`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ObjectOutputStream(java.io.OutputStream) throws java.io.IOException` |  |
| `ObjectOutputStream() throws java.io.IOException, java.lang.SecurityException` |  |
| `ObjectOutputStream.PutField()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void annotateClass(Class<?>) throws java.io.IOException` |  |
| `void annotateProxyClass(Class<?>) throws java.io.IOException` |  |
| `void defaultWriteObject() throws java.io.IOException` |  |
| `void drain() throws java.io.IOException` |  |
| `boolean enableReplaceObject(boolean) throws java.lang.SecurityException` |  |
| `java.io.ObjectOutputStream.PutField putFields() throws java.io.IOException` |  |
| `Object replaceObject(Object) throws java.io.IOException` |  |
| `void reset() throws java.io.IOException` |  |
| `void useProtocolVersion(int) throws java.io.IOException` |  |
| `void write(int) throws java.io.IOException` |  |
| `void writeBoolean(boolean) throws java.io.IOException` |  |
| `void writeByte(int) throws java.io.IOException` |  |
| `void writeBytes(String) throws java.io.IOException` |  |
| `void writeChar(int) throws java.io.IOException` |  |
| `void writeChars(String) throws java.io.IOException` |  |
| `void writeClassDescriptor(java.io.ObjectStreamClass) throws java.io.IOException` |  |
| `void writeDouble(double) throws java.io.IOException` |  |
| `void writeFields() throws java.io.IOException` |  |
| `void writeFloat(float) throws java.io.IOException` |  |
| `void writeInt(int) throws java.io.IOException` |  |
| `void writeLong(long) throws java.io.IOException` |  |
| `final void writeObject(Object) throws java.io.IOException` |  |
| `void writeObjectOverride(Object) throws java.io.IOException` |  |
| `void writeShort(int) throws java.io.IOException` |  |
| `void writeStreamHeader() throws java.io.IOException` |  |
| `void writeUTF(String) throws java.io.IOException` |  |
| `void writeUnshared(Object) throws java.io.IOException` |  |
| `abstract void put(String, boolean)` |  |
| `abstract void put(String, byte)` |  |
| `abstract void put(String, char)` |  |
| `abstract void put(String, short)` |  |
| `abstract void put(String, int)` |  |
| `abstract void put(String, long)` |  |
| `abstract void put(String, float)` |  |
| `abstract void put(String, double)` |  |
| `abstract void put(String, Object)` |  |

---

### `class ObjectStreamClass`

- **Implements:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.io.ObjectStreamField[] NO_FIELDS` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Class<?> forClass()` |  |
| `java.io.ObjectStreamField getField(String)` |  |
| `java.io.ObjectStreamField[] getFields()` |  |
| `String getName()` |  |
| `long getSerialVersionUID()` |  |
| `static java.io.ObjectStreamClass lookup(Class<?>)` |  |
| `static java.io.ObjectStreamClass lookupAny(Class<?>)` |  |

---

### `interface ObjectStreamConstants`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PROTOCOL_VERSION_1 = 1` |  |
| `static final int PROTOCOL_VERSION_2 = 2` |  |
| `static final byte SC_BLOCK_DATA = 8` |  |
| `static final byte SC_ENUM = 16` |  |
| `static final byte SC_EXTERNALIZABLE = 4` |  |
| `static final byte SC_SERIALIZABLE = 2` |  |
| `static final byte SC_WRITE_METHOD = 1` |  |
| `static final short STREAM_MAGIC = -21267` |  |
| `static final short STREAM_VERSION = 5` |  |
| `static final java.io.SerializablePermission SUBCLASS_IMPLEMENTATION_PERMISSION` |  |
| `static final java.io.SerializablePermission SUBSTITUTION_PERMISSION` |  |
| `static final byte TC_ARRAY = 117` |  |
| `static final byte TC_BASE = 112` |  |
| `static final byte TC_BLOCKDATA = 119` |  |
| `static final byte TC_BLOCKDATALONG = 122` |  |
| `static final byte TC_CLASS = 118` |  |
| `static final byte TC_CLASSDESC = 114` |  |
| `static final byte TC_ENDBLOCKDATA = 120` |  |
| `static final byte TC_ENUM = 126` |  |
| `static final byte TC_EXCEPTION = 123` |  |
| `static final byte TC_LONGSTRING = 124` |  |
| `static final byte TC_MAX = 126` |  |
| `static final byte TC_NULL = 112` |  |
| `static final byte TC_OBJECT = 115` |  |
| `static final byte TC_PROXYCLASSDESC = 125` |  |
| `static final byte TC_REFERENCE = 113` |  |
| `static final byte TC_RESET = 121` |  |
| `static final byte TC_STRING = 116` |  |
| `static final int baseWireHandle = 8257536` |  |

---

### `class abstract ObjectStreamException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ObjectStreamException(String)` |  |
| `ObjectStreamException()` |  |

---

### `class ObjectStreamField`

- **Implements:** `java.lang.Comparable<java.lang.Object>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ObjectStreamField(String, Class<?>)` |  |
| `ObjectStreamField(String, Class<?>, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compareTo(Object)` |  |
| `String getName()` |  |
| `int getOffset()` |  |
| `Class<?> getType()` |  |
| `char getTypeCode()` |  |
| `String getTypeString()` |  |
| `boolean isPrimitive()` |  |
| `boolean isUnshared()` |  |
| `void setOffset(int)` |  |

---

### `class OptionalDataException`

- **Extends:** `java.io.ObjectStreamException`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `boolean eof` |  |
| `int length` |  |

---

### `class abstract OutputStream`

- **Implements:** `java.io.Closeable java.io.Flushable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `OutputStream()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void flush() throws java.io.IOException` |  |
| `abstract void write(int) throws java.io.IOException` |  |
| `void write(byte[]) throws java.io.IOException` |  |
| `void write(byte[], int, int) throws java.io.IOException` |  |

---

### `class OutputStreamWriter`

- **Extends:** `java.io.Writer`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `OutputStreamWriter(java.io.OutputStream, String) throws java.io.UnsupportedEncodingException` |  |
| `OutputStreamWriter(java.io.OutputStream)` |  |
| `OutputStreamWriter(java.io.OutputStream, java.nio.charset.Charset)` |  |
| `OutputStreamWriter(java.io.OutputStream, java.nio.charset.CharsetEncoder)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void flush() throws java.io.IOException` |  |
| `String getEncoding()` |  |
| `void write(char[], int, int) throws java.io.IOException` |  |

---

### `class PipedInputStream`

- **Extends:** `java.io.InputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PipedInputStream(java.io.PipedOutputStream) throws java.io.IOException` |  |
| `PipedInputStream(java.io.PipedOutputStream, int) throws java.io.IOException` |  |
| `PipedInputStream()` |  |
| `PipedInputStream(int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PIPE_SIZE = 1024` |  |
| `byte[] buffer` |  |
| `int in` |  |
| `int out` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void connect(java.io.PipedOutputStream) throws java.io.IOException` |  |
| `int read() throws java.io.IOException` |  |
| `void receive(int) throws java.io.IOException` |  |

---

### `class PipedOutputStream`

- **Extends:** `java.io.OutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PipedOutputStream(java.io.PipedInputStream) throws java.io.IOException` |  |
| `PipedOutputStream()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void connect(java.io.PipedInputStream) throws java.io.IOException` |  |
| `void write(int) throws java.io.IOException` |  |

---

### `class PipedReader`

- **Extends:** `java.io.Reader`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PipedReader(java.io.PipedWriter) throws java.io.IOException` |  |
| `PipedReader(java.io.PipedWriter, int) throws java.io.IOException` |  |
| `PipedReader()` |  |
| `PipedReader(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void connect(java.io.PipedWriter) throws java.io.IOException` |  |
| `int read(char[], int, int) throws java.io.IOException` |  |

---

### `class PipedWriter`

- **Extends:** `java.io.Writer`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PipedWriter(java.io.PipedReader) throws java.io.IOException` |  |
| `PipedWriter()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void connect(java.io.PipedReader) throws java.io.IOException` |  |
| `void flush() throws java.io.IOException` |  |
| `void write(char[], int, int) throws java.io.IOException` |  |

---

### `class PrintStream`

- **Extends:** `java.io.FilterOutputStream`
- **Implements:** `java.lang.Appendable java.io.Closeable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrintStream(java.io.OutputStream)` |  |
| `PrintStream(java.io.OutputStream, boolean)` |  |
| `PrintStream(java.io.OutputStream, boolean, String) throws java.io.UnsupportedEncodingException` |  |
| `PrintStream(String) throws java.io.FileNotFoundException` |  |
| `PrintStream(String, String) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException` |  |
| `PrintStream(java.io.File) throws java.io.FileNotFoundException` |  |
| `PrintStream(java.io.File, String) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.io.PrintStream append(CharSequence)` |  |
| `java.io.PrintStream append(CharSequence, int, int)` |  |
| `java.io.PrintStream append(char)` |  |
| `boolean checkError()` |  |
| `void clearError()` |  |
| `void close()` |  |
| `void flush()` |  |
| `java.io.PrintStream format(String, java.lang.Object...)` |  |
| `java.io.PrintStream format(java.util.Locale, String, java.lang.Object...)` |  |
| `void print(boolean)` |  |
| `void print(char)` |  |
| `void print(int)` |  |
| `void print(long)` |  |
| `void print(float)` |  |
| `void print(double)` |  |
| `void print(char[])` |  |
| `void print(String)` |  |
| `void print(Object)` |  |
| `java.io.PrintStream printf(String, java.lang.Object...)` |  |
| `java.io.PrintStream printf(java.util.Locale, String, java.lang.Object...)` |  |
| `void println()` |  |
| `void println(boolean)` |  |
| `void println(char)` |  |
| `void println(int)` |  |
| `void println(long)` |  |
| `void println(float)` |  |
| `void println(double)` |  |
| `void println(char[])` |  |
| `void println(String)` |  |
| `void println(Object)` |  |
| `void setError()` |  |
| `void write(int)` |  |
| `void write(byte[], int, int)` |  |

---

### `class PrintWriter`

- **Extends:** `java.io.Writer`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrintWriter(@NonNull java.io.Writer)` |  |
| `PrintWriter(@NonNull java.io.Writer, boolean)` |  |
| `PrintWriter(@NonNull java.io.OutputStream)` |  |
| `PrintWriter(@NonNull java.io.OutputStream, boolean)` |  |
| `PrintWriter(@NonNull String) throws java.io.FileNotFoundException` |  |
| `PrintWriter(@NonNull String, @NonNull String) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException` |  |
| `PrintWriter(@NonNull java.io.File) throws java.io.FileNotFoundException` |  |
| `PrintWriter(@NonNull java.io.File, @NonNull String) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.io.Writer out` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean checkError()` |  |
| `void clearError()` |  |
| `void close()` |  |
| `void flush()` |  |
| `void print(boolean)` |  |
| `void print(char)` |  |
| `void print(int)` |  |
| `void print(long)` |  |
| `void print(float)` |  |
| `void print(double)` |  |
| `void print(char[])` |  |
| `void print(@Nullable String)` |  |
| `void print(@Nullable Object)` |  |
| `void println()` |  |
| `void println(boolean)` |  |
| `void println(char)` |  |
| `void println(int)` |  |
| `void println(long)` |  |
| `void println(float)` |  |
| `void println(double)` |  |
| `void println(char[])` |  |
| `void println(@Nullable String)` |  |
| `void println(@Nullable Object)` |  |
| `void setError()` |  |
| `void write(int)` |  |
| `void write(char[], int, int)` |  |
| `void write(char[])` |  |
| `void write(@NonNull String, int, int)` |  |
| `void write(@NonNull String)` |  |

---

### `class PushbackInputStream`

- **Extends:** `java.io.FilterInputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PushbackInputStream(java.io.InputStream, int)` |  |
| `PushbackInputStream(java.io.InputStream)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] buf` |  |
| `int pos` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void unread(int) throws java.io.IOException` |  |
| `void unread(byte[], int, int) throws java.io.IOException` |  |
| `void unread(byte[]) throws java.io.IOException` |  |

---

### `class PushbackReader`

- **Extends:** `java.io.FilterReader`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PushbackReader(java.io.Reader, int)` |  |
| `PushbackReader(java.io.Reader)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void unread(int) throws java.io.IOException` |  |
| `void unread(char[], int, int) throws java.io.IOException` |  |
| `void unread(char[]) throws java.io.IOException` |  |

---

### `class RandomAccessFile`

- **Implements:** `java.io.Closeable java.io.DataInput java.io.DataOutput`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RandomAccessFile(String, String) throws java.io.FileNotFoundException` |  |
| `RandomAccessFile(java.io.File, String) throws java.io.FileNotFoundException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `final java.nio.channels.FileChannel getChannel()` |  |
| `final java.io.FileDescriptor getFD() throws java.io.IOException` |  |
| `long getFilePointer() throws java.io.IOException` |  |
| `long length() throws java.io.IOException` |  |
| `int read() throws java.io.IOException` |  |
| `int read(byte[], int, int) throws java.io.IOException` |  |
| `int read(byte[]) throws java.io.IOException` |  |
| `final boolean readBoolean() throws java.io.IOException` |  |
| `final byte readByte() throws java.io.IOException` |  |
| `final char readChar() throws java.io.IOException` |  |
| `final double readDouble() throws java.io.IOException` |  |
| `final float readFloat() throws java.io.IOException` |  |
| `final void readFully(byte[]) throws java.io.IOException` |  |
| `final void readFully(byte[], int, int) throws java.io.IOException` |  |
| `final int readInt() throws java.io.IOException` |  |
| `final String readLine() throws java.io.IOException` |  |
| `final long readLong() throws java.io.IOException` |  |
| `final short readShort() throws java.io.IOException` |  |
| `final String readUTF() throws java.io.IOException` |  |
| `final int readUnsignedByte() throws java.io.IOException` |  |
| `final int readUnsignedShort() throws java.io.IOException` |  |
| `void seek(long) throws java.io.IOException` |  |
| `void setLength(long) throws java.io.IOException` |  |
| `int skipBytes(int) throws java.io.IOException` |  |
| `void write(int) throws java.io.IOException` |  |
| `void write(byte[]) throws java.io.IOException` |  |
| `void write(byte[], int, int) throws java.io.IOException` |  |
| `final void writeBoolean(boolean) throws java.io.IOException` |  |
| `final void writeByte(int) throws java.io.IOException` |  |
| `final void writeBytes(String) throws java.io.IOException` |  |
| `final void writeChar(int) throws java.io.IOException` |  |
| `final void writeChars(String) throws java.io.IOException` |  |
| `final void writeDouble(double) throws java.io.IOException` |  |
| `final void writeFloat(float) throws java.io.IOException` |  |
| `final void writeInt(int) throws java.io.IOException` |  |
| `final void writeLong(long) throws java.io.IOException` |  |
| `final void writeShort(int) throws java.io.IOException` |  |
| `final void writeUTF(String) throws java.io.IOException` |  |

---

### `class abstract Reader`

- **Implements:** `java.io.Closeable java.lang.Readable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Reader()` |  |
| `Reader(Object)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `Object lock` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void mark(int) throws java.io.IOException` |  |
| `boolean markSupported()` |  |
| `int read(java.nio.CharBuffer) throws java.io.IOException` |  |
| `int read() throws java.io.IOException` |  |
| `int read(char[]) throws java.io.IOException` |  |
| `abstract int read(char[], int, int) throws java.io.IOException` |  |
| `boolean ready() throws java.io.IOException` |  |
| `void reset() throws java.io.IOException` |  |
| `long skip(long) throws java.io.IOException` |  |

---

### `class SequenceInputStream`

- **Extends:** `java.io.InputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SequenceInputStream(java.util.Enumeration<? extends java.io.InputStream>)` |  |
| `SequenceInputStream(java.io.InputStream, java.io.InputStream)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int read() throws java.io.IOException` |  |

---

### `interface Serializable`


---

### `class final SerializablePermission`

- **Extends:** `java.security.BasicPermission`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SerializablePermission(String)` |  |
| `SerializablePermission(String, String)` |  |

---

### `class StreamCorruptedException`

- **Extends:** `java.io.ObjectStreamException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StreamCorruptedException(String)` |  |
| `StreamCorruptedException()` |  |

---

### `class StreamTokenizer`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StreamTokenizer(java.io.Reader)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TT_EOF = -1` |  |
| `static final int TT_EOL = 10` |  |
| `static final int TT_NUMBER = -2` |  |
| `static final int TT_WORD = -3` |  |
| `double nval` |  |
| `String sval` |  |
| `int ttype` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void commentChar(int)` |  |
| `void eolIsSignificant(boolean)` |  |
| `int lineno()` |  |
| `void lowerCaseMode(boolean)` |  |
| `int nextToken() throws java.io.IOException` |  |
| `void ordinaryChar(int)` |  |
| `void ordinaryChars(int, int)` |  |
| `void parseNumbers()` |  |
| `void pushBack()` |  |
| `void quoteChar(int)` |  |
| `void resetSyntax()` |  |
| `void slashSlashComments(boolean)` |  |
| `void slashStarComments(boolean)` |  |
| `void whitespaceChars(int, int)` |  |
| `void wordChars(int, int)` |  |

---

### `class StringBufferInputStream` ~~DEPRECATED~~

- **Extends:** `java.io.InputStream`
- **Annotations:** `@Deprecated`

---

### `class StringReader`

- **Extends:** `java.io.Reader`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StringReader(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `int read(char[], int, int) throws java.io.IOException` |  |

---

### `class StringWriter`

- **Extends:** `java.io.Writer`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StringWriter()` |  |
| `StringWriter(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.io.StringWriter append(CharSequence)` |  |
| `java.io.StringWriter append(CharSequence, int, int)` |  |
| `java.io.StringWriter append(char)` |  |
| `void close() throws java.io.IOException` |  |
| `void flush()` |  |
| `StringBuffer getBuffer()` |  |
| `void write(int)` |  |
| `void write(char[], int, int)` |  |
| `void write(String)` |  |
| `void write(String, int, int)` |  |

---

### `class SyncFailedException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SyncFailedException(String)` |  |

---

### `class UTFDataFormatException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UTFDataFormatException()` |  |
| `UTFDataFormatException(String)` |  |

---

### `class UncheckedIOException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UncheckedIOException(String, java.io.IOException)` |  |
| `UncheckedIOException(java.io.IOException)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.io.IOException getCause()` |  |

---

### `class UnsupportedEncodingException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnsupportedEncodingException()` |  |
| `UnsupportedEncodingException(String)` |  |

---

### `class WriteAbortedException`

- **Extends:** `java.io.ObjectStreamException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WriteAbortedException(String, Exception)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `Exception detail` |  |

---

### `class abstract Writer`

- **Implements:** `java.lang.Appendable java.io.Closeable java.io.Flushable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Writer()` |  |
| `Writer(Object)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `Object lock` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.io.Writer append(CharSequence) throws java.io.IOException` |  |
| `java.io.Writer append(CharSequence, int, int) throws java.io.IOException` |  |
| `java.io.Writer append(char) throws java.io.IOException` |  |
| `void write(int) throws java.io.IOException` |  |
| `void write(char[]) throws java.io.IOException` |  |
| `abstract void write(char[], int, int) throws java.io.IOException` |  |
| `void write(String) throws java.io.IOException` |  |
| `void write(String, int, int) throws java.io.IOException` |  |

---

## Package: `java.lang`

### `class AbstractMethodError`

- **Extends:** `java.lang.IncompatibleClassChangeError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractMethodError()` |  |
| `AbstractMethodError(String)` |  |

---

### `interface Appendable`


---

### `class ArithmeticException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ArithmeticException()` |  |
| `ArithmeticException(String)` |  |

---

### `class ArrayIndexOutOfBoundsException`

- **Extends:** `java.lang.IndexOutOfBoundsException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ArrayIndexOutOfBoundsException()` |  |
| `ArrayIndexOutOfBoundsException(int)` |  |
| `ArrayIndexOutOfBoundsException(String)` |  |

---

### `class ArrayStoreException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ArrayStoreException()` |  |
| `ArrayStoreException(String)` |  |

---

### `class AssertionError`

- **Extends:** `java.lang.Error`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AssertionError()` |  |
| `AssertionError(Object)` |  |
| `AssertionError(boolean)` |  |
| `AssertionError(char)` |  |
| `AssertionError(int)` |  |
| `AssertionError(long)` |  |
| `AssertionError(float)` |  |
| `AssertionError(double)` |  |
| `AssertionError(String, Throwable)` |  |

---

### `interface AutoCloseable`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.lang.Exception` |  |

---

### `class final Boolean`

- **Implements:** `java.lang.Comparable<java.lang.Boolean> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Boolean(boolean)` |  |
| `Boolean(@Nullable String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final Boolean FALSE` |  |
| `static final Boolean TRUE` |  |
| `static final Class<java.lang.Boolean> TYPE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean booleanValue()` |  |
| `static int compare(boolean, boolean)` |  |
| `int compareTo(@NonNull Boolean)` |  |
| `static boolean getBoolean(@NonNull String)` |  |
| `static int hashCode(boolean)` |  |
| `static boolean logicalAnd(boolean, boolean)` |  |
| `static boolean logicalOr(boolean, boolean)` |  |
| `static boolean logicalXor(boolean, boolean)` |  |
| `static boolean parseBoolean(@Nullable String)` |  |

---

### `class BootstrapMethodError`

- **Extends:** `java.lang.LinkageError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BootstrapMethodError()` |  |
| `BootstrapMethodError(String)` |  |
| `BootstrapMethodError(String, Throwable)` |  |
| `BootstrapMethodError(Throwable)` |  |

---

### `class final Byte`

- **Extends:** `java.lang.Number`
- **Implements:** `java.lang.Comparable<java.lang.Byte>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Byte(byte)` |  |
| `Byte(@NonNull String) throws java.lang.NumberFormatException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BYTES = 1` |  |
| `static final byte MAX_VALUE = 127` |  |
| `static final byte MIN_VALUE = -128` |  |
| `static final int SIZE = 8` |  |
| `static final Class<java.lang.Byte> TYPE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int compare(byte, byte)` |  |
| `int compareTo(@NonNull Byte)` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `static int hashCode(byte)` |  |
| `int intValue()` |  |
| `long longValue()` |  |
| `static byte parseByte(@NonNull String, int) throws java.lang.NumberFormatException` |  |
| `static byte parseByte(@NonNull String) throws java.lang.NumberFormatException` |  |
| `static int toUnsignedInt(byte)` |  |
| `static long toUnsignedLong(byte)` |  |

---

### `interface CharSequence`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `char charAt(int)` |  |
| `int length()` |  |

---

### `class final Character`

- **Implements:** `java.lang.Comparable<java.lang.Character> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Character(char)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BYTES = 2` |  |
| `static final byte COMBINING_SPACING_MARK = 8` |  |
| `static final byte CONNECTOR_PUNCTUATION = 23` |  |
| `static final byte CONTROL = 15` |  |
| `static final byte CURRENCY_SYMBOL = 26` |  |
| `static final byte DASH_PUNCTUATION = 20` |  |
| `static final byte DECIMAL_DIGIT_NUMBER = 9` |  |
| `static final byte DIRECTIONALITY_ARABIC_NUMBER = 6` |  |
| `static final byte DIRECTIONALITY_BOUNDARY_NEUTRAL = 9` |  |
| `static final byte DIRECTIONALITY_COMMON_NUMBER_SEPARATOR = 7` |  |
| `static final byte DIRECTIONALITY_EUROPEAN_NUMBER = 3` |  |
| `static final byte DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = 4` |  |
| `static final byte DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = 5` |  |
| `static final byte DIRECTIONALITY_LEFT_TO_RIGHT = 0` |  |
| `static final byte DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = 14` |  |
| `static final byte DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = 15` |  |
| `static final byte DIRECTIONALITY_NONSPACING_MARK = 8` |  |
| `static final byte DIRECTIONALITY_OTHER_NEUTRALS = 13` |  |
| `static final byte DIRECTIONALITY_PARAGRAPH_SEPARATOR = 10` |  |
| `static final byte DIRECTIONALITY_POP_DIRECTIONAL_FORMAT = 18` |  |
| `static final byte DIRECTIONALITY_RIGHT_TO_LEFT = 1` |  |
| `static final byte DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = 2` |  |
| `static final byte DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = 16` |  |
| `static final byte DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = 17` |  |
| `static final byte DIRECTIONALITY_SEGMENT_SEPARATOR = 11` |  |
| `static final byte DIRECTIONALITY_UNDEFINED = -1` |  |
| `static final byte DIRECTIONALITY_WHITESPACE = 12` |  |
| `static final byte ENCLOSING_MARK = 7` |  |
| `static final byte END_PUNCTUATION = 22` |  |
| `static final byte FINAL_QUOTE_PUNCTUATION = 30` |  |
| `static final byte FORMAT = 16` |  |
| `static final byte INITIAL_QUOTE_PUNCTUATION = 29` |  |
| `static final byte LETTER_NUMBER = 10` |  |
| `static final byte LINE_SEPARATOR = 13` |  |
| `static final byte LOWERCASE_LETTER = 2` |  |
| `static final byte MATH_SYMBOL = 25` |  |
| `static final int MAX_CODE_POINT = 1114111` |  |
| `static final char MAX_HIGH_SURROGATE = 56319` |  |
| `static final char MAX_LOW_SURROGATE = 57343` |  |
| `static final int MAX_RADIX = 36` |  |
| `static final char MAX_SURROGATE = 57343` |  |
| `static final char MAX_VALUE = 65535` |  |
| `static final int MIN_CODE_POINT = 0` |  |
| `static final char MIN_HIGH_SURROGATE = 55296` |  |
| `static final char MIN_LOW_SURROGATE = 56320` |  |
| `static final int MIN_RADIX = 2` |  |
| `static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536` |  |
| `static final char MIN_SURROGATE = 55296` |  |
| `static final char MIN_VALUE = 0` |  |
| `static final byte MODIFIER_LETTER = 4` |  |
| `static final byte MODIFIER_SYMBOL = 27` |  |
| `static final byte NON_SPACING_MARK = 6` |  |
| `static final byte OTHER_LETTER = 5` |  |
| `static final byte OTHER_NUMBER = 11` |  |
| `static final byte OTHER_PUNCTUATION = 24` |  |
| `static final byte OTHER_SYMBOL = 28` |  |
| `static final byte PARAGRAPH_SEPARATOR = 14` |  |
| `static final byte PRIVATE_USE = 18` |  |
| `static final int SIZE = 16` |  |
| `static final byte SPACE_SEPARATOR = 12` |  |
| `static final byte START_PUNCTUATION = 21` |  |
| `static final byte SURROGATE = 19` |  |
| `static final byte TITLECASE_LETTER = 3` |  |
| `static final Class<java.lang.Character> TYPE` |  |
| `static final byte UNASSIGNED = 0` |  |
| `static final byte UPPERCASE_LETTER = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int charCount(int)` |  |
| `char charValue()` |  |
| `static int codePointAt(@NonNull CharSequence, int)` |  |
| `static int codePointAt(char[], int)` |  |
| `static int codePointAt(char[], int, int)` |  |
| `static int codePointBefore(@NonNull CharSequence, int)` |  |
| `static int codePointBefore(char[], int)` |  |
| `static int codePointBefore(char[], int, int)` |  |
| `static int codePointCount(@NonNull CharSequence, int, int)` |  |
| `static int codePointCount(char[], int, int)` |  |
| `static int compare(char, char)` |  |
| `int compareTo(@NonNull Character)` |  |
| `static int digit(char, int)` |  |
| `static int digit(int, int)` |  |
| `static char forDigit(int, int)` |  |
| `static byte getDirectionality(char)` |  |
| `static byte getDirectionality(int)` |  |
| `static int getNumericValue(char)` |  |
| `static int getNumericValue(int)` |  |
| `static int getType(char)` |  |
| `static int getType(int)` |  |
| `static int hashCode(char)` |  |
| `static char highSurrogate(int)` |  |
| `static boolean isAlphabetic(int)` |  |
| `static boolean isBmpCodePoint(int)` |  |
| `static boolean isDefined(char)` |  |
| `static boolean isDefined(int)` |  |
| `static boolean isDigit(char)` |  |
| `static boolean isDigit(int)` |  |
| `static boolean isHighSurrogate(char)` |  |
| `static boolean isISOControl(char)` |  |
| `static boolean isISOControl(int)` |  |
| `static boolean isIdentifierIgnorable(char)` |  |
| `static boolean isIdentifierIgnorable(int)` |  |
| `static boolean isIdeographic(int)` |  |
| `static boolean isJavaIdentifierPart(char)` |  |
| `static boolean isJavaIdentifierPart(int)` |  |
| `static boolean isJavaIdentifierStart(char)` |  |
| `static boolean isJavaIdentifierStart(int)` |  |
| `static boolean isLetter(char)` |  |
| `static boolean isLetter(int)` |  |
| `static boolean isLetterOrDigit(char)` |  |
| `static boolean isLetterOrDigit(int)` |  |
| `static boolean isLowSurrogate(char)` |  |
| `static boolean isLowerCase(char)` |  |
| `static boolean isLowerCase(int)` |  |
| `static boolean isMirrored(char)` |  |
| `static boolean isMirrored(int)` |  |
| `static boolean isSpaceChar(char)` |  |
| `static boolean isSpaceChar(int)` |  |
| `static boolean isSupplementaryCodePoint(int)` |  |
| `static boolean isSurrogate(char)` |  |
| `static boolean isSurrogatePair(char, char)` |  |
| `static boolean isTitleCase(char)` |  |
| `static boolean isTitleCase(int)` |  |
| `static boolean isUnicodeIdentifierPart(char)` |  |
| `static boolean isUnicodeIdentifierPart(int)` |  |
| `static boolean isUnicodeIdentifierStart(char)` |  |
| `static boolean isUnicodeIdentifierStart(int)` |  |
| `static boolean isUpperCase(char)` |  |
| `static boolean isUpperCase(int)` |  |
| `static boolean isValidCodePoint(int)` |  |
| `static boolean isWhitespace(char)` |  |
| `static boolean isWhitespace(int)` |  |
| `static char lowSurrogate(int)` |  |
| `static int offsetByCodePoints(@NonNull CharSequence, int, int)` |  |
| `static int offsetByCodePoints(char[], int, int, int, int)` |  |
| `static char reverseBytes(char)` |  |
| `static int toChars(int, char[], int)` |  |
| `static char[] toChars(int)` |  |
| `static int toCodePoint(char, char)` |  |
| `static char toLowerCase(char)` |  |
| `static int toLowerCase(int)` |  |
| `static char toTitleCase(char)` |  |
| `static int toTitleCase(int)` |  |
| `static char toUpperCase(char)` |  |
| `static int toUpperCase(int)` |  |

---

### `class static Character.Subset`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Character.Subset(@NonNull String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final boolean equals(@Nullable Object)` |  |
| `final int hashCode()` |  |

---

### `class static final Character.UnicodeBlock`

- **Extends:** `java.lang.Character.Subset`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.lang.Character.UnicodeBlock AEGEAN_NUMBERS` |  |
| `static final java.lang.Character.UnicodeBlock ALCHEMICAL_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock ALPHABETIC_PRESENTATION_FORMS` |  |
| `static final java.lang.Character.UnicodeBlock ANCIENT_GREEK_MUSICAL_NOTATION` |  |
| `static final java.lang.Character.UnicodeBlock ANCIENT_GREEK_NUMBERS` |  |
| `static final java.lang.Character.UnicodeBlock ANCIENT_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock ARABIC` |  |
| `static final java.lang.Character.UnicodeBlock ARABIC_EXTENDED_A` |  |
| `static final java.lang.Character.UnicodeBlock ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock ARABIC_PRESENTATION_FORMS_A` |  |
| `static final java.lang.Character.UnicodeBlock ARABIC_PRESENTATION_FORMS_B` |  |
| `static final java.lang.Character.UnicodeBlock ARABIC_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock ARMENIAN` |  |
| `static final java.lang.Character.UnicodeBlock ARROWS` |  |
| `static final java.lang.Character.UnicodeBlock AVESTAN` |  |
| `static final java.lang.Character.UnicodeBlock BALINESE` |  |
| `static final java.lang.Character.UnicodeBlock BAMUM` |  |
| `static final java.lang.Character.UnicodeBlock BAMUM_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock BASIC_LATIN` |  |
| `static final java.lang.Character.UnicodeBlock BATAK` |  |
| `static final java.lang.Character.UnicodeBlock BENGALI` |  |
| `static final java.lang.Character.UnicodeBlock BLOCK_ELEMENTS` |  |
| `static final java.lang.Character.UnicodeBlock BOPOMOFO` |  |
| `static final java.lang.Character.UnicodeBlock BOPOMOFO_EXTENDED` |  |
| `static final java.lang.Character.UnicodeBlock BOX_DRAWING` |  |
| `static final java.lang.Character.UnicodeBlock BRAHMI` |  |
| `static final java.lang.Character.UnicodeBlock BRAILLE_PATTERNS` |  |
| `static final java.lang.Character.UnicodeBlock BUGINESE` |  |
| `static final java.lang.Character.UnicodeBlock BUHID` |  |
| `static final java.lang.Character.UnicodeBlock BYZANTINE_MUSICAL_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock CARIAN` |  |
| `static final java.lang.Character.UnicodeBlock CHAKMA` |  |
| `static final java.lang.Character.UnicodeBlock CHAM` |  |
| `static final java.lang.Character.UnicodeBlock CHEROKEE` |  |
| `static final java.lang.Character.UnicodeBlock CJK_COMPATIBILITY` |  |
| `static final java.lang.Character.UnicodeBlock CJK_COMPATIBILITY_FORMS` |  |
| `static final java.lang.Character.UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS` |  |
| `static final java.lang.Character.UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock CJK_RADICALS_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock CJK_STROKES` |  |
| `static final java.lang.Character.UnicodeBlock CJK_SYMBOLS_AND_PUNCTUATION` |  |
| `static final java.lang.Character.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS` |  |
| `static final java.lang.Character.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A` |  |
| `static final java.lang.Character.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B` |  |
| `static final java.lang.Character.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C` |  |
| `static final java.lang.Character.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D` |  |
| `static final java.lang.Character.UnicodeBlock COMBINING_DIACRITICAL_MARKS` |  |
| `static final java.lang.Character.UnicodeBlock COMBINING_DIACRITICAL_MARKS_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock COMBINING_HALF_MARKS` |  |
| `static final java.lang.Character.UnicodeBlock COMBINING_MARKS_FOR_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock COMMON_INDIC_NUMBER_FORMS` |  |
| `static final java.lang.Character.UnicodeBlock CONTROL_PICTURES` |  |
| `static final java.lang.Character.UnicodeBlock COPTIC` |  |
| `static final java.lang.Character.UnicodeBlock COUNTING_ROD_NUMERALS` |  |
| `static final java.lang.Character.UnicodeBlock CUNEIFORM` |  |
| `static final java.lang.Character.UnicodeBlock CUNEIFORM_NUMBERS_AND_PUNCTUATION` |  |
| `static final java.lang.Character.UnicodeBlock CURRENCY_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock CYPRIOT_SYLLABARY` |  |
| `static final java.lang.Character.UnicodeBlock CYRILLIC` |  |
| `static final java.lang.Character.UnicodeBlock CYRILLIC_EXTENDED_A` |  |
| `static final java.lang.Character.UnicodeBlock CYRILLIC_EXTENDED_B` |  |
| `static final java.lang.Character.UnicodeBlock CYRILLIC_SUPPLEMENTARY` |  |
| `static final java.lang.Character.UnicodeBlock DESERET` |  |
| `static final java.lang.Character.UnicodeBlock DEVANAGARI` |  |
| `static final java.lang.Character.UnicodeBlock DEVANAGARI_EXTENDED` |  |
| `static final java.lang.Character.UnicodeBlock DINGBATS` |  |
| `static final java.lang.Character.UnicodeBlock DOMINO_TILES` |  |
| `static final java.lang.Character.UnicodeBlock EGYPTIAN_HIEROGLYPHS` |  |
| `static final java.lang.Character.UnicodeBlock EMOTICONS` |  |
| `static final java.lang.Character.UnicodeBlock ENCLOSED_ALPHANUMERICS` |  |
| `static final java.lang.Character.UnicodeBlock ENCLOSED_ALPHANUMERIC_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock ENCLOSED_CJK_LETTERS_AND_MONTHS` |  |
| `static final java.lang.Character.UnicodeBlock ENCLOSED_IDEOGRAPHIC_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock ETHIOPIC` |  |
| `static final java.lang.Character.UnicodeBlock ETHIOPIC_EXTENDED` |  |
| `static final java.lang.Character.UnicodeBlock ETHIOPIC_EXTENDED_A` |  |
| `static final java.lang.Character.UnicodeBlock ETHIOPIC_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock GENERAL_PUNCTUATION` |  |
| `static final java.lang.Character.UnicodeBlock GEOMETRIC_SHAPES` |  |
| `static final java.lang.Character.UnicodeBlock GEORGIAN` |  |
| `static final java.lang.Character.UnicodeBlock GEORGIAN_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock GLAGOLITIC` |  |
| `static final java.lang.Character.UnicodeBlock GOTHIC` |  |
| `static final java.lang.Character.UnicodeBlock GREEK` |  |
| `static final java.lang.Character.UnicodeBlock GREEK_EXTENDED` |  |
| `static final java.lang.Character.UnicodeBlock GUJARATI` |  |
| `static final java.lang.Character.UnicodeBlock GURMUKHI` |  |
| `static final java.lang.Character.UnicodeBlock HALFWIDTH_AND_FULLWIDTH_FORMS` |  |
| `static final java.lang.Character.UnicodeBlock HANGUL_COMPATIBILITY_JAMO` |  |
| `static final java.lang.Character.UnicodeBlock HANGUL_JAMO` |  |
| `static final java.lang.Character.UnicodeBlock HANGUL_JAMO_EXTENDED_A` |  |
| `static final java.lang.Character.UnicodeBlock HANGUL_JAMO_EXTENDED_B` |  |
| `static final java.lang.Character.UnicodeBlock HANGUL_SYLLABLES` |  |
| `static final java.lang.Character.UnicodeBlock HANUNOO` |  |
| `static final java.lang.Character.UnicodeBlock HEBREW` |  |
| `static final java.lang.Character.UnicodeBlock HIGH_PRIVATE_USE_SURROGATES` |  |
| `static final java.lang.Character.UnicodeBlock HIGH_SURROGATES` |  |
| `static final java.lang.Character.UnicodeBlock HIRAGANA` |  |
| `static final java.lang.Character.UnicodeBlock IDEOGRAPHIC_DESCRIPTION_CHARACTERS` |  |
| `static final java.lang.Character.UnicodeBlock IMPERIAL_ARAMAIC` |  |
| `static final java.lang.Character.UnicodeBlock INSCRIPTIONAL_PAHLAVI` |  |
| `static final java.lang.Character.UnicodeBlock INSCRIPTIONAL_PARTHIAN` |  |
| `static final java.lang.Character.UnicodeBlock IPA_EXTENSIONS` |  |
| `static final java.lang.Character.UnicodeBlock JAVANESE` |  |
| `static final java.lang.Character.UnicodeBlock KAITHI` |  |
| `static final java.lang.Character.UnicodeBlock KANA_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock KANBUN` |  |
| `static final java.lang.Character.UnicodeBlock KANGXI_RADICALS` |  |
| `static final java.lang.Character.UnicodeBlock KANNADA` |  |
| `static final java.lang.Character.UnicodeBlock KATAKANA` |  |
| `static final java.lang.Character.UnicodeBlock KATAKANA_PHONETIC_EXTENSIONS` |  |
| `static final java.lang.Character.UnicodeBlock KAYAH_LI` |  |
| `static final java.lang.Character.UnicodeBlock KHAROSHTHI` |  |
| `static final java.lang.Character.UnicodeBlock KHMER` |  |
| `static final java.lang.Character.UnicodeBlock KHMER_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock LAO` |  |
| `static final java.lang.Character.UnicodeBlock LATIN_1_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock LATIN_EXTENDED_A` |  |
| `static final java.lang.Character.UnicodeBlock LATIN_EXTENDED_ADDITIONAL` |  |
| `static final java.lang.Character.UnicodeBlock LATIN_EXTENDED_B` |  |
| `static final java.lang.Character.UnicodeBlock LATIN_EXTENDED_C` |  |
| `static final java.lang.Character.UnicodeBlock LATIN_EXTENDED_D` |  |
| `static final java.lang.Character.UnicodeBlock LEPCHA` |  |
| `static final java.lang.Character.UnicodeBlock LETTERLIKE_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock LIMBU` |  |
| `static final java.lang.Character.UnicodeBlock LINEAR_B_IDEOGRAMS` |  |
| `static final java.lang.Character.UnicodeBlock LINEAR_B_SYLLABARY` |  |
| `static final java.lang.Character.UnicodeBlock LISU` |  |
| `static final java.lang.Character.UnicodeBlock LOW_SURROGATES` |  |
| `static final java.lang.Character.UnicodeBlock LYCIAN` |  |
| `static final java.lang.Character.UnicodeBlock LYDIAN` |  |
| `static final java.lang.Character.UnicodeBlock MAHJONG_TILES` |  |
| `static final java.lang.Character.UnicodeBlock MALAYALAM` |  |
| `static final java.lang.Character.UnicodeBlock MANDAIC` |  |
| `static final java.lang.Character.UnicodeBlock MATHEMATICAL_ALPHANUMERIC_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock MATHEMATICAL_OPERATORS` |  |
| `static final java.lang.Character.UnicodeBlock MEETEI_MAYEK` |  |
| `static final java.lang.Character.UnicodeBlock MEETEI_MAYEK_EXTENSIONS` |  |
| `static final java.lang.Character.UnicodeBlock MEROITIC_CURSIVE` |  |
| `static final java.lang.Character.UnicodeBlock MEROITIC_HIEROGLYPHS` |  |
| `static final java.lang.Character.UnicodeBlock MIAO` |  |
| `static final java.lang.Character.UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A` |  |
| `static final java.lang.Character.UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B` |  |
| `static final java.lang.Character.UnicodeBlock MISCELLANEOUS_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_ARROWS` |  |
| `static final java.lang.Character.UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS` |  |
| `static final java.lang.Character.UnicodeBlock MISCELLANEOUS_TECHNICAL` |  |
| `static final java.lang.Character.UnicodeBlock MODIFIER_TONE_LETTERS` |  |
| `static final java.lang.Character.UnicodeBlock MONGOLIAN` |  |
| `static final java.lang.Character.UnicodeBlock MUSICAL_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock MYANMAR` |  |
| `static final java.lang.Character.UnicodeBlock MYANMAR_EXTENDED_A` |  |
| `static final java.lang.Character.UnicodeBlock NEW_TAI_LUE` |  |
| `static final java.lang.Character.UnicodeBlock NKO` |  |
| `static final java.lang.Character.UnicodeBlock NUMBER_FORMS` |  |
| `static final java.lang.Character.UnicodeBlock OGHAM` |  |
| `static final java.lang.Character.UnicodeBlock OLD_ITALIC` |  |
| `static final java.lang.Character.UnicodeBlock OLD_PERSIAN` |  |
| `static final java.lang.Character.UnicodeBlock OLD_SOUTH_ARABIAN` |  |
| `static final java.lang.Character.UnicodeBlock OLD_TURKIC` |  |
| `static final java.lang.Character.UnicodeBlock OL_CHIKI` |  |
| `static final java.lang.Character.UnicodeBlock OPTICAL_CHARACTER_RECOGNITION` |  |
| `static final java.lang.Character.UnicodeBlock ORIYA` |  |
| `static final java.lang.Character.UnicodeBlock OSMANYA` |  |
| `static final java.lang.Character.UnicodeBlock PHAGS_PA` |  |
| `static final java.lang.Character.UnicodeBlock PHAISTOS_DISC` |  |
| `static final java.lang.Character.UnicodeBlock PHOENICIAN` |  |
| `static final java.lang.Character.UnicodeBlock PHONETIC_EXTENSIONS` |  |
| `static final java.lang.Character.UnicodeBlock PHONETIC_EXTENSIONS_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock PLAYING_CARDS` |  |
| `static final java.lang.Character.UnicodeBlock PRIVATE_USE_AREA` |  |
| `static final java.lang.Character.UnicodeBlock REJANG` |  |
| `static final java.lang.Character.UnicodeBlock RUMI_NUMERAL_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock RUNIC` |  |
| `static final java.lang.Character.UnicodeBlock SAMARITAN` |  |
| `static final java.lang.Character.UnicodeBlock SAURASHTRA` |  |
| `static final java.lang.Character.UnicodeBlock SHARADA` |  |
| `static final java.lang.Character.UnicodeBlock SHAVIAN` |  |
| `static final java.lang.Character.UnicodeBlock SINHALA` |  |
| `static final java.lang.Character.UnicodeBlock SMALL_FORM_VARIANTS` |  |
| `static final java.lang.Character.UnicodeBlock SORA_SOMPENG` |  |
| `static final java.lang.Character.UnicodeBlock SPACING_MODIFIER_LETTERS` |  |
| `static final java.lang.Character.UnicodeBlock SPECIALS` |  |
| `static final java.lang.Character.UnicodeBlock SUNDANESE` |  |
| `static final java.lang.Character.UnicodeBlock SUNDANESE_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock SUPERSCRIPTS_AND_SUBSCRIPTS` |  |
| `static final java.lang.Character.UnicodeBlock SUPPLEMENTAL_ARROWS_A` |  |
| `static final java.lang.Character.UnicodeBlock SUPPLEMENTAL_ARROWS_B` |  |
| `static final java.lang.Character.UnicodeBlock SUPPLEMENTAL_MATHEMATICAL_OPERATORS` |  |
| `static final java.lang.Character.UnicodeBlock SUPPLEMENTAL_PUNCTUATION` |  |
| `static final java.lang.Character.UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_A` |  |
| `static final java.lang.Character.UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_B` |  |
| `static final java.lang.Character.UnicodeBlock SYLOTI_NAGRI` |  |
| `static final java.lang.Character.UnicodeBlock SYRIAC` |  |
| `static final java.lang.Character.UnicodeBlock TAGALOG` |  |
| `static final java.lang.Character.UnicodeBlock TAGBANWA` |  |
| `static final java.lang.Character.UnicodeBlock TAGS` |  |
| `static final java.lang.Character.UnicodeBlock TAI_LE` |  |
| `static final java.lang.Character.UnicodeBlock TAI_THAM` |  |
| `static final java.lang.Character.UnicodeBlock TAI_VIET` |  |
| `static final java.lang.Character.UnicodeBlock TAI_XUAN_JING_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock TAKRI` |  |
| `static final java.lang.Character.UnicodeBlock TAMIL` |  |
| `static final java.lang.Character.UnicodeBlock TELUGU` |  |
| `static final java.lang.Character.UnicodeBlock THAANA` |  |
| `static final java.lang.Character.UnicodeBlock THAI` |  |
| `static final java.lang.Character.UnicodeBlock TIBETAN` |  |
| `static final java.lang.Character.UnicodeBlock TIFINAGH` |  |
| `static final java.lang.Character.UnicodeBlock TRANSPORT_AND_MAP_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock UGARITIC` |  |
| `static final java.lang.Character.UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS` |  |
| `static final java.lang.Character.UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED` |  |
| `static final java.lang.Character.UnicodeBlock VAI` |  |
| `static final java.lang.Character.UnicodeBlock VARIATION_SELECTORS` |  |
| `static final java.lang.Character.UnicodeBlock VARIATION_SELECTORS_SUPPLEMENT` |  |
| `static final java.lang.Character.UnicodeBlock VEDIC_EXTENSIONS` |  |
| `static final java.lang.Character.UnicodeBlock VERTICAL_FORMS` |  |
| `static final java.lang.Character.UnicodeBlock YIJING_HEXAGRAM_SYMBOLS` |  |
| `static final java.lang.Character.UnicodeBlock YI_RADICALS` |  |
| `static final java.lang.Character.UnicodeBlock YI_SYLLABLES` |  |

---

### `enum Character.UnicodeScript`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.lang.Character.UnicodeScript ARABIC` |  |
| `static final java.lang.Character.UnicodeScript ARMENIAN` |  |
| `static final java.lang.Character.UnicodeScript AVESTAN` |  |
| `static final java.lang.Character.UnicodeScript BALINESE` |  |
| `static final java.lang.Character.UnicodeScript BAMUM` |  |
| `static final java.lang.Character.UnicodeScript BATAK` |  |
| `static final java.lang.Character.UnicodeScript BENGALI` |  |
| `static final java.lang.Character.UnicodeScript BOPOMOFO` |  |
| `static final java.lang.Character.UnicodeScript BRAHMI` |  |
| `static final java.lang.Character.UnicodeScript BRAILLE` |  |
| `static final java.lang.Character.UnicodeScript BUGINESE` |  |
| `static final java.lang.Character.UnicodeScript BUHID` |  |
| `static final java.lang.Character.UnicodeScript CANADIAN_ABORIGINAL` |  |
| `static final java.lang.Character.UnicodeScript CARIAN` |  |
| `static final java.lang.Character.UnicodeScript CHAKMA` |  |
| `static final java.lang.Character.UnicodeScript CHAM` |  |
| `static final java.lang.Character.UnicodeScript CHEROKEE` |  |
| `static final java.lang.Character.UnicodeScript COMMON` |  |
| `static final java.lang.Character.UnicodeScript COPTIC` |  |
| `static final java.lang.Character.UnicodeScript CUNEIFORM` |  |
| `static final java.lang.Character.UnicodeScript CYPRIOT` |  |
| `static final java.lang.Character.UnicodeScript CYRILLIC` |  |
| `static final java.lang.Character.UnicodeScript DESERET` |  |
| `static final java.lang.Character.UnicodeScript DEVANAGARI` |  |
| `static final java.lang.Character.UnicodeScript EGYPTIAN_HIEROGLYPHS` |  |
| `static final java.lang.Character.UnicodeScript ETHIOPIC` |  |
| `static final java.lang.Character.UnicodeScript GEORGIAN` |  |
| `static final java.lang.Character.UnicodeScript GLAGOLITIC` |  |
| `static final java.lang.Character.UnicodeScript GOTHIC` |  |
| `static final java.lang.Character.UnicodeScript GREEK` |  |
| `static final java.lang.Character.UnicodeScript GUJARATI` |  |
| `static final java.lang.Character.UnicodeScript GURMUKHI` |  |
| `static final java.lang.Character.UnicodeScript HAN` |  |
| `static final java.lang.Character.UnicodeScript HANGUL` |  |
| `static final java.lang.Character.UnicodeScript HANUNOO` |  |
| `static final java.lang.Character.UnicodeScript HEBREW` |  |
| `static final java.lang.Character.UnicodeScript HIRAGANA` |  |
| `static final java.lang.Character.UnicodeScript IMPERIAL_ARAMAIC` |  |
| `static final java.lang.Character.UnicodeScript INHERITED` |  |
| `static final java.lang.Character.UnicodeScript INSCRIPTIONAL_PAHLAVI` |  |
| `static final java.lang.Character.UnicodeScript INSCRIPTIONAL_PARTHIAN` |  |
| `static final java.lang.Character.UnicodeScript JAVANESE` |  |
| `static final java.lang.Character.UnicodeScript KAITHI` |  |
| `static final java.lang.Character.UnicodeScript KANNADA` |  |
| `static final java.lang.Character.UnicodeScript KATAKANA` |  |
| `static final java.lang.Character.UnicodeScript KAYAH_LI` |  |
| `static final java.lang.Character.UnicodeScript KHAROSHTHI` |  |
| `static final java.lang.Character.UnicodeScript KHMER` |  |
| `static final java.lang.Character.UnicodeScript LAO` |  |
| `static final java.lang.Character.UnicodeScript LATIN` |  |
| `static final java.lang.Character.UnicodeScript LEPCHA` |  |
| `static final java.lang.Character.UnicodeScript LIMBU` |  |
| `static final java.lang.Character.UnicodeScript LINEAR_B` |  |
| `static final java.lang.Character.UnicodeScript LISU` |  |
| `static final java.lang.Character.UnicodeScript LYCIAN` |  |
| `static final java.lang.Character.UnicodeScript LYDIAN` |  |
| `static final java.lang.Character.UnicodeScript MALAYALAM` |  |
| `static final java.lang.Character.UnicodeScript MANDAIC` |  |
| `static final java.lang.Character.UnicodeScript MEETEI_MAYEK` |  |
| `static final java.lang.Character.UnicodeScript MEROITIC_CURSIVE` |  |
| `static final java.lang.Character.UnicodeScript MEROITIC_HIEROGLYPHS` |  |
| `static final java.lang.Character.UnicodeScript MIAO` |  |
| `static final java.lang.Character.UnicodeScript MONGOLIAN` |  |
| `static final java.lang.Character.UnicodeScript MYANMAR` |  |
| `static final java.lang.Character.UnicodeScript NEW_TAI_LUE` |  |
| `static final java.lang.Character.UnicodeScript NKO` |  |
| `static final java.lang.Character.UnicodeScript OGHAM` |  |
| `static final java.lang.Character.UnicodeScript OLD_ITALIC` |  |
| `static final java.lang.Character.UnicodeScript OLD_PERSIAN` |  |
| `static final java.lang.Character.UnicodeScript OLD_SOUTH_ARABIAN` |  |
| `static final java.lang.Character.UnicodeScript OLD_TURKIC` |  |
| `static final java.lang.Character.UnicodeScript OL_CHIKI` |  |
| `static final java.lang.Character.UnicodeScript ORIYA` |  |
| `static final java.lang.Character.UnicodeScript OSMANYA` |  |
| `static final java.lang.Character.UnicodeScript PHAGS_PA` |  |
| `static final java.lang.Character.UnicodeScript PHOENICIAN` |  |
| `static final java.lang.Character.UnicodeScript REJANG` |  |
| `static final java.lang.Character.UnicodeScript RUNIC` |  |
| `static final java.lang.Character.UnicodeScript SAMARITAN` |  |
| `static final java.lang.Character.UnicodeScript SAURASHTRA` |  |
| `static final java.lang.Character.UnicodeScript SHARADA` |  |
| `static final java.lang.Character.UnicodeScript SHAVIAN` |  |
| `static final java.lang.Character.UnicodeScript SINHALA` |  |
| `static final java.lang.Character.UnicodeScript SORA_SOMPENG` |  |
| `static final java.lang.Character.UnicodeScript SUNDANESE` |  |
| `static final java.lang.Character.UnicodeScript SYLOTI_NAGRI` |  |
| `static final java.lang.Character.UnicodeScript SYRIAC` |  |
| `static final java.lang.Character.UnicodeScript TAGALOG` |  |
| `static final java.lang.Character.UnicodeScript TAGBANWA` |  |
| `static final java.lang.Character.UnicodeScript TAI_LE` |  |
| `static final java.lang.Character.UnicodeScript TAI_THAM` |  |
| `static final java.lang.Character.UnicodeScript TAI_VIET` |  |
| `static final java.lang.Character.UnicodeScript TAKRI` |  |
| `static final java.lang.Character.UnicodeScript TAMIL` |  |
| `static final java.lang.Character.UnicodeScript TELUGU` |  |
| `static final java.lang.Character.UnicodeScript THAANA` |  |
| `static final java.lang.Character.UnicodeScript THAI` |  |
| `static final java.lang.Character.UnicodeScript TIBETAN` |  |
| `static final java.lang.Character.UnicodeScript TIFINAGH` |  |
| `static final java.lang.Character.UnicodeScript UGARITIC` |  |
| `static final java.lang.Character.UnicodeScript UNKNOWN` |  |
| `static final java.lang.Character.UnicodeScript VAI` |  |
| `static final java.lang.Character.UnicodeScript YI` |  |

---

### `class final Class<T>`

- **Implements:** `java.lang.reflect.AnnotatedElement java.lang.reflect.GenericDeclaration java.io.Serializable java.lang.reflect.Type`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean desiredAssertionStatus()` |  |
| `int getModifiers()` |  |
| `boolean isAnnotation()` |  |
| `boolean isAnonymousClass()` |  |
| `boolean isArray()` |  |
| `boolean isAssignableFrom(@NonNull Class<?>)` |  |
| `boolean isEnum()` |  |
| `boolean isInstance(@Nullable Object)` |  |
| `boolean isInterface()` |  |
| `boolean isLocalClass()` |  |
| `boolean isMemberClass()` |  |
| `boolean isPrimitive()` |  |
| `boolean isSynthetic()` |  |

---

### `class ClassCastException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ClassCastException()` |  |
| `ClassCastException(String)` |  |

---

### `class ClassCircularityError`

- **Extends:** `java.lang.LinkageError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ClassCircularityError()` |  |
| `ClassCircularityError(String)` |  |

---

### `class ClassFormatError`

- **Extends:** `java.lang.LinkageError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ClassFormatError()` |  |
| `ClassFormatError(String)` |  |

---

### `class abstract ClassLoader`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ClassLoader(ClassLoader)` |  |
| `ClassLoader()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clearAssertionStatus()` |  |
| `final Class<?> defineClass(String, byte[], int, int) throws java.lang.ClassFormatError` |  |
| `final Class<?> defineClass(String, byte[], int, int, java.security.ProtectionDomain) throws java.lang.ClassFormatError` |  |
| `final Class<?> defineClass(String, java.nio.ByteBuffer, java.security.ProtectionDomain) throws java.lang.ClassFormatError` |  |
| `Package definePackage(String, String, String, String, String, String, String, java.net.URL) throws java.lang.IllegalArgumentException` |  |
| `Class<?> findClass(String) throws java.lang.ClassNotFoundException` |  |
| `String findLibrary(String)` |  |
| `final Class<?> findLoadedClass(String)` |  |
| `java.net.URL findResource(String)` |  |
| `java.util.Enumeration<java.net.URL> findResources(String) throws java.io.IOException` |  |
| `final Class<?> findSystemClass(String) throws java.lang.ClassNotFoundException` |  |
| `Package getPackage(String)` |  |
| `Package[] getPackages()` |  |
| `final ClassLoader getParent()` |  |
| `java.net.URL getResource(String)` |  |
| `java.io.InputStream getResourceAsStream(String)` |  |
| `java.util.Enumeration<java.net.URL> getResources(String) throws java.io.IOException` |  |
| `static ClassLoader getSystemClassLoader()` |  |
| `static java.net.URL getSystemResource(String)` |  |
| `static java.io.InputStream getSystemResourceAsStream(String)` |  |
| `static java.util.Enumeration<java.net.URL> getSystemResources(String) throws java.io.IOException` |  |
| `Class<?> loadClass(String) throws java.lang.ClassNotFoundException` |  |
| `Class<?> loadClass(String, boolean) throws java.lang.ClassNotFoundException` |  |
| `static boolean registerAsParallelCapable()` |  |
| `final void resolveClass(Class<?>)` |  |
| `void setClassAssertionStatus(String, boolean)` |  |
| `void setDefaultAssertionStatus(boolean)` |  |
| `void setPackageAssertionStatus(String, boolean)` |  |
| `final void setSigners(Class<?>, Object[])` |  |

---

### `class ClassNotFoundException`

- **Extends:** `java.lang.ReflectiveOperationException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ClassNotFoundException()` |  |
| `ClassNotFoundException(String)` |  |
| `ClassNotFoundException(String, Throwable)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Throwable getException()` |  |

---

### `class CloneNotSupportedException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CloneNotSupportedException()` |  |
| `CloneNotSupportedException(String)` |  |

---

### `interface Cloneable`


---

### `interface Comparable<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compareTo(T)` |  |

---

### `class final Compiler`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static Object command(Object)` |  |
| `static boolean compileClass(Class<?>)` |  |
| `static boolean compileClasses(String)` |  |
| `static void disable()` |  |
| `static void enable()` |  |

---

### `class final Double`

- **Extends:** `java.lang.Number`
- **Implements:** `java.lang.Comparable<java.lang.Double>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Double(double)` |  |
| `Double(@NonNull String) throws java.lang.NumberFormatException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BYTES = 8` |  |
| `static final int MAX_EXPONENT = 1023` |  |
| `static final double MAX_VALUE = 1.7976931348623157E308` |  |
| `static final int MIN_EXPONENT = -1022` |  |
| `static final double MIN_NORMAL = 2.2250738585072014E-308` |  |
| `static final double MIN_VALUE = 4.9E-324` |  |
| `static final double NEGATIVE_INFINITY = (-1.0/0.0)` |  |
| `static final double NaN = (0.0/0.0)` |  |
| `static final double POSITIVE_INFINITY = (1.0/0.0)` |  |
| `static final int SIZE = 64` |  |
| `static final Class<java.lang.Double> TYPE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int compare(double, double)` |  |
| `int compareTo(@NonNull Double)` |  |
| `static long doubleToLongBits(double)` |  |
| `static long doubleToRawLongBits(double)` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `static int hashCode(double)` |  |
| `int intValue()` |  |
| `static boolean isFinite(double)` |  |
| `static boolean isInfinite(double)` |  |
| `boolean isInfinite()` |  |
| `static boolean isNaN(double)` |  |
| `boolean isNaN()` |  |
| `static double longBitsToDouble(long)` |  |
| `long longValue()` |  |
| `static double max(double, double)` |  |
| `static double min(double, double)` |  |
| `static double parseDouble(@NonNull String) throws java.lang.NumberFormatException` |  |
| `static double sum(double, double)` |  |

---

### `class abstract Enum<E`

- **Extends:** `java.lang.Enum<E>>`
- **Implements:** `java.lang.Comparable<E> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Enum(@NonNull String, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int compareTo(E)` |  |
| `final boolean equals(@Nullable Object)` |  |
| `final void finalize()` |  |
| `final int hashCode()` |  |
| `final int ordinal()` |  |

---

### `class EnumConstantNotPresentException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EnumConstantNotPresentException(Class<? extends java.lang.Enum>, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String constantName()` |  |
| `Class<? extends java.lang.Enum> enumType()` |  |

---

### `class Error`

- **Extends:** `java.lang.Throwable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Error()` |  |
| `Error(String)` |  |
| `Error(String, Throwable)` |  |
| `Error(Throwable)` |  |
| `Error(String, Throwable, boolean, boolean)` |  |

---

### `class Exception`

- **Extends:** `java.lang.Throwable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Exception()` |  |
| `Exception(String)` |  |
| `Exception(String, Throwable)` |  |
| `Exception(Throwable)` |  |
| `Exception(String, Throwable, boolean, boolean)` |  |

---

### `class ExceptionInInitializerError`

- **Extends:** `java.lang.LinkageError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ExceptionInInitializerError()` |  |
| `ExceptionInInitializerError(Throwable)` |  |
| `ExceptionInInitializerError(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Throwable getException()` |  |

---

### `class final Float`

- **Extends:** `java.lang.Number`
- **Implements:** `java.lang.Comparable<java.lang.Float>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Float(float)` |  |
| `Float(double)` |  |
| `Float(@NonNull String) throws java.lang.NumberFormatException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BYTES = 4` |  |
| `static final int MAX_EXPONENT = 127` |  |
| `static final float MAX_VALUE = 3.4028235E38f` |  |
| `static final int MIN_EXPONENT = -126` |  |
| `static final float MIN_NORMAL = 1.17549435E-38f` |  |
| `static final float MIN_VALUE = 1.4E-45f` |  |
| `static final float NEGATIVE_INFINITY = (-1.0f/0.0f)` |  |
| `static final float NaN = (0.0f/0.0f)` |  |
| `static final float POSITIVE_INFINITY = (1.0f/0.0f)` |  |
| `static final int SIZE = 32` |  |
| `static final Class<java.lang.Float> TYPE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int compare(float, float)` |  |
| `int compareTo(@NonNull Float)` |  |
| `double doubleValue()` |  |
| `static int floatToIntBits(float)` |  |
| `static int floatToRawIntBits(float)` |  |
| `float floatValue()` |  |
| `static int hashCode(float)` |  |
| `static float intBitsToFloat(int)` |  |
| `int intValue()` |  |
| `static boolean isFinite(float)` |  |
| `static boolean isInfinite(float)` |  |
| `boolean isInfinite()` |  |
| `static boolean isNaN(float)` |  |
| `boolean isNaN()` |  |
| `long longValue()` |  |
| `static float max(float, float)` |  |
| `static float min(float, float)` |  |
| `static float parseFloat(@NonNull String) throws java.lang.NumberFormatException` |  |
| `static float sum(float, float)` |  |

---

### `class IllegalAccessError`

- **Extends:** `java.lang.IncompatibleClassChangeError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalAccessError()` |  |
| `IllegalAccessError(String)` |  |

---

### `class IllegalAccessException`

- **Extends:** `java.lang.ReflectiveOperationException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalAccessException()` |  |
| `IllegalAccessException(String)` |  |

---

### `class IllegalArgumentException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalArgumentException()` |  |
| `IllegalArgumentException(String)` |  |
| `IllegalArgumentException(String, Throwable)` |  |
| `IllegalArgumentException(Throwable)` |  |

---

### `class IllegalMonitorStateException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalMonitorStateException()` |  |
| `IllegalMonitorStateException(String)` |  |

---

### `class IllegalStateException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalStateException()` |  |
| `IllegalStateException(String)` |  |
| `IllegalStateException(String, Throwable)` |  |
| `IllegalStateException(Throwable)` |  |

---

### `class IllegalThreadStateException`

- **Extends:** `java.lang.IllegalArgumentException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalThreadStateException()` |  |
| `IllegalThreadStateException(String)` |  |

---

### `class IncompatibleClassChangeError`

- **Extends:** `java.lang.LinkageError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IncompatibleClassChangeError()` |  |
| `IncompatibleClassChangeError(String)` |  |

---

### `class IndexOutOfBoundsException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IndexOutOfBoundsException()` |  |
| `IndexOutOfBoundsException(String)` |  |

---

### `class InheritableThreadLocal<T>`

- **Extends:** `java.lang.ThreadLocal<T>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InheritableThreadLocal()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `T childValue(T)` |  |

---

### `class InstantiationError`

- **Extends:** `java.lang.IncompatibleClassChangeError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InstantiationError()` |  |
| `InstantiationError(String)` |  |

---

### `class InstantiationException`

- **Extends:** `java.lang.ReflectiveOperationException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InstantiationException()` |  |
| `InstantiationException(String)` |  |

---

### `class final Integer`

- **Extends:** `java.lang.Number`
- **Implements:** `java.lang.Comparable<java.lang.Integer>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Integer(int)` |  |
| `Integer(@NonNull String) throws java.lang.NumberFormatException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BYTES = 4` |  |
| `static final int MAX_VALUE = 2147483647` |  |
| `static final int MIN_VALUE = -2147483648` |  |
| `static final int SIZE = 32` |  |
| `static final Class<java.lang.Integer> TYPE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int bitCount(int)` |  |
| `static int compare(int, int)` |  |
| `int compareTo(@NonNull Integer)` |  |
| `static int compareUnsigned(int, int)` |  |
| `static int divideUnsigned(int, int)` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `static int hashCode(int)` |  |
| `static int highestOneBit(int)` |  |
| `int intValue()` |  |
| `long longValue()` |  |
| `static int lowestOneBit(int)` |  |
| `static int max(int, int)` |  |
| `static int min(int, int)` |  |
| `static int numberOfLeadingZeros(int)` |  |
| `static int numberOfTrailingZeros(int)` |  |
| `static int parseInt(@NonNull String, int) throws java.lang.NumberFormatException` |  |
| `static int parseInt(@NonNull String) throws java.lang.NumberFormatException` |  |
| `static int parseUnsignedInt(@NonNull String, int) throws java.lang.NumberFormatException` |  |
| `static int parseUnsignedInt(@NonNull String) throws java.lang.NumberFormatException` |  |
| `static int remainderUnsigned(int, int)` |  |
| `static int reverse(int)` |  |
| `static int reverseBytes(int)` |  |
| `static int rotateLeft(int, int)` |  |
| `static int rotateRight(int, int)` |  |
| `static int signum(int)` |  |
| `static int sum(int, int)` |  |
| `static long toUnsignedLong(int)` |  |

---

### `class InternalError`

- **Extends:** `java.lang.VirtualMachineError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InternalError()` |  |
| `InternalError(String)` |  |
| `InternalError(String, Throwable)` |  |
| `InternalError(Throwable)` |  |

---

### `class InterruptedException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InterruptedException()` |  |
| `InterruptedException(String)` |  |

---

### `interface Iterable<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default void forEach(@NonNull java.util.function.Consumer<? super T>)` |  |

---

### `class LinkageError`

- **Extends:** `java.lang.Error`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LinkageError()` |  |
| `LinkageError(String)` |  |
| `LinkageError(String, Throwable)` |  |

---

### `class final Long`

- **Extends:** `java.lang.Number`
- **Implements:** `java.lang.Comparable<java.lang.Long>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Long(long)` |  |
| `Long(@NonNull String) throws java.lang.NumberFormatException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BYTES = 8` |  |
| `static final long MAX_VALUE = 9223372036854775807L` |  |
| `static final long MIN_VALUE = -9223372036854775808L` |  |
| `static final int SIZE = 64` |  |
| `static final Class<java.lang.Long> TYPE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int bitCount(long)` |  |
| `static int compare(long, long)` |  |
| `int compareTo(@NonNull Long)` |  |
| `static int compareUnsigned(long, long)` |  |
| `static long divideUnsigned(long, long)` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `static int hashCode(long)` |  |
| `static long highestOneBit(long)` |  |
| `int intValue()` |  |
| `long longValue()` |  |
| `static long lowestOneBit(long)` |  |
| `static long max(long, long)` |  |
| `static long min(long, long)` |  |
| `static int numberOfLeadingZeros(long)` |  |
| `static int numberOfTrailingZeros(long)` |  |
| `static long parseLong(@NonNull String, int) throws java.lang.NumberFormatException` |  |
| `static long parseLong(@NonNull String) throws java.lang.NumberFormatException` |  |
| `static long parseUnsignedLong(@NonNull String, int) throws java.lang.NumberFormatException` |  |
| `static long parseUnsignedLong(@NonNull String) throws java.lang.NumberFormatException` |  |
| `static long remainderUnsigned(long, long)` |  |
| `static long reverse(long)` |  |
| `static long reverseBytes(long)` |  |
| `static long rotateLeft(long, int)` |  |
| `static long rotateRight(long, int)` |  |
| `static int signum(long)` |  |
| `static long sum(long, long)` |  |

---

### `class final Math`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final double E = 2.718281828459045` |  |
| `static final double PI = 3.141592653589793` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static double IEEEremainder(double, double)` |  |
| `static int abs(int)` |  |
| `static long abs(long)` |  |
| `static float abs(float)` |  |
| `static double abs(double)` |  |
| `static double acos(double)` |  |
| `static int addExact(int, int)` |  |
| `static long addExact(long, long)` |  |
| `static double asin(double)` |  |
| `static double atan(double)` |  |
| `static double atan2(double, double)` |  |
| `static double cbrt(double)` |  |
| `static double ceil(double)` |  |
| `static double copySign(double, double)` |  |
| `static float copySign(float, float)` |  |
| `static double cos(double)` |  |
| `static double cosh(double)` |  |
| `static int decrementExact(int)` |  |
| `static long decrementExact(long)` |  |
| `static double exp(double)` |  |
| `static double expm1(double)` |  |
| `static double floor(double)` |  |
| `static int floorDiv(int, int)` |  |
| `static long floorDiv(long, long)` |  |
| `static int floorMod(int, int)` |  |
| `static long floorMod(long, long)` |  |
| `static int getExponent(float)` |  |
| `static int getExponent(double)` |  |
| `static double hypot(double, double)` |  |
| `static int incrementExact(int)` |  |
| `static long incrementExact(long)` |  |
| `static double log(double)` |  |
| `static double log10(double)` |  |
| `static double log1p(double)` |  |
| `static int max(int, int)` |  |
| `static long max(long, long)` |  |
| `static float max(float, float)` |  |
| `static double max(double, double)` |  |
| `static int min(int, int)` |  |
| `static long min(long, long)` |  |
| `static float min(float, float)` |  |
| `static double min(double, double)` |  |
| `static int multiplyExact(int, int)` |  |
| `static long multiplyExact(long, long)` |  |
| `static int negateExact(int)` |  |
| `static long negateExact(long)` |  |
| `static double nextAfter(double, double)` |  |
| `static float nextAfter(float, double)` |  |
| `static double nextDown(double)` |  |
| `static float nextDown(float)` |  |
| `static double nextUp(double)` |  |
| `static float nextUp(float)` |  |
| `static double pow(double, double)` |  |
| `static double random()` |  |
| `static double rint(double)` |  |
| `static int round(float)` |  |
| `static long round(double)` |  |
| `static double scalb(double, int)` |  |
| `static float scalb(float, int)` |  |
| `static double signum(double)` |  |
| `static float signum(float)` |  |
| `static double sin(double)` |  |
| `static double sinh(double)` |  |
| `static double sqrt(double)` |  |
| `static int subtractExact(int, int)` |  |
| `static long subtractExact(long, long)` |  |
| `static double tan(double)` |  |
| `static double tanh(double)` |  |
| `static double toDegrees(double)` |  |
| `static int toIntExact(long)` |  |
| `static double toRadians(double)` |  |
| `static double ulp(double)` |  |
| `static float ulp(float)` |  |

---

### `class NegativeArraySizeException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NegativeArraySizeException()` |  |
| `NegativeArraySizeException(String)` |  |

---

### `class NoClassDefFoundError`

- **Extends:** `java.lang.LinkageError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NoClassDefFoundError()` |  |
| `NoClassDefFoundError(String)` |  |

---

### `class NoSuchFieldError`

- **Extends:** `java.lang.IncompatibleClassChangeError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NoSuchFieldError()` |  |
| `NoSuchFieldError(String)` |  |

---

### `class NoSuchFieldException`

- **Extends:** `java.lang.ReflectiveOperationException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NoSuchFieldException()` |  |
| `NoSuchFieldException(String)` |  |

---

### `class NoSuchMethodError`

- **Extends:** `java.lang.IncompatibleClassChangeError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NoSuchMethodError()` |  |
| `NoSuchMethodError(String)` |  |

---

### `class NoSuchMethodException`

- **Extends:** `java.lang.ReflectiveOperationException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NoSuchMethodException()` |  |
| `NoSuchMethodException(String)` |  |

---

### `class NullPointerException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NullPointerException()` |  |
| `NullPointerException(String)` |  |

---

### `class abstract Number`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Number()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `byte byteValue()` |  |
| `abstract double doubleValue()` |  |
| `abstract float floatValue()` |  |
| `abstract int intValue()` |  |
| `abstract long longValue()` |  |
| `short shortValue()` |  |

---

### `class NumberFormatException`

- **Extends:** `java.lang.IllegalArgumentException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NumberFormatException()` |  |
| `NumberFormatException(String)` |  |

---

### `class Object`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Object()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean equals(@Nullable Object)` |  |
| `void finalize() throws java.lang.Throwable` |  |
| `int hashCode()` |  |
| `final void notify()` |  |
| `final void notifyAll()` |  |
| `final void wait(long) throws java.lang.InterruptedException` |  |
| `final void wait(long, int) throws java.lang.InterruptedException` |  |
| `final void wait() throws java.lang.InterruptedException` |  |

---

### `class OutOfMemoryError`

- **Extends:** `java.lang.VirtualMachineError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `OutOfMemoryError()` |  |
| `OutOfMemoryError(String)` |  |

---

### `class Package`

- **Implements:** `java.lang.reflect.AnnotatedElement`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `<A extends java.lang.annotation.Annotation> A getAnnotation(Class<A>)` |  |
| `java.lang.annotation.Annotation[] getAnnotations()` |  |
| `<A extends java.lang.annotation.Annotation> A[] getAnnotationsByType(Class<A>)` |  |
| `<A extends java.lang.annotation.Annotation> A getDeclaredAnnotation(Class<A>)` |  |
| `java.lang.annotation.Annotation[] getDeclaredAnnotations()` |  |
| `<A extends java.lang.annotation.Annotation> A[] getDeclaredAnnotationsByType(Class<A>)` |  |
| `String getImplementationTitle()` |  |
| `String getImplementationVendor()` |  |
| `String getImplementationVersion()` |  |
| `String getName()` |  |
| `static Package getPackage(String)` |  |
| `static Package[] getPackages()` |  |
| `String getSpecificationTitle()` |  |
| `String getSpecificationVendor()` |  |
| `String getSpecificationVersion()` |  |
| `boolean isCompatibleWith(String) throws java.lang.NumberFormatException` |  |
| `boolean isSealed()` |  |
| `boolean isSealed(java.net.URL)` |  |

---

### `class abstract Process`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Process()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void destroy()` |  |
| `Process destroyForcibly()` |  |
| `abstract int exitValue()` |  |
| `abstract java.io.InputStream getErrorStream()` |  |
| `abstract java.io.InputStream getInputStream()` |  |
| `abstract java.io.OutputStream getOutputStream()` |  |
| `boolean isAlive()` |  |
| `abstract int waitFor() throws java.lang.InterruptedException` |  |
| `boolean waitFor(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |

---

### `class final ProcessBuilder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ProcessBuilder(java.util.List<java.lang.String>)` |  |
| `ProcessBuilder(java.lang.String...)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.lang.ProcessBuilder.Redirect INHERIT` |  |
| `static final java.lang.ProcessBuilder.Redirect PIPE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `ProcessBuilder command(java.util.List<java.lang.String>)` |  |
| `ProcessBuilder command(java.lang.String...)` |  |
| `java.util.List<java.lang.String> command()` |  |
| `java.io.File directory()` |  |
| `ProcessBuilder directory(java.io.File)` |  |
| `java.util.Map<java.lang.String,java.lang.String> environment()` |  |
| `ProcessBuilder inheritIO()` |  |
| `ProcessBuilder redirectError(java.lang.ProcessBuilder.Redirect)` |  |
| `ProcessBuilder redirectError(java.io.File)` |  |
| `java.lang.ProcessBuilder.Redirect redirectError()` |  |
| `boolean redirectErrorStream()` |  |
| `ProcessBuilder redirectErrorStream(boolean)` |  |
| `ProcessBuilder redirectInput(java.lang.ProcessBuilder.Redirect)` |  |
| `ProcessBuilder redirectInput(java.io.File)` |  |
| `java.lang.ProcessBuilder.Redirect redirectInput()` |  |
| `ProcessBuilder redirectOutput(java.lang.ProcessBuilder.Redirect)` |  |
| `ProcessBuilder redirectOutput(java.io.File)` |  |
| `java.lang.ProcessBuilder.Redirect redirectOutput()` |  |
| `Process start() throws java.io.IOException` |  |
| `static java.lang.ProcessBuilder.Redirect appendTo(java.io.File)` |  |
| `java.io.File file()` |  |
| `static java.lang.ProcessBuilder.Redirect from(java.io.File)` |  |
| `static java.lang.ProcessBuilder.Redirect to(java.io.File)` |  |
| `abstract java.lang.ProcessBuilder.Redirect.Type type()` |  |

---

### `enum ProcessBuilder.Redirect.Type`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.lang.ProcessBuilder.Redirect.Type APPEND` |  |
| `static final java.lang.ProcessBuilder.Redirect.Type INHERIT` |  |
| `static final java.lang.ProcessBuilder.Redirect.Type PIPE` |  |
| `static final java.lang.ProcessBuilder.Redirect.Type READ` |  |
| `static final java.lang.ProcessBuilder.Redirect.Type WRITE` |  |

---

### `interface Readable`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int read(java.nio.CharBuffer) throws java.io.IOException` |  |

---

### `class ReflectiveOperationException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ReflectiveOperationException()` |  |
| `ReflectiveOperationException(String)` |  |
| `ReflectiveOperationException(String, Throwable)` |  |
| `ReflectiveOperationException(Throwable)` |  |

---

### `interface Runnable`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void run()` |  |

---

### `class Runtime`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addShutdownHook(Thread)` |  |
| `int availableProcessors()` |  |
| `Process exec(String) throws java.io.IOException` |  |
| `Process exec(String, String[]) throws java.io.IOException` |  |
| `Process exec(String, String[], java.io.File) throws java.io.IOException` |  |
| `Process exec(String[]) throws java.io.IOException` |  |
| `Process exec(String[], String[]) throws java.io.IOException` |  |
| `Process exec(String[], String[], java.io.File) throws java.io.IOException` |  |
| `void exit(int)` |  |
| `long freeMemory()` |  |
| `void gc()` |  |
| `static Runtime getRuntime()` |  |
| `void halt(int)` |  |
| `void load(String)` |  |
| `void loadLibrary(String)` |  |
| `long maxMemory()` |  |
| `boolean removeShutdownHook(Thread)` |  |
| `void runFinalization()` |  |
| `long totalMemory()` |  |
| `void traceInstructions(boolean)` |  |
| `void traceMethodCalls(boolean)` |  |

---

### `class RuntimeException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RuntimeException()` |  |
| `RuntimeException(String)` |  |
| `RuntimeException(String, Throwable)` |  |
| `RuntimeException(Throwable)` |  |
| `RuntimeException(String, Throwable, boolean, boolean)` |  |

---

### `class final RuntimePermission`

- **Extends:** `java.security.BasicPermission`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RuntimePermission(String)` |  |
| `RuntimePermission(String, String)` |  |

---

### `class SecurityException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SecurityException()` |  |
| `SecurityException(String)` |  |
| `SecurityException(String, Throwable)` |  |
| `SecurityException(Throwable)` |  |

---

### `class SecurityManager`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SecurityManager()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void checkAccept(String, int)` |  |
| `void checkAccess(Thread)` |  |
| `void checkAccess(ThreadGroup)` |  |
| `void checkAwtEventQueueAccess()` |  |
| `void checkConnect(String, int)` |  |
| `void checkConnect(String, int, Object)` |  |
| `void checkCreateClassLoader()` |  |
| `void checkDelete(String)` |  |
| `void checkExec(String)` |  |
| `void checkExit(int)` |  |
| `void checkLink(String)` |  |
| `void checkListen(int)` |  |
| `void checkMemberAccess(Class<?>, int)` |  |
| `void checkMulticast(java.net.InetAddress)` |  |
| `void checkPackageAccess(String)` |  |
| `void checkPackageDefinition(String)` |  |
| `void checkPermission(java.security.Permission)` |  |
| `void checkPermission(java.security.Permission, Object)` |  |
| `void checkPrintJobAccess()` |  |
| `void checkPropertiesAccess()` |  |
| `void checkPropertyAccess(String)` |  |
| `void checkRead(java.io.FileDescriptor)` |  |
| `void checkRead(String)` |  |
| `void checkRead(String, Object)` |  |
| `void checkSecurityAccess(String)` |  |
| `void checkSetFactory()` |  |
| `void checkSystemClipboardAccess()` |  |
| `boolean checkTopLevelWindow(Object)` |  |
| `void checkWrite(java.io.FileDescriptor)` |  |
| `void checkWrite(String)` |  |
| `Class[] getClassContext()` |  |
| `Object getSecurityContext()` |  |
| `ThreadGroup getThreadGroup()` |  |

---

### `class final Short`

- **Extends:** `java.lang.Number`
- **Implements:** `java.lang.Comparable<java.lang.Short>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Short(short)` |  |
| `Short(String) throws java.lang.NumberFormatException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BYTES = 2` |  |
| `static final short MAX_VALUE = 32767` |  |
| `static final short MIN_VALUE = -32768` |  |
| `static final int SIZE = 16` |  |
| `static final Class<java.lang.Short> TYPE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int compare(short, short)` |  |
| `int compareTo(Short)` |  |
| `static Short decode(String) throws java.lang.NumberFormatException` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `static int hashCode(short)` |  |
| `int intValue()` |  |
| `long longValue()` |  |
| `static short parseShort(String, int) throws java.lang.NumberFormatException` |  |
| `static short parseShort(String) throws java.lang.NumberFormatException` |  |
| `static short reverseBytes(short)` |  |
| `static String toString(short)` |  |
| `static int toUnsignedInt(short)` |  |
| `static long toUnsignedLong(short)` |  |
| `static Short valueOf(String, int) throws java.lang.NumberFormatException` |  |
| `static Short valueOf(String) throws java.lang.NumberFormatException` |  |
| `static Short valueOf(short)` |  |

---

### `class StackOverflowError`

- **Extends:** `java.lang.VirtualMachineError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StackOverflowError()` |  |
| `StackOverflowError(String)` |  |

---

### `class final StackTraceElement`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StackTraceElement(String, String, String, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getClassName()` |  |
| `String getFileName()` |  |
| `int getLineNumber()` |  |
| `String getMethodName()` |  |
| `boolean isNativeMethod()` |  |

---

### `class final StrictMath`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final double E = 2.718281828459045` |  |
| `static final double PI = 3.141592653589793` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static double IEEEremainder(double, double)` |  |
| `static int abs(int)` |  |
| `static long abs(long)` |  |
| `static float abs(float)` |  |
| `static double abs(double)` |  |
| `static double acos(double)` |  |
| `static int addExact(int, int)` |  |
| `static long addExact(long, long)` |  |
| `static double asin(double)` |  |
| `static double atan(double)` |  |
| `static double atan2(double, double)` |  |
| `static double cbrt(double)` |  |
| `static double ceil(double)` |  |
| `static double copySign(double, double)` |  |
| `static float copySign(float, float)` |  |
| `static double cos(double)` |  |
| `static double cosh(double)` |  |
| `static double exp(double)` |  |
| `static double expm1(double)` |  |
| `static double floor(double)` |  |
| `static int floorDiv(int, int)` |  |
| `static long floorDiv(long, long)` |  |
| `static int floorMod(int, int)` |  |
| `static long floorMod(long, long)` |  |
| `static int getExponent(float)` |  |
| `static int getExponent(double)` |  |
| `static double hypot(double, double)` |  |
| `static double log(double)` |  |
| `static double log10(double)` |  |
| `static double log1p(double)` |  |
| `static int max(int, int)` |  |
| `static long max(long, long)` |  |
| `static float max(float, float)` |  |
| `static double max(double, double)` |  |
| `static int min(int, int)` |  |
| `static long min(long, long)` |  |
| `static float min(float, float)` |  |
| `static double min(double, double)` |  |
| `static int multiplyExact(int, int)` |  |
| `static long multiplyExact(long, long)` |  |
| `static double nextAfter(double, double)` |  |
| `static float nextAfter(float, double)` |  |
| `static double nextDown(double)` |  |
| `static float nextDown(float)` |  |
| `static double nextUp(double)` |  |
| `static float nextUp(float)` |  |
| `static double pow(double, double)` |  |
| `static double random()` |  |
| `static double rint(double)` |  |
| `static int round(float)` |  |
| `static long round(double)` |  |
| `static double scalb(double, int)` |  |
| `static float scalb(float, int)` |  |
| `static double signum(double)` |  |
| `static float signum(float)` |  |
| `static double sin(double)` |  |
| `static double sinh(double)` |  |
| `static double sqrt(double)` |  |
| `static int subtractExact(int, int)` |  |
| `static long subtractExact(long, long)` |  |
| `static double tan(double)` |  |
| `static double tanh(double)` |  |
| `static double toDegrees(double)` |  |
| `static int toIntExact(long)` |  |
| `static double toRadians(double)` |  |
| `static double ulp(double)` |  |
| `static float ulp(float)` |  |

---

### `class final String`

- **Implements:** `java.lang.CharSequence java.lang.Comparable<java.lang.String> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `String()` |  |
| `String(@NonNull String)` |  |
| `String(char[])` |  |
| `String(char[], int, int)` |  |
| `String(int[], int, int)` |  |
| `String(byte[], int, int, @NonNull String) throws java.io.UnsupportedEncodingException` |  |
| `String(byte[], int, int, @NonNull java.nio.charset.Charset)` |  |
| `String(byte[], @NonNull String) throws java.io.UnsupportedEncodingException` |  |
| `String(byte[], @NonNull java.nio.charset.Charset)` |  |
| `String(byte[], int, int)` |  |
| `String(byte[])` |  |
| `String(@NonNull StringBuffer)` |  |
| `String(@NonNull StringBuilder)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.util.Comparator<java.lang.String> CASE_INSENSITIVE_ORDER` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `char charAt(int)` |  |
| `int codePointAt(int)` |  |
| `int codePointBefore(int)` |  |
| `int codePointCount(int, int)` |  |
| `int compareTo(@NonNull String)` |  |
| `int compareToIgnoreCase(@NonNull String)` |  |
| `boolean contains(@NonNull CharSequence)` |  |
| `boolean contentEquals(@NonNull StringBuffer)` |  |
| `boolean contentEquals(@NonNull CharSequence)` |  |
| `boolean endsWith(@NonNull String)` |  |
| `boolean equalsIgnoreCase(@Nullable String)` |  |
| `byte[] getBytes(@NonNull String) throws java.io.UnsupportedEncodingException` |  |
| `byte[] getBytes(@NonNull java.nio.charset.Charset)` |  |
| `byte[] getBytes()` |  |
| `void getChars(int, int, char[], int)` |  |
| `int indexOf(int)` |  |
| `int indexOf(int, int)` |  |
| `int indexOf(@NonNull String)` |  |
| `int indexOf(@NonNull String, int)` |  |
| `boolean isEmpty()` |  |
| `int lastIndexOf(int)` |  |
| `int lastIndexOf(int, int)` |  |
| `int lastIndexOf(@NonNull String)` |  |
| `int lastIndexOf(@NonNull String, int)` |  |
| `int length()` |  |
| `boolean matches(@NonNull String)` |  |
| `int offsetByCodePoints(int, int)` |  |
| `boolean regionMatches(int, @NonNull String, int, int)` |  |
| `boolean regionMatches(boolean, int, @NonNull String, int, int)` |  |
| `boolean startsWith(@NonNull String, int)` |  |
| `boolean startsWith(@NonNull String)` |  |
| `char[] toCharArray()` |  |

---

### `class final StringBuffer`

- **Implements:** `java.lang.Appendable java.lang.CharSequence java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StringBuffer()` |  |
| `StringBuffer(int)` |  |
| `StringBuffer(@NonNull String)` |  |
| `StringBuffer(@NonNull CharSequence)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int capacity()` |  |
| `char charAt(int)` |  |
| `int codePointAt(int)` |  |
| `int codePointBefore(int)` |  |
| `int codePointCount(int, int)` |  |
| `void ensureCapacity(int)` |  |
| `void getChars(int, int, char[], int)` |  |
| `int indexOf(@NonNull String)` |  |
| `int indexOf(@NonNull String, int)` |  |
| `int lastIndexOf(@NonNull String)` |  |
| `int lastIndexOf(@NonNull String, int)` |  |
| `int length()` |  |
| `int offsetByCodePoints(int, int)` |  |
| `void setCharAt(int, char)` |  |
| `void setLength(int)` |  |
| `void trimToSize()` |  |

---

### `class final StringBuilder`

- **Implements:** `java.lang.Appendable java.lang.CharSequence java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StringBuilder()` |  |
| `StringBuilder(int)` |  |
| `StringBuilder(@NonNull String)` |  |
| `StringBuilder(@NonNull CharSequence)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int capacity()` |  |
| `char charAt(int)` |  |
| `int codePointAt(int)` |  |
| `int codePointBefore(int)` |  |
| `int codePointCount(int, int)` |  |
| `void ensureCapacity(int)` |  |
| `void getChars(int, int, char[], int)` |  |
| `int indexOf(@NonNull String)` |  |
| `int indexOf(@NonNull String, int)` |  |
| `int lastIndexOf(@NonNull String)` |  |
| `int lastIndexOf(@NonNull String, int)` |  |
| `int length()` |  |
| `int offsetByCodePoints(int, int)` |  |
| `void setCharAt(int, char)` |  |
| `void setLength(int)` |  |
| `void trimToSize()` |  |

---

### `class StringIndexOutOfBoundsException`

- **Extends:** `java.lang.IndexOutOfBoundsException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StringIndexOutOfBoundsException()` |  |
| `StringIndexOutOfBoundsException(String)` |  |
| `StringIndexOutOfBoundsException(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract String[] value()` |  |

---

### `class final System`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.io.PrintStream err` |  |
| `static final java.io.InputStream in` |  |
| `static final java.io.PrintStream out` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static void arraycopy(@NonNull Object, int, @NonNull Object, int, int)` |  |
| `static long currentTimeMillis()` |  |
| `static void exit(int)` |  |
| `static void gc()` |  |
| `static int identityHashCode(@Nullable Object)` |  |
| `static void load(@NonNull String)` |  |
| `static void loadLibrary(@NonNull String)` |  |
| `static long nanoTime()` |  |
| `static void runFinalization()` |  |
| `static void setErr(@Nullable java.io.PrintStream)` |  |
| `static void setIn(@Nullable java.io.InputStream)` |  |
| `static void setOut(@Nullable java.io.PrintStream)` |  |
| `static void setProperties(@Nullable java.util.Properties)` |  |
| `static void setSecurityManager(@Nullable SecurityManager)` |  |

---

### `class Thread`

- **Implements:** `java.lang.Runnable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Thread()` |  |
| `Thread(@Nullable Runnable)` |  |
| `Thread(@Nullable ThreadGroup, @Nullable Runnable)` |  |
| `Thread(@NonNull String)` |  |
| `Thread(@Nullable ThreadGroup, @NonNull String)` |  |
| `Thread(@Nullable Runnable, @NonNull String)` |  |
| `Thread(@Nullable ThreadGroup, @Nullable Runnable, @NonNull String)` |  |
| `Thread(@Nullable ThreadGroup, @Nullable Runnable, @NonNull String, long)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MAX_PRIORITY = 10` |  |
| `static final int MIN_PRIORITY = 1` |  |
| `static final int NORM_PRIORITY = 5` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int activeCount()` |  |
| `final void checkAccess()` |  |
| `static void dumpStack()` |  |
| `static int enumerate(Thread[])` |  |
| `long getId()` |  |
| `final int getPriority()` |  |
| `static boolean holdsLock(@NonNull Object)` |  |
| `void interrupt()` |  |
| `static boolean interrupted()` |  |
| `final boolean isAlive()` |  |
| `final boolean isDaemon()` |  |
| `boolean isInterrupted()` |  |
| `final void join(long) throws java.lang.InterruptedException` |  |
| `final void join(long, int) throws java.lang.InterruptedException` |  |
| `final void join() throws java.lang.InterruptedException` |  |
| `void run()` |  |
| `void setContextClassLoader(@Nullable ClassLoader)` |  |
| `final void setDaemon(boolean)` |  |
| `static void setDefaultUncaughtExceptionHandler(@Nullable java.lang.Thread.UncaughtExceptionHandler)` |  |
| `final void setName(@NonNull String)` |  |
| `final void setPriority(int)` |  |
| `void setUncaughtExceptionHandler(@Nullable java.lang.Thread.UncaughtExceptionHandler)` |  |
| `static void sleep(long) throws java.lang.InterruptedException` |  |
| `static void sleep(long, int) throws java.lang.InterruptedException` |  |
| `void start()` |  |
| `static void yield()` |  |

---

### `enum Thread.State`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.lang.Thread.State BLOCKED` |  |
| `static final java.lang.Thread.State NEW` |  |
| `static final java.lang.Thread.State RUNNABLE` |  |
| `static final java.lang.Thread.State TERMINATED` |  |
| `static final java.lang.Thread.State TIMED_WAITING` |  |
| `static final java.lang.Thread.State WAITING` |  |

---

### `interface static Thread.UncaughtExceptionHandler`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void uncaughtException(@NonNull Thread, @NonNull Throwable)` |  |

---

### `class ThreadDeath`

- **Extends:** `java.lang.Error`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ThreadDeath()` |  |

---

### `class ThreadGroup`

- **Implements:** `java.lang.Thread.UncaughtExceptionHandler`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ThreadGroup(String)` |  |
| `ThreadGroup(ThreadGroup, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int activeCount()` |  |
| `int activeGroupCount()` |  |
| `final void checkAccess()` |  |
| `final void destroy()` |  |
| `int enumerate(Thread[])` |  |
| `int enumerate(Thread[], boolean)` |  |
| `int enumerate(ThreadGroup[])` |  |
| `int enumerate(ThreadGroup[], boolean)` |  |
| `final int getMaxPriority()` |  |
| `final String getName()` |  |
| `final ThreadGroup getParent()` |  |
| `final void interrupt()` |  |
| `final boolean isDaemon()` |  |
| `boolean isDestroyed()` |  |
| `void list()` |  |
| `final boolean parentOf(ThreadGroup)` |  |
| `final void setDaemon(boolean)` |  |
| `final void setMaxPriority(int)` |  |
| `void uncaughtException(Thread, Throwable)` |  |

---

### `class ThreadLocal<T>`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ThreadLocal()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void remove()` |  |
| `void set(T)` |  |

---

### `class Throwable`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Throwable()` |  |
| `Throwable(@Nullable String)` |  |
| `Throwable(@Nullable String, @Nullable Throwable)` |  |
| `Throwable(@Nullable Throwable)` |  |
| `Throwable(@Nullable String, @Nullable Throwable, boolean, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void addSuppressed(@NonNull Throwable)` |  |
| `void printStackTrace()` |  |
| `void printStackTrace(@NonNull java.io.PrintStream)` |  |
| `void printStackTrace(@NonNull java.io.PrintWriter)` |  |
| `void setStackTrace(@NonNull StackTraceElement[])` |  |

---

### `class TypeNotPresentException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TypeNotPresentException(String, Throwable)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String typeName()` |  |

---

### `class UnknownError`

- **Extends:** `java.lang.VirtualMachineError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnknownError()` |  |
| `UnknownError(String)` |  |

---

### `class UnsatisfiedLinkError`

- **Extends:** `java.lang.LinkageError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnsatisfiedLinkError()` |  |
| `UnsatisfiedLinkError(String)` |  |

---

### `class UnsupportedClassVersionError`

- **Extends:** `java.lang.ClassFormatError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnsupportedClassVersionError()` |  |
| `UnsupportedClassVersionError(String)` |  |

---

### `class UnsupportedOperationException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnsupportedOperationException()` |  |
| `UnsupportedOperationException(String)` |  |
| `UnsupportedOperationException(String, Throwable)` |  |
| `UnsupportedOperationException(Throwable)` |  |

---

### `class VerifyError`

- **Extends:** `java.lang.LinkageError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `VerifyError()` |  |
| `VerifyError(String)` |  |

---

### `class abstract VirtualMachineError`

- **Extends:** `java.lang.Error`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `VirtualMachineError()` |  |
| `VirtualMachineError(String)` |  |
| `VirtualMachineError(String, Throwable)` |  |
| `VirtualMachineError(Throwable)` |  |

---

### `class final Void`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final Class<java.lang.Void> TYPE` |  |

---

## Package: `java.lang.annotation`

### `interface Annotation`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Class<? extends java.lang.annotation.Annotation> annotationType()` |  |
| `boolean equals(Object)` |  |
| `int hashCode()` |  |
| `String toString()` |  |

---

### `class AnnotationFormatError`

- **Extends:** `java.lang.Error`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AnnotationFormatError(String)` |  |
| `AnnotationFormatError(String, Throwable)` |  |
| `AnnotationFormatError(Throwable)` |  |

---

### `class AnnotationTypeMismatchException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AnnotationTypeMismatchException(java.lang.reflect.Method, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.lang.reflect.Method element()` |  |
| `String foundType()` |  |

---

### `enum ElementType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.lang.annotation.ElementType ANNOTATION_TYPE` |  |
| `static final java.lang.annotation.ElementType CONSTRUCTOR` |  |
| `static final java.lang.annotation.ElementType FIELD` |  |
| `static final java.lang.annotation.ElementType LOCAL_VARIABLE` |  |
| `static final java.lang.annotation.ElementType METHOD` |  |
| `static final java.lang.annotation.ElementType PACKAGE` |  |
| `static final java.lang.annotation.ElementType PARAMETER` |  |
| `static final java.lang.annotation.ElementType TYPE` |  |
| `static final java.lang.annotation.ElementType TYPE_PARAMETER` |  |
| `static final java.lang.annotation.ElementType TYPE_USE` |  |

---

### `class IncompleteAnnotationException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IncompleteAnnotationException(Class<? extends java.lang.annotation.Annotation>, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Class<? extends java.lang.annotation.Annotation> annotationType()` |  |
| `String elementName()` |  |
| `abstract Class<? extends java.lang.annotation.Annotation> value()` |  |
| `abstract java.lang.annotation.RetentionPolicy value()` |  |

---

### `enum RetentionPolicy`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.lang.annotation.RetentionPolicy CLASS` |  |
| `static final java.lang.annotation.RetentionPolicy RUNTIME` |  |
| `static final java.lang.annotation.RetentionPolicy SOURCE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.lang.annotation.ElementType[] value()` |  |

---

## Package: `java.lang.invoke`

### `class abstract CallSite`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.lang.invoke.MethodHandle dynamicInvoker()` |  |
| `abstract java.lang.invoke.MethodHandle getTarget()` |  |
| `abstract void setTarget(java.lang.invoke.MethodHandle)` |  |
| `java.lang.invoke.MethodType type()` |  |

---

### `class ConstantCallSite`

- **Extends:** `java.lang.invoke.CallSite`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConstantCallSite(java.lang.invoke.MethodHandle)` |  |
| `ConstantCallSite(java.lang.invoke.MethodType, java.lang.invoke.MethodHandle) throws java.lang.Throwable` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.lang.invoke.MethodHandle dynamicInvoker()` |  |
| `final java.lang.invoke.MethodHandle getTarget()` |  |
| `final void setTarget(java.lang.invoke.MethodHandle)` |  |

---

### `class LambdaConversionException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LambdaConversionException()` |  |
| `LambdaConversionException(String)` |  |
| `LambdaConversionException(String, Throwable)` |  |
| `LambdaConversionException(Throwable)` |  |
| `LambdaConversionException(String, Throwable, boolean, boolean)` |  |

---

### `class abstract MethodHandle`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.lang.invoke.MethodHandle asCollector(Class<?>, int)` |  |
| `java.lang.invoke.MethodHandle asFixedArity()` |  |
| `java.lang.invoke.MethodHandle asSpreader(Class<?>, int)` |  |
| `java.lang.invoke.MethodHandle asType(java.lang.invoke.MethodType)` |  |
| `java.lang.invoke.MethodHandle asVarargsCollector(Class<?>)` |  |
| `java.lang.invoke.MethodHandle bindTo(Object)` |  |
| `final Object invoke(java.lang.Object...) throws java.lang.Throwable` |  |
| `final Object invokeExact(java.lang.Object...) throws java.lang.Throwable` |  |
| `Object invokeWithArguments(java.lang.Object...) throws java.lang.Throwable` |  |
| `Object invokeWithArguments(java.util.List<?>) throws java.lang.Throwable` |  |
| `boolean isVarargsCollector()` |  |
| `java.lang.invoke.MethodType type()` |  |

---

### `interface MethodHandleInfo`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int REF_getField = 1` |  |
| `static final int REF_getStatic = 2` |  |
| `static final int REF_invokeInterface = 9` |  |
| `static final int REF_invokeSpecial = 7` |  |
| `static final int REF_invokeStatic = 6` |  |
| `static final int REF_invokeVirtual = 5` |  |
| `static final int REF_newInvokeSpecial = 8` |  |
| `static final int REF_putField = 3` |  |
| `static final int REF_putStatic = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Class<?> getDeclaringClass()` |  |
| `java.lang.invoke.MethodType getMethodType()` |  |
| `int getModifiers()` |  |
| `String getName()` |  |
| `int getReferenceKind()` |  |
| `default boolean isVarArgs()` |  |
| `static String referenceKindToString(int)` |  |
| `<T extends java.lang.reflect.Member> T reflectAs(Class<T>, java.lang.invoke.MethodHandles.Lookup)` |  |
| `static String toString(int, Class<?>, String, java.lang.invoke.MethodType)` |  |

---

### `class MethodHandles`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.lang.invoke.MethodHandle arrayElementGetter(Class<?>) throws java.lang.IllegalArgumentException` |  |
| `static java.lang.invoke.MethodHandle arrayElementSetter(Class<?>) throws java.lang.IllegalArgumentException` |  |
| `static java.lang.invoke.MethodHandle catchException(java.lang.invoke.MethodHandle, Class<? extends java.lang.Throwable>, java.lang.invoke.MethodHandle)` |  |
| `static java.lang.invoke.MethodHandle collectArguments(java.lang.invoke.MethodHandle, int, java.lang.invoke.MethodHandle)` |  |
| `static java.lang.invoke.MethodHandle constant(Class<?>, Object)` |  |
| `static java.lang.invoke.MethodHandle dropArguments(java.lang.invoke.MethodHandle, int, java.util.List<java.lang.Class<?>>)` |  |
| `static java.lang.invoke.MethodHandle dropArguments(java.lang.invoke.MethodHandle, int, Class<?>...)` |  |
| `static java.lang.invoke.MethodHandle exactInvoker(java.lang.invoke.MethodType)` |  |
| `static java.lang.invoke.MethodHandle explicitCastArguments(java.lang.invoke.MethodHandle, java.lang.invoke.MethodType)` |  |
| `static java.lang.invoke.MethodHandle filterArguments(java.lang.invoke.MethodHandle, int, java.lang.invoke.MethodHandle...)` |  |
| `static java.lang.invoke.MethodHandle filterReturnValue(java.lang.invoke.MethodHandle, java.lang.invoke.MethodHandle)` |  |
| `static java.lang.invoke.MethodHandle foldArguments(java.lang.invoke.MethodHandle, java.lang.invoke.MethodHandle)` |  |
| `static java.lang.invoke.MethodHandle guardWithTest(java.lang.invoke.MethodHandle, java.lang.invoke.MethodHandle, java.lang.invoke.MethodHandle)` |  |
| `static java.lang.invoke.MethodHandle identity(Class<?>)` |  |
| `static java.lang.invoke.MethodHandle insertArguments(java.lang.invoke.MethodHandle, int, java.lang.Object...)` |  |
| `static java.lang.invoke.MethodHandle invoker(java.lang.invoke.MethodType)` |  |
| `static java.lang.invoke.MethodHandles.Lookup lookup()` |  |
| `static java.lang.invoke.MethodHandle permuteArguments(java.lang.invoke.MethodHandle, java.lang.invoke.MethodType, int...)` |  |
| `static java.lang.invoke.MethodHandles.Lookup publicLookup()` |  |
| `static <T extends java.lang.reflect.Member> T reflectAs(Class<T>, java.lang.invoke.MethodHandle)` |  |
| `static java.lang.invoke.MethodHandle spreadInvoker(java.lang.invoke.MethodType, int)` |  |
| `static java.lang.invoke.MethodHandle throwException(Class<?>, Class<? extends java.lang.Throwable>)` |  |

---

### `class static final MethodHandles.Lookup`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PACKAGE = 8` |  |
| `static final int PRIVATE = 2` |  |
| `static final int PROTECTED = 4` |  |
| `static final int PUBLIC = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.lang.invoke.MethodHandle bind(Object, String, java.lang.invoke.MethodType) throws java.lang.IllegalAccessException, java.lang.NoSuchMethodException` |  |
| `java.lang.invoke.MethodHandle findConstructor(Class<?>, java.lang.invoke.MethodType) throws java.lang.IllegalAccessException, java.lang.NoSuchMethodException` |  |
| `java.lang.invoke.MethodHandle findGetter(Class<?>, String, Class<?>) throws java.lang.IllegalAccessException, java.lang.NoSuchFieldException` |  |
| `java.lang.invoke.MethodHandle findSetter(Class<?>, String, Class<?>) throws java.lang.IllegalAccessException, java.lang.NoSuchFieldException` |  |
| `java.lang.invoke.MethodHandle findSpecial(Class<?>, String, java.lang.invoke.MethodType, Class<?>) throws java.lang.IllegalAccessException, java.lang.NoSuchMethodException` |  |
| `java.lang.invoke.MethodHandle findStatic(Class<?>, String, java.lang.invoke.MethodType) throws java.lang.IllegalAccessException, java.lang.NoSuchMethodException` |  |
| `java.lang.invoke.MethodHandle findStaticGetter(Class<?>, String, Class<?>) throws java.lang.IllegalAccessException, java.lang.NoSuchFieldException` |  |
| `java.lang.invoke.MethodHandle findStaticSetter(Class<?>, String, Class<?>) throws java.lang.IllegalAccessException, java.lang.NoSuchFieldException` |  |
| `java.lang.invoke.MethodHandle findVirtual(Class<?>, String, java.lang.invoke.MethodType) throws java.lang.IllegalAccessException, java.lang.NoSuchMethodException` |  |
| `java.lang.invoke.MethodHandles.Lookup in(Class<?>)` |  |
| `Class<?> lookupClass()` |  |
| `int lookupModes()` |  |
| `java.lang.invoke.MethodHandleInfo revealDirect(java.lang.invoke.MethodHandle)` |  |
| `java.lang.invoke.MethodHandle unreflect(java.lang.reflect.Method) throws java.lang.IllegalAccessException` |  |
| `java.lang.invoke.MethodHandle unreflectConstructor(java.lang.reflect.Constructor<?>) throws java.lang.IllegalAccessException` |  |
| `java.lang.invoke.MethodHandle unreflectGetter(java.lang.reflect.Field) throws java.lang.IllegalAccessException` |  |
| `java.lang.invoke.MethodHandle unreflectSetter(java.lang.reflect.Field) throws java.lang.IllegalAccessException` |  |
| `java.lang.invoke.MethodHandle unreflectSpecial(java.lang.reflect.Method, Class<?>) throws java.lang.IllegalAccessException` |  |

---

### `class final MethodType`

- **Implements:** `java.io.Serializable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.lang.invoke.MethodType appendParameterTypes(Class<?>...)` |  |
| `java.lang.invoke.MethodType appendParameterTypes(java.util.List<java.lang.Class<?>>)` |  |
| `java.lang.invoke.MethodType changeParameterType(int, Class<?>)` |  |
| `java.lang.invoke.MethodType changeReturnType(Class<?>)` |  |
| `java.lang.invoke.MethodType dropParameterTypes(int, int)` |  |
| `java.lang.invoke.MethodType erase()` |  |
| `static java.lang.invoke.MethodType fromMethodDescriptorString(String, ClassLoader) throws java.lang.IllegalArgumentException, java.lang.TypeNotPresentException` |  |
| `java.lang.invoke.MethodType generic()` |  |
| `static java.lang.invoke.MethodType genericMethodType(int, boolean)` |  |
| `static java.lang.invoke.MethodType genericMethodType(int)` |  |
| `boolean hasPrimitives()` |  |
| `boolean hasWrappers()` |  |
| `java.lang.invoke.MethodType insertParameterTypes(int, Class<?>...)` |  |
| `java.lang.invoke.MethodType insertParameterTypes(int, java.util.List<java.lang.Class<?>>)` |  |
| `static java.lang.invoke.MethodType methodType(Class<?>, Class<?>[])` |  |
| `static java.lang.invoke.MethodType methodType(Class<?>, java.util.List<java.lang.Class<?>>)` |  |
| `static java.lang.invoke.MethodType methodType(Class<?>, Class<?>, Class<?>...)` |  |
| `static java.lang.invoke.MethodType methodType(Class<?>)` |  |
| `static java.lang.invoke.MethodType methodType(Class<?>, Class<?>)` |  |
| `static java.lang.invoke.MethodType methodType(Class<?>, java.lang.invoke.MethodType)` |  |
| `Class<?>[] parameterArray()` |  |
| `int parameterCount()` |  |
| `java.util.List<java.lang.Class<?>> parameterList()` |  |
| `Class<?> parameterType(int)` |  |
| `Class<?> returnType()` |  |
| `String toMethodDescriptorString()` |  |
| `java.lang.invoke.MethodType unwrap()` |  |
| `java.lang.invoke.MethodType wrap()` |  |

---

### `class MutableCallSite`

- **Extends:** `java.lang.invoke.CallSite`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MutableCallSite(java.lang.invoke.MethodType)` |  |
| `MutableCallSite(java.lang.invoke.MethodHandle)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.lang.invoke.MethodHandle dynamicInvoker()` |  |
| `final java.lang.invoke.MethodHandle getTarget()` |  |
| `void setTarget(java.lang.invoke.MethodHandle)` |  |

---

### `class VolatileCallSite`

- **Extends:** `java.lang.invoke.CallSite`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `VolatileCallSite(java.lang.invoke.MethodType)` |  |
| `VolatileCallSite(java.lang.invoke.MethodHandle)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.lang.invoke.MethodHandle dynamicInvoker()` |  |
| `final java.lang.invoke.MethodHandle getTarget()` |  |
| `void setTarget(java.lang.invoke.MethodHandle)` |  |

---

### `class WrongMethodTypeException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WrongMethodTypeException()` |  |
| `WrongMethodTypeException(String)` |  |

---

## Package: `java.lang.ref`

### `class PhantomReference<T>`

- **Extends:** `java.lang.ref.Reference<T>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PhantomReference(T, java.lang.ref.ReferenceQueue<? super T>)` |  |

---

### `class abstract Reference<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clear()` |  |
| `boolean enqueue()` |  |
| `T get()` |  |
| `boolean isEnqueued()` |  |
| `static void reachabilityFence(Object)` |  |

---

### `class ReferenceQueue<T>`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ReferenceQueue()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.lang.ref.Reference<? extends T> poll()` |  |
| `java.lang.ref.Reference<? extends T> remove(long) throws java.lang.IllegalArgumentException, java.lang.InterruptedException` |  |
| `java.lang.ref.Reference<? extends T> remove() throws java.lang.InterruptedException` |  |

---

### `class SoftReference<T>`

- **Extends:** `java.lang.ref.Reference<T>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SoftReference(T)` |  |
| `SoftReference(T, java.lang.ref.ReferenceQueue<? super T>)` |  |

---

### `class WeakReference<T>`

- **Extends:** `java.lang.ref.Reference<T>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WeakReference(T)` |  |
| `WeakReference(T, java.lang.ref.ReferenceQueue<? super T>)` |  |

---

## Package: `java.lang.reflect`

### `class AccessibleObject`

- **Implements:** `java.lang.reflect.AnnotatedElement`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibleObject()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isAccessible()` |  |
| `static void setAccessible(java.lang.reflect.AccessibleObject[], boolean) throws java.lang.SecurityException` |  |
| `void setAccessible(boolean) throws java.lang.SecurityException` |  |

---

### `interface AnnotatedElement`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default <T extends java.lang.annotation.Annotation> T[] getAnnotationsByType(@NonNull Class<T>)` |  |
| `default <T extends java.lang.annotation.Annotation> T[] getDeclaredAnnotationsByType(@NonNull Class<T>)` |  |
| `default boolean isAnnotationPresent(@NonNull Class<? extends java.lang.annotation.Annotation>)` |  |

---

### `class final Array`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static boolean getBoolean(@NonNull Object, int) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static byte getByte(@NonNull Object, int) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static char getChar(@NonNull Object, int) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static double getDouble(@NonNull Object, int) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static float getFloat(@NonNull Object, int) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static int getInt(@NonNull Object, int) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static int getLength(@NonNull Object)` |  |
| `static long getLong(@NonNull Object, int) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static short getShort(@NonNull Object, int) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static void set(@NonNull Object, int, @Nullable Object) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static void setBoolean(@NonNull Object, int, boolean)` |  |
| `static void setByte(@NonNull Object, int, byte) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static void setChar(@NonNull Object, int, char) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static void setDouble(@NonNull Object, int, double) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static void setFloat(@NonNull Object, int, float) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static void setInt(@NonNull Object, int, int) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static void setLong(@NonNull Object, int, long) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |
| `static void setShort(@NonNull Object, int, short) throws java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException` |  |

---

### `class final Constructor<T>`

- **Extends:** `java.lang.reflect.Executable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Class<?>[] getExceptionTypes()` |  |
| `int getModifiers()` |  |
| `java.lang.annotation.Annotation[][] getParameterAnnotations()` |  |
| `java.lang.reflect.TypeVariable<java.lang.reflect.Constructor<T>>[] getTypeParameters()` |  |

---

### `class abstract Executable`

- **Extends:** `java.lang.reflect.AccessibleObject`
- **Implements:** `java.lang.reflect.GenericDeclaration java.lang.reflect.Member`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getParameterCount()` |  |
| `final boolean isAnnotationPresent(@NonNull Class<? extends java.lang.annotation.Annotation>)` |  |
| `boolean isSynthetic()` |  |
| `boolean isVarArgs()` |  |

---

### `class final Field`

- **Extends:** `java.lang.reflect.AccessibleObject`
- **Implements:** `java.lang.reflect.Member`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean getBoolean(@Nullable Object) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `byte getByte(@Nullable Object) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `char getChar(@Nullable Object) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `double getDouble(@Nullable Object) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `float getFloat(@Nullable Object) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `int getInt(@Nullable Object) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `long getLong(@Nullable Object) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `int getModifiers()` |  |
| `short getShort(@Nullable Object) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `boolean isEnumConstant()` |  |
| `boolean isSynthetic()` |  |
| `void set(@Nullable Object, @Nullable Object) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `void setBoolean(@Nullable Object, boolean) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `void setByte(@Nullable Object, byte) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `void setChar(@Nullable Object, char) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `void setDouble(@Nullable Object, double) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `void setFloat(@Nullable Object, float) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `void setInt(@Nullable Object, int) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `void setLong(@Nullable Object, long) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |
| `void setShort(@Nullable Object, short) throws java.lang.IllegalAccessException, java.lang.IllegalArgumentException` |  |

---

### `interface GenericArrayType`

- **Extends:** `java.lang.reflect.Type`

---

### `interface GenericDeclaration`

- **Extends:** `java.lang.reflect.AnnotatedElement`

---

### `class GenericSignatureFormatError`

- **Extends:** `java.lang.ClassFormatError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GenericSignatureFormatError()` |  |
| `GenericSignatureFormatError(String)` |  |

---

### `interface InvocationHandler`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object invoke(Object, java.lang.reflect.Method, Object[]) throws java.lang.Throwable` |  |

---

### `class InvocationTargetException`

- **Extends:** `java.lang.ReflectiveOperationException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvocationTargetException()` |  |
| `InvocationTargetException(Throwable)` |  |
| `InvocationTargetException(Throwable, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Throwable getTargetException()` |  |

---

### `class MalformedParameterizedTypeException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MalformedParameterizedTypeException()` |  |

---

### `class MalformedParametersException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MalformedParametersException()` |  |
| `MalformedParametersException(String)` |  |

---

### `interface Member`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DECLARED = 1` |  |
| `static final int PUBLIC = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getModifiers()` |  |
| `boolean isSynthetic()` |  |

---

### `class final Method`

- **Extends:** `java.lang.reflect.Executable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getModifiers()` |  |
| `boolean isBridge()` |  |
| `boolean isDefault()` |  |

---

### `class Modifier`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Modifier()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ABSTRACT = 1024` |  |
| `static final int FINAL = 16` |  |
| `static final int INTERFACE = 512` |  |
| `static final int NATIVE = 256` |  |
| `static final int PRIVATE = 2` |  |
| `static final int PROTECTED = 4` |  |
| `static final int PUBLIC = 1` |  |
| `static final int STATIC = 8` |  |
| `static final int STRICT = 2048` |  |
| `static final int SYNCHRONIZED = 32` |  |
| `static final int TRANSIENT = 128` |  |
| `static final int VOLATILE = 64` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int classModifiers()` |  |
| `static int constructorModifiers()` |  |
| `static int fieldModifiers()` |  |
| `static int interfaceModifiers()` |  |
| `static boolean isAbstract(int)` |  |
| `static boolean isFinal(int)` |  |
| `static boolean isInterface(int)` |  |
| `static boolean isNative(int)` |  |
| `static boolean isPrivate(int)` |  |
| `static boolean isProtected(int)` |  |
| `static boolean isPublic(int)` |  |
| `static boolean isStatic(int)` |  |
| `static boolean isStrict(int)` |  |
| `static boolean isSynchronized(int)` |  |
| `static boolean isTransient(int)` |  |
| `static boolean isVolatile(int)` |  |
| `static int methodModifiers()` |  |
| `static int parameterModifiers()` |  |
| `static String toString(int)` |  |

---

### `class final Parameter`

- **Implements:** `java.lang.reflect.AnnotatedElement`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getModifiers()` |  |
| `boolean isImplicit()` |  |
| `boolean isNamePresent()` |  |
| `boolean isSynthetic()` |  |
| `boolean isVarArgs()` |  |

---

### `interface ParameterizedType`

- **Extends:** `java.lang.reflect.Type`

---

### `class Proxy`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Proxy(@NonNull java.lang.reflect.InvocationHandler)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.lang.reflect.InvocationHandler h` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static boolean isProxyClass(@NonNull Class<?>)` |  |

---

### `class final ReflectPermission`

- **Extends:** `java.security.BasicPermission`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ReflectPermission(String)` |  |
| `ReflectPermission(String, String)` |  |

---

### `interface Type`


---

### `interface TypeVariable<D`

- **Extends:** `java.lang.reflect.GenericDeclaration> extends java.lang.reflect.Type`

---

### `class UndeclaredThrowableException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UndeclaredThrowableException(Throwable)` |  |
| `UndeclaredThrowableException(Throwable, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Throwable getUndeclaredThrowable()` |  |

---

### `interface WildcardType`

- **Extends:** `java.lang.reflect.Type`

---

## Package: `java.math`

### `class BigDecimal`

- **Extends:** `java.lang.Number`
- **Implements:** `java.lang.Comparable<java.math.BigDecimal> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BigDecimal(char[], int, int)` |  |
| `BigDecimal(char[], int, int, java.math.MathContext)` |  |
| `BigDecimal(char[])` |  |
| `BigDecimal(char[], java.math.MathContext)` |  |
| `BigDecimal(String)` |  |
| `BigDecimal(String, java.math.MathContext)` |  |
| `BigDecimal(double)` |  |
| `BigDecimal(double, java.math.MathContext)` |  |
| `BigDecimal(java.math.BigInteger)` |  |
| `BigDecimal(java.math.BigInteger, java.math.MathContext)` |  |
| `BigDecimal(java.math.BigInteger, int)` |  |
| `BigDecimal(java.math.BigInteger, int, java.math.MathContext)` |  |
| `BigDecimal(int)` |  |
| `BigDecimal(int, java.math.MathContext)` |  |
| `BigDecimal(long)` |  |
| `BigDecimal(long, java.math.MathContext)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.math.BigDecimal ONE` |  |
| `static final int ROUND_CEILING = 2` |  |
| `static final int ROUND_DOWN = 1` |  |
| `static final int ROUND_FLOOR = 3` |  |
| `static final int ROUND_HALF_DOWN = 5` |  |
| `static final int ROUND_HALF_EVEN = 6` |  |
| `static final int ROUND_HALF_UP = 4` |  |
| `static final int ROUND_UNNECESSARY = 7` |  |
| `static final int ROUND_UP = 0` |  |
| `static final java.math.BigDecimal TEN` |  |
| `static final java.math.BigDecimal ZERO` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigDecimal abs()` |  |
| `java.math.BigDecimal abs(java.math.MathContext)` |  |
| `java.math.BigDecimal add(java.math.BigDecimal)` |  |
| `java.math.BigDecimal add(java.math.BigDecimal, java.math.MathContext)` |  |
| `byte byteValueExact()` |  |
| `int compareTo(java.math.BigDecimal)` |  |
| `java.math.BigDecimal divide(java.math.BigDecimal, int, int)` |  |
| `java.math.BigDecimal divide(java.math.BigDecimal, int, java.math.RoundingMode)` |  |
| `java.math.BigDecimal divide(java.math.BigDecimal, int)` |  |
| `java.math.BigDecimal divide(java.math.BigDecimal, java.math.RoundingMode)` |  |
| `java.math.BigDecimal divide(java.math.BigDecimal)` |  |
| `java.math.BigDecimal divide(java.math.BigDecimal, java.math.MathContext)` |  |
| `java.math.BigDecimal[] divideAndRemainder(java.math.BigDecimal)` |  |
| `java.math.BigDecimal[] divideAndRemainder(java.math.BigDecimal, java.math.MathContext)` |  |
| `java.math.BigDecimal divideToIntegralValue(java.math.BigDecimal)` |  |
| `java.math.BigDecimal divideToIntegralValue(java.math.BigDecimal, java.math.MathContext)` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `int intValue()` |  |
| `int intValueExact()` |  |
| `long longValue()` |  |
| `long longValueExact()` |  |
| `java.math.BigDecimal max(java.math.BigDecimal)` |  |
| `java.math.BigDecimal min(java.math.BigDecimal)` |  |
| `java.math.BigDecimal movePointLeft(int)` |  |
| `java.math.BigDecimal movePointRight(int)` |  |
| `java.math.BigDecimal multiply(java.math.BigDecimal)` |  |
| `java.math.BigDecimal multiply(java.math.BigDecimal, java.math.MathContext)` |  |
| `java.math.BigDecimal negate()` |  |
| `java.math.BigDecimal negate(java.math.MathContext)` |  |
| `java.math.BigDecimal plus()` |  |
| `java.math.BigDecimal plus(java.math.MathContext)` |  |
| `java.math.BigDecimal pow(int)` |  |
| `java.math.BigDecimal pow(int, java.math.MathContext)` |  |
| `int precision()` |  |
| `java.math.BigDecimal remainder(java.math.BigDecimal)` |  |
| `java.math.BigDecimal remainder(java.math.BigDecimal, java.math.MathContext)` |  |
| `java.math.BigDecimal round(java.math.MathContext)` |  |
| `int scale()` |  |
| `java.math.BigDecimal scaleByPowerOfTen(int)` |  |
| `java.math.BigDecimal setScale(int, java.math.RoundingMode)` |  |
| `java.math.BigDecimal setScale(int, int)` |  |
| `java.math.BigDecimal setScale(int)` |  |
| `short shortValueExact()` |  |
| `int signum()` |  |
| `java.math.BigDecimal stripTrailingZeros()` |  |
| `java.math.BigDecimal subtract(java.math.BigDecimal)` |  |
| `java.math.BigDecimal subtract(java.math.BigDecimal, java.math.MathContext)` |  |
| `java.math.BigInteger toBigInteger()` |  |
| `java.math.BigInteger toBigIntegerExact()` |  |
| `String toEngineeringString()` |  |
| `String toPlainString()` |  |
| `java.math.BigDecimal ulp()` |  |
| `java.math.BigInteger unscaledValue()` |  |
| `static java.math.BigDecimal valueOf(long, int)` |  |
| `static java.math.BigDecimal valueOf(long)` |  |
| `static java.math.BigDecimal valueOf(double)` |  |

---

### `class BigInteger`

- **Extends:** `java.lang.Number`
- **Implements:** `java.lang.Comparable<java.math.BigInteger> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BigInteger(int, @NonNull java.util.Random)` |  |
| `BigInteger(int, int, @NonNull java.util.Random)` |  |
| `BigInteger(@NonNull String)` |  |
| `BigInteger(@NonNull String, int)` |  |
| `BigInteger(int, byte[])` |  |
| `BigInteger(byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int bitCount()` |  |
| `int bitLength()` |  |
| `int compareTo(@NonNull java.math.BigInteger)` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `int getLowestSetBit()` |  |
| `int intValue()` |  |
| `boolean isProbablePrime(int)` |  |
| `long longValue()` |  |
| `int signum()` |  |
| `boolean testBit(int)` |  |
| `byte[] toByteArray()` |  |

---

### `class final MathContext`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MathContext(int)` |  |
| `MathContext(int, java.math.RoundingMode)` |  |
| `MathContext(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.math.MathContext DECIMAL128` |  |
| `static final java.math.MathContext DECIMAL32` |  |
| `static final java.math.MathContext DECIMAL64` |  |
| `static final java.math.MathContext UNLIMITED` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getPrecision()` |  |
| `java.math.RoundingMode getRoundingMode()` |  |

---

### `enum RoundingMode`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.math.RoundingMode CEILING` |  |
| `static final java.math.RoundingMode DOWN` |  |
| `static final java.math.RoundingMode FLOOR` |  |
| `static final java.math.RoundingMode HALF_DOWN` |  |
| `static final java.math.RoundingMode HALF_EVEN` |  |
| `static final java.math.RoundingMode HALF_UP` |  |
| `static final java.math.RoundingMode UNNECESSARY` |  |
| `static final java.math.RoundingMode UP` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.math.RoundingMode valueOf(int)` |  |

---

## Package: `java.net`

### `class abstract Authenticator`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Authenticator()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.PasswordAuthentication getPasswordAuthentication()` |  |
| `final String getRequestingHost()` |  |
| `final int getRequestingPort()` |  |
| `final String getRequestingPrompt()` |  |
| `final String getRequestingProtocol()` |  |
| `final String getRequestingScheme()` |  |
| `final java.net.InetAddress getRequestingSite()` |  |
| `java.net.URL getRequestingURL()` |  |
| `java.net.Authenticator.RequestorType getRequestorType()` |  |
| `static java.net.PasswordAuthentication requestPasswordAuthentication(java.net.InetAddress, int, String, String, String)` |  |
| `static java.net.PasswordAuthentication requestPasswordAuthentication(String, java.net.InetAddress, int, String, String, String)` |  |
| `static java.net.PasswordAuthentication requestPasswordAuthentication(String, java.net.InetAddress, int, String, String, String, java.net.URL, java.net.Authenticator.RequestorType)` |  |
| `static void setDefault(java.net.Authenticator)` |  |

---

### `enum Authenticator.RequestorType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.net.Authenticator.RequestorType PROXY` |  |
| `static final java.net.Authenticator.RequestorType SERVER` |  |

---

### `class BindException`

- **Extends:** `java.net.SocketException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BindException(String)` |  |
| `BindException()` |  |

---

### `class abstract CacheRequest`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CacheRequest()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void abort()` |  |
| `abstract java.io.OutputStream getBody() throws java.io.IOException` |  |

---

### `class abstract CacheResponse`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CacheResponse()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.io.InputStream getBody() throws java.io.IOException` |  |
| `abstract java.util.Map<java.lang.String,java.util.List<java.lang.String>> getHeaders() throws java.io.IOException` |  |

---

### `class ConnectException`

- **Extends:** `java.net.SocketException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConnectException(String)` |  |
| `ConnectException()` |  |

---

### `class abstract ContentHandler`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ContentHandler()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract Object getContent(java.net.URLConnection) throws java.io.IOException` |  |
| `Object getContent(java.net.URLConnection, Class[]) throws java.io.IOException` |  |

---

### `interface ContentHandlerFactory`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.ContentHandler createContentHandler(String)` |  |

---

### `class abstract CookieHandler`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CookieHandler()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.util.Map<java.lang.String,java.util.List<java.lang.String>> get(java.net.URI, java.util.Map<java.lang.String,java.util.List<java.lang.String>>) throws java.io.IOException` |  |
| `static java.net.CookieHandler getDefault()` |  |
| `abstract void put(java.net.URI, java.util.Map<java.lang.String,java.util.List<java.lang.String>>) throws java.io.IOException` |  |
| `static void setDefault(java.net.CookieHandler)` |  |

---

### `class CookieManager`

- **Extends:** `java.net.CookieHandler`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CookieManager()` |  |
| `CookieManager(java.net.CookieStore, java.net.CookiePolicy)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Map<java.lang.String,java.util.List<java.lang.String>> get(java.net.URI, java.util.Map<java.lang.String,java.util.List<java.lang.String>>) throws java.io.IOException` |  |
| `java.net.CookieStore getCookieStore()` |  |
| `void put(java.net.URI, java.util.Map<java.lang.String,java.util.List<java.lang.String>>) throws java.io.IOException` |  |
| `void setCookiePolicy(java.net.CookiePolicy)` |  |

---

### `interface CookiePolicy`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.net.CookiePolicy ACCEPT_ALL` |  |
| `static final java.net.CookiePolicy ACCEPT_NONE` |  |
| `static final java.net.CookiePolicy ACCEPT_ORIGINAL_SERVER` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean shouldAccept(java.net.URI, java.net.HttpCookie)` |  |

---

### `interface CookieStore`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void add(java.net.URI, java.net.HttpCookie)` |  |
| `java.util.List<java.net.HttpCookie> get(java.net.URI)` |  |
| `java.util.List<java.net.HttpCookie> getCookies()` |  |
| `java.util.List<java.net.URI> getURIs()` |  |
| `boolean remove(java.net.URI, java.net.HttpCookie)` |  |
| `boolean removeAll()` |  |

---

### `class final DatagramPacket`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DatagramPacket(byte[], int, int)` |  |
| `DatagramPacket(byte[], int)` |  |
| `DatagramPacket(byte[], int, int, java.net.InetAddress, int)` |  |
| `DatagramPacket(byte[], int, int, java.net.SocketAddress)` |  |
| `DatagramPacket(byte[], int, java.net.InetAddress, int)` |  |
| `DatagramPacket(byte[], int, java.net.SocketAddress)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.InetAddress getAddress()` |  |
| `byte[] getData()` |  |
| `int getLength()` |  |
| `int getOffset()` |  |
| `int getPort()` |  |
| `java.net.SocketAddress getSocketAddress()` |  |
| `void setAddress(java.net.InetAddress)` |  |
| `void setData(byte[], int, int)` |  |
| `void setData(byte[])` |  |
| `void setLength(int)` |  |
| `void setPort(int)` |  |
| `void setSocketAddress(java.net.SocketAddress)` |  |

---

### `class DatagramSocket`

- **Implements:** `java.io.Closeable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DatagramSocket() throws java.net.SocketException` |  |
| `DatagramSocket(java.net.DatagramSocketImpl)` |  |
| `DatagramSocket(java.net.SocketAddress) throws java.net.SocketException` |  |
| `DatagramSocket(int) throws java.net.SocketException` |  |
| `DatagramSocket(int, java.net.InetAddress) throws java.net.SocketException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void bind(java.net.SocketAddress) throws java.net.SocketException` |  |
| `void close()` |  |
| `void connect(java.net.InetAddress, int)` |  |
| `void connect(java.net.SocketAddress) throws java.net.SocketException` |  |
| `void disconnect()` |  |
| `boolean getBroadcast() throws java.net.SocketException` |  |
| `java.nio.channels.DatagramChannel getChannel()` |  |
| `java.net.InetAddress getInetAddress()` |  |
| `java.net.InetAddress getLocalAddress()` |  |
| `int getLocalPort()` |  |
| `java.net.SocketAddress getLocalSocketAddress()` |  |
| `int getPort()` |  |
| `int getReceiveBufferSize() throws java.net.SocketException` |  |
| `java.net.SocketAddress getRemoteSocketAddress()` |  |
| `boolean getReuseAddress() throws java.net.SocketException` |  |
| `int getSendBufferSize() throws java.net.SocketException` |  |
| `int getSoTimeout() throws java.net.SocketException` |  |
| `int getTrafficClass() throws java.net.SocketException` |  |
| `boolean isBound()` |  |
| `boolean isClosed()` |  |
| `boolean isConnected()` |  |
| `void receive(java.net.DatagramPacket) throws java.io.IOException` |  |
| `void send(java.net.DatagramPacket) throws java.io.IOException` |  |
| `void setBroadcast(boolean) throws java.net.SocketException` |  |
| `static void setDatagramSocketImplFactory(java.net.DatagramSocketImplFactory) throws java.io.IOException` |  |
| `void setReceiveBufferSize(int) throws java.net.SocketException` |  |
| `void setReuseAddress(boolean) throws java.net.SocketException` |  |
| `void setSendBufferSize(int) throws java.net.SocketException` |  |
| `void setSoTimeout(int) throws java.net.SocketException` |  |
| `void setTrafficClass(int) throws java.net.SocketException` |  |

---

### `class abstract DatagramSocketImpl`

- **Implements:** `java.net.SocketOptions`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DatagramSocketImpl()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.io.FileDescriptor fd` |  |
| `int localPort` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void bind(int, java.net.InetAddress) throws java.net.SocketException` |  |
| `abstract void close()` |  |
| `void connect(java.net.InetAddress, int) throws java.net.SocketException` |  |
| `abstract void create() throws java.net.SocketException` |  |
| `void disconnect()` |  |
| `java.io.FileDescriptor getFileDescriptor()` |  |
| `int getLocalPort()` |  |
| `abstract int getTimeToLive() throws java.io.IOException` |  |
| `abstract void join(java.net.InetAddress) throws java.io.IOException` |  |
| `abstract void joinGroup(java.net.SocketAddress, java.net.NetworkInterface) throws java.io.IOException` |  |
| `abstract void leave(java.net.InetAddress) throws java.io.IOException` |  |
| `abstract void leaveGroup(java.net.SocketAddress, java.net.NetworkInterface) throws java.io.IOException` |  |
| `abstract int peek(java.net.InetAddress) throws java.io.IOException` |  |
| `abstract int peekData(java.net.DatagramPacket) throws java.io.IOException` |  |
| `abstract void receive(java.net.DatagramPacket) throws java.io.IOException` |  |
| `abstract void send(java.net.DatagramPacket) throws java.io.IOException` |  |
| `abstract void setTimeToLive(int) throws java.io.IOException` |  |

---

### `interface DatagramSocketImplFactory`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.DatagramSocketImpl createDatagramSocketImpl()` |  |

---

### `interface FileNameMap`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getContentTypeFor(String)` |  |

---

### `class final HttpCookie`

- **Implements:** `java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `HttpCookie(String, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `static boolean domainMatches(String, String)` |  |
| `String getComment()` |  |
| `String getCommentURL()` |  |
| `boolean getDiscard()` |  |
| `String getDomain()` |  |
| `long getMaxAge()` |  |
| `String getName()` |  |
| `String getPath()` |  |
| `String getPortlist()` |  |
| `boolean getSecure()` |  |
| `String getValue()` |  |
| `int getVersion()` |  |
| `boolean hasExpired()` |  |
| `boolean isHttpOnly()` |  |
| `static java.util.List<java.net.HttpCookie> parse(String)` |  |
| `void setComment(String)` |  |
| `void setCommentURL(String)` |  |
| `void setDiscard(boolean)` |  |
| `void setDomain(String)` |  |
| `void setHttpOnly(boolean)` |  |
| `void setMaxAge(long)` |  |
| `void setPath(String)` |  |
| `void setPortlist(String)` |  |
| `void setSecure(boolean)` |  |
| `void setValue(String)` |  |
| `void setVersion(int)` |  |

---

### `class HttpRetryException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `HttpRetryException(String, int)` |  |
| `HttpRetryException(String, int, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getLocation()` |  |
| `String getReason()` |  |
| `int responseCode()` |  |

---

### `class abstract HttpURLConnection`

- **Extends:** `java.net.URLConnection`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `HttpURLConnection(java.net.URL)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int HTTP_ACCEPTED = 202` |  |
| `static final int HTTP_BAD_GATEWAY = 502` |  |
| `static final int HTTP_BAD_METHOD = 405` |  |
| `static final int HTTP_BAD_REQUEST = 400` |  |
| `static final int HTTP_CLIENT_TIMEOUT = 408` |  |
| `static final int HTTP_CONFLICT = 409` |  |
| `static final int HTTP_CREATED = 201` |  |
| `static final int HTTP_ENTITY_TOO_LARGE = 413` |  |
| `static final int HTTP_FORBIDDEN = 403` |  |
| `static final int HTTP_GATEWAY_TIMEOUT = 504` |  |
| `static final int HTTP_GONE = 410` |  |
| `static final int HTTP_INTERNAL_ERROR = 500` |  |
| `static final int HTTP_LENGTH_REQUIRED = 411` |  |
| `static final int HTTP_MOVED_PERM = 301` |  |
| `static final int HTTP_MOVED_TEMP = 302` |  |
| `static final int HTTP_MULT_CHOICE = 300` |  |
| `static final int HTTP_NOT_ACCEPTABLE = 406` |  |
| `static final int HTTP_NOT_AUTHORITATIVE = 203` |  |
| `static final int HTTP_NOT_FOUND = 404` |  |
| `static final int HTTP_NOT_IMPLEMENTED = 501` |  |
| `static final int HTTP_NOT_MODIFIED = 304` |  |
| `static final int HTTP_NO_CONTENT = 204` |  |
| `static final int HTTP_OK = 200` |  |
| `static final int HTTP_PARTIAL = 206` |  |
| `static final int HTTP_PAYMENT_REQUIRED = 402` |  |
| `static final int HTTP_PRECON_FAILED = 412` |  |
| `static final int HTTP_PROXY_AUTH = 407` |  |
| `static final int HTTP_REQ_TOO_LONG = 414` |  |
| `static final int HTTP_RESET = 205` |  |
| `static final int HTTP_SEE_OTHER = 303` |  |
| `static final int HTTP_UNAUTHORIZED = 401` |  |
| `static final int HTTP_UNAVAILABLE = 503` |  |
| `static final int HTTP_UNSUPPORTED_TYPE = 415` |  |
| `static final int HTTP_USE_PROXY = 305` |  |
| `static final int HTTP_VERSION = 505` |  |
| `int chunkLength` |  |
| `int fixedContentLength` |  |
| `long fixedContentLengthLong` |  |
| `boolean instanceFollowRedirects` |  |
| `String method` |  |
| `int responseCode` |  |
| `String responseMessage` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void disconnect()` |  |
| `java.io.InputStream getErrorStream()` |  |
| `static boolean getFollowRedirects()` |  |
| `boolean getInstanceFollowRedirects()` |  |
| `String getRequestMethod()` |  |
| `int getResponseCode() throws java.io.IOException` |  |
| `String getResponseMessage() throws java.io.IOException` |  |
| `void setChunkedStreamingMode(int)` |  |
| `void setFixedLengthStreamingMode(int)` |  |
| `void setFixedLengthStreamingMode(long)` |  |
| `static void setFollowRedirects(boolean)` |  |
| `void setInstanceFollowRedirects(boolean)` |  |
| `void setRequestMethod(String) throws java.net.ProtocolException` |  |
| `abstract boolean usingProxy()` |  |

---

### `class final IDN`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ALLOW_UNASSIGNED = 1` |  |
| `static final int USE_STD3_ASCII_RULES = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static String toASCII(String, int)` |  |
| `static String toASCII(String)` |  |
| `static String toUnicode(String, int)` |  |
| `static String toUnicode(String)` |  |

---

### `class final Inet4Address`

- **Extends:** `java.net.InetAddress`

---

### `class final Inet6Address`

- **Extends:** `java.net.InetAddress`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.net.Inet6Address getByAddress(String, byte[], java.net.NetworkInterface) throws java.net.UnknownHostException` |  |
| `static java.net.Inet6Address getByAddress(String, byte[], int) throws java.net.UnknownHostException` |  |
| `int getScopeId()` |  |
| `java.net.NetworkInterface getScopedInterface()` |  |
| `boolean isIPv4CompatibleAddress()` |  |

---

### `class InetAddress`

- **Implements:** `java.io.Serializable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] getAddress()` |  |
| `static java.net.InetAddress[] getAllByName(String) throws java.net.UnknownHostException` |  |
| `static java.net.InetAddress getByAddress(String, byte[]) throws java.net.UnknownHostException` |  |
| `static java.net.InetAddress getByAddress(byte[]) throws java.net.UnknownHostException` |  |
| `static java.net.InetAddress getByName(String) throws java.net.UnknownHostException` |  |
| `String getCanonicalHostName()` |  |
| `String getHostAddress()` |  |
| `String getHostName()` |  |
| `static java.net.InetAddress getLocalHost() throws java.net.UnknownHostException` |  |
| `static java.net.InetAddress getLoopbackAddress()` |  |
| `boolean isAnyLocalAddress()` |  |
| `boolean isLinkLocalAddress()` |  |
| `boolean isLoopbackAddress()` |  |
| `boolean isMCGlobal()` |  |
| `boolean isMCLinkLocal()` |  |
| `boolean isMCNodeLocal()` |  |
| `boolean isMCOrgLocal()` |  |
| `boolean isMCSiteLocal()` |  |
| `boolean isMulticastAddress()` |  |
| `boolean isReachable(int) throws java.io.IOException` |  |
| `boolean isReachable(java.net.NetworkInterface, int, int) throws java.io.IOException` |  |
| `boolean isSiteLocalAddress()` |  |

---

### `class InetSocketAddress`

- **Extends:** `java.net.SocketAddress`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InetSocketAddress(int)` |  |
| `InetSocketAddress(java.net.InetAddress, int)` |  |
| `InetSocketAddress(String, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.net.InetSocketAddress createUnresolved(String, int)` |  |
| `final boolean equals(Object)` |  |
| `final java.net.InetAddress getAddress()` |  |
| `final String getHostName()` |  |
| `final String getHostString()` |  |
| `final int getPort()` |  |
| `final int hashCode()` |  |
| `final boolean isUnresolved()` |  |

---

### `class InterfaceAddress`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.InetAddress getAddress()` |  |
| `java.net.InetAddress getBroadcast()` |  |
| `short getNetworkPrefixLength()` |  |

---

### `class abstract JarURLConnection`

- **Extends:** `java.net.URLConnection`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `JarURLConnection(java.net.URL) throws java.net.MalformedURLException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.URLConnection jarFileURLConnection` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.jar.Attributes getAttributes() throws java.io.IOException` |  |
| `java.security.cert.Certificate[] getCertificates() throws java.io.IOException` |  |
| `String getEntryName()` |  |
| `java.util.jar.JarEntry getJarEntry() throws java.io.IOException` |  |
| `abstract java.util.jar.JarFile getJarFile() throws java.io.IOException` |  |
| `java.net.URL getJarFileURL()` |  |
| `java.util.jar.Attributes getMainAttributes() throws java.io.IOException` |  |
| `java.util.jar.Manifest getManifest() throws java.io.IOException` |  |

---

### `class MalformedURLException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MalformedURLException()` |  |
| `MalformedURLException(String)` |  |

---

### `class MulticastSocket`

- **Extends:** `java.net.DatagramSocket`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MulticastSocket() throws java.io.IOException` |  |
| `MulticastSocket(int) throws java.io.IOException` |  |
| `MulticastSocket(java.net.SocketAddress) throws java.io.IOException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.InetAddress getInterface() throws java.net.SocketException` |  |
| `boolean getLoopbackMode() throws java.net.SocketException` |  |
| `java.net.NetworkInterface getNetworkInterface() throws java.net.SocketException` |  |
| `int getTimeToLive() throws java.io.IOException` |  |
| `void joinGroup(java.net.InetAddress) throws java.io.IOException` |  |
| `void joinGroup(java.net.SocketAddress, java.net.NetworkInterface) throws java.io.IOException` |  |
| `void leaveGroup(java.net.InetAddress) throws java.io.IOException` |  |
| `void leaveGroup(java.net.SocketAddress, java.net.NetworkInterface) throws java.io.IOException` |  |
| `void setInterface(java.net.InetAddress) throws java.net.SocketException` |  |
| `void setLoopbackMode(boolean) throws java.net.SocketException` |  |
| `void setNetworkInterface(java.net.NetworkInterface) throws java.net.SocketException` |  |
| `void setTimeToLive(int) throws java.io.IOException` |  |

---

### `class final NetPermission`

- **Extends:** `java.security.BasicPermission`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NetPermission(String)` |  |
| `NetPermission(String, String)` |  |

---

### `class final NetworkInterface`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.net.NetworkInterface getByIndex(int) throws java.net.SocketException` |  |
| `static java.net.NetworkInterface getByInetAddress(java.net.InetAddress) throws java.net.SocketException` |  |
| `static java.net.NetworkInterface getByName(String) throws java.net.SocketException` |  |
| `String getDisplayName()` |  |
| `byte[] getHardwareAddress() throws java.net.SocketException` |  |
| `int getIndex()` |  |
| `java.util.Enumeration<java.net.InetAddress> getInetAddresses()` |  |
| `java.util.List<java.net.InterfaceAddress> getInterfaceAddresses()` |  |
| `int getMTU() throws java.net.SocketException` |  |
| `String getName()` |  |
| `static java.util.Enumeration<java.net.NetworkInterface> getNetworkInterfaces() throws java.net.SocketException` |  |
| `java.net.NetworkInterface getParent()` |  |
| `java.util.Enumeration<java.net.NetworkInterface> getSubInterfaces()` |  |
| `boolean isLoopback() throws java.net.SocketException` |  |
| `boolean isPointToPoint() throws java.net.SocketException` |  |
| `boolean isUp() throws java.net.SocketException` |  |
| `boolean isVirtual()` |  |
| `boolean supportsMulticast() throws java.net.SocketException` |  |

---

### `class NoRouteToHostException`

- **Extends:** `java.net.SocketException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NoRouteToHostException(String)` |  |
| `NoRouteToHostException()` |  |

---

### `class final PasswordAuthentication`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PasswordAuthentication(String, char[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `char[] getPassword()` |  |
| `String getUserName()` |  |

---

### `class PortUnreachableException`

- **Extends:** `java.net.SocketException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PortUnreachableException(String)` |  |
| `PortUnreachableException()` |  |

---

### `class ProtocolException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ProtocolException(String)` |  |
| `ProtocolException()` |  |

---

### `interface ProtocolFamily`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String name()` |  |

---

### `class Proxy`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Proxy(java.net.Proxy.Type, java.net.SocketAddress)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.net.Proxy NO_PROXY` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.SocketAddress address()` |  |
| `final boolean equals(Object)` |  |
| `final int hashCode()` |  |
| `java.net.Proxy.Type type()` |  |

---

### `enum Proxy.Type`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.net.Proxy.Type DIRECT` |  |
| `static final java.net.Proxy.Type HTTP` |  |
| `static final java.net.Proxy.Type SOCKS` |  |

---

### `class abstract ProxySelector`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ProxySelector()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void connectFailed(java.net.URI, java.net.SocketAddress, java.io.IOException)` |  |
| `static java.net.ProxySelector getDefault()` |  |
| `abstract java.util.List<java.net.Proxy> select(java.net.URI)` |  |
| `static void setDefault(java.net.ProxySelector)` |  |

---

### `class abstract ResponseCache`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ResponseCache()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.net.CacheResponse get(java.net.URI, String, java.util.Map<java.lang.String,java.util.List<java.lang.String>>) throws java.io.IOException` |  |
| `static java.net.ResponseCache getDefault()` |  |
| `abstract java.net.CacheRequest put(java.net.URI, java.net.URLConnection) throws java.io.IOException` |  |
| `static void setDefault(java.net.ResponseCache)` |  |

---

### `class abstract SecureCacheResponse`

- **Extends:** `java.net.CacheResponse`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SecureCacheResponse()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract String getCipherSuite()` |  |
| `abstract java.util.List<java.security.cert.Certificate> getLocalCertificateChain()` |  |
| `abstract java.security.Principal getLocalPrincipal()` |  |
| `abstract java.security.Principal getPeerPrincipal() throws javax.net.ssl.SSLPeerUnverifiedException` |  |
| `abstract java.util.List<java.security.cert.Certificate> getServerCertificateChain() throws javax.net.ssl.SSLPeerUnverifiedException` |  |

---

### `class ServerSocket`

- **Implements:** `java.io.Closeable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ServerSocket() throws java.io.IOException` |  |
| `ServerSocket(int) throws java.io.IOException` |  |
| `ServerSocket(int, int) throws java.io.IOException` |  |
| `ServerSocket(int, int, java.net.InetAddress) throws java.io.IOException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.Socket accept() throws java.io.IOException` |  |
| `void bind(java.net.SocketAddress) throws java.io.IOException` |  |
| `void bind(java.net.SocketAddress, int) throws java.io.IOException` |  |
| `void close() throws java.io.IOException` |  |
| `java.nio.channels.ServerSocketChannel getChannel()` |  |
| `java.net.InetAddress getInetAddress()` |  |
| `int getLocalPort()` |  |
| `java.net.SocketAddress getLocalSocketAddress()` |  |
| `int getReceiveBufferSize() throws java.net.SocketException` |  |
| `boolean getReuseAddress() throws java.net.SocketException` |  |
| `int getSoTimeout() throws java.io.IOException` |  |
| `final void implAccept(java.net.Socket) throws java.io.IOException` |  |
| `boolean isBound()` |  |
| `boolean isClosed()` |  |
| `void setPerformancePreferences(int, int, int)` |  |
| `void setReceiveBufferSize(int) throws java.net.SocketException` |  |
| `void setReuseAddress(boolean) throws java.net.SocketException` |  |
| `void setSoTimeout(int) throws java.net.SocketException` |  |
| `static void setSocketFactory(java.net.SocketImplFactory) throws java.io.IOException` |  |

---

### `class Socket`

- **Implements:** `java.io.Closeable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Socket()` |  |
| `Socket(java.net.Proxy)` |  |
| `Socket(java.net.SocketImpl) throws java.net.SocketException` |  |
| `Socket(String, int) throws java.io.IOException, java.net.UnknownHostException` |  |
| `Socket(java.net.InetAddress, int) throws java.io.IOException` |  |
| `Socket(String, int, java.net.InetAddress, int) throws java.io.IOException` |  |
| `Socket(java.net.InetAddress, int, java.net.InetAddress, int) throws java.io.IOException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void bind(java.net.SocketAddress) throws java.io.IOException` |  |
| `void close() throws java.io.IOException` |  |
| `void connect(java.net.SocketAddress) throws java.io.IOException` |  |
| `void connect(java.net.SocketAddress, int) throws java.io.IOException` |  |
| `java.nio.channels.SocketChannel getChannel()` |  |
| `java.net.InetAddress getInetAddress()` |  |
| `java.io.InputStream getInputStream() throws java.io.IOException` |  |
| `boolean getKeepAlive() throws java.net.SocketException` |  |
| `java.net.InetAddress getLocalAddress()` |  |
| `int getLocalPort()` |  |
| `java.net.SocketAddress getLocalSocketAddress()` |  |
| `boolean getOOBInline() throws java.net.SocketException` |  |
| `java.io.OutputStream getOutputStream() throws java.io.IOException` |  |
| `int getPort()` |  |
| `int getReceiveBufferSize() throws java.net.SocketException` |  |
| `java.net.SocketAddress getRemoteSocketAddress()` |  |
| `boolean getReuseAddress() throws java.net.SocketException` |  |
| `int getSendBufferSize() throws java.net.SocketException` |  |
| `int getSoLinger() throws java.net.SocketException` |  |
| `int getSoTimeout() throws java.net.SocketException` |  |
| `boolean getTcpNoDelay() throws java.net.SocketException` |  |
| `int getTrafficClass() throws java.net.SocketException` |  |
| `boolean isBound()` |  |
| `boolean isClosed()` |  |
| `boolean isConnected()` |  |
| `boolean isInputShutdown()` |  |
| `boolean isOutputShutdown()` |  |
| `void sendUrgentData(int) throws java.io.IOException` |  |
| `void setKeepAlive(boolean) throws java.net.SocketException` |  |
| `void setOOBInline(boolean) throws java.net.SocketException` |  |
| `void setPerformancePreferences(int, int, int)` |  |
| `void setReceiveBufferSize(int) throws java.net.SocketException` |  |
| `void setReuseAddress(boolean) throws java.net.SocketException` |  |
| `void setSendBufferSize(int) throws java.net.SocketException` |  |
| `void setSoLinger(boolean, int) throws java.net.SocketException` |  |
| `void setSoTimeout(int) throws java.net.SocketException` |  |
| `static void setSocketImplFactory(java.net.SocketImplFactory) throws java.io.IOException` |  |
| `void setTcpNoDelay(boolean) throws java.net.SocketException` |  |
| `void setTrafficClass(int) throws java.net.SocketException` |  |
| `void shutdownInput() throws java.io.IOException` |  |
| `void shutdownOutput() throws java.io.IOException` |  |

---

### `class abstract SocketAddress`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SocketAddress()` |  |

---

### `class SocketException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SocketException(String)` |  |
| `SocketException()` |  |

---

### `class abstract SocketImpl`

- **Implements:** `java.net.SocketOptions`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SocketImpl()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.InetAddress address` |  |
| `java.io.FileDescriptor fd` |  |
| `int localport` |  |
| `int port` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void accept(java.net.SocketImpl) throws java.io.IOException` |  |
| `abstract int available() throws java.io.IOException` |  |
| `abstract void bind(java.net.InetAddress, int) throws java.io.IOException` |  |
| `abstract void close() throws java.io.IOException` |  |
| `abstract void connect(String, int) throws java.io.IOException` |  |
| `abstract void connect(java.net.InetAddress, int) throws java.io.IOException` |  |
| `abstract void connect(java.net.SocketAddress, int) throws java.io.IOException` |  |
| `abstract void create(boolean) throws java.io.IOException` |  |
| `java.io.FileDescriptor getFileDescriptor()` |  |
| `java.net.InetAddress getInetAddress()` |  |
| `abstract java.io.InputStream getInputStream() throws java.io.IOException` |  |
| `int getLocalPort()` |  |
| `abstract java.io.OutputStream getOutputStream() throws java.io.IOException` |  |
| `int getPort()` |  |
| `abstract void listen(int) throws java.io.IOException` |  |
| `abstract void sendUrgentData(int) throws java.io.IOException` |  |
| `void setPerformancePreferences(int, int, int)` |  |
| `void shutdownInput() throws java.io.IOException` |  |
| `void shutdownOutput() throws java.io.IOException` |  |
| `boolean supportsUrgentData()` |  |

---

### `interface SocketImplFactory`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.SocketImpl createSocketImpl()` |  |

---

### `interface SocketOption<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String name()` |  |
| `Class<T> type()` |  |

---

### `interface SocketOptions`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int IP_MULTICAST_IF = 16` |  |
| `static final int IP_MULTICAST_IF2 = 31` |  |
| `static final int IP_MULTICAST_LOOP = 18` |  |
| `static final int IP_TOS = 3` |  |
| `static final int SO_BINDADDR = 15` |  |
| `static final int SO_BROADCAST = 32` |  |
| `static final int SO_KEEPALIVE = 8` |  |
| `static final int SO_LINGER = 128` |  |
| `static final int SO_OOBINLINE = 4099` |  |
| `static final int SO_RCVBUF = 4098` |  |
| `static final int SO_REUSEADDR = 4` |  |
| `static final int SO_SNDBUF = 4097` |  |
| `static final int SO_TIMEOUT = 4102` |  |
| `static final int TCP_NODELAY = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object getOption(int) throws java.net.SocketException` |  |
| `void setOption(int, Object) throws java.net.SocketException` |  |

---

### `class final SocketPermission`

- **Extends:** `java.security.Permission`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SocketPermission(String, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getActions()` |  |
| `boolean implies(java.security.Permission)` |  |

---

### `class SocketTimeoutException`

- **Extends:** `java.io.InterruptedIOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SocketTimeoutException(String)` |  |
| `SocketTimeoutException()` |  |

---

### `enum StandardProtocolFamily`

- **Implements:** `java.net.ProtocolFamily`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.net.StandardProtocolFamily INET` |  |
| `static final java.net.StandardProtocolFamily INET6` |  |

---

### `class final StandardSocketOptions`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.net.SocketOption<java.net.NetworkInterface> IP_MULTICAST_IF` |  |
| `static final java.net.SocketOption<java.lang.Boolean> IP_MULTICAST_LOOP` |  |
| `static final java.net.SocketOption<java.lang.Integer> IP_MULTICAST_TTL` |  |
| `static final java.net.SocketOption<java.lang.Integer> IP_TOS` |  |
| `static final java.net.SocketOption<java.lang.Boolean> SO_BROADCAST` |  |
| `static final java.net.SocketOption<java.lang.Boolean> SO_KEEPALIVE` |  |
| `static final java.net.SocketOption<java.lang.Integer> SO_LINGER` |  |
| `static final java.net.SocketOption<java.lang.Integer> SO_RCVBUF` |  |
| `static final java.net.SocketOption<java.lang.Boolean> SO_REUSEADDR` |  |
| `static final java.net.SocketOption<java.lang.Integer> SO_SNDBUF` |  |
| `static final java.net.SocketOption<java.lang.Boolean> TCP_NODELAY` |  |

---

### `class final URI`

- **Implements:** `java.lang.Comparable<java.net.URI> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `URI(String) throws java.net.URISyntaxException` |  |
| `URI(String, String, String, int, String, String, String) throws java.net.URISyntaxException` |  |
| `URI(String, String, String, String, String) throws java.net.URISyntaxException` |  |
| `URI(String, String, String, String) throws java.net.URISyntaxException` |  |
| `URI(String, String, String) throws java.net.URISyntaxException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compareTo(java.net.URI)` |  |
| `static java.net.URI create(String)` |  |
| `String getAuthority()` |  |
| `String getFragment()` |  |
| `String getHost()` |  |
| `String getPath()` |  |
| `int getPort()` |  |
| `String getQuery()` |  |
| `String getRawAuthority()` |  |
| `String getRawFragment()` |  |
| `String getRawPath()` |  |
| `String getRawQuery()` |  |
| `String getRawSchemeSpecificPart()` |  |
| `String getRawUserInfo()` |  |
| `String getScheme()` |  |
| `String getSchemeSpecificPart()` |  |
| `String getUserInfo()` |  |
| `boolean isAbsolute()` |  |
| `boolean isOpaque()` |  |
| `java.net.URI normalize()` |  |
| `java.net.URI parseServerAuthority() throws java.net.URISyntaxException` |  |
| `java.net.URI relativize(java.net.URI)` |  |
| `java.net.URI resolve(java.net.URI)` |  |
| `java.net.URI resolve(String)` |  |
| `String toASCIIString()` |  |
| `java.net.URL toURL() throws java.net.MalformedURLException` |  |

---

### `class URISyntaxException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `URISyntaxException(String, String, int)` |  |
| `URISyntaxException(String, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getIndex()` |  |
| `String getInput()` |  |
| `String getReason()` |  |

---

### `class final URL`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `URL(String, String, int, String) throws java.net.MalformedURLException` |  |
| `URL(String, String, String) throws java.net.MalformedURLException` |  |
| `URL(String, String, int, String, java.net.URLStreamHandler) throws java.net.MalformedURLException` |  |
| `URL(String) throws java.net.MalformedURLException` |  |
| `URL(java.net.URL, String) throws java.net.MalformedURLException` |  |
| `URL(java.net.URL, String, java.net.URLStreamHandler) throws java.net.MalformedURLException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getAuthority()` |  |
| `Object getContent() throws java.io.IOException` |  |
| `Object getContent(Class[]) throws java.io.IOException` |  |
| `int getDefaultPort()` |  |
| `String getFile()` |  |
| `String getHost()` |  |
| `String getPath()` |  |
| `int getPort()` |  |
| `String getProtocol()` |  |
| `String getQuery()` |  |
| `String getRef()` |  |
| `String getUserInfo()` |  |
| `java.net.URLConnection openConnection() throws java.io.IOException` |  |
| `java.net.URLConnection openConnection(java.net.Proxy) throws java.io.IOException` |  |
| `java.io.InputStream openStream() throws java.io.IOException` |  |
| `boolean sameFile(java.net.URL)` |  |
| `static void setURLStreamHandlerFactory(java.net.URLStreamHandlerFactory)` |  |
| `String toExternalForm()` |  |
| `java.net.URI toURI() throws java.net.URISyntaxException` |  |

---

### `class URLClassLoader`

- **Extends:** `java.security.SecureClassLoader`
- **Implements:** `java.io.Closeable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `URLClassLoader(java.net.URL[], ClassLoader)` |  |
| `URLClassLoader(java.net.URL[])` |  |
| `URLClassLoader(java.net.URL[], ClassLoader, java.net.URLStreamHandlerFactory)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addURL(java.net.URL)` |  |
| `void close() throws java.io.IOException` |  |
| `Package definePackage(String, java.util.jar.Manifest, java.net.URL) throws java.lang.IllegalArgumentException` |  |
| `java.net.URL findResource(String)` |  |
| `java.util.Enumeration<java.net.URL> findResources(String) throws java.io.IOException` |  |
| `java.net.URL[] getURLs()` |  |
| `static java.net.URLClassLoader newInstance(java.net.URL[], ClassLoader)` |  |
| `static java.net.URLClassLoader newInstance(java.net.URL[])` |  |

---

### `class abstract URLConnection`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `URLConnection(java.net.URL)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `boolean allowUserInteraction` |  |
| `boolean connected` |  |
| `boolean doInput` |  |
| `boolean doOutput` |  |
| `long ifModifiedSince` |  |
| `java.net.URL url` |  |
| `boolean useCaches` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addRequestProperty(String, String)` |  |
| `abstract void connect() throws java.io.IOException` |  |
| `boolean getAllowUserInteraction()` |  |
| `int getConnectTimeout()` |  |
| `Object getContent() throws java.io.IOException` |  |
| `Object getContent(Class[]) throws java.io.IOException` |  |
| `String getContentEncoding()` |  |
| `int getContentLength()` |  |
| `long getContentLengthLong()` |  |
| `String getContentType()` |  |
| `long getDate()` |  |
| `static boolean getDefaultAllowUserInteraction()` |  |
| `boolean getDefaultUseCaches()` |  |
| `boolean getDoInput()` |  |
| `boolean getDoOutput()` |  |
| `long getExpiration()` |  |
| `static java.net.FileNameMap getFileNameMap()` |  |
| `String getHeaderField(String)` |  |
| `String getHeaderField(int)` |  |
| `long getHeaderFieldDate(String, long)` |  |
| `int getHeaderFieldInt(String, int)` |  |
| `String getHeaderFieldKey(int)` |  |
| `long getHeaderFieldLong(String, long)` |  |
| `java.util.Map<java.lang.String,java.util.List<java.lang.String>> getHeaderFields()` |  |
| `long getIfModifiedSince()` |  |
| `java.io.InputStream getInputStream() throws java.io.IOException` |  |
| `long getLastModified()` |  |
| `java.io.OutputStream getOutputStream() throws java.io.IOException` |  |
| `java.security.Permission getPermission() throws java.io.IOException` |  |
| `int getReadTimeout()` |  |
| `java.util.Map<java.lang.String,java.util.List<java.lang.String>> getRequestProperties()` |  |
| `String getRequestProperty(String)` |  |
| `java.net.URL getURL()` |  |
| `boolean getUseCaches()` |  |
| `static String guessContentTypeFromName(String)` |  |
| `static String guessContentTypeFromStream(java.io.InputStream) throws java.io.IOException` |  |
| `void setAllowUserInteraction(boolean)` |  |
| `void setConnectTimeout(int)` |  |
| `static void setContentHandlerFactory(java.net.ContentHandlerFactory)` |  |
| `static void setDefaultAllowUserInteraction(boolean)` |  |
| `void setDefaultUseCaches(boolean)` |  |
| `void setDoInput(boolean)` |  |
| `void setDoOutput(boolean)` |  |
| `static void setFileNameMap(java.net.FileNameMap)` |  |
| `void setIfModifiedSince(long)` |  |
| `void setReadTimeout(int)` |  |
| `void setRequestProperty(String, String)` |  |
| `void setUseCaches(boolean)` |  |

---

### `class URLDecoder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `URLDecoder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static String decode(String, String) throws java.io.UnsupportedEncodingException` |  |

---

### `class URLEncoder`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static String encode(String, String) throws java.io.UnsupportedEncodingException` |  |

---

### `class abstract URLStreamHandler`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `URLStreamHandler()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean equals(java.net.URL, java.net.URL)` |  |
| `int getDefaultPort()` |  |
| `java.net.InetAddress getHostAddress(java.net.URL)` |  |
| `int hashCode(java.net.URL)` |  |
| `boolean hostsEqual(java.net.URL, java.net.URL)` |  |
| `abstract java.net.URLConnection openConnection(java.net.URL) throws java.io.IOException` |  |
| `java.net.URLConnection openConnection(java.net.URL, java.net.Proxy) throws java.io.IOException` |  |
| `void parseURL(java.net.URL, String, int, int)` |  |
| `boolean sameFile(java.net.URL, java.net.URL)` |  |
| `void setURL(java.net.URL, String, String, int, String, String, String, String, String)` |  |
| `String toExternalForm(java.net.URL)` |  |

---

### `interface URLStreamHandlerFactory`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.URLStreamHandler createURLStreamHandler(String)` |  |

---

### `class UnknownHostException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnknownHostException(String)` |  |
| `UnknownHostException()` |  |

---

### `class UnknownServiceException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnknownServiceException()` |  |
| `UnknownServiceException(String)` |  |

---

## Package: `java.nio`

### `class abstract Buffer`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract Object array()` |  |
| `abstract int arrayOffset()` |  |
| `final int capacity()` |  |
| `java.nio.Buffer clear()` |  |
| `java.nio.Buffer flip()` |  |
| `abstract boolean hasArray()` |  |
| `final boolean hasRemaining()` |  |
| `abstract boolean isDirect()` |  |
| `abstract boolean isReadOnly()` |  |
| `final int limit()` |  |
| `java.nio.Buffer limit(int)` |  |
| `java.nio.Buffer mark()` |  |
| `final int position()` |  |
| `java.nio.Buffer position(int)` |  |
| `final int remaining()` |  |
| `java.nio.Buffer reset()` |  |
| `java.nio.Buffer rewind()` |  |

---

### `class BufferOverflowException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BufferOverflowException()` |  |

---

### `class BufferUnderflowException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BufferUnderflowException()` |  |

---

### `class abstract ByteBuffer`

- **Extends:** `java.nio.Buffer`
- **Implements:** `java.lang.Comparable<java.nio.ByteBuffer>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int arrayOffset()` |  |
| `int compareTo(@NonNull java.nio.ByteBuffer)` |  |
| `abstract byte get()` |  |
| `abstract byte get(int)` |  |
| `abstract char getChar()` |  |
| `abstract char getChar(int)` |  |
| `abstract double getDouble()` |  |
| `abstract double getDouble(int)` |  |
| `abstract float getFloat()` |  |
| `abstract float getFloat(int)` |  |
| `abstract int getInt()` |  |
| `abstract int getInt(int)` |  |
| `abstract long getLong()` |  |
| `abstract long getLong(int)` |  |
| `abstract short getShort()` |  |
| `abstract short getShort(int)` |  |
| `final boolean hasArray()` |  |

---

### `class final ByteOrder`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.nio.ByteOrder BIG_ENDIAN` |  |
| `static final java.nio.ByteOrder LITTLE_ENDIAN` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.ByteOrder nativeOrder()` |  |

---

### `class abstract CharBuffer`

- **Extends:** `java.nio.Buffer`
- **Implements:** `java.lang.Appendable java.lang.CharSequence java.lang.Comparable<java.nio.CharBuffer> java.lang.Readable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.CharBuffer allocate(int)` |  |
| `java.nio.CharBuffer append(CharSequence)` |  |
| `java.nio.CharBuffer append(CharSequence, int, int)` |  |
| `java.nio.CharBuffer append(char)` |  |
| `final char[] array()` |  |
| `final int arrayOffset()` |  |
| `abstract java.nio.CharBuffer asReadOnlyBuffer()` |  |
| `final char charAt(int)` |  |
| `abstract java.nio.CharBuffer compact()` |  |
| `int compareTo(java.nio.CharBuffer)` |  |
| `abstract java.nio.CharBuffer duplicate()` |  |
| `abstract char get()` |  |
| `abstract char get(int)` |  |
| `java.nio.CharBuffer get(char[], int, int)` |  |
| `java.nio.CharBuffer get(char[])` |  |
| `final boolean hasArray()` |  |
| `final int length()` |  |
| `abstract java.nio.ByteOrder order()` |  |
| `abstract java.nio.CharBuffer put(char)` |  |
| `abstract java.nio.CharBuffer put(int, char)` |  |
| `java.nio.CharBuffer put(java.nio.CharBuffer)` |  |
| `java.nio.CharBuffer put(char[], int, int)` |  |
| `final java.nio.CharBuffer put(char[])` |  |
| `java.nio.CharBuffer put(String, int, int)` |  |
| `final java.nio.CharBuffer put(String)` |  |
| `int read(java.nio.CharBuffer) throws java.io.IOException` |  |
| `abstract java.nio.CharBuffer slice()` |  |
| `abstract java.nio.CharBuffer subSequence(int, int)` |  |
| `static java.nio.CharBuffer wrap(char[], int, int)` |  |
| `static java.nio.CharBuffer wrap(char[])` |  |
| `static java.nio.CharBuffer wrap(CharSequence, int, int)` |  |
| `static java.nio.CharBuffer wrap(CharSequence)` |  |

---

### `class abstract DoubleBuffer`

- **Extends:** `java.nio.Buffer`
- **Implements:** `java.lang.Comparable<java.nio.DoubleBuffer>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.DoubleBuffer allocate(int)` |  |
| `final double[] array()` |  |
| `final int arrayOffset()` |  |
| `abstract java.nio.DoubleBuffer asReadOnlyBuffer()` |  |
| `abstract java.nio.DoubleBuffer compact()` |  |
| `int compareTo(java.nio.DoubleBuffer)` |  |
| `abstract java.nio.DoubleBuffer duplicate()` |  |
| `abstract double get()` |  |
| `abstract double get(int)` |  |
| `java.nio.DoubleBuffer get(double[], int, int)` |  |
| `java.nio.DoubleBuffer get(double[])` |  |
| `final boolean hasArray()` |  |
| `abstract java.nio.ByteOrder order()` |  |
| `abstract java.nio.DoubleBuffer put(double)` |  |
| `abstract java.nio.DoubleBuffer put(int, double)` |  |
| `java.nio.DoubleBuffer put(java.nio.DoubleBuffer)` |  |
| `java.nio.DoubleBuffer put(double[], int, int)` |  |
| `final java.nio.DoubleBuffer put(double[])` |  |
| `abstract java.nio.DoubleBuffer slice()` |  |
| `static java.nio.DoubleBuffer wrap(double[], int, int)` |  |
| `static java.nio.DoubleBuffer wrap(double[])` |  |

---

### `class abstract FloatBuffer`

- **Extends:** `java.nio.Buffer`
- **Implements:** `java.lang.Comparable<java.nio.FloatBuffer>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.FloatBuffer allocate(int)` |  |
| `final float[] array()` |  |
| `final int arrayOffset()` |  |
| `abstract java.nio.FloatBuffer asReadOnlyBuffer()` |  |
| `abstract java.nio.FloatBuffer compact()` |  |
| `int compareTo(java.nio.FloatBuffer)` |  |
| `abstract java.nio.FloatBuffer duplicate()` |  |
| `abstract float get()` |  |
| `abstract float get(int)` |  |
| `java.nio.FloatBuffer get(float[], int, int)` |  |
| `java.nio.FloatBuffer get(float[])` |  |
| `final boolean hasArray()` |  |
| `abstract java.nio.ByteOrder order()` |  |
| `abstract java.nio.FloatBuffer put(float)` |  |
| `abstract java.nio.FloatBuffer put(int, float)` |  |
| `java.nio.FloatBuffer put(java.nio.FloatBuffer)` |  |
| `java.nio.FloatBuffer put(float[], int, int)` |  |
| `final java.nio.FloatBuffer put(float[])` |  |
| `abstract java.nio.FloatBuffer slice()` |  |
| `static java.nio.FloatBuffer wrap(float[], int, int)` |  |
| `static java.nio.FloatBuffer wrap(float[])` |  |

---

### `class abstract IntBuffer`

- **Extends:** `java.nio.Buffer`
- **Implements:** `java.lang.Comparable<java.nio.IntBuffer>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.IntBuffer allocate(int)` |  |
| `final int[] array()` |  |
| `final int arrayOffset()` |  |
| `abstract java.nio.IntBuffer asReadOnlyBuffer()` |  |
| `abstract java.nio.IntBuffer compact()` |  |
| `int compareTo(java.nio.IntBuffer)` |  |
| `abstract java.nio.IntBuffer duplicate()` |  |
| `abstract int get()` |  |
| `abstract int get(int)` |  |
| `java.nio.IntBuffer get(int[], int, int)` |  |
| `java.nio.IntBuffer get(int[])` |  |
| `final boolean hasArray()` |  |
| `abstract java.nio.ByteOrder order()` |  |
| `abstract java.nio.IntBuffer put(int)` |  |
| `abstract java.nio.IntBuffer put(int, int)` |  |
| `java.nio.IntBuffer put(java.nio.IntBuffer)` |  |
| `java.nio.IntBuffer put(int[], int, int)` |  |
| `final java.nio.IntBuffer put(int[])` |  |
| `abstract java.nio.IntBuffer slice()` |  |
| `static java.nio.IntBuffer wrap(int[], int, int)` |  |
| `static java.nio.IntBuffer wrap(int[])` |  |

---

### `class InvalidMarkException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidMarkException()` |  |

---

### `class abstract LongBuffer`

- **Extends:** `java.nio.Buffer`
- **Implements:** `java.lang.Comparable<java.nio.LongBuffer>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.LongBuffer allocate(int)` |  |
| `final long[] array()` |  |
| `final int arrayOffset()` |  |
| `abstract java.nio.LongBuffer asReadOnlyBuffer()` |  |
| `abstract java.nio.LongBuffer compact()` |  |
| `int compareTo(java.nio.LongBuffer)` |  |
| `abstract java.nio.LongBuffer duplicate()` |  |
| `abstract long get()` |  |
| `abstract long get(int)` |  |
| `java.nio.LongBuffer get(long[], int, int)` |  |
| `java.nio.LongBuffer get(long[])` |  |
| `final boolean hasArray()` |  |
| `abstract java.nio.ByteOrder order()` |  |
| `abstract java.nio.LongBuffer put(long)` |  |
| `abstract java.nio.LongBuffer put(int, long)` |  |
| `java.nio.LongBuffer put(java.nio.LongBuffer)` |  |
| `java.nio.LongBuffer put(long[], int, int)` |  |
| `final java.nio.LongBuffer put(long[])` |  |
| `abstract java.nio.LongBuffer slice()` |  |
| `static java.nio.LongBuffer wrap(long[], int, int)` |  |
| `static java.nio.LongBuffer wrap(long[])` |  |

---

### `class abstract MappedByteBuffer`

- **Extends:** `java.nio.ByteBuffer`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.nio.MappedByteBuffer force()` |  |
| `final boolean isLoaded()` |  |
| `final java.nio.MappedByteBuffer load()` |  |

---

### `class ReadOnlyBufferException`

- **Extends:** `java.lang.UnsupportedOperationException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ReadOnlyBufferException()` |  |

---

### `class abstract ShortBuffer`

- **Extends:** `java.nio.Buffer`
- **Implements:** `java.lang.Comparable<java.nio.ShortBuffer>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.ShortBuffer allocate(int)` |  |
| `final short[] array()` |  |
| `final int arrayOffset()` |  |
| `abstract java.nio.ShortBuffer asReadOnlyBuffer()` |  |
| `abstract java.nio.ShortBuffer compact()` |  |
| `int compareTo(java.nio.ShortBuffer)` |  |
| `abstract java.nio.ShortBuffer duplicate()` |  |
| `abstract short get()` |  |
| `abstract short get(int)` |  |
| `java.nio.ShortBuffer get(short[], int, int)` |  |
| `java.nio.ShortBuffer get(short[])` |  |
| `final boolean hasArray()` |  |
| `abstract java.nio.ByteOrder order()` |  |
| `abstract java.nio.ShortBuffer put(short)` |  |
| `abstract java.nio.ShortBuffer put(int, short)` |  |
| `java.nio.ShortBuffer put(java.nio.ShortBuffer)` |  |
| `java.nio.ShortBuffer put(short[], int, int)` |  |
| `final java.nio.ShortBuffer put(short[])` |  |
| `abstract java.nio.ShortBuffer slice()` |  |
| `static java.nio.ShortBuffer wrap(short[], int, int)` |  |
| `static java.nio.ShortBuffer wrap(short[])` |  |

---

## Package: `java.nio.channels`

### `class AcceptPendingException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AcceptPendingException()` |  |

---

### `class AlreadyBoundException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AlreadyBoundException()` |  |

---

### `class AlreadyConnectedException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AlreadyConnectedException()` |  |

---

### `interface AsynchronousByteChannel`

- **Extends:** `java.nio.channels.AsynchronousChannel`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `<A> void read(java.nio.ByteBuffer, A, java.nio.channels.CompletionHandler<java.lang.Integer,? super A>)` |  |
| `java.util.concurrent.Future<java.lang.Integer> read(java.nio.ByteBuffer)` |  |
| `<A> void write(java.nio.ByteBuffer, A, java.nio.channels.CompletionHandler<java.lang.Integer,? super A>)` |  |
| `java.util.concurrent.Future<java.lang.Integer> write(java.nio.ByteBuffer)` |  |

---

### `interface AsynchronousChannel`

- **Extends:** `java.nio.channels.Channel`

---

### `class abstract AsynchronousChannelGroup`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AsynchronousChannelGroup(java.nio.channels.spi.AsynchronousChannelProvider)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract boolean awaitTermination(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `abstract boolean isShutdown()` |  |
| `abstract boolean isTerminated()` |  |
| `final java.nio.channels.spi.AsynchronousChannelProvider provider()` |  |
| `abstract void shutdown()` |  |
| `abstract void shutdownNow() throws java.io.IOException` |  |
| `static java.nio.channels.AsynchronousChannelGroup withCachedThreadPool(java.util.concurrent.ExecutorService, int) throws java.io.IOException` |  |
| `static java.nio.channels.AsynchronousChannelGroup withFixedThreadPool(int, java.util.concurrent.ThreadFactory) throws java.io.IOException` |  |
| `static java.nio.channels.AsynchronousChannelGroup withThreadPool(java.util.concurrent.ExecutorService) throws java.io.IOException` |  |

---

### `class AsynchronousCloseException`

- **Extends:** `java.nio.channels.ClosedChannelException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AsynchronousCloseException()` |  |

---

### `class abstract AsynchronousFileChannel`

- **Implements:** `java.nio.channels.AsynchronousChannel`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AsynchronousFileChannel()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void force(boolean) throws java.io.IOException` |  |
| `abstract <A> void lock(long, long, boolean, A, java.nio.channels.CompletionHandler<java.nio.channels.FileLock,? super A>)` |  |
| `final <A> void lock(A, java.nio.channels.CompletionHandler<java.nio.channels.FileLock,? super A>)` |  |
| `abstract java.util.concurrent.Future<java.nio.channels.FileLock> lock(long, long, boolean)` |  |
| `final java.util.concurrent.Future<java.nio.channels.FileLock> lock()` |  |
| `static java.nio.channels.AsynchronousFileChannel open(java.nio.file.Path, java.util.Set<? extends java.nio.file.OpenOption>, java.util.concurrent.ExecutorService, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `static java.nio.channels.AsynchronousFileChannel open(java.nio.file.Path, java.nio.file.OpenOption...) throws java.io.IOException` |  |
| `abstract <A> void read(java.nio.ByteBuffer, long, A, java.nio.channels.CompletionHandler<java.lang.Integer,? super A>)` |  |
| `abstract java.util.concurrent.Future<java.lang.Integer> read(java.nio.ByteBuffer, long)` |  |
| `abstract long size() throws java.io.IOException` |  |
| `abstract java.nio.channels.AsynchronousFileChannel truncate(long) throws java.io.IOException` |  |
| `abstract java.nio.channels.FileLock tryLock(long, long, boolean) throws java.io.IOException` |  |
| `final java.nio.channels.FileLock tryLock() throws java.io.IOException` |  |
| `abstract <A> void write(java.nio.ByteBuffer, long, A, java.nio.channels.CompletionHandler<java.lang.Integer,? super A>)` |  |
| `abstract java.util.concurrent.Future<java.lang.Integer> write(java.nio.ByteBuffer, long)` |  |

---

### `class abstract AsynchronousServerSocketChannel`

- **Implements:** `java.nio.channels.AsynchronousChannel java.nio.channels.NetworkChannel`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AsynchronousServerSocketChannel(java.nio.channels.spi.AsynchronousChannelProvider)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract <A> void accept(A, java.nio.channels.CompletionHandler<java.nio.channels.AsynchronousSocketChannel,? super A>)` |  |
| `abstract java.util.concurrent.Future<java.nio.channels.AsynchronousSocketChannel> accept()` |  |
| `final java.nio.channels.AsynchronousServerSocketChannel bind(java.net.SocketAddress) throws java.io.IOException` |  |
| `abstract java.nio.channels.AsynchronousServerSocketChannel bind(java.net.SocketAddress, int) throws java.io.IOException` |  |
| `static java.nio.channels.AsynchronousServerSocketChannel open(java.nio.channels.AsynchronousChannelGroup) throws java.io.IOException` |  |
| `static java.nio.channels.AsynchronousServerSocketChannel open() throws java.io.IOException` |  |
| `final java.nio.channels.spi.AsynchronousChannelProvider provider()` |  |
| `abstract <T> java.nio.channels.AsynchronousServerSocketChannel setOption(java.net.SocketOption<T>, T) throws java.io.IOException` |  |

---

### `class abstract AsynchronousSocketChannel`

- **Implements:** `java.nio.channels.AsynchronousByteChannel java.nio.channels.NetworkChannel`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AsynchronousSocketChannel(java.nio.channels.spi.AsynchronousChannelProvider)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.nio.channels.AsynchronousSocketChannel bind(java.net.SocketAddress) throws java.io.IOException` |  |
| `abstract <A> void connect(java.net.SocketAddress, A, java.nio.channels.CompletionHandler<java.lang.Void,? super A>)` |  |
| `abstract java.util.concurrent.Future<java.lang.Void> connect(java.net.SocketAddress)` |  |
| `abstract java.net.SocketAddress getRemoteAddress() throws java.io.IOException` |  |
| `static java.nio.channels.AsynchronousSocketChannel open(java.nio.channels.AsynchronousChannelGroup) throws java.io.IOException` |  |
| `static java.nio.channels.AsynchronousSocketChannel open() throws java.io.IOException` |  |
| `final java.nio.channels.spi.AsynchronousChannelProvider provider()` |  |
| `abstract <A> void read(java.nio.ByteBuffer, long, java.util.concurrent.TimeUnit, A, java.nio.channels.CompletionHandler<java.lang.Integer,? super A>)` |  |
| `final <A> void read(java.nio.ByteBuffer, A, java.nio.channels.CompletionHandler<java.lang.Integer,? super A>)` |  |
| `abstract <A> void read(java.nio.ByteBuffer[], int, int, long, java.util.concurrent.TimeUnit, A, java.nio.channels.CompletionHandler<java.lang.Long,? super A>)` |  |
| `abstract <T> java.nio.channels.AsynchronousSocketChannel setOption(java.net.SocketOption<T>, T) throws java.io.IOException` |  |
| `abstract java.nio.channels.AsynchronousSocketChannel shutdownInput() throws java.io.IOException` |  |
| `abstract java.nio.channels.AsynchronousSocketChannel shutdownOutput() throws java.io.IOException` |  |
| `abstract <A> void write(java.nio.ByteBuffer, long, java.util.concurrent.TimeUnit, A, java.nio.channels.CompletionHandler<java.lang.Integer,? super A>)` |  |
| `final <A> void write(java.nio.ByteBuffer, A, java.nio.channels.CompletionHandler<java.lang.Integer,? super A>)` |  |
| `abstract <A> void write(java.nio.ByteBuffer[], int, int, long, java.util.concurrent.TimeUnit, A, java.nio.channels.CompletionHandler<java.lang.Long,? super A>)` |  |

---

### `interface ByteChannel`

- **Extends:** `java.nio.channels.ReadableByteChannel java.nio.channels.WritableByteChannel`

---

### `class CancelledKeyException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CancelledKeyException()` |  |

---

### `interface Channel`

- **Extends:** `java.io.Closeable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isOpen()` |  |

---

### `class final Channels`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.channels.ReadableByteChannel newChannel(java.io.InputStream)` |  |
| `static java.nio.channels.WritableByteChannel newChannel(java.io.OutputStream)` |  |
| `static java.io.InputStream newInputStream(java.nio.channels.ReadableByteChannel)` |  |
| `static java.io.InputStream newInputStream(java.nio.channels.AsynchronousByteChannel)` |  |
| `static java.io.OutputStream newOutputStream(java.nio.channels.WritableByteChannel)` |  |
| `static java.io.OutputStream newOutputStream(java.nio.channels.AsynchronousByteChannel)` |  |
| `static java.io.Reader newReader(java.nio.channels.ReadableByteChannel, java.nio.charset.CharsetDecoder, int)` |  |
| `static java.io.Reader newReader(java.nio.channels.ReadableByteChannel, String)` |  |
| `static java.io.Writer newWriter(java.nio.channels.WritableByteChannel, java.nio.charset.CharsetEncoder, int)` |  |
| `static java.io.Writer newWriter(java.nio.channels.WritableByteChannel, String)` |  |

---

### `class ClosedByInterruptException`

- **Extends:** `java.nio.channels.AsynchronousCloseException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ClosedByInterruptException()` |  |

---

### `class ClosedChannelException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ClosedChannelException()` |  |

---

### `class ClosedSelectorException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ClosedSelectorException()` |  |

---

### `interface CompletionHandler<V, A>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void completed(V, A)` |  |
| `void failed(Throwable, A)` |  |

---

### `class ConnectionPendingException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConnectionPendingException()` |  |

---

### `class abstract DatagramChannel`

- **Extends:** `java.nio.channels.spi.AbstractSelectableChannel`
- **Implements:** `java.nio.channels.ByteChannel java.nio.channels.GatheringByteChannel java.nio.channels.MulticastChannel java.nio.channels.ScatteringByteChannel`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DatagramChannel(java.nio.channels.spi.SelectorProvider)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.nio.channels.DatagramChannel bind(java.net.SocketAddress) throws java.io.IOException` |  |
| `abstract java.nio.channels.DatagramChannel connect(java.net.SocketAddress) throws java.io.IOException` |  |
| `abstract java.nio.channels.DatagramChannel disconnect() throws java.io.IOException` |  |
| `abstract java.net.SocketAddress getRemoteAddress() throws java.io.IOException` |  |
| `abstract boolean isConnected()` |  |
| `static java.nio.channels.DatagramChannel open() throws java.io.IOException` |  |
| `static java.nio.channels.DatagramChannel open(java.net.ProtocolFamily) throws java.io.IOException` |  |
| `final long read(java.nio.ByteBuffer[]) throws java.io.IOException` |  |
| `abstract java.net.SocketAddress receive(java.nio.ByteBuffer) throws java.io.IOException` |  |
| `abstract int send(java.nio.ByteBuffer, java.net.SocketAddress) throws java.io.IOException` |  |
| `abstract <T> java.nio.channels.DatagramChannel setOption(java.net.SocketOption<T>, T) throws java.io.IOException` |  |
| `abstract java.net.DatagramSocket socket()` |  |
| `final int validOps()` |  |
| `final long write(java.nio.ByteBuffer[]) throws java.io.IOException` |  |

---

### `class abstract FileChannel`

- **Extends:** `java.nio.channels.spi.AbstractInterruptibleChannel`
- **Implements:** `java.nio.channels.GatheringByteChannel java.nio.channels.ScatteringByteChannel java.nio.channels.SeekableByteChannel`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileChannel()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void force(boolean) throws java.io.IOException` |  |
| `abstract java.nio.channels.FileLock lock(long, long, boolean) throws java.io.IOException` |  |
| `final java.nio.channels.FileLock lock() throws java.io.IOException` |  |
| `abstract java.nio.MappedByteBuffer map(java.nio.channels.FileChannel.MapMode, long, long) throws java.io.IOException` |  |
| `static java.nio.channels.FileChannel open(java.nio.file.Path, java.util.Set<? extends java.nio.file.OpenOption>, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `static java.nio.channels.FileChannel open(java.nio.file.Path, java.nio.file.OpenOption...) throws java.io.IOException` |  |
| `abstract java.nio.channels.FileChannel position(long) throws java.io.IOException` |  |
| `final long read(java.nio.ByteBuffer[]) throws java.io.IOException` |  |
| `abstract int read(java.nio.ByteBuffer, long) throws java.io.IOException` |  |
| `abstract long transferFrom(java.nio.channels.ReadableByteChannel, long, long) throws java.io.IOException` |  |
| `abstract long transferTo(long, long, java.nio.channels.WritableByteChannel) throws java.io.IOException` |  |
| `abstract java.nio.channels.FileChannel truncate(long) throws java.io.IOException` |  |
| `abstract java.nio.channels.FileLock tryLock(long, long, boolean) throws java.io.IOException` |  |
| `final java.nio.channels.FileLock tryLock() throws java.io.IOException` |  |
| `final long write(java.nio.ByteBuffer[]) throws java.io.IOException` |  |
| `abstract int write(java.nio.ByteBuffer, long) throws java.io.IOException` |  |

---

### `class static FileChannel.MapMode`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.nio.channels.FileChannel.MapMode PRIVATE` |  |
| `static final java.nio.channels.FileChannel.MapMode READ_ONLY` |  |
| `static final java.nio.channels.FileChannel.MapMode READ_WRITE` |  |

---

### `class abstract FileLock`

- **Implements:** `java.lang.AutoCloseable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileLock(java.nio.channels.FileChannel, long, long, boolean)` |  |
| `FileLock(java.nio.channels.AsynchronousFileChannel, long, long, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.channels.Channel acquiredBy()` |  |
| `final java.nio.channels.FileChannel channel()` |  |
| `final void close() throws java.io.IOException` |  |
| `final boolean isShared()` |  |
| `abstract boolean isValid()` |  |
| `final boolean overlaps(long, long)` |  |
| `final long position()` |  |
| `abstract void release() throws java.io.IOException` |  |
| `final long size()` |  |
| `final String toString()` |  |

---

### `class FileLockInterruptionException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileLockInterruptionException()` |  |

---

### `interface GatheringByteChannel`

- **Extends:** `java.nio.channels.WritableByteChannel`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long write(java.nio.ByteBuffer[], int, int) throws java.io.IOException` |  |
| `long write(java.nio.ByteBuffer[]) throws java.io.IOException` |  |

---

### `class IllegalBlockingModeException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalBlockingModeException()` |  |

---

### `class IllegalChannelGroupException`

- **Extends:** `java.lang.IllegalArgumentException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalChannelGroupException()` |  |

---

### `class IllegalSelectorException`

- **Extends:** `java.lang.IllegalArgumentException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalSelectorException()` |  |

---

### `class InterruptedByTimeoutException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InterruptedByTimeoutException()` |  |

---

### `interface InterruptibleChannel`

- **Extends:** `java.nio.channels.Channel`

---

### `class abstract MembershipKey`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MembershipKey()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.nio.channels.MembershipKey block(java.net.InetAddress) throws java.io.IOException` |  |
| `abstract java.nio.channels.MulticastChannel channel()` |  |
| `abstract void drop()` |  |
| `abstract java.net.InetAddress group()` |  |
| `abstract boolean isValid()` |  |
| `abstract java.net.NetworkInterface networkInterface()` |  |
| `abstract java.net.InetAddress sourceAddress()` |  |
| `abstract java.nio.channels.MembershipKey unblock(java.net.InetAddress)` |  |

---

### `interface MulticastChannel`

- **Extends:** `java.nio.channels.NetworkChannel`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.channels.MembershipKey join(java.net.InetAddress, java.net.NetworkInterface) throws java.io.IOException` |  |
| `java.nio.channels.MembershipKey join(java.net.InetAddress, java.net.NetworkInterface, java.net.InetAddress) throws java.io.IOException` |  |

---

### `interface NetworkChannel`

- **Extends:** `java.nio.channels.Channel`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.channels.NetworkChannel bind(java.net.SocketAddress) throws java.io.IOException` |  |
| `java.net.SocketAddress getLocalAddress() throws java.io.IOException` |  |
| `<T> T getOption(java.net.SocketOption<T>) throws java.io.IOException` |  |
| `<T> java.nio.channels.NetworkChannel setOption(java.net.SocketOption<T>, T) throws java.io.IOException` |  |
| `java.util.Set<java.net.SocketOption<?>> supportedOptions()` |  |

---

### `class NoConnectionPendingException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NoConnectionPendingException()` |  |

---

### `class NonReadableChannelException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NonReadableChannelException()` |  |

---

### `class NonWritableChannelException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NonWritableChannelException()` |  |

---

### `class NotYetBoundException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NotYetBoundException()` |  |

---

### `class NotYetConnectedException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NotYetConnectedException()` |  |

---

### `class OverlappingFileLockException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `OverlappingFileLockException()` |  |

---

### `class abstract Pipe`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Pipe()` |  |
| `Pipe.SinkChannel(java.nio.channels.spi.SelectorProvider)` |  |
| `Pipe.SourceChannel(java.nio.channels.spi.SelectorProvider)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.channels.Pipe open() throws java.io.IOException` |  |
| `abstract java.nio.channels.Pipe.SinkChannel sink()` |  |
| `abstract java.nio.channels.Pipe.SourceChannel source()` |  |
| `final int validOps()` |  |
| `final int validOps()` |  |

---

### `class ReadPendingException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ReadPendingException()` |  |

---

### `interface ReadableByteChannel`

- **Extends:** `java.nio.channels.Channel`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int read(java.nio.ByteBuffer) throws java.io.IOException` |  |

---

### `interface ScatteringByteChannel`

- **Extends:** `java.nio.channels.ReadableByteChannel`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long read(java.nio.ByteBuffer[], int, int) throws java.io.IOException` |  |
| `long read(java.nio.ByteBuffer[]) throws java.io.IOException` |  |

---

### `interface SeekableByteChannel`

- **Extends:** `java.nio.channels.ByteChannel`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long position() throws java.io.IOException` |  |
| `java.nio.channels.SeekableByteChannel position(long) throws java.io.IOException` |  |
| `long size() throws java.io.IOException` |  |
| `java.nio.channels.SeekableByteChannel truncate(long) throws java.io.IOException` |  |

---

### `class abstract SelectableChannel`

- **Extends:** `java.nio.channels.spi.AbstractInterruptibleChannel`
- **Implements:** `java.nio.channels.Channel`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SelectableChannel()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract Object blockingLock()` |  |
| `abstract java.nio.channels.SelectableChannel configureBlocking(boolean) throws java.io.IOException` |  |
| `abstract boolean isBlocking()` |  |
| `abstract boolean isRegistered()` |  |
| `abstract java.nio.channels.SelectionKey keyFor(java.nio.channels.Selector)` |  |
| `abstract java.nio.channels.spi.SelectorProvider provider()` |  |
| `abstract java.nio.channels.SelectionKey register(java.nio.channels.Selector, int, Object) throws java.nio.channels.ClosedChannelException` |  |
| `final java.nio.channels.SelectionKey register(java.nio.channels.Selector, int) throws java.nio.channels.ClosedChannelException` |  |
| `abstract int validOps()` |  |

---

### `class abstract SelectionKey`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SelectionKey()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int OP_ACCEPT = 16` |  |
| `static final int OP_CONNECT = 8` |  |
| `static final int OP_READ = 1` |  |
| `static final int OP_WRITE = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final Object attach(Object)` |  |
| `final Object attachment()` |  |
| `abstract void cancel()` |  |
| `abstract java.nio.channels.SelectableChannel channel()` |  |
| `abstract int interestOps()` |  |
| `abstract java.nio.channels.SelectionKey interestOps(int)` |  |
| `final boolean isAcceptable()` |  |
| `final boolean isConnectable()` |  |
| `final boolean isReadable()` |  |
| `abstract boolean isValid()` |  |
| `final boolean isWritable()` |  |
| `abstract int readyOps()` |  |
| `abstract java.nio.channels.Selector selector()` |  |

---

### `class abstract Selector`

- **Implements:** `java.io.Closeable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Selector()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract boolean isOpen()` |  |
| `abstract java.util.Set<java.nio.channels.SelectionKey> keys()` |  |
| `static java.nio.channels.Selector open() throws java.io.IOException` |  |
| `abstract java.nio.channels.spi.SelectorProvider provider()` |  |
| `abstract int select(long) throws java.io.IOException` |  |
| `abstract int select() throws java.io.IOException` |  |
| `abstract int selectNow() throws java.io.IOException` |  |
| `abstract java.util.Set<java.nio.channels.SelectionKey> selectedKeys()` |  |
| `abstract java.nio.channels.Selector wakeup()` |  |

---

### `class abstract ServerSocketChannel`

- **Extends:** `java.nio.channels.spi.AbstractSelectableChannel`
- **Implements:** `java.nio.channels.NetworkChannel`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ServerSocketChannel(java.nio.channels.spi.SelectorProvider)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.nio.channels.SocketChannel accept() throws java.io.IOException` |  |
| `final java.nio.channels.ServerSocketChannel bind(java.net.SocketAddress) throws java.io.IOException` |  |
| `abstract java.nio.channels.ServerSocketChannel bind(java.net.SocketAddress, int) throws java.io.IOException` |  |
| `static java.nio.channels.ServerSocketChannel open() throws java.io.IOException` |  |
| `abstract <T> java.nio.channels.ServerSocketChannel setOption(java.net.SocketOption<T>, T) throws java.io.IOException` |  |
| `abstract java.net.ServerSocket socket()` |  |
| `final int validOps()` |  |

---

### `class ShutdownChannelGroupException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ShutdownChannelGroupException()` |  |

---

### `class abstract SocketChannel`

- **Extends:** `java.nio.channels.spi.AbstractSelectableChannel`
- **Implements:** `java.nio.channels.ByteChannel java.nio.channels.GatheringByteChannel java.nio.channels.NetworkChannel java.nio.channels.ScatteringByteChannel`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SocketChannel(java.nio.channels.spi.SelectorProvider)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.nio.channels.SocketChannel bind(java.net.SocketAddress) throws java.io.IOException` |  |
| `abstract boolean connect(java.net.SocketAddress) throws java.io.IOException` |  |
| `abstract boolean finishConnect() throws java.io.IOException` |  |
| `abstract java.net.SocketAddress getRemoteAddress() throws java.io.IOException` |  |
| `abstract boolean isConnected()` |  |
| `abstract boolean isConnectionPending()` |  |
| `static java.nio.channels.SocketChannel open() throws java.io.IOException` |  |
| `static java.nio.channels.SocketChannel open(java.net.SocketAddress) throws java.io.IOException` |  |
| `final long read(java.nio.ByteBuffer[]) throws java.io.IOException` |  |
| `abstract <T> java.nio.channels.SocketChannel setOption(java.net.SocketOption<T>, T) throws java.io.IOException` |  |
| `abstract java.nio.channels.SocketChannel shutdownInput() throws java.io.IOException` |  |
| `abstract java.nio.channels.SocketChannel shutdownOutput() throws java.io.IOException` |  |
| `abstract java.net.Socket socket()` |  |
| `final int validOps()` |  |
| `final long write(java.nio.ByteBuffer[]) throws java.io.IOException` |  |

---

### `class UnresolvedAddressException`

- **Extends:** `java.lang.IllegalArgumentException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnresolvedAddressException()` |  |

---

### `class UnsupportedAddressTypeException`

- **Extends:** `java.lang.IllegalArgumentException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnsupportedAddressTypeException()` |  |

---

### `interface WritableByteChannel`

- **Extends:** `java.nio.channels.Channel`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int write(java.nio.ByteBuffer) throws java.io.IOException` |  |

---

### `class WritePendingException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WritePendingException()` |  |

---

## Package: `java.nio.channels.spi`

### `class abstract AbstractInterruptibleChannel`

- **Implements:** `java.nio.channels.Channel java.nio.channels.InterruptibleChannel`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractInterruptibleChannel()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void begin()` |  |
| `final void close() throws java.io.IOException` |  |
| `final void end(boolean) throws java.nio.channels.AsynchronousCloseException` |  |
| `abstract void implCloseChannel() throws java.io.IOException` |  |
| `final boolean isOpen()` |  |

---

### `class abstract AbstractSelectableChannel`

- **Extends:** `java.nio.channels.SelectableChannel`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractSelectableChannel(java.nio.channels.spi.SelectorProvider)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final Object blockingLock()` |  |
| `final java.nio.channels.SelectableChannel configureBlocking(boolean) throws java.io.IOException` |  |
| `final void implCloseChannel() throws java.io.IOException` |  |
| `abstract void implCloseSelectableChannel() throws java.io.IOException` |  |
| `abstract void implConfigureBlocking(boolean) throws java.io.IOException` |  |
| `final boolean isBlocking()` |  |
| `final boolean isRegistered()` |  |
| `final java.nio.channels.SelectionKey keyFor(java.nio.channels.Selector)` |  |
| `final java.nio.channels.spi.SelectorProvider provider()` |  |
| `final java.nio.channels.SelectionKey register(java.nio.channels.Selector, int, Object) throws java.nio.channels.ClosedChannelException` |  |

---

### `class abstract AbstractSelectionKey`

- **Extends:** `java.nio.channels.SelectionKey`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractSelectionKey()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void cancel()` |  |
| `final boolean isValid()` |  |

---

### `class abstract AbstractSelector`

- **Extends:** `java.nio.channels.Selector`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractSelector(java.nio.channels.spi.SelectorProvider)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void begin()` |  |
| `final java.util.Set<java.nio.channels.SelectionKey> cancelledKeys()` |  |
| `final void close() throws java.io.IOException` |  |
| `final void deregister(java.nio.channels.spi.AbstractSelectionKey)` |  |
| `final void end()` |  |
| `abstract void implCloseSelector() throws java.io.IOException` |  |
| `final boolean isOpen()` |  |
| `final java.nio.channels.spi.SelectorProvider provider()` |  |
| `abstract java.nio.channels.SelectionKey register(java.nio.channels.spi.AbstractSelectableChannel, int, Object)` |  |

---

### `class abstract AsynchronousChannelProvider`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AsynchronousChannelProvider()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.nio.channels.AsynchronousChannelGroup openAsynchronousChannelGroup(int, java.util.concurrent.ThreadFactory) throws java.io.IOException` |  |
| `abstract java.nio.channels.AsynchronousChannelGroup openAsynchronousChannelGroup(java.util.concurrent.ExecutorService, int) throws java.io.IOException` |  |
| `abstract java.nio.channels.AsynchronousServerSocketChannel openAsynchronousServerSocketChannel(java.nio.channels.AsynchronousChannelGroup) throws java.io.IOException` |  |
| `abstract java.nio.channels.AsynchronousSocketChannel openAsynchronousSocketChannel(java.nio.channels.AsynchronousChannelGroup) throws java.io.IOException` |  |
| `static java.nio.channels.spi.AsynchronousChannelProvider provider()` |  |

---

### `class abstract SelectorProvider`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SelectorProvider()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.channels.Channel inheritedChannel() throws java.io.IOException` |  |
| `abstract java.nio.channels.DatagramChannel openDatagramChannel() throws java.io.IOException` |  |
| `abstract java.nio.channels.DatagramChannel openDatagramChannel(java.net.ProtocolFamily) throws java.io.IOException` |  |
| `abstract java.nio.channels.Pipe openPipe() throws java.io.IOException` |  |
| `abstract java.nio.channels.spi.AbstractSelector openSelector() throws java.io.IOException` |  |
| `abstract java.nio.channels.ServerSocketChannel openServerSocketChannel() throws java.io.IOException` |  |
| `abstract java.nio.channels.SocketChannel openSocketChannel() throws java.io.IOException` |  |
| `static java.nio.channels.spi.SelectorProvider provider()` |  |

---

## Package: `java.nio.charset`

### `class CharacterCodingException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CharacterCodingException()` |  |

---

### `class abstract Charset`

- **Implements:** `java.lang.Comparable<java.nio.charset.Charset>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Charset(String, String[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.util.Set<java.lang.String> aliases()` |  |
| `static java.util.SortedMap<java.lang.String,java.nio.charset.Charset> availableCharsets()` |  |
| `boolean canEncode()` |  |
| `final int compareTo(java.nio.charset.Charset)` |  |
| `abstract boolean contains(java.nio.charset.Charset)` |  |
| `final java.nio.CharBuffer decode(java.nio.ByteBuffer)` |  |
| `static java.nio.charset.Charset defaultCharset()` |  |
| `String displayName()` |  |
| `String displayName(java.util.Locale)` |  |
| `final java.nio.ByteBuffer encode(java.nio.CharBuffer)` |  |
| `final java.nio.ByteBuffer encode(String)` |  |
| `final boolean equals(Object)` |  |
| `static java.nio.charset.Charset forName(String)` |  |
| `final int hashCode()` |  |
| `final boolean isRegistered()` |  |
| `static boolean isSupported(String)` |  |
| `final String name()` |  |
| `abstract java.nio.charset.CharsetDecoder newDecoder()` |  |
| `abstract java.nio.charset.CharsetEncoder newEncoder()` |  |
| `final String toString()` |  |

---

### `class abstract CharsetDecoder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CharsetDecoder(java.nio.charset.Charset, float, float)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final float averageCharsPerByte()` |  |
| `final java.nio.charset.Charset charset()` |  |
| `final java.nio.charset.CoderResult decode(java.nio.ByteBuffer, java.nio.CharBuffer, boolean)` |  |
| `final java.nio.CharBuffer decode(java.nio.ByteBuffer) throws java.nio.charset.CharacterCodingException` |  |
| `abstract java.nio.charset.CoderResult decodeLoop(java.nio.ByteBuffer, java.nio.CharBuffer)` |  |
| `java.nio.charset.Charset detectedCharset()` |  |
| `final java.nio.charset.CoderResult flush(java.nio.CharBuffer)` |  |
| `java.nio.charset.CoderResult implFlush(java.nio.CharBuffer)` |  |
| `void implOnMalformedInput(java.nio.charset.CodingErrorAction)` |  |
| `void implOnUnmappableCharacter(java.nio.charset.CodingErrorAction)` |  |
| `void implReplaceWith(String)` |  |
| `void implReset()` |  |
| `boolean isAutoDetecting()` |  |
| `boolean isCharsetDetected()` |  |
| `java.nio.charset.CodingErrorAction malformedInputAction()` |  |
| `final float maxCharsPerByte()` |  |
| `final java.nio.charset.CharsetDecoder onMalformedInput(java.nio.charset.CodingErrorAction)` |  |
| `final java.nio.charset.CharsetDecoder onUnmappableCharacter(java.nio.charset.CodingErrorAction)` |  |
| `final java.nio.charset.CharsetDecoder replaceWith(String)` |  |
| `final String replacement()` |  |
| `final java.nio.charset.CharsetDecoder reset()` |  |
| `java.nio.charset.CodingErrorAction unmappableCharacterAction()` |  |

---

### `class abstract CharsetEncoder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CharsetEncoder(java.nio.charset.Charset, float, float, byte[])` |  |
| `CharsetEncoder(java.nio.charset.Charset, float, float)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final float averageBytesPerChar()` |  |
| `boolean canEncode(char)` |  |
| `boolean canEncode(CharSequence)` |  |
| `final java.nio.charset.Charset charset()` |  |
| `final java.nio.charset.CoderResult encode(java.nio.CharBuffer, java.nio.ByteBuffer, boolean)` |  |
| `final java.nio.ByteBuffer encode(java.nio.CharBuffer) throws java.nio.charset.CharacterCodingException` |  |
| `abstract java.nio.charset.CoderResult encodeLoop(java.nio.CharBuffer, java.nio.ByteBuffer)` |  |
| `final java.nio.charset.CoderResult flush(java.nio.ByteBuffer)` |  |
| `java.nio.charset.CoderResult implFlush(java.nio.ByteBuffer)` |  |
| `void implOnMalformedInput(java.nio.charset.CodingErrorAction)` |  |
| `void implOnUnmappableCharacter(java.nio.charset.CodingErrorAction)` |  |
| `void implReplaceWith(byte[])` |  |
| `void implReset()` |  |
| `boolean isLegalReplacement(byte[])` |  |
| `java.nio.charset.CodingErrorAction malformedInputAction()` |  |
| `final float maxBytesPerChar()` |  |
| `final java.nio.charset.CharsetEncoder onMalformedInput(java.nio.charset.CodingErrorAction)` |  |
| `final java.nio.charset.CharsetEncoder onUnmappableCharacter(java.nio.charset.CodingErrorAction)` |  |
| `final java.nio.charset.CharsetEncoder replaceWith(byte[])` |  |
| `final byte[] replacement()` |  |
| `final java.nio.charset.CharsetEncoder reset()` |  |
| `java.nio.charset.CodingErrorAction unmappableCharacterAction()` |  |

---

### `class CoderMalfunctionError`

- **Extends:** `java.lang.Error`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CoderMalfunctionError(Exception)` |  |

---

### `class CoderResult`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.nio.charset.CoderResult OVERFLOW` |  |
| `static final java.nio.charset.CoderResult UNDERFLOW` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isError()` |  |
| `boolean isMalformed()` |  |
| `boolean isOverflow()` |  |
| `boolean isUnderflow()` |  |
| `boolean isUnmappable()` |  |
| `int length()` |  |
| `static java.nio.charset.CoderResult malformedForLength(int)` |  |
| `void throwException() throws java.nio.charset.CharacterCodingException` |  |
| `static java.nio.charset.CoderResult unmappableForLength(int)` |  |

---

### `class CodingErrorAction`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.nio.charset.CodingErrorAction IGNORE` |  |
| `static final java.nio.charset.CodingErrorAction REPLACE` |  |
| `static final java.nio.charset.CodingErrorAction REPORT` |  |

---

### `class IllegalCharsetNameException`

- **Extends:** `java.lang.IllegalArgumentException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalCharsetNameException(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getCharsetName()` |  |

---

### `class MalformedInputException`

- **Extends:** `java.nio.charset.CharacterCodingException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MalformedInputException(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getInputLength()` |  |

---

### `class final StandardCharsets`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.nio.charset.Charset ISO_8859_1` |  |
| `static final java.nio.charset.Charset US_ASCII` |  |
| `static final java.nio.charset.Charset UTF_16` |  |
| `static final java.nio.charset.Charset UTF_16BE` |  |
| `static final java.nio.charset.Charset UTF_16LE` |  |
| `static final java.nio.charset.Charset UTF_8` |  |

---

### `class UnmappableCharacterException`

- **Extends:** `java.nio.charset.CharacterCodingException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnmappableCharacterException(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getInputLength()` |  |

---

### `class UnsupportedCharsetException`

- **Extends:** `java.lang.IllegalArgumentException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnsupportedCharsetException(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getCharsetName()` |  |

---

## Package: `java.nio.charset.spi`

### `class abstract CharsetProvider`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CharsetProvider()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.nio.charset.Charset charsetForName(String)` |  |
| `abstract java.util.Iterator<java.nio.charset.Charset> charsets()` |  |

---

## Package: `java.nio.file`

### `class AccessDeniedException`

- **Extends:** `java.nio.file.FileSystemException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessDeniedException(String)` |  |
| `AccessDeniedException(String, String, String)` |  |

---

### `enum AccessMode`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.nio.file.AccessMode EXECUTE` |  |
| `static final java.nio.file.AccessMode READ` |  |
| `static final java.nio.file.AccessMode WRITE` |  |

---

### `class AtomicMoveNotSupportedException`

- **Extends:** `java.nio.file.FileSystemException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicMoveNotSupportedException(String, String, String)` |  |

---

### `class ClosedDirectoryStreamException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ClosedDirectoryStreamException()` |  |

---

### `class ClosedFileSystemException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ClosedFileSystemException()` |  |

---

### `class ClosedWatchServiceException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ClosedWatchServiceException()` |  |

---

### `interface CopyOption`


---

### `class final DirectoryIteratorException`

- **Extends:** `java.util.ConcurrentModificationException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DirectoryIteratorException(java.io.IOException)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.io.IOException getCause()` |  |

---

### `class DirectoryNotEmptyException`

- **Extends:** `java.nio.file.FileSystemException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DirectoryNotEmptyException(String)` |  |

---

### `interface DirectoryStream<T>`

- **Extends:** `java.io.Closeable java.lang.Iterable<T>`

---

### `interface static DirectoryStream.Filter<T>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean accept(T) throws java.io.IOException` |  |

---

### `class FileAlreadyExistsException`

- **Extends:** `java.nio.file.FileSystemException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileAlreadyExistsException(String)` |  |
| `FileAlreadyExistsException(String, String, String)` |  |

---

### `class abstract FileStore`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileStore()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract Object getAttribute(String) throws java.io.IOException` |  |
| `abstract <V extends java.nio.file.attribute.FileStoreAttributeView> V getFileStoreAttributeView(Class<V>)` |  |
| `abstract long getTotalSpace() throws java.io.IOException` |  |
| `abstract long getUnallocatedSpace() throws java.io.IOException` |  |
| `abstract long getUsableSpace() throws java.io.IOException` |  |
| `abstract boolean isReadOnly()` |  |
| `abstract String name()` |  |
| `abstract boolean supportsFileAttributeView(Class<? extends java.nio.file.attribute.FileAttributeView>)` |  |
| `abstract boolean supportsFileAttributeView(String)` |  |
| `abstract String type()` |  |

---

### `class abstract FileSystem`

- **Implements:** `java.io.Closeable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileSystem()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract Iterable<java.nio.file.FileStore> getFileStores()` |  |
| `abstract java.nio.file.Path getPath(String, java.lang.String...)` |  |
| `abstract java.nio.file.PathMatcher getPathMatcher(String)` |  |
| `abstract Iterable<java.nio.file.Path> getRootDirectories()` |  |
| `abstract String getSeparator()` |  |
| `abstract java.nio.file.attribute.UserPrincipalLookupService getUserPrincipalLookupService()` |  |
| `abstract boolean isOpen()` |  |
| `abstract boolean isReadOnly()` |  |
| `abstract java.nio.file.WatchService newWatchService() throws java.io.IOException` |  |
| `abstract java.nio.file.spi.FileSystemProvider provider()` |  |
| `abstract java.util.Set<java.lang.String> supportedFileAttributeViews()` |  |

---

### `class FileSystemAlreadyExistsException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileSystemAlreadyExistsException()` |  |
| `FileSystemAlreadyExistsException(String)` |  |

---

### `class FileSystemException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileSystemException(String)` |  |
| `FileSystemException(String, String, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getFile()` |  |
| `String getOtherFile()` |  |
| `String getReason()` |  |

---

### `class FileSystemLoopException`

- **Extends:** `java.nio.file.FileSystemException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileSystemLoopException(String)` |  |

---

### `class FileSystemNotFoundException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileSystemNotFoundException()` |  |
| `FileSystemNotFoundException(String)` |  |

---

### `class final FileSystems`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.file.FileSystem getDefault()` |  |
| `static java.nio.file.FileSystem getFileSystem(java.net.URI)` |  |
| `static java.nio.file.FileSystem newFileSystem(java.net.URI, java.util.Map<java.lang.String,?>) throws java.io.IOException` |  |
| `static java.nio.file.FileSystem newFileSystem(java.net.URI, java.util.Map<java.lang.String,?>, ClassLoader) throws java.io.IOException` |  |
| `static java.nio.file.FileSystem newFileSystem(java.nio.file.Path, ClassLoader) throws java.io.IOException` |  |

---

### `enum FileVisitOption`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.nio.file.FileVisitOption FOLLOW_LINKS` |  |

---

### `enum FileVisitResult`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.nio.file.FileVisitResult CONTINUE` |  |
| `static final java.nio.file.FileVisitResult SKIP_SIBLINGS` |  |
| `static final java.nio.file.FileVisitResult SKIP_SUBTREE` |  |
| `static final java.nio.file.FileVisitResult TERMINATE` |  |

---

### `interface FileVisitor<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.file.FileVisitResult postVisitDirectory(T, java.io.IOException) throws java.io.IOException` |  |
| `java.nio.file.FileVisitResult preVisitDirectory(T, java.nio.file.attribute.BasicFileAttributes) throws java.io.IOException` |  |
| `java.nio.file.FileVisitResult visitFile(T, java.nio.file.attribute.BasicFileAttributes) throws java.io.IOException` |  |
| `java.nio.file.FileVisitResult visitFileFailed(T, java.io.IOException) throws java.io.IOException` |  |

---

### `class final Files`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.file.Path copy(java.nio.file.Path, java.nio.file.Path, java.nio.file.CopyOption...) throws java.io.IOException` |  |
| `static long copy(java.io.InputStream, java.nio.file.Path, java.nio.file.CopyOption...) throws java.io.IOException` |  |
| `static long copy(java.nio.file.Path, java.io.OutputStream) throws java.io.IOException` |  |
| `static java.nio.file.Path createDirectories(java.nio.file.Path, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `static java.nio.file.Path createDirectory(java.nio.file.Path, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `static java.nio.file.Path createFile(java.nio.file.Path, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `static java.nio.file.Path createLink(java.nio.file.Path, java.nio.file.Path) throws java.io.IOException` |  |
| `static java.nio.file.Path createSymbolicLink(java.nio.file.Path, java.nio.file.Path, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `static java.nio.file.Path createTempDirectory(java.nio.file.Path, String, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `static java.nio.file.Path createTempDirectory(String, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `static java.nio.file.Path createTempFile(java.nio.file.Path, String, String, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `static java.nio.file.Path createTempFile(String, String, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `static void delete(java.nio.file.Path) throws java.io.IOException` |  |
| `static boolean deleteIfExists(java.nio.file.Path) throws java.io.IOException` |  |
| `static boolean exists(java.nio.file.Path, java.nio.file.LinkOption...)` |  |
| `static java.util.stream.Stream<java.nio.file.Path> find(java.nio.file.Path, int, java.util.function.BiPredicate<java.nio.file.Path,java.nio.file.attribute.BasicFileAttributes>, java.nio.file.FileVisitOption...) throws java.io.IOException` |  |
| `static Object getAttribute(java.nio.file.Path, String, java.nio.file.LinkOption...) throws java.io.IOException` |  |
| `static <V extends java.nio.file.attribute.FileAttributeView> V getFileAttributeView(java.nio.file.Path, Class<V>, java.nio.file.LinkOption...)` |  |
| `static java.nio.file.FileStore getFileStore(java.nio.file.Path) throws java.io.IOException` |  |
| `static java.nio.file.attribute.FileTime getLastModifiedTime(java.nio.file.Path, java.nio.file.LinkOption...) throws java.io.IOException` |  |
| `static java.nio.file.attribute.UserPrincipal getOwner(java.nio.file.Path, java.nio.file.LinkOption...) throws java.io.IOException` |  |
| `static java.util.Set<java.nio.file.attribute.PosixFilePermission> getPosixFilePermissions(java.nio.file.Path, java.nio.file.LinkOption...) throws java.io.IOException` |  |
| `static boolean isDirectory(java.nio.file.Path, java.nio.file.LinkOption...)` |  |
| `static boolean isExecutable(java.nio.file.Path)` |  |
| `static boolean isHidden(java.nio.file.Path) throws java.io.IOException` |  |
| `static boolean isReadable(java.nio.file.Path)` |  |
| `static boolean isRegularFile(java.nio.file.Path, java.nio.file.LinkOption...)` |  |
| `static boolean isSameFile(java.nio.file.Path, java.nio.file.Path) throws java.io.IOException` |  |
| `static boolean isSymbolicLink(java.nio.file.Path)` |  |
| `static boolean isWritable(java.nio.file.Path)` |  |
| `static java.util.stream.Stream<java.lang.String> lines(java.nio.file.Path, java.nio.charset.Charset) throws java.io.IOException` |  |
| `static java.util.stream.Stream<java.lang.String> lines(java.nio.file.Path) throws java.io.IOException` |  |
| `static java.util.stream.Stream<java.nio.file.Path> list(java.nio.file.Path) throws java.io.IOException` |  |
| `static java.nio.file.Path move(java.nio.file.Path, java.nio.file.Path, java.nio.file.CopyOption...) throws java.io.IOException` |  |
| `static java.io.BufferedReader newBufferedReader(java.nio.file.Path, java.nio.charset.Charset) throws java.io.IOException` |  |
| `static java.io.BufferedReader newBufferedReader(java.nio.file.Path) throws java.io.IOException` |  |
| `static java.io.BufferedWriter newBufferedWriter(java.nio.file.Path, java.nio.charset.Charset, java.nio.file.OpenOption...) throws java.io.IOException` |  |
| `static java.io.BufferedWriter newBufferedWriter(java.nio.file.Path, java.nio.file.OpenOption...) throws java.io.IOException` |  |
| `static java.nio.channels.SeekableByteChannel newByteChannel(java.nio.file.Path, java.util.Set<? extends java.nio.file.OpenOption>, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `static java.nio.channels.SeekableByteChannel newByteChannel(java.nio.file.Path, java.nio.file.OpenOption...) throws java.io.IOException` |  |
| `static java.nio.file.DirectoryStream<java.nio.file.Path> newDirectoryStream(java.nio.file.Path) throws java.io.IOException` |  |
| `static java.nio.file.DirectoryStream<java.nio.file.Path> newDirectoryStream(java.nio.file.Path, String) throws java.io.IOException` |  |
| `static java.nio.file.DirectoryStream<java.nio.file.Path> newDirectoryStream(java.nio.file.Path, java.nio.file.DirectoryStream.Filter<? super java.nio.file.Path>) throws java.io.IOException` |  |
| `static java.io.InputStream newInputStream(java.nio.file.Path, java.nio.file.OpenOption...) throws java.io.IOException` |  |
| `static java.io.OutputStream newOutputStream(java.nio.file.Path, java.nio.file.OpenOption...) throws java.io.IOException` |  |
| `static boolean notExists(java.nio.file.Path, java.nio.file.LinkOption...)` |  |
| `static String probeContentType(java.nio.file.Path) throws java.io.IOException` |  |
| `static byte[] readAllBytes(java.nio.file.Path) throws java.io.IOException` |  |
| `static java.util.List<java.lang.String> readAllLines(java.nio.file.Path, java.nio.charset.Charset) throws java.io.IOException` |  |
| `static java.util.List<java.lang.String> readAllLines(java.nio.file.Path) throws java.io.IOException` |  |
| `static <A extends java.nio.file.attribute.BasicFileAttributes> A readAttributes(java.nio.file.Path, Class<A>, java.nio.file.LinkOption...) throws java.io.IOException` |  |
| `static java.util.Map<java.lang.String,java.lang.Object> readAttributes(java.nio.file.Path, String, java.nio.file.LinkOption...) throws java.io.IOException` |  |
| `static java.nio.file.Path readSymbolicLink(java.nio.file.Path) throws java.io.IOException` |  |
| `static java.nio.file.Path setAttribute(java.nio.file.Path, String, Object, java.nio.file.LinkOption...) throws java.io.IOException` |  |
| `static java.nio.file.Path setLastModifiedTime(java.nio.file.Path, java.nio.file.attribute.FileTime) throws java.io.IOException` |  |
| `static java.nio.file.Path setOwner(java.nio.file.Path, java.nio.file.attribute.UserPrincipal) throws java.io.IOException` |  |
| `static java.nio.file.Path setPosixFilePermissions(java.nio.file.Path, java.util.Set<java.nio.file.attribute.PosixFilePermission>) throws java.io.IOException` |  |
| `static long size(java.nio.file.Path) throws java.io.IOException` |  |
| `static java.util.stream.Stream<java.nio.file.Path> walk(java.nio.file.Path, int, java.nio.file.FileVisitOption...) throws java.io.IOException` |  |
| `static java.util.stream.Stream<java.nio.file.Path> walk(java.nio.file.Path, java.nio.file.FileVisitOption...) throws java.io.IOException` |  |
| `static java.nio.file.Path walkFileTree(java.nio.file.Path, java.util.Set<java.nio.file.FileVisitOption>, int, java.nio.file.FileVisitor<? super java.nio.file.Path>) throws java.io.IOException` |  |
| `static java.nio.file.Path walkFileTree(java.nio.file.Path, java.nio.file.FileVisitor<? super java.nio.file.Path>) throws java.io.IOException` |  |
| `static java.nio.file.Path write(java.nio.file.Path, byte[], java.nio.file.OpenOption...) throws java.io.IOException` |  |
| `static java.nio.file.Path write(java.nio.file.Path, Iterable<? extends java.lang.CharSequence>, java.nio.charset.Charset, java.nio.file.OpenOption...) throws java.io.IOException` |  |
| `static java.nio.file.Path write(java.nio.file.Path, Iterable<? extends java.lang.CharSequence>, java.nio.file.OpenOption...) throws java.io.IOException` |  |

---

### `class InvalidPathException`

- **Extends:** `java.lang.IllegalArgumentException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidPathException(String, String, int)` |  |
| `InvalidPathException(String, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getIndex()` |  |
| `String getInput()` |  |
| `String getReason()` |  |

---

### `enum LinkOption`

- **Implements:** `java.nio.file.CopyOption java.nio.file.OpenOption`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.nio.file.LinkOption NOFOLLOW_LINKS` |  |

---

### `class final LinkPermission`

- **Extends:** `java.security.BasicPermission`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LinkPermission(String)` |  |
| `LinkPermission(String, String)` |  |

---

### `class NoSuchFileException`

- **Extends:** `java.nio.file.FileSystemException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NoSuchFileException(String)` |  |
| `NoSuchFileException(String, String, String)` |  |

---

### `class NotDirectoryException`

- **Extends:** `java.nio.file.FileSystemException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NotDirectoryException(String)` |  |

---

### `class NotLinkException`

- **Extends:** `java.nio.file.FileSystemException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NotLinkException(String)` |  |
| `NotLinkException(String, String, String)` |  |

---

### `interface OpenOption`


---

### `interface Path`

- **Extends:** `java.lang.Comparable<java.nio.file.Path> java.lang.Iterable<java.nio.file.Path> java.nio.file.Watchable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compareTo(java.nio.file.Path)` |  |
| `boolean endsWith(java.nio.file.Path)` |  |
| `boolean endsWith(String)` |  |
| `boolean equals(Object)` |  |
| `java.nio.file.Path getFileName()` |  |
| `java.nio.file.FileSystem getFileSystem()` |  |
| `java.nio.file.Path getName(int)` |  |
| `int getNameCount()` |  |
| `java.nio.file.Path getParent()` |  |
| `java.nio.file.Path getRoot()` |  |
| `int hashCode()` |  |
| `boolean isAbsolute()` |  |
| `java.util.Iterator<java.nio.file.Path> iterator()` |  |
| `java.nio.file.Path normalize()` |  |
| `java.nio.file.Path relativize(java.nio.file.Path)` |  |
| `java.nio.file.Path resolve(java.nio.file.Path)` |  |
| `java.nio.file.Path resolve(String)` |  |
| `java.nio.file.Path resolveSibling(java.nio.file.Path)` |  |
| `java.nio.file.Path resolveSibling(String)` |  |
| `boolean startsWith(java.nio.file.Path)` |  |
| `boolean startsWith(String)` |  |
| `java.nio.file.Path subpath(int, int)` |  |
| `java.nio.file.Path toAbsolutePath()` |  |
| `java.io.File toFile()` |  |
| `java.nio.file.Path toRealPath(java.nio.file.LinkOption...) throws java.io.IOException` |  |
| `String toString()` |  |
| `java.net.URI toUri()` |  |

---

### `interface PathMatcher`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean matches(java.nio.file.Path)` |  |

---

### `class final Paths`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.file.Path get(String, java.lang.String...)` |  |
| `static java.nio.file.Path get(java.net.URI)` |  |

---

### `class ProviderMismatchException`

- **Extends:** `java.lang.IllegalArgumentException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ProviderMismatchException()` |  |
| `ProviderMismatchException(String)` |  |

---

### `class ProviderNotFoundException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ProviderNotFoundException()` |  |
| `ProviderNotFoundException(String)` |  |

---

### `class ReadOnlyFileSystemException`

- **Extends:** `java.lang.UnsupportedOperationException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ReadOnlyFileSystemException()` |  |

---

### `interface SecureDirectoryStream<T>`

- **Extends:** `java.nio.file.DirectoryStream<T>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void deleteDirectory(T) throws java.io.IOException` |  |
| `void deleteFile(T) throws java.io.IOException` |  |
| `<V extends java.nio.file.attribute.FileAttributeView> V getFileAttributeView(Class<V>)` |  |
| `<V extends java.nio.file.attribute.FileAttributeView> V getFileAttributeView(T, Class<V>, java.nio.file.LinkOption...)` |  |
| `void move(T, java.nio.file.SecureDirectoryStream<T>, T) throws java.io.IOException` |  |
| `java.nio.channels.SeekableByteChannel newByteChannel(T, java.util.Set<? extends java.nio.file.OpenOption>, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `java.nio.file.SecureDirectoryStream<T> newDirectoryStream(T, java.nio.file.LinkOption...) throws java.io.IOException` |  |

---

### `class SimpleFileVisitor<T>`

- **Implements:** `java.nio.file.FileVisitor<T>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SimpleFileVisitor()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.file.FileVisitResult postVisitDirectory(T, java.io.IOException) throws java.io.IOException` |  |
| `java.nio.file.FileVisitResult preVisitDirectory(T, java.nio.file.attribute.BasicFileAttributes) throws java.io.IOException` |  |
| `java.nio.file.FileVisitResult visitFile(T, java.nio.file.attribute.BasicFileAttributes) throws java.io.IOException` |  |
| `java.nio.file.FileVisitResult visitFileFailed(T, java.io.IOException) throws java.io.IOException` |  |

---

### `enum StandardCopyOption`

- **Implements:** `java.nio.file.CopyOption`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.nio.file.StandardCopyOption ATOMIC_MOVE` |  |
| `static final java.nio.file.StandardCopyOption COPY_ATTRIBUTES` |  |
| `static final java.nio.file.StandardCopyOption REPLACE_EXISTING` |  |

---

### `enum StandardOpenOption`

- **Implements:** `java.nio.file.OpenOption`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.nio.file.StandardOpenOption APPEND` |  |
| `static final java.nio.file.StandardOpenOption CREATE` |  |
| `static final java.nio.file.StandardOpenOption CREATE_NEW` |  |
| `static final java.nio.file.StandardOpenOption DELETE_ON_CLOSE` |  |
| `static final java.nio.file.StandardOpenOption DSYNC` |  |
| `static final java.nio.file.StandardOpenOption READ` |  |
| `static final java.nio.file.StandardOpenOption SPARSE` |  |
| `static final java.nio.file.StandardOpenOption SYNC` |  |
| `static final java.nio.file.StandardOpenOption TRUNCATE_EXISTING` |  |
| `static final java.nio.file.StandardOpenOption WRITE` |  |

---

### `class final StandardWatchEventKinds`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.nio.file.WatchEvent.Kind<java.nio.file.Path> ENTRY_CREATE` |  |
| `static final java.nio.file.WatchEvent.Kind<java.nio.file.Path> ENTRY_DELETE` |  |
| `static final java.nio.file.WatchEvent.Kind<java.nio.file.Path> ENTRY_MODIFY` |  |
| `static final java.nio.file.WatchEvent.Kind<java.lang.Object> OVERFLOW` |  |

---

### `interface WatchEvent<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `T context()` |  |
| `int count()` |  |
| `java.nio.file.WatchEvent.Kind<T> kind()` |  |

---

### `interface static WatchEvent.Kind<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String name()` |  |
| `Class<T> type()` |  |

---

### `interface static WatchEvent.Modifier`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String name()` |  |

---

### `interface WatchKey`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void cancel()` |  |
| `boolean isValid()` |  |
| `java.util.List<java.nio.file.WatchEvent<?>> pollEvents()` |  |
| `boolean reset()` |  |
| `java.nio.file.Watchable watchable()` |  |

---

### `interface WatchService`

- **Extends:** `java.io.Closeable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.file.WatchKey poll()` |  |
| `java.nio.file.WatchKey poll(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `java.nio.file.WatchKey take() throws java.lang.InterruptedException` |  |

---

### `interface Watchable`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.file.WatchKey register(java.nio.file.WatchService, java.nio.file.WatchEvent.Kind<?>[], java.nio.file.WatchEvent.Modifier...) throws java.io.IOException` |  |
| `java.nio.file.WatchKey register(java.nio.file.WatchService, java.nio.file.WatchEvent.Kind<?>...) throws java.io.IOException` |  |

---

## Package: `java.nio.file.attribute`

### `class final AclEntry`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Set<java.nio.file.attribute.AclEntryFlag> flags()` |  |
| `static java.nio.file.attribute.AclEntry.Builder newBuilder()` |  |
| `static java.nio.file.attribute.AclEntry.Builder newBuilder(java.nio.file.attribute.AclEntry)` |  |
| `java.util.Set<java.nio.file.attribute.AclEntryPermission> permissions()` |  |
| `java.nio.file.attribute.UserPrincipal principal()` |  |
| `java.nio.file.attribute.AclEntryType type()` |  |

---

### `class static final AclEntry.Builder`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.file.attribute.AclEntry build()` |  |
| `java.nio.file.attribute.AclEntry.Builder setFlags(java.util.Set<java.nio.file.attribute.AclEntryFlag>)` |  |
| `java.nio.file.attribute.AclEntry.Builder setFlags(java.nio.file.attribute.AclEntryFlag...)` |  |
| `java.nio.file.attribute.AclEntry.Builder setPermissions(java.util.Set<java.nio.file.attribute.AclEntryPermission>)` |  |
| `java.nio.file.attribute.AclEntry.Builder setPermissions(java.nio.file.attribute.AclEntryPermission...)` |  |
| `java.nio.file.attribute.AclEntry.Builder setPrincipal(java.nio.file.attribute.UserPrincipal)` |  |
| `java.nio.file.attribute.AclEntry.Builder setType(java.nio.file.attribute.AclEntryType)` |  |

---

### `enum AclEntryFlag`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.nio.file.attribute.AclEntryFlag DIRECTORY_INHERIT` |  |
| `static final java.nio.file.attribute.AclEntryFlag FILE_INHERIT` |  |
| `static final java.nio.file.attribute.AclEntryFlag INHERIT_ONLY` |  |
| `static final java.nio.file.attribute.AclEntryFlag NO_PROPAGATE_INHERIT` |  |

---

### `enum AclEntryPermission`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.nio.file.attribute.AclEntryPermission APPEND_DATA` |  |
| `static final java.nio.file.attribute.AclEntryPermission DELETE` |  |
| `static final java.nio.file.attribute.AclEntryPermission DELETE_CHILD` |  |
| `static final java.nio.file.attribute.AclEntryPermission EXECUTE` |  |
| `static final java.nio.file.attribute.AclEntryPermission READ_ACL` |  |
| `static final java.nio.file.attribute.AclEntryPermission READ_ATTRIBUTES` |  |
| `static final java.nio.file.attribute.AclEntryPermission READ_DATA` |  |
| `static final java.nio.file.attribute.AclEntryPermission READ_NAMED_ATTRS` |  |
| `static final java.nio.file.attribute.AclEntryPermission SYNCHRONIZE` |  |
| `static final java.nio.file.attribute.AclEntryPermission WRITE_ACL` |  |
| `static final java.nio.file.attribute.AclEntryPermission WRITE_ATTRIBUTES` |  |
| `static final java.nio.file.attribute.AclEntryPermission WRITE_DATA` |  |
| `static final java.nio.file.attribute.AclEntryPermission WRITE_NAMED_ATTRS` |  |
| `static final java.nio.file.attribute.AclEntryPermission WRITE_OWNER` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.nio.file.attribute.AclEntryPermission ADD_FILE` |  |
| `static final java.nio.file.attribute.AclEntryPermission ADD_SUBDIRECTORY` |  |
| `static final java.nio.file.attribute.AclEntryPermission LIST_DIRECTORY` |  |

---

### `enum AclEntryType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.nio.file.attribute.AclEntryType ALARM` |  |
| `static final java.nio.file.attribute.AclEntryType ALLOW` |  |
| `static final java.nio.file.attribute.AclEntryType AUDIT` |  |
| `static final java.nio.file.attribute.AclEntryType DENY` |  |

---

### `interface AclFileAttributeView`

- **Extends:** `java.nio.file.attribute.FileOwnerAttributeView`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.List<java.nio.file.attribute.AclEntry> getAcl() throws java.io.IOException` |  |
| `void setAcl(java.util.List<java.nio.file.attribute.AclEntry>) throws java.io.IOException` |  |

---

### `interface AttributeView`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String name()` |  |

---

### `interface BasicFileAttributeView`

- **Extends:** `java.nio.file.attribute.FileAttributeView`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.file.attribute.BasicFileAttributes readAttributes() throws java.io.IOException` |  |
| `void setTimes(java.nio.file.attribute.FileTime, java.nio.file.attribute.FileTime, java.nio.file.attribute.FileTime) throws java.io.IOException` |  |

---

### `interface BasicFileAttributes`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.file.attribute.FileTime creationTime()` |  |
| `Object fileKey()` |  |
| `boolean isDirectory()` |  |
| `boolean isOther()` |  |
| `boolean isRegularFile()` |  |
| `boolean isSymbolicLink()` |  |
| `java.nio.file.attribute.FileTime lastAccessTime()` |  |
| `java.nio.file.attribute.FileTime lastModifiedTime()` |  |
| `long size()` |  |

---

### `interface DosFileAttributeView`

- **Extends:** `java.nio.file.attribute.BasicFileAttributeView`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.file.attribute.DosFileAttributes readAttributes() throws java.io.IOException` |  |
| `void setArchive(boolean) throws java.io.IOException` |  |
| `void setHidden(boolean) throws java.io.IOException` |  |
| `void setReadOnly(boolean) throws java.io.IOException` |  |
| `void setSystem(boolean) throws java.io.IOException` |  |

---

### `interface DosFileAttributes`

- **Extends:** `java.nio.file.attribute.BasicFileAttributes`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isArchive()` |  |
| `boolean isHidden()` |  |
| `boolean isReadOnly()` |  |
| `boolean isSystem()` |  |

---

### `interface FileAttribute<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String name()` |  |
| `T value()` |  |

---

### `interface FileAttributeView`

- **Extends:** `java.nio.file.attribute.AttributeView`

---

### `interface FileOwnerAttributeView`

- **Extends:** `java.nio.file.attribute.FileAttributeView`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.file.attribute.UserPrincipal getOwner() throws java.io.IOException` |  |
| `void setOwner(java.nio.file.attribute.UserPrincipal) throws java.io.IOException` |  |

---

### `interface FileStoreAttributeView`

- **Extends:** `java.nio.file.attribute.AttributeView`

---

### `class final FileTime`

- **Implements:** `java.lang.Comparable<java.nio.file.attribute.FileTime>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compareTo(java.nio.file.attribute.FileTime)` |  |
| `static java.nio.file.attribute.FileTime from(long, java.util.concurrent.TimeUnit)` |  |
| `static java.nio.file.attribute.FileTime from(java.time.Instant)` |  |
| `static java.nio.file.attribute.FileTime fromMillis(long)` |  |
| `long to(java.util.concurrent.TimeUnit)` |  |
| `java.time.Instant toInstant()` |  |
| `long toMillis()` |  |

---

### `interface GroupPrincipal`

- **Extends:** `java.nio.file.attribute.UserPrincipal`

---

### `interface PosixFileAttributeView`

- **Extends:** `java.nio.file.attribute.BasicFileAttributeView java.nio.file.attribute.FileOwnerAttributeView`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.file.attribute.PosixFileAttributes readAttributes() throws java.io.IOException` |  |
| `void setGroup(java.nio.file.attribute.GroupPrincipal) throws java.io.IOException` |  |
| `void setPermissions(java.util.Set<java.nio.file.attribute.PosixFilePermission>) throws java.io.IOException` |  |

---

### `interface PosixFileAttributes`

- **Extends:** `java.nio.file.attribute.BasicFileAttributes`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.nio.file.attribute.GroupPrincipal group()` |  |
| `java.nio.file.attribute.UserPrincipal owner()` |  |
| `java.util.Set<java.nio.file.attribute.PosixFilePermission> permissions()` |  |

---

### `enum PosixFilePermission`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.nio.file.attribute.PosixFilePermission GROUP_EXECUTE` |  |
| `static final java.nio.file.attribute.PosixFilePermission GROUP_READ` |  |
| `static final java.nio.file.attribute.PosixFilePermission GROUP_WRITE` |  |
| `static final java.nio.file.attribute.PosixFilePermission OTHERS_EXECUTE` |  |
| `static final java.nio.file.attribute.PosixFilePermission OTHERS_READ` |  |
| `static final java.nio.file.attribute.PosixFilePermission OTHERS_WRITE` |  |
| `static final java.nio.file.attribute.PosixFilePermission OWNER_EXECUTE` |  |
| `static final java.nio.file.attribute.PosixFilePermission OWNER_READ` |  |
| `static final java.nio.file.attribute.PosixFilePermission OWNER_WRITE` |  |

---

### `class final PosixFilePermissions`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.nio.file.attribute.FileAttribute<java.util.Set<java.nio.file.attribute.PosixFilePermission>> asFileAttribute(java.util.Set<java.nio.file.attribute.PosixFilePermission>)` |  |
| `static java.util.Set<java.nio.file.attribute.PosixFilePermission> fromString(String)` |  |
| `static String toString(java.util.Set<java.nio.file.attribute.PosixFilePermission>)` |  |

---

### `interface UserDefinedFileAttributeView`

- **Extends:** `java.nio.file.attribute.FileAttributeView`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void delete(String) throws java.io.IOException` |  |
| `java.util.List<java.lang.String> list() throws java.io.IOException` |  |
| `int read(String, java.nio.ByteBuffer) throws java.io.IOException` |  |
| `int size(String) throws java.io.IOException` |  |
| `int write(String, java.nio.ByteBuffer) throws java.io.IOException` |  |

---

### `interface UserPrincipal`

- **Extends:** `java.security.Principal`

---

### `class abstract UserPrincipalLookupService`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UserPrincipalLookupService()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.nio.file.attribute.GroupPrincipal lookupPrincipalByGroupName(String) throws java.io.IOException` |  |
| `abstract java.nio.file.attribute.UserPrincipal lookupPrincipalByName(String) throws java.io.IOException` |  |

---

### `class UserPrincipalNotFoundException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UserPrincipalNotFoundException(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getName()` |  |

---

## Package: `java.nio.file.spi`

### `class abstract FileSystemProvider`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileSystemProvider()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void checkAccess(java.nio.file.Path, java.nio.file.AccessMode...) throws java.io.IOException` |  |
| `abstract void copy(java.nio.file.Path, java.nio.file.Path, java.nio.file.CopyOption...) throws java.io.IOException` |  |
| `abstract void createDirectory(java.nio.file.Path, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `void createLink(java.nio.file.Path, java.nio.file.Path) throws java.io.IOException` |  |
| `void createSymbolicLink(java.nio.file.Path, java.nio.file.Path, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `abstract void delete(java.nio.file.Path) throws java.io.IOException` |  |
| `boolean deleteIfExists(java.nio.file.Path) throws java.io.IOException` |  |
| `abstract <V extends java.nio.file.attribute.FileAttributeView> V getFileAttributeView(java.nio.file.Path, Class<V>, java.nio.file.LinkOption...)` |  |
| `abstract java.nio.file.FileStore getFileStore(java.nio.file.Path) throws java.io.IOException` |  |
| `abstract java.nio.file.FileSystem getFileSystem(java.net.URI)` |  |
| `abstract java.nio.file.Path getPath(java.net.URI)` |  |
| `abstract String getScheme()` |  |
| `static java.util.List<java.nio.file.spi.FileSystemProvider> installedProviders()` |  |
| `abstract boolean isHidden(java.nio.file.Path) throws java.io.IOException` |  |
| `abstract boolean isSameFile(java.nio.file.Path, java.nio.file.Path) throws java.io.IOException` |  |
| `abstract void move(java.nio.file.Path, java.nio.file.Path, java.nio.file.CopyOption...) throws java.io.IOException` |  |
| `java.nio.channels.AsynchronousFileChannel newAsynchronousFileChannel(java.nio.file.Path, java.util.Set<? extends java.nio.file.OpenOption>, java.util.concurrent.ExecutorService, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `abstract java.nio.channels.SeekableByteChannel newByteChannel(java.nio.file.Path, java.util.Set<? extends java.nio.file.OpenOption>, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `abstract java.nio.file.DirectoryStream<java.nio.file.Path> newDirectoryStream(java.nio.file.Path, java.nio.file.DirectoryStream.Filter<? super java.nio.file.Path>) throws java.io.IOException` |  |
| `java.nio.channels.FileChannel newFileChannel(java.nio.file.Path, java.util.Set<? extends java.nio.file.OpenOption>, java.nio.file.attribute.FileAttribute<?>...) throws java.io.IOException` |  |
| `abstract java.nio.file.FileSystem newFileSystem(java.net.URI, java.util.Map<java.lang.String,?>) throws java.io.IOException` |  |
| `java.nio.file.FileSystem newFileSystem(java.nio.file.Path, java.util.Map<java.lang.String,?>) throws java.io.IOException` |  |
| `java.io.InputStream newInputStream(java.nio.file.Path, java.nio.file.OpenOption...) throws java.io.IOException` |  |
| `java.io.OutputStream newOutputStream(java.nio.file.Path, java.nio.file.OpenOption...) throws java.io.IOException` |  |
| `abstract <A extends java.nio.file.attribute.BasicFileAttributes> A readAttributes(java.nio.file.Path, Class<A>, java.nio.file.LinkOption...) throws java.io.IOException` |  |
| `abstract java.util.Map<java.lang.String,java.lang.Object> readAttributes(java.nio.file.Path, String, java.nio.file.LinkOption...) throws java.io.IOException` |  |
| `java.nio.file.Path readSymbolicLink(java.nio.file.Path) throws java.io.IOException` |  |
| `abstract void setAttribute(java.nio.file.Path, String, Object, java.nio.file.LinkOption...) throws java.io.IOException` |  |

---

### `class abstract FileTypeDetector`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileTypeDetector()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract String probeContentType(java.nio.file.Path) throws java.io.IOException` |  |

---

## Package: `java.security`

### `class final AccessControlContext`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessControlContext(java.security.ProtectionDomain[])` |  |
| `AccessControlContext(java.security.AccessControlContext, java.security.DomainCombiner)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void checkPermission(java.security.Permission) throws java.security.AccessControlException` |  |
| `java.security.DomainCombiner getDomainCombiner()` |  |

---

### `class AccessControlException`

- **Extends:** `java.lang.SecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessControlException(String)` |  |
| `AccessControlException(String, java.security.Permission)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.Permission getPermission()` |  |

---

### `class final AccessController`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static void checkPermission(java.security.Permission) throws java.security.AccessControlException` |  |
| `static <T> T doPrivileged(java.security.PrivilegedAction<T>)` |  |
| `static <T> T doPrivileged(java.security.PrivilegedAction<T>, java.security.AccessControlContext)` |  |
| `static <T> T doPrivileged(java.security.PrivilegedExceptionAction<T>) throws java.security.PrivilegedActionException` |  |
| `static <T> T doPrivileged(java.security.PrivilegedExceptionAction<T>, java.security.AccessControlContext) throws java.security.PrivilegedActionException` |  |
| `static <T> T doPrivilegedWithCombiner(java.security.PrivilegedAction<T>)` |  |
| `static <T> T doPrivilegedWithCombiner(java.security.PrivilegedExceptionAction<T>) throws java.security.PrivilegedActionException` |  |
| `static java.security.AccessControlContext getContext()` |  |

---

### `interface AlgorithmConstraints`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean permits(java.util.Set<java.security.CryptoPrimitive>, String, java.security.AlgorithmParameters)` |  |
| `boolean permits(java.util.Set<java.security.CryptoPrimitive>, java.security.Key)` |  |
| `boolean permits(java.util.Set<java.security.CryptoPrimitive>, String, java.security.Key, java.security.AlgorithmParameters)` |  |

---

### `class AlgorithmParameterGenerator`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AlgorithmParameterGenerator(java.security.AlgorithmParameterGeneratorSpi, java.security.Provider, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.security.AlgorithmParameters generateParameters()` |  |
| `final String getAlgorithm()` |  |
| `static java.security.AlgorithmParameterGenerator getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static java.security.AlgorithmParameterGenerator getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static java.security.AlgorithmParameterGenerator getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final java.security.Provider getProvider()` |  |
| `final void init(int)` |  |
| `final void init(int, java.security.SecureRandom)` |  |
| `final void init(java.security.spec.AlgorithmParameterSpec) throws java.security.InvalidAlgorithmParameterException` |  |
| `final void init(java.security.spec.AlgorithmParameterSpec, java.security.SecureRandom) throws java.security.InvalidAlgorithmParameterException` |  |

---

### `class abstract AlgorithmParameterGeneratorSpi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AlgorithmParameterGeneratorSpi()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.security.AlgorithmParameters engineGenerateParameters()` |  |
| `abstract void engineInit(int, java.security.SecureRandom)` |  |
| `abstract void engineInit(java.security.spec.AlgorithmParameterSpec, java.security.SecureRandom) throws java.security.InvalidAlgorithmParameterException` |  |

---

### `class AlgorithmParameters`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AlgorithmParameters(java.security.AlgorithmParametersSpi, java.security.Provider, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final String getAlgorithm()` |  |
| `final byte[] getEncoded() throws java.io.IOException` |  |
| `final byte[] getEncoded(String) throws java.io.IOException` |  |
| `static java.security.AlgorithmParameters getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static java.security.AlgorithmParameters getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static java.security.AlgorithmParameters getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final <T extends java.security.spec.AlgorithmParameterSpec> T getParameterSpec(Class<T>) throws java.security.spec.InvalidParameterSpecException` |  |
| `final java.security.Provider getProvider()` |  |
| `final void init(java.security.spec.AlgorithmParameterSpec) throws java.security.spec.InvalidParameterSpecException` |  |
| `final void init(byte[]) throws java.io.IOException` |  |
| `final void init(byte[], String) throws java.io.IOException` |  |
| `final String toString()` |  |

---

### `class abstract AlgorithmParametersSpi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AlgorithmParametersSpi()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract byte[] engineGetEncoded() throws java.io.IOException` |  |
| `abstract byte[] engineGetEncoded(String) throws java.io.IOException` |  |
| `abstract <T extends java.security.spec.AlgorithmParameterSpec> T engineGetParameterSpec(Class<T>) throws java.security.spec.InvalidParameterSpecException` |  |
| `abstract void engineInit(java.security.spec.AlgorithmParameterSpec) throws java.security.spec.InvalidParameterSpecException` |  |
| `abstract void engineInit(byte[]) throws java.io.IOException` |  |
| `abstract void engineInit(byte[], String) throws java.io.IOException` |  |
| `abstract String engineToString()` |  |

---

### `class final AllPermission`

- **Extends:** `java.security.Permission`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AllPermission()` |  |
| `AllPermission(String, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getActions()` |  |
| `boolean implies(java.security.Permission)` |  |

---

### `class abstract AuthProvider`

- **Extends:** `java.security.Provider`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AuthProvider(String, double, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void login(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler) throws javax.security.auth.login.LoginException` |  |
| `abstract void logout() throws javax.security.auth.login.LoginException` |  |
| `abstract void setCallbackHandler(javax.security.auth.callback.CallbackHandler)` |  |

---

### `class abstract BasicPermission`

- **Extends:** `java.security.Permission`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BasicPermission(String)` |  |
| `BasicPermission(String, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getActions()` |  |
| `boolean implies(java.security.Permission)` |  |

---

### `interface Certificate` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class final CodeSigner`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CodeSigner(java.security.cert.CertPath, java.security.Timestamp)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.cert.CertPath getSignerCertPath()` |  |
| `java.security.Timestamp getTimestamp()` |  |

---

### `class CodeSource`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CodeSource(java.net.URL, java.security.cert.Certificate[])` |  |
| `CodeSource(java.net.URL, java.security.CodeSigner[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.security.cert.Certificate[] getCertificates()` |  |
| `final java.security.CodeSigner[] getCodeSigners()` |  |
| `final java.net.URL getLocation()` |  |
| `boolean implies(java.security.CodeSource)` |  |

---

### `enum CryptoPrimitive`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.security.CryptoPrimitive BLOCK_CIPHER` |  |
| `static final java.security.CryptoPrimitive KEY_AGREEMENT` |  |
| `static final java.security.CryptoPrimitive KEY_ENCAPSULATION` |  |
| `static final java.security.CryptoPrimitive KEY_WRAP` |  |
| `static final java.security.CryptoPrimitive MAC` |  |
| `static final java.security.CryptoPrimitive MESSAGE_DIGEST` |  |
| `static final java.security.CryptoPrimitive PUBLIC_KEY_ENCRYPTION` |  |
| `static final java.security.CryptoPrimitive SECURE_RANDOM` |  |
| `static final java.security.CryptoPrimitive SIGNATURE` |  |
| `static final java.security.CryptoPrimitive STREAM_CIPHER` |  |

---

### `class DigestException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DigestException()` |  |
| `DigestException(String)` |  |
| `DigestException(String, Throwable)` |  |
| `DigestException(Throwable)` |  |

---

### `class DigestInputStream`

- **Extends:** `java.io.FilterInputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DigestInputStream(java.io.InputStream, java.security.MessageDigest)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.MessageDigest digest` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.MessageDigest getMessageDigest()` |  |
| `void on(boolean)` |  |
| `void setMessageDigest(java.security.MessageDigest)` |  |

---

### `class DigestOutputStream`

- **Extends:** `java.io.FilterOutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DigestOutputStream(java.io.OutputStream, java.security.MessageDigest)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.MessageDigest digest` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.MessageDigest getMessageDigest()` |  |
| `void on(boolean)` |  |
| `void setMessageDigest(java.security.MessageDigest)` |  |

---

### `interface DomainCombiner`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.ProtectionDomain[] combine(java.security.ProtectionDomain[], java.security.ProtectionDomain[])` |  |

---

### `class final DomainLoadStoreParameter`

- **Implements:** `java.security.KeyStore.LoadStoreParameter`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DomainLoadStoreParameter(java.net.URI, java.util.Map<java.lang.String,java.security.KeyStore.ProtectionParameter>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.net.URI getConfiguration()` |  |
| `java.security.KeyStore.ProtectionParameter getProtectionParameter()` |  |
| `java.util.Map<java.lang.String,java.security.KeyStore.ProtectionParameter> getProtectionParams()` |  |

---

### `class GeneralSecurityException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GeneralSecurityException()` |  |
| `GeneralSecurityException(String)` |  |
| `GeneralSecurityException(String, Throwable)` |  |
| `GeneralSecurityException(Throwable)` |  |

---

### `interface Guard`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void checkGuard(Object) throws java.lang.SecurityException` |  |

---

### `class GuardedObject`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GuardedObject(Object, java.security.Guard)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object getObject() throws java.lang.SecurityException` |  |

---

### `class abstract Identity` ~~DEPRECATED~~

- **Implements:** `java.security.Principal java.io.Serializable`
- **Annotations:** `@Deprecated`

---

### `class abstract IdentityScope` ~~DEPRECATED~~

- **Extends:** `java.security.Identity`
- **Annotations:** `@Deprecated`

---

### `class InvalidAlgorithmParameterException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidAlgorithmParameterException()` |  |
| `InvalidAlgorithmParameterException(String)` |  |
| `InvalidAlgorithmParameterException(String, Throwable)` |  |
| `InvalidAlgorithmParameterException(Throwable)` |  |

---

### `class InvalidKeyException`

- **Extends:** `java.security.KeyException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidKeyException()` |  |
| `InvalidKeyException(String)` |  |
| `InvalidKeyException(String, Throwable)` |  |
| `InvalidKeyException(Throwable)` |  |

---

### `class InvalidParameterException`

- **Extends:** `java.lang.IllegalArgumentException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidParameterException()` |  |
| `InvalidParameterException(String)` |  |

---

### `interface Key`

- **Extends:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = 6603384152749567654L` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getAlgorithm()` |  |
| `byte[] getEncoded()` |  |
| `String getFormat()` |  |

---

### `class KeyException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyException()` |  |
| `KeyException(String)` |  |
| `KeyException(String, Throwable)` |  |
| `KeyException(Throwable)` |  |

---

### `class KeyFactory`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyFactory(java.security.KeyFactorySpi, java.security.Provider, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.security.PrivateKey generatePrivate(java.security.spec.KeySpec) throws java.security.spec.InvalidKeySpecException` |  |
| `final java.security.PublicKey generatePublic(java.security.spec.KeySpec) throws java.security.spec.InvalidKeySpecException` |  |
| `final String getAlgorithm()` |  |
| `static java.security.KeyFactory getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static java.security.KeyFactory getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static java.security.KeyFactory getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final <T extends java.security.spec.KeySpec> T getKeySpec(java.security.Key, Class<T>) throws java.security.spec.InvalidKeySpecException` |  |
| `final java.security.Provider getProvider()` |  |
| `final java.security.Key translateKey(java.security.Key) throws java.security.InvalidKeyException` |  |

---

### `class abstract KeyFactorySpi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyFactorySpi()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.security.PrivateKey engineGeneratePrivate(java.security.spec.KeySpec) throws java.security.spec.InvalidKeySpecException` |  |
| `abstract java.security.PublicKey engineGeneratePublic(java.security.spec.KeySpec) throws java.security.spec.InvalidKeySpecException` |  |
| `abstract <T extends java.security.spec.KeySpec> T engineGetKeySpec(java.security.Key, Class<T>) throws java.security.spec.InvalidKeySpecException` |  |
| `abstract java.security.Key engineTranslateKey(java.security.Key) throws java.security.InvalidKeyException` |  |

---

### `class KeyManagementException`

- **Extends:** `java.security.KeyException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyManagementException()` |  |
| `KeyManagementException(String)` |  |
| `KeyManagementException(String, Throwable)` |  |
| `KeyManagementException(Throwable)` |  |

---

### `class final KeyPair`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyPair(java.security.PublicKey, java.security.PrivateKey)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.PrivateKey getPrivate()` |  |
| `java.security.PublicKey getPublic()` |  |

---

### `class abstract KeyPairGenerator`

- **Extends:** `java.security.KeyPairGeneratorSpi`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyPairGenerator(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.security.KeyPair genKeyPair()` |  |
| `java.security.KeyPair generateKeyPair()` |  |
| `String getAlgorithm()` |  |
| `static java.security.KeyPairGenerator getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static java.security.KeyPairGenerator getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static java.security.KeyPairGenerator getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final java.security.Provider getProvider()` |  |
| `void initialize(int)` |  |
| `void initialize(int, java.security.SecureRandom)` |  |
| `void initialize(java.security.spec.AlgorithmParameterSpec) throws java.security.InvalidAlgorithmParameterException` |  |

---

### `class abstract KeyPairGeneratorSpi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyPairGeneratorSpi()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.security.KeyPair generateKeyPair()` |  |
| `abstract void initialize(int, java.security.SecureRandom)` |  |
| `void initialize(java.security.spec.AlgorithmParameterSpec, java.security.SecureRandom) throws java.security.InvalidAlgorithmParameterException` |  |

---

### `class KeyRep`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyRep(java.security.KeyRep.Type, String, String, byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object readResolve() throws java.io.ObjectStreamException` |  |

---

### `enum KeyRep.Type`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.security.KeyRep.Type PRIVATE` |  |
| `static final java.security.KeyRep.Type PUBLIC` |  |
| `static final java.security.KeyRep.Type SECRET` |  |

---

### `class KeyStore`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyStore(java.security.KeyStoreSpi, java.security.Provider, String)` |  |
| `KeyStore.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.util.Enumeration<java.lang.String> aliases() throws java.security.KeyStoreException` |  |
| `final boolean containsAlias(String) throws java.security.KeyStoreException` |  |
| `final void deleteEntry(String) throws java.security.KeyStoreException` |  |
| `final boolean entryInstanceOf(String, Class<? extends java.security.KeyStore.Entry>) throws java.security.KeyStoreException` |  |
| `final java.security.cert.Certificate getCertificate(String) throws java.security.KeyStoreException` |  |
| `final String getCertificateAlias(java.security.cert.Certificate) throws java.security.KeyStoreException` |  |
| `final java.security.cert.Certificate[] getCertificateChain(String) throws java.security.KeyStoreException` |  |
| `final java.util.Date getCreationDate(String) throws java.security.KeyStoreException` |  |
| `static final String getDefaultType()` |  |
| `final java.security.KeyStore.Entry getEntry(String, java.security.KeyStore.ProtectionParameter) throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.UnrecoverableEntryException` |  |
| `static java.security.KeyStore getInstance(String) throws java.security.KeyStoreException` |  |
| `static java.security.KeyStore getInstance(String, String) throws java.security.KeyStoreException, java.security.NoSuchProviderException` |  |
| `static java.security.KeyStore getInstance(String, java.security.Provider) throws java.security.KeyStoreException` |  |
| `final java.security.Key getKey(String, char[]) throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.UnrecoverableKeyException` |  |
| `final java.security.Provider getProvider()` |  |
| `final String getType()` |  |
| `final boolean isCertificateEntry(String) throws java.security.KeyStoreException` |  |
| `final boolean isKeyEntry(String) throws java.security.KeyStoreException` |  |
| `final void load(java.io.InputStream, char[]) throws java.security.cert.CertificateException, java.io.IOException, java.security.NoSuchAlgorithmException` |  |
| `final void load(java.security.KeyStore.LoadStoreParameter) throws java.security.cert.CertificateException, java.io.IOException, java.security.NoSuchAlgorithmException` |  |
| `final void setCertificateEntry(String, java.security.cert.Certificate) throws java.security.KeyStoreException` |  |
| `final void setEntry(String, java.security.KeyStore.Entry, java.security.KeyStore.ProtectionParameter) throws java.security.KeyStoreException` |  |
| `final void setKeyEntry(String, java.security.Key, char[], java.security.cert.Certificate[]) throws java.security.KeyStoreException` |  |
| `final void setKeyEntry(String, byte[], java.security.cert.Certificate[]) throws java.security.KeyStoreException` |  |
| `final int size() throws java.security.KeyStoreException` |  |
| `final void store(java.io.OutputStream, char[]) throws java.security.cert.CertificateException, java.io.IOException, java.security.KeyStoreException, java.security.NoSuchAlgorithmException` |  |
| `final void store(java.security.KeyStore.LoadStoreParameter) throws java.security.cert.CertificateException, java.io.IOException, java.security.KeyStoreException, java.security.NoSuchAlgorithmException` |  |
| `abstract java.security.KeyStore getKeyStore() throws java.security.KeyStoreException` |  |
| `abstract java.security.KeyStore.ProtectionParameter getProtectionParameter(String) throws java.security.KeyStoreException` |  |
| `static java.security.KeyStore.Builder newInstance(java.security.KeyStore, java.security.KeyStore.ProtectionParameter)` |  |
| `static java.security.KeyStore.Builder newInstance(String, java.security.Provider, java.io.File, java.security.KeyStore.ProtectionParameter)` |  |
| `static java.security.KeyStore.Builder newInstance(String, java.security.Provider, java.security.KeyStore.ProtectionParameter)` |  |

---

### `class static KeyStore.CallbackHandlerProtection`

- **Implements:** `java.security.KeyStore.ProtectionParameter`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyStore.CallbackHandlerProtection(javax.security.auth.callback.CallbackHandler)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `javax.security.auth.callback.CallbackHandler getCallbackHandler()` |  |

---

### `interface static KeyStore.Entry`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.Set<java.security.KeyStore.Entry.Attribute> getAttributes()` |  |

---

### `interface static KeyStore.Entry.Attribute`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getName()` |  |
| `String getValue()` |  |

---

### `interface static KeyStore.LoadStoreParameter`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.KeyStore.ProtectionParameter getProtectionParameter()` |  |

---

### `class static KeyStore.PasswordProtection`

- **Implements:** `javax.security.auth.Destroyable java.security.KeyStore.ProtectionParameter`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyStore.PasswordProtection(char[])` |  |
| `KeyStore.PasswordProtection(char[], String, java.security.spec.AlgorithmParameterSpec)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `char[] getPassword()` |  |
| `String getProtectionAlgorithm()` |  |
| `java.security.spec.AlgorithmParameterSpec getProtectionParameters()` |  |

---

### `class static final KeyStore.PrivateKeyEntry`

- **Implements:** `java.security.KeyStore.Entry`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyStore.PrivateKeyEntry(java.security.PrivateKey, java.security.cert.Certificate[])` |  |
| `KeyStore.PrivateKeyEntry(java.security.PrivateKey, java.security.cert.Certificate[], java.util.Set<java.security.KeyStore.Entry.Attribute>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.cert.Certificate getCertificate()` |  |
| `java.security.cert.Certificate[] getCertificateChain()` |  |
| `java.security.PrivateKey getPrivateKey()` |  |

---

### `interface static KeyStore.ProtectionParameter`


---

### `class static final KeyStore.SecretKeyEntry`

- **Implements:** `java.security.KeyStore.Entry`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyStore.SecretKeyEntry(javax.crypto.SecretKey)` |  |
| `KeyStore.SecretKeyEntry(javax.crypto.SecretKey, java.util.Set<java.security.KeyStore.Entry.Attribute>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `javax.crypto.SecretKey getSecretKey()` |  |

---

### `class static final KeyStore.TrustedCertificateEntry`

- **Implements:** `java.security.KeyStore.Entry`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyStore.TrustedCertificateEntry(java.security.cert.Certificate)` |  |
| `KeyStore.TrustedCertificateEntry(java.security.cert.Certificate, java.util.Set<java.security.KeyStore.Entry.Attribute>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.cert.Certificate getTrustedCertificate()` |  |

---

### `class KeyStoreException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyStoreException()` |  |
| `KeyStoreException(String)` |  |
| `KeyStoreException(String, Throwable)` |  |
| `KeyStoreException(Throwable)` |  |

---

### `class abstract KeyStoreSpi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyStoreSpi()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.util.Enumeration<java.lang.String> engineAliases()` |  |
| `abstract boolean engineContainsAlias(String)` |  |
| `abstract void engineDeleteEntry(String) throws java.security.KeyStoreException` |  |
| `boolean engineEntryInstanceOf(String, Class<? extends java.security.KeyStore.Entry>)` |  |
| `abstract java.security.cert.Certificate engineGetCertificate(String)` |  |
| `abstract String engineGetCertificateAlias(java.security.cert.Certificate)` |  |
| `abstract java.security.cert.Certificate[] engineGetCertificateChain(String)` |  |
| `abstract java.util.Date engineGetCreationDate(String)` |  |
| `java.security.KeyStore.Entry engineGetEntry(String, java.security.KeyStore.ProtectionParameter) throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.UnrecoverableEntryException` |  |
| `abstract java.security.Key engineGetKey(String, char[]) throws java.security.NoSuchAlgorithmException, java.security.UnrecoverableKeyException` |  |
| `abstract boolean engineIsCertificateEntry(String)` |  |
| `abstract boolean engineIsKeyEntry(String)` |  |
| `abstract void engineLoad(java.io.InputStream, char[]) throws java.security.cert.CertificateException, java.io.IOException, java.security.NoSuchAlgorithmException` |  |
| `void engineLoad(java.security.KeyStore.LoadStoreParameter) throws java.security.cert.CertificateException, java.io.IOException, java.security.NoSuchAlgorithmException` |  |
| `abstract void engineSetCertificateEntry(String, java.security.cert.Certificate) throws java.security.KeyStoreException` |  |
| `void engineSetEntry(String, java.security.KeyStore.Entry, java.security.KeyStore.ProtectionParameter) throws java.security.KeyStoreException` |  |
| `abstract void engineSetKeyEntry(String, java.security.Key, char[], java.security.cert.Certificate[]) throws java.security.KeyStoreException` |  |
| `abstract void engineSetKeyEntry(String, byte[], java.security.cert.Certificate[]) throws java.security.KeyStoreException` |  |
| `abstract int engineSize()` |  |
| `abstract void engineStore(java.io.OutputStream, char[]) throws java.security.cert.CertificateException, java.io.IOException, java.security.NoSuchAlgorithmException` |  |
| `void engineStore(java.security.KeyStore.LoadStoreParameter) throws java.security.cert.CertificateException, java.io.IOException, java.security.NoSuchAlgorithmException` |  |

---

### `class abstract MessageDigest`

- **Extends:** `java.security.MessageDigestSpi`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MessageDigest(@NonNull String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int digest(@NonNull byte[], int, int) throws java.security.DigestException` |  |
| `final int getDigestLength()` |  |
| `static boolean isEqual(@Nullable byte[], @Nullable byte[])` |  |
| `void reset()` |  |
| `void update(byte)` |  |
| `void update(@NonNull byte[], int, int)` |  |
| `void update(@NonNull byte[])` |  |
| `final void update(@NonNull java.nio.ByteBuffer)` |  |

---

### `class abstract MessageDigestSpi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MessageDigestSpi()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone() throws java.lang.CloneNotSupportedException` |  |
| `abstract byte[] engineDigest()` |  |
| `int engineDigest(byte[], int, int) throws java.security.DigestException` |  |
| `int engineGetDigestLength()` |  |
| `abstract void engineReset()` |  |
| `abstract void engineUpdate(byte)` |  |
| `abstract void engineUpdate(byte[], int, int)` |  |
| `void engineUpdate(java.nio.ByteBuffer)` |  |

---

### `class NoSuchAlgorithmException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NoSuchAlgorithmException()` |  |
| `NoSuchAlgorithmException(String)` |  |
| `NoSuchAlgorithmException(String, Throwable)` |  |
| `NoSuchAlgorithmException(Throwable)` |  |

---

### `class NoSuchProviderException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NoSuchProviderException()` |  |
| `NoSuchProviderException(String)` |  |

---

### `class final PKCS12Attribute`

- **Implements:** `java.security.KeyStore.Entry.Attribute`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PKCS12Attribute(String, String)` |  |
| `PKCS12Attribute(byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] getEncoded()` |  |
| `String getName()` |  |
| `String getValue()` |  |

---

### `class abstract Permission`

- **Implements:** `java.security.Guard java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Permission(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void checkGuard(Object) throws java.lang.SecurityException` |  |
| `abstract String getActions()` |  |
| `final String getName()` |  |
| `abstract boolean implies(java.security.Permission)` |  |
| `java.security.PermissionCollection newPermissionCollection()` |  |

---

### `class abstract PermissionCollection`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PermissionCollection()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void add(java.security.Permission)` |  |
| `abstract java.util.Enumeration<java.security.Permission> elements()` |  |
| `abstract boolean implies(java.security.Permission)` |  |
| `boolean isReadOnly()` |  |
| `void setReadOnly()` |  |

---

### `class final Permissions`

- **Extends:** `java.security.PermissionCollection`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Permissions()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void add(java.security.Permission)` |  |
| `java.util.Enumeration<java.security.Permission> elements()` |  |
| `boolean implies(java.security.Permission)` |  |

---

### `class abstract Policy`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Policy()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.security.PermissionCollection UNSUPPORTED_EMPTY_COLLECTION` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.security.Policy getInstance(String, java.security.Policy.Parameters) throws java.security.NoSuchAlgorithmException` |  |
| `static java.security.Policy getInstance(String, java.security.Policy.Parameters, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static java.security.Policy getInstance(String, java.security.Policy.Parameters, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `java.security.Policy.Parameters getParameters()` |  |
| `java.security.PermissionCollection getPermissions(java.security.CodeSource)` |  |
| `java.security.PermissionCollection getPermissions(java.security.ProtectionDomain)` |  |
| `static java.security.Policy getPolicy()` |  |
| `java.security.Provider getProvider()` |  |
| `String getType()` |  |
| `boolean implies(java.security.ProtectionDomain, java.security.Permission)` |  |
| `void refresh()` |  |
| `static void setPolicy(java.security.Policy)` |  |

---

### `interface static Policy.Parameters`


---

### `class abstract PolicySpi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PolicySpi()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.PermissionCollection engineGetPermissions(java.security.CodeSource)` |  |
| `java.security.PermissionCollection engineGetPermissions(java.security.ProtectionDomain)` |  |
| `abstract boolean engineImplies(java.security.ProtectionDomain, java.security.Permission)` |  |
| `void engineRefresh()` |  |

---

### `interface Principal`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean equals(Object)` |  |
| `String getName()` |  |
| `int hashCode()` |  |
| `default boolean implies(javax.security.auth.Subject)` |  |
| `String toString()` |  |

---

### `interface PrivateKey`

- **Extends:** `java.security.Key javax.security.auth.Destroyable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = 6034044314589513430L` |  |

---

### `interface PrivilegedAction<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `T run()` |  |

---

### `class PrivilegedActionException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrivilegedActionException(Exception)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Exception getException()` |  |

---

### `interface PrivilegedExceptionAction<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `T run() throws java.lang.Exception` |  |

---

### `class ProtectionDomain`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ProtectionDomain(java.security.CodeSource, java.security.PermissionCollection)` |  |
| `ProtectionDomain(java.security.CodeSource, java.security.PermissionCollection, ClassLoader, java.security.Principal[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final ClassLoader getClassLoader()` |  |
| `final java.security.CodeSource getCodeSource()` |  |
| `final java.security.PermissionCollection getPermissions()` |  |
| `final java.security.Principal[] getPrincipals()` |  |
| `boolean implies(java.security.Permission)` |  |

---

### `class abstract Provider`

- **Extends:** `java.util.Properties`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Provider(String, double, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object compute(Object, java.util.function.BiFunction<? super java.lang.Object,? super java.lang.Object,?>)` |  |
| `Object computeIfAbsent(Object, java.util.function.Function<? super java.lang.Object,?>)` |  |
| `Object computeIfPresent(Object, java.util.function.BiFunction<? super java.lang.Object,? super java.lang.Object,?>)` |  |
| `java.util.Enumeration<java.lang.Object> elements()` |  |
| `java.util.Set<java.util.Map.Entry<java.lang.Object,java.lang.Object>> entrySet()` |  |
| `void forEach(java.util.function.BiConsumer<? super java.lang.Object,? super java.lang.Object>)` |  |
| `Object get(Object)` |  |
| `String getInfo()` |  |
| `String getName()` |  |
| `Object getOrDefault(Object, Object)` |  |
| `java.security.Provider.Service getService(String, String)` |  |
| `java.util.Set<java.security.Provider.Service> getServices()` |  |
| `double getVersion()` |  |
| `java.util.Set<java.lang.Object> keySet()` |  |
| `java.util.Enumeration<java.lang.Object> keys()` |  |
| `Object merge(Object, Object, java.util.function.BiFunction<? super java.lang.Object,? super java.lang.Object,?>)` |  |
| `Object put(Object, Object)` |  |
| `void putAll(java.util.Map<?,?>)` |  |
| `Object putIfAbsent(Object, Object)` |  |
| `void putService(java.security.Provider.Service)` |  |
| `Object remove(Object)` |  |
| `void removeService(java.security.Provider.Service)` |  |
| `boolean replace(Object, Object, Object)` |  |
| `Object replace(Object, Object)` |  |
| `void replaceAll(java.util.function.BiFunction<? super java.lang.Object,? super java.lang.Object,?>)` |  |
| `java.util.Collection<java.lang.Object> values()` |  |

---

### `class static Provider.Service`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Provider.Service(java.security.Provider, String, String, String, java.util.List<java.lang.String>, java.util.Map<java.lang.String,java.lang.String>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final String getAlgorithm()` |  |
| `final String getAttribute(String)` |  |
| `final String getClassName()` |  |
| `final java.security.Provider getProvider()` |  |
| `final String getType()` |  |
| `Object newInstance(Object) throws java.security.NoSuchAlgorithmException` |  |
| `boolean supportsParameter(Object)` |  |

---

### `class ProviderException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ProviderException()` |  |
| `ProviderException(String)` |  |
| `ProviderException(String, Throwable)` |  |
| `ProviderException(Throwable)` |  |

---

### `interface PublicKey`

- **Extends:** `java.security.Key`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = 7187392471159151072L` |  |

---

### `class SecureClassLoader`

- **Extends:** `java.lang.ClassLoader`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SecureClassLoader(ClassLoader)` |  |
| `SecureClassLoader()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final Class<?> defineClass(String, byte[], int, int, java.security.CodeSource)` |  |
| `final Class<?> defineClass(String, java.nio.ByteBuffer, java.security.CodeSource)` |  |
| `java.security.PermissionCollection getPermissions(java.security.CodeSource)` |  |

---

### `class SecureRandom`

- **Extends:** `java.util.Random`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SecureRandom()` |  |
| `SecureRandom(byte[])` |  |
| `SecureRandom(java.security.SecureRandomSpi, java.security.Provider)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] generateSeed(int)` |  |
| `String getAlgorithm()` |  |
| `static java.security.SecureRandom getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static java.security.SecureRandom getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static java.security.SecureRandom getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `static java.security.SecureRandom getInstanceStrong() throws java.security.NoSuchAlgorithmException` |  |
| `final java.security.Provider getProvider()` |  |
| `static byte[] getSeed(int)` |  |
| `final int next(int)` |  |
| `void setSeed(byte[])` |  |

---

### `class abstract SecureRandomSpi`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SecureRandomSpi()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract byte[] engineGenerateSeed(int)` |  |
| `abstract void engineNextBytes(byte[])` |  |
| `abstract void engineSetSeed(byte[])` |  |

---

### `class final Security`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int addProvider(java.security.Provider)` |  |
| `static java.util.Set<java.lang.String> getAlgorithms(String)` |  |
| `static String getProperty(String)` |  |
| `static java.security.Provider getProvider(String)` |  |
| `static java.security.Provider[] getProviders()` |  |
| `static java.security.Provider[] getProviders(String)` |  |
| `static java.security.Provider[] getProviders(java.util.Map<java.lang.String,java.lang.String>)` |  |
| `static int insertProviderAt(java.security.Provider, int)` |  |
| `static void removeProvider(String)` |  |
| `static void setProperty(String, String)` |  |

---

### `class final SecurityPermission`

- **Extends:** `java.security.BasicPermission`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SecurityPermission(String)` |  |
| `SecurityPermission(String, String)` |  |

---

### `class abstract Signature`

- **Extends:** `java.security.SignatureSpi`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Signature(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int SIGN = 2` |  |
| `static final int UNINITIALIZED = 0` |  |
| `static final int VERIFY = 3` |  |
| `int state` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final String getAlgorithm()` |  |
| `static java.security.Signature getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static java.security.Signature getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static java.security.Signature getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final java.security.AlgorithmParameters getParameters()` |  |
| `final java.security.Provider getProvider()` |  |
| `final void initSign(java.security.PrivateKey) throws java.security.InvalidKeyException` |  |
| `final void initSign(java.security.PrivateKey, java.security.SecureRandom) throws java.security.InvalidKeyException` |  |
| `final void initVerify(java.security.PublicKey) throws java.security.InvalidKeyException` |  |
| `final void initVerify(java.security.cert.Certificate) throws java.security.InvalidKeyException` |  |
| `final void setParameter(java.security.spec.AlgorithmParameterSpec) throws java.security.InvalidAlgorithmParameterException` |  |
| `final byte[] sign() throws java.security.SignatureException` |  |
| `final int sign(byte[], int, int) throws java.security.SignatureException` |  |
| `final void update(byte) throws java.security.SignatureException` |  |
| `final void update(byte[]) throws java.security.SignatureException` |  |
| `final void update(byte[], int, int) throws java.security.SignatureException` |  |
| `final void update(java.nio.ByteBuffer) throws java.security.SignatureException` |  |
| `final boolean verify(byte[]) throws java.security.SignatureException` |  |
| `final boolean verify(byte[], int, int) throws java.security.SignatureException` |  |

---

### `class SignatureException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SignatureException()` |  |
| `SignatureException(String)` |  |
| `SignatureException(String, Throwable)` |  |
| `SignatureException(Throwable)` |  |

---

### `class abstract SignatureSpi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SignatureSpi()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.SecureRandom appRandom` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone() throws java.lang.CloneNotSupportedException` |  |
| `java.security.AlgorithmParameters engineGetParameters()` |  |
| `abstract void engineInitSign(java.security.PrivateKey) throws java.security.InvalidKeyException` |  |
| `void engineInitSign(java.security.PrivateKey, java.security.SecureRandom) throws java.security.InvalidKeyException` |  |
| `abstract void engineInitVerify(java.security.PublicKey) throws java.security.InvalidKeyException` |  |
| `void engineSetParameter(java.security.spec.AlgorithmParameterSpec) throws java.security.InvalidAlgorithmParameterException` |  |
| `abstract byte[] engineSign() throws java.security.SignatureException` |  |
| `int engineSign(byte[], int, int) throws java.security.SignatureException` |  |
| `abstract void engineUpdate(byte) throws java.security.SignatureException` |  |
| `abstract void engineUpdate(byte[], int, int) throws java.security.SignatureException` |  |
| `void engineUpdate(java.nio.ByteBuffer)` |  |
| `abstract boolean engineVerify(byte[]) throws java.security.SignatureException` |  |
| `boolean engineVerify(byte[], int, int) throws java.security.SignatureException` |  |

---

### `class final SignedObject`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SignedObject(java.io.Serializable, java.security.PrivateKey, java.security.Signature) throws java.io.IOException, java.security.InvalidKeyException, java.security.SignatureException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getAlgorithm()` |  |
| `Object getObject() throws java.lang.ClassNotFoundException, java.io.IOException` |  |
| `byte[] getSignature()` |  |
| `boolean verify(java.security.PublicKey, java.security.Signature) throws java.security.InvalidKeyException, java.security.SignatureException` |  |

---

### `class abstract Signer` ~~DEPRECATED~~

- **Extends:** `java.security.Identity`
- **Annotations:** `@Deprecated`

---

### `class final Timestamp`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Timestamp(java.util.Date, java.security.cert.CertPath)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.cert.CertPath getSignerCertPath()` |  |
| `java.util.Date getTimestamp()` |  |

---

### `class UnrecoverableEntryException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnrecoverableEntryException()` |  |
| `UnrecoverableEntryException(String)` |  |

---

### `class UnrecoverableKeyException`

- **Extends:** `java.security.UnrecoverableEntryException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnrecoverableKeyException()` |  |
| `UnrecoverableKeyException(String)` |  |

---

### `class final UnresolvedPermission`

- **Extends:** `java.security.Permission`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnresolvedPermission(String, String, String, java.security.cert.Certificate[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getActions()` |  |
| `String getUnresolvedActions()` |  |
| `java.security.cert.Certificate[] getUnresolvedCerts()` |  |
| `String getUnresolvedName()` |  |
| `String getUnresolvedType()` |  |
| `boolean implies(java.security.Permission)` |  |

---

## Package: `java.security.acl`

### `interface Acl`

- **Extends:** `java.security.acl.Owner`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean addEntry(java.security.Principal, java.security.acl.AclEntry) throws java.security.acl.NotOwnerException` |  |
| `boolean checkPermission(java.security.Principal, java.security.acl.Permission)` |  |
| `java.util.Enumeration<java.security.acl.AclEntry> entries()` |  |
| `String getName()` |  |
| `java.util.Enumeration<java.security.acl.Permission> getPermissions(java.security.Principal)` |  |
| `boolean removeEntry(java.security.Principal, java.security.acl.AclEntry) throws java.security.acl.NotOwnerException` |  |
| `void setName(java.security.Principal, String) throws java.security.acl.NotOwnerException` |  |
| `String toString()` |  |

---

### `interface AclEntry`

- **Extends:** `java.lang.Cloneable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean addPermission(java.security.acl.Permission)` |  |
| `boolean checkPermission(java.security.acl.Permission)` |  |
| `Object clone()` |  |
| `java.security.Principal getPrincipal()` |  |
| `boolean isNegative()` |  |
| `java.util.Enumeration<java.security.acl.Permission> permissions()` |  |
| `boolean removePermission(java.security.acl.Permission)` |  |
| `void setNegativePermissions()` |  |
| `boolean setPrincipal(java.security.Principal)` |  |
| `String toString()` |  |

---

### `class AclNotFoundException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AclNotFoundException()` |  |

---

### `interface Group`

- **Extends:** `java.security.Principal`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean addMember(java.security.Principal)` |  |
| `boolean isMember(java.security.Principal)` |  |
| `java.util.Enumeration<? extends java.security.Principal> members()` |  |
| `boolean removeMember(java.security.Principal)` |  |

---

### `class LastOwnerException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LastOwnerException()` |  |

---

### `class NotOwnerException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NotOwnerException()` |  |

---

### `interface Owner`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean addOwner(java.security.Principal, java.security.Principal) throws java.security.acl.NotOwnerException` |  |
| `boolean deleteOwner(java.security.Principal, java.security.Principal) throws java.security.acl.LastOwnerException, java.security.acl.NotOwnerException` |  |
| `boolean isOwner(java.security.Principal)` |  |

---

### `interface Permission`


---

## Package: `java.security.cert`

### `class abstract CRL`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CRL(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final String getType()` |  |
| `abstract boolean isRevoked(java.security.cert.Certificate)` |  |
| `abstract String toString()` |  |

---

### `class CRLException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CRLException()` |  |
| `CRLException(String)` |  |
| `CRLException(String, Throwable)` |  |
| `CRLException(Throwable)` |  |

---

### `enum CRLReason`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.security.cert.CRLReason AA_COMPROMISE` |  |
| `static final java.security.cert.CRLReason AFFILIATION_CHANGED` |  |
| `static final java.security.cert.CRLReason CA_COMPROMISE` |  |
| `static final java.security.cert.CRLReason CERTIFICATE_HOLD` |  |
| `static final java.security.cert.CRLReason CESSATION_OF_OPERATION` |  |
| `static final java.security.cert.CRLReason KEY_COMPROMISE` |  |
| `static final java.security.cert.CRLReason PRIVILEGE_WITHDRAWN` |  |
| `static final java.security.cert.CRLReason REMOVE_FROM_CRL` |  |
| `static final java.security.cert.CRLReason SUPERSEDED` |  |
| `static final java.security.cert.CRLReason UNSPECIFIED` |  |
| `static final java.security.cert.CRLReason UNUSED` |  |

---

### `interface CRLSelector`

- **Extends:** `java.lang.Cloneable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `boolean match(java.security.cert.CRL)` |  |

---

### `class abstract CertPath`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertPath(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.util.List<? extends java.security.cert.Certificate> getCertificates()` |  |
| `abstract byte[] getEncoded() throws java.security.cert.CertificateEncodingException` |  |
| `abstract byte[] getEncoded(String) throws java.security.cert.CertificateEncodingException` |  |
| `abstract java.util.Iterator<java.lang.String> getEncodings()` |  |
| `String getType()` |  |
| `Object writeReplace() throws java.io.ObjectStreamException` |  |

---

### `class static CertPath.CertPathRep`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertPath.CertPathRep(String, byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object readResolve() throws java.io.ObjectStreamException` |  |

---

### `class CertPathBuilder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertPathBuilder(java.security.cert.CertPathBuilderSpi, java.security.Provider, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.security.cert.CertPathBuilderResult build(java.security.cert.CertPathParameters) throws java.security.cert.CertPathBuilderException, java.security.InvalidAlgorithmParameterException` |  |
| `final String getAlgorithm()` |  |
| `static final String getDefaultType()` |  |
| `static java.security.cert.CertPathBuilder getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static java.security.cert.CertPathBuilder getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static java.security.cert.CertPathBuilder getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final java.security.Provider getProvider()` |  |
| `final java.security.cert.CertPathChecker getRevocationChecker()` |  |

---

### `class CertPathBuilderException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertPathBuilderException()` |  |
| `CertPathBuilderException(String)` |  |
| `CertPathBuilderException(Throwable)` |  |
| `CertPathBuilderException(String, Throwable)` |  |

---

### `interface CertPathBuilderResult`

- **Extends:** `java.lang.Cloneable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `java.security.cert.CertPath getCertPath()` |  |

---

### `class abstract CertPathBuilderSpi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertPathBuilderSpi()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.security.cert.CertPathBuilderResult engineBuild(java.security.cert.CertPathParameters) throws java.security.cert.CertPathBuilderException, java.security.InvalidAlgorithmParameterException` |  |
| `java.security.cert.CertPathChecker engineGetRevocationChecker()` |  |

---

### `interface CertPathChecker`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void check(java.security.cert.Certificate) throws java.security.cert.CertPathValidatorException` |  |
| `void init(boolean) throws java.security.cert.CertPathValidatorException` |  |
| `boolean isForwardCheckingSupported()` |  |

---

### `interface CertPathParameters`

- **Extends:** `java.lang.Cloneable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |

---

### `class CertPathValidator`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertPathValidator(java.security.cert.CertPathValidatorSpi, java.security.Provider, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final String getAlgorithm()` |  |
| `static final String getDefaultType()` |  |
| `static java.security.cert.CertPathValidator getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static java.security.cert.CertPathValidator getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static java.security.cert.CertPathValidator getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final java.security.Provider getProvider()` |  |
| `final java.security.cert.CertPathChecker getRevocationChecker()` |  |
| `final java.security.cert.CertPathValidatorResult validate(java.security.cert.CertPath, java.security.cert.CertPathParameters) throws java.security.cert.CertPathValidatorException, java.security.InvalidAlgorithmParameterException` |  |

---

### `class CertPathValidatorException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertPathValidatorException()` |  |
| `CertPathValidatorException(String)` |  |
| `CertPathValidatorException(Throwable)` |  |
| `CertPathValidatorException(String, Throwable)` |  |
| `CertPathValidatorException(String, Throwable, java.security.cert.CertPath, int)` |  |
| `CertPathValidatorException(String, Throwable, java.security.cert.CertPath, int, java.security.cert.CertPathValidatorException.Reason)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.cert.CertPath getCertPath()` |  |
| `int getIndex()` |  |
| `java.security.cert.CertPathValidatorException.Reason getReason()` |  |

---

### `enum CertPathValidatorException.BasicReason`

- **Implements:** `java.security.cert.CertPathValidatorException.Reason`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.security.cert.CertPathValidatorException.BasicReason ALGORITHM_CONSTRAINED` |  |
| `static final java.security.cert.CertPathValidatorException.BasicReason EXPIRED` |  |
| `static final java.security.cert.CertPathValidatorException.BasicReason INVALID_SIGNATURE` |  |
| `static final java.security.cert.CertPathValidatorException.BasicReason NOT_YET_VALID` |  |
| `static final java.security.cert.CertPathValidatorException.BasicReason REVOKED` |  |
| `static final java.security.cert.CertPathValidatorException.BasicReason UNDETERMINED_REVOCATION_STATUS` |  |
| `static final java.security.cert.CertPathValidatorException.BasicReason UNSPECIFIED` |  |

---

### `interface static CertPathValidatorException.Reason`

- **Extends:** `java.io.Serializable`

---

### `interface CertPathValidatorResult`

- **Extends:** `java.lang.Cloneable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |

---

### `class abstract CertPathValidatorSpi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertPathValidatorSpi()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.cert.CertPathChecker engineGetRevocationChecker()` |  |
| `abstract java.security.cert.CertPathValidatorResult engineValidate(java.security.cert.CertPath, java.security.cert.CertPathParameters) throws java.security.cert.CertPathValidatorException, java.security.InvalidAlgorithmParameterException` |  |

---

### `interface CertSelector`

- **Extends:** `java.lang.Cloneable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `boolean match(java.security.cert.Certificate)` |  |

---

### `class CertStore`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertStore(java.security.cert.CertStoreSpi, java.security.Provider, String, java.security.cert.CertStoreParameters)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.util.Collection<? extends java.security.cert.CRL> getCRLs(java.security.cert.CRLSelector) throws java.security.cert.CertStoreException` |  |
| `final java.security.cert.CertStoreParameters getCertStoreParameters()` |  |
| `final java.util.Collection<? extends java.security.cert.Certificate> getCertificates(java.security.cert.CertSelector) throws java.security.cert.CertStoreException` |  |
| `static final String getDefaultType()` |  |
| `static java.security.cert.CertStore getInstance(String, java.security.cert.CertStoreParameters) throws java.security.InvalidAlgorithmParameterException, java.security.NoSuchAlgorithmException` |  |
| `static java.security.cert.CertStore getInstance(String, java.security.cert.CertStoreParameters, String) throws java.security.InvalidAlgorithmParameterException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static java.security.cert.CertStore getInstance(String, java.security.cert.CertStoreParameters, java.security.Provider) throws java.security.InvalidAlgorithmParameterException, java.security.NoSuchAlgorithmException` |  |
| `final java.security.Provider getProvider()` |  |
| `final String getType()` |  |

---

### `class CertStoreException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertStoreException()` |  |
| `CertStoreException(String)` |  |
| `CertStoreException(Throwable)` |  |
| `CertStoreException(String, Throwable)` |  |

---

### `interface CertStoreParameters`

- **Extends:** `java.lang.Cloneable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |

---

### `class abstract CertStoreSpi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertStoreSpi(java.security.cert.CertStoreParameters) throws java.security.InvalidAlgorithmParameterException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.util.Collection<? extends java.security.cert.CRL> engineGetCRLs(java.security.cert.CRLSelector) throws java.security.cert.CertStoreException` |  |
| `abstract java.util.Collection<? extends java.security.cert.Certificate> engineGetCertificates(java.security.cert.CertSelector) throws java.security.cert.CertStoreException` |  |

---

### `class abstract Certificate`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Certificate(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract byte[] getEncoded() throws java.security.cert.CertificateEncodingException` |  |
| `abstract java.security.PublicKey getPublicKey()` |  |
| `final String getType()` |  |
| `abstract String toString()` |  |
| `abstract void verify(java.security.PublicKey) throws java.security.cert.CertificateException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException, java.security.SignatureException` |  |
| `abstract void verify(java.security.PublicKey, String) throws java.security.cert.CertificateException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException, java.security.SignatureException` |  |
| `void verify(java.security.PublicKey, java.security.Provider) throws java.security.cert.CertificateException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.SignatureException` |  |
| `Object writeReplace() throws java.io.ObjectStreamException` |  |

---

### `class static Certificate.CertificateRep`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Certificate.CertificateRep(String, byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object readResolve() throws java.io.ObjectStreamException` |  |

---

### `class CertificateEncodingException`

- **Extends:** `java.security.cert.CertificateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertificateEncodingException()` |  |
| `CertificateEncodingException(String)` |  |
| `CertificateEncodingException(String, Throwable)` |  |
| `CertificateEncodingException(Throwable)` |  |

---

### `class CertificateException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertificateException()` |  |
| `CertificateException(String)` |  |
| `CertificateException(String, Throwable)` |  |
| `CertificateException(Throwable)` |  |

---

### `class CertificateExpiredException`

- **Extends:** `java.security.cert.CertificateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertificateExpiredException()` |  |
| `CertificateExpiredException(String)` |  |

---

### `class CertificateFactory`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertificateFactory(java.security.cert.CertificateFactorySpi, java.security.Provider, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.security.cert.CRL generateCRL(java.io.InputStream) throws java.security.cert.CRLException` |  |
| `final java.util.Collection<? extends java.security.cert.CRL> generateCRLs(java.io.InputStream) throws java.security.cert.CRLException` |  |
| `final java.security.cert.CertPath generateCertPath(java.io.InputStream) throws java.security.cert.CertificateException` |  |
| `final java.security.cert.CertPath generateCertPath(java.io.InputStream, String) throws java.security.cert.CertificateException` |  |
| `final java.security.cert.CertPath generateCertPath(java.util.List<? extends java.security.cert.Certificate>) throws java.security.cert.CertificateException` |  |
| `final java.security.cert.Certificate generateCertificate(java.io.InputStream) throws java.security.cert.CertificateException` |  |
| `final java.util.Collection<? extends java.security.cert.Certificate> generateCertificates(java.io.InputStream) throws java.security.cert.CertificateException` |  |
| `final java.util.Iterator<java.lang.String> getCertPathEncodings()` |  |
| `static final java.security.cert.CertificateFactory getInstance(String) throws java.security.cert.CertificateException` |  |
| `static final java.security.cert.CertificateFactory getInstance(String, String) throws java.security.cert.CertificateException, java.security.NoSuchProviderException` |  |
| `static final java.security.cert.CertificateFactory getInstance(String, java.security.Provider) throws java.security.cert.CertificateException` |  |
| `final java.security.Provider getProvider()` |  |
| `final String getType()` |  |

---

### `class abstract CertificateFactorySpi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertificateFactorySpi()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.security.cert.CRL engineGenerateCRL(java.io.InputStream) throws java.security.cert.CRLException` |  |
| `abstract java.util.Collection<? extends java.security.cert.CRL> engineGenerateCRLs(java.io.InputStream) throws java.security.cert.CRLException` |  |
| `java.security.cert.CertPath engineGenerateCertPath(java.io.InputStream) throws java.security.cert.CertificateException` |  |
| `java.security.cert.CertPath engineGenerateCertPath(java.io.InputStream, String) throws java.security.cert.CertificateException` |  |
| `java.security.cert.CertPath engineGenerateCertPath(java.util.List<? extends java.security.cert.Certificate>) throws java.security.cert.CertificateException` |  |
| `abstract java.security.cert.Certificate engineGenerateCertificate(java.io.InputStream) throws java.security.cert.CertificateException` |  |
| `abstract java.util.Collection<? extends java.security.cert.Certificate> engineGenerateCertificates(java.io.InputStream) throws java.security.cert.CertificateException` |  |
| `java.util.Iterator<java.lang.String> engineGetCertPathEncodings()` |  |

---

### `class CertificateNotYetValidException`

- **Extends:** `java.security.cert.CertificateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertificateNotYetValidException()` |  |
| `CertificateNotYetValidException(String)` |  |

---

### `class CertificateParsingException`

- **Extends:** `java.security.cert.CertificateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertificateParsingException()` |  |
| `CertificateParsingException(String)` |  |
| `CertificateParsingException(String, Throwable)` |  |
| `CertificateParsingException(Throwable)` |  |

---

### `class CertificateRevokedException`

- **Extends:** `java.security.cert.CertificateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CertificateRevokedException(java.util.Date, java.security.cert.CRLReason, javax.security.auth.x500.X500Principal, java.util.Map<java.lang.String,java.security.cert.Extension>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `javax.security.auth.x500.X500Principal getAuthorityName()` |  |
| `java.util.Map<java.lang.String,java.security.cert.Extension> getExtensions()` |  |
| `java.util.Date getInvalidityDate()` |  |
| `java.util.Date getRevocationDate()` |  |
| `java.security.cert.CRLReason getRevocationReason()` |  |

---

### `class CollectionCertStoreParameters`

- **Implements:** `java.security.cert.CertStoreParameters`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CollectionCertStoreParameters(java.util.Collection<?>)` |  |
| `CollectionCertStoreParameters()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `java.util.Collection<?> getCollection()` |  |

---

### `interface Extension`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void encode(java.io.OutputStream) throws java.io.IOException` |  |
| `String getId()` |  |
| `byte[] getValue()` |  |
| `boolean isCritical()` |  |

---

### `class LDAPCertStoreParameters`

- **Implements:** `java.security.cert.CertStoreParameters`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LDAPCertStoreParameters(String, int)` |  |
| `LDAPCertStoreParameters(String)` |  |
| `LDAPCertStoreParameters()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `int getPort()` |  |
| `String getServerName()` |  |

---

### `class PKIXBuilderParameters`

- **Extends:** `java.security.cert.PKIXParameters`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PKIXBuilderParameters(java.util.Set<java.security.cert.TrustAnchor>, java.security.cert.CertSelector) throws java.security.InvalidAlgorithmParameterException` |  |
| `PKIXBuilderParameters(java.security.KeyStore, java.security.cert.CertSelector) throws java.security.InvalidAlgorithmParameterException, java.security.KeyStoreException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getMaxPathLength()` |  |
| `void setMaxPathLength(int)` |  |

---

### `class PKIXCertPathBuilderResult`

- **Extends:** `java.security.cert.PKIXCertPathValidatorResult`
- **Implements:** `java.security.cert.CertPathBuilderResult`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PKIXCertPathBuilderResult(java.security.cert.CertPath, java.security.cert.TrustAnchor, java.security.cert.PolicyNode, java.security.PublicKey)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.cert.CertPath getCertPath()` |  |

---

### `class abstract PKIXCertPathChecker`

- **Implements:** `java.security.cert.CertPathChecker java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PKIXCertPathChecker()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void check(java.security.cert.Certificate, java.util.Collection<java.lang.String>) throws java.security.cert.CertPathValidatorException` |  |
| `void check(java.security.cert.Certificate) throws java.security.cert.CertPathValidatorException` |  |
| `Object clone()` |  |
| `abstract java.util.Set<java.lang.String> getSupportedExtensions()` |  |

---

### `class PKIXCertPathValidatorResult`

- **Implements:** `java.security.cert.CertPathValidatorResult`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PKIXCertPathValidatorResult(java.security.cert.TrustAnchor, java.security.cert.PolicyNode, java.security.PublicKey)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `java.security.cert.PolicyNode getPolicyTree()` |  |
| `java.security.PublicKey getPublicKey()` |  |
| `java.security.cert.TrustAnchor getTrustAnchor()` |  |

---

### `class PKIXParameters`

- **Implements:** `java.security.cert.CertPathParameters`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PKIXParameters(java.util.Set<java.security.cert.TrustAnchor>) throws java.security.InvalidAlgorithmParameterException` |  |
| `PKIXParameters(java.security.KeyStore) throws java.security.InvalidAlgorithmParameterException, java.security.KeyStoreException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addCertPathChecker(java.security.cert.PKIXCertPathChecker)` |  |
| `void addCertStore(java.security.cert.CertStore)` |  |
| `Object clone()` |  |
| `java.util.List<java.security.cert.PKIXCertPathChecker> getCertPathCheckers()` |  |
| `java.util.List<java.security.cert.CertStore> getCertStores()` |  |
| `java.util.Date getDate()` |  |
| `java.util.Set<java.lang.String> getInitialPolicies()` |  |
| `boolean getPolicyQualifiersRejected()` |  |
| `String getSigProvider()` |  |
| `java.security.cert.CertSelector getTargetCertConstraints()` |  |
| `java.util.Set<java.security.cert.TrustAnchor> getTrustAnchors()` |  |
| `boolean isAnyPolicyInhibited()` |  |
| `boolean isExplicitPolicyRequired()` |  |
| `boolean isPolicyMappingInhibited()` |  |
| `boolean isRevocationEnabled()` |  |
| `void setAnyPolicyInhibited(boolean)` |  |
| `void setCertPathCheckers(java.util.List<java.security.cert.PKIXCertPathChecker>)` |  |
| `void setCertStores(java.util.List<java.security.cert.CertStore>)` |  |
| `void setDate(java.util.Date)` |  |
| `void setExplicitPolicyRequired(boolean)` |  |
| `void setInitialPolicies(java.util.Set<java.lang.String>)` |  |
| `void setPolicyMappingInhibited(boolean)` |  |
| `void setPolicyQualifiersRejected(boolean)` |  |
| `void setRevocationEnabled(boolean)` |  |
| `void setSigProvider(String)` |  |
| `void setTargetCertConstraints(java.security.cert.CertSelector)` |  |
| `void setTrustAnchors(java.util.Set<java.security.cert.TrustAnchor>) throws java.security.InvalidAlgorithmParameterException` |  |

---

### `enum PKIXReason`

- **Implements:** `java.security.cert.CertPathValidatorException.Reason`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.security.cert.PKIXReason INVALID_KEY_USAGE` |  |
| `static final java.security.cert.PKIXReason INVALID_NAME` |  |
| `static final java.security.cert.PKIXReason INVALID_POLICY` |  |
| `static final java.security.cert.PKIXReason NAME_CHAINING` |  |
| `static final java.security.cert.PKIXReason NOT_CA_CERT` |  |
| `static final java.security.cert.PKIXReason NO_TRUST_ANCHOR` |  |
| `static final java.security.cert.PKIXReason PATH_TOO_LONG` |  |
| `static final java.security.cert.PKIXReason UNRECOGNIZED_CRIT_EXT` |  |

---

### `class abstract PKIXRevocationChecker`

- **Extends:** `java.security.cert.PKIXCertPathChecker`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PKIXRevocationChecker()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.cert.PKIXRevocationChecker clone()` |  |
| `java.util.List<java.security.cert.Extension> getOcspExtensions()` |  |
| `java.net.URI getOcspResponder()` |  |
| `java.security.cert.X509Certificate getOcspResponderCert()` |  |
| `java.util.Map<java.security.cert.X509Certificate,byte[]> getOcspResponses()` |  |
| `java.util.Set<java.security.cert.PKIXRevocationChecker.Option> getOptions()` |  |
| `abstract java.util.List<java.security.cert.CertPathValidatorException> getSoftFailExceptions()` |  |
| `void setOcspExtensions(java.util.List<java.security.cert.Extension>)` |  |
| `void setOcspResponder(java.net.URI)` |  |
| `void setOcspResponderCert(java.security.cert.X509Certificate)` |  |
| `void setOcspResponses(java.util.Map<java.security.cert.X509Certificate,byte[]>)` |  |
| `void setOptions(java.util.Set<java.security.cert.PKIXRevocationChecker.Option>)` |  |

---

### `enum PKIXRevocationChecker.Option`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.security.cert.PKIXRevocationChecker.Option NO_FALLBACK` |  |
| `static final java.security.cert.PKIXRevocationChecker.Option ONLY_END_ENTITY` |  |
| `static final java.security.cert.PKIXRevocationChecker.Option PREFER_CRLS` |  |
| `static final java.security.cert.PKIXRevocationChecker.Option SOFT_FAIL` |  |

---

### `interface PolicyNode`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Iterator<? extends java.security.cert.PolicyNode> getChildren()` |  |
| `int getDepth()` |  |
| `java.util.Set<java.lang.String> getExpectedPolicies()` |  |
| `java.security.cert.PolicyNode getParent()` |  |
| `java.util.Set<? extends java.security.cert.PolicyQualifierInfo> getPolicyQualifiers()` |  |
| `String getValidPolicy()` |  |
| `boolean isCritical()` |  |

---

### `class PolicyQualifierInfo`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PolicyQualifierInfo(byte[]) throws java.io.IOException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final byte[] getEncoded()` |  |
| `final byte[] getPolicyQualifier()` |  |
| `final String getPolicyQualifierId()` |  |

---

### `class TrustAnchor`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TrustAnchor(java.security.cert.X509Certificate, byte[])` |  |
| `TrustAnchor(javax.security.auth.x500.X500Principal, java.security.PublicKey, byte[])` |  |
| `TrustAnchor(String, java.security.PublicKey, byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final javax.security.auth.x500.X500Principal getCA()` |  |
| `final String getCAName()` |  |
| `final java.security.PublicKey getCAPublicKey()` |  |
| `final byte[] getNameConstraints()` |  |
| `final java.security.cert.X509Certificate getTrustedCert()` |  |

---

### `class abstract X509CRL`

- **Extends:** `java.security.cert.CRL`
- **Implements:** `java.security.cert.X509Extension`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `X509CRL()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract byte[] getEncoded() throws java.security.cert.CRLException` |  |
| `abstract java.security.Principal getIssuerDN()` |  |
| `javax.security.auth.x500.X500Principal getIssuerX500Principal()` |  |
| `abstract java.util.Date getNextUpdate()` |  |
| `abstract java.security.cert.X509CRLEntry getRevokedCertificate(java.math.BigInteger)` |  |
| `java.security.cert.X509CRLEntry getRevokedCertificate(java.security.cert.X509Certificate)` |  |
| `abstract java.util.Set<? extends java.security.cert.X509CRLEntry> getRevokedCertificates()` |  |
| `abstract String getSigAlgName()` |  |
| `abstract String getSigAlgOID()` |  |
| `abstract byte[] getSigAlgParams()` |  |
| `abstract byte[] getSignature()` |  |
| `abstract byte[] getTBSCertList() throws java.security.cert.CRLException` |  |
| `abstract java.util.Date getThisUpdate()` |  |
| `abstract int getVersion()` |  |
| `abstract void verify(java.security.PublicKey) throws java.security.cert.CRLException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException, java.security.SignatureException` |  |
| `abstract void verify(java.security.PublicKey, String) throws java.security.cert.CRLException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException, java.security.SignatureException` |  |
| `void verify(java.security.PublicKey, java.security.Provider) throws java.security.cert.CRLException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.SignatureException` |  |

---

### `class abstract X509CRLEntry`

- **Implements:** `java.security.cert.X509Extension`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `X509CRLEntry()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `javax.security.auth.x500.X500Principal getCertificateIssuer()` |  |
| `abstract byte[] getEncoded() throws java.security.cert.CRLException` |  |
| `abstract java.util.Date getRevocationDate()` |  |
| `java.security.cert.CRLReason getRevocationReason()` |  |
| `abstract java.math.BigInteger getSerialNumber()` |  |
| `abstract boolean hasExtensions()` |  |
| `abstract String toString()` |  |

---

### `class X509CRLSelector`

- **Implements:** `java.security.cert.CRLSelector`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `X509CRLSelector()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addIssuer(javax.security.auth.x500.X500Principal)` |  |
| `void addIssuerName(String) throws java.io.IOException` |  |
| `void addIssuerName(byte[]) throws java.io.IOException` |  |
| `Object clone()` |  |
| `java.security.cert.X509Certificate getCertificateChecking()` |  |
| `java.util.Date getDateAndTime()` |  |
| `java.util.Collection<java.lang.Object> getIssuerNames()` |  |
| `java.util.Collection<javax.security.auth.x500.X500Principal> getIssuers()` |  |
| `java.math.BigInteger getMaxCRL()` |  |
| `java.math.BigInteger getMinCRL()` |  |
| `boolean match(java.security.cert.CRL)` |  |
| `void setCertificateChecking(java.security.cert.X509Certificate)` |  |
| `void setDateAndTime(java.util.Date)` |  |
| `void setIssuerNames(java.util.Collection<?>) throws java.io.IOException` |  |
| `void setIssuers(java.util.Collection<javax.security.auth.x500.X500Principal>)` |  |
| `void setMaxCRLNumber(java.math.BigInteger)` |  |
| `void setMinCRLNumber(java.math.BigInteger)` |  |

---

### `class X509CertSelector`

- **Implements:** `java.security.cert.CertSelector`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `X509CertSelector()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addPathToName(int, String) throws java.io.IOException` |  |
| `void addPathToName(int, byte[]) throws java.io.IOException` |  |
| `void addSubjectAlternativeName(int, String) throws java.io.IOException` |  |
| `void addSubjectAlternativeName(int, byte[]) throws java.io.IOException` |  |
| `Object clone()` |  |
| `byte[] getAuthorityKeyIdentifier()` |  |
| `int getBasicConstraints()` |  |
| `java.security.cert.X509Certificate getCertificate()` |  |
| `java.util.Date getCertificateValid()` |  |
| `java.util.Set<java.lang.String> getExtendedKeyUsage()` |  |
| `javax.security.auth.x500.X500Principal getIssuer()` |  |
| `byte[] getIssuerAsBytes() throws java.io.IOException` |  |
| `String getIssuerAsString()` |  |
| `boolean[] getKeyUsage()` |  |
| `boolean getMatchAllSubjectAltNames()` |  |
| `byte[] getNameConstraints()` |  |
| `java.util.Collection<java.util.List<?>> getPathToNames()` |  |
| `java.util.Set<java.lang.String> getPolicy()` |  |
| `java.util.Date getPrivateKeyValid()` |  |
| `java.math.BigInteger getSerialNumber()` |  |
| `javax.security.auth.x500.X500Principal getSubject()` |  |
| `java.util.Collection<java.util.List<?>> getSubjectAlternativeNames()` |  |
| `byte[] getSubjectAsBytes() throws java.io.IOException` |  |
| `String getSubjectAsString()` |  |
| `byte[] getSubjectKeyIdentifier()` |  |
| `java.security.PublicKey getSubjectPublicKey()` |  |
| `String getSubjectPublicKeyAlgID()` |  |
| `boolean match(java.security.cert.Certificate)` |  |
| `void setAuthorityKeyIdentifier(byte[])` |  |
| `void setBasicConstraints(int)` |  |
| `void setCertificate(java.security.cert.X509Certificate)` |  |
| `void setCertificateValid(java.util.Date)` |  |
| `void setExtendedKeyUsage(java.util.Set<java.lang.String>) throws java.io.IOException` |  |
| `void setIssuer(javax.security.auth.x500.X500Principal)` |  |
| `void setIssuer(String) throws java.io.IOException` |  |
| `void setIssuer(byte[]) throws java.io.IOException` |  |
| `void setKeyUsage(boolean[])` |  |
| `void setMatchAllSubjectAltNames(boolean)` |  |
| `void setNameConstraints(byte[]) throws java.io.IOException` |  |
| `void setPathToNames(java.util.Collection<java.util.List<?>>) throws java.io.IOException` |  |
| `void setPolicy(java.util.Set<java.lang.String>) throws java.io.IOException` |  |
| `void setPrivateKeyValid(java.util.Date)` |  |
| `void setSerialNumber(java.math.BigInteger)` |  |
| `void setSubject(javax.security.auth.x500.X500Principal)` |  |
| `void setSubject(String) throws java.io.IOException` |  |
| `void setSubject(byte[]) throws java.io.IOException` |  |
| `void setSubjectAlternativeNames(java.util.Collection<java.util.List<?>>) throws java.io.IOException` |  |
| `void setSubjectKeyIdentifier(byte[])` |  |
| `void setSubjectPublicKey(java.security.PublicKey)` |  |
| `void setSubjectPublicKey(byte[]) throws java.io.IOException` |  |
| `void setSubjectPublicKeyAlgID(String) throws java.io.IOException` |  |

---

### `class abstract X509Certificate`

- **Extends:** `java.security.cert.Certificate`
- **Implements:** `java.security.cert.X509Extension`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `X509Certificate()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void checkValidity() throws java.security.cert.CertificateExpiredException, java.security.cert.CertificateNotYetValidException` |  |
| `abstract void checkValidity(java.util.Date) throws java.security.cert.CertificateExpiredException, java.security.cert.CertificateNotYetValidException` |  |
| `abstract int getBasicConstraints()` |  |
| `java.util.List<java.lang.String> getExtendedKeyUsage() throws java.security.cert.CertificateParsingException` |  |
| `java.util.Collection<java.util.List<?>> getIssuerAlternativeNames() throws java.security.cert.CertificateParsingException` |  |
| `abstract java.security.Principal getIssuerDN()` |  |
| `abstract boolean[] getIssuerUniqueID()` |  |
| `javax.security.auth.x500.X500Principal getIssuerX500Principal()` |  |
| `abstract boolean[] getKeyUsage()` |  |
| `abstract java.util.Date getNotAfter()` |  |
| `abstract java.util.Date getNotBefore()` |  |
| `abstract java.math.BigInteger getSerialNumber()` |  |
| `abstract String getSigAlgName()` |  |
| `abstract String getSigAlgOID()` |  |
| `abstract byte[] getSigAlgParams()` |  |
| `abstract byte[] getSignature()` |  |
| `java.util.Collection<java.util.List<?>> getSubjectAlternativeNames() throws java.security.cert.CertificateParsingException` |  |
| `abstract java.security.Principal getSubjectDN()` |  |
| `abstract boolean[] getSubjectUniqueID()` |  |
| `javax.security.auth.x500.X500Principal getSubjectX500Principal()` |  |
| `abstract byte[] getTBSCertificate() throws java.security.cert.CertificateEncodingException` |  |
| `abstract int getVersion()` |  |

---

### `interface X509Extension`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Set<java.lang.String> getCriticalExtensionOIDs()` |  |
| `byte[] getExtensionValue(String)` |  |
| `java.util.Set<java.lang.String> getNonCriticalExtensionOIDs()` |  |
| `boolean hasUnsupportedCriticalExtension()` |  |

---

## Package: `java.security.interfaces`

### `interface DSAKey`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.interfaces.DSAParams getParams()` |  |

---

### `interface DSAKeyPairGenerator`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void initialize(java.security.interfaces.DSAParams, java.security.SecureRandom) throws java.security.InvalidParameterException` |  |
| `void initialize(int, boolean, java.security.SecureRandom) throws java.security.InvalidParameterException` |  |

---

### `interface DSAParams`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getG()` |  |
| `java.math.BigInteger getP()` |  |
| `java.math.BigInteger getQ()` |  |

---

### `interface DSAPrivateKey`

- **Extends:** `java.security.interfaces.DSAKey java.security.PrivateKey`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = 7776497482533790279L` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getX()` |  |

---

### `interface DSAPublicKey`

- **Extends:** `java.security.interfaces.DSAKey java.security.PublicKey`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = 1234526332779022332L` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getY()` |  |

---

### `interface ECKey`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.spec.ECParameterSpec getParams()` |  |

---

### `interface ECPrivateKey`

- **Extends:** `java.security.PrivateKey java.security.interfaces.ECKey`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = -7896394956925609184L` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getS()` |  |

---

### `interface ECPublicKey`

- **Extends:** `java.security.PublicKey java.security.interfaces.ECKey`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = -3314988629879632826L` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.spec.ECPoint getW()` |  |

---

### `interface RSAKey`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getModulus()` |  |

---

### `interface RSAMultiPrimePrivateCrtKey`

- **Extends:** `java.security.interfaces.RSAPrivateKey`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = 618058533534628008L` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getCrtCoefficient()` |  |
| `java.security.spec.RSAOtherPrimeInfo[] getOtherPrimeInfo()` |  |
| `java.math.BigInteger getPrimeExponentP()` |  |
| `java.math.BigInteger getPrimeExponentQ()` |  |
| `java.math.BigInteger getPrimeP()` |  |
| `java.math.BigInteger getPrimeQ()` |  |
| `java.math.BigInteger getPublicExponent()` |  |

---

### `interface RSAPrivateCrtKey`

- **Extends:** `java.security.interfaces.RSAPrivateKey`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = -5682214253527700368L` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getCrtCoefficient()` |  |
| `java.math.BigInteger getPrimeExponentP()` |  |
| `java.math.BigInteger getPrimeExponentQ()` |  |
| `java.math.BigInteger getPrimeP()` |  |
| `java.math.BigInteger getPrimeQ()` |  |
| `java.math.BigInteger getPublicExponent()` |  |

---

### `interface RSAPrivateKey`

- **Extends:** `java.security.PrivateKey java.security.interfaces.RSAKey`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = 5187144804936595022L` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getPrivateExponent()` |  |

---

### `interface RSAPublicKey`

- **Extends:** `java.security.PublicKey java.security.interfaces.RSAKey`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = -8727434096241101194L` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getPublicExponent()` |  |

---

## Package: `java.security.spec`

### `interface AlgorithmParameterSpec`


---

### `class DSAParameterSpec`

- **Implements:** `java.security.spec.AlgorithmParameterSpec java.security.interfaces.DSAParams`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DSAParameterSpec(java.math.BigInteger, java.math.BigInteger, java.math.BigInteger)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getG()` |  |
| `java.math.BigInteger getP()` |  |
| `java.math.BigInteger getQ()` |  |

---

### `class DSAPrivateKeySpec`

- **Implements:** `java.security.spec.KeySpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DSAPrivateKeySpec(java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getG()` |  |
| `java.math.BigInteger getP()` |  |
| `java.math.BigInteger getQ()` |  |
| `java.math.BigInteger getX()` |  |

---

### `class DSAPublicKeySpec`

- **Implements:** `java.security.spec.KeySpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DSAPublicKeySpec(java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getG()` |  |
| `java.math.BigInteger getP()` |  |
| `java.math.BigInteger getQ()` |  |
| `java.math.BigInteger getY()` |  |

---

### `interface ECField`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getFieldSize()` |  |

---

### `class ECFieldF2m`

- **Implements:** `java.security.spec.ECField`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ECFieldF2m(int)` |  |
| `ECFieldF2m(int, java.math.BigInteger)` |  |
| `ECFieldF2m(int, int[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getFieldSize()` |  |
| `int getM()` |  |
| `int[] getMidTermsOfReductionPolynomial()` |  |
| `java.math.BigInteger getReductionPolynomial()` |  |

---

### `class ECFieldFp`

- **Implements:** `java.security.spec.ECField`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ECFieldFp(java.math.BigInteger)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getFieldSize()` |  |
| `java.math.BigInteger getP()` |  |

---

### `class ECGenParameterSpec`

- **Implements:** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ECGenParameterSpec(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getName()` |  |

---

### `class ECParameterSpec`

- **Implements:** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ECParameterSpec(java.security.spec.EllipticCurve, java.security.spec.ECPoint, java.math.BigInteger, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getCofactor()` |  |
| `java.security.spec.EllipticCurve getCurve()` |  |
| `java.security.spec.ECPoint getGenerator()` |  |
| `java.math.BigInteger getOrder()` |  |

---

### `class ECPoint`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ECPoint(java.math.BigInteger, java.math.BigInteger)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.security.spec.ECPoint POINT_INFINITY` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getAffineX()` |  |
| `java.math.BigInteger getAffineY()` |  |

---

### `class ECPrivateKeySpec`

- **Implements:** `java.security.spec.KeySpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ECPrivateKeySpec(java.math.BigInteger, java.security.spec.ECParameterSpec)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.spec.ECParameterSpec getParams()` |  |
| `java.math.BigInteger getS()` |  |

---

### `class ECPublicKeySpec`

- **Implements:** `java.security.spec.KeySpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ECPublicKeySpec(java.security.spec.ECPoint, java.security.spec.ECParameterSpec)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.spec.ECParameterSpec getParams()` |  |
| `java.security.spec.ECPoint getW()` |  |

---

### `class EllipticCurve`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EllipticCurve(java.security.spec.ECField, java.math.BigInteger, java.math.BigInteger)` |  |
| `EllipticCurve(java.security.spec.ECField, java.math.BigInteger, java.math.BigInteger, byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getA()` |  |
| `java.math.BigInteger getB()` |  |
| `java.security.spec.ECField getField()` |  |
| `byte[] getSeed()` |  |

---

### `class abstract EncodedKeySpec`

- **Implements:** `java.security.spec.KeySpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EncodedKeySpec(byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] getEncoded()` |  |
| `abstract String getFormat()` |  |

---

### `class InvalidKeySpecException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidKeySpecException()` |  |
| `InvalidKeySpecException(String)` |  |
| `InvalidKeySpecException(String, Throwable)` |  |
| `InvalidKeySpecException(Throwable)` |  |

---

### `class InvalidParameterSpecException`

- **Extends:** `java.security.GeneralSecurityException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidParameterSpecException()` |  |
| `InvalidParameterSpecException(String)` |  |

---

### `interface KeySpec`


---

### `class MGF1ParameterSpec`

- **Implements:** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MGF1ParameterSpec(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.security.spec.MGF1ParameterSpec SHA1` |  |
| `static final java.security.spec.MGF1ParameterSpec SHA224` |  |
| `static final java.security.spec.MGF1ParameterSpec SHA256` |  |
| `static final java.security.spec.MGF1ParameterSpec SHA384` |  |
| `static final java.security.spec.MGF1ParameterSpec SHA512` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getDigestAlgorithm()` |  |

---

### `class PKCS8EncodedKeySpec`

- **Extends:** `java.security.spec.EncodedKeySpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PKCS8EncodedKeySpec(byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final String getFormat()` |  |

---

### `class PSSParameterSpec`

- **Implements:** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PSSParameterSpec(String, String, java.security.spec.AlgorithmParameterSpec, int, int)` |  |
| `PSSParameterSpec(int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.security.spec.PSSParameterSpec DEFAULT` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getDigestAlgorithm()` |  |
| `String getMGFAlgorithm()` |  |
| `java.security.spec.AlgorithmParameterSpec getMGFParameters()` |  |
| `int getSaltLength()` |  |
| `int getTrailerField()` |  |

---

### `class RSAKeyGenParameterSpec`

- **Implements:** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RSAKeyGenParameterSpec(int, java.math.BigInteger)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.math.BigInteger F0` |  |
| `static final java.math.BigInteger F4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getKeysize()` |  |
| `java.math.BigInteger getPublicExponent()` |  |

---

### `class RSAMultiPrimePrivateCrtKeySpec`

- **Extends:** `java.security.spec.RSAPrivateKeySpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RSAMultiPrimePrivateCrtKeySpec(java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.security.spec.RSAOtherPrimeInfo[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getCrtCoefficient()` |  |
| `java.security.spec.RSAOtherPrimeInfo[] getOtherPrimeInfo()` |  |
| `java.math.BigInteger getPrimeExponentP()` |  |
| `java.math.BigInteger getPrimeExponentQ()` |  |
| `java.math.BigInteger getPrimeP()` |  |
| `java.math.BigInteger getPrimeQ()` |  |
| `java.math.BigInteger getPublicExponent()` |  |

---

### `class RSAOtherPrimeInfo`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RSAOtherPrimeInfo(java.math.BigInteger, java.math.BigInteger, java.math.BigInteger)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.math.BigInteger getCrtCoefficient()` |  |
| `final java.math.BigInteger getExponent()` |  |
| `final java.math.BigInteger getPrime()` |  |

---

### `class RSAPrivateCrtKeySpec`

- **Extends:** `java.security.spec.RSAPrivateKeySpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RSAPrivateCrtKeySpec(java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getCrtCoefficient()` |  |
| `java.math.BigInteger getPrimeExponentP()` |  |
| `java.math.BigInteger getPrimeExponentQ()` |  |
| `java.math.BigInteger getPrimeP()` |  |
| `java.math.BigInteger getPrimeQ()` |  |
| `java.math.BigInteger getPublicExponent()` |  |

---

### `class RSAPrivateKeySpec`

- **Implements:** `java.security.spec.KeySpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RSAPrivateKeySpec(java.math.BigInteger, java.math.BigInteger)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getModulus()` |  |
| `java.math.BigInteger getPrivateExponent()` |  |

---

### `class RSAPublicKeySpec`

- **Implements:** `java.security.spec.KeySpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RSAPublicKeySpec(java.math.BigInteger, java.math.BigInteger)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getModulus()` |  |
| `java.math.BigInteger getPublicExponent()` |  |

---

### `class X509EncodedKeySpec`

- **Extends:** `java.security.spec.EncodedKeySpec`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `X509EncodedKeySpec(byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final String getFormat()` |  |

---

## Package: `java.sql`

### `interface Array`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void free() throws java.sql.SQLException` |  |
| `Object getArray() throws java.sql.SQLException` |  |
| `Object getArray(java.util.Map<java.lang.String,java.lang.Class<?>>) throws java.sql.SQLException` |  |
| `Object getArray(long, int) throws java.sql.SQLException` |  |
| `Object getArray(long, int, java.util.Map<java.lang.String,java.lang.Class<?>>) throws java.sql.SQLException` |  |
| `int getBaseType() throws java.sql.SQLException` |  |
| `String getBaseTypeName() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getResultSet() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getResultSet(java.util.Map<java.lang.String,java.lang.Class<?>>) throws java.sql.SQLException` |  |
| `java.sql.ResultSet getResultSet(long, int) throws java.sql.SQLException` |  |
| `java.sql.ResultSet getResultSet(long, int, java.util.Map<java.lang.String,java.lang.Class<?>>) throws java.sql.SQLException` |  |

---

### `class BatchUpdateException`

- **Extends:** `java.sql.SQLException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BatchUpdateException(String, String, int, int[])` |  |
| `BatchUpdateException(String, String, int[])` |  |
| `BatchUpdateException(String, int[])` |  |
| `BatchUpdateException(int[])` |  |
| `BatchUpdateException()` |  |
| `BatchUpdateException(Throwable)` |  |
| `BatchUpdateException(int[], Throwable)` |  |
| `BatchUpdateException(String, int[], Throwable)` |  |
| `BatchUpdateException(String, String, int[], Throwable)` |  |
| `BatchUpdateException(String, String, int, int[], Throwable)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int[] getUpdateCounts()` |  |

---

### `interface Blob`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void free() throws java.sql.SQLException` |  |
| `java.io.InputStream getBinaryStream() throws java.sql.SQLException` |  |
| `java.io.InputStream getBinaryStream(long, long) throws java.sql.SQLException` |  |
| `byte[] getBytes(long, int) throws java.sql.SQLException` |  |
| `long length() throws java.sql.SQLException` |  |
| `long position(byte[], long) throws java.sql.SQLException` |  |
| `long position(java.sql.Blob, long) throws java.sql.SQLException` |  |
| `java.io.OutputStream setBinaryStream(long) throws java.sql.SQLException` |  |
| `int setBytes(long, byte[]) throws java.sql.SQLException` |  |
| `int setBytes(long, byte[], int, int) throws java.sql.SQLException` |  |
| `void truncate(long) throws java.sql.SQLException` |  |

---

### `interface CallableStatement`

- **Extends:** `java.sql.PreparedStatement`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.sql.Array getArray(int) throws java.sql.SQLException` |  |
| `java.sql.Array getArray(String) throws java.sql.SQLException` |  |
| `java.math.BigDecimal getBigDecimal(int) throws java.sql.SQLException` |  |
| `java.math.BigDecimal getBigDecimal(String) throws java.sql.SQLException` |  |
| `java.sql.Blob getBlob(int) throws java.sql.SQLException` |  |
| `java.sql.Blob getBlob(String) throws java.sql.SQLException` |  |
| `boolean getBoolean(int) throws java.sql.SQLException` |  |
| `boolean getBoolean(String) throws java.sql.SQLException` |  |
| `byte getByte(int) throws java.sql.SQLException` |  |
| `byte getByte(String) throws java.sql.SQLException` |  |
| `byte[] getBytes(int) throws java.sql.SQLException` |  |
| `byte[] getBytes(String) throws java.sql.SQLException` |  |
| `java.io.Reader getCharacterStream(int) throws java.sql.SQLException` |  |
| `java.io.Reader getCharacterStream(String) throws java.sql.SQLException` |  |
| `java.sql.Clob getClob(int) throws java.sql.SQLException` |  |
| `java.sql.Clob getClob(String) throws java.sql.SQLException` |  |
| `java.sql.Date getDate(int) throws java.sql.SQLException` |  |
| `java.sql.Date getDate(int, java.util.Calendar) throws java.sql.SQLException` |  |
| `java.sql.Date getDate(String) throws java.sql.SQLException` |  |
| `java.sql.Date getDate(String, java.util.Calendar) throws java.sql.SQLException` |  |
| `double getDouble(int) throws java.sql.SQLException` |  |
| `double getDouble(String) throws java.sql.SQLException` |  |
| `float getFloat(int) throws java.sql.SQLException` |  |
| `float getFloat(String) throws java.sql.SQLException` |  |
| `int getInt(int) throws java.sql.SQLException` |  |
| `int getInt(String) throws java.sql.SQLException` |  |
| `long getLong(int) throws java.sql.SQLException` |  |
| `long getLong(String) throws java.sql.SQLException` |  |
| `java.io.Reader getNCharacterStream(int) throws java.sql.SQLException` |  |
| `java.io.Reader getNCharacterStream(String) throws java.sql.SQLException` |  |
| `java.sql.NClob getNClob(int) throws java.sql.SQLException` |  |
| `java.sql.NClob getNClob(String) throws java.sql.SQLException` |  |
| `String getNString(int) throws java.sql.SQLException` |  |
| `String getNString(String) throws java.sql.SQLException` |  |
| `Object getObject(int) throws java.sql.SQLException` |  |
| `Object getObject(int, java.util.Map<java.lang.String,java.lang.Class<?>>) throws java.sql.SQLException` |  |
| `Object getObject(String) throws java.sql.SQLException` |  |
| `Object getObject(String, java.util.Map<java.lang.String,java.lang.Class<?>>) throws java.sql.SQLException` |  |
| `java.sql.Ref getRef(int) throws java.sql.SQLException` |  |
| `java.sql.Ref getRef(String) throws java.sql.SQLException` |  |
| `java.sql.RowId getRowId(int) throws java.sql.SQLException` |  |
| `java.sql.RowId getRowId(String) throws java.sql.SQLException` |  |
| `java.sql.SQLXML getSQLXML(int) throws java.sql.SQLException` |  |
| `java.sql.SQLXML getSQLXML(String) throws java.sql.SQLException` |  |
| `short getShort(int) throws java.sql.SQLException` |  |
| `short getShort(String) throws java.sql.SQLException` |  |
| `String getString(int) throws java.sql.SQLException` |  |
| `String getString(String) throws java.sql.SQLException` |  |
| `java.sql.Time getTime(int) throws java.sql.SQLException` |  |
| `java.sql.Time getTime(int, java.util.Calendar) throws java.sql.SQLException` |  |
| `java.sql.Time getTime(String) throws java.sql.SQLException` |  |
| `java.sql.Time getTime(String, java.util.Calendar) throws java.sql.SQLException` |  |
| `java.sql.Timestamp getTimestamp(int) throws java.sql.SQLException` |  |
| `java.sql.Timestamp getTimestamp(int, java.util.Calendar) throws java.sql.SQLException` |  |
| `java.sql.Timestamp getTimestamp(String) throws java.sql.SQLException` |  |
| `java.sql.Timestamp getTimestamp(String, java.util.Calendar) throws java.sql.SQLException` |  |
| `java.net.URL getURL(int) throws java.sql.SQLException` |  |
| `java.net.URL getURL(String) throws java.sql.SQLException` |  |
| `void registerOutParameter(int, int) throws java.sql.SQLException` |  |
| `void registerOutParameter(int, int, int) throws java.sql.SQLException` |  |
| `void registerOutParameter(int, int, String) throws java.sql.SQLException` |  |
| `void registerOutParameter(String, int) throws java.sql.SQLException` |  |
| `void registerOutParameter(String, int, int) throws java.sql.SQLException` |  |
| `void registerOutParameter(String, int, String) throws java.sql.SQLException` |  |
| `void setAsciiStream(String, java.io.InputStream, int) throws java.sql.SQLException` |  |
| `void setAsciiStream(String, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void setAsciiStream(String, java.io.InputStream) throws java.sql.SQLException` |  |
| `void setBigDecimal(String, java.math.BigDecimal) throws java.sql.SQLException` |  |
| `void setBinaryStream(String, java.io.InputStream, int) throws java.sql.SQLException` |  |
| `void setBinaryStream(String, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void setBinaryStream(String, java.io.InputStream) throws java.sql.SQLException` |  |
| `void setBlob(String, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void setBlob(String, java.sql.Blob) throws java.sql.SQLException` |  |
| `void setBlob(String, java.io.InputStream) throws java.sql.SQLException` |  |
| `void setBoolean(String, boolean) throws java.sql.SQLException` |  |
| `void setByte(String, byte) throws java.sql.SQLException` |  |
| `void setBytes(String, byte[]) throws java.sql.SQLException` |  |
| `void setCharacterStream(String, java.io.Reader, int) throws java.sql.SQLException` |  |
| `void setCharacterStream(String, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setCharacterStream(String, java.io.Reader) throws java.sql.SQLException` |  |
| `void setClob(String, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setClob(String, java.sql.Clob) throws java.sql.SQLException` |  |
| `void setClob(String, java.io.Reader) throws java.sql.SQLException` |  |
| `void setDate(String, java.sql.Date) throws java.sql.SQLException` |  |
| `void setDate(String, java.sql.Date, java.util.Calendar) throws java.sql.SQLException` |  |
| `void setDouble(String, double) throws java.sql.SQLException` |  |
| `void setFloat(String, float) throws java.sql.SQLException` |  |
| `void setInt(String, int) throws java.sql.SQLException` |  |
| `void setLong(String, long) throws java.sql.SQLException` |  |
| `void setNCharacterStream(String, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setNCharacterStream(String, java.io.Reader) throws java.sql.SQLException` |  |
| `void setNClob(String, java.sql.NClob) throws java.sql.SQLException` |  |
| `void setNClob(String, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setNClob(String, java.io.Reader) throws java.sql.SQLException` |  |
| `void setNString(String, String) throws java.sql.SQLException` |  |
| `void setNull(String, int) throws java.sql.SQLException` |  |
| `void setNull(String, int, String) throws java.sql.SQLException` |  |
| `void setObject(String, Object, int, int) throws java.sql.SQLException` |  |
| `void setObject(String, Object, int) throws java.sql.SQLException` |  |
| `void setObject(String, Object) throws java.sql.SQLException` |  |
| `void setRowId(String, java.sql.RowId) throws java.sql.SQLException` |  |
| `void setSQLXML(String, java.sql.SQLXML) throws java.sql.SQLException` |  |
| `void setShort(String, short) throws java.sql.SQLException` |  |
| `void setString(String, String) throws java.sql.SQLException` |  |
| `void setTime(String, java.sql.Time) throws java.sql.SQLException` |  |
| `void setTime(String, java.sql.Time, java.util.Calendar) throws java.sql.SQLException` |  |
| `void setTimestamp(String, java.sql.Timestamp) throws java.sql.SQLException` |  |
| `void setTimestamp(String, java.sql.Timestamp, java.util.Calendar) throws java.sql.SQLException` |  |
| `void setURL(String, java.net.URL) throws java.sql.SQLException` |  |
| `boolean wasNull() throws java.sql.SQLException` |  |

---

### `enum ClientInfoStatus`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.sql.ClientInfoStatus REASON_UNKNOWN` |  |
| `static final java.sql.ClientInfoStatus REASON_UNKNOWN_PROPERTY` |  |
| `static final java.sql.ClientInfoStatus REASON_VALUE_INVALID` |  |
| `static final java.sql.ClientInfoStatus REASON_VALUE_TRUNCATED` |  |

---

### `interface Clob`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void free() throws java.sql.SQLException` |  |
| `java.io.InputStream getAsciiStream() throws java.sql.SQLException` |  |
| `java.io.Reader getCharacterStream() throws java.sql.SQLException` |  |
| `java.io.Reader getCharacterStream(long, long) throws java.sql.SQLException` |  |
| `String getSubString(long, int) throws java.sql.SQLException` |  |
| `long length() throws java.sql.SQLException` |  |
| `long position(String, long) throws java.sql.SQLException` |  |
| `long position(java.sql.Clob, long) throws java.sql.SQLException` |  |
| `java.io.OutputStream setAsciiStream(long) throws java.sql.SQLException` |  |
| `java.io.Writer setCharacterStream(long) throws java.sql.SQLException` |  |
| `int setString(long, String) throws java.sql.SQLException` |  |
| `int setString(long, String, int, int) throws java.sql.SQLException` |  |
| `void truncate(long) throws java.sql.SQLException` |  |

---

### `interface Connection`

- **Extends:** `java.sql.Wrapper java.lang.AutoCloseable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TRANSACTION_NONE = 0` |  |
| `static final int TRANSACTION_READ_COMMITTED = 2` |  |
| `static final int TRANSACTION_READ_UNCOMMITTED = 1` |  |
| `static final int TRANSACTION_REPEATABLE_READ = 4` |  |
| `static final int TRANSACTION_SERIALIZABLE = 8` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clearWarnings() throws java.sql.SQLException` |  |
| `void close() throws java.sql.SQLException` |  |
| `void commit() throws java.sql.SQLException` |  |
| `java.sql.Array createArrayOf(String, Object[]) throws java.sql.SQLException` |  |
| `java.sql.Blob createBlob() throws java.sql.SQLException` |  |
| `java.sql.Clob createClob() throws java.sql.SQLException` |  |
| `java.sql.NClob createNClob() throws java.sql.SQLException` |  |
| `java.sql.SQLXML createSQLXML() throws java.sql.SQLException` |  |
| `java.sql.Statement createStatement() throws java.sql.SQLException` |  |
| `java.sql.Statement createStatement(int, int) throws java.sql.SQLException` |  |
| `java.sql.Statement createStatement(int, int, int) throws java.sql.SQLException` |  |
| `java.sql.Struct createStruct(String, Object[]) throws java.sql.SQLException` |  |
| `boolean getAutoCommit() throws java.sql.SQLException` |  |
| `String getCatalog() throws java.sql.SQLException` |  |
| `String getClientInfo(String) throws java.sql.SQLException` |  |
| `java.util.Properties getClientInfo() throws java.sql.SQLException` |  |
| `int getHoldability() throws java.sql.SQLException` |  |
| `java.sql.DatabaseMetaData getMetaData() throws java.sql.SQLException` |  |
| `int getTransactionIsolation() throws java.sql.SQLException` |  |
| `java.util.Map<java.lang.String,java.lang.Class<?>> getTypeMap() throws java.sql.SQLException` |  |
| `java.sql.SQLWarning getWarnings() throws java.sql.SQLException` |  |
| `boolean isClosed() throws java.sql.SQLException` |  |
| `boolean isReadOnly() throws java.sql.SQLException` |  |
| `boolean isValid(int) throws java.sql.SQLException` |  |
| `String nativeSQL(String) throws java.sql.SQLException` |  |
| `java.sql.CallableStatement prepareCall(String) throws java.sql.SQLException` |  |
| `java.sql.CallableStatement prepareCall(String, int, int) throws java.sql.SQLException` |  |
| `java.sql.CallableStatement prepareCall(String, int, int, int) throws java.sql.SQLException` |  |
| `java.sql.PreparedStatement prepareStatement(String) throws java.sql.SQLException` |  |
| `java.sql.PreparedStatement prepareStatement(String, int, int) throws java.sql.SQLException` |  |
| `java.sql.PreparedStatement prepareStatement(String, int, int, int) throws java.sql.SQLException` |  |
| `java.sql.PreparedStatement prepareStatement(String, int) throws java.sql.SQLException` |  |
| `java.sql.PreparedStatement prepareStatement(String, int[]) throws java.sql.SQLException` |  |
| `java.sql.PreparedStatement prepareStatement(String, String[]) throws java.sql.SQLException` |  |
| `void releaseSavepoint(java.sql.Savepoint) throws java.sql.SQLException` |  |
| `void rollback() throws java.sql.SQLException` |  |
| `void rollback(java.sql.Savepoint) throws java.sql.SQLException` |  |
| `void setAutoCommit(boolean) throws java.sql.SQLException` |  |
| `void setCatalog(String) throws java.sql.SQLException` |  |
| `void setClientInfo(String, String) throws java.sql.SQLClientInfoException` |  |
| `void setClientInfo(java.util.Properties) throws java.sql.SQLClientInfoException` |  |
| `void setHoldability(int) throws java.sql.SQLException` |  |
| `void setReadOnly(boolean) throws java.sql.SQLException` |  |
| `java.sql.Savepoint setSavepoint() throws java.sql.SQLException` |  |
| `java.sql.Savepoint setSavepoint(String) throws java.sql.SQLException` |  |
| `void setTransactionIsolation(int) throws java.sql.SQLException` |  |
| `void setTypeMap(java.util.Map<java.lang.String,java.lang.Class<?>>) throws java.sql.SQLException` |  |

---

### `class DataTruncation`

- **Extends:** `java.sql.SQLWarning`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DataTruncation(int, boolean, boolean, int, int)` |  |
| `DataTruncation(int, boolean, boolean, int, int, Throwable)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getDataSize()` |  |
| `int getIndex()` |  |
| `boolean getParameter()` |  |
| `boolean getRead()` |  |
| `int getTransferSize()` |  |

---

### `interface DatabaseMetaData`

- **Extends:** `java.sql.Wrapper`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final short attributeNoNulls = 0` |  |
| `static final short attributeNullable = 1` |  |
| `static final short attributeNullableUnknown = 2` |  |
| `static final int bestRowNotPseudo = 1` |  |
| `static final int bestRowPseudo = 2` |  |
| `static final int bestRowSession = 2` |  |
| `static final int bestRowTemporary = 0` |  |
| `static final int bestRowTransaction = 1` |  |
| `static final int bestRowUnknown = 0` |  |
| `static final int columnNoNulls = 0` |  |
| `static final int columnNullable = 1` |  |
| `static final int columnNullableUnknown = 2` |  |
| `static final int functionColumnIn = 1` |  |
| `static final int functionColumnInOut = 2` |  |
| `static final int functionColumnOut = 3` |  |
| `static final int functionColumnResult = 5` |  |
| `static final int functionColumnUnknown = 0` |  |
| `static final int functionNoNulls = 0` |  |
| `static final int functionNoTable = 1` |  |
| `static final int functionNullable = 1` |  |
| `static final int functionNullableUnknown = 2` |  |
| `static final int functionResultUnknown = 0` |  |
| `static final int functionReturn = 4` |  |
| `static final int functionReturnsTable = 2` |  |
| `static final int importedKeyCascade = 0` |  |
| `static final int importedKeyInitiallyDeferred = 5` |  |
| `static final int importedKeyInitiallyImmediate = 6` |  |
| `static final int importedKeyNoAction = 3` |  |
| `static final int importedKeyNotDeferrable = 7` |  |
| `static final int importedKeyRestrict = 1` |  |
| `static final int importedKeySetDefault = 4` |  |
| `static final int importedKeySetNull = 2` |  |
| `static final int procedureColumnIn = 1` |  |
| `static final int procedureColumnInOut = 2` |  |
| `static final int procedureColumnOut = 4` |  |
| `static final int procedureColumnResult = 3` |  |
| `static final int procedureColumnReturn = 5` |  |
| `static final int procedureColumnUnknown = 0` |  |
| `static final int procedureNoNulls = 0` |  |
| `static final int procedureNoResult = 1` |  |
| `static final int procedureNullable = 1` |  |
| `static final int procedureNullableUnknown = 2` |  |
| `static final int procedureResultUnknown = 0` |  |
| `static final int procedureReturnsResult = 2` |  |
| `static final int sqlStateSQL = 2` |  |
| `static final int sqlStateSQL99 = 2` |  |
| `static final int sqlStateXOpen = 1` |  |
| `static final short tableIndexClustered = 1` |  |
| `static final short tableIndexHashed = 2` |  |
| `static final short tableIndexOther = 3` |  |
| `static final short tableIndexStatistic = 0` |  |
| `static final int typeNoNulls = 0` |  |
| `static final int typeNullable = 1` |  |
| `static final int typeNullableUnknown = 2` |  |
| `static final int typePredBasic = 2` |  |
| `static final int typePredChar = 1` |  |
| `static final int typePredNone = 0` |  |
| `static final int typeSearchable = 3` |  |
| `static final int versionColumnNotPseudo = 1` |  |
| `static final int versionColumnPseudo = 2` |  |
| `static final int versionColumnUnknown = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean allProceduresAreCallable() throws java.sql.SQLException` |  |
| `boolean allTablesAreSelectable() throws java.sql.SQLException` |  |
| `boolean autoCommitFailureClosesAllResultSets() throws java.sql.SQLException` |  |
| `boolean dataDefinitionCausesTransactionCommit() throws java.sql.SQLException` |  |
| `boolean dataDefinitionIgnoredInTransactions() throws java.sql.SQLException` |  |
| `boolean deletesAreDetected(int) throws java.sql.SQLException` |  |
| `boolean doesMaxRowSizeIncludeBlobs() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getAttributes(String, String, String, String) throws java.sql.SQLException` |  |
| `java.sql.ResultSet getBestRowIdentifier(String, String, String, int, boolean) throws java.sql.SQLException` |  |
| `String getCatalogSeparator() throws java.sql.SQLException` |  |
| `String getCatalogTerm() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getCatalogs() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getClientInfoProperties() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getColumnPrivileges(String, String, String, String) throws java.sql.SQLException` |  |
| `java.sql.ResultSet getColumns(String, String, String, String) throws java.sql.SQLException` |  |
| `java.sql.Connection getConnection() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getCrossReference(String, String, String, String, String, String) throws java.sql.SQLException` |  |
| `int getDatabaseMajorVersion() throws java.sql.SQLException` |  |
| `int getDatabaseMinorVersion() throws java.sql.SQLException` |  |
| `String getDatabaseProductName() throws java.sql.SQLException` |  |
| `String getDatabaseProductVersion() throws java.sql.SQLException` |  |
| `int getDefaultTransactionIsolation() throws java.sql.SQLException` |  |
| `int getDriverMajorVersion()` |  |
| `int getDriverMinorVersion()` |  |
| `String getDriverName() throws java.sql.SQLException` |  |
| `String getDriverVersion() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getExportedKeys(String, String, String) throws java.sql.SQLException` |  |
| `String getExtraNameCharacters() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getFunctionColumns(String, String, String, String) throws java.sql.SQLException` |  |
| `java.sql.ResultSet getFunctions(String, String, String) throws java.sql.SQLException` |  |
| `String getIdentifierQuoteString() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getImportedKeys(String, String, String) throws java.sql.SQLException` |  |
| `java.sql.ResultSet getIndexInfo(String, String, String, boolean, boolean) throws java.sql.SQLException` |  |
| `int getJDBCMajorVersion() throws java.sql.SQLException` |  |
| `int getJDBCMinorVersion() throws java.sql.SQLException` |  |
| `int getMaxBinaryLiteralLength() throws java.sql.SQLException` |  |
| `int getMaxCatalogNameLength() throws java.sql.SQLException` |  |
| `int getMaxCharLiteralLength() throws java.sql.SQLException` |  |
| `int getMaxColumnNameLength() throws java.sql.SQLException` |  |
| `int getMaxColumnsInGroupBy() throws java.sql.SQLException` |  |
| `int getMaxColumnsInIndex() throws java.sql.SQLException` |  |
| `int getMaxColumnsInOrderBy() throws java.sql.SQLException` |  |
| `int getMaxColumnsInSelect() throws java.sql.SQLException` |  |
| `int getMaxColumnsInTable() throws java.sql.SQLException` |  |
| `int getMaxConnections() throws java.sql.SQLException` |  |
| `int getMaxCursorNameLength() throws java.sql.SQLException` |  |
| `int getMaxIndexLength() throws java.sql.SQLException` |  |
| `int getMaxProcedureNameLength() throws java.sql.SQLException` |  |
| `int getMaxRowSize() throws java.sql.SQLException` |  |
| `int getMaxSchemaNameLength() throws java.sql.SQLException` |  |
| `int getMaxStatementLength() throws java.sql.SQLException` |  |
| `int getMaxStatements() throws java.sql.SQLException` |  |
| `int getMaxTableNameLength() throws java.sql.SQLException` |  |
| `int getMaxTablesInSelect() throws java.sql.SQLException` |  |
| `int getMaxUserNameLength() throws java.sql.SQLException` |  |
| `String getNumericFunctions() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getPrimaryKeys(String, String, String) throws java.sql.SQLException` |  |
| `java.sql.ResultSet getProcedureColumns(String, String, String, String) throws java.sql.SQLException` |  |
| `String getProcedureTerm() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getProcedures(String, String, String) throws java.sql.SQLException` |  |
| `int getResultSetHoldability() throws java.sql.SQLException` |  |
| `java.sql.RowIdLifetime getRowIdLifetime() throws java.sql.SQLException` |  |
| `String getSQLKeywords() throws java.sql.SQLException` |  |
| `int getSQLStateType() throws java.sql.SQLException` |  |
| `String getSchemaTerm() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getSchemas() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getSchemas(String, String) throws java.sql.SQLException` |  |
| `String getSearchStringEscape() throws java.sql.SQLException` |  |
| `String getStringFunctions() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getSuperTables(String, String, String) throws java.sql.SQLException` |  |
| `java.sql.ResultSet getSuperTypes(String, String, String) throws java.sql.SQLException` |  |
| `String getSystemFunctions() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getTablePrivileges(String, String, String) throws java.sql.SQLException` |  |
| `java.sql.ResultSet getTableTypes() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getTables(String, String, String, String[]) throws java.sql.SQLException` |  |
| `String getTimeDateFunctions() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getTypeInfo() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getUDTs(String, String, String, int[]) throws java.sql.SQLException` |  |
| `String getURL() throws java.sql.SQLException` |  |
| `String getUserName() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getVersionColumns(String, String, String) throws java.sql.SQLException` |  |
| `boolean insertsAreDetected(int) throws java.sql.SQLException` |  |
| `boolean isCatalogAtStart() throws java.sql.SQLException` |  |
| `boolean isReadOnly() throws java.sql.SQLException` |  |
| `boolean locatorsUpdateCopy() throws java.sql.SQLException` |  |
| `boolean nullPlusNonNullIsNull() throws java.sql.SQLException` |  |
| `boolean nullsAreSortedAtEnd() throws java.sql.SQLException` |  |
| `boolean nullsAreSortedAtStart() throws java.sql.SQLException` |  |
| `boolean nullsAreSortedHigh() throws java.sql.SQLException` |  |
| `boolean nullsAreSortedLow() throws java.sql.SQLException` |  |
| `boolean othersDeletesAreVisible(int) throws java.sql.SQLException` |  |
| `boolean othersInsertsAreVisible(int) throws java.sql.SQLException` |  |
| `boolean othersUpdatesAreVisible(int) throws java.sql.SQLException` |  |
| `boolean ownDeletesAreVisible(int) throws java.sql.SQLException` |  |
| `boolean ownInsertsAreVisible(int) throws java.sql.SQLException` |  |
| `boolean ownUpdatesAreVisible(int) throws java.sql.SQLException` |  |
| `boolean storesLowerCaseIdentifiers() throws java.sql.SQLException` |  |
| `boolean storesLowerCaseQuotedIdentifiers() throws java.sql.SQLException` |  |
| `boolean storesMixedCaseIdentifiers() throws java.sql.SQLException` |  |
| `boolean storesMixedCaseQuotedIdentifiers() throws java.sql.SQLException` |  |
| `boolean storesUpperCaseIdentifiers() throws java.sql.SQLException` |  |
| `boolean storesUpperCaseQuotedIdentifiers() throws java.sql.SQLException` |  |
| `boolean supportsANSI92EntryLevelSQL() throws java.sql.SQLException` |  |
| `boolean supportsANSI92FullSQL() throws java.sql.SQLException` |  |
| `boolean supportsANSI92IntermediateSQL() throws java.sql.SQLException` |  |
| `boolean supportsAlterTableWithAddColumn() throws java.sql.SQLException` |  |
| `boolean supportsAlterTableWithDropColumn() throws java.sql.SQLException` |  |
| `boolean supportsBatchUpdates() throws java.sql.SQLException` |  |
| `boolean supportsCatalogsInDataManipulation() throws java.sql.SQLException` |  |
| `boolean supportsCatalogsInIndexDefinitions() throws java.sql.SQLException` |  |
| `boolean supportsCatalogsInPrivilegeDefinitions() throws java.sql.SQLException` |  |
| `boolean supportsCatalogsInProcedureCalls() throws java.sql.SQLException` |  |
| `boolean supportsCatalogsInTableDefinitions() throws java.sql.SQLException` |  |
| `boolean supportsColumnAliasing() throws java.sql.SQLException` |  |
| `boolean supportsConvert() throws java.sql.SQLException` |  |
| `boolean supportsConvert(int, int) throws java.sql.SQLException` |  |
| `boolean supportsCoreSQLGrammar() throws java.sql.SQLException` |  |
| `boolean supportsCorrelatedSubqueries() throws java.sql.SQLException` |  |
| `boolean supportsDataDefinitionAndDataManipulationTransactions() throws java.sql.SQLException` |  |
| `boolean supportsDataManipulationTransactionsOnly() throws java.sql.SQLException` |  |
| `boolean supportsDifferentTableCorrelationNames() throws java.sql.SQLException` |  |
| `boolean supportsExpressionsInOrderBy() throws java.sql.SQLException` |  |
| `boolean supportsExtendedSQLGrammar() throws java.sql.SQLException` |  |
| `boolean supportsFullOuterJoins() throws java.sql.SQLException` |  |
| `boolean supportsGetGeneratedKeys() throws java.sql.SQLException` |  |
| `boolean supportsGroupBy() throws java.sql.SQLException` |  |
| `boolean supportsGroupByBeyondSelect() throws java.sql.SQLException` |  |
| `boolean supportsGroupByUnrelated() throws java.sql.SQLException` |  |
| `boolean supportsIntegrityEnhancementFacility() throws java.sql.SQLException` |  |
| `boolean supportsLikeEscapeClause() throws java.sql.SQLException` |  |
| `boolean supportsLimitedOuterJoins() throws java.sql.SQLException` |  |
| `boolean supportsMinimumSQLGrammar() throws java.sql.SQLException` |  |
| `boolean supportsMixedCaseIdentifiers() throws java.sql.SQLException` |  |
| `boolean supportsMixedCaseQuotedIdentifiers() throws java.sql.SQLException` |  |
| `boolean supportsMultipleOpenResults() throws java.sql.SQLException` |  |
| `boolean supportsMultipleResultSets() throws java.sql.SQLException` |  |
| `boolean supportsMultipleTransactions() throws java.sql.SQLException` |  |
| `boolean supportsNamedParameters() throws java.sql.SQLException` |  |
| `boolean supportsNonNullableColumns() throws java.sql.SQLException` |  |
| `boolean supportsOpenCursorsAcrossCommit() throws java.sql.SQLException` |  |
| `boolean supportsOpenCursorsAcrossRollback() throws java.sql.SQLException` |  |
| `boolean supportsOpenStatementsAcrossCommit() throws java.sql.SQLException` |  |
| `boolean supportsOpenStatementsAcrossRollback() throws java.sql.SQLException` |  |
| `boolean supportsOrderByUnrelated() throws java.sql.SQLException` |  |
| `boolean supportsOuterJoins() throws java.sql.SQLException` |  |
| `boolean supportsPositionedDelete() throws java.sql.SQLException` |  |
| `boolean supportsPositionedUpdate() throws java.sql.SQLException` |  |
| `boolean supportsResultSetConcurrency(int, int) throws java.sql.SQLException` |  |
| `boolean supportsResultSetHoldability(int) throws java.sql.SQLException` |  |
| `boolean supportsResultSetType(int) throws java.sql.SQLException` |  |
| `boolean supportsSavepoints() throws java.sql.SQLException` |  |
| `boolean supportsSchemasInDataManipulation() throws java.sql.SQLException` |  |
| `boolean supportsSchemasInIndexDefinitions() throws java.sql.SQLException` |  |
| `boolean supportsSchemasInPrivilegeDefinitions() throws java.sql.SQLException` |  |
| `boolean supportsSchemasInProcedureCalls() throws java.sql.SQLException` |  |
| `boolean supportsSchemasInTableDefinitions() throws java.sql.SQLException` |  |
| `boolean supportsSelectForUpdate() throws java.sql.SQLException` |  |
| `boolean supportsStatementPooling() throws java.sql.SQLException` |  |
| `boolean supportsStoredFunctionsUsingCallSyntax() throws java.sql.SQLException` |  |
| `boolean supportsStoredProcedures() throws java.sql.SQLException` |  |
| `boolean supportsSubqueriesInComparisons() throws java.sql.SQLException` |  |
| `boolean supportsSubqueriesInExists() throws java.sql.SQLException` |  |
| `boolean supportsSubqueriesInIns() throws java.sql.SQLException` |  |
| `boolean supportsSubqueriesInQuantifieds() throws java.sql.SQLException` |  |
| `boolean supportsTableCorrelationNames() throws java.sql.SQLException` |  |
| `boolean supportsTransactionIsolationLevel(int) throws java.sql.SQLException` |  |
| `boolean supportsTransactions() throws java.sql.SQLException` |  |
| `boolean supportsUnion() throws java.sql.SQLException` |  |
| `boolean supportsUnionAll() throws java.sql.SQLException` |  |
| `boolean updatesAreDetected(int) throws java.sql.SQLException` |  |
| `boolean usesLocalFilePerTable() throws java.sql.SQLException` |  |
| `boolean usesLocalFiles() throws java.sql.SQLException` |  |

---

### `class Date`

- **Extends:** `java.util.Date`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Date(long)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.sql.Date valueOf(String)` |  |

---

### `interface Driver`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean acceptsURL(String) throws java.sql.SQLException` |  |
| `java.sql.Connection connect(String, java.util.Properties) throws java.sql.SQLException` |  |
| `int getMajorVersion()` |  |
| `int getMinorVersion()` |  |
| `java.sql.DriverPropertyInfo[] getPropertyInfo(String, java.util.Properties) throws java.sql.SQLException` |  |
| `boolean jdbcCompliant()` |  |

---

### `class DriverManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static void deregisterDriver(java.sql.Driver) throws java.sql.SQLException` |  |
| `static java.sql.Connection getConnection(String, java.util.Properties) throws java.sql.SQLException` |  |
| `static java.sql.Connection getConnection(String, String, String) throws java.sql.SQLException` |  |
| `static java.sql.Connection getConnection(String) throws java.sql.SQLException` |  |
| `static java.sql.Driver getDriver(String) throws java.sql.SQLException` |  |
| `static java.util.Enumeration<java.sql.Driver> getDrivers()` |  |
| `static java.io.PrintWriter getLogWriter()` |  |
| `static int getLoginTimeout()` |  |
| `static void println(String)` |  |
| `static void registerDriver(java.sql.Driver) throws java.sql.SQLException` |  |
| `static void setLogWriter(java.io.PrintWriter)` |  |
| `static void setLoginTimeout(int)` |  |

---

### `class DriverPropertyInfo`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DriverPropertyInfo(String, String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `String[] choices` |  |
| `String description` |  |
| `String name` |  |
| `boolean required` |  |
| `String value` |  |

---

### `interface NClob`

- **Extends:** `java.sql.Clob`

---

### `interface ParameterMetaData`

- **Extends:** `java.sql.Wrapper`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int parameterModeIn = 1` |  |
| `static final int parameterModeInOut = 2` |  |
| `static final int parameterModeOut = 4` |  |
| `static final int parameterModeUnknown = 0` |  |
| `static final int parameterNoNulls = 0` |  |
| `static final int parameterNullable = 1` |  |
| `static final int parameterNullableUnknown = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getParameterClassName(int) throws java.sql.SQLException` |  |
| `int getParameterCount() throws java.sql.SQLException` |  |
| `int getParameterMode(int) throws java.sql.SQLException` |  |
| `int getParameterType(int) throws java.sql.SQLException` |  |
| `String getParameterTypeName(int) throws java.sql.SQLException` |  |
| `int getPrecision(int) throws java.sql.SQLException` |  |
| `int getScale(int) throws java.sql.SQLException` |  |
| `int isNullable(int) throws java.sql.SQLException` |  |
| `boolean isSigned(int) throws java.sql.SQLException` |  |

---

### `interface PreparedStatement`

- **Extends:** `java.sql.Statement`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addBatch() throws java.sql.SQLException` |  |
| `void clearParameters() throws java.sql.SQLException` |  |
| `boolean execute() throws java.sql.SQLException` |  |
| `java.sql.ResultSet executeQuery() throws java.sql.SQLException` |  |
| `int executeUpdate() throws java.sql.SQLException` |  |
| `java.sql.ResultSetMetaData getMetaData() throws java.sql.SQLException` |  |
| `java.sql.ParameterMetaData getParameterMetaData() throws java.sql.SQLException` |  |
| `void setArray(int, java.sql.Array) throws java.sql.SQLException` |  |
| `void setAsciiStream(int, java.io.InputStream, int) throws java.sql.SQLException` |  |
| `void setAsciiStream(int, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void setAsciiStream(int, java.io.InputStream) throws java.sql.SQLException` |  |
| `void setBigDecimal(int, java.math.BigDecimal) throws java.sql.SQLException` |  |
| `void setBinaryStream(int, java.io.InputStream, int) throws java.sql.SQLException` |  |
| `void setBinaryStream(int, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void setBinaryStream(int, java.io.InputStream) throws java.sql.SQLException` |  |
| `void setBlob(int, java.sql.Blob) throws java.sql.SQLException` |  |
| `void setBlob(int, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void setBlob(int, java.io.InputStream) throws java.sql.SQLException` |  |
| `void setBoolean(int, boolean) throws java.sql.SQLException` |  |
| `void setByte(int, byte) throws java.sql.SQLException` |  |
| `void setBytes(int, byte[]) throws java.sql.SQLException` |  |
| `void setCharacterStream(int, java.io.Reader, int) throws java.sql.SQLException` |  |
| `void setCharacterStream(int, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setCharacterStream(int, java.io.Reader) throws java.sql.SQLException` |  |
| `void setClob(int, java.sql.Clob) throws java.sql.SQLException` |  |
| `void setClob(int, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setClob(int, java.io.Reader) throws java.sql.SQLException` |  |
| `void setDate(int, java.sql.Date) throws java.sql.SQLException` |  |
| `void setDate(int, java.sql.Date, java.util.Calendar) throws java.sql.SQLException` |  |
| `void setDouble(int, double) throws java.sql.SQLException` |  |
| `void setFloat(int, float) throws java.sql.SQLException` |  |
| `void setInt(int, int) throws java.sql.SQLException` |  |
| `void setLong(int, long) throws java.sql.SQLException` |  |
| `void setNCharacterStream(int, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setNCharacterStream(int, java.io.Reader) throws java.sql.SQLException` |  |
| `void setNClob(int, java.sql.NClob) throws java.sql.SQLException` |  |
| `void setNClob(int, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setNClob(int, java.io.Reader) throws java.sql.SQLException` |  |
| `void setNString(int, String) throws java.sql.SQLException` |  |
| `void setNull(int, int) throws java.sql.SQLException` |  |
| `void setNull(int, int, String) throws java.sql.SQLException` |  |
| `void setObject(int, Object, int) throws java.sql.SQLException` |  |
| `void setObject(int, Object) throws java.sql.SQLException` |  |
| `void setObject(int, Object, int, int) throws java.sql.SQLException` |  |
| `void setRef(int, java.sql.Ref) throws java.sql.SQLException` |  |
| `void setRowId(int, java.sql.RowId) throws java.sql.SQLException` |  |
| `void setSQLXML(int, java.sql.SQLXML) throws java.sql.SQLException` |  |
| `void setShort(int, short) throws java.sql.SQLException` |  |
| `void setString(int, String) throws java.sql.SQLException` |  |
| `void setTime(int, java.sql.Time) throws java.sql.SQLException` |  |
| `void setTime(int, java.sql.Time, java.util.Calendar) throws java.sql.SQLException` |  |
| `void setTimestamp(int, java.sql.Timestamp) throws java.sql.SQLException` |  |
| `void setTimestamp(int, java.sql.Timestamp, java.util.Calendar) throws java.sql.SQLException` |  |
| `void setURL(int, java.net.URL) throws java.sql.SQLException` |  |

---

### `interface Ref`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getBaseTypeName() throws java.sql.SQLException` |  |
| `Object getObject(java.util.Map<java.lang.String,java.lang.Class<?>>) throws java.sql.SQLException` |  |
| `Object getObject() throws java.sql.SQLException` |  |
| `void setObject(Object) throws java.sql.SQLException` |  |

---

### `interface ResultSet`

- **Extends:** `java.sql.Wrapper java.lang.AutoCloseable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CLOSE_CURSORS_AT_COMMIT = 2` |  |
| `static final int CONCUR_READ_ONLY = 1007` |  |
| `static final int CONCUR_UPDATABLE = 1008` |  |
| `static final int FETCH_FORWARD = 1000` |  |
| `static final int FETCH_REVERSE = 1001` |  |
| `static final int FETCH_UNKNOWN = 1002` |  |
| `static final int HOLD_CURSORS_OVER_COMMIT = 1` |  |
| `static final int TYPE_FORWARD_ONLY = 1003` |  |
| `static final int TYPE_SCROLL_INSENSITIVE = 1004` |  |
| `static final int TYPE_SCROLL_SENSITIVE = 1005` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean absolute(int) throws java.sql.SQLException` |  |
| `void afterLast() throws java.sql.SQLException` |  |
| `void beforeFirst() throws java.sql.SQLException` |  |
| `void cancelRowUpdates() throws java.sql.SQLException` |  |
| `void clearWarnings() throws java.sql.SQLException` |  |
| `void close() throws java.sql.SQLException` |  |
| `void deleteRow() throws java.sql.SQLException` |  |
| `int findColumn(String) throws java.sql.SQLException` |  |
| `boolean first() throws java.sql.SQLException` |  |
| `java.sql.Array getArray(int) throws java.sql.SQLException` |  |
| `java.sql.Array getArray(String) throws java.sql.SQLException` |  |
| `java.io.InputStream getAsciiStream(int) throws java.sql.SQLException` |  |
| `java.io.InputStream getAsciiStream(String) throws java.sql.SQLException` |  |
| `java.math.BigDecimal getBigDecimal(int) throws java.sql.SQLException` |  |
| `java.math.BigDecimal getBigDecimal(String) throws java.sql.SQLException` |  |
| `java.io.InputStream getBinaryStream(int) throws java.sql.SQLException` |  |
| `java.io.InputStream getBinaryStream(String) throws java.sql.SQLException` |  |
| `java.sql.Blob getBlob(int) throws java.sql.SQLException` |  |
| `java.sql.Blob getBlob(String) throws java.sql.SQLException` |  |
| `boolean getBoolean(int) throws java.sql.SQLException` |  |
| `boolean getBoolean(String) throws java.sql.SQLException` |  |
| `byte getByte(int) throws java.sql.SQLException` |  |
| `byte getByte(String) throws java.sql.SQLException` |  |
| `byte[] getBytes(int) throws java.sql.SQLException` |  |
| `byte[] getBytes(String) throws java.sql.SQLException` |  |
| `java.io.Reader getCharacterStream(int) throws java.sql.SQLException` |  |
| `java.io.Reader getCharacterStream(String) throws java.sql.SQLException` |  |
| `java.sql.Clob getClob(int) throws java.sql.SQLException` |  |
| `java.sql.Clob getClob(String) throws java.sql.SQLException` |  |
| `int getConcurrency() throws java.sql.SQLException` |  |
| `String getCursorName() throws java.sql.SQLException` |  |
| `java.sql.Date getDate(int) throws java.sql.SQLException` |  |
| `java.sql.Date getDate(String) throws java.sql.SQLException` |  |
| `java.sql.Date getDate(int, java.util.Calendar) throws java.sql.SQLException` |  |
| `java.sql.Date getDate(String, java.util.Calendar) throws java.sql.SQLException` |  |
| `double getDouble(int) throws java.sql.SQLException` |  |
| `double getDouble(String) throws java.sql.SQLException` |  |
| `int getFetchDirection() throws java.sql.SQLException` |  |
| `int getFetchSize() throws java.sql.SQLException` |  |
| `float getFloat(int) throws java.sql.SQLException` |  |
| `float getFloat(String) throws java.sql.SQLException` |  |
| `int getHoldability() throws java.sql.SQLException` |  |
| `int getInt(int) throws java.sql.SQLException` |  |
| `int getInt(String) throws java.sql.SQLException` |  |
| `long getLong(int) throws java.sql.SQLException` |  |
| `long getLong(String) throws java.sql.SQLException` |  |
| `java.sql.ResultSetMetaData getMetaData() throws java.sql.SQLException` |  |
| `java.io.Reader getNCharacterStream(int) throws java.sql.SQLException` |  |
| `java.io.Reader getNCharacterStream(String) throws java.sql.SQLException` |  |
| `java.sql.NClob getNClob(int) throws java.sql.SQLException` |  |
| `java.sql.NClob getNClob(String) throws java.sql.SQLException` |  |
| `String getNString(int) throws java.sql.SQLException` |  |
| `String getNString(String) throws java.sql.SQLException` |  |
| `Object getObject(int) throws java.sql.SQLException` |  |
| `Object getObject(String) throws java.sql.SQLException` |  |
| `Object getObject(int, java.util.Map<java.lang.String,java.lang.Class<?>>) throws java.sql.SQLException` |  |
| `Object getObject(String, java.util.Map<java.lang.String,java.lang.Class<?>>) throws java.sql.SQLException` |  |
| `java.sql.Ref getRef(int) throws java.sql.SQLException` |  |
| `java.sql.Ref getRef(String) throws java.sql.SQLException` |  |
| `int getRow() throws java.sql.SQLException` |  |
| `java.sql.RowId getRowId(int) throws java.sql.SQLException` |  |
| `java.sql.RowId getRowId(String) throws java.sql.SQLException` |  |
| `java.sql.SQLXML getSQLXML(int) throws java.sql.SQLException` |  |
| `java.sql.SQLXML getSQLXML(String) throws java.sql.SQLException` |  |
| `short getShort(int) throws java.sql.SQLException` |  |
| `short getShort(String) throws java.sql.SQLException` |  |
| `java.sql.Statement getStatement() throws java.sql.SQLException` |  |
| `String getString(int) throws java.sql.SQLException` |  |
| `String getString(String) throws java.sql.SQLException` |  |
| `java.sql.Time getTime(int) throws java.sql.SQLException` |  |
| `java.sql.Time getTime(String) throws java.sql.SQLException` |  |
| `java.sql.Time getTime(int, java.util.Calendar) throws java.sql.SQLException` |  |
| `java.sql.Time getTime(String, java.util.Calendar) throws java.sql.SQLException` |  |
| `java.sql.Timestamp getTimestamp(int) throws java.sql.SQLException` |  |
| `java.sql.Timestamp getTimestamp(String) throws java.sql.SQLException` |  |
| `java.sql.Timestamp getTimestamp(int, java.util.Calendar) throws java.sql.SQLException` |  |
| `java.sql.Timestamp getTimestamp(String, java.util.Calendar) throws java.sql.SQLException` |  |
| `int getType() throws java.sql.SQLException` |  |
| `java.net.URL getURL(int) throws java.sql.SQLException` |  |
| `java.net.URL getURL(String) throws java.sql.SQLException` |  |
| `java.sql.SQLWarning getWarnings() throws java.sql.SQLException` |  |
| `void insertRow() throws java.sql.SQLException` |  |
| `boolean isAfterLast() throws java.sql.SQLException` |  |
| `boolean isBeforeFirst() throws java.sql.SQLException` |  |
| `boolean isClosed() throws java.sql.SQLException` |  |
| `boolean isFirst() throws java.sql.SQLException` |  |
| `boolean isLast() throws java.sql.SQLException` |  |
| `boolean last() throws java.sql.SQLException` |  |
| `void moveToCurrentRow() throws java.sql.SQLException` |  |
| `void moveToInsertRow() throws java.sql.SQLException` |  |
| `boolean next() throws java.sql.SQLException` |  |
| `boolean previous() throws java.sql.SQLException` |  |
| `void refreshRow() throws java.sql.SQLException` |  |
| `boolean relative(int) throws java.sql.SQLException` |  |
| `boolean rowDeleted() throws java.sql.SQLException` |  |
| `boolean rowInserted() throws java.sql.SQLException` |  |
| `boolean rowUpdated() throws java.sql.SQLException` |  |
| `void setFetchDirection(int) throws java.sql.SQLException` |  |
| `void setFetchSize(int) throws java.sql.SQLException` |  |
| `void updateArray(int, java.sql.Array) throws java.sql.SQLException` |  |
| `void updateArray(String, java.sql.Array) throws java.sql.SQLException` |  |
| `void updateAsciiStream(int, java.io.InputStream, int) throws java.sql.SQLException` |  |
| `void updateAsciiStream(String, java.io.InputStream, int) throws java.sql.SQLException` |  |
| `void updateAsciiStream(int, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void updateAsciiStream(String, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void updateAsciiStream(int, java.io.InputStream) throws java.sql.SQLException` |  |
| `void updateAsciiStream(String, java.io.InputStream) throws java.sql.SQLException` |  |
| `void updateBigDecimal(int, java.math.BigDecimal) throws java.sql.SQLException` |  |
| `void updateBigDecimal(String, java.math.BigDecimal) throws java.sql.SQLException` |  |
| `void updateBinaryStream(int, java.io.InputStream, int) throws java.sql.SQLException` |  |
| `void updateBinaryStream(String, java.io.InputStream, int) throws java.sql.SQLException` |  |
| `void updateBinaryStream(int, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void updateBinaryStream(String, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void updateBinaryStream(int, java.io.InputStream) throws java.sql.SQLException` |  |
| `void updateBinaryStream(String, java.io.InputStream) throws java.sql.SQLException` |  |
| `void updateBlob(int, java.sql.Blob) throws java.sql.SQLException` |  |
| `void updateBlob(String, java.sql.Blob) throws java.sql.SQLException` |  |
| `void updateBlob(int, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void updateBlob(String, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void updateBlob(int, java.io.InputStream) throws java.sql.SQLException` |  |
| `void updateBlob(String, java.io.InputStream) throws java.sql.SQLException` |  |
| `void updateBoolean(int, boolean) throws java.sql.SQLException` |  |
| `void updateBoolean(String, boolean) throws java.sql.SQLException` |  |
| `void updateByte(int, byte) throws java.sql.SQLException` |  |
| `void updateByte(String, byte) throws java.sql.SQLException` |  |
| `void updateBytes(int, byte[]) throws java.sql.SQLException` |  |
| `void updateBytes(String, byte[]) throws java.sql.SQLException` |  |
| `void updateCharacterStream(int, java.io.Reader, int) throws java.sql.SQLException` |  |
| `void updateCharacterStream(String, java.io.Reader, int) throws java.sql.SQLException` |  |
| `void updateCharacterStream(int, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void updateCharacterStream(String, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void updateCharacterStream(int, java.io.Reader) throws java.sql.SQLException` |  |
| `void updateCharacterStream(String, java.io.Reader) throws java.sql.SQLException` |  |
| `void updateClob(int, java.sql.Clob) throws java.sql.SQLException` |  |
| `void updateClob(String, java.sql.Clob) throws java.sql.SQLException` |  |
| `void updateClob(int, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void updateClob(String, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void updateClob(int, java.io.Reader) throws java.sql.SQLException` |  |
| `void updateClob(String, java.io.Reader) throws java.sql.SQLException` |  |
| `void updateDate(int, java.sql.Date) throws java.sql.SQLException` |  |
| `void updateDate(String, java.sql.Date) throws java.sql.SQLException` |  |
| `void updateDouble(int, double) throws java.sql.SQLException` |  |
| `void updateDouble(String, double) throws java.sql.SQLException` |  |
| `void updateFloat(int, float) throws java.sql.SQLException` |  |
| `void updateFloat(String, float) throws java.sql.SQLException` |  |
| `void updateInt(int, int) throws java.sql.SQLException` |  |
| `void updateInt(String, int) throws java.sql.SQLException` |  |
| `void updateLong(int, long) throws java.sql.SQLException` |  |
| `void updateLong(String, long) throws java.sql.SQLException` |  |
| `void updateNCharacterStream(int, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void updateNCharacterStream(String, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void updateNCharacterStream(int, java.io.Reader) throws java.sql.SQLException` |  |
| `void updateNCharacterStream(String, java.io.Reader) throws java.sql.SQLException` |  |
| `void updateNClob(int, java.sql.NClob) throws java.sql.SQLException` |  |
| `void updateNClob(String, java.sql.NClob) throws java.sql.SQLException` |  |
| `void updateNClob(int, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void updateNClob(String, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void updateNClob(int, java.io.Reader) throws java.sql.SQLException` |  |
| `void updateNClob(String, java.io.Reader) throws java.sql.SQLException` |  |
| `void updateNString(int, String) throws java.sql.SQLException` |  |
| `void updateNString(String, String) throws java.sql.SQLException` |  |
| `void updateNull(int) throws java.sql.SQLException` |  |
| `void updateNull(String) throws java.sql.SQLException` |  |
| `void updateObject(int, Object, int) throws java.sql.SQLException` |  |
| `void updateObject(int, Object) throws java.sql.SQLException` |  |
| `void updateObject(String, Object, int) throws java.sql.SQLException` |  |
| `void updateObject(String, Object) throws java.sql.SQLException` |  |
| `void updateRef(int, java.sql.Ref) throws java.sql.SQLException` |  |
| `void updateRef(String, java.sql.Ref) throws java.sql.SQLException` |  |
| `void updateRow() throws java.sql.SQLException` |  |
| `void updateRowId(int, java.sql.RowId) throws java.sql.SQLException` |  |
| `void updateRowId(String, java.sql.RowId) throws java.sql.SQLException` |  |
| `void updateSQLXML(int, java.sql.SQLXML) throws java.sql.SQLException` |  |
| `void updateSQLXML(String, java.sql.SQLXML) throws java.sql.SQLException` |  |
| `void updateShort(int, short) throws java.sql.SQLException` |  |
| `void updateShort(String, short) throws java.sql.SQLException` |  |
| `void updateString(int, String) throws java.sql.SQLException` |  |
| `void updateString(String, String) throws java.sql.SQLException` |  |
| `void updateTime(int, java.sql.Time) throws java.sql.SQLException` |  |
| `void updateTime(String, java.sql.Time) throws java.sql.SQLException` |  |
| `void updateTimestamp(int, java.sql.Timestamp) throws java.sql.SQLException` |  |
| `void updateTimestamp(String, java.sql.Timestamp) throws java.sql.SQLException` |  |
| `boolean wasNull() throws java.sql.SQLException` |  |

---

### `interface ResultSetMetaData`

- **Extends:** `java.sql.Wrapper`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int columnNoNulls = 0` |  |
| `static final int columnNullable = 1` |  |
| `static final int columnNullableUnknown = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getCatalogName(int) throws java.sql.SQLException` |  |
| `String getColumnClassName(int) throws java.sql.SQLException` |  |
| `int getColumnCount() throws java.sql.SQLException` |  |
| `int getColumnDisplaySize(int) throws java.sql.SQLException` |  |
| `String getColumnLabel(int) throws java.sql.SQLException` |  |
| `String getColumnName(int) throws java.sql.SQLException` |  |
| `int getColumnType(int) throws java.sql.SQLException` |  |
| `String getColumnTypeName(int) throws java.sql.SQLException` |  |
| `int getPrecision(int) throws java.sql.SQLException` |  |
| `int getScale(int) throws java.sql.SQLException` |  |
| `String getSchemaName(int) throws java.sql.SQLException` |  |
| `String getTableName(int) throws java.sql.SQLException` |  |
| `boolean isAutoIncrement(int) throws java.sql.SQLException` |  |
| `boolean isCaseSensitive(int) throws java.sql.SQLException` |  |
| `boolean isCurrency(int) throws java.sql.SQLException` |  |
| `boolean isDefinitelyWritable(int) throws java.sql.SQLException` |  |
| `int isNullable(int) throws java.sql.SQLException` |  |
| `boolean isReadOnly(int) throws java.sql.SQLException` |  |
| `boolean isSearchable(int) throws java.sql.SQLException` |  |
| `boolean isSigned(int) throws java.sql.SQLException` |  |
| `boolean isWritable(int) throws java.sql.SQLException` |  |

---

### `interface RowId`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean equals(Object)` |  |
| `byte[] getBytes()` |  |
| `int hashCode()` |  |
| `String toString()` |  |

---

### `enum RowIdLifetime`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.sql.RowIdLifetime ROWID_UNSUPPORTED` |  |
| `static final java.sql.RowIdLifetime ROWID_VALID_FOREVER` |  |
| `static final java.sql.RowIdLifetime ROWID_VALID_OTHER` |  |
| `static final java.sql.RowIdLifetime ROWID_VALID_SESSION` |  |
| `static final java.sql.RowIdLifetime ROWID_VALID_TRANSACTION` |  |

---

### `class SQLClientInfoException`

- **Extends:** `java.sql.SQLException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLClientInfoException()` |  |
| `SQLClientInfoException(java.util.Map<java.lang.String,java.sql.ClientInfoStatus>)` |  |
| `SQLClientInfoException(java.util.Map<java.lang.String,java.sql.ClientInfoStatus>, Throwable)` |  |
| `SQLClientInfoException(String, java.util.Map<java.lang.String,java.sql.ClientInfoStatus>)` |  |
| `SQLClientInfoException(String, java.util.Map<java.lang.String,java.sql.ClientInfoStatus>, Throwable)` |  |
| `SQLClientInfoException(String, String, java.util.Map<java.lang.String,java.sql.ClientInfoStatus>)` |  |
| `SQLClientInfoException(String, String, java.util.Map<java.lang.String,java.sql.ClientInfoStatus>, Throwable)` |  |
| `SQLClientInfoException(String, String, int, java.util.Map<java.lang.String,java.sql.ClientInfoStatus>)` |  |
| `SQLClientInfoException(String, String, int, java.util.Map<java.lang.String,java.sql.ClientInfoStatus>, Throwable)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Map<java.lang.String,java.sql.ClientInfoStatus> getFailedProperties()` |  |

---

### `interface SQLData`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getSQLTypeName() throws java.sql.SQLException` |  |
| `void readSQL(java.sql.SQLInput, String) throws java.sql.SQLException` |  |
| `void writeSQL(java.sql.SQLOutput) throws java.sql.SQLException` |  |

---

### `class SQLDataException`

- **Extends:** `java.sql.SQLNonTransientException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLDataException()` |  |
| `SQLDataException(String)` |  |
| `SQLDataException(String, String)` |  |
| `SQLDataException(String, String, int)` |  |
| `SQLDataException(Throwable)` |  |
| `SQLDataException(String, Throwable)` |  |
| `SQLDataException(String, String, Throwable)` |  |
| `SQLDataException(String, String, int, Throwable)` |  |

---

### `class SQLException`

- **Extends:** `java.lang.Exception`
- **Implements:** `java.lang.Iterable<java.lang.Throwable>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLException(String, String, int)` |  |
| `SQLException(String, String)` |  |
| `SQLException(String)` |  |
| `SQLException()` |  |
| `SQLException(Throwable)` |  |
| `SQLException(String, Throwable)` |  |
| `SQLException(String, String, Throwable)` |  |
| `SQLException(String, String, int, Throwable)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getErrorCode()` |  |
| `java.sql.SQLException getNextException()` |  |
| `String getSQLState()` |  |
| `java.util.Iterator<java.lang.Throwable> iterator()` |  |
| `void setNextException(java.sql.SQLException)` |  |

---

### `class SQLFeatureNotSupportedException`

- **Extends:** `java.sql.SQLNonTransientException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLFeatureNotSupportedException()` |  |
| `SQLFeatureNotSupportedException(String)` |  |
| `SQLFeatureNotSupportedException(String, String)` |  |
| `SQLFeatureNotSupportedException(String, String, int)` |  |
| `SQLFeatureNotSupportedException(Throwable)` |  |
| `SQLFeatureNotSupportedException(String, Throwable)` |  |
| `SQLFeatureNotSupportedException(String, String, Throwable)` |  |
| `SQLFeatureNotSupportedException(String, String, int, Throwable)` |  |

---

### `interface SQLInput`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.sql.Array readArray() throws java.sql.SQLException` |  |
| `java.io.InputStream readAsciiStream() throws java.sql.SQLException` |  |
| `java.math.BigDecimal readBigDecimal() throws java.sql.SQLException` |  |
| `java.io.InputStream readBinaryStream() throws java.sql.SQLException` |  |
| `java.sql.Blob readBlob() throws java.sql.SQLException` |  |
| `boolean readBoolean() throws java.sql.SQLException` |  |
| `byte readByte() throws java.sql.SQLException` |  |
| `byte[] readBytes() throws java.sql.SQLException` |  |
| `java.io.Reader readCharacterStream() throws java.sql.SQLException` |  |
| `java.sql.Clob readClob() throws java.sql.SQLException` |  |
| `java.sql.Date readDate() throws java.sql.SQLException` |  |
| `double readDouble() throws java.sql.SQLException` |  |
| `float readFloat() throws java.sql.SQLException` |  |
| `int readInt() throws java.sql.SQLException` |  |
| `long readLong() throws java.sql.SQLException` |  |
| `java.sql.NClob readNClob() throws java.sql.SQLException` |  |
| `String readNString() throws java.sql.SQLException` |  |
| `Object readObject() throws java.sql.SQLException` |  |
| `java.sql.Ref readRef() throws java.sql.SQLException` |  |
| `java.sql.RowId readRowId() throws java.sql.SQLException` |  |
| `java.sql.SQLXML readSQLXML() throws java.sql.SQLException` |  |
| `short readShort() throws java.sql.SQLException` |  |
| `String readString() throws java.sql.SQLException` |  |
| `java.sql.Time readTime() throws java.sql.SQLException` |  |
| `java.sql.Timestamp readTimestamp() throws java.sql.SQLException` |  |
| `java.net.URL readURL() throws java.sql.SQLException` |  |
| `boolean wasNull() throws java.sql.SQLException` |  |

---

### `class SQLIntegrityConstraintViolationException`

- **Extends:** `java.sql.SQLNonTransientException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLIntegrityConstraintViolationException()` |  |
| `SQLIntegrityConstraintViolationException(String)` |  |
| `SQLIntegrityConstraintViolationException(String, String)` |  |
| `SQLIntegrityConstraintViolationException(String, String, int)` |  |
| `SQLIntegrityConstraintViolationException(Throwable)` |  |
| `SQLIntegrityConstraintViolationException(String, Throwable)` |  |
| `SQLIntegrityConstraintViolationException(String, String, Throwable)` |  |
| `SQLIntegrityConstraintViolationException(String, String, int, Throwable)` |  |

---

### `class SQLInvalidAuthorizationSpecException`

- **Extends:** `java.sql.SQLNonTransientException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLInvalidAuthorizationSpecException()` |  |
| `SQLInvalidAuthorizationSpecException(String)` |  |
| `SQLInvalidAuthorizationSpecException(String, String)` |  |
| `SQLInvalidAuthorizationSpecException(String, String, int)` |  |
| `SQLInvalidAuthorizationSpecException(Throwable)` |  |
| `SQLInvalidAuthorizationSpecException(String, Throwable)` |  |
| `SQLInvalidAuthorizationSpecException(String, String, Throwable)` |  |
| `SQLInvalidAuthorizationSpecException(String, String, int, Throwable)` |  |

---

### `class SQLNonTransientConnectionException`

- **Extends:** `java.sql.SQLNonTransientException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLNonTransientConnectionException()` |  |
| `SQLNonTransientConnectionException(String)` |  |
| `SQLNonTransientConnectionException(String, String)` |  |
| `SQLNonTransientConnectionException(String, String, int)` |  |
| `SQLNonTransientConnectionException(Throwable)` |  |
| `SQLNonTransientConnectionException(String, Throwable)` |  |
| `SQLNonTransientConnectionException(String, String, Throwable)` |  |
| `SQLNonTransientConnectionException(String, String, int, Throwable)` |  |

---

### `class SQLNonTransientException`

- **Extends:** `java.sql.SQLException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLNonTransientException()` |  |
| `SQLNonTransientException(String)` |  |
| `SQLNonTransientException(String, String)` |  |
| `SQLNonTransientException(String, String, int)` |  |
| `SQLNonTransientException(Throwable)` |  |
| `SQLNonTransientException(String, Throwable)` |  |
| `SQLNonTransientException(String, String, Throwable)` |  |
| `SQLNonTransientException(String, String, int, Throwable)` |  |

---

### `interface SQLOutput`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void writeArray(java.sql.Array) throws java.sql.SQLException` |  |
| `void writeAsciiStream(java.io.InputStream) throws java.sql.SQLException` |  |
| `void writeBigDecimal(java.math.BigDecimal) throws java.sql.SQLException` |  |
| `void writeBinaryStream(java.io.InputStream) throws java.sql.SQLException` |  |
| `void writeBlob(java.sql.Blob) throws java.sql.SQLException` |  |
| `void writeBoolean(boolean) throws java.sql.SQLException` |  |
| `void writeByte(byte) throws java.sql.SQLException` |  |
| `void writeBytes(byte[]) throws java.sql.SQLException` |  |
| `void writeCharacterStream(java.io.Reader) throws java.sql.SQLException` |  |
| `void writeClob(java.sql.Clob) throws java.sql.SQLException` |  |
| `void writeDate(java.sql.Date) throws java.sql.SQLException` |  |
| `void writeDouble(double) throws java.sql.SQLException` |  |
| `void writeFloat(float) throws java.sql.SQLException` |  |
| `void writeInt(int) throws java.sql.SQLException` |  |
| `void writeLong(long) throws java.sql.SQLException` |  |
| `void writeNClob(java.sql.NClob) throws java.sql.SQLException` |  |
| `void writeNString(String) throws java.sql.SQLException` |  |
| `void writeObject(java.sql.SQLData) throws java.sql.SQLException` |  |
| `void writeRef(java.sql.Ref) throws java.sql.SQLException` |  |
| `void writeRowId(java.sql.RowId) throws java.sql.SQLException` |  |
| `void writeSQLXML(java.sql.SQLXML) throws java.sql.SQLException` |  |
| `void writeShort(short) throws java.sql.SQLException` |  |
| `void writeString(String) throws java.sql.SQLException` |  |
| `void writeStruct(java.sql.Struct) throws java.sql.SQLException` |  |
| `void writeTime(java.sql.Time) throws java.sql.SQLException` |  |
| `void writeTimestamp(java.sql.Timestamp) throws java.sql.SQLException` |  |
| `void writeURL(java.net.URL) throws java.sql.SQLException` |  |

---

### `class final SQLPermission`

- **Extends:** `java.security.BasicPermission`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLPermission(String)` |  |
| `SQLPermission(String, String)` |  |

---

### `class SQLRecoverableException`

- **Extends:** `java.sql.SQLException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLRecoverableException()` |  |
| `SQLRecoverableException(String)` |  |
| `SQLRecoverableException(String, String)` |  |
| `SQLRecoverableException(String, String, int)` |  |
| `SQLRecoverableException(Throwable)` |  |
| `SQLRecoverableException(String, Throwable)` |  |
| `SQLRecoverableException(String, String, Throwable)` |  |
| `SQLRecoverableException(String, String, int, Throwable)` |  |

---

### `class SQLSyntaxErrorException`

- **Extends:** `java.sql.SQLNonTransientException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLSyntaxErrorException()` |  |
| `SQLSyntaxErrorException(String)` |  |
| `SQLSyntaxErrorException(String, String)` |  |
| `SQLSyntaxErrorException(String, String, int)` |  |
| `SQLSyntaxErrorException(Throwable)` |  |
| `SQLSyntaxErrorException(String, Throwable)` |  |
| `SQLSyntaxErrorException(String, String, Throwable)` |  |
| `SQLSyntaxErrorException(String, String, int, Throwable)` |  |

---

### `class SQLTimeoutException`

- **Extends:** `java.sql.SQLTransientException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLTimeoutException()` |  |
| `SQLTimeoutException(String)` |  |
| `SQLTimeoutException(String, String)` |  |
| `SQLTimeoutException(String, String, int)` |  |
| `SQLTimeoutException(Throwable)` |  |
| `SQLTimeoutException(String, Throwable)` |  |
| `SQLTimeoutException(String, String, Throwable)` |  |
| `SQLTimeoutException(String, String, int, Throwable)` |  |

---

### `class SQLTransactionRollbackException`

- **Extends:** `java.sql.SQLTransientException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLTransactionRollbackException()` |  |
| `SQLTransactionRollbackException(String)` |  |
| `SQLTransactionRollbackException(String, String)` |  |
| `SQLTransactionRollbackException(String, String, int)` |  |
| `SQLTransactionRollbackException(Throwable)` |  |
| `SQLTransactionRollbackException(String, Throwable)` |  |
| `SQLTransactionRollbackException(String, String, Throwable)` |  |
| `SQLTransactionRollbackException(String, String, int, Throwable)` |  |

---

### `class SQLTransientConnectionException`

- **Extends:** `java.sql.SQLTransientException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLTransientConnectionException()` |  |
| `SQLTransientConnectionException(String)` |  |
| `SQLTransientConnectionException(String, String)` |  |
| `SQLTransientConnectionException(String, String, int)` |  |
| `SQLTransientConnectionException(Throwable)` |  |
| `SQLTransientConnectionException(String, Throwable)` |  |
| `SQLTransientConnectionException(String, String, Throwable)` |  |
| `SQLTransientConnectionException(String, String, int, Throwable)` |  |

---

### `class SQLTransientException`

- **Extends:** `java.sql.SQLException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLTransientException()` |  |
| `SQLTransientException(String)` |  |
| `SQLTransientException(String, String)` |  |
| `SQLTransientException(String, String, int)` |  |
| `SQLTransientException(Throwable)` |  |
| `SQLTransientException(String, Throwable)` |  |
| `SQLTransientException(String, String, Throwable)` |  |
| `SQLTransientException(String, String, int, Throwable)` |  |

---

### `class SQLWarning`

- **Extends:** `java.sql.SQLException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SQLWarning(String, String, int)` |  |
| `SQLWarning(String, String)` |  |
| `SQLWarning(String)` |  |
| `SQLWarning()` |  |
| `SQLWarning(Throwable)` |  |
| `SQLWarning(String, Throwable)` |  |
| `SQLWarning(String, String, Throwable)` |  |
| `SQLWarning(String, String, int, Throwable)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.sql.SQLWarning getNextWarning()` |  |
| `void setNextWarning(java.sql.SQLWarning)` |  |

---

### `interface SQLXML`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void free() throws java.sql.SQLException` |  |
| `java.io.InputStream getBinaryStream() throws java.sql.SQLException` |  |
| `java.io.Reader getCharacterStream() throws java.sql.SQLException` |  |
| `<T extends javax.xml.transform.Source> T getSource(Class<T>) throws java.sql.SQLException` |  |
| `String getString() throws java.sql.SQLException` |  |
| `java.io.OutputStream setBinaryStream() throws java.sql.SQLException` |  |
| `java.io.Writer setCharacterStream() throws java.sql.SQLException` |  |
| `<T extends javax.xml.transform.Result> T setResult(Class<T>) throws java.sql.SQLException` |  |
| `void setString(String) throws java.sql.SQLException` |  |

---

### `interface Savepoint`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getSavepointId() throws java.sql.SQLException` |  |
| `String getSavepointName() throws java.sql.SQLException` |  |

---

### `interface Statement`

- **Extends:** `java.sql.Wrapper java.lang.AutoCloseable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CLOSE_ALL_RESULTS = 3` |  |
| `static final int CLOSE_CURRENT_RESULT = 1` |  |
| `static final int EXECUTE_FAILED = -3` |  |
| `static final int KEEP_CURRENT_RESULT = 2` |  |
| `static final int NO_GENERATED_KEYS = 2` |  |
| `static final int RETURN_GENERATED_KEYS = 1` |  |
| `static final int SUCCESS_NO_INFO = -2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addBatch(String) throws java.sql.SQLException` |  |
| `void cancel() throws java.sql.SQLException` |  |
| `void clearBatch() throws java.sql.SQLException` |  |
| `void clearWarnings() throws java.sql.SQLException` |  |
| `void close() throws java.sql.SQLException` |  |
| `boolean execute(String) throws java.sql.SQLException` |  |
| `boolean execute(String, int) throws java.sql.SQLException` |  |
| `boolean execute(String, int[]) throws java.sql.SQLException` |  |
| `boolean execute(String, String[]) throws java.sql.SQLException` |  |
| `int[] executeBatch() throws java.sql.SQLException` |  |
| `java.sql.ResultSet executeQuery(String) throws java.sql.SQLException` |  |
| `int executeUpdate(String) throws java.sql.SQLException` |  |
| `int executeUpdate(String, int) throws java.sql.SQLException` |  |
| `int executeUpdate(String, int[]) throws java.sql.SQLException` |  |
| `int executeUpdate(String, String[]) throws java.sql.SQLException` |  |
| `java.sql.Connection getConnection() throws java.sql.SQLException` |  |
| `int getFetchDirection() throws java.sql.SQLException` |  |
| `int getFetchSize() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getGeneratedKeys() throws java.sql.SQLException` |  |
| `int getMaxFieldSize() throws java.sql.SQLException` |  |
| `int getMaxRows() throws java.sql.SQLException` |  |
| `boolean getMoreResults() throws java.sql.SQLException` |  |
| `boolean getMoreResults(int) throws java.sql.SQLException` |  |
| `int getQueryTimeout() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getResultSet() throws java.sql.SQLException` |  |
| `int getResultSetConcurrency() throws java.sql.SQLException` |  |
| `int getResultSetHoldability() throws java.sql.SQLException` |  |
| `int getResultSetType() throws java.sql.SQLException` |  |
| `int getUpdateCount() throws java.sql.SQLException` |  |
| `java.sql.SQLWarning getWarnings() throws java.sql.SQLException` |  |
| `boolean isClosed() throws java.sql.SQLException` |  |
| `boolean isPoolable() throws java.sql.SQLException` |  |
| `void setCursorName(String) throws java.sql.SQLException` |  |
| `void setEscapeProcessing(boolean) throws java.sql.SQLException` |  |
| `void setFetchDirection(int) throws java.sql.SQLException` |  |
| `void setFetchSize(int) throws java.sql.SQLException` |  |
| `void setMaxFieldSize(int) throws java.sql.SQLException` |  |
| `void setMaxRows(int) throws java.sql.SQLException` |  |
| `void setPoolable(boolean) throws java.sql.SQLException` |  |
| `void setQueryTimeout(int) throws java.sql.SQLException` |  |

---

### `interface Struct`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object[] getAttributes() throws java.sql.SQLException` |  |
| `Object[] getAttributes(java.util.Map<java.lang.String,java.lang.Class<?>>) throws java.sql.SQLException` |  |
| `String getSQLTypeName() throws java.sql.SQLException` |  |

---

### `class Time`

- **Extends:** `java.util.Date`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Time(long)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.sql.Time valueOf(String)` |  |

---

### `class Timestamp`

- **Extends:** `java.util.Date`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Timestamp(long)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean after(java.sql.Timestamp)` |  |
| `boolean before(java.sql.Timestamp)` |  |
| `int compareTo(java.sql.Timestamp)` |  |
| `boolean equals(java.sql.Timestamp)` |  |
| `int getNanos()` |  |
| `void setNanos(int)` |  |
| `static java.sql.Timestamp valueOf(String)` |  |

---

### `class Types`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ARRAY = 2003` |  |
| `static final int BIGINT = -5` |  |
| `static final int BINARY = -2` |  |
| `static final int BIT = -7` |  |
| `static final int BLOB = 2004` |  |
| `static final int BOOLEAN = 16` |  |
| `static final int CHAR = 1` |  |
| `static final int CLOB = 2005` |  |
| `static final int DATALINK = 70` |  |
| `static final int DATE = 91` |  |
| `static final int DECIMAL = 3` |  |
| `static final int DISTINCT = 2001` |  |
| `static final int DOUBLE = 8` |  |
| `static final int FLOAT = 6` |  |
| `static final int INTEGER = 4` |  |
| `static final int JAVA_OBJECT = 2000` |  |
| `static final int LONGNVARCHAR = -16` |  |
| `static final int LONGVARBINARY = -4` |  |
| `static final int LONGVARCHAR = -1` |  |
| `static final int NCHAR = -15` |  |
| `static final int NCLOB = 2011` |  |
| `static final int NULL = 0` |  |
| `static final int NUMERIC = 2` |  |
| `static final int NVARCHAR = -9` |  |
| `static final int OTHER = 1111` |  |
| `static final int REAL = 7` |  |
| `static final int REF = 2006` |  |
| `static final int ROWID = -8` |  |
| `static final int SMALLINT = 5` |  |
| `static final int SQLXML = 2009` |  |
| `static final int STRUCT = 2002` |  |
| `static final int TIME = 92` |  |
| `static final int TIMESTAMP = 93` |  |
| `static final int TINYINT = -6` |  |
| `static final int VARBINARY = -3` |  |
| `static final int VARCHAR = 12` |  |

---

### `interface Wrapper`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isWrapperFor(Class<?>) throws java.sql.SQLException` |  |
| `<T> T unwrap(Class<T>) throws java.sql.SQLException` |  |

---

## Package: `java.text`

### `class Annotation`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Annotation(Object)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object getValue()` |  |

---

### `interface AttributedCharacterIterator`

- **Extends:** `java.text.CharacterIterator`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Set<java.text.AttributedCharacterIterator.Attribute> getAllAttributeKeys()` |  |
| `Object getAttribute(java.text.AttributedCharacterIterator.Attribute)` |  |
| `java.util.Map<java.text.AttributedCharacterIterator.Attribute,java.lang.Object> getAttributes()` |  |
| `int getRunLimit()` |  |
| `int getRunLimit(java.text.AttributedCharacterIterator.Attribute)` |  |
| `int getRunLimit(java.util.Set<? extends java.text.AttributedCharacterIterator.Attribute>)` |  |
| `int getRunStart()` |  |
| `int getRunStart(java.text.AttributedCharacterIterator.Attribute)` |  |
| `int getRunStart(java.util.Set<? extends java.text.AttributedCharacterIterator.Attribute>)` |  |

---

### `class static AttributedCharacterIterator.Attribute`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AttributedCharacterIterator.Attribute(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.text.AttributedCharacterIterator.Attribute INPUT_METHOD_SEGMENT` |  |
| `static final java.text.AttributedCharacterIterator.Attribute LANGUAGE` |  |
| `static final java.text.AttributedCharacterIterator.Attribute READING` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final boolean equals(Object)` |  |
| `String getName()` |  |
| `final int hashCode()` |  |
| `Object readResolve() throws java.io.InvalidObjectException` |  |

---

### `class AttributedString`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AttributedString(String)` |  |
| `AttributedString(String, java.util.Map<? extends java.text.AttributedCharacterIterator.Attribute,?>)` |  |
| `AttributedString(java.text.AttributedCharacterIterator)` |  |
| `AttributedString(java.text.AttributedCharacterIterator, int, int)` |  |
| `AttributedString(java.text.AttributedCharacterIterator, int, int, java.text.AttributedCharacterIterator.Attribute[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addAttribute(java.text.AttributedCharacterIterator.Attribute, Object)` |  |
| `void addAttribute(java.text.AttributedCharacterIterator.Attribute, Object, int, int)` |  |
| `void addAttributes(java.util.Map<? extends java.text.AttributedCharacterIterator.Attribute,?>, int, int)` |  |
| `java.text.AttributedCharacterIterator getIterator()` |  |
| `java.text.AttributedCharacterIterator getIterator(java.text.AttributedCharacterIterator.Attribute[])` |  |
| `java.text.AttributedCharacterIterator getIterator(java.text.AttributedCharacterIterator.Attribute[], int, int)` |  |

---

### `class final Bidi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Bidi(String, int)` |  |
| `Bidi(java.text.AttributedCharacterIterator)` |  |
| `Bidi(char[], int, byte[], int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DIRECTION_DEFAULT_LEFT_TO_RIGHT = -2` |  |
| `static final int DIRECTION_DEFAULT_RIGHT_TO_LEFT = -1` |  |
| `static final int DIRECTION_LEFT_TO_RIGHT = 0` |  |
| `static final int DIRECTION_RIGHT_TO_LEFT = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean baseIsLeftToRight()` |  |
| `java.text.Bidi createLineBidi(int, int)` |  |
| `int getBaseLevel()` |  |
| `int getLength()` |  |
| `int getLevelAt(int)` |  |
| `int getRunCount()` |  |
| `int getRunLevel(int)` |  |
| `int getRunLimit(int)` |  |
| `int getRunStart(int)` |  |
| `boolean isLeftToRight()` |  |
| `boolean isMixed()` |  |
| `boolean isRightToLeft()` |  |
| `static void reorderVisually(byte[], int, Object[], int, int)` |  |
| `static boolean requiresBidi(char[], int, int)` |  |

---

### `class abstract BreakIterator`

- **Implements:** `java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BreakIterator()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DONE = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `abstract int current()` |  |
| `abstract int first()` |  |
| `abstract int following(int)` |  |
| `static java.util.Locale[] getAvailableLocales()` |  |
| `static java.text.BreakIterator getCharacterInstance()` |  |
| `static java.text.BreakIterator getCharacterInstance(java.util.Locale)` |  |
| `static java.text.BreakIterator getLineInstance()` |  |
| `static java.text.BreakIterator getLineInstance(java.util.Locale)` |  |
| `static java.text.BreakIterator getSentenceInstance()` |  |
| `static java.text.BreakIterator getSentenceInstance(java.util.Locale)` |  |
| `abstract java.text.CharacterIterator getText()` |  |
| `static java.text.BreakIterator getWordInstance()` |  |
| `static java.text.BreakIterator getWordInstance(java.util.Locale)` |  |
| `boolean isBoundary(int)` |  |
| `abstract int last()` |  |
| `abstract int next(int)` |  |
| `abstract int next()` |  |
| `int preceding(int)` |  |
| `abstract int previous()` |  |
| `void setText(String)` |  |
| `abstract void setText(java.text.CharacterIterator)` |  |

---

### `interface CharacterIterator`

- **Extends:** `java.lang.Cloneable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final char DONE = 65535` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `char current()` |  |
| `char first()` |  |
| `int getBeginIndex()` |  |
| `int getEndIndex()` |  |
| `int getIndex()` |  |
| `char last()` |  |
| `char next()` |  |
| `char previous()` |  |
| `char setIndex(int)` |  |

---

### `class ChoiceFormat`

- **Extends:** `java.text.NumberFormat`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ChoiceFormat(String)` |  |
| `ChoiceFormat(double[], String[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void applyPattern(String)` |  |
| `StringBuffer format(long, StringBuffer, java.text.FieldPosition)` |  |
| `StringBuffer format(double, StringBuffer, java.text.FieldPosition)` |  |
| `Object[] getFormats()` |  |
| `double[] getLimits()` |  |
| `static final double nextDouble(double)` |  |
| `static double nextDouble(double, boolean)` |  |
| `Number parse(String, java.text.ParsePosition)` |  |
| `static final double previousDouble(double)` |  |
| `void setChoices(double[], String[])` |  |
| `String toPattern()` |  |

---

### `class final CollationElementIterator`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int NULLORDER = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getMaxExpansion(int)` |  |
| `int getOffset()` |  |
| `int next()` |  |
| `int previous()` |  |
| `static int primaryOrder(int)` |  |
| `void reset()` |  |
| `static short secondaryOrder(int)` |  |
| `void setOffset(int)` |  |
| `void setText(String)` |  |
| `void setText(java.text.CharacterIterator)` |  |
| `static short tertiaryOrder(int)` |  |

---

### `class abstract CollationKey`

- **Implements:** `java.lang.Comparable<java.text.CollationKey>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CollationKey(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract int compareTo(java.text.CollationKey)` |  |
| `String getSourceString()` |  |
| `abstract byte[] toByteArray()` |  |

---

### `class abstract Collator`

- **Implements:** `java.lang.Cloneable java.util.Comparator<java.lang.Object>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Collator()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CANONICAL_DECOMPOSITION = 1` |  |
| `static final int FULL_DECOMPOSITION = 2` |  |
| `static final int IDENTICAL = 3` |  |
| `static final int NO_DECOMPOSITION = 0` |  |
| `static final int PRIMARY = 0` |  |
| `static final int SECONDARY = 1` |  |
| `static final int TERTIARY = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `abstract int compare(String, String)` |  |
| `int compare(Object, Object)` |  |
| `boolean equals(String, String)` |  |
| `static java.util.Locale[] getAvailableLocales()` |  |
| `abstract java.text.CollationKey getCollationKey(String)` |  |
| `int getDecomposition()` |  |
| `static java.text.Collator getInstance()` |  |
| `static java.text.Collator getInstance(java.util.Locale)` |  |
| `int getStrength()` |  |
| `abstract int hashCode()` |  |
| `void setDecomposition(int)` |  |
| `void setStrength(int)` |  |

---

### `class abstract DateFormat`

- **Extends:** `java.text.Format`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateFormat()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AM_PM_FIELD = 14` |  |
| `static final int DATE_FIELD = 3` |  |
| `static final int DAY_OF_WEEK_FIELD = 9` |  |
| `static final int DAY_OF_WEEK_IN_MONTH_FIELD = 11` |  |
| `static final int DAY_OF_YEAR_FIELD = 10` |  |
| `static final int DEFAULT = 2` |  |
| `static final int ERA_FIELD = 0` |  |
| `static final int FULL = 0` |  |
| `static final int HOUR0_FIELD = 16` |  |
| `static final int HOUR1_FIELD = 15` |  |
| `static final int HOUR_OF_DAY0_FIELD = 5` |  |
| `static final int HOUR_OF_DAY1_FIELD = 4` |  |
| `static final int LONG = 1` |  |
| `static final int MEDIUM = 2` |  |
| `static final int MILLISECOND_FIELD = 8` |  |
| `static final int MINUTE_FIELD = 6` |  |
| `static final int MONTH_FIELD = 2` |  |
| `static final int SECOND_FIELD = 7` |  |
| `static final int SHORT = 3` |  |
| `static final int TIMEZONE_FIELD = 17` |  |
| `static final int WEEK_OF_MONTH_FIELD = 13` |  |
| `static final int WEEK_OF_YEAR_FIELD = 12` |  |
| `static final int YEAR_FIELD = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isLenient()` |  |
| `void setCalendar(@NonNull java.util.Calendar)` |  |
| `void setLenient(boolean)` |  |
| `void setNumberFormat(@NonNull java.text.NumberFormat)` |  |
| `void setTimeZone(@NonNull java.util.TimeZone)` |  |

---

### `class static DateFormat.Field`

- **Extends:** `java.text.Format.Field`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateFormat.Field(@NonNull String, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getCalendarField()` |  |

---

### `class DateFormatSymbols`

- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateFormatSymbols()` |  |
| `DateFormatSymbols(java.util.Locale)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `String[] getAmPmStrings()` |  |
| `static java.util.Locale[] getAvailableLocales()` |  |
| `String[] getEras()` |  |
| `static final java.text.DateFormatSymbols getInstance()` |  |
| `static final java.text.DateFormatSymbols getInstance(java.util.Locale)` |  |
| `String getLocalPatternChars()` |  |
| `String[] getMonths()` |  |
| `String[] getShortMonths()` |  |
| `String[] getShortWeekdays()` |  |
| `String[] getWeekdays()` |  |
| `String[][] getZoneStrings()` |  |
| `void setAmPmStrings(String[])` |  |
| `void setEras(String[])` |  |
| `void setLocalPatternChars(String)` |  |
| `void setMonths(String[])` |  |
| `void setShortMonths(String[])` |  |
| `void setShortWeekdays(String[])` |  |
| `void setWeekdays(String[])` |  |
| `void setZoneStrings(String[][])` |  |

---

### `class DecimalFormat`

- **Extends:** `java.text.NumberFormat`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DecimalFormat()` |  |
| `DecimalFormat(String)` |  |
| `DecimalFormat(String, java.text.DecimalFormatSymbols)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void applyLocalizedPattern(String)` |  |
| `void applyPattern(String)` |  |
| `final StringBuffer format(Object, StringBuffer, java.text.FieldPosition)` |  |
| `StringBuffer format(double, StringBuffer, java.text.FieldPosition)` |  |
| `StringBuffer format(long, StringBuffer, java.text.FieldPosition)` |  |
| `java.text.DecimalFormatSymbols getDecimalFormatSymbols()` |  |
| `int getGroupingSize()` |  |
| `int getMultiplier()` |  |
| `String getNegativePrefix()` |  |
| `String getNegativeSuffix()` |  |
| `String getPositivePrefix()` |  |
| `String getPositiveSuffix()` |  |
| `boolean isDecimalSeparatorAlwaysShown()` |  |
| `boolean isParseBigDecimal()` |  |
| `Number parse(String, java.text.ParsePosition)` |  |
| `void setDecimalFormatSymbols(java.text.DecimalFormatSymbols)` |  |
| `void setDecimalSeparatorAlwaysShown(boolean)` |  |
| `void setGroupingSize(int)` |  |
| `void setMultiplier(int)` |  |
| `void setNegativePrefix(String)` |  |
| `void setNegativeSuffix(String)` |  |
| `void setParseBigDecimal(boolean)` |  |
| `void setPositivePrefix(String)` |  |
| `void setPositiveSuffix(String)` |  |
| `String toLocalizedPattern()` |  |
| `String toPattern()` |  |

---

### `class DecimalFormatSymbols`

- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DecimalFormatSymbols()` |  |
| `DecimalFormatSymbols(java.util.Locale)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `static java.util.Locale[] getAvailableLocales()` |  |
| `java.util.Currency getCurrency()` |  |
| `String getCurrencySymbol()` |  |
| `char getDecimalSeparator()` |  |
| `char getDigit()` |  |
| `String getExponentSeparator()` |  |
| `char getGroupingSeparator()` |  |
| `String getInfinity()` |  |
| `static final java.text.DecimalFormatSymbols getInstance()` |  |
| `static final java.text.DecimalFormatSymbols getInstance(java.util.Locale)` |  |
| `String getInternationalCurrencySymbol()` |  |
| `char getMinusSign()` |  |
| `char getMonetaryDecimalSeparator()` |  |
| `String getNaN()` |  |
| `char getPatternSeparator()` |  |
| `char getPerMill()` |  |
| `char getPercent()` |  |
| `char getZeroDigit()` |  |
| `void setCurrency(java.util.Currency)` |  |
| `void setCurrencySymbol(String)` |  |
| `void setDecimalSeparator(char)` |  |
| `void setDigit(char)` |  |
| `void setExponentSeparator(String)` |  |
| `void setGroupingSeparator(char)` |  |
| `void setInfinity(String)` |  |
| `void setInternationalCurrencySymbol(String)` |  |
| `void setMinusSign(char)` |  |
| `void setMonetaryDecimalSeparator(char)` |  |
| `void setNaN(String)` |  |
| `void setPatternSeparator(char)` |  |
| `void setPerMill(char)` |  |
| `void setPercent(char)` |  |
| `void setZeroDigit(char)` |  |

---

### `class FieldPosition`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FieldPosition(int)` |  |
| `FieldPosition(java.text.Format.Field)` |  |
| `FieldPosition(java.text.Format.Field, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getBeginIndex()` |  |
| `int getEndIndex()` |  |
| `int getField()` |  |
| `java.text.Format.Field getFieldAttribute()` |  |
| `void setBeginIndex(int)` |  |
| `void setEndIndex(int)` |  |

---

### `class abstract Format`

- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Format()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `final String format(Object)` |  |
| `abstract StringBuffer format(Object, StringBuffer, java.text.FieldPosition)` |  |
| `java.text.AttributedCharacterIterator formatToCharacterIterator(Object)` |  |
| `abstract Object parseObject(String, java.text.ParsePosition)` |  |
| `Object parseObject(String) throws java.text.ParseException` |  |

---

### `class static Format.Field`

- **Extends:** `java.text.AttributedCharacterIterator.Attribute`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Format.Field(String)` |  |

---

### `class MessageFormat`

- **Extends:** `java.text.Format`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MessageFormat(String)` |  |
| `MessageFormat(String, java.util.Locale)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void applyPattern(String)` |  |
| `final StringBuffer format(Object[], StringBuffer, java.text.FieldPosition)` |  |
| `static String format(String, java.lang.Object...)` |  |
| `final StringBuffer format(Object, StringBuffer, java.text.FieldPosition)` |  |
| `java.text.Format[] getFormats()` |  |
| `java.text.Format[] getFormatsByArgumentIndex()` |  |
| `java.util.Locale getLocale()` |  |
| `Object[] parse(String, java.text.ParsePosition)` |  |
| `Object[] parse(String) throws java.text.ParseException` |  |
| `Object parseObject(String, java.text.ParsePosition)` |  |
| `void setFormat(int, java.text.Format)` |  |
| `void setFormatByArgumentIndex(int, java.text.Format)` |  |
| `void setFormats(java.text.Format[])` |  |
| `void setFormatsByArgumentIndex(java.text.Format[])` |  |
| `void setLocale(java.util.Locale)` |  |
| `String toPattern()` |  |

---

### `class static MessageFormat.Field`

- **Extends:** `java.text.Format.Field`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MessageFormat.Field(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.text.MessageFormat.Field ARGUMENT` |  |

---

### `class final Normalizer`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static boolean isNormalized(CharSequence, java.text.Normalizer.Form)` |  |
| `static String normalize(CharSequence, java.text.Normalizer.Form)` |  |

---

### `enum Normalizer.Form`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.text.Normalizer.Form NFC` |  |
| `static final java.text.Normalizer.Form NFD` |  |
| `static final java.text.Normalizer.Form NFKC` |  |
| `static final java.text.Normalizer.Form NFKD` |  |

---

### `class abstract NumberFormat`

- **Extends:** `java.text.Format`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NumberFormat()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FRACTION_FIELD = 1` |  |
| `static final int INTEGER_FIELD = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getMaximumFractionDigits()` |  |
| `int getMaximumIntegerDigits()` |  |
| `int getMinimumFractionDigits()` |  |
| `int getMinimumIntegerDigits()` |  |
| `boolean isGroupingUsed()` |  |
| `boolean isParseIntegerOnly()` |  |
| `void setCurrency(@NonNull java.util.Currency)` |  |
| `void setGroupingUsed(boolean)` |  |
| `void setMaximumFractionDigits(int)` |  |
| `void setMaximumIntegerDigits(int)` |  |
| `void setMinimumFractionDigits(int)` |  |
| `void setMinimumIntegerDigits(int)` |  |
| `void setParseIntegerOnly(boolean)` |  |
| `void setRoundingMode(@Nullable java.math.RoundingMode)` |  |

---

### `class static NumberFormat.Field`

- **Extends:** `java.text.Format.Field`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NumberFormat.Field(@NonNull String)` |  |

---

### `class ParseException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ParseException(String, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getErrorOffset()` |  |

---

### `class ParsePosition`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ParsePosition(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getErrorIndex()` |  |
| `int getIndex()` |  |
| `void setErrorIndex(int)` |  |
| `void setIndex(int)` |  |

---

### `class RuleBasedCollator`

- **Extends:** `java.text.Collator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RuleBasedCollator(String) throws java.text.ParseException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compare(String, String)` |  |
| `java.text.CollationElementIterator getCollationElementIterator(String)` |  |
| `java.text.CollationElementIterator getCollationElementIterator(java.text.CharacterIterator)` |  |
| `java.text.CollationKey getCollationKey(String)` |  |
| `String getRules()` |  |

---

### `class SimpleDateFormat`

- **Extends:** `java.text.DateFormat`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SimpleDateFormat()` |  |
| `SimpleDateFormat(String)` |  |
| `SimpleDateFormat(String, java.util.Locale)` |  |
| `SimpleDateFormat(String, java.text.DateFormatSymbols)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void applyLocalizedPattern(String)` |  |
| `void applyPattern(String)` |  |
| `StringBuffer format(java.util.Date, StringBuffer, java.text.FieldPosition)` |  |
| `java.util.Date get2DigitYearStart()` |  |
| `java.text.DateFormatSymbols getDateFormatSymbols()` |  |
| `java.util.Date parse(String, java.text.ParsePosition)` |  |
| `void set2DigitYearStart(java.util.Date)` |  |
| `void setDateFormatSymbols(java.text.DateFormatSymbols)` |  |
| `String toLocalizedPattern()` |  |
| `String toPattern()` |  |

---

### `class final StringCharacterIterator`

- **Implements:** `java.text.CharacterIterator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StringCharacterIterator(String)` |  |
| `StringCharacterIterator(String, int)` |  |
| `StringCharacterIterator(String, int, int, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `char current()` |  |
| `char first()` |  |
| `int getBeginIndex()` |  |
| `int getEndIndex()` |  |
| `int getIndex()` |  |
| `char last()` |  |
| `char next()` |  |
| `char previous()` |  |
| `char setIndex(int)` |  |
| `void setText(String)` |  |

---

## Package: `java.time`

### `class abstract Clock`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Clock()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.time.Clock fixed(java.time.Instant, java.time.ZoneId)` |  |
| `abstract java.time.ZoneId getZone()` |  |
| `abstract java.time.Instant instant()` |  |
| `long millis()` |  |
| `static java.time.Clock offset(java.time.Clock, java.time.Duration)` |  |
| `static java.time.Clock system(java.time.ZoneId)` |  |
| `static java.time.Clock systemDefaultZone()` |  |
| `static java.time.Clock systemUTC()` |  |
| `static java.time.Clock tick(java.time.Clock, java.time.Duration)` |  |
| `static java.time.Clock tickMinutes(java.time.ZoneId)` |  |
| `static java.time.Clock tickSeconds(java.time.ZoneId)` |  |
| `abstract java.time.Clock withZone(java.time.ZoneId)` |  |

---

### `class DateTimeException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateTimeException(String)` |  |
| `DateTimeException(String, Throwable)` |  |

---

### `enum DayOfWeek`

- **Implements:** `java.time.temporal.TemporalAccessor java.time.temporal.TemporalAdjuster`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.DayOfWeek FRIDAY` |  |
| `static final java.time.DayOfWeek MONDAY` |  |
| `static final java.time.DayOfWeek SATURDAY` |  |
| `static final java.time.DayOfWeek SUNDAY` |  |
| `static final java.time.DayOfWeek THURSDAY` |  |
| `static final java.time.DayOfWeek TUESDAY` |  |
| `static final java.time.DayOfWeek WEDNESDAY` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `static java.time.DayOfWeek from(java.time.temporal.TemporalAccessor)` |  |
| `String getDisplayName(java.time.format.TextStyle, java.util.Locale)` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `int getValue()` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `java.time.DayOfWeek minus(long)` |  |
| `static java.time.DayOfWeek of(int)` |  |
| `java.time.DayOfWeek plus(long)` |  |

---

### `class final Duration`

- **Implements:** `java.lang.Comparable<java.time.Duration> java.io.Serializable java.time.temporal.TemporalAmount`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.Duration ZERO` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.Duration abs()` |  |
| `java.time.temporal.Temporal addTo(java.time.temporal.Temporal)` |  |
| `static java.time.Duration between(java.time.temporal.Temporal, java.time.temporal.Temporal)` |  |
| `int compareTo(java.time.Duration)` |  |
| `java.time.Duration dividedBy(long)` |  |
| `static java.time.Duration from(java.time.temporal.TemporalAmount)` |  |
| `long get(java.time.temporal.TemporalUnit)` |  |
| `int getNano()` |  |
| `long getSeconds()` |  |
| `java.util.List<java.time.temporal.TemporalUnit> getUnits()` |  |
| `boolean isNegative()` |  |
| `boolean isZero()` |  |
| `java.time.Duration minus(java.time.Duration)` |  |
| `java.time.Duration minus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.Duration minusDays(long)` |  |
| `java.time.Duration minusHours(long)` |  |
| `java.time.Duration minusMillis(long)` |  |
| `java.time.Duration minusMinutes(long)` |  |
| `java.time.Duration minusNanos(long)` |  |
| `java.time.Duration minusSeconds(long)` |  |
| `java.time.Duration multipliedBy(long)` |  |
| `java.time.Duration negated()` |  |
| `static java.time.Duration of(long, java.time.temporal.TemporalUnit)` |  |
| `static java.time.Duration ofDays(long)` |  |
| `static java.time.Duration ofHours(long)` |  |
| `static java.time.Duration ofMillis(long)` |  |
| `static java.time.Duration ofMinutes(long)` |  |
| `static java.time.Duration ofNanos(long)` |  |
| `static java.time.Duration ofSeconds(long)` |  |
| `static java.time.Duration ofSeconds(long, long)` |  |
| `static java.time.Duration parse(CharSequence)` |  |
| `java.time.Duration plus(java.time.Duration)` |  |
| `java.time.Duration plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.Duration plusDays(long)` |  |
| `java.time.Duration plusHours(long)` |  |
| `java.time.Duration plusMillis(long)` |  |
| `java.time.Duration plusMinutes(long)` |  |
| `java.time.Duration plusNanos(long)` |  |
| `java.time.Duration plusSeconds(long)` |  |
| `java.time.temporal.Temporal subtractFrom(java.time.temporal.Temporal)` |  |
| `long toDays()` |  |
| `long toHours()` |  |
| `long toMillis()` |  |
| `long toMinutes()` |  |
| `long toNanos()` |  |
| `java.time.Duration withNanos(int)` |  |
| `java.time.Duration withSeconds(long)` |  |

---

### `class final Instant`

- **Implements:** `java.lang.Comparable<java.time.Instant> java.io.Serializable java.time.temporal.Temporal java.time.temporal.TemporalAdjuster`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.Instant EPOCH` |  |
| `static final java.time.Instant MAX` |  |
| `static final java.time.Instant MIN` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `java.time.OffsetDateTime atOffset(java.time.ZoneOffset)` |  |
| `java.time.ZonedDateTime atZone(java.time.ZoneId)` |  |
| `int compareTo(java.time.Instant)` |  |
| `static java.time.Instant from(java.time.temporal.TemporalAccessor)` |  |
| `long getEpochSecond()` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `int getNano()` |  |
| `boolean isAfter(java.time.Instant)` |  |
| `boolean isBefore(java.time.Instant)` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `boolean isSupported(java.time.temporal.TemporalUnit)` |  |
| `java.time.Instant minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.Instant minus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.Instant minusMillis(long)` |  |
| `java.time.Instant minusNanos(long)` |  |
| `java.time.Instant minusSeconds(long)` |  |
| `static java.time.Instant now()` |  |
| `static java.time.Instant now(java.time.Clock)` |  |
| `static java.time.Instant ofEpochMilli(long)` |  |
| `static java.time.Instant ofEpochSecond(long)` |  |
| `static java.time.Instant ofEpochSecond(long, long)` |  |
| `static java.time.Instant parse(CharSequence)` |  |
| `java.time.Instant plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.Instant plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.Instant plusMillis(long)` |  |
| `java.time.Instant plusNanos(long)` |  |
| `java.time.Instant plusSeconds(long)` |  |
| `long toEpochMilli()` |  |
| `java.time.Instant truncatedTo(java.time.temporal.TemporalUnit)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.Instant with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.Instant with(java.time.temporal.TemporalField, long)` |  |

---

### `class final LocalDate`

- **Implements:** `java.time.chrono.ChronoLocalDate java.io.Serializable java.time.temporal.Temporal java.time.temporal.TemporalAdjuster`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.LocalDate MAX` |  |
| `static final java.time.LocalDate MIN` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.LocalDateTime atStartOfDay()` |  |
| `java.time.ZonedDateTime atStartOfDay(java.time.ZoneId)` |  |
| `java.time.LocalDateTime atTime(java.time.LocalTime)` |  |
| `java.time.LocalDateTime atTime(int, int)` |  |
| `java.time.LocalDateTime atTime(int, int, int)` |  |
| `java.time.LocalDateTime atTime(int, int, int, int)` |  |
| `java.time.OffsetDateTime atTime(java.time.OffsetTime)` |  |
| `static java.time.LocalDate from(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.IsoChronology getChronology()` |  |
| `int getDayOfMonth()` |  |
| `java.time.DayOfWeek getDayOfWeek()` |  |
| `int getDayOfYear()` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `java.time.Month getMonth()` |  |
| `int getMonthValue()` |  |
| `int getYear()` |  |
| `int lengthOfMonth()` |  |
| `java.time.LocalDate minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.LocalDate minus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.LocalDate minusDays(long)` |  |
| `java.time.LocalDate minusMonths(long)` |  |
| `java.time.LocalDate minusWeeks(long)` |  |
| `java.time.LocalDate minusYears(long)` |  |
| `static java.time.LocalDate now()` |  |
| `static java.time.LocalDate now(java.time.ZoneId)` |  |
| `static java.time.LocalDate now(java.time.Clock)` |  |
| `static java.time.LocalDate of(int, java.time.Month, int)` |  |
| `static java.time.LocalDate of(int, int, int)` |  |
| `static java.time.LocalDate ofEpochDay(long)` |  |
| `static java.time.LocalDate ofYearDay(int, int)` |  |
| `static java.time.LocalDate parse(CharSequence)` |  |
| `static java.time.LocalDate parse(CharSequence, java.time.format.DateTimeFormatter)` |  |
| `java.time.LocalDate plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.LocalDate plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.LocalDate plusDays(long)` |  |
| `java.time.LocalDate plusMonths(long)` |  |
| `java.time.LocalDate plusWeeks(long)` |  |
| `java.time.LocalDate plusYears(long)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.Period until(java.time.chrono.ChronoLocalDate)` |  |
| `java.time.LocalDate with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.LocalDate with(java.time.temporal.TemporalField, long)` |  |
| `java.time.LocalDate withDayOfMonth(int)` |  |
| `java.time.LocalDate withDayOfYear(int)` |  |
| `java.time.LocalDate withMonth(int)` |  |
| `java.time.LocalDate withYear(int)` |  |

---

### `class final LocalDateTime`

- **Implements:** `java.time.chrono.ChronoLocalDateTime<java.time.LocalDate> java.io.Serializable java.time.temporal.Temporal java.time.temporal.TemporalAdjuster`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.LocalDateTime MAX` |  |
| `static final java.time.LocalDateTime MIN` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.OffsetDateTime atOffset(java.time.ZoneOffset)` |  |
| `java.time.ZonedDateTime atZone(java.time.ZoneId)` |  |
| `static java.time.LocalDateTime from(java.time.temporal.TemporalAccessor)` |  |
| `int getDayOfMonth()` |  |
| `java.time.DayOfWeek getDayOfWeek()` |  |
| `int getDayOfYear()` |  |
| `int getHour()` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `int getMinute()` |  |
| `java.time.Month getMonth()` |  |
| `int getMonthValue()` |  |
| `int getNano()` |  |
| `int getSecond()` |  |
| `int getYear()` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `java.time.LocalDateTime minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.LocalDateTime minus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.LocalDateTime minusDays(long)` |  |
| `java.time.LocalDateTime minusHours(long)` |  |
| `java.time.LocalDateTime minusMinutes(long)` |  |
| `java.time.LocalDateTime minusMonths(long)` |  |
| `java.time.LocalDateTime minusNanos(long)` |  |
| `java.time.LocalDateTime minusSeconds(long)` |  |
| `java.time.LocalDateTime minusWeeks(long)` |  |
| `java.time.LocalDateTime minusYears(long)` |  |
| `static java.time.LocalDateTime now()` |  |
| `static java.time.LocalDateTime now(java.time.ZoneId)` |  |
| `static java.time.LocalDateTime now(java.time.Clock)` |  |
| `static java.time.LocalDateTime of(int, java.time.Month, int, int, int)` |  |
| `static java.time.LocalDateTime of(int, java.time.Month, int, int, int, int)` |  |
| `static java.time.LocalDateTime of(int, java.time.Month, int, int, int, int, int)` |  |
| `static java.time.LocalDateTime of(int, int, int, int, int)` |  |
| `static java.time.LocalDateTime of(int, int, int, int, int, int)` |  |
| `static java.time.LocalDateTime of(int, int, int, int, int, int, int)` |  |
| `static java.time.LocalDateTime of(java.time.LocalDate, java.time.LocalTime)` |  |
| `static java.time.LocalDateTime ofEpochSecond(long, int, java.time.ZoneOffset)` |  |
| `static java.time.LocalDateTime ofInstant(java.time.Instant, java.time.ZoneId)` |  |
| `static java.time.LocalDateTime parse(CharSequence)` |  |
| `static java.time.LocalDateTime parse(CharSequence, java.time.format.DateTimeFormatter)` |  |
| `java.time.LocalDateTime plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.LocalDateTime plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.LocalDateTime plusDays(long)` |  |
| `java.time.LocalDateTime plusHours(long)` |  |
| `java.time.LocalDateTime plusMinutes(long)` |  |
| `java.time.LocalDateTime plusMonths(long)` |  |
| `java.time.LocalDateTime plusNanos(long)` |  |
| `java.time.LocalDateTime plusSeconds(long)` |  |
| `java.time.LocalDateTime plusWeeks(long)` |  |
| `java.time.LocalDateTime plusYears(long)` |  |
| `java.time.LocalDate toLocalDate()` |  |
| `java.time.LocalTime toLocalTime()` |  |
| `java.time.LocalDateTime truncatedTo(java.time.temporal.TemporalUnit)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.LocalDateTime with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.LocalDateTime with(java.time.temporal.TemporalField, long)` |  |
| `java.time.LocalDateTime withDayOfMonth(int)` |  |
| `java.time.LocalDateTime withDayOfYear(int)` |  |
| `java.time.LocalDateTime withHour(int)` |  |
| `java.time.LocalDateTime withMinute(int)` |  |
| `java.time.LocalDateTime withMonth(int)` |  |
| `java.time.LocalDateTime withNano(int)` |  |
| `java.time.LocalDateTime withSecond(int)` |  |
| `java.time.LocalDateTime withYear(int)` |  |

---

### `class final LocalTime`

- **Implements:** `java.lang.Comparable<java.time.LocalTime> java.io.Serializable java.time.temporal.Temporal java.time.temporal.TemporalAdjuster`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.LocalTime MAX` |  |
| `static final java.time.LocalTime MIDNIGHT` |  |
| `static final java.time.LocalTime MIN` |  |
| `static final java.time.LocalTime NOON` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `java.time.LocalDateTime atDate(java.time.LocalDate)` |  |
| `java.time.OffsetTime atOffset(java.time.ZoneOffset)` |  |
| `int compareTo(java.time.LocalTime)` |  |
| `String format(java.time.format.DateTimeFormatter)` |  |
| `static java.time.LocalTime from(java.time.temporal.TemporalAccessor)` |  |
| `int getHour()` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `int getMinute()` |  |
| `int getNano()` |  |
| `int getSecond()` |  |
| `boolean isAfter(java.time.LocalTime)` |  |
| `boolean isBefore(java.time.LocalTime)` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `boolean isSupported(java.time.temporal.TemporalUnit)` |  |
| `java.time.LocalTime minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.LocalTime minus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.LocalTime minusHours(long)` |  |
| `java.time.LocalTime minusMinutes(long)` |  |
| `java.time.LocalTime minusNanos(long)` |  |
| `java.time.LocalTime minusSeconds(long)` |  |
| `static java.time.LocalTime now()` |  |
| `static java.time.LocalTime now(java.time.ZoneId)` |  |
| `static java.time.LocalTime now(java.time.Clock)` |  |
| `static java.time.LocalTime of(int, int)` |  |
| `static java.time.LocalTime of(int, int, int)` |  |
| `static java.time.LocalTime of(int, int, int, int)` |  |
| `static java.time.LocalTime ofNanoOfDay(long)` |  |
| `static java.time.LocalTime ofSecondOfDay(long)` |  |
| `static java.time.LocalTime parse(CharSequence)` |  |
| `static java.time.LocalTime parse(CharSequence, java.time.format.DateTimeFormatter)` |  |
| `java.time.LocalTime plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.LocalTime plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.LocalTime plusHours(long)` |  |
| `java.time.LocalTime plusMinutes(long)` |  |
| `java.time.LocalTime plusNanos(long)` |  |
| `java.time.LocalTime plusSeconds(long)` |  |
| `long toNanoOfDay()` |  |
| `int toSecondOfDay()` |  |
| `java.time.LocalTime truncatedTo(java.time.temporal.TemporalUnit)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.LocalTime with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.LocalTime with(java.time.temporal.TemporalField, long)` |  |
| `java.time.LocalTime withHour(int)` |  |
| `java.time.LocalTime withMinute(int)` |  |
| `java.time.LocalTime withNano(int)` |  |
| `java.time.LocalTime withSecond(int)` |  |

---

### `enum Month`

- **Implements:** `java.time.temporal.TemporalAccessor java.time.temporal.TemporalAdjuster`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.Month APRIL` |  |
| `static final java.time.Month AUGUST` |  |
| `static final java.time.Month DECEMBER` |  |
| `static final java.time.Month FEBRUARY` |  |
| `static final java.time.Month JANUARY` |  |
| `static final java.time.Month JULY` |  |
| `static final java.time.Month JUNE` |  |
| `static final java.time.Month MARCH` |  |
| `static final java.time.Month MAY` |  |
| `static final java.time.Month NOVEMBER` |  |
| `static final java.time.Month OCTOBER` |  |
| `static final java.time.Month SEPTEMBER` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `int firstDayOfYear(boolean)` |  |
| `java.time.Month firstMonthOfQuarter()` |  |
| `static java.time.Month from(java.time.temporal.TemporalAccessor)` |  |
| `String getDisplayName(java.time.format.TextStyle, java.util.Locale)` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `int getValue()` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `int length(boolean)` |  |
| `int maxLength()` |  |
| `int minLength()` |  |
| `java.time.Month minus(long)` |  |
| `static java.time.Month of(int)` |  |
| `java.time.Month plus(long)` |  |

---

### `class final MonthDay`

- **Implements:** `java.lang.Comparable<java.time.MonthDay> java.io.Serializable java.time.temporal.TemporalAccessor java.time.temporal.TemporalAdjuster`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `java.time.LocalDate atYear(int)` |  |
| `int compareTo(java.time.MonthDay)` |  |
| `String format(java.time.format.DateTimeFormatter)` |  |
| `static java.time.MonthDay from(java.time.temporal.TemporalAccessor)` |  |
| `int getDayOfMonth()` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `java.time.Month getMonth()` |  |
| `int getMonthValue()` |  |
| `boolean isAfter(java.time.MonthDay)` |  |
| `boolean isBefore(java.time.MonthDay)` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `boolean isValidYear(int)` |  |
| `static java.time.MonthDay now()` |  |
| `static java.time.MonthDay now(java.time.ZoneId)` |  |
| `static java.time.MonthDay now(java.time.Clock)` |  |
| `static java.time.MonthDay of(java.time.Month, int)` |  |
| `static java.time.MonthDay of(int, int)` |  |
| `static java.time.MonthDay parse(CharSequence)` |  |
| `static java.time.MonthDay parse(CharSequence, java.time.format.DateTimeFormatter)` |  |
| `java.time.MonthDay with(java.time.Month)` |  |
| `java.time.MonthDay withDayOfMonth(int)` |  |
| `java.time.MonthDay withMonth(int)` |  |

---

### `class final OffsetDateTime`

- **Implements:** `java.lang.Comparable<java.time.OffsetDateTime> java.io.Serializable java.time.temporal.Temporal java.time.temporal.TemporalAdjuster`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.OffsetDateTime MAX` |  |
| `static final java.time.OffsetDateTime MIN` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `java.time.ZonedDateTime atZoneSameInstant(java.time.ZoneId)` |  |
| `java.time.ZonedDateTime atZoneSimilarLocal(java.time.ZoneId)` |  |
| `int compareTo(java.time.OffsetDateTime)` |  |
| `String format(java.time.format.DateTimeFormatter)` |  |
| `static java.time.OffsetDateTime from(java.time.temporal.TemporalAccessor)` |  |
| `int getDayOfMonth()` |  |
| `java.time.DayOfWeek getDayOfWeek()` |  |
| `int getDayOfYear()` |  |
| `int getHour()` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `int getMinute()` |  |
| `java.time.Month getMonth()` |  |
| `int getMonthValue()` |  |
| `int getNano()` |  |
| `java.time.ZoneOffset getOffset()` |  |
| `int getSecond()` |  |
| `int getYear()` |  |
| `boolean isAfter(java.time.OffsetDateTime)` |  |
| `boolean isBefore(java.time.OffsetDateTime)` |  |
| `boolean isEqual(java.time.OffsetDateTime)` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `boolean isSupported(java.time.temporal.TemporalUnit)` |  |
| `java.time.OffsetDateTime minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.OffsetDateTime minus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.OffsetDateTime minusDays(long)` |  |
| `java.time.OffsetDateTime minusHours(long)` |  |
| `java.time.OffsetDateTime minusMinutes(long)` |  |
| `java.time.OffsetDateTime minusMonths(long)` |  |
| `java.time.OffsetDateTime minusNanos(long)` |  |
| `java.time.OffsetDateTime minusSeconds(long)` |  |
| `java.time.OffsetDateTime minusWeeks(long)` |  |
| `java.time.OffsetDateTime minusYears(long)` |  |
| `static java.time.OffsetDateTime now()` |  |
| `static java.time.OffsetDateTime now(java.time.ZoneId)` |  |
| `static java.time.OffsetDateTime now(java.time.Clock)` |  |
| `static java.time.OffsetDateTime of(java.time.LocalDate, java.time.LocalTime, java.time.ZoneOffset)` |  |
| `static java.time.OffsetDateTime of(java.time.LocalDateTime, java.time.ZoneOffset)` |  |
| `static java.time.OffsetDateTime of(int, int, int, int, int, int, int, java.time.ZoneOffset)` |  |
| `static java.time.OffsetDateTime ofInstant(java.time.Instant, java.time.ZoneId)` |  |
| `static java.time.OffsetDateTime parse(CharSequence)` |  |
| `static java.time.OffsetDateTime parse(CharSequence, java.time.format.DateTimeFormatter)` |  |
| `java.time.OffsetDateTime plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.OffsetDateTime plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.OffsetDateTime plusDays(long)` |  |
| `java.time.OffsetDateTime plusHours(long)` |  |
| `java.time.OffsetDateTime plusMinutes(long)` |  |
| `java.time.OffsetDateTime plusMonths(long)` |  |
| `java.time.OffsetDateTime plusNanos(long)` |  |
| `java.time.OffsetDateTime plusSeconds(long)` |  |
| `java.time.OffsetDateTime plusWeeks(long)` |  |
| `java.time.OffsetDateTime plusYears(long)` |  |
| `static java.util.Comparator<java.time.OffsetDateTime> timeLineOrder()` |  |
| `long toEpochSecond()` |  |
| `java.time.Instant toInstant()` |  |
| `java.time.LocalDate toLocalDate()` |  |
| `java.time.LocalDateTime toLocalDateTime()` |  |
| `java.time.LocalTime toLocalTime()` |  |
| `java.time.OffsetTime toOffsetTime()` |  |
| `java.time.ZonedDateTime toZonedDateTime()` |  |
| `java.time.OffsetDateTime truncatedTo(java.time.temporal.TemporalUnit)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.OffsetDateTime with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.OffsetDateTime with(java.time.temporal.TemporalField, long)` |  |
| `java.time.OffsetDateTime withDayOfMonth(int)` |  |
| `java.time.OffsetDateTime withDayOfYear(int)` |  |
| `java.time.OffsetDateTime withHour(int)` |  |
| `java.time.OffsetDateTime withMinute(int)` |  |
| `java.time.OffsetDateTime withMonth(int)` |  |
| `java.time.OffsetDateTime withNano(int)` |  |
| `java.time.OffsetDateTime withOffsetSameInstant(java.time.ZoneOffset)` |  |
| `java.time.OffsetDateTime withOffsetSameLocal(java.time.ZoneOffset)` |  |
| `java.time.OffsetDateTime withSecond(int)` |  |
| `java.time.OffsetDateTime withYear(int)` |  |

---

### `class final OffsetTime`

- **Implements:** `java.lang.Comparable<java.time.OffsetTime> java.io.Serializable java.time.temporal.Temporal java.time.temporal.TemporalAdjuster`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.OffsetTime MAX` |  |
| `static final java.time.OffsetTime MIN` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `java.time.OffsetDateTime atDate(java.time.LocalDate)` |  |
| `int compareTo(java.time.OffsetTime)` |  |
| `String format(java.time.format.DateTimeFormatter)` |  |
| `static java.time.OffsetTime from(java.time.temporal.TemporalAccessor)` |  |
| `int getHour()` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `int getMinute()` |  |
| `int getNano()` |  |
| `java.time.ZoneOffset getOffset()` |  |
| `int getSecond()` |  |
| `boolean isAfter(java.time.OffsetTime)` |  |
| `boolean isBefore(java.time.OffsetTime)` |  |
| `boolean isEqual(java.time.OffsetTime)` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `boolean isSupported(java.time.temporal.TemporalUnit)` |  |
| `java.time.OffsetTime minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.OffsetTime minus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.OffsetTime minusHours(long)` |  |
| `java.time.OffsetTime minusMinutes(long)` |  |
| `java.time.OffsetTime minusNanos(long)` |  |
| `java.time.OffsetTime minusSeconds(long)` |  |
| `static java.time.OffsetTime now()` |  |
| `static java.time.OffsetTime now(java.time.ZoneId)` |  |
| `static java.time.OffsetTime now(java.time.Clock)` |  |
| `static java.time.OffsetTime of(java.time.LocalTime, java.time.ZoneOffset)` |  |
| `static java.time.OffsetTime of(int, int, int, int, java.time.ZoneOffset)` |  |
| `static java.time.OffsetTime ofInstant(java.time.Instant, java.time.ZoneId)` |  |
| `static java.time.OffsetTime parse(CharSequence)` |  |
| `static java.time.OffsetTime parse(CharSequence, java.time.format.DateTimeFormatter)` |  |
| `java.time.OffsetTime plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.OffsetTime plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.OffsetTime plusHours(long)` |  |
| `java.time.OffsetTime plusMinutes(long)` |  |
| `java.time.OffsetTime plusNanos(long)` |  |
| `java.time.OffsetTime plusSeconds(long)` |  |
| `java.time.LocalTime toLocalTime()` |  |
| `java.time.OffsetTime truncatedTo(java.time.temporal.TemporalUnit)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.OffsetTime with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.OffsetTime with(java.time.temporal.TemporalField, long)` |  |
| `java.time.OffsetTime withHour(int)` |  |
| `java.time.OffsetTime withMinute(int)` |  |
| `java.time.OffsetTime withNano(int)` |  |
| `java.time.OffsetTime withOffsetSameInstant(java.time.ZoneOffset)` |  |
| `java.time.OffsetTime withOffsetSameLocal(java.time.ZoneOffset)` |  |
| `java.time.OffsetTime withSecond(int)` |  |

---

### `class final Period`

- **Implements:** `java.time.chrono.ChronoPeriod java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.Period ZERO` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal addTo(java.time.temporal.Temporal)` |  |
| `static java.time.Period between(java.time.LocalDate, java.time.LocalDate)` |  |
| `static java.time.Period from(java.time.temporal.TemporalAmount)` |  |
| `long get(java.time.temporal.TemporalUnit)` |  |
| `java.time.chrono.IsoChronology getChronology()` |  |
| `int getDays()` |  |
| `int getMonths()` |  |
| `java.util.List<java.time.temporal.TemporalUnit> getUnits()` |  |
| `int getYears()` |  |
| `java.time.Period minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.Period minusDays(long)` |  |
| `java.time.Period minusMonths(long)` |  |
| `java.time.Period minusYears(long)` |  |
| `java.time.Period multipliedBy(int)` |  |
| `java.time.Period negated()` |  |
| `java.time.Period normalized()` |  |
| `static java.time.Period of(int, int, int)` |  |
| `static java.time.Period ofDays(int)` |  |
| `static java.time.Period ofMonths(int)` |  |
| `static java.time.Period ofWeeks(int)` |  |
| `static java.time.Period ofYears(int)` |  |
| `static java.time.Period parse(CharSequence)` |  |
| `java.time.Period plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.Period plusDays(long)` |  |
| `java.time.Period plusMonths(long)` |  |
| `java.time.Period plusYears(long)` |  |
| `java.time.temporal.Temporal subtractFrom(java.time.temporal.Temporal)` |  |
| `long toTotalMonths()` |  |
| `java.time.Period withDays(int)` |  |
| `java.time.Period withMonths(int)` |  |
| `java.time.Period withYears(int)` |  |

---

### `class final Year`

- **Implements:** `java.lang.Comparable<java.time.Year> java.io.Serializable java.time.temporal.Temporal java.time.temporal.TemporalAdjuster`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MAX_VALUE = 999999999` |  |
| `static final int MIN_VALUE = -999999999` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `java.time.LocalDate atDay(int)` |  |
| `java.time.YearMonth atMonth(java.time.Month)` |  |
| `java.time.YearMonth atMonth(int)` |  |
| `java.time.LocalDate atMonthDay(java.time.MonthDay)` |  |
| `int compareTo(java.time.Year)` |  |
| `String format(java.time.format.DateTimeFormatter)` |  |
| `static java.time.Year from(java.time.temporal.TemporalAccessor)` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `int getValue()` |  |
| `boolean isAfter(java.time.Year)` |  |
| `boolean isBefore(java.time.Year)` |  |
| `static boolean isLeap(long)` |  |
| `boolean isLeap()` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `boolean isSupported(java.time.temporal.TemporalUnit)` |  |
| `boolean isValidMonthDay(java.time.MonthDay)` |  |
| `int length()` |  |
| `java.time.Year minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.Year minus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.Year minusYears(long)` |  |
| `static java.time.Year now()` |  |
| `static java.time.Year now(java.time.ZoneId)` |  |
| `static java.time.Year now(java.time.Clock)` |  |
| `static java.time.Year of(int)` |  |
| `static java.time.Year parse(CharSequence)` |  |
| `static java.time.Year parse(CharSequence, java.time.format.DateTimeFormatter)` |  |
| `java.time.Year plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.Year plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.Year plusYears(long)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.Year with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.Year with(java.time.temporal.TemporalField, long)` |  |

---

### `class final YearMonth`

- **Implements:** `java.lang.Comparable<java.time.YearMonth> java.io.Serializable java.time.temporal.Temporal java.time.temporal.TemporalAdjuster`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `java.time.LocalDate atDay(int)` |  |
| `java.time.LocalDate atEndOfMonth()` |  |
| `int compareTo(java.time.YearMonth)` |  |
| `String format(java.time.format.DateTimeFormatter)` |  |
| `static java.time.YearMonth from(java.time.temporal.TemporalAccessor)` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `java.time.Month getMonth()` |  |
| `int getMonthValue()` |  |
| `int getYear()` |  |
| `boolean isAfter(java.time.YearMonth)` |  |
| `boolean isBefore(java.time.YearMonth)` |  |
| `boolean isLeapYear()` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `boolean isSupported(java.time.temporal.TemporalUnit)` |  |
| `boolean isValidDay(int)` |  |
| `int lengthOfMonth()` |  |
| `int lengthOfYear()` |  |
| `java.time.YearMonth minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.YearMonth minus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.YearMonth minusMonths(long)` |  |
| `java.time.YearMonth minusYears(long)` |  |
| `static java.time.YearMonth now()` |  |
| `static java.time.YearMonth now(java.time.ZoneId)` |  |
| `static java.time.YearMonth now(java.time.Clock)` |  |
| `static java.time.YearMonth of(int, java.time.Month)` |  |
| `static java.time.YearMonth of(int, int)` |  |
| `static java.time.YearMonth parse(CharSequence)` |  |
| `static java.time.YearMonth parse(CharSequence, java.time.format.DateTimeFormatter)` |  |
| `java.time.YearMonth plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.YearMonth plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.YearMonth plusMonths(long)` |  |
| `java.time.YearMonth plusYears(long)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.YearMonth with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.YearMonth with(java.time.temporal.TemporalField, long)` |  |
| `java.time.YearMonth withMonth(int)` |  |
| `java.time.YearMonth withYear(int)` |  |

---

### `class abstract ZoneId`

- **Implements:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.util.Map<java.lang.String,java.lang.String> SHORT_IDS` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.time.ZoneId from(java.time.temporal.TemporalAccessor)` |  |
| `static java.util.Set<java.lang.String> getAvailableZoneIds()` |  |
| `String getDisplayName(java.time.format.TextStyle, java.util.Locale)` |  |
| `abstract String getId()` |  |
| `abstract java.time.zone.ZoneRules getRules()` |  |
| `java.time.ZoneId normalized()` |  |
| `static java.time.ZoneId of(String, java.util.Map<java.lang.String,java.lang.String>)` |  |
| `static java.time.ZoneId of(String)` |  |
| `static java.time.ZoneId ofOffset(String, java.time.ZoneOffset)` |  |
| `static java.time.ZoneId systemDefault()` |  |

---

### `class final ZoneOffset`

- **Extends:** `java.time.ZoneId`
- **Implements:** `java.lang.Comparable<java.time.ZoneOffset> java.io.Serializable java.time.temporal.TemporalAccessor java.time.temporal.TemporalAdjuster`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.ZoneOffset MAX` |  |
| `static final java.time.ZoneOffset MIN` |  |
| `static final java.time.ZoneOffset UTC` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `int compareTo(java.time.ZoneOffset)` |  |
| `static java.time.ZoneOffset from(java.time.temporal.TemporalAccessor)` |  |
| `String getId()` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `java.time.zone.ZoneRules getRules()` |  |
| `int getTotalSeconds()` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `static java.time.ZoneOffset of(String)` |  |
| `static java.time.ZoneOffset ofHours(int)` |  |
| `static java.time.ZoneOffset ofHoursMinutes(int, int)` |  |
| `static java.time.ZoneOffset ofHoursMinutesSeconds(int, int, int)` |  |
| `static java.time.ZoneOffset ofTotalSeconds(int)` |  |

---

### `class final ZonedDateTime`

- **Implements:** `java.time.chrono.ChronoZonedDateTime<java.time.LocalDate> java.io.Serializable java.time.temporal.Temporal`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.time.ZonedDateTime from(java.time.temporal.TemporalAccessor)` |  |
| `int getDayOfMonth()` |  |
| `java.time.DayOfWeek getDayOfWeek()` |  |
| `int getDayOfYear()` |  |
| `int getHour()` |  |
| `int getMinute()` |  |
| `java.time.Month getMonth()` |  |
| `int getMonthValue()` |  |
| `int getNano()` |  |
| `java.time.ZoneOffset getOffset()` |  |
| `int getSecond()` |  |
| `int getYear()` |  |
| `java.time.ZoneId getZone()` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `java.time.ZonedDateTime minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.ZonedDateTime minus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.ZonedDateTime minusDays(long)` |  |
| `java.time.ZonedDateTime minusHours(long)` |  |
| `java.time.ZonedDateTime minusMinutes(long)` |  |
| `java.time.ZonedDateTime minusMonths(long)` |  |
| `java.time.ZonedDateTime minusNanos(long)` |  |
| `java.time.ZonedDateTime minusSeconds(long)` |  |
| `java.time.ZonedDateTime minusWeeks(long)` |  |
| `java.time.ZonedDateTime minusYears(long)` |  |
| `static java.time.ZonedDateTime now()` |  |
| `static java.time.ZonedDateTime now(java.time.ZoneId)` |  |
| `static java.time.ZonedDateTime now(java.time.Clock)` |  |
| `static java.time.ZonedDateTime of(java.time.LocalDate, java.time.LocalTime, java.time.ZoneId)` |  |
| `static java.time.ZonedDateTime of(java.time.LocalDateTime, java.time.ZoneId)` |  |
| `static java.time.ZonedDateTime of(int, int, int, int, int, int, int, java.time.ZoneId)` |  |
| `static java.time.ZonedDateTime ofInstant(java.time.Instant, java.time.ZoneId)` |  |
| `static java.time.ZonedDateTime ofInstant(java.time.LocalDateTime, java.time.ZoneOffset, java.time.ZoneId)` |  |
| `static java.time.ZonedDateTime ofLocal(java.time.LocalDateTime, java.time.ZoneId, java.time.ZoneOffset)` |  |
| `static java.time.ZonedDateTime ofStrict(java.time.LocalDateTime, java.time.ZoneOffset, java.time.ZoneId)` |  |
| `static java.time.ZonedDateTime parse(CharSequence)` |  |
| `static java.time.ZonedDateTime parse(CharSequence, java.time.format.DateTimeFormatter)` |  |
| `java.time.ZonedDateTime plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.ZonedDateTime plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.ZonedDateTime plusDays(long)` |  |
| `java.time.ZonedDateTime plusHours(long)` |  |
| `java.time.ZonedDateTime plusMinutes(long)` |  |
| `java.time.ZonedDateTime plusMonths(long)` |  |
| `java.time.ZonedDateTime plusNanos(long)` |  |
| `java.time.ZonedDateTime plusSeconds(long)` |  |
| `java.time.ZonedDateTime plusWeeks(long)` |  |
| `java.time.ZonedDateTime plusYears(long)` |  |
| `java.time.LocalDate toLocalDate()` |  |
| `java.time.LocalDateTime toLocalDateTime()` |  |
| `java.time.OffsetDateTime toOffsetDateTime()` |  |
| `java.time.ZonedDateTime truncatedTo(java.time.temporal.TemporalUnit)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.ZonedDateTime with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.ZonedDateTime with(java.time.temporal.TemporalField, long)` |  |
| `java.time.ZonedDateTime withDayOfMonth(int)` |  |
| `java.time.ZonedDateTime withDayOfYear(int)` |  |
| `java.time.ZonedDateTime withEarlierOffsetAtOverlap()` |  |
| `java.time.ZonedDateTime withFixedOffsetZone()` |  |
| `java.time.ZonedDateTime withHour(int)` |  |
| `java.time.ZonedDateTime withLaterOffsetAtOverlap()` |  |
| `java.time.ZonedDateTime withMinute(int)` |  |
| `java.time.ZonedDateTime withMonth(int)` |  |
| `java.time.ZonedDateTime withNano(int)` |  |
| `java.time.ZonedDateTime withSecond(int)` |  |
| `java.time.ZonedDateTime withYear(int)` |  |
| `java.time.ZonedDateTime withZoneSameInstant(java.time.ZoneId)` |  |
| `java.time.ZonedDateTime withZoneSameLocal(java.time.ZoneId)` |  |

---

## Package: `java.time.chrono`

### `class abstract AbstractChronology`

- **Implements:** `java.time.chrono.Chronology`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractChronology()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compareTo(java.time.chrono.Chronology)` |  |
| `java.time.chrono.ChronoLocalDate resolveDate(java.util.Map<java.time.temporal.TemporalField,java.lang.Long>, java.time.format.ResolverStyle)` |  |

---

### `interface ChronoLocalDate`

- **Extends:** `java.time.temporal.Temporal java.lang.Comparable<java.time.chrono.ChronoLocalDate> java.time.temporal.TemporalAdjuster`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `default java.time.chrono.ChronoLocalDateTime<?> atTime(java.time.LocalTime)` |  |
| `default int compareTo(java.time.chrono.ChronoLocalDate)` |  |
| `boolean equals(Object)` |  |
| `default String format(java.time.format.DateTimeFormatter)` |  |
| `static java.time.chrono.ChronoLocalDate from(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.Chronology getChronology()` |  |
| `default java.time.chrono.Era getEra()` |  |
| `int hashCode()` |  |
| `default boolean isAfter(java.time.chrono.ChronoLocalDate)` |  |
| `default boolean isBefore(java.time.chrono.ChronoLocalDate)` |  |
| `default boolean isEqual(java.time.chrono.ChronoLocalDate)` |  |
| `default boolean isLeapYear()` |  |
| `default boolean isSupported(java.time.temporal.TemporalField)` |  |
| `default boolean isSupported(java.time.temporal.TemporalUnit)` |  |
| `int lengthOfMonth()` |  |
| `default int lengthOfYear()` |  |
| `default java.time.chrono.ChronoLocalDate minus(java.time.temporal.TemporalAmount)` |  |
| `default java.time.chrono.ChronoLocalDate minus(long, java.time.temporal.TemporalUnit)` |  |
| `default java.time.chrono.ChronoLocalDate plus(java.time.temporal.TemporalAmount)` |  |
| `default java.time.chrono.ChronoLocalDate plus(long, java.time.temporal.TemporalUnit)` |  |
| `static java.util.Comparator<java.time.chrono.ChronoLocalDate> timeLineOrder()` |  |
| `default long toEpochDay()` |  |
| `String toString()` |  |
| `java.time.chrono.ChronoPeriod until(java.time.chrono.ChronoLocalDate)` |  |
| `default java.time.chrono.ChronoLocalDate with(java.time.temporal.TemporalAdjuster)` |  |
| `default java.time.chrono.ChronoLocalDate with(java.time.temporal.TemporalField, long)` |  |

---

### `interface ChronoLocalDateTime<D`

- **Extends:** `java.time.chrono.ChronoLocalDate> extends java.time.temporal.Temporal java.lang.Comparable<java.time.chrono.ChronoLocalDateTime<?>> java.time.temporal.TemporalAdjuster`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `java.time.chrono.ChronoZonedDateTime<D> atZone(java.time.ZoneId)` |  |
| `default int compareTo(java.time.chrono.ChronoLocalDateTime<?>)` |  |
| `boolean equals(Object)` |  |
| `default String format(java.time.format.DateTimeFormatter)` |  |
| `static java.time.chrono.ChronoLocalDateTime<?> from(java.time.temporal.TemporalAccessor)` |  |
| `default java.time.chrono.Chronology getChronology()` |  |
| `int hashCode()` |  |
| `default boolean isAfter(java.time.chrono.ChronoLocalDateTime<?>)` |  |
| `default boolean isBefore(java.time.chrono.ChronoLocalDateTime<?>)` |  |
| `default boolean isEqual(java.time.chrono.ChronoLocalDateTime<?>)` |  |
| `default boolean isSupported(java.time.temporal.TemporalUnit)` |  |
| `default java.time.chrono.ChronoLocalDateTime<D> minus(java.time.temporal.TemporalAmount)` |  |
| `default java.time.chrono.ChronoLocalDateTime<D> minus(long, java.time.temporal.TemporalUnit)` |  |
| `default java.time.chrono.ChronoLocalDateTime<D> plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.chrono.ChronoLocalDateTime<D> plus(long, java.time.temporal.TemporalUnit)` |  |
| `static java.util.Comparator<java.time.chrono.ChronoLocalDateTime<?>> timeLineOrder()` |  |
| `default long toEpochSecond(java.time.ZoneOffset)` |  |
| `default java.time.Instant toInstant(java.time.ZoneOffset)` |  |
| `D toLocalDate()` |  |
| `java.time.LocalTime toLocalTime()` |  |
| `String toString()` |  |
| `default java.time.chrono.ChronoLocalDateTime<D> with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.chrono.ChronoLocalDateTime<D> with(java.time.temporal.TemporalField, long)` |  |

---

### `interface ChronoPeriod`

- **Extends:** `java.time.temporal.TemporalAmount`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.time.chrono.ChronoPeriod between(java.time.chrono.ChronoLocalDate, java.time.chrono.ChronoLocalDate)` |  |
| `boolean equals(Object)` |  |
| `java.time.chrono.Chronology getChronology()` |  |
| `int hashCode()` |  |
| `default boolean isNegative()` |  |
| `default boolean isZero()` |  |
| `java.time.chrono.ChronoPeriod minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.chrono.ChronoPeriod multipliedBy(int)` |  |
| `default java.time.chrono.ChronoPeriod negated()` |  |
| `java.time.chrono.ChronoPeriod normalized()` |  |
| `java.time.chrono.ChronoPeriod plus(java.time.temporal.TemporalAmount)` |  |
| `String toString()` |  |

---

### `interface ChronoZonedDateTime<D`

- **Extends:** `java.time.chrono.ChronoLocalDate> extends java.time.temporal.Temporal java.lang.Comparable<java.time.chrono.ChronoZonedDateTime<?>>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default int compareTo(java.time.chrono.ChronoZonedDateTime<?>)` |  |
| `boolean equals(Object)` |  |
| `default String format(java.time.format.DateTimeFormatter)` |  |
| `static java.time.chrono.ChronoZonedDateTime<?> from(java.time.temporal.TemporalAccessor)` |  |
| `default java.time.chrono.Chronology getChronology()` |  |
| `default long getLong(java.time.temporal.TemporalField)` |  |
| `java.time.ZoneOffset getOffset()` |  |
| `java.time.ZoneId getZone()` |  |
| `int hashCode()` |  |
| `default boolean isAfter(java.time.chrono.ChronoZonedDateTime<?>)` |  |
| `default boolean isBefore(java.time.chrono.ChronoZonedDateTime<?>)` |  |
| `default boolean isEqual(java.time.chrono.ChronoZonedDateTime<?>)` |  |
| `default boolean isSupported(java.time.temporal.TemporalUnit)` |  |
| `default java.time.chrono.ChronoZonedDateTime<D> minus(java.time.temporal.TemporalAmount)` |  |
| `default java.time.chrono.ChronoZonedDateTime<D> minus(long, java.time.temporal.TemporalUnit)` |  |
| `default java.time.chrono.ChronoZonedDateTime<D> plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.chrono.ChronoZonedDateTime<D> plus(long, java.time.temporal.TemporalUnit)` |  |
| `static java.util.Comparator<java.time.chrono.ChronoZonedDateTime<?>> timeLineOrder()` |  |
| `default long toEpochSecond()` |  |
| `default java.time.Instant toInstant()` |  |
| `default D toLocalDate()` |  |
| `java.time.chrono.ChronoLocalDateTime<D> toLocalDateTime()` |  |
| `default java.time.LocalTime toLocalTime()` |  |
| `String toString()` |  |
| `default java.time.chrono.ChronoZonedDateTime<D> with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.chrono.ChronoZonedDateTime<D> with(java.time.temporal.TemporalField, long)` |  |
| `java.time.chrono.ChronoZonedDateTime<D> withEarlierOffsetAtOverlap()` |  |
| `java.time.chrono.ChronoZonedDateTime<D> withLaterOffsetAtOverlap()` |  |
| `java.time.chrono.ChronoZonedDateTime<D> withZoneSameInstant(java.time.ZoneId)` |  |
| `java.time.chrono.ChronoZonedDateTime<D> withZoneSameLocal(java.time.ZoneId)` |  |

---

### `interface Chronology`

- **Extends:** `java.lang.Comparable<java.time.chrono.Chronology>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compareTo(java.time.chrono.Chronology)` |  |
| `default java.time.chrono.ChronoLocalDate date(java.time.chrono.Era, int, int, int)` |  |
| `java.time.chrono.ChronoLocalDate date(int, int, int)` |  |
| `java.time.chrono.ChronoLocalDate date(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.ChronoLocalDate dateEpochDay(long)` |  |
| `default java.time.chrono.ChronoLocalDate dateNow()` |  |
| `default java.time.chrono.ChronoLocalDate dateNow(java.time.ZoneId)` |  |
| `default java.time.chrono.ChronoLocalDate dateNow(java.time.Clock)` |  |
| `default java.time.chrono.ChronoLocalDate dateYearDay(java.time.chrono.Era, int, int)` |  |
| `java.time.chrono.ChronoLocalDate dateYearDay(int, int)` |  |
| `boolean equals(Object)` |  |
| `java.time.chrono.Era eraOf(int)` |  |
| `java.util.List<java.time.chrono.Era> eras()` |  |
| `static java.time.chrono.Chronology from(java.time.temporal.TemporalAccessor)` |  |
| `static java.util.Set<java.time.chrono.Chronology> getAvailableChronologies()` |  |
| `String getCalendarType()` |  |
| `default String getDisplayName(java.time.format.TextStyle, java.util.Locale)` |  |
| `String getId()` |  |
| `int hashCode()` |  |
| `boolean isLeapYear(long)` |  |
| `default java.time.chrono.ChronoLocalDateTime<? extends java.time.chrono.ChronoLocalDate> localDateTime(java.time.temporal.TemporalAccessor)` |  |
| `static java.time.chrono.Chronology of(String)` |  |
| `static java.time.chrono.Chronology ofLocale(java.util.Locale)` |  |
| `default java.time.chrono.ChronoPeriod period(int, int, int)` |  |
| `int prolepticYear(java.time.chrono.Era, int)` |  |
| `java.time.temporal.ValueRange range(java.time.temporal.ChronoField)` |  |
| `java.time.chrono.ChronoLocalDate resolveDate(java.util.Map<java.time.temporal.TemporalField,java.lang.Long>, java.time.format.ResolverStyle)` |  |
| `String toString()` |  |
| `default java.time.chrono.ChronoZonedDateTime<? extends java.time.chrono.ChronoLocalDate> zonedDateTime(java.time.temporal.TemporalAccessor)` |  |
| `default java.time.chrono.ChronoZonedDateTime<? extends java.time.chrono.ChronoLocalDate> zonedDateTime(java.time.Instant, java.time.ZoneId)` |  |

---

### `interface Era`

- **Extends:** `java.time.temporal.TemporalAccessor java.time.temporal.TemporalAdjuster`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |
| `default String getDisplayName(java.time.format.TextStyle, java.util.Locale)` |  |
| `default long getLong(java.time.temporal.TemporalField)` |  |
| `int getValue()` |  |
| `default boolean isSupported(java.time.temporal.TemporalField)` |  |

---

### `class final HijrahChronology`

- **Extends:** `java.time.chrono.AbstractChronology`
- **Implements:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.chrono.HijrahChronology INSTANCE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.chrono.HijrahDate date(java.time.chrono.Era, int, int, int)` |  |
| `java.time.chrono.HijrahDate date(int, int, int)` |  |
| `java.time.chrono.HijrahDate date(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.HijrahDate dateEpochDay(long)` |  |
| `java.time.chrono.HijrahDate dateNow()` |  |
| `java.time.chrono.HijrahDate dateNow(java.time.ZoneId)` |  |
| `java.time.chrono.HijrahDate dateNow(java.time.Clock)` |  |
| `java.time.chrono.HijrahDate dateYearDay(java.time.chrono.Era, int, int)` |  |
| `java.time.chrono.HijrahDate dateYearDay(int, int)` |  |
| `java.time.chrono.HijrahEra eraOf(int)` |  |
| `java.util.List<java.time.chrono.Era> eras()` |  |
| `String getCalendarType()` |  |
| `String getId()` |  |
| `boolean isLeapYear(long)` |  |
| `java.time.chrono.ChronoLocalDateTime<java.time.chrono.HijrahDate> localDateTime(java.time.temporal.TemporalAccessor)` |  |
| `int prolepticYear(java.time.chrono.Era, int)` |  |
| `java.time.temporal.ValueRange range(java.time.temporal.ChronoField)` |  |
| `java.time.chrono.HijrahDate resolveDate(java.util.Map<java.time.temporal.TemporalField,java.lang.Long>, java.time.format.ResolverStyle)` |  |
| `java.time.chrono.ChronoZonedDateTime<java.time.chrono.HijrahDate> zonedDateTime(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.ChronoZonedDateTime<java.time.chrono.HijrahDate> zonedDateTime(java.time.Instant, java.time.ZoneId)` |  |

---

### `class final HijrahDate`

- **Implements:** `java.time.chrono.ChronoLocalDate java.io.Serializable java.time.temporal.Temporal java.time.temporal.TemporalAdjuster`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.chrono.ChronoLocalDateTime<java.time.chrono.HijrahDate> atTime(java.time.LocalTime)` |  |
| `static java.time.chrono.HijrahDate from(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.HijrahChronology getChronology()` |  |
| `java.time.chrono.HijrahEra getEra()` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `int lengthOfMonth()` |  |
| `java.time.chrono.HijrahDate minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.chrono.HijrahDate minus(long, java.time.temporal.TemporalUnit)` |  |
| `static java.time.chrono.HijrahDate now()` |  |
| `static java.time.chrono.HijrahDate now(java.time.ZoneId)` |  |
| `static java.time.chrono.HijrahDate now(java.time.Clock)` |  |
| `static java.time.chrono.HijrahDate of(int, int, int)` |  |
| `java.time.chrono.HijrahDate plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.chrono.HijrahDate plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.chrono.ChronoPeriod until(java.time.chrono.ChronoLocalDate)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.chrono.HijrahDate with(java.time.temporal.TemporalField, long)` |  |
| `java.time.chrono.HijrahDate with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.chrono.HijrahDate withVariant(java.time.chrono.HijrahChronology)` |  |

---

### `enum HijrahEra`

- **Implements:** `java.time.chrono.Era`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.chrono.HijrahEra AH` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getValue()` |  |
| `static java.time.chrono.HijrahEra of(int)` |  |

---

### `class final IsoChronology`

- **Extends:** `java.time.chrono.AbstractChronology`
- **Implements:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.chrono.IsoChronology INSTANCE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.LocalDate date(java.time.chrono.Era, int, int, int)` |  |
| `java.time.LocalDate date(int, int, int)` |  |
| `java.time.LocalDate date(java.time.temporal.TemporalAccessor)` |  |
| `java.time.LocalDate dateEpochDay(long)` |  |
| `java.time.LocalDate dateNow()` |  |
| `java.time.LocalDate dateNow(java.time.ZoneId)` |  |
| `java.time.LocalDate dateNow(java.time.Clock)` |  |
| `java.time.LocalDate dateYearDay(java.time.chrono.Era, int, int)` |  |
| `java.time.LocalDate dateYearDay(int, int)` |  |
| `java.time.chrono.IsoEra eraOf(int)` |  |
| `java.util.List<java.time.chrono.Era> eras()` |  |
| `String getCalendarType()` |  |
| `String getId()` |  |
| `boolean isLeapYear(long)` |  |
| `java.time.LocalDateTime localDateTime(java.time.temporal.TemporalAccessor)` |  |
| `java.time.Period period(int, int, int)` |  |
| `int prolepticYear(java.time.chrono.Era, int)` |  |
| `java.time.temporal.ValueRange range(java.time.temporal.ChronoField)` |  |
| `java.time.LocalDate resolveDate(java.util.Map<java.time.temporal.TemporalField,java.lang.Long>, java.time.format.ResolverStyle)` |  |
| `java.time.ZonedDateTime zonedDateTime(java.time.temporal.TemporalAccessor)` |  |
| `java.time.ZonedDateTime zonedDateTime(java.time.Instant, java.time.ZoneId)` |  |

---

### `enum IsoEra`

- **Implements:** `java.time.chrono.Era`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.chrono.IsoEra BCE` |  |
| `static final java.time.chrono.IsoEra CE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getValue()` |  |
| `static java.time.chrono.IsoEra of(int)` |  |

---

### `class final JapaneseChronology`

- **Extends:** `java.time.chrono.AbstractChronology`
- **Implements:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.chrono.JapaneseChronology INSTANCE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.chrono.JapaneseDate date(java.time.chrono.Era, int, int, int)` |  |
| `java.time.chrono.JapaneseDate date(int, int, int)` |  |
| `java.time.chrono.JapaneseDate date(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.JapaneseDate dateEpochDay(long)` |  |
| `java.time.chrono.JapaneseDate dateNow()` |  |
| `java.time.chrono.JapaneseDate dateNow(java.time.ZoneId)` |  |
| `java.time.chrono.JapaneseDate dateNow(java.time.Clock)` |  |
| `java.time.chrono.JapaneseDate dateYearDay(java.time.chrono.Era, int, int)` |  |
| `java.time.chrono.JapaneseDate dateYearDay(int, int)` |  |
| `java.time.chrono.JapaneseEra eraOf(int)` |  |
| `java.util.List<java.time.chrono.Era> eras()` |  |
| `String getCalendarType()` |  |
| `String getId()` |  |
| `boolean isLeapYear(long)` |  |
| `java.time.chrono.ChronoLocalDateTime<java.time.chrono.JapaneseDate> localDateTime(java.time.temporal.TemporalAccessor)` |  |
| `int prolepticYear(java.time.chrono.Era, int)` |  |
| `java.time.temporal.ValueRange range(java.time.temporal.ChronoField)` |  |
| `java.time.chrono.JapaneseDate resolveDate(java.util.Map<java.time.temporal.TemporalField,java.lang.Long>, java.time.format.ResolverStyle)` |  |
| `java.time.chrono.ChronoZonedDateTime<java.time.chrono.JapaneseDate> zonedDateTime(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.ChronoZonedDateTime<java.time.chrono.JapaneseDate> zonedDateTime(java.time.Instant, java.time.ZoneId)` |  |

---

### `class final JapaneseDate`

- **Implements:** `java.time.chrono.ChronoLocalDate java.io.Serializable java.time.temporal.Temporal java.time.temporal.TemporalAdjuster`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.chrono.ChronoLocalDateTime<java.time.chrono.JapaneseDate> atTime(java.time.LocalTime)` |  |
| `static java.time.chrono.JapaneseDate from(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.JapaneseChronology getChronology()` |  |
| `java.time.chrono.JapaneseEra getEra()` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `int lengthOfMonth()` |  |
| `java.time.chrono.JapaneseDate minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.chrono.JapaneseDate minus(long, java.time.temporal.TemporalUnit)` |  |
| `static java.time.chrono.JapaneseDate now()` |  |
| `static java.time.chrono.JapaneseDate now(java.time.ZoneId)` |  |
| `static java.time.chrono.JapaneseDate now(java.time.Clock)` |  |
| `static java.time.chrono.JapaneseDate of(java.time.chrono.JapaneseEra, int, int, int)` |  |
| `static java.time.chrono.JapaneseDate of(int, int, int)` |  |
| `java.time.chrono.JapaneseDate plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.chrono.JapaneseDate plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.chrono.ChronoPeriod until(java.time.chrono.ChronoLocalDate)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.chrono.JapaneseDate with(java.time.temporal.TemporalField, long)` |  |
| `java.time.chrono.JapaneseDate with(java.time.temporal.TemporalAdjuster)` |  |

---

### `class final JapaneseEra`

- **Implements:** `java.time.chrono.Era java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.chrono.JapaneseEra HEISEI` |  |
| `static final java.time.chrono.JapaneseEra MEIJI` |  |
| `static final java.time.chrono.JapaneseEra REIWA` |  |
| `static final java.time.chrono.JapaneseEra SHOWA` |  |
| `static final java.time.chrono.JapaneseEra TAISHO` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getValue()` |  |
| `static java.time.chrono.JapaneseEra of(int)` |  |
| `static java.time.chrono.JapaneseEra valueOf(String)` |  |
| `static java.time.chrono.JapaneseEra[] values()` |  |

---

### `class final MinguoChronology`

- **Extends:** `java.time.chrono.AbstractChronology`
- **Implements:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.chrono.MinguoChronology INSTANCE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.chrono.MinguoDate date(java.time.chrono.Era, int, int, int)` |  |
| `java.time.chrono.MinguoDate date(int, int, int)` |  |
| `java.time.chrono.MinguoDate date(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.MinguoDate dateEpochDay(long)` |  |
| `java.time.chrono.MinguoDate dateNow()` |  |
| `java.time.chrono.MinguoDate dateNow(java.time.ZoneId)` |  |
| `java.time.chrono.MinguoDate dateNow(java.time.Clock)` |  |
| `java.time.chrono.MinguoDate dateYearDay(java.time.chrono.Era, int, int)` |  |
| `java.time.chrono.MinguoDate dateYearDay(int, int)` |  |
| `java.time.chrono.MinguoEra eraOf(int)` |  |
| `java.util.List<java.time.chrono.Era> eras()` |  |
| `String getCalendarType()` |  |
| `String getId()` |  |
| `boolean isLeapYear(long)` |  |
| `java.time.chrono.ChronoLocalDateTime<java.time.chrono.MinguoDate> localDateTime(java.time.temporal.TemporalAccessor)` |  |
| `int prolepticYear(java.time.chrono.Era, int)` |  |
| `java.time.temporal.ValueRange range(java.time.temporal.ChronoField)` |  |
| `java.time.chrono.MinguoDate resolveDate(java.util.Map<java.time.temporal.TemporalField,java.lang.Long>, java.time.format.ResolverStyle)` |  |
| `java.time.chrono.ChronoZonedDateTime<java.time.chrono.MinguoDate> zonedDateTime(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.ChronoZonedDateTime<java.time.chrono.MinguoDate> zonedDateTime(java.time.Instant, java.time.ZoneId)` |  |

---

### `class final MinguoDate`

- **Implements:** `java.time.chrono.ChronoLocalDate java.io.Serializable java.time.temporal.Temporal java.time.temporal.TemporalAdjuster`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.chrono.ChronoLocalDateTime<java.time.chrono.MinguoDate> atTime(java.time.LocalTime)` |  |
| `static java.time.chrono.MinguoDate from(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.MinguoChronology getChronology()` |  |
| `java.time.chrono.MinguoEra getEra()` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `int lengthOfMonth()` |  |
| `java.time.chrono.MinguoDate minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.chrono.MinguoDate minus(long, java.time.temporal.TemporalUnit)` |  |
| `static java.time.chrono.MinguoDate now()` |  |
| `static java.time.chrono.MinguoDate now(java.time.ZoneId)` |  |
| `static java.time.chrono.MinguoDate now(java.time.Clock)` |  |
| `static java.time.chrono.MinguoDate of(int, int, int)` |  |
| `java.time.chrono.MinguoDate plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.chrono.MinguoDate plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.chrono.ChronoPeriod until(java.time.chrono.ChronoLocalDate)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.chrono.MinguoDate with(java.time.temporal.TemporalField, long)` |  |
| `java.time.chrono.MinguoDate with(java.time.temporal.TemporalAdjuster)` |  |

---

### `enum MinguoEra`

- **Implements:** `java.time.chrono.Era`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.chrono.MinguoEra BEFORE_ROC` |  |
| `static final java.time.chrono.MinguoEra ROC` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getValue()` |  |
| `static java.time.chrono.MinguoEra of(int)` |  |

---

### `class final ThaiBuddhistChronology`

- **Extends:** `java.time.chrono.AbstractChronology`
- **Implements:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.chrono.ThaiBuddhistChronology INSTANCE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.chrono.ThaiBuddhistDate date(java.time.chrono.Era, int, int, int)` |  |
| `java.time.chrono.ThaiBuddhistDate date(int, int, int)` |  |
| `java.time.chrono.ThaiBuddhistDate date(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.ThaiBuddhistDate dateEpochDay(long)` |  |
| `java.time.chrono.ThaiBuddhistDate dateNow()` |  |
| `java.time.chrono.ThaiBuddhistDate dateNow(java.time.ZoneId)` |  |
| `java.time.chrono.ThaiBuddhistDate dateNow(java.time.Clock)` |  |
| `java.time.chrono.ThaiBuddhistDate dateYearDay(java.time.chrono.Era, int, int)` |  |
| `java.time.chrono.ThaiBuddhistDate dateYearDay(int, int)` |  |
| `java.time.chrono.ThaiBuddhistEra eraOf(int)` |  |
| `java.util.List<java.time.chrono.Era> eras()` |  |
| `String getCalendarType()` |  |
| `String getId()` |  |
| `boolean isLeapYear(long)` |  |
| `java.time.chrono.ChronoLocalDateTime<java.time.chrono.ThaiBuddhistDate> localDateTime(java.time.temporal.TemporalAccessor)` |  |
| `int prolepticYear(java.time.chrono.Era, int)` |  |
| `java.time.temporal.ValueRange range(java.time.temporal.ChronoField)` |  |
| `java.time.chrono.ThaiBuddhistDate resolveDate(java.util.Map<java.time.temporal.TemporalField,java.lang.Long>, java.time.format.ResolverStyle)` |  |
| `java.time.chrono.ChronoZonedDateTime<java.time.chrono.ThaiBuddhistDate> zonedDateTime(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.ChronoZonedDateTime<java.time.chrono.ThaiBuddhistDate> zonedDateTime(java.time.Instant, java.time.ZoneId)` |  |

---

### `class final ThaiBuddhistDate`

- **Implements:** `java.time.chrono.ChronoLocalDate java.io.Serializable java.time.temporal.Temporal java.time.temporal.TemporalAdjuster`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.chrono.ChronoLocalDateTime<java.time.chrono.ThaiBuddhistDate> atTime(java.time.LocalTime)` |  |
| `static java.time.chrono.ThaiBuddhistDate from(java.time.temporal.TemporalAccessor)` |  |
| `java.time.chrono.ThaiBuddhistChronology getChronology()` |  |
| `java.time.chrono.ThaiBuddhistEra getEra()` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `int lengthOfMonth()` |  |
| `java.time.chrono.ThaiBuddhistDate minus(java.time.temporal.TemporalAmount)` |  |
| `java.time.chrono.ThaiBuddhistDate minus(long, java.time.temporal.TemporalUnit)` |  |
| `static java.time.chrono.ThaiBuddhistDate now()` |  |
| `static java.time.chrono.ThaiBuddhistDate now(java.time.ZoneId)` |  |
| `static java.time.chrono.ThaiBuddhistDate now(java.time.Clock)` |  |
| `static java.time.chrono.ThaiBuddhistDate of(int, int, int)` |  |
| `java.time.chrono.ThaiBuddhistDate plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.chrono.ThaiBuddhistDate plus(long, java.time.temporal.TemporalUnit)` |  |
| `java.time.chrono.ChronoPeriod until(java.time.chrono.ChronoLocalDate)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `java.time.chrono.ThaiBuddhistDate with(java.time.temporal.TemporalField, long)` |  |
| `java.time.chrono.ThaiBuddhistDate with(java.time.temporal.TemporalAdjuster)` |  |

---

### `enum ThaiBuddhistEra`

- **Implements:** `java.time.chrono.Era`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.chrono.ThaiBuddhistEra BE` |  |
| `static final java.time.chrono.ThaiBuddhistEra BEFORE_BE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getValue()` |  |
| `static java.time.chrono.ThaiBuddhistEra of(int)` |  |

---

## Package: `java.time.format`

### `class final DateTimeFormatter`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.format.DateTimeFormatter BASIC_ISO_DATE` |  |
| `static final java.time.format.DateTimeFormatter ISO_DATE` |  |
| `static final java.time.format.DateTimeFormatter ISO_DATE_TIME` |  |
| `static final java.time.format.DateTimeFormatter ISO_INSTANT` |  |
| `static final java.time.format.DateTimeFormatter ISO_LOCAL_DATE` |  |
| `static final java.time.format.DateTimeFormatter ISO_LOCAL_DATE_TIME` |  |
| `static final java.time.format.DateTimeFormatter ISO_LOCAL_TIME` |  |
| `static final java.time.format.DateTimeFormatter ISO_OFFSET_DATE` |  |
| `static final java.time.format.DateTimeFormatter ISO_OFFSET_DATE_TIME` |  |
| `static final java.time.format.DateTimeFormatter ISO_OFFSET_TIME` |  |
| `static final java.time.format.DateTimeFormatter ISO_ORDINAL_DATE` |  |
| `static final java.time.format.DateTimeFormatter ISO_TIME` |  |
| `static final java.time.format.DateTimeFormatter ISO_WEEK_DATE` |  |
| `static final java.time.format.DateTimeFormatter ISO_ZONED_DATE_TIME` |  |
| `static final java.time.format.DateTimeFormatter RFC_1123_DATE_TIME` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String format(java.time.temporal.TemporalAccessor)` |  |
| `void formatTo(java.time.temporal.TemporalAccessor, Appendable)` |  |
| `java.time.chrono.Chronology getChronology()` |  |
| `java.time.format.DecimalStyle getDecimalStyle()` |  |
| `java.util.Locale getLocale()` |  |
| `java.util.Set<java.time.temporal.TemporalField> getResolverFields()` |  |
| `java.time.format.ResolverStyle getResolverStyle()` |  |
| `java.time.ZoneId getZone()` |  |
| `static java.time.format.DateTimeFormatter ofLocalizedDate(java.time.format.FormatStyle)` |  |
| `static java.time.format.DateTimeFormatter ofLocalizedDateTime(java.time.format.FormatStyle)` |  |
| `static java.time.format.DateTimeFormatter ofLocalizedDateTime(java.time.format.FormatStyle, java.time.format.FormatStyle)` |  |
| `static java.time.format.DateTimeFormatter ofLocalizedTime(java.time.format.FormatStyle)` |  |
| `static java.time.format.DateTimeFormatter ofPattern(String)` |  |
| `static java.time.format.DateTimeFormatter ofPattern(String, java.util.Locale)` |  |
| `java.time.temporal.TemporalAccessor parse(CharSequence)` |  |
| `java.time.temporal.TemporalAccessor parse(CharSequence, java.text.ParsePosition)` |  |
| `<T> T parse(CharSequence, java.time.temporal.TemporalQuery<T>)` |  |
| `java.time.temporal.TemporalAccessor parseBest(CharSequence, java.time.temporal.TemporalQuery<?>...)` |  |
| `java.time.temporal.TemporalAccessor parseUnresolved(CharSequence, java.text.ParsePosition)` |  |
| `static java.time.temporal.TemporalQuery<java.time.Period> parsedExcessDays()` |  |
| `static java.time.temporal.TemporalQuery<java.lang.Boolean> parsedLeapSecond()` |  |
| `java.text.Format toFormat()` |  |
| `java.text.Format toFormat(java.time.temporal.TemporalQuery<?>)` |  |
| `java.time.format.DateTimeFormatter withChronology(java.time.chrono.Chronology)` |  |
| `java.time.format.DateTimeFormatter withDecimalStyle(java.time.format.DecimalStyle)` |  |
| `java.time.format.DateTimeFormatter withLocale(java.util.Locale)` |  |
| `java.time.format.DateTimeFormatter withResolverFields(java.time.temporal.TemporalField...)` |  |
| `java.time.format.DateTimeFormatter withResolverFields(java.util.Set<java.time.temporal.TemporalField>)` |  |
| `java.time.format.DateTimeFormatter withResolverStyle(java.time.format.ResolverStyle)` |  |
| `java.time.format.DateTimeFormatter withZone(java.time.ZoneId)` |  |

---

### `class final DateTimeFormatterBuilder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateTimeFormatterBuilder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.format.DateTimeFormatterBuilder append(java.time.format.DateTimeFormatter)` |  |
| `java.time.format.DateTimeFormatterBuilder appendChronologyId()` |  |
| `java.time.format.DateTimeFormatterBuilder appendChronologyText(java.time.format.TextStyle)` |  |
| `java.time.format.DateTimeFormatterBuilder appendFraction(java.time.temporal.TemporalField, int, int, boolean)` |  |
| `java.time.format.DateTimeFormatterBuilder appendInstant()` |  |
| `java.time.format.DateTimeFormatterBuilder appendInstant(int)` |  |
| `java.time.format.DateTimeFormatterBuilder appendLiteral(char)` |  |
| `java.time.format.DateTimeFormatterBuilder appendLiteral(String)` |  |
| `java.time.format.DateTimeFormatterBuilder appendLocalized(java.time.format.FormatStyle, java.time.format.FormatStyle)` |  |
| `java.time.format.DateTimeFormatterBuilder appendLocalizedOffset(java.time.format.TextStyle)` |  |
| `java.time.format.DateTimeFormatterBuilder appendOffset(String, String)` |  |
| `java.time.format.DateTimeFormatterBuilder appendOffsetId()` |  |
| `java.time.format.DateTimeFormatterBuilder appendOptional(java.time.format.DateTimeFormatter)` |  |
| `java.time.format.DateTimeFormatterBuilder appendPattern(String)` |  |
| `java.time.format.DateTimeFormatterBuilder appendText(java.time.temporal.TemporalField)` |  |
| `java.time.format.DateTimeFormatterBuilder appendText(java.time.temporal.TemporalField, java.time.format.TextStyle)` |  |
| `java.time.format.DateTimeFormatterBuilder appendText(java.time.temporal.TemporalField, java.util.Map<java.lang.Long,java.lang.String>)` |  |
| `java.time.format.DateTimeFormatterBuilder appendValue(java.time.temporal.TemporalField)` |  |
| `java.time.format.DateTimeFormatterBuilder appendValue(java.time.temporal.TemporalField, int)` |  |
| `java.time.format.DateTimeFormatterBuilder appendValue(java.time.temporal.TemporalField, int, int, java.time.format.SignStyle)` |  |
| `java.time.format.DateTimeFormatterBuilder appendValueReduced(java.time.temporal.TemporalField, int, int, int)` |  |
| `java.time.format.DateTimeFormatterBuilder appendValueReduced(java.time.temporal.TemporalField, int, int, java.time.chrono.ChronoLocalDate)` |  |
| `java.time.format.DateTimeFormatterBuilder appendZoneId()` |  |
| `java.time.format.DateTimeFormatterBuilder appendZoneOrOffsetId()` |  |
| `java.time.format.DateTimeFormatterBuilder appendZoneRegionId()` |  |
| `java.time.format.DateTimeFormatterBuilder appendZoneText(java.time.format.TextStyle)` |  |
| `java.time.format.DateTimeFormatterBuilder appendZoneText(java.time.format.TextStyle, java.util.Set<java.time.ZoneId>)` |  |
| `static String getLocalizedDateTimePattern(java.time.format.FormatStyle, java.time.format.FormatStyle, java.time.chrono.Chronology, java.util.Locale)` |  |
| `java.time.format.DateTimeFormatterBuilder optionalEnd()` |  |
| `java.time.format.DateTimeFormatterBuilder optionalStart()` |  |
| `java.time.format.DateTimeFormatterBuilder padNext(int)` |  |
| `java.time.format.DateTimeFormatterBuilder padNext(int, char)` |  |
| `java.time.format.DateTimeFormatterBuilder parseCaseInsensitive()` |  |
| `java.time.format.DateTimeFormatterBuilder parseCaseSensitive()` |  |
| `java.time.format.DateTimeFormatterBuilder parseDefaulting(java.time.temporal.TemporalField, long)` |  |
| `java.time.format.DateTimeFormatterBuilder parseLenient()` |  |
| `java.time.format.DateTimeFormatterBuilder parseStrict()` |  |
| `java.time.format.DateTimeFormatter toFormatter()` |  |
| `java.time.format.DateTimeFormatter toFormatter(java.util.Locale)` |  |

---

### `class DateTimeParseException`

- **Extends:** `java.time.DateTimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateTimeParseException(String, CharSequence, int)` |  |
| `DateTimeParseException(String, CharSequence, int, Throwable)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getErrorIndex()` |  |
| `String getParsedString()` |  |

---

### `class final DecimalStyle`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.format.DecimalStyle STANDARD` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.Set<java.util.Locale> getAvailableLocales()` |  |
| `char getDecimalSeparator()` |  |
| `char getNegativeSign()` |  |
| `char getPositiveSign()` |  |
| `char getZeroDigit()` |  |
| `static java.time.format.DecimalStyle of(java.util.Locale)` |  |
| `static java.time.format.DecimalStyle ofDefaultLocale()` |  |
| `java.time.format.DecimalStyle withDecimalSeparator(char)` |  |
| `java.time.format.DecimalStyle withNegativeSign(char)` |  |
| `java.time.format.DecimalStyle withPositiveSign(char)` |  |
| `java.time.format.DecimalStyle withZeroDigit(char)` |  |

---

### `enum FormatStyle`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.format.FormatStyle FULL` |  |
| `static final java.time.format.FormatStyle LONG` |  |
| `static final java.time.format.FormatStyle MEDIUM` |  |
| `static final java.time.format.FormatStyle SHORT` |  |

---

### `enum ResolverStyle`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.format.ResolverStyle LENIENT` |  |
| `static final java.time.format.ResolverStyle SMART` |  |
| `static final java.time.format.ResolverStyle STRICT` |  |

---

### `enum SignStyle`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.format.SignStyle ALWAYS` |  |
| `static final java.time.format.SignStyle EXCEEDS_PAD` |  |
| `static final java.time.format.SignStyle NEVER` |  |
| `static final java.time.format.SignStyle NORMAL` |  |
| `static final java.time.format.SignStyle NOT_NEGATIVE` |  |

---

### `enum TextStyle`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.format.TextStyle FULL` |  |
| `static final java.time.format.TextStyle FULL_STANDALONE` |  |
| `static final java.time.format.TextStyle NARROW` |  |
| `static final java.time.format.TextStyle NARROW_STANDALONE` |  |
| `static final java.time.format.TextStyle SHORT` |  |
| `static final java.time.format.TextStyle SHORT_STANDALONE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.format.TextStyle asNormal()` |  |
| `java.time.format.TextStyle asStandalone()` |  |
| `boolean isStandalone()` |  |

---

## Package: `java.time.temporal`

### `enum ChronoField`

- **Implements:** `java.time.temporal.TemporalField`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.temporal.ChronoField ALIGNED_DAY_OF_WEEK_IN_MONTH` |  |
| `static final java.time.temporal.ChronoField ALIGNED_DAY_OF_WEEK_IN_YEAR` |  |
| `static final java.time.temporal.ChronoField ALIGNED_WEEK_OF_MONTH` |  |
| `static final java.time.temporal.ChronoField ALIGNED_WEEK_OF_YEAR` |  |
| `static final java.time.temporal.ChronoField AMPM_OF_DAY` |  |
| `static final java.time.temporal.ChronoField CLOCK_HOUR_OF_AMPM` |  |
| `static final java.time.temporal.ChronoField CLOCK_HOUR_OF_DAY` |  |
| `static final java.time.temporal.ChronoField DAY_OF_MONTH` |  |
| `static final java.time.temporal.ChronoField DAY_OF_WEEK` |  |
| `static final java.time.temporal.ChronoField DAY_OF_YEAR` |  |
| `static final java.time.temporal.ChronoField EPOCH_DAY` |  |
| `static final java.time.temporal.ChronoField ERA` |  |
| `static final java.time.temporal.ChronoField HOUR_OF_AMPM` |  |
| `static final java.time.temporal.ChronoField HOUR_OF_DAY` |  |
| `static final java.time.temporal.ChronoField INSTANT_SECONDS` |  |
| `static final java.time.temporal.ChronoField MICRO_OF_DAY` |  |
| `static final java.time.temporal.ChronoField MICRO_OF_SECOND` |  |
| `static final java.time.temporal.ChronoField MILLI_OF_DAY` |  |
| `static final java.time.temporal.ChronoField MILLI_OF_SECOND` |  |
| `static final java.time.temporal.ChronoField MINUTE_OF_DAY` |  |
| `static final java.time.temporal.ChronoField MINUTE_OF_HOUR` |  |
| `static final java.time.temporal.ChronoField MONTH_OF_YEAR` |  |
| `static final java.time.temporal.ChronoField NANO_OF_DAY` |  |
| `static final java.time.temporal.ChronoField NANO_OF_SECOND` |  |
| `static final java.time.temporal.ChronoField OFFSET_SECONDS` |  |
| `static final java.time.temporal.ChronoField PROLEPTIC_MONTH` |  |
| `static final java.time.temporal.ChronoField SECOND_OF_DAY` |  |
| `static final java.time.temporal.ChronoField SECOND_OF_MINUTE` |  |
| `static final java.time.temporal.ChronoField YEAR` |  |
| `static final java.time.temporal.ChronoField YEAR_OF_ERA` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `<R extends java.time.temporal.Temporal> R adjustInto(R, long)` |  |
| `int checkValidIntValue(long)` |  |
| `long checkValidValue(long)` |  |
| `java.time.temporal.TemporalUnit getBaseUnit()` |  |
| `long getFrom(java.time.temporal.TemporalAccessor)` |  |
| `java.time.temporal.TemporalUnit getRangeUnit()` |  |
| `boolean isDateBased()` |  |
| `boolean isSupportedBy(java.time.temporal.TemporalAccessor)` |  |
| `boolean isTimeBased()` |  |
| `java.time.temporal.ValueRange range()` |  |
| `java.time.temporal.ValueRange rangeRefinedBy(java.time.temporal.TemporalAccessor)` |  |

---

### `enum ChronoUnit`

- **Implements:** `java.time.temporal.TemporalUnit`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.temporal.ChronoUnit CENTURIES` |  |
| `static final java.time.temporal.ChronoUnit DAYS` |  |
| `static final java.time.temporal.ChronoUnit DECADES` |  |
| `static final java.time.temporal.ChronoUnit ERAS` |  |
| `static final java.time.temporal.ChronoUnit FOREVER` |  |
| `static final java.time.temporal.ChronoUnit HALF_DAYS` |  |
| `static final java.time.temporal.ChronoUnit HOURS` |  |
| `static final java.time.temporal.ChronoUnit MICROS` |  |
| `static final java.time.temporal.ChronoUnit MILLENNIA` |  |
| `static final java.time.temporal.ChronoUnit MILLIS` |  |
| `static final java.time.temporal.ChronoUnit MINUTES` |  |
| `static final java.time.temporal.ChronoUnit MONTHS` |  |
| `static final java.time.temporal.ChronoUnit NANOS` |  |
| `static final java.time.temporal.ChronoUnit SECONDS` |  |
| `static final java.time.temporal.ChronoUnit WEEKS` |  |
| `static final java.time.temporal.ChronoUnit YEARS` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `<R extends java.time.temporal.Temporal> R addTo(R, long)` |  |
| `long between(java.time.temporal.Temporal, java.time.temporal.Temporal)` |  |
| `java.time.Duration getDuration()` |  |
| `boolean isDateBased()` |  |
| `boolean isDurationEstimated()` |  |
| `boolean isTimeBased()` |  |

---

### `class final IsoFields`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.temporal.TemporalField DAY_OF_QUARTER` |  |
| `static final java.time.temporal.TemporalField QUARTER_OF_YEAR` |  |
| `static final java.time.temporal.TemporalUnit QUARTER_YEARS` |  |
| `static final java.time.temporal.TemporalField WEEK_BASED_YEAR` |  |
| `static final java.time.temporal.TemporalUnit WEEK_BASED_YEARS` |  |
| `static final java.time.temporal.TemporalField WEEK_OF_WEEK_BASED_YEAR` |  |

---

### `class final JulianFields`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.temporal.TemporalField JULIAN_DAY` |  |
| `static final java.time.temporal.TemporalField MODIFIED_JULIAN_DAY` |  |
| `static final java.time.temporal.TemporalField RATA_DIE` |  |

---

### `interface Temporal`

- **Extends:** `java.time.temporal.TemporalAccessor`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isSupported(java.time.temporal.TemporalUnit)` |  |
| `default java.time.temporal.Temporal minus(java.time.temporal.TemporalAmount)` |  |
| `default java.time.temporal.Temporal minus(long, java.time.temporal.TemporalUnit)` |  |
| `default java.time.temporal.Temporal plus(java.time.temporal.TemporalAmount)` |  |
| `java.time.temporal.Temporal plus(long, java.time.temporal.TemporalUnit)` |  |
| `long until(java.time.temporal.Temporal, java.time.temporal.TemporalUnit)` |  |
| `default java.time.temporal.Temporal with(java.time.temporal.TemporalAdjuster)` |  |
| `java.time.temporal.Temporal with(java.time.temporal.TemporalField, long)` |  |

---

### `interface TemporalAccessor`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default int get(java.time.temporal.TemporalField)` |  |
| `long getLong(java.time.temporal.TemporalField)` |  |
| `boolean isSupported(java.time.temporal.TemporalField)` |  |
| `default <R> R query(java.time.temporal.TemporalQuery<R>)` |  |
| `default java.time.temporal.ValueRange range(java.time.temporal.TemporalField)` |  |

---

### `interface TemporalAdjuster`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal adjustInto(java.time.temporal.Temporal)` |  |

---

### `class final TemporalAdjusters`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.time.temporal.TemporalAdjuster dayOfWeekInMonth(int, java.time.DayOfWeek)` |  |
| `static java.time.temporal.TemporalAdjuster firstDayOfMonth()` |  |
| `static java.time.temporal.TemporalAdjuster firstDayOfNextMonth()` |  |
| `static java.time.temporal.TemporalAdjuster firstDayOfNextYear()` |  |
| `static java.time.temporal.TemporalAdjuster firstDayOfYear()` |  |
| `static java.time.temporal.TemporalAdjuster firstInMonth(java.time.DayOfWeek)` |  |
| `static java.time.temporal.TemporalAdjuster lastDayOfMonth()` |  |
| `static java.time.temporal.TemporalAdjuster lastDayOfYear()` |  |
| `static java.time.temporal.TemporalAdjuster lastInMonth(java.time.DayOfWeek)` |  |
| `static java.time.temporal.TemporalAdjuster next(java.time.DayOfWeek)` |  |
| `static java.time.temporal.TemporalAdjuster nextOrSame(java.time.DayOfWeek)` |  |
| `static java.time.temporal.TemporalAdjuster ofDateAdjuster(java.util.function.UnaryOperator<java.time.LocalDate>)` |  |
| `static java.time.temporal.TemporalAdjuster previous(java.time.DayOfWeek)` |  |
| `static java.time.temporal.TemporalAdjuster previousOrSame(java.time.DayOfWeek)` |  |

---

### `interface TemporalAmount`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.Temporal addTo(java.time.temporal.Temporal)` |  |
| `long get(java.time.temporal.TemporalUnit)` |  |
| `java.util.List<java.time.temporal.TemporalUnit> getUnits()` |  |
| `java.time.temporal.Temporal subtractFrom(java.time.temporal.Temporal)` |  |

---

### `interface TemporalField`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `<R extends java.time.temporal.Temporal> R adjustInto(R, long)` |  |
| `java.time.temporal.TemporalUnit getBaseUnit()` |  |
| `default String getDisplayName(java.util.Locale)` |  |
| `long getFrom(java.time.temporal.TemporalAccessor)` |  |
| `java.time.temporal.TemporalUnit getRangeUnit()` |  |
| `boolean isDateBased()` |  |
| `boolean isSupportedBy(java.time.temporal.TemporalAccessor)` |  |
| `boolean isTimeBased()` |  |
| `java.time.temporal.ValueRange range()` |  |
| `java.time.temporal.ValueRange rangeRefinedBy(java.time.temporal.TemporalAccessor)` |  |
| `default java.time.temporal.TemporalAccessor resolve(java.util.Map<java.time.temporal.TemporalField,java.lang.Long>, java.time.temporal.TemporalAccessor, java.time.format.ResolverStyle)` |  |
| `String toString()` |  |

---

### `class final TemporalQueries`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.time.temporal.TemporalQuery<java.time.chrono.Chronology> chronology()` |  |
| `static java.time.temporal.TemporalQuery<java.time.LocalDate> localDate()` |  |
| `static java.time.temporal.TemporalQuery<java.time.LocalTime> localTime()` |  |
| `static java.time.temporal.TemporalQuery<java.time.ZoneOffset> offset()` |  |
| `static java.time.temporal.TemporalQuery<java.time.temporal.TemporalUnit> precision()` |  |
| `static java.time.temporal.TemporalQuery<java.time.ZoneId> zone()` |  |
| `static java.time.temporal.TemporalQuery<java.time.ZoneId> zoneId()` |  |

---

### `interface TemporalQuery<R>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `R queryFrom(java.time.temporal.TemporalAccessor)` |  |

---

### `interface TemporalUnit`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `<R extends java.time.temporal.Temporal> R addTo(R, long)` |  |
| `long between(java.time.temporal.Temporal, java.time.temporal.Temporal)` |  |
| `java.time.Duration getDuration()` |  |
| `boolean isDateBased()` |  |
| `boolean isDurationEstimated()` |  |
| `default boolean isSupportedBy(java.time.temporal.Temporal)` |  |
| `boolean isTimeBased()` |  |
| `String toString()` |  |

---

### `class UnsupportedTemporalTypeException`

- **Extends:** `java.time.DateTimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnsupportedTemporalTypeException(String)` |  |
| `UnsupportedTemporalTypeException(String, Throwable)` |  |

---

### `class final ValueRange`

- **Implements:** `java.io.Serializable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int checkValidIntValue(long, java.time.temporal.TemporalField)` |  |
| `long checkValidValue(long, java.time.temporal.TemporalField)` |  |
| `long getLargestMinimum()` |  |
| `long getMaximum()` |  |
| `long getMinimum()` |  |
| `long getSmallestMaximum()` |  |
| `boolean isFixed()` |  |
| `boolean isIntValue()` |  |
| `boolean isValidIntValue(long)` |  |
| `boolean isValidValue(long)` |  |
| `static java.time.temporal.ValueRange of(long, long)` |  |
| `static java.time.temporal.ValueRange of(long, long, long)` |  |
| `static java.time.temporal.ValueRange of(long, long, long, long)` |  |

---

### `class final WeekFields`

- **Implements:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.time.temporal.WeekFields ISO` |  |
| `static final java.time.temporal.WeekFields SUNDAY_START` |  |
| `static final java.time.temporal.TemporalUnit WEEK_BASED_YEARS` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.temporal.TemporalField dayOfWeek()` |  |
| `java.time.DayOfWeek getFirstDayOfWeek()` |  |
| `int getMinimalDaysInFirstWeek()` |  |
| `static java.time.temporal.WeekFields of(java.util.Locale)` |  |
| `static java.time.temporal.WeekFields of(java.time.DayOfWeek, int)` |  |
| `java.time.temporal.TemporalField weekBasedYear()` |  |
| `java.time.temporal.TemporalField weekOfMonth()` |  |
| `java.time.temporal.TemporalField weekOfWeekBasedYear()` |  |
| `java.time.temporal.TemporalField weekOfYear()` |  |

---

## Package: `java.time.zone`

### `class final ZoneOffsetTransition`

- **Implements:** `java.lang.Comparable<java.time.zone.ZoneOffsetTransition> java.io.Serializable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compareTo(java.time.zone.ZoneOffsetTransition)` |  |
| `java.time.LocalDateTime getDateTimeAfter()` |  |
| `java.time.LocalDateTime getDateTimeBefore()` |  |
| `java.time.Duration getDuration()` |  |
| `java.time.Instant getInstant()` |  |
| `java.time.ZoneOffset getOffsetAfter()` |  |
| `java.time.ZoneOffset getOffsetBefore()` |  |
| `boolean isGap()` |  |
| `boolean isOverlap()` |  |
| `boolean isValidOffset(java.time.ZoneOffset)` |  |
| `static java.time.zone.ZoneOffsetTransition of(java.time.LocalDateTime, java.time.ZoneOffset, java.time.ZoneOffset)` |  |
| `long toEpochSecond()` |  |

---

### `class final ZoneOffsetTransitionRule`

- **Implements:** `java.io.Serializable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.zone.ZoneOffsetTransition createTransition(int)` |  |
| `int getDayOfMonthIndicator()` |  |
| `java.time.DayOfWeek getDayOfWeek()` |  |
| `java.time.LocalTime getLocalTime()` |  |
| `java.time.Month getMonth()` |  |
| `java.time.ZoneOffset getOffsetAfter()` |  |
| `java.time.ZoneOffset getOffsetBefore()` |  |
| `java.time.ZoneOffset getStandardOffset()` |  |
| `java.time.zone.ZoneOffsetTransitionRule.TimeDefinition getTimeDefinition()` |  |
| `boolean isMidnightEndOfDay()` |  |
| `static java.time.zone.ZoneOffsetTransitionRule of(java.time.Month, int, java.time.DayOfWeek, java.time.LocalTime, boolean, java.time.zone.ZoneOffsetTransitionRule.TimeDefinition, java.time.ZoneOffset, java.time.ZoneOffset, java.time.ZoneOffset)` |  |

---

### `enum ZoneOffsetTransitionRule.TimeDefinition`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.time.zone.ZoneOffsetTransitionRule.TimeDefinition STANDARD` |  |
| `static final java.time.zone.ZoneOffsetTransitionRule.TimeDefinition UTC` |  |
| `static final java.time.zone.ZoneOffsetTransitionRule.TimeDefinition WALL` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.LocalDateTime createDateTime(java.time.LocalDateTime, java.time.ZoneOffset, java.time.ZoneOffset)` |  |

---

### `class final ZoneRules`

- **Implements:** `java.io.Serializable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.time.Duration getDaylightSavings(java.time.Instant)` |  |
| `java.time.ZoneOffset getOffset(java.time.Instant)` |  |
| `java.time.ZoneOffset getOffset(java.time.LocalDateTime)` |  |
| `java.time.ZoneOffset getStandardOffset(java.time.Instant)` |  |
| `java.time.zone.ZoneOffsetTransition getTransition(java.time.LocalDateTime)` |  |
| `java.util.List<java.time.zone.ZoneOffsetTransitionRule> getTransitionRules()` |  |
| `java.util.List<java.time.zone.ZoneOffsetTransition> getTransitions()` |  |
| `java.util.List<java.time.ZoneOffset> getValidOffsets(java.time.LocalDateTime)` |  |
| `boolean isDaylightSavings(java.time.Instant)` |  |
| `boolean isFixedOffset()` |  |
| `boolean isValidOffset(java.time.LocalDateTime, java.time.ZoneOffset)` |  |
| `java.time.zone.ZoneOffsetTransition nextTransition(java.time.Instant)` |  |
| `static java.time.zone.ZoneRules of(java.time.ZoneOffset, java.time.ZoneOffset, java.util.List<java.time.zone.ZoneOffsetTransition>, java.util.List<java.time.zone.ZoneOffsetTransition>, java.util.List<java.time.zone.ZoneOffsetTransitionRule>)` |  |
| `static java.time.zone.ZoneRules of(java.time.ZoneOffset)` |  |
| `java.time.zone.ZoneOffsetTransition previousTransition(java.time.Instant)` |  |

---

### `class ZoneRulesException`

- **Extends:** `java.time.DateTimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ZoneRulesException(String)` |  |
| `ZoneRulesException(String, Throwable)` |  |

---

## Package: `java.util`

### `class abstract AbstractCollection<E>`

- **Implements:** `java.util.Collection<E>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractCollection()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean add(E)` |  |
| `boolean addAll(@NonNull java.util.Collection<? extends E>)` |  |
| `void clear()` |  |
| `boolean contains(@Nullable Object)` |  |
| `boolean containsAll(@NonNull java.util.Collection<?>)` |  |
| `boolean isEmpty()` |  |
| `boolean remove(@Nullable Object)` |  |
| `boolean removeAll(@NonNull java.util.Collection<?>)` |  |
| `boolean retainAll(@NonNull java.util.Collection<?>)` |  |

---

### `class abstract AbstractList<E>`

- **Extends:** `java.util.AbstractCollection<E>`
- **Implements:** `java.util.List<E>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractList()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `transient int modCount` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void add(int, E)` |  |
| `boolean addAll(int, @NonNull java.util.Collection<? extends E>)` |  |
| `int indexOf(@Nullable Object)` |  |
| `int lastIndexOf(@Nullable Object)` |  |
| `E remove(int)` |  |
| `void removeRange(int, int)` |  |
| `E set(int, E)` |  |

---

### `class abstract AbstractMap<K, V>`

- **Implements:** `java.util.Map<K,V>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractMap()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clear()` |  |
| `boolean containsKey(@Nullable Object)` |  |
| `boolean containsValue(@Nullable Object)` |  |
| `boolean isEmpty()` |  |
| `void putAll(@NonNull java.util.Map<? extends K,? extends V>)` |  |
| `int size()` |  |

---

### `class static AbstractMap.SimpleEntry<K, V>`

- **Implements:** `java.util.Map.Entry<K,V> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractMap.SimpleEntry(K, V)` |  |
| `AbstractMap.SimpleEntry(@NonNull java.util.Map.Entry<? extends K,? extends V>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `K getKey()` |  |
| `V getValue()` |  |
| `V setValue(V)` |  |

---

### `class static AbstractMap.SimpleImmutableEntry<K, V>`

- **Implements:** `java.util.Map.Entry<K,V> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractMap.SimpleImmutableEntry(K, V)` |  |
| `AbstractMap.SimpleImmutableEntry(@NonNull java.util.Map.Entry<? extends K,? extends V>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `K getKey()` |  |
| `V getValue()` |  |
| `V setValue(V)` |  |

---

### `class abstract AbstractQueue<E>`

- **Extends:** `java.util.AbstractCollection<E>`
- **Implements:** `java.util.Queue<E>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractQueue()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `E element()` |  |
| `E remove()` |  |

---

### `class abstract AbstractSequentialList<E>`

- **Extends:** `java.util.AbstractList<E>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractSequentialList()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `E get(int)` |  |

---

### `class abstract AbstractSet<E>`

- **Extends:** `java.util.AbstractCollection<E>`
- **Implements:** `java.util.Set<E>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractSet()` |  |

---

### `class ArrayDeque<E>`

- **Extends:** `java.util.AbstractCollection<E>`
- **Implements:** `java.lang.Cloneable java.util.Deque<E> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ArrayDeque()` |  |
| `ArrayDeque(int)` |  |
| `ArrayDeque(@NonNull java.util.Collection<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addFirst(E)` |  |
| `void addLast(E)` |  |
| `E element()` |  |
| `E getFirst()` |  |
| `E getLast()` |  |
| `boolean offer(E)` |  |
| `boolean offerFirst(E)` |  |
| `boolean offerLast(E)` |  |
| `E pop()` |  |
| `void push(E)` |  |
| `E remove()` |  |
| `E removeFirst()` |  |
| `boolean removeFirstOccurrence(@Nullable Object)` |  |
| `E removeLast()` |  |
| `boolean removeLastOccurrence(@Nullable Object)` |  |
| `int size()` |  |

---

### `class ArrayList<E>`

- **Extends:** `java.util.AbstractList<E>`
- **Implements:** `java.lang.Cloneable java.util.List<E> java.util.RandomAccess java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ArrayList(int)` |  |
| `ArrayList()` |  |
| `ArrayList(@NonNull java.util.Collection<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void ensureCapacity(int)` |  |
| `void forEach(@NonNull java.util.function.Consumer<? super E>)` |  |
| `E get(int)` |  |
| `int size()` |  |
| `void trimToSize()` |  |

---

### `class Arrays`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int binarySearch(@NonNull long[], long)` |  |
| `static int binarySearch(@NonNull long[], int, int, long)` |  |
| `static int binarySearch(@NonNull int[], int)` |  |
| `static int binarySearch(@NonNull int[], int, int, int)` |  |
| `static int binarySearch(@NonNull short[], short)` |  |
| `static int binarySearch(@NonNull short[], int, int, short)` |  |
| `static int binarySearch(@NonNull char[], char)` |  |
| `static int binarySearch(@NonNull char[], int, int, char)` |  |
| `static int binarySearch(@NonNull byte[], byte)` |  |
| `static int binarySearch(@NonNull byte[], int, int, byte)` |  |
| `static int binarySearch(@NonNull double[], double)` |  |
| `static int binarySearch(@NonNull double[], int, int, double)` |  |
| `static int binarySearch(@NonNull float[], float)` |  |
| `static int binarySearch(@NonNull float[], int, int, float)` |  |
| `static int binarySearch(@NonNull Object[], @NonNull Object)` |  |
| `static int binarySearch(@NonNull Object[], int, int, @NonNull Object)` |  |
| `static <T> int binarySearch(@NonNull T[], T, @Nullable java.util.Comparator<? super T>)` |  |
| `static <T> int binarySearch(@NonNull T[], int, int, T, @Nullable java.util.Comparator<? super T>)` |  |
| `static boolean deepEquals(@Nullable Object[], @Nullable Object[])` |  |
| `static int deepHashCode(@Nullable Object[])` |  |
| `static boolean equals(@Nullable long[], @Nullable long[])` |  |
| `static boolean equals(@Nullable int[], @Nullable int[])` |  |
| `static boolean equals(@Nullable short[], @Nullable short[])` |  |
| `static boolean equals(@Nullable char[], @Nullable char[])` |  |
| `static boolean equals(@Nullable byte[], @Nullable byte[])` |  |
| `static boolean equals(@Nullable boolean[], @Nullable boolean[])` |  |
| `static boolean equals(@Nullable double[], @Nullable double[])` |  |
| `static boolean equals(@Nullable float[], @Nullable float[])` |  |
| `static boolean equals(@Nullable Object[], @Nullable Object[])` |  |
| `static void fill(@NonNull long[], long)` |  |
| `static void fill(@NonNull long[], int, int, long)` |  |
| `static void fill(@NonNull int[], int)` |  |
| `static void fill(@NonNull int[], int, int, int)` |  |
| `static void fill(@NonNull short[], short)` |  |
| `static void fill(@NonNull short[], int, int, short)` |  |
| `static void fill(@NonNull char[], char)` |  |
| `static void fill(@NonNull char[], int, int, char)` |  |
| `static void fill(@NonNull byte[], byte)` |  |
| `static void fill(@NonNull byte[], int, int, byte)` |  |
| `static void fill(@NonNull boolean[], boolean)` |  |
| `static void fill(@NonNull boolean[], int, int, boolean)` |  |
| `static void fill(@NonNull double[], double)` |  |
| `static void fill(@NonNull double[], int, int, double)` |  |
| `static void fill(@NonNull float[], float)` |  |
| `static void fill(@NonNull float[], int, int, float)` |  |
| `static void fill(@NonNull Object[], @Nullable Object)` |  |
| `static void fill(@NonNull Object[], int, int, @Nullable Object)` |  |
| `static int hashCode(@Nullable long[])` |  |
| `static int hashCode(@Nullable int[])` |  |
| `static int hashCode(@Nullable short[])` |  |
| `static int hashCode(@Nullable char[])` |  |
| `static int hashCode(@Nullable byte[])` |  |
| `static int hashCode(@Nullable boolean[])` |  |
| `static int hashCode(@Nullable float[])` |  |
| `static int hashCode(@Nullable double[])` |  |
| `static int hashCode(@Nullable Object[])` |  |
| `static <T> void parallelPrefix(@NonNull T[], @NonNull java.util.function.BinaryOperator<T>)` |  |
| `static <T> void parallelPrefix(@NonNull T[], int, int, @NonNull java.util.function.BinaryOperator<T>)` |  |
| `static void parallelPrefix(@NonNull long[], @NonNull java.util.function.LongBinaryOperator)` |  |
| `static void parallelPrefix(@NonNull long[], int, int, @NonNull java.util.function.LongBinaryOperator)` |  |
| `static void parallelPrefix(@NonNull double[], @NonNull java.util.function.DoubleBinaryOperator)` |  |
| `static void parallelPrefix(@NonNull double[], int, int, @NonNull java.util.function.DoubleBinaryOperator)` |  |
| `static void parallelPrefix(@NonNull int[], @NonNull java.util.function.IntBinaryOperator)` |  |
| `static void parallelPrefix(@NonNull int[], int, int, @NonNull java.util.function.IntBinaryOperator)` |  |
| `static <T> void parallelSetAll(@NonNull T[], @NonNull java.util.function.IntFunction<? extends T>)` |  |
| `static void parallelSetAll(@NonNull int[], @NonNull java.util.function.IntUnaryOperator)` |  |
| `static void parallelSetAll(@NonNull long[], @NonNull java.util.function.IntToLongFunction)` |  |
| `static void parallelSetAll(@NonNull double[], @NonNull java.util.function.IntToDoubleFunction)` |  |
| `static void parallelSort(@NonNull byte[])` |  |
| `static void parallelSort(@NonNull byte[], int, int)` |  |
| `static void parallelSort(@NonNull char[])` |  |
| `static void parallelSort(@NonNull char[], int, int)` |  |
| `static void parallelSort(@NonNull short[])` |  |
| `static void parallelSort(@NonNull short[], int, int)` |  |
| `static void parallelSort(@NonNull int[])` |  |
| `static void parallelSort(@NonNull int[], int, int)` |  |
| `static void parallelSort(@NonNull long[])` |  |
| `static void parallelSort(@NonNull long[], int, int)` |  |
| `static void parallelSort(@NonNull float[])` |  |
| `static void parallelSort(@NonNull float[], int, int)` |  |
| `static void parallelSort(@NonNull double[])` |  |
| `static void parallelSort(@NonNull double[], int, int)` |  |
| `static <T extends java.lang.Comparable<? super T>> void parallelSort(@NonNull T[])` |  |
| `static <T extends java.lang.Comparable<? super T>> void parallelSort(@NonNull T[], int, int)` |  |
| `static <T> void parallelSort(@NonNull T[], @Nullable java.util.Comparator<? super T>)` |  |
| `static <T> void parallelSort(@NonNull T[], int, int, @Nullable java.util.Comparator<? super T>)` |  |
| `static <T> void setAll(@NonNull T[], @NonNull java.util.function.IntFunction<? extends T>)` |  |
| `static void setAll(@NonNull int[], @NonNull java.util.function.IntUnaryOperator)` |  |
| `static void setAll(@NonNull long[], @NonNull java.util.function.IntToLongFunction)` |  |
| `static void setAll(@NonNull double[], @NonNull java.util.function.IntToDoubleFunction)` |  |
| `static void sort(@NonNull int[])` |  |
| `static void sort(@NonNull int[], int, int)` |  |
| `static void sort(@NonNull long[])` |  |
| `static void sort(@NonNull long[], int, int)` |  |
| `static void sort(@NonNull short[])` |  |
| `static void sort(@NonNull short[], int, int)` |  |
| `static void sort(@NonNull char[])` |  |
| `static void sort(@NonNull char[], int, int)` |  |
| `static void sort(@NonNull byte[])` |  |
| `static void sort(@NonNull byte[], int, int)` |  |
| `static void sort(@NonNull float[])` |  |
| `static void sort(@NonNull float[], int, int)` |  |
| `static void sort(@NonNull double[])` |  |
| `static void sort(@NonNull double[], int, int)` |  |
| `static void sort(@NonNull Object[])` |  |
| `static void sort(@NonNull Object[], int, int)` |  |
| `static <T> void sort(@NonNull T[], @Nullable java.util.Comparator<? super T>)` |  |
| `static <T> void sort(@NonNull T[], int, int, @Nullable java.util.Comparator<? super T>)` |  |

---

### `class Base64`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.Base64.Decoder getDecoder()` |  |
| `static java.util.Base64.Encoder getEncoder()` |  |
| `static java.util.Base64.Decoder getMimeDecoder()` |  |
| `static java.util.Base64.Encoder getMimeEncoder()` |  |
| `static java.util.Base64.Encoder getMimeEncoder(int, byte[])` |  |
| `static java.util.Base64.Decoder getUrlDecoder()` |  |
| `static java.util.Base64.Encoder getUrlEncoder()` |  |

---

### `class static Base64.Decoder`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] decode(byte[])` |  |
| `byte[] decode(String)` |  |
| `int decode(byte[], byte[])` |  |
| `java.nio.ByteBuffer decode(java.nio.ByteBuffer)` |  |
| `java.io.InputStream wrap(java.io.InputStream)` |  |

---

### `class static Base64.Encoder`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] encode(byte[])` |  |
| `int encode(byte[], byte[])` |  |
| `java.nio.ByteBuffer encode(java.nio.ByteBuffer)` |  |
| `String encodeToString(byte[])` |  |
| `java.util.Base64.Encoder withoutPadding()` |  |
| `java.io.OutputStream wrap(java.io.OutputStream)` |  |

---

### `class BitSet`

- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BitSet()` |  |
| `BitSet(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void and(java.util.BitSet)` |  |
| `void andNot(java.util.BitSet)` |  |
| `int cardinality()` |  |
| `void clear(int)` |  |
| `void clear(int, int)` |  |
| `void clear()` |  |
| `Object clone()` |  |
| `void flip(int)` |  |
| `void flip(int, int)` |  |
| `boolean get(int)` |  |
| `java.util.BitSet get(int, int)` |  |
| `boolean intersects(java.util.BitSet)` |  |
| `boolean isEmpty()` |  |
| `int length()` |  |
| `int nextClearBit(int)` |  |
| `int nextSetBit(int)` |  |
| `void or(java.util.BitSet)` |  |
| `int previousClearBit(int)` |  |
| `int previousSetBit(int)` |  |
| `void set(int)` |  |
| `void set(int, boolean)` |  |
| `void set(int, int)` |  |
| `void set(int, int, boolean)` |  |
| `int size()` |  |
| `java.util.stream.IntStream stream()` |  |
| `byte[] toByteArray()` |  |
| `long[] toLongArray()` |  |
| `static java.util.BitSet valueOf(long[])` |  |
| `static java.util.BitSet valueOf(java.nio.LongBuffer)` |  |
| `static java.util.BitSet valueOf(byte[])` |  |
| `static java.util.BitSet valueOf(java.nio.ByteBuffer)` |  |
| `void xor(java.util.BitSet)` |  |

---

### `class abstract Calendar`

- **Implements:** `java.lang.Cloneable java.lang.Comparable<java.util.Calendar> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Calendar()` |  |
| `Calendar(@NonNull java.util.TimeZone, @NonNull java.util.Locale)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ALL_STYLES = 0` |  |
| `static final int AM = 0` |  |
| `static final int AM_PM = 9` |  |
| `static final int APRIL = 3` |  |
| `static final int AUGUST = 7` |  |
| `static final int DATE = 5` |  |
| `static final int DAY_OF_MONTH = 5` |  |
| `static final int DAY_OF_WEEK = 7` |  |
| `static final int DAY_OF_WEEK_IN_MONTH = 8` |  |
| `static final int DAY_OF_YEAR = 6` |  |
| `static final int DECEMBER = 11` |  |
| `static final int DST_OFFSET = 16` |  |
| `static final int ERA = 0` |  |
| `static final int FEBRUARY = 1` |  |
| `static final int FIELD_COUNT = 17` |  |
| `static final int FRIDAY = 6` |  |
| `static final int HOUR = 10` |  |
| `static final int HOUR_OF_DAY = 11` |  |
| `static final int JANUARY = 0` |  |
| `static final int JULY = 6` |  |
| `static final int JUNE = 5` |  |
| `static final int LONG = 2` |  |
| `static final int LONG_FORMAT = 2` |  |
| `static final int LONG_STANDALONE = 32770` |  |
| `static final int MARCH = 2` |  |
| `static final int MAY = 4` |  |
| `static final int MILLISECOND = 14` |  |
| `static final int MINUTE = 12` |  |
| `static final int MONDAY = 2` |  |
| `static final int MONTH = 2` |  |
| `static final int NARROW_FORMAT = 4` |  |
| `static final int NARROW_STANDALONE = 32772` |  |
| `static final int NOVEMBER = 10` |  |
| `static final int OCTOBER = 9` |  |
| `static final int PM = 1` |  |
| `static final int SATURDAY = 7` |  |
| `static final int SECOND = 13` |  |
| `static final int SEPTEMBER = 8` |  |
| `static final int SHORT = 1` |  |
| `static final int SHORT_FORMAT = 1` |  |
| `static final int SHORT_STANDALONE = 32769` |  |
| `static final int SUNDAY = 1` |  |
| `static final int THURSDAY = 5` |  |
| `static final int TUESDAY = 3` |  |
| `static final int UNDECIMBER = 12` |  |
| `static final int WEDNESDAY = 4` |  |
| `static final int WEEK_OF_MONTH = 4` |  |
| `static final int WEEK_OF_YEAR = 3` |  |
| `static final int YEAR = 1` |  |
| `static final int ZONE_OFFSET = 15` |  |
| `boolean areFieldsSet` |  |
| `boolean isTimeSet` |  |
| `long time` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void add(int, int)` |  |
| `boolean after(@Nullable Object)` |  |
| `boolean before(@Nullable Object)` |  |
| `final void clear()` |  |
| `final void clear(int)` |  |
| `int compareTo(@NonNull java.util.Calendar)` |  |
| `void complete()` |  |
| `abstract void computeFields()` |  |
| `abstract void computeTime()` |  |
| `int get(int)` |  |
| `int getActualMaximum(int)` |  |
| `int getActualMinimum(int)` |  |
| `int getFirstDayOfWeek()` |  |
| `abstract int getGreatestMinimum(int)` |  |
| `abstract int getLeastMaximum(int)` |  |
| `abstract int getMaximum(int)` |  |
| `int getMinimalDaysInFirstWeek()` |  |
| `abstract int getMinimum(int)` |  |
| `long getTimeInMillis()` |  |
| `int getWeekYear()` |  |
| `int getWeeksInWeekYear()` |  |
| `final int internalGet(int)` |  |
| `boolean isLenient()` |  |
| `final boolean isSet(int)` |  |
| `boolean isWeekDateSupported()` |  |
| `abstract void roll(int, boolean)` |  |
| `void roll(int, int)` |  |
| `void set(int, int)` |  |
| `final void set(int, int, int)` |  |
| `final void set(int, int, int, int, int)` |  |
| `final void set(int, int, int, int, int, int)` |  |
| `void setFirstDayOfWeek(int)` |  |
| `void setLenient(boolean)` |  |
| `void setMinimalDaysInFirstWeek(int)` |  |
| `final void setTime(@NonNull java.util.Date)` |  |
| `void setTimeInMillis(long)` |  |
| `void setTimeZone(@NonNull java.util.TimeZone)` |  |
| `void setWeekDate(int, int, int)` |  |

---

### `class static Calendar.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Calendar.Builder()` |  |

---

### `interface Collection<E>`

- **Extends:** `java.lang.Iterable<E>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean add(E)` |  |
| `boolean addAll(@NonNull java.util.Collection<? extends E>)` |  |
| `void clear()` |  |
| `boolean contains(@Nullable Object)` |  |
| `boolean containsAll(@NonNull java.util.Collection<?>)` |  |
| `boolean equals(@Nullable Object)` |  |
| `int hashCode()` |  |
| `boolean isEmpty()` |  |
| `boolean remove(@Nullable Object)` |  |
| `boolean removeAll(@NonNull java.util.Collection<?>)` |  |
| `default boolean removeIf(@NonNull java.util.function.Predicate<? super E>)` |  |
| `boolean retainAll(@NonNull java.util.Collection<?>)` |  |
| `int size()` |  |

---

### `class Collections`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static <T> int binarySearch(@NonNull java.util.List<? extends java.lang.Comparable<? super T>>, @NonNull T)` |  |
| `static <T> int binarySearch(@NonNull java.util.List<? extends T>, T, @Nullable java.util.Comparator<? super T>)` |  |
| `static <T> void copy(@NonNull java.util.List<? super T>, @NonNull java.util.List<? extends T>)` |  |
| `static boolean disjoint(@NonNull java.util.Collection<?>, @NonNull java.util.Collection<?>)` |  |
| `static <T> void fill(@NonNull java.util.List<? super T>, T)` |  |
| `static int frequency(@NonNull java.util.Collection<?>, @Nullable Object)` |  |
| `static int indexOfSubList(@NonNull java.util.List<?>, @NonNull java.util.List<?>)` |  |
| `static int lastIndexOfSubList(@NonNull java.util.List<?>, @NonNull java.util.List<?>)` |  |
| `static <T> T max(@NonNull java.util.Collection<? extends T>, @Nullable java.util.Comparator<? super T>)` |  |
| `static <T> T min(@NonNull java.util.Collection<? extends T>, @Nullable java.util.Comparator<? super T>)` |  |
| `static <T> boolean replaceAll(@NonNull java.util.List<T>, T, T)` |  |
| `static void reverse(@NonNull java.util.List<?>)` |  |
| `static void rotate(@NonNull java.util.List<?>, int)` |  |
| `static void shuffle(@NonNull java.util.List<?>)` |  |
| `static void shuffle(@NonNull java.util.List<?>, @NonNull java.util.Random)` |  |
| `static <T extends java.lang.Comparable<? super T>> void sort(@NonNull java.util.List<T>)` |  |
| `static <T> void sort(@NonNull java.util.List<T>, @Nullable java.util.Comparator<? super T>)` |  |
| `static void swap(@NonNull java.util.List<?>, int, int)` |  |

---

### `interface Comparator<T>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compare(T, T)` |  |
| `static <T, U> java.util.Comparator<T> comparing(java.util.function.Function<? super T,? extends U>, java.util.Comparator<? super U>)` |  |
| `static <T, U extends java.lang.Comparable<? super U>> java.util.Comparator<T> comparing(java.util.function.Function<? super T,? extends U>)` |  |
| `static <T> java.util.Comparator<T> comparingDouble(java.util.function.ToDoubleFunction<? super T>)` |  |
| `static <T> java.util.Comparator<T> comparingInt(java.util.function.ToIntFunction<? super T>)` |  |
| `static <T> java.util.Comparator<T> comparingLong(java.util.function.ToLongFunction<? super T>)` |  |
| `boolean equals(Object)` |  |
| `static <T extends java.lang.Comparable<? super T>> java.util.Comparator<T> naturalOrder()` |  |
| `static <T> java.util.Comparator<T> nullsFirst(java.util.Comparator<? super T>)` |  |
| `static <T> java.util.Comparator<T> nullsLast(java.util.Comparator<? super T>)` |  |
| `static <T extends java.lang.Comparable<? super T>> java.util.Comparator<T> reverseOrder()` |  |
| `default java.util.Comparator<T> reversed()` |  |
| `default java.util.Comparator<T> thenComparing(java.util.Comparator<? super T>)` |  |
| `default <U> java.util.Comparator<T> thenComparing(java.util.function.Function<? super T,? extends U>, java.util.Comparator<? super U>)` |  |
| `default <U extends java.lang.Comparable<? super U>> java.util.Comparator<T> thenComparing(java.util.function.Function<? super T,? extends U>)` |  |
| `default java.util.Comparator<T> thenComparingDouble(java.util.function.ToDoubleFunction<? super T>)` |  |
| `default java.util.Comparator<T> thenComparingInt(java.util.function.ToIntFunction<? super T>)` |  |
| `default java.util.Comparator<T> thenComparingLong(java.util.function.ToLongFunction<? super T>)` |  |

---

### `class ConcurrentModificationException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConcurrentModificationException()` |  |
| `ConcurrentModificationException(String)` |  |
| `ConcurrentModificationException(Throwable)` |  |
| `ConcurrentModificationException(String, Throwable)` |  |

---

### `class final Currency`

- **Implements:** `java.io.Serializable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.Set<java.util.Currency> getAvailableCurrencies()` |  |
| `String getCurrencyCode()` |  |
| `int getDefaultFractionDigits()` |  |
| `String getDisplayName()` |  |
| `String getDisplayName(java.util.Locale)` |  |
| `static java.util.Currency getInstance(String)` |  |
| `static java.util.Currency getInstance(java.util.Locale)` |  |
| `int getNumericCode()` |  |
| `String getSymbol()` |  |
| `String getSymbol(java.util.Locale)` |  |

---

### `class Date`

- **Implements:** `java.lang.Cloneable java.lang.Comparable<java.util.Date> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Date()` |  |
| `Date(long)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean after(java.util.Date)` |  |
| `boolean before(java.util.Date)` |  |
| `Object clone()` |  |
| `int compareTo(java.util.Date)` |  |
| `static java.util.Date from(java.time.Instant)` |  |
| `long getTime()` |  |
| `void setTime(long)` |  |
| `java.time.Instant toInstant()` |  |

---

### `interface Deque<E>`

- **Extends:** `java.util.Queue<E>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addFirst(E)` |  |
| `void addLast(E)` |  |
| `E getFirst()` |  |
| `E getLast()` |  |
| `boolean offerFirst(E)` |  |
| `boolean offerLast(E)` |  |
| `E pop()` |  |
| `void push(E)` |  |
| `E removeFirst()` |  |
| `boolean removeFirstOccurrence(@Nullable Object)` |  |
| `E removeLast()` |  |
| `boolean removeLastOccurrence(@Nullable Object)` |  |

---

### `class abstract Dictionary<K, V>`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Dictionary()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.util.Enumeration<V> elements()` |  |
| `abstract V get(Object)` |  |
| `abstract boolean isEmpty()` |  |
| `abstract java.util.Enumeration<K> keys()` |  |
| `abstract V put(K, V)` |  |
| `abstract V remove(Object)` |  |
| `abstract int size()` |  |

---

### `class DoubleSummaryStatistics`

- **Implements:** `java.util.function.DoubleConsumer`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DoubleSummaryStatistics()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accept(double)` |  |
| `void combine(java.util.DoubleSummaryStatistics)` |  |
| `final double getAverage()` |  |
| `final long getCount()` |  |
| `final double getMax()` |  |
| `final double getMin()` |  |
| `final double getSum()` |  |

---

### `class DuplicateFormatFlagsException`

- **Extends:** `java.util.IllegalFormatException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DuplicateFormatFlagsException(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getFlags()` |  |

---

### `class EmptyStackException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EmptyStackException()` |  |

---

### `class EnumMap<K`

- **Extends:** `java.lang.Enum<K>, V> extends java.util.AbstractMap<K,V>`
- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EnumMap(Class<K>)` |  |
| `EnumMap(java.util.EnumMap<K,? extends V>)` |  |
| `EnumMap(java.util.Map<K,? extends V>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.EnumMap<K,V> clone()` |  |
| `java.util.Set<java.util.Map.Entry<K,V>> entrySet()` |  |

---

### `class abstract EnumSet<E`

- **Extends:** `java.lang.Enum<E>> extends java.util.AbstractSet<E>`
- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static <E extends java.lang.Enum<E>> java.util.EnumSet<E> allOf(Class<E>)` |  |
| `java.util.EnumSet<E> clone()` |  |
| `static <E extends java.lang.Enum<E>> java.util.EnumSet<E> complementOf(java.util.EnumSet<E>)` |  |
| `static <E extends java.lang.Enum<E>> java.util.EnumSet<E> copyOf(java.util.EnumSet<E>)` |  |
| `static <E extends java.lang.Enum<E>> java.util.EnumSet<E> copyOf(java.util.Collection<E>)` |  |
| `static <E extends java.lang.Enum<E>> java.util.EnumSet<E> noneOf(Class<E>)` |  |
| `static <E extends java.lang.Enum<E>> java.util.EnumSet<E> of(E)` |  |
| `static <E extends java.lang.Enum<E>> java.util.EnumSet<E> of(E, E)` |  |
| `static <E extends java.lang.Enum<E>> java.util.EnumSet<E> of(E, E, E)` |  |
| `static <E extends java.lang.Enum<E>> java.util.EnumSet<E> of(E, E, E, E)` |  |
| `static <E extends java.lang.Enum<E>> java.util.EnumSet<E> of(E, E, E, E, E)` |  |
| `static <E extends java.lang.Enum<E>> java.util.EnumSet<E> range(E, E)` |  |

---

### `interface Enumeration<E>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean hasMoreElements()` |  |
| `E nextElement()` |  |

---

### `interface EventListener`


---

### `class abstract EventListenerProxy<T`

- **Extends:** `java.util.EventListener>`
- **Implements:** `java.util.EventListener`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EventListenerProxy(T)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `T getListener()` |  |

---

### `class EventObject`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EventObject(Object)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `transient Object source` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object getSource()` |  |

---

### `class FormatFlagsConversionMismatchException`

- **Extends:** `java.util.IllegalFormatException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FormatFlagsConversionMismatchException(String, char)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `char getConversion()` |  |
| `String getFlags()` |  |

---

### `interface Formattable`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void formatTo(java.util.Formatter, int, int, int)` |  |

---

### `class FormattableFlags`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ALTERNATE = 4` |  |
| `static final int LEFT_JUSTIFY = 1` |  |
| `static final int UPPERCASE = 2` |  |

---

### `class final Formatter`

- **Implements:** `java.io.Closeable java.io.Flushable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Formatter()` |  |
| `Formatter(Appendable)` |  |
| `Formatter(java.util.Locale)` |  |
| `Formatter(Appendable, java.util.Locale)` |  |
| `Formatter(String) throws java.io.FileNotFoundException` |  |
| `Formatter(String, String) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException` |  |
| `Formatter(String, String, java.util.Locale) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException` |  |
| `Formatter(java.io.File) throws java.io.FileNotFoundException` |  |
| `Formatter(java.io.File, String) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException` |  |
| `Formatter(java.io.File, String, java.util.Locale) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException` |  |
| `Formatter(java.io.PrintStream)` |  |
| `Formatter(java.io.OutputStream)` |  |
| `Formatter(java.io.OutputStream, String) throws java.io.UnsupportedEncodingException` |  |
| `Formatter(java.io.OutputStream, String, java.util.Locale) throws java.io.UnsupportedEncodingException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `void flush()` |  |
| `java.util.Formatter format(String, java.lang.Object...)` |  |
| `java.util.Formatter format(java.util.Locale, String, java.lang.Object...)` |  |
| `java.io.IOException ioException()` |  |
| `java.util.Locale locale()` |  |
| `Appendable out()` |  |

---

### `enum Formatter.BigDecimalLayoutForm`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.util.Formatter.BigDecimalLayoutForm DECIMAL_FLOAT` |  |
| `static final java.util.Formatter.BigDecimalLayoutForm SCIENTIFIC` |  |

---

### `class FormatterClosedException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FormatterClosedException()` |  |

---

### `class GregorianCalendar`

- **Extends:** `java.util.Calendar`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GregorianCalendar()` |  |
| `GregorianCalendar(java.util.TimeZone)` |  |
| `GregorianCalendar(java.util.Locale)` |  |
| `GregorianCalendar(java.util.TimeZone, java.util.Locale)` |  |
| `GregorianCalendar(int, int, int)` |  |
| `GregorianCalendar(int, int, int, int, int)` |  |
| `GregorianCalendar(int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AD = 1` |  |
| `static final int BC = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void add(int, int)` |  |
| `void computeFields()` |  |
| `void computeTime()` |  |
| `static java.util.GregorianCalendar from(java.time.ZonedDateTime)` |  |
| `int getGreatestMinimum(int)` |  |
| `final java.util.Date getGregorianChange()` |  |
| `int getLeastMaximum(int)` |  |
| `int getMaximum(int)` |  |
| `int getMinimum(int)` |  |
| `boolean isLeapYear(int)` |  |
| `final boolean isWeekDateSupported()` |  |
| `void roll(int, boolean)` |  |
| `void setGregorianChange(java.util.Date)` |  |
| `java.time.ZonedDateTime toZonedDateTime()` |  |

---

### `class HashMap<K, V>`

- **Extends:** `java.util.AbstractMap<K,V>`
- **Implements:** `java.lang.Cloneable java.util.Map<K,V> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `HashMap(int, float)` |  |
| `HashMap(int)` |  |
| `HashMap()` |  |
| `HashMap(@NonNull java.util.Map<? extends K,? extends V>)` |  |

---

### `class HashSet<E>`

- **Extends:** `java.util.AbstractSet<E>`
- **Implements:** `java.lang.Cloneable java.io.Serializable java.util.Set<E>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `HashSet()` |  |
| `HashSet(@NonNull java.util.Collection<? extends E>)` |  |
| `HashSet(int, float)` |  |
| `HashSet(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int size()` |  |

---

### `class Hashtable<K, V>`

- **Extends:** `java.util.Dictionary<K,V>`
- **Implements:** `java.lang.Cloneable java.util.Map<K,V> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Hashtable(int, float)` |  |
| `Hashtable(int)` |  |
| `Hashtable()` |  |
| `Hashtable(java.util.Map<? extends K,? extends V>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clear()` |  |
| `Object clone()` |  |
| `boolean contains(Object)` |  |
| `boolean containsKey(Object)` |  |
| `boolean containsValue(Object)` |  |
| `java.util.Enumeration<V> elements()` |  |
| `java.util.Set<java.util.Map.Entry<K,V>> entrySet()` |  |
| `V get(Object)` |  |
| `boolean isEmpty()` |  |
| `java.util.Set<K> keySet()` |  |
| `java.util.Enumeration<K> keys()` |  |
| `V put(K, V)` |  |
| `void putAll(java.util.Map<? extends K,? extends V>)` |  |
| `void rehash()` |  |
| `V remove(Object)` |  |
| `int size()` |  |
| `java.util.Collection<V> values()` |  |

---

### `class IdentityHashMap<K, V>`

- **Extends:** `java.util.AbstractMap<K,V>`
- **Implements:** `java.lang.Cloneable java.util.Map<K,V> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IdentityHashMap()` |  |
| `IdentityHashMap(int)` |  |
| `IdentityHashMap(java.util.Map<? extends K,? extends V>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `java.util.Set<java.util.Map.Entry<K,V>> entrySet()` |  |

---

### `class IllegalFormatCodePointException`

- **Extends:** `java.util.IllegalFormatException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalFormatCodePointException(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getCodePoint()` |  |

---

### `class IllegalFormatConversionException`

- **Extends:** `java.util.IllegalFormatException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalFormatConversionException(char, Class<?>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Class<?> getArgumentClass()` |  |
| `char getConversion()` |  |

---

### `class IllegalFormatException`

- **Extends:** `java.lang.IllegalArgumentException`

---

### `class IllegalFormatFlagsException`

- **Extends:** `java.util.IllegalFormatException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalFormatFlagsException(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getFlags()` |  |

---

### `class IllegalFormatPrecisionException`

- **Extends:** `java.util.IllegalFormatException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalFormatPrecisionException(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getPrecision()` |  |

---

### `class IllegalFormatWidthException`

- **Extends:** `java.util.IllegalFormatException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllegalFormatWidthException(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getWidth()` |  |

---

### `class IllformedLocaleException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllformedLocaleException()` |  |
| `IllformedLocaleException(String)` |  |
| `IllformedLocaleException(String, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getErrorIndex()` |  |

---

### `class InputMismatchException`

- **Extends:** `java.util.NoSuchElementException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InputMismatchException()` |  |
| `InputMismatchException(String)` |  |

---

### `class IntSummaryStatistics`

- **Implements:** `java.util.function.IntConsumer`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IntSummaryStatistics()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accept(int)` |  |
| `void combine(java.util.IntSummaryStatistics)` |  |
| `final double getAverage()` |  |
| `final long getCount()` |  |
| `final int getMax()` |  |
| `final int getMin()` |  |
| `final long getSum()` |  |

---

### `class InvalidPropertiesFormatException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidPropertiesFormatException(Throwable)` |  |
| `InvalidPropertiesFormatException(String)` |  |

---

### `interface Iterator<E>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default void forEachRemaining(@NonNull java.util.function.Consumer<? super E>)` |  |
| `boolean hasNext()` |  |
| `E next()` |  |
| `default void remove()` |  |

---

### `class LinkedHashMap<K, V>`

- **Extends:** `java.util.HashMap<K,V>`
- **Implements:** `java.util.Map<K,V>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LinkedHashMap(int, float)` |  |
| `LinkedHashMap(int)` |  |
| `LinkedHashMap()` |  |
| `LinkedHashMap(java.util.Map<? extends K,? extends V>)` |  |
| `LinkedHashMap(int, float, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean removeEldestEntry(java.util.Map.Entry<K,V>)` |  |

---

### `class LinkedHashSet<E>`

- **Extends:** `java.util.HashSet<E>`
- **Implements:** `java.lang.Cloneable java.io.Serializable java.util.Set<E>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LinkedHashSet(int, float)` |  |
| `LinkedHashSet(int)` |  |
| `LinkedHashSet()` |  |
| `LinkedHashSet(java.util.Collection<? extends E>)` |  |

---

### `class LinkedList<E>`

- **Extends:** `java.util.AbstractSequentialList<E>`
- **Implements:** `java.lang.Cloneable java.util.Deque<E> java.util.List<E> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LinkedList()` |  |
| `LinkedList(@NonNull java.util.Collection<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addFirst(E)` |  |
| `void addLast(E)` |  |
| `E element()` |  |
| `E getFirst()` |  |
| `E getLast()` |  |
| `boolean offer(E)` |  |
| `boolean offerFirst(E)` |  |
| `boolean offerLast(E)` |  |
| `E pop()` |  |
| `void push(E)` |  |
| `E remove()` |  |
| `E removeFirst()` |  |
| `boolean removeFirstOccurrence(@Nullable Object)` |  |
| `E removeLast()` |  |
| `boolean removeLastOccurrence(@Nullable Object)` |  |
| `int size()` |  |

---

### `interface List<E>`

- **Extends:** `java.util.Collection<E>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void add(int, E)` |  |
| `boolean addAll(int, @NonNull java.util.Collection<? extends E>)` |  |
| `E get(int)` |  |
| `int indexOf(@Nullable Object)` |  |
| `int lastIndexOf(@Nullable Object)` |  |
| `E remove(int)` |  |
| `default void replaceAll(@NonNull java.util.function.UnaryOperator<E>)` |  |
| `E set(int, E)` |  |
| `default void sort(@Nullable java.util.Comparator<? super E>)` |  |

---

### `interface ListIterator<E>`

- **Extends:** `java.util.Iterator<E>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void add(E)` |  |
| `boolean hasPrevious()` |  |
| `int nextIndex()` |  |
| `E previous()` |  |
| `int previousIndex()` |  |
| `void remove()` |  |
| `void set(E)` |  |

---

### `class abstract ListResourceBundle`

- **Extends:** `java.util.ResourceBundle`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ListResourceBundle()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract Object[][] getContents()` |  |
| `java.util.Enumeration<java.lang.String> getKeys()` |  |
| `final Object handleGetObject(String)` |  |

---

### `class final Locale`

- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Locale(@NonNull String, @NonNull String, @NonNull String)` |  |
| `Locale(@NonNull String, @NonNull String)` |  |
| `Locale(@NonNull String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final char PRIVATE_USE_EXTENSION = 120` |  |
| `static final char UNICODE_LOCALE_EXTENSION = 117` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean hasExtensions()` |  |
| `static void setDefault(@NonNull java.util.Locale)` |  |
| `static void setDefault(@NonNull java.util.Locale.Category, @NonNull java.util.Locale)` |  |

---

### `class static final Locale.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Locale.Builder()` |  |

---

### `enum Locale.Category`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.util.Locale.Category DISPLAY` |  |
| `static final java.util.Locale.Category FORMAT` |  |

---

### `enum Locale.FilteringMode`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.util.Locale.FilteringMode AUTOSELECT_FILTERING` |  |
| `static final java.util.Locale.FilteringMode EXTENDED_FILTERING` |  |
| `static final java.util.Locale.FilteringMode IGNORE_EXTENDED_RANGES` |  |
| `static final java.util.Locale.FilteringMode MAP_EXTENDED_RANGES` |  |
| `static final java.util.Locale.FilteringMode REJECT_EXTENDED_RANGES` |  |

---

### `class static final Locale.LanguageRange`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Locale.LanguageRange(@NonNull String)` |  |
| `Locale.LanguageRange(@NonNull String, double)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final double MAX_WEIGHT = 1.0` |  |
| `static final double MIN_WEIGHT = 0.0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `double getWeight()` |  |

---

### `class LongSummaryStatistics`

- **Implements:** `java.util.function.IntConsumer java.util.function.LongConsumer`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LongSummaryStatistics()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accept(int)` |  |
| `void accept(long)` |  |
| `void combine(java.util.LongSummaryStatistics)` |  |
| `final double getAverage()` |  |
| `final long getCount()` |  |
| `final long getMax()` |  |
| `final long getMin()` |  |
| `final long getSum()` |  |

---

### `interface Map<K, V>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clear()` |  |
| `boolean containsKey(@Nullable Object)` |  |
| `boolean containsValue(@Nullable Object)` |  |
| `boolean equals(@Nullable Object)` |  |
| `default void forEach(@NonNull java.util.function.BiConsumer<? super K,? super V>)` |  |
| `int hashCode()` |  |
| `boolean isEmpty()` |  |
| `void putAll(@NonNull java.util.Map<? extends K,? extends V>)` |  |
| `default boolean remove(@Nullable Object, @Nullable Object)` |  |
| `default boolean replace(K, @Nullable V, V)` |  |
| `default void replaceAll(@NonNull java.util.function.BiFunction<? super K,? super V,? extends V>)` |  |
| `int size()` |  |

---

### `interface static Map.Entry<K, V>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean equals(@Nullable Object)` |  |
| `K getKey()` |  |
| `V getValue()` |  |
| `int hashCode()` |  |
| `V setValue(V)` |  |

---

### `class MissingFormatArgumentException`

- **Extends:** `java.util.IllegalFormatException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MissingFormatArgumentException(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getFormatSpecifier()` |  |

---

### `class MissingFormatWidthException`

- **Extends:** `java.util.IllegalFormatException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MissingFormatWidthException(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getFormatSpecifier()` |  |

---

### `class MissingResourceException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MissingResourceException(String, String, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getClassName()` |  |
| `String getKey()` |  |

---

### `interface NavigableMap<K, V>`

- **Extends:** `java.util.SortedMap<K,V>`

---

### `interface NavigableSet<E>`

- **Extends:** `java.util.SortedSet<E>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `E ceiling(E)` |  |
| `java.util.Iterator<E> descendingIterator()` |  |
| `java.util.NavigableSet<E> descendingSet()` |  |
| `E floor(E)` |  |
| `java.util.NavigableSet<E> headSet(E, boolean)` |  |
| `E higher(E)` |  |
| `E lower(E)` |  |
| `E pollFirst()` |  |
| `E pollLast()` |  |
| `java.util.NavigableSet<E> subSet(E, boolean, E, boolean)` |  |
| `java.util.NavigableSet<E> tailSet(E, boolean)` |  |

---

### `class NoSuchElementException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NoSuchElementException()` |  |
| `NoSuchElementException(String)` |  |

---

### `class final Objects`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int checkFromIndexSize(int, int, int)` |  |
| `static int checkFromToIndex(int, int, int)` |  |
| `static int checkIndex(int, int)` |  |
| `static <T> int compare(T, T, @NonNull java.util.Comparator<? super T>)` |  |
| `static boolean deepEquals(@Nullable Object, @Nullable Object)` |  |
| `static boolean equals(@Nullable Object, @Nullable Object)` |  |
| `static int hash(@Nullable java.lang.Object...)` |  |
| `static int hashCode(@Nullable Object)` |  |
| `static boolean isNull(@Nullable Object)` |  |
| `static boolean nonNull(@Nullable Object)` |  |

---

### `class Observable`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Observable()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addObserver(java.util.Observer)` |  |
| `void clearChanged()` |  |
| `int countObservers()` |  |
| `void deleteObserver(java.util.Observer)` |  |
| `void deleteObservers()` |  |
| `boolean hasChanged()` |  |
| `void notifyObservers()` |  |
| `void notifyObservers(Object)` |  |
| `void setChanged()` |  |

---

### `interface Observer`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void update(java.util.Observable, Object)` |  |

---

### `class final Optional<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static <T> java.util.Optional<T> empty()` |  |
| `java.util.Optional<T> filter(java.util.function.Predicate<? super T>)` |  |
| `<U> java.util.Optional<U> flatMap(java.util.function.Function<? super T,java.util.Optional<U>>)` |  |
| `T get()` |  |
| `void ifPresent(java.util.function.Consumer<? super T>)` |  |
| `boolean isPresent()` |  |
| `<U> java.util.Optional<U> map(java.util.function.Function<? super T,? extends U>)` |  |
| `static <T> java.util.Optional<T> of(T)` |  |
| `static <T> java.util.Optional<T> ofNullable(T)` |  |
| `T orElse(T)` |  |
| `T orElseGet(java.util.function.Supplier<? extends T>)` |  |
| `<X extends java.lang.Throwable> T orElseThrow(java.util.function.Supplier<? extends X>) throws X` |  |

---

### `class final OptionalDouble`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.OptionalDouble empty()` |  |
| `double getAsDouble()` |  |
| `void ifPresent(java.util.function.DoubleConsumer)` |  |
| `boolean isPresent()` |  |
| `static java.util.OptionalDouble of(double)` |  |
| `double orElse(double)` |  |
| `double orElseGet(java.util.function.DoubleSupplier)` |  |
| `<X extends java.lang.Throwable> double orElseThrow(java.util.function.Supplier<X>) throws X` |  |

---

### `class final OptionalInt`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.OptionalInt empty()` |  |
| `int getAsInt()` |  |
| `void ifPresent(java.util.function.IntConsumer)` |  |
| `boolean isPresent()` |  |
| `static java.util.OptionalInt of(int)` |  |
| `int orElse(int)` |  |
| `int orElseGet(java.util.function.IntSupplier)` |  |
| `<X extends java.lang.Throwable> int orElseThrow(java.util.function.Supplier<X>) throws X` |  |

---

### `class final OptionalLong`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.OptionalLong empty()` |  |
| `long getAsLong()` |  |
| `void ifPresent(java.util.function.LongConsumer)` |  |
| `boolean isPresent()` |  |
| `static java.util.OptionalLong of(long)` |  |
| `long orElse(long)` |  |
| `long orElseGet(java.util.function.LongSupplier)` |  |
| `<X extends java.lang.Throwable> long orElseThrow(java.util.function.Supplier<X>) throws X` |  |

---

### `interface PrimitiveIterator<T, T_CONS>`

- **Extends:** `java.util.Iterator<T>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void forEachRemaining(T_CONS)` |  |

---

### `interface static PrimitiveIterator.OfDouble`

- **Extends:** `java.util.PrimitiveIterator<java.lang.Double,java.util.function.DoubleConsumer>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default void forEachRemaining(java.util.function.DoubleConsumer)` |  |
| `default void forEachRemaining(java.util.function.Consumer<? super java.lang.Double>)` |  |
| `default Double next()` |  |
| `double nextDouble()` |  |

---

### `interface static PrimitiveIterator.OfInt`

- **Extends:** `java.util.PrimitiveIterator<java.lang.Integer,java.util.function.IntConsumer>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default void forEachRemaining(java.util.function.IntConsumer)` |  |
| `default void forEachRemaining(java.util.function.Consumer<? super java.lang.Integer>)` |  |
| `default Integer next()` |  |
| `int nextInt()` |  |

---

### `interface static PrimitiveIterator.OfLong`

- **Extends:** `java.util.PrimitiveIterator<java.lang.Long,java.util.function.LongConsumer>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default void forEachRemaining(java.util.function.LongConsumer)` |  |
| `default void forEachRemaining(java.util.function.Consumer<? super java.lang.Long>)` |  |
| `default Long next()` |  |
| `long nextLong()` |  |

---

### `class PriorityQueue<E>`

- **Extends:** `java.util.AbstractQueue<E>`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PriorityQueue()` |  |
| `PriorityQueue(int)` |  |
| `PriorityQueue(java.util.Comparator<? super E>)` |  |
| `PriorityQueue(int, java.util.Comparator<? super E>)` |  |
| `PriorityQueue(java.util.Collection<? extends E>)` |  |
| `PriorityQueue(java.util.PriorityQueue<? extends E>)` |  |
| `PriorityQueue(java.util.SortedSet<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Comparator<? super E> comparator()` |  |
| `java.util.Iterator<E> iterator()` |  |
| `boolean offer(E)` |  |
| `E peek()` |  |
| `E poll()` |  |
| `int size()` |  |
| `final java.util.Spliterator<E> spliterator()` |  |

---

### `class Properties`

- **Extends:** `java.util.Hashtable<java.lang.Object,java.lang.Object>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Properties()` |  |
| `Properties(java.util.Properties)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Properties defaults` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getProperty(String)` |  |
| `String getProperty(String, String)` |  |
| `void list(java.io.PrintStream)` |  |
| `void list(java.io.PrintWriter)` |  |
| `void load(java.io.Reader) throws java.io.IOException` |  |
| `void load(java.io.InputStream) throws java.io.IOException` |  |
| `void loadFromXML(java.io.InputStream) throws java.io.IOException, java.util.InvalidPropertiesFormatException` |  |
| `java.util.Enumeration<?> propertyNames()` |  |
| `Object setProperty(String, String)` |  |
| `void store(java.io.Writer, String) throws java.io.IOException` |  |
| `void store(java.io.OutputStream, String) throws java.io.IOException` |  |
| `void storeToXML(java.io.OutputStream, String) throws java.io.IOException` |  |
| `void storeToXML(java.io.OutputStream, String, String) throws java.io.IOException` |  |
| `java.util.Set<java.lang.String> stringPropertyNames()` |  |

---

### `class final PropertyPermission`

- **Extends:** `java.security.BasicPermission`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PropertyPermission(String, String)` |  |

---

### `class PropertyResourceBundle`

- **Extends:** `java.util.ResourceBundle`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PropertyResourceBundle(java.io.InputStream) throws java.io.IOException` |  |
| `PropertyResourceBundle(java.io.Reader) throws java.io.IOException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Enumeration<java.lang.String> getKeys()` |  |
| `Object handleGetObject(String)` |  |

---

### `interface Queue<E>`

- **Extends:** `java.util.Collection<E>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `E element()` |  |
| `boolean offer(E)` |  |
| `E remove()` |  |

---

### `class Random`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Random()` |  |
| `Random(long)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.stream.DoubleStream doubles(long)` |  |
| `java.util.stream.DoubleStream doubles()` |  |
| `java.util.stream.DoubleStream doubles(long, double, double)` |  |
| `java.util.stream.DoubleStream doubles(double, double)` |  |
| `java.util.stream.IntStream ints(long)` |  |
| `java.util.stream.IntStream ints()` |  |
| `java.util.stream.IntStream ints(long, int, int)` |  |
| `java.util.stream.IntStream ints(int, int)` |  |
| `java.util.stream.LongStream longs(long)` |  |
| `java.util.stream.LongStream longs()` |  |
| `java.util.stream.LongStream longs(long, long, long)` |  |
| `java.util.stream.LongStream longs(long, long)` |  |
| `int next(int)` |  |
| `boolean nextBoolean()` |  |
| `void nextBytes(byte[])` |  |
| `double nextDouble()` |  |
| `float nextFloat()` |  |
| `double nextGaussian()` |  |
| `int nextInt()` |  |
| `int nextInt(int)` |  |
| `long nextLong()` |  |
| `void setSeed(long)` |  |

---

### `interface RandomAccess`


---

### `class abstract ResourceBundle`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ResourceBundle()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.ResourceBundle parent` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static final void clearCache()` |  |
| `static final void clearCache(ClassLoader)` |  |
| `boolean containsKey(String)` |  |
| `String getBaseBundleName()` |  |
| `static final java.util.ResourceBundle getBundle(String)` |  |
| `static final java.util.ResourceBundle getBundle(String, java.util.ResourceBundle.Control)` |  |
| `static final java.util.ResourceBundle getBundle(String, java.util.Locale)` |  |
| `static final java.util.ResourceBundle getBundle(String, java.util.Locale, java.util.ResourceBundle.Control)` |  |
| `static java.util.ResourceBundle getBundle(String, java.util.Locale, ClassLoader)` |  |
| `static java.util.ResourceBundle getBundle(String, java.util.Locale, ClassLoader, java.util.ResourceBundle.Control)` |  |
| `abstract java.util.Enumeration<java.lang.String> getKeys()` |  |
| `java.util.Locale getLocale()` |  |
| `final Object getObject(String)` |  |
| `final String getString(String)` |  |
| `final String[] getStringArray(String)` |  |
| `abstract Object handleGetObject(String)` |  |
| `java.util.Set<java.lang.String> handleKeySet()` |  |
| `java.util.Set<java.lang.String> keySet()` |  |
| `void setParent(java.util.ResourceBundle)` |  |

---

### `class static ResourceBundle.Control`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ResourceBundle.Control()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.util.List<java.lang.String> FORMAT_CLASS` |  |
| `static final java.util.List<java.lang.String> FORMAT_DEFAULT` |  |
| `static final java.util.List<java.lang.String> FORMAT_PROPERTIES` |  |
| `static final long TTL_DONT_CACHE = -1L` |  |
| `static final long TTL_NO_EXPIRATION_CONTROL = -2L` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.List<java.util.Locale> getCandidateLocales(String, java.util.Locale)` |  |
| `static final java.util.ResourceBundle.Control getControl(java.util.List<java.lang.String>)` |  |
| `java.util.Locale getFallbackLocale(String, java.util.Locale)` |  |
| `java.util.List<java.lang.String> getFormats(String)` |  |
| `static final java.util.ResourceBundle.Control getNoFallbackControl(java.util.List<java.lang.String>)` |  |
| `long getTimeToLive(String, java.util.Locale)` |  |
| `boolean needsReload(String, java.util.Locale, String, ClassLoader, java.util.ResourceBundle, long)` |  |
| `java.util.ResourceBundle newBundle(String, java.util.Locale, String, ClassLoader, boolean) throws java.io.IOException, java.lang.IllegalAccessException, java.lang.InstantiationException` |  |
| `String toBundleName(String, java.util.Locale)` |  |
| `final String toResourceName(String, String)` |  |

---

### `class final Scanner`

- **Implements:** `java.io.Closeable java.util.Iterator<java.lang.String>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Scanner(Readable)` |  |
| `Scanner(java.io.InputStream)` |  |
| `Scanner(java.io.InputStream, String)` |  |
| `Scanner(java.io.File) throws java.io.FileNotFoundException` |  |
| `Scanner(java.io.File, String) throws java.io.FileNotFoundException` |  |
| `Scanner(java.nio.file.Path) throws java.io.IOException` |  |
| `Scanner(java.nio.file.Path, String) throws java.io.IOException` |  |
| `Scanner(String)` |  |
| `Scanner(java.nio.channels.ReadableByteChannel)` |  |
| `Scanner(java.nio.channels.ReadableByteChannel, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `java.util.regex.Pattern delimiter()` |  |
| `String findInLine(String)` |  |
| `String findInLine(java.util.regex.Pattern)` |  |
| `String findWithinHorizon(String, int)` |  |
| `String findWithinHorizon(java.util.regex.Pattern, int)` |  |
| `boolean hasNext()` |  |
| `boolean hasNext(String)` |  |
| `boolean hasNext(java.util.regex.Pattern)` |  |
| `boolean hasNextBigDecimal()` |  |
| `boolean hasNextBigInteger()` |  |
| `boolean hasNextBigInteger(int)` |  |
| `boolean hasNextBoolean()` |  |
| `boolean hasNextByte()` |  |
| `boolean hasNextByte(int)` |  |
| `boolean hasNextDouble()` |  |
| `boolean hasNextFloat()` |  |
| `boolean hasNextInt()` |  |
| `boolean hasNextInt(int)` |  |
| `boolean hasNextLine()` |  |
| `boolean hasNextLong()` |  |
| `boolean hasNextLong(int)` |  |
| `boolean hasNextShort()` |  |
| `boolean hasNextShort(int)` |  |
| `java.io.IOException ioException()` |  |
| `java.util.Locale locale()` |  |
| `java.util.regex.MatchResult match()` |  |
| `String next()` |  |
| `String next(String)` |  |
| `String next(java.util.regex.Pattern)` |  |
| `java.math.BigDecimal nextBigDecimal()` |  |
| `java.math.BigInteger nextBigInteger()` |  |
| `java.math.BigInteger nextBigInteger(int)` |  |
| `boolean nextBoolean()` |  |
| `byte nextByte()` |  |
| `byte nextByte(int)` |  |
| `double nextDouble()` |  |
| `float nextFloat()` |  |
| `int nextInt()` |  |
| `int nextInt(int)` |  |
| `String nextLine()` |  |
| `long nextLong()` |  |
| `long nextLong(int)` |  |
| `short nextShort()` |  |
| `short nextShort(int)` |  |
| `int radix()` |  |
| `java.util.Scanner reset()` |  |
| `java.util.Scanner skip(java.util.regex.Pattern)` |  |
| `java.util.Scanner skip(String)` |  |
| `java.util.Scanner useDelimiter(java.util.regex.Pattern)` |  |
| `java.util.Scanner useDelimiter(String)` |  |
| `java.util.Scanner useLocale(java.util.Locale)` |  |
| `java.util.Scanner useRadix(int)` |  |

---

### `class ServiceConfigurationError`

- **Extends:** `java.lang.Error`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ServiceConfigurationError(String)` |  |
| `ServiceConfigurationError(String, Throwable)` |  |

---

### `class final ServiceLoader<S>`

- **Implements:** `java.lang.Iterable<S>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Iterator<S> iterator()` |  |
| `static <S> java.util.ServiceLoader<S> load(Class<S>, ClassLoader)` |  |
| `static <S> java.util.ServiceLoader<S> load(Class<S>)` |  |
| `static <S> java.util.ServiceLoader<S> loadInstalled(Class<S>)` |  |
| `void reload()` |  |

---

### `interface Set<E>`

- **Extends:** `java.util.Collection<E>`

---

### `class SimpleTimeZone`

- **Extends:** `java.util.TimeZone`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SimpleTimeZone(int, String)` |  |
| `SimpleTimeZone(int, String, int, int, int, int, int, int, int, int)` |  |
| `SimpleTimeZone(int, String, int, int, int, int, int, int, int, int, int)` |  |
| `SimpleTimeZone(int, String, int, int, int, int, int, int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int STANDARD_TIME = 1` |  |
| `static final int UTC_TIME = 2` |  |
| `static final int WALL_TIME = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getOffset(int, int, int, int, int, int)` |  |
| `int getRawOffset()` |  |
| `boolean inDaylightTime(java.util.Date)` |  |
| `void setDSTSavings(int)` |  |
| `void setEndRule(int, int, int, int)` |  |
| `void setEndRule(int, int, int)` |  |
| `void setEndRule(int, int, int, int, boolean)` |  |
| `void setRawOffset(int)` |  |
| `void setStartRule(int, int, int, int)` |  |
| `void setStartRule(int, int, int)` |  |
| `void setStartRule(int, int, int, int, boolean)` |  |
| `void setStartYear(int)` |  |
| `boolean useDaylightTime()` |  |

---

### `interface SortedMap<K, V>`

- **Extends:** `java.util.Map<K,V>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `K firstKey()` |  |
| `K lastKey()` |  |

---

### `interface SortedSet<E>`

- **Extends:** `java.util.Set<E>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Comparator<? super E> comparator()` |  |
| `E first()` |  |
| `java.util.SortedSet<E> headSet(E)` |  |
| `E last()` |  |
| `java.util.SortedSet<E> subSet(E, E)` |  |
| `java.util.SortedSet<E> tailSet(E)` |  |

---

### `interface Spliterator<T>`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CONCURRENT = 4096` |  |
| `static final int DISTINCT = 1` |  |
| `static final int IMMUTABLE = 1024` |  |
| `static final int NONNULL = 256` |  |
| `static final int ORDERED = 16` |  |
| `static final int SIZED = 64` |  |
| `static final int SORTED = 4` |  |
| `static final int SUBSIZED = 16384` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int characteristics()` |  |
| `long estimateSize()` |  |
| `default void forEachRemaining(java.util.function.Consumer<? super T>)` |  |
| `default java.util.Comparator<? super T> getComparator()` |  |
| `default long getExactSizeIfKnown()` |  |
| `default boolean hasCharacteristics(int)` |  |
| `boolean tryAdvance(java.util.function.Consumer<? super T>)` |  |
| `java.util.Spliterator<T> trySplit()` |  |

---

### `interface static Spliterator.OfDouble`

- **Extends:** `java.util.Spliterator.OfPrimitive<java.lang.Double,java.util.function.DoubleConsumer,java.util.Spliterator.OfDouble>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default void forEachRemaining(java.util.function.DoubleConsumer)` |  |
| `default void forEachRemaining(java.util.function.Consumer<? super java.lang.Double>)` |  |
| `boolean tryAdvance(java.util.function.DoubleConsumer)` |  |
| `default boolean tryAdvance(java.util.function.Consumer<? super java.lang.Double>)` |  |
| `java.util.Spliterator.OfDouble trySplit()` |  |

---

### `interface static Spliterator.OfInt`

- **Extends:** `java.util.Spliterator.OfPrimitive<java.lang.Integer,java.util.function.IntConsumer,java.util.Spliterator.OfInt>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default void forEachRemaining(java.util.function.IntConsumer)` |  |
| `default void forEachRemaining(java.util.function.Consumer<? super java.lang.Integer>)` |  |
| `boolean tryAdvance(java.util.function.IntConsumer)` |  |
| `default boolean tryAdvance(java.util.function.Consumer<? super java.lang.Integer>)` |  |
| `java.util.Spliterator.OfInt trySplit()` |  |

---

### `interface static Spliterator.OfLong`

- **Extends:** `java.util.Spliterator.OfPrimitive<java.lang.Long,java.util.function.LongConsumer,java.util.Spliterator.OfLong>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default void forEachRemaining(java.util.function.LongConsumer)` |  |
| `default void forEachRemaining(java.util.function.Consumer<? super java.lang.Long>)` |  |
| `boolean tryAdvance(java.util.function.LongConsumer)` |  |
| `default boolean tryAdvance(java.util.function.Consumer<? super java.lang.Long>)` |  |
| `java.util.Spliterator.OfLong trySplit()` |  |

---

### `interface static Spliterator.OfPrimitive<T, T_CONS, T_SPLITR`

- **Extends:** `java.util.Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends java.util.Spliterator<T>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default void forEachRemaining(T_CONS)` |  |
| `boolean tryAdvance(T_CONS)` |  |
| `T_SPLITR trySplit()` |  |

---

### `class final Spliterators`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Spliterators.AbstractDoubleSpliterator(long, int)` |  |
| `Spliterators.AbstractIntSpliterator(long, int)` |  |
| `Spliterators.AbstractLongSpliterator(long, int)` |  |
| `Spliterators.AbstractSpliterator(long, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.Spliterator.OfDouble emptyDoubleSpliterator()` |  |
| `static java.util.Spliterator.OfInt emptyIntSpliterator()` |  |
| `static java.util.Spliterator.OfLong emptyLongSpliterator()` |  |
| `static <T> java.util.Spliterator<T> emptySpliterator()` |  |
| `static <T> java.util.Iterator<T> iterator(java.util.Spliterator<? extends T>)` |  |
| `static java.util.PrimitiveIterator.OfInt iterator(java.util.Spliterator.OfInt)` |  |
| `static java.util.PrimitiveIterator.OfLong iterator(java.util.Spliterator.OfLong)` |  |
| `static java.util.PrimitiveIterator.OfDouble iterator(java.util.Spliterator.OfDouble)` |  |
| `static <T> java.util.Spliterator<T> spliterator(Object[], int)` |  |
| `static <T> java.util.Spliterator<T> spliterator(Object[], int, int, int)` |  |
| `static java.util.Spliterator.OfInt spliterator(int[], int)` |  |
| `static java.util.Spliterator.OfInt spliterator(int[], int, int, int)` |  |
| `static java.util.Spliterator.OfLong spliterator(long[], int)` |  |
| `static java.util.Spliterator.OfLong spliterator(long[], int, int, int)` |  |
| `static java.util.Spliterator.OfDouble spliterator(double[], int)` |  |
| `static java.util.Spliterator.OfDouble spliterator(double[], int, int, int)` |  |
| `static <T> java.util.Spliterator<T> spliterator(java.util.Collection<? extends T>, int)` |  |
| `static <T> java.util.Spliterator<T> spliterator(java.util.Iterator<? extends T>, long, int)` |  |
| `static java.util.Spliterator.OfInt spliterator(java.util.PrimitiveIterator.OfInt, long, int)` |  |
| `static java.util.Spliterator.OfLong spliterator(java.util.PrimitiveIterator.OfLong, long, int)` |  |
| `static java.util.Spliterator.OfDouble spliterator(java.util.PrimitiveIterator.OfDouble, long, int)` |  |
| `static <T> java.util.Spliterator<T> spliteratorUnknownSize(java.util.Iterator<? extends T>, int)` |  |
| `static java.util.Spliterator.OfInt spliteratorUnknownSize(java.util.PrimitiveIterator.OfInt, int)` |  |
| `static java.util.Spliterator.OfLong spliteratorUnknownSize(java.util.PrimitiveIterator.OfLong, int)` |  |
| `static java.util.Spliterator.OfDouble spliteratorUnknownSize(java.util.PrimitiveIterator.OfDouble, int)` |  |
| `int characteristics()` |  |
| `long estimateSize()` |  |
| `java.util.Spliterator.OfDouble trySplit()` |  |
| `int characteristics()` |  |
| `long estimateSize()` |  |
| `java.util.Spliterator.OfInt trySplit()` |  |
| `int characteristics()` |  |
| `long estimateSize()` |  |
| `java.util.Spliterator.OfLong trySplit()` |  |
| `int characteristics()` |  |
| `long estimateSize()` |  |
| `java.util.Spliterator<T> trySplit()` |  |

---

### `class final SplittableRandom`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SplittableRandom(long)` |  |
| `SplittableRandom()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.stream.DoubleStream doubles(long)` |  |
| `java.util.stream.DoubleStream doubles()` |  |
| `java.util.stream.DoubleStream doubles(long, double, double)` |  |
| `java.util.stream.DoubleStream doubles(double, double)` |  |
| `java.util.stream.IntStream ints(long)` |  |
| `java.util.stream.IntStream ints()` |  |
| `java.util.stream.IntStream ints(long, int, int)` |  |
| `java.util.stream.IntStream ints(int, int)` |  |
| `java.util.stream.LongStream longs(long)` |  |
| `java.util.stream.LongStream longs()` |  |
| `java.util.stream.LongStream longs(long, long, long)` |  |
| `java.util.stream.LongStream longs(long, long)` |  |
| `boolean nextBoolean()` |  |
| `double nextDouble()` |  |
| `double nextDouble(double)` |  |
| `double nextDouble(double, double)` |  |
| `int nextInt()` |  |
| `int nextInt(int)` |  |
| `int nextInt(int, int)` |  |
| `long nextLong()` |  |
| `long nextLong(long)` |  |
| `long nextLong(long, long)` |  |
| `java.util.SplittableRandom split()` |  |

---

### `class Stack<E>`

- **Extends:** `java.util.Vector<E>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Stack()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean empty()` |  |
| `E peek()` |  |
| `E pop()` |  |
| `E push(E)` |  |
| `int search(Object)` |  |

---

### `class final StringJoiner`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StringJoiner(CharSequence)` |  |
| `StringJoiner(CharSequence, CharSequence, CharSequence)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.StringJoiner add(CharSequence)` |  |
| `int length()` |  |
| `java.util.StringJoiner merge(java.util.StringJoiner)` |  |
| `java.util.StringJoiner setEmptyValue(CharSequence)` |  |

---

### `class StringTokenizer`

- **Implements:** `java.util.Enumeration<java.lang.Object>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StringTokenizer(String, String, boolean)` |  |
| `StringTokenizer(String, String)` |  |
| `StringTokenizer(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int countTokens()` |  |
| `boolean hasMoreElements()` |  |
| `boolean hasMoreTokens()` |  |
| `Object nextElement()` |  |
| `String nextToken()` |  |
| `String nextToken(String)` |  |

---

### `class abstract TimeZone`

- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TimeZone()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int LONG = 1` |  |
| `static final int SHORT = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `static String[] getAvailableIDs(int)` |  |
| `static String[] getAvailableIDs()` |  |
| `int getDSTSavings()` |  |
| `static java.util.TimeZone getDefault()` |  |
| `final String getDisplayName()` |  |
| `final String getDisplayName(java.util.Locale)` |  |
| `final String getDisplayName(boolean, int)` |  |
| `String getDisplayName(boolean, int, java.util.Locale)` |  |
| `String getID()` |  |
| `abstract int getOffset(int, int, int, int, int, int)` |  |
| `int getOffset(long)` |  |
| `abstract int getRawOffset()` |  |
| `static java.util.TimeZone getTimeZone(String)` |  |
| `static java.util.TimeZone getTimeZone(java.time.ZoneId)` |  |
| `boolean hasSameRules(java.util.TimeZone)` |  |
| `abstract boolean inDaylightTime(java.util.Date)` |  |
| `boolean observesDaylightTime()` |  |
| `static void setDefault(java.util.TimeZone)` |  |
| `void setID(String)` |  |
| `abstract void setRawOffset(int)` |  |
| `java.time.ZoneId toZoneId()` |  |
| `abstract boolean useDaylightTime()` |  |

---

### `class Timer`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Timer()` |  |
| `Timer(boolean)` |  |
| `Timer(String)` |  |
| `Timer(String, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void cancel()` |  |
| `int purge()` |  |
| `void schedule(java.util.TimerTask, long)` |  |
| `void schedule(java.util.TimerTask, java.util.Date)` |  |
| `void schedule(java.util.TimerTask, long, long)` |  |
| `void schedule(java.util.TimerTask, java.util.Date, long)` |  |
| `void scheduleAtFixedRate(java.util.TimerTask, long, long)` |  |
| `void scheduleAtFixedRate(java.util.TimerTask, java.util.Date, long)` |  |

---

### `class abstract TimerTask`

- **Implements:** `java.lang.Runnable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TimerTask()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean cancel()` |  |
| `long scheduledExecutionTime()` |  |

---

### `class TooManyListenersException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TooManyListenersException()` |  |
| `TooManyListenersException(String)` |  |

---

### `class TreeMap<K, V>`

- **Extends:** `java.util.AbstractMap<K,V>`
- **Implements:** `java.lang.Cloneable java.util.NavigableMap<K,V> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TreeMap()` |  |
| `TreeMap(@Nullable java.util.Comparator<? super K>)` |  |
| `TreeMap(@NonNull java.util.Map<? extends K,? extends V>)` |  |
| `TreeMap(@NonNull java.util.SortedMap<K,? extends V>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `K firstKey()` |  |
| `K lastKey()` |  |

---

### `class TreeSet<E>`

- **Extends:** `java.util.AbstractSet<E>`
- **Implements:** `java.lang.Cloneable java.util.NavigableSet<E> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TreeSet()` |  |
| `TreeSet(java.util.Comparator<? super E>)` |  |
| `TreeSet(java.util.Collection<? extends E>)` |  |
| `TreeSet(java.util.SortedSet<E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `E ceiling(E)` |  |
| `Object clone()` |  |
| `java.util.Comparator<? super E> comparator()` |  |
| `java.util.Iterator<E> descendingIterator()` |  |
| `java.util.NavigableSet<E> descendingSet()` |  |
| `E first()` |  |
| `E floor(E)` |  |
| `java.util.NavigableSet<E> headSet(E, boolean)` |  |
| `java.util.SortedSet<E> headSet(E)` |  |
| `E higher(E)` |  |
| `java.util.Iterator<E> iterator()` |  |
| `E last()` |  |
| `E lower(E)` |  |
| `E pollFirst()` |  |
| `E pollLast()` |  |
| `int size()` |  |
| `java.util.NavigableSet<E> subSet(E, boolean, E, boolean)` |  |
| `java.util.SortedSet<E> subSet(E, E)` |  |
| `java.util.NavigableSet<E> tailSet(E, boolean)` |  |
| `java.util.SortedSet<E> tailSet(E)` |  |

---

### `class final UUID`

- **Implements:** `java.lang.Comparable<java.util.UUID> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UUID(long, long)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int clockSequence()` |  |
| `int compareTo(java.util.UUID)` |  |
| `static java.util.UUID fromString(String)` |  |
| `long getLeastSignificantBits()` |  |
| `long getMostSignificantBits()` |  |
| `static java.util.UUID nameUUIDFromBytes(byte[])` |  |
| `long node()` |  |
| `static java.util.UUID randomUUID()` |  |
| `long timestamp()` |  |
| `int variant()` |  |
| `int version()` |  |

---

### `class UnknownFormatConversionException`

- **Extends:** `java.util.IllegalFormatException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnknownFormatConversionException(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getConversion()` |  |

---

### `class UnknownFormatFlagsException`

- **Extends:** `java.util.IllegalFormatException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnknownFormatFlagsException(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getFlags()` |  |

---

### `class Vector<E>`

- **Extends:** `java.util.AbstractList<E>`
- **Implements:** `java.lang.Cloneable java.util.List<E> java.util.RandomAccess java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Vector(int, int)` |  |
| `Vector(int)` |  |
| `Vector()` |  |
| `Vector(@NonNull java.util.Collection<? extends E>)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int capacityIncrement` |  |
| `int elementCount` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addElement(E)` |  |
| `int capacity()` |  |
| `void copyInto(@NonNull Object[])` |  |
| `E elementAt(int)` |  |
| `void ensureCapacity(int)` |  |
| `E firstElement()` |  |
| `void forEach(@NonNull java.util.function.Consumer<? super E>)` |  |
| `E get(int)` |  |
| `int indexOf(@Nullable Object, int)` |  |
| `void insertElementAt(E, int)` |  |
| `E lastElement()` |  |
| `int lastIndexOf(@Nullable Object, int)` |  |
| `void removeAllElements()` |  |
| `boolean removeElement(@Nullable Object)` |  |
| `void removeElementAt(int)` |  |
| `void setElementAt(E, int)` |  |
| `void setSize(int)` |  |
| `int size()` |  |
| `void trimToSize()` |  |

---

### `class WeakHashMap<K, V>`

- **Extends:** `java.util.AbstractMap<K,V>`
- **Implements:** `java.util.Map<K,V>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WeakHashMap(int, float)` |  |
| `WeakHashMap(int)` |  |
| `WeakHashMap()` |  |
| `WeakHashMap(@NonNull java.util.Map<? extends K,? extends V>)` |  |

---

## Package: `java.util.concurrent`

### `class abstract AbstractExecutorService`

- **Implements:** `java.util.concurrent.ExecutorService`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractExecutorService()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `<T> java.util.List<java.util.concurrent.Future<T>> invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>>) throws java.lang.InterruptedException` |  |
| `<T> java.util.List<java.util.concurrent.Future<T>> invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>>, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `<T> T invokeAny(java.util.Collection<? extends java.util.concurrent.Callable<T>>) throws java.util.concurrent.ExecutionException, java.lang.InterruptedException` |  |
| `<T> T invokeAny(java.util.Collection<? extends java.util.concurrent.Callable<T>>, long, java.util.concurrent.TimeUnit) throws java.util.concurrent.ExecutionException, java.lang.InterruptedException, java.util.concurrent.TimeoutException` |  |
| `<T> java.util.concurrent.RunnableFuture<T> newTaskFor(Runnable, T)` |  |
| `<T> java.util.concurrent.RunnableFuture<T> newTaskFor(java.util.concurrent.Callable<T>)` |  |
| `java.util.concurrent.Future<?> submit(Runnable)` |  |
| `<T> java.util.concurrent.Future<T> submit(Runnable, T)` |  |
| `<T> java.util.concurrent.Future<T> submit(java.util.concurrent.Callable<T>)` |  |

---

### `class ArrayBlockingQueue<E>`

- **Extends:** `java.util.AbstractQueue<E>`
- **Implements:** `java.util.concurrent.BlockingQueue<E> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ArrayBlockingQueue(int)` |  |
| `ArrayBlockingQueue(int, boolean)` |  |
| `ArrayBlockingQueue(int, boolean, java.util.Collection<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int drainTo(java.util.Collection<? super E>)` |  |
| `int drainTo(java.util.Collection<? super E>, int)` |  |
| `java.util.Iterator<E> iterator()` |  |
| `boolean offer(E)` |  |
| `boolean offer(E, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `E peek()` |  |
| `E poll()` |  |
| `E poll(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `void put(E) throws java.lang.InterruptedException` |  |
| `int remainingCapacity()` |  |
| `int size()` |  |
| `E take() throws java.lang.InterruptedException` |  |

---

### `interface BlockingDeque<E>`

- **Extends:** `java.util.concurrent.BlockingQueue<E> java.util.Deque<E>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean offerFirst(E, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `boolean offerLast(E, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `E pollFirst(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `E pollLast(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `void putFirst(E) throws java.lang.InterruptedException` |  |
| `void putLast(E) throws java.lang.InterruptedException` |  |
| `E takeFirst() throws java.lang.InterruptedException` |  |
| `E takeLast() throws java.lang.InterruptedException` |  |

---

### `interface BlockingQueue<E>`

- **Extends:** `java.util.Queue<E>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int drainTo(java.util.Collection<? super E>)` |  |
| `int drainTo(java.util.Collection<? super E>, int)` |  |
| `boolean offer(E, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `E poll(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `void put(E) throws java.lang.InterruptedException` |  |
| `int remainingCapacity()` |  |
| `E take() throws java.lang.InterruptedException` |  |

---

### `class BrokenBarrierException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BrokenBarrierException()` |  |
| `BrokenBarrierException(String)` |  |

---

### `interface Callable<V>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `V call() throws java.lang.Exception` |  |

---

### `class CancellationException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CancellationException()` |  |
| `CancellationException(String)` |  |

---

### `class CompletableFuture<T>`

- **Implements:** `java.util.concurrent.CompletionStage<T> java.util.concurrent.Future<T>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CompletableFuture()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.concurrent.CompletableFuture<java.lang.Void> acceptEither(java.util.concurrent.CompletionStage<? extends T>, java.util.function.Consumer<? super T>)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> acceptEitherAsync(java.util.concurrent.CompletionStage<? extends T>, java.util.function.Consumer<? super T>)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> acceptEitherAsync(java.util.concurrent.CompletionStage<? extends T>, java.util.function.Consumer<? super T>, java.util.concurrent.Executor)` |  |
| `static java.util.concurrent.CompletableFuture<java.lang.Void> allOf(java.util.concurrent.CompletableFuture<?>...)` |  |
| `static java.util.concurrent.CompletableFuture<java.lang.Object> anyOf(java.util.concurrent.CompletableFuture<?>...)` |  |
| `<U> java.util.concurrent.CompletableFuture<U> applyToEither(java.util.concurrent.CompletionStage<? extends T>, java.util.function.Function<? super T,U>)` |  |
| `<U> java.util.concurrent.CompletableFuture<U> applyToEitherAsync(java.util.concurrent.CompletionStage<? extends T>, java.util.function.Function<? super T,U>)` |  |
| `<U> java.util.concurrent.CompletableFuture<U> applyToEitherAsync(java.util.concurrent.CompletionStage<? extends T>, java.util.function.Function<? super T,U>, java.util.concurrent.Executor)` |  |
| `boolean cancel(boolean)` |  |
| `boolean complete(T)` |  |
| `boolean completeExceptionally(Throwable)` |  |
| `static <U> java.util.concurrent.CompletableFuture<U> completedFuture(U)` |  |
| `java.util.concurrent.CompletableFuture<T> exceptionally(java.util.function.Function<java.lang.Throwable,? extends T>)` |  |
| `T get() throws java.util.concurrent.ExecutionException, java.lang.InterruptedException` |  |
| `T get(long, java.util.concurrent.TimeUnit) throws java.util.concurrent.ExecutionException, java.lang.InterruptedException, java.util.concurrent.TimeoutException` |  |
| `T getNow(T)` |  |
| `int getNumberOfDependents()` |  |
| `<U> java.util.concurrent.CompletableFuture<U> handle(java.util.function.BiFunction<? super T,java.lang.Throwable,? extends U>)` |  |
| `<U> java.util.concurrent.CompletableFuture<U> handleAsync(java.util.function.BiFunction<? super T,java.lang.Throwable,? extends U>)` |  |
| `<U> java.util.concurrent.CompletableFuture<U> handleAsync(java.util.function.BiFunction<? super T,java.lang.Throwable,? extends U>, java.util.concurrent.Executor)` |  |
| `boolean isCancelled()` |  |
| `boolean isCompletedExceptionally()` |  |
| `boolean isDone()` |  |
| `T join()` |  |
| `void obtrudeException(Throwable)` |  |
| `void obtrudeValue(T)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> runAfterBoth(java.util.concurrent.CompletionStage<?>, Runnable)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> runAfterBothAsync(java.util.concurrent.CompletionStage<?>, Runnable)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> runAfterBothAsync(java.util.concurrent.CompletionStage<?>, Runnable, java.util.concurrent.Executor)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> runAfterEither(java.util.concurrent.CompletionStage<?>, Runnable)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> runAfterEitherAsync(java.util.concurrent.CompletionStage<?>, Runnable)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> runAfterEitherAsync(java.util.concurrent.CompletionStage<?>, Runnable, java.util.concurrent.Executor)` |  |
| `static java.util.concurrent.CompletableFuture<java.lang.Void> runAsync(Runnable)` |  |
| `static java.util.concurrent.CompletableFuture<java.lang.Void> runAsync(Runnable, java.util.concurrent.Executor)` |  |
| `static <U> java.util.concurrent.CompletableFuture<U> supplyAsync(java.util.function.Supplier<U>)` |  |
| `static <U> java.util.concurrent.CompletableFuture<U> supplyAsync(java.util.function.Supplier<U>, java.util.concurrent.Executor)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> thenAccept(java.util.function.Consumer<? super T>)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> thenAcceptAsync(java.util.function.Consumer<? super T>)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> thenAcceptAsync(java.util.function.Consumer<? super T>, java.util.concurrent.Executor)` |  |
| `<U> java.util.concurrent.CompletableFuture<java.lang.Void> thenAcceptBoth(java.util.concurrent.CompletionStage<? extends U>, java.util.function.BiConsumer<? super T,? super U>)` |  |
| `<U> java.util.concurrent.CompletableFuture<java.lang.Void> thenAcceptBothAsync(java.util.concurrent.CompletionStage<? extends U>, java.util.function.BiConsumer<? super T,? super U>)` |  |
| `<U> java.util.concurrent.CompletableFuture<java.lang.Void> thenAcceptBothAsync(java.util.concurrent.CompletionStage<? extends U>, java.util.function.BiConsumer<? super T,? super U>, java.util.concurrent.Executor)` |  |
| `<U> java.util.concurrent.CompletableFuture<U> thenApply(java.util.function.Function<? super T,? extends U>)` |  |
| `<U> java.util.concurrent.CompletableFuture<U> thenApplyAsync(java.util.function.Function<? super T,? extends U>)` |  |
| `<U> java.util.concurrent.CompletableFuture<U> thenApplyAsync(java.util.function.Function<? super T,? extends U>, java.util.concurrent.Executor)` |  |
| `<U, V> java.util.concurrent.CompletableFuture<V> thenCombine(java.util.concurrent.CompletionStage<? extends U>, java.util.function.BiFunction<? super T,? super U,? extends V>)` |  |
| `<U, V> java.util.concurrent.CompletableFuture<V> thenCombineAsync(java.util.concurrent.CompletionStage<? extends U>, java.util.function.BiFunction<? super T,? super U,? extends V>)` |  |
| `<U, V> java.util.concurrent.CompletableFuture<V> thenCombineAsync(java.util.concurrent.CompletionStage<? extends U>, java.util.function.BiFunction<? super T,? super U,? extends V>, java.util.concurrent.Executor)` |  |
| `<U> java.util.concurrent.CompletableFuture<U> thenCompose(java.util.function.Function<? super T,? extends java.util.concurrent.CompletionStage<U>>)` |  |
| `<U> java.util.concurrent.CompletableFuture<U> thenComposeAsync(java.util.function.Function<? super T,? extends java.util.concurrent.CompletionStage<U>>)` |  |
| `<U> java.util.concurrent.CompletableFuture<U> thenComposeAsync(java.util.function.Function<? super T,? extends java.util.concurrent.CompletionStage<U>>, java.util.concurrent.Executor)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> thenRun(Runnable)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> thenRunAsync(Runnable)` |  |
| `java.util.concurrent.CompletableFuture<java.lang.Void> thenRunAsync(Runnable, java.util.concurrent.Executor)` |  |
| `java.util.concurrent.CompletableFuture<T> toCompletableFuture()` |  |
| `java.util.concurrent.CompletableFuture<T> whenComplete(java.util.function.BiConsumer<? super T,? super java.lang.Throwable>)` |  |
| `java.util.concurrent.CompletableFuture<T> whenCompleteAsync(java.util.function.BiConsumer<? super T,? super java.lang.Throwable>)` |  |
| `java.util.concurrent.CompletableFuture<T> whenCompleteAsync(java.util.function.BiConsumer<? super T,? super java.lang.Throwable>, java.util.concurrent.Executor)` |  |

---

### `interface static CompletableFuture.AsynchronousCompletionTask`


---

### `class CompletionException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CompletionException()` |  |
| `CompletionException(String)` |  |
| `CompletionException(String, Throwable)` |  |
| `CompletionException(Throwable)` |  |

---

### `interface CompletionService<V>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.concurrent.Future<V> poll()` |  |
| `java.util.concurrent.Future<V> poll(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `java.util.concurrent.Future<V> submit(java.util.concurrent.Callable<V>)` |  |
| `java.util.concurrent.Future<V> submit(Runnable, V)` |  |
| `java.util.concurrent.Future<V> take() throws java.lang.InterruptedException` |  |

---

### `interface CompletionStage<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.concurrent.CompletionStage<java.lang.Void> acceptEither(java.util.concurrent.CompletionStage<? extends T>, java.util.function.Consumer<? super T>)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> acceptEitherAsync(java.util.concurrent.CompletionStage<? extends T>, java.util.function.Consumer<? super T>)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> acceptEitherAsync(java.util.concurrent.CompletionStage<? extends T>, java.util.function.Consumer<? super T>, java.util.concurrent.Executor)` |  |
| `<U> java.util.concurrent.CompletionStage<U> applyToEither(java.util.concurrent.CompletionStage<? extends T>, java.util.function.Function<? super T,U>)` |  |
| `<U> java.util.concurrent.CompletionStage<U> applyToEitherAsync(java.util.concurrent.CompletionStage<? extends T>, java.util.function.Function<? super T,U>)` |  |
| `<U> java.util.concurrent.CompletionStage<U> applyToEitherAsync(java.util.concurrent.CompletionStage<? extends T>, java.util.function.Function<? super T,U>, java.util.concurrent.Executor)` |  |
| `java.util.concurrent.CompletionStage<T> exceptionally(java.util.function.Function<java.lang.Throwable,? extends T>)` |  |
| `<U> java.util.concurrent.CompletionStage<U> handle(java.util.function.BiFunction<? super T,java.lang.Throwable,? extends U>)` |  |
| `<U> java.util.concurrent.CompletionStage<U> handleAsync(java.util.function.BiFunction<? super T,java.lang.Throwable,? extends U>)` |  |
| `<U> java.util.concurrent.CompletionStage<U> handleAsync(java.util.function.BiFunction<? super T,java.lang.Throwable,? extends U>, java.util.concurrent.Executor)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> runAfterBoth(java.util.concurrent.CompletionStage<?>, Runnable)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> runAfterBothAsync(java.util.concurrent.CompletionStage<?>, Runnable)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> runAfterBothAsync(java.util.concurrent.CompletionStage<?>, Runnable, java.util.concurrent.Executor)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> runAfterEither(java.util.concurrent.CompletionStage<?>, Runnable)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> runAfterEitherAsync(java.util.concurrent.CompletionStage<?>, Runnable)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> runAfterEitherAsync(java.util.concurrent.CompletionStage<?>, Runnable, java.util.concurrent.Executor)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> thenAccept(java.util.function.Consumer<? super T>)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> thenAcceptAsync(java.util.function.Consumer<? super T>)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> thenAcceptAsync(java.util.function.Consumer<? super T>, java.util.concurrent.Executor)` |  |
| `<U> java.util.concurrent.CompletionStage<java.lang.Void> thenAcceptBoth(java.util.concurrent.CompletionStage<? extends U>, java.util.function.BiConsumer<? super T,? super U>)` |  |
| `<U> java.util.concurrent.CompletionStage<java.lang.Void> thenAcceptBothAsync(java.util.concurrent.CompletionStage<? extends U>, java.util.function.BiConsumer<? super T,? super U>)` |  |
| `<U> java.util.concurrent.CompletionStage<java.lang.Void> thenAcceptBothAsync(java.util.concurrent.CompletionStage<? extends U>, java.util.function.BiConsumer<? super T,? super U>, java.util.concurrent.Executor)` |  |
| `<U> java.util.concurrent.CompletionStage<U> thenApply(java.util.function.Function<? super T,? extends U>)` |  |
| `<U> java.util.concurrent.CompletionStage<U> thenApplyAsync(java.util.function.Function<? super T,? extends U>)` |  |
| `<U> java.util.concurrent.CompletionStage<U> thenApplyAsync(java.util.function.Function<? super T,? extends U>, java.util.concurrent.Executor)` |  |
| `<U, V> java.util.concurrent.CompletionStage<V> thenCombine(java.util.concurrent.CompletionStage<? extends U>, java.util.function.BiFunction<? super T,? super U,? extends V>)` |  |
| `<U, V> java.util.concurrent.CompletionStage<V> thenCombineAsync(java.util.concurrent.CompletionStage<? extends U>, java.util.function.BiFunction<? super T,? super U,? extends V>)` |  |
| `<U, V> java.util.concurrent.CompletionStage<V> thenCombineAsync(java.util.concurrent.CompletionStage<? extends U>, java.util.function.BiFunction<? super T,? super U,? extends V>, java.util.concurrent.Executor)` |  |
| `<U> java.util.concurrent.CompletionStage<U> thenCompose(java.util.function.Function<? super T,? extends java.util.concurrent.CompletionStage<U>>)` |  |
| `<U> java.util.concurrent.CompletionStage<U> thenComposeAsync(java.util.function.Function<? super T,? extends java.util.concurrent.CompletionStage<U>>)` |  |
| `<U> java.util.concurrent.CompletionStage<U> thenComposeAsync(java.util.function.Function<? super T,? extends java.util.concurrent.CompletionStage<U>>, java.util.concurrent.Executor)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> thenRun(Runnable)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> thenRunAsync(Runnable)` |  |
| `java.util.concurrent.CompletionStage<java.lang.Void> thenRunAsync(Runnable, java.util.concurrent.Executor)` |  |
| `java.util.concurrent.CompletableFuture<T> toCompletableFuture()` |  |
| `java.util.concurrent.CompletionStage<T> whenComplete(java.util.function.BiConsumer<? super T,? super java.lang.Throwable>)` |  |
| `java.util.concurrent.CompletionStage<T> whenCompleteAsync(java.util.function.BiConsumer<? super T,? super java.lang.Throwable>)` |  |
| `java.util.concurrent.CompletionStage<T> whenCompleteAsync(java.util.function.BiConsumer<? super T,? super java.lang.Throwable>, java.util.concurrent.Executor)` |  |

---

### `class ConcurrentHashMap<K, V>`

- **Extends:** `java.util.AbstractMap<K,V>`
- **Implements:** `java.util.concurrent.ConcurrentMap<K,V> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConcurrentHashMap()` |  |
| `ConcurrentHashMap(int)` |  |
| `ConcurrentHashMap(@NonNull java.util.Map<? extends K,? extends V>)` |  |
| `ConcurrentHashMap(int, float)` |  |
| `ConcurrentHashMap(int, float, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean contains(@NonNull Object)` |  |
| `void forEach(long, @NonNull java.util.function.BiConsumer<? super K,? super V>)` |  |
| `<U> void forEach(long, @NonNull java.util.function.BiFunction<? super K,? super V,? extends U>, @NonNull java.util.function.Consumer<? super U>)` |  |
| `void forEachEntry(long, @NonNull java.util.function.Consumer<? super java.util.Map.Entry<K,V>>)` |  |
| `<U> void forEachEntry(long, @NonNull java.util.function.Function<java.util.Map.Entry<K,V>,? extends U>, @NonNull java.util.function.Consumer<? super U>)` |  |
| `void forEachKey(long, @NonNull java.util.function.Consumer<? super K>)` |  |
| `<U> void forEachKey(long, @NonNull java.util.function.Function<? super K,? extends U>, @NonNull java.util.function.Consumer<? super U>)` |  |
| `void forEachValue(long, @NonNull java.util.function.Consumer<? super V>)` |  |
| `<U> void forEachValue(long, @NonNull java.util.function.Function<? super V,? extends U>, @NonNull java.util.function.Consumer<? super U>)` |  |
| `long mappingCount()` |  |
| `double reduceEntriesToDouble(long, @NonNull java.util.function.ToDoubleFunction<java.util.Map.Entry<K,V>>, double, @NonNull java.util.function.DoubleBinaryOperator)` |  |
| `int reduceEntriesToInt(long, @NonNull java.util.function.ToIntFunction<java.util.Map.Entry<K,V>>, int, @NonNull java.util.function.IntBinaryOperator)` |  |
| `long reduceEntriesToLong(long, @NonNull java.util.function.ToLongFunction<java.util.Map.Entry<K,V>>, long, @NonNull java.util.function.LongBinaryOperator)` |  |
| `double reduceKeysToDouble(long, @NonNull java.util.function.ToDoubleFunction<? super K>, double, @NonNull java.util.function.DoubleBinaryOperator)` |  |
| `int reduceKeysToInt(long, @NonNull java.util.function.ToIntFunction<? super K>, int, @NonNull java.util.function.IntBinaryOperator)` |  |
| `long reduceKeysToLong(long, @NonNull java.util.function.ToLongFunction<? super K>, long, @NonNull java.util.function.LongBinaryOperator)` |  |
| `double reduceToDouble(long, @NonNull java.util.function.ToDoubleBiFunction<? super K,? super V>, double, @NonNull java.util.function.DoubleBinaryOperator)` |  |
| `int reduceToInt(long, @NonNull java.util.function.ToIntBiFunction<? super K,? super V>, int, @NonNull java.util.function.IntBinaryOperator)` |  |
| `long reduceToLong(long, @NonNull java.util.function.ToLongBiFunction<? super K,? super V>, long, @NonNull java.util.function.LongBinaryOperator)` |  |
| `double reduceValuesToDouble(long, @NonNull java.util.function.ToDoubleFunction<? super V>, double, @NonNull java.util.function.DoubleBinaryOperator)` |  |
| `int reduceValuesToInt(long, @NonNull java.util.function.ToIntFunction<? super V>, int, @NonNull java.util.function.IntBinaryOperator)` |  |
| `long reduceValuesToLong(long, @NonNull java.util.function.ToLongFunction<? super V>, long, @NonNull java.util.function.LongBinaryOperator)` |  |

---

### `class static ConcurrentHashMap.KeySetView<K, V>`

- **Implements:** `java.util.Collection<K> java.io.Serializable java.util.Set<K>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean add(@NonNull K)` |  |
| `boolean addAll(@NonNull java.util.Collection<? extends K>)` |  |
| `final void clear()` |  |
| `boolean contains(@NonNull Object)` |  |
| `final boolean containsAll(@NonNull java.util.Collection<?>)` |  |
| `void forEach(@NonNull java.util.function.Consumer<? super K>)` |  |
| `final boolean isEmpty()` |  |
| `boolean remove(@NonNull Object)` |  |
| `final boolean removeAll(@NonNull java.util.Collection<?>)` |  |
| `final boolean retainAll(@NonNull java.util.Collection<?>)` |  |
| `final int size()` |  |

---

### `class ConcurrentLinkedDeque<E>`

- **Extends:** `java.util.AbstractCollection<E>`
- **Implements:** `java.util.Deque<E> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConcurrentLinkedDeque()` |  |
| `ConcurrentLinkedDeque(java.util.Collection<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addFirst(E)` |  |
| `void addLast(E)` |  |
| `java.util.Iterator<E> descendingIterator()` |  |
| `E element()` |  |
| `E getFirst()` |  |
| `E getLast()` |  |
| `java.util.Iterator<E> iterator()` |  |
| `boolean offer(E)` |  |
| `boolean offerFirst(E)` |  |
| `boolean offerLast(E)` |  |
| `E peek()` |  |
| `E peekFirst()` |  |
| `E peekLast()` |  |
| `E poll()` |  |
| `E pollFirst()` |  |
| `E pollLast()` |  |
| `E pop()` |  |
| `void push(E)` |  |
| `E remove()` |  |
| `E removeFirst()` |  |
| `boolean removeFirstOccurrence(Object)` |  |
| `E removeLast()` |  |
| `boolean removeLastOccurrence(Object)` |  |
| `int size()` |  |

---

### `class ConcurrentLinkedQueue<E>`

- **Extends:** `java.util.AbstractQueue<E>`
- **Implements:** `java.util.Queue<E> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConcurrentLinkedQueue()` |  |
| `ConcurrentLinkedQueue(java.util.Collection<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Iterator<E> iterator()` |  |
| `boolean offer(E)` |  |
| `E peek()` |  |
| `E poll()` |  |
| `int size()` |  |

---

### `interface ConcurrentMap<K, V>`

- **Extends:** `java.util.Map<K,V>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `V putIfAbsent(K, V)` |  |
| `boolean remove(Object, Object)` |  |
| `boolean replace(K, V, V)` |  |
| `V replace(K, V)` |  |

---

### `interface ConcurrentNavigableMap<K, V>`

- **Extends:** `java.util.concurrent.ConcurrentMap<K,V> java.util.NavigableMap<K,V>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.concurrent.ConcurrentNavigableMap<K,V> descendingMap()` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> headMap(K, boolean)` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> headMap(K)` |  |
| `java.util.NavigableSet<K> keySet()` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> subMap(K, boolean, K, boolean)` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> subMap(K, K)` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> tailMap(K, boolean)` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> tailMap(K)` |  |

---

### `class ConcurrentSkipListMap<K, V>`

- **Extends:** `java.util.AbstractMap<K,V>`
- **Implements:** `java.lang.Cloneable java.util.concurrent.ConcurrentNavigableMap<K,V> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConcurrentSkipListMap()` |  |
| `ConcurrentSkipListMap(java.util.Comparator<? super K>)` |  |
| `ConcurrentSkipListMap(java.util.Map<? extends K,? extends V>)` |  |
| `ConcurrentSkipListMap(java.util.SortedMap<K,? extends V>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Map.Entry<K,V> ceilingEntry(K)` |  |
| `K ceilingKey(K)` |  |
| `java.util.concurrent.ConcurrentSkipListMap<K,V> clone()` |  |
| `java.util.Comparator<? super K> comparator()` |  |
| `java.util.NavigableSet<K> descendingKeySet()` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> descendingMap()` |  |
| `java.util.Set<java.util.Map.Entry<K,V>> entrySet()` |  |
| `java.util.Map.Entry<K,V> firstEntry()` |  |
| `K firstKey()` |  |
| `java.util.Map.Entry<K,V> floorEntry(K)` |  |
| `K floorKey(K)` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> headMap(K, boolean)` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> headMap(K)` |  |
| `java.util.Map.Entry<K,V> higherEntry(K)` |  |
| `K higherKey(K)` |  |
| `java.util.NavigableSet<K> keySet()` |  |
| `java.util.Map.Entry<K,V> lastEntry()` |  |
| `K lastKey()` |  |
| `java.util.Map.Entry<K,V> lowerEntry(K)` |  |
| `K lowerKey(K)` |  |
| `java.util.NavigableSet<K> navigableKeySet()` |  |
| `java.util.Map.Entry<K,V> pollFirstEntry()` |  |
| `java.util.Map.Entry<K,V> pollLastEntry()` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> subMap(K, boolean, K, boolean)` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> subMap(K, K)` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> tailMap(K, boolean)` |  |
| `java.util.concurrent.ConcurrentNavigableMap<K,V> tailMap(K)` |  |

---

### `class ConcurrentSkipListSet<E>`

- **Extends:** `java.util.AbstractSet<E>`
- **Implements:** `java.lang.Cloneable java.util.NavigableSet<E> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConcurrentSkipListSet()` |  |
| `ConcurrentSkipListSet(java.util.Comparator<? super E>)` |  |
| `ConcurrentSkipListSet(java.util.Collection<? extends E>)` |  |
| `ConcurrentSkipListSet(java.util.SortedSet<E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `E ceiling(E)` |  |
| `java.util.concurrent.ConcurrentSkipListSet<E> clone()` |  |
| `java.util.Comparator<? super E> comparator()` |  |
| `java.util.Iterator<E> descendingIterator()` |  |
| `java.util.NavigableSet<E> descendingSet()` |  |
| `E first()` |  |
| `E floor(E)` |  |
| `java.util.NavigableSet<E> headSet(E, boolean)` |  |
| `java.util.NavigableSet<E> headSet(E)` |  |
| `E higher(E)` |  |
| `java.util.Iterator<E> iterator()` |  |
| `E last()` |  |
| `E lower(E)` |  |
| `E pollFirst()` |  |
| `E pollLast()` |  |
| `int size()` |  |
| `java.util.NavigableSet<E> subSet(E, boolean, E, boolean)` |  |
| `java.util.NavigableSet<E> subSet(E, E)` |  |
| `java.util.NavigableSet<E> tailSet(E, boolean)` |  |
| `java.util.NavigableSet<E> tailSet(E)` |  |

---

### `class CopyOnWriteArrayList<E>`

- **Implements:** `java.lang.Cloneable java.util.List<E> java.util.RandomAccess java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CopyOnWriteArrayList()` |  |
| `CopyOnWriteArrayList(@NonNull java.util.Collection<? extends E>)` |  |
| `CopyOnWriteArrayList(@NonNull E[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean add(E)` |  |
| `void add(int, E)` |  |
| `boolean addAll(@NonNull java.util.Collection<? extends E>)` |  |
| `boolean addAll(int, @NonNull java.util.Collection<? extends E>)` |  |
| `int addAllAbsent(@NonNull java.util.Collection<? extends E>)` |  |
| `boolean addIfAbsent(E)` |  |
| `void clear()` |  |
| `boolean contains(@Nullable Object)` |  |
| `boolean containsAll(@NonNull java.util.Collection<?>)` |  |
| `void forEach(@NonNull java.util.function.Consumer<? super E>)` |  |
| `E get(int)` |  |
| `int indexOf(@Nullable Object)` |  |
| `int indexOf(@Nullable E, int)` |  |
| `boolean isEmpty()` |  |
| `int lastIndexOf(@Nullable Object)` |  |
| `int lastIndexOf(@Nullable E, int)` |  |
| `E remove(int)` |  |
| `boolean remove(@Nullable Object)` |  |
| `boolean removeAll(@NonNull java.util.Collection<?>)` |  |
| `boolean retainAll(@NonNull java.util.Collection<?>)` |  |
| `E set(int, E)` |  |
| `int size()` |  |

---

### `class CopyOnWriteArraySet<E>`

- **Extends:** `java.util.AbstractSet<E>`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CopyOnWriteArraySet()` |  |
| `CopyOnWriteArraySet(java.util.Collection<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void forEach(java.util.function.Consumer<? super E>)` |  |
| `java.util.Iterator<E> iterator()` |  |
| `int size()` |  |

---

### `class CountDownLatch`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CountDownLatch(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void await() throws java.lang.InterruptedException` |  |
| `boolean await(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `void countDown()` |  |
| `long getCount()` |  |

---

### `class abstract CountedCompleter<T>`

- **Extends:** `java.util.concurrent.ForkJoinTask<T>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CountedCompleter(java.util.concurrent.CountedCompleter<?>, int)` |  |
| `CountedCompleter(java.util.concurrent.CountedCompleter<?>)` |  |
| `CountedCompleter()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void addToPendingCount(int)` |  |
| `final boolean compareAndSetPendingCount(int, int)` |  |
| `void complete(T)` |  |
| `abstract void compute()` |  |
| `final int decrementPendingCountUnlessZero()` |  |
| `final boolean exec()` |  |
| `final java.util.concurrent.CountedCompleter<?> firstComplete()` |  |
| `final java.util.concurrent.CountedCompleter<?> getCompleter()` |  |
| `final int getPendingCount()` |  |
| `T getRawResult()` |  |
| `final java.util.concurrent.CountedCompleter<?> getRoot()` |  |
| `final void helpComplete(int)` |  |
| `final java.util.concurrent.CountedCompleter<?> nextComplete()` |  |
| `void onCompletion(java.util.concurrent.CountedCompleter<?>)` |  |
| `boolean onExceptionalCompletion(Throwable, java.util.concurrent.CountedCompleter<?>)` |  |
| `final void propagateCompletion()` |  |
| `final void quietlyCompleteRoot()` |  |
| `final void setPendingCount(int)` |  |
| `void setRawResult(T)` |  |
| `final void tryComplete()` |  |

---

### `class CyclicBarrier`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CyclicBarrier(int, Runnable)` |  |
| `CyclicBarrier(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int await() throws java.util.concurrent.BrokenBarrierException, java.lang.InterruptedException` |  |
| `int await(long, java.util.concurrent.TimeUnit) throws java.util.concurrent.BrokenBarrierException, java.lang.InterruptedException, java.util.concurrent.TimeoutException` |  |
| `int getNumberWaiting()` |  |
| `int getParties()` |  |
| `boolean isBroken()` |  |
| `void reset()` |  |

---

### `class DelayQueue<E`

- **Extends:** `java.util.concurrent.Delayed> extends java.util.AbstractQueue<E>`
- **Implements:** `java.util.concurrent.BlockingQueue<E>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DelayQueue()` |  |
| `DelayQueue(java.util.Collection<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int drainTo(java.util.Collection<? super E>)` |  |
| `int drainTo(java.util.Collection<? super E>, int)` |  |
| `java.util.Iterator<E> iterator()` |  |
| `boolean offer(E)` |  |
| `boolean offer(E, long, java.util.concurrent.TimeUnit)` |  |
| `E peek()` |  |
| `E poll()` |  |
| `E poll(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `void put(E)` |  |
| `int remainingCapacity()` |  |
| `int size()` |  |
| `E take() throws java.lang.InterruptedException` |  |

---

### `interface Delayed`

- **Extends:** `java.lang.Comparable<java.util.concurrent.Delayed>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getDelay(java.util.concurrent.TimeUnit)` |  |

---

### `class Exchanger<V>`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Exchanger()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `V exchange(V) throws java.lang.InterruptedException` |  |
| `V exchange(V, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException, java.util.concurrent.TimeoutException` |  |

---

### `class ExecutionException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ExecutionException()` |  |
| `ExecutionException(String)` |  |
| `ExecutionException(String, Throwable)` |  |
| `ExecutionException(Throwable)` |  |

---

### `interface Executor`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void execute(Runnable)` |  |

---

### `class ExecutorCompletionService<V>`

- **Implements:** `java.util.concurrent.CompletionService<V>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ExecutorCompletionService(java.util.concurrent.Executor)` |  |
| `ExecutorCompletionService(java.util.concurrent.Executor, java.util.concurrent.BlockingQueue<java.util.concurrent.Future<V>>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.concurrent.Future<V> poll()` |  |
| `java.util.concurrent.Future<V> poll(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `java.util.concurrent.Future<V> submit(java.util.concurrent.Callable<V>)` |  |
| `java.util.concurrent.Future<V> submit(Runnable, V)` |  |
| `java.util.concurrent.Future<V> take() throws java.lang.InterruptedException` |  |

---

### `interface ExecutorService`

- **Extends:** `java.util.concurrent.Executor`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean awaitTermination(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `<T> java.util.List<java.util.concurrent.Future<T>> invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>>) throws java.lang.InterruptedException` |  |
| `<T> java.util.List<java.util.concurrent.Future<T>> invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>>, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `<T> T invokeAny(java.util.Collection<? extends java.util.concurrent.Callable<T>>) throws java.util.concurrent.ExecutionException, java.lang.InterruptedException` |  |
| `<T> T invokeAny(java.util.Collection<? extends java.util.concurrent.Callable<T>>, long, java.util.concurrent.TimeUnit) throws java.util.concurrent.ExecutionException, java.lang.InterruptedException, java.util.concurrent.TimeoutException` |  |
| `boolean isShutdown()` |  |
| `boolean isTerminated()` |  |
| `void shutdown()` |  |
| `java.util.List<java.lang.Runnable> shutdownNow()` |  |
| `<T> java.util.concurrent.Future<T> submit(java.util.concurrent.Callable<T>)` |  |
| `<T> java.util.concurrent.Future<T> submit(Runnable, T)` |  |
| `java.util.concurrent.Future<?> submit(Runnable)` |  |

---

### `class Executors`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static <T> java.util.concurrent.Callable<T> callable(Runnable, T)` |  |
| `static java.util.concurrent.Callable<java.lang.Object> callable(Runnable)` |  |
| `static java.util.concurrent.Callable<java.lang.Object> callable(java.security.PrivilegedAction<?>)` |  |
| `static java.util.concurrent.Callable<java.lang.Object> callable(java.security.PrivilegedExceptionAction<?>)` |  |
| `static java.util.concurrent.ThreadFactory defaultThreadFactory()` |  |
| `static java.util.concurrent.ExecutorService newCachedThreadPool()` |  |
| `static java.util.concurrent.ExecutorService newCachedThreadPool(java.util.concurrent.ThreadFactory)` |  |
| `static java.util.concurrent.ExecutorService newFixedThreadPool(int)` |  |
| `static java.util.concurrent.ExecutorService newFixedThreadPool(int, java.util.concurrent.ThreadFactory)` |  |
| `static java.util.concurrent.ScheduledExecutorService newScheduledThreadPool(int)` |  |
| `static java.util.concurrent.ScheduledExecutorService newScheduledThreadPool(int, java.util.concurrent.ThreadFactory)` |  |
| `static java.util.concurrent.ExecutorService newSingleThreadExecutor()` |  |
| `static java.util.concurrent.ExecutorService newSingleThreadExecutor(java.util.concurrent.ThreadFactory)` |  |
| `static java.util.concurrent.ScheduledExecutorService newSingleThreadScheduledExecutor()` |  |
| `static java.util.concurrent.ScheduledExecutorService newSingleThreadScheduledExecutor(java.util.concurrent.ThreadFactory)` |  |
| `static java.util.concurrent.ExecutorService newWorkStealingPool(int)` |  |
| `static java.util.concurrent.ExecutorService newWorkStealingPool()` |  |
| `static <T> java.util.concurrent.Callable<T> privilegedCallable(java.util.concurrent.Callable<T>)` |  |
| `static <T> java.util.concurrent.Callable<T> privilegedCallableUsingCurrentClassLoader(java.util.concurrent.Callable<T>)` |  |
| `static java.util.concurrent.ThreadFactory privilegedThreadFactory()` |  |
| `static java.util.concurrent.ExecutorService unconfigurableExecutorService(java.util.concurrent.ExecutorService)` |  |
| `static java.util.concurrent.ScheduledExecutorService unconfigurableScheduledExecutorService(java.util.concurrent.ScheduledExecutorService)` |  |

---

### `class final Flow`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int defaultBufferSize()` |  |

---

### `interface static Flow.Processor<T, R>`

- **Extends:** `java.util.concurrent.Flow.Subscriber<T> java.util.concurrent.Flow.Publisher<R>`

---

### `interface static Flow.Publisher<T>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void subscribe(java.util.concurrent.Flow.Subscriber<? super T>)` |  |

---

### `interface static Flow.Subscriber<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onComplete()` |  |
| `void onError(Throwable)` |  |
| `void onNext(T)` |  |
| `void onSubscribe(java.util.concurrent.Flow.Subscription)` |  |

---

### `interface static Flow.Subscription`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void cancel()` |  |
| `void request(long)` |  |

---

### `class ForkJoinPool`

- **Extends:** `java.util.concurrent.AbstractExecutorService`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ForkJoinPool()` |  |
| `ForkJoinPool(int)` |  |
| `ForkJoinPool(int, java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory, java.lang.Thread.UncaughtExceptionHandler, boolean)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean awaitQuiescence(long, java.util.concurrent.TimeUnit)` |  |
| `boolean awaitTermination(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `static java.util.concurrent.ForkJoinPool commonPool()` |  |
| `int drainTasksTo(java.util.Collection<? super java.util.concurrent.ForkJoinTask<?>>)` |  |
| `void execute(java.util.concurrent.ForkJoinTask<?>)` |  |
| `void execute(Runnable)` |  |
| `int getActiveThreadCount()` |  |
| `boolean getAsyncMode()` |  |
| `static int getCommonPoolParallelism()` |  |
| `java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory getFactory()` |  |
| `int getParallelism()` |  |
| `int getPoolSize()` |  |
| `int getQueuedSubmissionCount()` |  |
| `long getQueuedTaskCount()` |  |
| `int getRunningThreadCount()` |  |
| `long getStealCount()` |  |
| `java.lang.Thread.UncaughtExceptionHandler getUncaughtExceptionHandler()` |  |
| `boolean hasQueuedSubmissions()` |  |
| `<T> T invoke(java.util.concurrent.ForkJoinTask<T>)` |  |
| `<T> java.util.List<java.util.concurrent.Future<T>> invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>>)` |  |
| `boolean isQuiescent()` |  |
| `boolean isShutdown()` |  |
| `boolean isTerminated()` |  |
| `boolean isTerminating()` |  |
| `static void managedBlock(java.util.concurrent.ForkJoinPool.ManagedBlocker) throws java.lang.InterruptedException` |  |
| `java.util.concurrent.ForkJoinTask<?> pollSubmission()` |  |
| `void shutdown()` |  |
| `java.util.List<java.lang.Runnable> shutdownNow()` |  |
| `<T> java.util.concurrent.ForkJoinTask<T> submit(java.util.concurrent.ForkJoinTask<T>)` |  |
| `<T> java.util.concurrent.ForkJoinTask<T> submit(java.util.concurrent.Callable<T>)` |  |
| `<T> java.util.concurrent.ForkJoinTask<T> submit(Runnable, T)` |  |
| `java.util.concurrent.ForkJoinTask<?> submit(Runnable)` |  |

---

### `interface static ForkJoinPool.ForkJoinWorkerThreadFactory`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.concurrent.ForkJoinWorkerThread newThread(java.util.concurrent.ForkJoinPool)` |  |

---

### `interface static ForkJoinPool.ManagedBlocker`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean block() throws java.lang.InterruptedException` |  |
| `boolean isReleasable()` |  |

---

### `class abstract ForkJoinTask<V>`

- **Implements:** `java.util.concurrent.Future<V> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ForkJoinTask()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.concurrent.ForkJoinTask<?> adapt(Runnable)` |  |
| `static <T> java.util.concurrent.ForkJoinTask<T> adapt(Runnable, T)` |  |
| `static <T> java.util.concurrent.ForkJoinTask<T> adapt(java.util.concurrent.Callable<? extends T>)` |  |
| `boolean cancel(boolean)` |  |
| `final boolean compareAndSetForkJoinTaskTag(short, short)` |  |
| `void complete(V)` |  |
| `void completeExceptionally(Throwable)` |  |
| `abstract boolean exec()` |  |
| `final java.util.concurrent.ForkJoinTask<V> fork()` |  |
| `final V get() throws java.util.concurrent.ExecutionException, java.lang.InterruptedException` |  |
| `final V get(long, java.util.concurrent.TimeUnit) throws java.util.concurrent.ExecutionException, java.lang.InterruptedException, java.util.concurrent.TimeoutException` |  |
| `final Throwable getException()` |  |
| `final short getForkJoinTaskTag()` |  |
| `static java.util.concurrent.ForkJoinPool getPool()` |  |
| `static int getQueuedTaskCount()` |  |
| `abstract V getRawResult()` |  |
| `static int getSurplusQueuedTaskCount()` |  |
| `static void helpQuiesce()` |  |
| `static boolean inForkJoinPool()` |  |
| `final V invoke()` |  |
| `static void invokeAll(java.util.concurrent.ForkJoinTask<?>, java.util.concurrent.ForkJoinTask<?>)` |  |
| `static void invokeAll(java.util.concurrent.ForkJoinTask<?>...)` |  |
| `static <T extends java.util.concurrent.ForkJoinTask<?>> java.util.Collection<T> invokeAll(java.util.Collection<T>)` |  |
| `final boolean isCancelled()` |  |
| `final boolean isCompletedAbnormally()` |  |
| `final boolean isCompletedNormally()` |  |
| `final boolean isDone()` |  |
| `final V join()` |  |
| `static java.util.concurrent.ForkJoinTask<?> peekNextLocalTask()` |  |
| `static java.util.concurrent.ForkJoinTask<?> pollNextLocalTask()` |  |
| `static java.util.concurrent.ForkJoinTask<?> pollTask()` |  |
| `final void quietlyComplete()` |  |
| `final void quietlyInvoke()` |  |
| `final void quietlyJoin()` |  |
| `void reinitialize()` |  |
| `final short setForkJoinTaskTag(short)` |  |
| `abstract void setRawResult(V)` |  |
| `boolean tryUnfork()` |  |

---

### `class ForkJoinWorkerThread`

- **Extends:** `java.lang.Thread`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ForkJoinWorkerThread(java.util.concurrent.ForkJoinPool)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.concurrent.ForkJoinPool getPool()` |  |
| `int getPoolIndex()` |  |
| `void onStart()` |  |
| `void onTermination(Throwable)` |  |

---

### `interface Future<V>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean cancel(boolean)` |  |
| `V get() throws java.util.concurrent.ExecutionException, java.lang.InterruptedException` |  |
| `V get(long, java.util.concurrent.TimeUnit) throws java.util.concurrent.ExecutionException, java.lang.InterruptedException, java.util.concurrent.TimeoutException` |  |
| `boolean isCancelled()` |  |
| `boolean isDone()` |  |

---

### `class FutureTask<V>`

- **Implements:** `java.util.concurrent.RunnableFuture<V>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FutureTask(java.util.concurrent.Callable<V>)` |  |
| `FutureTask(Runnable, V)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean cancel(boolean)` |  |
| `void done()` |  |
| `V get() throws java.util.concurrent.ExecutionException, java.lang.InterruptedException` |  |
| `V get(long, java.util.concurrent.TimeUnit) throws java.util.concurrent.ExecutionException, java.lang.InterruptedException, java.util.concurrent.TimeoutException` |  |
| `boolean isCancelled()` |  |
| `boolean isDone()` |  |
| `void run()` |  |
| `boolean runAndReset()` |  |
| `void set(V)` |  |
| `void setException(Throwable)` |  |

---

### `class LinkedBlockingDeque<E>`

- **Extends:** `java.util.AbstractQueue<E>`
- **Implements:** `java.util.concurrent.BlockingDeque<E> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LinkedBlockingDeque()` |  |
| `LinkedBlockingDeque(int)` |  |
| `LinkedBlockingDeque(java.util.Collection<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addFirst(E)` |  |
| `void addLast(E)` |  |
| `java.util.Iterator<E> descendingIterator()` |  |
| `int drainTo(java.util.Collection<? super E>)` |  |
| `int drainTo(java.util.Collection<? super E>, int)` |  |
| `E getFirst()` |  |
| `E getLast()` |  |
| `java.util.Iterator<E> iterator()` |  |
| `boolean offer(E)` |  |
| `boolean offer(E, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `boolean offerFirst(E)` |  |
| `boolean offerFirst(E, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `boolean offerLast(E)` |  |
| `boolean offerLast(E, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `E peek()` |  |
| `E peekFirst()` |  |
| `E peekLast()` |  |
| `E poll()` |  |
| `E poll(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `E pollFirst()` |  |
| `E pollFirst(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `E pollLast()` |  |
| `E pollLast(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `E pop()` |  |
| `void push(E)` |  |
| `void put(E) throws java.lang.InterruptedException` |  |
| `void putFirst(E) throws java.lang.InterruptedException` |  |
| `void putLast(E) throws java.lang.InterruptedException` |  |
| `int remainingCapacity()` |  |
| `E removeFirst()` |  |
| `boolean removeFirstOccurrence(Object)` |  |
| `E removeLast()` |  |
| `boolean removeLastOccurrence(Object)` |  |
| `int size()` |  |
| `E take() throws java.lang.InterruptedException` |  |
| `E takeFirst() throws java.lang.InterruptedException` |  |
| `E takeLast() throws java.lang.InterruptedException` |  |

---

### `class LinkedBlockingQueue<E>`

- **Extends:** `java.util.AbstractQueue<E>`
- **Implements:** `java.util.concurrent.BlockingQueue<E> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LinkedBlockingQueue()` |  |
| `LinkedBlockingQueue(int)` |  |
| `LinkedBlockingQueue(java.util.Collection<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int drainTo(java.util.Collection<? super E>)` |  |
| `int drainTo(java.util.Collection<? super E>, int)` |  |
| `java.util.Iterator<E> iterator()` |  |
| `boolean offer(E, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `boolean offer(E)` |  |
| `E peek()` |  |
| `E poll(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `E poll()` |  |
| `void put(E) throws java.lang.InterruptedException` |  |
| `int remainingCapacity()` |  |
| `int size()` |  |
| `E take() throws java.lang.InterruptedException` |  |

---

### `class LinkedTransferQueue<E>`

- **Extends:** `java.util.AbstractQueue<E>`
- **Implements:** `java.io.Serializable java.util.concurrent.TransferQueue<E>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LinkedTransferQueue()` |  |
| `LinkedTransferQueue(java.util.Collection<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int drainTo(java.util.Collection<? super E>)` |  |
| `int drainTo(java.util.Collection<? super E>, int)` |  |
| `int getWaitingConsumerCount()` |  |
| `boolean hasWaitingConsumer()` |  |
| `java.util.Iterator<E> iterator()` |  |
| `boolean offer(E, long, java.util.concurrent.TimeUnit)` |  |
| `boolean offer(E)` |  |
| `E peek()` |  |
| `E poll(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `E poll()` |  |
| `void put(E)` |  |
| `int remainingCapacity()` |  |
| `int size()` |  |
| `E take() throws java.lang.InterruptedException` |  |
| `void transfer(E) throws java.lang.InterruptedException` |  |
| `boolean tryTransfer(E)` |  |
| `boolean tryTransfer(E, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |

---

### `class Phaser`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Phaser()` |  |
| `Phaser(int)` |  |
| `Phaser(java.util.concurrent.Phaser)` |  |
| `Phaser(java.util.concurrent.Phaser, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int arrive()` |  |
| `int arriveAndAwaitAdvance()` |  |
| `int arriveAndDeregister()` |  |
| `int awaitAdvance(int)` |  |
| `int awaitAdvanceInterruptibly(int) throws java.lang.InterruptedException` |  |
| `int awaitAdvanceInterruptibly(int, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException, java.util.concurrent.TimeoutException` |  |
| `int bulkRegister(int)` |  |
| `void forceTermination()` |  |
| `int getArrivedParties()` |  |
| `java.util.concurrent.Phaser getParent()` |  |
| `final int getPhase()` |  |
| `int getRegisteredParties()` |  |
| `java.util.concurrent.Phaser getRoot()` |  |
| `int getUnarrivedParties()` |  |
| `boolean isTerminated()` |  |
| `boolean onAdvance(int, int)` |  |
| `int register()` |  |

---

### `class PriorityBlockingQueue<E>`

- **Extends:** `java.util.AbstractQueue<E>`
- **Implements:** `java.util.concurrent.BlockingQueue<E> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PriorityBlockingQueue()` |  |
| `PriorityBlockingQueue(int)` |  |
| `PriorityBlockingQueue(int, java.util.Comparator<? super E>)` |  |
| `PriorityBlockingQueue(java.util.Collection<? extends E>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Comparator<? super E> comparator()` |  |
| `int drainTo(java.util.Collection<? super E>)` |  |
| `int drainTo(java.util.Collection<? super E>, int)` |  |
| `java.util.Iterator<E> iterator()` |  |
| `boolean offer(E)` |  |
| `boolean offer(E, long, java.util.concurrent.TimeUnit)` |  |
| `E peek()` |  |
| `E poll()` |  |
| `E poll(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `void put(E)` |  |
| `int remainingCapacity()` |  |
| `int size()` |  |
| `E take() throws java.lang.InterruptedException` |  |

---

### `class abstract RecursiveAction`

- **Extends:** `java.util.concurrent.ForkJoinTask<java.lang.Void>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RecursiveAction()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void compute()` |  |
| `final boolean exec()` |  |
| `final Void getRawResult()` |  |
| `final void setRawResult(Void)` |  |

---

### `class abstract RecursiveTask<V>`

- **Extends:** `java.util.concurrent.ForkJoinTask<V>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RecursiveTask()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract V compute()` |  |
| `final boolean exec()` |  |
| `final V getRawResult()` |  |
| `final void setRawResult(V)` |  |

---

### `class RejectedExecutionException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RejectedExecutionException()` |  |
| `RejectedExecutionException(String)` |  |
| `RejectedExecutionException(String, Throwable)` |  |
| `RejectedExecutionException(Throwable)` |  |

---

### `interface RejectedExecutionHandler`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void rejectedExecution(Runnable, java.util.concurrent.ThreadPoolExecutor)` |  |

---

### `interface RunnableFuture<V>`

- **Extends:** `java.lang.Runnable java.util.concurrent.Future<V>`

---

### `interface RunnableScheduledFuture<V>`

- **Extends:** `java.util.concurrent.RunnableFuture<V> java.util.concurrent.ScheduledFuture<V>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isPeriodic()` |  |

---

### `interface ScheduledExecutorService`

- **Extends:** `java.util.concurrent.ExecutorService`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.concurrent.ScheduledFuture<?> schedule(Runnable, long, java.util.concurrent.TimeUnit)` |  |
| `<V> java.util.concurrent.ScheduledFuture<V> schedule(java.util.concurrent.Callable<V>, long, java.util.concurrent.TimeUnit)` |  |
| `java.util.concurrent.ScheduledFuture<?> scheduleAtFixedRate(Runnable, long, long, java.util.concurrent.TimeUnit)` |  |
| `java.util.concurrent.ScheduledFuture<?> scheduleWithFixedDelay(Runnable, long, long, java.util.concurrent.TimeUnit)` |  |

---

### `interface ScheduledFuture<V>`

- **Extends:** `java.util.concurrent.Delayed java.util.concurrent.Future<V>`

---

### `class ScheduledThreadPoolExecutor`

- **Extends:** `java.util.concurrent.ThreadPoolExecutor`
- **Implements:** `java.util.concurrent.ScheduledExecutorService`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ScheduledThreadPoolExecutor(int)` |  |
| `ScheduledThreadPoolExecutor(int, java.util.concurrent.ThreadFactory)` |  |
| `ScheduledThreadPoolExecutor(int, java.util.concurrent.RejectedExecutionHandler)` |  |
| `ScheduledThreadPoolExecutor(int, java.util.concurrent.ThreadFactory, java.util.concurrent.RejectedExecutionHandler)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `<V> java.util.concurrent.RunnableScheduledFuture<V> decorateTask(Runnable, java.util.concurrent.RunnableScheduledFuture<V>)` |  |
| `<V> java.util.concurrent.RunnableScheduledFuture<V> decorateTask(java.util.concurrent.Callable<V>, java.util.concurrent.RunnableScheduledFuture<V>)` |  |
| `boolean getContinueExistingPeriodicTasksAfterShutdownPolicy()` |  |
| `boolean getExecuteExistingDelayedTasksAfterShutdownPolicy()` |  |
| `boolean getRemoveOnCancelPolicy()` |  |
| `java.util.concurrent.ScheduledFuture<?> schedule(Runnable, long, java.util.concurrent.TimeUnit)` |  |
| `<V> java.util.concurrent.ScheduledFuture<V> schedule(java.util.concurrent.Callable<V>, long, java.util.concurrent.TimeUnit)` |  |
| `java.util.concurrent.ScheduledFuture<?> scheduleAtFixedRate(Runnable, long, long, java.util.concurrent.TimeUnit)` |  |
| `java.util.concurrent.ScheduledFuture<?> scheduleWithFixedDelay(Runnable, long, long, java.util.concurrent.TimeUnit)` |  |
| `void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean)` |  |
| `void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean)` |  |
| `void setRemoveOnCancelPolicy(boolean)` |  |

---

### `class Semaphore`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Semaphore(int)` |  |
| `Semaphore(int, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void acquire() throws java.lang.InterruptedException` |  |
| `void acquire(int) throws java.lang.InterruptedException` |  |
| `void acquireUninterruptibly()` |  |
| `void acquireUninterruptibly(int)` |  |
| `int availablePermits()` |  |
| `int drainPermits()` |  |
| `final int getQueueLength()` |  |
| `java.util.Collection<java.lang.Thread> getQueuedThreads()` |  |
| `final boolean hasQueuedThreads()` |  |
| `boolean isFair()` |  |
| `void reducePermits(int)` |  |
| `void release()` |  |
| `void release(int)` |  |
| `boolean tryAcquire()` |  |
| `boolean tryAcquire(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `boolean tryAcquire(int)` |  |
| `boolean tryAcquire(int, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |

---

### `class SynchronousQueue<E>`

- **Extends:** `java.util.AbstractQueue<E>`
- **Implements:** `java.util.concurrent.BlockingQueue<E> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SynchronousQueue()` |  |
| `SynchronousQueue(boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int drainTo(java.util.Collection<? super E>)` |  |
| `int drainTo(java.util.Collection<? super E>, int)` |  |
| `java.util.Iterator<E> iterator()` |  |
| `boolean offer(E, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `boolean offer(E)` |  |
| `E peek()` |  |
| `E poll(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `E poll()` |  |
| `void put(E) throws java.lang.InterruptedException` |  |
| `int remainingCapacity()` |  |
| `int size()` |  |
| `E take() throws java.lang.InterruptedException` |  |

---

### `interface ThreadFactory`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Thread newThread(Runnable)` |  |

---

### `class ThreadLocalRandom`

- **Extends:** `java.util.Random`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.concurrent.ThreadLocalRandom current()` |  |
| `double nextDouble(double)` |  |
| `double nextDouble(double, double)` |  |
| `int nextInt(int, int)` |  |
| `long nextLong(long)` |  |
| `long nextLong(long, long)` |  |

---

### `class ThreadPoolExecutor`

- **Extends:** `java.util.concurrent.AbstractExecutorService`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit, java.util.concurrent.BlockingQueue<java.lang.Runnable>)` |  |
| `ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit, java.util.concurrent.BlockingQueue<java.lang.Runnable>, java.util.concurrent.ThreadFactory)` |  |
| `ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit, java.util.concurrent.BlockingQueue<java.lang.Runnable>, java.util.concurrent.RejectedExecutionHandler)` |  |
| `ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit, java.util.concurrent.BlockingQueue<java.lang.Runnable>, java.util.concurrent.ThreadFactory, java.util.concurrent.RejectedExecutionHandler)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void afterExecute(Runnable, Throwable)` |  |
| `void allowCoreThreadTimeOut(boolean)` |  |
| `boolean allowsCoreThreadTimeOut()` |  |
| `boolean awaitTermination(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `void beforeExecute(Thread, Runnable)` |  |
| `void execute(Runnable)` |  |
| `void finalize()` |  |
| `int getActiveCount()` |  |
| `long getCompletedTaskCount()` |  |
| `int getCorePoolSize()` |  |
| `long getKeepAliveTime(java.util.concurrent.TimeUnit)` |  |
| `int getLargestPoolSize()` |  |
| `int getMaximumPoolSize()` |  |
| `int getPoolSize()` |  |
| `java.util.concurrent.BlockingQueue<java.lang.Runnable> getQueue()` |  |
| `java.util.concurrent.RejectedExecutionHandler getRejectedExecutionHandler()` |  |
| `long getTaskCount()` |  |
| `java.util.concurrent.ThreadFactory getThreadFactory()` |  |
| `boolean isShutdown()` |  |
| `boolean isTerminated()` |  |
| `boolean isTerminating()` |  |
| `int prestartAllCoreThreads()` |  |
| `boolean prestartCoreThread()` |  |
| `void purge()` |  |
| `boolean remove(Runnable)` |  |
| `void setCorePoolSize(int)` |  |
| `void setKeepAliveTime(long, java.util.concurrent.TimeUnit)` |  |
| `void setMaximumPoolSize(int)` |  |
| `void setRejectedExecutionHandler(java.util.concurrent.RejectedExecutionHandler)` |  |
| `void setThreadFactory(java.util.concurrent.ThreadFactory)` |  |
| `void shutdown()` |  |
| `java.util.List<java.lang.Runnable> shutdownNow()` |  |
| `void terminated()` |  |

---

### `class static ThreadPoolExecutor.AbortPolicy`

- **Implements:** `java.util.concurrent.RejectedExecutionHandler`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ThreadPoolExecutor.AbortPolicy()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void rejectedExecution(Runnable, java.util.concurrent.ThreadPoolExecutor)` |  |

---

### `class static ThreadPoolExecutor.CallerRunsPolicy`

- **Implements:** `java.util.concurrent.RejectedExecutionHandler`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ThreadPoolExecutor.CallerRunsPolicy()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void rejectedExecution(Runnable, java.util.concurrent.ThreadPoolExecutor)` |  |

---

### `class static ThreadPoolExecutor.DiscardOldestPolicy`

- **Implements:** `java.util.concurrent.RejectedExecutionHandler`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ThreadPoolExecutor.DiscardOldestPolicy()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void rejectedExecution(Runnable, java.util.concurrent.ThreadPoolExecutor)` |  |

---

### `class static ThreadPoolExecutor.DiscardPolicy`

- **Implements:** `java.util.concurrent.RejectedExecutionHandler`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ThreadPoolExecutor.DiscardPolicy()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void rejectedExecution(Runnable, java.util.concurrent.ThreadPoolExecutor)` |  |

---

### `enum TimeUnit`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.util.concurrent.TimeUnit DAYS` |  |
| `static final java.util.concurrent.TimeUnit HOURS` |  |
| `static final java.util.concurrent.TimeUnit MICROSECONDS` |  |
| `static final java.util.concurrent.TimeUnit MILLISECONDS` |  |
| `static final java.util.concurrent.TimeUnit MINUTES` |  |
| `static final java.util.concurrent.TimeUnit NANOSECONDS` |  |
| `static final java.util.concurrent.TimeUnit SECONDS` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long convert(long, java.util.concurrent.TimeUnit)` |  |
| `void sleep(long) throws java.lang.InterruptedException` |  |
| `void timedJoin(Thread, long) throws java.lang.InterruptedException` |  |
| `void timedWait(Object, long) throws java.lang.InterruptedException` |  |
| `long toDays(long)` |  |
| `long toHours(long)` |  |
| `long toMicros(long)` |  |
| `long toMillis(long)` |  |
| `long toMinutes(long)` |  |
| `long toNanos(long)` |  |
| `long toSeconds(long)` |  |

---

### `class TimeoutException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TimeoutException()` |  |
| `TimeoutException(String)` |  |

---

### `interface TransferQueue<E>`

- **Extends:** `java.util.concurrent.BlockingQueue<E>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getWaitingConsumerCount()` |  |
| `boolean hasWaitingConsumer()` |  |
| `void transfer(E) throws java.lang.InterruptedException` |  |
| `boolean tryTransfer(E)` |  |
| `boolean tryTransfer(E, long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |

---

## Package: `java.util.concurrent.atomic`

### `class AtomicBoolean`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicBoolean(boolean)` |  |
| `AtomicBoolean()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final boolean compareAndSet(boolean, boolean)` |  |
| `final boolean get()` |  |
| `final boolean getAndSet(boolean)` |  |
| `final void lazySet(boolean)` |  |
| `final void set(boolean)` |  |
| `boolean weakCompareAndSet(boolean, boolean)` |  |

---

### `class AtomicInteger`

- **Extends:** `java.lang.Number`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicInteger(int)` |  |
| `AtomicInteger()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int accumulateAndGet(int, java.util.function.IntBinaryOperator)` |  |
| `final int addAndGet(int)` |  |
| `final boolean compareAndSet(int, int)` |  |
| `final int decrementAndGet()` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `final int get()` |  |
| `final int getAndAccumulate(int, java.util.function.IntBinaryOperator)` |  |
| `final int getAndAdd(int)` |  |
| `final int getAndDecrement()` |  |
| `final int getAndIncrement()` |  |
| `final int getAndSet(int)` |  |
| `final int getAndUpdate(java.util.function.IntUnaryOperator)` |  |
| `final int incrementAndGet()` |  |
| `int intValue()` |  |
| `final void lazySet(int)` |  |
| `long longValue()` |  |
| `final void set(int)` |  |
| `final int updateAndGet(java.util.function.IntUnaryOperator)` |  |
| `final boolean weakCompareAndSet(int, int)` |  |

---

### `class AtomicIntegerArray`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicIntegerArray(int)` |  |
| `AtomicIntegerArray(int[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int accumulateAndGet(int, int, java.util.function.IntBinaryOperator)` |  |
| `final int addAndGet(int, int)` |  |
| `final boolean compareAndSet(int, int, int)` |  |
| `final int decrementAndGet(int)` |  |
| `final int get(int)` |  |
| `final int getAndAccumulate(int, int, java.util.function.IntBinaryOperator)` |  |
| `final int getAndAdd(int, int)` |  |
| `final int getAndDecrement(int)` |  |
| `final int getAndIncrement(int)` |  |
| `final int getAndSet(int, int)` |  |
| `final int getAndUpdate(int, java.util.function.IntUnaryOperator)` |  |
| `final int incrementAndGet(int)` |  |
| `final void lazySet(int, int)` |  |
| `final int length()` |  |
| `final void set(int, int)` |  |
| `final int updateAndGet(int, java.util.function.IntUnaryOperator)` |  |
| `final boolean weakCompareAndSet(int, int, int)` |  |

---

### `class abstract AtomicIntegerFieldUpdater<T>`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicIntegerFieldUpdater()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int accumulateAndGet(T, int, java.util.function.IntBinaryOperator)` |  |
| `int addAndGet(T, int)` |  |
| `abstract boolean compareAndSet(T, int, int)` |  |
| `int decrementAndGet(T)` |  |
| `abstract int get(T)` |  |
| `final int getAndAccumulate(T, int, java.util.function.IntBinaryOperator)` |  |
| `int getAndAdd(T, int)` |  |
| `int getAndDecrement(T)` |  |
| `int getAndIncrement(T)` |  |
| `int getAndSet(T, int)` |  |
| `final int getAndUpdate(T, java.util.function.IntUnaryOperator)` |  |
| `int incrementAndGet(T)` |  |
| `abstract void lazySet(T, int)` |  |
| `static <U> java.util.concurrent.atomic.AtomicIntegerFieldUpdater<U> newUpdater(Class<U>, String)` |  |
| `abstract void set(T, int)` |  |
| `final int updateAndGet(T, java.util.function.IntUnaryOperator)` |  |
| `abstract boolean weakCompareAndSet(T, int, int)` |  |

---

### `class AtomicLong`

- **Extends:** `java.lang.Number`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicLong(long)` |  |
| `AtomicLong()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final long accumulateAndGet(long, java.util.function.LongBinaryOperator)` |  |
| `final long addAndGet(long)` |  |
| `final boolean compareAndSet(long, long)` |  |
| `final long decrementAndGet()` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `final long get()` |  |
| `final long getAndAccumulate(long, java.util.function.LongBinaryOperator)` |  |
| `final long getAndAdd(long)` |  |
| `final long getAndDecrement()` |  |
| `final long getAndIncrement()` |  |
| `final long getAndSet(long)` |  |
| `final long getAndUpdate(java.util.function.LongUnaryOperator)` |  |
| `final long incrementAndGet()` |  |
| `int intValue()` |  |
| `final void lazySet(long)` |  |
| `long longValue()` |  |
| `final void set(long)` |  |
| `final long updateAndGet(java.util.function.LongUnaryOperator)` |  |
| `final boolean weakCompareAndSet(long, long)` |  |

---

### `class AtomicLongArray`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicLongArray(int)` |  |
| `AtomicLongArray(long[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final long accumulateAndGet(int, long, java.util.function.LongBinaryOperator)` |  |
| `long addAndGet(int, long)` |  |
| `final boolean compareAndSet(int, long, long)` |  |
| `final long decrementAndGet(int)` |  |
| `final long get(int)` |  |
| `final long getAndAccumulate(int, long, java.util.function.LongBinaryOperator)` |  |
| `final long getAndAdd(int, long)` |  |
| `final long getAndDecrement(int)` |  |
| `final long getAndIncrement(int)` |  |
| `final long getAndSet(int, long)` |  |
| `final long getAndUpdate(int, java.util.function.LongUnaryOperator)` |  |
| `final long incrementAndGet(int)` |  |
| `final void lazySet(int, long)` |  |
| `final int length()` |  |
| `final void set(int, long)` |  |
| `final long updateAndGet(int, java.util.function.LongUnaryOperator)` |  |
| `final boolean weakCompareAndSet(int, long, long)` |  |

---

### `class abstract AtomicLongFieldUpdater<T>`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicLongFieldUpdater()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final long accumulateAndGet(T, long, java.util.function.LongBinaryOperator)` |  |
| `long addAndGet(T, long)` |  |
| `abstract boolean compareAndSet(T, long, long)` |  |
| `long decrementAndGet(T)` |  |
| `abstract long get(T)` |  |
| `final long getAndAccumulate(T, long, java.util.function.LongBinaryOperator)` |  |
| `long getAndAdd(T, long)` |  |
| `long getAndDecrement(T)` |  |
| `long getAndIncrement(T)` |  |
| `long getAndSet(T, long)` |  |
| `final long getAndUpdate(T, java.util.function.LongUnaryOperator)` |  |
| `long incrementAndGet(T)` |  |
| `abstract void lazySet(T, long)` |  |
| `static <U> java.util.concurrent.atomic.AtomicLongFieldUpdater<U> newUpdater(Class<U>, String)` |  |
| `abstract void set(T, long)` |  |
| `final long updateAndGet(T, java.util.function.LongUnaryOperator)` |  |
| `abstract boolean weakCompareAndSet(T, long, long)` |  |

---

### `class AtomicMarkableReference<V>`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicMarkableReference(V, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean attemptMark(V, boolean)` |  |
| `boolean compareAndSet(V, V, boolean, boolean)` |  |
| `V get(boolean[])` |  |
| `V getReference()` |  |
| `boolean isMarked()` |  |
| `void set(V, boolean)` |  |
| `boolean weakCompareAndSet(V, V, boolean, boolean)` |  |

---

### `class AtomicReference<V>`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicReference(V)` |  |
| `AtomicReference()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final V accumulateAndGet(V, java.util.function.BinaryOperator<V>)` |  |
| `final boolean compareAndSet(V, V)` |  |
| `final V get()` |  |
| `final V getAndAccumulate(V, java.util.function.BinaryOperator<V>)` |  |
| `final V getAndSet(V)` |  |
| `final V getAndUpdate(java.util.function.UnaryOperator<V>)` |  |
| `final void lazySet(V)` |  |
| `final void set(V)` |  |
| `final V updateAndGet(java.util.function.UnaryOperator<V>)` |  |
| `final boolean weakCompareAndSet(V, V)` |  |

---

### `class AtomicReferenceArray<E>`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicReferenceArray(int)` |  |
| `AtomicReferenceArray(E[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final E accumulateAndGet(int, E, java.util.function.BinaryOperator<E>)` |  |
| `final boolean compareAndSet(int, E, E)` |  |
| `final E get(int)` |  |
| `final E getAndAccumulate(int, E, java.util.function.BinaryOperator<E>)` |  |
| `final E getAndSet(int, E)` |  |
| `final E getAndUpdate(int, java.util.function.UnaryOperator<E>)` |  |
| `final void lazySet(int, E)` |  |
| `final int length()` |  |
| `final void set(int, E)` |  |
| `final E updateAndGet(int, java.util.function.UnaryOperator<E>)` |  |
| `final boolean weakCompareAndSet(int, E, E)` |  |

---

### `class abstract AtomicReferenceFieldUpdater<T, V>`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicReferenceFieldUpdater()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final V accumulateAndGet(T, V, java.util.function.BinaryOperator<V>)` |  |
| `abstract boolean compareAndSet(T, V, V)` |  |
| `abstract V get(T)` |  |
| `final V getAndAccumulate(T, V, java.util.function.BinaryOperator<V>)` |  |
| `V getAndSet(T, V)` |  |
| `final V getAndUpdate(T, java.util.function.UnaryOperator<V>)` |  |
| `abstract void lazySet(T, V)` |  |
| `static <U, W> java.util.concurrent.atomic.AtomicReferenceFieldUpdater<U,W> newUpdater(Class<U>, Class<W>, String)` |  |
| `abstract void set(T, V)` |  |
| `final V updateAndGet(T, java.util.function.UnaryOperator<V>)` |  |
| `abstract boolean weakCompareAndSet(T, V, V)` |  |

---

### `class AtomicStampedReference<V>`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AtomicStampedReference(V, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean attemptStamp(V, int)` |  |
| `boolean compareAndSet(V, V, int, int)` |  |
| `V get(int[])` |  |
| `V getReference()` |  |
| `int getStamp()` |  |
| `void set(V, int)` |  |
| `boolean weakCompareAndSet(V, V, int, int)` |  |

---

### `class DoubleAccumulator`

- **Extends:** `java.lang.Number`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DoubleAccumulator(java.util.function.DoubleBinaryOperator, double)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accumulate(double)` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `double get()` |  |
| `double getThenReset()` |  |
| `int intValue()` |  |
| `long longValue()` |  |
| `void reset()` |  |

---

### `class DoubleAdder`

- **Extends:** `java.lang.Number`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DoubleAdder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void add(double)` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `int intValue()` |  |
| `long longValue()` |  |
| `void reset()` |  |
| `double sum()` |  |
| `double sumThenReset()` |  |

---

### `class LongAccumulator`

- **Extends:** `java.lang.Number`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LongAccumulator(java.util.function.LongBinaryOperator, long)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accumulate(long)` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `long get()` |  |
| `long getThenReset()` |  |
| `int intValue()` |  |
| `long longValue()` |  |
| `void reset()` |  |

---

### `class LongAdder`

- **Extends:** `java.lang.Number`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LongAdder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void add(long)` |  |
| `void decrement()` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `void increment()` |  |
| `int intValue()` |  |
| `long longValue()` |  |
| `void reset()` |  |
| `long sum()` |  |
| `long sumThenReset()` |  |

---

## Package: `java.util.concurrent.locks`

### `class abstract AbstractOwnableSynchronizer`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractOwnableSynchronizer()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final Thread getExclusiveOwnerThread()` |  |
| `final void setExclusiveOwnerThread(Thread)` |  |

---

### `class abstract AbstractQueuedLongSynchronizer`

- **Extends:** `java.util.concurrent.locks.AbstractOwnableSynchronizer`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractQueuedLongSynchronizer()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void acquire(long)` |  |
| `final void acquireInterruptibly(long) throws java.lang.InterruptedException` |  |
| `final void acquireShared(long)` |  |
| `final void acquireSharedInterruptibly(long) throws java.lang.InterruptedException` |  |
| `final boolean compareAndSetState(long, long)` |  |
| `final java.util.Collection<java.lang.Thread> getExclusiveQueuedThreads()` |  |
| `final Thread getFirstQueuedThread()` |  |
| `final int getQueueLength()` |  |
| `final java.util.Collection<java.lang.Thread> getQueuedThreads()` |  |
| `final java.util.Collection<java.lang.Thread> getSharedQueuedThreads()` |  |
| `final long getState()` |  |
| `final int getWaitQueueLength(java.util.concurrent.locks.AbstractQueuedLongSynchronizer.ConditionObject)` |  |
| `final java.util.Collection<java.lang.Thread> getWaitingThreads(java.util.concurrent.locks.AbstractQueuedLongSynchronizer.ConditionObject)` |  |
| `final boolean hasContended()` |  |
| `final boolean hasQueuedPredecessors()` |  |
| `final boolean hasQueuedThreads()` |  |
| `final boolean hasWaiters(java.util.concurrent.locks.AbstractQueuedLongSynchronizer.ConditionObject)` |  |
| `boolean isHeldExclusively()` |  |
| `final boolean isQueued(Thread)` |  |
| `final boolean owns(java.util.concurrent.locks.AbstractQueuedLongSynchronizer.ConditionObject)` |  |
| `final boolean release(long)` |  |
| `final boolean releaseShared(long)` |  |
| `final void setState(long)` |  |
| `boolean tryAcquire(long)` |  |
| `final boolean tryAcquireNanos(long, long) throws java.lang.InterruptedException` |  |
| `long tryAcquireShared(long)` |  |
| `final boolean tryAcquireSharedNanos(long, long) throws java.lang.InterruptedException` |  |
| `boolean tryRelease(long)` |  |
| `boolean tryReleaseShared(long)` |  |

---

### `class AbstractQueuedLongSynchronizer.ConditionObject`

- **Implements:** `java.util.concurrent.locks.Condition java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractQueuedLongSynchronizer.ConditionObject()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void await() throws java.lang.InterruptedException` |  |
| `final boolean await(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `final long awaitNanos(long) throws java.lang.InterruptedException` |  |
| `final void awaitUninterruptibly()` |  |
| `final boolean awaitUntil(java.util.Date) throws java.lang.InterruptedException` |  |
| `final int getWaitQueueLength()` |  |
| `final java.util.Collection<java.lang.Thread> getWaitingThreads()` |  |
| `final boolean hasWaiters()` |  |
| `final void signal()` |  |
| `final void signalAll()` |  |

---

### `class abstract AbstractQueuedSynchronizer`

- **Extends:** `java.util.concurrent.locks.AbstractOwnableSynchronizer`
- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractQueuedSynchronizer()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void acquire(int)` |  |
| `final void acquireInterruptibly(int) throws java.lang.InterruptedException` |  |
| `final void acquireShared(int)` |  |
| `final void acquireSharedInterruptibly(int) throws java.lang.InterruptedException` |  |
| `final boolean compareAndSetState(int, int)` |  |
| `final java.util.Collection<java.lang.Thread> getExclusiveQueuedThreads()` |  |
| `final Thread getFirstQueuedThread()` |  |
| `final int getQueueLength()` |  |
| `final java.util.Collection<java.lang.Thread> getQueuedThreads()` |  |
| `final java.util.Collection<java.lang.Thread> getSharedQueuedThreads()` |  |
| `final int getState()` |  |
| `final int getWaitQueueLength(java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject)` |  |
| `final java.util.Collection<java.lang.Thread> getWaitingThreads(java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject)` |  |
| `final boolean hasContended()` |  |
| `final boolean hasQueuedPredecessors()` |  |
| `final boolean hasQueuedThreads()` |  |
| `final boolean hasWaiters(java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject)` |  |
| `boolean isHeldExclusively()` |  |
| `final boolean isQueued(Thread)` |  |
| `final boolean owns(java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject)` |  |
| `final boolean release(int)` |  |
| `final boolean releaseShared(int)` |  |
| `final void setState(int)` |  |
| `boolean tryAcquire(int)` |  |
| `final boolean tryAcquireNanos(int, long) throws java.lang.InterruptedException` |  |
| `int tryAcquireShared(int)` |  |
| `final boolean tryAcquireSharedNanos(int, long) throws java.lang.InterruptedException` |  |
| `boolean tryRelease(int)` |  |
| `boolean tryReleaseShared(int)` |  |

---

### `class AbstractQueuedSynchronizer.ConditionObject`

- **Implements:** `java.util.concurrent.locks.Condition java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractQueuedSynchronizer.ConditionObject()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void await() throws java.lang.InterruptedException` |  |
| `final boolean await(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `final long awaitNanos(long) throws java.lang.InterruptedException` |  |
| `final void awaitUninterruptibly()` |  |
| `final boolean awaitUntil(java.util.Date) throws java.lang.InterruptedException` |  |
| `final int getWaitQueueLength()` |  |
| `final java.util.Collection<java.lang.Thread> getWaitingThreads()` |  |
| `final boolean hasWaiters()` |  |
| `final void signal()` |  |
| `final void signalAll()` |  |

---

### `interface Condition`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void await() throws java.lang.InterruptedException` |  |
| `boolean await(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `long awaitNanos(long) throws java.lang.InterruptedException` |  |
| `void awaitUninterruptibly()` |  |
| `boolean awaitUntil(java.util.Date) throws java.lang.InterruptedException` |  |
| `void signal()` |  |
| `void signalAll()` |  |

---

### `interface Lock`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void lock()` |  |
| `void lockInterruptibly() throws java.lang.InterruptedException` |  |
| `java.util.concurrent.locks.Condition newCondition()` |  |
| `boolean tryLock()` |  |
| `boolean tryLock(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `void unlock()` |  |

---

### `class LockSupport`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static Object getBlocker(Thread)` |  |
| `static void park(Object)` |  |
| `static void park()` |  |
| `static void parkNanos(Object, long)` |  |
| `static void parkNanos(long)` |  |
| `static void parkUntil(Object, long)` |  |
| `static void parkUntil(long)` |  |
| `static void unpark(Thread)` |  |

---

### `interface ReadWriteLock`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.concurrent.locks.Lock readLock()` |  |
| `java.util.concurrent.locks.Lock writeLock()` |  |

---

### `class ReentrantLock`

- **Implements:** `java.util.concurrent.locks.Lock java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ReentrantLock()` |  |
| `ReentrantLock(boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getHoldCount()` |  |
| `Thread getOwner()` |  |
| `final int getQueueLength()` |  |
| `java.util.Collection<java.lang.Thread> getQueuedThreads()` |  |
| `int getWaitQueueLength(java.util.concurrent.locks.Condition)` |  |
| `java.util.Collection<java.lang.Thread> getWaitingThreads(java.util.concurrent.locks.Condition)` |  |
| `final boolean hasQueuedThread(Thread)` |  |
| `final boolean hasQueuedThreads()` |  |
| `boolean hasWaiters(java.util.concurrent.locks.Condition)` |  |
| `final boolean isFair()` |  |
| `boolean isHeldByCurrentThread()` |  |
| `boolean isLocked()` |  |
| `void lock()` |  |
| `void lockInterruptibly() throws java.lang.InterruptedException` |  |
| `java.util.concurrent.locks.Condition newCondition()` |  |
| `boolean tryLock()` |  |
| `boolean tryLock(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `void unlock()` |  |

---

### `class ReentrantReadWriteLock`

- **Implements:** `java.util.concurrent.locks.ReadWriteLock java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ReentrantReadWriteLock()` |  |
| `ReentrantReadWriteLock(boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Thread getOwner()` |  |
| `final int getQueueLength()` |  |
| `java.util.Collection<java.lang.Thread> getQueuedReaderThreads()` |  |
| `java.util.Collection<java.lang.Thread> getQueuedThreads()` |  |
| `java.util.Collection<java.lang.Thread> getQueuedWriterThreads()` |  |
| `int getReadHoldCount()` |  |
| `int getReadLockCount()` |  |
| `int getWaitQueueLength(java.util.concurrent.locks.Condition)` |  |
| `java.util.Collection<java.lang.Thread> getWaitingThreads(java.util.concurrent.locks.Condition)` |  |
| `int getWriteHoldCount()` |  |
| `final boolean hasQueuedThread(Thread)` |  |
| `final boolean hasQueuedThreads()` |  |
| `boolean hasWaiters(java.util.concurrent.locks.Condition)` |  |
| `final boolean isFair()` |  |
| `boolean isWriteLocked()` |  |
| `boolean isWriteLockedByCurrentThread()` |  |
| `java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock readLock()` |  |
| `java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock writeLock()` |  |

---

### `class static ReentrantReadWriteLock.ReadLock`

- **Implements:** `java.util.concurrent.locks.Lock java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ReentrantReadWriteLock.ReadLock(java.util.concurrent.locks.ReentrantReadWriteLock)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void lock()` |  |
| `void lockInterruptibly() throws java.lang.InterruptedException` |  |
| `java.util.concurrent.locks.Condition newCondition()` |  |
| `boolean tryLock()` |  |
| `boolean tryLock(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `void unlock()` |  |

---

### `class static ReentrantReadWriteLock.WriteLock`

- **Implements:** `java.util.concurrent.locks.Lock java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ReentrantReadWriteLock.WriteLock(java.util.concurrent.locks.ReentrantReadWriteLock)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getHoldCount()` |  |
| `boolean isHeldByCurrentThread()` |  |
| `void lock()` |  |
| `void lockInterruptibly() throws java.lang.InterruptedException` |  |
| `java.util.concurrent.locks.Condition newCondition()` |  |
| `boolean tryLock()` |  |
| `boolean tryLock(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `void unlock()` |  |

---

### `class StampedLock`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StampedLock()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.concurrent.locks.Lock asReadLock()` |  |
| `java.util.concurrent.locks.ReadWriteLock asReadWriteLock()` |  |
| `java.util.concurrent.locks.Lock asWriteLock()` |  |
| `int getReadLockCount()` |  |
| `boolean isReadLocked()` |  |
| `boolean isWriteLocked()` |  |
| `long readLock()` |  |
| `long readLockInterruptibly() throws java.lang.InterruptedException` |  |
| `long tryConvertToOptimisticRead(long)` |  |
| `long tryConvertToReadLock(long)` |  |
| `long tryConvertToWriteLock(long)` |  |
| `long tryOptimisticRead()` |  |
| `long tryReadLock()` |  |
| `long tryReadLock(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `boolean tryUnlockRead()` |  |
| `boolean tryUnlockWrite()` |  |
| `long tryWriteLock()` |  |
| `long tryWriteLock(long, java.util.concurrent.TimeUnit) throws java.lang.InterruptedException` |  |
| `void unlock(long)` |  |
| `void unlockRead(long)` |  |
| `void unlockWrite(long)` |  |
| `boolean validate(long)` |  |
| `long writeLock()` |  |
| `long writeLockInterruptibly() throws java.lang.InterruptedException` |  |

---

## Package: `java.util.function`

### `interface BiConsumer<T, U>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accept(T, U)` |  |
| `default java.util.function.BiConsumer<T,U> andThen(java.util.function.BiConsumer<? super T,? super U>)` |  |

---

### `interface BiFunction<T, U, R>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default <V> java.util.function.BiFunction<T,U,V> andThen(java.util.function.Function<? super R,? extends V>)` |  |
| `R apply(T, U)` |  |

---

### `interface BiPredicate<T, U>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.function.BiPredicate<T,U> and(java.util.function.BiPredicate<? super T,? super U>)` |  |
| `default java.util.function.BiPredicate<T,U> negate()` |  |
| `default java.util.function.BiPredicate<T,U> or(java.util.function.BiPredicate<? super T,? super U>)` |  |
| `boolean test(T, U)` |  |

---

### `interface BinaryOperator<T>`

- **Extends:** `java.util.function.BiFunction<T,T,T>`
- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static <T> java.util.function.BinaryOperator<T> maxBy(java.util.Comparator<? super T>)` |  |
| `static <T> java.util.function.BinaryOperator<T> minBy(java.util.Comparator<? super T>)` |  |

---

### `interface BooleanSupplier`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean getAsBoolean()` |  |

---

### `interface Consumer<T>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accept(T)` |  |
| `default java.util.function.Consumer<T> andThen(java.util.function.Consumer<? super T>)` |  |

---

### `interface DoubleBinaryOperator`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `double applyAsDouble(double, double)` |  |

---

### `interface DoubleConsumer`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accept(double)` |  |
| `default java.util.function.DoubleConsumer andThen(java.util.function.DoubleConsumer)` |  |

---

### `interface DoubleFunction<R>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `R apply(double)` |  |

---

### `interface DoublePredicate`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.function.DoublePredicate and(java.util.function.DoublePredicate)` |  |
| `default java.util.function.DoublePredicate negate()` |  |
| `default java.util.function.DoublePredicate or(java.util.function.DoublePredicate)` |  |
| `boolean test(double)` |  |

---

### `interface DoubleSupplier`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `double getAsDouble()` |  |

---

### `interface DoubleToIntFunction`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int applyAsInt(double)` |  |

---

### `interface DoubleToLongFunction`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long applyAsLong(double)` |  |

---

### `interface DoubleUnaryOperator`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.function.DoubleUnaryOperator andThen(java.util.function.DoubleUnaryOperator)` |  |
| `double applyAsDouble(double)` |  |
| `default java.util.function.DoubleUnaryOperator compose(java.util.function.DoubleUnaryOperator)` |  |
| `static java.util.function.DoubleUnaryOperator identity()` |  |

---

### `interface Function<T, R>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default <V> java.util.function.Function<T,V> andThen(java.util.function.Function<? super R,? extends V>)` |  |
| `R apply(T)` |  |
| `default <V> java.util.function.Function<V,R> compose(java.util.function.Function<? super V,? extends T>)` |  |
| `static <T> java.util.function.Function<T,T> identity()` |  |

---

### `interface IntBinaryOperator`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int applyAsInt(int, int)` |  |

---

### `interface IntConsumer`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accept(int)` |  |
| `default java.util.function.IntConsumer andThen(java.util.function.IntConsumer)` |  |

---

### `interface IntFunction<R>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `R apply(int)` |  |

---

### `interface IntPredicate`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.function.IntPredicate and(java.util.function.IntPredicate)` |  |
| `default java.util.function.IntPredicate negate()` |  |
| `default java.util.function.IntPredicate or(java.util.function.IntPredicate)` |  |
| `boolean test(int)` |  |

---

### `interface IntSupplier`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getAsInt()` |  |

---

### `interface IntToDoubleFunction`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `double applyAsDouble(int)` |  |

---

### `interface IntToLongFunction`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long applyAsLong(int)` |  |

---

### `interface IntUnaryOperator`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.function.IntUnaryOperator andThen(java.util.function.IntUnaryOperator)` |  |
| `int applyAsInt(int)` |  |
| `default java.util.function.IntUnaryOperator compose(java.util.function.IntUnaryOperator)` |  |
| `static java.util.function.IntUnaryOperator identity()` |  |

---

### `interface LongBinaryOperator`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long applyAsLong(long, long)` |  |

---

### `interface LongConsumer`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accept(long)` |  |
| `default java.util.function.LongConsumer andThen(java.util.function.LongConsumer)` |  |

---

### `interface LongFunction<R>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `R apply(long)` |  |

---

### `interface LongPredicate`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.function.LongPredicate and(java.util.function.LongPredicate)` |  |
| `default java.util.function.LongPredicate negate()` |  |
| `default java.util.function.LongPredicate or(java.util.function.LongPredicate)` |  |
| `boolean test(long)` |  |

---

### `interface LongSupplier`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getAsLong()` |  |

---

### `interface LongToDoubleFunction`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `double applyAsDouble(long)` |  |

---

### `interface LongToIntFunction`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int applyAsInt(long)` |  |

---

### `interface LongUnaryOperator`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.function.LongUnaryOperator andThen(java.util.function.LongUnaryOperator)` |  |
| `long applyAsLong(long)` |  |
| `default java.util.function.LongUnaryOperator compose(java.util.function.LongUnaryOperator)` |  |
| `static java.util.function.LongUnaryOperator identity()` |  |

---

### `interface ObjDoubleConsumer<T>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accept(T, double)` |  |

---

### `interface ObjIntConsumer<T>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accept(T, int)` |  |

---

### `interface ObjLongConsumer<T>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void accept(T, long)` |  |

---

### `interface Predicate<T>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.function.Predicate<T> and(java.util.function.Predicate<? super T>)` |  |
| `static <T> java.util.function.Predicate<T> isEqual(Object)` |  |
| `default java.util.function.Predicate<T> negate()` |  |
| `default java.util.function.Predicate<T> or(java.util.function.Predicate<? super T>)` |  |
| `boolean test(T)` |  |

---

### `interface Supplier<T>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `T get()` |  |

---

### `interface ToDoubleBiFunction<T, U>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `double applyAsDouble(T, U)` |  |

---

### `interface ToDoubleFunction<T>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `double applyAsDouble(T)` |  |

---

### `interface ToIntBiFunction<T, U>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int applyAsInt(T, U)` |  |

---

### `interface ToIntFunction<T>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int applyAsInt(T)` |  |

---

### `interface ToLongBiFunction<T, U>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long applyAsLong(T, U)` |  |

---

### `interface ToLongFunction<T>`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long applyAsLong(T)` |  |

---

### `interface UnaryOperator<T>`

- **Extends:** `java.util.function.Function<T,T>`
- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static <T> java.util.function.UnaryOperator<T> identity()` |  |

---

## Package: `java.util.jar`

### `class Attributes`

- **Implements:** `java.lang.Cloneable java.util.Map<java.lang.Object,java.lang.Object>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Attributes()` |  |
| `Attributes(int)` |  |
| `Attributes(java.util.jar.Attributes)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Map<java.lang.Object,java.lang.Object> map` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clear()` |  |
| `Object clone()` |  |
| `boolean containsKey(Object)` |  |
| `boolean containsValue(Object)` |  |
| `java.util.Set<java.util.Map.Entry<java.lang.Object,java.lang.Object>> entrySet()` |  |
| `Object get(Object)` |  |
| `String getValue(String)` |  |
| `String getValue(java.util.jar.Attributes.Name)` |  |
| `boolean isEmpty()` |  |
| `java.util.Set<java.lang.Object> keySet()` |  |
| `Object put(Object, Object)` |  |
| `void putAll(java.util.Map<?,?>)` |  |
| `String putValue(String, String)` |  |
| `Object remove(Object)` |  |
| `int size()` |  |
| `java.util.Collection<java.lang.Object> values()` |  |

---

### `class static Attributes.Name`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Attributes.Name(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final java.util.jar.Attributes.Name CLASS_PATH` |  |
| `static final java.util.jar.Attributes.Name CONTENT_TYPE` |  |
| `static final java.util.jar.Attributes.Name EXTENSION_LIST` |  |
| `static final java.util.jar.Attributes.Name EXTENSION_NAME` |  |
| `static final java.util.jar.Attributes.Name IMPLEMENTATION_TITLE` |  |
| `static final java.util.jar.Attributes.Name IMPLEMENTATION_VENDOR` |  |
| `static final java.util.jar.Attributes.Name IMPLEMENTATION_VERSION` |  |
| `static final java.util.jar.Attributes.Name MAIN_CLASS` |  |
| `static final java.util.jar.Attributes.Name MANIFEST_VERSION` |  |
| `static final java.util.jar.Attributes.Name SEALED` |  |
| `static final java.util.jar.Attributes.Name SIGNATURE_VERSION` |  |
| `static final java.util.jar.Attributes.Name SPECIFICATION_TITLE` |  |
| `static final java.util.jar.Attributes.Name SPECIFICATION_VENDOR` |  |
| `static final java.util.jar.Attributes.Name SPECIFICATION_VERSION` |  |

---

### `class JarEntry`

- **Extends:** `java.util.zip.ZipEntry`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `JarEntry(String)` |  |
| `JarEntry(java.util.zip.ZipEntry)` |  |
| `JarEntry(java.util.jar.JarEntry)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CENATT = 36` |  |
| `static final int CENATX = 38` |  |
| `static final int CENCOM = 32` |  |
| `static final int CENCRC = 16` |  |
| `static final int CENDSK = 34` |  |
| `static final int CENEXT = 30` |  |
| `static final int CENFLG = 8` |  |
| `static final int CENHDR = 46` |  |
| `static final int CENHOW = 10` |  |
| `static final int CENLEN = 24` |  |
| `static final int CENNAM = 28` |  |
| `static final int CENOFF = 42` |  |
| `static final long CENSIG = 33639248L` |  |
| `static final int CENSIZ = 20` |  |
| `static final int CENTIM = 12` |  |
| `static final int CENVEM = 4` |  |
| `static final int CENVER = 6` |  |
| `static final int ENDCOM = 20` |  |
| `static final int ENDHDR = 22` |  |
| `static final int ENDOFF = 16` |  |
| `static final long ENDSIG = 101010256L` |  |
| `static final int ENDSIZ = 12` |  |
| `static final int ENDSUB = 8` |  |
| `static final int ENDTOT = 10` |  |
| `static final int EXTCRC = 4` |  |
| `static final int EXTHDR = 16` |  |
| `static final int EXTLEN = 12` |  |
| `static final long EXTSIG = 134695760L` |  |
| `static final int EXTSIZ = 8` |  |
| `static final int LOCCRC = 14` |  |
| `static final int LOCEXT = 28` |  |
| `static final int LOCFLG = 6` |  |
| `static final int LOCHDR = 30` |  |
| `static final int LOCHOW = 8` |  |
| `static final int LOCLEN = 22` |  |
| `static final int LOCNAM = 26` |  |
| `static final long LOCSIG = 67324752L` |  |
| `static final int LOCSIZ = 18` |  |
| `static final int LOCTIM = 10` |  |
| `static final int LOCVER = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.jar.Attributes getAttributes() throws java.io.IOException` |  |
| `java.security.cert.Certificate[] getCertificates()` |  |
| `java.security.CodeSigner[] getCodeSigners()` |  |

---

### `class JarException`

- **Extends:** `java.util.zip.ZipException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `JarException()` |  |
| `JarException(String)` |  |

---

### `class JarFile`

- **Extends:** `java.util.zip.ZipFile`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `JarFile(String) throws java.io.IOException` |  |
| `JarFile(String, boolean) throws java.io.IOException` |  |
| `JarFile(java.io.File) throws java.io.IOException` |  |
| `JarFile(java.io.File, boolean) throws java.io.IOException` |  |
| `JarFile(java.io.File, boolean, int) throws java.io.IOException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CENATT = 36` |  |
| `static final int CENATX = 38` |  |
| `static final int CENCOM = 32` |  |
| `static final int CENCRC = 16` |  |
| `static final int CENDSK = 34` |  |
| `static final int CENEXT = 30` |  |
| `static final int CENFLG = 8` |  |
| `static final int CENHDR = 46` |  |
| `static final int CENHOW = 10` |  |
| `static final int CENLEN = 24` |  |
| `static final int CENNAM = 28` |  |
| `static final int CENOFF = 42` |  |
| `static final long CENSIG = 33639248L` |  |
| `static final int CENSIZ = 20` |  |
| `static final int CENTIM = 12` |  |
| `static final int CENVEM = 4` |  |
| `static final int CENVER = 6` |  |
| `static final int ENDCOM = 20` |  |
| `static final int ENDHDR = 22` |  |
| `static final int ENDOFF = 16` |  |
| `static final long ENDSIG = 101010256L` |  |
| `static final int ENDSIZ = 12` |  |
| `static final int ENDSUB = 8` |  |
| `static final int ENDTOT = 10` |  |
| `static final int EXTCRC = 4` |  |
| `static final int EXTHDR = 16` |  |
| `static final int EXTLEN = 12` |  |
| `static final long EXTSIG = 134695760L` |  |
| `static final int EXTSIZ = 8` |  |
| `static final int LOCCRC = 14` |  |
| `static final int LOCEXT = 28` |  |
| `static final int LOCFLG = 6` |  |
| `static final int LOCHDR = 30` |  |
| `static final int LOCHOW = 8` |  |
| `static final int LOCLEN = 22` |  |
| `static final int LOCNAM = 26` |  |
| `static final long LOCSIG = 67324752L` |  |
| `static final int LOCSIZ = 18` |  |
| `static final int LOCTIM = 10` |  |
| `static final int LOCVER = 4` |  |
| `static final String MANIFEST_NAME = "META-INF/MANIFEST.MF"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Enumeration<java.util.jar.JarEntry> entries()` |  |
| `java.util.jar.JarEntry getJarEntry(String)` |  |
| `java.util.jar.Manifest getManifest() throws java.io.IOException` |  |
| `java.util.stream.Stream<java.util.jar.JarEntry> stream()` |  |

---

### `class JarInputStream`

- **Extends:** `java.util.zip.ZipInputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `JarInputStream(java.io.InputStream) throws java.io.IOException` |  |
| `JarInputStream(java.io.InputStream, boolean) throws java.io.IOException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CENATT = 36` |  |
| `static final int CENATX = 38` |  |
| `static final int CENCOM = 32` |  |
| `static final int CENCRC = 16` |  |
| `static final int CENDSK = 34` |  |
| `static final int CENEXT = 30` |  |
| `static final int CENFLG = 8` |  |
| `static final int CENHDR = 46` |  |
| `static final int CENHOW = 10` |  |
| `static final int CENLEN = 24` |  |
| `static final int CENNAM = 28` |  |
| `static final int CENOFF = 42` |  |
| `static final long CENSIG = 33639248L` |  |
| `static final int CENSIZ = 20` |  |
| `static final int CENTIM = 12` |  |
| `static final int CENVEM = 4` |  |
| `static final int CENVER = 6` |  |
| `static final int ENDCOM = 20` |  |
| `static final int ENDHDR = 22` |  |
| `static final int ENDOFF = 16` |  |
| `static final long ENDSIG = 101010256L` |  |
| `static final int ENDSIZ = 12` |  |
| `static final int ENDSUB = 8` |  |
| `static final int ENDTOT = 10` |  |
| `static final int EXTCRC = 4` |  |
| `static final int EXTHDR = 16` |  |
| `static final int EXTLEN = 12` |  |
| `static final long EXTSIG = 134695760L` |  |
| `static final int EXTSIZ = 8` |  |
| `static final int LOCCRC = 14` |  |
| `static final int LOCEXT = 28` |  |
| `static final int LOCFLG = 6` |  |
| `static final int LOCHDR = 30` |  |
| `static final int LOCHOW = 8` |  |
| `static final int LOCLEN = 22` |  |
| `static final int LOCNAM = 26` |  |
| `static final long LOCSIG = 67324752L` |  |
| `static final int LOCSIZ = 18` |  |
| `static final int LOCTIM = 10` |  |
| `static final int LOCVER = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.jar.Manifest getManifest()` |  |
| `java.util.jar.JarEntry getNextJarEntry() throws java.io.IOException` |  |

---

### `class JarOutputStream`

- **Extends:** `java.util.zip.ZipOutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `JarOutputStream(java.io.OutputStream, java.util.jar.Manifest) throws java.io.IOException` |  |
| `JarOutputStream(java.io.OutputStream) throws java.io.IOException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CENATT = 36` |  |
| `static final int CENATX = 38` |  |
| `static final int CENCOM = 32` |  |
| `static final int CENCRC = 16` |  |
| `static final int CENDSK = 34` |  |
| `static final int CENEXT = 30` |  |
| `static final int CENFLG = 8` |  |
| `static final int CENHDR = 46` |  |
| `static final int CENHOW = 10` |  |
| `static final int CENLEN = 24` |  |
| `static final int CENNAM = 28` |  |
| `static final int CENOFF = 42` |  |
| `static final long CENSIG = 33639248L` |  |
| `static final int CENSIZ = 20` |  |
| `static final int CENTIM = 12` |  |
| `static final int CENVEM = 4` |  |
| `static final int CENVER = 6` |  |
| `static final int ENDCOM = 20` |  |
| `static final int ENDHDR = 22` |  |
| `static final int ENDOFF = 16` |  |
| `static final long ENDSIG = 101010256L` |  |
| `static final int ENDSIZ = 12` |  |
| `static final int ENDSUB = 8` |  |
| `static final int ENDTOT = 10` |  |
| `static final int EXTCRC = 4` |  |
| `static final int EXTHDR = 16` |  |
| `static final int EXTLEN = 12` |  |
| `static final long EXTSIG = 134695760L` |  |
| `static final int EXTSIZ = 8` |  |
| `static final int LOCCRC = 14` |  |
| `static final int LOCEXT = 28` |  |
| `static final int LOCFLG = 6` |  |
| `static final int LOCHDR = 30` |  |
| `static final int LOCHOW = 8` |  |
| `static final int LOCLEN = 22` |  |
| `static final int LOCNAM = 26` |  |
| `static final long LOCSIG = 67324752L` |  |
| `static final int LOCSIZ = 18` |  |
| `static final int LOCTIM = 10` |  |
| `static final int LOCVER = 4` |  |

---

### `class Manifest`

- **Implements:** `java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Manifest()` |  |
| `Manifest(java.io.InputStream) throws java.io.IOException` |  |
| `Manifest(java.util.jar.Manifest)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clear()` |  |
| `Object clone()` |  |
| `java.util.jar.Attributes getAttributes(String)` |  |
| `java.util.Map<java.lang.String,java.util.jar.Attributes> getEntries()` |  |
| `java.util.jar.Attributes getMainAttributes()` |  |
| `void read(java.io.InputStream) throws java.io.IOException` |  |
| `void write(java.io.OutputStream) throws java.io.IOException` |  |

---

### `class abstract Pack200`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.jar.Pack200.Packer newPacker()` |  |
| `static java.util.jar.Pack200.Unpacker newUnpacker()` |  |

---

### `interface static Pack200.Packer`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String CLASS_ATTRIBUTE_PFX = "pack.class.attribute."` |  |
| `static final String CODE_ATTRIBUTE_PFX = "pack.code.attribute."` |  |
| `static final String DEFLATE_HINT = "pack.deflate.hint"` |  |
| `static final String EFFORT = "pack.effort"` |  |
| `static final String ERROR = "error"` |  |
| `static final String FALSE = "false"` |  |
| `static final String FIELD_ATTRIBUTE_PFX = "pack.field.attribute."` |  |
| `static final String KEEP = "keep"` |  |
| `static final String KEEP_FILE_ORDER = "pack.keep.file.order"` |  |
| `static final String LATEST = "latest"` |  |
| `static final String METHOD_ATTRIBUTE_PFX = "pack.method.attribute."` |  |
| `static final String MODIFICATION_TIME = "pack.modification.time"` |  |
| `static final String PASS = "pass"` |  |
| `static final String PASS_FILE_PFX = "pack.pass.file."` |  |
| `static final String PROGRESS = "pack.progress"` |  |
| `static final String SEGMENT_LIMIT = "pack.segment.limit"` |  |
| `static final String STRIP = "strip"` |  |
| `static final String TRUE = "true"` |  |
| `static final String UNKNOWN_ATTRIBUTE = "pack.unknown.attribute"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void pack(java.util.jar.JarFile, java.io.OutputStream) throws java.io.IOException` |  |
| `void pack(java.util.jar.JarInputStream, java.io.OutputStream) throws java.io.IOException` |  |
| `java.util.SortedMap<java.lang.String,java.lang.String> properties()` |  |

---

### `interface static Pack200.Unpacker`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String DEFLATE_HINT = "unpack.deflate.hint"` |  |
| `static final String FALSE = "false"` |  |
| `static final String KEEP = "keep"` |  |
| `static final String PROGRESS = "unpack.progress"` |  |
| `static final String TRUE = "true"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.SortedMap<java.lang.String,java.lang.String> properties()` |  |
| `void unpack(java.io.InputStream, java.util.jar.JarOutputStream) throws java.io.IOException` |  |
| `void unpack(java.io.File, java.util.jar.JarOutputStream) throws java.io.IOException` |  |

---

## Package: `java.util.logging`

### `class ConsoleHandler`

- **Extends:** `java.util.logging.StreamHandler`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConsoleHandler()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |

---

### `class ErrorManager`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ErrorManager()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CLOSE_FAILURE = 3` |  |
| `static final int FLUSH_FAILURE = 2` |  |
| `static final int FORMAT_FAILURE = 5` |  |
| `static final int GENERIC_FAILURE = 0` |  |
| `static final int OPEN_FAILURE = 4` |  |
| `static final int WRITE_FAILURE = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void error(String, Exception, int)` |  |

---

### `class FileHandler`

- **Extends:** `java.util.logging.StreamHandler`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileHandler() throws java.io.IOException, java.lang.SecurityException` |  |
| `FileHandler(String) throws java.io.IOException, java.lang.SecurityException` |  |
| `FileHandler(String, boolean) throws java.io.IOException, java.lang.SecurityException` |  |
| `FileHandler(String, int, int) throws java.io.IOException, java.lang.SecurityException` |  |
| `FileHandler(String, int, int, boolean) throws java.io.IOException, java.lang.SecurityException` |  |

---

### `interface Filter`

- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isLoggable(java.util.logging.LogRecord)` |  |

---

### `class abstract Formatter`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Formatter()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract String format(java.util.logging.LogRecord)` |  |
| `String formatMessage(java.util.logging.LogRecord)` |  |
| `String getHead(java.util.logging.Handler)` |  |
| `String getTail(java.util.logging.Handler)` |  |

---

### `class abstract Handler`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Handler()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void close() throws java.lang.SecurityException` |  |
| `abstract void flush()` |  |
| `String getEncoding()` |  |
| `java.util.logging.ErrorManager getErrorManager()` |  |
| `java.util.logging.Filter getFilter()` |  |
| `java.util.logging.Formatter getFormatter()` |  |
| `java.util.logging.Level getLevel()` |  |
| `boolean isLoggable(java.util.logging.LogRecord)` |  |
| `abstract void publish(java.util.logging.LogRecord)` |  |
| `void reportError(String, Exception, int)` |  |
| `void setEncoding(String) throws java.lang.SecurityException, java.io.UnsupportedEncodingException` |  |
| `void setErrorManager(java.util.logging.ErrorManager)` |  |
| `void setFilter(java.util.logging.Filter) throws java.lang.SecurityException` |  |
| `void setFormatter(java.util.logging.Formatter) throws java.lang.SecurityException` |  |
| `void setLevel(java.util.logging.Level) throws java.lang.SecurityException` |  |

---

### `class Level`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Level(@NonNull String, int)` |  |
| `Level(@NonNull String, int, @Nullable String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int intValue()` |  |

---

### `class LogManager`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LogManager()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String LOGGING_MXBEAN_NAME = "java.util.logging:type=Logging"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean addLogger(java.util.logging.Logger)` |  |
| `void checkAccess() throws java.lang.SecurityException` |  |
| `static java.util.logging.LogManager getLogManager()` |  |
| `java.util.logging.Logger getLogger(String)` |  |
| `java.util.Enumeration<java.lang.String> getLoggerNames()` |  |
| `static java.util.logging.LoggingMXBean getLoggingMXBean()` |  |
| `String getProperty(String)` |  |
| `void readConfiguration() throws java.io.IOException, java.lang.SecurityException` |  |
| `void readConfiguration(java.io.InputStream) throws java.io.IOException, java.lang.SecurityException` |  |
| `void reset() throws java.lang.SecurityException` |  |

---

### `class LogRecord`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LogRecord(java.util.logging.Level, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.logging.Level getLevel()` |  |
| `String getLoggerName()` |  |
| `String getMessage()` |  |
| `long getMillis()` |  |
| `Object[] getParameters()` |  |
| `java.util.ResourceBundle getResourceBundle()` |  |
| `String getResourceBundleName()` |  |
| `long getSequenceNumber()` |  |
| `String getSourceClassName()` |  |
| `String getSourceMethodName()` |  |
| `int getThreadID()` |  |
| `Throwable getThrown()` |  |
| `void setLevel(java.util.logging.Level)` |  |
| `void setLoggerName(String)` |  |
| `void setMessage(String)` |  |
| `void setMillis(long)` |  |
| `void setParameters(Object[])` |  |
| `void setResourceBundle(java.util.ResourceBundle)` |  |
| `void setResourceBundleName(String)` |  |
| `void setSequenceNumber(long)` |  |
| `void setSourceClassName(String)` |  |
| `void setSourceMethodName(String)` |  |
| `void setThreadID(int)` |  |
| `void setThrown(Throwable)` |  |

---

### `class Logger`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Logger(@Nullable String, @Nullable String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addHandler(@NonNull java.util.logging.Handler) throws java.lang.SecurityException` |  |
| `void config(@Nullable String)` |  |
| `void config(@NonNull java.util.function.Supplier<java.lang.String>)` |  |
| `void entering(@Nullable String, @Nullable String)` |  |
| `void entering(@Nullable String, @Nullable String, @Nullable Object)` |  |
| `void entering(@Nullable String, @Nullable String, @Nullable Object[])` |  |
| `void exiting(@Nullable String, @Nullable String)` |  |
| `void exiting(@Nullable String, @Nullable String, @Nullable Object)` |  |
| `void fine(@Nullable String)` |  |
| `void fine(@NonNull java.util.function.Supplier<java.lang.String>)` |  |
| `void finer(@Nullable String)` |  |
| `void finer(@NonNull java.util.function.Supplier<java.lang.String>)` |  |
| `void finest(@Nullable String)` |  |
| `void finest(@NonNull java.util.function.Supplier<java.lang.String>)` |  |
| `boolean getUseParentHandlers()` |  |
| `void info(@Nullable String)` |  |
| `void info(@NonNull java.util.function.Supplier<java.lang.String>)` |  |
| `boolean isLoggable(@NonNull java.util.logging.Level)` |  |
| `void log(@NonNull java.util.logging.LogRecord)` |  |
| `void log(@NonNull java.util.logging.Level, @Nullable String)` |  |
| `void log(@NonNull java.util.logging.Level, @NonNull java.util.function.Supplier<java.lang.String>)` |  |
| `void log(@NonNull java.util.logging.Level, @Nullable String, @Nullable Object)` |  |
| `void log(@NonNull java.util.logging.Level, @Nullable String, @Nullable Object[])` |  |
| `void log(@NonNull java.util.logging.Level, @Nullable String, @Nullable Throwable)` |  |
| `void log(@NonNull java.util.logging.Level, @Nullable Throwable, @NonNull java.util.function.Supplier<java.lang.String>)` |  |
| `void logp(@NonNull java.util.logging.Level, @Nullable String, @Nullable String, @Nullable String)` |  |
| `void logp(@NonNull java.util.logging.Level, @Nullable String, @Nullable String, @NonNull java.util.function.Supplier<java.lang.String>)` |  |
| `void logp(@NonNull java.util.logging.Level, @Nullable String, @Nullable String, @Nullable String, @Nullable Object)` |  |
| `void logp(@NonNull java.util.logging.Level, @Nullable String, @Nullable String, @Nullable String, @Nullable Object[])` |  |
| `void logp(@NonNull java.util.logging.Level, @Nullable String, @Nullable String, @Nullable String, @Nullable Throwable)` |  |
| `void logp(@NonNull java.util.logging.Level, @Nullable String, @Nullable String, @Nullable Throwable, @NonNull java.util.function.Supplier<java.lang.String>)` |  |
| `void logrb(@NonNull java.util.logging.Level, @Nullable String, @Nullable String, @Nullable java.util.ResourceBundle, @Nullable String, @Nullable java.lang.Object...)` |  |
| `void logrb(@NonNull java.util.logging.Level, @Nullable String, @Nullable String, @Nullable java.util.ResourceBundle, @Nullable String, @Nullable Throwable)` |  |
| `void removeHandler(@Nullable java.util.logging.Handler) throws java.lang.SecurityException` |  |
| `void setFilter(@Nullable java.util.logging.Filter) throws java.lang.SecurityException` |  |
| `void setLevel(@Nullable java.util.logging.Level) throws java.lang.SecurityException` |  |
| `void setParent(@NonNull java.util.logging.Logger)` |  |
| `void setResourceBundle(@NonNull java.util.ResourceBundle)` |  |
| `void setUseParentHandlers(boolean)` |  |
| `void severe(@Nullable String)` |  |
| `void severe(@NonNull java.util.function.Supplier<java.lang.String>)` |  |
| `void throwing(@Nullable String, @Nullable String, @Nullable Throwable)` |  |
| `void warning(@Nullable String)` |  |
| `void warning(@NonNull java.util.function.Supplier<java.lang.String>)` |  |

---

### `interface LoggingMXBean`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getLoggerLevel(String)` |  |
| `java.util.List<java.lang.String> getLoggerNames()` |  |
| `String getParentLoggerName(String)` |  |
| `void setLoggerLevel(String, String)` |  |

---

### `class final LoggingPermission`

- **Extends:** `java.security.BasicPermission`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LoggingPermission(String, String) throws java.lang.IllegalArgumentException` |  |

---

### `class MemoryHandler`

- **Extends:** `java.util.logging.Handler`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MemoryHandler()` |  |
| `MemoryHandler(java.util.logging.Handler, int, java.util.logging.Level)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.lang.SecurityException` |  |
| `void flush()` |  |
| `java.util.logging.Level getPushLevel()` |  |
| `void publish(java.util.logging.LogRecord)` |  |
| `void push()` |  |
| `void setPushLevel(java.util.logging.Level) throws java.lang.SecurityException` |  |

---

### `class SimpleFormatter`

- **Extends:** `java.util.logging.Formatter`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SimpleFormatter()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String format(java.util.logging.LogRecord)` |  |

---

### `class SocketHandler`

- **Extends:** `java.util.logging.StreamHandler`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SocketHandler() throws java.io.IOException` |  |
| `SocketHandler(String, int) throws java.io.IOException` |  |

---

### `class StreamHandler`

- **Extends:** `java.util.logging.Handler`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StreamHandler()` |  |
| `StreamHandler(java.io.OutputStream, java.util.logging.Formatter)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.lang.SecurityException` |  |
| `void flush()` |  |
| `void publish(java.util.logging.LogRecord)` |  |
| `void setOutputStream(java.io.OutputStream) throws java.lang.SecurityException` |  |

---

### `class XMLFormatter`

- **Extends:** `java.util.logging.Formatter`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `XMLFormatter()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String format(java.util.logging.LogRecord)` |  |

---

## Package: `java.util.prefs`

### `class abstract AbstractPreferences`

- **Extends:** `java.util.prefs.Preferences`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractPreferences(java.util.prefs.AbstractPreferences, String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final Object lock` |  |
| `boolean newNode` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String absolutePath()` |  |
| `void addNodeChangeListener(java.util.prefs.NodeChangeListener)` |  |
| `void addPreferenceChangeListener(java.util.prefs.PreferenceChangeListener)` |  |
| `final java.util.prefs.AbstractPreferences[] cachedChildren()` |  |
| `abstract java.util.prefs.AbstractPreferences childSpi(String)` |  |
| `String[] childrenNames() throws java.util.prefs.BackingStoreException` |  |
| `abstract String[] childrenNamesSpi() throws java.util.prefs.BackingStoreException` |  |
| `void clear() throws java.util.prefs.BackingStoreException` |  |
| `void exportNode(java.io.OutputStream) throws java.util.prefs.BackingStoreException, java.io.IOException` |  |
| `void exportSubtree(java.io.OutputStream) throws java.util.prefs.BackingStoreException, java.io.IOException` |  |
| `void flush() throws java.util.prefs.BackingStoreException` |  |
| `abstract void flushSpi() throws java.util.prefs.BackingStoreException` |  |
| `String get(String, String)` |  |
| `boolean getBoolean(String, boolean)` |  |
| `byte[] getByteArray(String, byte[])` |  |
| `java.util.prefs.AbstractPreferences getChild(String) throws java.util.prefs.BackingStoreException` |  |
| `double getDouble(String, double)` |  |
| `float getFloat(String, float)` |  |
| `int getInt(String, int)` |  |
| `long getLong(String, long)` |  |
| `abstract String getSpi(String)` |  |
| `boolean isRemoved()` |  |
| `boolean isUserNode()` |  |
| `String[] keys() throws java.util.prefs.BackingStoreException` |  |
| `abstract String[] keysSpi() throws java.util.prefs.BackingStoreException` |  |
| `String name()` |  |
| `java.util.prefs.Preferences node(String)` |  |
| `boolean nodeExists(String) throws java.util.prefs.BackingStoreException` |  |
| `java.util.prefs.Preferences parent()` |  |
| `void put(String, String)` |  |
| `void putBoolean(String, boolean)` |  |
| `void putByteArray(String, byte[])` |  |
| `void putDouble(String, double)` |  |
| `void putFloat(String, float)` |  |
| `void putInt(String, int)` |  |
| `void putLong(String, long)` |  |
| `abstract void putSpi(String, String)` |  |
| `void remove(String)` |  |
| `void removeNode() throws java.util.prefs.BackingStoreException` |  |
| `void removeNodeChangeListener(java.util.prefs.NodeChangeListener)` |  |
| `abstract void removeNodeSpi() throws java.util.prefs.BackingStoreException` |  |
| `void removePreferenceChangeListener(java.util.prefs.PreferenceChangeListener)` |  |
| `abstract void removeSpi(String)` |  |
| `void sync() throws java.util.prefs.BackingStoreException` |  |
| `abstract void syncSpi() throws java.util.prefs.BackingStoreException` |  |

---

### `class BackingStoreException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BackingStoreException(String)` |  |
| `BackingStoreException(Throwable)` |  |

---

### `class InvalidPreferencesFormatException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidPreferencesFormatException(Throwable)` |  |
| `InvalidPreferencesFormatException(String)` |  |
| `InvalidPreferencesFormatException(String, Throwable)` |  |

---

### `class NodeChangeEvent`

- **Extends:** `java.util.EventObject`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NodeChangeEvent(java.util.prefs.Preferences, java.util.prefs.Preferences)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.prefs.Preferences getChild()` |  |
| `java.util.prefs.Preferences getParent()` |  |

---

### `interface NodeChangeListener`

- **Extends:** `java.util.EventListener`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void childAdded(java.util.prefs.NodeChangeEvent)` |  |
| `void childRemoved(java.util.prefs.NodeChangeEvent)` |  |

---

### `class PreferenceChangeEvent`

- **Extends:** `java.util.EventObject`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PreferenceChangeEvent(java.util.prefs.Preferences, String, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getKey()` |  |
| `String getNewValue()` |  |
| `java.util.prefs.Preferences getNode()` |  |

---

### `interface PreferenceChangeListener`

- **Extends:** `java.util.EventListener`
- **Annotations:** `@java.lang.FunctionalInterface`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void preferenceChange(java.util.prefs.PreferenceChangeEvent)` |  |

---

### `class abstract Preferences`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Preferences()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MAX_KEY_LENGTH = 80` |  |
| `static final int MAX_NAME_LENGTH = 80` |  |
| `static final int MAX_VALUE_LENGTH = 8192` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract String absolutePath()` |  |
| `abstract void addNodeChangeListener(java.util.prefs.NodeChangeListener)` |  |
| `abstract void addPreferenceChangeListener(java.util.prefs.PreferenceChangeListener)` |  |
| `abstract String[] childrenNames() throws java.util.prefs.BackingStoreException` |  |
| `abstract void clear() throws java.util.prefs.BackingStoreException` |  |
| `abstract void exportNode(java.io.OutputStream) throws java.util.prefs.BackingStoreException, java.io.IOException` |  |
| `abstract void exportSubtree(java.io.OutputStream) throws java.util.prefs.BackingStoreException, java.io.IOException` |  |
| `abstract void flush() throws java.util.prefs.BackingStoreException` |  |
| `abstract String get(String, String)` |  |
| `abstract boolean getBoolean(String, boolean)` |  |
| `abstract byte[] getByteArray(String, byte[])` |  |
| `abstract double getDouble(String, double)` |  |
| `abstract float getFloat(String, float)` |  |
| `abstract int getInt(String, int)` |  |
| `abstract long getLong(String, long)` |  |
| `static void importPreferences(java.io.InputStream) throws java.io.IOException, java.util.prefs.InvalidPreferencesFormatException` |  |
| `abstract boolean isUserNode()` |  |
| `abstract String[] keys() throws java.util.prefs.BackingStoreException` |  |
| `abstract String name()` |  |
| `abstract java.util.prefs.Preferences node(String)` |  |
| `abstract boolean nodeExists(String) throws java.util.prefs.BackingStoreException` |  |
| `abstract java.util.prefs.Preferences parent()` |  |
| `abstract void put(String, String)` |  |
| `abstract void putBoolean(String, boolean)` |  |
| `abstract void putByteArray(String, byte[])` |  |
| `abstract void putDouble(String, double)` |  |
| `abstract void putFloat(String, float)` |  |
| `abstract void putInt(String, int)` |  |
| `abstract void putLong(String, long)` |  |
| `abstract void remove(String)` |  |
| `abstract void removeNode() throws java.util.prefs.BackingStoreException` |  |
| `abstract void removeNodeChangeListener(java.util.prefs.NodeChangeListener)` |  |
| `abstract void removePreferenceChangeListener(java.util.prefs.PreferenceChangeListener)` |  |
| `abstract void sync() throws java.util.prefs.BackingStoreException` |  |
| `static java.util.prefs.Preferences systemNodeForPackage(Class<?>)` |  |
| `static java.util.prefs.Preferences systemRoot()` |  |
| `abstract String toString()` |  |
| `static java.util.prefs.Preferences userNodeForPackage(Class<?>)` |  |
| `static java.util.prefs.Preferences userRoot()` |  |

---

### `interface PreferencesFactory`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.prefs.Preferences systemRoot()` |  |
| `java.util.prefs.Preferences userRoot()` |  |

---

## Package: `java.util.regex`

### `interface MatchResult`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int end()` |  |
| `int end(int)` |  |
| `String group()` |  |
| `String group(int)` |  |
| `int groupCount()` |  |
| `int start()` |  |
| `int start(int)` |  |

---

### `class final Matcher`

- **Implements:** `java.util.regex.MatchResult`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int end()` |  |
| `int end(int)` |  |
| `int end(@NonNull String)` |  |
| `boolean find()` |  |
| `boolean find(int)` |  |
| `int groupCount()` |  |
| `boolean hasAnchoringBounds()` |  |
| `boolean hasTransparentBounds()` |  |
| `boolean hitEnd()` |  |
| `boolean lookingAt()` |  |
| `boolean matches()` |  |
| `int regionEnd()` |  |
| `int regionStart()` |  |
| `boolean requireEnd()` |  |
| `int start()` |  |
| `int start(int)` |  |
| `int start(@NonNull String)` |  |

---

### `class final Pattern`

- **Implements:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CANON_EQ = 128` |  |
| `static final int CASE_INSENSITIVE = 2` |  |
| `static final int COMMENTS = 4` |  |
| `static final int DOTALL = 32` |  |
| `static final int LITERAL = 16` |  |
| `static final int MULTILINE = 8` |  |
| `static final int UNICODE_CASE = 64` |  |
| `static final int UNICODE_CHARACTER_CLASS = 256` |  |
| `static final int UNIX_LINES = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int flags()` |  |
| `static boolean matches(@NonNull String, @NonNull CharSequence)` |  |

---

### `class PatternSyntaxException`

- **Extends:** `java.lang.IllegalArgumentException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PatternSyntaxException(String, String, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getDescription()` |  |
| `int getIndex()` |  |
| `String getPattern()` |  |

---

## Package: `java.util.stream`

### `interface BaseStream<T, S`

- **Extends:** `java.util.stream.BaseStream<T, S>> extends java.lang.AutoCloseable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `boolean isParallel()` |  |
| `java.util.Iterator<T> iterator()` |  |
| `S onClose(Runnable)` |  |
| `S parallel()` |  |
| `S sequential()` |  |
| `java.util.Spliterator<T> spliterator()` |  |
| `S unordered()` |  |

---

### `interface Collector<T, A, R>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.function.BiConsumer<A,T> accumulator()` |  |
| `java.util.Set<java.util.stream.Collector.Characteristics> characteristics()` |  |
| `java.util.function.BinaryOperator<A> combiner()` |  |
| `java.util.function.Function<A,R> finisher()` |  |
| `static <T, R> java.util.stream.Collector<T,R,R> of(java.util.function.Supplier<R>, java.util.function.BiConsumer<R,T>, java.util.function.BinaryOperator<R>, java.util.stream.Collector.Characteristics...)` |  |
| `static <T, A, R> java.util.stream.Collector<T,A,R> of(java.util.function.Supplier<A>, java.util.function.BiConsumer<A,T>, java.util.function.BinaryOperator<A>, java.util.function.Function<A,R>, java.util.stream.Collector.Characteristics...)` |  |
| `java.util.function.Supplier<A> supplier()` |  |

---

### `enum Collector.Characteristics`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final java.util.stream.Collector.Characteristics CONCURRENT` |  |
| `static final java.util.stream.Collector.Characteristics IDENTITY_FINISH` |  |
| `static final java.util.stream.Collector.Characteristics UNORDERED` |  |

---

### `class final Collectors`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static <T> java.util.stream.Collector<T,?,java.lang.Double> averagingDouble(java.util.function.ToDoubleFunction<? super T>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.lang.Double> averagingInt(java.util.function.ToIntFunction<? super T>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.lang.Double> averagingLong(java.util.function.ToLongFunction<? super T>)` |  |
| `static <T, A, R, RR> java.util.stream.Collector<T,A,RR> collectingAndThen(java.util.stream.Collector<T,A,R>, java.util.function.Function<R,RR>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.lang.Long> counting()` |  |
| `static <T, K> java.util.stream.Collector<T,?,java.util.Map<K,java.util.List<T>>> groupingBy(java.util.function.Function<? super T,? extends K>)` |  |
| `static <T, K, A, D> java.util.stream.Collector<T,?,java.util.Map<K,D>> groupingBy(java.util.function.Function<? super T,? extends K>, java.util.stream.Collector<? super T,A,D>)` |  |
| `static <T, K, D, A, M extends java.util.Map<K, D>> java.util.stream.Collector<T,?,M> groupingBy(java.util.function.Function<? super T,? extends K>, java.util.function.Supplier<M>, java.util.stream.Collector<? super T,A,D>)` |  |
| `static <T, K> java.util.stream.Collector<T,?,java.util.concurrent.ConcurrentMap<K,java.util.List<T>>> groupingByConcurrent(java.util.function.Function<? super T,? extends K>)` |  |
| `static <T, K, A, D> java.util.stream.Collector<T,?,java.util.concurrent.ConcurrentMap<K,D>> groupingByConcurrent(java.util.function.Function<? super T,? extends K>, java.util.stream.Collector<? super T,A,D>)` |  |
| `static <T, K, A, D, M extends java.util.concurrent.ConcurrentMap<K, D>> java.util.stream.Collector<T,?,M> groupingByConcurrent(java.util.function.Function<? super T,? extends K>, java.util.function.Supplier<M>, java.util.stream.Collector<? super T,A,D>)` |  |
| `static java.util.stream.Collector<java.lang.CharSequence,?,java.lang.String> joining()` |  |
| `static java.util.stream.Collector<java.lang.CharSequence,?,java.lang.String> joining(CharSequence)` |  |
| `static java.util.stream.Collector<java.lang.CharSequence,?,java.lang.String> joining(CharSequence, CharSequence, CharSequence)` |  |
| `static <T, U, A, R> java.util.stream.Collector<T,?,R> mapping(java.util.function.Function<? super T,? extends U>, java.util.stream.Collector<? super U,A,R>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.util.Optional<T>> maxBy(java.util.Comparator<? super T>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.util.Optional<T>> minBy(java.util.Comparator<? super T>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.util.Map<java.lang.Boolean,java.util.List<T>>> partitioningBy(java.util.function.Predicate<? super T>)` |  |
| `static <T, D, A> java.util.stream.Collector<T,?,java.util.Map<java.lang.Boolean,D>> partitioningBy(java.util.function.Predicate<? super T>, java.util.stream.Collector<? super T,A,D>)` |  |
| `static <T> java.util.stream.Collector<T,?,T> reducing(T, java.util.function.BinaryOperator<T>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.util.Optional<T>> reducing(java.util.function.BinaryOperator<T>)` |  |
| `static <T, U> java.util.stream.Collector<T,?,U> reducing(U, java.util.function.Function<? super T,? extends U>, java.util.function.BinaryOperator<U>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.util.DoubleSummaryStatistics> summarizingDouble(java.util.function.ToDoubleFunction<? super T>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.util.IntSummaryStatistics> summarizingInt(java.util.function.ToIntFunction<? super T>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.util.LongSummaryStatistics> summarizingLong(java.util.function.ToLongFunction<? super T>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.lang.Double> summingDouble(java.util.function.ToDoubleFunction<? super T>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.lang.Integer> summingInt(java.util.function.ToIntFunction<? super T>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.lang.Long> summingLong(java.util.function.ToLongFunction<? super T>)` |  |
| `static <T, C extends java.util.Collection<T>> java.util.stream.Collector<T,?,C> toCollection(java.util.function.Supplier<C>)` |  |
| `static <T, K, U> java.util.stream.Collector<T,?,java.util.concurrent.ConcurrentMap<K,U>> toConcurrentMap(java.util.function.Function<? super T,? extends K>, java.util.function.Function<? super T,? extends U>)` |  |
| `static <T, K, U> java.util.stream.Collector<T,?,java.util.concurrent.ConcurrentMap<K,U>> toConcurrentMap(java.util.function.Function<? super T,? extends K>, java.util.function.Function<? super T,? extends U>, java.util.function.BinaryOperator<U>)` |  |
| `static <T, K, U, M extends java.util.concurrent.ConcurrentMap<K, U>> java.util.stream.Collector<T,?,M> toConcurrentMap(java.util.function.Function<? super T,? extends K>, java.util.function.Function<? super T,? extends U>, java.util.function.BinaryOperator<U>, java.util.function.Supplier<M>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.util.List<T>> toList()` |  |
| `static <T, K, U> java.util.stream.Collector<T,?,java.util.Map<K,U>> toMap(java.util.function.Function<? super T,? extends K>, java.util.function.Function<? super T,? extends U>)` |  |
| `static <T, K, U> java.util.stream.Collector<T,?,java.util.Map<K,U>> toMap(java.util.function.Function<? super T,? extends K>, java.util.function.Function<? super T,? extends U>, java.util.function.BinaryOperator<U>)` |  |
| `static <T, K, U, M extends java.util.Map<K, U>> java.util.stream.Collector<T,?,M> toMap(java.util.function.Function<? super T,? extends K>, java.util.function.Function<? super T,? extends U>, java.util.function.BinaryOperator<U>, java.util.function.Supplier<M>)` |  |
| `static <T> java.util.stream.Collector<T,?,java.util.Set<T>> toSet()` |  |

---

### `interface DoubleStream`

- **Extends:** `java.util.stream.BaseStream<java.lang.Double,java.util.stream.DoubleStream>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean allMatch(java.util.function.DoublePredicate)` |  |
| `boolean anyMatch(java.util.function.DoublePredicate)` |  |
| `java.util.OptionalDouble average()` |  |
| `java.util.stream.Stream<java.lang.Double> boxed()` |  |
| `static java.util.stream.DoubleStream.Builder builder()` |  |
| `<R> R collect(java.util.function.Supplier<R>, java.util.function.ObjDoubleConsumer<R>, java.util.function.BiConsumer<R,R>)` |  |
| `static java.util.stream.DoubleStream concat(java.util.stream.DoubleStream, java.util.stream.DoubleStream)` |  |
| `long count()` |  |
| `java.util.stream.DoubleStream distinct()` |  |
| `static java.util.stream.DoubleStream empty()` |  |
| `java.util.stream.DoubleStream filter(java.util.function.DoublePredicate)` |  |
| `java.util.OptionalDouble findAny()` |  |
| `java.util.OptionalDouble findFirst()` |  |
| `java.util.stream.DoubleStream flatMap(java.util.function.DoubleFunction<? extends java.util.stream.DoubleStream>)` |  |
| `void forEach(java.util.function.DoubleConsumer)` |  |
| `void forEachOrdered(java.util.function.DoubleConsumer)` |  |
| `static java.util.stream.DoubleStream generate(java.util.function.DoubleSupplier)` |  |
| `static java.util.stream.DoubleStream iterate(double, java.util.function.DoubleUnaryOperator)` |  |
| `java.util.PrimitiveIterator.OfDouble iterator()` |  |
| `java.util.stream.DoubleStream limit(long)` |  |
| `java.util.stream.DoubleStream map(java.util.function.DoubleUnaryOperator)` |  |
| `java.util.stream.IntStream mapToInt(java.util.function.DoubleToIntFunction)` |  |
| `java.util.stream.LongStream mapToLong(java.util.function.DoubleToLongFunction)` |  |
| `<U> java.util.stream.Stream<U> mapToObj(java.util.function.DoubleFunction<? extends U>)` |  |
| `java.util.OptionalDouble max()` |  |
| `java.util.OptionalDouble min()` |  |
| `boolean noneMatch(java.util.function.DoublePredicate)` |  |
| `static java.util.stream.DoubleStream of(double)` |  |
| `static java.util.stream.DoubleStream of(double...)` |  |
| `java.util.stream.DoubleStream parallel()` |  |
| `java.util.stream.DoubleStream peek(java.util.function.DoubleConsumer)` |  |
| `double reduce(double, java.util.function.DoubleBinaryOperator)` |  |
| `java.util.OptionalDouble reduce(java.util.function.DoubleBinaryOperator)` |  |
| `java.util.stream.DoubleStream sequential()` |  |
| `java.util.stream.DoubleStream skip(long)` |  |
| `java.util.stream.DoubleStream sorted()` |  |
| `java.util.Spliterator.OfDouble spliterator()` |  |
| `double sum()` |  |
| `java.util.DoubleSummaryStatistics summaryStatistics()` |  |
| `double[] toArray()` |  |

---

### `interface static DoubleStream.Builder`

- **Extends:** `java.util.function.DoubleConsumer`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.stream.DoubleStream.Builder add(double)` |  |
| `java.util.stream.DoubleStream build()` |  |

---

### `interface IntStream`

- **Extends:** `java.util.stream.BaseStream<java.lang.Integer,java.util.stream.IntStream>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean allMatch(java.util.function.IntPredicate)` |  |
| `boolean anyMatch(java.util.function.IntPredicate)` |  |
| `java.util.stream.DoubleStream asDoubleStream()` |  |
| `java.util.stream.LongStream asLongStream()` |  |
| `java.util.OptionalDouble average()` |  |
| `java.util.stream.Stream<java.lang.Integer> boxed()` |  |
| `static java.util.stream.IntStream.Builder builder()` |  |
| `<R> R collect(java.util.function.Supplier<R>, java.util.function.ObjIntConsumer<R>, java.util.function.BiConsumer<R,R>)` |  |
| `static java.util.stream.IntStream concat(java.util.stream.IntStream, java.util.stream.IntStream)` |  |
| `long count()` |  |
| `java.util.stream.IntStream distinct()` |  |
| `static java.util.stream.IntStream empty()` |  |
| `java.util.stream.IntStream filter(java.util.function.IntPredicate)` |  |
| `java.util.OptionalInt findAny()` |  |
| `java.util.OptionalInt findFirst()` |  |
| `java.util.stream.IntStream flatMap(java.util.function.IntFunction<? extends java.util.stream.IntStream>)` |  |
| `void forEach(java.util.function.IntConsumer)` |  |
| `void forEachOrdered(java.util.function.IntConsumer)` |  |
| `static java.util.stream.IntStream generate(java.util.function.IntSupplier)` |  |
| `static java.util.stream.IntStream iterate(int, java.util.function.IntUnaryOperator)` |  |
| `java.util.PrimitiveIterator.OfInt iterator()` |  |
| `java.util.stream.IntStream limit(long)` |  |
| `java.util.stream.IntStream map(java.util.function.IntUnaryOperator)` |  |
| `java.util.stream.DoubleStream mapToDouble(java.util.function.IntToDoubleFunction)` |  |
| `java.util.stream.LongStream mapToLong(java.util.function.IntToLongFunction)` |  |
| `<U> java.util.stream.Stream<U> mapToObj(java.util.function.IntFunction<? extends U>)` |  |
| `java.util.OptionalInt max()` |  |
| `java.util.OptionalInt min()` |  |
| `boolean noneMatch(java.util.function.IntPredicate)` |  |
| `static java.util.stream.IntStream of(int)` |  |
| `static java.util.stream.IntStream of(int...)` |  |
| `java.util.stream.IntStream parallel()` |  |
| `java.util.stream.IntStream peek(java.util.function.IntConsumer)` |  |
| `static java.util.stream.IntStream range(int, int)` |  |
| `static java.util.stream.IntStream rangeClosed(int, int)` |  |
| `int reduce(int, java.util.function.IntBinaryOperator)` |  |
| `java.util.OptionalInt reduce(java.util.function.IntBinaryOperator)` |  |
| `java.util.stream.IntStream sequential()` |  |
| `java.util.stream.IntStream skip(long)` |  |
| `java.util.stream.IntStream sorted()` |  |
| `java.util.Spliterator.OfInt spliterator()` |  |
| `int sum()` |  |
| `java.util.IntSummaryStatistics summaryStatistics()` |  |
| `int[] toArray()` |  |

---

### `interface static IntStream.Builder`

- **Extends:** `java.util.function.IntConsumer`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.stream.IntStream.Builder add(int)` |  |
| `java.util.stream.IntStream build()` |  |

---

### `interface LongStream`

- **Extends:** `java.util.stream.BaseStream<java.lang.Long,java.util.stream.LongStream>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean allMatch(java.util.function.LongPredicate)` |  |
| `boolean anyMatch(java.util.function.LongPredicate)` |  |
| `java.util.stream.DoubleStream asDoubleStream()` |  |
| `java.util.OptionalDouble average()` |  |
| `java.util.stream.Stream<java.lang.Long> boxed()` |  |
| `static java.util.stream.LongStream.Builder builder()` |  |
| `<R> R collect(java.util.function.Supplier<R>, java.util.function.ObjLongConsumer<R>, java.util.function.BiConsumer<R,R>)` |  |
| `static java.util.stream.LongStream concat(java.util.stream.LongStream, java.util.stream.LongStream)` |  |
| `long count()` |  |
| `java.util.stream.LongStream distinct()` |  |
| `static java.util.stream.LongStream empty()` |  |
| `java.util.stream.LongStream filter(java.util.function.LongPredicate)` |  |
| `java.util.OptionalLong findAny()` |  |
| `java.util.OptionalLong findFirst()` |  |
| `java.util.stream.LongStream flatMap(java.util.function.LongFunction<? extends java.util.stream.LongStream>)` |  |
| `void forEach(java.util.function.LongConsumer)` |  |
| `void forEachOrdered(java.util.function.LongConsumer)` |  |
| `static java.util.stream.LongStream generate(java.util.function.LongSupplier)` |  |
| `static java.util.stream.LongStream iterate(long, java.util.function.LongUnaryOperator)` |  |
| `java.util.PrimitiveIterator.OfLong iterator()` |  |
| `java.util.stream.LongStream limit(long)` |  |
| `java.util.stream.LongStream map(java.util.function.LongUnaryOperator)` |  |
| `java.util.stream.DoubleStream mapToDouble(java.util.function.LongToDoubleFunction)` |  |
| `java.util.stream.IntStream mapToInt(java.util.function.LongToIntFunction)` |  |
| `<U> java.util.stream.Stream<U> mapToObj(java.util.function.LongFunction<? extends U>)` |  |
| `java.util.OptionalLong max()` |  |
| `java.util.OptionalLong min()` |  |
| `boolean noneMatch(java.util.function.LongPredicate)` |  |
| `static java.util.stream.LongStream of(long)` |  |
| `static java.util.stream.LongStream of(long...)` |  |
| `java.util.stream.LongStream parallel()` |  |
| `java.util.stream.LongStream peek(java.util.function.LongConsumer)` |  |
| `static java.util.stream.LongStream range(long, long)` |  |
| `static java.util.stream.LongStream rangeClosed(long, long)` |  |
| `long reduce(long, java.util.function.LongBinaryOperator)` |  |
| `java.util.OptionalLong reduce(java.util.function.LongBinaryOperator)` |  |
| `java.util.stream.LongStream sequential()` |  |
| `java.util.stream.LongStream skip(long)` |  |
| `java.util.stream.LongStream sorted()` |  |
| `java.util.Spliterator.OfLong spliterator()` |  |
| `long sum()` |  |
| `java.util.LongSummaryStatistics summaryStatistics()` |  |
| `long[] toArray()` |  |

---

### `interface static LongStream.Builder`

- **Extends:** `java.util.function.LongConsumer`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.stream.LongStream.Builder add(long)` |  |
| `java.util.stream.LongStream build()` |  |

---

### `interface Stream<T>`

- **Extends:** `java.util.stream.BaseStream<T,java.util.stream.Stream<T>>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean allMatch(java.util.function.Predicate<? super T>)` |  |
| `boolean anyMatch(java.util.function.Predicate<? super T>)` |  |
| `static <T> java.util.stream.Stream.Builder<T> builder()` |  |
| `<R> R collect(java.util.function.Supplier<R>, java.util.function.BiConsumer<R,? super T>, java.util.function.BiConsumer<R,R>)` |  |
| `<R, A> R collect(java.util.stream.Collector<? super T,A,R>)` |  |
| `static <T> java.util.stream.Stream<T> concat(java.util.stream.Stream<? extends T>, java.util.stream.Stream<? extends T>)` |  |
| `long count()` |  |
| `java.util.stream.Stream<T> distinct()` |  |
| `static <T> java.util.stream.Stream<T> empty()` |  |
| `java.util.stream.Stream<T> filter(java.util.function.Predicate<? super T>)` |  |
| `java.util.Optional<T> findAny()` |  |
| `java.util.Optional<T> findFirst()` |  |
| `<R> java.util.stream.Stream<R> flatMap(java.util.function.Function<? super T,? extends java.util.stream.Stream<? extends R>>)` |  |
| `java.util.stream.DoubleStream flatMapToDouble(java.util.function.Function<? super T,? extends java.util.stream.DoubleStream>)` |  |
| `java.util.stream.IntStream flatMapToInt(java.util.function.Function<? super T,? extends java.util.stream.IntStream>)` |  |
| `java.util.stream.LongStream flatMapToLong(java.util.function.Function<? super T,? extends java.util.stream.LongStream>)` |  |
| `void forEach(java.util.function.Consumer<? super T>)` |  |
| `void forEachOrdered(java.util.function.Consumer<? super T>)` |  |
| `static <T> java.util.stream.Stream<T> generate(java.util.function.Supplier<T>)` |  |
| `static <T> java.util.stream.Stream<T> iterate(T, java.util.function.UnaryOperator<T>)` |  |
| `java.util.stream.Stream<T> limit(long)` |  |
| `<R> java.util.stream.Stream<R> map(java.util.function.Function<? super T,? extends R>)` |  |
| `java.util.stream.DoubleStream mapToDouble(java.util.function.ToDoubleFunction<? super T>)` |  |
| `java.util.stream.IntStream mapToInt(java.util.function.ToIntFunction<? super T>)` |  |
| `java.util.stream.LongStream mapToLong(java.util.function.ToLongFunction<? super T>)` |  |
| `java.util.Optional<T> max(java.util.Comparator<? super T>)` |  |
| `java.util.Optional<T> min(java.util.Comparator<? super T>)` |  |
| `boolean noneMatch(java.util.function.Predicate<? super T>)` |  |
| `static <T> java.util.stream.Stream<T> of(T)` |  |
| `java.util.stream.Stream<T> peek(java.util.function.Consumer<? super T>)` |  |
| `T reduce(T, java.util.function.BinaryOperator<T>)` |  |
| `java.util.Optional<T> reduce(java.util.function.BinaryOperator<T>)` |  |
| `<U> U reduce(U, java.util.function.BiFunction<U,? super T,U>, java.util.function.BinaryOperator<U>)` |  |
| `java.util.stream.Stream<T> skip(long)` |  |
| `java.util.stream.Stream<T> sorted()` |  |
| `java.util.stream.Stream<T> sorted(java.util.Comparator<? super T>)` |  |
| `Object[] toArray()` |  |
| `<A> A[] toArray(java.util.function.IntFunction<A[]>)` |  |

---

### `interface static Stream.Builder<T>`

- **Extends:** `java.util.function.Consumer<T>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default java.util.stream.Stream.Builder<T> add(T)` |  |
| `java.util.stream.Stream<T> build()` |  |

---

### `class final StreamSupport`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.stream.DoubleStream doubleStream(java.util.Spliterator.OfDouble, boolean)` |  |
| `static java.util.stream.DoubleStream doubleStream(java.util.function.Supplier<? extends java.util.Spliterator.OfDouble>, int, boolean)` |  |
| `static java.util.stream.IntStream intStream(java.util.Spliterator.OfInt, boolean)` |  |
| `static java.util.stream.IntStream intStream(java.util.function.Supplier<? extends java.util.Spliterator.OfInt>, int, boolean)` |  |
| `static java.util.stream.LongStream longStream(java.util.Spliterator.OfLong, boolean)` |  |
| `static java.util.stream.LongStream longStream(java.util.function.Supplier<? extends java.util.Spliterator.OfLong>, int, boolean)` |  |
| `static <T> java.util.stream.Stream<T> stream(java.util.Spliterator<T>, boolean)` |  |
| `static <T> java.util.stream.Stream<T> stream(java.util.function.Supplier<? extends java.util.Spliterator<T>>, int, boolean)` |  |

---

## Package: `java.util.zip`

### `class Adler32`

- **Implements:** `java.util.zip.Checksum`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Adler32()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getValue()` |  |
| `void reset()` |  |
| `void update(int)` |  |
| `void update(byte[], int, int)` |  |
| `void update(byte[])` |  |
| `void update(java.nio.ByteBuffer)` |  |

---

### `class CRC32`

- **Implements:** `java.util.zip.Checksum`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CRC32()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getValue()` |  |
| `void reset()` |  |
| `void update(int)` |  |
| `void update(byte[], int, int)` |  |
| `void update(byte[])` |  |
| `void update(java.nio.ByteBuffer)` |  |

---

### `class CheckedInputStream`

- **Extends:** `java.io.FilterInputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CheckedInputStream(java.io.InputStream, java.util.zip.Checksum)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.zip.Checksum getChecksum()` |  |

---

### `class CheckedOutputStream`

- **Extends:** `java.io.FilterOutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CheckedOutputStream(java.io.OutputStream, java.util.zip.Checksum)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.zip.Checksum getChecksum()` |  |

---

### `interface Checksum`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getValue()` |  |
| `void reset()` |  |
| `void update(int)` |  |
| `void update(byte[], int, int)` |  |

---

### `class DataFormatException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DataFormatException()` |  |
| `DataFormatException(String)` |  |

---

### `class Deflater`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Deflater(int, boolean)` |  |
| `Deflater(int)` |  |
| `Deflater()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BEST_COMPRESSION = 9` |  |
| `static final int BEST_SPEED = 1` |  |
| `static final int DEFAULT_COMPRESSION = -1` |  |
| `static final int DEFAULT_STRATEGY = 0` |  |
| `static final int DEFLATED = 8` |  |
| `static final int FILTERED = 1` |  |
| `static final int FULL_FLUSH = 3` |  |
| `static final int HUFFMAN_ONLY = 2` |  |
| `static final int NO_COMPRESSION = 0` |  |
| `static final int NO_FLUSH = 0` |  |
| `static final int SYNC_FLUSH = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int deflate(byte[], int, int)` |  |
| `int deflate(byte[])` |  |
| `int deflate(byte[], int, int, int)` |  |
| `void end()` |  |
| `void finalize()` |  |
| `void finish()` |  |
| `boolean finished()` |  |
| `int getAdler()` |  |
| `long getBytesRead()` |  |
| `long getBytesWritten()` |  |
| `int getTotalIn()` |  |
| `int getTotalOut()` |  |
| `boolean needsInput()` |  |
| `void reset()` |  |
| `void setDictionary(byte[], int, int)` |  |
| `void setDictionary(byte[])` |  |
| `void setInput(byte[], int, int)` |  |
| `void setInput(byte[])` |  |
| `void setLevel(int)` |  |
| `void setStrategy(int)` |  |

---

### `class DeflaterInputStream`

- **Extends:** `java.io.FilterInputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DeflaterInputStream(java.io.InputStream)` |  |
| `DeflaterInputStream(java.io.InputStream, java.util.zip.Deflater)` |  |
| `DeflaterInputStream(java.io.InputStream, java.util.zip.Deflater, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final byte[] buf` |  |
| `final java.util.zip.Deflater def` |  |

---

### `class DeflaterOutputStream`

- **Extends:** `java.io.FilterOutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DeflaterOutputStream(java.io.OutputStream, java.util.zip.Deflater, int, boolean)` |  |
| `DeflaterOutputStream(java.io.OutputStream, java.util.zip.Deflater, int)` |  |
| `DeflaterOutputStream(java.io.OutputStream, java.util.zip.Deflater, boolean)` |  |
| `DeflaterOutputStream(java.io.OutputStream, java.util.zip.Deflater)` |  |
| `DeflaterOutputStream(java.io.OutputStream, boolean)` |  |
| `DeflaterOutputStream(java.io.OutputStream)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] buf` |  |
| `java.util.zip.Deflater def` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void deflate() throws java.io.IOException` |  |
| `void finish() throws java.io.IOException` |  |

---

### `class GZIPInputStream`

- **Extends:** `java.util.zip.InflaterInputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GZIPInputStream(java.io.InputStream, int) throws java.io.IOException` |  |
| `GZIPInputStream(java.io.InputStream) throws java.io.IOException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int GZIP_MAGIC = 35615` |  |
| `java.util.zip.CRC32 crc` |  |
| `boolean eos` |  |

---

### `class GZIPOutputStream`

- **Extends:** `java.util.zip.DeflaterOutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GZIPOutputStream(java.io.OutputStream, int) throws java.io.IOException` |  |
| `GZIPOutputStream(java.io.OutputStream, int, boolean) throws java.io.IOException` |  |
| `GZIPOutputStream(java.io.OutputStream) throws java.io.IOException` |  |
| `GZIPOutputStream(java.io.OutputStream, boolean) throws java.io.IOException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.zip.CRC32 crc` |  |

---

### `class Inflater`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Inflater(boolean)` |  |
| `Inflater()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void end()` |  |
| `void finalize()` |  |
| `boolean finished()` |  |
| `int getAdler()` |  |
| `long getBytesRead()` |  |
| `long getBytesWritten()` |  |
| `int getRemaining()` |  |
| `int getTotalIn()` |  |
| `int getTotalOut()` |  |
| `int inflate(byte[], int, int) throws java.util.zip.DataFormatException` |  |
| `int inflate(byte[]) throws java.util.zip.DataFormatException` |  |
| `boolean needsDictionary()` |  |
| `boolean needsInput()` |  |
| `void reset()` |  |
| `void setDictionary(byte[], int, int)` |  |
| `void setDictionary(byte[])` |  |
| `void setInput(byte[], int, int)` |  |
| `void setInput(byte[])` |  |

---

### `class InflaterInputStream`

- **Extends:** `java.io.FilterInputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InflaterInputStream(java.io.InputStream, java.util.zip.Inflater, int)` |  |
| `InflaterInputStream(java.io.InputStream, java.util.zip.Inflater)` |  |
| `InflaterInputStream(java.io.InputStream)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] buf` |  |
| `java.util.zip.Inflater inf` |  |
| `int len` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void fill() throws java.io.IOException` |  |

---

### `class InflaterOutputStream`

- **Extends:** `java.io.FilterOutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InflaterOutputStream(java.io.OutputStream)` |  |
| `InflaterOutputStream(java.io.OutputStream, java.util.zip.Inflater)` |  |
| `InflaterOutputStream(java.io.OutputStream, java.util.zip.Inflater, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final byte[] buf` |  |
| `final java.util.zip.Inflater inf` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finish() throws java.io.IOException` |  |

---

### `class ZipEntry`

- **Implements:** `java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ZipEntry(String)` |  |
| `ZipEntry(java.util.zip.ZipEntry)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CENATT = 36` |  |
| `static final int CENATX = 38` |  |
| `static final int CENCOM = 32` |  |
| `static final int CENCRC = 16` |  |
| `static final int CENDSK = 34` |  |
| `static final int CENEXT = 30` |  |
| `static final int CENFLG = 8` |  |
| `static final int CENHDR = 46` |  |
| `static final int CENHOW = 10` |  |
| `static final int CENLEN = 24` |  |
| `static final int CENNAM = 28` |  |
| `static final int CENOFF = 42` |  |
| `static final long CENSIG = 33639248L` |  |
| `static final int CENSIZ = 20` |  |
| `static final int CENTIM = 12` |  |
| `static final int CENVEM = 4` |  |
| `static final int CENVER = 6` |  |
| `static final int DEFLATED = 8` |  |
| `static final int ENDCOM = 20` |  |
| `static final int ENDHDR = 22` |  |
| `static final int ENDOFF = 16` |  |
| `static final long ENDSIG = 101010256L` |  |
| `static final int ENDSIZ = 12` |  |
| `static final int ENDSUB = 8` |  |
| `static final int ENDTOT = 10` |  |
| `static final int EXTCRC = 4` |  |
| `static final int EXTHDR = 16` |  |
| `static final int EXTLEN = 12` |  |
| `static final long EXTSIG = 134695760L` |  |
| `static final int EXTSIZ = 8` |  |
| `static final int LOCCRC = 14` |  |
| `static final int LOCEXT = 28` |  |
| `static final int LOCFLG = 6` |  |
| `static final int LOCHDR = 30` |  |
| `static final int LOCHOW = 8` |  |
| `static final int LOCLEN = 22` |  |
| `static final int LOCNAM = 26` |  |
| `static final long LOCSIG = 67324752L` |  |
| `static final int LOCSIZ = 18` |  |
| `static final int LOCTIM = 10` |  |
| `static final int LOCVER = 4` |  |
| `static final int STORED = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `String getComment()` |  |
| `long getCompressedSize()` |  |
| `long getCrc()` |  |
| `java.nio.file.attribute.FileTime getCreationTime()` |  |
| `byte[] getExtra()` |  |
| `java.nio.file.attribute.FileTime getLastAccessTime()` |  |
| `java.nio.file.attribute.FileTime getLastModifiedTime()` |  |
| `int getMethod()` |  |
| `String getName()` |  |
| `long getSize()` |  |
| `long getTime()` |  |
| `boolean isDirectory()` |  |
| `void setComment(String)` |  |
| `void setCompressedSize(long)` |  |
| `void setCrc(long)` |  |
| `java.util.zip.ZipEntry setCreationTime(java.nio.file.attribute.FileTime)` |  |
| `void setExtra(byte[])` |  |
| `java.util.zip.ZipEntry setLastAccessTime(java.nio.file.attribute.FileTime)` |  |
| `java.util.zip.ZipEntry setLastModifiedTime(java.nio.file.attribute.FileTime)` |  |
| `void setMethod(int)` |  |
| `void setSize(long)` |  |
| `void setTime(long)` |  |

---

### `class ZipError`

- **Extends:** `java.lang.InternalError`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ZipError(String)` |  |

---

### `class ZipException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ZipException()` |  |
| `ZipException(String)` |  |

---

### `class ZipFile`

- **Implements:** `java.io.Closeable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ZipFile(String) throws java.io.IOException` |  |
| `ZipFile(java.io.File, int) throws java.io.IOException` |  |
| `ZipFile(java.io.File) throws java.io.IOException, java.util.zip.ZipException` |  |
| `ZipFile(java.io.File, int, java.nio.charset.Charset) throws java.io.IOException` |  |
| `ZipFile(String, java.nio.charset.Charset) throws java.io.IOException` |  |
| `ZipFile(java.io.File, java.nio.charset.Charset) throws java.io.IOException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CENATT = 36` |  |
| `static final int CENATX = 38` |  |
| `static final int CENCOM = 32` |  |
| `static final int CENCRC = 16` |  |
| `static final int CENDSK = 34` |  |
| `static final int CENEXT = 30` |  |
| `static final int CENFLG = 8` |  |
| `static final int CENHDR = 46` |  |
| `static final int CENHOW = 10` |  |
| `static final int CENLEN = 24` |  |
| `static final int CENNAM = 28` |  |
| `static final int CENOFF = 42` |  |
| `static final long CENSIG = 33639248L` |  |
| `static final int CENSIZ = 20` |  |
| `static final int CENTIM = 12` |  |
| `static final int CENVEM = 4` |  |
| `static final int CENVER = 6` |  |
| `static final int ENDCOM = 20` |  |
| `static final int ENDHDR = 22` |  |
| `static final int ENDOFF = 16` |  |
| `static final long ENDSIG = 101010256L` |  |
| `static final int ENDSIZ = 12` |  |
| `static final int ENDSUB = 8` |  |
| `static final int ENDTOT = 10` |  |
| `static final int EXTCRC = 4` |  |
| `static final int EXTHDR = 16` |  |
| `static final int EXTLEN = 12` |  |
| `static final long EXTSIG = 134695760L` |  |
| `static final int EXTSIZ = 8` |  |
| `static final int LOCCRC = 14` |  |
| `static final int LOCEXT = 28` |  |
| `static final int LOCFLG = 6` |  |
| `static final int LOCHDR = 30` |  |
| `static final int LOCHOW = 8` |  |
| `static final int LOCLEN = 22` |  |
| `static final int LOCNAM = 26` |  |
| `static final long LOCSIG = 67324752L` |  |
| `static final int LOCSIZ = 18` |  |
| `static final int LOCTIM = 10` |  |
| `static final int LOCVER = 4` |  |
| `static final int OPEN_DELETE = 4` |  |
| `static final int OPEN_READ = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `java.util.Enumeration<? extends java.util.zip.ZipEntry> entries()` |  |
| `void finalize() throws java.io.IOException` |  |
| `String getComment()` |  |
| `java.util.zip.ZipEntry getEntry(String)` |  |
| `java.io.InputStream getInputStream(java.util.zip.ZipEntry) throws java.io.IOException` |  |
| `String getName()` |  |
| `int size()` |  |
| `java.util.stream.Stream<? extends java.util.zip.ZipEntry> stream()` |  |

---

### `class ZipInputStream`

- **Extends:** `java.util.zip.InflaterInputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ZipInputStream(java.io.InputStream)` |  |
| `ZipInputStream(java.io.InputStream, java.nio.charset.Charset)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CENATT = 36` |  |
| `static final int CENATX = 38` |  |
| `static final int CENCOM = 32` |  |
| `static final int CENCRC = 16` |  |
| `static final int CENDSK = 34` |  |
| `static final int CENEXT = 30` |  |
| `static final int CENFLG = 8` |  |
| `static final int CENHDR = 46` |  |
| `static final int CENHOW = 10` |  |
| `static final int CENLEN = 24` |  |
| `static final int CENNAM = 28` |  |
| `static final int CENOFF = 42` |  |
| `static final long CENSIG = 33639248L` |  |
| `static final int CENSIZ = 20` |  |
| `static final int CENTIM = 12` |  |
| `static final int CENVEM = 4` |  |
| `static final int CENVER = 6` |  |
| `static final int ENDCOM = 20` |  |
| `static final int ENDHDR = 22` |  |
| `static final int ENDOFF = 16` |  |
| `static final long ENDSIG = 101010256L` |  |
| `static final int ENDSIZ = 12` |  |
| `static final int ENDSUB = 8` |  |
| `static final int ENDTOT = 10` |  |
| `static final int EXTCRC = 4` |  |
| `static final int EXTHDR = 16` |  |
| `static final int EXTLEN = 12` |  |
| `static final long EXTSIG = 134695760L` |  |
| `static final int EXTSIZ = 8` |  |
| `static final int LOCCRC = 14` |  |
| `static final int LOCEXT = 28` |  |
| `static final int LOCFLG = 6` |  |
| `static final int LOCHDR = 30` |  |
| `static final int LOCHOW = 8` |  |
| `static final int LOCLEN = 22` |  |
| `static final int LOCNAM = 26` |  |
| `static final long LOCSIG = 67324752L` |  |
| `static final int LOCSIZ = 18` |  |
| `static final int LOCTIM = 10` |  |
| `static final int LOCVER = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void closeEntry() throws java.io.IOException` |  |
| `java.util.zip.ZipEntry createZipEntry(String)` |  |
| `java.util.zip.ZipEntry getNextEntry() throws java.io.IOException` |  |

---

### `class ZipOutputStream`

- **Extends:** `java.util.zip.DeflaterOutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ZipOutputStream(java.io.OutputStream)` |  |
| `ZipOutputStream(java.io.OutputStream, java.nio.charset.Charset)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CENATT = 36` |  |
| `static final int CENATX = 38` |  |
| `static final int CENCOM = 32` |  |
| `static final int CENCRC = 16` |  |
| `static final int CENDSK = 34` |  |
| `static final int CENEXT = 30` |  |
| `static final int CENFLG = 8` |  |
| `static final int CENHDR = 46` |  |
| `static final int CENHOW = 10` |  |
| `static final int CENLEN = 24` |  |
| `static final int CENNAM = 28` |  |
| `static final int CENOFF = 42` |  |
| `static final long CENSIG = 33639248L` |  |
| `static final int CENSIZ = 20` |  |
| `static final int CENTIM = 12` |  |
| `static final int CENVEM = 4` |  |
| `static final int CENVER = 6` |  |
| `static final int DEFLATED = 8` |  |
| `static final int ENDCOM = 20` |  |
| `static final int ENDHDR = 22` |  |
| `static final int ENDOFF = 16` |  |
| `static final long ENDSIG = 101010256L` |  |
| `static final int ENDSIZ = 12` |  |
| `static final int ENDSUB = 8` |  |
| `static final int ENDTOT = 10` |  |
| `static final int EXTCRC = 4` |  |
| `static final int EXTHDR = 16` |  |
| `static final int EXTLEN = 12` |  |
| `static final long EXTSIG = 134695760L` |  |
| `static final int EXTSIZ = 8` |  |
| `static final int LOCCRC = 14` |  |
| `static final int LOCEXT = 28` |  |
| `static final int LOCFLG = 6` |  |
| `static final int LOCHDR = 30` |  |
| `static final int LOCHOW = 8` |  |
| `static final int LOCLEN = 22` |  |
| `static final int LOCNAM = 26` |  |
| `static final long LOCSIG = 67324752L` |  |
| `static final int LOCSIZ = 18` |  |
| `static final int LOCTIM = 10` |  |
| `static final int LOCVER = 4` |  |
| `static final int STORED = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void closeEntry() throws java.io.IOException` |  |
| `void putNextEntry(java.util.zip.ZipEntry) throws java.io.IOException` |  |
| `void setComment(String)` |  |
| `void setLevel(int)` |  |
| `void setMethod(int)` |  |

---

