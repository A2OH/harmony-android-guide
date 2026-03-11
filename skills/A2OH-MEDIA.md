# A2OH-MEDIA: Media, Camera, Audio & Image Conversion

## Quick Reference

| Android | OpenHarmony | Import |
|---------|-------------|--------|
| `MediaPlayer` | `AVPlayer` | `import media from '@ohos.multimedia.media'` |
| `MediaRecorder` | `AVRecorder` | `import media from '@ohos.multimedia.media'` |
| `AudioManager` | `AudioManager` | `import audio from '@ohos.multimedia.audio'` |
| `AudioTrack` | `AudioRenderer` | `import audio from '@ohos.multimedia.audio'` |
| `AudioRecord` | `AudioCapturer` | `import audio from '@ohos.multimedia.audio'` |
| `Camera2 API` | `camera module` | `import camera from '@ohos.multimedia.camera'` |
| `ImageView` + Glide/Picasso | `Image` component + `PixelMap` | `import image from '@ohos.multimedia.image'` |
| `ExoPlayer` | `AVPlayer` | `import media from '@ohos.multimedia.media'` |
| `AudioFocus` | `AudioInterrupt` | `import audio from '@ohos.multimedia.audio'` |
| `SoundPool` | `SoundPool` | `import media from '@ohos.multimedia.media'` |

---

## Conversion Rules

### RULE 1: MediaPlayer to AVPlayer

Android `MediaPlayer` uses synchronous calls with `OnPreparedListener`. OH `AVPlayer` is
fully async/state-driven. You MUST listen to `stateChange` and act on state transitions.

**State machine:** `idle` -> `initialized` -> `prepared` -> `playing` -> `paused` -> `stopped` -> `released`

```
// ANDROID
MediaPlayer mp = new MediaPlayer();
mp.setDataSource(path);
mp.setOnPreparedListener(mp -> mp.start());
mp.prepareAsync();
// later: mp.pause(), mp.stop(), mp.release()
```

```
// OPENHARMONY
import media from '@ohos.multimedia.media'

let avPlayer: media.AVPlayer = await media.createAVPlayer()

avPlayer.on('stateChange', async (state: string) => {
  switch (state) {
    case 'initialized':
      avPlayer.prepare()
      break
    case 'prepared':
      avPlayer.play()
      break
    case 'completed':
      avPlayer.stop()
      break
  }
})

avPlayer.on('error', (err) => {
  console.error(`AVPlayer error: ${err.message}`)
  avPlayer.reset()
})

// Trigger state machine — sets state to 'initialized'
avPlayer.url = 'file:///data/storage/el2/base/files/sample.mp3'
// OR for file descriptor:
// avPlayer.fdSrc = { fd: fd, offset: 0, length: fileSize }
```

**Key differences:**
- No `setOnPreparedListener` — use `on('stateChange')` instead
- No `setDataSource()` + `prepare()` — assign `url` or `fdSrc`, which triggers state machine
- `seek(timeMs)` works in `prepared`, `playing`, or `paused` states only
- `setVolume(vol)` takes a single float 0.0-1.0 (not separate L/R channels)

### RULE 2: MediaRecorder to AVRecorder

```
// ANDROID
MediaRecorder recorder = new MediaRecorder();
recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
recorder.setOutputFile(path);
recorder.prepare();
recorder.start();
```

```
// OPENHARMONY
import media from '@ohos.multimedia.media'

let avRecorder: media.AVRecorder = await media.createAVRecorder()

let config: media.AVRecorderConfig = {
  audioSourceType: media.AudioSourceType.AUDIO_SOURCE_TYPE_MIC,
  profile: {
    audioBitrate: 48000,
    audioChannels: 2,
    audioCodec: media.CodecMimeType.AUDIO_AAC,
    audioSampleRate: 48000,
    fileFormat: media.ContainerFormatType.CFT_MPEG_4,
  },
  url: 'fd://' + fd  // file descriptor, not file path
}

await avRecorder.prepare(config)
await avRecorder.start()
// later:
await avRecorder.stop()
await avRecorder.release()
```

**Key differences:**
- Config is a single object, not chained setter calls
- Output is `fd://` file descriptor URI, not a file path
- All methods return Promise — use `await`

### RULE 3: AudioManager and Volume

```
// ANDROID
AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
int vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
am.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
```

```
// OPENHARMONY
import audio from '@ohos.multimedia.audio'

let audioManager: audio.AudioManager = audio.getAudioManager()
let volumeManager: audio.AudioVolumeManager = audioManager.getVolumeManager()

// Volume groups replace stream types
let groupManager = volumeManager.getVolumeGroupManager(audio.DEFAULT_VOLUME_GROUP_ID)
let vol: number = await groupManager.getVolume(audio.AudioVolumeType.MEDIA)
let maxVol: number = await groupManager.getMaxVolume(audio.AudioVolumeType.MEDIA)
```

**Volume type mapping:**
| Android `AudioManager.STREAM_*` | OH `audio.AudioVolumeType` |
|----------------------------------|---------------------------|
| `STREAM_MUSIC` | `MEDIA` |
| `STREAM_RING` | `RINGTONE` |
| `STREAM_VOICE_CALL` | `VOICE_CALL` |
| `STREAM_ALARM` | `ALARM` |

### RULE 4: Audio Focus to AudioInterrupt

Android's `AudioFocus` model (request/abandon focus) is replaced by OH's event-driven
`AudioInterrupt` system. You do NOT request focus. The system notifies you of interruptions.

```
// ANDROID
AudioManager am = getSystemService(AudioManager.class);
AudioManager.OnAudioFocusChangeListener listener = focusChange -> {
    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) { mp.pause(); }
    if (focusChange == AudioManager.AUDIOFOCUS_GAIN) { mp.start(); }
};
am.requestAudioFocus(listener, AudioManager.STREAM_MUSIC,
    AudioManager.AUDIOFOCUS_GAIN);
```

```
// OPENHARMONY
import audio from '@ohos.multimedia.audio'

let audioManager = audio.getAudioManager()
let audioRoutingManager = audioManager.getRoutingManager()

// Register interrupt listener on the AudioRenderer or AVPlayer
audioRenderer.on('audioInterrupt', (event: audio.InterruptEvent) => {
  if (event.forceType === audio.InterruptForceType.INTERRUPT_FORCE) {
    // System already paused/stopped — update UI only
    switch (event.hintType) {
      case audio.InterruptHint.INTERRUPT_HINT_PAUSE:
        isPlaying = false
        break
      case audio.InterruptHint.INTERRUPT_HINT_STOP:
        isPlaying = false
        break
    }
  } else {
    // INTERRUPT_SHARE — app should handle it
    switch (event.hintType) {
      case audio.InterruptHint.INTERRUPT_HINT_PAUSE:
        audioRenderer.pause()
        break
      case audio.InterruptHint.INTERRUPT_HINT_RESUME:
        audioRenderer.start()
        break
    }
  }
})
```

**Key difference:** No `requestAudioFocus()` / `abandonAudioFocus()`. The system manages
focus automatically. Convert all focus request/abandon calls to interrupt event handlers.

### RULE 5: Camera2 API to OH Camera

Android Camera2 uses `CameraManager` -> `CameraDevice` -> `CaptureSession` pipeline.
OH uses a similar but simplified pipeline.

```
// ANDROID (Camera2)
CameraManager cm = getSystemService(CameraManager.class);
String cameraId = cm.getCameraIdList()[0];
cm.openCamera(cameraId, new CameraDevice.StateCallback() {
    public void onOpened(CameraDevice camera) {
        Surface surface = textureView.getSurfaceTexture();
        camera.createCaptureSession(Arrays.asList(surface), ...);
    }
}, handler);
```

```
// OPENHARMONY
import camera from '@ohos.multimedia.camera'

let cameraManager: camera.CameraManager = camera.getCameraManager(this.context)
let cameras: Array<camera.CameraDevice> = cameraManager.getSupportedCameras()

let cameraInput: camera.CameraInput = cameraManager.createCameraInput(cameras[0])
await cameraInput.open()

let previewProfile = cameraManager.getSupportedOutputCapability(cameras[0])
  .previewProfiles[0]
let previewOutput: camera.PreviewOutput =
  cameraManager.createPreviewOutput(previewProfile, surfaceId)

let captureSession: camera.CaptureSession = cameraManager.createCaptureSession()
captureSession.beginConfig()
captureSession.addInput(cameraInput)
captureSession.addOutput(previewOutput)
await captureSession.commitConfig()
await captureSession.start()
```

**Photo capture:**
```
let photoProfile = cameraManager.getSupportedOutputCapability(cameras[0])
  .photoProfiles[0]
let photoOutput: camera.PhotoOutput =
  cameraManager.createPhotoOutput(photoProfile, surfaceId)
// Add to session before commitConfig()

photoOutput.capture()  // takes a photo
```

**Key differences:**
- No callback-based `openCamera` — use `createCameraInput()` + `await open()`
- `surfaceId` comes from XComponent, not `SurfaceTexture`
- Session config is synchronous (`beginConfig`/`addInput`/`addOutput`), commit is async
- Use `getSupportedOutputCapability()` to get profiles, not `StreamConfigurationMap`

### RULE 6: Image Loading (Glide/Picasso/ImageView)

Android image loading libraries (Glide, Picasso, Coil) have no OH equivalent.
Use `Image` component directly with `PixelMap` for decoded images.

```
// ANDROID
// Glide
Glide.with(context).load(url).into(imageView);
// or plain ImageView
imageView.setImageBitmap(bitmap);
imageView.setImageResource(R.drawable.icon);
```

```
// OPENHARMONY
// Simple resource or URL loading — Image component handles it
@Component
struct MyImageView {
  build() {
    Column() {
      // From resource
      Image($r('app.media.icon'))
        .width(200).height(200)
        .objectFit(ImageFit.Contain)

      // From network URL — no library needed
      Image('https://example.com/photo.jpg')
        .width(200).height(200)

      // From PixelMap (decoded bitmap)
      Image(this.pixelMap)
        .width(200).height(200)
    }
  }
}
```

**Decoding images from file/buffer (replaces BitmapFactory):**
```
import image from '@ohos.multimedia.image'

// From file descriptor
let imageSource: image.ImageSource = image.createImageSource(fd)
let pixelMap: image.PixelMap = await imageSource.createPixelMap()

// From ArrayBuffer
let imageSource2 = image.createImageSource(buffer)
let opts: image.DecodingOptions = { desiredSize: { width: 200, height: 200 } }
let pixelMap2 = await imageSource2.createPixelMap(opts)

// Create empty PixelMap
let initOpts: image.InitializationOptions = {
  size: { width: 100, height: 100 },
  pixelFormat: image.PixelMapFormat.RGBA_8888
}
let emptyMap = await image.createPixelMap(new ArrayBuffer(100 * 100 * 4), initOpts)
```

### RULE 7: ExoPlayer to AVPlayer

ExoPlayer is a popular third-party Android media library. It maps to the same `AVPlayer`
as `MediaPlayer`. Key ExoPlayer concepts and their OH equivalents:

| ExoPlayer | OH AVPlayer |
|-----------|------------|
| `SimpleExoPlayer.Builder(ctx).build()` | `await media.createAVPlayer()` |
| `player.setMediaItem(MediaItem.fromUri(uri))` | `avPlayer.url = uri` |
| `player.prepare()` | automatic on url/fdSrc assignment |
| `player.play()` / `player.pause()` | `avPlayer.play()` / `avPlayer.pause()` |
| `Player.Listener.onPlaybackStateChanged` | `avPlayer.on('stateChange', ...)` |
| `Player.Listener.onPlayerError` | `avPlayer.on('error', ...)` |
| `player.seekTo(posMs)` | `avPlayer.seek(posMs)` |
| `player.currentPosition` | `avPlayer.currentTime` (in `on('timeUpdate')`) |
| `player.duration` | `avPlayer.duration` (available in `prepared` state) |
| Playlist / `addMediaItem()` | No built-in playlist — manage array of URLs manually |
| `TrackSelector` | No equivalent — codec negotiation is automatic |

**Important:** ExoPlayer playlists, track selection, and DRM have no direct OH equivalent.
Flag these with `// A2OH-TODO: manual review needed`.

---

## Complete Before/After: Music Player Activity

### Android (Java)
```java
public class MusicActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener focusListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        focusListener = change -> {
            if (change == AudioManager.AUDIOFOCUS_LOSS) pause();
        };

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getFilePath());
            mediaPlayer.setOnPreparedListener(mp -> mp.start());
            mediaPlayer.prepareAsync();
        } catch (IOException e) { e.printStackTrace(); }

        findViewById(R.id.btn_pause).setOnClickListener(v -> pause());
    }

    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }
        audioManager.abandonAudioFocus(focusListener);
    }
}
```

### OpenHarmony (ArkTS)
```typescript
import media from '@ohos.multimedia.media'
import audio from '@ohos.multimedia.audio'

@Entry
@Component
struct MusicPage {
  private avPlayer?: media.AVPlayer
  @State isPlaying: boolean = false

  async aboutToAppear() {
    this.avPlayer = await media.createAVPlayer()

    this.avPlayer.on('stateChange', async (state: string) => {
      switch (state) {
        case 'initialized':
          this.avPlayer?.prepare()
          break
        case 'prepared':
          this.avPlayer?.play()
          this.isPlaying = true
          break
        case 'completed':
          this.avPlayer?.stop()
          this.isPlaying = false
          break
      }
    })

    this.avPlayer.on('error', (err) => {
      console.error(`Player error: ${err.message}`)
    })

    // Audio interrupt replaces AudioFocus
    this.avPlayer.on('audioInterrupt', (event: audio.InterruptEvent) => {
      if (event.hintType === audio.InterruptHint.INTERRUPT_HINT_PAUSE) {
        this.isPlaying = false
      } else if (event.hintType === audio.InterruptHint.INTERRUPT_HINT_RESUME) {
        this.avPlayer?.play()
        this.isPlaying = true
      }
    })

    // Assign URL to start state machine
    this.avPlayer.url = 'file:///data/storage/el2/base/files/song.mp3'
  }

  private pause() {
    if (this.avPlayer && this.isPlaying) {
      this.avPlayer.pause()
      this.isPlaying = false
    }
  }

  aboutToDisappear() {
    this.avPlayer?.release()
    this.avPlayer = undefined
  }

  build() {
    Column() {
      Text(this.isPlaying ? 'Playing' : 'Paused')
        .fontSize(24)
      Button('Pause')
        .onClick(() => this.pause())
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
  }
}
```

---

## Common Pitfalls

1. **Forgetting state machine:** AVPlayer methods only work in specific states. Calling
   `play()` before `prepared` state throws an error. Always use `on('stateChange')`.

2. **File paths vs fd:** OH AVPlayer prefers `fd://` or `fdSrc` for local files. Raw file
   paths need `file:///` prefix. Content URIs do not work directly.

3. **No AudioFocus request:** Do not try to replicate `requestAudioFocus()`. Register
   `audioInterrupt` on the player/renderer instead.

4. **Camera surfaceId:** OH camera needs a `surfaceId` string from an `XComponent`, not a
   `Surface` object. You must use `XComponent({ id: '', type: 'surface' })` in the UI and
   get the ID from `onLoad` callback.

5. **Image component handles URLs:** Unlike Android where you need Glide/Picasso for URL
   images, OH `Image('https://...')` loads network images natively. Do not add an image
   loading library.

6. **Permissions:** Camera and microphone require `ohos.permission.CAMERA` and
   `ohos.permission.MICROPHONE` in `module.json5`, plus runtime permission request via
   `abilityAccessCtrl.createAtManager().requestPermissionsFromUser()`.
