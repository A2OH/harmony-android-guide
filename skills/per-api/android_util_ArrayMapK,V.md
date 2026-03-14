# SKILL: android.util.ArrayMap<K, V>

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.ArrayMap<K, V>`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.ArrayMap<K, V>` |
| **Package** | `android.util` |
| **Total Methods** | 26 |
| **Avg Score** | 3.9 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 5 (19%) |
| **Partial/Composite** | 14 (53%) |
| **No Mapping** | 7 (26%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `remove` | `V remove(Object)` | 9 | direct | hard | `remove` | `@ohos.util.LightWeightMap.LightWeightMap` |
| `containsKey` | `boolean containsKey(Object)` | 7 | near | hard | `hasKey` | `@ohos.util.LightWeightMap.LightWeightMap` |
| `get` | `V get(Object)` | 7 | near | hard | `get` | `@ohos.util.LightWeightMap.LightWeightMap` |
| `put` | `V put(K, V)` | 7 | near | impossible | `set` | `@ohos.util.LightWeightMap.LightWeightMap` |
| `size` | `int size()` | 7 | near | moderate | `length` | `@ohos.util.LightWeightMap.LightWeightMap` |
| `clear` | `void clear()` | 6 | partial | moderate | `clear` | `clear(): void` |
| `isEmpty` | `boolean isEmpty()` | 5 | partial | moderate | `isKeepData` | `isKeepData: boolean` |

## Gap Descriptions (per method)

- **`remove`**: Direct
- **`containsKey`**: Name: hasKey
- **`get`**: Direct
- **`put`**: Name: set vs put
- **`size`**: Property

## Stub APIs (score < 5): 19 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `valueAt` | 5 | partial | throw UnsupportedOperationException |
| `indexOfKey` | 5 | partial | throw UnsupportedOperationException |
| `keyAt` | 5 | partial | throw UnsupportedOperationException |
| `indexOfValue` | 4 | partial | throw UnsupportedOperationException |
| `keySet` | 4 | partial | Log warning + no-op |
| `containsAll` | 4 | composite | Store callback, never fire |
| `containsValue` | 4 | composite | Store callback, never fire |
| `removeAll` | 3 | composite | Log warning + no-op |
| `setValueAt` | 3 | composite | Log warning + no-op |
| `removeAt` | 3 | composite | Log warning + no-op |
| `entrySet` | 3 | composite | Log warning + no-op |
| `retainAll` | 2 | composite | throw UnsupportedOperationException |
| `ArrayMap` | 1 | none | throw UnsupportedOperationException |
| `ArrayMap` | 1 | none | throw UnsupportedOperationException |
| `ArrayMap` | 1 | none | throw UnsupportedOperationException |
| `ensureCapacity` | 1 | none | throw UnsupportedOperationException |
| `putAll` | 1 | none | Log warning + no-op |
| `putAll` | 1 | none | Log warning + no-op |
| `values` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.util.ArrayMap<K, V>`:


## Quality Gates

Before marking `android.util.ArrayMap<K, V>` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 26 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
