# SKILL: android.media.MicrophoneInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MicrophoneInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MicrophoneInfo` |
| **Package** | `android.media` |
| **Total Methods** | 14 |
| **Avg Score** | 3.0 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 12 (85%) |
| **No Mapping** | 2 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getGroup` | `int getGroup()` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Stub APIs (score < 5): 13 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getOrientation` | 5 | partial | Return safe default (null/false/0/empty) |
| `getType` | 5 | partial | Return safe default (null/false/0/empty) |
| `getPosition` | 4 | partial | Return safe default (null/false/0/empty) |
| `getId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDirectionality` | 3 | composite | Return safe default (null/false/0/empty) |
| `getLocation` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaxSpl` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMinSpl` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDescription` | 3 | composite | Return safe default (null/false/0/empty) |
| `getIndexInTheGroup` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSensitivity` | 3 | composite | Return safe default (null/false/0/empty) |
| `getChannelMapping` | 1 | none | Return safe default (null/false/0/empty) |
| `getFrequencyResponse` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MicrophoneInfo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MicrophoneInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
