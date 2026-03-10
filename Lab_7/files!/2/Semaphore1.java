import java.util.concurrent.Semaphore;
//https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Semaphore.html
public class Semaphore1
{
    static int counter=0;//shared memory -> possible race condition
    static Semaphore s=new Semaphore(1);//create a binary semaphore
    public static void main(String[] args) throws InterruptedException
	{
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    s.acquire();//enter critical section -> take a key
                    counter++;//perform update
                } catch (InterruptedException e) {}
                finally {
                    s.release();//leave critical section -> return key
                }
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();
        t1.join();//join back to main thread before final print happens
        t2.join();

        System.out.println("Final counter: " + counter);
    }
}