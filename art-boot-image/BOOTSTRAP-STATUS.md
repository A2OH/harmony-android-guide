# ART dex2oat Boot Image Bootstrap - Status

## What was accomplished

### 1. ART-compatible core library DEX (core-patched-boot.jar)
- Started from AOSP SDK30 core-for-system-modules.jar (1630 .class files)
- Patched java.lang.Object with ART shadow fields (`shadow$_klass_`, `shadow$_monitor_`)
- Added ART-internal classes: StringFactory, Daemons, BootClassLoader, ClassExt,
  EmulatedStackFrame, VMRuntime, DexPathList, FinalizerReference, etc.
- Replaced Thread/ThreadGroup/Throwable/Reference with field-correct versions
- Added package-private String methods (getChars, getCharsNoCheck) for AOSP vtable
- Added CharSequence default methods (chars, codePoints) for correct vtable count
- Converted to DEX format using `dx --dex --core-library`
- Result: 434KB JAR with DEX inside (1630+ classes)

### 2. Generated correct asm_defines.h (154 defines)
- Compiled AOSP's `art/tools/cpp-define-generator/asm_defines.cc`
- Extracted all ART struct offset constants needed for assembly
- Saved to `asm_defines.h.generated` and installed in art-universal-build/stubs/

### 3. Assembled real x86_64 entry points
- quick_entrypoints_x86_64.S (136KB .o) - all quick trampoline entry points
- jni_entrypoints_x86_64.S (2KB .o) - JNI lookup stubs
- memcmp16_x86_64.S (5KB .o) - String comparison helper

### 4. Boot image creation progress
dex2oat successfully:
- Parses arguments and opens the core DEX file
- Loads java.lang.Object with correct ART shadow fields (offset=0, offset=4)
- Loads java.lang.String and links it with CharSequence/Comparable/Serializable
- Resolves CharSequence default methods (chars/codePoints) into String vtable
- Creates boot.vdex (1.1MB) containing the verified DEX data
- **Crashes** at rip=0x0 during embedded vtable/IMT population in LinkClass

### 5. Key size values verified
- sizeof(mirror::Class) = 120
- sizeof(mirror::Object) = 8
- sizeof(mirror::String) = 16
- String::ClassSize(k64) = 688 (pre-allocated in InitWithoutImage)
- Object::kVTableLength = 11
- Expected String vtable = 67 entries (11 Object + 54 String + 2 CharSequence defaults)

## Root cause of remaining crash

The crash at `rip=0x0` occurs during **PopulateEmbeddedVTable** or **SetImt** in
`ClassLinker::LinkClass`. The null pointer comes from uninitialized method entry
points in ArtMethod objects.

### What was tested and eliminated:
1. **Trampoline null hypothesis** - DISPROVED. Patching `class_linker.cc:855` to
   always set trampolines (removing `!IsAotCompiler()` guard) did NOT fix the crash.
   The stub entrypoint addresses are non-null (verified via `nm`).

2. **Assembly entrypoint hypothesis** - DISPROVED. Linking the real assembled .S
   files instead of abort() stubs caused a different crash (assembly code expects
   Thread TLS which isn't set up in the AOT compiler context).

3. **String vtable size mismatch** - MOSTLY FIXED. The String class now has the
   correct vtable entry count (55 declared + bridge + 2 defaults = 67 total).
   The class size matches String::ClassSize(k64) = 688.

### Most likely actual cause:
The crash is a **write beyond allocated class object bounds**. During
`LinkClass -> PopulateEmbeddedVTable`, dex2oat writes 67 vtable entry pointers
(67 x 8 = 536 bytes) into the class object. However, the class object may have
been allocated with a different size due to the static fields (CASE_INSENSITIVE_ORDER
etc.) taking up additional space in the class, leading to an off-by-N overflow
that corrupts the heap and eventually causes a jump to address 0.

## Recommended next step: Download pre-built boot.art

The cleanest solution is to obtain a pre-built Android 11 x86_64 boot image:

```bash
# Download Android 11 x86_64 system image from Google
# Extract boot.art + boot.oat + boot.vdex from /system/framework/x86_64/
# Use those directly with dex2oat --boot-image=boot.art for app compilation
```

Sources:
- Android SDK system images (`system-images;android-30;google_apis;x86_64`)
- Android factory images (Pixel emulator)
- AOSP CI build artifacts

This bypasses the bootstrapping problem entirely - we only need dex2oat to
compile APP DEX files, not to create the boot image from scratch.

## Key files
- `core-patched-boot.jar` - ART-compatible core library DEX (434KB)
- `core-patched.dex` - Raw DEX file (1.5MB, 1630+ classes)
- `asm_defines.h.generated` - Correct ART struct offsets (154 defines)
- `patches/Object.java` - Patched Object with ART shadow fields
- `patches/String.java` - String with package-private methods for correct vtable
- `patches/CharSequence.java` - With default methods for correct vtable count
