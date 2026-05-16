OH Build GN Args (rk3568 / DAYU200)
====================================

Required --gn-args for successful compilation:

  allow_sanitize_debug=true          # Bypass CFI sanitizer assertion
  treat_warnings_as_errors=false     # Disable -Werror (weekly_20260302 has format string bugs fixed in later releases)

Full build command:
  ./build.sh --product-name rk3568 --ccache \
      --gn-args 'allow_sanitize_debug=true treat_warnings_as_errors=false' \
      --build-target abilityms \
      --build-target libappms \
      --build-target scene_session_manager \
      --build-target scene_session \
      --build-target libbms

irtoc segfault workaround:
  The irtoc tool segfaults when processing interpreter_inline.bc, blocking
  libarkruntime.so compilation. Current workaround: phony-edge patches in
  build/ninja_patches/patch_{irtoc,arkruntime,ani_helpers}.sh applied after
  gn gen. These turn the relevant build edges into no-op phony targets so
  ninja can proceed without actually invoking irtoc.

  Do NOT use runtime_core_enable_codegen=false -- it causes
  "SetupLLVMDispatchTableImpl undeclared" errors in interpreter_impl.cpp.

  NOTE: this project does not actually need libarkruntime.so at runtime,
  so the phony approach is correct — we only need the build graph to
  complete, not the library to be usable.

musl SYS_* fix:
  Before first build (or after GN regen), run:
    bash ~/adapter/build/oh_build_patches/musl_syscall_fix.sh ~/oh/out/rk3568
  Then use --fast-rebuild if musl was the only failure.

  Long-term fix: apply the source-level patch at
    ohos_patches/third_party/musl/arch/aarch64/bits/syscall.h.in.patch
  which targets syscall.h.in (the source template), not the generated file.

Target device: DAYU200 (RK3568, 32-bit userspace)
OH Version: OpenHarmony 7.0.0.18 (weekly_20260302)
Last updated: 2026-04-11
