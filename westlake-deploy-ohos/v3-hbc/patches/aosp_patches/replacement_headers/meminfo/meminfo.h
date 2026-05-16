/*
 * aosp_patches/replacement_headers/meminfo/meminfo.h
 *
 * REAL umbrella replacement for Android libmeminfo's top-level header.
 * Used when cross-compiling AOSP core/jni against a partial sync that
 * does NOT include system/memory/libmeminfo/.
 *
 * Provides:
 *   - struct MemUsage   (per-VMA accounting fields)
 *   - struct Vma        (one memory mapping: range + name + usage)
 *   - ForEachVmaFromFile(path, cb) — real /proc/.../smaps parser
 *
 * NOT a stub — each field is populated from the kernel's authoritative
 * /proc interfaces, same as the real libmeminfo does internally. Only
 * the subset of the API actually used by AOSP core/jni callers is
 * implemented here; extend as more call sites appear.
 */
#pragma once

#include <cstdint>
#include <cstdio>
#include <cstring>
#include <functional>
#include <string>

namespace android {
namespace meminfo {

struct MemUsage {
    uint64_t rss = 0;
    uint64_t pss = 0;
    uint64_t uss = 0;
    uint64_t private_clean = 0;
    uint64_t private_dirty = 0;
    uint64_t shared_clean = 0;
    uint64_t shared_dirty = 0;
    uint64_t swap = 0;
    uint64_t swap_pss = 0;
    uint64_t anon_huge_pages = 0;
    uint64_t shmem_pmd_mapped = 0;
    uint64_t file_pmd_mapped = 0;
    uint64_t shared_hugetlb = 0;
    uint64_t private_hugetlb = 0;
};

struct Vma {
    uint64_t start = 0;
    uint64_t end = 0;
    uint64_t offset = 0;
    uint16_t flags = 0;     // r/w/x permission bits
    std::string name;
    MemUsage usage;
};

namespace detail {

// Parse a single "Key: value kB" smaps line into the matching MemUsage field.
// Real libmeminfo uses a static hash table; a switch on prefix is sufficient
// for the fields AOSP core/jni actually looks at.
inline void parse_smaps_usage_line(const char* line, MemUsage* u) {
    if (!u || !line) return;
    unsigned long long v = 0;
    auto match = [&](const char* key) -> bool {
        size_t klen = strlen(key);
        if (strncmp(line, key, klen) != 0) return false;
        return sscanf(line + klen, " %llu kB", &v) == 1;
    };
    if      (match("Rss:"))            u->rss            = v;
    else if (match("Pss:"))            u->pss            = v;
    else if (match("Shared_Clean:"))   u->shared_clean   = v;
    else if (match("Shared_Dirty:"))   u->shared_dirty   = v;
    else if (match("Private_Clean:"))  u->private_clean  = v;
    else if (match("Private_Dirty:"))  u->private_dirty  = v;
    else if (match("Swap:"))           u->swap           = v;
    else if (match("SwapPss:"))        u->swap_pss       = v;
    else if (match("AnonHugePages:"))  u->anon_huge_pages = v;
    else if (match("ShmemPmdMapped:")) u->shmem_pmd_mapped = v;
    else if (match("FilePmdMapped:"))  u->file_pmd_mapped  = v;
    else if (match("Shared_Hugetlb:")) u->shared_hugetlb   = v;
    else if (match("Private_Hugetlb:")) u->private_hugetlb = v;

    // USS (unique set size) is not a raw kernel field — it's computed as
    // private_clean + private_dirty once all lines have been parsed. Real
    // libmeminfo recomputes it after each Vma is complete; ForEachVmaFromFile
    // below does the same.
}

// Parse the header line of a smaps VMA block:
//   "00400000-0041c000 r-xp 00000000 fe:00 1234567  /path/to/file"
// Writes start/end/offset/flags/name into *vma. Returns true on match.
inline bool parse_smaps_header(const char* line, Vma* vma) {
    if (!line || !vma) return false;
    unsigned long long start = 0, end = 0, offset = 0;
    char perm[5] = {0};
    unsigned long dev_major = 0, dev_minor = 0;
    unsigned long inode = 0;
    int scanned = 0;
    // Name is whatever follows the inode, possibly empty / whitespace-only.
    int n = sscanf(line, "%llx-%llx %4s %llx %lx:%lx %lu %n",
                   &start, &end, perm, &offset, &dev_major, &dev_minor, &inode, &scanned);
    if (n < 7) return false;
    vma->start = start;
    vma->end = end;
    vma->offset = offset;
    vma->flags = 0;
    if (perm[0] == 'r') vma->flags |= 0x1;
    if (perm[1] == 'w') vma->flags |= 0x2;
    if (perm[2] == 'x') vma->flags |= 0x4;
    if (perm[3] == 's') vma->flags |= 0x8;
    // Strip leading whitespace from name; trim trailing newline.
    const char* name_p = line + scanned;
    while (*name_p == ' ' || *name_p == '\t') ++name_p;
    std::string name(name_p);
    while (!name.empty() && (name.back() == '\n' || name.back() == '\r' || name.back() == ' ')) {
        name.pop_back();
    }
    vma->name = std::move(name);
    vma->usage = MemUsage{};
    return true;
}

}  // namespace detail

// Real /proc/<pid>/smaps parser. Iterates all Vma blocks in the given file
// and invokes `callback` once per fully-populated Vma. Returns false if the
// file cannot be opened. Mirrors the semantics of real libmeminfo::ForEachVmaFromFile.
inline bool ForEachVmaFromFile(const std::string& path,
                               const std::function<void(const Vma&)>& callback) {
    FILE* f = fopen(path.c_str(), "re");
    if (!f) return false;
    char line[1024];
    Vma current;
    bool have_vma = false;
    while (fgets(line, sizeof(line), f) != nullptr) {
        // Header lines start with hex digits (address range). Heuristic: the
        // first char is a hex digit and the 9th or later is '-'.
        bool is_header =
            ((line[0] >= '0' && line[0] <= '9') ||
             (line[0] >= 'a' && line[0] <= 'f') ||
             (line[0] >= 'A' && line[0] <= 'F')) &&
            strchr(line, '-') != nullptr &&
            strchr(line, ' ') != nullptr;
        Vma parsed;
        if (is_header && detail::parse_smaps_header(line, &parsed)) {
            if (have_vma) {
                current.usage.uss =
                    current.usage.private_clean + current.usage.private_dirty;
                callback(current);
            }
            current = std::move(parsed);
            have_vma = true;
        } else if (have_vma) {
            detail::parse_smaps_usage_line(line, &current.usage);
        }
    }
    if (have_vma) {
        current.usage.uss =
            current.usage.private_clean + current.usage.private_dirty;
        callback(current);
    }
    fclose(f);
    return true;
}

}  // namespace meminfo
}  // namespace android
