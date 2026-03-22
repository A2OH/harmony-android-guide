package java.lang;
public class Object {
    private transient Class<?> shadow$_klass_;
    private transient int shadow$_monitor_;
    public Object() {}
    public final Class<?> getClass() { return shadow$_klass_; }
    public int hashCode() { return 0; }
    public boolean equals(Object obj) { return (this == obj); }
    protected Object clone() throws CloneNotSupportedException { throw new CloneNotSupportedException(); }
    public String toString() { return ""; }
    public final native void notify();
    public final native void notifyAll();
    public final native void wait(long timeout) throws InterruptedException;
    public final native void wait(long timeout, int nanos) throws InterruptedException;
    public final void wait() throws InterruptedException { wait(0); }
    protected void finalize() throws Throwable {}
}
