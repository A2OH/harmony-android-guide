cmake_minimum_required(VERSION 3.10)
project(oh_adapter_bridge)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# OpenHarmony IPC SDK include paths (configure based on actual OH SDK install path)
set(OH_SDK_ROOT "" CACHE PATH "OpenHarmony SDK root path")
set(OH_IPC_INCLUDE "${OH_SDK_ROOT}/ipc/interfaces/innerkits/ipc_core/include")
set(OH_SAMGR_INCLUDE "${OH_SDK_ROOT}/samgr/interfaces/innerkits/samgr_proxy/include")
set(OH_ABILITY_INCLUDE "${OH_SDK_ROOT}/ability/interfaces/inner_api")
set(OH_WMS_INCLUDE "${OH_SDK_ROOT}/wms")
set(OH_GRAPHIC_2D_INCLUDE "${OH_SDK_ROOT}/graphic_2d")
set(OH_GRAPHIC_SURFACE_INCLUDE "${OH_SDK_ROOT}/graphic_surface")
set(OH_CES_INCLUDE "${OH_SDK_ROOT}/ces/interfaces/inner_api")

# Source files — organized by feature
set(BRIDGE_SOURCES
    # core: JNI entry point, JavaVM management, callback dispatch
    core/jni/adapter_bridge.cpp
    core/jni/oh_callback_handler.cpp

    # activity: AMS/ATMS bridges, lifecycle, Intent/Want conversion
    activity/jni/oh_ability_manager_client.cpp
    activity/jni/oh_app_mgr_client.cpp
    activity/jni/app_scheduler_adapter.cpp
    activity/jni/ability_scheduler_adapter.cpp
    activity/jni/ability_connection_adapter.cpp
    activity/jni/intent_want_converter.cpp
    activity/jni/lifecycle_state_mapper.cpp

    # window: WMS/WindowSession bridges, input event bridging
    window/jni/oh_window_manager_client.cpp
    window/jni/oh_input_bridge.cpp
    window/jni/window_callback_adapter.cpp
    window/jni/session_stage_adapter.cpp
    window/jni/window_manager_agent_adapter.cpp

    # surface: RSSurfaceNode + IGraphicBufferProducer bridging
    surface/jni/oh_surface_bridge.cpp
    surface/jni/oh_graphic_buffer_producer.cpp

    # broadcast: CommonEvent / BroadcastReceiver bridging
    broadcast/jni/oh_common_event_client.cpp
    broadcast/jni/common_event_subscriber_adapter.cpp

    # contentprovider: DataShare / ContentProvider bridging
    contentprovider/jni/oh_datashare_client.cpp

    # package-manager: BMS client (PackageManager queries)
    package-manager/jni/oh_bundle_mgr_client.cpp
)

# Shared library
add_library(oh_adapter_bridge SHARED ${BRIDGE_SOURCES})

# Include directories — each feature's jni/ dir
target_include_directories(oh_adapter_bridge PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/core/jni
    ${CMAKE_CURRENT_SOURCE_DIR}/activity/jni
    ${CMAKE_CURRENT_SOURCE_DIR}/window/jni
    ${CMAKE_CURRENT_SOURCE_DIR}/surface/jni
    ${CMAKE_CURRENT_SOURCE_DIR}/broadcast/jni
    ${CMAKE_CURRENT_SOURCE_DIR}/contentprovider/jni
    ${CMAKE_CURRENT_SOURCE_DIR}/package-manager/jni
    # OH SDK
    ${OH_IPC_INCLUDE}
    ${OH_SAMGR_INCLUDE}
    ${OH_ABILITY_INCLUDE}
    ${OH_WMS_INCLUDE}
    # OH RenderService client (RSSurfaceNode, RSUIDirector, RSTransaction)
    ${OH_GRAPHIC_2D_INCLUDE}/rosen/modules/render_service_client/core
    ${OH_GRAPHIC_2D_INCLUDE}/rosen/modules/render_service_base/include
    # OH Surface / BufferQueue / SyncFence
    ${OH_GRAPHIC_SURFACE_INCLUDE}/surface/include
    ${OH_GRAPHIC_SURFACE_INCLUDE}/interfaces/inner_api/common
    ${OH_GRAPHIC_SURFACE_INCLUDE}/interfaces/inner_api/surface
    # OH CommonEventService (CES)
    ${OH_CES_INCLUDE}
    ${OH_CES_INCLUDE}/..    # for frameworks/core/include
    # OH DataShare (ContentProvider bridge)
    ${OH_SDK_ROOT}/distributeddatamgr/data_share/interfaces/inner_api/common/include
    ${OH_SDK_ROOT}/distributeddatamgr/relational_store/interfaces/inner_api/rdb/include
    # OH BMS (PackageManager bridge)
    ${OH_SDK_ROOT}/bundlemanager/bundle_framework/interfaces/inner_api/appexecfwk_base/include
)

# Link Android NDK log library
find_library(log-lib log)
target_link_libraries(oh_adapter_bridge ${log-lib})

# Link OH IPC and graphic libraries (must be provided at actual build time)
# target_link_libraries(oh_adapter_bridge
#     ipc_core samgr_proxy
#     render_service_client    # RSSurfaceNode, RSUIDirector, RSTransaction
#     surface                  # Surface, SurfaceBuffer, BufferQueue
#     sync_fence               # SyncFence
#     common_event_framework   # CommonEventManager, CommonEventSubscriber
#     appexecfwk_core          # BMS client
# )
