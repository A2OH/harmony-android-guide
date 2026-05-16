/*
 * oh_minikin_shim.cpp
 *
 * Provides minimal real implementations of minikin::* classes used by
 * libhwui's text rendering path. Bridges to OH_Drawing_Font / OH_Drawing_TextBlob
 * NDK API for actual glyph metrics and shaping.
 *
 * Coverage:
 *   - minikin::FontStyle / FontVariation / FontFakery / MinikinRect / MinikinExtent
 *   - minikin::MinikinPaint
 *   - minikin::MinikinFont (abstract base — provides default implementations)
 *   - minikin::FontCollection (creates default OH font collection)
 *   - minikin::Layout (single-line layout via OH_Drawing_Font glyph metrics)
 *   - minikin::Hyphenator (no-op stub — Hello World doesn't need hyphenation)
 *
 * Build:
 *   $CXX --target=arm-linux-ohos -shared -fPIC -std=c++17 \
 *        -o liboh_minikin_shim.so oh_minikin_shim.cpp \
 *        -lnative_drawing
 */
#include <atomic>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <memory>
#include <string>
#include <vector>

/* G2.14au probe: use fprintf(stderr) so output goes to the child stderr
 * redirect file (no extra NEEDED dependency that could fail dlopen). */
#define G214AU_LOGI(fmt, ...) \
    std::fprintf(stderr, "[G214au_Mnk] " fmt "\n", ##__VA_ARGS__)

/* OH Drawing NDK includes */
extern "C" {
typedef struct OH_Drawing_Font OH_Drawing_Font;
typedef struct OH_Drawing_Typeface OH_Drawing_Typeface;
typedef struct OH_Drawing_TextBlob OH_Drawing_TextBlob;

OH_Drawing_Font *OH_Drawing_FontCreate(void);
void OH_Drawing_FontDestroy(OH_Drawing_Font *font);
void OH_Drawing_FontSetTextSize(OH_Drawing_Font *font, float textSize);
void OH_Drawing_FontSetTypeface(OH_Drawing_Font *font, OH_Drawing_Typeface *typeface);
OH_Drawing_Typeface *OH_Drawing_TypefaceCreateDefault(void);
void OH_Drawing_TypefaceDestroy(OH_Drawing_Typeface *typeface);
void OH_Drawing_FontGetWidths(const OH_Drawing_Font *font, const uint16_t *glyphs,
                              int count, float *widths);
}

namespace minikin {

/* ============================================================ */
/* Basic types (also defined in our stub headers — must match) */
/* ============================================================ */

struct FontStyle {
    uint16_t weight = 400;
    uint8_t  slant = 0;
    enum Slant : uint8_t { kUpright = 0, kItalic = 1 };
    constexpr FontStyle() = default;
    constexpr FontStyle(uint16_t w, Slant s) : weight(w), slant((uint8_t)s) {}
};

struct FontVariation {
    uint32_t axisTag = 0;
    float value = 0.0f;
    FontVariation() = default;
    FontVariation(uint32_t t, float v) : axisTag(t), value(v) {}
};

struct FontFakery {
    bool isFakeBold = false;
    bool isFakeItalic = false;
};

struct MinikinRect {
    float mLeft = 0;
    float mTop = 0;
    float mRight = 0;
    float mBottom = 0;
};

struct MinikinExtent {
    float ascent = 0;
    float descent = 0;
    float line_gap = 0;
};

class MinikinPaint {
public:
    float size = 14.0f;
    float scaleX = 1.0f;
    float skewX = 0.0f;
    FontStyle fontStyle;
    int32_t letterSpacing = 0;
    int32_t wordSpacing = 0;
    uint32_t paintFlags = 0;
};

/* ============================================================ */
/* MinikinFont — abstract base, provide default impl that uses OH Drawing */
/* ============================================================ */

class MinikinFont {
public:
    MinikinFont() {
        mTypeface = OH_Drawing_TypefaceCreateDefault();
        mFont = OH_Drawing_FontCreate();
        if (mFont && mTypeface) {
            OH_Drawing_FontSetTypeface(mFont, mTypeface);
        }
    }

    virtual ~MinikinFont() {
        if (mFont) OH_Drawing_FontDestroy(mFont);
        if (mTypeface) OH_Drawing_TypefaceDestroy(mTypeface);
    }

    virtual float GetHorizontalAdvance(uint32_t glyph_id, const MinikinPaint &paint,
                                       const FontFakery &fakery) const {
        /* G2.14au probe: sample every 50th call. */
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        if (n == 1 || n % 50 == 0) {
            G214AU_LOGI("MinikinFont::GetHorizontalAdvance #%d glyph=%u size=%.1f mFont=%p",
                        n, glyph_id, paint.size, (void*)mFont);
        }
        if (!mFont) return 0;
        OH_Drawing_FontSetTextSize(mFont, paint.size);
        uint16_t g = (uint16_t)glyph_id;
        float width = 0;
        OH_Drawing_FontGetWidths(mFont, &g, 1, &width);
        return width;
    }

    virtual void GetHorizontalAdvances(uint16_t *glyph_ids, uint32_t count,
                                       const MinikinPaint &paint,
                                       const FontFakery &fakery,
                                       float *outAdvances) const {
        /* G2.14au probe: sample every 50th call. count is total glyphs in run. */
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        if (n == 1 || n % 50 == 0) {
            G214AU_LOGI("MinikinFont::GetHorizontalAdvances #%d count=%u size=%.1f mFont=%p outAdv=%p",
                        n, count, paint.size, (void*)mFont, (void*)outAdvances);
        }
        if (!mFont || !glyph_ids || !outAdvances) return;
        OH_Drawing_FontSetTextSize(mFont, paint.size);
        OH_Drawing_FontGetWidths(mFont, glyph_ids, (int)count, outAdvances);
    }

    virtual void GetBounds(MinikinRect *bounds, uint32_t glyph_id,
                          const MinikinPaint &paint, const FontFakery &fakery) const {
        /* G2.14au probe: sample every 50th call. */
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        if (n == 1 || n % 50 == 0) {
            G214AU_LOGI("MinikinFont::GetBounds #%d glyph=%u size=%.1f",
                        n, glyph_id, paint.size);
        }
        if (!bounds) return;
        /* Approximate: use square advance as bounding box */
        float advance = GetHorizontalAdvance(glyph_id, paint, fakery);
        bounds->mLeft = 0;
        bounds->mTop = -paint.size * 0.8f;
        bounds->mRight = advance;
        bounds->mBottom = paint.size * 0.2f;
    }

    virtual void GetFontExtent(MinikinExtent *extent, const MinikinPaint &paint,
                               const FontFakery &fakery) const {
        if (!extent) return;
        extent->ascent = -paint.size * 0.8f;
        extent->descent = paint.size * 0.2f;
        extent->line_gap = 0;
    }

    virtual const std::string &GetFontPath() const {
        static std::string path = "/system/fonts/HarmonyOS_Sans.ttf";
        return path;
    }

    virtual int GetSourceId() const { return 0; }
    virtual int32_t GetUniqueId() const { return 0; }
    virtual int GetFontIndex() const { return 0; }

    virtual const std::vector<FontVariation> &GetAxes() const {
        static std::vector<FontVariation> empty;
        return empty;
    }

    virtual const void *GetFontData() const { return nullptr; }
    virtual size_t GetFontSize() const { return 0; }

private:
    OH_Drawing_Font *mFont = nullptr;
    OH_Drawing_Typeface *mTypeface = nullptr;
};

/* ============================================================ */
/* FontFamily / FontCollection                                   */
/* ============================================================ */

class FontFamily {
public:
    FontFamily() = default;
};

class Font {
public:
    Font() = default;
};

class FontCollection {
public:
    FontCollection() = default;
    static std::shared_ptr<FontCollection> create() {
        return std::make_shared<FontCollection>();
    }
    std::shared_ptr<FontCollection> createCollectionWithVariation(
        const std::vector<FontVariation> &) const {
        return std::make_shared<FontCollection>();
    }
    bool hasVariationSelector(uint32_t, uint32_t) const { return false; }
};

/* ============================================================ */
/* Layout — minimal single-line layout                          */
/* ============================================================ */

class Layout {
public:
    Layout() = default;

    int nGlyphs = 0;
    std::vector<float> mAdvances;
    std::vector<uint16_t> mGlyphs;
    std::vector<float> mPositions; /* x positions */

    int getGlyphCount() const {
        /* G2.14au probe: sample every 100th call. Tells us whether hwui replay
         * actually asks the Layout for glyph count (= TextView text shaping path).
         * If always 0, text never gets shaped — DisplayList's DrawTextBlob op is
         * effectively a no-op even though the op slot exists. */
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        if (n == 1 || n % 100 == 0) {
            G214AU_LOGI("Layout::getGlyphCount #%d returning=%d positionsSz=%zu",
                        n, nGlyphs, mPositions.size());
        }
        return nGlyphs;
    }
    float getX(int index) const {
        return (index >= 0 && index < (int)mPositions.size()) ? mPositions[index] : 0;
    }
    float getY(int) const { return 0; }
    void getBounds(MinikinRect *bounds) const {
        if (!bounds) return;
        bounds->mLeft = 0;
        bounds->mTop = -16;
        bounds->mRight = mPositions.empty() ? 0 : mPositions.back();
        bounds->mBottom = 4;
    }

    /* Provide advance for index — used by libhwui */
    float getAdvance() const {
        return mPositions.empty() ? 0 : mPositions.back();
    }
};

/* ============================================================ */
/* Hyphenator and other text classes — empty stubs              */
/* ============================================================ */

class Hyphenator {
public:
    Hyphenator() = default;
};

enum class StartHyphenEdit : uint8_t { NO_EDIT = 0 };
enum class EndHyphenEdit : uint8_t { NO_EDIT = 0 };

inline uint32_t packHyphenEdit(StartHyphenEdit s, EndHyphenEdit e) {
    return (uint32_t)(((uint8_t)s << 8) | (uint8_t)e);
}

class LineBreaker {};
class MeasuredText {};
class GraphemeBreak {};

}  /* namespace minikin */

/* ============================================================ */
/* A.7 fix: android::fonts::getNewSourceId()                     */
/*                                                                */
/* Referenced as UND by libhwui's jni/FontFamily.cpp (root       */
/* directory entry in libhwui_static). Defined in AOSP           */
/* frameworks/base/libs/hwui/jni/fonts/Font.cpp, but that file   */
/* cannot be compiled against our stub minikin headers (heavy    */
/* BufferReader/LocaleList dependency). The symbol itself is a   */
/* standalone atomic counter — extracted verbatim below.         */
/* ============================================================ */
#include <atomic>
namespace android {
namespace fonts {
int getNewSourceId() {
    static std::atomic<int> sSourceId = {0};
    return sSourceId++;
}
}  /* namespace fonts */
}  /* namespace android */
