# SKILL: android.telephony.SmsMessage

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.SmsMessage`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.SmsMessage` |
| **Package** | `android.telephony` |
| **Total Methods** | 29 |
| **Avg Score** | 2.3 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 22 (75%) |
| **No Mapping** | 7 (24%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 29 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isMwiDontStore` | 5 | partial | Return safe default (null/false/0/empty) |
| `getProtocolIdentifier` | 3 | composite | Return safe default (null/false/0/empty) |
| `getStatus` | 3 | composite | Return safe default (null/false/0/empty) |
| `isMWISetMessage` | 3 | composite | Return safe default (null/false/0/empty) |
| `getStatusOnIcc` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMessageClass` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMessageBody` | 3 | composite | Return safe default (null/false/0/empty) |
| `isMWIClearMessage` | 3 | composite | Return safe default (null/false/0/empty) |
| `isStatusReportMessage` | 3 | composite | Return safe default (null/false/0/empty) |
| `getEmailBody` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSubmitPdu` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSubmitPdu` | 3 | composite | Return safe default (null/false/0/empty) |
| `isCphsMwiMessage` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPdu` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDisplayMessageBody` | 3 | composite | Return safe default (null/false/0/empty) |
| `getEmailFrom` | 3 | composite | Return safe default (null/false/0/empty) |
| `getIndexOnIcc` | 3 | composite | Return safe default (null/false/0/empty) |
| `getUserData` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTimestampMillis` | 2 | composite | Return safe default (null/false/0/empty) |
| `isEmail` | 2 | composite | Return safe default (null/false/0/empty) |
| `createFromPdu` | 2 | composite | Return dummy instance / no-op |
| `getServiceCenterAddress` | 2 | composite | Return safe default (null/false/0/empty) |
| `calculateLength` | 1 | none | throw UnsupportedOperationException |
| `calculateLength` | 1 | none | throw UnsupportedOperationException |
| `getDisplayOriginatingAddress` | 1 | none | Return safe default (null/false/0/empty) |
| `getPseudoSubject` | 1 | none | Return safe default (null/false/0/empty) |
| `getTPLayerLengthForPDU` | 1 | none | Return safe default (null/false/0/empty) |
| `isReplace` | 1 | none | Return safe default (null/false/0/empty) |
| `isReplyPathPresent` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.SmsMessage`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.SmsMessage` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 29 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
