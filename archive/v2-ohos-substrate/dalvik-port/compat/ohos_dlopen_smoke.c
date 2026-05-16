/*
 * ohos_dlopen_smoke — minimal smoke test proving that a 32-bit ARM EABI5
 * process on DAYU200 OHOS 7.0.0.18 can dlopen the OHOS NAPI / native_window
 * shared libraries in-process. This is the architectural premise behind
 * CR60 (the 32-bit dalvikvm bitness pivot).
 *
 * If this binary prints "[ohos-dlopen-smoke] PASS" on the board, then a
 * 32-bit dalvikvm built from this same toolchain can also dlopen these
 * libraries via System.loadLibrary / JNI — eliminating the need for the
 * M6 daemon + AF_UNIX bridge in the production XComponent path.
 *
 * Built as a *dynamic* (not -static) ELF — we need the dynamic loader
 * (/lib/ld-musl-arm.so.1) to perform real dlopen() resolution. A static
 * musl dlopen() exists but only supports DT_NEEDED libraries linked at
 * build time, not arbitrary runtime targets.
 *
 * Bitness discipline: this is C; all pointer-sized arithmetic uses
 * uintptr_t / size_t (none required here — we only print sym addresses).
 *
 * Author: agent 11 (CR60 spike, 2026-05-14)
 */
#include <dlfcn.h>
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

/* One library + one well-known symbol per library. The libraries are at
 * non-default paths on DAYU200; we use explicit absolute paths so we
 * don't depend on ld-musl's search path being configured. */
struct probe {
    const char *path;
    const char *sym;
};

int main(int argc, char **argv) {
    struct probe probes[] = {
        { "/system/lib/platformsdk/libace_napi.z.so",   "napi_get_undefined" },
        { "/system/lib/chipset-sdk-sp/libnative_window.so", "OH_NativeWindow_NativeWindowRequestBuffer" },
        { "/system/lib/ndk/libace_ndk.z.so",            "OH_NativeXComponent_GetXComponentId" },
    };
    int nprobes = sizeof(probes) / sizeof(probes[0]);
    int ok = 0;
    /* Allow caller to limit to a single probe by index, e.g.
     * `ohos_dlopen_smoke 0` only probes libace_napi. Used during the
     * E5 spike to isolate which library crashes the loader. */
    int start = 0, end = nprobes;
    if (argc >= 2) {
        int idx = atoi(argv[1]);
        if (idx >= 0 && idx < nprobes) { start = idx; end = idx + 1; }
    }

    setvbuf(stdout, NULL, _IOLBF, 0);
    fprintf(stdout, "[ohos-dlopen-smoke] starting; range [%d,%d) of %d probes\n",
            start, end, nprobes);
    fflush(stdout);

    for (int i = start; i < end; i++) {
        fprintf(stdout, "[ohos-dlopen-smoke] probe %d: dlopen %s\n", i, probes[i].path);
        fflush(stdout);
        void *h = dlopen(probes[i].path, RTLD_NOW);
        fprintf(stdout, "[ohos-dlopen-smoke] probe %d: dlopen returned %p\n", i, h);
        fflush(stdout);
        if (!h) {
            const char *e = dlerror();
            fprintf(stdout, "[ohos-dlopen-smoke] dlopen FAIL: %s -> %s\n",
                    probes[i].path, e ? e : "(no error)");
            fflush(stdout);
            continue;
        }
        void *s = dlsym(h, probes[i].sym);
        fprintf(stdout, "[ohos-dlopen-smoke] probe %d: dlsym returned %p\n", i, s);
        fflush(stdout);
        if (!s) {
            const char *e = dlerror();
            fprintf(stdout, "[ohos-dlopen-smoke] dlsym FAIL: %s::%s -> %s\n",
                    probes[i].path, probes[i].sym, e ? e : "(no error)");
            fflush(stdout);
            /* Don't dlclose() on failure path during diag — some musl
             * builds crash inside dlclose with a partially-loaded SO. */
            continue;
        }
        fprintf(stdout, "[ohos-dlopen-smoke] OK  : %s::%s @ %p (handle %p)\n",
                probes[i].path, probes[i].sym, s, h);
        fflush(stdout);
        /* Skip dlclose during smoke — keeps loader state simple. */
        ok++;
    }

    int range = end - start;
    if (ok == range) {
        fprintf(stdout, "[ohos-dlopen-smoke] PASS  %d/%d\n", ok, range);
        return 0;
    } else {
        fprintf(stdout, "[ohos-dlopen-smoke] FAIL  %d/%d\n", ok, range);
        return 1;
    }
}
