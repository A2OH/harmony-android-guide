# SKILL: android.os.Handler

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Handler`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Handler` |
| **Package** | `android.os` |
| **Total Methods** | 26 |
| **Avg Score** | 3.5 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 4 (15%) |
| **Partial/Composite** | 14 (53%) |
| **No Mapping** | 8 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `post` | `final boolean post(@NonNull Runnable)` | 9 | direct | hard | `execute` | `execute(func: Function, ...args: Object[]): Promise<Object>` |
| `postDelayed` | `final boolean postDelayed(@NonNull Runnable, long)` | 9 | direct | moderate | `execute` | `execute(func: Function, ...args: Object[]): Promise<Object>` |
| `postDelayed` | `final boolean postDelayed(@NonNull Runnable, @Nullable Object, long)` | 9 | direct | moderate | `execute` | `execute(func: Function, ...args: Object[]): Promise<Object>` |
| `sendMessage` | `final boolean sendMessage(@NonNull android.os.Message)` | 9 | direct | moderate | `execute` | `execute(func: Function, ...args: Object[]): Promise<Object>` |

## Gap Descriptions (per method)

- **`post`**: Task dispatch
- **`postDelayed`**: Delayed task
- **`postDelayed`**: Delayed task
- **`sendMessage`**: Message dispatch

## Stub APIs (score < 5): 22 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `dispatchMessage` | 5 | partial | Return safe default (null/false/0/empty) |
| `handleMessage` | 5 | partial | throw UnsupportedOperationException |
| `sendEmptyMessage` | 4 | partial | throw UnsupportedOperationException |
| `sendEmptyMessageAtTime` | 4 | partial | throw UnsupportedOperationException |
| `removeCallbacks` | 4 | composite | Log warning + no-op |
| `removeCallbacks` | 4 | composite | Log warning + no-op |
| `removeMessages` | 3 | composite | Log warning + no-op |
| `removeMessages` | 3 | composite | Log warning + no-op |
| `removeCallbacksAndMessages` | 3 | composite | Log warning + no-op |
| `postAtTime` | 3 | composite | throw UnsupportedOperationException |
| `postAtTime` | 3 | composite | throw UnsupportedOperationException |
| `sendMessageDelayed` | 3 | composite | throw UnsupportedOperationException |
| `sendEmptyMessageDelayed` | 3 | composite | throw UnsupportedOperationException |
| `sendMessageAtTime` | 3 | composite | throw UnsupportedOperationException |
| `Handler` | 1 | none | throw UnsupportedOperationException |
| `Handler` | 1 | none | throw UnsupportedOperationException |
| `dump` | 1 | none | throw UnsupportedOperationException |
| `hasCallbacks` | 1 | none | Return safe default (null/false/0/empty) |
| `hasMessages` | 1 | none | Return safe default (null/false/0/empty) |
| `hasMessages` | 1 | none | Return safe default (null/false/0/empty) |
| `postAtFrontOfQueue` | 1 | none | Store callback, never fire |
| `sendMessageAtFrontOfQueue` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Handler`:

- `android.os.Looper` (not yet shimmed)
- `android.os.Message` (not yet shimmed)

## Quality Gates

Before marking `android.os.Handler` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 26 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
