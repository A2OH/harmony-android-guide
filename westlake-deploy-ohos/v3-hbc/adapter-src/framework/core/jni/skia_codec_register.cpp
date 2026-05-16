// ============================================================================
// skia_codec_register.cpp
//
// OH `libskia_canvaskit.z.so` (Skia m133) requires callers to explicitly
// register codecs via `SkCodecs::Register(SkCodecs::Decoder)` before
// `SkCodec::MakeFromStream` can decode any image format.  The 4-arg
// MakeFromStream wrapper used by AOSP libhwui's ImageDecoder.cpp loads from
// an empty global Decoder list when no Register() calls have been made,
// causing every PNG/JPEG/WEBP load to fail with `kUnimplemented`.
//
// Diagnosed 2026-04-29 during HelloWorld setContentView path:
//   - libandroidfw resource lookup OK (xhdpi cab_background_top_mtrl_alpha.9.png)
//   - openRawResource OK (PNG signature 89 50 4E 47 confirmed in stream head)
//   - ImageDecoder.decodeBitmap → "Failed to create image decoder ... 'unimplemented'"
//   - Root cause: SkCodecs::Register never called → empty decoder span.
//
// Called once from adapter JNI_OnLoad before any framework JNI usage.
// ============================================================================

#include <codec/SkBmpDecoder.h>
#include <codec/SkCodec.h>
#include <codec/SkGifDecoder.h>
#include <codec/SkIcoDecoder.h>
#include <codec/SkJpegDecoder.h>
#include <codec/SkPngDecoder.h>
#include <codec/SkWbmpDecoder.h>
#include <codec/SkWebpDecoder.h>
#include <core/SkStream.h>
#include <FrontBufferedStream.h>

#include <cstdio>
#include <cstring>
#include <memory>

namespace adapter {

// Register the standard Skia codec set.  Idempotent — Skia's Register
// implementation uses a global vector but tolerates repeated registrations.
//
// Uses fprintf(stderr) (instead of hilog/__android_log_print) so the trace
// is captured by appspawn-x stderr → test.log, allowing direct verification
// that this function ran.
void RegisterAllSkiaCodecs() {
    fprintf(stderr, "[OH_SkiaCodecRegister] entering RegisterAllSkiaCodecs\n");

    // Direct cross-DSO smoke test: invoke OH SkPngDecoder::IsPng directly with
    // a known-good PNG signature buffer.  If IsPng returns true here, the PLT
    // path adapter→libskia_canvaskit is wired correctly.  If it returns false
    // or crashes, the function pointer registered into OH's global Decoder
    // vector is also broken (same PLT entry), explaining kIncompleteInput.
    {
        unsigned char head[8] = {0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        bool isPngResult = SkPngDecoder::IsPng(head, sizeof(head));
        fprintf(stderr, "[OH_SkiaCodecRegister] direct IsPng(89 50 4E 47 ...) = %d (expected 1)\n",
                isPngResult ? 1 : 0);
    }

    auto pngDecoder = SkPngDecoder::Decoder();
    fprintf(stderr, "[OH_SkiaCodecRegister] SkPngDecoder::Decoder() returned: id.size=%zu data=%p isFormat=%p decode=%p\n",
            pngDecoder.id.size(), pngDecoder.id.data(),
            (void*)pngDecoder.isFormat, (void*)pngDecoder.makeFromStream);

    SkCodecs::Register(pngDecoder);
    fprintf(stderr, "[OH_SkiaCodecRegister]   1/7 png registered\n");
    SkCodecs::Register(SkJpegDecoder::Decoder());
    SkCodecs::Register(SkWebpDecoder::Decoder());
    SkCodecs::Register(SkGifDecoder::Decoder());
    SkCodecs::Register(SkBmpDecoder::Decoder());
    SkCodecs::Register(SkIcoDecoder::Decoder());
    SkCodecs::Register(SkWbmpDecoder::Decoder());
    fprintf(stderr, "[OH_SkiaCodecRegister] all 7 Skia codecs registered\n");

    // In-memory PNG decode smoke test.  Bypasses Java InputStream wrapping
    // and ImageDecoder.cpp's FrontBufferedStream — directly invokes
    // SkCodec::MakeFromStream from adapter native context.  If this returns
    // a valid codec, the cross-DSO MakeFromStream call works and the
    // failure path must be in Java InputStream → SkStream wrapping.  If
    // this also returns kIncompleteInput, the cross-DSO MakeFromStream
    // call itself is broken — adapter→OH Skia binding deeper issue.
    {
        // Minimal valid 1x1 RGBA PNG (67 bytes).  Standard "transparent
        // pixel" PNG, all CRCs correct, well-formed for any PNG decoder.
        static const unsigned char kMinimalPng[] = {
            0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,  // PNG signature
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,  // IHDR length=13
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,  // 1x1
            0x08, 0x06, 0x00, 0x00, 0x00,                    // 8-bit RGBA
            0x1F, 0x15, 0xC4, 0x89,                           // IHDR CRC
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x44, 0x41, 0x54,  // IDAT length=13
            0x78, 0x9C, 0x62, 0x00, 0x01, 0x00, 0x00, 0x05,
            0x00, 0x01, 0x0D, 0x0A, 0x2D, 0xB4,               // IDAT data + CRC
            0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,  // IEND length=0
            0xAE, 0x42, 0x60, 0x82                            // IEND CRC
        };
        fprintf(stderr, "[SkiaSmokeTest] in-memory PNG MakeFromStream test (%zu bytes)\n",
                sizeof(kMinimalPng));
        auto memStream = std::unique_ptr<SkStream>(
                new SkMemoryStream(kMinimalPng, sizeof(kMinimalPng), false));
        SkCodec::Result result = SkCodec::kSuccess;
        auto codec = SkCodec::MakeFromStream(std::move(memStream), &result, nullptr,
                                              SkCodec::SelectionPolicy::kPreferStillImage);
        fprintf(stderr, "[SkiaSmokeTest] MakeFromStream returned: result=%d codec=%p\n",
                static_cast<int>(result), codec.get());
        // Result enum values: 0=Success, 1=IncompleteInput, 2=ErrorInInput,
        // 3=InvalidConversion, 4=InvalidScale, 5=InvalidParameters,
        // 6=InvalidInput, 7=CouldNotRewind, 8=InternalError, 9=Unimplemented
    }

    // Second test: adapter-defined SkStream subclass with only `read` and
    // `isAtEnd` overridden — mimics what AOSP libhwui's JavaInputStreamAdaptor
    // does.  If this fails while SkMemoryStream succeeded, the bug is in the
    // adapter-layer SkStream subclass binding (vtable layout / header version
    // mismatch / cross-DSO virtual dispatch).
    {
        static const unsigned char kMinimalPng[] = {
            0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
            0x08, 0x06, 0x00, 0x00, 0x00,
            0x1F, 0x15, 0xC4, 0x89,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x44, 0x41, 0x54,
            0x78, 0x9C, 0x62, 0x00, 0x01, 0x00, 0x00, 0x05,
            0x00, 0x01, 0x0D, 0x0A, 0x2D, 0xB4,
            0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
            0xAE, 0x42, 0x60, 0x82
        };

        class AdapterTestStream : public SkStream {
        public:
            AdapterTestStream(const void* data, size_t size)
                : fData(static_cast<const unsigned char*>(data)), fSize(size),
                  fPos(0), fIsAtEnd(false) {}
            size_t read(void* buf, size_t size) override {
                size_t avail = fSize - fPos;
                size_t n = size < avail ? size : avail;
                if (buf && n > 0) memcpy(buf, fData + fPos, n);
                fPos += n;
                if (fPos >= fSize) fIsAtEnd = true;
                fprintf(stderr, "[AdapterStream::read(%zu)] returned %zu pos=%zu/%zu\n",
                        size, n, fPos, fSize);
                return n;
            }
            bool isAtEnd() const override { return fIsAtEnd; }
        private:
            const unsigned char* fData;
            size_t fSize;
            size_t fPos;
            bool fIsAtEnd;
        };

        fprintf(stderr, "[SkiaSmokeTest2] adapter-defined SkStream subclass test\n");
        auto adapterStream = std::unique_ptr<SkStream>(
                new AdapterTestStream(kMinimalPng, sizeof(kMinimalPng)));
        SkCodec::Result result2 = SkCodec::kSuccess;
        auto codec2 = SkCodec::MakeFromStream(std::move(adapterStream), &result2, nullptr,
                                                SkCodec::SelectionPolicy::kPreferStillImage);
        fprintf(stderr, "[SkiaSmokeTest2] MakeFromStream returned: result=%d codec=%p\n",
                static_cast<int>(result2), codec2.get());

        // Third test: wrap adapter stream with FrontBufferedStream — exact
        // same composition as ImageDecoder.cpp's nCreateInputStream path.
        // If this succeeds, FrontBufferedStream wraps adapter SkStream OK
        // and the production failure is specifically in JavaInputStreamAdaptor
        // (Java→native data flow), not in the SkStream subclass mechanism.
        fprintf(stderr, "[SkiaSmokeTest3] FrontBufferedStream(AdapterStream) test\n");
        auto innerStream = std::unique_ptr<SkStream>(
                new AdapterTestStream(kMinimalPng, sizeof(kMinimalPng)));
        auto bufferedStream = std::unique_ptr<SkStream>(
                android::skia::FrontBufferedStream::Make(
                        std::move(innerStream), SkCodec::MinBufferedBytesNeeded()).release());
        SkCodec::Result result3 = SkCodec::kSuccess;
        auto codec3 = SkCodec::MakeFromStream(std::move(bufferedStream), &result3, nullptr,
                                                SkCodec::SelectionPolicy::kPreferStillImage);
        fprintf(stderr, "[SkiaSmokeTest3] MakeFromStream returned: result=%d codec=%p\n",
                static_cast<int>(result3), codec3.get());

        // Fourth test: read the actual 229-byte production PNG
        // (cab_background_top_mtrl_alpha.9.png from framework-res.apk) into
        // adapter native memory, wrap with same Adapter+FrontBuffered config,
        // decode.  If this fails while 67B PNG succeeded, the issue is in
        // the specific PNG file decode (corruption / 9-patch chunk handling).
        // If it succeeds, the issue is purely in JavaInputStreamAdaptor's
        // Java-callback path (different code path than adapter native).
        FILE* fp = fopen("/system/android/framework/cab_test.bin", "rb");
        if (!fp) {
            // Fallback: try directly reading the apk's drawable file
            fp = fopen("/system/android/framework/framework-res-test.bin", "rb");
        }
        if (fp) {
            unsigned char realPng[1024];
            size_t n = fread(realPng, 1, sizeof(realPng), fp);
            fclose(fp);
            fprintf(stderr, "[SkiaSmokeTest4] read %zu bytes from disk\n", n);
            auto realStream = std::unique_ptr<SkStream>(
                    new AdapterTestStream(realPng, n));
            auto realBuffered = std::unique_ptr<SkStream>(
                    android::skia::FrontBufferedStream::Make(
                            std::move(realStream), SkCodec::MinBufferedBytesNeeded()).release());
            SkCodec::Result result4 = SkCodec::kSuccess;
            auto codec4 = SkCodec::MakeFromStream(std::move(realBuffered), &result4, nullptr,
                                                    SkCodec::SelectionPolicy::kPreferStillImage);
            fprintf(stderr, "[SkiaSmokeTest4] real PNG MakeFromStream: result=%d codec=%p\n",
                    static_cast<int>(result4), codec4.get());
        } else {
            fprintf(stderr, "[SkiaSmokeTest4] SKIPPED (no test bin file at /system/android/framework/)\n");
        }

        // Test 5: full bitmap decode via SkMemoryStream — matches what
        // ImageDecoder.decodeBitmap does (MakeFromStream + getPixels).  If
        // this succeeds, full decode in adapter native works; if it fails,
        // the same internal Skia path that production uses also fails here.
        {
            fprintf(stderr, "[SkiaSmokeTest5] full getPixels via SkMemoryStream (67-byte minPng)\n");
            auto memStream = std::unique_ptr<SkStream>(
                    new SkMemoryStream(kMinimalPng, sizeof(kMinimalPng), false));
            SkCodec::Result mkResult = SkCodec::kSuccess;
            auto codec5 = SkCodec::MakeFromStream(std::move(memStream), &mkResult, nullptr,
                                                    SkCodec::SelectionPolicy::kPreferStillImage);
            if (!codec5) {
                fprintf(stderr, "[SkiaSmokeTest5] MakeFromStream failed: result=%d\n", (int)mkResult);
            } else {
                SkImageInfo info = codec5->getInfo();
                size_t rowBytes = info.minRowBytes();
                size_t bufSize = rowBytes * info.height();
                fprintf(stderr, "[SkiaSmokeTest5] codec OK: %dx%d, allocating %zu bytes\n",
                        info.width(), info.height(), bufSize);
                std::unique_ptr<unsigned char[]> pixels(new unsigned char[bufSize]);
                SkCodec::Result pxResult = codec5->getPixels(info, pixels.get(), rowBytes);
                fprintf(stderr, "[SkiaSmokeTest5] getPixels result=%d (0=Success)\n", (int)pxResult);
            }
        }

        // Test 6: 229B real PNG + adapter SkStream + FrontBufferedStream + getPixels.
        // True production-equivalent: same data (real cab_background_top_mtrl_alpha.9.png),
        // same FrontBufferedStream wrapping, full getPixels decode.
        FILE* fp2 = fopen("/system/android/framework/cab_test.bin", "rb");
        if (fp2) {
            unsigned char realPng[1024];
            size_t realLen = fread(realPng, 1, sizeof(realPng), fp2);
            fclose(fp2);
            fprintf(stderr, "[SkiaSmokeTest6] 229B real PNG full decode (%zu bytes)\n", realLen);
            auto innerStream6 = std::unique_ptr<SkStream>(
                    new AdapterTestStream(realPng, realLen));
            auto bufferedStream6 = std::unique_ptr<SkStream>(
                    android::skia::FrontBufferedStream::Make(
                            std::move(innerStream6), SkCodec::MinBufferedBytesNeeded()).release());
            SkCodec::Result mkResult6 = SkCodec::kSuccess;
            auto codec6 = SkCodec::MakeFromStream(std::move(bufferedStream6), &mkResult6, nullptr,
                                                    SkCodec::SelectionPolicy::kPreferStillImage);
            if (!codec6) {
                fprintf(stderr, "[SkiaSmokeTest6] MakeFromStream failed: result=%d\n", (int)mkResult6);
            } else {
                SkImageInfo info6 = codec6->getInfo();
                size_t rowBytes6 = info6.minRowBytes();
                size_t bufSize6 = rowBytes6 * info6.height();
                fprintf(stderr, "[SkiaSmokeTest6] codec OK: %dx%d, allocating %zu bytes\n",
                        info6.width(), info6.height(), bufSize6);
                std::unique_ptr<unsigned char[]> pixels6(new unsigned char[bufSize6]);
                SkCodec::Result pxResult6 = codec6->getPixels(info6, pixels6.get(), rowBytes6);
                fprintf(stderr, "[SkiaSmokeTest6] getPixels result=%d (0=Success)\n", (int)pxResult6);
            }
        }
    }
}

}  // namespace adapter
