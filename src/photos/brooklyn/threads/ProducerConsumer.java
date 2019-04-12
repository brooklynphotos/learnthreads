package photos.brooklyn.threads;

public class ProducerConsumer {
    private static Object lock = new Object();
    private static int[] buffer;
    private static int counter;

    private static final boolean isFull(int[] buff) {
        return counter == buff.length;
    }

    private static final boolean isEmpty(int[] buff) {
        return counter == 0;
    }

    static class Producer {
        void produce() {
            synchronized (lock) {
                if (isFull(buffer)) {
                    System.out.println("Uh oh, it's full, wait until someone consumes something");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                buffer[counter++] = 1;
                lock.notify();
            }
        }
    }

    static class Consumer {
        void consume() {
            synchronized (lock) {
                System.out.println("Uh oh, it's empty, wait until someone produces something");
                if (isEmpty(buffer)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                buffer[--counter] = 0;
                lock.notify();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final Producer producer = new Producer();
        final Consumer consumer = new Consumer();
        buffer = new int[10];

        final int stepsCount = 50;

        final Thread prodT = new Thread(()->{
            for (int i = 0; i < stepsCount; i++) {
                producer.produce();
            }
            System.out.println("Done producing");
        });

        final Thread consT = new Thread(()->{
            for (int i = 0; i < stepsCount; i++) {
                consumer.consume();
            }
            System.out.println("Done consuming");
        });

        prodT.start();
        consT.start();

        prodT.join();
        consT.join();

        System.out.println("Done with counter="+counter);
    }
}
