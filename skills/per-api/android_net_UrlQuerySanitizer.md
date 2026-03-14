# SKILL: android.net.UrlQuerySanitizer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.UrlQuerySanitizer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.UrlQuerySanitizer` |
| **Package** | `android.net` |
| **Total Methods** | 33 |
| **Avg Score** | 1.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 12 (36%) |
| **No Mapping** | 21 (63%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clear` | `void clear()` | 5 | partial | moderate | `clear` | `clear(): void` |

## Stub APIs (score < 5): 32 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getAllIllegal` | 3 | composite | Return safe default (null/false/0/empty) |
| `getParameterList` | 3 | composite | Return safe default (null/false/0/empty) |
| `getValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAllButNulLegal` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAmpLegal` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAllButWhitespaceLegal` | 2 | composite | Return safe default (null/false/0/empty) |
| `getParameterSet` | 2 | composite | Return safe default (null/false/0/empty) |
| `getUrlLegal` | 2 | composite | Return safe default (null/false/0/empty) |
| `isHexDigit` | 2 | composite | Return safe default (null/false/0/empty) |
| `getSpaceLegal` | 2 | composite | Return safe default (null/false/0/empty) |
| `getValueSanitizer` | 2 | composite | Return safe default (null/false/0/empty) |
| `UrlQuerySanitizer` | 1 | none | Return safe default (null/false/0/empty) |
| `UrlQuerySanitizer` | 1 | none | Return safe default (null/false/0/empty) |
| `addSanitizedEntry` | 1 | none | Log warning + no-op |
| `decodeHexDigit` | 1 | none | throw UnsupportedOperationException |
| `getAllButNulAndAngleBracketsLegal` | 1 | none | Return safe default (null/false/0/empty) |
| `getAllowUnregisteredParamaters` | 1 | none | Return safe default (null/false/0/empty) |
| `getAmpAndSpaceLegal` | 1 | none | Return safe default (null/false/0/empty) |
| `getEffectiveValueSanitizer` | 1 | none | Return safe default (null/false/0/empty) |
| `getPreferFirstRepeatedParameter` | 1 | none | Return safe default (null/false/0/empty) |
| `getUnregisteredParameterValueSanitizer` | 1 | none | Return safe default (null/false/0/empty) |
| `getUrlAndSpaceLegal` | 1 | none | Return safe default (null/false/0/empty) |
| `hasParameter` | 1 | none | Return safe default (null/false/0/empty) |
| `parseEntry` | 1 | none | throw UnsupportedOperationException |
| `parseQuery` | 1 | none | Return safe default (null/false/0/empty) |
| `parseUrl` | 1 | none | throw UnsupportedOperationException |
| `registerParameter` | 1 | none | Return safe default (null/false/0/empty) |
| `registerParameters` | 1 | none | Return safe default (null/false/0/empty) |
| `setAllowUnregisteredParamaters` | 1 | none | Return safe default (null/false/0/empty) |
| `setPreferFirstRepeatedParameter` | 1 | none | Log warning + no-op |
| `setUnregisteredParameterValueSanitizer` | 1 | none | Return safe default (null/false/0/empty) |
| `unescape` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.net.UrlQuerySanitizer`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.UrlQuerySanitizer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 33 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
