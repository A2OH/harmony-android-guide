/*
 * window_event_channel_adapter.h
 *
 * Reverse callback adapter: OH IWindowEventChannel -> Android InputChannel.
 *
 * V7 OH split the input-event channel out of IWindow into a dedicated
 * IWindowEventChannel interface. SceneSessionManager.CreateAndConnectSpecificSession
 * requires an IWindowEventChannel sptr alongside ISessionStage. Android's input
 * pipeline uses a separate InputChannel (kernel pipe + InputDispatcher), so the
 * 11 Transfer* methods here are no-op log endpoints — the actual key/pointer
 * events arrive on the Android side via OHInputBridge → InputChannel, not via
 * this stub. This adapter only exists so SSM has a non-null callback target it
 * can store and address.
 *
 * 2026-04-22: inheritance changed from OHOS::Rosen::WindowEventChannelStub
 * to OHOS::IRemoteStub<OHOS::Rosen::IWindowEventChannel>. Reason: libscene_session.z.so
 * uses explicit linker version-script (libscene_session.map) that does NOT
 * export WindowEventChannelStub's member functions (only vtable/VTT). Bridge.so
 * linked to the stub's OnRemoteRequest symbol at the vtable slot → runtime
 * dlopen UND at _ZN4OHOS5Rosen22WindowEventChannelStub15OnRemoteRequest...
 *
 * Solution (adapter-side, zero OH patch): inherit IRemoteStub<IWindowEventChannel>
 * directly — the template is header-only, locally instantiated in bridge.so
 * via libscene_session's public config includes. IWindowEventChannel is an
 * interface (pure-virtual), adapter overrides every Transfer* method anyway.
 * OnRemoteRequest dispatch logic is ported from OH window_event_channel_stub.cpp,
 * simplified to code-aware no-op replies matching IPC expected return types
 * (adapter never receives real IPC traffic, but replies must structurally
 * match so any caller that does probe doesn't see "transaction failed").
 *
 * See doc/build_patch_log.html AA.1/AA.6 + memory feedback_oh_internal_class_no_export.md
 * (Case H variant: provider vtable exported but member functions excluded
 * via .map script — adapter-side fix only).
 */
#ifndef OH_ADAPTER_WINDOW_EVENT_CHANNEL_ADAPTER_H
#define OH_ADAPTER_WINDOW_EVENT_CHANNEL_ADAPTER_H

#include <jni.h>
#include <iremote_stub.h>
#include <message_option.h>
#include <message_parcel.h>
#include "session/container/include/zidl/window_event_channel_interface.h"
#include "ws_common.h"

namespace oh_adapter {

class WindowEventChannelAdapter
    : public OHOS::IRemoteStub<OHOS::Rosen::IWindowEventChannel> {
public:
    explicit WindowEventChannelAdapter(JavaVM* jvm) : jvm_(jvm) {}
    ~WindowEventChannelAdapter() override = default;

    // IPC dispatch — ported from OH WindowEventChannelStub::OnRemoteRequest.
    // Routes incoming codes to Transfer* virtual methods (all no-op in this
    // adapter, see header block comment). Writes structurally correct replies
    // so that remote callers can deserialize without error, even though
    // adapter never actually handles real IPC traffic.
    int OnRemoteRequest(uint32_t code, OHOS::MessageParcel& data,
                        OHOS::MessageParcel& reply,
                        OHOS::MessageOption& option) override;

    // IWindowEventChannel — all 11 methods are no-op log (events route via
    // OHInputBridge + InputChannel on Android side).
    OHOS::Rosen::WSError TransferKeyEvent(
        const std::shared_ptr<OHOS::MMI::KeyEvent>& keyEvent) override;
    OHOS::Rosen::WSError TransferPointerEvent(
        const std::shared_ptr<OHOS::MMI::PointerEvent>& pointerEvent) override;
    OHOS::Rosen::WSError TransferBackpressedEventForConsumed(bool& isConsumed) override;
    OHOS::Rosen::WSError TransferKeyEventForConsumed(
        const std::shared_ptr<OHOS::MMI::KeyEvent>& keyEvent, bool& isConsumed,
        bool isPreImeEvent = false) override;
    OHOS::Rosen::WSError TransferKeyEventForConsumedAsync(
        const std::shared_ptr<OHOS::MMI::KeyEvent>& keyEvent, bool isPreImeEvent,
        const OHOS::sptr<OHOS::IRemoteObject>& listener) override;
    OHOS::Rosen::WSError TransferFocusActiveEvent(bool isFocusActive) override;
    OHOS::Rosen::WSError TransferFocusState(bool focusState) override;
    OHOS::Rosen::WSError TransferAccessibilityHoverEvent(
        float pointX, float pointY, int32_t sourceType, int32_t eventType,
        int64_t timeMs) override;
    OHOS::Rosen::WSError TransferAccessibilityChildTreeRegister(
        uint32_t windowId, int32_t treeId, int64_t accessibilityId) override;
    OHOS::Rosen::WSError TransferAccessibilityChildTreeUnregister() override;
    OHOS::Rosen::WSError TransferAccessibilityDumpChildInfo(
        const std::vector<std::string>& params, std::vector<std::string>& info) override;

private:
    JavaVM* jvm_;
};

}  // namespace oh_adapter

#endif  // OH_ADAPTER_WINDOW_EVENT_CHANNEL_ADAPTER_H
