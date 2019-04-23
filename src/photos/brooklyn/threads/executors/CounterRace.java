package photos.brooklyn.threads.executors;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterRace {
    public static void main(String[] args) {
        run(new CounterWithLock());
        run(new CounterWithAtomic());
    }

    static void run(Counter cr){
        final LocalDateTime start = LocalDateTime.now();
        final List<Future<?>> tasks = new ArrayList<>(8);
        final ExecutorService ec = Executors.newFixedThreadPool(8);
        try {
            for (int i = 0; i < 4; i++) {
                tasks.add(ec.submit(cr.getIncrementer()));
            }
            for (int i = 0; i < 4; i++) {
                tasks.add(ec.submit(cr.getDecrementer()));
            }
            for (Future<?> t : tasks) {
                try {
                    t.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(cr.getCounter());
            System.out.println(ChronoUnit.MICROS.between(LocalDateTime.now(), start));
        }finally {
            ec.shutdown();
        }
    }
}

class CounterWithLock implements Counter {
    private int counter;
    final Object lock = new Object();

    @Override
    public Runnable getIncrementer() {
        return new Incrementer();
    }

    @Override
    public int getCounter() {
        return counter;
    }

    @Override
    public Runnable getDecrementer() {
        return new Decrementer();
    }

    class Incrementer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                synchronized (lock) {
                    counter++;
                }
            }
        }
    }

    class Decrementer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                synchronized (lock) {
                    counter--;
                }
            }
        }
    }
}

class CounterWithAtomic implements Counter {
    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Runnable getIncrementer() {
        return new Incrementer();
    }

    @Override
    public int getCounter() {
        return counter.get();
    }

    @Override
    public Runnable getDecrementer() {
        return new Decrementer();
    }

    class Incrementer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                counter.incrementAndGet();
            }
        }
    }

    class Decrementer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                counter.decrementAndGet();
            }
        }
    }
}
interface Counter {
    Runnable getIncrementer();
    int getCounter();
    Runnable getDecrementer();
}
