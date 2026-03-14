# SKILL: android.media.AudioManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.AudioManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.AudioManager` |
| **Package** | `android.media` |
| **Total Methods** | 52 |
| **Avg Score** | 3.2 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 9 (17%) |
| **Partial/Composite** | 24 (46%) |
| **No Mapping** | 19 (36%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 4 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getRingerMode` | `int getRingerMode()` | 9 | direct | easy | `getRingerMode` | `@ohos.multimedia.audio.AudioManager` |
| `getStreamVolume` | `int getStreamVolume(int)` | 9 | direct | easy | `getVolume` | `@ohos.multimedia.audio.AudioManager` |
| `isMicrophoneMute` | `boolean isMicrophoneMute()` | 9 | direct | impossible | `isMicrophoneMute` | `@ohos.multimedia.audio.AudioVolumeGroupManager` |
| `setMicrophoneMute` | `void setMicrophoneMute(boolean)` | 9 | direct | impossible | `setMicrophoneMute` | `@ohos.multimedia.audio.AudioVolumeGroupManager` |
| `setStreamVolume` | `void setStreamVolume(int, int, int)` | 9 | direct | easy | `setVolume` | `@ohos.multimedia.audio.AudioManager` |
| `getDevices` | `android.media.AudioDeviceInfo[] getDevices(int)` | 7 | near | hard | `getDevices` | `@ohos.multimedia.audio.AudioRoutingManager` |
| `getStreamMaxVolume` | `int getStreamMaxVolume(int)` | 7 | near | rewrite | `getMaxVolume` | `@ohos.multimedia.audio.AudioVolumeGroupManager` |
| `getStreamMinVolume` | `int getStreamMinVolume(int)` | 7 | near | hard | `getMinVolume` | `@ohos.multimedia.audio.AudioVolumeGroupManager` |
| `isMusicActive` | `boolean isMusicActive()` | 7 | near | impossible | `isActive` | `@ohos.multimedia.audio.AudioStreamManager` |

## Gap Descriptions (per method)

- **`getRingerMode`**: Ringer
- **`getStreamVolume`**: Volume
- **`isMicrophoneMute`**: Direct equivalent
- **`setMicrophoneMute`**: Direct equivalent
- **`setStreamVolume`**: Volume
- **`getDevices`**: AudioDeviceDescriptors
- **`getStreamMaxVolume`**: AudioVolumeType enum
- **`getStreamMinVolume`**: AudioVolumeType enum
- **`isMusicActive`**: Takes AudioVolumeType

## Stub APIs (score < 5): 43 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getMode` | 5 | partial | Return safe default (null/false/0/empty) |
| `isStreamMute` | 4 | partial | Return safe default (null/false/0/empty) |
| `getMicrophones` | 4 | partial | Return safe default (null/false/0/empty) |
| `registerAudioRecordingCallback` | 4 | partial | Return safe default (null/false/0/empty) |
| `unregisterAudioRecordingCallback` | 4 | partial | Return safe default (null/false/0/empty) |
| `registerAudioPlaybackCallback` | 4 | partial | Return safe default (null/false/0/empty) |
| `dispatchMediaKeyEvent` | 4 | composite | Return safe default (null/false/0/empty) |
| `isCallScreeningModeSupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `AudioRecordingCallback` | 3 | composite | throw UnsupportedOperationException |
| `getParameters` | 3 | composite | Return safe default (null/false/0/empty) |
| `AudioPlaybackCallback` | 3 | composite | throw UnsupportedOperationException |
| `getStreamVolumeDb` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAllowedCapturePolicy` | 3 | composite | Return safe default (null/false/0/empty) |
| `generateAudioSessionId` | 3 | composite | Store callback, never fire |
| `setParameters` | 3 | composite | Log warning + no-op |
| `getProperty` | 3 | composite | Return safe default (null/false/0/empty) |
| `registerAudioDeviceCallback` | 3 | composite | Return safe default (null/false/0/empty) |
| `unregisterAudioDeviceCallback` | 3 | composite | Return safe default (null/false/0/empty) |
| `isHapticPlaybackSupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `isOffloadedPlaybackSupported` | 2 | composite | Return safe default (null/false/0/empty) |
| `unregisterAudioPlaybackCallback` | 2 | composite | Return safe default (null/false/0/empty) |
| `abandonAudioFocusRequest` | 2 | composite | Store callback, never fire |
| `requestAudioFocus` | 2 | composite | throw UnsupportedOperationException |
| `adjustVolume` | 2 | composite | throw UnsupportedOperationException |
| `adjustStreamVolume` | 1 | none | throw UnsupportedOperationException |
| `adjustSuggestedStreamVolume` | 1 | none | throw UnsupportedOperationException |
| `isBluetoothScoAvailableOffCall` | 1 | none | Return safe default (null/false/0/empty) |
| `isBluetoothScoOn` | 1 | none | Return safe default (null/false/0/empty) |
| `isSpeakerphoneOn` | 1 | none | Return safe default (null/false/0/empty) |
| `isVolumeFixed` | 1 | none | Return safe default (null/false/0/empty) |
| `loadSoundEffects` | 1 | none | throw UnsupportedOperationException |
| `playSoundEffect` | 1 | none | throw UnsupportedOperationException |
| `playSoundEffect` | 1 | none | throw UnsupportedOperationException |
| `setAllowedCapturePolicy` | 1 | none | Log warning + no-op |
| `setBluetoothScoOn` | 1 | none | Log warning + no-op |
| `setMode` | 1 | none | Log warning + no-op |
| `setRingerMode` | 1 | none | Log warning + no-op |
| `setSpeakerphoneOn` | 1 | none | Log warning + no-op |
| `startBluetoothSco` | 1 | none | Return dummy instance / no-op |
| `stopBluetoothSco` | 1 | none | No-op |
| `unloadSoundEffects` | 1 | none | throw UnsupportedOperationException |
| `onPlaybackConfigChanged` | 1 | none | Store callback, never fire |
| `onRecordingConfigChanged` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.AudioManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.AudioManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 52 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
