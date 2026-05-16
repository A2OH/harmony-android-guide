/*
 * adapter_bridge.h
 *
 * JNI Bridge header file.
 * Defines the bridging interface between Java and C++.
 */
#ifndef OH_ADAPTER_BRIDGE_H
#define OH_ADAPTER_BRIDGE_H

#include <jni.h>
#include <string>
#include <memory>

namespace oh_adapter {

/**
 * JNI Bridge manager class.
 * Holds JVM reference and manages Java callbacks.
 */
class AdapterBridge {
public:
    static AdapterBridge& getInstance();

    bool initialize(JNIEnv* env);
    void shutdown();

    // Store JVM reference for use by OH callback threads
    void setJavaVM(JavaVM* vm) { jvm_ = vm; }
    JavaVM* getJavaVM() const { return jvm_; }

    // Store Java-side LifecycleAdapter singleton reference
    void setLifecycleAdapterRef(JNIEnv* env, jobject obj);

    // Call Java methods from OH callback threads
    void callbackLifecycleChange(int abilityToken, int ohState);

    JNIEnv* getEnv();

private:
    AdapterBridge() = default;
    ~AdapterBridge() = default;

    JavaVM* jvm_ = nullptr;
    jobject lifecycle_adapter_ref_ = nullptr;  // Global ref to LifecycleAdapter singleton
    jclass lifecycle_adapter_class_ = nullptr;
    jmethodID on_oh_lifecycle_callback_ = nullptr;
};

}  // namespace oh_adapter

#endif  // OH_ADAPTER_BRIDGE_H
