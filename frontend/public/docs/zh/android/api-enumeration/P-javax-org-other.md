# Android 11 (API 30) Public API Enumeration: Javax Org Other

Generated from `frameworks/base/api/current.txt`

## 概要

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `dalvik.annotation` | 0 | 0 | 0 | 0 |
| `dalvik.bytecode` | 2 | 0 | 265 | 0 |
| `dalvik.system` | 6 | 2 | 0 | 10 |
| `javax.crypto` | 24 | 158 | 9 | 34 |
| `javax.crypto.interfaces` | 4 | 6 | 3 | 0 |
| `javax.crypto.spec` | 16 | 42 | 4 | 29 |
| `javax.microedition.khronos.egl` | 7 | 25 | 72 | 4 |
| `javax.microedition.khronos.opengles` | 6 | 267 | 461 | 0 |
| `javax.net` | 2 | 11 | 0 | 2 |
| `javax.net.ssl` | 43 | 238 | 2 | 45 |
| `javax.security.auth` | 6 | 21 | 0 | 8 |
| `javax.security.auth.callback` | 4 | 7 | 0 | 3 |
| `javax.security.auth.login` | 1 | 0 | 0 | 2 |
| `javax.security.auth.x500` | 1 | 4 | 3 | 4 |
| `javax.security.cert` | 7 | 18 | 0 | 12 |
| `javax.sql` | 15 | 164 | 0 | 5 |
| `javax.xml` | 1 | 0 | 12 | 0 |
| `javax.xml.datatype` | 6 | 84 | 38 | 7 |
| `javax.xml.namespace` | 2 | 9 | 0 | 3 |
| `javax.xml.parsers` | 6 | 70 | 0 | 10 |
| `javax.xml.transform` | 12 | 47 | 12 | 17 |
| `javax.xml.transform.dom` | 3 | 11 | 2 | 8 |
| `javax.xml.transform.sax` | 5 | 26 | 4 | 6 |
| `javax.xml.transform.stream` | 2 | 16 | 2 | 12 |
| `javax.xml.validation` | 6 | 45 | 0 | 6 |
| `javax.xml.xpath` | 11 | 28 | 8 | 9 |
| `org.apache.http.conn` | 1 | 0 | 0 | 0 |
| `org.apache.http.conn.scheme` | 3 | 0 | 0 | 0 |
| `org.apache.http.conn.ssl` | 6 | 0 | 0 | 0 |
| `org.apache.http.params` | 3 | 0 | 0 | 0 |
| `org.json` | 5 | 75 | 0 | 15 |
| `org.w3c.dom` | 28 | 171 | 48 | 1 |
| `org.w3c.dom.ls` | 8 | 47 | 14 | 1 |
| `org.xml.sax` | 17 | 64 | 0 | 16 |
| `org.xml.sax.ext` | 8 | 46 | 0 | 5 |
| `org.xml.sax.helpers` | 10 | 134 | 2 | 12 |
| `org.xmlpull.v1` | 4 | 75 | 25 | 3 |
| `org.xmlpull.v1.sax2` | 1 | 32 | 11 | 2 |
| **TOTAL** | **292** | **1943** | **997** | **291** |

---

## Package: `dalvik.annotation`

## Package: `dalvik.bytecode`

### `class final OpcodeInfo`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int MAXIMUM_PACKED_VALUE` |  |
| `static final int MAXIMUM_VALUE` |  |

---

### `interface Opcodes`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int OP_ADD_DOUBLE = 171` |  |
| `static final int OP_ADD_DOUBLE_2ADDR = 203` |  |
| `static final int OP_ADD_FLOAT = 166` |  |
| `static final int OP_ADD_FLOAT_2ADDR = 198` |  |
| `static final int OP_ADD_INT = 144` |  |
| `static final int OP_ADD_INT_2ADDR = 176` |  |
| `static final int OP_ADD_INT_LIT16 = 208` |  |
| `static final int OP_ADD_INT_LIT8 = 216` |  |
| `static final int OP_ADD_LONG = 155` |  |
| `static final int OP_ADD_LONG_2ADDR = 187` |  |
| `static final int OP_AGET = 68` |  |
| `static final int OP_AGET_BOOLEAN = 71` |  |
| `static final int OP_AGET_BYTE = 72` |  |
| `static final int OP_AGET_CHAR = 73` |  |
| `static final int OP_AGET_OBJECT = 70` |  |
| `static final int OP_AGET_SHORT = 74` |  |
| `static final int OP_AGET_WIDE = 69` |  |
| `static final int OP_AND_INT = 149` |  |
| `static final int OP_AND_INT_2ADDR = 181` |  |
| `static final int OP_AND_INT_LIT16 = 213` |  |
| `static final int OP_AND_INT_LIT8 = 221` |  |
| `static final int OP_AND_LONG = 160` |  |
| `static final int OP_AND_LONG_2ADDR = 192` |  |
| `static final int OP_APUT = 75` |  |
| `static final int OP_APUT_BOOLEAN = 78` |  |
| `static final int OP_APUT_BYTE = 79` |  |
| `static final int OP_APUT_CHAR = 80` |  |
| `static final int OP_APUT_OBJECT = 77` |  |
| `static final int OP_APUT_SHORT = 81` |  |
| `static final int OP_APUT_WIDE = 76` |  |
| `static final int OP_ARRAY_LENGTH = 33` |  |
| `static final int OP_CHECK_CAST = 31` |  |
| `static final int OP_CHECK_CAST_JUMBO = 511` |  |
| `static final int OP_CMPG_DOUBLE = 48` |  |
| `static final int OP_CMPG_FLOAT = 46` |  |
| `static final int OP_CMPL_DOUBLE = 47` |  |
| `static final int OP_CMPL_FLOAT = 45` |  |
| `static final int OP_CMP_LONG = 49` |  |
| `static final int OP_CONST = 20` |  |
| `static final int OP_CONST_16 = 19` |  |
| `static final int OP_CONST_4 = 18` |  |
| `static final int OP_CONST_CLASS = 28` |  |
| `static final int OP_CONST_CLASS_JUMBO = 255` |  |
| `static final int OP_CONST_HIGH16 = 21` |  |
| `static final int OP_CONST_METHOD_HANDLE = 254` |  |
| `static final int OP_CONST_METHOD_TYPE = 255` |  |
| `static final int OP_CONST_STRING = 26` |  |
| `static final int OP_CONST_STRING_JUMBO = 27` |  |
| `static final int OP_CONST_WIDE = 24` |  |
| `static final int OP_CONST_WIDE_16 = 22` |  |
| `static final int OP_CONST_WIDE_32 = 23` |  |
| `static final int OP_CONST_WIDE_HIGH16 = 25` |  |
| `static final int OP_DIV_DOUBLE = 174` |  |
| `static final int OP_DIV_DOUBLE_2ADDR = 206` |  |
| `static final int OP_DIV_FLOAT = 169` |  |
| `static final int OP_DIV_FLOAT_2ADDR = 201` |  |
| `static final int OP_DIV_INT = 147` |  |
| `static final int OP_DIV_INT_2ADDR = 179` |  |
| `static final int OP_DIV_INT_LIT16 = 211` |  |
| `static final int OP_DIV_INT_LIT8 = 219` |  |
| `static final int OP_DIV_LONG = 158` |  |
| `static final int OP_DIV_LONG_2ADDR = 190` |  |
| `static final int OP_DOUBLE_TO_FLOAT = 140` |  |
| `static final int OP_DOUBLE_TO_INT = 138` |  |
| `static final int OP_DOUBLE_TO_LONG = 139` |  |
| `static final int OP_FILLED_NEW_ARRAY = 36` |  |
| `static final int OP_FILLED_NEW_ARRAY_JUMBO = 1535` |  |
| `static final int OP_FILLED_NEW_ARRAY_RANGE = 37` |  |
| `static final int OP_FILL_ARRAY_DATA = 38` |  |
| `static final int OP_FLOAT_TO_DOUBLE = 137` |  |
| `static final int OP_FLOAT_TO_INT = 135` |  |
| `static final int OP_FLOAT_TO_LONG = 136` |  |
| `static final int OP_GOTO = 40` |  |
| `static final int OP_GOTO_16 = 41` |  |
| `static final int OP_GOTO_32 = 42` |  |
| `static final int OP_IF_EQ = 50` |  |
| `static final int OP_IF_EQZ = 56` |  |
| `static final int OP_IF_GE = 53` |  |
| `static final int OP_IF_GEZ = 59` |  |
| `static final int OP_IF_GT = 54` |  |
| `static final int OP_IF_GTZ = 60` |  |
| `static final int OP_IF_LE = 55` |  |
| `static final int OP_IF_LEZ = 61` |  |
| `static final int OP_IF_LT = 52` |  |
| `static final int OP_IF_LTZ = 58` |  |
| `static final int OP_IF_NE = 51` |  |
| `static final int OP_IF_NEZ = 57` |  |
| `static final int OP_IGET = 82` |  |
| `static final int OP_IGET_BOOLEAN = 85` |  |
| `static final int OP_IGET_BOOLEAN_JUMBO = 2559` |  |
| `static final int OP_IGET_BYTE = 86` |  |
| `static final int OP_IGET_BYTE_JUMBO = 2815` |  |
| `static final int OP_IGET_CHAR = 87` |  |
| `static final int OP_IGET_CHAR_JUMBO = 3071` |  |
| `static final int OP_IGET_JUMBO = 1791` |  |
| `static final int OP_IGET_OBJECT = 84` |  |
| `static final int OP_IGET_OBJECT_JUMBO = 2303` |  |
| `static final int OP_IGET_SHORT = 88` |  |
| `static final int OP_IGET_SHORT_JUMBO = 3327` |  |
| `static final int OP_IGET_WIDE = 83` |  |
| `static final int OP_IGET_WIDE_JUMBO = 2047` |  |
| `static final int OP_INSTANCE_OF = 32` |  |
| `static final int OP_INSTANCE_OF_JUMBO = 767` |  |
| `static final int OP_INT_TO_BYTE = 141` |  |
| `static final int OP_INT_TO_CHAR = 142` |  |
| `static final int OP_INT_TO_DOUBLE = 131` |  |
| `static final int OP_INT_TO_FLOAT = 130` |  |
| `static final int OP_INT_TO_LONG = 129` |  |
| `static final int OP_INT_TO_SHORT = 143` |  |
| `static final int OP_INVOKE_CUSTOM = 252` |  |
| `static final int OP_INVOKE_CUSTOM_RANGE = 253` |  |
| `static final int OP_INVOKE_DIRECT = 112` |  |
| `static final int OP_INVOKE_DIRECT_JUMBO = 9471` |  |
| `static final int OP_INVOKE_DIRECT_RANGE = 118` |  |
| `static final int OP_INVOKE_INTERFACE = 114` |  |
| `static final int OP_INVOKE_INTERFACE_JUMBO = 9983` |  |
| `static final int OP_INVOKE_INTERFACE_RANGE = 120` |  |
| `static final int OP_INVOKE_POLYMORPHIC = 250` |  |
| `static final int OP_INVOKE_POLYMORPHIC_RANGE = 251` |  |
| `static final int OP_INVOKE_STATIC = 113` |  |
| `static final int OP_INVOKE_STATIC_JUMBO = 9727` |  |
| `static final int OP_INVOKE_STATIC_RANGE = 119` |  |
| `static final int OP_INVOKE_SUPER = 111` |  |
| `static final int OP_INVOKE_SUPER_JUMBO = 9215` |  |
| `static final int OP_INVOKE_SUPER_RANGE = 117` |  |
| `static final int OP_INVOKE_VIRTUAL = 110` |  |
| `static final int OP_INVOKE_VIRTUAL_JUMBO = 8959` |  |
| `static final int OP_INVOKE_VIRTUAL_RANGE = 116` |  |
| `static final int OP_IPUT = 89` |  |
| `static final int OP_IPUT_BOOLEAN = 92` |  |
| `static final int OP_IPUT_BOOLEAN_JUMBO = 4351` |  |
| `static final int OP_IPUT_BYTE = 93` |  |
| `static final int OP_IPUT_BYTE_JUMBO = 4607` |  |
| `static final int OP_IPUT_CHAR = 94` |  |
| `static final int OP_IPUT_CHAR_JUMBO = 4863` |  |
| `static final int OP_IPUT_JUMBO = 3583` |  |
| `static final int OP_IPUT_OBJECT = 91` |  |
| `static final int OP_IPUT_OBJECT_JUMBO = 4095` |  |
| `static final int OP_IPUT_SHORT = 95` |  |
| `static final int OP_IPUT_SHORT_JUMBO = 5119` |  |
| `static final int OP_IPUT_WIDE = 90` |  |
| `static final int OP_IPUT_WIDE_JUMBO = 3839` |  |
| `static final int OP_LONG_TO_DOUBLE = 134` |  |
| `static final int OP_LONG_TO_FLOAT = 133` |  |
| `static final int OP_LONG_TO_INT = 132` |  |
| `static final int OP_MONITOR_ENTER = 29` |  |
| `static final int OP_MONITOR_EXIT = 30` |  |
| `static final int OP_MOVE = 1` |  |
| `static final int OP_MOVE_16 = 3` |  |
| `static final int OP_MOVE_EXCEPTION = 13` |  |
| `static final int OP_MOVE_FROM16 = 2` |  |
| `static final int OP_MOVE_OBJECT = 7` |  |
| `static final int OP_MOVE_OBJECT_16 = 9` |  |
| `static final int OP_MOVE_OBJECT_FROM16 = 8` |  |
| `static final int OP_MOVE_RESULT = 10` |  |
| `static final int OP_MOVE_RESULT_OBJECT = 12` |  |
| `static final int OP_MOVE_RESULT_WIDE = 11` |  |
| `static final int OP_MOVE_WIDE = 4` |  |
| `static final int OP_MOVE_WIDE_16 = 6` |  |
| `static final int OP_MOVE_WIDE_FROM16 = 5` |  |
| `static final int OP_MUL_DOUBLE = 173` |  |
| `static final int OP_MUL_DOUBLE_2ADDR = 205` |  |
| `static final int OP_MUL_FLOAT = 168` |  |
| `static final int OP_MUL_FLOAT_2ADDR = 200` |  |
| `static final int OP_MUL_INT = 146` |  |
| `static final int OP_MUL_INT_2ADDR = 178` |  |
| `static final int OP_MUL_INT_LIT16 = 210` |  |
| `static final int OP_MUL_INT_LIT8 = 218` |  |
| `static final int OP_MUL_LONG = 157` |  |
| `static final int OP_MUL_LONG_2ADDR = 189` |  |
| `static final int OP_NEG_DOUBLE = 128` |  |
| `static final int OP_NEG_FLOAT = 127` |  |
| `static final int OP_NEG_INT = 123` |  |
| `static final int OP_NEG_LONG = 125` |  |
| `static final int OP_NEW_ARRAY = 35` |  |
| `static final int OP_NEW_ARRAY_JUMBO = 1279` |  |
| `static final int OP_NEW_INSTANCE = 34` |  |
| `static final int OP_NEW_INSTANCE_JUMBO = 1023` |  |
| `static final int OP_NOP = 0` |  |
| `static final int OP_NOT_INT = 124` |  |
| `static final int OP_NOT_LONG = 126` |  |
| `static final int OP_OR_INT = 150` |  |
| `static final int OP_OR_INT_2ADDR = 182` |  |
| `static final int OP_OR_INT_LIT16 = 214` |  |
| `static final int OP_OR_INT_LIT8 = 222` |  |
| `static final int OP_OR_LONG = 161` |  |
| `static final int OP_OR_LONG_2ADDR = 193` |  |
| `static final int OP_PACKED_SWITCH = 43` |  |
| `static final int OP_REM_DOUBLE = 175` |  |
| `static final int OP_REM_DOUBLE_2ADDR = 207` |  |
| `static final int OP_REM_FLOAT = 170` |  |
| `static final int OP_REM_FLOAT_2ADDR = 202` |  |
| `static final int OP_REM_INT = 148` |  |
| `static final int OP_REM_INT_2ADDR = 180` |  |
| `static final int OP_REM_INT_LIT16 = 212` |  |
| `static final int OP_REM_INT_LIT8 = 220` |  |
| `static final int OP_REM_LONG = 159` |  |
| `static final int OP_REM_LONG_2ADDR = 191` |  |
| `static final int OP_RETURN = 15` |  |
| `static final int OP_RETURN_OBJECT = 17` |  |
| `static final int OP_RETURN_VOID = 14` |  |
| `static final int OP_RETURN_WIDE = 16` |  |
| `static final int OP_RSUB_INT = 209` |  |
| `static final int OP_RSUB_INT_LIT8 = 217` |  |
| `static final int OP_SGET = 96` |  |
| `static final int OP_SGET_BOOLEAN = 99` |  |
| `static final int OP_SGET_BOOLEAN_JUMBO = 6143` |  |
| `static final int OP_SGET_BYTE = 100` |  |
| `static final int OP_SGET_BYTE_JUMBO = 6399` |  |
| `static final int OP_SGET_CHAR = 101` |  |
| `static final int OP_SGET_CHAR_JUMBO = 6655` |  |
| `static final int OP_SGET_JUMBO = 5375` |  |
| `static final int OP_SGET_OBJECT = 98` |  |
| `static final int OP_SGET_OBJECT_JUMBO = 5887` |  |
| `static final int OP_SGET_SHORT = 102` |  |
| `static final int OP_SGET_SHORT_JUMBO = 6911` |  |
| `static final int OP_SGET_WIDE = 97` |  |
| `static final int OP_SGET_WIDE_JUMBO = 5631` |  |
| `static final int OP_SHL_INT = 152` |  |
| `static final int OP_SHL_INT_2ADDR = 184` |  |
| `static final int OP_SHL_INT_LIT8 = 224` |  |
| `static final int OP_SHL_LONG = 163` |  |
| `static final int OP_SHL_LONG_2ADDR = 195` |  |
| `static final int OP_SHR_INT = 153` |  |
| `static final int OP_SHR_INT_2ADDR = 185` |  |
| `static final int OP_SHR_INT_LIT8 = 225` |  |
| `static final int OP_SHR_LONG = 164` |  |
| `static final int OP_SHR_LONG_2ADDR = 196` |  |
| `static final int OP_SPARSE_SWITCH = 44` |  |
| `static final int OP_SPUT = 103` |  |
| `static final int OP_SPUT_BOOLEAN = 106` |  |
| `static final int OP_SPUT_BOOLEAN_JUMBO = 7935` |  |
| `static final int OP_SPUT_BYTE = 107` |  |
| `static final int OP_SPUT_BYTE_JUMBO = 8191` |  |
| `static final int OP_SPUT_CHAR = 108` |  |
| `static final int OP_SPUT_CHAR_JUMBO = 8447` |  |
| `static final int OP_SPUT_JUMBO = 7167` |  |
| `static final int OP_SPUT_OBJECT = 105` |  |
| `static final int OP_SPUT_OBJECT_JUMBO = 7679` |  |
| `static final int OP_SPUT_SHORT = 109` |  |
| `static final int OP_SPUT_SHORT_JUMBO = 8703` |  |
| `static final int OP_SPUT_WIDE = 104` |  |
| `static final int OP_SPUT_WIDE_JUMBO = 7423` |  |
| `static final int OP_SUB_DOUBLE = 172` |  |
| `static final int OP_SUB_DOUBLE_2ADDR = 204` |  |
| `static final int OP_SUB_FLOAT = 167` |  |
| `static final int OP_SUB_FLOAT_2ADDR = 199` |  |
| `static final int OP_SUB_INT = 145` |  |
| `static final int OP_SUB_INT_2ADDR = 177` |  |
| `static final int OP_SUB_LONG = 156` |  |
| `static final int OP_SUB_LONG_2ADDR = 188` |  |
| `static final int OP_THROW = 39` |  |
| `static final int OP_USHR_INT = 154` |  |
| `static final int OP_USHR_INT_2ADDR = 186` |  |
| `static final int OP_USHR_INT_LIT8 = 226` |  |
| `static final int OP_USHR_LONG = 165` |  |
| `static final int OP_USHR_LONG_2ADDR = 197` |  |
| `static final int OP_XOR_INT = 151` |  |
| `static final int OP_XOR_INT_2ADDR = 183` |  |
| `static final int OP_XOR_INT_LIT16 = 215` |  |
| `static final int OP_XOR_INT_LIT8 = 223` |  |
| `static final int OP_XOR_LONG = 162` |  |
| `static final int OP_XOR_LONG_2ADDR = 194` |  |

---

## Package: `dalvik.system`

### `class BaseDexClassLoader`

- **继承：** `java.lang.ClassLoader`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BaseDexClassLoader(String, java.io.File, String, ClassLoader)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String findLibrary(String)` |  |
| `java.util.Enumeration<java.net.URL> findResources(String)` |  |

---

### `class final DelegateLastClassLoader`

- **继承：** `dalvik.system.PathClassLoader`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DelegateLastClassLoader(String, ClassLoader)` |  |
| `DelegateLastClassLoader(String, String, ClassLoader)` |  |
| `DelegateLastClassLoader(@NonNull String, @Nullable String, @Nullable ClassLoader, boolean)` |  |

---

### `class DexClassLoader`

- **继承：** `dalvik.system.BaseDexClassLoader`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DexClassLoader(String, String, String, ClassLoader)` |  |

---

### `class final DexFile` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class final InMemoryDexClassLoader`

- **继承：** `dalvik.system.BaseDexClassLoader`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `InMemoryDexClassLoader(@NonNull java.nio.ByteBuffer[], @Nullable String, @Nullable ClassLoader)` |  |
| `InMemoryDexClassLoader(@NonNull java.nio.ByteBuffer[], @Nullable ClassLoader)` |  |
| `InMemoryDexClassLoader(@NonNull java.nio.ByteBuffer, @Nullable ClassLoader)` |  |

---

### `class PathClassLoader`

- **继承：** `dalvik.system.BaseDexClassLoader`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PathClassLoader(String, ClassLoader)` |  |
| `PathClassLoader(String, String, ClassLoader)` |  |

---

## Package: `javax.crypto`

### `class AEADBadTagException`

- **继承：** `javax.crypto.BadPaddingException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AEADBadTagException()` |  |
| `AEADBadTagException(String)` |  |

---

### `class BadPaddingException`

- **继承：** `java.security.GeneralSecurityException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BadPaddingException()` |  |
| `BadPaddingException(String)` |  |

---

### `class Cipher`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Cipher(javax.crypto.CipherSpi, java.security.Provider, String)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DECRYPT_MODE = 2` |  |
| `static final int ENCRYPT_MODE = 1` |  |
| `static final int PRIVATE_KEY = 2` |  |
| `static final int PUBLIC_KEY = 1` |  |
| `static final int SECRET_KEY = 3` |  |
| `static final int UNWRAP_MODE = 4` |  |
| `static final int WRAP_MODE = 3` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final byte[] doFinal() throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException` |  |
| `final int doFinal(byte[], int) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException, javax.crypto.ShortBufferException` |  |
| `final byte[] doFinal(byte[]) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException` |  |
| `final byte[] doFinal(byte[], int, int) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException` |  |
| `final int doFinal(byte[], int, int, byte[]) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException, javax.crypto.ShortBufferException` |  |
| `final int doFinal(byte[], int, int, byte[], int) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException, javax.crypto.ShortBufferException` |  |
| `final int doFinal(java.nio.ByteBuffer, java.nio.ByteBuffer) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException, javax.crypto.ShortBufferException` |  |
| `final String getAlgorithm()` |  |
| `final int getBlockSize()` |  |
| `final javax.crypto.ExemptionMechanism getExemptionMechanism()` |  |
| `final byte[] getIV()` |  |
| `static final javax.crypto.Cipher getInstance(String) throws java.security.NoSuchAlgorithmException, javax.crypto.NoSuchPaddingException` |  |
| `static final javax.crypto.Cipher getInstance(String, String) throws java.security.NoSuchAlgorithmException, javax.crypto.NoSuchPaddingException, java.security.NoSuchProviderException` |  |
| `static final javax.crypto.Cipher getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException, javax.crypto.NoSuchPaddingException` |  |
| `static final int getMaxAllowedKeyLength(String) throws java.security.NoSuchAlgorithmException` |  |
| `static final java.security.spec.AlgorithmParameterSpec getMaxAllowedParameterSpec(String) throws java.security.NoSuchAlgorithmException` |  |
| `final int getOutputSize(int)` |  |
| `final java.security.AlgorithmParameters getParameters()` |  |
| `final java.security.Provider getProvider()` |  |
| `final void init(int, java.security.Key) throws java.security.InvalidKeyException` |  |
| `final void init(int, java.security.Key, java.security.SecureRandom) throws java.security.InvalidKeyException` |  |
| `final void init(int, java.security.Key, java.security.spec.AlgorithmParameterSpec) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |
| `final void init(int, java.security.Key, java.security.spec.AlgorithmParameterSpec, java.security.SecureRandom) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |
| `final void init(int, java.security.Key, java.security.AlgorithmParameters) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |
| `final void init(int, java.security.Key, java.security.AlgorithmParameters, java.security.SecureRandom) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |
| `final void init(int, java.security.cert.Certificate) throws java.security.InvalidKeyException` |  |
| `final void init(int, java.security.cert.Certificate, java.security.SecureRandom) throws java.security.InvalidKeyException` |  |
| `final java.security.Key unwrap(byte[], String, int) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException` |  |
| `final byte[] update(byte[])` |  |
| `final byte[] update(byte[], int, int)` |  |
| `final int update(byte[], int, int, byte[]) throws javax.crypto.ShortBufferException` |  |
| `final int update(byte[], int, int, byte[], int) throws javax.crypto.ShortBufferException` |  |
| `final int update(java.nio.ByteBuffer, java.nio.ByteBuffer) throws javax.crypto.ShortBufferException` |  |
| `final void updateAAD(byte[])` |  |
| `final void updateAAD(byte[], int, int)` |  |
| `final void updateAAD(java.nio.ByteBuffer)` |  |
| `final byte[] wrap(java.security.Key) throws javax.crypto.IllegalBlockSizeException, java.security.InvalidKeyException` |  |

---

### `class CipherInputStream`

- **继承：** `java.io.FilterInputStream`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CipherInputStream(java.io.InputStream, javax.crypto.Cipher)` |  |
| `CipherInputStream(java.io.InputStream)` |  |

---

### `class CipherOutputStream`

- **继承：** `java.io.FilterOutputStream`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CipherOutputStream(java.io.OutputStream, javax.crypto.Cipher)` |  |
| `CipherOutputStream(java.io.OutputStream)` |  |

---

### `class abstract CipherSpi`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CipherSpi()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract byte[] engineDoFinal(byte[], int, int) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException` |  |
| `abstract int engineDoFinal(byte[], int, int, byte[], int) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException, javax.crypto.ShortBufferException` |  |
| `int engineDoFinal(java.nio.ByteBuffer, java.nio.ByteBuffer) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException, javax.crypto.ShortBufferException` |  |
| `abstract int engineGetBlockSize()` |  |
| `abstract byte[] engineGetIV()` |  |
| `int engineGetKeySize(java.security.Key) throws java.security.InvalidKeyException` |  |
| `abstract int engineGetOutputSize(int)` |  |
| `abstract java.security.AlgorithmParameters engineGetParameters()` |  |
| `abstract void engineInit(int, java.security.Key, java.security.SecureRandom) throws java.security.InvalidKeyException` |  |
| `abstract void engineInit(int, java.security.Key, java.security.spec.AlgorithmParameterSpec, java.security.SecureRandom) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |
| `abstract void engineInit(int, java.security.Key, java.security.AlgorithmParameters, java.security.SecureRandom) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |
| `abstract void engineSetMode(String) throws java.security.NoSuchAlgorithmException` |  |
| `abstract void engineSetPadding(String) throws javax.crypto.NoSuchPaddingException` |  |
| `java.security.Key engineUnwrap(byte[], String, int) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException` |  |
| `abstract byte[] engineUpdate(byte[], int, int)` |  |
| `abstract int engineUpdate(byte[], int, int, byte[], int) throws javax.crypto.ShortBufferException` |  |
| `int engineUpdate(java.nio.ByteBuffer, java.nio.ByteBuffer) throws javax.crypto.ShortBufferException` |  |
| `void engineUpdateAAD(byte[], int, int)` |  |
| `void engineUpdateAAD(java.nio.ByteBuffer)` |  |
| `byte[] engineWrap(java.security.Key) throws javax.crypto.IllegalBlockSizeException, java.security.InvalidKeyException` |  |

---

### `class EncryptedPrivateKeyInfo`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `EncryptedPrivateKeyInfo(byte[]) throws java.io.IOException` |  |
| `EncryptedPrivateKeyInfo(String, byte[]) throws java.security.NoSuchAlgorithmException` |  |
| `EncryptedPrivateKeyInfo(java.security.AlgorithmParameters, byte[]) throws java.security.NoSuchAlgorithmException` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getAlgName()` |  |
| `java.security.AlgorithmParameters getAlgParameters()` |  |
| `byte[] getEncoded() throws java.io.IOException` |  |
| `byte[] getEncryptedData()` |  |
| `java.security.spec.PKCS8EncodedKeySpec getKeySpec(javax.crypto.Cipher) throws java.security.spec.InvalidKeySpecException` |  |
| `java.security.spec.PKCS8EncodedKeySpec getKeySpec(java.security.Key) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException` |  |
| `java.security.spec.PKCS8EncodedKeySpec getKeySpec(java.security.Key, String) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `java.security.spec.PKCS8EncodedKeySpec getKeySpec(java.security.Key, java.security.Provider) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException` |  |

---

### `class ExemptionMechanism`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ExemptionMechanism(javax.crypto.ExemptionMechanismSpi, java.security.Provider, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final byte[] genExemptionBlob() throws javax.crypto.ExemptionMechanismException, java.lang.IllegalStateException` |  |
| `final int genExemptionBlob(byte[]) throws javax.crypto.ExemptionMechanismException, java.lang.IllegalStateException, javax.crypto.ShortBufferException` |  |
| `final int genExemptionBlob(byte[], int) throws javax.crypto.ExemptionMechanismException, java.lang.IllegalStateException, javax.crypto.ShortBufferException` |  |
| `static final javax.crypto.ExemptionMechanism getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static final javax.crypto.ExemptionMechanism getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static final javax.crypto.ExemptionMechanism getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final String getName()` |  |
| `final int getOutputSize(int) throws java.lang.IllegalStateException` |  |
| `final java.security.Provider getProvider()` |  |
| `final void init(java.security.Key) throws javax.crypto.ExemptionMechanismException, java.security.InvalidKeyException` |  |
| `final void init(java.security.Key, java.security.spec.AlgorithmParameterSpec) throws javax.crypto.ExemptionMechanismException, java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |
| `final void init(java.security.Key, java.security.AlgorithmParameters) throws javax.crypto.ExemptionMechanismException, java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |
| `final boolean isCryptoAllowed(java.security.Key) throws javax.crypto.ExemptionMechanismException` |  |

---

### `class ExemptionMechanismException`

- **继承：** `java.security.GeneralSecurityException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ExemptionMechanismException()` |  |
| `ExemptionMechanismException(String)` |  |

---

### `class abstract ExemptionMechanismSpi`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ExemptionMechanismSpi()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract byte[] engineGenExemptionBlob() throws javax.crypto.ExemptionMechanismException` |  |
| `abstract int engineGenExemptionBlob(byte[], int) throws javax.crypto.ExemptionMechanismException, javax.crypto.ShortBufferException` |  |
| `abstract int engineGetOutputSize(int)` |  |
| `abstract void engineInit(java.security.Key) throws javax.crypto.ExemptionMechanismException, java.security.InvalidKeyException` |  |
| `abstract void engineInit(java.security.Key, java.security.spec.AlgorithmParameterSpec) throws javax.crypto.ExemptionMechanismException, java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |
| `abstract void engineInit(java.security.Key, java.security.AlgorithmParameters) throws javax.crypto.ExemptionMechanismException, java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |

---

### `class IllegalBlockSizeException`

- **继承：** `java.security.GeneralSecurityException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `IllegalBlockSizeException()` |  |
| `IllegalBlockSizeException(String)` |  |

---

### `class KeyAgreement`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `KeyAgreement(javax.crypto.KeyAgreementSpi, java.security.Provider, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final java.security.Key doPhase(java.security.Key, boolean) throws java.lang.IllegalStateException, java.security.InvalidKeyException` |  |
| `final byte[] generateSecret() throws java.lang.IllegalStateException` |  |
| `final int generateSecret(byte[], int) throws java.lang.IllegalStateException, javax.crypto.ShortBufferException` |  |
| `final javax.crypto.SecretKey generateSecret(String) throws java.lang.IllegalStateException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException` |  |
| `final String getAlgorithm()` |  |
| `static final javax.crypto.KeyAgreement getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static final javax.crypto.KeyAgreement getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static final javax.crypto.KeyAgreement getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final java.security.Provider getProvider()` |  |
| `final void init(java.security.Key) throws java.security.InvalidKeyException` |  |
| `final void init(java.security.Key, java.security.SecureRandom) throws java.security.InvalidKeyException` |  |
| `final void init(java.security.Key, java.security.spec.AlgorithmParameterSpec) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |
| `final void init(java.security.Key, java.security.spec.AlgorithmParameterSpec, java.security.SecureRandom) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |

---

### `class abstract KeyAgreementSpi`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `KeyAgreementSpi()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract java.security.Key engineDoPhase(java.security.Key, boolean) throws java.lang.IllegalStateException, java.security.InvalidKeyException` |  |
| `abstract byte[] engineGenerateSecret() throws java.lang.IllegalStateException` |  |
| `abstract int engineGenerateSecret(byte[], int) throws java.lang.IllegalStateException, javax.crypto.ShortBufferException` |  |
| `abstract javax.crypto.SecretKey engineGenerateSecret(String) throws java.lang.IllegalStateException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException` |  |
| `abstract void engineInit(java.security.Key, java.security.SecureRandom) throws java.security.InvalidKeyException` |  |
| `abstract void engineInit(java.security.Key, java.security.spec.AlgorithmParameterSpec, java.security.SecureRandom) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |

---

### `class KeyGenerator`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `KeyGenerator(javax.crypto.KeyGeneratorSpi, java.security.Provider, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final javax.crypto.SecretKey generateKey()` |  |
| `final String getAlgorithm()` |  |
| `static final javax.crypto.KeyGenerator getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static final javax.crypto.KeyGenerator getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static final javax.crypto.KeyGenerator getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final java.security.Provider getProvider()` |  |
| `final void init(java.security.SecureRandom)` |  |
| `final void init(java.security.spec.AlgorithmParameterSpec) throws java.security.InvalidAlgorithmParameterException` |  |
| `final void init(java.security.spec.AlgorithmParameterSpec, java.security.SecureRandom) throws java.security.InvalidAlgorithmParameterException` |  |
| `final void init(int)` |  |
| `final void init(int, java.security.SecureRandom)` |  |

---

### `class abstract KeyGeneratorSpi`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `KeyGeneratorSpi()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract javax.crypto.SecretKey engineGenerateKey()` |  |
| `abstract void engineInit(java.security.SecureRandom)` |  |
| `abstract void engineInit(java.security.spec.AlgorithmParameterSpec, java.security.SecureRandom) throws java.security.InvalidAlgorithmParameterException` |  |
| `abstract void engineInit(int, java.security.SecureRandom)` |  |

---

### `class Mac`

- **实现：** `java.lang.Cloneable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Mac(javax.crypto.MacSpi, java.security.Provider, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final Object clone() throws java.lang.CloneNotSupportedException` |  |
| `final byte[] doFinal() throws java.lang.IllegalStateException` |  |
| `final void doFinal(byte[], int) throws java.lang.IllegalStateException, javax.crypto.ShortBufferException` |  |
| `final byte[] doFinal(byte[]) throws java.lang.IllegalStateException` |  |
| `final String getAlgorithm()` |  |
| `static final javax.crypto.Mac getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static final javax.crypto.Mac getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static final javax.crypto.Mac getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final int getMacLength()` |  |
| `final java.security.Provider getProvider()` |  |
| `final void init(java.security.Key) throws java.security.InvalidKeyException` |  |
| `final void init(java.security.Key, java.security.spec.AlgorithmParameterSpec) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |
| `final void reset()` |  |
| `final void update(byte) throws java.lang.IllegalStateException` |  |
| `final void update(byte[]) throws java.lang.IllegalStateException` |  |
| `final void update(byte[], int, int) throws java.lang.IllegalStateException` |  |
| `final void update(java.nio.ByteBuffer)` |  |

---

### `class abstract MacSpi`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MacSpi()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `Object clone() throws java.lang.CloneNotSupportedException` |  |
| `abstract byte[] engineDoFinal()` |  |
| `abstract int engineGetMacLength()` |  |
| `abstract void engineInit(java.security.Key, java.security.spec.AlgorithmParameterSpec) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException` |  |
| `abstract void engineReset()` |  |
| `abstract void engineUpdate(byte)` |  |
| `abstract void engineUpdate(byte[], int, int)` |  |
| `void engineUpdate(java.nio.ByteBuffer)` |  |

---

### `class NoSuchPaddingException`

- **继承：** `java.security.GeneralSecurityException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NoSuchPaddingException()` |  |
| `NoSuchPaddingException(String)` |  |

---

### `class NullCipher`

- **继承：** `javax.crypto.Cipher`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NullCipher()` |  |

---

### `class SealedObject`

- **实现：** `java.io.Serializable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SealedObject(java.io.Serializable, javax.crypto.Cipher) throws java.io.IOException, javax.crypto.IllegalBlockSizeException` |  |
| `SealedObject(javax.crypto.SealedObject)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `byte[] encodedParams` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final String getAlgorithm()` |  |
| `final Object getObject(java.security.Key) throws java.lang.ClassNotFoundException, java.io.IOException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException` |  |
| `final Object getObject(javax.crypto.Cipher) throws javax.crypto.BadPaddingException, java.lang.ClassNotFoundException, java.io.IOException, javax.crypto.IllegalBlockSizeException` |  |
| `final Object getObject(java.security.Key, String) throws java.lang.ClassNotFoundException, java.io.IOException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |

---

### `interface SecretKey`

- **继承：** `java.security.Key javax.security.auth.Destroyable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = -4795878709595146952L` |  |

---

### `class SecretKeyFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SecretKeyFactory(javax.crypto.SecretKeyFactorySpi, java.security.Provider, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final javax.crypto.SecretKey generateSecret(java.security.spec.KeySpec) throws java.security.spec.InvalidKeySpecException` |  |
| `final String getAlgorithm()` |  |
| `static final javax.crypto.SecretKeyFactory getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static final javax.crypto.SecretKeyFactory getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static final javax.crypto.SecretKeyFactory getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final java.security.spec.KeySpec getKeySpec(javax.crypto.SecretKey, Class<?>) throws java.security.spec.InvalidKeySpecException` |  |
| `final java.security.Provider getProvider()` |  |
| `final javax.crypto.SecretKey translateKey(javax.crypto.SecretKey) throws java.security.InvalidKeyException` |  |

---

### `class abstract SecretKeyFactorySpi`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SecretKeyFactorySpi()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract javax.crypto.SecretKey engineGenerateSecret(java.security.spec.KeySpec) throws java.security.spec.InvalidKeySpecException` |  |
| `abstract java.security.spec.KeySpec engineGetKeySpec(javax.crypto.SecretKey, Class<?>) throws java.security.spec.InvalidKeySpecException` |  |
| `abstract javax.crypto.SecretKey engineTranslateKey(javax.crypto.SecretKey) throws java.security.InvalidKeyException` |  |

---

### `class ShortBufferException`

- **继承：** `java.security.GeneralSecurityException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ShortBufferException()` |  |
| `ShortBufferException(String)` |  |

---

## Package: `javax.crypto.interfaces`

### `interface DHKey`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `javax.crypto.spec.DHParameterSpec getParams()` |  |

---

### `interface DHPrivateKey`

- **继承：** `javax.crypto.interfaces.DHKey java.security.PrivateKey`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = 2211791113380396553L` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getX()` |  |

---

### `interface DHPublicKey`

- **继承：** `javax.crypto.interfaces.DHKey java.security.PublicKey`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = -6628103563352519193L` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getY()` |  |

---

### `interface PBEKey`

- **继承：** `javax.crypto.SecretKey`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final long serialVersionUID = -1430015993304333921L` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getIterationCount()` |  |
| `char[] getPassword()` |  |
| `byte[] getSalt()` |  |

---

## Package: `javax.crypto.spec`

### `class DESKeySpec`

- **实现：** `java.security.spec.KeySpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DESKeySpec(byte[]) throws java.security.InvalidKeyException` |  |
| `DESKeySpec(byte[], int) throws java.security.InvalidKeyException` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DES_KEY_LEN = 8` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `byte[] getKey()` |  |
| `static boolean isParityAdjusted(byte[], int) throws java.security.InvalidKeyException` |  |
| `static boolean isWeak(byte[], int) throws java.security.InvalidKeyException` |  |

---

### `class DESedeKeySpec`

- **实现：** `java.security.spec.KeySpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DESedeKeySpec(byte[]) throws java.security.InvalidKeyException` |  |
| `DESedeKeySpec(byte[], int) throws java.security.InvalidKeyException` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DES_EDE_KEY_LEN = 24` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `byte[] getKey()` |  |
| `static boolean isParityAdjusted(byte[], int) throws java.security.InvalidKeyException` |  |

---

### `class DHGenParameterSpec`

- **实现：** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DHGenParameterSpec(int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getExponentSize()` |  |
| `int getPrimeSize()` |  |

---

### `class DHParameterSpec`

- **实现：** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DHParameterSpec(java.math.BigInteger, java.math.BigInteger)` |  |
| `DHParameterSpec(java.math.BigInteger, java.math.BigInteger, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getG()` |  |
| `int getL()` |  |
| `java.math.BigInteger getP()` |  |

---

### `class DHPrivateKeySpec`

- **实现：** `java.security.spec.KeySpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DHPrivateKeySpec(java.math.BigInteger, java.math.BigInteger, java.math.BigInteger)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getG()` |  |
| `java.math.BigInteger getP()` |  |
| `java.math.BigInteger getX()` |  |

---

### `class DHPublicKeySpec`

- **实现：** `java.security.spec.KeySpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DHPublicKeySpec(java.math.BigInteger, java.math.BigInteger, java.math.BigInteger)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.math.BigInteger getG()` |  |
| `java.math.BigInteger getP()` |  |
| `java.math.BigInteger getY()` |  |

---

### `class GCMParameterSpec`

- **实现：** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GCMParameterSpec(int, byte[])` |  |
| `GCMParameterSpec(int, byte[], int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `byte[] getIV()` |  |
| `int getTLen()` |  |

---

### `class IvParameterSpec`

- **实现：** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `IvParameterSpec(byte[])` |  |
| `IvParameterSpec(byte[], int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `byte[] getIV()` |  |

---

### `class OAEPParameterSpec`

- **实现：** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `OAEPParameterSpec(String, String, java.security.spec.AlgorithmParameterSpec, javax.crypto.spec.PSource)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final javax.crypto.spec.OAEPParameterSpec DEFAULT` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getDigestAlgorithm()` |  |
| `String getMGFAlgorithm()` |  |
| `java.security.spec.AlgorithmParameterSpec getMGFParameters()` |  |
| `javax.crypto.spec.PSource getPSource()` |  |

---

### `class PBEKeySpec`

- **实现：** `java.security.spec.KeySpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PBEKeySpec(char[])` |  |
| `PBEKeySpec(char[], byte[], int, int)` |  |
| `PBEKeySpec(char[], byte[], int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final void clearPassword()` |  |
| `final int getIterationCount()` |  |
| `final int getKeyLength()` |  |
| `final char[] getPassword()` |  |
| `final byte[] getSalt()` |  |

---

### `class PBEParameterSpec`

- **实现：** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PBEParameterSpec(byte[], int)` |  |
| `PBEParameterSpec(byte[], int, java.security.spec.AlgorithmParameterSpec)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getIterationCount()` |  |
| `java.security.spec.AlgorithmParameterSpec getParameterSpec()` |  |
| `byte[] getSalt()` |  |

---

### `class PSource`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PSource(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getAlgorithm()` |  |

---

### `class static final PSource.PSpecified`

- **继承：** `javax.crypto.spec.PSource`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PSource.PSpecified(byte[])` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final javax.crypto.spec.PSource.PSpecified DEFAULT` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `byte[] getValue()` |  |

---

### `class RC2ParameterSpec`

- **实现：** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RC2ParameterSpec(int)` |  |
| `RC2ParameterSpec(int, byte[])` |  |
| `RC2ParameterSpec(int, byte[], int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getEffectiveKeyBits()` |  |
| `byte[] getIV()` |  |

---

### `class RC5ParameterSpec`

- **实现：** `java.security.spec.AlgorithmParameterSpec`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RC5ParameterSpec(int, int, int)` |  |
| `RC5ParameterSpec(int, int, int, byte[])` |  |
| `RC5ParameterSpec(int, int, int, byte[], int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `byte[] getIV()` |  |
| `int getRounds()` |  |
| `int getVersion()` |  |
| `int getWordSize()` |  |

---

### `class SecretKeySpec`

- **实现：** `java.security.spec.KeySpec javax.crypto.SecretKey`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SecretKeySpec(byte[], String)` |  |
| `SecretKeySpec(byte[], int, int, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getAlgorithm()` |  |
| `byte[] getEncoded()` |  |
| `String getFormat()` |  |

---

## Package: `javax.microedition.khronos.egl`

### `interface EGL`


---

### `interface EGL10`

- **继承：** `javax.microedition.khronos.egl.EGL`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int EGL_ALPHA_FORMAT = 12424` |  |
| `static final int EGL_ALPHA_MASK_SIZE = 12350` |  |
| `static final int EGL_ALPHA_SIZE = 12321` |  |
| `static final int EGL_BAD_ACCESS = 12290` |  |
| `static final int EGL_BAD_ALLOC = 12291` |  |
| `static final int EGL_BAD_ATTRIBUTE = 12292` |  |
| `static final int EGL_BAD_CONFIG = 12293` |  |
| `static final int EGL_BAD_CONTEXT = 12294` |  |
| `static final int EGL_BAD_CURRENT_SURFACE = 12295` |  |
| `static final int EGL_BAD_DISPLAY = 12296` |  |
| `static final int EGL_BAD_MATCH = 12297` |  |
| `static final int EGL_BAD_NATIVE_PIXMAP = 12298` |  |
| `static final int EGL_BAD_NATIVE_WINDOW = 12299` |  |
| `static final int EGL_BAD_PARAMETER = 12300` |  |
| `static final int EGL_BAD_SURFACE = 12301` |  |
| `static final int EGL_BLUE_SIZE = 12322` |  |
| `static final int EGL_BUFFER_SIZE = 12320` |  |
| `static final int EGL_COLORSPACE = 12423` |  |
| `static final int EGL_COLOR_BUFFER_TYPE = 12351` |  |
| `static final int EGL_CONFIG_CAVEAT = 12327` |  |
| `static final int EGL_CONFIG_ID = 12328` |  |
| `static final int EGL_CORE_NATIVE_ENGINE = 12379` |  |
| `static final Object EGL_DEFAULT_DISPLAY` |  |
| `static final int EGL_DEPTH_SIZE = 12325` |  |
| `static final int EGL_DONT_CARE = -1` |  |
| `static final int EGL_DRAW = 12377` |  |
| `static final int EGL_EXTENSIONS = 12373` |  |
| `static final int EGL_GREEN_SIZE = 12323` |  |
| `static final int EGL_HEIGHT = 12374` |  |
| `static final int EGL_HORIZONTAL_RESOLUTION = 12432` |  |
| `static final int EGL_LARGEST_PBUFFER = 12376` |  |
| `static final int EGL_LEVEL = 12329` |  |
| `static final int EGL_LUMINANCE_BUFFER = 12431` |  |
| `static final int EGL_LUMINANCE_SIZE = 12349` |  |
| `static final int EGL_MAX_PBUFFER_HEIGHT = 12330` |  |
| `static final int EGL_MAX_PBUFFER_PIXELS = 12331` |  |
| `static final int EGL_MAX_PBUFFER_WIDTH = 12332` |  |
| `static final int EGL_NATIVE_RENDERABLE = 12333` |  |
| `static final int EGL_NATIVE_VISUAL_ID = 12334` |  |
| `static final int EGL_NATIVE_VISUAL_TYPE = 12335` |  |
| `static final int EGL_NONE = 12344` |  |
| `static final int EGL_NON_CONFORMANT_CONFIG = 12369` |  |
| `static final int EGL_NOT_INITIALIZED = 12289` |  |
| `static final javax.microedition.khronos.egl.EGLContext EGL_NO_CONTEXT` |  |
| `static final javax.microedition.khronos.egl.EGLDisplay EGL_NO_DISPLAY` |  |
| `static final javax.microedition.khronos.egl.EGLSurface EGL_NO_SURFACE` |  |
| `static final int EGL_PBUFFER_BIT = 1` |  |
| `static final int EGL_PIXEL_ASPECT_RATIO = 12434` |  |
| `static final int EGL_PIXMAP_BIT = 2` |  |
| `static final int EGL_READ = 12378` |  |
| `static final int EGL_RED_SIZE = 12324` |  |
| `static final int EGL_RENDERABLE_TYPE = 12352` |  |
| `static final int EGL_RENDER_BUFFER = 12422` |  |
| `static final int EGL_RGB_BUFFER = 12430` |  |
| `static final int EGL_SAMPLES = 12337` |  |
| `static final int EGL_SAMPLE_BUFFERS = 12338` |  |
| `static final int EGL_SINGLE_BUFFER = 12421` |  |
| `static final int EGL_SLOW_CONFIG = 12368` |  |
| `static final int EGL_STENCIL_SIZE = 12326` |  |
| `static final int EGL_SUCCESS = 12288` |  |
| `static final int EGL_SURFACE_TYPE = 12339` |  |
| `static final int EGL_TRANSPARENT_BLUE_VALUE = 12341` |  |
| `static final int EGL_TRANSPARENT_GREEN_VALUE = 12342` |  |
| `static final int EGL_TRANSPARENT_RED_VALUE = 12343` |  |
| `static final int EGL_TRANSPARENT_RGB = 12370` |  |
| `static final int EGL_TRANSPARENT_TYPE = 12340` |  |
| `static final int EGL_VENDOR = 12371` |  |
| `static final int EGL_VERSION = 12372` |  |
| `static final int EGL_VERTICAL_RESOLUTION = 12433` |  |
| `static final int EGL_WIDTH = 12375` |  |
| `static final int EGL_WINDOW_BIT = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean eglChooseConfig(javax.microedition.khronos.egl.EGLDisplay, int[], javax.microedition.khronos.egl.EGLConfig[], int, int[])` |  |
| `boolean eglCopyBuffers(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLSurface, Object)` |  |
| `javax.microedition.khronos.egl.EGLContext eglCreateContext(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLConfig, javax.microedition.khronos.egl.EGLContext, int[])` |  |
| `javax.microedition.khronos.egl.EGLSurface eglCreatePbufferSurface(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLConfig, int[])` |  |
| `javax.microedition.khronos.egl.EGLSurface eglCreateWindowSurface(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLConfig, Object, int[])` |  |
| `boolean eglDestroyContext(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLContext)` |  |
| `boolean eglDestroySurface(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLSurface)` |  |
| `boolean eglGetConfigAttrib(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLConfig, int, int[])` |  |
| `boolean eglGetConfigs(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLConfig[], int, int[])` |  |
| `javax.microedition.khronos.egl.EGLContext eglGetCurrentContext()` |  |
| `javax.microedition.khronos.egl.EGLDisplay eglGetCurrentDisplay()` |  |
| `javax.microedition.khronos.egl.EGLSurface eglGetCurrentSurface(int)` |  |
| `javax.microedition.khronos.egl.EGLDisplay eglGetDisplay(Object)` |  |
| `int eglGetError()` |  |
| `boolean eglInitialize(javax.microedition.khronos.egl.EGLDisplay, int[])` |  |
| `boolean eglMakeCurrent(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLSurface, javax.microedition.khronos.egl.EGLSurface, javax.microedition.khronos.egl.EGLContext)` |  |
| `boolean eglQueryContext(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLContext, int, int[])` |  |
| `String eglQueryString(javax.microedition.khronos.egl.EGLDisplay, int)` |  |
| `boolean eglQuerySurface(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLSurface, int, int[])` |  |
| `boolean eglSwapBuffers(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLSurface)` |  |
| `boolean eglTerminate(javax.microedition.khronos.egl.EGLDisplay)` |  |
| `boolean eglWaitGL()` |  |
| `boolean eglWaitNative(int, Object)` |  |

---

### `interface EGL11`

- **继承：** `javax.microedition.khronos.egl.EGL10`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int EGL_CONTEXT_LOST = 12302` |  |

---

### `class abstract EGLConfig`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `EGLConfig()` |  |

---

### `class abstract EGLContext`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `EGLContext()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static javax.microedition.khronos.egl.EGL getEGL()` |  |
| `abstract javax.microedition.khronos.opengles.GL getGL()` |  |

---

### `class abstract EGLDisplay`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `EGLDisplay()` |  |

---

### `class abstract EGLSurface`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `EGLSurface()` |  |

---

## Package: `javax.microedition.khronos.opengles`

### `interface GL`


---

### `interface GL10`

- **继承：** `javax.microedition.khronos.opengles.GL`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GL_ADD = 260` |  |
| `static final int GL_ALIASED_LINE_WIDTH_RANGE = 33902` |  |
| `static final int GL_ALIASED_POINT_SIZE_RANGE = 33901` |  |
| `static final int GL_ALPHA = 6406` |  |
| `static final int GL_ALPHA_BITS = 3413` |  |
| `static final int GL_ALPHA_TEST = 3008` |  |
| `static final int GL_ALWAYS = 519` |  |
| `static final int GL_AMBIENT = 4608` |  |
| `static final int GL_AMBIENT_AND_DIFFUSE = 5634` |  |
| `static final int GL_AND = 5377` |  |
| `static final int GL_AND_INVERTED = 5380` |  |
| `static final int GL_AND_REVERSE = 5378` |  |
| `static final int GL_BACK = 1029` |  |
| `static final int GL_BLEND = 3042` |  |
| `static final int GL_BLUE_BITS = 3412` |  |
| `static final int GL_BYTE = 5120` |  |
| `static final int GL_CCW = 2305` |  |
| `static final int GL_CLAMP_TO_EDGE = 33071` |  |
| `static final int GL_CLEAR = 5376` |  |
| `static final int GL_COLOR_ARRAY = 32886` |  |
| `static final int GL_COLOR_BUFFER_BIT = 16384` |  |
| `static final int GL_COLOR_LOGIC_OP = 3058` |  |
| `static final int GL_COLOR_MATERIAL = 2903` |  |
| `static final int GL_COMPRESSED_TEXTURE_FORMATS = 34467` |  |
| `static final int GL_CONSTANT_ATTENUATION = 4615` |  |
| `static final int GL_COPY = 5379` |  |
| `static final int GL_COPY_INVERTED = 5388` |  |
| `static final int GL_CULL_FACE = 2884` |  |
| `static final int GL_CW = 2304` |  |
| `static final int GL_DECAL = 8449` |  |
| `static final int GL_DECR = 7683` |  |
| `static final int GL_DEPTH_BITS = 3414` |  |
| `static final int GL_DEPTH_BUFFER_BIT = 256` |  |
| `static final int GL_DEPTH_TEST = 2929` |  |
| `static final int GL_DIFFUSE = 4609` |  |
| `static final int GL_DITHER = 3024` |  |
| `static final int GL_DONT_CARE = 4352` |  |
| `static final int GL_DST_ALPHA = 772` |  |
| `static final int GL_DST_COLOR = 774` |  |
| `static final int GL_EMISSION = 5632` |  |
| `static final int GL_EQUAL = 514` |  |
| `static final int GL_EQUIV = 5385` |  |
| `static final int GL_EXP = 2048` |  |
| `static final int GL_EXP2 = 2049` |  |
| `static final int GL_EXTENSIONS = 7939` |  |
| `static final int GL_FALSE = 0` |  |
| `static final int GL_FASTEST = 4353` |  |
| `static final int GL_FIXED = 5132` |  |
| `static final int GL_FLAT = 7424` |  |
| `static final int GL_FLOAT = 5126` |  |
| `static final int GL_FOG = 2912` |  |
| `static final int GL_FOG_COLOR = 2918` |  |
| `static final int GL_FOG_DENSITY = 2914` |  |
| `static final int GL_FOG_END = 2916` |  |
| `static final int GL_FOG_HINT = 3156` |  |
| `static final int GL_FOG_MODE = 2917` |  |
| `static final int GL_FOG_START = 2915` |  |
| `static final int GL_FRONT = 1028` |  |
| `static final int GL_FRONT_AND_BACK = 1032` |  |
| `static final int GL_GEQUAL = 518` |  |
| `static final int GL_GREATER = 516` |  |
| `static final int GL_GREEN_BITS = 3411` |  |
| `static final int GL_IMPLEMENTATION_COLOR_READ_FORMAT_OES = 35739` |  |
| `static final int GL_IMPLEMENTATION_COLOR_READ_TYPE_OES = 35738` |  |
| `static final int GL_INCR = 7682` |  |
| `static final int GL_INVALID_ENUM = 1280` |  |
| `static final int GL_INVALID_OPERATION = 1282` |  |
| `static final int GL_INVALID_VALUE = 1281` |  |
| `static final int GL_INVERT = 5386` |  |
| `static final int GL_KEEP = 7680` |  |
| `static final int GL_LEQUAL = 515` |  |
| `static final int GL_LESS = 513` |  |
| `static final int GL_LIGHT0 = 16384` |  |
| `static final int GL_LIGHT1 = 16385` |  |
| `static final int GL_LIGHT2 = 16386` |  |
| `static final int GL_LIGHT3 = 16387` |  |
| `static final int GL_LIGHT4 = 16388` |  |
| `static final int GL_LIGHT5 = 16389` |  |
| `static final int GL_LIGHT6 = 16390` |  |
| `static final int GL_LIGHT7 = 16391` |  |
| `static final int GL_LIGHTING = 2896` |  |
| `static final int GL_LIGHT_MODEL_AMBIENT = 2899` |  |
| `static final int GL_LIGHT_MODEL_TWO_SIDE = 2898` |  |
| `static final int GL_LINEAR = 9729` |  |
| `static final int GL_LINEAR_ATTENUATION = 4616` |  |
| `static final int GL_LINEAR_MIPMAP_LINEAR = 9987` |  |
| `static final int GL_LINEAR_MIPMAP_NEAREST = 9985` |  |
| `static final int GL_LINES = 1` |  |
| `static final int GL_LINE_LOOP = 2` |  |
| `static final int GL_LINE_SMOOTH = 2848` |  |
| `static final int GL_LINE_SMOOTH_HINT = 3154` |  |
| `static final int GL_LINE_STRIP = 3` |  |
| `static final int GL_LUMINANCE = 6409` |  |
| `static final int GL_LUMINANCE_ALPHA = 6410` |  |
| `static final int GL_MAX_ELEMENTS_INDICES = 33001` |  |
| `static final int GL_MAX_ELEMENTS_VERTICES = 33000` |  |
| `static final int GL_MAX_LIGHTS = 3377` |  |
| `static final int GL_MAX_MODELVIEW_STACK_DEPTH = 3382` |  |
| `static final int GL_MAX_PROJECTION_STACK_DEPTH = 3384` |  |
| `static final int GL_MAX_TEXTURE_SIZE = 3379` |  |
| `static final int GL_MAX_TEXTURE_STACK_DEPTH = 3385` |  |
| `static final int GL_MAX_TEXTURE_UNITS = 34018` |  |
| `static final int GL_MAX_VIEWPORT_DIMS = 3386` |  |
| `static final int GL_MODELVIEW = 5888` |  |
| `static final int GL_MODULATE = 8448` |  |
| `static final int GL_MULTISAMPLE = 32925` |  |
| `static final int GL_NAND = 5390` |  |
| `static final int GL_NEAREST = 9728` |  |
| `static final int GL_NEAREST_MIPMAP_LINEAR = 9986` |  |
| `static final int GL_NEAREST_MIPMAP_NEAREST = 9984` |  |
| `static final int GL_NEVER = 512` |  |
| `static final int GL_NICEST = 4354` |  |
| `static final int GL_NOOP = 5381` |  |
| `static final int GL_NOR = 5384` |  |
| `static final int GL_NORMALIZE = 2977` |  |
| `static final int GL_NORMAL_ARRAY = 32885` |  |
| `static final int GL_NOTEQUAL = 517` |  |
| `static final int GL_NO_ERROR = 0` |  |
| `static final int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 34466` |  |
| `static final int GL_ONE = 1` |  |
| `static final int GL_ONE_MINUS_DST_ALPHA = 773` |  |
| `static final int GL_ONE_MINUS_DST_COLOR = 775` |  |
| `static final int GL_ONE_MINUS_SRC_ALPHA = 771` |  |
| `static final int GL_ONE_MINUS_SRC_COLOR = 769` |  |
| `static final int GL_OR = 5383` |  |
| `static final int GL_OR_INVERTED = 5389` |  |
| `static final int GL_OR_REVERSE = 5387` |  |
| `static final int GL_OUT_OF_MEMORY = 1285` |  |
| `static final int GL_PACK_ALIGNMENT = 3333` |  |
| `static final int GL_PALETTE4_R5_G6_B5_OES = 35730` |  |
| `static final int GL_PALETTE4_RGB5_A1_OES = 35732` |  |
| `static final int GL_PALETTE4_RGB8_OES = 35728` |  |
| `static final int GL_PALETTE4_RGBA4_OES = 35731` |  |
| `static final int GL_PALETTE4_RGBA8_OES = 35729` |  |
| `static final int GL_PALETTE8_R5_G6_B5_OES = 35735` |  |
| `static final int GL_PALETTE8_RGB5_A1_OES = 35737` |  |
| `static final int GL_PALETTE8_RGB8_OES = 35733` |  |
| `static final int GL_PALETTE8_RGBA4_OES = 35736` |  |
| `static final int GL_PALETTE8_RGBA8_OES = 35734` |  |
| `static final int GL_PERSPECTIVE_CORRECTION_HINT = 3152` |  |
| `static final int GL_POINTS = 0` |  |
| `static final int GL_POINT_FADE_THRESHOLD_SIZE = 33064` |  |
| `static final int GL_POINT_SIZE = 2833` |  |
| `static final int GL_POINT_SMOOTH = 2832` |  |
| `static final int GL_POINT_SMOOTH_HINT = 3153` |  |
| `static final int GL_POLYGON_OFFSET_FILL = 32823` |  |
| `static final int GL_POLYGON_SMOOTH_HINT = 3155` |  |
| `static final int GL_POSITION = 4611` |  |
| `static final int GL_PROJECTION = 5889` |  |
| `static final int GL_QUADRATIC_ATTENUATION = 4617` |  |
| `static final int GL_RED_BITS = 3410` |  |
| `static final int GL_RENDERER = 7937` |  |
| `static final int GL_REPEAT = 10497` |  |
| `static final int GL_REPLACE = 7681` |  |
| `static final int GL_RESCALE_NORMAL = 32826` |  |
| `static final int GL_RGB = 6407` |  |
| `static final int GL_RGBA = 6408` |  |
| `static final int GL_SAMPLE_ALPHA_TO_COVERAGE = 32926` |  |
| `static final int GL_SAMPLE_ALPHA_TO_ONE = 32927` |  |
| `static final int GL_SAMPLE_COVERAGE = 32928` |  |
| `static final int GL_SCISSOR_TEST = 3089` |  |
| `static final int GL_SET = 5391` |  |
| `static final int GL_SHININESS = 5633` |  |
| `static final int GL_SHORT = 5122` |  |
| `static final int GL_SMOOTH = 7425` |  |
| `static final int GL_SMOOTH_LINE_WIDTH_RANGE = 2850` |  |
| `static final int GL_SMOOTH_POINT_SIZE_RANGE = 2834` |  |
| `static final int GL_SPECULAR = 4610` |  |
| `static final int GL_SPOT_CUTOFF = 4614` |  |
| `static final int GL_SPOT_DIRECTION = 4612` |  |
| `static final int GL_SPOT_EXPONENT = 4613` |  |
| `static final int GL_SRC_ALPHA = 770` |  |
| `static final int GL_SRC_ALPHA_SATURATE = 776` |  |
| `static final int GL_SRC_COLOR = 768` |  |
| `static final int GL_STACK_OVERFLOW = 1283` |  |
| `static final int GL_STACK_UNDERFLOW = 1284` |  |
| `static final int GL_STENCIL_BITS = 3415` |  |
| `static final int GL_STENCIL_BUFFER_BIT = 1024` |  |
| `static final int GL_STENCIL_TEST = 2960` |  |
| `static final int GL_SUBPIXEL_BITS = 3408` |  |
| `static final int GL_TEXTURE = 5890` |  |
| `static final int GL_TEXTURE0 = 33984` |  |
| `static final int GL_TEXTURE1 = 33985` |  |
| `static final int GL_TEXTURE10 = 33994` |  |
| `static final int GL_TEXTURE11 = 33995` |  |
| `static final int GL_TEXTURE12 = 33996` |  |
| `static final int GL_TEXTURE13 = 33997` |  |
| `static final int GL_TEXTURE14 = 33998` |  |
| `static final int GL_TEXTURE15 = 33999` |  |
| `static final int GL_TEXTURE16 = 34000` |  |
| `static final int GL_TEXTURE17 = 34001` |  |
| `static final int GL_TEXTURE18 = 34002` |  |
| `static final int GL_TEXTURE19 = 34003` |  |
| `static final int GL_TEXTURE2 = 33986` |  |
| `static final int GL_TEXTURE20 = 34004` |  |
| `static final int GL_TEXTURE21 = 34005` |  |
| `static final int GL_TEXTURE22 = 34006` |  |
| `static final int GL_TEXTURE23 = 34007` |  |
| `static final int GL_TEXTURE24 = 34008` |  |
| `static final int GL_TEXTURE25 = 34009` |  |
| `static final int GL_TEXTURE26 = 34010` |  |
| `static final int GL_TEXTURE27 = 34011` |  |
| `static final int GL_TEXTURE28 = 34012` |  |
| `static final int GL_TEXTURE29 = 34013` |  |
| `static final int GL_TEXTURE3 = 33987` |  |
| `static final int GL_TEXTURE30 = 34014` |  |
| `static final int GL_TEXTURE31 = 34015` |  |
| `static final int GL_TEXTURE4 = 33988` |  |
| `static final int GL_TEXTURE5 = 33989` |  |
| `static final int GL_TEXTURE6 = 33990` |  |
| `static final int GL_TEXTURE7 = 33991` |  |
| `static final int GL_TEXTURE8 = 33992` |  |
| `static final int GL_TEXTURE9 = 33993` |  |
| `static final int GL_TEXTURE_2D = 3553` |  |
| `static final int GL_TEXTURE_COORD_ARRAY = 32888` |  |
| `static final int GL_TEXTURE_ENV = 8960` |  |
| `static final int GL_TEXTURE_ENV_COLOR = 8705` |  |
| `static final int GL_TEXTURE_ENV_MODE = 8704` |  |
| `static final int GL_TEXTURE_MAG_FILTER = 10240` |  |
| `static final int GL_TEXTURE_MIN_FILTER = 10241` |  |
| `static final int GL_TEXTURE_WRAP_S = 10242` |  |
| `static final int GL_TEXTURE_WRAP_T = 10243` |  |
| `static final int GL_TRIANGLES = 4` |  |
| `static final int GL_TRIANGLE_FAN = 6` |  |
| `static final int GL_TRIANGLE_STRIP = 5` |  |
| `static final int GL_TRUE = 1` |  |
| `static final int GL_UNPACK_ALIGNMENT = 3317` |  |
| `static final int GL_UNSIGNED_BYTE = 5121` |  |
| `static final int GL_UNSIGNED_SHORT = 5123` |  |
| `static final int GL_UNSIGNED_SHORT_4_4_4_4 = 32819` |  |
| `static final int GL_UNSIGNED_SHORT_5_5_5_1 = 32820` |  |
| `static final int GL_UNSIGNED_SHORT_5_6_5 = 33635` |  |
| `static final int GL_VENDOR = 7936` |  |
| `static final int GL_VERSION = 7938` |  |
| `static final int GL_VERTEX_ARRAY = 32884` |  |
| `static final int GL_XOR = 5382` |  |
| `static final int GL_ZERO = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void glActiveTexture(int)` |  |
| `void glAlphaFunc(int, float)` |  |
| `void glAlphaFuncx(int, int)` |  |
| `void glBindTexture(int, int)` |  |
| `void glBlendFunc(int, int)` |  |
| `void glClear(int)` |  |
| `void glClearColor(float, float, float, float)` |  |
| `void glClearColorx(int, int, int, int)` |  |
| `void glClearDepthf(float)` |  |
| `void glClearDepthx(int)` |  |
| `void glClearStencil(int)` |  |
| `void glClientActiveTexture(int)` |  |
| `void glColor4f(float, float, float, float)` |  |
| `void glColor4x(int, int, int, int)` |  |
| `void glColorMask(boolean, boolean, boolean, boolean)` |  |
| `void glColorPointer(int, int, int, java.nio.Buffer)` |  |
| `void glCompressedTexImage2D(int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `void glCompressedTexSubImage2D(int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `void glCopyTexImage2D(int, int, int, int, int, int, int, int)` |  |
| `void glCopyTexSubImage2D(int, int, int, int, int, int, int, int)` |  |
| `void glCullFace(int)` |  |
| `void glDeleteTextures(int, int[], int)` |  |
| `void glDeleteTextures(int, java.nio.IntBuffer)` |  |
| `void glDepthFunc(int)` |  |
| `void glDepthMask(boolean)` |  |
| `void glDepthRangef(float, float)` |  |
| `void glDepthRangex(int, int)` |  |
| `void glDisable(int)` |  |
| `void glDisableClientState(int)` |  |
| `void glDrawArrays(int, int, int)` |  |
| `void glDrawElements(int, int, int, java.nio.Buffer)` |  |
| `void glEnable(int)` |  |
| `void glEnableClientState(int)` |  |
| `void glFinish()` |  |
| `void glFlush()` |  |
| `void glFogf(int, float)` |  |
| `void glFogfv(int, float[], int)` |  |
| `void glFogfv(int, java.nio.FloatBuffer)` |  |
| `void glFogx(int, int)` |  |
| `void glFogxv(int, int[], int)` |  |
| `void glFogxv(int, java.nio.IntBuffer)` |  |
| `void glFrontFace(int)` |  |
| `void glFrustumf(float, float, float, float, float, float)` |  |
| `void glFrustumx(int, int, int, int, int, int)` |  |
| `void glGenTextures(int, int[], int)` |  |
| `void glGenTextures(int, java.nio.IntBuffer)` |  |
| `int glGetError()` |  |
| `void glGetIntegerv(int, int[], int)` |  |
| `void glGetIntegerv(int, java.nio.IntBuffer)` |  |
| `String glGetString(int)` |  |
| `void glHint(int, int)` |  |
| `void glLightModelf(int, float)` |  |
| `void glLightModelfv(int, float[], int)` |  |
| `void glLightModelfv(int, java.nio.FloatBuffer)` |  |
| `void glLightModelx(int, int)` |  |
| `void glLightModelxv(int, int[], int)` |  |
| `void glLightModelxv(int, java.nio.IntBuffer)` |  |
| `void glLightf(int, int, float)` |  |
| `void glLightfv(int, int, float[], int)` |  |
| `void glLightfv(int, int, java.nio.FloatBuffer)` |  |
| `void glLightx(int, int, int)` |  |
| `void glLightxv(int, int, int[], int)` |  |
| `void glLightxv(int, int, java.nio.IntBuffer)` |  |
| `void glLineWidth(float)` |  |
| `void glLineWidthx(int)` |  |
| `void glLoadIdentity()` |  |
| `void glLoadMatrixf(float[], int)` |  |
| `void glLoadMatrixf(java.nio.FloatBuffer)` |  |
| `void glLoadMatrixx(int[], int)` |  |
| `void glLoadMatrixx(java.nio.IntBuffer)` |  |
| `void glLogicOp(int)` |  |
| `void glMaterialf(int, int, float)` |  |
| `void glMaterialfv(int, int, float[], int)` |  |
| `void glMaterialfv(int, int, java.nio.FloatBuffer)` |  |
| `void glMaterialx(int, int, int)` |  |
| `void glMaterialxv(int, int, int[], int)` |  |
| `void glMaterialxv(int, int, java.nio.IntBuffer)` |  |
| `void glMatrixMode(int)` |  |
| `void glMultMatrixf(float[], int)` |  |
| `void glMultMatrixf(java.nio.FloatBuffer)` |  |
| `void glMultMatrixx(int[], int)` |  |
| `void glMultMatrixx(java.nio.IntBuffer)` |  |
| `void glMultiTexCoord4f(int, float, float, float, float)` |  |
| `void glMultiTexCoord4x(int, int, int, int, int)` |  |
| `void glNormal3f(float, float, float)` |  |
| `void glNormal3x(int, int, int)` |  |
| `void glNormalPointer(int, int, java.nio.Buffer)` |  |
| `void glOrthof(float, float, float, float, float, float)` |  |
| `void glOrthox(int, int, int, int, int, int)` |  |
| `void glPixelStorei(int, int)` |  |
| `void glPointSize(float)` |  |
| `void glPointSizex(int)` |  |
| `void glPolygonOffset(float, float)` |  |
| `void glPolygonOffsetx(int, int)` |  |
| `void glPopMatrix()` |  |
| `void glPushMatrix()` |  |
| `void glReadPixels(int, int, int, int, int, int, java.nio.Buffer)` |  |
| `void glRotatef(float, float, float, float)` |  |
| `void glRotatex(int, int, int, int)` |  |
| `void glSampleCoverage(float, boolean)` |  |
| `void glSampleCoveragex(int, boolean)` |  |
| `void glScalef(float, float, float)` |  |
| `void glScalex(int, int, int)` |  |
| `void glScissor(int, int, int, int)` |  |
| `void glShadeModel(int)` |  |
| `void glStencilFunc(int, int, int)` |  |
| `void glStencilMask(int)` |  |
| `void glStencilOp(int, int, int)` |  |
| `void glTexCoordPointer(int, int, int, java.nio.Buffer)` |  |
| `void glTexEnvf(int, int, float)` |  |
| `void glTexEnvfv(int, int, float[], int)` |  |
| `void glTexEnvfv(int, int, java.nio.FloatBuffer)` |  |
| `void glTexEnvx(int, int, int)` |  |
| `void glTexEnvxv(int, int, int[], int)` |  |
| `void glTexEnvxv(int, int, java.nio.IntBuffer)` |  |
| `void glTexImage2D(int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `void glTexParameterf(int, int, float)` |  |
| `void glTexParameterx(int, int, int)` |  |
| `void glTexSubImage2D(int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `void glTranslatef(float, float, float)` |  |
| `void glTranslatex(int, int, int)` |  |
| `void glVertexPointer(int, int, int, java.nio.Buffer)` |  |
| `void glViewport(int, int, int, int)` |  |

---

### `interface GL10Ext`

- **继承：** `javax.microedition.khronos.opengles.GL`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int glQueryMatrixxOES(int[], int, int[], int)` |  |
| `int glQueryMatrixxOES(java.nio.IntBuffer, java.nio.IntBuffer)` |  |

---

### `interface GL11`

- **继承：** `javax.microedition.khronos.opengles.GL10`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GL_ACTIVE_TEXTURE = 34016` |  |
| `static final int GL_ADD_SIGNED = 34164` |  |
| `static final int GL_ALPHA_SCALE = 3356` |  |
| `static final int GL_ALPHA_TEST_FUNC = 3009` |  |
| `static final int GL_ALPHA_TEST_REF = 3010` |  |
| `static final int GL_ARRAY_BUFFER = 34962` |  |
| `static final int GL_ARRAY_BUFFER_BINDING = 34964` |  |
| `static final int GL_BLEND_DST = 3040` |  |
| `static final int GL_BLEND_SRC = 3041` |  |
| `static final int GL_BUFFER_ACCESS = 35003` |  |
| `static final int GL_BUFFER_SIZE = 34660` |  |
| `static final int GL_BUFFER_USAGE = 34661` |  |
| `static final int GL_CLIENT_ACTIVE_TEXTURE = 34017` |  |
| `static final int GL_CLIP_PLANE0 = 12288` |  |
| `static final int GL_CLIP_PLANE1 = 12289` |  |
| `static final int GL_CLIP_PLANE2 = 12290` |  |
| `static final int GL_CLIP_PLANE3 = 12291` |  |
| `static final int GL_CLIP_PLANE4 = 12292` |  |
| `static final int GL_CLIP_PLANE5 = 12293` |  |
| `static final int GL_COLOR_ARRAY_BUFFER_BINDING = 34968` |  |
| `static final int GL_COLOR_ARRAY_POINTER = 32912` |  |
| `static final int GL_COLOR_ARRAY_SIZE = 32897` |  |
| `static final int GL_COLOR_ARRAY_STRIDE = 32899` |  |
| `static final int GL_COLOR_ARRAY_TYPE = 32898` |  |
| `static final int GL_COLOR_CLEAR_VALUE = 3106` |  |
| `static final int GL_COLOR_WRITEMASK = 3107` |  |
| `static final int GL_COMBINE = 34160` |  |
| `static final int GL_COMBINE_ALPHA = 34162` |  |
| `static final int GL_COMBINE_RGB = 34161` |  |
| `static final int GL_CONSTANT = 34166` |  |
| `static final int GL_COORD_REPLACE_OES = 34914` |  |
| `static final int GL_CULL_FACE_MODE = 2885` |  |
| `static final int GL_CURRENT_COLOR = 2816` |  |
| `static final int GL_CURRENT_NORMAL = 2818` |  |
| `static final int GL_CURRENT_TEXTURE_COORDS = 2819` |  |
| `static final int GL_DEPTH_CLEAR_VALUE = 2931` |  |
| `static final int GL_DEPTH_FUNC = 2932` |  |
| `static final int GL_DEPTH_RANGE = 2928` |  |
| `static final int GL_DEPTH_WRITEMASK = 2930` |  |
| `static final int GL_DOT3_RGB = 34478` |  |
| `static final int GL_DOT3_RGBA = 34479` |  |
| `static final int GL_DYNAMIC_DRAW = 35048` |  |
| `static final int GL_ELEMENT_ARRAY_BUFFER = 34963` |  |
| `static final int GL_ELEMENT_ARRAY_BUFFER_BINDING = 34965` |  |
| `static final int GL_FRONT_FACE = 2886` |  |
| `static final int GL_GENERATE_MIPMAP = 33169` |  |
| `static final int GL_GENERATE_MIPMAP_HINT = 33170` |  |
| `static final int GL_INTERPOLATE = 34165` |  |
| `static final int GL_LINE_WIDTH = 2849` |  |
| `static final int GL_LOGIC_OP_MODE = 3056` |  |
| `static final int GL_MATRIX_MODE = 2976` |  |
| `static final int GL_MAX_CLIP_PLANES = 3378` |  |
| `static final int GL_MODELVIEW_MATRIX = 2982` |  |
| `static final int GL_MODELVIEW_MATRIX_FLOAT_AS_INT_BITS_OES = 35213` |  |
| `static final int GL_MODELVIEW_STACK_DEPTH = 2979` |  |
| `static final int GL_NORMAL_ARRAY_BUFFER_BINDING = 34967` |  |
| `static final int GL_NORMAL_ARRAY_POINTER = 32911` |  |
| `static final int GL_NORMAL_ARRAY_STRIDE = 32895` |  |
| `static final int GL_NORMAL_ARRAY_TYPE = 32894` |  |
| `static final int GL_OPERAND0_ALPHA = 34200` |  |
| `static final int GL_OPERAND0_RGB = 34192` |  |
| `static final int GL_OPERAND1_ALPHA = 34201` |  |
| `static final int GL_OPERAND1_RGB = 34193` |  |
| `static final int GL_OPERAND2_ALPHA = 34202` |  |
| `static final int GL_OPERAND2_RGB = 34194` |  |
| `static final int GL_POINT_DISTANCE_ATTENUATION = 33065` |  |
| `static final int GL_POINT_FADE_THRESHOLD_SIZE = 33064` |  |
| `static final int GL_POINT_SIZE = 2833` |  |
| `static final int GL_POINT_SIZE_ARRAY_BUFFER_BINDING_OES = 35743` |  |
| `static final int GL_POINT_SIZE_ARRAY_OES = 35740` |  |
| `static final int GL_POINT_SIZE_ARRAY_POINTER_OES = 35212` |  |
| `static final int GL_POINT_SIZE_ARRAY_STRIDE_OES = 35211` |  |
| `static final int GL_POINT_SIZE_ARRAY_TYPE_OES = 35210` |  |
| `static final int GL_POINT_SIZE_MAX = 33063` |  |
| `static final int GL_POINT_SIZE_MIN = 33062` |  |
| `static final int GL_POINT_SPRITE_OES = 34913` |  |
| `static final int GL_POLYGON_OFFSET_FACTOR = 32824` |  |
| `static final int GL_POLYGON_OFFSET_UNITS = 10752` |  |
| `static final int GL_PREVIOUS = 34168` |  |
| `static final int GL_PRIMARY_COLOR = 34167` |  |
| `static final int GL_PROJECTION_MATRIX = 2983` |  |
| `static final int GL_PROJECTION_MATRIX_FLOAT_AS_INT_BITS_OES = 35214` |  |
| `static final int GL_PROJECTION_STACK_DEPTH = 2980` |  |
| `static final int GL_RGB_SCALE = 34163` |  |
| `static final int GL_SAMPLES = 32937` |  |
| `static final int GL_SAMPLE_BUFFERS = 32936` |  |
| `static final int GL_SAMPLE_COVERAGE_INVERT = 32939` |  |
| `static final int GL_SAMPLE_COVERAGE_VALUE = 32938` |  |
| `static final int GL_SCISSOR_BOX = 3088` |  |
| `static final int GL_SHADE_MODEL = 2900` |  |
| `static final int GL_SRC0_ALPHA = 34184` |  |
| `static final int GL_SRC0_RGB = 34176` |  |
| `static final int GL_SRC1_ALPHA = 34185` |  |
| `static final int GL_SRC1_RGB = 34177` |  |
| `static final int GL_SRC2_ALPHA = 34186` |  |
| `static final int GL_SRC2_RGB = 34178` |  |
| `static final int GL_STATIC_DRAW = 35044` |  |
| `static final int GL_STENCIL_CLEAR_VALUE = 2961` |  |
| `static final int GL_STENCIL_FAIL = 2964` |  |
| `static final int GL_STENCIL_FUNC = 2962` |  |
| `static final int GL_STENCIL_PASS_DEPTH_FAIL = 2965` |  |
| `static final int GL_STENCIL_PASS_DEPTH_PASS = 2966` |  |
| `static final int GL_STENCIL_REF = 2967` |  |
| `static final int GL_STENCIL_VALUE_MASK = 2963` |  |
| `static final int GL_STENCIL_WRITEMASK = 2968` |  |
| `static final int GL_SUBTRACT = 34023` |  |
| `static final int GL_TEXTURE_BINDING_2D = 32873` |  |
| `static final int GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING = 34970` |  |
| `static final int GL_TEXTURE_COORD_ARRAY_POINTER = 32914` |  |
| `static final int GL_TEXTURE_COORD_ARRAY_SIZE = 32904` |  |
| `static final int GL_TEXTURE_COORD_ARRAY_STRIDE = 32906` |  |
| `static final int GL_TEXTURE_COORD_ARRAY_TYPE = 32905` |  |
| `static final int GL_TEXTURE_MATRIX = 2984` |  |
| `static final int GL_TEXTURE_MATRIX_FLOAT_AS_INT_BITS_OES = 35215` |  |
| `static final int GL_TEXTURE_STACK_DEPTH = 2981` |  |
| `static final int GL_VERTEX_ARRAY_BUFFER_BINDING = 34966` |  |
| `static final int GL_VERTEX_ARRAY_POINTER = 32910` |  |
| `static final int GL_VERTEX_ARRAY_SIZE = 32890` |  |
| `static final int GL_VERTEX_ARRAY_STRIDE = 32892` |  |
| `static final int GL_VERTEX_ARRAY_TYPE = 32891` |  |
| `static final int GL_VIEWPORT = 2978` |  |
| `static final int GL_WRITE_ONLY = 35001` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void glBindBuffer(int, int)` |  |
| `void glBufferData(int, int, java.nio.Buffer, int)` |  |
| `void glBufferSubData(int, int, int, java.nio.Buffer)` |  |
| `void glClipPlanef(int, float[], int)` |  |
| `void glClipPlanef(int, java.nio.FloatBuffer)` |  |
| `void glClipPlanex(int, int[], int)` |  |
| `void glClipPlanex(int, java.nio.IntBuffer)` |  |
| `void glColor4ub(byte, byte, byte, byte)` |  |
| `void glColorPointer(int, int, int, int)` |  |
| `void glDeleteBuffers(int, int[], int)` |  |
| `void glDeleteBuffers(int, java.nio.IntBuffer)` |  |
| `void glDrawElements(int, int, int, int)` |  |
| `void glGenBuffers(int, int[], int)` |  |
| `void glGenBuffers(int, java.nio.IntBuffer)` |  |
| `void glGetBooleanv(int, boolean[], int)` |  |
| `void glGetBooleanv(int, java.nio.IntBuffer)` |  |
| `void glGetBufferParameteriv(int, int, int[], int)` |  |
| `void glGetBufferParameteriv(int, int, java.nio.IntBuffer)` |  |
| `void glGetClipPlanef(int, float[], int)` |  |
| `void glGetClipPlanef(int, java.nio.FloatBuffer)` |  |
| `void glGetClipPlanex(int, int[], int)` |  |
| `void glGetClipPlanex(int, java.nio.IntBuffer)` |  |
| `void glGetFixedv(int, int[], int)` |  |
| `void glGetFixedv(int, java.nio.IntBuffer)` |  |
| `void glGetFloatv(int, float[], int)` |  |
| `void glGetFloatv(int, java.nio.FloatBuffer)` |  |
| `void glGetLightfv(int, int, float[], int)` |  |
| `void glGetLightfv(int, int, java.nio.FloatBuffer)` |  |
| `void glGetLightxv(int, int, int[], int)` |  |
| `void glGetLightxv(int, int, java.nio.IntBuffer)` |  |
| `void glGetMaterialfv(int, int, float[], int)` |  |
| `void glGetMaterialfv(int, int, java.nio.FloatBuffer)` |  |
| `void glGetMaterialxv(int, int, int[], int)` |  |
| `void glGetMaterialxv(int, int, java.nio.IntBuffer)` |  |
| `void glGetPointerv(int, java.nio.Buffer[])` |  |
| `void glGetTexEnviv(int, int, int[], int)` |  |
| `void glGetTexEnviv(int, int, java.nio.IntBuffer)` |  |
| `void glGetTexEnvxv(int, int, int[], int)` |  |
| `void glGetTexEnvxv(int, int, java.nio.IntBuffer)` |  |
| `void glGetTexParameterfv(int, int, float[], int)` |  |
| `void glGetTexParameterfv(int, int, java.nio.FloatBuffer)` |  |
| `void glGetTexParameteriv(int, int, int[], int)` |  |
| `void glGetTexParameteriv(int, int, java.nio.IntBuffer)` |  |
| `void glGetTexParameterxv(int, int, int[], int)` |  |
| `void glGetTexParameterxv(int, int, java.nio.IntBuffer)` |  |
| `boolean glIsBuffer(int)` |  |
| `boolean glIsEnabled(int)` |  |
| `boolean glIsTexture(int)` |  |
| `void glNormalPointer(int, int, int)` |  |
| `void glPointParameterf(int, float)` |  |
| `void glPointParameterfv(int, float[], int)` |  |
| `void glPointParameterfv(int, java.nio.FloatBuffer)` |  |
| `void glPointParameterx(int, int)` |  |
| `void glPointParameterxv(int, int[], int)` |  |
| `void glPointParameterxv(int, java.nio.IntBuffer)` |  |
| `void glPointSizePointerOES(int, int, java.nio.Buffer)` |  |
| `void glTexCoordPointer(int, int, int, int)` |  |
| `void glTexEnvi(int, int, int)` |  |
| `void glTexEnviv(int, int, int[], int)` |  |
| `void glTexEnviv(int, int, java.nio.IntBuffer)` |  |
| `void glTexParameterfv(int, int, float[], int)` |  |
| `void glTexParameterfv(int, int, java.nio.FloatBuffer)` |  |
| `void glTexParameteri(int, int, int)` |  |
| `void glTexParameteriv(int, int, int[], int)` |  |
| `void glTexParameteriv(int, int, java.nio.IntBuffer)` |  |
| `void glTexParameterxv(int, int, int[], int)` |  |
| `void glTexParameterxv(int, int, java.nio.IntBuffer)` |  |
| `void glVertexPointer(int, int, int, int)` |  |

---

### `interface GL11Ext`

- **继承：** `javax.microedition.khronos.opengles.GL`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GL_MATRIX_INDEX_ARRAY_BUFFER_BINDING_OES = 35742` |  |
| `static final int GL_MATRIX_INDEX_ARRAY_OES = 34884` |  |
| `static final int GL_MATRIX_INDEX_ARRAY_POINTER_OES = 34889` |  |
| `static final int GL_MATRIX_INDEX_ARRAY_SIZE_OES = 34886` |  |
| `static final int GL_MATRIX_INDEX_ARRAY_STRIDE_OES = 34888` |  |
| `static final int GL_MATRIX_INDEX_ARRAY_TYPE_OES = 34887` |  |
| `static final int GL_MATRIX_PALETTE_OES = 34880` |  |
| `static final int GL_MAX_PALETTE_MATRICES_OES = 34882` |  |
| `static final int GL_MAX_VERTEX_UNITS_OES = 34468` |  |
| `static final int GL_TEXTURE_CROP_RECT_OES = 35741` |  |
| `static final int GL_WEIGHT_ARRAY_BUFFER_BINDING_OES = 34974` |  |
| `static final int GL_WEIGHT_ARRAY_OES = 34477` |  |
| `static final int GL_WEIGHT_ARRAY_POINTER_OES = 34476` |  |
| `static final int GL_WEIGHT_ARRAY_SIZE_OES = 34475` |  |
| `static final int GL_WEIGHT_ARRAY_STRIDE_OES = 34474` |  |
| `static final int GL_WEIGHT_ARRAY_TYPE_OES = 34473` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void glCurrentPaletteMatrixOES(int)` |  |
| `void glDrawTexfOES(float, float, float, float, float)` |  |
| `void glDrawTexfvOES(float[], int)` |  |
| `void glDrawTexfvOES(java.nio.FloatBuffer)` |  |
| `void glDrawTexiOES(int, int, int, int, int)` |  |
| `void glDrawTexivOES(int[], int)` |  |
| `void glDrawTexivOES(java.nio.IntBuffer)` |  |
| `void glDrawTexsOES(short, short, short, short, short)` |  |
| `void glDrawTexsvOES(short[], int)` |  |
| `void glDrawTexsvOES(java.nio.ShortBuffer)` |  |
| `void glDrawTexxOES(int, int, int, int, int)` |  |
| `void glDrawTexxvOES(int[], int)` |  |
| `void glDrawTexxvOES(java.nio.IntBuffer)` |  |
| `void glEnable(int)` |  |
| `void glEnableClientState(int)` |  |
| `void glLoadPaletteFromModelViewMatrixOES()` |  |
| `void glMatrixIndexPointerOES(int, int, int, java.nio.Buffer)` |  |
| `void glMatrixIndexPointerOES(int, int, int, int)` |  |
| `void glTexParameterfv(int, int, float[], int)` |  |
| `void glWeightPointerOES(int, int, int, java.nio.Buffer)` |  |
| `void glWeightPointerOES(int, int, int, int)` |  |

---

### `interface GL11ExtensionPack`

- **继承：** `javax.microedition.khronos.opengles.GL`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GL_BLEND_DST_ALPHA = 32970` |  |
| `static final int GL_BLEND_DST_RGB = 32968` |  |
| `static final int GL_BLEND_EQUATION = 32777` |  |
| `static final int GL_BLEND_EQUATION_ALPHA = 34877` |  |
| `static final int GL_BLEND_EQUATION_RGB = 32777` |  |
| `static final int GL_BLEND_SRC_ALPHA = 32971` |  |
| `static final int GL_BLEND_SRC_RGB = 32969` |  |
| `static final int GL_COLOR_ATTACHMENT0_OES = 36064` |  |
| `static final int GL_COLOR_ATTACHMENT10_OES = 36074` |  |
| `static final int GL_COLOR_ATTACHMENT11_OES = 36075` |  |
| `static final int GL_COLOR_ATTACHMENT12_OES = 36076` |  |
| `static final int GL_COLOR_ATTACHMENT13_OES = 36077` |  |
| `static final int GL_COLOR_ATTACHMENT14_OES = 36078` |  |
| `static final int GL_COLOR_ATTACHMENT15_OES = 36079` |  |
| `static final int GL_COLOR_ATTACHMENT1_OES = 36065` |  |
| `static final int GL_COLOR_ATTACHMENT2_OES = 36066` |  |
| `static final int GL_COLOR_ATTACHMENT3_OES = 36067` |  |
| `static final int GL_COLOR_ATTACHMENT4_OES = 36068` |  |
| `static final int GL_COLOR_ATTACHMENT5_OES = 36069` |  |
| `static final int GL_COLOR_ATTACHMENT6_OES = 36070` |  |
| `static final int GL_COLOR_ATTACHMENT7_OES = 36071` |  |
| `static final int GL_COLOR_ATTACHMENT8_OES = 36072` |  |
| `static final int GL_COLOR_ATTACHMENT9_OES = 36073` |  |
| `static final int GL_DECR_WRAP = 34056` |  |
| `static final int GL_DEPTH_ATTACHMENT_OES = 36096` |  |
| `static final int GL_DEPTH_COMPONENT = 6402` |  |
| `static final int GL_DEPTH_COMPONENT16 = 33189` |  |
| `static final int GL_DEPTH_COMPONENT24 = 33190` |  |
| `static final int GL_DEPTH_COMPONENT32 = 33191` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME_OES = 36049` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE_OES = 36048` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE_OES = 36051` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL_OES = 36050` |  |
| `static final int GL_FRAMEBUFFER_BINDING_OES = 36006` |  |
| `static final int GL_FRAMEBUFFER_COMPLETE_OES = 36053` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_OES = 36054` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_OES = 36057` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_OES = 36059` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_FORMATS_OES = 36058` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_OES = 36055` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_OES = 36060` |  |
| `static final int GL_FRAMEBUFFER_OES = 36160` |  |
| `static final int GL_FRAMEBUFFER_UNSUPPORTED_OES = 36061` |  |
| `static final int GL_FUNC_ADD = 32774` |  |
| `static final int GL_FUNC_REVERSE_SUBTRACT = 32779` |  |
| `static final int GL_FUNC_SUBTRACT = 32778` |  |
| `static final int GL_INCR_WRAP = 34055` |  |
| `static final int GL_INVALID_FRAMEBUFFER_OPERATION_OES = 1286` |  |
| `static final int GL_MAX_COLOR_ATTACHMENTS_OES = 36063` |  |
| `static final int GL_MAX_CUBE_MAP_TEXTURE_SIZE = 34076` |  |
| `static final int GL_MAX_RENDERBUFFER_SIZE_OES = 34024` |  |
| `static final int GL_MIRRORED_REPEAT = 33648` |  |
| `static final int GL_NORMAL_MAP = 34065` |  |
| `static final int GL_REFLECTION_MAP = 34066` |  |
| `static final int GL_RENDERBUFFER_ALPHA_SIZE_OES = 36179` |  |
| `static final int GL_RENDERBUFFER_BINDING_OES = 36007` |  |
| `static final int GL_RENDERBUFFER_BLUE_SIZE_OES = 36178` |  |
| `static final int GL_RENDERBUFFER_DEPTH_SIZE_OES = 36180` |  |
| `static final int GL_RENDERBUFFER_GREEN_SIZE_OES = 36177` |  |
| `static final int GL_RENDERBUFFER_HEIGHT_OES = 36163` |  |
| `static final int GL_RENDERBUFFER_INTERNAL_FORMAT_OES = 36164` |  |
| `static final int GL_RENDERBUFFER_OES = 36161` |  |
| `static final int GL_RENDERBUFFER_RED_SIZE_OES = 36176` |  |
| `static final int GL_RENDERBUFFER_STENCIL_SIZE_OES = 36181` |  |
| `static final int GL_RENDERBUFFER_WIDTH_OES = 36162` |  |
| `static final int GL_RGB565_OES = 36194` |  |
| `static final int GL_RGB5_A1 = 32855` |  |
| `static final int GL_RGB8 = 32849` |  |
| `static final int GL_RGBA4 = 32854` |  |
| `static final int GL_RGBA8 = 32856` |  |
| `static final int GL_STENCIL_ATTACHMENT_OES = 36128` |  |
| `static final int GL_STENCIL_INDEX = 6401` |  |
| `static final int GL_STENCIL_INDEX1_OES = 36166` |  |
| `static final int GL_STENCIL_INDEX4_OES = 36167` |  |
| `static final int GL_STENCIL_INDEX8_OES = 36168` |  |
| `static final int GL_STR = -1` |  |
| `static final int GL_TEXTURE_BINDING_CUBE_MAP = 34068` |  |
| `static final int GL_TEXTURE_CUBE_MAP = 34067` |  |
| `static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 34070` |  |
| `static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 34072` |  |
| `static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 34074` |  |
| `static final int GL_TEXTURE_CUBE_MAP_POSITIVE_X = 34069` |  |
| `static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 34071` |  |
| `static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 34073` |  |
| `static final int GL_TEXTURE_GEN_MODE = 9472` |  |
| `static final int GL_TEXTURE_GEN_STR = 36192` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void glBindFramebufferOES(int, int)` |  |
| `void glBindRenderbufferOES(int, int)` |  |
| `void glBindTexture(int, int)` |  |
| `void glBlendEquation(int)` |  |
| `void glBlendEquationSeparate(int, int)` |  |
| `void glBlendFuncSeparate(int, int, int, int)` |  |
| `int glCheckFramebufferStatusOES(int)` |  |
| `void glCompressedTexImage2D(int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `void glCopyTexImage2D(int, int, int, int, int, int, int, int)` |  |
| `void glDeleteFramebuffersOES(int, int[], int)` |  |
| `void glDeleteFramebuffersOES(int, java.nio.IntBuffer)` |  |
| `void glDeleteRenderbuffersOES(int, int[], int)` |  |
| `void glDeleteRenderbuffersOES(int, java.nio.IntBuffer)` |  |
| `void glEnable(int)` |  |
| `void glFramebufferRenderbufferOES(int, int, int, int)` |  |
| `void glFramebufferTexture2DOES(int, int, int, int, int)` |  |
| `void glGenFramebuffersOES(int, int[], int)` |  |
| `void glGenFramebuffersOES(int, java.nio.IntBuffer)` |  |
| `void glGenRenderbuffersOES(int, int[], int)` |  |
| `void glGenRenderbuffersOES(int, java.nio.IntBuffer)` |  |
| `void glGenerateMipmapOES(int)` |  |
| `void glGetFramebufferAttachmentParameterivOES(int, int, int, int[], int)` |  |
| `void glGetFramebufferAttachmentParameterivOES(int, int, int, java.nio.IntBuffer)` |  |
| `void glGetIntegerv(int, int[], int)` |  |
| `void glGetIntegerv(int, java.nio.IntBuffer)` |  |
| `void glGetRenderbufferParameterivOES(int, int, int[], int)` |  |
| `void glGetRenderbufferParameterivOES(int, int, java.nio.IntBuffer)` |  |
| `void glGetTexGenfv(int, int, float[], int)` |  |
| `void glGetTexGenfv(int, int, java.nio.FloatBuffer)` |  |
| `void glGetTexGeniv(int, int, int[], int)` |  |
| `void glGetTexGeniv(int, int, java.nio.IntBuffer)` |  |
| `void glGetTexGenxv(int, int, int[], int)` |  |
| `void glGetTexGenxv(int, int, java.nio.IntBuffer)` |  |
| `boolean glIsFramebufferOES(int)` |  |
| `boolean glIsRenderbufferOES(int)` |  |
| `void glRenderbufferStorageOES(int, int, int, int)` |  |
| `void glStencilOp(int, int, int)` |  |
| `void glTexEnvf(int, int, float)` |  |
| `void glTexEnvfv(int, int, float[], int)` |  |
| `void glTexEnvfv(int, int, java.nio.FloatBuffer)` |  |
| `void glTexEnvx(int, int, int)` |  |
| `void glTexEnvxv(int, int, int[], int)` |  |
| `void glTexEnvxv(int, int, java.nio.IntBuffer)` |  |
| `void glTexGenf(int, int, float)` |  |
| `void glTexGenfv(int, int, float[], int)` |  |
| `void glTexGenfv(int, int, java.nio.FloatBuffer)` |  |
| `void glTexGeni(int, int, int)` |  |
| `void glTexGeniv(int, int, int[], int)` |  |
| `void glTexGeniv(int, int, java.nio.IntBuffer)` |  |
| `void glTexGenx(int, int, int)` |  |
| `void glTexGenxv(int, int, int[], int)` |  |
| `void glTexGenxv(int, int, java.nio.IntBuffer)` |  |
| `void glTexParameterf(int, int, float)` |  |

---

## Package: `javax.net`

### `class abstract ServerSocketFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ServerSocketFactory()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.net.ServerSocket createServerSocket() throws java.io.IOException` |  |
| `abstract java.net.ServerSocket createServerSocket(int) throws java.io.IOException` |  |
| `abstract java.net.ServerSocket createServerSocket(int, int) throws java.io.IOException` |  |
| `abstract java.net.ServerSocket createServerSocket(int, int, java.net.InetAddress) throws java.io.IOException` |  |
| `static javax.net.ServerSocketFactory getDefault()` |  |

---

### `class abstract SocketFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SocketFactory()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.net.Socket createSocket() throws java.io.IOException` |  |
| `abstract java.net.Socket createSocket(String, int) throws java.io.IOException, java.net.UnknownHostException` |  |
| `abstract java.net.Socket createSocket(String, int, java.net.InetAddress, int) throws java.io.IOException, java.net.UnknownHostException` |  |
| `abstract java.net.Socket createSocket(java.net.InetAddress, int) throws java.io.IOException` |  |
| `abstract java.net.Socket createSocket(java.net.InetAddress, int, java.net.InetAddress, int) throws java.io.IOException` |  |
| `static javax.net.SocketFactory getDefault()` |  |

---

## Package: `javax.net.ssl`

### `class CertPathTrustManagerParameters`

- **实现：** `javax.net.ssl.ManagerFactoryParameters`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CertPathTrustManagerParameters(java.security.cert.CertPathParameters)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.security.cert.CertPathParameters getParameters()` |  |

---

### `class abstract ExtendedSSLSession`

- **实现：** `javax.net.ssl.SSLSession`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ExtendedSSLSession()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract String[] getLocalSupportedSignatureAlgorithms()` |  |
| `abstract String[] getPeerSupportedSignatureAlgorithms()` |  |
| `java.util.List<javax.net.ssl.SNIServerName> getRequestedServerNames()` |  |

---

### `class HandshakeCompletedEvent`

- **继承：** `java.util.EventObject`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `HandshakeCompletedEvent(javax.net.ssl.SSLSocket, javax.net.ssl.SSLSession)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getCipherSuite()` |  |
| `java.security.cert.Certificate[] getLocalCertificates()` |  |
| `java.security.Principal getLocalPrincipal()` |  |
| `javax.security.cert.X509Certificate[] getPeerCertificateChain() throws javax.net.ssl.SSLPeerUnverifiedException` |  |
| `java.security.cert.Certificate[] getPeerCertificates() throws javax.net.ssl.SSLPeerUnverifiedException` |  |
| `java.security.Principal getPeerPrincipal() throws javax.net.ssl.SSLPeerUnverifiedException` |  |
| `javax.net.ssl.SSLSession getSession()` |  |
| `javax.net.ssl.SSLSocket getSocket()` |  |

---

### `interface HandshakeCompletedListener`

- **继承：** `java.util.EventListener`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void handshakeCompleted(javax.net.ssl.HandshakeCompletedEvent)` |  |

---

### `interface HostnameVerifier`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean verify(String, javax.net.ssl.SSLSession)` |  |

---

### `class abstract HttpsURLConnection`

- **继承：** `java.net.HttpURLConnection`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `HttpsURLConnection(java.net.URL)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `javax.net.ssl.HostnameVerifier hostnameVerifier` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract String getCipherSuite()` |  |
| `static javax.net.ssl.HostnameVerifier getDefaultHostnameVerifier()` |  |
| `static javax.net.ssl.SSLSocketFactory getDefaultSSLSocketFactory()` |  |
| `javax.net.ssl.HostnameVerifier getHostnameVerifier()` |  |
| `abstract java.security.cert.Certificate[] getLocalCertificates()` |  |
| `java.security.Principal getLocalPrincipal()` |  |
| `java.security.Principal getPeerPrincipal() throws javax.net.ssl.SSLPeerUnverifiedException` |  |
| `javax.net.ssl.SSLSocketFactory getSSLSocketFactory()` |  |
| `abstract java.security.cert.Certificate[] getServerCertificates() throws javax.net.ssl.SSLPeerUnverifiedException` |  |
| `static void setDefaultHostnameVerifier(javax.net.ssl.HostnameVerifier)` |  |
| `static void setDefaultSSLSocketFactory(javax.net.ssl.SSLSocketFactory)` |  |
| `void setHostnameVerifier(javax.net.ssl.HostnameVerifier)` |  |
| `void setSSLSocketFactory(javax.net.ssl.SSLSocketFactory)` |  |

---

### `interface KeyManager`


---

### `class KeyManagerFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `KeyManagerFactory(javax.net.ssl.KeyManagerFactorySpi, java.security.Provider, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final String getAlgorithm()` |  |
| `static final String getDefaultAlgorithm()` |  |
| `static final javax.net.ssl.KeyManagerFactory getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static final javax.net.ssl.KeyManagerFactory getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static final javax.net.ssl.KeyManagerFactory getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final javax.net.ssl.KeyManager[] getKeyManagers()` |  |
| `final java.security.Provider getProvider()` |  |
| `final void init(java.security.KeyStore, char[]) throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.UnrecoverableKeyException` |  |
| `final void init(javax.net.ssl.ManagerFactoryParameters) throws java.security.InvalidAlgorithmParameterException` |  |

---

### `class abstract KeyManagerFactorySpi`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `KeyManagerFactorySpi()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract javax.net.ssl.KeyManager[] engineGetKeyManagers()` |  |
| `abstract void engineInit(java.security.KeyStore, char[]) throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.UnrecoverableKeyException` |  |
| `abstract void engineInit(javax.net.ssl.ManagerFactoryParameters) throws java.security.InvalidAlgorithmParameterException` |  |

---

### `class KeyStoreBuilderParameters`

- **实现：** `javax.net.ssl.ManagerFactoryParameters`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `KeyStoreBuilderParameters(java.security.KeyStore.Builder)` |  |
| `KeyStoreBuilderParameters(java.util.List<java.security.KeyStore.Builder>)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.util.List<java.security.KeyStore.Builder> getParameters()` |  |

---

### `interface ManagerFactoryParameters`


---

### `class final SNIHostName`

- **继承：** `javax.net.ssl.SNIServerName`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SNIHostName(String)` |  |
| `SNIHostName(byte[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static javax.net.ssl.SNIMatcher createSNIMatcher(String)` |  |
| `String getAsciiName()` |  |

---

### `class abstract SNIMatcher`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SNIMatcher(int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final int getType()` |  |
| `abstract boolean matches(javax.net.ssl.SNIServerName)` |  |

---

### `class abstract SNIServerName`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SNIServerName(int, byte[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final byte[] getEncoded()` |  |
| `final int getType()` |  |

---

### `class SSLContext`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLContext(javax.net.ssl.SSLContextSpi, java.security.Provider, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final javax.net.ssl.SSLEngine createSSLEngine()` |  |
| `final javax.net.ssl.SSLEngine createSSLEngine(String, int)` |  |
| `final javax.net.ssl.SSLSessionContext getClientSessionContext()` |  |
| `static javax.net.ssl.SSLContext getDefault() throws java.security.NoSuchAlgorithmException` |  |
| `final javax.net.ssl.SSLParameters getDefaultSSLParameters()` |  |
| `static javax.net.ssl.SSLContext getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static javax.net.ssl.SSLContext getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static javax.net.ssl.SSLContext getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final String getProtocol()` |  |
| `final java.security.Provider getProvider()` |  |
| `final javax.net.ssl.SSLSessionContext getServerSessionContext()` |  |
| `final javax.net.ssl.SSLServerSocketFactory getServerSocketFactory()` |  |
| `final javax.net.ssl.SSLSocketFactory getSocketFactory()` |  |
| `final javax.net.ssl.SSLParameters getSupportedSSLParameters()` |  |
| `final void init(javax.net.ssl.KeyManager[], javax.net.ssl.TrustManager[], java.security.SecureRandom) throws java.security.KeyManagementException` |  |
| `static void setDefault(javax.net.ssl.SSLContext)` |  |

---

### `class abstract SSLContextSpi`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLContextSpi()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract javax.net.ssl.SSLEngine engineCreateSSLEngine()` |  |
| `abstract javax.net.ssl.SSLEngine engineCreateSSLEngine(String, int)` |  |
| `abstract javax.net.ssl.SSLSessionContext engineGetClientSessionContext()` |  |
| `javax.net.ssl.SSLParameters engineGetDefaultSSLParameters()` |  |
| `abstract javax.net.ssl.SSLSessionContext engineGetServerSessionContext()` |  |
| `abstract javax.net.ssl.SSLServerSocketFactory engineGetServerSocketFactory()` |  |
| `abstract javax.net.ssl.SSLSocketFactory engineGetSocketFactory()` |  |
| `javax.net.ssl.SSLParameters engineGetSupportedSSLParameters()` |  |
| `abstract void engineInit(javax.net.ssl.KeyManager[], javax.net.ssl.TrustManager[], java.security.SecureRandom) throws java.security.KeyManagementException` |  |

---

### `class abstract SSLEngine`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLEngine()` |  |
| `SSLEngine(String, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void beginHandshake() throws javax.net.ssl.SSLException` |  |
| `abstract void closeInbound() throws javax.net.ssl.SSLException` |  |
| `abstract void closeOutbound()` |  |
| `String getApplicationProtocol()` |  |
| `abstract Runnable getDelegatedTask()` |  |
| `abstract boolean getEnableSessionCreation()` |  |
| `abstract String[] getEnabledCipherSuites()` |  |
| `abstract String[] getEnabledProtocols()` |  |
| `String getHandshakeApplicationProtocol()` |  |
| `java.util.function.BiFunction<javax.net.ssl.SSLEngine,java.util.List<java.lang.String>,java.lang.String> getHandshakeApplicationProtocolSelector()` |  |
| `javax.net.ssl.SSLSession getHandshakeSession()` |  |
| `abstract javax.net.ssl.SSLEngineResult.HandshakeStatus getHandshakeStatus()` |  |
| `abstract boolean getNeedClientAuth()` |  |
| `String getPeerHost()` |  |
| `int getPeerPort()` |  |
| `javax.net.ssl.SSLParameters getSSLParameters()` |  |
| `abstract javax.net.ssl.SSLSession getSession()` |  |
| `abstract String[] getSupportedCipherSuites()` |  |
| `abstract String[] getSupportedProtocols()` |  |
| `abstract boolean getUseClientMode()` |  |
| `abstract boolean getWantClientAuth()` |  |
| `abstract boolean isInboundDone()` |  |
| `abstract boolean isOutboundDone()` |  |
| `abstract void setEnableSessionCreation(boolean)` |  |
| `abstract void setEnabledCipherSuites(String[])` |  |
| `abstract void setEnabledProtocols(String[])` |  |
| `void setHandshakeApplicationProtocolSelector(java.util.function.BiFunction<javax.net.ssl.SSLEngine,java.util.List<java.lang.String>,java.lang.String>)` |  |
| `abstract void setNeedClientAuth(boolean)` |  |
| `void setSSLParameters(javax.net.ssl.SSLParameters)` |  |
| `abstract void setUseClientMode(boolean)` |  |
| `abstract void setWantClientAuth(boolean)` |  |
| `javax.net.ssl.SSLEngineResult unwrap(java.nio.ByteBuffer, java.nio.ByteBuffer) throws javax.net.ssl.SSLException` |  |
| `javax.net.ssl.SSLEngineResult unwrap(java.nio.ByteBuffer, java.nio.ByteBuffer[]) throws javax.net.ssl.SSLException` |  |
| `abstract javax.net.ssl.SSLEngineResult unwrap(java.nio.ByteBuffer, java.nio.ByteBuffer[], int, int) throws javax.net.ssl.SSLException` |  |
| `javax.net.ssl.SSLEngineResult wrap(java.nio.ByteBuffer, java.nio.ByteBuffer) throws javax.net.ssl.SSLException` |  |
| `javax.net.ssl.SSLEngineResult wrap(java.nio.ByteBuffer[], java.nio.ByteBuffer) throws javax.net.ssl.SSLException` |  |
| `abstract javax.net.ssl.SSLEngineResult wrap(java.nio.ByteBuffer[], int, int, java.nio.ByteBuffer) throws javax.net.ssl.SSLException` |  |

---

### `class SSLEngineResult`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLEngineResult(javax.net.ssl.SSLEngineResult.Status, javax.net.ssl.SSLEngineResult.HandshakeStatus, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final int bytesConsumed()` |  |
| `final int bytesProduced()` |  |
| `final javax.net.ssl.SSLEngineResult.HandshakeStatus getHandshakeStatus()` |  |
| `final javax.net.ssl.SSLEngineResult.Status getStatus()` |  |

---

### `enum SSLEngineResult.HandshakeStatus`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final javax.net.ssl.SSLEngineResult.HandshakeStatus FINISHED` |  |
| `static final javax.net.ssl.SSLEngineResult.HandshakeStatus NEED_TASK` |  |
| `static final javax.net.ssl.SSLEngineResult.HandshakeStatus NEED_UNWRAP` |  |
| `static final javax.net.ssl.SSLEngineResult.HandshakeStatus NEED_WRAP` |  |
| `static final javax.net.ssl.SSLEngineResult.HandshakeStatus NOT_HANDSHAKING` |  |

---

### `enum SSLEngineResult.Status`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final javax.net.ssl.SSLEngineResult.Status BUFFER_OVERFLOW` |  |
| `static final javax.net.ssl.SSLEngineResult.Status BUFFER_UNDERFLOW` |  |
| `static final javax.net.ssl.SSLEngineResult.Status CLOSED` |  |
| `static final javax.net.ssl.SSLEngineResult.Status OK` |  |

---

### `class SSLException`

- **继承：** `java.io.IOException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLException(String)` |  |
| `SSLException(String, Throwable)` |  |
| `SSLException(Throwable)` |  |

---

### `class SSLHandshakeException`

- **继承：** `javax.net.ssl.SSLException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLHandshakeException(String)` |  |

---

### `class SSLKeyException`

- **继承：** `javax.net.ssl.SSLException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLKeyException(String)` |  |

---

### `class SSLParameters`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLParameters()` |  |
| `SSLParameters(String[])` |  |
| `SSLParameters(String[], String[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.security.AlgorithmConstraints getAlgorithmConstraints()` |  |
| `String[] getApplicationProtocols()` |  |
| `String[] getCipherSuites()` |  |
| `String getEndpointIdentificationAlgorithm()` |  |
| `boolean getNeedClientAuth()` |  |
| `String[] getProtocols()` |  |
| `final java.util.Collection<javax.net.ssl.SNIMatcher> getSNIMatchers()` |  |
| `final java.util.List<javax.net.ssl.SNIServerName> getServerNames()` |  |
| `final boolean getUseCipherSuitesOrder()` |  |
| `boolean getWantClientAuth()` |  |
| `void setAlgorithmConstraints(java.security.AlgorithmConstraints)` |  |
| `void setApplicationProtocols(String[])` |  |
| `void setCipherSuites(String[])` |  |
| `void setEndpointIdentificationAlgorithm(String)` |  |
| `void setNeedClientAuth(boolean)` |  |
| `void setProtocols(String[])` |  |
| `final void setSNIMatchers(java.util.Collection<javax.net.ssl.SNIMatcher>)` |  |
| `final void setServerNames(java.util.List<javax.net.ssl.SNIServerName>)` |  |
| `final void setUseCipherSuitesOrder(boolean)` |  |
| `void setWantClientAuth(boolean)` |  |

---

### `class SSLPeerUnverifiedException`

- **继承：** `javax.net.ssl.SSLException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLPeerUnverifiedException(String)` |  |

---

### `class final SSLPermission`

- **继承：** `java.security.BasicPermission`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLPermission(String)` |  |
| `SSLPermission(String, String)` |  |

---

### `class SSLProtocolException`

- **继承：** `javax.net.ssl.SSLException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLProtocolException(String)` |  |

---

### `class abstract SSLServerSocket`

- **继承：** `java.net.ServerSocket`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLServerSocket() throws java.io.IOException` |  |
| `SSLServerSocket(int) throws java.io.IOException` |  |
| `SSLServerSocket(int, int) throws java.io.IOException` |  |
| `SSLServerSocket(int, int, java.net.InetAddress) throws java.io.IOException` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract boolean getEnableSessionCreation()` |  |
| `abstract String[] getEnabledCipherSuites()` |  |
| `abstract String[] getEnabledProtocols()` |  |
| `abstract boolean getNeedClientAuth()` |  |
| `javax.net.ssl.SSLParameters getSSLParameters()` |  |
| `abstract String[] getSupportedCipherSuites()` |  |
| `abstract String[] getSupportedProtocols()` |  |
| `abstract boolean getUseClientMode()` |  |
| `abstract boolean getWantClientAuth()` |  |
| `abstract void setEnableSessionCreation(boolean)` |  |
| `abstract void setEnabledCipherSuites(String[])` |  |
| `abstract void setEnabledProtocols(String[])` |  |
| `abstract void setNeedClientAuth(boolean)` |  |
| `void setSSLParameters(javax.net.ssl.SSLParameters)` |  |
| `abstract void setUseClientMode(boolean)` |  |
| `abstract void setWantClientAuth(boolean)` |  |

---

### `class abstract SSLServerSocketFactory`

- **继承：** `javax.net.ServerSocketFactory`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLServerSocketFactory()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static javax.net.ServerSocketFactory getDefault()` |  |
| `abstract String[] getDefaultCipherSuites()` |  |
| `abstract String[] getSupportedCipherSuites()` |  |

---

### `interface SSLSession`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getApplicationBufferSize()` |  |
| `String getCipherSuite()` |  |
| `long getCreationTime()` |  |
| `byte[] getId()` |  |
| `long getLastAccessedTime()` |  |
| `java.security.cert.Certificate[] getLocalCertificates()` |  |
| `java.security.Principal getLocalPrincipal()` |  |
| `int getPacketBufferSize()` |  |
| `javax.security.cert.X509Certificate[] getPeerCertificateChain() throws javax.net.ssl.SSLPeerUnverifiedException` |  |
| `java.security.cert.Certificate[] getPeerCertificates() throws javax.net.ssl.SSLPeerUnverifiedException` |  |
| `String getPeerHost()` |  |
| `int getPeerPort()` |  |
| `java.security.Principal getPeerPrincipal() throws javax.net.ssl.SSLPeerUnverifiedException` |  |
| `String getProtocol()` |  |
| `javax.net.ssl.SSLSessionContext getSessionContext()` |  |
| `Object getValue(String)` |  |
| `String[] getValueNames()` |  |
| `void invalidate()` |  |
| `boolean isValid()` |  |
| `void putValue(String, Object)` |  |
| `void removeValue(String)` |  |

---

### `class SSLSessionBindingEvent`

- **继承：** `java.util.EventObject`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLSessionBindingEvent(javax.net.ssl.SSLSession, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getName()` |  |
| `javax.net.ssl.SSLSession getSession()` |  |

---

### `interface SSLSessionBindingListener`

- **继承：** `java.util.EventListener`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void valueBound(javax.net.ssl.SSLSessionBindingEvent)` |  |
| `void valueUnbound(javax.net.ssl.SSLSessionBindingEvent)` |  |

---

### `interface SSLSessionContext`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.util.Enumeration<byte[]> getIds()` |  |
| `javax.net.ssl.SSLSession getSession(byte[])` |  |
| `int getSessionCacheSize()` |  |
| `int getSessionTimeout()` |  |
| `void setSessionCacheSize(int) throws java.lang.IllegalArgumentException` |  |
| `void setSessionTimeout(int) throws java.lang.IllegalArgumentException` |  |

---

### `class abstract SSLSocket`

- **继承：** `java.net.Socket`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLSocket()` |  |
| `SSLSocket(String, int) throws java.io.IOException, java.net.UnknownHostException` |  |
| `SSLSocket(java.net.InetAddress, int) throws java.io.IOException` |  |
| `SSLSocket(String, int, java.net.InetAddress, int) throws java.io.IOException, java.net.UnknownHostException` |  |
| `SSLSocket(java.net.InetAddress, int, java.net.InetAddress, int) throws java.io.IOException` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void addHandshakeCompletedListener(javax.net.ssl.HandshakeCompletedListener)` |  |
| `String getApplicationProtocol()` |  |
| `abstract boolean getEnableSessionCreation()` |  |
| `abstract String[] getEnabledCipherSuites()` |  |
| `abstract String[] getEnabledProtocols()` |  |
| `String getHandshakeApplicationProtocol()` |  |
| `java.util.function.BiFunction<javax.net.ssl.SSLSocket,java.util.List<java.lang.String>,java.lang.String> getHandshakeApplicationProtocolSelector()` |  |
| `javax.net.ssl.SSLSession getHandshakeSession()` |  |
| `abstract boolean getNeedClientAuth()` |  |
| `javax.net.ssl.SSLParameters getSSLParameters()` |  |
| `abstract javax.net.ssl.SSLSession getSession()` |  |
| `abstract String[] getSupportedCipherSuites()` |  |
| `abstract String[] getSupportedProtocols()` |  |
| `abstract boolean getUseClientMode()` |  |
| `abstract boolean getWantClientAuth()` |  |
| `abstract void removeHandshakeCompletedListener(javax.net.ssl.HandshakeCompletedListener)` |  |
| `abstract void setEnableSessionCreation(boolean)` |  |
| `abstract void setEnabledCipherSuites(String[])` |  |
| `abstract void setEnabledProtocols(String[])` |  |
| `void setHandshakeApplicationProtocolSelector(java.util.function.BiFunction<javax.net.ssl.SSLSocket,java.util.List<java.lang.String>,java.lang.String>)` |  |
| `abstract void setNeedClientAuth(boolean)` |  |
| `void setSSLParameters(javax.net.ssl.SSLParameters)` |  |
| `abstract void setUseClientMode(boolean)` |  |
| `abstract void setWantClientAuth(boolean)` |  |
| `abstract void startHandshake() throws java.io.IOException` |  |

---

### `class abstract SSLSocketFactory`

- **继承：** `javax.net.SocketFactory`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLSocketFactory()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract java.net.Socket createSocket(java.net.Socket, String, int, boolean) throws java.io.IOException` |  |
| `static javax.net.SocketFactory getDefault()` |  |
| `abstract String[] getDefaultCipherSuites()` |  |
| `abstract String[] getSupportedCipherSuites()` |  |

---

### `class final StandardConstants`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SNI_HOST_NAME = 0` |  |

---

### `interface TrustManager`


---

### `class TrustManagerFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TrustManagerFactory(javax.net.ssl.TrustManagerFactorySpi, java.security.Provider, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final String getAlgorithm()` |  |
| `static final String getDefaultAlgorithm()` |  |
| `static final javax.net.ssl.TrustManagerFactory getInstance(String) throws java.security.NoSuchAlgorithmException` |  |
| `static final javax.net.ssl.TrustManagerFactory getInstance(String, String) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException` |  |
| `static final javax.net.ssl.TrustManagerFactory getInstance(String, java.security.Provider) throws java.security.NoSuchAlgorithmException` |  |
| `final java.security.Provider getProvider()` |  |
| `final javax.net.ssl.TrustManager[] getTrustManagers()` |  |
| `final void init(java.security.KeyStore) throws java.security.KeyStoreException` |  |
| `final void init(javax.net.ssl.ManagerFactoryParameters) throws java.security.InvalidAlgorithmParameterException` |  |

---

### `class abstract TrustManagerFactorySpi`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TrustManagerFactorySpi()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract javax.net.ssl.TrustManager[] engineGetTrustManagers()` |  |
| `abstract void engineInit(java.security.KeyStore) throws java.security.KeyStoreException` |  |
| `abstract void engineInit(javax.net.ssl.ManagerFactoryParameters) throws java.security.InvalidAlgorithmParameterException` |  |

---

### `class abstract X509ExtendedKeyManager`

- **实现：** `javax.net.ssl.X509KeyManager`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `X509ExtendedKeyManager()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String chooseEngineClientAlias(String[], java.security.Principal[], javax.net.ssl.SSLEngine)` |  |
| `String chooseEngineServerAlias(String, java.security.Principal[], javax.net.ssl.SSLEngine)` |  |

---

### `class abstract X509ExtendedTrustManager`

- **实现：** `javax.net.ssl.X509TrustManager`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `X509ExtendedTrustManager()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void checkClientTrusted(java.security.cert.X509Certificate[], String, java.net.Socket) throws java.security.cert.CertificateException` |  |
| `abstract void checkClientTrusted(java.security.cert.X509Certificate[], String, javax.net.ssl.SSLEngine) throws java.security.cert.CertificateException` |  |
| `abstract void checkServerTrusted(java.security.cert.X509Certificate[], String, java.net.Socket) throws java.security.cert.CertificateException` |  |
| `abstract void checkServerTrusted(java.security.cert.X509Certificate[], String, javax.net.ssl.SSLEngine) throws java.security.cert.CertificateException` |  |

---

### `interface X509KeyManager`

- **继承：** `javax.net.ssl.KeyManager`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String chooseClientAlias(String[], java.security.Principal[], java.net.Socket)` |  |
| `String chooseServerAlias(String, java.security.Principal[], java.net.Socket)` |  |
| `java.security.cert.X509Certificate[] getCertificateChain(String)` |  |
| `String[] getClientAliases(String, java.security.Principal[])` |  |
| `java.security.PrivateKey getPrivateKey(String)` |  |
| `String[] getServerAliases(String, java.security.Principal[])` |  |

---

### `interface X509TrustManager`

- **继承：** `javax.net.ssl.TrustManager`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void checkClientTrusted(java.security.cert.X509Certificate[], String) throws java.security.cert.CertificateException` |  |
| `void checkServerTrusted(java.security.cert.X509Certificate[], String) throws java.security.cert.CertificateException` |  |
| `java.security.cert.X509Certificate[] getAcceptedIssuers()` |  |

---

## Package: `javax.security.auth`

### `class final AuthPermission`

- **继承：** `java.security.BasicPermission`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AuthPermission(String)` |  |
| `AuthPermission(String, String)` |  |

---

### `class DestroyFailedException`

- **继承：** `java.lang.Exception`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DestroyFailedException()` |  |
| `DestroyFailedException(String)` |  |

---

### `interface Destroyable`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `default void destroy() throws javax.security.auth.DestroyFailedException` |  |
| `default boolean isDestroyed()` |  |

---

### `class final PrivateCredentialPermission`

- **继承：** `java.security.Permission`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PrivateCredentialPermission(String, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getActions()` |  |
| `String getCredentialClass()` |  |
| `String[][] getPrincipals()` |  |
| `boolean implies(java.security.Permission)` |  |

---

### `class final Subject`

- **实现：** `java.io.Serializable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Subject()` |  |
| `Subject(boolean, java.util.Set<? extends java.security.Principal>, java.util.Set<?>, java.util.Set<?>)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static <T> T doAs(javax.security.auth.Subject, java.security.PrivilegedAction<T>)` |  |
| `static <T> T doAs(javax.security.auth.Subject, java.security.PrivilegedExceptionAction<T>) throws java.security.PrivilegedActionException` |  |
| `static <T> T doAsPrivileged(javax.security.auth.Subject, java.security.PrivilegedAction<T>, java.security.AccessControlContext)` |  |
| `static <T> T doAsPrivileged(javax.security.auth.Subject, java.security.PrivilegedExceptionAction<T>, java.security.AccessControlContext) throws java.security.PrivilegedActionException` |  |
| `java.util.Set<java.security.Principal> getPrincipals()` |  |
| `<T extends java.security.Principal> java.util.Set<T> getPrincipals(Class<T>)` |  |
| `java.util.Set<java.lang.Object> getPrivateCredentials()` |  |
| `<T> java.util.Set<T> getPrivateCredentials(Class<T>)` |  |
| `java.util.Set<java.lang.Object> getPublicCredentials()` |  |
| `<T> java.util.Set<T> getPublicCredentials(Class<T>)` |  |
| `static javax.security.auth.Subject getSubject(java.security.AccessControlContext)` |  |
| `boolean isReadOnly()` |  |
| `void setReadOnly()` |  |

---

### `class SubjectDomainCombiner`

- **实现：** `java.security.DomainCombiner`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SubjectDomainCombiner(javax.security.auth.Subject)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.security.ProtectionDomain[] combine(java.security.ProtectionDomain[], java.security.ProtectionDomain[])` |  |
| `javax.security.auth.Subject getSubject()` |  |

---

## Package: `javax.security.auth.callback`

### `interface Callback`


---

### `interface CallbackHandler`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void handle(javax.security.auth.callback.Callback[]) throws java.io.IOException, javax.security.auth.callback.UnsupportedCallbackException` |  |

---

### `class PasswordCallback`

- **实现：** `javax.security.auth.callback.Callback java.io.Serializable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PasswordCallback(String, boolean)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void clearPassword()` |  |
| `char[] getPassword()` |  |
| `String getPrompt()` |  |
| `boolean isEchoOn()` |  |
| `void setPassword(char[])` |  |

---

### `class UnsupportedCallbackException`

- **继承：** `java.lang.Exception`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `UnsupportedCallbackException(javax.security.auth.callback.Callback)` |  |
| `UnsupportedCallbackException(javax.security.auth.callback.Callback, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `javax.security.auth.callback.Callback getCallback()` |  |

---

## Package: `javax.security.auth.login`

### `class LoginException`

- **继承：** `java.security.GeneralSecurityException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LoginException()` |  |
| `LoginException(String)` |  |

---

## Package: `javax.security.auth.x500`

### `class final X500Principal`

- **实现：** `java.security.Principal java.io.Serializable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `X500Principal(String)` |  |
| `X500Principal(String, java.util.Map<java.lang.String,java.lang.String>)` |  |
| `X500Principal(byte[])` |  |
| `X500Principal(java.io.InputStream)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CANONICAL = "CANONICAL"` |  |
| `static final String RFC1779 = "RFC1779"` |  |
| `static final String RFC2253 = "RFC2253"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `byte[] getEncoded()` |  |
| `String getName()` |  |
| `String getName(String)` |  |
| `String getName(String, java.util.Map<java.lang.String,java.lang.String>)` |  |

---

## Package: `javax.security.cert`

### `class abstract Certificate`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Certificate()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract byte[] getEncoded() throws javax.security.cert.CertificateEncodingException` |  |
| `abstract java.security.PublicKey getPublicKey()` |  |
| `abstract String toString()` |  |
| `abstract void verify(java.security.PublicKey) throws javax.security.cert.CertificateException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException, java.security.SignatureException` |  |
| `abstract void verify(java.security.PublicKey, String) throws javax.security.cert.CertificateException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException, java.security.SignatureException` |  |

---

### `class CertificateEncodingException`

- **继承：** `javax.security.cert.CertificateException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CertificateEncodingException()` |  |
| `CertificateEncodingException(String)` |  |

---

### `class CertificateException`

- **继承：** `java.lang.Exception`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CertificateException()` |  |
| `CertificateException(String)` |  |

---

### `class CertificateExpiredException`

- **继承：** `javax.security.cert.CertificateException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CertificateExpiredException()` |  |
| `CertificateExpiredException(String)` |  |

---

### `class CertificateNotYetValidException`

- **继承：** `javax.security.cert.CertificateException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CertificateNotYetValidException()` |  |
| `CertificateNotYetValidException(String)` |  |

---

### `class CertificateParsingException`

- **继承：** `javax.security.cert.CertificateException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CertificateParsingException()` |  |
| `CertificateParsingException(String)` |  |

---

### `class abstract X509Certificate`

- **继承：** `javax.security.cert.Certificate`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `X509Certificate()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void checkValidity() throws javax.security.cert.CertificateExpiredException, javax.security.cert.CertificateNotYetValidException` |  |
| `abstract void checkValidity(java.util.Date) throws javax.security.cert.CertificateExpiredException, javax.security.cert.CertificateNotYetValidException` |  |
| `static final javax.security.cert.X509Certificate getInstance(java.io.InputStream) throws javax.security.cert.CertificateException` |  |
| `static final javax.security.cert.X509Certificate getInstance(byte[]) throws javax.security.cert.CertificateException` |  |
| `abstract java.security.Principal getIssuerDN()` |  |
| `abstract java.util.Date getNotAfter()` |  |
| `abstract java.util.Date getNotBefore()` |  |
| `abstract java.math.BigInteger getSerialNumber()` |  |
| `abstract String getSigAlgName()` |  |
| `abstract String getSigAlgOID()` |  |
| `abstract byte[] getSigAlgParams()` |  |
| `abstract java.security.Principal getSubjectDN()` |  |
| `abstract int getVersion()` |  |

---

## Package: `javax.sql`

### `interface CommonDataSource`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.io.PrintWriter getLogWriter() throws java.sql.SQLException` |  |
| `int getLoginTimeout() throws java.sql.SQLException` |  |
| `java.util.logging.Logger getParentLogger() throws java.sql.SQLFeatureNotSupportedException` |  |
| `void setLogWriter(java.io.PrintWriter) throws java.sql.SQLException` |  |
| `void setLoginTimeout(int) throws java.sql.SQLException` |  |

---

### `class ConnectionEvent`

- **继承：** `java.util.EventObject`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ConnectionEvent(javax.sql.PooledConnection)` |  |
| `ConnectionEvent(javax.sql.PooledConnection, java.sql.SQLException)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.sql.SQLException getSQLException()` |  |

---

### `interface ConnectionEventListener`

- **继承：** `java.util.EventListener`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void connectionClosed(javax.sql.ConnectionEvent)` |  |
| `void connectionErrorOccurred(javax.sql.ConnectionEvent)` |  |

---

### `interface ConnectionPoolDataSource`

- **继承：** `javax.sql.CommonDataSource`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `javax.sql.PooledConnection getPooledConnection() throws java.sql.SQLException` |  |
| `javax.sql.PooledConnection getPooledConnection(String, String) throws java.sql.SQLException` |  |

---

### `interface DataSource`

- **继承：** `javax.sql.CommonDataSource java.sql.Wrapper`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.sql.Connection getConnection() throws java.sql.SQLException` |  |
| `java.sql.Connection getConnection(String, String) throws java.sql.SQLException` |  |

---

### `interface PooledConnection`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addConnectionEventListener(javax.sql.ConnectionEventListener)` |  |
| `void addStatementEventListener(javax.sql.StatementEventListener)` |  |
| `void close() throws java.sql.SQLException` |  |
| `java.sql.Connection getConnection() throws java.sql.SQLException` |  |
| `void removeConnectionEventListener(javax.sql.ConnectionEventListener)` |  |
| `void removeStatementEventListener(javax.sql.StatementEventListener)` |  |

---

### `interface RowSet`

- **继承：** `java.sql.ResultSet`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addRowSetListener(javax.sql.RowSetListener)` |  |
| `void clearParameters() throws java.sql.SQLException` |  |
| `void execute() throws java.sql.SQLException` |  |
| `String getCommand()` |  |
| `String getDataSourceName()` |  |
| `boolean getEscapeProcessing() throws java.sql.SQLException` |  |
| `int getMaxFieldSize() throws java.sql.SQLException` |  |
| `int getMaxRows() throws java.sql.SQLException` |  |
| `String getPassword()` |  |
| `int getQueryTimeout() throws java.sql.SQLException` |  |
| `int getTransactionIsolation()` |  |
| `java.util.Map<java.lang.String,java.lang.Class<?>> getTypeMap() throws java.sql.SQLException` |  |
| `String getUrl() throws java.sql.SQLException` |  |
| `String getUsername()` |  |
| `boolean isReadOnly()` |  |
| `void removeRowSetListener(javax.sql.RowSetListener)` |  |
| `void setArray(int, java.sql.Array) throws java.sql.SQLException` |  |
| `void setAsciiStream(int, java.io.InputStream, int) throws java.sql.SQLException` |  |
| `void setAsciiStream(String, java.io.InputStream, int) throws java.sql.SQLException` |  |
| `void setAsciiStream(int, java.io.InputStream) throws java.sql.SQLException` |  |
| `void setAsciiStream(String, java.io.InputStream) throws java.sql.SQLException` |  |
| `void setBigDecimal(int, java.math.BigDecimal) throws java.sql.SQLException` |  |
| `void setBigDecimal(String, java.math.BigDecimal) throws java.sql.SQLException` |  |
| `void setBinaryStream(int, java.io.InputStream, int) throws java.sql.SQLException` |  |
| `void setBinaryStream(String, java.io.InputStream, int) throws java.sql.SQLException` |  |
| `void setBinaryStream(int, java.io.InputStream) throws java.sql.SQLException` |  |
| `void setBinaryStream(String, java.io.InputStream) throws java.sql.SQLException` |  |
| `void setBlob(int, java.sql.Blob) throws java.sql.SQLException` |  |
| `void setBlob(int, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void setBlob(int, java.io.InputStream) throws java.sql.SQLException` |  |
| `void setBlob(String, java.io.InputStream, long) throws java.sql.SQLException` |  |
| `void setBlob(String, java.sql.Blob) throws java.sql.SQLException` |  |
| `void setBlob(String, java.io.InputStream) throws java.sql.SQLException` |  |
| `void setBoolean(int, boolean) throws java.sql.SQLException` |  |
| `void setBoolean(String, boolean) throws java.sql.SQLException` |  |
| `void setByte(int, byte) throws java.sql.SQLException` |  |
| `void setByte(String, byte) throws java.sql.SQLException` |  |
| `void setBytes(int, byte[]) throws java.sql.SQLException` |  |
| `void setBytes(String, byte[]) throws java.sql.SQLException` |  |
| `void setCharacterStream(int, java.io.Reader, int) throws java.sql.SQLException` |  |
| `void setCharacterStream(String, java.io.Reader, int) throws java.sql.SQLException` |  |
| `void setCharacterStream(int, java.io.Reader) throws java.sql.SQLException` |  |
| `void setCharacterStream(String, java.io.Reader) throws java.sql.SQLException` |  |
| `void setClob(int, java.sql.Clob) throws java.sql.SQLException` |  |
| `void setClob(int, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setClob(int, java.io.Reader) throws java.sql.SQLException` |  |
| `void setClob(String, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setClob(String, java.sql.Clob) throws java.sql.SQLException` |  |
| `void setClob(String, java.io.Reader) throws java.sql.SQLException` |  |
| `void setCommand(String) throws java.sql.SQLException` |  |
| `void setConcurrency(int) throws java.sql.SQLException` |  |
| `void setDataSourceName(String) throws java.sql.SQLException` |  |
| `void setDate(int, java.sql.Date) throws java.sql.SQLException` |  |
| `void setDate(int, java.sql.Date, java.util.Calendar) throws java.sql.SQLException` |  |
| `void setDate(String, java.sql.Date) throws java.sql.SQLException` |  |
| `void setDate(String, java.sql.Date, java.util.Calendar) throws java.sql.SQLException` |  |
| `void setDouble(int, double) throws java.sql.SQLException` |  |
| `void setDouble(String, double) throws java.sql.SQLException` |  |
| `void setEscapeProcessing(boolean) throws java.sql.SQLException` |  |
| `void setFloat(int, float) throws java.sql.SQLException` |  |
| `void setFloat(String, float) throws java.sql.SQLException` |  |
| `void setInt(int, int) throws java.sql.SQLException` |  |
| `void setInt(String, int) throws java.sql.SQLException` |  |
| `void setLong(int, long) throws java.sql.SQLException` |  |
| `void setLong(String, long) throws java.sql.SQLException` |  |
| `void setMaxFieldSize(int) throws java.sql.SQLException` |  |
| `void setMaxRows(int) throws java.sql.SQLException` |  |
| `void setNCharacterStream(int, java.io.Reader) throws java.sql.SQLException` |  |
| `void setNCharacterStream(int, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setNCharacterStream(String, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setNCharacterStream(String, java.io.Reader) throws java.sql.SQLException` |  |
| `void setNClob(String, java.sql.NClob) throws java.sql.SQLException` |  |
| `void setNClob(String, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setNClob(String, java.io.Reader) throws java.sql.SQLException` |  |
| `void setNClob(int, java.io.Reader, long) throws java.sql.SQLException` |  |
| `void setNClob(int, java.sql.NClob) throws java.sql.SQLException` |  |
| `void setNClob(int, java.io.Reader) throws java.sql.SQLException` |  |
| `void setNString(int, String) throws java.sql.SQLException` |  |
| `void setNString(String, String) throws java.sql.SQLException` |  |
| `void setNull(int, int) throws java.sql.SQLException` |  |
| `void setNull(String, int) throws java.sql.SQLException` |  |
| `void setNull(int, int, String) throws java.sql.SQLException` |  |
| `void setNull(String, int, String) throws java.sql.SQLException` |  |
| `void setObject(int, Object, int, int) throws java.sql.SQLException` |  |
| `void setObject(String, Object, int, int) throws java.sql.SQLException` |  |
| `void setObject(int, Object, int) throws java.sql.SQLException` |  |
| `void setObject(String, Object, int) throws java.sql.SQLException` |  |
| `void setObject(String, Object) throws java.sql.SQLException` |  |
| `void setObject(int, Object) throws java.sql.SQLException` |  |
| `void setPassword(String) throws java.sql.SQLException` |  |
| `void setQueryTimeout(int) throws java.sql.SQLException` |  |
| `void setReadOnly(boolean) throws java.sql.SQLException` |  |
| `void setRef(int, java.sql.Ref) throws java.sql.SQLException` |  |
| `void setRowId(int, java.sql.RowId) throws java.sql.SQLException` |  |
| `void setRowId(String, java.sql.RowId) throws java.sql.SQLException` |  |
| `void setSQLXML(int, java.sql.SQLXML) throws java.sql.SQLException` |  |
| `void setSQLXML(String, java.sql.SQLXML) throws java.sql.SQLException` |  |
| `void setShort(int, short) throws java.sql.SQLException` |  |
| `void setShort(String, short) throws java.sql.SQLException` |  |
| `void setString(int, String) throws java.sql.SQLException` |  |
| `void setString(String, String) throws java.sql.SQLException` |  |
| `void setTime(int, java.sql.Time) throws java.sql.SQLException` |  |
| `void setTime(int, java.sql.Time, java.util.Calendar) throws java.sql.SQLException` |  |
| `void setTime(String, java.sql.Time) throws java.sql.SQLException` |  |
| `void setTime(String, java.sql.Time, java.util.Calendar) throws java.sql.SQLException` |  |
| `void setTimestamp(int, java.sql.Timestamp) throws java.sql.SQLException` |  |
| `void setTimestamp(String, java.sql.Timestamp) throws java.sql.SQLException` |  |
| `void setTimestamp(int, java.sql.Timestamp, java.util.Calendar) throws java.sql.SQLException` |  |
| `void setTimestamp(String, java.sql.Timestamp, java.util.Calendar) throws java.sql.SQLException` |  |
| `void setTransactionIsolation(int) throws java.sql.SQLException` |  |
| `void setType(int) throws java.sql.SQLException` |  |
| `void setTypeMap(java.util.Map<java.lang.String,java.lang.Class<?>>) throws java.sql.SQLException` |  |
| `void setURL(int, java.net.URL) throws java.sql.SQLException` |  |
| `void setUrl(String) throws java.sql.SQLException` |  |
| `void setUsername(String) throws java.sql.SQLException` |  |

---

### `class RowSetEvent`

- **继承：** `java.util.EventObject`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RowSetEvent(javax.sql.RowSet)` |  |

---

### `interface RowSetInternal`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.sql.Connection getConnection() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getOriginal() throws java.sql.SQLException` |  |
| `java.sql.ResultSet getOriginalRow() throws java.sql.SQLException` |  |
| `Object[] getParams() throws java.sql.SQLException` |  |
| `void setMetaData(javax.sql.RowSetMetaData) throws java.sql.SQLException` |  |

---

### `interface RowSetListener`

- **继承：** `java.util.EventListener`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void cursorMoved(javax.sql.RowSetEvent)` |  |
| `void rowChanged(javax.sql.RowSetEvent)` |  |
| `void rowSetChanged(javax.sql.RowSetEvent)` |  |

---

### `interface RowSetMetaData`

- **继承：** `java.sql.ResultSetMetaData`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void setAutoIncrement(int, boolean) throws java.sql.SQLException` |  |
| `void setCaseSensitive(int, boolean) throws java.sql.SQLException` |  |
| `void setCatalogName(int, String) throws java.sql.SQLException` |  |
| `void setColumnCount(int) throws java.sql.SQLException` |  |
| `void setColumnDisplaySize(int, int) throws java.sql.SQLException` |  |
| `void setColumnLabel(int, String) throws java.sql.SQLException` |  |
| `void setColumnName(int, String) throws java.sql.SQLException` |  |
| `void setColumnType(int, int) throws java.sql.SQLException` |  |
| `void setColumnTypeName(int, String) throws java.sql.SQLException` |  |
| `void setCurrency(int, boolean) throws java.sql.SQLException` |  |
| `void setNullable(int, int) throws java.sql.SQLException` |  |
| `void setPrecision(int, int) throws java.sql.SQLException` |  |
| `void setScale(int, int) throws java.sql.SQLException` |  |
| `void setSchemaName(int, String) throws java.sql.SQLException` |  |
| `void setSearchable(int, boolean) throws java.sql.SQLException` |  |
| `void setSigned(int, boolean) throws java.sql.SQLException` |  |
| `void setTableName(int, String) throws java.sql.SQLException` |  |

---

### `interface RowSetReader`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void readData(javax.sql.RowSetInternal) throws java.sql.SQLException` |  |

---

### `interface RowSetWriter`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean writeData(javax.sql.RowSetInternal) throws java.sql.SQLException` |  |

---

### `class StatementEvent`

- **继承：** `java.util.EventObject`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `StatementEvent(javax.sql.PooledConnection, java.sql.PreparedStatement)` |  |
| `StatementEvent(javax.sql.PooledConnection, java.sql.PreparedStatement, java.sql.SQLException)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.sql.SQLException getSQLException()` |  |
| `java.sql.PreparedStatement getStatement()` |  |

---

### `interface StatementEventListener`

- **继承：** `java.util.EventListener`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void statementClosed(javax.sql.StatementEvent)` |  |
| `void statementErrorOccurred(javax.sql.StatementEvent)` |  |

---

## Package: `javax.xml`

### `class final XMLConstants`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DEFAULT_NS_PREFIX = ""` |  |
| `static final String FEATURE_SECURE_PROCESSING = "http://javax.xml.XMLConstants/feature/secure-processing"` |  |
| `static final String NULL_NS_URI = ""` |  |
| `static final String RELAXNG_NS_URI = "http://relaxng.org/ns/structure/1.0"` |  |
| `static final String W3C_XML_SCHEMA_INSTANCE_NS_URI = "http://www.w3.org/2001/XMLSchema-instance"` |  |
| `static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema"` |  |
| `static final String W3C_XPATH_DATATYPE_NS_URI = "http://www.w3.org/2003/11/xpath-datatypes"` |  |
| `static final String XMLNS_ATTRIBUTE = "xmlns"` |  |
| `static final String XMLNS_ATTRIBUTE_NS_URI = "http://www.w3.org/2000/xmlns/"` |  |
| `static final String XML_DTD_NS_URI = "http://www.w3.org/TR/REC-xml"` |  |
| `static final String XML_NS_PREFIX = "xml"` |  |
| `static final String XML_NS_URI = "http://www.w3.org/XML/1998/namespace"` |  |

---

## Package: `javax.xml.datatype`

### `class DatatypeConfigurationException`

- **继承：** `java.lang.Exception`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DatatypeConfigurationException()` |  |
| `DatatypeConfigurationException(String)` |  |
| `DatatypeConfigurationException(String, Throwable)` |  |
| `DatatypeConfigurationException(Throwable)` |  |

---

### `class final DatatypeConstants`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int APRIL = 4` |  |
| `static final int AUGUST = 8` |  |
| `static final javax.xml.namespace.QName DATE` |  |
| `static final javax.xml.namespace.QName DATETIME` |  |
| `static final javax.xml.datatype.DatatypeConstants.Field DAYS` |  |
| `static final int DECEMBER = 12` |  |
| `static final javax.xml.namespace.QName DURATION` |  |
| `static final javax.xml.namespace.QName DURATION_DAYTIME` |  |
| `static final javax.xml.namespace.QName DURATION_YEARMONTH` |  |
| `static final int EQUAL = 0` |  |
| `static final int FEBRUARY = 2` |  |
| `static final int FIELD_UNDEFINED = -2147483648` |  |
| `static final javax.xml.namespace.QName GDAY` |  |
| `static final javax.xml.namespace.QName GMONTH` |  |
| `static final javax.xml.namespace.QName GMONTHDAY` |  |
| `static final int GREATER = 1` |  |
| `static final javax.xml.namespace.QName GYEAR` |  |
| `static final javax.xml.namespace.QName GYEARMONTH` |  |
| `static final javax.xml.datatype.DatatypeConstants.Field HOURS` |  |
| `static final int INDETERMINATE = 2` |  |
| `static final int JANUARY = 1` |  |
| `static final int JULY = 7` |  |
| `static final int JUNE = 6` |  |
| `static final int LESSER = -1` |  |
| `static final int MARCH = 3` |  |
| `static final int MAX_TIMEZONE_OFFSET = -840` |  |
| `static final int MAY = 5` |  |
| `static final javax.xml.datatype.DatatypeConstants.Field MINUTES` |  |
| `static final int MIN_TIMEZONE_OFFSET = 840` |  |
| `static final javax.xml.datatype.DatatypeConstants.Field MONTHS` |  |
| `static final int NOVEMBER = 11` |  |
| `static final int OCTOBER = 10` |  |
| `static final javax.xml.datatype.DatatypeConstants.Field SECONDS` |  |
| `static final int SEPTEMBER = 9` |  |
| `static final javax.xml.namespace.QName TIME` |  |
| `static final javax.xml.datatype.DatatypeConstants.Field YEARS` |  |

---

### `class static final DatatypeConstants.Field`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getId()` |  |

---

### `class abstract DatatypeFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DatatypeFactory()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DATATYPEFACTORY_IMPLEMENTATION_CLASS` |  |
| `static final String DATATYPEFACTORY_PROPERTY = "javax.xml.datatype.DatatypeFactory"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract javax.xml.datatype.Duration newDuration(String)` |  |
| `abstract javax.xml.datatype.Duration newDuration(long)` |  |
| `abstract javax.xml.datatype.Duration newDuration(boolean, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigDecimal)` |  |
| `javax.xml.datatype.Duration newDuration(boolean, int, int, int, int, int, int)` |  |
| `javax.xml.datatype.Duration newDurationDayTime(String)` |  |
| `javax.xml.datatype.Duration newDurationDayTime(long)` |  |
| `javax.xml.datatype.Duration newDurationDayTime(boolean, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger, java.math.BigInteger)` |  |
| `javax.xml.datatype.Duration newDurationDayTime(boolean, int, int, int, int)` |  |
| `javax.xml.datatype.Duration newDurationYearMonth(String)` |  |
| `javax.xml.datatype.Duration newDurationYearMonth(long)` |  |
| `javax.xml.datatype.Duration newDurationYearMonth(boolean, java.math.BigInteger, java.math.BigInteger)` |  |
| `javax.xml.datatype.Duration newDurationYearMonth(boolean, int, int)` |  |
| `static javax.xml.datatype.DatatypeFactory newInstance() throws javax.xml.datatype.DatatypeConfigurationException` |  |
| `static javax.xml.datatype.DatatypeFactory newInstance(String, ClassLoader) throws javax.xml.datatype.DatatypeConfigurationException` |  |
| `abstract javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendar()` |  |
| `abstract javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendar(String)` |  |
| `abstract javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendar(java.util.GregorianCalendar)` |  |
| `abstract javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendar(java.math.BigInteger, int, int, int, int, int, java.math.BigDecimal, int)` |  |
| `javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendar(int, int, int, int, int, int, int, int)` |  |
| `javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendarDate(int, int, int, int)` |  |
| `javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendarTime(int, int, int, int)` |  |
| `javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendarTime(int, int, int, java.math.BigDecimal, int)` |  |
| `javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendarTime(int, int, int, int, int)` |  |

---

### `class abstract Duration`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Duration()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract javax.xml.datatype.Duration add(javax.xml.datatype.Duration)` |  |
| `abstract void addTo(java.util.Calendar)` |  |
| `void addTo(java.util.Date)` |  |
| `abstract int compare(javax.xml.datatype.Duration)` |  |
| `int getDays()` |  |
| `abstract Number getField(javax.xml.datatype.DatatypeConstants.Field)` |  |
| `int getHours()` |  |
| `int getMinutes()` |  |
| `int getMonths()` |  |
| `int getSeconds()` |  |
| `abstract int getSign()` |  |
| `long getTimeInMillis(java.util.Calendar)` |  |
| `long getTimeInMillis(java.util.Date)` |  |
| `javax.xml.namespace.QName getXMLSchemaType()` |  |
| `int getYears()` |  |
| `abstract int hashCode()` |  |
| `boolean isLongerThan(javax.xml.datatype.Duration)` |  |
| `abstract boolean isSet(javax.xml.datatype.DatatypeConstants.Field)` |  |
| `boolean isShorterThan(javax.xml.datatype.Duration)` |  |
| `javax.xml.datatype.Duration multiply(int)` |  |
| `abstract javax.xml.datatype.Duration multiply(java.math.BigDecimal)` |  |
| `abstract javax.xml.datatype.Duration negate()` |  |
| `abstract javax.xml.datatype.Duration normalizeWith(java.util.Calendar)` |  |
| `javax.xml.datatype.Duration subtract(javax.xml.datatype.Duration)` |  |

---

### `class abstract XMLGregorianCalendar`

- **实现：** `java.lang.Cloneable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `XMLGregorianCalendar()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void add(javax.xml.datatype.Duration)` |  |
| `abstract void clear()` |  |
| `abstract Object clone()` |  |
| `abstract int compare(javax.xml.datatype.XMLGregorianCalendar)` |  |
| `abstract int getDay()` |  |
| `abstract java.math.BigInteger getEon()` |  |
| `abstract java.math.BigInteger getEonAndYear()` |  |
| `abstract java.math.BigDecimal getFractionalSecond()` |  |
| `abstract int getHour()` |  |
| `int getMillisecond()` |  |
| `abstract int getMinute()` |  |
| `abstract int getMonth()` |  |
| `abstract int getSecond()` |  |
| `abstract java.util.TimeZone getTimeZone(int)` |  |
| `abstract int getTimezone()` |  |
| `abstract javax.xml.namespace.QName getXMLSchemaType()` |  |
| `abstract int getYear()` |  |
| `abstract boolean isValid()` |  |
| `abstract javax.xml.datatype.XMLGregorianCalendar normalize()` |  |
| `abstract void reset()` |  |
| `abstract void setDay(int)` |  |
| `abstract void setFractionalSecond(java.math.BigDecimal)` |  |
| `abstract void setHour(int)` |  |
| `abstract void setMillisecond(int)` |  |
| `abstract void setMinute(int)` |  |
| `abstract void setMonth(int)` |  |
| `abstract void setSecond(int)` |  |
| `void setTime(int, int, int)` |  |
| `void setTime(int, int, int, java.math.BigDecimal)` |  |
| `void setTime(int, int, int, int)` |  |
| `abstract void setTimezone(int)` |  |
| `abstract void setYear(java.math.BigInteger)` |  |
| `abstract void setYear(int)` |  |
| `abstract java.util.GregorianCalendar toGregorianCalendar()` |  |
| `abstract java.util.GregorianCalendar toGregorianCalendar(java.util.TimeZone, java.util.Locale, javax.xml.datatype.XMLGregorianCalendar)` |  |
| `abstract String toXMLFormat()` |  |

---

## Package: `javax.xml.namespace`

### `interface NamespaceContext`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getNamespaceURI(String)` |  |
| `String getPrefix(String)` |  |
| `java.util.Iterator getPrefixes(String)` |  |

---

### `class QName`

- **实现：** `java.io.Serializable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `QName(String, String)` |  |
| `QName(String, String, String)` |  |
| `QName(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final boolean equals(Object)` |  |
| `String getLocalPart()` |  |
| `String getNamespaceURI()` |  |
| `String getPrefix()` |  |
| `final int hashCode()` |  |
| `static javax.xml.namespace.QName valueOf(String)` |  |

---

## Package: `javax.xml.parsers`

### `class abstract DocumentBuilder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DocumentBuilder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract org.w3c.dom.DOMImplementation getDOMImplementation()` |  |
| `javax.xml.validation.Schema getSchema()` |  |
| `abstract boolean isNamespaceAware()` |  |
| `abstract boolean isValidating()` |  |
| `boolean isXIncludeAware()` |  |
| `abstract org.w3c.dom.Document newDocument()` |  |
| `org.w3c.dom.Document parse(java.io.InputStream) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `org.w3c.dom.Document parse(java.io.InputStream, String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `org.w3c.dom.Document parse(String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `org.w3c.dom.Document parse(java.io.File) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `abstract org.w3c.dom.Document parse(org.xml.sax.InputSource) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void reset()` |  |
| `abstract void setEntityResolver(org.xml.sax.EntityResolver)` |  |
| `abstract void setErrorHandler(org.xml.sax.ErrorHandler)` |  |

---

### `class abstract DocumentBuilderFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DocumentBuilderFactory()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract Object getAttribute(String) throws java.lang.IllegalArgumentException` |  |
| `abstract boolean getFeature(String) throws javax.xml.parsers.ParserConfigurationException` |  |
| `javax.xml.validation.Schema getSchema()` |  |
| `boolean isCoalescing()` |  |
| `boolean isExpandEntityReferences()` |  |
| `boolean isIgnoringComments()` |  |
| `boolean isIgnoringElementContentWhitespace()` |  |
| `boolean isNamespaceAware()` |  |
| `boolean isValidating()` |  |
| `boolean isXIncludeAware()` |  |
| `abstract javax.xml.parsers.DocumentBuilder newDocumentBuilder() throws javax.xml.parsers.ParserConfigurationException` |  |
| `static javax.xml.parsers.DocumentBuilderFactory newInstance()` |  |
| `static javax.xml.parsers.DocumentBuilderFactory newInstance(String, ClassLoader)` |  |
| `abstract void setAttribute(String, Object) throws java.lang.IllegalArgumentException` |  |
| `void setCoalescing(boolean)` |  |
| `void setExpandEntityReferences(boolean)` |  |
| `abstract void setFeature(String, boolean) throws javax.xml.parsers.ParserConfigurationException` |  |
| `void setIgnoringComments(boolean)` |  |
| `void setIgnoringElementContentWhitespace(boolean)` |  |
| `void setNamespaceAware(boolean)` |  |
| `void setSchema(javax.xml.validation.Schema)` |  |
| `void setValidating(boolean)` |  |
| `void setXIncludeAware(boolean)` |  |

---

### `class FactoryConfigurationError`

- **继承：** `java.lang.Error`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `FactoryConfigurationError()` |  |
| `FactoryConfigurationError(String)` |  |
| `FactoryConfigurationError(Exception)` |  |
| `FactoryConfigurationError(Exception, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `Exception getException()` |  |

---

### `class ParserConfigurationException`

- **继承：** `java.lang.Exception`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ParserConfigurationException()` |  |
| `ParserConfigurationException(String)` |  |

---

### `class abstract SAXParser`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SAXParser()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract org.xml.sax.Parser getParser() throws org.xml.sax.SAXException` |  |
| `abstract Object getProperty(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `javax.xml.validation.Schema getSchema()` |  |
| `abstract org.xml.sax.XMLReader getXMLReader() throws org.xml.sax.SAXException` |  |
| `abstract boolean isNamespaceAware()` |  |
| `abstract boolean isValidating()` |  |
| `boolean isXIncludeAware()` |  |
| `void parse(java.io.InputStream, org.xml.sax.HandlerBase) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(java.io.InputStream, org.xml.sax.HandlerBase, String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(java.io.InputStream, org.xml.sax.helpers.DefaultHandler) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(java.io.InputStream, org.xml.sax.helpers.DefaultHandler, String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(String, org.xml.sax.HandlerBase) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(String, org.xml.sax.helpers.DefaultHandler) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(java.io.File, org.xml.sax.HandlerBase) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(java.io.File, org.xml.sax.helpers.DefaultHandler) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(org.xml.sax.InputSource, org.xml.sax.HandlerBase) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(org.xml.sax.InputSource, org.xml.sax.helpers.DefaultHandler) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void reset()` |  |
| `abstract void setProperty(String, Object) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |

---

### `class abstract SAXParserFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SAXParserFactory()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract boolean getFeature(String) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `javax.xml.validation.Schema getSchema()` |  |
| `boolean isNamespaceAware()` |  |
| `boolean isValidating()` |  |
| `boolean isXIncludeAware()` |  |
| `static javax.xml.parsers.SAXParserFactory newInstance()` |  |
| `static javax.xml.parsers.SAXParserFactory newInstance(String, ClassLoader)` |  |
| `abstract javax.xml.parsers.SAXParser newSAXParser() throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException` |  |
| `abstract void setFeature(String, boolean) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void setNamespaceAware(boolean)` |  |
| `void setSchema(javax.xml.validation.Schema)` |  |
| `void setValidating(boolean)` |  |
| `void setXIncludeAware(boolean)` |  |

---

## Package: `javax.xml.transform`

### `interface ErrorListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void error(javax.xml.transform.TransformerException) throws javax.xml.transform.TransformerException` |  |
| `void fatalError(javax.xml.transform.TransformerException) throws javax.xml.transform.TransformerException` |  |
| `void warning(javax.xml.transform.TransformerException) throws javax.xml.transform.TransformerException` |  |

---

### `class OutputKeys`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CDATA_SECTION_ELEMENTS = "cdata-section-elements"` |  |
| `static final String DOCTYPE_PUBLIC = "doctype-public"` |  |
| `static final String DOCTYPE_SYSTEM = "doctype-system"` |  |
| `static final String ENCODING = "encoding"` |  |
| `static final String INDENT = "indent"` |  |
| `static final String MEDIA_TYPE = "media-type"` |  |
| `static final String METHOD = "method"` |  |
| `static final String OMIT_XML_DECLARATION = "omit-xml-declaration"` |  |
| `static final String STANDALONE = "standalone"` |  |
| `static final String VERSION = "version"` |  |

---

### `interface Result`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String PI_DISABLE_OUTPUT_ESCAPING = "javax.xml.transform.disable-output-escaping"` |  |
| `static final String PI_ENABLE_OUTPUT_ESCAPING = "javax.xml.transform.enable-output-escaping"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getSystemId()` |  |
| `void setSystemId(String)` |  |

---

### `interface Source`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getSystemId()` |  |
| `void setSystemId(String)` |  |

---

### `interface SourceLocator`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getColumnNumber()` |  |
| `int getLineNumber()` |  |
| `String getPublicId()` |  |
| `String getSystemId()` |  |

---

### `interface Templates`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.util.Properties getOutputProperties()` |  |
| `javax.xml.transform.Transformer newTransformer() throws javax.xml.transform.TransformerConfigurationException` |  |

---

### `class abstract Transformer`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Transformer()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void clearParameters()` |  |
| `abstract javax.xml.transform.ErrorListener getErrorListener()` |  |
| `abstract java.util.Properties getOutputProperties()` |  |
| `abstract String getOutputProperty(String) throws java.lang.IllegalArgumentException` |  |
| `abstract Object getParameter(String)` |  |
| `abstract javax.xml.transform.URIResolver getURIResolver()` |  |
| `void reset()` |  |
| `abstract void setErrorListener(javax.xml.transform.ErrorListener) throws java.lang.IllegalArgumentException` |  |
| `abstract void setOutputProperties(java.util.Properties)` |  |
| `abstract void setOutputProperty(String, String) throws java.lang.IllegalArgumentException` |  |
| `abstract void setParameter(String, Object)` |  |
| `abstract void setURIResolver(javax.xml.transform.URIResolver)` |  |
| `abstract void transform(javax.xml.transform.Source, javax.xml.transform.Result) throws javax.xml.transform.TransformerException` |  |

---

### `class TransformerConfigurationException`

- **继承：** `javax.xml.transform.TransformerException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TransformerConfigurationException()` |  |
| `TransformerConfigurationException(String)` |  |
| `TransformerConfigurationException(Throwable)` |  |
| `TransformerConfigurationException(String, Throwable)` |  |
| `TransformerConfigurationException(String, javax.xml.transform.SourceLocator)` |  |
| `TransformerConfigurationException(String, javax.xml.transform.SourceLocator, Throwable)` |  |

---

### `class TransformerException`

- **继承：** `java.lang.Exception`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TransformerException(String)` |  |
| `TransformerException(Throwable)` |  |
| `TransformerException(String, Throwable)` |  |
| `TransformerException(String, javax.xml.transform.SourceLocator)` |  |
| `TransformerException(String, javax.xml.transform.SourceLocator, Throwable)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `Throwable getException()` |  |
| `String getLocationAsString()` |  |
| `javax.xml.transform.SourceLocator getLocator()` |  |
| `String getMessageAndLocation()` |  |
| `void setLocator(javax.xml.transform.SourceLocator)` |  |

---

### `class abstract TransformerFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TransformerFactory()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract javax.xml.transform.Source getAssociatedStylesheet(javax.xml.transform.Source, String, String, String) throws javax.xml.transform.TransformerConfigurationException` |  |
| `abstract Object getAttribute(String)` |  |
| `abstract javax.xml.transform.ErrorListener getErrorListener()` |  |
| `abstract boolean getFeature(String)` |  |
| `abstract javax.xml.transform.URIResolver getURIResolver()` |  |
| `static javax.xml.transform.TransformerFactory newInstance() throws javax.xml.transform.TransformerFactoryConfigurationError` |  |
| `static javax.xml.transform.TransformerFactory newInstance(String, ClassLoader) throws javax.xml.transform.TransformerFactoryConfigurationError` |  |
| `abstract javax.xml.transform.Templates newTemplates(javax.xml.transform.Source) throws javax.xml.transform.TransformerConfigurationException` |  |
| `abstract javax.xml.transform.Transformer newTransformer(javax.xml.transform.Source) throws javax.xml.transform.TransformerConfigurationException` |  |
| `abstract javax.xml.transform.Transformer newTransformer() throws javax.xml.transform.TransformerConfigurationException` |  |
| `abstract void setAttribute(String, Object)` |  |
| `abstract void setErrorListener(javax.xml.transform.ErrorListener)` |  |
| `abstract void setFeature(String, boolean) throws javax.xml.transform.TransformerConfigurationException` |  |
| `abstract void setURIResolver(javax.xml.transform.URIResolver)` |  |

---

### `class TransformerFactoryConfigurationError`

- **继承：** `java.lang.Error`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TransformerFactoryConfigurationError()` |  |
| `TransformerFactoryConfigurationError(String)` |  |
| `TransformerFactoryConfigurationError(Exception)` |  |
| `TransformerFactoryConfigurationError(Exception, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `Exception getException()` |  |

---

### `interface URIResolver`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `javax.xml.transform.Source resolve(String, String) throws javax.xml.transform.TransformerException` |  |

---

## Package: `javax.xml.transform.dom`

### `interface DOMLocator`

- **继承：** `javax.xml.transform.SourceLocator`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.w3c.dom.Node getOriginatingNode()` |  |

---

### `class DOMResult`

- **实现：** `javax.xml.transform.Result`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DOMResult()` |  |
| `DOMResult(org.w3c.dom.Node)` |  |
| `DOMResult(org.w3c.dom.Node, String)` |  |
| `DOMResult(org.w3c.dom.Node, org.w3c.dom.Node)` |  |
| `DOMResult(org.w3c.dom.Node, org.w3c.dom.Node, String)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String FEATURE = "http://javax.xml.transform.dom.DOMResult/feature"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.w3c.dom.Node getNextSibling()` |  |
| `org.w3c.dom.Node getNode()` |  |
| `String getSystemId()` |  |
| `void setNextSibling(org.w3c.dom.Node)` |  |
| `void setNode(org.w3c.dom.Node)` |  |
| `void setSystemId(String)` |  |

---

### `class DOMSource`

- **实现：** `javax.xml.transform.Source`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DOMSource()` |  |
| `DOMSource(org.w3c.dom.Node)` |  |
| `DOMSource(org.w3c.dom.Node, String)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String FEATURE = "http://javax.xml.transform.dom.DOMSource/feature"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.w3c.dom.Node getNode()` |  |
| `String getSystemId()` |  |
| `void setNode(org.w3c.dom.Node)` |  |
| `void setSystemId(String)` |  |

---

## Package: `javax.xml.transform.sax`

### `class SAXResult`

- **实现：** `javax.xml.transform.Result`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SAXResult()` |  |
| `SAXResult(org.xml.sax.ContentHandler)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String FEATURE = "http://javax.xml.transform.sax.SAXResult/feature"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.xml.sax.ContentHandler getHandler()` |  |
| `org.xml.sax.ext.LexicalHandler getLexicalHandler()` |  |
| `String getSystemId()` |  |
| `void setHandler(org.xml.sax.ContentHandler)` |  |
| `void setLexicalHandler(org.xml.sax.ext.LexicalHandler)` |  |
| `void setSystemId(String)` |  |

---

### `class SAXSource`

- **实现：** `javax.xml.transform.Source`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SAXSource()` |  |
| `SAXSource(org.xml.sax.XMLReader, org.xml.sax.InputSource)` |  |
| `SAXSource(org.xml.sax.InputSource)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String FEATURE = "http://javax.xml.transform.sax.SAXSource/feature"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.xml.sax.InputSource getInputSource()` |  |
| `String getSystemId()` |  |
| `org.xml.sax.XMLReader getXMLReader()` |  |
| `void setInputSource(org.xml.sax.InputSource)` |  |
| `void setSystemId(String)` |  |
| `void setXMLReader(org.xml.sax.XMLReader)` |  |
| `static org.xml.sax.InputSource sourceToInputSource(javax.xml.transform.Source)` |  |

---

### `class abstract SAXTransformerFactory`

- **继承：** `javax.xml.transform.TransformerFactory`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SAXTransformerFactory()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String FEATURE = "http://javax.xml.transform.sax.SAXTransformerFactory/feature"` |  |
| `static final String FEATURE_XMLFILTER = "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract javax.xml.transform.sax.TemplatesHandler newTemplatesHandler() throws javax.xml.transform.TransformerConfigurationException` |  |
| `abstract javax.xml.transform.sax.TransformerHandler newTransformerHandler(javax.xml.transform.Source) throws javax.xml.transform.TransformerConfigurationException` |  |
| `abstract javax.xml.transform.sax.TransformerHandler newTransformerHandler(javax.xml.transform.Templates) throws javax.xml.transform.TransformerConfigurationException` |  |
| `abstract javax.xml.transform.sax.TransformerHandler newTransformerHandler() throws javax.xml.transform.TransformerConfigurationException` |  |
| `abstract org.xml.sax.XMLFilter newXMLFilter(javax.xml.transform.Source) throws javax.xml.transform.TransformerConfigurationException` |  |
| `abstract org.xml.sax.XMLFilter newXMLFilter(javax.xml.transform.Templates) throws javax.xml.transform.TransformerConfigurationException` |  |

---

### `interface TemplatesHandler`

- **继承：** `org.xml.sax.ContentHandler`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getSystemId()` |  |
| `javax.xml.transform.Templates getTemplates()` |  |
| `void setSystemId(String)` |  |

---

### `interface TransformerHandler`

- **继承：** `org.xml.sax.ContentHandler org.xml.sax.DTDHandler org.xml.sax.ext.LexicalHandler`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getSystemId()` |  |
| `javax.xml.transform.Transformer getTransformer()` |  |
| `void setResult(javax.xml.transform.Result) throws java.lang.IllegalArgumentException` |  |
| `void setSystemId(String)` |  |

---

## Package: `javax.xml.transform.stream`

### `class StreamResult`

- **实现：** `javax.xml.transform.Result`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `StreamResult()` |  |
| `StreamResult(java.io.OutputStream)` |  |
| `StreamResult(java.io.Writer)` |  |
| `StreamResult(String)` |  |
| `StreamResult(java.io.File)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String FEATURE = "http://javax.xml.transform.stream.StreamResult/feature"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.io.OutputStream getOutputStream()` |  |
| `String getSystemId()` |  |
| `java.io.Writer getWriter()` |  |
| `void setOutputStream(java.io.OutputStream)` |  |
| `void setSystemId(String)` |  |
| `void setSystemId(java.io.File)` |  |
| `void setWriter(java.io.Writer)` |  |

---

### `class StreamSource`

- **实现：** `javax.xml.transform.Source`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `StreamSource()` |  |
| `StreamSource(java.io.InputStream)` |  |
| `StreamSource(java.io.InputStream, String)` |  |
| `StreamSource(java.io.Reader)` |  |
| `StreamSource(java.io.Reader, String)` |  |
| `StreamSource(String)` |  |
| `StreamSource(java.io.File)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String FEATURE = "http://javax.xml.transform.stream.StreamSource/feature"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.io.InputStream getInputStream()` |  |
| `String getPublicId()` |  |
| `java.io.Reader getReader()` |  |
| `String getSystemId()` |  |
| `void setInputStream(java.io.InputStream)` |  |
| `void setPublicId(String)` |  |
| `void setReader(java.io.Reader)` |  |
| `void setSystemId(String)` |  |
| `void setSystemId(java.io.File)` |  |

---

## Package: `javax.xml.validation`

### `class abstract Schema`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Schema()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract javax.xml.validation.Validator newValidator()` |  |
| `abstract javax.xml.validation.ValidatorHandler newValidatorHandler()` |  |

---

### `class abstract SchemaFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SchemaFactory()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract org.xml.sax.ErrorHandler getErrorHandler()` |  |
| `boolean getFeature(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `Object getProperty(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `abstract org.w3c.dom.ls.LSResourceResolver getResourceResolver()` |  |
| `abstract boolean isSchemaLanguageSupported(String)` |  |
| `static javax.xml.validation.SchemaFactory newInstance(String)` |  |
| `static javax.xml.validation.SchemaFactory newInstance(String, String, ClassLoader)` |  |
| `javax.xml.validation.Schema newSchema(javax.xml.transform.Source) throws org.xml.sax.SAXException` |  |
| `javax.xml.validation.Schema newSchema(java.io.File) throws org.xml.sax.SAXException` |  |
| `javax.xml.validation.Schema newSchema(java.net.URL) throws org.xml.sax.SAXException` |  |
| `abstract javax.xml.validation.Schema newSchema(javax.xml.transform.Source[]) throws org.xml.sax.SAXException` |  |
| `abstract javax.xml.validation.Schema newSchema() throws org.xml.sax.SAXException` |  |
| `abstract void setErrorHandler(org.xml.sax.ErrorHandler)` |  |
| `void setFeature(String, boolean) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void setProperty(String, Object) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `abstract void setResourceResolver(org.w3c.dom.ls.LSResourceResolver)` |  |

---

### `class abstract SchemaFactoryLoader`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SchemaFactoryLoader()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract javax.xml.validation.SchemaFactory newFactory(String)` |  |

---

### `class abstract TypeInfoProvider`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TypeInfoProvider()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract org.w3c.dom.TypeInfo getAttributeTypeInfo(int)` |  |
| `abstract org.w3c.dom.TypeInfo getElementTypeInfo()` |  |
| `abstract boolean isIdAttribute(int)` |  |
| `abstract boolean isSpecified(int)` |  |

---

### `class abstract Validator`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Validator()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract org.xml.sax.ErrorHandler getErrorHandler()` |  |
| `boolean getFeature(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `Object getProperty(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `abstract org.w3c.dom.ls.LSResourceResolver getResourceResolver()` |  |
| `abstract void reset()` |  |
| `abstract void setErrorHandler(org.xml.sax.ErrorHandler)` |  |
| `void setFeature(String, boolean) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void setProperty(String, Object) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `abstract void setResourceResolver(org.w3c.dom.ls.LSResourceResolver)` |  |
| `void validate(javax.xml.transform.Source) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `abstract void validate(javax.xml.transform.Source, javax.xml.transform.Result) throws java.io.IOException, org.xml.sax.SAXException` |  |

---

### `class abstract ValidatorHandler`

- **实现：** `org.xml.sax.ContentHandler`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ValidatorHandler()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract org.xml.sax.ContentHandler getContentHandler()` |  |
| `abstract org.xml.sax.ErrorHandler getErrorHandler()` |  |
| `boolean getFeature(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `Object getProperty(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `abstract org.w3c.dom.ls.LSResourceResolver getResourceResolver()` |  |
| `abstract javax.xml.validation.TypeInfoProvider getTypeInfoProvider()` |  |
| `abstract void setContentHandler(org.xml.sax.ContentHandler)` |  |
| `abstract void setErrorHandler(org.xml.sax.ErrorHandler)` |  |
| `void setFeature(String, boolean) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void setProperty(String, Object) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `abstract void setResourceResolver(org.w3c.dom.ls.LSResourceResolver)` |  |

---

## Package: `javax.xml.xpath`

### `interface XPath`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `javax.xml.xpath.XPathExpression compile(String) throws javax.xml.xpath.XPathExpressionException` |  |
| `Object evaluate(String, Object, javax.xml.namespace.QName) throws javax.xml.xpath.XPathExpressionException` |  |
| `String evaluate(String, Object) throws javax.xml.xpath.XPathExpressionException` |  |
| `Object evaluate(String, org.xml.sax.InputSource, javax.xml.namespace.QName) throws javax.xml.xpath.XPathExpressionException` |  |
| `String evaluate(String, org.xml.sax.InputSource) throws javax.xml.xpath.XPathExpressionException` |  |
| `javax.xml.namespace.NamespaceContext getNamespaceContext()` |  |
| `javax.xml.xpath.XPathFunctionResolver getXPathFunctionResolver()` |  |
| `javax.xml.xpath.XPathVariableResolver getXPathVariableResolver()` |  |
| `void reset()` |  |
| `void setNamespaceContext(javax.xml.namespace.NamespaceContext)` |  |
| `void setXPathFunctionResolver(javax.xml.xpath.XPathFunctionResolver)` |  |
| `void setXPathVariableResolver(javax.xml.xpath.XPathVariableResolver)` |  |

---

### `class XPathConstants`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final javax.xml.namespace.QName BOOLEAN` |  |
| `static final String DOM_OBJECT_MODEL = "http://java.sun.com/jaxp/xpath/dom"` |  |
| `static final javax.xml.namespace.QName NODE` |  |
| `static final javax.xml.namespace.QName NODESET` |  |
| `static final javax.xml.namespace.QName NUMBER` |  |
| `static final javax.xml.namespace.QName STRING` |  |

---

### `class XPathException`

- **继承：** `java.lang.Exception`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `XPathException(String)` |  |
| `XPathException(Throwable)` |  |

---

### `interface XPathExpression`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `Object evaluate(Object, javax.xml.namespace.QName) throws javax.xml.xpath.XPathExpressionException` |  |
| `String evaluate(Object) throws javax.xml.xpath.XPathExpressionException` |  |
| `Object evaluate(org.xml.sax.InputSource, javax.xml.namespace.QName) throws javax.xml.xpath.XPathExpressionException` |  |
| `String evaluate(org.xml.sax.InputSource) throws javax.xml.xpath.XPathExpressionException` |  |

---

### `class XPathExpressionException`

- **继承：** `javax.xml.xpath.XPathException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `XPathExpressionException(String)` |  |
| `XPathExpressionException(Throwable)` |  |

---

### `class abstract XPathFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `XPathFactory()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DEFAULT_OBJECT_MODEL_URI = "http://java.sun.com/jaxp/xpath/dom"` |  |
| `static final String DEFAULT_PROPERTY_NAME = "javax.xml.xpath.XPathFactory"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract boolean getFeature(String) throws javax.xml.xpath.XPathFactoryConfigurationException` |  |
| `abstract boolean isObjectModelSupported(String)` |  |
| `static final javax.xml.xpath.XPathFactory newInstance()` |  |
| `static final javax.xml.xpath.XPathFactory newInstance(String) throws javax.xml.xpath.XPathFactoryConfigurationException` |  |
| `static javax.xml.xpath.XPathFactory newInstance(String, String, ClassLoader) throws javax.xml.xpath.XPathFactoryConfigurationException` |  |
| `abstract javax.xml.xpath.XPath newXPath()` |  |
| `abstract void setFeature(String, boolean) throws javax.xml.xpath.XPathFactoryConfigurationException` |  |
| `abstract void setXPathFunctionResolver(javax.xml.xpath.XPathFunctionResolver)` |  |
| `abstract void setXPathVariableResolver(javax.xml.xpath.XPathVariableResolver)` |  |

---

### `class XPathFactoryConfigurationException`

- **继承：** `javax.xml.xpath.XPathException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `XPathFactoryConfigurationException(String)` |  |
| `XPathFactoryConfigurationException(Throwable)` |  |

---

### `interface XPathFunction`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `Object evaluate(java.util.List) throws javax.xml.xpath.XPathFunctionException` |  |

---

### `class XPathFunctionException`

- **继承：** `javax.xml.xpath.XPathExpressionException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `XPathFunctionException(String)` |  |
| `XPathFunctionException(Throwable)` |  |

---

### `interface XPathFunctionResolver`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `javax.xml.xpath.XPathFunction resolveFunction(javax.xml.namespace.QName, int)` |  |

---

### `interface XPathVariableResolver`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `Object resolveVariable(javax.xml.namespace.QName)` |  |

---

## Package: `org.apache.http.conn`

### `class ConnectTimeoutException` ~~DEPRECATED~~

- **继承：** `java.io.InterruptedIOException`
- **注解：** `@Deprecated`

---

## Package: `org.apache.http.conn.scheme`

### `interface HostNameResolver` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface LayeredSocketFactory` ~~DEPRECATED~~

- **继承：** `org.apache.http.conn.scheme.SocketFactory`
- **注解：** `@Deprecated`

---

### `interface SocketFactory` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

## Package: `org.apache.http.conn.ssl`

### `class abstract AbstractVerifier` ~~DEPRECATED~~

- **实现：** `org.apache.http.conn.ssl.X509HostnameVerifier`
- **注解：** `@Deprecated`

---

### `class AllowAllHostnameVerifier` ~~DEPRECATED~~

- **继承：** `org.apache.http.conn.ssl.AbstractVerifier`
- **注解：** `@Deprecated`

---

### `class BrowserCompatHostnameVerifier` ~~DEPRECATED~~

- **继承：** `org.apache.http.conn.ssl.AbstractVerifier`
- **注解：** `@Deprecated`

---

### `class SSLSocketFactory` ~~DEPRECATED~~

- **实现：** `org.apache.http.conn.scheme.LayeredSocketFactory`
- **注解：** `@Deprecated`

---

### `class StrictHostnameVerifier` ~~DEPRECATED~~

- **继承：** `org.apache.http.conn.ssl.AbstractVerifier`
- **注解：** `@Deprecated`

---

### `interface X509HostnameVerifier` ~~DEPRECATED~~

- **继承：** `javax.net.ssl.HostnameVerifier`
- **注解：** `@Deprecated`

---

## Package: `org.apache.http.params`

### `interface CoreConnectionPNames` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class final HttpConnectionParams` ~~DEPRECATED~~

- **实现：** `org.apache.http.params.CoreConnectionPNames`
- **注解：** `@Deprecated`

---

### `interface HttpParams` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

## Package: `org.json`

### `class JSONArray`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `JSONArray()` |  |
| `JSONArray(java.util.Collection)` |  |
| `JSONArray(org.json.JSONTokener) throws org.json.JSONException` |  |
| `JSONArray(String) throws org.json.JSONException` |  |
| `JSONArray(Object) throws org.json.JSONException` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `Object get(int) throws org.json.JSONException` |  |
| `boolean getBoolean(int) throws org.json.JSONException` |  |
| `double getDouble(int) throws org.json.JSONException` |  |
| `int getInt(int) throws org.json.JSONException` |  |
| `org.json.JSONArray getJSONArray(int) throws org.json.JSONException` |  |
| `org.json.JSONObject getJSONObject(int) throws org.json.JSONException` |  |
| `long getLong(int) throws org.json.JSONException` |  |
| `String getString(int) throws org.json.JSONException` |  |
| `boolean isNull(int)` |  |
| `String join(String) throws org.json.JSONException` |  |
| `int length()` |  |
| `Object opt(int)` |  |
| `boolean optBoolean(int)` |  |
| `boolean optBoolean(int, boolean)` |  |
| `double optDouble(int)` |  |
| `double optDouble(int, double)` |  |
| `int optInt(int)` |  |
| `int optInt(int, int)` |  |
| `org.json.JSONArray optJSONArray(int)` |  |
| `org.json.JSONObject optJSONObject(int)` |  |
| `long optLong(int)` |  |
| `long optLong(int, long)` |  |
| `String optString(int)` |  |
| `String optString(int, String)` |  |
| `org.json.JSONArray put(boolean)` |  |
| `org.json.JSONArray put(double) throws org.json.JSONException` |  |
| `org.json.JSONArray put(int)` |  |
| `org.json.JSONArray put(long)` |  |
| `org.json.JSONArray put(Object)` |  |
| `org.json.JSONArray put(int, boolean) throws org.json.JSONException` |  |
| `org.json.JSONArray put(int, double) throws org.json.JSONException` |  |
| `org.json.JSONArray put(int, int) throws org.json.JSONException` |  |
| `org.json.JSONArray put(int, long) throws org.json.JSONException` |  |
| `org.json.JSONArray put(int, Object) throws org.json.JSONException` |  |
| `Object remove(int)` |  |
| `org.json.JSONObject toJSONObject(org.json.JSONArray) throws org.json.JSONException` |  |
| `String toString(int) throws org.json.JSONException` |  |

---

### `class JSONException`

- **继承：** `java.lang.Exception`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `JSONException(String)` |  |
| `JSONException(String, Throwable)` |  |
| `JSONException(Throwable)` |  |

---

### `class JSONObject`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `JSONObject()` |  |
| `JSONObject(@NonNull java.util.Map)` |  |
| `JSONObject(@NonNull org.json.JSONTokener) throws org.json.JSONException` |  |
| `JSONObject(@NonNull String) throws org.json.JSONException` |  |
| `JSONObject(@NonNull org.json.JSONObject, @NonNull String[]) throws org.json.JSONException` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean getBoolean(@NonNull String) throws org.json.JSONException` |  |
| `double getDouble(@NonNull String) throws org.json.JSONException` |  |
| `int getInt(@NonNull String) throws org.json.JSONException` |  |
| `long getLong(@NonNull String) throws org.json.JSONException` |  |
| `boolean has(@Nullable String)` |  |
| `boolean isNull(@Nullable String)` |  |
| `int length()` |  |
| `boolean optBoolean(@Nullable String)` |  |
| `boolean optBoolean(@Nullable String, boolean)` |  |
| `double optDouble(@Nullable String)` |  |
| `double optDouble(@Nullable String, double)` |  |
| `int optInt(@Nullable String)` |  |
| `int optInt(@Nullable String, int)` |  |
| `long optLong(@Nullable String)` |  |
| `long optLong(@Nullable String, long)` |  |

---

### `class JSONStringer`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `JSONStringer()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.json.JSONStringer array() throws org.json.JSONException` |  |
| `org.json.JSONStringer endArray() throws org.json.JSONException` |  |
| `org.json.JSONStringer endObject() throws org.json.JSONException` |  |
| `org.json.JSONStringer key(String) throws org.json.JSONException` |  |
| `org.json.JSONStringer object() throws org.json.JSONException` |  |
| `org.json.JSONStringer value(Object) throws org.json.JSONException` |  |
| `org.json.JSONStringer value(boolean) throws org.json.JSONException` |  |
| `org.json.JSONStringer value(double) throws org.json.JSONException` |  |
| `org.json.JSONStringer value(long) throws org.json.JSONException` |  |

---

### `class JSONTokener`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `JSONTokener(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void back()` |  |
| `static int dehexchar(char)` |  |
| `boolean more()` |  |
| `char next()` |  |
| `char next(char) throws org.json.JSONException` |  |
| `String next(int) throws org.json.JSONException` |  |
| `char nextClean() throws org.json.JSONException` |  |
| `String nextString(char) throws org.json.JSONException` |  |
| `String nextTo(String)` |  |
| `String nextTo(char)` |  |
| `Object nextValue() throws org.json.JSONException` |  |
| `void skipPast(String)` |  |
| `char skipTo(char)` |  |
| `org.json.JSONException syntaxError(String)` |  |

---

## Package: `org.w3c.dom`

### `interface Attr`

- **继承：** `org.w3c.dom.Node`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getName()` |  |
| `org.w3c.dom.Element getOwnerElement()` |  |
| `org.w3c.dom.TypeInfo getSchemaTypeInfo()` |  |
| `boolean getSpecified()` |  |
| `String getValue()` |  |
| `boolean isId()` |  |
| `void setValue(String) throws org.w3c.dom.DOMException` |  |

---

### `interface CDATASection`

- **继承：** `org.w3c.dom.Text`

---

### `interface CharacterData`

- **继承：** `org.w3c.dom.Node`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void appendData(String) throws org.w3c.dom.DOMException` |  |
| `void deleteData(int, int) throws org.w3c.dom.DOMException` |  |
| `String getData() throws org.w3c.dom.DOMException` |  |
| `int getLength()` |  |
| `void insertData(int, String) throws org.w3c.dom.DOMException` |  |
| `void replaceData(int, int, String) throws org.w3c.dom.DOMException` |  |
| `void setData(String) throws org.w3c.dom.DOMException` |  |
| `String substringData(int, int) throws org.w3c.dom.DOMException` |  |

---

### `interface Comment`

- **继承：** `org.w3c.dom.CharacterData`

---

### `interface DOMConfiguration`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean canSetParameter(String, Object)` |  |
| `Object getParameter(String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.DOMStringList getParameterNames()` |  |
| `void setParameter(String, Object) throws org.w3c.dom.DOMException` |  |

---

### `interface DOMError`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final short SEVERITY_ERROR = 2` |  |
| `static final short SEVERITY_FATAL_ERROR = 3` |  |
| `static final short SEVERITY_WARNING = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.w3c.dom.DOMLocator getLocation()` |  |
| `String getMessage()` |  |
| `Object getRelatedData()` |  |
| `Object getRelatedException()` |  |
| `short getSeverity()` |  |
| `String getType()` |  |

---

### `interface DOMErrorHandler`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean handleError(org.w3c.dom.DOMError)` |  |

---

### `class DOMException`

- **继承：** `java.lang.RuntimeException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DOMException(short, String)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final short DOMSTRING_SIZE_ERR = 2` |  |
| `static final short HIERARCHY_REQUEST_ERR = 3` |  |
| `static final short INDEX_SIZE_ERR = 1` |  |
| `static final short INUSE_ATTRIBUTE_ERR = 10` |  |
| `static final short INVALID_ACCESS_ERR = 15` |  |
| `static final short INVALID_CHARACTER_ERR = 5` |  |
| `static final short INVALID_MODIFICATION_ERR = 13` |  |
| `static final short INVALID_STATE_ERR = 11` |  |
| `static final short NAMESPACE_ERR = 14` |  |
| `static final short NOT_FOUND_ERR = 8` |  |
| `static final short NOT_SUPPORTED_ERR = 9` |  |
| `static final short NO_DATA_ALLOWED_ERR = 6` |  |
| `static final short NO_MODIFICATION_ALLOWED_ERR = 7` |  |
| `static final short SYNTAX_ERR = 12` |  |
| `static final short TYPE_MISMATCH_ERR = 17` |  |
| `static final short VALIDATION_ERR = 16` |  |
| `static final short WRONG_DOCUMENT_ERR = 4` |  |
| `short code` |  |

---

### `interface DOMImplementation`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.w3c.dom.Document createDocument(String, String, org.w3c.dom.DocumentType) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.DocumentType createDocumentType(String, String, String) throws org.w3c.dom.DOMException` |  |
| `Object getFeature(String, String)` |  |
| `boolean hasFeature(String, String)` |  |

---

### `interface DOMImplementationList`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getLength()` |  |
| `org.w3c.dom.DOMImplementation item(int)` |  |

---

### `interface DOMImplementationSource`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.w3c.dom.DOMImplementation getDOMImplementation(String)` |  |
| `org.w3c.dom.DOMImplementationList getDOMImplementationList(String)` |  |

---

### `interface DOMLocator`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getByteOffset()` |  |
| `int getColumnNumber()` |  |
| `int getLineNumber()` |  |
| `org.w3c.dom.Node getRelatedNode()` |  |
| `String getUri()` |  |
| `int getUtf16Offset()` |  |

---

### `interface DOMStringList`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean contains(String)` |  |
| `int getLength()` |  |
| `String item(int)` |  |

---

### `interface Document`

- **继承：** `org.w3c.dom.Node`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.w3c.dom.Node adoptNode(org.w3c.dom.Node) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Attr createAttribute(String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Attr createAttributeNS(String, String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.CDATASection createCDATASection(String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Comment createComment(String)` |  |
| `org.w3c.dom.DocumentFragment createDocumentFragment()` |  |
| `org.w3c.dom.Element createElement(String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Element createElementNS(String, String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.EntityReference createEntityReference(String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.ProcessingInstruction createProcessingInstruction(String, String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Text createTextNode(String)` |  |
| `org.w3c.dom.DocumentType getDoctype()` |  |
| `org.w3c.dom.Element getDocumentElement()` |  |
| `String getDocumentURI()` |  |
| `org.w3c.dom.DOMConfiguration getDomConfig()` |  |
| `org.w3c.dom.Element getElementById(String)` |  |
| `org.w3c.dom.NodeList getElementsByTagName(String)` |  |
| `org.w3c.dom.NodeList getElementsByTagNameNS(String, String)` |  |
| `org.w3c.dom.DOMImplementation getImplementation()` |  |
| `String getInputEncoding()` |  |
| `boolean getStrictErrorChecking()` |  |
| `String getXmlEncoding()` |  |
| `boolean getXmlStandalone()` |  |
| `String getXmlVersion()` |  |
| `org.w3c.dom.Node importNode(org.w3c.dom.Node, boolean) throws org.w3c.dom.DOMException` |  |
| `void normalizeDocument()` |  |
| `org.w3c.dom.Node renameNode(org.w3c.dom.Node, String, String) throws org.w3c.dom.DOMException` |  |
| `void setDocumentURI(String)` |  |
| `void setStrictErrorChecking(boolean)` |  |
| `void setXmlStandalone(boolean) throws org.w3c.dom.DOMException` |  |
| `void setXmlVersion(String) throws org.w3c.dom.DOMException` |  |

---

### `interface DocumentFragment`

- **继承：** `org.w3c.dom.Node`

---

### `interface DocumentType`

- **继承：** `org.w3c.dom.Node`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.w3c.dom.NamedNodeMap getEntities()` |  |
| `String getInternalSubset()` |  |
| `String getName()` |  |
| `org.w3c.dom.NamedNodeMap getNotations()` |  |
| `String getPublicId()` |  |
| `String getSystemId()` |  |

---

### `interface Element`

- **继承：** `org.w3c.dom.Node`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getAttribute(String)` |  |
| `String getAttributeNS(String, String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Attr getAttributeNode(String)` |  |
| `org.w3c.dom.Attr getAttributeNodeNS(String, String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.NodeList getElementsByTagName(String)` |  |
| `org.w3c.dom.NodeList getElementsByTagNameNS(String, String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.TypeInfo getSchemaTypeInfo()` |  |
| `String getTagName()` |  |
| `boolean hasAttribute(String)` |  |
| `boolean hasAttributeNS(String, String) throws org.w3c.dom.DOMException` |  |
| `void removeAttribute(String) throws org.w3c.dom.DOMException` |  |
| `void removeAttributeNS(String, String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Attr removeAttributeNode(org.w3c.dom.Attr) throws org.w3c.dom.DOMException` |  |
| `void setAttribute(String, String) throws org.w3c.dom.DOMException` |  |
| `void setAttributeNS(String, String, String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Attr setAttributeNode(org.w3c.dom.Attr) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Attr setAttributeNodeNS(org.w3c.dom.Attr) throws org.w3c.dom.DOMException` |  |
| `void setIdAttribute(String, boolean) throws org.w3c.dom.DOMException` |  |
| `void setIdAttributeNS(String, String, boolean) throws org.w3c.dom.DOMException` |  |
| `void setIdAttributeNode(org.w3c.dom.Attr, boolean) throws org.w3c.dom.DOMException` |  |

---

### `interface Entity`

- **继承：** `org.w3c.dom.Node`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getInputEncoding()` |  |
| `String getNotationName()` |  |
| `String getPublicId()` |  |
| `String getSystemId()` |  |
| `String getXmlEncoding()` |  |
| `String getXmlVersion()` |  |

---

### `interface EntityReference`

- **继承：** `org.w3c.dom.Node`

---

### `interface NameList`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean contains(String)` |  |
| `boolean containsNS(String, String)` |  |
| `int getLength()` |  |
| `String getName(int)` |  |
| `String getNamespaceURI(int)` |  |

---

### `interface NamedNodeMap`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getLength()` |  |
| `org.w3c.dom.Node getNamedItem(String)` |  |
| `org.w3c.dom.Node getNamedItemNS(String, String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Node item(int)` |  |
| `org.w3c.dom.Node removeNamedItem(String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Node removeNamedItemNS(String, String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Node setNamedItem(org.w3c.dom.Node) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Node setNamedItemNS(org.w3c.dom.Node) throws org.w3c.dom.DOMException` |  |

---

### `interface Node`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final short ATTRIBUTE_NODE = 2` |  |
| `static final short CDATA_SECTION_NODE = 4` |  |
| `static final short COMMENT_NODE = 8` |  |
| `static final short DOCUMENT_FRAGMENT_NODE = 11` |  |
| `static final short DOCUMENT_NODE = 9` |  |
| `static final short DOCUMENT_POSITION_CONTAINED_BY = 16` |  |
| `static final short DOCUMENT_POSITION_CONTAINS = 8` |  |
| `static final short DOCUMENT_POSITION_DISCONNECTED = 1` |  |
| `static final short DOCUMENT_POSITION_FOLLOWING = 4` |  |
| `static final short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 32` |  |
| `static final short DOCUMENT_POSITION_PRECEDING = 2` |  |
| `static final short DOCUMENT_TYPE_NODE = 10` |  |
| `static final short ELEMENT_NODE = 1` |  |
| `static final short ENTITY_NODE = 6` |  |
| `static final short ENTITY_REFERENCE_NODE = 5` |  |
| `static final short NOTATION_NODE = 12` |  |
| `static final short PROCESSING_INSTRUCTION_NODE = 7` |  |
| `static final short TEXT_NODE = 3` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.w3c.dom.Node appendChild(org.w3c.dom.Node) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Node cloneNode(boolean)` |  |
| `short compareDocumentPosition(org.w3c.dom.Node) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.NamedNodeMap getAttributes()` |  |
| `String getBaseURI()` |  |
| `org.w3c.dom.NodeList getChildNodes()` |  |
| `Object getFeature(String, String)` |  |
| `org.w3c.dom.Node getFirstChild()` |  |
| `org.w3c.dom.Node getLastChild()` |  |
| `String getLocalName()` |  |
| `String getNamespaceURI()` |  |
| `org.w3c.dom.Node getNextSibling()` |  |
| `String getNodeName()` |  |
| `short getNodeType()` |  |
| `String getNodeValue() throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Document getOwnerDocument()` |  |
| `org.w3c.dom.Node getParentNode()` |  |
| `String getPrefix()` |  |
| `org.w3c.dom.Node getPreviousSibling()` |  |
| `String getTextContent() throws org.w3c.dom.DOMException` |  |
| `Object getUserData(String)` |  |
| `boolean hasAttributes()` |  |
| `boolean hasChildNodes()` |  |
| `org.w3c.dom.Node insertBefore(org.w3c.dom.Node, org.w3c.dom.Node) throws org.w3c.dom.DOMException` |  |
| `boolean isDefaultNamespace(String)` |  |
| `boolean isEqualNode(org.w3c.dom.Node)` |  |
| `boolean isSameNode(org.w3c.dom.Node)` |  |
| `boolean isSupported(String, String)` |  |
| `String lookupNamespaceURI(String)` |  |
| `String lookupPrefix(String)` |  |
| `void normalize()` |  |
| `org.w3c.dom.Node removeChild(org.w3c.dom.Node) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Node replaceChild(org.w3c.dom.Node, org.w3c.dom.Node) throws org.w3c.dom.DOMException` |  |
| `void setNodeValue(String) throws org.w3c.dom.DOMException` |  |
| `void setPrefix(String) throws org.w3c.dom.DOMException` |  |
| `void setTextContent(String) throws org.w3c.dom.DOMException` |  |
| `Object setUserData(String, Object, org.w3c.dom.UserDataHandler)` |  |

---

### `interface NodeList`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getLength()` |  |
| `org.w3c.dom.Node item(int)` |  |

---

### `interface Notation`

- **继承：** `org.w3c.dom.Node`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getPublicId()` |  |
| `String getSystemId()` |  |

---

### `interface ProcessingInstruction`

- **继承：** `org.w3c.dom.Node`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getData()` |  |
| `String getTarget()` |  |
| `void setData(String) throws org.w3c.dom.DOMException` |  |

---

### `interface Text`

- **继承：** `org.w3c.dom.CharacterData`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getWholeText()` |  |
| `boolean isElementContentWhitespace()` |  |
| `org.w3c.dom.Text replaceWholeText(String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.Text splitText(int) throws org.w3c.dom.DOMException` |  |

---

### `interface TypeInfo`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DERIVATION_EXTENSION = 2` |  |
| `static final int DERIVATION_LIST = 8` |  |
| `static final int DERIVATION_RESTRICTION = 1` |  |
| `static final int DERIVATION_UNION = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getTypeName()` |  |
| `String getTypeNamespace()` |  |
| `boolean isDerivedFrom(String, String, int)` |  |

---

### `interface UserDataHandler`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final short NODE_ADOPTED = 5` |  |
| `static final short NODE_CLONED = 1` |  |
| `static final short NODE_DELETED = 3` |  |
| `static final short NODE_IMPORTED = 2` |  |
| `static final short NODE_RENAMED = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void handle(short, String, Object, org.w3c.dom.Node, org.w3c.dom.Node)` |  |

---

## Package: `org.w3c.dom.ls`

### `interface DOMImplementationLS`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final short MODE_ASYNCHRONOUS = 2` |  |
| `static final short MODE_SYNCHRONOUS = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.w3c.dom.ls.LSInput createLSInput()` |  |
| `org.w3c.dom.ls.LSOutput createLSOutput()` |  |
| `org.w3c.dom.ls.LSParser createLSParser(short, String) throws org.w3c.dom.DOMException` |  |
| `org.w3c.dom.ls.LSSerializer createLSSerializer()` |  |

---

### `class LSException`

- **继承：** `java.lang.RuntimeException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LSException(short, String)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final short PARSE_ERR = 81` |  |
| `static final short SERIALIZE_ERR = 82` |  |
| `short code` |  |

---

### `interface LSInput`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getBaseURI()` |  |
| `java.io.InputStream getByteStream()` |  |
| `boolean getCertifiedText()` |  |
| `java.io.Reader getCharacterStream()` |  |
| `String getEncoding()` |  |
| `String getPublicId()` |  |
| `String getStringData()` |  |
| `String getSystemId()` |  |
| `void setBaseURI(String)` |  |
| `void setByteStream(java.io.InputStream)` |  |
| `void setCertifiedText(boolean)` |  |
| `void setCharacterStream(java.io.Reader)` |  |
| `void setEncoding(String)` |  |
| `void setPublicId(String)` |  |
| `void setStringData(String)` |  |
| `void setSystemId(String)` |  |

---

### `interface LSOutput`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.io.OutputStream getByteStream()` |  |
| `java.io.Writer getCharacterStream()` |  |
| `String getEncoding()` |  |
| `String getSystemId()` |  |
| `void setByteStream(java.io.OutputStream)` |  |
| `void setCharacterStream(java.io.Writer)` |  |
| `void setEncoding(String)` |  |
| `void setSystemId(String)` |  |

---

### `interface LSParser`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final short ACTION_APPEND_AS_CHILDREN = 1` |  |
| `static final short ACTION_INSERT_AFTER = 4` |  |
| `static final short ACTION_INSERT_BEFORE = 3` |  |
| `static final short ACTION_REPLACE = 5` |  |
| `static final short ACTION_REPLACE_CHILDREN = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void abort()` |  |
| `boolean getAsync()` |  |
| `boolean getBusy()` |  |
| `org.w3c.dom.DOMConfiguration getDomConfig()` |  |
| `org.w3c.dom.ls.LSParserFilter getFilter()` |  |
| `org.w3c.dom.Document parse(org.w3c.dom.ls.LSInput) throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException` |  |
| `org.w3c.dom.Document parseURI(String) throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException` |  |
| `org.w3c.dom.Node parseWithContext(org.w3c.dom.ls.LSInput, org.w3c.dom.Node, short) throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException` |  |
| `void setFilter(org.w3c.dom.ls.LSParserFilter)` |  |

---

### `interface LSParserFilter`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final short FILTER_ACCEPT = 1` |  |
| `static final short FILTER_INTERRUPT = 4` |  |
| `static final short FILTER_REJECT = 2` |  |
| `static final short FILTER_SKIP = 3` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `short acceptNode(org.w3c.dom.Node)` |  |
| `int getWhatToShow()` |  |
| `short startElement(org.w3c.dom.Element)` |  |

---

### `interface LSResourceResolver`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.w3c.dom.ls.LSInput resolveResource(String, String, String, String, String)` |  |

---

### `interface LSSerializer`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.w3c.dom.DOMConfiguration getDomConfig()` |  |
| `String getNewLine()` |  |
| `void setNewLine(String)` |  |
| `boolean write(org.w3c.dom.Node, org.w3c.dom.ls.LSOutput) throws org.w3c.dom.ls.LSException` |  |
| `String writeToString(org.w3c.dom.Node) throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException` |  |
| `boolean writeToURI(org.w3c.dom.Node, String) throws org.w3c.dom.ls.LSException` |  |

---

## Package: `org.xml.sax`

### `interface AttributeList` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface Attributes`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getIndex(String, String)` |  |
| `int getIndex(String)` |  |
| `int getLength()` |  |
| `String getLocalName(int)` |  |
| `String getQName(int)` |  |
| `String getType(int)` |  |
| `String getType(String, String)` |  |
| `String getType(String)` |  |
| `String getURI(int)` |  |
| `String getValue(int)` |  |
| `String getValue(String, String)` |  |
| `String getValue(String)` |  |

---

### `interface ContentHandler`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void characters(char[], int, int) throws org.xml.sax.SAXException` |  |
| `void endDocument() throws org.xml.sax.SAXException` |  |
| `void endElement(String, String, String) throws org.xml.sax.SAXException` |  |
| `void endPrefixMapping(String) throws org.xml.sax.SAXException` |  |
| `void ignorableWhitespace(char[], int, int) throws org.xml.sax.SAXException` |  |
| `void processingInstruction(String, String) throws org.xml.sax.SAXException` |  |
| `void setDocumentLocator(org.xml.sax.Locator)` |  |
| `void skippedEntity(String) throws org.xml.sax.SAXException` |  |
| `void startDocument() throws org.xml.sax.SAXException` |  |
| `void startElement(String, String, String, org.xml.sax.Attributes) throws org.xml.sax.SAXException` |  |
| `void startPrefixMapping(String, String) throws org.xml.sax.SAXException` |  |

---

### `interface DTDHandler`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void notationDecl(String, String, String) throws org.xml.sax.SAXException` |  |
| `void unparsedEntityDecl(String, String, String, String) throws org.xml.sax.SAXException` |  |

---

### `interface DocumentHandler` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface EntityResolver`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.xml.sax.InputSource resolveEntity(String, String) throws java.io.IOException, org.xml.sax.SAXException` |  |

---

### `interface ErrorHandler`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void error(org.xml.sax.SAXParseException) throws org.xml.sax.SAXException` |  |
| `void fatalError(org.xml.sax.SAXParseException) throws org.xml.sax.SAXException` |  |
| `void warning(org.xml.sax.SAXParseException) throws org.xml.sax.SAXException` |  |

---

### `class HandlerBase` ~~DEPRECATED~~

- **实现：** `org.xml.sax.DTDHandler org.xml.sax.DocumentHandler org.xml.sax.EntityResolver org.xml.sax.ErrorHandler`
- **注解：** `@Deprecated`

---

### `class InputSource`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `InputSource()` |  |
| `InputSource(String)` |  |
| `InputSource(java.io.InputStream)` |  |
| `InputSource(java.io.Reader)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.io.InputStream getByteStream()` |  |
| `java.io.Reader getCharacterStream()` |  |
| `String getEncoding()` |  |
| `String getPublicId()` |  |
| `String getSystemId()` |  |
| `void setByteStream(java.io.InputStream)` |  |
| `void setCharacterStream(java.io.Reader)` |  |
| `void setEncoding(String)` |  |
| `void setPublicId(String)` |  |
| `void setSystemId(String)` |  |

---

### `interface Locator`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getColumnNumber()` |  |
| `int getLineNumber()` |  |
| `String getPublicId()` |  |
| `String getSystemId()` |  |

---

### `interface Parser` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class SAXException`

- **继承：** `java.lang.Exception`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SAXException()` |  |
| `SAXException(String)` |  |
| `SAXException(Exception)` |  |
| `SAXException(String, Exception)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `Exception getException()` |  |

---

### `class SAXNotRecognizedException`

- **继承：** `org.xml.sax.SAXException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SAXNotRecognizedException()` |  |
| `SAXNotRecognizedException(String)` |  |

---

### `class SAXNotSupportedException`

- **继承：** `org.xml.sax.SAXException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SAXNotSupportedException()` |  |
| `SAXNotSupportedException(String)` |  |

---

### `class SAXParseException`

- **继承：** `org.xml.sax.SAXException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SAXParseException(String, org.xml.sax.Locator)` |  |
| `SAXParseException(String, org.xml.sax.Locator, Exception)` |  |
| `SAXParseException(String, String, String, int, int)` |  |
| `SAXParseException(String, String, String, int, int, Exception)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getColumnNumber()` |  |
| `int getLineNumber()` |  |
| `String getPublicId()` |  |
| `String getSystemId()` |  |

---

### `interface XMLFilter`

- **继承：** `org.xml.sax.XMLReader`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.xml.sax.XMLReader getParent()` |  |
| `void setParent(org.xml.sax.XMLReader)` |  |

---

### `interface XMLReader`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.xml.sax.ContentHandler getContentHandler()` |  |
| `org.xml.sax.DTDHandler getDTDHandler()` |  |
| `org.xml.sax.EntityResolver getEntityResolver()` |  |
| `org.xml.sax.ErrorHandler getErrorHandler()` |  |
| `boolean getFeature(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `Object getProperty(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void parse(org.xml.sax.InputSource) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void setContentHandler(org.xml.sax.ContentHandler)` |  |
| `void setDTDHandler(org.xml.sax.DTDHandler)` |  |
| `void setEntityResolver(org.xml.sax.EntityResolver)` |  |
| `void setErrorHandler(org.xml.sax.ErrorHandler)` |  |
| `void setFeature(String, boolean) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void setProperty(String, Object) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |

---

## Package: `org.xml.sax.ext`

### `interface Attributes2`

- **继承：** `org.xml.sax.Attributes`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isDeclared(int)` |  |
| `boolean isDeclared(String)` |  |
| `boolean isDeclared(String, String)` |  |
| `boolean isSpecified(int)` |  |
| `boolean isSpecified(String, String)` |  |
| `boolean isSpecified(String)` |  |

---

### `class Attributes2Impl`

- **继承：** `org.xml.sax.helpers.AttributesImpl`
- **实现：** `org.xml.sax.ext.Attributes2`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Attributes2Impl()` |  |
| `Attributes2Impl(org.xml.sax.Attributes)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isDeclared(int)` |  |
| `boolean isDeclared(String, String)` |  |
| `boolean isDeclared(String)` |  |
| `boolean isSpecified(int)` |  |
| `boolean isSpecified(String, String)` |  |
| `boolean isSpecified(String)` |  |
| `void setDeclared(int, boolean)` |  |
| `void setSpecified(int, boolean)` |  |

---

### `interface DeclHandler`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void attributeDecl(String, String, String, String, String) throws org.xml.sax.SAXException` |  |
| `void elementDecl(String, String) throws org.xml.sax.SAXException` |  |
| `void externalEntityDecl(String, String, String) throws org.xml.sax.SAXException` |  |
| `void internalEntityDecl(String, String) throws org.xml.sax.SAXException` |  |

---

### `class DefaultHandler2`

- **继承：** `org.xml.sax.helpers.DefaultHandler`
- **实现：** `org.xml.sax.ext.DeclHandler org.xml.sax.ext.EntityResolver2 org.xml.sax.ext.LexicalHandler`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DefaultHandler2()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void attributeDecl(String, String, String, String, String) throws org.xml.sax.SAXException` |  |
| `void comment(char[], int, int) throws org.xml.sax.SAXException` |  |
| `void elementDecl(String, String) throws org.xml.sax.SAXException` |  |
| `void endCDATA() throws org.xml.sax.SAXException` |  |
| `void endDTD() throws org.xml.sax.SAXException` |  |
| `void endEntity(String) throws org.xml.sax.SAXException` |  |
| `void externalEntityDecl(String, String, String) throws org.xml.sax.SAXException` |  |
| `org.xml.sax.InputSource getExternalSubset(String, String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void internalEntityDecl(String, String) throws org.xml.sax.SAXException` |  |
| `org.xml.sax.InputSource resolveEntity(String, String, String, String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void startCDATA() throws org.xml.sax.SAXException` |  |
| `void startDTD(String, String, String) throws org.xml.sax.SAXException` |  |
| `void startEntity(String) throws org.xml.sax.SAXException` |  |

---

### `interface EntityResolver2`

- **继承：** `org.xml.sax.EntityResolver`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.xml.sax.InputSource getExternalSubset(String, String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `org.xml.sax.InputSource resolveEntity(String, String, String, String) throws java.io.IOException, org.xml.sax.SAXException` |  |

---

### `interface LexicalHandler`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void comment(char[], int, int) throws org.xml.sax.SAXException` |  |
| `void endCDATA() throws org.xml.sax.SAXException` |  |
| `void endDTD() throws org.xml.sax.SAXException` |  |
| `void endEntity(String) throws org.xml.sax.SAXException` |  |
| `void startCDATA() throws org.xml.sax.SAXException` |  |
| `void startDTD(String, String, String) throws org.xml.sax.SAXException` |  |
| `void startEntity(String) throws org.xml.sax.SAXException` |  |

---

### `interface Locator2`

- **继承：** `org.xml.sax.Locator`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getEncoding()` |  |
| `String getXMLVersion()` |  |

---

### `class Locator2Impl`

- **继承：** `org.xml.sax.helpers.LocatorImpl`
- **实现：** `org.xml.sax.ext.Locator2`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Locator2Impl()` |  |
| `Locator2Impl(org.xml.sax.Locator)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getEncoding()` |  |
| `String getXMLVersion()` |  |
| `void setEncoding(String)` |  |
| `void setXMLVersion(String)` |  |

---

## Package: `org.xml.sax.helpers`

### `class AttributeListImpl` ~~DEPRECATED~~

- **实现：** `org.xml.sax.AttributeList`
- **注解：** `@Deprecated`

---

### `class AttributesImpl`

- **实现：** `org.xml.sax.Attributes`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AttributesImpl()` |  |
| `AttributesImpl(org.xml.sax.Attributes)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addAttribute(String, String, String, String, String)` |  |
| `void clear()` |  |
| `int getIndex(String, String)` |  |
| `int getIndex(String)` |  |
| `int getLength()` |  |
| `String getLocalName(int)` |  |
| `String getQName(int)` |  |
| `String getType(int)` |  |
| `String getType(String, String)` |  |
| `String getType(String)` |  |
| `String getURI(int)` |  |
| `String getValue(int)` |  |
| `String getValue(String, String)` |  |
| `String getValue(String)` |  |
| `void removeAttribute(int)` |  |
| `void setAttribute(int, String, String, String, String, String)` |  |
| `void setAttributes(org.xml.sax.Attributes)` |  |
| `void setLocalName(int, String)` |  |
| `void setQName(int, String)` |  |
| `void setType(int, String)` |  |
| `void setURI(int, String)` |  |
| `void setValue(int, String)` |  |

---

### `class DefaultHandler`

- **实现：** `org.xml.sax.ContentHandler org.xml.sax.DTDHandler org.xml.sax.EntityResolver org.xml.sax.ErrorHandler`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DefaultHandler()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void characters(char[], int, int) throws org.xml.sax.SAXException` |  |
| `void endDocument() throws org.xml.sax.SAXException` |  |
| `void endElement(String, String, String) throws org.xml.sax.SAXException` |  |
| `void endPrefixMapping(String) throws org.xml.sax.SAXException` |  |
| `void error(org.xml.sax.SAXParseException) throws org.xml.sax.SAXException` |  |
| `void fatalError(org.xml.sax.SAXParseException) throws org.xml.sax.SAXException` |  |
| `void ignorableWhitespace(char[], int, int) throws org.xml.sax.SAXException` |  |
| `void notationDecl(String, String, String) throws org.xml.sax.SAXException` |  |
| `void processingInstruction(String, String) throws org.xml.sax.SAXException` |  |
| `org.xml.sax.InputSource resolveEntity(String, String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void setDocumentLocator(org.xml.sax.Locator)` |  |
| `void skippedEntity(String) throws org.xml.sax.SAXException` |  |
| `void startDocument() throws org.xml.sax.SAXException` |  |
| `void startElement(String, String, String, org.xml.sax.Attributes) throws org.xml.sax.SAXException` |  |
| `void startPrefixMapping(String, String) throws org.xml.sax.SAXException` |  |
| `void unparsedEntityDecl(String, String, String, String) throws org.xml.sax.SAXException` |  |
| `void warning(org.xml.sax.SAXParseException) throws org.xml.sax.SAXException` |  |

---

### `class LocatorImpl`

- **实现：** `org.xml.sax.Locator`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LocatorImpl()` |  |
| `LocatorImpl(org.xml.sax.Locator)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getColumnNumber()` |  |
| `int getLineNumber()` |  |
| `String getPublicId()` |  |
| `String getSystemId()` |  |
| `void setColumnNumber(int)` |  |
| `void setLineNumber(int)` |  |
| `void setPublicId(String)` |  |
| `void setSystemId(String)` |  |

---

### `class NamespaceSupport`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NamespaceSupport()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String NSDECL = "http://www.w3.org/xmlns/2000/"` |  |
| `static final String XMLNS = "http://www.w3.org/XML/1998/namespace"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean declarePrefix(String, String)` |  |
| `java.util.Enumeration getDeclaredPrefixes()` |  |
| `String getPrefix(String)` |  |
| `java.util.Enumeration getPrefixes()` |  |
| `java.util.Enumeration getPrefixes(String)` |  |
| `String getURI(String)` |  |
| `boolean isNamespaceDeclUris()` |  |
| `void popContext()` |  |
| `String[] processName(String, String[], boolean)` |  |
| `void pushContext()` |  |
| `void reset()` |  |
| `void setNamespaceDeclUris(boolean)` |  |

---

### `class ParserAdapter`

- **实现：** `org.xml.sax.DocumentHandler org.xml.sax.XMLReader`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ParserAdapter() throws org.xml.sax.SAXException` |  |
| `ParserAdapter(org.xml.sax.Parser)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void characters(char[], int, int) throws org.xml.sax.SAXException` |  |
| `void endDocument() throws org.xml.sax.SAXException` |  |
| `void endElement(String) throws org.xml.sax.SAXException` |  |
| `org.xml.sax.ContentHandler getContentHandler()` |  |
| `org.xml.sax.DTDHandler getDTDHandler()` |  |
| `org.xml.sax.EntityResolver getEntityResolver()` |  |
| `org.xml.sax.ErrorHandler getErrorHandler()` |  |
| `boolean getFeature(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `Object getProperty(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void ignorableWhitespace(char[], int, int) throws org.xml.sax.SAXException` |  |
| `void parse(String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(org.xml.sax.InputSource) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void processingInstruction(String, String) throws org.xml.sax.SAXException` |  |
| `void setContentHandler(org.xml.sax.ContentHandler)` |  |
| `void setDTDHandler(org.xml.sax.DTDHandler)` |  |
| `void setDocumentLocator(org.xml.sax.Locator)` |  |
| `void setEntityResolver(org.xml.sax.EntityResolver)` |  |
| `void setErrorHandler(org.xml.sax.ErrorHandler)` |  |
| `void setFeature(String, boolean) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void setProperty(String, Object) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void startDocument() throws org.xml.sax.SAXException` |  |
| `void startElement(String, org.xml.sax.AttributeList) throws org.xml.sax.SAXException` |  |

---

### `class ParserFactory` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class XMLFilterImpl`

- **实现：** `org.xml.sax.ContentHandler org.xml.sax.DTDHandler org.xml.sax.EntityResolver org.xml.sax.ErrorHandler org.xml.sax.XMLFilter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `XMLFilterImpl()` |  |
| `XMLFilterImpl(org.xml.sax.XMLReader)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void characters(char[], int, int) throws org.xml.sax.SAXException` |  |
| `void endDocument() throws org.xml.sax.SAXException` |  |
| `void endElement(String, String, String) throws org.xml.sax.SAXException` |  |
| `void endPrefixMapping(String) throws org.xml.sax.SAXException` |  |
| `void error(org.xml.sax.SAXParseException) throws org.xml.sax.SAXException` |  |
| `void fatalError(org.xml.sax.SAXParseException) throws org.xml.sax.SAXException` |  |
| `org.xml.sax.ContentHandler getContentHandler()` |  |
| `org.xml.sax.DTDHandler getDTDHandler()` |  |
| `org.xml.sax.EntityResolver getEntityResolver()` |  |
| `org.xml.sax.ErrorHandler getErrorHandler()` |  |
| `boolean getFeature(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `org.xml.sax.XMLReader getParent()` |  |
| `Object getProperty(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void ignorableWhitespace(char[], int, int) throws org.xml.sax.SAXException` |  |
| `void notationDecl(String, String, String) throws org.xml.sax.SAXException` |  |
| `void parse(org.xml.sax.InputSource) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void processingInstruction(String, String) throws org.xml.sax.SAXException` |  |
| `org.xml.sax.InputSource resolveEntity(String, String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void setContentHandler(org.xml.sax.ContentHandler)` |  |
| `void setDTDHandler(org.xml.sax.DTDHandler)` |  |
| `void setDocumentLocator(org.xml.sax.Locator)` |  |
| `void setEntityResolver(org.xml.sax.EntityResolver)` |  |
| `void setErrorHandler(org.xml.sax.ErrorHandler)` |  |
| `void setFeature(String, boolean) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void setParent(org.xml.sax.XMLReader)` |  |
| `void setProperty(String, Object) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void skippedEntity(String) throws org.xml.sax.SAXException` |  |
| `void startDocument() throws org.xml.sax.SAXException` |  |
| `void startElement(String, String, String, org.xml.sax.Attributes) throws org.xml.sax.SAXException` |  |
| `void startPrefixMapping(String, String) throws org.xml.sax.SAXException` |  |
| `void unparsedEntityDecl(String, String, String, String) throws org.xml.sax.SAXException` |  |
| `void warning(org.xml.sax.SAXParseException) throws org.xml.sax.SAXException` |  |

---

### `class XMLReaderAdapter`

- **实现：** `org.xml.sax.ContentHandler org.xml.sax.Parser`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `XMLReaderAdapter() throws org.xml.sax.SAXException` |  |
| `XMLReaderAdapter(org.xml.sax.XMLReader)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void characters(char[], int, int) throws org.xml.sax.SAXException` |  |
| `void endDocument() throws org.xml.sax.SAXException` |  |
| `void endElement(String, String, String) throws org.xml.sax.SAXException` |  |
| `void endPrefixMapping(String)` |  |
| `void ignorableWhitespace(char[], int, int) throws org.xml.sax.SAXException` |  |
| `void parse(String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(org.xml.sax.InputSource) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void processingInstruction(String, String) throws org.xml.sax.SAXException` |  |
| `void setDTDHandler(org.xml.sax.DTDHandler)` |  |
| `void setDocumentHandler(org.xml.sax.DocumentHandler)` |  |
| `void setDocumentLocator(org.xml.sax.Locator)` |  |
| `void setEntityResolver(org.xml.sax.EntityResolver)` |  |
| `void setErrorHandler(org.xml.sax.ErrorHandler)` |  |
| `void setLocale(java.util.Locale) throws org.xml.sax.SAXException` |  |
| `void skippedEntity(String) throws org.xml.sax.SAXException` |  |
| `void startDocument() throws org.xml.sax.SAXException` |  |
| `void startElement(String, String, String, org.xml.sax.Attributes) throws org.xml.sax.SAXException` |  |
| `void startPrefixMapping(String, String)` |  |

---

### `class final XMLReaderFactory`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static org.xml.sax.XMLReader createXMLReader() throws org.xml.sax.SAXException` |  |
| `static org.xml.sax.XMLReader createXMLReader(String) throws org.xml.sax.SAXException` |  |

---

## Package: `org.xmlpull.v1`

### `interface XmlPullParser`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CDSECT = 5` |  |
| `static final int COMMENT = 9` |  |
| `static final int DOCDECL = 10` |  |
| `static final int END_DOCUMENT = 1` |  |
| `static final int END_TAG = 3` |  |
| `static final int ENTITY_REF = 6` |  |
| `static final String FEATURE_PROCESS_DOCDECL = "http://xmlpull.org/v1/doc/features.html#process-docdecl"` |  |
| `static final String FEATURE_PROCESS_NAMESPACES = "http://xmlpull.org/v1/doc/features.html#process-namespaces"` |  |
| `static final String FEATURE_REPORT_NAMESPACE_ATTRIBUTES = "http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes"` |  |
| `static final String FEATURE_VALIDATION = "http://xmlpull.org/v1/doc/features.html#validation"` |  |
| `static final int IGNORABLE_WHITESPACE = 7` |  |
| `static final String NO_NAMESPACE = ""` |  |
| `static final int PROCESSING_INSTRUCTION = 8` |  |
| `static final int START_DOCUMENT = 0` |  |
| `static final int START_TAG = 2` |  |
| `static final int TEXT = 4` |  |
| `static final String[] TYPES` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void defineEntityReplacementText(String, String) throws org.xmlpull.v1.XmlPullParserException` |  |
| `int getAttributeCount()` |  |
| `String getAttributeName(int)` |  |
| `String getAttributeNamespace(int)` |  |
| `String getAttributePrefix(int)` |  |
| `String getAttributeType(int)` |  |
| `String getAttributeValue(int)` |  |
| `String getAttributeValue(String, String)` |  |
| `int getColumnNumber()` |  |
| `int getDepth()` |  |
| `int getEventType() throws org.xmlpull.v1.XmlPullParserException` |  |
| `boolean getFeature(String)` |  |
| `String getInputEncoding()` |  |
| `int getLineNumber()` |  |
| `String getName()` |  |
| `String getNamespace(String)` |  |
| `String getNamespace()` |  |
| `int getNamespaceCount(int) throws org.xmlpull.v1.XmlPullParserException` |  |
| `String getNamespacePrefix(int) throws org.xmlpull.v1.XmlPullParserException` |  |
| `String getNamespaceUri(int) throws org.xmlpull.v1.XmlPullParserException` |  |
| `String getPositionDescription()` |  |
| `String getPrefix()` |  |
| `Object getProperty(String)` |  |
| `String getText()` |  |
| `char[] getTextCharacters(int[])` |  |
| `boolean isAttributeDefault(int)` |  |
| `boolean isEmptyElementTag() throws org.xmlpull.v1.XmlPullParserException` |  |
| `boolean isWhitespace() throws org.xmlpull.v1.XmlPullParserException` |  |
| `int next() throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |
| `int nextTag() throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |
| `String nextText() throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |
| `int nextToken() throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |
| `void require(int, String, String) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |
| `void setFeature(String, boolean) throws org.xmlpull.v1.XmlPullParserException` |  |
| `void setInput(java.io.Reader) throws org.xmlpull.v1.XmlPullParserException` |  |
| `void setInput(java.io.InputStream, String) throws org.xmlpull.v1.XmlPullParserException` |  |
| `void setProperty(String, Object) throws org.xmlpull.v1.XmlPullParserException` |  |

---

### `class XmlPullParserException`

- **继承：** `java.lang.Exception`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `XmlPullParserException(String)` |  |
| `XmlPullParserException(String, org.xmlpull.v1.XmlPullParser, Throwable)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int column` |  |
| `Throwable detail` |  |
| `int row` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getColumnNumber()` |  |
| `Throwable getDetail()` |  |
| `int getLineNumber()` |  |

---

### `class XmlPullParserFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `XmlPullParserFactory()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String PROPERTY_NAME = "org.xmlpull.v1.XmlPullParserFactory"` |  |
| `String classNamesLocation` |  |
| `java.util.HashMap<java.lang.String,java.lang.Boolean> features` |  |
| `java.util.ArrayList parserClasses` |  |
| `java.util.ArrayList serializerClasses` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean getFeature(String)` |  |
| `boolean isNamespaceAware()` |  |
| `boolean isValidating()` |  |
| `static org.xmlpull.v1.XmlPullParserFactory newInstance() throws org.xmlpull.v1.XmlPullParserException` |  |
| `static org.xmlpull.v1.XmlPullParserFactory newInstance(String, Class) throws org.xmlpull.v1.XmlPullParserException` |  |
| `org.xmlpull.v1.XmlPullParser newPullParser() throws org.xmlpull.v1.XmlPullParserException` |  |
| `org.xmlpull.v1.XmlSerializer newSerializer() throws org.xmlpull.v1.XmlPullParserException` |  |
| `void setFeature(String, boolean) throws org.xmlpull.v1.XmlPullParserException` |  |
| `void setNamespaceAware(boolean)` |  |
| `void setValidating(boolean)` |  |

---

### `interface XmlSerializer`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `org.xmlpull.v1.XmlSerializer attribute(String, String, String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void cdsect(String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void comment(String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void docdecl(String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void endDocument() throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `org.xmlpull.v1.XmlSerializer endTag(String, String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void entityRef(String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void flush() throws java.io.IOException` |  |
| `int getDepth()` |  |
| `boolean getFeature(String)` |  |
| `String getName()` |  |
| `String getNamespace()` |  |
| `String getPrefix(String, boolean) throws java.lang.IllegalArgumentException` |  |
| `Object getProperty(String)` |  |
| `void ignorableWhitespace(String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void processingInstruction(String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void setFeature(String, boolean) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void setOutput(java.io.OutputStream, String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void setOutput(java.io.Writer) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void setPrefix(String, String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void setProperty(String, Object) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void startDocument(String, Boolean) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `org.xmlpull.v1.XmlSerializer startTag(String, String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `org.xmlpull.v1.XmlSerializer text(String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `org.xmlpull.v1.XmlSerializer text(char[], int, int) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |

---

## Package: `org.xmlpull.v1.sax2`

### `class Driver`

- **实现：** `org.xml.sax.Attributes org.xml.sax.Locator org.xml.sax.XMLReader`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Driver() throws org.xmlpull.v1.XmlPullParserException` |  |
| `Driver(org.xmlpull.v1.XmlPullParser) throws org.xmlpull.v1.XmlPullParserException` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String APACHE_DYNAMIC_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/dynamic"` |  |
| `static final String APACHE_SCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema"` |  |
| `static final String DECLARATION_HANDLER_PROPERTY = "http://xml.org/sax/properties/declaration-handler"` |  |
| `static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler"` |  |
| `static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces"` |  |
| `static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes"` |  |
| `static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation"` |  |
| `org.xml.sax.ContentHandler contentHandler` |  |
| `org.xml.sax.ErrorHandler errorHandler` |  |
| `org.xmlpull.v1.XmlPullParser pp` |  |
| `String systemId` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getColumnNumber()` |  |
| `org.xml.sax.ContentHandler getContentHandler()` |  |
| `org.xml.sax.DTDHandler getDTDHandler()` |  |
| `org.xml.sax.EntityResolver getEntityResolver()` |  |
| `org.xml.sax.ErrorHandler getErrorHandler()` |  |
| `boolean getFeature(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `int getIndex(String, String)` |  |
| `int getIndex(String)` |  |
| `int getLength()` |  |
| `int getLineNumber()` |  |
| `String getLocalName(int)` |  |
| `Object getProperty(String) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `String getPublicId()` |  |
| `String getQName(int)` |  |
| `String getSystemId()` |  |
| `String getType(int)` |  |
| `String getType(String, String)` |  |
| `String getType(String)` |  |
| `String getURI(int)` |  |
| `String getValue(int)` |  |
| `String getValue(String, String)` |  |
| `String getValue(String)` |  |
| `void parse(org.xml.sax.InputSource) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parse(String) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void parseSubTree(org.xmlpull.v1.XmlPullParser) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `void setContentHandler(org.xml.sax.ContentHandler)` |  |
| `void setDTDHandler(org.xml.sax.DTDHandler)` |  |
| `void setEntityResolver(org.xml.sax.EntityResolver)` |  |
| `void setErrorHandler(org.xml.sax.ErrorHandler)` |  |
| `void setFeature(String, boolean) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void setProperty(String, Object) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException` |  |
| `void startElement(String, String, String) throws org.xml.sax.SAXException` |  |

---

