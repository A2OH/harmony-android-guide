# SKILL: android.util.ArraySet<E>

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.ArraySet<E>`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.ArraySet<E>` |
| **Package** | `android.util` |
| **Total Methods** | 24 |
| **Avg Score** | 3.5 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 20 (83%) |
| **No Mapping** | 4 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `size` | `int size()` | 6 | partial | moderate | `size` | `size: number` |
| `clear` | `void clear()` | 6 | partial | moderate | `clear` | `clear(): void` |
| `indexOf` | `int indexOf(Object)` | 6 | partial | moderate | `index` | `index: number` |
| `addAll` | `void addAll(android.util.ArraySet<? extends E>)` | 5 | partial | moderate | `add` | `add: (bundleName: string, userId: number) => void` |
| `isEmpty` | `boolean isEmpty()` | 5 | partial | moderate | `isKeepData` | `isKeepData: boolean` |
| `add` | `boolean add(E)` | 5 | partial | moderate | `add` | `add: (bundleName: string, userId: number) => void` |

## Stub APIs (score < 5): 18 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `valueAt` | 5 | partial | throw UnsupportedOperationException |
| `addAll` | 4 | partial | Log warning + no-op |
| `contains` | 4 | partial | Store callback, never fire |
| `containsAll` | 4 | composite | Store callback, never fire |
| `remove` | 3 | composite | Log warning + no-op |
| `removeAll` | 3 | composite | Log warning + no-op |
| `removeAll` | 3 | composite | Log warning + no-op |
| `removeAt` | 3 | composite | Log warning + no-op |
| `ArraySet` | 3 | composite | Log warning + no-op |
| `ArraySet` | 3 | composite | Log warning + no-op |
| `ArraySet` | 3 | composite | Log warning + no-op |
| `ArraySet` | 3 | composite | Log warning + no-op |
| `ArraySet` | 3 | composite | Log warning + no-op |
| `retainAll` | 2 | composite | throw UnsupportedOperationException |
| `ensureCapacity` | 1 | none | throw UnsupportedOperationException |
| `iterator` | 1 | none | throw UnsupportedOperationException |
| `toArray` | 1 | none | throw UnsupportedOperationException |
| `toArray` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.util.ArraySet<E>`:


## Quality Gates

Before marking `android.util.ArraySet<E>` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 24 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
