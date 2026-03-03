import java.io.*;
public class BytePipeExample
{
    public static void main(String[] args) throws IOException
	{
		/*
			Create a producer that prints some data
			Create a consumer that reads the data
		*/
        PipedOutputStream pout=new PipedOutputStream();
        PipedInputStream pin=new PipedInputStream(pout);

		/*() -> {} overrides the run() method in thread*/
        Thread producer=new Thread(()->
					{
						try(PrintWriter pw=new PrintWriter(pout))
						{
							for(int i=0;i<=15;i++) 
							{
								pw.println("Line " + i + " from producer");
								pw.flush();
								Thread.sleep(200);//to put some delay between lines
							}
						} catch(Exception e)
						{
							e.printStackTrace();
						}
					});
		/*Thread producer2=new Thread(()->
					{
						try(PrintWriter pw=new PrintWriter(pout))
						{
							for(int i=0;i<=15;i++) 
							{
								pw.println("Line " + i + " from producer 2");
								pw.flush();
								Thread.sleep(200);//to put some delay between lines
							}
						} catch(Exception e)
						{
							e.printStackTrace();
						}
					});
		*/			
        Thread consumer=new Thread(()->
					{
						try(BufferedReader br=new BufferedReader(new InputStreamReader(pin))) 
						{
							String line;
							while((line=br.readLine())!=null)
								System.out.println("Consumer read: "+line);
							System.out.println("Consumer: end of stream");
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					});
		/*Thread consumer2=new Thread(()->
					{
						try(BufferedReader br=new BufferedReader(new InputStreamReader(pin))) 
						{
							String line;
							while((line=br.readLine())!=null)
								System.out.println("Consumer2 read: "+line);
							System.out.println("Consumer2: end of stream");
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					});		
		*/					
        consumer.start();
		//consumer2.start();
        producer.start();
		//producer2.start();
    }
}