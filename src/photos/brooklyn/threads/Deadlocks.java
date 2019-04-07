package photos.brooklyn.threads;

public class Deadlocks {
    public static void main(String[] args) throws InterruptedException {
        final Deadlocks dl = new Deadlocks();
        final Runnable aR = ()->dl.a();
        final Runnable bR = ()->dl.b();

        final Thread aT = new Thread(aR);
        final Thread bT = new Thread(bR);

        aT.start();
        bT.start();

        aT.join();
        bT.join();
    }

    private Object aKey = new Object();
    private Object bKey = new Object();

    public void a(){
        synchronized (aKey) {
            System.out.println("["+Thread.currentThread().getName()+"] I am in a");
            b();
        }
    }
    public void b(){
        synchronized (bKey) {
            System.out.println("["+Thread.currentThread().getName()+"] I am in b");
            c();
        }
    }
    public void c(){
        synchronized (aKey) {
            System.out.println("["+Thread.currentThread().getName()+"] I am in c");
        }
    }
}
