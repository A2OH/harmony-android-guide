# SKILL: android.text.SpannableStringBuilder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.SpannableStringBuilder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.SpannableStringBuilder` |
| **Package** | `android.text` |
| **Total Methods** | 29 |
| **Avg Score** | 2.2 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 14 (48%) |
| **No Mapping** | 15 (51%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `length` | `int length()` | 5 | partial | moderate | `length` | `length?: number` |
| `clear` | `void clear()` | 5 | partial | moderate | `clear` | `clear(): void` |

## Stub APIs (score < 5): 27 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `append` | 4 | partial | throw UnsupportedOperationException |
| `append` | 4 | partial | throw UnsupportedOperationException |
| `append` | 4 | partial | throw UnsupportedOperationException |
| `append` | 4 | partial | throw UnsupportedOperationException |
| `getChars` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSpans` | 3 | composite | Return safe default (null/false/0/empty) |
| `SpannableStringBuilder` | 3 | composite | throw UnsupportedOperationException |
| `SpannableStringBuilder` | 3 | composite | throw UnsupportedOperationException |
| `SpannableStringBuilder` | 3 | composite | throw UnsupportedOperationException |
| `replace` | 3 | composite | throw UnsupportedOperationException |
| `replace` | 3 | composite | throw UnsupportedOperationException |
| `getSpanEnd` | 2 | composite | Return safe default (null/false/0/empty) |
| `charAt` | 1 | none | throw UnsupportedOperationException |
| `clearSpans` | 1 | none | throw UnsupportedOperationException |
| `delete` | 1 | none | throw UnsupportedOperationException |
| `getFilters` | 1 | none | Return safe default (null/false/0/empty) |
| `getSpanFlags` | 1 | none | Return safe default (null/false/0/empty) |
| `getSpanStart` | 1 | none | Return dummy instance / no-op |
| `getTextWatcherDepth` | 1 | none | Return safe default (null/false/0/empty) |
| `insert` | 1 | none | throw UnsupportedOperationException |
| `insert` | 1 | none | throw UnsupportedOperationException |
| `nextSpanTransition` | 1 | none | Store callback, never fire |
| `removeSpan` | 1 | none | Log warning + no-op |
| `setFilters` | 1 | none | Log warning + no-op |
| `setSpan` | 1 | none | Log warning + no-op |
| `subSequence` | 1 | none | throw UnsupportedOperationException |
| `valueOf` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.text.SpannableStringBuilder`:


## Quality Gates

Before marking `android.text.SpannableStringBuilder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 29 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
