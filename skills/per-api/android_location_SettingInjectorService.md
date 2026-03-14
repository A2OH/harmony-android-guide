# SKILL: android.location.SettingInjectorService

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.SettingInjectorService`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.SettingInjectorService` |
| **Package** | `android.location` |
| **Total Methods** | 7 |
| **Avg Score** | 4.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (85%) |
| **No Mapping** | 1 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onStart` | `final void onStart(android.content.Intent, int)` | 6 | partial | moderate | `start` | `start(): void` |
| `onGetEnabled` | `abstract boolean onGetEnabled()` | 5 | partial | moderate | `enabled` | `readonly enabled: boolean` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onGetSummary` | 4 | partial | Return safe default (null/false/0/empty) |
| `onStartCommand` | 4 | partial | Return dummy instance / no-op |
| `refreshSettings` | 4 | partial | Log warning + no-op |
| `SettingInjectorService` | 3 | composite | Log warning + no-op |
| `onBind` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.location.SettingInjectorService`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.SettingInjectorService` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
