================================================================================
  Android-OpenHarmony Adapter: AOSP Source Code Modifications
================================================================================

This directory contains the AOSP framework source files that need to be modified
to install the OH adapter bridges. Each file is a copy of the original AOSP 14
source with targeted modifications marked by "OH_ADAPTER" comments.

Files modified:
  1. frameworks/core/java/android/app/ActivityManager.java
     - IActivityManagerSingleton.create() returns ActivityManagerAdapter
  2. frameworks/core/java/android/app/ActivityTaskManager.java
     - IActivityTaskManagerSingleton.create() returns ActivityTaskManagerAdapter
  3. frameworks/core/java/android/view/WindowManagerGlobal.java
     - getWindowManagerService() returns WindowManagerAdapter
     - getWindowSession() returns WindowSessionAdapter

All modifications are conditional: they check if the OH adaptation layer is
available before replacing the normal service proxy. If the adapter is not
present (e.g., running on a standard Android device), the original behavior
is preserved.

Original AOSP source location: D:\code\android\frameworks\
Adapter source location: D:\code\adapter\framework\

HOW TO APPLY:
  Copy the modified files to the corresponding paths under your AOSP source tree,
  replacing the originals. Then rebuild the framework module.
================================================================================
