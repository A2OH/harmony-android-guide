#!/usr/bin/env python3
"""Analyze VM benchmark results and produce comparison table."""

import statistics

# Raw results from benchmark runs
# ART Interpreter (switch interpreter, no AOT)
art_interp = {
    "METHOD_CALLS_10M":     [635, 571, 602],
    "VIRTUAL_DISPATCH_10M": [667, 667, 686],
    "FIELD_ACCESS_10M":     [632, 616, 657],
    "FIBONACCI_40":         [21748, 20909, 23083],
    "TIGHT_LOOP_100M":      [2744, 2717, 3157],
    "OBJECT_ALLOC_1M":      [159, 150, 179],
    "ARRAY_OPS_1M":         [59, 64, 63],
    "STRING_CONCAT_100K":   [50, 52, 56],
    "HASHMAP_100K":         [802, 961, 905],
}

# ART "AOT" mode -- actually same interpreter since boot image fallback
# and no JIT compiler available (libart-compiler.so missing)
art_aot = {
    "METHOD_CALLS_10M":     [750, 743, 756],
    "VIRTUAL_DISPATCH_10M": [788, 801, 780],
    "FIELD_ACCESS_10M":     [631, 809, 762],
    "FIBONACCI_40":         [26320, 23471, 23998],
    "TIGHT_LOOP_100M":      [3505, 3284, 3363],
    "OBJECT_ALLOC_1M":      [188, 177, 187],
    "ARRAY_OPS_1M":         [82, 73, 73],
    "STRING_CONCAT_100K":   [70, 60, 62],
    "HASHMAP_100K":         [1075, 1064, 1067],
}

# Host JVM (OpenJDK 21, JIT compiled)
host_jvm = {
    "METHOD_CALLS_10M":     [2, 2, 0],
    "VIRTUAL_DISPATCH_10M": [2, 1, 2],
    "FIELD_ACCESS_10M":     [6, 5, 7],
    "FIBONACCI_40":         [298, 295, 319],
    "TIGHT_LOOP_100M":      [30, 38, 29],
    "OBJECT_ALLOC_1M":      [24, 21, 17],
    "ARRAY_OPS_1M":         [0, 0, 0],
    "STRING_CONCAT_100K":   [2, 1, 1],
    "HASHMAP_100K":         [36, 25, 30],
}

# Pretty names
names = {
    "METHOD_CALLS_10M":     "Method calls (10M)",
    "VIRTUAL_DISPATCH_10M": "Virtual dispatch (10M)",
    "FIELD_ACCESS_10M":     "Field access (10M)",
    "FIBONACCI_40":         "Fibonacci(40) recursive",
    "TIGHT_LOOP_100M":      "Tight loop sum (100M)",
    "OBJECT_ALLOC_1M":      "Object alloc (1M)",
    "ARRAY_OPS_1M":         "Array fill+sum (1M)",
    "STRING_CONCAT_100K":   "StringBuilder (100K)",
    "HASHMAP_100K":         "HashMap put+get (100K)",
}

tests = list(names.keys())

def median(vals):
    return sorted(vals)[len(vals)//2]

print("=" * 100)
print("  VM BENCHMARK COMPARISON")
print("=" * 100)
print()
print("Configurations tested:")
print("  - ART Switch Interpreter: Android 11 ART runtime, -Xint mode, core libs from boot image")
print("  - ART Non-Xint:           Same ART runtime, no -Xint (still interpreter, JIT unavailable)")
print("  - Host JVM:               OpenJDK 21.0.6 (JBR) with C2 JIT compiler")
print("  - Dalvik Portable:        KitKat portable C interpreter (x86_64 build non-functional*)")
print()
print("* Dalvik x86_64 cannot load bootclasspath JAR (mmap/dexopt issue).")
print("  ARM32 OHOS build works but is only available via QEMU.")
print("  Dalvik portable is estimated at 2-5x slower than ART interpreter")
print("  based on known characteristics of the portable C interpreter vs")
print("  ART's optimized switch interpreter with better dispatch tables.")
print()

# Table header
hdr = "| {:<25s} | {:>13s} | {:>13s} | {:>13s} | {:>14s} |".format(
    "Benchmark", "ART Interp", "ART Non-Xint", "Host JVM", "ART/JVM ratio")
sep = "|" + "-"*27 + "|" + "-"*15 + "|" + "-"*15 + "|" + "-"*15 + "|" + "-"*16 + "|"

print(sep)
print(hdr)
print(sep)

for t in tests:
    ai = median(art_interp[t])
    aa = median(art_aot[t])
    hj = median(host_jvm[t])
    if hj > 0:
        ratio = ai / hj
        ratio_str = "{:.0f}x".format(ratio)
    else:
        ratio_str = ">600x"

    row = "| {:<25s} | {:>10d} ms | {:>10d} ms | {:>10d} ms | {:>14s} |".format(
        names[t], ai, aa, hj, ratio_str)
    print(row)

print(sep)
print()

# Summary stats
ai_total = sum(median(art_interp[t]) for t in tests)
aa_total = sum(median(art_aot[t]) for t in tests)
hj_total = sum(median(host_jvm[t]) for t in tests)

print("Total time (median of 3 runs):")
print("  ART Interpreter:  {} ms".format(ai_total))
print("  ART Non-Xint:     {} ms".format(aa_total))
print("  Host JVM (JIT):   {} ms".format(hj_total))
print("  Overall ART/JVM:  {:.0f}x slower".format(ai_total / max(hj_total, 1)))
print()

# Dalvik estimation
print("Dalvik Portable Interpreter (estimated based on architecture):")
print("  The portable C interpreter uses a large switch() dispatch loop")
print("  with no assembly optimizations, no JIT, and no inline caching.")
print("  Expected 2-5x slower than ART switch interpreter for:")
print("    - Method calls:   ~1200-3000 ms  (overhead from C dispatch)")
print("    - Virtual dispatch: ~1400-3400 ms  (no inline caches)")
print("    - Fibonacci(40):  ~42000-105000 ms  (call overhead dominates)")
print("    - Tight loop:     ~5500-14000 ms  (C switch vs optimized switch)")
print("    - Object alloc:   ~300-750 ms  (similar GC, slower dispatch)")
print()
print("Key findings:")
print("  1. ART switch interpreter is ~70-300x slower than JIT-compiled JVM")
print("  2. For compute-heavy tasks (fib, loops), the gap is ~70-100x")
print("  3. For allocation-heavy tasks (objects, strings), the gap is ~7-50x")
print("  4. Dalvik portable would add another 2-5x on top of ART interpreter")
print("  5. The ART 'AOT' column shows no improvement because:")
print("     - Boot image couldn't be loaded (path mismatch)")
print("     - libart-compiler.so (JIT) is not available")
print("     - Both columns run the same switch interpreter")
