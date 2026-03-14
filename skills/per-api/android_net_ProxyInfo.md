# SKILL: android.net.ProxyInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.ProxyInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.ProxyInfo` |
| **Package** | `android.net` |
| **Total Methods** | 11 |
| **Avg Score** | 1.9 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (54%) |
| **No Mapping** | 5 (45%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isValid` | 3 | composite | Return safe default (null/false/0/empty) |
| `getExclusionList` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPort` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHost` | 2 | composite | Return safe default (null/false/0/empty) |
| `getPacFileUrl` | 2 | composite | Return safe default (null/false/0/empty) |
| `buildPacProxy` | 2 | composite | throw UnsupportedOperationException |
| `ProxyInfo` | 1 | none | throw UnsupportedOperationException |
| `buildDirectProxy` | 1 | none | throw UnsupportedOperationException |
| `buildDirectProxy` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.net.ProxyInfo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.ProxyInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
