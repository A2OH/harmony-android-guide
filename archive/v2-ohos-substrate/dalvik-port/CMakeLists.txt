cmake_minimum_required(VERSION 3.10)
project(dalvikvm-portable C CXX)

set(CMAKE_C_STANDARD 11)
set(CMAKE_CXX_STANDARD 11)

# Dalvik source location
set(DALVIK_SRC "/home/dspfac/dalvik-kitkat" CACHE PATH "Path to AOSP Dalvik KitKat source")
set(AOSP_SRC "/home/dspfac/aosp-android-11" CACHE PATH "Path to AOSP Android 11 source (for JNI headers)")

set(VM "${DALVIK_SRC}/vm")
set(LIBDEX "${DALVIK_SRC}/libdex")
set(COMPAT "${CMAKE_CURRENT_SOURCE_DIR}/compat")

# ── Compiler flags ──

add_compile_definitions(
    HAVE_LITTLE_ENDIAN
    HAVE_ENDIAN_H
    HAVE_SYS_UIO_H
    _GNU_SOURCE
    ANDROID_SMP=1
    LOG_TAG="dalvikvm"
    DVM_SHOW_EXCEPTION=1
    # Use portable interpreter (no assembly, no JIT)
    # No JIT
    # No JDWP (debugger)
    # No hprof
)

# Suppress warnings for old C++ code
add_compile_options(
    -Wall -Wno-unused-parameter -Wno-sign-compare
    -Wno-unused-function -Wno-unused-variable
    -Wno-format -Wno-write-strings
    -fPIC
)

# ── Include paths ──

include_directories(
    ${VM}                   # vm/*.h (Dalvik.h, Common.h, etc.)
    ${DALVIK_SRC}           # for "libdex/..." includes
    ${COMPAT}               # our Android compat shim headers
    ${AOSP_SRC}/libnativehelper/include_jni  # jni.h
)

# ── libdex (DEX file parser) ──

add_library(dex STATIC
    ${LIBDEX}/DexFile.cpp
    ${LIBDEX}/DexSwapVerify.cpp
    ${LIBDEX}/DexCatch.cpp
    ${LIBDEX}/DexClass.cpp
    ${LIBDEX}/DexDataMap.cpp
    ${LIBDEX}/DexDebugInfo.cpp
    ${LIBDEX}/DexInlines.cpp
    ${LIBDEX}/DexOpcodes.cpp
    ${LIBDEX}/DexOptData.cpp
    ${LIBDEX}/DexProto.cpp
    ${LIBDEX}/DexUtf.cpp
    ${LIBDEX}/InstrUtils.cpp
    ${LIBDEX}/Leb128.cpp
    ${LIBDEX}/OptInvocation.cpp
    ${LIBDEX}/SysUtil.cpp
    ${LIBDEX}/ZipArchive.cpp
    ${LIBDEX}/CmdUtils.cpp
    ${LIBDEX}/sha1.cpp
)
target_link_libraries(dex z)

# ── libdvm (Dalvik VM core) ──

# Core VM sources (from Dvm.mk)
set(DVM_CORE_SRCS
    ${VM}/AllocTracker.cpp
    ${VM}/Atomic.cpp
    ${VM}/AtomicCache.cpp
    ${VM}/BitVector.cpp
    ${VM}/CheckJni.cpp
    ${VM}/Ddm.cpp
    ${VM}/Debugger.cpp
    ${VM}/DvmDex.cpp
    ${VM}/Exception.cpp
    ${VM}/Hash.cpp
    ${VM}/IndirectRefTable.cpp
    ${VM}/Init.cpp
    ${VM}/InitRefs.cpp
    ${VM}/InlineNative.cpp
    ${VM}/Inlines.cpp
    ${VM}/Intern.cpp
    ${VM}/Jni.cpp
    ${VM}/JarFile.cpp
    ${VM}/LinearAlloc.cpp
    ${VM}/Misc.cpp
    ${VM}/Native.cpp
    ${VM}/PointerSet.cpp
    ${VM}/Profile.cpp
    ${VM}/RawDexFile.cpp
    ${VM}/ReferenceTable.cpp
    ${VM}/SignalCatcher.cpp
    ${VM}/StdioConverter.cpp
    ${VM}/Sync.cpp
    ${VM}/Thread.cpp
    ${VM}/UtfString.cpp
)

# Allocator (mark-sweep GC, not copying)
set(DVM_ALLOC_SRCS
    ${VM}/alloc/Alloc.cpp
    ${VM}/alloc/CardTable.cpp
    ${VM}/alloc/HeapBitmap.cpp
    ${VM}/alloc/HeapDebug.cpp
    ${VM}/alloc/Heap.cpp
    ${VM}/alloc/DdmHeap.cpp
    ${VM}/alloc/Verify.cpp
    ${VM}/alloc/Visit.cpp
    ${VM}/alloc/DlMalloc.cpp
    ${VM}/alloc/HeapSource.cpp
    ${VM}/alloc/MarkSweep.cpp
)

# Analysis (bytecode verification, optimization)
set(DVM_ANALYSIS_SRCS
    ${VM}/analysis/CodeVerify.cpp
    ${VM}/analysis/DexPrepare.cpp
    ${VM}/analysis/DexVerify.cpp
    ${VM}/analysis/Liveness.cpp
    ${VM}/analysis/Optimize.cpp
    ${VM}/analysis/RegisterMap.cpp
    ${VM}/analysis/VerifySubs.cpp
    ${VM}/analysis/VfyBasicBlock.cpp
)

# Interpreter (portable C — no assembly)
set(DVM_INTERP_SRCS
    ${VM}/interp/Interp.cpp
    ${VM}/interp/Stack.cpp
    ${VM}/mterp/Mterp.cpp
    ${VM}/mterp/out/InterpC-portable.cpp
)

# Object system
set(DVM_OO_SRCS
    ${VM}/oo/AccessCheck.cpp
    ${VM}/oo/Array.cpp
    ${VM}/oo/Class.cpp
    ${VM}/oo/Object.cpp
    ${VM}/oo/Resolve.cpp
    ${VM}/oo/TypeCheck.cpp
)

# Reflection
set(DVM_REFLECT_SRCS
    ${VM}/reflect/Annotation.cpp
    ${VM}/reflect/Proxy.cpp
    ${VM}/reflect/Reflect.cpp
)

# Native method implementations (java.lang.*, dalvik.system.*)
set(DVM_NATIVE_SRCS
    ${VM}/native/InternalNative.cpp
    ${VM}/native/dalvik_bytecode_OpcodeInfo.cpp
    ${VM}/native/dalvik_system_DexFile.cpp
    ${VM}/native/dalvik_system_VMDebug.cpp
    ${VM}/native/dalvik_system_VMRuntime.cpp
    ${VM}/native/dalvik_system_VMStack.cpp
    ${VM}/native/dalvik_system_Zygote.cpp
    ${VM}/native/java_lang_Class.cpp
    ${VM}/native/java_lang_Double.cpp
    ${VM}/native/java_lang_Float.cpp
    ${VM}/native/java_lang_Math.cpp
    ${VM}/native/java_lang_Object.cpp
    ${VM}/native/java_lang_Runtime.cpp
    ${VM}/native/java_lang_String.cpp
    ${VM}/native/java_lang_System.cpp
    ${VM}/native/java_lang_Throwable.cpp
    ${VM}/native/java_lang_VMClassLoader.cpp
    ${VM}/native/java_lang_VMThread.cpp
    ${VM}/native/java_lang_reflect_AccessibleObject.cpp
    ${VM}/native/java_lang_reflect_Array.cpp
    ${VM}/native/java_lang_reflect_Constructor.cpp
    ${VM}/native/java_lang_reflect_Field.cpp
    ${VM}/native/java_lang_reflect_Method.cpp
    ${VM}/native/java_lang_reflect_Proxy.cpp
    ${VM}/native/java_util_concurrent_atomic_AtomicLong.cpp
    ${VM}/native/org_apache_harmony_dalvik_NativeTestTarget.cpp
    ${VM}/native/org_apache_harmony_dalvik_ddmc_DdmServer.cpp
    ${VM}/native/org_apache_harmony_dalvik_ddmc_DdmVmInternal.cpp
    ${VM}/native/sun_misc_Unsafe.cpp
)

# JDWP (debugger) — include but can be stubbed
set(DVM_JDWP_SRCS
    ${VM}/jdwp/ExpandBuf.cpp
    ${VM}/jdwp/JdwpAdb.cpp
    ${VM}/jdwp/JdwpConstants.cpp
    ${VM}/jdwp/JdwpEvent.cpp
    ${VM}/jdwp/JdwpHandler.cpp
    ${VM}/jdwp/JdwpMain.cpp
    ${VM}/jdwp/JdwpSocket.cpp
)

# Hprof (heap profiler) — include for completeness
set(DVM_HPROF_SRCS
    ${VM}/hprof/Hprof.cpp
    ${VM}/hprof/HprofClass.cpp
    ${VM}/hprof/HprofHeap.cpp
    ${VM}/hprof/HprofOutput.cpp
    ${VM}/hprof/HprofString.cpp
)

# OS layer — use linux.cpp (generic, no Android deps)
set(DVM_OS_SRCS
    ${VM}/os/linux.cpp
)

# Architecture — use generic (FFI-based, no assembly)
set(DVM_ARCH_SRCS
    ${VM}/arch/generic/Call.cpp
    ${VM}/arch/generic/Hints.cpp
)

# Tests (optional)
set(DVM_TEST_SRCS
    ${VM}/test/TestHash.cpp
    ${VM}/test/TestIndirectRefTable.cpp
)

add_library(dvm STATIC
    ${DVM_CORE_SRCS}
    ${DVM_ALLOC_SRCS}
    ${DVM_ANALYSIS_SRCS}
    ${DVM_INTERP_SRCS}
    ${DVM_OO_SRCS}
    ${DVM_REFLECT_SRCS}
    ${DVM_NATIVE_SRCS}
    ${DVM_JDWP_SRCS}
    ${DVM_HPROF_SRCS}
    ${DVM_OS_SRCS}
    ${DVM_ARCH_SRCS}
    ${DVM_TEST_SRCS}
)

target_link_libraries(dvm dex z pthread dl ffi)

# ── dalvikvm executable ──
# Minimal launcher that loads a DEX file and calls main()
# TODO: create standalone launcher (for now, just build the library)
# add_executable(dalvikvm launcher.cpp)
# target_link_libraries(dalvikvm dvm)
