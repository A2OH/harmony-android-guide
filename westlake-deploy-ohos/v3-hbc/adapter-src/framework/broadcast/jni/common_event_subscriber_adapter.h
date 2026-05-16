/*
 * common_event_subscriber_adapter.h
 *
 * Adapter subscriber for OH CommonEventService.
 * Inherits OH CommonEventSubscriber and forwards received events
 * to the Java layer via JNI callback.
 *
 * Each instance is associated with a subscriptionId that maps to
 * an Android IIntentReceiver in the Java layer.
 *
 * Reference paths:
 *   OH: ces/interfaces/inner_api/common_event_subscriber.h
 *   OH: ces/interfaces/inner_api/async_common_event_result.h
 */
#ifndef COMMON_EVENT_SUBSCRIBER_ADAPTER_H
#define COMMON_EVENT_SUBSCRIBER_ADAPTER_H
#include <jni.h>

#include "common_event_subscriber.h"
#include "common_event_subscribe_info.h"
#include <string>

namespace oh_adapter {

class AdapterEventSubscriber : public OHOS::EventFwk::CommonEventSubscriber {
public:
    AdapterEventSubscriber(const OHOS::EventFwk::CommonEventSubscribeInfo& subscribeInfo,
                           int32_t subscriptionId);

    /**
     * Called by CES when an event matching our subscription is received.
     * Extracts event data and calls back to Java CommonEventReceiverBridge.
     */
    void OnReceiveEvent(const OHOS::EventFwk::CommonEventData& data) override;

    int32_t getSubscriptionId() const { return subscriptionId_; }

private:
    int32_t subscriptionId_;
};

/**
 * Initialize JNI callback references for CommonEvent bridging.
 * Must be called once during adapter initialization (from AdapterBridge::initialize).
 */
bool initCommonEventJNI(JNIEnv* env);

}  // namespace oh_adapter

#endif  // COMMON_EVENT_SUBSCRIBER_ADAPTER_H
