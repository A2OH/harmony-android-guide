# ============================================================================
# Westlake — AOSP libbinder.so cross-compile for OpenHarmony / Phones
#
# Two coexisting variants:
#   * musl (default targets):  cross-compile against OHOS musl + libcxx, output
#     to out/.  Used on OpenHarmony devices and as M1/M2 sandbox reference on
#     Android phones that have musl loader installed.
#   * bionic (`*-bionic` targets):  cross-compile against Android NDK r25
#     (bionic libc + libc++), output to out/bionic/.  Used by M3 when loading
#     libbinder.so into a bionic-linked dalvikvm process on a real Android
#     phone.  Same source, same patches, different sysroot + ABI.
#
# Usage (musl path — unchanged):
#   make aidl           Run AIDL compiler over .aidl files
#   make deps           Compile libutils_binder + libcutils into static archives
#   make libbinder      Compile libbinder.so
#   make smoke          Compile binder_smoke test binary
#   make servicemanager Cross-compile AOSP servicemanager binary
#   make sm_smoke       Compile sm_smoke test binary (M2 sandbox test)
#   make all            Run all of the above in order
#
# Usage (bionic path — M3):
#   make libbinder-bionic       Bionic-linked libbinder.so (out/bionic/)
#   make servicemanager-bionic  Bionic-linked servicemanager (out/bionic/)
#   make sm_smoke-bionic        Bionic-linked sm_smoke (out/bionic/test/)
#   make libandroid_runtime_stub-bionic  libandroid_runtime_stub.so (M3 JNI shim)
#   make all-bionic             All bionic targets in order
#
# Bookkeeping:
#   make clean          Remove all build outputs (both variants)
# ============================================================================

OHOS_LLVM   := /home/dspfac/openharmony/prebuilts/clang/ohos/linux-x86_64/llvm
SYSROOT     := $(CURDIR)/ohos-sysroot
AIDL        := /home/dspfac/android-sdk/build-tools/34.0.0/aidl
SM_SRC      := /home/dspfac/aosp-sources/frameworks-native/cmds/servicemanager
LIBCUTILS_SRC := /home/dspfac/aosp-sources/system-core/libcutils

OUT         := $(CURDIR)/out
OBJ         := $(OUT)/obj
AIDL_GEN    := $(OUT)/aidl-gen

CXX         := $(OHOS_LLVM)/bin/clang++
CC          := $(OHOS_LLVM)/bin/clang
AR          := $(OHOS_LLVM)/bin/llvm-ar
STRIP       := $(OHOS_LLVM)/bin/llvm-strip

# Target triple + sysroot
TARGET_FLAGS := --target=aarch64-linux-ohos \
                --sysroot=$(SYSROOT) \
                -D__MUSL__ \
                -fuse-ld=lld \
                --rtlib=compiler-rt

# kHeader selector (see patches/0003-parcel-kheader-syst.patch).  AOSP's
# Parcel.cpp non-__ANDROID__ branch normally writes 'UNKN' as the 4-byte
# transaction header magic; we override at compile time to make our libbinder
# interoperable with real peers.  Today's Phase 1 sandbox talks to the phone's
# system_server (which expects 'SYST') and our own AOSP servicemanager;
# Phase 2's OHOS-vendor deployment will flip this to WESTLAKE_KHEADER_VNDR=1.
# Defining neither restores stock AOSP behavior ('UNKN').
KHEADER_FLAG := -DWESTLAKE_KHEADER_SYST=1

# Defines
DEFINES     := -DBINDER_WITH_KERNEL_IPC \
               -DBINDER_ENABLE_LIBLOG_ASSERT \
               -DANDROID_UTILS_CALLSTACK_ENABLED=0 \
               $(KHEADER_FLAG)

# Common C++ flags.  _GNU_SOURCE pulls in musl's strerror_r, getrandom, etc.
CXXFLAGS    := $(TARGET_FLAGS) $(DEFINES) \
               -fPIC \
               -std=c++17 \
               -fno-rtti -fno-exceptions \
               -fvisibility=hidden \
               -O2 \
               -D_GNU_SOURCE \
               -Wno-c99-designator \
               -Wno-missing-field-initializers \
               -Wno-unused-parameter \
               -Wno-zero-as-null-pointer-constant

# libbinder-specific: hide symbols and mark as building
LIBBINDER_CXXFLAGS := $(CXXFLAGS) -DBUILDING_LIBBINDER

# Include paths
INC_LIBBINDER := -I$(AIDL_GEN)/include \
                 -I$(CURDIR)/aosp-src/libbinder/include \
                 -I$(CURDIR)/aosp-src/libbinder/ndk/include_cpp \
                 -I$(CURDIR)/aosp-src/libbinder \
                 -I$(CURDIR)/aosp-src/libbinder/liblog_stub/include \
                 -I$(CURDIR)/deps-src/libutils_binder/include \
                 -I$(CURDIR)/deps-src/libcutils/include \
                 -I$(CURDIR)/deps-src/libbase/include \
                 -I$(CURDIR)/deps-src/libsystem/include

INC_DEPS    := -I$(CURDIR)/deps-src/libutils_binder/include \
               -I$(CURDIR)/deps-src/libcutils/include \
               -I$(CURDIR)/deps-src/libbase/include \
               -I$(CURDIR)/deps-src/libsystem/include \
               -I$(CURDIR)/aosp-src/libbinder/liblog_stub/include

# ----------------------------------------------------------------------------
# Source files
# ----------------------------------------------------------------------------

# libbinder common (transport-independent, 18 .cpp from libbinder_common_defaults)
LIBBINDER_COMMON_SRCS := \
    Binder.cpp BpBinder.cpp Debug.cpp FdTrigger.cpp IInterface.cpp \
    IResultReceiver.cpp Parcel.cpp ParcelFileDescriptor.cpp \
    RecordedTransaction.cpp RpcSession.cpp RpcServer.cpp RpcState.cpp \
    RpcTransportRaw.cpp Stability.cpp Status.cpp TextOutput.cpp Utils.cpp \
    file.cpp

# Kernel IPC layer (the actual M1 goal — 7 .cpp from libbinder_kernel_defaults)
LIBBINDER_KERNEL_SRCS := \
    BufferedTextOutput.cpp BackendUnifiedServiceManager.cpp \
    IPCThreadState.cpp IServiceManager.cpp IServiceManagerFFI.cpp \
    ProcessState.cpp Static.cpp

# OS abstraction (musl variant — no bionic)
LIBBINDER_OS_SRCS := \
    OS_non_android_linux.cpp OS_unix_base.cpp

# Memory + bookkeeping (8 .cpp from libbinder main cc_library srcs)
LIBBINDER_MEM_SRCS := \
    IMemory.cpp IShellCallback.cpp LazyServiceRegistrar.cpp \
    MemoryBase.cpp MemoryDealer.cpp MemoryHeapBase.cpp \
    ParcelableHolder.cpp PersistableBundle.cpp

# Device-interface (3 .cpp from libbinder_device_interface_sources filegroup —
# Android 16 always builds these into libbinder, only excluded for vendor)
LIBBINDER_DEVICE_SRCS := \
    IPermissionController.cpp PermissionCache.cpp PermissionController.cpp

# Host-path delegation (used when not __ANDROID__/not __FUCHSIA — see
# IServiceManager.cpp around line 47.  Provides getDeviceService(...) which
# IServiceManager calls into when running off-device.)
LIBBINDER_HOST_SRCS := \
    ServiceManagerHost.cpp UtilsHost.cpp

LIBBINDER_SRCS := $(addprefix aosp-src/libbinder/,$(LIBBINDER_COMMON_SRCS) $(LIBBINDER_KERNEL_SRCS) $(LIBBINDER_OS_SRCS) $(LIBBINDER_MEM_SRCS) $(LIBBINDER_DEVICE_SRCS) $(LIBBINDER_HOST_SRCS))

# libutils_binder + extra libutils (the libutils_binder_sdk equivalent + threads/time)
LIBUTILS_SRCS := \
    Errors.cpp RefBase.cpp SharedBuffer.cpp String16.cpp String8.cpp \
    StrongPointer.cpp Unicode.cpp VectorImpl.cpp \
    Threads.cpp Timers.cpp SystemClock.cpp Looper.cpp NativeHandle.cpp \
    Printer.cpp StopWatch.cpp Tokenizer.cpp misc.cpp FileMap.cpp \
    JenkinsHash.cpp LightRefBase.cpp

LIBUTILS_FULL_SRCS := $(addprefix deps-src/libutils_binder/,$(LIBUTILS_SRCS))

# libcutils + atrace stub (atrace is normally provided by libcutils on Android;
# on musl we no-op it)
LIBCUTILS_SRCS := \
    ashmem-host.cpp native_handle.cpp atrace_stub.cpp

LIBCUTILS_FULL_SRCS := $(addprefix deps-src/libcutils/,$(LIBCUTILS_SRCS))

# libbase — needed for ReadFully/WriteFully (file.cpp), StartsWith (strings.cpp),
# StringPrintf, posix_strerror_r.  logging.cpp is replaced by logging_stub.cpp
# because the real one depends on liblog daemon APIs we don't have.
# properties_stub.cpp is M2-only — implements SetProperty/GetProperty as no-ops
# (servicemanager calls these but in our sandbox no init listens).
LIBBASE_SRCS := \
    file.cpp strings.cpp stringprintf.cpp \
    posix_strerror_r.cpp errors_unix.cpp \
    logging_stub.cpp properties_stub.cpp

LIBBASE_FULL_SRCS := $(addprefix deps-src/libbase/,$(LIBBASE_SRCS))

# AIDL inputs
LIBBINDER_AIDL_FILES := \
    aidl/android/os/ConnectionInfo.aidl \
    aidl/android/os/IClientCallback.aidl \
    aidl/android/os/IServiceCallback.aidl \
    aidl/android/os/IServiceManager.aidl \
    aidl/android/os/Service.aidl \
    aidl/android/os/ServiceWithMetadata.aidl \
    aidl/android/os/ServiceDebugInfo.aidl \
    aidl/android/os/IAccessor.aidl

PKGMGR_AIDL_FILES := \
    aidl/android/content/pm/IPackageManagerNative.aidl \
    aidl/android/content/pm/IStagedApexObserver.aidl \
    aidl/android/content/pm/ApexStagedEvent.aidl \
    aidl/android/content/pm/StagedApexInfo.aidl

ALL_AIDL_FILES := $(LIBBINDER_AIDL_FILES) $(PKGMGR_AIDL_FILES)

# Generated AIDL .cpp files (the ones AIDL emits an implementation for —
# .aidl interfaces emit .cpp; .aidl parcelables emit .cpp too as of recent AIDL)
AIDL_OBJ_NAMES := IClientCallback IServiceCallback IServiceManager IAccessor \
                  Service ServiceWithMetadata ServiceDebugInfo ConnectionInfo \
                  IPackageManagerNative IStagedApexObserver ApexStagedEvent StagedApexInfo

# ----------------------------------------------------------------------------
# Object lists
# ----------------------------------------------------------------------------

LIBBINDER_OBJS := $(patsubst aosp-src/libbinder/%.cpp,$(OBJ)/libbinder/%.o,$(LIBBINDER_SRCS))
LIBUTILS_OBJS  := $(patsubst deps-src/libutils_binder/%.cpp,$(OBJ)/libutils/%.o,$(LIBUTILS_FULL_SRCS))
LIBCUTILS_OBJS := $(patsubst deps-src/libcutils/%.cpp,$(OBJ)/libcutils/%.o,$(LIBCUTILS_FULL_SRCS))
LIBBASE_OBJS   := $(patsubst deps-src/libbase/%.cpp,$(OBJ)/libbase/%.o,$(LIBBASE_FULL_SRCS))
AIDL_OBJS      := $(patsubst %,$(OBJ)/aidl/%.o,$(AIDL_OBJ_NAMES))

# ----------------------------------------------------------------------------
# Phony targets
# ----------------------------------------------------------------------------

.PHONY: all aidl deps libbinder smoke servicemanager sm_smoke clean dirs

all: dirs aidl deps libbinder smoke servicemanager sm_smoke
	@echo ""
	@echo "═════ Build Complete ═════"
	@ls -lh $(OUT)/libbinder.so $(OUT)/test/binder_smoke $(OUT)/servicemanager $(OUT)/test/sm_smoke 2>/dev/null || true

dirs:
	@mkdir -p $(OBJ)/libbinder $(OBJ)/libutils $(OBJ)/libcutils $(OBJ)/libbase $(OBJ)/aidl $(OBJ)/test $(OBJ)/sm
	@mkdir -p $(AIDL_GEN)/include $(AIDL_GEN)/src $(OUT)/test

# ----------------------------------------------------------------------------
# AIDL code generation
# ----------------------------------------------------------------------------

aidl: dirs $(AIDL_GEN)/.stamp

$(AIDL_GEN)/.stamp: $(addprefix aosp-src/libbinder/,$(ALL_AIDL_FILES))
	@echo "═══ Running AIDL compiler over $(words $(ALL_AIDL_FILES)) .aidl files ═══"
	@for f in $^; do \
		$(AIDL) --lang=cpp -h $(AIDL_GEN)/include -o $(AIDL_GEN)/src \
		        -I $(CURDIR)/aosp-src/libbinder/aidl $$f; \
	done
	@touch $@

# ----------------------------------------------------------------------------
# Object compilation rules
# ----------------------------------------------------------------------------

$(OBJ)/libbinder/%.o: aosp-src/libbinder/%.cpp $(AIDL_GEN)/.stamp
	@echo "CXX libbinder/$*.cpp"
	@$(CXX) $(LIBBINDER_CXXFLAGS) $(INC_LIBBINDER) -c $< -o $@

$(OBJ)/libutils/%.o: deps-src/libutils_binder/%.cpp
	@echo "CXX libutils/$*.cpp"
	@$(CXX) $(CXXFLAGS) $(INC_DEPS) -c $< -o $@

$(OBJ)/libcutils/%.o: deps-src/libcutils/%.cpp
	@echo "CXX libcutils/$*.cpp"
	@$(CXX) $(CXXFLAGS) $(INC_DEPS) -c $< -o $@

$(OBJ)/libbase/%.o: deps-src/libbase/%.cpp
	@echo "CXX libbase/$*.cpp"
	@$(CXX) $(CXXFLAGS) $(INC_DEPS) -c $< -o $@

# AIDL-generated .cpp files live in $(AIDL_GEN)/src/android/{os,content/pm}/.
# Pattern matches the basename.
$(OBJ)/aidl/%.o: $(AIDL_GEN)/.stamp
	@echo "CXX aidl/$*.cpp"
	@FOUND=$$(find $(AIDL_GEN)/src -name "$*.cpp" | head -1); \
	if [ -z "$$FOUND" ]; then echo "Not found: $*.cpp"; exit 1; fi; \
	$(CXX) $(LIBBINDER_CXXFLAGS) $(INC_LIBBINDER) -c $$FOUND -o $@

# ----------------------------------------------------------------------------
# Dependency static libraries
# ----------------------------------------------------------------------------

deps: $(OUT)/libutils_static.a $(OUT)/libcutils_static.a $(OUT)/libbase_static.a

$(OUT)/libutils_static.a: $(LIBUTILS_OBJS)
	@echo "AR  libutils_static.a"
	@$(AR) rcs $@ $^

$(OUT)/libcutils_static.a: $(LIBCUTILS_OBJS)
	@echo "AR  libcutils_static.a"
	@$(AR) rcs $@ $^

$(OUT)/libbase_static.a: $(LIBBASE_OBJS)
	@echo "AR  libbase_static.a"
	@$(AR) rcs $@ $^

# ----------------------------------------------------------------------------
# libbinder.so
# ----------------------------------------------------------------------------

libbinder: $(OUT)/libbinder.so

# Link shared library.  --whole-archive on libutils/libcutils so all needed
# symbols are pulled in (binder, utils, cutils funcs are referenced from many
# .cpp files in libbinder).
$(OUT)/libbinder.so: $(LIBBINDER_OBJS) $(AIDL_OBJS) $(OUT)/libutils_static.a $(OUT)/libcutils_static.a $(OUT)/libbase_static.a
	@echo "═══ Linking libbinder.so ═══"
	$(CXX) $(TARGET_FLAGS) -fPIC -shared -o $@ \
	    -Wl,--soname,libbinder.so \
	    -Wl,--no-undefined \
	    -Wl,--whole-archive \
	      $(LIBBINDER_OBJS) $(AIDL_OBJS) \
	      $(OUT)/libutils_static.a $(OUT)/libcutils_static.a $(OUT)/libbase_static.a \
	    -Wl,--no-whole-archive \
	    -lc++ -ldl -lpthread
	@echo ""
	@echo "Stripping libbinder.so..."
	@cp $@ $@.unstripped
	@$(STRIP) $@
	@echo "Sizes:"
	@ls -lh $@.unstripped $@

# ----------------------------------------------------------------------------
# Smoke test
# ----------------------------------------------------------------------------

smoke: $(OUT)/test/binder_smoke

$(OUT)/test/binder_smoke: test/binder_smoke.cc $(OUT)/libbinder.so $(OUT)/libutils_static.a
	@echo "═══ Building binder_smoke ═══"
	$(CXX) $(TARGET_FLAGS) -std=c++17 \
	    -fno-rtti -fno-exceptions \
	    $(INC_LIBBINDER) \
	    -o $@ $< \
	    -L$(OUT) -lbinder \
	    $(OUT)/libutils_static.a $(OUT)/libcutils_static.a $(OUT)/libbase_static.a \
	    -lc++ -ldl -lpthread
	@echo ""
	@echo "Smoke binary:"
	@ls -lh $@

# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# M2 — AOSP servicemanager binary
#
# Sources: main.cpp + ServiceManager.cpp + Access.cpp from
# /home/dspfac/aosp-sources/frameworks-native/cmds/servicemanager/, plus
# libcutils/multiuser.cpp from system-core.
#
# Compile-time strategy:
#   * -DVENDORSERVICEMANAGER drops vintf + perfetto code paths entirely
#     (servicemanager has #ifdef VENDORSERVICEMANAGER guards around every
#     vintf::VintfObject / perfetto reference).
#   * NOT defining __ANDROID__ makes Access.cpp's SELinux paths inert
#     (they're already guarded by #ifdef __ANDROID__, falling back to
#     returning true from all access checks — ideal for our sandbox).
#   * Link against M1's libbinder.so + our libutils/libcutils/libbase static
#     archives.  Properties_stub.cpp in libbase satisfies SetProperty.
# ----------------------------------------------------------------------------

# Match libbinder/libutils compile flags (no RTTI, no exceptions, hidden
# visibility) so that the typeinfo/vtable references generated by ServiceManager
# fit with what's emitted into libbinder.so itself.  -fno-rtti elides typeinfo
# entirely so libbinder's hidden vtables (without typeinfo) suffice.
SM_CXXFLAGS := $(CXXFLAGS) \
               -DVENDORSERVICEMANAGER=1 \
               -I$(SM_SRC) \
               -I$(AIDL_GEN)/include \
               -I$(CURDIR)/aosp-src/libbinder/include \
               -I$(CURDIR)/aosp-src/libbinder/ndk/include_cpp \
               -I$(CURDIR)/aosp-src/libbinder/liblog_stub/include \
               -I$(CURDIR)/deps-src/libutils_binder/include \
               -I$(CURDIR)/deps-src/libcutils/include \
               -I$(CURDIR)/deps-src/libbase/include \
               -I$(CURDIR)/deps-src/libsystem/include \
               -I$(CURDIR)/deps-src/libselinux_stub/include

SM_SOURCES := main.cpp ServiceManager.cpp Access.cpp
SM_OBJS    := $(patsubst %.cpp,$(OBJ)/sm/%.o,$(SM_SOURCES))

# multiuser.cpp is required by ServiceManager.cpp for multiuser_get_app_id().
# We compile it as an extra object rather than dumping into libcutils_static
# because libcutils_static built from M1 has -DNDEBUG and is_multiuser path
# wasn't needed at M1 time.
SM_EXTRA_OBJS := $(OBJ)/sm/multiuser.o

servicemanager: dirs $(OUT)/servicemanager

$(OBJ)/sm/main.o: $(SM_SRC)/main.cpp | dirs
	@echo "CXX sm/main.cpp"
	@$(CXX) $(SM_CXXFLAGS) -c $< -o $@

$(OBJ)/sm/ServiceManager.o: $(SM_SRC)/ServiceManager.cpp | dirs
	@echo "CXX sm/ServiceManager.cpp"
	@$(CXX) $(SM_CXXFLAGS) -c $< -o $@

$(OBJ)/sm/Access.o: $(SM_SRC)/Access.cpp | dirs
	@echo "CXX sm/Access.cpp"
	@$(CXX) $(SM_CXXFLAGS) -c $< -o $@

$(OBJ)/sm/multiuser.o: $(LIBCUTILS_SRC)/multiuser.cpp | dirs
	@echo "CXX sm/multiuser.cpp"
	@$(CXX) $(CXXFLAGS) $(INC_DEPS) -c $< -o $@

# The AIDL objects produced by M1 include the BnServiceManager+IServiceManager
# typeinfo/vtable symbols.  M1 compiles them with -fvisibility=hidden so they
# become "HIDDEN" in libbinder.so's symbol table and aren't exported.  The
# servicemanager binary, however, needs BnServiceManager's vtable for its
# ServiceManager subclass — so we link the AIDL .o files directly into the
# binary alongside our SM objects.  The objects' hidden visibility doesn't
# matter inside a single executable; the linker resolves them locally.
$(OUT)/servicemanager: $(SM_OBJS) $(SM_EXTRA_OBJS) $(AIDL_OBJS) $(OUT)/libbinder.so $(OUT)/libutils_static.a $(OUT)/libcutils_static.a $(OUT)/libbase_static.a
	@echo "═══ Linking servicemanager ═══"
	$(CXX) $(TARGET_FLAGS) -std=c++17 \
	    -o $@ \
	    $(SM_OBJS) $(SM_EXTRA_OBJS) $(AIDL_OBJS) \
	    -L$(OUT) -lbinder \
	    $(OUT)/libutils_static.a $(OUT)/libcutils_static.a $(OUT)/libbase_static.a \
	    -lc++ -ldl -lpthread
	@echo ""
	@echo "Stripping servicemanager..."
	@cp $@ $@.unstripped
	@$(STRIP) $@
	@echo "Sizes:"
	@ls -lh $@.unstripped $@

# ----------------------------------------------------------------------------
# M2 — sm_smoke: end-to-end addService/getService/listServices round-trip test
#
# Forks a child that registers "westlake.test.echo" with our servicemanager,
# then in the parent looks it up and verifies the binder is non-null.
# ----------------------------------------------------------------------------

sm_smoke: $(OUT)/test/sm_smoke

$(OUT)/test/sm_smoke: test/sm_smoke.cc $(OUT)/libbinder.so $(OUT)/libutils_static.a
	@echo "═══ Building sm_smoke ═══"
	$(CXX) $(TARGET_FLAGS) -std=c++17 \
	    -fno-rtti -fno-exceptions \
	    $(INC_LIBBINDER) \
	    -o $@ $< \
	    -L$(OUT) -lbinder \
	    $(OUT)/libutils_static.a $(OUT)/libcutils_static.a $(OUT)/libbase_static.a \
	    -lc++ -ldl -lpthread
	@echo ""
	@echo "sm_smoke binary:"
	@ls -lh $@

# ----------------------------------------------------------------------------
# M3 — Bionic variant
#
# Coexists with the musl variant.  Goal: a libbinder.so + servicemanager that
# load cleanly into a bionic-linked dalvikvm process on the OnePlus 6 (kernel
# 4.9.337, Android 15 LineageOS).  Mixing musl libbinder with bionic dalvikvm
# crashes on pthread/TLS conflicts, so we maintain a parallel bionic build.
#
# Strategy mirrors the musl path:
#   * Reuse all the same source files (aosp-src, deps-src, generated AIDL).
#   * Keep -U__ANDROID__ so the source goes through the same "host" code paths
#     (no vintf, no perfetto, no SELinux), avoiding pulling in symbols we
#     don't have stubs for.  We still link against bionic libc/libdl/libc++.
#   * Use NDK r25 toolchain.  This is the same NDK Makefile.bionic-arm64 in
#     art-latest uses to build the bionic dalvikvm.
#   * Reuse the existing AIDL .cpp/.h outputs from the musl build (no need to
#     re-run AIDL; the generated code is target-independent).
# ----------------------------------------------------------------------------

NDK_LLVM    := /home/dspfac/android-sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/linux-x86_64
NDK_SYSROOT := $(NDK_LLVM)/sysroot

BIONIC_CXX   := $(NDK_LLVM)/bin/clang++
BIONIC_CC    := $(NDK_LLVM)/bin/clang
BIONIC_AR    := $(NDK_LLVM)/bin/llvm-ar
BIONIC_STRIP := $(NDK_LLVM)/bin/llvm-strip

BIONIC_OUT   := $(OUT)/bionic
BIONIC_OBJ   := $(BIONIC_OUT)/obj

# Target API level 33: needed by NDK <android/binder_parcelable_utils.h>
# which calls AParcel_reset / AParcel_getDataSize (introduced in API 31)
# unconditionally.  Higher API level just unlocks those symbols; we still
# run on the OnePlus 6 (Android 15) because all our actual binder syscalls
# go through /dev/binder ioctl, not through the NDK binder C API.
BIONIC_TARGET_FLAGS := --target=aarch64-linux-android33 \
                       --sysroot=$(NDK_SYSROOT) \
                       -fuse-ld=lld \
                       --rtlib=compiler-rt

# -U__ANDROID__ AFTER any -D__ANDROID__ that the target spec might inject —
# this forces the libbinder/libutils source through the "host" branch so we
# don't pull in vintf, perfetto, SELinux, or AID lookup paths.  IServiceManager
# then dispatches host RPC to /dev/binder via getDeviceService() — exactly
# what the musl build does.
#
# M5-Step3: -D_LIBCPP_ABI_NAMESPACE=__1 forces NDK libc++ to use the platform
# default inline namespace `std::__1::` instead of NDK r25's default
# `std::__ndk1::`.  Required so that platform libs we dlopen (libaaudio.so and
# its transitive dep framework-permission-aidl-cpp.so) can resolve symbols
# such as `android::Parcel::readUtf8FromUtf16(std::__1::optional<...>*)`
# against our libbinder.so exports.  See docs/engine/M5_STEP2_REPORT.md §3.1.
BIONIC_DEFINES := $(DEFINES) -U__ANDROID__ -D_LIBCPP_ABI_NAMESPACE=__1

BIONIC_CXXFLAGS := $(BIONIC_TARGET_FLAGS) $(BIONIC_DEFINES) \
                   -fPIC -std=c++17 \
                   -fno-rtti -fno-exceptions \
                   -fvisibility=hidden \
                   -O2 \
                   -D_GNU_SOURCE \
                   '-DANDROID_FDSAN_OWNER_TYPE_NATIVE_HANDLE=((android_fdsan_owner_type)13)' \
                   '-DANDROID_FDSAN_OWNER_TYPE_PARCEL=((android_fdsan_owner_type)14)' \
                   -Wno-c99-designator \
                   -Wno-missing-field-initializers \
                   -Wno-unused-parameter \
                   -Wno-zero-as-null-pointer-constant

BIONIC_LIBBINDER_CXXFLAGS := $(BIONIC_CXXFLAGS) -DBUILDING_LIBBINDER

# M5-Step3 link flags for bionic targets that link against libbinder.so:
# use the staged platform libc++.so (`__1` namespace) instead of NDK's
# `__ndk1` static libc++.  At runtime the dynamic linker resolves -lc++ to
# /system/lib64/libc++.so on the phone.
BIONIC_LIBCXX_LINK := -nostdlib++ -L$(CURDIR)/platform-libs/lib64 -lc++

# Same includes as the musl build, plus a bionic overlay that supplies a
# newer uapi/linux/android/binder.h with BR_FROZEN_BINDER, BC_FREEZE_*,
# struct binder_frozen_state_info — none of which ship in NDK r25's sysroot
# (the headers there pre-date Android 14).  The overlay path comes FIRST so
# the kernel header override resolves before NDK's stale copy.
BIONIC_INC_LIBBINDER := -I$(AIDL_GEN)/include \
                        -I$(CURDIR)/bionic-overlay/include \
                        -I$(CURDIR)/aosp-src/libbinder/include \
                        -I$(CURDIR)/aosp-src/libbinder/ndk/include_cpp \
                        -I$(CURDIR)/aosp-src/libbinder \
                        -I$(CURDIR)/aosp-src/libbinder/liblog_stub/include \
                        -I$(CURDIR)/deps-src/libutils_binder/include \
                        -I$(CURDIR)/deps-src/libcutils/include \
                        -I$(CURDIR)/deps-src/libbase/include \
                        -I$(CURDIR)/deps-src/libsystem/include

BIONIC_INC_DEPS := -I$(CURDIR)/bionic-overlay/include \
                   -I$(CURDIR)/deps-src/libutils_binder/include \
                   -I$(CURDIR)/deps-src/libcutils/include \
                   -I$(CURDIR)/deps-src/libbase/include \
                   -I$(CURDIR)/deps-src/libsystem/include \
                   -I$(CURDIR)/aosp-src/libbinder/liblog_stub/include

# -- Object lists (live under $(BIONIC_OBJ) so they don't collide with musl). --
BIONIC_LIBBINDER_OBJS := $(patsubst aosp-src/libbinder/%.cpp,$(BIONIC_OBJ)/libbinder/%.o,$(LIBBINDER_SRCS))
BIONIC_LIBUTILS_OBJS  := $(patsubst deps-src/libutils_binder/%.cpp,$(BIONIC_OBJ)/libutils/%.o,$(LIBUTILS_FULL_SRCS))
BIONIC_LIBCUTILS_OBJS := $(patsubst deps-src/libcutils/%.cpp,$(BIONIC_OBJ)/libcutils/%.o,$(LIBCUTILS_FULL_SRCS))
BIONIC_LIBBASE_OBJS   := $(patsubst deps-src/libbase/%.cpp,$(BIONIC_OBJ)/libbase/%.o,$(LIBBASE_FULL_SRCS))
BIONIC_AIDL_OBJS      := $(patsubst %,$(BIONIC_OBJ)/aidl/%.o,$(AIDL_OBJ_NAMES))

.PHONY: all-bionic dirs-bionic libbinder-bionic deps-bionic \
        servicemanager-bionic sm_smoke-bionic smoke-bionic \
        libandroid_runtime_stub-bionic

all-bionic: dirs-bionic aidl deps-bionic libbinder-bionic smoke-bionic \
            servicemanager-bionic sm_smoke-bionic sm_registrar-bionic \
            libandroid_runtime_stub-bionic
	@echo ""
	@echo "═════ Bionic Build Complete ═════"
	@ls -lh $(BIONIC_OUT)/libbinder.so $(BIONIC_OUT)/test/binder_smoke \
	        $(BIONIC_OUT)/servicemanager $(BIONIC_OUT)/test/sm_smoke \
	        $(BIONIC_OUT)/test/sm_registrar \
	        $(BIONIC_OUT)/libandroid_runtime_stub.so 2>/dev/null || true

dirs-bionic:
	@mkdir -p $(BIONIC_OBJ)/libbinder $(BIONIC_OBJ)/libutils $(BIONIC_OBJ)/libcutils \
	          $(BIONIC_OBJ)/libbase $(BIONIC_OBJ)/aidl $(BIONIC_OBJ)/sm \
	          $(BIONIC_OBJ)/jni $(BIONIC_OUT)/test

# -- Object compilation -------------------------------------------------------
$(BIONIC_OBJ)/libbinder/%.o: aosp-src/libbinder/%.cpp $(AIDL_GEN)/.stamp | dirs-bionic
	@echo "CXX(bionic) libbinder/$*.cpp"
	@$(BIONIC_CXX) $(BIONIC_LIBBINDER_CXXFLAGS) $(BIONIC_INC_LIBBINDER) -c $< -o $@

$(BIONIC_OBJ)/libutils/%.o: deps-src/libutils_binder/%.cpp | dirs-bionic
	@echo "CXX(bionic) libutils/$*.cpp"
	@$(BIONIC_CXX) $(BIONIC_CXXFLAGS) $(BIONIC_INC_DEPS) -c $< -o $@

$(BIONIC_OBJ)/libcutils/%.o: deps-src/libcutils/%.cpp | dirs-bionic
	@echo "CXX(bionic) libcutils/$*.cpp"
	@$(BIONIC_CXX) $(BIONIC_CXXFLAGS) $(BIONIC_INC_DEPS) -c $< -o $@

$(BIONIC_OBJ)/libbase/%.o: deps-src/libbase/%.cpp | dirs-bionic
	@echo "CXX(bionic) libbase/$*.cpp"
	@$(BIONIC_CXX) $(BIONIC_CXXFLAGS) $(BIONIC_INC_DEPS) -c $< -o $@

$(BIONIC_OBJ)/aidl/%.o: $(AIDL_GEN)/.stamp | dirs-bionic
	@echo "CXX(bionic) aidl/$*.cpp"
	@FOUND=$$(find $(AIDL_GEN)/src -name "$*.cpp" | head -1); \
	if [ -z "$$FOUND" ]; then echo "Not found: $*.cpp"; exit 1; fi; \
	$(BIONIC_CXX) $(BIONIC_LIBBINDER_CXXFLAGS) $(BIONIC_INC_LIBBINDER) -c $$FOUND -o $@

# -- Dependency static archives -----------------------------------------------
deps-bionic: $(BIONIC_OUT)/libutils_static.a $(BIONIC_OUT)/libcutils_static.a $(BIONIC_OUT)/libbase_static.a

$(BIONIC_OUT)/libutils_static.a: $(BIONIC_LIBUTILS_OBJS)
	@echo "AR(bionic)  libutils_static.a"
	@$(BIONIC_AR) rcs $@ $^

$(BIONIC_OUT)/libcutils_static.a: $(BIONIC_LIBCUTILS_OBJS)
	@echo "AR(bionic)  libcutils_static.a"
	@$(BIONIC_AR) rcs $@ $^

$(BIONIC_OUT)/libbase_static.a: $(BIONIC_LIBBASE_OBJS)
	@echo "AR(bionic)  libbase_static.a"
	@$(BIONIC_AR) rcs $@ $^

# -- libbinder.so (bionic) ----------------------------------------------------
libbinder-bionic: $(BIONIC_OUT)/libbinder.so

$(BIONIC_OUT)/libbinder.so: $(BIONIC_LIBBINDER_OBJS) $(BIONIC_AIDL_OBJS) \
                            $(BIONIC_OUT)/libutils_static.a $(BIONIC_OUT)/libcutils_static.a \
                            $(BIONIC_OUT)/libbase_static.a
	@echo "═══ Linking libbinder.so (bionic) ═══"
	# M5-Step3: dynamic-link against the platform libc++.so (in platform-libs/
	# below) instead of -static-libstdc++.  The NDK r25 static libc++ is built
	# with `__ndk1` namespace symbols; the platform libc++.so on Android 14+
	# exports `__1` symbols.  Combined with -D_LIBCPP_ABI_NAMESPACE=__1 in
	# BIONIC_DEFINES, this makes our libbinder.so emit and resolve `__1`
	# symbols so libaaudio.so (and other platform libs we dlopen) can wire up.
	$(BIONIC_CXX) $(BIONIC_TARGET_FLAGS) -fPIC -shared -o $@ \
	    -Wl,--soname,libbinder.so \
	    -Wl,--no-undefined \
	    -Wl,--whole-archive \
	      $(BIONIC_LIBBINDER_OBJS) $(BIONIC_AIDL_OBJS) \
	      $(BIONIC_OUT)/libutils_static.a $(BIONIC_OUT)/libcutils_static.a \
	      $(BIONIC_OUT)/libbase_static.a \
	    -Wl,--no-whole-archive \
	    -nostdlib++ \
	    -L$(CURDIR)/platform-libs/lib64 -lc++ \
	    -ldl
	@cp $@ $@.unstripped
	@$(BIONIC_STRIP) $@
	@echo "Sizes (bionic):"
	@ls -lh $@.unstripped $@

# -- binder_smoke (bionic) ----------------------------------------------------
smoke-bionic: $(BIONIC_OUT)/test/binder_smoke

$(BIONIC_OUT)/test/binder_smoke: test/binder_smoke.cc $(BIONIC_OUT)/libbinder.so $(BIONIC_OUT)/libutils_static.a
	@echo "═══ Building binder_smoke (bionic) ═══"
	$(BIONIC_CXX) $(BIONIC_TARGET_FLAGS) -D_LIBCPP_ABI_NAMESPACE=__1 -std=c++17 \
	    -fno-rtti -fno-exceptions \
	    $(BIONIC_INC_LIBBINDER) \
	    -o $@ $< \
	    -L$(BIONIC_OUT) -lbinder \
	    $(BIONIC_OUT)/libutils_static.a $(BIONIC_OUT)/libcutils_static.a $(BIONIC_OUT)/libbase_static.a \
	    $(BIONIC_LIBCXX_LINK) -ldl
	@ls -lh $@

# -- servicemanager (bionic) --------------------------------------------------
BIONIC_SM_CXXFLAGS := $(BIONIC_CXXFLAGS) \
                      -DVENDORSERVICEMANAGER=1 \
                      -I$(SM_SRC) \
                      -I$(AIDL_GEN)/include \
                      -I$(CURDIR)/aosp-src/libbinder/include \
                      -I$(CURDIR)/aosp-src/libbinder/ndk/include_cpp \
                      -I$(CURDIR)/aosp-src/libbinder/liblog_stub/include \
                      -I$(CURDIR)/deps-src/libutils_binder/include \
                      -I$(CURDIR)/deps-src/libcutils/include \
                      -I$(CURDIR)/deps-src/libbase/include \
                      -I$(CURDIR)/deps-src/libsystem/include \
                      -I$(CURDIR)/deps-src/libselinux_stub/include

BIONIC_SM_OBJS := $(patsubst %.cpp,$(BIONIC_OBJ)/sm/%.o,$(SM_SOURCES))
BIONIC_SM_EXTRA_OBJS := $(BIONIC_OBJ)/sm/multiuser.o

servicemanager-bionic: dirs-bionic $(BIONIC_OUT)/servicemanager

$(BIONIC_OBJ)/sm/main.o: $(SM_SRC)/main.cpp | dirs-bionic
	@echo "CXX(bionic) sm/main.cpp"
	@$(BIONIC_CXX) $(BIONIC_SM_CXXFLAGS) -c $< -o $@

$(BIONIC_OBJ)/sm/ServiceManager.o: $(SM_SRC)/ServiceManager.cpp | dirs-bionic
	@echo "CXX(bionic) sm/ServiceManager.cpp"
	@$(BIONIC_CXX) $(BIONIC_SM_CXXFLAGS) -c $< -o $@

$(BIONIC_OBJ)/sm/Access.o: $(SM_SRC)/Access.cpp | dirs-bionic
	@echo "CXX(bionic) sm/Access.cpp"
	@$(BIONIC_CXX) $(BIONIC_SM_CXXFLAGS) -c $< -o $@

$(BIONIC_OBJ)/sm/multiuser.o: $(LIBCUTILS_SRC)/multiuser.cpp | dirs-bionic
	@echo "CXX(bionic) sm/multiuser.cpp"
	@$(BIONIC_CXX) $(BIONIC_CXXFLAGS) $(BIONIC_INC_DEPS) -c $< -o $@

$(BIONIC_OUT)/servicemanager: $(BIONIC_SM_OBJS) $(BIONIC_SM_EXTRA_OBJS) $(BIONIC_AIDL_OBJS) \
                              $(BIONIC_OUT)/libbinder.so $(BIONIC_OUT)/libutils_static.a \
                              $(BIONIC_OUT)/libcutils_static.a $(BIONIC_OUT)/libbase_static.a
	@echo "═══ Linking servicemanager (bionic) ═══"
	$(BIONIC_CXX) $(BIONIC_TARGET_FLAGS) -std=c++17 \
	    -o $@ \
	    $(BIONIC_SM_OBJS) $(BIONIC_SM_EXTRA_OBJS) $(BIONIC_AIDL_OBJS) \
	    -L$(BIONIC_OUT) -lbinder \
	    $(BIONIC_OUT)/libutils_static.a $(BIONIC_OUT)/libcutils_static.a $(BIONIC_OUT)/libbase_static.a \
	    $(BIONIC_LIBCXX_LINK) -ldl
	@cp $@ $@.unstripped
	@$(BIONIC_STRIP) $@
	@ls -lh $@.unstripped $@

# -- sm_smoke (bionic) --------------------------------------------------------
sm_smoke-bionic: $(BIONIC_OUT)/test/sm_smoke

$(BIONIC_OUT)/test/sm_smoke: test/sm_smoke.cc $(BIONIC_OUT)/libbinder.so $(BIONIC_OUT)/libutils_static.a
	@echo "═══ Building sm_smoke (bionic) ═══"
	$(BIONIC_CXX) $(BIONIC_TARGET_FLAGS) -D_LIBCPP_ABI_NAMESPACE=__1 -std=c++17 \
	    -fno-rtti -fno-exceptions \
	    $(BIONIC_INC_LIBBINDER) \
	    -o $@ $< \
	    -L$(BIONIC_OUT) -lbinder \
	    $(BIONIC_OUT)/libutils_static.a $(BIONIC_OUT)/libcutils_static.a $(BIONIC_OUT)/libbase_static.a \
	    $(BIONIC_LIBCXX_LINK) -ldl
	@ls -lh $@

# -- sm_registrar (bionic) — long-lived service registrar for M3 -------------
.PHONY: sm_registrar-bionic
sm_registrar-bionic: $(BIONIC_OUT)/test/sm_registrar

$(BIONIC_OUT)/test/sm_registrar: test/sm_registrar.cc $(BIONIC_OUT)/libbinder.so $(BIONIC_OUT)/libutils_static.a
	@echo "═══ Building sm_registrar (bionic) ═══"
	$(BIONIC_CXX) $(BIONIC_TARGET_FLAGS) -D_LIBCPP_ABI_NAMESPACE=__1 -std=c++17 \
	    -fno-rtti -fno-exceptions \
	    $(BIONIC_INC_LIBBINDER) \
	    -o $@ $< \
	    -L$(BIONIC_OUT) -lbinder \
	    $(BIONIC_OUT)/libutils_static.a $(BIONIC_OUT)/libcutils_static.a $(BIONIC_OUT)/libbase_static.a \
	    $(BIONIC_LIBCXX_LINK) -ldl
	@ls -lh $@

# -- libandroid_runtime_stub.so (M3 JNI shim) ---------------------------------
# Minimal native lib that bridges between dalvikvm's JNI and our libbinder.
# Implements just the JNI methods AOSP framework.jar's android.os.ServiceManager
# (and android.os.BinderInternal) declare native: getContextObject,
# waitForService, and the small Parcel/Binder hot paths the M3 HelloBinder
# test exercises.  Loaded from HelloBinder.java's static initializer.
libandroid_runtime_stub-bionic: $(BIONIC_OUT)/libandroid_runtime_stub.so

$(BIONIC_OBJ)/jni/libandroid_runtime_stub.o: native/libandroid_runtime_stub.cc $(AIDL_GEN)/.stamp | dirs-bionic
	@echo "CXX(bionic) jni/libandroid_runtime_stub.cc"
	@$(BIONIC_CXX) $(BIONIC_CXXFLAGS) \
	    $(BIONIC_INC_LIBBINDER) \
	    -I$(NDK_SYSROOT)/usr/include \
	    -c $< -o $@

$(BIONIC_OUT)/libandroid_runtime_stub.so: $(BIONIC_OBJ)/jni/libandroid_runtime_stub.o $(BIONIC_OUT)/libbinder.so
	@echo "═══ Linking libandroid_runtime_stub.so ═══"
	$(BIONIC_CXX) $(BIONIC_TARGET_FLAGS) -fPIC -shared -o $@ \
	    -Wl,--soname,libandroid_runtime_stub.so \
	    -Wl,--no-undefined \
	    $(BIONIC_OBJ)/jni/libandroid_runtime_stub.o \
	    -L$(BIONIC_OUT) -lbinder \
	    $(BIONIC_OUT)/libutils_static.a $(BIONIC_OUT)/libcutils_static.a $(BIONIC_OUT)/libbase_static.a \
	    $(BIONIC_LIBCXX_LINK) -ldl -llog
	@cp $@ $@.unstripped
	@$(BIONIC_STRIP) $@
	@ls -lh $@.unstripped $@

# -- libbinder_full_static.a (M3-finish: dalvikvm static-link bundle) ---------
# Single static archive containing ALL libbinder + AIDL + libutils + libcutils
# + libbase objects, so it can be linked whole-archive into the statically-
# linked dalvikvm binary.  This unblocks M3 by replacing the libandroid_runtime
# _stub.so .dlopen()-load path (which fails because bionic-static dalvikvm
# has a stub libdl) with a baked-in symbol approach: the binder JNI methods
# are linked into dalvikvm at build time, see art-latest/stubs/binder_jni_stub.cc.
#
# M3++: now also includes JavaBBinderHolder.o (Java Binder <-> C++ BBinder
# bridge for the same-process Stub.asInterface optimization).
.PHONY: libbinder_full_static-bionic javabinder-bionic

$(BIONIC_OBJ)/jni/JavaBBinderHolder.o: native/JavaBBinderHolder.cpp \
                                       native/JavaBBinderHolder.h | dirs-bionic
	@echo "CXX(bionic) jni/JavaBBinderHolder.cpp"
	@$(BIONIC_CXX) $(BIONIC_CXXFLAGS) \
	    $(BIONIC_INC_LIBBINDER) \
	    -I$(NDK_SYSROOT)/usr/include \
	    -c $< -o $@

# M3++ build subcommand: compile the JavaBBinderHolder + repackage the
# full static archive.  Use this after editing JavaBBinderHolder.cpp.
javabinder-bionic: $(BIONIC_OUT)/libbinder_full_static.a

libbinder_full_static-bionic: $(BIONIC_OUT)/libbinder_full_static.a

$(BIONIC_OUT)/libbinder_full_static.a: $(BIONIC_LIBBINDER_OBJS) $(BIONIC_AIDL_OBJS) \
                                       $(BIONIC_LIBUTILS_OBJS) $(BIONIC_LIBCUTILS_OBJS) \
                                       $(BIONIC_LIBBASE_OBJS) \
                                       $(BIONIC_OBJ)/jni/JavaBBinderHolder.o
	@echo "═══ AR(bionic)  libbinder_full_static.a (M3++) ═══"
	@$(BIONIC_AR) rcs $@ $^
	@ls -lh $@

# ----------------------------------------------------------------------------

clean:
	rm -rf $(OUT)

# Don't auto-generate dep files - keep build simple
