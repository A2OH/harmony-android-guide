# SKILL: android.telephony.SubscriptionInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.SubscriptionInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.SubscriptionInfo` |
| **Package** | `android.telephony` |
| **Total Methods** | 17 |
| **Avg Score** | 2.3 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 13 (76%) |
| **No Mapping** | 4 (23%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 17 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getIccId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getIconTint` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSimSlotIndex` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCardId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCountryIso` | 3 | composite | Return safe default (null/false/0/empty) |
| `getNumber` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSubscriptionType` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCarrierId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSubscriptionId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDataRoaming` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDisplayName` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCarrierName` | 2 | composite | Return safe default (null/false/0/empty) |
| `createIconBitmap` | 2 | composite | Return dummy instance / no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `isEmbedded` | 1 | none | Return safe default (null/false/0/empty) |
| `isOpportunistic` | 1 | none | Return safe default (null/false/0/empty) |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.SubscriptionInfo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.SubscriptionInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
