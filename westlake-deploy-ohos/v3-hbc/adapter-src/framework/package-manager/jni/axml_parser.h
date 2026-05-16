/*
 * axml_parser.h
 *
 * Self-contained AOSP AXML (Android binary XML) parser.
 *
 * Why this exists:
 *   apk_manifest_parser.cpp originally used android::ResXMLTree from libandroidfw,
 *   which transitively requires libbase + libcutils + liblog from /system/android/lib.
 *   Foundation-context dlopen of libapk_installer.so fails because the OH systemscence
 *   namespace doesn't search /system/android/lib. To keep libapk_installer.so loadable
 *   in the OH BMS process without touching the namespace linker config, we drop the
 *   libandroidfw dependency entirely and parse AXML in-house.
 *
 * Scope: only the subset of AXML actually emitted by `aapt2`/`aapt` for
 * AndroidManifest.xml — string pool (UTF-8 or UTF-16, with surrogate pair
 * support) + resource map + START_ELEMENT / END_ELEMENT chunks. CDATA, styles,
 * namespaces, and resource-table reference resolution (TYPE_REFERENCE) are
 * out of scope. If a future call site needs layout-XML or @string/foo
 * dereferencing, expand here rather than re-introducing libandroidfw.
 *
 * API surface mirrors android::ResXMLParser closely enough that the call sites
 * in apk_manifest_parser.cpp swap with minimal edits.
 */
#ifndef OH_ADAPTER_AXML_PARSER_H
#define OH_ADAPTER_AXML_PARSER_H

#include <cstddef>
#include <cstdint>
#include <string>
#include <vector>

namespace oh_adapter {

// Compatibility with android::Res_value common dataType constants.
// Only the codes used by AndroidManifest.xml are listed.
struct ResValue {
    uint8_t  size;       // typically 8
    uint8_t  res0;       // reserved, 0
    uint8_t  dataType;   // see TYPE_*
    uint32_t data;

    enum : uint8_t {
        TYPE_NULL          = 0x00,
        TYPE_REFERENCE     = 0x01,
        TYPE_ATTRIBUTE     = 0x02,
        TYPE_STRING        = 0x03,
        TYPE_FLOAT         = 0x04,
        TYPE_DIMENSION     = 0x05,
        TYPE_FRACTION      = 0x06,
        TYPE_INT_DEC       = 0x10,
        TYPE_INT_HEX       = 0x11,
        TYPE_INT_BOOLEAN   = 0x12,
        TYPE_INT_COLOR_ARGB8 = 0x1c,
        TYPE_INT_COLOR_RGB8  = 0x1d,
        TYPE_INT_COLOR_ARGB4 = 0x1e,
        TYPE_INT_COLOR_RGB4  = 0x1f,
    };
};

class AxmlParser {
public:
    enum EventCode {
        EC_NOT_STARTED = -1,
        EC_BAD_DOCUMENT = 1,
        EC_START_DOCUMENT = 2,
        EC_END_DOCUMENT = 3,
        EC_START_NAMESPACE = 4,
        EC_END_NAMESPACE = 5,
        EC_START_TAG = 6,
        EC_END_TAG = 7,
        EC_TEXT = 8,
    };

    AxmlParser() = default;
    ~AxmlParser() = default;

    // Load a buffer holding a full AXML document. Returns 0 on success.
    int  setTo(const void* data, size_t size);

    // Advance to the next interesting event. Returns the new event code; the
    // parser stays at EC_END_DOCUMENT after the document ends (idempotent).
    EventCode next();

    EventCode getEventType() const { return event_; }

    // Element name (current START/END_TAG event). Returns nullptr if no name.
    // The returned pointer is owned by the parser; caller must NOT free it.
    // The string is decoded to UTF-8 and lives until the next() call.
    const char* getElementName(size_t* outLen = nullptr) const;

    // Attribute API — only valid at START_TAG.
    size_t getAttributeCount() const;
    // Attribute name string (UTF-8). Same lifetime as getElementName().
    const char* getAttributeName(size_t i, size_t* outLen = nullptr) const;
    // Resource ID for attribute (from resource map). 0 if no mapping.
    uint32_t    getAttributeNameResID(size_t i) const;
    // String value as UTF-8. nullptr if attribute is not a TYPE_STRING.
    const char* getAttributeStringValue(size_t i, size_t* outLen = nullptr) const;
    // Typed value (TYPE_INT_*, TYPE_INT_BOOLEAN, etc.). Returns 0 on success,
    // negative on no value.
    int         getAttributeValue(size_t i, ResValue* outValue) const;

private:
    // Parse-time state captured from the document.
    bool isUtf8_ = false;
    std::vector<uint32_t> stringOffsets_;  // offsets into strings_ (entry-relative)
    const uint8_t* strings_ = nullptr;     // pointer to start of strings region
    size_t stringsSize_ = 0;
    std::vector<uint32_t> resourceMap_;    // i -> attribute resource id

    // Cached decoded string pool entries (UTF-8). Lazy: filled on demand.
    mutable std::vector<std::string> stringCache_;
    mutable std::vector<bool> stringCached_;

    // Current XML cursor.
    const uint8_t* cur_ = nullptr;
    const uint8_t* end_ = nullptr;
    EventCode event_ = EC_NOT_STARTED;

    // Element fields populated at START_TAG / END_TAG.
    int32_t curNameRef_ = -1;            // string pool index of element name
    const uint8_t* curAttrs_ = nullptr;  // pointer to first attribute (START_TAG only)
    uint16_t curAttrSize_ = 0;           // size of one attribute entry
    uint16_t curAttrCount_ = 0;          // number of attributes

    // Decode a string from string pool index → UTF-8. Cached.
    const std::string& getString(int32_t ref) const;
};

}  // namespace oh_adapter

#endif  // OH_ADAPTER_AXML_PARSER_H
