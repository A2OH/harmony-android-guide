# SKILL: android.media.RingtoneManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.RingtoneManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.RingtoneManager` |
| **Package** | `android.media` |
| **Total Methods** | 20 |
| **Avg Score** | 3.0 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 16 (80%) |
| **No Mapping** | 4 (20%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 20 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setType` | 5 | partial | Log warning + no-op |
| `RingtoneManager` | 4 | partial | Store callback, never fire |
| `RingtoneManager` | 4 | partial | Store callback, never fire |
| `hasHapticChannels` | 4 | partial | Return safe default (null/false/0/empty) |
| `hasHapticChannels` | 4 | partial | Return safe default (null/false/0/empty) |
| `getDefaultType` | 4 | partial | Return safe default (null/false/0/empty) |
| `inferStreamType` | 4 | partial | throw UnsupportedOperationException |
| `getCursor` | 4 | partial | Return safe default (null/false/0/empty) |
| `getRingtonePosition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRingtone` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRingtone` | 3 | composite | Return safe default (null/false/0/empty) |
| `isDefault` | 3 | composite | Return safe default (null/false/0/empty) |
| `getValidRingtoneUri` | 2 | composite | Return safe default (null/false/0/empty) |
| `getStopPreviousRingtone` | 2 | composite | No-op |
| `stopPreviousRingtone` | 2 | composite | No-op |
| `getRingtoneUri` | 2 | composite | Return safe default (null/false/0/empty) |
| `getActualDefaultRingtoneUri` | 1 | none | Return safe default (null/false/0/empty) |
| `getDefaultUri` | 1 | none | Return safe default (null/false/0/empty) |
| `setActualDefaultRingtoneUri` | 1 | none | Log warning + no-op |
| `setStopPreviousRingtone` | 1 | none | No-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.RingtoneManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.RingtoneManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 20 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
