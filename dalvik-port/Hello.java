public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello from Dalvik on Linux!");
        System.out.println("os.arch = " + System.getProperty("os.arch"));
        System.out.println("os.name = " + System.getProperty("os.name"));
        System.out.println("java.vm.name = " + System.getProperty("java.vm.name"));
        System.out.println("user.name = " + System.getProperty("user.name"));
        System.out.println("1 + 1 = " + (1 + 1));
        System.out.println("Done!");
    }
}
