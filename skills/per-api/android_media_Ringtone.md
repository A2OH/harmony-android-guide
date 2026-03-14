# SKILL: android.media.Ringtone

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.Ringtone`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.Ringtone` |
| **Package** | `android.media` |
| **Total Methods** | 11 |
| **Avg Score** | 3.3 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 10 (90%) |
| **No Mapping** | 1 (9%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getTitle` | `String getTitle(android.content.Context)` | 5 | partial | moderate | `title` | `title: string` |
| `play` | `void play()` | 5 | partial | moderate | `play` | `play(): void` |

## Stub APIs (score < 5): 9 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getAudioAttributes` | 4 | partial | Return safe default (null/false/0/empty) |
| `stop` | 3 | composite | No-op |
| `setAudioAttributes` | 3 | composite | Log warning + no-op |
| `getVolume` | 3 | composite | Return safe default (null/false/0/empty) |
| `setLooping` | 3 | composite | Log warning + no-op |
| `isLooping` | 3 | composite | Return safe default (null/false/0/empty) |
| `isPlaying` | 3 | composite | Return safe default (null/false/0/empty) |
| `setVolume` | 3 | composite | Log warning + no-op |
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.Ringtone`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.Ringtone` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
