# Touch Event Protocol

## File: /data/local/tmp/a2oh/touch.dat

Binary format — viewer writes, dalvikvm reads:
- 4 bytes: action (0=DOWN, 1=UP, 2=MOVE)
- 4 bytes: x (int, in 480x800 coordinate space)
- 4 bytes: y (int, in 480x800 coordinate space)
- 4 bytes: sequence number (increments each event)

Total: 16 bytes per event, overwritten in place.

## Flow
1. Viewer captures onTouchEvent
2. Scales screen coords to 480x800
3. Writes 16 bytes to touch.dat
4. dalvikvm polls touch.dat in its event loop
5. Dispatches to MiniActivityManager → View tree
