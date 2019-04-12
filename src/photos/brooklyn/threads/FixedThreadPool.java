package photos.brooklyn.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FixedThreadPool {
    public static void main(String[] args) {
        final Runnable task = () -> System.out.println("I am in thread " + Thread.currentThread());

        final ExecutorService s = Executors.newFixedThreadPool(4);

        try {
            for (int i = 0; i < 10; i++) {
                s.execute(task);
            }
        }finally {
            s.shutdown();
        }

    }
}
