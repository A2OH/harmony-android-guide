# SKILL: android.media.MediaFormat

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaFormat`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaFormat` |
| **Package** | `android.media` |
| **Total Methods** | 20 |
| **Avg Score** | 2.8 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 13 (65%) |
| **No Mapping** | 7 (35%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getLong` | `long getLong(@NonNull String)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getLong` | `long getLong(@NonNull String, long)` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Stub APIs (score < 5): 18 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `MediaFormat` | 5 | partial | throw UnsupportedOperationException |
| `MediaFormat` | 5 | partial | throw UnsupportedOperationException |
| `getInteger` | 4 | partial | Return safe default (null/false/0/empty) |
| `getInteger` | 4 | partial | Return safe default (null/false/0/empty) |
| `setFeatureEnabled` | 3 | composite | Log warning + no-op |
| `getValueTypeForKey` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFloat` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFloat` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFeatureEnabled` | 3 | composite | Return safe default (null/false/0/empty) |
| `setByteBuffer` | 2 | composite | Log warning + no-op |
| `setString` | 2 | composite | Log warning + no-op |
| `containsFeature` | 1 | none | Store callback, never fire |
| `containsKey` | 1 | none | Store callback, never fire |
| `removeFeature` | 1 | none | Log warning + no-op |
| `removeKey` | 1 | none | Log warning + no-op |
| `setFloat` | 1 | none | Log warning + no-op |
| `setInteger` | 1 | none | Log warning + no-op |
| `setLong` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaFormat`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaFormat` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 20 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
