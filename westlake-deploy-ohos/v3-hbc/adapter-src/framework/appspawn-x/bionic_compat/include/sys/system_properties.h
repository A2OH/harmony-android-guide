// bionic_compat/include/sys/system_properties.h
// Compatibility header: maps Android system property APIs to OH sysparam
#ifndef BIONIC_COMPAT_SYSTEM_PROPERTIES_H
#define BIONIC_COMPAT_SYSTEM_PROPERTIES_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

#define PROP_NAME_MAX 256
#define PROP_VALUE_MAX 92

// Opaque handle returned by __system_property_find
typedef struct prop_info prop_info;

const prop_info* __system_property_find(const char* name);

int __system_property_read(const prop_info* pi, char* name, char* value);

void __system_property_read_callback(
    const prop_info* pi,
    void (*callback)(void* cookie, const char* name, const char* value, uint32_t serial),
    void* cookie);

int __system_property_get(const char* name, char* value);

int __system_property_set(const char* name, const char* value);

int __system_property_foreach(
    void (*callback)(const prop_info* pi, void* cookie),
    void* cookie);

// Bionic-internal: register property change callback
void add_sysprop_change_callback(void (*callback)(void), int priority);

#ifdef __cplusplus
}
#endif

#endif // BIONIC_COMPAT_SYSTEM_PROPERTIES_H
