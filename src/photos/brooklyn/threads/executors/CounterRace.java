package photos.brooklyn.threads.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CounterRace {
    int counter;
    final Object lock = new Object();
    public static void main(String[] args) {
        final CounterRace cr = new CounterRace();
        final List<Future<?>> tasks = new ArrayList<>(8);
        final ExecutorService ec = Executors.newFixedThreadPool(8);
        try {
            for (int i = 0; i < 4; i++) {
                tasks.add(ec.submit(cr.new Incrementer()));
            }
            for (int i = 0; i < 4; i++) {
                tasks.add(ec.submit(cr.new Decrementer()));
            }
            for (Future<?> t : tasks) {
                try {
                    t.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(cr.counter);
        }finally {
            ec.shutdown();
        }
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
