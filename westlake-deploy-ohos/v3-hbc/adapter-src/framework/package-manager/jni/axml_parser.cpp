/*
 * axml_parser.cpp
 *
 * Standalone AXML parser (no libandroidfw dependency). See header for scope.
 */
#include "axml_parser.h"

#include <cstring>
#include <hilog/log.h>

#undef LOG_DOMAIN
#undef LOG_TAG
#define LOG_DOMAIN 0xD001801
#define LOG_TAG    "AxmlParser"

#define AXML_LOGI(...) ((void)0)
#define AXML_LOGE(...) HILOG_ERROR(LOG_CORE, __VA_ARGS__)

namespace oh_adapter {

namespace {

// ---------- AXML chunk type constants (mirror AOSP ResourceTypes.h) ----------
constexpr uint16_t RES_NULL_TYPE              = 0x0000;
constexpr uint16_t RES_STRING_POOL_TYPE       = 0x0001;
constexpr uint16_t RES_TABLE_TYPE             = 0x0002;
constexpr uint16_t RES_XML_TYPE               = 0x0003;
constexpr uint16_t RES_XML_FIRST_CHUNK_TYPE   = 0x0100;
constexpr uint16_t RES_XML_START_NAMESPACE_TYPE = 0x0100;
constexpr uint16_t RES_XML_END_NAMESPACE_TYPE   = 0x0101;
constexpr uint16_t RES_XML_START_ELEMENT_TYPE   = 0x0102;
constexpr uint16_t RES_XML_END_ELEMENT_TYPE     = 0x0103;
constexpr uint16_t RES_XML_CDATA_TYPE           = 0x0104;
constexpr uint16_t RES_XML_LAST_CHUNK_TYPE      = 0x017f;
constexpr uint16_t RES_XML_RESOURCE_MAP_TYPE    = 0x0180;

// String pool flags
constexpr uint32_t SORTED_FLAG = 1 << 0;
constexpr uint32_t UTF8_FLAG   = 1 << 8;

// ---------- Little-endian readers ----------
inline uint16_t read16(const uint8_t* p) { return p[0] | (uint16_t(p[1]) << 8); }
inline uint32_t read32(const uint8_t* p) {
    return p[0] | (uint32_t(p[1]) << 8) | (uint32_t(p[2]) << 16) | (uint32_t(p[3]) << 24);
}

// ---------- AOSP string pool length encoding ----------
// UTF-16: uint16_t length; if high bit set, the next uint16_t holds extra length.
// UTF-8: uint8_t length; if high bit set, next uint8_t holds extra length. Two
// length fields are emitted: the first is UTF-16 character count (we ignore),
// the second is the UTF-8 byte count. AOSP comment: see ResStringPool::stringAt.
static const uint8_t* parseUtf16Len(const uint8_t* p, size_t* outLen) {
    uint16_t v = read16(p);
    p += 2;
    if (v & 0x8000) {
        v = ((v & 0x7fff) << 16) | read16(p);
        p += 2;
    }
    *outLen = v;
    return p;
}
static const uint8_t* parseUtf8Len(const uint8_t* p, size_t* outLen) {
    uint8_t v = *p++;
    if (v & 0x80) {
        v = ((v & 0x7f) << 8) | *p++;
    }
    *outLen = v;
    return p;
}

// UTF-16 → UTF-8, including surrogate pair handling for code points > U+FFFF.
// High surrogate 0xD800-0xDBFF combines with low surrogate 0xDC00-0xDFFF into
// a 21-bit code point that emits as 4-byte UTF-8 (covers emoji, 4-byte CJK,
// historic scripts).
static std::string utf16ToUtf8(const uint16_t* src, size_t cnt) {
    std::string out;
    out.reserve(cnt);
    for (size_t i = 0; i < cnt; ++i) {
        uint32_t c = src[i];
        if (c >= 0xD800 && c <= 0xDBFF && i + 1 < cnt) {
            uint16_t low = src[i + 1];
            if (low >= 0xDC00 && low <= 0xDFFF) {
                c = 0x10000 + (((c - 0xD800) << 10) | (low - 0xDC00));
                ++i;
            }
            // Otherwise leave as lone high surrogate; emit as 3-byte UTF-8 below.
        }
        if (c < 0x80) {
            out.push_back(char(c));
        } else if (c < 0x800) {
            out.push_back(char(0xC0 | (c >> 6)));
            out.push_back(char(0x80 | (c & 0x3F)));
        } else if (c < 0x10000) {
            out.push_back(char(0xE0 | (c >> 12)));
            out.push_back(char(0x80 | ((c >> 6) & 0x3F)));
            out.push_back(char(0x80 | (c & 0x3F)));
        } else {
            out.push_back(char(0xF0 | (c >> 18)));
            out.push_back(char(0x80 | ((c >> 12) & 0x3F)));
            out.push_back(char(0x80 | ((c >> 6) & 0x3F)));
            out.push_back(char(0x80 | (c & 0x3F)));
        }
    }
    return out;
}

}  // namespace

int AxmlParser::setTo(const void* data, size_t size) {
    const uint8_t* p = static_cast<const uint8_t*>(data);
    if (size < 8) {
        AXML_LOGE("AXML buffer too small: %{public}zu", size);
        return -1;
    }
    uint16_t topType       = read16(p);
    uint16_t topHeaderSize = read16(p + 2);
    uint32_t topSize       = read32(p + 4);
    if (topType != RES_XML_TYPE) {
        AXML_LOGE("AXML top chunk type 0x%{public}x is not RES_XML_TYPE", topType);
        return -1;
    }
    if (topSize > size) {
        AXML_LOGE("AXML top size %{public}u exceeds buffer %{public}zu", topSize, size);
        return -1;
    }

    const uint8_t* docEnd = p + topSize;
    const uint8_t* q      = p + topHeaderSize;

    // Walk pre-XML chunks looking for STRING_POOL and RESOURCE_MAP. Stop at the
    // first XML node chunk, which becomes our cursor for next().
    while (q + 8 <= docEnd) {
        uint16_t cType       = read16(q);
        uint16_t cHeaderSize = read16(q + 2);
        uint32_t cSize       = read32(q + 4);
        if (cSize < 8 || q + cSize > docEnd) {
            AXML_LOGE("AXML chunk size invalid at offset %{public}zd", q - p);
            return -1;
        }

        if (cType == RES_STRING_POOL_TYPE) {
            // Header (after generic ResChunk_header):
            //   uint32 stringCount, styleCount, flags, stringsStart, stylesStart
            uint32_t strCount    = read32(q + 8);
            // uint32_t styCount = read32(q + 12);
            uint32_t flags       = read32(q + 16);
            uint32_t strStart    = read32(q + 20);
            // uint32_t styStart  = read32(q + 24);
            isUtf8_ = (flags & UTF8_FLAG) != 0;
            stringOffsets_.resize(strCount);
            const uint8_t* offs = q + cHeaderSize;
            for (uint32_t i = 0; i < strCount; ++i) {
                stringOffsets_[i] = read32(offs + i * 4);
            }
            strings_     = q + strStart;
            stringsSize_ = cSize - strStart;
            stringCache_.assign(strCount, std::string());
            stringCached_.assign(strCount, false);
        } else if (cType == RES_XML_RESOURCE_MAP_TYPE) {
            uint32_t entryCount = (cSize - cHeaderSize) / 4;
            resourceMap_.resize(entryCount);
            const uint8_t* m = q + cHeaderSize;
            for (uint32_t i = 0; i < entryCount; ++i) {
                resourceMap_[i] = read32(m + i * 4);
            }
        } else if (cType >= RES_XML_FIRST_CHUNK_TYPE && cType <= RES_XML_LAST_CHUNK_TYPE) {
            // Found first XML node chunk — stop pre-walk. cur_ points at it.
            cur_ = q;
            end_ = docEnd;
            event_ = EC_START_DOCUMENT;
            return 0;
        }

        q += cSize;
    }

    AXML_LOGE("AXML has no XML chunks after string pool / resource map");
    return -1;
}

const std::string& AxmlParser::getString(int32_t ref) const {
    static const std::string empty;
    if (ref < 0 || static_cast<size_t>(ref) >= stringOffsets_.size()) return empty;
    if (stringCached_[ref]) return stringCache_[ref];

    uint32_t off = stringOffsets_[ref];
    if (off >= stringsSize_) {
        stringCached_[ref] = true;
        return stringCache_[ref];  // empty
    }
    const uint8_t* p = strings_ + off;
    if (isUtf8_) {
        size_t utf16Len = 0;
        size_t utf8Len  = 0;
        p = parseUtf8Len(p, &utf16Len);  // ignored
        p = parseUtf8Len(p, &utf8Len);
        stringCache_[ref].assign(reinterpret_cast<const char*>(p), utf8Len);
    } else {
        size_t cnt = 0;
        p = parseUtf16Len(p, &cnt);
        stringCache_[ref] = utf16ToUtf8(reinterpret_cast<const uint16_t*>(p), cnt);
    }
    stringCached_[ref] = true;
    return stringCache_[ref];
}

AxmlParser::EventCode AxmlParser::next() {
    if (event_ == EC_END_DOCUMENT || event_ == EC_BAD_DOCUMENT) return event_;
    if (cur_ == nullptr) {
        event_ = EC_BAD_DOCUMENT;
        return event_;
    }

    while (cur_ + 8 <= end_) {
        uint16_t cType       = read16(cur_);
        uint16_t cHeaderSize = read16(cur_ + 2);
        uint32_t cSize       = read32(cur_ + 4);
        if (cSize < 8 || cur_ + cSize > end_) {
            AXML_LOGE("AXML node chunk size invalid");
            event_ = EC_BAD_DOCUMENT;
            return event_;
        }

        const uint8_t* nodeStart = cur_;
        cur_ += cSize;  // advance for next call regardless of type

        // Common XML node header: ResChunk_header + uint32 lineNumber + uint32 commentRef.
        // Element-specific extensions follow at nodeStart + cHeaderSize.
        if (cType == RES_XML_START_ELEMENT_TYPE) {
            const uint8_t* ext = nodeStart + cHeaderSize;
            // ResXMLTree_attrExt:
            //   ResStringPool_ref ns, name;          // 8 bytes
            //   uint16_t attributeStart;             // offset from this struct to first attribute (typically 0x14)
            //   uint16_t attributeSize;              // size of each attribute (typically 0x14)
            //   uint16_t attributeCount;
            //   uint16_t idIndex, classIndex, styleIndex;
            int32_t  nameRef  = static_cast<int32_t>(read32(ext + 4));
            uint16_t attrStart = read16(ext + 8);
            uint16_t attrSize  = read16(ext + 10);
            uint16_t attrCount = read16(ext + 12);
            curNameRef_   = nameRef;
            curAttrs_     = ext + attrStart;
            curAttrSize_  = attrSize;
            curAttrCount_ = attrCount;
            event_ = EC_START_TAG;
            return event_;
        }
        if (cType == RES_XML_END_ELEMENT_TYPE) {
            const uint8_t* ext = nodeStart + cHeaderSize;
            // ResXMLTree_endElementExt: ns, name (8 bytes)
            curNameRef_   = static_cast<int32_t>(read32(ext + 4));
            curAttrs_     = nullptr;
            curAttrSize_  = 0;
            curAttrCount_ = 0;
            event_ = EC_END_TAG;
            return event_;
        }
        if (cType == RES_XML_END_NAMESPACE_TYPE || cType == RES_XML_START_NAMESPACE_TYPE ||
            cType == RES_XML_CDATA_TYPE) {
            // Skip — caller does not care about namespaces or CDATA.
            continue;
        }
        // Unknown node type — skip.
    }

    event_ = EC_END_DOCUMENT;
    return event_;
}

const char* AxmlParser::getElementName(size_t* outLen) const {
    if (curNameRef_ < 0) return nullptr;
    const std::string& s = getString(curNameRef_);
    if (outLen) *outLen = s.size();
    return s.c_str();
}

size_t AxmlParser::getAttributeCount() const {
    return curAttrCount_;
}

const char* AxmlParser::getAttributeName(size_t i, size_t* outLen) const {
    if (i >= curAttrCount_ || curAttrs_ == nullptr) return nullptr;
    const uint8_t* a = curAttrs_ + i * curAttrSize_;
    int32_t nameRef = static_cast<int32_t>(read32(a + 4));  // ns(0), name(4)
    const std::string& s = getString(nameRef);
    if (outLen) *outLen = s.size();
    return s.c_str();
}

uint32_t AxmlParser::getAttributeNameResID(size_t i) const {
    if (i >= curAttrCount_ || curAttrs_ == nullptr) return 0;
    const uint8_t* a = curAttrs_ + i * curAttrSize_;
    int32_t nameRef = static_cast<int32_t>(read32(a + 4));
    if (nameRef >= 0 && static_cast<size_t>(nameRef) < resourceMap_.size()) {
        return resourceMap_[nameRef];
    }
    return 0;
}

const char* AxmlParser::getAttributeStringValue(size_t i, size_t* outLen) const {
    if (i >= curAttrCount_ || curAttrs_ == nullptr) return nullptr;
    const uint8_t* a = curAttrs_ + i * curAttrSize_;
    // Layout: ns(0), name(4), rawValue(8), typedValue(12: size 2, res0 1, type 1, data 4)
    int32_t  rawRef = static_cast<int32_t>(read32(a + 8));
    uint8_t  type   = a[12 + 3];
    uint32_t data   = read32(a + 12 + 4);

    int32_t stringRef = -1;
    if (rawRef >= 0) {
        stringRef = rawRef;
    } else if (type == ResValue::TYPE_STRING) {
        stringRef = static_cast<int32_t>(data);
    } else {
        return nullptr;
    }
    const std::string& s = getString(stringRef);
    if (outLen) *outLen = s.size();
    return s.c_str();
}

int AxmlParser::getAttributeValue(size_t i, ResValue* outValue) const {
    if (i >= curAttrCount_ || curAttrs_ == nullptr || outValue == nullptr) return -1;
    const uint8_t* a = curAttrs_ + i * curAttrSize_;
    // typedValue at offset 12: size(2 bytes), res0(1), dataType(1), data(4)
    outValue->size     = uint8_t(read16(a + 12));  // typedValue.size — only low byte significant
    outValue->res0     = a[14];
    outValue->dataType = a[15];
    outValue->data     = read32(a + 16);
    return 0;
}

}  // namespace oh_adapter
