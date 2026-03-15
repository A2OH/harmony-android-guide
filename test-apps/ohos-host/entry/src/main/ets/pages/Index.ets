/**
 * Android-on-OpenHarmony XComponent host page.
 *
 * This page contains a full-screen XComponent of type 'surface' that loads
 * liboh_bridge.so. When the XComponent's surface is created, the native
 * library receives the NativeWindow handle and begins rendering the Android
 * view tree into it.
 *
 * Data flow:
 *   XComponent surface ready
 *     → C++ on_surface_created(OH_NativeXComponent*, void* window)
 *       → Rust shim_surface_set_native_window(id, window)
 *         → SET_BUFFER_GEOMETRY(window, width, height)
 *     → Java Activity.onSurfaceCreated(handle, width, height)
 *       → Activity.renderFrame() on each frame
 *         → decorView.draw(canvas) → OH_Drawing_Bitmap
 *         → surfaceFlush() → memcpy bitmap → NativeWindow buffer → display
 */

@Entry
@Component
struct Index {
  // XComponent configuration
  private xComponentId: string = 'android_surface';
  private xComponentType: XComponentType = XComponentType.SURFACE;

  // Surface dimensions (updated on surface change)
  @State surfaceWidth: number = 0;
  @State surfaceHeight: number = 0;

  // Status text for debugging
  @State statusText: string = 'Initializing...';

  build() {
    Stack() {
      // Full-screen XComponent that loads liboh_bridge.so
      // The native library registers XComponent callbacks in NAPI_Init
      // which fire on_surface_created → Rust → Java rendering pipeline
      XComponent({
        id: this.xComponentId,
        type: this.xComponentType,
        libraryname: 'oh_bridge'
      })
        .width('100%')
        .height('100%')
        .onLoad(() => {
          this.statusText = 'Native library loaded';
        })
        .onDestroy(() => {
          this.statusText = 'Surface destroyed';
        })

      // Debug overlay (only visible during development)
      if (this.statusText !== '') {
        Text(this.statusText)
          .fontSize(12)
          .fontColor('#80FFFFFF')
          .position({ x: 8, y: 8 })
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#000000')
  }
}
