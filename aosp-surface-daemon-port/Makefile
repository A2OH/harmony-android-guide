# ============================================================================
# Westlake — M6 surface daemon (Step 1 skeleton)
#
# Cross-compiles `westlake-surface-daemon` (registered with SM as
# "SurfaceFlinger") against bionic-arm64.  Reuses the
# aosp-libbinder-port/out/bionic/libbinder.so + libutils/libcutils/libbase
# static archives — no rebuild of libbinder, just link against it.
#
# Usage:
#   make            Build out/bionic/surfaceflinger
#   make clean      Remove out/
# ============================================================================

# Reuse the same NDK toolchain that aosp-libbinder-port/Makefile uses.
NDK_LLVM    := /home/dspfac/android-sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/linux-x86_64
NDK_SYSROOT := $(NDK_LLVM)/sysroot

CXX         := $(NDK_LLVM)/bin/clang++
STRIP       := $(NDK_LLVM)/bin/llvm-strip

# Sibling port — we link against its already-built libbinder.so.
LIBBINDER_PORT := $(CURDIR)/../aosp-libbinder-port
LIBBINDER_OUT  := $(LIBBINDER_PORT)/out/bionic
AIDL_GEN       := $(LIBBINDER_PORT)/out/aidl-gen

OUT  := $(CURDIR)/out/bionic
OBJ  := $(OUT)/obj

# Target API 33 matches the bionic libbinder build (api33 unlocks
# AParcel_reset / AParcel_getDataSize that libbinder's NDK helpers
# reference unconditionally).
TARGET_FLAGS := --target=aarch64-linux-android33 \
                --sysroot=$(NDK_SYSROOT) \
                -fuse-ld=lld \
                --rtlib=compiler-rt

# Match libbinder build flags: no RTTI, no exceptions, hidden visibility,
# _GNU_SOURCE for the few libc helpers libbinder headers use.  -U__ANDROID__
# matches the BIONIC_DEFINES path in the sibling port (libbinder uses the
# host code paths so we don't pull in vintf/perfetto/selinux symbols).
#
# M5-Step3: -D_LIBCPP_ABI_NAMESPACE=__1 forces NDK libc++ headers into the
# platform `std::__1::` namespace so this daemon's symbol references match
# the libbinder.so we link against (also rebuilt with __1).
# Sibling change: aosp-libbinder-port BIONIC_DEFINES.
CXXFLAGS := $(TARGET_FLAGS) \
            -U__ANDROID__ \
            -D_LIBCPP_ABI_NAMESPACE=__1 \
            -fPIC \
            -std=c++17 \
            -fno-rtti -fno-exceptions \
            -fvisibility=hidden \
            -O2 \
            -D_GNU_SOURCE \
            -Wall \
            -Wno-unused-parameter

# M5-Step3 link: use the staged platform libc++.so (`__1`) instead of NDK
# static libc++ (`__ndk1`).  Mirror of BIONIC_LIBCXX_LINK in libbinder-port.
LIBCXX_LINK := -nostdlib++ -L$(LIBBINDER_PORT)/platform-libs/lib64 -lc++

# Includes: libbinder headers + the AIDL-generated headers + libutils.
INCLUDES := -I$(AIDL_GEN)/include \
            -I$(LIBBINDER_PORT)/bionic-overlay/include \
            -I$(LIBBINDER_PORT)/aosp-src/libbinder/include \
            -I$(LIBBINDER_PORT)/aosp-src/libbinder/ndk/include_cpp \
            -I$(LIBBINDER_PORT)/aosp-src/libbinder/liblog_stub/include \
            -I$(LIBBINDER_PORT)/deps-src/libutils_binder/include \
            -I$(LIBBINDER_PORT)/deps-src/libcutils/include \
            -I$(LIBBINDER_PORT)/deps-src/libbase/include \
            -I$(LIBBINDER_PORT)/deps-src/libsystem/include

# Sources we own.
DAEMON_SRCS := native/surfaceflinger_main.cpp \
               native/WestlakeSurfaceComposer.cpp \
               native/WestlakeSurfaceComposerClient.cpp \
               native/WestlakeGraphicBufferProducer.cpp \
               native/MemfdGraphicBuffer.cpp \
               native/DlstConsumer.cpp \
               native/WestlakeDisplayEventConnection.cpp

DAEMON_OBJS := $(patsubst native/%.cpp,$(OBJ)/%.o,$(DAEMON_SRCS))

# Headers each TU may transitively include.  The Step-3 buffer-pipeline
# headers (WestlakeGraphicBufferProducer + MemfdGraphicBuffer) are reached
# by the Client + the producer TU; listing them all is harmless conservative.
# Step-4 adds DlstConsumer.h.  Step-5 adds WestlakeDisplayEventConnection.h.
DAEMON_HDRS := native/WestlakeSurfaceComposer.h \
               native/WestlakeSurfaceComposerClient.h \
               native/WestlakeGraphicBufferProducer.h \
               native/MemfdGraphicBuffer.h \
               native/DlstConsumer.h \
               native/WestlakeDisplayEventConnection.h

# Link against the prebuilt libbinder.so + the three sibling static archives.
LIBBINDER_DEPS := $(LIBBINDER_OUT)/libbinder.so \
                  $(LIBBINDER_OUT)/libutils_static.a \
                  $(LIBBINDER_OUT)/libcutils_static.a \
                  $(LIBBINDER_OUT)/libbase_static.a

BIN := $(OUT)/surfaceflinger

# Step-2 transaction smoke test driver.
SMOKE_SRC := native/surface_smoke.cc
SMOKE_OBJ := $(OBJ)/surface_smoke.o
SMOKE_BIN := $(OUT)/surface_smoke

.PHONY: all clean dirs smoke

all: dirs $(BIN) $(SMOKE_BIN)
	@echo ""
	@echo "════ Build complete ════"
	@ls -lh $(BIN) $(SMOKE_BIN)

smoke: dirs $(SMOKE_BIN)

dirs:
	@mkdir -p $(OBJ) $(OUT)

# Verify the sibling port has been built — we depend on its artifacts.
$(LIBBINDER_OUT)/libbinder.so:
	@echo "ERROR: $(LIBBINDER_OUT)/libbinder.so not found."
	@echo "       Run aosp-libbinder-port/build.sh bionic first."
	@exit 1

$(OBJ)/%.o: native/%.cpp $(DAEMON_HDRS) | dirs
	@echo "CXX(bionic) $<"
	@$(CXX) $(CXXFLAGS) $(INCLUDES) -c $< -o $@

$(BIN): $(DAEMON_OBJS) $(LIBBINDER_DEPS)
	@echo "════ Linking westlake-surface-daemon ($(notdir $(BIN))) ════"
	$(CXX) $(TARGET_FLAGS) -std=c++17 \
	    -o $@ \
	    $(DAEMON_OBJS) \
	    -L$(LIBBINDER_OUT) -lbinder \
	    $(LIBBINDER_OUT)/libutils_static.a \
	    $(LIBBINDER_OUT)/libcutils_static.a \
	    $(LIBBINDER_OUT)/libbase_static.a \
	    $(LIBCXX_LINK) -ldl
# Note: pthread symbols come from bionic libc (no separate -lpthread on Android).
	@cp $@ $@.unstripped
	@$(STRIP) $@
	@echo "Sizes:"
	@ls -lh $@.unstripped $@

# Smoke driver (Step-2 transaction round-trip test).
$(SMOKE_OBJ): $(SMOKE_SRC) | dirs
	@echo "CXX(bionic) $<"
	@$(CXX) $(CXXFLAGS) $(INCLUDES) -c $< -o $@

$(SMOKE_BIN): $(SMOKE_OBJ) $(LIBBINDER_DEPS)
	@echo "════ Linking surface_smoke ($(notdir $(SMOKE_BIN))) ════"
	$(CXX) $(TARGET_FLAGS) -std=c++17 \
	    -o $@ \
	    $(SMOKE_OBJ) \
	    -L$(LIBBINDER_OUT) -lbinder \
	    $(LIBBINDER_OUT)/libutils_static.a \
	    $(LIBBINDER_OUT)/libcutils_static.a \
	    $(LIBBINDER_OUT)/libbase_static.a \
	    $(LIBCXX_LINK) -ldl
	@cp $@ $@.unstripped
	@$(STRIP) $@
	@echo "Sizes:"
	@ls -lh $@.unstripped $@

clean:
	rm -rf out
