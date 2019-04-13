package photos.brooklyn.threads.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumer {
    private static final int maxBufferSize = 10;
    private Lock lock = new ReentrantLock();
    private Condition isEmpty = lock.newCondition();
    private Condition isFull = lock.newCondition();
    private List<Integer> buffer = new ArrayList<>();
    private final List<Producer> producers;
    final List<Consumer> consumers;

    private static final boolean isEmpty(List<Integer> buf) {
        return buf.size() == 0;
    }

    private static final boolean isFull(List<Integer> buf) {
        return buf.size() == maxBufferSize;
    }

    class Consumer implements Callable<String> {
        @Override
        public String call() throws InterruptedException {
            int count = 0;
            while (count++ < 50) {
                lock.lock();
                try{
                    while (isEmpty(buffer)) {
                        // have to wait until it's not empty
                        isEmpty.await();
                    }
                    buffer.remove(buffer.size() - 1);
                    // notify that buffer has changed
                    isFull.signalAll();
                }finally {
                    lock.unlock();
                }
            }
            return "Consumed " + count;
        }
    }
    class Producer implements Callable<String> {

        @Override
        public String call() throws InterruptedException {
            int count = 0;
            while (count++ < 50) {
                lock.lock();
                try {
                    while (isFull(buffer)) {
                        isFull.await();
                    }
                    buffer.add(1);
                    // notify that buffer is no longer empty
                    isEmpty.signalAll();
                }finally {
                    lock.unlock();
                }
            }
            return "Produced " + count;
        }
    }

    ProducerConsumer() {
        producers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            producers.add(new Producer());
        }
        consumers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            consumers.add(new Consumer());
        }
    }

    void start() throws InterruptedException {
        System.out.println("Starting Producer-Consumer");
        final List<Callable<String>> calls = new ArrayList<>(producers.size() + consumers.size());
        calls.addAll(producers);
        calls.addAll(consumers);
        final ExecutorService exs = Executors.newFixedThreadPool(calls.size());
        try {
            final List<Future<String>> fs = exs.invokeAll(calls);
            fs.forEach(f-> {
                try {
                    System.out.println("Result: "+f.get());
                } catch (InterruptedException  | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }finally {
            exs.shutdown();
            System.out.println("Shutdown");
        }

    }

    public static void main(String[] args) throws InterruptedException {
        final ProducerConsumer pc = new ProducerConsumer();
        pc.start();

    }
}
