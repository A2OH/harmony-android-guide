# SKILL: android.util.Base64InputStream

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.Base64InputStream`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.Base64InputStream` |
| **Package** | `android.util` |
| **Total Methods** | 3 |
| **Avg Score** | 4.0 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `available` | `int available()` | 5 | partial | moderate | `available` | `available: POST and PUT. The default value is POST.
   *
   * @syscap SystemCapability.MiscServices.Upload
   * @since 3
   * @deprecated since 9
   */
  method?: string` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `reset` | 4 | partial | Log warning + no-op |
| `Base64InputStream` | 3 | composite | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.util.Base64InputStream`:


## Quality Gates

Before marking `android.util.Base64InputStream` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
