# A2OH-UI-REWRITE: Android View/XML → ArkUI Declarative Conversion

## Core Paradigm Shift

```
ANDROID: XML defines structure → Java/Kotlin imperatively mutates views at runtime
OPENHARMONY: build() method declares UI as a function of state → framework re-renders on state change
```

### Rules

1. DELETE all XML layouts. All UI lives in `build()` methods inside `@Component` structs.
2. DELETE all `findViewById` calls. Use `@State` / `@Prop` / `@Link` for data binding.
3. NEVER write imperative view manipulation (`view.setText()`, `view.setVisibility()`). Change the state variable; the framework re-renders.
4. Every Android Activity with a layout → one `@Component struct` with a `build()` method.
5. Every XML layout file → inline ArkUI in the corresponding component's `build()`.
6. Every Adapter → `LazyForEach` with an `IDataSource` implementation.
7. Every Fragment → a standalone `@Component`. Use `Navigation` for routing between them.
8. Resources: `R.string.xxx` → `$r('app.string.xxx')`, `R.drawable.xxx` → `$r('app.media.xxx')`.
9. Styles: `styles.xml` / `themes.xml` → `@Styles` function or `@Extend` decorator.
10. Dimensions: `dp` → `vp`, `sp` → `fp`. Direct numeric values default to `vp`.

### State Management Rules

| Android Pattern | ArkUI Equivalent | When to Use |
|----------------|-----------------|-------------|
| Local field + `view.setX()` | `@State` | Component-local reactive state |
| Parent passes data to child view | `@Prop` | One-way parent → child (copies value) |
| Two-way binding (rare in Android) | `@Link` | Two-way parent ↔ child (shared reference) |
| ViewModel / LiveData / StateFlow | `@Observed` class + `@ObjectLink` | Observed model objects |
| Singleton / global state | `AppStorage` or `LocalStorage` | App-wide or page-wide state |
| `onSaveInstanceState` | `PersistentStorage` or `@StorageLink` | Survive process kill |

## Widget Equivalence Table

| Android View | ArkUI Component | Notes |
|-------------|----------------|-------|
| `TextView` | `Text(content)` | `.fontSize()`, `.fontColor()`, `.maxLines()` |
| `Button` | `Button(label)` | Also `Button({ type: ButtonType.Capsule })` |
| `ImageView` | `Image(src)` | src = `$r('app.media.x')` or URL string |
| `EditText` | `TextInput({ text: val })` | Use `TextArea` for multiline |
| `RecyclerView` | `List` | With `LazyForEach` inside; see section below |
| `ScrollView` | `Scroll` | Wrap content in `Scroll() { Column() { ... } }` |
| `ViewPager` | `Swiper` | `.index()`, `.loop()`, `.autoPlay()` |
| `ProgressBar` | `Progress({ value, total, type })` | `type: ProgressType.Linear` or `.Ring` |
| `Switch` | `Toggle({ type: ToggleType.Switch })` | `.isOn(val)` |
| `CheckBox` | `Checkbox({ name, group })` | `.select(boolean)` |
| `RadioButton` | `Radio({ value, group })` | `.checked(boolean)` |
| `Spinner` | `Select(options[])` | `Select([{value:'A'},{value:'B'}])` |
| `WebView` | `Web({ src, controller })` | `import web_webview from '@ohos.web.webview'` |
| `SeekBar` | `Slider({ value, min, max })` | `.blockColor()`, `.trackColor()` |
| `AlertDialog` | `AlertDialog.show({...})` | Or `@CustomDialog` for complex dialogs |
| `Toast` | `promptAction.showToast({message})` | `import promptAction from '@ohos.promptAction'` |
| `Toolbar` / `ActionBar` | `Navigation() { ... }` with `.title()` `.menus()` | Built into Navigation |
| `FloatingActionButton` | `Button` + `.position()` + `.zIndex()` | No direct FAB; compose manually |
| `CardView` | `Column` + `.shadow()` + `.borderRadius()` | Or use `.backgroundBlurStyle()` |
| `TabLayout` + `ViewPager` | `Tabs` + `TabContent` | See layout section |

## Layout Conversion

### LinearLayout → Column / Row
```
RULE: LinearLayout(orientation=vertical) → Column()
RULE: LinearLayout(orientation=horizontal) → Row()
RULE: layout_weight → .layoutWeight(n)
RULE: gravity → .justifyContent() on parent, .alignSelf() on child
RULE: padding → .padding({ top, right, bottom, left })
RULE: layout_margin → .margin({ top, right, bottom, left })
```
### FrameLayout → Stack
```
RULE: FrameLayout → Stack()
RULE: Children stack in z-order (last child on top)
RULE: layout_gravity → .alignContent() on Stack or .position() on child
```
### RelativeLayout / ConstraintLayout → RelativeContainer
```
RULE: RelativeLayout → RelativeContainer()
RULE: ConstraintLayout → RelativeContainer()
RULE: Each child MUST have .id('uniqueId')
RULE: alignRules replace relative/constraint references:
  - layout_alignParentStart → .alignRules({ left: { anchor: '__container__', align: HorizontalAlign.Start } })
  - layout_below="@id/X" → .alignRules({ top: { anchor: 'X', align: VerticalAlign.Bottom } })
  - layout_toEndOf="@id/X" → .alignRules({ left: { anchor: 'X', align: HorizontalAlign.End } })
```

### GridLayout → Grid

```
RULE: GridLayout → Grid() { ... }
RULE: columnCount=3 → .columnsTemplate('1fr 1fr 1fr')
RULE: rowCount → .rowsTemplate('1fr 1fr')
RULE: Each child in a GridItem()
```

### TabLayout + ViewPager → Tabs

```
RULE: TabLayout + ViewPager + FragmentPagerAdapter → Tabs + TabContent
RULE: Each fragment page → one TabContent() block
RULE: @TabBuilder builds custom tab bar items
```

## RecyclerView + Adapter → List + LazyForEach

### Conversion Rules

```
RULE: RecyclerView → List({ space: 10 })
RULE: RecyclerView.Adapter → class implementing IDataSource
RULE: onCreateViewHolder + onBindViewHolder → @Builder or child @Component
RULE: ViewHolder is eliminated — no view holder pattern needed
RULE: getItemCount() → totalCount()
RULE: notifyDataSetChanged() → this.dataSource.notifyDataReload()
RULE: notifyItemInserted(i) → this.dataSource.notifyDataAdd(i)
RULE: LinearLayoutManager(VERTICAL) → List (default)
RULE: LinearLayoutManager(HORIZONTAL) → List() { ... }.listDirection(Axis.Horizontal)
RULE: GridLayoutManager(spanCount=2) → Grid().columnsTemplate('1fr 1fr')
RULE: DiffUtil → listeners.forEach(l => l.onDataChange(index)) on IDataSource
```

### IDataSource Template

```typescript
// ALWAYS implement this interface for LazyForEach
class MyDataSource implements IDataSource {
  private data: MyItem[] = []
  private listeners: DataChangeListener[] = []
  totalCount(): number { return this.data.length }
  getData(index: number): MyItem { return this.data[index] }
  registerDataChangeListener(l: DataChangeListener): void { this.listeners.push(l) }
  unregisterDataChangeListener(l: DataChangeListener): void {
    const i = this.listeners.indexOf(l); if (i >= 0) this.listeners.splice(i, 1)
  }
  reload(): void { this.listeners.forEach(l => l.onDataReloaded()) }
  addItem(item: MyItem): void {
    this.data.push(item); this.listeners.forEach(l => l.onDataAdd(this.data.length - 1))
  }
}
```

## Fragment → @Component + Navigation

```
RULE: Fragment class → @Component struct (standalone)
RULE: FragmentManager.replace() → NavPathStack.pushPath()
RULE: FragmentTransaction.addToBackStack() → NavPathStack handles back stack automatically
RULE: onCreateView inflating XML → build() method
RULE: getArguments().getString("key") → params from NavPathStack or @Prop
RULE: Activity hosting fragments → Navigation() component with NavRouter/NavDestination
```

```typescript
// Fragment host activity → Navigation page
@Entry @Component struct MainPage {
  private navStack: NavPathStack = new NavPathStack()
  build() {
    Navigation(this.navStack) { /* default content */ }
      .navDestination(this.routeMap)
  }
  @Builder routeMap(name: string, params: Object) {
    if (name === 'detail') { DetailComponent({ item: params as ItemModel }) }
    if (name === 'settings') { SettingsComponent() }
  }
}
```

## Event Handling Conversion

| Android Listener | ArkUI Declarative Event |
|-----------------|------------------------|
| `setOnClickListener(v -> ...)` | `.onClick(() => { ... })` |
| `setOnLongClickListener(v -> ...)` | `.gesture(LongPressGesture().onAction(() => { ... }))` |
| `addTextChangedListener(watcher)` | `.onChange((value: string) => { ... })` on TextInput |
| `setOnScrollListener` | `.onScroll((offset, state) => { ... })` |
| `setOnItemClickListener` | Handle in `ListItem` `.onClick()` |
| `setOnCheckedChangeListener` | `.onChange((isOn: boolean) => { ... })` |
| `setOnTouchListener` | `.onTouch((event: TouchEvent) => { ... })` |
| `setOnFocusChangeListener` | `.onFocus(() => ...)` + `.onBlur(() => ...)` |
| `onBackPressed()` | `.onBackPress(() => { ... return true })` on `@Entry` |
| `SwipeRefreshLayout.setOnRefreshListener` | `Refresh({ refreshing: $var }) { List() {} }` |

```
RULE: All Android listeners become chained method calls on the component.
RULE: Never store references to views. Events update @State, which triggers re-render.
RULE: GestureDetector → use .gesture() with TapGesture, PanGesture, PinchGesture, RotationGesture.
```

## Animation Conversion

| Android | ArkUI |
|---------|-------|
| `ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)` | `.opacity(val)` with `animateTo()` |
| `view.animate().translationX(100)` | `.translate({ x: val })` with `.animation()` |
| `TransitionManager.beginDelayedTransition` | `.transition()` on appearing/disappearing components |
| `AnimatorSet` (sequential) | `animateTo` chained in `onFinish` callbacks |
| `RecyclerView.ItemAnimator` | `List` has built-in transitions via `.transition()` |
| `LayoutTransition` | `.transition(TransitionEffect.OPACITY.combine(TransitionEffect.scale()))` |
| Lottie | `@ohos/lottie` third-party library (same API) |

```
RULE: Property animation → change @State in animateTo({ duration, curve }, () => { this.x = newVal })
RULE: Transition animation → .transition(TransitionEffect.xxx) on conditionally rendered components
RULE: Shared element → geometryTransition('sharedId') on both source and target
```

## Custom View → Custom @Component

```
RULE: class MyView extends View → @Component struct MyView
RULE: Custom XML attrs → @Prop or @Link parameters
RULE: onDraw(Canvas) → build() with Canvas component or Stack of shapes (Circle, Rect, Path)
RULE: onMeasure/onLayout → not needed; use .width(), .height(), .constraintSize()
RULE: invalidate() → not needed; change @State and framework re-renders
RULE: attrs.xml (custom attributes) → component's @Prop fields
```

## Complete Before/After Example: List Screen with Item Click

### BEFORE: Android (3 files)

```xml
<!-- activity_main.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <TextView android:id="@+id/tvTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/app_name"
        android:textSize="20sp"
        android:padding="16dp"/>
    <EditText android:id="@+id/etSearch"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Search..."
        android:layout_marginHorizontal="16dp"/>
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"/>
</LinearLayout>
```

```java
// MainActivity.java
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private EditText etSearch;
    private List<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvTitle = findViewById(R.id.tvTitle);
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter(items, item -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("itemId", item.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        loadItems();

        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) {}
            public void afterTextChanged(Editable s) { filterItems(s.toString()); }
        });
    }

    private void loadItems() {
        // fetch items...
        items.addAll(fetchedItems);
        adapter.notifyDataSetChanged();
    }

    private void filterItems(String query) {
        adapter.updateList(items.stream()
            .filter(i -> i.getName().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList()));
    }
}
```

```java
// ItemAdapter.java
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH> {
    private List<Item> data;
    private OnItemClickListener listener;

    public ItemAdapter(List<Item> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_row, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(VH holder, int position) {
        Item item = data.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDesc.setText(item.getDescription());
        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override public int getItemCount() { return data.size(); }

    public void updateList(List<Item> newData) {
        data = newData;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc;
        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvDesc = v.findViewById(R.id.tvDesc);
        }
    }

    interface OnItemClickListener { void onClick(Item item); }
}
```

### AFTER: OpenHarmony (1 file)

```typescript
// pages/MainPage.ets
import { router } from '@kit.ArkUI'
import { promptAction } from '@kit.ArkUI'

class ItemDataSource implements IDataSource {
  private items: ItemModel[] = []
  private listeners: DataChangeListener[] = []

  totalCount(): number { return this.items.length }
  getData(index: number): ItemModel { return this.items[index] }

  registerDataChangeListener(l: DataChangeListener): void { this.listeners.push(l) }
  unregisterDataChangeListener(l: DataChangeListener): void {
    const i = this.listeners.indexOf(l)
    if (i >= 0) this.listeners.splice(i, 1)
  }

  replaceAll(newItems: ItemModel[]): void {
    this.items = newItems
    this.listeners.forEach(l => l.onDataReloaded())
  }
}

interface ItemModel {
  id: string
  name: string
  description: string
}

@Entry
@Component
struct MainPage {
  @State private searchText: string = ''
  @State private allItems: ItemModel[] = []
  private dataSource: ItemDataSource = new ItemDataSource()

  aboutToAppear(): void {
    this.loadItems()
  }

  private loadItems(): void {
    // fetch items...
    const fetched: ItemModel[] = [] // A2OH-TODO: wire up actual data fetch
    this.allItems = fetched
    this.dataSource.replaceAll(fetched)
  }

  private filterItems(): void {
    const q = this.searchText.toLowerCase()
    const filtered = this.allItems.filter(i => i.name.toLowerCase().includes(q))
    this.dataSource.replaceAll(filtered)
  }

  build() {
    Column() {
      Text($r('app.string.app_name'))
        .fontSize(20)
        .width('100%')
        .padding(16)

      TextInput({ placeholder: 'Search...' })
        .width('100%')
        .margin({ left: 16, right: 16 })
        .onChange((value: string) => {
          this.searchText = value
          this.filterItems()
        })

      List({ space: 8 }) {
        LazyForEach(this.dataSource, (item: ItemModel) => {
          ListItem() {
            Column() {
              Text(item.name)
                .fontSize(16)
                .fontWeight(FontWeight.Bold)
              Text(item.description)
                .fontSize(14)
                .fontColor('#666666')
                .margin({ top: 4 })
            }
            .width('100%')
            .padding(12)
          }
          .onClick(() => {
            router.pushUrl({ url: 'pages/DetailPage', params: { itemId: item.id } })
          })
        }, (item: ItemModel) => item.id)
      }
      .width('100%')
      .layoutWeight(1)
    }
    .width('100%')
    .height('100%')
  }
}
```

## Quick Reference: Common Pitfalls

```
PITFALL: Storing component references → FIX: use @State and let framework manage rendering.
PITFALL: Calling .visibility(View.GONE) → FIX: use `if (this.show) { Component() }` for conditional rendering.
PITFALL: Runnable / Handler.postDelayed → FIX: setTimeout(() => { this.state = x }, ms).
PITFALL: getContext() in adapter → FIX: not needed; components access context via getContext(this).
PITFALL: dp/sp literal numbers → FIX: just use numbers (vp is default), use fp for font sizes.
PITFALL: R.color.xxx in code → FIX: $r('app.color.xxx') or Color.xxx for standard colors.
PITFALL: match_parent → FIX: .width('100%').height('100%').
PITFALL: wrap_content → FIX: omit .width()/.height() (default is fit-to-content).
PITFALL: Adapter with multiple view types → FIX: use if/else in LazyForEach builder to pick different @Builder functions.
```
