package photos.brooklyn.threads;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HashmapRace {
    private Map<Integer, String> m = new HashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    String get(int k) {
        readLock.lock();
        try{
            return m.get(k);
        } finally {
            readLock.unlock();
        }
    }

    void put(int k, String v) {
        writeLock.lock();
        try{
            m.put(k, v);
        }finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        final HashmapRace m = new HashmapRace();

        class Producer implements Callable<Integer> {
            final int loopCount = 100_000;

            @Override
            public Integer call() {
                for (int i = 0; i < loopCount; i++) {
                    final Integer randNum = new Random().nextInt(10_000);
                    m.put(randNum, randNum.toString());
                    if (m.get(randNum)==null) {
                        System.out.println(Thread.currentThread()+"] Failed to put key "+randNum);
                    }
                }
                return 1;
            }
        }
        final int count = 4;
        final ExecutorService es = Executors.newFixedThreadPool(count);
        try {
            for (int i = 0; i < count; i++) {
                es.submit(new Producer());
            }
        }finally {
            es.shutdown();
        }
    }

}
