/*
 * aosp_patches/replacement_headers/meminfo/procmeminfo.h
 *
 * REAL minimal replacement for Android libmeminfo ProcMemInfo — drop-in
 * header+inline implementation used when cross-compiling AOSP core/jni
 * against a partial sync that does NOT include system/memory/libmeminfo/.
 *
 * NOT a stub — real /proc parser. SmapsOrRollup() parses
 * /proc/<pid>/smaps_rollup into a full MemUsage (all fields, not just Pss).
 * SmapsOrRollupPss() is a fast-path convenience.
 */
#pragma once

#include "meminfo.h"  // for MemUsage, detail::parse_smaps_usage_line

#include <cstdint>
#include <cstdio>
#include <cstring>
#include <string>
#include <sys/types.h>

namespace android {
namespace meminfo {

class ProcMemInfo {
 public:
    explicit ProcMemInfo(pid_t pid) : pid_(pid) {}

    // Reads /proc/<pid>/smaps_rollup and returns the Pss value in kB via *pss.
    // Returns false if the file cannot be opened or no Pss line is found.
    bool SmapsOrRollupPss(uint64_t* pss) {
        if (!pss) return false;
        char path[64];
        snprintf(path, sizeof(path), "/proc/%d/smaps_rollup", static_cast<int>(pid_));
        FILE* f = fopen(path, "re");
        if (!f) {
            snprintf(path, sizeof(path), "/proc/%d/smaps", static_cast<int>(pid_));
            f = fopen(path, "re");
            if (!f) return false;
        }
        char line[256];
        uint64_t pss_total = 0;
        bool found = false;
        while (fgets(line, sizeof(line), f) != nullptr) {
            if (strncmp(line, "Pss:", 4) == 0) {
                unsigned long long v = 0;
                if (sscanf(line + 4, " %llu kB", &v) == 1) {
                    pss_total += static_cast<uint64_t>(v);
                    found = true;
                }
            }
        }
        fclose(f);
        if (!found) return false;
        *pss = pss_total;
        return true;
    }

    // Reads /proc/<pid>/smaps_rollup and fills all fields of *stats.
    // Returns false if the file cannot be opened. Mirrors real libmeminfo
    // semantics (smaps_rollup is a kernel-side rollup of all VMAs).
    bool SmapsOrRollup(MemUsage* stats) {
        if (!stats) return false;
        char path[64];
        snprintf(path, sizeof(path), "/proc/%d/smaps_rollup", static_cast<int>(pid_));
        FILE* f = fopen(path, "re");
        if (!f) {
            snprintf(path, sizeof(path), "/proc/%d/smaps", static_cast<int>(pid_));
            f = fopen(path, "re");
            if (!f) return false;
        }
        *stats = MemUsage{};
        char line[256];
        while (fgets(line, sizeof(line), f) != nullptr) {
            detail::parse_smaps_usage_line(line, stats);
        }
        stats->uss = stats->private_clean + stats->private_dirty;
        fclose(f);
        return true;
    }

 private:
    pid_t pid_;
};

}  // namespace meminfo
}  // namespace android
