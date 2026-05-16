/*
 * oh_common_event_client.cpp
 *
 * OpenHarmony CommonEventService IPC client implementation.
 *
 * Uses OH CommonEventManager API to subscribe/unsubscribe/publish events.
 * Manages AdapterEventSubscriber instances that bridge OH events to JNI callbacks.
 *
 * Reference paths:
 *   OH: ces/interfaces/inner_api/common_event_manager.h
 *   OH: ces/interfaces/inner_api/common_event_data.h
 *   OH: ces/interfaces/inner_api/common_event_publish_info.h
 *   OH: ces/interfaces/inner_api/matching_skills.h
 */
#include "oh_common_event_client.h"
#include "common_event_subscriber_adapter.h"
#include <android/log.h>

#include "common_event_manager.h"
#include "common_event_data.h"
#include "common_event_publish_info.h"
#include "common_event_subscribe_info.h"
#include "matching_skills.h"
#include "want.h"

#define LOG_TAG "OH_CEClient"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

namespace oh_adapter {

OHCommonEventClient& OHCommonEventClient::getInstance() {
    static OHCommonEventClient instance;
    return instance;
}

int OHCommonEventClient::subscribe(int32_t subscriptionId,
                                    const std::vector<std::string>& events,
                                    int32_t priority,
                                    const std::string& permission) {
    std::lock_guard<std::mutex> lock(mutex_);

    LOGI("Subscribe: subscriptionId=%d, eventCount=%zu, priority=%d",
         subscriptionId, events.size(), priority);

    // Build MatchingSkills from event names
    OHOS::EventFwk::MatchingSkills matchingSkills;
    for (const auto& event : events) {
        matchingSkills.AddEvent(event);
        LOGI("  event: %s", event.c_str());
    }

    // Build subscribe info
    OHOS::EventFwk::CommonEventSubscribeInfo subscribeInfo(matchingSkills);
    subscribeInfo.SetPriority(priority);
    if (!permission.empty()) {
        subscribeInfo.SetPermission(permission);
    }

    // Create adapter subscriber
    auto subscriber = std::make_shared<AdapterEventSubscriber>(subscribeInfo, subscriptionId);

    // Register with CES
    bool result = OHOS::EventFwk::CommonEventManager::SubscribeCommonEvent(subscriber);
    if (!result) {
        LOGE("Subscribe failed for subscriptionId=%d", subscriptionId);
        return -1;
    }

    subscribers_[subscriptionId] = subscriber;
    LOGI("Subscribe successful: subscriptionId=%d", subscriptionId);
    return 0;
}

int OHCommonEventClient::unsubscribe(int32_t subscriptionId) {
    std::lock_guard<std::mutex> lock(mutex_);

    LOGI("Unsubscribe: subscriptionId=%d", subscriptionId);

    auto it = subscribers_.find(subscriptionId);
    if (it == subscribers_.end()) {
        LOGW("Unsubscribe: subscriptionId=%d not found", subscriptionId);
        return -1;
    }

    bool result = OHOS::EventFwk::CommonEventManager::UnSubscribeCommonEvent(it->second);
    if (!result) {
        LOGE("Unsubscribe failed for subscriptionId=%d", subscriptionId);
        return -1;
    }

    subscribers_.erase(it);
    asyncResults_.erase(subscriptionId);
    LOGI("Unsubscribe successful: subscriptionId=%d", subscriptionId);
    return 0;
}

int OHCommonEventClient::publish(const std::string& event, const std::string& extrasJson,
                                  const std::string& uri, int32_t code,
                                  const std::string& data,
                                  bool ordered, bool sticky,
                                  const std::vector<std::string>& subscriberPermissions) {
    LOGI("Publish: event=%s, ordered=%d, sticky=%d", event.c_str(), ordered, sticky);

    // Build Want with event action
    OHOS::AAFwk::Want want;
    want.SetAction(event);
    if (!uri.empty()) {
        want.SetUri(uri);
    }
    if (!extrasJson.empty()) {
        want.SetParam("android_extras_json", extrasJson);
    }

    // Build CommonEventData
    OHOS::EventFwk::CommonEventData eventData;
    eventData.SetWant(want);
    eventData.SetCode(code);
    eventData.SetData(data);

    // Build PublishInfo
    OHOS::EventFwk::CommonEventPublishInfo publishInfo;
    publishInfo.SetOrdered(ordered);
    publishInfo.SetSticky(sticky);
    if (!subscriberPermissions.empty()) {
        publishInfo.SetSubscriberPermissions(subscriberPermissions);
    }

    // Publish
    bool result = OHOS::EventFwk::CommonEventManager::PublishCommonEvent(eventData, publishInfo);
    if (!result) {
        LOGE("Publish failed for event=%s", event.c_str());
        return -1;
    }

    LOGI("Publish successful: event=%s", event.c_str());
    return 0;
}

int OHCommonEventClient::finishReceiver(int32_t subscriptionId, int32_t code,
                                         const std::string& data, bool abortEvent) {
    std::lock_guard<std::mutex> lock(mutex_);

    LOGI("FinishReceiver: subscriptionId=%d, code=%d, abort=%d", subscriptionId, code, abortEvent);

    auto it = asyncResults_.find(subscriptionId);
    if (it == asyncResults_.end()) {
        LOGW("FinishReceiver: no async result for subscriptionId=%d", subscriptionId);
        return -1;
    }

    auto asyncResult = it->second;
    asyncResults_.erase(it);

    if (!asyncResult) {
        LOGW("FinishReceiver: null async result for subscriptionId=%d", subscriptionId);
        return -1;
    }

    // Set results on the ordered event
    asyncResult->SetCode(code);
    asyncResult->SetData(data);
    if (abortEvent) {
        asyncResult->AbortCommonEvent();
    }

    // Signal completion to CES — this triggers delivery to the next subscriber
    bool result = asyncResult->FinishCommonEvent();
    if (!result) {
        LOGE("FinishReceiver failed for subscriptionId=%d", subscriptionId);
        return -1;
    }

    LOGI("FinishReceiver successful: subscriptionId=%d", subscriptionId);
    return 0;
}

std::string OHCommonEventClient::getStickyEvent(const std::string& event) {
    LOGI("GetStickyEvent: event=%s", event.c_str());

    OHOS::EventFwk::CommonEventData eventData;
    bool result = OHOS::EventFwk::CommonEventManager::GetStickyCommonEvent(event, eventData);
    if (!result) {
        return "";
    }

    // Extract data from sticky event and return as JSON
    auto want = eventData.GetWant();
    std::string extrasJson = want.GetStringParam("android_extras_json");
    std::string action = want.GetAction();
    std::string uri = want.GetUri().ToString();
    int32_t code = eventData.GetCode();
    std::string data = eventData.GetData();

    // Return a simple JSON object with event fields
    // Format: {"action":"...","extrasJson":"...","uri":"...","code":N,"data":"..."}
    std::string json = "{\"action\":\"" + action + "\"";
    if (!extrasJson.empty()) {
        json += ",\"extrasJson\":\"" + extrasJson + "\"";
    }
    if (!uri.empty()) {
        json += ",\"uri\":\"" + uri + "\"";
    }
    json += ",\"code\":" + std::to_string(code);
    if (!data.empty()) {
        json += ",\"data\":\"" + data + "\"";
    }
    json += "}";

    return json;
}

void OHCommonEventClient::storeAsyncResult(
        int32_t subscriptionId,
        std::shared_ptr<OHOS::EventFwk::AsyncCommonEventResult> result) {
    std::lock_guard<std::mutex> lock(mutex_);
    asyncResults_[subscriptionId] = result;
}

}  // namespace oh_adapter
