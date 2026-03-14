# SKILL: android.util.Rational

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.Rational`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.Rational` |
| **Package** | `android.util` |
| **Total Methods** | 13 |
| **Avg Score** | 3.8 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 12 (92%) |
| **No Mapping** | 1 (7%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `intValue` | `int intValue()` | 5 | partial | moderate | `value` | `value: number` |
| `longValue` | `long longValue()` | 5 | partial | moderate | `value` | `value: number` |
| `isInfinite` | `boolean isInfinite()` | 5 | partial | moderate | `isWifiActive` | `isWifiActive(): boolean` |
| `isNaN` | `boolean isNaN()` | 5 | partial | moderate | `isInSandbox` | `isInSandbox(): Promise<boolean>` |

## Stub APIs (score < 5): 9 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isFinite` | 5 | partial | Return dummy instance / no-op |
| `doubleValue` | 4 | partial | throw UnsupportedOperationException |
| `getNumerator` | 4 | partial | Return safe default (null/false/0/empty) |
| `isZero` | 3 | composite | Return safe default (null/false/0/empty) |
| `compareTo` | 3 | composite | throw UnsupportedOperationException |
| `floatValue` | 3 | composite | throw UnsupportedOperationException |
| `getDenominator` | 3 | composite | Return safe default (null/false/0/empty) |
| `parseRational` | 2 | composite | Store callback, never fire |
| `Rational` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.util.Rational`:


## Quality Gates

Before marking `android.util.Rational` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
