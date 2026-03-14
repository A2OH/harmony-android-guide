# SKILL: android.text.SpannedString

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.SpannedString`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.SpannedString` |
| **Package** | `android.text` |
| **Total Methods** | 11 |
| **Avg Score** | 1.9 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (36%) |
| **No Mapping** | 7 (63%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `length` | `final int length()` | 5 | partial | moderate | `length` | `length?: number` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getChars` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSpans` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSpanEnd` | 2 | composite | Return safe default (null/false/0/empty) |
| `SpannedString` | 1 | none | throw UnsupportedOperationException |
| `charAt` | 1 | none | throw UnsupportedOperationException |
| `getSpanFlags` | 1 | none | Return safe default (null/false/0/empty) |
| `getSpanStart` | 1 | none | Return dummy instance / no-op |
| `nextSpanTransition` | 1 | none | Store callback, never fire |
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

Check if these related classes are already shimmed before generating `android.text.SpannedString`:


## Quality Gates

Before marking `android.text.SpannedString` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
