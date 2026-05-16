#!/usr/bin/env python3
"""apply_G214be_displaylist_probe.py

G2.14be — In-place patch RenderNodeDrawable.cpp to identify WHICH NOOP branch
the View tree replay takes when SkCanvas->clear(red) is preserved through
renderFrame() (per G2.14ba data showing #3+ post-renderFrame pixels = pure
red from probe A's clear, meaning renderRenderNodes wrote nothing).  4
candidate causes:

  1. DisplayList isEmpty()                       -> Java Canvas record produced no ops
  2. forceDraw early-return (renderable/nothing) -> tree marked invisible/empty
  3. drawContent quickReject                     -> bounds vs clip mismatch
  4. neither + replay still no-op                -> m133 Skia ABI silent failure

Probe A (extend existing G214au_HWR onDraw probe):
  Append isEmpty / getUsedSize / hasText / hasFunctor / hasVectorDrawables /
  isRenderable / nothingToDraw / mComposeLayer / mProjectedDisplayList / Z to
  the first probe firing at #1, #100, #200, ...

Probe B (forceDraw early-return reason):
  Log projDL / renderable / nothingToDraw / composeLayer when the early
  return short-circuits drawContent.

Probe C (drawContent quickReject diagnostic):
  Log quickReject result, clipToBounds, bounds dimensions.

Idempotent (per-probe marker check); creates .bak.G214be on first run.

Target: ~/aosp/frameworks/base/libs/hwui/pipeline/skia/RenderNodeDrawable.cpp
"""
import os
import sys

AOSP_ROOT = os.environ.get("AOSP_ROOT", "/home/HanBingChen/aosp")
TARGET = os.path.join(AOSP_ROOT, "frameworks/base/libs/hwui/pipeline/skia/RenderNodeDrawable.cpp")


# ============================================================================
# Probe A: extend existing G214au_HWR onDraw probe with DisplayList state.
# Existing block at line ~123-129 of RenderNodeDrawable.cpp:
#
#     if (n == 1 || n % 100 == 0) {
#         fprintf(stderr, "[G214au_HWR] RenderNodeDrawable::onDraw #%d ...\n",
#                 n, (void*)canvas, mInReorderingSection ? 1 : 0);
#     }
#
# Insert DisplayList state log inside the same gated block so it co-fires with
# the existing probe (every 100 frames + frame #1).
# ============================================================================
A_ANCHOR = """        if (n == 1 || n % 100 == 0) {
            fprintf(stderr, "[G214au_HWR] RenderNodeDrawable::onDraw #%d canvas=%p inReorder=%d\\n",
                    n, (void*)canvas, mInReorderingSection ? 1 : 0);
        }"""

A_INSERT = """        if (n == 1 || n % 100 == 0) {
            fprintf(stderr, "[G214au_HWR] RenderNodeDrawable::onDraw #%d canvas=%p inReorder=%d\\n",
                    n, (void*)canvas, mInReorderingSection ? 1 : 0);
            // G2.14be probe A — log DisplayList state to distinguish empty-list
            // vs early-return vs silent-replay.  marker: G214be_DLProbe_A
            RenderNode* rnA = mRenderNode.get();
            SkiaDisplayList* dlA = rnA ? rnA->getDisplayList().asSkiaDl() : nullptr;
            fprintf(stderr,
                "[G214be] A dl-state #%d rn=%p dl=%p isEmpty=%d usedSize=%zu hasText=%d hasFunctor=%d hasVecDr=%d isRenderable=%d nothingToDraw=%d composeLayer=%d projDL=%p z=%.2f\\n",
                n, (void*)rnA, (void*)dlA,
                dlA ? (int)dlA->isEmpty() : -1,
                dlA ? dlA->getUsedSize() : (size_t)-1,
                dlA ? (int)dlA->hasText() : -1,
                dlA ? (int)dlA->hasFunctor() : -1,
                dlA ? (int)dlA->hasVectorDrawables() : -1,
                rnA ? (int)rnA->isRenderable() : -1,
                rnA ? (int)rnA->nothingToDraw() : -1,
                mComposeLayer ? 1 : 0,
                (void*)mProjectedDisplayList,
                rnA ? (double)rnA->properties().getZ() : 0.0);
        }"""


# ============================================================================
# Probe B: forceDraw early-return reason.
# Existing block at line ~165-168 of RenderNodeDrawable.cpp:
#
#     if ((mProjectedDisplayList == nullptr && !renderNode->isRenderable()) ||
#         (renderNode->nothingToDraw() && mComposeLayer)) {
#         return;
#     }
#
# Insert probe BEFORE the bare return so the log fires exactly when the
# silent skip happens, with reasons.
# ============================================================================
B_ANCHOR = """    if ((mProjectedDisplayList == nullptr && !renderNode->isRenderable()) ||
        (renderNode->nothingToDraw() && mComposeLayer)) {
        return;
    }"""

B_INSERT = """    if ((mProjectedDisplayList == nullptr && !renderNode->isRenderable()) ||
        (renderNode->nothingToDraw() && mComposeLayer)) {
        // G2.14be probe B — forceDraw early-return diagnostic.
        // marker: G214be_DLProbe_B
        {
            static std::atomic<int> g_b_count{0};
            int nb = ++g_b_count;
            if (nb == 1 || nb % 100 == 0) {
                fprintf(stderr,
                    "[G214be] B forceDraw #%d RETURN early-skip projDL=%p renderable=%d nothingToDraw=%d composeLayer=%d\\n",
                    nb, (void*)mProjectedDisplayList,
                    (int)renderNode->isRenderable(),
                    (int)renderNode->nothingToDraw(),
                    mComposeLayer ? 1 : 0);
            }
        }
        return;
    }"""


# ============================================================================
# Probe C: drawContent quickReject diagnostic.
# Existing line ~245 of RenderNodeDrawable.cpp:
#
#     bool quickRejected = properties.getClipToBounds() && canvas->quickReject(bounds);
#     if (!quickRejected) {
#
# Insert probe BETWEEN the assignment and the if.
# ============================================================================
C_ANCHOR = """    bool quickRejected = properties.getClipToBounds() && canvas->quickReject(bounds);
    if (!quickRejected) {"""

C_INSERT = """    bool quickRejected = properties.getClipToBounds() && canvas->quickReject(bounds);
    // G2.14be probe C — drawContent quickReject diagnostic.
    // marker: G214be_DLProbe_C
    {
        static std::atomic<int> g_c_count{0};
        int nc = ++g_c_count;
        if (nc == 1 || nc % 100 == 0) {
            fprintf(stderr,
                "[G214be] C drawContent #%d quickReject=%d clipToBounds=%d boundsW=%.1f boundsH=%.1f\\n",
                nc, quickRejected ? 1 : 0,
                properties.getClipToBounds() ? 1 : 0,
                (double)(bounds.width()), (double)(bounds.height()));
        }
    }
    if (!quickRejected) {"""


def patch(target, anchor, insert, marker_substr):
    with open(target, "r") as f:
        content = f.read()
    if marker_substr in content:
        print(f"  [skip] {marker_substr} already present")
        return False
    count = content.count(anchor)
    if count != 1:
        print(f"  [FAIL] anchor not unique (occurrences={count}) for marker {marker_substr}")
        return False
    content = content.replace(anchor, insert, 1)
    with open(target, "w") as f:
        f.write(content)
    print(f"  [OK] inserted probe {marker_substr}")
    return True


def main():
    if not os.path.exists(TARGET):
        print(f"ERROR: target not found: {TARGET}", file=sys.stderr)
        return 2

    backup = TARGET + ".bak.G214be"
    if not os.path.exists(backup):
        import shutil
        shutil.copy2(TARGET, backup)
        print(f"  [backup] {backup}")

    print(f"Patching {TARGET}")
    ok_a = patch(TARGET, A_ANCHOR, A_INSERT, "G214be_DLProbe_A")
    ok_b = patch(TARGET, B_ANCHOR, B_INSERT, "G214be_DLProbe_B")
    ok_c = patch(TARGET, C_ANCHOR, C_INSERT, "G214be_DLProbe_C")

    if ok_a or ok_b or ok_c:
        print("Probes inserted. Re-compile libhwui to take effect.")
    else:
        print("No changes (already patched or anchors moved).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
