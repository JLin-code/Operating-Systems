import java.util.concurrent.Semaphore;
import java.util.LinkedList;
public class Semaphore3
{
	//uses a linked list as our buffer area
    static LinkedList<Integer> buffer=new LinkedList<Integer>();
	//limits the size of the buffer
    static final int CAPACITY=5;

	//creates a counting semaphore to be able to give out CAPACITY/5 keys
    static Semaphore empty=new Semaphore(CAPACITY);
	//semaphore to represent a partially full/full buffer
	//starts as 0 for doesn't have anything
    static Semaphore full=new Semaphore(0);
	//Binary semaphore to represent a lock/mutex
    static Semaphore mutex=new Semaphore(1);

    public static void main(String[] args)
	{
        Runnable producer=() -> {
            for(int i=0;i<10;i++){
                try {
                    empty.acquire();//get a key/space in the buffer
                    mutex.acquire();//lock the shared data area
                    buffer.add(i);//put the value to the buffer
                    System.out.println("Produced: " + i);
                } catch (InterruptedException e) {}
                finally {
                    mutex.release();//give back the key for buffer space
                    full.release();//buffer has something
                }
            }
        };
        Runnable consumer= () -> {
            for(int i=0;i<10;i++){
                try {
                    full.acquire();//take key if buffer has something
                    mutex.acquire();//lock the shared data
                    int item = buffer.removeFirst();//consume the value
                    System.out.println("Consumed: " + item);
                } catch (InterruptedException e) {}
                finally {
                    mutex.release();//unlock data
                    empty.release();//buffer has a free space
                }
            }
        };

        new Thread(producer).start();
		new Thread(consumer).start();
    }
}