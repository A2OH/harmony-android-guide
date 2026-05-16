/*
 * window_event_channel_adapter.cpp
 *
 * No-op IWindowEventChannel stub. See header for rationale.
 */
#include "window_event_channel_adapter.h"
#include "session/container/include/zidl/window_event_ipc_interface_code.h"

#include <android/log.h>
#include <ipc_types.h>

#define LOG_TAG "OH_WindowEvtChan"
#define ALOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define ALOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

namespace oh_adapter {

using OHOS::Rosen::WSError;
using Code = OHOS::Rosen::WindowEventInterfaceCode;

// OnRemoteRequest ported from OH WindowEventChannelStub::OnRemoteRequest
// (wms/window_scene/session/container/src/zidl/window_event_channel_stub.cpp).
// Simplified: adapter never receives real IPC traffic (events route via
// OHInputBridge+InputChannel Android-side), so Handle* methods skip full
// parcel deserialization and write structurally-correct reply that matches
// what the remote proxy expects to read.
//
// If a real IPC request does arrive, it is a bug — log WARN and still return
// proper reply so caller doesn't hang.
int WindowEventChannelAdapter::OnRemoteRequest(
    uint32_t code, OHOS::MessageParcel& data,
    OHOS::MessageParcel& reply, OHOS::MessageOption& option)
{
    ALOGW("[OH_ONLY] OnRemoteRequest code=%u — adapter is callback-target-only, replying no-op success", code);

    if (data.ReadInterfaceToken() != GetDescriptor()) {
        ALOGW("InterfaceToken mismatch");
        return OHOS::ERR_TRANSACTION_FAILED;
    }

    switch (code) {
        case static_cast<uint32_t>(Code::TRANS_ID_TRANSFER_KEY_EVENT): {
            // OH parses MMI::KeyEvent from parcel then calls
            // TransferKeyEventForConsumed(keyEvent, isConsumed, isPreImeEvent).
            // Adapter skips the parcel parse (MMI KeyEvent::ReadFromParcel not
            // in our link set) — Transfer* overrides are no-op anyway, so the
            // result is the same: isConsumed=false + WS_OK.
            reply.WriteBool(false);
            reply.WriteInt32(static_cast<int32_t>(WSError::WS_OK));
            return OHOS::ERR_NONE;
        }
        case static_cast<uint32_t>(Code::TRANS_ID_TRANSFER_KEY_EVENT_ASYNC): {
            // OH: WSError TransferKeyEventForConsumedAsync; Reply: WriteUint32(errCode)
            reply.WriteUint32(static_cast<uint32_t>(WSError::WS_OK));
            return OHOS::ERR_NONE;
        }
        case static_cast<uint32_t>(Code::TRANS_ID_TRANSFER_POINTER_EVENT): {
            // OH: WSError TransferPointerEvent; Reply: WriteInt32(errCode)
            reply.WriteInt32(static_cast<int32_t>(WSError::WS_OK));
            return OHOS::ERR_NONE;
        }
        case static_cast<uint32_t>(Code::TRANS_ID_TRANSFER_FOCUS_ACTIVE_EVENT): {
            bool isFocusActive = data.ReadBool();
            WSError errCode = TransferFocusActiveEvent(isFocusActive);
            reply.WriteInt32(static_cast<int32_t>(errCode));
            return OHOS::ERR_NONE;
        }
        case static_cast<uint32_t>(Code::TRANS_ID_TRANSFER_FOCUS_STATE_EVENT): {
            bool focusState = data.ReadBool();
            WSError errCode = TransferFocusState(focusState);
            reply.WriteInt32(static_cast<int32_t>(errCode));
            return OHOS::ERR_NONE;
        }
        case static_cast<uint32_t>(Code::TRANS_ID_TRANSFER_BACKPRESSED_EVENT): {
            bool isConsumed = false;
            WSError errCode = TransferBackpressedEventForConsumed(isConsumed);
            reply.WriteBool(isConsumed);
            reply.WriteInt32(static_cast<int32_t>(errCode));
            return OHOS::ERR_NONE;
        }
        case static_cast<uint32_t>(Code::TRANS_ID_TRANSFER_ACCESSIBILITY_HOVER_EVENT): {
            float pointX = 0.0f;
            float pointY = 0.0f;
            int32_t sourceType = 0;
            int32_t eventType = 0;
            int64_t timeMs = 0;
            data.ReadFloat(pointX);
            data.ReadFloat(pointY);
            data.ReadInt32(sourceType);
            data.ReadInt32(eventType);
            data.ReadInt64(timeMs);
            WSError errCode = TransferAccessibilityHoverEvent(pointX, pointY, sourceType, eventType, timeMs);
            reply.WriteInt32(static_cast<int32_t>(errCode));
            return OHOS::ERR_NONE;
        }
        case static_cast<uint32_t>(Code::TRANS_ID_TRANSFER_ACCESSIBILITY_CHILD_TREE_REGISTER): {
            uint32_t windowId = 0;
            int32_t treeId = -1;
            int64_t accessibilityId = -1;
            data.ReadUint32(windowId);
            data.ReadInt32(treeId);
            data.ReadInt64(accessibilityId);
            WSError errCode = TransferAccessibilityChildTreeRegister(windowId, treeId, accessibilityId);
            reply.WriteInt32(static_cast<int32_t>(errCode));
            return OHOS::ERR_NONE;
        }
        case static_cast<uint32_t>(Code::TRANS_ID_TRANSFER_ACCESSIBILITY_CHILD_TREE_UNREGISTER): {
            WSError errCode = TransferAccessibilityChildTreeUnregister();
            reply.WriteInt32(static_cast<int32_t>(errCode));
            return OHOS::ERR_NONE;
        }
        case static_cast<uint32_t>(Code::TRANS_ID_TRANSFER_ACCESSIBILITY_DUMP_CHILD_INFO): {
            std::vector<std::string> params;
            data.ReadStringVector(&params);
            std::vector<std::string> info;
            TransferAccessibilityDumpChildInfo(params, info);
            reply.WriteStringVector(info);
            return OHOS::ERR_NONE;
        }
        default:
            // Unknown code — delegate to IPCObjectStub default handler
            return OHOS::IPCObjectStub::OnRemoteRequest(code, data, reply, option);
    }
}

WSError WindowEventChannelAdapter::TransferKeyEvent(
    const std::shared_ptr<OHOS::MMI::KeyEvent>& /*keyEvent*/)
{
    ALOGI("[OH_ONLY] TransferKeyEvent — Android receives via InputChannel, ignoring");
    return WSError::WS_OK;
}

WSError WindowEventChannelAdapter::TransferPointerEvent(
    const std::shared_ptr<OHOS::MMI::PointerEvent>& /*pointerEvent*/)
{
    ALOGI("[OH_ONLY] TransferPointerEvent — Android receives via InputChannel, ignoring");
    return WSError::WS_OK;
}

WSError WindowEventChannelAdapter::TransferBackpressedEventForConsumed(bool& isConsumed)
{
    isConsumed = false;
    ALOGI("[OH_ONLY] TransferBackpressedEventForConsumed → not consumed");
    return WSError::WS_OK;
}

WSError WindowEventChannelAdapter::TransferFocusActiveEvent(bool isFocusActive)
{
    ALOGI("[OH_ONLY] TransferFocusActiveEvent active=%d", isFocusActive ? 1 : 0);
    return WSError::WS_OK;
}

WSError WindowEventChannelAdapter::TransferFocusState(bool focusState)
{
    ALOGI("[OH_ONLY] TransferFocusState state=%d", focusState ? 1 : 0);
    return WSError::WS_OK;
}

WSError WindowEventChannelAdapter::TransferKeyEventForConsumed(
    const std::shared_ptr<OHOS::MMI::KeyEvent>& /*keyEvent*/, bool& isConsumed, bool /*isPreIme*/)
{
    isConsumed = false;
    ALOGI("[OH_ONLY] TransferKeyEventForConsumed → not consumed");
    return WSError::WS_OK;
}

WSError WindowEventChannelAdapter::TransferKeyEventForConsumedAsync(
    const std::shared_ptr<OHOS::MMI::KeyEvent>& /*keyEvent*/, bool /*isPreIme*/,
    const OHOS::sptr<OHOS::IRemoteObject>& /*listener*/)
{
    ALOGI("[OH_ONLY] TransferKeyEventForConsumedAsync");
    return WSError::WS_OK;
}

WSError WindowEventChannelAdapter::TransferAccessibilityHoverEvent(
    float /*pointX*/, float /*pointY*/, int32_t /*sourceType*/, int32_t /*eventType*/,
    int64_t /*timeMs*/)
{
    ALOGI("[OH_ONLY] TransferAccessibilityHoverEvent");
    return WSError::WS_OK;
}

WSError WindowEventChannelAdapter::TransferAccessibilityChildTreeRegister(
    uint32_t /*windowId*/, int32_t /*treeId*/, int64_t /*accessibilityId*/)
{
    ALOGI("[OH_ONLY] TransferAccessibilityChildTreeRegister");
    return WSError::WS_OK;
}

WSError WindowEventChannelAdapter::TransferAccessibilityChildTreeUnregister()
{
    ALOGI("[OH_ONLY] TransferAccessibilityChildTreeUnregister");
    return WSError::WS_OK;
}

WSError WindowEventChannelAdapter::TransferAccessibilityDumpChildInfo(
    const std::vector<std::string>& /*params*/, std::vector<std::string>& /*info*/)
{
    ALOGI("[OH_ONLY] TransferAccessibilityDumpChildInfo");
    return WSError::WS_OK;
}

}  // namespace oh_adapter
