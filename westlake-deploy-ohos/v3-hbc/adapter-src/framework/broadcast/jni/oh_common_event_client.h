/*
 * oh_common_event_client.h
 *
 * OpenHarmony CommonEventService IPC client.
 * Manages event subscriptions and publishing through the OH CES framework.
 *
 * Reference paths:
 *   OH: ces/interfaces/inner_api/common_event_manager.h
 *   OH: ces/interfaces/inner_api/common_event_subscriber.h
 *   OH: ces/interfaces/inner_api/common_event_data.h
 */
#ifndef OH_COMMON_EVENT_CLIENT_H
#define OH_COMMON_EVENT_CLIENT_H

#include <string>
#include <vector>
#include <unordered_map>
#include <mutex>
#include <memory>

// Forward declarations for OH CES types (provided by OH SDK at build time)
namespace OHOS {
namespace EventFwk {
    class CommonEventSubscriber;
    class CommonEventData;
    class AsyncCommonEventResult;
}  // namespace EventFwk
}  // namespace OHOS

namespace oh_adapter {

// Forward declaration
class AdapterEventSubscriber;

/**
 * Client for OH CommonEventService.
 * Manages subscriptions (subscribe/unsubscribe) and event publishing.
 */
class OHCommonEventClient {
public:
    static OHCommonEventClient& getInstance();

    /**
     * Subscribe to OH CommonEvents.
     * Creates an AdapterEventSubscriber and registers with CES.
     * @param subscriptionId Adapter-assigned ID for this subscription
     * @param events OH event names to subscribe to
     * @param priority Subscriber priority (for ordered events)
     * @param permission Required publisher permission (empty = none)
     * @return 0 on success, non-zero error code
     */
    int subscribe(int32_t subscriptionId, const std::vector<std::string>& events,
                  int32_t priority, const std::string& permission);

    /**
     * Unsubscribe from OH CommonEvents.
     * @param subscriptionId The subscription to remove
     * @return 0 on success
     */
    int unsubscribe(int32_t subscriptionId);

    /**
     * Publish an OH CommonEvent.
     * @param event OH event name
     * @param extrasJson Intent extras serialized as JSON
     * @param uri Data URI (empty = none)
     * @param code Result code
     * @param data Result data string
     * @param ordered true for ordered (sequential) delivery
     * @param sticky true for sticky event
     * @param subscriberPermissions Required permissions for subscribers
     * @return 0 on success
     */
    int publish(const std::string& event, const std::string& extrasJson,
                const std::string& uri, int32_t code, const std::string& data,
                bool ordered, bool sticky,
                const std::vector<std::string>& subscriberPermissions);

    /**
     * Finish processing an ordered event.
     * Called when Android BroadcastReceiver completes (finishReceiver).
     * @param subscriptionId Subscription that finished processing
     * @param code Result code to pass to next receiver
     * @param data Result data to pass to next receiver
     * @param abortEvent true to abort delivery chain
     * @return 0 on success
     */
    int finishReceiver(int32_t subscriptionId, int32_t code,
                       const std::string& data, bool abortEvent);

    /**
     * Query a sticky CommonEvent.
     * @param event OH event name
     * @return JSON string with event data, or empty if no sticky event
     */
    std::string getStickyEvent(const std::string& event);

    /**
     * Store an AsyncCommonEventResult for a subscription (called by AdapterEventSubscriber).
     */
    void storeAsyncResult(int32_t subscriptionId,
                          std::shared_ptr<OHOS::EventFwk::AsyncCommonEventResult> result);

private:
    OHCommonEventClient() = default;
    ~OHCommonEventClient() = default;

    std::mutex mutex_;
    // subscriptionId -> subscriber instance
    std::unordered_map<int32_t, std::shared_ptr<AdapterEventSubscriber>> subscribers_;
    // subscriptionId -> async result (for ordered events pending finishReceiver)
    std::unordered_map<int32_t, std::shared_ptr<OHOS::EventFwk::AsyncCommonEventResult>> asyncResults_;
};

}  // namespace oh_adapter

#endif  // OH_COMMON_EVENT_CLIENT_H
