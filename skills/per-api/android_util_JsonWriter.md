# SKILL: android.util.JsonWriter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.JsonWriter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.JsonWriter` |
| **Package** | `android.util` |
| **Total Methods** | 17 |
| **Avg Score** | 3.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 14 (82%) |
| **No Mapping** | 3 (17%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `value` | `android.util.JsonWriter value(String) throws java.io.IOException` | 5 | partial | moderate | `value` | `value: number` |
| `value` | `android.util.JsonWriter value(boolean) throws java.io.IOException` | 5 | partial | moderate | `value` | `value: number` |
| `value` | `android.util.JsonWriter value(double) throws java.io.IOException` | 5 | partial | moderate | `value` | `value: number` |
| `value` | `android.util.JsonWriter value(long) throws java.io.IOException` | 5 | partial | moderate | `value` | `value: number` |
| `value` | `android.util.JsonWriter value(Number) throws java.io.IOException` | 5 | partial | moderate | `value` | `value: number` |
| `name` | `android.util.JsonWriter name(String) throws java.io.IOException` | 5 | partial | moderate | `name` | `name: string` |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `nullValue` | 5 | partial | throw UnsupportedOperationException |
| `close` | 4 | partial | No-op |
| `setIndent` | 4 | composite | Log warning + no-op |
| `isLenient` | 3 | composite | Return safe default (null/false/0/empty) |
| `setLenient` | 3 | composite | Log warning + no-op |
| `beginArray` | 3 | composite | throw UnsupportedOperationException |
| `beginObject` | 3 | composite | throw UnsupportedOperationException |
| `endArray` | 2 | composite | throw UnsupportedOperationException |
| `JsonWriter` | 1 | none | Log warning + no-op |
| `endObject` | 1 | none | throw UnsupportedOperationException |
| `flush` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.util.JsonWriter`:


## Quality Gates

Before marking `android.util.JsonWriter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
