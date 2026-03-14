# SKILL: android.os.MessageQueue

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.MessageQueue`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.MessageQueue` |
| **Package** | `android.os` |
| **Total Methods** | 5 |
| **Avg Score** | 2.4 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (80%) |
| **No Mapping** | 1 (20%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `removeIdleHandler` | 3 | composite | Log warning + no-op |
| `isIdle` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeOnFileDescriptorEventListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnFileDescriptorEventListener` | 2 | composite | Return safe default (null/false/0/empty) |
| `addIdleHandler` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.os.MessageQueue`:


## Quality Gates

Before marking `android.os.MessageQueue` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
