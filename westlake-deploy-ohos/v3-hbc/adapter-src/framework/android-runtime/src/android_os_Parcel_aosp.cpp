// ============================================================================
// android_os_Parcel_aosp.cpp
//
// G2.14u r2 (2026-05-07) — Parcel JNI for liboh_android_runtime.so, fully
// decoupled from libbinder.
//
// DESIGN
// ------
// android.os.Parcel is the central serialization container used by Bundle,
// Intent, AIDL stubs, SurfaceControl, View hierarchy parameters, etc.  AOSP's
// frameworks/base/core/jni/android_os_Parcel.cpp delegates to the C++ Parcel
// class in libbinder (frameworks/native/libs/binder/Parcel.cpp), which in turn
// pulls in IBinder, IPCThreadState, IServiceManager, ProcessState — the entire
// Android binder stack.  OH does not have Android binder; it has its own IPC
// (OHOS::MessageParcel + IRemoteObject), which is incompatible at every level.
//
// adapter rule: this .so must contain ZERO undefined symbols from libbinder.
// We achieve that by:
//   1. Including no <binder/...> or <utils/...> headers — even header-only
//      references to Parcel/IBinder/IPCThreadState would create UND if used.
//   2. Re-implementing the data plane (writeInt/readInt/writeByteArray/
//      marshall/dataSize/etc, ~36 methods) on top of an adapter-local
//      OhParcelData byte-buffer struct — purely intra-process serialization,
//      sufficient for Bundle/Intent round-tripping inside the same VM.
//   3. Stubbing the binder plane (writeStrongBinder/readStrongBinder/
//      markForBinder/writeFileDescriptor/readFileDescriptor/hasFileDescriptors/
//      writeInterfaceToken/enforceInterface/readCallingWorkSourceUid/
//      replaceCallingWorkSourceUid, ~12 methods) — emit a HiLog warning and
//      return a safe default so Java callers don't crash but also don't get
//      a real cross-process binder transaction.
//
// SCOPE OF SUPPORTED USE
// ----------------------
// - In-process Bundle/Intent.writeToParcel + readFromParcel: WORKS
// - SurfaceControl/relayoutWindow Parcel-based parameter passing inside the
//   same process: WORKS (data only)
// - Cross-process binder IPC via Parcel: STUBBED — no transaction occurs.
//   Adapter routes IPC through dedicated OH service adapter classes
//   (ActivityManagerAdapter / WindowSessionAdapter / etc) that don't go
//   through Parcel.
//
// PARCEL WIRE FORMAT
// ------------------
// We mirror AOSP Parcel's on-the-wire layout for primitive types so that a
// Java-side write + read round-trip produces identical bytes:
//   - All writes are 4-byte aligned (PAD_SIZE_4) — int/long/float/double
//     stored little-endian, post-padded with zeros to a multiple of 4.
//   - byteArray: int32 length prefix (-1 = null) + bytes + 0..3 padding.
//   - String8: int32 byte length + UTF-8 bytes + NUL terminator + padding.
//   - String16: int32 char count + UTF-16LE bytes + NUL16 + padding.
//   - blob: int32 length + IN-PLACE flag (0) + bytes (we don't support
//     ashmem-backed blobs; everything goes inline).
//
// ============================================================================

#define LOG_TAG "OH_Parcel"

#include "jni.h"
#include <nativehelper/JNIHelp.h>
#include <hilog/log.h>

#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <vector>

#include "core_jni_helpers.h"

// HiLog domain for adapter logs (project convention).
#ifndef OH_PARCEL_LOG_DOMAIN
#define OH_PARCEL_LOG_DOMAIN 0xD000F00u
#endif

#define PWARN(fmt, ...) \
    HiLogPrint(LOG_CORE, LOG_WARN, OH_PARCEL_LOG_DOMAIN, "OH_Parcel", \
               "[stub] " fmt, ##__VA_ARGS__)

namespace android {

// ----------------------------------------------------------------------------
// OhParcelData — adapter-local Parcel backing store.  No libbinder dependency.
// ----------------------------------------------------------------------------
struct OhParcelData {
    static constexpr uint32_t MAGIC = 0x4F485043u; // 'OHPC'
    uint32_t            magic = MAGIC;
    std::vector<uint8_t> buf;
    int32_t             pos      = 0;     // current read/write cursor
    int32_t             dataSize = 0;     // valid data length
    bool                allowFds  = true;
    bool                sensitive = false;
    bool                forRpc    = false;

    static int32_t pad4(int32_t n) { return (n + 3) & ~3; }

    void ensureCap(int32_t need) {
        if ((int32_t)buf.size() < need) buf.resize(need, 0);
    }

    void advanceWrite(int32_t advanced) {
        pos += advanced;
        if (pos > dataSize) dataSize = pos;
    }

    template <typename T>
    int32_t writePOD(const T& v) {
        const int32_t aligned = pad4(sizeof(T));
        ensureCap(pos + aligned);
        std::memcpy(buf.data() + pos, &v, sizeof(T));
        if (aligned > (int32_t)sizeof(T)) {
            std::memset(buf.data() + pos + sizeof(T), 0, aligned - sizeof(T));
        }
        advanceWrite(aligned);
        return 0;
    }

    template <typename T>
    T readPOD() {
        const int32_t aligned = pad4(sizeof(T));
        if (pos + (int32_t)sizeof(T) > dataSize) return T{};
        T v;
        std::memcpy(&v, buf.data() + pos, sizeof(T));
        pos += aligned;
        return v;
    }

    // Write `len` raw bytes at cursor + 0..3 zero padding to keep 4-byte align.
    int32_t writeBytes(const void* src, int32_t len) {
        if (len < 0) return -22 /* BAD_VALUE */;
        const int32_t aligned = pad4(len);
        ensureCap(pos + aligned);
        if (src && len > 0) {
            std::memcpy(buf.data() + pos, src, len);
        }
        if (aligned > len) {
            std::memset(buf.data() + pos + len, 0, aligned - len);
        }
        advanceWrite(aligned);
        return 0;
    }

    // Returns pointer to in-buffer data (caller must copy out before next write
    // because subsequent writes may resize the vector and invalidate it).
    const void* readBytes(int32_t len) {
        if (len < 0 || pos + len > dataSize) return nullptr;
        const void* p = buf.data() + pos;
        pos += pad4(len);
        return p;
    }

    // writeInplace: reserve `len` bytes (4-byte aligned) and return a writable
    // pointer into the buffer.  Caller fills it in.  Pointer remains valid
    // until next write/resize.
    void* writeInplace(int32_t len) {
        if (len < 0) return nullptr;
        const int32_t aligned = pad4(len);
        ensureCap(pos + aligned);
        void* p = buf.data() + pos;
        if (aligned > len) {
            std::memset((uint8_t*)p + len, 0, aligned - len);
        }
        advanceWrite(aligned);
        return p;
    }
};

static OhParcelData* asParcel(jlong nativePtr) {
    auto* p = reinterpret_cast<OhParcelData*>(nativePtr);
    if (!p || p->magic != OhParcelData::MAGIC) return nullptr;
    return p;
}

// ----------------------------------------------------------------------------
// Java-side offset cache (for register_android_os_Parcel)
// ----------------------------------------------------------------------------
static struct parcel_offsets_t {
    jclass     clazz;
    jfieldID   mNativePtr;
    jmethodID  obtain;
    jmethodID  recycle;
} gParcelOffsets;

// ============================================================================
// Lifecycle
// ============================================================================

static jlong android_os_Parcel_create(JNIEnv* /*env*/, jclass /*clazz*/) {
    auto* p = new (std::nothrow) OhParcelData();
    return reinterpret_cast<jlong>(p);
}

static void android_os_Parcel_destroy(JNIEnv* /*env*/, jclass /*clazz*/, jlong nativePtr) {
    auto* p = reinterpret_cast<OhParcelData*>(nativePtr);
    delete p;
}

static void android_os_Parcel_freeBuffer(JNIEnv* /*env*/, jclass /*clazz*/, jlong nativePtr) {
    auto* p = asParcel(nativePtr);
    if (!p) return;
    p->buf.clear();
    p->pos      = 0;
    p->dataSize = 0;
}

// ============================================================================
// Cursor / size accessors
// ============================================================================

static jint  android_os_Parcel_dataSize    (jlong np) { auto* p = asParcel(np); return p ? p->dataSize        : 0; }
static jint  android_os_Parcel_dataAvail   (jlong np) { auto* p = asParcel(np); return p ? (p->dataSize - p->pos) : 0; }
static jint  android_os_Parcel_dataPosition(jlong np) { auto* p = asParcel(np); return p ? p->pos             : 0; }
static jint  android_os_Parcel_dataCapacity(jlong np) { auto* p = asParcel(np); return p ? (jint)p->buf.size() : 0; }

static void android_os_Parcel_setDataSize(JNIEnv* /*env*/, jclass /*clazz*/, jlong np, jint size) {
    auto* p = asParcel(np);
    if (!p || size < 0) return;
    p->ensureCap(size);
    p->dataSize = size;
    if (p->pos > size) p->pos = size;
}

static void android_os_Parcel_setDataPosition(jlong np, jint pos) {
    auto* p = asParcel(np);
    if (!p || pos < 0) return;
    p->pos = pos;
}

static void android_os_Parcel_setDataCapacity(JNIEnv* /*env*/, jclass /*clazz*/, jlong np, jint size) {
    auto* p = asParcel(np);
    if (!p || size < 0) return;
    if ((int32_t)p->buf.size() < size) p->buf.resize(size, 0);
}

// ============================================================================
// Flags
// ============================================================================

static void android_os_Parcel_markSensitive(jlong np) {
    auto* p = asParcel(np);
    if (p) p->sensitive = true;
}

static jboolean android_os_Parcel_isForRpc(jlong np) {
    auto* p = asParcel(np);
    return (p && p->forRpc) ? JNI_TRUE : JNI_FALSE;
}

static jboolean android_os_Parcel_pushAllowFds(jlong np, jboolean allowFds) {
    auto* p = asParcel(np);
    if (!p) return JNI_TRUE;
    bool prev = p->allowFds;
    p->allowFds = (allowFds == JNI_TRUE);
    return prev ? JNI_TRUE : JNI_FALSE;
}

static void android_os_Parcel_restoreAllowFds(jlong np, jboolean lastValue) {
    auto* p = asParcel(np);
    if (p) p->allowFds = (lastValue == JNI_TRUE);
}

// ============================================================================
// Primitive writes
// ============================================================================

static jint android_os_Parcel_writeInt(jlong np, jint val) {
    auto* p = asParcel(np); return p ? p->writePOD<int32_t>(val) : 0;
}
static jint android_os_Parcel_writeLong(jlong np, jlong val) {
    auto* p = asParcel(np); return p ? p->writePOD<int64_t>(val) : 0;
}
static jint android_os_Parcel_writeFloat(jlong np, jfloat val) {
    auto* p = asParcel(np); return p ? p->writePOD<float>(val) : 0;
}
static jint android_os_Parcel_writeDouble(jlong np, jdouble val) {
    auto* p = asParcel(np); return p ? p->writePOD<double>(val) : 0;
}

static void android_os_Parcel_nativeSignalExceptionForError(JNIEnv* env, jclass /*clazz*/, jint err) {
    char buf[64];
    std::snprintf(buf, sizeof(buf), "Parcel adapter status_t=%d", (int)err);
    jniThrowException(env, "java/lang/RuntimeException", buf);
}

// ============================================================================
// Primitive reads
// ============================================================================

static jint   android_os_Parcel_readInt   (jlong np) { auto* p = asParcel(np); return p ? p->readPOD<int32_t>() : 0; }
static jlong  android_os_Parcel_readLong  (jlong np) { auto* p = asParcel(np); return p ? p->readPOD<int64_t>() : 0; }
static jfloat android_os_Parcel_readFloat (jlong np) { auto* p = asParcel(np); return p ? p->readPOD<float>()   : 0; }
static jdouble android_os_Parcel_readDouble(jlong np) { auto* p = asParcel(np); return p ? p->readPOD<double>() : 0; }

// ============================================================================
// Byte arrays / blobs
// ============================================================================

static void android_os_Parcel_writeByteArray(JNIEnv* env, jclass /*clazz*/,
        jlong np, jobject data, jint offset, jint length) {
    auto* p = asParcel(np);
    if (!p) return;
    p->writePOD<int32_t>(length);
    if (length > 0 && data != nullptr) {
        void* dest = p->writeInplace(length);
        if (dest) {
            jbyte* ar = (jbyte*)env->GetPrimitiveArrayCritical((jarray)data, 0);
            if (ar) {
                std::memcpy(dest, ar + offset, length);
                env->ReleasePrimitiveArrayCritical((jarray)data, ar, 0);
            }
        }
    }
}

static void android_os_Parcel_writeBlob(JNIEnv* env, jclass /*clazz*/,
        jlong np, jobject data, jint offset, jint length) {
    auto* p = asParcel(np);
    if (!p) return;
    if (data == nullptr) {
        p->writePOD<int32_t>(-1);
        return;
    }
    p->writePOD<int32_t>(length);
    // Inline-only: write a 0 flag (BLOB_INPLACE = 0) then bytes.
    // AOSP's BLOB_INPLACE=0 / BLOB_ASHMEM_IMMUTABLE=1 distinction; we always
    // inline so readers must accept flag=0 + raw bytes.
    p->writePOD<int32_t>(0);
    void* dest = p->writeInplace(length);
    if (dest) {
        jbyte* ar = (jbyte*)env->GetPrimitiveArrayCritical((jarray)data, 0);
        if (ar) {
            std::memcpy(dest, ar + offset, length);
            env->ReleasePrimitiveArrayCritical((jarray)data, ar, 0);
        } else {
            std::memset(dest, 0, length);
        }
    }
}

static jbyteArray android_os_Parcel_createByteArray(JNIEnv* env, jclass /*clazz*/, jlong np) {
    auto* p = asParcel(np);
    if (!p) return nullptr;
    int32_t len = p->readPOD<int32_t>();
    if (len < 0) return nullptr;
    if (len > p->dataSize - p->pos + (int32_t)sizeof(int32_t)) return nullptr;
    jbyteArray ret = env->NewByteArray(len);
    if (ret && len > 0) {
        const void* src = p->readBytes(len);
        if (src) {
            jbyte* dst = (jbyte*)env->GetPrimitiveArrayCritical(ret, 0);
            if (dst) {
                std::memcpy(dst, src, len);
                env->ReleasePrimitiveArrayCritical(ret, dst, 0);
            }
        }
    }
    return ret;
}

static jboolean android_os_Parcel_readByteArray(JNIEnv* env, jclass /*clazz*/,
        jlong np, jobject dest, jint destLen) {
    auto* p = asParcel(np);
    if (!p) return JNI_FALSE;
    int32_t len = p->readPOD<int32_t>();
    if (len != destLen || len < 0) return JNI_FALSE;
    const void* src = p->readBytes(len);
    if (!src) return JNI_FALSE;
    jbyte* ar = (jbyte*)env->GetPrimitiveArrayCritical((jarray)dest, 0);
    if (!ar) return JNI_FALSE;
    std::memcpy(ar, src, len);
    env->ReleasePrimitiveArrayCritical((jarray)dest, ar, 0);
    return JNI_TRUE;
}

static jbyteArray android_os_Parcel_readBlob(JNIEnv* env, jclass /*clazz*/, jlong np) {
    auto* p = asParcel(np);
    if (!p) return nullptr;
    int32_t len = p->readPOD<int32_t>();
    if (len < 0) return nullptr;
    int32_t flag = p->readPOD<int32_t>();
    (void)flag;
    jbyteArray ret = env->NewByteArray(len);
    if (ret && len > 0) {
        const void* src = p->readBytes(len);
        if (src) {
            jbyte* dst = (jbyte*)env->GetPrimitiveArrayCritical(ret, 0);
            if (dst) {
                std::memcpy(dst, src, len);
                env->ReleasePrimitiveArrayCritical(ret, dst, 0);
            }
        }
    }
    return ret;
}

// ============================================================================
// Strings
// ============================================================================

static void android_os_Parcel_writeString8(JNIEnv* env, jclass /*clazz*/,
        jlong np, jstring val) {
    auto* p = asParcel(np);
    if (!p) return;
    if (!val) { p->writePOD<int32_t>(-1); return; }
    const jsize utf8Len = env->GetStringUTFLength(val);
    p->writePOD<int32_t>(utf8Len);
    char* dst = (char*)p->writeInplace(utf8Len + 1);
    if (dst) {
        const jsize len = env->GetStringLength(val);
        env->GetStringUTFRegion(val, 0, len, dst);
        dst[utf8Len] = 0;
    }
}

static void android_os_Parcel_writeString16(JNIEnv* env, jclass /*clazz*/,
        jlong np, jstring val) {
    auto* p = asParcel(np);
    if (!p) return;
    if (!val) { p->writePOD<int32_t>(-1); return; }
    const jsize len = env->GetStringLength(val);
    p->writePOD<int32_t>(len);
    char* dst = (char*)p->writeInplace((len + 1) * sizeof(jchar));
    if (dst) {
        env->GetStringRegion(val, 0, len, reinterpret_cast<jchar*>(dst));
        // NUL-terminate
        ((jchar*)dst)[len] = 0;
    }
}

static jstring android_os_Parcel_readString8(JNIEnv* env, jclass /*clazz*/, jlong np) {
    auto* p = asParcel(np);
    if (!p) return nullptr;
    int32_t len = p->readPOD<int32_t>();
    if (len < 0) return nullptr;
    const char* src = (const char*)p->readBytes(len + 1);
    if (!src) return nullptr;
    return env->NewStringUTF(src);
}

static jstring android_os_Parcel_readString16(JNIEnv* env, jclass /*clazz*/, jlong np) {
    auto* p = asParcel(np);
    if (!p) return nullptr;
    int32_t len = p->readPOD<int32_t>();
    if (len < 0) return nullptr;
    const jchar* src = (const jchar*)p->readBytes((len + 1) * sizeof(jchar));
    if (!src) return nullptr;
    return env->NewString(src, len);
}

// ============================================================================
// Marshall / unmarshall / compare / appendFrom
// ============================================================================

static jbyteArray android_os_Parcel_marshall(JNIEnv* env, jclass /*clazz*/, jlong np) {
    auto* p = asParcel(np);
    if (!p) return nullptr;
    jbyteArray ret = env->NewByteArray(p->dataSize);
    if (ret && p->dataSize > 0) {
        jbyte* dst = (jbyte*)env->GetPrimitiveArrayCritical(ret, 0);
        if (dst) {
            std::memcpy(dst, p->buf.data(), p->dataSize);
            env->ReleasePrimitiveArrayCritical(ret, dst, 0);
        }
    }
    return ret;
}

static void android_os_Parcel_unmarshall(JNIEnv* env, jclass /*clazz*/,
        jlong np, jbyteArray data, jint offset, jint length) {
    auto* p = asParcel(np);
    if (!p || length < 0) return;
    p->ensureCap(length);
    p->dataSize = length;
    p->pos      = 0;
    if (length > 0) {
        jbyte* src = (jbyte*)env->GetPrimitiveArrayCritical(data, 0);
        if (src) {
            std::memcpy(p->buf.data(), src + offset, length);
            env->ReleasePrimitiveArrayCritical(data, src, 0);
        }
    }
}

static jint android_os_Parcel_compareData(JNIEnv* /*env*/, jclass /*clazz*/,
        jlong thisNp, jlong otherNp) {
    auto* a = asParcel(thisNp);
    auto* b = asParcel(otherNp);
    if (!a || !b) return -1;
    const int32_t n = (a->dataSize < b->dataSize) ? a->dataSize : b->dataSize;
    int diff = (n > 0) ? std::memcmp(a->buf.data(), b->buf.data(), n) : 0;
    if (diff != 0) return diff;
    return a->dataSize - b->dataSize;
}

static jboolean android_os_Parcel_compareDataInRange(JNIEnv* /*env*/, jclass /*clazz*/,
        jlong thisNp, jint thisOffset, jlong otherNp, jint otherOffset, jint length) {
    auto* a = asParcel(thisNp);
    auto* b = asParcel(otherNp);
    if (!a || !b) return JNI_FALSE;
    if (thisOffset + length > a->dataSize) return JNI_FALSE;
    if (otherOffset + length > b->dataSize) return JNI_FALSE;
    if (length <= 0) return JNI_TRUE;
    return (std::memcmp(a->buf.data() + thisOffset,
                        b->buf.data() + otherOffset,
                        length) == 0) ? JNI_TRUE : JNI_FALSE;
}

static void android_os_Parcel_appendFrom(JNIEnv* /*env*/, jclass /*clazz*/,
        jlong thisNp, jlong otherNp, jint offset, jint length) {
    auto* a = asParcel(thisNp);
    auto* b = asParcel(otherNp);
    if (!a || !b || length < 0) return;
    if (offset + length > b->dataSize) return;
    a->writeBytes(b->buf.data() + offset, length);
}

// ============================================================================
// Global counters (process-wide stats — return zeros)
// ============================================================================

static jlong android_os_Parcel_getGlobalAllocSize  (JNIEnv*, jclass) { return 0; }
static jlong android_os_Parcel_getGlobalAllocCount (JNIEnv*, jclass) { return 0; }
static jlong android_os_Parcel_getOpenAshmemSize   (jlong /*np*/)    { return 0; }

// ============================================================================
// Binder-touching methods — STUBBED.
// All emit a HiLog warning on first hit, return safe default, no UND on libbinder.
// ============================================================================

static void android_os_Parcel_markForBinder(JNIEnv* /*env*/, jclass /*clazz*/,
        jlong /*np*/, jobject /*binder*/) {
    PWARN("markForBinder: no binder bridge in adapter");
}

static void android_os_Parcel_writeStrongBinder(JNIEnv* /*env*/, jclass /*clazz*/,
        jlong np, jobject /*object*/) {
    PWARN("writeStrongBinder: writing 0-token placeholder");
    auto* p = asParcel(np);
    if (p) p->writePOD<int32_t>(0); // null binder marker
}

static jobject android_os_Parcel_readStrongBinder(JNIEnv* /*env*/, jclass /*clazz*/,
        jlong np) {
    auto* p = asParcel(np);
    if (p) (void)p->readPOD<int32_t>(); // consume the placeholder we wrote
    PWARN("readStrongBinder: returning null (no binder bridge)");
    return nullptr;
}

static void android_os_Parcel_writeFileDescriptor(JNIEnv* /*env*/, jclass /*clazz*/,
        jlong /*np*/, jobject /*object*/) {
    PWARN("writeFileDescriptor: FD-via-binder not supported");
}

static jobject android_os_Parcel_readFileDescriptor(JNIEnv* /*env*/, jclass /*clazz*/,
        jlong /*np*/) {
    PWARN("readFileDescriptor: returning null");
    return nullptr;
}

static jboolean android_os_Parcel_hasFileDescriptors(jlong /*np*/) {
    return JNI_FALSE;
}

static jboolean android_os_Parcel_hasFileDescriptorsInRange(JNIEnv* /*env*/, jclass /*clazz*/,
        jlong /*np*/, jint /*offset*/, jint /*length*/) {
    return JNI_FALSE;
}

static void android_os_Parcel_writeInterfaceToken(JNIEnv* /*env*/, jclass /*clazz*/,
        jlong /*np*/, jstring /*name*/) {
    PWARN("writeInterfaceToken: no IPC, ignored");
}

static void android_os_Parcel_enforceInterface(JNIEnv* /*env*/, jclass /*clazz*/,
        jlong /*np*/, jstring /*name*/) {
    // Silently accept — Java side calling this in adapter context indicates
    // an AIDL-stub that wasn't routed to the OH IPC adapter; logging once
    // is fine but spamming on every call (e.g. AIDL polling) is not.
}

static jint android_os_Parcel_readCallingWorkSourceUid(jlong /*np*/) {
    return -1; // IPCThreadState::kUnsetWorkSource
}

static jboolean android_os_Parcel_replaceCallingWorkSourceUid(jlong /*np*/, jint /*uid*/) {
    return JNI_FALSE;
}

// ----------------------------------------------------------------------------
// JNI registration table (signatures unchanged from AOSP; binder methods point
// at the stubs above so RegisterNatives still resolves all entries).
// ----------------------------------------------------------------------------
static const JNINativeMethod gParcelMethods[] = {
    // @CriticalNative
    {"nativeMarkSensitive",       "(J)V", (void*)android_os_Parcel_markSensitive},
    // @FastNative
    {"nativeMarkForBinder",       "(JLandroid/os/IBinder;)V", (void*)android_os_Parcel_markForBinder},
    // @CriticalNative
    {"nativeIsForRpc",            "(J)Z", (void*)android_os_Parcel_isForRpc},
    // @CriticalNative
    {"nativeDataSize",            "(J)I", (void*)android_os_Parcel_dataSize},
    // @CriticalNative
    {"nativeDataAvail",           "(J)I", (void*)android_os_Parcel_dataAvail},
    // @CriticalNative
    {"nativeDataPosition",        "(J)I", (void*)android_os_Parcel_dataPosition},
    // @CriticalNative
    {"nativeDataCapacity",        "(J)I", (void*)android_os_Parcel_dataCapacity},
    // @FastNative
    {"nativeSetDataSize",         "(JI)V", (void*)android_os_Parcel_setDataSize},
    // @CriticalNative
    {"nativeSetDataPosition",     "(JI)V", (void*)android_os_Parcel_setDataPosition},
    // @FastNative
    {"nativeSetDataCapacity",     "(JI)V", (void*)android_os_Parcel_setDataCapacity},

    // @CriticalNative
    {"nativePushAllowFds",        "(JZ)Z", (void*)android_os_Parcel_pushAllowFds},
    // @CriticalNative
    {"nativeRestoreAllowFds",     "(JZ)V", (void*)android_os_Parcel_restoreAllowFds},

    {"nativeWriteByteArray",      "(J[BII)V", (void*)android_os_Parcel_writeByteArray},
    {"nativeWriteBlob",           "(J[BII)V", (void*)android_os_Parcel_writeBlob},
    // @CriticalNative
    {"nativeWriteInt",            "(JI)I", (void*)android_os_Parcel_writeInt},
    // @CriticalNative
    {"nativeWriteLong",           "(JJ)I", (void*)android_os_Parcel_writeLong},
    // @CriticalNative
    {"nativeWriteFloat",          "(JF)I", (void*)android_os_Parcel_writeFloat},
    // @CriticalNative
    {"nativeWriteDouble",         "(JD)I", (void*)android_os_Parcel_writeDouble},
    {"nativeSignalExceptionForError", "(I)V", (void*)android_os_Parcel_nativeSignalExceptionForError},
    // @FastNative
    {"nativeWriteString8",        "(JLjava/lang/String;)V", (void*)android_os_Parcel_writeString8},
    // @FastNative
    {"nativeWriteString16",       "(JLjava/lang/String;)V", (void*)android_os_Parcel_writeString16},
    // @FastNative
    {"nativeWriteStrongBinder",   "(JLandroid/os/IBinder;)V", (void*)android_os_Parcel_writeStrongBinder},
    // @FastNative
    {"nativeWriteFileDescriptor", "(JLjava/io/FileDescriptor;)V", (void*)android_os_Parcel_writeFileDescriptor},

    {"nativeCreateByteArray",     "(J)[B", (void*)android_os_Parcel_createByteArray},
    {"nativeReadByteArray",       "(J[BI)Z", (void*)android_os_Parcel_readByteArray},
    {"nativeReadBlob",            "(J)[B", (void*)android_os_Parcel_readBlob},
    // @CriticalNative
    {"nativeReadInt",             "(J)I", (void*)android_os_Parcel_readInt},
    // @CriticalNative
    {"nativeReadLong",            "(J)J", (void*)android_os_Parcel_readLong},
    // @CriticalNative
    {"nativeReadFloat",           "(J)F", (void*)android_os_Parcel_readFloat},
    // @CriticalNative
    {"nativeReadDouble",          "(J)D", (void*)android_os_Parcel_readDouble},
    // @FastNative
    {"nativeReadString8",         "(J)Ljava/lang/String;", (void*)android_os_Parcel_readString8},
    // @FastNative
    {"nativeReadString16",        "(J)Ljava/lang/String;", (void*)android_os_Parcel_readString16},
    // @FastNative
    {"nativeReadStrongBinder",    "(J)Landroid/os/IBinder;", (void*)android_os_Parcel_readStrongBinder},
    // @FastNative
    {"nativeReadFileDescriptor",  "(J)Ljava/io/FileDescriptor;", (void*)android_os_Parcel_readFileDescriptor},

    {"nativeCreate",              "()J", (void*)android_os_Parcel_create},
    {"nativeFreeBuffer",          "(J)V", (void*)android_os_Parcel_freeBuffer},
    {"nativeDestroy",             "(J)V", (void*)android_os_Parcel_destroy},

    {"nativeMarshall",            "(J)[B", (void*)android_os_Parcel_marshall},
    {"nativeUnmarshall",          "(J[BII)V", (void*)android_os_Parcel_unmarshall},
    {"nativeCompareData",         "(JJ)I", (void*)android_os_Parcel_compareData},
    {"nativeCompareDataInRange",  "(JIJII)Z", (void*)android_os_Parcel_compareDataInRange},
    {"nativeAppendFrom",          "(JJII)V", (void*)android_os_Parcel_appendFrom},
    // @CriticalNative
    {"nativeHasFileDescriptors",  "(J)Z", (void*)android_os_Parcel_hasFileDescriptors},
    {"nativeHasFileDescriptorsInRange", "(JII)Z", (void*)android_os_Parcel_hasFileDescriptorsInRange},
    {"nativeWriteInterfaceToken", "(JLjava/lang/String;)V", (void*)android_os_Parcel_writeInterfaceToken},
    {"nativeEnforceInterface",    "(JLjava/lang/String;)V", (void*)android_os_Parcel_enforceInterface},

    {"getGlobalAllocSize",        "()J", (void*)android_os_Parcel_getGlobalAllocSize},
    {"getGlobalAllocCount",       "()J", (void*)android_os_Parcel_getGlobalAllocCount},

    // @CriticalNative
    {"nativeGetOpenAshmemSize",       "(J)J", (void*)android_os_Parcel_getOpenAshmemSize},

    // @CriticalNative
    {"nativeReadCallingWorkSourceUid", "(J)I", (void*)android_os_Parcel_readCallingWorkSourceUid},
    // @CriticalNative
    {"nativeReplaceCallingWorkSourceUid", "(JI)Z", (void*)android_os_Parcel_replaceCallingWorkSourceUid},
};

const char* const kParcelPathName = "android/os/Parcel";

int register_android_os_Parcel(JNIEnv* env) {
    jclass clazz = FindClassOrDie(env, kParcelPathName);
    if (!clazz) return -1;

    gParcelOffsets.clazz      = MakeGlobalRefOrDie(env, clazz);
    gParcelOffsets.mNativePtr = GetFieldIDOrDie(env, clazz, "mNativePtr", "J");
    gParcelOffsets.obtain     = GetStaticMethodIDOrDie(env, clazz, "obtain", "()Landroid/os/Parcel;");
    gParcelOffsets.recycle    = GetMethodIDOrDie(env, clazz, "recycle", "()V");

    return RegisterMethodsOrDie(env, kParcelPathName, gParcelMethods, NELEM(gParcelMethods));
}

}  // namespace android
