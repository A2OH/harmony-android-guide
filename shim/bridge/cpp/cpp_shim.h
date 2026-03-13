/**
 * cpp_shim.h — Thin C wrapper around OH C++ inner APIs + libcurl.
 *
 * Produces: liboh_cpp_shim.so
 *
 * Wraps:
 * - OHOS::NativePreferences::Preferences (SharedPreferences shim)
 * - OHOS::Notification::NotificationHelper (Notification shim)
 * - OHOS::Notification::ReminderHelper + ReminderRequestTimer (AlarmManager shim)
 * - @ohos.promptAction via NAPI (Toast shim)
 * - OHOS::AbilityRuntime::AbilityContext + AAFwk::Want (navigation shim)
 * - libcurl (HttpURLConnection shim)
 */

#ifndef OH_CPP_SHIM_H
#define OH_CPP_SHIM_H

#ifdef __cplusplus
extern "C" {
#endif

/* ── Preferences ───────────────────────────────────────────────── */

typedef void* ShimPreferences;

ShimPreferences shim_preferences_open(const char* file_path);
void shim_preferences_close(ShimPreferences prefs);

int shim_preferences_get_string(ShimPreferences prefs, const char* key,
                                 const char* def, char* out_buf, int buf_len);
int shim_preferences_get_int(ShimPreferences prefs, const char* key, int def);
long long shim_preferences_get_long(ShimPreferences prefs, const char* key, long long def);
float shim_preferences_get_float(ShimPreferences prefs, const char* key, float def);
int shim_preferences_get_bool(ShimPreferences prefs, const char* key, int def);

int shim_preferences_put_string(ShimPreferences prefs, const char* key, const char* value);
int shim_preferences_put_int(ShimPreferences prefs, const char* key, int value);
int shim_preferences_put_long(ShimPreferences prefs, const char* key, long long value);
int shim_preferences_put_float(ShimPreferences prefs, const char* key, float value);
int shim_preferences_put_bool(ShimPreferences prefs, const char* key, int value);

int shim_preferences_delete(ShimPreferences prefs, const char* key);
int shim_preferences_clear(ShimPreferences prefs);
int shim_preferences_flush(ShimPreferences prefs);

/* ── Notification ──────────────────────────────────────────────── */

int shim_notification_publish(int id, const char* title, const char* text, int slot_type);
int shim_notification_cancel(int id);
int shim_notification_add_slot(int slot_type);

/* ── Reminder ──────────────────────────────────────────────────── */

int shim_reminder_publish_timer(int delay_seconds, const char* title,
                                 const char* content, const char* bundle_name,
                                 const char* ability_name);
int shim_reminder_cancel(int reminder_id);

/* ── Toast (via NAPI → @ohos.promptAction) ─────────────────────── */

/** Must be called once at startup to provide the NAPI environment */
void shim_set_napi_env(void* env);

int shim_show_toast(const char* message, int duration);

/* ── Ability Navigation ────────────────────────────────────────── */

/** Must be called once at startup to provide the AbilityContext */
void shim_set_ability_context(void* context);

int shim_start_ability(const char* bundle_name, const char* ability_name,
                        const char* params_json);
int shim_terminate_self(void);

/* ── HTTP (via libcurl) ────────────────────────────────────────── */

int shim_http_request(const char* url, const char* method,
                       const char* headers_json, const char* body,
                       char** out_response, int* out_len);
void shim_http_free(char* ptr);

/* ── ArkUI Native Node API (View rendering) ───────────────────── */

/** Must be called once at startup to init the ArkUI native node API */
int shim_arkui_init(void);

/** Create a native ArkUI node. Returns opaque handle (0 on failure).
 *  node_type values match ArkUI_NodeType enum:
 *    1=TEXT, 4=IMAGE, 5=TOGGLE, 7=TEXT_INPUT, 8=STACK, 9=SCROLL,
 *    10=LIST, 12=TEXT_AREA, 13=BUTTON, 14=PROGRESS, 15=CHECKBOX,
 *    16=COLUMN, 17=ROW, 18=FLEX, 19=LIST_ITEM, 25=SLIDER */
long long shim_node_create(int node_type);

/** Dispose (destroy) a native node */
void shim_node_dispose(long long node);

/** Tree operations */
void shim_node_add_child(long long parent, long long child);
void shim_node_remove_child(long long parent, long long child);
void shim_node_insert_child_at(long long parent, long long child, int position);

/** Set a float attribute (width, height, fontSize, opacity, padding, etc.)
 *  attr_type matches ArkUI_NodeAttributeType enum values */
int shim_node_set_attr_float(long long node, int attr_type,
                              const float* values, int count);

/** Set a u32 attribute (backgroundColor, fontColor, borderColor) */
int shim_node_set_attr_u32(long long node, int attr_type,
                            const unsigned int* values, int count);

/** Set an i32 attribute (visibility, enabled, fontWeight, textAlign, etc.) */
int shim_node_set_attr_i32(long long node, int attr_type,
                            const int* values, int count);

/** Set a string attribute (text content, image src, placeholder, id, etc.) */
int shim_node_set_attr_string(long long node, int attr_type, const char* value);

/** Register an event on a node. eventId is passed back in the callback. */
int shim_node_register_event(long long node, int event_type, int event_id);

/** Unregister an event */
void shim_node_unregister_event(long long node, int event_type);

/** Set the global event callback. The callback receives (eventId, nodeHandle, eventKind, float_data[12]).
 *  This is called from shim_arkui_init — the Rust side registers its dispatch fn. */
typedef void (*ShimNodeEventCallback)(int event_id, long long node, int event_kind,
                                       const float* data, int data_count,
                                       const char* string_data);
void shim_node_set_event_callback(ShimNodeEventCallback callback);

/** Mark a node dirty for re-layout/re-render. flag: 1=MEASURE, 2=LAYOUT, 3=RENDER */
void shim_node_mark_dirty(long long node, int flag);

/** Attach a native root node to an XComponent surface */
int shim_xcomponent_attach_root(void* xcomponent, long long root_node);

#ifdef __cplusplus
}
#endif

#endif /* OH_CPP_SHIM_H */
