# SKILL: android.util.MonthDisplayHelper

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.MonthDisplayHelper`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.MonthDisplayHelper` |
| **Package** | `android.util` |
| **Total Methods** | 15 |
| **Avg Score** | 4.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 15 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getOffset` | `int getOffset()` | 5 | partial | moderate | `offset` | `offset: number` |
| `getMonth` | `int getMonth()` | 5 | partial | moderate | `month` | `month: number` |
| `getYear` | `int getYear()` | 5 | partial | moderate | `year` | `year: number` |

## Stub APIs (score < 5): 12 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getNumberOfDaysInMonth` | 5 | partial | Return safe default (null/false/0/empty) |
| `nextMonth` | 4 | partial | Store callback, never fire |
| `MonthDisplayHelper` | 4 | partial | Return safe default (null/false/0/empty) |
| `MonthDisplayHelper` | 4 | partial | Return safe default (null/false/0/empty) |
| `isWithinCurrentMonth` | 4 | partial | Return safe default (null/false/0/empty) |
| `getWeekStartDay` | 4 | partial | Return dummy instance / no-op |
| `previousMonth` | 4 | partial | Store callback, never fire |
| `getRowOf` | 4 | partial | Return safe default (null/false/0/empty) |
| `getDigitsForRow` | 4 | partial | Return safe default (null/false/0/empty) |
| `getFirstDayOfMonth` | 4 | partial | Return safe default (null/false/0/empty) |
| `getDayAt` | 3 | composite | Return safe default (null/false/0/empty) |
| `getColumnOf` | 3 | composite | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 12 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.MonthDisplayHelper`:


## Quality Gates

Before marking `android.util.MonthDisplayHelper` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
