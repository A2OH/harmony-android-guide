# SKILL: android.os.Parcel

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Parcel`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Parcel` |
| **Package** | `android.os` |
| **Total Methods** | 83 |
| **Avg Score** | 3.2 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 69 (83%) |
| **No Mapping** | 14 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `readInt` | `int readInt()` | 5 | partial | moderate | `read` | `read(): Promise<number[]>` |
| `dataSize` | `int dataSize()` | 5 | partial | moderate | `dataSize` | `dataSize: number` |
| `readLong` | `long readLong()` | 5 | partial | moderate | `read` | `read(): Promise<number[]>` |

## Stub APIs (score < 5): 80 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `readFloat` | 5 | partial | Return safe default (null/false/0/empty) |
| `writeTypedList` | 5 | partial | Return safe default (null/false/0/empty) |
| `writeTypedArrayMap` | 5 | partial | Log warning + no-op |
| `writeTypedArray` | 4 | partial | Log warning + no-op |
| `writeParcelableList` | 4 | partial | Return safe default (null/false/0/empty) |
| `dataAvail` | 4 | partial | throw UnsupportedOperationException |
| `readMap` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeException` | 4 | partial | Log warning + no-op |
| `writePersistableBundle` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeNoException` | 4 | partial | Log warning + no-op |
| `readList` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeSerializable` | 4 | partial | Log warning + no-op |
| `readBinderList` | 4 | partial | Return safe default (null/false/0/empty) |
| `readBinderArray` | 4 | partial | Return safe default (null/false/0/empty) |
| `readIntArray` | 4 | partial | Return safe default (null/false/0/empty) |
| `readDouble` | 4 | partial | Return safe default (null/false/0/empty) |
| `readFloatArray` | 4 | partial | Return safe default (null/false/0/empty) |
| `readStringList` | 4 | partial | Return safe default (null/false/0/empty) |
| `setDataCapacity` | 4 | partial | Log warning + no-op |
| `readBoolean` | 4 | partial | Return safe default (null/false/0/empty) |
| `readDoubleArray` | 4 | partial | Return safe default (null/false/0/empty) |
| `readStrongBinder` | 4 | composite | Return safe default (null/false/0/empty) |
| `setDataSize` | 4 | composite | Log warning + no-op |
| `writeInt` | 4 | composite | Log warning + no-op |
| `writeMap` | 4 | composite | Log warning + no-op |
| `writeByte` | 4 | composite | Log warning + no-op |
| `writeList` | 4 | composite | Return safe default (null/false/0/empty) |
| `writeLong` | 4 | composite | Log warning + no-op |
| `writeSize` | 4 | composite | Log warning + no-op |
| `writeBundle` | 4 | composite | Log warning + no-op |
| `writeArray` | 3 | composite | Log warning + no-op |
| `writeBinderList` | 3 | composite | Return safe default (null/false/0/empty) |
| `writeFloat` | 3 | composite | Log warning + no-op |
| `writeSizeF` | 3 | composite | Log warning + no-op |
| `writeValue` | 3 | composite | Log warning + no-op |
| `writeBinderArray` | 3 | composite | Log warning + no-op |
| `writeIntArray` | 3 | composite | Log warning + no-op |
| `writeDouble` | 3 | composite | Log warning + no-op |
| `writeFloatArray` | 3 | composite | Log warning + no-op |
| `writeBoolean` | 3 | composite | Log warning + no-op |
| `setDataPosition` | 3 | composite | Log warning + no-op |
| `writeDoubleArray` | 3 | composite | Log warning + no-op |
| `writeStrongBinder` | 3 | composite | Log warning + no-op |
| `writeByteArray` | 3 | composite | Log warning + no-op |
| `writeByteArray` | 3 | composite | Log warning + no-op |
| `writeLongArray` | 3 | composite | Log warning + no-op |
| `writeCharArray` | 3 | composite | Log warning + no-op |
| `readFileDescriptor` | 3 | composite | Return safe default (null/false/0/empty) |
| `writeFileDescriptor` | 3 | composite | Log warning + no-op |
| `readTypedList` | 3 | composite | Return safe default (null/false/0/empty) |
| `hasFileDescriptors` | 3 | composite | Return safe default (null/false/0/empty) |
| `readByte` | 3 | composite | Return safe default (null/false/0/empty) |
| `enforceInterface` | 3 | composite | throw UnsupportedOperationException |
| `writeSparseArray` | 3 | composite | Log warning + no-op |
| `writeTypedObject` | 3 | composite | Log warning + no-op |
| `writeString` | 3 | composite | Log warning + no-op |
| `readTypedArray` | 3 | composite | Return safe default (null/false/0/empty) |
| `readException` | 3 | composite | Return safe default (null/false/0/empty) |
| `readException` | 3 | composite | Return safe default (null/false/0/empty) |
| `writeStrongInterface` | 3 | composite | Log warning + no-op |
| `writeInterfaceToken` | 3 | composite | Log warning + no-op |
| `writeStringList` | 3 | composite | Return safe default (null/false/0/empty) |
| `appendFrom` | 2 | composite | throw UnsupportedOperationException |
| `readByteArray` | 2 | composite | Return safe default (null/false/0/empty) |
| `writeStringArray` | 2 | composite | Log warning + no-op |
| `readStringArray` | 2 | composite | Return safe default (null/false/0/empty) |
| `dataCapacity` | 1 | none | throw UnsupportedOperationException |
| `dataPosition` | 1 | none | Store callback, never fire |
| `marshall` | 1 | none | throw UnsupportedOperationException |
| `readBooleanArray` | 1 | none | Return safe default (null/false/0/empty) |
| `readCharArray` | 1 | none | Return safe default (null/false/0/empty) |
| `readLongArray` | 1 | none | Return safe default (null/false/0/empty) |
| `recycle` | 1 | none | throw UnsupportedOperationException |
| `unmarshall` | 1 | none | throw UnsupportedOperationException |
| `writeBooleanArray` | 1 | none | Log warning + no-op |
| `writeParcelable` | 1 | none | Log warning + no-op |
| `writeParcelableArray` | 1 | none | Log warning + no-op |
| `writeParcelableCreator` | 1 | none | Log warning + no-op |
| `writeSparseBooleanArray` | 1 | none | Log warning + no-op |
| `writeTypedSparseArray` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Parcel`:

- `android.os.Bundle` (already shimmed)

## Quality Gates

Before marking `android.os.Parcel` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 83 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
