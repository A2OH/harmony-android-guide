// bionic_compat/src/system_properties.cpp
// Maps Android system property API to OH sysparam API
// Uses only C and POSIX APIs to avoid C++ STL include issues with musl

#include <sys/system_properties.h>
#include <string.h>
#include <stdlib.h>
#include <pthread.h>

// OH sysparam API
// The actual exported symbols in libbegetutil.z.so are versioned (@@1.0):
//   SystemReadParam@@1.0, SystemSetParameter@@1.0, SystemWriteParam@@1.0
// OH header defines: #define SystemGetParameter SystemReadParam
// We declare with __asm__ to link against the versioned symbols directly.
extern "C" {
    int SystemReadParam(const char* key, char* value, uint32_t* len)
        __asm__("SystemReadParam");
    int SystemSetParameter(const char* key, const char* value)
        __asm__("SystemSetParameter");
}
#define SystemGetParameter SystemReadParam

// Simple prop_info cache (fixed-size, no STL)
#define MAX_CACHED_PROPS 256

struct prop_info {
    char name[PROP_NAME_MAX];
    char value[PROP_VALUE_MAX];
    uint32_t serial;
};

static prop_info g_prop_cache[MAX_CACHED_PROPS];
static int g_prop_count = 0;
static uint32_t g_serial = 0;
static pthread_mutex_t g_mutex = PTHREAD_MUTEX_INITIALIZER;

static prop_info* find_or_create_cache_entry(const char* name) {
    // Find existing
    for (int i = 0; i < g_prop_count; i++) {
        if (strcmp(g_prop_cache[i].name, name) == 0) {
            return &g_prop_cache[i];
        }
    }
    // Create new if space available
    if (g_prop_count < MAX_CACHED_PROPS) {
        prop_info* pi = &g_prop_cache[g_prop_count++];
        strncpy(pi->name, name, PROP_NAME_MAX - 1);
        pi->name[PROP_NAME_MAX - 1] = '\0';
        return pi;
    }
    return nullptr;
}

const prop_info* __system_property_find(const char* name) {
    if (!name) return nullptr;

    char value[PROP_VALUE_MAX] = {0};
    unsigned int len = PROP_VALUE_MAX;
    int ret = SystemGetParameter(name, value, &len);
    if (ret != 0 && value[0] == '\0') {
        return nullptr;
    }

    pthread_mutex_lock(&g_mutex);
    prop_info* pi = find_or_create_cache_entry(name);
    if (pi) {
        strncpy(pi->value, value, PROP_VALUE_MAX - 1);
        pi->value[PROP_VALUE_MAX - 1] = '\0';
        pi->serial = ++g_serial;
    }
    pthread_mutex_unlock(&g_mutex);
    return pi;
}

int __system_property_read(const prop_info* pi, char* name, char* value) {
    if (!pi) return -1;
    if (name) strncpy(name, pi->name, PROP_NAME_MAX);
    if (value) strncpy(value, pi->value, PROP_VALUE_MAX);
    return (int)strlen(pi->value);
}

void __system_property_read_callback(
        const prop_info* pi,
        void (*callback)(void*, const char*, const char*, uint32_t),
        void* cookie) {
    if (pi && callback) {
        callback(cookie, pi->name, pi->value, pi->serial);
    }
}

int __system_property_get(const char* name, char* value) {
    if (!name || !value) return 0;
    unsigned int len = PROP_VALUE_MAX;
    value[0] = '\0';
    SystemGetParameter(name, value, &len);
    return (int)strlen(value);
}

int __system_property_set(const char* name, const char* value) {
    if (!name || !value) return -1;
    return SystemSetParameter(name, value);
}

int __system_property_foreach(
        void (*callback)(const prop_info*, void*),
        void* cookie) {
    (void)callback;
    (void)cookie;
    return 0;
}

// Callback registry (simple array, no STL)
#define MAX_CALLBACKS 16
static void (*g_callbacks[MAX_CALLBACKS])(void);
static int g_callback_count = 0;

void add_sysprop_change_callback(void (*callback)(void), int priority) {
    (void)priority;
    if (!callback) return;
    pthread_mutex_lock(&g_mutex);
    if (g_callback_count < MAX_CALLBACKS) {
        g_callbacks[g_callback_count++] = callback;
    }
    pthread_mutex_unlock(&g_mutex);
}
