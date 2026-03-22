/**
 * VMBenchmark -- Comprehensive Dalvik vs ART performance comparison.
 *
 * Constraints:
 *   - No lambdas (KitKat Dalvik)
 *   - No String.format (KitKat natives missing)
 *   - No String.split / Pattern.compile
 *   - Must run on both Dalvik (KitKat portable) and ART (Android 11)
 */
public class VMBenchmark {

    // ---- Interfaces / helper classes for virtual dispatch ----

    interface Callable {
        int call(int x);
    }

    static class Adder implements Callable {
        public int call(int x) { return x + 1; }
    }

    static class FieldBox {
        int value;
    }

    static class SmallObject {
        int a;
        int b;
        long c;
    }

    // ---- Empty method for call overhead ----
    static int emptyMethod(int x) {
        return x;
    }

    // ---- Fibonacci ----
    static int fib(int n) {
        if (n <= 1) return n;
        return fib(n - 1) + fib(n - 2);
    }

    // ---- Timing helper ----
    static long now() {
        return System.nanoTime();
    }

    static long toMs(long nanos) {
        return nanos / 1000000L;
    }

    // ====================================================================
    // Benchmark methods -- each returns elapsed milliseconds
    // ====================================================================

    static long benchMethodCalls() {
        long start = now();
        int sink = 0;
        for (int i = 0; i < 10000000; i++) {
            sink = emptyMethod(sink);
        }
        long elapsed = now() - start;
        if (sink == Integer.MIN_VALUE) System.out.println("prevent opt");
        return toMs(elapsed);
    }

    static long benchVirtualDispatch() {
        Callable c = new Adder();
        long start = now();
        int sink = 0;
        for (int i = 0; i < 10000000; i++) {
            sink = c.call(sink);
        }
        long elapsed = now() - start;
        if (sink == Integer.MIN_VALUE) System.out.println("prevent opt");
        return toMs(elapsed);
    }

    static long benchFieldAccess() {
        FieldBox box = new FieldBox();
        long start = now();
        for (int i = 0; i < 10000000; i++) {
            box.value = i;
            int v = box.value;
            box.value = v + 1;
        }
        long elapsed = now() - start;
        if (box.value == Integer.MIN_VALUE) System.out.println("prevent opt");
        return toMs(elapsed);
    }

    static long benchFibonacci() {
        long start = now();
        int result = fib(40);
        long elapsed = now() - start;
        System.out.println("  fib(40) = " + result);
        return toMs(elapsed);
    }

    static long benchTightLoop() {
        long start = now();
        long sum = 0;
        for (int i = 0; i < 100000000; i++) {
            sum += i;
        }
        long elapsed = now() - start;
        if (sum == Long.MIN_VALUE) System.out.println("prevent opt");
        return toMs(elapsed);
    }

    static long benchObjectAlloc() {
        long start = now();
        SmallObject last = null;
        for (int i = 0; i < 1000000; i++) {
            SmallObject obj = new SmallObject();
            obj.a = i;
            obj.b = i + 1;
            obj.c = (long) i * 2;
            last = obj;
        }
        long elapsed = now() - start;
        if (last != null && last.a == Integer.MIN_VALUE) System.out.println("prevent opt");
        return toMs(elapsed);
    }

    static long benchArrayOps() {
        int[] arr = new int[1000000];
        long start = now();
        // Fill
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i * 3;
        }
        // Sum
        long sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        long elapsed = now() - start;
        if (sum == Long.MIN_VALUE) System.out.println("prevent opt");
        return toMs(elapsed);
    }

    static long benchStringConcat() {
        long start = now();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            sb.append("x");
        }
        long elapsed = now() - start;
        int len = sb.length();
        if (len == 0) System.out.println("prevent opt");
        return toMs(elapsed);
    }

    static long benchHashMap() {
        java.util.HashMap<String, Integer> map = new java.util.HashMap<String, Integer>();
        long start = now();
        // Insert
        for (int i = 0; i < 100000; i++) {
            map.put("key" + i, Integer.valueOf(i));
        }
        // Lookup
        long sum = 0;
        for (int i = 0; i < 100000; i++) {
            Integer val = map.get("key" + i);
            if (val != null) sum += val.intValue();
        }
        long elapsed = now() - start;
        if (sum == Long.MIN_VALUE) System.out.println("prevent opt");
        return toMs(elapsed);
    }

    // ====================================================================
    // Main
    // ====================================================================

    public static void main(String[] args) {
        System.out.println("=== VM Benchmark Suite ===");
        System.out.println("java.vm.name: " + System.getProperty("java.vm.name", "unknown"));
        System.out.println("java.vm.version: " + System.getProperty("java.vm.version", "unknown"));
        System.out.println("");

        // Warmup pass (shorter, just to prime things)
        System.out.println("--- Warmup ---");
        benchMethodCalls();
        benchVirtualDispatch();
        benchFieldAccess();
        // Skip fib warmup (too slow for warmup)
        benchTightLoop();
        benchObjectAlloc();
        benchArrayOps();
        benchStringConcat();
        benchHashMap();
        System.out.println("--- Warmup done ---");
        System.out.println("");

        // Measured pass
        System.out.println("--- Benchmark Results ---");

        long t1 = benchMethodCalls();
        System.out.println("METHOD_CALLS_10M: " + t1 + "ms");

        long t2 = benchVirtualDispatch();
        System.out.println("VIRTUAL_DISPATCH_10M: " + t2 + "ms");

        long t3 = benchFieldAccess();
        System.out.println("FIELD_ACCESS_10M: " + t3 + "ms");

        long t4 = benchFibonacci();
        System.out.println("FIBONACCI_40: " + t4 + "ms");

        long t5 = benchTightLoop();
        System.out.println("TIGHT_LOOP_100M: " + t5 + "ms");

        long t6 = benchObjectAlloc();
        System.out.println("OBJECT_ALLOC_1M: " + t6 + "ms");

        long t7 = benchArrayOps();
        System.out.println("ARRAY_OPS_1M: " + t7 + "ms");

        long t8 = benchStringConcat();
        System.out.println("STRING_CONCAT_100K: " + t8 + "ms");

        long t9 = benchHashMap();
        System.out.println("HASHMAP_100K: " + t9 + "ms");

        System.out.println("--- Done ---");

        // ART shutdown workaround
        Runtime.getRuntime().halt(0);
    }
}
