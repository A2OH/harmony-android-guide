/**
 * appspawn-x child process entry point.
 *
 * After fork(), the child process calls ChildMain::run() which applies
 * OH security restrictions (DAC, sandbox, SELinux, AccessToken), initializes
 * the adapter layer, and enters the Android ActivityThread event loop.
 */

#pragma once

#include "spawn_msg.h"
#include <jni.h>

namespace appspawnx {

class AppSpawnXRuntime;

class ChildMain {
public:
    // Run in child process after fork.
    // This function does not return – it enters ActivityThread.main() event loop.
    [[noreturn]] static void run(const SpawnMsg& msg, AppSpawnXRuntime* runtime);

private:
    // OH security specialization
    static int applyDac(const SpawnMsg& msg);
    static int applySandbox(const SpawnMsg& msg);
    static int applySELinux(const SpawnMsg& msg);
    static int applyAccessToken(const SpawnMsg& msg);

    // Android initialization — both use the runtime's cached PathClassLoader
    // (when available) to find classes in oh-adapter-runtime.jar, avoiding
    // bootstrap-classloader re-load of liboh_adapter_bridge.so.
    static int initAdapterLayer(JNIEnv* env, AppSpawnXRuntime* runtime);
    static void launchActivityThread(JNIEnv* env, const SpawnMsg& msg,
                                     AppSpawnXRuntime* runtime);
};

} // namespace appspawnx
