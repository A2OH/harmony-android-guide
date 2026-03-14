# SKILL: android.text.TextUtils

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.TextUtils`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.TextUtils` |
| **Package** | `android.text` |
| **Total Methods** | 36 |
| **Avg Score** | 1.2 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (8%) |
| **No Mapping** | 33 (91%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 36 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getChars` | 3 | composite | Return safe default (null/false/0/empty) |
| `concat` | 3 | composite | Store callback, never fire |
| `replace` | 3 | composite | throw UnsupportedOperationException |
| `copySpansFrom` | 1 | none | throw UnsupportedOperationException |
| `dumpSpans` | 1 | none | throw UnsupportedOperationException |
| `ellipsize` | 1 | none | throw UnsupportedOperationException |
| `ellipsize` | 1 | none | throw UnsupportedOperationException |
| `equals` | 1 | none | throw UnsupportedOperationException |
| `expandTemplate` | 1 | none | throw UnsupportedOperationException |
| `getCapsMode` | 1 | none | Return safe default (null/false/0/empty) |
| `getLayoutDirectionFromLocale` | 1 | none | Return safe default (null/false/0/empty) |
| `getOffsetAfter` | 1 | none | Return safe default (null/false/0/empty) |
| `getOffsetBefore` | 1 | none | Return safe default (null/false/0/empty) |
| `getTrimmedLength` | 1 | none | Return safe default (null/false/0/empty) |
| `htmlEncode` | 1 | none | throw UnsupportedOperationException |
| `indexOf` | 1 | none | throw UnsupportedOperationException |
| `indexOf` | 1 | none | throw UnsupportedOperationException |
| `indexOf` | 1 | none | throw UnsupportedOperationException |
| `indexOf` | 1 | none | throw UnsupportedOperationException |
| `indexOf` | 1 | none | throw UnsupportedOperationException |
| `indexOf` | 1 | none | throw UnsupportedOperationException |
| `isDigitsOnly` | 1 | none | Return safe default (null/false/0/empty) |
| `isEmpty` | 1 | none | Return safe default (null/false/0/empty) |
| `isGraphic` | 1 | none | Return safe default (null/false/0/empty) |
| `join` | 1 | none | throw UnsupportedOperationException |
| `join` | 1 | none | throw UnsupportedOperationException |
| `lastIndexOf` | 1 | none | throw UnsupportedOperationException |
| `lastIndexOf` | 1 | none | throw UnsupportedOperationException |
| `lastIndexOf` | 1 | none | throw UnsupportedOperationException |
| `listEllipsize` | 1 | none | Return safe default (null/false/0/empty) |
| `regionMatches` | 1 | none | Store callback, never fire |
| `split` | 1 | none | throw UnsupportedOperationException |
| `split` | 1 | none | throw UnsupportedOperationException |
| `stringOrSpannedString` | 1 | none | throw UnsupportedOperationException |
| `substring` | 1 | none | throw UnsupportedOperationException |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.text.TextUtils`:


## Quality Gates

Before marking `android.text.TextUtils` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 36 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
