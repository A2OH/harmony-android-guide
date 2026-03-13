/**
 * cpp_shim.cpp — Thin C wrapper around OH C++ inner APIs.
 *
 * Links against:
 * - libpreferences.z.so     (OHOS::NativePreferences)
 * - libans_innerkits.z.so   (OHOS::Notification + ReminderHelper)
 * - libability_runtime.z.so (OHOS::AbilityRuntime)
 * - libwant.z.so            (AAFwk::Want)
 * - libcurl.z.so            (HTTP client)
 * - libace_napi.z.so        (NAPI for ArkTS calls)
 *
 * Build:
 *   clang++ -shared -fPIC -o liboh_cpp_shim.so cpp_shim.cpp \
 *     -I${OH}/foundation/distributeddatamgr/preferences/interfaces/inner_api/include \
 *     -I${OH}/base/notification/distributed_notification_service/interfaces/inner_api \
 *     -I${OH}/foundation/ability/ability_runtime/interfaces/kits/native/ability/ability_runtime \
 *     -I${OH}/foundation/ability/ability_base/interfaces/kits/native/want/include \
 *     -I${OH}/third_party/curl/include \
 *     -I${OH}/interface/sdk_c/arkui/napi \
 *     -lpreferences -lans_innerkits -lability_runtime -lwant -lcurl -lace_napi
 */

#include "cpp_shim.h"

#include <string>
#include <memory>
#include <cstring>
#include <mutex>

// ── OH inner API headers ──
#include "preferences_helper.h"
#include "preferences.h"
#include "notification_helper.h"
#include "notification_request.h"
#include "notification_slot.h"
#include "notification_content.h"
#include "reminder_helper.h"
#include "reminder_request_timer.h"
#include "want.h"
#include "ability_context.h"

// ── libcurl for HTTP ──
#include "curl/curl.h"

// ── NAPI for calling ArkTS (Toast) ──
#include "napi/native_api.h"

using namespace OHOS::NativePreferences;
using namespace OHOS::Notification;

// ═══════════════════════════════════════════════════════════════════
// Preferences (unchanged from before)
// ═══════════════════════════════════════════════════════════════════

struct PrefsHandle {
    std::shared_ptr<Preferences> prefs;
};

extern "C" ShimPreferences shim_preferences_open(const char* file_path) {
    Options options(file_path);
    int errCode = 0;
    auto prefs = PreferencesHelper::GetPreferences(options, errCode);
    if (errCode != 0 || !prefs) return nullptr;
    return static_cast<ShimPreferences>(new PrefsHandle{prefs});
}

extern "C" void shim_preferences_close(ShimPreferences p) {
    delete static_cast<PrefsHandle*>(p);
}

static Preferences* get_prefs(ShimPreferences p) {
    if (!p) return nullptr;
    return static_cast<PrefsHandle*>(p)->prefs.get();
}

extern "C" int shim_preferences_get_string(ShimPreferences p, const char* key,
                                            const char* def, char* out_buf, int buf_len) {
    auto* prefs = get_prefs(p);
    if (!prefs) return -1;
    std::string val = prefs->GetString(key, def ? def : "");
    int copy_len = std::min((int)val.size(), buf_len - 1);
    std::memcpy(out_buf, val.c_str(), copy_len);
    out_buf[copy_len] = '\0';
    return copy_len;
}

extern "C" int shim_preferences_get_int(ShimPreferences p, const char* key, int def) {
    auto* prefs = get_prefs(p);
    return prefs ? prefs->GetInt(key, def) : def;
}

extern "C" long long shim_preferences_get_long(ShimPreferences p, const char* key, long long def) {
    auto* prefs = get_prefs(p);
    return prefs ? prefs->GetLong(key, def) : def;
}

extern "C" float shim_preferences_get_float(ShimPreferences p, const char* key, float def) {
    auto* prefs = get_prefs(p);
    return prefs ? prefs->GetFloat(key, def) : def;
}

extern "C" int shim_preferences_get_bool(ShimPreferences p, const char* key, int def) {
    auto* prefs = get_prefs(p);
    return prefs ? (prefs->GetBool(key, def != 0) ? 1 : 0) : def;
}

extern "C" int shim_preferences_put_string(ShimPreferences p, const char* key, const char* value) {
    auto* prefs = get_prefs(p);
    return prefs ? prefs->PutString(key, value ? value : "") : -1;
}

extern "C" int shim_preferences_put_int(ShimPreferences p, const char* key, int value) {
    auto* prefs = get_prefs(p);
    return prefs ? prefs->PutInt(key, value) : -1;
}

extern "C" int shim_preferences_put_long(ShimPreferences p, const char* key, long long value) {
    auto* prefs = get_prefs(p);
    return prefs ? prefs->PutLong(key, value) : -1;
}

extern "C" int shim_preferences_put_float(ShimPreferences p, const char* key, float value) {
    auto* prefs = get_prefs(p);
    return prefs ? prefs->PutFloat(key, value) : -1;
}

extern "C" int shim_preferences_put_bool(ShimPreferences p, const char* key, int value) {
    auto* prefs = get_prefs(p);
    return prefs ? prefs->PutBool(key, value != 0) : -1;
}

extern "C" int shim_preferences_delete(ShimPreferences p, const char* key) {
    auto* prefs = get_prefs(p);
    return prefs ? prefs->Delete(key) : -1;
}

extern "C" int shim_preferences_clear(ShimPreferences p) {
    auto* prefs = get_prefs(p);
    return prefs ? prefs->Clear() : -1;
}

extern "C" int shim_preferences_flush(ShimPreferences p) {
    auto* prefs = get_prefs(p);
    if (!prefs) return -1;
    return prefs->FlushSync();
}

// ═══════════════════════════════════════════════════════════════════
// Notification
// ═══════════════════════════════════════════════════════════════════

extern "C" int shim_notification_add_slot(int slot_type) {
    auto slotType = static_cast<NotificationConstant::SlotType>(slot_type);
    return static_cast<int>(NotificationHelper::AddSlotByType(slotType));
}

extern "C" int shim_notification_publish(int id, const char* title, const char* text, int slot_type) {
    NotificationRequest request;
    request.SetNotificationId(id);
    request.SetSlotType(static_cast<NotificationConstant::SlotType>(slot_type));

    auto normalContent = std::make_shared<NotificationNormalContent>();
    normalContent->SetTitle(title ? title : "");
    normalContent->SetText(text ? text : "");

    auto content = std::make_shared<NotificationContent>(normalContent);
    request.SetContent(content);

    return static_cast<int>(NotificationHelper::PublishNotification(request));
}

extern "C" int shim_notification_cancel(int id) {
    return static_cast<int>(NotificationHelper::CancelNotification(id));
}

// ═══════════════════════════════════════════════════════════════════
// Reminder — ReminderRequestTimer via ReminderHelper
// Maps: AlarmManager.setExact() → ReminderHelper::PublishReminder()
// ═══════════════════════════════════════════════════════════════════

extern "C" int shim_reminder_publish_timer(int delay_seconds, const char* title,
                                            const char* content, const char* bundle_name,
                                            const char* ability_name) {
    if (delay_seconds <= 0) return -1;

    // Construct a timer-based reminder
    ReminderRequestTimer reminder(static_cast<uint64_t>(delay_seconds));
    reminder.SetTitle(title ? title : "");
    reminder.SetContent(content ? content : "");

    // Set the click action — opens the specified ability when tapped
    auto wantInfo = std::make_shared<ReminderRequest::WantAgentInfo>();
    wantInfo->pkgName = bundle_name ? bundle_name : "";
    wantInfo->abilityName = ability_name ? ability_name : "";
    reminder.SetWantAgentInfo(wantInfo);

    // Use CONTENT_INFORMATION slot type by default
    reminder.SetSlotType(NotificationConstant::SlotType::CONTENT_INFORMATION);

    auto err = ReminderHelper::PublishReminder(reminder);
    if (err != 0) return -1;

    // PublishReminder sets the reminder ID on success
    return reminder.GetReminderId();
}

extern "C" int shim_reminder_cancel(int reminder_id) {
    return static_cast<int>(ReminderHelper::CancelReminder(
        static_cast<int32_t>(reminder_id)));
}

// ═══════════════════════════════════════════════════════════════════
// Toast — via NAPI call to @ohos.promptAction.showToast
//
// Since promptAction is ArkTS-only, we call it through NAPI.
// Requires a valid napi_env from the runtime context.
// ═══════════════════════════════════════════════════════════════════

// Global napi_env — set by the shim runtime initializer
static napi_env g_napi_env = nullptr;
static std::mutex g_napi_mutex;

// Called once at startup by the shim runtime to provide the NAPI env
extern "C" void shim_set_napi_env(napi_env env) {
    std::lock_guard<std::mutex> lock(g_napi_mutex);
    g_napi_env = env;
}

extern "C" int shim_show_toast(const char* message, int duration) {
    std::lock_guard<std::mutex> lock(g_napi_mutex);
    if (!g_napi_env || !message) return -1;

    napi_env env = g_napi_env;
    napi_value module = nullptr;

    // Load @ohos.promptAction module
    napi_status status = napi_load_module(env, "@ohos.promptAction", &module);
    if (status != napi_ok || !module) return -1;

    // Get showToast function
    napi_value showToastFn = nullptr;
    status = napi_get_named_property(env, module, "showToast", &showToastFn);
    if (status != napi_ok || !showToastFn) return -1;

    // Build options object: { message: "...", duration: N }
    napi_value options = nullptr;
    napi_create_object(env, &options);

    napi_value msgVal = nullptr;
    napi_create_string_utf8(env, message, strlen(message), &msgVal);
    napi_set_named_property(env, options, "message", msgVal);

    napi_value durVal = nullptr;
    napi_create_int32(env, duration, &durVal);
    napi_set_named_property(env, options, "duration", durVal);

    // Call showToast(options)
    napi_value global = nullptr;
    napi_get_global(env, &global);

    napi_value result = nullptr;
    status = napi_call_function(env, global, showToastFn, 1, &options, &result);
    return (status == napi_ok) ? 0 : -1;
}

// ═══════════════════════════════════════════════════════════════════
// Ability Navigation — Want + AbilityContext
//
// Requires a reference to the current AbilityContext, provided by
// the shim runtime at startup.
// ═══════════════════════════════════════════════════════════════════

// Global ability context — set by the shim runtime
static std::shared_ptr<OHOS::AbilityRuntime::AbilityContext> g_ability_context;
static std::mutex g_ctx_mutex;

extern "C" void shim_set_ability_context(void* context) {
    std::lock_guard<std::mutex> lock(g_ctx_mutex);
    // The runtime passes a raw pointer to the shared_ptr wrapper
    if (context) {
        g_ability_context = *static_cast<std::shared_ptr<OHOS::AbilityRuntime::AbilityContext>*>(context);
    }
}

extern "C" int shim_start_ability(const char* bundle_name, const char* ability_name,
                                   const char* params_json) {
    std::lock_guard<std::mutex> lock(g_ctx_mutex);
    if (!g_ability_context || !bundle_name || !ability_name) return -1;

    AAFwk::Want want;
    want.SetElementName(std::string(bundle_name), std::string(ability_name));

    // Parse simple JSON params into Want parameters
    // Format: {"key":"value", "key2":123}
    if (params_json && params_json[0] == '{') {
        std::string json(params_json);
        // Simple parser for flat key:value pairs
        // Handles "key":"string_value" and "key":number patterns
        size_t pos = 1;
        while (pos < json.size()) {
            // Find key
            size_t kstart = json.find('"', pos);
            if (kstart == std::string::npos) break;
            size_t kend = json.find('"', kstart + 1);
            if (kend == std::string::npos) break;
            std::string key = json.substr(kstart + 1, kend - kstart - 1);

            // Skip to value (past ':')
            size_t colon = json.find(':', kend);
            if (colon == std::string::npos) break;
            size_t vstart = colon + 1;
            while (vstart < json.size() && json[vstart] == ' ') vstart++;

            if (vstart < json.size() && json[vstart] == '"') {
                // String value
                size_t vend = json.find('"', vstart + 1);
                if (vend == std::string::npos) break;
                want.SetParam(key, std::string(json.substr(vstart + 1, vend - vstart - 1)));
                pos = vend + 1;
            } else {
                // Number value
                size_t vend = json.find_first_of(",}", vstart);
                if (vend == std::string::npos) vend = json.size();
                std::string numstr = json.substr(vstart, vend - vstart);
                // Trim whitespace
                while (!numstr.empty() && numstr.back() == ' ') numstr.pop_back();
                if (numstr.find('.') != std::string::npos) {
                    want.SetParam(key, std::stod(numstr));
                } else {
                    want.SetParam(key, std::stoi(numstr));
                }
                pos = vend;
            }
            // Skip comma
            pos = json.find_first_of(",}", pos);
            if (pos == std::string::npos || json[pos] == '}') break;
            pos++;
        }
    }

    auto err = g_ability_context->StartAbility(want, 0);
    return static_cast<int>(err);
}

extern "C" int shim_terminate_self() {
    std::lock_guard<std::mutex> lock(g_ctx_mutex);
    if (!g_ability_context) return -1;
    auto err = g_ability_context->TerminateSelf();
    return static_cast<int>(err);
}

// ═══════════════════════════════════════════════════════════════════
// HTTP — via libcurl (available in OH third_party)
// Maps: java.net.HttpURLConnection → curl_easy_*
// ═══════════════════════════════════════════════════════════════════

static bool g_curl_initialized = false;

static size_t curl_write_callback(void* contents, size_t size, size_t nmemb, void* userp) {
    size_t total = size * nmemb;
    auto* buf = static_cast<std::string*>(userp);
    buf->append(static_cast<char*>(contents), total);
    return total;
}

extern "C" int shim_http_request(const char* url, const char* method,
                                  const char* headers_json, const char* body,
                                  char** out_response, int* out_len) {
    if (!url || !out_response || !out_len) return -1;

    if (!g_curl_initialized) {
        curl_global_init(CURL_GLOBAL_ALL);
        g_curl_initialized = true;
    }

    CURL* curl = curl_easy_init();
    if (!curl) return -1;

    std::string response;

    curl_easy_setopt(curl, CURLOPT_URL, url);
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, curl_write_callback);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &response);
    curl_easy_setopt(curl, CURLOPT_TIMEOUT, 30L);
    curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L);

    // Set method
    if (method) {
        std::string m(method);
        if (m == "POST") {
            curl_easy_setopt(curl, CURLOPT_POST, 1L);
        } else if (m == "PUT") {
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "PUT");
        } else if (m == "DELETE") {
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "DELETE");
        } else if (m == "PATCH") {
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "PATCH");
        }
        // GET is default
    }

    // Set body for POST/PUT
    if (body && body[0] != '\0') {
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, body);
    }

    // Parse and set headers from JSON: {"Content-Type":"application/json", ...}
    struct curl_slist* header_list = nullptr;
    if (headers_json && headers_json[0] == '{') {
        std::string hj(headers_json);
        size_t pos = 1;
        while (pos < hj.size()) {
            size_t kstart = hj.find('"', pos);
            if (kstart == std::string::npos) break;
            size_t kend = hj.find('"', kstart + 1);
            if (kend == std::string::npos) break;
            std::string key = hj.substr(kstart + 1, kend - kstart - 1);

            size_t vstart = hj.find('"', kend + 1);
            if (vstart == std::string::npos) break;
            size_t vend = hj.find('"', vstart + 1);
            if (vend == std::string::npos) break;
            std::string val = hj.substr(vstart + 1, vend - vstart - 1);

            std::string header_line = key + ": " + val;
            header_list = curl_slist_append(header_list, header_line.c_str());
            pos = vend + 1;
        }
        if (header_list) {
            curl_easy_setopt(curl, CURLOPT_HTTPHEADER, header_list);
        }
    }

    CURLcode res = curl_easy_perform(curl);

    if (header_list) curl_slist_free_all(header_list);

    if (res != CURLE_OK) {
        curl_easy_cleanup(curl);
        *out_response = nullptr;
        *out_len = 0;
        return -1;
    }

    // Copy response to caller-owned buffer
    *out_len = static_cast<int>(response.size());
    *out_response = static_cast<char*>(malloc(response.size() + 1));
    if (*out_response) {
        std::memcpy(*out_response, response.c_str(), response.size());
        (*out_response)[response.size()] = '\0';
    }

    curl_easy_cleanup(curl);
    return 0;
}

extern "C" void shim_http_free(char* ptr) {
    free(ptr);
}

// ═══════════════════════════════════════════════════════════════════
// ArkUI Native Node API — View rendering bridge
//
// Uses ArkUI_NativeNodeAPI_1 to create/manage UI nodes programmatically.
// Links against libace_ndk.z.so.
// ═══════════════════════════════════════════════════════════════════

#include "arkui/native_interface.h"
#include "arkui/native_node.h"
#include "native_interface_xcomponent.h"

static ArkUI_NativeNodeAPI_1* g_node_api = nullptr;
static ShimNodeEventCallback g_event_callback = nullptr;
static std::mutex g_arkui_mutex;

// Global event receiver — dispatches ArkUI events to the Rust callback
static void arkui_event_receiver(ArkUI_NodeEvent* event) {
    if (!event || !g_event_callback) return;

    float data[MAX_COMPONENT_EVENT_ARG_NUM] = {};
    const char* string_data = nullptr;
    int data_count = 0;

    if (event->kind == NODE_TOUCH_EVENT) {
        // Touch events — pass basic info
        data[0] = event->touchEvent.action;
        data[1] = event->touchEvent.x;
        data[2] = event->touchEvent.y;
        data_count = 3;
    } else if (event->kind >= NODE_TEXT_INPUT_ON_CHANGE &&
               event->kind <= NODE_TEXT_INPUT_ON_PASTE) {
        // Text change events carry string data
        string_data = event->stringEvent.pStr;
        data_count = 0;
    } else if (event->kind >= NODE_TEXT_AREA_ON_CHANGE &&
               event->kind < NODE_TEXT_AREA_ON_CHANGE + 10) {
        string_data = event->stringEvent.pStr;
        data_count = 0;
    } else {
        // Component events — pass numeric data
        for (int i = 0; i < MAX_COMPONENT_EVENT_ARG_NUM; i++) {
            data[i] = event->componentEvent.data[i].f32;
        }
        data_count = MAX_COMPONENT_EVENT_ARG_NUM;
    }

    g_event_callback(event->eventId,
                     reinterpret_cast<long long>(event->node),
                     event->kind,
                     data, data_count,
                     string_data);
}

extern "C" int shim_arkui_init() {
    std::lock_guard<std::mutex> lock(g_arkui_mutex);
    if (g_node_api) return 0; // already initialized

    auto* anyApi = OH_ArkUI_GetNativeAPI(ARKUI_NATIVE_NODE, 1);
    if (!anyApi || anyApi->version != 1) return -1;

    g_node_api = reinterpret_cast<ArkUI_NativeNodeAPI_1*>(anyApi);
    g_node_api->registerNodeEventReceiver(arkui_event_receiver);
    return 0;
}

extern "C" void shim_node_set_event_callback(ShimNodeEventCallback callback) {
    g_event_callback = callback;
}

extern "C" long long shim_node_create(int node_type) {
    if (!g_node_api) return 0;
    auto handle = g_node_api->createNode(static_cast<ArkUI_NodeType>(node_type));
    return reinterpret_cast<long long>(handle);
}

extern "C" void shim_node_dispose(long long node) {
    if (!g_node_api || !node) return;
    g_node_api->disposeNode(reinterpret_cast<ArkUI_NodeHandle>(node));
}

extern "C" void shim_node_add_child(long long parent, long long child) {
    if (!g_node_api || !parent || !child) return;
    g_node_api->addChild(reinterpret_cast<ArkUI_NodeHandle>(parent),
                         reinterpret_cast<ArkUI_NodeHandle>(child));
}

extern "C" void shim_node_remove_child(long long parent, long long child) {
    if (!g_node_api || !parent || !child) return;
    g_node_api->removeChild(reinterpret_cast<ArkUI_NodeHandle>(parent),
                            reinterpret_cast<ArkUI_NodeHandle>(child));
}

extern "C" void shim_node_insert_child_at(long long parent, long long child, int position) {
    if (!g_node_api || !parent || !child) return;
    g_node_api->insertChildAt(reinterpret_cast<ArkUI_NodeHandle>(parent),
                              reinterpret_cast<ArkUI_NodeHandle>(child),
                              position);
}

extern "C" int shim_node_set_attr_float(long long node, int attr_type,
                                         const float* values, int count) {
    if (!g_node_api || !node || !values || count <= 0) return -1;
    ArkUI_NumberValue nums[16];
    int n = count < 16 ? count : 16;
    for (int i = 0; i < n; i++) nums[i].f32 = values[i];
    ArkUI_AttributeItem item = { nums, n, nullptr, nullptr };
    return g_node_api->setAttribute(reinterpret_cast<ArkUI_NodeHandle>(node),
                                    static_cast<ArkUI_NodeAttributeType>(attr_type),
                                    &item);
}

extern "C" int shim_node_set_attr_u32(long long node, int attr_type,
                                       const unsigned int* values, int count) {
    if (!g_node_api || !node || !values || count <= 0) return -1;
    ArkUI_NumberValue nums[16];
    int n = count < 16 ? count : 16;
    for (int i = 0; i < n; i++) nums[i].u32 = values[i];
    ArkUI_AttributeItem item = { nums, n, nullptr, nullptr };
    return g_node_api->setAttribute(reinterpret_cast<ArkUI_NodeHandle>(node),
                                    static_cast<ArkUI_NodeAttributeType>(attr_type),
                                    &item);
}

extern "C" int shim_node_set_attr_i32(long long node, int attr_type,
                                       const int* values, int count) {
    if (!g_node_api || !node || !values || count <= 0) return -1;
    ArkUI_NumberValue nums[16];
    int n = count < 16 ? count : 16;
    for (int i = 0; i < n; i++) nums[i].i32 = values[i];
    ArkUI_AttributeItem item = { nums, n, nullptr, nullptr };
    return g_node_api->setAttribute(reinterpret_cast<ArkUI_NodeHandle>(node),
                                    static_cast<ArkUI_NodeAttributeType>(attr_type),
                                    &item);
}

extern "C" int shim_node_set_attr_string(long long node, int attr_type, const char* value) {
    if (!g_node_api || !node) return -1;
    ArkUI_AttributeItem item = { nullptr, 0, value, nullptr };
    return g_node_api->setAttribute(reinterpret_cast<ArkUI_NodeHandle>(node),
                                    static_cast<ArkUI_NodeAttributeType>(attr_type),
                                    &item);
}

extern "C" int shim_node_register_event(long long node, int event_type, int event_id) {
    if (!g_node_api || !node) return -1;
    return g_node_api->registerNodeEvent(reinterpret_cast<ArkUI_NodeHandle>(node),
                                         static_cast<ArkUI_NodeEventType>(event_type),
                                         event_id);
}

extern "C" void shim_node_unregister_event(long long node, int event_type) {
    if (!g_node_api || !node) return;
    g_node_api->unregisterNodeEvent(reinterpret_cast<ArkUI_NodeHandle>(node),
                                    static_cast<ArkUI_NodeEventType>(event_type));
}

extern "C" void shim_node_mark_dirty(long long node, int flag) {
    if (!g_node_api || !node) return;
    g_node_api->markDirty(reinterpret_cast<ArkUI_NodeHandle>(node),
                          static_cast<ArkUI_NodeDirtyFlag>(flag));
}

extern "C" int shim_xcomponent_attach_root(void* xcomponent, long long root_node) {
    if (!xcomponent || !root_node) return -1;
    return OH_NativeXComponent_AttachNativeRootNode(
        static_cast<OH_NativeXComponent*>(xcomponent),
        reinterpret_cast<ArkUI_NodeHandle>(root_node));
}
