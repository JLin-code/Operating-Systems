/*
https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html
https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Runnable.html
*/
public class MultithreadEx
 {
	public static void main(String[] args)
	{
		int n = 8; 
		for (int i=0;i<n;i++) 
		{
			MultiThread obj=new MultiThread();
			obj.start();
		}
		/*
		System.out.println("-------");
		for (int i=0;i<n;i++) 
		{
			MultiThread2 obj=new MultiThread2();
			Thread t=new Thread(obj);
			t.start();
		}
		
		
		System.out.println("-------");

		MyTask task=new MyTask();
        Thread thread=new Thread(task);
        thread.start();
*/
        System.out.println("Main thread continues...");
		
	}
}
/*
 Java code for thread creation by extending the Thread class
*/
class MultiThread extends Thread
{
	public void run()
	{
		try 
		{
			System.out.println("Thread "+Thread.currentThread().getId()+" is running");
		}
		catch(Exception e)
		{
			System.out.println("Exception is caught");
			System.out.println(e.getMessage());
		}
	}
}
/*
 Java code for thread creation by implementing the Runnable Interface
*/
class MultiThread2 implements Runnable 
{
    public void run()
    {
        try 
		{
			System.out.println("Thread "+Thread.currentThread().getId()+" is running");
		}
		catch(Exception e)
		{
			System.out.println("Exception is caught");
			System.out.println(e.getMessage());
		}
    }
}
class MyTask implements Runnable 
{
    public void run()
	{
        for(int i=0;i<5;i++)
		{
            System.out.println("Task running: " + i);
            try
			{
                Thread.sleep(1000); // Sleep for 1 second
            }
			catch(InterruptedException e)
			{
                e.printStackTrace();
            }
        }
    }
}
