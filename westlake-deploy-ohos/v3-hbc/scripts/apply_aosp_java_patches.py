#!/usr/bin/env python3
"""Apply AOSP Java patches in-place to $AOSP_ROOT/frameworks/base/.

Strategy:
- Each patch modifies a Singleton.create() method (or equivalent factory) to first
  attempt loading the OH adapter via reflection. If reflection fails, falls through
  to the original AOSP implementation. This keeps framework.jar's compile-time
  classpath unchanged (zero static dep on adapter.* classes).

- Backup is taken once per file (.bak.adapter). If .bak.adapter already exists,
  it is treated as the canonical original — re-running the script restores from
  it before re-patching, so the script is idempotent.

- After patching, the patched files stay in place (NO revert) per CLAUDE.md
  Build Isolation Rule.

- Modified files are appended to $LOG_FILE for build_patch_log.

Path resolution (2026-04-11 made portable):
  AOSP root priority:
    1. --aosp-root=PATH command-line flag
    2. $AOSP_ROOT environment variable
    3. $HOME/aosp (default)
  Log file:
    1. $ADAPTER_LOG_FILE environment variable
    2. /tmp/aosp_patches_applied.txt (default)
"""
import os
import re
import shutil
import sys
from datetime import datetime


def _resolve_aosp_base():
    """Return $AOSP_ROOT/frameworks/base/core/java/android, with CLI/env fallback."""
    root = None
    for arg in sys.argv[1:]:
        if arg.startswith('--aosp-root='):
            root = arg.split('=', 1)[1]
            break
    if root is None:
        root = os.environ.get('AOSP_ROOT')
    if root is None:
        root = os.path.expanduser('~/aosp')
    return os.path.join(root, 'frameworks/base/core/java/android')


AOSP_BASE = _resolve_aosp_base()
LOG_FILE = os.environ.get('ADAPTER_LOG_FILE', '/tmp/aosp_patches_applied.txt')

if not os.path.isdir(AOSP_BASE):
    print(f'[apply_aosp_java_patches] ERROR: AOSP_BASE not found: {AOSP_BASE}', file=sys.stderr)
    print(f'[apply_aosp_java_patches] Set $AOSP_ROOT or pass --aosp-root=PATH', file=sys.stderr)
    sys.exit(2)

print(f'[apply_aosp_java_patches] AOSP_BASE = {AOSP_BASE}')
print(f'[apply_aosp_java_patches] LOG_FILE  = {LOG_FILE}')

PATCHES = []  # populated below

# ============================================================================
# Helper: backup-then-edit
# ============================================================================

def patch_file(rel_path, fn, description):
    """Apply fn() to file. fn takes file content string and returns new content.
    Backup taken once as .bak.adapter."""
    full = os.path.join(AOSP_BASE, rel_path)
    if not os.path.exists(full):
        print(f'  SKIP {rel_path}: file not found')
        return False
    bak = full + '.bak.adapter'
    if not os.path.exists(bak):
        shutil.copy(full, bak)
        print(f'  BACKUP {os.path.basename(full)} -> .bak.adapter')
    # Always re-read from .bak.adapter so re-runs are idempotent
    with open(bak) as f:
        original = f.read()
    new = fn(original)
    if new == original:
        print(f'  WARN {rel_path}: patch produced no change')
        return False
    with open(full, 'w') as f:
        f.write(new)
    print(f'  PATCHED {rel_path} ({description})')
    with open(LOG_FILE, 'a') as log:
        log.write(f'{datetime.now().isoformat()}\t{full}\t{description}\n')
    return True

# ============================================================================
# Patch 1: ActivityManager.java
# ============================================================================

def patch_activity_manager(c):
    old = '''    private static final Singleton<IActivityManager> IActivityManagerSingleton =
            new Singleton<IActivityManager>() {
                @Override
                protected IActivityManager create() {
                    final IBinder b = ServiceManager.getService(Context.ACTIVITY_SERVICE);
                    final IActivityManager am = IActivityManager.Stub.asInterface(b);
                    return am;
                }
            };'''
    new = '''    private static final Singleton<IActivityManager> IActivityManagerSingleton =
            new Singleton<IActivityManager>() {
                @Override
                protected IActivityManager create() {
                    // [OH_ADAPTER B.40] Delegate to OHEnvironment which uses
                    // system classloader to load adapter from oh-adapter-runtime.jar
                    // (PathClassLoader, non-BCP).  Keeps framework.jar's BCP
                    // dependency narrow (only OHEnvironment, no concrete adapter).
                    try {
                        Class<?> envClass = Class.forName("adapter.core.OHEnvironment");
                        Boolean isOH = (Boolean) envClass.getMethod("isOHEnvironment").invoke(null);
                        if (Boolean.TRUE.equals(isOH)) {
                            Object adapter = envClass.getMethod("getActivityManagerAdapter").invoke(null);
                            if (adapter != null) return (IActivityManager) adapter;
                        }
                    } catch (Throwable t) {
                        android.util.Log.w("ActivityManager",
                            "OH adapter init failed, falling back: " + t);
                    }
                    // Original AOSP path (fallback)
                    final IBinder b = ServiceManager.getService(Context.ACTIVITY_SERVICE);
                    final IActivityManager am = IActivityManager.Stub.asInterface(b);
                    return am;
                }
            };'''
    if old not in c:
        # Already patched? Check for our marker.
        if '[OH_ADAPTER]' in c:
            return c  # idempotent
        raise RuntimeError('ActivityManager.java: target block not found')
    return c.replace(old, new)

PATCHES.append(('app/ActivityManager.java', patch_activity_manager,
                'Inject OH adapter via reflection in IActivityManagerSingleton'))

# ============================================================================
# Patch 2: ActivityTaskManager.java
# ============================================================================

def patch_activity_task_manager(c):
    # Find IActivityTaskManagerSingleton create() and inject reflection
    # The exact form is similar to ActivityManager
    pat = re.compile(
        r'(private static final Singleton<IActivityTaskManager>\s+IActivityTaskManagerSingleton\s*=\s*'
        r'new Singleton<IActivityTaskManager>\(\)\s*\{\s*'
        r'@Override\s*'
        r'protected IActivityTaskManager create\(\)\s*\{)'
        r'(\s*final IBinder b = ServiceManager\.getService\(Context\.ACTIVITY_TASK_SERVICE\);)',
        re.MULTILINE
    )
    if '[OH_ADAPTER]' in c:
        return c  # idempotent
    m = pat.search(c)
    if not m:
        raise RuntimeError('ActivityTaskManager.java: target block not found')
    inject = '''
                    // [OH_ADAPTER B.40] Delegate to OHEnvironment system-classloader factory
                    try {
                        Class<?> envClass = Class.forName("adapter.core.OHEnvironment");
                        Boolean isOH = (Boolean) envClass.getMethod("isOHEnvironment").invoke(null);
                        if (Boolean.TRUE.equals(isOH)) {
                            Object adapter = envClass.getMethod("getActivityTaskManagerAdapter").invoke(null);
                            if (adapter != null) return (IActivityTaskManager) adapter;
                        }
                    } catch (Throwable t) {
                        android.util.Log.w("ActivityTaskManager",
                            "OH adapter init failed, falling back: " + t);
                    }
                    // Original AOSP path (fallback)'''
    return c[:m.end(1)] + inject + c[m.start(2):]

PATCHES.append(('app/ActivityTaskManager.java', patch_activity_task_manager,
                'Inject OH adapter via reflection in IActivityTaskManagerSingleton'))

# ============================================================================
# Patch 3: ActivityThread.java (getPackageManager)
# ============================================================================

def patch_activity_thread(c):
    if '[OH_ADAPTER]' in c:
        return c
    pat = re.compile(
        r'(public static IPackageManager getPackageManager\(\)\s*\{)'
        r'(\s*if \(sPackageManager != null\) \{)'
    )
    m = pat.search(c)
    if not m:
        raise RuntimeError('ActivityThread.java: getPackageManager not found')
    inject = '''
        // [OH_ADAPTER B.40] Delegate to OHEnvironment system-classloader factory
        if (sPackageManager == null) {
            try {
                Class<?> envClass = Class.forName("adapter.core.OHEnvironment");
                Boolean isOH = (Boolean) envClass.getMethod("isOHEnvironment").invoke(null);
                if (Boolean.TRUE.equals(isOH)) {
                    Object adapter = envClass.getMethod("getPackageManagerAdapter").invoke(null);
                    if (adapter != null) {
                        sPackageManager = (IPackageManager) adapter;
                        return sPackageManager;
                    }
                }
            } catch (Throwable t) {
                android.util.Log.w("ActivityThread",
                    "OH adapter init failed, falling back: " + t);
            }
        }'''
    return c[:m.end(1)] + inject + c[m.start(2):]

PATCHES.append(('app/ActivityThread.java', patch_activity_thread,
                'Inject OH adapter via reflection in getPackageManager'))

# ============================================================================
# Patch 4: WindowManagerGlobal.java (getWindowManagerService + getWindowSession)
# ============================================================================

def patch_window_manager_global(c):
    if '[OH_ADAPTER]' in c:
        return c

    # 4a: getWindowManagerService
    pat1 = re.compile(
        r'(public static IWindowManager getWindowManagerService\(\)\s*\{\s*'
        r'synchronized \(WindowManagerGlobal\.class\)\s*\{)'
        r'(\s*if \(sWindowManagerService == null\) \{)'
    )
    m1 = pat1.search(c)
    if not m1:
        raise RuntimeError('WindowManagerGlobal.java: getWindowManagerService not found')
    inject1 = '''
                // [OH_ADAPTER B.40] Delegate to OHEnvironment system-classloader factory
                if (sWindowManagerService == null) {
                    try {
                        Class<?> envClass = Class.forName("adapter.core.OHEnvironment");
                        Boolean isOH = (Boolean) envClass.getMethod("isOHEnvironment").invoke(null);
                        if (Boolean.TRUE.equals(isOH)) {
                            Object adapter = envClass.getMethod("getWindowManagerAdapter").invoke(null);
                            if (adapter != null) {
                                sWindowManagerService = (IWindowManager) adapter;
                                return sWindowManagerService;
                            }
                        }
                    } catch (Throwable t) {
                        android.util.Log.w("WindowManagerGlobal",
                            "OH adapter (WMS) init failed, falling back: " + t);
                    }
                }'''
    c = c[:m1.end(1)] + inject1 + c[m1.start(2):]

    # 4b: getWindowSession
    pat2 = re.compile(
        r'(public static IWindowSession getWindowSession\(\)\s*\{\s*'
        r'synchronized \(WindowManagerGlobal\.class\)\s*\{)'
        r'(\s*if \(sWindowSession == null\) \{)'
    )
    m2 = pat2.search(c)
    if not m2:
        raise RuntimeError('WindowManagerGlobal.java: getWindowSession not found')
    inject2 = '''
                // [OH_ADAPTER B.40] Delegate to OHEnvironment system-classloader factory
                if (sWindowSession == null) {
                    try {
                        Class<?> envClass = Class.forName("adapter.core.OHEnvironment");
                        Boolean isOH = (Boolean) envClass.getMethod("isOHEnvironment").invoke(null);
                        if (Boolean.TRUE.equals(isOH)) {
                            Object adapter = envClass.getMethod("getWindowSessionAdapter").invoke(null);
                            if (adapter != null) {
                                sWindowSession = (IWindowSession) adapter;
                                return sWindowSession;
                            }
                        }
                    } catch (Throwable t) {
                        android.util.Log.w("WindowManagerGlobal",
                            "OH adapter (Session) init failed, falling back: " + t);
                    }
                }'''
    c = c[:m2.end(1)] + inject2 + c[m2.start(2):]
    return c

PATCHES.append(('view/WindowManagerGlobal.java', patch_window_manager_global,
                'Inject OH adapter via reflection in getWindowManagerService + getWindowSession'))

# ============================================================================
# Main
# ============================================================================

def main():
    # Truncate log file
    with open(LOG_FILE, 'w') as f:
        f.write(f'# AOSP Java patches applied at {datetime.now().isoformat()}\n')

    print(f'Applying {len(PATCHES)} AOSP Java patches...')
    print(f'AOSP base: {AOSP_BASE}')
    print()

    failures = 0
    for rel_path, fn, desc in PATCHES:
        try:
            patch_file(rel_path, fn, desc)
        except Exception as e:
            print(f'  FAIL {rel_path}: {e}')
            failures += 1

    print()
    print(f'Done: {len(PATCHES) - failures} patched, {failures} failed')
    print(f'Modified file list: {LOG_FILE}')
    return failures

if __name__ == '__main__':
    sys.exit(main())
