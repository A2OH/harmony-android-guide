# SKILL: android.telephony.TelephonyManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.TelephonyManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.TelephonyManager` |
| **Package** | `android.telephony` |
| **Total Methods** | 53 |
| **Avg Score** | 4.3 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 11 (20%) |
| **Partial/Composite** | 41 (77%) |
| **No Mapping** | 1 (1%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 11 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getCallState` | `int getCallState()` | 9 | direct | hard | `getCallState` | `@ohos.telephony.call.call` |
| `getNetworkCountryIso` | `String getNetworkCountryIso()` | 9 | direct | rewrite | `getISOCountryCodeForNetwork` | `@ohos.telephony.radio.radio` |
| `getNetworkOperatorName` | `String getNetworkOperatorName()` | 9 | direct | moderate | `getSimSpn` | `getSimSpn(slotId: number, callback: AsyncCallback<string>): void` |
| `getSimCountryIso` | `String getSimCountryIso()` | 9 | direct | hard | `getISOCountryCodeForSim` | `@ohos.telephony.sim.sim` |
| `getSimOperator` | `String getSimOperator()` | 9 | direct | hard | `getSimOperatorNumeric` | `@ohos.telephony.sim.sim` |
| `getSimState` | `int getSimState()` | 9 | direct | moderate | `getSimState` | `getSimState(slotId: number, callback: AsyncCallback<SimState>): void` |
| `getSimState` | `int getSimState(int)` | 9 | direct | moderate | `getSimState` | `getSimState(slotId: number, callback: AsyncCallback<SimState>): void` |
| `hasIccCard` | `boolean hasIccCard()` | 9 | direct | rewrite | `hasSimCard` | `@ohos.telephony.sim.sim` |
| `isEmergencyNumber` | `boolean isEmergencyNumber(@NonNull String)` | 9 | direct | hard | `isEmergencyPhoneNumber` | `@ohos.telephony.call.call` |
| `isSmsCapable` | `boolean isSmsCapable()` | 9 | direct | moderate | `hasSmsCapability` | `@ohos.telephony.sms.sms` |
| `listen` | `void listen(android.telephony.PhoneStateListener, int)` | 9 | direct | easy | `on` | `on(type: 'networkStateChange', callback: Callback<NetworkState>): void` |

## Gap Descriptions (per method)

- **`getCallState`**: Direct equivalent
- **`getNetworkCountryIso`**: Direct equivalent
- **`getNetworkOperatorName`**: Operator name
- **`getSimCountryIso`**: Direct equivalent
- **`getSimOperator`**: Direct equivalent
- **`getSimState`**: SIM state
- **`getSimState`**: SIM state
- **`hasIccCard`**: Direct equivalent
- **`isEmergencyNumber`**: Direct equivalent
- **`isSmsCapable`**: Direct equivalent
- **`listen`**: Telephony events

## Stub APIs (score < 5): 42 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onError` | 5 | partial | Store callback, never fire |
| `onCellInfo` | 4 | partial | Store callback, never fire |
| `setVoiceMailNumber` | 4 | composite | Log warning + no-op |
| `getSimOperatorName` | 4 | composite | Return safe default (null/false/0/empty) |
| `setVisualVoicemailSmsFilterSettings` | 4 | composite | Return safe default (null/false/0/empty) |
| `getPhoneType` | 3 | composite | Return safe default (null/false/0/empty) |
| `isRttSupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `onReceiveUssdResponse` | 3 | composite | Store callback, never fire |
| `sendVisualVoicemailSms` | 3 | composite | Return safe default (null/false/0/empty) |
| `getNetworkSpecifier` | 3 | composite | Return safe default (null/false/0/empty) |
| `sendDialerSpecialCode` | 3 | composite | throw UnsupportedOperationException |
| `onReceiveUssdResponseFailed` | 3 | composite | Store callback, never fire |
| `setPreferredOpportunisticDataSubscription` | 3 | composite | Return safe default (null/false/0/empty) |
| `updateAvailableNetworks` | 3 | composite | Log warning + no-op |
| `getActiveModemCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `isVoiceCapable` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSubscriptionId` | 3 | composite | Return safe default (null/false/0/empty) |
| `isWorldPhone` | 3 | composite | Return safe default (null/false/0/empty) |
| `getNetworkOperator` | 3 | composite | Return safe default (null/false/0/empty) |
| `getVoicemailRingtoneUri` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMmsUAProfUrl` | 3 | composite | Return safe default (null/false/0/empty) |
| `getIccAuthentication` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOperatorBrandOverride` | 3 | composite | Log warning + no-op |
| `CellInfoCallback` | 3 | composite | throw UnsupportedOperationException |
| `getMmsUserAgent` | 3 | composite | Return safe default (null/false/0/empty) |
| `isVoicemailVibrationEnabled` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSupportedModemCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCardIdForDefaultEuicc` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSimSpecificCarrierId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDataState` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSimCarrierId` | 3 | composite | Return safe default (null/false/0/empty) |
| `hasCarrierPrivileges` | 3 | composite | Return safe default (null/false/0/empty) |
| `isConcurrentVoiceAndDataSupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCarrierIdFromSimMccMnc` | 3 | composite | Return safe default (null/false/0/empty) |
| `isNetworkRoaming` | 3 | composite | Return safe default (null/false/0/empty) |
| `createForSubscriptionId` | 3 | composite | Return dummy instance / no-op |
| `setLine1NumberForDisplay` | 3 | composite | Return safe default (null/false/0/empty) |
| `isHearingAidCompatibilitySupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `setPreferredNetworkTypeToGlobal` | 2 | composite | Log warning + no-op |
| `getDataActivity` | 2 | composite | Return safe default (null/false/0/empty) |
| `UssdResponseCallback` | 2 | composite | Store callback, never fire |
| `canChangeDtmfToneLength` | 2 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.TelephonyManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.TelephonyManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 53 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 11 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
