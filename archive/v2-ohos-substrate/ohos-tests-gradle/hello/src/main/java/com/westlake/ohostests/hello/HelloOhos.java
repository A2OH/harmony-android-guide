package com.westlake.ohostests.hello;

/**
 * MVP-0 (#616): minimum dalvikvm-on-OHOS smoke.
 *
 * Prints a marker line and a few system properties. The driver script
 * (scripts/run-ohos-test.sh hello) greps for the marker — keep it
 * stable.
 *
 * Acceptance line (do NOT change wording without also updating
 * scripts/run-ohos-test.sh):
 *     westlake-dalvik on OHOS — main reached
 */
public class HelloOhos {
    public static void main(String[] args) {
        System.out.println("westlake-dalvik on OHOS — main reached");
        System.out.println("args.length=" + args.length);
        for (String a : args) {
            System.out.println("  arg: " + a);
        }
        System.out.println("os.arch=" + System.getProperty("os.arch"));
        System.out.println("java.vm.name=" + System.getProperty("java.vm.name"));
        System.out.println("java.vm.version=" + System.getProperty("java.vm.version"));
        System.out.println("java.specification.version="
                + System.getProperty("java.specification.version"));
    }
}
