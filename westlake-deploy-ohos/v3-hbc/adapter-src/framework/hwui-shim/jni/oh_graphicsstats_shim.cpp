/*
 * oh_graphicsstats_shim.cpp
 *
 * A.8 PENDING STUB — android::uirenderer::GraphicsStatsService
 *
 * The real implementation in AOSP frameworks/base/libs/hwui/service/
 * GraphicsStatsService.cpp depends on protobuf + libstatspull + statslog_hwui
 * (all AOSP-exclusive subsystems that cannot be ported to OH/musl without
 * pulling a multi-MB stats infrastructure). These six symbols are only
 * invoked by the `dumpsys gfxinfo` diagnostic path and the Java-side
 * GraphicsStatsService, neither of which is exercised during Hello World
 * cold start.
 *
 * Replacement semantics: dump operations become no-ops; createDump returns
 * nullptr (callers treat it as "stats unavailable"). This is logged as a
 * PENDING STUB in doc/graphics_rendering_design.html appendix.
 *
 * Mangling note: the UND in libhwui.so uses `std::__h::basic_string`
 * (OH libc++ inline namespace tag), so this file MUST be compiled with the
 * OH clang + OH libcxx-ohos toolchain to emit matching mangled names.
 */
#include <string>
#include <cstdint>

/* Opaque forward decl — matches `P15AStatsEventList` in libhwui UND (global
 * namespace, 15-char class name). Real type lives in AOSP
 * system/stats/stats_event.h. */
struct AStatsEventList;

namespace android {
namespace uirenderer {

/* Opaque forward decl for ProfileData (from libhwui internal JankTracker.h).
 * We never dereference it — stubs just accept pointer. */
struct ProfileData;

class GraphicsStatsService {
public:
    class Dump;
    enum class DumpType {
        Text,
        Protobuf,
        ProtobufStatsd,
    };

    static void saveBuffer(const std::string& path, const std::string& package,
                           int64_t versionCode, int64_t startTime, int64_t endTime,
                           const ProfileData* data);
    static Dump* createDump(int outFd, DumpType type);
    static void addToDump(Dump* dump, const std::string& path, const std::string& package,
                          int64_t versionCode, int64_t startTime, int64_t endTime,
                          const ProfileData* data);
    static void addToDump(Dump* dump, const std::string& path);
    static void finishDump(Dump* dump);
    static void finishDumpInMemory(Dump* dump, AStatsEventList* data, bool lastFullDay);
};

/* ============================================================ */
/* Stub bodies — all no-op                                       */
/* ============================================================ */

void GraphicsStatsService::saveBuffer(const std::string&, const std::string&,
                                      int64_t, int64_t, int64_t,
                                      const ProfileData*) {
    /* A.8 PENDING STUB */
}

GraphicsStatsService::Dump* GraphicsStatsService::createDump(int, DumpType) {
    /* A.8 PENDING STUB — returning nullptr signals "stats unavailable" to
     * GraphicsStatsService.java; subsequent addToDump/finishDump are no-ops. */
    return nullptr;
}

void GraphicsStatsService::addToDump(Dump*, const std::string&, const std::string&,
                                     int64_t, int64_t, int64_t,
                                     const ProfileData*) {
    /* A.8 PENDING STUB */
}

void GraphicsStatsService::addToDump(Dump*, const std::string&) {
    /* A.8 PENDING STUB */
}

void GraphicsStatsService::finishDump(Dump*) {
    /* A.8 PENDING STUB */
}

void GraphicsStatsService::finishDumpInMemory(Dump*, AStatsEventList*, bool) {
    /* A.8 PENDING STUB */
}

}  /* namespace uirenderer */
}  /* namespace android */
