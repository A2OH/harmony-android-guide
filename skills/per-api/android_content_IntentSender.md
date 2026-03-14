# SKILL: android.content.IntentSender

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.IntentSender`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.IntentSender` |
| **Package** | `android.content` |
| **Total Methods** | 9 |
| **Avg Score** | 1.5 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (33%) |
| **No Mapping** | 6 (66%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 9 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getCreatorPackage` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCreatorUid` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCreatorUserHandle` | 2 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `readIntentSenderOrNullFromParcel` | 1 | none | Return safe default (null/false/0/empty) |
| `sendIntent` | 1 | none | throw UnsupportedOperationException |
| `sendIntent` | 1 | none | throw UnsupportedOperationException |
| `writeIntentSenderOrNullToParcel` | 1 | none | Log warning + no-op |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.content.IntentSender`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.IntentSender` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
