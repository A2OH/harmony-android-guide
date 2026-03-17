/*
 * ArkUI Native Bridge for Dalvik — vtable-style wrapper around OH_ArkUI_GetNativeAPI.
 *
 * Links against libace_ndk.z.so via dlopen.
 * Gets ArkUI_NativeNodeAPI_1 vtable, then calls createNode/setAttribute/addChild.
 */
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>
#include <stdint.h>

/* Forward declarations matching ArkUI Node-API C interface */
typedef void* ArkUI_NodeHandle;
typedef union { float f32; int32_t i32; uint32_t u32; } ArkUI_NumberValue;
typedef struct {
    const ArkUI_NumberValue* value;
    int32_t size;
    const char* string;
    void* object;
} ArkUI_AttributeItem;
typedef struct { int32_t version; } ArkUI_AnyNativeAPI;
typedef enum { ARKUI_NATIVE_NODE = 0 } ArkUI_NativeAPIVariantKind;

/* The vtable struct returned by OH_ArkUI_GetNativeAPI(ARKUI_NATIVE_NODE, 1) */
typedef struct {
    int32_t version;
    ArkUI_NodeHandle (*createNode)(int32_t type);
    void (*disposeNode)(ArkUI_NodeHandle node);
    int32_t (*addChild)(ArkUI_NodeHandle parent, ArkUI_NodeHandle child);
    int32_t (*removeChild)(ArkUI_NodeHandle parent, ArkUI_NodeHandle child);
    int32_t (*insertChildAt)(ArkUI_NodeHandle parent, ArkUI_NodeHandle child, int32_t pos);
    int32_t (*setAttribute)(ArkUI_NodeHandle node, int32_t attr, const ArkUI_AttributeItem* item);
    const ArkUI_AttributeItem* (*getAttribute)(ArkUI_NodeHandle node, int32_t attr);
    int32_t (*resetAttribute)(ArkUI_NodeHandle node, int32_t attr);
    int32_t (*registerNodeEvent)(ArkUI_NodeHandle node, int32_t eventType, int32_t targetId);
    void (*unregisterNodeEvent)(ArkUI_NodeHandle node, int32_t eventType);
} ArkUI_NativeNodeAPI_1;

typedef ArkUI_AnyNativeAPI* (*GetNativeAPI_fn)(ArkUI_NativeAPIVariantKind, int32_t);

static ArkUI_NativeNodeAPI_1* g_api = NULL;
static int g_initDone = 0;

static int initAPI() {
    if (g_initDone) return g_api ? 0 : -1;
    g_initDone = 1;

    void* lib = dlopen("libace_ndk.z.so", RTLD_NOW);
    if (!lib) lib = dlopen("/system/lib/libace_ndk.z.so", RTLD_NOW);
    if (!lib) lib = dlopen("/data/a2oh/libace_ndk.z.so", RTLD_NOW);
    if (!lib) {
        fprintf(stderr, "[ArkUI] dlopen failed: %s\n", dlerror());
        return -1;
    }
    GetNativeAPI_fn getAPI = (GetNativeAPI_fn) dlsym(lib, "OH_ArkUI_GetNativeAPI");
    if (!getAPI) {
        fprintf(stderr, "[ArkUI] OH_ArkUI_GetNativeAPI not found\n");
        return -1;
    }
    ArkUI_AnyNativeAPI* any = getAPI(ARKUI_NATIVE_NODE, 1);
    if (!any || any->version < 1) {
        fprintf(stderr, "[ArkUI] GetNativeAPI returned null or bad version\n");
        return -1;
    }
    g_api = (ArkUI_NativeNodeAPI_1*) any;
    fprintf(stderr, "[ArkUI] Loaded API version %d\n", g_api->version);
    return 0;
}

extern "C" {

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeCreate(
    JNIEnv*, jclass, jint type) {
    if (initAPI() < 0 || !g_api->createNode) return 0;
    return (jlong)(uintptr_t) g_api->createNode(type);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeDispose(
    JNIEnv*, jclass, jlong h) {
    if (!g_api || !g_api->disposeNode || !h) return;
    g_api->disposeNode((ArkUI_NodeHandle)(uintptr_t)h);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeAddChild(
    JNIEnv*, jclass, jlong p, jlong c) {
    if (!g_api || !g_api->addChild || !p || !c) return;
    g_api->addChild((ArkUI_NodeHandle)(uintptr_t)p, (ArkUI_NodeHandle)(uintptr_t)c);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeRemoveChild(
    JNIEnv*, jclass, jlong p, jlong c) {
    if (!g_api || !g_api->removeChild || !p || !c) return;
    g_api->removeChild((ArkUI_NodeHandle)(uintptr_t)p, (ArkUI_NodeHandle)(uintptr_t)c);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeInsertChildAt(
    JNIEnv*, jclass, jlong p, jlong c, jint pos) {
    if (!g_api || !g_api->insertChildAt || !p || !c) return;
    g_api->insertChildAt((ArkUI_NodeHandle)(uintptr_t)p, (ArkUI_NodeHandle)(uintptr_t)c, pos);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrString(
    JNIEnv* env, jclass, jlong h, jint attr, jstring jval) {
    if (!g_api || !g_api->setAttribute || !h) return;
    const char* val = env->GetStringUTFChars(jval, NULL);
    ArkUI_AttributeItem item = {}; item.string = val;
    g_api->setAttribute((ArkUI_NodeHandle)(uintptr_t)h, attr, &item);
    env->ReleaseStringUTFChars(jval, val);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrInt(
    JNIEnv*, jclass, jlong h, jint attr, jint val) {
    if (!g_api || !g_api->setAttribute || !h) return;
    ArkUI_NumberValue nv; nv.i32 = val;
    ArkUI_AttributeItem item = {}; item.value = &nv; item.size = 1;
    g_api->setAttribute((ArkUI_NodeHandle)(uintptr_t)h, attr, &item);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrFloat(
    JNIEnv*, jclass, jlong h, jint attr, jfloat val) {
    if (!g_api || !g_api->setAttribute || !h) return;
    ArkUI_NumberValue nv; nv.f32 = val;
    ArkUI_AttributeItem item = {}; item.value = &nv; item.size = 1;
    g_api->setAttribute((ArkUI_NodeHandle)(uintptr_t)h, attr, &item);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrColor(
    JNIEnv*, jclass, jlong h, jint attr, jint argb) {
    if (!g_api || !g_api->setAttribute || !h) return;
    ArkUI_NumberValue nv; nv.u32 = (uint32_t)argb;
    ArkUI_AttributeItem item = {}; item.value = &nv; item.size = 1;
    g_api->setAttribute((ArkUI_NodeHandle)(uintptr_t)h, attr, &item);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeRegisterEvent(
    JNIEnv*, jclass, jlong h, jint eventType, jint targetId) {
    if (!g_api || !g_api->registerNodeEvent || !h) return;
    g_api->registerNodeEvent((ArkUI_NodeHandle)(uintptr_t)h, eventType, targetId);
}

} /* extern "C" */
