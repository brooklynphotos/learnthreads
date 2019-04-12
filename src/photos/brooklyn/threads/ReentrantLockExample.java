package photos.brooklyn.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample {
    private Lock lock = new ReentrantLock();
    private int counter;

    public void lockOne() {
        lock.lock();
        try {
            Thread.sleep(500);
            lockInner();
            System.out.println(Thread.currentThread()+"/lockOne Counter: "+counter);
            counter++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void lockInner() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread()+"/lockInner Counter: "+counter);
        }finally {
            lock.unlock();
        }
    }

    public void lockTwo() {
        lock.lock();
        try {
            Thread.sleep(800);
            System.out.println(Thread.currentThread()+"/lockTwo Counter: "+counter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        final ReentrantLockExample e = new ReentrantLockExample();
        final ExecutorService es = Executors.newFixedThreadPool(2);
        try {
            es.execute(()->{
                e.lockOne();
            });
            es.execute(()->{
                e.lockTwo();
            });
        }finally {
            es.shutdown();
        }
    }
}
