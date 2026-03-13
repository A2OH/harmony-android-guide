/*
 * Android systrace shim — no-op.
 */
#ifndef _CUTILS_TRACE_H
#define _CUTILS_TRACE_H

#define ATRACE_TAG_DALVIK  0x4000
#define ATRACE_ENABLED()   (0)
#define ATRACE_BEGIN(name) ((void)(name))
#define ATRACE_END()       ((void)0)
#define ATRACE_INT(name, value) ((void)(name), (void)(value))

#define atrace_begin(tag, name) ((void)(tag), (void)(name))
#define atrace_end(tag)         ((void)(tag))

#endif /* _CUTILS_TRACE_H */
