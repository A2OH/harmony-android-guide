/*
 * oh_graphic_buffer_producer.cpp
 *
 * Implements OHGraphicBufferProducer: the core adapter bridging Android's
 * IGraphicBufferProducer buffer operations to OH Surface (ProducerSurface).
 *
 * Buffer flow:
 *   dequeueBuffer() -> OH Surface::RequestBuffer()  (get buffer for rendering)
 *   queueBuffer()   -> OH Surface::FlushBuffer()    (submit rendered buffer)
 *   cancelBuffer()  -> OH Surface::CancelBuffer()   (return unused buffer)
 *
 * Zero-copy sharing:
 *   OH SurfaceBuffer::GetBufferHandle()->fd is a standard Linux dmabuf fd,
 *   identical to Android's GraphicBuffer fd. The fd is dup'd and cached per slot.
 *
 * Fence synchronization:
 *   OH SyncFence wraps Linux sync_file fd, same as Android's Fence.
 *   Direct dup() interchange between the two systems.
 *
 * Reference:
 *   OH: graphic_surface/surface/include/surface.h
 *   OH: graphic_surface/surface/include/surface_buffer.h
 *   OH: graphic_surface/interfaces/inner_api/common/buffer_handle.h
 *   OH: graphic_2d/rosen/modules/render_service_base/include/common/rs_common_def.h
 */
#include "oh_graphic_buffer_producer.h"
#include "pixel_format_mapper.h"

#include <android/log.h>
#include <unistd.h>
#include <cerrno>
#include <cstring>

// OH Surface and buffer headers
#include "surface.h"
#include "surface_buffer.h"
#include "sync_fence.h"
#include "buffer_handle.h"

#define LOG_TAG "OH_GBProducer"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

// G2.14ac: unified IPC trace+log macros (buffer cycle is critical for
// use-after-free diagnosis 0xcafe5c06 — every dequeue/queue/cancel must show
// up in hitrace for cross-process timeline correlation).
#include "oh_br_trace.h"

namespace oh_adapter {

OHGraphicBufferProducer::OHGraphicBufferProducer(OHOS::sptr<OHOS::Surface> ohSurface)
    : ohSurface_(ohSurface)
{
    LOGI("OHGraphicBufferProducer created, surface=%p", ohSurface.GetRefPtr());
}

OHGraphicBufferProducer::~OHGraphicBufferProducer() {
    LOGI("OHGraphicBufferProducer destroyed");

    // Close all cached dmabuf fds
    std::lock_guard<std::mutex> lock(mutex_);
    for (auto& [slot, bufSlot] : slots_) {
        if (bufSlot.dmabufFd >= 0) {
            close(bufSlot.dmabufFd);
            bufSlot.dmabufFd = -1;
        }
    }
    slots_.clear();
}

int OHGraphicBufferProducer::dequeueBuffer(int* outSlot, int* outFenceFd,
                                            int32_t width, int32_t height,
                                            int32_t format, uint64_t usage)
{
    OH_BR_IPC_ENTER("GBProducer.dequeueBuffer", "w=%{public}d h=%{public}d fmt=%{public}d usage=0x%{public}llx",
                    width, height, format, (unsigned long long)usage);
    if (!ohSurface_) {
        OH_BR_IPC_FAIL("GBProducer.dequeueBuffer", -1, "OH Surface is null");
        return -1;
    }

    // Convert Android format/usage to OH equivalents
    int32_t ohFormat = toOHPixelFormat(format);
    uint64_t ohUsage = toOHUsage(usage);

    // Configure request
    OHOS::BufferRequestConfig requestConfig = {
        .width = width,
        .height = height,
        .strideAlignment = 8,
        .format = ohFormat,
        .usage = ohUsage,
        .timeout = 0,  // Non-blocking
    };

    // Request buffer from OH Surface (maps to BufferQueue::RequestBuffer)
    OHOS::sptr<OHOS::SurfaceBuffer> ohBuffer = nullptr;
    OHOS::sptr<OHOS::SyncFence> releaseFence = OHOS::SyncFence::INVALID_FENCE;

    OHOS::SurfaceError ret = ohSurface_->RequestBuffer(
        ohBuffer, releaseFence, requestConfig);

    if (ret != OHOS::SURFACE_ERROR_OK || ohBuffer == nullptr) {
        OH_BR_IPC_FAIL("GBProducer.dequeueBuffer", -static_cast<int>(ret),
                       "RequestBuffer failed");
        return -static_cast<int>(ret);
    }

    // Extract sequence number for tracking
    uint32_t seqNum = ohBuffer->GetSeqNum();

    // Get the dmabuf fd from the BufferHandle
    // OH BufferHandle.fd is a standard Linux dmabuf fd, same as Android's GraphicBuffer
    BufferHandle* handle = ohBuffer->GetBufferHandle();
    int dmabufFd = -1;
    if (handle != nullptr && handle->fd >= 0) {
        // Dup the fd so we have our own copy
        dmabufFd = dup(handle->fd);
        if (dmabufFd < 0) {
            LOGE("dequeueBuffer: Failed to dup dmabuf fd: %s", strerror(errno));
        }
    } else {
        LOGW("dequeueBuffer: BufferHandle or fd is invalid");
    }

    // Assign a slot number
    int slot;
    {
        std::lock_guard<std::mutex> lock(mutex_);
        slot = nextSlot_++;

        // Close any previously cached fd for this slot
        auto it = slots_.find(slot);
        if (it != slots_.end() && it->second.dmabufFd >= 0) {
            close(it->second.dmabufFd);
        }

        // Cache the buffer and its info
        BufferSlot& bufSlot = slots_[slot];
        bufSlot.ohBuffer = ohBuffer;
        bufSlot.ohSeqNum = seqNum;
        bufSlot.width = ohBuffer->GetWidth();
        bufSlot.height = ohBuffer->GetHeight();
        bufSlot.format = format;  // Keep Android format for getBufferInfo
        bufSlot.dmabufFd = dmabufFd;
    }

    // Extract fence fd from OH SyncFence
    // OH SyncFence wraps a Linux sync_file fd, identical to Android's Fence
    int fenceFd = -1;
    if (releaseFence != nullptr && releaseFence != OHOS::SyncFence::INVALID_FENCE) {
        int ohFenceFd = releaseFence->Get();
        if (ohFenceFd >= 0) {
            fenceFd = dup(ohFenceFd);
        }
    }

    *outSlot = slot;
    *outFenceFd = fenceFd;

    LOGI("dequeueBuffer: slot=%d, seqNum=%u, fd=%d, fenceFd=%d, size=%dx%d",
         slot, seqNum, dmabufFd, fenceFd, width, height);

    OH_BR_IPC_EXIT("GBProducer.dequeueBuffer", slot);
    return 0;
}

int OHGraphicBufferProducer::queueBuffer(int slot, int fenceFd, int64_t timestamp,
                                          int cropLeft, int cropTop,
                                          int cropRight, int cropBottom)
{
    OH_BR_IPC_ENTER("GBProducer.queueBuffer", "slot=%{public}d fenceFd=%{public}d ts=%{public}lld crop=[%{public}d,%{public}d,%{public}d,%{public}d]",
                    slot, fenceFd, (long long)timestamp,
                    cropLeft, cropTop, cropRight, cropBottom);
    if (!ohSurface_) {
        OH_BR_IPC_FAIL("GBProducer.queueBuffer", -1, "OH Surface is null");
        return -1;
    }

    OHOS::sptr<OHOS::SurfaceBuffer> ohBuffer;
    {
        std::lock_guard<std::mutex> lock(mutex_);
        auto it = slots_.find(slot);
        if (it == slots_.end()) {
            OH_BR_IPC_FAIL("GBProducer.queueBuffer", -1, "Invalid slot %{public}d", slot);
            return -1;
        }
        ohBuffer = it->second.ohBuffer;
    }

    if (ohBuffer == nullptr) {
        OH_BR_IPC_FAIL("GBProducer.queueBuffer", -1, "Buffer for slot %{public}d is null", slot);
        return -1;
    }

    // Build flush config
    OHOS::BufferFlushConfig flushConfig = {
        .damage = {
            .x = cropLeft,
            .y = cropTop,
            .w = cropRight - cropLeft,
            .h = cropBottom - cropTop,
        },
        .timestamp = timestamp,
    };

    // Convert Android fence fd to OH SyncFence
    // Both use Linux sync_file fd — direct interchange via dup
    OHOS::sptr<OHOS::SyncFence> acquireFence = OHOS::SyncFence::INVALID_FENCE;
    if (fenceFd >= 0) {
        acquireFence = new OHOS::SyncFence(fenceFd);
        // SyncFence takes ownership of the fd, no need to close separately
    }

    // Flush buffer to OH Surface (maps to BufferQueue::FlushBuffer)
    OHOS::SurfaceError ret = ohSurface_->FlushBuffer(
        ohBuffer, acquireFence, flushConfig);

    if (ret != OHOS::SURFACE_ERROR_OK) {
        OH_BR_IPC_FAIL("GBProducer.queueBuffer", -static_cast<int>(ret),
                       "FlushBuffer failed");
        return -static_cast<int>(ret);
    }

    LOGI("queueBuffer: slot=%d, timestamp=%lld, crop=[%d,%d,%d,%d]",
         slot, static_cast<long long>(timestamp),
         cropLeft, cropTop, cropRight, cropBottom);

    // Clean up the slot's cached dmabuf fd (buffer is now owned by consumer)
    {
        std::lock_guard<std::mutex> lock(mutex_);
        auto it = slots_.find(slot);
        if (it != slots_.end()) {
            if (it->second.dmabufFd >= 0) {
                close(it->second.dmabufFd);
                it->second.dmabufFd = -1;
            }
            it->second.ohBuffer = nullptr;
            slots_.erase(it);
        }
    }

    OH_BR_IPC_EXIT("GBProducer.queueBuffer", 0);
    return 0;
}

int OHGraphicBufferProducer::cancelBuffer(int slot, int fenceFd) {
    OH_BR_IPC_ENTER("GBProducer.cancelBuffer", "slot=%{public}d fenceFd=%{public}d", slot, fenceFd);
    if (!ohSurface_) {
        OH_BR_IPC_FAIL("GBProducer.cancelBuffer", -1, "OH Surface is null");
        return -1;
    }

    OHOS::sptr<OHOS::SurfaceBuffer> ohBuffer;
    {
        std::lock_guard<std::mutex> lock(mutex_);
        auto it = slots_.find(slot);
        if (it == slots_.end()) {
            OH_BR_IPC_FAIL("GBProducer.cancelBuffer", -1, "Invalid slot %{public}d", slot);
            return -1;
        }
        ohBuffer = it->second.ohBuffer;
    }

    if (ohBuffer == nullptr) {
        OH_BR_IPC_FAIL("GBProducer.cancelBuffer", -1, "Buffer for slot %{public}d is null", slot);
        return -1;
    }

    // Build SyncFence from the fence fd
    OHOS::sptr<OHOS::SyncFence> fence = OHOS::SyncFence::INVALID_FENCE;
    if (fenceFd >= 0) {
        fence = new OHOS::SyncFence(dup(fenceFd));
    }

    OHOS::SurfaceError ret = ohSurface_->CancelBuffer(ohBuffer);

    if (ret != OHOS::SURFACE_ERROR_OK) {
        OH_BR_IPC_FAIL("GBProducer.cancelBuffer", -static_cast<int>(ret),
                       "CancelBuffer failed");
        return -static_cast<int>(ret);
    }

    // Clean up the slot
    {
        std::lock_guard<std::mutex> lock(mutex_);
        auto it = slots_.find(slot);
        if (it != slots_.end()) {
            if (it->second.dmabufFd >= 0) {
                close(it->second.dmabufFd);
            }
            slots_.erase(it);
        }
    }

    LOGI("cancelBuffer: slot=%d", slot);
    OH_BR_IPC_EXIT("GBProducer.cancelBuffer", 0);
    return 0;
}

int OHGraphicBufferProducer::getBufferFd(int slot) const {
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = slots_.find(slot);
    if (it == slots_.end()) {
        LOGE("getBufferFd: Invalid slot %d", slot);
        return -1;
    }
    return it->second.dmabufFd;
}

void* OHGraphicBufferProducer::getBufferAddr(int slot) const
{
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = slots_.find(slot);
    if (it == slots_.end() || !it->second.ohBuffer) {
        return nullptr;
    }
    return it->second.ohBuffer->GetVirAddr();
}

bool OHGraphicBufferProducer::getBufferInfo(int slot, int32_t* width,
                                             int32_t* height, int32_t* stride,
                                             int32_t* format) const
{
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = slots_.find(slot);
    if (it == slots_.end()) {
        return false;
    }

    const BufferSlot& bufSlot = it->second;
    if (width) *width = bufSlot.width;
    if (height) *height = bufSlot.height;
    if (stride) {
        // Get stride from the OH SurfaceBuffer
        if (bufSlot.ohBuffer) {
            *stride = bufSlot.ohBuffer->GetStride();
        } else {
            *stride = bufSlot.width;
        }
    }
    if (format) *format = bufSlot.format;
    return true;
}

int OHGraphicBufferProducer::connect() {
    OH_BR_IPC_SCOPE("GBProducer.connect", "");
    LOGI("connect()");
    // OH Surface does not have an explicit connect step.
    // The producer is connected upon Surface creation.
    return 0;
}

int OHGraphicBufferProducer::disconnect() {
    OH_BR_IPC_SCOPE("GBProducer.disconnect", "");
    LOGI("disconnect()");

    // Clean up all remaining slots
    std::lock_guard<std::mutex> lock(mutex_);
    for (auto& [slot, bufSlot] : slots_) {
        if (bufSlot.dmabufFd >= 0) {
            close(bufSlot.dmabufFd);
            bufSlot.dmabufFd = -1;
        }
    }
    slots_.clear();

    return 0;
}

int OHGraphicBufferProducer::setBufferCount(int count) {
    if (!ohSurface_) {
        LOGE("setBufferCount: OH Surface is null");
        return -1;
    }

    LOGI("setBufferCount: %d", count);
    OHOS::SurfaceError ret = ohSurface_->SetQueueSize(count);
    if (ret != OHOS::SURFACE_ERROR_OK) {
        LOGE("setBufferCount: SetQueueSize failed (ret=%d)", static_cast<int>(ret));
        return -static_cast<int>(ret);
    }
    return 0;
}

// Static format/usage conversion using pixel_format_mapper.h
int32_t OHGraphicBufferProducer::toOHPixelFormat(int32_t androidFormat) {
    return androidToOHPixelFormat(androidFormat);
}

uint64_t OHGraphicBufferProducer::toOHUsage(uint64_t androidUsage) {
    return androidToOHUsage(androidUsage);
}

}  // namespace oh_adapter
