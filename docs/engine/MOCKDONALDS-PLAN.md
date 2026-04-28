# MockDonalds: End-to-End Android-on-OHOS Integration Test

## 2026-04-28 Supervisor Update

MockDonalds is no longer the only McDonald's-shaped integration proof. The
current delivery target is PF-466, `com.westlake.mcdprofile`, documented in
`docs/engine/OHOS-MCD-PROFILE-INTEGRATION.md`.

PF-466 has passed on the connected Android phone through Westlake `dalvikvm`
with compiled XML resource loading and McD-profile Material tags present,
SharedPreferences cart state, live host/OHBridge JSON/image/REST, full-phone
`DLST`, and strict touch navigation. The latest accepted run launches through
`WestlakeActivityThread` and `AppComponentFactory`, wires layout bytes before
`onCreate`, inflates a 25-view guest tree with 10 Material-shaped views, binds
the XML `ListView`, and exercises adapter row binding through position `4`.
Treat PF-466 as the immediate OHOS port target and the older MockDonalds plan
below as historical background plus a headless/API regression ladder.

Remaining gaps before a stock McDonald's APK are generalizing the accepted
WAT/AppComponentFactory launch slice beyond this controlled app, object-array
runtime correctness, standalone `resources.arsc` table parsing, upstream
Material XML/theming, generic View draw/hit/scroll, streamed multi-image
networking, and OHOS adapter parity for the PF-466 contracts.

## Goal

Validate the entire Android-on-OpenHarmony stack by running a mock McDonald's-like app through:
**OHOS Kernel → Dalvik VM → Java Shim Layer → Android APIs → App Logic**

## Current State (What's Proven vs Unproven)

| Component | Proven | Unproven |
|-----------|--------|----------|
| Headless OHOS on QEMU ARM32 | Boots, init runs, services start, hdcd works | - |
| Dalvik VM | Runs `Hello.java` on x86_64 Linux | Running on OHOS QEMU ARM32 with shim layer |
| Java shim layer (1,978 classes) | Activity lifecycle + View tree + Canvas on host JVM | Running on Dalvik (any platform) |
| ArkUI headless | Button layout works on x86 host via JNI | Cross-compiled for ARM32 |
| End-to-end | Nothing | Everything together |

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│  MockDonalds APK (.dex)                                 │
│  MenuActivity → ItemDetail → Cart → Checkout            │
├─────────────────────────────────────────────────────────┤
│  Android Shim Layer (1,978 Java classes)                │
│  Activity, View, ListView, SharedPreferences, SQLite    │
├─────────────────────────────────────────────────────────┤
│  MiniServer (ActivityManager, PackageManager)           │
│  Lifecycle, Intent routing, View rendering              │
├─────────────────────────────────────────────────────────┤
│  Dalvik VM (KitKat portable C interpreter, ARM32)       │
│  + libcore_bridge.cpp (JNI for System/Posix/ICU)        │
├─────────────────────────────────────────────────────────┤
│  OpenHarmony Headless (QEMU ARM32)                      │
│  Kernel + init + musl libc + system services            │
└─────────────────────────────────────────────────────────┘
```

## Two Parallel Workstreams

### WS-A: OHOS Headless Platform
**Owner:** OpenHarmony agent (openharmony repo)
**Goal:** QEMU system that can host Dalvik + deploy script

### WS-B: Android Shim + Mock App
**Owner:** A2OH agents (harmony-android-guide repo, GitHub Issues)
**Goal:** MockDonalds .dex that passes all tests on host JVM

**Integration point:** `hdc file send mockdonalds.dex` + `dalvikvm -cp mockdonalds.dex MockDonaldsRunner`

---

## WS-A Issues (OHOS Platform)

| ID | Title | Priority | Depends | Blocks |
|----|-------|----------|---------|--------|
| A1 | Verify Dalvik VM boots on OHOS QEMU ARM32 | BLOCKER | A2 | A3, A4 |
| A2 | Build/verify dalvikvm + dexopt ARM32 binary | HIGH | - | A1 |
| A3 | Create QEMU deployment package + script | HIGH | A1 | A4 |
| A4 | End-to-end smoke: HelloWorldActivity on QEMU | HIGH | A3, B1 | - |
| A5 | Headless ArkUI rendering on ARM32 (Phase 2) | DEFERRED | A4 | - |

## WS-B Issues (Shim + App)

| ID | Title | Priority | Depends | Blocks |
|----|-------|----------|---------|--------|
| B1 | Verify shim layer compiles to DEX | BLOCKER | - | A4, B5 |
| B2 | Fix SQLiteOpenHelper + SQLiteDatabase e2e | HIGH | - | B5 |
| B3 | Fix ListView + BaseAdapter refresh | MEDIUM | - | B5 |
| B4 | Verify Intent extras round-trip | HIGH | - | B5 |
| B5 | Build MockDonalds app | HIGH | B1-B4 | A4 |
| B6 | Fix missing shim classes for MockDonalds | MEDIUM | B5 | - |

### Parallelism

```
Time →

WS-A:  [A2: build dalvik]──→[A1: verify on QEMU]──→[A3: deploy pkg]──→[A4: e2e test]
                                                                              ↑
WS-B:  [B1: DEX compile]──→┬──[B2: SQLite fix]────→[B5: build app]──────────┘
                           ├──[B3: ListView fix]──→┘
                           └──[B4: Intent verify]─┘
```

B2, B3, B4 are fully parallel (no shared code). B5 integrates them. A4 is the final integration test.

---

## The Mock App: MockDonalds

### File Layout
```
test-apps/04-mockdonalds/
  src/com/example/mockdonalds/
    MenuItem.java            -- POJO: id, name, description, price, category
    MenuDbHelper.java        -- SQLiteOpenHelper: creates + populates menu table
    CartManager.java         -- Cart ops via SharedPreferences + SQLite
    MenuAdapter.java         -- BaseAdapter for menu ListView
    CartAdapter.java         -- BaseAdapter for cart ListView
    MenuActivity.java        -- Launcher: header + menu list
    ItemDetailActivity.java  -- Item detail + "Add to Cart"
    CartActivity.java        -- Cart list + total + "Checkout"
    CheckoutActivity.java    -- Order confirm + finish
  src/MockDonaldsRunner.java -- Headless test runner
  build.sh                   -- Compile + DEX + test
```

### Menu Data (hardcoded)
```
Big Mock Burger     $5.99  Burgers
Quarter Mocker      $4.99  Burgers
Mock Nuggets (6)    $3.49  Sides
Mock Fries (L)      $2.99  Sides
Mock Cola (L)       $1.99  Drinks
Mock Shake          $3.99  Drinks
Mock Flurry         $2.49  Desserts
Apple Mock Pie      $1.49  Desserts
```

### Android APIs Exercised
- Activity lifecycle (onCreate/onResume/onPause/finish)
- setContentView with programmatic Views
- LinearLayout, TextView, Button, ListView
- BaseAdapter pattern
- Intent with putExtra/getStringExtra/getIntExtra
- startActivity / startActivityForResult / onActivityResult
- SharedPreferences (cart count, order history)
- SQLiteOpenHelper + SQLiteDatabase (menu table, cart table)
- Toast.makeText
- View.OnClickListener

### Expected Output
```
=== MockDonalds End-to-End Test ===
[PASS] MiniServer initialized
[PASS] MenuActivity created with 8 menu items
[PASS] ListView has 8 children
[PASS] Click item 0 → ItemDetailActivity launched
[PASS] Item name = "Big Mock Burger", price = "$5.99"
[PASS] Add to Cart → CartManager count = 1
[PASS] CartActivity shows 1 item, total = $5.99
[PASS] Checkout → order saved to SharedPreferences
[PASS] Cart cleared, count = 0
[PASS] Render: draw log contains "Big Mock Burger"
Results: 10 passed, 0 failed
```

## Verification Ladder

1. **Host JVM** (fastest): `java -cp build MockDonaldsRunner`
2. **Host Dalvik x86_64**: `dalvikvm -cp mockdonalds.dex MockDonaldsRunner`
3. **QEMU ARM32**: `hdc shell /data/a2oh/dalvikvm -cp mockdonalds.dex MockDonaldsRunner`

Each step validates the same output. Step 3 proves the full stack works.

## What's Deferred (Phase 2+)
- ArkUI native node creation (Views use Canvas-only rendering for now)
- XML layout inflation (programmatic UI only)
- Image loading (placeholder rectangles)
- Network/HTTP
- Fragments
- Real SQLite engine (in-memory HashMap sufficient)
- Real display output (headless Canvas recording)
