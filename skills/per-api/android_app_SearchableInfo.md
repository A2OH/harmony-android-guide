# SKILL: android.app.SearchableInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.SearchableInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.SearchableInfo` |
| **Package** | `android.app` |
| **Total Methods** | 23 |
| **Avg Score** | 1.8 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 10 (43%) |
| **No Mapping** | 13 (56%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 23 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getSuggestIntentAction` | 4 | partial | Return safe default (null/false/0/empty) |
| `getSuggestPath` | 4 | composite | Return safe default (null/false/0/empty) |
| `getHintId` | 4 | composite | Return safe default (null/false/0/empty) |
| `getInputType` | 3 | composite | Return safe default (null/false/0/empty) |
| `getImeOptions` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSuggestPackage` | 2 | composite | Return safe default (null/false/0/empty) |
| `getSearchActivity` | 2 | composite | Return safe default (null/false/0/empty) |
| `getSuggestSelection` | 2 | composite | Return safe default (null/false/0/empty) |
| `getSuggestIntentData` | 2 | composite | Return safe default (null/false/0/empty) |
| `getVoiceSearchEnabled` | 2 | composite | Return safe default (null/false/0/empty) |
| `autoUrlDetect` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `getSettingsDescriptionId` | 1 | none | Return safe default (null/false/0/empty) |
| `getSuggestAuthority` | 1 | none | Return safe default (null/false/0/empty) |
| `getSuggestThreshold` | 1 | none | Return safe default (null/false/0/empty) |
| `getVoiceMaxResults` | 1 | none | Return safe default (null/false/0/empty) |
| `getVoiceSearchLaunchRecognizer` | 1 | none | Return safe default (null/false/0/empty) |
| `getVoiceSearchLaunchWebSearch` | 1 | none | Return safe default (null/false/0/empty) |
| `queryAfterZeroResults` | 1 | none | Return safe default (null/false/0/empty) |
| `shouldIncludeInGlobalSearch` | 1 | none | throw UnsupportedOperationException |
| `shouldRewriteQueryFromData` | 1 | none | Return safe default (null/false/0/empty) |
| `shouldRewriteQueryFromText` | 1 | none | Return safe default (null/false/0/empty) |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.app.SearchableInfo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.SearchableInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
