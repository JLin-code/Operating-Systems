import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadersWritersSimulation {
    static class SharedBoard {
        private String value = "Initial data";
        private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);

        String read(String readerName) {
            rwLock.readLock().lock();
            try {
                System.out.println(readerName + " reads: " + value);
                return value;
            } finally {
                rwLock.readLock().unlock();
            }
        }

        void write(String writerName, String newValue) {
            rwLock.writeLock().lock();
            try {
                value = newValue;
                System.out.println(writerName + " writes: " + value);
            } finally {
                rwLock.writeLock().unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int readers = 4;
        final int writers = 2;
        final int readsPerReader = 6;
        final int writesPerWriter = 4;

        SharedBoard board = new SharedBoard();
        Random random = new Random();

        Thread[] readerThreads = new Thread[readers];
        for (int i = 0; i < readers; i++) {
            final int readerId = i + 1;
            readerThreads[i] = new Thread(() -> {
                for (int j = 0; j < readsPerReader; j++) {
                    try {
                        Thread.sleep(120 + random.nextInt(180));
                        board.read("Reader-" + readerId);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            });
            readerThreads[i].start();
        }

        Thread[] writerThreads = new Thread[writers];
        for (int i = 0; i < writers; i++) {
            final int writerId = i + 1;
            writerThreads[i] = new Thread(() -> {
                for (int j = 1; j <= writesPerWriter; j++) {
                    try {
                        Thread.sleep(220 + random.nextInt(220));
                        board.write("Writer-" + writerId, "Value W" + writerId + "-" + j);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            });
            writerThreads[i].start();
        }

        for (Thread reader : readerThreads) {
            reader.join();
        }
        for (Thread writer : writerThreads) {
            writer.join();
        }

        System.out.println("Readers/Writers simulation complete.");
    }
}
