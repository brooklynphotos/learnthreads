package photos.brooklyn.threads;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * each thread will do its own thing but waits for the cyclic barrier to be done before leaving the await() call
 */
public class CyclicBarrierMultiThread {
    public int countDown(final int latchCount) {
        final CyclicBarrier b = new CyclicBarrier(latchCount);
        final AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < latchCount; i++) {
            final int marker = i;
            final Thread t = new Thread(()->{
                System.out.println("Doing some work " + Thread.currentThread().getName());
                try {
                    Thread.sleep(marker*100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Started await"+Thread.currentThread().getName());
                try {
                    b.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("Done waiting: "+count.incrementAndGet()+" : "+Thread.currentThread().getName());
            });
            t.start();
        }
        return count.get();
    }

    public static void main(String[] args) throws Exception {
        new CyclicBarrierMultiThread().countDown(10);
    }
}
