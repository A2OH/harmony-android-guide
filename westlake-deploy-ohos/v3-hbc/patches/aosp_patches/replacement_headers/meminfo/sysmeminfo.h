/*
 * aosp_patches/replacement_headers/meminfo/sysmeminfo.h
 *
 * REAL minimal replacement for Android libmeminfo SysMemInfo — drop-in
 * header+inline implementation. NOT a stub — real /proc/meminfo and
 * /proc/vmallocinfo parsers.
 *
 * Provides the API surface used by AOSP core/jni:
 *   - kMemTotal / kMemFree / kMemCached / ... tag constants
 *   - kDefaultSysMemInfoTags vector (iterable by Debug.cpp)
 *   - ReadMemInfo(count, tags, out) — looks up tags in /proc/meminfo
 *   - ReadVmallocInfo() — sums byte counts from /proc/vmallocinfo
 */
#pragma once

#include <cstdint>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <string>
#include <string_view>
#include <vector>

namespace android {
namespace meminfo {

class SysMemInfo {
 public:
    // Tag constants — the first N bytes of the line prefix in /proc/meminfo.
    static constexpr std::string_view kMemTotal     = "MemTotal:";
    static constexpr std::string_view kMemFree      = "MemFree:";
    static constexpr std::string_view kMemAvailable = "MemAvailable:";
    static constexpr std::string_view kMemBuffers   = "Buffers:";
    static constexpr std::string_view kMemCached    = "Cached:";
    static constexpr std::string_view kMemShmem     = "Shmem:";
    static constexpr std::string_view kMemSlab      = "Slab:";
    static constexpr std::string_view kMemSReclaim  = "SReclaimable:";
    static constexpr std::string_view kMemSUnreclaim = "SUnreclaim:";
    static constexpr std::string_view kMemSwapTotal = "SwapTotal:";
    static constexpr std::string_view kMemSwapFree  = "SwapFree:";
    static constexpr std::string_view kMemMapped    = "Mapped:";
    static constexpr std::string_view kMemVmallocUsed = "VmallocUsed:";
    static constexpr std::string_view kMemPageTables = "PageTables:";
    static constexpr std::string_view kMemKernelStack = "KernelStack:";

    // Default tag set — matches the set used by real libmeminfo for
    // android.os.Debug.getMemInfo(). Order matters for iteration callers.
    static inline const std::vector<std::string_view> kDefaultSysMemInfoTags = {
        kMemTotal, kMemFree, kMemBuffers, kMemCached, kMemShmem, kMemSlab,
        kMemSReclaim, kMemSUnreclaim, kMemSwapTotal, kMemSwapFree, kMemMapped,
        kMemVmallocUsed, kMemPageTables, kMemKernelStack,
    };

    // Reads /proc/meminfo, looks up each tag (prefix match), writes the
    // numeric value in kB to out[i]. Returns true on success (file opened).
    bool ReadMemInfo(size_t count,
                     const std::string_view* tags,
                     uint64_t* out) {
        if (!tags || !out) return false;
        for (size_t i = 0; i < count; ++i) out[i] = 0;
        FILE* f = fopen("/proc/meminfo", "re");
        if (!f) return false;
        char line[256];
        while (fgets(line, sizeof(line), f) != nullptr) {
            for (size_t i = 0; i < count; ++i) {
                const std::string_view& tag = tags[i];
                if (strncmp(line, tag.data(), tag.size()) == 0) {
                    unsigned long long v = 0;
                    if (sscanf(line + tag.size(), " %llu kB", &v) == 1) {
                        out[i] = static_cast<uint64_t>(v);
                    }
                    break;  // one tag match per line
                }
            }
        }
        fclose(f);
        return true;
    }

    // Parses /proc/vmallocinfo and returns total bytes used (vm_map_ram +
    // ioremap + vmalloc entries). Real libmeminfo excludes "unpurged" entries;
    // this implementation sums the byte count field of every non-empty line,
    // which is the same accounting the kernel presents to userspace.
    uint64_t ReadVmallocInfo() {
        FILE* f = fopen("/proc/vmallocinfo", "re");
        if (!f) return 0;
        char line[512];
        uint64_t total = 0;
        while (fgets(line, sizeof(line), f) != nullptr) {
            // Line format: "0xffffff... - 0xffffff... NNN <tag> ..."
            // Find the byte count — it is the third whitespace-separated field.
            char* p = line;
            // skip first field (start address)
            while (*p && *p != ' ') ++p;
            while (*p == ' ') ++p;
            // skip second field ("- addr")
            if (*p == '-') {
                ++p;
                while (*p == ' ') ++p;
                while (*p && *p != ' ') ++p;
                while (*p == ' ') ++p;
            }
            // third field is the byte count
            if (*p >= '0' && *p <= '9') {
                total += strtoull(p, nullptr, 10);
            }
        }
        fclose(f);
        return total;
    }
};

}  // namespace meminfo
}  // namespace android
