# SKILL: android.os.UserManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.UserManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.UserManager` |
| **Package** | `android.os` |
| **Total Methods** | 15 |
| **Avg Score** | 2.8 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 13 (86%) |
| **No Mapping** | 2 (13%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isQuietModeEnabled` | `boolean isQuietModeEnabled(android.os.UserHandle)` | 5 | partial | moderate | `isPiPEnabled` | `isPiPEnabled(): boolean` |

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getUserRestrictions` | 4 | partial | Return safe default (null/false/0/empty) |
| `isManagedProfile` | 3 | composite | Return safe default (null/false/0/empty) |
| `isUserUnlocked` | 3 | composite | Return safe default (null/false/0/empty) |
| `getUserProfiles` | 3 | composite | Return safe default (null/false/0/empty) |
| `getUserCreationTime` | 3 | composite | Return safe default (null/false/0/empty) |
| `isSystemUser` | 3 | composite | Return safe default (null/false/0/empty) |
| `isDemoUser` | 3 | composite | Return safe default (null/false/0/empty) |
| `getUserForSerialNumber` | 3 | composite | Return safe default (null/false/0/empty) |
| `createUserCreationIntent` | 3 | composite | Return dummy instance / no-op |
| `requestQuietModeEnabled` | 3 | composite | throw UnsupportedOperationException |
| `getSerialNumberForUser` | 2 | composite | Return safe default (null/false/0/empty) |
| `isUserAGoat` | 2 | composite | Return safe default (null/false/0/empty) |
| `hasUserRestriction` | 1 | none | Return safe default (null/false/0/empty) |
| `supportsMultipleUsers` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.os.UserManager`:


## Quality Gates

Before marking `android.os.UserManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
