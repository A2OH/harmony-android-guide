# SKILL: android.media.MediaPlayer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaPlayer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaPlayer` |
| **Package** | `android.media` |
| **Total Methods** | 85 |
| **Avg Score** | 4.3 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 30 (35%) |
| **Partial/Composite** | 22 (25%) |
| **No Mapping** | 33 (38%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 5 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 31 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getCurrentPosition` | `int getCurrentPosition()` | 9 | direct | easy | `currentTime` | `@ohos.multimedia.media.AVPlayer` |
| `getDuration` | `int getDuration()` | 9 | direct | easy | `duration` | `@ohos.multimedia.media.AVPlayer` |
| `isPlaying` | `boolean isPlaying()` | 9 | direct | easy | `state` | `@ohos.multimedia.media.AVPlayer` |
| `pause` | `void pause() throws java.lang.IllegalStateException` | 9 | direct | easy | `pause` | `@ohos.multimedia.media.AVPlayer` |
| `prepare` | `void prepare() throws java.io.IOException, java.lang.IllegalStateException` | 9 | direct | easy | `prepare` | `@ohos.multimedia.media.AVPlayer` |
| `prepareAsync` | `void prepareAsync() throws java.lang.IllegalStateException` | 9 | direct | easy | `prepare` | `@ohos.multimedia.media.AVPlayer` |
| `release` | `void release()` | 9 | direct | easy | `release` | `@ohos.multimedia.media.AVPlayer` |
| `reset` | `void reset()` | 9 | direct | easy | `reset` | `@ohos.multimedia.media.AVPlayer` |
| `seekTo` | `void seekTo(long, int)` | 9 | direct | easy | `seek` | `@ohos.multimedia.media.AVPlayer` |
| `seekTo` | `void seekTo(int) throws java.lang.IllegalStateException` | 9 | direct | easy | `seek` | `@ohos.multimedia.media.AVPlayer` |
| `setDataSource` | `void setDataSource(@NonNull android.content.Context, @NonNull android.net.Uri) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.SecurityException` | 9 | direct | easy | `url` | `@ohos.multimedia.media.AVPlayer` |
| `setDataSource` | `void setDataSource(@NonNull android.content.Context, @NonNull android.net.Uri, @Nullable java.util.Map<java.lang.String,java.lang.String>, @Nullable java.util.List<java.net.HttpCookie>) throws java.io.IOException` | 9 | direct | easy | `url` | `@ohos.multimedia.media.AVPlayer` |
| `setDataSource` | `void setDataSource(@NonNull android.content.Context, @NonNull android.net.Uri, @Nullable java.util.Map<java.lang.String,java.lang.String>) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.SecurityException` | 9 | direct | easy | `url` | `@ohos.multimedia.media.AVPlayer` |
| `setDataSource` | `void setDataSource(String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.SecurityException` | 9 | direct | easy | `url` | `@ohos.multimedia.media.AVPlayer` |
| `setDataSource` | `void setDataSource(@NonNull android.content.res.AssetFileDescriptor) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 9 | direct | easy | `url` | `@ohos.multimedia.media.AVPlayer` |
| `setDataSource` | `void setDataSource(java.io.FileDescriptor) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 9 | direct | easy | `url` | `@ohos.multimedia.media.AVPlayer` |
| `setDataSource` | `void setDataSource(java.io.FileDescriptor, long, long) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 9 | direct | easy | `url` | `@ohos.multimedia.media.AVPlayer` |
| `setDataSource` | `void setDataSource(android.media.MediaDataSource) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 9 | direct | easy | `url` | `@ohos.multimedia.media.AVPlayer` |
| `setLooping` | `void setLooping(boolean)` | 9 | direct | easy | `loop` | `@ohos.multimedia.media.AVPlayer` |
| `setOnCompletionListener` | `void setOnCompletionListener(android.media.MediaPlayer.OnCompletionListener)` | 9 | direct | easy | `on('stateChange')` | `@ohos.multimedia.media.AVPlayer` |
| `setOnErrorListener` | `void setOnErrorListener(android.media.MediaPlayer.OnErrorListener)` | 9 | direct | easy | `on('error')` | `@ohos.multimedia.media.AVPlayer` |
| `setOnPreparedListener` | `void setOnPreparedListener(android.media.MediaPlayer.OnPreparedListener)` | 9 | direct | easy | `on('stateChange')` | `@ohos.multimedia.media.AVPlayer` |
| `setVolume` | `void setVolume(float, float)` | 9 | direct | easy | `setVolume` | `@ohos.multimedia.media.AVPlayer` |
| `start` | `void start() throws java.lang.IllegalStateException` | 9 | direct | easy | `play` | `@ohos.multimedia.media.AVPlayer` |
| `stop` | `void stop() throws java.lang.IllegalStateException` | 9 | direct | easy | `stop` | `@ohos.multimedia.media.AVPlayer` |
| `getVideoHeight` | `int getVideoHeight()` | 7 | near | rewrite | `height` | `@ohos.multimedia.media.AVPlayer` |
| `getVideoWidth` | `int getVideoWidth()` | 7 | near | rewrite | `width` | `@ohos.multimedia.media.AVPlayer` |
| `setOnBufferingUpdateListener` | `void setOnBufferingUpdateListener(android.media.MediaPlayer.OnBufferingUpdateListener)` | 7 | near | impossible | `on` | `@ohos.multimedia.media.AVPlayer` |
| `setOnSeekCompleteListener` | `void setOnSeekCompleteListener(android.media.MediaPlayer.OnSeekCompleteListener)` | 7 | near | impossible | `on` | `@ohos.multimedia.media.AVPlayer` |
| `setVideoScalingMode` | `void setVideoScalingMode(int)` | 7 | near | hard | `videoScaleType` | `@ohos.multimedia.media.AVPlayer` |
| `setPlaybackParams` | `void setPlaybackParams(@NonNull android.media.PlaybackParams)` | 5 | partial | hard | `setSpeed` | `@ohos.multimedia.media.AVPlayer` |

## Gap Descriptions (per method)

- **`getCurrentPosition`**: Current time
- **`getDuration`**: Duration
- **`isPlaying`**: Check state
- **`pause`**: Pause
- **`prepare`**: Prepare
- **`prepareAsync`**: Async prepare
- **`release`**: Release
- **`reset`**: Reset
- **`seekTo`**: Seek
- **`seekTo`**: Seek
- **`setDataSource`**: Set media source
- **`setDataSource`**: Set media source
- **`setDataSource`**: Set media source
- **`setDataSource`**: Set media source
- **`setDataSource`**: Set media source
- **`setDataSource`**: Set media source
- **`setDataSource`**: Set media source
- **`setDataSource`**: Set media source
- **`setLooping`**: Loop
- **`setOnCompletionListener`**: State callback
- **`setOnErrorListener`**: Error callback
- **`setOnPreparedListener`**: State callback
- **`setVolume`**: Volume
- **`start`**: Play
- **`stop`**: Stop
- **`getVideoHeight`**: Readonly property vs method
- **`getVideoWidth`**: Readonly property vs method
- **`setOnBufferingUpdateListener`**: on(bufferingUpdate) event
- **`setOnSeekCompleteListener`**: on(seekDone) event
- **`setVideoScalingMode`**: Property vs method
- **`setPlaybackParams`**: setSpeed is subset of PlaybackParams

## Stub APIs (score < 5): 54 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `MediaPlayer` | 5 | partial | throw UnsupportedOperationException |
| `setDisplay` | 5 | partial | Return safe default (null/false/0/empty) |
| `getAudioSessionId` | 4 | partial | Return safe default (null/false/0/empty) |
| `setAudioSessionId` | 4 | partial | Log warning + no-op |
| `getTrackInfo` | 4 | partial | Return safe default (null/false/0/empty) |
| `releaseDrm` | 4 | partial | No-op |
| `getRoutedDevice` | 4 | partial | Return safe default (null/false/0/empty) |
| `setNextMediaPlayer` | 4 | partial | Log warning + no-op |
| `setAudioAttributes` | 3 | composite | Log warning + no-op |
| `setSurface` | 3 | composite | Log warning + no-op |
| `getMetrics` | 3 | composite | Return safe default (null/false/0/empty) |
| `deselectTrack` | 3 | composite | throw UnsupportedOperationException |
| `selectTrack` | 3 | composite | throw UnsupportedOperationException |
| `getDrmInfo` | 3 | composite | Return safe default (null/false/0/empty) |
| `create` | 3 | composite | Return dummy instance / no-op |
| `create` | 3 | composite | Return dummy instance / no-op |
| `create` | 3 | composite | Return dummy instance / no-op |
| `create` | 3 | composite | Return dummy instance / no-op |
| `create` | 3 | composite | Return dummy instance / no-op |
| `getSelectedTrack` | 3 | composite | Return safe default (null/false/0/empty) |
| `isLooping` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnRoutingChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `addTimedTextSource` | 1 | none | Log warning + no-op |
| `addTimedTextSource` | 1 | none | Log warning + no-op |
| `addTimedTextSource` | 1 | none | Log warning + no-op |
| `addTimedTextSource` | 1 | none | Log warning + no-op |
| `attachAuxEffect` | 1 | none | throw UnsupportedOperationException |
| `clearOnMediaTimeDiscontinuityListener` | 1 | none | Return safe default (null/false/0/empty) |
| `clearOnSubtitleDataListener` | 1 | none | Return safe default (null/false/0/empty) |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `getPreferredDevice` | 1 | none | Return safe default (null/false/0/empty) |
| `prepareDrm` | 1 | none | throw UnsupportedOperationException |
| `provideKeyResponse` | 1 | none | Store callback, never fire |
| `removeOnRoutingChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `restoreKeys` | 1 | none | throw UnsupportedOperationException |
| `setAuxEffectSendLevel` | 1 | none | Log warning + no-op |
| `setDrmPropertyString` | 1 | none | Log warning + no-op |
| `setOnDrmConfigHelper` | 1 | none | Log warning + no-op |
| `setOnDrmInfoListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnDrmInfoListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnDrmPreparedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnDrmPreparedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnInfoListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnMediaTimeDiscontinuityListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnMediaTimeDiscontinuityListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnSubtitleDataListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnSubtitleDataListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnTimedMetaDataAvailableListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnTimedTextListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnVideoSizeChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setPreferredDevice` | 1 | none | Log warning + no-op |
| `setScreenOnWhilePlaying` | 1 | none | Log warning + no-op |
| `setSyncParams` | 1 | none | Log warning + no-op |
| `setWakeMode` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaPlayer`:

- `android.content.Context` (already shimmed)
- `android.net.Uri` (already shimmed)

## Quality Gates

Before marking `android.media.MediaPlayer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 85 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 31 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
