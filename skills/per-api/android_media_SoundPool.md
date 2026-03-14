# SKILL: android.media.SoundPool

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.SoundPool`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.SoundPool` |
| **Package** | `android.media` |
| **Total Methods** | 18 |
| **Avg Score** | 4.8 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 9 (50%) |
| **Partial/Composite** | 4 (22%) |
| **No Mapping** | 5 (27%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 5 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `pause` | `final void pause(int)` | 9 | direct | moderate | `pause` | `@ohos.multimedia.media.SoundPool` |
| `release` | `final void release()` | 9 | direct | hard | `release` | `@ohos.multimedia.media.SoundPool` |
| `resume` | `final void resume(int)` | 9 | direct | moderate | `resume` | `@ohos.multimedia.media.SoundPool` |
| `stop` | `final void stop(int)` | 9 | direct | hard | `stop` | `@ohos.multimedia.media.SoundPool` |
| `load` | `int load(String, int)` | 7 | near | impossible | `load` | `@ohos.multimedia.media.SoundPool` |
| `load` | `int load(android.content.Context, int, int)` | 7 | near | impossible | `load` | `@ohos.multimedia.media.SoundPool` |
| `load` | `int load(android.content.res.AssetFileDescriptor, int)` | 7 | near | impossible | `load` | `@ohos.multimedia.media.SoundPool` |
| `load` | `int load(java.io.FileDescriptor, long, long, int)` | 7 | near | impossible | `load` | `@ohos.multimedia.media.SoundPool` |
| `play` | `final int play(int, float, float, int, int, float)` | 7 | near | hard | `play` | `@ohos.multimedia.media.SoundPool` |

## Gap Descriptions (per method)

- **`pause`**: Direct equivalent
- **`release`**: Direct equivalent
- **`resume`**: Direct equivalent
- **`stop`**: Direct equivalent
- **`load`**: Different params
- **`load`**: Different params
- **`load`**: Different params
- **`load`**: Different params
- **`play`**: PlayParameters object vs positional

## Stub APIs (score < 5): 9 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setRate` | 3 | composite | Log warning + no-op |
| `setPriority` | 3 | composite | Log warning + no-op |
| `setVolume` | 3 | composite | Log warning + no-op |
| `autoPause` | 2 | composite | throw UnsupportedOperationException |
| `autoResume` | 1 | none | throw UnsupportedOperationException |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `setLoop` | 1 | none | Log warning + no-op |
| `setOnLoadCompleteListener` | 1 | none | Return safe default (null/false/0/empty) |
| `unload` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.media.SoundPool`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.SoundPool` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
