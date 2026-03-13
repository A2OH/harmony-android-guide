# ═══════════════════════════════════════════════════════════════
# Dalvik Portable VM — Standalone Build
# Builds libdex + libdvm + dalvikvm from AOSP KitKat sources
#
# Usage:
#   make -j$(nproc)                    # Build for Linux x86_64
#   make -j$(nproc) TARGET=ohos        # Build for OpenHarmony aarch64
#   make -j$(nproc) TARGET=ohos-x86    # Build for OpenHarmony x86_64
# ═══════════════════════════════════════════════════════════════

DALVIK    := /home/dspfac/dalvik-kitkat
AOSP      := /home/dspfac/aosp-android-11
VM        := $(DALVIK)/vm
LIBDEX    := $(DALVIK)/libdex
COMPAT    := $(CURDIR)/compat
BUILD     := $(CURDIR)/build

# ── Toolchain selection ──

OHOS_LLVM := /home/dspfac/openharmony/prebuilts/clang/ohos/linux-x86_64/llvm
OHOS_MUSL := /home/dspfac/openharmony/third_party/musl
OHOS_SYSROOT := $(CURDIR)/ohos-sysroot

ifeq ($(TARGET),ohos)
  # OpenHarmony aarch64 (primary target)
  CXX       := $(OHOS_LLVM)/bin/clang++
  CC        := $(OHOS_LLVM)/bin/clang
  AR        := $(OHOS_LLVM)/bin/llvm-ar
  RANLIB    := $(OHOS_LLVM)/bin/llvm-ranlib
  BUILD     := $(CURDIR)/build-ohos-aarch64
  TARGET_FLAGS := --target=aarch64-linux-ohos \
                  --sysroot=$(OHOS_SYSROOT) \
                  -D__MUSL__ \
                  -fuse-ld=lld \
                  --rtlib=compiler-rt
  LINK_FLAGS := -fuse-ld=lld --rtlib=compiler-rt \
                --target=aarch64-linux-ohos \
                --sysroot=$(OHOS_SYSROOT) \
                -static
  LDFLAGS   := -lpthread -ldl -lz -lffi
else ifeq ($(TARGET),ohos-arm32)
  # OpenHarmony ARM32 (for QEMU virt-arm)
  OHOS_SYSROOT_ARM32 := $(CURDIR)/ohos-sysroot-arm32
  CXX       := $(OHOS_LLVM)/bin/clang++
  CC        := $(OHOS_LLVM)/bin/clang
  AR        := $(OHOS_LLVM)/bin/llvm-ar
  RANLIB    := $(OHOS_LLVM)/bin/llvm-ranlib
  BUILD     := $(CURDIR)/build-ohos-arm32
  TARGET_FLAGS := --target=arm-linux-ohos \
                  --sysroot=$(OHOS_SYSROOT_ARM32) \
                  -D__MUSL__ \
                  -march=armv7-a -mfloat-abi=softfp -mfpu=neon \
                  -fuse-ld=lld \
                  --rtlib=compiler-rt
  LINK_FLAGS := -fuse-ld=lld --rtlib=compiler-rt \
                --target=arm-linux-ohos \
                --sysroot=$(OHOS_SYSROOT_ARM32) \
                -march=armv7-a -mfloat-abi=softfp -mfpu=neon \
                -static
  LDFLAGS   := -lpthread -ldl -lz -lffi
else ifeq ($(TARGET),ohos-x86)
  # OpenHarmony x86_64 (for OHOS emulator)
  CXX       := $(OHOS_LLVM)/bin/clang++
  CC        := $(OHOS_LLVM)/bin/clang
  AR        := $(OHOS_LLVM)/bin/llvm-ar
  RANLIB    := $(OHOS_LLVM)/bin/llvm-ranlib
  BUILD     := $(CURDIR)/build-ohos-x86_64
  TARGET_FLAGS := --target=x86_64-linux-ohos \
                  --sysroot=$(OHOS_SYSROOT) \
                  -D__MUSL__ \
                  -fuse-ld=lld \
                  --rtlib=compiler-rt
  LINK_FLAGS := -fuse-ld=lld --rtlib=compiler-rt \
                --target=x86_64-linux-ohos \
                --sysroot=$(OHOS_SYSROOT) \
                -static
  LDFLAGS   := -lpthread -ldl -lz -lffi
else
  # Linux x86_64 (default — native build)
  CXX       := g++
  CC        := gcc
  AR        := ar
  RANLIB    := ranlib
  TARGET_FLAGS :=
  LINK_FLAGS :=
  LDFLAGS   := -lpthread -ldl -l:libz.so.1 -l:libffi.so.8
endif

# Include paths
INCLUDES  := -I$(VM) \
             -I$(DALVIK) \
             -I$(COMPAT) \
             -I$(AOSP)/libnativehelper/include_jni \
             -I$(AOSP)/external/zlib \
             -I$(AOSP)/external/dlmalloc

# Common defines
COMMON_DEFS := -DHAVE_LITTLE_ENDIAN \
               -DHAVE_ENDIAN_H \
               -DHAVE_SYS_UIO_H \
               -DANDROID_SMP=1 \
               -DLOG_TAG='"dalvikvm"' \
               -DDVM_SHOW_EXCEPTION=1

# Compiler flags
CFLAGS    := -std=c11 -fPIC -O2 \
             $(COMMON_DEFS) \
             -D_GNU_SOURCE \
             -Wno-unused-parameter \
             -Wno-sign-compare \
             $(TARGET_FLAGS) \
             $(INCLUDES)

CXXFLAGS  := -std=c++11 -fPIC -O0 -g \
             $(COMMON_DEFS) \
             -D_GNU_SOURCE \
             -DNDEBUG \
             -DHAVE_POSIX_FILEMAP \
             -DDVM_NO_ASM_INTERP=1 \
             -DDVM_JMP_TABLE_MTERP=1 \
             -DHAVE_STRLCPY=1 \
             -Dtypeof=__typeof__ \
             -fpermissive \
             -include unistd.h \
             -include sys/uio.h \
             -include sys/resource.h \
             -Wno-unused-parameter \
             -Wno-sign-compare \
             -Wno-write-strings \
             -Wno-format \
             -Wno-narrowing \
             -Wno-pointer-arith \
             $(TARGET_FLAGS) \
             $(INCLUDES)

# ── Source files ──

LIBDEX_SRCS := \
    $(LIBDEX)/DexFile.cpp \
    $(LIBDEX)/DexSwapVerify.cpp \
    $(LIBDEX)/DexCatch.cpp \
    $(LIBDEX)/DexClass.cpp \
    $(LIBDEX)/DexDataMap.cpp \
    $(LIBDEX)/DexDebugInfo.cpp \
    $(LIBDEX)/DexInlines.cpp \
    $(LIBDEX)/DexOpcodes.cpp \
    $(LIBDEX)/DexOptData.cpp \
    $(LIBDEX)/DexProto.cpp \
    $(LIBDEX)/DexUtf.cpp \
    $(LIBDEX)/InstrUtils.cpp \
    $(LIBDEX)/Leb128.cpp \
    $(LIBDEX)/OptInvocation.cpp \
    $(LIBDEX)/SysUtil.cpp \
    $(LIBDEX)/ZipArchive.cpp \
    $(LIBDEX)/CmdUtils.cpp \
    $(LIBDEX)/sha1.cpp

DVM_SRCS := \
    $(VM)/AllocTracker.cpp \
    $(VM)/Atomic.cpp \
    $(VM)/AtomicCache.cpp \
    $(VM)/BitVector.cpp \
    $(VM)/CheckJni.cpp \
    $(VM)/Ddm.cpp \
    $(VM)/Debugger.cpp \
    $(VM)/DvmDex.cpp \
    $(VM)/Exception.cpp \
    $(VM)/Hash.cpp \
    $(VM)/IndirectRefTable.cpp \
    $(VM)/Init.cpp \
    $(VM)/InitRefs.cpp \
    $(VM)/InlineNative.cpp \
    $(VM)/Inlines.cpp \
    $(VM)/Intern.cpp \
    $(VM)/Jni.cpp \
    $(VM)/JarFile.cpp \
    $(VM)/LinearAlloc.cpp \
    $(VM)/Misc.cpp \
    $(VM)/Native.cpp \
    $(VM)/PointerSet.cpp \
    $(VM)/Profile.cpp \
    $(VM)/RawDexFile.cpp \
    $(VM)/ReferenceTable.cpp \
    $(VM)/SignalCatcher.cpp \
    $(VM)/StdioConverter.cpp \
    $(VM)/Sync.cpp \
    $(VM)/Thread.cpp \
    $(VM)/UtfString.cpp \
    $(VM)/alloc/Alloc.cpp \
    $(VM)/alloc/CardTable.cpp \
    $(VM)/alloc/HeapBitmap.cpp \
    $(VM)/alloc/HeapDebug.cpp \
    $(VM)/alloc/Heap.cpp \
    $(VM)/alloc/DdmHeap.cpp \
    $(VM)/alloc/Verify.cpp \
    $(VM)/alloc/Visit.cpp \
    $(VM)/alloc/DlMalloc.cpp \
    $(VM)/alloc/HeapSource.cpp \
    $(VM)/alloc/MarkSweep.cpp \
    $(VM)/analysis/CodeVerify.cpp \
    $(VM)/analysis/DexPrepare.cpp \
    $(VM)/analysis/DexVerify.cpp \
    $(VM)/analysis/Liveness.cpp \
    $(VM)/analysis/Optimize.cpp \
    $(VM)/analysis/RegisterMap.cpp \
    $(VM)/analysis/VerifySubs.cpp \
    $(VM)/analysis/VfyBasicBlock.cpp \
    $(VM)/interp/Interp.cpp \
    $(VM)/interp/Stack.cpp \
    $(VM)/mterp/Mterp.cpp \
    $(VM)/mterp/out/InterpC-portable.cpp \
    $(VM)/oo/AccessCheck.cpp \
    $(VM)/oo/Array.cpp \
    $(VM)/oo/Class.cpp \
    $(VM)/oo/Object.cpp \
    $(VM)/oo/Resolve.cpp \
    $(VM)/oo/TypeCheck.cpp \
    $(VM)/reflect/Annotation.cpp \
    $(VM)/reflect/Proxy.cpp \
    $(VM)/reflect/Reflect.cpp \
    $(VM)/native/InternalNative.cpp \
    $(VM)/native/dalvik_bytecode_OpcodeInfo.cpp \
    $(VM)/native/dalvik_system_DexFile.cpp \
    $(VM)/native/dalvik_system_VMDebug.cpp \
    $(VM)/native/dalvik_system_VMRuntime.cpp \
    $(VM)/native/dalvik_system_VMStack.cpp \
    $(VM)/native/dalvik_system_Zygote.cpp \
    $(VM)/native/java_lang_Class.cpp \
    $(VM)/native/java_lang_Double.cpp \
    $(VM)/native/java_lang_Float.cpp \
    $(VM)/native/java_lang_Math.cpp \
    $(VM)/native/java_lang_Object.cpp \
    $(VM)/native/java_lang_Runtime.cpp \
    $(VM)/native/java_lang_String.cpp \
    $(VM)/native/java_lang_System.cpp \
    $(VM)/native/java_lang_Throwable.cpp \
    $(VM)/native/java_lang_VMClassLoader.cpp \
    $(VM)/native/java_lang_VMThread.cpp \
    $(VM)/native/java_lang_reflect_AccessibleObject.cpp \
    $(VM)/native/java_lang_reflect_Array.cpp \
    $(VM)/native/java_lang_reflect_Constructor.cpp \
    $(VM)/native/java_lang_reflect_Field.cpp \
    $(VM)/native/java_lang_reflect_Method.cpp \
    $(VM)/native/java_lang_reflect_Proxy.cpp \
    $(VM)/native/java_util_concurrent_atomic_AtomicLong.cpp \
    $(VM)/native/org_apache_harmony_dalvik_NativeTestTarget.cpp \
    $(VM)/native/org_apache_harmony_dalvik_ddmc_DdmServer.cpp \
    $(VM)/native/org_apache_harmony_dalvik_ddmc_DdmVmInternal.cpp \
    $(VM)/native/sun_misc_Unsafe.cpp \
    $(VM)/jdwp/ExpandBuf.cpp \
    $(VM)/jdwp/JdwpAdb.cpp \
    $(VM)/jdwp/JdwpConstants.cpp \
    $(VM)/jdwp/JdwpEvent.cpp \
    $(VM)/jdwp/JdwpHandler.cpp \
    $(VM)/jdwp/JdwpMain.cpp \
    $(VM)/jdwp/JdwpSocket.cpp \
    $(VM)/hprof/Hprof.cpp \
    $(VM)/hprof/HprofClass.cpp \
    $(VM)/hprof/HprofHeap.cpp \
    $(VM)/hprof/HprofOutput.cpp \
    $(VM)/hprof/HprofString.cpp \
    $(VM)/os/linux.cpp \
    $(VM)/arch/generic/Call.cpp \
    $(VM)/arch/generic/Hints.cpp \
    $(VM)/test/TestHash.cpp

# Object files
LIBDEX_OBJS := $(patsubst %.cpp,$(BUILD)/%.o,$(notdir $(LIBDEX_SRCS)))
DVM_OBJS    := $(patsubst %.cpp,$(BUILD)/dvm/%.o,$(notdir $(DVM_SRCS)))

.PHONY: all clean first-pass build-all link dalvikvm sysroot

all: build-all link dalvikvm dexopt

# ── OHOS sysroot construction ──
# Run: make sysroot TARGET=ohos
# Creates ohos-sysroot/ with musl headers + arch bits for cross-compilation

sysroot:
	@echo "═══ Building OHOS sysroot for cross-compilation ═══"
	@mkdir -p $(OHOS_SYSROOT)/usr/include/bits
	@mkdir -p $(OHOS_SYSROOT)/usr/lib
	@# Copy musl headers
	@cp -r $(OHOS_MUSL)/ndk_musl_include/* $(OHOS_SYSROOT)/usr/include/
	@# Copy arch-specific bits (aarch64 by default)
ifeq ($(TARGET),ohos-x86)
	@cp -r $(OHOS_MUSL)/arch/x86_64/bits/* $(OHOS_SYSROOT)/usr/include/bits/
	@cp $(OHOS_MUSL)/arch/x86_64/*.h $(OHOS_SYSROOT)/usr/include/ 2>/dev/null || true
	@# Copy generic bits that aren't overridden by arch
	@cp -n $(OHOS_MUSL)/arch/generic/bits/* $(OHOS_SYSROOT)/usr/include/bits/ 2>/dev/null || true
	@# Copy x86_64 compiler-rt libs
	@cp $(OHOS_LLVM)/lib/clang/15.0.4/lib/x86_64-linux-ohos/* $(OHOS_SYSROOT)/usr/lib/ 2>/dev/null || true
	@cp $(OHOS_LLVM)/lib/x86_64-linux-ohos/* $(OHOS_SYSROOT)/usr/lib/ 2>/dev/null || true
else
	@cp -r $(OHOS_MUSL)/arch/aarch64/bits/* $(OHOS_SYSROOT)/usr/include/bits/
	@cp $(OHOS_MUSL)/arch/aarch64/*.h $(OHOS_SYSROOT)/usr/include/ 2>/dev/null || true
	@# Copy generic bits that aren't overridden by arch
	@cp -n $(OHOS_MUSL)/arch/generic/bits/* $(OHOS_SYSROOT)/usr/include/bits/ 2>/dev/null || true
	@# Copy aarch64 compiler-rt libs
	@cp $(OHOS_LLVM)/lib/clang/15.0.4/lib/aarch64-linux-ohos/* $(OHOS_SYSROOT)/usr/lib/ 2>/dev/null || true
	@cp $(OHOS_LLVM)/lib/aarch64-linux-ohos/* $(OHOS_SYSROOT)/usr/lib/ 2>/dev/null || true
endif
	@# Copy linux kernel headers (needed for syscalls, ioctl, etc.)
	@if [ -d "$(OHOS_MUSL)/porting/linux/user/include" ]; then \
		cp -r $(OHOS_MUSL)/porting/linux/user/include/* $(OHOS_SYSROOT)/usr/include/ 2>/dev/null || true; \
	fi
	@echo "Sysroot created at: $(OHOS_SYSROOT)"
	@echo "Headers: $$(find $(OHOS_SYSROOT)/usr/include -name '*.h' | wc -l) files"
	@echo "Libs:    $$(find $(OHOS_SYSROOT)/usr/lib -type f | wc -l) files"

# First pass: just try to compile the first few files to see what headers are missing
first-pass:
	@mkdir -p $(BUILD)
	@echo "═══ Dalvik Port — First Compilation Pass ═══"
	@echo "Testing: libdex/DexFile.cpp"
	$(CXX) $(CXXFLAGS) -c $(LIBDEX)/DexFile.cpp -o $(BUILD)/DexFile.o 2>&1 | head -30
	@echo ""
	@echo "Testing: vm/Common.h (via Init.cpp)"
	$(CXX) $(CXXFLAGS) -c $(VM)/Init.cpp -o $(BUILD)/Init.o 2>&1 | head -50
	@echo ""
	@echo "Testing: portable interpreter"
	$(CXX) $(CXXFLAGS) -c $(VM)/mterp/out/InterpC-portable.cpp -o $(BUILD)/InterpC-portable.o 2>&1 | head -30

# Full build: compile all files, report errors
build-all:
	@mkdir -p $(BUILD)
	@PASS=0; FAIL=0; ERRORS=""; \
	for src in $(LIBDEX_SRCS) $(DVM_SRCS); do \
		name=$$(basename $$src .cpp); \
		$(CXX) $(CXXFLAGS) -c $$src -o $(BUILD)/$$name.o 2>/tmp/dalvik-build-err.txt; \
		if [ $$? -eq 0 ]; then \
			PASS=$$((PASS+1)); \
		else \
			FAIL=$$((FAIL+1)); \
			ERRORS="$$ERRORS\n═══ FAILED: $$src ═══\n$$(cat /tmp/dalvik-build-err.txt | grep 'error:' | head -5)"; \
		fi; \
	done; \
	echo "═══ Build Results ═══"; \
	echo "Compiled: $$PASS"; \
	echo "Failed:   $$FAIL"; \
	echo "Total:    $$((PASS+FAIL))"; \
	if [ $$FAIL -gt 0 ]; then \
		echo ""; \
		echo "═══ Error Summary ═══"; \
		echo -e "$$ERRORS"; \
	fi

# Compile link_fixups separately (without AOSP dlmalloc include path to avoid macro conflicts)
$(BUILD)/link_fixups.o: $(COMPAT)/link_fixups.cpp
	$(CXX) -std=c++11 -fPIC -O2 -fpermissive $(TARGET_FLAGS) -c $< -o $@

# Compile libcore bridge (JNI natives for boot classes)
$(BUILD)/libcore_bridge.o: $(COMPAT)/libcore_bridge.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

# Link: create static library from all object files
link: build-all $(BUILD)/link_fixups.o $(BUILD)/libcore_bridge.o
	@echo "═══ Linking libdvm.a ═══"
	$(AR) rcs $(BUILD)/libdvm.a $(BUILD)/*.o
	@echo "Created: $(BUILD)/libdvm.a ($$(du -h $(BUILD)/libdvm.a | cut -f1))"

# Build dalvikvm launcher
dalvikvm: link
	@echo "═══ Building dalvikvm launcher ═══"
	$(CXX) $(CXXFLAGS) $(LINK_FLAGS) -o $(BUILD)/dalvikvm launcher.cpp $(BUILD)/libdvm.a $(LDFLAGS)
	@echo "Created: $(BUILD)/dalvikvm"
	@ls -lh $(BUILD)/dalvikvm
ifneq ($(TARGET),)
	@file $(BUILD)/dalvikvm
endif

# Build dexopt tool (required for DEX verification/optimization at runtime)
dexopt: link
	@echo "═══ Building dexopt ═══"
	$(CXX) $(CXXFLAGS) $(LINK_FLAGS) -o $(BUILD)/dexopt $(DALVIK)/dexopt/OptMain.cpp $(BUILD)/libdvm.a $(LDFLAGS)
	@echo "Created: $(BUILD)/dexopt"
	@ls -lh $(BUILD)/dexopt

clean:
	rm -rf $(BUILD)

clean-ohos:
	rm -rf build-ohos-aarch64 build-ohos-x86_64 ohos-sysroot
