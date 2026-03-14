# SKILL: android.os.Parcel

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Parcel`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Parcel` |
| **Package** | `android.os` |
| **Total Methods** | 83 |
| **Avg Score** | 7.1 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 58 (69%) |
| **Partial/Composite** | 23 (27%) |
| **No Mapping** | 2 (2%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 3 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 62 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `dataCapacity` | `int dataCapacity()` | 9 | direct | impossible | `getCapacity` | `@ohos.rpc.MessageSequence` |
| `dataSize` | `int dataSize()` | 9 | direct | moderate | `getSize` | `@ohos.rpc.MessageSequence` |
| `hasFileDescriptors` | `boolean hasFileDescriptors()` | 9 | direct | rewrite | `containFileDescriptors` | `@ohos.rpc.MessageSequence` |
| `readBoolean` | `boolean readBoolean()` | 9 | direct | hard | `readBoolean` | `@ohos.rpc.MessageSequence` |
| `readBooleanArray` | `void readBooleanArray(@NonNull boolean[])` | 9 | direct | impossible | `readBooleanArray` | `@ohos.rpc.MessageSequence` |
| `readByte` | `byte readByte()` | 9 | direct | rewrite | `readByte` | `@ohos.rpc.MessageSequence` |
| `readByteArray` | `void readByteArray(@NonNull byte[])` | 9 | direct | rewrite | `readByteArray` | `@ohos.rpc.MessageSequence` |
| `readCharArray` | `void readCharArray(@NonNull char[])` | 9 | direct | impossible | `readCharArray` | `@ohos.rpc.MessageSequence` |
| `readDouble` | `double readDouble()` | 9 | direct | hard | `readDouble` | `@ohos.rpc.MessageSequence` |
| `readDoubleArray` | `void readDoubleArray(@NonNull double[])` | 9 | direct | hard | `readDoubleArray` | `@ohos.rpc.MessageSequence` |
| `readException` | `void readException()` | 9 | direct | rewrite | `readException` | `@ohos.rpc.MessageSequence` |
| `readException` | `void readException(int, String)` | 9 | direct | rewrite | `readException` | `@ohos.rpc.MessageSequence` |
| `readFileDescriptor` | `android.os.ParcelFileDescriptor readFileDescriptor()` | 9 | direct | hard | `readFileDescriptor` | `@ohos.rpc.MessageSequence` |
| `readFloat` | `float readFloat()` | 9 | direct | hard | `readFloat` | `@ohos.rpc.MessageSequence` |
| `readFloatArray` | `void readFloatArray(@NonNull float[])` | 9 | direct | hard | `readFloatArray` | `@ohos.rpc.MessageSequence` |
| `readInt` | `int readInt()` | 9 | direct | moderate | `readInt` | `@ohos.rpc.MessageSequence` |
| `readIntArray` | `void readIntArray(@NonNull int[])` | 9 | direct | hard | `readIntArray` | `@ohos.rpc.MessageSequence` |
| `readLong` | `long readLong()` | 9 | direct | moderate | `readLong` | `@ohos.rpc.MessageSequence` |
| `readLongArray` | `void readLongArray(@NonNull long[])` | 9 | direct | impossible | `readLongArray` | `@ohos.rpc.MessageSequence` |
| `readStringArray` | `void readStringArray(@NonNull String[])` | 9 | direct | rewrite | `readStringArray` | `@ohos.rpc.MessageSequence` |
| `recycle` | `void recycle()` | 9 | direct | impossible | `reclaim` | `@ohos.rpc.MessageSequence` |
| `setDataCapacity` | `void setDataCapacity(int)` | 9 | direct | hard | `setCapacity` | `@ohos.rpc.MessageSequence` |
| `setDataSize` | `void setDataSize(int)` | 9 | direct | hard | `setSize` | `@ohos.rpc.MessageSequence` |
| `writeBoolean` | `void writeBoolean(boolean)` | 9 | direct | hard | `writeBoolean` | `@ohos.rpc.MessageSequence` |
| `writeBooleanArray` | `void writeBooleanArray(@Nullable boolean[])` | 9 | direct | impossible | `writeBooleanArray` | `@ohos.rpc.MessageSequence` |
| `writeByte` | `void writeByte(byte)` | 9 | direct | hard | `writeByte` | `@ohos.rpc.MessageSequence` |
| `writeByteArray` | `void writeByteArray(@Nullable byte[])` | 9 | direct | hard | `writeByteArray` | `@ohos.rpc.MessageSequence` |
| `writeByteArray` | `void writeByteArray(@Nullable byte[], int, int)` | 9 | direct | hard | `writeByteArray` | `@ohos.rpc.MessageSequence` |
| `writeCharArray` | `void writeCharArray(@Nullable char[])` | 9 | direct | hard | `writeCharArray` | `@ohos.rpc.MessageSequence` |
| `writeDouble` | `void writeDouble(double)` | 9 | direct | hard | `writeDouble` | `@ohos.rpc.MessageSequence` |
| `writeDoubleArray` | `void writeDoubleArray(@Nullable double[])` | 9 | direct | hard | `writeDoubleArray` | `@ohos.rpc.MessageSequence` |
| `writeFileDescriptor` | `void writeFileDescriptor(@NonNull java.io.FileDescriptor)` | 9 | direct | hard | `writeFileDescriptor` | `@ohos.rpc.MessageSequence` |
| `writeFloat` | `void writeFloat(float)` | 9 | direct | hard | `writeFloat` | `@ohos.rpc.MessageSequence` |
| `writeFloatArray` | `void writeFloatArray(@Nullable float[])` | 9 | direct | hard | `writeFloatArray` | `@ohos.rpc.MessageSequence` |
| `writeInt` | `void writeInt(int)` | 9 | direct | hard | `writeInt` | `@ohos.rpc.MessageSequence` |
| `writeIntArray` | `void writeIntArray(@Nullable int[])` | 9 | direct | hard | `writeIntArray` | `@ohos.rpc.MessageSequence` |
| `writeInterfaceToken` | `void writeInterfaceToken(String)` | 9 | direct | rewrite | `writeInterfaceToken` | `@ohos.rpc.MessageSequence` |
| `writeLong` | `void writeLong(long)` | 9 | direct | hard | `writeLong` | `@ohos.rpc.MessageSequence` |
| `writeLongArray` | `void writeLongArray(@Nullable long[])` | 9 | direct | hard | `writeLongArray` | `@ohos.rpc.MessageSequence` |
| `writeNoException` | `void writeNoException()` | 9 | direct | hard | `writeNoException` | `@ohos.rpc.MessageSequence` |
| `writeString` | `void writeString(@Nullable String)` | 9 | direct | rewrite | `writeString` | `@ohos.rpc.MessageSequence` |
| `writeStringArray` | `void writeStringArray(@Nullable String[])` | 9 | direct | rewrite | `writeStringArray` | `@ohos.rpc.MessageSequence` |
| `dataAvail` | `int dataAvail()` | 8 | direct | hard | `getReadableBytes` | `@ohos.rpc.MessageSequence` |
| `dataPosition` | `int dataPosition()` | 8 | direct | impossible | `getReadPosition` | `@ohos.rpc.MessageSequence` |
| `enforceInterface` | `void enforceInterface(String)` | 8 | direct | rewrite | `readInterfaceToken` | `@ohos.rpc.MessageSequence` |
| `readStrongBinder` | `android.os.IBinder readStrongBinder()` | 8 | direct | hard | `readRemoteObject` | `@ohos.rpc.MessageSequence` |
| `writeParcelable` | `void writeParcelable(@Nullable android.os.Parcelable, int)` | 8 | direct | impossible | `writeParcelable` | `@ohos.rpc.MessageSequence` |
| `writeParcelableArray` | `<T extends android.os.Parcelable> void writeParcelableArray(@Nullable T[], int)` | 8 | direct | impossible | `writeParcelableArray` | `@ohos.rpc.MessageSequence` |
| `writeStrongBinder` | `void writeStrongBinder(android.os.IBinder)` | 8 | direct | hard | `writeRemoteObject` | `@ohos.rpc.MessageSequence` |
| `readBinderList` | `void readBinderList(@NonNull java.util.List<android.os.IBinder>)` | 7 | near | hard | `readRemoteObjectArray` | `@ohos.rpc.MessageSequence` |
| `readTypedArray` | `<T> void readTypedArray(@NonNull T[], @NonNull android.os.Parcelable.Creator<T>)` | 7 | near | rewrite | `readParcelableArray` | `@ohos.rpc.MessageSequence` |
| `setDataPosition` | `void setDataPosition(int)` | 7 | near | hard | `rewindRead` | `@ohos.rpc.MessageSequence` |
| `writeBinderList` | `void writeBinderList(@Nullable java.util.List<android.os.IBinder>)` | 7 | near | hard | `writeRemoteObjectArray` | `@ohos.rpc.MessageSequence` |
| `writeStrongInterface` | `void writeStrongInterface(android.os.IInterface)` | 7 | near | rewrite | `writeRemoteObject` | `@ohos.rpc.MessageSequence` |
| `writeTypedArray` | `<T extends android.os.Parcelable> void writeTypedArray(@Nullable T[], int)` | 7 | near | hard | `writeParcelableArray` | `@ohos.rpc.MessageSequence` |
| `writeTypedObject` | `<T extends android.os.Parcelable> void writeTypedObject(@Nullable T, int)` | 7 | near | rewrite | `writeParcelable` | `@ohos.rpc.MessageSequence` |
| `readTypedList` | `<T> void readTypedList(@NonNull java.util.List<T>, @NonNull android.os.Parcelable.Creator<T>)` | 6 | near | rewrite | `readParcelableArray` | `@ohos.rpc.MessageSequence` |
| `writeTypedList` | `<T extends android.os.Parcelable> void writeTypedList(@Nullable java.util.List<T>)` | 6 | near | hard | `writeParcelableArray` | `@ohos.rpc.MessageSequence` |
| `marshall` | `byte[] marshall()` | 5 | partial | impossible | `writeRawData` | `@ohos.rpc.MessageSequence` |
| `unmarshall` | `void unmarshall(@NonNull byte[], int, int)` | 5 | partial | impossible | `readRawData` | `@ohos.rpc.MessageSequence` |
| `writeSize` | `void writeSize(@NonNull android.util.Size)` | 5 | partial | hard | `write` | `@ohos.rpc.MessageSequence` |
| `writeSizeF` | `void writeSizeF(@NonNull android.util.SizeF)` | 5 | partial | hard | `write` | `@ohos.rpc.MessageSequence` |

## Gap Descriptions (per method)

- **`dataCapacity`**: Direct mapping
- **`dataSize`**: Direct mapping
- **`hasFileDescriptors`**: Direct mapping
- **`readBoolean`**: Direct mapping
- **`readBooleanArray`**: Direct mapping
- **`readByte`**: Direct mapping
- **`readByteArray`**: Direct mapping
- **`readCharArray`**: Direct mapping
- **`readDouble`**: Direct mapping
- **`readDoubleArray`**: Direct mapping
- **`readException`**: Direct mapping
- **`readException`**: Direct mapping
- **`readFileDescriptor`**: Direct mapping
- **`readFloat`**: Direct mapping
- **`readFloatArray`**: Direct mapping
- **`readInt`**: Direct mapping
- **`readIntArray`**: Direct mapping
- **`readLong`**: Direct mapping
- **`readLongArray`**: Direct mapping
- **`readStringArray`**: Direct mapping
- **`recycle`**: Direct mapping
- **`setDataCapacity`**: Direct mapping
- **`setDataSize`**: Direct mapping
- **`writeBoolean`**: Direct mapping
- **`writeBooleanArray`**: Direct mapping
- **`writeByte`**: Direct mapping
- **`writeByteArray`**: Direct mapping
- **`writeByteArray`**: Direct mapping
- **`writeCharArray`**: Direct mapping
- **`writeDouble`**: Direct mapping
- **`writeDoubleArray`**: Direct mapping
- **`writeFileDescriptor`**: Direct mapping
- **`writeFloat`**: Direct mapping
- **`writeFloatArray`**: Direct mapping
- **`writeInt`**: Direct mapping
- **`writeIntArray`**: Direct mapping
- **`writeInterfaceToken`**: Direct mapping
- **`writeLong`**: Direct mapping
- **`writeLongArray`**: Direct mapping
- **`writeNoException`**: Direct mapping
- **`writeString`**: Direct mapping
- **`writeStringArray`**: Direct mapping
- **`dataAvail`**: Approximate: readable bytes remaining
- **`dataPosition`**: Read position; OH separates read/write positions
- **`enforceInterface`**: Read and verify token; OH uses readInterfaceToken
- **`readStrongBinder`**: Binder→RemoteObject; conceptual match
- **`writeParcelable`**: OH uses Sequenceable interface instead of Parcelable
- **`writeParcelableArray`**: OH uses Sequenceable arrays
- **`writeStrongBinder`**: Binder→RemoteObject; conceptual match
- **`readBinderList`**: Read list of RemoteObjects
- **`readTypedArray`**: Read as Sequenceable array
- **`setDataPosition`**: OH uses rewindRead/rewindWrite; no arbitrary position set
- **`writeBinderList`**: Write list of RemoteObjects
- **`writeStrongInterface`**: Write IInterface as RemoteObject
- **`writeTypedArray`**: Write as Sequenceable array
- **`writeTypedObject`**: Write single Sequenceable
- **`readTypedList`**: Read as Sequenceable array
- **`writeTypedList`**: Write as Sequenceable array
- **`marshall`**: Approximate: serialize to raw bytes
- **`unmarshall`**: Approximate: deserialize from raw bytes
- **`writeSize`**: Write width+height as two ints
- **`writeSizeF`**: Write width+height as two floats

## Stub APIs (score < 5): 21 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeTypedArrayMap` | 5 | partial | Log warning + no-op |
| `writeParcelableList` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeException` | 4 | partial | Log warning + no-op |
| `readBinderArray` | 4 | partial | Return safe default (null/false/0/empty) |
| `readStringList` | 4 | partial | Return safe default (null/false/0/empty) |
| `readList` | 4 | partial | Return safe default (null/false/0/empty) |
| `readMap` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeBundle` | 4 | partial | Log warning + no-op |
| `writeList` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeMap` | 4 | partial | Log warning + no-op |
| `writePersistableBundle` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeValue` | 4 | partial | Log warning + no-op |
| `writeArray` | 3 | composite | Log warning + no-op |
| `writeBinderArray` | 3 | composite | Log warning + no-op |
| `appendFrom` | 3 | composite | throw UnsupportedOperationException |
| `writeSerializable` | 3 | composite | Log warning + no-op |
| `writeSparseArray` | 3 | composite | Log warning + no-op |
| `writeSparseBooleanArray` | 3 | composite | Log warning + no-op |
| `writeStringList` | 3 | composite | Return safe default (null/false/0/empty) |
| `writeParcelableCreator` | 1 | none | Log warning + no-op |
| `writeTypedSparseArray` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Parcel`:

- `android.os.Bundle` (already shimmed)

## Quality Gates

Before marking `android.os.Parcel` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 83 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 62 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
