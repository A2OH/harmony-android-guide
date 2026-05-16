/*
 * common_event_subscriber_adapter.cpp
 *
 * Adapter subscriber implementation.
 * Receives OH CommonEvents and forwards them to Java via JNI callback.
 *
 * JNI callback target:
 *   CommonEventReceiverBridge.onCommonEventReceived(
 *       subscriptionId, ohAction, extrasJson, uri, code, data, ordered, sticky)
 */
#include "common_event_subscriber_adapter.h"
#include "oh_common_event_client.h"
#include "adapter_bridge.h"
#include <android/log.h>

#include "common_event_data.h"
#include "want.h"

#define LOG_TAG "OH_CESubscriberAdapter"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace oh_adapter {

// Cached JNI references for the callback method
static jclass g_receiverBridgeClass = nullptr;
static jmethodID g_onEventReceivedMethod = nullptr;

bool initCommonEventJNI(JNIEnv* env) {
    // Class moved from adapter.bridge.callback to adapter.broadcast.  Failing
    // FindClass would leak a JNI exception (NoClassDefFoundError), which the
    // call_once lambda in Java_adapter_*_OHEnvironment_nativeInitialize then
    // propagates to the Java caller as bare NCDFE with no cause.  Try the
    // current location first; clear pending exception on the legacy fallback.
    jclass clazz = env->FindClass("adapter/broadcast/CommonEventReceiverBridge");
    if (clazz == nullptr) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        clazz = env->FindClass("adapter/bridge/callback/CommonEventReceiverBridge");
    }
    if (clazz == nullptr) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        LOGE("Failed to find adapter/broadcast/CommonEventReceiverBridge class");
        return false;
    }
    g_receiverBridgeClass = (jclass)env->NewGlobalRef(clazz);

    g_onEventReceivedMethod = env->GetStaticMethodID(clazz, "onCommonEventReceived",
            "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;ZZ)V");
    if (g_onEventReceivedMethod == nullptr) {
        LOGE("Failed to find onCommonEventReceived method");
        return false;
    }

    LOGI("CommonEvent JNI callback initialized");
    return true;
}

AdapterEventSubscriber::AdapterEventSubscriber(
        const OHOS::EventFwk::CommonEventSubscribeInfo& subscribeInfo,
        int32_t subscriptionId)
    : OHOS::EventFwk::CommonEventSubscriber(subscribeInfo),
      subscriptionId_(subscriptionId) {
}

void AdapterEventSubscriber::OnReceiveEvent(const OHOS::EventFwk::CommonEventData& data) {
    // Extract event data
    auto want = data.GetWant();
    std::string action = want.GetAction();
    std::string extrasJson = want.GetStringParam("android_extras_json");
    std::string uri = want.GetUri().ToString();
    int32_t code = data.GetCode();
    std::string eventData = data.GetData();

    bool ordered = IsOrderedCommonEvent();
    bool sticky = IsStickyCommonEvent();

    LOGI("OnReceiveEvent: subscriptionId=%d, action=%s, ordered=%d, sticky=%d",
         subscriptionId_, action.c_str(), ordered, sticky);

    // For ordered events: go async so we can wait for finishReceiver from Java
    if (ordered) {
        auto asyncResult = GoAsyncCommonEvent();
        if (asyncResult) {
            OHCommonEventClient::getInstance().storeAsyncResult(subscriptionId_, asyncResult);
        }
    }

    // Get JNI environment (we may be on an OH IPC thread)
    JNIEnv* env = AdapterBridge::getInstance().getEnv();
    if (!env) {
        LOGE("OnReceiveEvent: failed to get JNI environment");
        return;
    }
    if (!g_receiverBridgeClass || !g_onEventReceivedMethod) {
        LOGE("OnReceiveEvent: JNI callback not initialized");
        return;
    }

    // Convert C++ strings to Java strings
    jstring jAction = env->NewStringUTF(action.c_str());
    jstring jExtrasJson = extrasJson.empty() ? nullptr : env->NewStringUTF(extrasJson.c_str());
    jstring jUri = uri.empty() ? nullptr : env->NewStringUTF(uri.c_str());
    jstring jData = eventData.empty() ? nullptr : env->NewStringUTF(eventData.c_str());

    // Call Java: CommonEventReceiverBridge.onCommonEventReceived(...)
    env->CallStaticVoidMethod(g_receiverBridgeClass, g_onEventReceivedMethod,
            subscriptionId_,
            jAction, jExtrasJson, jUri,
            code, jData,
            (jboolean)ordered, (jboolean)sticky);

    // Clean up local references
    if (jAction) env->DeleteLocalRef(jAction);
    if (jExtrasJson) env->DeleteLocalRef(jExtrasJson);
    if (jUri) env->DeleteLocalRef(jUri);
    if (jData) env->DeleteLocalRef(jData);

    // Check for Java exceptions
    if (env->ExceptionCheck()) {
        LOGE("OnReceiveEvent: Java exception occurred");
        env->ExceptionClear();
    }
}

}  // namespace oh_adapter
