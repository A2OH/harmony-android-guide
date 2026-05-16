/*
 * oh_br_trace.h — Unified hitrace + hilog macros for adapter IPC endpoints.
 *
 * Each IPC entry/exit (forward bridge into OH, reverse callback into Android)
 * gets a single-line ENTER/EXIT pair that emits:
 *   1. OH_HiTrace_StartTrace / FinishTrace (hitrace mark, ns-level)
 *   2. HiLogPrint INFO line "[IPC] ENTER ..." / "[IPC] EXIT status=..."
 *
 * Usage in any .cpp under framework/{window,surface,activity,...}/jni/:
 *
 *     #include "oh_br_trace.h"
 *     #define LOG_TAG "OH_WMClient"          // module-specific
 *     // ... existing OH_BR_DOMAIN already defined or pick one ...
 *
 *     int someIpcMethod(int sessionId, const char* name) {
 *         OH_BR_IPC_ENTER("WMClient.someIpcMethod", "sid=%d name=%s",
 *                         sessionId, name ? name : "");
 *         int rc = doWork();
 *         OH_BR_IPC_EXIT("WMClient.someIpcMethod", rc);
 *         return rc;
 *     }
 *
 * Or RAII style for automatic FinishTrace on early return / exception:
 *
 *     int someIpcMethod() {
 *         OH_BR_IPC_SCOPE("WMClient.someIpcMethod");  // begin trace + log enter
 *         if (errorPath) return -1;                    // FinishTrace via dtor
 *         ...
 *     }
 *
 * 2026-05-08 G2.14ac.
 */
#ifndef OH_ADAPTER_BR_TRACE_H
#define OH_ADAPTER_BR_TRACE_H

#include <stdint.h>

/* HiLogPrint is declared in OH innerAPI header hilog/log_c.h with specific
 * attributes; include it directly to avoid forward-declaration conflicts. */
#include "hilog/log_c.h"

#ifdef __cplusplus
extern "C" {
#endif

/* OH innerAPI — definition in libhitrace_ndk.z.so. */
void OH_HiTrace_StartTrace(const char* name);
void OH_HiTrace_FinishTrace(void);

#ifdef __cplusplus
}  /* extern "C" */
#endif

/* Default domain for adapter logs — keep aligned with existing OH_JNI_Bridge,
 * OH_WMClient, OH_AbilityMgrClient etc. (all use 0xD000F00). */
#ifndef OH_BR_DOMAIN
#define OH_BR_DOMAIN 0xD000F00u
#endif

/* Caller must define LOG_TAG before including this header (or before macro
 * expansion). Each translation unit should set its own short module tag. */
#ifndef LOG_TAG
#define LOG_TAG "OH_BR"
#endif

/* HiLogPrint signature (OH log_c.h):
 *   int HiLogPrint(LogType type, LogLevel level, unsigned int domain,
 *                  const char* tag, const char* fmt, ...);
 * LogType: LOG_CORE = 3
 * LogLevel: LOG_DEBUG = 3, LOG_INFO = 4, LOG_WARN = 5, LOG_ERROR = 6
 * 用枚举名而非 int 字面量避免类型冲突。 */

/* ENTER macro: emit hitrace begin mark + hilog INFO with args.
 * args 必须按 printf 格式（args_fmt + variadic）。 */
#define OH_BR_IPC_ENTER(label, args_fmt, ...)                              \
    do {                                                                   \
        ::OH_HiTrace_StartTrace(label);                                    \
        ::HiLogPrint(LOG_CORE, LOG_INFO, OH_BR_DOMAIN, LOG_TAG,            \
                     "[IPC] ENTER %{public}s " args_fmt, label, ##__VA_ARGS__);    \
    } while (0)

/* EXIT macro: emit hitrace end mark + hilog with return value.
 * 自动级别：rc>=0 → INFO（成功），rc<0 → ERROR（失败）。 */
#define OH_BR_IPC_EXIT(label, retcode)                                     \
    do {                                                                   \
        long _ohBrRc = (long)(retcode);                                    \
        ::HiLogPrint(LOG_CORE, _ohBrRc < 0 ? LOG_ERROR : LOG_INFO,         \
                     OH_BR_DOMAIN, LOG_TAG,                                \
                     "[IPC] EXIT  %{public}s rc=%{public}ld", label, _ohBrRc); \
        ::OH_HiTrace_FinishTrace();                                        \
    } while (0)

/* FAIL macro: 错误路径 + 携带具体错误描述 + 自动 FinishTrace。 */
#define OH_BR_IPC_FAIL(label, rc, fmt, ...)                                \
    do {                                                                   \
        ::HiLogPrint(LOG_CORE, LOG_ERROR, OH_BR_DOMAIN, LOG_TAG,           \
                     "[IPC] FAIL  %{public}s rc=%{public}ld " fmt,         \
                     label, (long)(rc), ##__VA_ARGS__);                    \
        ::OH_HiTrace_FinishTrace();                                        \
    } while (0)

/* EXIT_VOID for functions that return void — only emit hitrace end. */
#define OH_BR_IPC_EXIT_VOID(label)                                         \
    do {                                                                   \
        ::HiLogPrint(LOG_CORE, LOG_INFO, OH_BR_DOMAIN, LOG_TAG,            \
                     "[IPC] EXIT  %{public}s (void)", label);              \
        ::OH_HiTrace_FinishTrace();                                        \
    } while (0)

/* WARN / ERROR helpers (for inline error paths inside IPC handlers). */
#define OH_BR_IPC_WARN(label, fmt, ...)                                    \
    ::HiLogPrint(LOG_CORE, LOG_WARN, OH_BR_DOMAIN, LOG_TAG,                \
                 "[IPC] %{public}s WARN " fmt, label, ##__VA_ARGS__)

#define OH_BR_IPC_ERROR(label, fmt, ...)                                   \
    ::HiLogPrint(LOG_CORE, LOG_ERROR, OH_BR_DOMAIN, LOG_TAG,               \
                 "[IPC] %{public}s ERROR " fmt, label, ##__VA_ARGS__)

#ifdef __cplusplus
/* RAII scope helper — automatic FinishTrace on dtor.
 * 用于 early return / exception / 多分支函数 — 不必每个分支写 EXIT。
 *
 * Usage: OH_BR_IPC_SCOPE("WMClient.fn");
 *        // function body... any return path auto-emits FinishTrace
 *
 * Limitation: 不记录 retcode（dtor 看不到返回值）；如要 retcode 用 ENTER/EXIT 配对。
 */
class OhBrTraceScope {
public:
    explicit OhBrTraceScope(const char* name) : name_(name) {
        ::OH_HiTrace_StartTrace(name_);
    }
    ~OhBrTraceScope() { ::OH_HiTrace_FinishTrace(); }
    OhBrTraceScope(const OhBrTraceScope&) = delete;
    OhBrTraceScope& operator=(const OhBrTraceScope&) = delete;
private:
    const char* name_;
};

/* OH_BR_IPC_SCOPE(label, args_fmt = "", ...): RAII begin trace + log enter.
 *
 * 第二个参数后是 printf-style args（可空）。两种用法：
 *   OH_BR_IPC_SCOPE("Module.fn", "");              // 无参函数
 *   OH_BR_IPC_SCOPE("Module.fn", "x=%d y=%s", x, y); // 带参函数
 *
 * 自动取代原"函数开头一行 LOGI(详情) + 加 SCOPE"两行写法。
 */
#define OH_BR_IPC_SCOPE(label, args_fmt, ...)                              \
    OhBrTraceScope _ohBrScope_##__LINE__(label);                           \
    ::HiLogPrint(LOG_CORE, LOG_INFO, OH_BR_DOMAIN, LOG_TAG,                \
                 "[IPC] ENTER %{public}s " args_fmt, label, ##__VA_ARGS__)
#endif  /* __cplusplus */

#endif  /* OH_ADAPTER_BR_TRACE_H */
