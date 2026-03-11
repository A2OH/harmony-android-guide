# OpenHarmony JS/TS SDK API Enumeration — Part 4 (Subdirectory Files)

**Files covered:** 268

**Total API elements:** 3489


### @internal/component/ets/ability_component.d.ts
#### Interfaces
- **AbilityComponentInterface**

### @internal/component/ets/action_sheet.d.ts
#### Interfaces
- **SheetInfo**
  - `title`: string | Resource
  - `icon?`: string | Resource
  - `action`: () => void
- **ActionSheetOptions**
  - `title`: string | Resource
  - `subtitle?`: ResourceStr
  - `message`: string | Resource
  - `enabled?`: boolean
  - `defaultFocus?`: boolean
  - `style?`: DialogButtonStyle
  - `value`: string | Resource
  - `action`: () => void
  - `cancel?`: () => void
  - `sheets`: Array<SheetInfo>
  - `autoCancel?`: boolean
  - `alignment?`: DialogAlignment
  - `offset?`: { dx: number | string | Resource; dy: number | string | Resource }
  - `maskRect?`: Rectangle
  - `showInSubWindow?`: boolean
  - `isModal?`: boolean
  - `backgroundColor?`: ResourceColor
  - `backgroundBlurStyle?`: BlurStyle
#### Classes
- **ActionSheet**

### @internal/component/ets/alert_dialog.d.ts
#### Interfaces
- **AlertDialogButtonOptions**
  - `enabled?`: boolean
  - `defaultFocus?`: boolean
  - `style?`: DialogButtonStyle
  - `value`: ResourceStr
  - `fontColor?`: ResourceColor
  - `backgroundColor?`: ResourceColor
  - `action`: () => void
- **AlertDialogParam**
  - `title?`: ResourceStr
  - `subtitle?`: ResourceStr
  - `message`: ResourceStr
  - `autoCancel?`: boolean
  - `cancel?`: () => void
  - `alignment?`: DialogAlignment
  - `offset?`: Offset
  - `gridCount?`: number
  - `maskRect?`: Rectangle
  - `showInSubWindow?`: boolean
  - `isModal?`: boolean
  - `backgroundColor?`: ResourceColor
  - `backgroundBlurStyle?`: BlurStyle
- **AlertDialogParamWithConfirm**
  - `enabled?`: boolean
  - `defaultFocus?`: boolean
  - `style?`: DialogButtonStyle
  - `value`: ResourceStr
  - `fontColor?`: ResourceColor
  - `backgroundColor?`: ResourceColor
  - `action`: () => void
- **AlertDialogParamWithButtons**
  - `enabled?`: boolean
  - `defaultFocus?`: boolean
  - `style?`: DialogButtonStyle
  - `value`: ResourceStr
  - `fontColor?`: ResourceColor
  - `backgroundColor?`: ResourceColor
  - `action`: () => void
  - `enabled?`: boolean
  - `defaultFocus?`: boolean
  - `style?`: DialogButtonStyle
  - `value`: ResourceStr
  - `fontColor?`: ResourceColor
  - `backgroundColor?`: ResourceColor
  - `action`: () => void
- **AlertDialogParamWithOptions**
  - `buttons`: Array<AlertDialogButtonOptions>
  - `buttonDirection?`: DialogButtonDirection
#### Enums
- **DialogAlignment**
- **DialogButtonDirection**
  - `AUTO` = 0
  - `HORIZONTAL` = 1
  - `VERTICAL` = 2
#### Classes
- **AlertDialog**

### @internal/component/ets/alphabet_indexer.d.ts
#### Interfaces
- **AlphabetIndexerInterface**
#### Enums
- **IndexerAlign**

### @internal/component/ets/animator.d.ts
#### Interfaces
- **AnimatorInterface**
#### Classes
- **SpringProp**
- **SpringMotion**
- **FrictionMotion**
- **ScrollMotion**

### @internal/component/ets/badge.d.ts
#### Interfaces
- **BadgeStyle**
  - `color?`: ResourceColor
  - `fontSize?`: number | string
  - `badgeSize?`: number | string
  - `badgeColor?`: ResourceColor
  - `borderColor?`: ResourceColor
  - `borderWidth?`: Length
  - `fontWeight?`: number | FontWeight | string
- **BadgeParam**
  - `position?`: BadgePosition | Position
  - `style`: BadgeStyle
- **BadgeParamWithNumber**
  - `count`: number
  - `maxCount?`: number
- **BadgeParamWithString**
  - `value`: string
- **BadgeInterface**
#### Enums
- **BadgePosition**

### @internal/component/ets/blank.d.ts
#### Interfaces
- **BlankInterface**

### @internal/component/ets/button.d.ts
#### Interfaces
- **ButtonOptions**
  - `type?`: ButtonType
  - `stateEffect?`: boolean
  - `buttonStyle?`: ButtonStyleMode
  - `controlSize?`: ControlSize
- **ButtonInterface**
- **LabelStyle**
  - `overflow?`: TextOverflow
  - `maxLines?`: number
  - `minFontSize?`: number | ResourceStr
  - `maxFontSize?`: number | ResourceStr
  - `heightAdaptivePolicy?`: TextHeightAdaptivePolicy
  - `font?`: Font
#### Enums
- **ButtonType**
- **ButtonStyleMode**
  - `NORMAL` = 0
  - `EMPHASIZED` = 1
  - `TEXTUAL` = 2
- **ControlSize**
  - `SMALL` = 'small'
  - `NORMAL` = 'normal'

### @internal/component/ets/calendar.d.ts
#### Interfaces
- **CalendarDay**
  - `index`: number
  - `lunarMonth`: string
  - `lunarDay`: string
  - `dayMark`: string
  - `dayMarkValue`: string
  - `year`: number
  - `month`: number
  - `day`: number
  - `isFirstOfLunar`: boolean
  - `hasSchedule`: boolean
  - `markLunarDay`: boolean
- **MonthData**
  - `year`: number
  - `month`: number
  - `data`: CalendarDay[]
- **CurrentDayStyle**
  - `dayColor?`: ResourceColor
  - `lunarColor?`: ResourceColor
  - `markLunarColor?`: ResourceColor
  - `dayFontSize?`: number
  - `lunarDayFontSize?`: number
  - `dayHeight?`: number
  - `dayWidth?`: number
  - `gregorianCalendarHeight?`: number
  - `dayYAxisOffset?`: number
  - `lunarDayYAxisOffset?`: number
  - `underscoreXAxisOffset?`: number
  - `underscoreYAxisOffset?`: number
  - `scheduleMarkerXAxisOffset?`: number
  - `scheduleMarkerYAxisOffset?`: number
  - `colSpace?`: number
  - `dailyFiveRowSpace?`: number
  - `dailySixRowSpace?`: number
  - `lunarHeight?`: number
  - `underscoreWidth?`: number
  - `underscoreLength?`: number
  - `scheduleMarkerRadius?`: number
  - `boundaryRowOffset?`: number
  - `boundaryColOffset?`: number
- **NonCurrentDayStyle**
  - `nonCurrentMonthDayColor?`: ResourceColor
  - `nonCurrentMonthLunarColor?`: ResourceColor
  - `nonCurrentMonthWorkDayMarkColor?`: ResourceColor
  - `nonCurrentMonthOffDayMarkColor?`: ResourceColor
- **TodayStyle**
  - `focusedDayColor?`: ResourceColor
  - `focusedLunarColor?`: ResourceColor
  - `focusedAreaBackgroundColor?`: ResourceColor
  - `focusedAreaRadius?`: number
- **WeekStyle**
  - `weekColor?`: ResourceColor
  - `weekendDayColor?`: ResourceColor
  - `weekendLunarColor?`: ResourceColor
  - `weekFontSize?`: number
  - `weekHeight?`: number
  - `weekWidth?`: number
  - `weekAndDayRowSpace?`: number
- **WorkStateStyle**
  - `workDayMarkColor?`: ResourceColor
  - `offDayMarkColor?`: ResourceColor
  - `workDayMarkSize?`: number
  - `offDayMarkSize?`: number
  - `workStateWidth?`: number
  - `workStateHorizontalMovingDistance?`: number
  - `workStateVerticalMovingDistance?`: number
- **CalendarSelectedDate**
  - `year`: number
  - `month`: number
  - `day`: number
- **CalendarRequestedData**
  - `year`: number
  - `month`: number
  - `currentYear`: number
  - `currentMonth`: number
  - `monthState`: number
- **CalendarInterface**
  - `date`: { year: number; month: number; day: number }
  - `currentData`: MonthData
  - `preData`: MonthData
  - `nextData`: MonthData
  - `controller?`: CalendarController
#### Classes
- **CalendarController**
- **CalendarAttribute**
  - `showLunar()`: CalendarAttribute
  - `showHoliday()`: CalendarAttribute
  - `needSlide()`: CalendarAttribute
  - `startOfWeek()`: CalendarAttribute
  - `offDays()`: CalendarAttribute
  - `direction()`: CalendarAttribute
  - `currentDayStyle()`: CalendarAttribute
  - `nonCurrentDayStyle()`: CalendarAttribute
  - `todayStyle()`: CalendarAttribute
  - `weekStyle()`: CalendarAttribute
  - `workStateStyle()`: CalendarAttribute

### @internal/component/ets/calendar_picker.d.ts
#### Interfaces
- **CalendarOptions**
  - `hintRadius?`: number | Resource
  - `selected?`: Date
- **CalendarPickerInterface**
- **CalendarDialogOptions**
  - `onAccept?`: (value: Date) => void
  - `onCancel?`: () => void
  - `onChange?`: (value: Date) => void
  - `backgroundColor?`: ResourceColor
  - `backgroundBlurStyle?`: BlurStyle
#### Enums
- **CalendarAlign**
  - `START` = 0
  - `CENTER` = 1
  - `END` = 2
#### Classes
- **CalendarPickerDialog**
  - `show()`: void

### @internal/component/ets/canvas.d.ts
#### Interfaces
- **CanvasPattern**
  - `setTransform`: void
- **TextMetrics**
  - `readonly actualBoundingBoxAscent`: number
  - `readonly actualBoundingBoxDescent`: number
  - `readonly actualBoundingBoxLeft`: number
  - `readonly actualBoundingBoxRight`: number
  - `readonly alphabeticBaseline`: number
  - `readonly emHeightAscent`: number
  - `readonly emHeightDescent`: number
  - `readonly fontBoundingBoxAscent`: number
  - `readonly fontBoundingBoxDescent`: number
  - `readonly hangingBaseline`: number
  - `readonly ideographicBaseline`: number
  - `readonly width`: number
  - `readonly height`: number
- **CanvasInterface**
#### Classes
- **CanvasGradient**
  - `addColorStop()`: void
- **CanvasPath**
  - `arc()`: void
  - `arcTo()`: void
  - `bezierCurveTo()`: void
  - `closePath()`: void
  - `ellipse()`: void
  - `lineTo()`: void
  - `moveTo()`: void
  - `quadraticCurveTo()`: void
  - `rect()`: void
- **Path2D**
  - `addPath()`: void
- **ImageBitmap**
  - `close()`: void
- **ImageData**
- **RenderingContextSettings**
- **CanvasRenderer**
  - `drawImage()`: void
  - `drawImage()`: void
  - `drawImage()`: void
  - `beginPath()`: void
  - `clip()`: void
  - `clip()`: void
  - `fill()`: void
  - `fill()`: void
  - `stroke()`: void
  - `stroke()`: void
  - `createLinearGradient()`: CanvasGradient
  - `createPattern()`: CanvasPattern | null
  - `createRadialGradient()`: CanvasGradient
  - `createConicGradient()`: CanvasGradient
  - `createImageData()`: ImageData
  - `createImageData()`: ImageData
  - `getImageData()`: ImageData
  - `getPixelMap()`: PixelMap
  - `putImageData()`: void
  - `putImageData()`: void
  - `getLineDash()`: number[]
  - `setLineDash()`: void
  - `clearRect()`: void
  - `fillRect()`: void
  - `strokeRect()`: void
  - `restore()`: void
  - `save()`: void
  - `fillText()`: void
  - `measureText()`: TextMetrics
  - `strokeText()`: void
  - `getTransform()`: Matrix2D
  - `resetTransform()`: void
  - `rotate()`: void
  - `scale()`: void
  - `setTransform()`: void
  - `setTransform()`: void
  - `transform()`: void
  - `translate()`: void
  - `setPixelMap()`: void
  - `transferFromImageBitmap()`: void
- **CanvasRenderingContext2D**
  - `toDataURL()`: string
- **OffscreenCanvasRenderingContext2D**
  - `toDataURL()`: string
  - `transferToImageBitmap()`: ImageBitmap
- **OffscreenCanvas**
  - `transferToImageBitmap()`: ImageBitmap
  - `getContext()`: OffscreenCanvasRenderingContext2D
#### Type Aliases
- `CanvasFillRule` = "evenodd" | "nonzero"
- `CanvasLineCap` = "butt" | "round" | "square"
- `CanvasLineJoin` = "bevel" | "miter" | "round"
- `CanvasDirection` = "inherit" | "ltr" | "rtl"
- `CanvasTextAlign` = "center" | "end" | "left" | "right" | "start"
- `CanvasTextBaseline` = "alphabetic" | "bottom" | "hanging" | "ideographic" | "middle" | "top"
- `ImageSmoothingQuality` = "high" | "low" | "medium"

### @internal/component/ets/checkbox.d.ts
#### Interfaces
- **CheckboxOptions**
  - `name?`: string
  - `group?`: string
- **CheckboxInterface**

### @internal/component/ets/checkboxgroup.d.ts
#### Interfaces
- **CheckboxGroupOptions**
  - `group?`: string
- **CheckboxGroupResult**
  - `name`: Array<string>
  - `status`: SelectStatus
- **CheckboxGroupInterface**
#### Enums
- **SelectStatus**

### @internal/component/ets/circle.d.ts
#### Interfaces
- **CircleOptions**
  - `width?`: string | number
  - `height?`: string | number
- **CircleInterface**
  - `new`: CircleAttribute

### @internal/component/ets/column.d.ts
#### Interfaces
- **ColumnInterface**

### @internal/component/ets/column_split.d.ts
#### Interfaces
- **ColumnSplitInterface**
- **ColumnSplitDividerStyle**
  - `startMargin?`: Dimension
  - `endMargin?`: Dimension

### @internal/component/ets/common.d.ts
#### Interfaces
- **ComponentOptions**
- **InputCounterOptions**
  - `thresholdPercentage?`: number
  - `highlightBorder?`: boolean
- **EntryOptions**
- **ProvideOptions**
- **AnimatableArithmetic**
  - `plus`: AnimatableArithmetic<T>
  - `subtract`: AnimatableArithmetic<T>
  - `multiply`: AnimatableArithmetic<T>
  - `equals`: boolean
- **Configuration**
  - `readonly colorMode`: string
  - `readonly fontScale`: number
- **Rectangle**
  - `x?`: Length
  - `y?`: Length
  - `width?`: Length
  - `height?`: Length
- **ExpectedFrameRateRange**
- **AnimateParam**
  - `duration?`: number
  - `tempo?`: number
  - `curve?`: Curve | string | ICurve
  - `delay?`: number
  - `iterations?`: number
  - `playMode?`: PlayMode
  - `onFinish?`: () => void
  - `finishCallbackType?`: FinishCallbackType
  - `expectedFrameRateRange?`: ExpectedFrameRateRange
- **ICurve**
  - `interpolate`: number
- **MotionPathOptions**
  - `path`: string
  - `from?`: number
  - `to?`: number
  - `rotatable?`: boolean
- **sharedTransitionOptions**
  - `duration?`: number
  - `curve?`: Curve | string | ICurve
  - `delay?`: number
  - `motionPath?`: MotionPathOptions
  - `zIndex?`: number
  - `type?`: SharedTransitionEffectType
- **GeometryTransitionOptions**
  - `follow?`: boolean
- **TranslateOptions**
  - `x?`: number | string
  - `y?`: number | string
  - `z?`: number | string
- **ScaleOptions**
  - `x?`: number
  - `y?`: number
  - `z?`: number
  - `centerX?`: number | string
  - `centerY?`: number | string
- **AlignRuleOption**
  - `left?`: { anchor: string, align: HorizontalAlign }
  - `right?`: { anchor: string, align: HorizontalAlign }
  - `middle?`: { anchor: string, align: HorizontalAlign }
  - `top?`: { anchor: string, align: VerticalAlign }
  - `bottom?`: { anchor: string, align: VerticalAlign }
  - `center?`: { anchor: string, align: VerticalAlign }
  - `bias?`: Bias
- **RotateOptions**
  - `x?`: number
  - `y?`: number
  - `z?`: number
  - `centerX?`: number | string
  - `centerY?`: number | string
  - `centerZ?`: number
  - `perspective?`: number
  - `angle`: number | string
- **TransitionOptions**
  - `type?`: TransitionType
  - `opacity?`: number
  - `translate?`: TranslateOptions
  - `scale?`: ScaleOptions
  - `rotate?`: RotateOptions
- **PreviewParams**
  - `title?`: string
  - `width?`: number
  - `height?`: number
  - `locale?`: string
  - `colorMode?`: string
  - `deviceType?`: string
  - `dpi?`: number
  - `orientation?`: string
  - `roundScreen?`: boolean
- **ItemDragInfo**
  - `x`: number
  - `y`: number
- **DragItemInfo**
  - `pixelMap?`: PixelMap
  - `builder?`: CustomBuilder
  - `extraInfo?`: string
- **EventTarget**
  - `area`: Area
- **BackgroundBlurStyleOptions**
- **ForegroundBlurStyleOptions**
- **BlurOptions**
  - `grayscale`: [number, number]
- **BlurStyleOptions**
  - `colorMode?`: ThemeColorMode
  - `adaptiveColor?`: AdaptiveColor
  - `scale?`: number
  - `blurOptions?`: BlurOptions
- **BackgroundEffectOptions**
  - `radius`: number
  - `saturation?`: number
  - `brightness?`: number
  - `color?`: ResourceColor
  - `adaptiveColor?`: AdaptiveColor
  - `blurOptions?`: BlurOptions
- **PickerTextStyle**
  - `color?`: ResourceColor
  - `font?`: Font
- **ShadowOptions**
  - `radius`: number | Resource
  - `type?`: ShadowType
  - `color?`: Color | string | Resource | ColoringStrategy
  - `offsetX?`: number | Resource
  - `offsetY?`: number | Resource
  - `fill?`: boolean
- **MultiShadowOptions**
  - `radius?`: number | Resource
  - `offsetX?`: number | Resource
  - `offsetY?`: number | Resource
- **BaseEvent**
  - `target`: EventTarget
  - `timestamp`: number
  - `source`: SourceType
  - `pressure`: number
  - `tiltX`: number
  - `tiltY`: number
  - `sourceTool`: SourceTool
- **BorderImageOption**
- **ClickEvent**
  - `displayX`: number
  - `displayY`: number
  - `windowX`: number
  - `windowY`: number
  - `screenX`: number
  - `screenY`: number
  - `x`: number
  - `y`: number
- **HoverEvent**
  - `stopPropagation`: () => void
- **MouseEvent**
  - `button`: MouseButton
  - `action`: MouseAction
  - `displayX`: number
  - `displayY`: number
  - `windowX`: number
  - `windowY`: number
  - `screenX`: number
  - `screenY`: number
  - `x`: number
  - `y`: number
  - `stopPropagation`: () => void
- **TouchObject**
  - `type`: TouchType
  - `id`: number
  - `displayX`: number
  - `displayY`: number
  - `windowX`: number
  - `windowY`: number
  - `screenX`: number
  - `screenY`: number
  - `x`: number
  - `y`: number
- **HistoricalPoint**
  - `touchObject`: TouchObject
  - `size`: number
  - `force`: number
  - `timestamp`: number
- **TouchEvent**
  - `type`: TouchType
  - `touches`: TouchObject[]
  - `changedTouches`: TouchObject[]
  - `stopPropagation`: () => void
  - `getHistoricalPoints`: Array<HistoricalPoint>
- **PixelMapMock**
  - `release`: void
- **DragEvent**
  - `getDisplayX`: number
  - `getDisplayY`: number
  - `getWindowX`: number
  - `getWindowY`: number
  - `getX`: number
  - `getY`: number
  - `dragBehavior`: DragBehavior
  - `useCustomDropAnimation`: boolean
  - `setData`: void
  - `getData`: UnifiedData
  - `getSummary`: Summary
  - `setResult`: void
  - `getResult`: DragResult
  - `getPreviewRect`: Rectangle
  - `getVelocityX`: number
  - `getVelocityY`: number
  - `getVelocity`: number
- **KeyEvent**
  - `type`: KeyType
  - `keyCode`: number
  - `keyText`: string
  - `keySource`: KeySource
  - `deviceId`: number
  - `metaKey`: number
  - `timestamp`: number
  - `stopPropagation`: () => void
  - `intentionCode`: IntentionCode
- **BindOptions**
  - `backgroundColor?`: ResourceColor
  - `onAppear?`: () => void
  - `onDisappear?`: () => void
- **ContentCoverOptions**
- **SheetTitleOptions**
  - `title`: ResourceStr
  - `subtitle?`: ResourceStr
- **SheetDismiss**
  - `dismiss`: () => void
- **SheetOptions**
  - `height?`: SheetSize | Length
  - `dragBar?`: boolean
  - `maskColor?`: ResourceColor
  - `detents?`: [(SheetSize | Length), (SheetSize | Length)?, (SheetSize | Length)?]
  - `blurStyle?`: BlurStyle
  - `showClose?`: boolean | Resource
  - `preferType?`: SheetType.CENTER | SheetType.POPUP
  - `title?`: SheetTitleOptions | CustomBuilder
  - `shouldDismiss?`: (sheetDismiss: SheetDismiss) => void
  - `enableOutsideInteractive?`: boolean
- **StateStyles**
  - `normal?`: any
  - `pressed?`: any
  - `disabled?`: any
  - `focused?`: any
  - `clicked?`: any
  - `selected?`: object
- **PopupMessageOptions**
  - `textColor?`: ResourceColor
  - `font?`: Font
- **PopupOptions**
  - `message`: string
  - `placementOnTop?`: boolean
  - `placement?`: Placement
  - `value`: string
  - `action`: () => void
  - `value`: string
  - `action`: () => void
  - `arrowOffset?`: Length
  - `showInSubWindow?`: boolean
  - `mask?`: boolean | { color: ResourceColor }
  - `enableArrow?`: boolean
  - `popupColor?`: Color | string | Resource | number
  - `autoCancel?`: boolean
  - `width?`: Dimension
  - `arrowPointPosition?`: ArrowPointPosition
  - `arrowWidth?`: Dimension
  - `arrowHeight?`: Dimension
  - `radius?`: Dimension
  - `shadow?`: ShadowOptions | ShadowStyle
  - `backgroundBlurStyle?`: BlurStyle
- **CustomPopupOptions**
  - `builder`: CustomBuilder
  - `placement?`: Placement
  - `maskColor?`: Color | string | Resource | number
  - `popupColor?`: Color | string | Resource | number
  - `enableArrow?`: boolean
  - `autoCancel?`: boolean
  - `arrowOffset?`: Length
  - `showInSubWindow?`: boolean
  - `mask?`: boolean | { color: ResourceColor }
  - `width?`: Dimension
  - `arrowPointPosition?`: ArrowPointPosition
  - `arrowWidth?`: Dimension
  - `arrowHeight?`: Dimension
  - `radius?`: Dimension
  - `shadow?`: ShadowOptions | ShadowStyle
  - `backgroundBlurStyle?`: BlurStyle
  - `focusable?`: boolean
- **ContextMenuAnimationOptions**
  - `scale?`: AnimationRange<number>
- **ContextMenuOptions**
  - `offset?`: Position
  - `placement?`: Placement
  - `enableArrow?`: boolean
  - `arrowOffset?`: Length
  - `preview?`: MenuPreviewMode | CustomBuilder
  - `onAppear?`: () => void
  - `onDisappear?`: () => void
  - `aboutToAppear?`: () => void
  - `aboutToDisappear?`: () => void
  - `previewAnimationOptions?`: ContextMenuAnimationOptions
  - `backgroundColor?`: ResourceColor
  - `backgroundBlurStyle?`: BlurStyle
- **MenuOptions**
  - `title?`: ResourceStr
  - `showInSubWindow?`: boolean
- **PixelStretchEffectOptions**
  - `top?`: Length
  - `bottom?`: Length
  - `left?`: Length
  - `right?`: Length
- **ClickEffect**
  - `level`: ClickEffectLevel
  - `scale?`: number
- **NestedScrollOptions**
  - `scrollForward`: NestedScrollMode
  - `scrollBackward`: NestedScrollMode
- **MenuElement**
  - `value`: ResourceStr
  - `icon?`: ResourceStr
  - `enabled?`: boolean
  - `action`: () => void
- **AttributeModifier**
  - `applyNormalAttribute?`: void
  - `applyPressedAttribute?`: void
  - `applyFocusedAttribute?`: void
  - `applyDisabledAttribute?`: void
  - `applySelectedAttribute?`: void
- **DragPreviewOptions**
  - `mode?`: DragPreviewMode
- **InvertOptions**
  - `low`: number
  - `high`: number
  - `threshold`: number
  - `thresholdRange`: number
- **CommonInterface**
- **LinearGradient**
  - `angle?`: number | string
  - `direction?`: GradientDirection
  - `colors`: Array<any>
  - `repeating?`: boolean
- **PixelRoundPolicy**
  - `start?`: PixelRoundCalcPolicy
  - `top?`: PixelRoundCalcPolicy
  - `end?`: PixelRoundCalcPolicy
  - `bottom?`: PixelRoundCalcPolicy
- **LinearGradientBlurOptions**
  - `fractionStops`: FractionStop[]
  - `direction`: GradientDirection
- **LayoutBorderInfo**
  - `borderWidth`: EdgeWidths
- **LayoutInfo**
- **LayoutChild**
- **GeometryInfo**
  - `borderWidth`: EdgeWidth
- **Layoutable**
- **Measurable**
- **SizeResult**
- **MeasureResult**
- **RectResult**
  - `x`: number
  - `y`: number
  - `width`: number
  - `height`: number
- **CaretOffset**
  - `index`: number
  - `x`: number
  - `y`: number
- **EdgeEffectOptions**
  - `alwaysEnabled`: boolean
- **BackgroundBrightnessOptions**
  - `rate`: number
  - `lightUpDegree`: number
- **PointLightStyle**
  - `lightSource?`: LightSource
  - `illuminated?`: IlluminatedType
  - `bloom?`: number
- **LightSource**
  - `positionX`: Dimension
  - `positionY`: Dimension
  - `positionZ`: Dimension
  - `intensity`: number
- **KeyframeAnimateParam**
  - `delay?`: number
  - `iterations?`: number
  - `onFinish?`: () => void
- **KeyframeState**
  - `duration`: number
  - `curve?`: Curve | string | ICurve
  - `event`: () => void
#### Enums
- **FinishCallbackType**
  - `REMOVED` = 0
  - `LOGICALLY` = 1
- **TouchTestStrategy**
  - `DEFAULT` = 0
  - `FORWARD_COMPETITION` = 1
  - `FORWARD` = 2
- **TransitionEdge**
- **SourceType**
- **SourceTool**
- **RepeatMode**
- **BlurStyle**
  - `COMPONENT_ULTRA_THIN` = 8
  - `COMPONENT_THIN` = 9
  - `COMPONENT_REGULAR` = 10
  - `COMPONENT_THICK` = 11
  - `COMPONENT_ULTRA_THICK` = 12
- **ThemeColorMode**
- **AdaptiveColor**
- **ModalTransition**
- **ShadowType**
- **ShadowStyle**
- **SafeAreaType**
- **SafeAreaEdge**
- **SheetSize**
  - `FIT_CONTENT` = 2
- **DragBehavior**
- **DragResult**
  - `DRAG_SUCCESSFUL` = 0
  - `DRAG_FAILED` = 1
  - `DRAG_CANCELED` = 2
  - `DROP_ENABLED` = 3
  - `DROP_DISABLED` = 4
- **BlendMode**
  - `NONE` = 0
  - `CLEAR` = 1
  - `r` = s
  - `SRC` = 2
  - `r` = d
  - `DST` = 3
  - `r` = s + (1 - sa) * d
  - `SRC_OVER` = 4
  - `r` = d + (1 - da) * s
  - `DST_OVER` = 5
  - `r` = s * da
  - `SRC_IN` = 6
  - `r` = d * sa
  - `DST_IN` = 7
  - `r` = s * (1 - da)
  - `SRC_OUT` = 8
  - `r` = d * (1 - sa)
  - `DST_OUT` = 9
  - `r` = s * da + d * (1 - sa)
  - `SRC_ATOP` = 10
  - `r` = d * sa + s * (1 - da)
  - `DST_ATOP` = 11
  - `r` = s * (1 - da) + d * (1 - sa)
  - `XOR` = 12
  - `r` = min(s + d
  - `PLUS` = 13
  - `r` = s * d
  - `MODULATE` = 14
  - `r` = s + d - s * d
  - `SCREEN` = 15
  - `OVERLAY` = 16
  - `rc` = s + d - max(s * da
  - `ra` = kSrcOver
  - `DARKEN` = 17
  - `rc` = s + d - min(s * da
  - `ra` = kSrcOver
  - `LIGHTEN` = 18
  - `COLOR_DODGE` = 19
  - `COLOR_BURN` = 20
  - `HARD_LIGHT` = 21
  - `SOFT_LIGHT` = 22
  - `rc` = s + d - 2 * (min(s * da
  - `ra` = kSrcOver
  - `DIFFERENCE` = 23
  - `rc` = s + d - two(s * d)
  - `ra` = kSrcOver
  - `EXCLUSION` = 24
  - `r` = s * (1 - da) + d * (1 - sa) + s * d
  - `MULTIPLY` = 25
  - `HUE` = 26
  - `SATURATION` = 27
  - `COLOR` = 28
  - `LUMINOSITY` = 29
- **BlendApplyType**
  - `FAST` = 0
  - `OFFSCREEN` = 1
- **SheetType**
  - `BOTTOM` = 0
  - `CENTER` = 1
  - `POPUP` = 2
- **MenuPreviewMode**
  - `NONE` = 0
  - `IMAGE` = 1
- **OutlineStyle**
  - `SOLID` = 0
  - `DASHED` = 1
  - `DOTTED` = 2
- **DragPreviewMode**
  - `AUTO` = 1
  - `DISABLE_SCALE` = 2
#### Functions
- `getContext(component?: Object): Context`
- `postCardAction(component: Object, action: Object): void`
- `vp2px(value: number): number`
- `px2vp(value: number): number`
- `fp2px(value: number): number`
- `px2fp(value: number): number`
- `lpx2px(value: number): number`
- `px2lpx(value: number): number`
- `requestFocus(value: string): boolean`
- `setCursor(value: PointerStyle): void`
- `restoreDefault(): void`
#### Classes
- **ProgressMask**
  - `updateProgress()`: void
  - `updateColor()`: void
- **TouchTestInfo**
- **TouchResult**
- **CustomComponent**
  - `build()`: void
  - `aboutToAppear?()`: void
  - `aboutToDisappear?()`: void
  - `aboutToReuse?()`: void
  - `aboutToRecycle?()`: void
  - `onLayout?()`: void
  - `onPlaceChildren?()`: void
  - `onMeasure?()`: void
  - `onMeasureSize?()`: SizeResult
  - `onPageShow?()`: void
  - `onPageHide?()`: void
  - `onFormRecycle?()`: string
  - `onFormRecover?()`: void
  - `onBackPress?()`: void | boolean
  - `pageTransition?()`: void
  - `getUIContext()`: UIContext
  - `queryNavDestinationInfo()`: NavDestinationInfo | undefined
- **View**
  - `create()`: any
- **TextContentControllerBase**
  - `getCaretOffset()`: CaretOffset
  - `getTextContentRect()`: RectResult
  - `getTextContentLineCount()`: number
#### Type Aliases
- `Context` = import('../api/application/Context').default
- `TransitionEffects` = {
  identity: undefined
- `PointerStyle` = import('../api/@ohos.multimodalInput.pointer').default.PointerStyle
- `PixelMap` = import('../api/@ohos.multimedia.image').default.PixelMap
- `UnifiedData` = import('../api/@ohos.data.unifiedDataChannel').default.UnifiedData
- `Summary` = import('../api/@ohos.data.unifiedDataChannel').default.Summary
- `UniformDataType` = import('../api/@ohos.data.uniformTypeDescriptor').default.UniformDataType
- `IntentionCode` = import('../api/@ohos.multimodalInput.intentionCode').IntentionCode
- `AnimationRange` = [from: T, to: T]
- `CustomBuilder` = (() => any) | void
- `FractionStop` = [ number, number ]
- `NavDestinationInfo` = import('../api/@ohos.arkui.observer').default.NavDestinationInfo
- `UIContext` = import('../api/@ohos.arkui.UIContext').UIContext

### @internal/component/ets/common_ts_ets_api.d.ts
#### Interfaces
- **IPropertySubscriber**
  - `id`: number
  - `aboutToBeDeleted`: void
- **ISinglePropertyChangeSubscriber**
  - `hasChanged`: void
- **EnvPropsOptions**
  - `key`: string
  - `defaultValue`: number | string | boolean
- **PersistPropsOptions**
  - `key`: string
  - `defaultValue`: number | string | boolean | Object
#### Classes
- **AppStorage**
  - `Link()`: any
  - `Prop()`: any
  - `Has()`: boolean
  - `has()`: boolean
  - `Delete()`: boolean
  - `delete()`: boolean
  - `Keys()`: IterableIterator<string>
  - `keys()`: IterableIterator<string>
  - `staticClear()`: boolean
  - `Clear()`: boolean
  - `clear()`: boolean
  - `IsMutable()`: boolean
  - `Size()`: number
  - `size()`: number
- **SubscribaleAbstract**
- **Environment**
  - `EnvProps()`: void
  - `envProps()`: void
  - `Keys()`: Array<string>
  - `keys()`: Array<string>
- **PersistentStorage**
  - `DeleteProp()`: void
  - `deleteProp()`: void
  - `PersistProps()`: void
  - `persistProps()`: void
  - `Keys()`: Array<string>
  - `keys()`: Array<string>
- **LocalStorage**
  - `GetShared()`: LocalStorage
  - `getShared()`: LocalStorage
  - `has()`: boolean
  - `keys()`: IterableIterator<string>
  - `size()`: number
  - `delete()`: boolean
  - `clear()`: boolean

### @internal/component/ets/component3d.d.ts
#### Interfaces
- **SceneOptions**
  - `scene?`: Resource
  - `modelType?`: ModelType
- **Component3DInterface**
#### Enums
- **ModelType**
  - `TEXTURE` = 0
  - `SURFACE` = 1

### @internal/component/ets/container_span.d.ts
#### Interfaces
- **ContainerSpanInterface**
#### Classes
- **ContainerSpanAttribute**
  - `textBackgroundStyle()`: ContainerSpanAttribute

### @internal/component/ets/context_menu.d.ts
#### Classes
- **ContextMenu**

### @internal/component/ets/counter.d.ts
#### Interfaces
- **CounterInterface**

### @internal/component/ets/custom_dialog_controller.d.ts
#### Interfaces
- **CustomDialogControllerOptions**
  - `builder`: any
  - `cancel?`: () => void
  - `autoCancel?`: boolean
  - `alignment?`: DialogAlignment
  - `offset?`: Offset
  - `customStyle?`: boolean
  - `gridCount?`: number
  - `maskColor?`: ResourceColor
  - `maskRect?`: Rectangle
  - `openAnimation?`: AnimateParam
  - `closeAnimation?`: AnimateParam
  - `showInSubWindow?`: boolean
  - `backgroundColor?`: ResourceColor
  - `cornerRadius?`: Dimension | BorderRadiuses
  - `isModal?`: boolean
#### Classes
- **CustomDialogController**

### @internal/component/ets/data_panel.d.ts
#### Interfaces
- **DataPanelShadowOptions**
  - `colors?`: Array<ResourceColor | LinearGradient>
- **DataPanelOptions**
  - `values`: number[]
  - `max?`: number
  - `type?`: DataPanelType
- **DataPanelInterface**
#### Enums
- **DataPanelType**
#### Classes
- **LinearGradient**
#### Type Aliases
- `ColorStop` = {
  /**
   * Color property.
   * @type { ResourceColor } color - the color value.
   * @syscap SystemCapability.ArkUI.ArkUI.Full
   * @crossplatform


### @internal/component/ets/date_picker.d.ts
#### Interfaces
- **DatePickerResult**
  - `year?`: number
  - `month?`: number
  - `day?`: number
- **DatePickerOptions**
  - `start?`: Date
  - `end?`: Date
  - `selected?`: Date
- **DatePickerInterface**
- **DatePickerDialogOptions**
  - `lunar?`: boolean
  - `lunarSwitch?`: boolean
  - `showTime?`: boolean
  - `useMilitaryTime?`: boolean
  - `disappearTextStyle?`: PickerTextStyle
  - `textStyle?`: PickerTextStyle
  - `selectedTextStyle?`: PickerTextStyle
  - `maskRect?`: Rectangle
  - `alignment?`: DialogAlignment
  - `offset?`: Offset
  - `onAccept?`: (value: DatePickerResult) => void
  - `onCancel?`: () => void
  - `onChange?`: (value: DatePickerResult) => void
  - `onDateAccept?`: (value: Date) => void
  - `onDateChange?`: (value: Date) => void
  - `backgroundColor?`: ResourceColor
  - `backgroundBlurStyle?`: BlurStyle
#### Classes
- **DatePickerDialog**

### @internal/component/ets/divider.d.ts
#### Interfaces
- **DividerInterface**

### @internal/component/ets/effect_component.d.ts
#### Interfaces
- **EffectComponentInterface**

### @internal/component/ets/ellipse.d.ts
#### Interfaces
- **EllipseInterface**
  - `new`: EllipseAttribute

### @internal/component/ets/enums.d.ts
#### Enums
- **CheckBoxShape**
  - `CIRCLE` = 0
  - `ROUNDED_SQUARE` = 1
- **Color**
- **ColoringStrategy**
  - `INVERT` = 'invert'
  - `AVERAGE` = 'average'
  - `PRIMARY` = 'primary'
- **ImageFit**
- **BorderStyle**
- **LineJoinStyle**
- **TouchType**
- **MouseButton**
- **MouseAction**
- **AnimationStatus**
- **Curve**
- **FillMode**
- **PlayMode**
- **KeyType**
- **KeySource**
- **Edge**
- **Week**
- **Direction**
- **BarState**
- **EdgeEffect**
- **Alignment**
- **TransitionType**
- **RelateType**
- **Visibility**
- **LineCapStyle**
- **Axis**
- **HorizontalAlign**
- **FlexAlign**
- **ItemAlign**
- **FlexDirection**
- **PixelRoundCalcPolicy**
  - `NO_FORCE_ROUND` = 0
  - `FORCE_CEIL` = 1
  - `FORCE_FLOOR` = 2
- **FlexWrap**
- **VerticalAlign**
- **ImageRepeat**
- **ImageSize**
- **GradientDirection**
- **SharedTransitionEffectType**
- **FontStyle**
- **FontWeight**
- **TextAlign**
- **TextOverflow**
- **TextDecorationType**
- **TextCase**
- **TextHeightAdaptivePolicy**
- **ResponseType**
- **HoverEffect**
- **Placement**
- **ArrowPointPosition**
  - `START` = 'Start'
  - `CENTER` = 'Center'
  - `END` = 'End'
- **CopyOptions**
  - `None` = 0
  - `InApp` = 1
  - `LocalDevice` = 2
  - `CROSS_DEVICE` = 3
- **HitTestMode**
- **TitleHeight**
- **ModifierKey**
- **FunctionKey**
- **ImageSpanAlignment**
- **ObscuredReasons**
  - `PLACEHOLDER` = 0
- **TextContentStyle**
- **ClickEffectLevel**
- **XComponentType**
- **NestedScrollMode**
- **RenderFit**
  - `CENTER` = 0
  - `TOP` = 1
  - `BOTTOM` = 2
  - `LEFT` = 3
  - `RIGHT` = 4
  - `TOP_LEFT` = 5
  - `TOP_RIGHT` = 6
  - `BOTTOM_LEFT` = 7
  - `BOTTOM_RIGHT` = 8
  - `RESIZE_FILL` = 9
  - `RESIZE_CONTAIN` = 10
  - `RESIZE_CONTAIN_TOP_LEFT` = 11
  - `RESIZE_CONTAIN_BOTTOM_RIGHT` = 12
  - `RESIZE_COVER` = 13
  - `RESIZE_COVER_TOP_LEFT` = 14
  - `RESIZE_COVER_BOTTOM_RIGHT` = 15
- **DialogButtonStyle**
  - `DEFAULT` = 0
  - `HIGHLIGHT` = 1
- **WordBreak**
  - `NORMAL` = 0
  - `BREAK_ALL` = 1
  - `BREAK_WORD` = 2
- **EllipsisMode**
  - `START` = 0
  - `CENTER` = 1
  - `END` = 2
- **OptionWidthMode**
  - `FIT_CONTENT` = 'fit_content'
  - `FIT_TRIGGER` = 'fit_trigger'
- **IlluminatedType**
  - `NONE` = 0
  - `BORDER` = 1
  - `CONTENT` = 2
  - `BORDER_CONTENT` = 3
  - `BLOOM_BORDER` = 4
  - `BLOOM_BORDER_CONTENT` = 5
- **FoldStatus**
  - `FOLD_STATUS_UNKNOWN` = 0
  - `FOLD_STATUS_EXPANDED` = 1
  - `FOLD_STATUS_FOLDED` = 2
  - `FOLD_STATUS_HALF_FOLDED` = 3
#### Type Aliases
- `Nullable` = T | undefined

### @internal/component/ets/flex.d.ts
#### Interfaces
- **FlexOptions**
  - `direction?`: FlexDirection
  - `wrap?`: FlexWrap
  - `justifyContent?`: FlexAlign
  - `alignItems?`: ItemAlign
  - `alignContent?`: FlexAlign
- **FlexInterface**

### @internal/component/ets/flow_item.d.ts
#### Interfaces
- **FlowItemInterface**

### @internal/component/ets/folder_stack.d.ts
#### Interfaces
- **FolderStackInterface**

### @internal/component/ets/for_each.d.ts
#### Interfaces
- **ForEachInterface**

### @internal/component/ets/form_component.d.ts
#### Interfaces
- **FormComponentInterface**
  - `id`: number
  - `name`: string
  - `bundle`: string
  - `ability`: string
  - `module`: string
  - `dimension?`: FormDimension
  - `temporary?`: boolean
  - `want?`: import('../api/@ohos.app.ability.Want').default
  - `renderingMode?`: FormRenderingMode
#### Enums
- **FormDimension**
  - `DIMENSION_1_1` = 6
- **FormRenderingMode**

### @internal/component/ets/form_link.d.ts
#### Interfaces
- **FormLinkOptions**
  - `action`: string
  - `moduleName?`: string
  - `bundleName?`: string
  - `abilityName?`: string
  - `uri?`: string
  - `params?`: Object
- **FormLinkInterface**

### @internal/component/ets/gauge.d.ts
#### Interfaces
- **GaugeInterface**
- **GaugeShadowOptions**
- **GaugeIndicatorOptions**
  - `icon?`: ResourceStr
  - `space?`: Dimension

### @internal/component/ets/gesture.d.ts
#### Interfaces
- **GestureInfo**
  - `tag?`: string
  - `type`: GestureControl.GestureType
  - `isSystemGesture`: boolean
- **FingerInfo**
  - `id`: number
  - `globalX`: number
  - `globalY`: number
  - `localX`: number
  - `localY`: number
- **BaseGestureEvent**
  - `fingerList`: FingerInfo[]
- **TapGestureEvent**
- **LongPressGestureEvent**
  - `repeat`: boolean
- **PanGestureEvent**
  - `offsetX`: number
  - `offsetY`: number
  - `velocityX`: number
  - `velocityY`: number
  - `velocity`: number
- **PinchGestureEvent**
  - `scale`: number
  - `pinchCenterX`: number
  - `pinchCenterY`: number
- **RotationGestureEvent**
  - `angle`: number
- **SwipeGestureEvent**
  - `angle`: number
  - `speed`: number
- **GestureEvent**
  - `repeat`: boolean
  - `fingerList`: FingerInfo[]
  - `offsetX`: number
  - `offsetY`: number
  - `angle`: number
  - `speed`: number
  - `scale`: number
  - `pinchCenterX`: number
  - `pinchCenterY`: number
  - `velocityX`: number
  - `velocityY`: number
  - `velocity`: number
- **GestureInterface**
  - `tag`: T
- **TapGestureInterface**
- **LongPressGestureInterface**
- **PanGestureInterface**
- **SwipeGestureInterface**
- **PinchGestureInterface**
- **RotationGestureInterface**
- **GestureGroupInterface**
#### Enums
- **PanDirection**
- **SwipeDirection**
- **GestureMode**
- **GestureMask**
- **GestureJudgeResult**
  - `CONTINUE` = 0
  - `REJECT` = 1
- **GestureType**
  - `TAP_GESTURE` = 0
  - `LONG_PRESS_GESTURE` = 1
  - `PAN_GESTURE` = 2
  - `PINCH_GESTURE` = 3
  - `SWIPE_GESTURE` = 4
  - `ROTATION_GESTURE` = 5
  - `DRAG` = 6
  - `CLICK` = 7
#### Classes
- **PanGestureOptions**
#### Type Aliases
- `GestureType` = TapGestureInterface
  | LongPressGestureInterface
  | PanGestureInterface
  | PinchGestureInterface
  | SwipeGestureInterface
  | RotationGestureInter

### @internal/component/ets/grid.d.ts
#### Interfaces
- **GridLayoutOptions**
  - `regularSize`: [number, number]
  - `irregularIndexes?`: number[]
- **GridInterface**
- **ComputedBarAttribute**
  - `totalOffset`: number
  - `totalLength`: number
#### Enums
- **GridDirection**

### @internal/component/ets/gridItem.d.ts
#### Interfaces
- **GridItemOptions**
  - `style?`: GridItemStyle
- **GridItemInterface**
#### Enums
- **GridItemStyle**
  - `NONE` = 0
  - `PLAIN` = 1

### @internal/component/ets/grid_col.d.ts
#### Interfaces
- **GridColColumnOption**
- **GridColOptions**
  - `span?`: number | GridColColumnOption
  - `offset?`: number | GridColColumnOption
  - `order?`: number | GridColColumnOption
- **GridColInterface**

### @internal/component/ets/grid_container.d.ts
#### Interfaces
- **GridContainerOptions**
  - `columns?`: number | "auto"
  - `sizeType?`: SizeType
  - `gutter?`: number | string
  - `margin?`: number | string
- **GridContainerInterface**
#### Enums
- **SizeType**
#### Classes
- **GridContainerAttribute**

### @internal/component/ets/grid_row.d.ts
#### Interfaces
- **GridRowSizeOption**
- **GridRowColumnOption**
- **GutterOption**
- **BreakPoints**
- **GridRowOptions**
  - `gutter?`: Length | GutterOption
  - `columns?`: number | GridRowColumnOption
  - `breakpoints?`: BreakPoints
  - `direction?`: GridRowDirection
- **GridRowInterface**
#### Enums
- **BreakpointsReference**
- **GridRowDirection**

### @internal/component/ets/hyperlink.d.ts
#### Interfaces
- **HyperlinkInterface**

### @internal/component/ets/image.d.ts
#### Interfaces
- **ImageInterface**
- **ImageError**
  - `componentWidth`: number
  - `componentHeight`: number
- **ResizableOptions**
  - `slice?`: EdgeWidths
#### Enums
- **ImageRenderMode**
- **ImageInterpolation**
#### Type Aliases
- `DrawableDescriptor` = import ('../api/@ohos.arkui.drawableDescriptor').DrawableDescriptor
- `ImageErrorCallback` = (error: ImageError) => void

### @internal/component/ets/image_animator.d.ts
#### Interfaces
- **ImageAnimatorInterface**
- **ImageFrameInfo**
  - `src`: string | Resource
  - `width?`: number | string
  - `height?`: number | string
  - `top?`: number | string
  - `left?`: number | string
  - `duration?`: number

### @internal/component/ets/image_common.d.ts
#### Interfaces
- **ImageAnalyzerConfig**
  - `types`: ImageAnalyzerType[]
#### Enums
- **ImageAnalyzerType**
  - `SUBJECT` = 0

### @internal/component/ets/image_span.d.ts
#### Interfaces
- **ImageSpanInterface**

### @internal/component/ets/index-full.d.ts

### @internal/component/ets/inspector.d.ts
#### Functions
- `getInspectorNodes(): object`
- `getInspectorNodeById(id: number): object`
- `unregisterVsyncCallback(): void`
- `setAppBgColor(value: string): void`

### @internal/component/ets/lazy_for_each.d.ts
#### Interfaces
- **DataChangeListener**
  - `onDataReloaded`: void
  - `onDataAdded`: void
  - `onDataAdd`: void
  - `onDataMoved`: void
  - `onDataMove`: void
  - `onDataDeleted`: void
  - `onDataDelete`: void
  - `onDataChanged`: void
  - `onDataChange`: void
- **IDataSource**
  - `totalCount`: number
  - `getData`: any
  - `registerDataChangeListener`: void
  - `unregisterDataChangeListener`: void
- **LazyForEachInterface**

### @internal/component/ets/line.d.ts
#### Interfaces
- **LineInterface**
  - `new`: LineAttribute

### @internal/component/ets/list.d.ts
#### Interfaces
- **ChainAnimationOptions**
  - `minSpace`: Length
  - `maxSpace`: Length
  - `conductivity?`: number
  - `intensity?`: number
  - `edgeEffect?`: ChainEdgeEffect
  - `stiffness?`: number
  - `damping?`: number
- **CloseSwipeActionOptions**
- **ListInterface**
#### Enums
- **ScrollState**
- **ListItemAlign**
- **StickyStyle**
  - `None` = 0
  - `Header` = 1
  - `Footer` = 2
- **ChainEdgeEffect**
- **ScrollSnapAlign**
#### Classes
- **ListScroller**
  - `getItemRectInGroup()`: RectResult
  - `scrollToItemInGroup()`: void
  - `closeAllSwipeActions()`: void

### @internal/component/ets/list_item.d.ts
#### Interfaces
- **SwipeActionItem**
  - `builder?`: CustomBuilder
  - `actionAreaDistance?`: Length
  - `onAction?`: () => void
  - `onEnterActionArea?`: () => void
  - `onExitActionArea?`: () => void
  - `onStateChange?`: (state: SwipeActionState) => void
- **SwipeActionOptions**
  - `start?`: CustomBuilder | SwipeActionItem
  - `end?`: CustomBuilder | SwipeActionItem
  - `edgeEffect?`: SwipeEdgeEffect
  - `onOffsetChange?`: (offset: number) => void
- **ListItemOptions**
  - `style?`: ListItemStyle
- **ListItemInterface**
#### Enums
- **Sticky**
- **EditMode**
- **SwipeEdgeEffect**
- **SwipeActionState**
- **ListItemStyle**
  - `NONE` = 0
  - `CARD` = 1

### @internal/component/ets/list_item_group.d.ts
#### Interfaces
- **ListItemGroupOptions**
  - `header?`: CustomBuilder
  - `footer?`: CustomBuilder
  - `space?`: number | string
  - `style?`: ListItemGroupStyle
- **ListItemGroupInterface**
#### Enums
- **ListItemGroupStyle**
  - `NONE` = 0
  - `CARD` = 1

### @internal/component/ets/loading_progress.d.ts
#### Interfaces
- **LoadingProgressInterface**
#### Enums
- **LoadingProgressStyle**

### @internal/component/ets/location_button.d.ts
#### Interfaces
- **LocationButtonOptions**
  - `icon?`: LocationIconStyle
  - `text?`: LocationDescription
  - `buttonType?`: ButtonType
- **LocationButtonInterface**
#### Enums
- **LocationIconStyle**
  - `FULL_FILLED` = 0
  - `LINES` = 1
- **LocationDescription**
  - `CURRENT_LOCATION` = 0
  - `ADD_LOCATION` = 1
  - `SELECT_LOCATION` = 2
  - `SHARE_LOCATION` = 3
  - `SEND_LOCATION` = 4
  - `LOCATING` = 5
  - `LOCATION` = 6
  - `SEND_CURRENT_LOCATION` = 7
  - `RELOCATION` = 8
  - `PUNCH_IN` = 9
  - `CURRENT_POSITION` = 10
- **LocationButtonOnClickResult**
  - `SUCCESS` = 0
  - `TEMPORARY_AUTHORIZATION_FAILED` = 1

### @internal/component/ets/marquee.d.ts
#### Interfaces
- **MarqueeInterface**

### @internal/component/ets/matrix2d.d.ts
#### Classes
- **Matrix2D**
  - `identity()`: Matrix2D
  - `invert()`: Matrix2D
  - `multiply()`: Matrix2D
  - `rotate()`: Matrix2D
  - `rotate()`: Matrix2D
  - `translate()`: Matrix2D
  - `scale()`: Matrix2D

### @internal/component/ets/menu.d.ts
#### Interfaces
- **MenuInterface**

### @internal/component/ets/menu_item.d.ts
#### Interfaces
- **MenuItemOptions**
  - `startIcon?`: ResourceStr
  - `content?`: ResourceStr
  - `endIcon?`: ResourceStr
  - `labelInfo?`: ResourceStr
  - `builder?`: CustomBuilder
- **MenuItemInterface**

### @internal/component/ets/menu_item_group.d.ts
#### Interfaces
- **MenuItemGroupOptions**
  - `header?`: ResourceStr | CustomBuilder
  - `footer?`: ResourceStr | CustomBuilder
- **MenuItemGroupInterface**

### @internal/component/ets/nav_destination.d.ts
#### Interfaces
- **NavDestinationCommonTitle**
  - `main`: string
  - `sub`: string
- **NavDestinationCustomTitle**
  - `builder`: CustomBuilder
  - `height`: TitleHeight | Length
- **NavDestinationInterface**
- **NavDestinationContext**
  - `pathInfo`: NavPathInfo
  - `pathStack`: NavPathStack
#### Enums
- **NavDestinationMode**
  - `STANDARD` = 0
  - `DIALOG` = 1

### @internal/component/ets/nav_router.d.ts
#### Interfaces
- **RouteInfo**
  - `name`: string
  - `param?`: unknown
- **NavRouterInterface**
#### Enums
- **NavRouteMode**

### @internal/component/ets/navigation.d.ts
#### Interfaces
- **NavigationCommonTitle**
  - `main`: string
  - `sub`: string
- **NavigationCustomTitle**
  - `builder`: CustomBuilder
  - `height`: TitleHeight | Length
- **NavigationMenuItem**
  - `value`: string
  - `icon?`: string
  - `action?`: () => void
- **PopInfo**
  - `info`: NavPathInfo
  - `result`: Object
- **NavigationInterface**
- **ToolbarItem**
  - `value`: ResourceStr
  - `icon?`: ResourceStr
  - `action?`: () => void
  - `status?`: ToolbarItemStatus
  - `activeIcon?`: ResourceStr
- **NavigationTitleOptions**
  - `backgroundColor?`: ResourceColor
  - `backgroundBlurStyle?`: BlurStyle
- **NavigationToolbarOptions**
  - `backgroundColor?`: ResourceColor
  - `backgroundBlurStyle?`: BlurStyle
- **NavigationAnimatedTransition**
  - `timeout?`: number
- **NavigationTransitionProxy**
  - `from`: NavContentInfo
  - `to`: NavContentInfo
  - `finishTransition`: void
- **NavContentInfo**
  - `name?`: string
  - `index`: number
  - `mode?`: NavDestinationMode
#### Enums
- **NavigationMode**
- **NavBarPosition**
- **NavigationTitleMode**
  - `Free` = 0
- **ToolbarItemStatus**
  - `NORMAL` = 0
  - `DISABLED` = 1
  - `ACTIVE` = 2
- **NavigationOperation**
  - `PUSH` = 1
  - `POP` = 2
  - `REPLACE` = 3
#### Classes
- **NavPathInfo**
- **NavPathStack**
  - `pushPath()`: void
  - `pushDestination()`: Promise<void>
  - `pushPathByName()`: void
  - `pushDestinationByName()`: Promise<void>
  - `replacePath()`: void
  - `replacePathByName()`: void
  - `removeByIndexes()`: number
  - `removeByName()`: number
  - `pop()`: NavPathInfo | undefined
  - `pop()`: NavPathInfo | undefined
  - `popToName()`: number
  - `popToName()`: number
  - `popToIndex()`: void
  - `popToIndex()`: void
  - `moveToTop()`: number
  - `moveIndexToTop()`: void
  - `clear()`: void
  - `getAllPathName()`: Array<string>
  - `getParamByIndex()`: unknown | undefined
  - `getParamByName()`: Array<unknown>
  - `getIndexByName()`: Array<number>
  - `getParent()`: NavPathStack | null
  - `size()`: number
  - `disableAnimation()`: void

### @internal/component/ets/navigator.d.ts
#### Interfaces
- **NavigatorInterface**
#### Enums
- **NavigationType**

### @internal/component/ets/node_container.d.ts
#### Interfaces
- **NodeContainerInterface**

### @internal/component/ets/page_transition.d.ts
#### Interfaces
- **PageTransitionOptions**
  - `type?`: RouteType
  - `duration?`: number
  - `curve?`: Curve | string | ICurve
  - `delay?`: number
- **PageTransitionEnterInterface**
- **PageTransitionExitInterface**
#### Enums
- **RouteType**
- **SlideEffect**

### @internal/component/ets/panel.d.ts
#### Interfaces
- **PanelInterface**
#### Enums
- **PanelMode**
- **PanelType**
  - `Minibar` = 0
  - `Foldable` = 1
  - `Temporary` = 2
  - `CUSTOM` = 3
- **PanelHeight**
  - `WRAP_CONTENT` = 'wrapContent'

### @internal/component/ets/particle.d.ts
#### Interfaces
- **ParticleOptions**
  - `emitter`: EmitterOptions<PARTICLE>
  - `color?`: ParticleColorPropertyOptions<COLOR_UPDATER>
  - `opacity?`: ParticlePropertyOptions<number, OPACITY_UPDATER>
  - `scale?`: ParticlePropertyOptions<number, SCALE_UPDATER>
  - `speed`: [number, number]
  - `angle`: [number, number]
  - `speed?`: ParticlePropertyOptions<number, ACC_SPEED_UPDATER>
  - `angle?`: ParticlePropertyOptions<number, ACC_ANGLE_UPDATER>
  - `spin?`: ParticlePropertyOptions<number, SPIN_UPDATER>
- **PointParticleParameters**
  - `radius`: VP
- **ImageParticleParameters**
  - `src`: ResourceStr
  - `size`: [Dimension, Dimension]
  - `objectFit?`: ImageFit
- **ParticleConfigs**
- **EmitterOptions**
  - `type`: PARTICLE
  - `config`: ParticleConfigs[PARTICLE]
  - `count`: number
  - `lifetime?`: number
  - `emitRate?`: number
  - `shape?`: ParticleEmitterShape
  - `position?`: [Dimension, Dimension]
  - `size?`: [Dimension, Dimension]
- **ParticlePropertyUpdaterConfigs**
- **ParticlePropertyOptions**
  - `range`: [TYPE, TYPE]
  - `type`: UPDATER
  - `config`: ParticlePropertyUpdaterConfigs<TYPE>[UPDATER]
- **ParticleColorPropertyUpdaterConfigs**
  - `r`: [number, number]
  - `g`: [number, number]
  - `b`: [number, number]
  - `a`: [number, number]
- **ParticleColorPropertyOptions**
  - `range`: [ResourceColor, ResourceColor]
  - `type`: UPDATER
  - `config`: ParticleColorPropertyUpdaterConfigs[UPDATER]
- **ParticlePropertyAnimation**
  - `from`: T
  - `to`: T
  - `startMillis`: number
  - `endMillis`: number
  - `curve?`: Curve | ICurve
- **ParticleInterface**
#### Enums
- **ParticleType**
  - `POINT` = 'point'
  - `IMAGE` = 'image'
- **ParticleEmitterShape**
  - `RECTANGLE` = 'rectangle'
  - `CIRCLE` = 'circle'
  - `ELLIPSE` = 'ellipse'
- **ParticleUpdater**
  - `NONE` = 'none'
  - `RANDOM` = 'random'
  - `CURVE` = 'curve'

### @internal/component/ets/paste_button.d.ts
#### Interfaces
- **PasteButtonOptions**
  - `icon?`: PasteIconStyle
  - `text?`: PasteDescription
  - `buttonType?`: ButtonType
- **PasteButtonInterface**
#### Enums
- **PasteIconStyle**
  - `LINES` = 0
- **PasteDescription**
  - `PASTE` = 0
- **PasteButtonOnClickResult**
  - `SUCCESS` = 0
  - `TEMPORARY_AUTHORIZATION_FAILED` = 1

### @internal/component/ets/path.d.ts
#### Interfaces
- **PathInterface**
  - `new`: PathAttribute

### @internal/component/ets/pattern_lock.d.ts
#### Interfaces
- **PatternLockInterface**
#### Enums
- **PatternLockChallengeResult**
  - `CORRECT` = 1
  - `WRONG` = 2
#### Classes
- **PatternLockController**
  - `setChallengeResult()`: void

### @internal/component/ets/plugin_component.d.ts
#### Interfaces
- **PluginComponentTemplate**
  - `source`: string
  - `bundleName`: string
- **PluginComponentInterface**

### @internal/component/ets/polygon.d.ts
#### Interfaces
- **PolygonInterface**
  - `new`: PolygonAttribute

### @internal/component/ets/polyline.d.ts
#### Interfaces
- **PolylineInterface**
  - `new`: PolylineAttribute

### @internal/component/ets/progress.d.ts
#### Interfaces
- **ProgressOptions**
  - `value`: number
  - `total?`: number
- **ProgressStyleOptions**
  - `strokeWidth?`: Length
  - `scaleCount?`: number
  - `scaleWidth?`: Length
- **CommonProgressStyleOptions**
  - `enableSmoothEffect?`: boolean
- **ScanEffectOptions**
  - `enableScanEffect?`: boolean
- **EclipseStyleOptions**
- **ScaleRingStyleOptions**
  - `strokeWidth?`: Length
  - `scaleWidth?`: Length
  - `scaleCount?`: number
- **RingStyleOptions**
  - `strokeWidth?`: Length
  - `shadow?`: boolean
  - `status?`: ProgressStatus
- **LinearStyleOptions**
  - `strokeWidth?`: Length
  - `strokeRadius?`: PX | VP | LPX | Resource
- **CapsuleStyleOptions**
  - `borderColor?`: ResourceColor
  - `borderWidth?`: Length
  - `content?`: string
  - `font?`: Font
  - `fontColor?`: ResourceColor
  - `showDefaultPercentage?`: boolean
- **ProgressStyleMap**
- **ProgressInterface**
#### Enums
- **ProgressType**
  - `Linear` = 0
  - `Ring` = 1
  - `Eclipse` = 2
  - `ScaleRing` = 3
  - `Capsule` = 4
- **ProgressStatus**
- **ProgressStyle**

### @internal/component/ets/qrcode.d.ts
#### Interfaces
- **QRCodeInterface**

### @internal/component/ets/radio.d.ts
#### Interfaces
- **RadioOptions**
  - `group`: string
  - `value`: string
- **RadioStyle**
  - `checkedBackgroundColor?`: ResourceColor
  - `uncheckedBorderColor?`: ResourceColor
  - `indicatorColor?`: ResourceColor
- **RadioInterface**

### @internal/component/ets/rating.d.ts
#### Interfaces
- **RatingInterface**

### @internal/component/ets/rect.d.ts
#### Interfaces
- **RectInterface**
  - `new`: RectAttribute
  - `width?`: number | string
  - `height?`: number | string
  - `radius?`: number | string | Array<any>
  - `width?`: number | string
  - `height?`: number | string
  - `radiusWidth?`: number | string
  - `radiusHeight?`: number | string

### @internal/component/ets/refresh.d.ts
#### Interfaces
- **RefreshOptions**
  - `refreshing`: boolean
  - `offset?`: number | string
  - `friction?`: number | string
  - `builder?`: CustomBuilder
- **RefreshInterface**
#### Enums
- **RefreshStatus**

### @internal/component/ets/relative_container.d.ts
#### Interfaces
- **RelativeContainerInterface**

### @internal/component/ets/remote_window.d.ts
#### Interfaces
- **RRect**
  - `left`: number
  - `top`: number
  - `width`: number
  - `height`: number
  - `radius`: number
- **WindowAnimationTarget**
  - `readonly bundleName`: string
  - `readonly abilityName`: string
  - `readonly windowBounds`: RRect
  - `readonly missionId`: number
- **RemoteWindowInterface**

### @internal/component/ets/rich_editor.d.ts
#### Interfaces
- **RichEditorSpanPosition**
  - `spanIndex`: number
  - `spanRange`: [number, number]
- **RichEditorTextStyle**
  - `fontColor?`: ResourceColor
  - `fontSize?`: Length | number
  - `fontStyle?`: FontStyle
  - `fontWeight?`: number | FontWeight | string
  - `fontFamily?`: ResourceStr
  - `decoration?`: { type: TextDecorationType; color?: ResourceColor; }
  - `textShadow?`: ShadowOptions | Array<ShadowOptions>
- **LeadingMarginPlaceholder**
  - `pixelMap`: PixelMap
  - `size`: [Dimension, Dimension]
- **RichEditorParagraphStyle**
  - `textAlign?`: TextAlign
  - `leadingMargin?`: Dimension | LeadingMarginPlaceholder
- **PasteEvent**
  - `preventDefault?`: () => void
- **RichEditorTextSpan**
  - `spanPosition`: RichEditorSpanPosition
  - `value`: string
  - `textStyle?`: RichEditorTextStyle
- **RichEditorLayoutStyle**
  - `margin?`: Dimension | Margin
  - `borderRadius?`: Dimension | BorderRadiuses
- **RichEditorImageSpanStyle**
  - `size?`: [Dimension, Dimension]
  - `verticalAlign?`: ImageSpanAlignment
  - `objectFit?`: ImageFit
  - `layoutStyle?`: RichEditorLayoutStyle
- **RichEditorSymbolSpanStyle**
  - `fontSize?`: number | string | Resource
  - `fontColor?`: Array<ResourceColor>
  - `fontWeight?`: number | FontWeight | string
  - `effectStrategy?`: SymbolEffectStrategy
  - `renderingStrategy?`: SymbolRenderingStrategy
- **RichEditorTextStyleResult**
  - `fontColor`: ResourceColor
  - `fontSize`: number
  - `fontStyle`: FontStyle
  - `fontWeight`: number
  - `fontFamily`: string
  - `decoration`: { type: TextDecorationType; color: ResourceColor; }
- **RichEditorParagraphResult**
  - `style`: RichEditorParagraphStyle
  - `range`: [number, number]
- **RichEditorSymbolSpanStyleResult**
  - `fontSize`: number | string | Resource
  - `fontColor`: Array<ResourceColor>
  - `fontWeight`: number | FontWeight | string
  - `effectStrategy`: SymbolEffectStrategy
  - `renderingStrategy`: SymbolRenderingStrategy
- **RichEditorTextSpanResult**
  - `spanPosition`: RichEditorSpanPosition
  - `value`: string
  - `textStyle`: RichEditorTextStyleResult
  - `offsetInSpan`: [number, number]
  - `symbolSpanStyle?`: RichEditorSymbolSpanStyle
  - `valueResource?`: Resource
- **RichEditorImageSpanStyleResult**
  - `size`: [number, number]
  - `verticalAlign`: ImageSpanAlignment
  - `objectFit`: ImageFit
- **RichEditorImageSpanResult**
  - `spanPosition`: RichEditorSpanPosition
  - `valuePixelMap?`: PixelMap
  - `valueResourceStr?`: ResourceStr
  - `imageStyle`: RichEditorImageSpanStyleResult
  - `offsetInSpan`: [number, number]
- **RichEditorImageSpan**
  - `spanPosition`: RichEditorSpanPosition
  - `value`: PixelMap | ResourceStr
  - `imageStyle?`: RichEditorImageSpanStyle
- **RichEditorRange**
  - `start?`: number
  - `end?`: number
- **RichEditorGesture**
  - `onClick?`: (event: ClickEvent) => void
  - `onLongPress?`: (event: GestureEvent) => void
- **RichEditorTextSpanOptions**
  - `offset?`: number
  - `style?`: RichEditorTextStyle
  - `paragraphStyle?`: RichEditorParagraphStyle
  - `gesture?`: RichEditorGesture
- **RichEditorImageSpanOptions**
  - `offset?`: number
  - `imageStyle?`: RichEditorImageSpanStyle
  - `gesture?`: RichEditorGesture
- **RichEditorBuilderSpanOptions**
  - `offset?`: number
- **RichEditorSpanStyleOptions**
- **RichEditorParagraphStyleOptions**
  - `style`: RichEditorParagraphStyle
- **RichEditorUpdateTextSpanStyleOptions**
  - `textStyle`: RichEditorTextStyle
- **RichEditorUpdateImageSpanStyleOptions**
  - `imageStyle`: RichEditorImageSpanStyle
- **RichEditorUpdateSymbolSpanStyleOptions**
  - `symbolStyle`: RichEditorSymbolSpanStyle
- **RichEditorSymbolSpanOptions**
  - `offset?`: number
  - `style?`: RichEditorSymbolSpanStyle
- **RichEditorSelection**
  - `selection`: [number, number]
  - `spans`: Array<RichEditorTextSpanResult | RichEditorImageSpanResult>
- **RichEditorInsertValue**
  - `insertOffset`: number
  - `insertValue`: string
- **RichEditorDeleteValue**
  - `offset`: number
  - `direction`: RichEditorDeleteDirection
  - `length`: number
  - `richEditorDeleteSpans`: Array<RichEditorTextSpanResult | RichEditorImageSpanResult>
- **RichEditorOptions**
  - `controller`: RichEditorController
- **SelectionMenuOptions**
  - `onAppear?`: () => void
  - `onDisappear?`: () => void
- **RichEditorInterface**
#### Enums
- **RichEditorDeleteDirection**
- **RichEditorSpanType**
  - `TEXT` = 0
  - `IMAGE` = 1
  - `MIXED` = 2
- **RichEditorResponseType**
  - `RIGHT_CLICK` = 0
  - `LONG_PRESS` = 1
  - `SELECT` = 2
#### Classes
- **RichEditorController**
  - `getCaretOffset()`: number
  - `setCaretOffset()`: boolean
  - `addTextSpan()`: number
  - `addImageSpan()`: number
  - `addBuilderSpan()`: number
  - `addSymbolSpan()`: number
  - `updateSpanStyle()`: void
  - `updateParagraphStyle()`: void
  - `deleteSpans()`: void
  - `getSpans()`: Array<RichEditorImageSpanResult | RichEditorTextSpanResult>
  - `getParagraphs()`: Array<RichEditorParagraphResult>
  - `closeSelectionMenu()`: void
  - `getTypingStyle()`: RichEditorTextStyle
  - `setTypingStyle()`: void
  - `setSelection()`: void
  - `getSelection()`: RichEditorSelection

### @internal/component/ets/rich_text.d.ts
#### Interfaces
- **RichTextInterface**

### @internal/component/ets/root_scene.d.ts
#### Interfaces
- **RootSceneSession**
- **RootSceneInterface**

### @internal/component/ets/row.d.ts
#### Interfaces
- **RowInterface**

### @internal/component/ets/row_split.d.ts
#### Interfaces
- **RowSplitInterface**

### @internal/component/ets/save_button.d.ts
#### Interfaces
- **SaveButtonOptions**
  - `icon?`: SaveIconStyle
  - `text?`: SaveDescription
  - `buttonType?`: ButtonType
- **SaveButtonInterface**
#### Enums
- **SaveIconStyle**
  - `FULL_FILLED` = 0
  - `LINES` = 1
- **SaveDescription**
  - `DOWNLOAD` = 0
  - `DOWNLOAD_FILE` = 1
  - `SAVE` = 2
  - `SAVE_IMAGE` = 3
  - `SAVE_FILE` = 4
  - `DOWNLOAD_AND_SHARE` = 5
  - `RECEIVE` = 6
  - `CONTINUE_TO_RECEIVE` = 7
- **SaveButtonOnClickResult**
  - `SUCCESS` = 0
  - `TEMPORARY_AUTHORIZATION_FAILED` = 1

### @internal/component/ets/screen.d.ts
#### Interfaces
- **ScreenInterface**

### @internal/component/ets/scroll.d.ts
#### Interfaces
- **OffsetResult**
  - `xOffset`: number
  - `yOffset`: number
- **ScrollSnapOptions**
  - `snapAlign`: ScrollSnapAlign
  - `snapPagination?`: Dimension | Array<Dimension>
  - `enableSnapToStart?`: boolean
  - `enableSnapToEnd?`: boolean
- **ScrollInterface**
#### Enums
- **ScrollDirection**
- **ScrollAlign**
#### Classes
- **Scroller**
  - `currentOffset()`: OffsetResult
  - `isAtEnd()`: boolean
  - `getItemRect()`: RectResult

### @internal/component/ets/scroll_bar.d.ts
#### Interfaces
- **ScrollBarOptions**
  - `scroller`: Scroller
  - `direction?`: ScrollBarDirection
  - `state?`: BarState
- **ScrollBarInterface**
#### Enums
- **ScrollBarDirection**

### @internal/component/ets/search.d.ts
#### Interfaces
- **SearchInterface**
  - `value?`: string
  - `placeholder?`: ResourceStr
  - `icon?`: string
- **IconOptions**
  - `size?`: Length
  - `color?`: ResourceColor
  - `src?`: ResourceStr
- **CaretStyle**
- **SearchButtonOptions**
  - `fontSize?`: Length
  - `fontColor?`: ResourceColor
#### Enums
- **CancelButtonStyle**
- **SearchType**
  - `NORMAL` = 0
  - `NUMBER` = 2
  - `PHONE_NUMBER` = 3
  - `EMAIL` = 5
#### Classes
- **SearchController**
  - `caretPosition()`: void
  - `stopEditing()`: void

### @internal/component/ets/security_component.d.ts
#### Enums
- **SecurityComponentLayoutDirection**
  - `HORIZONTAL` = 0
  - `VERTICAL` = 1

### @internal/component/ets/select.d.ts
#### Interfaces
- **SelectOption**
  - `value`: ResourceStr
  - `icon?`: ResourceStr
- **SelectInterface**
#### Enums
- **ArrowPosition**
  - `END` = 0
  - `START` = 1
- **MenuAlignType**

### @internal/component/ets/shape.d.ts
#### Interfaces
- **ShapeInterface**
  - `new`: ShapeAttribute

### @internal/component/ets/sidebar.d.ts
#### Interfaces
- **ButtonStyle**
  - `left?`: number
  - `top?`: number
  - `width?`: number
  - `height?`: number
  - `shown`: string | PixelMap | Resource
  - `hidden`: string | PixelMap | Resource
  - `switching?`: string | PixelMap | Resource
- **SideBarContainerInterface**
- **DividerStyle**
  - `strokeWidth`: Length
  - `color?`: ResourceColor
  - `startMargin?`: Length
  - `endMargin?`: Length
#### Enums
- **SideBarContainerType**
- **SideBarPosition**

### @internal/component/ets/slider.d.ts
#### Interfaces
- **SliderOptions**
  - `value?`: number
  - `min?`: number
  - `max?`: number
  - `step?`: number
  - `style?`: SliderStyle
  - `direction?`: Axis
  - `reverse?`: boolean
- **SliderBlockStyle**
  - `type`: SliderBlockType
  - `image?`: ResourceStr
  - `shape?`: CircleAttribute | EllipseAttribute | PathAttribute | RectAttribute
- **SliderInterface**
#### Enums
- **SliderStyle**
- **SliderChangeMode**
- **SliderBlockType**

### @internal/component/ets/span.d.ts
#### Interfaces
- **TextBackgroundStyle**
  - `color?`: ResourceColor
  - `radius?`: Dimension | BorderRadiuses
- **SpanInterface**

### @internal/component/ets/stack.d.ts
#### Interfaces
- **StackInterface**

### @internal/component/ets/state_management.d.ts
#### Enums
- **ColorMode**
  - `LIGHT` = 0
- **LayoutDirection**
#### Classes
- **Storage**
  - `get()`: string | undefined
  - `set()`: void
  - `clear()`: void
  - `delete()`: void

### @internal/component/ets/stepper.d.ts
#### Interfaces
- **StepperInterface**

### @internal/component/ets/stepper_item.d.ts
#### Interfaces
- **StepperItemInterface**
#### Enums
- **ItemState**

### @internal/component/ets/swiper.d.ts
#### Interfaces
- **ArrowStyle**
  - `showBackground?`: boolean
  - `isSidebarMiddle?`: boolean
  - `backgroundSize?`: Length
  - `backgroundColor?`: ResourceColor
  - `arrowSize?`: Length
  - `arrowColor?`: ResourceColor
- **SwiperInterface**
- **IndicatorStyle**
  - `left?`: Length
  - `top?`: Length
  - `right?`: Length
  - `bottom?`: Length
  - `size?`: Length
  - `mask?`: boolean
  - `color?`: ResourceColor
  - `selectedColor?`: ResourceColor
- **SwiperAnimationEvent**
  - `currentOffset`: number
  - `targetOffset`: number
  - `velocity`: number
#### Enums
- **SwiperDisplayMode**
- **SwiperNestedScrollMode**
  - `SELF_ONLY` = 0
  - `SELF_FIRST` = 1
#### Classes
- **SwiperController**
#### Type Aliases
- `SwiperAutoFill` = {
  /**
   * Set minSize size.
   *
   * @syscap SystemCapability.ArkUI.ArkUI.Full
   * @since 10
   * @form
   */
  /**
   * Set minSize size.
   *
 

### @internal/component/ets/symbol_span.d.ts
#### Interfaces
- **SymbolSpanInterface**

### @internal/component/ets/symbolglyph.d.ts
#### Interfaces
- **SymbolGlyphInterface**
#### Enums
- **SymbolRenderingStrategy**
  - `SINGLE` = 0
  - `MULTIPLE_COLOR` = 1
  - `MULTIPLE_OPACITY` = 2
- **SymbolEffectStrategy**
  - `NONE` = 0
  - `SCALE` = 1
  - `HIERARCHICAL` = 2

### @internal/component/ets/tab_content.d.ts
#### Interfaces
- **IndicatorStyle**
  - `color?`: ResourceColor
  - `height?`: Length
  - `width?`: Length
  - `borderRadius?`: Length
  - `marginTop?`: Length
- **BoardStyle**
  - `borderRadius?`: Length
- **LabelStyle**
  - `overflow?`: TextOverflow
  - `maxLines?`: number
  - `minFontSize?`: number | ResourceStr
  - `maxFontSize?`: number | ResourceStr
  - `heightAdaptivePolicy?`: TextHeightAdaptivePolicy
  - `font?`: Font
- **TabContentInterface**
#### Enums
- **SelectedMode**
- **LayoutMode**
  - `AUTO` = 0
  - `VERTICAL` = 1
  - `HORIZONTAL` = 2
#### Classes
- **SubTabBarStyle**
  - `of()`: SubTabBarStyle
  - `indicator()`: SubTabBarStyle
  - `selectedMode()`: SubTabBarStyle
  - `board()`: SubTabBarStyle
  - `labelStyle()`: SubTabBarStyle
  - `padding()`: SubTabBarStyle
  - `id()`: SubTabBarStyle
- **BottomTabBarStyle**
  - `of()`: BottomTabBarStyle
  - `labelStyle()`: BottomTabBarStyle
  - `padding()`: BottomTabBarStyle
  - `layoutMode()`: BottomTabBarStyle
  - `verticalAlign()`: BottomTabBarStyle
  - `symmetricExtensible()`: BottomTabBarStyle
  - `id()`: BottomTabBarStyle

### @internal/component/ets/tabs.d.ts
#### Interfaces
- **TabsInterface**
- **DividerStyle**
  - `strokeWidth`: Length
  - `color?`: ResourceColor
  - `startMargin?`: Length
  - `endMargin?`: Length
- **TabsAnimationEvent**
  - `currentOffset`: number
  - `targetOffset`: number
  - `velocity`: number
- **BarGridColumnOptions**
  - `sm?`: number
  - `md?`: number
  - `lg?`: number
  - `margin?`: Dimension
  - `gutter?`: Dimension
- **ScrollableBarModeOptions**
  - `margin?`: Dimension
  - `nonScrollableLayoutStyle?`: LayoutStyle
- **TabContentAnimatedTransition**
  - `timeout?`: number
  - `transition`: (proxy: TabContentTransitionProxy) => void
- **TabContentTransitionProxy**
  - `from`: number
  - `to`: number
  - `finishTransition`: void
#### Enums
- **BarMode**
  - `Scrollable` = 0
  - `Fixed` = 1
- **BarPosition**
- **LayoutStyle**
  - `ALWAYS_CENTER` = 0
  - `ALWAYS_AVERAGE_SPLIT` = 1
  - `SPACE_BETWEEN_OR_CENTER` = 2
#### Classes
- **TabsController**
  - `changeIndex()`: void

### @internal/component/ets/text.d.ts
#### Interfaces
- **TextInterface**
- **TextOptions**
  - `controller`: TextController
#### Enums
- **TextSpanType**
  - `TEXT` = 0
  - `IMAGE` = 1
  - `MIXED` = 2
- **TextResponseType**
  - `RIGHT_CLICK` = 0
  - `LONG_PRESS` = 1
  - `SELECT` = 2
#### Classes
- **TextController**
  - `closeSelectionMenu()`: void

### @internal/component/ets/text_area.d.ts
#### Interfaces
- **TextAreaOptions**
  - `placeholder?`: ResourceStr
  - `text?`: ResourceStr
  - `controller?`: TextAreaController
- **TextAreaInterface**
#### Enums
- **TextAreaType**
  - `NORMAL` = 0
  - `NUMBER` = 2
  - `PHONE_NUMBER` = 3
  - `EMAIL` = 5
#### Classes
- **TextAreaController**
  - `caretPosition()`: void
  - `setTextSelection()`: void
  - `stopEditing()`: void

### @internal/component/ets/text_clock.d.ts
#### Interfaces
- **TextClockInterface**
#### Classes
- **TextClockController**

### @internal/component/ets/text_common.d.ts
#### Interfaces
- **TextDataDetectorConfig**
#### Enums
- **TextDataDetectorType**
  - `PHONE_NUMBER` = 0
  - `URL` = 1
  - `EMAIL` = 2
  - `ADDRESS` = 3

### @internal/component/ets/text_input.d.ts
#### Interfaces
- **SubmitEvent**
  - `keepEditableState`: void
  - `text`: string
- **TextInputOptions**
  - `placeholder?`: ResourceStr
  - `text?`: ResourceStr
  - `controller?`: TextInputController
- **TextInputInterface**
- **CaretStyle**
  - `width?`: Length
- **PasswordIcon**
  - `onIconSrc?`: string | Resource
  - `offIconSrc?`: string | Resource
#### Enums
- **InputType**
  - `NUMBER_PASSWORD` = 8
  - `SCREEN_LOCK_PASSWORD` = 9
  - `USER_NAME` = 10
  - `NEW_PASSWORD` = 11
  - `NUMBER_DECIMAL` = 12
- **EnterKeyType**
  - `PREVIOUS` = 7
  - `NEW_LINE` = 8
- **TextInputStyle**
#### Classes
- **TextInputController**
  - `caretPosition()`: void
  - `setTextSelection()`: void
  - `stopEditing()`: void

### @internal/component/ets/text_picker.d.ts
#### Interfaces
- **TextPickerRangeContent**
  - `icon`: string | Resource
  - `text?`: string | Resource
- **TextCascadePickerRangeContent**
  - `text`: string | Resource
  - `children?`: TextCascadePickerRangeContent[]
- **TextPickerOptions**
  - `range`: string[] | string[][] | Resource | TextPickerRangeContent[] | TextCascadePickerRangeContent[]
  - `value?`: string | string[]
  - `selected?`: number | number[]
- **TextPickerInterface**
- **TextPickerResult**
  - `value`: string | string[]
  - `index`: number | number[]
- **TextPickerDialogOptions**
  - `defaultPickerItemHeight?`: number | string
  - `canLoop?`: boolean
  - `disappearTextStyle?`: PickerTextStyle
  - `textStyle?`: PickerTextStyle
  - `selectedTextStyle?`: PickerTextStyle
  - `onAccept?`: (value: TextPickerResult) => void
  - `onCancel?`: () => void
  - `onChange?`: (value: TextPickerResult) => void
  - `maskRect?`: Rectangle
  - `alignment?`: DialogAlignment
  - `offset?`: Offset
  - `backgroundColor?`: ResourceColor
  - `backgroundBlurStyle?`: BlurStyle
#### Classes
- **TextPickerDialog**

### @internal/component/ets/text_timer.d.ts
#### Interfaces
- **TextTimerOptions**
  - `isCountDown?`: boolean
  - `count?`: number
  - `controller?`: TextTimerController
- **TextTimerInterface**
#### Classes
- **TextTimerController**

### @internal/component/ets/time_picker.d.ts
#### Interfaces
- **TimePickerResult**
  - `hour`: number
  - `minute`: number
  - `second`: number
- **TimePickerOptions**
  - `selected?`: Date
  - `format?`: TimePickerFormat
- **TimePickerInterface**
- **TimePickerDialogOptions**
  - `useMilitaryTime?`: boolean
  - `disappearTextStyle?`: PickerTextStyle
  - `textStyle?`: PickerTextStyle
  - `selectedTextStyle?`: PickerTextStyle
  - `maskRect?`: Rectangle
  - `alignment?`: DialogAlignment
  - `offset?`: Offset
  - `onAccept?`: (value: TimePickerResult) => void
  - `onCancel?`: () => void
  - `onChange?`: (value: TimePickerResult) => void
  - `backgroundColor?`: ResourceColor
  - `backgroundBlurStyle?`: BlurStyle
#### Enums
- **TimePickerFormat**
#### Classes
- **TimePickerDialog**

### @internal/component/ets/toggle.d.ts
#### Interfaces
- **ToggleInterface**
#### Enums
- **ToggleType**

### @internal/component/ets/ui_extension_component.d.ts
#### Interfaces
- **UIExtensionOptions**
  - `isTransferringCaller?`: boolean
- **UIExtensionProxy**
  - `send`: void
  - `sendSync`: { [key: string]: Object }
- **UIExtensionComponentInterface**

### @internal/component/ets/units.d.ts
#### Interfaces
- **Font**
  - `size?`: Length
  - `weight?`: FontWeight | number | string
  - `family?`: string | Resource
  - `style?`: FontStyle
- **Area**
  - `width`: Length
  - `height`: Length
  - `position`: Position
  - `globalPosition`: Position
- **Position**
  - `x?`: Length
  - `y?`: Length
- **Bias**
  - `horizontal?`: number
  - `vertical?`: number
- **ConstraintSizeOptions**
  - `minWidth?`: Length
  - `maxWidth?`: Length
  - `minHeight?`: Length
  - `maxHeight?`: Length
- **SizeOptions**
  - `width?`: Length
  - `height?`: Length
- **BorderOptions**
  - `width?`: EdgeWidths | Length
  - `color?`: EdgeColors | ResourceColor
  - `radius?`: BorderRadiuses | Length
  - `style?`: EdgeStyles | BorderStyle
- **OutlineOptions**
  - `width?`: EdgeOutlineWidths | Dimension
  - `color?`: EdgeColors | ResourceColor
  - `radius?`: OutlineRadiuses | Dimension
  - `style?`: EdgeOutlineStyles | OutlineStyle
- **MarkStyle**
  - `strokeColor?`: ResourceColor
  - `size?`: Length
  - `strokeWidth?`: Length
- **TouchPoint**
  - `x`: Dimension
  - `y`: Dimension
#### Classes
- **ColorFilter**
#### Type Aliases
- `Resource` = import('../api/global/resource').Resource
- `Length` = string | number | Resource
- `PX` = `${number}px`
- `VP` = `${number}vp` | number
- `FP` = `${number}fp`
- `LPX` = `${number}lpx`
- `Percentage` = `${number}%`
- `Degree` = `${number}deg`
- `Dimension` = PX | VP | FP | LPX | Percentage | Resource
- `ResourceStr` = string | Resource
- `Padding` = {
  /**
   * top property.
   *
   * @type { ?Length }
   * @syscap SystemCapability.ArkUI.ArkUI.Full
   * @since 7
   */
  /**
   * top property.
   
- `Margin` = Padding
- `EdgeWidth` = EdgeWidths
- `EdgeWidths` = {
  /**
   * top property.
   *
   * @type { ?Length }
   * @syscap SystemCapability.ArkUI.ArkUI.Full
   * @since 9
   * @form
   */
  /**
   * top pr
- `EdgeOutlineWidths` = {
  /**
   * top outline width property.
   *
   * @type { ?Dimension }
   * @syscap SystemCapability.ArkUI.ArkUI.Full
   * @crossplatform
   * @since
- `BorderRadiuses` = {
  /**
   * top-left property.
   *
   * @type { ?Length }
   * @syscap SystemCapability.ArkUI.ArkUI.Full
   * @since 9
   * @form
   */
  /**
   * t
- `OutlineRadiuses` = {
  /**
   * top-left property.
   *
   * @type { ?Dimension }
   * @syscap SystemCapability.ArkUI.ArkUI.Full
   * @crossplatform
   * @since 11
   * 
- `EdgeColors` = {
  /**
   * top property.
   *
   * @type { ?ResourceColor }
   * @syscap SystemCapability.ArkUI.ArkUI.Full
   * @since 9
   * @form
   */
  /**
   *
- `EdgeStyles` = {
  /**
   * top property.
   *
   * @type { ?BorderStyle }
   * @syscap SystemCapability.ArkUI.ArkUI.Full
   * @since 9
   * @form
   */
  /**
   * t
- `EdgeOutlineStyles` = {
  /**
   * top property.
   *
   * @type { ?OutlineStyle }
   * @syscap SystemCapability.ArkUI.ArkUI.Full
   * @crossplatform
   * @since 11
   * @f
- `Offset` = {
  /**
   * dx property.
   *
   * @type { Length }
   * @syscap SystemCapability.ArkUI.ArkUI.Full
   * @since 7
   */
  /**
   * dx property.
   *
 
- `ResourceColor` = Color | number | string | Resource
- `LengthConstrain` = {
  /**
   * minimum length.
   *
   * @type { Length }
   * @syscap SystemCapability.ArkUI.ArkUI.Full
   * @since 9
   * @form
   */
  /**
   * minim

### @internal/component/ets/video.d.ts
#### Interfaces
- **VideoOptions**
  - `src?`: string | Resource
  - `currentProgressRate?`: number | string | PlaybackSpeed
  - `previewUri?`: string | PixelMap | Resource
  - `controller?`: VideoController
- **VideoInterface**
#### Enums
- **SeekMode**
- **PlaybackSpeed**
#### Classes
- **VideoController**

### @internal/component/ets/water_flow.d.ts
#### Interfaces
- **WaterFlowOptions**
  - `footer?`: CustomBuilder
  - `scroller?`: Scroller
- **WaterFlowInterface**

### @internal/component/ets/web.d.ts
#### Interfaces
- **WebMediaOptions**
  - `resumeInterval?`: number
  - `audioExclusive?`: boolean
- **ScreenCaptureConfig**
  - `captureMode`: WebCaptureMode
- **Header**
  - `headerKey`: string
  - `headerValue`: string
- **WebOptions**
  - `src`: string | Resource
  - `controller`: WebController | WebviewController
  - `incognitoMode?`: boolean
- **ScriptItem**
  - `script`: string
  - `scriptRules`: Array<string>
- **LoadCommittedDetails**
  - `isMainFrame`: boolean
  - `isSameDocument`: boolean
  - `didReplaceEntry`: boolean
  - `navigationType`: WebNavigationType
  - `url`: string
- **WebInterface**
- **NativeEmbedInfo**
  - `id?`: string
  - `type?`: string
  - `src?`: string
  - `width?`: number
  - `height?`: number
  - `url?`: string
- **NativeEmbedDataInfo**
  - `status?`: NativeEmbedStatus
  - `surfaceId?`: string
  - `embedId?`: string
  - `info?`: NativeEmbedInfo
- **NativeEmbedTouchInfo**
  - `embedId?`: string
  - `touchEvent?`: TouchEvent
#### Enums
- **MessageLevel**
- **MixedMode**
- **HitTestType**
  - `src` = http.
  - `src` = http.
  - `src` = http + HTML::img.
  - `src` = http + HTML::img.
- **CacheMode**
- **OverScrollMode**
- **WebDarkMode**
- **WebCaptureMode**
  - `HOME_SCREEN` = 0
- **ThreatType**
  - `THREAT_ILLEGAL` = 0
  - `THREAT_FRAUD` = 1
  - `THREAT_RISK` = 2
  - `THREAT_WARNING` = 3
- **RenderExitReason**
- **SslError**
- **FileSelectorMode**
- **WebLayoutMode**
- **ProtectedResourceType**
  - `MidiSysex` = 'TYPE_MIDI_SYSEX'
  - `VIDEO_CAPTURE` = 'TYPE_VIDEO_CAPTURE'
  - `AUDIO_CAPTURE` = 'TYPE_AUDIO_CAPTURE'
- **ContextMenuSourceType**
- **ContextMenuMediaType**
- **ContextMenuInputFieldType**
- **NativeEmbedStatus**
  - `CREATE` = 0
  - `UPDATE` = 1
  - `DESTROY` = 2
- **ContextMenuEditStateFlags**
  - `NONE` = 0
  - `CAN_CUT` = 1 << 0
  - `CAN_COPY` = 1 << 1
  - `CAN_PASTE` = 1 << 2
  - `CAN_SELECT_ALL` = 1 << 3
- **WebNavigationType**
  - `UNKNOWN` = 0
  - `MAIN_FRAME_NEW_ENTRY` = 1
  - `MAIN_FRAME_EXISTING_ENTRY` = 2
  - `NAVIGATION_TYPE_NEW_SUBFRAME` = 4
  - `NAVIGATION_TYPE_AUTO_SUBFRAME` = 5
#### Classes
- **FullScreenExitHandler**
  - `exitFullScreen()`: void
- **FileSelectorParam**
  - `getTitle()`: string
  - `getMode()`: FileSelectorMode
  - `getAcceptType()`: Array<string>
  - `isCapture()`: boolean
- **JsResult**
  - `handleCancel()`: void
  - `handleConfirm()`: void
  - `handlePromptConfirm()`: void
- **FileSelectorResult**
  - `handleFileList()`: void
- **HttpAuthHandler**
  - `confirm()`: boolean
  - `cancel()`: void
  - `isHttpAuthInfoSaved()`: boolean
- **SslErrorHandler**
  - `handleConfirm()`: void
  - `handleCancel()`: void
- **ClientAuthenticationHandler**
  - `confirm()`: void
  - `confirm()`: void
  - `cancel()`: void
  - `ignore()`: void
- **PermissionRequest**
  - `deny()`: void
  - `getOrigin()`: string
  - `getAccessibleResource()`: Array<string>
  - `grant()`: void
- **ScreenCaptureHandler**
  - `getOrigin()`: string
  - `grant()`: void
  - `deny()`: void
- **DataResubmissionHandler**
  - `resend()`: void
  - `cancel()`: void
- **ControllerHandler**
  - `setWebController()`: void
- **WebContextMenuParam**
  - `x()`: number
  - `y()`: number
  - `getLinkUrl()`: string
  - `getUnfilteredLinkUrl()`: string
  - `getSourceUrl()`: string
  - `existsImageContents()`: boolean
  - `getMediaType()`: ContextMenuMediaType
  - `getSelectionText()`: string
  - `getSourceType()`: ContextMenuSourceType
  - `getInputFieldType()`: ContextMenuInputFieldType
  - `isEditable()`: boolean
  - `getEditStateFlags()`: number
- **WebContextMenuResult**
  - `closeContextMenu()`: void
  - `copyImage()`: void
  - `copy()`: void
  - `paste()`: void
  - `cut()`: void
  - `selectAll()`: void
- **ConsoleMessage**
  - `getMessage()`: string
  - `getSourceId()`: string
  - `getLineNumber()`: number
  - `getMessageLevel()`: MessageLevel
- **WebResourceRequest**
  - `getRequestHeader()`: Array<Header>
  - `getRequestUrl()`: string
  - `isRequestGesture()`: boolean
  - `isMainFrame()`: boolean
  - `isRedirect()`: boolean
  - `getRequestMethod()`: string
- **WebResourceResponse**
  - `getResponseData()`: string
  - `getResponseEncoding()`: string
  - `getResponseMimeType()`: string
  - `getReasonMessage()`: string
  - `getResponseHeader()`: Array<Header>
  - `getResponseCode()`: number
- **WebResourceError**
  - `getErrorInfo()`: string
  - `getErrorCode()`: number
- **JsGeolocation**
  - `invoke()`: void
- **WebCookie**
- **WebController**
  - `onInactive()`: void
  - `onActive()`: void
  - `zoom()`: void
  - `clearHistory()`: void
  - `getHitTest()`: HitTestType
  - `accessBackward()`: boolean
  - `accessForward()`: boolean
  - `accessStep()`: boolean
#### Type Aliases
- `WebviewController` = import('../api/@ohos.web.webview').default.WebviewController
- `OnNavigationEntryCommittedCallback` = (loadCommittedDetails: LoadCommittedDetails) => void
- `OnSafeBrowsingCheckResultCallback` = (threatType: ThreatType) => void
- `OnContextMenuHideCallback` = () => void

### @internal/component/ets/window_scene.d.ts
#### Interfaces
- **WindowSceneInterface**

### @internal/component/ets/xcomponent.d.ts
#### Interfaces
- **XComponentInterface**
#### Classes
- **XComponentController**
  - `getXComponentSurfaceId()`: string
  - `getXComponentContext()`: Object
  - `setXComponentSurfaceSize()`: void

### @internal/ets/global.d.ts
#### Functions
- `setInterval(handler: Function | string, delay: number, ...arguments: any[]): number`
- `setTimeout(handler: Function | string, delay?: number, ...arguments: any[]): number`
- `clearInterval(intervalID?: number): void`
- `clearTimeout(timeoutID?: number): void`
- `canIUse(syscap: string): boolean`
- `getInspectorByKey(id: string): string`
- `getInspectorTree(): Object`
- `sendEventByKey(id: string, action: number, params: string): boolean`
- `sendTouchEvent(event: TouchObject): boolean`
- `sendKeyEvent(event: KeyEvent): boolean`
- `sendMouseEvent(event: MouseEvent): boolean`
- `markModuleCollectable(namespace: Object): void`
#### Classes
- **console**
  - `debug()`: void
  - `log()`: void
  - `info()`: void
  - `warn()`: void
  - `error()`: void
  - `assert()`: void
  - `count()`: void
  - `countReset()`: void
  - `dir()`: void
  - `dirxml()`: void
  - `group()`: void
  - `groupCollapsed()`: void
  - `groupEnd()`: void
  - `table()`: void
  - `time()`: void
  - `timeEnd()`: void
  - `timeLog()`: void
  - `trace()`: void

### @internal/ets/index.d.ts

### @internal/ets/lifecycle.d.ts
#### Interfaces
- **LifecycleForm**
  - `onCreate?`: formBindingData.FormBindingData
  - `onCastToNormal?`: void
  - `onUpdate?`: void
  - `onVisibilityChange?`: void
  - `onEvent?`: void
  - `onDestroy?`: void
  - `onAcquireFormState?`: formInfo.FormState
  - `onShare?`: { [key: string]: any }
  - `onShareForm?`: Record<string, Object>
- **LifecycleApp**
  - `onShow?`: void
  - `onHide?`: void
  - `onDestroy?`: void
  - `onCreate?`: void
  - `onWindowDisplayModeChanged?`: void
  - `onStartContinuation?`: boolean
  - `onSaveData?`: boolean
  - `onCompleteContinuation?`: void
  - `onRestoreData?`: void
  - `onRemoteTerminated?`: void
  - `onSaveAbilityState?`: void
  - `onRestoreAbilityState?`: void
  - `onInactive?`: void
  - `onActive?`: void
  - `onNewWant?`: void
  - `onMemoryLevel?`: void
- **LifecycleService**
  - `onStart?`: void
  - `onCommand?`: void
  - `onStop?`: void
  - `onConnect?`: rpc.RemoteObject
  - `onDisconnect?`: void
  - `onReconnect?`: void
- **LifecycleData**
  - `update?`: void
  - `query?`: void
  - `delete?`: void
  - `normalizeUri?`: void
  - `batchInsert?`: void
  - `denormalizeUri?`: void
  - `insert?`: void
  - `openFile?`: void
  - `getFileTypes?`: void
  - `onInitialized?`: void
  - `getType?`: void
  - `executeBatch?`: void
  - `call?`: void

### ability/abilityResult.d.ts
#### Interfaces
- **AbilityResult**
  - `resultCode`: number
  - `want?`: Want

### ability/connectOptions.d.ts
#### Interfaces
- **ConnectOptions**
  - `onConnect`: void
  - `onDisconnect`: void
  - `onFailed`: void

### ability/dataAbilityHelper.d.ts
#### Interfaces
- **DataAbilityHelper**
  - `openFile`: void
  - `openFile`: Promise<number>
  - `on`: void
  - `off`: void
  - `getType`: void
  - `getType`: Promise<string>
  - `getFileTypes`: void
  - `getFileTypes`: Promise<Array<string>>
  - `normalizeUri`: void
  - `normalizeUri`: Promise<string>
  - `denormalizeUri`: void
  - `denormalizeUri`: Promise<string>
  - `notifyChange`: void
  - `notifyChange`: Promise<void>
  - `insert`: void
  - `insert`: Promise<number>
  - `batchInsert`: void
  - `batchInsert`: Promise<number>
  - `delete`: void
  - `delete`: Promise<number>
  - `delete`: void
  - `update`: void
  - `update`: Promise<number>
  - `update`: void
  - `query`: void
  - `query`: void
  - `query`: void
  - `query`: void
  - `query`: Promise<ResultSet>
  - `call`: void
  - `call`: Promise<PacMap>
  - `executeBatch`: void
  - `executeBatch`: Promise<Array<DataAbilityResult>>
- **PacMap**

### ability/dataAbilityOperation.d.ts
#### Interfaces
- **DataAbilityOperation**
  - `uri`: string
  - `type`: featureAbility.DataAbilityOperationType
  - `valuesBucket?`: rdb.ValuesBucket
  - `valueBackReferences?`: rdb.ValuesBucket
  - `predicates?`: dataAbility.DataAbilityPredicates
  - `predicatesBackReferences?`: Map<number, number>
  - `interrupted?`: boolean
  - `expectedCount?`: number

### ability/dataAbilityResult.d.ts
#### Interfaces
- **DataAbilityResult**
  - `uri?`: string
  - `count?`: number

### ability/startAbilityParameter.d.ts
#### Interfaces
- **StartAbilityParameter**
  - `want`: Want
  - `abilityStartSetting?`: { [key: string]: any }
  - `abilityStartSettings?`: Record<string, Object>

### ability/want.d.ts
#### Interfaces
- **Want**
  - `deviceId?`: string
  - `bundleName?`: string
  - `abilityName?`: string
  - `uri?`: string
  - `type?`: string
  - `flags?`: number
  - `action?`: string
  - `parameters?`: { [key: string]: any }
  - `entities?`: Array<string>

### advertising/advertisement.d.ts
#### Interfaces
- **Advertisement**
  - `adType`: number
  - `rewardVerifyConfig`: Map<string, string>
  - `uniqueId`: string
  - `rewarded`: boolean
  - `shown`: boolean
  - `clicked`: boolean

### app/appVersionInfo.d.ts
#### Interfaces
- **AppVersionInfo**
  - `readonly appName`: string
  - `readonly versionCode`: number
  - `readonly versionName`: string

### app/context.d.ts
#### Interfaces
- **Context**
  - `getOrCreateLocalDir`: Promise<string>
  - `getOrCreateLocalDir`: void
  - `verifyPermission`: Promise<number>
  - `verifyPermission`: void
  - `verifyPermission`: void
  - `requestPermissionsFromUser`: void
  - `requestPermissionsFromUser`: Promise<PermissionRequestResult>
  - `getApplicationInfo`: void
  - `getApplicationInfo`: Promise<ApplicationInfo>
  - `getBundleName`: void
  - `getBundleName`: Promise<string>
  - `getDisplayOrientation`: void
  - `getDisplayOrientation`: Promise<bundle.DisplayOrientation>
  - `getExternalCacheDir`: void
  - `getExternalCacheDir`: Promise<string>
  - `setDisplayOrientation`: void
  - `setDisplayOrientation`: Promise<void>
  - `setShowOnLockScreen`: void
  - `setShowOnLockScreen`: Promise<void>
  - `setWakeUpScreen`: void
  - `setWakeUpScreen`: Promise<void>
  - `getProcessInfo`: void
  - `getProcessInfo`: Promise<ProcessInfo>
  - `getElementName`: void
  - `getElementName`: Promise<ElementName>
  - `getProcessName`: void
  - `getProcessName`: Promise<string>
  - `getCallingBundle`: void
  - `getCallingBundle`: Promise<string>
  - `getFilesDir`: void
  - `getFilesDir`: Promise<string>
  - `getCacheDir`: void
  - `getCacheDir`: Promise<string>
  - `getOrCreateDistributedDir`: Promise<string>
  - `getOrCreateDistributedDir`: void
  - `getAppType`: void
  - `getAppType`: Promise<string>
  - `getHapModuleInfo`: void
  - `getHapModuleInfo`: Promise<HapModuleInfo>
  - `getAppVersionInfo`: void
  - `getAppVersionInfo`: Promise<AppVersionInfo>
  - `getApplicationContext`: Context
  - `getAbilityInfo`: void
  - `getAbilityInfo`: Promise<AbilityInfo>
  - `isUpdatingConfigurations`: void
  - `isUpdatingConfigurations`: Promise<boolean>
  - `printDrawnCompleted`: void
  - `printDrawnCompleted`: Promise<void>
- **PermissionRequestResult**
  - `requestCode`: number
  - `permissions`: Array<string>
  - `authResults`: Array<number>
- **PermissionOptions**
  - `pid?`: number
  - `uid?`: number

### app/processInfo.d.ts
#### Interfaces
- **ProcessInfo**
  - `pid`: number
  - `processName`: string

### application/AbilityDelegator.d.ts
#### Interfaces
- **AbilityDelegator**
  - `addAbilityMonitor`: void
  - `addAbilityMonitor`: Promise<void>
  - `addAbilityMonitorSync`: void
  - `addAbilityStageMonitor`: void
  - `addAbilityStageMonitor`: Promise<void>
  - `addAbilityStageMonitorSync`: void
  - `removeAbilityMonitor`: void
  - `removeAbilityMonitor`: Promise<void>
  - `removeAbilityMonitorSync`: void
  - `removeAbilityStageMonitor`: void
  - `removeAbilityStageMonitor`: Promise<void>
  - `removeAbilityStageMonitorSync`: void
  - `waitAbilityMonitor`: void
  - `waitAbilityMonitor`: void
  - `waitAbilityMonitor`: Promise<UIAbility>
  - `waitAbilityStageMonitor`: void
  - `waitAbilityStageMonitor`: void
  - `waitAbilityStageMonitor`: Promise<AbilityStage>
  - `getAppContext`: Context
  - `getAbilityState`: number
  - `getCurrentTopAbility`: void
  - `getCurrentTopAbility`: Promise<UIAbility>
  - `startAbility`: void
  - `startAbility`: Promise<void>
  - `doAbilityForeground`: void
  - `doAbilityForeground`: Promise<void>
  - `doAbilityBackground`: void
  - `doAbilityBackground`: Promise<void>
  - `print`: void
  - `print`: Promise<void>
  - `printSync`: void
  - `executeShellCommand`: void
  - `executeShellCommand`: void
  - `executeShellCommand`: Promise<ShellCmdResult>
  - `finishTest`: void
  - `finishTest`: Promise<void>
  - `setMockList`: void

### application/AbilityForegroundStateObserver.d.ts
#### Classes
- **AbilityForegroundStateObserver**
  - `onAbilityStateChanged()`: void

### application/AbilityMonitor.d.ts
#### Interfaces
- **AbilityMonitor**
  - `abilityName`: string
  - `moduleName?`: string
  - `onAbilityCreate?`: (ability: UIAbility) => void
  - `onAbilityForeground?`: (ability: UIAbility) => void
  - `onAbilityBackground?`: (ability: UIAbility) => void
  - `onAbilityDestroy?`: (ability: UIAbility) => void
  - `onWindowStageCreate?`: (ability: UIAbility) => void
  - `onWindowStageRestore?`: (ability: UIAbility) => void
  - `onWindowStageDestroy?`: (ability: UIAbility) => void

### application/AbilityRunningInfo.d.ts
#### Interfaces
- **AbilityRunningInfo**
  - `ability`: ElementName
  - `pid`: number
  - `uid`: number
  - `processName`: string
  - `startTime`: number
  - `abilityState`: abilityManager.AbilityState

### application/AbilityStageContext.d.ts
#### Classes
- **AbilityStageContext**

### application/AbilityStageMonitor.d.ts
#### Interfaces
- **AbilityStageMonitor**
  - `moduleName`: string
  - `srcEntrance`: string

### application/AbilityStartCallback.d.ts
#### Classes
- **AbilityStartCallback**
  - `onError()`: void

### application/AbilityStateData.d.ts
#### Classes
- **AbilityStateData**

### application/AccessibilityExtensionContext.d.ts
#### Interfaces
- **AccessibilityElement**
  - `actionNames`: void
  - `actionNames`: Promise<Array<string>>
  - `performAction`: void
  - `performAction`: Promise<void>
  - `performAction`: void
  - `findElement`: void
  - `findElement`: Promise<Array<AccessibilityElement>>
  - `findElement`: void
  - `findElement`: Promise<AccessibilityElement>
  - `findElement`: void
  - `findElement`: Promise<AccessibilityElement>
- **ElementAttributeValues**
  - `accessibilityFocused`: boolean
  - `bundleName`: string
  - `checkable`: boolean
  - `checked`: boolean
  - `children`: Array<AccessibilityElement>
  - `clickable`: boolean
  - `componentId`: number
  - `componentType`: string
  - `contents`: Array<string>
  - `currentIndex`: number
  - `description`: string
  - `editable`: boolean
  - `endIndex`: number
  - `error`: string
  - `focusable`: boolean
  - `hintText`: string
  - `inputType`: number
  - `inspectorKey`: string
  - `isActive`: boolean
  - `isEnable`: boolean
  - `isHint`: boolean
  - `isFocused`: boolean
  - `isPassword`: boolean
  - `isVisible`: boolean
  - `itemCount`: number
  - `lastContent`: string
  - `layer`: number
  - `longClickable`: boolean
  - `pageId`: number
  - `parent`: AccessibilityElement
  - `pluralLineSupported`: boolean
  - `rect`: Rect
  - `resourceName`: string
  - `rootElement`: AccessibilityElement
  - `screenRect`: Rect
  - `scrollable`: boolean
  - `selected`: boolean
  - `startIndex`: number
  - `text`: string
  - `textLengthLimit`: number
  - `textMoveUnit`: accessibility.TextMoveUnit
  - `triggerAction`: accessibility.Action
  - `type`: WindowType
  - `valueMax`: number
  - `valueMin`: number
  - `valueNow`: number
  - `windowId`: number
- **Rect**
  - `left`: number
  - `top`: number
  - `width`: number
  - `height`: number
#### Classes
- **AccessibilityExtensionContext**
  - `setTargetBundleName()`: void
  - `setTargetBundleName()`: Promise<void>
  - `getFocusElement()`: void
  - `getFocusElement()`: Promise<AccessibilityElement>
  - `getFocusElement()`: void
  - `getWindowRootElement()`: void
  - `getWindowRootElement()`: Promise<AccessibilityElement>
  - `getWindowRootElement()`: void
  - `getWindows()`: void
  - `getWindows()`: Promise<Array<AccessibilityElement>>
  - `getWindows()`: void
  - `injectGesture()`: void
  - `injectGesture()`: Promise<void>
  - `injectGestureSync()`: void
#### Type Aliases
- `FocusDirection` = 'up' | 'down' | 'left' | 'right' | 'forward' | 'backward'
- `FocusType` = 'accessibility' | 'normal'
- `WindowType` = 'application' | 'system'

### application/AppForegroundStateObserver.d.ts
#### Classes
- **AppForegroundStateObserver**
  - `onAppStateChanged()`: void

### application/AppStateData.d.ts
#### Classes
- **AppStateData**

### application/ApplicationContext.d.ts
#### Classes
- **ApplicationContext**
  - `on()`: number
  - `off()`: void
  - `off()`: Promise<void>
  - `on()`: number
  - `off()`: void
  - `off()`: Promise<void>
  - `on()`: void
  - `off()`: void
  - `getRunningProcessInformation()`: Promise<Array<ProcessInformation>>
  - `getRunningProcessInformation()`: void
  - `killAllProcesses()`: Promise<void>
  - `setColorMode()`: void
  - `setLanguage()`: void
  - `clearUpApplicationData()`: Promise<void>
  - `clearUpApplicationData()`: void

### application/ApplicationStateObserver.d.ts
#### Classes
- **ApplicationStateObserver**
  - `onForegroundApplicationChanged()`: void
  - `onAbilityStateChanged()`: void
  - `onProcessCreated()`: void
  - `onProcessDied()`: void
  - `onProcessStateChanged()`: void
#### Type Aliases
- `ProcessData` = _ProcessData.default

### application/AutoFillExtensionContext.d.ts
#### Classes
- **AutoFillExtensionContext**

### application/AutoFillRequest.d.ts
#### Interfaces
- **FillRequest**
  - `type`: AutoFillType
  - `viewData`: ViewData
- **SaveRequest**
  - `viewData`: ViewData
- **FillResponse**
  - `viewData`: ViewData
- **FillRequestCallback**
  - `onSuccess`: void
  - `onFailure`: void
  - `onCancel`: void
- **SaveRequestCallback**
  - `onSuccess`: void
  - `onFailure`: void

### application/AutoFillType.d.ts
#### Enums
- **AutoFillType**
  - `UNSPECIFIED` = 0
  - `PASSWORD` = 1
  - `USER_NAME` = 2
  - `NEW_PASSWORD` = 3

### application/AutoStartupCallback.d.ts
#### Interfaces
- **AutoStartupCallback**
  - `onAutoStartupOn`: void
  - `onAutoStartupOff`: void

### application/AutoStartupInfo.d.ts
#### Interfaces
- **AutoStartupInfo**
  - `bundleName`: string
  - `moduleName?`: string
  - `abilityName`: string
  - `abilityTypeName?`: string

### application/BaseContext.d.ts
#### Classes
- **BaseContext**

### application/BusinessAbilityInfo.d.ts
#### Interfaces
- **BusinessAbilityInfo**
  - `readonly bundleName`: string
  - `readonly moduleName`: string
  - `readonly name`: string
  - `readonly labelId`: number
  - `readonly descriptionId`: number
  - `readonly iconId`: number
  - `readonly businessType`: businessAbilityRouter.BusinessType
  - `readonly applicationInfo`: ApplicationInfo

### application/Context.d.ts
#### Classes
- **Context**
  - `createBundleContext()`: Context
  - `createModuleContext()`: Context
  - `createModuleContext()`: Context
  - `getApplicationContext()`: ApplicationContext
  - `getGroupDir()`: void
  - `getGroupDir()`: Promise<string>
  - `createModuleResourceManager()`: resmgr.ResourceManager

### application/ContinuableInfo.d.ts
#### Interfaces
- **ContinuableInfo**
  - `srcDeviceId`: string
  - `bundleName`: string

### application/ContinueCallback.d.ts
#### Interfaces
- **ContinueCallback**
  - `onContinueDone`: void

### application/ContinueDeviceInfo.d.ts
#### Interfaces
- **ContinueDeviceInfo**
  - `srcDeviceId`: string
  - `dstDeviceId`: string
  - `missionId`: number
  - `wantParam`: Record<string, Object>

### application/ContinueMissionInfo.d.ts
#### Interfaces
- **ContinueMissionInfo**
  - `srcDeviceId`: string
  - `dstDeviceId`: string
  - `bundleName`: string
  - `wantParam`: Record<string, Object>

### application/DriverExtensionContext.d.ts
#### Classes
- **DriverExtensionContext**
  - `updateDriverState()`: void

### application/ErrorObserver.d.ts
#### Classes
- **ErrorObserver**
  - `onUnhandledException()`: void
  - `onException?()`: void

### application/EventHub.d.ts
#### Classes
- **EventHub**
  - `on()`: void
  - `off()`: void
  - `emit()`: void

### application/ExtensionContext.d.ts
#### Classes
- **ExtensionContext**

### application/ExtensionRunningInfo.d.ts
#### Interfaces
- **ExtensionRunningInfo**
  - `extension`: ElementName
  - `pid`: number
  - `uid`: number
  - `processName`: string
  - `startTime`: number
  - `clientPackage`: Array<String>
  - `type`: bundle.ExtensionAbilityType

### application/FormExtensionContext.d.ts
#### Classes
- **FormExtensionContext**
  - `startAbility()`: void
  - `startAbility()`: Promise<void>
  - `connectServiceExtensionAbility()`: number
  - `disconnectServiceExtensionAbility()`: void
  - `disconnectServiceExtensionAbility()`: Promise<void>

### application/MediaControlExtensionContext.d.ts
#### Classes
- **MediaControlExtensionContext**

### application/MissionCallbacks.d.ts
#### Interfaces
- **MissionCallback**
  - `notifyMissionsChanged`: void
  - `notifySnapshot`: void
  - `notifyNetDisconnect`: void

### application/MissionDeviceInfo.d.ts
#### Interfaces
- **MissionDeviceInfo**
  - `deviceId`: string

### application/MissionInfo.d.ts
#### Interfaces
- **MissionInfo**
  - `missionId`: number
  - `runningState`: number
  - `lockedState`: boolean
  - `timestamp`: string
  - `want`: Want
  - `label`: string
  - `iconPath`: string
  - `continuable`: boolean
  - `abilityState`: number
  - `unclearable`: boolean

### application/MissionListener.d.ts
#### Interfaces
- **MissionListener**
  - `onMissionCreated`: void
  - `onMissionDestroyed`: void
  - `onMissionSnapshotChanged`: void
  - `onMissionMovedToFront`: void
  - `onMissionLabelUpdated`: void
  - `onMissionIconUpdated`: void
  - `onMissionClosed`: void

### application/MissionParameter.d.ts
#### Interfaces
- **MissionParameter**
  - `deviceId`: string
  - `fixConflict`: boolean
  - `tag`: number

### application/MissionSnapshot.d.ts
#### Interfaces
- **MissionSnapshot**
  - `ability`: ElementName
  - `snapshot`: image.PixelMap

### application/PageNodeInfo.d.ts
#### Interfaces
- **PageNodeInfo**
  - `id`: number
  - `depth`: number
  - `autoFillType`: AutoFillType
  - `tag`: string
  - `value`: string
  - `placeholder?`: string
  - `passwordRules?`: string
  - `enableAutoFill`: boolean

### application/ProcessData.d.ts
#### Classes
- **ProcessData**

### application/ProcessInformation.d.ts
#### Interfaces
- **ProcessInformation**
  - `pid`: number
  - `uid`: number
  - `processName`: string
  - `bundleNames`: Array<string>
  - `state`: appManager.ProcessState

### application/ProcessRunningInfo.d.ts
#### Interfaces
- **ProcessRunningInfo**
  - `pid`: number
  - `uid`: number
  - `processName`: string
  - `bundleNames`: Array<string>

### application/ServiceExtensionContext.d.ts
#### Classes
- **ServiceExtensionContext**
  - `startAbility()`: void
  - `startAbility()`: void
  - `startAbility()`: Promise<void>
  - `startAbilityAsCaller()`: void
  - `startAbilityAsCaller()`: void
  - `startAbilityAsCaller()`: Promise<void>
  - `startAbilityWithAccount()`: void
  - `startAbilityWithAccount()`: void
  - `startAbilityWithAccount()`: Promise<void>
  - `startServiceExtensionAbility()`: void
  - `startServiceExtensionAbility()`: Promise<void>
  - `startServiceExtensionAbilityWithAccount()`: void
  - `startServiceExtensionAbilityWithAccount()`: Promise<void>
  - `stopServiceExtensionAbility()`: void
  - `stopServiceExtensionAbility()`: Promise<void>
  - `stopServiceExtensionAbilityWithAccount()`: void
  - `stopServiceExtensionAbilityWithAccount()`: Promise<void>
  - `terminateSelf()`: void
  - `terminateSelf()`: Promise<void>
  - `connectServiceExtensionAbility()`: number
  - `connectServiceExtensionAbilityWithAccount()`: number
  - `disconnectServiceExtensionAbility()`: void
  - `disconnectServiceExtensionAbility()`: Promise<void>
  - `startAbilityByCall()`: Promise<Caller>
  - `startAbilityByCallWithAccount()`: Promise<Caller>
  - `startRecentAbility()`: void
  - `startRecentAbility()`: void
  - `startRecentAbility()`: Promise<void>
  - `requestModalUIExtension()`: void
  - `requestModalUIExtension()`: Promise<void>

### application/UIAbilityContext.d.ts
#### Classes
- **UIAbilityContext**
  - `startAbility()`: void
  - `startAbility()`: void
  - `startAbility()`: Promise<void>
  - `startAbilityAsCaller()`: void
  - `startAbilityAsCaller()`: void
  - `startAbilityAsCaller()`: Promise<void>
  - `startAbilityByCall()`: Promise<Caller>
  - `startAbilityByCallWithAccount()`: Promise<Caller>
  - `startAbilityWithAccount()`: void
  - `startAbilityWithAccount()`: void
  - `startAbilityWithAccount()`: Promise<void>
  - `startAbilityForResult()`: void
  - `startAbilityForResult()`: void
  - `startAbilityForResult()`: Promise<AbilityResult>
  - `startAbilityForResultWithAccount()`: void
  - `startAbilityForResultWithAccount()`: void
  - `startAbilityForResultWithAccount()`: Promise<AbilityResult>
  - `startServiceExtensionAbility()`: void
  - `startServiceExtensionAbility()`: Promise<void>
  - `startServiceExtensionAbilityWithAccount()`: void
  - `startServiceExtensionAbilityWithAccount()`: Promise<void>
  - `stopServiceExtensionAbility()`: void
  - `stopServiceExtensionAbility()`: Promise<void>
  - `stopServiceExtensionAbilityWithAccount()`: void
  - `stopServiceExtensionAbilityWithAccount()`: Promise<void>
  - `terminateSelf()`: void
  - `terminateSelf()`: Promise<void>
  - `terminateSelfWithResult()`: void
  - `terminateSelfWithResult()`: Promise<void>
  - `connectServiceExtensionAbility()`: number
  - `connectServiceExtensionAbilityWithAccount()`: number
  - `disconnectServiceExtensionAbility()`: void
  - `disconnectServiceExtensionAbility()`: Promise<void>
  - `setMissionLabel()`: void
  - `setMissionLabel()`: Promise<void>
  - `setMissionIcon()`: void
  - `setMissionIcon()`: Promise<void>
  - `setMissionContinueState()`: void
  - `setMissionContinueState()`: Promise<void>
  - `restoreWindowStage()`: void
  - `isTerminating()`: boolean
  - `startRecentAbility()`: void
  - `startRecentAbility()`: void
  - `startRecentAbility()`: Promise<void>
  - `requestDialogService()`: void
  - `requestDialogService()`: Promise<dialogRequest.RequestResult>
  - `reportDrawnCompleted()`: void
  - `startAbilityByType()`: void
  - `startAbilityByType()`: Promise<void>
  - `requestModalUIExtension()`: void
  - `requestModalUIExtension()`: Promise<void>

### application/UIExtensionContext.d.ts
#### Classes
- **UIExtensionContext**
  - `startAbility()`: void
  - `startAbility()`: void
  - `startAbility()`: Promise<void>
  - `startAbilityForResult()`: void
  - `startAbilityForResult()`: void
  - `startAbilityForResult()`: Promise<AbilityResult>
  - `connectServiceExtensionAbility()`: number
  - `disconnectServiceExtensionAbility()`: void
  - `disconnectServiceExtensionAbility()`: Promise<void>

### application/ViewData.d.ts
#### Interfaces
- **ViewData**
  - `bundleName`: string
  - `moduleName`: string
  - `abilityName`: string
  - `pageUrl`: string
  - `pageNodeInfos`: Array<PageNodeInfo>

### application/VpnExtensionContext.d.ts
#### Classes
- **VpnExtensionContext**

### application/WindowExtensionContext.d.ts
#### Classes
- **WindowExtensionContext**
  - `startAbility()`: void
  - `startAbility()`: Promise<void>

### application/WorkSchedulerExtensionContext.d.ts
#### Classes
- **WorkSchedulerExtensionContext**

### application/abilityDelegatorArgs.d.ts
#### Interfaces
- **AbilityDelegatorArgs**
  - `bundleName`: string
  - `parameters`: Record<string, string>
  - `testCaseNames`: string
  - `testRunnerClassName`: string

### application/shellCmdResult.d.ts
#### Interfaces
- **ShellCmdResult**
  - `stdResult`: string
  - `exitCode`: number

### arkui/BuilderNode.d.ts
#### Interfaces
- **RenderOptions**
  - `selfIdealSize?`: Size
  - `type?`: NodeRenderType
  - `surfaceId?`: string
#### Enums
- **NodeRenderType**
  - `RENDER_TYPE_DISPLAY` = 0
  - `RENDER_TYPE_TEXTURE` = 1

### arkui/FrameNode.d.ts
#### Classes
- **FrameNode**
  - `getRenderNode()`: RenderNode | null

### arkui/Graphics.d.ts
#### Interfaces
- **Size**
  - `width`: number
  - `height`: number
- **Vector2**
- **Vector3**
  - `x`: number
  - `y`: number
  - `z`: number
- **Frame**
  - `x`: number
  - `y`: number
  - `width`: number
  - `height`: number
#### Classes
- **DrawContext**
#### Type Aliases
- `Matrix4` = [
  number,
  number,
  number,
  number,
  number,
  number,
  number,
  number,
  number,
  number,
  number,
  number,
  number,
  number,
  number
- `Offset` = Vector2
- `Position` = Vector2
- `Pivot` = Vector2
- `Scale` = Vector2
- `Translation` = Vector2
- `Rotation` = Vector3

### arkui/NodeController.d.ts
#### Classes
- **NodeController**
  - `aboutToResize?()`: void
  - `aboutToAppear?()`: void
  - `aboutToDisappear?()`: void
  - `rebuild()`: void
  - `onTouchEvent?()`: void

### arkui/RenderNode.d.ts
#### Classes
- **RenderNode**
  - `appendChild()`: void
  - `insertChildAfter()`: void
  - `removeChild()`: void
  - `clearChildren()`: void
  - `getChild()`: RenderNode | null
  - `getFirstChild()`: RenderNode | null
  - `getNextSibling()`: RenderNode | null
  - `getPreviousSibling()`: RenderNode | null
  - `draw()`: void
  - `invalidate()`: void

### arkui/XComponentNode.d.ts
#### Classes
- **XComponentNode**
  - `onCreate()`: void
  - `onDestroy()`: void
  - `changeRenderType()`: boolean

### bundle/PermissionDef.d.ts
#### Interfaces
- **PermissionDef**
  - `permissionName`: string
  - `grantMode`: number
  - `labelId`: number
  - `descriptionId`: number

### bundle/abilityInfo.d.ts
#### Interfaces
- **AbilityInfo**
  - `readonly bundleName`: string
  - `readonly name`: string
  - `readonly label`: string
  - `readonly description`: string
  - `readonly icon`: string
  - `readonly labelId`: number
  - `readonly descriptionId`: number
  - `readonly iconId`: number
  - `readonly moduleName`: string
  - `readonly process`: string
  - `readonly targetAbility`: string
  - `readonly backgroundModes`: number
  - `readonly isVisible`: boolean
  - `readonly formEnabled`: boolean
  - `readonly type`: bundle.AbilityType
  - `readonly subType`: bundle.AbilitySubType
  - `readonly orientation`: bundle.DisplayOrientation
  - `readonly launchMode`: bundle.LaunchMode
  - `readonly permissions`: Array<string>
  - `readonly deviceTypes`: Array<string>
  - `readonly deviceCapabilities`: Array<string>
  - `readonly readPermission`: string
  - `readonly writePermission`: string
  - `readonly applicationInfo`: ApplicationInfo
  - `readonly uri`: string
  - `readonly metaData`: Array<CustomizeData>
  - `readonly enabled`: boolean

### bundle/applicationInfo.d.ts
#### Interfaces
- **ApplicationInfo**
  - `readonly name`: string
  - `readonly description`: string
  - `readonly descriptionId`: number
  - `readonly systemApp`: boolean
  - `readonly enabled`: boolean
  - `readonly label`: string
  - `readonly labelId`: string
  - `readonly icon`: string
  - `readonly iconId`: string
  - `readonly process`: string
  - `readonly supportedModes`: number
  - `readonly moduleSourceDirs`: Array<string>
  - `readonly permissions`: Array<string>
  - `readonly moduleInfos`: Array<ModuleInfo>
  - `readonly entryDir`: string
  - `readonly codePath`: string
  - `readonly metaData`: Map<string, Array<CustomizeData>>
  - `readonly removable`: boolean
  - `readonly accessTokenId`: number
  - `readonly uid`: number
  - `readonly entityType`: string

### bundle/bundleInfo.d.ts
#### Interfaces
- **UsedScene**
  - `abilities`: Array<string>
  - `when`: string
- **ReqPermissionDetail**
  - `name`: string
  - `reason`: string
  - `usedScene`: UsedScene
- **BundleInfo**
  - `readonly name`: string
  - `readonly type`: string
  - `readonly appId`: string
  - `readonly uid`: number
  - `readonly installTime`: number
  - `readonly updateTime`: number
  - `readonly appInfo`: ApplicationInfo
  - `readonly abilityInfos`: Array<AbilityInfo>
  - `readonly reqPermissions`: Array<string>
  - `readonly reqPermissionDetails`: Array<ReqPermissionDetail>
  - `readonly vendor`: string
  - `readonly versionCode`: number
  - `readonly versionName`: string
  - `readonly compatibleVersion`: number
  - `readonly targetVersion`: number
  - `readonly isCompressNativeLibs`: boolean
  - `readonly hapModuleInfos`: Array<HapModuleInfo>
  - `readonly entryModuleName`: string
  - `readonly cpuAbi`: string
  - `readonly isSilentInstallation`: string
  - `readonly minCompatibleVersionCode`: number
  - `readonly entryInstallationFree`: boolean
  - `readonly reqPermissionStates`: Array<number>

### bundle/bundleInstaller.d.ts
#### Interfaces
- **InstallParam**
  - `userId`: number
  - `installFlag`: number
  - `isKeepData`: boolean
- **InstallStatus**
  - `status`: bundle.InstallErrorCode
  - `statusMessage`: string
- **BundleInstaller**
  - `install`: void
  - `uninstall`: void
  - `recover`: void

### bundle/bundleStatusCallback.d.ts
#### Interfaces
- **BundleStatusCallback**
  - `add`: (bundleName: string, userId: number) => void
  - `update`: (bundleName: string, userId: number) => void
  - `remove`: (bundleName: string, userId: number) => void

### bundle/customizeData.d.ts
#### Interfaces
- **CustomizeData**
  - `name`: string
  - `value`: string
  - `extra`: string

### bundle/elementName.d.ts
#### Interfaces
- **ElementName**
  - `deviceId?`: string
  - `bundleName`: string
  - `abilityName`: string
  - `uri?`: string
  - `shortName?`: string

### bundle/hapModuleInfo.d.ts
#### Interfaces
- **HapModuleInfo**
  - `readonly name`: string
  - `readonly description`: string
  - `readonly descriptionId`: number
  - `readonly icon`: string
  - `readonly label`: string
  - `readonly labelId`: number
  - `readonly iconId`: number
  - `readonly backgroundImg`: string
  - `readonly supportedModes`: number
  - `readonly reqCapabilities`: Array<string>
  - `readonly deviceTypes`: Array<string>
  - `readonly abilityInfo`: Array<AbilityInfo>
  - `readonly moduleName`: string
  - `readonly mainAbilityName`: string
  - `readonly installationFree`: boolean

### bundle/launcherAbilityInfo.d.ts
#### Interfaces
- **LauncherAbilityInfo**
  - `readonly applicationInfo`: ApplicationInfo
  - `readonly elementName`: ElementName
  - `readonly labelId`: number
  - `readonly iconId`: number
  - `readonly userId`: number
  - `readonly installTime`: number

### bundle/moduleInfo.d.ts
#### Interfaces
- **ModuleInfo**
  - `readonly moduleName`: string
  - `readonly moduleSourceDir`: string

### bundle/remoteAbilityInfo.d.ts
#### Interfaces
- **RemoteAbilityInfo**
  - `readonly elementName`: ElementName
  - `readonly label`: string
  - `readonly icon`: string

### bundle/shortcutInfo.d.ts
#### Interfaces
- **ShortcutWant**
  - `readonly targetBundle`: string
  - `readonly targetClass`: string
- **ShortcutInfo**
  - `readonly id`: string
  - `readonly bundleName`: string
  - `readonly hostAbility`: string
  - `readonly icon`: string
  - `readonly iconId`: number
  - `readonly label`: string
  - `readonly labelId`: number
  - `readonly disableMessage`: string
  - `readonly wants`: Array<ShortcutWant>
  - `readonly isStatic?`: boolean
  - `readonly isHomeShortcut?`: boolean
  - `readonly isEnabled?`: boolean

### bundleManager/AbilityInfo.d.ts
#### Interfaces
- **AbilityInfo**
  - `readonly bundleName`: string
  - `readonly moduleName`: string
  - `readonly name`: string
  - `readonly label`: string
  - `readonly labelId`: number
  - `readonly description`: string
  - `readonly descriptionId`: number
  - `readonly icon`: string
  - `readonly iconId`: number
  - `readonly process`: string
  - `readonly exported`: boolean
  - `readonly type`: bundleManager.AbilityType
  - `readonly orientation`: bundleManager.DisplayOrientation
  - `readonly launchType`: bundleManager.LaunchType
  - `readonly permissions`: Array<string>
  - `readonly readPermission`: string
  - `readonly writePermission`: string
  - `readonly uri`: string
  - `readonly deviceTypes`: Array<string>
  - `readonly applicationInfo`: ApplicationInfo
  - `readonly metadata`: Array<Metadata>
  - `readonly enabled`: boolean
  - `readonly supportWindowModes`: Array<bundleManager.SupportWindowMode>
  - `readonly windowSize`: WindowSize
- **WindowSize**
  - `readonly maxWindowRatio`: number
  - `readonly minWindowRatio`: number
  - `readonly maxWindowWidth`: number
  - `readonly minWindowWidth`: number
  - `readonly maxWindowHeight`: number
  - `readonly minWindowHeight`: number

### bundleManager/AppProvisionInfo.d.ts
#### Interfaces
- **AppProvisionInfo**
  - `readonly versionCode`: number
  - `readonly versionName`: string
  - `readonly uuid`: string
  - `readonly type`: string
  - `readonly appDistributionType`: string
  - `readonly validity`: Validity
  - `readonly developerId`: string
  - `readonly certificate`: string
  - `readonly apl`: string
  - `readonly issuer`: string
  - `readonly appIdentifier`: string
- **Validity**
  - `readonly notBefore`: number
  - `readonly notAfter`: number

### bundleManager/ApplicationInfo.d.ts
#### Interfaces
- **ApplicationInfo**
  - `readonly name`: string
  - `readonly description`: string
  - `readonly descriptionId`: number
  - `readonly enabled`: boolean
  - `readonly label`: string
  - `readonly labelId`: number
  - `readonly icon`: string
  - `readonly iconId`: number
  - `readonly process`: string
  - `readonly permissions`: Array<string>
  - `readonly codePath`: string
  - `readonly metadata`: Map<string, Array<Metadata>>
  - `readonly metadataArray`: Array<ModuleMetadata>
  - `readonly removable`: boolean
  - `readonly accessTokenId`: number
  - `readonly uid`: number
  - `readonly iconResource`: Resource
  - `readonly labelResource`: Resource
  - `readonly descriptionResource`: Resource
  - `readonly appDistributionType`: string
  - `readonly appProvisionType`: string
  - `readonly systemApp`: boolean
  - `readonly bundleType`: bundleManager.BundleType
  - `readonly debug`: boolean
  - `readonly dataUnclearable`: boolean
- **ModuleMetadata**
  - `readonly moduleName`: string
  - `readonly metadata`: Array<Metadata>

### bundleManager/BundleInfo.d.ts
#### Interfaces
- **BundleInfo**
  - `readonly name`: string
  - `readonly vendor`: string
  - `readonly versionCode`: number
  - `readonly versionName`: string
  - `readonly minCompatibleVersionCode`: number
  - `readonly targetVersion`: number
  - `readonly appInfo`: ApplicationInfo
  - `readonly hapModulesInfo`: Array<HapModuleInfo>
  - `readonly reqPermissionDetails`: Array<ReqPermissionDetail>
  - `readonly permissionGrantStates`: Array<bundleManager.PermissionGrantState>
  - `readonly signatureInfo`: SignatureInfo
  - `readonly installTime`: number
  - `readonly updateTime`: number
- **ReqPermissionDetail**
  - `name`: string
  - `moduleName`: string
  - `reason`: string
  - `reasonId`: number
  - `usedScene`: UsedScene
- **UsedScene**
  - `abilities`: Array<string>
  - `when`: string
- **SignatureInfo**
  - `readonly appId`: string
  - `readonly fingerprint`: string
  - `readonly appIdentifier`: string

### bundleManager/BundlePackInfo.d.ts
#### Interfaces
- **BundlePackInfo**
  - `readonly packages`: Array<PackageConfig>
  - `readonly summary`: PackageSummary
- **PackageConfig**
  - `readonly deviceTypes`: Array<string>
  - `readonly name`: string
  - `readonly moduleType`: string
  - `readonly deliveryWithInstall`: boolean
- **PackageSummary**
  - `readonly app`: BundleConfigInfo
  - `readonly modules`: Array<ModuleConfigInfo>
- **BundleConfigInfo**
  - `readonly bundleName`: string
  - `readonly version`: Version
- **ExtensionAbility**
  - `readonly name`: string
  - `readonly forms`: Array<AbilityFormInfo>
- **ModuleConfigInfo**
  - `readonly mainAbility`: string
  - `readonly apiVersion`: ApiVersion
  - `readonly deviceTypes`: Array<string>
  - `readonly distro`: ModuleDistroInfo
  - `readonly abilities`: Array<ModuleAbilityInfo>
  - `readonly extensionAbilities`: Array<ExtensionAbility>
- **ModuleDistroInfo**
  - `readonly deliveryWithInstall`: boolean
  - `readonly installationFree`: boolean
  - `readonly moduleName`: string
  - `readonly moduleType`: string
- **ModuleAbilityInfo**
  - `readonly name`: string
  - `readonly label`: string
  - `readonly exported`: boolean
  - `readonly forms`: Array<AbilityFormInfo>
- **AbilityFormInfo**
  - `readonly name`: string
  - `readonly type`: string
  - `readonly updateEnabled`: boolean
  - `readonly scheduledUpdateTime`: string
  - `readonly updateDuration`: number
  - `readonly supportDimensions`: Array<string>
  - `readonly defaultDimension`: string
- **Version**
  - `readonly minCompatibleVersionCode`: number
  - `readonly name`: string
  - `readonly code`: number
- **ApiVersion**
  - `readonly releaseType`: string
  - `readonly compatible`: number
  - `readonly target`: number

### bundleManager/BundleResourceInfo.d.ts
#### Interfaces
- **BundleResourceInfo**
  - `readonly bundleName`: string
  - `readonly icon`: string
  - `readonly label`: string

### bundleManager/DispatchInfo.d.ts
#### Interfaces
- **DispatchInfo**
  - `readonly version`: string
  - `readonly dispatchAPIVersion`: string

### bundleManager/ElementName.d.ts
#### Interfaces
- **ElementName**
  - `deviceId?`: string
  - `bundleName`: string
  - `moduleName?`: string
  - `abilityName`: string
  - `uri?`: string
  - `shortName?`: string

### bundleManager/ExtensionAbilityInfo.d.ts
#### Interfaces
- **ExtensionAbilityInfo**
  - `readonly bundleName`: string
  - `readonly moduleName`: string
  - `readonly name`: string
  - `readonly labelId`: number
  - `readonly descriptionId`: number
  - `readonly iconId`: number
  - `readonly exported`: boolean
  - `readonly extensionAbilityType`: bundleManager.ExtensionAbilityType
  - `readonly extensionAbilityTypeName`: string
  - `readonly permissions`: Array<string>
  - `readonly applicationInfo`: ApplicationInfo
  - `readonly metadata`: Array<Metadata>
  - `readonly enabled`: boolean
  - `readonly readPermission`: string
  - `readonly writePermission`: string

### bundleManager/HapModuleInfo.d.ts
#### Interfaces
- **HapModuleInfo**
  - `readonly name`: string
  - `readonly icon`: string
  - `readonly iconId`: number
  - `readonly label`: string
  - `readonly labelId`: number
  - `readonly description`: string
  - `readonly descriptionId`: number
  - `readonly mainElementName`: string
  - `readonly abilitiesInfo`: Array<AbilityInfo>
  - `readonly extensionAbilitiesInfo`: Array<ExtensionAbilityInfo>
  - `readonly metadata`: Array<Metadata>
  - `readonly deviceTypes`: Array<string>
  - `readonly installationFree`: boolean
  - `readonly hashValue`: string
  - `readonly type`: bundleManager.ModuleType
  - `readonly dependencies`: Array<Dependency>
  - `readonly preloads`: Array<PreloadItem>
  - `readonly fileContextMenuConfig`: string
- **Dependency**
  - `readonly moduleName`: string
  - `readonly bundleName`: string
  - `readonly versionCode`: number
- **PreloadItem**
  - `readonly moduleName`: string

### bundleManager/LauncherAbilityInfo.d.ts
#### Interfaces
- **LauncherAbilityInfo**
  - `readonly applicationInfo`: ApplicationInfo
  - `readonly elementName`: ElementName
  - `readonly labelId`: number
  - `readonly iconId`: number
  - `readonly userId`: number
  - `readonly installTime`: number

### bundleManager/LauncherAbilityResourceInfo.d.ts
#### Interfaces
- **LauncherAbilityResourceInfo**
  - `readonly bundleName`: string
  - `readonly moduleName`: string
  - `readonly abilityName`: string
  - `readonly icon`: string
  - `readonly label`: string

### bundleManager/Metadata.d.ts
#### Interfaces
- **Metadata**
  - `name`: string
  - `value`: string
  - `resource`: string

### bundleManager/OverlayModuleInfo.d.ts
#### Interfaces
- **OverlayModuleInfo**
  - `readonly bundleName`: string
  - `readonly moduleName`: string
  - `readonly targetModuleName`: string
  - `readonly priority`: number
  - `readonly state`: number

### bundleManager/PermissionDef.d.ts
#### Interfaces
- **PermissionDef**
  - `readonly permissionName`: string
  - `readonly grantMode`: number
  - `readonly labelId`: number
  - `readonly descriptionId`: number

### bundleManager/RecoverableApplicationInfo.d.ts
#### Interfaces
- **RecoverableApplicationInfo**
  - `readonly bundleName`: string
  - `readonly moduleName`: string
  - `readonly labelId`: number
  - `readonly iconId`: number

### bundleManager/RemoteAbilityInfo.d.ts
#### Interfaces
- **RemoteAbilityInfo**
  - `readonly elementName`: ElementName
  - `readonly label`: string
  - `readonly icon`: string

### bundleManager/SharedBundleInfo.d.ts
#### Interfaces
- **SharedBundleInfo**
  - `readonly name`: string
  - `readonly compatiblePolicy`: bundleManager.CompatiblePolicy
  - `readonly sharedModuleInfo`: Array<SharedModuleInfo>
- **SharedModuleInfo**
  - `readonly name`: string
  - `readonly versionCode`: number
  - `readonly versionName`: string
  - `readonly description`: string
  - `readonly descriptionId`: number

### bundleManager/ShortcutInfo.d.ts
#### Interfaces
- **ShortcutInfo**
  - `readonly id`: string
  - `readonly bundleName`: string
  - `readonly moduleName`: string
  - `readonly hostAbility`: string
  - `readonly icon`: string
  - `readonly iconId`: number
  - `readonly label`: string
  - `readonly labelId`: number
  - `readonly wants`: Array<ShortcutWant>
- **ShortcutWant**
  - `readonly targetBundle`: string
  - `readonly targetModule`: string
  - `readonly targetAbility`: string

### common/full/canvaspattern.d.ts
#### Interfaces
- **CanvasPattern**
  - `setTransform`: void
#### Classes
- **Matrix2D**
  - `identity()`: Matrix2D
  - `invert()`: Matrix2D
  - `multiply()`: Matrix2D
  - `rotate()`: Matrix2D
  - `translate()`: Matrix2D
  - `scale()`: Matrix2D

### common/full/console.d.ts
#### Classes
- **console**
  - `debug()`: void
  - `log()`: void
  - `info()`: void
  - `warn()`: void
  - `error()`: void

### common/full/dom.d.ts
#### Classes
- **dom**

### common/full/featureability.d.ts
#### Interfaces
- **Result**
  - `code`: number
  - `data`: object
- **SubscribeMessageResponse**
  - `deviceId`: string
  - `bundleName`: string
  - `abilityName`: string
  - `message`: string
- **CallAbilityParam**
  - `bundleName`: string
  - `abilityName`: string
  - `messageCode`: number
  - `abilityType`: number
  - `data?`: object
  - `syncOption?`: number
- **SubscribeAbilityEventParam**
  - `bundleName`: string
  - `abilityName`: string
  - `messageCode`: number
  - `abilityType`: number
  - `syncOption?`: number
- **SendMessageOptions**
  - `deviceId`: string
  - `bundleName`: string
  - `abilityName`: string
  - `message?`: string
  - `success?`: () => void
  - `fail?`: (data: string, code: number) => void
  - `complete?`: () => void
- **SubscribeMessageOptions**
  - `success?`: (data: SubscribeMessageResponse) => void
  - `fail?`: (data: string, code: number) => void
- **RequestParams**
  - `bundleName?`: string
  - `abilityName?`: string
  - `entities?`: Array<string>
  - `action?`: string
  - `deviceType?`: number
  - `data?`: object
  - `flag?`: number
  - `url?`: string
- **FinishWithResultParams**
  - `code`: number
  - `result`: object
#### Classes
- **FeatureAbility**
  - `startAbility()`: Promise<Result>
  - `startAbilityForResult()`: Promise<Result>
  - `finishWithResult()`: Promise<Result>
  - `getDeviceList()`: Promise<Result>
  - `callAbility()`: Promise<string>
  - `continueAbility()`: Promise<Result>
  - `subscribeAbilityEvent()`: Promise<string>
  - `unsubscribeAbilityEvent()`: Promise<string>
  - `sendMsg()`: void
  - `subscribeMsg()`: void
  - `unsubscribeMsg()`: void

### common/full/global.d.ts
#### Functions
- `setInterval(handler: Function | string, delay: number, ...arguments: any[]): number`
- `setTimeout(handler: Function | string, delay?: number, ...arguments: any[]): number`
- `requestAnimationFrame(handler: Function): number`
- `cancelAnimationFrame(requestId: number): void`
- `clearInterval(intervalID?: number): void`
- `clearTimeout(timeoutID?: number): void`
- `createLocalParticleAbility(name?: string): any`
- `canIUse(syscap: string): boolean`
- `getApp(): object`
#### Classes
- **Image**
- **ImageData**
- **OffscreenCanvas**
  - `getContext()`: OffscreenCanvasRenderingContext2D
  - `toDataURL()`: string
  - `transferToImageBitmap()`: ImageBitmap
- **ImageBitmap**

### common/full/index.d.ts

### common/full/viewmodel.d.ts
#### Interfaces
- **FocusParamObj**
  - `focus`: boolean
- **RectObj**
  - `width`: number
  - `height`: number
  - `left`: number
  - `top`: number
- **ContextAttrOptions**
  - `antialias`: boolean
- **AnimateStyle**
  - `width`: number
  - `height`: number
  - `left`: number
  - `top`: number
  - `right`: number
  - `bottom`: number
  - `backgroundColor`: string
  - `opacity`: number
  - `backgroundPosition`: string
  - `transformOrigin`: string
  - `transform`: "none" | TransformObject
  - `offset?`: number
- **TransformObject**
  - `matrix`: void
  - `matrix3d`: void
  - `translate`: void
  - `translate3d`: void
  - `translateX`: void
  - `translateY`: void
  - `translateZ`: void
  - `scale`: void
  - `scale3d`: void
  - `scaleX`: void
  - `scaleY`: void
  - `scaleZ`: void
  - `rotate`: void
  - `rotate3d`: void
  - `rotateX`: void
  - `rotateY`: void
  - `rotateZ`: void
  - `skew`: void
  - `skewX`: void
  - `skewY`: void
  - `perspective`: void
- **AnimateOptions**
  - `duration`: number
  - `easing`: string
  - `delay`: number
  - `iterations`: number | string
  - `direction`: "normal" | "reverse" | "alternate" | "alternate-reverse"
  - `fill`: "none" | "forwards" | "backwards" | "both"
- **AnimationResult**
  - `finished`: boolean
  - `pending`: boolean
  - `playstate`: string
  - `startTime`: number
  - `play`: void
  - `finish`: void
  - `pause`: void
  - `cancel`: void
  - `reverse`: void
  - `onstart`: () => void
  - `onfinish`: () => void
  - `oncancel`: () => void
  - `onrepeat`: () => void
- **Element**
  - `focus`: void
  - `rotation`: void
  - `animate`: AnimationResult
  - `getBoundingClientRect`: RectObj
  - `getInspector`: string
  - `createIntersectionObserver`: observer
  - `addChild`: void
  - `setAttribute`: void
  - `setStyle`: boolean
- **observer**
  - `observe`: void
  - `unobserve`: void
- **AnimationElement**
  - `play`: void
  - `finish`: void
  - `pause`: void
  - `cancel`: void
  - `reverse`: void
- **ScrollParam**
  - `dx?`: number
  - `dy?`: number
  - `smooth?`: boolean
- **CurrentOffsetResultValue**
  - `x`: number
  - `y`: number
- **ListScrollToOptions**
  - `index`: number
- **ListElement**
  - `scrollTo`: void
  - `scrollBy`: void
  - `scrollTop`: void
  - `scrollBottom`: void
  - `scrollPage`: void
  - `scrollArrow`: void
  - `collapseGroup`: void
  - `expandGroup`: void
  - `currentOffset`: CurrentOffsetResultValue
- **SwiperElement**
  - `swipeTo`: void
  - `showNext`: void
  - `showPrevious`: void
- **CameraTakePhotoOptions**
  - `quality`: "high" | "normal" | "low"
  - `success?`: (result: Object) => void
  - `fail?`: (result: Object) => void
  - `complete?`: (result: Object) => void
- **CameraElement**
  - `takePhoto`: void
- **WebElement**
  - `reload`: void
- **DialogElement**
  - `show`: void
  - `close`: void
- **ImageAnimatorElement**
  - `start`: void
  - `pause`: void
  - `stop`: void
  - `resume`: void
  - `getState`: "Playing" | "Paused" | "Stopped"
- **MarqueeElement**
  - `start`: void
  - `stop`: void
- **MenuElement**
  - `show`: void
- **ChartElement**
  - `append`: void
- **InputElement**
  - `focus`: void
  - `showError`: void
  - `delete`: void
- **ButtonElement**
  - `setProgress`: void
- **TextAreaElement**
  - `focus`: void
- **PickerElement**
  - `show`: void
- **VideoElement**
  - `start`: void
  - `pause`: void
  - `setCurrentTime`: void
  - `requestFullscreen`: void
  - `exitFullscreen`: void
  - `stop`: void
- **TextMetrics**
  - `width`: number
  - `height`: number
- **OffscreenCanvasRenderingContext2D**
  - `getLineDash`: Array<number>
  - `fillStyle?`: string | CanvasGradient | CanvasPattern
  - `strokeStyle?`: string | CanvasGradient | CanvasPattern
  - `setLineDash`: void
  - `drawImage`: void
  - `drawImage`: void
  - `drawImage`: void
  - `drawImage`: void
  - `beginPath`: void
  - `clip`: void
  - `fill`: void
  - `isPointInPath`: boolean
  - `isPointInPath`: boolean
  - `isPointInStroke`: boolean
  - `isPointInStroke`: boolean
  - `stroke`: void
  - `stroke`: void
  - `createRadialGradient`: CanvasGradient
  - `createPattern`: CanvasPattern
  - `createLinearGradient`: CanvasGradient
  - `createImageData`: ImageData
  - `createImageData`: ImageData
  - `createPath2D`: Path2D
  - `createPath2D`: Path2D
  - `getImageData`: ImageData
  - `putImageData`: void
  - `putImageData`: void
  - `arc`: void
  - `arcTo`: void
  - `bezierCurveTo`: void
  - `closePath`: void
  - `lineTo`: void
  - `ellipse`: void
  - `moveTo`: void
  - `quadraticCurveTo`: void
  - `rect`: void
  - `clearRect`: void
  - `fillRect`: void
  - `strokeRect`: void
  - `fillText`: void
  - `measureText`: TextMetrics
  - `strokeText`: void
  - `resetTransform`: void
  - `rotate`: void
  - `scale`: void
  - `setTransform`: void
  - `transform`: void
  - `translate`: void
  - `restore`: void
  - `save`: void
- **CanvasRenderingContext2D**
  - `fillRect`: void
  - `fillStyle?`: string | CanvasGradient | CanvasPattern
  - `clearRect`: void
  - `strokeRect`: void
  - `fillText`: void
  - `strokeText`: void
  - `measureText`: TextMetrics
  - `lineWidth?`: number
  - `strokeStyle?`: string | CanvasGradient | CanvasPattern
  - `stroke`: void
  - `stroke`: void
  - `beginPath`: void
  - `moveTo`: void
  - `lineTo`: void
  - `closePath`: void
  - `lineCap`: string
  - `lineJoin`: string
  - `miterLimit`: number
  - `font`: string
  - `textAlign`: "left" | "right" | "center" | "start" | "end"
  - `imageSmoothingEnabled`: boolean
  - `textBaseline`: string
  - `createLinearGradient`: CanvasGradient
  - `createRadialGradient`: CanvasGradient
  - `createPattern`: object
  - `createPath2D`: Path2D
  - `createPath2D`: Path2D
  - `bezierCurveTo`: void
  - `quadraticCurveTo`: void
  - `arc`: void
  - `arcTo`: void
  - `ellipse`: void
  - `rect`: void
  - `fill`: void
  - `clip`: void
  - `rotate`: void
  - `scale`: void
  - `transform`: void
  - `setTransform`: void
  - `translate`: void
  - `globalAlpha`: number
  - `drawImage`: void
  - `drawImage`: void
  - `drawImage`: void
  - `drawImage`: void
  - `restore`: () => void
  - `save`: () => void
  - `createImageData`: ImageData
  - `createImageData`: ImageData
  - `getImageData`: ImageData
  - `putImageData`: void
  - `putImageData`: void
  - `setLineDash`: void
  - `getLineDash`: Array<number>
  - `lineDashOffset`: number
  - `globalCompositeOperation`: string
  - `shadowBlur`: number
  - `shadowColor`: string
  - `shadowOffsetX`: number
  - `shadowOffsetY`: number
  - `transferFromImageBitmap`: void
- **CanvasGradient**
  - `addColorStop`: void
- **Path2D**
  - `addPath`: void
  - `setTransform`: void
  - `closePath`: void
  - `moveTo`: void
  - `lineTo`: void
  - `bezierCurveTo`: void
  - `quadraticCurveTo`: void
  - `arc`: void
  - `arcTo`: void
  - `ellipse`: void
  - `rect`: void
- **CanvasElement**
  - `getContext`: CanvasRenderingContext2D
  - `getContext`: WebGLRenderingContext
  - `getContext`: WebGL2RenderingContext
  - `toDataURL`: string
- **ScrollOptions**
  - `position`: number
  - `duration`: number
  - `id?`: string
  - `timingFunction?`: string
  - `success?`: (result: Object) => void
  - `fail?`: (result: Object) => void
  - `complete?`: (result: Object) => void
- **ScrollOffset**
  - `x`: number
  - `y`: number
- **DivElement**
  - `scrollBy`: void
  - `getScrollOffset`: ScrollOffset
- **Application**
- **ViewModel**
  - `scrollTo`: void
- **ElementReferences**
#### Functions
- `extendViewModel<T extends ViewModel, Data>(options: CombinedOptions<T, Data>): ViewModel & Data`
#### Classes
- **Locate**
- **Configuration**
#### Type Aliases
- `DefaultData` = object
- `CombinedOptions` = object & Options<T, Data> & ThisType<T & ViewModel & Data>

### common/lite/console.d.ts
#### Classes
- **console**
  - `debug()`: void
  - `log()`: void
  - `info()`: void
  - `warn()`: void
  - `error()`: void

### common/lite/featureability.d.ts
#### Interfaces
- **SubscribeMessageResponse**
  - `deviceId`: string
  - `bundleName`: string
  - `abilityName`: string
  - `message`: string
- **SendMessageOptions**
  - `deviceId`: string
  - `bundleName`: string
  - `abilityName`: string
  - `message?`: string
  - `success?`: () => void
  - `fail?`: (data: string, code: number) => void
  - `complete?`: () => void
- **SubscribeMessageOptions**
  - `success?`: (data: SubscribeMessageResponse) => void
  - `fail?`: (data: string, code: number) => void
#### Classes
- **FeatureAbility**
  - `sendMsg()`: void
  - `subscribeMsg()`: void
  - `unsubscribeMsg()`: void

### common/lite/global.d.ts
#### Functions
- `setInterval(
  handler: Function,
  delay: number,
  ...arguments: any[]
): number`
- `setTimeout(
  handler: Function,
  delay?: number,
  ...arguments: any[]
): number`
- `clearInterval(intervalID?: number): void`
- `clearTimeout(timeoutID?: number): void`
- `createLocalParticleAbility(name?: string): any`
- `canIUse(syscap: string): boolean`
- `getApp(): object`

### common/lite/index.d.ts

### common/lite/viewmodel.d.ts
#### Interfaces
- **ViewModel**
- **ListScrollToOptions**
  - `index`: number
- **ListElement**
  - `scrollTo`: void
- **ImageAnimatorElement**
  - `start`: void
  - `pause`: void
  - `stop`: void
  - `resume`: void
  - `getState`: "Playing" | "Paused" | "Stopped"
- **ElementReferences**
#### Functions
- `extendViewModel<T extends ViewModel, Data>(
  options: CombinedOptions<T, Data>
): ViewModel & Data`
#### Type Aliases
- `DefaultData` = object
- `CombinedOptions` = object &
  Options<T, Data> &
  ThisType<T & ViewModel & Data>

### commonEvent/commonEventData.d.ts
#### Interfaces
- **CommonEventData**
  - `event`: string
  - `bundleName?`: string
  - `code?`: number
  - `data?`: string
  - `parameters?`: { [key: string]: any }

### commonEvent/commonEventPublishData.d.ts
#### Interfaces
- **CommonEventPublishData**
  - `bundleName?`: string
  - `code?`: number
  - `data?`: string
  - `subscriberPermissions?`: Array<string>
  - `isOrdered?`: boolean
  - `isSticky?`: boolean
  - `parameters?`: { [key: string]: any }

### commonEvent/commonEventSubscribeInfo.d.ts
#### Interfaces
- **CommonEventSubscribeInfo**
  - `events`: Array<string>
  - `publisherPermission?`: string
  - `publisherDeviceId?`: string
  - `userId?`: number
  - `priority?`: number
  - `publisherBundleName?`: string

### commonEvent/commonEventSubscriber.d.ts
#### Interfaces
- **CommonEventSubscriber**
  - `getCode`: void
  - `getCode`: Promise<number>
  - `getCodeSync`: number
  - `setCode`: void
  - `setCode`: Promise<void>
  - `setCodeSync`: void
  - `getData`: void
  - `getData`: Promise<string>
  - `getDataSync`: string
  - `setData`: void
  - `setData`: Promise<void>
  - `setDataSync`: void
  - `setCodeAndData`: void
  - `setCodeAndData`: Promise<void>
  - `setCodeAndDataSync`: void
  - `isOrderedCommonEvent`: void
  - `isOrderedCommonEvent`: Promise<boolean>
  - `isOrderedCommonEventSync`: boolean
  - `isStickyCommonEvent`: void
  - `isStickyCommonEvent`: Promise<boolean>
  - `isStickyCommonEventSync`: boolean
  - `abortCommonEvent`: void
  - `abortCommonEvent`: Promise<void>
  - `abortCommonEventSync`: void
  - `clearAbortCommonEvent`: void
  - `clearAbortCommonEvent`: Promise<void>
  - `clearAbortCommonEventSync`: void
  - `getAbortCommonEvent`: void
  - `getAbortCommonEvent`: Promise<boolean>
  - `getAbortCommonEventSync`: boolean
  - `getSubscribeInfo`: void
  - `getSubscribeInfo`: Promise<CommonEventSubscribeInfo>
  - `getSubscribeInfoSync`: CommonEventSubscribeInfo
  - `finishCommonEvent`: void
  - `finishCommonEvent`: Promise<void>

### continuation/continuationExtraParams.d.ts
#### Interfaces
- **ContinuationExtraParams**
  - `deviceType?`: Array<string>
  - `targetBundle?`: string
  - `description?`: string
  - `filter?`: any
  - `continuationMode?`: continuationManager.ContinuationMode
  - `authInfo?`: Record<string, Object>

### continuation/continuationResult.d.ts
#### Interfaces
- **ContinuationResult**
  - `id`: string
  - `type`: string
  - `name`: string

### data/rdb/resultSet.d.ts
#### Interfaces
- **ResultSet**
  - `columnNames`: Array<string>
  - `columnCount`: number
  - `rowCount`: number
  - `rowIndex`: number
  - `isAtFirstRow`: boolean
  - `isAtLastRow`: boolean
  - `isEnded`: boolean
  - `isStarted`: boolean
  - `isClosed`: boolean
  - `getColumnIndex`: number
  - `getColumnName`: string
  - `goTo`: boolean
  - `goToRow`: boolean
  - `goToFirstRow`: boolean
  - `goToLastRow`: boolean
  - `goToNextRow`: boolean
  - `goToPreviousRow`: boolean
  - `getBlob`: Uint8Array
  - `getString`: string
  - `getLong`: number
  - `getDouble`: number
  - `isColumnNull`: boolean
  - `close`: void

### global/rawFileDescriptor.d.ts
#### Interfaces
- **RawFileDescriptor**
  - `fd`: number
  - `offset`: number
  - `length`: number

### global/resource.d.ts
#### Interfaces
- **Resource**
  - `bundleName`: string
  - `moduleName`: string
  - `id`: number
  - `params?`: any[]
  - `type?`: number

### multimedia/ringtonePlayer.d.ts
#### Interfaces
- **RingtoneOptions**
  - `volume`: number
  - `loop`: boolean
- **RingtonePlayer**
  - `readonly state`: media.AVPlayerState
  - `getTitle`: void
  - `getTitle`: Promise<string>
  - `getAudioRendererInfo`: void
  - `getAudioRendererInfo`: Promise<audio.AudioRendererInfo>
  - `configure`: void
  - `configure`: Promise<void>
  - `start`: void
  - `start`: Promise<void>
  - `stop`: void
  - `stop`: Promise<void>
  - `release`: void
  - `release`: Promise<void>
  - `on`: void

### multimedia/soundPool.d.ts
#### Interfaces
- **PlayParameters**
  - `loop?`: number
  - `rate?`: number
  - `leftVolume?`: number
  - `rightVolume?`: number
  - `priority?`: number
  - `parallelPlayFlag?`: boolean
- **SoundPool**
  - `load`: void
  - `load`: Promise<number>
  - `load`: void
  - `load`: Promise<number>
  - `play`: void
  - `play`: void
  - `play`: Promise<number>
  - `stop`: void
  - `stop`: Promise<void>
  - `setLoop`: void
  - `setLoop`: Promise<void>
  - `setPriority`: void
  - `setPriority`: Promise<void>
  - `setRate`: void
  - `setRate`: Promise<void>
  - `setVolume`: void
  - `setVolume`: Promise<void>
  - `unload`: void
  - `unload`: Promise<void>
  - `release`: void
  - `release`: Promise<void>
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void

### multimedia/systemTonePlayer.d.ts
#### Interfaces
- **SystemTonePlayer**
  - `getTitle`: Promise<string>
  - `prepare`: Promise<void>
  - `start`: Promise<number>
  - `stop`: Promise<void>
  - `release`: Promise<void>
- **SystemToneOptions**
  - `muteAudio?`: boolean
  - `muteHaptics?`: boolean

### notification/NotificationCommonDef.d.ts
#### Interfaces
- **BundleOption**
  - `bundle`: string
  - `uid?`: number

### notification/notificationActionButton.d.ts
#### Interfaces
- **NotificationActionButton**
  - `title`: string
  - `wantAgent`: WantAgent
  - `extras?`: { [key: string]: any }
  - `userInput?`: NotificationUserInput

### notification/notificationContent.d.ts
#### Interfaces
- **NotificationBasicContent**
  - `title`: string
  - `text`: string
  - `additionalText?`: string
- **NotificationLongTextContent**
  - `longText`: string
  - `briefText`: string
  - `expandedTitle`: string
- **NotificationLiveViewContent**
  - `status`: LiveViewStatus
  - `version?`: number
  - `extraInfo?`: Record<string, Object>
  - `pictureInfo?`: Record<string, Array<image.PixelMap>>
- **NotificationMultiLineContent**
  - `briefText`: string
  - `longTitle`: string
  - `lines`: Array<string>
- **NotificationPictureContent**
  - `briefText`: string
  - `expandedTitle`: string
  - `picture`: image.PixelMap
- **NotificationSystemLiveViewContent**
  - `typeCode`: number
  - `capsule?`: NotificationCapsule
  - `button?`: NotificationButton
  - `time?`: NotificationTime
  - `progress?`: NotificationProgress
- **NotificationCapsule**
  - `title?`: string
  - `icon?`: image.PixelMap
  - `backgroundColor?`: string
- **NotificationButton**
  - `names?`: Array<string>
  - `icons?`: Array<image.PixelMap>
- **NotificationTime**
  - `initialTime?`: number
  - `isCountDown?`: boolean
  - `isPaused?`: boolean
  - `isInTitle?`: boolean
- **NotificationProgress**
  - `maxValue?`: number
  - `currentValue?`: number
  - `isPercentage?`: boolean
- **NotificationContent**
  - `contentType?`: notification.ContentType
  - `notificationContentType?`: notificationManager.ContentType
  - `normal?`: NotificationBasicContent
  - `longText?`: NotificationLongTextContent
  - `multiLine?`: NotificationMultiLineContent
  - `picture?`: NotificationPictureContent
  - `systemLiveView?`: NotificationSystemLiveViewContent
  - `liveView?`: NotificationLiveViewContent
#### Enums
- **LiveViewStatus**
  - `LIVE_VIEW_CREATE` = 0
  - `LIVE_VIEW_INCREMENTAL_UPDATE` = 1
  - `LIVE_VIEW_END` = 2
  - `LIVE_VIEW_FULL_UPDATE` = 3

### notification/notificationFlags.d.ts
#### Interfaces
- **NotificationFlags**
  - `readonly soundEnabled?`: NotificationFlagStatus
  - `readonly vibrationEnabled?`: NotificationFlagStatus
  - `readonly reminderFlags?`: number
#### Enums
- **NotificationFlagStatus**
  - `TYPE_NONE` = 0
  - `TYPE_OPEN` = 1
  - `TYPE_CLOSE` = 2

### notification/notificationRequest.d.ts
#### Interfaces
- **NotificationRequest**
  - `content`: NotificationContent
  - `id?`: number
  - `slotType?`: notification.SlotType
  - `notificationSlotType?`: notificationManager.SlotType
  - `isOngoing?`: boolean
  - `isUnremovable?`: boolean
  - `deliveryTime?`: number
  - `tapDismissed?`: boolean
  - `autoDeletedTime?`: number
  - `wantAgent?`: WantAgent
  - `extraInfo?`: { [key: string]: any }
  - `color?`: number
  - `colorEnabled?`: boolean
  - `isAlertOnce?`: boolean
  - `isStopwatch?`: boolean
  - `isCountDown?`: boolean
  - `isFloatingIcon?`: boolean
  - `label?`: string
  - `badgeIconStyle?`: number
  - `showDeliveryTime?`: boolean
  - `actionButtons?`: Array<NotificationActionButton>
  - `smallIcon?`: image.PixelMap
  - `largeIcon?`: image.PixelMap
  - `overlayIcon?`: image.PixelMap
  - `groupName?`: string
  - `readonly creatorBundleName?`: string
  - `readonly creatorUid?`: number
  - `readonly creatorPid?`: number
  - `readonly creatorUserId?`: number
  - `classification?`: string
  - `readonly hashCode?`: string
  - `isRemoveAllowed?`: boolean
  - `readonly source?`: number
  - `template?`: NotificationTemplate
  - `distributedOption?`: DistributedOptions
  - `readonly deviceId?`: string
  - `readonly notificationFlags?`: NotificationFlags
  - `removalWantAgent?`: WantAgent
  - `badgeNumber?`: number
- **DistributedOptions**
  - `isDistributed?`: boolean
  - `supportDisplayDevices?`: Array<string>
  - `supportOperateDevices?`: Array<string>
  - `readonly remindType?`: number
- **NotificationFilter**
  - `bundle`: BundleOption
  - `notificationKey`: notificationSubscribe.NotificationKey
  - `extraInfoKeys?`: Array<string>
- **NotificationCheckRequest**
  - `contentType`: notificationManager.ContentType
  - `slotType`: notificationManager.SlotType
  - `extraInfoKeys`: Array<string>

### notification/notificationSlot.d.ts
#### Interfaces
- **NotificationSlot**
  - `type?`: notification.SlotType
  - `notificationType?`: notificationManager.SlotType
  - `level?`: notification.SlotLevel
  - `desc?`: string
  - `badgeFlag?`: boolean
  - `bypassDnd?`: boolean
  - `lockscreenVisibility?`: number
  - `vibrationEnabled?`: boolean
  - `sound?`: string
  - `lightEnabled?`: boolean
  - `lightColor?`: number
  - `vibrationValues?`: Array<number>
  - `readonly enabled?`: boolean
  - `readonly reminderMode?`: number

### notification/notificationSorting.d.ts
#### Interfaces
- **NotificationSorting**
  - `readonly slot`: NotificationSlot
  - `readonly hashCode`: string
  - `readonly ranking`: number

### notification/notificationSortingMap.d.ts
#### Interfaces
- **NotificationSortingMap**
  - `readonly sortings`: Record<string, NotificationSorting>
  - `readonly sortedHashCode`: Array<string>

### notification/notificationSubscribeInfo.d.ts
#### Interfaces
- **NotificationSubscribeInfo**
  - `bundleNames?`: Array<string>
  - `userId?`: number

### notification/notificationSubscriber.d.ts
#### Interfaces
- **NotificationSubscriber**
  - `onConsume?`: (data: SubscribeCallbackData) => void
  - `onCancel?`: (data: SubscribeCallbackData) => void
  - `onUpdate?`: (data: NotificationSortingMap) => void
  - `onConnect?`: () => void
  - `onDisconnect?`: () => void
  - `onDestroy?`: () => void
  - `onDoNotDisturbDateChange?`: (mode: notification.DoNotDisturbDate) => void
  - `onDoNotDisturbChanged?`: (mode: notificationManager.DoNotDisturbDate) => void
  - `onEnabledNotificationChanged?`: (callbackData: EnabledNotificationCallbackData) => void
  - `onBadgeChanged?`: (data: BadgeNumberCallbackData) => void
  - `onBatchCancel?`: (data: Array<SubscribeCallbackData>) => void
- **SubscribeCallbackData**
  - `readonly request`: NotificationRequest
  - `readonly sortingMap?`: NotificationSortingMap
  - `readonly reason?`: number
  - `readonly sound?`: string
  - `readonly vibrationValues?`: Array<number>
- **EnabledNotificationCallbackData**
  - `readonly bundle`: string
  - `readonly uid`: number
  - `readonly enable`: boolean
- **BadgeNumberCallbackData**
  - `readonly bundle`: string
  - `readonly uid`: number
  - `readonly badgeNumber`: number

### notification/notificationTemplate.d.ts
#### Interfaces
- **NotificationTemplate**
  - `name`: string
  - `data`: Record<string, Object>

### notification/notificationUserInput.d.ts
#### Interfaces
- **NotificationUserInput**
  - `inputKey`: string

### security/PermissionRequestResult.d.ts
#### Classes
- **PermissionRequestResult**

### tag/nfctech.d.ts
#### Interfaces
- **NfcATag**
  - `getSak`: number
  - `getAtqa`: number[]
- **NfcBTag**
  - `getRespAppData`: number[]
  - `getRespProtocol`: number[]
- **NfcFTag**
  - `getSystemCode`: number[]
  - `getPmm`: number[]
- **NfcVTag**
  - `getResponseFlags`: number
  - `getDsfId`: number
- **IsoDepTag**
  - `getHistoricalBytes`: number[]
  - `getHiLayerResponse`: number[]
  - `isExtendedApduSupported`: Promise<boolean>
  - `isExtendedApduSupported`: void
- **NdefMessage**
  - `getNdefRecords`: tag.NdefRecord[]
- **NdefTag**
  - `getNdefTagType`: tag.NfcForumType
  - `getNdefMessage`: NdefMessage
  - `isNdefWritable`: boolean
  - `readNdef`: Promise<NdefMessage>
  - `readNdef`: void
  - `writeNdef`: Promise<void>
  - `writeNdef`: void
  - `canSetReadOnly`: boolean
  - `setReadOnly`: Promise<void>
  - `setReadOnly`: void
  - `getNdefTagTypeString`: string
- **MifareClassicTag**
  - `authenticateSector`: Promise<void>
  - `authenticateSector`: void
  - `readSingleBlock`: Promise<number[]>
  - `readSingleBlock`: void
  - `writeSingleBlock`: Promise<void>
  - `writeSingleBlock`: void
  - `incrementBlock`: Promise<void>
  - `incrementBlock`: void
  - `decrementBlock`: Promise<void>
  - `decrementBlock`: void
  - `transferToBlock`: Promise<void>
  - `transferToBlock`: void
  - `restoreFromBlock`: Promise<void>
  - `restoreFromBlock`: void
  - `getSectorCount`: number
  - `getBlockCountInSector`: number
  - `getType`: tag.MifareClassicType
  - `getTagSize`: number
  - `isEmulatedTag`: boolean
  - `getBlockIndex`: number
  - `getSectorIndex`: number
- **MifareUltralightTag**
  - `readMultiplePages`: Promise<number[]>
  - `readMultiplePages`: void
  - `writeSinglePage`: Promise<void>
  - `writeSinglePage`: void
  - `getType`: tag.MifareUltralightType
- **NdefFormatableTag**
  - `format`: Promise<void>
  - `format`: void
  - `formatReadOnly`: Promise<void>
  - `formatReadOnly`: void

### tag/tagSession.d.ts
#### Interfaces
- **TagSession**
  - `getTagInfo`: tag.TagInfo
  - `connectTag`: boolean
  - `connect`: void
  - `reset`: void
  - `resetConnection`: void
  - `isTagConnected`: boolean
  - `isConnected`: boolean
  - `setSendDataTimeout`: boolean
  - `setTimeout`: void
  - `getSendDataTimeout`: number
  - `getTimeout`: number
  - `sendData`: Promise<number[]>
  - `sendData`: void
  - `transmit`: Promise<number[]>
  - `transmit`: void
  - `getMaxSendLength`: number
  - `getMaxTransmitSize`: number

### wantAgent/triggerInfo.d.ts
#### Interfaces
- **TriggerInfo**
  - `code`: number
  - `want?`: Want
  - `permission?`: string
  - `extraInfo?`: { [key: string]: any }
  - `extraInfos?`: Record<string, Object>

### wantAgent/wantAgentInfo.d.ts
#### Interfaces
- **WantAgentInfo**
  - `wants`: Array<Want>
  - `operationType?`: wantAgent.OperationType
  - `actionType?`: abilityWantAgent.OperationType
  - `requestCode`: number
  - `wantAgentFlags?`: Array<wantAgent.WantAgentFlags>
  - `actionFlags?`: Array<abilityWantAgent.WantAgentFlags>
  - `extraInfo?`: { [key: string]: any }
  - `extraInfos?`: Record<string, Object>