/* libartpalette-system.so stub — all Palette methods return OK */
#include <stddef.h>
#include <stdint.h>

typedef int palette_status_t;
#define PALETTE_STATUS_OK 0

palette_status_t PaletteSchedSetPriority(int32_t tid, int32_t java_priority) { return PALETTE_STATUS_OK; }
palette_status_t PaletteSchedGetPriority(int32_t tid, int32_t* java_priority) { if (java_priority) *java_priority = 5; return PALETTE_STATUS_OK; }
palette_status_t PaletteWriteCrashThreadStacks(const char* s, size_t l) { return PALETTE_STATUS_OK; }
palette_status_t PaletteTraceEnabled(int* enabled) { if (enabled) *enabled = 0; return PALETTE_STATUS_OK; }
palette_status_t PaletteTraceBegin(const char* name) { return PALETTE_STATUS_OK; }
palette_status_t PaletteTraceEnd(void) { return PALETTE_STATUS_OK; }
palette_status_t PaletteTraceIntegerValue(const char* name, int32_t value) { return PALETTE_STATUS_OK; }
palette_status_t PaletteAshmemCreateRegion(const char* name, size_t size, int* fd) { return PALETTE_STATUS_OK; }
palette_status_t PaletteAshmemSetProtRegion(int fd, int prot) { return PALETTE_STATUS_OK; }
palette_status_t PaletteCreateOdrefreshStagingDirectory(const char** dir) { return PALETTE_STATUS_OK; }
palette_status_t PaletteShouldReportDex2oatCompilation(int* r) { if (r) *r = 0; return PALETTE_STATUS_OK; }
palette_status_t PaletteNotifyStartDex2oatCompilation(int a, int b, int c, int d) { return PALETTE_STATUS_OK; }
palette_status_t PaletteNotifyEndDex2oatCompilation(int a, int b, int c, int d) { return PALETTE_STATUS_OK; }
palette_status_t PaletteNotifyDexFileLoaded(const char* p) { return PALETTE_STATUS_OK; }
palette_status_t PaletteNotifyOatFileLoaded(const char* p) { return PALETTE_STATUS_OK; }
palette_status_t PaletteShouldReportJniInvocations(int* r) { if (r) *r = 0; return PALETTE_STATUS_OK; }
palette_status_t PaletteNotifyBeginJniInvocation(void* env) { return PALETTE_STATUS_OK; }
palette_status_t PaletteNotifyEndJniInvocation(void* env) { return PALETTE_STATUS_OK; }
palette_status_t PaletteReportLockContention(void) { return PALETTE_STATUS_OK; }
palette_status_t PaletteSetTaskProfiles(int32_t tid, const char* const* p, size_t l) { return PALETTE_STATUS_OK; }
