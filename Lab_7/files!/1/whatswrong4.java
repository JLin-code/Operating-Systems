import java.util.concurrent.locks.ReentrantLock;
class Counter
{
    int count = 0;
    ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args) throws Exception
    {
        whatswrong4.main(args);
    }
    void increment() 
	{
        lock.lock();
        try
        {
            count++;
        }
        finally
        {
            lock.unlock();
        }
    }
	int getCount()
	{
        return count;
    }
}
public class whatswrong4
{
    public static void main(String[] args) throws Exception 
	{
        Counter counter=new Counter();
        Thread t1 = new Thread(() -> {
            for(int i = 0; i < 100000; i++)
                counter.increment();
        });

        Thread t2 = new Thread(() -> {
            for(int i = 0; i < 100000; i++)
                counter.increment();
        });

        t1.start();
        t2.start();
		t1.join();
		t2.join();
        System.out.println("Count: " + counter.getCount());
    }
}