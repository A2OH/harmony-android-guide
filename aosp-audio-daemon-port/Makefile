# ============================================================================
# Westlake — M5 audio daemon (Step 2: Tier-1 dispatch + AAudio backend)
#
# Cross-compiles `audio_flinger` (registered with SM as "media.audio_flinger")
# against bionic-arm64.  Reuses the aosp-libbinder-port/out/bionic/libbinder.so
# + libutils/libcutils/libbase static archives — no rebuild of libbinder, just
# link against it.  Mirror of aosp-surface-daemon-port/Makefile (M6 sibling).
#
# Step 2 additions: links against libaaudio.so (NDK sysroot) for the AAudio
# NDK backend that produces speaker output.  CR34 spike validated the call
# pattern on cfb7c9e3.
#
# Usage:
#   make            Build out/bionic/audio_flinger + audio_smoke
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
# the libbinder.so we link against (also rebuilt with __1) AND the platform
# libaaudio.so we dlopen.  Sibling change: aosp-libbinder-port BIONIC_DEFINES.
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

# Includes: libbinder headers + the AIDL-generated headers + libutils +
# NDK AAudio header (already in the sysroot, but spelled here for clarity).
INCLUDES := -I$(AIDL_GEN)/include \
            -I$(LIBBINDER_PORT)/bionic-overlay/include \
            -I$(LIBBINDER_PORT)/aosp-src/libbinder/include \
            -I$(LIBBINDER_PORT)/aosp-src/libbinder/ndk/include_cpp \
            -I$(LIBBINDER_PORT)/aosp-src/libbinder/liblog_stub/include \
            -I$(LIBBINDER_PORT)/deps-src/libutils_binder/include \
            -I$(LIBBINDER_PORT)/deps-src/libcutils/include \
            -I$(LIBBINDER_PORT)/deps-src/libbase/include \
            -I$(LIBBINDER_PORT)/deps-src/libsystem/include

# Sources we own (daemon + smoke driver + audio helper subprocess).
# M5-Step5: AAudioBackend replaced by AudioHelper (fork+exec audio_helper
# subprocess so AAudio's libaudioclient binds to the platform /dev/binder
# SM rather than our /dev/vndbinder Bn-self loop — see M5_STEP5_REPORT §3).
DAEMON_SRCS := native/audiopolicy_main.cpp \
               native/WestlakeAudioFlinger.cpp \
               native/WestlakeAudioTrack.cpp \
               native/AudioHelper.cpp

DAEMON_OBJS := $(patsubst native/%.cpp,$(OBJ)/%.o,$(DAEMON_SRCS))

DAEMON_HDRS := native/WestlakeAudioFlinger.h \
               native/WestlakeAudioTrack.h \
               native/AudioHelper.h

# Link against the prebuilt libbinder.so + the three sibling static archives
# + libaaudio.so from the NDK sysroot (Phase-1 backend per CR34).
LIBBINDER_DEPS := $(LIBBINDER_OUT)/libbinder.so \
                  $(LIBBINDER_OUT)/libutils_static.a \
                  $(LIBBINDER_OUT)/libcutils_static.a \
                  $(LIBBINDER_OUT)/libbase_static.a

BIN := $(OUT)/audio_flinger

# Step-2 transaction smoke driver (compiled as a .cc; uses libbinder + libm).
SMOKE_SRC := native/audio_smoke.cc
SMOKE_OBJ := $(OBJ)/audio_smoke.o
SMOKE_BIN := $(OUT)/audio_smoke

# M5-Step5: audio_helper is a standalone child binary spawned by the daemon.
# It links libaaudio directly (the daemon does NOT — that's the whole point
# of the helper-process pivot).  Pure bionic-libc + libaaudio + libm, no
# libbinder dependency: in its address space the system audio-flinger that
# libaudioclient resolves is the platform's real audioserver on /dev/binder.
HELPER_SRC := native/audio_helper.cc
HELPER_OBJ := $(OBJ)/audio_helper.o
HELPER_BIN := $(OUT)/audio_helper

.PHONY: all clean dirs smoke helper

all: dirs $(BIN) $(SMOKE_BIN) $(HELPER_BIN)
	@echo ""
	@echo "════ Build complete ════"
	@ls -lh $(BIN) $(SMOKE_BIN) $(HELPER_BIN)

smoke: dirs $(SMOKE_BIN)

helper: dirs $(HELPER_BIN)

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

# Smoke .cc rule (separate from the .cpp pattern above).
$(SMOKE_OBJ): $(SMOKE_SRC) $(DAEMON_HDRS) | dirs
	@echo "CXX(bionic) $<"
	@$(CXX) $(CXXFLAGS) $(INCLUDES) -c $< -o $@

$(BIN): $(DAEMON_OBJS) $(LIBBINDER_DEPS)
	@echo "════ Linking westlake-audio-daemon ($(notdir $(BIN))) ════"
	$(CXX) $(TARGET_FLAGS) -std=c++17 \
	    -o $@ \
	    $(DAEMON_OBJS) \
	    -L$(LIBBINDER_OUT) -lbinder \
	    $(LIBBINDER_OUT)/libutils_static.a \
	    $(LIBBINDER_OUT)/libcutils_static.a \
	    $(LIBBINDER_OUT)/libbase_static.a \
	    $(LIBCXX_LINK) -ldl -lm
	@# M5-Step5: the daemon NO LONGER touches libaaudio.so directly.  AAudio
	@# work happens in the audio_helper subprocess (see AudioHelper.cpp top
	@# comment for the in-process re-entrance hazard this avoids).  The
	@# daemon now uses pipe-and-fork to talk to the helper; clean separation.
	@cp $@ $@.unstripped
	@$(STRIP) $@
	@echo "Sizes:"
	@ls -lh $@.unstripped $@

$(SMOKE_BIN): $(SMOKE_OBJ) $(LIBBINDER_DEPS)
	@echo "════ Linking audio_smoke ($(notdir $(SMOKE_BIN))) ════"
	$(CXX) $(TARGET_FLAGS) -std=c++17 \
	    -o $@ \
	    $(SMOKE_OBJ) \
	    -L$(LIBBINDER_OUT) -lbinder \
	    $(LIBBINDER_OUT)/libutils_static.a \
	    $(LIBBINDER_OUT)/libcutils_static.a \
	    $(LIBBINDER_OUT)/libbase_static.a \
	    $(LIBCXX_LINK) -ldl -lm
	@cp $@ $@.unstripped
	@$(STRIP) $@
	@echo "Sizes:"
	@ls -lh $@.unstripped $@

# audio_helper build rule.  No libbinder; no libutils.  This is the entire
# point: a virgin process so libaudioclient's defaultServiceManager() goes
# to /dev/binder (real audioserver) instead of inheriting the daemon's
# /dev/vndbinder ProcessState.  Same NDK toolchain & target flags so the
# binary works on bionic-arm64 alongside the daemon binary.
$(HELPER_OBJ): $(HELPER_SRC) | dirs
	@echo "CXX(bionic) $<"
	@$(CXX) $(TARGET_FLAGS) \
	    -std=c++17 \
	    -fPIE \
	    -O2 \
	    -Wall \
	    -Wno-unused-parameter \
	    -D_GNU_SOURCE \
	    -c $< -o $@

$(HELPER_BIN): $(HELPER_OBJ)
	@echo "════ Linking audio_helper ($(notdir $(HELPER_BIN))) ════"
	@# Statically link libc++ so the helper binary doesn't depend on
	@# libc++_shared.so at runtime (not staged on the phone outside an APK).
	$(CXX) $(TARGET_FLAGS) -std=c++17 -fPIE -pie \
	    -static-libstdc++ \
	    -o $@ \
	    $(HELPER_OBJ) \
	    -laaudio -ldl -lm -llog
	@cp $@ $@.unstripped
	@$(STRIP) $@
	@echo "Sizes:"
	@ls -lh $@.unstripped $@

clean:
	rm -rf out
