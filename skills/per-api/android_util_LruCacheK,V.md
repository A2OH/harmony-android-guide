# SKILL: android.util.LruCache<K, V>

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.LruCache<K, V>`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.LruCache<K, V>` |
| **Package** | `android.util` |
| **Total Methods** | 19 |
| **Avg Score** | 3.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 15 (78%) |
| **No Mapping** | 4 (21%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `size` | `final int size()` | 6 | partial | moderate | `size` | `size: number` |
| `sizeOf` | `int sizeOf(K, V)` | 6 | partial | moderate | `size` | `size: number` |
| `hitCount` | `final int hitCount()` | 5 | partial | moderate | `count` | `readonly count: number` |
| `putCount` | `final int putCount()` | 5 | partial | moderate | `count` | `readonly count: number` |
| `missCount` | `final int missCount()` | 5 | partial | moderate | `count` | `readonly count: number` |

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `maxSize` | 5 | partial | throw UnsupportedOperationException |
| `trimToSize` | 5 | partial | throw UnsupportedOperationException |
| `entryRemoved` | 4 | partial | Log warning + no-op |
| `toString` | 4 | composite | throw UnsupportedOperationException |
| `create` | 3 | composite | Return dummy instance / no-op |
| `get` | 3 | composite | Return safe default (null/false/0/empty) |
| `remove` | 3 | composite | Log warning + no-op |
| `evictAll` | 3 | composite | throw UnsupportedOperationException |
| `createCount` | 3 | composite | Return dummy instance / no-op |
| `evictionCount` | 3 | composite | Store callback, never fire |
| `LruCache` | 1 | none | throw UnsupportedOperationException |
| `put` | 1 | none | Log warning + no-op |
| `resize` | 1 | none | throw UnsupportedOperationException |
| `snapshot` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.util.LruCache<K, V>`:


## Quality Gates

Before marking `android.util.LruCache<K, V>` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 19 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
