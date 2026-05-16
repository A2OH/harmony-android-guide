/*
 * oh_graphic_buffer_producer.h
 *
 * Adapter that implements Android's IGraphicBufferProducer interface
 * backed by an OH Surface (ProducerSurface).
 *
 * This is the core of the Surface bridging mechanism:
 *   - Android HWUI calls dequeueBuffer() → mapped to OH Surface::RequestBuffer()
 *   - Android HWUI calls queueBuffer()   → mapped to OH Surface::FlushBuffer()
 *   - Buffer memory is shared via dmabuf fd (zero-copy)
 *   - Fence synchronization uses dup(fd) (both are Linux sync_file)
 *
 * The OH RenderService (Consumer side) acquires and composites the buffers
 * submitted through this producer, enabling Android Apps to render into
 * OH's display pipeline without any app-level code changes.
 */
#ifndef OH_GRAPHIC_BUFFER_PRODUCER_H
#define OH_GRAPHIC_BUFFER_PRODUCER_H
#include <refbase.h>

#include <cstdint>
#include <unordered_map>
#include <mutex>
#include <string>

// Forward declarations for OH types
namespace OHOS {
    template<typename T> class sptr;
    class Surface;
    class SurfaceBuffer;
    class SyncFence;
}

namespace oh_adapter {

/**
 * Tracks the mapping between Android buffer slots and OH SurfaceBuffers.
 */
struct BufferSlot {
    OHOS::sptr<OHOS::SurfaceBuffer> ohBuffer;  // OH buffer object (holds dmabuf fd)
    uint32_t ohSeqNum = 0;                      // Sequence number in OH BufferQueue
    int32_t width = 0;
    int32_t height = 0;
    int32_t format = 0;
    int dmabufFd = -1;                          // Cached dup'd dmabuf fd
};

/**
 * OHGraphicBufferProducer wraps an OH Surface (ProducerSurface) and provides
 * the buffer operations that Android's rendering pipeline expects.
 *
 * In the full AOSP integration this would extend BnGraphicBufferProducer.
 * For the adapter layer, it provides the equivalent C++ methods that the
 * JNI bridge and modified BLASTBufferQueue can call.
 */
class OHGraphicBufferProducer {
public:
    /**
     * Construct with an OH ProducerSurface.
     * @param ohSurface  The Surface obtained from RSSurfaceNode::GetSurface().
     */
    explicit OHGraphicBufferProducer(OHOS::sptr<OHOS::Surface> ohSurface);
    ~OHGraphicBufferProducer();

    /**
     * Dequeue a buffer for rendering.
     * Maps to OH Surface::RequestBuffer().
     *
     * @param outSlot       out: Buffer slot index.
     * @param outFenceFd    out: Fence fd to wait on before writing (-1 if none).
     * @param width         Requested width.
     * @param height        Requested height.
     * @param format        Pixel format (Android value, mapped internally).
     * @param usage         Usage flags (Android value, mapped internally).
     * @return 0 on success, negative on error.
     */
    int dequeueBuffer(int* outSlot, int* outFenceFd,
                      int32_t width, int32_t height,
                      int32_t format, uint64_t usage);

    /**
     * Queue a rendered buffer for consumption by RenderService.
     * Maps to OH Surface::FlushBuffer().
     *
     * @param slot          Slot from dequeueBuffer.
     * @param fenceFd       Fence fd signaling render completion (-1 if none).
     * @param timestamp     Frame timestamp in nanoseconds.
     * @param cropLeft      Crop rectangle left.
     * @param cropTop       Crop rectangle top.
     * @param cropRight     Crop rectangle right.
     * @param cropBottom    Crop rectangle bottom.
     * @return 0 on success, negative on error.
     */
    int queueBuffer(int slot, int fenceFd, int64_t timestamp,
                    int cropLeft, int cropTop, int cropRight, int cropBottom);

    /**
     * Cancel a previously dequeued buffer.
     * Maps to OH Surface::CancelBuffer().
     *
     * @param slot      Slot from dequeueBuffer.
     * @param fenceFd   Fence fd (-1 if none). Caller retains ownership.
     * @return 0 on success.
     */
    int cancelBuffer(int slot, int fenceFd);

    /**
     * Get the dmabuf fd for a dequeued buffer slot.
     * Used by the Android side to import as GraphicBuffer.
     *
     * @param slot   Buffer slot index.
     * @return dmabuf fd (caller must dup if needed), or -1 on error.
     */
    int getBufferFd(int slot) const;

    /**
     * Get the mmapped virtual address of a dequeued buffer slot.
     * Maps to OH SurfaceBuffer::GetVirAddr().
     * Caller does not own the memory; it is valid until cancelBuffer/queueBuffer.
     * @param slot   Buffer slot index.
     * @return Virtual address, or nullptr on error.
     */
    void* getBufferAddr(int slot) const;

    /**
     * Get buffer info for a slot.
     */
    bool getBufferInfo(int slot, int32_t* width, int32_t* height,
                       int32_t* stride, int32_t* format) const;

    /**
     * Connect/disconnect producer.
     */
    int connect();
    int disconnect();

    /**
     * Set buffer queue size (default 3 for triple buffering).
     */
    int setBufferCount(int count);

    /**
     * Get the underlying OH Surface (for external use).
     */
    OHOS::sptr<OHOS::Surface> getOHSurface() const { return ohSurface_; }

private:
    OHOS::sptr<OHOS::Surface> ohSurface_;

    mutable std::mutex mutex_;
    std::unordered_map<int, BufferSlot> slots_;
    int nextSlot_ = 0;

    // Format and usage conversion helpers
    static int32_t toOHPixelFormat(int32_t androidFormat);
    static uint64_t toOHUsage(uint64_t androidUsage);
};

}  // namespace oh_adapter

#endif  // OH_GRAPHIC_BUFFER_PRODUCER_H
