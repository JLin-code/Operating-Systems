import java.util.concurrent.atomic.AtomicInteger;

class Adder
{
    static AtomicInteger sum=new AtomicInteger(0);
    static void add(int x)
	{
        sum.addAndGet(x);
    }
}
public class Problem3
{
    public static void main(String[] args) throws InterruptedException
	{
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                Adder.add(5);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                Adder.add(5);
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final sum: " + Adder.sum.get());
    }
}