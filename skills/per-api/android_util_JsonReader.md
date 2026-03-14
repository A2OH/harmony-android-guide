# SKILL: android.util.JsonReader

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.JsonReader`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.JsonReader` |
| **Package** | `android.util` |
| **Total Methods** | 18 |
| **Avg Score** | 2.4 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 10 (55%) |
| **No Mapping** | 8 (44%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `nextName` | `String nextName() throws java.io.IOException` | 5 | partial | moderate | `bundleName` | `bundleName?: string` |

## Stub APIs (score < 5): 17 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `skipValue` | 5 | partial | throw UnsupportedOperationException |
| `close` | 4 | partial | No-op |
| `nextString` | 4 | composite | throw UnsupportedOperationException |
| `isLenient` | 3 | composite | Return safe default (null/false/0/empty) |
| `setLenient` | 3 | composite | Log warning + no-op |
| `endArray` | 3 | composite | throw UnsupportedOperationException |
| `hasNext` | 3 | composite | Return safe default (null/false/0/empty) |
| `beginArray` | 3 | composite | throw UnsupportedOperationException |
| `beginObject` | 3 | composite | throw UnsupportedOperationException |
| `JsonReader` | 1 | none | Return safe default (null/false/0/empty) |
| `endObject` | 1 | none | throw UnsupportedOperationException |
| `nextBoolean` | 1 | none | throw UnsupportedOperationException |
| `nextDouble` | 1 | none | throw UnsupportedOperationException |
| `nextInt` | 1 | none | throw UnsupportedOperationException |
| `nextLong` | 1 | none | Store callback, never fire |
| `nextNull` | 1 | none | throw UnsupportedOperationException |
| `peek` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.util.JsonReader`:


## Quality Gates

Before marking `android.util.JsonReader` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
