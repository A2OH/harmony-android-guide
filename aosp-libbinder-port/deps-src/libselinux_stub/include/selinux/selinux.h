// libbinder-port: stub selinux.h
//
// Provides the minimum set of declarations needed for AOSP servicemanager
// Access.cpp / ServiceManager.cpp to compile without libselinux.
// At runtime, all the heavy paths sit behind #ifdef __ANDROID__ which we do
// NOT define — the only function that's actually called when SELinux is off
// is freecon() (in the Access destructor), which we make a free() call.
#pragma once

#include <stdlib.h>

typedef unsigned short security_class_t;

#ifdef __cplusplus
extern "C" {
#endif

// security_context_t is `char*` historically; new selinux uses plain char*.
static inline void freecon(char* p) { free(p); }

#ifdef __cplusplus
}
#endif
