// bionic_compat/src/minizip.cpp
// Minimal ZIP archive reader implementing the AOSP libziparchive API.
// Handles both stored (method 0) and deflated (method 8) entries.
// Compiled as part of libart_runtime_stubs.so on ARM32 OpenHarmony (musl libc).
// Links with -lz for zlib inflate support.

#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <errno.h>
#include <zlib.h>

#include <string_view>

// Use AOSP's actual ZipEntry definition for ABI compatibility
#include <ziparchive/zip_archive.h>

// Remove our custom ZipEntry definition — AOSP's header provides it.
// The following was the old custom struct (now replaced by #include above):
#if 0  // Disabled: using AOSP zip_archive.h for ZipEntry/ZipEntry64 definitions
    uint8_t pad[3];
};
#endif  // #if 0

// ---------------------------------------------------------------------------
// Internal MiniZipArchive struct (our implementation, opaque to callers).
// AOSP's ZipArchiveHandle (from zip_archive.h) is cast to MiniZipArchive*
// at API boundaries via reinterpret_cast.
// ---------------------------------------------------------------------------
struct MiniZipArchive {
    int fd;
    bool owns_fd;

    // Memory-mapped source (for OpenArchiveFromMemory)
    const uint8_t* mem_base;
    size_t mem_len;

    // Central directory: raw bytes, count, offset
    uint8_t* cd_data;
    uint32_t cd_size;
    uint32_t cd_offset;
    uint16_t entry_count;

    char* debug_name;
};

// ZipArchiveHandle is provided by AOSP's zip_archive.h (typedef ZipArchive* ZipArchiveHandle).
// We cast between ZipArchiveHandle and MiniZipArchive* at API boundaries.

// ---------------------------------------------------------------------------
// ZIP format constants
// ---------------------------------------------------------------------------
static constexpr uint32_t kEOCDSignature      = 0x06054b50;
static constexpr uint32_t kCDEntrySignature    = 0x02014b50;
static constexpr uint32_t kLocalHeaderSignature = 0x04034b50;
static constexpr uint16_t kMethodStored  = 0;
static constexpr uint16_t kMethodDeflate = 8;
static constexpr int kMaxEOCDSearch = 65536 + 22; // max comment + EOCD size

// Error codes
static constexpr int kSuccess = 0;
static constexpr int kErrInvalidZip      = -1;
static constexpr int kErrEntryNotFound   = -2;
static constexpr int kErrIO              = -3;
static constexpr int kErrDecompression   = -4;
static constexpr int kErrBufferTooSmall  = -5;
static constexpr int kErrInvalidHandle   = -6;
static constexpr int kErrAllocation      = -7;

// ---------------------------------------------------------------------------
// Helpers: read from fd or memory
// ---------------------------------------------------------------------------
static ssize_t archive_read_at(MiniZipArchive* ar, void* buf, size_t count, off64_t offset) {
    if (ar->mem_base) {
        if (offset < 0 || (size_t)offset + count > ar->mem_len) return -1;
        memcpy(buf, ar->mem_base + offset, count);
        return (ssize_t)count;
    }
    if (lseek64(ar->fd, offset, SEEK_SET) == (off64_t)-1) return -1;
    size_t total = 0;
    while (total < count) {
        ssize_t r = read(ar->fd, (uint8_t*)buf + total, count - total);
        if (r < 0) { if (errno == EINTR) continue; return -1; }
        if (r == 0) break;
        total += (size_t)r;
    }
    return (ssize_t)total;
}

static uint16_t read_le16(const uint8_t* p) { return (uint16_t)(p[0] | (p[1] << 8)); }
static uint32_t read_le32(const uint8_t* p) { return p[0] | (p[1] << 8) | (p[2] << 16) | ((uint32_t)p[3] << 24); }

// ---------------------------------------------------------------------------
// Find End-of-Central-Directory record
// ---------------------------------------------------------------------------
static int find_eocd(MiniZipArchive* ar, off64_t file_size, uint32_t* cd_offset_out,
                     uint32_t* cd_size_out, uint16_t* entry_count_out) {
    // Search backwards from end of file for EOCD signature
    int search_len = (int)((file_size < kMaxEOCDSearch) ? file_size : kMaxEOCDSearch);
    uint8_t* buf = (uint8_t*)malloc(search_len);
    if (!buf) return kErrAllocation;

    off64_t search_start = file_size - search_len;
    if (archive_read_at(ar, buf, search_len, search_start) != search_len) {
        free(buf);
        return kErrIO;
    }

    // Scan backwards for the EOCD signature (0x06054b50)
    int eocd_off = -1;
    for (int i = search_len - 22; i >= 0; i--) {
        if (read_le32(buf + i) == kEOCDSignature) {
            eocd_off = i;
            break;
        }
    }
    if (eocd_off < 0) {
        free(buf);
        return kErrInvalidZip;
    }

    const uint8_t* eocd = buf + eocd_off;
    // EOCD layout: sig(4) disk(2) cd_disk(2) disk_entries(2) total_entries(2)
    //              cd_size(4) cd_offset(4) comment_len(2)
    *entry_count_out = read_le16(eocd + 8);
    *cd_size_out     = read_le32(eocd + 12);
    *cd_offset_out   = read_le32(eocd + 16);

    free(buf);
    return kSuccess;
}

// ---------------------------------------------------------------------------
// Internal: initialize archive from fd or memory
// ---------------------------------------------------------------------------
static int init_archive(MiniZipArchive* ar) {
    off64_t file_size;
    if (ar->mem_base) {
        file_size = (off64_t)ar->mem_len;
    } else {
        struct stat st;
        if (fstat(ar->fd, &st) < 0) return kErrIO;
        file_size = st.st_size;
    }

    if (file_size < 22) return kErrInvalidZip;

    int rc = find_eocd(ar, file_size, &ar->cd_offset, &ar->cd_size, &ar->entry_count);
    if (rc != kSuccess) return rc;

    if (ar->cd_size == 0 || (off64_t)(ar->cd_offset + ar->cd_size) > file_size)
        return kErrInvalidZip;

    ar->cd_data = (uint8_t*)malloc(ar->cd_size);
    if (!ar->cd_data) return kErrAllocation;

    if (archive_read_at(ar, ar->cd_data, ar->cd_size, ar->cd_offset) != (ssize_t)ar->cd_size) {
        free(ar->cd_data);
        ar->cd_data = nullptr;
        return kErrIO;
    }

    return kSuccess;
}

// ---------------------------------------------------------------------------
// Public API (C++ signatures matching AOSP zip_archive.h)
// The compiler will produce the exact mangled names expected by libartbase.
// ---------------------------------------------------------------------------

int OpenArchiveFd(int fd, const char* debug_name, ZipArchiveHandle* handle, bool assume_ownership) {
    if (!handle) return kErrInvalidHandle;
    *handle = nullptr;

    MiniZipArchive* ar = (MiniZipArchive*)calloc(1, sizeof(MiniZipArchive));
    if (!ar) return kErrAllocation;

    ar->fd = fd;
    ar->owns_fd = assume_ownership;
    ar->mem_base = nullptr;
    ar->mem_len = 0;
    ar->debug_name = debug_name ? strdup(debug_name) : nullptr;

    int rc = init_archive(ar);
    if (rc != kSuccess) {
        if (ar->owns_fd && ar->fd >= 0) close(ar->fd);
        free(ar->debug_name);
        free(ar);
        return rc;
    }

    *handle = reinterpret_cast<ZipArchiveHandle>(ar);
    return kSuccess;
}

int OpenArchive(const char* filename, ZipArchiveHandle* handle) {
    if (!handle) return kErrInvalidHandle;
    *handle = nullptr;

    int fd = open(filename, O_RDONLY | O_CLOEXEC);
    if (fd < 0) return kErrIO;

    return OpenArchiveFd(fd, filename, handle, true);
}

void CloseArchive(ZipArchiveHandle handle) {
    if (!handle) return;
    MiniZipArchive* ar = reinterpret_cast<MiniZipArchive*>(handle);
    if (ar->owns_fd && ar->fd >= 0) close(ar->fd);
    free(ar->cd_data);
    free(ar->debug_name);
    free(ar);
}

int FindEntry(ZipArchiveHandle handle, std::string_view name, ZipEntry* entry) {
    if (!handle || !entry) return kErrInvalidHandle;
    MiniZipArchive* ar = reinterpret_cast<MiniZipArchive*>(handle);

    const uint8_t* p = ar->cd_data;
    const uint8_t* end = ar->cd_data + ar->cd_size;

    for (uint16_t i = 0; i < ar->entry_count && p + 46 <= end; i++) {
        if (read_le32(p) != kCDEntrySignature) return kErrInvalidZip;

        uint16_t method       = read_le16(p + 10);
        uint32_t mod_time     = read_le32(p + 12); // DOS time + date
        uint32_t crc          = read_le32(p + 16);
        uint32_t comp_size    = read_le32(p + 20);
        uint32_t uncomp_size  = read_le32(p + 24);
        uint16_t name_len     = read_le16(p + 28);
        uint16_t extra_len    = read_le16(p + 30);
        uint16_t comment_len  = read_le16(p + 32);
        uint32_t local_offset = read_le32(p + 42);

        const uint8_t* entry_name = p + 46;
        if (entry_name + name_len > end) return kErrInvalidZip;

        if (name_len == name.size() && memcmp(entry_name, name.data(), name_len) == 0) {
            memset(entry, 0, sizeof(*entry));
            entry->method             = method;
            entry->mod_time           = mod_time;
            entry->crc32              = crc;
            entry->compressed_length  = comp_size;
            entry->uncompressed_length = uncomp_size;
            entry->offset             = (off64_t)local_offset;
            return kSuccess;
        }

        p += 46 + name_len + extra_len + comment_len;
    }

    return kErrEntryNotFound;
}

// Read the actual compressed data offset (skip past local file header)
static off64_t get_data_offset(MiniZipArchive* ar, const ZipEntry* entry) {
    uint8_t local_hdr[30];
    if (archive_read_at(ar, local_hdr, 30, entry->offset) != 30) return -1;
    if (read_le32(local_hdr) != kLocalHeaderSignature) return -1;
    uint16_t local_name_len  = read_le16(local_hdr + 26);
    uint16_t local_extra_len = read_le16(local_hdr + 28);
    return entry->offset + 30 + local_name_len + local_extra_len;
}

int ExtractToMemory(ZipArchiveHandle handle, const ZipEntry* entry,
                    uint8_t* begin, size_t size) {
    if (!handle || !entry || !begin) return kErrInvalidHandle;
    MiniZipArchive* ar = reinterpret_cast<MiniZipArchive*>(handle);
    if (size < entry->uncompressed_length) return kErrBufferTooSmall;

    off64_t data_offset = get_data_offset(ar, entry);
    if (data_offset < 0) return kErrInvalidZip;

    if (entry->method == kMethodStored) {
        if (archive_read_at(ar, begin, entry->uncompressed_length, data_offset)
            != (ssize_t)entry->uncompressed_length) {
            return kErrIO;
        }
        return kSuccess;
    }

    if (entry->method == kMethodDeflate) {
        uint8_t* comp_buf = (uint8_t*)malloc(entry->compressed_length);
        if (!comp_buf) return kErrAllocation;

        if (archive_read_at(ar, comp_buf, entry->compressed_length, data_offset)
            != (ssize_t)entry->compressed_length) {
            free(comp_buf);
            return kErrIO;
        }

        z_stream strm;
        memset(&strm, 0, sizeof(strm));
        strm.next_in   = comp_buf;
        strm.avail_in  = entry->compressed_length;
        strm.next_out  = begin;
        strm.avail_out = (uInt)size;

        // -MAX_WBITS for raw deflate (no zlib/gzip header)
        int zrc = inflateInit2(&strm, -MAX_WBITS);
        if (zrc != Z_OK) {
            free(comp_buf);
            return kErrDecompression;
        }

        zrc = inflate(&strm, Z_FINISH);
        inflateEnd(&strm);
        free(comp_buf);

        if (zrc != Z_STREAM_END) return kErrDecompression;
        if (strm.total_out != entry->uncompressed_length) return kErrDecompression;
        return kSuccess;
    }

    // Unsupported compression method
    return kErrDecompression;
}

int ExtractEntryToFile(ZipArchiveHandle handle, const ZipEntry* entry, int fd) {
    if (!handle || !entry) return kErrInvalidHandle;
    (void)reinterpret_cast<MiniZipArchive*>(handle);  // validate handle type

    uint8_t* buf = (uint8_t*)malloc(entry->uncompressed_length);
    if (!buf) return kErrAllocation;

    int rc = ExtractToMemory(handle, entry, buf, entry->uncompressed_length);
    if (rc != kSuccess) {
        free(buf);
        return rc;
    }

    size_t total = 0;
    while (total < entry->uncompressed_length) {
        ssize_t w = write(fd, buf + total, entry->uncompressed_length - total);
        if (w < 0) {
            if (errno == EINTR) continue;
            free(buf);
            return kErrIO;
        }
        total += (size_t)w;
    }

    free(buf);
    return kSuccess;
}

int GetFileDescriptor(ZipArchiveHandle handle) {
    if (!handle) return -1;
    MiniZipArchive* ar = reinterpret_cast<MiniZipArchive*>(handle);
    return ar->fd;
}

const char* ErrorCodeString(int err) {
    switch (err) {
        case kSuccess:          return "Success";
        case kErrInvalidZip:    return "Invalid ZIP archive";
        case kErrEntryNotFound: return "Entry not found";
        case kErrIO:            return "I/O error";
        case kErrDecompression: return "Decompression error";
        case kErrBufferTooSmall:return "Buffer too small";
        case kErrInvalidHandle: return "Invalid handle";
        case kErrAllocation:    return "Memory allocation failed";
        default:                return "Unknown error";
    }
}

int OpenArchiveFromMemory(const void* addr, size_t len, const char* debug_name,
                          ZipArchiveHandle* handle) {
    if (!handle || !addr) return kErrInvalidHandle;
    *handle = nullptr;

    MiniZipArchive* ar = (MiniZipArchive*)calloc(1, sizeof(MiniZipArchive));
    if (!ar) return kErrAllocation;

    ar->fd = -1;
    ar->owns_fd = false;
    ar->mem_base = (const uint8_t*)addr;
    ar->mem_len = len;
    ar->debug_name = debug_name ? strdup(debug_name) : nullptr;

    int rc = init_archive(ar);
    if (rc != kSuccess) {
        free(ar->debug_name);
        free(ar);
        return rc;
    }

    *handle = reinterpret_cast<ZipArchiveHandle>(ar);
    return kSuccess;
}
