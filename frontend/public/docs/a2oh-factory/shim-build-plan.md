# API Shim Layer: AI-Driven Build Plan

## Executive Summary

Build a transparent Android-to-OHOS API translation layer using AI-assisted code generation, leveraging the existing `api_compat.db` (57,289 Android APIs mapped with scores), 3 mock Android apps, automated OHOS build+QEMU testing, and headless ArkUI testing. The system will iteratively generate shim code, compile it, test it on both Android and OHOS sides, and feed results back to improve generation quality.

### Existing Assets

| Asset | Description | Location |
|-------|-------------|----------|
| API Database | 57,289 Android APIs + 29,290 OH APIs, scored 1-10 | `database/api_compat.db` |
| Shim Classes | 28+ Java classes (3,105 lines) | `shim/java/android/` |
| JNI Bridge | Rust + C++ bridge (2,658 lines) | `shim/bridge/` |
| Backend API | FastAPI + SQLite query system | `backend/` |
| Frontend | React dashboard with score/effort visualization | `frontend/` |
| Test Apps | FlashNote, headless CLI, UI mockup | `test-apps/` |
| Auto Generator | Self-evolving shim loop (416 lines) | `a2oh-loop.sh` |
| ArkUI Tests | Standalone CMake, 26 button tests in ~3ms | `~/openharmony/arkui_test_standalone/` |
| OHOS Build | GN+Ninja, qemu-arm-linux-min product | `~/openharmony/` |
| QEMU Runtime | Extracted qemu-system-arm, bootable images | `~/openharmony/tools/qemu-extracted/` |
| Skill Guides | 11 migration skill documents | `skills/` |

### API Tier Summary

| Tier | Score | Count | % | Mapping Type | Strategy |
|------|-------|-------|---|--------------|----------|
| Tier 1 | 8-10 | 7,457 | 13% | direct/near | AI generates, auto-tests |
| Tier 2 | 5-7 | 14,777 | 26% | partial/composite | AI generates with richer context, human review |
| Tier 3 | 1-4 | 8,534 | 15% | structural gap | UI rewrite or Dalvik fallback |
| None | 0 | 26,521 | 46% | no equivalent | Out of scope or Dalvik fallback |

---

## System Architecture

```mermaid
graph TD
    subgraph Input["Data Sources"]
        DB["api_compat.db<br/>57,289 API mappings<br/>scores + guides"]
        EXISTING["Existing Shim Code<br/>34 classes shimmed"]
    end

    subgraph AI["AI Code Generation"]
        PROMPT["Build AI Prompt<br/>mapping table + guides + examples"]
        CLAUDE["Claude API<br/>prompts + feedback loop"]
    end

    subgraph Output["Generated Artifacts"]
        JAVA["Java Shim<br/>android.* classes"]
        BRIDGE["JNI/FFI Bridge<br/>Rust + C++"]
        TESTS["Test Cases<br/>JUnit + gtest"]
    end

    subgraph Targets["Runtime Targets"]
        ANDROID["Android Tests<br/>JVM mock"]
        OHOS["OHOS Native APIs<br/>@ohos.* / NDK"]
        PIPELINE["3-Level Pipeline<br/>Mock / Headless / QEMU"]
    end

    subgraph Track["Progress Tracking"]
        PROGRESS["shim_progress.db<br/>status + results"]
    end

    DB --> PROMPT
    EXISTING --> PROMPT
    PROMPT --> CLAUDE
    CLAUDE --> JAVA
    CLAUDE --> BRIDGE
    CLAUDE --> TESTS
    JAVA --> ANDROID
    BRIDGE --> OHOS
    TESTS --> PIPELINE
    PIPELINE --> PROGRESS
    PROGRESS -.->|feedback| PROMPT

    style DB fill:#1e3a5f,stroke:#3b82f6,color:#e5e7eb
    style CLAUDE fill:#6d28d9,stroke:#7c3aed,color:#e5e7eb
    style PROGRESS fill:#065f46,stroke:#10b981,color:#e5e7eb
    style JAVA fill:#92400e,stroke:#f59e0b,color:#e5e7eb
    style BRIDGE fill:#92400e,stroke:#f59e0b,color:#e5e7eb
    style TESTS fill:#92400e,stroke:#f59e0b,color:#e5e7eb
```

### Shim Layer Architecture (3 Layers)

```mermaid
graph TD
    APK["APK Bytecode<br/>unmodified .dex files"]

    subgraph L1["Layer 1: Java Shim (~3,105 lines)"]
        direction LR
        APP["android.app.*<br/>Activity, Service<br/>Notification"]
        CONTENT["android.content.*<br/>Context, Intent<br/>SharedPreferences"]
        OS["android.os.*<br/>Bundle, Handler<br/>Looper"]
        VIEW["android.view.*<br/>View, ViewGroup<br/>LayoutInflater"]
        WIDGET["android.widget.*<br/>Button, TextView<br/>EditText, Toast"]
        NET["android.net.*<br/>Uri, HttpURL"]
    end

    subgraph L2["Layer 2: JNI/FFI Bridge (~2,658 lines Rust/C++)"]
        PREFS["preferences.rs<br/>SharedPreferences"]
        RDB["rdb_store.rs<br/>SQLiteDatabase"]
        NAV["navigation.rs<br/>Activity lifecycle"]
        NOTIF["notification.rs<br/>Notifications"]
        LOG["logging.rs<br/>Log → HiLog"]
        VBRIDGE["view.rs<br/>View rendering"]
    end

    subgraph L3["Layer 3: OpenHarmony Native APIs"]
        direction LR
        DATA["@ohos.data.*<br/>preferences<br/>relationalStore"]
        ABILITY["@ohos.app.ability<br/>UIAbility"]
        OHNOTIF["@ohos.notification"]
        OHNET["@ohos.net.http"]
        MEDIA["@ohos.multimedia"]
        ARKUI["ArkUI<br/>declarative UI"]
    end

    APK -->|calls android.*| L1
    L1 -->|JNI calls| L2
    L2 -->|FFI calls| L3

    style APK fill:#1f2937,stroke:#6b7280,color:#e5e7eb
    style L1 fill:#1e3a5f,stroke:#3b82f6,color:#e5e7eb
    style L2 fill:#6d28d9,stroke:#7c3aed,color:#e5e7eb
    style L3 fill:#065f46,stroke:#10b981,color:#e5e7eb
```

### End-to-End Pipeline Flow

```mermaid
flowchart TD
    START["api_compat.db"] --> QUEUE["Priority Queue<br/>unimplemented classes"]
    QUEUE --> PROMPT["Build AI Prompt<br/>API mappings + guides<br/>+ code examples"]
    PROMPT --> GEN["Claude API Generate<br/>1. Java shim class<br/>2. JUnit test<br/>3. Rust bridge<br/>4. gtest test"]

    GEN --> ACOMPILE["Android Compile<br/>javac"]
    GEN --> OCOMPILE["OHOS Compile<br/>CMake + Clang"]

    ACOMPILE --> AFAIL{Compile OK?}
    OCOMPILE --> OFAIL{Compile OK?}

    AFAIL -->|No| AFEEDBACK["Feed errors to AI<br/>retry max 3x"]
    AFEEDBACK --> GEN
    OFAIL -->|No| OFEEDBACK["Feed errors to AI<br/>retry max 3x"]
    OFEEDBACK --> GEN

    AFAIL -->|Yes| MOCK1["Level 1: Mock Test<br/>JVM ~5s"]
    OFAIL -->|Yes| MOCK2["Level 1: Mock Test<br/>x86 native ~5s"]

    MOCK1 --> INTEG1["Level 2: Integration<br/>Android emulator"]
    MOCK2 --> INTEG2["Level 2: Headless ArkUI<br/>CMake ~30s"]

    INTEG1 --> QEMU["Level 3: QEMU<br/>Full build + boot ~5min"]
    INTEG2 --> QEMU

    QEMU --> PASS{All pass?}
    PASS -->|Yes| COMMIT["Git Commit<br/>Update shim_progress.db"]
    PASS -->|No| FAILLOG["Log failure<br/>Feed back to AI<br/>or flag for human review"]
    FAILLOG --> GEN

    style START fill:#1e3a5f,stroke:#3b82f6,color:#e5e7eb
    style GEN fill:#6d28d9,stroke:#7c3aed,color:#e5e7eb
    style COMMIT fill:#065f46,stroke:#10b981,color:#e5e7eb
    style FAILLOG fill:#7f1d1d,stroke:#ef4444,color:#e5e7eb
    style QEMU fill:#92400e,stroke:#f59e0b,color:#e5e7eb
```

### UI Shim Architecture (Phase 3)

```mermaid
flowchart TD
    subgraph Android["Android Imperative Code"]
        CODE["LinearLayout layout = new LinearLayout(ctx)<br/>Button btn = new Button(ctx)<br/>btn.setText('Click me')<br/>btn.setOnClickListener(v → handleClick())<br/>layout.addView(btn)"]
    end

    subgraph Tree["View Description Tree"]
        VT["ViewTree<br/>LinearLayout orientation=VERTICAL<br/>└─ Button text='Click me' onClick=handleClick"]
    end

    subgraph ArkUI["ArkUI Declarative Renderer"]
        COMP["@Component AndroidViewHost<br/>@State viewTree: ViewNode<br/>build: Column → Button"]
    end

    CODE -->|"Shim captures"| VT
    VT -->|"ArkUI reads tree"| COMP

    style Android fill:#1e3a5f,stroke:#3b82f6,color:#e5e7eb
    style Tree fill:#6d28d9,stroke:#7c3aed,color:#e5e7eb
    style ArkUI fill:#065f46,stroke:#10b981,color:#e5e7eb
```

## Phase 0: Infrastructure Hardening (Week 1)

### 0.1 — End-to-End Test Pipeline Validation

**Goal:** Verify the full loop works: generate shim -> compile on Android side -> compile on OHOS side -> run tests on both -> compare results.

**Tasks:**

1. **Validate existing `run-local-tests.sh`** — ensure `02-headless-cli` and `03-ui-mockup` test apps still compile and pass with current shim classes
2. **Validate OHOS build** — confirm `qemu-arm-linux-min` builds cleanly, images boot in QEMU
3. **Validate ArkUI headless tests** — rebuild `/tmp/arkui-test-build/`, confirm 26 button tests pass
4. **Create a canary test** — one SharedPreferences round-trip test that exercises the full path: Android mock -> shim Java class -> JNI bridge stub -> OHOS native API mock. This becomes the "hello world" for the pipeline.

### 0.2 — Unified Test Harness

**Goal:** Single command that runs both Android-side and OHOS-side tests and produces a combined pass/fail report.

**Create `test-harness/run-all.sh`:**

```
+---------------------------------------------+
|  run-all.sh                                 |
|                                             |
|  1. Compile shim Java classes (javac)       |
|  2. Run Android-side headless tests (JVM)   |
|  3. Build OHOS native test binary (CMake)   |
|  4. Run OHOS-side tests (native x86)        |
|  5. [Optional] Build qemu-arm-linux-min     |
|  6. [Optional] Boot QEMU, run on-device     |
|  7. Merge results -> JSON report            |
|  8. Update shim_progress.db                 |
+---------------------------------------------+
```

**Levels of testing (selectable via `--level` flag):**

- **Level 1 — Mock-only** (~5 sec): Both sides use mocks, no real platform. Catches compilation errors, API shape mismatches, basic logic errors.
- **Level 2 — Native headless** (~30 sec): OHOS side compiles against real ace_engine sources via standalone CMake. Catches ArkUI integration issues.
- **Level 3 — QEMU integration** (~5 min): Full build + QEMU boot + on-device test execution via HDC shell. Catches runtime/kernel/init issues.

### 0.3 — Shim Progress Database Enhancement

**Extend `shim_progress.db` schema to track full generation lifecycle:**

```sql
CREATE TABLE shim_classes (
    android_class TEXT PRIMARY KEY,
    shim_java_path TEXT,           -- path to generated Java file
    bridge_rust_path TEXT,         -- path to bridge module
    ohos_test_path TEXT,           -- path to OHOS-side test
    android_test_path TEXT,        -- path to Android-side test
    generation_model TEXT,         -- which AI model generated it
    generation_prompt_hash TEXT,   -- hash of the prompt used
    api_count INTEGER,             -- number of APIs shimmed in this class
    test_count INTEGER,            -- number of test cases
    android_test_pass BOOLEAN,     -- last Android-side result
    ohos_test_pass BOOLEAN,        -- last OHOS-side result
    ohos_qemu_pass BOOLEAN,        -- last QEMU integration result
    iteration INTEGER DEFAULT 1,   -- how many AI fix iterations
    status TEXT,                   -- draft|compiles|tests_pass|verified
    score_avg REAL,                -- avg compatibility score of APIs
    effort_level TEXT,             -- from api_compat.db
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE shim_apis (
    android_api_id INTEGER,        -- FK to api_compat.db
    shim_class TEXT,               -- FK to shim_classes
    method_name TEXT,
    shimmed BOOLEAN,
    test_covered BOOLEAN,
    test_pass BOOLEAN,
    notes TEXT
);

CREATE TABLE generation_log (
    id INTEGER PRIMARY KEY,
    shim_class TEXT,
    iteration INTEGER,
    prompt TEXT,                    -- full prompt sent to AI
    response TEXT,                  -- AI response (code)
    compile_stdout TEXT,
    compile_stderr TEXT,
    test_stdout TEXT,
    test_stderr TEXT,
    success BOOLEAN,
    timestamp TIMESTAMP
);
```

---

## Phase 1: Tier 1 — Direct Mappings (Weeks 2-4)

### Target: 7,457 APIs with score 8-10 (13% of total)

These have near-identical semantics. AI generation should succeed on first or second attempt for most.

### 1.1 — API Prioritization Queue

**Query `api_compat.db` to build the generation queue:**

```sql
SELECT at.name as class_name,
       COUNT(*) as api_count,
       AVG(m.score) as avg_score,
       m.effort_level,
       GROUP_CONCAT(DISTINCT m.mapping_type) as types
FROM api_mappings m
JOIN android_apis aa ON m.android_api_id = aa.id
JOIN android_types at ON aa.type_id = at.id
WHERE m.score >= 8
  AND m.mapping_type IN ('direct', 'near')
  AND at.name NOT IN (/* already shimmed 34 classes */)
GROUP BY at.name
ORDER BY api_count DESC, avg_score DESC;
```

**Expected high-priority classes (not yet shimmed):**

| Priority | Android Class | API Count | Avg Score | OH Equivalent |
|----------|--------------|-----------|-----------|---------------|
| 1 | `android.util.Log` | 12 | 9.5 | `hilog` |
| 2 | `android.os.Handler` | 18 | 8.2 | `taskpool`/`EventRunner` |
| 3 | `android.os.Looper` | 8 | 8.0 | `EventRunner` |
| 4 | `android.graphics.Color` | 15 | 9.0 | ArkUI Color |
| 5 | `android.text.TextUtils` | 10 | 8.5 | TS string utils |
| 6 | `android.os.SystemClock` | 5 | 9.0 | `systemDateTime` |
| 7 | `android.util.SparseArray` | 12 | 8.0 | TS Map |
| 8 | `android.util.ArrayMap` | 15 | 8.0 | TS Map |
| ... | ... | ... | ... | ... |

### 1.2 — AI Generation Prompt Template (Tier 1)

Each class gets a structured prompt built from `api_compat.db` data:

```
You are generating an Android API shim class for OpenHarmony.

## Target Class: android.content.SharedPreferences

## API Mappings (from compatibility database):
| Android Method | OH Equivalent | Score | Mapping Type | Migration Guide |
|---|---|---|---|---|
| getString(String, String) | preferences.get(key) | 9 | direct | Direct call, cast return |
| putString(String, String) | preferences.put(key, value) | 9 | direct | Direct call |
| getInt(String, int) | preferences.get(key) | 8 | near | OH returns string, cast needed |
| ...

## Existing Shim Code (if updating):
{contents of shim/java/android/content/SharedPreferences.java}

## Existing Bridge Code:
{contents of shim/bridge/rust/src/preferences.rs}

## Constraints:
- Java shim must implement android.content.SharedPreferences interface exactly
- All public methods must be present (even if stubbed with TODO)
- Bridge calls go through OHBridge.nativeXxx() JNI methods
- Must be testable with MockOHBridge (no real OH runtime needed)

## Generate:
1. Java shim class (complete, compilable)
2. Unit test class (JUnit4, testing each method)
3. Bridge Rust module (JNI entry points)
4. OHOS-side test (gtest, testing OH API calls)
```

### 1.3 — Generation Loop (Enhanced `a2oh-loop.sh`)

**Enhance the existing 416-line script with error-feedback loops:**

```
+--------------------------------------------------+
|                AI GENERATION LOOP                |
|                                                  |
|  FOR each class in priority queue:               |
|                                                  |
|    1. QUERY api_compat.db for all APIs + guides  |
|    2. QUERY existing shim code (if updating)     |
|    3. BUILD prompt from template                 |
|    4. CALL Claude API -> get Java + test + bridge|
|    5. WRITE files to shim/ and test-apps/        |
|    6. COMPILE (javac for Java, cmake for OHOS)   |
|       +-- SUCCESS -> go to step 7               |
|       +-- FAILURE -> feed errors back to AI     |
|          +-- RETRY (max 3 iterations)           |
|    7. RUN Android-side tests                     |
|       +-- PASS -> go to step 8                  |
|       +-- FAIL -> feed failures back to AI      |
|          +-- RETRY (max 3 iterations)           |
|    8. RUN OHOS-side tests (headless)             |
|       +-- PASS -> go to step 9                  |
|       +-- FAIL -> feed failures back to AI      |
|          +-- RETRY (max 3 iterations)           |
|    9. UPDATE shim_progress.db                    |
|   10. GIT COMMIT (if all pass)                   |
|                                                  |
|  REPORT: classes attempted, passed, failed       |
+--------------------------------------------------+
```

**Key improvement over existing `a2oh-loop.sh`:** The error-feedback loop. When compilation or tests fail, the error output is appended to the prompt and the AI regenerates. This self-healing loop is where AI coding adds the most value — the AI sees its own mistakes and corrects them, typically converging within 2-3 iterations.

### 1.4 — Tier 1 Test App: `04-tier1-validation`

**Create a new test app specifically covering Tier 1 APIs:**

```java
// test-apps/04-tier1-validation/src/Tier1Test.java
public class Tier1Test {
    // Group 1: Data Storage
    void testSharedPreferences() { /* put/get/remove/clear */ }
    void testSQLiteDatabase() { /* CRUD operations */ }

    // Group 2: Logging & Utils
    void testLog() { /* Log.d/i/w/e with tags */ }
    void testTextUtils() { /* isEmpty, join, split */ }

    // Group 3: System Services
    void testSystemClock() { /* elapsedRealtime, uptimeMillis */ }
    void testToast() { /* makeText, show */ }

    // Group 4: Data Structures
    void testBundle() { /* put/get various types */ }
    void testUri() { /* parse, getScheme, getPath */ }
    void testIntent() { /* actions, extras, component */ }

    // Each test method:
    // 1. Calls Android API
    // 2. Captures return value
    // 3. Asserts expected behavior
    // 4. Same test runs on OHOS side via bridge
}
```

### 1.5 — Expected Output (Tier 1)

| Metric | Target |
|--------|--------|
| Classes shimmed | 50-80 |
| APIs covered | ~5,000 of 7,457 |
| Test coverage | >90% of shimmed APIs |
| Android-side pass rate | >95% |
| OHOS mock pass rate | >90% |
| AI iterations avg | 1.5 per class |

---

## Phase 2: Tier 2 — Composite Mappings (Weeks 5-10)

### Target: 14,777 APIs with score 5-7 (26% of total)

These require adaptation logic — parameter reordering, async-to-sync conversion, multi-API composition. AI needs richer context.

### 2.1 — Subsystem-Ordered Generation

**Process Tier 2 by subsystem to build up dependent shim code incrementally:**

| Order | Subsystem | Key Classes | Complexity |
|-------|-----------|-------------|------------|
| 1 | OS/System | Handler, Looper, Message, Parcel | Medium — event loop model differs |
| 2 | Content | ContentResolver, ContentValues | Medium — DataShareHelper mapping |
| 3 | App Framework | Activity (enhanced), Service, BroadcastReceiver | High — lifecycle model gap |
| 4 | Networking | HttpURLConnection, ConnectivityManager | Medium — @ohos.net.http |
| 5 | Media | MediaPlayer, AudioManager | Medium — @ohos.multimedia |
| 6 | Sensors | SensorManager, SensorEvent | Medium — @ohos.sensor |
| 7 | Notifications | NotificationManager (enhanced), Channels | Medium — already partially shimmed |
| 8 | Permissions | PermissionManager, checkSelfPermission | Medium — @ohos.abilityAccessCtrl |
| 9 | Bluetooth | BluetoothAdapter, BluetoothDevice | Hard — different discovery model |
| 10 | Telephony | TelephonyManager, SmsManager | Hard — @ohos.telephony |

### 2.2 — Enhanced Prompt Template (Tier 2)

Tier 2 prompts include additional context beyond Tier 1:

```
## Paradigm Differences:
- Android uses synchronous callbacks; OH uses async Promises
- Android ContentProvider uses URI-based CRUD; OH uses DataShareHelper
- Android Handler/Looper is thread-based; OH uses TaskPool/EventRunner

## Already Shimmed Dependencies:
{list of Tier 1 shims this class can call}

## Code Examples from api_compat.db:
### Android:
{code_example_android from mapping}

### OpenHarmony:
{code_example_oh from mapping}

## Known Gaps (from gap_description):
{gap_description field for each API}

## Strategy:
- For async gaps: wrap OH Promise in blocking Future for Android-compatible sync API
- For missing params: provide sensible defaults documented in migration_guide
- For multi-call composition: generate helper methods in OHBridge
```

### 2.3 — Composite Test Apps

**Extend test apps to cover cross-class interactions:**

```
test-apps/05-tier2-lifecycle/
  +-- TestActivity.java          # Activity lifecycle test
  +-- TestService.java           # Service lifecycle test
  +-- TestBroadcastReceiver.java # Broadcast test

test-apps/06-tier2-networking/
  +-- HttpTest.java              # HTTP GET/POST
  +-- ConnectivityTest.java      # Network state
  +-- SocketTest.java            # TCP/UDP

test-apps/07-tier2-media/
  +-- MediaPlayerTest.java       # Audio playback
  +-- AudioRecordTest.java       # Recording
  +-- AudioManagerTest.java      # Volume/routing
```

### 2.4 — QEMU Integration Tests (Level 3)

For Tier 2 APIs, mock testing is insufficient — some behaviors only manifest on a real OHOS runtime.

**QEMU test runner flow:**

```
1. Build qemu-arm-linux-min with test binary included
2. Boot QEMU with virtio-mmio drives (correct order: userdata, vendor, system, updater)
3. Wait for init to reach multi-user target
4. Push test binary via HDC or embed in system.img
5. Execute tests, capture stdout/stderr via serial console
6. Parse gtest XML output
7. Shutdown QEMU
8. Merge results into shim_progress.db
```

**Automation script: `test-harness/qemu-test-runner.sh`:**

```bash
#!/bin/bash
# Build, boot, test, shutdown — fully automated
QEMU_BIN="tools/qemu-extracted/usr/bin/qemu-system-arm"
KERNEL="out/qemu-arm-linux/packages/phone/images/zImage"
SYSTEM_IMG="out/qemu-arm-linux/packages/phone/images/system.img"

# Configure drives in correct order (reverse enumeration)
DRIVES="-drive file=userdata.img,format=raw,if=none,id=d3 -device virtio-blk-device,drive=d3"
DRIVES+=" -drive file=vendor.img,format=raw,if=none,id=d2 -device virtio-blk-device,drive=d2"
DRIVES+=" -drive file=system.img,format=raw,if=none,id=d1 -device virtio-blk-device,drive=d1"
DRIVES+=" -drive file=updater.img,format=raw,if=none,id=d0 -device virtio-blk-device,drive=d0"

# Boot with serial console capture
LD_LIBRARY_PATH=tools/qemu-extracted/usr/lib \
  $QEMU_BIN -M virt -cpu cortex-a7 -m 512M \
  -kernel $KERNEL -initrd ramdisk.img \
  $DRIVES \
  -serial file:qemu_output.log \
  -nographic -no-reboot &

QEMU_PID=$!

# Wait for boot, run tests, parse output
wait_for_boot qemu_output.log 120
run_tests_via_serial
parse_gtest_xml qemu_output.log

kill $QEMU_PID
```

**QEMU environment constraints (from prior work):**
- system.img must NOT have ext4 64bit feature (32-bit ARM kernel limitation)
- Recreate images with: `mke2fs -t ext4 -b 4096 -I 256 -O ^64bit,^metadata_csum`
- Boot reaches all init stages but minimal build lacks some config files
- Must use prebuilt Python 3.10, NOT system Python 3.13

---

## Phase 3: UI Layer — ArkUI Shim (Weeks 11-18)

### Target: View/Widget APIs (score 1-4, ~4,700 APIs)

This is the hardest tier. Android's imperative `View` system cannot be directly mapped to ArkUI's declarative model. Strategy: **compile-time translation, not runtime shimming**.

### 3.1 — Strategy: Declarative View Builder

Instead of trying to make `new Button()` / `button.setText("Hello")` work at runtime (impossible with declarative ArkUI), generate a **View Description Tree** that can be rendered by an ArkUI adapter.

```
Android imperative code:
  LinearLayout layout = new LinearLayout(ctx);
  Button btn = new Button(ctx);
  btn.setText("Click me");
  btn.setOnClickListener(v -> handleClick());
  layout.addView(btn);

      | Shim captures as View Tree |
      v                            v

ViewTree {
  LinearLayout {
    orientation: VERTICAL
    children: [
      Button {
        text: "Click me",
        onClick: handleClick
      }
    ]
  }
}

      | ArkUI renderer reads tree |
      v                           v

@Component
struct AndroidViewHost {
  @State viewTree: ViewNode
  build() {
    Column() {          // LinearLayout -> Column
      Button(this.viewTree.children[0].text)
        .onClick(() => this.viewTree.children[0].onClick())
    }
  }
}
```

### 3.2 — Headless ArkUI Testing for View Shims

**Leverage the existing `arkui_test_standalone/` infrastructure:**

For each shimmed Android widget, create a corresponding ArkUI test that validates the declarative output matches expected rendering behavior.

**Extend CMakeLists.txt to add new test targets:**

```cmake
# New target: view_shim_test_ng
add_executable(view_shim_test_ng
    ${VIEW_SHIM_TEST_SRCS}    # Our custom tests
    ${ACE_BASE_SRCS}
    ${ACE_COMPONENTS_BASE_SRCS}
    # ... same framework sources as button_test_ng
    ${ACE_MOCK_SRCS}
    ${LINKER_STUBS}
)
```

**Test cases to add (using `/arkui-test-add` skill pattern):**

| Android Widget | ArkUI Component | Test File | Tests |
|---------------|-----------------|-----------|-------|
| Button | Button | `view_shim_button_test.cpp` | 15+ |
| TextView | Text | `view_shim_text_test.cpp` | 20+ |
| EditText | TextInput | `view_shim_textinput_test.cpp` | 15+ |
| ImageView | Image | `view_shim_image_test.cpp` | 10+ |
| LinearLayout | Column/Row | `view_shim_flex_test.cpp` | 12+ |
| FrameLayout | Stack | `view_shim_stack_test.cpp` | 8+ |
| ScrollView | Scroll | `view_shim_scroll_test.cpp` | 10+ |
| RecyclerView | List/LazyForEach | `view_shim_list_test.cpp` | 15+ |
| CheckBox | Checkbox | `view_shim_checkbox_test.cpp` | 8+ |
| Switch | Toggle | `view_shim_toggle_test.cpp` | 8+ |
| ProgressBar | Progress | `view_shim_progress_test.cpp` | 6+ |
| Spinner | Select | `view_shim_select_test.cpp` | 8+ |

**Each test validates:**

1. Component creation with correct properties
2. Property propagation (text, color, size, padding, margin)
3. Event handler attachment (onClick, onTouch, onFocus)
4. Layout constraint behavior (width, height, weight, gravity -> flexGrow, alignSelf)
5. View hierarchy (addView/removeView -> component tree operations)

**Key headless testing constraints (from existing setup):**
- `ENABLE_ROSEN_BACKEND` must NOT be defined (pulls in v1 Skia code paths)
- Skia headers are stubbed (~60 files), not real implementations
- `linker_stubs.cpp` provides empty implementations for unused patterns
- Generated theme code must be regenerated after `/tmp/` is cleaned
- Build output in `/tmp/arkui-test-build/` is volatile across reboots

### 3.3 — View Property Mapping Table

**AI generates these mappings; tests validate them:**

| Android Property | ArkUI Property | Conversion |
|-----------------|----------------|------------|
| `view.setWidth(dp)` | `.width(vp)` | dp to vp (1:1 on standard density) |
| `view.setPadding(l,t,r,b)` | `.padding({left,top,right,bottom})` | direct |
| `view.setBackgroundColor(c)` | `.backgroundColor(c)` | Color int to `#AARRGGBB` |
| `view.setVisibility(GONE)` | `.visibility(Visibility.None)` | enum mapping |
| `view.setVisibility(INVISIBLE)` | `.visibility(Visibility.Hidden)` | enum mapping |
| `view.setVisibility(VISIBLE)` | `.visibility(Visibility.Visible)` | enum mapping |
| `view.setAlpha(f)` | `.opacity(f)` | direct |
| `view.setElevation(f)` | `.shadow({radius:f})` | approximate |
| `view.setEnabled(b)` | `.enabled(b)` | direct |
| `layout.setOrientation(VERTICAL)` | `Column()` | structural — different container |
| `layout.setOrientation(HORIZONTAL)` | `Row()` | structural — different container |
| `layout.setGravity(CENTER)` | `.justifyContent(FlexAlign.Center)` | enum mapping |
| `layout.setGravity(END)` | `.justifyContent(FlexAlign.End)` | enum mapping |
| `view.setOnClickListener` | `.onClick(callback)` | wrap callback |
| `view.setOnLongClickListener` | `.gesture(LongPressGesture)` | different gesture model |
| `text.setTextSize(sp)` | `.fontSize(fp)` | sp to fp |
| `text.setTextColor(c)` | `.fontColor(c)` | Color int to ArkUI color |
| `text.setTypeface(BOLD)` | `.fontWeight(FontWeight.Bold)` | enum mapping |
| `text.setGravity(CENTER)` | `.textAlign(TextAlign.Center)` | enum mapping |
| `text.setMaxLines(n)` | `.maxLines(n)` | direct |
| `text.setEllipsize(END)` | `.textOverflow({overflow:TextOverflow.Ellipsis})` | structural |
| `editText.setHint(s)` | `.placeholder(s)` | direct |
| `editText.setInputType(TYPE)` | `.type(InputType.XXX)` | enum mapping |
| `image.setScaleType(FIT_CENTER)` | `.objectFit(ImageFit.Contain)` | enum mapping |
| `image.setScaleType(CENTER_CROP)` | `.objectFit(ImageFit.Cover)` | enum mapping |
| `recycler.setAdapter(a)` | `List() { LazyForEach(dataSource) }` | paradigm shift |
| `checkbox.setChecked(b)` | `Checkbox({select: b})` | constructor param |
| `switch.setChecked(b)` | `Toggle({isOn: b})` | constructor param |
| `progress.setProgress(n)` | `Progress({value: n})` | constructor param |

### 3.4 — AI Generation for View Shims

**Special prompt strategy for UI components:**

The AI receives:

1. The Android View class's full API surface (from `api_compat.db`)
2. The corresponding ArkUI component's test file (from `foundation/arkui/ace_engine/test/unittest/core/pattern/`)
3. The ArkUI component's pattern source (from `frameworks/core/components_ng/pattern/`)
4. The property-mapping table above

**It generates:**

1. Java shim class that builds a ViewTree description
2. ArkUI adapter component (`@Component`) that renders from ViewTree
3. Headless ArkUI test (C++ gtest) validating component properties
4. Android-side test validating ViewTree structure

### 3.5 — Layout Algorithm Validation

**Critical:** Layout is where Android-ArkUI divergence causes real bugs.

**Create layout comparison tests:**

```
Given: LinearLayout with 3 children, weights [1,2,1], parent 400px wide

Android expected: [100px, 200px, 100px]
ArkUI (Row + flexGrow): should produce same [100px, 200px, 100px]

Test: Create both, measure, compare pixel-by-pixel.
```

**Android side:** Run in Android emulator, capture layout dump (`adb shell dumpsys activity`)
**OHOS side:** Run headless ArkUI test, read `LayoutWrapper::GetGeometryNode()->GetFrameSize()`

**Automated comparison script:**

```python
# test-harness/layout_compare.py
# 1. Parse Android layout dump -> {view_id: {x, y, w, h}}
# 2. Parse ArkUI test output -> {node_id: {x, y, w, h}}
# 3. Compare with tolerance (+/-1px)
# 4. Report mismatches
```

**Layout test matrix:**

| Layout Scenario | Android | ArkUI Equivalent | Validation |
|----------------|---------|------------------|------------|
| Vertical stack, equal weight | LinearLayout(V) + weight=1 | Column() + flexGrow(1) | Sizes match |
| Horizontal with wrap | FlexboxLayout(WRAP) | Flex({wrap: FlexWrap.Wrap}) | Wrapping behavior |
| Centered child | FrameLayout + gravity=CENTER | Stack() + align(Alignment.Center) | Position match |
| Margin collapse | Two views with margins | Two components with margin | Gap size match |
| Match parent + padding | match_parent + padding | .width('100%') + .padding() | Content area match |
| ScrollView with tall content | ScrollView > LinearLayout | Scroll() > Column() | Scroll behavior |
| RecyclerView with 1000 items | RecyclerView + Adapter | List() + LazyForEach | Visible items match |

---

## Phase 4: Bridge Layer Hardening (Weeks 12-16, parallel with Phase 3)

### 4.1 — Rust Bridge Module Generation

**For each shimmed class, AI generates a Rust JNI bridge module:**

```
shim/bridge/rust/src/
+-- lib.rs              # JNI registration
+-- preferences.rs      # SharedPreferences <-> Preferences
+-- rdb_store.rs        # SQLiteDatabase <-> RdbStore
+-- handler.rs          # NEW: Handler <-> EventRunner
+-- connectivity.rs     # NEW: ConnectivityManager <-> @ohos.net.connection
+-- media_player.rs     # NEW: MediaPlayer <-> @ohos.multimedia.media
+-- sensor.rs           # NEW: SensorManager <-> @ohos.sensor
+-- bluetooth.rs        # NEW: BluetoothAdapter <-> @ohos.bluetooth
+-- view_bridge.rs      # NEW: ViewTree serialization <-> ArkUI
+-- ...
```

**Each module must handle:**

- JNI type marshalling (jobject <-> Rust struct <-> OH C type)
- Async-to-sync conversion (OH Promises -> blocking JNI return)
- Error translation (OH error codes -> Java exceptions)
- Lifecycle management (JNI global/local ref tracking)
- Thread safety (JNI env is thread-local, OH APIs may require main thread)

### 4.2 — Bridge Compilation & Testing

**Extend standalone CMake to compile bridge modules:**

```cmake
# For x86 host testing (mock OH APIs):
add_library(android_shim_bridge SHARED
    ${BRIDGE_RUST_SRCS}     # Compiled via corrosion or manual rustc
    ${BRIDGE_CPP_SRCS}      # C++ JNI glue
)

# For ARM cross-compile (real OH target):
# Use prebuilts/clang/ohos/linux-x86_64/llvm with --target=arm-linux-ohos
```

### 4.3 — Type Marshalling Test Matrix

**Automated tests for every JNI type conversion:**

| Java Type | JNI Type | Rust Type | OH C Type | Test Case |
|-----------|----------|-----------|-----------|-----------|
| `String` | `jstring` | `String` | `char*` | roundtrip "hello", "你好", "🌍", empty, null |
| `int` | `jint` | `i32` | `int32_t` | INT_MIN, -1, 0, 1, INT_MAX |
| `long` | `jlong` | `i64` | `int64_t` | LONG_MIN, 0, LONG_MAX |
| `float` | `jfloat` | `f32` | `float` | 0.0, -0.0, NaN, Inf, subnormal |
| `double` | `jdouble` | `f64` | `double` | same edge cases |
| `boolean` | `jboolean` | `bool` | `bool` | true, false |
| `byte[]` | `jbyteArray` | `Vec<u8>` | `uint8_t*` | empty, 1 byte, 1MB, binary data |
| `int[]` | `jintArray` | `Vec<i32>` | `int32_t*` | empty, single, large |
| `String[]` | `jobjectArray` | `Vec<String>` | `char**` | empty, mixed lengths |
| `Bundle` | `jobject` | `HashMap` | `OH_Want*` | nested bundles, all value types |
| `null` | `NULL` | `Option::None` | `nullptr` | null safety for every type |
| `Uri` | `jobject` | `String` (URL) | `char*` | scheme, path, query, fragment |

---

## Phase 5: Continuous Validation & Regression (Weeks 8+, ongoing)

### 5.1 — Regression Test Suite

**As shim classes accumulate, build a comprehensive regression suite:**

```
test-harness/
+-- run-all.sh                    # Master runner
+-- android-side/
|   +-- tier1-tests/              # 50+ JUnit test classes
|   +-- tier2-tests/              # 30+ JUnit test classes
|   +-- ui-tests/                 # View shim tests
+-- ohos-side/
|   +-- headless/                 # Standalone CMake tests
|   |   +-- CMakeLists.txt        # Extends arkui_test_standalone
|   |   +-- data_shim_test.cpp    # Preferences, RDB
|   |   +-- lifecycle_test.cpp    # Activity -> UIAbility
|   |   +-- network_test.cpp      # HTTP, connectivity
|   |   +-- view_shim_test.cpp    # ArkUI component validation
|   +-- qemu/                     # QEMU integration tests
|       +-- qemu-test-runner.sh
|       +-- on-device-tests/
+-- comparison/
|   +-- layout_compare.py         # Layout pixel comparison
|   +-- behavior_compare.py       # API behavior comparison
+-- reports/
    +-- coverage.html             # Which APIs are tested
    +-- pass-rate.html            # Pass rates over time
    +-- regression.html           # Newly broken tests
```

### 5.2 — CI Loop (Nightly)

```
+-----------------------------------------------------+
|  NIGHTLY CI                                         |
|                                                     |
|  1. Pull latest api_compat.db updates               |
|  2. Run AI generation for next N unshimmed classes   |
|  3. Compile all shim code (Android + OHOS)          |
|  4. Run Level 1 tests (mock, ~30 sec)               |
|  5. Run Level 2 tests (headless ArkUI, ~2 min)      |
|  6. Run Level 3 tests (QEMU, ~10 min)               |
|  7. Update shim_progress.db                         |
|  8. Generate coverage report                        |
|  9. Commit passing shims                            |
| 10. File issues for failures needing human review   |
+-----------------------------------------------------+
```

### 5.3 — Coverage Tracking Dashboard

**Extend existing FastAPI backend with new endpoints:**

```
GET /api/shim/progress          # Overall shim completion
GET /api/shim/coverage          # API coverage % by subsystem
GET /api/shim/test-results      # Test pass/fail history
GET /api/shim/blockers          # APIs that fail >3 AI iterations
GET /api/shim/quality           # Score distribution of shimmed vs unshimmed
```

**Dashboard metrics:**

- Total APIs shimmed / total Android APIs (target: 54% = Tier 1 + Tier 2)
- Test pass rate (target: >95% for Tier 1, >85% for Tier 2)
- AI generation success rate (first attempt vs. needed retries)
- APIs requiring human intervention (score <5, failed >3 iterations)
- Cost tracking: tokens consumed per class, cost per API shimmed

---

## Phase 6: Real App Validation (Weeks 16-20)

### 6.1 — FlashNote App (Already Converted)

**Use as regression benchmark:**

- All FlashNote APIs should be covered by Tier 1/2 shims
- Run FlashNote through the shim layer instead of manual conversion
- Compare behavior with the manually-converted OHOS version in `test-apps/01-flashnote/openharmony/`
- Success criterion: identical functional behavior (data persistence, reminders, notifications)

### 6.2 — New Validation Apps (Increasing Complexity)

| App | Android APIs Used | Tier Mix | Purpose |
|-----|------------------|----------|---------|
| **TodoApp** | SQLite, RecyclerView, SharedPrefs, Notifications | 70% T1, 30% T2 | Basic CRUD + UI |
| **WeatherApp** | HTTP, JSON, ListView, AsyncTask, Location | 40% T1, 50% T2, 10% T3 | Networking + location |
| **ChatApp** | WebSocket, RecyclerView, Camera, FileProvider | 20% T1, 50% T2, 30% T3 | Real-time + media |
| **MapApp** | MapView, Location, Sensors, Custom drawing | 10% T1, 30% T2, 60% T3 | Heavy T3 stress test |

**Each app has:**

1. Android source (runs on Android emulator)
2. Expected behavior specification (test oracle)
3. Automated comparison between Android and OHOS-via-shim output

### 6.3 — Behavioral Equivalence Testing

**For non-UI APIs:**

```
1. Run Android app action -> capture API call trace + return values
2. Run same action through shim on OHOS -> capture same trace
3. Diff traces: same calls? same returns? same side effects?
4. Report behavioral divergences
```

**For UI APIs:**

```
1. Run Android layout -> capture view hierarchy + measurements
2. Run ArkUI equivalent -> capture component tree + measurements
3. Compare: same structure? same sizes (+/-tolerance)? same event handling?
```

**Equivalence categories:**

| Category | Definition | Acceptable Divergence |
|----------|-----------|----------------------|
| **Exact** | Identical return values, same side effects | None |
| **Semantic** | Same observable behavior, different internals | Internal state may differ |
| **Approximate** | Close but not identical (e.g., layout rounding) | +/-1px, +/-1ms timing |
| **Behavioral** | Same user-visible outcome, different execution path | Async ordering may vary |
| **Documented Gap** | Known limitation, behavior documented | Must be in known_limitations.json |

---

## Phase 7: Optimization & Production Readiness (Weeks 20-24)

### 7.1 — Performance Profiling

- Measure shim overhead per API call (target: <1ms for direct mappings)
- Identify hot paths (frequently called APIs) and optimize
- JNI call reduction (batch operations where possible)
- Memory profiling: ensure no JNI reference leaks

**Performance test framework:**

```java
// For each shimmed API, measure overhead:
long start = System.nanoTime();
for (int i = 0; i < 10000; i++) {
    shimmedApi.call(args);
}
long elapsed = (System.nanoTime() - start) / 10000;
// Report: API name, avg latency, p99 latency
```

### 7.2 — Error Handling Hardening

- Map all OH error codes to appropriate Java exceptions
- Ensure no silent failures (every OH API failure surfaces to Java caller)
- Add defensive null checks at JNI boundary
- Test error paths explicitly (permission denied, resource not found, network timeout)

**Error mapping table:**

| OH Error | Java Exception | Context |
|----------|---------------|---------|
| `ERR_PERMISSION_DENIED` | `SecurityException` | Missing OHOS permission |
| `ERR_INVALID_PARAM` | `IllegalArgumentException` | Bad parameter |
| `ERR_NOT_FOUND` | `FileNotFoundException` / `NoSuchElementException` | Resource missing |
| `ERR_NO_MEMORY` | `OutOfMemoryError` | Memory exhaustion |
| `ERR_TIMEOUT` | `TimeoutException` | Operation timed out |
| `ERR_IO` | `IOException` | I/O failure |

### 7.3 — Documentation Generation

**AI generates migration docs from `api_compat.db` + shim code:**

- Per-class API reference showing Android-to-OHOS mapping
- Known limitations and behavioral differences
- Performance characteristics
- Code examples (before/after)

### 7.4 — Packaging

```
android-ohos-shim/
+-- shim.jar                    # All Java shim classes
+-- libshim_bridge.so           # Rust/C++ bridge (ARM)
+-- libshim_bridge_x86.so       # For testing on x86
+-- ohos_adapters/              # ArkUI adapter components
|   +-- AndroidViewHost.ets     # View tree renderer
|   +-- AndroidActivity.ets     # Activity lifecycle adapter
|   +-- ...
+-- config/
    +-- api_coverage.json       # Which APIs are shimmed
    +-- known_limitations.json  # Behavioral differences
    +-- error_mappings.json     # OH error -> Java exception map
```

---

## Success Metrics

| Milestone | Target | Measurement |
|-----------|--------|-------------|
| Phase 0 complete | Pipeline works end-to-end | Canary test passes on all 3 levels |
| Phase 1 complete | 5,000+ Tier 1 APIs shimmed | `shim_progress.db` count |
| Phase 2 complete | 10,000+ Tier 2 APIs shimmed | `shim_progress.db` count |
| Phase 3 complete | 20+ Android widgets rendered via ArkUI | Headless ArkUI tests pass |
| Phase 4 complete | Bridge compiles for ARM target | Cross-compile succeeds |
| Phase 5 complete | >90% test pass rate across all tiers | Nightly CI report |
| Phase 6 complete | FlashNote runs through shim layer | Behavioral equivalence verified |
| Phase 7 complete | <1ms overhead per shim call | Performance benchmark |

**Overall target:** ~25,000 APIs shimmed (44% of total), covering the Tier 1 + Tier 2 APIs that represent >80% of typical app usage. The remaining 46% (Tier 3 with no OH equivalent) require app-level rewrites or Dalvik VM fallback (documented in `skills/DALVIK-PORT.md`).

---

## Risk Mitigation

| Risk | Impact | Mitigation |
|------|--------|------------|
| AI generates incorrect mappings | Wrong behavior at runtime | 3-level testing (mock, headless, QEMU) catches errors |
| ArkUI property model diverges from Android View | UI rendering mismatch | Layout comparison tests with pixel tolerance |
| JNI bridge memory leaks | App crashes/OOM | JNI ref tracking + valgrind on x86 tests |
| OH API breaking changes between versions | Shim layer breaks | Version-pin OH SDK, track API stability |
| Tier 3 APIs block real app adoption | Limited app compatibility | Dalvik VM fallback for unsupported APIs |
| QEMU boot instability | Flaky integration tests | Timeout + retry + fallback to Level 2 tests |
| `/tmp/` volatility loses build artifacts | Rebuild overhead | Cache artifacts in persistent directory |

---

## Appendix A: Already Shimmed Classes (34)

From existing `a2oh-loop.sh` exclusion list:

- `android.app.Activity`, `Application`, `NotificationManager`, `NotificationChannel`, `Notification`, `AlarmManager`, `PendingIntent`
- `android.content.Context`, `Intent`, `SharedPreferences`, `BroadcastReceiver`, `ContentValues`
- `android.os.Build`, `Bundle`
- `android.database.Cursor`, `CursorWrapper`, `SQLException`
- `android.database.sqlite.SQLiteDatabase`, `SQLiteOpenHelper`
- `android.view.View`, `ViewGroup`, `LayoutInflater`, `Gravity`
- `android.widget.Toast`, `TextView`, `Button`, `EditText`, `ImageView`
- `android.net.Uri`
- `android.util.Log`

## Appendix B: Subsystem Compatibility Scores

| Subsystem | API Count | Avg Score | Coverage | Priority |
|-----------|-----------|-----------|----------|----------|
| Java Standard | 13,613 | 4.96 | ~50% | Medium |
| Graphics | 5,299 | 5.36 | ~54% | Medium |
| ICU | 4,191 | 3.57 | ~36% | Low |
| Media | 4,118 | 3.19 | ~32% | Medium |
| Telephony | 2,855 | 2.64 | ~26% | Low |
| App Framework | 2,823 | 3.53 | ~35% | High |
| View | 2,748 | 2.11 | ~21% | Critical (T3) |
| Core | 2,613 | 1.24 | ~12% | Low |
| Widget | 1,983 | 2.36 | ~24% | Critical (T3) |
| Provider | 1,884 | 1.92 | ~19% | Medium |
| OS | 1,737 | 4.20 | ~42% | High |
| Content | 1,266 | 3.47 | ~35% | High |
| Text | 1,254 | 2.81 | ~28% | Medium |
| Bluetooth | 884 | 3.19 | ~32% | Low |
| Package Manager | 836 | 2.40 | ~24% | Low |

## Appendix C: Available ArkUI Test Patterns (86+)

Components with existing test files in `foundation/arkui/ace_engine/test/unittest/core/pattern/`:

animator, app_bar, badge, blank, bubble, button, calendar, calendar_picker, canvas_renderer, checkbox, checkboxgroup, common_view, container_modal, counter, custom, custom_paint, data_panel, dialog, divider, flex, folder_stack, form, gauge, grid, grid_col, grid_container, grid_row, hyperlink, image, image_animator, indexer, linear_layout, linear_split, list, loading_progress, marquee, menu, navigation, navigator, and 47+ more.

These can be leveraged for Phase 3 UI validation by following the `arkui_test_standalone/` pattern: standalone CMake build, mock infrastructure, Skia stubs, linker stubs.
