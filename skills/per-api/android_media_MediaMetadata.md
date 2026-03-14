# SKILL: android.media.MediaMetadata

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaMetadata`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaMetadata` |
| **Package** | `android.media` |
| **Total Methods** | 10 |
| **Avg Score** | 2.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 1 (10%) |
| **Partial/Composite** | 5 (50%) |
| **No Mapping** | 4 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `size` | `int size()` | 6 | near | moderate | `size` | `@ohos.multimedia.mediaLibrary.FileAsset` |
| `getLong` | `long getLong(String)` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Gap Descriptions (per method)

- **`size`**: Auto-promoted: near score=6.02272727272727

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getBitmap` | 3 | composite | Return safe default (null/false/0/empty) |
| `getText` | 3 | composite | Return safe default (null/false/0/empty) |
| `getString` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRating` | 2 | composite | Return safe default (null/false/0/empty) |
| `containsKey` | 1 | none | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |
| `keySet` | 1 | none | Log warning + no-op |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaMetadata`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaMetadata` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
