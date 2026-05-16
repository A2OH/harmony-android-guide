# CR61.1 — Amendment: libipc / samgr permitted via HBC adapter pattern (V3 path only)

**Date:** 2026-05-16
**Author:** agent 42 (during V3 architecture authoring)
**Status:** AMENDMENT — relaxes CR61's "no libipc / no samgr" rule for the V3 OHOS path only; the V2 Android-phone path is unaffected
**Amends:** `CR61_BINDER_STRATEGY_POST_CR60.md` (2026-05-14)
**Does not supersede:** any landed binder code in `aosp-libbinder-port/`. The musl + bionic builds Westlake ships for the Android-phone V2 path stay green.
**Cross-refs:** `V3-ARCHITECTURE.md` §§3-5, `CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md` §§2-4, `CR-FF-HBC-BORROWABLE-PATTERNS.md` §"The CR61-equivalent finding"

---

## TL;DR

CR61 (2026-05-14) ruled that Westlake **must not** call into `libipc.dylib.so` / `libsamgr.dylib.so` from the dalvikvm process and **must not** register Westlake services on OHOS samgr. That rule was correct for the V2-OHOS path (where Westlake owns its own substrate top-to-bottom).

V3 (2026-05-16) replaces that path. Under V3 the V2 substrate is replaced by HBC's runtime substrate, which is **already a platformsdk consumer** linking `ipc_core` + `samgr_proxy` (innerAPI variants) — see CR-EE §2 final paragraph and CR-FF §"The CR61-equivalent finding". The CR61 prohibition therefore needs an explicit retraction for the V3 path. Without it, V3 is internally inconsistent with itself.

**Amendment:** under V3, platform-level adapters MAY link `libipc_core` and `libsamgr_proxy` via their innerAPI variants. The macro-shim contract still applies unchanged at the Java level. The Android-phone V2 path is unaffected — CR61 stands there.

---

## 1. What CR61 said (verbatim summary)

`CR61_BINDER_STRATEGY_POST_CR60.md` §"Decision 1" stated:

> We continue to ship the existing `aosp-libbinder-port/` artifacts (cross-compiled from AOSP source) and use them as the only IPC stack inside the Westlake process tree.
>
> **Concrete rejection list:** we do NOT call into `libipc.dylib.so`, `libsamgr.dylib.so`, or `/system/bin/samgr` from our dalvikvm process. We do NOT register Westlake services on OHOS's samgr. We do NOT translate AOSP parcel format ↔ OHOS parcel format.

The rationale was sound for V2-OHOS:
- Android APKs in Westlake call AOSP binder APIs through framework.jar / aosp-shim.dex
- Re-implementing every framework binder transaction against OHOS `IRemoteObject` would have been "substitute-everything" anti-architecture
- Our libbinder already passed 14/14 regression on Android phone
- OHOS libipc would not interop with `framework.jar` service stubs without per-service adapters

---

## 2. What changed (V3)

V3 inverts the premise: instead of substituting at the framework-class level (V2), we adopt HBC's runtime, which **uses the real `framework.jar` byte-perfect** and instead transcodes at the Binder-service boundary via 4 surgical L5 patches:

- `ActivityManager.java` — `IActivityManagerSingleton.create()` → `OHEnvironment.getActivityManagerAdapter()`
- `ActivityTaskManager.java` — same pattern
- `ActivityThread.java` — `getPackageManager()` → adapter
- `WindowManagerGlobal.java` — `getWindowManagerService()` + `getWindowSession()` → adapter

Each adapter is a `Java class extends IXxx.Stub` (real AOSP-generated Stub), with a `long mOhXxxHandle` and `native` methods that forward into HBC's `liboh_adapter_bridge.so` (the C++ side of the adapter), which **does link real OH `ipc_core` + `samgr_proxy` (innerAPI variants)** — see CR-EE §2 verifying:

> `liboh_adapter_bridge.so` links OH `ipc_core` + `samgr_proxy` + ability_runtime inner_api + `libwms` + `librender_service_client` — the adapter native side is a **first-class OH platformsdk consumer**, not a HAP-style NDK consumer.

The HBC project's own internal discipline doc (`~/adapter/doc/technical_decision_overview.html` §2.10, 2026-04-15, "innerAPI 优先于 NDK") rules:

> 本项目与 OH 任何子系统对接时，一律链接 innerAPI / platformsdk / chipsetsdk 变体，不链 NDK 公共变体。

Translated: "When this project integrates with any OH subsystem, it shall link the innerAPI / platformsdk / chipsetsdk variant; never the NDK public variant."

Five reasons (CR-FF §"The CR61-equivalent finding"):
1. The project is a **platform-level adapter layer**, not a sandboxed app. Adapter binaries live in `/system/`; therefore platformsdk consumers, not NDK consumers.
2. OH's own `innerapi_tags = [chipsetsdk, platformsdk, sasdk]` is an **explicit whitelist**; linking platform-tagged inner APIs is permitted by OHOS itself.
3. NDK is a strict semantic subset. NDK `OH_LOG_Print` only has `LOG_APP`; innerAPI `HiLogPrint` has 5 LogTypes mapping cleanly to Android `log_id_t`'s 6 buffers. NDK lacks equivalents for `__android_log_set_minimum_priority`. The same NDK gap exists across `ipc_core`, `bms`, `ability_runtime`.
4. innerAPI .so already exist on every device's `/system/lib/` (OH system services use them); no new artifact ships.
5. Fallback hierarchy: `innerAPI → libhilog_base.a static lib → kmsg / socket directly`. **Never NDK** for a platform-level adapter.

---

## 3. The amendment (concrete rule changes)

**A. CR61 Decision 1 — "Ship our own libbinder, NOT OHOS libipc"**

| Path | V3 rule |
|---|---|
| Android-phone V2 path (cfb7c9e3 etc.) | UNCHANGED. Westlake ships `aosp-libbinder-port/out/{musl,bionic}/libbinder.so` + `servicemanager`. No libipc, no samgr. CR61 stands. |
| OHOS V3 path (DAYU200 etc.) | **AMENDED.** Westlake reuses HBC's runtime substrate including HBC's `liboh_adapter_bridge.so`. That .so links real OH `libipc_core` and `libsamgr_proxy` (innerAPI variants). Westlake-owned dalvikvm code never directly `dlopen`s libipc or libsamgr — the linkage is transitive through the HBC-provided adapter, exactly as a platformsdk consumer does on any OHOS system process. |

**B. CR61 Decision 2 — "Talk to the kernel via `/dev/vndbinder`"**

| Path | V3 rule |
|---|---|
| Android-phone V2 path | UNCHANGED. `/dev/vndbinder` via Westlake libbinder. |
| OHOS V3 path | NOT APPLICABLE. Under V3 the OHOS-side Binder traffic for Westlake apps goes through OH IPC (`libipc_core` over OHOS's own IPC channel registered with samgr), not through `/dev/vndbinder`. The kernel binder driver still being live on DAYU200 is irrelevant to V3 because we don't use it on the OHOS path. |

**C. CR61 Decision 3 — "Run our own servicemanager in parallel with OHOS samgr"**

| Path | V3 rule |
|---|---|
| Android-phone V2 path | UNCHANGED. |
| OHOS V3 path | **AMENDED.** No Westlake-owned `servicemanager` process on OHOS. HBC's 5 forward bridges (IXxx.Stub subclasses) + 6 reverse C++/Java bridges route through OH samgr the same way any OH system process does. Westlake apps never register services with OHOS samgr — they consume HBC's adapter-provided handles. |

**D. CR61 Decision 4 — "SELinux policy plan for `/dev/vndbinder` from non-root"**

| Path | V3 rule |
|---|---|
| Android-phone V2 path | UNCHANGED. |
| OHOS V3 path | SUPERSEDED by HBC's existing SELinux policy work. HBC ships `ohos_patches/selinux_adapter/file_contexts.patch` which adds `/system/bin/appspawn-x → appspawn_exec`, `/system/android/lib/* → system_lib_file` (CR-EE §4 table). Westlake inherits that policy via the HBC artifact pull (W1) and the deploy SOP (W9). `setenforce 0` remains forbidden — same rule as CR61. |

**E. CR61 Decision 5 — "M9/M10/M11/M12 retargeting matrix"**

| Milestone | CR61 (V2) state | V3 state |
|---|---|---|
| M9 — binder.ko for OHOS kernel | DONE by OHOS itself | NOT APPLICABLE in V3 (we don't use the kernel binder driver on OHOS path) |
| M10 — libbinder + servicemanager for OHOS sysroot | Cross-compile for arm-linux-ohos-musl | **DELETED.** Replaced by HBC's `liboh_adapter_bridge.so`. The Android-phone target of M10 remains; only the OHOS arm32 target is dropped. |
| M11 — Audio daemon swap → OHOS AudioRenderer | In-process via dalvikvm dlopen | **DELETED for OHOS path.** Replaced by HBC's audio path. M5 audio daemon retained for Android phone. |
| M12 — Surface daemon swap → OHOS XComponent | In-process via dalvikvm dlopen | **DELETED for OHOS path.** Replaced by HBC's `ANativeWindow → IBufferProducer` path. M6 surface daemon retained for Android phone. |
| M13 — noice on OHOS phone | Terminal milestone | Renumbered V3-W6 (noice on OHOS via V3). McD on OHOS = V3-W7. |

---

## 4. Constraints that are KEPT under CR61.1

The amendment **does not weaken** any of the following — these continue verbatim:

- **Macro-shim contract** (`feedback_macro_shim_contract.md`) — Java-side rule unchanged: no `Unsafe.allocateInstance`, no `setAccessible(true)`, no per-app branches, no NoSuchMethodError catching. Now applied at the **integration seam between Westlake engine and HBC runtime**, not at framework-class level (which is real AOSP under V3). See `V3-ARCHITECTURE.md` §7.
- **No `setenforce 0` "fix."** SELinux issues are addressed via documented policy work or HBC-shipped policy modules. Not via disabling MAC.
- **No per-app branches** anywhere in Westlake-owned code. Same V2 rule, same grep.
- **No fork of HBC adapter sources.** If a fix is needed, either (a) propose to HBC, (b) shadow the class in `oh-adapter-runtime.jar` (PathClassLoader-loaded), or (c) document the blocker and STOP. Direct edits to HBC source are a one-way door to drift.
- **APK transparency** (zero `import adapter.*` or `import com.westlake.*` in any APK source) is now enforced at W6/W7 acceptance with an anti-grep CI check (CR-EE §11.1).
- **CR60 bitness discipline** unchanged. V3 stack is 32-bit ARM on DAYU200. `intptr_t` / `size_t` for pointer-sized integers; dual-arch CI; no `#ifdef __aarch64__` branches in Java/JNI bridge code.

---

## 5. Migration impact

| Already-landed code | Status under CR61.1 |
|---|---|
| `aosp-libbinder-port/out/musl/libbinder.so` | KEPT — Android-phone V2 path uses it |
| `aosp-libbinder-port/out/bionic/libbinder.so` | KEPT — Android-phone V2 path uses it |
| `aosp-libbinder-port/out/arm32/` (any OHOS-target binder builds) | ARCHIVED — moved under `archive/v2-ohos-substrate/`; not deleted |
| Westlake `servicemanager` binary (any OHOS-target build) | ARCHIVED — same disposition |
| `aosp-audio-daemon-port/out/<ohos>` | ARCHIVED |
| `aosp-surface-daemon-port/out/<ohos>` | ARCHIVED |
| All V2 substrate Java for OHOS (Westlake-shadowed framework classes deployed via `framework_duplicates.txt`) | KEPT — still useful as Android-phone V2 fallback; OHOS path now uses real HBC `framework.jar` |
| `OhosMvpLauncher` and OHOS-side launchers | ARCHIVED |
| `SoftwareCanvas` + `drm_inproc_bridge.c` | ARCHIVED |

No deletion. All "archived" artifacts move under `archive/v2-ohos-substrate/` with a top-level `README.md` pointing to this CR61.1 + `V3-ARCHITECTURE.md` §4 so future readers know why.

---

## 6. Self-audit gate for any V3 CR that touches IPC

Run before reporting complete:

```bash
# 1. No direct dlopen of libipc / libsamgr from Westlake-owned code
grep -rn 'dlopen.*"libipc\|dlopen.*"libsamgr\|System.loadLibrary("ipc_core"\|System.loadLibrary("samgr_proxy"' \
  $(find . -type d -name 'third_party' -prune -o -type f \( -name '*.java' -o -name '*.kt' -o -name '*.cpp' -o -name '*.c' -o -name '*.h' \) -print) \
  | grep -v "^.*://" \
  && echo "FAIL: direct OH IPC dlopen from Westlake code" || echo "OK"

# 2. No Westlake fork of HBC adapter source
git diff --name-only HEAD~1 | grep -E "^third_party/hbc-runtime/(framework|aosp_patches|ohos_patches)/" \
  && echo "FAIL: edited HBC source" || echo "OK"

# 3. No setenforce 0
grep -rn "setenforce 0\|setenforce=0\|setenforce  *0" $(find . -name '.git' -prune -o -type f \( -name '*.sh' -o -name '*.py' -o -name '*.md' \) -print) \
  | grep -v "forbidden\|do NOT\|never" \
  && echo "FAIL: setenforce 0 in scripts" || echo "OK"
```

All three must report `OK`.

---

## 7. Cross-references

- `CR61_BINDER_STRATEGY_POST_CR60.md` — original CR61 (still authoritative for Android-phone path)
- `V3-ARCHITECTURE.md` §§3-5, §7 — V3 layer stack, component ownership, what we delete/keep/borrow
- `V3-WORKSTREAMS.md` — W1 (HBC artifact pull) operationalizes this amendment
- `CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md` §2 (final paragraph) — HBC's adapter native side is a platformsdk consumer
- `CR-FF-HBC-BORROWABLE-PATTERNS.md` §"The CR61-equivalent finding" — 5-bullet rationale for innerAPI > NDK
- `CR60_BITNESS_PIVOT_DECISION.md` — 32-bit ARM userspace constraint (preserved)
- `feedback_macro_shim_contract.md` — Java-side contract (unchanged)
- HBC source: `~/adapter/doc/technical_decision_overview.html` §2.10 (the rule we adopt)
