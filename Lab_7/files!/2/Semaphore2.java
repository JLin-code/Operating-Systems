import java.util.concurrent.Semaphore;
public class Semaphore2
{
	//constant for how many keys/rooms are available
    static final int KEYS=3;
	//counting semaphore to controll giving out the keys
    static Semaphore keysDesk=new Semaphore(KEYS);

    static class Person implements Runnable
	{
        private final String name;
        Person(String name)
		{ 
			this.name=name;
		}
        @Override
        public void run()
		{
            try 
			{
                System.out.println(name+" wants a room key.");
                keysDesk.acquire(); //give out a key
                System.out.println(name+" got a key and entered a room.");
                Thread.sleep(2000); //simulate time in room
                System.out.println(name + " is leaving room and returning key.");
                keysDesk.release(); //return key to desk
            } catch (InterruptedException e)
			{
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args)
	{
        String[] people = {"Alice", "Bob", "Charlie", "David", "Eve", "Faye", "Greg"};
        for(String name:people) 
		{
            new Thread(new Person(name)).start();
        }
    }
}