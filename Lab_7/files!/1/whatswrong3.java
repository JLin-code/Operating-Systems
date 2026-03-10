class Counter 
{
    int count = 0;
    public static void main(String[] args) throws Exception
	{
		whatswrong3.main(args);
	}
    void increment() 
	{
		synchronized(this) 
		{
            count++;
        }
    }
	int getCount()
	{
        return count;
    }
}
public class whatswrong3
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