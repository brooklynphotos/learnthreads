package photos.brooklyn.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * https://www.baeldung.com/java-start-thread
 */
public class ScheduledMultiThread {
    final private ScheduledExecutorService svc = Executors.newScheduledThreadPool(2);
    final private AtomicInteger sharedIndex = new AtomicInteger(0);
    public void startCalculating() {
        final ScheduledFuture<?> res = svc.scheduleAtFixedRate(() -> {
            System.out.println("Entered schedule");
            longTask();
        }, 100, 450, TimeUnit.MILLISECONDS);

    }

    private int longTask() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("In a long task: "+sharedIndex.incrementAndGet());
        return 1;
    }

    public static void main(String[] args) throws InterruptedException {
        final ScheduledMultiThread t = new ScheduledMultiThread();
        t.startCalculating();
        Thread.sleep(2000);
        t.svc.shutdown();
    }
}
