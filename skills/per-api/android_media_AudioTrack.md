# SKILL: android.media.AudioTrack

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.AudioTrack`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.AudioTrack` |
| **Package** | `android.media` |
| **Total Methods** | 65 |
| **Avg Score** | 3.5 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 13 (20%) |
| **Partial/Composite** | 34 (52%) |
| **No Mapping** | 18 (27%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 7 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 15 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `flush` | `void flush()` | 9 | direct | impossible | `flush` | `@ohos.multimedia.audio.AudioRenderer` |
| `pause` | `void pause() throws java.lang.IllegalStateException` | 9 | direct | moderate | `pause` | `@ohos.multimedia.audio.AudioRenderer` |
| `play` | `void play() throws java.lang.IllegalStateException` | 9 | direct | moderate | `start` | `@ohos.multimedia.audio.AudioRenderer` |
| `release` | `void release()` | 9 | direct | hard | `release` | `@ohos.multimedia.audio.AudioRenderer` |
| `setVolume` | `int setVolume(float)` | 9 | direct | rewrite | `setVolume` | `@ohos.multimedia.audio.AudioRenderer` |
| `stop` | `void stop() throws java.lang.IllegalStateException` | 9 | direct | hard | `stop` | `@ohos.multimedia.audio.AudioRenderer` |
| `write` | `int write(@NonNull byte[], int, int)` | 7 | near | rewrite | `write` | `@ohos.multimedia.audio.AudioRenderer` |
| `write` | `int write(@NonNull byte[], int, int, int)` | 7 | near | rewrite | `write` | `@ohos.multimedia.audio.AudioRenderer` |
| `write` | `int write(@NonNull short[], int, int)` | 7 | near | rewrite | `write` | `@ohos.multimedia.audio.AudioRenderer` |
| `write` | `int write(@NonNull short[], int, int, int)` | 7 | near | rewrite | `write` | `@ohos.multimedia.audio.AudioRenderer` |
| `write` | `int write(@NonNull float[], int, int, int)` | 7 | near | rewrite | `write` | `@ohos.multimedia.audio.AudioRenderer` |
| `write` | `int write(@NonNull java.nio.ByteBuffer, int, int)` | 7 | near | rewrite | `write` | `@ohos.multimedia.audio.AudioRenderer` |
| `write` | `int write(@NonNull java.nio.ByteBuffer, int, int, long)` | 7 | near | rewrite | `write` | `@ohos.multimedia.audio.AudioRenderer` |
| `getChannelCount` | `int getChannelCount()` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getUnderrunCount` | `int getUnderrunCount()` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Gap Descriptions (per method)

- **`flush`**: Direct equivalent
- **`pause`**: Direct equivalent
- **`play`**: Direct equivalent
- **`release`**: Direct equivalent
- **`setVolume`**: Direct equivalent
- **`stop`**: Direct equivalent
- **`write`**: ArrayBuffer vs byte[]
- **`write`**: ArrayBuffer vs byte[]
- **`write`**: ArrayBuffer vs byte[]
- **`write`**: ArrayBuffer vs byte[]
- **`write`**: ArrayBuffer vs byte[]
- **`write`**: ArrayBuffer vs byte[]
- **`write`**: ArrayBuffer vs byte[]

## Stub APIs (score < 5): 50 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getSampleRate` | 5 | partial | Return safe default (null/false/0/empty) |
| `AudioTrack` | 5 | partial | throw UnsupportedOperationException |
| `getAudioSessionId` | 5 | partial | Return safe default (null/false/0/empty) |
| `getAudioFormat` | 4 | partial | Return safe default (null/false/0/empty) |
| `getState` | 4 | partial | Return safe default (null/false/0/empty) |
| `getStreamType` | 4 | partial | Return safe default (null/false/0/empty) |
| `getRoutedDevice` | 4 | partial | Return safe default (null/false/0/empty) |
| `setPlaybackParams` | 3 | composite | Log warning + no-op |
| `isDirectPlaybackSupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaxVolume` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMetrics` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMinVolume` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPlayState` | 3 | composite | Return safe default (null/false/0/empty) |
| `setPlaybackRate` | 3 | composite | Log warning + no-op |
| `getPositionNotificationPeriod` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPlaybackHeadPosition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPlaybackRate` | 3 | composite | Return safe default (null/false/0/empty) |
| `isOffloadedPlayback` | 3 | composite | Return safe default (null/false/0/empty) |
| `unregisterStreamEventCallback` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTimestamp` | 3 | composite | Return safe default (null/false/0/empty) |
| `getNativeOutputSampleRate` | 3 | composite | Return safe default (null/false/0/empty) |
| `setPositionNotificationPeriod` | 3 | composite | Log warning + no-op |
| `getDualMonoMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `registerStreamEventCallback` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAudioDescriptionMixLeveldB` | 2 | composite | Return safe default (null/false/0/empty) |
| `setPresentation` | 2 | composite | Log warning + no-op |
| `setBufferSizeInFrames` | 2 | composite | Log warning + no-op |
| `getPerformanceMode` | 2 | composite | Return safe default (null/false/0/empty) |
| `setPlaybackHeadPosition` | 2 | composite | Log warning + no-op |
| `setOffloadEndOfStream` | 2 | composite | Log warning + no-op |
| `setLoopPoints` | 2 | composite | Log warning + no-op |
| `getChannelConfiguration` | 2 | composite | Return safe default (null/false/0/empty) |
| `addOnCodecFormatChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `addOnRoutingChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `attachAuxEffect` | 1 | none | throw UnsupportedOperationException |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `getMinBufferSize` | 1 | none | Return safe default (null/false/0/empty) |
| `getNotificationMarkerPosition` | 1 | none | Return safe default (null/false/0/empty) |
| `getPreferredDevice` | 1 | none | Return safe default (null/false/0/empty) |
| `reloadStaticData` | 1 | none | throw UnsupportedOperationException |
| `removeOnCodecFormatChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `removeOnRoutingChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setAudioDescriptionMixLeveldB` | 1 | none | Log warning + no-op |
| `setAuxEffectSendLevel` | 1 | none | Log warning + no-op |
| `setDualMonoMode` | 1 | none | Log warning + no-op |
| `setNotificationMarkerPosition` | 1 | none | Log warning + no-op |
| `setOffloadDelayPadding` | 1 | none | Log warning + no-op |
| `setPlaybackPositionUpdateListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setPlaybackPositionUpdateListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setPreferredDevice` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.AudioTrack`:

- `android.media.AudioManager` (not yet shimmed)

## Quality Gates

Before marking `android.media.AudioTrack` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 65 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 15 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
