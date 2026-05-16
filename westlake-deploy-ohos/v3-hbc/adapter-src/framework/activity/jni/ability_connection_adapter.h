/*
 * ability_connection_adapter.h
 *
 * OH IAbilityConnection stub that routes OnAbilityConnectDone /
 * OnAbilityDisconnectDone callbacks to the Java ServiceConnectionRegistry
 * via JNI.
 *
 * Each instance is associated with a connectionId assigned by the Java
 * ServiceConnectionRegistry. Multiple instances can exist concurrently,
 * one per active bindService call.
 */
#ifndef OH_ADAPTER_ABILITY_CONNECTION_ADAPTER_H
#define OH_ADAPTER_ABILITY_CONNECTION_ADAPTER_H

#include <jni.h>
#include "ability_connect_callback_stub.h"

namespace oh_adapter {

class AbilityConnectionAdapter : public OHOS::AAFwk::AbilityConnectionStub {
public:
    /**
     * @param jvm           JavaVM pointer for AttachCurrentThread on IPC threads.
     * @param connectionId  Local connection ID from ServiceConnectionRegistry.
     */
    AbilityConnectionAdapter(JavaVM* jvm, int connectionId);
    ~AbilityConnectionAdapter() override;

    /**
     * OH callback: service ability connected.
     * Routes to ServiceConnectionRegistry.onServiceConnected via JNI.
     */
    void OnAbilityConnectDone(const OHOS::AppExecFwk::ElementName& element,
                              const OHOS::sptr<OHOS::IRemoteObject>& remoteObject,
                              int resultCode) override;

    /**
     * OH callback: service ability disconnected.
     * Routes to ServiceConnectionRegistry.onServiceDisconnected via JNI.
     */
    void OnAbilityDisconnectDone(const OHOS::AppExecFwk::ElementName& element,
                                  int resultCode) override;

    int getConnectionId() const { return connectionId_; }

private:
    JavaVM* jvm_ = nullptr;
    int connectionId_ = -1;
};

}  // namespace oh_adapter

#endif  // OH_ADAPTER_ABILITY_CONNECTION_ADAPTER_H
