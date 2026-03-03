import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.IOException;

public class CharacterPipeExample2
{
    public static void main(String[] args)
	{
        try
		{
            PipedWriter writer=new PipedWriter();
            PipedReader reader=new PipedReader(writer);
            
            // Writer thread
            Thread writerThread=new Thread(()-> 
					{
						try
						{
							writer.write("Message from writer thread".toCharArray());
							writer.flush();
							writer.close();
						}catch(IOException e)
						{
							e.printStackTrace();
						}
					});
            
            // Reader thread
            Thread readerThread=new Thread(()->
					{
						try
						{
							char[] buffer=new char[100];
							int bytesRead=reader.read(buffer);
							System.out.println("Reader thread received: " + 
								new String(buffer,0,bytesRead));
							reader.close();
						}catch(IOException e) 
						{
							e.printStackTrace();
						}
					});
            
            writerThread.start();
            readerThread.start();
            
            writerThread.join();
            readerThread.join();
            
        } catch (IOException | InterruptedException e) 
		{
            e.printStackTrace();
        }
    }
}
