package photos.brooklyn.threads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * all the threads are done regardless of other threads, but the latch is awaited outside of each thread call
 */
public class CountDownMultiThread {
    public int countDown(final int threadCount, final int latchCount) throws InterruptedException {
        final CountDownLatch l = new CountDownLatch(latchCount);
        final AtomicInteger counter = new AtomicInteger();
        for (int i = 0; i < threadCount; i++) {
            final Thread t = new Thread(() -> {
                for (int j = 0; j < latchCount / threadCount; j++) {
                    l.countDown();
                    System.out.println("Counter updated: " + counter.incrementAndGet());
                    try {
                        Thread.sleep(500l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Done sleeping: " + Thread.currentThread().getName());
                }
            });
            t.start();
        }
        System.out.println("Starting to wait");
        l.await();
        System.out.println("Done with the latch");
        return counter.get();
    }

    public static void main(String[] args) throws Exception {
        new CountDownMultiThread().countDown(5, 10);
    }
}