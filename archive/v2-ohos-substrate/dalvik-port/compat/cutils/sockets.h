/*
 * Android socket utils shim — JDWP ADB transport only.
 * We disable JDWP, so this is just a stub.
 */
#ifndef _CUTILS_SOCKETS_H
#define _CUTILS_SOCKETS_H

#include <sys/socket.h>
#include <sys/un.h>

#ifdef __cplusplus
extern "C" {
#endif

static inline int socket_local_client(const char* name, int namespaceId, int type) {
    (void)name; (void)namespaceId; (void)type;
    return -1; /* not supported */
}

#ifdef __cplusplus
}
#endif

#endif /* _CUTILS_SOCKETS_H */
